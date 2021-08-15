package org.jfantasy.graphql.util;

import java.util.function.Function;
import java.util.stream.Collectors;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.Pagination;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.regexp.RegexpUtil;
import org.jfantasy.graphql.Connection;
import org.jfantasy.graphql.Edge;
import org.jfantasy.graphql.PageInfo;

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
      Pager<T> pager, Class<C> connectionClass, Function<? super T, ? extends R> mapper) {
    Connection connection = ClassUtil.newInstance(connectionClass);
    connection.setPageInfo(
        PageInfo.builder()
            .hasPreviousPage(pager.getCurrentPage() > 1)
            .hasNextPage(pager.getCurrentPage() < pager.getTotalPage())
            .build());
    if (mapper instanceof EdgeConverter && ((EdgeConverter) mapper).edgeClass == null) {
      Class edgeClass =
          ClassUtil.forName(
              RegexpUtil.parseGroup(
                  connectionClass.getGenericSuperclass().getTypeName(), "<([^>]+)>", 1));
      ((EdgeConverter<? super T, ? extends R>) mapper).setEdgeClass(edgeClass);
    }
    connection.setEdges(pager.getPageItems().stream().map(mapper).collect(Collectors.toList()));
    if (connection instanceof Pagination) {
      Pagination pagination = (Pagination) connection;
      pagination.setCurrentPage(pager.getCurrentPage());
      pagination.setPageSize(pager.getPageSize());
      pagination.setTotalCount(pager.getTotalCount());
      pagination.setTotalPage(pager.getTotalPage());
    }
    return (C) connection;
  }

  public static <C extends Connection, T> C connection(Pager<T> pager, Class<C> connectionClass) {
    Class edgeClass =
        ClassUtil.forName(
            RegexpUtil.parseGroup(
                connectionClass.getGenericSuperclass().getTypeName(), "<([^>]+)>", 1));
    return (C) connection(pager, connectionClass, new EdgeConverter(edgeClass));
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
      if (mapper != null) {
        edge.setNode(mapper.apply(value));
      } else {
        edge.setNode(value);
      }
      return (R) edge;
    }
  }
}
