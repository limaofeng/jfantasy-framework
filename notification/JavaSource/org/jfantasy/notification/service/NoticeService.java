package org.jfantasy.notification.service;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.error.IgnoreException;
import org.jfantasy.framework.spring.SpringContextUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.concurrent.LinkedQueue;
import org.jfantasy.framework.util.jackson.JSON;
import org.jfantasy.notification.bean.Model;
import org.jfantasy.notification.bean.Notice;
import org.jfantasy.notification.dao.ModelDao;
import org.jfantasy.notification.dao.NoticeDao;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提醒 service
 */

@Service
@Transactional
public class NoticeService {

    @Autowired
    private NoticeDao noticeDao;
    @Autowired
    private ModelDao modelDao;
    private LinkedQueue<Notice> noticeQueue = new LinkedQueue<Notice>();

    /**
     * 查看
     *
     * @param pager
     * @param filters
     * @return
     */
    public Pager<Notice> findPager(Pager<Notice> pager, List<PropertyFilter> filters) {
        return this.noticeDao.findPager(pager, filters);
    }


    /**
     * 保存
     *
     * @param notice
     */
    public void save(Notice notice){
        if (notice.getId() == null && StringUtil.isNotNull(notice.getReplaceMap()) && notice.getModel() != null && notice.getModel().getCode() != null) {
            Model m = modelDao.get(notice.getModel().getCode());
            if (m == null){
                throw new IgnoreException("无匹配model项");
            }
            Map<String, String> replaceMap = JSON.deserialize(notice.getReplaceMap(), new HashMap<String, String>().getClass());
            if (replaceMap != null) {
                String content = m.getContent();
                String url = m.getUrl();
                for (String s : replaceMap.keySet()) {
                    if (StringUtil.isNotNull(content) && !StringUtil.isNotNull(notice.getContent())){
                        content = content.replace("${" + s + "}", replaceMap.get(s));
                    }
                    if (StringUtil.isNotNull(url) && !StringUtil.isNotNull(notice.getUrl())){
                        url = url.replace("${" + s + "}", replaceMap.get(s));
                    }
                }
                if (!StringUtil.isNotNull(notice.getUrl())) {
                    notice.setUrl(url);
                }
                if (!StringUtil.isNotNull(notice.getContent())) {
                    notice.setContent(content);
                }

            }
        }
        boolean b = notice.getId() == null;
        notice = this.noticeDao.save(notice);
        if (b){
            try {
                noticeQueue.put(notice);
            } catch (InterruptedException e) {
                throw new IgnoreException(e.getMessage());
            }
        }
    }


    /**
     * 查看
     *
     * @param id
     * @return
     */
    public Notice get(Long id) {
        return this.noticeDao.get(id);
    }


    /**
     * 删除
     *
     * @param ids
     */
    public void delete(Long... ids) {
        for (Long id : ids) {
            this.noticeDao.delete(id);
        }

    }

    public static String findUserNotice() {
        NoticeService noticeService = SpringContextUtil.getBean("noticeService", NoticeService.class);
        List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
        filters.add(new PropertyFilter("EQB_isRead", "false"));
        Pager pager = new Pager<Notice>();
        pager.setOrderBy("modifyTime");
        pager.setOrders(Pager.Order.desc);
        return JSON.serialize(noticeService.findPager(pager, filters));
    }

    private static final Logger LOG = Logger.getLogger(NoticeService.class);

    public Notice getNotices() {
        try {
            return noticeQueue.take();
        } catch (InterruptedException e) {
            LOG.debug(e.getMessage(), e);
        }
        return null;
    }

}