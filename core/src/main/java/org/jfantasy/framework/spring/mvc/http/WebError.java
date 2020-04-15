package org.jfantasy.framework.spring.mvc.http;

import lombok.Data;
import org.jfantasy.framework.error.ErrorResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2020/3/22 4:43 下午
 */
@Data
public class WebError extends ErrorResponse {

    private final String path;

    public WebError(HttpServletRequest request){
        this.path = request.getRequestURI();
    }

}