package org.jfantasy.framework.lucene.cluster;


import java.io.Serializable;

public class EntityMessage<T extends Serializable> implements ClusterMessage {
    private static final long serialVersionUID = 1L;
    private int type;
    private T entity;

    public T getEntity() {
        return this.entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    @Override
    public int getType() {
        return this.type;
    }

    @Override
    public void setType(int type) {
        this.type = type;
    }
}
