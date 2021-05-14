package org.jfantasy.framework.spring.mvc.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties("status")
public class WebError extends ErrorResponse {

    /**
     * 对应浏览器状态
     */
    private int status;

    private final String path;

    public WebError(HttpServletRequest request) {
        this.path = request.getRequestURI();
    }

}
