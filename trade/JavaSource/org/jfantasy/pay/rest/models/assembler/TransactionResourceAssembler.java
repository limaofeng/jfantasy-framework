package org.jfantasy.pay.rest.models.assembler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.trade.bean.Transaction;
import org.jfantasy.trade.bean.enums.BillType;
import org.jfantasy.pay.bean.enums.OwnerType;
import org.jfantasy.pay.rest.LogController;
import org.jfantasy.trade.rest.TransactionController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class TransactionResourceAssembler extends ResourceAssemblerSupport<Transaction, ResultResourceSupport> {

    @JsonIgnore
    private String account;

    public TransactionResourceAssembler() {
        super(TransactionController.class, ResultResourceSupport.class);
    }

    public TransactionResourceAssembler(String sn) {
        this();
        this.account = sn;
    }

    @Override
    protected ResultResourceSupport<Transaction> instantiateResource(Transaction entity) {
        return new ResultResourceSupport<>(entity);
    }

    @Override
    public ResultResourceSupport toResource(Transaction entity) {
        ResultResourceSupport resource = createResourceWithId(entity.getSn(), entity);
        resource.add(linkTo(methodOn(LogController.class).search(OwnerType.transaction, entity.getSn())).withRel("logs"));
        if (account != null) {
            resource.set("type", account.equals(entity.getFrom()) ? BillType.credit : BillType.debit);//TODO 弄反了，改哪里勒
        }
        return resource;
    }

    public Pager<ResultResourceSupport> toResources(Pager<Transaction> pager) {
        return new Pager<>(pager, this.toResources(pager.getPageItems()));
    }

}
