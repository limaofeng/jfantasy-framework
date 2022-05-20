package org.jfantasy.graphql.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.Id;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.reflect.Property;
import org.jfantasy.graphql.Connection;
import org.jfantasy.graphql.Edge;
import org.jfantasy.graphql.PageInfo;
import org.springframework.data.domain.Page;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

/**
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-02 17:30
 */
public class Kit {

  private static final Map<Class, Property> cache = new ConcurrentHashMap<>();

  public static String typeName(Object input) {
    if (input == null) {
      return "null";
    }
    return input.getClass().getSimpleName();
  }

  public static <C extends Connection, T, R extends Edge> C connection(
      Page<T> page, Class<C> connectionClass, Function<T, R> mapper) {
    C connection = ClassUtil.newInstance(connectionClass);
    assert connection != null;

    List<T> nodes = page.getContent();

    PageInfo.PageInfoBuilder pageInfoBuilder =
        PageInfo.builder()
            .total(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .current(page.getNumber() + 1)
            .pageSize(page.getSize())
            .hasPreviousPage(page.hasPrevious())
            .hasNextPage(page.hasNext());

    if (mapper instanceof EdgeConverter && ((EdgeConverter) mapper).edgeClass == null) {
      Class edgeClass =
          ClassUtil.forName(
              ((ParameterizedTypeImpl) connectionClass.getGenericSuperclass())
                  .getActualTypeArguments()[0].getTypeName());
      ((EdgeConverter<T, R>) mapper).setEdgeClass(edgeClass);
    }
    connection.setEdges(nodes.stream().map(mapper).collect(Collectors.toList()));

    if (!nodes.isEmpty()) {
      List<R> edges = connection.getEdges();
      pageInfoBuilder
          .startCursor(edges.get(0).getCursor())
          .endCursor(edges.get(nodes.size() - 1).getCursor());
    }

    connection.setPageInfo(pageInfoBuilder.build());

    // 临时的兼容，后期会删除
    connection.setTotalCount((int) page.getTotalElements());
    connection.setTotalPage(page.getTotalPages());
    connection.setCurrentPage(page.getNumber() + 1);
    connection.setPageSize(page.getSize());

    return connection;
  }

  public static <C extends Connection, T> C connection(Page<T> page, Class<C> connectionClass) {
    Class edgeClass =
        ClassUtil.forName(
            ((ParameterizedTypeImpl) connectionClass.getGenericSuperclass())
                .getActualTypeArguments()[0].getTypeName());
    return (C) connection(page, connectionClass, new EdgeConverter(edgeClass));
  }

  public static class EdgeConverter<T, R> implements Function<T, R> {
    private Class edgeClass;
    private Function mapper;

    public EdgeConverter(Class edgeClass) {
      this.edgeClass = edgeClass;
    }

    public EdgeConverter(Function<? super T, ? extends T> mapper) {
      this.mapper = mapper;
    }

    public void setEdgeClass(Class edgeClass) {
      this.edgeClass = edgeClass;
    }

    @Override
    public R apply(T value) {
      Edge edge = (Edge) ClassUtil.newInstance(edgeClass);
      assert edge != null;
      if (mapper != null) {
        edge.setNode(mapper.apply(value));
      } else {
        edge.setNode(value);
      }
      edge.setCursor(getCursor(value));
      return (R) edge;
    }

    public static Property getIdProperty(Class clazz) {
      if (cache.containsKey(clazz)) {
        return cache.get(clazz);
      }
      Property[] properties = ClassUtil.getProperties(clazz);
      List<Property> propertyList =
          Arrays.stream(properties)
              .filter(item -> item.getAnnotation(Id.class) != null)
              .collect(Collectors.toList());
      Property property;
      if (propertyList.isEmpty()) {
        property = ClassUtil.getProperty(clazz, "id");
      } else if (propertyList.size() == 1) {
        property = propertyList.get(0);
      } else {
        property = ClassUtil.getProperty(clazz, "id");
      }
      if (property == null) {
        return null;
      }
      cache.put(clazz, property);
      return property;
    }

    public static <T> String getCursor(T value) {
      Class clazz = ClassUtil.getRealClass(value);
      Property idProperty = getIdProperty(clazz);
      if (idProperty != null) {
        Object id = idProperty.getValue(value);
        return id != null ? id.toString() : null;
      }
      return null;
    }
  }
}
