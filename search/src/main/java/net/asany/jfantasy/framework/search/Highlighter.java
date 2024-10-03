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
package net.asany.jfantasy.framework.search;

import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.util.ObjectBuilder;
import java.util.function.Function;

public class Highlighter {

  private final Highlight.Builder builder;

  private Highlighter(Highlight.Builder builder) {
    this.builder = builder;
  }

  public static Highlighter of(String field, String... fields) {
    Highlight.Builder builder = new Highlight.Builder();

    builder.fields(field, builder1 -> builder1);

    for (String _field : fields) {
      builder.fields(_field, builder1 -> builder1);
    }

    return new Highlighter(builder);
  }

  public static Highlighter of(Function<Highlight.Builder, Highlight.Builder> fn) {
    return new Highlighter(fn.apply(new Highlight.Builder()));
  }

  public Highlighter fields(
      String name, Function<HighlightField.Builder, ObjectBuilder<HighlightField>> fn) {
    builder.fields(name, fn);
    return this;
  }

  public Highlight build() {
    return this.builder.build();
  }
}
