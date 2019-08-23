package org.jfantasy.framework.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.apache.ibatis.type.Alias;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.web.RedirectAttributesWriter;
import org.springframework.data.domain.Sort;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 通用分页对象
 *
 * @param <T>
 * @author limaofeng
 */
@Alias("Pager")
@JsonIgnoreProperties(value = {"orders", "first", "order_by_setted"})
public class Pager<T> implements Serializable {

    private static final long serialVersionUID = -2343309063338998483L;

    /**
     * 排序 - 升序
     */
    public static final String SORT_ASC = "asc";
    /**
     * 排序 - 降序
     */
    public static final String SORT_DESC = "desc";
    /**
     * 最大数据条数
     */
    @Builder.Default
    @JsonProperty("count")
    private int totalCount = 0;
    /**
     * 每页显示的数据条数
     */
    @Builder.Default
    @JsonProperty("per_page")
    private int pageSize = 15;
    /**
     * 总页数
     */
    @JsonProperty("total")
    @Builder.Default
    private int totalPage = 1;
    /**
     * 当前页码
     */
    @Builder.Default
    @JsonProperty("page")
    private int currentPage = 1;
    /**
     * 开始数据索引
     */
    @Builder.Default
    private int first = 0;
    /**
     * 排序字段
     */
    @JsonProperty("sort")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private OrderBy orderBy;
    @JsonProperty("items")
    private transient List<T> pageItems;

    public Pager() {
        this(15);
    }

    public Pager(int pageSize) {
        this.pageSize = pageSize;
    }

    public Pager(Pager pager) {
        this.currentPage = pager.currentPage;
        this.pageSize = pager.pageSize;
        this.totalCount = pager.totalCount;
        this.totalPage = pager.totalPage;
        this.orderBy = pager.orderBy;
    }

    public Pager(Pager pager, List<T> items) {
        this.currentPage = pager.currentPage;
        this.pageSize = pager.pageSize;
        this.totalCount = pager.totalCount;
        this.totalPage = pager.totalPage;
        this.orderBy = pager.orderBy;
        this.pageItems = items;
    }

    public Pager(int currentPage, int pageSize, OrderBy orderBy) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.orderBy = orderBy;
    }

    /**
     * 获取总页码
     *
     * @return 总页数
     */
    public int getTotalPage() {
        return totalPage;
    }


    /**
     * 获取每页显示的条数
     *
     * @return 每页显示条数
     */
    public int getPageSize() {
        return pageSize;
    }

    public long getOffset() {
        return this.getFirst();
    }

    @JsonIgnore
    public Sort getSort() {
        if (!this.isOrderBySetted()) {
            return Sort.unsorted();
        }
        if (getOrderBy().isMulti()) {
            return Sort.by(getOrderBy().getOrders().stream().map(item -> new Sort.Order(Sort.Direction.valueOf(item.getDirection().name()), item.getProperty())).collect(Collectors.toList()));
        }
        return Sort.by(new Sort.Order(Sort.Direction.valueOf(getOrderBy().getDirection().name()), getOrderBy().getProperty()));
    }

    /**
     * 设置显示的页码 注意是页码
     *
     * @param currentPage 当前页码
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * 返回翻页开始位置
     *
     * @param first 数据开始位置
     */
    public void setFirst(int first) {
        this.first = first;
    }

    public int getFirst() {
        return first;
    }

    /**
     * 设置每页显示数据的条数
     *
     * @param pageSize 每页显示数据条数
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 获取当前显示的页码
     *
     * @return currentPage
     */
    public int getCurrentPage() {
        return currentPage <= 0 ? 1 : currentPage;
    }

    /**
     * 获取数据总条数
     *
     * @return totalCount
     */
    public int getTotalCount() {
        return totalCount;
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
        return "Pager [totalCount=" + totalCount + ", first=" + first + ", pageSize=" + pageSize + ", totalPage=" + totalPage + ", currentPage=" + currentPage + ", orderBy=" + orderBy + "]";
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
            setFirst((totalPage - 1) * pageSize);
        } else if (currentPage <= 0) {
            setCurrentPage(1);
            setFirst(first);
        } else {
            setFirst((currentPage - 1) * pageSize);
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
        if (this.getFirst() != 0) {
            attrs.addAttribute("limit", this.getFirst() + "," + this.getPageSize());
        } else if (this.getPageSize() != 15) {
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