package com.sunwayworld.cloud.lims.reporttemplate.support.util;

import com.sunwayworld.framework.utils.BeanUtils;
import java.util.Map;

public class ReflectUtils {
   public static <T, V> V getPropertyValue(T item, String propertyName) {
      if (item == null) {
         return null;
      } else {
         return (V)(item instanceof Map ? ((Map)item).get(propertyName) : BeanUtils.getPropertyValue(item, propertyName));
      }
   }
}
