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

import org.junit.jupiter.api.Test;

public class TestMySQLDialect {

  @Test
  public void testGetLimitString() {
    MySQLDialect mySQLDialect = new MySQLDialect();

    String sql = "select GEN_NAME,GEN_VALUE FROM sys_sequence WHERE GEN_NAME = #{value}";

    System.out.println(mySQLDialect.getLimitString(sql, 1, 5));

    System.out.println(mySQLDialect.getCountString(sql));

    System.out.print(sql);
  }
}
