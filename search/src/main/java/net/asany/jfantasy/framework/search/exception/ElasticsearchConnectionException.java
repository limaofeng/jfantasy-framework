package net.asany.jfantasy.framework.search.exception;

public class ElasticsearchConnectionException extends RuntimeException {
  public ElasticsearchConnectionException(String message) {
    super(message);
  }

  public ElasticsearchConnectionException(String message, Exception e) {
    super(message, e);
  }
}
