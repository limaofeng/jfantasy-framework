package org.jfantasy.framework.spring.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.PropertiesHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourceConfig {

    @Autowired
    public DataSourceConfig(DataSourceProperties properties) {
        PropertiesHelper helper = PropertiesHelper.load("application.properties");
        if (properties.getType() == null) {
            properties.setType(ClassUtil.forName(helper.getProperty("spring.datasource.type")));
        }
        if (properties.getDriverClassName() == null) {
            properties.setDriverClassName(helper.getProperty("spring.datasource.driver-class-name"));
        }
    }

    @Bean
    @ConfigurationProperties("spring.datasource.druid")
    public DruidDataSource dataSource(DataSourceProperties properties) {
        // 手动加载配置
        PropertiesHelper helper = PropertiesHelper.load("application.properties");
        Properties druidProperties = PropertiesHelper.load(helper.getProperties()).filter(item-> item.getKey().toString().startsWith("spring.datasource.druid.")).map(item -> new PropertiesHelper.MapEntry(item.getKey().toString().replaceFirst("spring.datasource.",""),item.getValue())).getProperties();
        // 创建 datasource
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.configFromPropety(druidProperties);
        dataSource.setDriverClassName(properties.determineDriverClassName());
        dataSource.setUrl(properties.determineUrl());
        dataSource.setUsername(properties.determineUsername());
        dataSource.setPassword(properties.determinePassword());
        DatabaseDriver databaseDriver = DatabaseDriver.fromJdbcUrl(properties.determineUrl());
        String validationQuery = databaseDriver.getValidationQuery();
        if (validationQuery != null) {
            dataSource.setTestOnBorrow(true);
            dataSource.setValidationQuery(validationQuery);
        }
        return dataSource;
    }

}
