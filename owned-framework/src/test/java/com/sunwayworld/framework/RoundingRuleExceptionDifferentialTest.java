package com.sunwayworld.framework;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

public final class RoundingRuleExceptionDifferentialTest {
    private static URLClassLoader loader(File artifact, File owned, File deps) throws Exception {
        List<URL> urls=new ArrayList<URL>();
        if (artifact!=null) urls.add(artifact.toURI().toURL());
        if (owned!=null) urls.add(owned.toURI().toURL());
        if (deps!=null) for(File f:deps.listFiles()) if(f.getName().endsWith(".jar")) urls.add(f.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[0]), null);
    }
    private static String construct(ClassLoader cl,Object argument) throws Exception {
        Class<?> type=cl.loadClass("com.sunwayworld.framework.exception.RoundingRuleException");
        Constructor<?> ctor=argument instanceof Throwable ? type.getConstructor(Throwable.class) : type.getConstructor(String.class);
        Throwable value=(Throwable)ctor.newInstance(argument);
        Throwable cause=value.getCause();
        return value.getClass().getName()+"|"+String.valueOf(value.getMessage())+"|"+(cause==null?"null":cause.getClass().getName()+":"+cause.getMessage());
    }
    private static String serialization(ClassLoader cl,Object argument) throws Exception {
        Class<?> type=cl.loadClass("com.sunwayworld.framework.exception.RoundingRuleException");
        Constructor<?> ctor=argument instanceof Throwable ? type.getConstructor(Throwable.class) : type.getConstructor(String.class);
        Throwable value=(Throwable)ctor.newInstance(argument);
        Field uid=type.getDeclaredField("serialVersionUID"); uid.setAccessible(true);
        ByteArrayOutputStream bytes=new ByteArrayOutputStream(); new ObjectOutputStream(bytes).writeObject(value);
        Object restored=new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray())).readObject();
        return uid.getLong(null)+"|"+bytes.size()+"|"+restored.getClass().getName()+"|"+restored;
    }
    private static String annotation(ClassLoader cl) throws Exception {
        Class<?> type=cl.loadClass("com.sunwayworld.framework.exception.RoundingRuleException");
        Class<?> status=cl.loadClass("org.springframework.web.bind.annotation.ResponseStatus");
        Annotation annotation=type.getAnnotation((Class<? extends Annotation>)status);
        return annotation==null?"null":String.valueOf(status.getMethod("value").invoke(annotation));
    }
    public static void main(String[] args) throws Exception {
        Object[] values={null,"", "rounding failed", new IllegalStateException("bad rounding"), new RuntimeException(), new Exception("checked")};
        try(URLClassLoader supplier=loader(new File(args[0]),null,new File(args[2])); URLClassLoader owned=loader(null,new File(args[1]),new File(args[2]))) {
            for(Object value:values) {
                String a=construct(supplier,value), b=construct(owned,value); if(!a.equals(b)) throw new AssertionError("construct supplier="+a+" owned="+b);
                a=serialization(supplier,value); b=serialization(owned,value); if(!a.equals(b)) throw new AssertionError("serialization supplier="+a+" owned="+b);
            }
            String a=annotation(supplier), b=annotation(owned); if(!a.equals(b)) throw new AssertionError("annotation supplier="+a+" owned="+b);
            System.out.println("RoundingRuleException differential: PASS (constructors, causes/messages, annotation, serialization)");
        }
    }
}
