package org.jfantasy.framework.spring.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
import org.jfantasy.framework.dao.hibernate.event.PropertyGeneratorPersistEventListener;
import org.jfantasy.framework.dao.hibernate.event.PropertyGeneratorSaveOrUpdatEventListener;
import org.jfantasy.framework.dao.hibernate.generator.SequenceGenerator;
import org.jfantasy.framework.dao.hibernate.generator.SerialNumberGenerator;
import org.jfantasy.framework.dao.jpa.ComplexJpaRepository;
import org.jfantasy.framework.spring.SpringContextUtil;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;

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

    private static SessionFactory sessionFactory;

    /**
     * 返回 SessionFactory 对象，因为 HibernateDao 依赖问题。
     *
     * @return SessionFactory
     */
    public synchronized static SessionFactory getSessionFactory(EntityManagerFactory entityManagerFactory) {
        if (DaoConfig.sessionFactory != null) {
            return DaoConfig.sessionFactory;
        }
        SessionFactoryImplementor sessionFactory = entityManagerFactory.unwrap(SessionFactoryImplementor.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
        MutableIdentifierGeneratorFactory identifierGeneratorFactory = sessionFactory.getServiceRegistry().getService(MutableIdentifierGeneratorFactory.class);
        // 自定义序列生成器
        identifierGeneratorFactory.register("fantasy-sequence", SequenceGenerator.class);
        identifierGeneratorFactory.register("serialnumber", SerialNumberGenerator.class);
        // 默认监听器
        registry.prependListeners(EventType.SAVE_UPDATE, createListenerInstance(new PropertyGeneratorSaveOrUpdatEventListener(identifierGeneratorFactory)));
        registry.prependListeners(EventType.PERSIST, createListenerInstance(new PropertyGeneratorPersistEventListener(identifierGeneratorFactory)));
        DaoConfig.sessionFactory = sessionFactory;
        return sessionFactory;
    }

    private static <T> T createListenerInstance(T bean) {
        SpringContextUtil.getApplicationContext().getAutowireCapableBeanFactory().autowireBean(bean);
        return bean;
    }


}
