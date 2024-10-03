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
package net.asany.jfantasy.framework.error;

import java.io.Serializable;
import java.util.*;
import lombok.Data;
import net.asany.jfantasy.framework.util.common.DateUtil;

/**
 * @author limaofeng
 */
@Data
public class ErrorResponse implements Serializable {
  /** HTTP 状态码 */
  private int status;

  /** 错误发生时间 */
  private Date timestamp;

  /** 状态码错误说明 */
  private String error;

  /** 定义的具体错误码 */
  private String code;

  /** 错误消息 */
  private String message;

  /** 原始异常信息 */
  private String exception;

  /** 当验证错误时，各项具体的错误信息 */
  private List<FieldValidationError> fieldErrors = new ArrayList<>();

  /** 定义的返回数据 */
  private Map<String, Object> data = new HashMap<>();

  public ErrorResponse() {
    this.timestamp = DateUtil.now();
  }

  public void addFieldError(FieldValidationError error) {
    this.fieldErrors.add(error);
  }

  public void addDataValue(String key, Object value) {
    this.data.put(key, value);
  }

  public void addData(Map<String, Object> data) {
    this.data.putAll(data);
  }
}
