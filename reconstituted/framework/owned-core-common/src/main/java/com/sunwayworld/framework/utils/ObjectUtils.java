package com.sunwayworld.framework.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.sunwayworld.framework.exception.CloneException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

public abstract class ObjectUtils {
   private static ValueFilter valueFilter = new ValueFilter() {
      @Override
      public Object process(Object object, String name, Object value) {
         if (value != null) {
            Class<?> clazz = value.getClass();
            if (byte[].class.isAssignableFrom(clazz)) {
               return "-bytes-";
            }

            if (Byte[].class.isAssignableFrom(clazz)) {
               return "-Bytes-";
            }

            if (InputStream.class.isAssignableFrom(clazz)) {
               return "-InputStream-";
            }

            if (OutputStream.class.isAssignableFrom(clazz)) {
               return "-OutputStream-";
            }

            if (CharSequence.class.isAssignableFrom(clazz)) {
               CharSequence cs = (CharSequence)value;
               if (cs.length() > 124) {
                  return cs.subSequence(0, 123) + "...(more)";
               }
            }
         }

         return value;
      }
   };

   public static final boolean equals(Object v1, Object v2) {
      if (v1 != null || v2 != null && !"".equals(v2)) {
         if (v2 == null && "".equals(v1)) {
            return true;
         } else if (v1 != null && v2 != null) {
            Class<?> clazz1 = v1.getClass();
            Class<?> clazz2 = v2.getClass();
            if (!clazz1.equals(clazz2)) {
               return false;
            } else if (Collection.class.isAssignableFrom(clazz1)) {
               long qty = ((Collection)v1).stream().filter(c1 -> !((Collection)v2).contains(c1)).count();
               if (qty > 0L) {
                  return false;
               } else {
                  qty = ((Collection)v2).stream().filter(c2 -> !((Collection)v1).contains(c2)).count();
                  return qty <= 0L;
               }
            } else if (Map.class.isAssignableFrom(clazz1)) {
               long qty = ((Map<?, ?>)v1)
                  .entrySet()
                  .stream()
                  .filter(
                     m1 -> ((Map<?, ?>)v2).entrySet().stream().filter(m2 -> m2.getKey().equals(m1.getKey()) && equals(m2.getValue(), m1.getValue())).count() == 0L
                  )
                  .count();
               if (qty > 0L) {
                  return false;
               } else {
                  qty = ((Map<?, ?>)v2)
                     .entrySet()
                     .stream()
                     .filter(
                        m2 -> ((Map<?, ?>)v1).entrySet().stream().filter(m1 -> m1.getKey().equals(m2.getKey()) && equals(m1.getValue(), m2.getValue())).count() == 0L
                     )
                     .count();
                  return qty <= 0L;
               }
            } else {
               return v1.equals(v2);
            }
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public static final String toShortString(Object target) {
      if (target == null) {
         return "";
      } else {
         Class<?> clazz = target.getClass();
         if (!clazz.isArray()) {
            if (String.class.isAssignableFrom(clazz)) {
               return (String)target;
            } else if (Number.class.isAssignableFrom(clazz)) {
               return NumberUtils.formatNumber((Number)target);
            } else if (Throwable.class.isAssignableFrom(clazz)) {
               return getStackTrace((Throwable)target);
            } else if (cannotDisplayString(clazz)) {
               return target.toString();
            } else {
               try {
                  return JSONObject.toJSONString(target, valueFilter, new SerializerFeature[0]);
               } catch (Exception var5) {
                  return target.toString();
               }
            }
         } else {
            int length = Array.getLength(target);
            String[] array = new String[length];

            for (int i = 0; i < length; i++) {
               array[i] = toString(Array.get(target, i));
            }

            return Arrays.toString((Object[])array);
         }
      }
   }

   public static final boolean isEmpty(Object object) {
      if (object == null) {
         return true;
      } else if ("".equals(object)) {
         return true;
      } else {
         Class<?> clazz = object.getClass();
         if (clazz.isArray()) {
            return Array.getLength(object) == 0;
         } else if (Collection.class.isAssignableFrom(clazz)) {
            return ((Collection)object).size() == 0;
         } else {
            return Map.class.isAssignableFrom(clazz) ? ((Map)object).size() == 0 : false;
         }
      }
   }

   public static final <T> T clone(final T target) {
      if (target == null) {
         return null;
      } else {
         try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(target);
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (T)ois.readObject();
         } catch (ClassNotFoundException | IOException var8) {
            Class<T> clazz = (Class<T>)ClassUtils.getRawType(target.getClass());
            if (clazz.isArray()) {
               int length = Array.getLength(target);
               Class<?> arrayType = ClassUtils.getRawType(ArrayUtils.getArrayType(clazz));
               T clone = (T)Array.newInstance(arrayType, length);

               for (int i = 0; i < length; i++) {
                  Object cloneInstance = ClassUtils.newInstance(arrayType);
                  BeanUtils.copyProperties(Array.get(target, i), cloneInstance);
                  Array.set(clone, i, cloneInstance);
               }

               return clone;
            } else if (List.class.isAssignableFrom(clazz)) {
               List list = (List)target;
               List cloneList = new ArrayList();
               list.forEach(t -> {
                  Object cloneInstancex = ClassUtils.newInstance(ClassUtils.getRawType(t.getClass()));
                  BeanUtils.copyProperties(t, cloneInstancex);
                  cloneList.add(cloneInstancex);
               });
               return (T)cloneList;
            } else if (StringUtils.startsWith(clazz.getName(), "com.sunwayworld.")) {
               T clone = ClassUtils.newInstance(clazz);
               BeanUtils.copyProperties(target, clone);
               return clone;
            } else {
               throw new CloneException(target, var8);
            }
         }
      }
   }

   public static final <T> String toString(final T target) {
      return target == null ? "" : ConvertUtils.convert(target, String.class, "");
   }

   public static <T> T defaultIfNull(T target, T defaultValue) {
      return target == null ? defaultValue : target;
   }

   public static final String getStackTrace(final Throwable throwable) {
      StringWriter stringWriter = new StringWriter();
      PrintWriter printWriter = new PrintWriter(stringWriter);
      throwable.printStackTrace(printWriter);
      return stringWriter.toString();
   }

   private static final boolean cannotDisplayString(final Class<?> sourceClass) {
      return ServletRequest.class.isAssignableFrom(sourceClass)
         || ServletResponse.class.isAssignableFrom(sourceClass)
         || HttpSession.class.isAssignableFrom(sourceClass)
         || AutoCloseable.class.isAssignableFrom(sourceClass)
         || Closeable.class.isAssignableFrom(sourceClass)
         || Blob.class.isAssignableFrom(sourceClass)
         || Clob.class.isAssignableFrom(sourceClass)
         || byte[].class.isAssignableFrom(sourceClass)
         || Byte[].class.isAssignableFrom(sourceClass)
         || Charset.class.isAssignableFrom(sourceClass);
   }
}
