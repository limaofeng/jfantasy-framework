package org.jfantasy.framework.util.json.mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties("name")
public interface MixInUser {

}
