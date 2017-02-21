package org.jfantasy.member.rest;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiParam;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.jackson.annotation.AllowProperty;
import org.jfantasy.framework.jackson.annotation.IgnoreProperty;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.framework.spring.mvc.error.NotFoundException;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.web.RedirectAttributesWriter;
import org.jfantasy.framework.util.web.WebUtil;
import org.jfantasy.member.bean.Comment;
import org.jfantasy.member.bean.Favorite;
import org.jfantasy.member.bean.Member;
import org.jfantasy.member.bean.MemberDetails;
import org.jfantasy.member.bean.enums.SignUpType;
import org.jfantasy.member.bean.enums.TeamMemberStatus;
import org.jfantasy.member.rest.models.PasswordForm;
import org.jfantasy.member.rest.models.RegisterForm;
import org.jfantasy.member.rest.models.assembler.MemberResourceAssembler;
import org.jfantasy.member.rest.models.assembler.ProfileResourceAssembler;
import org.jfantasy.member.service.FavoriteService;
import org.jfantasy.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 会员接口
 **/
@RestController
@RequestMapping("/members")
public class MemberController {

    private static final String FILTERS_EQ_MEMBERID = "EQL_memberId";
    private static final String FILTERS_EQ_MEMBER_ID = "EQL_member.id";

    public static final MemberResourceAssembler assembler = new MemberResourceAssembler();
    private static final ProfileResourceAssembler profileAssembler = new ProfileResourceAssembler();

    private final MemberService memberService;
    private final FavoriteService favoriteService;

    private TeamController teamController;
    private CommentController commentController;

    @Autowired
    public MemberController(MemberService memberService, FavoriteService favoriteService) {
        this.memberService = memberService;
        this.favoriteService = favoriteService;
    }

    /**
     * 查询会员信息<br/>
     * 通过 filters 可以过滤数据<br/>本接口支持 <br/> X-Page-Fields、X-Result-Fields、X-Expend-Fields 功能
     *
     * @param pager   翻页对象
     * @param filters 筛选
     * @return Pager<ResultResourceSupport>
     */
    @JsonResultFilter(
            ignore = @IgnoreProperty(pojo = Member.class, name = {"password", "enabled", "accountNonExpired", "accountNonLocked", "credentialsNonExpired"}),
            allow = @AllowProperty(pojo = MemberDetails.class, name = {"name", "sex", "birthday", "avatar"})
    )
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParam(value = "filters", name = "filters", paramType = "query", dataType = "string")
    public Pager<ResultResourceSupport> search(Pager<Member> pager, @ApiParam(hidden = true) List<PropertyFilter> filters) {
        return assembler.toResources(this.memberService.findPager(pager, filters));
    }

    /**
     * 会员详情
     *
     * @param id KEY
     * @return Member
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResultResourceSupport view(@PathVariable("id") Long id) {
        return assembler.toResource(get(id));
    }

    @JsonResultFilter(ignore = @IgnoreProperty(pojo = Favorite.class, name = {"member", "member_id"}))
    @RequestMapping(value = "/{id}/favorites", method = RequestMethod.GET)
    @ResponseBody
    public List<Favorite> favorites(@PathVariable("id") Long id, @RequestParam(value = "type") String type) {
        return this.favoriteService.findByMemberId(id, type);
    }

    /**
     * 获取用户的详细信息
     *
     * @param response HttpServletResponse
     * @param id       id
     * @return ResultResourceSupport
     */
    @JsonResultFilter(
            allow = @AllowProperty(pojo = Member.class, name = {"id", "target_id", "target_type", "type", "username"})
    )
    @RequestMapping(value = "/{id}/profile", method = RequestMethod.GET)
    @ResponseBody
    public ResultResourceSupport profile(HttpServletResponse response, @PathVariable("id") Long id, @RequestParam(value = "type", defaultValue = Member.MEMBER_TYPE_PERSONAL) String type) {
        Member member = get(id);
        if (ObjectUtil.exists(member.getTypes(), "id", type)) {
            return profileAssembler.toResource(member.getDetails());
        }
        response.setStatus(307);
        return assembler.toResource(member);
    }

    @JsonResultFilter(
            allow = @AllowProperty(pojo = Member.class, name = {"id", "target_id", "target_type", "type", "username"})
    )
    @RequestMapping(value = "/{id}/profile", method = RequestMethod.PUT)
    @ResponseBody
    public ResultResourceSupport profile(HttpServletResponse response, @PathVariable("id") Long id, @RequestBody MemberDetails details) {
        Member member = get(id);
        if (ObjectUtil.exists(member.getTypes(), "id", Member.MEMBER_TYPE_PERSONAL)) {
            details.setMemberId(id);
            return profileAssembler.toResource(memberService.update(details));
        }
        response.setStatus(307);
        return assembler.toResource(member);
    }

