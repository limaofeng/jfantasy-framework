package org.jfantasy.autoconfigure;

import org.jfantasy.framework.dao.DataSourceSetUtf8mb4;
import org.jfantasy.framework.spring.SpringContextUtil;
import org.jfantasy.framework.spring.config.AppConfig;
import org.jfantasy.framework.spring.config.DaoConfig;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import({AppConfig.class, DaoConfig.class})
public class CoreAutoConfiguration {

    @Bean
    public SpringContextUtil springContextUtil() {
        return new SpringContextUtil();
    }

    @Bean
    public DataSourceSetUtf8mb4 dataSourceSetUtf8mb4() {
        return new DataSourceSetUtf8mb4();
    }

}
