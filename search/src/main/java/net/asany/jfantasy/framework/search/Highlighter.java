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
