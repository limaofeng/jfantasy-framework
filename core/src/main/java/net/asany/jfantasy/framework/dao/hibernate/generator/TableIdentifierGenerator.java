package net.asany.jfantasy.framework.dao.hibernate.generator;

import java.lang.reflect.Member;
import java.util.Properties;
import net.asany.jfantasy.framework.dao.hibernate.annotations.TableGenerator;
import net.asany.jfantasy.framework.dao.mybatis.keygen.util.DatabaseSequenceGenerator;
import net.asany.jfantasy.framework.util.common.StringUtil;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;
import org.hibernate.mapping.RootClass;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

/**
 * 自定义序列生成器
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-1-14 下午02:07:25
 */
public class TableIdentifierGenerator implements IdentifierGenerator {

  private DatabaseSequenceGenerator baseKeyGenerator;

  public static final String KEY_NAME = "keyName";

  private String keyName;
  private String entityName;

  public TableIdentifierGenerator() {}

  public TableIdentifierGenerator(
      TableGenerator tableGenerator,
      Member ignoredMember,
      CustomIdGeneratorCreationContext context) {
    this();
    RootClass rootClass = context.getRootClass();
    this.entityName = rootClass.getEntityName();
    if (StringUtil.isNotBlank(tableGenerator.name())) {
      this.keyName = tableGenerator.name();
    } else
      this.keyName =
          (rootClass.getTable().getName() + ":" + rootClass.getIdentifierProperty().getName())
              .toLowerCase();
    this.baseKeyGenerator =
        DatabaseSequenceGenerator.create(
            keyName,
            tableGenerator.incrementSize(),
            tableGenerator.allocationSize(),
            tableGenerator.initialValue());
  }

  @Override
  public void configure(Type type, Properties params, ServiceRegistry serviceRegistry)
      throws MappingException {
    this.entityName = params.getProperty("entity_name");
    this.keyName =
        StringUtil.defaultValue(
                params.getProperty(KEY_NAME),
                params.getProperty("target_table") + ":" + params.getProperty("target_column"))
            .toLowerCase();
    this.baseKeyGenerator = DatabaseSequenceGenerator.create(keyName);
  }

  @Override
  public Object generate(SharedSessionContractImplementor session, Object object)
      throws HibernateException {
    final EntityPersister descriptor =
        session.getFactory().getMappingMetamodel().findEntityDescriptor(entityName);
    Object id = descriptor.getIdentifier(object, session);
    if (id != null) {
      return id;
    }
    return this.baseKeyGenerator.nextValue();
  }
}
