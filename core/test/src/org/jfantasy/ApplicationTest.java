package org.jfantasy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("org.jfantasy.springboot")
@EntityScan("org.jfantasy.*.bean")
@EnableAutoConfiguration(exclude = {WebSocketAutoConfiguration.class, JmxAutoConfiguration.class})
public class ApplicationTest {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationTest.class, args);
    }
}
