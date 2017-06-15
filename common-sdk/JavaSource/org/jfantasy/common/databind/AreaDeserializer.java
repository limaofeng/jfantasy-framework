package org.jfantasy.common.databind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.autoconfigure.ApiGatewaySettings;
import org.jfantasy.common.Area;
import org.jfantasy.framework.spring.SpringContextUtil;
import org.jfantasy.framework.util.common.StringUtil;

import java.io.IOException;

public class AreaDeserializer extends JsonDeserializer<Area> {

    private static final Log LOG = LogFactory.getLog(AreaDeserializer.class);

    @Override
    public Area deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String id = jp.getValueAsString();
        return StringUtil.isNotBlank(id) ? getArea(id) : null;
    }

    private Area getArea(String id) {
        ApiGatewaySettings apiGatewaySettings = SpringContextUtil.getBeanByType(ApiGatewaySettings.class);
        assert apiGatewaySettings != null;
        try {
            HttpResponse<Area> response = Unirest.get(apiGatewaySettings.getUrl() + "/areas/" + id).asObject(Area.class);
            if (response.getStatus() == 404) {
                return null;
            }
            return response.getBody();
        }catch (UnirestException e){
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

}
