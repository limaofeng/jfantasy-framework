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
package net.asany.jfantasy.framework.context.service;

import java.util.Locale;
import java.util.Optional;
import net.asany.jfantasy.framework.context.bean.Language;
import net.asany.jfantasy.framework.context.dao.LanguageRepository;
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
