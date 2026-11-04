package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.exception.UnexpectedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class CollectionUtils {
   public static <T> boolean isEmpty(Collection<T> coll) {
      return coll == null || coll.isEmpty();
   }

   public static boolean isEmpty(Map<?, ?> map) {
      return map == null || map.isEmpty();
   }

   public static final <T> List<T> union(Collection<T> first, Collection<T> second) {
      List<T> unionList = new ArrayList<>(first);
      unionList.addAll(second);
      return unionList.stream().distinct().collect(Collectors.toList());
   }

   public static final <T> List<T> intersection(Collection<T> first, Collection<T> second) {
      return first.stream().filter(f -> second.contains(f)).distinct().collect(Collectors.toList());
   }

   public static final <T> List<T> disjunction(Collection<T> first, Collection<T> second) {
      List<T> disjunctionList = subtract(first, second);
      disjunctionList.addAll(subtract(second, first));
      return disjunctionList;
   }

   public static final <T> void merge(Collection<T> first, Collection<T> second) {
      if (!isEmpty(second)) {
         second.stream().filter(s -> !first.contains(s)).forEach(s -> first.add((T)s));
      }
   }

   public static final <T> List<T> subtract(Collection<T> first, Collection<T> second) {
      return first.stream().filter(f -> !second.contains(f)).distinct().collect(Collectors.toList());
   }

   public static final boolean containsIgnoreCase(Collection<String> collection, String value) {
      if (collection.isEmpty()) {
         return false;
      } else {
         for (String v : collection) {
            if (v == null) {
               if (value == null) {
                  return true;
               }
            } else if (v.equalsIgnoreCase(value)) {
               return true;
            }
         }

         return false;
      }
   }

   public static final Object getFirstValue(Map<?, ?> map) {
      return map != null && !map.isEmpty() ? map.values().toArray()[0] : null;
   }

   public static final Object getFirstValue(Collection<?> collection) {
      return collection != null && !collection.isEmpty() ? collection.iterator().next() : null;
   }

   public static final boolean containsIgnoreCase(Map<String, Object> map, String key) {
      return map != null && !map.isEmpty() ? map.keySet().stream().anyMatch(k -> k.equalsIgnoreCase(key)) : false;
   }

   public static final boolean containsKeyIgnorecase(Map<String, ?> map, String key) {
      return map != null && !map.isEmpty() ? map.keySet().stream().anyMatch(k -> k.equalsIgnoreCase(key)) : false;
   }

   public static final Object getValueIgnorecase(Map<String, ?> map, String key) {
      if (map != null && !map.isEmpty()) {
         String actualKey = map.keySet().stream().filter(k -> k.equalsIgnoreCase(key)).findFirst().orElse(null);
         return StringUtils.isEmpty(actualKey) ? null : map.get(actualKey);
      } else {
         return null;
      }
   }

   public static final <S, T> List<T> convert(List<S> itemList, Class<T> type) {
      if (itemList.isEmpty()) {
         return emptyList();
      } else {
         Class<S> sourceType = (Class<S>)itemList.get(0).getClass();
         return type.isAssignableFrom(sourceType)
            ? itemList.stream().map(i -> (T)i).collect(Collectors.toList())
            : itemList.stream().map(s -> ConvertUtils.convert((S)s, type)).collect(Collectors.toList());
      }
   }

   public static final <T> List<T> distinct(List<T> itemList) {
      return itemList.stream().distinct().collect(Collectors.toList());
   }

   public static final void lowerCaseKey(Map<String, Object> map) {
      new HashSet<>(map.keySet()).forEach(k -> {
         String lowerCaseKey = k.toLowerCase();
         if (!k.equals(lowerCaseKey)) {
            Object value = map.get(k);
            map.remove(k);
            map.put(lowerCaseKey, value);
         }
      });
   }

   public static final void upperCaseKey(Map<String, Object> map) {
      new HashSet<>(map.keySet()).forEach(k -> {
         String upperCaseKey = k.toUpperCase();
         if (!k.equals(upperCaseKey)) {
            Object value = map.get(k);
            map.remove(k);
            map.put(upperCaseKey, value);
         }
      });
   }

   public static final <K, V> void putIfNotBlank(Map<K, V> map, K key, V value) {
      if (value != null && !StringUtils.isBlank(value.toString())) {
         map.put(key, value);
      }
   }

   public static final <T> void sort(List<T> list, Comparator<T> comparator) {
      int size = list.size();
      if (size > 1) {
         List<T> sortedList = new ArrayList<>();

         while (sortedList.size() < size) {
            int innerSize = list.size();
            boolean minFound = true;

            for (int i = 0; i < innerSize; i++) {
               T item = list.get(i);
               minFound = true;

               for (int j = 0; j < innerSize; j++) {
                  if (i != j) {
                     T another = list.get(j);
                     if (comparator.compare(item, another) > 0) {
                        minFound = false;
                        break;
                     }
                  }
               }

               if (minFound) {
                  sortedList.add(item);
                  list.remove(i);
                  break;
               }
            }

            if (!minFound) {
               throw new UnexpectedException("GIKAM.EXCEPTION.ORDER_INFINITE_LOOP");
            }
         }

         list.addAll(sortedList);
      }
   }

   public static final <T> List<T> emptyList() {
      return new ArrayList<>();
   }

   public static final <K, V> Map<K, V> emptyMap() {
      return new HashMap<>();
   }
}
