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
package net.asany.jfantasy.framework.dao.mybatis.dialect;

/**
 * MyBatis 方言接口
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2012-10-28 下午08:22:02
 */
public interface Dialect {

  String getLimitString(String sql, int offset, int limit);

  String getCountString(String sql);
}
