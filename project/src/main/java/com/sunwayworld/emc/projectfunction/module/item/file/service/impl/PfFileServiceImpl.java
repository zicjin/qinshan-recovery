/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.emc.projectfunction.module.item.file.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.constant.CoreFileOperation;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileEventService;
import com.sunwayworld.module.item.file.service.CoreFileLogService;
import com.sunwayworld.module.item.file.service.impl.CoreFileServiceImpl;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@GikamBean
@Repository
public class PfFileServiceImpl
extends CoreFileServiceImpl {
    private static final Logger logger = LogManager.getLogger(PfFileServiceImpl.class);
    @Autowired
    private CoreFileLogService fileLogService;
    @Autowired
    private CoreFileEventService fileEventService;
    @Lazy
    @Autowired
    private CoreFileManager fileManager;

    @Override
    @Transactional(timeout=200000)
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long upload(CoreFileBean coreFile, MultipartFile file) {
        Long key = ApplicationContextHelper.getNextIdentity();
        CoreFileBean insertCoreFile = new CoreFileBean();
        insertCoreFile.setId(key);
        insertCoreFile.setTargetId(this.getReplactedTargetId(coreFile.getTargetId()));
        insertCoreFile.setName(this.getOriginalFilename(file.getOriginalFilename()));
        insertCoreFile.setFileExt(FileUtils.getFileExtension(insertCoreFile.getName()));
        insertCoreFile.setMd5Name(EncryptUtils.MD5Encrypt(key + "$" + insertCoreFile.getName()));
        insertCoreFile.setScope(Objects.toString(coreFile.getScope(), FileScope.secure.name()));
        insertCoreFile.setValidTimeFrom(coreFile.getValidTimeFrom());
        insertCoreFile.setValidTimeTo(coreFile.getValidTimeTo());
        insertCoreFile.setSize(file.getSize());
        insertCoreFile.setTimes(0L);
        insertCoreFile.setPermanent(Optional.ofNullable(coreFile.getPermanent()).orElse("1"));
        insertCoreFile.setExt(coreFile.getExt());
        insertCoreFile.setDownloadable(Optional.ofNullable(coreFile.getDownloadable()).orElse("1"));
        insertCoreFile.setCreatedTime(LocalDateTime.now());
        insertCoreFile.setBizCategory(coreFile.getBizCategory());
        insertCoreFile.setExt$(coreFile.getExt$());
        if (this.isImage(file)) {
            byte[] compressed = this.compressImageIfNeeded(file, 1920, 1080, 0.7, 0xA00000L);
            if (compressed != null) {
                insertCoreFile.setSize(NumberUtils.parseLong(compressed.length));
                this.fileManager.upload(insertCoreFile, (InputStream)new ByteArrayInputStream(compressed));
            } else {
                insertCoreFile.setSize(file.getSize());
                this.fileManager.upload(insertCoreFile, file);
            }
            this.fileLogService.insert(key, CoreFileOperation.INSERT.name());
            this.fileEventService.publishFileEvent(CoreFileOperation.INSERT, insertCoreFile);
        } else {
            insertCoreFile.setSize(file.getSize());
            this.fileManager.upload(insertCoreFile, file);
            this.fileLogService.insert(key, CoreFileOperation.INSERT.name());
            this.fileEventService.publishFileEvent(CoreFileOperation.INSERT, insertCoreFile);
        }
        this.getDao().insert(insertCoreFile);
        return key;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Transactional(timeout=200000)
    public Long upload(CoreFileBean coreFile, File uploadFile) {
        Long key = ApplicationContextHelper.getNextIdentity();
        CoreFileBean insertCoreFile = new CoreFileBean();
        insertCoreFile.setId(key);
        insertCoreFile.setTargetId(this.getReplactedTargetId(coreFile.getTargetId()));
        insertCoreFile.setName(uploadFile.getName());
        insertCoreFile.setFileExt(FileUtils.getFileExtension(uploadFile.getName()));
        insertCoreFile.setMd5Name(EncryptUtils.MD5Encrypt(key + "$" + uploadFile.getName()));
        insertCoreFile.setScope(Objects.toString(coreFile.getScope(), FileScope.secure.name()));
        insertCoreFile.setValidTimeFrom(coreFile.getValidTimeFrom());
        insertCoreFile.setValidTimeTo(coreFile.getValidTimeTo());
        insertCoreFile.setSize(uploadFile.length());
        insertCoreFile.setTimes(0L);
        insertCoreFile.setPermanent(Optional.ofNullable(coreFile.getPermanent()).orElse("1"));
        insertCoreFile.setExt(coreFile.getExt());
        insertCoreFile.setDownloadable("1");
        insertCoreFile.setCreatedTime(LocalDateTime.now());
        insertCoreFile.setExt$(coreFile.getExt$());
        insertCoreFile.setBizCategory(coreFile.getBizCategory());
        this.getDao().insert(insertCoreFile);
        long t0 = System.nanoTime();
        long sizeBytes = -1L;
        try {
            try {
                sizeBytes = Files.size(uploadFile.toPath());
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.fileManager.upload(insertCoreFile, uploadFile);
        }
        catch (Throwable throwable) {
            long nanos = System.nanoTime() - t0;
            long ms = TimeUnit.NANOSECONDS.toMillis(nanos);
            double seconds = (double)nanos / 1.0E9;
            if (sizeBytes > 0L) {
                double kb = (double)sizeBytes / 1024.0;
                double speed = seconds > 0.0 ? kb / seconds : Double.NaN;
                logger.info("PfFileServiceImpl.upload \u7528\u65f6={} ms (~{} s), \u5927\u5c0f={} KB, \u901f\u5ea6={} KB/s", (Object)ms, (Object)String.format("%.3f", seconds), (Object)String.format("%.2f", kb), (Object)(Double.isNaN(speed) ? "NaN" : String.format("%.2f", speed)));
            } else {
                logger.info("PfFileServiceImpl.upload \u7528\u65f6={} ms (~{} s)", (Object)ms, (Object)String.format("%.3f", seconds));
            }
            throw throwable;
        }
        long nanos = System.nanoTime() - t0;
        long ms = TimeUnit.NANOSECONDS.toMillis(nanos);
        double seconds = (double)nanos / 1.0E9;
        if (sizeBytes > 0L) {
            double kb = (double)sizeBytes / 1024.0;
            double speed = seconds > 0.0 ? kb / seconds : Double.NaN;
            logger.info("PfFileServiceImpl.upload \u7528\u65f6={} ms (~{} s), \u5927\u5c0f={} KB, \u901f\u5ea6={} KB/s", (Object)ms, (Object)String.format("%.3f", seconds), (Object)String.format("%.2f", kb), (Object)(Double.isNaN(speed) ? "NaN" : String.format("%.2f", speed)));
        } else {
            logger.info("PfFileServiceImpl.upload \u7528\u65f6={} ms (~{} s)", (Object)ms, (Object)String.format("%.3f", seconds));
        }
        this.fileLogService.insert(key, CoreFileOperation.INSERT.name());
        this.fileEventService.publishFileEvent(CoreFileOperation.INSERT, insertCoreFile);
        return key;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private byte[] compressImageIfNeeded(MultipartFile file, int maxWidth, int maxHeight, double quality, long minCompressSize) {
        try {
            if (file.getSize() < minCompressSize) {
                return null;
            }
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                return null;
            }
            if (image.getWidth() <= maxWidth && image.getHeight() <= maxHeight) {
                return null;
            }
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();){
                Thumbnails.of(image).size(maxWidth, maxHeight).outputQuality(quality).outputFormat("jpg").toOutputStream(baos);
                byte[] byArray = baos.toByteArray();
                return byArray;
            }
        }
        catch (IOException e) {
            return null;
        }
    }

    private boolean isImage(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            return false;
        }
        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename == null) {
            return false;
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        return Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp").contains(extension);
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
