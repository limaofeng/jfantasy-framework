package org.jfantasy.member.rest;

import org.jfantasy.framework.spring.mvc.error.NotFoundException;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.member.bean.TeamInvite;
import org.jfantasy.member.rest.models.assembler.InviteResourceAssembler;
import org.jfantasy.member.service.InviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members/{mid}/invites")
public class MemberInviteController {

    protected static InviteResourceAssembler assembler = new InviteResourceAssembler();

    private final InviteService inviteService;

    @Autowired
    public MemberInviteController(InviteService inviteService) {
        this.inviteService = inviteService;
    }

    /**
     * 发起邀请
     *
     * @param teamInvite
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResultResourceSupport create(@RequestBody TeamInvite teamInvite) {
        return assembler.toResource(this.inviteService.save(teamInvite));
    }

    /**
     * 查看邀请
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResultResourceSupport view(@PathVariable("id") Long id) {
        return assembler.toResource(this.get(id));
    }

    /**
     * 删除邀请
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        this.inviteService.deltele(id);
    }

    private TeamInvite get(Long id) {
        TeamInvite teamInvite = this.inviteService.get(id);
        if (teamInvite == null) {
            throw new NotFoundException("[id =" + id + "]对应的发票申请信息不存在");
        }
        return teamInvite;
    }

}
