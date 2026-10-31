package com.sunwayworld.emc.module.bp.archive.service.impl;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.cloud.module.fr.report.service.CoreReportConfigLineService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.archive.bean.EmcArchiveCategoryBean;
import com.sunwayworld.emc.module.bp.archive.service.EmcArchiveFileService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcContractBean;
import com.sunwayworld.emc.module.mdm.contract.service.EmcContractService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;

/**
 * @author zhengxy@sunwayworld.com 2020年10月31日
 */
@Repository
@GikamBean
public class EmcArchiveFileServiceImpl implements EmcArchiveFileService {

    private static final Logger logger = LogManager.getLogger(EmcArchiveFileServiceImpl.class);

    /** 归档文件类型 */
    private static final String WORD_REPORT_FILES = "wordReportFiles";
    private final static String REPORT_AUDIT_RECORD_FILES = "reportAuditRecordFiles";
    private final static String CONTRACT_REVIEW_FILES = "contractReviewFiles";
    private final static String CONTRACT_FILES = "contractFiles";
    private final static String DETECT_SCHEME_FILES = "detectSchemeFiles";
    private final static String CALIBRATION_RECORD_FILES = "calibrationRecordFiles";
    private final static String SAMPLING_RECORD_FILES = "samplingRecordFiles";
    private final static String SAMPLING_VIDEO_FILES = "samplingVideoFiles";
    private final static String RECEIVE_RECORD_FILES = "receiveRecordFiles";
    private final static String ANALYSIS_RECORD_FILES = "analysisRecordFiles";
    private final static String OTHER_FILES = "otherFiles";

    private final static String REPORT_AUDIT_RECORD_KEYWORD = "报告审核记录";
    private final static String CONTRACT_REVIEW_FILENAME = "合同评审";
    private final static String CONTRACT_FILENAME = "委托协议书";
    private final static String CALIBRATION_RECORD_FILENAME = "采样设备校准记录";
    private final static String SAMPLING_RECORD_FILENAME = "采样记录";
    private final static String RECEIVE_RECORD_FILE = "样品交接记录";
    private final static String PDF_SUFFIX = ".pdf";
    private final static String ARCHIVE_FILE = "archiveFile";

    @Autowired
    @Lazy
    private EmcProjectService projectService;

    @Autowired
    @Lazy
    private EmcSampleService sampleService;

    @Autowired
    private EmcContractService contractService;

    @Autowired
    private CoreReportConfigLineService coreReportConfigLineService;

    @Autowired
    private CoreFileService coreFileService;

    @Lazy
    @Autowired
    private CoreFileManager fileManager;

    @Autowired
    @Lazy
    private EmcReportService reportService;

    @Override
    public String archive(RestJsonWrapperBean wrapper) {
        List<EmcReportBean> reportList = wrapper.parse(EmcReportBean.class, 0);
        List<EmcArchiveCategoryBean> archiveCategoryList = wrapper.parse(EmcArchiveCategoryBean.class, 1);

        Long projectId = reportList.get(0).getProjId();
        EmcProjectBean project = projectService.selectById(projectId);

        archiveCategoryList.forEach(archiveCategory -> {
            switch (archiveCategory.getCategoryCode()) {
                case WORD_REPORT_FILES:
                    archiveWordReportFiles(project);
                    break;
                case REPORT_AUDIT_RECORD_FILES:
                    archiveReportAuditRecordFiles(project);
                    break;
                case CONTRACT_REVIEW_FILES:
                    archiveContractReviewFiles(project);
                    break;
                case CONTRACT_FILES:
                    archiveContractFiles(project);
                    break;
                case DETECT_SCHEME_FILES:
                    archiveDetectSchemeFiles(project);
                    break;
                case CALIBRATION_RECORD_FILES:
                    archiveCalibrationRecordFiles(project);
                    break;
                case SAMPLING_RECORD_FILES:
                    archiveSamplingRecordFiles(project);
                    break;
                case SAMPLING_VIDEO_FILES:
                    archiveSamplingVideoFiles(project);
                    break;
                case RECEIVE_RECORD_FILES:
                    archiveReceiveRecordFiles(project);
                    break;
                case ANALYSIS_RECORD_FILES:
                    archiveAnalysisRecordFiles(project);
                    break;
                case OTHER_FILES:
                    archiveOtherFiles(project);
                    break;
                    default:
                        break;
            }
        });

        return null;
    }

