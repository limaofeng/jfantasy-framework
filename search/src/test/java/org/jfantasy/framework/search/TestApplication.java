package org.jfantasy.framework.search;

import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.search.config.IndexedScan;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@Configuration
@EntityScan("cn.asany.demo")
@IndexedScan("cn.asany.demo")
@ComponentScan("cn.asany.demo")
@EnableJpaRepositories(basePackages = "cn.asany.demo.dao")
@EnableAutoConfiguration(
    exclude = {
      WebClientAutoConfiguration.class,
      MongoAutoConfiguration.class,
      QuartzAutoConfiguration.class
    })
public class TestApplication {}
