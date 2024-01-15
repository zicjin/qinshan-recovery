package com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.sunwayworld.emc.framework.utils.EmcPdfUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.bean.EmcEquiptAcqFileBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.service.PfEquiptAcqFileService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.utils.EmcFileUtils;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.service.EmcEquiptAcqResultService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEquiptAcqScriptBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.EmcEquiptAcqScriptService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.PdfUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;

/**
 * @author zhengxy@sunwayworld.com 2022年03月09日
 */
@Repository
@GikamBean
public class PfEquiptAcqFileServiceImpl extends EmcEquiptAcqFileServiceImpl implements PfEquiptAcqFileService {

    private static final Logger logger = LogManager.getLogger(PfEquiptAcqFileServiceImpl.class);
    private static final String MERGED_FILE_KEY = "MERGED_FILE::";

    @Autowired
    private EmcEquiptAcqScriptService acqScriptService;

    @Autowired
    private EmcEquiptAcqResultService equiptAcqResultService;

    @Autowired
    private EmcEquiptAcqScriptService equiptAcqScriptService;

    @Autowired
    private EmcEquiptService equiptService;

    @Autowired
    private CoreFileService coreFileService;

    @Override
    @Transactional
    @Scheduled(fixedDelay = 15000)
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void dcuAcquisition() {
        String equiptDirString = ApplicationContextHelper.getEnvironment().getProperty("sunway.equipt.acq.file.local.path");
        if (StringUtils.isEmpty(equiptDirString)) {
            return;
        }

        Path equiptDir = Paths.get(equiptDirString);
        File equiptDirFile = equiptDir.toFile();
        if (!equiptDirFile.exists()) {
            return;
        }

        List<File> dcuFile = FileUtils.getFileList(equiptDir);
        if (dcuFile.isEmpty()) {
            return;
        }

        String equiptBackupDirValue = ApplicationContextHelper.getEnvironment().getProperty("sunway.equipt.acq.file.local.backup.path");
        List<EmcEquiptAcqFileBean> acqFileList = new ArrayList<>();
        long currentTimeMillis = System.currentTimeMillis();

        dcuFile.stream().filter(file -> !file.isDirectory()).forEach(file -> {
            try {
                EmcEquiptAcqFileBean equiptAcqFile = copyAcqFile(file, equiptDirFile);
                if (equiptAcqFile != null) {
                    acqFileList.add(equiptAcqFile);
                    return;
                }

                if (StringUtils.isEmpty(equiptBackupDirValue)) {
                    file.delete();
                    return;
                }

                File equiptBackupDir = new File(equiptBackupDirValue + File.separator + currentTimeMillis);
                if (!equiptBackupDir.exists()) {
                    equiptBackupDir.mkdir();
                }
                Files.move(file.toPath(), Paths.get(equiptBackupDir.getPath() + File.separator + file.getName()));
            } catch (Exception e) {
                e.printStackTrace();
                file.delete();
            }
        });

        if (!acqFileList.isEmpty()) {
            getDao().insert(acqFileList);
            Map<Long, List<EmcEquiptAcqFileBean>> acqFileMap = acqFileList.stream().filter(f -> !ObjectUtils.isEmpty(f.getEquiptId())).collect(Collectors.groupingBy(EmcEquiptAcqFileBean::getEquiptId));
            acqFileMap.forEach((equiptId, curFileList) -> equiptAcqResultService.insertResultByAnalyAcqFiles(curFileList));
        }
    }

