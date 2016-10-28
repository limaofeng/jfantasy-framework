package org.jfantasy.pay.service;

import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.security.SpringSecurityUtils;
import org.jfantasy.framework.spring.mvc.error.RestException;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.oauth.userdetails.OAuthUserDetails;
import org.jfantasy.pay.bean.*;
import org.jfantasy.pay.bean.enums.*;
import org.jfantasy.pay.dao.AccountDao;
import org.jfantasy.pay.dao.BillDao;
import org.jfantasy.pay.dao.ProjectDao;
import org.jfantasy.pay.dao.TransactionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountDao accountDao;
    @Autowired
    private TransactionDao transactionDao;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private BillDao billDao;

    private PasswordEncoder passwordEncoder = new StandardPasswordEncoder();

    @Transactional
    public Pager<Account> findPager(Pager<Account> pager, List<PropertyFilter> filters) {
        return this.accountDao.findPager(pager, filters);
    }

    @Transactional
    public Account findUnique(AccountType type, String owner) {
        return this.accountDao.findUnique(Restrictions.eq("type", type), Restrictions.eq("owner", owner));
    }

    public Account get(String id) {
        return this.accountDao.get(id);
    }

    /**
     * 获取平台账号
     *
     * @return account
     */
    public Account platform() {
        return this.accountDao.findUnique(Restrictions.eq("type", AccountType.platform));
    }

    /**
     * 创建账户
     *
     * @param type  账户类型
     * @param owner 所有者
     * @return Account
     */
    public Account save(AccountType type, String owner, String password) {
        if (this.accountDao.count(Restrictions.eq("type", type), Restrictions.eq("owner", owner)) > 0) {
            throw new ValidationException(103.1f, "账号存在,创建失败");
        }
        Account account = new Account();
        account.setType(type);
        account.setAmount(BigDecimal.ZERO);
        account.setPoints(0L);
        account.setOwner(owner);
        account.setStatus(StringUtil.isBlank(password) ? AccountStatus.unactivated : AccountStatus.activated);
        return this.accountDao.save(account);
    }

    /**
     * 获取当前用户对应的账号信息
     *
     * @return account
     */
    public Account findUniqueByCurrentUser() {
        OAuthUserDetails user = SpringSecurityUtils.getCurrentUser(OAuthUserDetails.class);
        assert user != null;
        String key = user.getKey();
        Account account = this.accountDao.findUnique(Restrictions.eq("owner", key));
        return account == null ? save(AccountType.personal, key, "") : account;
    }

    /**
     * 划账接口
     *
     * @param trxNo    交易单号
     * @param password 如果为 internal 内部付款需要提供支付密码
     * @param notes    描述
     * @return Transaction
     */
    @Transactional
    public Transaction transfer(String trxNo, String password, String notes) {
        Transaction transaction = transactionDao.get(trxNo);
        if (transaction.getChannel() == TxChannel.internal) {
            /*
            Account from = this.accountDao.get(transaction.getFrom());
            if (from.getStatus() != AccountStatus.activated) {
                throw new RestException("账户未激活不能进行付款操作");
            }
            if (StringUtil.isBlank(password)) {
                throw new RestException("支付密码不能为空");
            }
            if (!passwordEncoder.matches(from.getPassword(), password)) {
                throw new RestException("支付密码错误");
            }*/
        }
        return this.transfer(trxNo, notes);
    }

    /**
     * 划账接口
     *
     * @param trxNo 交易单号
     * @param notes 描述
     * @return Transaction
     */
    @Transactional
    public Transaction transfer(String trxNo, String notes) {
        Transaction transaction = transactionDao.get(trxNo);
        if (transaction.getStatus() == TxStatus.close) {
            throw new RestException("交易已经关闭,不能划账");
        }
        if (transaction.getStatus() == TxStatus.success) {
            throw new RestException("交易已经完成,不能划账");
        }

        if (StringUtil.isBlank(transaction.getPayConfigName())) {
            transaction.setPayConfigName(transaction.getChannel() == TxChannel.internal ? "账户余额" : "未知");
        }

        if (StringUtil.isNotBlank(transaction.getFrom())) {
            Account from = this.accountDao.get(transaction.getFrom());
            if (from.getAmount().compareTo(transaction.getAmount()) < 0) {
                throw new RestException("账户余额不足,支付失败");
            }
            from.setAmount(from.getAmount().subtract(transaction.getAmount()));
            this.addBill(BillType.debit, transaction, from);//添加对应账单
            this.accountDao.update(from);
        }

        if (StringUtil.isNotBlank(transaction.getTo())) {
            Account to = this.accountDao.get(transaction.getTo());
            to.setAmount(to.getAmount().add(transaction.getAmount()));
            this.addBill(BillType.credit, transaction, to);//添加对应账单
            this.accountDao.update(to);
        }

        //更新交易状态
        transaction.setStatus(TxStatus.success);
        transaction.setStatusText(transaction.getStatus().name());
        transaction.setNotes(notes);
        return transactionDao.save(transaction);
    }

    private void addBill(BillType type, Transaction transaction, Account account) {
        Project project = transaction.getProject();
        Bill bill = new Bill();
        bill.setAccount(account);
        bill.setType(type);
        bill.setAmount(transaction.getAmount());
        bill.setProject(project.getName());
        bill.setPaymentMethod(transaction.getPayConfigName());
        bill.setBalance(account.getAmount());
        this.billDao.save(bill);
    }

    @Transactional
    public Account activate(String no, String password) {
        Account account = this.accountDao.get(no);
        account.setPassword(passwordEncoder.encode(password));
        return this.accountDao.save(account);
    }

    /**
     * 绑卡记录交易
     *
     * @param card 卡
     */
    @Transactional
    public void inpour(Card card) {
        Account to = accountDao.findUnique(Restrictions.eq("owner", card.getOwner()));
        //添加充值记录
        Transaction transaction = new Transaction();
        transaction.setAmount(card.getAmount());
        transaction.setChannel(TxChannel.internal);
        transaction.setTo(to.getSn());
        transaction.set(Transaction.CARD_ID, card.getNo());
        transaction.setProject(projectDao.get(Project.INPOUR));
        transaction.setUnionId(Transaction.generateUnionid(transaction.getProject().getKey(), card.getNo()));
        transaction.setStatus(TxStatus.unprocessed);
        transaction.setStatusText(TxStatus.unprocessed.name());
        transaction.setNotes("会员卡充值");
        transaction.setPayConfigName("会员卡充值");
        this.transactionDao.save(transaction);
    }

}
