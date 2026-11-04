package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.cache.memory.MemoryCacheManager;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.GikamBeanContext;
import com.sunwayworld.framework.exception.ReflectionException;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import sun.reflect.generics.reflectiveObjects.GenericArrayTypeImpl;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;

public abstract class ReflectionUtils {
   public static final boolean isReadMethod(Method method) {
      String methodName = method.getName();
      return (
            StringUtils.startsWith(methodName, "CGLIB$get")
               || StringUtils.startsWith(methodName, "get")
               || StringUtils.startsWith(methodName, "CGLIB$is")
               || StringUtils.startsWith(methodName, "is")
         )
         && method.getParameterCount() == 0;
   }

   public static final boolean isWriteMethod(Method method) {
      String methodName = method.getName();
      return (StringUtils.startsWith(methodName, "CGLIB$set") || StringUtils.startsWith(methodName, "set")) && method.getParameterCount() == 1;
   }

   public static final Method findReadMethod(Class<?> clazz, String propertyName) {
      Method readMethod = findMethod(clazz, "get" + propertyName);
      return readMethod == null ? findMethod(clazz, "is" + propertyName) : readMethod;
   }

   public static final Method findWriteMethod(Class<?> clazz, String propertyName) {
      return Arrays.asList(clazz.getMethods())
         .stream()
         .filter(m -> m.getName().equalsIgnoreCase("set" + propertyName) && m.getParameterCount() == 1)
         .findFirst()
         .orElse(null);
   }

   public static final Method getUniqueMethodByName(Class<?> clazz, String name) {
      List<ReflectionUtils.MethodWrapper> methodList = getMethodWrapperList(clazz)
         .stream()
         .filter(m -> name.equalsIgnoreCase(m.getMethod().getName()))
         .collect(Collectors.toList());
      if (methodList.isEmpty()) {
         return null;
      } else if (methodList.size() == 1) {
         return methodList.get(0).getMethod();
      } else {
         throw new ApplicationRuntimeException("GIKAM.EXCEPTION.REFLECTION.DUPLICATED_METHOD_NAME", clazz.toString(), name);
      }
   }

   public static final List<ReflectionUtils.MethodWrapper> getMethodWrapperListByName(Class<?> clazz, String name) {
      return getMethodWrapperList(clazz).stream().filter(m -> name.equalsIgnoreCase(m.getMethod().getName())).collect(Collectors.toList());
   }

   public static List<Method> getMethodList(Class<?> clazz, int... modifiers) {
      return ArrayUtils.isEmpty(modifiers)
         ? getMethodWrapperList(clazz).stream().map(m -> m.getMethod()).collect(Collectors.toList())
         : getMethodWrapperList(clazz)
            .stream()
            .filter(m -> Arrays.stream(modifiers).allMatch(f -> (m.getMethod().getModifiers() & f) != 0))
            .map(m -> m.getMethod())
            .collect(Collectors.toList());
   }

   public static List<ReflectionUtils.MethodWrapper> getAnnotatedMethodList(Class<?> clazz, Class<? extends Annotation> annotationClazz) {
      return getMethodWrapperList(clazz)
         .stream()
         .filter(m -> m.getAnnotationList().stream().anyMatch(a -> a.annotationType().equals(annotationClazz)))
         .collect(Collectors.toList());
   }

   public static List<Class<?>> getAllInterfaces(Class<?> clazz) {
      if (clazz != null && !Object.class.equals(clazz)) {
         List<Class<?>> interfaceList = new ArrayList<>();

         for (Class<?> searchClazz = clazz; searchClazz != null; searchClazz = searchClazz.getSuperclass()) {
            for (Class<?> iClass : searchClazz.getInterfaces()) {
               interfaceList.add(iClass);
               interfaceList.addAll(getAllInterfaces(iClass));
            }
         }

         return interfaceList;
      } else {
         return CollectionUtils.emptyList();
      }
   }

   public static final Method findMethod(Class<?> clazz, String name) {
      return findMethod(clazz, name);
   }

