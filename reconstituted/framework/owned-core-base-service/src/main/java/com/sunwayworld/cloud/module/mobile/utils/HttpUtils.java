/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.utils;

import com.sunwayworld.framework.utils.StringUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;

public final class HttpUtils {
    private static String ContentType = "application/x-www-form-urlencoded";
    private static final HttpDelegate delegate;

    private HttpUtils() {
    }

    public static void setContentType(String contentType) {
        ContentType = contentType;
    }

    public static String get(String url) {
        return delegate.get(url);
    }

    public static String get(String url, String token) {
        return delegate.get(url, token);
    }

    public static String get(String url, Map<String, String> headers) {
        return delegate.get(url, headers);
    }

    public static <T> String get(String url, Map<String, T> queryParas, String token) {
        return delegate.get(url, queryParas, token);
    }

    public static String post(String url, String data) {
        return delegate.post(url, data);
    }

    public static String post(String url, String data, String token) {
        return delegate.post(url, data, token);
    }

    public static <T> String post(String url, Map<String, T> params) {
        return delegate.post(url, params);
    }

    public static <T> String post(String url, Map<String, T> params, Map<String, String> headers) {
        return delegate.post(url, params, headers);
    }

    public static <T> String post(String url, Map<String, T> params, String token) {
        return delegate.post(url, params, token);
    }

    public static String postSSL(String url, String data, String certPath, String certPass) {
        return delegate.postSSL(url, data, certPath, certPass);
    }

    public static String postSSL(String url, String data, String certPath, String certPass, String token) {
        return delegate.postSSL(url, data, certPath, certPass, token);
    }

    public static <T> String postSSL(String url, Map<String, T> params, String certPath, String certPass, String token) {
        return delegate.postSSL(url, params, certPath, certPass, token);
    }

    public static String put(String url, String data) {
        return delegate.put(url, data);
    }

    public static String put(String url, String data, String token) {
        return delegate.put(url, data, token);
    }

    public static <T> String put(String url, Map<String, T> params) {
        return delegate.put(url, params);
    }

    public static <T> String put(String url, Map<String, T> params, String token) {
        return delegate.put(url, params, token);
    }

    public static String delete(String url, String data) {
        return delegate.delete(url, data);
    }

    public static String delete(String url, String data, String token) {
        return delegate.delete(url, data, token);
    }

    public static <T> String delete(String url, Map<String, T> params) {
        return delegate.delete(url, params);
    }

    public static <T> String delete(String url, Map<String, T> params, String token) {
        return delegate.delete(url, params, token);
    }

    public static InputStream download(String url) {
        return delegate.download(url);
    }

    public static InputStream download(String url, String params) {
        return delegate.download(url, params);
    }

    public static <T> InputStream download(String url, Map<String, T> params) {
        return delegate.download(url, params);
    }

    public static InputStream download(String url, String params, String token) {
        return delegate.download(url, params, token);
    }

    public static <T> InputStream download(String url, Map<String, T> params, String token) {
        return delegate.download(url, params, token);
    }

    public static String upload(String url, File file, String params) {
        return delegate.upload(url, file, params);
    }

    public static String upload(String url, File file, String params, String token) {
        return delegate.upload(url, file, params, token);
    }

    public static <T> String upload(String url, File file, Map<String, T> params) {
        return delegate.upload(url, file, params);
    }

    public static <T> String upload(String url, File file, Map<String, T> params, String token) {
        return delegate.upload(url, file, params, token);
    }

    public static boolean isPresent(String className, ClassLoader classLoader) {
        try {
            Class.forName(className, true, classLoader);
            return true;
        }
        catch (Throwable ex) {
            return false;
        }
    }

    static {
        OkHttp3Delegate delegateToUse = null;
        if (HttpUtils.isPresent("okhttp3.OkHttpClient", HttpUtils.class.getClassLoader())) {
            delegateToUse = new OkHttp3Delegate();
        }
        delegate = delegateToUse;
    }

