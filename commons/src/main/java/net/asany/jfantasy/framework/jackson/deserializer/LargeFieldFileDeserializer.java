/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.jackson.deserializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.asany.jfantasy.framework.util.common.DateUtil;
import net.asany.jfantasy.framework.util.common.StringUtil;
import net.asany.jfantasy.framework.util.common.file.FileUtil;

/**
 * 大字段文件反序列化<br>
 * 当 JSON 中包含文件字段时，避免序列化时将文件加载到内存 注意：使用时应该使用 inputStream 作为参数
 * JSON.deserialize(request.getInputStream(), Target.class)
 *
 * @author limaofeng
 */
public class LargeFieldFileDeserializer extends StdDeserializer<File> {

  public LargeFieldFileDeserializer() {
    super(File.class);
  }

  @Override
  public File deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
      throws IOException {
    Path path =
        Paths.get(
            FileUtil.tmpdir()
                + DateUtil.format("yyyy-MM-dd")
                + "/"
                + StringUtil.shortUUID()
                + ".json");
    File tempFile = FileUtil.createFile(path).toFile();
    try (FileOutputStream fos = new FileOutputStream(tempFile);
        JsonGenerator generator = jsonParser.getCodec().getFactory().createGenerator(fos)) {
      generator.copyCurrentStructure(jsonParser);
    }
    return tempFile;
  }
}
