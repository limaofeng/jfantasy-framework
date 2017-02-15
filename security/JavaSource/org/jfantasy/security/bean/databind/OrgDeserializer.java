package org.jfantasy.security.bean.databind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.security.bean.Organization;

import java.io.IOException;

public class OrgDeserializer extends JsonDeserializer<Organization> {

    @Override
    public Organization deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        if (StringUtil.isBlank(jp.getValueAsString())) {
            return null;
        }
        return new Organization(jp.getValueAsString());
    }

}