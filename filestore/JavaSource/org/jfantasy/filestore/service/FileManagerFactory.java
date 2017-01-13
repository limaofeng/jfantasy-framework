package org.jfantasy.filestore.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.filestore.FileManager;
import org.jfantasy.filestore.FileManagerBuilder;
import org.jfantasy.filestore.bean.ConfigParam;
import org.jfantasy.filestore.bean.FileManagerConfig;
import org.jfantasy.filestore.bean.enums.FileManagerType;
import org.jfantasy.filestore.builders.LocalFileManagerBuilder;
import org.jfantasy.filestore.builders.OSSFileManagerBuilder;
import org.jfantasy.framework.spring.SpringContextUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * FileManager 管理类
 * 1.从数据库。初始化FileManager类
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-7-12 下午03:57:31
 */
@Transactional
public class FileManagerFactory implements InitializingBean, ApplicationListener<ContextRefreshedEvent> {

    private static final Log LOG = LogFactory.getLog(FileManagerFactory.class);

    private static FileManagerFactory fileManagerFactory;

    private FileManagerService fileManagerService;

    private Map<FileManagerType, FileManagerBuilder> builders = new EnumMap<>(FileManagerType.class);

    private static final ConcurrentMap<String, FileManager> fileManagerCache = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        this.addBuilder(FileManagerType.local, new LocalFileManagerBuilder());
        this.addBuilder(FileManagerType.oss, new OSSFileManagerBuilder());
    }

    @Async
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (fileManagerCache.isEmpty()) {
            this.load();
        }
    }

    private void load() {
        long start = System.currentTimeMillis();
        // 初始化文件管理器
        for (FileManagerConfig config : fileManagerService.getAll()) {
            try {
                FileManagerFactory.this.register(config.getId(), config.getType(), config.getConfigParams());
            } catch (Exception ex) {
                LOG.error("注册 FileManager id = [" + config.getId() + "] 失败!", ex);
            }
        }
        LOG.error("\n初始化 FileManagerFactory 耗时:" + (System.currentTimeMillis() - start) + "ms");
    }

    public void register(String id, FileManagerType type, final List<ConfigParam> configParams) {
        if (!builders.containsKey(type)) {
            LOG.error(" 未找到 [" + type + "] 对应的构建程序!请参考 FileManagerBuilder 实现,并添加到 FileManagerFactory 的配置中");
            return;
        }
        Map<String, String> params = new HashMap<>();
        for (ConfigParam configParam : configParams) {
            params.put(configParam.getName(), configParam.getValue());
        }
        fileManagerCache.put(id, builders.get(type).register(params));
    }

    /**
     * 获取文件管理的单例对象
     *
     * @return FileManagerFactory
     */
    public static synchronized FileManagerFactory getInstance() {
        if (fileManagerFactory == null) {
            fileManagerFactory = SpringContextUtil.getBeanByType(FileManagerFactory.class);
        }
        return fileManagerFactory;
    }

    /**
     * 根据id获取对应的文件管理器
     *
     * @param id Id
     * @return FileManager
     */
    public FileManager getFileManager(String id) {
        if (fileManagerCache.isEmpty()) {
            this.load();
        }
        return fileManagerCache.get(id);
    }

    public void remove(FileManagerConfig config) {
        fileManagerCache.remove(config.getId());
    }

    public void addBuilder(FileManagerType type, FileManagerBuilder builder) {
        this.builders.put(type, builder);
    }

    @Autowired
    public void setFileManagerService(FileManagerService fileManagerService) {
        this.fileManagerService = fileManagerService;
    }

}
