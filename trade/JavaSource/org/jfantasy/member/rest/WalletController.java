package org.jfantasy.member.rest;

import io.swagger.annotations.ApiImplicitParam;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.member.bean.Wallet;
import org.jfantasy.member.rest.models.assembler.WalletResourceAssembler;
import org.jfantasy.member.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 钱包接口
 */
@RestController
@RequestMapping("/wallets")
public class WalletController {

    private static final WalletResourceAssembler assembler = new WalletResourceAssembler();

    private WalletService walletService;

    /**
     * 钱包列表 - 查询所有的钱包
     *
     * @param pager   分页对象
     * @param filters 过滤器
     * @return Pager<Wallet>
     */
    @RequestMapping(method = RequestMethod.GET)
    @ApiImplicitParam(value = "filters", name = "filters", paramType = "query", dataType = "string")
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
     * 查询钱包对于的账户信息
     *
     * @param id 钱包ID
     * @return ModelAndView
     */
    @RequestMapping(value = "/{id}/account", method = RequestMethod.GET)
    public ModelAndView teams(@PathVariable("id") Long id) {
        Wallet wallet = this.walletService.getWallet(id);
        return new ModelAndView("redirect:/accounts/" + wallet.getAccount());
    }

    @Autowired
    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

}