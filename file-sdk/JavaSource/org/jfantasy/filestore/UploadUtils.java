package org.jfantasy.filestore;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.autoconfigure.ApiGatewaySettings;
import org.jfantasy.framework.spring.SpringContextUtil;

import java.util.HashMap;
import java.util.Map;

public class UploadUtils {

    private static final Log LOG = LogFactory.getLog(UploadUtils.class);

    public static Image uploadImage(final String url,final String dir) {
        Map<String, Object> data = new HashMap<>();
        data.put("url", url);
        data.put("dir", dir);
        try {
            ApiGatewaySettings apiGatewaySettings = SpringContextUtil.getBeanByType(ApiGatewaySettings.class);
            assert apiGatewaySettings != null;
            HttpResponse<Image> response = Unirest.post(apiGatewaySettings.getUrl() + "/files").queryString(data).asObject(Image.class);
            return response.getBody();
        } catch (UnirestException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }


}
