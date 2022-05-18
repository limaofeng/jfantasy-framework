package cn.asany.his.demo.service;

import cn.asany.his.TestApplication;
import cn.asany.his.demo.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.dao.Pagination;
import org.jfantasy.framework.dao.jpa.PropertyFilter;
import org.jfantasy.framework.dao.jpa.PropertyFilterBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    Pagination<User> pager = Pagination.newPager();
    PropertyFilterBuilder builder =
        PropertyFilter.builder()
            .equal("username", "3")
            .or(
                PropertyFilter.builder().equal("username", "1").equal("password", "1"),
                PropertyFilter.builder().equal("username", "2").equal("password", "2"));
    pager = this.userService.findPager(pager, builder.build());
    log.debug("TotalCount:" + pager.getTotalCount());
  }
}
