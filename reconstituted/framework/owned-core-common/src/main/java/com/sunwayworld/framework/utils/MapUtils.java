package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapUtils {
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

   public static <V> V getObject(final Map<String, V> map, final String key) {
      if (map != null) {
         String finalKey = map.keySet().stream().filter(mapKey -> StringUtils.equalsIgnoreCase(mapKey, key)).findFirst().orElse(key);
         return map.get(finalKey);
      } else {
         return null;
      }
   }

   public static String getString(final Map<String, ?> map, final String key) {
      if (map != null) {
         Object answer = getObject(map, key);
         if (answer != null) {
            return answer.toString();
         }
      }

      return null;
   }

   public static String getString(final Map<String, ?> map, final String key, final String defaultValue) {
      String answer = getString(map, key);
      if (answer == null) {
         answer = defaultValue;
      }

      return answer;
   }

   public static Boolean getBoolean(final Map<String, ?> map, final String key) {
      if (map != null) {
         Object answer = getObject(map, key);
         if (answer != null) {
            if (answer instanceof Boolean) {
               return (Boolean)answer;
            }

            if (answer instanceof String) {
               return Boolean.valueOf((String)answer);
            }

            if (answer instanceof Number) {
               Number n = (Number)answer;
               return n.intValue() != 0 ? Boolean.TRUE : Boolean.FALSE;
            }
         }
      }

      return null;
   }

   public static Boolean getBoolean(final Map<String, ?> map, final String key, final Boolean defaultValue) {
      Boolean answer = getBoolean(map, key);
      if (answer == null) {
         answer = defaultValue;
      }

      return answer;
   }

   public static boolean getBooleanValue(final Map<String, ?> map, final String key) {
      return Boolean.TRUE.equals(getBoolean(map, key));
   }

   public static boolean getBooleanValue(final Map<String, ?> map, final String key, final boolean defaultValue) {
      Boolean booleanObject = getBoolean(map, key);
      return booleanObject == null ? defaultValue : booleanObject;
   }

   public static Number getNumber(final Map<String, ?> map, final String key) {
      if (map != null) {
         Object answer = getObject(map, key);
         if (answer != null) {
            if (answer instanceof Number) {
               return (Number)answer;
            }

            if (answer instanceof String) {
               try {
                  String text = (String)answer;
                  return NumberFormat.getInstance().parse(text);
               } catch (ParseException var4) {
               }
            }
         }
      }

      return null;
   }

   public static Long getLong(final Map<String, ?> map, final String key) {
      Number answer = getNumber(map, key);
      if (answer == null) {
         return null;
      } else {
         return answer instanceof Long ? (Long)answer : answer.longValue();
      }
   }

   public static Long getLong(final Map<String, ?> map, final String key, final Long defaultValue) {
      Long answer = getLong(map, key);
      if (answer == null) {
         answer = defaultValue;
      }

      return answer;
   }

   public static long getLongValue(final Map<String, ?> map, final String key) {
      Long longObject = getLong(map, key);
      return longObject == null ? 0L : longObject;
   }

   public static long getLongValue(final Map<String, ?> map, final String key, final long defaultValue) {
      Long longObject = getLong(map, key);
      return longObject == null ? defaultValue : longObject;
   }

   public static Integer getInteger(final Map<String, ?> map, final String key) {
      Number answer = getNumber(map, key);
      if (answer == null) {
         return null;
      } else {
         return answer instanceof Integer ? (Integer)answer : answer.intValue();
      }
   }

   public static Integer getInteger(final Map<String, ?> map, final String key, final Integer defaultValue) {
      Integer answer = getInteger(map, key);
      if (answer == null) {
         answer = defaultValue;
      }

      return answer;
   }

   public static int getIntValue(final Map<String, ?> map, final String key) {
      Integer integerObject = getInteger(map, key);
      return integerObject == null ? 0 : integerObject;
   }

   public static int getIntValue(final Map<String, ?> map, final String key, final int defaultValue) {
      Integer integerObject = getInteger(map, key);
      return integerObject == null ? defaultValue : integerObject;
   }

   public static Double getDouble(final Map<String, ?> map, final String key) {
      Number answer = getNumber(map, key);
      if (answer == null) {
         return null;
      } else {
         return answer instanceof Double ? (Double)answer : answer.doubleValue();
      }
   }

   public static Double getDouble(final Map<String, ?> map, final String key, final Double defaultValue) {
      Double answer = getDouble(map, key);
      if (answer == null) {
         answer = defaultValue;
      }

      return answer;
   }

   public static double getDoubleValue(final Map<String, ?> map, final String key) {
      Double doubleObject = getDouble(map, key);
      return doubleObject == null ? 0.0 : doubleObject;
   }

   public static double getDoubleValue(final Map<String, ?> map, final String key, final double defaultValue) {
      Double doubleObject = getDouble(map, key);
      return doubleObject == null ? defaultValue : doubleObject;
   }

   public static LocalDate getLocalDate(final Map<String, ?> map, final String key) {
      if (map != null) {
         Object answer = getObject(map, key);
         if (answer != null) {
            if (answer instanceof LocalDate) {
               return (LocalDate)answer;
            }

            if (answer instanceof LocalDateTime) {
               return ((LocalDateTime)answer).toLocalDate();
            }

            if (answer instanceof Timestamp) {
               return ((Timestamp)answer).toLocalDateTime().toLocalDate();
            }

            if (answer instanceof Date) {
               return ((Date)answer).toLocalDate();
            }

            if (answer instanceof String) {
               return DateTimeUtils.parseLocalDate((String)answer);
            }
         }
      }

      return null;
   }

   public static LocalDateTime getLocalDateTime(final Map<String, ?> map, final String key) {
      if (map != null) {
         Object answer = getObject(map, key);
         if (answer != null) {
            if (answer instanceof LocalDateTime) {
               return (LocalDateTime)answer;
            }

            if (answer instanceof String) {
               return DateTimeUtils.parseLocalDateTime((String)answer);
            }
         }
      }

      return null;
   }

   public static <V> void copy(Map<String, V> source, Map<String, ? super V> target, String... ignoreKeys) {
      if (source != null && target != null) {
         if (ignoreKeys != null && ignoreKeys.length != 0) {
            source.forEach((key, value) -> {
               if (!Arrays.stream(ignoreKeys).anyMatch(mapKey -> StringUtils.equalsIgnoreCase(mapKey, key))) {
                  target.put(key, value);
               }
            });
         } else {
            target.putAll(source);
         }
      }
   }

   public static boolean isEmptyKey(Map<String, Object> map, String key) {
      String mapKey = map.keySet().stream().filter(f -> StringUtils.equalsIgnoreCase(f, key)).findFirst().orElse(null);
      return mapKey == null ? true : map.get(mapKey) == null;
   }

   public static void assignCreatedElementIfNecessary(Map<String, Object> map) {
      if (map != null) {
         if (isEmptyKey(map, "createdbyid")) {
            map.put("createdbyid", LocalContextHelper.getLoginUserId());
         }

         if (isEmptyKey(map, "createdbyname")) {
            map.put("createdbyname", LocalContextHelper.getLoginUserName());
         }

         if (isEmptyKey(map, "createdtime")) {
            map.put("createdtime", LocalDateTime.now());
         }

         if (isEmptyKey(map, "createdbyorgid")) {
            map.put("createdbyorgid", LocalContextHelper.getLoginOrgId());
         }

         if (isEmptyKey(map, "createdbyorgname")) {
            map.put("createdbyorgname", LocalContextHelper.getLoginOrgName());
         }
      }
   }

   public static void assignCreatedElementIfNecessary(List<Map<String, Object>> mapList) {
      if (mapList != null) {
         mapList.forEach(map -> assignCreatedElementIfNecessary((Map<String, Object>)map));
      }
   }

   public static void assignCreatedElement(Map<String, Object> map) {
      if (map != null) {
         map.put("createdbyid", LocalContextHelper.getLoginUserId());
         map.put("createdbyname", LocalContextHelper.getLoginUserName());
         map.put("createdtime", LocalDateTime.now());
         map.put("createdbyorgid", LocalContextHelper.getLoginOrgId());
         map.put("createdbyorgname", LocalContextHelper.getLoginOrgName());
      }
   }

   public static void assignCreatedElement(List<Map<String, Object>> mapList) {
      if (mapList != null) {
         mapList.forEach(map -> assignCreatedElement((Map<String, Object>)map));
      }
   }

   public static void resetBasicProperties(Map<String, Object> map) {
      if (map != null) {
         map.forEach((key, value) -> {
            if (ArrayUtils.containsIgnoreCase(BEAN_BASIC_PROPERTIES, key)) {
               map.put(key, null);
            }
         });
      }
   }

   public static void resetBasicProperties(List<Map<String, Object>> mapList) {
      if (mapList != null) {
         mapList.forEach(map -> resetBasicProperties((Map<String, Object>)map));
      }
   }

   public static Map<String, Object> lowerCaseKey(Map<String, Object> map) {
      if (map == null) {
         return null;
      } else {
         Map<String, Object> returnMap = new HashMap<>();
         map.forEach((key, value) -> {
            if (value instanceof Timestamp) {
               returnMap.put(key.toLowerCase(), value.toString());
            } else {
               returnMap.put(key.toLowerCase(), value);
            }
         });
         map.clear();
         map.putAll(returnMap);
         return returnMap;
      }
   }

   public static List<Map<String, Object>> lowerCaseKey(List<Map<String, Object>> list) {
      if (list == null) {
         return null;
      } else {
         List<Map<String, Object>> returnList = new ArrayList<>();

         for (Map<String, Object> map : list) {
            Map<String, Object> returnMap = lowerCaseKey(map);
            returnList.add(returnMap);
         }

         return returnList;
      }
   }

   public static Map<String, Object> toMap(Object bean) {
      if (bean == null) {
         return new HashMap<>();
      } else if (Map.class.isAssignableFrom(bean.getClass())) {
         return (Map<String, Object>)bean;
      } else {
         Map<String, Object> map = BeanUtils.toMap(bean);
         Method getExt$Method = ReflectionUtils.getUniqueMethodByName(bean.getClass(), "getExt$");
         if (getExt$Method != null) {
            Map<String, String> ext$Map = (Map<String, String>)ReflectionUtils.invokeMethod(getExt$Method, bean);
            ext$Map.forEach((k, v) -> map.put(k, v));
         }

         Method getChildrenMethod = ReflectionUtils.getUniqueMethodByName(bean.getClass(), "getChildren");
         if (getChildrenMethod != null) {
            List<Object> childrenList = (List<Object>)ReflectionUtils.invokeMethod(getChildrenMethod, bean);
            if (childrenList != null && childrenList.size() > 0) {
               List<Map<String, Object>> childrenMapList = toMapList(childrenList);
               map.put("children", childrenMapList);
            }
         }

         return map;
      }
   }

   public static List<Map<String, Object>> toMapList(List<?> beanList) {
      List<Map<String, Object>> mapList = new ArrayList<>();
      if (beanList == null) {
         return mapList;
      } else {
         for (Object bean : beanList) {
            mapList.add(toMap(bean));
         }

         return mapList;
      }
   }

   public static Page<Map<String, Object>> toMapPage(Page<?> beanPage) {
      if (beanPage == null) {
         return new Pagination<>();
      } else {
         List<Map<String, Object>> mapList = new ArrayList<>();
         List<?> dataList = beanPage.getRows();
         if (!dataList.isEmpty()) {
            for (Object bean : dataList) {
               mapList.add(toMap(bean));
            }
         }

         return new Pagination<>(beanPage.getTotal(), beanPage.getPageSize(), beanPage.getPageNumber(), mapList);
      }
   }
}
