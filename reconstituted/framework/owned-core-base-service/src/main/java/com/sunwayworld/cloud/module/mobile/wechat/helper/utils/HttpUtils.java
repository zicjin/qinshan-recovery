/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.utils;

import com.sunwayworld.cloud.module.mobile.wechat.helper.media.MediaFile;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.ClassUtils;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.IOUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class HttpUtils {
    private static HttpDelegate delegate;

    private HttpUtils() {
    }

    public static String get(String url) {
        return delegate.get(url);
    }

    public static String get(String url, Map<String, String> queryParas) {
        return delegate.get(url, queryParas);
    }

    public static String post(String url, String data) {
        return delegate.post(url, data);
    }

    public static String post(String url, Map<String, String> queryParas, String data) {
        return delegate.post(url, queryParas, data);
    }

    public static String postSSL(String url, String data, String certPath, String certPass) {
        return delegate.postSSL(url, data, certPath, certPass);
    }

    public static MediaFile download(String url) {
        return delegate.download(url);
    }

    public static InputStream download(String url, String params) {
        return delegate.download(url, params);
    }

    public static String upload(String url, File file, String params) {
        return delegate.upload(url, file, params);
    }

    public static void setHttpDelegate(HttpDelegate httpDelegate) {
        delegate = httpDelegate;
    }

    static {
        OkHttp3Delegate delegateToUse = null;
        if (ClassUtils.isPresent("okhttp3.OkHttpClient", HttpUtils.class.getClassLoader())) {
            delegateToUse = new OkHttp3Delegate();
        }
        delegate = delegateToUse;
    }

    private static class OkHttp3Delegate
    implements HttpDelegate {
        private OkHttpClient httpClient = new OkHttpClient().newBuilder().connectTimeout(10L, TimeUnit.SECONDS).writeTimeout(10L, TimeUnit.SECONDS).readTimeout(30L, TimeUnit.SECONDS).build();
        private static final MediaType CONTENT_TYPE_FORM = MediaType.parse("application/x-www-form-urlencoded");

        private String exec(Request request) {
            try {
                Response response = this.httpClient.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Unexpected code " + response);
                }
                return response.body().string();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String get(String url) {
            Request request = new Request.Builder().url(url).get().build();
            return this.exec(request);
        }

        @Override
        public String get(String url, Map<String, String> queryParas) {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
            for (Map.Entry<String, String> entry : queryParas.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
            HttpUrl httpUrl = urlBuilder.build();
            Request request = new Request.Builder().url(httpUrl).get().build();
            return this.exec(request);
        }

        @Override
        public String post(String url, String params) {
            RequestBody body = RequestBody.create(CONTENT_TYPE_FORM, params);
            Request request = new Request.Builder().url(url).post(body).build();
            return this.exec(request);
        }

        @Override
        public String post(String url, Map<String, String> queryParas, String params) {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
            for (Map.Entry<String, String> entry : queryParas.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
            HttpUrl httpUrl = urlBuilder.build();
            RequestBody body = RequestBody.create(CONTENT_TYPE_FORM, params);
            Request request = new Request.Builder().url(httpUrl).post(body).build();
            return this.exec(request);
        }

        @Override
        public String postSSL(String url, String data, String certPath, String certPass) {
            String string;
            RequestBody body = RequestBody.create(CONTENT_TYPE_FORM, data);
            Request request = new Request.Builder().url(url).post(body).build();
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
        public MediaFile download(String url) {
            Request request = new Request.Builder().url(url).get().build();
            try {
                Response response = this.httpClient.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Unexpected code " + response);
                }
                ResponseBody body = response.body();
                MediaType mediaType = body.contentType();
                MediaFile mediaFile = new MediaFile();
                if (mediaType.type().equals("text")) {
                    mediaFile.setError(body.string());
                } else {
                    BufferedInputStream bis = new BufferedInputStream(body.byteStream());
                    String ds = response.header("Content-disposition");
                    String fullName = ds.substring(ds.indexOf("filename=\"") + 10, ds.length() - 1);
                    String relName = fullName.substring(0, fullName.lastIndexOf("."));
                    String suffix = fullName.substring(relName.length() + 1);
                    mediaFile.setFullName(fullName);
                    mediaFile.setFileName(relName);
                    mediaFile.setSuffix(suffix);
                    mediaFile.setContentLength(body.contentLength() + "");
                    mediaFile.setContentType(body.contentType().toString());
                    mediaFile.setFileStream(bis);
                }
                return mediaFile;
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public InputStream download(String url, String params) {
            Request request;
            if (!StringUtils.isBlank(params)) {
                RequestBody body = RequestBody.create(CONTENT_TYPE_FORM, params);
                request = new Request.Builder().url(url).post(body).build();
            } else {
                request = new Request.Builder().url(url).get().build();
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
        public String upload(String url, File file, String params) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("media", file.getName(), fileBody);
            if (!StringUtils.isBlank(params)) {
                builder.addFormDataPart("description", params);
            }
            MultipartBody requestBody = builder.build();
            Request request = new Request.Builder().url(url).post(requestBody).build();
            return this.exec(request);
        }
    }

    private static interface HttpDelegate {
        public String get(String var1);

        public String get(String var1, Map<String, String> var2);

        public String post(String var1, String var2);

        public String post(String var1, Map<String, String> var2, String var3);

        public String postSSL(String var1, String var2, String var3, String var4);

        public MediaFile download(String var1);

        public InputStream download(String var1, String var2);

        public String upload(String var1, File var2, String var3);
    }
}
