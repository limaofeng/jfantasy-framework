package org.jfantasy.framework.spring.config;

import org.jfantasy.framework.dao.mybatis.keygen.util.DataBaseKeyGenerator;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Description: <应用配置类>. <br>
 * <p>
 * <负责注册除Controller等web层以外的所有bean，包括aop代理，service层，dao层，缓存，等等>
 * </p>
 */
@Configuration
@EnableAsync
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Import({DaoConfig.class, QuartzConfig.class})
public class AppConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer placehodlerConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public DataBaseKeyGenerator dataBaseKeyGenerator(@Value("${dataBaseKey.poolSize:10}") String dataBaseKeyPoolSize) {
        DataBaseKeyGenerator dataBaseKeyGenerator = new DataBaseKeyGenerator();
        dataBaseKeyGenerator.setPoolSize(Integer.valueOf(dataBaseKeyPoolSize));
        return dataBaseKeyGenerator;
    }

    /*
    @Value("${mail.hostname}")
    private String hostname;
    @Value("${mail.from}")
    private String from;
    @Value("${mail.displayName}")
    private String displayName;
    @Value("${mail.username}")
    private String username;
    @Value("${mail.password}")
    private String passWord;
    @Value("${mail.charset}")
    private String charset;

    @Bean
    public MailSendService mailSendService() {
        MailSendService mailSendService = new MailSendService();
        mailSendService.setHostname(hostname);
        mailSendService.setFrom(from);
        mailSendService.setDisplayName(displayName);
        mailSendService.setUsername(username);
        mailSendService.setPassword(passWord);
        mailSendService.setCharset(charset);
        return mailSendService;
    }*/

    @Bean(name = "taskExecutor")
    public SchedulingTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        //线程池所使用的缓冲队列
        pool.setQueueCapacity(200);
        //线程池维护线程的最少数量
        pool.setCorePoolSize(5);
        //线程池维护线程的最大数量
        pool.setMaxPoolSize(1000);
        //线程池维护线程所允许的空闲时间
        pool.setKeepAliveSeconds(30000);
        pool.setThreadNamePrefix("Task-");
        pool.initialize();
        return pool;
    }

    @Configuration
    public static class ThreadingConfig implements AsyncConfigurer {

        @Override
        public Executor getAsyncExecutor() {
            ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
            pool.setMaxPoolSize(10);
            pool.setCorePoolSize(10);
            pool.setThreadNamePrefix("Spring-Async-");
            pool.initialize();
            return pool;
        }

        @Override
        public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
            return new SimpleAsyncUncaughtExceptionHandler();
        }

    }

    /*
    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster applicationEventMulticaster() {
        SimpleApplicationEventMulticaster applicationEventMulticaster = new SimpleApplicationEventMulticaster();
        applicationEventMulticaster.setTaskExecutor(taskExecutor());
        return applicationEventMulticaster;
    }*/


}
