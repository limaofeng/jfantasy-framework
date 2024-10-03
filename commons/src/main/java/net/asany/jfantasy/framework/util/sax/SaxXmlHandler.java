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
package net.asany.jfantasy.framework.util.sax;

import net.asany.jfantasy.framework.util.Stack;
import net.asany.jfantasy.framework.util.common.StringUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxXmlHandler extends DefaultHandler {
  private final Stack<XmlElement> stack = new Stack<>();
  private XmlElement RootElement;

  @Override
  public void startDocument() throws SAXException {
    super.startDocument();
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes)
      throws SAXException {
    XmlElement element = new XmlElement(qName);
    for (int i = 0; i < attributes.getLength(); i++) {
      element.addAttribute(attributes.getQName(i), attributes.getValue(i));
    }
    this.stack.push(element);

    super.startElement(uri, localName, qName, attributes);
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    String value = new String(ch, start, length);
    XmlElement element = (XmlElement) this.stack.peek();
    if (!"".equals(value.trim())) {
      element.setContent(StringUtil.nullValue(element.getContent()) + value.trim());
    }
    super.characters(ch, start, length);
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    XmlElement element = this.stack.pop();
    if (!this.stack.empty()) {
      this.RootElement = this.stack.peek();
      if (this.RootElement != null) {
        this.RootElement.addElement(element);
      }
    }
    super.endElement(uri, localName, qName);
  }

  @Override
  public void endDocument() throws SAXException {
    super.endDocument();
  }

  public XmlElement getElement() {
    return this.RootElement;
  }
}
