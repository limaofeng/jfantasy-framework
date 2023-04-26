package org.jfantasy.framework.util.common.file;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Iterator;

public class MyPath implements Path {

  private URI uri;
  private FileSystem fileSystem;

  public MyPath(URI uri, FileSystem fileSystem) {
    this.uri = uri;
    this.fileSystem = fileSystem;
  }

  public MyPath() {}

  @Override
  public FileSystem getFileSystem() {
    return fileSystem;
  }

  @Override
  public boolean isAbsolute() {
    return false;
  }

  @Override
  public Path getRoot() {
    return null;
  }

  @Override
  public Path getFileName() {
    return null;
  }

  @Override
  public Path getParent() {
    return null;
  }

  @Override
  public int getNameCount() {
    return 0;
  }

  @Override
  public Path getName(int index) {
    return null;
  }

  @Override
  public Path subpath(int beginIndex, int endIndex) {
    return null;
  }

  @Override
  public boolean startsWith(Path other) {
    return false;
  }

  @Override
  public boolean startsWith(String other) {
    return false;
  }

  @Override
  public boolean endsWith(Path other) {
    return false;
  }

  @Override
  public boolean endsWith(String other) {
    return false;
  }

  @Override
  public Path normalize() {
    return null;
  }

  @Override
  public Path resolve(Path other) {
    return null;
  }

  @Override
  public Path resolve(String other) {
    return null;
  }

  @Override
  public Path resolveSibling(Path other) {
    return null;
  }

  @Override
  public Path resolveSibling(String other) {
    return null;
  }

  @Override
  public Path relativize(Path other) {
    return null;
  }

  @Override
  public URI toUri() {
    return null;
  }

  @Override
  public Path toAbsolutePath() {
    return null;
  }

  @Override
  public Path toRealPath(LinkOption... options) throws IOException {
    return null;
  }

  @Override
  public File toFile() {
    return null;
  }

  @Override
  public WatchKey register(
      WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers)
      throws IOException {
    return null;
  }

  @Override
  public WatchKey register(WatchService watcher, WatchEvent.Kind<?>... events) throws IOException {
    return null;
  }

  @Override
  public Iterator<Path> iterator() {
    return null;
  }

  @Override
  public int compareTo(Path other) {
    return 0;
  }
}
