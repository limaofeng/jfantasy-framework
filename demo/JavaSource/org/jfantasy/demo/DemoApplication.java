package org.jfantasy.demo;

import org.jfantasy.framework.util.common.ClassUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 07/11/2017 8:58 PM
 */
@Configuration
@EnableAutoConfiguration
public class DemoApplication {

    public DemoApplication(@Autowired MultipartConfigElement multipartConfigElement){
        ClassUtil.setFieldValue(multipartConfigElement,"maxFileSize",10450787);
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
