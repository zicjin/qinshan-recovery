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
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

public class ClassUtils {
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<Class<?>, Class<?>>(8);
    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap<Class<?>, Class<?>>(8);

    static {
        primitiveWrapperTypeMap.put(Boolean.class, Boolean.TYPE);
        primitiveWrapperTypeMap.put(Byte.class, Byte.TYPE);
        primitiveWrapperTypeMap.put(Character.class, Character.TYPE);
        primitiveWrapperTypeMap.put(Double.class, Double.TYPE);
        primitiveWrapperTypeMap.put(Float.class, Float.TYPE);
        primitiveWrapperTypeMap.put(Integer.class, Integer.TYPE);
        primitiveWrapperTypeMap.put(Long.class, Long.TYPE);
        primitiveWrapperTypeMap.put(Short.class, Short.TYPE);
        for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
            primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
        }
    }

    public ClassUtils() { }

    public static final <T> T newInstance(Class<T> type) {
        Constructor<?> constructor = Arrays.stream(type.getDeclaredConstructors())
                .min((left, right) -> left.getParameterCount() - right.getParameterCount()).get();
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] arguments = new Object[parameterTypes.length];
        for (int index = 0; index < parameterTypes.length; index++) {
            Class<?> parameterType = parameterTypes[index];
            arguments[index] = isPrimitiveType(parameterType) ? getPrimitiveDefaultValue(parameterType) : null;
        }
        try {
            @SuppressWarnings("unchecked") T value = (T) constructor.newInstance(arguments);
            return value;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            throw new ClassInstantiationException(type, exception);
        }
    }

    public static final <T> T newInstance(Class<T> type, TypeValuePair<? extends Object>... pairs) {
        if (pairs == null || pairs.length == 0) {
            try {
                return type.newInstance();
            } catch (InstantiationException | IllegalAccessException exception) {
                throw new ClassInstantiationException(type, exception);
            }
        }
        Class<?>[] parameterTypes = new Class<?>[pairs.length];
        Object[] arguments = new Object[pairs.length];
        for (int index = 0; index < pairs.length; index++) {
            parameterTypes[index] = pairs[index].getFirst();
            arguments[index] = pairs[index].getSecond();
        }
        try {
            Constructor<T> constructor = type.getConstructor(parameterTypes);
            return constructor.newInstance(arguments);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException exception) {
            throw new ClassInstantiationException(type, exception);
        }
    }

    public static final <T> Constructor<T> getConstructorIfAvailable(Class<T> type, Class<?>... parameterTypes) {
        try {
            return type.getConstructor(parameterTypes);
        } catch (NoSuchMethodException exception) {
            return null;
        }
    }

    public static final Class<?> getRawType(Class<?> type) {
        if (type.getName().startsWith("com.sun.proxy.$Proxy")) {
            return type.getInterfaces()[0];
        }
        while (type.getName().contains("CGLIB$")) {
            type = type.getSuperclass();
        }
        return type;
    }

    public static final boolean isCglibProxyClass(Class<?> type) {
        return type != null && type.getName().contains("CGLIB$");
    }

    public static final boolean isPrimitiveType(Class<?> type) {
        return primitiveTypeToWrapperMap.containsKey(type);
    }

    public static final boolean isWrapperType(Class<?> type) {
        return primitiveWrapperTypeMap.containsKey(type);
    }

    public static final Class<?> getWrapperType(Class<?> type) {
        return primitiveTypeToWrapperMap.get(type);
    }

    public static final Class<?> getPrimitiveType(Class<?> type) {
        return primitiveWrapperTypeMap.get(type);
    }

    public static final Object getPrimitiveDefaultValue(Class<?> type) {
        if (type == Boolean.TYPE) return Boolean.valueOf(false);
        if (type == Byte.TYPE) return Character.valueOf('0');
        if (type == Character.TYPE) return Character.valueOf('0');
        if (type == Short.TYPE) return Short.valueOf(Short.parseShort("0"));
        if (type == Integer.TYPE) return Integer.valueOf(0);
        if (type == Float.TYPE) return Float.valueOf(0.0F);
        if (type == Double.TYPE) return Double.valueOf(0.0D);
        if (type == Long.TYPE) return Long.valueOf(0L);
        return null;
    }

    public static Class<?> resolvePrimitiveIfNecessary(Class<?> type) {
        return type.isPrimitive() && type != Void.TYPE ? getWrapperType(type) : type;
    }

    public static final boolean isSimpleType(Class<?> type) {
        return isPrimitiveType(type) || isWrapperType(type)
                || CharSequence.class.isAssignableFrom(type)
                || Number.class.isAssignableFrom(type)
                || Date.class.isAssignableFrom(type)
                || Map.class.isAssignableFrom(type)
                || Collection.class.isAssignableFrom(type)
                || Temporal.class.isAssignableFrom(type)
                || type == Object.class;
    }

    public static final boolean isGikamType(Class<?> type) {
        return type.getName().startsWith("com.sunwayworld");
    }

    public static final <T> Class<T> getClass(String name) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) loader = ClassLoader.getSystemClassLoader();
        String abbreviation = getClassAbbreviation(name);
        try {
            if (name.equals(abbreviation)) {
                @SuppressWarnings("unchecked") Class<T> result = (Class<T>) Class.forName(getFullyQualifiedName(name), false, loader);
                return result;
            }
            @SuppressWarnings("unchecked") Class<T> result = (Class<T>) Class.forName("[" + abbreviation, false, loader).getComponentType();
            return result;
        } catch (ClassNotFoundException exception) {
            throw new UnexpectedException(exception);
        }
    }

    public static final <T> Class<T> getGikamClass(String name) {
        for (Class<?> type : getAllClasses("com.sunwayworld")) {
            if (type.getSimpleName().equalsIgnoreCase(name)) {
                @SuppressWarnings("unchecked") Class<T> result = (Class<T>) type;
                return result;
            }
        }
        return null;
    }

    public static final List<Class<?>> getPredicatedClasses(String packageName, Predicate<Class<?>> predicate) {
        String normalized = normalizePackage(packageName);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory(resolver);
        try {
            Resource[] resources = resolver.getResources("classpath*:" + normalized + "/**/*.class");
            List<Class<?>> result = new ArrayList<Class<?>>();
            for (Resource resource : resources) {
                MetadataReader reader = factory.getMetadataReader(resource);
                Class<?> type = getClass(reader.getClassMetadata().getClassName());
                if (predicate.test(type)) result.add(type);
            }
            return result;
        } catch (IOException exception) {
            throw new UnexpectedException(exception);
        }
    }

    public static final boolean hasAnyMatchClass(String packageName, Predicate<Class<?>> predicate) {
        for (Class<?> type : getAllClasses(packageName)) {
            if (predicate.test(type)) return true;
        }
        return false;
    }

    public static final List<Class<?>> getAllInterfaces(Class<?> type) {
        List<Class<?>> result = new ArrayList<Class<?>>();
        while (type != null && !Object.class.equals(type)) {
            if (type.isInterface()) result.add(type);
            for (Class<?> interfaceType : type.getInterfaces()) result.addAll(getAllInterfaces(interfaceType));
            type = type.getSuperclass();
        }
        return result.stream().distinct().collect(java.util.stream.Collectors.toList());
    }

    public static final List<Class<?>> getAllClasses(String packageName) {
        String cacheKey = "classesUnderPackage:" + packageName;
        @SuppressWarnings("unchecked") List<Class<?>> cached = (List<Class<?>>) MemoryCacheManager.get(cacheKey);
        if (cached != null) return cached;
        List<Class<?>> result = new ArrayList<Class<?>>();
        String normalized = normalizePackage(packageName);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory(resolver);
        try {
            Resource[] resources = resolver.getResources("classpath*:" + normalized + "/**/*.class");
            for (Resource resource : resources) {
                MetadataReader reader = factory.getMetadataReader(resource);
                String className = reader.getClassMetadata().getClassName();
                if ("com.sunwayworld.framework.utils.EncodingUtils$BytesEncodingDetect".equals(className)) continue;
                result.add(getClass(className));
            }
        } catch (IOException exception) {
            throw new UnexpectedException(exception);
        }
        MemoryCacheManager.put(cacheKey, Collections.unmodifiableList(result));
        return result;
    }

    public static final List<Resource> getResourceList(String packageName, Predicate<Resource>... predicates) {
        StringBuilder pattern = new StringBuilder("classpath:");
        packageName = packageName.replace(".", "/");
        if (!packageName.startsWith("/")) pattern.append('/');
        pattern.append(packageName);
        if (!packageName.endsWith("/")) pattern.append('/');
        pattern.append("**");
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(pattern.toString());
            List<Resource> result = new ArrayList<Resource>();
            for (Resource resource : resources) {
                if (!resource.exists() || !resource.isReadable()) continue;
                if (predicates != null && predicates.length != 0) {
                    boolean match = true;
                    for (Predicate<Resource> predicate : predicates) if (!predicate.test(resource)) { match = false; break; }
                    if (!match) continue;
                }
                result.add(resource);
            }
            return result;
        } catch (IOException exception) {
            throw new ZipException(exception);
        }
    }

    public static boolean isAnnotationPrsent(Class<?> type, Class<? extends Annotation> annotation) {
        Class<?> current = type;
        while (!Object.class.equals(current)) {
            if (current.isAnnotationPresent(annotation)) return true;
            current = type.getSuperclass();
        }
        for (Class<?> interfaceType : type.getInterfaces()) if (isAnnotationPrsent(interfaceType, annotation)) return true;
        return false;
    }

    public static List<Class<?>> getGikamConcreteClassList(Class<?> type) {
        return getPredicatedClasses("com.sunwayworld", candidate -> type.isAssignableFrom(candidate)
                && !Modifier.isInterface(candidate.getModifiers()) && !Modifier.isAbstract(candidate.getModifiers()));
    }

    private static String normalizePackage(String packageName) {
        if (packageName == null) return null;
        String normalized = packageName.replace(".", "/");
        if (normalized.endsWith("/")) normalized = normalized.substring(0, normalized.length() - 1);
        if (normalized.startsWith("/")) normalized = normalized.substring(1);
        return normalized;
    }

    private static String getFullyQualifiedName(String name) {
        if (!name.endsWith("[]")) return name;
        StringBuilder result = new StringBuilder();
        String base = name;
        while (base.endsWith("[]")) {
            base = base.substring(0, base.length() - 2);
            result.append('[');
        }
        String abbreviation = getClassAbbreviation(base);
        if (abbreviation.isEmpty()) result.append('L').append(base).append(';');
        else result.append(abbreviation);
        return result.toString();
    }

    private static String getClassAbbreviation(String name) {
        if ("int".equals(name)) return "I";
        if ("long".equals(name)) return "J";
        if ("double".equals(name)) return "D";
        if ("float".equals(name)) return "F";
        if ("char".equals(name)) return "C";
        if ("short".equals(name)) return "S";
        if ("boolean".equals(name)) return "Z";
        if ("byte".equals(name)) return "B";
        if ("void".equals(name)) return "V";
        return name;
    }
}
