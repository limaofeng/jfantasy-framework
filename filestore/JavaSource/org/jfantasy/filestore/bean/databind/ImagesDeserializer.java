package org.jfantasy.filestore.bean.databind;


import org.jfantasy.filestore.bean.FileDetail;
import org.jfantasy.filestore.bean.Image;
import org.jfantasy.filestore.service.FileService;
import org.jfantasy.framework.spring.SpringContextUtil;
import org.jfantasy.framework.util.common.StringUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImagesDeserializer extends JsonDeserializer<Image[]> {

    private static FileService fileService;

    @Override
    public Image[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String values = jp.getValueAsString();
        if (StringUtil.isBlank(values)) {
            return new Image[0];
        }
        List<Image> images = new ArrayList<>();
        for (String value : StringUtil.tokenizeToStringArray(values)) {
            String[] arry = value.split(":");
            FileDetail fileDetail = getFileService().get(arry[0]);
            if (fileDetail == null) {
                continue;
            }
            images.add(new Image(fileDetail));
        }
        return images.toArray(new Image[images.size()]);
    }

    private static FileService getFileService() {
        if (fileService == null) {
            fileService = SpringContextUtil.getBeanByType(FileService.class);
        }
        return fileService;
    }

}
