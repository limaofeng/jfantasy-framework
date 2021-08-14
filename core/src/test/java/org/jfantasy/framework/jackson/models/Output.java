package org.jfantasy.framework.jackson.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Builder;
import lombok.Data;

/**
 * @author limaofeng
 * @version V1.0
 * 
 * @date 2019-03-07 10:39
 */
@Data
public class Output<T> {
    private Message message;

    private T data;

    @Data
    public static class Message {
        private String result;
        private String description;
    }
}
