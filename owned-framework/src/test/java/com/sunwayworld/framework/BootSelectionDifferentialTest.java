package com.sunwayworld.framework;
import java.io.File;import java.net.*;import java.lang.reflect.*;import java.util.*;
/** Real Spring registration in separate supplier and owned classloaders. */
public final class BootSelectionDifferentialTest {
 private static List<String> observe(URL[] urls)throws Exception{try(URLClassLoader l=new URLClassLoader(urls,null)){ Thread.currentThread().setContextClassLoader(l);
 Class<?> test=l.loadClass("com.sunwayworld.framework.SunwayApplicationSelectionTest"),cache=l.loadClass("com.sunwayworld.framework.cache.memory.MemoryCacheManager"),ctx=l.loadClass("org.springframework.context.annotation.AnnotationConfigApplicationContext");
 Class<?> a=l.loadClass("com.sunwayworld.framework.SunwayApplicationSelectionTest$Selected"),b=l.loadClass("com.sunwayworld.framework.SunwayApplicationSelectionTest$Other");
 List<String> out=new ArrayList<String>();for(Class<?> chosen:new Class<?>[]{a,b}){cache.getMethod("put",String.class,Object.class).invoke(null,"SunwayMainApplicationClass",chosen);Object c=ctx.newInstance();ctx.getMethod("register",Class[].class).invoke(c,new Object[]{new Class<?>[]{a,b}});ctx.getMethod("refresh").invoke(c);out.add(chosen.getSimpleName()+":"+ctx.getMethod("containsBean",String.class).invoke(c,"selected")+":"+ctx.getMethod("containsBean",String.class).invoke(c,"other"));ctx.getMethod("close").invoke(c);}return out;}}
 public static void main(String[]a)throws Exception{List<URL> deps=new ArrayList<URL>();for(File f:new File(a[0]).listFiles())if(f.getName().endsWith(".jar"))deps.add(f.toURI().toURL());deps.add(new File(a[1]).toURI().toURL());List<URL>s=new ArrayList<URL>(deps),o=new ArrayList<URL>(deps);s.add(new File(a[2]).toURI().toURL());o.add(new File(a[3]).toURI().toURL());List<String>x=observe(s.toArray(new URL[0])),y=observe(o.toArray(new URL[0]));if(!x.equals(y))throw new AssertionError(x+" != "+y);System.out.println("bootstrap selection differential: PASS "+x);}
}
