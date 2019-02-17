package org.jfantasy.framework.lucene.cluster;

public class ClassIdMessage implements ClusterMessage {
    private static final long serialVersionUID = 1L;
    private int type;
    private Class<?> clazz;
    private String id;

    public Class<?> getClazz() {
        return this.clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
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
