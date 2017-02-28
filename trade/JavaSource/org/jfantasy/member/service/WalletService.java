package org.jfantasy.member.service;

import org.hibernate.criterion.Restrictions;
import org.jfantasy.card.bean.Card;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.member.bean.Wallet;
import org.jfantasy.member.dao.WalletDao;
import org.jfantasy.order.bean.ExtraService;
import org.jfantasy.trade.bean.Account;
import org.jfantasy.trade.bean.enums.AccountType;
import org.jfantasy.trade.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletService {

    private final WalletDao walletDao;
    private AccountService accountService;

    @Autowired
    public WalletService(WalletDao walletDao) {
        this.walletDao = walletDao;
    }

    private Wallet loadByAccount(String accountNo) {
        Wallet wallet = this.walletDao.findUnique(Restrictions.eq("account", accountNo));
        if (wallet != null) {
            return wallet;
        }
        return newWallet(this.accountService.get(accountNo));
    }

    /**
     * 通过账户创建钱包
     *
     * @param account 账户
     * @return Wallet
     */
    private Wallet newWallet(Account account) {
        Wallet wallet = new Wallet();
        wallet.setMemberId(Long.valueOf(account.getOwner()));
        wallet.setAccount(account.getSn());
        wallet.setAmount(account.getAmount());
        wallet.setIncome(BigDecimal.ZERO);
        //初始化积分与成长值
        wallet.setGrowth(0L);
        wallet.setPoints(0L);
        wallet.setCards(0L);
        //初始化账单 并 计算收益
        return walletDao.insert(wallet);
    }

    private Wallet save(Long memberId) {
        Wallet wallet = this.walletDao.findUnique(Restrictions.eq("memberId", memberId));
        if (wallet == null) {
            return newWallet(this.accountService.loadAccountByOwner(AccountType.personal, memberId.toString()));
        }
        return wallet;
    }

    @Transactional
    public Wallet getWalletByMember(Long memberId) {
        return this.save(memberId);
    }

    public Wallet getWallet(Long id) {
        return this.walletDao.findUnique(Restrictions.eq("id", id));
    }

    public Pager<Wallet> findPager(Pager<Wallet> pager, List<PropertyFilter> filters) {
        return this.walletDao.findPager(pager, filters);
    }

    @Transactional
    public Wallet saveOrUpdateWallet(Account account) {
        Wallet wallet = this.walletDao.findUnique(Restrictions.eq("account", account.getSn()));
        if (wallet == null) {
            return newWallet(account);
        } else {
            wallet.setAmount(account.getAmount());
            this.walletDao.update(wallet);
            return wallet;
        }
    }

    @Transactional
    public void addCard(Card card) {
        // 关联卡
        Wallet wallet = this.loadByAccount(card.getAccount());
        // 计算附加服务
        ExtraService[] services = card.getExtras();
        if (services.length != 0) {
            //添加成长值
            ExtraService service = ObjectUtil.find(services, "project", ExtraService.ExtraProject.growth);
            if (service != null) {
                if (wallet.getGrowth() == null) {
                    wallet.setGrowth(0L);
                }
                wallet.setGrowth(wallet.getGrowth() + service.getValue());
            }
            //添加积分
            service = ObjectUtil.find(services, "project", ExtraService.ExtraProject.point);
            if (service != null) {
                if (wallet.getPoints() == null) {
                    wallet.setPoints(0L);
                }
                wallet.setPoints(wallet.getPoints() + service.getValue());
            }
        }
        wallet.setCards(ObjectUtil.defaultValue(wallet.getCards(), 0L) + 1);
        this.walletDao.update(wallet);
    }

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

}
