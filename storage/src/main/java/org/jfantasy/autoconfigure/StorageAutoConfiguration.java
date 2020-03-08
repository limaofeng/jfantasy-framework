package org.jfantasy.autoconfigure;

import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;
import org.jfantasy.storage.graphql.scalars.FileObjectCoercing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2020/3/7 8:02 下午
 */
@Configuration
public class StorageAutoConfiguration {

    @Value("${storage.url}")
    private String storageUrl;

    @Bean
    public GraphQLScalarType fileByScalar() {
        return GraphQLScalarType.newScalar().name("FileObject").description("文件对象").coercing(new FileObjectCoercing(this.storageUrl)).build();
    }

}
