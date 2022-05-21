package org.jfantasy.framework.util;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Template;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.framework.util.common.StringUtil;

public class HandlebarsTemplateUtils {

  private static final Handlebars handlebars = new Handlebars();

  private static final Log LOG = LogFactory.getLog(HandlebarsTemplateUtils.class);

  static {
    registerHelper(
        "format",
        (Helper<Date>) (context, options) -> DateUtil.format(context, (String) options.params[0]));
    registerHelper(
        "URLEncode",
        (Helper<String>)
            (context, options) -> URLEncoder.encode(context, (String) options.params[0]));
  }

  public static <H> Handlebars registerHelper(String name, Helper<H> helper) {
    return handlebars.registerHelper(name, helper);
  }

  public static String processTemplateIntoString(String inputTemplate, Object model) {
    if (StringUtil.isBlank(inputTemplate)) {
      return null;
    }
    try {
      return handlebars.compileInline(inputTemplate).apply(model);
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  public static String processTemplateIntoString(Template template, Object model)
      throws IOException {
    return template.apply(model);
  }

  public static void writer(Object model, String template, ByteArrayOutputStream out) {}

  /**
   * 从文件加载模板
   *
   * @param path 文件路径
   * @return Template
   */
  public static Template template(String path) throws IOException {
    return handlebars.compile(path);
  }
}
