/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.log.annotation;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

@Configuration
@Slf4j
public abstract class AbstractLogConfiguration implements ImportAware {

  protected AnnotationAttributes enableLog;

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
    log.debug("reconcileLogManager");
  }
}
