package net.asany.jfantasy.framework.util.ognl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import net.asany.jfantasy.framework.util.common.ObjectUtil;
import net.asany.jfantasy.framework.util.error.OperationFailedException;
import net.asany.jfantasy.framework.util.ognl.typeConverter.DateConverter;
import net.asany.jfantasy.framework.util.reflect.Property;
import net.asany.jfantasy.framework.util.regexp.RegexpUtil;
import ognl.*;

@Slf4j
public class OgnlUtil {

  private static final ConcurrentHashMap<String, OgnlUtil> ognlUtilCache =
      new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, Object> expressions = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<Class<?>, BeanInfo> beanInfoCache =
      new ConcurrentHashMap<Class<?>, BeanInfo>();

  @Setter
  private Map<Class<?>, TypeConverter> typeConverters = new HashMap<Class<?>, TypeConverter>();

  private final TypeConverter defaultTypeConverter =
      new DefaultTypeConverter() {

        @Override
        public Object convertValue(
            OgnlContext context,
            Object root,
            Member member,
            String name,
            Object value,
            Class<?> toType) {
          if (OgnlUtil.this.typeConverters.containsKey(toType)) {
            return OgnlUtil.this
                .typeConverters
                .get(toType)
                .convertValue(context, root, member, name, value, toType);
          } else if (value != null
              && OgnlUtil.this.typeConverters.containsKey(ClassUtil.getRealClass(value))) {
            return OgnlUtil.this
                .typeConverters
                .get(ClassUtil.getRealClass(value))
                .convertValue(context, root, member, name, value, toType);
          } else if ("EMPTY".equals(value) && !ClassUtil.isBasicType(toType)) {
            return ClassUtil.newInstance(toType);
          }
          return super.convertValue(context, root, member, name, value, toType);
        }
      };

  public static boolean containsKey(String key) {
    return ognlUtilCache.containsKey(key);
  }

  public static OgnlUtil getInstance() {
    return getInstance("default");
  }

  public static synchronized OgnlUtil getInstance(String key) {
    OgnlUtil ognlUtil;
    if (!ognlUtilCache.containsKey(key)) {
      ognlUtil = new OgnlUtil();
      ognlUtil.addTypeConverter(Date.class, new DateConverter());
      ognlUtilCache.put(key, ognlUtil);
    } else {
      ognlUtil = ognlUtilCache.get(key);
    }
    return ognlUtil;
  }

  public void addTypeConverter(Class<?> type, TypeConverter typeConverter) {
    this.typeConverters.put(type, typeConverter);
  }

  public void setValue(String name, Object root, Object value) {
    try {
      OgnlContext context = createDefaultContext(root);
      setValue(name, context, root, value);
    } catch (OgnlException e) {
      log.debug(e.getMessage(), e);
    }
  }

