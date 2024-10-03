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
package net.asany.jfantasy.framework.service.batch;

import java.util.concurrent.CompletableFuture;
import lombok.Getter;

@Getter
public class Cargo<T, R> {
  private final CompletableFuture<R> hearthstone = new CompletableFuture<>();
  private final T content;

  private Cargo(T o) {
    this.content = o;
  }

  public static <T, R> Cargo<T, R> of(T o) {
    return new Cargo<>(o);
  }
}
