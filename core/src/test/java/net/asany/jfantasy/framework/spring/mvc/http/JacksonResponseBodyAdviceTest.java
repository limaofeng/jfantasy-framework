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
