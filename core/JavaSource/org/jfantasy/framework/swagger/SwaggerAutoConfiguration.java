package org.jfantasy.framework.swagger;

import com.google.common.base.Predicate;
import org.jfantasy.framework.spring.config.WebMvcConfig;
import org.jfantasy.schedule.rest.ScheduleController;
import org.jfantasy.schedule.rest.TriggerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.mvc.*;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.BasicErrorController;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import springfox.documentation.RequestHandler;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashSet;
import java.util.Set;

import static springfox.documentation.builders.PathSelectors.regex;

@ConditionalOnClass(EnableSwagger2.class)
@Configuration
@EnableSwagger2
@AutoConfigureAfter(WebMvcConfig.class)
public class SwaggerAutoConfiguration implements EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(SwaggerAutoConfiguration.class);
    private static final String DEFAULT_INCLUDE_PATTERN = "/.*";
    private static final Set<Class<?>> EXCLUDES = new HashSet<>();

    static {
        EXCLUDES.add(BasicErrorController.class);
        EXCLUDES.add(HalJsonMvcEndpoint.class);
        EXCLUDES.add(EndpointMvcAdapter.class);
        EXCLUDES.add(HealthMvcEndpoint.class);
        EXCLUDES.add(LogFileMvcEndpoint.class);
        EXCLUDES.add(MetricsMvcEndpoint.class);
        EXCLUDES.add(EnvironmentMvcEndpoint.class);
        EXCLUDES.add(ScheduleController.class);
        EXCLUDES.add(TriggerController.class);
    }

    private static final Predicate<RequestHandler> APIS = handler -> {
        assert handler != null;
        Class<?> beanType = handler.declaringClass();
        return !EXCLUDES.contains(beanType);
    };

    private RelaxedPropertyResolver propertyResolver;

    @Override
    public void setEnvironment(Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, "springfox.documentation.swagger.");
    }

    @Bean
    public Docket swaggerSpringfoxDocket() {
        LOG.debug("Starting Swagger");
        StopWatch watch = new StopWatch();
        watch.start();
        Docket swaggerSpringMvcPlugin = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .genericModelSubstitutes(ResponseEntity.class)
                .select()
                .apis(APIS)
                .paths(regex(DEFAULT_INCLUDE_PATTERN)).build();// and by paths
        if (propertyResolver.containsProperty("host")) {
            swaggerSpringMvcPlugin.host(propertyResolver.getProperty("host"));
        }
        watch.stop();
        LOG.debug("Started Swagger in {} ms", watch.getTotalTimeMillis());
        return swaggerSpringMvcPlugin;
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                propertyResolver.getProperty("api.title", ""),
                propertyResolver.getProperty("api.description", ""),
                propertyResolver.getProperty("api.version", "v1.0"),
                propertyResolver.getProperty("api.termsOfServiceUrl", ""),
                new Contact(propertyResolver.getProperty("api.contact.name", ""), propertyResolver.getProperty("api.contact.url", ""), propertyResolver.getProperty("api.contact.email", "")),
                propertyResolver.getProperty("api.license", ""),
                propertyResolver.getProperty("api.licenseUrl", "")
        );
    }

}