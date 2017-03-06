package org.jfantasy.pay.rest.models.assembler;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.order.bean.Order;
import org.jfantasy.order.rest.OrderController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class OrderResourceAssembler extends ResourceAssemblerSupport<Order, ResultResourceSupport> {

    public OrderResourceAssembler() {
        super(OrderController.class, ResultResourceSupport.class);
    }

    @Override
    protected ResultResourceSupport<Order> instantiateResource(Order entity) {
        return new ResultResourceSupport<>(entity);
    }

    @Override
    public ResultResourceSupport toResource(Order entity) {
        ResultResourceSupport resource = instantiateResource(entity);
        resource.add(linkTo(methodOn(OrderController.class).view(entity.getId(),null)).withSelfRel());
        resource.add(linkTo(methodOn(OrderController.class).payments(entity.getId())).withRel("payments"));
        resource.add(linkTo(methodOn(OrderController.class).refunds(entity.getId())).withRel("refunds"));
        resource.add(linkTo(methodOn(OrderController.class).items(entity.getId())).withRel("items"));
        return resource;
    }

    public Pager<ResultResourceSupport> toResources(Pager<Order> pager) {
        return new Pager<>(pager,this.toResources(pager.getPageItems()));
    }
}
