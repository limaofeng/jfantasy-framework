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
package net.asany.jfantasy.framework.dao;

public class DataQueryContextHolder {

  private static final ThreadLocal<DataQueryContext> HOLDER = new ThreadLocal<>();

  public static DataQueryContext getContext() {
    return HOLDER.get();
  }

  public static void setContext(DataQueryContext context) {
    DataQueryContext securityContextHolder = HOLDER.get();
    if (securityContextHolder != null) {
      HOLDER.remove();
    }
    HOLDER.set(context);
  }

  public static void clear() {
    DataQueryContext securityContextHolder = HOLDER.get();
    if (securityContextHolder != null) {
      HOLDER.remove();
    }
  }

  public static DataQueryContext createEmptyContext() {
    return new DataQueryContext();
  }
}
