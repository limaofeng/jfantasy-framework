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

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import org.junit.jupiter.api.Test;

public class DynamicCompilerExample {

  @Test
  public void javac() throws Exception {
    // 定义一个简单的 Java 类
    String className = "HelloWorld";
    String sourceCode =
        "public class "
            + className
            + " {\n"
            + "    public void sayHello() {\n"
            + "        System.out.println(\"Hello, world!\");\n"
            + "    }\n"
            + "}";

    // 创建一个 Java 文件，并将源代码写入该文件中
    File javaFile = new File(className + ".java");
    javaFile.createNewFile();
    Files.write(javaFile.toPath(), sourceCode.getBytes());

    // 使用 Java Compiler API 编译 Java 文件
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    int result = compiler.run(null, null, null, javaFile.getPath());
    if (result != 0) {
      System.err.println("Compilation failed.");
      return;
    }

    // 使用 URLClassLoader 动态加载编译后的 Java 类
    URLClassLoader classLoader =
        URLClassLoader.newInstance(new URL[] {new File(".").toURI().toURL()});
    Class<?> clazz = Class.forName(className, true, classLoader);

    // 使用 Reflection API 创建实例并调用方法
    Object instance = clazz.getDeclaredConstructor().newInstance();
    clazz.getMethod("sayHello").invoke(instance);
  }
}
