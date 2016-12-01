package org.jfantasy.pay.rest.models.assembler;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.card.bean.Card;
import org.jfantasy.card.bean.CardBatch;
import org.jfantasy.card.bean.CardType;
import org.jfantasy.card.rest.CardTypeController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import java.util.ArrayList;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class CardTypeResourceAssembler extends ResourceAssemblerSupport<CardType, ResultResourceSupport> {

    public CardTypeResourceAssembler() {
        super(CardTypeController.class, ResultResourceSupport.class);
    }

    @Override
    protected ResultResourceSupport<CardType> instantiateResource(CardType entity) {
        return new ResultResourceSupport<>(entity);
    }

    @Override
    public ResultResourceSupport toResource(CardType entity) {
        ResultResourceSupport resource = createResourceWithId(entity.getKey(), entity);
        resource.add(linkTo(methodOn(CardTypeController.class).batchs(entity.getKey(), new Pager<CardBatch>(), new ArrayList<PropertyFilter>())).withRel("batchs"));
        resource.add(linkTo(methodOn(CardTypeController.class).cards(entity.getKey(), new Pager<Card>(), new ArrayList<PropertyFilter>())).withRel("cards"));
        return resource;
    }

    public Pager<ResultResourceSupport> toResources(Pager<CardType> pager) {
        return new Pager<>(pager,this.toResources(pager.getPageItems()));
    }

}
