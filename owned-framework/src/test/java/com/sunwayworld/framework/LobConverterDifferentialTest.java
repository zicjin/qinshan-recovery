package com.sunwayworld.framework;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import javax.sql.rowset.serial.*;

/** JDBC-only LOB observations, including partial reads and exception/cause paths. */
public final class LobConverterDifferentialTest {
    private static String describe(Throwable cause) {
        return cause.getClass().getName() + ":" + cause.getMessage()
                + (cause.getCause() == null ? "" : ":cause=" + describe(cause.getCause()));
    }
    private static String show(Object value) throws Exception {
        if (value == null) return "null";
        if (value instanceof byte[]) return value.getClass().getName() + Arrays.toString((byte[]) value);
        if (value instanceof char[]) return value.getClass().getName() + Arrays.toString((char[]) value);
        if (value instanceof Blob) return value.getClass().getName() + ":" + Arrays.toString(((Blob) value).length() == 0 ? new byte[0] : ((Blob) value).getBytes(1, (int) ((Blob) value).length()));
        if (value instanceof Clob) return value.getClass().getName() + ":" + (((Clob) value).length() == 0 ? "" : ((Clob) value).getSubString(1, (int) ((Clob) value).length()));
        return value.getClass().getName() + ":" + value;
    }
    private static final class Stub implements InvocationHandler {
        final boolean blob;
        final String failure;
        boolean closed;
        Stub(boolean blob, String failure) { this.blob = blob; this.failure = failure; }
        Object proxy() { return java.lang.reflect.Proxy.newProxyInstance(LobConverterDifferentialTest.class.getClassLoader(), new Class<?>[] {blob ? Blob.class : Clob.class}, this); }
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            if (name.equals(failure)) throw new SQLException("synthetic-" + name);
            if (name.equals("length")) return 4L;
            if (name.equals("getBinaryStream")) return new ByteArrayInputStream(new byte[] {65, 66, 67, 68}) {
                public synchronized int read(byte[] bytes, int off, int len) { return super.read(bytes, off, Math.min(1, len)); }
                public void close() throws IOException { closed = true; if (failure.equals("close")) throw new IOException("synthetic-close"); super.close(); }
            };
            if (name.equals("getCharacterStream")) return new StringReader("abcd") {
                public int read(char[] chars, int off, int len) throws IOException { return super.read(chars, off, Math.min(1, len)); }
                public void close() { closed = true; super.close(); }
            };
            if (name.equals("getBytes")) return new byte[] {65, 66, 67, 68};
            if (name.equals("toString")) return "synthetic-lob";
            if (name.equals("free")) return null;
            throw new UnsupportedOperationException(name);
        }
    }
    private static List<String> observe(ClassLoader loader) throws Exception {
        List<String> out = new ArrayList<String>();
        String prefix = "com.sunwayworld.framework.converter.";
        for (String name : new String[] {"array.PrimitiveByteArrayToStringConverter", "array.PrimitiveByteArrayToBlobConverter", "array.PrimitiveCharacterArrayToClobConverter", "string.StringToBlobConverter", "string.StringToClobConverter", "sql.BlobToPrimitiveByteArrayConverter", "sql.BlobToStringConverter", "sql.ClobToPrimitiveCharacterArrayConverter", "sql.ClobToStringConverter"}) {
            Class<?> type = loader.loadClass(prefix + name);
            Object converter = type.getField("INSTANCE").get(null);
            Class<?> source = (Class<?>) type.getMethod("getSourceType").invoke(converter);
            out.add(name + ":source=" + source + ":target=" + type.getMethod("getTargetType").invoke(converter));
            Object[] values = source == byte[].class ? new Object[] {null, new byte[0], new byte[] {65, (byte) 0xff}}
                    : source == char[].class ? new Object[] {null, new char[0], new char[] {'a', '中'}}
                    : source == String.class ? new Object[] {null, "", "héllo\n世界"}
                    : source == Blob.class ? new Object[] {null, new SerialBlob(new byte[0]), new SerialBlob(new byte[] {65, 66})}
                    : new Object[] {null, new SerialClob(new char[0]), new SerialClob(new char[] {'a', '中'})};
            Method convert = type.getMethod("convert", Object.class);
            for (Object value : values) {
                try { out.add(name + ":convert=" + show(convert.invoke(converter, value))); }
                catch (InvocationTargetException error) { out.add(name + ":error=" + describe(error.getCause())); }
            }
            if (source == Blob.class || source == Clob.class) {
                for (String failure : new String[] {"none", "length", source == Blob.class ? "getBinaryStream" : "getCharacterStream", "getBytes", "close"}) {
                    Stub stub = new Stub(source == Blob.class, failure);
                    try { out.add(name + ":stub:" + failure + "=" + show(convert.invoke(converter, stub.proxy()))); }
                    catch (InvocationTargetException error) { out.add(name + ":stub:" + failure + "=" + describe(error.getCause())); }
                    out.add(name + ":closed=" + stub.closed);
                }
            }
        }
        return out;
    }
    public static void main(String[] args) throws Exception {
        List<URL> supplierUrls = new ArrayList<URL>();
        for (File dependency : new File(args[0]).listFiles()) supplierUrls.add(dependency.toURI().toURL());
        try (URLClassLoader supplier = new URLClassLoader(supplierUrls.toArray(new URL[0]), null);
             URLClassLoader owned = new URLClassLoader(new URL[] {new File(args[1]).toURI().toURL()}, null)) {
            List<String> expected = observe(supplier), actual = observe(owned);
            if (expected.size() != actual.size()) throw new AssertionError("observation count");
            for (int i = 0; i < expected.size(); i++) if (!expected.get(i).equals(actual.get(i))) throw new AssertionError("at " + i + "\nsupplier=" + expected.get(i) + "\nowned=" + actual.get(i));
            System.out.println("LOB converter differential: PASS (" + expected.size() + " observations)");
        }
    }
}
