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
package net.asany.jfantasy.framework.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PinyinUtilsTest {

  @BeforeEach
  public void setUp() throws Exception {
    PinyinUtils.addMutilDict("白术", "bái,zhú");
  }

  @Test
  public void getShort() throws Exception {
    System.out.println(PinyinUtils.getShort("白术"));
  }

  @Test
  public void getAll() throws Exception {
    System.out.println(PinyinUtils.getAll("白术"));
    System.out.println(PinyinUtils.getAll("白术", "-"));
  }
}
