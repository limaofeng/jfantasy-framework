package net.asany.jfantasy.framework.security.auth.oauth2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
   *
   * <p>一般是应用标识
   */
  private String iss;

  /**
   * 主题
   *
   * <p>一般是 token 所面向的用户
   */
  private String sub;

  /**
   * 接收方
   *
   * <p>一般是租户 ID
   */
  private String aud;

  /**
   * 过期时间
   *
   * <p>通常为Unix时间戳格式。
   */
  private Long exp;

  /**
   * 生效时间
   *
   * <p>在此时间之前不应被接受处理。
   */
  private Long nbf;

  /**
   * 发行时间
   *
   * <p>通常为Unix时间戳格式。
   */
  private Long iat;

  /**
   * 唯一标识符
   *
   * <p>用于防止令牌被重放。
   */
  private String jti;

  /*------------------------ 公共声明 ------------------------*/

  /**
   * 用户名
   *
   * <p>一般是用户的登录名
   */
  private String name;

  /**
   * 用户电子邮件地址
   *
   * <p>一般是用户的电子邮件地址
   */
  private String email;

  /*------------------------ 私有声明 ------------------------*/

  /** 用户 ID */
  @JsonProperty("user_id")
  private Long userId;

  /** （Key ID）可以用来标识具体使用的密钥 */
  private String kid;

  /**
   * 权限范围
   *
   * <p>用户的权限范围
   */
  private List<String> scope;
}
