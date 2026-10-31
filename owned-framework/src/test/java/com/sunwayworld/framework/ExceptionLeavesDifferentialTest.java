package com.sunwayworld.framework;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Parentless behavior and serialization checks for the two closed exception leaves. */
public final class ExceptionLeavesDifferentialTest {
    private static URLClassLoader loader(FileHolder artifact, FileHolder owned, FileHolder deps) throws Exception {
        List<URL> urls=new ArrayList<URL>();
        if (artifact != null) urls.add(artifact.file.toURI().toURL());
        if (owned != null) urls.add(owned.file.toURI().toURL());
        if (deps != null) for(java.io.File f:deps.file.listFiles()) if(f.getName().endsWith(".jar")) urls.add(f.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[0]),null);
    }
    private static final class FileHolder { final java.io.File file; FileHolder(String path){file=new java.io.File(path);} }
    private static String describe(Throwable failure) {
        String s=failure.getClass().getName()+":"+String.valueOf(failure.getMessage());
        if(failure.getCause()!=null) s += ":cause="+failure.getCause().getClass().getName()+":"+String.valueOf(failure.getCause().getMessage());
        return s;
    }
    private static Constructor<?> constructorFor(Class<?> type, Object argument) {
        for (Constructor<?> constructor : type.getConstructors()) {
            Class<?>[] parameters = constructor.getParameterTypes();
            if (argument == null && parameters.length == 0) return constructor;
            if (argument == null && parameters.length == 1 && !parameters[0].isPrimitive()) return constructor;
            if (argument != null && parameters.length == 1 && parameters[0].isAssignableFrom(argument.getClass())) return constructor;
        }
        throw new IllegalArgumentException("no compatible constructor for " + type + " argument=" + argument);
    }
    private static Object[] constructorArguments(Constructor<?> constructor, Object argument) {
        return constructor.getParameterTypes().length == 0 ? new Object[0] : new Object[]{argument};
    }
    private static String construct(ClassLoader loader,String name,Object argument) throws Exception {
        try {
            Class<?> type=loader.loadClass(name);
            Constructor<?> ctor=constructorFor(type, argument);
            Object value=ctor.newInstance(constructorArguments(ctor, argument));
            Throwable cause=value instanceof Throwable ? ((Throwable)value).getCause() : null;
            return type.getName()+":message="+String.valueOf(((Throwable)value).getMessage())+":cause="+(cause==null?"null":cause.getClass().getName()+":"+cause.getMessage());
        } catch(InvocationTargetException failure) { return "!"+describe(failure.getCause()); }
    }
    private static String serial(ClassLoader loader,String name,Object argument) throws Exception {
        Class<?> type=loader.loadClass(name); Constructor<?> ctor=constructorFor(type, argument); Object value=ctor.newInstance(constructorArguments(ctor, argument));
        Field serial=type.getDeclaredField("serialVersionUID"); serial.setAccessible(true);
        ByteArrayOutputStream bytes=new ByteArrayOutputStream(); new ObjectOutputStream(bytes).writeObject(value);
        Object restored=new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray())).readObject();
        return serial.getLong(null)+":"+bytes.size()+":"+restored.getClass().getName()+":"+((Throwable)restored).getMessage();
    }
    private static String explicit(ClassLoader loader,String name,Class<?>[] parameterTypes,Object[] arguments) throws Exception {
        Class<?> type=loader.loadClass(name); Constructor<?> ctor=type.getConstructor(parameterTypes); Throwable value=(Throwable)ctor.newInstance(arguments); return value.getClass().getName()+":"+String.valueOf(value.getMessage());
    }
    private static String annotation(ClassLoader loader,String name) throws Exception {
        Class<?> status=loader.loadClass("org.springframework.web.bind.annotation.ResponseStatus");
        Object annotation=loader.loadClass(name).getAnnotation((Class)status);
        Method value=status.getMethod("value");
        return annotation==null?"null":String.valueOf(value.invoke(annotation));
    }
    public static void main(String[] args) throws Exception {
        try(URLClassLoader supplier=loader(new FileHolder(args[0]),null,new FileHolder(args[2])); URLClassLoader owned=loader(null,new FileHolder(args[1]),new FileHolder(args[2]))) {
            String[] names={"com.sunwayworld.framework.exception.ReflectionException","com.sunwayworld.framework.beans.exception.BeanException","com.sunwayworld.framework.exception.ProcessException","com.sunwayworld.framework.exception.ConcurrentException","com.sunwayworld.framework.exception.FormulaException","com.sunwayworld.framework.exception.TimeException","com.sunwayworld.framework.exception.UnauthorizedAccessException","com.sunwayworld.framework.exception.MaskImplementationException","com.sunwayworld.framework.exception.CompileException","com.sunwayworld.framework.exception.CronException","com.sunwayworld.framework.exception.EncryptException","com.sunwayworld.framework.exception.PasswordException","com.sunwayworld.framework.exception.ScriptException","com.sunwayworld.framework.exception.TaskException","com.sunwayworld.framework.exception.ThreadException","com.sunwayworld.framework.exception.ResolverArgumentException","com.sunwayworld.framework.exception.UnsupportedOperationSystemException","com.sunwayworld.framework.exception.FastDfsException","com.sunwayworld.framework.exception.FtpException","com.sunwayworld.framework.exception.checked.CheckedException","com.sunwayworld.framework.exception.database.JdbcException","com.sunwayworld.framework.exception.database.NotUniqueResultJdbcException","com.sunwayworld.framework.exception.database.UnsupportedDataTypeJdbcException"};
            Object[] reflectionValues={null,new IllegalStateException("boom"),new RuntimeException(),new Exception("checked")};
            Object[] beanValues={null,"","text","message with unicode 雪"};
            for(String name:names) {
                Object[] values=name.endsWith("ReflectionException")?reflectionValues:name.endsWith("ProcessException")||name.endsWith("CheckedException")?new Object[]{null,"text",new IllegalStateException("process")}:name.endsWith("NotUniqueResultJdbcException")?new Object[]{null}:name.endsWith("ConcurrentException")||name.endsWith("UnauthorizedAccessException")?new Object[]{null,"text"}:name.endsWith("FormulaException")||name.endsWith("TimeException")||name.endsWith("UnsupportedDataTypeJdbcException")?new Object[]{"text"}:name.endsWith("MaskImplementationException")?new Object[]{null,"text",new IllegalStateException("mask")}:name.endsWith("CompileException")||name.endsWith("CronException")?new Object[]{"text"}:name.endsWith("ThreadException")?new Object[]{new IllegalStateException("thread")}:name.endsWith("TaskException")?new Object[]{"text"}:name.endsWith("ResolverArgumentException")?new Object[]{"text",new IllegalStateException("resolver")}:name.endsWith("EncryptException")||name.endsWith("PasswordException")||name.endsWith("ScriptException")||name.endsWith("UnsupportedOperationSystemException")||name.endsWith("FastDfsException")||name.endsWith("FtpException")||name.endsWith("JdbcException")?new Object[]{"text",new IllegalStateException("cause")}:beanValues;
                for(Object value:values) {
                    String expected=construct(supplier,name,value), actual=construct(owned,name,value);
                    if(!expected.equals(actual)) throw new AssertionError(name+" construct supplier="+expected+" owned="+actual);
                    String expectedSerial=serial(supplier,name,value), actualSerial=serial(owned,name,value);
                    if(!expectedSerial.equals(actualSerial)) throw new AssertionError(name+" serialization supplier="+expectedSerial+" owned="+actualSerial);
                }
                String expectedAnnotation=annotation(supplier,name), actualAnnotation=annotation(owned,name);
                if(!expectedAnnotation.equals(actualAnnotation)) throw new AssertionError(name+" annotation supplier="+expectedAnnotation+" owned="+actualAnnotation);
            }
            for (String name : new String[]{"com.sunwayworld.framework.exception.database.MisMatchColumnNumberJdbcException","com.sunwayworld.framework.exception.database.MisMatchRowNumberJdbcException"}) {
                Class<?>[] types={int.class,int.class}; Object[] values={Integer.valueOf(2),Integer.valueOf(5)};
                String expected=explicit(supplier,name,types,values), actual=explicit(owned,name,types,values); if(!expected.equals(actual)) throw new AssertionError(name+" explicit supplier="+expected+" owned="+actual);
            }
            String noId="com.sunwayworld.framework.exception.database.NoIdPropertyJdbcException"; String expectedNoId=explicit(supplier,noId,new Class<?>[]{Class.class},new Object[]{String.class}); String actualNoId=explicit(owned,noId,new Class<?>[]{Class.class},new Object[]{String.class}); if(!expectedNoId.equals(actualNoId)) throw new AssertionError(noId+" explicit supplier="+expectedNoId+" owned="+actualNoId);
            System.out.println("exception leaves differential: PASS (constructors, cause/message, annotation, serialization)");
        }
    }
}
