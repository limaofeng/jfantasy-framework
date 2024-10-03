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
package net.asany.jfantasy.framework.util.asm;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AnnotationDescriptor {

  private final Class<? extends Annotation> type;

  private Map<String, Object> elements = new HashMap<>();

  public AnnotationDescriptor(Class<? extends Annotation> annotationType) {
    type = annotationType;
  }

  public void setValue(String elementName, Object value) {
    elements.put(elementName, value);
  }

  public Object valueOf(String elementName) {
    return elements.get(elementName);
  }

  public boolean containsElement(String elementName) {
    return elements.containsKey(elementName);
  }

  public Set<String> keys() {
    return elements.keySet();
  }

  public int numberOfElements() {
    return elements.size();
  }

  public Class<? extends Annotation> type() {
    return type;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(Class<? extends Annotation> type) {
    return new Builder(type);
  }

  public static class Builder {

    private Class<? extends Annotation> type;
    private final Map<String, Object> elements = new HashMap<>();

    private Builder() {}

    private Builder(Class<? extends Annotation> type) {
      this.type = type;
    }

    public AnnotationDescriptor.Builder type(Class<? extends Annotation> clas) {
      this.type = clas;
      return this;
    }

    public AnnotationDescriptor.Builder setValue(String key, String value) {
      this.elements.put(key, value);
      return this;
    }

    public AnnotationDescriptor build() {
      AnnotationDescriptor descriptor = new AnnotationDescriptor(this.type);
      descriptor.elements = elements;
      return descriptor;
    }
  }
}
