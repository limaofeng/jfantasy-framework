package com.thuni.his.demo.graphql.inputs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.jfantasy.framework.dao.jpa.PropertyFilter;
import org.jfantasy.framework.dao.jpa.PropertyFilterBuilder;

import java.util.List;

@Data
public class UserFilter {

    private PropertyFilterBuilder builder = new PropertyFilterBuilder();

    @JsonProperty("username_contains")
    public void setUsernameContains(String value) {
        builder.contains("username", value);
    }

    public List<PropertyFilter> build() {
        return this.builder.build();
    }

}
