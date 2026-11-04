/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.http;

import com.sunwayworld.framework.http.BaseHttpClient;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpSimpleClient
extends BaseHttpClient {
    private static final Logger logger = LogManager.getLogger(HttpSimpleClient.class);
    private static volatile HttpSimpleClient instance;

    private HttpSimpleClient() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static final HttpSimpleClient getInstance() {
        if (instance != null) return instance;
        Class<HttpSimpleClient> clazz = HttpSimpleClient.class;
        synchronized (HttpSimpleClient.class) {
            if (instance != null) return instance;
            instance = new HttpSimpleClient();
            // ** MonitorExit[var0] (shouldn't be in output)
            return instance;
        }
    }

    @Override
    public CloseableHttpClient getHttpClient() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy(){

                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(sslContext);
            return HttpClients.custom().setDefaultRequestConfig(this.getDefaultRequestConfig()).setSSLSocketFactory(sslFactory).build();
        }
        catch (Exception e) {
            logger.warn("TRUST ALL CERTS EXCEPTION");
            return HttpClients.custom().setDefaultRequestConfig(this.getDefaultRequestConfig()).build();
        }
    }
}
