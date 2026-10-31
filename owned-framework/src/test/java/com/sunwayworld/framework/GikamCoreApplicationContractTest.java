package com.sunwayworld.framework;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** Parentless declaration and owned-context bootstrap test for GikamCoreApplication. */
public final class GikamCoreApplicationContractTest {
    private static URLClassLoader loader(File deps, File framework, File reference, File fixture) throws Exception {
        List<URL> urls = new ArrayList<URL>(); urls.add(fixture.toURI().toURL()); urls.add(framework.toURI().toURL()); if (reference != null) urls.add(reference.toURI().toURL());
        for (File f : Objects.requireNonNull(deps.listFiles())) if (f.getName().endsWith(".jar")) urls.add(f.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[0]), null);
    }
    private static String declarations(Class<?> type) {
        StringBuilder result = new StringBuilder(type.getName()).append('|').append(type.getSuperclass().getName()).append('|').append(Arrays.toString(type.getInterfaces()));
        for (Method method : type.getMethods()) if (method.getDeclaringClass() == type) result.append('|').append(method.toGenericString());
        for (Method method : type.getDeclaredMethods()) if (Modifier.isProtected(method.getModifiers())) result.append('|').append(method.toGenericString());
        return result.toString();
    }
    public static void main(String[] args) throws Exception {
        File deps = new File(args[0]), reference = new File(args[1]), ownedClasses = new File(args[2]), fixture = new File(args[3]);
        try (URLClassLoader supplier = loader(deps, reference, null, fixture); URLClassLoader owned = loader(deps, ownedClasses, null, fixture)) {
            same("declarations", declarations(supplier.loadClass("com.sunwayworld.GikamCoreApplication")), declarations(owned.loadClass("com.sunwayworld.GikamCoreApplication")));
            Class<?> ownedBase = owned.loadClass("com.sunwayworld.GikamCoreApplication");
            Set<String> annotationNames = new HashSet<String>(); for (Annotation annotation : ownedBase.getAnnotations()) annotationNames.add(annotation.annotationType().getName());
            if (!annotationNames.contains(ComponentScan.class.getName()) || !annotationNames.contains(EnableTransactionManagement.class.getName()) || !annotationNames.contains(ConditionalOnProperty.class.getName())) throw new AssertionError("owned application base configuration annotations are missing");
            Class<?> launcher = owned.loadClass("com.sunwayworld.framework.GikamCoreApplicationLaunchFixture");
            Thread.currentThread().setContextClassLoader(owned);
            launcher.getMethod("main", String[].class).invoke(null, new Object[]{new String[0]});
            System.out.println("GikamCoreApplication contract: PASS (declarations, configuration annotations, fixture registration, owned Spring context, security customizer)");
        }
    }
    private static void same(String label, String expected, String actual) { if (!Objects.equals(expected, actual)) throw new AssertionError(label + " supplier=" + expected + " owned=" + actual); }
}
