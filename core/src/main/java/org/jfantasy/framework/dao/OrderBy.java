package org.jfantasy.framework.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.data.domain.Sort;

/**
 * 排序对象 <br>
 * 只在 Mybatis 中使用
 *
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-03 14:01
 */
@Data
public class OrderBy {
  private String property;
  private Direction direction;
  private static final OrderBy UNSORTED = OrderBy.by();
  private final List<OrderBy> orders;
  private Sort.NullHandling nullHandling = Sort.NullHandling.NATIVE;

  public OrderBy(List<OrderBy> orders) {
    this.orders = Collections.unmodifiableList(orders);
  }

  public OrderBy(String property, Direction direction) {
    this.property = property;
    this.direction = direction;
    this.orders = Collections.emptyList();
  }

  public OrderBy(String property, Direction direction, Sort.NullHandling nullHandling) {
    this.property = property;
    this.direction = direction;
    this.orders = Collections.emptyList();
    this.nullHandling = nullHandling;
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

  public static OrderBy newOrderBy(
      String property, Direction direction, Sort.NullHandling nullHandling) {
    return new OrderBy(property, direction, nullHandling);
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

  public static OrderBy sort(Sort sort) {
    if (sort.isSorted()) {
      return OrderBy.unsorted();
    }
    List<OrderBy> orderByList = new ArrayList<>();
    sort.forEach(
        item ->
            orderByList.add(
                OrderBy.newOrderBy(
                    item.getProperty(),
                    item.getDirection() == Sort.Direction.DESC ? Direction.DESC : Direction.ASC)));
    return OrderBy.by(orderByList);
  }

  public Sort toSort() {
    boolean isEmpty = direction == null && this.orders.isEmpty();
    if (this == UNSORTED || isEmpty) {
      return Sort.unsorted();
    }
    if (this.isMulti()) {
      return Sort.by(
          this.getOrders().stream()
              .map(
                  item ->
                      new Sort.Order(
                          Sort.Direction.valueOf(item.getDirection().name()),
                          item.getProperty(),
                          item.getNullHandling()))
              .collect(Collectors.toList()));
    }
    return Sort.by(
        new Sort.Order(
            Sort.Direction.valueOf(this.getDirection().name()),
            this.getProperty(),
            this.getNullHandling()));
  }

  @Override
  public String toString() {
    if (this.orders.isEmpty()) {
      return property + "_" + this.direction.name();
    }
    return this.orders.stream().map(OrderBy::toString).collect(Collectors.joining(","));
  }

  public boolean isMulti() {
    return !this.orders.isEmpty();
  }

  public static enum Direction {
    /** 升序 */
    ASC,
    /** 降序 */
    DESC;

    public boolean isAscending() {
      return this.equals(ASC);
    }

    public boolean isDescending() {
      return this.equals(DESC);
    }
  }
}
