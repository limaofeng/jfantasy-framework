package org.jfantasy.filestore.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.jfantasy.filestore.File;

import java.io.IOException;

public class FileSerializer extends JsonSerializer<File> {

    @Override
    public void serialize(File file, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(file.getPath());
    }

}
