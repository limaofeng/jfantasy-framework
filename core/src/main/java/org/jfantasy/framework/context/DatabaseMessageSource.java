package org.jfantasy.framework.context;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jfantasy.framework.context.bean.Language;
import org.jfantasy.framework.context.service.LanguageService;
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
