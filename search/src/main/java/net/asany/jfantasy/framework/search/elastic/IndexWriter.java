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
package net.asany.jfantasy.framework.search.elastic;

import java.io.IOException;
import java.io.Serializable;
import net.asany.jfantasy.framework.search.Document;

/**
 * 索引 Writer
 *
 * @author limaofeng
 */
public interface IndexWriter {

  /**
   * 提交
   *
   * @throws IOException 异常
   */
  void commit() throws IOException;

  /**
   * 删除所有
   *
   * @throws IOException 异常
   */
  void deleteAll() throws IOException;

  /**
   * 添加文档
   *
   * @param doc Document
   * @throws IOException 异常
   */
  void addDocument(Document doc) throws IOException;

  /**
   * 更新文档
   *
   * @param id 文档ID
   * @param doc 文档
   * @throws IOException 异常
   */
  void updateDocument(Serializable id, Document doc) throws IOException;

  /**
   * 删除文档
   *
   * @param id 文档ID
   * @throws IOException 异常
   */
  void deleteDocument(Serializable id) throws IOException;
}
