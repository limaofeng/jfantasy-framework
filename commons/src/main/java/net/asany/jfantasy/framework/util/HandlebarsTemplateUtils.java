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
package net.asany.jfantasy.framework.util;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Template;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.util.common.DateUtil;
import net.asany.jfantasy.framework.util.common.StreamUtil;
import net.asany.jfantasy.framework.util.common.StringUtil;

/**
 * Handlebars 模板工具类
 *
 * @author limaofeng
 */
@Slf4j
public class HandlebarsTemplateUtils {

  private static final Handlebars HANDLEBARS = new Handlebars();

  static {
    registerHelper(
        "formatDate",
        (Helper<Date>) (context, options) -> DateUtil.format(context, (String) options.params[0]));
    registerHelper(
        "URLEncode",
        (Helper<String>)
            (context, options) -> URLEncoder.encode(context, (String) options.params[0]));
  }

  /**
   * 注册模板助手
   *
   * @param name 助手名称
   * @param helper 模板助手
   * @param <H> 模板助手类型
   */
  public static <H> void registerHelper(String name, Helper<H> helper) {
    HANDLEBARS.registerHelper(name, helper);
  }

  /**
   * 将模板处理为字符串
   *
   * @param inputTemplate 字符串模板
   * @return Template
   */
  public static String processTemplateIntoString(String inputTemplate, Object model) {
    if (StringUtil.isBlank(inputTemplate)) {
      return null;
    }
    try {
      return HANDLEBARS.compileInline(inputTemplate).apply(model);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  /**
   * 将模板处理为字符串
   *
   * @param template 模板
   * @param model 模型
   * @return String
   */
  public static String processTemplateIntoString(Template template, Object model)
      throws IOException {
    return template.apply(model);
  }

  /**
   * 将模板处理为字节流
   *
   * @param template 模板
   * @param model 模型
   * @param out 输出流
   */
  public static void writer(String template, Object model, OutputStream out) {
    Writer writer = new OutputStreamWriter(out);
    try {
      HANDLEBARS.compileInline(template).apply(model, writer);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    } finally {
      StreamUtil.closeQuietly(writer);
    }
  }

  /**
   * 从文件加载模板
   *
   * @param path 文件路径
   * @return Template
   */
  public static Template template(String path) throws IOException {
    return HANDLEBARS.compile(path);
  }

  /**
   * 从字符串加载模板
   *
   * @param template 字符串模板
   * @return Template
   */
  public static Template templateInline(String template) throws IOException {
    return HANDLEBARS.compileInline(template);
  }

  /**
   * 获取 Handlebars 实例
   *
   * @return Handlebars
   */
  public static Handlebars getHandlebars() {
    return HANDLEBARS;
  }
}
