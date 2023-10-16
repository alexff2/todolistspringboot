package br.com.alexandre.todolist.utils;

public class Utils {
  public static void copyNonNullProperties(Object source, Object target) {
    org.springframework.beans.BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
  }

  public static String[] getNullPropertyNames(Object source) {
    final java.beans.BeanInfo beanInfo;
    try {
      beanInfo = java.beans.Introspector.getBeanInfo(source.getClass());
    } catch (java.beans.IntrospectionException e) {
      throw new RuntimeException(e);
    }
    final java.util.List<String> emptyNames = new java.util.ArrayList<String>();
    for (java.beans.PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
      if (pd.getReadMethod() == null)
        continue;
      try {
        if (pd.getReadMethod().invoke(source) == null)
          emptyNames.add(pd.getName());
      } catch (java.lang.Exception e) {
        throw new RuntimeException(e);
      }
    }
    String[] result = new String[emptyNames.size()];
    return emptyNames.toArray(result);
  }
}
