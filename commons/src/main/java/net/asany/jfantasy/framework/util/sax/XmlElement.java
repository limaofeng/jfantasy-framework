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

import java.util.*;

public class XmlElement extends Observable {

  public static final XmlElement Empty = new XmlElement();

  private String tagName;
  private String content;
  private Map<String, String> attribute;
  private List<XmlElement> childNodes;
  private XmlElement parent;

  public XmlElement() {}

  public XmlElement(String qName) {
    this.tagName = qName;
  }

  public String getTagName() {
    return this.tagName;
  }

  public void setTagName(String name) {
    this.tagName = name;
  }

  public String getContent() {
    return this.content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public List<XmlElement> getChildNodes() {
    return this.childNodes == null
        ? this.childNodes = new ArrayList<XmlElement>()
        : this.childNodes;
  }

  public void setChildNodes(List<XmlElement> childNodes) {
    this.childNodes = childNodes;
  }

  public XmlElement getParentElement() {
    return this.parent;
  }

  public void setParentElement(XmlElement parentElement) {
    this.parent = parentElement;
  }

  public Map<String, String> getAttribute() {
    return this.attribute == null ? this.attribute = new HashMap<String, String>() : this.attribute;
  }

  public void setAttribute(Map<String, String> attribute) {
    this.attribute = attribute;
  }

  public void addAttribute(String name, String value) {
    getAttribute().put(name, value);
  }

  public String getAttribute(String name) {
    return (String) getAttribute().get(name);
  }

  public void addElement(XmlElement element) {
    element.setParentElement(this);
    getChildNodes().add(element);
  }

  public void addElement(int index, XmlElement element) {
    element.setParentElement(this);
    getChildNodes().add(index, element);
  }

  public void remove(XmlElement element) {
    getChildNodes().remove(element);
  }

  public void remove() {
    if (getParentElement() != null) {
      getParentElement().remove(this);
    }
  }

  public List<XmlElement> getChildNodesByTagName(String tagName) {
    List<XmlElement> rev = new ArrayList<XmlElement>();
    List<XmlElement> childNodes = getChildNodes();
    for (XmlElement element : childNodes) {
      if (element.getTagName().equalsIgnoreCase(tagName)) {
        rev.add(element);
      }
    }
    return rev;
  }

  public XmlElement getChildNodeByTagName(String tagName) {
    List<XmlElement> childNodes = getChildNodes();
    for (XmlElement element : childNodes) {
      if (element.getTagName().equalsIgnoreCase(tagName)) {
        return element;
      }
    }
    return XmlElement.Empty;
  }
}
