package org.jfantasy.framework.util.xml;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.jfantasy.framework.error.IgnoreException;

@Slf4j
public final class Dom4jUtil {

  private Dom4jUtil() {}

  public static Document reader(InputStream inputStream) {
    try {
      return new SAXReader().read(inputStream);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new IgnoreException(e.getMessage());
    }
  }

  public static Document reader(URL url) {
    try {
      return new SAXReader().read(url);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new IgnoreException(e.getMessage());
    }
  }

  @SuppressWarnings("unchecked")
  public static void readNode(Element root, String prefix) {
    if (root == null) {
      return;
    }
    // 获取属性
    List<Attribute> attrs = root.attributes();
    if (attrs != null && !attrs.isEmpty()) {
      log.error(prefix);
      for (Attribute attr : attrs) {
        log.error(attr.getValue() + " ");
      }
    }
    // 获取他的子节点
    List<Element> childNodes = root.elements();
    prefix += "\t";
    for (Element e : childNodes) {
      readNode(e, prefix);
    }
  }

  public static class MyVistor extends VisitorSupport {
    @Override
    public void visit(Attribute node) {
      log.debug("Attibute: " + node.getName() + "=" + node.getValue());
    }

    @Override
    public void visit(Element node) {
      if (node.isTextOnly()) {
        log.debug("Element: " + node.getName() + "=" + node.getText());
      } else {
        log.debug("root:" + node.getName());
      }
    }

    @Override
    public void visit(ProcessingInstruction node) {
      log.debug("PI:" + node.getTarget() + " " + node.getText());
    }
  }
}
