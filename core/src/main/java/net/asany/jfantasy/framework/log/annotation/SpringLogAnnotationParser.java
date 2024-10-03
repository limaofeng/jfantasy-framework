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

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("serial")
public class SpringLogAnnotationParser implements LogAnnotationParser, Serializable {

  @Override
  public Collection<LogOperation> parseLogAnnotations(AnnotatedElement ae) {
    Collection<LogOperation> ops = null;
    Collection<Log> logs = getAnnotations(ae, Log.class);
    if (logs != null) {
      ops = lazyInit(ops);
      for (Log LOG : logs) {
        ops.add(parseAnnotation(ae, LOG));
      }
    }
    return ops;
  }

  LogOperation parseAnnotation(AnnotatedElement ae, Log LOG) {
    LogOperation cuo = new LogOperation();
    cuo.setCondition(LOG.condition());
    cuo.setType(LOG.type());
    cuo.setText(LOG.text());
    return cuo;
  }

  private <T extends Annotation> Collection<LogOperation> lazyInit(Collection<LogOperation> ops) {
    return ops != null ? ops : new ArrayList<>(1);
  }

  private static <T extends Annotation> Collection<T> getAnnotations(
      AnnotatedElement ae, Class<T> annotationType) {
    Collection<T> anns = new ArrayList<>(2);
    T ann = ae.getAnnotation(annotationType);
    if (ann != null) {
      anns.add(ann);
    }
    for (Annotation metaAnn : ae.getAnnotations()) {
      ann = metaAnn.annotationType().getAnnotation(annotationType);
      if (ann != null) {
        anns.add(ann);
      }
    }
    return anns.isEmpty() ? null : anns;
  }
}
