package org.jfantasy.security.bean.databind;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.jfantasy.security.bean.Role;

import java.io.IOException;

public class RoleSerializer extends JsonSerializer<Role> {

    @Override
    public void serialize(Role value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.getId() != null ? value.getId() : "");
    }

}
