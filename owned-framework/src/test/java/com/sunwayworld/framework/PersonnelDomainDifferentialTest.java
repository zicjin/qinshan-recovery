package com.sunwayworld.framework;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

/** Runs the unchanged recovered personnel DTO against separate framework loaders. */
public final class PersonnelDomainDifferentialTest {
    private static URL[] urls(File... entries) throws Exception {
        List<URL> result = new ArrayList<URL>();
        for (File entry : entries) {
            if (entry.isDirectory() && entry.getName().equals("public-deps")) {
                File[] jars = entry.listFiles(); Arrays.sort(jars);
                for (File jar : jars) result.add(jar.toURI().toURL());
            } else result.add(entry.toURI().toURL());
        }
        return result.toArray(new URL[result.size()]);
    }

    private static TreeSet<String> methods(Class<?> type) {
        TreeSet<String> result = new TreeSet<String>();
        for (Method method : type.getDeclaredMethods()) {
            if (!java.lang.reflect.Modifier.isPublic(method.getModifiers())) continue;
            result.add(method.getName() + Arrays.toString(method.getParameterTypes()) + method.getReturnType().getName());
        }
        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static List<String> observe(ClassLoader loader) throws Exception {
        List<String> out = new ArrayList<String>();
        Class<?> bean = loader.loadClass("com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpBean");
        Object employee = bean.newInstance();
        for (String property : new String[]{"Id", "UserName", "CreatedById", "CreatedByName", "CreatedByOrgId", "CreatedByOrgName"}) {
            out.add(property + "-default=" + bean.getMethod("get" + property).invoke(employee));
            bean.getMethod("set" + property, String.class).invoke(employee, "value-" + property);
            out.add(property + "=" + bean.getMethod("get" + property).invoke(employee));
        }
        LocalDateTime now = LocalDateTime.of(2026, 10, 19, 12, 30);
        bean.getMethod("setCreatedTime", LocalDateTime.class).invoke(employee, now);
        out.add("createdTime=" + bean.getMethod("getCreatedTime").invoke(employee));
        Class<?> inherited = loader.loadClass("com.sunwayworld.framework.DomainRecordFixture");
        Object record = inherited.newInstance();
        for (String property : new String[]{"CreatedById", "CreatedByName", "CreatedByOrgId", "CreatedByOrgName"}) {
            out.add("inherited-" + property + "-default=" + inherited.getMethod("get" + property).invoke(record));
            inherited.getMethod("set" + property, String.class).invoke(record, property);
            out.add("inherited-" + property + "=" + inherited.getMethod("get" + property).invoke(record));
        }
        inherited.getMethod("setCreatedTime", LocalDateTime.class).invoke(record, now);
        out.add("inherited-time=" + inherited.getMethod("getCreatedTime").invoke(record));
        Map ext = (Map) bean.getMethod("getExt$").invoke(employee);
        bean.getMethod("setExt$Item", String.class, String.class).invoke(employee, "DepartmentName", "Lab");
        out.add("case-key=" + ext.keySet() + ":" + ext.get("DEPARTMENTNAME"));
        Map<String, String> extra = new LinkedHashMap<String, String>(); extra.put("OrgName", "Root");
        bean.getMethod("setExt$", Map.class).invoke(employee, extra);
        extra.put("OrgName", "changed");
        out.add("merge-alias=" + (ext == extra) + ":" + ext);
        out.add("live-map=" + (ext == bean.getMethod("getExt$").invoke(employee)));
        ext.put("DIRECT", "direct");
        out.add("direct=" + bean.getMethod("getExt$Item", String.class).invoke(employee, "Direct"));
        try { bean.getMethod("setExt$", Map.class).invoke(employee, new Object[]{null}); out.add("null-ext=accepted"); }
        catch (InvocationTargetException e) { out.add("null-ext=" + e.getCause().getClass().getName()); }
        Class<?> mapClass = loader.loadClass("com.sunwayworld.framework.data.CaseInsensitiveLinkedMap");
        Map<String, String> map = (Map<String, String>) mapClass.newInstance();
        map.put("UPPER", "one"); map.putIfAbsent("Upper", "two");
        out.add("put-if-absent=" + map + ":" + map.containsKey("UPPER"));
        out.add("get-default=" + map.getOrDefault("uPPER", "missing"));
        map.replace("UPPER", "one", "three"); map.replace("UPPER", "four");
        map.computeIfAbsent("ABSENT", key -> key); map.computeIfPresent("UPPER", (key,value) -> key + value);
        map.compute("COMPUTE", (key,value) -> key); map.merge("UPPER", "plus", (a,b) -> a+b);
        out.add("java8-mutators=" + map);
        out.add("remove-pair=" + map.remove("UPPER", "upperfourplus"));
        out.add("remove=" + map.remove("ABSENT"));
        map.put(null, "null"); out.add("null-key=" + map.get(null));
        Map ordered = (Map) mapClass.getConstructor(int.class,float.class,boolean.class).newInstance(16,0.75f,true);
        ordered.put("A", "1"); ordered.put("B", "2"); ordered.get("A");
        out.add("access-order=" + ordered.keySet());
        Map source = new LinkedHashMap(); source.put("First", "1"); source.put("Second", "2");
        Map copy = (Map) mapClass.getConstructor(Map.class).newInstance(source);
        out.add("map-copy=" + copy + ":source=" + source);
        for (String operation : new String[]{"constructor", "putAll"}) {
            try {
                if (operation.equals("constructor")) mapClass.getConstructor(Map.class).newInstance(new Object[]{null});
                else mapClass.getMethod("putAll", Map.class).invoke(map, new Object[]{null});
                out.add(operation+"-null=accepted");
            } catch (InvocationTargetException e) { out.add(operation+"-null="+e.getCause().getClass().getName()); }
        }
        Map nestedSource = new LinkedHashMap(); List<String> nested = new ArrayList<String>(Arrays.asList("initial"));
        nestedSource.put("already_lower", nested); nestedSource.put("UPPER", nested);
        Map nestedCopy = (Map) mapClass.getConstructor(Map.class).newInstance(nestedSource);
        out.add("copy-value-alias="+(nestedCopy.get("already_lower")==nested)+":"+(nestedCopy.get("UPPER")==nested));
        for (String annotation : new String[]{"Id", "Table", "Transient"}) {
            Class<?> a = loader.loadClass("com.sunwayworld.framework.data.annotation." + annotation);
            out.add(annotation + "=" + Arrays.toString(a.getDeclaredAnnotations()));
        }
        return out;
    }

    public static void main(String[] args) throws Exception {
        File deps = new File(args[0]), beanClasses = new File(args[1]), ownedClasses = new File(args[2]);
        File testClasses = new File(args[4]);
        try (URLClassLoader supplier = new URLClassLoader(urls(new File(args[3]), beanClasses, testClasses, deps), null);
             URLClassLoader owned = new URLClassLoader(urls(ownedClasses, beanClasses, testClasses, deps), null)) {
            for (String name : new String[]{"BaseData", "AbstractBaseData", "Persistable", "AbstractPersistable", "Insertable", "AbstractInsertable"}) {
                String fqcn = "com.sunwayworld.framework.support.domain." + name;
                if (!methods(supplier.loadClass(fqcn)).equals(methods(owned.loadClass(fqcn)))) throw new AssertionError("method contract: " + name);
            }
            Locale previous = Locale.getDefault();
            try {
                Locale.setDefault(Locale.ENGLISH);
                List<String> expected = observe(supplier), actual = observe(owned);
                if (!expected.equals(actual)) {
                    for (int i=0; i<Math.min(expected.size(), actual.size()); i++) {
                        if (!expected.get(i).equals(actual.get(i))) throw new AssertionError("supplier: " + expected.get(i) + "\nowned: " + actual.get(i));
                    }
                    throw new AssertionError("observation count differs");
                }
                System.out.println("personnel domain differential: PASS (" + expected.size() + " observations)");
            } finally { Locale.setDefault(previous); }
        }
    }
}
