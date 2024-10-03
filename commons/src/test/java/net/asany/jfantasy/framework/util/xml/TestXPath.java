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

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Slf4j
public class TestXPath {

  @Test
  public void read() {
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = dbf.newDocumentBuilder();
      InputStream in = TestXPath.class.getResourceAsStream("test.xml");
      Document doc = builder.parse(in);
      XPathFactory factory = XPathFactory.newInstance();
      XPath xpath = factory.newXPath();
      // 选取所有class元素的name属性
      // XPath语法介绍： http://w3school.com.cn/xpath/
      XPathExpression expr = xpath.compile("//class/@name");
      NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
      for (int i = 0; i < nodes.getLength(); i++) {
        log.debug("name = " + nodes.item(i).getNodeValue());
      }
    } catch (XPathExpressionException e) {
      log.debug(e.getMessage(), e);
    } catch (ParserConfigurationException e) {
      log.debug(e.getMessage(), e);
    } catch (SAXException e) {
      log.debug(e.getMessage(), e);
    } catch (IOException e) {
      log.debug(e.getMessage(), e);
    }
  }
}
