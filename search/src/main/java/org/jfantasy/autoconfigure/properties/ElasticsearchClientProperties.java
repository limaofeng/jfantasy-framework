package org.jfantasy.autoconfigure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.cuckoo.elasticsearch.client")
public class ElasticsearchClientProperties {

  private String url;
  private String username;
  private String password;
  private String apiKey;
  private SSL ssl;

  @Data
  public static class SSL {
    String certificate;
    String certificatePath;
  }
}
