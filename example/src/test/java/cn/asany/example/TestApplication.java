package cn.asany.example;

import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.dao.jpa.SimpleAnyJpaRepository;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author limaofeng
 * @version V1.0
 * @date 2019/2/13 4:04 PM
 */
@Slf4j
@Configuration
@ComponentScan("cn.asany.example.demo")
@EntityScan({
  "cn.asany.example.*.bean",
})
@EnableJpaRepositories(
    includeFilters = {
      @ComponentScan.Filter(
          type = FilterType.ASSIGNABLE_TYPE,
          value = {JpaRepository.class})
    },
    basePackages = {
      "cn.asany.example.*.dao",
    },
    repositoryBaseClass = SimpleAnyJpaRepository.class)
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, QuartzAutoConfiguration.class})
public class TestApplication {}
