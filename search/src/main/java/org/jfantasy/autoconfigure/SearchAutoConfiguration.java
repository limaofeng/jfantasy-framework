package org.jfantasy.autoconfigure;

import org.jfantasy.framework.search.BuguIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.SchedulingTaskExecutor;

@Configuration
public class SearchAutoConfiguration {

  @Bean(initMethod = "open", destroyMethod = "close")
  public BuguIndex buguIndex(@Autowired(required = false) SchedulingTaskExecutor taskExecutor) {
    //    PropertiesHelper helper = PropertiesHelper.load("props/lucene.properties");
    BuguIndex buguIndex = new BuguIndex();
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
    return buguIndex;
  }
}
