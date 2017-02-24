package org.jfantasy.trade.service;

import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.trade.bean.*;
import org.jfantasy.trade.bean.enums.*;
import org.jfantasy.trade.dao.ProjectDao;
import org.jfantasy.trade.dao.ReportDao;
import org.jfantasy.trade.dao.ReportUniqueDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ReportService {

    private final ReportDao reportDao;
    private final ProjectDao projectDao;
    private final ReportUniqueDao reportUniqueDao;

    @Autowired
    public ReportService(ReportDao reportDao, ReportUniqueDao reportUniqueDao, ProjectDao projectDao) {
        this.reportDao = reportDao;
        this.reportUniqueDao = reportUniqueDao;
        this.projectDao = projectDao;
    }

    @Transactional
    public Pager<Report> findPager(Pager<Report> pager, List<PropertyFilter> filters) {
        return this.reportDao.findPager(pager, filters);
    }

    private boolean exists(ReportUnique unique) {
        if (this.reportUniqueDao.exists(Restrictions.eq("key", unique.getKey()), Restrictions.eq("tag", unique.getTag()))) {
            return true;
        }
        this.reportUniqueDao.insert(unique);
        return false;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void analyze(Transaction transaction) {
        if (transaction.getStatus() == TxStatus.processing || transaction.getStatus() == TxStatus.close) {
            return;
        }

        Project project = this.projectDao.get(transaction.getProject());
        BigDecimal amount = transaction.getAmount();
        String code = transaction.getProject() + (StringUtil.isBlank(transaction.getSubject()) ? "" : ("-" + transaction.getSubject()));

        if (transaction.getStatus() == TxStatus.success) {
            String day = DateUtil.format(transaction.getTime(TxStatus.success), "yyyyMMdd");
            if (project.getType() == ProjectType.order || project.getType() == ProjectType.transfer) {//转账及订单支付与退款
                if (!this.exists(new ReportUnique(ReportTargetType.account,transaction.getFrom() ,transaction.getSn(), "out"))) {
                    this.analyze(ReportTargetType.account, transaction.getFrom(), TimeUnit.day, day, BillType.debit, code, amount);//记录出帐
                }
                if (!this.exists(new ReportUnique(ReportTargetType.account,transaction.getTo(), transaction.getSn(), "in"))) {
                    this.analyze(ReportTargetType.account, transaction.getTo(), TimeUnit.day, day, BillType.credit, code, amount);//记录入帐
                }
            } else if (project.getType() == ProjectType.deposit) {//充值
                if (!this.exists(new ReportUnique(ReportTargetType.account,transaction.getTo(), transaction.getSn(), "in"))) {
                    this.analyze(ReportTargetType.account, transaction.getTo(), TimeUnit.day, day, BillType.credit, code, amount);//记录入帐
                }
            }
        } else if (transaction.getStatus() == TxStatus.unprocessed && ProjectType.withdraw == project.getType()) {//提现
            if (!this.exists(new ReportUnique(ReportTargetType.account,transaction.getFrom(), transaction.getSn(), "out"))) {
                String day = DateUtil.format(transaction.getTime(TxStatus.unprocessed), "yyyyMMdd");
                this.analyze(ReportTargetType.account, transaction.getFrom(), TimeUnit.day, day, BillType.debit, code, amount);//记录出帐
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Report analyze(ReportTargetType targetType, String targetId, TimeUnit timeUnit, String time, BillType type, String code, BigDecimal value) {
        Report report = this.reportDao.findUnique(Restrictions.eq("targetType", targetType), Restrictions.eq("targetId", targetId), Restrictions.eq("timeUnit", timeUnit), Restrictions.eq("time", time));
        if (report == null) {
            report = new Report();
            report.setTargetType(targetType);
            report.setTargetId(targetId);
            report.setTimeUnit(timeUnit);
            report.setTime(time);
            report.addItem(new ReportItem(type, code, value));
        } else {
            ReportItem details = report.getItems(type, code);
            if (details == null) {
                report.addItem(new ReportItem(type, code, value));
            } else {
                details.addValue(value);
            }
        }
        try {
            return this.reportDao.save(report);
        } finally {
            if (timeUnit == TimeUnit.day) {
                analyze(targetType, targetId, TimeUnit.month, time.substring(0, 6), type, code, value);
            } else if (timeUnit == TimeUnit.month) {
                analyze(targetType, targetId, TimeUnit.year, time.substring(0, 4), type, code, value);
            } else if (timeUnit == TimeUnit.year) {
                analyze(targetType, targetId, TimeUnit.all, "longtime", type, code, value);
            }
        }
    }

    @Transactional
    public void clean(ReportTargetType targetType, String targetId) {
        this.reportDao.batchExecute("delete Report where targetType = ? and targetId = ?", targetType, targetId);
        this.reportUniqueDao.batchExecute("delete ReportUnique where targetType = ? and targetId = ?", targetType, targetId);
    }

}