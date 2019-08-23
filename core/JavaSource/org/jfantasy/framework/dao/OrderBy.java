package org.jfantasy.framework.dao;

import lombok.Data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019-04-03 14:01
 */
@Data
public class OrderBy {
    private String property;
    private Direction direction;
    private static final OrderBy UNSORTED = OrderBy.by(new OrderBy[0]);
    private final List<OrderBy> orders;

    public OrderBy(List<OrderBy> orders) {
        this.orders = Collections.unmodifiableList(orders);
    }

    public OrderBy(String property, Direction direction) {
        this.property = property;
        this.direction = direction;
        this.orders = Collections.emptyList();
    }

    public static OrderBy asc(String property) {
        return new OrderBy(property, Direction.ASC);
    }

    public static OrderBy desc(String property) {
        return new OrderBy(property, Direction.DESC);
    }

    public static OrderBy newOrderBy(String property, Direction direction) {
        return new OrderBy(property, direction);
    }

    public static OrderBy unsorted() {
        return UNSORTED;
    }

    public static OrderBy by(OrderBy... orders) {
        return new OrderBy(Arrays.asList(orders));
    }

    public static OrderBy by(List<OrderBy> orders) {
        return orders.isEmpty() ? OrderBy.unsorted() : new OrderBy(orders);
    }

    @Override
    public String toString() {
        if (this.orders.isEmpty()) {
            return property + "_" + this.direction.name();
        }
        return this.orders.stream().map(item -> item.toString()).collect(Collectors.joining(","));
    }

    public boolean isMulti() {
        return !this.orders.isEmpty();
    }

    public static enum Direction {
        /**
         * 升序
         */
        ASC,
        /**
         * 降序
         */
        DESC;

        public boolean isAscending() {
            return this.equals(ASC);
        }

        public boolean isDescending() {
            return this.equals(DESC);
        }
    }
}