package org.jfantasy.logistics.service;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.logistics.bean.DeliveryType;
import org.jfantasy.logistics.bean.enums.DeliveryMethod;
import org.jfantasy.logistics.dao.DeliveryTypeDao;
import org.jfantasy.logistics.dao.LogisticsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class DeliveryTypeService {

    @Autowired
    private DeliveryTypeDao deliveryTypeDao;
    @Autowired
    private LogisticsDao shippingDao;

    public List<DeliveryType> find(List<PropertyFilter> filters) {
        return this.deliveryTypeDao.find(filters);
    }

    public DeliveryType get(Long id) {
        return deliveryTypeDao.get(id);
    }

    /**
     * 配送列表
     *
     * @param pager   分页对象
     * @param filters 过滤条件
     * @return Pager<DeliveryType>
     */
    public Pager<DeliveryType> findPager(Pager<DeliveryType> pager, List<PropertyFilter> filters) {
        return this.deliveryTypeDao.findPager(pager, filters);
    }

    /**
     * 配送保存
     *
     * @param deliveryType 配送对象
     * @return DeliveryType
     */
    public DeliveryType save(DeliveryType deliveryType) {
        if (deliveryType.getFirstWeight() == null) {
            deliveryType.setFirstWeight(0);
        }
        if (deliveryType.getFirstWeightPrice() == null) {
            deliveryType.setFirstWeightPrice(BigDecimal.ZERO);
        }
        if (deliveryType.getContinueWeight() == null) {
            deliveryType.setContinueWeight(0);
        }
        if (deliveryType.getContinueWeightPrice() == null) {
            deliveryType.setContinueWeightPrice(BigDecimal.ZERO);
        }
        return this.deliveryTypeDao.save(deliveryType);
    }

    /**
     * 配送删除
     *
     * @param ids 配送类型
     */
    public void delete(Long... ids) {
        for (Long id : ids) {
            this.deliveryTypeDao.delete(id);
        }
    }

    public List<DeliveryType> listDeliveryType(DeliveryMethod method) {
        return this.deliveryTypeDao.find(Restrictions.eq("method", method));
    }

    public DeliveryType findUnique(Criterion... criterions) {
        return this.deliveryTypeDao.findUnique(criterions);
    }

}