  public void setValue(String name, OgnlContext context, Object root, Object value)
      throws OgnlException {
    if ((name.contains(".") || RegexpUtil.isMatch(name, "\\[\\d+\\]$"))
        && !name.trim().startsWith("new")) {
      String[] ns = name.split("\\.");
      String q = "";
      for (int i = 0; i < ns.length - (RegexpUtil.isMatch(name, "\\[\\d+\\]$") ? 0 : 1); i++) {
        String names = q + ns[i];
        if (RegexpUtil.isMatch(names, "\\[\\d+\\]$")) { // is array or list
          int index =
              Integer.parseInt(
                  Objects.requireNonNull(
                      RegexpUtil.parseGroup(names, "\\[(\\d+)\\]$", 1))); // array
          // length
          String arrayName = RegexpUtil.replace(names, "\\[\\d+\\]$", "");
          Object array = getValue(arrayName, root);
          Object parent =
              arrayName.contains(".")
                  ? getValue(RegexpUtil.replace(arrayName, "\\.[^.]+$", ""), root)
                  : root;
          String shortName =
              arrayName.contains(".")
                  ? RegexpUtil.parseGroup(arrayName, "\\.([^.]+)$", 1)
                  : arrayName;
          Property property = ClassUtil.getProperty(parent, shortName);
          if (ClassUtil.isList(property.getPropertyType())) {
            Class<?> listType =
                ClassUtil.getMethodGenericReturnType(property.getReadMethod().getMethod());
            List<?> list = array == null ? new ArrayList<>() : (List<?>) array;
            for (int k = list.size(); k < index + 1; k++) {
              list.add(null);
            }
            OgnlUtil.getInstance().setValue(shortName, parent, list);
            Object object = OgnlUtil.getInstance().getValue(shortName + "[" + index + "]", parent);
            if ((arrayName + "[" + index + "]").equals(name)) {
              continue;
            }
            if (object == null) {
              OgnlUtil.getInstance()
                  .setValue(shortName + "[" + index + "]", parent, ClassUtil.newInstance(listType));
            }
          } else if (ClassUtil.isArray(property.getPropertyType())) {
            if (array == null) {
              array =
                  ClassUtil.newInstance(property.getPropertyType().getComponentType(), index + 1);
              OgnlUtil.getInstance().setValue(shortName, parent, array);
            } else if (Array.getLength(array) <= index) {
              Object arrays =
                  ClassUtil.newInstance(property.getPropertyType().getComponentType(), index + 1);
              for (int j = 0, len = Array.getLength(array); j < len; j++) {
                Array.set(arrays, j, Array.get(array, j));
              }
              OgnlUtil.getInstance().setValue(shortName, parent, arrays);
            }
            if (RegexpUtil.isMatch(name, "\\[\\d+\\]$")) {
              continue;
            }
            Object object = OgnlUtil.getInstance().getValue(shortName + "[" + index + "]", parent);
            if (object == null) {
              OgnlUtil.getInstance()
                  .setValue(
                      shortName + "[" + index + "]",
                      parent,
                      ClassUtil.newInstance(property.getPropertyType().getComponentType()));
            }
          }
        } else {
          Object v = getValue(names, root);
          if (v == null) {
            log.debug("p:" + names);
            Ognl.setValue(compile(names), context, root, "EMPTY");
          }
        }
        q = ns[i] + ".";
      }
    }
    Ognl.setValue(compile(name), context, root, value);
  }

  public <T> T getValue(String key, Object root) {
    return (T) getValue(key, createDefaultContext(root), root);
  }

  public Object getValue(String name, OgnlContext context, Object root) {
    try {
      if (context == null) {
        return Ognl.getValue(name, root);
      }
      return Ognl.getValue(compile(name), context, root);
    } catch (OgnlException e) {
      log.debug(e.getMessage(), e);
      return null;
    }
  }

  public <T> T getValue(String name, Object root, Class<T> resultType) {
    return getValue(name, createDefaultContext(root), root, resultType);
  }

  public <T> T getValue(String name, OgnlContext context, Object root, Class<T> resultType) {
    try {
      return (T) Ognl.getValue(compile(name), context, root, resultType);
    } catch (OgnlException e) {
      log.debug(e.getMessage(), e);
      return null;
    }
  }

  @SneakyThrows
  public Object compile(String expression) {
    Object o = this.expressions.get(expression);
    if (o == null) {
      this.expressions.putIfAbsent(expression, o = Ognl.parseExpression(expression));
    }
    return o;
  }

  public PropertyDescriptor[] getPropertyDescriptors(Object source) throws IntrospectionException {
    BeanInfo beanInfo = getBeanInfo(source);
    return beanInfo.getPropertyDescriptors();
  }

