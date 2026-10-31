package com.sunwayworld.framework;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Differential contract for data holders, tuples, tri-consumers, and annotations. */
public final class DataLeavesDifferentialTest {
    private interface Action { Object run() throws Exception; }

    private static URLClassLoader loader(File implementation, File dependencies) throws Exception {
        List<URL> urls = new ArrayList<URL>();
        urls.add(implementation.toURI().toURL());
        File[] jars = dependencies.listFiles();
        if (jars != null) for (File jar : jars) if (jar.getName().endsWith(".jar")) urls.add(jar.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[urls.size()]), null);
    }

    private static String failure(Throwable error) {
        return "!" + error.getClass().getName() + ":" + String.valueOf(error.getMessage());
    }

    private static void record(List<String> result, String name, Action action) {
        try { result.add(name + "=" + value(action.run())); }
        catch (InvocationTargetException error) { result.add(name + "=" + failure(error.getCause())); }
        catch (Throwable error) { result.add(name + "=" + failure(error)); }
    }

    private static String value(Object value) {
        if (value == null) return "null";
        if (value instanceof Object[]) return Arrays.deepToString((Object[]) value);
        return String.valueOf(value);
    }

    private static byte[] serialize(Object value) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream stream = new ObjectOutputStream(bytes)) { stream.writeObject(value); }
        return bytes.toByteArray();
    }

    private static Object deserialize(byte[] bytes, final ClassLoader loader) throws Exception {
        try (ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(bytes)) {
            @Override protected Class<?> resolveClass(ObjectStreamClass descriptor) throws IOException, ClassNotFoundException {
                return Class.forName(descriptor.getName(), false, loader);
            }
        }) { return stream.readObject(); }
    }

    private static List<String> declarations(Class<?> type) {
        List<String> declarations = new ArrayList<String>();
        declarations.add(type.toGenericString());
        for (Constructor<?> constructor : type.getDeclaredConstructors()) declarations.add(constructor.toGenericString());
        for (Method method : type.getDeclaredMethods()) declarations.add(method.toGenericString());
        Collections.sort(declarations);
        return declarations;
    }

    private static List<String> observe(ClassLoader loader) throws Exception {
        Class<?> holder = loader.loadClass("com.sunwayworld.framework.data.ObjectHolder");
        Class<?> resultEntity = loader.loadClass("com.sunwayworld.framework.data.ResultEntity");
        Class<?> triple = loader.loadClass("com.sunwayworld.framework.data.Triple");
        Class<?> triConsumer = loader.loadClass("com.sunwayworld.framework.data.TriConsumer");
        List<String> result = new ArrayList<String>();
        for (Class<?> type : new Class<?>[] {holder, resultEntity, triple, triConsumer})
            result.add("declarations=" + declarations(type));

        Constructor<?> holderDefault = holder.getConstructor();
        Constructor<?> holderValue = holder.getConstructor(Object.class);
        Object emptyHolder = holderDefault.newInstance();
        Object valueHolder = holderValue.newInstance("initial");
        record(result, "holder-modifiers", () -> Modifier.toString(holder.getModifiers()));
        record(result, "holder-default-get", () -> holder.getMethod("get").invoke(emptyHolder));
        record(result, "holder-default-default", () -> holder.getMethod("getOrDefault", Object.class).invoke(emptyHolder, "fallback"));
        record(result, "holder-value-get", () -> holder.getMethod("get").invoke(valueHolder));
        record(result, "holder-value-default", () -> holder.getMethod("getOrDefault", Object.class).invoke(valueHolder, "fallback"));
        record(result, "holder-set-null", () -> { holder.getMethod("setObject", Object.class).invoke(valueHolder, new Object[] {null}); return holder.getMethod("getOrDefault", Object.class).invoke(valueHolder, "fallback"); });
        record(result, "holder-set-object", () -> { holder.getMethod("setObject", Object.class).invoke(valueHolder, "changed"); return holder.getMethod("get").invoke(valueHolder); });
        record(result, "holder-field", () -> { java.lang.reflect.Field field = holder.getDeclaredField("object"); return Modifier.toString(field.getModifiers()); });

        record(result, "result-serial", () -> { java.lang.reflect.Field field = resultEntity.getDeclaredField("serialVersionUID"); field.setAccessible(true); return field.getLong(null); });
        Object success = resultEntity.getMethod("success").invoke(null);
        Object failure = resultEntity.getMethod("fail", String.class).invoke(null, "bad");
        record(result, "result-success", () -> resultEntity.getMethod("isSucess").invoke(success));
        record(result, "result-success-message", () -> resultEntity.getMethod("getMessage").invoke(success));
        record(result, "result-fail", () -> resultEntity.getMethod("isSucess").invoke(failure));
        record(result, "result-fail-message", () -> resultEntity.getMethod("getMessage").invoke(failure));
        record(result, "result-setters", () -> { resultEntity.getMethod("setSucess", boolean.class).invoke(failure, true); resultEntity.getMethod("setMessage", String.class).invoke(failure, (Object) null); return resultEntity.getMethod("isSucess").invoke(failure) + ":" + resultEntity.getMethod("getMessage").invoke(failure); });
        record(result, "result-constructor", () -> { Constructor<?> constructor = resultEntity.getDeclaredConstructor(); return Modifier.toString(constructor.getModifiers()); });
        record(result, "result-null-failure", () -> { Object entity = resultEntity.getMethod("fail", String.class).invoke(null, (Object) null); return resultEntity.getMethod("isSucess").invoke(entity) + ":" + resultEntity.getMethod("getMessage").invoke(entity); });
        record(result, "result-factory-fresh", () -> success != resultEntity.getMethod("success").invoke(null));
        record(result, "result-serialized", () -> java.util.Base64.getEncoder().encodeToString(serialize(failure)));

        Object tuple = triple.getMethod("of", Object.class, Object.class, Object.class).invoke(null, "left", Integer.valueOf(2), null);
        record(result, "triple-serial", () -> { java.lang.reflect.Field field = triple.getDeclaredField("serialVersionUID"); field.setAccessible(true); return field.getLong(null); });
        record(result, "triple-values", () -> triple.getMethod("getLeft").invoke(tuple) + ":" + triple.getMethod("getMiddle").invoke(tuple) + ":" + triple.getMethod("getRight").invoke(tuple));
        record(result, "triple-setters", () -> { triple.getMethod("setLeft", Object.class).invoke(tuple, "L"); triple.getMethod("setMiddle", Object.class).invoke(tuple, Integer.valueOf(3)); triple.getMethod("setRight", Object.class).invoke(tuple, "R"); return triple.getMethod("getLeft").invoke(tuple) + ":" + triple.getMethod("getMiddle").invoke(tuple) + ":" + triple.getMethod("getRight").invoke(tuple); });
        record(result, "triple-constructor", () -> { Constructor<?>[] constructors = triple.getDeclaredConstructors(); for (Constructor<?> constructor : constructors) if (constructor.getParameterTypes().length == 3) return Modifier.toString(constructor.getModifiers()); return "missing"; });
        record(result, "triple-serialized", () -> java.util.Base64.getEncoder().encodeToString(serialize(tuple)));

        record(result, "tri-consumer-modifiers", () -> Modifier.toString(triConsumer.getModifiers()));
        record(result, "tri-consumer-functional", () -> triConsumer.getAnnotation(FunctionalInterface.class) != null);
        record(result, "tri-consumer-method", () -> triConsumer.getMethod("accept", Object.class, Object.class, Object.class).toGenericString());
        final List<String> accepted = new ArrayList<String>();
        InvocationHandler handler = (proxy, method, arguments) -> { if ("accept".equals(method.getName())) accepted.add(Arrays.toString(arguments)); return null; };
        Object consumer = Proxy.newProxyInstance(loader, new Class<?>[] {triConsumer}, handler);
        triConsumer.getMethod("accept", Object.class, Object.class, Object.class).invoke(consumer, "a", Integer.valueOf(1), null);
        result.add("tri-consumer-invocation=" + accepted);

        for (String annotationName : new String[] {"Blob", "Clob", "NotNull", "ReadOnly", "Timestamp"}) {
            Class<?> annotation = loader.loadClass("com.sunwayworld.framework.data.annotation." + annotationName);
            result.add("annotation=" + annotationName + ":" + annotationSummary(annotation));
        }
        return result;
    }

    private static void crossSerialization(ClassLoader source, ClassLoader target) throws Exception {
        Class<?> sourceResult = source.loadClass("com.sunwayworld.framework.data.ResultEntity");
        Object failure = sourceResult.getMethod("fail", String.class).invoke(null, "failure message");
        Object copy = deserialize(serialize(failure), target);
        if (copy.getClass().getClassLoader() != target || (Boolean) copy.getClass().getMethod("isSucess").invoke(copy)
                || !"failure message".equals(copy.getClass().getMethod("getMessage").invoke(copy)))
            throw new AssertionError("ResultEntity cross-loader serialization lost state");
        Class<?> sourceTriple = source.loadClass("com.sunwayworld.framework.data.Triple");
        Object tuple = sourceTriple.getMethod("of", Object.class, Object.class, Object.class).invoke(null, "left", Integer.valueOf(2), null);
        copy = deserialize(serialize(tuple), target);
        if (copy.getClass().getClassLoader() != target || !"left".equals(copy.getClass().getMethod("getLeft").invoke(copy))
                || !Integer.valueOf(2).equals(copy.getClass().getMethod("getMiddle").invoke(copy))
                || copy.getClass().getMethod("getRight").invoke(copy) != null)
            throw new AssertionError("Triple cross-loader serialization lost state");
    }

    private static String annotationSummary(Class<?> annotation) {
        Retention retention = annotation.getAnnotation(Retention.class);
        Target target = annotation.getAnnotation(Target.class);
        List<String> targets = new ArrayList<String>();
        if (target != null) for (ElementType type : target.value()) targets.add(type.name());
        Collections.sort(targets);
        List<String> methods = new ArrayList<String>();
        for (Method method : annotation.getDeclaredMethods()) methods.add(method.getName() + "=" + String.valueOf(method.getDefaultValue()));
        Collections.sort(methods);
        return "annotation=" + annotation.isAnnotation() + ";documented=" + (annotation.getAnnotation(Documented.class) != null)
                + ";retention=" + (retention == null ? "null" : retention.value().name())
                + ";targets=" + targets + ";methods=" + methods;
    }

    public static void main(String[] args) throws Exception {
        try (URLClassLoader supplier = loader(new File(args[0]), new File(args[2])); URLClassLoader owned = loader(new File(args[1]), new File(args[2]))) {
            List<String> expected = observe(supplier);
            List<String> actual = observe(owned);
            if (!expected.equals(actual)) {
                for (int i = 0; i < Math.min(expected.size(), actual.size()); i++)
                    if (!expected.get(i).equals(actual.get(i))) throw new AssertionError("observation " + i + " supplier=" + expected.get(i) + " owned=" + actual.get(i));
                throw new AssertionError("observation count supplier=" + expected.size() + " owned=" + actual.size());
            }
            crossSerialization(supplier, owned);
            crossSerialization(owned, supplier);
            System.out.println("data leaves differential: PASS (" + expected.size() + " observations)");
            System.out.println("data leaves cross-loader serialization: PASS (4 transfers)");
        }
    }
}
