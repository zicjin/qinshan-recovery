/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.fileacq;

import com.sunwayworld.cloud.module.equipt.fileacq.CoreEquiptFileAcqService;
import com.sunwayworld.cloud.module.equipt.record.bean.CoreEquiptAcqRecordBean;
import com.sunwayworld.cloud.module.equipt.record.service.CoreEquiptAcqRecordService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.executor.ExecutorHelper;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Profile(value={"file-acq"})
public class CoreEquiptFileAcqSchedule {
    private static final Logger logger = LogManager.getLogger(CoreEquiptFileAcqSchedule.class);

    @Transactional
    @Scheduled(fixedDelay=10000L)
    public void fileAcqSchedule() {
        GikamConcurrentLocker.isolatedRun("CORE_FILE_ACQ_INSERTRECORD_LOOP_RUNNER", () -> this.insertAcqRecord());
        this.fileAcq();
    }

    private void insertAcqRecord() {
        CoreEquiptAcqRecordService equiptAcqRecordService = ApplicationContextHelper.getBean(CoreEquiptAcqRecordService.class);
        String equiptAcqRecordTable = equiptAcqRecordService.getDao().getTable();
        CoreEquiptFileAcqService equiptFileAcqService = ApplicationContextHelper.getBean(CoreEquiptFileAcqService.class);
        CoreFileService fileService = ApplicationContextHelper.getBean(CoreFileService.class);
        String fileAcqdir = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.equipt.file.acq.path");
        if (StringUtils.isEmpty(fileAcqdir)) {
            return;
        }
        Path fileAcqPath = Paths.get(fileAcqdir, new String[0]);
        if (!Files.isDirectory(fileAcqPath, new LinkOption[0])) {
            return;
        }
        ArrayList<CoreEquiptAcqRecordBean> insertAcqRecordList = new ArrayList<CoreEquiptAcqRecordBean>();
        List<File> fileList = FileUtils.getFileList(fileAcqPath);
        fileList.forEach(file -> {
            CoreEquiptAcqRecordBean fileAcq = new CoreEquiptAcqRecordBean();
            fileAcq.setId(ApplicationContextHelper.getNextIdentity());
            fileAcq.setAcqCategory("file");
            fileAcq.setFilePath(file.getAbsolutePath());
            fileAcq.setFileName(file.getName());
            fileAcq.setCreatedById("SYSTEM");
            try {
                fileAcq.setAcqRawData(equiptFileAcqService.extractTextFromFile((File)file));
                equiptFileAcqService.preAcqRecordInserted(fileAcq, (File)file);
            }
            catch (Throwable t) {
                logger.error(file.getName() + I18nHelper.getMessage("CORE.MODULE.EQUIPT.FILE.ACQ.EXCEPTION.FILE_READ_FAIL", new String[0]) + t.getMessage());
                fileAcq.setMessage(ObjectUtils.getStackTrace(t));
                fileAcq.setAcqResult("-1");
            }
            try {
                CoreFileBean coreFile = new CoreFileBean();
                coreFile.setTargetId(equiptAcqRecordTable + "$" + fileAcq.getId());
                coreFile.setScope(FileScope.equipt.name());
                fileService.upload(coreFile, (File)file);
                FileUtils.deleteQuietly(file);
                insertAcqRecordList.add(fileAcq);
            }
            catch (Exception e) {
                logger.error(file.getName() + I18nHelper.getMessage("CORE.MODULE.EQUIPT.FILE.ACQ.EXCEPTION.ARCHIVE", new String[0]) + e.getMessage());
            }
        });
        if (!insertAcqRecordList.isEmpty()) {
            equiptAcqRecordService.insertInNewTransaction(insertAcqRecordList);
        }
    }

    private void fileAcq() {
        CoreEquiptAcqRecordService equiptAcqRecordService = ApplicationContextHelper.getBean(CoreEquiptAcqRecordService.class);
        CoreEquiptFileAcqService equiptFileAcqService = ApplicationContextHelper.getBean(CoreEquiptFileAcqService.class);
        List<CoreEquiptAcqRecordBean> noneAcqRecordList = equiptAcqRecordService.selectNoneAcqRecordList();
        if (noneAcqRecordList.isEmpty()) {
            return;
        }
        noneAcqRecordList.forEach(r -> ExecutorHelper.submit(() -> {
            Long recordId = r.getId();
            GikamConcurrentLocker.isolatedRun("CORE_FILE_ACQ_LOOP_RUNNER_" + recordId, () -> {
                if (!"0".equals(((CoreEquiptAcqRecordBean)equiptAcqRecordService.selectById(recordId)).getAcqResult())) {
                    return;
                }
                try {
                    equiptFileAcqService.fileAcq((CoreEquiptAcqRecordBean)r);
                    r.setAcqResult("1");
                }
                catch (Throwable t) {
                    logger.error(t.getMessage());
                    r.setMessage(ObjectUtils.getStackTrace(t));
                    r.setAcqResult("-1");
                }
                equiptAcqRecordService.updateIfChangedInNewTransaction((CoreEquiptAcqRecordBean)r);
            });
        }));
        noneAcqRecordList.clear();
    }
}
