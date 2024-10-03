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
package net.asany.jfantasy.framework.util;

import java.util.List;
import net.asany.jfantasy.framework.dao.jpa.PropertyPredicate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class RedirectAttributesWriter {

  private final RedirectAttributes attrs;

  RedirectAttributesWriter(RedirectAttributes attrs) {
    this.attrs = attrs;
  }

  public RedirectAttributesWriter write(List<PropertyPredicate> filters) {
    for (PropertyPredicate filter : filters) {
      this.attrs.addAttribute(filter.getFilterName(), filter.getPropertyValue());
    }
    return this;
  }

  public static RedirectAttributesWriter writer(RedirectAttributes attrs) {
    return new RedirectAttributesWriter(attrs);
  }
}
