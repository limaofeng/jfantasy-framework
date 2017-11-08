package org.jfantasy.demo.rest;

import org.jfantasy.framework.jackson.annotation.BeanFilter;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 07/11/2017 9:00 PM
 */
@RestController
@RequestMapping("/demos")
public class DemoController {

    @GetMapping("/{id}")
    @JsonResultFilter({
            @BeanFilter(type = Demo.class, excludes = "id"),
            @BeanFilter(type = Tag.class, excludes = "name")
    })
    public Demo view(@PathVariable Long id) {
        Demo demo = new Demo();
        demo.setId(id);
        demo.setName("演示 - " + demo.getId());
        demo.setTag(new Tag(id, "Tag - " + id));
        return demo;
    }


}
