package org.jfantasy.framework.spring.config;

import org.apache.ibatis.type.TypeAliasRegistry;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.mybatis.binding.MyBatisMapperRegistry;
import org.jfantasy.framework.dao.mybatis.dialect.MySQLDialect;
import org.jfantasy.framework.dao.mybatis.interceptors.AutoKeyInterceptor;
import org.jfantasy.framework.dao.mybatis.interceptors.LimitInterceptor;
import org.jfantasy.framework.dao.mybatis.interceptors.MultiDataSourceInterceptor;
import org.jfantasy.framework.dao.mybatis.keygen.bean.Sequence;
import org.jfantasy.framework.dao.mybatis.keygen.util.DataBaseKeyGenerator;
import org.jfantasy.framework.dao.mybatis.sqlmapper.SqlMapper;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.PropertiesHelper;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@EntityScan("org.jfantasy.framework.dao.mybatis.keygen.bean")
@MapperScan(markerInterface = SqlMapper.class,basePackages = "org.jfantasy.framework.dao.mybatis.keygen")
@EnableConfigurationProperties(MybatisProperties.class)
public class MyBatisConfig {

    @Autowired
    public MyBatisConfig(MybatisProperties properties){
        PropertiesHelper helper = PropertiesHelper.load("application.properties");
        properties.setMapperLocations(helper.getMergeProperty("mybatis.mapper-locations"));
    }

    @Bean
    public DataBaseKeyGenerator dataBaseKeyGenerator(@Value("${dataBaseKey.poolSize:10}") String dataBaseKeyPoolSize) {
        DataBaseKeyGenerator dataBaseKeyGenerator = new DataBaseKeyGenerator();
        dataBaseKeyGenerator.setPoolSize(Integer.valueOf(dataBaseKeyPoolSize));
        return dataBaseKeyGenerator;
    }

    @Bean
    public ConfigurationCustomizer mybatisConfigurationCustomizer() {
        return configuration -> {
            ClassUtil.setFieldValue(configuration, "mapperRegistry", new MyBatisMapperRegistry(configuration));

            configuration.setCacheEnabled(false);
            configuration.setLazyLoadingEnabled(true);
            configuration.setAggressiveLazyLoading(false);

            Properties settings = new Properties();
            settings.setProperty("dialectClass", "org.jfantasy.framework.dao.mybatis.dialect.MySQLDialect");
            configuration.setVariables(settings);

            configuration.addInterceptor(new MultiDataSourceInterceptor());
            configuration.addInterceptor(new AutoKeyInterceptor());
            configuration.addInterceptor(new LimitInterceptor(MySQLDialect.class));

            TypeAliasRegistry typeAliasRegistry = configuration.getTypeAliasRegistry();
            typeAliasRegistry.registerAlias(Pager.class);
            typeAliasRegistry.registerAlias(Sequence.class);
        };
    }

}