    @Override
    public void archiveWordReportFiles(EmcProjectBean project) {
        List<CoreFileBean> fileList = coreFileService.getDao().selectListByOneColumnValue(projectService.getDao().getTable() + "$" + project.getId(), "TARGETID");
        List<CoreFileBean> deleteFileList = fileList.stream().filter(f -> ObjectUtils.equals(f.getExt(), "wordReportPdf")).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(deleteFileList)) {
            coreFileService.deleteList(deleteFileList);
        }

        EmcReportBean report = reportService.getDao().selectListByOneColumnValue(project.getId(), "PROJID").stream().filter(s -> !"draft".equals(s.getStatus())).collect(Collectors.toList()).get(0);
        List<CoreFileBean> insertfileList = coreFileService.getDao().selectListByOneColumnValue(reportService.getDao().getTable() + "$" + report.getId(), "TARGETID");
        List<CoreFileBean> filterFile = insertfileList.stream().filter(f -> StringUtils.contains(f.getName(), "盖章")).collect(Collectors.toList());
        filterFile.forEach(f -> {
            File file = CoreFileUtils.getLocalPath(f).toFile();
            CoreFileBean newFile = new CoreFileBean();
            BeanUtils.copyProperties(f, newFile, EmcConstant.CREATED_COLUMNS);
            newFile.setId(ApplicationContextHelper.getNextIdentity());
            newFile.setTargetId(projectService.getDao().getTable() + "$" + project.getId());
            newFile.setDownloadUrl(file.getPath());
            newFile.setBizCategory("projectFile");
            newFile.setExt("wordReportPdf");
            coreFileService.getDao().insert(newFile);
            fileManager.upload(newFile, file);
        });
    }

    @Override
    public void archiveReportAuditRecordFiles(EmcProjectBean project) {
        List<CoreReportConfigLineBean> configLineList = coreReportConfigLineService.getDao().selectListByOneColumnValue("rasproject", "REPORTCONFIGID");
        List<CoreReportConfigLineBean> auditRecordReportList = configLineList.stream().filter(line -> StringUtils.contains(line.getReportName(), REPORT_AUDIT_RECORD_KEYWORD)).collect(Collectors.toList());
        if (auditRecordReportList.isEmpty()) {
            return;
        }

        String reportingServiceServerUrl = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.fine.independent-deploy-server.url");
        if (StringUtils.isEmpty(reportingServiceServerUrl)) {
            return;
        }

        Pair<String, CoreFileBean> resultPair = getDownloadUrl(reportingServiceServerUrl, project.getId(), auditRecordReportList.get(0), project, REPORT_AUDIT_RECORD_FILES,"报告审核记录");
        if (StringUtils.isEmpty(resultPair.getFirst())) {
            return;
        }

        deleteExistedFile(project.getId(), REPORT_AUDIT_RECORD_FILES);

        File file = downloadFile(resultPair.getFirst(), REPORT_AUDIT_RECORD_KEYWORD + PDF_SUFFIX);
        if (file != null) {
            coreFileService.upload(resultPair.getSecond(), file);
        }
    }

    @Override
    public void archiveContractReviewFiles(EmcProjectBean project) {
        String contractId = project.getExt$Item("contractid");
        if (StringUtils.isEmpty(contractId)) {
            return;
        }

        String reportingServiceServerUrl = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.fine.independent-deploy-server.url");
        if (StringUtils.isEmpty(reportingServiceServerUrl)) {
            return;
        }

        List<String> contractIdStrList = Arrays.stream(StringUtils.split(contractId, ",")).distinct().collect(Collectors.toList());
        List<Long> contractIdList = contractIdStrList.stream().mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());
        List<EmcContractBean> contractList = contractService.getDao().selectListByIds(contractIdList);

        if (contractList.isEmpty()) {
            return;
        }

        List<String> reportLineIdList = contractList.stream().map(EmcContractBean::getReviewReportLineId).distinct().collect(Collectors.toList());
        if (reportLineIdList.isEmpty()) {
            return;
        }

        List<Long> reportConfigLineIdList = reportLineIdList.stream().mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());
        List<CoreReportConfigLineBean> lineList = coreReportConfigLineService.selectListByIds(reportConfigLineIdList);
        if (lineList.isEmpty()) {
            return;
        }

        deleteExistedFile(project.getId(), CONTRACT_REVIEW_FILES);

        AtomicInteger fileIndex = new AtomicInteger();
        contractList.forEach(contract -> {
            lineList.stream().filter(line -> ObjectUtils.equals(contract.getReviewReportLineId(), line.getId() + "")).findFirst().ifPresent(configLine -> {
                Pair<String, CoreFileBean> resultPair = getDownloadUrl(reportingServiceServerUrl, project.getId(), configLine, contract, CONTRACT_REVIEW_FILES,"合同评审");
                if (StringUtils.isEmpty(resultPair.getFirst())) {
                    return;
                }

                File file = downloadFile(resultPair.getFirst(), concatFileName(CONTRACT_REVIEW_FILENAME, fileIndex.incrementAndGet()));
                if (file != null) {
                    coreFileService.upload(resultPair.getSecond(), file);
                }
            });
        });
    }

    @Override
    public void archiveContractFiles(EmcProjectBean project) {
        String contractId = project.getExt$Item("contractid");
        if (StringUtils.isEmpty(contractId)) {
            return;
        }

        String reportingServiceServerUrl = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.fine.independent-deploy-server.url");
        if (StringUtils.isEmpty(reportingServiceServerUrl)) {
            return;
        }

        List<String> contractIdStrList = Arrays.stream(StringUtils.split(contractId, ",")).distinct().collect(Collectors.toList());
        List<Long> contractIdList = contractIdStrList.stream().mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());
        List<EmcContractBean> contractList = contractService.getDao().selectListByIds(contractIdList);

        if (contractList.isEmpty()) {
            return;
        }

        List<String> reportIdList = contractList.stream().map(contract -> contract.getExt$Item("reportconfiglineid")).distinct().collect(Collectors.toList());
        if (reportIdList.isEmpty()) {
            return;
        }

        List<Long> reportConfigLineIdList = reportIdList.stream().mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());
        List<CoreReportConfigLineBean> lineList = coreReportConfigLineService.selectListByIds(reportConfigLineIdList);
        if (lineList.isEmpty()) {
            return;
        }

        deleteExistedFile(project.getId(), CONTRACT_FILES);

        AtomicInteger fileIndex = new AtomicInteger();
        contractList.forEach(contract -> {
            lineList.stream().filter(line -> ObjectUtils.equals(contract.getExt$Item("reportconfiglineid"), line.getId() + "")).findFirst().ifPresent(configLine -> {
                Pair<String, CoreFileBean> resultPair = getDownloadUrl(reportingServiceServerUrl, project.getId(), configLine, contract, CONTRACT_FILES,"委托协议书");
                if (StringUtils.isEmpty(resultPair.getFirst())) {
                    return;
                }

                File file = downloadFile(resultPair.getFirst(), concatFileName(CONTRACT_FILENAME, fileIndex.incrementAndGet()));
                if (file != null) {
                    coreFileService.upload(resultPair.getSecond(), file);
                }
            });
        });
    }

    @Override
    public void archiveDetectSchemeFiles(EmcProjectBean project) {
        String schemeReportLineId = project.getSchemeReportLineId();
        if (StringUtils.isEmpty(schemeReportLineId)) {
            return;
        }

        String reportingServiceServerUrl = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.fine.independent-deploy-server.url");
        if (StringUtils.isEmpty(reportingServiceServerUrl)) {
            return;
        }

        CoreReportConfigLineBean configLine = coreReportConfigLineService.selectById(NumberUtils.parseLong(schemeReportLineId));
        if (configLine == null || StringUtils.isEmpty(configLine.getExt$Item("reportpath"))) {
            return;
        }

        Pair<String, CoreFileBean> resultPair = getDownloadUrl(reportingServiceServerUrl, project.getId(), configLine, project, DETECT_SCHEME_FILES,"检测方案");
        if (StringUtils.isEmpty(resultPair.getFirst())) {
            return;
        }

        deleteExistedFile(project.getId(), DETECT_SCHEME_FILES);

        File file = downloadFile(resultPair.getFirst(), "检测方案.pdf");
        if (file != null) {
            coreFileService.upload(resultPair.getSecond(), file);
        }
    }

    @Override
    public void archiveCalibrationRecordFiles(EmcProjectBean project) {
        List<EmcSampleBean> sampleList = sampleService.getDao().selectListByOneColumnValue(project.getId(), "PROJID");
        List<String> caliReportLineIdList = sampleList.stream().map(EmcSampleBean::getCaliReportLineId).distinct().collect(Collectors.toList());
        if (caliReportLineIdList.isEmpty()) {
            return;
        }

        String reportingServiceServerUrl = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.fine.independent-deploy-server.url");
        if (StringUtils.isEmpty(reportingServiceServerUrl)) {
            return;
        }

        List<Long> reportConfigLineIdList = caliReportLineIdList.stream().mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());
        List<CoreReportConfigLineBean> lineList = coreReportConfigLineService.selectListByIds(reportConfigLineIdList);
        if (lineList.isEmpty()) {
            return;
        }

        deleteExistedFile(project.getId(), CALIBRATION_RECORD_FILES);

        AtomicInteger fileIndex = new AtomicInteger();
        sampleList.forEach(sample -> {
            lineList.stream().filter(line -> ObjectUtils.equals(sample.getCaliReportLineId(), line.getId() + "")).findFirst().ifPresent(configLine -> {
                Pair<String, CoreFileBean> resultPair = getDownloadUrl(reportingServiceServerUrl, project.getId(), configLine, sample, CALIBRATION_RECORD_FILES,"采样设备校准记录");
                if (StringUtils.isEmpty(resultPair.getFirst())) {
                    return;
                }

                File file = downloadFile(resultPair.getFirst(), concatFileName(CALIBRATION_RECORD_FILENAME, fileIndex.incrementAndGet()));
                if (file != null) {
                    coreFileService.upload(resultPair.getSecond(), file);
                }
            });
        });

    }

    @Override
    public void archiveSamplingRecordFiles(EmcProjectBean project) {
        List<EmcSampleBean> sampleList = sampleService.getDao().selectListByOneColumnValue(project.getId(), "PROJID");
        List<String> samplingRecordLineIdList = sampleList.stream().map(EmcSampleBean::getReportConfigLineId).distinct().collect(Collectors.toList());
        if (samplingRecordLineIdList.isEmpty()) {
            return;
        }

        String reportingServiceServerUrl = ApplicationContextHelper.getConstantValue("REPORTING_SERVICE_SERVER_URL");
        if (StringUtils.isEmpty(reportingServiceServerUrl)) {
            return;
        }

        List<Long> reportConfigLineIdList = samplingRecordLineIdList.stream().mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());
        List<CoreReportConfigLineBean> lineList = coreReportConfigLineService.selectListByIds(reportConfigLineIdList);
        if (lineList.isEmpty()) {
            return;
        }

        deleteExistedFile(project.getId(), SAMPLING_RECORD_FILES);

        AtomicInteger fileIndex = new AtomicInteger();
        sampleList.forEach(sample -> {
            lineList.stream().filter(line -> ObjectUtils.equals(sample.getReportConfigLineId(), line.getId() + "")).findFirst().ifPresent(configLine -> {
                Pair<String, CoreFileBean> resultPair = getDownloadUrl(reportingServiceServerUrl, project.getId(), configLine, sample, SAMPLING_RECORD_FILES,"采样记录");
                if (StringUtils.isEmpty(resultPair.getFirst())) {
                    return;
                }

                File file = downloadFile(resultPair.getFirst(), concatFileName(SAMPLING_RECORD_FILENAME, fileIndex.incrementAndGet()));
                if (file != null) {
                    coreFileService.upload(resultPair.getSecond(), file);
                }
            });
        });

    }

    @Override
    public void archiveSamplingVideoFiles(EmcProjectBean project) {
        // 技术服务过程影像资料，比如现场采样拍的照片或视频
    }

    @Override
    public void archiveReceiveRecordFiles(EmcProjectBean project) {
        List<EmcSampleBean> sampleList = sampleService.getDao().selectListByOneColumnValue(project.getId(), "PROJID");
        List<String> receiveRecordLineIdList = sampleList.stream().map(EmcSampleBean::getReceiveReportLineId).distinct().collect(Collectors.toList());
        if (receiveRecordLineIdList.isEmpty()) {
            return;
        }

        String reportingServiceServerUrl = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.fine.independent-deploy-server.url");
        if (StringUtils.isEmpty(reportingServiceServerUrl)) {
            return;
        }

        List<Long> reportConfigLineIdList = new ArrayList<>();
        reportConfigLineIdList.add(2360L);
        List<CoreReportConfigLineBean> lineList = coreReportConfigLineService.selectListByIds(reportConfigLineIdList);
        if (lineList.isEmpty()) {
            return;
        }

        deleteExistedFile(project.getId(), RECEIVE_RECORD_FILES);

        AtomicInteger fileIndex = new AtomicInteger();
        sampleList.forEach(sample -> {
            sample.setProjNo(project.getProjNo());
            lineList.stream().filter(line -> ObjectUtils.equals(sample.getReceiveReportLineId(), line.getId() + "")).findFirst().ifPresent(configLine -> {
                Pair<String, CoreFileBean> resultPair = getDownloadUrl(reportingServiceServerUrl, project.getId(), configLine, sample, RECEIVE_RECORD_FILES, "样品交接记录");
                if (StringUtils.isEmpty(resultPair.getFirst())) {
                    return;
                }

                File file = downloadFile(resultPair.getFirst(), concatFileName(RECEIVE_RECORD_FILE, fileIndex.incrementAndGet()));
                if (file != null) {
                    coreFileService.upload(resultPair.getSecond(), file);
                }
            });
        });
    }

    @Override
    public void archiveAnalysisRecordFiles(EmcProjectBean project) {
        // 原始分析记录在分析审核提交时已经生成
    }

    @Override
    public void archiveOtherFiles(EmcProjectBean project) {
        // 其他的项目附件
        archiveContractAnnexFiles(project);
    }

    private void archiveContractAnnexFiles(EmcProjectBean project) {
        String contractId = project.getExt$Item("contractId");
        List<String> targetIdList = Arrays.stream(StringUtils.split(contractId, ",")).map(id -> contractService.getDao().getTable() + "$" + id).collect(Collectors.toList());
        List<CoreFileBean> contractFileList = coreFileService.getDao().selectListByOneColumnValues(targetIdList, "targetId");
        if (contractFileList.isEmpty()) {
            return;
        }

        deleteExistedFile(project.getId(), OTHER_FILES);

        List<CoreFileBean> archiveContractFileList = new ArrayList<>(contractFileList.size());
        contractFileList.forEach(contractFile -> {
            CoreFileBean archiveContractFile = new CoreFileBean();
            BeanUtils.copyProperties(contractFile, archiveContractFile);
            archiveContractFile.setId(ApplicationContextHelper.getNextIdentity());
            archiveContractFile.setTargetId("T_EMC_PROJECT" + "$" + project.getId());
            archiveContractFile.setName(contractFile.getName());
            archiveContractFile.setExt("composePdf");
            archiveContractFile.setBizCategory(OTHER_FILES);
            archiveContractFileList.add(archiveContractFile);
        });

        coreFileService.getDao().insert(archiveContractFileList);
    }

    protected void deleteExistedFile(Long projectId, String bizCategory) {
        CoreFileBean deleteFilterFile = new CoreFileBean();
        deleteFilterFile.setTargetId("T_EMC_PROJECT" + "$" + projectId);
        deleteFilterFile.setExt("composePdf");
        deleteFilterFile.setBizCategory(bizCategory);
        coreFileService.getDao().deleteBy(deleteFilterFile, "TARGETID", "EXT", "BIZCATEGORY");
    }

    protected Pair<String, CoreFileBean> getDownloadUrl(String fineBaseUrl, long projectId, CoreReportConfigLineBean configLine, Object object, String bizCategory, String remark) {
        String configReportLineTargetId = coreReportConfigLineService.getDao().getTable() + "$" + configLine.getId();
        List<CoreFileBean> configReportLineCoreFileBeanList = coreFileService.getDao().selectListByOneColumnValue(configReportLineTargetId, "TARGETID");
        if (configReportLineCoreFileBeanList.isEmpty()) {
            return Pair.of(null, null);
        }

        StringBuilder reportUrl = new StringBuilder();
        CoreFileBean configReportLineCoreFileBean = configReportLineCoreFileBeanList.get(0);
        String fileName = configLine.getReportConfigLineNo() + "/" + configReportLineCoreFileBean.getName();
        try {
            reportUrl.append(fineBaseUrl).append("/decision/view/report?viewlet=").append(URLEncoder.encode(fileName, "UTF-8")).append("&format=pdf");
            String paramStr = parseReportParam(object, configLine.getParam());
            if (!StringUtils.isEmpty(paramStr)) {
                reportUrl.append("&").append(paramStr);
            }

            logger.info(reportUrl.toString());
            CoreFileBean coreFileBean = new CoreFileBean();
            coreFileBean.setTargetId("T_EMC_PROJECT" + "$" + projectId);
            coreFileBean.setScope(FileScope.secure.name());
            coreFileBean.setExt("composePdf");
            coreFileBean.setBizCategory(bizCategory);
            coreFileBean.setRemark(remark);

            return Pair.of(reportUrl.toString(), coreFileBean);
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        }

        return Pair.of(null, null);
    }

    /**
     * 解析报表参数，忽略参数名大小写
     */
    protected String parseReportParam(Object object, String param) {
        if (StringUtils.isEmpty(param)) {
            return "";
        }

        String[] params = StringUtils.split(param, "&");

        StringBuilder sb = new StringBuilder();

        if (params.length > 0) {
            for (String curParam : params) {
                String[] pair = StringUtils.split(curParam, "=");

                if (pair.length > 1) {
                    Object value = BeanUtils.getPropertyValue(object, pair[1]);

                    if (value != null) {
                        if (sb.length() > 0) {
                            sb.append("&");
                        }

                        sb.append(pair[0]).append("=").append(ObjectUtils.toString(value));
                    }
                }
            }
        }

        return sb.toString();
    }

    /**
     * 下载文件，并保存到指定目录下
     */
    protected File downloadFile(String url, String fileName) {
        FilePathDTO dest = FilePathDTO.of(FileScope.temp.name(), "", StringUtils.randomUUID(8), fileName);
        Path destPath = FilePathManager.getLocalPath(dest);
        File tempFile = new File(destPath.toString());
        logger.info(url);
        HttpClientManager.getInstance().download(url, tempFile);

        return tempFile;
    }

    protected String concatFileName(String prefix, int fileIndex) {
        if (fileIndex == 0) {
            return prefix + PDF_SUFFIX;
        }

        return prefix + "-" + fileIndex + PDF_SUFFIX;
    }
}
