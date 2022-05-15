package org.jfantasy.autoconfigure;

import org.jfantasy.autoconfigure.properties.CuckooProperties;
import org.jfantasy.autoconfigure.properties.ElasticsearchClientProperties;
import org.jfantasy.framework.search.CuckooIndexFactory;
import org.jfantasy.framework.search.dao.EntityChangedEventListener;
import org.jfantasy.framework.util.regexp.RegexpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.SchedulingTaskExecutor;

@Configuration
@EnableConfigurationProperties({CuckooProperties.class, ElasticsearchClientProperties.class})
public class SearchAutoConfiguration {

  @Bean(initMethod = "open", destroyMethod = "close")
  public CuckooIndexFactory cuckooIndex(
      @Autowired(required = false) SchedulingTaskExecutor taskExecutor,
      @Autowired CuckooProperties cuckooProperties,
      @Autowired ElasticsearchClientProperties clientProperties) {
    CuckooIndexFactory cuckooIndexFactory = new CuckooIndexFactory();

    boolean ssl = "https".equals(RegexpUtil.parseFirst(clientProperties.getUrl(), "^(https|http)"));

    String portString = RegexpUtil.parseGroup(clientProperties.getUrl(), ":([0-9]+)", 1);
    int port = Integer.parseInt(portString == null ? !ssl ? "80" : "443" : portString);

    String hostname = RegexpUtil.parseFirst(clientProperties.getUrl(), "^(https|http)://([^/]+)");

    cuckooIndexFactory.setHostname(hostname);
    cuckooIndexFactory.setApiKey(clientProperties.getApiKey());
    cuckooIndexFactory.setUsername(clientProperties.getUsername());
    cuckooIndexFactory.setPassword(clientProperties.getPassword());

    cuckooIndexFactory.setRebuild(cuckooProperties.isRebuild());

    cuckooIndexFactory.setExecutor(taskExecutor);
    cuckooIndexFactory.setRebuild(true);

    return cuckooIndexFactory;
  }

  @Bean
  public EntityChangedEventListener entityChangedEventListener() {
    return new EntityChangedEventListener();
  }
}
