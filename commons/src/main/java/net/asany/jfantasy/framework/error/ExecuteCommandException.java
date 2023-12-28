package net.asany.jfantasy.framework.error;

/**
 * 执行命令异常
 *
 * @author limaofeng
 */
public class ExecuteCommandException extends Exception {

  public ExecuteCommandException(Exception e) {
    super(e);
  }
}
