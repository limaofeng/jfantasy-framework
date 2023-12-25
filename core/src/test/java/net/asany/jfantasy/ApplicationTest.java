package net.asany.jfantasy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("net.asany.jfantasy.springboot")
@EntityScan("net.asany.jfantasy.*.bean")
@EnableAutoConfiguration(exclude = {JmxAutoConfiguration.class})
public class ApplicationTest {
  public static void main(String[] args) {
    SpringApplication.run(ApplicationTest.class, args);
  }
}
