// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）
package com.sunwayworld.emc.module.bp.archive.service.impl;

import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.cloud.module.fr.report.service.CoreReportConfigLineService;
import com.sunwayworld.emc.framework.utils.SpireOfficeUtil;
import com.sunwayworld.emc.module.bp.archive.bean.EmcArchiveCategoryBean;
import com.sunwayworld.emc.module.bp.archive.service.PfArchiveFileService;
import com.sunwayworld.emc.module.bp.archive.service.impl.EmcArchiveFileServiceImpl;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportService;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.result.run.service.EmcRunService;
import com.sunwayworld.emc.module.bp.result.run.service.PfRunService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.executor.ExecutorHelper;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArchiveUtils;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.constant.CoreFileOperation;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileEventService;
import com.sunwayworld.module.item.file.service.CoreFileLogService;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import com.sunwayworld.module.item.notification.helper.CoreNotificationHelper;
import com.sunwayworld.module.sys.constant.service.CoreConstantService;
import com.sunwayworld.module.sys.role.service.CoreRoleUserService;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.sunwayworld.module.sys.role.bean.CoreRoleUserBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import java.lang.Long;
import java.util.Map;
import java.lang.Object;
import java.util.Set;
import java.lang.String;

@GikamBean
@Repository
public class PfArchiveFileServiceImpl
extends EmcArchiveFileServiceImpl
implements PfArchiveFileService {
    private static final Logger logger = LogManager.getLogger(PfArchiveFileServiceImpl.class);
    private static final String CONTRACT_FILES = "contractFiles";
    private static final String WORD_REPORT_FILES = "wordReportFiles";
    private static final String REPORT_AUDIT_RECORD_FILES = "reportAuditRecordFiles";
    private static final String CONTRACT_REVIEW_FILES = "contractReviewFiles";
    private static final String DETECT_SCHEME_FILES = "detectSchemeFiles";
    private static final String CALIBRATION_RECORD_FILES = "calibrationRecordFiles";
    private static final String SAMPLING_RECORD_FILES = "samplingRecordFiles";
    private static final String SAMPLING_VIDEO_FILES = "samplingVideoFiles";
    private static final String RECEIVE_RECORD_FILES = "receiveRecordFiles";
    private static final String ANALYSIS_RECORD_FILES = "analysisRecordFiles";
    private static final String OTHER_FILES = "otherFiles";
    private static final String WEIGHING_RECORD_FILES = "weighingRecordFiles";
    private static final String PREHANDLE_RECORD_FILES = "preHandleRecordFiles";
    private static final String TASK_RECORD_FILES = "taskRecordFiles";
    private static final String SAMPLING_CONTACT_RECORD_FILES = "samplingContactRecordFiles";
    private static final String DELIVERY_RECORD_FILES = "deliveryRecordFiles";
    private static final String STD_SOL_FILES = "stdSolFiles";
    private static final String PROJECT_FILE = "projectFile";
    private static final String ANALYSIS_FILE = "analysisFile";
    private static final String WEIGHING_RECORD_FILENAME = "称重记录";
    private static final String SAMPLING_CONTACT_RECORD_FILENAME = "现场联系单";
    private static final String TASK_RECORD_FILENAME = "样品分析任务表";
    private static final String DELIVERY_RECORD_FILENAME = "来样送检交接单(整体)";
    private static final String SAMPLING_RECORD_FILENAME = "采样记录";
    private static final String STD_SOL_FILENAME = "标定记录";
    private static final String ANALYSIS_RECORD_FILENAME = "实验室分析记录";
    private static final String PDF_SUFFIX = ".pdf";
    private static final List<String> fileExtList = Arrays.asList("xlsx", "xls", "pdf", "jpg", "png", "doc", "docx");
    @Autowired
    @Lazy
    private EmcProjectService projectService;
    @Autowired
    private EmcOrderTaskService orderTaskService;
    @Autowired
    private EmcRunService runService;
    @Autowired
    private EmcSampleService sampleService;
    @Lazy
    @Autowired
    private EmcOrderService orderService;
    @Lazy
    @Autowired
    private EmcFolderService folderService;
    @Autowired
    private CoreReportConfigLineService coreReportConfigLineService;
    @Autowired
    private CoreFileService coreFileService;
    @Autowired
    private CoreConstantService coreConstantService;
    @Autowired
    @Lazy
    private EmcReportService reportService;
    @Autowired
    @Lazy
    private CoreFileLogService fileLogService;
    @Autowired
    @Lazy
    private CoreFileEventService fileEventService;
    @Lazy
    @Autowired
    private CoreFileManager fileManager;
    @Autowired
    @Lazy
    private PfRunService pfRunService;
    @Autowired
    @Lazy
    private CoreRoleUserService coreRoleUserService;

    @Override
    public String archive(RestJsonWrapperBean wrapper) {
        List<EmcReportBean> reportList = wrapper.parse(EmcReportBean.class, 0);
        List<EmcArchiveCategoryBean> archiveCategoryList = wrapper.parse(EmcArchiveCategoryBean.class, 1);
        Long projectId = reportList.get(0).getProjId();
        EmcProjectBean project = (EmcProjectBean)this.projectService.selectById(projectId);
        Boolean withCoreFile = !StringUtils.isBlank(wrapper.getParamValue("withCoreFile"));
        String merge = wrapper.getParamValue("merge");
        ExecutorHelper.submit(() -> this.archive(archiveCategoryList, project, !StringUtils.isBlank(merge), withCoreFile));
        return null;
    }

    @Override
    public void archive(List<EmcArchiveCategoryBean> archiveCategoryList, EmcProjectBean project, Boolean merge, Boolean withCoreFile) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        try {
            archiveCategoryList.forEach(archiveCategory -> {
                switch (archiveCategory.getCategoryCode()) {
                    case "wordReportFiles":
                        this.archiveWordReportFiles(project);
                        break;
                    case "samplingRecordFiles":
                        this.archiveSamplingRecordFiles(project);
                        List<EmcArchiveCategoryBean> samplingList = archiveCategoryList.stream().filter(s -> SAMPLING_RECORD_FILES.equals(s.getCategoryCode())).collect(Collectors.toList());
                        this.mergeComposePdf(project, samplingList, withCoreFile);
                        break;
                    case "receiveRecordFiles":
                        this.archiveReceiveRecordFiles(project);
                        List<EmcArchiveCategoryBean> receiveList = archiveCategoryList.stream().filter(s -> RECEIVE_RECORD_FILES.equals(s.getCategoryCode())).collect(Collectors.toList());
                        this.mergeComposePdf(project, receiveList, false);
                        break;
                    case "analysisRecordFiles":
                        this.archiveAnalysisRecordFiles(project);
                        List<EmcArchiveCategoryBean> analysisRecordFiles = archiveCategoryList.stream().filter(s -> ANALYSIS_RECORD_FILES.equals(s.getCategoryCode())).collect(Collectors.toList());
                        this.mergeComposePdf(project, analysisRecordFiles, withCoreFile);
                        break;
                    case "taskRecordFiles":
                        this.archiveTaskRecordFiles(project);
                        List<EmcArchiveCategoryBean> taskRecordFiles = archiveCategoryList.stream().filter(s -> TASK_RECORD_FILES.equals(s.getCategoryCode())).collect(Collectors.toList());
                        this.mergeComposePdf(project, taskRecordFiles, false);
                        break;
                    case "projectFile":
                        this.archiveOtherSampleFiles(project);
                        this.archiveOtherRunFiles(project);
                        break;
                    case "analysisFile":
                        break;
                    default:
                        break;
                }
            });
            if (merge.booleanValue()) {
                this.mergeComposePdf(project, archiveCategoryList, false);
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        paramMap.put("projNo", project.getProjNo());
        List<CoreRoleUserBean> userList = this.coreRoleUserService.selectListByFilter(SearchFilter.instance().match("ROLEID", "1189433295257664").filter(MatchPattern.SEQ).match("PARENTORGID", LocalContextHelper.getLoginUser().getExt$Item("parentorgid")).filter(MatchPattern.SEQ), new Order[0]);
        List<String> userIdList = userList.stream().map(e -> e.getUserId()).distinct().collect(Collectors.toList());
        CoreNotificationHelper.sendNotification("reportArchive", userIdList, paramMap);
    }

    public void archiveOtherRunFiles(EmcProjectBean project) {
        List<CoreFileBean> allProjectFile = this.coreFileService.getDao().selectListByOneColumnValue((String)("T_EMC_PROJECT$" + project.getId()), "TARGETID", new Order[0]);
        if (!ObjectUtils.isEmpty(allProjectFile)) {
            List<CoreFileBean> filterProjectFile = allProjectFile.stream().filter(f -> ObjectUtils.equals(f.getExt(), ANALYSIS_RECORD_FILES) && ObjectUtils.equals(f.getBizCategory(), "run-zip")).collect(Collectors.toList());
            if (!ObjectUtils.isEmpty(filterProjectFile)) {
                this.coreFileService.deleteList(filterProjectFile);
            }
        }
        List<EmcRunBean> runList = this.pfRunService.getDao().selectListByOneColumnValue((Long)project.getId(), "PROJID", new Order[0]);
        List<String> targetIdList = runList.stream().map(f -> this.pfRunService.getDao().getTable() + "$" + f.getId()).collect(Collectors.toList());
        List<CoreFileBean> allFileList = this.coreFileService.getDao().selectListByOneColumnValues(targetIdList, "TARGETID", new Order[0]);
        if (!ObjectUtils.isEmpty(allFileList)) {
            List<CoreFileBean> filterAllFileList = allFileList.stream().filter(f -> !ObjectUtils.equals(f.getExt(), "composePdf")).collect(Collectors.toList());
            runList.forEach(f -> {
                List<CoreFileBean> zipFileList = filterAllFileList.stream().filter(a -> ObjectUtils.equals(a.getTargetId(), this.pfRunService.getDao().getTable() + "$" + f.getId())).collect(Collectors.toList());
                if (!ObjectUtils.isEmpty(zipFileList)) {
                    FilePathDTO filePath = FilePathDTO.of(FileScope.temp.name(), LocalDateTime.now(), StringUtils.randomUUID(16), f.getId() + ".zip");
                    List<Path> zipPathList = new ArrayList<Path>();
                    for (CoreFileBean coreFile : zipFileList) {
                        CoreFileBean file = this.coreFileService.selectDetail(coreFile.getId());
                        zipPathList.add(CoreFileUtils.getLocalPath(file));
                    }
                    ArchiveUtils.zip(FilePathManager.getLocalPath(filePath), zipPathList.toArray(new Path[zipPathList.size()]));
                    String tempFilePath = FilePathManager.getLocalPath(filePath).toString();
                    if (!StringUtils.isEmpty(tempFilePath) && tempFilePath.contains(f.getId())) {
                        CoreFileBean coreFileBean = new CoreFileBean();
                        coreFileBean.setTargetId("T_EMC_PROJECT$" + project.getId());
                        coreFileBean.setScope(FileScope.secure.name());
                        coreFileBean.setName(f.getId() + ".zip");
                        coreFileBean.setBizCategory("run-zip");
                        coreFileBean.setExt(ANALYSIS_RECORD_FILES);
                        this.upload(coreFileBean, new File(tempFilePath), f.getId() + ".zip");
                    }
                }
            });
        }
    }

    public void archiveOtherSampleFiles(EmcProjectBean project) {
        List<CoreFileBean> allProjectFile = this.coreFileService.getDao().selectListByOneColumnValue((String)("T_EMC_PROJECT$" + project.getId()), "TARGETID", new Order[0]);
        if (!ObjectUtils.isEmpty(allProjectFile)) {
            List<CoreFileBean> filterProjectFile = allProjectFile.stream().filter(f -> ObjectUtils.equals(f.getExt(), SAMPLING_RECORD_FILES) && ObjectUtils.equals(f.getBizCategory(), "sample-zip")).collect(Collectors.toList());
            if (!ObjectUtils.isEmpty(filterProjectFile)) {
                this.coreFileService.deleteList(filterProjectFile);
            }
        }
        List<EmcSampleBean> sampleList = this.sampleService.getDao().selectListByOneColumnValue((Long)project.getId(), "PROJID", new Order[0]);
        List<Long> sampleIdList = sampleList.stream().map(s -> s.getId()).collect(Collectors.toList());
        List<EmcOrderBean> orderList = this.orderService.getDao().selectListByOneColumnValues(sampleIdList, "SAMPLEID", new Order[0]);
        List<Long> folderIdList = orderList.stream().map(o -> o.getFolderId()).collect(Collectors.toList());
        List<EmcFolderBean> folderList = this.folderService.selectListByIds(folderIdList);
        List<String> targetIdList = sampleList.stream().map(f -> this.sampleService.getDao().getTable() + "$" + f.getId()).collect(Collectors.toList());
        List<CoreFileBean> allFileList = this.coreFileService.getDao().selectListByOneColumnValues(targetIdList, "TARGETID", new Order[0]);
        if (!ObjectUtils.isEmpty(allFileList)) {
            sampleList.forEach(f -> {
                List<CoreFileBean> zipFileList = allFileList.stream().filter(a -> ObjectUtils.equals(a.getTargetId(), this.sampleService.getDao().getTable() + "$" + f.getId())).collect(Collectors.toList());
                if (!ObjectUtils.isEmpty(zipFileList)) {
                    FilePathDTO filePath = FilePathDTO.of(FileScope.temp.name(), LocalDateTime.now(), StringUtils.randomUUID(16), f.getSampleNo() + ".zip");
                    List<Path> zipPathList = new ArrayList<Path>();
                    for (CoreFileBean coreFile : zipFileList) {
                        CoreFileBean file = this.coreFileService.selectDetail(coreFile.getId());
                        zipPathList.add(CoreFileUtils.getLocalPath(file));
                    }
                    ArchiveUtils.zip(FilePathManager.getLocalPath(filePath), zipPathList.toArray(new Path[zipPathList.size()]));
                    String tempFilePath = FilePathManager.getLocalPath(filePath).toString();
                    List<Long> folderIds = orderList.stream().filter(order -> ObjectUtils.equals(f.getId(), order.getSampleId())).map(o -> o.getFolderId()).collect(Collectors.toList());
                    String folderNames = folderList.stream().filter(folder -> folderIds.contains(folder.getId())).map(fo -> fo.getMonitorPointName()).collect(Collectors.joining("、"));
                    if (!StringUtils.isEmpty(tempFilePath) && tempFilePath.contains(f.getSampleNo())) {
                        CoreFileBean coreFileBean = new CoreFileBean();
                        coreFileBean.setTargetId("T_EMC_PROJECT$" + project.getId());
                        coreFileBean.setScope(FileScope.secure.name());
                        coreFileBean.setName(f.getSampleNo() + ":" + folderNames + ".zip");
                        coreFileBean.setBizCategory("sample-zip");
                        coreFileBean.setExt(SAMPLING_RECORD_FILES);
                        this.upload(coreFileBean, new File(tempFilePath), f.getSampleNo() + ".zip");
                    }
                }
            });
        }
    }

    @Transactional(timeout=200000)
    Long upload(CoreFileBean coreFile, File uploadFile, String name) {
        Long key = ApplicationContextHelper.getNextIdentity();
        CoreFileBean insertCoreFile = new CoreFileBean();
        insertCoreFile.setId(key);
        insertCoreFile.setTargetId(this.getReplactedTargetId(coreFile.getTargetId()));
        insertCoreFile.setName(name);
        insertCoreFile.setFileExt(FileUtils.getFileExtension(uploadFile.getName()));
        insertCoreFile.setMd5Name(EncryptUtils.MD5Encrypt(key + "$" + uploadFile.getName()));
        insertCoreFile.setScope(Objects.toString(coreFile.getScope(), FileScope.secure.name()));
        insertCoreFile.setValidTimeFrom(coreFile.getValidTimeFrom());
        insertCoreFile.setValidTimeTo(coreFile.getValidTimeTo());
        insertCoreFile.setSize(uploadFile.length());
        insertCoreFile.setExt(coreFile.getExt());
        insertCoreFile.setDownloadable(Constant.YES);
        insertCoreFile.setCreatedTime(LocalDateTime.now());
        insertCoreFile.setExt$(coreFile.getExt$());
        insertCoreFile.setBizCategory(coreFile.getBizCategory());
        this.coreFileService.getDao().insert((CoreFileBean)insertCoreFile);
        this.fileManager.upload(insertCoreFile, uploadFile);
        this.fileLogService.insert(key, CoreFileOperation.INSERT.name());
        this.fileEventService.publishFileEvent(CoreFileOperation.INSERT, insertCoreFile);
        return key;
    }

    protected String getReplactedTargetId(String targetId) {
        if (StringUtils.contains(targetId, "[{loginUserId}]")) {
            return StringUtils.replace(targetId, "[{loginUserId}]", LocalContextHelper.getLoginUserId());
        }
        return targetId;
    }

    private void mergeComposePdf(EmcProjectBean project, List<EmcArchiveCategoryBean> archiveCategoryList, boolean withCoreFile) {
        Long projectId = project.getId();
        CoreFileBean deleteFilterMergeFile = new CoreFileBean();
        deleteFilterMergeFile.setTargetId("T_EMC_PROJECT$" + projectId);
        deleteFilterMergeFile.setExt(archiveCategoryList.get(0).getCategoryCode());
        this.coreFileService.getDao().deleteBy((CoreFileBean)deleteFilterMergeFile, "TARGETID", "EXT");
        List<String> categoryCodeList = archiveCategoryList.stream().map(e -> e.getCategoryCode()).collect(Collectors.toList());
        List<CoreFileBean> fileBeanList = this.coreFileService.selectListByFilter(SearchFilter.instance().match("ext", "composePdf").filter(MatchPattern.SEQ).match("bizcategory", categoryCodeList).filter(MatchPattern.OR).match("targetid", "T_EMC_PROJECT$" + projectId).filter(MatchPattern.SEQ), new Order[0]);
        if (fileBeanList.isEmpty()) {
            return;
        }
        fileBeanList = fileBeanList.stream().sorted(Comparator.comparing(e -> e.getBizCategory(), Comparator.comparing(categoryCodeList::indexOf))).collect(Collectors.toList());
        String fileName = "file.pdf";
        if (SAMPLING_RECORD_FILES.equals(archiveCategoryList.get(0).getCategoryCode())) {
            fileName = "采样记录.pdf";
        } else if (ANALYSIS_RECORD_FILES.equals(archiveCategoryList.get(0).getCategoryCode())) {
            fileName = "分析记录.pdf";
            if (withCoreFile) {
                List<EmcOrderTaskBean> orderTaskBeanList = this.orderTaskService.getDao().selectListByOneColumnValue((Long)project.getId(), "projid", new Order[0]);
                List<String> runIdList = orderTaskBeanList.stream().filter(e -> e.getRunId() != null).map(e -> e.getRunId()).distinct().collect(Collectors.toList());
                List<String> targetIdList = runIdList.stream().map(e -> "T_EMC_RUN$" + e).collect(Collectors.toList());
                List<CoreFileBean> coreFileBeanList = this.coreFileService.selectListByFilter(SearchFilter.instance().match("TARGETID", targetIdList).filter(MatchPattern.OR), new Order[0]);
                coreFileBeanList = coreFileBeanList.stream().sorted(Comparator.comparing(e -> e.getCreatedTime())).collect(Collectors.toList());
                List<String> targetIdList2 = runIdList.stream().map(e -> "T_EMC_EQUIPT$" + e).collect(Collectors.toList());
                List<CoreFileBean> coreFileBeanList2 = this.coreFileService.selectListByFilter(SearchFilter.instance().match("TARGETID", targetIdList2).filter(MatchPattern.OR), new Order[0]);
                coreFileBeanList2 = coreFileBeanList2.stream().sorted(Comparator.comparing(e -> e.getCreatedTime())).collect(Collectors.toList());
                coreFileBeanList.addAll(coreFileBeanList2);
                List<CoreFileBean> archiveFileList = new ArrayList<>();
                for (CoreFileBean originalCoreFileBean : coreFileBeanList) {
                    File originalFile = CoreFileUtils.getLocalPath(originalCoreFileBean).toFile();
                    if (!originalFile.exists()) {
                        logger.error("附件归档文件不存在：" + originalFile.getName());
                        throw new InvalidDataException("文件不存在：" + originalFile.getName());
                    }
                    FilePathDTO archivePdfPathDTO = CoreFileUtils.toFilePath(FileScope.temp, FileUtils.getFileBaseName(originalFile.getName()) + PDF_SUFFIX);
                    Path pdfFilePath = FilePathManager.getLocalPath(archivePdfPathDTO);
                    FileUtils.makeDirs(pdfFilePath);
                    try {
                        SpireOfficeUtil.fileToPdf(originalFile, pdfFilePath.toFile());
                    }
                    catch (Exception e2) {
                        logger.error("附件归档文件转pdf失败：" + originalFile.getName());
                        throw new InvalidDataException("文件转pdf失败：" + originalFile.getName());
                    }
                    CoreFileBean archiveCoreFile = new CoreFileBean();
                    archiveCoreFile.setTargetId("T_EMC_PROJECT_ARCHIVE$" + project.getId());
                    archiveCoreFile.setScope(FileScope.secure.name());
                    Long CoreFileId = this.coreFileService.upload(archiveCoreFile, pdfFilePath.toFile());
                    archiveFileList.add(this.coreFileService.selectById(CoreFileId));
                }
                fileBeanList.addAll(archiveFileList);
            }
        } else if (TASK_RECORD_FILES.equals(archiveCategoryList.get(0).getCategoryCode())) {
            fileName = "任务流转单.pdf";
        } else if (RECEIVE_RECORD_FILES.equals(archiveCategoryList.get(0).getCategoryCode())) {
            fileName = "样品交接记录.pdf";
        }
        fileBeanList.forEach(CoreFileUtils::getLocalPath);
        List<FilePathDTO> fileRepresentationList = fileBeanList.stream().map(CoreFileUtils::toFilePath).collect(Collectors.toList());
        logger.info("PfArchiveFileService.mergeComposePdf.fileRepresentationList.size----------------" + fileRepresentationList.size());
        List<FilePathDTO> validFileRepresentationList = fileRepresentationList.stream().filter(fr -> FilePathManager.getLocalDirPath(fr).toFile().exists()).collect(Collectors.toList());
        logger.info("PfArchiveFileService.mergeComposePdf.validFileRepresentationList.size----------------" + validFileRepresentationList.size());
        logger.info("PfArchiveFileService.mergeComposePdf.validFileRepresentationList----------------" + validFileRepresentationList.stream().map(e -> FilePathManager.getLocalDirPath(e)).collect(Collectors.toList()));
        if (validFileRepresentationList.isEmpty()) {
            return;
        }
        FilePathDTO composedPdfFile = FilePathDTO.of(FileScope.temp.name(), "" + System.currentTimeMillis(), StringUtils.randomUUID(8), fileName);
        this.mergePdfList(validFileRepresentationList, composedPdfFile);
        logger.info("PfArchiveFileService.mergeComposePdf.mergePdfList----------------end");
        String tempFilePath = FilePathManager.getLocalPath(composedPdfFile).toString();
        logger.info("PfArchiveFileService.mergeComposePdf.tempFilePath----------------" + tempFilePath);
        if (!StringUtils.isEmpty(tempFilePath)) {
            CoreFileBean coreFileBean = new CoreFileBean();
            coreFileBean.setTargetId("T_EMC_PROJECT$" + projectId);
            coreFileBean.setScope(FileScope.secure.name());
            coreFileBean.setExt(archiveCategoryList.get(0).getCategoryCode());
            coreFileBean.setBizCategory(PROJECT_FILE);
            this.coreFileService.upload(coreFileBean, new File(tempFilePath));
        }
        CoreFileBean deleteFilterFile = new CoreFileBean();
        deleteFilterFile.setTargetId("T_EMC_PROJECT$" + projectId);
        deleteFilterFile.setExt("composePdf");
        this.coreFileService.getDao().deleteBy((CoreFileBean)deleteFilterFile, "TARGETID", "EXT");
    }

    private void mergePdfList(List<FilePathDTO> sourcePdfList, FilePathDTO targetPdf) {
        List<Path> pdfList = new ArrayList<>();
        sourcePdfList.forEach(s -> {
            Path path = FilePathManager.getLocalPath(s);
            this.fileManager.download((FilePathDTO)s, path);
            pdfList.add(path);
        });
        PfArchiveFileServiceImpl.mergePdfFiles(FilePathManager.getLocalPath(targetPdf), pdfList.toArray(new Path[0]));
    }

    private static void mergePdfFiles(Path destPath, Path ... pdfPaths) {
        if (ArrayUtils.isEmpty(pdfPaths)) {
            return;
        }
        String[] absolutePdfPaths = Arrays.stream(pdfPaths).map(Path::toString).toArray(String[]::new);
        try {
            PfArchiveFileServiceImpl.mergePdfFiles(destPath.toString(), absolutePdfPaths);
        }
        catch (Exception e) {
            throw new InvalidDataException("合成pdf失败：" + destPath);
        }
    }

    private static void mergePdfFiles(String targetFilePath, String ... sourcePdfPaths) throws Exception {
        if (sourcePdfPaths == null || sourcePdfPaths.length == 0) {
            throw new IllegalArgumentException("没有需要合并的PDF文件");
        }
        Path targetPath = Paths.get(targetFilePath, new String[0]);
        FileUtils.makeDirs(targetPath);
        PDFMergerUtility merger = new PDFMergerUtility();
        merger.setDestinationFileName(targetFilePath);
        for (String pdfPath : sourcePdfPaths) {
            merger.addSource(pdfPath);
        }
        merger.mergeDocuments(null);
        logger.info("PDF合并完成: " + targetFilePath);
    }

    private void archiveProjectFile(EmcProjectBean project) {
        Long projectId = project.getId();
        String targetId = this.projectService.getDao().getTable() + "$" + projectId;
        List<CoreFileBean> fileList = this.coreFileService.selectListByFilter(SearchFilter.instance().match("targetId", targetId).filter(MatchPattern.SEQ).match("ext", null).filter(MatchPattern.SEQ).match("fileExt", fileExtList).filter(MatchPattern.OR), new Order[0]);
        if (fileList.isEmpty()) {
            return;
        }
        this.fileToProjectPdf(project, fileList, PROJECT_FILE);
    }

    private void archiveAnalysisFile(EmcProjectBean project) {
        Long projId = project.getId();
        List<EmcOrderTaskBean> orderTaskList = this.orderTaskService.getDao().selectListByOneColumnValue((Long)project.getId(), "projId", new Order[0]);
        List<String> runIdList = orderTaskList.stream().filter(task -> !StringUtils.isEmpty(task.getRunId())).map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());
        if (runIdList.isEmpty()) {
            return;
        }
        List<String> targetIdList = new ArrayList<String>();
        for (String runId : runIdList) {
            targetIdList.add("T_EMC_RUN$" + runId);
            targetIdList.add("T_EMC_EQUIPT$" + runId);
        }
        List<CoreFileBean> fileList = this.coreFileService.selectListByFilter(SearchFilter.instance().match("targetId", targetIdList).filter(MatchPattern.OR).match("ext", null).filter(MatchPattern.SEQ).match("fileExt", fileExtList).filter(MatchPattern.OR), new Order[0]);
        if (fileList.isEmpty()) {
            return;
        }
        List<CoreFileBean> archiveFileList = new ArrayList<>();
        for (CoreFileBean originalCoreFileBean : fileList) {
            File originalFile = CoreFileUtils.getLocalPath(originalCoreFileBean).toFile();
            FilePathDTO archivePdfPathDTO = CoreFileUtils.toFilePath(FileScope.temp, FileUtils.getFileBaseName(originalFile.getName()) + PDF_SUFFIX);
            Path pdfFilePath = FilePathManager.getLocalPath(archivePdfPathDTO);
            FileUtils.makeDirs(pdfFilePath);
            SpireOfficeUtil.fileToPdf(originalFile, pdfFilePath.toFile());
            CoreFileBean archiveCoreFile = new CoreFileBean();
            archiveCoreFile.setTargetId("T_EMC_PROJECT$" + project.getId());
            archiveCoreFile.setScope(FileScope.secure.name());
            archiveCoreFile.setBizCategory(ANALYSIS_RECORD_FILES);
            archiveCoreFile.setExt("composePdf");
            this.coreFileService.upload(archiveCoreFile, pdfFilePath.toFile());
        }
    }

    private void fileToProjectPdf(EmcProjectBean project, List<CoreFileBean> fileList, String bizCategory) {
        if (fileList.isEmpty()) {
            return;
        }
        this.deleteExistedFile(project.getId(), bizCategory);
        List<CoreFileBean> archiveFileList = new ArrayList<>();
        for (CoreFileBean originalCoreFileBean : fileList) {
            File originalFile = CoreFileUtils.getLocalPath(originalCoreFileBean).toFile();
            FilePathDTO archivePdfPathDTO = CoreFileUtils.toFilePath(FileScope.temp, FileUtils.getFileBaseName(originalFile.getName()) + PDF_SUFFIX);
            Path pdfFilePath = FilePathManager.getLocalPath(archivePdfPathDTO);
            FileUtils.makeDirs(pdfFilePath);
            SpireOfficeUtil.fileToPdf(originalFile, pdfFilePath.toFile());
            CoreFileBean archiveCoreFile = new CoreFileBean();
            archiveCoreFile.setTargetId("T_EMC_PROJECT$" + project.getId());
            archiveCoreFile.setScope(FileScope.secure.name());
            archiveCoreFile.setBizCategory(bizCategory);
            archiveCoreFile.setExt("composePdf");
            this.coreFileService.upload(archiveCoreFile, pdfFilePath.toFile());
        }
    }

    @Override
    public void archiveWeighingRecordFiles(EmcProjectBean project) {
        String fineBaseUrl = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.fine.independent-deploy-server.url");
        if (StringUtils.isEmpty(fineBaseUrl)) {
            return;
        }
        List<EmcOrderTaskBean> taskList = this.orderTaskService.getDao().selectListByOneColumnValue((Long)project.getId(), "projId", Arrays.asList("id", "runId"), new Order[0]);
        List<String> runIdList = taskList.stream().filter(task -> !StringUtils.isEmpty(task.getRunId())).map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());
        if (runIdList.isEmpty()) {
            return;
        }
        List<EmcRunBean> runList = this.runService.getDao().selectListByOneColumnValues(runIdList, "id", Arrays.asList("id", "weighingReportLineId"), new Order[0]);
        List<EmcRunBean> validRunList = runList.stream().filter(run -> !ObjectUtils.isEmpty(run.getWeighingReportLineId())).collect(Collectors.toList());
        if (validRunList.isEmpty()) {
            return;
        }
        List<Long> reportLineIdList = validRunList.stream().map(EmcRunBean::getWeighingReportLineId).distinct().collect(Collectors.toList());
        List<CoreReportConfigLineBean> configLineList = this.coreReportConfigLineService.selectListByIds(reportLineIdList);
        if (configLineList.isEmpty()) {
            return;
        }
        this.deleteExistedFile(project.getId(), WEIGHING_RECORD_FILES);
        AtomicInteger fileIndex = new AtomicInteger();
        validRunList.forEach(run -> {
            CoreReportConfigLineBean reportLine = configLineList.stream().filter(line -> ObjectUtils.equals(line.getId(), run.getWeighingReportLineId())).findFirst().orElse(null);
            if (reportLine == null) {
                return;
            }
            Pair<String, CoreFileBean> resultPair = this.getDownloadUrl(fineBaseUrl, project.getId(), reportLine, run, WEIGHING_RECORD_FILES, WEIGHING_RECORD_FILENAME);
            if (StringUtils.isEmpty(resultPair.getFirst())) {
                return;
            }
            File file = this.downloadFile(resultPair.getFirst(), this.concatFileName(WEIGHING_RECORD_FILENAME, fileIndex.incrementAndGet()));
            if (file != null) {
                this.coreFileService.upload(resultPair.getSecond(), file);
            }
        });
    }

    public void archiveStdSolFiles(EmcProjectBean project) {
        String fineBaseUrl = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.fine.independent-deploy-server.url");
        if (StringUtils.isEmpty(fineBaseUrl)) {
            return;
        }
        List<EmcOrderTaskBean> taskList = this.orderTaskService.getDao().selectListByOneColumnValue((Long)project.getId(), "projId", Arrays.asList("id", "runId"), new Order[0]);
        List<String> runIdList = taskList.stream().filter(task -> !StringUtils.isEmpty(task.getRunId())).map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());
        if (runIdList.isEmpty()) {
            return;
        }
        List<EmcRunBean> runBeforeList = this.runService.getDao().selectListByOneColumnValues(runIdList, "id", new Order[0]);
        List<EmcRunBean> runList = new ArrayList<>();
        runBeforeList.forEach(r -> {
            if (r.getSol1Id() != null) {
                runList.add(r);
            } else if (r.getSol2Id() != null) {
                runList.add(r);
            }
        });
        if (runList.isEmpty()) {
            return;
        }
        Long reportLineId = 903396790157376L;
        CoreReportConfigLineBean reportLine = (CoreReportConfigLineBean)this.coreReportConfigLineService.selectById(reportLineId);
        this.deleteExistedFile(project.getId(), STD_SOL_FILES);
        AtomicInteger fileIndex = new AtomicInteger();
        runList.forEach(run -> {
            if (reportLine == null) {
                return;
            }
            Pair<String, CoreFileBean> resultPair = this.getDownloadUrl(fineBaseUrl, project.getId(), reportLine, run, STD_SOL_FILES, STD_SOL_FILENAME);
            if (StringUtils.isEmpty(resultPair.getFirst())) {
                return;
            }
            File file = this.downloadFile(resultPair.getFirst(), this.concatFileName(STD_SOL_FILENAME, fileIndex.incrementAndGet()));
            if (file != null) {
                this.coreFileService.upload(resultPair.getSecond(), file);
            }
        });
    }

    @Override
    public void archiveAnalysisRecordFiles(EmcProjectBean project) {
        String fineBaseUrl = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.fine.independent-deploy-server.url");
        String projNo = project.getProjNo();
        Long projId = project.getId();
        List<EmcOrderTaskBean> taskList = this.orderTaskService.getDao().selectListByOneColumnValue((Long)project.getId(), "projId", Arrays.asList("id", "runId"), new Order[0]);
        List<String> runIdList = taskList.stream().filter(task -> !StringUtils.isEmpty(task.getRunId())).map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());
        if (runIdList.isEmpty()) {
            return;
        }
        List<EmcRunBean> runList = this.runService.getDao().selectListByOneColumnValues(runIdList, "id", new Order[0]);
        List<CoreReportConfigLineBean> configLineBeanList = this.coreReportConfigLineService.selectListByIds(runList.stream().filter(r -> r.getReportConfigLineId() != null).map(EmcRunBean::getReportConfigLineId).collect(Collectors.toList()));
        if (configLineBeanList.isEmpty()) {
            return;
        }
        List<CoreReportConfigLineBean> frConfigLineBeanList = configLineBeanList.stream().filter(r -> "fanruan".equals(r.getReportCategory())).collect(Collectors.toList());
        if (!frConfigLineBeanList.isEmpty()) {
            List<String> configReportLineIdList = frConfigLineBeanList.stream().map(r -> this.coreReportConfigLineService.getDao().getTable() + "$" + r.getId()).collect(Collectors.toList());
            List<CoreFileBean> coreFileBeanList = this.coreFileService.getDao().selectListByOneColumnValues(configReportLineIdList, "TARGETID", new Order[0]);
            Map<String, CoreFileBean> pdfReportMap = new LinkedHashMap<>();
            runList.stream().forEach(r -> {
                Optional<CoreFileBean> coreFileBeanOptional = coreFileBeanList.stream().filter(l -> ObjectUtils.equals(this.coreReportConfigLineService.getDao().getTable() + "$" + r.getReportConfigLineId(), l.getTargetId())).findFirst();
                Optional<CoreReportConfigLineBean> configLineBeanOptional = frConfigLineBeanList.stream().filter(m -> ObjectUtils.equals(m.getId(), r.getReportConfigLineId())).findFirst();
                if (coreFileBeanOptional.isPresent() && configLineBeanOptional.isPresent()) {
                    CoreFileBean coreFileBean = coreFileBeanOptional.get();
                    String fileName = configLineBeanOptional.get().getReportConfigLineNo() + "/" + coreFileBean.getName();
                    if (!StringUtils.isEmpty(fileName)) {
                        StringBuilder reportUrl = new StringBuilder();
                        try {
                            reportUrl.append(fineBaseUrl).append("/decision/view/report?viewlet=").append(URLEncoder.encode(fileName, "UTF-8")).append("&format=pdf");
                        }
                        catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        String paramStr = this.parseReportParam(r, configLineBeanOptional.get().getParam());
                        if (!StringUtils.isEmpty(paramStr)) {
                            reportUrl.append("&").append(paramStr);
                        }
                        CoreFileBean newFileBean = new CoreFileBean();
                        newFileBean.setTargetId("T_EMC_RUN$" + r.getId());
                        newFileBean.setScope(FileScope.secure.name());
                        newFileBean.setExt("composePdf");
                        newFileBean.setExt$Item("PROJNO", projNo);
                        newFileBean.setExt$Item("RUNID", r.getId());
                        pdfReportMap.put(reportUrl.toString() + "#" + r.getId(), newFileBean);
                    }
                }
            });
            this.deleteExistedFile(projId, ANALYSIS_RECORD_FILES);
            pdfReportMap.entrySet().stream().forEach(e -> {
                File reportPdfFile = this.downloadFile((String)e.getKey(), ((CoreFileBean)e.getValue()).getExt$Item("PROJNO") + "_" + ((CoreFileBean)e.getValue()).getExt$Item("RUNID") + PDF_SUFFIX);
                if (reportPdfFile != null && reportPdfFile.length() != 0L) {
                    CoreFileBean coreFileBean = new CoreFileBean();
                    coreFileBean.setTargetId("T_EMC_PROJECT$" + projId);
                    coreFileBean.setScope(FileScope.secure.name());
                    coreFileBean.setExt("composePdf");
                    coreFileBean.setBizCategory(ANALYSIS_RECORD_FILES);
                    this.coreFileService.upload(coreFileBean, reportPdfFile);
                }
                else {
                    throw new InvalidDataException("合成原始记录pdf失败：" + reportPdfFile.getName());
                }
            });
        }
    }

    @Override
    public void archiveTaskRecordFiles(EmcProjectBean project) {
        List<CoreReportConfigLineBean> configLineList = this.coreReportConfigLineService.getDao().selectListByOneColumnValue((String)"xianchanglianxi", "REPORTCONFIGID", new Order[0]);
        if (configLineList.isEmpty()) {
            return;
        }
        String fineBaseUrl = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.fine.independent-deploy-server.url");
        if (StringUtils.isEmpty(fineBaseUrl)) {
            return;
        }
        CoreReportConfigLineBean samplingRecord = configLineList.stream().filter(rcl -> StringUtils.contains(rcl.getReportName(), "采样任务流转单(任务查询)")).findFirst().orElseGet(CoreReportConfigLineBean::new);
        CoreReportConfigLineBean nonSamplingRecord = configLineList.stream().filter(rcl -> StringUtils.contains(rcl.getReportName(), "送样任务流转单(任务查询)")).findFirst().orElseGet(CoreReportConfigLineBean::new);
        CoreReportConfigLineBean reportLine = "1".equals(project.getSample()) ? samplingRecord : nonSamplingRecord;
        String fileName = "1".equals(project.getSample()) ? "采样任务流转单(任务查询)" : "送样任务流转单(任务查询)";
        if (null == reportLine) {
            return;
        }
        Pair<String, CoreFileBean> resultPair = this.getDownloadUrl(fineBaseUrl, project.getId(), reportLine, project, TASK_RECORD_FILES, fileName);
        if (StringUtils.isEmpty(resultPair.getFirst())) {
            return;
        }
        this.deleteExistedFile(project.getId(), TASK_RECORD_FILES);
        File file = this.downloadFile(resultPair.getFirst(), fileName + PDF_SUFFIX);
        if (file != null) {
            this.coreFileService.upload(resultPair.getSecond(), file);
        }
    }

    @Override
    public void archiveSamplingContactRecordFiles(EmcProjectBean project) {
        List<CoreReportConfigLineBean> configLineList = this.coreReportConfigLineService.getDao().selectListByOneColumnValue((String)"xianchanglianxi", "REPORTCONFIGID", new Order[0]);
        if (configLineList.isEmpty()) {
            return;
        }
        String fineBaseUrl = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.fine.independent-deploy-server.url");
        if (StringUtils.isEmpty(fineBaseUrl)) {
            return;
        }
        Pair<String, CoreFileBean> resultPair = this.getDownloadUrl(fineBaseUrl, project.getId(), (CoreReportConfigLineBean)configLineList.get(0), project, SAMPLING_CONTACT_RECORD_FILES, SAMPLING_CONTACT_RECORD_FILENAME);
        if (StringUtils.isEmpty(resultPair.getFirst())) {
            return;
        }
        this.deleteExistedFile(project.getId(), SAMPLING_CONTACT_RECORD_FILES);
        File file = this.downloadFile(resultPair.getFirst(), "现场联系单.pdf");
        if (file != null) {
            this.coreFileService.upload(resultPair.getSecond(), file);
        }
    }

    @Override
    public void archiveDeliveryRecordFiles(EmcProjectBean project) {
        List<CoreReportConfigLineBean> configLineList = this.coreReportConfigLineService.getDao().selectListByOneColumnValue((String)"deliveringLabel", "REPORTCONFIGID", new Order[0]);
        if (configLineList.isEmpty()) {
            return;
        }
        String fineBaseUrl = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.fine.independent-deploy-server.url");
        if (StringUtils.isEmpty(fineBaseUrl)) {
            return;
        }
        CoreReportConfigLineBean reportLine = configLineList.stream().filter(line -> StringUtils.equals(line.getReportName(), DELIVERY_RECORD_FILENAME)).findFirst().orElse(null);
        if (null == reportLine) {
            return;
        }
        Pair<String, CoreFileBean> resultPair = this.getDownloadUrl(fineBaseUrl, project.getId(), reportLine, project, DELIVERY_RECORD_FILES, DELIVERY_RECORD_FILENAME);
        if (StringUtils.isEmpty(resultPair.getFirst())) {
            return;
        }
        this.deleteExistedFile(project.getId(), DELIVERY_RECORD_FILES);
        File file = this.downloadFile(resultPair.getFirst(), "来样送检交接单(整体).pdf");
        if (file != null) {
            this.coreFileService.upload(resultPair.getSecond(), file);
        }
    }

    @Override
    public void archiveSamplingRecordFiles(EmcProjectBean project) {
        String fineBaseUrl = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.fine.independent-deploy-server.url");
        if (StringUtils.isEmpty(fineBaseUrl)) {
            return;
        }
        List<EmcSampleBean> sampleListAll = this.sampleService.getDao().selectListByOneColumnValue((Long)project.getId(), "PROJID", new Order[0]);
        List<EmcSampleBean> sampleList = sampleListAll.stream().filter(s -> !"orderregistration".equals(s.getSource())).collect(Collectors.toList());
        List<String> samplingRecordLineIdList = sampleList.stream().map(EmcSampleBean::getReportConfigLineId).distinct().collect(Collectors.toList());
        List<String> archiveReportLineIdList = sampleList.stream().filter(sample -> !StringUtils.isEmpty(sample.getExt$Item("archiveReportLineId"))).distinct().map(sample -> sample.getExt$Item("archiveReportLineId")).collect(Collectors.toList());
        if (samplingRecordLineIdList.isEmpty() && archiveReportLineIdList.isEmpty()) {
            return;
        }
        if (archiveReportLineIdList.isEmpty()) {
            List<Long> reportConfigLineIdList = samplingRecordLineIdList.stream().mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());
            List<CoreReportConfigLineBean> lineList = this.coreReportConfigLineService.selectListByIds(reportConfigLineIdList);
            if (lineList.isEmpty()) {
                return;
            }
            this.deleteExistedFile(project.getId(), SAMPLING_RECORD_FILES);
            AtomicInteger fileIndex = new AtomicInteger();
            sampleList.forEach(sample -> lineList.stream().filter(line -> ObjectUtils.equals(sample.getReportConfigLineId(), line.getId() + "")).findFirst().ifPresent(configLine -> {
                Pair<String, CoreFileBean> resultPair = this.getDownloadUrl(fineBaseUrl, project.getId(), (CoreReportConfigLineBean)configLine, sample, SAMPLING_RECORD_FILES, SAMPLING_RECORD_FILENAME);
                if (StringUtils.isEmpty(resultPair.getFirst())) {
                    return;
                }
                File file = this.downloadFile(resultPair.getFirst(), this.concatFileName(SAMPLING_RECORD_FILENAME, fileIndex.incrementAndGet()));
                if (file != null) {
                    this.coreFileService.upload(resultPair.getSecond(), file);
                }
            }));
            return;
        }
        Set<Long> archiveReportLineIdSet = new HashSet<>();
        archiveReportLineIdList.forEach(lineId -> {
            List<Long> lineIdList = Arrays.stream(StringUtils.split(lineId, ",")).filter(NumberUtils::isNumber).mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());
            archiveReportLineIdSet.addAll(lineIdList);
        });
        List<CoreReportConfigLineBean> lineList = this.coreReportConfigLineService.selectListByIds(new ArrayList(archiveReportLineIdSet));
        if (lineList.isEmpty()) {
            return;
        }
        this.deleteExistedFile(project.getId(), SAMPLING_RECORD_FILES);
        AtomicInteger fileIndex = new AtomicInteger();
        sampleList.forEach(sample -> {
            String archiveReportLineId = sample.getExt$Item("archiveReportLineId");
            if (StringUtils.isEmpty(archiveReportLineId)) {
                return;
            }
            List<Long> curLineIdList = Arrays.stream(StringUtils.split(archiveReportLineId, ",")).filter(NumberUtils::isNumber).mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());
            curLineIdList.forEach(lineId -> lineList.stream().filter(line -> ObjectUtils.equals(line.getId(), lineId)).findFirst().ifPresent(configLine -> {
                Pair<String, CoreFileBean> resultPair = this.getDownloadUrl(fineBaseUrl, project.getId(), (CoreReportConfigLineBean)configLine, sample, SAMPLING_RECORD_FILES, SAMPLING_RECORD_FILENAME);
                if (StringUtils.isEmpty(resultPair.getFirst())) {
                    return;
                }
                File file = this.downloadFile(resultPair.getFirst(), this.concatFileName(SAMPLING_RECORD_FILENAME, fileIndex.incrementAndGet()));
                if (file != null) {
                    this.coreFileService.upload(resultPair.getSecond(), file);
                }
            }));
        });
    }
}
