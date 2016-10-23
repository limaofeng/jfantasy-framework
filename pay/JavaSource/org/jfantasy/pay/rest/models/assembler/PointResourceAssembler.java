package org.jfantasy.pay.rest.models.assembler;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.pay.bean.Point;
import org.jfantasy.pay.rest.PointController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

public class PointResourceAssembler extends ResourceAssemblerSupport<Point, ResultResourceSupport> {

    public PointResourceAssembler() {
        super(PointController.class, ResultResourceSupport.class);
    }

    @Override
    protected ResultResourceSupport<Point> instantiateResource(Point entity) {
        return new ResultResourceSupport<>(entity);
    }

    @Override
    public ResultResourceSupport toResource(Point entity) {
        return instantiateResource(entity);
    }

    public Pager<ResultResourceSupport> toResources(Pager<Point> pager) {
        return new Pager<>(pager,this.toResources(pager.getPageItems()));
    }

}
