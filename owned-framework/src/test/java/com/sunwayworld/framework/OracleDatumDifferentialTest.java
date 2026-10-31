package com.sunwayworld.framework;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/** Both sides load their own fixture and public Oracle driver under parentless loaders. */
public final class OracleDatumDifferentialTest {
    private static URLClassLoader loader(File implementation, File tests, File dependencies) throws Exception {
        List<URL> urls = new ArrayList<URL>();
        urls.add(implementation.toURI().toURL()); urls.add(tests.toURI().toURL());
        for (File jar : dependencies.listFiles()) if (jar.getName().endsWith(".jar")) urls.add(jar.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[0]), null);
    }
    public static void main(String[] args) throws Exception {
        try (URLClassLoader supplier = loader(new File(args[0]), new File(args[2]), new File(args[3]));
             URLClassLoader owned = loader(new File(args[1]), new File(args[2]), new File(args[3]))) {
            List<?> expected = (List<?>) supplier.loadClass("com.sunwayworld.framework.OracleDatumFixture").getMethod("observe").invoke(null);
            List<?> actual = (List<?>) owned.loadClass("com.sunwayworld.framework.OracleDatumFixture").getMethod("observe").invoke(null);
            if (expected.size() != actual.size()) throw new AssertionError("observation count");
            for (int i=0; i<expected.size(); i++) if (!expected.get(i).equals(actual.get(i)))
                throw new AssertionError("observation "+i+" supplier="+expected.get(i)+" owned="+actual.get(i));
            System.out.println("Oracle datum differential: PASS ("+actual.size()+" observations)");
        }
    }
}
