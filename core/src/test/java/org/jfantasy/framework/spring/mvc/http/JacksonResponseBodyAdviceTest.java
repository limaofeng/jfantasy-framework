package org.jfantasy.framework.spring.mvc.http;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import java.lang.reflect.Method;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.jackson.JSONTest;
import org.jfantasy.framework.jackson.annotation.BeanFilter;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.framework.util.common.ClassUtil;
import org.junit.jupiter.api.Test;

public class JacksonResponseBodyAdviceTest {

  private static final Log LOG = LogFactory.getLog(JacksonResponseBodyAdviceTest.class);

  private JacksonResponseBodyAdvice advice = new JacksonResponseBodyAdvice();

  @Test
  @JsonResultFilter(
      value = {
        @BeanFilter(
            type = JSONTest.Article.class,
            excludes = {"title", "category"}),
        @BeanFilter(type = JSONTest.ArticleCategory.class, excludes = "articles")
      })
  public void getFilterProvider() throws Exception {
    JSONTest.Article article = JSONTest.TestDataBuilder.build(JSONTest.Article.class, "JSONTest");

    Method method =
        ClassUtil.getDeclaredMethod(JacksonResponseBodyAdviceTest.class, "getFilterProvider");
    JsonResultFilter filter = ClassUtil.getMethodAnno(method, JsonResultFilter.class);
    FilterProvider provider = advice.getFilterProvider(filter);
    LOG.debug(JSON.serialize(article, provider));
  }
}
