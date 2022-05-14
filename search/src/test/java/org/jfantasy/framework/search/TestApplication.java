package org.jfantasy.framework.search;

import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.search.config.IndexedScan;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@IndexedScan("cn.asany.demo.bean")
@EnableAutoConfiguration(
    exclude = {
      WebClientAutoConfiguration.class,
      MongoAutoConfiguration.class,
      QuartzAutoConfiguration.class
    })
public class TestApplication {}
