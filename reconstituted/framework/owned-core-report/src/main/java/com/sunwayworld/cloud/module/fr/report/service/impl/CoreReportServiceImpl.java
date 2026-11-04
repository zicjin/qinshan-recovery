/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.fr.report.service.impl;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.cloud.module.fr.report.service.CoreReportConfigLineService;
import com.sunwayworld.cloud.module.fr.report.service.CoreReportService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.exception.NetworkException;
import com.sunwayworld.framework.exception.NotImplementedException;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.net.ssl.SSLContext;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreReportServiceImpl
implements CoreReportService {
    private static final Logger logger = LogManager.getLogger(CoreReportService.class);
    @Autowired
    private CoreReportConfigLineService reportConfigLineService;
    @Autowired
    private CoreFileManager fileManager;

    public CoreReportConfigLineService getCoreReportConfigLineService() {
        return this.reportConfigLineService;
    }

    @Override
    public <T> List<Pair<CoreReportConfigLineBean, String>> exportByConfigId(String exportType, String reportConfigId, T t) {
        return this.export(exportType, null, reportConfigId, null, t);
    }

    public <T> Pair<CoreReportConfigLineBean, String> exportByConfigLineId(String exportType, Long reportConfigLineId, T t) {
        return this.exportByConfigLineId(exportType, null, reportConfigLineId, t);
    }

    @Override
    public <T> Pair<CoreReportConfigLineBean, String> exportByConfigLineId(String exportType, String exportFileName, Long reportConfigLineId, T t) {
        List<Pair<CoreReportConfigLineBean, String>> pairList = this.export(exportType, exportFileName, null, reportConfigLineId, t);
        if (!pairList.isEmpty()) {
            return pairList.get(0);
        }
        return null;
    }

    @Override
    public String export(String exportType, Long reportConfigLineId, String json) {
        Map parameter = null;
        parameter = StringUtils.isEmpty(json) ? new HashMap() : JSON.parseObject(json, Map.class);
        return this.export(exportType, reportConfigLineId, parameter);
    }

    public String export(String exportType, Long reportConfigLineId, Map<String, Object> parameter) {
        CoreReportConfigLineBean report = (CoreReportConfigLineBean)this.getCoreReportConfigLineService().selectDetail(reportConfigLineId);
        if (null == report) {
            return null;
        }
        String exportFileName = (parameter.containsKey("frExportFileName") ? parameter.get("frExportFileName") : DateTimeUtils.getDateTimeSerialNumber(LocalDateTime.now())) + "." + exportType;
        FilePathDTO filePath = FilePathDTO.of(FileScope.export.name(), "" + System.currentTimeMillis(), UUID.randomUUID().toString(), exportFileName);
        Path path = FilePathManager.getLocalPath(filePath);
        FileUtils.makeDirs(path);
        String cptPath = report.getReportConfigLineNo() + File.separator + report.getExt$Item("filename");
        String cptCategory = report.getReportCategory();
        this.exportByFine(cptCategory, cptPath, exportType, filePath, parameter);
        this.fileManager.upload(filePath, path);
        return this.fileManager.getDownloadUrl(filePath);
    }

    protected void exportByFine(String cptCategory, String cptPath, String exportType, FilePathDTO filePath, Map<String, Object> parameter) {
        if (!"independent".equals(ApplicationContextHelper.getEnvironment().getProperty("sunway.config.fine.deploy-mode"))) {
            throw new NotImplementedException();
        }
        String deployServerUrl = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.fine.independent-deploy-server.url");
        String fineVersion = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.fine.version");
        String viewUrl = "";
        if ("11.0".equals(fineVersion)) {
            viewUrl = deployServerUrl + "/decision/view/report?viewlet=";
        } else if ("10.0".equals(fineVersion)) {
            viewUrl = deployServerUrl + "/decision/view/report?viewlet=";
        } else if ("9.0".equals(fineVersion)) {
            viewUrl = deployServerUrl + "/ReportServer?reportlet=";
        }
        StringBuilder paramSb = new StringBuilder();
        if (!CollectionUtils.isEmpty(parameter)) {
            parameter.forEach((k, v) -> paramSb.append("&").append((String)k).append("=").append(v));
        }
        String urlSuffix = null;
        switch (exportType) {
            case "pdf": {
                urlSuffix = "&format=pdf";
                break;
            }
            case "doc": {
                urlSuffix = "&format=word";
                break;
            }
            case "xlsx": {
                urlSuffix = "&format=excel&extype=simple";
                break;
            }
            case "png": {
                urlSuffix = "&format=image&extype=PNG";
            }
        }
        String exportUrl = viewUrl + StringUtils.encodeURL(cptPath + paramSb) + urlSuffix;
        exportUrl = exportUrl.replaceAll("%26", "&");
        exportUrl = exportUrl.replaceAll("%3D", "=");
        Path path = FilePathManager.getLocalPath(filePath);
        File file = path.toFile();
        FileUtils.makeDirs(file.toPath());
        try (CloseableHttpClient httpClient = this.getClient(exportUrl);){
            HttpGet httpGet = new HttpGet(exportUrl);
            try (CloseableHttpResponse response = httpClient.execute(httpGet);
                 InputStream is = response.getEntity().getContent();){
                BufferedInputStream bis = new BufferedInputStream(is);
                FileUtils.createFile(file);
                try (FileOutputStream fos = new FileOutputStream(file);){
                    byte[] buffer = new byte[4096048];
                    int size = 0;
                    while ((size = bis.read(buffer)) != -1) {
                        fos.write(buffer, 0, size);
                    }
                }
                catch (IOException ioe) {
                    throw new FileException(ioe);
                }
            }
        }
        catch (IOException ioe) {
            throw new NetworkException(ioe);
        }
    }

    private <T> List<Pair<CoreReportConfigLineBean, String>> export(String exportType, String exportFileName, String reportConfigId, Long reportConfigLineId, T t) {
        List<CoreReportConfigLineBean> reportLineList = null;
        reportLineList = null == reportConfigLineId ? this.reportConfigLineService.selectListByFilter(SearchFilter.instance().match("REPORTCONFIGID", reportConfigId).filter(MatchPattern.EQ).match("ACTIVATEDFLAG", "1").filter(MatchPattern.EQ), new Order[0]).stream().filter(cl -> !StringUtils.isEmpty(cl.getExt$Item("fileid"))).collect(Collectors.toList()) : ArrayUtils.asList((CoreReportConfigLineBean)this.reportConfigLineService.selectDetail(reportConfigLineId));
        if (reportLineList.isEmpty()) {
            return CollectionUtils.emptyList();
        }
        HashMap<String, Object> parameter = new HashMap<String, Object>();
        if (t instanceof Map) {
            parameter.putAll((Map)t);
        } else {
            parameter.putAll(BeanUtils.deeplyToMap(t));
        }
        if (!StringUtils.isEmpty(exportFileName)) {
            parameter.put("frExportFileName", exportFileName);
        }
        return reportLineList.stream().map(r -> {
            String exportUrl = this.export(exportType, r.getId(), parameter);
            return Pair.of(r, exportUrl);
        }).collect(Collectors.toList());
    }

    private CloseableHttpClient getClient(String url) {
        if (StringUtils.startsWithIgnoreCase(url, "https:")) {
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

    private RequestConfig getDefaultRequestConfig() {
        return RequestConfig.custom().setSocketTimeout(ApplicationContextHelper.getEnvironment().getProperty("sunway.http-client.socket-timeout", Integer.class, 30000)).setConnectTimeout(ApplicationContextHelper.getEnvironment().getProperty("sunway.http-client.connect-timeout", Integer.class, 3000)).build();
    }
}
