package org.jfantasy.framework.lucene.handler;

import org.jfantasy.framework.lucene.annotations.IndexEmbedBy;
import org.jfantasy.framework.util.reflect.Property;
import org.apache.lucene.document.Document;

import java.util.List;

public class EmbedByFieldHandler extends ByFieldHandler {
    private Class<?> embedBy;

    public EmbedByFieldHandler(Class<?> embedBy, Object obj, Property property, String prefix) {
        super(obj, property, prefix);
        this.embedBy = embedBy;
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
                process(doc, analyze, store, boost);
                break;
            }
        }

    }
}