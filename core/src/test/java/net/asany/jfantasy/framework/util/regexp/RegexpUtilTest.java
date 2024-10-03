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
package net.asany.jfantasy.framework.util.regexp;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Array;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.error.User;
import net.asany.jfantasy.framework.util.web.WebUtil;
import org.junit.jupiter.api.Test;

@Slf4j
public class RegexpUtilTest {

  @Test
  public void testGetPattern() throws Exception {}

  @Test
  public void testParseFirstGroup() throws Exception {}

  @Test
  public void testParseGroups() throws Exception {}

  @Test
  public void testIsMatch() throws Exception {

    String className = Array.newInstance(User.class, 0).getClass().getName();

    assertTrue(RegexpUtil.isMatch(className, "^\\[L|;$"));

    assertTrue(RegexpUtil.isMatch(className, "^\\[L[a-zA-Z._]+;$"));

    assertTrue(
        RegexpUtil.isMatch("[Lnet.asany.jfantasy.security.bean.User$Test;", "^\\[L[a-zA-Z._$]+;$"));
  }

  @Test
  public void testSplit() throws Exception {}

  @Test
  public void testIsMatch1() throws Exception {
    assert RegexpUtil.isMatch("na/me", "[<>|*?,/]");
  }

  @Test
  public void testFind() throws Exception {}

  @Test
  public void testParseFirst() throws Exception {
    log.debug(RegexpUtil.parseGroup("ddd/", "([^/]+)/$", 1));

    log.debug(RegexpUtil.parseGroup("aaa/bbb/ccc/ddd/", "([^/]+)/$", 1));

    log.debug(RegexpUtil.parseGroup("qqqq/", "([^/]+)/$", 1));

    String fileName = "[电影天堂www.dytt89.com]美丽人生BD国意双语中英双字.mp4.part00001";

    String number = RegexpUtil.parseGroup(WebUtil.getExtension(fileName), "part(\\d+)$", 1);

    log.debug(number);
  }

  @Test
  public void testParseGroup() throws Exception {
    String fileName = "sdsdfsdf.jpg.part0001";
    int partNumber =
        Integer.parseInt(
            Objects.requireNonNull(
                RegexpUtil.parseGroup(WebUtil.getExtension(fileName, true), "part(\\d+)$", 1)));
    System.out.println("partNumber:" + partNumber);
  }

  @Test
  public void testParseGroup1() throws Exception {}

  @Test
  public void testReplaceFirst() throws Exception {
    log.debug("member:15921884771".replaceAll("^[^:]+:", ""));

    log.debug("/a/b/c/".replaceFirst("[^/]+/$", ""));
  }

  @Test
  public void testReplace() throws Exception {
    String newName =
        RegexpUtil.replace(
            "nameSpaceWat", "[A-Z]", (text, index, matcher) -> "_" + text.toLowerCase());
    log.debug(newName);
    assertSame("name_space_wat", newName);
  }

  @Test
  public void testWildMatch() throws Exception {}

  @Test
  public void matches() {
    String regex = "[/].+";
    log.debug(":" + "/".matches(regex));
    log.debug(":" + "/error".matches(regex));
  }

  public static void main(String[] args) {
    System.out.println(RegexpUtil.replace("as.jpgfsdf", "()$", ""));

    // System.out.println(replace("15921884771", "(\\d{3})\\d{4}(\\d{4,})",
    // "$1****$2"));
    // test("*.js", "/itsm/static/js/fantasy/String.js");
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

  // private static void test(String pattern, String str) {
  // System.out.println(pattern + " " + str + " =>> " + wildMatch(pattern, str));
  // }

}
