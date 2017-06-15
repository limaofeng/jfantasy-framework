package org.jfantasy.filestore.databind;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.jfantasy.filestore.Image;
import org.jfantasy.framework.util.common.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImagesDeserializer extends JsonDeserializer<Image[]> {

    @Override
    public Image[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String values = jp.getValueAsString();
        if (StringUtil.isBlank(values)) {
            return new Image[0];
        }
        List<Image> images = new ArrayList<>();
        for (String value : StringUtil.tokenizeToStringArray(values)) {
            Image image = ImageDeserializer.getFile(value);
            if (image == null) {
                continue;
            }
            images.add(image);
        }
        return images.toArray(new Image[images.size()]);
    }

}
