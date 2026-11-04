/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.manager.impl;

import com.sunwayworld.framework.exception.FtpException;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.FtpUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.core.CoreFileFtpProperties;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@GikamBean
@Repository
@EnableConfigurationProperties(value={CoreFileFtpProperties.class})
public class CoreFileFtpManagerImpl
implements CoreFileManager {
    @Autowired
    private CoreFileFtpProperties ftpProperties;

    @Override
    public String getDownloadUrl(FilePathDTO filePath) {
        return this.getSecuredDownloadUrl(ServletUtils.getUrl(null, FilePathManager.getUrl(filePath)), filePath);
    }

    @Override
    public String getAbsoluteDownloadUrl(FilePathDTO filePath) {
        String url = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return this.getSecuredDownloadUrl(ServletUtils.getUrl(url, FilePathManager.getUrl(filePath)), filePath);
    }

    @Override
    public void upload(FilePathDTO filePath, MultipartFile multipartFile) {
        try (InputStream is = multipartFile.getInputStream();){
            this.upload(filePath, is);
        }
        catch (IOException ioe) {
            throw new FtpException(ioe);
        }
    }

    @Override
    public void upload(FilePathDTO filePath, Path path) {
        try (InputStream is = Files.newInputStream(path, new OpenOption[0]);){
            this.upload(filePath, is);
        }
        catch (IOException ioe) {
            throw new FtpException(ioe);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void upload(FilePathDTO filePath, InputStream is) {
        FTPClient client = null;
        try {
            client = this.getFtpClient();
            FtpUtils.upload(client, filePath, is);
        }
        finally {
            FtpUtils.disconnect(client);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void download(FilePathDTO filePath, Path toFile) {
        FTPClient client = null;
        try {
            client = this.getFtpClient();
            FtpUtils.download(client, filePath, toFile);
        }
        finally {
            FtpUtils.disconnect(client);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void copyFile(FilePathDTO from, FilePathDTO to) {
        FTPClient client = null;
        try {
            client = this.getFtpClient();
            FtpUtils.copy(client, from, to);
        }
        finally {
            FtpUtils.disconnect(client);
        }
    }

    @Override
    public void duplicateFile(CoreFileBean from, String toFileName) {
        CoreFileBean to = ObjectUtils.clone(from);
        to.setName(toFileName);
        this.copyFile(from, to);
    }

    private FTPClient getFtpClient() {
        return FtpUtils.getFtpClient(this.ftpProperties.getIp(), this.ftpProperties.getPort(), this.ftpProperties.getUser(), this.ftpProperties.getPassword(), this.ftpProperties.getFolder());
    }
}
