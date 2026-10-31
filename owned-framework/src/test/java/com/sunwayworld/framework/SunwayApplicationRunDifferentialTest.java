package com.sunwayworld.framework;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/** Runs the real run overloads once through supplier and once through owned classes. */
public final class SunwayApplicationRunDifferentialTest {
    private static void run(List<URL> urls) throws Exception {
        try (URLClassLoader loader = new URLClassLoader(urls.toArray(new URL[urls.size()]), null)) {
            Thread.currentThread().setContextClassLoader(loader);
            Class<?> test = loader.loadClass("com.sunwayworld.framework.SunwayApplicationRunContractTest");
            try {
                test.getMethod("main", String[].class).invoke(null, new Object[] {new String[0]});
            } catch (InvocationTargetException error) {
                throw new AssertionError(error.getCause());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 4) throw new IllegalArgumentException("public-deps test-classes supplier-common owned-classes");
        List<URL> base = new ArrayList<URL>();
        for (File file : new File(args[0]).listFiles()) {
            if (file.getName().endsWith(".jar")) base.add(file.toURI().toURL());
        }
        base.add(new File(args[1]).toURI().toURL());
        List<URL> supplier = new ArrayList<URL>(base);
        List<URL> owned = new ArrayList<URL>(base);
        supplier.add(new File(args[2]).toURI().toURL());
        owned.add(new File(args[3]).toURI().toURL());
        run(supplier);
        run(owned);
        System.out.println("sunway application run differential: PASS");
    }
}
