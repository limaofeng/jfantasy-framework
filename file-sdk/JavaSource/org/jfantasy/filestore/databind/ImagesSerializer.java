package org.jfantasy.filestore.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.jfantasy.filestore.Image;
import org.jfantasy.framework.util.common.ObjectUtil;

import java.io.IOException;

public class ImagesSerializer extends JsonSerializer<Image[]> {

    @Override
    public void serialize(Image[] images, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeObject(ObjectUtil.toFieldArray(images, "path", String.class));
    }

}
