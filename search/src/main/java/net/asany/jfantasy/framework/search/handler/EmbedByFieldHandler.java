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
package net.asany.jfantasy.framework.search.handler;

import java.util.List;
import net.asany.jfantasy.framework.search.Document;
import net.asany.jfantasy.framework.search.annotations.IndexEmbedBy;
import net.asany.jfantasy.framework.util.reflect.Property;

public class EmbedByFieldHandler extends ByFieldHandler {
  private Class<?> embedBy;

  public EmbedByFieldHandler(Class<?> embedBy, Object obj, Property property, String prefix) {
    super(obj, property, prefix);
    this.embedBy = embedBy;
  }

  public EmbedByFieldHandler(Property property, String prefix) {
    super(property, prefix);
  }

  @Override
  public void handle(Document doc) {
    IndexEmbedBy ieb = this.property.getAnnotation(IndexEmbedBy.class);
    Class<?>[] cls = ieb.value();
    int len = cls.length;
    for (int i = 0; i < len; i++) {
      if (cls[i].equals(this.embedBy)) {
        boolean analyze = false;
        boolean[] as = ieb.analyze();
        if (as.length > 0) {
          analyze = as[i];
        }
        boolean store = false;
        boolean[] ss = ieb.store();
        if (ss.length > 0) {
          store = ss[i];
        }
        float boost = 1.0F;
        float[] bs = ieb.boost();
        if (bs.length > 0) {
          boost = bs[i];
        }
        if (this.obj instanceof List<?>) {
          processList((List<?>) this.obj, doc, analyze, store, boost);
          break;
        }
        process(doc);
        break;
      }
    }
  }
}
