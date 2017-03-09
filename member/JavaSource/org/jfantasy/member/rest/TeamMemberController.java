package org.jfantasy.member.rest;

import org.jfantasy.framework.jackson.annotation.IgnoreProperty;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.framework.spring.mvc.error.NotFoundException;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.web.WebUtil;
import org.jfantasy.member.bean.TeamMember;
import org.jfantasy.member.rest.models.assembler.TeamMemberResourceAssembler;
import org.jfantasy.member.service.TeamMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 团队成员
 */
@RestController
@RequestMapping("/team-members")
public class TeamMemberController {

    public static final TeamMemberResourceAssembler assembler = new TeamMemberResourceAssembler();

    private final TeamMemberService teamMemberService;

    @Autowired
    public TeamMemberController(TeamMemberService teamMemberService) {
        this.teamMemberService = teamMemberService;
    }

    /**
     * 查看团队成员 - 查看团队成员
     *
     * @param id TMID
     * @return TeamMember
     */
    @RequestMapping(value = "/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public ResultResourceSupport view(@PathVariable("id") String id, @RequestParam(value = "type", defaultValue = "id") String type, @RequestParam(value = "tid", required = false) String tid) {
        return assembler.toResource(this.get(type, id, tid));
    }

    /**
     * 添加团队成员 - 添加团队成员
     *
     * @param member TMID
     * @return TeamMember
     */
    @JsonResultFilter(ignore = @IgnoreProperty(pojo = TeamMember.class, name = "team"))
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResourceSupport create(@RequestBody TeamMember member) {
        return assembler.toResource(this.teamMemberService.save(member));
    }

    /**
     * 更新团队成员 - 更新团队成员地址
     **/
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResultResourceSupport update(@PathVariable("id") Long id, HttpServletRequest request, @RequestBody TeamMember member) {
        member.setId(id);
        return assembler.toResource(this.teamMemberService.update(member, WebUtil.has(request, RequestMethod.PATCH)));
    }

    /**
     * 删除团队成员 - 删除团队成员
     **/
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        this.teamMemberService.deltele(id);
    }

    private TeamMember get(Long id) {
        TeamMember member = this.teamMemberService.get(id);
        if (member == null) {
            throw new NotFoundException("[id =" + id + "]对应的团队成员信息不存在");
        }
        return member;
    }

    private TeamMember get(String type, String id, String tid) {
        TeamMember member;
        switch (type) {
            case "id":
                member = this.teamMemberService.get(Long.valueOf(id));
                break;
            case "phone":
                if(StringUtil.isBlank(tid)){
                    throw new ValidationException("type = phone 时，必须提供 tid 参数");
                }
                member = this.teamMemberService.findUnique(tid, id);
                break;
            default:
                throw new ValidationException("不支持的查询方式");
        }
        if (member == null) {
            throw new NotFoundException("用户不存在");
        }
        return member;
    }

}
