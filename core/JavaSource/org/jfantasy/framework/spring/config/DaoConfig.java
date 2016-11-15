package org.jfantasy.framework.spring.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
import org.jfantasy.framework.dao.annotations.EventListener;
import org.jfantasy.framework.dao.hibernate.event.PropertyGeneratorPersistEventListener;
import org.jfantasy.framework.dao.hibernate.event.PropertyGeneratorSaveOrUpdatEventListener;
import org.jfantasy.framework.util.common.ClassUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Map;

/**
 * Description: <数据源相关bean的注册>. <br>
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories(transactionManagerRef = "jpaTransactionManager", includeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {JpaRepository.class})}, basePackages = "org.jfantasy.*.dao")
@Import({DataSourceConfig.class,MyBatisConfig.class})
public class DaoConfig {

    private static final Log LOG = LogFactory.getLog(DaoConfig.class);

    private final EntityManagerFactory entityManagerFactory;
    private final ApplicationContext applicationContext;

    @Autowired(required = false)
    public DaoConfig(EntityManagerFactory entityManagerFactory, ApplicationContext applicationContext) {
        this.entityManagerFactory = entityManagerFactory;
        this.applicationContext = applicationContext;
    }

    private <T> T createListenerInstance(T bean) {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(bean);
        return bean;
    }

    @SuppressWarnings("unchecked")
    @Bean(name = "sessionFactory")
    public SessionFactory sessionFactory() {
        SessionFactoryImplementor sessionFactory = entityManagerFactory.unwrap(SessionFactoryImplementor.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);

        MutableIdentifierGeneratorFactory identifierGeneratorFactory = sessionFactory.getServiceRegistry().getService(MutableIdentifierGeneratorFactory.class);
        // 默认监听器
        registry.prependListeners(EventType.SAVE_UPDATE, createListenerInstance(new PropertyGeneratorSaveOrUpdatEventListener(identifierGeneratorFactory)));
        registry.prependListeners(EventType.PERSIST, createListenerInstance(new PropertyGeneratorPersistEventListener(identifierGeneratorFactory)));
        //通过注解添加监听
        for (Map.Entry<String, Object> entry : this.applicationContext.getBeansWithAnnotation(EventListener.class).entrySet()) {
            for(String eventTypeName : ClassUtil.getAnnotation(ClassUtil.getRealClass(entry.getValue()),EventListener.class).value()){
                registry.appendListeners(EventType.resolveEventTypeByName(eventTypeName),entry.getValue());
            }
        }
        LOG.debug(" SessionFactory 加载成功! ");
        return sessionFactory;
    }

    @Bean(name = "dataSourceTransactionManager")
    public PlatformTransactionManager dataSourceTransactionManager(DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);
        return dataSourceTransactionManager;
    }

    @Primary
    @Bean(name = "hibernateTransactionManager")
    public PlatformTransactionManager hibernateTransactionManager() {
        HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager();
        hibernateTransactionManager.setSessionFactory(sessionFactory());
        return hibernateTransactionManager;
    }

    @Bean(name = "jpaTransactionManager")
    public PlatformTransactionManager jpaTransactionManager() {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
