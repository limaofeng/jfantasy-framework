package org.jfantasy.member.rest;

import org.jfantasy.framework.jackson.annotation.IgnoreProperty;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.member.bean.Member;
import org.jfantasy.member.bean.Wallet;
import org.jfantasy.member.rest.models.PointDetails;
import org.jfantasy.member.rest.models.assembler.PointDetailsResourceAssembler;
import org.jfantasy.member.rest.models.assembler.WalletResourceAssembler;
import org.jfantasy.member.service.CardService;
import org.jfantasy.member.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/members")
public class MemberWalletController {

    private PointDetailsResourceAssembler assembler = new PointDetailsResourceAssembler();
    private WalletResourceAssembler walletAssembler = new WalletResourceAssembler();

    @Autowired
    private WalletService walletService;
    @Autowired
    private WalletController walletController;
    @Autowired
    private CardService cardService;

    /**
     * 用户钱包信息 - 返回钱包详情
     * @param id
     * @return
     */
    @JsonResultFilter(ignore = @IgnoreProperty(pojo = Wallet.class, name = {"member", "bills"}))
    @RequestMapping(value = "/{memid}/wallet", method = RequestMethod.GET)
    public ResultResourceSupport view(@PathVariable("memid") Long id) {
        Wallet wallet = walletService.getWalletByMember(id);
        ResultResourceSupport resource = walletController.view(wallet.getId());
        if (Member.MEMBER_TYPE_PERSONAL.equals(wallet.getMember().getType())) {
            resource.set("level", wallet.getMember().getDetails().getLevel());
        }
        resource.set("cards", cardService.count(wallet.getId()));
        return resource;
    }

    /**
     * 用户积分信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/{memid}/point-details", method = RequestMethod.GET)
    public ResultResourceSupport pointDetails(@PathVariable("memid") Long id) {
        PointDetails details = new PointDetails();
        Wallet wallet = walletService.getWalletByMember(id);
        details.setPoints(wallet.getPoints());
        return assembler.toResource(details);
    }

}
