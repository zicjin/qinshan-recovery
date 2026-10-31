package com.sunwayworld.framework;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/** Declaration and scalar behavior check for the Fastjson deserializer leaf. */
public final class DefaultJsonDeserializerDifferentialTest {
    private static URLClassLoader loader(File primary, File dependencies) throws Exception {
        java.util.List<URL> urls = new java.util.ArrayList<URL>(); urls.add(primary.toURI().toURL());
        for (File file : dependencies.listFiles()) if (file.getName().endsWith(".jar")) urls.add(file.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[0]), null);
    }
    public static void main(String[] args) throws Exception {
        try (URLClassLoader supplier = loader(new File(args[0]), new File(args[1]));
             URLClassLoader owned = loader(new File(args[2]), new File(args[1]))) {
            Class<?> s = supplier.loadClass("com.sunwayworld.framework.json.deserializer.DefaultJsonDeserializer");
            Class<?> o = owned.loadClass("com.sunwayworld.framework.json.deserializer.DefaultJsonDeserializer");
            if (s.getEnumConstants().length != o.getEnumConstants().length) throw new AssertionError("enum constants differ");
            int st = ((Number) s.getMethod("getFastMatchToken").invoke(s.getField("INSTANCE").get(null))).intValue();
            int ot = ((Number) o.getMethod("getFastMatchToken").invoke(o.getField("INSTANCE").get(null))).intValue();
            if (st != ot) throw new AssertionError("fast-match token supplier=" + st + " owned=" + ot);
            System.out.println("default json deserializer differential: PASS (2 observations)");
        }
    }
}
