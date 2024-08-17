package net.asany.jfantasy.framework.security.oauth2;

import net.asany.jfantasy.framework.security.auth.AuthType;
import org.junit.jupiter.api.Test;

class AuthTypeTest {

  @Test
  void valueOf() {
    String jwt =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MDQzNDkzNjgsIm5hbWUiOiJsaW1hb2ZlbmciLCJlbWFpbCI6ImxpbWFvZmVuZ0Btc24uY29tIiwidXNlcl9pZCI6MX0.Gx2b-UgKONUVjI2yPMCit2GI7Dtc5F-X5OzoVSpRISk";
    AuthType.of(jwt);
  }
}
