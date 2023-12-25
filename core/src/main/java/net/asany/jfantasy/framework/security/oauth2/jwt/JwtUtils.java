package net.asany.jfantasy.framework.security.oauth2.jwt;

import net.asany.jfantasy.framework.jackson.JSON;
import net.asany.jfantasy.framework.security.oauth2.JwtTokenPayload;
import net.asany.jfantasy.framework.util.common.StringUtil;
import org.apache.commons.codec.binary.Base64;

public class JwtUtils {

  public static JwtTokenPayload payload(String accessToken) {
    String[] strings = StringUtil.tokenizeToStringArray(accessToken, ".");
    return JSON.deserialize(new String(Base64.decodeBase64(strings[1])), JwtTokenPayload.class);
  }
}
