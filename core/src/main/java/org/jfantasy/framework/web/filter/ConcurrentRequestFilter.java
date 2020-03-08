package org.jfantasy.framework.web.filter;


import org.jfantasy.framework.util.LinkedBlockingQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentRequestFilter extends OncePerRequestFilter {

    private static final Log LOG = LogFactory.getLog(ConcurrentRequestFilter.class);

    private LinkedBlockingQueue<Lock> locks = new LinkedBlockingQueue<>();

    public ConcurrentRequestFilter(int locksNumber) {
        LOG.debug("初始化[" + locksNumber + "]把锁,用于限制请求并发");
        for (int i = 0; i < locksNumber; i++) {
            try {
                locks.put(new ReentrantLock());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
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
