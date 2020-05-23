package org.jfantasy.graphql;

import graphql.kickstart.tools.SchemaParserDictionary;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2020/5/23 12:41 PM
 */
public interface SchemaParserDictionaryBuilder {

    void build(SchemaParserDictionary dictionary);

}
