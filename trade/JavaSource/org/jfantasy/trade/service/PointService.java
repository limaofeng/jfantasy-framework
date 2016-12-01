package org.jfantasy.trade.service;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.trade.bean.Point;
import org.jfantasy.trade.bean.enums.PointStatus;
import org.jfantasy.trade.dao.PointDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointService {

    @Autowired
    private PointDao pointDao;

    public PointDao getPointDao() {
        return pointDao;
    }

    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }

    public Pager<Point> findPager(Pager<Point> pager, List<PropertyFilter> filters) {
        return pointDao.findPager(pager,filters);
    }

    public Point update(String sn, PointStatus status, String remark) {
        return null;
    }

    public Point save(Point point) {
        return null;
    }
}
