package org.jfantasy.logistics.service;

import org.hibernate.criterion.Criterion;
import org.jfantasy.logistics.bean.Express;
import org.jfantasy.logistics.dao.ExpressDao;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExpressService {

    @Autowired
    private ExpressDao expressDao;

    /**
     * 列表查询
     *
     * @param filters 过滤条件
     * @return List<DeliveryCorp>
     */
    public List<Express> find(List<PropertyFilter> filters) {
        return this.expressDao.find(filters);
    }

    /**
     * 保存
     */
    public Express save(Express deliveryCorp) {
        return this.expressDao.save(deliveryCorp);
    }

    /**
     * 根据主键获取id
     *
     * @param id 物流公司id
     * @return DeliveryCorp
     */
    public Express get(String id) {
        return this.expressDao.get(id);
    }

    /**
     * 批量删除
     *
     * @param ids 物流公司 ids
     */
    public void delete(String... ids) {
        for (String id : ids) {
            this.expressDao.delete(id);
        }
    }

    public Pager<Express> findPager(Pager<Express> pager, List<PropertyFilter> filters) {
        return this.expressDao.findPager(pager, filters);
    }

    public Express findUnique(Criterion... criterions) {
        return this.expressDao.findUnique(criterions);
    }

}
