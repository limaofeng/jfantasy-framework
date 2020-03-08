package org.jfantasy.autoconfigure;

import com.coxautodev.graphql.tools.SchemaParserDictionary;
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
    public SchemaParserDictionary schemaParserDictionary() {
        return  new SchemaParserDictionary();
    }

}
