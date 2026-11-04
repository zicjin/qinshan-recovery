package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.cache.memory.MemoryCacheManager;
import com.sunwayworld.framework.data.TypeValuePair;
import com.sunwayworld.framework.exception.ClassInstantiationException;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.exception.ZipException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

public class ClassUtils {
   private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<>(8);
   private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap<>(8);

   public static final <T> T newInstance(final Class<T> clazz) {
      Constructor<?>[] constructors = clazz.getDeclaredConstructors();
      Constructor<?> constructor = Arrays.stream(constructors).min((c1, c2) -> c1.getParameterCount() - c2.getParameterCount()).get();
      Class<?>[] parameterTypes = constructor.getParameterTypes();
      Object[] parameterDefaultValues = new Object[parameterTypes.length];

      for (int i = 0; i < parameterTypes.length; i++) {
         Class<?> parameterType = parameterTypes[i];
         parameterDefaultValues[i] = isPrimitiveType(parameterType) ? getPrimitiveDefaultValue(parameterType) : null;
      }

      try {
         return (T)constructor.newInstance(parameterDefaultValues);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException var7) {
         throw new ClassInstantiationException(clazz, var7);
      }
   }

   @SafeVarargs
   public static final <T> T newInstance(final Class<T> clazz, final TypeValuePair<? extends Object>... typeValuePairs) {
      if (typeValuePairs != null && typeValuePairs.length != 0) {
         Class<?>[] parameterTypes = new Class[typeValuePairs.length];
         Object[] parameterValues = new Object[typeValuePairs.length];

         for (int i = 0; i < typeValuePairs.length; i++) {
            parameterTypes[i] = typeValuePairs[i].getFirst();
            parameterValues[i] = typeValuePairs[i].getSecond();
         }

         try {
            Constructor<T> constructor = clazz.getConstructor(parameterTypes);
            return constructor.newInstance(parameterValues);
         } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException var5) {
            throw new ClassInstantiationException(clazz, var5);
         }
      } else {
         try {
            return clazz.newInstance();
         } catch (IllegalAccessException | InstantiationException var6) {
            throw new ClassInstantiationException(clazz, var6);
         }
      }
   }

   public static final <T> Constructor<T> getConstructorIfAvailable(final Class<T> clazz, final Class<?>... parameterTypes) {
      try {
         return clazz.getConstructor(parameterTypes);
      } catch (NoSuchMethodException var3) {
         return null;
      }
   }

   public static final Class<?> getRawType(Class<?> clazz) {
      if (clazz.getName().startsWith("com.sun.proxy.$Proxy")) {
         return clazz.getInterfaces()[0];
      } else {
         while (clazz.getName().contains("CGLIB$")) {
            clazz = clazz.getSuperclass();
         }

         return clazz;
      }
   }

   public static final boolean isCglibProxyClass(Class<?> clazz) {
      return clazz != null && clazz.getName().contains("CGLIB$");
   }

   public static final boolean isPrimitiveType(Class<?> clazz) {
      return primitiveTypeToWrapperMap.containsKey(clazz);
   }

   public static final boolean isWrapperType(Class<?> clazz) {
      return primitiveWrapperTypeMap.containsKey(clazz);
   }

   public static final Class<?> getWrapperType(Class<?> primitiveType) {
      return primitiveTypeToWrapperMap.get(primitiveType);
   }

   public static final Class<?> getPrimitiveType(Class<?> wrapperType) {
      return primitiveWrapperTypeMap.get(wrapperType);
   }

   public static final Object getPrimitiveDefaultValue(Class<?> primitiveType) {
      if (boolean.class == primitiveType) {
         return false;
      } else if (byte.class == primitiveType) {
         return '0';
      } else if (char.class == primitiveType) {
         return '0';
      } else if (short.class == primitiveType) {
         return Short.parseShort("0");
      } else if (int.class == primitiveType) {
         return 0;
      } else if (float.class == primitiveType) {
         return 0.0F;
      } else if (double.class == primitiveType) {
         return 0.0;
      } else {
         return long.class == primitiveType ? 0L : null;
      }
   }

   public static Class<?> resolvePrimitiveIfNecessary(Class<?> clazz) {
      return clazz.isPrimitive() && clazz != void.class ? getWrapperType(clazz) : clazz;
   }

   public static final boolean isSimpleType(Class<?> type) {
      return isPrimitiveType(type)
         || isWrapperType(type)
         || CharSequence.class.isAssignableFrom(type)
         || Number.class.isAssignableFrom(type)
         || Date.class.isAssignableFrom(type)
         || Map.class.isAssignableFrom(type)
         || Collection.class.isAssignableFrom(type)
         || Temporal.class.isAssignableFrom(type)
         || Object.class == type;
   }

   public static final boolean isGikamType(Class<?> type) {
      return type.getName().startsWith("com.sunwayworld");
   }

   public static final <T> Class<T> getClass(String className) {
      ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
      ClassLoader loader = contextCL == null ? ClassLoader.getSystemClassLoader() : contextCL;

      try {
         String abbreviation = getClassAbbreviation(className);
         return (Class<T>)(className.equals(abbreviation)
            ? Class.forName(getFullyQualifiedName(className), false, loader)
            : Class.forName("[" + abbreviation, false, loader).getComponentType());
      } catch (ClassNotFoundException var4) {
         throw new UnexpectedException(var4);
      }
   }

   public static final <T> Class<T> getGikamClass(String simpleName) {
      return (Class<T>)getAllClasses("com.sunwayworld").stream().filter(c -> c.getSimpleName().equalsIgnoreCase(simpleName)).findFirst().orElse(null);
   }

   public static final List<Class<?>> getPredicatedClasses(String packageName, Predicate<Class<?>> classTester) {
      String packageDirName = StringUtils.removeStart(StringUtils.removeEnd(StringUtils.replace(packageName, ".", "/"), "/"), "/");
      ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
      MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

      try {
         Resource[] resources = resourcePatternResolver.getResources("classpath*:" + packageDirName + "/**/*.class");
         List<Class<?>> list = new ArrayList<>();

         for (Resource r : resources) {
            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(r);
            Class<?> clazz = getClass(metadataReader.getClassMetadata().getClassName());
            if (classTester.test(clazz)) {
               list.add(clazz);
            }
         }

         return list;
      } catch (IOException var13) {
         throw new UnexpectedException(var13);
      }
   }

   public static final boolean hasAnyMatchClass(String packageName, Predicate<Class<?>> classTester) {
      for (Class<?> clazz : getAllClasses(packageName)) {
         if (classTester.test(clazz)) {
            return true;
         }
      }

      return false;
   }

   public static final List<Class<?>> getAllInterfaces(Class<?> clazz) {
      List<Class<?>> interfaceList = new ArrayList<>();

      while (clazz != null && !clazz.equals(Object.class)) {
         if (clazz.isInterface()) {
            interfaceList.add(clazz);
         }

         for (Class<?> interfaceClass : clazz.getInterfaces()) {
            interfaceList.addAll(getAllInterfaces(interfaceClass));
         }

         clazz = clazz.getSuperclass();
      }

      return interfaceList.stream().distinct().collect(Collectors.toList());
   }

   public static final List<Class<?>> getAllClasses(String packageName) {
      List<Class<?>> classList = MemoryCacheManager.get("classesUnderPackage:" + packageName);
      if (classList == null) {
         classList = new ArrayList<>();
         String packageDirName = StringUtils.removeStart(StringUtils.removeEnd(StringUtils.replace(packageName, ".", "/"), "/"), "/");
         ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
         MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

         try {
            Resource[] resources = resourcePatternResolver.getResources("classpath*:" + packageDirName + "/**/*.class");

            for (Resource r : resources) {
               MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(r);
               if (!"com.sunwayworld.framework.utils.EncodingUtils$BytesEncodingDetect".equals(metadataReader.getClassMetadata().getClassName())) {
                  Class<?> clazz = getClass(metadataReader.getClassMetadata().getClassName());
                  classList.add(clazz);
               }
            }
         } catch (IOException var12) {
            throw new UnexpectedException(var12);
         }

         MemoryCacheManager.put("classesUnderPackage:" + packageName, Collections.unmodifiableList(classList));
      }

      return classList;
   }

   @SafeVarargs
   public static final List<Resource> getResourceList(String packageName, Predicate<Resource>... predicates) {
      StringBuilder sb = new StringBuilder("classpath:");
      packageName = StringUtils.replace(packageName, ".", "/");
      if (!packageName.startsWith("/")) {
         sb.append("/");
      }

      sb.append(packageName);
      if (!packageName.endsWith("/")) {
         sb.append("/");
      }

      sb.append("**");

      try {
         Resource[] resources = new PathMatchingResourcePatternResolver().getResources(sb.toString());
         return Arrays.stream(resources)
            .filter(r -> r.exists() && r.isReadable() && (ArrayUtils.isEmpty(predicates) || Arrays.stream(predicates).allMatch(p -> p.test(r))))
            .collect(Collectors.toList());
      } catch (IOException var4) {
         throw new ZipException(var4);
      }
   }

   public static boolean isAnnotationPrsent(Class<?> type, Class<? extends Annotation> annotationClass) {
      for (Class<?> superClass = type; !Object.class.equals(superClass); superClass = type.getSuperclass()) {
         if (superClass.isAnnotationPresent(annotationClass)) {
            return true;
         }
      }

      for (Class<?> interfaceType : type.getInterfaces()) {
         if (isAnnotationPrsent(interfaceType, annotationClass)) {
            return true;
         }
      }

      return false;
   }

   public static List<Class<?>> getGikamConcreteClassList(Class<?> interfaceOrAbstractClass) {
      return getPredicatedClasses(
         "com.sunwayworld",
         c -> interfaceOrAbstractClass.isAssignableFrom(c) && !Modifier.isInterface(c.getModifiers()) && !Modifier.isAbstract(c.getModifiers())
      );
   }

   private static final String getFullyQualifiedName(final String className) {
      if (!className.endsWith("[]")) {
         return className;
      } else {
         StringBuffer sb = new StringBuffer();
         String name = className;

         while (name.endsWith("[]")) {
            name = name.substring(0, name.length() - 2);
            sb.append("[");
         }

         String abbreviation = getClassAbbreviation(name);
         if (StringUtils.isEmpty(abbreviation)) {
            sb.append("L").append(name).append(";");
         } else {
            sb.append(abbreviation);
         }

         return sb.toString();
      }
   }

   private static final String getClassAbbreviation(final String className) {
      if ("int".equals(className)) {
         return "I";
      } else if ("long".equals(className)) {
         return "J";
      } else if ("double".equals(className)) {
         return "D";
      } else if ("float".equals(className)) {
         return "F";
      } else if ("char".equals(className)) {
         return "C";
      } else if ("short".equals(className)) {
         return "S";
      } else if ("boolean".equals(className)) {
         return "Z";
      } else if ("byte".equals(className)) {
         return "B";
      } else {
         return "void".equals(className) ? "V" : className;
      }
   }

   static {
      primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
      primitiveWrapperTypeMap.put(Byte.class, byte.class);
      primitiveWrapperTypeMap.put(Character.class, char.class);
      primitiveWrapperTypeMap.put(Double.class, double.class);
      primitiveWrapperTypeMap.put(Float.class, float.class);
      primitiveWrapperTypeMap.put(Integer.class, int.class);
      primitiveWrapperTypeMap.put(Long.class, long.class);
      primitiveWrapperTypeMap.put(Short.class, short.class);
      primitiveWrapperTypeMap.forEach((w, p) -> {
         Class var10000 = primitiveTypeToWrapperMap.put((Class<?>)p, (Class<?>)w);
      });
   }
}
