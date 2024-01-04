package net.asany.jfantasy.framework.security.oauth2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.asany.jfantasy.framework.security.oauth2.core.TokenType;

import java.util.List;

/**
 * JWT 数据内容
 *
 * @author limaofeng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtTokenPayload {

  /*------------------------ JWT 标准字段 ------------------------*/

  /**
   * 签发者
   * <p>
   * 一般是服务端的地址
   */
  private String iss;
  /**
   * 主题
   * <p>
   * 一般是 token 所面向的用户
   */
  private String sub;
  /**
   * 接收方
   * <p>
   * 一般是客户端的地址
   */
  private String aud;
  /**
   * 过期时间
   * <p>
   * 通常为Unix时间戳格式。
   */
  private Long exp;
  /**
   * 生效时间
   * <p>
   * 在此时间之前不应被接受处理。
   */
  private Long nbf;
  /**
   * 发行时间
   * <p>
   * 通常为Unix时间戳格式。
   */
  private Long iat;
  /**
   * 唯一标识符
   * <p>
   * 用于防止令牌被重放。
   */
  private String jti;

  /*------------------------ 公共声明 ------------------------*/

  /**
   * 用户名
   * <p>
   * 一般是用户的登录名
   */
  private String name;
  /**
   * 用户电子邮件地址
   * <p>
   * 一般是用户的电子邮件地址
   */
  private String email;

  /*------------------------ 私有声明 ------------------------*/

  /** 用户 ID */
  @JsonProperty("user_id")
  private Long userId;

  /**
   * 权限范围
   * <p>
   * 用户的权限范围
   */
  private List<String> scope;
  /**
   * 客户端 ID
   * <p>
   * 一般是客户端的标识
   */
  @JsonProperty("client_id")
  private String clientId;
  /**
   * 令牌类型
   * <p>
   *  令牌类型，该值大小写不敏感，必选项，可以是bearer类型或mac类型。
   */
  @JsonProperty("token_type")
  private TokenType tokenType;
}
