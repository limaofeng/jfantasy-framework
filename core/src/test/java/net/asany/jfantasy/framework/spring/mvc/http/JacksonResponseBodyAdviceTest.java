package net.asany.jfantasy.framework.spring.mvc.http;

import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.spring.mvc.servlet.JacksonResponseBodyAdvice;

@Slf4j
public class JacksonResponseBodyAdviceTest {

  private final JacksonResponseBodyAdvice advice = new JacksonResponseBodyAdvice();

  //  @Test
  //  @JsonResultFilter(
  //      value = {
  //        @BeanFilter(
  ////            type = JSONTest.Article.class,
  //            excludes = {"title", "category"}),
  //        @BeanFilter(type = JSONTest.ArticleCategory.class, excludes = "articles")
  //      })
  //  public void getFilterProvider() throws Exception {
  //    JSON.initialize();
  //
  //    JSONTest.Article article = JSONTest.TestDataBuilder.build(JSONTest.Article.class,
  // "JSONTest");
  //
  //    Method method =
  //        ClassUtil.getDeclaredMethod(JacksonResponseBodyAdviceTest.class, "getFilterProvider");
  //    JsonResultFilter filter = ClassUtil.getMethodAnno(method, JsonResultFilter.class);
  //    FilterProvider provider = FilteredMixinHolder.getFilterProvider(filter);
  //    log.info(JSON.serialize(article, provider));
  //  }
}
