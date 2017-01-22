package org.jfantasy.trade.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jfantasy.card.bean.Card;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.SpringContextUtil;
import org.jfantasy.framework.spring.mvc.error.RestException;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.order.bean.Order;
import org.jfantasy.trade.bean.Account;
import org.jfantasy.trade.bean.Project;
import org.jfantasy.trade.bean.Transaction;
import org.jfantasy.trade.bean.enums.AccountType;
import org.jfantasy.trade.bean.enums.ProjectType;
import org.jfantasy.trade.bean.enums.TxChannel;
import org.jfantasy.trade.bean.enums.TxStatus;
import org.jfantasy.trade.dao.AccountDao;
import org.jfantasy.trade.dao.ProjectDao;
import org.jfantasy.trade.dao.TransactionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    private static final Log LOG = LogFactory.getLog(TransactionService.class);
    private static final String UNION_ID = "unionId";

    private final ProjectDao projectDao;
    private final TransactionDao transactionDao;
    private final AccountDao accountDao;
    private AccountService accountService;

    @Autowired
    public TransactionService(TransactionDao transactionDao, ProjectDao projectDao, AccountDao accountDao) {
        this.transactionDao = transactionDao;
        this.projectDao = projectDao;
        this.accountDao = accountDao;
    }

    /**
     * 获取平台账号
     *
     * @return account
     */
    public Account platform() {
        return this.accountDao.findUnique(Restrictions.eq("type", AccountType.platform));
    }

    @Transactional
    public Transaction get(String sn) {
        return transactionDao.get(sn);
    }

    public Transaction getByUnionId(String unionId) {
        return this.transactionDao.findUnique(Restrictions.eq("unionId", unionId));
    }

    @Transactional
    public Pager<Transaction> findPager(Pager<Transaction> pager, List<PropertyFilter> filters) {
        return this.transactionDao.findPager(pager, filters);
    }


    /**
     * 支付业务
     *
     * @param from       付款账户
     * @param amount     金额
     * @param notes      备注
     * @param properties 额外记录
     * @return Transaction
     */
    @Transactional
    public Transaction payment(String from, BigDecimal amount, String notes, Map<String, Object> properties) {
        String to = platform().getSn();// 平台收款
        return this.save(Project.PAYMENT, from, to, amount, notes, properties);
    }

    /**
     * 退款
     *
     * @param original 原交易
     * @param amount   退款金额
     * @param notes    备注
     * @return Transaction
     */
    @Transactional
    public Transaction refund(Transaction original, BigDecimal amount, String notes) {
        if (original == null || original.getStatus() != TxStatus.success) {
            throw new RestException("原交易不存在或者未支付成功");
        }
        Transaction transaction = this.save(Project.REFUND, original.getTo(), original.getFrom(), amount, notes, original.getProperties());
        transaction.setPayConfigName(original.getPayConfigName());
        return this.transactionDao.update(transaction);
    }

    /**
     * 同步保存
     *
     * @param projectKey 项目
     * @param from       转出账户
     * @param to         转入账户
     * @param amount     交易金额
     * @param notes      备注
     * @param properties 附加属性
     * @return Transaction
     */
    @Transactional
    public Transaction syncSave(String projectKey, String from, String to, BigDecimal amount, String notes, Map<String, Object> properties) {
        Transaction transaction = this.save(projectKey, from, to, amount, notes, properties);
        if(transaction.getStatus() == TxStatus.success){
            return transaction;
        }
        this.handleAllowFailure(transaction.getSn(), "");
        return transaction;
    }

    /**
     * 同步执行转账逻辑
     *
     * @param project 项目
     * @param from    转出账户
     * @param to      转入账户
     * @param channel 渠道
     * @param amount  交易金额
     * @param notes   备注
     * @param data    附加属性
     * @return Transaction
     */
    @Transactional
    public Transaction syncSave(String project, String from, String to, TxChannel channel, BigDecimal amount, String notes, Map<String, Object> data) {
        Transaction transaction = this.save(project, from, to, channel, amount, notes, data);
        this.handleAllowFailure(transaction.getSn(), "");
        return transaction;
    }

    /**
     * 异步执行转账逻辑
     *
     * @param projectKey 项目
     * @param from       转出账户
     * @param to         转入账户
     * @param amount     交易金额
     * @param notes      备注
     * @param properties 附加属性
     * @return Transaction
     */
    @Transactional
    public Transaction asyncSave(String projectKey, String from, String to, BigDecimal amount, String notes, Map<String, Object> properties) {
        return this.save(projectKey, from, to, amount, notes, properties);
    }

    private Transaction save(String projectKey, String from, String to, BigDecimal amount, String notes, Map<String, Object> properties) {
        return this.save(projectKey, from, to, null, amount, notes, properties);
    }

    @Transactional
    public Transaction getByUniqueId(String unionid) {
        return this.transactionDao.findUnique(Restrictions.eq(UNION_ID, unionid));
    }

    /**
     * 保存交易接口
     *
     * @param projectKey 项目
     * @param from       发起人
     * @param to         接收人
     * @param amount     金额
     * @param notes      备注
     * @param properties 扩展属性
     * @return Transaction
     */
    private Transaction save(String projectKey, String from, String to, TxChannel channel, BigDecimal amount, String notes, Map<String, Object> properties) {
        Project project = projectDao.get(projectKey);
        // 生成 unionid
        String orderId = (String) properties.get(Transaction.ORDER_ID);
        String orderType = (String) properties.get(Transaction.ORDER_TYPE);
        String key = StringUtil.defaultValue(properties.remove(Transaction.UNION_KEY), orderId);
        String unionid = Transaction.generateUnionid(project.getKey(), key);
        // 判断交易是否已经存在
        Transaction src = this.transactionDao.findUnique(Restrictions.eq(UNION_ID, unionid));
        if (src != null) {
            return src;
        }
        if (StringUtil.isBlank(from) && StringUtil.isBlank(to)) {
            throw new ValidationException(100000, "交易账户全部为NULL，输入数据不合法");
        }
        // 创建交易
        Transaction transaction = new Transaction();
        transaction.setUnionId(unionid);
        transaction.setProject(project.getKey());
        transaction.setFrom(from);
        transaction.setTo(to);
        transaction.setAmount(amount);
        transaction.setNotes(notes);
        transaction.setProperties(properties);
        if (project.getType() == ProjectType.order) {
            transaction.set("stage", Transaction.STAGE_PAYMENT);
            transaction.setChannel(TxChannel.online);
            transaction.setSubject(orderType);
            transaction.setOrder(new Order(orderId));
        }
        if (project.getType() == ProjectType.transfer) {
            transaction.setChannel(ObjectUtil.defaultValue(channel, TxChannel.internal));
            if (StringUtil.isNotBlank(orderType)) {
                transaction.setSubject(orderType);
            }
            if (StringUtil.isNotBlank(orderId)) {
                transaction.setOrder(new Order(orderId));
            }
        } else {
            transaction.setChannel(channel);
        }
        transaction.setStatus(TxStatus.unprocessed);
        transaction.setFlowStatus(0);
        transaction.setStatusText(projectKey.equals(Project.PAYMENT) ? "等待付款" : "待处理");
        //验证数据合法性
        project.getType().verify(transaction);
        return this.transactionDao.save(transaction);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void handleAllowFailure(String sn, String notes) {
        this.accountService.transfer(sn, notes);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void handleAllowFailure(String sn, String password, String notes) {
        this.accountService.transfer(sn, password, notes);
    }

    public void handle(String sn, String notes) {
        try {
            this.accountService.transfer(sn, notes);
        } catch (ValidationException e) {
            LOG.error(e.getMessage(), e);
            SpringContextUtil.getBeanByType(TransactionService.class).close(sn, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public Transaction update(String sn, TxStatus status, String statusText, String notes) {
        if (TxStatus.unprocessed.equals(status)) {
            throw new ValidationException("不能将交易状态修改为\"未处理\"");
        }
        Transaction transaction = this.transactionDao.get(sn);
        if (TxStatus.success.equals(transaction.getStatus()) || TxStatus.close.equals(transaction.getStatus())) {
            throw new ValidationException("交易已经完成或者关闭，不能继续操作");
        }
        transaction.setStatus(status);
        transaction.setStatusText(ObjectUtil.defaultValue(statusText, status.getValue()));
        transaction.setNotes(notes);
        return this.transactionDao.update(transaction);
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
        transaction.setChannel(TxChannel.card);
        transaction.setTo(to.getSn());
        transaction.set(Transaction.CARD_ID, card.getNo());
        transaction.setProject(Project.INPOUR);
        transaction.setSubject(Card.SUBJECT_BY_CARD_INPOUR);
        transaction.setUnionId(Transaction.generateUnionid(transaction.getProject(), card.getNo()));
        transaction.setStatus(TxStatus.unprocessed);
        transaction.setFlowStatus(0);
        transaction.setStatusText(TxStatus.unprocessed.getValue());
        transaction.setNotes("会员卡充值");
        transaction.setPayConfigName(TxChannel.card.getValue());
        this.transactionDao.save(transaction);
        this.handleAllowFailure(transaction.getSn(),"会员卡充值");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Transaction close(String id, String notes) {
        Transaction transaction = this.transactionDao.get(id);
        transaction.setStatus(TxStatus.close);
        transaction.setFlowStatus(-1);
        transaction.setStatusText(TxStatus.close.getValue());
        transaction.setNotes(notes);
        return transactionDao.update(transaction);
    }

    @Transactional
    public List<Transaction> find(Criterion... criterions) {
        return this.transactionDao.find(criterions);
    }

    @Transactional
    public void update(Transaction transaction) {
        this.transactionDao.update(transaction);
    }

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

}
