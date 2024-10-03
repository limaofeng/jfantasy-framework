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
package net.asany.jfantasy.framework.search;

import net.asany.jfantasy.framework.search.elastic.IndexWriter;
import net.asany.jfantasy.framework.search.elastic.SmartSearcher;
import net.asany.jfantasy.framework.search.query.Query;

/**
 * 索引
 *
 * @author limaofeng
 */
public interface CuckooIndex {

  /**
   * 获取索引的实体类
   *
   * @return 索引类型
   */
  <T> Class<T> getIndexClass();

  /**
   * 索引 Writer
   *
   * @return 索引 Writer
   */
  IndexWriter getIndexWriter();

  /**
   * 索引名称
   *
   * @return 索引名称
   */
  String getIndexName();

  /**
   * 搜索者
   *
   * @param query 查询对象
   * @param <T> 返回结果
   * @return 搜索者
   */
  <T> SmartSearcher<T> searcher(Query query);
}
