package org.jfantasy.framework.dao.hibernate.util;

import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.Type;

public class TypeFactory {

    private TypeFactory(){

    }
    private static final BasicTypeRegistry typeResolver =new BasicTypeRegistry();

    public static Type basic(String name) {
        return typeResolver.getRegisteredType(name);
    }

}
