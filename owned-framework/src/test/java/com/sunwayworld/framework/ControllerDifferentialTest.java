package com.sunwayworld.framework;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeSet;

/** Compares selected page/bean contracts in independent, supplier-free owned loaders. */
public final class ControllerDifferentialTest {
    private static final String FRAMEWORK = "com.sunwayworld.framework.";

    private static URLClassLoader loader(File deps, File framework, File tests, File business) throws Exception {
        List<URL> urls = new ArrayList<URL>();
        urls.add(framework.toURI().toURL());
        urls.add(tests.toURI().toURL());
        urls.add(business.toURI().toURL());
        File[] jars = deps.listFiles(); Arrays.sort(jars);
        for (File jar : jars) urls.add(jar.toURI().toURL());
        // Limit both condition scanners to the same controlled candidate universe.
        // Supplier framework classes remain loadable but are not business candidates.
        final List<URL> candidates = Arrays.asList(new File(tests, "com/sunwayworld").toURI().toURL(),
                new File(business, "com/sunwayworld").toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[urls.size()]), null) {
            @Override public Enumeration<URL> getResources(String name) throws java.io.IOException {
                return name.startsWith("com/sunwayworld") ? Collections.enumeration(candidates) : super.getResources(name);
            }
        };
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static List<String> observe(ClassLoader loader) throws Exception {
        List<String> result = new ArrayList<String>();
        Class<?> condition = loader.loadClass(FRAMEWORK + "spring.annotation.GikamCondition");
        Constructor<?> constructor = condition.getDeclaredConstructor(); constructor.setAccessible(true);
        Object instance = constructor.newInstance();
        Class<?> contextType = loader.loadClass("org.springframework.context.annotation.ConditionContext");
        Class<?> metadataType = loader.loadClass("org.springframework.core.type.AnnotatedTypeMetadata");
        Method matches = condition.getMethod("matches", contextType, metadataType); matches.setAccessible(true);
        Class<?> environment = loader.loadClass("org.springframework.core.env.StandardEnvironment");
        Class<?> annotations = loader.loadClass("org.springframework.core.type.AnnotationMetadata");
        Method introspect = annotations.getMethod("introspect", Class.class);
        for (String[] profiles : new String[][]{ {}, {"one"}, {"one", "two"}, {"default"} }) {
            Object env = environment.newInstance();
            environment.getMethod("setActiveProfiles", String[].class).invoke(env, (Object) profiles);
            Object context = java.lang.reflect.Proxy.newProxyInstance(loader, new Class<?>[]{contextType},
                    (proxy, method, args) -> method.getName().equals("getEnvironment") ? env : null);
            for (String name : new String[]{"BaseController", "OverrideController", "ProfileController", "Plain", "MetaOnly", "Rest", "ComponentBean", "ServiceBean", "RepositoryBean", "LiteralNegation", "EmptyProfiles", "NameMapper", "InheritedOnly"}) {
                Class<?> type = loader.loadClass(FRAMEWORK + "GikamConditionTest$" + name);
                result.add(Arrays.toString(profiles) + ":" + name + "=" + matches.invoke(instance, context, introspect.invoke(null, type)));
            }
            Class<?> actual = loader.loadClass("com.sunwayworld.emc.module.mdm.emp.emp.controller.impl.EmcEmpControllerImpl");
            result.add(Arrays.toString(profiles) + ":actual=" + matches.invoke(instance, context, introspect.invoke(null, actual)));
        }
        for (String name : new String[]{"spring.annotation.GikamBean", "log.annotation.Log", "log.annotation.LogModule", "controller.GenericChoosePageController", "controller.GenericDetailPageController", "controller.GenericEditListPageController"}) {
            Class<?> type = loader.loadClass(FRAMEWORK + name);
            TreeSet<String> metadata = new TreeSet<String>();
            for (Annotation annotation : type.getDeclaredAnnotations()) metadata.add(annotation.toString());
            result.add(name + "=" + metadata);
            for (Method method : type.getDeclaredMethods()) {
                TreeSet<String> values = new TreeSet<String>();
                for (Annotation annotation : method.getDeclaredAnnotations()) values.add(annotation.toString());
                result.add(name + "." + method.getName() + "=" + values + ":default=" + method.getDefaultValue());
            }
        }
        Class<?> logType = loader.loadClass(FRAMEWORK + "log.annotation.LogType");
        result.add("logTypes=" + Arrays.toString(logType.getEnumConstants()));
        Class<?> actual = loader.loadClass("com.sunwayworld.emc.module.mdm.emp.emp.controller.impl.EmcEmpControllerImpl");
        Object controller = actual.newInstance();
        try { actual.getMethod("simpleChoosePage").invoke(controller); throw new AssertionError("default method returned"); }
        catch (java.lang.reflect.InvocationTargetException expected) { result.add("simpleChoosePage=" + expected.getCause().getClass().getName()); }
        Collections.sort(result);
        return result;
    }

    public static void main(String[] args) throws Exception {
        File deps = new File(args[0]), owned = new File(args[1]), tests = new File(args[2]), business = new File(args[3]), supplier = new File(args[4]);
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        try (URLClassLoader reference = loader(deps, supplier, tests, business);
             URLClassLoader replacement = loader(deps, owned, tests, business)) {
            Thread.currentThread().setContextClassLoader(reference);
            List<String> expected = observe(reference);
            Thread.currentThread().setContextClassLoader(replacement);
            List<String> actual = observe(replacement);
            if (!expected.equals(actual)) {
                List<String> missing = new ArrayList<String>(expected); missing.removeAll(actual);
                List<String> extra = new ArrayList<String>(actual); extra.removeAll(expected);
                throw new AssertionError("controller observations differ: reference=" + missing + " owned=" + extra);
            }
            System.out.println("controller differential: PASS (" + actual.size() + " observations)");
        } finally { Thread.currentThread().setContextClassLoader(previous); }
    }
}
