package org.jfantasy.framework.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.ibatis.type.Alias;
import org.hibernate.criterion.Order;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.web.RedirectAttributesWriter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * 通用分页对象
 *
 * @param <T>
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
    @JsonProperty("count")
    private int totalCount = 0;
    /**
     * 每页显示的数据条数
     */
    @JsonProperty("per_page")
    private int pageSize = 0;
    /**
     * 总页数
     */
    @JsonProperty("total")
    private int totalPage = 1;
    /**
     * 当前页码
     */
    @JsonProperty("page")
    private int currentPage = 1;
    /**
     * 开始数据索引
     */
    private int first = 0;
    /**
     * 排序字段
     */
    @JsonProperty("sort")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String orderBy;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String order;
    @JsonProperty("items")
    private transient List<T> pageItems;

    public Pager() {
        this.pageSize = 10;
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
        this.order = pager.order;
    }

    public Pager(Pager pager, List<T> items) {
        this.currentPage = pager.currentPage;
        this.pageSize = pager.pageSize;
        this.totalCount = pager.totalCount;
        this.totalPage = pager.totalPage;
        this.orderBy = pager.orderBy;
        this.order = pager.order;
        this.pageItems = items;
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
        List<Sort.Order> orders = new ArrayList<>();
        String[] _orderBys = StringUtil.tokenizeToStringArray(getOrderBy());
        String[] _orders = StringUtil.tokenizeToStringArray(getOrder());
        for (int i = 0; i < _orders.length; i++) {
            orders.add(new Sort.Order(Pager.SORT_ASC.equals(_orders[i]) ? Sort.Direction.ASC : Sort.Direction.DESC, _orderBys[i]));
        }
        return Sort.by(orders);
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

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrder() {
        if (StringUtil.isNotBlank(this.getOrderBy()) && StringUtil.isBlank(this.order)) {
            this.setOrder("asc");
        }
        return this.order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    /**
     * 是否启用排序
     *
     * @return boolean
     */
    public boolean isOrderBySetted() {
        return StringUtil.isNotBlank(this.getOrderBy()) && StringUtil.isNotBlank(this.getOrder());
    }

    @Override
    public String toString() {
        return "Pager [totalCount=" + totalCount + ", first=" + first + ", pageSize=" + pageSize + ", totalPage=" + totalPage + ", currentPage=" + currentPage + ", orderBy=" + orderBy + ", order=" + order + "]";
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

    public void sort(String orderBy, String order) {
        this.orderBy = orderBy;
        this.order = order;
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
            attrs.addAttribute("sort", this.getOrderBy());
            attrs.addAttribute("order", this.getOrder());
        }
        return RedirectAttributesWriter.writer(attrs);
    }


}