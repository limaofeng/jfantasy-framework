package org.jfantasy.framework.security.oauth2.core;

/**
 * 客户端查询服务
 *
 * @author limaofeng
 */
public interface ClientDetailsService {
  /**
   * 查询客户端信息
   *
   * @param clientId 客户ID
   * @return ClientDetails
   * @throws ClientRegistrationException 异常
   */
  ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException;
}
