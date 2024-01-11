package com.sunwayworld.emc.framework.word;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

import com.sunwayworld.cloud.gikam.fr.bean.report.CoreReportConfigLineBean;
import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhengxy@sunwayworld.com 2020年12月14日
 */
public class ReportingServiceUtils {

    private static final String HTTPS_PREFIX = "https://";

    /**
     * 获取 Reporting Service 报表下载地址
     * @param reportingServiceServerUrl 报表服务器地址
     * @param configLine 报表配置明细
     * @param source 参数取值源对象
     * @param format 文件格式，支持PDF、EXCEL
     * @return
     */
    public static String getDownloadUrl(String reportingServiceServerUrl, CoreReportConfigLineBean configLine, Object source, String format) {
        if (StringUtils.isEmpty(reportingServiceServerUrl) || StringUtils.isEmpty(format)) {
            return null;
        }

        StringBuilder reportUrl = new StringBuilder();
        String reportPath = configLine.getExt$Item("reportpath");

        String reportFilePath = reportPath.substring(0, reportPath.indexOf("&") > 0 ? reportPath.indexOf("&") : reportPath.length());
        try {
            reportUrl.append(reportingServiceServerUrl).append("?").append(URLEncoder.encode(reportFilePath, "UTF-8")).append("&rs:Command=Render&rs:Format=").append(format.toUpperCase());
            String paramStr = parseReportParam(source, configLine.getParam());
            if (!StringUtils.isEmpty(paramStr)) {
                reportUrl.append("&").append(paramStr);
            }

            return reportUrl.toString();
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * 解析报表参数，忽略参数名大小写
     */
    private static String parseReportParam(Object object, String param) {
        if (StringUtils.isEmpty(param)) {
            return "";
        }

        String[] values = StringUtils.split(param, "&");

        StringBuilder sb = new StringBuilder();

        if (values.length > 0) {
            Arrays.stream(values).forEach(arg -> {
                String[] pair = StringUtils.split(arg, "=");

                if (pair.length > 1) {
                    Object value = BeanUtils.getPropertyValue(object, pair[1]);

                    if (value != null) {
                        if (sb.length() > 0) {
                            sb.append("&");
                        }

                        sb.append(pair[0]).append("=").append(ObjectUtils.toString(value));
                    }
                }
            });
        }

        return sb.toString();
    }

    /**
     * 下载文件，并保存到指定目录下
     * @param url 下载地址
     * @param fileName 文件名
     * @return
     */
    public static File downloadFile(String url, String fileName) {
        File tempFile = FileHelper.createTempFile(fileName, 0L);
        if (StringUtils.startsWithIgnoreCase(url, HTTPS_PREFIX)) {
            HttpClientManager.getInstance().download(url, tempFile);
        } else {
        	HttpClientManager.getInstance().download(url, tempFile);
        }

        return tempFile;
    }
}
