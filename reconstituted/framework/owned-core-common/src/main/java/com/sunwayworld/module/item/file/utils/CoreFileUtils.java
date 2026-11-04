/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.utils;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileUploadDTO;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public abstract class CoreFileUtils {
    public static final <ID extends Serializable> String getTargetId(String table, ID itemId) {
        return table.toUpperCase() + "$" + itemId;
    }

    public static final Path getLocalPath(CoreFileBean coreFile) {
        Path path = FilePathManager.getLocalPath(CoreFileUtils.toFilePath(coreFile));
        if (!Files.exists(path, new LinkOption[0])) {
            CoreFileManager fileManager = ApplicationContextHelper.getBean(CoreFileManager.class);
            fileManager.download(coreFile);
        }
        return path;
    }

    public static final FilePathDTO toFilePath(CoreFileBean coreFile) {
        String irregularName = FileScope.finereport.equals((Object)FileScope.valueOf(coreFile.getScope())) ? coreFile.getExt() : coreFile.getMd5Name();
        return FilePathDTO.of(coreFile.getScope(), coreFile.getCreatedTime(), irregularName, coreFile.getName());
    }

    public static final FilePathDTO toFilePath(FileScope scope, String fileName) {
        return FilePathDTO.of(scope.name(), LocalDateTime.now(), UUID.randomUUID().toString(), fileName);
    }

    public static final CoreFileUploadDTO toFileUpload(CoreFileBean coreFile) {
        CoreFileUploadDTO fileUpload = new CoreFileUploadDTO();
        fileUpload.setId(coreFile.getId());
        fileUpload.setScope(coreFile.getScope());
        fileUpload.setTimestamp(DateTimeUtils.getDateSerialNumber(Objects.requireNonNull(coreFile.getCreatedTime())));
        fileUpload.setIrregularName(FileScope.finereport.equals((Object)FileScope.valueOf(coreFile.getScope())) ? coreFile.getExt() : coreFile.getMd5Name());
        fileUpload.setFileName(coreFile.getName());
        return fileUpload;
    }

    public static final CoreFileUploadDTO toFileUpload(FilePathDTO filePath) {
        CoreFileUploadDTO fileUpload = new CoreFileUploadDTO();
        fileUpload.setScope(filePath.getScope().name());
        fileUpload.setTimestamp(filePath.getTimestamp());
        fileUpload.setIrregularName(filePath.getIrregularName());
        fileUpload.setFileName(filePath.getFileName());
        return fileUpload;
    }
}
