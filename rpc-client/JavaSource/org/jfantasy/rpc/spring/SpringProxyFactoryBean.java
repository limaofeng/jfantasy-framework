package org.jfantasy.rpc.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.rpc.proxy.RpcProxy;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.Proxy;

public class SpringProxyFactoryBean<T> implements InitializingBean, FactoryBean<T> {

    private static final Log LOG = LogFactory.getLog(SpringProxyFactoryBean.class);

    private String innerClassName;

    private int timeoutInMillis;

    public void setInnerClassName(String innerClassName) {
        this.innerClassName = innerClassName;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getObject() throws Exception {
        Class innerClass = Class.forName(innerClassName);
        return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{innerClass}, new RpcProxy());
    }

    @Override
    public Class<?> getObjectType() {
        try {
            return Class.forName(innerClassName);
        } catch (ClassNotFoundException e) {
            LOG.error(e.getMessage(),e);
        }
        return null;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
