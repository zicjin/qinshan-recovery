package com.sunwayworld.framework;

import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.net.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/** Public ClassUtils observations in independent supplier and owned class loaders. */
public final class ClassUtilsDifferentialTest {
    @Retention(RetentionPolicy.RUNTIME) public @interface Mark { }
    public interface Root { }
    public interface Left extends Root { }
    public interface Right extends Root { }
    public static class Base implements Left { }
    public static class Child extends Base implements Right { }
    public static class Plain { public Plain() { } public String toString() { return "plain"; } }
    @Mark public static class Annotated { }
    @Mark public interface MarkedInterface { }
    public static class ImplementsMarked implements MarkedInterface { }
    public static class CGLIB$First extends Plain { }
    public static class CGLIB$Second extends CGLIB$First { }
    public static class Values {
        private final String value;
        public Values(int i, boolean b, char c, short s, long l, float f, double d, String text) { value=i+":"+b+":"+(int)c+":"+s+":"+l+":"+f+":"+d+":"+text; }
        public String toString() { return value; }
    }
    public static class ByteValue { public ByteValue(byte b) { } }
    public static class PrivateCtor { private PrivateCtor() { } }
    public static abstract class AbstractType { public AbstractType() { } }
    public static class Throwing { public Throwing() { throw new IllegalStateException("fixture failure"); } }
    public static class Typed {
        private final String value;
        public Typed(String text, int number) { value=text+":"+number; }
        public String toString() { return value; }
    }
    private static int checks;
    private static URLClassLoader loader(File deps, File framework, File fixtures, File fixtureJar) throws Exception {
        List<URL> urls=new ArrayList<URL>();
        urls.add(fixtures.toURI().toURL()); urls.add(fixtureJar.toURI().toURL()); urls.add(framework.toURI().toURL());
        File[] jars=deps.listFiles(); Arrays.sort(jars);
        for(File file:jars) if(file.getName().endsWith(".jar") && !file.equals(framework)) urls.add(file.toURI().toURL());
        final URL root=new File(fixtures,"com/sunwayworld").toURI().toURL();
        final URL utilityRoot=new File(fixtures,"com/sunwayworld/framework/utils").toURI().toURL();
        return new URLClassLoader(urls.toArray(new URL[0]),null) {
            // Keep the vendor-wide concrete-class scan within a controlled fixture tree.
            public Enumeration<URL> getResources(String name) throws IOException {
                if(name.equals("com/sunwayworld") || name.equals("com/sunwayworld/")) return Collections.enumeration(Collections.singleton(root));
                if(name.equals("com/sunwayworld/framework/utils") || name.equals("com/sunwayworld/framework/utils/")) return Collections.enumeration(Collections.singleton(utilityRoot));
                if(name.equals("iofailure") || name.equals("iofailure/")) throw new IOException("fixture scan failure");
                return super.getResources(name);
            }
        };
    }
    private static Object invoke(Class<?> type,String name,Class<?>[] signature,Object... args) throws Exception {
        try { return type.getMethod(name,signature).invoke(null,args); }
        catch(InvocationTargetException e) { return failure(e.getCause()); }
    }
    private static String failure(Throwable t) {
        String out=t.getClass().getName();
        if(t.getCause()!=null) out+="->"+failure(t.getCause());
        // JVM reflective messages can contain loader-specific object identities.
        if(t instanceof IllegalStateException || t instanceof ClassNotFoundException) out+=":"+t.getMessage();
        return "ERROR:"+out;
    }
    private static String canonical(Object value) {
        if(value==null) return "null";
        if(value instanceof Class) return ((Class<?>)value).getName();
        if(value instanceof Constructor) return ((Constructor<?>)value).toGenericString();
        if(value instanceof List) { List<String> a=new ArrayList<String>(); for(Object item:(List<?>)value)a.add(canonical(item)); return a.toString(); }
        return value.getClass().getName()+":"+value;
    }
    private static void same(Object expected,Object actual,String label) {
        checks++; if(!Objects.equals(canonical(expected),canonical(actual))) throw new AssertionError(label+" supplier="+canonical(expected)+" owned="+canonical(actual));
    }
    private static Object unary(Class<?> utility,String name,Class<?> value)throws Exception { return invoke(utility,name,new Class<?>[]{Class.class},value); }
    private static Object typed(Class<?> utility,Class<?> target,Class<?>[] types,Object[] values)throws Exception {
        Class<?> pair=utility.getClassLoader().loadClass("com.sunwayworld.framework.data.TypeValuePair");
        Object array=types==null?null:Array.newInstance(pair,types.length);
        if(types!=null)for(int i=0;i<types.length;i++)Array.set(array,i,pair.getConstructor(Class.class,Object.class).newInstance(types[i],values[i]));
        return invoke(utility,"newInstance",new Class<?>[]{Class.class,Array.newInstance(pair,0).getClass()},target,array);
    }
    private static void primitives(Class<?> r,Class<?> o)throws Exception {
        Class<?>[] types={null,boolean.class,byte.class,char.class,short.class,int.class,long.class,float.class,double.class,void.class,Boolean.class,Byte.class,Character.class,Short.class,Integer.class,Long.class,Float.class,Double.class,Void.class,String.class,StringBuilder.class,Number.class,java.math.BigDecimal.class,Date.class,java.sql.Date.class,Map.class,HashMap.class,List.class,Set.class,LocalDate.class,Duration.class,Object.class,int[].class,Plain.class};
        for(Class<?> t:types)for(String n:new String[]{"isPrimitiveType","isWrapperType","getWrapperType","getPrimitiveType","getPrimitiveDefaultValue","resolvePrimitiveIfNecessary","isSimpleType","isGikamType","isCglibProxyClass","getRawType"})same(unary(r,n,t),unary(o,n,t),n+" "+t);
        for(Class<?> t:new Class<?>[]{CGLIB$First.class,CGLIB$Second.class,java.lang.reflect.Proxy.getProxyClass(ClassUtilsDifferentialTest.class.getClassLoader(),Root.class)}) {
            same(unary(r,"getRawType",t),unary(o,"getRawType",t),"proxy raw "+t);
            same(unary(r,"isCglibProxyClass",t),unary(o,"isCglibProxyClass",t),"proxy marker "+t);
        }
    }
    private static void constructors(Class<?> r,Class<?> o)throws Exception {
        for(Class<?> t:new Class<?>[]{null,Plain.class,Values.class,ByteValue.class,PrivateCtor.class,AbstractType.class,Throwing.class,Root.class,int.class}) {
            same(unary(r,"newInstance",t),unary(o,"newInstance",t),"minimal ctor "+t);
            same(typed(r,t,null,null),typed(o,t,null,null),"null typed args "+t);
            same(typed(r,t,new Class<?>[0],new Object[0]),typed(o,t,new Class<?>[0],new Object[0]),"empty typed args "+t);
        }
        same(typed(r,Typed.class,new Class<?>[]{String.class,int.class},new Object[]{"text",7}),typed(o,Typed.class,new Class<?>[]{String.class,int.class},new Object[]{"text",7}),"typed public ctor");
        same(typed(r,Typed.class,new Class<?>[]{String.class,Integer.class},new Object[]{"text",7}),typed(o,Typed.class,new Class<?>[]{String.class,Integer.class},new Object[]{"text",7}),"typed exact signature");
        for(Class<?> t:new Class<?>[]{null,Plain.class,Typed.class,PrivateCtor.class})for(Class<?>[] args:new Class<?>[][]{null,new Class<?>[0],{String.class,int.class},{String.class,Integer.class}})same(invoke(r,"getConstructorIfAvailable",new Class<?>[]{Class.class,Class[].class},t,args),invoke(o,"getConstructorIfAvailable",new Class<?>[]{Class.class,Class[].class},t,args),"constructor lookup");
    }
    private static void loading(Class<?> r,Class<?> o,ClassLoader rl,ClassLoader ol)throws Exception {
        for(String name:new String[]{null,"","int","long","double","float","char","short","boolean","byte","void","java.lang.String","int[][]","java.lang.String[]","[Ljava.lang.String;","[[I","missing.NoSuchClass","com.sunwayworld.contractscan.ScanFixture$Lazy"}) {
            Thread.currentThread().setContextClassLoader(rl);Object x=invoke(r,"getClass",new Class<?>[]{String.class},name);
            Thread.currentThread().setContextClassLoader(ol);Object y=invoke(o,"getClass",new Class<?>[]{String.class},name);same(x,y,"class resolution "+name);
        }
        if(System.getProperty("classutils.fixture.initialized")!=null)throw new AssertionError("getClass initialized fixture");
        Thread.currentThread().setContextClassLoader(null);
        same(invoke(r,"getClass",new Class<?>[]{String.class},"java.lang.String"),invoke(o,"getClass",new Class<?>[]{String.class},"java.lang.String"),"system loader fallback");
        for(Class<?> t:new Class<?>[]{null,Object.class,Root.class,Left.class,Right.class,Base.class,Child.class})same(unary(r,"getAllInterfaces",t),unary(o,"getAllInterfaces",t),"interface order "+t);
        for(Class<?> t:new Class<?>[]{null,Object.class,Plain.class,Annotated.class,MarkedInterface.class,ImplementsMarked.class,Root.class})same(invoke(r,"isAnnotationPrsent",new Class<?>[]{Class.class,Class.class},t,Mark.class),invoke(o,"isAnnotationPrsent",new Class<?>[]{Class.class,Class.class},t,Mark.class),"annotation "+t);
    }
    @SuppressWarnings("unchecked") private static List<String> scan(Class<?> utility,ClassLoader loader)throws Exception {
        Thread.currentThread().setContextClassLoader(loader);
        List<String> out=new ArrayList<String>();
        Class<?> cache=loader.loadClass("com.sunwayworld.framework.cache.memory.MemoryCacheManager");
        Method remove=cache.getMethod("remove",String.class),put=cache.getMethod("put",String.class,Object.class);
        for(String pkg:new String[]{"com.sunwayworld.contractscan","/com/sunwayworld/contractscan/","contractjar","com.sunwayworld.framework.utils","absent.fixture"}) {
            remove.invoke(null,"classesUnderPackage:"+pkg);
            List<Class<?>> first=(List<Class<?>>)utility.getMethod("getAllClasses",String.class).invoke(null,pkg);
            out.add("scan "+pkg+"="+canonical(first));
            AtomicInteger misses=new AtomicInteger();
            out.add("any-false="+invoke(utility,"hasAnyMatchClass",new Class<?>[]{String.class,Predicate.class},pkg,(Predicate<Class<?>>)c->{misses.incrementAndGet();return false;}));
            out.add("false-predicate-calls="+misses.get());
            List<Class<?>> second=(List<Class<?>>)utility.getMethod("getAllClasses",String.class).invoke(null,pkg);
            out.add("same-list="+(first==second));
            first.add(String.class);out.add("backed-cache="+second.contains(String.class));
            try{second.add(Integer.class);out.add("cache-mutable");}catch(UnsupportedOperationException expected){out.add("cache-unmodifiable");}
            AtomicInteger calls=new AtomicInteger();
            out.add("any="+invoke(utility,"hasAnyMatchClass",new Class<?>[]{String.class,Predicate.class},pkg,(Predicate<Class<?>>)c->{calls.incrementAndGet();return true;}));
            out.add("predicate-calls="+calls.get());
        }
        for(String pkg:new String[]{null,"com.sunwayworld.contractscan","/com/sunwayworld/contractscan/","absent.fixture","iofailure"})out.add("predicated="+canonical(invoke(utility,"getPredicatedClasses",new Class<?>[]{String.class,Predicate.class},pkg,(Predicate<Class<?>>)c->c.getSimpleName().equals("Concrete"))));
        out.add("scan-io="+canonical(invoke(utility,"getAllClasses",new Class<?>[]{String.class},"iofailure")));
        out.add("predicated-excluded-inner="+canonical(invoke(utility,"getPredicatedClasses",new Class<?>[]{String.class,Predicate.class},"com.sunwayworld.framework.utils",(Predicate<Class<?>>)c->true)));
        out.add("null-predicate="+canonical(invoke(utility,"getPredicatedClasses",new Class<?>[]{String.class,Predicate.class},"com.sunwayworld.contractscan",null)));
        List<Class<?>> cached=new ArrayList<Class<?>>();cached.add(String.class);cached.add(Integer.class);put.invoke(null,"classesUnderPackage:com.sunwayworld",cached);
        for(String name:new String[]{"string","INTEGER","missing",null})out.add("gikam="+canonical(invoke(utility,"getGikamClass",new Class<?>[]{String.class},name)));
        Class<?> base=loader.loadClass("com.sunwayworld.contractscan.ScanFixture$Service");
        out.add("concrete="+canonical(unary(utility,"getGikamConcreteClassList",base)));
        // Cache keys do not include the current class loader.
        ClassLoader old=Thread.currentThread().getContextClassLoader();Thread.currentThread().setContextClassLoader(new URLClassLoader(new URL[0],null));
        out.add("cached-other-loader="+(cached==utility.getMethod("getAllClasses",String.class).invoke(null,"com.sunwayworld")));Thread.currentThread().setContextClassLoader(old);
        Class<?> resource=loader.loadClass("org.springframework.core.io.Resource");
        Predicate<Object> txt=x->{try{return ((String)resource.getMethod("getFilename").invoke(x)).endsWith(".txt");}catch(Exception e){throw new RuntimeException(e);}};
        for(String path:new String[]{null,"contractresources","/contractresources/","absent.fixture"})for(Predicate<?>[] predicates:new Predicate<?>[][]{null,new Predicate<?>[0],new Predicate<?>[]{txt},new Predicate<?>[]{txt,x->false}}) {
            Object value=invoke(utility,"getResourceList",new Class<?>[]{String.class,Predicate[].class},path,predicates);
            if(value instanceof List){List<String> names=new ArrayList<String>();for(Object item:(List<?>)value)names.add((String)resource.getMethod("getFilename").invoke(item));out.add("resources="+names);}else out.add("resources="+canonical(value));
        }
        return out;
    }
    public static void main(String[] args)throws Exception {
        File deps=new File(args[0]),framework=new File(args[1]),fixtures=new File(args[3]),fixtureJar=new File(args[4]);
        ClassLoader original=Thread.currentThread().getContextClassLoader();
        try(URLClassLoader r=loader(deps,framework,fixtures,fixtureJar);URLClassLoader o=loader(deps,new File(args[2]),fixtures,fixtureJar)) {
            Class<?> reference=r.loadClass("com.sunwayworld.framework.utils.ClassUtils");
            Class<?> owned=o.loadClass(reference.getName());
            if(args.length>5 && args[5].equals("hang")) { Class<?> target=args[6].equals("supplier")?reference:owned;System.out.println("annotation-loop-entered");System.out.flush();target.getMethod("isAnnotationPrsent",Class.class,Class.class).invoke(null,Child.class,Mark.class);throw new AssertionError("supplier annotation loop unexpectedly returned"); }
            if(!owned.getProtectionDomain().getCodeSource().getLocation().equals(new File(args[2]).toURI().toURL()))throw new AssertionError("owned utility loaded outside owned classes");
            primitives(reference,owned);constructors(reference,owned);loading(reference,owned,r,o);
            List<String> expected=scan(reference,r),actual=scan(owned,o);
            if(expected.size()!=actual.size())throw new AssertionError("scan observation count");
            for(int index=0;index<expected.size();index++)same(expected.get(index),actual.get(index),"scan observation "+index);
            if(System.getProperty("classutils.fixture.initialized")!=null)throw new AssertionError("scan initialized fixture");
            System.out.println("ClassUtils differential: PASS ("+checks+" observations; isolated supplier-free owned loading)");
        } finally {Thread.currentThread().setContextClassLoader(original);}
    }
}
