package org.jfantasy.rpc;

import org.jfantasy.autoconfigure.LuceneAutoConfiguration;
import org.jfantasy.autoconfigure.QuartzAutoConfiguration;
import org.jfantasy.framework.swagger.SwaggerAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration;

@SpringBootApplication(exclude = {WebSocketAutoConfiguration.class, JmxAutoConfiguration.class,LuceneAutoConfiguration.class,SwaggerAutoConfiguration.class, QuartzAutoConfiguration.class})
public class RpcServerApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(RpcServerApplication.class);
        app.setWebEnvironment(false);
        app.run(args);
    }

}
