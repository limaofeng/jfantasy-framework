package org.jfantasy.graphql;

import graphql.kickstart.tools.SchemaParserDictionary;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 模式解析器字典生成器
 *
 * @author limaofeng
 * @version V1.0
 * @date 2020/5/23 12:41 PM
 */
public interface SchemaParserDictionaryBuilder {

    /**
     * 构建方法
     *
     * @param dictionary
     */
    void build(SchemaParserDictionary dictionary);

}
