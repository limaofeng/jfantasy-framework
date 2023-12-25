package net.asany.jfantasy.framework.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PinyinUtilsTest {

  @BeforeEach
  public void setUp() throws Exception {
    PinyinUtils.addMutilDict("白术", "bái,zhú");
  }

  @Test
  public void getShort() throws Exception {
    System.out.println(PinyinUtils.getShort("白术"));
  }

  @Test
  public void getAll() throws Exception {
    System.out.println(PinyinUtils.getAll("白术"));
    System.out.println(PinyinUtils.getAll("白术", "-"));
  }
}
