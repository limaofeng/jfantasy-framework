package net.asany.jfantasy.framework.security;

import java.util.Optional;
import net.asany.jfantasy.framework.security.core.AuthenticatedPrincipal;
import net.asany.jfantasy.framework.util.common.ClassUtil;

/**
 * 用户字段扩展变量接口
 *
 * <p>但与用户关联的变量，但由其他模块实现，比如： 用户的网盘，用户的邮件设置等等
 */
public interface PrincipalDynamicAttribute<P extends AuthenticatedPrincipal, T> {

  default boolean supports(Class<? extends AuthenticatedPrincipal> authentication) {
    return ClassUtil.getInterfaceGenricType(this.getClass(), PrincipalDynamicAttribute.class)
        .isAssignableFrom(authentication);
  }

  /**
   * 获取字段名称
   *
   * @return 字段名称
   */
  String name();

  /**
   * 根据 LoginUser 获取字段的值
   *
   * @param user 用户对象
   * @return 字段值
   */
  Optional<T> value(P user);
}
