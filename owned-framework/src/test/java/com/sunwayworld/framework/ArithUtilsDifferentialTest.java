package com.sunwayworld.framework;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Differential observations for the dependency-closed arithmetic leaf. */
public final class ArithUtilsDifferentialTest {
    private static URLClassLoader loader(File supplier, File ownedClasses, File dependencies) throws Exception {
        List<URL> urls = new ArrayList<URL>();
        urls.add((supplier == null ? ownedClasses : supplier).toURI().toURL());
        if (supplier != null) urls.add(ownedClasses.toURI().toURL());
        for (File file : dependencies.listFiles()) if (file.getName().endsWith(".jar")) urls.add(file.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[0]), null);
    }

    private static Object call(Class<?> type, String name, Class<?>[] signature, Object... args) throws Exception {
        return type.getMethod(name, signature).invoke(null, args);
    }

    private static void equalNumber(Object supplier, Object owned, String label) {
        double left = ((Number) supplier).doubleValue();
        double right = ((Number) owned).doubleValue();
        if (Double.doubleToLongBits(left) != Double.doubleToLongBits(right))
            throw new AssertionError(label + " supplier=" + left + " owned=" + right);
    }

    private static void compareFailure(Class<?> supplier, Class<?> owned) throws Exception {
        Throwable left = null, right = null;
        try { call(supplier, "div", new Class<?>[]{double.class, double.class, int.class}, 1D, 0D, 16); }
        catch (InvocationTargetException e) { left = e.getCause(); }
        try { call(owned, "div", new Class<?>[]{double.class, double.class, int.class}, 1D, 0D, 16); }
        catch (InvocationTargetException e) { right = e.getCause(); }
        if (left == null || right == null || !left.getClass().getName().equals(right.getClass().getName())
                || !String.valueOf(left.getMessage()).equals(String.valueOf(right.getMessage())))
            throw new AssertionError("divide-zero supplier=" + left + " owned=" + right);
    }

    public static void main(String[] args) throws Exception {
        File supplierJar = new File(args[0]);
        File dependencies = new File(args[1]);
        File ownedClasses = new File(args[2]);
        try (URLClassLoader supplierLoader = loader(supplierJar, ownedClasses, dependencies);
             URLClassLoader ownedLoader = loader(null, ownedClasses, dependencies)) {
            Class<?> supplier = supplierLoader.loadClass("com.sunwayworld.framework.utils.ArithUtils");
            Class<?> owned = ownedLoader.loadClass("com.sunwayworld.framework.utils.ArithUtils");
            int count = 0;
            equalNumber(call(supplier, "add", new Class<?>[]{double.class, double.class}, .1D, .2D), call(owned, "add", new Class<?>[]{double.class, double.class}, .1D, .2D), "add double"); count++;
            equalNumber(call(supplier, "add", new Class<?>[]{String.class, String.class}, " 1.20 ", "2.30"), call(owned, "add", new Class<?>[]{String.class, String.class}, " 1.20 ", "2.30"), "add string"); count++;
            equalNumber(call(supplier, "add", new Class<?>[]{List.class}, Arrays.asList("1.2", "2.3", "3.4")), call(owned, "add", new Class<?>[]{List.class}, Arrays.asList("1.2", "2.3", "3.4")), "add list"); count++;
            equalNumber(call(supplier, "sub", new Class<?>[]{double.class, double.class}, 7.5D, 2.25D), call(owned, "sub", new Class<?>[]{double.class, double.class}, 7.5D, 2.25D), "sub"); count++;
            equalNumber(call(supplier, "mul", new Class<?>[]{List.class}, Arrays.asList("1.2", "2.5")), call(owned, "mul", new Class<?>[]{List.class}, Arrays.asList("1.2", "2.5")), "mul list"); count++;
            equalNumber(call(supplier, "div", new Class<?>[]{String.class, String.class}, "10", "3"), call(owned, "div", new Class<?>[]{String.class, String.class}, "10", "3"), "div string"); count++;
            equalNumber(call(supplier, "sqrt", new Class<?>[]{double.class, double.class}, 9D, 3D), call(owned, "sqrt", new Class<?>[]{double.class, double.class}, 9D, 3D), "sqrt"); count++;
            equalNumber(call(supplier, "pow", new Class<?>[]{double.class, double.class}, 2D, 3D), call(owned, "pow", new Class<?>[]{double.class, double.class}, 2D, 3D), "pow"); count++;
            equalNumber(call(supplier, "round", new Class<?>[]{String.class, int.class}, "1.235", 2), call(owned, "round", new Class<?>[]{String.class, int.class}, "1.235", 2), "round string"); count++;
            for (String expression : new String[]{"1+2*3", "(1+2)*3", "-2+5", "2p3", "9s3", "1e+3+2"}) {
                equalNumber(call(supplier, "eval", new Class<?>[]{String.class}, expression), call(owned, "eval", new Class<?>[]{String.class}, expression), "eval " + expression); count++;
            }
            compareFailure(supplier, owned); count++;
            System.out.println("arith utils differential: PASS (" + count + " observations)");
        }
    }
}
