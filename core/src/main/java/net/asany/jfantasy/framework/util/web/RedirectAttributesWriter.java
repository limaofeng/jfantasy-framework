package net.asany.jfantasy.framework.util.web;

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
