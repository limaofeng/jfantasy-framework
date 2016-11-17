package org.jfantasy.pay.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.pay.bean.OrderType;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.Date;

@Repository
public class OrderTypeDao extends HibernateDao<OrderType,String>{

    public boolean isExpired(String id, Date orderTime) {
        OrderType orderType = this.get(id);
        return !(orderType == null || orderTime == null) && DateUtil.interval(DateUtil.now(), orderTime, Calendar.MINUTE) >= orderType.getExpires();
    }

}
