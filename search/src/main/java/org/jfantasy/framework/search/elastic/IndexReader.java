package org.jfantasy.framework.search.elastic;

import java.io.IOException;

public interface IndexReader {
  void decRef() throws IOException;
}
