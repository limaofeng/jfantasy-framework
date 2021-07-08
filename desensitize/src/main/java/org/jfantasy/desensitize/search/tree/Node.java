package org.jfantasy.desensitize.search.tree;

import java.util.HashMap;
import java.util.Map;

public class Node {
    private Map<String, Node> children = new HashMap(0);
    private boolean isEnd = false;
    private String word;
    private double level = 0.0D;

    public Node() {
    }

    public Node addChar(char c) {
        String cStr = String.valueOf(c);
        Node node = (Node) this.children.get(cStr);
        if (node == null) {
            node = new Node();
            this.children.put(cStr, node);
        }

        return node;
    }

    public Node findChar(char c) {
        String cStr = String.valueOf(c);
        return (Node) this.children.get(cStr);
    }

    public boolean isEnd() {
        return this.isEnd;
    }

    public void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }

    public double getLevel() {
        return this.level;
    }

    public void setLevel(double level) {
        this.level = level;
    }

    public String getWord() {
        return this.word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
