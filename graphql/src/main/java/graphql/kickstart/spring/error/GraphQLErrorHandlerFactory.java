package graphql.kickstart.spring.error;

import static graphql.kickstart.spring.error.GraphQLErrorFactory.withReflection;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import graphql.kickstart.execution.error.DefaultGraphQLErrorHandler;
import graphql.kickstart.execution.error.GraphQLErrorHandler;

import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.util.common.ClassUtil;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 解决代理类，无法获取正确的方法描述的问题
 * <p>
 * 使用 ClassUtil.getRealClass(objClz) 包装 objClz
 *
 * @author limaofeng
 */
@Slf4j
public class GraphQLErrorHandlerFactory {

    public GraphQLErrorHandler create(ConfigurableApplicationContext applicationContext, boolean exceptionHandlersEnabled) {
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        List<GraphQLErrorFactory> factories = Arrays.stream(beanFactory.getBeanDefinitionNames())
            .filter(applicationContext::containsBean)
            .map(name -> scanForExceptionHandlers(applicationContext, name))
            .flatMap(List::stream)
            .collect(toList());

        if (!factories.isEmpty() || exceptionHandlersEnabled) {
            log.debug("Handle GraphQL errors using exception handlers defined in {} custom factories",
                factories.size());
            return new GraphQLErrorFromExceptionHandler(factories);
        }

        log.debug("Using default GraphQL error handler");
        return new DefaultGraphQLErrorHandler();
    }

    private List<GraphQLErrorFactory> scanForExceptionHandlers(ApplicationContext context,
                                                               String name) {
        try {
            Class<?> objClz = context.getType(name);
            if (objClz == null) {
                log.info("Cannot load class " + name);
                return emptyList();
            }
            return Arrays.stream(ClassUtil.getRealClass(objClz).getDeclaredMethods())
                .filter(ReflectiveMethodValidator::isGraphQLExceptionHandler)
                .map(method -> withReflection(context.getBean(name), method))
                .collect(toList());
        } catch (BeanCreationException e) {
            log.error("Cannot load class {}. {}", name, e.getMessage());
            return emptyList();
        }
    }

}
