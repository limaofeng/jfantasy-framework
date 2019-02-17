package org.jfantasy.framework.spring.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import org.hibernate.validator.HibernateValidator;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.jackson.UnirestObjectMapper;
import org.jfantasy.framework.jackson.annotation.BeanFilter;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.framework.spring.ClassPathScanner;
import org.jfantasy.framework.spring.mvc.method.annotation.PagerModelAttributeMethodProcessor;
import org.jfantasy.framework.spring.mvc.method.annotation.PropertyFilterModelAttributeMethodProcessor;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.web.filter.ActionContextFilter;
import org.jfantasy.framework.web.filter.ConversionCharacterEncodingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;

import javax.servlet.DispatcherType;
import javax.servlet.MultipartConfigElement;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@EnableWebMvc
@Configuration
@ComponentScan(basePackages = {"org.jfantasy.*.rest"}, useDefaultFilters = false, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = {RestController.class, Controller.class})
})
@Order(value = WebMvcConfig.ORDER)
public class WebMvcConfig implements WebMvcConfigurer {

    public static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 32;

    private final ApplicationContext applicationContext;

    @Autowired(required = false)
    public WebMvcConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void configureContentNegotiation(final ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false);
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("*.html").addResourceLocations("/");
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(10485760);
        return factory.createMultipartConfig();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = JSON.getObjectMapper();
        Unirest.setObjectMapper(new UnirestObjectMapper(objectMapper));
        return objectMapper;
    }

    public static Set<Class> scanJsonResultFilter() {
        Set<Class> classes = new HashSet<>();
        for (Class restClass : ClassPathScanner.getInstance().findAnnotationedClasses("*.**.rest", RestController.class)) {
            for (Method method : ClassUtil.getDeclaredMethods(restClass)) {
                JsonResultFilter jsonResultFilter = ClassUtil.getMethodAnno(method, JsonResultFilter.class);
                if (jsonResultFilter != null) {
                    for (BeanFilter filter : jsonResultFilter.value()) {
                        classes.add(filter.type());
                    }
                }
            }
        }
        return classes;
    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        Class[] removeClazz = new Class[]{StringHttpMessageConverter.class, MappingJackson2HttpMessageConverter.class};
        converters.removeIf(converter -> ObjectUtil.exists(removeClazz, converter.getClass()));
        converters.add(0, new MappingJackson2HttpMessageConverter(objectMapper()));
        converters.add(0, new StringHttpMessageConverter(Charset.forName("utf-8")));
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new PropertyFilterModelAttributeMethodProcessor());
        argumentResolvers.add(new PagerModelAttributeMethodProcessor());
    }

    @Override
    public Validator getValidator() {
        if (applicationContext instanceof XmlWebApplicationContext) {
            ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(LocalValidatorFactoryBean.class);
            beanDefinitionBuilder.setAutowireMode(2);
            beanDefinitionBuilder.addPropertyValue("providerClass", HibernateValidator.class);
            defaultListableBeanFactory.registerBeanDefinition("validator", beanDefinitionBuilder.getBeanDefinition());
            return configurableApplicationContext.getBean("validator", Validator.class);
        }
        return null;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "HEAD", "PATCH", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Accept", "Origin", "Authorization", "Content-Type", "Last-Modified")
                .allowCredentials(true).maxAge(3600);
    }

    @Bean
    public FilterRegistrationBean conversionCharacterEncodingFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new ConversionCharacterEncodingFilter());
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.setOrder(200);
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean actionContextFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new ActionContextFilter());
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.setOrder(300);
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean openSessionInViewFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new OpenEntityManagerInViewFilter());
        filterRegistrationBean.addInitParameter("entityManagerFactoryBeanName", "entityManagerFactory");
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.setOrder(400);
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

}
