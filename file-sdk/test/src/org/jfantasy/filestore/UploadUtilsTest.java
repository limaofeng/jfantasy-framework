package org.jfantasy.filestore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.options.Option;
import com.mashape.unirest.http.options.Options;
import org.jfantasy.framework.jackson.JSON;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class UploadUtilsTest {

    static {
        Object object = Options.getOption(Option.OBJECT_MAPPER);
        if (object == null) {
            Unirest.setObjectMapper(new ObjectMapper() {
                @Override
                public <T> T readValue(String value, Class<T> valueType) {
                    try {
                        return JSON.getObjectMapper().readValue(value, valueType);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public String writeValue(Object value) {
                    try {
                        return JSON.getObjectMapper().writeValueAsString(value);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            });
        }
    }

    @Test
    public void uploadImage() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("url", "http://wx.qlogo.cn/mmopen/BO1qQiajiacVllMEeRQibWyY3OzmHLv5OIQzaPKzqfIgD5unW8HDkDAZXOiaNvtAVic8SomJDL4VKyUVqjrYvT6ZK8XlqSwFcR2sc/0");
        data.put("dir", "avatar");
        HttpResponse<Image> response = Unirest.post("http://114.55.142.155:8000/files").queryString(data).asObject(Image.class);
        System.out.println(response.getBody());
    }

}