package org.jfantasy.trade.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Restrictions;
import org.jfantasy.card.bean.Card;
import org.jfantasy.card.dao.CardDao;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.security.SpringSecurityUtils;
import org.jfantasy.framework.spring.mvc.error.RestException;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.oauth.userdetails.OAuthUserDetails;
import org.jfantasy.order.bean.ExtraService;
import org.jfantasy.pay.error.PayException;
import org.jfantasy.trade.bean.*;
import org.jfantasy.trade.bean.enums.*;
import org.jfantasy.trade.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private static Log LOG = LogFactory.getLog(AccountService.class);

    private final AccountDao accountDao;
    private final ProjectDao projectDao;
    private final TransactionDao transactionDao;
    private final PointDao pointDao;
    private final BillDao billDao;
    private final CardDao cardDao;

    private PasswordEncoder passwordEncoder = new StandardPasswordEncoder();

    @Autowired
    public AccountService(PointDao pointDao, ProjectDao projectDao, TransactionDao transactionDao, BillDao billDao, AccountDao accountDao, CardDao cardDao) {
        this.pointDao = pointDao;
        this.projectDao = projectDao;
        this.transactionDao = transactionDao;
        this.billDao = billDao;
        this.accountDao = accountDao;
        this.cardDao = cardDao;
    }

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
     * 创建账户
     *
     * @param type  账户类型
     * @param owner 所有者
     * @return Account
     */
    @Transactional
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
        /*
        if (transaction.getChannel() == TxChannel.internal) {
            Account from = this.accountDao.get(transaction.getFrom());
            if (from.getStatus() != AccountStatus.activated) {
                throw new RestException("账户未激活不能进行付款操作");
            }
            if (StringUtil.isBlank(password)) {
                throw new RestException("支付密码不能为空");
            }
            if (!passwordEncoder.matches(from.getPassword(), password)) {
                throw new RestException("支付密码错误");
            }
        }
        */
        if (StringUtil.isNotBlank(transaction.getFrom()) && accountDao.get(transaction.getFrom()) == null) {
            throw new ValidationException("[" + transaction.getFrom() + "]账户不存在");
        }
        if (StringUtil.isNotBlank(transaction.getTo()) && accountDao.get(transaction.getTo()) == null) {
            throw new ValidationException("[" + transaction.getTo() + "]账户不存在");
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
        Project project = this.projectDao.get(transaction.getProject());
        if (transaction.getStatus() == TxStatus.close) {
            throw new RestException("交易已经关闭,不能划账");
        }
        if (transaction.getStatus() == TxStatus.success) {
            throw new RestException("交易已经完成,不能划账");
        }

        if (StringUtil.isBlank(transaction.getPayConfigName())) {
            transaction.setPayConfigName(transaction.getChannel() == TxChannel.internal ? "账户余额" : "未知");
        }

        try {
            this.out(transaction.getFrom(), transaction.getAmount(), transaction);
        } catch (PayException e) {
            LOG.error(e.getMessage(), e);
            return this.close(transaction, e.getMessage());
        }

        this.in(transaction.getTo(), transaction.getAmount(), project, transaction);

        if (Project.WITHDRAWAL.equals(project.getKey())) {//如果为提现交易，不修改交易状态。而且现在都是线下交易
            transaction.setPayConfigName("线下转账");
            transaction.setStatus(TxStatus.unprocessed);
            transaction.setStatusText(transaction.getStatus().getValue());
            transaction.setNotes(notes);
        } else {//更新交易状态
            transaction.setStatus(TxStatus.success);
            transaction.setStatusText(transaction.getStatus().getValue());
            transaction.setNotes(notes);
        }
        return transactionDao.save(transaction);
    }

    private void in(String account, BigDecimal amount, Project project, Transaction transaction) {
        if (StringUtil.isBlank(transaction.getTo())) {
            return;
        }
        Account to = this.accountDao.get(account);
        to.setAmount(to.getAmount().add(amount));
        // 充值积分处理
        if (Project.INPOUR.equals(project.getKey()) && Card.SUBJECT_BY_CARD_INPOUR.equals(transaction.getSubject())) {
            Card card = this.cardDao.get(transaction.get(Transaction.CARD_ID));
            ExtraService service = ObjectUtil.find(card.getExtras(), "project", ExtraService.ExtraProject.point);
            if (service != null) {
                to.setPoints(to.getPoints() + service.getValue());
                this.addPonit(to, (long) service.getValue(), "会员卡充值奖励");
            }
        }
        this.addBill(BillType.credit, transaction, to);//添加对应账单
        this.accountDao.update(to);
    }

    private void out(String account, BigDecimal amount, Transaction transaction) throws PayException {
        if (StringUtil.isBlank(account)) {
            return;
        }
        Account from = this.accountDao.get(account);
        if (from.getAmount().compareTo(amount) < 0) {
            throw new PayException("账户余额不足,交易失败");
        }
        from.setAmount(from.getAmount().subtract(amount));
        this.addBill(BillType.debit, transaction, from);//添加对应账单
        this.accountDao.update(from);
    }

    private Transaction close(Transaction transaction, String notes) {
        transaction.setStatus(TxStatus.close);
        transaction.setStatusText(TxStatus.close.getValue());
        transaction.setNotes(notes);
        return transactionDao.save(transaction);
    }

    private void addPonit(Account account, Long number, String notes) {
        Point point = new Point();
        point.setAccount(account);
        point.setExpire(null);
        point.setType(PointType.plus);
        point.setPoint(number);
        point.setStatus(PointStatus.finished);
        point.setNotes(notes);
        this.pointDao.save(point);
    }

    private void addBill(BillType type, Transaction transaction, Account account) {
        Project project = this.projectDao.get(transaction.getProject());
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

}
