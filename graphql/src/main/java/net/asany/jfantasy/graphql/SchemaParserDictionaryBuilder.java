/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
