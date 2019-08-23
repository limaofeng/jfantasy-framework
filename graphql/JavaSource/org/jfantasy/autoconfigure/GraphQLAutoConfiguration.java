package org.jfantasy.autoconfigure;

import com.oembedler.moon.graphql.boot.SchemaDirective;
import org.jfantasy.graphql.directives.AuthorisationDirective;
import org.jfantasy.graphql.directives.DateFormatDirective;
import org.jfantasy.graphql.directives.FileObjectFormatDirective;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019/8/23 6:18 下午
 */
@Configuration
public class GraphQLAutoConfiguration {

    @Bean
    public OpenEntityManagerInViewFilter openEntityManagerInViewFilter() {
        return new OpenEntityManagerInViewFilter();
    }

    @Bean
    public SchemaDirective authorisationDirective() {
        return new SchemaDirective("auth", new AuthorisationDirective());
    }

    @Bean
    public SchemaDirective dateFormattingDirective() {
        return new SchemaDirective("dateFormat", new DateFormatDirective());
    }

    @Bean
    public SchemaDirective testDirective() {
        return new SchemaDirective("fileObjectFormat", new FileObjectFormatDirective());
    }

}
