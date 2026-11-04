/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.http;

import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.beans.BeanPropertyHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.exception.NetworkException;
import com.sunwayworld.framework.http.HttpClientInterface;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

public abstract class BaseHttpClient
implements HttpClientInterface {
    private static final String DEFAULT_CHARSET_NAME = "UTF-8";

    abstract CloseableHttpClient getHttpClient();

    protected RequestConfig getDefaultRequestConfig() {
        return RequestConfig.custom().setSocketTimeout(ApplicationContextHelper.getEnvironment().getProperty("sunway.http-client.socket-timeout", Integer.class, 30000)).setConnectTimeout(ApplicationContextHelper.getEnvironment().getProperty("sunway.http-client.connect-timeout", Integer.class, 3000)).build();
    }

    /*
     * Exception decompiling
     */
    @Override
    public final String sendHttpGet(String url, Consumer<HttpGet> consumer) {
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

    /*
     * Exception decompiling
     */
    @Override
    public final String sendHttpPost(String url, String jsonString, Consumer<HttpPost> consumer) {
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

    /*
     * Exception decompiling
     */
    @Override
    public final String sendHttpPut(String url, String jsonString, Consumer<HttpPut> consumer) {
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

    /*
     * Exception decompiling
     */
    @Override
    public final String sendHttpDelete(String url, Consumer<HttpDelete> consumer) {
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

    /*
     * Exception decompiling
     */
    @Override
    public final String sendHttpPatch(String url, String jsonString, Consumer<HttpPatch> consumer) {
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

    @Override
    public final String upload(String targetUrl, MultipartFile multipartFile) {
        return this.upload(targetUrl, multipartFile, null);
    }

    @Override
    public final String upload(String targetUrl, File file) {
        return this.upload(targetUrl, file, null);
    }

    @Override
    public final String upload(String targetUrl, String fileName, InputStream is) {
        return this.upload(targetUrl, fileName, is, null);
    }

    @Override
    public final String upload(String targetUrl, String fileName, String sourceUrl) {
        return this.upload(targetUrl, fileName, sourceUrl, null);
    }

    @Override
    public final <T> String upload(String targetUrl, MultipartFile multipartFile, @Nullable T item) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        try {
            builder.addBinaryBody("file", multipartFile.getInputStream(), ContentType.DEFAULT_BINARY, this.getOriginalFilename(multipartFile.getOriginalFilename()));
        }
        catch (IOException ioe) {
            throw new FileException(ioe);
        }
        if (item != null) {
            List<BeanPropertyDescriptor> propertyDescriptorList = BeanPropertyHelper.getBeanPropertyDescriptorList(item.getClass());
            for (BeanPropertyDescriptor propertyDescriptor : propertyDescriptorList) {
                String strValue;
                Object value = propertyDescriptor.getPropertyValue(item);
                if (value == null || !ConvertUtils.canConvert(value, String.class) || StringUtils.isBlank(strValue = ConvertUtils.convert(value, String.class))) continue;
                builder.addPart(propertyDescriptor.getName(), new StringBody(strValue, ContentType.create("text/plain", Consts.UTF_8)));
            }
        }
        return this.upload(targetUrl, builder);
    }

    @Override
    public final <T> String upload(String targetUrl, File file, @Nullable T item) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", file);
        if (item != null) {
            List<BeanPropertyDescriptor> propertyDescriptorList = BeanPropertyHelper.getBeanPropertyDescriptorList(item.getClass());
            for (BeanPropertyDescriptor propertyDescriptor : propertyDescriptorList) {
                String strValue;
                Object value = propertyDescriptor.getPropertyValue(item);
                if (value == null || !ConvertUtils.canConvert(value, String.class) || StringUtils.isBlank(strValue = ConvertUtils.convert(value, String.class))) continue;
                builder.addPart(propertyDescriptor.getName(), new StringBody(strValue, ContentType.create("text/plain", Consts.UTF_8)));
            }
        }
        return this.upload(targetUrl, builder);
    }

    @Override
    public final <T> String upload(String targetUrl, String fileName, InputStream is, @Nullable T item) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", is, ContentType.DEFAULT_BINARY, fileName);
        if (item != null) {
            List<BeanPropertyDescriptor> propertyDescriptorList = BeanPropertyHelper.getBeanPropertyDescriptorList(item.getClass());
            for (BeanPropertyDescriptor propertyDescriptor : propertyDescriptorList) {
                String strValue;
                Object value = propertyDescriptor.getPropertyValue(item);
                if (value == null || !ConvertUtils.canConvert(value, String.class) || StringUtils.isBlank(strValue = ConvertUtils.convert(value, String.class))) continue;
                builder.addPart(propertyDescriptor.getName(), new StringBody(strValue, ContentType.create("text/plain", Consts.UTF_8)));
            }
        }
        return this.upload(targetUrl, builder);
    }

    /*
     * Exception decompiling
     */
    @Override
    public final <T> String upload(String targetUrl, String fileName, String sourceUrl, @Nullable T item) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         *
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 5 blocks at once
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

    @Override
    public final void download(String sourceUrl, String targetPath) {
        try (CloseableHttpClient httpClient = this.getHttpClient();){
            HttpGet httpGet = new HttpGet(sourceUrl);
            try (CloseableHttpResponse response = httpClient.execute(httpGet);
                 InputStream is = response.getEntity().getContent();){
                File target = new File(targetPath);
                FileUtils.createFile(target);
                FileUtils.write(target, is);
            }
        }
        catch (IOException ioe) {
            throw new NetworkException(ioe);
        }
    }

    @Override
    public final void download(String sourceUrl, File targetFile) {
        FileUtils.makeDirs(targetFile.toPath());
        try (CloseableHttpClient httpClient = this.getHttpClient();){
            HttpGet httpGet = new HttpGet(sourceUrl);
            try (CloseableHttpResponse response = httpClient.execute(httpGet);
                 InputStream is = response.getEntity().getContent();){
                FileUtils.write(targetFile, is);
            }
        }
        catch (IOException ioe) {
            throw new NetworkException(ioe);
        }
    }

    /*
     * Exception decompiling
     */
    @Override
    public InputStream download(String url) {
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

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private final String upload(String targetUrl, MultipartEntityBuilder builder) {
        try (CloseableHttpClient httpClient = this.getHttpClient();){
            int statusCode;
            Throwable throwable;
            CloseableHttpResponse response;
            block25: {
                String string;
                block26: {
                    HttpPost httpPost = new HttpPost(targetUrl);
                    HttpEntity multipartEntity = builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE).setCharset(StandardCharsets.UTF_8).build();
                    httpPost.setEntity(multipartEntity);
                    response = httpClient.execute(httpPost);
                    throwable = null;
                    statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode != 200) break block25;
                    string = EntityUtils.toString(response.getEntity());
                    if (response == null) return string;
                    if (throwable == null) break block26;
                    try {
                        response.close();
                        return string;
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                        return string;
                    }
                }
                response.close();
                return string;
            }
            try {
                try {
                    throw new NetworkException(statusCode);
                }
                catch (Throwable throwable3) {
                    throwable = throwable3;
                    throw throwable3;
                }
            }
            catch (Throwable throwable4) {
                if (response == null) throw throwable4;
                if (throwable == null) {
                    response.close();
                    throw throwable4;
                }
                try {
                    response.close();
                    throw throwable4;
                }
                catch (Throwable throwable5) {
                    throwable.addSuppressed(throwable5);
                    throw throwable4;
                }
            }
        }
        catch (IOException ioe) {
            throw new NetworkException(ioe);
        }
    }

    private String getOriginalFilename(String filename) {
        int index = filename.lastIndexOf("\\");
        if (index >= 0) {
            return filename.substring(index + 1);
        }
        index = filename.lastIndexOf("/");
        if (index > 0) {
            return filename.substring(index + 1);
        }
        return filename;
    }
}
