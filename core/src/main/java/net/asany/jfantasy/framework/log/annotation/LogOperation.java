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
package net.asany.jfantasy.framework.log.annotation;

import lombok.Getter;
import org.springframework.util.Assert;

public class LogOperation {

  private String condition = "";
  private String type = "";
  @Getter private final String name = "";
  private String text = "";

  public String getCondition() {
    return condition;
  }

  public String getType() {
    return type;
  }

  public void setCondition(String condition) {
    Assert.notNull(condition, "condition is null");
    this.condition = condition;
  }

  public void setType(String type) {
    Assert.notNull(type, "type is null");
    this.type = type;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof LogOperation && toString().equals(other.toString());
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }

  @Override
  public String toString() {
    return getOperationDescription().toString();
  }

  protected StringBuilder getOperationDescription() {
    StringBuilder result = new StringBuilder();
    result.append(getClass().getSimpleName());
    result.append("[");
    result.append(this.text);
    result.append(" ] condition='");
    result.append(this.condition);
    result.append("' | type='");
    result.append(this.type);
    result.append("'");
    return result;
  }
}
