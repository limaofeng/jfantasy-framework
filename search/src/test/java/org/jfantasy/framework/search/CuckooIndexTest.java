package org.jfantasy.framework.search;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
@Slf4j
class CuckooIndexTest {

  @Autowired private ApplicationContext applicationContext;

  @Autowired private SchedulingTaskExecutor executor;

  @Autowired
  private CuckooIndexFactory cuckooIndexFactory;

  @Test
  void initialize() throws ClassNotFoundException {

  }
}
