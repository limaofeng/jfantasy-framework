package net.asany.jfantasy.framework.dao.hibernate.generator;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Properties;
import net.asany.jfantasy.framework.dao.hibernate.annotations.PaddingType;
import net.asany.jfantasy.framework.dao.hibernate.annotations.SnowflakeFormat;
import net.asany.jfantasy.framework.dao.hibernate.annotations.SnowflakeGenerator;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

public class SnowflakeIdentifierGenerator implements IdentifierGenerator {

  private static final String BASE62_CHARS =
      "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  private static final SecureRandom secureRandom = new SecureRandom();
  private SnowflakeFormat format;
  private long length;
  private PaddingType paddingType;
  private Snowflake snowflake;
  private boolean toStr = false;

  public SnowflakeIdentifierGenerator() {}

  public SnowflakeIdentifierGenerator(
      SnowflakeGenerator snowflakeGenerator,
      Member member,
      CustomIdGeneratorCreationContext context) {
    this();
    long workerId = snowflakeGenerator.workerId();
    long dataCenterId = snowflakeGenerator.dataCenterId();
    this.format = snowflakeGenerator.format();
    this.length = snowflakeGenerator.length();
    this.paddingType = snowflakeGenerator.paddingType();
    this.snowflake = IdUtil.getSnowflake(workerId, dataCenterId);
    this.toStr = ((Field) member).getType() == String.class;
  }

  @Override
  public void configure(Type type, Properties params, ServiceRegistry serviceRegistry)
      throws MappingException {
    IdentifierGenerator.super.configure(type, params, serviceRegistry);
    String workerId = params.getProperty("workerId", "1");
    String dataCenterId = params.getProperty("dataCenterId", "1");
    this.snowflake = IdUtil.getSnowflake(Long.parseLong(workerId), Long.parseLong(dataCenterId));
    this.format = SnowflakeFormat.valueOf(params.getProperty("format", "BASE62"));
    this.length = Long.parseLong(params.getProperty("length", "0"));
    this.paddingType = PaddingType.valueOf(params.getProperty("paddingType", "PREFIX"));
    this.toStr = type.getReturnedClass() == String.class;
  }

  @Override
  public Serializable generate(SharedSessionContractImplementor session, Object object)
      throws HibernateException {
    if (this.toStr) {
      return switch (format) {
        case BASE62 -> {
          if (length == 0) {
            yield convertDecimalToBase62(snowflake.nextId());
          }
          yield switch (paddingType) {
            case PREFIX -> {
              String base62Timestamp = convertDecimalToBase62(snowflake.nextId());
              String randomString = generateRandomString((int) length - base62Timestamp.length());
              yield randomString + base62Timestamp;
            }
            case SUFFIX -> {
              String base62Timestamp = convertDecimalToBase62(snowflake.nextId());
              String randomString = generateRandomString((int) length - base62Timestamp.length());
              yield base62Timestamp + randomString;
            }
            case SHUFFLE -> {
              String base62Timestamp = convertDecimalToBase62(snowflake.nextId());
              String randomString = generateRandomString((int) length - base62Timestamp.length());
              yield mixParts(base62Timestamp + randomString);
            }
          };
        }
        case NONE -> snowflake.nextIdStr();
      };
    }
    return snowflake.nextId();
  }

  // 将10进制数转换为62进制字符串
  public static String convertDecimalToBase62(long decimalNumber) {
    if (decimalNumber < 0) {
      throw new IllegalArgumentException("Decimal number must be non-negative.");
    }
    StringBuilder base62String = new StringBuilder();
    // 不断取余数，转换为对应的Base62字符
    while (decimalNumber > 0) {
      int remainder = (int) (decimalNumber % 62);
      base62String.insert(0, BASE62_CHARS.charAt(remainder));
      decimalNumber = decimalNumber / 62;
    }
    // 如果输入为0，直接返回"0"
    return !base62String.isEmpty() ? base62String.toString() : "0";
  }

  private static String generateRandomString(int length) {
    int[] randomNumbers = new int[length];
    nextNumbers(randomNumbers);
    return Arrays.stream(randomNumbers)
        .map(BASE62_CHARS::charAt)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }

  private static void nextNumbers(int[] randomNumbers) {
    int maxRandomNumber = BASE62_CHARS.length();
    for (int i = 0; i < randomNumbers.length; i++) {
      randomNumbers[i] = secureRandom.nextInt(maxRandomNumber);
    }
  }

  private static String mixParts(String input) {
    StringBuilder result = new StringBuilder(input.length());
    int[] indices = secureRandom.ints(0, input.length()).distinct().limit(input.length()).toArray();
    for (int index : indices) {
      result.append(input.charAt(index));
    }
    return result.toString();
  }
}
