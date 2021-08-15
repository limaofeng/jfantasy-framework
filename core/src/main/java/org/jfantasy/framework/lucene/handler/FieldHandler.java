package org.jfantasy.framework.lucene.handler;

import org.apache.lucene.document.Document;

public interface FieldHandler {

  void handle(Document paramDocument);
}
