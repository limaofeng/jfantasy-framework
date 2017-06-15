package org.jfantasy.filestore.databind;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.autoconfigure.ApiGatewaySettings;
import org.jfantasy.filestore.Image;
import org.jfantasy.framework.spring.SpringContextUtil;
import org.jfantasy.framework.util.common.StringUtil;

import java.io.IOException;

public class ImageDeserializer extends JsonDeserializer<Image> {

    private static final Log LOG = LogFactory.getLog(ImageDeserializer.class);

    @Override
    public Image deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String value = jp.getValueAsString();
        if (StringUtil.isBlank(value)) {
            return null;
        }
        return getFile(value);
    }

    public static Image getFile(String path) {
        ApiGatewaySettings apiGatewaySettings = SpringContextUtil.getBeanByType(ApiGatewaySettings.class);
        assert apiGatewaySettings != null;
        try {
            HttpResponse<Image> response = Unirest.get(apiGatewaySettings.getUrl() + "/files?path=" + path).asObject(Image.class);
            if (response.getStatus() == 404) {
                return null;
            }
            return response.getBody();
        } catch (UnirestException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

}
