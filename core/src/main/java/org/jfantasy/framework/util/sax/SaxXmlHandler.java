package org.jfantasy.framework.util.sax;

import org.jfantasy.framework.util.Stack;
import org.jfantasy.framework.util.common.StringUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxXmlHandler extends DefaultHandler {
  private Stack<XmlElement> stack = new Stack<>();
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
