package org.jfantasy.member.rest.models.assembler;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.member.bean.TeamInvite;
import org.jfantasy.member.rest.MemberInviteController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

public class InviteResourceAssembler extends ResourceAssemblerSupport<TeamInvite, ResultResourceSupport> {

    public InviteResourceAssembler() {
        super(MemberInviteController.class, ResultResourceSupport.class);
    }

    @Override
    protected ResultResourceSupport<TeamInvite> instantiateResource(TeamInvite entity) {
        return new ResultResourceSupport<>(entity);
    }

    @Override
    public ResultResourceSupport toResource(TeamInvite entity) {
        return createResourceWithId(entity.getId(), entity);
    }

    public Pager<ResultResourceSupport> toResources(Pager<TeamInvite> pager) {
        return new Pager<>(pager,this.toResources(pager.getPageItems()));
    }

}
