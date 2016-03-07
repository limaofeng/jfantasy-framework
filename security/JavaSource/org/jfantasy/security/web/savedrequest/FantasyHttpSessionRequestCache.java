package org.jfantasy.security.web.savedrequest;

import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.web.WebUtil;
import org.springframework.security.web.PortResolver;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 替换默认的HttpSessionRequestCache
 * 
 *  ajax请求的时候不要记录请求地址
 * @author 李茂峰
 * @since 2013-9-10 上午9:07:21
 * @version 1.0
 */
public class FantasyHttpSessionRequestCache extends HttpSessionRequestCache {

	static final String SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST";

	private PortResolver portResolver = new PortResolverImpl();
	private boolean createSessionAllowed = true;
	private RequestMatcher requestMatcher = AnyRequestMatcher.INSTANCE;

	@Override
	public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
		if (requestMatcher.matches(request)) {
			DefaultSavedRequest savedRequest = new DefaultSavedRequest(request, portResolver);
			if (createSessionAllowed || request.getSession(false) != null) {
				request.getSession().setAttribute(SAVED_REQUEST, savedRequest);
				logger.debug("DefaultSavedRequest added to Session: " + savedRequest);
			}
			if (WebUtil.isAjax(request)) {
				String refererUrl = savedRequest.getHeaderValues("Referer").get(0);
				if (ObjectUtil.isNotNull(refererUrl)) {
					ClassUtil.setValue(savedRequest, "requestURL", refererUrl);
					ClassUtil.setValue(savedRequest, "requestURI", refererUrl.replaceFirst(WebUtil.getServerUrl(request, ""), ""));
					String[] refererUrls = refererUrl.split(StringUtil.nullValue(ClassUtil.getValue(savedRequest, "contextPath")));
					if (refererUrls.length > 1) {
						refererUrl = refererUrls[1];
						ClassUtil.setValue(savedRequest, "servletPath", refererUrl);
						ClassUtil.setValue(savedRequest, "requestURI", StringUtil.nullValue(ClassUtil.getValue(savedRequest, "contextPath")) + refererUrl);
					}
				}
                logger.debug(savedRequest);
			}
		} else {
			logger.debug("Request not saved as configured RequestMatcher did not match");
		}
	}

    @Override
    public void setCreateSessionAllowed(boolean createSessionAllowed) {
        this.createSessionAllowed = createSessionAllowed;
    }

    @Override
    public void setRequestMatcher(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
    }

    @Override
    public void setPortResolver(PortResolver portResolver) {
        this.portResolver = portResolver;
    }
}
