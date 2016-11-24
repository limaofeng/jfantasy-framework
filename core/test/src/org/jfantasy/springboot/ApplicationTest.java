package org.jfantasy.springboot;

import org.jfantasy.autoconfigure.WebStarterAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(value = {"org.jfantasy.springboot","org.jfantasy.async"})
@EntityScan("org.jfantasy.springboot.bean")
@Configuration
@EnableAutoConfiguration(exclude = {WebSocketAutoConfiguration.class,JmxAutoConfiguration.class, WebStarterAutoConfiguration.class})
public class ApplicationTest {

}
