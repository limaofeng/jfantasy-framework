package org.jfantasy.framework.context;

import org.jfantasy.framework.context.bean.Language;
import org.jfantasy.framework.context.dao.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractMessageSource;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * 数据库消息源
 *
 * @author limaofeng
 */
public class DatabaseMessageSource extends AbstractMessageSource {

    @Autowired
    private LanguageRepository languageRepository;

    private static final String DEFAULT_LOCALE_CODE = "zh";

    @Override
    protected MessageFormat resolveCode(String key, Locale locale) {
        Language message = languageRepository.findByKeyAndLocale(key, locale.getLanguage());
        if (message == null) {
            message = languageRepository.findByKeyAndLocale(key, DEFAULT_LOCALE_CODE);
        }
        if (message == null) {
            return null;
        }
        return new MessageFormat(message.getContent(), locale);
    }
}
