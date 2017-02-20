package org.jfantasy.member.bean.databind;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.member.bean.TeamType;

import java.io.IOException;

public class TeamTypeDeserializer extends JsonDeserializer<TeamType> {

    @Override
    public TeamType deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String value = jp.getValueAsString();
        if (StringUtil.isBlank(value)) {
            return null;
        }
        TeamType teamType = new TeamType();
        teamType.setId(value);
        return teamType;
    }

}
