package org.jfantasy.framework.search;

import static org.junit.jupiter.api.Assertions.*;

import cn.asany.demo.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
@Slf4j
class CuckooIndexTest {

  @Autowired private ApplicationContext applicationContext;

  private final CuckooIndex cuckooIndex = new CuckooIndex();

  @Test
  void initialize() throws ClassNotFoundException {
    cuckooIndex.setApplicationContext(applicationContext);
    cuckooIndex.initialize();

    cuckooIndex.rebuild(User.class);
  }
}
