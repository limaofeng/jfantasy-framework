package net.asany.jfantasy.framework.util.common;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.util.regexp.RegexpUtil;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.data.util.ParsingUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

@Slf4j
public abstract class StringUtil {

  private static final String[] EMPTY_ARRAY = new String[0];

  private static final String NONCE_CHARS =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

  private static final char[] LOWERCASES = {
    '\000', '\001', '\002', '\003', '\004', '\005', '\006', '\007', '\b', '\t', '\n', '\013', '\f',
    '\r', '\016', '\017', '\020', '\021', '\022', '\023', '\024', '\025', '\026', '\027', '\030',
    '\031', '\032', '\033', '\034', '\035', '\036', '\037', ' ', '!', '"', '#', '$', '%', '&', '\'',
    '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':',
    ';', '<', '=', '>', '?', '@', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
    'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '[', '\\', ']', '^', '_', '`',
    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
    't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', ''
  };

  private static final char[] CHAR_MAP = new char[64];

  static {
    for (int i = 0; i < 10; i++) {
      CHAR_MAP[i] = (char) ('0' + i);
    }
    for (int i = 10; i < 36; i++) {
      CHAR_MAP[i] = (char) ('a' + i - 10);
    }
    for (int i = 36; i < 62; i++) {
      CHAR_MAP[i] = (char) ('A' + i - 36);
    }
    CHAR_MAP[62] = '_';
    CHAR_MAP[63] = '-';
  }

  private StringUtil() {}

  /**
   * 字符串超出指定长度时截取到指定长度，并在末尾追加指定的字符串。
   *
   * @param value 源字符串
   * @param len 要求的长度
   * @param word 超出长度，追加末尾的字符串
   * @return {string}
   */
  public static String ellipsis(String value, int len, String word) {
    if (length(value) <= len) {
      return value;
    }
    String tValue = value;
    int tLen = len;
    tLen -= length(word);
    if (tValue.length() > tLen) {
      tValue = tValue.substring(0, tLen);
    }
    while (length(tValue) > tLen) {
      tValue = tValue.substring(0, tValue.length() - 1);
    }
    return tValue + word;
  }

  /**
   * 判断字符串长度,非单字节字符串算两个长度
   *
   * @param str 计算长度的字符串
   * @return {int}
   */
  public static int length(String str) {
    return str.replaceAll("[^\\x00-\\xff]", "rr").length();
  }

  /**
   * 判断字符串中是否包含中文
   *
   * @param input 要判断的字符串
   * @return {boolean}
   */
  public static boolean isChinese(String input) {
    return RegexpUtil.isMatch(input, "[^\\x00-\\xff]");
  }

  /**
   * 剔除字符串中的空白字符
   *
   * @param s 字符串
   * @return {string}
   */
  public static String trim(String s) {
    if (s == null) {
      return "";
    }
    return s.trim();
  }

  /**
   * 以{delim}格式截取字符串返回list,不返回为空的字符串
   *
   * @param s 源字符串
   * @param delim 分割字符
   * @return {List<String>}
   */
  public static List<String> split(String s, String delim) {
    List<String> list = new ArrayList<>();
    String ts = trim(s);
    String[] rs = ts.split(delim);
    for (String str : rs) {
      if (!str.trim().isEmpty()) {
        list.add(str);
      }
    }

    return list;
  }

  /**
   * 判断字符串是否为空或者空字符串
   *
   * @param s 要判断的字符串
   * @return {boolean}
   */
  public static boolean isNull(Object s) {
    return isBlank(nullValue(s));
  }

  /**
   * 判断字符串是否为非空字符串 {@link #isNull(Object)} 方法的取反
   *
   * @param s 要判断的字符串
   * @return {boolean}
   */
  public static boolean isNotNull(Object s) {
    return !isNull(s);
  }

  /**
   * 判断字符串是否为空，如果为空返回空字符，如果不为空，trim该字符串后返回
   *
   * @param s 要判断的字符串
   * @return {String}
   */
  public static String nullValue(String s) {
    return s == null ? "" : s.trim();
  }

  /**
   * 判断对象是否为空，如果为空返回空字符，如果不为空，返回该字符串的toString字符串
   *
   * @param s 要判断的对象
   * @return {String}
   */
  public static String nullValue(Object s) {
    return s == null ? "" : s.toString();
  }

  public static String nullValue(long s) {
    return s < 0L ? "" : String.valueOf(s);
  }

  public static String nullValue(int s) {
    return s < 0 ? "" : String.valueOf(s);
  }

  /**
   * 判断字符串是否为空，如果为空返回 {defaultValue}，如果不为空，直接返回该字符串
   *
   * @param s 要转换的对象
   * @param defaultValue 默认字符串
   * @return {String}
   */
  public static String defaultValue(Object s, String defaultValue) {
    return s == null ? defaultValue : s.toString();
  }

  /**
   * 判断字符串是否为空，如果为空返回 {defaultValue}，如果不为空，直接返回该字符串
   *
   * @param s 要转换的对象
   * @param def 默认字符串
   * @return {String}
   */
  public static String defaultValue(Object s, Supplier<String> def) {
    return s == null ? def.get() : s.toString();
  }

  /**
   * 判断字符串是否为空，如果为空返回 {defaultValue}，如果不为空，直接返回该字符串 {@link #defaultValue(Object, String)} 的重载
   *
   * @param s 要转换的字符串
   * @param defaultValue 默认字符串
   * @return {String}
   */
  public static String defaultValue(String s, String defaultValue) {
    return isBlank(s) ? defaultValue : s;
  }

  /**
   * 将Long转为String格式 如果{s}为NULL 或者 小于 0 返回空字符。否则返回{s}的toString字符串
   *
   * @param s Long
   * @return {String}
   */
  public static String longValue(Long s) {
    return (s == null) || (s.intValue() <= 0) ? "" : s.toString();
  }

  /**
   * 将Long转为String格式 如果{s}为NULL 或者 小于 0 返回"0"。否则返回{s}的toString字符串
   *
   * @param s Long
   * @return {String}
   */
  public static String longValueZero(Long s) {
    return (s == null) || (s.intValue() <= 0) ? "0" : s.toString();
  }

  public static String noNull(String s) {
    return s == null ? "" : s;
  }

  public static boolean isBlank(Object s) {
    return (s == null) || (nullValue(s).trim().isEmpty());
  }

  public static boolean isNotBlank(Object s) {
    return !isBlank(s);
  }

  public static boolean isNotBlank(String s) {
    return !isBlank(s);
  }

  /**
   * 首字母大写
   *
   * @param s 字符串
   * @return {String}
   */
  public static String upperCaseFirst(String s) {
    return s.substring(0, 1).toUpperCase() + s.substring(1);
  }

  /**
   * 首字母小写
   *
   * @param s 字符串
   * @return {String}
   */
  public static String lowerCaseFirst(String s) {
    return s.substring(0, 1).toLowerCase() + s.substring(1);
  }

  /**
   * 将字符串{ori}补齐到{length}长度 {length} 为正数右边补{fillChar}，为负数左边补{fillChar}<br>
   * {fillChar}有多个的话取随机
   *
   * @param ori 源字符串
   * @param length 要补齐的长度
   * @param fillChar 追加的字符串
   * @return {String}
   */
  public static String append(String ori, int length, String... fillChar) {
    int absLength = Math.abs(length);
    if (ori == null) {
      return "";
    }
    StringBuilder result = new StringBuilder(ori);
    if (result.length() < absLength) {
      for (int i = result.length(); i < absLength; i++) {
        if (length < 0) {
          result.insert(0, fillChar[NumberUtil.randomInt(fillChar.length)]);
        } else {
          result.append(fillChar[NumberUtil.randomInt(fillChar.length)]);
        }
        if (result.length() == absLength) {
          break;
        }
      }
    }
    return result.toString();
  }

  /**
   * 左边补零以满足长度要求
   *
   * <p>resultLength 最终长度
   */
  public static String addZeroLeft(String ori, int resultLength) {
    return append(ori, resultLength < 0 ? resultLength : -resultLength, "0");
  }

  public static String addLeft(String ori, int resultLength, String... fillChar) {
    return append(ori, resultLength < 0 ? resultLength : -resultLength, fillChar);
  }

  /**
   * 判断字符串是否为数值类型<br>
   *
   * <p>判断规则: 是否为[0-9]之间的字符组成，不能包含其他字符，否则返回false
   *
   * @param curPage 判断字符串是否为数字
   * @return {boolean}
   */
  public static boolean isNumber(String curPage) {
    return RegexpUtil.isMatch(curPage, "^[0-9]+$");
  }

  /**
   * 编码字符串 {s} 格式{enc}
   *
   * @param s 要编码的字符串
   * @param enc 格式
   * @return {String}
   */
  public static String encodeURI(String s, String enc) {
    try {
      return isBlank(s) ? s : URLEncoder.encode(s, enc).replace("+", "%20");
    } catch (UnsupportedEncodingException e) {
      log.error(e.getMessage(), e);
      return s;
    }
  }

  public static boolean contains(String s, CharSequence... ses) {
    for (CharSequence sequence : ses) {
      if (s.contains(sequence)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 解码字符串 {s} 格式{enc}
   *
   * @param s 要解码的字符串
   * @param enc 格式
   * @return {String}
   */
  public static String decodeURI(String s, String enc) {
    try {
      if (s.contains(" ") && s.contains("+")) {
        log.error("同时存在 ' ' 与 '+' decodeURI 后 可能会出现问题,所以原串返回");
        return s;
      }
      if (contains(s, /* "+", */ " ", "/", "?", /* "%", */ "#", "&", "=")) {
        log.warn("存在 特殊字符 decodeURI 后 字符串可能已经 decodeURI 过.");
        return s;
      }
      return isBlank(s) ? s : URLDecoder.decode(s, enc);
    } catch (UnsupportedEncodingException | IllegalArgumentException e) {
      log.error(e.getMessage(), e);
      return s;
    }
  }

  /**
   * 转义特殊字符
   *
   * @param condition 字符串
   * @return {String}
   */
  public static String escapeSpecialSign(String condition) {
    String bb = StringUtils.replace(condition, "/", "//");
    bb = StringUtils.replace(bb, "%", "/%");
    bb = StringUtils.replace(bb, "_", "/_");
    return bb;
  }

  public static String asciiToLowerCase(String s) {
    char[] c = null;
    int i = s.length();

    while (i-- > 0) {
      char c1 = s.charAt(i);
      if (c1 <= '') {
        char c2 = LOWERCASES[c1];
        if (c1 != c2) {
          c = s.toCharArray();
          c[i] = c2;
          break;
        }
      }
    }

    while (i-- > 0) {
      if (c != null && c.length > i && c[i] <= '') {
        c[i] = LOWERCASES[c[i]];
      }
    }
    return c == null ? s : new String(c);
  }

  public static int indexFrom(String s, String chars) {
    for (int i = 0; i < s.length(); i++) {
      if (chars.indexOf(s.charAt(i)) >= 0) {
        return i;
      }
    }
    return -1;
  }

  public static String replace(String s, String sub, String with) {
    int c = 0;
    int i = s.indexOf(sub, c);
    if (i == -1) {
      return s;
    }
    StringBuilder buf = new StringBuilder(s.length() + with.length());
    do {
      buf.append(s, c, i);
      buf.append(with);
      c = i + sub.length();
    } while ((i = s.indexOf(sub, c)) != -1);

    if (c < s.length()) {
      buf.append(s.substring(c));
    }
    return buf.toString();
  }

  public static synchronized void append(StringBuilder buf, String s, int offset, int length) {
    int end = offset + length;
    for (int i = offset; i < end; i++) {
      if (i >= s.length()) {
        break;
      }
      buf.append(s.charAt(i));
    }
  }

  public static void append(StringBuilder buf, byte b, int base) {
    int bi = 0xFF & b;
    int c = 48 + bi / base % base;
    if (c > 57) {
      c = 97 + (c - 48 - 10);
    }
    buf.append((char) c);
    c = 48 + bi % base;
    if (c > 57) {
      c = 97 + (c - 48 - 10);
    }
    buf.append((char) c);
  }

  public static String join(String[] array) {
    return join(array, "");
  }

  public static String join(String[] array, String str) {
    StringBuilder buf = new StringBuilder();
    for (String s : array) {
      buf.append(s).append(str);
    }
    return buf.toString().replaceAll(str + "$", "");
  }

  public static String[] add(String[] array, String... strs) {
    Set<String> list = new HashSet<>(Arrays.asList(array));
    Collections.addAll(list, strs);
    return list.toArray(new String[0]);
  }

  public static String nonNull(String s) {
    if (s == null) {
      return "";
    }
    return s;
  }

  public static String toUTF8String(byte[] b, int offset, int length) {
    return new String(b, offset, length, StandardCharsets.UTF_8);
  }

  public static String toString(byte[] b, String charset) {
    try {
      return new String(b, 0, b.length, charset);
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static String toString(byte[] b, int offset, int length, String charset) {
    try {
      return new String(b, offset, length, charset);
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static String printable(String name) {
    if (name == null) {
      return null;
    }
    StringBuilder buf = new StringBuilder(name.length());
    for (int i = 0; i < name.length(); i++) {
      char c = name.charAt(i);
      if (!Character.isISOControl(c)) {
        buf.append(c);
      }
    }
    return buf.toString();
  }

  public static byte[] getBytes(String s) {
    return s.getBytes(StandardCharsets.ISO_8859_1);
  }

  public static byte[] getBytes(String s, String charset) {
    try {
      return s.getBytes(charset);
    } catch (Exception e) {
      log.warn(e.getMessage(), e);
    }
    return s.getBytes();
  }

  public static boolean isEmpty(String s) {
    return (s == null) || (s.trim().isEmpty());
  }

  public static String escapeHtml(String result) {
    return StringEscapeUtils.escapeHtml4(result);
  }

  public static String escapeJavaScript(String result) {
    return StringEscapeUtils.escapeEcmaScript(result);
  }

  public static String escapeXml(String result) {
    return StringEscapeUtils.escapeXml10(result);
  }

  public static String escapeCsv(String result) {
    return StringEscapeUtils.escapeCsv(result);
  }

  public static String[] tokenizeToStringArray(String[] tokenizes) {
    List<String> strings = new ArrayList<>(tokenizes.length);
    for (String tokenize : tokenizes) {
      strings.addAll(Arrays.asList(tokenizeToStringArray(tokenize)));
    }
    return strings.toArray(new String[0]);
  }

  public static String[] tokenizeToStringArray(String tokenize) {
    return tokenizeToStringArray(tokenize, ",; \t\n");
  }

  public static String[] tokenizeToStringArray(String tokenize, String delimiters) {
    return ObjectUtil.defaultValue(
        StringUtils.tokenizeToStringArray(tokenize, delimiters), EMPTY_ARRAY);
  }

  public static Date getBirthday(String idCard) {
    return DateUtil.parse(idCard.substring(6, 6 + 8), "yyyyMMdd");
  }

  public static String hexTo64(String hex) {
    StringBuilder r = new StringBuilder();
    int index;
    int[] buff = new int[3];
    int l = hex.length();
    for (int i = 0; i < l; i++) {
      index = i % 3;
      buff[index] = Integer.parseInt(String.valueOf(hex.charAt(i)), 16);
      if (index == 2) {
        r.append(CHAR_MAP[buff[0] << 2 | buff[1] >>> 2]);
        r.append(CHAR_MAP[(buff[1] & 3) << 4 | buff[2]]);
      }
    }
    return r.toString();
  }

  public static String md5(String body) {
    return DigestUtils.md5DigestAsHex(body.getBytes());
  }

  public static String[] shortUrl(String s, String key) {
    String[] chars =
        new String[] {
          "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r",
          "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
          "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
          "S", "T", "U", "V", "W", "X", "Y", "Z"
        };
    // 对传入网址进行 MD5 加密
    String hex = DigestUtils.md5DigestAsHex((key + s).getBytes());
    String[] resUrl = new String[4];
    for (int i = 0; i < 4; i++) {
      // 把加密字符按照 8 位一组 16 进制与 0x3FFFFFFF 进行位与运算
      String sTempSubString = hex.substring(i * 8, i * 8 + 8);
      // 这里需要使用 long 型来转换，因为 Integer.parseInt() 只能处理 31 位 , 首位为符号位 , 如果不用 long ，则会越界
      long lHexLong = 0x3FFFFFFF & Long.parseLong(sTempSubString, 16);
      StringBuilder outChars = new StringBuilder();
      for (int j = 0; j < 6; j++) {
        // 把得到的值与 0x0000003D 进行位与运算，取得字符数组 chars 索引
        long index = 0x0000003D & lHexLong;
        // 把取得的字符相加
        outChars.append(chars[(int) index]);
        // 每次循环按位右移 5 位
        lHexLong = lHexLong >> 5;
      }
      // 把字符串存入对应索引的输出数组
      resUrl[i] = outChars.toString();
    }
    return resUrl;
  }

  /**
   * 生成短链接
   *
   * @param s 字符串
   * @return String[]
   */
  public static String[] shortUrl(String s) {
    return shortUrl(s, "net.asany.jfantasy");
  }

  public static String generateNonceString(int length) {
    return generateNonceString(NONCE_CHARS, length);
  }

  public static String generateNonceString(String nonceChars, int length) {
    int maxPos = nonceChars.length();
    StringBuilder nonceStr = new StringBuilder();
    Random random = new Random();
    for (int i = 0; i < length; i++) {
      nonceStr.append(nonceChars.charAt(random.nextInt(maxPos)));
    }
    return nonceStr.toString();
  }

  public static String uuid() {
    UUID uuid = UUID.randomUUID();
    return uuid.toString().replaceAll("-", "");
  }

  public static String[] chars =
      new String[] {
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r",
        "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
        "S", "T", "U", "V", "W", "X", "Y", "Z"
      };

  public static String shortUUID() {
    StringBuilder shortBuffer = new StringBuilder();
    String uuid = java.util.UUID.randomUUID().toString().replace("-", "");
    for (int i = 0; i < 8; i++) {
      String str = uuid.substring(i * 4, i * 4 + 4);
      int x = Integer.parseInt(str, 16);
      shortBuffer.append(chars[x % 0x3E]);
    }
    return shortBuffer.toString();
  }

  private static final Random random;
  private static String s_id;

  static {
    SecureRandom secureRandom = new SecureRandom();
    long secureInitializer = secureRandom.nextLong();
    random = new Random(secureInitializer);
    try {
      s_id = InetAddress.getLocalHost().toString();
    } catch (UnknownHostException e) {
      log.error(e.getMessage());
    }
  }

  public static String guid() {
    StringBuilder sbValueBeforeMD5 = new StringBuilder(128);
    long time = System.currentTimeMillis();
    long rand;
    rand = random.nextLong();
    sbValueBeforeMD5.append(s_id);
    sbValueBeforeMD5.append(":");
    sbValueBeforeMD5.append(time);
    sbValueBeforeMD5.append(":");
    sbValueBeforeMD5.append(rand);
    return pretty(DigestUtils.md5DigestAsHex(sbValueBeforeMD5.toString().getBytes()));
  }

  private static String pretty(String md5str) {
    String raw = md5str.toUpperCase();
    StringBuilder sb = new StringBuilder(64);
    sb.append(raw, 0, 8);
    sb.append("-");
    sb.append(raw, 8, 12);
    sb.append("-");
    sb.append(raw, 12, 16);
    sb.append("-");
    sb.append(raw, 16, 20);
    sb.append("-");
    sb.append(raw.substring(20));
    if (log.isDebugEnabled()) {
      log.debug("生成的GUID:" + sb);
    }
    return sb.toString();
  }

  /**
   * 下划线转驼峰命名
   *
   * @param source 原字符串
   * @return 字符串
   */
  public static String camelCase(String source) {
    return Arrays.stream(StringUtil.tokenizeToStringArray(source, "_"))
        .reduce((l, r) -> l + StringUtil.upperCaseFirst(r))
        .orElse("");
  }

  /**
   * 驼峰转下划线命名
   *
   * @param source 原字符串
   * @return 字符串
   */
  public static String snakeCase(String source) {
    return ParsingUtils.reconcatenateCamelCase(source, "_");
  }
}
