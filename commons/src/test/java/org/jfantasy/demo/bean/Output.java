package org.jfantasy.demo.bean;

import lombok.Data;

/**
 * @author limaofeng
 * @version V1.0
 * @date 2019-03-07 10:39
 */
@Data
public class Output<T> {
  private Message message;

  private T data;

  @Data
  public static class Message {
    private String result;
    private String description;
  }
}
