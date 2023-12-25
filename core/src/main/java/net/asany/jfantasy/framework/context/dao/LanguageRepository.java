package net.asany.jfantasy.framework.context.dao;

import java.util.Optional;
import net.asany.jfantasy.framework.context.bean.Language;
import net.asany.jfantasy.framework.dao.jpa.AnyJpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 国际化
 *
 * @author limaofeng
 */
@Repository
public interface LanguageRepository extends AnyJpaRepository<Language, Long> {
  /**
   * 获取语言信息
   *
   * @param key 国际化 Key
   * @param locale 地区
   * @return Language 语音
   */
  Optional<Language> findByKeyAndLocale(String key, String locale);
}
