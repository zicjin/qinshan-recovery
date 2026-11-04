package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.beans.BeanPropertyEvent;
import com.sunwayworld.framework.beans.BeanPropertyHelper;
import com.sunwayworld.framework.beans.BeanPropertyListener;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.framework.support.domain.BaseData;
import com.sunwayworld.framework.support.domain.Persistable;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.ServletRequest;

public abstract class BeanUtils {
   public static final <T> T getPropertyListenerProxy(T bean) {
      return bean == null ? null : BeanPropertyHelper.getPropertyListenerProxy(bean, false);
   }

   public static final <T> T getPropertyListenerProxy(Class<T> clazz) {
      T instance = ClassUtils.newInstance(clazz);
      return BeanPropertyHelper.getPropertyListenerProxy(instance, true);
   }

   public static final void copyProperties(Object source, Object target, String... ignoreProperties) {
      copyProperties(source, target, BeanUtils.CopyConfig.DEFAULT, ignoreProperties);
   }

   public static final void copyProperties(Object source, Object target, BeanUtils.CopyConfig copyConfig, String... ignoreProperties) {
      if (source != null && target != null) {
         if (copyConfig == null) {
            copyConfig = BeanUtils.CopyConfig.DEFAULT;
         }

         Class<?> targetClass = ClassUtils.getRawType(target.getClass());
         Class<?> sourceClass = ClassUtils.getRawType(source.getClass());
         String resolvedSourcePrefix = Optional.ofNullable(copyConfig.getSourcePrefix()).orElse("");
         String resolvedSourceSuffix = Optional.ofNullable(copyConfig.getSourceSuffix()).orElse("");
         String resolvedTargetPrefix = Optional.ofNullable(copyConfig.getTargetPrefix()).orElse("");
         String resolvedTargetSuffix = Optional.ofNullable(copyConfig.getTargetSuffix()).orElse("");
         Stream<BeanPropertyDescriptor> stream = BeanPropertyHelper.getBeanPropertyDescriptorList(sourceClass)
            .stream()
            .filter(
               s -> (ignoreProperties == null || !ArrayUtils.containsIgnoreCase(ignoreProperties, s.getName()))
                  && StringUtils.startsWith(s.getName(), resolvedSourcePrefix)
                  && StringUtils.endsWith(s.getName(), resolvedSourceSuffix)
            );
         if (BeanPropertyListener.class.isAssignableFrom(source.getClass())) {
            List<BeanPropertyEvent> propertyEventList = ((BeanPropertyListener)source).getChangedPropertyEventList();
            stream = stream.filter(p -> propertyEventList.stream().anyMatch(e -> p.getName().equalsIgnoreCase(e.getPropertyDescriptor().getName())));
         }

         stream.forEach(
            s -> {
               String key = StringUtils.removeEnd(StringUtils.removeStart(s.getName(), resolvedSourcePrefix), resolvedSourceSuffix);
               BeanPropertyDescriptor propertyDescriptor = BeanPropertyHelper.getBeanPropertyDescriptorList(targetClass)
                  .stream()
                  .filter(
                     t -> StringUtils.startsWith(t.getName(), resolvedTargetPrefix)
                        && StringUtils.endsWith(t.getName(), resolvedTargetSuffix)
                        && key.equalsIgnoreCase(StringUtils.removeStart(StringUtils.removeEnd(t.getName(), resolvedTargetSuffix), resolvedTargetPrefix))
                        && t.getPropertyType().equals(s.getPropertyType())
                  )
                  .findFirst()
                  .orElse(null);
               if (propertyDescriptor == null) {
                  if (Persistable.class.isAssignableFrom(targetClass)) {
                     Object sourceValue = ReflectionUtils.invokeMethod(s.getReadMethod(), source);
                     ((Persistable)target).setExt$Item(key.toLowerCase(), ConvertUtils.convert(sourceValue, String.class));
                  }
               } else {
                  Object sourceValue = ReflectionUtils.invokeMethod(s.getReadMethod(), source);
                  ReflectionUtils.invokeMethod(propertyDescriptor.getWriteMethod(), target, sourceValue);
               }
            }
         );
         if (Persistable.class.isAssignableFrom(sourceClass)) {
            copyProperties(((Persistable)source).getExt$(), target, copyConfig, ignoreProperties);
         }
      }
   }

   public static final void copyProperties(ServletRequest source, Object target, String... ignoreProperties) {
      if (source != null && target != null) {
         Stream<BeanPropertyDescriptor> stream = BeanPropertyHelper.getBeanPropertyDescriptorList(target.getClass())
            .stream()
            .filter(t -> ignoreProperties == null || !ArrayUtils.containsIgnoreCase(ignoreProperties, t.getName()));
         stream.forEach(s -> {
            String propertyName = s.getName();
            String parameterValue = source.getParameter(propertyName);
            if (parameterValue != null) {
               ReflectionUtils.invokeMethod(s.getWriteMethod(), target, ConvertUtils.convert(parameterValue, s.getPropertyType()));
            }
         });
      }
   }

