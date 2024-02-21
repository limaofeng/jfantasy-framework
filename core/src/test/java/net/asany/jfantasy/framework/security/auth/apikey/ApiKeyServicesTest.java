package net.asany.jfantasy.framework.security.auth.apikey;

import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class ApiKeyServicesTest {

  @Test
  void generateApiKey() {
    String apiKey = new ApiKeyServices(null, null).generateApiKey();
    log.info("apiKey: {}", apiKey);
  }
}
