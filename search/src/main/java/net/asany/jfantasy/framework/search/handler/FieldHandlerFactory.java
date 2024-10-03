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

import jakarta.persistence.Id;
import net.asany.jfantasy.framework.search.annotations.*;
import net.asany.jfantasy.framework.util.reflect.Property;

public class FieldHandlerFactory {
  private FieldHandlerFactory() {}

  private static final String DOT = ".";

  public static boolean isHandler(Property p) {
    Class[] tClases = {
      Id.class,
      IndexProperty.class,
      IndexEmbed.class,
      IndexEmbedList.class,
      IndexRef.class,
      IndexRefList.class
    };
    for (Class tClass : tClases) {
      if (p.getAnnotation(tClass) != null) {
        return true;
      }
    }
    return false;
  }

  public static FieldHandler create(Object obj, Property p, String prefix) {
    FieldHandler handler = null;
    if (p.getAnnotation(Id.class) != null) {
      handler = new IdFieldHandler(obj, p, "");
    } else if (p.getAnnotation(IndexProperty.class) != null) {
      handler = new PropertyFieldHandler(obj, p, prefix);
    } else if (p.getAnnotation(IndexEmbed.class) != null) {
      handler = new EmbedFieldHandler(obj, p, p.getName() + DOT);
    } else if (p.getAnnotation(IndexEmbedList.class) != null) {
      handler = new EmbedListFieldHandler(obj, p, p.getName() + DOT);
    } else if (p.getAnnotation(IndexRef.class) != null) {
      handler = new RefFieldHandler(obj, p, p.getName() + DOT);
    } else if (p.getAnnotation(IndexRefList.class) != null) {
      handler = new RefListFieldHandler(obj, p, p.getName() + DOT);
    }
    return handler;
  }

  public static FieldHandler create(Property p, String prefix) {
    FieldHandler handler = null;
    if (p.getAnnotation(Id.class) != null) {
      handler = new IdFieldHandler(p, "");
    } else if (p.getAnnotation(IndexProperty.class) != null) {
      handler = new PropertyFieldHandler(p, prefix);
    } else if (p.getAnnotation(IndexEmbed.class) != null) {
      handler = new EmbedFieldHandler(p, p.getName() + DOT);
    } else if (p.getAnnotation(IndexEmbedList.class) != null) {
      handler = new EmbedListFieldHandler(p, p.getName() + DOT);
    } else if (p.getAnnotation(IndexRef.class) != null) {
      handler = new RefFieldHandler(p, p.getName() + DOT);
    } else if (p.getAnnotation(IndexRefList.class) != null) {
      handler = new RefListFieldHandler(p, p.getName() + DOT);
    }
    return handler;
  }
}
