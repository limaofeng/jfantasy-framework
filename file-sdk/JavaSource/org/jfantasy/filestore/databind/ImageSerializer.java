package org.jfantasy.filestore.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.jfantasy.filestore.Image;

import java.io.IOException;

public class ImageSerializer extends JsonSerializer<Image> {

    @Override
    public void serialize(Image image, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(image.getPath());
    }

}
