package org.jfantasy.framework.install;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class ConfigResolverTest {

  private static final Log LOG = LogFactory.getLog(ConfigResolverTest.class);

  private ResourcePatternResolver resourcePatternResolver =
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
