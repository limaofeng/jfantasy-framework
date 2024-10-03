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
package net.asany.jfantasy.framework.util.asm;

import java.util.HashMap;
import java.util.Map;

public class AsmContext {

  private static final ThreadLocal<AsmContext> threadLocal = new ThreadLocal<>();
  private final Map<String, Object> data = new HashMap<>();

  public static AsmContext getContext() {
    AsmContext context = threadLocal.get();
    if (context == null) {
      threadLocal.set(context = new AsmContext());
    }
    return context;
  }

  public Object get(String key) {
    return data.get(key);
  }

  public void set(String key, Object value) {
    data.put(key, value);
  }

  public <T> T get(String key, Class<T> classed) {
    return classed.cast(get(key));
  }
}
