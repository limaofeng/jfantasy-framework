package net.asany.jfantasy.framework.util.reflect;

public interface IClassFactory {

  <T> IClass<T> getClass(Class<T> paramClass);
}
