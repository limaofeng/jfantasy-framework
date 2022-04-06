package org.jfantasy.framework.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.util.common.StreamUtil;

@Slf4j
public class CommandUtil {

  @SneakyThrows
  public static String exec(String command) {
    Process proc = Runtime.getRuntime().exec(command);
    return waitForResult(proc);
  }

  @SneakyThrows
  public static String exec(String... args) {
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command(args);
    Process process = processBuilder.start();
    return waitForResult(process);
  }

  private static String waitForResult(Process process) throws InterruptedException, IOException {
    int exitVal = process.waitFor();
    SequenceInputStream sis =
        new SequenceInputStream(process.getInputStream(), process.getErrorStream());
    BufferedReader br = new BufferedReader(new InputStreamReader(sis, StandardCharsets.UTF_8));
    String line = null;
    StringBuilder result = new StringBuilder();
    while ((line = br.readLine()) != null) {
      result.append(line).append("\n");
    }
    StreamUtil.closeQuietly(br);
    if (exitVal != 0) {
      throw new RuntimeException(result.toString());
    }
    return result.toString();
  }
}
