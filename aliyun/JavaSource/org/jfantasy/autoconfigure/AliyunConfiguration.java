package org.jfantasy.autoconfigure;

import org.jfantasy.aliyun.AliyunSettings;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AliyunConfiguration {

    @Bean(name = "aliyunSettings")
    @ConfigurationProperties(prefix = "aliyun.ons")
    public AliyunSettings aliyunSettings() {
        return new AliyunSettings();
    }

}
