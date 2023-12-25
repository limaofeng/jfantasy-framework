package net.asany.jfantasy.graphql;

import graphql.kickstart.tools.SchemaParserDictionary;

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
   * @param dictionary SchemaParserDictionary
   */
  void build(SchemaParserDictionary dictionary);
}
