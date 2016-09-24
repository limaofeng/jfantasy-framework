package org.jfantasy.common.service;

import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;
import org.jfantasy.common.bean.FtpConfig;
import org.jfantasy.framework.service.FTPService;
import org.jfantasy.framework.spring.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * FtpServiceFactory
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-12-5 下午4:57:11
 */
@Service
@Transactional
@Lazy(false)
public class FtpServiceFactory {

    @Autowired
    private FtpConfigService ftpConfigService;

    private static final ConcurrentMap<Long, FTPService> FTP_SERVICE_CACHE = new ConcurrentHashMap<>();

    public static String getFtpServiceBeanId(Long configId) {
        return "ftpService-" + configId;
    }

    private FTPService initialize(FtpConfig config) {
        Map<String, Object> propertyValues = new HashMap<>();
        propertyValues.put("hostname", config.getHostname());
        propertyValues.put("port", config.getPort());
        propertyValues.put("username", config.getUsername());
        propertyValues.put("password", config.getPassword());
        propertyValues.put("controlEncoding", config.getControlEncoding());
        SpringContextUtil.registerBeanDefinition(getFtpServiceBeanId(config.getId()), FTPService.class, propertyValues);
        return SpringContextUtil.getBean(getFtpServiceBeanId(config.getId()), FTPService.class);
    }

    /**
     * 更新FtpService连接信息
     *
     * @param config
     * @功能描述
     */
    public void updateFtpService(FtpConfig config) {
        FTPService ftpService = getFtpService(config.getId());
        if (ftpService != null) {
            ftpService.setHostname(config.getHostname());
            ftpService.setPort(config.getPort());
            ftpService.setUsername(config.getUsername());
            ftpService.setPassword(config.getPassword());
            ftpService.setControlEncoding(config.getControlEncoding());
        }
    }

    /**
     * 删除一个指定的FtpService
     *
     * @param config
     * @功能描述
     */
    public void removeFtpService(FtpConfig config) {
        if (FTP_SERVICE_CACHE.containsKey(config.getId())) {
            SpringContextUtil.removeBeanDefinition(getFtpServiceBeanId(config.getId()));
            FTP_SERVICE_CACHE.remove(config.getId());
        }
    }

    /**
     * 通过Id获取FtpService对象
     *
     * @param id
     * @return
     * @功能描述
     */
    public FTPService getFtpService(Long id) {
        if (!FTP_SERVICE_CACHE.containsKey(id)) {
            FTP_SERVICE_CACHE.put(id, this.initialize(ftpConfigService.get(id)));
        }
        return FTP_SERVICE_CACHE.get(id);
    }

    public static class FtpConfigEventListener implements PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener {

        private static final long serialVersionUID = -8410630987365469163L;

        @Autowired
        private transient FtpServiceFactory ftpServiceFactory;

        @Override
        public boolean requiresPostCommitHanding(EntityPersister persister) {
            return false;
        }

        @Override
        public void onPostInsert(PostInsertEvent event) {
            if (event.getEntity() instanceof FtpConfig) {
                ftpServiceFactory.initialize((FtpConfig) event.getEntity());
            }
        }

        @Override
        public void onPostUpdate(PostUpdateEvent event) {
            if (event.getEntity() instanceof FtpConfig) {
                ftpServiceFactory.updateFtpService((FtpConfig) event.getEntity());
            }
        }

        @Override
        public void onPostDelete(PostDeleteEvent event) {
            if (event.getEntity() instanceof FtpConfig) {
                ftpServiceFactory.removeFtpService((FtpConfig) event.getEntity());
            }
        }
    }

}
