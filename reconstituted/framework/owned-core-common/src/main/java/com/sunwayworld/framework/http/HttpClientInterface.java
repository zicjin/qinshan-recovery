/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.http;

import java.io.File;
import java.io.InputStream;
import java.util.function.Consumer;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

public interface HttpClientInterface {
    public String sendHttpGet(String var1, Consumer<HttpGet> var2);

    default public String sendHttpGet(String url) {
        return this.sendHttpGet(url, null);
    }

    public String sendHttpPost(String var1, String var2, Consumer<HttpPost> var3);

    default public String sendHttpPost(String url, String jsonString) {
        return this.sendHttpPost(url, jsonString, null);
    }

    public String sendHttpPut(String var1, String var2, Consumer<HttpPut> var3);

    default public String sendHttpPut(String url, String jsonString) {
        return this.sendHttpPut(url, jsonString, null);
    }

    public String sendHttpDelete(String var1, Consumer<HttpDelete> var2);

    default public String sendHttpDelete(String url) {
        return this.sendHttpDelete(url, null);
    }

    public String sendHttpPatch(String var1, String var2, Consumer<HttpPatch> var3);

    default public String sendHttpPatch(String url, String jsonString) {
        return this.sendHttpPatch(url, jsonString, null);
    }

    public String upload(String var1, MultipartFile var2);

    public String upload(String var1, File var2);

    public String upload(String var1, String var2, InputStream var3);

    public String upload(String var1, String var2, String var3);

    public <T> String upload(String var1, MultipartFile var2, @Nullable T var3);

    public <T> String upload(String var1, File var2, @Nullable T var3);

    public <T> String upload(String var1, String var2, InputStream var3, @Nullable T var4);

    public <T> String upload(String var1, String var2, String var3, @Nullable T var4);

    public void download(String var1, String var2);

    public void download(String var1, File var2);

    public InputStream download(String var1);
}
