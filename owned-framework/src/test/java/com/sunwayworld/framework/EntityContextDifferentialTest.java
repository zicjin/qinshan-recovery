package com.sunwayworld.framework;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import java.io.File;
import java.io.ObjectStreamClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/** Parentless supplier/owned differential for the complete entity metadata leaf. */
public final class EntityContextDifferentialTest {
    /** Loaded independently by each parentless framework loader. */
    public static final class Fixture extends AbstractBaseData {
        private static final long serialVersionUID = 1L;
        private String name = "initial";
        private Integer number;
        public String getName() { return name; }
        public void setName(String value) { name = value; }
        public Integer getNumber() { return number; }
        public void setNumber(Integer value) { number = value; }
    }

    private static URLClassLoader loader(File deps, File framework, File testClasses) throws Exception {
        List<URL> urls = new ArrayList<URL>();
        urls.add(framework.toURI().toURL());
        urls.add(testClasses.toURI().toURL());
        File[] jars = deps.listFiles();
        if (jars != null) {
            Arrays.sort(jars, Comparator.comparing(File::getName));
            for (File f : jars) if (f.getName().endsWith(".jar")) urls.add(f.toURI().toURL());
        }
        return new URLClassLoader(urls.toArray(new URL[urls.size()]), null);
    }

    private static String value(Object value) {
        if (value == null) return "null";
        if (value instanceof Class) return ((Class<?>) value).getName();
        if (value instanceof List) return "list:" + ((List<?>) value).size() + ":" + value.getClass().getName();
        if (value instanceof Map) return "map:" + value;
        return String.valueOf(value);
    }

    private static Object invoke(Object target, String method, Class<?>[] signature, Object... args) throws Exception {
        return target.getClass().getMethod(method, signature).invoke(target, args);
    }

