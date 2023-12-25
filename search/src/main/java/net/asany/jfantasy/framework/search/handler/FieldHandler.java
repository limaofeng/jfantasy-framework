package net.asany.jfantasy.framework.search.handler;

import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import net.asany.jfantasy.framework.search.Document;

public interface FieldHandler {

  void handle(Document paramDocument);

  void handle(TypeMapping.Builder typeMapping);
}
