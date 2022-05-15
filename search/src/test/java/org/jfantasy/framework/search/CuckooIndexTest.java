package org.jfantasy.framework.search;

import cn.asany.demo.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.search.elastic.ElasticIndexedFactory;
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

  private final CuckooIndexFactory cuckooIndexFactory = new CuckooIndexFactory();

  @Test
  void initialize() throws ClassNotFoundException {
    cuckooIndexFactory.setApplicationContext(applicationContext);
    cuckooIndexFactory.setIndexedFactory(new ElasticIndexedFactory());
    cuckooIndexFactory.setExecutor(executor);
    cuckooIndexFactory.initialize();

    cuckooIndexFactory.rebuild(User.class);
  }
}
