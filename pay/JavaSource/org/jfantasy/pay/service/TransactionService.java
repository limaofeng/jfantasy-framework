package org.jfantasy.pay.service;

import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.pay.bean.Project;
import org.jfantasy.pay.bean.Transaction;
import org.jfantasy.pay.bean.enums.ProjectType;
import org.jfantasy.pay.bean.enums.TxChannel;
import org.jfantasy.pay.bean.enums.TxStatus;
import org.jfantasy.pay.dao.TransactionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    private final TransactionDao transactionDao;

    @Autowired
    public TransactionService(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    /**
     * 第三方支付业务
     *
     * @param project 支付项目
     * @param from    付款方
     * @param to      收款方
     * @param amount  金额
     * @param notes   备注
     * @return Transaction
     */
    public Transaction thirdparty(Project project, String from, String to, BigDecimal amount, String notes, Map<String, Object> properties) {
        Transaction transaction = new Transaction();
        transaction.setFrom(from);
        transaction.setTo(to);
        transaction.setAmount(amount);
        transaction.setNotes(notes);
        transaction.setStatus(TxStatus.unprocessed);
        transaction.setChannel(TxChannel.thirdparty);
        transaction.setProject(project);
        transaction.setProperties(properties);
        //保存交易日志
        transaction = this.transactionDao.save(transaction);
        return transaction;
    }

    public Transaction transaction() {
        return null;
    }

    public Transaction get(String sn) {
        return transactionDao.get(sn);
    }

    public Pager<Transaction> findPager(Pager<Transaction> pager, List<PropertyFilter> filters) {
        return this.transactionDao.findPager(pager, filters);
    }

    /**
     * 保存交易接口
     *
     * @param transaction 交易对象
     *                    必填字段为: from 、to 、amount
     * @return Transaction
     */
    @Transactional
    public Transaction save(Transaction transaction) {
        Project project = transaction.getProject();
        String key = StringUtil.defaultValue(transaction.get(Transaction.UNION_KEY), transaction.get(Transaction.ORDER_KEY));
        String unionid = Transaction.generateUnionid(transaction.getProject().getKey(), key);
        Transaction src = this.transactionDao.findUnique(Restrictions.eq("unionId", unionid));
        if (src != null) {
            return src;
        }
        // 设置额外参数
        transaction.setUnionId(unionid);
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
}
