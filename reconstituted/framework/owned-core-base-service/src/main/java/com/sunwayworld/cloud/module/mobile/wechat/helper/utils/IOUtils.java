/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.utils;

import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.Charsets;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;

public abstract class IOUtils {
    private static final int DEFAULT_BUFFER_SIZE = 4096;

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static String toString(InputStream input) throws IOException {
        return IOUtils.toString(input, Charsets.UTF_8);
    }

    public static String toString(InputStream input, Charset charset) throws IOException {
        int n;
        InputStreamReader in = new InputStreamReader(input, charset);
        StringBuffer out = new StringBuffer();
        char[] c = new char[4096];
        while ((n = in.read(c)) != -1) {
            out.append(new String(c, 0, n));
        }
        IOUtils.closeQuietly(in);
        IOUtils.closeQuietly(input);
        return out.toString();
    }

    public static void toFile(InputStream input, File file) throws IOException {
        FileOutputStream os = new FileOutputStream(file);
        int bytesRead = 0;
        byte[] buffer = new byte[4096];
        while ((bytesRead = input.read(buffer, 0, 4096)) != -1) {
            ((OutputStream)os).write(buffer, 0, bytesRead);
        }
        IOUtils.closeQuietly(os);
        IOUtils.closeQuietly(input);
    }
}
