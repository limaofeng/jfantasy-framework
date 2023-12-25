package cn.asany.example.demo.service;

import cn.asany.example.TestApplication;
import cn.asany.example.demo.domain.User;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.dao.jpa.PropertyFilter;
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
    PropertyFilter filter =
        PropertyFilter.newFilter()
            .equal("username", "3")
            .or(
                PropertyFilter.newFilter().equal("username", "1").equal("password", "1"),
                PropertyFilter.newFilter().equal("username", "2").equal("password", "2"));
    Page<User> page = this.userService.findPage(pageable, filter);
    log.debug("TotalCount:" + page.getTotalElements());
  }
}
