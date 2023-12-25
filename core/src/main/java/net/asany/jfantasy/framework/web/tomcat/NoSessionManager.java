package net.asany.jfantasy.framework.web.tomcat;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Session;
import org.apache.catalina.session.ManagerBase;

/**
 * 禁用Session管理器
 *
 * @author limaofeng
 */
@Slf4j
public class NoSessionManager extends ManagerBase implements Lifecycle {
  @Override
  protected synchronized void startInternal() throws LifecycleException {
    super.startInternal();
    load();
    setState(LifecycleState.STARTING);
  }

  @Override
  protected synchronized void stopInternal() throws LifecycleException {
    setState(LifecycleState.STOPPING);
    unload();
    super.stopInternal();
  }

  @Override
  public void load() {
    log.info("HttpSession 已经关闭,若开启请配置：spring.tomcat.disableSession=false");
  }

  @Override
  public void unload() {}

  @Override
  public Session createSession(String sessionId) {
    return null;
  }

  @Override
  public Session createEmptySession() {
    return null;
  }

  @Override
  public void add(Session session) {}

  @Override
  public Session findSession(String id) {
    return null;
  }

  @Override
  public Session[] findSessions() {
    return null;
  }

  @Override
  public void processExpires() {}
}
