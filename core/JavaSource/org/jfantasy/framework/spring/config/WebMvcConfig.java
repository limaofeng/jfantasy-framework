package org.jfantasy.framework.spring.config;


import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import org.hibernate.validator.HibernateValidator;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.jackson.ThreadJacksonMixInHolder;
import org.jfantasy.framework.jackson.UnirestObjectMapper;
import org.jfantasy.framework.spring.mvc.method.annotation.FormModelMethodArgumentResolver;
import org.jfantasy.framework.spring.mvc.method.annotation.PagerModelAttributeMethodProcessor;
import org.jfantasy.framework.spring.mvc.method.annotation.PropertyFilterModelAttributeMethodProcessor;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.PropertiesHelper;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.web.filter.ActionContextFilter;
import org.jfantasy.framework.web.filter.ConversionCharacterEncodingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.DispatcherType;
import javax.servlet.MultipartConfigElement;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EnableWebMvc
@Configuration
@ComponentScan(basePackages = {"org.jfantasy.*.rest"}, useDefaultFilters = false, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = {RestController.class, Controller.class})
})
public class WebMvcConfig extends WebMvcConfigurerAdapter implements EnvironmentAware {

    private final ApplicationContext applicationContext;

    private RelaxedPropertyResolver jacksonPropertyResolver;

    @Autowired
    public WebMvcConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * <基于cookie的本地化资源处理器>. <br>
     *
     * @return CookieLocaleResolver
     */
    @Bean(name = "localeResolver")
    public CookieLocaleResolver cookieLocaleResolver() {
        return new CookieLocaleResolver();
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        MappingJackson2JsonView jackson2JsonView = new MappingJackson2JsonView();
        Set<String> parameterNames = new HashSet<>();
        parameterNames.add("callback");
        jackson2JsonView.setJsonpParameterNames(parameterNames);
        registry.enableContentNegotiation();
        super.configureViewResolvers(registry);
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

    @Override
    public void setEnvironment(Environment environment) {
        this.jacksonPropertyResolver = new RelaxedPropertyResolver(environment, "spring.jackson.");
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = applicationContext.getBean("jacksonObjectMapper", ObjectMapper.class);
        // 将 objectMapper 设置到 JSON 中
        JSON.initialize(objectMapper);
        // 读取 spring.jackson.mixin.packages
        PropertiesHelper helper = PropertiesHelper.load("application.properties");
        Set<String> packages = new HashSet<>();
        for (String _packages : helper.getMergeProperty("spring.jackson.mixin.packages")) {
            packages.addAll(Arrays.asList(StringUtil.tokenizeToStringArray(_packages)));
        }
        packages.addAll(Arrays.asList(StringUtil.tokenizeToStringArray(this.jacksonPropertyResolver.getProperty("mixin.packages", "org.jfantasy.*.bean"))));
        ThreadJacksonMixInHolder.scan(packages.toArray(new String[packages.size()]));
        objectMapper.setMixIns(ThreadJacksonMixInHolder.getSourceMixins());
        // 设置到 Unirest 中
        Unirest.setObjectMapper(new UnirestObjectMapper(objectMapper));
        return objectMapper;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        Class[] removeClazz = new Class[]{StringHttpMessageConverter.class, MappingJackson2HttpMessageConverter.class};
        converters.removeIf(converter -> ObjectUtil.exists(removeClazz, converter.getClass()));
        converters.add(0, new MappingJackson2HttpMessageConverter(objectMapper()));
        converters.add(0, new StringHttpMessageConverter(Charset.forName("utf-8")));
        super.configureMessageConverters(converters);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new FormModelMethodArgumentResolver());
        argumentResolvers.add(new PropertyFilterModelAttributeMethodProcessor());
        argumentResolvers.add(new PagerModelAttributeMethodProcessor());
        super.addArgumentResolvers(argumentResolvers);
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
        } else {
            return super.getValidator();
        }
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "HEAD", "PATCH", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Accept", "Origin", "X-Requested-With", "Content-Type", "Last-Modified")
                .exposedHeaders("Set-Cookie")
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
        filterRegistrationBean.setFilter(new OpenSessionInViewFilter());
        filterRegistrationBean.addInitParameter("flushMode", "COMMIT");
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.setOrder(400);
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.setOrder(500);
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

    @Bean
    public ServletRegistrationBean druidServlet() {
        return new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
    }

}
