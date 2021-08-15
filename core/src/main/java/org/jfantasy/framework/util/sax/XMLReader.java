package org.jfantasy.framework.util.sax;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import lombok.SneakyThrows;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class XMLReader {

  private XMLReader() {
    throw new IllegalStateException("Utility class");
  }

  private static final Log LOGGER = LogFactory.getLog(XMLReader.class);

  @SneakyThrows
  public static XmlElement reader(InputStream input) {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(false);
    SAXParser saxParse = factory.newSAXParser();
    SaxXmlHandler handler = new SaxXmlHandler();
    saxParse.parse(input, handler);
    return handler.getElement();
  }

  @SneakyThrows
  public static XmlElement reader(String path) {
    File xmlFile = new File(path);
    LOGGER.info("开始解析XML文件：[" + path + "]");
    InputStream input = new FileInputStream(xmlFile);
    try {
      return reader(input);
    } finally {
      LOGGER.info("XML文件：[" + path + "]解析完成");
      input.close();
    }
  }

  public static String toJSON(XmlElement element) {
    String retVal = parse(element);
    LOGGER.info("将XML文件转为JSON：" + retVal);
    return retVal;
  }

  private static String parse(XmlElement element) {
    return "{" + element.getTagName() + ":" + attributeToString(element) + "}";
  }

  private static String attributeToString(XmlElement element) {
    StringBuilder buffer = new StringBuilder();
    Map<String, String> attributes = element.getAttribute();
    if (attributes.isEmpty() && element.getChildNodes().isEmpty()) {
      return "\"" + element.getContent() + "\"";
    } else {
      Iterator<String> iterator = attributes.keySet().iterator();
      buffer.append("{");
      while (iterator.hasNext()) {
        String key = iterator.next();
        buffer
            .append(key)
            .append(":")
            .append("\"")
            .append(attributes.get(key))
            .append("\"")
            .append(iterator.hasNext() ? "," : "");
      }
      if ((element.getChildNodes() != null) && (!element.getChildNodes().isEmpty())) {
        buffer.append(buffer.length() > 1 ? "," : "").append(subElementsToString(element));
      }
      buffer.append("}");
      return buffer.toString();
    }
  }

  private static String subElementsToString(XmlElement element) {
    StringBuilder buffer = new StringBuilder();
    List<XmlElement> list = element.getChildNodes();
    if (list != null) {
      Map<String, List<XmlElement>> map = new HashMap<>();
      for (XmlElement ele : list) {
        if (!map.containsKey(ele.getTagName())) {
          map.put(ele.getTagName(), new ArrayList<>());
        }
        List<XmlElement> nList = map.get(ele.getTagName());
        nList.add(ele);
      }
      Iterator<String> iterator = map.keySet().iterator();
      while (iterator.hasNext()) {
        String key = iterator.next();
        buffer.append(key).append(":");
        List<XmlElement> nList = map.get(key);
        for (int i = 0; i < nList.size(); i++) {
          buffer.append(i == 0 ? "" : ",").append(attributeToString(nList.get(i)));
        }
        buffer.append(iterator.hasNext() ? "," : "");
      }
    }
    return buffer.toString();
  }
}
