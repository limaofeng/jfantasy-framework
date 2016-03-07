package org.jfantasy.system.service;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.schedule.service.ScheduleService;
import org.jfantasy.system.bean.Dict;
import org.jfantasy.system.bean.DictKey;
import org.jfantasy.system.bean.DictType;
import org.jfantasy.system.dao.DataDictionaryDao;
import org.jfantasy.system.dao.DataDictionaryTypeDao;
import org.jfantasy.system.job.DictJob;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.quartz.JobKey;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class DataDictionaryService implements InitializingBean {

    private static final Log LOGGER = LogFactory.getLog(DataDictionaryService.class);

    public static final JobKey jobKey = JobKey.jobKey("DataDictionary", "SYSTEM");

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private DataDictionaryTypeDao dataDictionaryTypeDao;

    @Autowired
    private DataDictionaryDao dataDictionaryDao;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!this.scheduleService.isStartTimerTisk()) {
            LOGGER.error(" scheduler 定时任务未启动！");
            return;
        }
        if (this.scheduleService.checkExists(jobKey)) {
            this.scheduleService.deleteJob(jobKey);
        }
        LOGGER.debug("添加用于生成 json 文件的 Job ");
        this.scheduleService.addJob(jobKey, DictJob.class);
    }

    public List<DictType> allTypes() {
        return dataDictionaryTypeDao.getAll();
    }

    public List<Dict> allDataDicts() {
        return dataDictionaryDao.find(new Criterion[0], "sort", "asc");
    }

    public Dict get(DictKey key) {
        return dataDictionaryDao.get(key);
    }

    public Dict get(String key) {
        String[] array = key.split(":");
        return dataDictionaryDao.get(DictKey.newInstance(array[1], array[0]));
    }

    /**
     * 查询配置项分类
     *
     * @param name     like查询
     * @param showsize 返回结果条数，默认15条
     * @return {list}
     */
    public List<DictType> types(String name, int showsize) {
        showsize = showsize == 0 ? 15 : showsize;
        if (StringUtil.isBlank(name)) {
            return dataDictionaryTypeDao.find(new Criterion[0], 0, showsize);
        } else {
            return dataDictionaryTypeDao.find(new Criterion[]{Restrictions.like("name", name)}, 0, showsize);
        }
    }

    public DictType getDataDictionaryType(String code) {
        return dataDictionaryTypeDao.findUniqueBy("code", code);
    }

    /**
     * 通过配置项分类及配置项CODE返回配置项
     *
     * @param type 类型
     * @param code 配置项CODE，返回的List顺序与codes的顺序一致
     * @return {DataDictionary}
     */
    public Dict getUnique(String type, String code) {
        return dataDictionaryDao.findUnique(Restrictions.eq("type", type), Restrictions.eq("code", code));
    }

    /**
     * 通过分类及上级编码查询配置项
     *
     * @param type       分类
     * @param parentCode 上级编码
     * @return {List}
     */
    public List<Dict> list(String type, String parentCode) {
        Criterion[] criterions = new Criterion[]{Restrictions.eq("type", type)};
        if (StringUtil.isNotBlank(parentCode)) {
            criterions = ObjectUtil.join(criterions, Restrictions.eq("parent.code", parentCode));
        }
        return dataDictionaryDao.find(criterions, "sort", "asc");
    }

    /**
     * 分页查询方法
     *
     * @param pager   分页对象
     * @param filters 过滤条件
     * @return {List}
     */
    public Pager<Dict> findPager(Pager<Dict> pager, List<PropertyFilter> filters) {
        return this.dataDictionaryDao.findPager(pager, filters);
    }

    public Pager<DictType> findDataDictionaryTypePager(Pager<DictType> pager, List<PropertyFilter> filters) {
        return this.dataDictionaryTypeDao.findPager(pager, filters);
    }

    /**
     * 添加及更新配置项
     *
     * @param dict 数据字典项
     */
    public Dict save(Dict dict) {
        return this.dataDictionaryDao.save(dict);
    }

    /**
     * 添加及更新配置项分类方法
     *
     * @param dictType 数据字典分类
     */
    public DictType save(DictType dictType) {
        List<DictType> types;
        boolean root = false;
        if (dictType.getParent() == null || StringUtil.isBlank(dictType.getParent().getCode())) {
            dictType.setLayer(0);
            dictType.setPath(dictType.getCode() + DictType.PATH_SEPARATOR);
            root = true;
            types = ObjectUtil.sort(dataDictionaryTypeDao.find(Restrictions.isNull("parent")), "sort", "asc");
        } else {
            DictType parentCategory = this.dataDictionaryTypeDao.get(dictType.getParent().getCode());
            dictType.setLayer(parentCategory.getLayer() + 1);
            dictType.setPath(parentCategory.getPath() + dictType.getCode() + DictType.PATH_SEPARATOR);// 设置path
            types = ObjectUtil.sort(dataDictionaryTypeDao.findBy("parent.code", parentCategory.getCode()), "sort", "asc");
        }
        DictType old = dictType.getCode() != null ? this.dataDictionaryTypeDao.get(dictType.getCode()) : null;
        if (old != null) {// 更新数据
            if (dictType.getSort() != null && (ObjectUtil.find(types, "code", old.getCode()) == null || !old.getSort().equals(dictType.getSort()))) {
                if (ObjectUtil.find(types, "code", old.getCode()) == null) {// 移动了节点的层级
                    int i = 0;
                    for (DictType m : ObjectUtil.sort((old.getParent() == null || StringUtil.isBlank(old.getParent().getCode())) ? dataDictionaryTypeDao.find(Restrictions.isNull("parent")) : dataDictionaryTypeDao.findBy("parent.code", old.getParent().getCode()), "sort", "asc")) {
                        m.setSort(i++);
                        this.dataDictionaryTypeDao.save(m);
                    }
                    types.add(dictType.getSort() - 1, dictType);
                } else {
                    DictType t = ObjectUtil.remove(types, "code", old.getCode());
                    if (types.size() >= dictType.getSort()) {
                        types.add(dictType.getSort() - 1, t);
                    } else {
                        types.add(t);
                    }
                }
                // 重新排序后更新新的位置
                for (int i = 0; i < types.size(); i++) {
                    DictType m = types.get(i);
                    if (m.getCode().equals(dictType.getCode())) {
                        continue;
                    }
                    m.setSort(i + 1);
                    this.dataDictionaryTypeDao.save(m);
                }
            }
        } else {// 新增数据
            dictType.setSort(types.size() + 1);
        }
        dictType = this.dataDictionaryTypeDao.save(dictType);
        if (root) {
            dictType.setParent(null);
            this.dataDictionaryTypeDao.update(dictType);
        }
        return dictType;
    }

    /**
     * 删除配置项
     *
     * @param keys 数据字段 key
     */
    public void delete(DictKey... keys) {
        for (DictKey key : keys) {
            Dict dd = this.get(key);
            if(dd == null){
                LOGGER.warn(" 数据字典项 key = " + key + " 不存在 , 请检查方法参数 !");
                continue;
            }
            this.dataDictionaryDao.delete(dd);
        }
    }

    public void delete(String... keys) {
        List<DictKey> dictKeys = new ArrayList<DictKey>();
        for (String key : keys) {
            dictKeys.add(new DictKey(key));
        }
        this.delete(dictKeys.toArray(new DictKey[dictKeys.size()]));
    }

    public void deleteType(String... codes) {
        for (String code : codes) {
            DictType dictType = this.dataDictionaryTypeDao.get(code);
            if (dictType == null) {
                continue;
            }
            for (Dict dict : dictType.getDataDictionaries()) {
                this.dataDictionaryDao.delete(dict.getKey());
            }
            this.dataDictionaryTypeDao.delete(dictType.getCode());
        }
    }

    public List<Dict> find(Criterion... criterions) {
        return this.dataDictionaryDao.find(criterions);
    }

}
