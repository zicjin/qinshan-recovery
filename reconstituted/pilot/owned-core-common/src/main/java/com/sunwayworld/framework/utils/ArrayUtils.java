package com.sunwayworld.framework.utils;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.framework.exception.EmptyArrayException;
import com.sunwayworld.framework.exception.OutOfRangeException;
import com.sunwayworld.framework.exception.TypeMismatchException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ArrayUtils {
   public static final Class<?> getArrayType(Class<?> arrayClass) {
      if (!arrayClass.isArray()) {
         throw new TypeMismatchException("array", arrayClass);
      } else {
         Class<?> componentType = arrayClass.getComponentType();

         while (componentType.isArray()) {
            componentType = componentType.getComponentType();
         }

         return componentType;
      }
   }

   public static final boolean hasElement(Object array) {
      return array != null && array.getClass().isArray() && Array.getLength(array) > 0;
   }

   public static final <T> boolean contains(T[] array, T value) {
      return array != null && array.length > 0 && Arrays.<Object>stream(array).anyMatch(t -> value == null && t == null || value != null && value.equals(t));
   }

   public static final boolean containsIgnoreCase(String[] array, String value) {
      return array != null && array.length > 0 && Arrays.stream(array).anyMatch(t -> value == null && t == null || value != null && value.equalsIgnoreCase(t));
   }

   public static final <T> T[] concat(T[] first, T[] second) {
      if (first == null) {
         return second;
      } else if (second == null) {
         return first;
      } else {
         T[] array = (T[])Array.newInstance(first.getClass().getComponentType(), first.length + second.length);
         System.arraycopy(first, 0, array, 0, first.length);
         System.arraycopy(second, 0, array, first.length, second.length);
         return array;
      }
   }

   public static final <T> T[] append(T[] array, T target) {
      if (target == null) {
         return array;
      } else {
         int length = array == null ? 0 : array.length;
         T[] newArray = (T[])Array.newInstance(target.getClass(), length + 1);
         if (length > 0) {
            System.arraycopy(array, 0, newArray, 0, length);
         }

         newArray[length] = target;
         return newArray;
      }
   }

   public static <T> String arrayToDelimitedString(T[] array, String delim) {
      if (!hasElement(array)) {
         return "";
      } else if (array.length == 1) {
         return ConvertUtils.convert(array[0], String.class, "");
      } else {
         StringBuilder sb = new StringBuilder();
         Arrays.<Object>stream(array).forEach(v -> sb.append(ConvertUtils.convert(v, String.class, "")).append(delim));
         return sb.substring(0, sb.length() - delim.length());
      }
   }

   public static <T> T[] requireNotEmpty(T[] array) {
      if (!hasElement(array)) {
         throw new EmptyArrayException();
      } else {
         return array;
      }
   }

   public static <T> T[] emptyArray(Class<T> componentType) {
      return newInstance(componentType, 0);
   }

   public static <T> T[] newInstance(Class<T> componentType, int size) {
      return (T[])((Object[])Array.newInstance(componentType, size));
   }

   public static <T> T[] instanceToArray(T instance) {
      T[] array = (T[])Array.newInstance(instance.getClass(), 1);
      array[0] = instance;
      return array;
   }

   public static <A, T> T getUniqueTypeValue(A[] array, Class<T> type) {
      if (array != null && array.length != 0) {
         List<T> list = Arrays.stream(array).filter(a -> a != null && a.getClass().equals(type)).map(a -> (T)a).collect(Collectors.toList());
         if (list.size() > 1) {
            throw new OutOfRangeException(JSON.toJSONString(array) + " contains more than one instance of type " + type + ".");
         } else {
            return list.isEmpty() ? null : list.get(0);
         }
      } else {
         return null;
      }
   }

   public static <T> Class<? extends T> getUniqueAssignableType(Class<?>[] array, Class<T> type) {
      if (array != null && array.length != 0) {
         List<Class<?>> list = Arrays.stream(array).filter(a -> a != null && type.isAssignableFrom((Class<?>)a)).collect(Collectors.toList());
         if (list.size() > 1) {
            throw new OutOfRangeException(JSON.toJSONString(array) + " contains more than one class assignable type " + type + ".");
         } else {
            return list.isEmpty() ? null : (Class)list.get(0);
         }
      } else {
         return null;
      }
   }

   public static <T> boolean isEquals(T[] left, T[] right) {
      if (left == null && right == null) {
         return true;
      } else if ((left != null || right == null) && (left == null || right != null)) {
         if (left.length != right.length) {
            return false;
         } else {
            int i = 0;

            for (int j = left.length; i < j; i++) {
               T leftValue = left[i];
               T rightValue = right[i];
               if (leftValue == null) {
                  if (rightValue != null) {
                     return false;
                  }
               } else if (!leftValue.equals(rightValue)) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   @SafeVarargs
   public static <T> List<T> asList(T... items) {
      if (items == null) {
         return CollectionUtils.emptyList();
      } else {
         List<T> itemList = new ArrayList<>();

         for (T item : items) {
            itemList.add(item);
         }

         return itemList;
      }
   }

   public static <T> boolean isEmpty(T... items) {
      return items == null || items.length == 0;
   }

   public static <T> T[] subarray(T[] array, int start, int length) {
      if (array != null && array.length != 0) {
         Class<T> type = (Class<T>)array[0].getClass();
         if (array.length < start + length) {
            return emptyArray(type);
         } else {
            T[] subarray = newInstance(type, length);
            System.arraycopy(array, start, subarray, 0, length);
            return subarray;
         }
      } else {
         return array;
      }
   }

   public static int[] subarray(int[] array, int start, int length) {
      if (array != null && array.length != 0) {
         if (array.length < start + length) {
            return new int[0];
         } else {
            int[] subarray = new int[length];
            System.arraycopy(array, start, subarray, 0, length);
            return subarray;
         }
      } else {
         return array;
      }
   }

   public static long[] subarray(long[] array, int start, int length) {
      if (array != null && array.length != 0) {
         if (array.length < start + length) {
            return new long[0];
         } else {
            long[] subarray = new long[length];
            System.arraycopy(array, start, subarray, 0, length);
            return subarray;
         }
      } else {
         return array;
      }
   }

   public static double[] subarray(double[] array, int start, int length) {
      if (array != null && array.length != 0) {
         if (array.length < start + length) {
            return new double[0];
         } else {
            double[] subarray = new double[length];
            System.arraycopy(array, start, subarray, 0, length);
            return subarray;
         }
      } else {
         return array;
      }
   }

   public static byte[] subarray(byte[] array, int start, int length) {
      if (array != null && array.length != 0) {
         if (array.length < start + length) {
            return new byte[0];
         } else {
            byte[] subarray = new byte[length];
            System.arraycopy(array, start, subarray, 0, length);
            return subarray;
         }
      } else {
         return array;
      }
   }

   public static final int[] concat(int[] first, int[] second) {
      if (first == null) {
         return second;
      } else if (second == null) {
         return first;
      } else {
         int[] array = new int[first.length + second.length];
         System.arraycopy(first, 0, array, 0, first.length);
         System.arraycopy(second, 0, array, first.length, second.length);
         return array;
      }
   }

   public static final long[] concat(long[] first, long[] second) {
      if (first == null) {
         return second;
      } else if (second == null) {
         return first;
      } else {
         long[] array = new long[first.length + second.length];
         System.arraycopy(first, 0, array, 0, first.length);
         System.arraycopy(second, 0, array, first.length, second.length);
         return array;
      }
   }

   public static final double[] concat(double[] first, double[] second) {
      if (first == null) {
         return second;
      } else if (second == null) {
         return first;
      } else {
         double[] array = new double[first.length + second.length];
         System.arraycopy(first, 0, array, 0, first.length);
         System.arraycopy(second, 0, array, first.length, second.length);
         return array;
      }
   }

   public static final byte[] concat(byte[] first, byte[] second) {
      if (first == null) {
         return second;
      } else if (second == null) {
         return first;
      } else {
         byte[] array = new byte[first.length + second.length];
         System.arraycopy(first, 0, array, 0, first.length);
         System.arraycopy(second, 0, array, first.length, second.length);
         return array;
      }
   }
}