    private Member get(Long id) {
        Member member = this.memberService.get(id);
        if (member == null) {
            throw new NotFoundException("用户不存在");
        }
        return member;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    public ResultResourceSupport create(@Validated(RESTful.POST.class) @RequestBody RegisterForm form) {
        if (StringUtil.isNotBlank(form.getMacode())) {
            //TODO 需要验证注册验证码
        }
        return assembler.toResource(memberService.signUp(form.getUsername(), form.getPassword(), SignUpType.sms));
    }

    @JsonResultFilter(
            ignore = @IgnoreProperty(pojo = Member.class, name = {"password", "enabled", "account_nonExpired", "accountNonLocked", "credentialsNonExpired"}),
            allow = @AllowProperty(pojo = MemberDetails.class, name = {"name", "sex", "birthday", "avatar"})
    )
    @RequestMapping(value = "/{id}/password", method = RequestMethod.PUT)
    @ResponseBody
    public ResultResourceSupport password(@PathVariable("id") Long id, @RequestBody PasswordForm form) {
        return assembler.toResource(this.memberService.changePassword(id, form.getType(), form.getOldPassword(), form.getNewPassword()));
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.PATCH, RequestMethod.PUT})
    @ResponseBody
    public ResultResourceSupport update(HttpServletRequest request, @PathVariable("id") Long id, @RequestBody Member member) {
        member.setId(id);
        return assembler.toResource(memberService.update(member, WebUtil.has(request, RequestMethod.PATCH)));
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.DELETE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        this.memberService.delete(id);
    }

    /**
     * 查询会员评论 - 返回会员的会员评论
     *
     * @param memberId 会员ID
     * @param pager    分页
     * @param filters  筛选
     * @return Pager<Comment>
     */
    @JsonResultFilter(
            ignore = @IgnoreProperty(pojo = Comment.class, name = {"member"})
    )
    @RequestMapping(value = "/{memid}/comments", method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParam(value = "filters", name = "filters", paramType = "query", dataType = "string")
    public Pager<ResultResourceSupport> comments(@PathVariable("memid") Long memberId, Pager<Comment> pager, @ApiParam(hidden = true) List<PropertyFilter> filters) {
        filters.add(new PropertyFilter(FILTERS_EQ_MEMBER_ID, memberId.toString()));
        if (!pager.isOrderBySetted()) {
            pager.setOrderBy(Comment.FIELDS_BY_CREATE_TIME);
            pager.setOrder(Pager.SORT_DESC);
        }
        return this.commentController.search(pager, filters);
    }

    /**
     * 查询会员收货地址 - 返回会员的会员评论
     *
     * @param memberId 会员ID
     * @param filters  筛选
     * @return List<Receiver>
     */
    @RequestMapping(value = "/{memid}/receivers", method = RequestMethod.GET)
    public ModelAndView receivers(@PathVariable("memid") Long memberId, RedirectAttributes attrs, List<PropertyFilter> filters) {
        attrs.addAttribute(FILTERS_EQ_MEMBERID, memberId);
        RedirectAttributesWriter.writer(attrs).write(filters);
        return new ModelAndView("redirect:/receivers");
    }

    /**
     * 查询会员的开票信息
     *
     * @param memberId 会员ID
     * @param pager    分页对象
     * @param filters  筛选
     * @return Pager<ResultResourceSupport>
     */
    @GetMapping("/{memid}/invoices")
    public ModelAndView invoices(@PathVariable("memid") Long memberId, RedirectAttributes attrs, Pager pager, List<PropertyFilter> filters) {
        attrs.addAttribute(FILTERS_EQ_MEMBERID, memberId);
        if (!pager.isOrderBySetted()) {
            pager.setOrderBy(BaseBusEntity.FIELDS_BY_CREATE_TIME);
            pager.setOrder(Pager.SORT_DESC);
        }
        pager.writeTo(attrs).write(filters);
        return new ModelAndView("redirect:/invoices");
    }

    @GetMapping("{id}/orders")
    public ModelAndView search(@PathVariable("id") Long id, @RequestParam(value = "status", required = false) String status, RedirectAttributes attrs, Pager pager, List<PropertyFilter> filters) {
        attrs.addAttribute(FILTERS_EQ_MEMBERID, id);
        if (StringUtil.isNotBlank(status)) {
            attrs.addAttribute("EQE_status", status);
        }
        pager.writeTo(attrs).write(filters);
        return new ModelAndView("redirect:/orders");
    }

    /**
     * 查询会员的团队信息
     *
     * @param memberId 会员id
     * @param type     团队类型
     * @param filters  筛选
     * @return List<ResultResourceSupport>
     */
    @RequestMapping(value = "/{memid}/teams", method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParam(value = "filters", name = "filters", paramType = "query", dataType = "string")
    public List<ResultResourceSupport> teams(@PathVariable("memid") Long memberId, @RequestParam(value = "type", required = false) String type, @ApiParam(hidden = true) List<PropertyFilter> filters) {
        filters.add(new PropertyFilter("EQL_teamMembers.member.id", memberId.toString()));//包含当前会员
        filters.add(new PropertyFilter("EQE_teamMembers.status", TeamMemberStatus.activated));//状态有效
        return teamController.search(type, new Pager<>(1000), filters).getPageItems();
    }

    @Autowired
    public void setTeamController(TeamController teamController) {
        this.teamController = teamController;
    }

    @Autowired
    public void setCommentController(CommentController commentController) {
        this.commentController = commentController;
    }

}
