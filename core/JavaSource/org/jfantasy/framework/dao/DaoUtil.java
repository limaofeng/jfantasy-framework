package org.jfantasy.framework.dao;

import org.apache.commons.collections.map.HashedMap;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
import org.jfantasy.framework.dao.hibernate.event.PropertyGeneratorPersistEventListener;
import org.jfantasy.framework.dao.hibernate.event.PropertyGeneratorSaveOrUpdatEventListener;
import org.jfantasy.framework.dao.hibernate.generator.SequenceGenerator;
import org.jfantasy.framework.dao.hibernate.generator.SerialNumberGenerator;
import org.jfantasy.framework.spring.SpringContextUtil;
import org.jfantasy.framework.util.common.ObjectUtil;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class DaoUtil {

    public DaoUtil() {
    }

    public static Connection getConnection(String dataSourceName) throws SQLException {
        DataSource dataSource = SpringContextUtil.getBean(dataSourceName);
        if (ObjectUtil.isNotNull(dataSource)) {
            return dataSource.getConnection();
        }
        throw new SQLException("名为:" + dataSourceName + ",的数据源没有找到!");
    }

    /**
     * 多数据分页
     *
     * @param <T>
     * @param pager
     * @param param
     * @param callBacks
     * @return Pager<T>
     */
    public static <T> Pager<T> findPager(Pager<T> pager, Map<String, Object> param, FindPagerCallBack<T>... callBacks) {
        pager = pager == null ? new Pager<>() : pager;
        int totalCount = 0;
        Map<Pager<T>, FindPagerCallBack<T>> pagers = new LinkedHashMap<>();
        // 计算总条数
        for (FindPagerCallBack<T> callBack : callBacks) {
            Pager<T> page = callBack.call(new Pager<>(1), param);
            totalCount += page.getTotalCount();
            pagers.put(page, callBack);
        }
        pager.reset(totalCount, new ArrayList<>());
        int first = pager.getFirst();
        int pageSize = pager.getPageSize();
        totalCount = 0;
        for (Map.Entry<Pager<T>, FindPagerCallBack<T>> entry : pagers.entrySet()) {
            totalCount += entry.getKey().getTotalCount();
            if (first < totalCount && entry.getKey().getTotalCount() > 0) {
                int cFirst = first - (totalCount - entry.getKey().getTotalCount()) + pager.getPageItems().size();
                entry.getKey().setFirst(cFirst);
                entry.getKey().setPageSize(pageSize - pager.getPageItems().size());
                Pager<T> page = entry.getValue().call(entry.getKey(), param);
                pager.getPageItems().addAll(page.getPageItems());
                if (pager.getPageItems().size() >= pageSize) {
                    break;
                }
            }
        }
        return pager;
    }

    /**
     * 多数据表分页接口
     *
     * @param <T>
     * @author 李茂峰
     * @version 1.0
     * @功能描述
     * @since 2012-10-31 下午09:01:21
     */
    public interface FindPagerCallBack<T> {

        /**
         * 回调
         *
         * @param pager 分页对象
         * @param param 查询参数
         * @return Pager<T>
         */
        Pager<T> call(Pager<T> pager, Map<String, Object> param);

    }
}
