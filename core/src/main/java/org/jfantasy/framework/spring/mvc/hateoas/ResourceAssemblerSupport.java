package org.jfantasy.framework.spring.mvc.hateoas;

import java.util.ArrayList;
import java.util.List;
import org.jfantasy.framework.dao.Pagination;

public abstract class ResourceAssemblerSupport<T, R extends ResultResourceSupport> {

  protected ResultResourceSupport<T> instantiateResource(T entity) {
    return new ResultResourceSupport<>(entity);
  }

  protected ResultResourceSupport createResourceWithId(T entity) {
    return instantiateResource(entity);
  }

  public abstract ResultResourceSupport toResource(T entity);

  public List<ResultResourceSupport> toResources(List<T> items) {
    List<ResultResourceSupport> supports = new ArrayList<>(items.size());
    for (T item : items) {
      supports.add(toResource(item));
    }
    return supports;
  }

  public Pagination<ResultResourceSupport> toResources(Pagination<T> pager) {
    return new Pagination<>(pager, this.toResources(pager.getPageItems()));
  }
}
