package org.jfantasy.pay.rest.models.assembler;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.pay.bean.PayConfig;
import org.jfantasy.pay.rest.PayConfigController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.ArrayList;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


public class PayConfigResourceAssembler extends ResourceAssemblerSupport<PayConfig, ResultResourceSupport> {

    public PayConfigResourceAssembler() {
        super(PayConfigController.class, ResultResourceSupport.class);
    }

    @Override
    protected ResultResourceSupport instantiateResource(PayConfig entity) {
        return new ResultResourceSupport<>(entity);
    }

    @Override
    public ResultResourceSupport toResource(PayConfig entity) {
        ResultResourceSupport resource = createResourceWithId(entity.getId(), entity);
        resource.add(linkTo(methodOn(PayConfigController.class).payments(entity.getId().toString(), new RedirectAttributesModelMap(), new Pager<>(), new ArrayList<>())).withRel("payments"));
        resource.add(linkTo(methodOn(PayConfigController.class).refunds(entity.getId().toString(), new RedirectAttributesModelMap(), new Pager<>(), new ArrayList<>())).withRel("refunds"));
        return resource;
    }

    public Pager<ResultResourceSupport> toResources(Pager<PayConfig> pager) {
        return new Pager<>(pager, this.toResources(pager.getPageItems()));
    }
    
}
