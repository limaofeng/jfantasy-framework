package org.jfantasy.framework.util.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StringUtilTest {

    @Test
    public void encodeURI() throws Exception {
        // String source = "P7kxnMLbEJ7VVqd5FFdPdwHx8v070UeIVLUHQdeTVUoSEBJwBBMZ1rx9dMuOAmxzuV90mtN7A0J7Dv5LWUUgpuQcL3FCTWNxb2fnX+APObmVnjv0sta7/gycBdRKNw6Od5M3W3eVHXqvAGu/vzk+ TtUbuZZ3j8gISJcJK9wBAvQ=";

        System.out.println(StringUtil.decodeURI("11%", "utf-8"));

        System.out.println(StringUtil.decodeURI("%11", "utf-8"));

        //System.out.println(URLEncoder.encode("+ ", "utf-8"));



        /*
        Assert.assertEquals(source,decode);

        String encode = StringUtil.encodeURI(source, "utf-8");
        LOG.debug(source + "=>" + encode);

        decode = StringUtil.decodeURI(encode,"utf-8");
        LOG.debug(encode + "=>" + decode);

        Assert.assertEquals(source,decode);
        */
    }

    private static final Log LOG = LogFactory.getLog(StringUtilTest.class);

    @Test
    public void testEllipsis() throws Exception {
        String str = "iVBORw0KGgoAAAANSUhEUgAAATgAAAEaCAYAAACFG0tXAAAKQWlDQ1BJQ0MgUHJvZmlsZQAASA2d";

        String newStr = StringUtil.ellipsis(str, 20, "...");

        LOG.debug(" StringUtil.ellipsis(str,20,\"...\") ==> " + newStr);

        Assert.assertEquals(str.substring(0, 17) + "...", newStr);

    }

    @Test
    public void testLength() throws Exception {

    }

    @Test
    public void testIsChinese() throws Exception {

    }

    @Test
    public void testTrim() throws Exception {

    }

    @Test
    public void testSplit() throws Exception {

    }

    @Test
    public void testIsNull() throws Exception {

    }

    @Test
    public void testIsNotNull() throws Exception {

    }

    @Test
    public void testNullValue() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("123", "123");

        String s = (String) data.get("1234");
        System.out.print(s);
    }

    @Test
    public void testDefaultValue() throws Exception {

    }

    @Test
    public void testLongValue() throws Exception {

    }

    @Test
    public void testNoNull() throws Exception {

    }

    @Test
    public void testIsBlank() throws Exception {

    }

    @Test
    public void testIsNotBlank() throws Exception {

    }

    @Test
    public void testUpperCaseFirst() throws Exception {

    }

    @Test
    public void testLowerCaseFirst() throws Exception {

    }

    @Test
    public void testAppend() throws Exception {

    }

    @Test
    public void testAddZeroLeft() throws Exception {

    }

    @Test
    public void testAddLeft() throws Exception {

    }

    @Test
    public void testIsNumber() throws Exception {

    }

    @Test
    public void testEncodeURI() throws Exception {

    }

    @Test
    public void testDecodeURI() throws Exception {

    }

    @Test
    public void testEscapeSpecialSign() throws Exception {

    }

    @Test
    public void testAsciiToLowerCase() throws Exception {

    }

    @Test
    public void testStartsWithIgnoreCase() throws Exception {

    }

    @Test
    public void testEndsWithIgnoreCase() throws Exception {

    }

    @Test
    public void testIndexFrom() throws Exception {

    }

    @Test
    public void testReplace() throws Exception {

    }

    @Test
    public void testAppend1() throws Exception {

    }

    @Test
    public void testAppend2digits() throws Exception {

    }

    @Test
    public void testJoin() throws Exception {

    }

    @Test
    public void testAdd() throws Exception {

    }

    @Test
    public void testNonNull() throws Exception {

    }

    @Test
    public void testEquals() throws Exception {

    }

    @Test
    public void testToUTF8String() throws Exception {

    }

    @Test
    public void testToString() throws Exception {

    }

    @Test
    public void testPrintable() throws Exception {

    }

    @Test
    public void testGetBytes() throws Exception {

    }

    @Test
    public void testGetBytes1() throws Exception {

    }

    @Test
    public void testSidBytesToString() throws Exception {

    }

    @Test
    public void testSidStringToBytes() throws Exception {

    }

    @Test
    public void testIsEmpty() throws Exception {

    }

    @Test
    public void testEscapeHtml() throws Exception {

    }

    @Test
    public void testEscapeJavaScript() throws Exception {

    }

    @Test
    public void testEscapeXml() throws Exception {

    }

    @Test
    public void testEscapeCsv() throws Exception {

    }

    @Test
    public void testTokenizeToStringArray() throws Exception {
        String name = "user.job.id";
        String[] names = StringUtil.tokenizeToStringArray(name, ".");
        LOG.debug(names);
    }

    @Test
    public void testTokenizeToStringArray1() throws Exception {

    }

    @Test
    public void testGetBirthday() throws Exception {

    }

    @Test
    public void testShortUrl() throws Exception {

    }

    @Test
    public void shortUrl() throws Exception {
        LOG.debug(Arrays.toString(StringUtil.shortUrl("/articles/1230")));
    }

    @Test
    public void testHexTo64() throws Exception {
    }

    @Test
    public void uuid() throws Exception {
        String uuid = StringUtil.uuid();
        System.out.println(uuid);
        System.out.println(uuid.length());
    }

}