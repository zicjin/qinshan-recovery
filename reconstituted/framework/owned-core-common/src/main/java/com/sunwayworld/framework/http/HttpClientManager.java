/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.http;

import com.sunwayworld.framework.http.HttpClientInterface;
import com.sunwayworld.framework.http.HttpSimpleClient;
import com.sunwayworld.framework.http.HttpsSimpleClient;
import com.sunwayworld.framework.utils.StringUtils;
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

public class HttpClientManager
implements HttpClientInterface {
    private static volatile HttpClientManager instance;

    private HttpClientManager() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static HttpClientManager getInstance() {
        if (instance != null) return instance;
        Class<HttpClientManager> clazz = HttpClientManager.class;
        synchronized (HttpClientManager.class) {
            if (instance != null) return instance;
            instance = new HttpClientManager();
            // ** MonitorExit[var0] (shouldn't be in output)
            return instance;
        }
    }

    @Override
    public String sendHttpGet(String url, Consumer<HttpGet> consumer) {
        return this.getClient(url).sendHttpGet(url, consumer);
    }

    @Override
    public String sendHttpPost(String url, String jsonString, Consumer<HttpPost> consumer) {
        return this.getClient(url).sendHttpPost(url, jsonString, consumer);
    }

    @Override
    public String sendHttpPut(String url, String jsonString, Consumer<HttpPut> consumer) {
        return this.getClient(url).sendHttpPut(url, jsonString, consumer);
    }

    @Override
    public String sendHttpDelete(String url, Consumer<HttpDelete> consumer) {
        return this.getClient(url).sendHttpDelete(url, consumer);
    }

    @Override
    public String sendHttpPatch(String url, String jsonString, Consumer<HttpPatch> consumer) {
        return this.getClient(url).sendHttpPatch(url, jsonString, consumer);
    }

    @Override
    public String upload(String targetUrl, MultipartFile multipartFile) {
        return this.getClient(targetUrl).upload(targetUrl, multipartFile);
    }

    @Override
    public String upload(String targetUrl, File file) {
        return this.getClient(targetUrl).upload(targetUrl, file);
    }

    @Override
    public String upload(String targetUrl, String fileName, InputStream is) {
        return this.getClient(targetUrl).upload(targetUrl, fileName, is);
    }

    @Override
    public String upload(String targetUrl, String fileName, String sourceUrl) {
        return this.getClient(targetUrl).upload(targetUrl, fileName, sourceUrl);
    }

    @Override
    public <T> String upload(String targetUrl, MultipartFile multipartFile, @Nullable T param) {
        return this.getClient(targetUrl).upload(targetUrl, multipartFile, param);
    }

    @Override
    public <T> String upload(String targetUrl, File file, @Nullable T param) {
        return this.getClient(targetUrl).upload(targetUrl, file, param);
    }

    @Override
    public <T> String upload(String targetUrl, String fileName, InputStream is, @Nullable T param) {
        return this.getClient(targetUrl).upload(targetUrl, fileName, is, param);
    }

    @Override
    public <T> String upload(String targetUrl, String fileName, String sourceUrl, @Nullable T param) {
        return this.getClient(targetUrl).upload(targetUrl, fileName, sourceUrl, param);
    }

    @Override
    public void download(String sourceUrl, String targetPath) {
        this.getClient(sourceUrl).download(sourceUrl, targetPath);
    }

    @Override
    public void download(String sourceUrl, File targetFile) {
        this.getClient(sourceUrl).download(sourceUrl, targetFile);
    }

    @Override
    public InputStream download(String url) {
        return this.getClient(url).download(url);
    }

    private HttpClientInterface getClient(String url) {
        if (StringUtils.startsWithIgnoreCase(url, "https:")) {
            return HttpsSimpleClient.getInstance();
        }
        return HttpSimpleClient.getInstance();
    }
}
