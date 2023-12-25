package net.asany.jfantasy.framework.security.crypto.password;

/**
 * 密码编码器
 *
 * @author limaofeng
 * @version V1.0
 * @date 2019-03-28 15:00
 */
public interface PasswordEncoder {

  /**
   * 加密逻辑
   *
   * @param rawPassword 原密码
   * @return 加密后的密码
   */
  String encode(CharSequence rawPassword);

  /**
   * 密码比较方法
   *
   * @param rawPassword 未加密密码 一般指用户输入的原始密码
   * @param encodedPassword 已加密密码 数据库中存储的已加密密码
   * @return 是否相同
   */
  boolean matches(CharSequence rawPassword, String encodedPassword);
}
