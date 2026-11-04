package com.sunwayworld.framework.mybatis;

import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class MybatisStrictMap<V> extends HashMap<String, V> {
   private static final long serialVersionUID = -5990269867385375370L;
   private final String name;

   public MybatisStrictMap(String name, int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
      this.name = name;
   }

   public MybatisStrictMap(String name, int initialCapacity) {
      super(initialCapacity);
      this.name = name;
   }

   public MybatisStrictMap(String name) {
      this.name = name;
   }

   public MybatisStrictMap(String name, Map<String, ? extends V> m) {
      super(m);
      this.name = name;
   }

   public V put(String key, V value) {
      if (MybatisHelper.getKeyPrefix() != null) {
         key = MybatisHelper.getKeyPrefix() + "$" + key;
      }

      if (key.contains(".")) {
         String shortKey = this.getShortName(key);
         if (super.get(shortKey) == null) {
            super.put(shortKey, value);
         } else {
            super.put(shortKey, (V)(new MybatisStrictMap.Ambiguity(shortKey)));
         }
      }

      return super.put(key, value);
   }

   @Override
   public V get(Object key) {
      String strKey = key.toString();
      V value = null;
      HttpServletRequest request = ServletUtils.getCurrentRequest();
      if (request != null && request.getHeader("sunway-menu") != null) {
         String menuId = request.getHeader("sunway-menu");
         String resolvedKey = menuId + "$" + strKey;
         if (!StringUtils.contains(strKey, "com.sunwayworld.framework.mybatis.mapper.DaoMapper")
            && !StringUtils.startsWith(MybatisHelper.getKeyPrefix(), "draft$")
            && !StringUtils.startsWith(strKey, "draft$")
            && request.getHeader("sunway-page-config") != null) {
            int lastDotIndex = strKey.lastIndexOf(".");
            if (lastDotIndex > 0) {
               MybatisHelper.setDraftMapper(menuId, strKey.substring(0, lastDotIndex));
            }

            value = super.get("draft$" + resolvedKey);
            if (value != null) {
               return value;
            }
         }

         value = super.get(resolvedKey);
         if (value != null) {
            return value;
         }
      }

      if (MybatisHelper.getKeyPrefix() != null) {
         value = super.get(MybatisHelper.getKeyPrefix() + "$" + strKey);
      }

      if (value != null) {
         return value;
      } else {
         value = super.get(strKey);
         if (value == null) {
            throw new IllegalArgumentException(this.name + " does not contain value for " + strKey);
         } else if (value instanceof MybatisStrictMap.Ambiguity) {
            throw new IllegalArgumentException(
               ((MybatisStrictMap.Ambiguity)value).getSubject()
                  + " is ambiguous in "
                  + this.name
                  + " (try using the full name including the namespace, or rename one of the entries)"
            );
         } else {
            return value;
         }
      }
   }

   private String getShortName(String key) {
      String[] keyParts = key.split("\\.");
      return keyParts[keyParts.length - 1];
   }

   protected static class Ambiguity {
      private final String subject;

      public Ambiguity(String subject) {
         this.subject = subject;
      }

      public String getSubject() {
         return this.subject;
      }
   }
}
