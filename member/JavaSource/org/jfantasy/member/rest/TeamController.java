package org.jfantasy.member.rest;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiParam;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.jackson.annotation.IgnoreProperty;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.framework.spring.mvc.error.NotFoundException;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.framework.util.web.WebUtil;
import org.jfantasy.member.bean.Address;
import org.jfantasy.member.bean.Invite;
import org.jfantasy.member.bean.Team;
import org.jfantasy.member.bean.TeamMember;
import org.jfantasy.member.rest.models.assembler.TeamResourceAssembler;
import org.jfantasy.member.service.InviteService;
import org.jfantasy.member.service.TeamMemberService;
import org.jfantasy.member.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 团队
 **/
@RestController
@RequestMapping("/teams")
public class TeamController {

    private TeamResourceAssembler assembler = new TeamResourceAssembler();

    private final TeamService teamService;
    private final InviteService inviteService;
    private final TeamMemberService teamMemberService;
    private AddressController addressController;

    @Autowired
    public TeamController(TeamService teamService, InviteService inviteService, TeamMemberService teamMemberService) {
        this.teamService = teamService;
        this.inviteService = inviteService;
        this.teamMemberService = teamMemberService;
    }


    /**
     * 团队列表 - 团队列表
     **/
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParam(value = "filters",name = "filters",paramType = "query",dataType = "string")
    public Pager<ResultResourceSupport> search(@RequestParam(value = "type", required = false) String type, Pager<Team> pager, List<PropertyFilter> filters) {
        filters.add(new PropertyFilter("EQS_type", type));
        return assembler.toResources(this.teamService.findPager(pager, filters));
    }

    /**
     * 查看团队 - 查看团队
     **/
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResultResourceSupport view(@PathVariable("id") String id) {
        return assembler.toResource(this.get(id));
    }

    /**
     * 添加团队 - 添加团队
     **/
    @RequestMapping(method = RequestMethod.POST)
    public ResultResourceSupport create(@Validated(RESTful.POST.class) @RequestBody Team team) {
        return assembler.toResource(this.teamService.save(team));
    }

    /**
     * 更新团队 - 更新团队地址
     **/
    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResultResourceSupport update(@PathVariable("id") String id, @RequestBody Team team, HttpServletRequest request) {
        team.setKey(id);
        return assembler.toResource(this.teamService.update(team, WebUtil.has(request,RequestMethod.PATCH)));
    }

    /**
     * 删除团队 - 删除团队
     **/
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String id) {
        this.teamService.deltele(id);
    }


    /**
     * 邀请列表 - 邀请列表
     **/
    @RequestMapping(value = "/{id}/invites", method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParam(value = "filters",name = "filters",paramType = "query",dataType = "string")
    public Pager<ResultResourceSupport> invites(@PathVariable("id") String id, Pager<Invite> pager,@ApiParam(hidden = true) List<PropertyFilter> filters) {
        filters.add(new PropertyFilter("EQS_team.key", id));
        return InviteController.assembler.toResources(this.inviteService.findPager(pager, filters));
    }

    /**
     * 批量邀请 - 批量邀请
     **/
    @RequestMapping(value = "/{id}/invites", method = RequestMethod.POST)
    @ResponseBody
    public List<ResultResourceSupport> invites(@PathVariable("id") String id, @RequestBody List<Invite> invites) {
        return InviteController.assembler.toResources(inviteService.save(id, invites));
    }

    /**
     * 团队成员列表
     *
     * @param id      集团ID
     * @param pager   翻页对象
     * @param filters 过滤接口
     * @return Pager<ResultResourceSupport>
     */
    @JsonResultFilter(ignore = @IgnoreProperty(pojo = TeamMember.class, name = {"team", "member"}))
    @RequestMapping(value = "/{id}/members", method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParam(value = "filters",name = "filters",paramType = "query",dataType = "string")
    public Pager<ResultResourceSupport> members(@PathVariable("id") String id, Pager<TeamMember> pager,@ApiParam(hidden = true) List<PropertyFilter> filters) {
        filters.add(new PropertyFilter("EQS_team.key", id));
        return TeamMemberController.assembler.toResources(this.teamMemberService.findPager(pager, filters));
    }

    /**
     * 团队地址列表
     **/
    @RequestMapping(value = "/{id}/addresses", method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParam(value = "filters",name = "filters",paramType = "query",dataType = "string")
    public List<Address> addresses(@PathVariable("id") String id, Pager<Address> pager,@ApiParam(hidden = true) List<PropertyFilter> filters) {
        filters.add(new PropertyFilter("EQS_ownerType", "team"));
        filters.add(new PropertyFilter("EQS_ownerId", get(id).getKey()));
        return this.addressController.search(pager, filters).getPageItems();
    }

    /**
     * 集团的发票申请列表
     * @param teamId
     * @param pager
     * @param filters
     * @return
     */
    @GetMapping("{id}/invoices")
    public ModelAndView invoices(@PathVariable("id") String teamId, RedirectAttributes attrs, Pager pager, List<PropertyFilter> filters) {
        get(teamId);
        attrs.addAttribute("EQS_targetType", "team");
        attrs.addAttribute("EQS_targetId", teamId);
        attrs.addAttribute("page", pager.getCurrentPage());
        attrs.addAttribute("per_page", pager.getPageSize());
        if (pager.isOrderBySetted()) {
            attrs.addAttribute("sort", pager.getOrderBy());
            attrs.addAttribute("order", pager.getOrder());
        }
        for (PropertyFilter filter : filters) {
            attrs.addAttribute(filter.getFilterName(), filter.getPropertyValue());
        }
        return new ModelAndView("redirect:/invoices");
    }

    private Team get(String id) {
        Team team = this.teamService.get(id);
        if (team == null) {
            throw new NotFoundException("[id =" + id + "]对应的团队信息不存在");
        }
        return team;
    }

    @Autowired
    public void setAddressController(AddressController addressController) {
        this.addressController = addressController;
    }

}
