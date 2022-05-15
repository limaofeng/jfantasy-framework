package org.jfantasy.framework.search.handler;

import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import org.jfantasy.framework.search.DocumentData;

public interface FieldHandler {

  void handle(DocumentData paramDocument);

  void handle(TypeMapping.Builder typeMapping);
}
