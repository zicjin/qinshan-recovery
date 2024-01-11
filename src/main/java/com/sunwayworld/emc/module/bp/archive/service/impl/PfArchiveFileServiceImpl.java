package com.sunwayworld.emc.module.bp.archive.service.impl;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.sunwayworld.cloud.gikam.fr.bean.report.CoreReportConfigLineBean;
import com.sunwayworld.cloud.gikam.fr.service.report.CoreReportConfigLineService;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.module.bp.archive.bean.EmcArchiveCategoryBean;
import com.sunwayworld.emc.module.bp.archive.service.PfArchiveFileService;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.result.run.service.EmcRunService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.exception.UnsupportedException;
import com.sunwayworld.framework.io.file.FileRepresentation;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.cloud.CoreFileCloudService;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.sys.constant.bean.CoreConstantBean;
import com.sunwayworld.module.sys.constant.service.CoreConstantService;

/**
 * @author zhengxy@sunwayworld.com 2022年04月06日
 */
@GikamBean
@Repository
public class PfArchiveFileServiceImpl extends EmcArchiveFileServiceImpl implements PfArchiveFileService {

    /** 归档文件类型 */
    private final static String CONTRACT_FILES = "contractFiles";
    private static final String WORD_REPORT_FILES = "wordReportFiles";
    private final static String REPORT_AUDIT_RECORD_FILES = "reportAuditRecordFiles";
    private final static String CONTRACT_REVIEW_FILES = "contractReviewFiles";
    private final static String DETECT_SCHEME_FILES = "detectSchemeFiles";
    private final static String CALIBRATION_RECORD_FILES = "calibrationRecordFiles";
    private final static String SAMPLING_RECORD_FILES = "samplingRecordFiles";
    private final static String SAMPLING_VIDEO_FILES = "samplingVideoFiles";
    private final static String RECEIVE_RECORD_FILES = "receiveRecordFiles";
    private final static String ANALYSIS_RECORD_FILES = "analysisRecordFiles";
    private final static String OTHER_FILES = "otherFiles";

    private final static String WEIGHING_RECORD_FILES = "weighingRecordFiles";
    private final static String PREHANDLE_RECORD_FILES = "preHandleRecordFiles";
    private final static String TASK_RECORD_FILES = "taskRecordFiles";
    private final static String SAMPLING_CONTACT_RECORD_FILES = "samplingContactRecordFiles";
    private final static String DELIVERY_RECORD_FILES = "deliveryRecordFiles";
    private final static String STD_SOL_FILES = "stdSolFiles";


    private final static String WEIGHING_RECORD_FILENAME = "称重记录";
    private final static String SAMPLING_CONTACT_RECORD_FILENAME = "现场联系单";
    private final static String TASK_RECORD_FILENAME = "样品分析任务表";
    private final static String DELIVERY_RECORD_FILENAME = "来样送检交接单(整体)";
    private final static String SAMPLING_RECORD_FILENAME = "采样记录";
    private final static String STD_SOL_FILENAME = "标定记录";
    private final static String ANALYSIS_RECORD_FILENAME = "实验室分析记录";


    private final static String PDF_SUFFIX = ".pdf";

    @Autowired
    @Lazy
    private EmcProjectService projectService;

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Autowired
    private EmcRunService runService;

    @Autowired
    private EmcSampleService sampleService;

    @Autowired
    private CoreReportConfigLineService coreReportConfigLineService;

    @Autowired
    private CoreFileService coreFileService;
    
    @Autowired
    @Lazy
    private CoreFileCloudService fileCloudService;

