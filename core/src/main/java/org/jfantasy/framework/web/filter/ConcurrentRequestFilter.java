package org.jfantasy.framework.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.util.LinkedBlockingQueue;
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
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
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
