package org.jfantasy.framework.search.elastic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.jfantasy.framework.search.DocumentData;

@Data
@Builder
@AllArgsConstructor
public class DocumentOperation {
  private String indexName;
  private String id;
  private Action action;
  private DocumentData doc;

  enum Action {
    create,
    update,
    delete
  }
}
