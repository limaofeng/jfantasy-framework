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
package net.asany.jfantasy.framework.spring.mvc.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * 绑定JSON/自定义 数据到 Map
 *
 * <p>默认自定义的MethodArgumentResolver是放在预定义之后，而且如果我们使用Map接收时，会自动绑定到Model上。 请参考<a href=
 * "http://jinnianshilongnian.iteye.com/blog/1698916">SpringMVC强大的数据绑定（1）——第六章
 * 注解式控制器详解——跟着开涛学SpringMVC</a> 第六部分：Model、Map、ModelMap
 *
 * <p>期待springmvc未来版本可以自定义参数解析器顺序
 *
 * @author Zhang Kaitao
 * @param <K>
 * @param <V>
 */
@Setter
@Getter
public class MapWapper<K, V> {

  private Map<K, V> innerMap = new HashMap<>();

  public void clear() {
    innerMap.clear();
  }

  public boolean containsKey(Object key) {
    return innerMap.containsKey(key);
  }

  public boolean containsValue(Object value) {
    return innerMap.containsValue(value);
  }

  public Set<Map.Entry<K, V>> entrySet() {
    return innerMap.entrySet();
  }

  @Override
  public boolean equals(Object o) {
    return innerMap.equals(o);
  }

  public V get(Object key) {
    return innerMap.get(key);
  }

  @Override
  public int hashCode() {
    return innerMap.hashCode();
  }

  public boolean isEmpty() {
    return innerMap.isEmpty();
  }

  public Set<K> keySet() {
    return innerMap.keySet();
  }

  public V put(K key, V value) {
    return innerMap.put(key, value);
  }

  public void putAll(Map<? extends K, ? extends V> m) {
    innerMap.putAll(m);
  }

  public V remove(Object key) {
    return innerMap.remove(key);
  }

  public int size() {
    return innerMap.size();
  }

  public Collection<V> values() {
    return innerMap.values();
  }

  @Override
  public String toString() {
    return innerMap.toString();
  }
}
