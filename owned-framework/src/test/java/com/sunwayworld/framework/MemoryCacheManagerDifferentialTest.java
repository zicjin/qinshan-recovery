package com.sunwayworld.framework;
import java.io.File;import java.net.*;import java.lang.reflect.*;import java.util.*;import java.util.concurrent.atomic.AtomicInteger;import java.util.function.Supplier;
/** Tests only public cache operations; never loads application configuration. */
public final class MemoryCacheManagerDifferentialTest {
 private static List<String> observe(ClassLoader loader)throws Exception {
  Class<?> c=loader.loadClass("com.sunwayworld.framework.cache.memory.MemoryCacheManager");
  Method get=c.getMethod("get",String.class),lazy=c.getMethod("get",String.class,Supplier.class),put=c.getMethod("put",String.class,Object.class),present=c.getMethod("isPresent",String.class),remove=c.getMethod("remove",String.class);
  List<String> out=new ArrayList<String>();String key="boot-observation";remove.invoke(null,key);out.add("absent="+get.invoke(null,key)+":"+present.invoke(null,key));AtomicInteger count=new AtomicInteger();
  Supplier<String> s=()->{count.incrementAndGet();return "value";};out.add("load="+lazy.invoke(null,key,s));out.add("hit="+lazy.invoke(null,key,(Supplier<String>)()->"wrong"));out.add("calls="+count.get());
  put.invoke(null,key,"replacement");out.add("replace="+get.invoke(null,key));remove.invoke(null,key);out.add("remove="+present.invoke(null,key));
  out.add("null-load="+lazy.invoke(null,key,(Supplier<Object>)()->null));out.add("null-present="+present.invoke(null,key));Object marker=get.invoke(null,key);out.add("direct-null-marker="+(marker!=null));out.add("null-hit="+lazy.invoke(null,key,(Supplier<String>)()->"wrong"));
  try{put.invoke(null,key,null);out.add("put-null=accepted");}catch(InvocationTargetException e){out.add("put-null="+e.getCause().getClass().getName());}
  for(String line:out)System.out.println(line);return out;
 }
 public static void main(String[] a)throws Exception{try(URLClassLoader s=new URLClassLoader(new URL[]{new File(a[0]).toURI().toURL()},null);URLClassLoader o=new URLClassLoader(new URL[]{new File(a[1]).toURI().toURL()},null)){List<String>x=observe(s),y=observe(o);if(!x.equals(y))throw new AssertionError("supplier="+x+" owned="+y);System.out.println("memory cache differential: PASS");}}
}
