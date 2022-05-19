package cn.asany.his.demo.service;

import cn.asany.his.TestApplication;
import cn.asany.his.demo.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.dao.jpa.PropertyFilter;
import org.jfantasy.framework.dao.jpa.PropertyFilterBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = TestApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Slf4j
class UserServiceTest {

  @Autowired private UserService userService;

  @Test
  void findPager() {
    Pageable pageable = Pageable.ofSize(10);
    PropertyFilterBuilder builder =
        PropertyFilter.builder()
            .equal("username", "3")
            .or(
                PropertyFilter.builder().equal("username", "1").equal("password", "1"),
                PropertyFilter.builder().equal("username", "2").equal("password", "2"));
    Page<User> page = this.userService.findPage(pageable, builder.build());
    log.debug("TotalCount:" + page.getTotalElements());
  }
}
