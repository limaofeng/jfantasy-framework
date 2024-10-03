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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Fantasy 动态类加载器。
 *
 * @author 李茂峰
 * @version 1.0 用于封装DynamicClassLoader加载器<br>
 *     如果一个类被多次修改并加载的话。需要不断创建新的加载器
 * @since 2012-11-6 下午10:33:48
 */
public class FantasyClassLoader extends ClassLoader {

  private final Lock lock = new ReentrantLock();

  private static final FantasyClassLoader fantasyClassLoader = new FantasyClassLoader();
  //      AccessController.doPrivileged((PrivilegedAction<FantasyClassLoader>)
  // FantasyClassLoader::new);

  private DynamicClassLoader classLoader =
      new DynamicClassLoader(DynamicClassLoader.class.getClassLoader());

  //      AccessController.doPrivileged(
  //          (PrivilegedAction<DynamicClassLoader>)
  //              () -> new DynamicClassLoader(DynamicClassLoader.class.getClassLoader()));

  /** 已被加载的对象 */
  private static final List<String> loadClasses = new ArrayList<>();

  /** 已被卸载的对象 */
  private static final List<String> unloadClasses = new ArrayList<>();

  /**
   * 从文件加载类
   *
   * @param classpath classpath
   * @param classname classname
   * @return Class
   * @throws ClassNotFoundException 类不存在
   */
  public <T> Class<T> loadClass(String classpath, String classname) throws ClassNotFoundException {
    try {
      lock.lock();
      if (loadClasses.contains(classname)) { // 如果对象已经被加载
        classLoader = new DynamicClassLoader(classLoader); // 创建新加载器
        loadClasses.clear();
      }
      loadClasses.add(classname);
      unloadClasses.remove(classname);
      return classLoader.loadClass(classpath, classname);
    } finally {
      lock.unlock();
    }
  }

  /**
   * 通过字节码加载类
   *
   * @param classData classData
   * @param classname classname
   * @return Class
   */
  public <T> Class<T> loadClass(byte[] classData, String classname) throws ClassNotFoundException {
    try {
      lock.lock();
      if (loadClasses.contains(classname)) { // 如果对象已经被加载
        classLoader = new DynamicClassLoader(classLoader); // 创建新加载器
        loadClasses.clear();
      }
      loadClasses.add(classname);
      unloadClasses.remove(classname);
      return (Class<T>) classLoader.loadClass(classData, classname);
    } finally {
      lock.unlock();
    }
  }

  /**
   * 自动加载类
   *
   * @param classname 类路径
   * @return Class
   */
  @Override
  public Class<?> loadClass(String classname) throws ClassNotFoundException {
    if (unloadClasses.contains(classname)) {
      throw new ClassNotFoundException(classname);
    }
    return classLoader.loadClass(classname);
  }

  public static FantasyClassLoader getClassLoader() {
    return fantasyClassLoader;
  }

  public void removeClass(String className) {
    unloadClasses.add(className);
  }
}