    private static class OkHttp3Delegate
    implements HttpDelegate {
        private OkHttpClient httpClient = new OkHttpClient().newBuilder().connectTimeout(10L, TimeUnit.SECONDS).writeTimeout(10L, TimeUnit.SECONDS).readTimeout(30L, TimeUnit.SECONDS).build();

        private static MediaType getMediaType() {
            return MediaType.parse(ContentType);
        }

        private String exec(Request request) {
            try {
                Response response = this.httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String url = response.request().url().toString();
                    if (url.endsWith("core/module/sys/login") && !response.headers("Set-Cookie").isEmpty()) {
                        ArrayList<String> cookies = new ArrayList<String>(response.headers("Set-Cookie"));
                        StringBuilder cookieHeader = new StringBuilder();
                        int size = cookies.size();
                        for (int i = 0; i < size; ++i) {
                            if (i > 0) {
                                cookieHeader.append(";");
                            }
                            cookieHeader.append(cookies.get(i));
                        }
                        return cookieHeader.toString();
                    }
                    return response.body().string();
                }
                throw new RuntimeException("Unexpected code " + response);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String get(String url) {
            return this.get(url, "");
        }

        @Override
        public String get(String url, Map<String, String> headers) {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
            HttpUrl httpUrl = urlBuilder.build();
            Request.Builder requestBuilder = new Request.Builder().url(httpUrl).get();
            if (headers != null && headers.size() > 0) {
                for (String key : headers.keySet()) {
                    String value = headers.get(key);
                    if (StringUtils.isEmpty(value)) continue;
                    requestBuilder.addHeader(key, value);
                }
            }
            Request request = requestBuilder.build();
            return this.exec(request);
        }

        @Override
        public String get(String url, String cookie) {
            Request request = StringUtils.isEmpty(cookie) ? new Request.Builder().url(url).get().build() : new Request.Builder().url(url).addHeader("Cookie", cookie).get().build();
            return this.exec(request);
        }

        @Override
        public <T> String get(String url, Map<String, T> queryParas, String cookie) {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
            if (queryParas != null && queryParas.size() > 0) {
                for (Map.Entry<String, T> entry : queryParas.entrySet()) {
                    urlBuilder.addQueryParameter(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
            HttpUrl httpUrl = urlBuilder.build();
            Request request = StringUtils.isEmpty(cookie) ? new Request.Builder().url(httpUrl).get().build() : new Request.Builder().url(httpUrl).addHeader("Cookie", cookie).get().build();
            return this.exec(request);
        }

        @Override
        public String post(String url, String params) {
            return this.post(url, params, "");
        }

        @Override
        public <T> String post(String url, Map<String, T> params) {
            return this.post(url, params, "");
        }

        @Override
        public <T> String post(String url, Map<String, T> params, String cookie) {
            FormBody.Builder builder = new FormBody.Builder();
            if (params != null) {
                for (Map.Entry<String, T> entry : params.entrySet()) {
                    builder.add(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
            FormBody body = builder.build();
            Request request = StringUtils.isEmpty(cookie) ? new Request.Builder().url(url).post(body).build() : new Request.Builder().url(url).addHeader("Cookie", cookie).post(body).build();
            return this.exec(request);
        }

        @Override
        public <T> String post(String url, Map<String, T> params, Map<String, String> headers) {
            FormBody.Builder builder = new FormBody.Builder();
            if (params != null) {
                for (Map.Entry<String, T> entry : params.entrySet()) {
                    builder.add(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
            FormBody body = builder.build();
            Request.Builder requestBuilder = new Request.Builder().url(url).post(body);
            if (headers != null && headers.size() > 0) {
                for (String key : headers.keySet()) {
                    String value = headers.get(key);
                    if (StringUtils.isEmpty(value)) continue;
                    requestBuilder.addHeader(key, value);
                }
            }
            Request request = requestBuilder.build();
            return this.exec(request);
        }

        @Override
        public String post(String url, String params, String cookie) {
            if (params == null) {
                params = "";
            }
            RequestBody body = RequestBody.create(OkHttp3Delegate.getMediaType(), params);
            Request request = StringUtils.isEmpty(cookie) ? new Request.Builder().url(url).post(body).build() : new Request.Builder().url(url).addHeader("Cookie", cookie).post(body).build();
            return this.exec(request);
        }

        @Override
        public String postSSL(String url, String data, String certPath, String certPass) {
            return this.postSSL(url, data, certPath, certPass, "");
        }

        @Override
        public InputStream download(String url) {
            return this.download(url, "", "");
        }

        @Override
        public InputStream download(String url, String params) {
            return this.download(url, params, "");
        }

        @Override
        public <T> InputStream download(String url, Map<String, T> params) {
            return this.download(url, params, "");
        }

        @Override
        public InputStream download(String url, String params, String cookie) {
            Request request;
            if (!StringUtils.isEmpty(params)) {
                RequestBody body = RequestBody.create(OkHttp3Delegate.getMediaType(), params);
                request = StringUtils.isEmpty(cookie) ? new Request.Builder().url(url).post(body).build() : new Request.Builder().url(url).addHeader("Cookie", cookie).post(body).build();
            } else {
                request = StringUtils.isEmpty(cookie) ? new Request.Builder().url(url).get().build() : new Request.Builder().url(url).addHeader("Cookie", cookie).get().build();
            }
            try {
                Response response = this.httpClient.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Unexpected code " + response);
                }
                return response.body().byteStream();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public <T> InputStream download(String url, Map<String, T> params, String cookie) {
            Request request;
            if (params != null) {
                FormBody.Builder builder = new FormBody.Builder();
                for (Map.Entry<String, T> entry : params.entrySet()) {
                    builder.add(entry.getKey(), String.valueOf(entry.getValue()));
                }
                FormBody body = builder.build();
                request = StringUtils.isEmpty(cookie) ? new Request.Builder().url(url).post(body).build() : new Request.Builder().url(url).addHeader("Cookie", cookie).post(body).build();
            } else {
                request = StringUtils.isEmpty(cookie) ? new Request.Builder().url(url).get().build() : new Request.Builder().url(url).addHeader("Cookie", cookie).get().build();
            }
            try {
                Response response = this.httpClient.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Unexpected code " + response);
                }
                return response.body().byteStream();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String postSSL(String url, String data, String certPath, String certPass, String cookie) {
            String string;
            RequestBody body = RequestBody.create(OkHttp3Delegate.getMediaType(), data);
            Request request = StringUtils.isEmpty(cookie) ? new Request.Builder().url(url).post(body).build() : new Request.Builder().url(url).addHeader("Cookie", cookie).post(body).build();
            FileInputStream inputStream = null;
            try {
                KeyStore clientStore = KeyStore.getInstance("PKCS12");
                inputStream = new FileInputStream(certPath);
                char[] passArray = certPass.toCharArray();
                clientStore.load(inputStream, passArray);
                KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                kmf.init(clientStore, passArray);
                KeyManager[] kms = kmf.getKeyManagers();
                SSLContext sslContext = SSLContext.getInstance("TLSv1");
                sslContext.init(kms, null, new SecureRandom());
                OkHttpClient httpsClient = new OkHttpClient().newBuilder().connectTimeout(10L, TimeUnit.SECONDS).writeTimeout(10L, TimeUnit.SECONDS).readTimeout(30L, TimeUnit.SECONDS).sslSocketFactory(sslContext.getSocketFactory()).build();
                Response response = httpsClient.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Unexpected code " + response);
                }
                string = response.body().string();
            }
            catch (Exception e) {
                try {
                    throw new RuntimeException(e);
                }
                catch (Throwable throwable) {
                    IOUtils.closeQuietly(inputStream);
                    throw throwable;
                }
            }
            IOUtils.closeQuietly(inputStream);
            return string;
        }

        @Override
        public <T> String postSSL(String url, Map<String, T> params, String certPath, String certPass, String cookie) {
            String string;
            FormBody.Builder builder = new FormBody.Builder();
            if (params != null) {
                for (Map.Entry<String, T> entry : params.entrySet()) {
                    builder.add(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
            FormBody body = builder.build();
            Request request = StringUtils.isEmpty(cookie) ? new Request.Builder().url(url).post(body).build() : new Request.Builder().url(url).addHeader("Cookie", cookie).post(body).build();
            FileInputStream inputStream = null;
            try {
                KeyStore clientStore = KeyStore.getInstance("PKCS12");
                inputStream = new FileInputStream(certPath);
                char[] passArray = certPass.toCharArray();
                clientStore.load(inputStream, passArray);
                KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                kmf.init(clientStore, passArray);
                KeyManager[] kms = kmf.getKeyManagers();
                SSLContext sslContext = SSLContext.getInstance("TLSv1");
                sslContext.init(kms, null, new SecureRandom());
                OkHttpClient httpsClient = new OkHttpClient().newBuilder().connectTimeout(10L, TimeUnit.SECONDS).writeTimeout(10L, TimeUnit.SECONDS).readTimeout(30L, TimeUnit.SECONDS).sslSocketFactory(sslContext.getSocketFactory()).build();
                Response response = httpsClient.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Unexpected code " + response);
                }
                string = response.body().string();
            }
            catch (Exception e) {
                try {
                    throw new RuntimeException(e);
                }
                catch (Throwable throwable) {
                    IOUtils.closeQuietly(inputStream);
                    throw throwable;
                }
            }
            IOUtils.closeQuietly(inputStream);
            return string;
        }

        @Override
        public String put(String url, String params) {
            return this.put(url, params, "");
        }

        @Override
        public <T> String put(String url, Map<String, T> params) {
            return this.put(url, params, "");
        }

        @Override
        public <T> String put(String url, Map<String, T> params, String cookie) {
            FormBody.Builder builder = new FormBody.Builder();
            if (params != null) {
                for (Map.Entry<String, T> entry : params.entrySet()) {
                    builder.add(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
            FormBody body = builder.build();
            Request request = StringUtils.isEmpty(cookie) ? new Request.Builder().url(url).put(body).build() : new Request.Builder().url(url).addHeader("Cookie", cookie).put(body).build();
            return this.exec(request);
        }

        @Override
        public String put(String url, String params, String cookie) {
            if (params == null) {
                params = "";
            }
            RequestBody body = RequestBody.create(OkHttp3Delegate.getMediaType(), params);
            Request request = StringUtils.isEmpty(cookie) ? new Request.Builder().url(url).put(body).build() : new Request.Builder().url(url).addHeader("Cookie", cookie).put(body).build();
            return this.exec(request);
        }

        @Override
        public String delete(String url, String params) {
            return this.delete(url, params, "");
        }

        @Override
        public <T> String delete(String url, Map<String, T> params) {
            return this.delete(url, params, "");
        }

        @Override
        public <T> String delete(String url, Map<String, T> params, String cookie) {
            FormBody.Builder builder = new FormBody.Builder();
            if (params != null) {
                for (Map.Entry<String, T> entry : params.entrySet()) {
                    builder.add(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
            FormBody body = builder.build();
            Request request = StringUtils.isEmpty(cookie) ? new Request.Builder().url(url).delete(body).build() : new Request.Builder().url(url).addHeader("Cookie", cookie).delete(body).build();
            return this.exec(request);
        }

        @Override
        public String delete(String url, String params, String cookie) {
            if (params == null) {
                params = "";
            }
            RequestBody body = RequestBody.create(OkHttp3Delegate.getMediaType(), params);
            Request request = StringUtils.isEmpty(cookie) ? new Request.Builder().url(url).delete(body).build() : new Request.Builder().url(url).addHeader("Cookie", cookie).delete(body).build();
            return this.exec(request);
        }

        @Override
        public String upload(String url, File file, String params) {
            return this.upload(url, file, params, "");
        }

        @Override
        public String upload(String url, File file, String params, String cookie) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", file.getName(), fileBody);
            if (!StringUtils.isEmpty(params)) {
                builder.addFormDataPart("description", params);
            }
            MultipartBody requestBody = builder.build();
            Request request = StringUtils.isEmpty(cookie) ? new Request.Builder().url(url).post(requestBody).build() : new Request.Builder().url(url).addHeader("Cookie", cookie).post(requestBody).build();
            return this.exec(request);
        }

        @Override
        public <T> String upload(String url, File file, Map<String, T> params) {
            return this.upload(url, file, params, null);
        }

        @Override
        public <T> String upload(String url, File file, Map<String, T> params, String cookie) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", file.getName(), fileBody);
            if (params != null) {
                for (Map.Entry<String, T> entry : params.entrySet()) {
                    builder.addFormDataPart(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
            MultipartBody requestBody = builder.build();
            Request request = StringUtils.isEmpty(cookie) ? new Request.Builder().url(url).post(requestBody).build() : new Request.Builder().url(url).addHeader("Cookie", cookie).post(requestBody).build();
            return this.exec(request);
        }
    }

    private static interface HttpDelegate {
        public String get(String var1);

        public String get(String var1, String var2);

        public String get(String var1, Map<String, String> var2);

        public <T> String get(String var1, Map<String, T> var2, String var3);

        public String post(String var1, String var2);

        public String post(String var1, String var2, String var3);

        public <T> String post(String var1, Map<String, T> var2);

        public <T> String post(String var1, Map<String, T> var2, Map<String, String> var3);

        public <T> String post(String var1, Map<String, T> var2, String var3);

        public String postSSL(String var1, String var2, String var3, String var4);

        public String postSSL(String var1, String var2, String var3, String var4, String var5);

        public <T> String postSSL(String var1, Map<String, T> var2, String var3, String var4, String var5);

        public String put(String var1, String var2);

        public String put(String var1, String var2, String var3);

        public <T> String put(String var1, Map<String, T> var2);

        public <T> String put(String var1, Map<String, T> var2, String var3);

        public String delete(String var1, String var2);

        public String delete(String var1, String var2, String var3);

        public <T> String delete(String var1, Map<String, T> var2);

        public <T> String delete(String var1, Map<String, T> var2, String var3);

        public InputStream download(String var1);

        public InputStream download(String var1, String var2);

        public <T> InputStream download(String var1, Map<String, T> var2);

        public InputStream download(String var1, String var2, String var3);

        public <T> InputStream download(String var1, Map<String, T> var2, String var3);

        public String upload(String var1, File var2, String var3);

        public String upload(String var1, File var2, String var3, String var4);

        public <T> String upload(String var1, File var2, Map<String, T> var3);

        public <T> String upload(String var1, File var2, Map<String, T> var3, String var4);
    }
}
