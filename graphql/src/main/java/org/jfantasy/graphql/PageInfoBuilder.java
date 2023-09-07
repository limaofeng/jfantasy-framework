package org.jfantasy.graphql;

public class PageInfoBuilder {
  private Boolean hasNextPage;
  private Boolean hasPreviousPage;
  private String startCursor;
  private String endCursor;
  private int current;
  private int pageSize;
  private long total;
  private int totalPages;

  PageInfoBuilder() {}

  public PageInfoBuilder hasNextPage(Boolean hasNextPage) {
    this.hasNextPage = hasNextPage;
    return this;
  }

  public PageInfoBuilder hasPreviousPage(Boolean hasPreviousPage) {
    this.hasPreviousPage = hasPreviousPage;
    return this;
  }

  public PageInfoBuilder startCursor(String startCursor) {
    this.startCursor = startCursor;
    return this;
  }

  public PageInfoBuilder endCursor(String endCursor) {
    this.endCursor = endCursor;
    return this;
  }

  public PageInfoBuilder current(int current) {
    this.current = current;
    return this;
  }

  public PageInfoBuilder pageSize(int pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  public PageInfoBuilder total(long total) {
    this.total = total;
    return this;
  }

  public PageInfoBuilder totalPages(int totalPages) {
    this.totalPages = totalPages;
    return this;
  }

  public PageInfo build() {
    return new PageInfo(
        this.hasNextPage,
        this.hasPreviousPage,
        this.startCursor,
        this.endCursor,
        this.current,
        this.pageSize,
        this.total,
        this.totalPages);
  }

  public String toString() {
    return "PageInfo.PageInfoBuilder(hasNextPage="
        + this.hasNextPage
        + ", hasPreviousPage="
        + this.hasPreviousPage
        + ", startCursor="
        + this.startCursor
        + ", endCursor="
        + this.endCursor
        + ", current="
        + this.current
        + ", pageSize="
        + this.pageSize
        + ", total="
        + this.total
        + ", totalPages="
        + this.totalPages
        + ")";
  }
}
