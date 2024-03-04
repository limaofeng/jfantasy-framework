package cn.asany.example.demo.graphql;

import cn.asany.example.demo.domain.User;
import org.jfantasy.graphql.publishers.BasePublisher;
import org.springframework.stereotype.Component;

@Component
public class UserChangePublisher extends BasePublisher<User> {}
