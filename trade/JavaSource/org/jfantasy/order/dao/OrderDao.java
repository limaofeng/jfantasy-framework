package org.jfantasy.order.dao;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.order.bean.Order;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderDao extends HibernateDao<Order, String> {

    @Override
    protected Criterion[] buildPropertyFilterCriterions(List<PropertyFilter> filters) {
        List<Criterion> criterions = new ArrayList<>();
        PropertyFilter payee = ObjectUtil.remove(filters, "propertyName", "payee");
        if (payee != null) {
            switch (payee.getMatchType()) {
                case EQ:
                    String value = payee.getPropertyValue(String.class);
                    criterions.add(Restrictions.sqlRestriction(" {alias}.sn in (select order_id from order_payee_value pv where pv.target = ? )", value, StringType.INSTANCE));
                    break;
                case IN:
                    criterions.add(getCriterionByPayee(payee,true));
                    break;
                case NOTIN:
                    criterions.add(getCriterionByPayee(payee,false));
                    break;
                default:
            }
        }
        return ObjectUtil.join(super.buildPropertyFilterCriterions(filters), criterions.toArray(new Criterion[criterions.size()]));
    }

    private Criterion getCriterionByPayee(PropertyFilter payee, boolean include) {
        List<String> values = new ArrayList<>();
        List<Type> valueTypes = new ArrayList<>();
        StringBuilder insql = new StringBuilder();
        for (String pvalue : payee.getPropertyValue(String[].class)) {
            insql.append("?,");
            values.add(pvalue);
            valueTypes.add(StringType.INSTANCE);
        }
        if (!values.isEmpty()) {
            insql.deleteCharAt(insql.length() - 1);
        }
        return Restrictions.sqlRestriction(String.format(" {alias}.sn %s (select order_id from order_payee_value pv where pv.target in (%s) )", include ? "in" : "not in", insql.toString()), values.toArray(new String[values.size()]), valueTypes.toArray(new Type[valueTypes.size()]));
    }

}
