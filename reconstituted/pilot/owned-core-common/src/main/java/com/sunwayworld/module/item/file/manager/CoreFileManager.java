/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.manager;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.security.authentication.AuthenticationHelper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public interface CoreFileManager {
    default public String getDownloadUrl(Long id) {
        CoreFileBean coreFile = (CoreFileBean)ApplicationContextHelper.getBean(CoreFileService.class).selectByIdIfPresent(id);
        if (coreFile == null) {
            return null;
        }
        return this.getDownloadUrl(coreFile);
    }

    default public String getAbsoluteDownloadUrl(Long id) {
        CoreFileBean coreFile = (CoreFileBean)ApplicationContextHelper.getBean(CoreFileService.class).selectByIdIfPresent(id);
        if (coreFile == null) {
            return null;
        }
        return this.getAbsoluteDownloadUrl(coreFile);
    }

    default public String getDownloadUrl(CoreFileBean coreFile) {
        FilePathDTO filePath = CoreFileUtils.toFilePath(coreFile);
        return this.getSecuredDownloadUrl(this.getDownloadUrl(filePath), filePath);
    }

    default public String getAbsoluteDownloadUrl(CoreFileBean coreFile) {
        FilePathDTO filePath = CoreFileUtils.toFilePath(coreFile);
        return this.getSecuredDownloadUrl(this.getAbsoluteDownloadUrl(filePath), filePath);
    }

    public String getDownloadUrl(FilePathDTO var1);

    public String getAbsoluteDownloadUrl(FilePathDTO var1);

    default public void upload(CoreFileBean coreFile, MultipartFile multipartFile) {
        this.upload(CoreFileUtils.toFilePath(coreFile), multipartFile);
    }

    default public void upload(CoreFileBean coreFile, Path path) {
        this.upload(CoreFileUtils.toFilePath(coreFile), path);
    }

    default public void upload(CoreFileBean coreFile, File file) {
        this.upload(CoreFileUtils.toFilePath(coreFile), file);
    }

    default public void upload(CoreFileBean coreFile, InputStream is) {
        this.upload(CoreFileUtils.toFilePath(coreFile), is);
    }

    public void upload(FilePathDTO var1, MultipartFile var2);

    public void upload(FilePathDTO var1, Path var2);

    default public void upload(FilePathDTO filePath, File file) {
        this.upload(filePath, file.toPath());
    }

    public void upload(FilePathDTO var1, InputStream var2);

    public void download(FilePathDTO var1, Path var2);

    default public void download(FilePathDTO filePath) {
        this.download(filePath, FilePathManager.getLocalPath(filePath));
    }

    default public void download(CoreFileBean coreFile, Path toFile) {
        this.download(CoreFileUtils.toFilePath(coreFile), toFile);
    }

    default public void download(CoreFileBean coreFile) {
        this.download(coreFile, FilePathManager.getLocalPath(CoreFileUtils.toFilePath(coreFile)));
    }

    public void copyFile(FilePathDTO var1, FilePathDTO var2);

    default public void copyFile(CoreFileBean from, CoreFileBean to) {
        this.copyFile(CoreFileUtils.toFilePath(from), CoreFileUtils.toFilePath(to));
    }

    public void duplicateFile(CoreFileBean var1, String var2);

    default public String getSecuredDownloadUrl(String downloadUrl, FilePathDTO filePath) {
        if (!ApplicationContextHelper.getEnvironment().getProperty("sunway.security.file-access-permission.enabled", Boolean.class, false).booleanValue()) {
            return downloadUrl;
        }
        String security = this.getSecurity(filePath);
        if (StringUtils.contains((CharSequence)downloadUrl, "?security=" + security) || StringUtils.contains((CharSequence)downloadUrl, "&security=" + security)) {
            return downloadUrl;
        }
        downloadUrl = downloadUrl.contains("?") ? downloadUrl + "&security=" + security : downloadUrl + "?security=" + security;
        if (LocalContextHelper.isUserLogin()) {
            LoginUser loginUser = LocalContextHelper.getLoginPrincipal();
            if (StringUtils.isBlank(loginUser.getLoginToken())) {
                return downloadUrl + "&token=" + AuthenticationHelper.getLoginUserToken();
            }
            return downloadUrl + "&token=" + loginUser.getLoginToken();
        }
        return downloadUrl;
    }

    default public String getSecurity(FilePathDTO filePath) {
        String url = FilePathManager.getUrl(filePath);
        if (LocalContextHelper.isUserLogin()) {
            return EncryptUtils.MD5Encrypt(LocalContextHelper.getLoginUserId() + "$" + url);
        }
        return EncryptUtils.MD5Encrypt(url);
    }
}
