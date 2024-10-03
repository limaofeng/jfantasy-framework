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
package net.asany.jfantasy.framework.util.common.file;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author limaofeng
 * @version V1.0
 */
@Slf4j
public class FileUtilTest {

  @Test
  public void createFolder() {
    System.out.println(new File("/tmp/a/b/c/d").mkdirs());
  }

  @Test
  void getMimeType() throws IOException {
    File file = new File("/Users/limaofeng/Workspace/framework/core/src/test/resources/banner.txt");
    String mimeType = FileUtil.getMimeType(file);
    System.out.println(mimeType);
    System.out.println(Files.probeContentType(file.toPath()));
  }

  @Test
  void mkdir() throws IOException {
    log.info(FileUtil.mkdirs(Paths.get("/tmp/a/b/c/d")).toString());
  }

  @Test
  public void fileSize() {
    System.out.println(FileUtil.bytesToSize(1024 + 1024 + 100));
    System.out.println(FileUtil.bytesToSize(2097152));
  }

  @Test
  void createFile() throws IOException {
    Path path = FileUtil.createFile(Paths.get("/tmp/a/b/c/d.txt"));
    log.info(path.toString());
  }

  @Test
  void getName() {
    Path path = Paths.get("/tmp/a/b/c/d.txt");
    log.info(path.getFileName().toString());
  }

  @Test
  void fileInfo() {
    Path path = Paths.get("/tmp/a/b/c/d.txt");
    log.info(path.getRoot().toString());
  }

  @Test
  void createDel() throws IOException {
    Path path1 = Paths.get("/tmp/a/b/c/d.txt");
    Path path = FileUtil.createFile(path1);
    Assertions.assertTrue(Files.exists(path));
    FileUtil.rm(path);
    Assertions.assertFalse(Files.exists(path));
    path = FileUtil.createFile(path1);
    Assertions.assertTrue(Files.exists(path));
    path = Paths.get("/tmp/a");
    FileUtil.rm(path, true);
    Assertions.assertFalse(Files.exists(path));
  }

  @Test
  void zip() throws IOException {
    FileUtil.createFile(Paths.get("/tmp/a/b/c/d.txt"));
    Path path = Paths.get("/tmp/a");
    FileUtil.zip(path, Paths.get("/tmp/a.zip"));
    //    FileUtil.delete(path, true);
    Paths.get("/tmp/a.zip");
  }

  @Test
  void fileSystem() throws IOException {

    MyFileSystemProvider provider = new MyFileSystemProvider();
    URI uri = URI.create("myfs://test.txt"); // 自定义文件系统的 URI
    //    Map<String, ?> env = Collections.emptyMap(); // 可选的环境参数

    //    FileSystem fs = provider.newFileSystem(uri, env);

    Path file = Paths.get(uri); // 通过 URI 获取文件
    //    Files.createFile(file);
    Files.write(file, "".getBytes());
  }
}
