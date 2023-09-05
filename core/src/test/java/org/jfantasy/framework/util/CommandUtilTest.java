package org.jfantasy.framework.util;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.error.ExecuteCommandException;
import org.junit.jupiter.api.Test;

@Slf4j
class CommandUtilTest {

  @Test
  void executeCommand() throws ExecuteCommandException, TimeoutException {
    String result = CommandUtil.executeCommand("magick", "--version");
    log.info(result);
  }

  @Test
  void test() throws IOException, InterruptedException {
    String command = "curl https://admin.app.asany.cn/umi.ac2b3b25.js";
    ProcessBuilder builder = new ProcessBuilder(command.split(" "));
    Process proc = builder.start();

    GobblerThread gobblerThread = new GobblerThread(proc.getInputStream(), "input");
    GobblerThread err = new GobblerThread(proc.getErrorStream(), "error");
    gobblerThread.start();
    err.start();

    long start = System.currentTimeMillis();
    // 等待子进程执行结束
    boolean exit = proc.waitFor(20, TimeUnit.SECONDS);
    log.info(exit + "\t" + "time:" + (System.currentTimeMillis() - start));

    start = System.currentTimeMillis();
    log.info("length:" + gobblerThread.getResult().length());
    //    log.info("error stream:" + err.getResult().length());
    log.info("time:" + (System.currentTimeMillis() - start));
    Thread.sleep(200);
    start = System.currentTimeMillis();
    log.info("length:" + gobblerThread.getResult().length());
    log.info("time:" + (System.currentTimeMillis() - start));
    // 销毁子进程
  }

  @Test
  void runPython() throws ExecuteCommandException, TimeoutException {
    String bin = "/usr/local/bin/python3";
    String workspace = "/Users/limaofeng/PycharmProjects/java-runtime";

    Map<String, String> env = new HashMap<>(2);
    env.put("input_path", "/Users/limaofeng/Downloads/data.json");
    env.put("PATH", System.getenv("PATH"));

    String result =
        CommandUtil.executeCommand(
            new String[] {bin, "Runtime.py"}, env, Paths.get(workspace).toFile(), 30);

    log.info(result);

    if (!result.contains("python_callback_result:")) {
      throw new RuntimeException("返回数据格式错误:" + result);
    }
  }
}
