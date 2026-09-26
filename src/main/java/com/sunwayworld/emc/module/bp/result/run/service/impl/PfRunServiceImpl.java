package com.sunwayworld.emc.module.bp.result.run.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.cloud.module.fr.report.service.CoreReportConfigLineService;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcAnalysisRecordDTO;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import com.sunwayworld.module.item.notification.bean.CoreNotificationInstanceBean;
import com.sunwayworld.module.item.notification.bean.CoreNotificationInstanceToBean;
import com.sunwayworld.module.item.notification.service.CoreNotificationInstanceService;
import com.sunwayworld.module.item.notification.service.CoreNotificationInstanceToService;
import org.apache.commons.collections4.ListUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfBizConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.projectnode.ProjectNodeUtils;
import com.sunwayworld.emc.framework.utils.EmcPdfUtils;
import com.sunwayworld.emc.framework.word.ReportingServiceUtils;
import com.sunwayworld.emc.module.bp.auditrecord.bean.EmcAuditRecordBean;
import com.sunwayworld.emc.module.bp.auditrecord.service.EmcAuditRecordService;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderContainerBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.computing.EmcOrderTaskResultComputingEngine;
import com.sunwayworld.emc.module.bp.bp.computing.async.AsyncFormulaComputingService;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderContainerService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskLogService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.PfOrderTaskService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportService;
import com.sunwayworld.emc.module.bp.result.curve.bean.EmcCurveBean;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcQcRelatedOrderBean;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.result.run.service.EmcQcRelatedOrderService;
import com.sunwayworld.emc.module.bp.result.run.service.PfRunService;
import com.sunwayworld.emc.module.bp.result.run.validator.EmcAddQcValidator;
import com.sunwayworld.emc.module.bp.result.run.validator.EmcDelQcValidator;
import com.sunwayworld.emc.module.bp.result.run.validator.EmcRunAuditSubmitValidator;
import com.sunwayworld.emc.module.bp.result.run.validator.EmcRunSubmitValidator;
import com.sunwayworld.emc.module.bp.result.run.validator.PfAddRunValidator;
import com.sunwayworld.emc.module.bp.result.run.validator.PfDelQcTaskValidator;
import com.sunwayworld.emc.module.bp.result.run.validator.PfMethodEditValidator;
import com.sunwayworld.emc.module.bp.result.temphum.bean.PfRunTempHumidityBean;
import com.sunwayworld.emc.module.bp.result.temphum.service.PfRunTempHumidityService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlMaintainBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlMaintainItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockConcentBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlMaintainItemService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlMaintainService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockConcentService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyDataService;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean.EmcQcCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.service.EmcQcCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcTestCapacityBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcTestCapacityService;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.EmcMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemRevisionBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodDefaultQcBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemRevisionService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodDefaultQcService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptTestMethodService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.exception.UnsupportedException;
import com.sunwayworld.framework.executor.ExecutorHelper;
import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.utils.ArchiveUtils;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserPermissionBean;
import com.sunwayworld.module.mdm.user.service.CoreUserPermissionService;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.constant.bean.CoreConstantBean;
import com.sunwayworld.module.sys.constant.service.CoreConstantService;

/**
 * @author zhengxy@sunwayworld.com 2021年05月28日
 */
@Repository
@GikamBean
public class PfRunServiceImpl extends EmcRunServiceImpl implements PfRunService {

    private static final Logger logger = LogManager.getLogger(PfRunServiceImpl.class);

    private static final String COMPOSE_ALL_RUNLIST_REPORT_SUCCESS = "composeAllRunListReportSuccess";

    private static final String COMPOSE_ALL_RUNLIST_REPORT_FAILURE = "composeAllRunListReportFailure";

    @Override
    public EmcAnalysisRecordDTO selectRunAuditFile(RestJsonWrapperBean wrapper) {
        String runId = wrapper.getParamValue("runId");
        EmcAnalysisRecordDTO dto = new EmcAnalysisRecordDTO();
        if (!ObjectUtils.isEmpty(runId)) {
            List<CoreFileBean> files = new ArrayList<>();
            files.addAll(coreFileService.selectListByFilter(SearchFilter.instance().match("targetId", "T_EMC_EQUIPT$" + runId).filter(MatchPattern.SEQ), new Order[0]));
            files.addAll(coreFileService.selectListByFilter(SearchFilter.instance().match("targetId", "T_EMC_RUN$" + runId).filter(MatchPattern.SEQ), new Order[0]));
            dto.setOtherRecordList(files);
        }
        return dto;
    }

    @Override
    @Transactional
    public void bathUploadFile(RestJsonWrapperBean wrapper) {
        String[] ids = wrapper.getParamValue("ids").split(",");
        String[] fileIds = wrapper.getParamValue("fileIds").split(",");
        String dbTable = wrapper.getParamValue("dbTable");
        if (dbTable == null) dbTable = "T_EMC_RUN";
        List<Long> fileIdList = Arrays.stream(fileIds).map(NumberUtils::parseLong).collect(Collectors.toList());
        List<CoreFileBean> sourceFiles = coreFileService.getDao().selectListByIds(fileIdList, new Order[0]);
        List<CoreFileBean> insertFiles = new ArrayList<>();
        for (CoreFileBean source : sourceFiles) {
            for (String id : ids) {
                CoreFileBean file = new CoreFileBean();
                BeanUtils.copyProperties(source, file);
                file.setId(ApplicationContextHelper.getNextIdentity());
                file.setTargetId(dbTable + "$" + id);
                insertFiles.add(file);
            }
        }
        coreFileService.getDao().insert(insertFiles);
    }

    @Override
    public String exportOriginalRecordExcelZip(RestJsonWrapperBean wrapper) {
        List<EmcRunBean> selectedRunList = wrapper.parse(EmcRunBean.class);
        if (selectedRunList.isEmpty()) return "";
        List<String> runIdList = selectedRunList.stream().map(run -> run.getId() + "").distinct().collect(Collectors.toList());
        List<EmcRunBean> runList = getDao().selectListByIds(runIdList, new Order[0]);
        if (runList.isEmpty()) return "";
        List<Long> reportConfigLineIdList = runList.stream().map(EmcRunBean::getReportConfigLineId).filter(v -> !ObjectUtils.isEmpty(v)).distinct().collect(Collectors.toList());
        List<CoreReportConfigLineBean> configLineList = coreReportConfigLineService.selectListByIds(reportConfigLineIdList);
        if (configLineList.isEmpty()) return "";
        String reportingServiceServer = ApplicationContextHelper.getConstantValue("REPORTING_SERVICE_SERVER_URL");
        String fineBaseUrl = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.fine.independent-deploy-server.url");
        List<Path> exportPathList = new ArrayList<>();
        try {
            for (EmcRunBean run : runList) {
                if (ObjectUtils.isEmpty(run.getReportConfigLineId())) continue;
                CoreReportConfigLineBean configLine = configLineList.stream().filter(c -> ObjectUtils.equals(c.getId(), run.getReportConfigLineId())).findFirst().orElse(null);
                if (configLine == null) continue;
                String downloadUrl = null;
                if ("reportingService".equals(configLine.getReportCategory())) {
                    downloadUrl = ReportingServiceUtils.getDownloadUrl(reportingServiceServer, configLine, run, "EXCEL");
                } else if ("fanruan".equals(configLine.getReportCategory())) {
                    List<String> targetIds = Collections.singletonList(coreReportConfigLineService.getDao().getTable() + "$" + configLine.getId());
                    CoreFileBean file = coreFileService.getDao().selectListByOneColumnValues(targetIds, "TARGETID", new Order[0]).stream().findFirst().orElse(null);
                    if (file != null) downloadUrl = buildRunFanruanOriginalRecordExcelUrl(fineBaseUrl, configLine, file.getName(), run);
                }
                if (!StringUtils.isEmpty(downloadUrl)) exportPathList.add(downloadToRunTempPath(downloadUrl, "分析原始记录_" + run.getId() + ".xls"));
            }
            if (exportPathList.isEmpty()) return "";
            FilePathDTO filePath = FilePathDTO.of(FileScope.temp.name(), LocalDateTime.now(), StringUtils.randomUUID(16), "分析原始记录批量导出.zip");
            ArchiveUtils.zip(FilePathManager.getLocalPath(filePath), exportPathList.toArray(new Path[0]));
            CoreFileManager fileManager = ApplicationContextHelper.getBean(CoreFileManager.class);
            fileManager.upload(filePath, FilePathManager.getLocalPath(filePath));
            return fileManager.getDownloadUrl(filePath);
        } catch (Exception e) {
            throw new FileException(e);
        }
    }

    private Path downloadToRunTempPath(String url, String fileName) {
        FilePathDTO filePath = FilePathDTO.of(FileScope.temp.name(), LocalDateTime.now(), StringUtils.randomUUID(16), fileName);
        try {
            Files.createDirectories(FilePathManager.getLocalDirPath(filePath), new FileAttribute[0]);
        } catch (IOException e) {
            throw new FileException(e);
        }
        HttpClientManager.getInstance().download(url, FilePathManager.getLocalPath(filePath).toFile());
        return FilePathManager.getLocalPath(filePath);
    }

    private String buildRunFanruanOriginalRecordExcelUrl(String fineBaseUrl, CoreReportConfigLineBean configLine, String reportFileName, EmcRunBean source) {
        if (StringUtils.isEmpty(fineBaseUrl) || StringUtils.isEmpty(reportFileName)) return null;
        try {
            String fileName = configLine.getReportConfigLineNo() + "/" + reportFileName;
            StringBuilder url = new StringBuilder(fineBaseUrl).append("/decision/view/report?viewlet=")
                    .append(URLEncoder.encode(fileName, "UTF-8")).append("&format=excel");
            String params = parseReportParam(source, configLine.getParam());
            if (!StringUtils.isEmpty(params)) url.append("&").append(params);
            return url.toString();
        } catch (UnsupportedEncodingException e) {
            throw new FileException(e);
        }
    }

    @Autowired
    @Lazy
    private EmcProjectService projectService;

    @Autowired
    @Lazy
    private EmcFolderService folderService;

    @Autowired
    @Lazy
    private EmcReportService reportService;

    @Autowired
    @Lazy
    private EmcOrderService orderService;

    @Autowired
    @Lazy
    private PfOrderTaskService orderTaskService;

    @Autowired
    @Lazy
    private EmcOrderTaskResultService orderTaskResultService;

    @Autowired
    @Lazy
    private EmcSampleService sampleService;

    @Autowired
    private CoreUserPermissionService userPermissionService;

    @Autowired
    private EmcOrderTaskLogService orderTaskLogService;

    @Autowired
    private EmcAuditRecordService auditRecordService;

    @Lazy
    @Autowired
    private EmcStockService stockService;

    @Autowired
    @Lazy
    private EmcStockConcentService stockConcentService;

    @Autowired
    @Lazy
    private EmcMtlMaintainItemService mtlMaintainItemService;

    @Autowired
    @Lazy
    private EmcItemService itemService;

    @Autowired
    @Lazy
    private EmcOrderItemGroupService orderItemGroupService;

    @Autowired
    @Lazy
    private EmcGroupItemService groupItemService;

    @Autowired
    private EmcCapacityItemGroupService capacityItemGroupService;

    @Autowired
    private EmcItemGroupService itemGroupService;

    @Autowired
    @Lazy
    private EmcQcCategoryService qcCategoryService;

    @Autowired
    private EmcMethodService methodService;

    @Autowired
    private EmcTestMethodService testMethodService;

    @Autowired
    private EmcPropertyDataService propertyDataService;

    @Autowired
    private EmcTestMethodDefaultQcService defaultQcService;

    @Autowired
    private EmcQcRelatedOrderService qcRelatedOrderService;

    @Autowired
    @Lazy
    private CoreFileService coreFileService;

    @Autowired
    @Lazy
    private EmcEquiptService equiptService;

    @Autowired
    private CoreConstantService coreConstantService;

    @Autowired
    private CoreReportConfigLineService coreReportConfigLineService;

    @Autowired
    private EmcEquiptTestMethodService equiptTestMethodService;

    @Autowired
    @Lazy
    private PfRunTempHumidityService pfRunTempHumidityService;

    @Autowired
    @Lazy
    private EmcTestCapacityService testCapacityService;

    @Autowired
    @Lazy
    private EmcMtlMaintainService mtlMaintainService;

    @Autowired
    private AsyncFormulaComputingService asyncFormulaComputingService;

    @Autowired
    private CoreOrgService coreOrgService;

    @Autowired
    private CoreNotificationInstanceService notificationInstanceService;

    @Autowired
    private CoreNotificationInstanceToService notificationInstanceToService;

    @Autowired
    private EmcItemRevisionService itemRevisionService;

    @Autowired
    private EmcCapacityItemService capacityItemService;

    @Autowired
    private EmcOrderContainerService orderContainerService;

    /**
     * 添加批功能，主要步骤如下 1、取RestJsonWrapperBean数据。 2、装配run表数据 3、区分正常样品和qc样，赋值和设置杯号
     * 4、结果表装配数据 5、属性数据表装配数据 6、插入数据库 7、分析项有默认值的自动触发公式
     */
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    @ValidateDataWith(PfAddRunValidator.class)
    public String insert(RestJsonWrapperBean wrapper) {
        // 1、取RestJsonWrapperBean数据。
        EmcEquiptBean equipt = wrapper.parseUnique(EmcEquiptBean.class);
        if ("1".equals(equipt.getEquiptRecord()) && (equipt.getExt$Item("equiptrecorddate") == null
                || DateTimeUtils.isBefore(DateTimeUtils.parseLocalDate(equipt.getExt$Item("equiptrecorddate")), LocalDate.now()))) {
            throw new InvalidDataException("仪器未检定");
        }
        List<EmcOrderTaskBean> selectOrderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<Long> selectOrderIdList = selectOrderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<Long> testMethodIdList = Arrays.stream(StringUtils.split(selectOrderTaskList.get(0).getExt$Item("testMethodIds"), ",")).filter(NumberUtils::isNumber).mapToLong(NumberUtils::parseLong).boxed().distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> allTaskList = orderTaskService.selectListByFilter(SearchFilter.instance()
                .match("orderId", selectOrderIdList).filter(MatchPattern.OR)
                .match("testMethodId", testMethodIdList).filter(MatchPattern.OR)
                .match("analysisType", Arrays.asList(EmcBizConstant.ANALYSISCATEGORY_ANALYSIS, "Preprocessing")).filter(MatchPattern.OR));
        // 过滤掉样品中已经建批的测试
        List<EmcOrderTaskBean> orderTaskList = allTaskList.stream().filter(task -> StringUtils.isEmpty(task.getRunId())).collect(Collectors.toList());

        // 2、装配run表数据
        EmcOrderTaskBean orderTask = orderTaskList.get(0);
        String userId = LocalContextHelper.getLoginUserId();
        String userName = LocalContextHelper.getLoginUserName();

        CoreUserBean user = LocalContextHelper.getLoginUser();
        //赋值所属单位和流程类型
        String parentOrgId = user.getExt$Item("parentorgid");
        String parentOrgName = user.getExt$Item("parentorgname");

        EmcRunBean run = new EmcRunBean();

        List<Long> testIdList = orderTaskList.stream().map(EmcOrderTaskBean::getTestId).distinct().collect(Collectors.toList());
        List<String> testNameList = orderTaskList.stream().map(EmcOrderTaskBean::getTestName).distinct().collect(Collectors.toList());

        String testId = StringUtils.join(testIdList.toArray(), ",");
        String testName = StringUtils.join(testNameList.toArray(), ",");
        String testMethodId = StringUtils.join(testMethodIdList.toArray(), ",");

        CoreOrgBean parentOrg = coreOrgService.selectById(parentOrgId);
        String orgShortName = parentOrg.getExt$Item("orgShortName");
        String runId = ApplicationContextHelper.getNextSequence(getDao().getTable(), orgShortName);
        Long reportConfigLineId = methodService.selectReportConfigLineIdById(orderTask.getMethodId());

        // 查询项目编号
        List<Long> projectIdList = orderTaskList.stream().map(EmcOrderTaskBean::getProjId).distinct().sorted(Comparator.comparing(Function.identity())).collect(Collectors.toList());
        List<EmcProjectBean> projectList = projectService.getDao().selectListByOneColumnValues(projectIdList, "id", Arrays.asList("id", "projNo", "sample"));

        // 查询测试方法中的前处理
        EmcTestMethodBean testMethod = testMethodService.getDao().selectById(testMethodIdList.get(0));

        // 允许多个样品类型的同一个方法添加到一个批
        String orderCategories = orderTaskList.stream().map(EmcOrderTaskBean::getOrderCategory).distinct().collect(Collectors.joining(","));

        EmcTestCapacityBean filterCapacity = new EmcTestCapacityBean();
        filterCapacity.setTestMethodId(testMethodIdList.get(0));
        filterCapacity.setOrgId(parentOrgId);
        EmcTestCapacityBean testCapacity = testCapacityService.getDao().selectOne(filterCapacity, "id", "inputuserid", "inputusername", "pretreatUserId", "pretreatUserName", "weighingUserId", "weighingUserName");
        String inputUserId = testCapacity.getExt$Item("inputuserid");
        String inputUserName = testCapacity.getExt$Item("inputusername");
        String pretreatUserId = testCapacity.getExt$Item("pretreatUserId");
        String pretreatUserName = testCapacity.getExt$Item("pretreatUserName");
        String weighingUserId = testCapacity.getExt$Item("weighingUserId");
        String weighingUserName = testCapacity.getExt$Item("weighingUserName");

        //取维护好的值分析人、称重人、前处理人
        run.setId(runId);
        run.setProjId(orderTask.getProjId());
        run.setOrderCategory(orderCategories);
        run.setTestMethodId(testMethodId);
        run.setTestId(testId);
        run.setTestName(testName);
        run.setAnalysisUserId(Optional.ofNullable(inputUserId).orElse(LocalContextHelper.getLoginUserId()));
        run.setAnalysisUserName(Optional.ofNullable(inputUserName).orElse(LocalContextHelper.getLoginUserName()));
        run.setExt$Item("PRETREATUSERID", pretreatUserId);
        run.setExt$Item("PRETREATUSERNAME", pretreatUserName);
        run.setExt$Item("WEIGHINGUSERID", weighingUserId);
        run.setExt$Item("WEIGHINGUSERNAME", weighingUserName);
        run.setEquiptId(equipt.getId());
        run.setMethodId(orderTask.getMethodId());
        run.setReportConfigLineId(reportConfigLineId);
        run.setBeforeHandle(testMethod.getBeforeHandle());
        run.setBeforeHandleId(testMethod.getBeforeHandleId());
        run.setOrgId(parentOrgId);
        run.setOrgName(parentOrgName);
        run.setExt$Item("adddate", DateTimeUtils.formatLocalDate(LocalDate.now()));

        // 3、区分正常样品和qc样，赋值和设置杯号

        // 按照前端筛选项目的顺序对添加批的样品测试进行排序
        String projectIds = wrapper.getParamValue("projectId");
        if (!StringUtils.isEmpty(projectIds)) {
            List<Long> sortedProjectIdList = Arrays.stream(StringUtils.split(projectIds, ",")).filter(NumberUtils::isNumber).mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());
            IntStream.range(0, sortedProjectIdList.size()).forEach(index -> {
                projectList.stream().filter(project -> ObjectUtils.equals(project.getId(), sortedProjectIdList.get(index))).forEach(project -> project.setExt$Item("sortNum", index + ""));
                orderTaskList.stream().filter(task -> ObjectUtils.equals(task.getProjId(), sortedProjectIdList.get(index))).forEach(task -> task.setExt$Item("sortNum", index + ""));
            });
            orderTaskList.sort(Comparator.comparing(task -> NumberUtils.parseInt(task.getExt$Item("sortNum"))));
            projectList.sort(Comparator.comparing(task -> NumberUtils.parseInt(task.getExt$Item("sortNum"))));
        }
        String projectNos = projectList.stream().map(EmcProjectBean::getProjNo).collect(Collectors.joining(","));
        run.setProjNo(projectNos);

        // 根据样品编号排序分组
        Map<String, List<EmcOrderTaskBean>> orderTaskMap = orderTaskList.stream().collect(Collectors.groupingBy(a -> a.getExt$Item("orderno"), LinkedHashMap::new, Collectors.toList()));
        // 对正常样品测试赋值和设置杯号
        Long cupNoIndex = 0L;
        for (Map.Entry<String, List<EmcOrderTaskBean>> entry : orderTaskMap.entrySet()) {
            Long cupNo = cupNoIndex + 1L;
            List<EmcOrderTaskBean> tempList = entry.getValue();
            tempList.forEach(task -> {
                task.setRunId(runId);
                task.setAnalysisUserId(userId);
                task.setAnalysisUserName(userName);
                task.setEquiptId(equipt.getId());
                task.setCupNo(cupNo);
            });
            cupNoIndex = cupNo;
        }

        // 4、结果表装配数据

        // 查询出来ordertask下的result
        List<Long> orderTaskIdList = orderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID");

        List<Long> itemIdList = orderTaskResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
        List<EmcItemBean> itemList = itemService.getDao().selectListByIds(itemIdList);
        Map<Long, String> itemMap = new HashMap<>();

        for (EmcItemBean e : itemList) {
            itemMap.put(e.getId(), e.getDefaultResult());
        }

        // 对于有默认值的样品测试结果，需要自动触发一次计算公式
        List<Long> defaultValueResultIdList = new ArrayList<>();
        for (EmcOrderTaskResultBean a : orderTaskResultList) {
            a.setRunId(runId);
            // 当测试管理中分析项默认值不会空而且当前结果值为空的情况下才进行默认结果
            if (!StringUtils.isEmpty(itemMap.get(a.getItemId())) && StringUtils.isEmpty(a.getReportResult())) {
                a.setOriginalResult(itemMap.get(a.getItemId()));
                defaultValueResultIdList.add(a.getId());
            }
        }

        // 6、插入数据库
        orderTaskService.getDao().update(orderTaskList, "RUNID", "ANALYSISUSERID", "ANALYSISUSERNAME", "EQUIPTID", "CUPNO");
        getDao().insert(run);
        orderTaskResultService.getDao().update(orderTaskResultList, "RUNID", "ORIGINALRESULT");

        // 添加默认QC
        //查询所有监测能力库
        List<EmcTestCapacityBean> testCapacityList = testCapacityService.selectListByFilter(SearchFilter.instance().match("testMethodId", testMethodIdList).filter(MatchPattern.OR).match("orgId", parentOrgId).filter(MatchPattern.SEQ));
        List<Long> testCapacityIdList = testCapacityList.stream().map(EmcTestCapacityBean::getId).distinct().collect(Collectors.toList());
        List<EmcTestMethodDefaultQcBean> defaultQcList = defaultQcService.selectListByFilter(SearchFilter.instance().match("testCapacityId", testCapacityIdList).filter(MatchPattern.OR));
        if (!defaultQcList.isEmpty()) {
            Map<Long, List<EmcOrderTaskBean>> orderMap = orderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getOrderId, LinkedHashMap::new, Collectors.toList()));
            List<Long> orderIdList = new ArrayList<>(orderMap.keySet());
            List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(orderIdList, "id", Arrays.asList("id", "orderNo", "projId"));
            if (!StringUtils.isEmpty(projectIds)) {
                List<Long> sortedProjectIdList = Arrays.stream(StringUtils.split(projectIds, ",")).filter(NumberUtils::isNumber).mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());
                IntStream.range(0, sortedProjectIdList.size()).forEach(index -> {
                    // 使用 orderNum 字段暂存排序值
                    orderList.stream().filter(order -> ObjectUtils.equals(order.getProjId(), sortedProjectIdList.get(index))).forEach(order -> order.setOrderNum((long) index));
                });

                orderList.sort(Comparator.comparing(EmcOrderBean::getOrderNum).thenComparing(EmcOrderBean::getOrderNo));
            } else {
                orderList.sort(Comparator.comparing(EmcOrderBean::getOrderNo));
            }

            int orderSize = orderMap.size();
            List<Long> qcCategoryIdList = defaultQcList.stream().map(EmcTestMethodDefaultQcBean::getQcCategoryId).distinct().collect(Collectors.toList());
            List<EmcQcCategoryBean> qcCategoryList = qcCategoryService.getDao().selectListByIds(qcCategoryIdList);

            Map<Long, List<EmcTestMethodDefaultQcBean>> defaultQcMap = defaultQcList.stream().collect(Collectors.groupingBy(EmcTestMethodDefaultQcBean::getQcCategoryId));
            defaultQcMap.forEach((qcId, curQcList) -> curQcList.stream().filter(qc -> qc.getStartOrderIndex() != null && qc.getStartOrderIndex() <= orderSize).findFirst().ifPresent(defaultQc -> {
                int startOrderIndex = Optional.ofNullable(defaultQc.getStartOrderIndex()).orElse(1);
                int intervalNum = Optional.ofNullable(defaultQc.getIntervalNum()).orElse(0);
                int due = (intervalNum == 0) ? 1 : ((orderSize - startOrderIndex) / intervalNum + 1);
                due = (due == 0 ? 1 : due);

                List<EmcQcCategoryBean> currentQcList = qcCategoryList.stream().filter(qc -> ObjectUtils.equals(qc.getId(), defaultQc.getQcCategoryId())).collect(Collectors.toList());
                currentQcList.forEach(qc -> {
                    qc.setAddNum(Optional.ofNullable(defaultQc.getAddNum()).orElse(1).longValue());
                    Optional.ofNullable(defaultQc.getCheckPoint()).ifPresent(checkPoint -> qc.setExt$Item("checkPoint", checkPoint.toString()));
                });
                List<EmcOrderTaskBean> currentOrderTaskList = new ArrayList<>();
                for (int i = 0; i < due; i++) {
                    int startIndex = i * intervalNum + startOrderIndex - 1;
                    int endIndex = (intervalNum == 0) ? orderSize : (i + 1) * intervalNum + startOrderIndex - 1;
                    List<EmcOrderTaskBean> taskList = orderMap.get(orderIdList.get(startIndex));
                    if (!taskList.isEmpty()) {
                        Set<String> relatedOrderIdList = new LinkedHashSet<>();
                        Set<String> relatedOrderNoList = new LinkedHashSet<>();
                        IntStream.range(startIndex, Math.min(endIndex, orderSize)).forEach(index -> {
                            relatedOrderIdList.add(orderList.get(index).getId() + "");
                            relatedOrderNoList.add(orderList.get(index).getOrderNo());
                        });
                        EmcOrderTaskBean targetTask = taskList.get(0);
                        targetTask.setExt$Item("relatedOrderId", String.join(",", relatedOrderIdList));
                        targetTask.setExt$Item("relatedOrderNo", String.join(",", relatedOrderNoList));
                        currentOrderTaskList.add(targetTask);
                    }
                }

                if (!currentOrderTaskList.isEmpty()) {
                    // 创建批添加默认QC时，不单独进行公式计算，添加完后统一再进行计算
                    addQc(currentOrderTaskList, currentQcList, null, false);
                }
            }));

            // 更新空白加标的父样
            boolean containBlankSpkc = qcCategoryList.stream().anyMatch(qcCategory -> StringUtils.equals(qcCategory.getQcCategoryId(), PfConstant.QC_L_SPKCK));
            if (containBlankSpkc) {
                updateBlankSpkc(orderTaskList.get(0).getRunId());
            }

            // 如果有默认的实验室空白，并且间隔数不为空，则重新调整实验室空白的杯号
            List<EmcTestMethodDefaultQcBean> blankDefaultQcList = defaultQcList.stream().filter(qc -> PfConstant.QCCATEGORY_QCCATEGORYID_L_BLANK.equals(qc.getQcCategoryCode()) && !ObjectUtils.isEmpty(qc.getIntervalNum())).collect(Collectors.toList());
            if (!blankDefaultQcList.isEmpty()) {
                adjustRunCupNo(runId, defaultQcList);
            }
        }

        // 5、属性数据表装配数据
        // 添加批的时候在 property_data 表中插入方法参数数据(根据 testMethodId 查询 属性数据表)
        List<EmcPropertyDataBean> propertyDataList = propertyDataService.getDao().selectListByOneColumnValue(testCapacity.getId(), "bizId");
        if (!propertyDataList.isEmpty()) {
            EmcPropertyDataBean basicPropertyData = propertyDataList.get(0);
            EmcPropertyDataBean runPropertyData = new EmcPropertyDataBean();
            BeanUtils.copyProperties(basicPropertyData, runPropertyData, EmcConstant.CREATED_COLUMNS);
            runPropertyData.setId(ApplicationContextHelper.getNextIdentity());
            runPropertyData.setBizId2(run.getId());
            runPropertyData.setTableName(getDao().getTable());
            propertyDataService.getDao().insert(runPropertyData);
        }

        //更新批的领样时间
        this.updateRunReceiveDate(runId);

        // 最后触发公式，计算存在默认值的分析项结果
        if (!defaultValueResultIdList.isEmpty()) {
            List<EmcOrderTaskResultBean> resultList = orderTaskResultService.getDao().selectListByOneColumnValue(runId, "runId", Arrays.asList("id", "originalResult"));
            List<Long> computedResultIdList = resultList.stream().filter(r -> !StringUtils.isEmpty(r.getOriginalResult())).map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
            asyncFormulaComputingService.asyncComputing(runId, computedResultIdList, LocalContextHelper.getLoginUserId(), false);
        }

        return runId;
    }

    /**
     * 调整批下样品的杯号
     *
     * @param runId
     * @param defaultQcList
     */
    private void adjustRunCupNo(String runId, List<EmcTestMethodDefaultQcBean> defaultQcList) {
        EmcTestMethodDefaultQcBean blankDefaultQc = defaultQcList.stream().filter(defaultQc -> PfConstant.QCCATEGORY_QCCATEGORYID_L_BLANK.equals(defaultQc.getQcCategoryCode())).findFirst().orElseGet(EmcTestMethodDefaultQcBean::new);
        Integer addNum = blankDefaultQc.getAddNum();
        Integer intervalNum = blankDefaultQc.getIntervalNum();

        if (addNum == null || intervalNum == null) {
            return;
        }

        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValue(runId, "runId", Arrays.asList("id", "orderId", "cupNo", "qcCategoryCode"));
        List<EmcOrderTaskBean> blankTaskList = orderTaskList.stream().filter(task -> PfConstant.QCCATEGORY_QCCATEGORYID_L_BLANK.equals(task.getQcCategoryCode())).collect(Collectors.toList());
        if (blankTaskList.isEmpty()) {
            return;
        }

        blankTaskList.sort(Comparator.comparing(EmcOrderTaskBean::getOrderId));
        List<Long> blankOrderIdList = blankTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());

        List<EmcOrderTaskBean> otherTaskList = orderTaskList.stream().filter(task -> !PfConstant.QCCATEGORY_QCCATEGORYID_L_BLANK.equals(task.getQcCategoryCode())).sorted(Comparator.comparing(EmcOrderTaskBean::getCupNo)).collect(Collectors.toList());
        List<Long> otherOrderIdList = otherTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());

        List<String> defaultQcCodeList = defaultQcList.stream().map(EmcTestMethodDefaultQcBean::getQcCategoryCode).collect(Collectors.toList());
        List<EmcOrderTaskBean> naOrderTaskList = orderTaskList.stream().filter(task -> !defaultQcCodeList.contains(task.getQcCategoryCode())).collect(Collectors.toList());
        naOrderTaskList.sort(Comparator.comparing(EmcOrderTaskBean::getCupNo));
        List<Long> naOrderIdList = naOrderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());

        List<List<Long>> blankPartitionList = ListUtils.partition(blankOrderIdList, addNum);
        List<List<Long>> naPartitionList = ListUtils.partition(naOrderIdList, intervalNum);

        if (blankPartitionList.size() != naPartitionList.size()) {
            return;
        }

        IntStream.range(0, naPartitionList.size()).forEach(index -> {
            List<Long> curNaPartitionList = naPartitionList.get(index);
            List<Long> curBlankList = blankPartitionList.get(index);
            Long firstOrderId = curNaPartitionList.get(0);
            int orderIndex = otherOrderIdList.indexOf(firstOrderId);
            IntStream.range(0, curBlankList.size()).forEach(blankIndex -> otherOrderIdList.add(orderIndex + blankIndex, curBlankList.get(blankIndex)));
        });

        // 重置杯号
        orderTaskList.forEach(task -> task.setCupNo(otherOrderIdList.indexOf(task.getOrderId()) + 1L));
        orderTaskService.getDao().update(orderTaskList, "cupNo");
    }

    @Override
    @Transactional
    @ValidateDataWith(PfMethodEditValidator.class)
    public void methodEdit(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        EmcMonitorSchemeBean monitorScheme = wrapper.parseUnique(EmcMonitorSchemeBean.class);
        EmcOrderTaskBean orderTask = orderTaskList.get(0);
        // 批号
        String runId = orderTask.getRunId();
        // 方法ID
        Long methodId = NumberUtils.parseLong(monitorScheme.getExt$Item("methodId"));
        // 测试方法ID
        Long testMethodId = monitorScheme.getTestMethodId();
        // 测试方法依据
        String testMethodBasis = monitorScheme.getExt$Item("testMethodBasis");
        // 监测方案名称
        String methodName = monitorScheme.getExt$Item("methodName");
        // 方法版本ID
        Long methodVersionId = NumberUtils.parseLong(monitorScheme.getExt$Item("methodVersionId"));
        // 分析项组ID 2023年10月26日 功能调整：【优化】分析录入，修改方法，左边只展示样品，右边只展示方法，不再细化至分析项组
//        Long capacityItemGroupId = monitorScheme.getItemGroupId();
        // 样品ID
        List<Long> orderIdList = orderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());

        // 重新查询样品测试列表，对于气的测试，需要支持修改现场的
        List<Long> testMethodIdList = orderTaskList.stream().map(EmcOrderTaskBean::getTestMethodId).distinct().collect(Collectors.toList());
        orderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("orderId", orderIdList).filter(MatchPattern.OR).match("testMethodId", testMethodIdList).filter(MatchPattern.OR));

        // 样品分析项组
        List<EmcOrderItemGroupBean> filterOrderItemGroupList = new ArrayList<>(orderIdList.size());
        orderIdList.forEach(orderId -> {
            EmcOrderItemGroupBean filterOrderItemGroup = new EmcOrderItemGroupBean();
            filterOrderItemGroup.setTestId(orderTask.getTestId());
            filterOrderItemGroup.setMethodId(orderTask.getMethodId());
            filterOrderItemGroup.setTestMethodId(orderTask.getTestMethodId());
            filterOrderItemGroup.setOrderId(orderId);
            filterOrderItemGroupList.add(filterOrderItemGroup);
        });
        List<EmcOrderItemGroupBean> targetOrderItemGroupList = orderItemGroupService.getDao().selectList(filterOrderItemGroupList, Arrays.asList("testId", "methodId", "testMethodId", "orderId"), Arrays.asList("id", "itemGroup"));

        List<String> itemGroupList = targetOrderItemGroupList.stream().filter(e -> !ObjectUtils.isEmpty(e.getItemGroup()))
                .map(EmcOrderItemGroupBean::getItemGroup).distinct().collect(Collectors.toList());

        String orgId = monitorScheme.getExt$Item("orgId");

        List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.selectListByFilter(SearchFilter.instance()
                .match("testMethodId", testMethodId).filter(MatchPattern.SEQ)
                .match("orgId", orgId).filter(MatchPattern.SEQ)
                .match("itemGroup", itemGroupList).filter(MatchPattern.OR));

//        List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByOneColumnValues(capacityItemGroupId, "id", Arrays.asList("id", "itemGroupId"));
        List<Long> itemGroupIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());

        // 获取分析项ID
        List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID");
        List<Long> itemIdList = groupItemList.stream().map(EmcGroupItemBean::getItemId).distinct().collect(Collectors.toList());
        List<EmcItemBean> itemList = itemService.getDao().selectListByIds(itemIdList);
        List<String> itemAliasNameList = itemList.stream().map(EmcItemBean::getItemAliasName).distinct().collect(Collectors.toList());

        // 获得样品测试下的结果信息
        List<Long> orderTaskId = orderTaskList.stream().map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskId, "ORDERTASKID");

        // 各ordertask下的result数据
        Map<Long, List<EmcOrderTaskResultBean>> orderTaskResultMap = orderTaskResultList.stream().collect(Collectors.groupingBy(EmcOrderTaskResultBean::getOrderTaskId));

        // 需要添加的数据
        List<EmcOrderTaskResultBean> addList = new ArrayList<>();
        // 需要删除的数据
        List<EmcOrderTaskResultBean> removeList = new ArrayList<>();
        // 需要更新的数据
        List<EmcOrderTaskResultBean> updateList = new ArrayList<>();
        // 遍历样品测试修改数据
        orderTaskList.forEach(a -> {
            a.setMethodId(methodId);
            a.setTestMethodId(testMethodId);
            a.setTestMethodBasis(testMethodBasis);
            a.setMethodName(methodName);
            a.setMethodVersionId(methodVersionId);
        });
        // 遍历样品分析项组表修改数据
        targetOrderItemGroupList.forEach(a -> {
            a.setSchemeName(monitorScheme.getSchemeName());
            a.setMethodId(methodId);
            a.setTestMethodId(testMethodId);
            a.setTestMethodBasis(testMethodBasis);
            a.setMethodName(methodName);
            a.setMethodVersionId(methodVersionId);
//            a.setItemGroupId(monitorScheme.getItemGroupId());
//            a.setItemGroup(monitorScheme.getItemGroup());
            EmcCapacityItemGroupBean capacityItemGroupBean = capacityItemGroupList.stream().filter(e -> ObjectUtils.equals(e.getItemGroup(), a.getItemGroup()))
                    .findFirst().orElse(null);
            if (!ObjectUtils.isEmpty(capacityItemGroupBean)) {
                a.setItemGroupId(capacityItemGroupBean.getItemGroupId());
                a.setItemGroup(capacityItemGroupBean.getItemGroup());
            }
        });
        // 判断结果分析项是否相同用什么字段还要再确认
        // 遍历结果,如果新选方法不包含该分析项则删除
        orderTaskResultList.forEach(a -> {
            if (itemAliasNameList.contains(a.getItemAliasName())) {
                a.setMethodId(methodId);
                a.setMethodVersionId(methodVersionId);
                a.setTestMethodId(testMethodId);
                itemList.stream().filter(i -> StringUtils.equals(i.getItemAliasName(), a.getItemAliasName())).findFirst().ifPresent(i -> {
                    a.setItemId(i.getId());
                    a.setItemName(i.getItemName());
                    a.setHighLimit(a.getHighLimit());
                    a.setLowLimit(i.getLowLimit());
                    a.setUnitName(i.getUnitName());
                    a.setRevisionRule(i.getRevisionRule());
                });
                updateList.add(a);
            } else {
                removeList.add(a);
            }
        });
        // 遍历每个样品测试下的结果,缺少的分析项加上
        orderTaskResultMap.forEach((key, value) -> {
            EmcOrderTaskResultBean resultBean = value.get(0);
            // 筛选包含选中分析项的数据
            List<String> resultItemAliasNameList = value.stream().filter(a -> itemAliasNameList.contains(a.getItemAliasName())).map(EmcOrderTaskResultBean::getItemAliasName).collect(Collectors.toList());

            itemAliasNameList.stream().filter(e -> !resultItemAliasNameList.contains(e)).forEach(a -> {

                EmcItemBean itemBean = itemList.stream().filter(b -> ObjectUtils.equals(a, b.getItemAliasName())).findAny().orElse(null);
                // 筛选启用的分析项
                if (itemBean != null && Constant.ACTIVATED_STATUS_YES.equals(itemBean.getActivatedFlag())) {
                    Long repeatItem = itemBean.getRepeatItem();
                    // 根据重复数插入多条分析项
                    for (Long i = 1L; i <= repeatItem; i++) {
                        EmcOrderTaskResultBean orderTaskResult = new EmcOrderTaskResultBean();
                        BeanUtils.copyProperties(resultBean, orderTaskResult, "ORIGINALRESULT", "REPORTRESULT", "RESULTSTATUS", "QCCATEGORYID", "QCCATEGORYCODE", "QCNAME",
                                "CREATEDBYID", "CREATEDBYNAME", "CREATEDTIME", "CREATEDBYORGID", "CREATEDBYORGNAME");

                        orderTaskResult.setId(ApplicationContextHelper.getNextIdentity());
                        orderTaskResult.setMethodId(methodId);
                        orderTaskResult.setMethodVersionId(methodVersionId);
                        orderTaskResult.setTestMethodId(testMethodId);
                        orderTaskResult.setItemId(itemBean.getId());
                        orderTaskResult.setItemAliasName(itemBean.getItemAliasName());
                        orderTaskResult.setItemName(itemBean.getItemName());
                        orderTaskResult.setItemOrderNo(itemBean.getOrderNo());
                        orderTaskResult.setRepeatNo(i);
                        orderTaskResult.setReportDisplay(itemBean.getReportDisplay());
                        orderTaskResult.setUnitName(itemBean.getUnitName());
                        orderTaskResult.setLowLimit(itemBean.getLowLimit());
                        orderTaskResult.setHighLimit(itemBean.getHighLimit());
                        orderTaskResult.setRevisionRule(itemBean.getRevisionRule());
                        orderTaskResult.setScriptFlag(itemBean.getScriptFlag());
                        addList.add(orderTaskResult);
                    }
                }
            });

        });
        List<Long> delIdList = removeList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
        orderTaskService.getDao().update(orderTaskList, "METHODID", "TESTMETHODID", "TESTMETHODBASIS", "METHODNAME", "METHODVERSIONID");
        orderItemGroupService.getDao().update(targetOrderItemGroupList, "schemeName", "METHODID", "TESTMETHODID", "TESTMETHODBASIS", "METHODNAME", "METHODVERSIONID", "ITEMGROUPID", "ITEMGROUP");
        // 插入样品测试记录表
        orderTaskLogService.insert(orderTaskList, EmcBizConstant.ORDER_TASK_LOG_EDIT_METHOD, EmcBizConstant.ORDER_TASK_LOG_ANALYSE);

        orderTaskResultService.getDao().deleteByIdList(delIdList);
        orderTaskResultService.getDao().insert(addList);
        orderTaskResultService.getDao().update(updateList, "METHODID", "METHODVERSIONID", "TESTMETHODID", "ITEMID", "ITEMNAME", "HIGHLIMIT", "LOWLIMIT", "UNITNAME", "REVISIONRULE");
    }

    @Override
    @Transactional
    public void delete(List<String> runIdList) {
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(runIdList, "RUNID").stream()
                .filter(a -> EmcBizConstant.ORDERTASK_STATUS_LOGGED.equals(a.getStatus())).collect(Collectors.toList());
        // 获取结果
        List<Long> orderTaskIdList = orderTaskList.stream().map(a -> a.getId()).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID");

        orderTaskList.forEach(e -> {
            e.setRunId(null);
            e.setAnalysisUserId(null);
            e.setAnalysisUserName(null);
            e.setCupNo(null);
            e.setEquiptId(null);
        });
        orderTaskResultList.forEach(a -> {
            a.setRunId(null);
            a.setAvgResult(null);
        });
        orderTaskService.getDao().update(orderTaskList, "RUNID", "ANALYSISUSERID", "ANALYSISUSERNAME", "EQUIPTID", "CUPNO");
        orderTaskResultService.getDao().update(orderTaskResultList, "RUNID", "AVGRESULT");

        List<EmcOrderTaskBean> qcOrderTaskList = orderTaskList.stream().filter(a -> a.getQcCategoryId() != null).collect(Collectors.toList());
        if (!qcOrderTaskList.isEmpty()) {
            // 获取实验室质控类型
            List<Long> qcIdList = qcOrderTaskList.stream().map(EmcOrderTaskBean::getQcCategoryId).distinct().collect(Collectors.toList());
            if (!qcIdList.isEmpty()) {
                List<EmcQcCategoryBean> qcCategoryList = qcCategoryService.getDao().selectListByIds(qcIdList).stream().filter(a -> EmcConstant.QCCATEGORY_USECATEGORY_LAB.equals(a.getUseCategory()))
                        .collect(Collectors.toList());
                if (!qcCategoryList.isEmpty()) {
                    List<Long> labQcIdList = qcCategoryList.stream().map(a -> a.getId()).collect(Collectors.toList());
                    // 要删除的数据
                    List<EmcOrderTaskBean> delOrderTaskList = qcOrderTaskList.stream().filter(a -> labQcIdList.contains(a.getQcCategoryId())).collect(Collectors.toList());
                    List<Long> delOrderTaskIdList = delOrderTaskList.stream().map(a -> a.getId()).collect(Collectors.toList());
                    List<EmcOrderTaskResultBean> delOrderTaskResultList = orderTaskResultList.stream().filter(a -> delOrderTaskIdList.contains(a.getOrderTaskId())).collect(Collectors.toList());
                    List<Long> delOrderIdList = delOrderTaskList.stream().map(a -> a.getOrderId()).distinct().collect(Collectors.toList());
                    List<Long> delFolderIdList = delOrderTaskList.stream().map(a -> a.getFolderId()).distinct().collect(Collectors.toList());
                    folderService.getDao().deleteByIdList(delFolderIdList);
                    orderService.getDao().deleteByIdList(delOrderIdList);
                    orderTaskService.getDao().deleteByIdList(delOrderTaskIdList);
                    orderTaskResultService.getDao().deleteBy(delOrderTaskResultList);
                }
            }
        }
        getDao().deleteByIdList(runIdList);

        // 删除批下测试关联的方法参数数据
        List<EmcPropertyDataBean> filterDataList = new ArrayList<>(orderTaskIdList.size());
        orderTaskIdList.forEach(taskId -> {
            EmcPropertyDataBean filterData = new EmcPropertyDataBean();
            filterData.setBizId(taskId);
            filterDataList.add(filterData);
        });
        propertyDataService.getDao().deleteBy(filterDataList, "bizId");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public Long addOrderTask(String id, RestJsonWrapperBean wrapper) {
        // 获取批下所有ordertask记录
        EmcOrderTaskBean filter = new EmcOrderTaskBean();
        filter.setRunId(id);
        filter.setStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED);
        List<EmcOrderTaskBean> selectOrderTaskList = orderTaskService.getDao().selectList(filter);

        // 拿出来最大杯号
        long maxCupNo = selectOrderTaskList.stream().mapToLong(EmcOrderTaskBean::getCupNo).max().orElse(0L);

        // 拿出来传回来的数据
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);

        // 根据ordertaskid拿出来ordertaskresult
        List<Long> orderTaskIdList = orderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID");

        // 根据orderid分组来设置杯号
        orderTaskList.sort(Comparator.comparing(EmcOrderTaskBean::getOrderId));
        Map<Long, List<EmcOrderTaskBean>> orderTaskGroupByMap = orderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getOrderId, LinkedHashMap::new, Collectors.toList()));

        // 空白list用来插入更新的数据
        List<EmcOrderTaskBean> updateList = new ArrayList<>();
        AtomicLong startCupNoIndex = new AtomicLong(maxCupNo);
        orderTaskGroupByMap.forEach((orderId, curTaskList) -> {
            long newCupNo = startCupNoIndex.incrementAndGet();
            curTaskList.forEach(task -> {
                task.setRunId(id);
                task.setCupNo(newCupNo);
                task.setStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED);
                updateList.add(task);
            });
        });

        // 结果设置默认值和批号
        List<Long> itemIdList = orderTaskResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
        List<EmcItemBean> itemList = itemService.getDao().selectListByIds(itemIdList);
        Map<Long, String> itemMap = new HashMap<>();
        for (EmcItemBean e : itemList) {
            itemMap.put(e.getId(), e.getDefaultResult());
        }

        // 针对有默认值的样品测试结果，需要自动触发一次计算公式
        List<Long> defaultValueResultIdList = new ArrayList<>();
        for (EmcOrderTaskResultBean a : orderTaskResultList) {
            a.setRunId(id);
            if (!StringUtils.isEmpty(itemMap.get(a.getItemId())) && StringUtils.isEmpty(a.getReportResult())) {
                a.setOriginalResult(itemMap.get(a.getItemId()));
                defaultValueResultIdList.add(a.getId());
            }
        }

        orderTaskService.getDao().update(updateList, "RUNID", "CUPNO", "STATUS");
        orderTaskResultService.getDao().update(orderTaskResultList, "RUNID", "ORIGINALRESULT");

        //更新批的领样时间
        this.updateRunReceiveDate(id);

        // 更新项目编号
        List<Long> originalProjectIdList = selectOrderTaskList.stream().filter(task -> !ObjectUtils.isEmpty(task.getProjId())).map(EmcOrderTaskBean::getProjId).distinct().collect(Collectors.toList());
        List<Long> newProjectIdList = updateList.stream().map(EmcOrderTaskBean::getProjId).distinct().collect(Collectors.toList());
        originalProjectIdList.addAll(newProjectIdList);
        List<Long> projectIdList = originalProjectIdList.stream().distinct().sorted(Comparator.comparing(Function.identity())).collect(Collectors.toList());
        List<EmcProjectBean> projectList = projectService.getDao().selectListByOneColumnValues(projectIdList, "id", Arrays.asList("id", "projNo"));
        String projectNos = projectList.stream().map(EmcProjectBean::getProjNo).collect(Collectors.joining(","));
        EmcRunBean run = new EmcRunBean();
        run.setId(id);
        run.setProjNo(projectNos);
        getDao().update(run, "projNo");

        // 触发一次计算公式
        if (!defaultValueResultIdList.isEmpty()) {
            EmcOrderTaskResultComputingEngine.getEngine(defaultValueResultIdList).compute();
        }
        return orderTaskList.get(0).getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public Long deleteOrderTask(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskTempList = wrapper.parse(EmcOrderTaskBean.class);
        List<Long> orderTaskIdList = orderTaskTempList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByIds(orderTaskIdList);

        // 拿出来该样品下的样品测试
        List<Long> orderIdList = orderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        String runId = orderTaskList.get(0).getRunId();

        EmcOrderTaskBean filter = new EmcOrderTaskBean();
        filter.setRunId(runId);
        filter.setStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED);

        List<EmcOrderTaskBean> orderTaskAllList = orderTaskService.selectList(filter, Order.asc("CUPNO"));
        List<EmcOrderTaskBean> selectOrderTaskList = orderTaskAllList.stream().filter(a -> orderIdList.contains(a.getOrderId())).collect(Collectors.toList());
        List<Long> selectOrderTaskIdList = selectOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> selectOrderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(selectOrderTaskIdList, "ORDERTASKID");

        List<Long> cupNoList = new ArrayList<>();
        selectOrderTaskList.forEach(e -> {
            cupNoList.add(e.getCupNo());
            e.setRunId(null);
            e.setCupNo(null);
        });
        selectOrderTaskResultList.forEach(a -> a.setRunId(null));

        orderTaskService.getDao().update(selectOrderTaskList, "RUNID", "CUPNO");
        orderTaskResultService.getDao().update(selectOrderTaskResultList, "RUNID");

        Collections.sort(cupNoList);
        List<EmcOrderTaskBean> orderTaskUpdateList = orderTaskService.selectList(filter);

        orderTaskUpdateList.sort(Comparator.comparing(EmcOrderTaskBean::getCupNo).thenComparingLong(EmcOrderTaskBean::getOrderId));
        AtomicLong cupNoIndex = new AtomicLong();
        LinkedHashMap<String, List<EmcOrderTaskBean>> cupNoTaskMap = orderTaskUpdateList.stream().collect(Collectors.groupingBy(task -> task.getCupNo() + "$" + task.getOrderId(), LinkedHashMap::new, Collectors.toList()));
        cupNoTaskMap.forEach((originalCupNo, taskList) -> {
            long newCupNo = cupNoIndex.incrementAndGet();
            taskList.forEach(task -> task.setCupNo(newCupNo));
        });

        //更新批的领样时间
        this.updateRunReceiveDate(runId);

        // 更新项目编号
        List<EmcOrderTaskBean> runOrderTaskList = orderTaskAllList.stream().filter(a -> !orderIdList.contains(a.getOrderId())).collect(Collectors.toList());
        List<Long> projectIdList = runOrderTaskList.stream().filter(task -> !ObjectUtils.isEmpty(task.getProjId())).map(EmcOrderTaskBean::getProjId).distinct().sorted(Comparator.comparing(Function.identity())).collect(Collectors.toList());
        List<EmcProjectBean> projectList = projectService.getDao().selectListByOneColumnValues(projectIdList, "id", Arrays.asList("id", "projNo"));
        String projectNos = projectList.stream().map(EmcProjectBean::getProjNo).collect(Collectors.joining(","));
        EmcRunBean run = new EmcRunBean();
        run.setId(runId);
        run.setProjNo(projectNos);
        getDao().update(run, "projNo");
        if (orderTaskUpdateList.isEmpty()) {
            return 0L;
        }
        orderTaskService.getDao().update(orderTaskUpdateList, "CUPNO");
        return orderTaskList.get(0).getId();
    }

    /*通过批号更新批的领样时间
    规则：1.如果同一个批，所有样品领样日期都一样，批上的时间就显示范围。2.不同日期，只展示日期，时间不展示*/
    private void updateRunReceiveDate(String runId) {
        if (!ObjectUtils.isEmpty(runId)) {
            List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValue(runId, "runId",
                    Arrays.asList("id", "runId", "sampleReceiverDate", "sampleReceiverTime"));
            if (!CollectionUtils.isEmpty(orderTaskList)) {
                orderTaskList = orderTaskList.stream().filter(e -> !ObjectUtils.isEmpty(e.getSampleReceiverDate())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(orderTaskList)) {
                    DateTimeFormatter dateFormatters = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    DateTimeFormatter timeFormatters = DateTimeFormatter.ofPattern("HH:mm");
                    Optional<EmcOrderTaskBean> maxReceiverDateOptional = orderTaskList.stream().max(Comparator.comparing(EmcOrderTaskBean::getSampleReceiverDate));
                    Optional<EmcOrderTaskBean> minReceiverDateOptional = orderTaskList.stream().min(Comparator.comparing(EmcOrderTaskBean::getSampleReceiverDate));

                    LocalDateTime maxReceiverDate = null;
                    LocalDateTime minReceiverDate = null;

                    if (maxReceiverDateOptional.isPresent()) {
                        maxReceiverDate = maxReceiverDateOptional.get().getSampleReceiverDate();
                    }
                    if (minReceiverDateOptional.isPresent()) {
                        minReceiverDate = minReceiverDateOptional.get().getSampleReceiverDate();
                    }

                    LocalTime maxReceiverTime = null;
                    LocalTime minReceiverTime = null;
                    Optional<EmcOrderTaskBean> maxReceiverTimeOptional = orderTaskList.stream().filter(e -> !ObjectUtils.isEmpty(e.getSampleReceiverTime()))
                            .max(Comparator.comparing(e -> DateTimeUtils.parseLocalTime(e.getSampleReceiverTime())));
                    Optional<EmcOrderTaskBean> minReceiverTimeOptional = orderTaskList.stream().filter(e -> !ObjectUtils.isEmpty(e.getSampleReceiverTime()))
                            .min(Comparator.comparing(e -> DateTimeUtils.parseLocalTime(e.getSampleReceiverTime())));
                    if (maxReceiverTimeOptional.isPresent()) {
                        maxReceiverTime = DateTimeUtils.parseLocalTime(maxReceiverTimeOptional.get().getSampleReceiverTime());
                    }
                    if (minReceiverTimeOptional.isPresent()) {
                        minReceiverTime = DateTimeUtils.parseLocalTime(minReceiverTimeOptional.get().getSampleReceiverTime());
                    }

                    String receiveDate = "";
                    String receiveTime = "";
                    if (!ObjectUtils.isEmpty(maxReceiverDate) && !ObjectUtils.isEmpty(minReceiverDate)) {
                        if (maxReceiverDate.equals(minReceiverDate)) {
                            receiveDate = maxReceiverDate.format(dateFormatters);
                            if (!ObjectUtils.isEmpty(maxReceiverTime) && !ObjectUtils.isEmpty(minReceiverTime)) {
                                if (maxReceiverTime.equals(minReceiverTime)) {
                                    receiveTime = maxReceiverTime.format(timeFormatters);
                                } else {
                                    receiveTime = minReceiverTime.format(timeFormatters) + "~" + maxReceiverTime.format(timeFormatters);
                                }
                            }
                        } else {
                            receiveDate = minReceiverDate.format(dateFormatters) + "~" + maxReceiverDate.format(dateFormatters);
                            receiveTime = null;
                        }
                    }

                    EmcRunBean run = new EmcRunBean();
                    run.setId(runId);
                    run.setReceiveDate(receiveDate);
                    run.setReceiveTime(receiveTime);
                    getDao().update(run, "receiveDate", "receiveTime");
                }
            }
        }
    }


    /**
     * 添加qc功能，主要步骤如下 1、取RestJsonWrapperBean数据。 2、准备数据,准备后面要用到的数据。 3、添加folder表数据。
     * 4、添加order、ordertask、ordertaskresult数据 5、插入数据库6、重新按照qc类型名称排列
     */
    @Override
    @Transactional
    @ValidateDataWith(EmcAddQcValidator.class)
    public Long addQc(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<EmcQcCategoryBean> qcCategoryList = wrapper.parse(EmcQcCategoryBean.class);
        String stockBatchNo = wrapper.getFilterValue("stockBatchNo");

        addQc(orderTaskList, qcCategoryList, stockBatchNo, true);

        boolean containBlankSpkc = qcCategoryList.stream().anyMatch(qcCategory -> StringUtils.equals(qcCategory.getQcCategoryId(), PfConstant.QC_L_SPKCK));
        if (containBlankSpkc) {
            updateBlankSpkc(orderTaskList.get(0).getRunId());
        }

        return 0L;
    }

    /**
     * 更新空白加标的父样为第一个实验室空白
     *
     * @param runId
     */
    private void updateBlankSpkc(String runId) {
        String blankOrderNo = String.join("-", runId, PfConstant.QC_LBLANK_ORDER_NO_PREFIX);
        List<EmcOrderBean> blankOrderList = orderService.getDao().selectListByOneColumnValue(blankOrderNo, "orderNo", Arrays.asList("id", "orderNo"));
        if (blankOrderList.isEmpty()) {
            return;
        }

        Long blankOrderId = blankOrderList.get(0).getId();

        List<EmcOrderTaskBean> filterOrderTaskList = new ArrayList<>(2);
        Arrays.asList(PfConstant.QCCATEGORY_QCCATEGORYID_L_BLANK, PfConstant.QC_L_SPKCK).forEach(qcCode -> {
            EmcOrderTaskBean filterTask = new EmcOrderTaskBean();
            filterTask.setQcCategoryCode(qcCode);
            filterTask.setRunId(runId);
            filterOrderTaskList.add(filterTask);
        });

        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectList(filterOrderTaskList, Arrays.asList("runId", "qcCategoryCode"), Collections.emptyList());
        List<EmcOrderTaskBean> blankTaskList = orderTaskList.stream().filter(task -> ObjectUtils.equals(task.getOrderId(), blankOrderId)).collect(Collectors.toList());

        List<EmcOrderTaskBean> blankSpkcTaskList = orderTaskList.stream().filter(task -> StringUtils.equals(task.getQcCategoryCode(), PfConstant.QC_L_SPKCK)).collect(Collectors.toList());
        List<Long> blankSpkcOrderIdList = blankSpkcTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());

        List<Long> blankSpkcTaskIdList = blankSpkcTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> blankSpkcTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(blankSpkcTaskIdList, "orderTaskId", Arrays.asList("id", "orderId", "orderNo", "orderTaskId", "itemId"));

        blankSpkcTaskList.forEach(blankSpkcTask -> blankTaskList.stream().filter(task -> ObjectUtils.equals(blankSpkcTask.getTestMethodId(), task.getTestMethodId())).findFirst().ifPresent(task -> blankSpkcTask.setParentId(task.getId())));
        blankSpkcTaskResultList.forEach(result -> result.setParentOrderNo(blankOrderNo));

        orderTaskService.getDao().update(blankSpkcTaskList, "parentId");
        orderTaskResultService.getDao().update(blankSpkcTaskResultList, "parentOrderNo");

        List<EmcOrderBean> updateBlankSpkcOrderList = new ArrayList<>(blankSpkcOrderIdList.size());
        blankSpkcOrderIdList.forEach(orderId -> {
            EmcOrderBean updateOrder = new EmcOrderBean();
            updateOrder.setId(orderId);
            updateOrder.setParentOrderNo(blankOrderNo);
            updateBlankSpkcOrderList.add(updateOrder);
        });
        orderService.getDao().update(updateBlankSpkcOrderList, "parentOrderNo");
    }

    @Override
    @Transactional
    @ValidateDataWith(EmcDelQcValidator.class)
    public Long delQc(RestJsonWrapperBean wrapper) {
        // 1、根据传过来的id获得完整的ordertaskList数据

        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<Long> orderTaskIdList = orderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> selectedOrderTaskList = orderTaskService.getDao().selectListByIds(orderTaskIdList);

        // 2、准备数据

        EmcOrderTaskBean firstOrderTask = selectedOrderTaskList.get(0);
        String runId = firstOrderTask.getRunId();
        String status = firstOrderTask.getStatus();
        // 获得批下所有的orderTask，按照杯号升序排列
        EmcOrderTaskBean otFilter = new EmcOrderTaskBean();
        otFilter.setRunId(runId);
        otFilter.setStatus(status);
        List<EmcOrderTaskBean> orderTaskAllList = orderTaskService.getDao().selectList(otFilter, Order.asc("CUPNO"));

        // 3、获取要删除的order、orderTask、orderTaskResult并删除

        // 获得要删除的orderTask数据
        List<Long> orderIdList = selectedOrderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskDelList = orderTaskAllList.stream().filter(b -> orderIdList.contains(b.getOrderId())).collect(Collectors.toList());
        // 获得要删除的orderTaskResult数据
        List<Long> orderTaskDelIdList = orderTaskDelList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskDelIdList, "ORDERTASKID");
        // 获得删除的杯号并进行排序
        List<Long> orderTaskDelCupNo = orderTaskDelList.stream().map(EmcOrderTaskBean::getCupNo).distinct().collect(Collectors.toList());
        Collections.sort(orderTaskDelCupNo);

        // 获得删除的OrderItemGroup
        List<EmcOrderItemGroupBean> orderItemGroupList = new ArrayList<>();
        orderIdList.forEach(t -> {
            EmcOrderItemGroupBean orderItemGroup = new EmcOrderItemGroupBean();
            orderItemGroup.setOrderId(t);
            orderItemGroupList.add(orderItemGroup);
        });

        // 对于多个批共用的质控，不能直接把质控样删除
        List<EmcOrderTaskBean> allOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "orderId", Arrays.asList("id", "runId", "orderId"));
        List<Long> commonOrderIdList = allOrderTaskList.stream().filter(task -> !StringUtils.equals(task.getRunId(), runId)).map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<Long> canDeleteOrderIdList = orderIdList.stream().filter(id -> !commonOrderIdList.contains(id)).collect(Collectors.toList());

        if (!canDeleteOrderIdList.isEmpty()) {
            orderService.getDao().deleteByIdList(canDeleteOrderIdList);
        }

        orderTaskService.getDao().deleteBy(orderTaskDelList);
        orderTaskResultService.getDao().deleteBy(orderTaskResultList);
        orderItemGroupService.getDao().deleteBy(orderItemGroupList, "ORDERID");

        // 4、判断是否可以删除folder表数据

        EmcOrderTaskBean filter = new EmcOrderTaskBean();
        filter.setRunId(runId);
        filter.setStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED);
        List<EmcOrderTaskBean> orderTaskTempList = orderTaskService.getDao().selectList(filter);
        // 判断批下是否还有分析录入添加的非质控样
        boolean delFlag = orderTaskTempList == null;
        if (orderTaskTempList != null) {
            List<Long> qcIdList = orderTaskTempList.stream().filter(a -> a.getQcCategoryId() != null).map(e -> e.getQcCategoryId()).distinct().collect(Collectors.toList());
            List<EmcQcCategoryBean> qcCategoryList = qcCategoryService.selectListByIds(qcIdList);
            delFlag = qcCategoryList.stream().noneMatch(e -> EmcConstant.QCCATEGORY_USECATEGORY_LAB.equals(e.getUseCategory()));
        }
        if (delFlag) {
            EmcFolderBean folderFilter = new EmcFolderBean();
            folderFilter.setRunId(runId);
            folderService.getDao().deleteBy(folderFilter, "RUNID");
        }

        // 更新杯号
        List<EmcOrderTaskBean> otherOrderTaskList = orderTaskAllList.stream().filter(task -> !orderTaskDelIdList.contains(task.getId())).sorted(Comparator.comparing(EmcOrderTaskBean::getCupNo)).collect(Collectors.toList());
        Map<Long, List<EmcOrderTaskBean>> cupNoTaskMap = otherOrderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getCupNo, LinkedHashMap::new, Collectors.toList()));
        List<Long> cupNoList = new ArrayList<>(cupNoTaskMap.keySet());
        IntStream.range(0, cupNoList.size()).forEach(index -> cupNoTaskMap.get(cupNoList.get(index)).forEach(task -> task.setCupNo(index + 1L)));
        orderTaskService.getDao().update(otherOrderTaskList, "cupNo");

        // 当删除的是实验室平行样时，需要判断该父样还有没有自己的实验室平行样，如果有重新计算均值，如果没有则清空父样均值
        List<EmcOrderTaskBean> ldupOrderTaskList = selectedOrderTaskList.stream().filter(t -> StringUtils.equals(EmcConstant.QC_L_DUP, t.getQcCategoryCode())).collect(Collectors.toList());
        if (!ldupOrderTaskList.isEmpty()) {
            List<Long> ldupOrderTaskIdList = ldupOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
            List<Long> parentOrderTaskIdList = ldupOrderTaskList.stream().map(EmcOrderTaskBean::getParentId).distinct().collect(Collectors.toList());

            List<EmcOrderTaskBean> childOrderTaskBeanList = orderTaskService.getDao().selectListByOneColumnValues(parentOrderTaskIdList, "PARENTID", Arrays.asList("ID", "PARENTID", "QCCATEGORYCODE"));
            List<Long> filterParentIdList = childOrderTaskBeanList.stream().filter(t -> !ldupOrderTaskIdList.contains(t.getId()) && StringUtils.equals(EmcConstant.QC_L_DUP, t.getQcCategoryCode()))
                    .map(EmcOrderTaskBean::getParentId).collect(Collectors.toList());

            List<Long> calcParentIdList = parentOrderTaskIdList.stream().filter(i -> filterParentIdList.contains(i)).collect(Collectors.toList());
            List<Long> updateOrderTaskParentIdList = parentOrderTaskIdList.stream().filter(i -> !filterParentIdList.contains(i)).collect(Collectors.toList());

            if (!updateOrderTaskParentIdList.isEmpty()) {
                List<EmcOrderTaskResultBean> updateOrderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(updateOrderTaskParentIdList, "ORDERTASKID", Arrays.asList("ID"));
                updateOrderTaskResultList.forEach(r -> r.setAvgResult(null));
                orderTaskResultService.getDao().update(updateOrderTaskResultList, "AVGRESULT");
            }

            if (!calcParentIdList.isEmpty()) {
                List<EmcOrderTaskResultBean> resultList = orderTaskResultService.getDao().selectListByOneColumnValues(calcParentIdList, "ORDERTASKID", Arrays.asList("ID", "AVGRESULT"));
                List<Long> calcResultIdList = resultList.stream().filter(r -> !StringUtils.isEmpty(r.getAvgResult())).map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
                if (!calcResultIdList.isEmpty()) {
                    EmcOrderTaskResultComputingEngine.getEngine(calcResultIdList).compute();
                }
            }
        }

        // 删除QC样关联的样品数据
        List<EmcQcRelatedOrderBean> deleteQcRelatedOrderList = new ArrayList<>(orderIdList.size());
        if (!canDeleteOrderIdList.isEmpty()) {
            canDeleteOrderIdList.forEach(orderId -> {
                EmcQcRelatedOrderBean relatedOrder = new EmcQcRelatedOrderBean();
                relatedOrder.setQcOrderId(orderId);
                deleteQcRelatedOrderList.add(relatedOrder);
            });

            qcRelatedOrderService.getDao().deleteBy(deleteQcRelatedOrderList, "qcOrderId");
        } else {
            // 如果多个批次共用了同一质控
            List<EmcQcRelatedOrderBean> qcRelatedOrderList = qcRelatedOrderService.getDao().selectListByOneColumnValues(orderIdList, "qcOrderId", Arrays.asList("id", "qcOrderId", "relatedOrderId"));
            List<EmcOrderTaskBean> curRunTaskList = orderTaskService.getDao().selectListByOneColumnValue(runId, "runId", Arrays.asList("id", "orderId"));
            List<Long> curRunOrderIdList = curRunTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
            List<Long> deleteQcRelatedOrderIdList = qcRelatedOrderList.stream().filter(relatedOrder -> curRunOrderIdList.contains(relatedOrder.getRelatedOrderId())).map(EmcQcRelatedOrderBean::getId).collect(Collectors.toList());

            qcRelatedOrderService.getDao().deleteByIdList(deleteQcRelatedOrderIdList);
        }

        return 0L;
    }

    @Override
    public void triggerFormlua(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        if (orderTaskList.isEmpty()) {
            return;
        }

        List<EmcOrderTaskBean> relatedOrderTaskList = orderTaskService.getDao().selectList(orderTaskList, Arrays.asList("ORDERID", "RUNID"), Collections.singletonList("ID"));

        List<Long> orderTaskIdList = relatedOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> resultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID", Collections.singletonList("ID"));

        String runId = orderTaskList.get(0).getRunId();
        List<Long> resultIdList = resultList.stream().map(EmcOrderTaskResultBean::getId).distinct().collect(Collectors.toList());
        asyncFormulaComputingService.asyncComputing(runId, resultIdList, LocalContextHelper.getLoginUserId(), false);
    }

    @Override
    public void syncTmplInfo(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        if (orderTaskList.isEmpty()) {
            return;
        }

        String runId = orderTaskList.get(0).getRunId();
        List<EmcOrderTaskResultBean> selectOrderTaskResultList = wrapper.parse(EmcOrderTaskResultBean.class);
        List<Long> selectItemIdList = selectOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());

        if (orderTaskList.size() > 1) {
            List<Long> orderIdList = orderTaskList.stream().map(EmcOrderTaskBean::getOrderId).collect(Collectors.toList());
            List<EmcOrderTaskResultBean> resultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderIdList, "orderId", Arrays.asList("id", "itemId", "runId"));
            selectOrderTaskResultList = resultList.stream().filter(r -> StringUtils.equals(r.getRunId(), runId) && selectItemIdList.contains(r.getItemId())).collect(Collectors.toList());
            resultList.clear();
        }

        // 获取分析项
        List<EmcItemBean> itemList = itemService.getDao().selectListByOneColumnValues(selectItemIdList, "id");
        List<Long> itemMatchIdList = itemList.stream().map(EmcItemBean::getId).distinct().collect(Collectors.toList());
        //分析项对应的分段修约
        List<EmcItemRevisionBean> itemRevisionBeans = itemRevisionService.getDao().selectListByOneColumnValues(itemMatchIdList, "itemId");
        Map<Long, EmcItemBean> itemMap = itemList.stream().collect(Collectors.toMap(EmcItemBean::getId, Function.identity()));

        List<EmcCapacityItemBean> capacityItemList = capacityItemService.selectListByFilter(SearchFilter.instance().match("itemId", itemMatchIdList).filter(MatchPattern.OR).match("orgId", LocalContextHelper.getLoginUser().getExt$Item("parentOrgId")).filter(MatchPattern.SEQ));

        // 从分析项同步数值
        selectOrderTaskResultList.forEach(result -> Optional.ofNullable(itemMap.get(result.getItemId())).ifPresent(item -> {
            result.setItemName(item.getItemName());
            result.setItemOrderNo(item.getOrderNo());
            result.setItemAliasName(item.getItemAliasName());
            result.setReportDisplay(item.getReportDisplay());
            String revisionRule = item.getRevisionRule();
            //如果结果表和分析项都没配修约
            if (StringUtils.isEmpty(revisionRule) && StringUtils.isEmpty(result.getRevisionRule())) {
                List<EmcItemRevisionBean> itemRevisionMatchBeans = itemRevisionBeans.stream().filter(ir -> ObjectUtils.equals(item.getId(), ir.getItemId())).collect(Collectors.toList());
                if (!itemRevisionMatchBeans.isEmpty()) {
                    String revisionRulePart = getReviseRule(result.getOriginalResult(), result, itemRevisionMatchBeans);
                    if (!StringUtils.isEmpty(revisionRulePart)) {
                        result.setRevisionRule(revisionRulePart);
                    }
                }
            } else {
                if (result.getQcCategoryCode() == null || result.getQcCategoryCode() == "") {
                    result.setRevisionRule(item.getRevisionRule());
                } else {
                    if (!result.getQcCategoryCode().equals("QC-STD")) {
                        if (result.getRevisionRule() == null || result.getRevisionRule() == "") {
                            result.setRevisionRule(item.getRevisionRule());
                        }
                    } else if (!result.getItemAliasName().equals("样品浓度")) {
                        if (result.getRevisionRule() == null || result.getRevisionRule() == "") {
                            result.setRevisionRule(item.getRevisionRule());
                        }
                    }
                }
            }

            result.setUnitName(item.getUnitName());
            result.setLowLimit(item.getLowLimit());
            result.setReportDisplay(item.getReportDisplay());
            result.setScriptFlag(item.getScriptFlag());
            result.setResultCategory(item.getResultCategory());
            // 同步允许偏差
            result.setRn13(item.getAllowableError());

            // 从监测能力库分析项同步
            capacityItemList.stream().filter(ci -> ObjectUtils.equals(item.getId(), ci.getItemId())).findFirst().ifPresent(ci -> {
                result.setLowLimit(ci.getLowLimit());
                result.setHighLimit(ci.getHighLimit());
                result.setOriginalResult(ci.getDefaultResult());
                result.setRevisionRule(ci.getRevisionRule());
            });
        }));

        orderTaskResultService.getDao().update(selectOrderTaskResultList, "ITEMID", "ITEMNAME", "ITEMORDERNO", "ITEMALIASNAME", "REVISIONRULE", "UNITNAME", "LOWLIMIT", "HIGHLIMIT", "REPORTDISPLAY", "SCRIPTFLAG", "RESULTCATEGORY", "RN13");
        List<Long> resultIdList = selectOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getId).distinct().collect(Collectors.toList());

        asyncFormulaComputingService.asyncComputing(runId, resultIdList, LocalContextHelper.getLoginUserId(), false);
        //同步标样试剂
        List<Long> orderOrgTaskIdList = selectOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getOrderTaskId).distinct().collect(Collectors.toList());
        List<Long> orderTaskIdList = orderOrgTaskIdList.stream().filter(item -> !ObjectUtils.isEmpty(item)).distinct().collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(orderTaskIdList)) {
            List<EmcOrderTaskBean> mtlOrderTaskList = orderTaskService.getDao().selectListByIds(orderTaskIdList);
            List<String> stockBatchNoList = orderTaskList.stream().map(task -> task.getStockBatchNo()).collect(Collectors.toList());
            List<EmcStockBean> stockList = stockService.getDao().selectListByOneColumnValues(stockBatchNoList, "batchNo", Arrays.asList("id", "batchNo", "mtlId"));
            if (stockList.isEmpty()) {
                return;
            }
            List<Long> stockIdList = stockList.stream().map(EmcStockBean::getId).distinct().collect(Collectors.toList());
            List<EmcStockConcentBean> stockConcentList = stockConcentService.getDao().selectListByOneColumnValues(stockIdList, "stockId");
            List<String> mtlIdList = stockList.stream().map(s -> s.getMtlId()).distinct().collect(Collectors.toList());
            List<EmcMtlMaintainBean> maintainList = mtlMaintainService.getDao().selectListByOneColumnValues(mtlIdList, "mtlId", Arrays.asList("id", "mtlId"));
            List<Long> maintainIdList = maintainList.stream().map(i -> i.getId()).distinct().collect(Collectors.toList());
            List<EmcMtlMaintainItemBean> maintainItemList = mtlMaintainItemService.getDao().selectListByOneColumnValues(maintainIdList, "mtlMaitainId");

            selectOrderTaskResultList.forEach(res -> {
                List<EmcOrderTaskBean> toOrderTaskList = mtlOrderTaskList.stream().filter(m -> ObjectUtils.equals(res.getOrderTaskId(), m.getId())).collect(Collectors.toList());
                String stockBatchNo = toOrderTaskList.get(0).getStockBatchNo();
                List<EmcStockBean> toStockList = stockList.stream().filter(s -> StringUtils.equals(stockBatchNo, s.getBatchNo())).collect(Collectors.toList());
                if (!toStockList.isEmpty()) {
                    List<Long> toStockIdList = toStockList.stream().map(EmcStockBean::getId).distinct().collect(Collectors.toList());
                    List<String> toMtlIdList = toStockList.stream().map(s -> s.getMtlId()).distinct().collect(Collectors.toList());
                    List<EmcStockConcentBean> toStockConcentList = stockConcentList.stream().filter(sc -> toStockIdList.contains(sc.getStockId())).collect(Collectors.toList());
                    List<EmcMtlMaintainBean> toMaintainList = maintainList.stream().filter(m -> toMtlIdList.contains(m.getMtlId())).collect(Collectors.toList());
                    List<Long> toMaintainIdList = toMaintainList.stream().map(i -> i.getId()).distinct().collect(Collectors.toList());
                    List<EmcMtlMaintainItemBean> toMaintainItemList = maintainItemList.stream().filter(mi -> toMaintainIdList.contains(mi.getMtlMaitainId())).collect(Collectors.toList());
                    List<EmcMtlMaintainItemBean> toBeforeMaintainItemSelectList = toMaintainItemList.stream().filter(mi -> mi.getTestMethodId().equals(res.getTestMethodId())).collect(Collectors.toList());
                    List<EmcMtlMaintainItemBean> updateMaintainItemSelectList = toBeforeMaintainItemSelectList.stream().filter(m -> ObjectUtils.equals(res.getItemId(), m.getItemId())).collect(Collectors.toList());
                    List<Long> itemIdList = toMaintainItemList.stream().map(m -> m.getItemId()).collect(Collectors.toList());
                    if (!updateMaintainItemSelectList.isEmpty() && itemIdList.contains(res.getItemId())) {

                        List<EmcStockConcentBean> stockConcentSelectList = toStockConcentList.stream().filter(sc -> sc.getMtlMaintainId().equals(updateMaintainItemSelectList.get(0).getMtlMaitainId())).collect(Collectors.toList());
                        List<EmcStockConcentBean> stockConcentMatchList = stockConcentSelectList.stream().filter(sc -> sc.getStockId().equals(toStockList.get(0).getId())).collect(Collectors.toList());
                        res.setRn16(stockConcentMatchList.get(0).getUncertainty());
                        res.setStandQty(stockConcentMatchList.get(0).getStdValue());
                        res.setIndexHighLimit(stockConcentMatchList.get(0).getHighLimit());
                        res.setIndexLowLimit(stockConcentMatchList.get(0).getLowLimit());
                    }
                }

            });
            orderTaskResultService.getDao().update(selectOrderTaskResultList, "rn16", "standQty", "indexLowLimit", "indexHighLimit");
        }
        selectOrderTaskResultList.clear();
    }

    @Override
    @Transactional
    public Long runInput(RestJsonWrapperBean wrapper) {
        // 模板源结果数据
        List<EmcOrderTaskResultBean> sourceResultList = wrapper.parse(EmcOrderTaskResultBean.class);
        List<EmcOrderTaskResultBean> validSourceResultList = sourceResultList.stream().filter(result -> !StringUtils.isEmpty(result.getOriginalResult())).collect(Collectors.toList());
        if (validSourceResultList.isEmpty()) {
            return 0L;
        }

        List<Long> sourceItemIdList = validSourceResultList.stream().map(EmcOrderTaskResultBean::getItemId).collect(Collectors.toList());
        String runId = validSourceResultList.get(0).getRunId();

        // 目标样品数据
        List<EmcOrderBean> targetOrderList = wrapper.parse(EmcOrderBean.class);
        List<Long> targetOrderIdList = targetOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());

        List<EmcOrderTaskBean> filterTaskList = new ArrayList<>(targetOrderIdList.size());
        targetOrderIdList.forEach(orderId -> {
            EmcOrderTaskBean filterTask = new EmcOrderTaskBean();
            filterTask.setRunId(runId);
            filterTask.setOrderId(orderId);
            filterTaskList.add(filterTask);
        });


        List<EmcOrderTaskBean> targetOrderTaskList = orderTaskService.getDao().selectList(filterTaskList, Arrays.asList("orderId", "runId"), Collections.singletonList("id"));
        List<Long> targetOrderTaskIdList = targetOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());

        List<EmcOrderTaskResultBean> targetResultList = orderTaskResultService.getDao().selectListByOneColumnValues(targetOrderTaskIdList, "orderTaskId", Arrays.asList("id", "itemId"));
        List<EmcOrderTaskResultBean> validTargetResultList = targetResultList.stream().filter(result -> sourceItemIdList.contains(result.getItemId())).collect(Collectors.toList());

        List<EmcOrderTaskResultBean> updateResultList = new ArrayList<>();
        validSourceResultList.forEach(sourceResult -> {
            List<EmcOrderTaskResultBean> matchedResultList = validTargetResultList.stream().filter(result -> ObjectUtils.equals(sourceResult.getItemId(), result.getItemId()) && ObjectUtils.equals(sourceResult.getRepeatNo(), result.getRepeatNo())).collect(Collectors.toList());
            matchedResultList.forEach(targetResult -> targetResult.setOriginalResult(sourceResult.getOriginalResult()));
            updateResultList.addAll(matchedResultList);
        });

        orderTaskResultService.getDao().update(updateResultList, "ORIGINALRESULT");

        // 触发公式计算
        List<Long> updateResultIdList = updateResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
        asyncFormulaComputingService.asyncComputing(runId, updateResultIdList, LocalContextHelper.getLoginUserId(), false);

        return sourceResultList.get(0).getId();
    }

    @Override
    public Page<EmcItemBean> selectEditItem(Long id, RestJsonWrapperBean wrapper) {
        EmcOrderTaskBean orderTask = orderTaskService.selectById(id);
        // 获取分析项组
        EmcOrderItemGroupBean filter = new EmcOrderItemGroupBean();
        filter.setSchemeName(orderTask.getSchemeName());
        filter.setTestId(orderTask.getTestId());
        filter.setTestMethodId(orderTask.getTestMethodId());
        filter.setMonitorTmplId(orderTask.getMonitorTmplId());
        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectList(filter);
        if (orderItemGroupList.isEmpty()) {
            return null;
        }
        List<Long> capacityItemGroupIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
        List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByIds(capacityItemGroupIdList, Arrays.asList("id", "itemGroupId"));
        List<Long> itemGroupIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
        if (itemGroupIdList.isEmpty()) {
            return null;
        }
        // 获取分析项
        List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID");
        List<Long> itemIdList = groupItemList.stream().map(EmcGroupItemBean::getItemId).distinct().collect(Collectors.toList());
        if (itemIdList.isEmpty()) {
            return null;
        }

        List<EmcItemBean> itemList = itemService.selectListByFilter(SearchFilter.instance().match("ID", itemIdList).filter(MatchPattern.OR));
        // 筛选类型是分析的,并且启用的分析项
        List<EmcItemBean> selectItemList = itemList.stream().filter(a -> "analysis".equals(a.getAnalysisCategory()) && Constant.ACTIVATED_STATUS_YES.equals(a.getActivatedFlag()))
                .collect(Collectors.toList());
        return new Pagination<>(selectItemList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_UNDO)
    public List<CoreBpmnInstanceStatusDTO<String>> undo(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<String>> instanceStatusList = super.unDoBasic(wrapper);
        List<String> idList = instanceStatusList.stream().map(CoreBpmnInstanceStatusDTO::getId).collect(Collectors.toList());
        List<EmcRunBean> runList = selectListByFilter(SearchFilter.instance().match("ID", idList).filter(MatchPattern.OR));

        List<EmcOrderTaskBean> filter = new ArrayList<>();
        idList.forEach(runId -> {
            EmcOrderTaskBean orderTask = new EmcOrderTaskBean();
            orderTask.setRunId(runId);
            filter.add(orderTask);
        });

        List<EmcOrderTaskBean> updateOrderTaskList = orderTaskService.getDao().selectList(filter, Collections.singletonList("RUNID"), Collections.emptyList());
        updateOrderTaskList.forEach(t -> t.setStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED));
        orderTaskService.getDao().update(updateOrderTaskList, "STATUS");

        // 插入样品测试操作记录表
        List<EmcOrderTaskBean> allOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(idList, "RUNID");
        orderTaskLogService.insert(allOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_LEADER_AUDIT_UNDO, EmcBizConstant.ORDER_TASK_LOG_ANALYSE_AUDIT);

        // 分析审核回退插入审核记录
        this.insertAuditRecordByRun(runList, "PROCESS_UNDO", wrapper.getParamValue("bpmn_comment"), null);

        runList.forEach(e -> {
            e.setCheckStatus(Constant.NO);
            e.setAuditedUserId(null);
            e.setAuditedUserName(null);
            e.setAuditedDate(null);
            e.setStatus(EmcBizConstant.RUN_STATUS_DRAFT);
            e.setUndoReason(null);
        });
        getDao().update(runList, "STATUS", "CHECKSTATUS", "AUDITEDUSERID", "AUDITEDUSERNAME", "AUDITEDDATE", "UNDOREASON");
        return instanceStatusList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    @ValidateDataWith(EmcRunAuditSubmitValidator.class)
    public void submitAnalysisProject(RestJsonWrapperBean wrapper) {
        List<EmcProjectBean> projectList = wrapper.parse(EmcProjectBean.class);
        List<Long> projectIdList = projectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());

        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(projectIdList, "projId", Arrays.asList("id", "projId", "qcCategoryId", "qcCategoryCode"));
        List<Long> qcProjectIdList = orderList.stream().filter(order -> StringUtils.equals(PfConstant.QC_Q_STD, order.getQcCategoryCode()) || StringUtils.equals(PfConstant.QC_Q_DUP, order.getQcCategoryCode())).map(EmcOrderBean::getProjId).distinct().collect(Collectors.toList());
        if (!qcProjectIdList.isEmpty()) {
            List<EmcOrderTaskBean> filterList = new ArrayList<>();
            projectList.stream().filter(p -> qcProjectIdList.contains(p.getId())).forEach(p -> {
                EmcOrderTaskBean filter = new EmcOrderTaskBean();
                filter.setProjId(p.getId());
                filter.setMonitorTmplId(p.getExt$Item("monitortmplid"));
                filter.setAnalysisType(EmcBizConstant.ANALYSISCATEGORY_ANALYSIS);
                filter.setStatus(EmcBizConstant.ORDERTASK_STATUS_AUDIT);
                filterList.add(filter);
            });
            List<EmcOrderTaskBean> orderTaskAnalysisList = orderTaskService.getDao().selectList(filterList, Arrays.asList("PROJID", "MONITORTMPLID", "ANALYSISTYPE", "STATUS"), Collections.emptyList());
            // 当前用户拥有权限的科室
            String userId = LocalContextHelper.getLoginUserId();
            CoreUserPermissionBean filter = new CoreUserPermissionBean();
            filter.setUserId(userId);
            filter.setPermissionTypeId("T_CORE_ORG");
            List<CoreUserPermissionBean> userPermissionList = userPermissionService.getDao().selectList(filter);
            List<String> orgIdList = userPermissionList.stream().map(CoreUserPermissionBean::getTargetId).collect(Collectors.toList());

            List<EmcOrderTaskBean> orderTaskList = orderTaskAnalysisList.stream().filter(a -> orgIdList.contains(a.getAnalysisOrgId())).collect(Collectors.toList());
            // 修改样品测试的状态置为qcAudit
            orderTaskList.forEach(task -> task.setStatus(PfBizConstant.ORDERTASK_STATUS_QC_AUDIT));
            orderTaskService.getDao().update(orderTaskList, "STATUS");
            // 插入样品测试操作记录表
            orderTaskLogService.insert(orderTaskList, EmcBizConstant.ORDER_TASK_LOG_LEADER_AUDIT, EmcBizConstant.ORDER_TASK_LOG_ANALYSE_AUDIT);
            // 插入样品测试关联批的审核人信息
            List<String> runIdList = orderTaskList.stream().filter(a -> !StringUtils.isEmpty(a.getRunId())).map(EmcOrderTaskBean::getRunId).collect(Collectors.toList());
            List<EmcRunBean> runList = getDao().selectListByIds(runIdList);
            List<EmcRunBean> updateRunList = new ArrayList<>();
            for (EmcRunBean run : runList) {
                EmcRunBean updateRun = new EmcRunBean();
                updateRun.setId(run.getId());
                updateRun.setAuditedUserId(LocalContextHelper.getLoginUserId());
                updateRun.setAuditedUserName(LocalContextHelper.getLoginUser().getUserName());
                if (run.getAuditedDate() != null) {
                    updateRun.setAuditedDate(run.getAuditedDate());
                } else {
                    updateRun.setAuditedDate(LocalDateTime.now());
                }
                updateRun.setStatus("qcAudit");
                updateRunList.add(updateRun);
            }

            getDao().update(updateRunList, "AUDITEDUSERID", "AUDITEDUSERNAME", "AUDITEDDATE", "STATUS");
            // 分析审核插入审核记录
            List<EmcAuditRecordBean> auditRecordList = new ArrayList<>();
            List<EmcRunBean> submitRunList = getDao().selectListByIds(runIdList);

            for (EmcRunBean submitRun : submitRunList) {
                EmcAuditRecordBean auditRecord = new EmcAuditRecordBean();
                auditRecord.setId(ApplicationContextHelper.getNextIdentity());
                auditRecord.setProjId(submitRun.getProjId());
                auditRecord.setRunId(submitRun.getId());
                auditRecord.setComments("通过");
                auditRecord.setLogDesc("批号:" + submitRun.getId());
                auditRecord.setBizCategory("run");
                auditRecord.setStepCode("5");
                auditRecord.setAuditCategory("结果录入");
                auditRecord.setOperateCategory("审核");
                auditRecord.setTaskName("分析审核");
                auditRecord.setNextTaskName("质控审核");

                auditRecordList.add(auditRecord);
            }
            auditRecordService.getDao().insert(auditRecordList);
        }

        // 对于不包含盲码标样、密码平行的项目，直接提交到报告编制
        List<EmcProjectBean> otherProjectList = projectList.stream().filter(p -> !qcProjectIdList.contains(p.getId())).collect(Collectors.toList());
        if (!otherProjectList.isEmpty()) {
            submitToReport(otherProjectList);
        }
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void qcAuditSubmit(RestJsonWrapperBean wrapper) {
        List<EmcProjectBean> projectList = wrapper.parse(EmcProjectBean.class);
        submitToReport(projectList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void qcAuditRunUndo(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<String>> instanceStatusList = unDoBasic(wrapper);
        List<String> idList = instanceStatusList.stream().map(CoreBpmnInstanceStatusDTO::getId).collect(Collectors.toList());
        List<EmcRunBean> runList = selectListByFilter(SearchFilter.instance().match("ID", idList).filter(MatchPattern.OR));

        List<EmcOrderTaskBean> filter = new ArrayList<>();
        idList.forEach(runId -> {
            EmcOrderTaskBean orderTask = new EmcOrderTaskBean();
            orderTask.setRunId(runId);
            orderTask.setStatus(PfBizConstant.ORDERTASK_STATUS_QC_AUDIT);
            filter.add(orderTask);
        });

        List<EmcOrderTaskBean> updateOrderTaskList = orderTaskService.getDao().selectList(filter, Arrays.asList("RUNID", "STATUS"), Collections.emptyList());
        updateOrderTaskList.forEach(t -> t.setStatus(EmcBizConstant.ORDERTASK_STATUS_AUDIT));
        orderTaskService.getDao().update(updateOrderTaskList, "STATUS");

        // 插入样品测试操作记录表
        List<EmcOrderTaskBean> allOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(idList, "RUNID");
        orderTaskLogService.insert(allOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_LEADER_AUDIT_UNDO, "质控审核");

        // 分析审核回退插入审核记录
        this.insertAuditRecordByRun(runList, "PROCESS_UNDO", wrapper.getParamValue("bpmn_comment"), null);

        runList.forEach(e -> {
            e.setCheckStatus(Constant.NO);
            e.setAuditedUserId(null);
            e.setAuditedUserName(null);
            e.setAuditedDate(null);
            e.setStatus(EmcBizConstant.RUN_STATUS_DRAFT);
        });
        getDao().update(runList, "STATUS", "CHECKSTATUS", "AUDITEDUSERID", "AUDITEDUSERNAME", "AUDITEDDATE");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void submitToReport(List<EmcProjectBean> projectList) {
        List<EmcOrderTaskBean> filterList = new ArrayList<>();
        projectList.forEach(e -> {
            EmcOrderTaskBean filter = new EmcOrderTaskBean();
            filter.setProjId(e.getId());
            filter.setMonitorTmplId(e.getExt$Item("monitortmplid"));
            filter.setAnalysisType(EmcBizConstant.ANALYSISCATEGORY_ANALYSIS);
            filterList.add(filter);
        });
        List<EmcOrderTaskBean> orderTaskAnalysisList = orderTaskService.getDao().selectList(filterList, Arrays.asList("PROJID", "MONITORTMPLID", "ANALYSISTYPE"), Collections.emptyList());
        // 当前用户拥有权限的科室
        String userId = LocalContextHelper.getLoginUserId();
        CoreUserPermissionBean filter = new CoreUserPermissionBean();
        filter.setUserId(userId);
        filter.setPermissionTypeId("T_CORE_ORG");
        List<CoreUserPermissionBean> userPermissionList = userPermissionService.getDao().selectList(filter);
        List<String> orgIdList = userPermissionList.stream().map(CoreUserPermissionBean::getTargetId).collect(Collectors.toList());

        List<EmcOrderTaskBean> orderTaskList = orderTaskAnalysisList.stream().filter(a -> orgIdList.contains(a.getAnalysisOrgId())).collect(Collectors.toList());
        // 修改样品测试的状态置为done
        orderTaskList.forEach(task -> task.setStatus(EmcBizConstant.ORDERTASK_STATUS_DONE));
        orderTaskService.getDao().update(orderTaskList, "STATUS");

        // 插入样品测试操作记录表
        orderTaskLogService.insert(orderTaskList, EmcBizConstant.ORDER_TASK_LOG_LEADER_AUDIT, EmcBizConstant.ORDER_TASK_LOG_ANALYSE_AUDIT);

        // 插入样品测试关联批的审核人信息
        List<String> runIdList = orderTaskList.stream().filter(a -> !StringUtils.isEmpty(a.getRunId())).map(EmcOrderTaskBean::getRunId).collect(Collectors.toList());
        List<EmcRunBean> runList = getDao().selectListByIds(runIdList);
        List<EmcRunBean> updateRunList = new ArrayList<>();
        for (EmcRunBean run : runList) {
            EmcRunBean updateRun = new EmcRunBean();
            updateRun.setId(run.getId());
            updateRun.setAuditedUserId(LocalContextHelper.getLoginUserId());
            updateRun.setAuditedUserName(LocalContextHelper.getLoginUser().getUserName());
            if (run.getAuditedDate() != null) {
                updateRun.setAuditedDate(run.getAuditedDate());
            } else {
                updateRun.setAuditedDate(LocalDateTime.now());
            }
            updateRun.setStatus("qcAudit");
            updateRunList.add(updateRun);
        }

        getDao().update(updateRunList, "AUDITEDUSERID", "AUDITEDUSERNAME", "AUDITEDDATE", "STATUS");
        // 分析审核插入审核记录
        List<EmcAuditRecordBean> auditRecordList = new ArrayList<>();
        List<EmcRunBean> submitRunList = getDao().selectListByIds(runIdList);

        for (EmcRunBean submitRun : submitRunList) {
            EmcAuditRecordBean auditRecord = new EmcAuditRecordBean();
            auditRecord.setId(ApplicationContextHelper.getNextIdentity());
            auditRecord.setProjId(submitRun.getProjId());
            auditRecord.setRunId(submitRun.getId());
            auditRecord.setComments("通过");
            auditRecord.setLogDesc("批号:" + submitRun.getId());
            auditRecord.setBizCategory("run");
            auditRecord.setStepCode("5");
            auditRecord.setAuditCategory("结果录入");
            auditRecord.setOperateCategory("审核");
            auditRecord.setTaskName("分析审核");
            auditRecord.setNextTaskName("报告编制");

            auditRecordList.add(auditRecord);
        }
        auditRecordService.getDao().insert(auditRecordList);

        // 判断提交的分析审核数据，是否可以生成报告。（相同的projId和monitorTmplId下的所有样品测试状态全部为done，则生成报告）
        List<EmcOrderTaskBean> reportOrderTaskList = orderTaskService.getDao().selectList(filterList, Arrays.asList("PROJID", "MONITORTMPLID"), Collections.emptyList());
        // 更新点位状态
        folderService.updateStatus(reportOrderTaskList);
        // 判断对应的项目样品类型下的样品测试是不是都已经完成，完成的话将报告状态置为待录入
        reportService.updateReportStatus(reportOrderTaskList);

        // 将报告所在项目节点状态置为报告编制
        List<Long> projIdList = projectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());
        // 更新项目状态
        updateProject(projIdList);
        // 更新项目节点
        ProjectNodeUtils.updateNodes(projIdList);

        // 合成项目pdf报表
        composeProjectPdfReport(orderTaskList);
    }

    @Override
    @Transactional
    public void addQc(List<EmcOrderTaskBean> orderTaskList, List<EmcQcCategoryBean> qcCategoryList, String stockBatchNo, boolean formulaComputing) {
        // 查询实验室标准样品信息
        Map<Long, EmcStockConcentBean> itemConcentMap = lookupStdInfo(stockBatchNo);

        // 2、准备数据
        String runId = orderTaskList.get(0).getRunId();
        List<EmcRunBean> runList = getDao().selectListByOneColumnValue(runId, "id", Arrays.asList("id", "relatedRunId"));
        List<String> runIdBeforeList = new ArrayList<>(Arrays.asList(StringUtils.split(runList.get(0).getRelatedRunId(), ",")));
        if (runIdBeforeList.isEmpty()) {
            runIdBeforeList.add(runId);
        }
        List<String> runIdList = runIdBeforeList.stream().filter(r -> !StringUtils.isEmpty(r)).collect(Collectors.toList());
        // 批下所有的orderTask记录，步骤5更新杯号要用
        List<EmcOrderTaskBean> orderTaskAllList = orderTaskService.getDao().selectListByOneColumnValues(runIdList, "RUNID", Order.asc("CUPNO"));
        ArrayList<EmcOrderTaskBean> uniqueOrderTaskList = orderTaskAllList.stream().filter(task -> PfConstant.N_A.equals(task.getQcCategoryCode())).collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmcOrderTaskBean::getTestMethodId))), ArrayList::new));
        if (uniqueOrderTaskList.isEmpty()) {
            // 处理批下只有现场质控样，没有正常样的场景
            uniqueOrderTaskList = orderTaskAllList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmcOrderTaskBean::getTestId))), ArrayList::new));
        }

        List<Long> allOrderTaskIdList = orderTaskAllList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        boolean containBlank = qcCategoryList.stream().anyMatch(qc -> StringUtils.equals(qc.getQcCategoryId(), EmcConstant.QCCATEGORY_QCCATEGORYID_L_BLANK));
        List<EmcOrderTaskResultBean> allTaskResultList = !containBlank ? Collections.emptyList() : orderTaskResultService.getDao().selectListByOneColumnValues(allOrderTaskIdList, "orderTaskId");

        List<EmcOrderTaskResultBean> uniqueTaskResultList = allTaskResultList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(r -> r.getItemId() + "$" + r.getRepeatNo()))), ArrayList::new));
        uniqueOrderTaskList.forEach(task -> uniqueTaskResultList.stream().filter(r -> ObjectUtils.equals(task.getTestMethodId(), r.getTestMethodId())).forEach(r -> r.setOrderTaskId(task.getId())));
        List<Long> uniqueItemIdList = uniqueTaskResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
        List<EmcItemBean> uniqueItemList = itemService.selectListByIds(uniqueItemIdList);

        // order表数据
        List<Long> idList = orderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> selectOrderList = orderService.getDao().selectListByIds(idList);
        // orderTask表数据，这是要复制的数据
        List<EmcOrderTaskBean> selectOrderTaskList = orderTaskAllList.stream().filter(a -> idList.contains(a.getOrderId())).collect(Collectors.toList());
        // 根据样品测试的杯号生成对样品排序后的orderList
        Map<Long, List<EmcOrderTaskBean>> orderTaskMap = selectOrderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getOrderId));
        List<EmcOrderTaskBean> sortList = new ArrayList<>();
        orderTaskMap.forEach((key, value) -> {
            sortList.add(value.get(0));
        });
        CollectionUtils.sort(sortList, (o1, o2) -> o1.getCupNo().compareTo(o2.getCupNo()));
        List<EmcOrderBean> orderList = new ArrayList<>();
        sortList.forEach(a -> {
            Long id = a.getOrderId();
            EmcOrderBean order = selectOrderList.stream().filter(b -> ObjectUtils.equals(b.getId(), id)).findAny().orElse(null);
            orderList.add(order);
        });
        // orderTaskResult表数据
        List<Long> orderTaskIdList = selectOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID");
        // 根据测试ID取测试下启用的分析类型为分析的分析项
        List<Long> testMethodIdList = selectOrderTaskList.stream().map(EmcOrderTaskBean::getTestMethodId).distinct().collect(Collectors.toList());
        List<EmcTestMethodBean> testMethodList = testMethodService.getDao().selectListByOneColumnValues(testMethodIdList, "id", Arrays.asList("id", "acqNoSuffix"));
        Map<Long, String> acqNoSuffixMap = testMethodList.stream().filter(testMethod -> !StringUtils.isEmpty(testMethod.getAcqNoSuffix())).collect(Collectors.toMap(EmcTestMethodBean::getId, EmcTestMethodBean::getAcqNoSuffix));

        List<EmcItemBean> itemTempList = itemService.selectListByFilter(SearchFilter.instance().match("TESTMETHODID", testMethodIdList).filter(MatchPattern.OR)
                .match("ACTIVATEDFLAG", Constant.ACTIVATED_STATUS_YES).filter(MatchPattern.EQ).match("ANALYSISCATEGORY", EmcBizConstant.ANALYSISCATEGORY_ANALYSIS).filter(MatchPattern.EQ));
        List<EmcItemBean> itemList = new ArrayList<>();
        List<EmcOrderItemGroupBean> oldOrderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(idList, "ORDERID");
        // 限制分析项需要时对应分析项组里面的数据
        if (!oldOrderItemGroupList.isEmpty()) {
            List<Long> capacityItemGroupIdList = oldOrderItemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
            List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByIds(capacityItemGroupIdList, Arrays.asList("id", "itemGroup", "testMethodId"));
            List<EmcItemGroupBean> filterItemGroupList = new ArrayList<>();
            capacityItemGroupList.forEach(group -> {
                EmcItemGroupBean itemGroup = new EmcItemGroupBean();
                itemGroup.setTestMethodId(group.getTestMethodId());
                itemGroup.setItemGroup(group.getItemGroup());
                filterItemGroupList.add(itemGroup);
            });
            List<EmcItemGroupBean> itemGroupList = itemGroupService.getDao().selectList(filterItemGroupList, Arrays.asList("testMethodId", "itemGroup"), Arrays.asList("id", "testMethodId", "itemGroup"));
            List<Long> itemGroupIdList = itemGroupList.stream().map(EmcItemGroupBean::getId).collect(Collectors.toList());

            // 获取分析项
            List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID");
            List<Long> itemIdList = groupItemList.stream().map(EmcGroupItemBean::getItemId).distinct().collect(Collectors.toList());
            itemList = itemTempList.stream().filter(e -> itemIdList.contains(e.getId())).collect(Collectors.toList());
        } else {
            Map<String, List<EmcOrderTaskBean>> orderTaskGroupMap = selectOrderTaskList.stream().collect(Collectors.groupingBy(a -> a.getTestMethodId() + a.getSchemeName() + a.getMonitorTmplId()));
            List<EmcOrderItemGroupBean> queryFilterList = new ArrayList<>();
            orderTaskGroupMap.forEach((key, value) -> {
                EmcOrderItemGroupBean filter = new EmcOrderItemGroupBean();
                EmcOrderTaskBean model = value.get(0);
                filter.setTestMethodId(model.getTestMethodId());
                filter.setSchemeName(model.getSchemeName());
                filter.setMonitorTmplId(model.getMonitorTmplId());
                queryFilterList.add(filter);
            });
            List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectList(queryFilterList, Arrays.asList("TESTMETHODID", "SCHEMENAME", "MONITORTMPLID"),
                    Collections.emptyList());
            List<Long> capacityItemGroupIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
            List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByIds(capacityItemGroupIdList, Arrays.asList("id", "itemGroup", "testMethodId"));
            List<EmcItemGroupBean> filterItemGroupList = new ArrayList<>();
            capacityItemGroupList.forEach(group -> {
                EmcItemGroupBean itemGroup = new EmcItemGroupBean();
                itemGroup.setTestMethodId(group.getTestMethodId());
                itemGroup.setItemGroup(group.getItemGroup());
                filterItemGroupList.add(itemGroup);
            });
            List<EmcItemGroupBean> itemGroupList = itemGroupService.getDao().selectList(filterItemGroupList, Arrays.asList("testMethodId", "itemGroup"), Arrays.asList("id", "testMethodId", "itemGroup"));
            List<Long> itemGroupIdList = itemGroupList.stream().map(EmcItemGroupBean::getId).collect(Collectors.toList());
            // 获取分析项
            List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID");
            List<Long> itemIdList = groupItemList.stream().map(EmcGroupItemBean::getItemId).distinct().collect(Collectors.toList());
            itemList = itemTempList.stream().filter(e -> itemIdList.contains(e.getId())).collect(Collectors.toList());
        }

        // 准备存放插入数据用的List
        List<EmcFolderBean> folderList = new ArrayList<>();
        List<EmcOrderBean> copyOrderList = new ArrayList<>();
        List<EmcOrderTaskBean> copyOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> copyOrderTaskResultList = new ArrayList<>();
        List<EmcOrderItemGroupBean> newOrderItemGroupList = new ArrayList<>();
        List<EmcQcRelatedOrderBean> qcRelatedOrderList = new ArrayList<>();

        // 3、添加folder数据

        Long folderId = null;
        // 查询批下是否存在质控样的folder(质控样的folder才有runId)
        EmcFolderBean filter = new EmcFolderBean();
        filter.setRunId(runId);
        EmcFolderBean qcFolder = folderService.getDao().selectFirstIfPresent(filter);
        // orderNo生成规则为folderNo+三位流水号
        String orderNo = null;
        if (qcFolder == null) {
            String folderNo = ApplicationContextHelper.getNextSequence("T_EMC_FOLDER_A");
            folderId = ApplicationContextHelper.getNextIdentity();
            qcFolder = new EmcFolderBean();
            qcFolder.setId(folderId);
            qcFolder.setStatus(EmcBizConstant.FOLDER_STATUS_DRAW);
            qcFolder.setFolderNo(folderNo);
            qcFolder.setRunId(runId);
            orderNo = folderNo + "000";
            folderList.add(qcFolder);
        } else {
            folderId = qcFolder.getId();
            EmcOrderBean tempOrderFirst = new EmcOrderBean();
            tempOrderFirst.setFolderId(folderId);
            // 如果folder存在那么orderNo从数据库取最大值
            EmcOrderBean tempOrder = orderService.selectFirstByFilter(SearchFilter.instance().match("folderId", folderId).filter(MatchPattern.EQ)
                    .match("qcCategoryCode", EmcConstant.QC_L_DUP).filter(MatchPattern.DIFFER)
                    .match("qcCategoryCode", EmcConstant.QCCATEGORY_QCCATGORYID_L_SPKC).filter(MatchPattern.DIFFER)
                    .match("qcCategoryCode", PfConstant.QC_L_SPKC_P).filter(MatchPattern.DIFFER)
                    .match("qcCategoryCode", PfConstant.QCCATEGORY_QCCATEGORYID_L_BLANK).filter(MatchPattern.DIFFER)
                    .match("qcCategoryCode", PfConstant.QCCATEGORY_QCCATEGORYID_QC_STD).filter(MatchPattern.DIFFER)
                    .match("qcCategoryCode", PfConstant.QCCATEGORY_QCCATEGORYID_L_SPKCK).filter(MatchPattern.DIFFER)
                    .match("qcCategoryCode", PfConstant.QC_L_SPKCK_P).filter(MatchPattern.DIFFER)
                    .match("qcCategoryCode", PfConstant.QC_QXJH).filter(MatchPattern.DIFFER)
                    .match("qcCategoryCode", PfConstant.QC_L_QXCHD).filter(MatchPattern.DIFFER), Order.desc("ORDERNO"));
            orderNo = tempOrder == null ? qcFolder.getFolderNo() + "000" : tempOrder.getOrderNo();
        }
        // 点位编号累加值
        String orderNoTemp = orderNo;

        // 特殊编号规则的QC类型
        List<String> qcCategoryCodeList = qcCategoryList.stream().map(EmcQcCategoryBean::getQcCategoryId).collect(Collectors.toList());
        List<String> specialSuffixQcCodeList = Arrays.asList(EmcConstant.QC_L_DUP, EmcConstant.QCCATEGORY_QCCATGORYID_L_SPKC, PfConstant.QC_L_SPKC_P);
        List<String> specialPrefixQcCodeList = Arrays.asList(EmcConstant.QCCATEGORY_QCCATEGORYID_L_BLANK, EmcConstant.QCCATEGORY_QCCATEGORYID_QC_STD,
                EmcConstant.QCCATEGORY_QCCATEGORYID_L_SPKCK, PfConstant.QC_L_SPKCK_P,
                PfConstant.QC_QXJH, PfConstant.QC_L_QXCHD);

        boolean hasSpecialSuffixQc = qcCategoryCodeList.stream().anyMatch(specialSuffixQcCodeList::contains);
        boolean hasSpecialPrefixQc = qcCategoryCodeList.stream().anyMatch(specialPrefixQcCodeList::contains);

        Map<String, String> specialQcSuffixMap = new HashMap<>(specialPrefixQcCodeList.size() + specialSuffixQcCodeList.size());
        specialQcSuffixMap.put(EmcConstant.QC_L_DUP, PfConstant.QC_LDUP_ORDER_NO_SUFFIX);
        specialQcSuffixMap.put(EmcConstant.QCCATEGORY_QCCATGORYID_L_SPKC, PfConstant.QC_SPKC_ORDER_NO_SUFFIX);
        specialQcSuffixMap.put(PfConstant.QC_L_SPKC_P, PfConstant.QC_SPKC_P_ORDER_NO_SUFFIX);
        specialQcSuffixMap.put(EmcConstant.QCCATEGORY_QCCATEGORYID_L_BLANK, PfConstant.QC_LBLANK_ORDER_NO_PREFIX);
        specialQcSuffixMap.put(EmcConstant.QCCATEGORY_QCCATEGORYID_QC_STD, PfConstant.QC_STD_ORDER_NO_PREFIX);
        specialQcSuffixMap.put(EmcConstant.QCCATEGORY_QCCATEGORYID_L_SPKCK, PfConstant.QC_SPKCK_ORDER_NO_PREFIX);
        specialQcSuffixMap.put(PfConstant.QC_L_SPKCK_P, PfConstant.QC_SPKCK_P_ORDER_NO_PREFIX);
        specialQcSuffixMap.put(PfConstant.QC_QXJH, PfConstant.QC_QXCH_ORDER_NO_PREFIX);
        specialQcSuffixMap.put(PfConstant.QC_L_QXCHD, PfConstant.QC_QXCHD_ORDER_NO_PREFIX);
        Map<String, Integer> specialSuffixOrderSeqNoMap = hasSpecialSuffixQc ? getSpecialSuffixOrderSeqNo(qcCategoryCodeList, specialSuffixQcCodeList, specialQcSuffixMap, orderList, folderId) : null;
        Map<String, Integer> specialPrefixOrderSeqNoMap = hasSpecialPrefixQc ? getSpecialPrefixOrderSeqNo(qcCategoryList, specialPrefixQcCodeList, specialQcSuffixMap, orderList, folderId) : null;

        // 4、添加order、ordertask、ordertaskresult数据
        List<Long> defaultValueResultIdList = new ArrayList<>();
        List<EmcOrderTaskBean> updateParentFlagTaskList = new ArrayList<>();

        // 不添加采集编号后缀的QC类型
        List<String> excludeAcqSuffixQcList = Arrays.asList(EmcConstant.QCCATEGORY_QCCATEGORYID_QC_STD, PfConstant.QC_QXJH, PfConstant.QC_L_QXCHD);

        for (int i = 0; i < orderList.size(); i++) {
            // 实验室空白类型数据杯号添加次数累加值
            Long labBlankIndex = 0L;
            // 样品下杯号添加次数累加值
            Long addTotalIndex = 0L;
            for (int j = 0; j < qcCategoryList.size(); j++) {
                EmcQcCategoryBean qcCategory = qcCategoryList.get(j);
                Long addNum = qcCategory.getAddNum() == null ? 1L : qcCategory.getAddNum();
                boolean isBlank = EmcConstant.QCCATEGORY_QCCATEGORYID_L_BLANK.equals(qcCategory.getQcCategoryId()); // 实验室空白类型数据需特殊处理
                boolean updateParNoFlag = "1".equals(qcCategory.getUpdateParNo()); // 是否更新父样编号字段
                boolean parOrderFlag = "1".equals(qcCategory.getParOrder()); // 是否有父样
                // 是否为曲线校核
                boolean isCurveCheck = Arrays.asList(PfConstant.QC_QXJH, PfConstant.QC_L_QXCHD).contains(qcCategory.getQcCategoryId());
                boolean isLowestCurveCheck = PfConstant.QC_L_QXCHD.equals(qcCategory.getQcCategoryId());
                // 是否为具有特殊编号规则的QC
                boolean isSpecialSuffixQc = specialSuffixQcCodeList.contains(qcCategory.getQcCategoryId());
                boolean isSpecialPrefixQc = specialPrefixQcCodeList.contains(qcCategory.getQcCategoryId());
                // 根据qc类型的添加数量插入多条数据
                for (Long num = 0L; num < addNum; num++) {

                    // 1)添加order表数据
                    EmcOrderBean order = orderList.get(i);
                    EmcOrderBean orderInfo = new EmcOrderBean();
                    BeanUtils.copyProperties(order, orderInfo, "ID", "PARENTORDERNO", "CREATEDBYID", "CREATEDBYNAME", "CREATEDTIME", "CREATEDBYORGID", "CREATEDBYORGNAME", "SAMPLEID");
                    Long id = ApplicationContextHelper.getNextIdentity();
                    orderInfo.setId(id);
                    orderInfo.setFolderId(folderId);
                    if (isSpecialSuffixQc) {
                        long newOrderSeqNo = Optional.ofNullable(specialSuffixOrderSeqNoMap.get(qcCategory.getQcCategoryId() + order.getOrderNo())).orElse(0) + num;
                        String newOrderNo = order.getOrderNo() + specialQcSuffixMap.get(qcCategory.getQcCategoryId()) + (newOrderSeqNo == 0 ? "" : newOrderSeqNo);
                        orderInfo.setOrderNo(newOrderNo);
                    } else if (isSpecialPrefixQc) {
                        long newOrderSeqNo = Optional.ofNullable(specialPrefixOrderSeqNoMap.get(qcCategory.getQcCategoryId())).orElse(0) + ((i * addNum) + num);
                        String newOrderNo = runId + "-" + specialQcSuffixMap.get(qcCategory.getQcCategoryId()) + (newOrderSeqNo == 0 ? "" : newOrderSeqNo);
                        if (isCurveCheck) {
                            String checkPoint = isLowestCurveCheck ? "0" : Optional.ofNullable(qcCategory.getExt$Item("checkPoint")).orElse("1");
                            newOrderNo = runId + "-" + specialQcSuffixMap.get(qcCategory.getQcCategoryId()) + checkPoint + (newOrderSeqNo == 0 ? "" : "-" + newOrderSeqNo);
                        }
                        orderInfo.setOrderNo(newOrderNo);
                    } else {
                        orderNoTemp = getNextOrderNo(orderNoTemp);
                        orderInfo.setOrderNo(qcFolder.getFolderNo() + orderNoTemp);
                    }

                    orderInfo.setLabelNo(orderInfo.getOrderNo());
                    orderInfo.setMonitorTmplId(order.getMonitorTmplId());
                    orderInfo.setQcCategoryId(qcCategory.getId());
                    orderInfo.setQcCategoryCode(qcCategory.getQcCategoryId());
                    orderInfo.setQcName(qcCategory.getQcName());
                    orderInfo.setParentOrderNo(null);
                    orderInfo.setOrderDesc(null);
                    orderInfo.setProjId(null);

                    // 设置质控样关联的样品编号
                    qcRelatedOrderList.addAll(buildQcRelatedOrder(orderTaskList, qcCategory, order, orderInfo, updateParNoFlag));

                    // 判断是否添加父样编号
                    if (updateParNoFlag && order != null) {
                        orderInfo.setParentOrderNo(order.getOrderNo());
                    }
                    copyOrderList.add(orderInfo);

                    // 添加order标志，判断order下是否有result插入，如果有再更新order下的杯号
                    boolean addOrderFlag = false;
                    // 存放此order下添加的orderTask
                    List<EmcOrderTaskBean> orderTaskTempList = new ArrayList<>();

                    // 复制orderItemGroup表和其他业务不相关
                    List<EmcOrderItemGroupBean> tempOrderItemList = oldOrderItemGroupList.stream().filter(e -> ObjectUtils.equals(order.getId(), e.getOrderId())).collect(Collectors.toList());
                    tempOrderItemList.stream().forEach(e -> {
                        EmcOrderItemGroupBean orderItemGroup = new EmcOrderItemGroupBean();
                        BeanUtils.copyProperties(e, orderItemGroup, EmcConstant.CREATED_COLUMNS);
                        orderItemGroup.setId(ApplicationContextHelper.getNextIdentity());
                        orderItemGroup.setOrderId(id);
                        newOrderItemGroupList.add(orderItemGroup);
                    });

                    List<EmcOrderTaskBean> targetTaskList = isBlank ? uniqueOrderTaskList : selectOrderTaskList;
                    for (int k = 0; k < targetTaskList.size(); k++) {
                        EmcOrderTaskBean orderTask = targetTaskList.get(k);
                        if (isBlank || ObjectUtils.equals(orderTask.getOrderId(), order.getId())) {

                            // 2)添加ordertask数据
                            // 添加ordertask标志，判断ordertask下是否有result插入，如果有再插入ordertask
                            boolean addOrderTaskFlag = false;
                            EmcOrderTaskBean otInfo = new EmcOrderTaskBean();
                            BeanUtils.copyProperties(orderTask, otInfo, "ID", "CUPNO", "CREATEDBYID", "CREATEDBYNAME", "CREATEDTIME", "CREATEDBYORGID", "CREATEDBYORGNAME", "STOCKID", "STOCKBATCHNO");
                            Long orderTaskId = ApplicationContextHelper.getNextIdentity();

                            otInfo.setId(orderTaskId);
                            otInfo.setFolderId(folderId);
                            otInfo.setOrderId(id);

                            // 仪器采集编号后缀
                            String acqNoSuffix = acqNoSuffixMap.get(orderTask.getTestMethodId());
                            boolean excludeSuffix = excludeAcqSuffixQcList.stream().anyMatch(qcCode -> StringUtils.equals(qcCode, qcCategory.getQcCategoryId()));
                            otInfo.setEquiptAcquNo((StringUtils.isEmpty(acqNoSuffix) || excludeSuffix) ? orderInfo.getOrderNo() : orderInfo.getOrderNo() + "-" + acqNoSuffix);

                            otInfo.setMonitorTmplId(orderTask.getMonitorTmplId());
                            otInfo.setQcCategoryId(qcCategory.getId());
                            otInfo.setQcCategoryCode(qcCategory.getQcCategoryId());
                            otInfo.setQcName(qcCategory.getQcName());
                            otInfo.setStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED);
                            otInfo.setRunId(runId);
                            //otInfo.setParentId(orderTask.getId());
                            otInfo.setCupNo(null);
                            otInfo.setProjId(null);

                            if (updateParNoFlag) {
                                otInfo.setParentId(orderTask.getId());
                                otInfo.setExt$Item("parentFlag", Constant.YES);

                                EmcOrderTaskBean updateTask = new EmcOrderTaskBean();
                                updateTask.setId(orderTask.getId());
                                updateTask.setExt$Item("parentFlag", Constant.YES);
                                updateParentFlagTaskList.add(updateTask);
                            }

                            // 对qc类型是QC-STD插入库存编号
                            if (EmcConstant.QCCATEGORY_QCCATEGORYID_QC_STD.equals(qcCategory.getQcCategoryId())) {
                                otInfo.setStockBatchNo(stockBatchNo);
                            }

                            List<EmcItemBean> targetItemList = isBlank ? uniqueItemList : itemList;
                            if (!targetItemList.isEmpty()) {
                                List<EmcOrderTaskResultBean> targetResultList = isBlank ? uniqueTaskResultList : orderTaskResultList;
                                for (EmcItemBean b : targetItemList) {
                                    for (int e = 0; e < targetResultList.size(); e++) {
                                        // 3)插入ordertaskresult数据
                                        EmcOrderTaskResultBean orderTaskResult = targetResultList.get(e);
                                        if (ObjectUtils.equals(orderTaskResult.getOrderTaskId(), orderTask.getId())) {
                                            if (ObjectUtils.equals(orderTaskResult.getItemId(), b.getId()) && !StringUtils.isEmpty(b.getQcCategoryId())
                                                    && b.getQcCategoryId().contains("" + qcCategory.getId())) {
                                                addOrderTaskFlag = true;
                                                addOrderFlag = true;

                                                EmcOrderTaskResultBean otrInfo = new EmcOrderTaskResultBean();
                                                BeanUtils.copyProperties(orderTaskResult, otrInfo, "ORIGINALRESULT", "REPORTRESULT", "CREATEDBYID", "CREATEDBYNAME", "CREATEDTIME", "CREATEDBYORGID", "CREATEDBYORGNAME");
                                                otrInfo.setId(ApplicationContextHelper.getNextIdentity());
                                                otrInfo.setFolderId(otInfo.getFolderId());
                                                otrInfo.setOrderId(id);
                                                otrInfo.setOrderNo(orderInfo.getOrderNo());
                                                otrInfo.setOrderTaskId(orderTaskId);
                                                otrInfo.setQcCategoryId(qcCategory.getId());
                                                otrInfo.setQcCategoryCode(qcCategory.getQcCategoryId());
                                                otrInfo.setQcName(qcCategory.getQcName());
                                                otrInfo.setRunId(runId);
                                                otrInfo.setOriginalResult(b.getDefaultResult());
                                                otrInfo.setReportResult(null);
                                                otrInfo.setResultStatus(null);
                                                otrInfo.setProjId(null);
                                                otrInfo.setParentOrderNo(null);
                                                otrInfo.setAvgResult(null);
                                                // 曲线校核设置默认的相对相差修约规则为FDA,1
                                                otrInfo.setRn10(isCurveCheck && Constant.YES.equals(b.getCalQcResult()) ? "FDA,1" : null);

                                                if (updateParNoFlag) {
                                                    otrInfo.setParentOrderNo(order.getOrderNo());
                                                }

                                                // 复制实验室标准样品标准值和不确定度
                                                if (itemConcentMap.size() > 0 && itemConcentMap.get(b.getId()) != null) {
                                                    EmcStockConcentBean stockConcent = itemConcentMap.get(b.getId());
                                                    String uncertainty = Optional.ofNullable(stockConcent.getUncertainty()).orElse(stockConcent.getUncertaintyPercent() + "%");

                                                    otrInfo.setStandQty(ObjectUtils.isEmpty(stockConcent.getStdValue()) ? "" : stockConcent.getStdValue() + "");
                                                    otrInfo.setRn16(uncertainty);
                                                    otrInfo.setIndexLowLimit(ObjectUtils.isEmpty(stockConcent.getLowLimit()) ? "" : stockConcent.getLowLimit() + "");
                                                    otrInfo.setIndexHighLimit(ObjectUtils.isEmpty(stockConcent.getHighLimit()) ? "" : stockConcent.getHighLimit() + "");
                                                }

                                                copyOrderTaskResultList.add(otrInfo);

                                                // 有默认值的分析项，在添加完成后需要自动触发一次公式
                                                if (!StringUtils.isEmpty(b.getDefaultResult())) {
                                                    defaultValueResultIdList.add(otrInfo.getId());
                                                }
                                            }
                                        }
                                    }
                                    // qc类型关联测试中启用的分析项都要加上
                                    boolean enableQc = !targetResultList.isEmpty() && targetResultList.stream().noneMatch(z -> z.getItemId().equals(b.getId()));
                                    if (enableQc) {
                                        if (!StringUtils.isEmpty(b.getQcCategoryId()) && b.getQcCategoryId().contains("" + qcCategory.getId()) && !ObjectUtils.isEmpty(b.getTestMethodId())
                                                && !ObjectUtils.isEmpty(orderTask.getTestMethodId()) && ObjectUtils.equals(b.getTestMethodId(), orderTask.getTestMethodId())) {
                                            addOrderTaskFlag = true;
                                            addOrderFlag = true;

                                            EmcOrderTaskResultBean otrInfo = new EmcOrderTaskResultBean();
                                            otrInfo.setId(ApplicationContextHelper.getNextIdentity());
                                            otrInfo.setFolderId(otInfo.getFolderId());
                                            otrInfo.setOrderId(id);
                                            otrInfo.setOrderNo(orderInfo.getOrderNo());
                                            otrInfo.setOrderTaskId(orderTaskId);
                                            otrInfo.setQcCategoryId(qcCategory.getId());
                                            otrInfo.setQcCategoryCode(qcCategory.getQcCategoryId());
                                            otrInfo.setQcName(qcCategory.getQcName());
                                            otrInfo.setTestId(orderTask.getTestId());
                                            otrInfo.setMethodId(orderTask.getMethodId());
                                            otrInfo.setTestMethodId(b.getTestMethodId());
                                            otrInfo.setMethodVersionId(targetResultList.get(0).getMethodVersionId());
                                            otrInfo.setRunId(runId);
                                            otrInfo.setItemId(b.getId());
                                            otrInfo.setFrequency(EmcConstant.ONE);
                                            otrInfo.setCycle(EmcConstant.ONE);
                                            otrInfo.setItemAliasName(b.getItemAliasName());
                                            otrInfo.setItemName(b.getItemName());
                                            otrInfo.setItemOrderNo(b.getOrderNo());
                                            otrInfo.setOriginalResult(b.getDefaultResult());
                                            otrInfo.setAnalysisType(EmcBizConstant.ANALYSISCATEGORY_ANALYSIS);
                                            otrInfo.setScriptFlag(b.getScriptFlag());
                                            otrInfo.setRepeatNo(b.getRepeatItem());
                                            otrInfo.setRepeatQty(b.getRepeatItem());
                                            otrInfo.setLowLimit(b.getLowLimit());
                                            otrInfo.setUnitName(b.getUnitName());
                                            otrInfo.setReportDisplay(b.getReportDisplay());
                                            otrInfo.setHighLimit(b.getHighLimit());
                                            otrInfo.setRevisionRule(b.getRevisionRule());
                                            otrInfo.setResultCategory(b.getResultCategory());
                                            otrInfo.setReportResult(null);
                                            otrInfo.setResultStatus(null);
                                            otrInfo.setProjId(null);
                                            otrInfo.setAvgResult(null);
                                            // 曲线校核设置默认的相对相差修约规则为FDA,1
                                            otrInfo.setRn10(isCurveCheck && Constant.YES.equals(b.getCalQcResult()) ? "FDA,1" : null);

                                            if (updateParNoFlag) {
                                                otrInfo.setParentOrderNo(order.getOrderNo());
                                            }

                                            // 复制实验室标准样品标准值和不确定度
                                            if (itemConcentMap.size() > 0 && itemConcentMap.get(b.getId()) != null) {
                                                EmcStockConcentBean stockConcent = itemConcentMap.get(b.getId());
                                                String uncertainty = Optional.ofNullable(stockConcent.getUncertainty()).orElse(stockConcent.getUncertaintyPercent() + "%");

                                                otrInfo.setStandQty(ObjectUtils.isEmpty(stockConcent.getStdValue()) ? "" : stockConcent.getStdValue() + "");
                                                otrInfo.setRn16(uncertainty);
                                                otrInfo.setIndexLowLimit(ObjectUtils.isEmpty(stockConcent.getLowLimit()) ? "" : stockConcent.getLowLimit() + "");
                                                otrInfo.setIndexHighLimit(ObjectUtils.isEmpty(stockConcent.getHighLimit()) ? "" : stockConcent.getHighLimit() + "");
                                            }

                                            copyOrderTaskResultList.add(otrInfo);

                                            // 有默认值的分析项，在添加完成后需要自动触发一次公式
                                            if (!StringUtils.isEmpty(b.getDefaultResult())) {
                                                defaultValueResultIdList.add(otrInfo.getId());
                                            }
                                        }
                                    }
                                }

                            }
                            // ordertask下有result插入，才会插入ordertask
                            if (addOrderTaskFlag) {
                                copyOrderTaskList.add(otInfo);
                                orderTaskTempList.add(orderTask);
                            }
                        }
                    }
                    // 如果order下的orderTask至少插入过一次，就更新杯号
                    // 注意，此处杯号的设置和更新为整个方法最为复杂的地方,需注意值传递和引用传递
                    if (addOrderFlag) {
                        EmcOrderTaskBean orderTask = orderTaskTempList.get(0);
                        if (isBlank) {
                            // 实验室空白的杯号设置规则
                            labBlankIndex++;
                            Long cupNo = labBlankIndex;
                            copyOrderTaskList.stream().filter(a -> a.getCupNo() != null && a.getCupNo() >= cupNo).forEach(n -> n.setCupNo(1L + n.getCupNo()));
                            copyOrderTaskList.stream().filter(a -> a.getCupNo() == null).forEach(n -> n.setCupNo(cupNo));
                            orderTaskAllList.stream().forEach(a -> a.setCupNo(1L + a.getCupNo()));
                        } else if (!parOrderFlag) {
                            // 非实验室空白且qc类型是否有父样为否或者空
                            Long cupNo = orderTask.getCupNo();
                            copyOrderTaskList.stream().filter(a -> a.getCupNo() != null && a.getCupNo() > cupNo).forEach(n -> n.setCupNo(1L + n.getCupNo()));
                            copyOrderTaskList.stream().filter(a -> a.getCupNo() == null).forEach(n -> n.setCupNo(cupNo));
                            orderTaskAllList.stream().filter(a -> a.getCupNo() >= cupNo).forEach(a -> a.setCupNo(1L + a.getCupNo()));
                        } else {
                            // 插入次数+1
                            addTotalIndex++;
                            // 非实验室空白且qc类型是否有父样是
                            Long cupNo = orderTask.getCupNo() + addTotalIndex;
                            copyOrderTaskList.stream().filter(a -> a.getCupNo() != null && a.getCupNo() >= cupNo).forEach(n -> n.setCupNo(1L + n.getCupNo()));
                            copyOrderTaskList.stream().filter(a -> a.getCupNo() == null).forEach(n -> n.setCupNo(cupNo));
                            orderTaskAllList.stream().filter(a -> a.getCupNo() >= cupNo).forEach(n -> n.setCupNo(1L + n.getCupNo()));
                        }
                    }
                }
            }
        }
        // 5、插入数据库
        if (!copyOrderTaskList.isEmpty()) {
            copyOrderContainer(runId, copyOrderTaskList, copyOrderList, specialSuffixQcCodeList);

            folderService.getDao().insert(folderList);
            orderService.getDao().insert(copyOrderList);
            orderTaskService.getDao().insert(copyOrderTaskList);
            // 过滤下没有添加的result
            List<Long> realorderTaskIdList = copyOrderTaskList.stream().map(a -> a.getId()).collect(Collectors.toList());
            List<EmcOrderTaskResultBean> realOrderTaskResultlist = copyOrderTaskResultList.stream().filter(a -> realorderTaskIdList.contains(a.getOrderTaskId())).collect(Collectors.toList());
            orderTaskResultService.getDao().insert(realOrderTaskResultlist);
            orderTaskService.getDao().update(orderTaskAllList, "CUPNO");
            orderItemGroupService.getDao().insert(newOrderItemGroupList);

            // 插入QC关联的样品数据
            qcRelatedOrderService.getDao().insert(qcRelatedOrderList);

            // 6、重新按照qc类型名称排列
            List<EmcOrderTaskBean> allTaskList = orderTaskService.getDao().selectListByOneColumnValue(runId, "RUNID", Order.asc("CUPNO"));
            List<EmcQcCategoryBean> qcList = qcCategoryService.getDao().selectListByOneColumnValue(EmcConstant.QCCATEGORY_USECATEGORY_LAB, "USECATEGORY");
            // 在原样品下面添加qc的数据
            List<Long> qcIdFirstList = qcList.stream().filter(a -> !EmcConstant.QCCATEGORY_QCCATEGORYID_L_BLANK.equals(a.getQcCategoryId()) && Constant.YES.equals(a.getParOrder())).map(b -> b.getId())
                    .distinct().collect(Collectors.toList());
            // 不包含实验室空白的在原样品上面添加qc的数据
            List<Long> qcIdSecondList = qcList.stream().filter(a -> !EmcConstant.QCCATEGORY_QCCATEGORYID_L_BLANK.equals(a.getQcCategoryId()) && !Constant.YES.equals(a.getParOrder()))
                    .map(b -> b.getId()).distinct().collect(Collectors.toList());
            // 在原样品下面添加qc的数据重新排列
            Map<Long, List<EmcOrderTaskBean>> mapFirst = allTaskList.stream().filter(a -> a.getParentId() != null && qcIdFirstList.contains(a.getQcCategoryId())).sorted(Comparator.comparing(EmcOrderTaskBean::getOrderId))
                    .collect(Collectors.groupingBy(EmcOrderTaskBean::getParentId, LinkedHashMap::new, Collectors.toList()));

            mapFirst.forEach((k, v) -> {
                List<Long> cupNoList = v.stream().map(a -> a.getCupNo()).distinct().collect(Collectors.toList());
                Collections.sort(cupNoList);
                CollectionUtils.sort(v, (o1, o2) -> (o1.getQcName() + o1.getOrderId()).compareTo(o2.getQcName() + o1.getOrderId()));
                List<Long> orderIdSortList = v.stream().map(a -> a.getOrderId()).distinct().collect(Collectors.toList());

                for (int i = 0; i < cupNoList.size(); i++) {
                    Long orderId = orderIdSortList.get(i);
                    Long cupNo = cupNoList.get(i);
                    v.stream().filter(a -> ObjectUtils.equals(a.getOrderId(), orderId)).forEach(b -> b.setCupNo(cupNo));
                }
            });

            // 不包含实验室空白的在原样品上面添加qc的数据重新排列
            Map<Long, List<EmcOrderTaskBean>> mapSecond = allTaskList.stream().filter(a -> a.getParentId() != null && qcIdSecondList.contains(a.getQcCategoryId())).sorted(Comparator.comparing(EmcOrderTaskBean::getOrderId))
                    .collect(Collectors.groupingBy(EmcOrderTaskBean::getParentId, LinkedHashMap::new, Collectors.toList()));

            mapSecond.forEach((k, v) -> {
                List<Long> cupNoList = v.stream().map(a -> a.getCupNo()).distinct().collect(Collectors.toList());
                Collections.sort(cupNoList);
                CollectionUtils.sort(v, (o1, o2) -> (o1.getQcName() + o1.getOrderId()).compareTo(o2.getQcName() + o1.getOrderId()));
                List<Long> orderIdSortList = v.stream().map(a -> a.getOrderId()).distinct().collect(Collectors.toList());

                for (int i = 0; i < cupNoList.size(); i++) {
                    Long orderId = orderIdSortList.get(i);
                    Long cupNo = cupNoList.get(i);
                    v.stream().filter(a -> ObjectUtils.equals(a.getOrderId(), orderId)).forEach(b -> b.setCupNo(cupNo));
                }
            });
            orderTaskService.getDao().update(allTaskList, "CUPNO");

            if (!updateParentFlagTaskList.isEmpty()) {
                orderTaskService.getDao().update(updateParentFlagTaskList, "parentFlag");
            }

            // 对有默认值的分析项触发一次公式计算
            if (!defaultValueResultIdList.isEmpty() && formulaComputing) {
                EmcOrderTaskResultComputingEngine.getEngine(defaultValueResultIdList).compute();
            }
        }
    }

    /**
     * 添加QC-复制样品容器信息
     *
     * @param copyTaskList
     * @param copyOrderList
     */
    private void copyOrderContainer(String runId, List<EmcOrderTaskBean> copyTaskList, List<EmcOrderBean> copyOrderList, List<String> concatOrderNoQcCodeList) {
        List<EmcOrderTaskBean> validTaskList = copyTaskList.stream().filter(task -> !ObjectUtils.isEmpty(task.getOrderContainerId())).collect(Collectors.toList());
        if (validTaskList.isEmpty()) {
            return;
        }

        List<Long> originalOcIdList = validTaskList.stream().map(EmcOrderTaskBean::getOrderContainerId).collect(Collectors.toList());
        List<EmcOrderContainerBean> originalOcList = orderContainerService.getDao().selectListByIds(originalOcIdList);

        List<EmcOrderContainerBean> newOcList = new ArrayList<>();

        copyOrderList.forEach(co -> {
            List<EmcOrderTaskBean> curTaskList = validTaskList.stream().filter(ct -> ObjectUtils.equals(ct.getOrderId(), co.getId())).collect(Collectors.toList());
            Map<Long, List<EmcOrderTaskBean>> validTaskMap = curTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getOrderContainerId));

            boolean concatOrderNo = concatOrderNoQcCodeList.contains(co.getQcCategoryCode());

            AtomicInteger ocIndex = new AtomicInteger(1);
            validTaskMap.forEach((orderContainerId, taskList) -> {
                List<Long> taskIdList = taskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
                EmcOrderContainerBean originalOc = originalOcList.stream().filter(oc -> ObjectUtils.equals(oc.getId(), orderContainerId)).findFirst().orElseGet(EmcOrderContainerBean::new);
                EmcOrderTaskBean basicTask = taskList.get(0);

                EmcOrderContainerBean newOc = new EmcOrderContainerBean();
                BeanUtils.copyProperties(originalOc, newOc, PfConstant.CREATED_COLUMNS);
                newOc.setProjId(null);
                newOc.setId(ApplicationContextHelper.getNextIdentity());
                newOc.setFolderId(basicTask.getFolderId());
                newOc.setOrderId(basicTask.getOrderId());
                newOc.setDeliveryId(null);
                newOc.setQcCategoryId(basicTask.getQcCategoryId());
                newOc.setQcCategoryCode(basicTask.getQcCategoryCode());
                newOc.setQcName(basicTask.getQcName());

                String orderNo = co.getOrderNo();
                if (StringUtils.contains(orderNo, "-")) {
                    String suffix = orderNo.substring(orderNo.indexOf("-"));
                    newOc.setOrderContainerNo((concatOrderNo ? originalOc.getOrderContainerNo() : runId) + suffix);
                } else {
                    newOc.setOrderContainerNo(orderNo + StringUtils.leftPad(ocIndex.getAndIncrement() + "", 3));
                }

                newOc.setOrderNo(orderNo);
                newOcList.add(newOc);

                copyTaskList.stream().filter(task -> taskIdList.contains(task.getId())).forEach(task -> {
                    task.setOrderContainerId(newOc.getId());
                    task.setEquiptAcquNo(newOc.getOrderContainerNo());
                });
            });
        });

        orderContainerService.getDao().insert(newOcList);
    }

    /**
     * 查询实验室标准样品信息
     *
     * @param stockBatchNo 库存批号
     * @return
     */
    private Map<Long, EmcStockConcentBean> lookupStdInfo(String stockBatchNo) {
        if (StringUtils.isEmpty(stockBatchNo)) {
            return Collections.emptyMap();
        }

        Map<Long, EmcStockConcentBean> itemConcentMap = new HashMap<>();
        List<EmcStockBean> stockList = stockService.getDao().selectListByOneColumnValue(stockBatchNo, "BATCHNO");
        List<Long> stockIdList = stockList.stream().map(EmcStockBean::getId).distinct().collect(Collectors.toList());
        List<EmcStockConcentBean> stockConcentList = stockConcentService.getDao().selectListByOneColumnValues(stockIdList, "STOCKID");
        List<Long> maintainIdList = stockConcentList.stream().map(EmcStockConcentBean::getMtlMaintainId).distinct().collect(Collectors.toList());
        List<EmcMtlMaintainItemBean> mtlMaintainList = mtlMaintainItemService.getDao().selectListByOneColumnValues(maintainIdList, "MTLMAITAINID");
        mtlMaintainList.forEach(m -> {
            EmcStockConcentBean stockConcent = stockConcentList.stream().filter(c -> ObjectUtils.equals(m.getMtlMaitainId(), c.getMtlMaintainId())).findFirst().get();
            itemConcentMap.put(m.getItemId(), stockConcent);
        });

        return itemConcentMap;
    }

    @Override
    public Long init(RestJsonWrapperBean wrapper) {
        List<EmcRunBean> runList = wrapper.parse(EmcRunBean.class);
        List<String> runIdList = runList.stream().filter(r -> !StringUtils.isEmpty(r.getId())).map(EmcRunBean::getId).collect(Collectors.toList());

        List<EmcOrderTaskResultBean> taskResultList = orderTaskResultService.selectListByFilter(SearchFilter.instance().match("runId", runIdList).filter(MatchPattern.OR).match("originalResult", null).filter(MatchPattern.DIFFER));
        taskResultList.forEach(result -> {
            result.setOriginalResult(null);
            result.setReportResult(null);
            result.setResultStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED);
        });

        orderTaskResultService.getDao().update(taskResultList, "ORIGINALRESULT", "REPORTRESULT", "RESULTSTATUS");

        return 0L;
    }

    @Override
    public void completeWeighing(RestJsonWrapperBean wrapper) {
        List<EmcRunBean> runList = wrapper.parse(EmcRunBean.class);
        runList.forEach(run -> run.setExt$Item("weighingStatus", "2"));

        getDao().update(runList, "weighingStatus");

        // 完成称重时，触发计算公式
        List<String> runIdList = runList.stream().filter(r -> !StringUtils.isEmpty(r.getId())).map(EmcRunBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.selectListByFilter(SearchFilter.instance().match("runId", runIdList).filter(MatchPattern.OR)
                .match("analysisType", EmcBizConstant.ANALYSISCATEGORY_ANALYSIS).filter(MatchPattern.SEQ)
                .match("useType", "weighing").filter(MatchPattern.SEQ));

        List<Long> resultIdList = orderTaskResultList.stream().filter(result -> !StringUtils.isEmpty(result.getOriginalResult())).map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());

        if (!resultIdList.isEmpty()) {
            asyncFormulaComputingService.asyncComputing(runList, resultIdList, false);
        }
    }

    @Override
    public void completePretreat(RestJsonWrapperBean wrapper) {
        List<EmcRunBean> runList = wrapper.parse(EmcRunBean.class);
        runList.forEach(run -> run.setExt$Item("pretreatStatus", "2"));

        getDao().update(runList, "pretreatStatus");
    }

    /**
     * 判断是否更新项目表状态
     *
     * @param projIdList
     */
    private void updateProject(List<Long> projIdList) {
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(projIdList, "PROJID");
        List<EmcSampleBean> sampleList = sampleService.getDao().selectListByOneColumnValues(projIdList, "PROJID");
        List<EmcProjectBean> updateProjList = new ArrayList<>();
        for (Long a : projIdList) {
            boolean orderTaskAllMatch = orderTaskList.stream().filter(b -> ObjectUtils.equals(b.getProjId(), a)).allMatch(e -> EmcBizConstant.ORDERTASK_STATUS_DONE.equals(e.getStatus()));

            boolean sampleAllMatch = sampleList.stream().filter(b -> ObjectUtils.equals(b.getProjId(), a)).allMatch(e -> EmcBizConstant.SAMPLE_STATUS_DONE.equals(e.getStatus()));

            if (orderTaskAllMatch && sampleAllMatch) {
                EmcProjectBean project = new EmcProjectBean();
                project.setId(a);
                project.setStatus(EmcBizConstant.PROJECT_STATUS_REPORTEDIT);
                updateProjList.add(project);
            }
        }
        projectService.getDao().update(updateProjList, "STATUS");
    }

    @Override
    public void batchInputResultRule(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<EmcOrderTaskResultBean> orderTaskResultList = wrapper.parse(EmcOrderTaskResultBean.class);
        EmcOrderTaskResultBean orderTaskResult = orderTaskResultList.get(0);
        String resultRule = wrapper.getFilterValue("resultRule");
        String runId = orderTaskResult.getRunId();
        List<Long> ItemIdList = orderTaskResultList.stream().map(a -> a.getItemId()).collect(Collectors.toList());
        // 判断是对样品进行操作还是只是对当前结果行进行操作
        if (orderTaskList.size() > 1) {
            List<Long> orderIdList = orderTaskList.stream().map(a -> a.getOrderId()).collect(Collectors.toList());
            // 选中样品下的orderTaskResult
            List<EmcOrderTaskResultBean> selectOrderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID").stream()
                    .filter(a -> StringUtils.equals(runId, a.getRunId()) && ItemIdList.contains(a.getItemId())).collect(Collectors.toList());
            selectOrderTaskResultList.stream().forEach(a -> a.setResultRule(resultRule));
            orderTaskResultService.getDao().update(selectOrderTaskResultList, "resultRule");
            EmcOrderTaskResultComputingEngine.getEngine(selectOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList())).compute();
        } else {
            orderTaskResultList.stream().forEach(a -> a.setResultRule(resultRule));
            orderTaskResultService.getDao().update(orderTaskResultList, "resultRule");
            EmcOrderTaskResultComputingEngine.getEngine(orderTaskResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList())).compute();
        }
    }

    @Override
    public RestValidationResultBean validateResultStatus(RestJsonWrapperBean wrapper) {
        List<EmcRunBean> runList = wrapper.parse(EmcRunBean.class);
        List<String> runIdList = runList.stream().map(EmcRunBean::getId).collect(Collectors.toList());

        return validateRunResultStatus(runIdList);
    }

    @Override
    public RestValidationResultBean validateProjectResultStatus(RestJsonWrapperBean wrapper) {
        List<EmcProjectBean> projectList = wrapper.parse(EmcProjectBean.class);
        List<Long> projectIdList = projectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());

        List<EmcOrderTaskBean> taskList = orderTaskService.getDao().selectListByOneColumnValues(projectIdList, "projId", Arrays.asList("ID", "RUNID"));
        List<String> runIdList = taskList.stream().filter(task -> !StringUtils.isEmpty(task.getRunId())).map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());

        return validateRunResultStatus(runIdList);
    }

    @Override
    public RestValidationResultBean validateRunResultStatus(List<String> runIdList) {
        // 筛选状态为logged记录
        List<String> runIdNotEmptyList = runIdList.stream().filter(r -> !StringUtils.isEmpty(r)).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(runIdNotEmptyList, "RUNID").stream().filter(e -> "logged".equals(e.getStatus())).collect(Collectors.toList());
        if (orderTaskList.isEmpty()) {
            return new RestValidationResultBean(true);
        }

        List<Long> orderTaskIdList = orderTaskList.stream().map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID", Arrays.asList("ID", "orderNo", "reportResult", "resultStatus", "qcCategoryCode", "lowLimit"));

        if (orderTaskResultList.isEmpty()) {
            return new RestValidationResultBean(true);
        }

        List<EmcOrderTaskResultBean> oosResultList = orderTaskResultList.stream().filter(result -> StringUtils.equals("OOS", result.getResultStatus())).collect(Collectors.toList());

        List<String> blankQcList = Arrays.asList(PfConstant.QCCATEGORY_QCCATEGORYID_P_BLANK,
                PfConstant.QCCATEGORY_QCCATEGORYID_L_BLANK, PfConstant.QC_P_YSKB, PfConstant.QC_P_ZCSBKB);
        List<EmcOrderTaskResultBean> nonNdResultList = orderTaskResultList.stream().filter(r -> blankQcList.contains(r.getQcCategoryCode()) && !StringUtils.isEmpty(r.getLowLimit()))
                .filter(r -> !StringUtils.equals(r.getReportResult(), PfConstant.ND)).collect(Collectors.toList());

        if (oosResultList.isEmpty() && nonNdResultList.isEmpty()) {
            return new RestValidationResultBean(true);
        }

        List<String> messages = new ArrayList<>();

        // 样品结果超标
        if (!oosResultList.isEmpty()) {
            String orderNos = oosResultList.stream().map(EmcOrderTaskResultBean::getOrderNo).distinct().collect(Collectors.joining("、"));
            if (oosResultList.get(0).getQcCategoryCode().contains("DUP")) {
                messages.add(I18nHelper.getMessage("EMC.MODULE.BP.RESULT.RUNS.TIP.DUP_RESULT_OOS", orderNos));
            } else {
                messages.add(I18nHelper.getMessage("EMC.MODULE.BP.RESULT.RUNS.TIP.RESULT_OOS", orderNos));
            }
        }

        // 空白样品结果不为 ND
        if (!nonNdResultList.isEmpty()) {
            String orderNos = nonNdResultList.stream().map(EmcOrderTaskResultBean::getOrderNo).distinct().collect(Collectors.joining("、"));
            messages.add(I18nHelper.getMessage("EMC.MODULE.BP.RESULT.RUNS.TIP.RESULT_NON_ND", orderNos));
        }

        messages.add(I18nHelper.getMessage("EMC.MODULE.BP.RESULT.RUNS.TIP.CONTINUE_SUBMIT"));

        return new RestValidationResultBean(false, String.join("，", messages));
    }

    @Override
    @Transactional
    public void linkWeightingRun(RestJsonWrapperBean wrapper) {
        List<EmcRunBean> targetRunList = wrapper.parse(EmcRunBean.class, 0);
        List<EmcRunBean> relatedRunList = wrapper.parse(EmcRunBean.class, 1);

        List<String> targetRunIdList = targetRunList.stream().map(EmcRunBean::getId).collect(Collectors.toList());
        List<String> relatedRunIdList = relatedRunList.stream().map(EmcRunBean::getId).collect(Collectors.toList());

        List<String> runIdList = new ArrayList<>(targetRunIdList);
        runIdList.addAll(relatedRunIdList);

        // 更新关联的批号
        String relatedRunIds = String.join(",", runIdList);
        targetRunList.forEach(run -> run.setExt$Item("weighingRelatedRunId", relatedRunIds));
        relatedRunList.forEach(run -> run.setExt$Item("weighingRelatedRunId", relatedRunIds));

        List<EmcRunBean> updateRunList = new ArrayList<>(targetRunList);
        updateRunList.addAll(relatedRunList);
        getDao().update(updateRunList, "weighingRelatedRunId");

        // 查询所有批次下的样品、测试和结果
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(runIdList, "runId", Arrays.asList("id", "orderId", "runId"));
        List<Long> orderIdList = orderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(orderIdList, "id", Arrays.asList("id", "orderNo", "parentOrderNo", "qcCategoryCode"));

        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.selectListByFilter(SearchFilter.instance().match("runId", runIdList).filter(MatchPattern.OR).match("analysisType", EmcBizConstant.ANALYSISCATEGORY_ANALYSIS).filter(MatchPattern.SEQ));

        // 筛选出无父样并且样品编号中包含-的质控，不同批次之间进行关联
        List<EmcOrderBean> noParentOrderList = orderList.stream().filter(order -> StringUtils.isEmpty(order.getParentOrderNo())
                && !StringUtils.equals(order.getQcCategoryCode(), EmcConstant.N_A)
                && StringUtils.contains(order.getOrderNo(), "-")).collect(Collectors.toList());

        List<Long> noParentOrderIdList = noParentOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> noParentOrderTaskList = orderTaskList.stream().filter(task -> noParentOrderIdList.contains(task.getOrderId())).collect(Collectors.toList());

        if (!noParentOrderList.isEmpty()) {
            List<EmcOrderTaskBean> targetTaskList = noParentOrderTaskList.stream().filter(task -> targetRunIdList.contains(task.getRunId())).collect(Collectors.toList());
            List<EmcOrderTaskBean> relatedTaskList = noParentOrderTaskList.stream().filter(task -> relatedRunIdList.contains(task.getRunId())).collect(Collectors.toList());

            // 过滤出被关联批下的使用类型为称重，并且原始值不为空的结果，需要把值复制到关联的批上
            List<EmcOrderTaskResultBean> targetWeighingResultList = orderTaskResultList.stream().filter(result -> targetRunIdList.contains(result.getRunId())
                    && StringUtils.equals(result.getExt$Item("useType"), "weighing")
                    && !StringUtils.isEmpty(result.getOriginalResult())).collect(Collectors.toList());

            // 过滤出关联批下的使用类型为称重，并且原始值为空的结果
            List<EmcOrderTaskResultBean> relatedWeighingResultList = orderTaskResultList.stream().filter(result -> relatedRunIdList.contains(result.getRunId())
                    && StringUtils.equals(result.getExt$Item("useType"), "weighing")
                    && StringUtils.isEmpty(result.getOriginalResult())).collect(Collectors.toList());

            // 判断是否需要进行复制
            boolean copyResult = !targetWeighingResultList.isEmpty() && !relatedWeighingResultList.isEmpty();
            List<EmcOrderTaskResultBean> relatedUpdateResultList = !copyResult ? Collections.emptyList() : new ArrayList<>();

            List<Long> targetOrderIdList = targetTaskList.stream().map(EmcOrderTaskBean::getOrderId).collect(Collectors.toList());
            noParentOrderList.sort(Comparator.comparing(EmcOrderBean::getOrderNo));
            Map<String, List<EmcOrderBean>> qcOrderMap = noParentOrderList.stream().collect(Collectors.groupingBy(EmcOrderBean::getQcCategoryCode, LinkedHashMap::new, Collectors.toList()));

            qcOrderMap.forEach((qcCode, curOrderList) -> {
                // 按样品编号后缀进行分组，比如 MB、MB1、STD0、STD1这类的
                Map<String, List<EmcOrderBean>> qcOrderNoSuffixMap = curOrderList.stream().collect(Collectors.groupingBy(order -> order.getOrderNo().substring(order.getOrderNo().lastIndexOf("-") + 1)));
                qcOrderNoSuffixMap.forEach((suffix, suffixOrderList) -> {
                    // 如果同一个后缀编号的存在两个及以上的样品，则表示可以进行自动关联，否则不进行关联
                    if (suffixOrderList.size() <= 1) {
                        return;
                    }

                    EmcOrderBean targetOrder = suffixOrderList.stream().filter(order -> targetOrderIdList.contains(order.getId())).findFirst().orElse(null);
                    if (targetOrder == null) {
                        return;
                    }

                    // 更新关联的质控样品编号和样品测试上的仪器采集编号
                    List<EmcOrderBean> curRelatedOrderList = suffixOrderList.stream().filter(order -> !targetOrderIdList.contains(order.getId())).collect(Collectors.toList());
                    curRelatedOrderList.forEach(relatedOrder -> relatedOrder.setExt$Item("relatedQcOrderNo", targetOrder.getOrderNo()));

                    List<Long> curRelatedOrderIdList = curRelatedOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
                    relatedTaskList.stream().filter(task -> curRelatedOrderIdList.contains(task.getOrderId())).forEach(relatedTask -> relatedTask.setEquiptAcquNo(targetOrder.getOrderNo()));

                    if (!copyResult) {
                        return;
                    }

                    List<EmcOrderTaskResultBean> curTargetResultList = targetWeighingResultList.stream().filter(result -> ObjectUtils.equals(result.getOrderId(), targetOrder.getId())).collect(Collectors.toList());
                    if (curTargetResultList.isEmpty()) {
                        return;
                    }

                    List<EmcOrderTaskResultBean> curRelatedResultList = relatedWeighingResultList.stream().filter(result -> curRelatedOrderIdList.contains(result.getOrderId())).collect(Collectors.toList());

                    // 根据分析项名称进行匹配过滤
                    curTargetResultList.forEach(targetResult -> curRelatedResultList.stream().filter(result -> StringUtils.equals(targetResult.getItemName(), result.getItemName())).forEach(result -> {
                        result.setOriginalResult(targetResult.getOriginalResult());
                        relatedUpdateResultList.add(result);
                    }));
                });
            });

            List<EmcOrderBean> updateOrderList = noParentOrderList.stream().filter(order -> !StringUtils.isEmpty(order.getExt$Item("relatedQcOrderNo"))).collect(Collectors.toList());
            if (!updateOrderList.isEmpty()) {
                orderService.getDao().update(updateOrderList, "relatedQcOrderNo");
                // 关联样品称重批次时，不替换仪器采集编号
                //orderTaskService.getDao().update(relatedTaskList, "equiptAcquNo");
            }

            // 更新关联批上的测试结果，并触发计算公式
            if (!relatedUpdateResultList.isEmpty()) {
                orderTaskResultService.getDao().update(relatedUpdateResultList, "originalResult");
                List<Long> updateResultIdList = relatedUpdateResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
                EmcOrderTaskResultComputingEngine.getEngine(updateResultIdList).compute();
            }
        }
    }

    @Override
    public void unlinkWeightingRun(RestJsonWrapperBean wrapper) {
        List<EmcRunBean> runList = wrapper.parse(EmcRunBean.class);
        // 至少选择两个批解除关联
        if (runList.size() <= 1) {
            return;
        }

        runList.forEach(run -> run.setExt$Item("weighingRelatedRunId", null));
        getDao().update(runList, "weighingRelatedRunId");

        List<String> runIdList = runList.stream().filter(r -> !StringUtils.isEmpty(r.getId())).sorted(Comparator.comparing(EmcRunBean::getId)).map(EmcRunBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(runIdList, "runId", Arrays.asList("id", "orderId", "runId"));
        List<Long> orderIdList = orderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(orderIdList, "id", Arrays.asList("id", "orderNo", "parentOrderNo", "qcCategoryCode"));

        // 筛选出无父样的质控，不同批次之间进行关联
        List<EmcOrderBean> noParentOrderList = orderList.stream().filter(order -> StringUtils.isEmpty(order.getParentOrderNo())
                && !StringUtils.equals(order.getQcCategoryCode(), EmcConstant.N_A)
                && StringUtils.contains(order.getOrderNo(), "-")).collect(Collectors.toList());

        if (!noParentOrderList.isEmpty()) {
            // 解除关联时，删除样品上的关联质控编号，以及修改样品测试上的采集编号
            noParentOrderList.forEach(order -> order.setExt$Item("relatedQcOrderNo", null));
            orderService.getDao().update(noParentOrderList, "relatedQcOrderNo");

            List<Long> noParentOrderIdList = noParentOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
            List<EmcOrderTaskBean> noParentOrderTaskList = orderTaskList.stream().filter(task -> noParentOrderIdList.contains(task.getOrderId())).collect(Collectors.toList());

            noParentOrderList.forEach(order -> noParentOrderTaskList.stream().filter(task -> ObjectUtils.equals(task.getOrderId(), order.getId())).forEach(task -> task.setEquiptAcquNo(order.getOrderNo())));
            orderTaskService.getDao().update(noParentOrderTaskList, "equiptAcquNo");
        }
    }

    @Override
    public void linkRun(RestJsonWrapperBean wrapper) {
        List<EmcRunBean> runList = wrapper.parse(EmcRunBean.class);
        // 至少选择两个批进行关联
        if (runList.size() <= 1) {
            return;
        }

        String relatedRunIds = runList.stream().map(EmcRunBean::getId).collect(Collectors.joining(","));

        runList.forEach(run -> run.setRelatedRunId(relatedRunIds));
        getDao().update(runList, "relatedRunId");
    }

    @Override
    public void unlinkRun(RestJsonWrapperBean wrapper) {
        List<EmcRunBean> runList = wrapper.parse(EmcRunBean.class);
        // 至少选择两个批解除关联
        if (runList.size() <= 1) {
            return;
        }

        runList.forEach(run -> run.setRelatedRunId(null));
        getDao().update(runList, "relatedRunId");
    }

    @Override
    @Transactional
    public void combineOrderTask(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> targetTaskList = wrapper.parse(EmcOrderTaskBean.class, 0);
        List<EmcOrderTaskBean> selectedTaskList = wrapper.parse(EmcOrderTaskBean.class, 1);

        List<Long> targetOrderIdList = targetTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<String> targetRunIdList = targetTaskList.stream().filter(t -> !StringUtils.isEmpty(t.getRunId())).map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());

        List<EmcOrderBean> targetOrderList = orderService.getDao().selectListByOneColumnValues(targetOrderIdList, "id", Arrays.asList("id", "orderNo", "parentOrderNo"));
        Map<Long, String> targetOrderIdNoMap = targetOrderList.stream().collect(Collectors.toMap(EmcOrderBean::getId, EmcOrderBean::getOrderNo));
        Map<Long, String> targetParentOrderIdNoMap = targetOrderList.stream().filter(order -> !StringUtils.isEmpty(order.getParentOrderNo())).collect(Collectors.toMap(EmcOrderBean::getId, EmcOrderBean::getParentOrderNo));

        List<EmcOrderTaskBean> originalOrderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("orderId", targetOrderIdList).filter(MatchPattern.OR)
                .match("runId", targetRunIdList).filter(MatchPattern.OR));
        List<Long> originalOrderTaskIdList = originalOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> originalResultList = orderTaskResultService.getDao().selectListByOneColumnValues(originalOrderTaskIdList, "orderTaskId", Arrays.asList("id", "orderTaskId", "itemId", "itemName", "originalResult", "reportResult"));

        List<Long> selectedTaskIdList = selectedTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> selectedResultList = orderTaskResultService.getDao().selectListByOneColumnValues(selectedTaskIdList, "orderTaskId");

        List<Long> selectedItemIdList = selectedResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
        List<EmcItemBean> selectedItemList = itemService.getDao().selectListByOneColumnValues(selectedItemIdList, "id", Arrays.asList("id", "defaultResult", "useType", "qcCategoryId"));

        List<EmcOrderTaskBean> virtualTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> virtualResultList = new ArrayList<>();
        List<EmcOrderTaskBean> updateTaskList = new ArrayList<>();

        List<Long> defaultValueResultIdList = new ArrayList<>();

        // 如果同时存在正常样品和平行、加标这种子样时，需要先合并正常样品，再合并子样，这样才能更新子样的parentId
        targetTaskList.forEach(task -> task.setExt$Item("combineSortNum", ObjectUtils.isEmpty(task.getQcCategoryId()) ? "0" : String.valueOf(task.getQcCategoryId())));
        targetTaskList.sort(Comparator.comparing(task -> task.getExt$Item("combineSortNum")));
        Map<String, Long> testTaskIdMap = new HashMap<>();

        targetTaskList.forEach(task -> {
            List<Long> curOriginalTaskTestIdList = originalOrderTaskList.stream().filter(originalTask -> StringUtils.equals(task.getRunId(), originalTask.getRunId())
                    && ObjectUtils.equals(task.getOrderId(), originalTask.getOrderId())).map(EmcOrderTaskBean::getTestId).collect(Collectors.toList());

            // 根据测试ID判断样品测试是否重复
            List<EmcOrderTaskBean> diffTaskList = selectedTaskList.stream().filter(selectedTask -> !curOriginalTaskTestIdList.contains(selectedTask.getTestId())).collect(Collectors.toList());
            if (diffTaskList.isEmpty()) {
                return;
            }

            updateTaskList.add(task);

            List<EmcOrderTaskResultBean> curOriginalResultList = originalResultList.stream().filter(result -> ObjectUtils.equals(result.getOrderTaskId(), task.getId())).collect(Collectors.toList());

            diffTaskList.forEach(selectedTask -> {
                EmcOrderTaskBean virtualTask = new EmcOrderTaskBean();
                BeanUtils.copyProperties(selectedTask, virtualTask);
                virtualTask.setId(ApplicationContextHelper.getNextIdentity());
                virtualTask.setOrderId(task.getOrderId());
                virtualTask.setProjId(task.getProjId());
                virtualTask.setFolderId(task.getFolderId());
                virtualTask.setQcCategoryId(task.getQcCategoryId());
                virtualTask.setQcCategoryCode(task.getQcCategoryCode());
                virtualTask.setQcName(task.getQcName());
                virtualTask.setExt$Item("virtual", Constant.YES);
                virtualTask.setRunId(task.getRunId());
                virtualTask.setCupNo(task.getCupNo());
                virtualTask.setEquiptAcquNo(task.getEquiptAcquNo());

                boolean isNa = EmcConstant.N_A.equals(virtualTask.getQcCategoryCode());
                if (isNa) {
                    String key = targetOrderIdNoMap.get(task.getOrderId()) + "$" + selectedTask.getTestId();
                    testTaskIdMap.put(key, virtualTask.getId());
                }

                // 实验室平行和样品加标
                boolean hasParent = !ObjectUtils.isEmpty(targetParentOrderIdNoMap.get(task.getOrderId()));
                if (hasParent) {
                    String key = targetParentOrderIdNoMap.get(task.getOrderId()) + "$" + selectedTask.getTestId();
                    virtualTask.setParentId(testTaskIdMap.get(key));
                }
                virtualTaskList.add(virtualTask);

                // 在QC中登录的分析项
                List<EmcItemBean> curValidItemList = isNa ? selectedItemList : selectedItemList.stream().filter(item -> StringUtils.contains(item.getQcCategoryId(), String.valueOf(virtualTask.getQcCategoryId()))).collect(Collectors.toList());
                Map<Long, EmcItemBean> curValidItemMap = curValidItemList.stream().collect(Collectors.toMap(EmcItemBean::getId, Function.identity()));
                List<Long> curValidItemIdList = curValidItemList.stream().map(EmcItemBean::getId).collect(Collectors.toList());

                selectedResultList.stream().filter(result -> ObjectUtils.equals(selectedTask.getId(), result.getOrderTaskId()) && curValidItemIdList.contains(result.getItemId())).forEach(result -> {
                    EmcOrderTaskResultBean virtualResult = new EmcOrderTaskResultBean();
                    BeanUtils.copyProperties(result, virtualResult);
                    virtualResult.setId(ApplicationContextHelper.getNextIdentity());
                    virtualResult.setOriginalResult(null);
                    virtualResult.setReportResult(null);
                    virtualResult.setAvgResult(null);

                    // 复制分析项默认值
                    EmcItemBean curItem = Optional.ofNullable(curValidItemMap.get(result.getItemId())).orElseGet(EmcItemBean::new);
                    if (!StringUtils.isEmpty(curItem.getDefaultResult())) {
                        virtualResult.setOriginalResult(curItem.getDefaultResult());
                        defaultValueResultIdList.add(virtualResult.getId());
                    }

                    // 复制称重类型分析项的值
                    if ("weighing".equals(curItem.getExt$Item("useType"))) {
                        curOriginalResultList.stream().filter(originalResult -> StringUtils.equals(virtualResult.getItemName(), originalResult.getItemName())
                                && !StringUtils.isEmpty(originalResult.getOriginalResult())).findFirst().ifPresent(originalResult -> {
                            virtualResult.setOriginalResult(originalResult.getOriginalResult());
                            virtualResult.setReportResult(originalResult.getReportResult());
                        });
                    }

                    virtualResult.setOrderId(task.getOrderId());
                    virtualResult.setOrderNo(targetOrderIdNoMap.get(task.getOrderId()));
                    virtualResult.setOrderTaskId(virtualTask.getId());
                    virtualResult.setProjId(task.getProjId());
                    virtualResult.setFolderId(task.getFolderId());
                    virtualResult.setRunId(task.getRunId());
                    virtualResult.setQcCategoryId(task.getQcCategoryId());
                    virtualResult.setQcCategoryCode(task.getQcCategoryCode());
                    virtualResult.setQcName(task.getQcName());
                    virtualResult.setExt$Item("virtual", Constant.YES);

                    if (hasParent) {
                        virtualResult.setParentOrderNo(targetParentOrderIdNoMap.get(task.getOrderId()));
                    }

                    virtualResultList.add(virtualResult);
                });
            });
        });

        // 更新样品表上的是否为原样字段
        if (!updateTaskList.isEmpty()) {
            List<Long> orderIdList = updateTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
            List<EmcOrderBean> updateOrderList = new ArrayList<>(orderIdList.size());
            orderIdList.forEach(orderId -> {
                EmcOrderBean updateOrder = new EmcOrderBean();
                updateOrder.setId(orderId);
                updateOrder.setExt$Item("originalOrder", Constant.YES);
                updateOrderList.add(updateOrder);
            });

            orderService.getDao().update(updateOrderList, "originalOrder");
        }

        // 判断是否有空白加标质控，空白加标质控需要更新父样ID
        List<EmcOrderTaskBean> blankSpkcTaskList = virtualTaskList.stream().filter(task -> StringUtils.equals(task.getQcCategoryCode(), PfConstant.QC_L_SPKCK)).collect(Collectors.toList());
        if (!blankSpkcTaskList.isEmpty()) {
            String runId = blankSpkcTaskList.get(0).getRunId();
            String blankOrderNo = String.join("-", runId, PfConstant.QC_LBLANK_ORDER_NO_PREFIX);
            List<EmcOrderBean> blankOrderList = orderService.getDao().selectListByOneColumnValue(blankOrderNo, "orderNo", Arrays.asList("id", "orderNo"));
            if (!blankOrderList.isEmpty()) {
                Long blankOrderId = blankOrderList.get(0).getId();
                List<EmcOrderTaskBean> blankTaskList = orderTaskService.getDao().selectListByOneColumnValue(blankOrderId, "orderId", Arrays.asList("id", "testMethodId"));
                blankTaskList.forEach(bt -> virtualTaskList.stream().filter(bst -> StringUtils.equals(bst.getQcCategoryCode(), PfConstant.QC_L_SPKCK) && ObjectUtils.equals(bt.getTestMethodId(), bst.getTestMethodId())).forEach(bst -> bst.setParentId(bt.getId())));
            }
        }

        orderTaskService.getDao().insert(virtualTaskList);
        orderTaskResultService.getDao().insert(virtualResultList);

        // 触发有默认值的公式计算
        if (!defaultValueResultIdList.isEmpty()) {
            List<Long> resultIdList = virtualResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
            asyncFormulaComputingService.asyncComputing(null, resultIdList, false);
        }
    }

    @Override
    public Page<EmcOrderTaskBean> selectUniqueOrderTask(RestJsonWrapperBean wrapper) {
        return orderTaskService.selectUniqueOrderTaskList(wrapper);
    }

    @Override
    public Page<EmcQcRelatedOrderBean> selectQcRelatedOrder(RestJsonWrapperBean wrapper) {
        return qcRelatedOrderService.selectPagination(wrapper);
    }

    @Override
    public Long deleteQcRelatedOrder(RestJsonWrapperBean wrapper) {
        qcRelatedOrderService.delete(wrapper);
        return 1L;
    }

    @Override
    public void linkOrder(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> originalTaskList = wrapper.parse(EmcOrderTaskBean.class, 0);
        List<EmcOrderBean> relatedOrderList = wrapper.parse(EmcOrderBean.class, 1);

        String relatedRunId = wrapper.getParamValue("relatedRunId");
        List<String> relatedBeforeRunIdList = Arrays.asList(StringUtils.split(relatedRunId, ","));
        List<String> relatedRunIdList = relatedBeforeRunIdList.stream().filter(r -> !StringUtils.isEmpty(r)).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> curOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(relatedRunIdList, "runId", Arrays.asList("id", "orderId", "runId"));
        Map<Long, List<EmcOrderTaskBean>> orderIdTaskMap = curOrderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getOrderId));

        List<Long> targetOrderIdList = originalTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<EmcQcRelatedOrderBean> existedRelatedOrderList = qcRelatedOrderService.getDao().selectListByOneColumnValues(targetOrderIdList, "qcOrderId");
        String originRunId = originalTaskList.get(0).getRunId();
        List<EmcQcRelatedOrderBean> qcRelatedOrderList = new ArrayList<>();
        originalTaskList.forEach(originalTask -> {
            List<Long> existedRelatedOrderIdList = existedRelatedOrderList.stream().filter(order -> ObjectUtils.equals(originalTask.getOrderId(), order.getQcOrderId()))
                    .filter(order -> StringUtils.equals(originRunId, order.getTaskRunId())).map(EmcQcRelatedOrderBean::getRelatedOrderId).collect(Collectors.toList());

            // 排除已经添加的
            relatedOrderList.stream().filter(order -> !existedRelatedOrderIdList.contains(order.getId())).forEach(relatedOrder -> {
                EmcQcRelatedOrderBean qcRelatedOrder = new EmcQcRelatedOrderBean();
                qcRelatedOrder.setId(ApplicationContextHelper.getNextIdentity());
                qcRelatedOrder.setQcOrderId(originalTask.getOrderId());
                qcRelatedOrder.setQcOrderNo(originalTask.getExt$Item("orderNo"));
                qcRelatedOrder.setQcCategoryId(originalTask.getQcCategoryId());
                qcRelatedOrder.setQcCategoryCode(originalTask.getQcCategoryCode());
                qcRelatedOrder.setQcName(originalTask.getQcName());
                qcRelatedOrder.setRelatedOrderId(relatedOrder.getId());
                qcRelatedOrder.setRelatedOrderNo(relatedOrder.getOrderNo());
                qcRelatedOrder.setTaskRunId(originalTask.getRunId());

                Optional.ofNullable(orderIdTaskMap.get(relatedOrder.getId())).ifPresent(taskList -> {
                    // 排除质控样所在的批号
                    String validRelatedRunId = taskList.stream().map(EmcOrderTaskBean::getRunId).distinct().filter(runId -> !StringUtils.equals(originalTask.getRunId(), runId)).collect(Collectors.joining(","));
                    qcRelatedOrder.setRelatedRunId(validRelatedRunId);
                });

                qcRelatedOrderList.add(qcRelatedOrder);
            });
        });

        qcRelatedOrderService.getDao().insert(qcRelatedOrderList);
    }

    @Override
    public void unlinkOrder(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);

        List<EmcQcRelatedOrderBean> deleteRelatedOrderList = new ArrayList<>(orderTaskList.size());
        orderTaskList.forEach(task -> {
            EmcQcRelatedOrderBean relatedOrder = new EmcQcRelatedOrderBean();
            relatedOrder.setQcOrderId(task.getOrderId());
            relatedOrder.setTaskRunId(task.getRunId());

            deleteRelatedOrderList.add(relatedOrder);
        });

        qcRelatedOrderService.getDao().deleteBy(deleteRelatedOrderList, "qcOrderId", "taskRunId");
    }

    @Override
    public Page<EmcEquiptBean> selectAcqEquiptPagination(String runId) {
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValue(runId, "RUNID");
        List<Long> equipids = orderTaskList.stream().map(e -> e.getEquiptId()).distinct().collect(Collectors.toList());
        List<EmcEquiptBean> EquiptList = equiptService.getDao().selectListByOneColumnValues(equipids, "ID");
        return new Pagination<>(EquiptList);
    }

    @Override
    public Page<EmcEquiptBean> selectEquiptPagination(String testMethodId, RestJsonWrapperBean wrapper) {
        List<String> testMethodIdList = Stream.of(StringUtils.split(testMethodId, ","))
                .filter(NumberUtils::isNumber).distinct().collect(Collectors.toList());
        HashMap<String, String> columnTypeMap = new HashMap<>();
        Arrays.asList("equiptid", "equiptname", "specification", "factoryno", "status", "equiptcategoryid",
                "useddepartmentid", "sharing", "maintain", "sourcemethod", "sourcedate")
                .forEach(column -> columnTypeMap.put(column, "string"));
        if (testMethodIdList.isEmpty()) {
            Pagination<EmcEquiptBean> pagination = new Pagination<>();
            pagination.setColumnTypeMap(columnTypeMap);
            return pagination;
        }
        List<EmcEquiptTestMethodBean> methodEquiptList = equiptTestMethodService.selectListByFilter(
                SearchFilter.instance().match("testMethodId", testMethodIdList).filter(MatchPattern.OR)
                        .match("analysisCategory", "analysis").filter(MatchPattern.SEQ)
                        .match("orgId", LocalContextHelper.getLoginUser().getExt$Item("parentOrgId")).filter(MatchPattern.SEQ));
        if (methodEquiptList.isEmpty()) {
            EmcEquiptBean filter = new EmcEquiptBean();
            filter.setEquiptName("N/A");
            EmcEquiptBean equipt = equiptService.getDao().selectFirstIfPresent(filter);
            Pagination<EmcEquiptBean> pagination = new Pagination<>(Collections.singletonList(equipt == null ? filter : equipt));
            pagination.setColumnTypeMap(columnTypeMap);
            return pagination;
        }
        Map<Long, List<EmcEquiptTestMethodBean>> grouped = methodEquiptList.stream()
                .collect(Collectors.groupingBy(EmcEquiptTestMethodBean::getEquiptId));
        List<Long> equiptIdList = grouped.entrySet().stream()
                .filter(entry -> entry.getValue().size() == testMethodIdList.size())
                .map(Map.Entry::getKey).collect(Collectors.toList());
        List<EmcEquiptBean> equiptList = equiptService.selectListByFilter(
                SearchFilter.instance().match("ID", equiptIdList).filter(MatchPattern.OR), wrapper);
        equiptList.forEach(equipt -> methodEquiptList.stream()
                .filter(method -> method.getEquiptId().equals(equipt.getId()))
                .forEach(method -> equipt.setExt$Item("defaultflag", method.getDefaultFlag())));
        List<EmcEquiptBean> validList = equiptList.stream()
                .filter(equipt -> equipt.getMaintenanceUserId() == null
                        || ObjectUtils.equals("", equipt.getMaintenanceUserId())
                        || equipt.getMaintenanceUserId().contains(LocalContextHelper.getLoginUserId()))
                .collect(Collectors.toList());
        Pagination<EmcEquiptBean> pagination = new Pagination<>(validList);
        pagination.setColumnTypeMap(columnTypeMap);
        return pagination;
    }

    public void equiptReCollect(String id, RestJsonWrapperBean wrapper) {
        String targetId = "T_EMC_EQUIPT$" + id;
        List<CoreFileBean> fileList = coreFileService.getDao().selectListByOneColumnValue(targetId, "targetId");
        String equiptNo = wrapper.getParamValue("equiptid");
        if (equiptNo == null) {
            equiptNo = equiptService.selectById(selectById(id).getEquiptId()).getEquiptId();
        }
        String equiptDirString = ApplicationContextHelper.getEnvironment().getProperty("sunway.equipt.acq.file.local.path");
        for (CoreFileBean file : fileList) {
            File inputFile = FilePathManager.getLocalPath(FilePathDTO.of(file.getScope(),
                    DateTimeUtils.getDateSerialNumber(file.getCreatedTime()), file.getMd5Name(), file.getName())).toFile();
            try (FileInputStream fis = new FileInputStream(inputFile)) {
                File newFile = new File(equiptDirString + File.separator + equiptNo + File.separator + id + File.separator + file.getName());
                if (!newFile.getParentFile().exists()) newFile.getParentFile().mkdirs();
                if (newFile.exists()) newFile.delete();
                FileUtils.write(newFile, fis);
            } catch (Exception e) {
                throw new FileException(e);
            }
        }
    }

    @Override
    public void equiptCollect(String id, RestJsonWrapperBean wrapper) {
        if ("repeate".equals(wrapper.getParamValue("type"))) {
            equiptReCollect(id, wrapper);
            return;
        }
        List<CoreFileBean> fileList = coreFileService.getDao().selectListByIds(wrapper.parse(CoreFileBean.class).stream().map(e -> e.getId()).collect(Collectors.toList()));

        String equiptNo;
        if (wrapper.getParamValue("equiptid") != null) {
            equiptNo = wrapper.getParamValue("equiptid");
        } else {
            equiptNo = equiptService.selectById(selectById(id).getEquiptId()).getEquiptId();
        }
        String equiptDirString = ApplicationContextHelper.getEnvironment().getProperty("sunway.equipt.acq.file.local.path");

        fileList.forEach(file -> {

            File inputFile = FilePathManager.getLocalPath(FilePathDTO.of(file.getScope(), DateTimeUtils.getDateSerialNumber(file.getCreatedTime()), file.getMd5Name(), file.getName())).toFile();

            try {

                FileInputStream fis = new FileInputStream(inputFile);

                // 文件目录：仪器文件夹 + 仪器编号 + 批号 + 文件名
                String fileName = equiptDirString + File.separator + equiptNo + File.separator + id + File.separator + file.getName();

                File newFile = new File(fileName);
                if (!newFile.getParentFile().exists()) {
                    newFile.getParentFile().mkdirs();
                }
                if (newFile.exists()) {
                    newFile.delete();
                }

                FileUtils.write(newFile, fis);

            } catch (Exception ioe) {
                throw new FileException(ioe);
            }
        });
    }

    @Override
    public Page<EmcEquiptBean> selectEditEquiptPagination(String testMethodId, String equiptId) {
        String[] array = StringUtils.split(testMethodId, ",");
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        if (array.length > 0) {
            List<Long> testMethodIdList = Arrays.stream(array).map(NumberUtils::parseLong).collect(Collectors.toList());

            List<EmcEquiptTestMethodBean> testMethodEquiptList = equiptTestMethodService.getDao().selectListByOneColumnValues(testMethodIdList, "TESTMETHODID").stream()
                    .filter(a -> "analysis".equals(a.getAnalysisCategory())).filter(a -> StringUtils.equals(parentOrgId, a.getOrgId())).collect(Collectors.toList());

            // 根据equiptId分组，如果list元素和testMethodId的数量一致，说明该测试下都维护了该仪器。
            Map<Long, List<EmcEquiptTestMethodBean>> testMethodEquiptMap = testMethodEquiptList.stream().collect(Collectors.groupingBy(EmcEquiptTestMethodBean::getEquiptId));

            List<Long> equiptIdList = testMethodEquiptMap.entrySet().stream().filter(e -> e.getValue().size() == array.length).map(Map.Entry::getKey).collect(Collectors.toList());

            List<EmcEquiptBean> equiptList = equiptService.getDao().selectListByIds(equiptIdList);

            // 只能看到当前账号有权限的仪器
            List<EmcEquiptBean> equiptOfMaintenanceUserList = equiptList.stream().filter(e -> e.getMaintenanceUserId() == null || ObjectUtils.equals("", e.getMaintenanceUserId()) || e.getMaintenanceUserId().contains(LocalContextHelper.getLoginUserId())).collect(Collectors.toList());

            return new Pagination<>(equiptOfMaintenanceUserList);
        }

        return new Pagination<>();
    }

    /**
     * 获取特殊规则样品编号序列号
     *
     * @param qcCategoryCodeList 待添加的QC
     * @param specialQcCodeList  特殊编号规则QC
     * @param specialQcSuffixMap 特殊编号规则后缀
     * @param orderList          待添加的样品
     * @param folderId           点位ID
     * @return
     */
    private Map<String, Integer> getSpecialSuffixOrderSeqNo(List<String> qcCategoryCodeList, List<String> specialQcCodeList, Map<String, String> specialQcSuffixMap, List<EmcOrderBean> orderList, Long folderId) {
        Map<String, Integer> orderSeqNoMap = new HashMap<>();

        List<String> curSpecialCodeList = qcCategoryCodeList.stream().filter(specialQcCodeList::contains).collect(Collectors.toList());
        if (!curSpecialCodeList.isEmpty()) {
            List<EmcOrderBean> filterOrderList = orderService.selectListByFilter(SearchFilter.instance().match("folderId", folderId).filter(MatchPattern.EQ)
                    .match("qcCategoryCode", specialQcCodeList).filter(MatchPattern.OR));

            orderList.forEach(order -> curSpecialCodeList.forEach(qcCategoryCode -> orderSeqNoMap.put(qcCategoryCode + order.getOrderNo(),
                    getMaxOrderSeqNo(filterOrderList, order, qcCategoryCode, specialQcSuffixMap.get(qcCategoryCode)))));
        }

        return orderSeqNoMap;
    }

    private int getMaxOrderSeqNo(List<EmcOrderBean> orderList, EmcOrderBean specialOrder, String qcCategoryCode, String suffix) {
        EmcOrderBean maxOrder = orderList.stream().filter(order -> StringUtils.equals(order.getQcCategoryCode(), qcCategoryCode)
                && StringUtils.contains(order.getOrderNo(), specialOrder.getOrderNo())
                && StringUtils.contains(order.getOrderNo(), suffix)).max(Comparator.comparing(EmcOrderBean::getOrderNo)).orElseGet(EmcOrderBean::new);

        if (!StringUtils.isEmpty(maxOrder.getOrderNo())) {
            String maxOrderNo = maxOrder.getOrderNo();
            String orderNoSuffix = maxOrderNo.substring(maxOrderNo.lastIndexOf(suffix) + suffix.length());
            int suffixValue = StringUtils.isEmpty(orderNoSuffix) ? 0 : NumberUtils.parseInt(orderNoSuffix);

            return suffixValue + 1;
        }

        return 0;
    }

    private Map<String, Integer> getSpecialPrefixOrderSeqNo(List<EmcQcCategoryBean> qcCategoryList, List<String> specialQcCodeList, Map<String, String> specialQcSuffixMap, List<EmcOrderBean> orderList, Long folderId) {
        Map<String, Integer> orderSeqNoMap = new HashMap<>();

        List<EmcQcCategoryBean> curSpecialQcList = qcCategoryList.stream().filter(qc -> specialQcCodeList.contains(qc.getQcCategoryId())).collect(Collectors.toList());
        if (!curSpecialQcList.isEmpty()) {
            List<EmcOrderBean> filterOrderList = orderService.selectListByFilter(SearchFilter.instance().match("folderId", folderId).filter(MatchPattern.EQ)
                    .match("qcCategoryCode", specialQcCodeList).filter(MatchPattern.OR));

            orderList.forEach(order -> curSpecialQcList.forEach(specialQc -> {
                orderSeqNoMap.put(specialQc.getQcCategoryId(), getPrefixMaxOrderSeqNo(filterOrderList, specialQc, specialQcSuffixMap.get(specialQc.getQcCategoryId())));
            }));
        }

        return orderSeqNoMap;
    }

    private int getPrefixMaxOrderSeqNo(List<EmcOrderBean> orderList, EmcQcCategoryBean qcCategory, String suffix) {
        EmcOrderBean maxOrder = orderList.stream().filter(order -> StringUtils.equals(order.getQcCategoryCode(), qcCategory.getQcCategoryId())
                && StringUtils.contains(order.getOrderNo(), suffix + Optional.ofNullable(qcCategory.getExt$Item("checkPoint")).orElse(""))).max(Comparator.comparing(EmcOrderBean::getOrderNo)).orElseGet(EmcOrderBean::new);

        if (!StringUtils.isEmpty(maxOrder.getOrderNo())) {
            String maxOrderNo = maxOrder.getOrderNo();
            if (!Arrays.asList(PfConstant.QC_QXJH, PfConstant.QC_L_QXCHD).contains(qcCategory.getQcCategoryId())) {
                String orderNoSuffix = maxOrderNo.substring(maxOrderNo.lastIndexOf(suffix) + suffix.length());
                int suffixValue = StringUtils.isEmpty(orderNoSuffix) ? 0 : NumberUtils.parseInt(orderNoSuffix);

                return suffixValue + 1;
            }

            // 对于曲线校核，需要特殊处理，曲线校核样品编号引入了中间校核点
            boolean isLowestCurveCheck = PfConstant.QC_L_QXCHD.equals(qcCategory.getQcCategoryId());
            String qcSuffix = suffix + (isLowestCurveCheck ? "0" : Optional.ofNullable(qcCategory.getExt$Item("checkPoint")).orElse("1"));
            String orderNoSuffix = maxOrderNo.substring(maxOrderNo.lastIndexOf(qcSuffix) + qcSuffix.length());
            if (!StringUtils.isEmpty(orderNoSuffix) && orderNoSuffix.startsWith("-")) {
                orderNoSuffix = orderNoSuffix.substring(1);
            }
            int suffixValue = StringUtils.isEmpty(orderNoSuffix) || !NumberUtils.isNumber(orderNoSuffix) ? 0 : NumberUtils.parseInt(orderNoSuffix);

            return suffixValue + 1;
        }

        return 0;
    }

    /**
     * 构建QC关联的样品数据
     *
     * @param orderTaskList
     * @param qcCategory
     * @param originalOrder
     * @param qcOrder
     * @return
     */
    private List<EmcQcRelatedOrderBean> buildQcRelatedOrder(List<EmcOrderTaskBean> orderTaskList, EmcQcCategoryBean qcCategory, EmcOrderBean originalOrder, EmcOrderBean qcOrder, boolean hasParent) {
        List<EmcQcRelatedOrderBean> qcRelatedOrderList = new ArrayList<>();
        orderTaskList.stream().filter(task -> ObjectUtils.equals(task.getOrderId(), originalOrder.getId())).findFirst().ifPresent(task -> {

            String relatedOrderId = task.getExt$Item("relatedOrderId");
            String relatedOrderNo = task.getExt$Item("relatedOrderNo");
            List<Long> relatedOrderIdList = Arrays.stream(StringUtils.split(relatedOrderId, ",")).filter(NumberUtils::isNumber).mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());
            List<String> relatedOrderNoList = Arrays.asList(StringUtils.split(relatedOrderNo, ","));

            if (relatedOrderIdList.isEmpty() || relatedOrderIdList.size() != relatedOrderNoList.size()) {
                return;
            }

            IntStream.range(0, relatedOrderIdList.size()).forEach(index -> {
                EmcQcRelatedOrderBean relatedOrder = new EmcQcRelatedOrderBean();
                relatedOrder.setId(ApplicationContextHelper.getNextIdentity());
                relatedOrder.setQcOrderId(qcOrder.getId());
                relatedOrder.setQcOrderNo(qcOrder.getOrderNo());
                relatedOrder.setQcCategoryId(qcCategory.getId());
                relatedOrder.setQcCategoryCode(qcCategory.getQcCategoryId());
                relatedOrder.setQcName(qcCategory.getQcName());
                relatedOrder.setRelatedOrderId(relatedOrderIdList.get(index));
                relatedOrder.setRelatedOrderNo(relatedOrderNoList.get(index));
                qcRelatedOrderList.add(relatedOrder);
            });
        });

        if (hasParent && qcRelatedOrderList.isEmpty()) {
            EmcQcRelatedOrderBean relatedOrder = new EmcQcRelatedOrderBean();
            relatedOrder.setId(ApplicationContextHelper.getNextIdentity());
            relatedOrder.setQcOrderId(qcOrder.getId());
            relatedOrder.setQcOrderNo(qcOrder.getOrderNo());
            relatedOrder.setQcCategoryId(qcCategory.getId());
            relatedOrder.setQcCategoryCode(qcCategory.getQcCategoryId());
            relatedOrder.setQcName(qcCategory.getQcName());
            relatedOrder.setRelatedOrderId(originalOrder.getId());
            relatedOrder.setRelatedOrderNo(originalOrder.getOrderNo());
            qcRelatedOrderList.add(relatedOrder);
        }

        return qcRelatedOrderList;
    }

    //复制称重
    @Override
    public void copyWeight(RestJsonWrapperBean wrapper) {
        List<EmcRunBean> runList = wrapper.parse(EmcRunBean.class);//复制到的结果集
        String id = wrapper.getFilterValue("runId");
        List<String> runIdList = runList.stream().filter(a -> !StringUtils.isEmpty(a.getId())).map(a -> a.getId()).collect(Collectors.toList());
        //被复制结果集
        List<EmcOrderTaskResultBean> selectOrderTaskResultOrgList = orderTaskResultService.selectListByFilter((SearchFilter.instance().match("RUNID", id).filter(MatchPattern.EQ).match("USETYPE", "weighing").filter(MatchPattern.EQ)));
        //复制到结果集
        List<EmcOrderTaskResultBean> selectOrderTaskResultList = orderTaskResultService.selectListByFilter((SearchFilter.instance().match("RUNID", runIdList).filter(MatchPattern.OR).match("USETYPE", "weighing").filter(MatchPattern.EQ)));
        //循环复制结果
        for (EmcOrderTaskResultBean resultBean : selectOrderTaskResultList) {
            EmcOrderTaskResultBean resultBean1 = getSameResultBean(resultBean, selectOrderTaskResultOrgList);
            if (resultBean1 != null && resultBean1.getOriginalResult() != null) {
                resultBean.setOriginalResult(resultBean1.getOriginalResult());
            }
        }
        orderTaskResultService.getDao().update(selectOrderTaskResultList, "originalResult");
        EmcOrderTaskResultComputingEngine.getEngine(selectOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList())).compute();
    }

    //复制称重功能返回字段相匹配的bean
    private EmcOrderTaskResultBean getSameResultBean(EmcOrderTaskResultBean resultBean, List<EmcOrderTaskResultBean> orglist) {
        if (resultBean.getOrderNo() == null || resultBean.getItemName() == null) {
            return null;
        }
        for (EmcOrderTaskResultBean orgResultBean : orglist) {
            if (orgResultBean.getOrderNo() == null || orgResultBean.getItemName() == null) {
                continue;
            }
            if (((resultBean.getOrderNo().equals(orgResultBean.getOrderNo()))) && resultBean.getItemName().equals(orgResultBean.getItemName())) {
                return orgResultBean;
            }
        }
        return null;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void composeAllPdf(RestJsonWrapperBean wrapper) {
        EmcRunBean targetRun = wrapper.parseUnique(EmcRunBean.class);
        List<String> projectNosList = Arrays.asList(StringUtils.split(targetRun.getProjNo(), ","));

        List<EmcProjectBean> curProjectList = projectService.getDao().selectListByOneColumnValues(projectNosList, "projNo", Arrays.asList("id", "projNo"));

        //获取全部的projectId
        List<Long> projectIdList = curProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());
        //当前批下项目的所有测试数据
        List<EmcOrderTaskBean> runOrderTaskList = orderTaskService.getDao().selectListByOneColumnValue(targetRun.getId(), "runId", Arrays.asList("id", "projId", "cupNo"));

        List<EmcRunBean> runList = new ArrayList<>(projectIdList.size());
        projectIdList.forEach(pid -> {
            List<EmcOrderTaskBean> curTaskList = runOrderTaskList.stream().filter(task -> ObjectUtils.equals(task.getProjId(), pid)).sorted(Comparator.comparing(EmcOrderTaskBean::getCupNo)).collect(Collectors.toList());
            if (curTaskList.isEmpty()) {
                return;
            }

            EmcRunBean newRun = new EmcRunBean();
            EmcProjectBean curProject = curProjectList.stream().filter(p -> ObjectUtils.equals(p.getId(), pid)).findFirst().orElseGet(EmcProjectBean::new);
            BeanUtils.copyProperties(targetRun, newRun);
            newRun.setProjId(pid);
            newRun.setProjNo(curProject.getProjNo());
            newRun.setExt$Item("cupNo", curTaskList.get(0).getCupNo() + "");
            runList.add(newRun);
        });
        runList.sort(Comparator.comparing(r -> NumberUtils.parseLong(r.getExt$Item("cupNo"))));
        composeAllRunReportPdf(runList);

        // 按批生成PDF文件
        // 把上一步生成的文件合并成一个PDF文件
        List<CoreFileBean> runPdfFileList = coreFileService.selectListByFilter(SearchFilter.instance().match("targetId", "T_EMC_RUN$" + targetRun.getId()).filter(MatchPattern.SEQ).match("ext", "composePdf").filter(MatchPattern.SEQ));
        if (runPdfFileList.size() > projectNosList.size()) {
            return;
        }

        List<FilePathDTO> fileRepresentations = runPdfFileList.stream().map(CoreFileUtils::toFilePath).collect(Collectors.toList());
        String projectReportPdfName = targetRun.getId() + ".pdf";
        FilePathDTO composedPdfFile = FilePathDTO.of(FileScope.temp.name(), "" + System.currentTimeMillis(), StringUtils.randomUUID(8), projectReportPdfName);
        coreFileService.mergePdfList(fileRepresentations, composedPdfFile);
        String tempFilePath = FilePathManager.getLocalPath(composedPdfFile).toString();
        List<CoreFileBean> deleteFileList = new ArrayList<>();
        CoreFileBean deleteFile = new CoreFileBean();
        deleteFile.setTargetId("T_EMC_RUN" + "$" + targetRun.getId());
        deleteFile.setExt("composeAllPdf");
        deleteFileList.add(deleteFile);
        coreFileService.getDao().deleteBy(deleteFileList, "TARGETID", "EXT");

        // 保存文件信息到 t_core_file 表
        if (!StringUtils.isEmpty(tempFilePath)) {
            CoreFileBean coreFileBean = new CoreFileBean();
            coreFileBean.setTargetId("T_EMC_RUN" + "$" + targetRun.getId());
            coreFileBean.setScope(FileScope.secure.name());
            coreFileBean.setExt("composeAllPdf");
            coreFileService.upload(coreFileBean, new File(tempFilePath));
        }

        EmcRunBean updateRun = new EmcRunBean();
        updateRun.setId(targetRun.getId());
        updateRun.setExt$Item("COMPOSEFLAG", "0");
        getDao().update(updateRun, "COMPOSEFLAG");//合并完原始记录，并把状态置为0
    }

    /**
     * 测试批原始记录生成 PDF 报表文件
     *
     * @param runBeanList
     */
    public void composeAllRunReportPdf(List<EmcRunBean> runBeanList) {
        // 通过系统参数COMPOSE_PDF来配置是否需要合成PDF
        CoreConstantBean composePdfConstant = coreConstantService.selectByIdIfPresent("COMPOSE_PDF");
        if (composePdfConstant == null || !Constant.YES.equals(composePdfConstant.getConstantValue())) {
            return;
        }

        // 查询测试批对应的报表模板
        List<CoreReportConfigLineBean> configLineBeanList = coreReportConfigLineService
                .selectListByIds(runBeanList.stream().filter(r -> r.getReportConfigLineId() != null).map(EmcRunBean::getReportConfigLineId).collect(Collectors.toList()));
        if (configLineBeanList.isEmpty()) {
            return;
        }
        List<Long> projIdList = runBeanList.stream().map(EmcRunBean::getProjId).collect(Collectors.toList());
        List<EmcProjectBean> projectList = projectService.getDao().selectListByIds(projIdList, Arrays.asList("id", "projNo"));
        // 生成的PDF默认名称
        String defaultPdfFileName = I18nHelper.getMessage("T_EMC_RUN.DOWNLOAD.REPORT_PDF_NAME");

        // Reporting Service 报表模板
        List<CoreReportConfigLineBean> rsConfigLineList = configLineBeanList.stream().filter(r -> "reportingService".equals(r.getReportCategory())).collect(Collectors.toList());
        if (!rsConfigLineList.isEmpty()) {
            // 获取配置的Reporting Service 服务器地址
            String reportingServiceServer = ApplicationContextHelper.getConstantValue("REPORTING_SERVICE_SERVER_URL");
            if (StringUtils.isEmpty(reportingServiceServer)) {
                return;
            }

            Map<String, CoreFileBean> pdfReportMap = new LinkedHashMap<>();
            runBeanList.stream().forEach(r -> {
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
                            logger.info(reportUrl.toString());
                            CoreFileBean coreFileBean = new CoreFileBean();
                            coreFileBean.setTargetId("T_EMC_RUN" + "$" + r.getId() + "-" + r.getProjId());
                            coreFileBean.setScope(FileScope.secure.name());
                            coreFileBean.setExt("composePdf");
                            coreFileBean.setExt$Item("PROJNO", r.getId() + "_" + r.getProjNo());
                            pdfReportMap.put(reportUrl.toString(), coreFileBean);
                        } catch (UnsupportedEncodingException e) {
                            throw new UnsupportedException();
                        }
                    }
                }
            });
            coreFileService.getDao().deleteBy(new ArrayList<>(pdfReportMap.values()), "TARGETID", "EXT");
            pdfReportMap.entrySet().stream().forEach(e -> {
                if (!projectList.isEmpty()) {
                    /*					List<EmcProjectBean> needProjList = projectList.stream().filter(p ->p.getId() == Long.parseLong(e.getKey().substring(e.getKey().lastIndexOf("=")+1))).collect(Collectors.toList());
                     */
                    logger.info(e.getKey());
                    //修改为每个批项目
                    File reportPdfFile = downloadFile(e.getKey(), e.getValue().getExt$Item("PROJNO") + ".pdf");
                    if (reportPdfFile != null) {
                        coreFileService.upload(e.getValue(), reportPdfFile);
                    }
                }

            });
        }

        // 帆软报表模板
        List<CoreReportConfigLineBean> frConfigLineBeanList = configLineBeanList.stream().filter(r -> "fanruan".equals(r.getReportCategory())).collect(Collectors.toList());
        if (!frConfigLineBeanList.isEmpty()) {
            List<String> configReportLineIdList = frConfigLineBeanList.stream().map(r -> coreReportConfigLineService.getDao().getTable() + "$" + r.getId()).collect(Collectors.toList());
            List<CoreFileBean> coreFileBeanList = coreFileService.getDao().selectListByOneColumnValues(configReportLineIdList, "TARGETID");

            Map<String, CoreFileBean> pdfReportMap = new LinkedHashMap<>();
            runBeanList.stream().forEach(r -> {
                Optional<CoreFileBean> coreFileBeanOptional = coreFileBeanList.stream()
                        .filter(l -> ObjectUtils.equals(coreReportConfigLineService.getDao().getTable() + "$" + r.getReportConfigLineId(), l.getTargetId())).findFirst();
                Optional<CoreReportConfigLineBean> configLineBeanOptional = frConfigLineBeanList.stream().filter(m -> ObjectUtils.equals(m.getId(), r.getReportConfigLineId())).findFirst();
                if (coreFileBeanOptional.isPresent() && configLineBeanOptional.isPresent()) {
                    CoreFileBean coreFileBean = coreFileBeanOptional.get();
                    String targetId = coreFileBean.getTargetId();
                    String fileName = configLineBeanOptional.get().getReportConfigLineNo() + "/" + coreFileBean.getName();
                    if (!StringUtils.isEmpty(fileName)) {
                        StringBuilder reportUrl = new StringBuilder();
                        reportUrl.append(ApplicationContextHelper.getServerAddress()).append("/ReportServer?reportlet=").append(fileName).append("&format=pdf");
                        String paramStr = parseReportParam(r, configLineBeanOptional.get().getParam());
                        if (!StringUtils.isEmpty(paramStr)) {
                            reportUrl.append("&").append(paramStr);
                        }
                        logger.info(reportUrl.toString());
                        coreFileBean = new CoreFileBean();
                        coreFileBean.setTargetId("T_EMC_RUN" + "$" + r.getId() + "-" + r.getProjId());
                        coreFileBean.setScope(FileScope.secure.name());
                        coreFileBean.setExt("composePdf");
                        pdfReportMap.put(reportUrl.toString(), coreFileBean);
                    }
                }
            });
            coreFileService.getDao().deleteBy(new ArrayList<>(pdfReportMap.values()), "TARGETID", "EXT");
            pdfReportMap.entrySet().stream().forEach(e -> {
                File reportPdfFile = downloadFile(e.getKey(), defaultPdfFileName);
                if (reportPdfFile != null) {
                    coreFileService.upload(e.getValue(), reportPdfFile);
                }
            });
        }
    }

    /**
     * 解析报表参数，忽略参数名大小写
     */
    private String parseReportParam(EmcRunBean run, String param) {
        if (StringUtils.isEmpty(param)) {
            return "";
        }

        String[] values = StringUtils.split(param, "&");

        StringBuilder sb = new StringBuilder();

        Arrays.stream(values).forEach(expr -> {
            String[] pair = StringUtils.split(expr, "=");

            if (pair.length > 1) {
                Object value = BeanUtils.getPropertyValue(run, pair[1]);

                if (value != null) {
                    if (sb.length() > 0) {
                        sb.append("&");
                    }

                    sb.append(pair[0]).append("=").append(ObjectUtils.toString(value));
                }
            }
        });

        return sb.toString();
    }

    /**
     * 下载文件，并保存到指定目录下
     */
    private File downloadFile(String url, String fileName) {
        File tempFile = FileUtils.createTempFile();
        if (StringUtils.startsWithIgnoreCase(url, "https://")) {
            HttpClientManager.getInstance().download(url, tempFile);
        } else {
            HttpClientManager.getInstance().download(url, tempFile);
        }

        return tempFile;
    }


    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void batchUpdateQcResult(RestJsonWrapperBean wrapper) {
        //选中的质控样
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        if (orderTaskList.isEmpty()) {
            return;
        }
        ;
        //FORM的分析项及数据，只更改录入的有数据的数据
        EmcOrderTaskResultBean copyOrderTaskResult = wrapper.parseUnique(EmcOrderTaskResultBean.class);
        String itemName = copyOrderTaskResult.getItemName();
        List<Long> orderTaskIdList = orderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        //找到对应选择的测试样品下的测试结果表
        List<EmcOrderTaskResultBean> qcResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID");
        //针对分析项筛选后的list即为该批下我们选择的质控对应单个分析项所有的结果
        List<EmcOrderTaskResultBean> newQcResultList = qcResultList.stream()
                .filter(result -> StringUtils.equals(itemName, result.getItemName())).collect(Collectors.toList());
        //循环赋值from表单中我们填写的数据；注意没填的不要赋值
        List<EmcOrderTaskResultBean> updateOrderTaskResultList = new ArrayList<EmcOrderTaskResultBean>();
        newQcResultList.forEach(e -> {
            EmcOrderTaskResultBean updateOrderTaskResult = new EmcOrderTaskResultBean();
            updateOrderTaskResult.setId(e.getId());
            if (copyOrderTaskResult.getOriginalResult() != "" && copyOrderTaskResult.getOriginalResult() != null) {
                updateOrderTaskResult.setOriginalResult(copyOrderTaskResult.getOriginalResult());
            } else {
                updateOrderTaskResult.setOriginalResult(e.getOriginalResult());
            }
            ;
            if (copyOrderTaskResult.getRn16() != "" && copyOrderTaskResult.getRn16() != null) {
                updateOrderTaskResult.setRn16(copyOrderTaskResult.getRn16());
            } else {
                updateOrderTaskResult.setRn16(e.getRn16());
            }
            ;
            if (copyOrderTaskResult.getStandQty() != "" && copyOrderTaskResult.getStandQty() != null) {
                updateOrderTaskResult.setStandQty(copyOrderTaskResult.getStandQty());
            } else {
                updateOrderTaskResult.setStandQty(e.getStandQty());
            }
            ;
            if (copyOrderTaskResult.getRn5() != "" && copyOrderTaskResult.getRn5() != null) {
                updateOrderTaskResult.setRn5(copyOrderTaskResult.getRn5());
            } else {
                updateOrderTaskResult.setRn5(e.getRn5());
            }
            ;
            updateOrderTaskResultList.add(updateOrderTaskResult);
        });
        orderTaskResultService.getDao().update(updateOrderTaskResultList, "ORIGINALRESULT", "RN5", "RN16", "STANDQTY");
        return;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void createTempHumidity(RestJsonWrapperBean wrapper) {
        String runId = wrapper.getFilterValue("runId");
        //根据runid找到ordertask表数据,温湿度表新增数据;
        List<PfRunTempHumidityBean> isContainList = pfRunTempHumidityService.selectListByFilter(SearchFilter.instance().match("runId", runId).filter(MatchPattern.OR));
        //已经生成的温湿度数据
        List<PfRunTempHumidityBean> updatePfRunTempHumidityList = new ArrayList<PfRunTempHumidityBean>();
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        //数据库中批下的样品测试数据
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValue(runId, "runId");
        if (isContainList.isEmpty()) {
            //温湿度表还没数据，按照测试1：1
            orderTaskList.forEach(e -> {
                PfRunTempHumidityBean updatePfRunTempHumidity = new PfRunTempHumidityBean();
                updatePfRunTempHumidity.setId(ApplicationContextHelper.getNextIdentity());
                updatePfRunTempHumidity.setRunId(runId);
                updatePfRunTempHumidity.setProjId(e.getProjId());
                updatePfRunTempHumidity.setOrderTaskId(e.getId());
                updatePfRunTempHumidity.setEquiptId(e.getEquiptId());
                updatePfRunTempHumidity.setOrderId(e.getOrderId());
                updatePfRunTempHumidity.setOrgid(parentOrgId);
                updatePfRunTempHumidityList.add(updatePfRunTempHumidity);
            });
            pfRunTempHumidityService.getDao().insert(updatePfRunTempHumidityList);
            updatePfRunTempHumidityList.clear();
            return;
        }
        //
        List<Long> orderTaskIdList = isContainList.stream().map(t -> t.getOrderTaskId()).collect(Collectors.toList());
        //排除已经生成的数据
        List<EmcOrderTaskBean> orderTaskFilterList = orderTaskService.getDao().selectListByOneColumnValue(runId, "runId").stream()
                .filter(t -> !orderTaskIdList.contains(t.getId())).collect(Collectors.toList());
        List<PfRunTempHumidityBean> insertNewPfRunTempHumidityList = new ArrayList<PfRunTempHumidityBean>();
        if (!orderTaskFilterList.isEmpty()) {
            //有新增测试
            orderTaskFilterList.forEach(e -> {
                PfRunTempHumidityBean insertNewPfRunTempHumidity = new PfRunTempHumidityBean();
                insertNewPfRunTempHumidity.setId(ApplicationContextHelper.getNextIdentity());
                insertNewPfRunTempHumidity.setRunId(runId);
                insertNewPfRunTempHumidity.setProjId(e.getProjId());
                insertNewPfRunTempHumidity.setOrderTaskId(e.getId());
                insertNewPfRunTempHumidity.setEquiptId(e.getEquiptId());
                insertNewPfRunTempHumidity.setOrderId(e.getOrderId());
                insertNewPfRunTempHumidity.setOrgid(parentOrgId);
                insertNewPfRunTempHumidityList.add(insertNewPfRunTempHumidity);
            });
            pfRunTempHumidityService.getDao().insert(insertNewPfRunTempHumidityList);

        }
        return;
    }

    //分析审核提交
    @Override
    public void submitProjectRun(RestJsonWrapperBean wrapper) {
        EmcProjectBean project = wrapper.parseUnique(EmcProjectBean.class);
        List<EmcRunBean> selectedRunList = wrapper.parse(EmcRunBean.class);

        // 查询当前项目，所提交批次下的样品测试
        List<String> selectedRunIdList = selectedRunList.stream().filter(r -> !StringUtils.isEmpty(r.getId())).map(EmcRunBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> filterOrderTaskList = new ArrayList<>();
        selectedRunIdList.forEach(runId -> {
            EmcOrderTaskBean filterOrderTask = new EmcOrderTaskBean();
            filterOrderTask.setRunId(runId);
            filterOrderTask.setAnalysisType(EmcBizConstant.ANALYSISCATEGORY_ANALYSIS);
            filterOrderTask.setProjId(project.getId());
            filterOrderTaskList.add(filterOrderTask);
        });

        List<EmcOrderTaskBean> analysisOrderTaskList = orderTaskService.getDao().selectList(filterOrderTaskList, Arrays.asList("runId", "analysisType", "projId"), Collections.emptyList());

        // 当前用户拥有权限的科室
        String userId = LocalContextHelper.getLoginUserId();
        CoreUserPermissionBean filter = new CoreUserPermissionBean();
        filter.setUserId(userId);
        filter.setPermissionTypeId("T_CORE_ORG");
        List<CoreUserPermissionBean> userPermissionList = userPermissionService.getDao().selectList(filter);
        List<String> orgIdList = userPermissionList.stream().map(CoreUserPermissionBean::getTargetId).collect(Collectors.toList());

        List<EmcOrderTaskBean> validOrderTaskList = analysisOrderTaskList.stream().filter(a -> orgIdList.contains(a.getAnalysisOrgId())).collect(Collectors.toList());
        validOrderTaskList.forEach(task -> task.setStatus(EmcBizConstant.ORDERTASK_STATUS_DONE));
        orderTaskService.getDao().update(validOrderTaskList, "STATUS");

        // 插入样品测试操作记录表
        orderTaskLogService.insert(validOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_LEADER_AUDIT, EmcBizConstant.ORDER_TASK_LOG_ANALYSE_AUDIT);

        // 插入样品测试关联批的审核人信息
        List<String> runIdList = validOrderTaskList.stream().filter(a -> !StringUtils.isEmpty(a.getRunId())).map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());
        EmcRunBean updateRun = new EmcRunBean();
        updateRun.setAuditedUserId(LocalContextHelper.getLoginUserId());
        updateRun.setAuditedUserName(LocalContextHelper.getLoginUser().getUserName());
        updateRun.setAuditedDate(LocalDateTime.now());
        updateRun.setStatus(EmcBizConstant.RUN_STATUS_DONE);
        updateRun.setUndoReason(null);
        getDao().updateByIds(updateRun, runIdList, "AUDITEDUSERID", "AUDITEDUSERNAME", "AUDITEDDATE", "STATUS", "undoReason");

        // 分析审核插入审核记录
        List<EmcAuditRecordBean> auditRecordList = new ArrayList<>();
        List<EmcRunBean> submitRunList = selectedRunList.stream().filter(run -> runIdList.contains(run.getId())).collect(Collectors.toList());

        for (EmcRunBean submitRun : submitRunList) {
            EmcAuditRecordBean auditRecord = new EmcAuditRecordBean();
            auditRecord.setId(ApplicationContextHelper.getNextIdentity());
            auditRecord.setProjId(submitRun.getProjId());
            auditRecord.setRunId(submitRun.getId());
            auditRecord.setComments("通过");
            auditRecord.setLogDesc("批号:" + submitRun.getId());
            auditRecord.setBizCategory("run");
            auditRecord.setStepCode("5");
            auditRecord.setAuditCategory("结果录入");
            auditRecord.setOperateCategory("审核");
            auditRecord.setTaskName("分析审核");
            auditRecord.setNextTaskName("报告编制");

            auditRecordList.add(auditRecord);
        }
        auditRecordService.getDao().insert(auditRecordList);

        // 更新点位状态
        List<Long> folderIdList = validOrderTaskList.stream().map(EmcOrderTaskBean::getFolderId).distinct().collect(Collectors.toList());
        List<Long> validOrderTaskIdList = validOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> folderOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(folderIdList, "folderId", Arrays.asList("id", "folderId", "status"));
        Map<Long, List<EmcOrderTaskBean>> folderOrderTaskMap = folderOrderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getFolderId));

        List<EmcFolderBean> updateFolderList = new ArrayList<>();
        folderOrderTaskMap.forEach((folderId, curTaskList) -> {
            // 判定当前点位下的样品测试是否都完成了
            boolean unfinished = curTaskList.stream().filter(task -> !validOrderTaskIdList.contains(task.getId())).anyMatch(task -> !EmcBizConstant.ORDERTASK_STATUS_DONE.equals(task.getStatus()));
            if (!unfinished) {
                EmcFolderBean updateFolder = new EmcFolderBean();
                updateFolder.setId(folderId);
                updateFolder.setStatus(EmcBizConstant.FOLDER_STATUS_DONE);
                updateFolderList.add(updateFolder);
            }
        });

        if (!updateFolderList.isEmpty()) {
            folderService.getDao().update(updateFolderList, "status");

            if (!EmcBizConstant.PROJECT_STATUS_REPORTEDIT.equals(project.getStatus())) {
                project.setStatus(EmcBizConstant.PROJECT_STATUS_REPORTEDIT);
                projectService.getDao().update(project, "status");
            }
        }

        List<EmcRunBean> composeRunList = getDao().selectListByOneColumnValues(runIdList, "id");
        List<String> skipComposeStatusList = Arrays.asList("pending");
        List<EmcRunBean> validRunList = composeRunList.stream().filter(run -> Constant.YES.equals(run.getCheckStatus()) && !skipComposeStatusList.contains(run.getExt$Item("composeStatus"))).collect(Collectors.toList());
        if (validRunList.isEmpty()) {
            return;
        }

        validRunList.forEach(run -> run.setExt$Item("composeStatus", "pending"));
        getDao().update(validRunList, "composeStatus");
        String loginUserId = LocalContextHelper.getLoginUserId();
//        ExecutorHelper.submit(() -> composeReportPdfOfProject(validRunList, loginUserId));
        ExecutorHelper.submit(
                new TimerTask() {
                    public void run() {
                        // 重新生成原始记录报表 PDF 文件
                        composeReportPdfOfProject(validRunList, loginUserId);
                    }
                },1000); // 延时1S执行
    }

    @Override
    @Transactional
    public void copyQcOrderTo(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> selectedOrderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<EmcRunBean> targetRunList = wrapper.parse(EmcRunBean.class);
        List<String> targetRunIdList = targetRunList.stream().filter(r -> !StringUtils.isEmpty(r.getId())).map(EmcRunBean::getId).collect(Collectors.toList());

        List<EmcOrderTaskBean> targetOrderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("runId", targetRunIdList).filter(MatchPattern.OR));
        ArrayList<EmcOrderTaskBean> uniqueOrderTaskList = targetOrderTaskList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(task -> task.getTestId() + "$" + task.getExt$Item("itemGroup")))), ArrayList::new));
        List<Long> uniqueOrderTaskIdList = uniqueOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> targetTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(uniqueOrderTaskIdList, "orderTaskId");

        // 比如把镉批下的实验室空白，复制到铜批下
        List<EmcOrderTaskBean> newOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> newOrderTaskResultList = new ArrayList<>();

        selectedOrderTaskList.forEach(copiedOrderTask -> targetRunIdList.forEach(targetRunId -> {
            List<EmcOrderTaskBean> curTaskList = uniqueOrderTaskList.stream().filter(task -> StringUtils.equals(targetRunId, task.getRunId())).collect(Collectors.toList());
            curTaskList.forEach(targetOrderTask -> {
                EmcOrderTaskBean newOrderTask = new EmcOrderTaskBean();
                BeanUtils.copyProperties(targetOrderTask, newOrderTask);
                newOrderTask.setProjId(copiedOrderTask.getProjId());
                newOrderTask.setFolderId(copiedOrderTask.getFolderId());
                newOrderTask.setOrderId(copiedOrderTask.getOrderId());
                newOrderTask.setEquiptAcquNo(copiedOrderTask.getEquiptAcquNo());
                newOrderTask.setQcCategoryId(copiedOrderTask.getQcCategoryId());
                newOrderTask.setQcCategoryCode(copiedOrderTask.getQcCategoryCode());
                newOrderTask.setQcName(copiedOrderTask.getQcName());
                newOrderTask.setCupNo(copiedOrderTask.getCupNo());
                newOrderTask.setId(ApplicationContextHelper.getNextIdentity());

                newOrderTaskList.add(newOrderTask);

                List<EmcOrderTaskResultBean> curResultList = targetTaskResultList.stream().filter(result -> ObjectUtils.equals(result.getOrderTaskId(), targetOrderTask.getId())).collect(Collectors.toList());
                curResultList.forEach(targetResult -> {
                    EmcOrderTaskResultBean newTaskResult = new EmcOrderTaskResultBean();
                    BeanUtils.copyProperties(targetResult, newTaskResult);
                    newTaskResult.setProjId(newOrderTask.getProjId());
                    newTaskResult.setFolderId(newOrderTask.getFolderId());
                    newTaskResult.setOrderId(newOrderTask.getOrderId());
                    newTaskResult.setOrderNo(copiedOrderTask.getExt$Item("orderNo"));
                    newTaskResult.setOrderTaskId(newOrderTask.getId());
                    newTaskResult.setQcCategoryId(newOrderTask.getQcCategoryId());
                    newTaskResult.setQcCategoryCode(newOrderTask.getQcCategoryCode());
                    newTaskResult.setQcName(newOrderTask.getQcName());
                    newTaskResult.setId(ApplicationContextHelper.getNextIdentity());
                    newTaskResult.setOriginalResult(null);
                    newTaskResult.setReportResult(null);
                    newTaskResult.setAvgResult(null);

                    newOrderTaskResultList.add(newTaskResult);
                });
            });
        }));

        if (!newOrderTaskList.isEmpty()) {
            orderTaskService.getDao().insert(newOrderTaskList);
            orderTaskResultService.getDao().insert(newOrderTaskResultList);
        }
    }

    @Override
    @Transactional
    public void copyReplaceQcOrder(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> selectedOrderTaskList = wrapper.parse(EmcOrderTaskBean.class, 0);
        List<EmcOrderTaskBean> replacedOrderTaskList = wrapper.parse(EmcOrderTaskBean.class, 1);

        Map<Long, List<EmcOrderTaskBean>> linkedMap = new HashMap<>(selectedOrderTaskList.size());
        Map<Long, List<Long>> linkedOrderIdMap = new HashMap<>(selectedOrderTaskList.size());
        selectedOrderTaskList.forEach(selectedTask -> {
            List<EmcOrderTaskBean> curReplacedTaskList = replacedOrderTaskList.stream().filter(replacedTask -> StringUtils.equals(selectedTask.getExt$Item("linkedId"), replacedTask.getExt$Item("linkedId"))).collect(Collectors.toList());
            List<Long> curReplacedOrderIdList = curReplacedTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
            linkedMap.put(selectedTask.getId(), curReplacedTaskList);
            linkedOrderIdMap.put(selectedTask.getId(), curReplacedOrderIdList);
        });

        // 镉批下有实验室空白1，铜批下也有实验室空白2，现在需要把铜批下的实验室空白2替换成实验室空白1
        List<Long> targetOrderIdList = replacedOrderTaskList.stream().map(EmcOrderTaskBean::getOrderId).collect(Collectors.toList());
        List<EmcOrderTaskBean> targetOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(targetOrderIdList, "orderId");
        List<EmcOrderTaskResultBean> targetTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(targetOrderIdList, "orderId");

        List<EmcOrderTaskBean> updateOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> updateOrderTaskResultList = new ArrayList<>();
        selectedOrderTaskList.forEach(selectedTask -> {
            List<EmcOrderTaskBean> curLinkedTaskList = linkedMap.get(selectedTask.getId());
            List<Long> curOrderIdList = curLinkedTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
            List<EmcOrderTaskBean> curTargetTaskList = targetOrderTaskList.stream().filter(task -> curOrderIdList.contains(task.getOrderId())).collect(Collectors.toList());
            curTargetTaskList.forEach(targetTask -> {
                targetTask.setFolderId(selectedTask.getFolderId());
                targetTask.setOrderId(selectedTask.getOrderId());
                targetTask.setEquiptAcquNo(selectedTask.getEquiptAcquNo());

                List<EmcOrderTaskResultBean> curResultList = targetTaskResultList.stream().filter(result -> ObjectUtils.equals(result.getOrderTaskId(), targetTask.getId())).collect(Collectors.toList());
                curResultList.forEach(targetResult -> {
                    targetResult.setFolderId(targetTask.getFolderId());
                    targetResult.setOrderId(targetTask.getOrderId());
                    targetResult.setOrderNo(selectedTask.getExt$Item("orderNo"));
                });
                updateOrderTaskResultList.addAll(curResultList);
            });

            updateOrderTaskList.addAll(curTargetTaskList);
        });

        // 质控关联的样品
        List<EmcQcRelatedOrderBean> qcRelatedOrderList = qcRelatedOrderService.getDao().selectListByOneColumnValues(targetOrderIdList, "qcOrderId");
        List<EmcQcRelatedOrderBean> updateQcRelatedOrderList = new ArrayList<>();
        selectedOrderTaskList.forEach(selectedTask -> {
            List<Long> curOrderIdList = linkedOrderIdMap.get(selectedTask.getId());
            List<EmcQcRelatedOrderBean> curRelatedOrderList = qcRelatedOrderList.stream().filter(relatedOrder -> curOrderIdList.contains(relatedOrder.getQcOrderId())).collect(Collectors.toList());
            curRelatedOrderList.forEach(qcRelatedOrder -> {
                qcRelatedOrder.setQcOrderId(selectedTask.getOrderId());
                qcRelatedOrder.setQcOrderNo(selectedTask.getExt$Item("orderNo"));
            });
            updateQcRelatedOrderList.addAll(curRelatedOrderList);
        });

        orderTaskService.getDao().update(updateOrderTaskList, "folderId", "orderId", "equiptAcquNo");
        orderTaskResultService.getDao().update(updateOrderTaskResultList, "folderId", "orderId", "orderNo");
        qcRelatedOrderService.getDao().update(updateQcRelatedOrderList, "qcOrderId", "qcOrderNo");

        // 质控样被替换之后，验证一下该质控样下是否还有其他的测试，若没有，则删除此质控样
        List<EmcOrderTaskBean> curTaskList = orderTaskService.getDao().selectListByOneColumnValues(targetOrderIdList, "orderId", Collections.singletonList("id"));
        if (curTaskList.isEmpty()) {
            orderService.getDao().deleteByIdList(targetOrderIdList);
        }
    }

    @Override
    public void syncAnalysisUserChange(RestJsonWrapperBean wrapper) {
        EmcRunBean orgRun = wrapper.parseUnique(EmcRunBean.class);
        String analysisUserId = wrapper.getFilterValue("analysisUserId");
        String analysisUserName = wrapper.getFilterValue("analysisUserName");

        List<Long> testMethodIdList = Arrays.stream(StringUtils.split(orgRun.getTestMethodId(), ",")).filter(NumberUtils::isNumber).mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());
        List<EmcTestCapacityBean> testCapacityList = testCapacityService.selectListByFilter(SearchFilter.instance().match("testMethodId", testMethodIdList).filter(MatchPattern.OR).match("orgId", LocalContextHelper.getLoginUser().getExt$Item("parentOrgId")).filter(MatchPattern.EQ));

        testCapacityList.forEach(capacity -> {
            capacity.setExt$Item("inputUserId", analysisUserId);
            capacity.setExt$Item("inputUserName", analysisUserName);
        });

        testCapacityService.getDao().update(testCapacityList, "inputUserId", "inputUserName");
    }

    @Override
    public void syncWeighingUserChange(RestJsonWrapperBean wrapper) {
        EmcRunBean orgRun = wrapper.parseUnique(EmcRunBean.class);
        String weighingUserId = wrapper.getFilterValue("weighinguserid");
        String weighingUserName = wrapper.getFilterValue("weighingusername");

        List<Long> testMethodIdList = Arrays.stream(StringUtils.split(orgRun.getTestMethodId(), ",")).filter(NumberUtils::isNumber).mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());
        List<EmcTestCapacityBean> testCapacityList = testCapacityService.selectListByFilter(SearchFilter.instance().match("testMethodId", testMethodIdList).filter(MatchPattern.OR).match("orgId", LocalContextHelper.getLoginUser().getExt$Item("parentOrgId")).filter(MatchPattern.EQ));

        testCapacityList.forEach(capacity -> {
            capacity.setExt$Item("weighingUserId", weighingUserId);
            capacity.setExt$Item("weighingUserName", weighingUserName);
        });

        testCapacityService.getDao().update(testCapacityList, "weighingUserId", "weighingUserName");
    }

    @Override
    public void syncPretreatUserUserChange(RestJsonWrapperBean wrapper) {
        EmcRunBean orgRun = wrapper.parseUnique(EmcRunBean.class);
        String pretreatUserId = wrapper.getFilterValue("pretreatuserid");
        String pretreatUserName = wrapper.getFilterValue("pretreatusername");

        List<Long> testMethodIdList = Arrays.stream(StringUtils.split(orgRun.getTestMethodId(), ",")).filter(NumberUtils::isNumber).mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());
        List<EmcTestCapacityBean> testCapacityList = testCapacityService.selectListByFilter(SearchFilter.instance().match("testMethodId", testMethodIdList).filter(MatchPattern.OR).match("orgId", LocalContextHelper.getLoginUser().getExt$Item("parentOrgId")).filter(MatchPattern.EQ));

        testCapacityList.forEach(capacity -> {
            capacity.setExt$Item("pretreatUserId", pretreatUserId);
            capacity.setExt$Item("pretreatUserName", pretreatUserName);
        });

        testCapacityService.getDao().update(testCapacityList, "pretreatUserId", "pretreatUserName");
    }

    @Override
    public void syncQCSTD(RestJsonWrapperBean wrapper) {
        EmcRunBean orgRun = wrapper.parseUnique(EmcRunBean.class);
        String runId = orgRun.getId();
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValue(runId, "runId", Arrays.asList("id", "methodId", "QCCATEGORYCODE", "testMethodId"));
        //获取该批下的标准测试样品QC类型的EmcOrderTaskResult表数据
        List<EmcOrderTaskBean> orderQCSTDTaskList = orderTaskList.stream().filter(o -> StringUtils.equals(EmcConstant.QCCATEGORY_QCCATEGORYID_QC_STD, o.getQcCategoryCode())).collect(Collectors.toList());
        List<Long> orderTaskIdList = orderTaskList.stream().map(o -> o.getId()).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "orderTaskId", Arrays.asList("id", "rn16", "standQty", "itemName", "testMethodId", "itemId", "qccategorycode"));
        List<EmcOrderTaskResultBean> orderTaskQCSTDResultList = orderTaskResultList.stream().filter(r -> StringUtils.equals(EmcConstant.QCCATEGORY_QCCATEGORYID_QC_STD, r.getQcCategoryCode())).collect(Collectors.toList());
        //获取监测能力库默认测试及方法相同的数据
        List<Long> orderTaskTestMethodIdList = orderQCSTDTaskList.stream().distinct().map(o -> o.getTestMethodId()).collect(Collectors.toList());
        List<EmcTestCapacityBean> testCapacityList = testCapacityService.selectListByFilter(SearchFilter.instance().match("testMethodId", orderTaskTestMethodIdList).filter(MatchPattern.OR).match("orgId", LocalContextHelper.getLoginUser().getExt$Item("parentOrgId")).filter(MatchPattern.EQ));
        List<EmcTestCapacityBean> testCapacityQCSTDList = testCapacityList.stream().filter(t -> StringUtils.equals("1", t.getExt$Item("ISDEFAULT")))
                .collect(Collectors.toList());
        if (testCapacityQCSTDList.isEmpty()) {
            return;
        }

        String batchNo = testCapacityQCSTDList.get(0).getExt$Item("batchno");
        List<EmcStockBean> stockList = stockService.getDao().selectListByOneColumnValue(batchNo, "batchNo", Arrays.asList("id", "batchNo", "mtlId"));
        if (stockList.isEmpty()) {
            return;
        }
        Long stockId = stockList.get(0).getId();
        //此编号下可能有多个曲线，根据testmethodId匹配
        List<EmcStockConcentBean> stockConcentList = stockConcentService.getDao().selectListByOneColumnValue(stockId, "stockId");
        List<String> mtlIdList = stockList.stream().map(s -> s.getMtlId()).collect(Collectors.toList());
        List<EmcMtlMaintainBean> maintainList = mtlMaintainService.getDao().selectListByOneColumnValues(mtlIdList, "mtlId", Arrays.asList("id", "mtlId"));
        List<Long> maintainIdList = maintainList.stream().map(i -> i.getId()).collect(Collectors.toList());
        List<EmcMtlMaintainItemBean> maintainItemList = mtlMaintainItemService.getDao().selectListByOneColumnValues(maintainIdList, "mtlMaitainId");
        orderTaskQCSTDResultList.forEach(orderTaskResult -> {
            //找到测试相同的浓度表数据
            List<EmcMtlMaintainItemBean> beforeMaintainItemSelectList = maintainItemList.stream().filter(m -> m.getTestMethodId().equals(orderTaskResult.getTestMethodId())).collect(Collectors.toList());
            List<EmcMtlMaintainItemBean> maintainItemSelectList = beforeMaintainItemSelectList.stream().filter(m -> ObjectUtils.equals(orderTaskResult.getItemId(), m.getItemId())).collect(Collectors.toList());
            List<Long> itemIdList = maintainItemList.stream().map(m -> m.getItemId()).collect(Collectors.toList());
            if (!maintainItemSelectList.isEmpty() && itemIdList.contains(orderTaskResult.getItemId())) {

                List<EmcStockConcentBean> stockConcentSelectList = stockConcentList.stream().filter(sc -> sc.getMtlMaintainId().equals(maintainItemSelectList.get(0).getMtlMaitainId())).filter(sc -> sc.getStockId().equals(stockId)).collect(Collectors.toList());
                List<EmcStockConcentBean> stockConcentMatchList = stockConcentSelectList.stream().filter(sc -> sc.getStockId().equals(stockList.get(0).getId())).collect(Collectors.toList());
                if (stockConcentMatchList.isEmpty()) {
                    return;
                }
                orderTaskResult.setRn16(StringUtils.isEmpty(stockConcentMatchList.get(0).getUncertainty()) ? "" : stockConcentMatchList.get(0).getUncertainty());
                orderTaskResult.setStandQty(StringUtils.isEmpty(stockConcentMatchList.get(0).getStdValue()) ? "" : stockConcentMatchList.get(0).getStdValue());
                orderTaskResult.setIndexHighLimit(StringUtils.isEmpty(stockConcentMatchList.get(0).getHighLimit()) ? "" : stockConcentMatchList.get(0).getHighLimit());
                orderTaskResult.setIndexLowLimit(StringUtils.isEmpty(stockConcentMatchList.get(0).getLowLimit()) ? "" : stockConcentMatchList.get(0).getLowLimit());

            }

        });
        orderQCSTDTaskList.forEach(orderTaskResult -> {
            orderTaskResult.setStockBatchNo(batchNo);
        });
        orderTaskService.getDao().update(orderQCSTDTaskList, "stockBatchNo");
        orderTaskResultService.getDao().update(orderTaskQCSTDResultList, "rn16", "standQty", "indexLowLimit", "indexHighLimit");

    }

    //手动改数据编号根据编号找到对应的不确定度和标准值，
    @Override
    public void editSyncQCSTD(RestJsonWrapperBean wrapper) {
        //选择的实验室标准样品
        List<EmcOrderTaskBean> orderTaskOrgList = wrapper.parse(EmcOrderTaskBean.class);
        List<Long> selectTestMethodIdList = orderTaskOrgList.stream().map(task -> task.getTestMethodId()).collect(Collectors.toList());
        String runId = orderTaskOrgList.get(0).getRunId();
        Long orderId = orderTaskOrgList.get(0).getOrderId();
        String batchNo = wrapper.getFilterValue("batchNo");
        //获取该批下的标准测试样品QC类型的EmcOrderTaskResult表数据
        List<EmcOrderTaskBean> orderQCSTDTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("runId", runId).filter(MatchPattern.OR)
                .match("orderId", orderId).filter(MatchPattern.OR).match("orderId", orderId).filter(MatchPattern.OR).match("qcCategoryCode", EmcConstant.QCCATEGORY_QCCATEGORYID_QC_STD).filter(MatchPattern.OR));

        if (orderQCSTDTaskList.isEmpty()) {
            return;
        }
        List<Long> orderTaskIdList = orderQCSTDTaskList.stream().map(o -> o.getId()).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskQCSTDResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "orderTaskId", Arrays.asList("id", "rn16", "standQty", "itemName", "testMethodId", "itemId", "qccategorycode", "itemAliasName", "revisionrule"));

        //获取监测能力库默认测试及方法相同的数据
        List<EmcStockBean> stockList = stockService.getDao().selectListByOneColumnValue(batchNo, "batchNo", Arrays.asList("id", "batchNo", "mtlId"));

        if (stockList.isEmpty()) {
            return;
        }
        Long stockId = stockList.get(0).getId();
        //此编号下可能有多个曲线，根据testmethodId匹配
        List<EmcStockConcentBean> stockConcentList = stockConcentService.getDao().selectListByOneColumnValue(stockId, "stockId");
        List<String> mtlIdList = stockList.stream().map(s -> s.getMtlId()).collect(Collectors.toList());
        List<EmcMtlMaintainBean> maintainList = mtlMaintainService.getDao().selectListByOneColumnValues(mtlIdList, "mtlId", Arrays.asList("id", "mtlId"));
        List<Long> maintainIdList = maintainList.stream().map(i -> i.getId()).collect(Collectors.toList());
        List<EmcMtlMaintainItemBean> maintainItemList = mtlMaintainItemService.getDao().selectListByOneColumnValues(maintainIdList, "mtlMaitainId");
        List<Long> testMethodIdList = orderTaskQCSTDResultList.stream().map(r -> r.getTestMethodId()).distinct().collect(Collectors.toList());
        orderTaskQCSTDResultList.forEach(orderTaskResult -> {
            //找到测试相同的浓度表数据
            List<EmcMtlMaintainItemBean> maintainBeforeItemSelectList = maintainItemList.stream().filter(m -> m.getTestMethodId().equals(orderTaskResult.getTestMethodId())).collect(Collectors.toList());
            List<EmcMtlMaintainItemBean> maintainItemSelectList = maintainBeforeItemSelectList.stream().filter(m -> ObjectUtils.equals(orderTaskResult.getItemId(), m.getItemId())).collect(Collectors.toList());

            List<Long> itemIdList = maintainItemList.stream().map(m -> m.getItemId()).collect(Collectors.toList());
            if (!maintainItemSelectList.isEmpty() && itemIdList.contains(orderTaskResult.getItemId())) {
                List<EmcStockConcentBean> stockConcentSelectList = stockConcentList.stream().filter(sc -> sc.getMtlMaintainId().equals(maintainItemSelectList.get(0).getMtlMaitainId())).filter(sc -> sc.getStockId().equals(stockId)).collect(Collectors.toList());
                List<EmcStockConcentBean> stockConcentMatchList = stockConcentSelectList.stream().filter(sc -> sc.getStockId().equals(stockList.get(0).getId())).collect(Collectors.toList());

                orderTaskResult.setRn16(stockConcentMatchList.get(0).getUncertainty());
                orderTaskResult.setStandQty(stockConcentMatchList.get(0).getStdValue());
                if (maintainItemSelectList.size() > 1) {
                    if (orderTaskResult.getItemAliasName().contains(PfConstant.SAMPLECONCENTRATION)) {
                        if (!StringUtils.isEmpty(stockConcentMatchList.get(0).getStdValue())) {
                            if (!StringUtils.isEmpty(stockConcentMatchList.get(0).getConcentUnitName())) {
                                orderTaskResult.setUnitName(stockConcentMatchList.get(0).getConcentUnitName());
                            }
                            String numBit = getNumBits(stockConcentMatchList.get(0).getStdValue());
                            if (!StringUtils.isEmpty(numBit)) {
                                orderTaskResult.setRevisionRule("FDA," + getNumBits(stockConcentMatchList.get(0).getStdValue()));
                            }
                        }
                    }
                } else {
                    if (orderTaskResult.getItemAliasName().contains(PfConstant.SAMPLECONCENTRATION)) {
                        if (!StringUtils.isEmpty(stockConcentMatchList.get(0).getStdValue())) {
                            if (!StringUtils.isEmpty(stockConcentMatchList.get(0).getConcentUnitName())) {
                                orderTaskResult.setUnitName(stockConcentMatchList.get(0).getConcentUnitName());
                            }
                            String numBit = getNumBits(stockConcentMatchList.get(0).getStdValue());
                            if (!StringUtils.isEmpty(numBit)) {
                                orderTaskResult.setRevisionRule("FDA," + getNumBits(stockConcentMatchList.get(0).getStdValue()));
                            }
                        }
                    }

                }

                orderTaskResult.setIndexHighLimit(stockConcentMatchList.get(0).getHighLimit());
                orderTaskResult.setIndexLowLimit(stockConcentMatchList.get(0).getLowLimit());
            }

        });
        orderQCSTDTaskList.forEach(orderTask -> {
            orderTask.setStockBatchNo(batchNo);
        });
        List<Long> miTestMethodIdList = maintainItemList.stream().map(mi -> mi.getTestMethodId()).distinct().collect(Collectors.toList());
        //样品测试下没有曲线对应的测试的测试
        List<Long> notInMtlTestMethodIdList = testMethodIdList.stream().filter(item -> !miTestMethodIdList.contains(item)).collect(Collectors.toList());
        //标准样品测试下在标液中的测试
        List<Long> inMtlTestMethodIdList = testMethodIdList.stream().filter(item -> miTestMethodIdList.contains(item)).distinct().collect(Collectors.toList());
        //需要删除的为notInMtlTestMethodIdList +在标液里面的测试未选择的测试
        //进一步筛选未选择的inMtlTestMethodIdList -选择的=未选择的
        List<Long> notSelectTestMethodIdList = inMtlTestMethodIdList.stream().filter(item -> !selectTestMethodIdList.contains(item)).distinct().collect(Collectors.toList());
        //真正要删除的测试
        notInMtlTestMethodIdList.addAll(notSelectTestMethodIdList);
        List<EmcOrderTaskResultBean> deleteOrderTaskQCSTDResultList = orderTaskQCSTDResultList.stream().filter(res -> notInMtlTestMethodIdList.contains(res.getTestMethodId())).distinct().collect(Collectors.toList());
        //删除库曲线中没有的测试
        orderTaskResultService.getDao().deleteBy(deleteOrderTaskQCSTDResultList);
        orderTaskService.getDao().update(orderQCSTDTaskList, "stockBatchNo");
        orderTaskResultService.getDao().update(orderTaskQCSTDResultList, "rn16", "standQty", "indexLowLimit", "indexHighLimit", "revisionrule", "unitName");
        List<Long> calcResultIdList = orderTaskQCSTDResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
        EmcOrderTaskResultComputingEngine.getEngine(calcResultIdList).compute();
    }

    @Override
    @Transactional
    public void cancelCombineOrderTask(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<Long> virtualOrderIdList = orderTaskList.stream().filter(task -> StringUtils.equals(task.getExt$Item("originalorder"), Constant.YES)).map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        if (virtualOrderIdList.isEmpty()) {
            return;
        }

        List<EmcOrderBean> updateOrderList = new ArrayList<>(virtualOrderIdList.size());
        virtualOrderIdList.forEach(orderId -> {
            EmcOrderBean updateOrder = new EmcOrderBean();
            updateOrder.setId(orderId);
            updateOrder.setExt$Item("originalOrder", Constant.NO);
            updateOrderList.add(updateOrder);
        });

        List<EmcOrderTaskBean> allOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(virtualOrderIdList, "orderId", Arrays.asList("id", "orderId", "virtual"));
        List<EmcOrderTaskBean> virtualTaskList = allOrderTaskList.stream().filter(task -> StringUtils.equals(task.getExt$Item("virtual"), Constant.YES)).collect(Collectors.toList());
        List<Long> virtualTaskIdList = virtualTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> virtualResultList = orderTaskResultService.getDao().selectListByOneColumnValues(virtualTaskIdList, "orderTaskId", Collections.singletonList("id"));
        List<Long> virtualResultIdList = virtualResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());

        orderService.getDao().update(updateOrderList, "originalOrder");
        orderTaskService.getDao().deleteByIdList(virtualTaskIdList);
        orderTaskResultService.getDao().deleteByIdList(virtualResultIdList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_COMPLETE)
    public List<CoreBpmnInstanceStatusDTO<String>> completeTask(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<String>> coreBpmnInstanceStatusDTOList = super.completeTaskBasic(wrapper);

        List<String> runIdList = coreBpmnInstanceStatusDTOList.stream().map(CoreBpmnInstanceStatusDTO::getId).collect(Collectors.toList());
        List<EmcRunBean> runList = getDao().selectListByIds(runIdList);

        List<EmcOrderTaskBean> filterOrderTaskList = new ArrayList<>();
        runIdList.forEach(runId -> {
            EmcOrderTaskBean filterOrderTask = new EmcOrderTaskBean();

            filterOrderTask.setRunId(runId);
            filterOrderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_APPROVE);
            filterOrderTaskList.add(filterOrderTask);
        });

        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectList(filterOrderTaskList, Arrays.asList("RUNID", "STATUS"), Collections.singletonList("ID"));

        // 分析校核审核通过
        if (!orderTaskList.isEmpty()) {
            orderTaskList.forEach(t -> t.setStatus(EmcBizConstant.ORDERTASK_STATUS_AUDIT));
            orderTaskService.getDao().update(orderTaskList, "STATUS");
            //分析审核通过更新项目的最新的分析校核通过时间
            List<Long> projIdList = runList.stream().map(EmcRunBean::getProjId).distinct().collect(Collectors.toList());
            List<EmcProjectBean> updateProjList = projIdList.stream().map(item -> {
                EmcProjectBean proj = new EmcProjectBean();
                proj.setId(item);
                proj.setExt$Item("newestauditdate", DateTimeUtils.formatLocalDate(LocalDate.now()));
                return proj;
            }).collect(Collectors.toList());
            projectService.getDao().update(updateProjList, "NEWESTAUDITDATE");
        }

        // 插入样品测试操作记录表
        List<EmcOrderTaskBean> allOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(runIdList, "RUNID");
        orderTaskLogService.insert(allOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_GROUP_AUDIT, EmcBizConstant.ORDER_TASK_LOG_ANALYSE_MUTUAL_AUDIT);

        // 分析校核审核插入审核记录
        this.insertAuditRecordByRun(runList, "PROCESS_COMPLETE", wrapper.getParamValue("bpmn_comment"), null);

        runList.forEach(run -> run.setStatus(EmcBizConstant.RUN_STATUS_AUDIT));
        getDao().update(runList, "STATUS");

        // 合并原始记录
        String loginUserId = LocalContextHelper.getLoginUserId();
//        ExecutorHelper.submit(() -> composeReportPdfOfProject(runList, loginUserId));
        ExecutorHelper.submit(
                new TimerTask() {
                    public void run() {
                        // 重新生成原始记录报表 PDF 文件
                        composeReportPdfOfProject(runList, loginUserId);
                    }
                },1000); // 延时1S执行
        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_START)
    @ValidateDataWith(EmcRunSubmitValidator.class)
    public List<CoreBpmnInstanceStatusDTO<String>> startProcess(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<String>> coreBpmnInstanceStatusDTOList = super.startProcessBasic(wrapper);
        List<String> idList = coreBpmnInstanceStatusDTOList.stream().map(CoreBpmnInstanceStatusDTO::getId).collect(Collectors.toList());
        List<EmcRunBean> runList = getDao().selectListByIds(idList);

        // 分析录入提交提交插入审核记录
        this.insertAuditRecordByRun(runList, "PROCESS_START", wrapper.getParamValue("bpmn_comment"), null);

        // orderTask表插入分析日期字段
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(idList, "RUNID").stream()
                .filter(a -> StringUtils.equals(a.getStatus(), EmcBizConstant.ORDERTASK_STATUS_LOGGED)).collect(Collectors.toList());

        runList.forEach(run -> orderTaskList.stream().filter(task -> StringUtils.equals(run.getId(), task.getRunId())).forEach(task -> {
            task.setAnalysisDate(run.getAnalysisBeginDate());
            task.setStatus(EmcBizConstant.ORDERTASK_STATUS_APPROVE);
        }));

        // 插入样品测试操作记录表
        orderTaskLogService.insert(orderTaskList, EmcBizConstant.ORDER_TASK_LOG_RESULT_INPUT, EmcBizConstant.ORDER_TASK_LOG_ANALYSE);

        // 将样品测试结果表的记录日志标志置为"1"
        List<Long> orderTaskIdList = orderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "orderTaskId");
        orderTaskResultList.forEach(result -> result.setSubmitFlag(Constant.YES));
        orderTaskResultService.getDao().update(orderTaskResultList, "SUBMITFLAG");
        orderTaskService.getDao().update(orderTaskList, "ANALYSISDATE", "STATUS");
        //这些批下的温湿度
        List<PfRunTempHumidityBean> runTempHumidityBeans = pfRunTempHumidityService.selectListByFilter(SearchFilter.instance().match("runId", idList).filter(MatchPattern.OR));
        //根据温湿度的最早分析开始时间最晚分析结束时间，赋值给批
        runList.forEach(run -> {
            //单个项目的温湿度
            List<PfRunTempHumidityBean> runTempHumiditySingleBeans = runTempHumidityBeans.stream().filter(rt -> StringUtils.equals(run.getId(), rt.getRunId())).collect(Collectors.toList());
            //由于有三个分析开始时间，结束时间，需要找到最早的开始时间和最晚的结束时间
            List<LocalDate> beginDateList = runTempHumiditySingleBeans.stream().map(PfRunTempHumidityBean::getBeginDate).filter(rt -> !ObjectUtils.isEmpty(rt)).distinct().collect(Collectors.toList());
            List<LocalDate> beginDateOneList = runTempHumiditySingleBeans.stream().map(PfRunTempHumidityBean::getBeginDateOne).filter(rt -> !ObjectUtils.isEmpty(rt)).distinct().collect(Collectors.toList());
            List<LocalDate> beginDateTwoList = runTempHumiditySingleBeans.stream().map(PfRunTempHumidityBean::getBeginDateTwo).filter(rt -> !ObjectUtils.isEmpty(rt)).distinct().collect(Collectors.toList());
            beginDateList.addAll(beginDateOneList);
            beginDateList.addAll(beginDateTwoList);
            List<LocalDate> endDateList = runTempHumiditySingleBeans.stream().map(PfRunTempHumidityBean::getEndDate).filter(rt -> !ObjectUtils.isEmpty(rt)).distinct().collect(Collectors.toList());
            List<LocalDate> endDateOneList = runTempHumiditySingleBeans.stream().map(PfRunTempHumidityBean::getEndDateOne).filter(rt -> !ObjectUtils.isEmpty(rt)).distinct().collect(Collectors.toList());
            List<LocalDate> endDateTwoList = runTempHumiditySingleBeans.stream().map(PfRunTempHumidityBean::getEndDateTwo).filter(rt -> !ObjectUtils.isEmpty(rt)).distinct().collect(Collectors.toList());
            endDateList.addAll(endDateOneList);
            endDateList.addAll(endDateTwoList);
            CollectionUtils.sort(beginDateList, (o1, o2) -> o1.compareTo(o2));
            CollectionUtils.sort(endDateList, (o1, o2) -> o2.compareTo(o1));
            if (!beginDateList.isEmpty()) {
                String minBeginDate = beginDateList.get(0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                run.setExt$Item("minanalysisbegindate", minBeginDate);
            } else {
                run.setExt$Item("minanalysisbegindate", "");
            }
            if (!endDateList.isEmpty()) {
                String maxEndDate = endDateList.get(0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                run.setExt$Item("maxnanalysisenddate", maxEndDate);
            } else {
                run.setExt$Item("maxnanalysisenddate", "");
            }
            run.setStatus(EmcBizConstant.RUN_STATUS_APPROVE);
        });

        runTempHumidityBeans.clear();
        // 提交时自动忽略未查看的计算公式完成通知
        CoreNotificationInstanceToBean filterInstTo = new CoreNotificationInstanceToBean();
        filterInstTo.setNotifiedId(LocalContextHelper.getLoginUserId());
        filterInstTo.setRead("0");
        List<CoreNotificationInstanceToBean> instanceToList = notificationInstanceToService.getDao().selectList(filterInstTo, Arrays.asList("notifiedId", "read"), Arrays.asList("id", "instId"));
        List<Long> instIdList = instanceToList.stream().map(CoreNotificationInstanceToBean::getInstId).distinct().collect(Collectors.toList());

        List<CoreNotificationInstanceBean> instanceList = notificationInstanceService.getDao().selectListByOneColumnValues(instIdList, "id", Arrays.asList("id", "desc"));
        List<Long> updateInstIdList = instanceList.stream().filter(inst -> idList.stream().anyMatch(runId -> StringUtils.contains(inst.getContent(), runId))).map(CoreNotificationInstanceBean::getId).collect(Collectors.toList());
        List<CoreNotificationInstanceToBean> updateInstToList = instanceToList.stream().filter(instTo -> updateInstIdList.contains(instTo.getInstId())).collect(Collectors.toList());
        updateInstToList.forEach(instTo -> instTo.setRead("2"));
        notificationInstanceToService.getDao().update(updateInstToList, "read");

        getDao().update(runList, "STATUS", "MINANALYSISBEGINDATE", "MAXNANALYSISENDDATE");

        // 合并原始记录
        String loginUserId = LocalContextHelper.getLoginUserId();
//        ExecutorHelper.submit(() -> composeReportPdfOfProject(runList, loginUserId));

        ExecutorHelper.submit(
                new TimerTask() {
                    public void run() {
                        // 重新生成原始记录报表 PDF 文件
                        composeReportPdfOfProject(runList, loginUserId);
                    }
                },1000); // 延时1S执行
        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void composeAllRunListReportPdf(RestJsonWrapperBean wrapper) {
        //所有批下的项目编号组合
        List<EmcRunBean> paramRunList = wrapper.parse(EmcRunBean.class);
        //要提交的批
        List<EmcRunBean> validRunList = paramRunList.stream().filter(run -> !(StringUtils.equals(run.getExt$Item("composeStatus"), "done")
                && StringUtils.equals(run.getCheckStatus(), Constant.YES))).collect(Collectors.toList());
        if (validRunList.isEmpty()) {
            return;
        }

        composeReportPdfOfProject(validRunList, LocalContextHelper.getLoginUserId());
    }

    @Override
    public void recomposeRunPdf(RestJsonWrapperBean wrapper) {
        List<EmcRunBean> runList = wrapper.parse(EmcRunBean.class);
        List<CoreFileBean> filterFileList = new ArrayList<>();
        String table = getDao().getTable() + "$";
        runList.forEach(r -> {
            CoreFileBean coreFile = new CoreFileBean();
            coreFile.setTargetId(table + r.getId());
            coreFile.setExt("composePdf");
            filterFileList.add(coreFile);
        });
        coreFileService.getDao().deleteBy(filterFileList, "TARGETID", "EXT");

        String loginUserId = LocalContextHelper.getLoginUserId();
        ExecutorHelper.submit(() -> composeReportPdfOfProject(runList, loginUserId));
    }

    @Override
    @Transactional
    public void composeReportPdfOfProject(List<EmcRunBean> validRunList, String noticeUserId) {
        List<String> runIdList = validRunList.stream().map(EmcRunBean::getId).distinct().collect(Collectors.toList());
        List<String> projnoList = validRunList.stream().map(EmcRunBean::getProjNo).distinct().collect(Collectors.toList());
        //所有批下的项目编号分割
        Set<String> projectNoForRunListSet = new HashSet<>();
        projnoList.forEach(nos -> projectNoForRunListSet.addAll(Arrays.asList(StringUtils.split(nos, ","))));

        //要提交的批中所有的项目
        List<EmcProjectBean> allProjectList = projectService.getDao().selectListByOneColumnValues(new ArrayList<>(projectNoForRunListSet), "projNo");
        List<EmcOrderTaskBean> runOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(runIdList, "runId", Arrays.asList("id", "projId", "runId", "cupNo"));

        //按照批去循环做操作
        IntStream.range(0, validRunList.size()).forEach(index -> {
            long startTime = System.currentTimeMillis();
            boolean success = true;
            Map<String, Object> paramMap = new HashMap<>(4);

            EmcRunBean targetRun = validRunList.get(index);
            paramMap.put("runId", targetRun.getId());
            List<String> curProjectNoList = Arrays.asList(StringUtils.split(targetRun.getProjNo(), ","));
            List<EmcProjectBean> curProjectList = allProjectList.stream().filter(p -> curProjectNoList.contains(p.getProjNo())).collect(Collectors.toList());
            //获取当前批关联的项目ID
            List<Long> projIdList = curProjectList.stream().map(EmcProjectBean::getId).distinct().collect(Collectors.toList());
            //当前批下项目的所有测试数据
            List<EmcOrderTaskBean> orderTaskRunList = runOrderTaskList.stream().filter(orderTask -> projIdList.contains(orderTask.getProjId()) && StringUtils.equals(orderTask.getRunId(), targetRun.getId())).collect(Collectors.toList());

            // 把测试批按项目编号进行拆分
            List<EmcRunBean> runList = new ArrayList<>(curProjectNoList.size());
            projIdList.forEach(pid -> {
                List<EmcOrderTaskBean> curTaskList = orderTaskRunList.stream().filter(orderTask -> ObjectUtils.equals(orderTask.getProjId(), pid))
                        .sorted(Comparator.comparing(EmcOrderTaskBean::getCupNo)).collect(Collectors.toList());
                String projNo = curProjectList.stream().filter(p -> ObjectUtils.equals(p.getId(), pid)).findFirst().orElseGet(EmcProjectBean::new).getProjNo();
                EmcRunBean newRun = new EmcRunBean();
                BeanUtils.copyProperties(targetRun, newRun);
                newRun.setProjId(pid);
                newRun.setProjNo(projNo);
                newRun.setExt$Item("cupNo", curTaskList.get(0).getCupNo() + "");
                runList.add(newRun);
            });

            runList.sort(Comparator.comparing(r -> NumberUtils.parseLong(r.getExt$Item("cupNo"))));
            try {
                composeAllRunReportPdf(runList);
            } catch (Exception e) {
                success = false;
                paramMap.put("errorMsg", e.getMessage());
            }
            // 按批生成PDF文件
            // 把上一步生成的文件合并成一个PDF文件
            List<CoreFileBean> fileFilters = new ArrayList<>();
            runList.forEach(r -> {
                CoreFileBean coreFileBean = new CoreFileBean();
                coreFileBean.setTargetId("T_EMC_RUN" + "$" + r.getId() + "-" + r.getProjId());
                coreFileBean.setExt("composePdf");
                fileFilters.add(coreFileBean);
            });

            List<CoreFileBean> runPdfFileList = coreFileService.getDao().selectList(fileFilters, Arrays.asList("TARGETID", "EXT"), Collections.emptyList());
            //if (runPdfFileList.size() > curProjectNoList.size()) {
            //    return;
            //}

            List<FilePathDTO> fileRepresentations = runPdfFileList.stream().map(CoreFileUtils::toFilePath).collect(Collectors.toList());
            String projectReportPdfName = targetRun.getId() + ".pdf";
            FilePathDTO composedPdfFile = FilePathDTO.of(FileScope.temp.name(), "" + System.currentTimeMillis(), StringUtils.randomUUID(8), projectReportPdfName);
            coreFileService.mergePdfList(fileRepresentations, composedPdfFile);
            String tempFilePath = FilePathManager.getLocalPath(composedPdfFile).toString();
            CoreFileBean deleteFile = new CoreFileBean();
            deleteFile.setTargetId("T_EMC_RUN" + "$" + targetRun.getId());
            deleteFile.setExt("composeAllPdf");
            coreFileService.getDao().deleteBy(deleteFile, "TARGETID", "EXT");

            // 保存文件信息到 t_core_file 表
            if (!StringUtils.isEmpty(tempFilePath)) {
                CoreFileBean coreFileBean = new CoreFileBean();
                coreFileBean.setTargetId("T_EMC_RUN" + "$" + targetRun.getId());
                coreFileBean.setScope(FileScope.secure.name());
                coreFileBean.setExt("composeAllPdf");
                coreFileService.upload(coreFileBean, new File(tempFilePath));
            }

            // 合成成功，更新合成状态
            targetRun.setExt$Item("composeStatus", "done");
            getDao().update(targetRun, "composeStatus");

            long endTime = System.currentTimeMillis();
            double duration = (endTime - startTime) / 1_000.0;
            paramMap.put("duration", duration);

            // CoreNotificationEvent completeEvent = new CoreNotificationEvent(success ? COMPOSE_ALL_RUNLIST_REPORT_SUCCESS : COMPOSE_ALL_RUNLIST_REPORT_FAILURE,
            //         Collections.singletonList(noticeUserId), null, LocalDateTime.now(), paramMap);
            // ApplicationContextHelper.getApplicationContext().publishEvent(completeEvent);
        });

        // 给pdf谱图增加分析员签名
        /*ExecutorHelper.submit(() -> {
            List<String> targetIdList = runIdList.stream().map(id -> "T_EMC_EQUIPT$" + id).collect(Collectors.toList());
            List<CoreFileBean> runEquiptFileList = coreFileService.getDao().selectListByOneColumnValues(targetIdList, "targetId");
            List<CoreFileBean> pdfFileList = runEquiptFileList.stream().filter(f -> StringUtils.equals(f.getFileExt(), "pdf")).collect(Collectors.toList());
            List<String> signedFileList = pdfFileList.stream().filter(f -> StringUtils.equals(f.getExt(), "signedPdf")).map(f -> StringUtils.replace(f.getName(), "signed_", "")).collect(Collectors.toList());
            List<CoreFileBean> unsignedFileList = pdfFileList.stream().filter(f -> !StringUtils.equals(f.getExt(), "signedPdf") && !signedFileList.contains(f.getName())).collect(Collectors.toList());
            if (!unsignedFileList.isEmpty()) {
                List<String> analysisUserIdList = validRunList.stream().map(EmcRunBean::getAnalysisUserId).distinct().map(id -> "T_CORE_USER$" + id).collect(Collectors.toList());
                List<CoreFileBean> userSignImageList = coreFileService.getDao().selectListByOneColumnValues(analysisUserIdList, "targetId");

                validRunList.forEach(run -> unsignedFileList.stream().filter(file -> StringUtils.endsWith(file.getTargetId(), run.getId())).forEach(file -> {
                    CoreFileBean signImage = userSignImageList.stream().filter(image -> StringUtils.endsWith(image.getTargetId(), run.getAnalysisUserId())).findFirst().orElse(null);
                    if (signImage == null) {
                        return;
                    }

                    String signedPdfName = "签名版_" + file.getName();
                    FileRepresentation signedPdfFile = FileRepresentation.of(FileScope.temp.name(), "" + System.currentTimeMillis(), StringUtils.randomUUID(8), signedPdfName);
                    File parentFile = FileHelper.getAbsoluteDirPath(signedPdfFile).toFile();
                    if (!parentFile.exists()) {
                        parentFile.mkdirs();
                    }

                    CoreFileBean coreFileBean = new CoreFileBean();
                    coreFileBean.setTargetId("T_EMC_EQUIPT$" + run.getId());
                    coreFileBean.setScope(FileScope.secure.name());
                    coreFileBean.setExt("signedPdf");

                    EmcPdfUtils.addImgInFooter(FileHelper.toFilePath(file), signedPdfFile, FileHelper.toFilePath(signImage), "分析员：");
                    coreFileService.upload(coreFileBean, FileHelper.getAbsolutePath(signedPdfFile).toFile());
                }));
            }
        });*/
    }

    @Override
    public Page<EmcItemBean> selectBatchEditItem(RestJsonWrapperBean wrapper) {
        String orderTaskIds = wrapper.getFilterValue("id_IN");
        List<Long> orderTaskIdList = Arrays.stream(StringUtils.split(orderTaskIds, ",")).filter(NumberUtils::isNumber).mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());

        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderTaskIdList, "id", Arrays.asList("id", "schemeName", "testId", "testMethodId", "orderId", "monitorTmplId"));

        // 获取分析项组
        List<EmcOrderItemGroupBean> filterList = new ArrayList<>(orderTaskList.size());
        orderTaskList.forEach(orderTask -> {
            EmcOrderItemGroupBean filter = new EmcOrderItemGroupBean();
            filter.setTestId(orderTask.getTestId());
            filter.setTestMethodId(orderTask.getTestMethodId());
            filter.setMonitorTmplId(orderTask.getMonitorTmplId());
            filter.setOrderId(orderTask.getOrderId());
            filterList.add(filter);
        });

        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectList(filterList, Arrays.asList("orderId", "testId", "testMethodId", "monitorTmplId"), Collections.emptyList());
        if (orderItemGroupList.isEmpty()) {
            return null;
        }

        List<Long> capacityItemGroupIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
        List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByIds(capacityItemGroupIdList, Arrays.asList("id", "itemGroupId"));
        List<Long> itemGroupIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
        if (itemGroupIdList.isEmpty()) {
            return null;
        }

        // 获取分析项
        List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID", Arrays.asList("id", "itemId"));
        List<Long> itemIdList = groupItemList.stream().map(EmcGroupItemBean::getItemId).distinct().collect(Collectors.toList());
        if (itemIdList.isEmpty()) {
            return null;
        }

        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "orderTaskId", Arrays.asList("id", "itemId"));
        List<Long> resultItemIdList = orderTaskResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());

        List<EmcItemBean> itemList = itemService.selectListByFilter(SearchFilter.instance().match("ID", itemIdList).filter(MatchPattern.OR));
        // 筛选类型是分析的,并且启用的分析项
        List<EmcItemBean> selectItemList = itemList.stream().filter(a -> "analysis".equals(a.getAnalysisCategory())
                && Constant.ACTIVATED_STATUS_YES.equals(a.getActivatedFlag())).collect(Collectors.toList());

        selectItemList.stream().filter(item -> resultItemIdList.contains(item.getId())).forEach(item -> item.setExt$Item("selected", Constant.YES));

        return new Pagination<>(selectItemList);
    }

    @Override
    @Transactional
    public void batchEditItem(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        if (orderTaskList.isEmpty()) {
            return;
        }

        List<EmcItemBean> itemList = wrapper.parse(EmcItemBean.class);
        List<Long> testMethodIdList = itemList.stream().map(EmcItemBean::getTestMethodId).distinct().collect(Collectors.toList());

        List<Long> orderIdList = orderTaskList.stream().map(EmcOrderTaskBean::getOrderId).collect(Collectors.toList());
        String runId = orderTaskList.get(0).getRunId();

        // 查询样品测试结果
        List<EmcOrderTaskResultBean> filterResultList = new ArrayList<>(orderIdList.size());
        orderIdList.forEach(orderId -> {
            EmcOrderTaskResultBean filterResult = new EmcOrderTaskResultBean();
            filterResult.setOrderId(orderId);
            filterResult.setRunId(runId);
            filterResultList.add(filterResult);
        });
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectList(filterResultList, Arrays.asList("orderId", "runId"), Collections.emptyList());
        List<EmcOrderTaskResultBean> validOrderTaskResultList = orderTaskResultList.stream().filter(r -> testMethodIdList.contains(r.getTestMethodId())).collect(Collectors.toList());

        Map<Long, List<EmcOrderTaskResultBean>> orderResultMap = validOrderTaskResultList.stream().collect(Collectors.groupingBy(EmcOrderTaskResultBean::getOrderId));
        List<EmcOrderBean> orderList = orderService.getDao().selectListByIds(orderIdList);
        Map<Long, EmcOrderBean> orderMap = orderList.stream().collect(Collectors.toMap(EmcOrderBean::getId, Function.identity()));

        List<Long> deletedResultIdList = new ArrayList<>();
        List<EmcOrderTaskResultBean> insertResultList = new ArrayList<>();

        orderResultMap.forEach((orderId, orderResultList) -> {
            Map<Long, List<EmcOrderTaskResultBean>> testMethodResultMap = orderResultList.stream().collect(Collectors.groupingBy(EmcOrderTaskResultBean::getTestMethodId));
            EmcOrderBean curOrder = Optional.ofNullable(orderMap.get(orderId)).orElseGet(EmcOrderBean::new);
            Long sampleNum = curOrder.getSampleNum();

            testMethodResultMap.forEach((testMethodId, curResultList) -> {
                List<EmcItemBean> curItemList = itemList.stream().filter(item -> ObjectUtils.equals(testMethodId, item.getTestMethodId())).collect(Collectors.toList());
                List<Long> curItemIdList = curItemList.stream().map(EmcItemBean::getId).collect(Collectors.toList());

                // 当前测试需要删除的结果数据
                List<Long> curDeleteResultIdList = curResultList.stream().filter(r -> !curItemIdList.contains(r.getItemId())).map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
                deletedResultIdList.addAll(curDeleteResultIdList);

                List<Long> resultItemIdList = curResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
                List<EmcItemBean> curInsertItemList = curItemList.stream().filter(item -> !resultItemIdList.contains(item.getId())).collect(Collectors.toList());

                curInsertItemList.forEach(item -> {
                    String pnFunc = item.getPnFunc();
                    // 如果分析项PN函数类型是日均值或者次均值且采样数不等于一的,不用插入分析项
                    if ((EmcConstant.PN_PERAVERAGE.equalsIgnoreCase(pnFunc) || EmcConstant.PN_DAYAVERAGE.equalsIgnoreCase(pnFunc)) && !ObjectUtils.equals(1L, sampleNum)) {
                        return;
                    }

                    Long repeatItem = Optional.ofNullable(item.getRepeatItem()).orElse(EmcConstant.ONE);
                    EmcOrderTaskResultBean copiedResult = curResultList.get(0);

                    LongStream.range(0, repeatItem).forEach(index -> {
                        EmcOrderTaskResultBean newOrderTaskResult = new EmcOrderTaskResultBean();

                        BeanUtils.copyProperties(copiedResult, newOrderTaskResult, EmcConstant.CREATED_COLUMNS);
                        newOrderTaskResult.setTestMethodId(item.getTestMethodId());
                        newOrderTaskResult.setItemId(item.getId());
                        newOrderTaskResult.setItemOrderNo(item.getOrderNo());
                        newOrderTaskResult.setItemName(item.getItemName());
                        newOrderTaskResult.setItemAliasName(item.getItemAliasName());
                        newOrderTaskResult.setScriptFlag(item.getScriptFlag());
                        newOrderTaskResult.setResultCategory(item.getResultCategory());
                        newOrderTaskResult.setRevisionRule(item.getRevisionRule());
                        newOrderTaskResult.setUnitName(item.getUnitName());
                        newOrderTaskResult.setReportDisplay(item.getReportDisplay());
                        newOrderTaskResult.setLowLimit(item.getLowLimit());
                        newOrderTaskResult.setHighLimit(item.getHighLimit());
                        newOrderTaskResult.setOriginalResult(item.getDefaultResult());
                        newOrderTaskResult.setReportResult(null);
                        newOrderTaskResult.setAvgResult(null);

                        newOrderTaskResult.setRepeatNo(NumberUtils.parseLong(index + 1));
                        newOrderTaskResult.setId(ApplicationContextHelper.getNextIdentity());

                        insertResultList.add(newOrderTaskResult);
                    });
                });
            });
        });

        orderTaskResultService.getDao().insert(insertResultList);
        orderTaskResultService.getDao().deleteByIdList(deletedResultIdList);

        // 对于有默认值的结果，触发一次公式计算
        List<Long> defaultResultIdList = insertResultList.stream().filter(r -> !StringUtils.isEmpty(r.getOriginalResult())).map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
        if (!defaultResultIdList.isEmpty()) {
            asyncFormulaComputingService.asyncComputing(runId, defaultResultIdList, null, false);
        }
    }

    @Override
    public void checkPass(RestJsonWrapperBean wrapper) {
        EmcProjectBean project = wrapper.parseUnique(EmcProjectBean.class);
        List<EmcRunBean> selectedRunList = wrapper.parse(EmcRunBean.class);

        // 查询当前项目，所提交批次下的样品测试
        List<String> selectedRunIdList = selectedRunList.stream().map(EmcRunBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> filterOrderTaskList = new ArrayList<>();
        selectedRunIdList.forEach(runId -> {
            EmcOrderTaskBean filterOrderTask = new EmcOrderTaskBean();
            filterOrderTask.setRunId(runId);
            filterOrderTask.setAnalysisType(EmcBizConstant.ANALYSISCATEGORY_ANALYSIS);
            filterOrderTask.setProjId(project.getId());
            filterOrderTaskList.add(filterOrderTask);
        });

        List<EmcOrderTaskBean> analysisOrderTaskList = orderTaskService.getDao().selectList(filterOrderTaskList, Arrays.asList("runId", "analysisType", "projId"), Collections.emptyList());

        // 当前用户拥有权限的科室
        String userId = LocalContextHelper.getLoginUserId();
        CoreUserPermissionBean filter = new CoreUserPermissionBean();
        filter.setUserId(userId);
        filter.setPermissionTypeId("T_CORE_ORG");
        List<CoreUserPermissionBean> userPermissionList = userPermissionService.getDao().selectList(filter);
        List<String> orgIdList = userPermissionList.stream().map(CoreUserPermissionBean::getTargetId).collect(Collectors.toList());

        List<EmcOrderTaskBean> validOrderTaskList = analysisOrderTaskList.stream().filter(a -> orgIdList.contains(a.getAnalysisOrgId())).collect(Collectors.toList());

        if (validOrderTaskList.isEmpty()) {
            return;
        }

        // 插入样品测试关联批的审核人信息
        List<String> runIdList = validOrderTaskList.stream().filter(ok -> !StringUtils.isEmpty(ok.getRunId())).map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());
        List<EmcRunBean> runList = getDao().selectListByOneColumnValues(runIdList, "id", Arrays.asList("id", "checkStatus", "auditedUserId"));
        List<EmcRunBean> updateRunList = runList.stream().filter(run -> !StringUtils.equals(run.getCheckStatus(), Constant.YES) && StringUtils.isEmpty(run.getAuditedUserId())).collect(Collectors.toList());

        if (updateRunList.isEmpty()) {
            return;
        }

        EmcRunBean updateRun = new EmcRunBean();
        updateRun.setCheckStatus(Constant.YES);
        updateRun.setAuditedUserId(LocalContextHelper.getLoginUserId());
        updateRun.setAuditedUserName(LocalContextHelper.getLoginUser().getUserName());
        updateRun.setAuditedDate(LocalDateTime.now());

        List<String> updateRunIdList = updateRunList.stream().map(EmcRunBean::getId).collect(Collectors.toList());
        getDao().updateByIds(updateRun, updateRunIdList, "checkStatus", "AUDITEDUSERID", "AUDITEDUSERNAME", "AUDITEDDATE");
    }

    @Override
    public String checkWeightingStatus(RestJsonWrapperBean wrapper) {
        //提交批之前校验称重状态是否为2，已完成称重，否则不允许提交
        String message = "";
        List<EmcRunBean> runList = wrapper.parse(EmcRunBean.class);
        List<String> runIdList = runList.stream().map(EmcRunBean::getId).distinct().collect(Collectors.toList());
        List<EmcRunBean> runAllList = getDao().selectListByIds(runIdList);
        List<EmcRunBean> runNoWeightList = runAllList.stream().filter(r -> !StringUtils.equals("2", r.getExt$Item("weighingStatus"))).collect(Collectors.toList());

        List<EmcRunBean> runNoWeightMatchList = runNoWeightList.stream().filter(r -> !r.getOrderCategory().contains("气")).filter(r -> !r.getOrderCategory().contains("声"))
                .filter(r -> !r.getOrderCategory().contains("水")).collect(Collectors.toList());

        if (!runNoWeightMatchList.isEmpty()) {
            String noWeightRunId = runNoWeightMatchList.stream().map(EmcRunBean::getId).distinct().collect(Collectors.joining("、"));
            message = I18nHelper.getMessage("EMC.MODULE.BP.RESULT.RUNS.TIP.NOT_COMPLETE_WEIGHTING", noWeightRunId);
        }
        return message;
    }

    @Override
    public void syncVolume(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskResultBean> resultList = wrapper.parse(EmcOrderTaskResultBean.class);
        String runId = resultList.get(0).getRunId();
        if (runId == null || runId == "") {
            return;
        }
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValue(runId, "runId");
        List<Long> projIdOrgList = orderTaskList.stream().map(EmcOrderTaskBean::getProjId).distinct().collect(Collectors.toList());
        List<Long> projIdList = projIdOrgList.stream().filter(item -> !ObjectUtils.isEmpty(item)).collect(Collectors.toList());
        List<EmcProjectBean> projList = projectService.getDao().selectListByIds(projIdList);
        //送样项目
        List<EmcProjectBean> noSampleProjList = projList.stream().filter(p -> StringUtils.equals("0", p.getSample())).collect(Collectors.toList());
        List<Long> noSampleProjIdList = noSampleProjList.stream().map(EmcProjectBean::getId).distinct().collect(Collectors.toList());

        List<EmcOrderTaskBean> orderTaskFilterList = orderTaskList.stream().filter(ot -> noSampleProjIdList.contains(ot.getProjId())).collect(Collectors.toList());
        List<Long> orderTaskFilterIdList = orderTaskFilterList.stream().map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());

        List<Long> orderIdList = orderTaskFilterList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> orderList = orderService.getDao().selectListByIds(orderIdList);
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskFilterIdList, "orderTaskId");
        List<Long> itemIdList = orderTaskResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
        List<EmcItemBean> itemList = itemService.getDao().selectListByIds(itemIdList);
        List<EmcItemBean> itemFilterList = itemList.stream().filter(i -> StringUtils.equals("1", i.getExt$Item("ISSENDTORESULT"))).distinct().collect(Collectors.toList());
        List<Long> itemFilterIdList = itemFilterList.stream().map(EmcItemBean::getId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultContainItemList = orderTaskResultList.stream().filter(res -> itemFilterIdList.contains(res.getItemId())).collect(Collectors.toList());
        orderTaskResultContainItemList.forEach(ot -> {
            List<EmcOrderBean> orderForThisList = orderList.stream().filter(o -> ObjectUtils.equals(o.getId(), ot.getOrderId())).collect(Collectors.toList());
            if (orderForThisList.isEmpty()) {
                return;
            }
            String volume = orderForThisList.get(0).getExt$Item("volume");
            if (!StringUtils.isEmpty(volume)) {
                volume = volume.replaceAll("[^0-9.]", "");
            } else {
                return;
            }

            List<EmcItemBean> itemLForThisist = itemFilterList.stream().filter(i -> ObjectUtils.equals(i.getId(), ot.getItemId())).collect(Collectors.toList());
            if (!StringUtils.isEmpty(itemLForThisist.get(0).getExt$Item("ISSENDTORESULT")) && !StringUtils.isEmpty(volume)) {
                if (itemLForThisist.get(0).getExt$Item("ISSENDTORESULT").equals("1")) {
                    if (!StringUtils.isEmpty(itemLForThisist.get(0).getExt$Item("SENDCOEFFICIENT"))) {
                      /*  BigDecimal volume1 = new BigDecimal(String.valueOf(volume.trim()));
                        BigDecimal sendCofficient = new BigDecimal(String.valueOf(itemLForThisist.get(0).getExt$Item("SENDCOEFFICIENT")));
                        BigDecimal volumeF = volume1.divide(sendCofficient,Integer.valueOf(numBit),BigDecimal.ROUND_UNNECESSARY);*/
                        Double volumeF = ArithUtils.div(volume.trim(), itemLForThisist.get(0).getExt$Item("SENDCOEFFICIENT"));

                        ot.setOriginalResult(volumeF.toString() + "");
                        ot.setReportResult(volumeF.toString() + "");
                    } else {
                        ot.setOriginalResult(volume);
                        ot.setReportResult(volume);

                    }
                }
            }


        });
        orderTaskResultService.getDao().update(orderTaskResultContainItemList, "originalResult", "reportResult");

    }

    @Override
    public void chooseBeforeHandle(RestJsonWrapperBean wrapper) {
        String runId = wrapper.getFilterValue("runId");
        String chooseBeforeHander = wrapper.getFilterValue("chooseBeforeHander");
        EmcRunBean run = getDao().selectById(runId);

        List<EmcTestMethodBean> testMethodList = testMethodService.selectListByFilter(SearchFilter.instance().match("methodId", run.getMethodId()).filter(MatchPattern.EQ));

        if (chooseBeforeHander.equals("beforeHandle1")) {
            if (testMethodList.get(0).getBeforeHandle() != null && testMethodList.get(0).getBeforeHandle() != "") {
                run.setBeforeHandle(testMethodList.get(0).getBeforeHandle());
            }
        } else if (chooseBeforeHander.equals("beforeHandle2")) {
            if (testMethodList.get(0).getExt$Item("beforehandle2") != null && testMethodList.get(0).getExt$Item("beforehandle2") != "") {
                run.setBeforeHandle(testMethodList.get(0).getExt$Item("beforehandle2"));
            }
        }
        getDao().update(run, "beforeHandle");
    }

    @Override
    public void chooseLimit(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskResultBean> resultList = wrapper.parse(EmcOrderTaskResultBean.class);
        List<Long> resultIdList = resultList.stream().map(EmcOrderTaskResultBean::getId).distinct().collect(Collectors.toList());
        List<Long> itemIdList = resultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
        String chooseLimit = wrapper.getFilterValue("chooseLimit");
        String runId = wrapper.getFilterValue("runId");
        EmcRunBean run = getDao().selectById(runId);
        List<EmcOrderTaskResultBean> resultFilterList = orderTaskResultService.getDao().selectListByIds(resultIdList);
        List<EmcTestMethodBean> testMethodList = testMethodService.getDao().selectListByOneColumnValue(run.getMethodId(), "methodId");
        List<EmcItemBean> itemList = itemService.getDao().selectListByIds(itemIdList);
        if (chooseLimit.equals("chooseLimit1")) {
            resultFilterList.forEach(res -> {
                List<EmcTestMethodBean> testMethodMatchList = testMethodList.stream().filter(tm -> ObjectUtils.equals(res.getTestMethodId(), tm.getId())).collect(Collectors.toList());
                List<EmcItemBean> item2List = itemList.stream().filter(i -> ObjectUtils.equals(res.getItemId(), i.getId())).collect(Collectors.toList());
                List<EmcItemBean> itemFilterList = item2List.stream().filter(i -> ObjectUtils.equals(testMethodMatchList.get(0).getId(), i.getTestMethodId())).collect(Collectors.toList());
                if (itemFilterList.isEmpty()) {
                    return;
                }
                if (itemFilterList.get(0).getLowLimit() != null && itemFilterList.get(0).getLowLimit() != "") {
                    res.setLowLimit(itemFilterList.get(0).getLowLimit());
                }
            });
        } else if (chooseLimit.equals("chooseLimit2")) {
            resultFilterList.forEach(res -> {
                List<EmcTestMethodBean> testMethodMatchList = testMethodList.stream().filter(tm -> ObjectUtils.equals(res.getTestMethodId(), tm.getId())).collect(Collectors.toList());
                List<EmcItemBean> item2List = itemList.stream().filter(i -> ObjectUtils.equals(res.getItemId(), i.getId())).collect(Collectors.toList());
                List<EmcItemBean> itemFilterList = item2List.stream().filter(i -> ObjectUtils.equals(testMethodMatchList.get(0).getId(), i.getTestMethodId())).collect(Collectors.toList());
                if (itemFilterList.isEmpty()) {
                    return;
                }
                if (itemFilterList.get(0).getExt$Item("lowLimit2") != null && itemFilterList.get(0).getExt$Item("lowLimit2") != "") {
                    res.setLowLimit(itemFilterList.get(0).getExt$Item("lowLimit2"));
                }
            });
        }
        orderTaskResultService.getDao().update(resultFilterList, "lowLimit");
    }

    @Override
    public void setDefaultCurveValue(RestJsonWrapperBean wrapper) {
        List<EmcCurveBean> curveList = wrapper.parse(EmcCurveBean.class);
        String runId = wrapper.getParamValue("runId");

        List<EmcOrderTaskResultBean> resultList = orderTaskResultService.selectListByFilter(SearchFilter.instance().match("runId", runId).filter(MatchPattern.SEQ)
                .match("qcCategoryCode", Arrays.asList(PfConstant.QC_QXJH, PfConstant.QCCATEGORY_QCCATGORYID_L_SPKC)).filter(MatchPattern.OR));

        if (resultList.isEmpty()) {
            return;
        }

        String curveValue = curveList.stream().filter(curve -> !StringUtils.isEmpty(curve.getExt$Item("curveValue"))).findFirst().map(curve -> curve.getExt$Item("curveValue")).orElse(null);
        String standQty = curveList.stream().filter(curve -> !StringUtils.isEmpty(curve.getExt$Item("standQty"))).findFirst().map(curve -> curve.getExt$Item("standQty")).orElse(null);
        String standQtyUnit = curveList.stream().filter(curve -> !StringUtils.isEmpty(curve.getExt$Item("standQtyUnit"))).findFirst().map(curve -> curve.getExt$Item("standQtyUnit")).orElse(null);

        List<EmcOrderTaskResultBean> updateResultList = new ArrayList<>();
        if (!StringUtils.isEmpty(curveValue)) {
            List<EmcOrderTaskResultBean> curveResultList = resultList.stream().filter(r -> PfConstant.QC_QXJH.equals(r.getQcCategoryCode()) && Constant.YES.equals(r.getExt$Item("calQcResult"))).collect(Collectors.toList());
            curveResultList.forEach(r -> r.setStandQty(curveValue));
            updateResultList.addAll(curveResultList);
        }

        if (!StringUtils.isEmpty(standQty)) {
            List<EmcOrderTaskResultBean> stdResultList = resultList.stream().filter(r -> PfConstant.QCCATEGORY_QCCATGORYID_L_SPKC.equals(r.getQcCategoryCode()) && Constant.YES.equals(r.getExt$Item("calQcResult"))).collect(Collectors.toList());
            stdResultList.forEach(r -> {
                r.setStandQty(standQty);
                r.setRn12(standQtyUnit);
            });
            updateResultList.addAll(stdResultList);
        }

        orderTaskResultService.getDao().update(updateResultList, "standQty", "rn12");
    }

    @Override
    public String selectComposeFlag(RestJsonWrapperBean wrapper) {

        String composeFlag = "";
        EmcRunBean run = wrapper.parseUnique(EmcRunBean.class);
        String runId = run.getId();
        //查询数据库中的合并状态
        List<EmcRunBean> runList = selectListByFilter(SearchFilter.instance().match("id", runId).filter(MatchPattern.SEQ));
        composeFlag = runList.get(0).getExt$Item("COMPOSEFLAG");
        //合并完成后合并状态会置为0，状态为1说明正在合成
        if (composeFlag == null || composeFlag.isEmpty()) {
            EmcRunBean updateRun = new EmcRunBean();
            updateRun.setId(run.getId());
            updateRun.setExt$Item("COMPOSEFLAG", "1");
            getDao().update(updateRun, "COMPOSEFLAG");
            return "OFF";//前端继续合并原始记录，并把状态置为1
        } else {
            if (composeFlag.equals("1")) {
                return "ON";//前端不执行合并原始记录，返回提示，状态不变
            } else {
                EmcRunBean updateRun = new EmcRunBean();
                updateRun.setId(run.getId());
                updateRun.setExt$Item("COMPOSEFLAG", "1");
                getDao().update(updateRun, "COMPOSEFLAG");
                return "OFF";//前端继续合并原始记录，并把状态置为1
            }
        }

    }


/*    @Override
    public void syncOliFilm(RestJsonWrapperBean wrapper) {
        EmcRunBean orgRun = wrapper.parseUnique(EmcRunBean.class);
        String runId = orgRun.getId();
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValue(runId, "runId", Arrays.asList("id", "projId","testMethodId","orderId"));
        //获取该批下的与滤筒信息配置的样品一样的
        List<Long>  orderTaskResultOrderIdList= orderTaskResultList.stream().map(EmcOrderTaskResultBean :: getOrderId).distinct().collect(Collectors.toList());
        //对应的滤膜信息
        List<EmcOilFilmBean> oliBeanList = oilFilmService.getDao().selectListByOneColumnValues(orderTaskResultOrderIdList, "orderId");
        List<EmcOrderTaskResultBean> orderTaskResultUpdateList = new ArrayList<>();
        oliBeanList.forEach(oli ->{
        	 List<EmcOrderTaskResultBean> orderTaskResultTargetList = orderTaskResultList.stream().filter(res ->ObjectUtils.equals(oli.getOrderId(), res.getOrderId())).collect(Collectors.toList());
        	 if(!orderTaskResultTargetList.isEmpty()){
            	 orderTaskResultTargetList.forEach(res ->{
            		 res.setExt$Item("EMPTYFILTERBARRELMONEONE", oli.getEmptyFilterBarrelMOneOne());
            		 res.setExt$Item("EMPTYFILTERBARRELMONETWO", oli.getEmptyFilterBarrelMOneTwo());
            		 res.setExt$Item("EMPTYFILTERBARRELMONETHREE", oli.getEmptyFilterBarrelMOneThree());
            		 res.setExt$Item("EMPTYFILTERBARRELMONEFOUR", oli.getEmptyFilterBarrelMOneFour());
            		 res.setExt$Item("EMPTYFILTERBARRELMONEFIVE", oli.getEmptyFilterBarrelMOneFive());
            		 res.setExt$Item("EMPTYFILTERBARRELMONESIX", oli.getEmptyFilterBarrelMOneSix());
            	 });
            	 orderTaskResultUpdateList.addAll(orderTaskResultTargetList);
            	 orderTaskResultService.getDao().update(orderTaskResultUpdateList, "EMPTYFILTERBARRELMONEONE","EMPTYFILTERBARRELMONETWO","EMPTYFILTERBARRELMONETHREE","EMPTYFILTERBARRELMONEFOUR","EMPTYFILTERBARRELMONEFIVE","EMPTYFILTERBARRELMONESIX");;
        	 }

        });

    }*/

    @Override
    public void syncOliFilm(RestJsonWrapperBean wrapper) {
        EmcRunBean orgRun = wrapper.parseUnique(EmcRunBean.class);
        String runId = orgRun.getId();
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValue(runId, "runId", Arrays.asList("id", "projId", "testMethodId", "orderId"));
        //获取该批下的与滤筒信息配置的样品一样的
        List<Long> orderTaskOrderIdList = orderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        //对应的滤膜信息
        List<EmcOrderBean> orderBeanList = orderService.getDao().selectListByIds(orderTaskOrderIdList);
        List<EmcOrderTaskBean> orderTaskUpdateList = new ArrayList<>();
        orderTaskList.forEach(ok -> {
            List<EmcOrderBean> orderNeedList = orderBeanList.stream().filter(o -> ObjectUtils.equals(ok.getOrderId(), o.getId())).collect(Collectors.toList());
            ok.setExt$Item("OILFILMNO", orderNeedList.get(0).getExt$Item("OILFILMNO"));
            orderTaskUpdateList.add(ok);
        });
        orderTaskService.getDao().update(orderTaskUpdateList, "OILFILMNO");
    }

    @Override
    @Transactional
    @ValidateDataWith(PfDelQcTaskValidator.class)
    public void deleteQcOrdertask(RestJsonWrapperBean wrapper) {
        //删除质控样下的部分样品测试(仅分析)
        //需删除ordertask表、ordertaskresult表
        List<EmcOrderTaskResultBean> orderTaskResultList = wrapper.parse(EmcOrderTaskResultBean.class);
        List<Long> orderTaskIdList = orderTaskResultList.stream().map(EmcOrderTaskResultBean::getOrderTaskId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskAllList = orderTaskService.getDao().selectListByIds(orderTaskIdList);
        List<EmcOrderTaskResultBean> orderTaskAllResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "orderTaskId");
        if (!orderTaskAllResultList.isEmpty()) {
            orderTaskResultService.getDao().deleteBy(orderTaskAllResultList);
        }
        if (orderTaskAllList.isEmpty()) {
            orderTaskService.getDao().deleteBy(orderTaskAllList);
        }

        return;
    }

    @Override
    @Transactional
    public void resetCompose(RestJsonWrapperBean wrapper) {
        List<EmcRunBean> runList = wrapper.parse(EmcRunBean.class);
        List<String> runIdList = runList.stream().map(EmcRunBean::getId).distinct().collect(Collectors.toList());
        List<EmcRunBean> updateRunList = getDao().selectListByIds(runIdList);
        updateRunList.forEach(r -> {
            r.setExt$Item("COMPOSEFLAG", "0");
        });
        getDao().update(updateRunList, "COMPOSEFLAG");
    }

    @Override
    @Transactional
    public String selectNotInItemgroup(RestJsonWrapperBean wrapper) {

        String runId = wrapper.getFilterValue("runId");
        EmcOrderTaskBean orderTaskBean = wrapper.parseUnique(EmcOrderTaskBean.class);
        Long orderId = orderTaskBean.getOrderId();
        String itemGroupIdStr = "";

        if (!StringUtils.isEmpty(runId)) {
            //测试不会消失，只是分析项组没有，更新ordertaskresult表就行
            List<EmcOrderTaskBean> orderTasklist = orderTaskService.selectListByFilter(SearchFilter.instance()
                    .match("runId", runId).filter(MatchPattern.SEQ).match("orderId", orderId).filter(MatchPattern.EQ)
            );
            List<Long> testMethodIdList = orderTasklist.stream().map(EmcOrderTaskBean::getTestMethodId).distinct().collect(Collectors.toList());
            //测试结果所有包含去除runid的
            List<Long> ordertaskIdList = orderTasklist.stream().map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());
            List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(ordertaskIdList, "ordertaskid");
            List<EmcOrderTaskResultBean> orderTaskResultMatchList = orderTaskResultList.stream()
                    .filter(res -> StringUtils.equals(runId, res.getRunId())).filter(res -> StringUtils.equals("analysis", res.getAnalysisType())).collect(Collectors.toList());
            //找到此样品下批下的全部分析项组(包含分析录入编辑分析项组移除批的分析项组 或者还未建批的)
            List<EmcOrderItemGroupBean> orderItemGroupSingleOrderB = orderItemGroupService.getDao().selectListByOneColumnValue(orderId, "orderId");

            List<EmcOrderItemGroupBean> orderItemGroupSingleOrder = orderItemGroupSingleOrderB.stream().filter(oig -> StringUtils.equals("1", oig.getExt$Item("REMOVEFLAG"))).filter(oig -> testMethodIdList.contains(oig.getTestMethodId())).collect(Collectors.toList());
            if (orderItemGroupSingleOrder.isEmpty()) {
                return "";
            }
            List<Long> tcItemGroupIdList = orderItemGroupSingleOrder.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
            List<EmcCapacityItemGroupBean> tcItemGroupList = capacityItemGroupService.selectListByIds(tcItemGroupIdList);
            //该样品下相同测试方法所有的分析项组id
            List<Long> trueItemGroupIdList = tcItemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());

            itemGroupIdStr = StringUtils.join(trueItemGroupIdList, ",");

            return itemGroupIdStr;


          /*  //过滤得到该批下该样品下的没有被移除批的测试
            List<Long> itemIdList = orderTaskResultMatchList.stream().map(EmcOrderTaskResultBean :: getItemId).distinct().collect(Collectors.toList());
            //根据测试结果表的分析项找到对应的分析项组，再筛选样品的
            List<EmcGroupItemBean> giList = groupItemService.getDao().selectListByOneColumnValues(itemIdList, "itemId");
            //项目中有的
            List<EmcGroupItemBean> giMatchList = giList.stream().filter(gi ->trueItemGroupIdList.contains(gi.getItemGroupId())).collect(Collectors.toList());

            List<Long> itemGroupIdList = giMatchList.stream().map(EmcGroupItemBean :: getItemGroupId).distinct().collect(Collectors.toList());
            List<Long> noInItemGroupIdList = trueItemGroupIdList.stream().filter(t ->!itemGroupIdList.contains(t)).collect(Collectors.toList());
            itemGroupIdStr = StringUtils.join(noInItemGroupIdList, ",");*/

        }

        return itemGroupIdStr;

    }

    @Override
    @Transactional
    public String selectInItemgroup(RestJsonWrapperBean wrapper) {
        String runId = wrapper.getFilterValue("runId");
        EmcOrderTaskBean orderTaskBean = wrapper.parseUnique(EmcOrderTaskBean.class);
        Long orderId = orderTaskBean.getOrderId();
        String itemGroupIdStr = "";
        if (!StringUtils.isEmpty(runId)) {
            //测试不会消失，只是分析项组没有，更新ordertaskresult表就行
            List<EmcOrderTaskBean> orderTasklist = orderTaskService.selectListByFilter(SearchFilter.instance()
                    .match("runId", runId).filter(MatchPattern.SEQ).match("orderId", orderId).filter(MatchPattern.EQ)
            );
            //测试结果所有包含去除runid的
            List<Long> testMethodIdList = orderTasklist.stream().map(EmcOrderTaskBean::getTestMethodId).distinct().collect(Collectors.toList());
            List<Long> ordertaskIdList = orderTasklist.stream().map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());
            List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(ordertaskIdList, "ordertaskid");
            //
            List<EmcOrderTaskResultBean> orderTaskResultMatchList = orderTaskResultList.stream()
                    .filter(res -> StringUtils.equals(runId, res.getRunId())).filter(res -> StringUtils.equals("analysis", res.getAnalysisType())).collect(Collectors.toList());
            if (orderTaskResultMatchList.isEmpty()) {
                return "";
            }
            //找到此样品下批下的全部分析项组(包含分析录入编辑分析项组移除批的分析项组)
            List<EmcOrderItemGroupBean> orderItemGroupSingleOrderB = orderItemGroupService.getDao().selectListByOneColumnValue(orderId, "orderId");
            //排除移除的分析项组
            List<EmcOrderItemGroupBean> orderItemGroupSingleOrder = orderItemGroupSingleOrderB.stream().filter(oig -> !StringUtils.equals("1", oig.getExt$Item("REMOVEFLAG"))).filter(oig -> testMethodIdList.contains(oig.getTestMethodId())).collect(Collectors.toList());

            List<Long> tcItemGroupIdList = orderItemGroupSingleOrder.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
            List<EmcCapacityItemGroupBean> tcItemGroupList = capacityItemGroupService.selectListByIds(tcItemGroupIdList);
            List<Long> trueItemGroupIdList = tcItemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
            //过滤得到该批下该样品下的没有被移除批的测试
            List<Long> itemIdList = orderTaskResultMatchList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
            //根据测试结果表的分析项找到对应的分析项组，再筛选样品的
            List<EmcGroupItemBean> giList = groupItemService.getDao().selectListByOneColumnValues(itemIdList, "itemId");
            List<EmcGroupItemBean> giMatchList = giList.stream().filter(gi -> trueItemGroupIdList.contains(gi.getItemGroupId())).collect(Collectors.toList());

            List<Long> itemGroupIdList = giMatchList.stream().map(EmcGroupItemBean::getItemGroupId).distinct().collect(Collectors.toList());
            itemGroupIdStr = StringUtils.join(itemGroupIdList, ",");
        }

        return itemGroupIdStr;

    }

    @Override
    public Page<EmcOrderItemGroupBean> selectUniqueOrderItemGroup(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance(), wrapper);
        List<Long> orderIdList = orderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<Long> testMethodIdList = orderTaskList.stream().map(EmcOrderTaskBean::getTestMethodId).distinct().collect(Collectors.toList());
        orderTaskList.clear();

        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.selectListByFilter(SearchFilter.instance().match("orderId", orderIdList).filter(MatchPattern.OR).match("testMethodId", testMethodIdList).filter(MatchPattern.OR));

        ArrayList<EmcOrderItemGroupBean> uniqueItemGroupList = orderItemGroupList.stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmcOrderItemGroupBean::getItemGroupId))), ArrayList::new));

        PageRowBounds rowBounds = wrapper.extractPageRowBounds();

        return MybatisPageHelper.get(rowBounds, () -> uniqueItemGroupList);
    }

    @Override
    public void combineOrderItemGroup(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> targetTaskList = wrapper.parse(EmcOrderTaskBean.class, 0);
        List<EmcOrderItemGroupBean> selectedItemGroupList = wrapper.parse(EmcOrderItemGroupBean.class, 1);

        List<Long> targetOrderIdList = targetTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<String> targetRunIdList = targetTaskList.stream().filter(t -> !StringUtils.isEmpty(t.getRunId())).map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());

        List<EmcOrderBean> targetOrderList = orderService.getDao().selectListByOneColumnValues(targetOrderIdList, "id", Arrays.asList("id", "orderNo", "parentOrderNo"));
        Map<Long, String> targetOrderIdNoMap = targetOrderList.stream().collect(Collectors.toMap(EmcOrderBean::getId, EmcOrderBean::getOrderNo));
        Map<Long, String> targetParentOrderIdNoMap = targetOrderList.stream().filter(order -> !StringUtils.isEmpty(order.getParentOrderNo())).collect(Collectors.toMap(EmcOrderBean::getId, EmcOrderBean::getParentOrderNo));

        List<EmcOrderTaskBean> originalOrderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("orderId", targetOrderIdList).filter(MatchPattern.OR)
                .match("runId", targetRunIdList).filter(MatchPattern.OR));
        List<Long> originalOrderTaskIdList = originalOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> originalResultList = orderTaskResultService.getDao().selectListByOneColumnValues(originalOrderTaskIdList, "orderTaskId", Arrays.asList("id", "orderTaskId", "itemId", "itemName", "originalResult", "reportResult"));

        List<Long> selectedCapacityItemGroupIdList = selectedItemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
        List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByOneColumnValues(selectedCapacityItemGroupIdList, "id", Arrays.asList("id", "itemGroupId"));
        List<Long> selectedItemGroupIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());

        List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(selectedItemGroupIdList, "itemGroupId", Arrays.asList("id", "itemGroupId", "itemId"));
        List<Long> selectedItemIdList = groupItemList.stream().map(EmcGroupItemBean::getItemId).distinct().collect(Collectors.toList());

        List<EmcOrderTaskBean> taskFilterList = new ArrayList<>(selectedItemGroupList.size());
        selectedItemGroupList.forEach(group -> {
            EmcOrderTaskBean filterTask = new EmcOrderTaskBean();
            filterTask.setOrderId(group.getOrderId());
            filterTask.setTestMethodId(group.getTestMethodId());
            taskFilterList.add(filterTask);
        });

        List<EmcOrderTaskBean> selectedTaskList = orderTaskService.getDao().selectList(taskFilterList, Arrays.asList("orderId", "testMethodId"), Collections.emptyList());
        List<Long> selectedTaskIdList = selectedTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> selectedResultList = orderTaskResultService.getDao().selectListByOneColumnValues(selectedTaskIdList, "orderTaskId").stream().filter(r -> selectedItemIdList.contains(r.getItemId())).collect(Collectors.toList());

        List<EmcItemBean> selectedItemList = itemService.getDao().selectListByOneColumnValues(selectedItemIdList, "id", Arrays.asList("id", "defaultResult", "useType", "qcCategoryId"));

        List<EmcOrderTaskBean> virtualTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> virtualResultList = new ArrayList<>();
        List<EmcOrderTaskBean> updateTaskList = new ArrayList<>();

        List<Long> defaultValueResultIdList = new ArrayList<>();

        // 如果同时存在正常样品和平行、加标这种子样时，需要先合并正常样品，再合并子样，这样才能更新子样的parentId
        targetTaskList.forEach(task -> task.setExt$Item("combineSortNum", ObjectUtils.isEmpty(task.getQcCategoryId()) ? "0" : String.valueOf(task.getQcCategoryId())));
        targetTaskList.sort(Comparator.comparing(task -> task.getExt$Item("combineSortNum")));
        Map<String, Long> testTaskIdMap = new HashMap<>();

        targetTaskList.forEach(task -> {
            List<EmcOrderTaskBean> curOriginalTaskList = originalOrderTaskList.stream().filter(originalTask -> StringUtils.equals(task.getRunId(), originalTask.getRunId())
                    && ObjectUtils.equals(task.getOrderId(), originalTask.getOrderId())).collect(Collectors.toList());
            List<Long> curOriginalTaskTestIdList = curOriginalTaskList.stream().map(EmcOrderTaskBean::getTestId).collect(Collectors.toList());

            updateTaskList.add(task);

            selectedTaskList.forEach(selectedTask -> {
                boolean existed = curOriginalTaskTestIdList.contains(selectedTask.getTestId());
                boolean isNa = EmcConstant.N_A.equals(task.getQcCategoryCode());
                boolean hasParent = !ObjectUtils.isEmpty(targetParentOrderIdNoMap.get(task.getOrderId()));
                if (!existed) {
                    EmcOrderTaskBean virtualTask = new EmcOrderTaskBean();
                    BeanUtils.copyProperties(selectedTask, virtualTask, PfConstant.CREATED_COLUMNS);
                    virtualTask.setId(ApplicationContextHelper.getNextIdentity());
                    virtualTask.setOrderId(task.getOrderId());
                    virtualTask.setProjId(task.getProjId());
                    virtualTask.setFolderId(task.getFolderId());
                    virtualTask.setQcCategoryId(task.getQcCategoryId());
                    virtualTask.setQcCategoryCode(task.getQcCategoryCode());
                    virtualTask.setQcName(task.getQcName());
                    virtualTask.setExt$Item("virtual", Constant.YES);
                    virtualTask.setRunId(task.getRunId());
                    virtualTask.setCupNo(task.getCupNo());
                    virtualTask.setEquiptAcquNo(task.getEquiptAcquNo());

                    if (isNa) {
                        String key = targetOrderIdNoMap.get(task.getOrderId()) + "$" + selectedTask.getTestId();
                        testTaskIdMap.put(key, virtualTask.getId());
                    }

                    // 实验室平行和样品加标
                    if (hasParent) {
                        String key = targetParentOrderIdNoMap.get(task.getOrderId()) + "$" + selectedTask.getTestId();
                        virtualTask.setParentId(testTaskIdMap.get(key));
                    }

                    virtualTaskList.add(virtualTask);
                }

                EmcOrderTaskBean curOriginalTask = curOriginalTaskList.stream().filter(t -> ObjectUtils.equals(t.getTestId(), selectedTask.getTestId())).findFirst().orElseGet(EmcOrderTaskBean::new);
                EmcOrderTaskBean curTask = existed ? curOriginalTask : virtualTaskList.get(virtualTaskList.size() - 1);

                List<EmcOrderTaskResultBean> curOriginalResultList = originalResultList.stream().filter(result -> ObjectUtils.equals(result.getOrderTaskId(), curTask.getId())).collect(Collectors.toList());

                // 在QC中登录的分析项
                List<EmcItemBean> curValidItemList = isNa ? selectedItemList : selectedItemList.stream().filter(item -> StringUtils.contains(item.getQcCategoryId(), String.valueOf(task.getQcCategoryId()))).collect(Collectors.toList());
                Map<Long, EmcItemBean> curValidItemMap = curValidItemList.stream().collect(Collectors.toMap(EmcItemBean::getId, Function.identity()));
                List<Long> curValidItemIdList = curValidItemList.stream().map(EmcItemBean::getId).collect(Collectors.toList());

                selectedResultList.stream().filter(result -> ObjectUtils.equals(selectedTask.getId(), result.getOrderTaskId()) && curValidItemIdList.contains(result.getItemId())).forEach(result -> {
                    // 如果原始结果中已有此分析项，则不添加
                    if (curOriginalResultList.stream().anyMatch(r -> ObjectUtils.equals(result.getItemId(), r.getItemId()))) {
                        return;
                    }

                    EmcOrderTaskResultBean virtualResult = new EmcOrderTaskResultBean();
                    BeanUtils.copyProperties(result, virtualResult, PfConstant.CREATED_COLUMNS);
                    virtualResult.setId(ApplicationContextHelper.getNextIdentity());
                    virtualResult.setOriginalResult(null);
                    virtualResult.setReportResult(null);
                    virtualResult.setAvgResult(null);

                    // 复制分析项默认值
                    EmcItemBean curItem = Optional.ofNullable(curValidItemMap.get(result.getItemId())).orElseGet(EmcItemBean::new);
                    if (!StringUtils.isEmpty(curItem.getDefaultResult())) {
                        virtualResult.setOriginalResult(curItem.getDefaultResult());
                        defaultValueResultIdList.add(virtualResult.getId());
                    }

                    // 复制称重类型分析项的值
                    if ("weighing".equals(curItem.getExt$Item("useType"))) {
                        curOriginalResultList.stream().filter(originalResult -> StringUtils.equals(virtualResult.getItemName(), originalResult.getItemName())
                                && !StringUtils.isEmpty(originalResult.getOriginalResult())).findFirst().ifPresent(originalResult -> {
                            virtualResult.setOriginalResult(originalResult.getOriginalResult());
                            virtualResult.setReportResult(originalResult.getReportResult());
                        });
                    }

                    virtualResult.setOrderId(task.getOrderId());
                    virtualResult.setOrderNo(targetOrderIdNoMap.get(task.getOrderId()));
                    virtualResult.setOrderTaskId(curTask.getId());
                    virtualResult.setProjId(task.getProjId());
                    virtualResult.setFolderId(task.getFolderId());
                    virtualResult.setRunId(task.getRunId());
                    virtualResult.setQcCategoryId(task.getQcCategoryId());
                    virtualResult.setQcCategoryCode(task.getQcCategoryCode());
                    virtualResult.setQcName(task.getQcName());
                    virtualResult.setExt$Item("virtual", Constant.YES);

                    if (hasParent) {
                        virtualResult.setParentOrderNo(targetParentOrderIdNoMap.get(task.getOrderId()));
                    }

                    virtualResultList.add(virtualResult);
                });
            });
        });

        // 更新样品表上的是否为原样字段
        if (!updateTaskList.isEmpty() && !virtualResultList.isEmpty()) {
            List<Long> orderIdList = updateTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
            List<EmcOrderBean> updateOrderList = new ArrayList<>(orderIdList.size());
            orderIdList.forEach(orderId -> {
                EmcOrderBean updateOrder = new EmcOrderBean();
                updateOrder.setId(orderId);
                updateOrder.setExt$Item("originalOrder", Constant.YES);
                updateOrderList.add(updateOrder);
            });

            orderService.getDao().update(updateOrderList, "originalOrder");
        }

        // 判断是否有空白加标质控，空白加标质控需要更新父样ID
        List<EmcOrderTaskBean> blankSpkcTaskList = virtualTaskList.stream().filter(task -> StringUtils.equals(task.getQcCategoryCode(), PfConstant.QC_L_SPKCK)).collect(Collectors.toList());
        if (!blankSpkcTaskList.isEmpty()) {
            String runId = blankSpkcTaskList.get(0).getRunId();
            String blankOrderNo = String.join("-", runId, PfConstant.QC_LBLANK_ORDER_NO_PREFIX);
            List<EmcOrderBean> blankOrderList = orderService.getDao().selectListByOneColumnValue(blankOrderNo, "orderNo", Arrays.asList("id", "orderNo"));
            if (!blankOrderList.isEmpty()) {
                Long blankOrderId = blankOrderList.get(0).getId();
                List<EmcOrderTaskBean> blankTaskList = orderTaskService.getDao().selectListByOneColumnValue(blankOrderId, "orderId", Arrays.asList("id", "testMethodId"));
                blankTaskList.forEach(bt -> virtualTaskList.stream().filter(bst -> StringUtils.equals(bst.getQcCategoryCode(), PfConstant.QC_L_SPKCK) && ObjectUtils.equals(bt.getTestMethodId(), bst.getTestMethodId())).forEach(bst -> bst.setParentId(bt.getId())));
            }
        }

        if (!virtualResultList.isEmpty()) {
            orderTaskService.getDao().insert(virtualTaskList);
            orderTaskResultService.getDao().insert(virtualResultList);
        }

        // 触发有默认值的公式计算
        if (!defaultValueResultIdList.isEmpty()) {
            List<Long> resultIdList = virtualResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
            asyncFormulaComputingService.asyncComputing(null, resultIdList, false);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelCombineOrderItemGroup(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<Long> virtualOrderIdList = orderTaskList.stream().filter(task -> StringUtils.equals(task.getExt$Item("originalorder"), Constant.YES)).map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        if (virtualOrderIdList.isEmpty()) {
            return;
        }

        List<EmcOrderBean> updateOrderList = new ArrayList<>(virtualOrderIdList.size());
        virtualOrderIdList.forEach(orderId -> {
            EmcOrderBean updateOrder = new EmcOrderBean();
            updateOrder.setId(orderId);
            updateOrder.setExt$Item("originalOrder", Constant.NO);
            updateOrderList.add(updateOrder);
        });

        String runId = orderTaskList.get(0).getRunId();

        List<EmcOrderTaskBean> allOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(virtualOrderIdList, "orderId", Arrays.asList("id", "orderId", "virtual"));
        List<EmcOrderTaskBean> virtualTaskList = allOrderTaskList.stream().filter(task -> StringUtils.equals(task.getExt$Item("virtual"), Constant.YES)).collect(Collectors.toList());

        List<EmcOrderTaskResultBean> filterResultList = new ArrayList<>(virtualOrderIdList.size());
        virtualOrderIdList.forEach(orderId -> {
            EmcOrderTaskResultBean filterResult = new EmcOrderTaskResultBean();
            filterResult.setOrderId(orderId);
            filterResult.setRunId(runId);
            filterResult.setExt$Item("virtual", Constant.YES);
            filterResultList.add(filterResult);
        });
        List<EmcOrderTaskResultBean> virtualResultList = orderTaskResultService.getDao().selectList(filterResultList, Arrays.asList("orderId", "runId", "virtual"), Collections.singletonList("id"));

        List<Long> virtualTaskIdList = virtualTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<Long> virtualResultIdList = virtualResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());

        orderService.getDao().update(updateOrderList, "originalOrder");
        orderTaskService.getDao().deleteByIdList(virtualTaskIdList);
        orderTaskResultService.getDao().deleteByIdList(virtualResultIdList);
    }

    @Override
    @Transactional
    public void addItemgroup(RestJsonWrapperBean wrapper) {
        String runId = wrapper.getFilterValue("runId");
        EmcOrderTaskBean orderTaskBean = wrapper.parseUnique(EmcOrderTaskBean.class);
        Long orderId = orderTaskBean.getOrderId();
        EmcOrderTaskBean orderTaskMatchBean = orderTaskService.selectById(orderTaskBean.getId());
        Long testMethodId = orderTaskMatchBean.getTestMethodId();

        List<EmcItemGroupBean> itemGroupList = wrapper.parse(EmcItemGroupBean.class);
        if (itemGroupList.isEmpty()) {
            return;
        }

        //根据样品id和测试方法可确定的分析项组
        List<EmcOrderItemGroupBean> orderItemGroupSingleOrderB = orderItemGroupService.getDao().selectListByOneColumnValue(orderId, "orderId");
        //当前批下样品的所有分析项组
        List<EmcOrderItemGroupBean> orderItemGroupSingleOrder = orderItemGroupSingleOrderB.stream().filter(oig -> ObjectUtils.equals(testMethodId, oig.getTestMethodId())).collect(Collectors.toList());

        List<Long> itemGroupIdList = itemGroupList.stream().map(EmcItemGroupBean::getId).distinct().collect(Collectors.toList());
        List<EmcGroupItemBean> giList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "itemGroupId");
        List<Long> itemIdList = giList.stream().map(EmcGroupItemBean::getItemId).distinct().collect(Collectors.toList());
        //List<EmcItemBean> itemList = itemService.getDao().selectListByIds(itemIdList);
        if (!StringUtils.isEmpty(runId)) {
            //测试不会消失，只是分析项组没有，更新ordertaskresult表就行
            List<EmcOrderTaskBean> orderTasklist = orderTaskService.selectListByFilter(SearchFilter.instance()
                    .match("runId", runId).filter(MatchPattern.SEQ).match("orderId", orderId).filter(MatchPattern.EQ)
            );
            //测试结果所有包含去除runid的
            List<Long> ordertaskIdList = orderTasklist.stream().map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());
            List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(ordertaskIdList, "ordertaskid");
            //runid是空的且是分析的项目
            List<EmcOrderTaskResultBean> orderTaskResultMatchList = orderTaskResultList.stream()
                    .filter(res -> StringUtils.equals("analysis", res.getAnalysisType()))
                    .filter(res -> itemIdList.contains(res.getItemId()))
                    .filter(res -> StringUtils.isEmpty(res.getRunId())).collect(Collectors.toList());
            orderTaskResultMatchList.forEach(r -> {
                r.setRunId(runId);
            });
            orderTaskResultService.getDao().update(orderTaskResultMatchList, "runId");

            //新增的分析项的orderItemGroup表的removeFlag状态为空
            List<EmcCapacityItemGroupBean> capacityItemGroupBeans = capacityItemGroupService.getDao().selectListByOneColumnValues(itemGroupIdList, "itemGroupId");
            List<Long> capacityItemGroupBeanIds = capacityItemGroupBeans.stream().map(EmcCapacityItemGroupBean::getId).distinct().collect(Collectors.toList());
            List<EmcOrderItemGroupBean> removeItemGroupList = orderItemGroupSingleOrder.stream().filter(oig -> capacityItemGroupBeanIds.contains(oig.getItemGroupId())).collect(Collectors.toList());
            removeItemGroupList.forEach(oig -> {
                oig.setExt$Item("removeFlag", "");
            });
            orderItemGroupService.getDao().update(removeItemGroupList, "removeFlag");
        }

    }

    @Override
    @Transactional
    public void removeItemgroup(RestJsonWrapperBean wrapper) {
        String runId = wrapper.getFilterValue("runId");
        List<EmcItemGroupBean> itemGroupList = wrapper.parse(EmcItemGroupBean.class);
        if (itemGroupList.isEmpty()) {
            return;
        }
        EmcOrderTaskBean orderTaskBean = wrapper.parseUnique(EmcOrderTaskBean.class);
        Long orderId = orderTaskBean.getOrderId();
        EmcOrderTaskBean orderTaskMatchBean = orderTaskService.selectById(orderTaskBean.getId());
        Long testMethodId = orderTaskMatchBean.getTestMethodId();
        //根据样品id和测试方法可确定的分析项组
        List<EmcOrderItemGroupBean> orderItemGroupSingleOrderB = orderItemGroupService.getDao().selectListByOneColumnValue(orderId, "orderId");
        //当前批下样品的所有分析项组
        List<EmcOrderItemGroupBean> orderItemGroupSingleOrder = orderItemGroupSingleOrderB.stream().filter(oig -> ObjectUtils.equals(testMethodId, oig.getTestMethodId())).collect(Collectors.toList());
        List<Long> itemGroupIdList = itemGroupList.stream().map(EmcItemGroupBean::getId).distinct().collect(Collectors.toList());
        List<EmcGroupItemBean> giList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "itemGroupId");
        List<Long> itemIdList = giList.stream().map(EmcGroupItemBean::getItemId).distinct().collect(Collectors.toList());
        //List<EmcItemBean> itemList = itemService.getDao().selectListByIds(itemIdList);
        if (!StringUtils.isEmpty(runId)) {
            //测试不会消失，只是分析项组没有，更新ordertaskresult表就行
            List<EmcOrderTaskBean> orderTasklist = orderTaskService.selectListByFilter(SearchFilter.instance()
                    .match("runId", runId).filter(MatchPattern.SEQ).match("orderId", orderId).filter(MatchPattern.EQ)

            );
            //测试结果所有包含去除runid的
            List<Long> ordertaskIdList = orderTasklist.stream().map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());
            List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(ordertaskIdList, "ordertaskid");
            //runid是空的且是分析的项目
            List<EmcOrderTaskResultBean> orderTaskResultMatchList = orderTaskResultList.stream()
                    .filter(res -> StringUtils.equals("analysis", res.getAnalysisType()))
                    .filter(res -> itemIdList.contains(res.getItemId()))
                    .filter(res -> !StringUtils.isEmpty(res.getRunId())).collect(Collectors.toList());
            orderTaskResultMatchList.forEach(r -> {
                r.setRunId("");
            });
            orderTaskResultService.getDao().update(orderTaskResultMatchList, "runId");
            //移除的分析项的orderItemGroup表的removeFlag状态为1
            List<EmcCapacityItemGroupBean> capacityItemGroupBeans = capacityItemGroupService.getDao().selectListByOneColumnValues(itemGroupIdList, "itemGroupId");
            List<Long> capacityItemGroupBeanIds = capacityItemGroupBeans.stream().map(EmcCapacityItemGroupBean::getId).distinct().collect(Collectors.toList());
            List<EmcOrderItemGroupBean> removeItemGroupList = orderItemGroupSingleOrder.stream().filter(oig -> capacityItemGroupBeanIds.contains(oig.getItemGroupId())).collect(Collectors.toList());
            removeItemGroupList.forEach(oig -> {
                oig.setExt$Item("removeFlag", "1");
            });
            orderItemGroupService.getDao().update(removeItemGroupList, "removeFlag");
        }

    }

    // 分析项ID：当前
    // 重写，根据分析项查询 EmcItemRevisionBean,value分析项的值
    public static String getReviseRule(String value, EmcOrderTaskResultBean resultBean, List<EmcItemRevisionBean> itemRevisionList) {
        if (StringUtils.isEmpty(value) || !NumberUtils.isNumber(value)) {
            return value;
        }

        double valueNum = NumberUtils.parseDouble(value);

        // 过滤出对应的分段修约数据
        List<EmcItemRevisionBean> filterItemRevisionList = itemRevisionList.stream().filter(e -> e.getItemId().equals(resultBean.getItemId())).collect(Collectors.toList());
        String revisionRule = resultBean.getRevisionRule();

        if (!resultBean.getQcCategoryCode().equals("QC-STD") || resultBean.getRevisionRule() == null) {
            // 处理分段修约：
            // 1、根据值查询他所在区间范围内的分段修约
            for (EmcItemRevisionBean e : filterItemRevisionList) {
                // 下限符号
                String lowLimitSymbol = e.getLowLimitSymbol();
                // 下限
                String lowLimit = e.getLowLimit();
                // 上限符号
                String highLimitSymbol = e.getHighLimitSymbol();
                // 上限
                String highLimit = e.getHighLimit();

                if (!StringUtils.equals("/", lowLimitSymbol) && !StringUtils.isEmpty(lowLimit) && NumberUtils.isNumber(lowLimit)) {

                    double lowLimitNum = NumberUtils.parseDouble(lowLimit);

                    if (StringUtils.equals(">", lowLimitSymbol)) {
                        if (valueNum <= lowLimitNum) {
                            continue;
                        }
                    } else {
                        if (valueNum < lowLimitNum) {
                            continue;
                        }
                    }

                }

                if (!StringUtils.equals("/", highLimitSymbol) && !StringUtils.isEmpty(highLimit) && NumberUtils.isNumber(highLimit)) {

                    double highLimitNum = NumberUtils.parseDouble(highLimit);

                    if (StringUtils.equals("<", highLimitSymbol)) {
                        if (valueNum >= highLimitNum) {
                            continue;
                        }
                    } else {
                        if (valueNum > highLimitNum) {
                            continue;
                        }
                    }

                }

                revisionRule = e.getRevisionRule();

            }
        }
        return revisionRule;
    }

    @Override
    @Transactional
    public String selectRelateOrder(RestJsonWrapperBean wrapper) {
        String searchFlag = "1";
        Long orderId = Long.valueOf(wrapper.getFilterValue("orderId"));
        List<EmcQcRelatedOrderBean> qcRelatedOrderBeans = qcRelatedOrderService.getDao().selectListByOneColumnValue(orderId, "qcOrderId");
        if (!qcRelatedOrderBeans.isEmpty()) {
            List<String> taskRunIdList = qcRelatedOrderBeans.stream().map(EmcQcRelatedOrderBean::getTaskRunId).distinct().filter(t -> !StringUtils.isEmpty(t)).collect(Collectors.toList());
            if (taskRunIdList.isEmpty()) {
                searchFlag = "2";
            }
        }
        return searchFlag;

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void undoAuditedRun(RestJsonWrapperBean wrapper) {
        String projNo = wrapper.getFilterValue("projNo");
        List<EmcProjectBean> projectBeans = projectService.getDao().selectListByOneColumnValue(projNo, "projNo");
        Long projId = projectBeans.get(0).getId();
        List<EmcOrderTaskBean> undoOrderTaskList;
        // 如果是分析的，则按选中的批次进行退回
        List<EmcRunBean> runList = wrapper.parse(EmcRunBean.class);
        List<String> runIdBeforeList = runList.stream().filter(r -> !StringUtils.isEmpty(r.getId())).map(EmcRunBean::getId).collect(Collectors.toList());
        undoOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(runIdBeforeList, "runId", Arrays.asList("id", "analysisOrgId", "analysisType", "runId", "orderId", "folderId"));
        // 排除已经终止的样品
        List<Long> undoOrderIdList = undoOrderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> undoOrderList = orderService.getDao().selectListByOneColumnValues(undoOrderIdList, "id", Arrays.asList("id", "termination", "sampleId"));
        List<EmcOrderBean> validUndoOrderList = undoOrderList.stream().filter(order -> StringUtils.equals(Constant.NO, order.getTermination())).collect(Collectors.toList());
        List<Long> validUndoOrderIdList = validUndoOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> validUndoOrderTaskList = undoOrderTaskList.stream().filter(task -> validUndoOrderIdList.contains(task.getOrderId())).collect(Collectors.toList());

        List<EmcOrderTaskBean> analysisTaskList = validUndoOrderTaskList.stream().filter(task -> EmcBizConstant.ANALYSISCATEGORY_ANALYSIS.equals(task.getAnalysisType())).collect(Collectors.toList());
        List<EmcOrderTaskBean> samplingTaskList = validUndoOrderTaskList.stream().filter(task -> EmcBizConstant.ANALYSISCATEGORY_SAMPLING.equals(task.getAnalysisType())).collect(Collectors.toList());

        // 分析类型的退回到分析审核
        analysisTaskList.forEach(task -> task.setStatus(EmcBizConstant.ORDERTASK_STATUS_AUDIT));
        samplingTaskList.forEach(task -> task.setStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED));

        List<EmcOrderTaskBean> updateTaskList = new ArrayList<>(analysisTaskList);
        updateTaskList.addAll(samplingTaskList);
        orderTaskService.getDao().update(updateTaskList, "status");
        // 更新测试批的状态
        List<String> runIdList = analysisTaskList.stream().map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());
        EmcRunBean updateRun = new EmcRunBean();
        updateRun.setStatus(EmcBizConstant.RUN_STATUS_AUDIT);
        updateRun.setAuditedUserId("");
        updateRun.setAuditedUserName("");
        updateRun.setAuditedDate(null);
        getDao().updateByIds(updateRun, runIdList, "STATUS", "auditedUserId", "auditedUserName", "auditedDate");

        // 报告管理退回插入审核记录
        EmcAuditRecordBean auditRecord = new EmcAuditRecordBean();
        auditRecord.setProjId(projId);
        auditRecord.setProjNo(projNo);
        auditRecord.setComments(PfConstant.UNDO_AUDITED);
        auditRecord.setLogDesc("项目:" + projNo);
        auditRecord.setBizCategory("report");
        auditRecord.setStepCode("6");
        auditRecord.setAuditCategory("报告管理");
        auditRecord.setTaskName("报告编制");
        auditRecord.setOperateCategory("退回");
        if (!samplingTaskList.isEmpty()) {
            EmcAuditRecordBean insertAuditRecord = new EmcAuditRecordBean();
            BeanUtils.copyProperties(auditRecord, insertAuditRecord);
            insertAuditRecord.setNextTaskName("结果录入-按采样单");
            insertAuditRecord.setId(ApplicationContextHelper.getNextIdentity());
            auditRecordService.getDao().insert(insertAuditRecord);
        }

        if (!analysisTaskList.isEmpty()) {
            EmcAuditRecordBean insertAuditRecord = new EmcAuditRecordBean();
            BeanUtils.copyProperties(auditRecord, insertAuditRecord);
            insertAuditRecord.setNextTaskName("分析审核");
            insertAuditRecord.setId(ApplicationContextHelper.getNextIdentity());
            auditRecordService.getDao().insert(insertAuditRecord);
        }
    }

    private String getNumBits(String needStr) {
        Pattern p = Pattern.compile("^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$");
        Matcher m = p.matcher(needStr);
        boolean b = m.matches();
        String numBits = "";
        if (b) {
            //获取小数点的位置
            int bitPos = needStr.indexOf(".");
            //字符串总长度减去小数点位置，再减去1，就是小数位数
            int numOfBits = needStr.length() - bitPos - 1;
            numBits = Integer.toString(numOfBits);
        }

        return numBits;

    }

    @Override
    public EmcAnalysisRecordDTO selectBrowseRecordFile(RestJsonWrapperBean wrapper) {
        String runId = wrapper.getParamValue("runId");

        EmcAnalysisRecordDTO analysisRecordDTO = new EmcAnalysisRecordDTO();
        if (!ObjectUtils.isEmpty(runId)) {
            //查询当前批原始记录pdf
            CoreFileBean analysisRecord = coreFileService.selectFirstByFilter(SearchFilter.instance().match("targetId", "T_EMC_RUN$" + runId)
                    .filter(MatchPattern.SEQ).match("ext", "composePdf").filter(MatchPattern.SEQ));
            analysisRecordDTO.setAnalysisRecord(analysisRecord);

            List<CoreFileBean> otherFileList = new ArrayList<>();

            //查询该批是否存在谱图文件
            List<CoreFileBean> dcuRecordList = coreFileService.selectListByFilter(SearchFilter.instance().match("targetId", "T_EMC_EQUIPT$" + runId)
                    .filter(MatchPattern.SEQ));
            otherFileList.addAll(dcuRecordList);

            //查询该批的采样单
            List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValue(runId, "runId", Arrays.asList("ID", "ORDERID"));
            List<Long> orderIdList = orderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());

            List<EmcOrderBean> orderList = orderService.selectListByFilter(SearchFilter.instance().match("id", orderIdList).filter(MatchPattern.OR)
                    .match("sample", Constant.YES).filter(MatchPattern.SEQ));

            List<Long> sampleIdList = orderList.stream().filter(e -> !ObjectUtils.isEmpty(e.getSampleId())).map(EmcOrderBean::getSampleId).distinct().collect(Collectors.toList());
            List<CoreFileBean> sampleFileFilterList = new ArrayList<>();
            sampleIdList.forEach(sampleId -> {
                CoreFileBean sampleFileFilter = new CoreFileBean();
                sampleFileFilter.setExt("sampleRecordPdf");
                sampleFileFilter.setTargetId("T_EMC_SAMPLE" + "$" + sampleId);
                sampleFileFilterList.add(sampleFileFilter);
            });

            List<CoreFileBean> sampleFileList = coreFileService.getDao().selectList(sampleFileFilterList, Arrays.asList("TARGETID", "EXT"), Collections.emptyList());
            otherFileList.addAll(sampleFileList);
            analysisRecordDTO.setOtherRecordList(otherFileList);
        }

        return analysisRecordDTO;
    }
}