   public static final void copyProperties(ResultSet rs, Object target) {
      Class<?> targetClazz = target.getClass();
      boolean isTargetPersistable = Persistable.class.isAssignableFrom(target.getClass());

      try {
         ResultSetMetaData rsmd = rs.getMetaData();
         int columnCount = rsmd.getColumnCount();

         for (int i = 0; i < columnCount; i++) {
            String columnName = rsmd.getColumnName(i + 1);
            Field field = ReflectionUtils.findField(targetClazz, columnName);
            if (field == null) {
               if (isTargetPersistable) {
                  ((Persistable)target).setExt$Item(columnName, ConvertUtils.convert(rs.getObject(i + 1), String.class));
               }
            } else {
               ReflectionUtils.invokeWriteMethod(target, columnName, ConvertUtils.convert(rs.getObject(i + 1), field.getType()));
            }
         }
      } catch (SQLException var9) {
         throw new ApplicationRuntimeException(var9);
      }
   }

   public static final <T, V> T parse(Map<String, V> source, Class<T> clazz, String... ignoreProperties) {
      T item = ClassUtils.newInstance(clazz);
      copyProperties(source, item, ignoreProperties);
      return item;
   }

   public static final <V> void copyProperties(Map<String, V> source, Object target, String... ignoreProperties) {
      copyProperties(source, target, BeanUtils.CopyConfig.DEFAULT, ignoreProperties);
   }

   public static final <V> void copyProperties(Map<String, V> source, Object target, BeanUtils.CopyConfig copyConfig, String... ignoreProperties) {
      if (source != null && target != null && !source.isEmpty()) {
         if (copyConfig == null) {
            copyConfig = BeanUtils.CopyConfig.DEFAULT;
         }

         String resolvedSourcePrefix = Optional.ofNullable(copyConfig.getSourcePrefix()).orElse("");
         String resolvedSourceSuffix = Optional.ofNullable(copyConfig.getSourceSuffix()).orElse("");
         String resolvedTargetPrefix = Optional.ofNullable(copyConfig.getTargetPrefix()).orElse("");
         String resolvedTargetSuffix = Optional.ofNullable(copyConfig.getTargetSuffix()).orElse("");
         List<BeanPropertyDescriptor> propertyDescriptorList = BeanPropertyHelper.getBeanPropertyDescriptorList(target.getClass());
         boolean isTargetPersistable = Persistable.class.isAssignableFrom(target.getClass());
         source.entrySet()
            .stream()
            .forEach(
               e -> {
                  if (!ArrayUtils.containsIgnoreCase(ignoreProperties, e.getKey())
                     && StringUtils.startsWith(e.getKey(), resolvedTargetPrefix)
                     && StringUtils.endsWith(e.getKey(), resolvedTargetSuffix)) {
                     String key = StringUtils.removeEnd(StringUtils.removeStart(e.getKey(), resolvedSourcePrefix), resolvedSourceSuffix);
                     BeanPropertyDescriptor propertyDescriptor = propertyDescriptorList.stream()
                        .filter(
                           p -> StringUtils.startsWith(p.getName(), resolvedTargetPrefix)
                              && StringUtils.endsWith(p.getName(), resolvedTargetSuffix)
                              && key.equalsIgnoreCase(StringUtils.removeStart(StringUtils.removeEnd(p.getName(), resolvedTargetSuffix), resolvedTargetPrefix))
                        )
                        .findFirst()
                        .orElse(null);
                     if (propertyDescriptor != null) {
                        ReflectionUtils.invokeMethod(
                           propertyDescriptor.getWriteMethod(), target, ConvertUtils.convert(e.getValue(), propertyDescriptor.getPropertyType())
                        );
                     } else if (isTargetPersistable) {
                        String resolvedKey = resolvedTargetPrefix + key + resolvedTargetSuffix;
                        ((Persistable)target).setExt$Item(resolvedKey.toLowerCase(), ConvertUtils.convert(e.getValue(), String.class));
                     }
                  }
               }
            );
      }
   }

   public static final <T> void emptyProperties(List<T> beans, String... properties) {
      if (!beans.isEmpty() && properties.length != 0) {
         List<BeanPropertyDescriptor> propertyDescriptorList = BeanPropertyHelper.getBeanPropertyDescriptorList(beans.get(0).getClass())
            .stream()
            .filter(p -> ArrayUtils.contains(properties, p.getName()))
            .collect(Collectors.toList());
         if (!propertyDescriptorList.isEmpty()) {
            for (T bean : beans) {
               propertyDescriptorList.stream().forEach(p -> ReflectionUtils.invokeMethod(p.getWriteMethod(), bean, null));
            }
         }
      }
   }

   public static <T> Map<String, Object> toMap(T bean) {
      Map<String, Object> result = new HashMap<>();
      if (bean == null) {
         return result;
      } else {
         Class<T> type = (Class<T>)bean.getClass();
         BeanPropertyHelper.getBeanPropertyDescriptorList(type)
            .forEach(p -> result.put(p.getName().toLowerCase(), ReflectionUtils.invokeMethod(p.getReadMethod(), bean)));
         return result;
      }
   }

