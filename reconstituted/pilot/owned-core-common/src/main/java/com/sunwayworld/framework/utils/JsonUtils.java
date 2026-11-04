package com.sunwayworld.framework.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;
import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.beans.BeanPropertyHelper;
import com.sunwayworld.framework.support.domain.BaseData;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonUtils {
   @Deprecated
   public static final <T> String toJsonString(T t) {
      return JSONObject.toJSONString(t);
   }

   public static final <T> T parse(String json, Class<T> objectClass) {
      if (ClassUtils.isSimpleType(objectClass)) {
         return ConvertUtils.convert(json, objectClass);
      } else {
         T instance = ClassUtils.newInstance(objectClass);
         parse(json, instance);
         return instance;
      }
   }

   public static final <T> T parse(JSONObject jsonObject, Class<T> objectClass) {
      if (ClassUtils.isSimpleType(objectClass)) {
         return jsonObject.toJavaObject(objectClass);
      } else {
         T instance = ClassUtils.newInstance(objectClass);
         parse(jsonObject, instance);
         return instance;
      }
   }

   public static final <T> void parse(JSONObject jsonObject, T item) {
      Class<T> clazz = (Class<T>)ClassUtils.getRawType(item.getClass());
      List<BeanPropertyDescriptor> bpdList = BeanPropertyHelper.getBeanPropertyDescriptorList(clazz);
      jsonObject.forEach(
         (k, v) -> {
            if (v != null) {
               String key = StringUtils.removeStart(k, "ext$.");
               BeanPropertyDescriptor bpd = bpdList.stream().filter(b -> b.getName().equalsIgnoreCase(key)).findFirst().orElse(null);
               if (bpd != null) {
                  if (bpd.isExt$BeanProperty()) {
                     if (BaseData.class.isAssignableFrom(clazz)) {
                        ((Map)v)
                           .forEach((k1, v1) -> ((BaseData)item).setExt$Item(ConvertUtils.convert(k1, String.class), ConvertUtils.convert(v1, String.class)));
                     }
                  } else if (v != null
                     && JSONArray.class.isAssignableFrom(v.getClass())
                     && (List.class.isAssignableFrom(bpd.getPropertyType()) || bpd.getPropertyType().isArray())) {
                     JSONArray ja = (JSONArray)v;
                     List list = new ArrayList();
                     Class<?> genericClazz = null;
                     Type genericType = bpd.getProperty().getGenericType();
                     if (genericType instanceof ParameterizedType) {
                        ParameterizedType pt = (ParameterizedType)genericType;
                        genericClazz = (Class<?>)pt.getActualTypeArguments()[0];
                     } else if (bpd.getPropertyType().isArray()) {
                        genericClazz = bpd.getPropertyType().getComponentType();
                     }

                     if (genericClazz != null) {
                        for (int i = 0; i < ja.size(); i++) {
                           list.add(parse(ja.getString(i), genericClazz));
                        }

                        if (List.class.isAssignableFrom(bpd.getPropertyType())) {
                           bpd.setPropertyValue(item, list);
                        } else {
                           bpd.setPropertyValue(item, list.toArray(ArrayUtils.emptyArray((Class<Object>)genericClazz)));
                        }
                     }
                  } else if (v instanceof JSONObject) {
                     Object value = parse((JSONObject)v, bpd.getPropertyType());
                     bpd.setPropertyValue(item, value);
                  } else if (v instanceof String) {
                     Object value = parse((String)v, bpd.getPropertyType());
                     bpd.setPropertyValue(item, value);
                  } else {
                     bpd.setPropertyValue(item, v);
                  }
               } else if (BaseData.class.isAssignableFrom(clazz)) {
                  ((BaseData)item).setExt$Item(key, ConvertUtils.convert(v, String.class));
               }
            }
         }
      );
   }

   public static final <T> void parse(String json, T item) {
      JSONObject jo = JSON.parseObject(json);
      parse(jo, item);
   }

   public static final <T> T parse(String json, String key, Class<T> objectClass) {
      JSONObject jsonObject = JSONObject.parseObject(json);
      JSONObject jsonKeyObject = (JSONObject)jsonObject.get(key);
      return jsonKeyObject == null ? null : parse(jsonKeyObject.toJSONString(), objectClass);
   }

   public static final <T> List<T> parseList(String json, String key, Class<T> objectClass) {
      JSONObject jsonObject = JSONObject.parseObject(json);
      JSONArray jsonKeyObject = (JSONArray)jsonObject.get(key);
      return jsonKeyObject == null ? CollectionUtils.emptyList() : parseList(jsonKeyObject.toJSONString(), objectClass);
   }

   public static final <T> List<T> parseList(String json, Class<T> objectClass) {
      return StringUtils.isEmpty(json)
         ? CollectionUtils.emptyList()
         : JSONObject.parseArray(json, String.class).stream().map(j -> parse(j, objectClass)).collect(Collectors.toList());
   }

   public static boolean isArray(String json) {
      try (JSONValidator validator = JSONValidator.from(json)) {
         return validator.validate() ? JSON.parse(json) instanceof JSONArray : false;
      } catch (Throwable var16) {
         return false;
      }
   }

   public static boolean isObject(String json) {
      try (JSONValidator validator = JSONValidator.from(json)) {
         return validator.validate() ? JSON.parse(json) instanceof JSONObject : false;
      } catch (Throwable var16) {
         return false;
      }
   }
}
