package org.jfantasy.framework.lucene.cluster;

import java.io.Serializable;

public interface ClusterMessage extends Serializable {
    int TYPE_INSERT = 1;
    int TYPE_UPDATE = 2;
    int TYPE_REMOVE = 3;
    int TYPE_REF_BY = 4;

    void setType(int paramInt);

    int getType();
}