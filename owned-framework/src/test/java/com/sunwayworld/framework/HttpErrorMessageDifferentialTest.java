package com.sunwayworld.framework;

import java.io.File;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/** Parentless differential for HTTP error payload factories and accessors. */
public final class HttpErrorMessageDifferentialTest {
    private static URLClassLoader loader(File deps,File framework,File reference)throws Exception{List<URL> u=new ArrayList<URL>();if(reference!=null)u.add(reference.toURI().toURL());u.add(framework.toURI().toURL());for(File f:deps.listFiles())if(f.getName().endsWith(".jar"))u.add(f.toURI().toURL());return new URLClassLoader(u.toArray(new URL[0]),null);}
    private static void same(String label,Object a,Object b){if(!Objects.equals(String.valueOf(a),String.valueOf(b)))throw new AssertionError(label+" supplier="+a+" owned="+b);}
    public static void main(String[] args)throws Exception{
        try(URLClassLoader supplier=loader(new File(args[0]),new File(args[1]),new File(args[2]));URLClassLoader owned=loader(new File(args[0]),new File(args[3]),null)){
            Class<?> ss=supplier.loadClass("org.springframework.http.HttpStatus"), os=owned.loadClass("org.springframework.http.HttpStatus"); Object sStatus=ss.getField("NOT_FOUND").get(null), oStatus=os.getField("NOT_FOUND").get(null);
            Class<?> st=supplier.loadClass("com.sunwayworld.framework.exception.core.HttpErrorMessage"), ot=owned.loadClass("com.sunwayworld.framework.exception.core.HttpErrorMessage");
            Object s=st.getMethod("of",ss,String.class).invoke(null,sStatus,"missing"); Object o=ot.getMethod("of",os,String.class).invoke(null,oStatus,"missing");
            for(String getter:new String[]{"getStatus","getError","getMessage","getPath","getStacktrace"})same(getter,st.getMethod(getter).invoke(s),ot.getMethod(getter).invoke(o));
            if(st.getMethod("getTimestamp").invoke(s)==null||ot.getMethod("getTimestamp").invoke(o)==null)throw new AssertionError("timestamp missing");
            Throwable sr=new IllegalStateException("boom"); Object se=st.getMethod("of",ss,String.class,Throwable.class).invoke(null,sStatus,"failed",sr); Object oe=ot.getMethod("of",os,String.class,Throwable.class).invoke(null,oStatus,"failed",new IllegalStateException("boom"));
            same("stacktrace contains",String.valueOf(st.getMethod("getStacktrace").invoke(se)).contains("boom"),String.valueOf(ot.getMethod("getStacktrace").invoke(oe)).contains("boom")); same("error",st.getMethod("getError").invoke(se),ot.getMethod("getError").invoke(oe)); same("message",st.getMethod("getMessage").invoke(se),ot.getMethod("getMessage").invoke(oe));
            Class<?> requestType=owned.loadClass("javax.servlet.http.HttpServletRequest"); Object request=Proxy.newProxyInstance(owned,new Class[]{requestType},(proxy,method,arguments)->method.getName().equals("getRequestURI")?"/owned/path":null); owned.loadClass("com.sunwayworld.owned.internal.context.OwnedRequestContext").getMethod("setServletRequest",owned.loadClass("javax.servlet.ServletRequest")).invoke(null,request); Object path=ot.getMethod("of",os,String.class).invoke(null,oStatus,"path"); same("request path", "/owned/path", ot.getMethod("getPath").invoke(path));
            System.out.println("HttpErrorMessage differential: PASS (factories, fields, stacktrace, request path and JSON payload)");
        }
    }
}
