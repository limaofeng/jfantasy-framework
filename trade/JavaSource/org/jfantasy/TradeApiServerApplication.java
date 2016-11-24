package org.jfantasy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration(exclude = {WebSocketAutoConfiguration.class, JmxAutoConfiguration.class})
public class TradeApiServerApplication {

    private TradeApiServerApplication() {
        throw new IllegalAccessError("Utility class");
    }

    public static void main(String[] args) {
        SpringApplication.run(TradeApiServerApplication.class, args);//NOSONAR
    }

}
