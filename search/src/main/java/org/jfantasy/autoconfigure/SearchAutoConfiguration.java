package org.jfantasy.autoconfigure;

import org.jfantasy.framework.search.CuckooIndex;
import org.jfantasy.framework.search.IndexedFactory;
import org.jfantasy.framework.search.dao.EntityChangedEventListener;
import org.jfantasy.framework.search.elastic.ElasticIndexedFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.SchedulingTaskExecutor;

@Configuration
public class SearchAutoConfiguration {

  @Bean
  public IndexedFactory indexedFactory() {
    return new ElasticIndexedFactory();
  }

  @Bean(initMethod = "open", destroyMethod = "close")
  public CuckooIndex cuckooIndex(
      @Autowired(required = false) SchedulingTaskExecutor taskExecutor,
      @Autowired IndexedFactory indexedFactory) {
    //    PropertiesHelper helper = PropertiesHelper.load("props/lucene.properties");
    CuckooIndex cuckooIndex = new CuckooIndex();
    cuckooIndex.setExecutor(taskExecutor);
    cuckooIndex.setIndexedFactory(indexedFactory);
    cuckooIndex.setRebuild(true);
    // if (StringUtil.isNotBlank(helper.getProperty("indexes.analyzer"))) {
    //
    // buguIndex.setAnalyzer(ClassUtil.newInstance(helper.getProperty("indexes.analyzer")));
    // }
    //    buguIndex.setBasePackage(StringUtil.join(helper.getMergeProperty("indexes.scan.package"),
    // ";"));
    //    buguIndex.setDirectoryPath(helper.getProperty("indexes.storage.path"));
    //    buguIndex.setIndexReopenPeriod(helper.getLong("indexes.reopen.reriod", 30000L));
    //    buguIndex.setExecutor(taskExecutor);
    //    buguIndex.setRebuild(helper.getBoolean("indexes.rebuild", false));
    return cuckooIndex;
  }

  @Bean
  public EntityChangedEventListener entityChangedEventListener() {
    return new EntityChangedEventListener();
  }
}
