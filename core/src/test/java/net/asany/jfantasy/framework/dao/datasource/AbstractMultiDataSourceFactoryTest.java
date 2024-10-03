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
package net.asany.jfantasy.framework.dao.datasource;

import java.util.HashMap;
import org.junit.jupiter.api.Test;

class AbstractMultiDataSourceFactoryTest {

  @Test
  void addDataSource() {
    AbstractMultiDataSourceManager observable = new AbstractMultiDataSourceManager(new HashMap<>());

    observable.on(
        MultiDataSourceOperations.ADD_DATA_SOURCE, (event) -> System.out.println(event.toString()));
    observable.on(
        MultiDataSourceOperations.ADD_DATA_SOURCE, (event) -> System.out.println(event.toString()));

    observable.addDataSource("xx", null);
    observable.addDataSource("2222", null);
  }
}
