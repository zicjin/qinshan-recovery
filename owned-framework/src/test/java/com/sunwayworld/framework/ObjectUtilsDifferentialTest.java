package com.sunwayworld.framework;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.time.*;

/** Isolated public ObjectUtils observations against the pinned supplier reference. */
public final class ObjectUtilsDifferentialTest {
    public static class Bean {
        private String name; private List<String> values;
        public Bean() { }
        public String getName() { return name; } public void setName(String value) { name=value; }
        public List<String> getValues() { return values; } public void setValues(List<String> value) { values=value; }
    }
    public static class SerializableBean extends Bean implements Serializable { private static final long serialVersionUID=1L; }
    public static class Plain { int x = 7; @Override public String toString() { return "plain-value"; } }
    public static class TrackingReader extends Reader {
        private final Reader delegate;
        private final boolean failRead;
        private final boolean failClose;
        boolean closed;
        TrackingReader(String text, boolean failRead, boolean failClose) {
            this.delegate = new StringReader(text); this.failRead = failRead; this.failClose = failClose;
        }
        public int read(char[] buffer, int offset, int length) throws IOException {
            if (failRead) throw new IOException("synthetic-read-failure");
            return delegate.read(buffer, offset, length);
        }
        public void close() throws IOException {
            closed = true;
            delegate.close();
            if (failClose) throw new IOException("synthetic-close-failure");
        }
    }
    private static int checks;
    private static URLClassLoader loader(File deps, File framework) throws Exception {
        List<URL> urls=new ArrayList<URL>(); urls.add(framework.toURI().toURL());
        for(File file:deps.listFiles()) if(file.getName().endsWith(".jar")) urls.add(file.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[0]),ObjectUtilsDifferentialTest.class.getClassLoader());
    }
    private static Object result(Class<?> type,String operation,Object... args) throws Exception {
        Class<?>[] signature=operation.equals("equals")||operation.equals("defaultIfNull")?new Class<?>[]{Object.class,Object.class}:new Class<?>[]{operation.equals("getStackTrace")?Throwable.class:Object.class};
        try { return type.getMethod(operation,signature).invoke(null,args); }
        catch(InvocationTargetException failure) { return "ERROR:"+failure.getCause().getClass().getName()+":"+failure.getCause().getMessage(); }
    }
    private static void same(Object expected,Object actual,String name) { checks++; if(!Objects.equals(expected,actual)) throw new AssertionError(name+": supplier="+String.valueOf(expected).replace("\n","\\n")+" ["+(expected==null?0:expected.toString().length())+"] owned="+String.valueOf(actual).replace("\n","\\n")+" ["+(actual==null?0:actual.toString().length())+"]"); }
    private static List<String> cloneObservation(Class<?> type,Object value) throws Exception {
        List<String> result=new ArrayList<String>(); Object copy=result(type,"clone",value);
        if(copy instanceof String && ((String)copy).startsWith("ERROR:")) { result.add(copy.toString()); return result; }
        result.add("null="+(copy==null)); if(copy==null)return result;
        result.add("type="+copy.getClass().getName()); result.add("same="+(value==copy));
        if(value instanceof Map) { Map<?,?> a=(Map<?,?>)value,b=(Map<?,?>)copy; result.add("map="+a.equals(b)); result.add("nested-same="+(a.get("nested")==b.get("nested"))); }
        if(value instanceof List) { List<?> a=(List<?>)value,b=(List<?>)copy; result.add("size="+b.size()); result.add("first-same="+(a.isEmpty()||a.get(0)==b.get(0))); }
        if(value instanceof Bean) { Bean a=(Bean)value,b=(Bean)copy; result.add("name="+b.getName());result.add("values="+b.getValues());result.add("nested-same="+(a.getValues()==b.getValues())); }
        if(value.getClass().isArray()) { result.add("length="+Array.getLength(copy)); if(Array.getLength(copy)>0)result.add("first-same="+(Array.get(value,0)==Array.get(copy,0))); }
        return result;
    }
    public static void main(String[] args) throws Exception {
        try(URLClassLoader r=loader(new File(args[0]),new File(args[1]));URLClassLoader o=loader(new File(args[2]),new File(args[3]))) {
            Class<?> reference=r.loadClass("com.sunwayworld.framework.utils.ObjectUtils"),owned=o.loadClass(reference.getName());
            Map<String,Object> map=new LinkedHashMap<String,Object>(); map.put("name","value");map.put("nested",new ArrayList<String>(Arrays.asList("x")));
            Object[] values={null,""," ","hello",0,1,1L,1D,Double.NaN,Double.POSITIVE_INFINITY,12.345678901234D, new java.math.BigDecimal("1000.0100"),new java.math.BigInteger("12345678901234567890"),false,new int[0],new int[]{1,2},new String[]{"x",null},Arrays.asList("a","b"),Arrays.asList("b","a"),Arrays.asList("a","a","b"),new ArrayList<String>(Arrays.asList("a","b")),Collections.emptyMap(),map,Optional.empty(),LocalDate.of(2026,10,19),LocalDateTime.of(2026,10,19,12,34,56,123456789),LocalTime.of(12,34,56,123456789),new Date(0),new StringBuilder("abc"),new char[]{'a','b'},new byte[]{65,66},new Byte[]{65,66},0.1f,java.nio.charset.StandardCharsets.UTF_8};
            for(Object a:values) {
                same(result(reference,"isEmpty",a),result(owned,"isEmpty",a),"empty "+(a==null?null:a.getClass()));
                for(Object b:values) same(result(reference,"equals",a,b),result(owned,"equals",a,b),"equals "+a+" / "+b);
                if(true) {
                    same(result(reference,"toString",a),result(owned,"toString",a),"toString "+(a==null?null:a.getClass()));
                    same(result(reference,"toShortString",a),result(owned,"toShortString",a),"short "+(a==null?null:a.getClass()));
                }
            }
            Bean bean=new Bean();bean.setName("bean");bean.setValues(new ArrayList<String>(Arrays.asList("one")));
            SerializableBean serial=new SerializableBean();serial.setName("serial");serial.setValues(new ArrayList<String>(Arrays.asList("one")));
            for(Object value:new Object[]{null,"text",map,new HashMap<String,Object>(map),new ArrayList<Bean>(Arrays.asList(bean)),new Bean[]{bean},bean,serial,new Plain(),new Plain[]{new Plain()},new ArrayList<Plain>(Arrays.asList(new Plain()))}) same(cloneObservation(reference,value),cloneObservation(owned,value),"clone "+(value==null?null:value.getClass()));
            Map<String,Object> logged=new LinkedHashMap<String,Object>();logged.put("text",String.join("",Collections.nCopies(130,"x")));logged.put("bytes",new byte[]{1}); logged.put("stream",new ByteArrayInputStream(new byte[]{1}));
            same(result(reference,"toShortString",logged),result(owned,"toShortString",logged),"redacted short JSON");
            for(Object value:new Object[]{null,"","x",0}) same(result(reference,"defaultIfNull",value,"fallback"),result(owned,"defaultIfNull",value,"fallback"),"default "+value);
            RuntimeException error=new RuntimeException("fixture",new IllegalArgumentException("cause"));error.setStackTrace(new StackTraceElement[]{new StackTraceElement("Example","run","Example.java",7)});
            same(result(reference,"toString",new StringReader("alpha\nbeta")),result(owned,"toString",new StringReader("alpha\nbeta")),"reader conversion");
            for (String text : new String[] {"", "alpha\rbeta", "alpha\r\nbeta\r\n", "\n", "世界\nlast"}) {
                TrackingReader left = new TrackingReader(text, false, false), right = new TrackingReader(text, false, false);
                same(result(reference,"toString",left), result(owned,"toString",right), "reader text "+text);
                same(left.closed, right.closed, "reader close "+text);
            }
            for (boolean failRead : new boolean[] {false, true}) {
                TrackingReader left = new TrackingReader("alpha", failRead, true), right = new TrackingReader("alpha", failRead, true);
                same(result(reference,"toString",left), result(owned,"toString",right), "reader failure "+failRead);
                same(left.closed, right.closed, "reader failure close "+failRead);
            }
            for (Object value : new Object[] {
                    new java.sql.Time(0), new java.sql.Timestamp(0), new java.sql.Date(0),
                    new javax.sql.rowset.serial.SerialBlob("héllo世界".getBytes(java.nio.charset.StandardCharsets.UTF_8)),
                    new javax.sql.rowset.serial.SerialClob("héllo世界".toCharArray()),
                    new java.math.BigDecimal("123456789012345678901234567890.12345678901234567890"),
                    new java.math.BigDecimal("0.000000000001"), new java.math.BigDecimal("-0.000000000001")})
                same(result(reference,"toString",value), result(owned,"toString",value), "extended conversion "+value.getClass().getName()+":"+value);
            same(result(reference,"getStackTrace",error),result(owned,"getStackTrace",error),"stack trace");
            same(result(reference,"getStackTrace",new Object[]{null}),result(owned,"getStackTrace",new Object[]{null}),"null stack trace");
            System.out.println("ObjectUtils differential: PASS ("+checks+" observations)");
        }
    }
}