   public static <T> Map<String, Object> deeplyToMap(T bean) {
      Map<String, Object> result = new HashMap<>();
      if (bean == null) {
         return result;
      } else {
         Class<T> type = (Class<T>)bean.getClass();
         BeanPropertyHelper.getBeanPropertyDescriptorList(type)
            .stream()
            .filter(p -> !Map.class.isAssignableFrom(p.getPropertyType()))
            .forEach(p -> result.put(p.getName().toLowerCase(), ReflectionUtils.invokeMethod(p.getReadMethod(), bean)));
         BeanPropertyHelper.getBeanPropertyDescriptorList(type).stream().filter(p -> Map.class.isAssignableFrom(p.getPropertyType())).forEach(p -> {
            if (p.getPropertyValue(bean) != null) {
               ((Map<?, ?>)p.getPropertyValue(bean)).entrySet().forEach(e -> result.put(ObjectUtils.toString(e.getKey()), e.getValue()));
            }
         });
         return result;
      }
   }

   public static <T> boolean match(T item, Map<String, Object> map) {
      if (item == null) {
         return false;
      } else if (map.isEmpty()) {
         return true;
      } else {
         Class<T> clazz = (Class<T>)item.getClass();

         for (Entry<String, Object> entry : map.entrySet()) {
            Field field = ReflectionUtils.findField(clazz, entry.getKey());
            Object value = ConvertUtils.convert(entry.getValue(), field.getType());
            if (!ObjectUtils.equals(value, ReflectionUtils.getFieldValue(item, entry.getKey()))) {
               Class<T> type = (Class<T>)item.getClass();
               if (!BaseData.class.isAssignableFrom(type)) {
                  return false;
               }

               BaseData data = (BaseData)item;
               if (!ObjectUtils.equals(entry.getValue(), data.getExt$().get(entry.getKey()))) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public static <T> boolean isEmpty(T item) {
      if (item == null) {
         return true;
      } else {
         Class<T> type = (Class<T>)item.getClass();
         if (BaseData.class.isAssignableFrom(type) && !((BaseData)item).getExt$().isEmpty()) {
            return false;
         } else if (BeanPropertyListener.class.isAssignableFrom(type)) {
            return ((BeanPropertyListener)item).getChangedPropertyEventList().isEmpty();
         } else {
            for (BeanPropertyDescriptor bpd : BeanPropertyHelper.getBeanPropertyDescriptorList(type)) {
               if (!bpd.isExt$BeanProperty() && bpd.getPropertyValue(item) != null) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public static <T, V> V getPropertyValue(T item, String propertyName) {
      if (item == null) {
         return null;
      } else {
         Class<T> type = (Class<T>)item.getClass();
         BeanPropertyDescriptor pd = BeanPropertyHelper.getBeanPropertyDescriptorList(type)
            .stream()
            .filter(p -> p.getName().equalsIgnoreCase(propertyName))
            .findAny()
            .orElse(null);
         if (pd != null) {
            return pd.getPropertyValue(item);
         } else {
            return (V)(BaseData.class.isAssignableFrom(type) ? ((BaseData)item).getExt$().get(propertyName) : null);
         }
      }
   }

   public static <T, V> V getPropertyValue(T oldItem, T newItem, String propertyName) {
      if (newItem != null) {
         return getPropertyValue(newItem, propertyName);
      } else {
         return oldItem != null ? getPropertyValue(oldItem, propertyName) : null;
      }
   }

   public static <T, V> void setPropertyValue(T item, String propertyName, V value) {
      if (item != null) {
         Class<T> type = (Class<T>)item.getClass();
         BeanPropertyDescriptor pd = BeanPropertyHelper.getBeanPropertyDescriptorList(type)
            .stream()
            .filter(p -> p.getName().equalsIgnoreCase(propertyName))
            .findAny()
            .orElse(null);
         if (pd != null) {
            pd.setPropertyValue(item, value);
         }

         if (BaseData.class.isAssignableFrom(type)) {
            ((BaseData)item).getExt$().put(propertyName, ObjectUtils.toString(value));
         }
      }
   }

   public static class CopyConfig {
      private static BeanUtils.CopyConfig DEFAULT = new BeanUtils.CopyConfig();
      private String sourcePrefix;
      private String sourceSuffix;
      private String targetPrefix;
      private String targetSuffix;

      public String getSourcePrefix() {
         return this.sourcePrefix;
      }

      public void setSourcePrefix(String sourcePrefix) {
         this.sourcePrefix = sourcePrefix;
      }

      public String getSourceSuffix() {
         return this.sourceSuffix;
      }

      public void setSourceSuffix(String sourceSuffix) {
         this.sourceSuffix = sourceSuffix;
      }

      public String getTargetPrefix() {
         return this.targetPrefix;
      }

      public void setTargetPrefix(String targetPrefix) {
         this.targetPrefix = targetPrefix;
      }

      public String getTargetSuffix() {
         return this.targetSuffix;
      }

      public void setTargetSuffix(String targetSuffix) {
         this.targetSuffix = targetSuffix;
      }
   }
}
