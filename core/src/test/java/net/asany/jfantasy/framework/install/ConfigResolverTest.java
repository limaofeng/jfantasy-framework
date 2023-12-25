package net.asany.jfantasy.framework.install;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

@Slf4j
public class ConfigResolverTest {

  private static final Log LOG = LogFactory.getLog(ConfigResolverTest.class);

  private final ResourcePatternResolver resourcePatternResolver =
      new PathMatchingResourcePatternResolver();

  @BeforeEach
  public void setUp() throws Exception {}

  @AfterEach
  public void tearDown() throws Exception {}

  @Test
  public void testParseConfiguration() throws Exception {
    Resource[] resources = this.resourcePatternResolver.getResources("classpath*:/install.xml");
    for (Resource resource : resources) {
      LOG.debug(resource);
    }
  }
}
