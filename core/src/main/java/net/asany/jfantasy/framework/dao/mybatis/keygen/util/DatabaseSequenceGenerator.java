package net.asany.jfantasy.framework.dao.mybatis.keygen.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.dao.mybatis.keygen.service.SequenceService;
import net.asany.jfantasy.framework.spring.SpringBeanUtils;

/**
 * 序列信息
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-8-24 上午10:40:58
 */
@Slf4j
public class DatabaseSequenceGenerator {

  protected static final ConcurrentMap<String, DatabaseSequenceGenerator> keys =
      new ConcurrentHashMap<>(10);
  private final Lock lock = new ReentrantLock();
  private final SequenceService service;

  private boolean initialized = false;

  /** 获取缓存序列的最大值 */
  @Getter private long keyMax = 1L;

  /** 获取缓存序列的最小值 */
  @Getter private long keyMin;

  private final AtomicLong currentValue = new AtomicLong(0);

  /** 序列的初始值 */
  @Getter private final long initialValue;

  /** 控制一次性分配给标识生成器的键值数量，以减少与数据库的通信开销。 */
  @Getter private final long allocationSize;

  /** 控制分配的键值范围内逐个递增的大小，通常设置为 1，以确保生成的标识值连续。 */
  @Getter private final long incrementSize;

  @Getter private final String keyName;

  public static DatabaseSequenceGenerator create(
      String keyName, long incrementSize, long allocationSize, long initialValue) {
    AtomicBoolean alreadyExist = new AtomicBoolean(true);
    DatabaseSequenceGenerator generator =
        keys.computeIfAbsent(
            keyName,
            k -> {
              alreadyExist.set(false);
              return new DatabaseSequenceGenerator(
                  keyName, incrementSize, allocationSize, initialValue);
            });
    if (alreadyExist.get()
        && (generator.getIncrementSize() != incrementSize
            || generator.getAllocationSize() != allocationSize)) {
      log.error(
          "二次初始化序列时，使用了不同的设置，keyName:{},incrementSize:{},allocationSize:{},initialValue:{}",
          keyName,
          incrementSize,
          allocationSize,
          initialValue);
    }
    return generator;
  }

  /**
   * 提供直接通过 SequenceInfo 直接查询索引的方法
   *
   * @param keyName KeyName
   * @return long
   */
  public static long nextValue(String keyName) {
    return DatabaseSequenceGenerator.create(keyName, 1, 50, 0).nextValue();
  }

  private DatabaseSequenceGenerator(
      String keyName, long incrementSize, long allocationSize, long initialValue) {
    this.service = SpringBeanUtils.getBeanByType(SequenceService.class);
    this.allocationSize = allocationSize;
    this.incrementSize = incrementSize;
    this.initialValue = initialValue;
    this.keyName = keyName;

    initDB();
  }

  public static DatabaseSequenceGenerator create(String keyName) {
    return create(keyName, 1, 50, 0);
  }

  private void initDB() {
    try {
      this.lock.lock();
      long keyFromDB;
      if (this.service.exists(this.keyName)) {
        keyFromDB = getCurrentKeyValue(keyName);
      } else {
        keyFromDB = createKey(this.keyName, this.initialValue);
      }
      this.keyMax = keyFromDB;
      this.keyMin = keyFromDB;
      this.currentValue.set(this.keyMin);
      initialized = true;
    } catch (Exception e) {
      log.error(e.getMessage());
    } finally {
      this.lock.unlock();
    }
  }

  /** 从数据库检索 */
  private void retrieveFromDB(long newCurrentValue) {
    if (newCurrentValue <= this.keyMax) {
      return;
    }
    this.keyMax = getKeyInfo(this.keyName, this.allocationSize);
    this.keyMin = this.keyMax - this.allocationSize;
    this.currentValue.set(this.keyMin);
  }

  private long getCurrentKeyValue(String keyName) {
    return this.service.getCurrentKeyValue(keyName);
  }

  /**
   * 获取序列的下个值
   *
   * @param keyName keyName
   * @param poolSize 缓冲值
   * @return long
   */
  private long getKeyInfo(String keyName, long poolSize) {
    return this.service.next(keyName, poolSize);
  }

  /**
   * 创建新的序列
   *
   * @param keyName keyName
   * @param poolSize 缓冲值
   * @return long
   */
  private long createKey(String keyName, long poolSize) {
    return this.service.newKey(keyName, poolSize);
  }

  /**
   * 获取序列的下一个值
   *
   * @return long
   */
  public long nextValue() {
    long newCurrentValue = this.currentValue.accumulateAndGet(incrementSize, Long::sum);
    if (newCurrentValue > this.keyMax) {
      try {
        this.lock.lock();
        retrieveFromDB(newCurrentValue);
      } finally {
        this.lock.unlock();
      }
      return nextValue();
    }
    return newCurrentValue;
  }
}
