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
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.NoSuchFileException;
import java.time.LocalDateTime;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@GikamBean
@Component
@Order(value=1)
public class CoreFileFtpFilter
implements Filter {
    private static String DOWNLOAD_URL_PREFIX = "/" + "files" + "/";
    @Autowired
    private CoreFileManager fileManager;
    @Autowired
    private CoreFileService fileService;

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
            Path fileLocalPath = FilePathManager.getLocalPath(filePath);
            if (!Files.exists(fileLocalPath, new LinkOption[0])) {
                if (!download(filePath, response)) return;
            } else {
                CoreFileBean file;
                String[] values = StringUtils.split(StringUtils.decodeURL(requestUri), "/");
                String fileMd5Name = "";
                if (values.length == 6) {
                    fileMd5Name = values[4];
                }
                if (values.length == 5) {
                    fileMd5Name = values[3];
                }
                if (null == (file = (CoreFileBean)this.fileService.selectFirstByFilter(SearchFilter.instance().match("MD5NAME", fileMd5Name).filter(MatchPattern.SEQ), com.sunwayworld.framework.database.sql.Order.desc("ID")))) {
                    if (!download(filePath, response)) return;
                } else {
                    File localFile = fileLocalPath.toFile();
                    LocalDateTime localFileTime = ConvertUtils.convert(localFile.lastModified(), LocalDateTime.class);
                    if (DateTimeUtils.isBefore(localFileTime, file.getCreatedTime())) {
                        if (!download(filePath, response)) return;
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }

    private boolean download(FilePathDTO filePath, ServletResponse response) throws IOException {
        try {
            this.fileManager.download(filePath);
            return true;
        } catch (UncheckedIOException failure) {
            if (!(failure.getCause() instanceof NoSuchFileException)) throw failure;
            ((HttpServletResponse)response).sendError(HttpServletResponse.SC_NOT_FOUND);
            return false;
        }
    }
}
