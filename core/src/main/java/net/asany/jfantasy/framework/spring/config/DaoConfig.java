/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.spring.config;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import net.asany.jfantasy.framework.dao.hibernate.event.PropertyGeneratorPersistEventListener;
import net.asany.jfantasy.framework.dao.hibernate.event.PropertyGeneratorSaveOrUpdateEventListener;
import net.asany.jfantasy.framework.dao.jpa.SimpleAnyJpaRepository;
import net.asany.jfantasy.framework.spring.SpringBeanUtils;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
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
@EntityScan("net.asany.jfantasy.framework.context.bean")
@EnableJpaRepositories(
    includeFilters = {
      @ComponentScan.Filter(
          type = FilterType.ASSIGNABLE_TYPE,
          value = {JpaRepository.class})
    },
    basePackages = {"net.asany.jfantasy.framework.context.dao"},
    repositoryBaseClass = SimpleAnyJpaRepository.class)
@ComponentScan({
  "net.asany.jfantasy.framework.context.service",
  "net.asany.jfantasy.framework.context.dao"
})
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
