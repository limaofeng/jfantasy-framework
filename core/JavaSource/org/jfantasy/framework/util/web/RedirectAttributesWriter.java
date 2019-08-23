package org.jfantasy.framework.util.web;

import org.jfantasy.framework.dao.jpa.PropertyFilter;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

public class RedirectAttributesWriter {

    private final RedirectAttributes attrs;

    RedirectAttributesWriter(RedirectAttributes attrs) {
        this.attrs = attrs;
    }

    public RedirectAttributesWriter write(List<PropertyFilter> filters) {
        for (PropertyFilter filter : filters) {
            this.attrs.addAttribute(filter.getFilterName(), filter.getPropertyValue());
        }
        return this;
    }

    public static RedirectAttributesWriter writer(RedirectAttributes attrs) {
        return new RedirectAttributesWriter(attrs);
    }

}
