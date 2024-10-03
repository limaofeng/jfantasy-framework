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
package net.asany.jfantasy.framework.util.common;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.demo.bean.Article;
import net.asany.jfantasy.demo.bean.User;
import net.asany.jfantasy.framework.util.reflect.Property;
import org.junit.jupiter.api.Test;

@Slf4j
public class ClassUtilTest {

  @Test
  public void testLoadClass() throws Exception {}

  @Test
  public void testGetBeanInfo() throws Exception {}

  @Test
  public void testNewInstance() throws Exception {}

  @Test
  public void testGetRealClass() throws Exception {}

  @Test
  public void testNewInstance1() throws Exception {}

  @Test
  public void testGetPropertys() throws Exception {
    // Property[] propertys = ClassUtil.getPropertys(Article.class);
    // for(Property property : propertys){
    // System.out.println(property.getReadMethod().getAnnotations());
    // }
  }

  @Test
  public void testGetProperty() throws Exception {
    Property property = ClassUtil.getProperty(Article.class, "attributeValues");
    ParameterizedType parameterizedType = property.getGenericType();
    for (Type type : parameterizedType.getActualTypeArguments()) {
      System.out.println(type);
    }
  }

  @Test
  public void testForName() throws Exception {
    String className = Array.newInstance(User.class, 0).getClass().getName();

    Class<?> clazz = ClassUtil.forName(className);

    assert clazz != null;
    log.debug(clazz.getName());

    assert clazz == Array.newInstance(User.class, 0).getClass();
  }

  @Test
  public void testGetValue() throws Exception {}

  @Test
  public void testGetDeclaredField() throws Exception {}

  @Test
  public void testSetValue() throws Exception {}

  @Test
  public void testGetMethodProxy() throws Exception {}

  @Test
  public void testIsBasicType() throws Exception {}

  @Test
  public void testIsArray() throws Exception {}

  @Test
  public void testIsInterface() throws Exception {}

  @Test
  public void testIsList() throws Exception {}

  @Test
  public void testIsMap() throws Exception {}

  @Test
  public void testIsList1() throws Exception {}

  @Test
  public void testGetSuperClassGenricType() throws Exception {}

  @Test
  public void testIsBeanType() throws Exception {
    // LOG.debug(ClassUtil.isBeanType(Sex.female.getClass()));
  }
}
