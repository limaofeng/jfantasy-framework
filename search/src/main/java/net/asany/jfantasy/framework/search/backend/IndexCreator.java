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
package net.asany.jfantasy.framework.search.backend;

import net.asany.jfantasy.framework.search.Document;
import net.asany.jfantasy.framework.search.annotations.BoostSwitch;
import net.asany.jfantasy.framework.search.cache.PropertysCache;
import net.asany.jfantasy.framework.search.handler.FieldHandler;
import net.asany.jfantasy.framework.search.handler.FieldHandlerFactory;
import net.asany.jfantasy.framework.util.reflect.Property;

/**
 * 索引文件 生成器
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-1-27 上午09:46:11
 */
public class IndexCreator {
  protected Object obj;
  protected String prefix;

  public IndexCreator(Object obj, String prefix) {
    this.obj = obj;
    this.prefix = prefix;
  }

  public void create(Document doc) {
    for (Property p : PropertysCache.getInstance().get(this.obj.getClass())) {
      BoostSwitch bs = p.getAnnotation(BoostSwitch.class);
      if (bs != null) {
        CompareChecker checker = new CompareChecker(this.obj);
        boolean fit = checker.isFit(p, bs.compare(), bs.value());
        if (fit) {
          doc.setBoost(bs.fit());
        } else {
          doc.setBoost(bs.unfit());
        }
      }
      if (FieldHandlerFactory.isHandler(p) && p.getValue(this.obj) != null) {
        FieldHandler handler = FieldHandlerFactory.create(this.obj, p, this.prefix);
        if (handler != null) {
          handler.handle(doc);
        }
      }
    }
  }
}
