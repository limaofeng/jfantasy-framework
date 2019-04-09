package org.jfantasy.graphql.util;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.regexp.RegexpUtil;
import org.jfantasy.graphql.Connection;
import org.jfantasy.graphql.Edge;
import org.jfantasy.graphql.PageInfo;
import org.jfantasy.graphql.Pagination;

import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019-04-02 17:30
 */
public class Kit {

    public static String typeName(Object input) {
        if (input == null) {
            return "null";
        }
        return input.getClass().getSimpleName();
    }

    public static <C extends Connection, T, R extends Edge> C connection(Pager<T> pager, Class<C> connectionClass, Function<? super T, ? extends R> mapper){
        Connection connection = ClassUtil.newInstance(connectionClass);
        connection.setPageInfo(PageInfo.builder().hasNextPage(pager.getCurrentPage() < pager.getTotalPage()).build());
        connection.setEdges(pager.getPageItems().stream().map(mapper).collect(Collectors.toList()));
        if(connection instanceof Pagination){
            Pagination pagination = (Pagination)connection;
            pagination.setCurrentPage(pager.getCurrentPage());
            pagination.setPageSize(pager.getPageSize());
            pagination.setTotalCount(pager.getTotalCount());
            pagination.setTotalPage(pager.getTotalPage());
        }
        return (C) connection;
    }

    public static <C extends Connection, T> C connection(Pager<T> pager, Class<C> connectionClass){
        Class edgeClass = ClassUtil.forName(RegexpUtil.parseGroup(connectionClass.getGenericInterfaces()[0].getTypeName(), "<([^>]+)>", 1));
        return (C) connection(pager, connectionClass, value -> {
            Edge edge = (Edge) ClassUtil.newInstance(edgeClass);
            edge.setNode(value);
            return edge;
        });
    }

}