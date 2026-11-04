/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.util;

import com.alibaba.fastjson.JSONObject;
import com.deepoove.poi.util.ByteUtils;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LimsFileUtils {
    private static final Logger logger = LoggerFactory.getLogger(LimsFileUtils.class);

    public static Path getFilePath(CoreFileBean coreFileBean) {
        FilePathDTO filePathDTO = CoreFileUtils.toFilePath(coreFileBean);
        Path absolutePath = FilePathManager.getLocalPath(filePathDTO);
        CoreFileManager coreFileManager = ApplicationContextHelper.getBean(CoreFileManager.class);
        try {
            coreFileManager.download(coreFileBean);
        }
        catch (Exception e) {
            logger.error("some error happened when download file {}", (Object)JSONObject.toJSONString(coreFileBean), (Object)e);
            throw new FileException(e);
        }
        return absolutePath;
    }

    public static byte[] getFileByte(CoreFileBean coreFile) {
        File toFile = FileUtils.createTempFile(coreFile.getFileExt());
        ApplicationContextHelper.getBean(CoreFileManager.class).download(coreFile, toFile.toPath());
        return ByteUtils.getLocalByteArray(toFile);
    }

    public static String getFileDownLoadUrl(CoreFileBean coreFile) {
        CoreFileManager coreFileManager = ApplicationContextHelper.getBean(CoreFileManager.class);
        return coreFileManager.getAbsoluteDownloadUrl(coreFile);
    }

    public static void unZipFile(Path zipFilePath, Path toFilePath) throws IOException {
        ZipFile zip = new ZipFile(zipFilePath.toFile(), Charset.forName("GBK"));
        zip.stream().forEach(zipEntry -> {
            String name = zipEntry.getName();
            String[] nameSplit = StringUtils.split(name, "\\");
            Path resolve = toFilePath;
            for (String fileName : nameSplit) {
                resolve = resolve.resolve(fileName);
            }
            if (zipEntry.isDirectory()) {
                try {
                    Files.createDirectories(resolve, new FileAttribute[0]);
                }
                catch (IOException e) {
                    logger.error("unzip error", e);
                }
            } else {
                FileUtils.makeDirs(resolve);
                try {
                    int len;
                    FileOutputStream outputStream = new FileOutputStream(resolve.toFile());
                    InputStream inputStream = zip.getInputStream((ZipEntry)zipEntry);
                    byte[] buf1 = new byte[1024];
                    while ((len = inputStream.read(buf1)) > 0) {
                        outputStream.write(buf1, 0, len);
                    }
                    inputStream.close();
                    outputStream.close();
                }
                catch (IOException e) {
                    logger.error("unzip error", e);
                }
            }
        });
    }
}
