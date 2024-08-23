package cn.asany.example.demo.graphql;

import cn.asany.example.demo.converter.UserConverter;
import cn.asany.example.demo.domain.User;
import cn.asany.example.demo.graphql.inputs.UserCreateInput;
import cn.asany.example.demo.service.UserService;
import com.zaxxer.hikari.HikariDataSource;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.dao.datasource.MultiDataSourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * @author limaofeng
 */
@Slf4j
@Component
public class UserGraphQLMutationResolver implements GraphQLMutationResolver {

  private final UserService userService;
  private final UserConverter userConverter;
  private final UserChangePublisher userChangePublisher;
  private final MultiDataSourceManager multiDataSourceManager;

  public UserGraphQLMutationResolver(
      UserChangePublisher userChangePublisher,
      UserService userService,
      UserConverter userConverter,
      @Autowired(required = false) MultiDataSourceManager multiDataSourceManager) {
    this.userService = userService;
    this.userConverter = userConverter;
    this.userChangePublisher = userChangePublisher;
    this.multiDataSourceManager = multiDataSourceManager;
  }

  public User createDemoUser(@Validated UserCreateInput input) {
    User user = userConverter.toUser(input);
    try {
      return userService.save(user);
    } finally {
      userChangePublisher.emit(user);
    }
  }

  public User updateDemoUser(String id, @Validated UserCreateInput input) {
    User user = userConverter.toUser(input);
    try {
      user = userService.update(id, true, user);
      //      log.debug("hasPropertyChanged: password: {}",
      // entityStateTracker.hasPropertyChanged(user, "password"));
      return user;
    } finally {
      userChangePublisher.emit(user);
    }
  }

  public Boolean createTenant(String name) throws Exception {
    DataSourceProperties properties = new DataSourceProperties();
    properties.setUrl(
        "jdbc:mysql://"
            + System.getenv("DATABASE_HOST")
            + "/demo_21?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull");
    properties.setUsername(System.getenv("DATABASE_USERNAME"));
    properties.setPassword(System.getenv("DATABASE_PASSWORD"));
    properties.afterPropertiesSet();
    multiDataSourceManager.addDataSource(
        name, properties.initializeDataSourceBuilder().type(HikariDataSource.class).build());
    return Boolean.TRUE;
  }
}
