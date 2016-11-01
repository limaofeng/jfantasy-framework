package org.jfantasy.framework.jackson;


public class FilterItem {

    enum Pattern{
        IGNORE,ALLOW
    }

    private Class clazz;
    private Pattern pattern;
    private String[] fields;

    public static FilterItem ignore(Class clazz,String... fields){
        FilterItem item = new FilterItem();
        item.clazz = clazz;
        item.fields = fields;
        item.pattern = Pattern.IGNORE;
        return item;
    }

    public static FilterItem allow(Class clazz,String... fields){
        FilterItem item = new FilterItem();
        item.clazz = clazz;
        item.fields = fields;
        item.pattern = Pattern.IGNORE;
        return item;
    }

    public Class getClazz() {
        return clazz;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String[] getFields() {
        return fields;
    }
}
