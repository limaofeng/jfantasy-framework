package org.jfantasy.security.bean.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.jfantasy.security.bean.Organization;

import java.io.IOException;

public class OrgSerializer extends JsonSerializer<Organization> {
    @Override
    public void serialize(Organization organization, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(organization.getId() != null ? organization.getId() : "");
    }
}