package org.jfantasy.autoconfigure;

import org.jfantasy.framework.dao.DataSourceSetUtf8mb4;
import org.jfantasy.framework.spring.SpringContextUtil;
import org.jfantasy.framework.spring.config.AppConfig;
import org.jfantasy.framework.spring.config.DaoConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * @author limaofeng
 */
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
