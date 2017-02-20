package org.jfantasy.member.bean.databind;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.jfantasy.member.bean.TeamType;

import java.io.IOException;

public class TeamTypeSerializer extends JsonSerializer<TeamType> {

    @Override
    public void serialize(TeamType teamType, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        if (teamType == null) {
            jgen.writeNull();
        } else {
            jgen.writeString(teamType.getId());
        }
    }
}
