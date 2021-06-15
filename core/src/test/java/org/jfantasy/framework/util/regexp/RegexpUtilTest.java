package org.jfantasy.framework.util.regexp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.util.json.bean.User;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.lang.reflect.Array;


public class RegexpUtilTest {

    private static final Log LOG = LogFactory.getLog(RegexpUtilTest.class);

    @Test
    public void testGetPattern() throws Exception {

    }

    @Test
    public void testParseFirstGroup() throws Exception {

    }

    @Test
    public void testParseGroups() throws Exception {

    }

    @Test
    public void testIsMatch() throws Exception {

        String className = Array.newInstance(User.class, 0).getClass().getName();

        Assert.isTrue(RegexpUtil.isMatch(className, "^\\[L|;$"));

        Assert.isTrue(RegexpUtil.isMatch(className, "^\\[L[a-zA-Z._]+;$"));

        Assert.isTrue(RegexpUtil.isMatch("[Lorg.jfantasy.security.bean.User$Test;", "^\\[L[a-zA-Z._$]+;$"));

    }

    @Test
    public void testSplit() throws Exception {

    }

    @Test
    public void testIsMatch1() throws Exception {

    }

    @Test
    public void testFind() throws Exception {

    }

    @Test
    public void testParseFirst() throws Exception {
        LOG.debug(RegexpUtil.parseGroup("ddd/", "([^/]+)/$", 1));

        LOG.debug(RegexpUtil.parseGroup("aaa/bbb/ccc/ddd/", "([^/]+)/$", 1));

        LOG.debug(RegexpUtil.parseGroup("qqqq/", "([^/]+)/$", 1));
    }

    @Test
    public void testParseGroup() throws Exception {

    }

    @Test
    public void testParseGroup1() throws Exception {

    }

    @Test
    public void testReplaceFirst() throws Exception {
        LOG.debug("member:15921884771".replaceAll("^[^:]+:", ""));
    }

    @Test
    public void testReplace() throws Exception {
        String newName = RegexpUtil.replace("nameSpaceWat", "[A-Z]", (text,index,matcher) -> "_" + text.toLowerCase());
        LOG.debug(newName);
        Assert.isTrue(newName =="name_space_wat");


    }


    @Test
    public void testWildMatch() throws Exception {

    }

    @Test
    public void matches() {
        String regex = "[/].+";
        LOG.debug("/".matches(regex));
        LOG.debug("/error".matches(regex));
    }


    public static void main(String[] args) {
        // System.out.println(replace("15921884771", "(\\d{3})\\d{4}(\\d{4,})", "$1****$2"));
//		test("*.js", "/itsm/static/js/fantasy/String.js");
        // test("toto.java", "tutu.java");
        // test("12345", "1234");
        // test("1234", "12345");
        // test("*f", "");
        // test("***", "toto");
        // test("*.java", "toto.");
        // test("*.java", "toto.jav");
        // test("*.java", "toto.java");
        // test("abc*", "");
        // test("a*c", "abbbbbccccc");
        // test("abc*xyz", "abcxxxyz");
        // test("*xyz", "abcxxxyz");
        // test("abc**xyz", "abcxxxyz");
        // test("abc**x", "abcxxx");
        // test("*a*b*c**x", "aaabcxxx");
        // test("abc*x*yz", "abcxxxyz");
        // test("abc*x*yz*", "abcxxxyz");
        // test("a*b*c*x*yf*z*", "aabbccxxxeeyffz");
        // test("a*b*c*x*yf*zze", "aabbccxxxeeyffz");
        // test("a*b*c*x*yf*ze", "aabbccxxxeeyffz");
        // test("a*b*c*x*yf*ze", "aabbccxxxeeyfze");
        // test("*LogServerInterface*.java", "_LogServerInterfaceImpl.java");
        // test("abc*xyz", "abcxyxyz");
    }

//	private static void test(String pattern, String str) {
//		System.out.println(pattern + " " + str + " =>> " + wildMatch(pattern, str));
//	}


}