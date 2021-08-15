package org.jfantasy.framework.util.common;

import java.awt.image.BufferedImage;
import org.jfantasy.framework.httpclient.HttpClientUtil;
import org.jfantasy.framework.httpclient.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ImageUtilTest {

  @BeforeEach
  public void setUp() throws Exception {
    System.setProperty("jmagick.systemclassloader", "no");
    // new Magick();
  }

  @AfterEach
  public void tearDown() throws Exception {}

  @Test
  public void testGetFormatName() throws Exception {}

  @Test
  public void testToJpegImageFile() throws Exception {}

  @Test
  public void testWatermark() throws Exception {}

  @Test
  public void testWrite() throws Exception {
    Response response =
        HttpClientUtil.doGet(
            "http://10.url.cn/qqcourse_logo_ng/ajNVdqHZLLA5jk6W2DFakdB05F1XCtRvia2y06ibaZiakVUicSFQvH0OicN9qK4gsfCZwwU8G3Kglujg/220");
    BufferedImage image = ImageUtil.getImage(ImageUtil.base64(response.getBody()));
  }

  @Test
  public void testPressText() throws Exception {}

  @Test
  public void testReduce() throws Exception {}

  @Test
  public void testMeasure() throws Exception {}

  @Test
  public void testGetFileAttributes() throws Exception {}

  @Test
  public void testBmpReader() throws Exception {}

  @Test
  public void testPngReader() throws Exception {}

  @Test
  public void testJpgReader() throws Exception {}

  @Test
  public void testScreenshots() throws Exception {}

  @Test
  public void testGetImage() throws Exception {}
}
