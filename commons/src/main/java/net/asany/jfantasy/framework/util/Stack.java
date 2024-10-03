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
package net.asany.jfantasy.framework.util;

import java.util.LinkedList;
import java.util.List;

/** 类名: Stack 描述: (先进后出) 作者: 李茂峰 创建时间: 2010-2-2 */
public class Stack<T> {
  private final LinkedList<T> list;

  public Stack() {
    list = new LinkedList<T>();
  }

  /** 从栈中取出元素 */
  public T pop() {
    return list.poll();
  }

  /**
   * 读取对象，但不取出
   *
   * @return T
   */
  public T peek() {
    return list.peek();
  }

  /**
   * 向栈添加元素
   *
   * @param o
   */
  public void push(T o) {
    list.addFirst(o);
  }

  /**
   * 判断栈 是否为空
   *
   * @return boolean
   */
  public boolean empty() {
    return list.isEmpty();
  }

  /** 清空栈 */
  public void clear() {
    list.clear();
  }

  public List<T> toList() {
    return this.list;
  }

  public int size() {
    return this.list.size();
  }
}
