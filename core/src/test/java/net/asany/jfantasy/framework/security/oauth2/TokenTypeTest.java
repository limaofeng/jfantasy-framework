package net.asany.jfantasy.framework.security.oauth2;

import static org.junit.jupiter.api.Assertions.*;

import net.asany.jfantasy.framework.security.auth.TokenType;
import org.junit.jupiter.api.Test;

class TokenTypeTest {

  @Test
  void valueOf() {
    String jwt =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MDQzNDkzNjgsIm5hbWUiOiJsaW1hb2ZlbmciLCJlbWFpbCI6ImxpbWFvZmVuZ0Btc24uY29tIiwidXNlcl9pZCI6MX0.Gx2b-UgKONUVjI2yPMCit2GI7Dtc5F-X5OzoVSpRISk";
    TokenType.of(jwt);
  }
}
