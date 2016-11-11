package org.jfantasy.pay.service;

import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.pay.bean.Report;
import org.jfantasy.pay.bean.ReportItem;
import org.jfantasy.pay.bean.enums.BillType;
import org.jfantasy.pay.bean.enums.ReportTargetType;
import org.jfantasy.pay.bean.enums.TimeUnit;
import org.jfantasy.pay.dao.ReportDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ReportService {

    private final ReportDao reportDao;

    @Autowired
    public ReportService(ReportDao reportDao) {
        this.reportDao = reportDao;
    }

    @Transactional
    public Pager<Report> findPager(Pager<Report> pager, List<PropertyFilter> filters) {
        return this.reportDao.findPager(pager, filters);
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
        try{
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

}