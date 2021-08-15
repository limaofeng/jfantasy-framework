package org.jfantasy.framework.dao;

import java.io.Serializable;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/** @Description @Author ChenWenJie @Data 2020/11/9 4:59 下午 */
public class LimitPageRequest implements Pageable, Serializable {
  private static final long serialVersionUID = -4541509938956089562L;

  private final int page;
  private final int size;
  private final Sort sort;

  public LimitPageRequest(int page, int size, Sort sort) {
    if (page < 0) {
      throw new IllegalArgumentException("Page index must not be less than zero!");
    }
    if (size < 1) {
      throw new IllegalArgumentException("Page size must not be less than one!");
    }
    this.page = page;
    this.size = size;
    Assert.notNull(sort, "Sort must not be null!");
    this.sort = sort;
  }

  /**
   * 创建一个新的{@link LimitPageRequest}
   *
   * @param page 从零开始的页面索引。
   * @param size 要返回的页面大小。
   * @return
   */
  public static LimitPageRequest of(int page, int size) {
    return of(page, size, Sort.unsorted());
  }

  /**
   * 创建一个新的{@link LimitPageRequest}并应用排序参数。
   *
   * @param page 从零开始的页面索引。
   * @param size 要返回的页面大小。
   * @param sort
   * @since 2.0
   */
  public static LimitPageRequest of(int page, int size, Sort sort) {
    return new LimitPageRequest(page, size, sort);
  }

  /**
   * 创建一个新的{@link LimitPageRequest}并应用排序方向和属性
   *
   * @param page 从零开始的页面索引，不能为负
   * @param size 要返回的页面大小，必须大于0。
   * @param direction must not be {@literal null}.
   * @param properties must not be {@literal null}.
   * @since 2.0
   */
  public static LimitPageRequest of(
      int page, int size, Sort.Direction direction, String... properties) {
    return of(page, size, Sort.by(direction, properties));
  }

  @Override
  public int getPageSize() {
    return size;
  }

  @Override
  public boolean isPaged() {
    return false;
  }

  @Override
  public boolean isUnpaged() {
    return false;
  }

  @Override
  public int getPageNumber() {
    return page;
  }

  @Override
  public long getOffset() {
    return this.page;
  }

  @Override
  public boolean hasPrevious() {
    return page > 0;
  }

  @Override
  public Optional<Pageable> toOptional() {
    return Optional.empty();
  }

  @Override
  public Pageable previousOrFirst() {
    return hasPrevious() ? previous() : first();
  }

  @Override
  public Sort getSort() {
    return sort;
  }

  @Override
  public Sort getSortOr(Sort sort) {
    return this.sort.and(sort);
  }

  @Override
  public Pageable next() {
    return new LimitPageRequest(getPageNumber() + 1, getPageSize(), getSort());
  }

  public LimitPageRequest previous() {
    return getPageNumber() == 0
        ? this
        : new LimitPageRequest(getPageNumber() - 1, getPageSize(), getSort());
  }

  @Override
  public Pageable first() {
    return new LimitPageRequest(0, getPageSize(), getSort());
  }

  @Override
  public Pageable withPage(int pageNumber) {
    return this;
  }

  @Override
  public boolean equals(@Nullable Object obj) {

    if (this == obj) {
      return true;
    }

    if (!(obj instanceof LimitPageRequest)) {
      return false;
    }

    LimitPageRequest that = (LimitPageRequest) obj;

    return super.equals(that) && this.sort.equals(that.sort);
  }

  @Override
  public int hashCode() {
    return 31 * super.hashCode() + sort.hashCode();
  }

  @Override
  public String toString() {
    return String.format(
        "Page request [number: %d, size %d, sort: %s]", getPageNumber(), getPageSize(), sort);
  }
}
