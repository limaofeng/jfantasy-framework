package org.jfantasy.framework.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class ImagesTest {

  @Test
  void base64() throws IOException {
    String base64 = Images.base64("/Users/limaofeng/Downloads/3.jpg");
    System.out.println(base64);
    Files.write(Paths.get("/Users/limaofeng/Downloads/3.txt"), base64.getBytes());
  }
}