  public PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) throws IntrospectionException {
    BeanInfo beanInfo = getBeanInfo(clazz);
    return beanInfo.getPropertyDescriptors();
  }

  public Map<String, Object> getBeanMap(Object source, String... excludeProperties)
      throws IntrospectionException, OgnlException {
    Map<String, Object> beanMap = new HashMap<>();
    OgnlContext sourceMap = Ognl.createDefaultContext(source);
    PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(source);
    for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
      String propertyName = propertyDescriptor.getDisplayName();
      if (ObjectUtil.indexOf(excludeProperties, propertyName) != -1) {
        continue;
      }
      Method readMethod = propertyDescriptor.getReadMethod();
      if (readMethod != null) {
        Object expr = compile(propertyName);
        Object value = Ognl.getValue(expr, sourceMap, source);
        beanMap.put(propertyName, value);
      } else {
        beanMap.put(propertyName, "There is no read method for " + propertyName);
      }
    }
    return beanMap;
  }

  public BeanInfo getBeanInfo(Object from) throws IntrospectionException {
    return getBeanInfo(ClassUtil.getRealClass(from));
  }

  public BeanInfo getBeanInfo(Class<?> clazz) throws IntrospectionException {
    if (!this.beanInfoCache.containsKey(clazz)) {
      this.beanInfoCache.putIfAbsent(clazz, Introspector.getBeanInfo(clazz, Object.class));
    }
    return this.beanInfoCache.get(clazz);
  }

  @SneakyThrows
  public void setProperties(
      Map<String, ?> props, Object o, OgnlContext context, boolean throwPropertyExceptions) {
    if (props == null) {
      return;
    }

    context = createDefaultContext(context, getTypeConverterFromContext());
    Object oldRoot = Ognl.getRoot(context);
    Ognl.setRoot(context, o);
    for (Map.Entry<String, ?> entry : props.entrySet()) {
      String expression = entry.getKey();
      internalSetProperty(expression, entry.getValue(), o, context, throwPropertyExceptions);
    }
    Ognl.setRoot(context, oldRoot);
  }

  void internalSetProperty(
      String name, Object value, Object o, OgnlContext context, boolean throwPropertyExceptions) {
    try {
      setValue(name, context, o, value);
    } catch (OgnlException e) {
      Throwable reason = e.getReason();
      String msg =
          "Caught OgnlException while setting property '"
              + name
              + "' on type '"
              + o.getClass().getName()
              + "'.";
      Throwable exception = reason == null ? e : reason;
      if (throwPropertyExceptions) {
        throw new OperationFailedException(msg + exception);
      }
    }
  }

  public TypeConverter getTypeConverterFromContext() {
    return this.defaultTypeConverter;
  }

  public OgnlContext createDefaultContext(Object target) {
    return createDefaultContext(target, null);
  }

  public OgnlContext createDefaultContext(Object target, TypeConverter converter) {
    ClassResolver classResolver =
        new ClassResolver() {
          private final DefaultClassResolver resolver = new DefaultClassResolver();

          @Override
          public <T> Class<T> classForName(String className, OgnlContext context)
              throws ClassNotFoundException {
            log.debug(className);
            return resolver.classForName(className, context);
          }
        };
    return Ognl.createDefaultContext(
        target, classResolver, ObjectUtil.defaultValue(converter, this.defaultTypeConverter));
  }

  public void copy(Object from, Object to) {
    copy(from, to, null, null);
  }

  public void copy(
      Object from, Object to, Collection<String> exclusions, Collection<String> inclusions) {
    if ((from == null) || (to == null)) {
      return;
    }
    TypeConverter conv = getTypeConverterFromContext();
    OgnlContext contextFrom = createDefaultContext(from, conv);
    OgnlContext contextTo = createDefaultContext(to, conv);
    PropertyDescriptor[] fromPds;
    PropertyDescriptor[] toPds;
    try {
      fromPds = getPropertyDescriptors(from);
      toPds = getPropertyDescriptors(to);
    } catch (IntrospectionException e) {
      log.debug("An error occured", e);
      return;
    }
    Map<String, PropertyDescriptor> toPdHash = new HashMap<String, PropertyDescriptor>();
    for (PropertyDescriptor toPd : toPds) {
      toPdHash.put(toPd.getName(), toPd);
    }
    for (PropertyDescriptor fromPd : fromPds) {
      if (fromPd.getReadMethod() != null) {
        boolean copy = true;
        if ((exclusions != null) && (exclusions.contains(fromPd.getName()))) {
          copy = false;
        } else if ((inclusions != null) && (!inclusions.contains(fromPd.getName()))) {
          copy = false;
        }
        if (copy) {
          PropertyDescriptor toPd = toPdHash.get(fromPd.getName());
          if ((toPd == null) || (toPd.getWriteMethod() == null)) {
            continue;
          }
          try {
            Object expr = compile(fromPd.getName());
            Object value = Ognl.getValue(expr, contextFrom, from);
            Ognl.setValue(expr, contextTo, to, value);
          } catch (OgnlException e) {
            log.debug(e.getMessage(), e);
          }
        }
      }
    }
  }
}
