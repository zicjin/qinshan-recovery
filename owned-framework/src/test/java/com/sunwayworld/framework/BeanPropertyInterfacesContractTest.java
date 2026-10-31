package com.sunwayworld.framework;

import java.io.File;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/** Parentless declaration differential for the bean property listener/interceptor seams. */
public final class BeanPropertyInterfacesContractTest {
    private static URLClassLoader loader(File deps, File framework, File reference) throws Exception {
        List<URL> urls = new ArrayList<URL>(); urls.add(framework.toURI().toURL()); if (reference != null) urls.add(reference.toURI().toURL());
        for (File f : Objects.requireNonNull(deps.listFiles())) if (f.getName().endsWith(".jar")) urls.add(f.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[0]), null);
    }
    private static String declarations(Class<?> type) {
        StringBuilder out = new StringBuilder(type.getName()).append('|').append(Arrays.toString(type.getInterfaces()));
        for (Method method : type.getMethods()) if (method.getDeclaringClass() == type) out.append('|').append(method.toGenericString());
        return out.toString();
    }
    public static void main(String[] args) throws Exception {
        try (URLClassLoader supplierLoader = loader(new File(args[0]), new File(args[1]), null); URLClassLoader ownedLoader = loader(new File(args[0]), new File(args[2]), null)) {
            for (String name : new String[]{"com.sunwayworld.framework.beans.BeanPropertyListener", "com.sunwayworld.framework.beans.BeanPropertyInterceptor"}) {
                String supplier = declarations(supplierLoader.loadClass(name));
                String owned = declarations(ownedLoader.loadClass(name));
                if (!supplier.equals(owned)) throw new AssertionError(name + " supplier=" + supplier + " owned=" + owned);
            }
            System.out.println("BeanProperty interfaces differential: PASS (listener/interceptor declarations)");
        }
    }
}
