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
package net.asany.jfantasy.framework.context;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Optional;
import net.asany.jfantasy.framework.context.bean.Language;
import net.asany.jfantasy.framework.context.service.LanguageService;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.support.AbstractMessageSource;

/**
 * 数据库消息源
 *
 * @author limaofeng
 */
public class DatabaseMessageSource extends AbstractMessageSource {

  private static final Locale DEFAULT_LOCALE = Locale.SIMPLIFIED_CHINESE;

  private final LanguageService languageService;

  public DatabaseMessageSource(LanguageService languageService) {
    this.languageService = languageService;
  }

  @Override
  protected MessageFormat resolveCode(@NotNull String key, @NotNull Locale locale) {
    Optional<Language> optional = languageService.getMessage(key, locale);
    Optional<Language> result =
        Optional.ofNullable(
            optional.orElseGet(() -> languageService.getMessage(key, DEFAULT_LOCALE).orElse(null)));
    return result.map(language -> new MessageFormat(language.getContent(), locale)).orElse(null);
  }
}
