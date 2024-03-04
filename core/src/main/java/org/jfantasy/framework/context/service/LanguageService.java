package org.jfantasy.framework.context.service;

import java.util.Locale;
import java.util.Optional;
import org.jfantasy.framework.context.bean.Language;
import org.jfantasy.framework.context.dao.LanguageRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 语言服务
 *
 * @author limaofeng
 */
@Service
public class LanguageService {

  private static final String CACHE_KEY = "Language";

  private final LanguageRepository languageRepository;

  public LanguageService(LanguageRepository languageRepository) {
    this.languageRepository = languageRepository;
  }

  @Cacheable(
      key = "targetClass + '.' + methodName + '#' + #key + ','+ #locale.language",
      value = CACHE_KEY)
  public Optional<Language> getMessage(String key, Locale locale) {
    return languageRepository.findByKeyAndLocale(key, locale.getLanguage());
  }
}
