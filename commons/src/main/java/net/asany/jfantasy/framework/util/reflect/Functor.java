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
package net.asany.jfantasy.framework.util.reflect;

public class Functor {
  private final Object source;
  private final MethodProxy method;

  private Functor(Object object, MethodProxy method) {
    this.source = object;
    this.method = method;
  }

  public Object call() {
    return this.method.invoke(this.source);
  }

  public Object call(Object object) {
    return this.method.invoke(this.source, new Object[] {object});
  }

  public static Functor create(Object object, MethodProxy method) {
    return new Functor(object, method);
  }
}
