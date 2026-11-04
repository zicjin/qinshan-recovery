/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.fastdfs.service;

import com.sunwayworld.framework.fastdfs.bean.FastDfsDeleteResponseDTO;
import com.sunwayworld.framework.fastdfs.bean.FastDfsInfoResponseDTO;
import com.sunwayworld.framework.fastdfs.bean.FastDfsUploadResponseDTO;
import com.sunwayworld.framework.fastdfs.core.FastDfsUploadConfig;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

public interface FastDfsService {
    public FastDfsUploadResponseDTO upload(FastDfsUploadConfig var1, MultipartFile var2);

    public FastDfsUploadResponseDTO upload(FastDfsUploadConfig var1, File var2);

    public FastDfsUploadResponseDTO upload(File var1);

    public FastDfsInfoResponseDTO getInfo(@Nullable String var1, String var2);

    public FastDfsDeleteResponseDTO delete(@Nullable String var1, String var2);

    default public FastDfsDeleteResponseDTO delete(String md5) {
        return this.delete("group", md5);
    }

    public FastDfsUploadResponseDTO upload(@Nullable String var1, FilePathDTO var2, InputStream var3);

    public FastDfsUploadResponseDTO upload(@Nullable String var1, FilePathDTO var2, MultipartFile var3);

    public FastDfsUploadResponseDTO upload(@Nullable String var1, FilePathDTO var2, File var3);

    default public void download(@Nullable String group, FilePathDTO filePath) {
        this.download(group, filePath, FilePathManager.getLocalPath(filePath));
    }

    public void download(@Nullable String var1, FilePathDTO var2, Path var3);
}
