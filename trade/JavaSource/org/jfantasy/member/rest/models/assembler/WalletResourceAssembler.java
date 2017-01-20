package org.jfantasy.member.rest.models.assembler;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.member.bean.Wallet;
import org.jfantasy.member.rest.WalletController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

public class WalletResourceAssembler extends ResourceAssemblerSupport<Wallet, ResultResourceSupport> {

    public WalletResourceAssembler() {
        super(WalletController.class, ResultResourceSupport.class);
    }

    @Override
    protected ResultResourceSupport<Wallet> instantiateResource(Wallet entity) {
        return new ResultResourceSupport<>(entity);
    }

    @Override
    public ResultResourceSupport toResource(Wallet entity) {
        return createResourceWithId(entity.getId(), entity);
    }

    public Pager<ResultResourceSupport> toResources(Pager<Wallet> pager) {
        return new Pager<>(pager,this.toResources(pager.getPageItems()));
    }

}
