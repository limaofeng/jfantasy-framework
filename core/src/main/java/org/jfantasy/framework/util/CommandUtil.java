package org.jfantasy.framework.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.error.ExecuteCommandException;

/**
 * 执行命令
 *
 * @author limaofeng
 */
@Slf4j
public class CommandUtil {

  private static final String SYSTEM_PATH = "PATH";

  /**
   * 执行命令
   *
   * @param cmdarray 命令
   * @return 返回结果
   * @throws ExecuteCommandException 执行异常
   */
  public static String executeCommand(String... cmdarray)
      throws ExecuteCommandException, TimeoutException {
    return executeCommand(cmdarray, new HashMap<>(0), null, 10);
  }

  /**
   * 执行命令
   *
   * @param command 命令
   * @param timeout 超时时间(秒)
   * @return 返回结果
   * @throws ExecuteCommandException 执行异常
   */
  public static String executeCommand(String command, long timeout)
      throws ExecuteCommandException, TimeoutException {
    return executeCommand(new String[] {command}, new HashMap<>(0), null, timeout);
  }

  /**
   * 执行命令
   *
   * @param cmdarray 命令
   * @param timeout 超时时间(秒)
   * @return 返回结果
   * @throws ExecuteCommandException 执行异常
   */
  public static String executeCommand(String[] cmdarray, long timeout)
      throws ExecuteCommandException, TimeoutException {
    return executeCommand(cmdarray, new HashMap<>(0), null, timeout);
  }

  /**
   * * 执行命令
   *
   * @param command 命令
   * @param env 环境变量
   * @param dir 执行目录
   * @param timeout 超时时间(秒)
   * @return 返回结果
   * @throws TimeoutException 超时异常
   * @throws ExecuteCommandException 执行异常
   */
  public static String executeCommand(
      String[] command, Map<String, String> env, File dir, long timeout)
      throws TimeoutException, ExecuteCommandException {
    Process proc = null;
    RuntimeStream stream = null;
    try {
      // 执行命令
      ProcessBuilder builder =
          new ProcessBuilder(command.length == 1 ? command[0].split("\\s+") : command);
      if (env != null && !env.isEmpty()) {
        builder.environment().putAll(env);
      }
      if (dir != null) {
        builder.directory(dir);
      }
      proc = builder.start();
      // 获取输出流
      stream = new RuntimeStream(proc.getErrorStream(), proc.getInputStream());
      // 等待执行完成
      int exitCode = waitFor(proc, timeout);
      // 获取执行结果
      String error = stream.getError();
      String result = stream.getResult();
      // 执行失败
      if (exitCode != 0) {
        log.error(error);
        throw new IOException(error);
      }
      // 执行成功
      return result;
    } catch (IOException | InterruptedException e) {
      throw new ExecuteCommandException(e);
    } finally {
      if (proc != null) {
        proc.destroy();
      }
      if (stream != null) {
        stream.close();
      }
    }
  }

  private static int waitFor(Process proc, long timeout)
      throws TimeoutException, InterruptedException {
    Worker worker = new Worker(proc);
    worker.start();
    try {
      worker.join(timeout * 1000);
      if (worker.exit == null) {
        throw new TimeoutException("Timeout waiting for process");
      }
      return worker.exit;
    } catch (InterruptedException ex) {
      worker.interrupt();
      Thread.currentThread().interrupt();
      throw ex;
    }
  }

  private static class Worker extends Thread {
    private final Process process;
    private Integer exit;

    private Worker(Process process) {
      this.process = process;
    }

    @Override
    public void run() {
      try {
        exit = process.waitFor();
      } catch (InterruptedException e) {
        log.error(e.getMessage());
      }
    }
  }

  private static class RuntimeStream {

    private final GobblerThread errorGobbler;
    private final GobblerThread outputGobbler;

    public RuntimeStream(InputStream errorStream, InputStream inputStream) {
      errorGobbler = new GobblerThread(errorStream);
      outputGobbler = new GobblerThread(inputStream);
      errorGobbler.start();
      outputGobbler.start();
    }

    public String getResult() {
      return outputGobbler.getResult();
    }

    public String getError() {
      return errorGobbler.getResult();
    }

    public void close() {
      errorGobbler.shutoff();
      outputGobbler.shutoff();
    }
  }

  @Slf4j
  private static class GobblerThread extends Thread {

    private final StringBuilder result = new StringBuilder();
    private final InputStream is;

    GobblerThread(InputStream is) {
      this.is = is;
    }

    @Override
    public void run() {
      try {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
          result.append(line).append("\n");
        }
      } catch (IOException e) {
        log.error(e.getMessage());
      }
    }

    public String getResult() {
      try {
        return this.result.toString();
      } finally {
        this.shutoff();
      }
    }

    public void shutoff() {
      if (this.isAlive()) {
        this.interrupt();
      }
    }
  }
}
