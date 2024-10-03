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
package net.asany.jfantasy.framework.dao.mybatis.keygen.dao;

import net.asany.jfantasy.framework.dao.mybatis.keygen.bean.Sequence;
import net.asany.jfantasy.framework.dao.mybatis.sqlmapper.SqlMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/** MyBatis SequenceDao 接口 */
@Repository
public interface SequenceDao extends SqlMapper {

  /**
   * 查询序列
   *
   * @param keyName keyName
   * @return Sequence
   */
  Sequence findUniqueByKey(String keyName);

  /**
   * 创建序列
   *
   * @param sequence sequence
   * @return int 影响行数
   */
  int insert(Sequence sequence);

  /**
   * 更新序列
   *
   * @param sequence sequence
   * @return int 影响行数
   */
  int update(Sequence sequence);

  /**
   * 删除序列
   *
   * @param key 序列 Key
   * @return 影响行数
   */
  int delete(String key);

  /**
   * 获取表中数据的max(id)
   *
   * @param table 表明
   * @param key 字段
   * @return max id
   */
  Integer queryTableSequence(@Param("table") String table, @Param("key") String key);
}
