package org.jfantasy.framework.util.common.file;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.Set;
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
    FileSystem fileSystem = FileSystems.newFileSystem(Paths.get("/123123"), null);
    log.info(fileSystem.toString());
  }

  static class MyFileSystemProvider extends FileSystemProvider {

    @Override
    public String getScheme() {
      return null;
    }

    @Override
    public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
      return null;
    }

    @Override
    public FileSystem getFileSystem(URI uri) {
      return null;
    }

    @Override
    public Path getPath(URI uri) {
      return null;
    }

    @Override
    public SeekableByteChannel newByteChannel(
        Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs)
        throws IOException {
      return null;
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(
        Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
      return null;
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {}

    @Override
    public void delete(Path path) throws IOException {}

    @Override
    public void copy(Path source, Path target, CopyOption... options) throws IOException {}

    @Override
    public void move(Path source, Path target, CopyOption... options) throws IOException {}

    @Override
    public boolean isSameFile(Path path, Path path2) throws IOException {
      return false;
    }

    @Override
    public boolean isHidden(Path path) throws IOException {
      return false;
    }

    @Override
    public FileStore getFileStore(Path path) throws IOException {
      return null;
    }

    @Override
    public void checkAccess(Path path, AccessMode... modes) throws IOException {}

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

  static class MyFileSystem extends FileSystem {

    @Override
    public FileSystemProvider provider() {
      return null;
    }

    @Override
    public void close() throws IOException {}

    @Override
    public boolean isOpen() {
      return false;
    }

    @Override
    public boolean isReadOnly() {
      return false;
    }

    @Override
    public String getSeparator() {
      return "/";
    }

    @Override
    public Iterable<Path> getRootDirectories() {
      return null;
    }

    @Override
    public Iterable<FileStore> getFileStores() {
      return null;
    }

    @Override
    public Set<String> supportedFileAttributeViews() {
      return null;
    }

    @Override
    public Path getPath(String first, String... more) {
      return null;
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
}
