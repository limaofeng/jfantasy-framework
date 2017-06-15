package org.jfantasy.framework.dao.mybatis;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.jfantasy.framework.dao.mybatis.binding.MyBatisMapperRegistry;
import org.jfantasy.framework.util.common.ClassUtil;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import static org.springframework.util.ObjectUtils.isEmpty;
import static org.springframework.util.StringUtils.hasLength;
import static org.springframework.util.StringUtils.tokenizeToStringArray;

public class DefaultSqlSessionFactoryBean extends SqlSessionFactoryBean {

    private static final Log LOGGER = LogFactory.getLog(DefaultSqlSessionFactoryBean.class);

    @Override
    protected SqlSessionFactory buildSqlSessionFactory() throws IOException {//NOSONAR
        Resource configLocation = ClassUtil.getFieldValue(this,SqlSessionFactoryBean.class,"configLocation");
        Properties configurationProperties = ClassUtil.getFieldValue(this,SqlSessionFactoryBean.class,"configurationProperties");
        ObjectFactory objectFactory = ClassUtil.getFieldValue(this,SqlSessionFactoryBean.class,"objectFactory");
        ObjectWrapperFactory objectWrapperFactory = ClassUtil.getFieldValue(this,SqlSessionFactoryBean.class,"objectWrapperFactory");
        String typeAliasesPackage = ClassUtil.getFieldValue(this,SqlSessionFactoryBean.class,"typeAliasesPackage");
        Class typeAliasesSuperType = ClassUtil.getFieldValue(this,SqlSessionFactoryBean.class,"typeAliasesSuperType");
        Class<?>[] typeAliases = ClassUtil.getFieldValue(this,SqlSessionFactoryBean.class,"typeAliasesSuperType");
        Interceptor[] plugins = ClassUtil.getFieldValue(this,SqlSessionFactoryBean.class,"plugins");
        String typeHandlersPackage = ClassUtil.getFieldValue(this,SqlSessionFactoryBean.class,"typeHandlersPackage");
        TypeHandler<?>[] typeHandlers = ClassUtil.getFieldValue(this,SqlSessionFactoryBean.class,"typeHandlers");
        DataSource dataSource = ClassUtil.getFieldValue(this,SqlSessionFactoryBean.class,"dataSource");
        Class<? extends VFS> vfs = ClassUtil.getFieldValue(this,SqlSessionFactoryBean.class,"vfs");
        DatabaseIdProvider databaseIdProvider = this.getDatabaseIdProvider();
        TransactionFactory transactionFactory = ClassUtil.getFieldValue(this,SqlSessionFactoryBean.class,"transactionFactory");
        String environment = ClassUtil.getFieldValue(this,SqlSessionFactoryBean.class,"environment");
        Resource[] mapperLocations = ClassUtil.getFieldValue(this,SqlSessionFactoryBean.class,"mapperLocations");
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = ClassUtil.getFieldValue(this,SqlSessionFactoryBean.class,"sqlSessionFactoryBuilder");

        Configuration configuration;

        XMLConfigBuilder xmlConfigBuilder = null;
        if (configLocation != null) {
            xmlConfigBuilder = new XMLConfigBuilder(configLocation.getInputStream(), null, configurationProperties);
            configuration = xmlConfigBuilder.getConfiguration();
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Property 'configLocation' not specified, using default MyBatis Configuration");
            }
            configuration = new Configuration();
            configuration.setVariables(configurationProperties);
        }
        ClassUtil.setFieldValue(configuration, Configuration.class,"mapperRegistry", new MyBatisMapperRegistry(configuration));

        if (objectFactory != null) {
            configuration.setObjectFactory(objectFactory);
        }

        if (objectWrapperFactory != null) {
            configuration.setObjectWrapperFactory(objectWrapperFactory);
        }

        if (hasLength(typeAliasesPackage)) {
            String[] typeAliasPackageArray = tokenizeToStringArray(typeAliasesPackage,
                    ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
            for (String packageToScan : typeAliasPackageArray) {
                configuration.getTypeAliasRegistry().registerAliases(packageToScan,
                        typeAliasesSuperType == null ? Object.class : typeAliasesSuperType);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Scanned package: '" + packageToScan + "' for aliases");
                }
            }
        }

        if (!isEmpty(typeAliases)) {
            for (Class<?> typeAlias : typeAliases) {
                configuration.getTypeAliasRegistry().registerAlias(typeAlias);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Registered type alias: '" + typeAlias + "'");
                }
            }
        }

        if (!isEmpty(plugins)) {
            for (Interceptor plugin : plugins) {
                configuration.addInterceptor(plugin);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Registered plugin: '" + plugin + "'");
                }
            }
        }

        if (hasLength(typeHandlersPackage)) {
            String[] typeHandlersPackageArray = tokenizeToStringArray(typeHandlersPackage,
                    ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
            for (String packageToScan : typeHandlersPackageArray) {
                configuration.getTypeHandlerRegistry().register(packageToScan);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Scanned package: '" + packageToScan + "' for type handlers");
                }
            }
        }

        if (!isEmpty(typeHandlers)) {
            for (TypeHandler<?> typeHandler : typeHandlers) {
                configuration.getTypeHandlerRegistry().register(typeHandler);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Registered type handler: '" + typeHandler + "'");
                }
            }
        }

        if (databaseIdProvider != null) {//fix #64 set databaseId before parse mapper xmls
            try {
                configuration.setDatabaseId(databaseIdProvider.getDatabaseId(dataSource));
            } catch (SQLException e) {
                throw new NestedIOException("Failed getting a databaseId", e);
            }
        }

        if (vfs != null) {
            configuration.setVfsImpl(vfs);
        }

        if (xmlConfigBuilder != null) {
            try {
                xmlConfigBuilder.parse();

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Parsed configuration file: '" + configLocation + "'");
                }
            } catch (Exception ex) {
                throw new NestedIOException("Failed to parse config resource: " + configLocation, ex);
            } finally {
                ErrorContext.instance().reset();
            }
        }

        if (transactionFactory == null) {
            transactionFactory = new SpringManagedTransactionFactory();
            this.setTransactionFactory(transactionFactory);
        }

        configuration.setEnvironment(new Environment(environment, transactionFactory, dataSource));

        if (!isEmpty(mapperLocations)) {
            for (Resource mapperLocation : mapperLocations) {
                if (mapperLocation == null) {
                    continue;
                }

                try {
                    XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(mapperLocation.getInputStream(),
                            configuration, mapperLocation.toString(), configuration.getSqlFragments());
                    xmlMapperBuilder.parse();
                } catch (Exception e) {
                    throw new NestedIOException("Failed to parse mapping resource: '" + mapperLocation + "'", e);
                } finally {
                    ErrorContext.instance().reset();
                }

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Parsed mapper file: '" + mapperLocation + "'");
                }
            }
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Property 'mapperLocations' was not specified or no matching resources found");
            }
        }

        return sqlSessionFactoryBuilder.build(configuration);
    }
}
