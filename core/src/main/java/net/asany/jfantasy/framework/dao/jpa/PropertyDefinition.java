package net.asany.jfantasy.framework.dao.jpa;

import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PropertyDefinition<C> {
  private String name;

  @Builder.Default
  private Map<String, PropertyFilterBuilder.PropertyPredicateCallback<C>> predicates =
      new HashMap<>();
}