    @Override
    @Scheduled(fixedDelay = 20000)
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void mergeEquiptAcqFile() {
        GikamConcurrentLocker.isolatedRun("MERGE_ACQ_FILE", () -> {
            long startTime = System.currentTimeMillis();
            String mergeDirectoryPath = ApplicationContextHelper.getEnvironment().getProperty("sunway.equipt.acq.file.merge.path");
            if (StringUtils.isEmpty(mergeDirectoryPath)) {
                return;
            }

            File mergeDirectory = new File(mergeDirectoryPath);
            if (!mergeDirectory.exists() || !mergeDirectory.isDirectory()) {
                return;
            }

            List<File> fileList = FileUtils.getFileList(mergeDirectory.toPath());
            if (fileList.isEmpty()) {
                return;
            }

            String acqDirectory = ApplicationContextHelper.getEnvironment().getProperty("sunway.equipt.acq.file.local.path");
            if (StringUtils.isEmpty(acqDirectory)) {
                return;
            }

            List<File> emptyFileList = fileList.stream().filter(file -> file.length() == 0).collect(Collectors.toList());
            emptyFileList.forEach(File::delete);

            List<File> validFileList = fileList.stream().filter(file -> file.length() > 0).collect(Collectors.toList());
            if (validFileList.isEmpty()) {
                return;
            }

            // 需要合并后采集的脚本配置
            List<String> catalogList = lookupMergedCatalog();
            List<File> needMergedFileList = validFileList.stream().filter(file -> StringUtils.endsWithIgnoreCase(file.getName(), ".pdf")
                    && catalogList.stream().anyMatch(catalog -> StringUtils.contains(file.getParent(), catalog))).collect(Collectors.toList());
            List<String> needMergedFilePathList = needMergedFileList.stream().map(File::getAbsolutePath).collect(Collectors.toList());

            List<File> otherFileList = validFileList.stream().filter(file -> !needMergedFilePathList.contains(file.getAbsolutePath())).collect(Collectors.toList());

            mergedFile(needMergedFileList, acqDirectory);
            uploadFile(otherFileList, acqDirectory);
            long endTime = System.currentTimeMillis();
            logger.info("合并谱图文件共耗时{}s", (endTime - startTime) / 1000);
        });
    }

    /**
     * 查询需要合并的采集目录
     * @return 采集目录列表
     */
    private List<String> lookupMergedCatalog() {
        List<EmcEquiptAcqScriptBean> mergedAcqScriptList = acqScriptService.selectListByFilter(SearchFilter.instance().match("mergedAcq", Constant.YES).filter(MatchPattern.EQ));
        List<Long> acqScriptIdList = mergedAcqScriptList.stream().map(EmcEquiptAcqScriptBean::getId).collect(Collectors.toList());
        List<EmcEquiptBean> equiptList = equiptService.getDao().selectListByOneColumnValues(acqScriptIdList, "acqScriptId", Arrays.asList("id", "acqScriptId", "catalog"));

        return equiptList.stream().map(EmcEquiptBean::getCatalog).distinct().collect(Collectors.toList());
    }

    /**
     * 合并文件，会延迟一个周期，为了解决部分文件先合并问题
     * 比如有100个文件，复制的时候，前50个先复制完成，这个时候刚好触发了合并，若不延迟，后50个文件则无法合并
     * 此处采用的策略并不能完全避免部分文件先合并的情况，只是通过加长时间来减少这种情况的发生
     * @param needMergedFileList
     * @param acqDirectory
     */
    private void mergedFile(List<File> needMergedFileList, String acqDirectory) {
        if (needMergedFileList.isEmpty()) {
            return;
        }

        Map<String, List<File>> fileMap = needMergedFileList.stream().collect(Collectors.groupingBy(File::getParent));
        fileMap.forEach((parentPath, partFileList) -> {
            List<File> validFileList = partFileList.stream().filter(file -> !file.isDirectory() && StringUtils.endsWithIgnoreCase(file.getName(), ".pdf")).collect(Collectors.toList());
            if (validFileList.isEmpty()) {
                return;
            }

            List<File> unmergedFileList = validFileList.stream().filter(file -> !StringUtils.startsWith(file.getName(), "merged_")).collect(Collectors.toList());
            List<File> mergedFileList = validFileList.stream().filter(file -> StringUtils.startsWith(file.getName(), "merged_")).collect(Collectors.toList());

            String runId = partFileList.get(0).getParentFile().getName();
            String mergedRedisKey = MERGED_FILE_KEY + runId;
            if (!unmergedFileList.isEmpty()) {
                int maxFileSuffixNo = mergedFileList.stream().filter(file -> StringUtils.contains(file.getName(), "_")).map(File::getName).map(name -> name.substring(name.lastIndexOf("_") + 1, name.lastIndexOf("."))).filter(NumberUtils::isNumber).mapToInt(NumberUtils::parseInt).max().orElse(0);
                int nextSuffixNo = maxFileSuffixNo + 1;
                String mergedPdfPath = String.join(File.separator, parentPath, "merged_" + nextSuffixNo + ".pdf");
                List<String> unmergedFilePathList = unmergedFileList.stream().map(File::getAbsolutePath).collect(Collectors.toList());

                // 记录每次合并的文件
                Set<Object> members = RedisHelper.getRedisTemplate().boundSetOps(mergedRedisKey).members();
                List<String> mergedPathList = (members == null) ? Collections.emptyList() : members.stream().map(String::valueOf).collect(Collectors.toList());
                List<String> validUnmergedFilePathList = unmergedFilePathList.stream().filter(path -> !mergedPathList.contains(path)).collect(Collectors.toList());

                EmcPdfUtils.mergePdfFile(validUnmergedFilePathList, mergedPdfPath);
                logger.info("本次【" + Thread.currentThread() + "】线程合并了【{}】文件", String.join(",", validUnmergedFilePathList));
                BoundSetOperations<String, Object> setOperations = RedisHelper.getRedisTemplate().boundSetOps(mergedRedisKey);
                validUnmergedFilePathList.forEach(path -> setOperations.add(path));

                // 合并完后删除原文件
                unmergedFileList.forEach(File::delete);
                setOperations.expire(30, TimeUnit.MINUTES);
                logger.info("本次【" + Thread.currentThread() + "】线程合并完成后，删除了【{}】文件", String.join(",", unmergedFilePathList));
                return;
            }

            String finalMergedPdfPath = String.join(File.separator, parentPath, runId + ".pdf");
            List<String> mergedFilePathList = mergedFileList.stream().map(File::getAbsolutePath).collect(Collectors.toList());
            EmcPdfUtils.mergePdfFile(mergedFilePathList, finalMergedPdfPath);
            logger.info("最终【" + Thread.currentThread() + "】线程合并了【{}】文件", String.join(",", mergedFilePathList));
            mergedFileList.forEach(File::delete);
            logger.info("最终【" + Thread.currentThread() + "】线程合并完成后，删除了【{}】文件", String.join(",", mergedFilePathList));
            File finalMergedFile = new File(finalMergedPdfPath);

            uploadFile(finalMergedFile, acqDirectory);

            RedisHelper.getRedisTemplate().delete(mergedRedisKey);
        });
    }