    private static List<String> declarations(Class<?> type) {
        List<String> result = new ArrayList<String>();
        for (Method method : type.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers()))
                result.add(method.getName() + Arrays.toString(method.getParameterTypes()) + ":" + method.getReturnType().getName());
        }
        for (java.lang.reflect.Field field : type.getFields())
            result.add("field:" + field.getName() + ":" + field.getType().getName() + ":" + field.getModifiers());
        java.util.Collections.sort(result);
        return result;
    }

    private static String descriptor(Object column) {
        try {
            Object descriptor = invoke(column, "getPropertyDescriptor", new Class<?>[0]);
            if (descriptor == null) return "null";
            return call(descriptor, "getName", new Class<?>[0]) + "|"
                    + call(descriptor, "getDeclaringType", new Class<?>[0]) + "|"
                    + call(descriptor, "getPropertyType", new Class<?>[0]) + "|"
                    + call(descriptor, "isExt$BeanProperty", new Class<?>[0]);
        } catch (InvocationTargetException e) {
            return "ERROR:" + e.getCause().getClass().getName() + ":" + e.getCause().getMessage();
        } catch (Exception e) { throw new AssertionError(e); }
    }

    private static String call(Object target, String method, Class<?>[] signature, Object... args) {
        try {
            Object result = invoke(target, method, signature, args);
            return "OK:" + value(result);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            String result = "ERROR:" + cause.getClass().getName() + ":" + String.valueOf(cause.getMessage());
            if ("getValue".equals(method) && args.length == 1 && "not-a-bean".equals(args[0])
                    && !(cause instanceof ClassCastException)) {
                StackTraceElement origin = cause.getStackTrace()[0];
                result += "|origin=" + origin.getClassName() + "." + origin.getMethodName();
                if (cause.getCause() != null) result += "|cause=" + cause.getCause().getClass().getName();
            }
            return result;
        } catch (Throwable e) {
            return "ERROR:" + e.getClass().getName() + ":" + String.valueOf(e.getMessage());
        }
    }

    private static Object newInstance(Class<?> type) throws Exception { return type.newInstance(); }

    private static List<String> observe(ClassLoader loader) throws Exception {
        Class<?> contextType = loader.loadClass("com.sunwayworld.framework.database.context.EntityContext");
        Class<?> columnType = loader.loadClass("com.sunwayworld.framework.database.context.EntityColumnContext");
        Class<?> fixtureType = loader.loadClass("com.sunwayworld.framework.EntityContextDifferentialTest$Fixture");
        List<String> out = new ArrayList<String>();
        out.add("context-public=" + declarations(contextType));
        out.add("column-public=" + declarations(columnType));
        out.add("fixture-base=" + fixtureType.getSuperclass().getName() + ":" + (fixtureType.getSuperclass().getClassLoader() == loader));
        out.add("context-uid=" + ObjectStreamClass.lookup(contextType).getSerialVersionUID());
        out.add("column-uid=" + ObjectStreamClass.lookup(columnType).getSerialVersionUID());

        Object context = newInstance(contextType);
        Object id = newInstance(columnType);
        Object version = newInstance(columnType);
        out.add("context-defaults=" + call(context, "getTableName", new Class<?>[0]) + "|"
                + call(context, "getIdContext", new Class<?>[0]) + "|"
                + call(context, "getVersionContext", new Class<?>[0]) + "|"
                + call(context, "getColumnContextList", new Class<?>[0]));
        out.add("number-without-id=" + call(context, "isNumberId", new Class<?>[0]));
        invoke(context, "setTableName", new Class<?>[]{String.class}, "T_EMP");
        invoke(context, "setIdContext", new Class<?>[]{columnType}, id);
        invoke(context, "setVersionContext", new Class<?>[]{columnType}, version);
        List<Object> columns = new ArrayList<Object>(); columns.add(id); columns.add(version);
        invoke(context, "setColumnContextList", new Class<?>[]{List.class}, columns);
        out.add("context-values=" + call(context, "getTableName", new Class<?>[0]) + "|"
                + (invoke(context, "getIdContext", new Class<?>[0]) == id) + "|"
                + (invoke(context, "getVersionContext", new Class<?>[0]) == version) + "|"
                + call(context, "getColumnContextList", new Class<?>[0]));

        out.add("columns-same-reference=" + (invoke(context, "getColumnContextList", new Class<?>[0]) == columns));
        columns.remove(1);
        out.add("columns-live=" + call(context, "getColumnContextList", new Class<?>[0]));
        for (String property : new String[]{"TableName", "IdContext", "VersionContext", "ColumnContextList"}) {
            Class<?> type = property.equals("TableName") ? String.class : property.equals("ColumnContextList") ? List.class : columnType;
            invoke(context, "set" + property, new Class<?>[]{type}, (Object) null);
            out.add("context-reset-" + property + "=" + call(context, "get" + property, new Class<?>[0]));
        }
        invoke(context, "setIdContext", new Class<?>[]{columnType}, id);
        for (Class<?> type : new Class<?>[]{Integer.class, int.class, Number.class, Long.class, String.class, null}) {
            invoke(id, "setType", new Class<?>[]{Class.class}, type);
            out.add("number-" + (type == null ? "null" : type.getName()) + "=" + call(context, "isNumberId", new Class<?>[0]));
        }
        invoke(context, "setIdContext", new Class<?>[]{columnType}, (Object) null);
        out.add("number-null-context=" + call(context, "isNumberId", new Class<?>[0]));

        out.add("column-defaults=" + call(id, "getColumnName", new Class<?>[0]) + "|"
                + call(id, "getEntityClass", new Class<?>[0]) + "|"
                + call(id, "getType", new Class<?>[0]) + "|"
                + call(id, "isClob", new Class<?>[0]) + "|"
                + call(id, "isBlob", new Class<?>[0]) + "|"
                + call(id, "isTimestamp", new Class<?>[0]));
        invoke(id, "setColumnName", new Class<?>[]{String.class}, "name");
        invoke(id, "setEntityClass", new Class<?>[]{Class.class}, fixtureType);
        invoke(id, "setType", new Class<?>[]{Class.class}, Integer.class);
        invoke(id, "setClob", new Class<?>[]{boolean.class}, true);
        invoke(id, "setBlob", new Class<?>[]{boolean.class}, true);
        invoke(id, "setTimestamp", new Class<?>[]{boolean.class}, true);
        out.add("column-values=" + call(id, "getColumnName", new Class<?>[0]) + "|"
                + call(id, "getEntityClass", new Class<?>[0]) + "|"
                + call(id, "getType", new Class<?>[0]) + "|"
                + call(id, "isClob", new Class<?>[0]) + "|"
                + call(id, "isBlob", new Class<?>[0]) + "|"
                + call(id, "isTimestamp", new Class<?>[0]));

        Object fixture = newInstance(fixtureType);
        Method setName = fixtureType.getMethod("setName", String.class);
        setName.invoke(fixture, "Alice");
        for (String column : new String[]{"name", "NAME", "missing", "ext$", null}) {
            invoke(id, "setColumnName", new Class<?>[]{String.class}, column);
            out.add("descriptor-" + String.valueOf(column) + "=" + descriptor(id));
            out.add("value-" + String.valueOf(column) + "=" + call(id, "getValue", new Class<?>[]{Object.class}, fixture));
        }
        invoke(id, "setColumnName", new Class<?>[]{String.class}, "missing");
        fixtureType.getMethod("setExt$Item", String.class, String.class).invoke(fixture, "missing", "extension-value");
        out.add("extension-value=" + call(id, "getValue", new Class<?>[]{Object.class}, fixture));
        out.add("extension-unknown=" + call(id, "getValue", new Class<?>[]{Object.class}, newInstance(fixtureType)));
        invoke(id, "setColumnName", new Class<?>[]{String.class}, "number");
        out.add("ordinary-null-property=" + call(id, "getValue", new Class<?>[]{Object.class}, fixture));
        fixtureType.getMethod("setNumber", Integer.class).invoke(fixture, 42);
        out.add("ordinary-number=" + call(id, "getValue", new Class<?>[]{Object.class}, fixture));
        invoke(id, "setColumnName", new Class<?>[]{String.class}, "name");
        out.add("ordinary-null=" + call(id, "getValue", new Class<?>[]{Object.class}, (Object) null));
        out.add("ordinary-wrong-type=" + call(id, "getValue", new Class<?>[]{Object.class}, "not-a-bean"));
        invoke(id, "setColumnName", new Class<?>[]{String.class}, "missing");
        out.add("extension-null=" + call(id, "getValue", new Class<?>[]{Object.class}, (Object) null));
        out.add("extension-wrong-type=" + call(id, "getValue", new Class<?>[]{Object.class}, "not-a-bean"));
        invoke(id, "setEntityClass", new Class<?>[]{Class.class}, (Object) null);
        out.add("null-entity-descriptor=" + call(id, "getPropertyDescriptor", new Class<?>[0]));
        out.add("null-entity-value=" + call(id, "getValue", new Class<?>[]{Object.class}, fixture));
        for (String property : new String[]{"ColumnName", "EntityClass", "Type", "Clob", "Blob", "Timestamp"}) {
            boolean flag = property.equals("Clob") || property.equals("Blob") || property.equals("Timestamp");
            Class<?> type = flag ? boolean.class : property.equals("ColumnName") ? String.class : Class.class;
            invoke(id, "set" + property, new Class<?>[]{type}, flag ? Boolean.FALSE : null);
            out.add("column-reset-" + property + "=" + call(id, (flag ? "is" : "get") + property, new Class<?>[0]));
        }
        invoke(id, "setEntityClass", new Class<?>[]{Class.class}, String.class);
        invoke(id, "setColumnName", new Class<?>[]{String.class}, "missing");
        out.add("no-extension-descriptor=" + descriptor(id));
        out.add("no-extension-value=" + call(id, "getValue", new Class<?>[]{Object.class}, "text"));
        return out;
    }

    public static void main(String[] args) throws Exception {
        File supplierDeps = new File(args[0]), supplierJar = new File(args[1]);
        File ownedDeps = new File(args[2]), ownedClasses = new File(args[3]), testClasses = new File(args[4]);
        try (URLClassLoader supplier = loader(supplierDeps, supplierJar, testClasses);
             URLClassLoader owned = loader(ownedDeps, ownedClasses, testClasses)) {
            for (String name : new String[]{"database.context.EntityContext", "database.context.EntityColumnContext",
                    "support.domain.AbstractBaseData", "beans.BeanPropertyHelper", "beans.BeanPropertyDescriptor"}) {
                String fqcn = "com.sunwayworld.framework." + name;
                if (supplier.loadClass(fqcn).getClassLoader() != supplier || owned.loadClass(fqcn).getClassLoader() != owned)
                    throw new AssertionError("framework loader fallback: " + fqcn);
                if (!supplier.loadClass(fqcn).getProtectionDomain().getCodeSource().getLocation().equals(supplierJar.toURI().toURL())
                        || !owned.loadClass(fqcn).getProtectionDomain().getCodeSource().getLocation().equals(ownedClasses.toURI().toURL()))
                    throw new AssertionError("framework artifact fallback: " + fqcn);
            }
            List<String> expected = observe(supplier), actual = observe(owned);
            if (expected.size() != actual.size()) throw new AssertionError("observation count differs");
            int coldErrorBoundary = 0;
            for (int i = 0; i < expected.size(); i++) {
                if (expected.get(i).equals(actual.get(i))) continue;
                if (expected.get(i).equals("ordinary-wrong-type=ERROR:java.lang.NullPointerException:null"
                        + "|origin=com.sunwayworld.framework.i18n.I18nHelper.getMessage")
                        && actual.get(i).equals("ordinary-wrong-type=ERROR:com.sunwayworld.framework.exception.core.ApplicationRuntimeException:"
                        + "object is not an instance of declaring class|origin=com.sunwayworld.framework.utils.ReflectionUtils.invokeMethod"
                        + "|cause=java.lang.IllegalArgumentException")) {
                    coldErrorBoundary++;
                    continue;
                }
                throw new AssertionError("supplier=" + expected.get(i) + "\nowned=" + actual.get(i));
            }
            if (coldErrorBoundary != 1) throw new AssertionError("unexpected cold error boundary count=" + coldErrorBoundary);
            Class<?> ownedContext = owned.loadClass("com.sunwayworld.framework.database.context.EntityContext");
            Class<?> ownedColumn = owned.loadClass("com.sunwayworld.framework.database.context.EntityColumnContext");
            if (ownedContext.getClassLoader() != owned || ownedColumn.getClassLoader() != owned)
                throw new AssertionError("owned classloader fallback");
            System.out.println("entity context differential: PASS (" + expected.size() + " observations; 1 supplier cold-context error boundary recorded)");
        }
    }
}
