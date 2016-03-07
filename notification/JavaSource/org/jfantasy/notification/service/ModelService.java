package org.jfantasy.notification.service;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.notification.bean.Model;
import org.jfantasy.notification.dao.ModelDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

/**
 * 消息模版 service
 */

@Service
@Transactional
public class ModelService {

    @Autowired
    private ModelDao modelDao;

    /**
     * 查看
     * @param pager
     * @param filters
     * @return
     */
    public Pager<Model> findPager(Pager<Model> pager,List<PropertyFilter> filters){
       return this.modelDao.findPager(pager,filters);
    }

    public List<Model> findAll(){
        return this.modelDao.find();
    }

    /**
     * 保存
     * @param notice
     */
    public Model save(Model notice){
        return this.modelDao.save(notice);
    }


    /**
     * 查看
     * @param id
     * @return
     */
    public Model get(String id){
         return this.modelDao.get(id);
    }


    /**
     * 删除
     * @param ids
     */
    public void delete(String... ids){
        for(String id:ids){
            this.modelDao.delete(id);
        }

    }

}
