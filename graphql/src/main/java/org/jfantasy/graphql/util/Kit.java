package org.jfantasy.graphql.util;

import java.util.function.Function;
import java.util.stream.Collectors;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.regexp.RegexpUtil;
import org.jfantasy.graphql.Connection;
import org.jfantasy.graphql.Edge;
import org.jfantasy.graphql.PageInfo;
import org.springframework.data.domain.Page;

/**
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-02 17:30
 */
public class Kit {

  public static String typeName(Object input) {
    if (input == null) {
      return "null";
    }
    return input.getClass().getSimpleName();
  }

  public static <C extends Connection, T, R extends Edge> C connection(
      Page<T> page, Class<C> connectionClass, Function<? super T, ? extends R> mapper) {
    Connection connection = ClassUtil.newInstance(connectionClass);
    assert connection != null;
    connection.setPageInfo(
        PageInfo.builder()
            .total(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .current(page.getNumber())
            .pageSize(page.getSize())
            .hasPreviousPage(page.hasPrevious())
            .hasNextPage(page.hasNext())
            .build());
    if (mapper instanceof EdgeConverter && ((EdgeConverter) mapper).edgeClass == null) {
      Class edgeClass =
          ClassUtil.forName(
              RegexpUtil.parseGroup(
                  connectionClass.getGenericSuperclass().getTypeName(), "<([^>]+)>", 1));
      ((EdgeConverter<? super T, ? extends R>) mapper).setEdgeClass(edgeClass);
    }
    connection.setEdges(page.getContent().stream().map(mapper).collect(Collectors.toList()));
    return (C) connection;
  }

  public static <C extends Connection, T> C connection(Page<T> page, Class<C> connectionClass) {
    Class edgeClass =
        ClassUtil.forName(
            RegexpUtil.parseGroup(
                connectionClass.getGenericSuperclass().getTypeName(), "<([^>]+)>", 1));
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
      return (R) edge;
    }
  }
}
