package org.jfantasy.framework.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    ProcessBuilder builder = new ProcessBuilder("curl", "http://admin.asany.cn/umi.e89bb902.js");
    Process proc = builder.start();

    //      Process proc = Runtime.getRuntime().exec("curl http://admin.asany.cn/umi.e89bb902.js");

    // 启动读取子进程输出的线程
    Thread thread =
        new Thread(
            () -> {
              BufferedReader reader =
                  new BufferedReader(new InputStreamReader(proc.getInputStream()));
              String line;
              try {
                while ((line = reader.readLine()) != null) {
                  System.out.println(line);
                }
              } catch (IOException e) {
                e.printStackTrace();
              }
            });

    // 启动线程
    thread.start();
    //
    //      BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
    //      BufferedReader stdError = new BufferedReader(new
    // InputStreamReader(proc.getErrorStream()));

    // 等待子进程执行结束
    boolean exit = proc.waitFor(10, TimeUnit.SECONDS);

    //      String line;
    //      while ((line = in.readLine()) != null) {
    //          System.out.println("xxx:" + line);
    //      }

    log.info(exit + "");
    // 销毁子进程
  }
}
