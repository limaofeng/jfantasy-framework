package net.asany.jfantasy.framework.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import net.asany.jfantasy.framework.util.web.RedirectAttributesWriter;
import org.apache.ibatis.type.Alias;
import org.springframework.data.domain.Sort;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 通用分页对象
 *
 * @param <T>
 * @author limaofeng
 */
@AllArgsConstructor
@Alias("Page")
@JsonIgnoreProperties(value = {"orders", "first", "order_by_setted"})
public class Pagination<T> implements Page<T>, Serializable {

  public static final int DEFAULT_PAGE_SIZE = 15;

  /** 排序 - 升序 */
  public static final String SORT_ASC = "asc";

  /** 排序 - 降序 */
  public static final String SORT_DESC = "desc";

  /** 最大数据条数 */
  @JsonProperty("count")
  private int totalCount = 0;

  /** 每页显示的数据条数 */
  @JsonProperty("per_page")
  private int pageSize;

  /** 总页数 */
  @JsonProperty("total")
  private int totalPage = 1;

  /** 当前页码 */
  @JsonProperty("page")
  private int currentPage = 1;

  /** 开始数据索引 */
  private int offset = 0;

  /** 排序字段 */
  @JsonProperty("sort")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private OrderBy orderBy;

  @JsonProperty("items")
  private transient List<T> pageItems;

  public Pagination() {
    this(15);
  }

  public Pagination(int pageSize) {
    this.pageSize = pageSize;
  }

  public Pagination(Page<?> page) {
    this.currentPage = page.getCurrentPage();
    this.pageSize = page.getPageSize();
    this.totalCount = page.getTotalCount();
    this.totalPage = page.getTotalPage();
    this.orderBy = page.getOrderBy();
  }

  public Pagination(Page<?> page, List<T> items) {
    this.currentPage = page.getCurrentPage();
    this.pageSize = page.getPageSize();
    this.totalCount = page.getTotalCount();
    this.totalPage = page.getTotalPage();
    this.orderBy = page.getOrderBy();
    this.pageItems = items;
  }

  public Pagination(int currentPage, int pageSize, OrderBy orderBy) {
    this.currentPage = currentPage;
    this.pageSize = pageSize;
    this.orderBy = orderBy;
  }

  /**
   * 根据下标查询时使用
   *
   * @param orderBy 排序
   * @param offset 开始
   * @param size 长度
   */
  public Pagination(OrderBy orderBy, int offset, int size) {
    this.offset = offset;
    this.pageSize = size;
    this.orderBy = orderBy;
  }

  public static <T> Page<T> newPager() {
    return new Pagination<>();
  }

  public static <T> Page<T> newPager(OrderBy orderBy) {
    Pagination<T> page = new Pagination<>();
    page.setOrderBy(orderBy);
    return page;
  }

  public static <T> Page<T> newPager(int size) {
    return new Pagination<>(size);
  }

  public static <T> Page<T> newPager(int page, int size) {
    Page<T> pager = new Pagination<>(size);
    pager.setCurrentPage(page);
    return pager;
  }

  public static <T> Page<T> newPager(int page, int size, OrderBy orderBy) {
    return new Pagination<>(page, size, orderBy);
  }

  public static <T> Page<T> newPager(int size, OrderBy orderBy) {
    Pagination<T> page = new Pagination<>(size);
    page.setOrderBy(orderBy);
    return page;
  }

  public static <T> Page<T> newPager(int size, OrderBy orderBy, int offset) {
    Pagination<T> page = new Pagination<>(size);
    page.setOrderBy(orderBy);
    page.setOffset(offset);
    return page;
  }

  public static <T> Page<T> newPager(Page<T> page) {
    return new Pagination<>(page);
  }

  /**
   * 获取总页码
   *
   * @return 总页数
   */
  @Override
  public int getTotalPage() {
    return totalPage;
  }

  /**
   * 获取每页显示的条数
   *
   * @return 每页显示条数
   */
  @Override
  public int getPageSize() {
    return pageSize;
  }

  public int getOffset() {
    return this.offset;
  }

  @JsonIgnore
  public Sort getSort() {
    if (!this.isOrderBySetted()) {
      return Sort.unsorted();
    }
    return this.orderBy.toSort();
  }

  /**
   * 设置显示的页码 注意是页码
   *
   * @param currentPage 当前页码
   */
  @Override
  public void setCurrentPage(int currentPage) {
    this.currentPage = currentPage;
  }

  /**
   * 返回翻页开始位置
   *
   * @param offset 数据开始位置
   */
  public void setOffset(int offset) {
    this.offset = offset;
  }

  /**
   * 设置每页显示数据的条数
   *
   * @param pageSize 每页显示数据条数
   */
  @Override
  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  @Override
  public void setTotalPage(int totalPage) {
    this.totalPage = totalPage;
  }

  /**
   * 获取当前显示的页码
   *
   * @return currentPage
   */
  @Override
  public int getCurrentPage() {
    return currentPage <= 0 ? 1 : currentPage;
  }

  /**
   * 获取数据总条数
   *
   * @return totalCount
   */
  @Override
  public int getTotalCount() {
    return totalCount;
  }

  @Override
  public void setTotalCount(int totalCount) {
    this.totalCount = totalCount;
  }

  public List<T> getPageItems() {
    return pageItems;
  }

  public OrderBy getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(OrderBy orderBy) {
    this.orderBy = orderBy;
  }

  /**
   * 是否启用排序
   *
   * @return boolean
   */
  public boolean isOrderBySetted() {
    return this.orderBy != null;
  }

  @Override
  public String toString() {
    return "Pager [totalCount="
        + totalCount
        + ", offset="
        + offset
        + ", pageSize="
        + pageSize
        + ", totalPage="
        + totalPage
        + ", currentPage="
        + currentPage
        + ", orderBy="
        + orderBy
        + "]";
  }

  /**
   * 设置总数据条数
   *
   * @param totalCount 总数据条数
   */
  public void reset(int totalCount) {
    this.totalCount = totalCount;
    this.totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
    if (currentPage >= totalPage) {
      setCurrentPage(totalPage);
      setOffset((totalPage - 1) * pageSize);
    } else if (currentPage <= 0) {
      setCurrentPage(1);
      setOffset(offset);
    } else {
      setOffset((currentPage - 1) * pageSize);
    }
  }

  public void sort(String property, OrderBy.Direction direction) {
    this.orderBy = OrderBy.newOrderBy(property, direction);
  }

  public void reset(List<T> items) {
    this.pageItems = items;
  }

  public void reset(int totalCount, List<T> items) {
    this.reset(totalCount);
    this.reset(items);
  }

  public RedirectAttributesWriter writeTo(RedirectAttributes attrs) {
    if (this.getOffset() != 0) {
      attrs.addAttribute("limit", this.getOffset() + "," + this.getPageSize());
    } else if (this.getPageSize() != DEFAULT_PAGE_SIZE) {
      attrs.addAttribute("per_page", this.getPageSize());
    }
    if (this.getCurrentPage() != 1) {
      attrs.addAttribute("page", this.getCurrentPage());
    }
    if (this.isOrderBySetted()) {
      attrs.addAttribute("orderBy", this.getOrderBy());
    }
    return RedirectAttributesWriter.writer(attrs);
  }
}
