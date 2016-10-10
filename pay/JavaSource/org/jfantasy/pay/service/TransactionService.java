package org.jfantasy.pay.service;

import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.mvc.error.RestException;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.pay.bean.Project;
import org.jfantasy.pay.bean.Transaction;
import org.jfantasy.pay.bean.enums.ProjectType;
import org.jfantasy.pay.bean.enums.TxChannel;
import org.jfantasy.pay.bean.enums.TxStatus;
import org.jfantasy.pay.dao.ProjectDao;
import org.jfantasy.pay.dao.TransactionDao;
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
    private AccountService accountService;

    @Autowired
    public TransactionService(TransactionDao transactionDao, ProjectDao projectDao) {
        this.transactionDao = transactionDao;
        this.projectDao = projectDao;
    }

    public Transaction get(String sn) {
        return transactionDao.get(sn);
    }

    public Transaction getByUnionId(String unionId) {
        return this.transactionDao.findUnique();
    }

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
    public Transaction payment(String from, BigDecimal amount, String notes, Map<String, String> properties) {
        String to = accountService.platform().getSn();// 平台收款
        return this.save(Project.ORDER_PAYMENT, from, to, amount, notes, properties);
    }

    /**
     * 退款
     *
     * @param orderKey 交易订单号
     * @param amount   退款金额
     * @param notes    备注
     * @return Transaction
     */
    public Transaction refund(String orderKey, BigDecimal amount, String notes) {
        Transaction original = this.getByUnionId(Transaction.generateUnionid(OrderTransaction.Type.refund.getValue(), orderKey));
        if (original == null || original.getStatus() != TxStatus.success) {
            throw new RestException("原交易不存在或者未支付成功");
        }
        return this.save(Project.ORDER_REFUND, original.getTo(), original.getFrom(), amount, notes, original.getProperties());
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
    public Transaction save(String projectKey, String from, String to, BigDecimal amount, String notes, Map<String, String> properties) {
        Project project = projectDao.get(projectKey);
        // 生成 unionid
        String key = StringUtil.defaultValue(properties.get(Transaction.UNION_KEY), properties.get(Transaction.ORDER_KEY));
        String unionid = Transaction.generateUnionid(project.getKey(), key);
        // 判断交易是否已经存在
        Transaction src = this.transactionDao.findUnique(Restrictions.eq("unionId", unionid));
        if (src != null) {
            return src;
        }
        // 创建交易
        Transaction transaction = new Transaction();
        transaction.setUnionId(unionid);
        transaction.setProject(project);
        transaction.setFrom(from);
        transaction.setTo(to);
        transaction.setAmount(amount);
        transaction.setNotes(notes);
        transaction.setProperties(properties);
        switch (project.getType()) {
            case order:
                transaction.set("stage", Transaction.STAGE_PAYMENT);
                break;
            case transfer://如果为转账交易，默认为内部交易
                transaction.setChannel(TxChannel.internal);
                break;
            default:
        }
        transaction.setStatus(TxStatus.unprocessed);
        transaction.setStatusText(transaction.getProject().getType() == ProjectType.order ? "等待付款" : "待处理");
        return this.transactionDao.save(transaction);
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
