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
package net.asany.jfantasy.framework.util.xml;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.error.IgnoreException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

@Slf4j
public class JdomUtil {
  private JdomUtil() {}

  public static Document reader(String filePath) {
    return reader(new File(filePath));
  }

  public static Document reader(File file) {
    try {
      return reader(Files.newInputStream(file.toPath()));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new IgnoreException(e.getMessage());
    }
  }

  public static Document reader(InputStream inputStream) {
    try {
      return new SAXBuilder().build(inputStream);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new IgnoreException(e.getMessage());
    }
  }

  public static Document reader(URL url) {
    try {
      return new SAXBuilder().build(url);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new IgnoreException(e.getMessage());
    }
  }

  public static Element parse(InputStream inputStream, Parser parser) {
    Document doc = reader(inputStream);
    Element root = doc.getRootElement();
    each(root, parser);
    return root;
  }

  public static void parse(String filePath, Parser parser) {
    Document doc = reader(filePath);
    Element root = doc.getRootElement();
    each(root, parser);
  }

  private static void each(Element ele, Parser parser) {
    for (Element element : ele.getChildren()) {
      parser.callBack(element.getName(), element);
      each(element, parser);
    }
  }

  public interface Parser {
    void callBack(String paramString, Element paramElement);
  }
}
