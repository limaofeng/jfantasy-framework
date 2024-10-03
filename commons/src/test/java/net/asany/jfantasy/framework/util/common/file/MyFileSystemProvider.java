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
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.Set;

public class MyFileSystemProvider extends FileSystemProvider {

  @Override
  public String getScheme() {
    return "myfs";
  }

  @Override
  public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
    return new MyFileSystem(Paths.get(uri), this);
  }

  @Override
  public FileSystem getFileSystem(URI uri) {
    return new MyFileSystem(new MyPath(), this);
  }

  @Override
  public Path getPath(URI uri) {
    return new MyPath(uri, getFileSystem(uri));
  }

  @Override
  public SeekableByteChannel newByteChannel(
      Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
    return new MySeekableByteChannel();
  }

  @Override
  public DirectoryStream<Path> newDirectoryStream(
      Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
    return null;
  }

  @Override
  public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {}

  @Override
  public void delete(Path path) throws IOException {
    System.out.println("delete");
  }

  @Override
  public void copy(Path source, Path target, CopyOption... options) throws IOException {
    System.out.println("copy");
  }

  @Override
  public void move(Path source, Path target, CopyOption... options) throws IOException {
    System.out.println("move");
  }

  @Override
  public boolean isSameFile(Path path, Path path2) throws IOException {
    System.out.println("isSameFile");
    return false;
  }

  @Override
  public boolean isHidden(Path path) throws IOException {
    System.out.println("isHidden");
    return false;
  }

  @Override
  public FileStore getFileStore(Path path) throws IOException {
    System.out.println("getFileStore");
    return null;
  }

  @Override
  public void checkAccess(Path path, AccessMode... modes) throws IOException {
    System.out.println("checkAccess");
  }

  @Override
  public <V extends FileAttributeView> V getFileAttributeView(
      Path path, Class<V> type, LinkOption... options) {
    return null;
  }

  @Override
  public <A extends BasicFileAttributes> A readAttributes(
      Path path, Class<A> type, LinkOption... options) throws IOException {
    return null;
  }

  @Override
  public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options)
      throws IOException {
    return null;
  }

  @Override
  public void setAttribute(Path path, String attribute, Object value, LinkOption... options)
      throws IOException {}
}
