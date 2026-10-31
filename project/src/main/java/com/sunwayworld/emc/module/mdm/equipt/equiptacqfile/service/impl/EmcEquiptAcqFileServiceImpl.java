package com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.bean.EmcEquiptAcqFileBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.dao.EmcEquiptAcqFileDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.service.EmcEquiptAcqFileService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.utils.EmcFileUtils;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.service.EmcEquiptAcqResultService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.EmcEquiptAcqScriptService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;

@GikamBean
@Repository
@Profile("equipt-acq-file")
public class EmcEquiptAcqFileServiceImpl implements EmcEquiptAcqFileService {

    @Autowired
    private EmcEquiptAcqFileDao EmcEquiptAcqFileDao;
    @Autowired
    private CoreFileService coreFileService;
    @Autowired
    private EmcEquiptService equiptService;
    @Autowired
    private EmcEquiptAcqScriptService equiptAcqScriptService;
    @Autowired
    private EmcEquiptAcqResultService equiptAcqResultService;

    @SuppressWarnings("unchecked")
    @Override
    public EmcEquiptAcqFileDao getDao() {
        return EmcEquiptAcqFileDao;
    }

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
            equiptAcqResultService.insertResultByAnalyAcqFiles(acqFileList);
        }
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
