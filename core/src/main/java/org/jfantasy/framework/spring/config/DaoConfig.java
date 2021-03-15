package org.jfantasy.framework.spring.config;

import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
import org.jfantasy.framework.dao.hibernate.InterceptorRegistration;
import org.jfantasy.framework.dao.hibernate.event.PropertyGeneratorPersistEventListener;
import org.jfantasy.framework.dao.hibernate.event.PropertyGeneratorSaveOrUpdatEventListener;
import org.jfantasy.framework.dao.hibernate.generator.SequenceGenerator;
import org.jfantasy.framework.dao.hibernate.generator.SerialNumberGenerator;
import org.jfantasy.framework.dao.hibernate.interceptors.BusEntityInterceptor;
import org.jfantasy.framework.dao.jpa.ComplexJpaRepository;
import org.jfantasy.framework.spring.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * Description: <数据源相关bean的注册>. <br>
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories(
    transactionManagerRef = "transactionManager",
    includeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            value = {JpaRepository.class}
        )
    },
    basePackages = "org.jfantasy.*.dao",
    repositoryBaseClass = ComplexJpaRepository.class
)
@Import({MyBatisConfig.class})
public class DaoConfig {

    @Autowired(required = false)
    private EntityManagerFactory entityManagerFactory;

    @PostConstruct
    private void init() {
        EventListenerRegistry registry = this.eventListenerRegistry(entityManagerFactory);
        SessionFactoryImplementor sessionFactory = entityManagerFactory.unwrap(SessionFactoryImplementor.class);
        MutableIdentifierGeneratorFactory identifierGeneratorFactory = sessionFactory.getServiceRegistry().getService(MutableIdentifierGeneratorFactory.class);
        // 自定义序列生成器
        identifierGeneratorFactory.register("fantasy-sequence", SequenceGenerator.class);
        identifierGeneratorFactory.register("serialnumber", SerialNumberGenerator.class);
        // 默认监听器
        registry.prependListeners(EventType.SAVE_UPDATE, createListenerInstance(new PropertyGeneratorSaveOrUpdatEventListener(identifierGeneratorFactory)));
        registry.prependListeners(EventType.PERSIST, createListenerInstance(new PropertyGeneratorPersistEventListener(identifierGeneratorFactory)));
    }

    /**
     * 返回 EventListenerRegistry 对象
     *
     * @return EventListenerRegistry
     */
    @Bean
    public EventListenerRegistry eventListenerRegistry(EntityManagerFactory entityManagerFactory) {
        SessionFactoryImplementor sessionFactory = entityManagerFactory.unwrap(SessionFactoryImplementor.class);
        return sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
    }

    private static <T> T createListenerInstance(T bean) {
        SpringContextUtil.getApplicationContext().getAutowireCapableBeanFactory().autowireBean(bean);
        return bean;
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager jpaTransactionManager() {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name = "dataSourceTransactionManager")
    public PlatformTransactionManager dataSourceTransactionManager(DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);
        return dataSourceTransactionManager;
    }

}