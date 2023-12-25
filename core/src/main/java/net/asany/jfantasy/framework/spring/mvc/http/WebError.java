package net.asany.jfantasy.framework.spring.mvc.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.asany.jfantasy.framework.error.ErrorResponse;

/**
 * @author limaofeng
 * @version V1.0
 * @date 2020/3/22 4:43 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties("status")
public class WebError extends ErrorResponse {

  /** 对应浏览器状态 */
  private int status;

  private final String path;

  public WebError(Map<String, Object> error) {
    this.path = "path";
  }
}
