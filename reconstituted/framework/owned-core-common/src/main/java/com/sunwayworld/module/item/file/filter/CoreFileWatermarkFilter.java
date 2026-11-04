/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.filter;

import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.SpireWatermarkUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.LocalDateTime;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@GikamBean
@Component
@Order(value=2)
public class CoreFileWatermarkFilter
implements Filter {
    private static String DOWNLOAD_URL_PREFIX = "/" + "files" + "/";
    @Lazy
    @Autowired
    private CoreFileService fileService;
    @Value(value="${sunway.config.file.watermark.enabled:false}")
    private Boolean watermarkEnable;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String requestUri;
        HttpServletRequest hsr = (HttpServletRequest)request;
        String method = hsr.getMethod();
        if ("GET".equals(method) && StringUtils.startsWith(requestUri = ServletUtils.getRequestUri(hsr), DOWNLOAD_URL_PREFIX)) {
            FilePathDTO filePath = FilePathManager.toFilePath(requestUri);
            if (filePath == null) {
                chain.doFilter(request, response);
                return;
            }
            filePath.setFileName(StringUtils.replace(filePath.getFileName(), "_REVERT_WATERMARK_BAK", ""));
            String fileBaseName = FileUtils.getFileBaseName(filePath.getFileName());
            String fileExt = FileUtils.getFileExtension(filePath.getFileName());
            String bakName = fileBaseName + "_REVERT_WATERMARK_BAK." + fileExt;
            FilePathDTO bakFilePathDTO = FilePathDTO.of(filePath.getScope().name(), filePath.getTimestamp(), filePath.getIrregularName(), bakName);
            Path bakFilePath = FilePathManager.getLocalPath(bakFilePathDTO);
            if (!Files.exists(bakFilePath, new LinkOption[0])) {
                if (this.watermarkEnable.booleanValue()) {
                    SpireWatermarkUtils.insertTextWatermark(filePath);
                }
            } else if (this.watermarkEnable.booleanValue()) {
                File bakFile;
                LocalDateTime bakFileTime;
                CoreFileBean file;
                String[] values = StringUtils.split(StringUtils.decodeURL(requestUri), "/");
                String fileMd5Name = "";
                if (values.length == 6) {
                    fileMd5Name = values[4];
                }
                if (values.length == 5) {
                    fileMd5Name = values[3];
                }
                if (null != (file = (CoreFileBean)this.fileService.selectFirstByFilter(SearchFilter.instance().match("MD5NAME", fileMd5Name).filter(MatchPattern.SEQ), com.sunwayworld.framework.database.sql.Order.desc("ID"))) && DateTimeUtils.isBefore(bakFileTime = ConvertUtils.convert((bakFile = bakFilePath.toFile()).lastModified(), LocalDateTime.class).plusSeconds(10L), file.getCreatedTime())) {
                    SpireWatermarkUtils.insertTextWatermark(filePath);
                }
            } else {
                SpireWatermarkUtils.revertBakSourceFile(filePath);
            }
        }
        chain.doFilter(request, response);
    }
}
