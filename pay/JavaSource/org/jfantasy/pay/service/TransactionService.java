package org.jfantasy.pay.service;

import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.mvc.error.RestException;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.pay.bean.Account;
import org.jfantasy.pay.bean.Card;
import org.jfantasy.pay.bean.Project;
import org.jfantasy.pay.bean.Transaction;
import org.jfantasy.pay.bean.enums.AccountType;
import org.jfantasy.pay.bean.enums.TxChannel;
import org.jfantasy.pay.bean.enums.TxStatus;
import org.jfantasy.pay.dao.AccountDao;
import org.jfantasy.pay.dao.ProjectDao;
import org.jfantasy.pay.dao.TransactionDao;
import org.jfantasy.pay.order.entity.OrderKey;
import org.jfantasy.pay.rest.models.OrderTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    private final ProjectDao projectDao;
    private final TransactionDao transactionDao;
    private final AccountDao accountDao;

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
    private Account platform() {
        return this.accountDao.findUnique(Restrictions.eq("type", AccountType.platform));
    }

    public Transaction get(String sn) {
        return transactionDao.get(sn);
    }

    private Transaction getByUnionId(String unionId) {
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
     * @param orderKey 交易订单号
     * @param amount   退款金额
     * @param notes    备注
     * @return Transaction
     */
    @Transactional
    public Transaction refund(String orderKey, BigDecimal amount, String notes) {
        Transaction original = this.getByUnionId(Transaction.generateUnionid(OrderTransaction.Type.payment.getValue(), orderKey));
        if (original == null || original.getStatus() != TxStatus.success) {
            throw new RestException("原交易不存在或者未支付成功");
        }
        return this.save(Project.REFUND, original.getTo(), original.getFrom(), amount, notes, original.getProperties());
    }

    @Transactional
    public Transaction save(String projectKey, String from, String to, BigDecimal amount, String notes, Map<String, Object> properties) {
        return this.save(projectKey, from, to, null, amount, notes, properties);
    }

    @Transactional
    public Transaction getByUniqueId(String unionid) {
        return this.transactionDao.findUnique(Restrictions.eq("unionId", unionid));
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
    @Transactional
    public Transaction save(String projectKey, String from, String to, TxChannel channel, BigDecimal amount, String notes, Map<String, Object> properties) {
        Project project = projectDao.get(projectKey);
        // 生成 unionid
        String orderKey = (String) properties.get(Transaction.ORDER_KEY);
        String key = StringUtil.defaultValue(properties.remove(Transaction.UNION_KEY), orderKey);
        String unionid = Transaction.generateUnionid(project.getKey(), key);
        // 判断交易是否已经存在
        Transaction src = this.transactionDao.findUnique(Restrictions.eq("unionId", unionid));
        if (src != null) {
            return src;
        }
        if (StringUtil.isBlank(from) && StringUtil.isBlank(to)) {
            throw new ValidationException(4001, "交易账户全部为NULL，输入数据不合法");
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
        switch (project.getType()) {
            case order:
                transaction.set("stage", Transaction.STAGE_PAYMENT);
                transaction.setSubject(OrderKey.newInstance(orderKey).getType());
                break;
            case transfer:
                transaction.setChannel(ObjectUtil.defaultValue(channel, TxChannel.internal));
                if (StringUtil.isNotBlank(orderKey)) {
                    transaction.setSubject(OrderKey.newInstance(orderKey).getType());
                }
                break;
            default:
        }
        transaction.setStatus(TxStatus.unprocessed);
        transaction.setStatusText(projectKey.equals(Project.PAYMENT) ? "等待付款" : "待处理");
        return this.transactionDao.save(transaction);
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
        transaction.setChannel(TxChannel.internal);
        transaction.setTo(to.getSn());
        transaction.set(Transaction.CARD_ID, card.getNo());
        transaction.setProject(Project.INPOUR);
        transaction.setSubject(Card.SUBJECT_BY_CARD_INPOUR);
        transaction.setUnionId(Transaction.generateUnionid(transaction.getProject(), card.getNo()));
        transaction.setStatus(TxStatus.unprocessed);
        transaction.setStatusText(TxStatus.unprocessed.name());
        transaction.setNotes("会员卡充值");
        transaction.setPayConfigName("会员卡充值");
        this.transactionDao.save(transaction);
    }

    @Transactional
    public void update(Transaction transaction) {
        this.transactionDao.update(transaction);
    }

}
