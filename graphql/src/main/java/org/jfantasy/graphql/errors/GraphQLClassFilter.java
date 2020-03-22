package org.jfantasy.graphql.errors;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import org.jfantasy.framework.util.common.ClassUtil;
import org.springframework.aop.ClassFilter;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2020/3/22 8:17 下午
 */
public class GraphQLClassFilter implements ClassFilter {
    private Class[] classes;

    public GraphQLClassFilter(Class[] classes) {
        this.classes = classes;
    }

    @Override
    public boolean matches(Class<?> clazz) {
        return ClassUtil.hasInterface(clazz, this.classes);
    }
}
