package org.jfantasy.framework.dao.hibernate.generator;

import java.io.Serializable;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.jfantasy.framework.dao.mybatis.keygen.util.DataBaseKeyGenerator;
import org.jfantasy.framework.spring.SpringBeanUtils;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.StringUtil;

/**
 * 自定义序列生成器
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-1-14 下午02:07:25
 */
public class SequenceGenerator implements IdentifierGenerator {

  private DataBaseKeyGenerator baseKeyGenerator;

  public static final String KEY_NAME = "keyName";

  private String keyName;
  private String entityName;

  @Override
  public void configure(Type type, Properties params, ServiceRegistry serviceRegistry)
      throws MappingException {
    this.entityName = params.getProperty("entity_name");
    this.keyName =
        StringUtil.defaultValue(
                params.getProperty(KEY_NAME),
                params.getProperty("target_table") + ":" + params.getProperty("target_column"))
            .toLowerCase();
    this.baseKeyGenerator = SpringBeanUtils.getBean(DataBaseKeyGenerator.class);
  }

  @Override
  public Serializable generate(SharedSessionContractImplementor session, Object object)
      throws HibernateException {
    final EntityPersister persister =
        session.getFactory().getMetamodel().entityPersister(entityName);
    Serializable id = persister.getIdentifier(object, session);
    if (id != null) {
      return id;
    }
    return this.baseKeyGenerator.nextValue(
        StringUtil.defaultValue(keyName, ClassUtil.getRealClass(object).getName()));
  }
}
