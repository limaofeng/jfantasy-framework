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
package net.asany.jfantasy.framework.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.util.LinkedBlockingQueue;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class ConcurrentRequestFilter extends OncePerRequestFilter {

  private final LinkedBlockingQueue<Lock> locks = new LinkedBlockingQueue<>();

  public ConcurrentRequestFilter(int locksNumber) {
    log.debug("初始化[" + locksNumber + "]把锁,用于限制请求并发");
    for (int i = 0; i < locksNumber; i++) {
      try {
        locks.put(new ReentrantLock());
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  @Override
  protected void doFilterInternal(
      @NotNull HttpServletRequest request,
      @NotNull HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {
    try {
      Lock lock = locks.take();
      try {
        filterChain.doFilter(request, response);
      } finally {
        lock.unlock();
        locks.put(lock);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
