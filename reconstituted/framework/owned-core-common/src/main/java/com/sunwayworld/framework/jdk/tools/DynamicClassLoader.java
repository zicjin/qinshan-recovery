/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.jdk.tools;

import com.sunwayworld.framework.jdk.tools.CompileMultipleResult;
import com.sunwayworld.framework.jdk.tools.JavaCompilerManager;
import com.sunwayworld.framework.jdk.tools.LoadMultipleResult;
import com.sunwayworld.framework.utils.StringUtils;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.boot.system.ApplicationHome;

public class DynamicClassLoader
extends URLClassLoader {
    private final Map<String, Class<?>> loadedClassMap = new ConcurrentHashMap();
    private static DynamicClassLoader instance;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static DynamicClassLoader getInstance() {
        if (instance != null) return instance;
        Class<DynamicClassLoader> clazz = DynamicClassLoader.class;
        synchronized (DynamicClassLoader.class) {
            if (instance != null) return instance;
            instance = new DynamicClassLoader();
            // ** MonitorExit[var0] (shouldn't be in output)
            return instance;
        }
    }

    private DynamicClassLoader() {
        super(new URL[0], DynamicClassLoader.class.getClassLoader());
        Thread.currentThread().setContextClassLoader(this);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> loaded = this.loadedClassMap.get(name);
        return loaded != null ? loaded : super.findClass(name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public LoadMultipleResult loadSourceCode(Map<String, String> sourceMap) {
        LoadMultipleResult result = new LoadMultipleResult();
        HashMap classMap = new HashMap();
        HashMap<String, String> loadSourceMap = new HashMap<String, String>();
        sourceMap.forEach((k, v) -> {
            if (this.loadedClassMap.containsKey(k)) {
                classMap.put((String)k, this.loadedClassMap.get(k));
            } else {
                loadSourceMap.put((String)k, (String)v);
            }
        });
        Map<String, Class<?>> map = this.loadedClassMap;
        synchronized (map) {
            if (!loadSourceMap.isEmpty()) {
                CompileMultipleResult compileResult = JavaCompilerManager.compile(loadSourceMap);
                if (compileResult.getErrorMessageMap() != null) {
                    result.setErrorMessageMap(compileResult.getErrorMessageMap());
                } else {
                    compileResult.getBytesMap().forEach((k, v) -> {
                        Class<?> clazz = this.defineClass((String)k, (byte[])v, 0, ((byte[])v).length);
                        this.loadedClassMap.put((String)k, clazz);
                        classMap.put((String)k, clazz);
                    });
                }
            }
        }
        if (result.getErrorMessageMap() == null) {
            result.setClassMap(classMap);
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Class<?> loadSourceCode(String sourceCode) {
        String className = JavaCompilerManager.getClassName(sourceCode);
        if (this.loadedClassMap.containsKey(className)) {
            return this.loadedClassMap.get(className);
        }
        Map<String, Class<?>> map = this.loadedClassMap;
        synchronized (map) {
            if (this.loadedClassMap.containsKey(className)) {
                return this.loadedClassMap.get(className);
            }
            Map<String, byte[]> map2 = JavaCompilerManager.compile(sourceCode);
            map2.forEach((key, bytes) -> {
                Class<?> clazz = this.defineClass((String)key, (byte[])bytes, 0, ((byte[])bytes).length);
                this.loadedClassMap.put((String)key, clazz);
            });
            return this.loadedClassMap.get(className);
        }
    }

    public static String getSourcePath() {
        return new ApplicationHome(DynamicClassLoader.class).getSource().getPath();
    }

    public static boolean isJar() {
        return DynamicClassLoader.getSourcePath().endsWith(".jar");
    }

    public static boolean isServerStartUp() {
        return DynamicClassLoader.getSourcePath().contains("BOOT-INF");
    }

    public static Class<?> getClass(String className) {
        Map.Entry stringClassEntry = DynamicClassLoader.getInstance().loadedClassMap.entrySet().stream().filter(r -> !StringUtils.contains((String)r.getKey(), "$") && StringUtils.contains((String)r.getKey(), className + "v")).findFirst().orElse(null);
        if (stringClassEntry != null) {
            return (Class)stringClassEntry.getValue();
        }
        return null;
    }

    public static Class<?> getUseClass(String className) {
        Class<?> aClass = DynamicClassLoader.getInstance().loadedClassMap.get(className);
        return aClass;
    }

    public static List<Map.Entry<String, Class<?>>> getClassList(String className) {
        List<Map.Entry<String, Class<?>>> classList = DynamicClassLoader.getInstance().loadedClassMap.entrySet().stream().filter(r -> !StringUtils.contains((String)r.getKey(), "$") && StringUtils.contains((String)r.getKey(), className + "v")).collect(Collectors.toList());
        return classList;
    }

    public static boolean isLcdpUtilsClass(String className) {
        List classList = DynamicClassLoader.getInstance().loadedClassMap.entrySet().stream().filter(r -> StringUtils.contains((String)r.getKey(), className + "v")).collect(Collectors.toList());
        return classList.isEmpty();
    }

    public static Class<?> getClassByClassName(String className) {
        return DynamicClassLoader.getInstance().loadedClassMap.get(className);
    }
}
