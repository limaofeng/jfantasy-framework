package org.jfantasy.graphql.util;

import jakarta.persistence.Id;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.reflect.Property;
import org.jfantasy.graphql.Connection;
import org.jfantasy.graphql.Edge;
import org.jfantasy.graphql.PageInfo;
import org.jfantasy.graphql.PageInfoBuilder;
import org.springframework.data.domain.Page;

/**
 * 将分页对象转为 connection
 *
 * @author limaofeng
 * @version V1.0
 */
public class Kit {

  private static final Map<Class<?>, Property> CACHE = new ConcurrentHashMap<>();

  public static String typeName(Object input) {
    if (input == null) {
      return "null";
    }
    return input.getClass().getSimpleName();
  }

  @SneakyThrows({InstantiationException.class, IllegalAccessException.class})
  public static <C extends Connection<R, S>, S, T, R extends Edge<S>> C connection(
      Page<T> page, Class<C> connectionClass, Function<T, R> mapper) {
    C connection = connectionClass.newInstance();

    List<T> nodes = page.getContent();

    PageInfoBuilder pageInfoBuilder =
        PageInfo.builder()
            .total(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .current(page.getNumber() + 1)
            .pageSize(page.getSize())
            .hasPreviousPage(page.hasPrevious())
            .hasNextPage(page.hasNext());

    buildConnection(connection, nodes, pageInfoBuilder, mapper, connectionClass);

    // 临时的兼容，后期会删除
    connection.setTotalCount((int) page.getTotalElements());
    connection.setTotalPage(page.getTotalPages());
    connection.setCurrentPage(page.getNumber() + 1);
    connection.setPageSize(page.getSize());

    return connection;
  }

  public static <C extends Connection<R, T>, T, R extends Edge<T>> C connection(
      Page<T> page, Class<C> connectionClass) {
    Class<?> edgeClass =
        ClassUtil.forName(
            ((ParameterizedType) connectionClass.getGenericSuperclass())
                .getActualTypeArguments()[0].getTypeName());
    return connection(page, connectionClass, new EdgeConverter<>(edgeClass));
  }

  public static <C extends Connection<R, S>, S, T, R extends Edge<S>> C connection(
      org.jfantasy.framework.dao.Page<T> page, Class<C> connectionClass) {
    Class<?> edgeClass =
        ClassUtil.forName(
            ((ParameterizedType) connectionClass.getGenericSuperclass())
                .getActualTypeArguments()[0].getTypeName());
    return connection(page, connectionClass, new EdgeConverter<>(edgeClass));
  }

  @SneakyThrows({InstantiationException.class, IllegalAccessException.class})
  public static <C extends Connection<R, S>, S, T, R extends Edge<S>> C connection(
      org.jfantasy.framework.dao.Page<T> page, Class<C> connectionClass, Function<T, R> mapper) {
    @SuppressWarnings("deprecation")
    C connection = connectionClass.newInstance();

    List<T> nodes = page.getPageItems();

    PageInfoBuilder pageInfoBuilder =
        PageInfo.builder()
            .total(page.getTotalCount())
            .totalPages(page.getTotalPage())
            .current(page.getCurrentPage())
            .pageSize(page.getPageSize())
            .hasPreviousPage(page.getCurrentPage() > 1)
            .hasNextPage(page.getCurrentPage() < page.getTotalPage());

    buildConnection(connection, nodes, pageInfoBuilder, mapper, connectionClass);

    // 临时的兼容，后期会删除
    connection.setTotalCount(page.getTotalCount());
    connection.setTotalPage(page.getTotalPage());
    connection.setCurrentPage(page.getCurrentPage());
    connection.setPageSize(page.getPageSize());

    return connection;
  }

  private static <C extends Connection<R, S>, S, T, R extends Edge<S>> void buildConnection(
      C connection,
      List<T> nodes,
      PageInfoBuilder pageInfoBuilder,
      Function<T, R> mapper,
      Class<C> connectionClass) {
    if (mapper instanceof EdgeConverter && ((EdgeConverter<?, ?>) mapper).edgeClass == null) {
      Class<?> edgeClass =
          ClassUtil.forName(
              ((ParameterizedType) connectionClass.getGenericSuperclass())
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
  }

  public static class EdgeConverter<T, R> implements Function<T, R> {
    private Class<?> edgeClass;
    private Function<T, R> mapper;

    public EdgeConverter(Class<?> edgeClass) {
      this.edgeClass = edgeClass;
    }

    public EdgeConverter(Function<T, R> mapper) {
      this.mapper = mapper;
    }

    public void setEdgeClass(Class<?> edgeClass) {
      this.edgeClass = edgeClass;
    }

    @Override
    @SneakyThrows(ReflectiveOperationException.class)
    public R apply(T value) {
      Edge<T> edge = (Edge<T>) edgeClass.newInstance();
      if (mapper != null) {
        edge.setNode((T) mapper.apply(value));
      } else {
        edge.setNode(value);
      }
      edge.setCursor(getCursor(value));
      return (R) edge;
    }

    public static Property getIdProperty(Class<?> clazz) {
      if (CACHE.containsKey(clazz)) {
        return CACHE.get(clazz);
      }
      Property[] properties = ClassUtil.getProperties(clazz);
      List<Property> propertyList =
          Arrays.stream(properties).filter(item -> item.getAnnotation(Id.class) != null).toList();
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
      CACHE.put(clazz, property);
      return property;
    }

    public static <T> String getCursor(T value) {
      Class<?> clazz = ClassUtil.getRealClass(value);
      Property idProperty = getIdProperty(clazz);
      if (idProperty != null) {
        Object id = idProperty.getValue(value);
        return id != null ? id.toString() : null;
      }
      return null;
    }
  }
}
