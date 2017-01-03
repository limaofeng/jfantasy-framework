package org.jfantasy.filestore.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.jfantasy.filestore.File;
import org.jfantasy.framework.util.common.ObjectUtil;

import java.io.IOException;

public class FilesSerializer extends JsonSerializer<File[]> {

    @Override
    public void serialize(File[] files, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeObject(ObjectUtil.toFieldArray(files, "path", String.class));
    }

}
