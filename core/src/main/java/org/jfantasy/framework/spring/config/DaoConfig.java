package org.jfantasy.framework.spring.config;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.jfantasy.framework.dao.hibernate.event.PropertyGeneratorPersistEventListener;
import org.jfantasy.framework.dao.hibernate.event.PropertyGeneratorSaveOrUpdateEventListener;
import org.jfantasy.framework.dao.jpa.ComplexJpaRepository;
import org.jfantasy.framework.spring.SpringBeanUtils;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 数据源相关bean的注册
 *
 * @author limaofeng
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@EntityScan("org.jfantasy.framework.context.bean")
@EnableJpaRepositories(
    includeFilters = {
      @ComponentScan.Filter(
          type = FilterType.ASSIGNABLE_TYPE,
          value = {JpaRepository.class})
    },
    basePackages = {"org.jfantasy.framework.context.dao"},
    repositoryBaseClass = ComplexJpaRepository.class)
@Import({MyBatisConfig.class})
public class DaoConfig {

  private final EntityManagerFactory entityManagerFactory;

  public DaoConfig(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  @PostConstruct
  private void init() {
    SessionFactoryImplementor sessionFactory =
        entityManagerFactory.unwrap(SessionFactoryImplementor.class);
    EventListenerRegistry registry =
        sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
    // TODO MutableIdentifierGeneratorFactory 被移除，如何解决
    //    MutableIdentifierGeneratorFactory identifierGeneratorFactory =
    //        sessionFactory.getServiceRegistry().getService(IdentifierGeneratorFactory.class);
    // 自定义序列生成器
    //    identifierGeneratorFactory.register("fantasy-sequence", SequenceGenerator.class);
    //    identifierGeneratorFactory.register("serialnumber", SerialNumberGenerator.class);
    // 默认监听器
    registry.prependListeners(
        EventType.SAVE_UPDATE,
        createListenerInstance(new PropertyGeneratorSaveOrUpdateEventListener(null)));
    registry.prependListeners(
        EventType.PERSIST, createListenerInstance(new PropertyGeneratorPersistEventListener(null)));
  }

  /**
   * 返回 EventListenerRegistry 对象
   *
   * @return EventListenerRegistry
   */
  @Bean
  public EventListenerRegistry eventListenerRegistry() {
    SessionFactoryImplementor sessionFactory =
        this.entityManagerFactory.unwrap(SessionFactoryImplementor.class);
    return sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
  }

  private static <T> T createListenerInstance(T bean) {
    SpringBeanUtils.getApplicationContext().getAutowireCapableBeanFactory().autowireBean(bean);
    return bean;
  }

  @Primary
  @Bean(name = "transactionManager")
  public PlatformTransactionManager jpaTransactionManager() {
    return new JpaTransactionManager(entityManagerFactory);
  }
}
