package net.asany.jfantasy.framework.util.common;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import org.junit.jupiter.api.Test;

public class PinyinUtilTest {

  @Test
  public void testHanziToPinyin() throws Exception {
    // Assert.assertEquals(PinyinUtil.hanziToPinyin("长沙"),"chang sha");

    // Assert.assertEquals(Arrays.toString(PinyinUtil.stringToPinyin("长沙",true,"
    // ")),Arrays.toString(new String[]{"chang sha"}));

    assert PinyinHelper.convertToPinyinString("长沙", " ", PinyinFormat.WITHOUT_TONE)
        .equals("chang sha");

    assert PinyinHelper.convertToPinyinString("重庆", " ", PinyinFormat.WITHOUT_TONE)
        .equals("chong qing");
  }
}
