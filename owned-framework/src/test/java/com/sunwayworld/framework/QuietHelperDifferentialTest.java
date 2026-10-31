package com.sunwayworld.framework;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/** Parentless behavioral differential for QuietHelper's two call seams. */
public final class QuietHelperDifferentialTest {
    private static URLClassLoader loader(File deps, File framework, File reference) throws Exception {
        List<URL> urls=new ArrayList<URL>(); if(reference!=null)urls.add(reference.toURI().toURL()); urls.add(framework.toURI().toURL()); for(File f:deps.listFiles())if(f.getName().endsWith(".jar"))urls.add(f.toURI().toURL()); return new URLClassLoader(urls.toArray(new URL[0]),null);
    }
    public static void main(String[] args) throws Exception {
        try(URLClassLoader supplier=loader(new File(args[0]),new File(args[1]),new File(args[2])); URLClassLoader owned=loader(new File(args[0]),new File(args[3]),null)) {
            Class<?> s=supplier.loadClass("com.sunwayworld.framework.exception.QuietHelper"), o=owned.loadClass("com.sunwayworld.framework.exception.QuietHelper");
            Object[] values=new Object[]{s.getMethod("call",java.util.function.Supplier.class).invoke(null,(java.util.function.Supplier<String>)()->"ok"),o.getMethod("call",java.util.function.Supplier.class).invoke(null,(java.util.function.Supplier<String>)()->"ok")};
            if(!Objects.equals(values[0],values[1]))throw new AssertionError("supplier success");
            Object[] failures=new Object[]{s.getMethod("call",java.util.function.Supplier.class).invoke(null,(java.util.function.Supplier<String>)()->{throw new IllegalStateException("x");}),o.getMethod("call",java.util.function.Supplier.class).invoke(null,(java.util.function.Supplier<String>)()->{throw new IllegalStateException("x");})};
            if(failures[0]!=null||failures[1]!=null)throw new AssertionError("supplier failure suppression");
            s.getMethod("call",Runnable.class).invoke(null,(Runnable)()->{throw new IllegalArgumentException("x");}); o.getMethod("call",Runnable.class).invoke(null,(Runnable)()->{throw new IllegalArgumentException("x");});
            System.out.println("QuietHelper differential: PASS (supplier success/failure and runnable suppression)");
        }
    }
}
