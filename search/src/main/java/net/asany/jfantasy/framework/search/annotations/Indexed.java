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
package net.asany.jfantasy.framework.search.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.asany.jfantasy.framework.search.dao.CuckooDao;
import net.asany.jfantasy.framework.search.dao.jpa.JpaDefaultCuckooDao;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Indexed {
  /**
   * elasticsearch 索引名称
   *
   * @return String
   */
  String indexName();

  /**
   * 配置是否创建索引
   *
   * @return boolean
   */
  boolean createIndex() default true;

  /**
   * 文档数据加载器
   *
   * @return Class
   */
  Class<? extends CuckooDao> dao() default JpaDefaultCuckooDao.class;
}