    /**
     * 上传文件
     * @param sourceFile
     * @param acqDirectory
     */
    private void uploadFile(File sourceFile, String acqDirectory) {
        String orgDirectory = sourceFile.getParentFile().getParentFile().getParentFile().getName();
        String equiptDirectory = sourceFile.getParentFile().getParentFile().getName();
        String runId = sourceFile.getParentFile().getName();
        String targetFilePath = String.join(File.separator, acqDirectory, orgDirectory, equiptDirectory, runId, sourceFile.getName());

        File targetFile = new File(targetFilePath);
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }

        if (targetFile.exists()) {
            targetFile.delete();
        }

        CoreFileBean newFile = new CoreFileBean();
        newFile.setTargetId("T_EMC_EQUIPT$" + runId);
        coreFileService.upload(newFile, sourceFile);
        try {
            Files.move(sourceFile.toPath(), targetFile.toPath());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void uploadFile(List<File> sourceFiles, String acqDirectory) {
        sourceFiles.forEach(sourceFile -> uploadFile(sourceFile, acqDirectory));
    }

    private EmcEquiptAcqFileBean copyAcqFile(File file, File equiptDirFile) {
        EmcEquiptAcqFileBean acqFileBean = new EmcEquiptAcqFileBean();
        acqFileBean.setId(ApplicationContextHelper.getNextIdentity());

        String fileName = file.getName();
        LocalDateTime now = LocalDateTime.now();
        String fileNameNoExt = fileName.substring(0, fileName.lastIndexOf("."));

        acqFileBean.setSourceFileName(fileName);
        acqFileBean.setTargetFileName(new StringBuilder().append(fileNameNoExt).append("_").append(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(now)).append(".")
                .append(FileUtils.getFileExtension(fileName)).toString());
        acqFileBean.setCreatedTime(now);
        acqFileBean.setStatus("0");

        // 关联批号
        String runId = file.getParentFile().getName();
        acqFileBean.setRunId(runId);

        // 维护了采集文件夹就用采集文件夹，否则用仪器编码
        // 采集文件夹有三层，例如：DAJC\5D097\20201127077，最里层的是批号目录，为了隔离采集的数据
        List<EmcEquiptBean> equiptOfFileFolderList = equiptService.selectListByFilter(SearchFilter.instance().match("CATALOG", file.getParentFile().getParentFile().getName()).filter(MatchPattern.EQ));
        if (equiptOfFileFolderList != null && equiptOfFileFolderList.size() > 0) {
            acqFileBean.setEquiptId(equiptOfFileFolderList.get(0).getId());
        } else {
            List<EmcEquiptBean> equiptOfNoList = equiptService.selectListByFilter(SearchFilter.instance().match("EQUIPTID", getEquiptNo(file)).filter(MatchPattern.EQ));
            if (equiptOfNoList != null && equiptOfNoList.size() > 0) {
                acqFileBean.setEquiptId(equiptOfNoList.get(0).getId());
            }
        }

        // 针对上传错误的采集文件，直接返回
        if (ObjectUtils.isEmpty(acqFileBean.getEquiptId())) {
            return null;
        }

        CoreFileBean insertCoreFile = new CoreFileBean();
        insertCoreFile.setId(ApplicationContextHelper.getNextIdentity());
        insertCoreFile.setTargetId(getDao().getTable() + "$" + acqFileBean.getId());
        insertCoreFile.setName(acqFileBean.getTargetFileName());
        insertCoreFile.setFileExt(FileUtils.getFileExtension(insertCoreFile.getName()));
        insertCoreFile.setMd5Name(EncryptUtils.MD5Encrypt(insertCoreFile.getTargetId() + "$" + insertCoreFile.getName()));
        insertCoreFile.setScope(FileScope.equipt.name());
        insertCoreFile.setSize(file.length());
        insertCoreFile.setTimes(0L);
        insertCoreFile.setDownloadable(Constant.YES);
        insertCoreFile.setCreatedTime(now);

        String catalogName = file.getParentFile().getName();
        acqFileBean.getExt$().put("equiptNo", catalogName);
        insertCoreFile.setExt("T_EMC_ORDER_EQUIPT_ACQ_FILE$" + catalogName);

        Long fileId = coreFileService.upload(insertCoreFile, file);

        acqFileBean.getExt$().put("fileId", fileId + "");

        String content = null;
        EmcEquiptBean equipt = equiptService.selectDetail(acqFileBean.getEquiptId());
        Long acqScriptId = equipt.getAcqScriptId();
        String scriptCategory = equipt.getScriptCategory();
        String fileExtension = FileUtils.getFileExtension(file.getName()).toLowerCase();
        if (EmcConstant.COMMON_SCRIPT.equals(scriptCategory) && EmcConstant.ANALYSIS_WAY_PRIOR_DATA.equals(equiptAcqScriptService.selectDetail(acqScriptId).getAnalysisWay())
                && EmcConstant.FILECATAEGORY_PDF.equals(fileExtension)) {
            content = EmcFileUtils.extractTextFromPDF(file);
        } else if (EmcConstant.FILECATAEGORY_XLSX.equals(fileExtension) || EmcConstant.FILECATAEGORY_XLS.equals(fileExtension)) {
            content = EmcFileUtils.extractTextFromXlsx(file);
        } else {
            content = FileUtils.extractTextFromFile(file);
        }

        // 如果文件解析不出什么任何内容，直接返回
        if (StringUtils.isEmpty(content)) {
            return null;
        }

        acqFileBean.setContent(content);

        moveFileToBackup(file, equiptDirFile);
        return acqFileBean;
    }

    /**
     * 获取设备编码 文件路径结构 dcu定义扫描文件夹 A -设备编码 -文件 A/设备编码/批号/文件
     *
     * @param file
     * @return 设备编码
     */
    private String getEquiptNo(File file) {
        return file.getParentFile().getParentFile().getName();
    }

    /**
     * 把文件移动到备份文件夹 文件路径 A/backup/设备编码/年月日/文件
     *
     * @param file
     */
    private void moveFileToBackup(File file, File equiptDirFile) {
        String afterParentFileString = StringUtils.replace(file.getAbsolutePath(), equiptDirFile.getAbsolutePath(), "");
        String fileName = file.getName();
        String dayString = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now());
        String newFileNamePart = StringUtils.replace(afterParentFileString, fileName, dayString + File.separator + fileName);
        File parentFile = equiptDirFile.getParentFile();
        if (parentFile == null) {
            file.delete();
            return;
        }
        String backupFilePath = parentFile.getAbsolutePath() + File.separator + "backup" + newFileNamePart;
        File backupFile = new File(backupFilePath);
        if (!backupFile.getParentFile().exists()) {
            backupFile.getParentFile().mkdirs();
        }
        if (backupFile.exists()) {
            backupFile.delete();
        }
        file.renameTo(backupFile);
    }

}
