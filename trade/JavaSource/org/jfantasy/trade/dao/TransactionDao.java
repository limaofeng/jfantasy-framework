package org.jfantasy.trade.dao;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.trade.bean.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransactionDao extends HibernateDao<Transaction, String> {

    @Override
    protected Criterion[] buildPropertyFilterCriterions(List<PropertyFilter> filters) {
        Criterion[] criterions = new Criterion[0];

        PropertyFilter filter = ObjectUtil.remove(filters, "filterName", "EQS_account.sn");
        if (filter != null) {
            String accountSn = filter.getPropertyValue(String.class);
            criterions = ObjectUtil.join(criterions, Restrictions.sqlRestriction("{alias}.from_account = ? or {alias}.to_account = ?",new String[]{accountSn,accountSn},new Type[]{StringType.INSTANCE,StringType.INSTANCE}));
        }

        filter = ObjectUtil.remove(filters, "filterName", "EQS_from");
        if (filter != null) {
            String from = filter.getPropertyValue(String.class);
            criterions = ObjectUtil.join(criterions, Restrictions.eq("fromAccount.sn", from));
        }

        filter = ObjectUtil.remove(filters, "filterName", "EQS_to");
        if (filter != null) {
            String to = filter.getPropertyValue(String.class);
            criterions = ObjectUtil.join(criterions, Restrictions.eq("toAccount.sn", to));
        }

        return ObjectUtil.join(super.buildPropertyFilterCriterions(filters),criterions);
    }

}
