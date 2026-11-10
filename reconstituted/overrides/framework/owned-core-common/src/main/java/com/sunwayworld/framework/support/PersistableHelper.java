package com.sunwayworld.framework.support;

import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.beans.BeanPropertyHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.support.domain.Activatable;
import com.sunwayworld.framework.support.domain.BaseData;
import com.sunwayworld.framework.support.domain.Modified;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.support.domain.Suspendable;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.utils.BeanUtils.CopyConfig;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public abstract class PersistableHelper {
   private static final String[] BEAN_BASIC_PROPERTIES = new String[]{
      "createdById",
      "createdByName",
      "createdByOrgId",
      "createdByOrgName",
      "createdTime",
      "activatedFlag",
      "activatedById",
      "activatedByName",
      "activatedTime",
      "lastSuspendedFlag",
      "lastSuspendedById",
      "lastSuspendedByName",
      "lastSuspendedTime",
      "lastRejectedFlag",
      "lastRejectedById",
      "lastRejectedByName",
      "lastRejectedTime",
      "activatedFlag",
      "activatedById",
      "activatedByName",
      "activatedTime",
      "processStatus"
   };

   public static String[] ignoreProperties(String... ignoreProperties) {
      return ArrayUtils.concat(BEAN_BASIC_PROPERTIES, ignoreProperties);
   }

   public static <M extends Persistable<?>, P extends Persistable<?>> M mapToModified(P item, Class<M> modifiedClass) {
      M m = (M)ClassUtils.newInstance(modifiedClass);
      if (Modified.class.isAssignableFrom(modifiedClass)) {
         CopyConfig config = new CopyConfig();
         config.setTargetPrefix("old");
         BeanUtils.copyProperties(item, m, config);
      }

      BeanUtils.copyProperties(item, m, BEAN_BASIC_PROPERTIES);
      m.setId(null);
      BeanPropertyHelper.getBeanPropertyDescriptorList(modifiedClass).forEach(p -> {
         String var10000 = m.getExt$().remove(p.getName().toLowerCase());
      });
      return m;
   }

   public static <M extends Persistable<ID>, P extends Persistable<ID>, ID extends Serializable> void remap(M item, P orig) {
      BeanUtils.copyProperties(item, orig, BEAN_BASIC_PROPERTIES);
      if (Modified.class.isAssignableFrom(item.getClass())) {
         orig.setId((ID)((Modified)item).getOldId());
      }
   }

   public static <P extends BaseData> P mapToPersistable(Map<String, Object> map, Class<P> persistableClass) {
      if (map != null && !map.isEmpty()) {
         P instance = (P)ClassUtils.newInstance(persistableClass);
         map.forEach((k, v) -> {
            BeanPropertyDescriptor propertyDescriptor = BeanPropertyHelper.getBeanPropertyDescriptor(persistableClass, k);
            if (propertyDescriptor == null) {
               String text = ConvertUtils.convert(v, String.class);
               if (text != null) {
                  if (StringUtils.endsWithIgnoreCase(k, "date")) {
                     try {
                        text = DateTimeUtils.formatLocalDate(DateTimeUtils.parseLocalDate(text));
                     } catch (Exception var8) {
                     }
                  } else if (StringUtils.endsWithIgnoreCase(k, "time")) {
                     try {
                        text = DateTimeUtils.formatLocalDateTime(DateTimeUtils.parseLocalDateTime(text));
                     } catch (Exception var7) {
                     }
                  }
               }

               instance.setExt$Item(k.toLowerCase(), text);
            } else {
               ReflectionUtils.invokeMethod(propertyDescriptor.getWriteMethod(), instance, ConvertUtils.convert(v, propertyDescriptor.getPropertyType()));
            }
         });
         return instance;
      } else {
         return null;
      }
   }

   public static <P extends Persistable<?>> Object getPropertyValue(P item, String propertyName) {
      if (item == null) {
         return null;
      } else {
         Field field = ReflectionUtils.findField(item.getClass(), propertyName);
         return field == null ? item.getExt$().get(propertyName.toLowerCase()) : ReflectionUtils.invokeReadMethod(item, propertyName);
      }
   }

   public static <P extends Persistable<?>> void setPropertyValue(P item, String propertyName, Object value) {
      if (item != null) {
         ReflectionUtils.invokeWriteMethod(item, propertyName, value);
      }
   }

   public static <T> void resetBasicProperties(T item) {
      for (BeanPropertyDescriptor propertyDescriptor : BeanPropertyHelper.getBeanPropertyDescriptorList(item.getClass())) {
         if (ArrayUtils.containsIgnoreCase(BEAN_BASIC_PROPERTIES, propertyDescriptor.getName())) {
            ReflectionUtils.invokeMethod(propertyDescriptor.getWriteMethod(), item, new Object[]{null});
         }
      }
   }

   public static void assignDefaultData(Object item) {
      BeanPropertyHelper.getBeanPropertyDescriptorList(item.getClass()).forEach(p -> {
         Object value = ReflectionUtils.invokeMethod(p.getReadMethod(), item);
         if (value == null) {
            NotNull notNull = p.getProperty().getAnnotation(NotNull.class);
            if (notNull != null) {
               if (notNull.defaultValue() != null) {
                  ReflectionUtils.invokeMethod(p.getWriteMethod(), item, ConvertUtils.convert(notNull.defaultValue(), p.getPropertyType()));
               } else if (LocalDate.class.isAssignableFrom(p.getPropertyType())) {
                  ReflectionUtils.invokeMethod(p.getWriteMethod(), item, LocalDate.now());
               } else if (LocalDateTime.class.isAssignableFrom(p.getPropertyType())) {
                  ReflectionUtils.invokeMethod(p.getWriteMethod(), item, LocalDateTime.now());
               } else {
                  if (!Number.class.isAssignableFrom(p.getPropertyType())) {
                     throw new NullPointerException();
                  }

                  ReflectionUtils.invokeMethod(p.getWriteMethod(), item, ConvertUtils.convert("0", p.getPropertyType()));
               }
            }
         }
      });
   }

   public static void assignCreatedUserData(Object item) {
      BeanPropertyHelper.getBeanPropertyDescriptorList(item.getClass()).forEach(p -> {
         if (PersistableMetadataHelper.isCreatedByIdPropertyDescriptor(p)) {
            if (ReflectionUtils.invokeMethod(p.getReadMethod(), item) == null) {
               ReflectionUtils.invokeMethod(p.getWriteMethod(), item, LocalContextHelper.getLoginUserId());
            }
         } else if (PersistableMetadataHelper.isCreatedByNamePropertyDescriptor(p)) {
            if (ReflectionUtils.invokeMethod(p.getReadMethod(), item) == null) {
               ReflectionUtils.invokeMethod(p.getWriteMethod(), item, LocalContextHelper.getLoginUserName());
            }
         } else if (PersistableMetadataHelper.isCreatedByOrgIdPropertyDescriptor(p)) {
            if (ReflectionUtils.invokeMethod(p.getReadMethod(), item) == null) {
               ReflectionUtils.invokeMethod(p.getWriteMethod(), item, LocalContextHelper.getLoginOrgId());
            }
         } else if (PersistableMetadataHelper.isCreatedByOrgNamePropertyDescriptor(p)) {
            if (ReflectionUtils.invokeMethod(p.getReadMethod(), item) == null) {
               ReflectionUtils.invokeMethod(p.getWriteMethod(), item, LocalContextHelper.getLoginOrgName());
            }
         } else if (PersistableMetadataHelper.isCreatedTimePropertyDescriptor(p) && ReflectionUtils.invokeMethod(p.getReadMethod(), item) == null) {
            ReflectionUtils.invokeMethod(p.getWriteMethod(), item, LocalDateTime.now());
         }
      });
   }

   public static void removeCreatedUserData(Object item) {
      BeanPropertyHelper.getBeanPropertyDescriptorList(item.getClass()).forEach(p -> {
         if (PersistableMetadataHelper.isCreatedByIdPropertyDescriptor(p)) {
            ReflectionUtils.invokeMethod(p.getWriteMethod(), item, new Object[]{null});
         } else if (PersistableMetadataHelper.isCreatedByNamePropertyDescriptor(p)) {
            ReflectionUtils.invokeMethod(p.getWriteMethod(), item, new Object[]{null});
         } else if (PersistableMetadataHelper.isCreatedByOrgIdPropertyDescriptor(p)) {
            ReflectionUtils.invokeMethod(p.getWriteMethod(), item, new Object[]{null});
         } else if (PersistableMetadataHelper.isCreatedByOrgNamePropertyDescriptor(p)) {
            ReflectionUtils.invokeMethod(p.getWriteMethod(), item, new Object[]{null});
         } else if (PersistableMetadataHelper.isCreatedTimePropertyDescriptor(p)) {
            ReflectionUtils.invokeMethod(p.getWriteMethod(), item, new Object[]{null});
         }
      });
   }

   public static void assignActivatedUserData(Object item) {
      if (item != null && item instanceof Activatable) {
         Activatable<?> activatable = (Activatable<?>)item;
         activatable.setActivatedById(LocalContextHelper.getLoginUserId());
         activatable.setActivatedByName(LocalContextHelper.getLoginUserName());
         activatable.setActivatedTime(LocalDateTime.now());
      }
   }

   public static void removeActivatedUserData(Object item) {
      if (item != null && item instanceof Activatable) {
         Activatable<?> activatable = (Activatable<?>)item;
         activatable.setActivatedById(null);
         activatable.setActivatedByName(null);
         activatable.setActivatedTime(null);
      }
   }

   public static void assignSuspendedUserData(Object item) {
      if (item != null && item instanceof Suspendable) {
         Suspendable<?> suspendable = (Suspendable<?>)item;
         suspendable.setLastSuspendedById(LocalContextHelper.getLoginUserId());
         suspendable.setLastSuspendedByName(LocalContextHelper.getLoginUserName());
         suspendable.setLastSuspendedTime(LocalDateTime.now());
      }
   }

   public static void removeSuspendedUserData(Object item) {
      if (item != null && item instanceof Suspendable) {
         Suspendable<?> suspendable = (Suspendable<?>)item;
         suspendable.setLastSuspendedById(null);
         suspendable.setLastSuspendedByName(null);
         suspendable.setLastSuspendedTime(null);
      }
   }
}
