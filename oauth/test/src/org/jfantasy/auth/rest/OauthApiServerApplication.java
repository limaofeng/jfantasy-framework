package org.jfantasy.auth.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableAutoConfiguration(exclude = {WebSocketAutoConfiguration.class, JmxAutoConfiguration.class})
public class OauthApiServerApplication {//NOSONAR

    public static void main(String[] args) {
        SpringApplication.run(OauthApiServerApplication.class, args);//NOSONAR
    }

}
