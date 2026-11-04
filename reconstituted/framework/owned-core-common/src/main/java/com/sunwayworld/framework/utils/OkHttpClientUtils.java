/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.exception.NetworkException;
import com.sunwayworld.framework.exception.UnexpectedException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpClientUtils {
    public static Response execute(Request request) {
        OkHttpClient httpClient = null;
        httpClient = request.isHttps() ? OkHttpClientUtils.getHttpsClient().build() : new OkHttpClient();
        try {
            return httpClient.newCall(request).execute();
        }
        catch (IOException ex) {
            throw new NetworkException(ex);
        }
    }

    private static OkHttpClient.Builder getHttpsClient() {
        try {
            TrustManager[] trustAllCerts = OkHttpClientUtils.getTrustManagers();
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
            return builder;
        }
        catch (KeyManagementException | NoSuchAlgorithmException ex) {
            throw new UnexpectedException(ex);
        }
    }

    private static TrustManager[] getTrustManagers() {
        return new TrustManager[]{new X509TrustManager(){

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};
    }
}
