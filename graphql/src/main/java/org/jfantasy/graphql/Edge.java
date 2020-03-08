package org.jfantasy.graphql;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019-04-02 18:23
 */
public interface Edge<T> {
    String getCursor();
    T getNode();
    void setCursor(String cursor);
    void setNode(T node);
}
