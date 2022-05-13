package org.jfantasy.framework.search.handler;

import org.jfantasy.framework.search.elastic.Document;

public interface FieldHandler {

  void handle(Document paramDocument);
}
