package org.jfantasy.member.rest;

import io.swagger.annotations.ApiImplicitParam;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.member.bean.Level;
import org.jfantasy.member.bean.Member;
import org.jfantasy.member.bean.Wallet;
import org.jfantasy.member.rest.models.assembler.LevelResourceAssembler;
import org.jfantasy.member.service.LevelService;
import org.jfantasy.member.service.MemberService;
import org.jfantasy.member.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员等级
 */
@RestController
public class LevelController {

    private LevelResourceAssembler assembler = new LevelResourceAssembler();

    private final LevelService levelService;
    private final WalletService walletService;
    private final MemberService memberService;

    @Autowired
    public LevelController(LevelService levelService, MemberService memberService, WalletService walletService) {
        this.levelService = levelService;
        this.memberService = memberService;
        this.walletService = walletService;
    }

    /**
     * 会员等级
     * @param filters 筛选条件
     * @return List<Level>
     */
    @RequestMapping(value = "/levels", method = RequestMethod.GET)
    @ApiImplicitParam(value = "filters",name = "filters",paramType = "query",dataType = "string")
    public List<ResultResourceSupport> search(List<PropertyFilter> filters) {
        return assembler.toResources(levelService.search(filters));
    }

    /** 添加等级 **/
    @RequestMapping(value = "/levels", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResultResourceSupport save(@RequestBody Level level) {
        return assembler.toResource(levelService.save(level));
    }

    /** 修改等级 **/
    @RequestMapping(value = "/levels/{id}", method = RequestMethod.PATCH)
    public ResultResourceSupport update(@PathVariable("id") Long id, @RequestBody Level level) {
        level.setId(id);
        return assembler.toResource(levelService.update(level));
    }

    /** 删除等级 **/
    @RequestMapping(value = "/levels/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable("id") Long id) {
        levelService.delete(id);
    }

    /**
     * 用户的会员等级
     * @param id ID
     * @return Level
     */
    @RequestMapping(value = "/members/{memid}/level", method = RequestMethod.GET)
    @ResponseBody
    public ResultResourceSupport level(@PathVariable("memid") Long id) {
        Member member = this.memberService.get(id);
        Wallet wallet = this.walletService.getWalletByMember(id);
        ResultResourceSupport resource = assembler.toResource(levelService.get(member.getDetails().getLevel()));
        resource.set("growth", wallet.getGrowth());//设置用户成长值
        return resource;
    }

}
