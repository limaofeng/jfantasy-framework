package org.jfantasy.autoconfigure;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan("org.jfantasy.system")
@Configuration
@EntityScan("org.jfantasy.system.bean")
public class SystemAutoConfiguration {
}
