package com.sunwayworld.framework;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/** Parentless byte/file/url observations for the owned encoding detector. */
public final class EncodingUtilsDifferentialTest {
    private static URLClassLoader loader(File... entries) throws Exception {
        URL[] urls = new URL[entries.length];
        for (int i = 0; i < entries.length; i++) urls[i] = entries[i].toURI().toURL();
        return new URLClassLoader(urls, null);
    }

    private static String call(Class<?> type, String method, Class<?>[] signature, Object... args) throws Exception {
        try {
            Object value = type.getMethod(method, signature).invoke(null, args);
            return value == null ? "null" : String.valueOf(value);
        } catch (InvocationTargetException failure) {
            Throwable cause = failure.getCause();
            return "!" + cause.getClass().getName() + ":" + cause.getMessage();
        }
    }

    private static List<String> observe(ClassLoader loader) throws Exception {
        Class<?> type = Class.forName("com.sunwayworld.framework.utils.EncodingUtils", true, loader);
        List<byte[]> samples = Arrays.asList(
            null,
            new byte[0],
            "plain ascii".getBytes("US-ASCII"),
            "中文 UTF-8".getBytes("UTF-8"),
            new byte[] {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF, 'x'},
            "中文".getBytes("UTF-16LE"),
            "中文".getBytes("UTF-16BE"),
            "中文".getBytes("GBK"),
            "中文".getBytes("Big5"),
            new byte[] {(byte) 0xFF, 0x00, 0x41, 0x00, 0x42, 0x00},
            new byte[] {(byte) 0xC3, 0x28, (byte) 0xA0, (byte) 0xA1},
            new byte[] {(byte) 0x80, (byte) 0x81, (byte) 0xFE, (byte) 0xFF}
        );
        List<String> out = new ArrayList<String>();
        for (byte[] sample : samples) {
            out.add("bytes=" + call(type, "detect", new Class<?>[] {byte[].class}, sample));
        }
        Random random = new Random(0x454e434f44494e47L);
        for (int i = 0; i < 128; i++) {
            byte[] sample = new byte[random.nextInt(513)];
            random.nextBytes(sample);
            out.add("random-" + i + "=" + call(type, "detect", new Class<?>[] {byte[].class}, sample));
        }
        File file = Files.createTempFile("encoding-contract", ".bin").toFile();
        try {
            byte[] payload = "中文 UTF-8".getBytes(Charset.forName("UTF-8"));
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(payload);
            stream.close();
            out.add("file=" + call(type, "detect", new Class<?>[] {File.class}, file));
            out.add("path=" + call(type, "detect", new Class<?>[] {String.class}, file.getAbsolutePath()));
            out.add("url=" + call(type, "detect", new Class<?>[] {URL.class}, file.toURI().toURL()));
            File missing = new File(file.getParentFile(), file.getName() + ".missing");
            out.add("missing-file=" + call(type, "detect", new Class<?>[] {File.class}, missing));
            String missingPath = call(type, "detect", new Class<?>[] {String.class}, missing.getAbsolutePath());
            out.add("missing-path=" + missingPath.replace(file.getName(), "<temp>.bin"));
        } finally {
            file.delete();
        }
        return out;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) throw new IllegalArgumentException("supplier jar, owned classes");
        List<String> supplier;
        List<String> owned;
        try (URLClassLoader reference = loader(new File(args[0])); URLClassLoader replacement = loader(new File(args[1]))) {
            supplier = observe(reference);
            owned = observe(replacement);
        }
        if (!supplier.equals(owned)) throw new AssertionError("encoding observations differ: " + supplier + " != " + owned);
        System.out.println("encoding utils differential: PASS " + supplier.size());
    }
}
