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

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.Set;

public class MyFileSystem extends FileSystem {
  private final Path rootPath;
  private final MyFileSystemProvider provider;

  public MyFileSystem(Path rootPath, MyFileSystemProvider provider) {
    this.rootPath = rootPath;
    this.provider = provider;
  }

  @Override
  public FileSystemProvider provider() {
    return provider;
  }

  @Override
  public void close() throws IOException {
    // 实现自定义的关闭逻辑
  }

  @Override
  public boolean isOpen() {
    // 实现自定义的 isOpen() 逻辑
    return true;
  }

  @Override
  public boolean isReadOnly() {
    // 实现自定义的 isReadOnly() 逻辑
    return false;
  }

  @Override
  public String getSeparator() {
    // 实现自定义的 getSeparator() 逻辑
    return "/";
  }

  @Override
  public Iterable<Path> getRootDirectories() {
    // 实现自定义的 getRootDirectories() 逻辑
    return Collections.singletonList(rootPath);
  }

  @Override
  public Iterable<FileStore> getFileStores() {
    // 实现自定义的 getFileStores() 逻辑
    return Collections.emptyList();
  }

  @Override
  public Set<String> supportedFileAttributeViews() {
    // 实现自定义的 supportedFileAttributeViews() 逻辑
    return Collections.singleton("basic");
  }

  @Override
  public Path getPath(String first, String... more) {
    // 实现自定义的 getPath() 逻辑
    return rootPath.resolve(Paths.get(first, more));
  }

  @Override
  public PathMatcher getPathMatcher(String syntaxAndPattern) {
    return null;
  }

  @Override
  public UserPrincipalLookupService getUserPrincipalLookupService() {
    return null;
  }

  @Override
  public WatchService newWatchService() throws IOException {
    return null;
  }
}
