package org.jfantasy.framework.spring.mvc.hateoas;

import org.jfantasy.framework.dao.Pager;

import java.util.ArrayList;
import java.util.List;

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

    public Pager<ResultResourceSupport> toResources(Pager<T> pager) {
        return new Pager<>(pager, this.toResources(pager.getPageItems()));
    }

}
