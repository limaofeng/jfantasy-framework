package org.jfantasy.demo.rest;

import java.io.Serializable;
import java.util.List;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 07/11/2017 9:00 PM
 */
public class Demo implements Serializable {

    private Long id;
    private String name;
    private List<Tag> tas;
    private Tag tag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Tag> getTas() {
        return tas;
    }

    public void setTas(List<Tag> tas) {
        this.tas = tas;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
