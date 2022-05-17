package org.jfantasy.autoconfigure;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.jfantasy.autoconfigure.properties.CuckooProperties;
import org.jfantasy.autoconfigure.properties.ElasticsearchClientProperties;
import org.jfantasy.framework.search.CuckooIndexFactory;
import org.jfantasy.framework.search.dao.EntityChangedEventListener;
import org.jfantasy.framework.util.regexp.RegexpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.SchedulingTaskExecutor;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;

@AutoConfigureAfter(JpaRepositoriesAutoConfiguration.class)
@Configuration
@EnableConfigurationProperties({CuckooProperties.class, ElasticsearchClientProperties.class})
public class SearchAutoConfiguration {

  private final EntityManagerFactory entityManagerFactory;

  public SearchAutoConfiguration(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  @PostConstruct
  private void init() {
    SessionFactoryImplementor sessionFactory =
        this.entityManagerFactory.unwrap(SessionFactoryImplementor.class);
    EventListenerRegistry registry =
        sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);

    EntityChangedEventListener entityChangedEventListener = entityChangedEventListener();
    registry.prependListeners(EventType.POST_INSERT, entityChangedEventListener);
    registry.prependListeners(EventType.POST_UPDATE, entityChangedEventListener);
    registry.prependListeners(EventType.POST_DELETE, entityChangedEventListener);
  }

  @Bean(initMethod = "initialize", destroyMethod = "destroy")
  public CuckooIndexFactory cuckooIndexFactory(
      @Autowired(required = false) SchedulingTaskExecutor taskExecutor,
      @Autowired CuckooProperties cuckooProperties,
      @Autowired ElasticsearchClientProperties clientProperties) {
    CuckooIndexFactory cuckooIndexFactory = new CuckooIndexFactory();

    boolean ssl = "https".equals(RegexpUtil.parseFirst(clientProperties.getUrl(), "^(https|http)"));

    String portString = RegexpUtil.parseGroup(clientProperties.getUrl(), ":([0-9]+)", 1);
    int port = Integer.parseInt(portString == null ? !ssl ? "80" : "443" : portString);

    String hostname =
        RegexpUtil.parseGroup(clientProperties.getUrl(), "^(https|http)://([^:|/]+)", 2);

    cuckooIndexFactory.setHostname(hostname);
    cuckooIndexFactory.setPort(port);
    cuckooIndexFactory.setApiKey(clientProperties.getApiKey());
    cuckooIndexFactory.setUsername(clientProperties.getUsername());
    cuckooIndexFactory.setPassword(clientProperties.getPassword());

    if (clientProperties.getSsl() != null) {
      cuckooIndexFactory.setSslCertificatePath(clientProperties.getSsl().getCertificatePath());
    }

    cuckooIndexFactory.setRebuild(cuckooProperties.isRebuild());

    cuckooIndexFactory.setExecutor(taskExecutor);

    return cuckooIndexFactory;
  }

  @Bean
  public EntityChangedEventListener entityChangedEventListener() {
    return new EntityChangedEventListener();
  }
}
