package org.jfantasy.framework.log.annotation;

import javax.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

@Configuration
public abstract class AbstractLogConfiguration implements ImportAware {

  protected AnnotationAttributes enableLog;

  private static final Logger LOGGER = LogManager.getLogger(AbstractLogConfiguration.class);

  @Override
  public void setImportMetadata(AnnotationMetadata importMetadata) {
    this.enableLog =
        AnnotationAttributes.fromMap(
            importMetadata.getAnnotationAttributes(EnableLog.class.getName(), false));
    Assert.notNull(
        this.enableLog,
        "@EnableLog is not present on importing class " + importMetadata.getClassName());
  }

  @PostConstruct
  protected void reconcileLogManager() {
    LOGGER.debug("reconcileLogManager");
  }
}
