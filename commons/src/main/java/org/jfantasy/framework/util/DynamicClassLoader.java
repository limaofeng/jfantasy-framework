package org.jfantasy.framework.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import lombok.extern.slf4j.Slf4j;

/**
 * 动态类加载器
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2012-11-30 下午05:12:44
 */
@Slf4j
public class DynamicClassLoader extends ClassLoader {

  public DynamicClassLoader(ClassLoader parent) {
    super(parent);
  }

  public <T> Class<T> loadClass(String classPath, String className) {
    try {
      String url = classPathParser(classPath) + classNameParser(className);
      URL myUrl = new URL(url);
      URLConnection connection = myUrl.openConnection();
      InputStream input = connection.getInputStream();
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      int data = input.read();
      while (data != -1) {
        buffer.write(data);
        data = input.read();
      }
      input.close();
      byte[] classData = buffer.toByteArray();
      //noinspection unchecked
      return (Class<T>) defineClass(noSuffix(className), classData, 0, classData.length);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }

  public <T> Class<T> loadClass(byte[] classData, String className) throws ClassNotFoundException {
    //noinspection unchecked
    return (Class<T>) defineClass(noSuffix(className), classData, 0, classData.length);
  }

  private String pathParser(String path) {
    return path.replaceAll("\\\\", "/");
  }

  private String classPathParser(String path) {
    String classPath = pathParser(path);
    if (!classPath.startsWith("file:")) {
      classPath = "file:" + classPath;
    }
    if (!classPath.endsWith("/")) {
      classPath = classPath + "/";
    }
    return classPath;
  }

  private String classNameParser(String className) {
    return className.replaceAll("\\.", "/") + ".class";
  }

  private String noSuffix(String className) {
    return className.endsWith(".class")
        ? className.substring(0, className.lastIndexOf("."))
        : className;
  }
}
