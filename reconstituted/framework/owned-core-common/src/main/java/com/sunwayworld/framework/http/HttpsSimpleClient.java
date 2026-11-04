/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.http;

import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.http.BaseHttpClient;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;

public class HttpsSimpleClient
extends BaseHttpClient {
    private static volatile HttpsSimpleClient instance;

    private HttpsSimpleClient() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static final HttpsSimpleClient getInstance() {
        if (instance != null) return instance;
        Class<HttpsSimpleClient> clazz = HttpsSimpleClient.class;
        synchronized (HttpsSimpleClient.class) {
            if (instance != null) return instance;
            instance = new HttpsSimpleClient();
            // ** MonitorExit[var0] (shouldn't be in output)
            return instance;
        }
    }

    @Override
    CloseableHttpClient getHttpClient() {
        try {
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy(){

                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            }).build();
            return HttpClients.custom().setSSLContext(sslContext).setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        }
        catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException ex) {
            throw new UnexpectedException(ex);
        }
    }
}
