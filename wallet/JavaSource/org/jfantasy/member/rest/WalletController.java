package org.jfantasy.member.rest;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.jackson.annotation.AllowProperty;
import org.jfantasy.framework.jackson.annotation.IgnoreProperty;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.member.bean.Card;
import org.jfantasy.member.bean.Member;
import org.jfantasy.member.bean.Wallet;
import org.jfantasy.member.bean.WalletBill;
import org.jfantasy.member.rest.models.assembler.WalletResourceAssembler;
import org.jfantasy.member.service.CardService;
import org.jfantasy.member.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 钱包接口
 */
@RestController
@RequestMapping("/wallets")
public class WalletController {

    private static final WalletResourceAssembler assembler = new WalletResourceAssembler();

    private WalletService walletService;
    private CardService cardService;
    private WalletBillController walletBillController;

    /**
     * 钱包列表 - 查询所有的钱包
     *
     * @param pager   分页对象
     * @param filters 过滤器
     * @return Pager<Wallet>
     */
    @JsonResultFilter(allow = @AllowProperty(pojo = Member.class, name = {"id", "username", "nickName"}))
    @RequestMapping(method = RequestMethod.GET)
    public Pager<ResultResourceSupport> search(Pager<Wallet> pager, List<PropertyFilter> filters) {
        return assembler.toResources(this.walletService.findPager(pager, filters));
    }

    /**
     * 获取钱包信息 - 返回钱包详情
     *
     * @param id 钱包ID
     * @return Wallet
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResultResourceSupport view(@PathVariable("id") Long id) {
        return assembler.toResource(this.walletService.getWallet(id));
    }

    /**
     * 查询钱包中的账单信息 - 账单列表
     *
     * @param walletId 钱包ID
     * @param pager    分页对象
     * @param filters  过滤器
     * @return Pager<Bill>
     */
    @RequestMapping(value = "/{id}/bills", method = RequestMethod.GET)
    public Pager<ResultResourceSupport> bills(@PathVariable("id") String walletId, Pager<WalletBill> pager, List<PropertyFilter> filters) {
        filters.add(new PropertyFilter("EQL_wallet.id", walletId));
        return walletBillController.search(pager, filters);
    }

    /**
     * 查询钱包中的卡片 - 卡列表
     *
     * @param walletId 钱包ID
     * @return List<Card>
     */
    @JsonResultFilter(ignore = @IgnoreProperty(pojo = Card.class, name = Card.BASE_JSONFIELDS))
    @RequestMapping(value = "/{id}/cards", method = RequestMethod.GET)
    public List<Card> cards(@PathVariable("id") Long walletId) {
        return cardService.findByWallet(walletId);
    }

    @Autowired
    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

    @Autowired
    public void setCardService(CardService cardService) {
        this.cardService = cardService;
    }

    @Autowired
    public void setWalletBillController(WalletBillController walletBillController) {
        this.walletBillController = walletBillController;
    }

}