   public static final Method findMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
      return getMethodWrapperList(clazz).stream().filter(w -> {
         if (!w.getMethod().getName().equalsIgnoreCase(name)) {
            return false;
         } else if (!ArrayUtils.isEmpty(parameterTypes)) {
            if (parameterTypes.length != w.getParamTypes().length) {
               return false;
            } else {
               for (int i = 0; i < parameterTypes.length; i++) {
                  if (!StringUtils.startsWith(w.getParamTypes()[i].getTypeName(), parameterTypes[i].getTypeName())) {
                     return false;
                  }
               }

               return true;
            }
         } else {
            return w.getParamTypes().length == 0;
         }
      }).findAny().map(w -> w.getMethod()).orElse(null);
   }

   public static final Method findMethodWithReturnType(Class<?> clazz, String name, Type returnType) {
      List<ReflectionUtils.MethodWrapper> methodWrapperList = getMethodWrapperList(clazz)
         .stream()
         .filter(w -> !w.getMethod().getName().equalsIgnoreCase(name) ? false : Objects.equals(w.getReturnType(), returnType))
         .collect(Collectors.toList());
      if (methodWrapperList.isEmpty()) {
         return null;
      } else if (methodWrapperList.size() == 1) {
         return methodWrapperList.get(0).getMethod();
      } else {
         throw new ApplicationRuntimeException(
            "GIKAM.EXCEPTION.REFLECTION.DUPLICATED_METHOD_NAME_WITH_RETURNTYPE", clazz.toString(), name, returnType.getTypeName()
         );
      }
   }

   public static final Object invokeMethod(Method method, Object target) {
      return invokeMethod(method, target);
   }

   public static final Object invokeMethod(Method method, Object target, Object... args) {
      boolean accessible = method.isAccessible();

      Object e;
      try {
         if (!accessible) {
            method.setAccessible(true);
         }

         e = method.invoke(target, args);
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var8) {
         throw new ApplicationRuntimeException(var8, var8.getMessage());
      } finally {
         if (!accessible) {
            method.setAccessible(accessible);
         }
      }

      return e;
   }

   public static final void invokeWriteMethod(Object target, String propertyName, Object arg) {
      Method writeMethod = findWriteMethod(target.getClass(), propertyName);
      if (writeMethod != null) {
         invokeMethod(writeMethod, target, arg);
      } else {
         throw new ApplicationRuntimeException("GIKAM.EXCEPTION.REFLECTION.FIELD_NO_SETTER", target.getClass().getName(), propertyName);
      }
   }

   public static final Object invokeReadMethod(Object target, String propertyName) {
      Class<?> aClass = target.getClass();
      String key = aClass.getName() + "$" + propertyName;
      Method readMethod = ReflectionUtils.InternalCache.CLASS_PROPERTY_READ_METHOD_CONTAINER.get(key);
      if (readMethod == null) {
         readMethod = findReadMethod(aClass, propertyName);
         if (readMethod != null) {
            ReflectionUtils.InternalCache.CLASS_PROPERTY_READ_METHOD_CONTAINER.put(key, readMethod);
         }
      }

      if (readMethod != null) {
         return invokeMethod(readMethod, target);
      } else {
         throw new ApplicationRuntimeException("GIKAM.EXCEPTION.REFLECTION.FIELD_NO_GETTER", aClass.getName(), propertyName);
      }
   }

   public static List<Field> getFiledList(Class<?> clazz, int... modifiers) {
      return ArrayUtils.isEmpty(modifiers)
         ? getFieldWrapperList(clazz).stream().map(w -> w.getField()).collect(Collectors.toList())
         : getFieldWrapperList(clazz)
            .stream()
            .filter(w -> Arrays.stream(modifiers).allMatch(m -> (w.getField().getModifiers() & m) != 0))
            .map(w -> w.getField())
            .collect(Collectors.toList());
   }

   public static Field findField(Class<?> clazz, String name) {
      return getFieldWrapperList(clazz).stream().filter(w -> w.getField().getName().equalsIgnoreCase(name)).map(w -> w.getField()).findAny().orElse(null);
   }

   public static final Field findFieldOfReadMethod(Method readMethod) {
      if (!isReadMethod(readMethod)) {
         throw new ApplicationRuntimeException("GIKAM.EXCEPTION.REFLECTION.METHOD_IS_NOT_GETTER", readMethod.toString());
      } else {
         Class<?> clazz = ClassUtils.getRawType(readMethod.getDeclaringClass());
         String methodName = readMethod.getName();
         String propertyName = null;
         if (StringUtils.startsWith(methodName, "is")) {
            propertyName = methodName.substring(2);
         } else {
            propertyName = methodName.substring(3);
         }

         Field field = findField(clazz, propertyName);
         if (field == null) {
            throw new ApplicationRuntimeException("GIKAM.EXCEPTION.REFLECTION.NO_FIELD_FOR_GETTER_METHOD", readMethod.getName());
         } else if (!Objects.equals(field.getType().getTypeName(), readMethod.getReturnType().getTypeName())) {
            throw new ApplicationRuntimeException("GIKAM.EXCEPTION.REFLECTION.FIELD_FOR_GETTER_METHOD_TYPE_UNMATCH", readMethod.getName());
         } else {
            return field;
         }
      }
   }

   public static final Field findFieldOfWriteMethod(Method writeMethod) {
      if (!isWriteMethod(writeMethod)) {
         throw new ApplicationRuntimeException("GIKAM.EXCEPTION.REFLECTION.METHOD_IS_NOT_SETTER", writeMethod.toString());
      } else {
         String propertyName = writeMethod.getName().substring(3);
         Field field = findField(writeMethod.getDeclaringClass(), StringUtils.uncapitalize(propertyName));
         if (field == null) {
            throw new ApplicationRuntimeException("GIKAM.EXCEPTION.REFLECTION.NO_FIELD_FOR_SETTER_METHOD", writeMethod.getName());
         } else if (!Objects.equals(field.getType().getTypeName(), writeMethod.getReturnType().getTypeName())) {
            throw new ApplicationRuntimeException("GIKAM.EXCEPTION.REFLECTION.FIELD_FOR_SETTER_METHOD_TYPE_UNMATCH", writeMethod.getName());
         } else {
            return field;
         }
      }
   }

   public static final Object getFieldValue(Object target, String propertyName) {
      Field property = findField(target.getClass(), propertyName);
      if (property == null) {
         return null;
      } else {
         Object value = null;

         try {
            makeAccesible(property);
            return property.get(target);
         } catch (IllegalAccessException | IllegalArgumentException var5) {
            throw new ReflectionException(var5);
         }
      }
   }

   public static final void setFieldValue(Object target, String propertyName, Object value) {
      Field property = findField(target.getClass(), propertyName);
      if (property != null) {
         try {
            makeAccesible(property);
            property.set(target, value);
         } catch (IllegalAccessException | IllegalArgumentException var5) {
            throw new ReflectionException(var5);
         }
      }
   }

   public static final void setStaticFieldValue(Class<?> clazz, String propertyName, Object value) {
      Field property = findField(clazz, propertyName);
      if (property != null) {
         try {
            makeAccesible(property);
            property.set(null, value);
         } catch (IllegalAccessException | SecurityException | IllegalArgumentException var5) {
            throw new ReflectionException(var5);
         }
      }
   }

   public static final <T extends Annotation> T getAnnotation(Class<?> targetClass, Class<T> annotationClass) {
      return (T)getClassAnnotationList(targetClass).stream().filter(a -> a.annotationType().equals(annotationClass)).findAny().orElse(null);
   }

   public static final <T extends Annotation> T getAnnotation(Method method, Class<T> annotationClass) {
      String cacheKey = method.toString() + "-" + annotationClass.getName();
      T cacheAnnotation = null;
      if (cacheAnnotation == null) {
         Class<?> clazz = method.getDeclaringClass();
         ReflectionUtils.MethodWrapper methodWrapper = getMethodWrapperList(clazz).stream().filter(w -> w.getMethod().equals(method)).findAny().orElse(null);
         if (methodWrapper != null) {
            cacheAnnotation = (T)methodWrapper.getAnnotationList().stream().filter(a -> a.annotationType().equals(annotationClass)).findAny().orElse(null);
            if (cacheAnnotation != null) {
               ReflectionUtils.InternalCache.METHOD_ANNOTATION_CONTAINER.put(cacheKey, cacheAnnotation);
            }
         }
      }

      return cacheAnnotation;
   }

   public static final boolean isValidBeanProperty(Class<?> type, Field property) {
      return !property.getName().equalsIgnoreCase("readonly")
         && findReadMethod(type, property.getName()) != null
         && findWriteMethod(type, property.getName()) != null;
   }

   public static List<ReflectionUtils.ConcreteMethod> getConcreteMethodByAnnotation(
      Class<? extends Annotation> classAnnotationType, Class<? extends Annotation> methodAnnotationType
   ) {
      String cacheKey = "METHOD$" + classAnnotationType + "$" + methodAnnotationType;
      List<ReflectionUtils.ConcreteMethod> concreteMethodList = MemoryCacheManager.get(cacheKey);
      if (concreteMethodList == null) {
         synchronized (GikamBeanContext.class) {
            concreteMethodList = MemoryCacheManager.get(cacheKey);
            if (concreteMethodList == null) {
               concreteMethodList = new ArrayList<>();
               ApplicationContext applicationContext = ApplicationContextHelper.getApplicationContext();
               Map<String, Object> map = applicationContext.getBeansWithAnnotation(classAnnotationType);

               for (Entry<String, Object> entry : map.entrySet()) {
                  Class<?> rawType = ClassUtils.getRawType(entry.getValue().getClass());
                  List<ReflectionUtils.MethodWrapper> annotatedMethodWrapperList = getAnnotatedMethodList(rawType, methodAnnotationType);
                  if (!annotatedMethodWrapperList.isEmpty()) {
                     for (ReflectionUtils.MethodWrapper methodWrapper : annotatedMethodWrapperList) {
                        concreteMethodList.add(new ReflectionUtils.ConcreteMethod(rawType, methodWrapper.getMethod()));
                     }
                  }
               }

               MemoryCacheManager.put(cacheKey, Collections.unmodifiableCollection(concreteMethodList));
            }
         }
      }

      return concreteMethodList;
   }

   public static final String getSimpleSignature(Class<?> clazz, Method method) {
      StringBuilder sb = new StringBuilder(clazz.getName()).append(".").append(method.getName()).append("(");
      Type[] types = method.getGenericParameterTypes();
      int i = 0;

      for (int j = types.length; i < j; i++) {
         Type type = types[i];
         if (i > 0) {
            sb.append(", ");
         }

         sb.append(getSimpleName(type));
      }

      sb.append(")");
      return sb.toString();
   }

   public static List<Annotation> getClassAnnotationList(Class<?> clazz) {
      clazz = ClassUtils.getRawType(clazz);
      List<Annotation> annotationList = new ArrayList<>();

      for (Class<?> loopClass = clazz; loopClass != null; loopClass = loopClass.getSuperclass()) {
         Annotation[] annotations = loopClass.getDeclaredAnnotations();

         for (Annotation annotation : annotations) {
            if (annotationList.stream().noneMatch(a -> a.annotationType().equals(annotation.annotationType()))) {
               annotationList.add(annotation);
            }
         }
      }

      for (Class<?> var13 = clazz; var13 != null; var13 = var13.getSuperclass()) {
         for (Class<?> interfaceClass : var13.getInterfaces()) {
            Annotation[] annotations = interfaceClass.getDeclaredAnnotations();

            for (Annotation annotationx : annotations) {
               if (annotationList.stream().noneMatch(a -> a.annotationType().equals(annotationx.annotationType()))) {
                  annotationList.add(annotationx);
               }
            }
         }
      }

      ReflectionUtils.InternalCache.CLASS_ANNOTATION_CONTAINER.put(clazz, annotationList);
      return annotationList;
   }

   public static List<ReflectionUtils.FieldWrapper> getFieldWrapperList(Class<?> clazz) {
      clazz = ClassUtils.getRawType(clazz);
      List<ReflectionUtils.FieldWrapper> fieldWrapperList = new ArrayList<>();
      Map<String, Class<?>> genericParamMap = getGenericParamMap(clazz);

      for (Class<?> loopClazz = clazz; loopClazz != null; loopClazz = loopClazz.getSuperclass()) {
         for (Field field : loopClazz.getDeclaredFields()) {
            if (fieldWrapperList.stream().noneMatch(w -> w.getField().getName().equals(field.getName()))) {
               String typeName = field.getGenericType().getTypeName();
               if (genericParamMap.containsKey(typeName)) {
                  fieldWrapperList.add(new ReflectionUtils.FieldWrapper(clazz, field, genericParamMap.get(typeName)));
               } else {
                  fieldWrapperList.add(new ReflectionUtils.FieldWrapper(clazz, field, field.getType()));
               }
            }
         }
      }

      for (Field fieldx : clazz.getFields()) {
         if (fieldWrapperList.stream().noneMatch(w -> w.getField().getName().equals(fieldx.getName()))) {
            String typeName = fieldx.getGenericType().getTypeName();
            if (genericParamMap.containsKey(typeName)) {
               fieldWrapperList.add(new ReflectionUtils.FieldWrapper(clazz, fieldx, genericParamMap.get(typeName)));
            } else {
               fieldWrapperList.add(new ReflectionUtils.FieldWrapper(clazz, fieldx, fieldx.getType()));
            }
         }
      }

      ReflectionUtils.InternalCache.CLASS_FIELD_CONTAINER.put(clazz, fieldWrapperList);
      return fieldWrapperList;
   }

   public static List<ReflectionUtils.MethodWrapper> getMethodWrapperList(Class<?> clazz) {
      clazz = ClassUtils.getRawType(clazz);
      List<ReflectionUtils.MethodWrapper> methodWrapperList = new ArrayList<>();
      Map<String, Class<?>> genericParamMap = getGenericParamMap(clazz);
      extractMethodWrapper(clazz, methodWrapperList, genericParamMap, clazz);
      methodWrapperList.sort((m1, m2) -> m1.getMethod().getName().compareTo(m2.getMethod().getName()));
      return methodWrapperList;
   }

   private static void extractMethodWrapper(
      Class<?> rootClazz, List<ReflectionUtils.MethodWrapper> methodWrapperList, Map<String, Class<?>> genericParamMap, Class<?> clazz
   ) {
      if (!clazz.equals(Object.class)) {
         for (Class<?> loopClazz = clazz; loopClazz != null; loopClazz = loopClazz.getSuperclass()) {
            for (Method method : loopClazz.getDeclaredMethods()) {
               if (!method.getName().startsWith("lambda$")) {
                  Type returnType = (Type)(method.getGenericReturnType() != null && genericParamMap.containsKey(method.getGenericReturnType().getTypeName())
                     ? genericParamMap.get(method.getGenericReturnType().getTypeName())
                     : method.getGenericReturnType());
                  Type[] paramTypes = method.getGenericParameterTypes();

                  for (int i = 0; i < paramTypes.length; i++) {
                     Type paramType = paramTypes[i];
                     if (genericParamMap.containsKey(paramType.getTypeName())) {
                        paramTypes[i] = genericParamMap.get(paramType.getTypeName());
                     }
                  }

                  ReflectionUtils.MethodWrapper methodWrapper = new ReflectionUtils.MethodWrapper(
                     rootClazz, method, paramTypes, returnType, Arrays.asList(method.getDeclaredAnnotations())
                  );
                  ReflectionUtils.MethodWrapper existingMethodWrapper = methodWrapperList.stream()
                     .filter(w -> isSuperMethod(w, methodWrapper) || w.equals(methodWrapper))
                     .findAny()
                     .orElse(null);
                  methodWrapperList.removeIf(w -> isSuperMethod(w, methodWrapper));
                  if (methodWrapperList.stream().noneMatch(w -> w.equals(methodWrapper))) {
                     if (existingMethodWrapper != null) {
                        existingMethodWrapper.getAnnotationList().forEach(a -> methodWrapper.addAnnotation(a));
                     }

                     methodWrapperList.add(methodWrapper);
                  } else {
                     methodWrapper.getAnnotationList().forEach(a -> existingMethodWrapper.addAnnotation(a));
                  }
               }
            }
         }

         for (Class<?> var13 = clazz; var13 != null; var13 = var13.getSuperclass()) {
            for (Class<?> interfaceClass : var13.getInterfaces()) {
               extractMethodWrapper(rootClazz, methodWrapperList, genericParamMap, interfaceClass);
            }
         }
      }
   }

   private static boolean isSuperMethod(ReflectionUtils.MethodWrapper superMethodWrapper, ReflectionUtils.MethodWrapper methodWrapper) {
      if (!Objects.equals(superMethodWrapper.getMethod().getName(), methodWrapper.getMethod().getName())) {
         return false;
      } else if (superMethodWrapper.getParamTypes().length != methodWrapper.getParamTypes().length) {
         return false;
      } else if (superMethodWrapper.getReturnType() == null && methodWrapper.getReturnType() != null) {
         return false;
      } else if (methodWrapper.getReturnType() == null) {
         return false;
      } else {
         boolean isSuper = false;
         if (isSuperType(superMethodWrapper.getReturnType(), methodWrapper.getReturnType())) {
            isSuper = true;
         } else if (!Objects.equals(superMethodWrapper.getReturnType().getTypeName(), methodWrapper.getReturnType().getTypeName())) {
            return false;
         }

         for (int i = 0; i < superMethodWrapper.getParamTypes().length; i++) {
            if (isSuperType(superMethodWrapper.getParamTypes()[i], methodWrapper.getParamTypes()[i])) {
               isSuper = true;
            } else if (!Objects.equals(superMethodWrapper.getParamTypes()[i].getTypeName(), methodWrapper.getParamTypes()[i].getTypeName())) {
               return false;
            }
         }

         return isSuper ? true : superMethodWrapper.getMethod().getDeclaringClass().isAssignableFrom(methodWrapper.getMethod().getDeclaringClass());
      }
   }

   private static void makeAccesible(Field field) {
      if (field != null) {
         if (Modifier.isFinal(field.getModifiers())) {
            try {
               Field modifiersProperty = Field.class.getDeclaredField("modifiers");
               modifiersProperty.setAccessible(true);
               modifiersProperty.setInt(field, field.getModifiers() & -17);
            } catch (IllegalAccessException | NoSuchFieldException | SecurityException | IllegalArgumentException var2) {
               throw new ApplicationRuntimeException(var2);
            }
         }

         if (!field.isAccessible()) {
            field.setAccessible(true);
         }
      }
   }

   private static String getSimpleName(Type type) {
      if (type instanceof Class) {
         return ((Class)type).getSimpleName();
      } else {
         String name = type.getTypeName();
         return !name.contains(".") ? name : name.replaceAll("([a-zA-Z]+\\.)+", "");
      }
   }

   private static Map<String, Class<?>> getGenericParamMap(Class<?> clazz) {
      return getGenericParamMap(ResolvableType.forClass(clazz));
   }

   private static Map<String, Class<?>> getGenericParamMap(ResolvableType rt) {
      Map<String, Class<?>> map = new HashMap<>();
      if (rt.hasGenerics()) {
         Class<?> clazz = rt.getRawClass();
         TypeVariable<?>[] typeVariables = clazz.getTypeParameters();
         Type type = rt.getType();
         if (type instanceof ParameterizedTypeImpl) {
            ParameterizedTypeImpl rti = (ParameterizedTypeImpl)type;

            for (int i = 0; i < typeVariables.length; i++) {
               TypeVariable<?> tv = typeVariables[i];
               String genericName = tv.getTypeName();
               String actualTypeName = rti.getActualTypeArguments()[i].getTypeName();
               if (!Objects.equals(genericName, actualTypeName)) {
                  try {
                     map.put(tv.getTypeName(), Class.forName(rti.getActualTypeArguments()[i].getTypeName()));
                  } catch (ClassNotFoundException var11) {
                  }
               }
            }
         }
      }

      ResolvableType st = rt.getSuperType();
      if (!st.toClass().equals(Object.class)) {
         map.putAll(getGenericParamMap(st));
      }

      for (ResolvableType it : rt.getInterfaces()) {
         map.putAll(getGenericParamMap(it));
      }

      return map;
   }

   private static boolean isSuperType(Type superType, Type type) {
      if (type.equals(superType)) {
         return false;
      } else {
         Class<?> typeClass = getTypeClass(type);
         if (Void.class.equals(typeClass)) {
            return false;
         } else {
            Class<?> superTypeClass = getTypeClass(superType);
            return Void.class.equals(superTypeClass) ? false : superTypeClass.isAssignableFrom(typeClass);
         }
      }
   }

   private static Class<?> getTypeClass(Type type) {
      if (type instanceof ParameterizedTypeImpl) {
         return ((ParameterizedTypeImpl)type).getRawType();
      } else if (!(type instanceof TypeVariableImpl) && !(type instanceof GenericArrayTypeImpl)) {
         return (Class<?>)type;
      } else {
         try {
            return Class.forName(type.getTypeName());
         } catch (ClassNotFoundException var2) {
            return Void.class;
         }
      }
   }

   public static class ConcreteMethod {
      private final Class<?> declaringClass;
      private final Method method;

      public ConcreteMethod(Class<?> declaringClass, Method method) {
         this.declaringClass = declaringClass;
         this.method = method;
      }

      public Class<?> getDeclaringClass() {
         return this.declaringClass;
      }

      public Method getMethod() {
         return this.method;
      }
   }

   public static class FieldWrapper {
      private final Class<?> declaringClass;
      private final Field field;
      private final Type type;

      public FieldWrapper(Class<?> declaringClass, Field field, Type type) {
         this.declaringClass = declaringClass;
         this.field = field;
         this.type = type;
      }

      public Class<?> getDeclaringClass() {
         return this.declaringClass;
      }

      public Field getField() {
         return this.field;
      }

      public Type getType() {
         return this.type;
      }

      @Override
      public String toString() {
         int mod = this.field.getModifiers();
         return (mod == 0 ? "" : Modifier.toString(mod) + " ")
            + this.getType().getTypeName()
            + " "
            + this.getDeclaringClass().getTypeName()
            + "."
            + this.field.getName();
      }
   }

   private static class InternalCache {
      static final Map<Class<?>, List<ReflectionUtils.MethodWrapper>> CLASS_METHOD_CONTAINER = new ConcurrentHashMap<>();
      static final Map<String, Method> CLASS_PROPERTY_READ_METHOD_CONTAINER = new ConcurrentHashMap<>();
      static final Map<Class<?>, List<ReflectionUtils.FieldWrapper>> CLASS_FIELD_CONTAINER = new ConcurrentHashMap<>();
      static final Map<Class<?>, List<Annotation>> CLASS_ANNOTATION_CONTAINER = new ConcurrentHashMap<>();
      static final Map<String, Annotation> METHOD_ANNOTATION_CONTAINER = new ConcurrentHashMap<>();
   }

   public static class MethodWrapper {
      private final Class<?> declaringClass;
      private final Method method;
      private final Type returnType;
      private final Type[] paramTypes;
      private final List<Annotation> annotationList;

      public MethodWrapper(Class<?> declaringClass, Method method, Type[] paramTypes, Type returnType, List<Annotation> annotationList) {
         this.declaringClass = declaringClass;
         this.method = method;
         this.paramTypes = paramTypes;
         this.returnType = returnType;
         this.annotationList = new ArrayList<>(annotationList);
      }

      public Class<?> getDeclaringClass() {
         return this.declaringClass;
      }

      public Method getMethod() {
         return this.method;
      }

      public Type getReturnType() {
         return this.returnType;
      }

      public Type[] getParamTypes() {
         return this.paramTypes;
      }

      public List<Annotation> getAnnotationList() {
         return this.annotationList;
      }

      public void addAnnotation(Annotation annotation) {
         if (this.annotationList.stream().noneMatch(a -> a.annotationType().equals(annotation.annotationType()))) {
            this.annotationList.add(annotation);
         }
      }

      @Override
      public boolean equals(Object ob) {
         if (ob == null) {
            return false;
         } else if (this == ob) {
            return true;
         } else if (!(ob instanceof ReflectionUtils.MethodWrapper)) {
            return false;
         } else {
            ReflectionUtils.MethodWrapper mw = (ReflectionUtils.MethodWrapper)ob;
            if (!this.getMethod().getName().equals(mw.getMethod().getName())) {
               return false;
            } else if (mw.getParamTypes().length != this.getParamTypes().length) {
               return false;
            } else {
               for (int i = 0; i < this.getParamTypes().length; i++) {
                  if (!Objects.equals(mw.getParamTypes()[i].getTypeName(), this.getParamTypes()[i].getTypeName())) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      @Override
      public String toString() {
         StringBuilder sb = new StringBuilder();
         int mod = this.method.getModifiers() & Modifier.methodModifiers();
         if (mod != 0 && !this.method.isDefault()) {
            sb.append(Modifier.toString(mod)).append(' ');
         } else {
            int access_mod = mod & 7;
            if (access_mod != 0) {
               sb.append(Modifier.toString(access_mod)).append(' ');
            }

            if (this.method.isDefault()) {
               sb.append("default ");
            }

            mod &= -8;
            if (mod != 0) {
               sb.append(Modifier.toString(mod)).append(' ');
            }
         }

         sb.append(this.returnType.getTypeName()).append(' ');
         sb.append(this.declaringClass.getTypeName()).append('.');
         sb.append(this.method.getName());
         sb.append('(');

         for (int j = 0; j < this.paramTypes.length; j++) {
            sb.append(this.paramTypes[j].getTypeName());
            if (j < this.paramTypes.length - 1) {
               sb.append(",");
            }
         }

         sb.append(')');
         if (this.method.getExceptionTypes().length > 0) {
            sb.append(" throws ");

            for (int jx = 0; jx < this.method.getExceptionTypes().length; jx++) {
               sb.append(this.method.getExceptionTypes()[jx].getTypeName());
               if (jx < this.method.getExceptionTypes().length - 1) {
                  sb.append(",");
               }
            }
         }

         return sb.toString();
      }
   }
}
