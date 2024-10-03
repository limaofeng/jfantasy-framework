/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
