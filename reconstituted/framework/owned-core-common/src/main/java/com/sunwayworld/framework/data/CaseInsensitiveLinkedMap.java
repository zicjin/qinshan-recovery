package com.sunwayworld.framework.data;

import com.sunwayworld.framework.utils.ObjectUtils;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CaseInsensitiveLinkedMap<V> extends LinkedHashMap<String, V> {
   private static final long serialVersionUID = 9078487774509185313L;

   public CaseInsensitiveLinkedMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
   }

   public CaseInsensitiveLinkedMap(int initialCapacity) {
      super(initialCapacity);
   }

   public CaseInsensitiveLinkedMap() {
   }

   public CaseInsensitiveLinkedMap(Map<? extends String, ? extends V> m) {
      super((Map<String, V>)(Map<?, ?>)m);
   }

   public CaseInsensitiveLinkedMap(int initialCapacity, float loadFactor, boolean accessOrder) {
      super(initialCapacity, loadFactor);
   }

   @Override
   public V get(Object key) {
      return super.get(getKey(key));
   }

   @Override
   public V getOrDefault(Object key, V defaultValue) {
      return super.getOrDefault(getKey(key), defaultValue);
   }

   @Override
   public boolean containsKey(Object key) {
      return super.containsKey(getKey(key));
   }

   public V put(String key, V value) {
      return super.put(getKey(key), value);
   }

   @Override
   public void putAll(Map<? extends String, ? extends V> m) {
      super.putAll((Map<String, V>)(Map<?, ?>)m);
   }

   @Override
   public V remove(Object key) {
      return super.remove(getKey(key));
   }

   public V putIfAbsent(String key, V value) {
      return super.putIfAbsent(getKey(key), value);
   }

   @Override
   public boolean remove(Object key, Object value) {
      return super.remove(getKey(key), value);
   }

   public boolean replace(String key, V oldValue, V newValue) {
      return super.replace(getKey(key), oldValue, newValue);
   }

   public V replace(String key, V value) {
      return super.replace(getKey(key), value);
   }

   public V computeIfAbsent(String key, Function<? super String, ? extends V> mappingFunction) {
      return super.computeIfAbsent(getKey(key), mappingFunction);
   }

   public V computeIfPresent(String key, BiFunction<? super String, ? super V, ? extends V> remappingFunction) {
      return super.computeIfPresent(getKey(key), remappingFunction);
   }

   public V compute(String key, BiFunction<? super String, ? super V, ? extends V> remappingFunction) {
      return super.compute(getKey(key), remappingFunction);
   }

   public V merge(String key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
      return super.merge(getKey(key), value, remappingFunction);
   }

   private static String getKey(Object key) {
      if (key == null) {
         return null;
      } else {
         return String.class.equals(key.getClass()) ? ((String)key).toLowerCase() : key.toString();
      }
   }

   private static <V> Map<String, V> getMap(Map<String, V> map) {
      Map<String, V> clone = ObjectUtils.clone(map);
      new HashSet<>(clone.keySet()).forEach(k -> {
         String key = getKey(k);
         if (!k.equals(key)) {
            V value = map.get(k);
            clone.remove(k);
            clone.put(key, value);
         }
      });
      return clone;
   }
}
