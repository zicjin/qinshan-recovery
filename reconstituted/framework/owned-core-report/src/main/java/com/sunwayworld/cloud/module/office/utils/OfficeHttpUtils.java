/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.utils;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.utils.StringUtils;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OfficeHttpUtils {
    private static final Logger logger = LogManager.getLogger(OfficeHttpUtils.class);
    private static final String DEFAULT_CHARSET_NAME = "UTF-8";
    private static final String APPLICATION_JSON = "application/json";

    /*
     * Exception decompiling
     */
    public static String sendOfficeHttpPost(String url, String jsonString) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         *
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 3 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private static RequestConfig getDefaultRequestConfig() {
        return RequestConfig.custom().setSocketTimeout(ApplicationContextHelper.getEnvironment().getProperty("sunway.http-client.socket-timeout", Integer.class, 30000)).setConnectTimeout(ApplicationContextHelper.getEnvironment().getProperty("sunway.http-client.connect-timeout", Integer.class, 3000)).build();
    }

    private static CloseableHttpClient getHttpClient() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy(){

                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(sslContext);
            return HttpClients.custom().setDefaultRequestConfig(OfficeHttpUtils.getDefaultRequestConfig()).setSSLSocketFactory(sslFactory).build();
        }
        catch (Exception e) {
            logger.warn("TRUST ALL CERTS EXCEPTION");
            return HttpClients.custom().setDefaultRequestConfig(OfficeHttpUtils.getDefaultRequestConfig()).build();
        }
    }

    private static CloseableHttpClient getHttpsClient() {
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

    private static CloseableHttpClient getClient(String url) {
        if (StringUtils.startsWithIgnoreCase(url, "https:")) {
            return OfficeHttpUtils.getHttpsClient();
        }
        return OfficeHttpUtils.getHttpClient();
    }
}
