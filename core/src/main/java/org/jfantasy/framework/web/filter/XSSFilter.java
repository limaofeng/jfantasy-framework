package org.jfantasy.framework.web.filter;

import org.jfantasy.framework.web.filter.wrapper.XSSRequestWrapper;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class XSSFilter extends GenericFilterBean {

    /**
     * 是否转编码GET请求的参数 8859_1 => request.getCharacterEncoding()
     */
    private boolean transform = false;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new XSSRequestWrapper((HttpServletRequest) request, this.transform), response);
    }

    public void setTransform(boolean transform) {
        this.transform = transform;
    }

}