    @Autowired
    private CoreConstantService coreConstantService;

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
                case WEIGHING_RECORD_FILES:
                    archiveWeighingRecordFiles(project);
                    break;
                case TASK_RECORD_FILES:
                    archiveTaskRecordFiles(project);
                    break;
                case SAMPLING_CONTACT_RECORD_FILES:
                    archiveSamplingContactRecordFiles(project);
                    break;
                case DELIVERY_RECORD_FILES:
                    archiveDeliveryRecordFiles(project);
                    break;
                case STD_SOL_FILES:
                    archiveStdSolFiles(project);
                    break;
                default:
                    break;
            }
        });

        return null;
    }

    @Override
    public void archiveWeighingRecordFiles(EmcProjectBean project) {
        String reportingServiceServerUrl = ApplicationContextHelper.getConstantValue("REPORTING_SERVICE_SERVER_URL");
        if (StringUtils.isEmpty(reportingServiceServerUrl)) {
            return;
        }

        List<EmcOrderTaskBean> taskList = orderTaskService.getDao().selectListByOneColumnValue(project.getId(), "projId", Arrays.asList("id", "runId"));
        List<String> runIdList = taskList.stream().filter(task -> !StringUtils.isEmpty(task.getRunId())).map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());
        if (runIdList.isEmpty()) {
            return;
        }

        List<EmcRunBean> runList = runService.getDao().selectListByOneColumnValues(runIdList, "id", Arrays.asList("id", "weighingReportLineId"));
        List<EmcRunBean> validRunList = runList.stream().filter(run -> !ObjectUtils.isEmpty(run.getWeighingReportLineId())).collect(Collectors.toList());
        if (validRunList.isEmpty()) {
            return;
        }

        List<Long> reportLineIdList = validRunList.stream().map(EmcRunBean::getWeighingReportLineId).distinct().collect(Collectors.toList());
        List<CoreReportConfigLineBean> configLineList = coreReportConfigLineService.selectListByIds(reportLineIdList);
        if (configLineList.isEmpty()) {
            return;
        }

        deleteExistedFile(project.getId(), WEIGHING_RECORD_FILES);

        AtomicInteger fileIndex = new AtomicInteger();
        validRunList.forEach(run -> {
            CoreReportConfigLineBean reportLine = configLineList.stream().filter(line -> ObjectUtils.equals(line.getId(), run.getWeighingReportLineId())).findFirst().orElse(null);
            if (reportLine == null) {
                return;
            }
            Pair<String, CoreFileBean> resultPair = getDownloadUrl(reportingServiceServerUrl, project.getId(), reportLine, run, WEIGHING_RECORD_FILES, WEIGHING_RECORD_FILENAME);
            if (StringUtils.isEmpty(resultPair.getFirst())) {
                return;
            }

            File file = downloadFile(resultPair.getFirst(), concatFileName(WEIGHING_RECORD_FILENAME, fileIndex.incrementAndGet()));
            if (file != null) {
                coreFileService.upload(resultPair.getSecond(), file);
            }

        });
    }

    public void archiveStdSolFiles(EmcProjectBean project) {
        String reportingServiceServerUrl = ApplicationContextHelper.getConstantValue("REPORTING_SERVICE_SERVER_URL");
        if (StringUtils.isEmpty(reportingServiceServerUrl)) {
            return;
        }

        List<EmcOrderTaskBean> taskList = orderTaskService.getDao().selectListByOneColumnValue(project.getId(), "projId", Arrays.asList("id", "runId"));
        List<String> runIdList = taskList.stream().filter(task -> !StringUtils.isEmpty(task.getRunId())).map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());
        if (runIdList.isEmpty()) {
            return;
        }

        List<EmcRunBean> runBeforeList = runService.getDao().selectListByOneColumnValues(runIdList, "id");
        //溶液1或者溶液2只要有一个不为空，该批即可用
        List<EmcRunBean> runList = new ArrayList<>();
        runBeforeList.forEach(r ->{
        	if(r.getSol1Id()!=null){
        		runList.add(r);
        	}else if(r.getSol2Id()!=null){
        		runList.add(r);
        	}
        });
        if(runList.isEmpty()){
            return;
        }
        //List<Long> reportLineIdList = validRunList.stream().map(EmcRunBean::getWeighingReportLineId).distinct().collect(Collectors.toList());
        Long reportLineId = 903396790157376L;
        CoreReportConfigLineBean reportLine = coreReportConfigLineService.selectById(reportLineId);
        //删除之前归档的标定文件
        deleteExistedFile(project.getId(), STD_SOL_FILES);

        AtomicInteger fileIndex = new AtomicInteger();
        runList.forEach(run -> {
            if (reportLine == null) {
                return;
            }
            Pair<String, CoreFileBean> resultPair = getDownloadUrl(reportingServiceServerUrl, project.getId(), reportLine, run, STD_SOL_FILES, STD_SOL_FILENAME);
            if (StringUtils.isEmpty(resultPair.getFirst())) {
                return;
            }

            File file = downloadFile(resultPair.getFirst(), concatFileName(STD_SOL_FILENAME, fileIndex.incrementAndGet()));
            if (file != null) {
                coreFileService.upload(resultPair.getSecond(), file);
            }

        });
    }
    
    @Override
    public void archiveAnalysisRecordFiles(EmcProjectBean project) {
        // 原始分析记录在分析审核提交时已经生成
    	//项目可能有多个批
    	//把多个批的项目的单个原始记录文件都查出来，拼接起来
    	String projNo = project.getProjNo();
    	Long projId = project.getId();
        List<EmcOrderTaskBean> taskList = orderTaskService.getDao().selectListByOneColumnValue(project.getId(), "projId", Arrays.asList("id", "runId"));
        List<String> runIdList = taskList.stream().filter(task -> !StringUtils.isEmpty(task.getRunId())).map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());
        if (runIdList.isEmpty()) {
            return;
        };
        List<EmcRunBean> runList = runService.getDao().selectListByOneColumnValues(runIdList, "id");
        List<CoreReportConfigLineBean> configLineBeanList = coreReportConfigLineService
                .selectListByIds(runList.stream().filter(r -> r.getReportConfigLineId() != null).map(EmcRunBean::getReportConfigLineId).collect(Collectors.toList()));
        if (configLineBeanList.isEmpty()) {
            return;
        }
        // Reporting Service 报表模板
        List<CoreReportConfigLineBean> rsConfigLineList = configLineBeanList.stream().filter(r -> "reportingService".equals(r.getReportCategory())).collect(Collectors.toList());
        if (!rsConfigLineList.isEmpty()) {
            // 获取配置的Reporting Service 服务器地址
            CoreConstantBean coreConstantBean = coreConstantService.selectByIdIfPresent("REPORTING_SERVICE_SERVER_URL");
            if (coreConstantBean == null) {
                return;
            }
            String reportingServiceServer = coreConstantBean.getConstantValue();
            if (StringUtils.isEmpty(reportingServiceServer)) {
                return;
            }

            Map<String, CoreFileBean> pdfReportMap = new LinkedHashMap<>();
            runList.stream().forEach(r -> {
                Optional<CoreReportConfigLineBean> reportConfigLineBeanOptional = rsConfigLineList.stream().filter(l -> ObjectUtils.equals(r.getReportConfigLineId(), l.getId())).findFirst();
                if (reportConfigLineBeanOptional.isPresent()) {
                    CoreReportConfigLineBean reportConfigLineBean = reportConfigLineBeanOptional.get();
                    String reportPath = reportConfigLineBean.getExt$Item("reportpath");

                    if (!StringUtils.isEmpty(reportPath)) {
                        StringBuilder reportUrl = new StringBuilder();

                        String reportFilePath = reportPath.substring(0, reportPath.indexOf("&") > 0 ? reportPath.indexOf("&") : reportPath.length());
                        try {
                            reportUrl.append(reportingServiceServer).append("?").append(URLEncoder.encode(reportFilePath, "UTF-8")).append("&rs:Command=Render&rs:Format=PDF");
                            String paramStr = parseReportParam(r, reportConfigLineBean.getParam());
                            if (!StringUtils.isEmpty(paramStr)) {
                                reportUrl.append("&").append(paramStr);
                            }
                            reportUrl.append("&").append("RUNID="+r.getId());
                            reportUrl.append("&").append("projid="+projId);
                            CoreFileBean coreFileBean = new CoreFileBean();
                            coreFileBean.setTargetId("T_EMC_RUN" + "$" + r.getId());
                            coreFileBean.setScope(FileScope.secure.name());
                            coreFileBean.setExt("composePdf");
                            coreFileBean.setExt$Item("PROJNO",projNo);
                            coreFileBean.setExt$Item("RUNID",r.getId());
                            pdfReportMap.put(reportUrl.toString()+"#"+r.getId(), coreFileBean);
                        } catch (UnsupportedEncodingException e) {
                            throw new UnsupportedException();
                        }
                    }
                }
            });
            //删除之前归档的实验室
            deleteExistedFile(projId, ANALYSIS_RECORD_FILES);
            pdfReportMap.entrySet().stream().forEach(e -> {
                //修改为每个批项目
            	//File reportPdfFile = downloadFile(e.getKey().substring(0, e.getKey().indexOf("#")), e.getValue().getExt$Item("PROJNO")+".pdf");
            	File reportPdfFile = downloadFile(e.getKey(), e.getValue().getExt$Item("PROJNO")+"_"+e.getValue().getExt$Item("RUNID")+".pdf");
				if (reportPdfFile != null) {
					  CoreFileBean coreFileBean = new CoreFileBean();
                      coreFileBean.setTargetId("T_EMC_PROJECT" + "$" + projId);
                      coreFileBean.setScope(FileScope.secure.name());
                      coreFileBean.setExt("composePdf");
                      coreFileBean.setBizCategory(ANALYSIS_RECORD_FILES);
                      coreFileService.upload(coreFileBean, reportPdfFile);
                }
			

            });
        }
    }
    
    @Override
    public void archiveTaskRecordFiles(EmcProjectBean project) {
        List<CoreReportConfigLineBean> configLineList = coreReportConfigLineService.getDao().selectListByOneColumnValue("deliveringLabel", "REPORTCONFIGID");
        if (configLineList.isEmpty()) {
            return;
        }

        String reportingServiceServerUrl = ApplicationContextHelper.getConstantValue("REPORTING_SERVICE_SERVER_URL");
        if (StringUtils.isEmpty(reportingServiceServerUrl)) {
            return;
        }

        CoreReportConfigLineBean reportLine = configLineList.stream().filter(line -> StringUtils.equals(line.getReportName(), TASK_RECORD_FILENAME)).findFirst().orElse(null);
        if (null == reportLine) {
            return;
        }

        Pair<String, CoreFileBean> resultPair = getDownloadUrl(reportingServiceServerUrl, project.getId(), reportLine, project, TASK_RECORD_FILES, TASK_RECORD_FILENAME);
        if (StringUtils.isEmpty(resultPair.getFirst())) {
            return;
        }

        deleteExistedFile(project.getId(), TASK_RECORD_FILES);

        File file = downloadFile(resultPair.getFirst(), TASK_RECORD_FILENAME + PDF_SUFFIX);
        if (file != null) {
            coreFileService.upload(resultPair.getSecond(), file);
        }
    }

    @Override
    public void archiveSamplingContactRecordFiles(EmcProjectBean project) {
        List<CoreReportConfigLineBean> configLineList = coreReportConfigLineService.getDao().selectListByOneColumnValue("xianchanglianxi", "REPORTCONFIGID");
        if (configLineList.isEmpty()) {
            return;
        }

        String reportingServiceServerUrl = ApplicationContextHelper.getConstantValue("REPORTING_SERVICE_SERVER_URL");
        if (StringUtils.isEmpty(reportingServiceServerUrl)) {
            return;
        }

        Pair<String, CoreFileBean> resultPair = getDownloadUrl(reportingServiceServerUrl, project.getId(), configLineList.get(0), project, SAMPLING_CONTACT_RECORD_FILES, SAMPLING_CONTACT_RECORD_FILENAME);
        if (StringUtils.isEmpty(resultPair.getFirst())) {
            return;
        }

        deleteExistedFile(project.getId(), SAMPLING_CONTACT_RECORD_FILES);

        File file = downloadFile(resultPair.getFirst(), SAMPLING_CONTACT_RECORD_FILENAME + PDF_SUFFIX);
        if (file != null) {
            coreFileService.upload(resultPair.getSecond(), file);
        }
    }

    @Override
    public void archiveDeliveryRecordFiles(EmcProjectBean project) {
        List<CoreReportConfigLineBean> configLineList = coreReportConfigLineService.getDao().selectListByOneColumnValue("deliveringLabel", "REPORTCONFIGID");
        if (configLineList.isEmpty()) {
            return;
        }

        String reportingServiceServerUrl = ApplicationContextHelper.getConstantValue("REPORTING_SERVICE_SERVER_URL");
        if (StringUtils.isEmpty(reportingServiceServerUrl)) {
            return;
        }

        CoreReportConfigLineBean reportLine = configLineList.stream().filter(line -> StringUtils.equals(line.getReportName(), DELIVERY_RECORD_FILENAME)).findFirst().orElse(null);
        if (null == reportLine) {
            return;
        }

        Pair<String, CoreFileBean> resultPair = getDownloadUrl(reportingServiceServerUrl, project.getId(), reportLine, project, DELIVERY_RECORD_FILES, DELIVERY_RECORD_FILENAME);
        if (StringUtils.isEmpty(resultPair.getFirst())) {
            return;
        }

        deleteExistedFile(project.getId(), DELIVERY_RECORD_FILES);

        File file = downloadFile(resultPair.getFirst(), DELIVERY_RECORD_FILENAME + PDF_SUFFIX);
        if (file != null) {
            coreFileService.upload(resultPair.getSecond(), file);
        }
    }

    @Override
    public void archiveSamplingRecordFiles(EmcProjectBean project) {
        String reportingServiceServerUrl = ApplicationContextHelper.getConstantValue("REPORTING_SERVICE_SERVER_URL");
        if (StringUtils.isEmpty(reportingServiceServerUrl)) {
            return;
        }

        List<EmcSampleBean> sampleList = sampleService.getDao().selectListByOneColumnValue(project.getId(), "PROJID");
        List<String> samplingRecordLineIdList = sampleList.stream().map(EmcSampleBean::getReportConfigLineId).distinct().collect(Collectors.toList());
        List<String> archiveReportLineIdList = sampleList.stream().filter(sample -> !StringUtils.isEmpty(sample.getExt$Item("archiveReportLineId"))).distinct().map(sample -> sample.getExt$Item("archiveReportLineId")).collect(Collectors.toList());

        if (samplingRecordLineIdList.isEmpty() && archiveReportLineIdList.isEmpty()) {
            return;
        }

        // 如果归档记录模板未配置，则直接使用采样记录模板归档
        if (archiveReportLineIdList.isEmpty()) {
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

            return;
        }

        Set<Long> archiveReportLineIdSet = new HashSet<>();
        archiveReportLineIdList.forEach(lineId -> {
            List<Long> lineIdList = Arrays.stream(StringUtils.split(lineId, ",")).filter(NumberUtils::isNumber).mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());
            archiveReportLineIdSet.addAll(lineIdList);
        });

        List<CoreReportConfigLineBean> lineList = coreReportConfigLineService.selectListByIds(new ArrayList<>(archiveReportLineIdSet));
        if (lineList.isEmpty()) {
            return;
        }

        deleteExistedFile(project.getId(), SAMPLING_RECORD_FILES);

        AtomicInteger fileIndex = new AtomicInteger();
        sampleList.forEach(sample -> {
            String archiveReportLineId = sample.getExt$Item("archiveReportLineId");
            if (StringUtils.isEmpty(archiveReportLineId)) {
                return;
            }

            List<Long> curLineIdList = Arrays.stream(StringUtils.split(archiveReportLineId, ",")).filter(NumberUtils::isNumber).mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());
            curLineIdList.forEach(lineId -> lineList.stream().filter(line -> ObjectUtils.equals(line.getId(), lineId)).findFirst().ifPresent(configLine -> {
                Pair<String, CoreFileBean> resultPair = getDownloadUrl(reportingServiceServerUrl, project.getId(), configLine, sample, SAMPLING_RECORD_FILES,"采样记录");
                if (StringUtils.isEmpty(resultPair.getFirst())) {
                    return;
                }

                File file = downloadFile(resultPair.getFirst(), concatFileName(SAMPLING_RECORD_FILENAME, fileIndex.incrementAndGet()));
                if (file != null) {
                    coreFileService.upload(resultPair.getSecond(), file);
                }
            }));
        });
    }
}
