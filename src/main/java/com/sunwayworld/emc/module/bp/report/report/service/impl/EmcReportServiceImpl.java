package com.sunwayworld.emc.module.bp.report.report.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.cloud.module.fr.report.service.CoreReportConfigLineService;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.utils.ArchiveUtils;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.word.NumberFormatUtils;
import com.sunwayworld.emc.framework.word.ReportingServiceUtils;
import com.sunwayworld.emc.framework.word.Word2007Utils;
import com.sunwayworld.emc.module.bp.archive.bean.EmcArchiveCategoryBean;
import com.sunwayworld.emc.module.bp.archive.service.EmcArchiveCategoryService;
import com.sunwayworld.emc.module.bp.archive.service.EmcArchiveFileService;
import com.sunwayworld.emc.module.bp.auditrecord.bean.EmcAuditRecordBean;
import com.sunwayworld.emc.module.bp.auditrecord.service.EmcAuditRecordService;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.dao.EmcReportDao;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportResultService;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportService;
import com.sunwayworld.emc.module.bp.report.report.validator.EmcReportStartProcessValidator;
import com.sunwayworld.emc.module.bp.report.report.validator.EmcViewDocumentValidator;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.result.run.service.EmcRunService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.bean.EmcIndexTestItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.service.EmcIndexTestItemService;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.bean.EmcMonitorPointCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.service.EmcMonitorPointCategoryService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyFieldBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyDataService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyFieldService;
import com.sunwayworld.emc.module.mdm.basemdm.revisionrule.service.EmcRevisionRuleService;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.service.EmcSubcontractRecordService;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcMonitorPointService;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcIndexCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcIndexSetService;
import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodVersionBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.EmcMethodVersionService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorTmplService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemRevisionBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemRevisionService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcContractBean;
import com.sunwayworld.emc.module.mdm.contract.service.EmcContractService;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpBean;
import com.sunwayworld.emc.module.mdm.emp.emp.service.EmcEmpService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnParameterDTO;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnCommentBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnCommentService;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.service.CoreCodeService;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-18
 */

@Repository
@GikamBean
public class EmcReportServiceImpl implements EmcReportService {

    @Autowired
    @Lazy
    private EmcProjectService projectService;

    @Autowired
    private EmcContractService contractService;

    @Autowired
    @Lazy
    private EmcFolderService folderService;

    @Autowired
    @Lazy
    private EmcSampleService sampleService;

    @Autowired
    @Lazy
    private EmcRunService runService;

    @Autowired
    @Lazy
    private EmcOrderService orderService;

    @Autowired
    private EmcItemGroupService itemGroupService;

    @Autowired
    private EmcGroupItemService groupItemService;

    @Autowired
    @Lazy
    private EmcOrderTaskService orderTaskService;

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Autowired
    private EmcReportResultService reportResultService;

    @Autowired
    private EmcSubcontractRecordService subcontractRecordService;

    @Autowired
    private EmcAuditRecordService auditRecordService;

    @Autowired
    private CoreFileService coreFileService;

    @Autowired
    private CoreFileManager fileManager;

    @Autowired
    private EmcPropertyFieldService propertyFieldService;

    @Autowired
    private EmcPropertyDataService propertyDataService;

    @Autowired
    private EmcMonitorPointCategoryService monitorPointCategoryService;

    @Autowired
    private EmcMonitorPointService monitorPointService;

    @Autowired
    private CoreCodeService coreCodeService;

    @Autowired
    private EmcMethodVersionService methodVersionService;

    @Autowired
    private EmcEquiptService equiptService;

    @Autowired
    private CoreBpmnCommentService bpmnCommentService;

    @Autowired
    private EmcEmpService empService;

    @Autowired
    private EmcItemRevisionService itemRevisionService;

    @Autowired
    private EmcMonitorTmplService monitorTmplService;

    @Autowired
    private EmcItemService itemService;

    @Autowired
    private EmcIndexSetService indexSetService;

    @Autowired
    private EmcIndexCategoryService indexCategoryService;

    @Autowired
    private EmcIndexTestItemService indexTestItemService;

    @Autowired
    private EmcRevisionRuleService revisionRuleService;

    @Autowired
    private EmcArchiveCategoryService archiveCategoryService;

    @Autowired
    @Lazy
    private EmcArchiveFileService archiveFileService;

    @Autowired
    private CoreReportConfigLineService coreReportConfigLineService;

    @Autowired
    private EmcReportDao emcReportDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcReportDao getDao() {
        return emcReportDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcReportBean report = jsonWrapper.parseUnique(EmcReportBean.class);

        // 删除旧的报告结果数据
        List<EmcReportBean> allReportList = selectListByFilter(SearchFilter.instance().match("PROJID", report.getProjId()).filter(MatchPattern.OR));
        List<Long> allReportIdList = allReportList.stream().map(EmcReportBean::getId).collect(Collectors.toList());
        List<Long> deleteReportResultIdList = reportResultService.getDao().selectListByOneColumnValues(allReportIdList, "reportId",
                Collections.singletonList("ID")).stream().map(EmcReportResultBean::getId).collect(Collectors.toList());
        if (!deleteReportResultIdList.isEmpty()) {
            reportResultService.getDao().deleteByIdList(deleteReportResultIdList);
        }

        if(ObjectUtils.isEmpty(report) || "".equals(ObjectUtils.toString(report.getProjId()) )) {
        	return 0L;
        }
        // 查询样品结果
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.selectListByFilter(SearchFilter.instance()
                .match("PROJID", report.getProjId()).filter(MatchPattern.OR)
                .match("REPORTDISPLAY", Constant.YES).filter(MatchPattern.SEQ)
                .match("RESULTSTATUS", EmcBizConstant.ORDERTASKRESULT_STATUS_DONE).filter(MatchPattern.SEQ));

        // 对于直接从项目登记提交到报告的项目，没有任何数据
        if (orderTaskResultList.isEmpty()) {
            return 0L;
        }

        List<EmcOrderBean> orderList = orderService.selectListByFilter(SearchFilter.instance().match("PROJID", report.getProjId()).filter(MatchPattern.OR));
        Map<Long, EmcOrderBean> orderIdMap = orderList.stream().collect(Collectors.toMap(EmcOrderBean::getId, Function.identity()));

        // 查询分析项及修约规则
        List<Long> itemIdList = orderTaskResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
        List<EmcItemRevisionBean> itemRevisionList = itemRevisionService.getDao().selectListByOneColumnValues(itemIdList, "ITEMID");
        List<EmcItemBean> itemList = itemService.getDao().selectListByIds(itemIdList);

        // 报告数据
        List<EmcReportResultBean> reportResultList = new ArrayList<>();

        // 判断报告数据是否包含油烟类型
        boolean existLampblack = orderList.stream().anyMatch(order -> StringUtils.contains(order.getOrderCategory(), EmcConstant.LAMPBLACK_ORDER_CATEGORY_KEYWORD));

        // 计算样品值
        List<EmcOrderTaskResultBean> finalResultList = calcParallelAverageValue(orderTaskResultList, itemRevisionList, itemList);
        Map<Long, Long> taskReportResultIdMap = new HashMap<>();
        finalResultList.forEach(result -> {
            EmcReportResultBean reportResult = new EmcReportResultBean();
            reportResult.setId(ApplicationContextHelper.getNextIdentity());
            reportResult.setReportId(report.getId());
            reportResult.setDataType(EmcConstant.PN_SAMPLE);

            copyProperties(reportResult, orderIdMap.get(result.getOrderId()), result);
            reportResultList.add(reportResult);

            // 保存报告结果与样品测试结果的关系，为了更新无效的油烟结果标识
            if (existLampblack) {
                taskReportResultIdMap.put(result.getId(), reportResult.getId());
            }
        });

        // 计算总均值
        List<Long> invalidLampblackResultIdList = new ArrayList<>();
        if (Constant.YES.equals(report.getIfStatistics())) {
            Map<String, List<EmcOrderTaskResultBean>> totalTaskResultMap = finalResultList.stream().collect(Collectors.groupingBy(e -> e.getFolderId() + "$" + e.getItemId() + "$" + e.getExt$Item("testname")));
            totalTaskResultMap.forEach((key, resultList) -> {
                resultList.forEach(result -> replaceNdHdValue(result, result.getReportResult()));

                List<String> resultValueList = resultList.stream().map(EmcOrderTaskResultBean::getReportResult).collect(Collectors.toList());

                EmcOrderBean currentOrder = orderIdMap.get(resultList.get(0).getOrderId());

                // 如果当前点位下只有一个样品，则样品值就是总均值
                if (resultList.size() == 1) {
                    EmcReportResultBean reportResult = new EmcReportResultBean();
                    reportResult.setId(ApplicationContextHelper.getNextIdentity());
                    reportResult.setReportId(report.getId());
                    reportResult.setDataType(EmcConstant.PN_TOTALAVERAGE);

                    copyProperties(reportResult, currentOrder, resultList.get(0));
                    reportResultList.add(reportResult);
                    return;
                }

                // 油烟总均值计算方式（需要计算日均值）
                if (StringUtils.contains(currentOrder.getOrderCategory(), EmcConstant.LAMPBLACK_ORDER_CATEGORY_KEYWORD)) {
                    Map<String, List<EmcOrderTaskResultBean>> dayResultListMap = resultList.stream().collect(Collectors.groupingBy(e -> e.getFolderId() + "$" + e.getCycle()));
                    dayResultListMap.forEach((cycle, dayResultList) -> {
                        String lampblackAvgResult = calcLampblackTotalAvgResult(dayResultList);
                        if (lampblackAvgResult != null) {
                            EmcOrderTaskResultBean totalResult = new EmcOrderTaskResultBean();
                            BeanUtils.copyProperties(dayResultList.get(0), totalResult);
                            totalResult.setOriginalResult(NumberUtils.formatNumber(NumberUtils.parseDouble(lampblackAvgResult)));

                            // 对均值原始结果进行修约
                            reviseOriginalValue(totalResult, itemRevisionList, itemList);

                            EmcReportResultBean reportResult = new EmcReportResultBean();
                            reportResult.setId(ApplicationContextHelper.getNextIdentity());
                            reportResult.setReportId(report.getId());
                            reportResult.setDataType(EmcConstant.PN_DAYAVERAGE);

                            copyProperties(reportResult, orderIdMap.get(dayResultList.get(0).getOrderId()), totalResult);
                            reportResultList.add(reportResult);
                        }
                    });

                    // 无效的油烟结果
                    resultList.stream().filter(r -> Constant.YES.equals(r.getExt$Item("invalidresult"))).forEach(r -> invalidLampblackResultIdList.add(r.getId()));

                    return;
                }

                resultValueList.stream().filter(NumberUtils::isNumber).mapToDouble(NumberUtils::parseDouble).average().ifPresent(avgResult -> {
                    EmcOrderTaskResultBean totalResult = new EmcOrderTaskResultBean();
                    BeanUtils.copyProperties(resultList.get(0), totalResult);
                    totalResult.setOriginalResult(NumberUtils.formatNumber(avgResult));

                    // 对均值原始结果进行修约
                    reviseOriginalValue(totalResult, itemRevisionList, itemList);

                    EmcReportResultBean reportResult = new EmcReportResultBean();
                    reportResult.setId(ApplicationContextHelper.getNextIdentity());
                    reportResult.setReportId(report.getId());
                    reportResult.setDataType(EmcConstant.PN_TOTALAVERAGE);

                    copyProperties(reportResult, currentOrder, totalResult);
                    reportResultList.add(reportResult);
                });
            });
        }

        if (!reportResultList.isEmpty()) {
            // 更新无效的油烟结果
            if (!invalidLampblackResultIdList.isEmpty()) {
                List<Long> invalidReportResultIdList = new ArrayList<>();
                invalidLampblackResultIdList.forEach(id -> invalidReportResultIdList.add(taskReportResultIdMap.get(id)));
                reportResultList.stream().filter(result -> invalidReportResultIdList.contains(result.getId())).forEach(result -> result.setInvalidResult(Constant.YES));
            }
            reportResultService.getDao().insert(reportResultList);
        }

        return report.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertByProject(List<Long> projectIdList, boolean viewFlag) {
        List<EmcReportBean> reportList = new ArrayList<>();
        if (viewFlag) {
            for (Long projId : projectIdList) {
                EmcReportBean report = new EmcReportBean();
                report.setId(ApplicationContextHelper.getNextIdentity());
                report.setProjId(projId);
                report.setStatus(EmcBizConstant.REPORT_STATUS_REPORTEDIT);
                reportList.add(report);
            }
        } else {
            List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(projectIdList, "PROJID");
            Map<Long, List<EmcOrderBean>> map = orderList.stream().collect(Collectors.groupingBy(EmcOrderBean::getProjId));
            map.forEach((k, v) -> {
                // 根据监测模板ID去重
                List<EmcOrderBean> filterOrderList = v.stream()
                        .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmcOrderBean::getMonitorTmplId))), ArrayList::new));
                for (EmcOrderBean order : filterOrderList) {
                    EmcReportBean report = new EmcReportBean();
                    report.setId(ApplicationContextHelper.getNextIdentity());
                    report.setProjId(order.getProjId());
                    report.setMonitorTmplId(order.getMonitorTmplId());
                    report.setOrderCategory(order.getOrderCategory());
                    report.setStatus(EmcBizConstant.REPORT_STATUS_DRAFT);
                    reportList.add(report);
                }
            });
        }

        this.getDao().insert(reportList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void bizUndo(RestJsonWrapperBean jsonWrapper) {

        EmcReportBean report = jsonWrapper.parse(EmcReportBean.class, 0).get(0);
        List<EmcReportBean> reportList = selectListByFilter(SearchFilter.instance().match("ID", report.getId()).filter(MatchPattern.EQ));

        report.setStatus(EmcBizConstant.REPORT_STATUS_DRAFT);

        getDao().update(report, "STATUS");

        List<EmcOrderTaskBean> filterOrderTaskList = jsonWrapper.parse(EmcOrderTaskBean.class, 1);

        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectList(filterOrderTaskList, Arrays.asList("PROJID", "MONITORTMPLID", "ANALYSISTYPE", "ANALYSISORGID"),
                Collections.emptyList());

        // 报告回退，将分析类型为分析的样品测试状态置为audit，退回分析审核
        List<EmcOrderTaskBean> analysisOrderTaskList = orderTaskList.stream().filter(e -> EmcBizConstant.ANALYSISCATEGORY_ANALYSIS.equals(e.getAnalysisType())).collect(Collectors.toList());

        analysisOrderTaskList.forEach(e -> e.setStatus(EmcBizConstant.ORDERTASK_STATUS_AUDIT));

        // 报告回退，将分析类型为采样的的样品测试状态置为logged，退回结果录入-按采样单
        List<EmcOrderTaskBean> sampleOrderTaskList = orderTaskList.stream().filter(e -> EmcBizConstant.ANALYSISCATEGORY_SAMPLING.equals(e.getAnalysisType())).collect(Collectors.toList());

        sampleOrderTaskList.forEach(e -> e.setStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED));

        // 获取未终止的样品信息
        List<Long> orderIdList = sampleOrderTaskList.stream().map(e -> e.getOrderId()).distinct().collect(Collectors.toList());
        List<EmcOrderBean> drawOrderList = orderService.selectListByIds(orderIdList);
        List<EmcOrderBean> comDrawOrderList = drawOrderList.stream().filter(e -> "0".equals(e.getTermination())).collect(Collectors.toList());
        List<Long> comDrawOrderIdList = comDrawOrderList.stream().map(e -> e.getId()).distinct().collect(Collectors.toList());
        List<Long> sampleIdList = orderService.selectListByIds(comDrawOrderIdList).stream().map(e -> e.getSampleId()).distinct().collect(Collectors.toList());

        List<Long> analOrderIdList = analysisOrderTaskList.stream().map(e -> e.getOrderId()).distinct().collect(Collectors.toList());
        List<EmcOrderBean> analOrderList = orderService.selectListByIds(analOrderIdList);
        List<EmcOrderBean> comAnalOrderList = analOrderList.stream().filter(e -> "0".equals(e.getTermination())).collect(Collectors.toList());
        List<Long> comAnalOrderIdList = comAnalOrderList.stream().map(e -> e.getId()).distinct().collect(Collectors.toList());
        // 报告回退，将采样单做回退操作，更新采样单状态为报告回退
        CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
        parameter.setComment("回退");
        sampleService.undo(sampleIdList, parameter);

        EmcSampleBean sample = new EmcSampleBean();

        sample.setStatus(EmcBizConstant.SAMPLE_STATUS_LOGGED);

        sampleService.getDao().updateByIds(sample, sampleIdList, "STATUS");

        List<EmcOrderTaskBean> updateOrderTaskList = new ArrayList<>();

        analysisOrderTaskList = analysisOrderTaskList.stream().filter(e -> comAnalOrderIdList.contains(e.getOrderId())).collect(Collectors.toList());
        sampleOrderTaskList = sampleOrderTaskList.stream().filter(e -> comDrawOrderIdList.contains(e.getOrderId())).collect(Collectors.toList());

        updateOrderTaskList.addAll(analysisOrderTaskList);
        updateOrderTaskList.addAll(sampleOrderTaskList);

        orderTaskService.getDao().update(updateOrderTaskList, "STATUS");

        // 更新项目节点状态
        List<Long> projIdList = reportList.stream().map(EmcReportBean::getProjId).distinct().collect(Collectors.toList());
        EmcProjectBean updateProject = new EmcProjectBean();

        if (!sampleOrderTaskList.isEmpty()) {
            updateProject.setStatus(EmcBizConstant.PROJECT_STATUS_AUDITFINISHED);
            updateProject.setProjNode(EmcBizConstant.PROJECT_CN_NODE_FXZ);
        } else {
            updateProject.setStatus(EmcBizConstant.PROJECT_STATUS_AUDITFINISHED);
            updateProject.setProjNode(EmcBizConstant.PROJECT_CN_NODE_FXZ);
        }
        projectService.getDao().updateByIds(updateProject, projIdList, "PROJNODE", "STATUS");

        // 报告管理退回插入审核记录
        EmcAuditRecordBean auditRecord = new EmcAuditRecordBean();
        EmcReportBean submitReport = reportList.get(0);
        auditRecord.setProjId(submitReport.getProjId());
        auditRecord.setProjNo(submitReport.getExt$Item("projno"));
        auditRecord.setComments("回退");
        auditRecord.setLogDesc("项目:" + submitReport.getExt$Item("projno"));
        auditRecord.setBizCategory("report");
        auditRecord.setStepCode("6");
        auditRecord.setAuditCategory("报告管理");
        auditRecord.setTaskName("报告编制");
        auditRecord.setOperateCategory("退回");
        if (!sampleOrderTaskList.isEmpty()) {
            EmcAuditRecordBean insertAuditRecord = new EmcAuditRecordBean();
            BeanUtils.copyProperties(auditRecord, insertAuditRecord);
            insertAuditRecord.setNextTaskName("结果录入-按采样单");
            insertAuditRecord.setId(ApplicationContextHelper.getNextIdentity());
            auditRecordService.getDao().insert(insertAuditRecord);
        }
        if (!analysisOrderTaskList.isEmpty()) {
            EmcAuditRecordBean insertAuditRecord = new EmcAuditRecordBean();
            BeanUtils.copyProperties(auditRecord, insertAuditRecord);
            insertAuditRecord.setNextTaskName("结果录入-按批");
            insertAuditRecord.setId(ApplicationContextHelper.getNextIdentity());
            auditRecordService.getDao().insert(insertAuditRecord);
        }
        List<Long> folderIdList = orderTaskList.stream().map(EmcOrderTaskBean::getFolderId).distinct().collect(Collectors.toList());
        List<EmcFolderBean> folderList = new ArrayList<>();
        folderIdList.forEach(folderId -> {
            EmcFolderBean folder = new EmcFolderBean();
            folder.setId(folderId);
            folder.setStatus(EmcBizConstant.FOLDER_STATUS_DRAW);
            folderList.add(folder);
        });
        folderService.getDao().update(folderList, "STATUS");

        List<String> runIdList = orderTaskList.stream().filter(t -> !ObjectUtils.isEmpty(t.getRunId())).map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());
        if (runIdList.isEmpty()) {
            return;
        }
        List<EmcRunBean> runList = new ArrayList<>();
        runIdList.stream().forEach(e -> {
            EmcRunBean run = new EmcRunBean();
            run.setId(e);
            run.setStatus(EmcBizConstant.RUN_STATUS_AUDIT);
            runList.add(run);
        });
        runService.getDao().update(runList, "STATUS");
    }

    /**
     * 报告编制超期代办
     */
    @Override
    public Page<EmcReportBean> selectReportOverduePagination(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return selectPagination(() -> getDao().selectReportOverdue(parameter), rowBounds);
    }

    /**
     * 报告签发超期代办
     */
    @Override
    public Page<EmcReportBean> selectReportIssueOverduePagination(RestJsonWrapperBean wrapper) {
        return this.selectPagination(wrapper);
    }

    @Override
    public Page<EmcOrderTaskBean> selectUndoableAnalysisOrgDataTypePagination(RestJsonWrapperBean wrapper) {
        EmcOrderTaskBean filterOrderTask = new EmcOrderTaskBean();
        filterOrderTask.setProjId(NumberUtils.parseLong(wrapper.getFilterValue("projId")));
        filterOrderTask.setMonitorTmplId(wrapper.getFilterValue("monitorTmplId"));

        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectList(filterOrderTask, Arrays.asList("PROJID", "MONITORTMPLID"), Collections.emptyList());
        // 按分析类型和分析科室分组
        List<EmcOrderTaskBean> resultOrderTaskList = orderTaskList.stream().collect(Collectors
                .collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getAnalysisType() + "#" + o.getAnalysisOrgId()))), ArrayList<EmcOrderTaskBean>::new));

        return new Pagination<>(resultOrderTaskList);
    }

    /**
     * 根据传过来的样品测试判断对应的项目样品类型下的样品测试是不是都已经完成，完成的话将报告状态置为待录入
     */
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateReportStatus(List<EmcOrderTaskBean> orderTaskList) {

        List<EmcOrderTaskBean> filterOrderTaskList = orderTaskList.stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getProjId() + "#" + o.getMonitorTmplId()))), ArrayList::new));
        List<EmcOrderTaskBean> allOrderTaskList = orderTaskService.getDao().selectList(filterOrderTaskList, Arrays.asList("PROJID", "MONITORTMPLID"), Collections.emptyList());

        List<EmcSampleBean> filterSampleList = new ArrayList<>();
        filterOrderTaskList.forEach(e -> {
            EmcSampleBean filterSample = new EmcSampleBean();
            filterSample.setProjId(e.getProjId());
            filterSample.setMonitorTmplId(e.getMonitorTmplId());
            filterSampleList.add(filterSample);
        });
        List<EmcSampleBean> allSampleList = sampleService.getDao().selectList(filterSampleList, Arrays.asList("PROJID", "MONITORTMPLID"), Collections.emptyList());

        List<EmcReportBean> filterReportList = new ArrayList<>();
        filterOrderTaskList.forEach(e -> {
            EmcReportBean filterReport = new EmcReportBean();
            filterReport.setProjId(e.getProjId());
            filterReport.setMonitorTmplId(e.getMonitorTmplId());
            filterReportList.add(filterReport);
        });
        List<EmcReportBean> allReportList = getDao().selectList(filterReportList, Arrays.asList("PROJID", "MONITORTMPLID"), Collections.emptyList());

        List<EmcReportBean> updateReportList = new ArrayList<>();

        for (EmcOrderTaskBean orderTask : filterOrderTaskList) {

            Long projId = orderTask.getProjId();
            String monitorTmplId = orderTask.getMonitorTmplId();

            boolean orderTaskAllMatch = allOrderTaskList.stream().filter(e -> ObjectUtils.equals(e.getProjId(), projId) && ObjectUtils.equals(e.getMonitorTmplId(), monitorTmplId))
                    .allMatch(e -> EmcBizConstant.ORDERTASK_STATUS_DONE.equals(e.getStatus()));

            boolean sampleAllMatch = allSampleList.stream().filter(e -> ObjectUtils.equals(e.getProjId(), projId) && ObjectUtils.equals(e.getMonitorTmplId(), monitorTmplId))
                    .allMatch(e -> EmcBizConstant.SAMPLE_STATUS_DONE.equals(e.getStatus()));

            if (orderTaskAllMatch && sampleAllMatch) {

                EmcReportBean report = allReportList.stream().filter(e -> ObjectUtils.equals(projId, e.getProjId()) && ObjectUtils.equals(monitorTmplId, e.getMonitorTmplId())).findFirst()
                        .orElse(null);
                EmcReportBean updateReport = new EmcReportBean();

                updateReport.setId(report.getId());
                updateReport.setStatus(EmcBizConstant.REPORT_STATUS_REPORTEDIT);

                updateReportList.add(updateReport);

            }
        }

        getDao().update(updateReportList, "STATUS");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_START)
    @ValidateDataWith(EmcReportStartProcessValidator.class)
    public List<CoreBpmnInstanceStatusDTO<Long>> startProcess(RestJsonWrapperBean wrapper) {
        List<EmcReportBean> reportList = wrapper.parse(EmcReportBean.class);
        List<Long> reportIdList = reportList.stream().map(EmcReportBean::getId).distinct().collect(Collectors.toList());

        reportList = selectListByFilter(SearchFilter.instance().match("ID", reportIdList).filter(MatchPattern.OR));

        List<Long> projIdList = reportList.stream().map(EmcReportBean::getProjId).distinct().collect(Collectors.toList());
        List<EmcReportBean> allReportList = selectListByFilter(SearchFilter.instance().match("PROJID", projIdList).filter(MatchPattern.OR));
        List<Long> allReportIdList = allReportList.stream().map(EmcReportBean::getId).distinct().collect(Collectors.toList());
        CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
        parameter.setComment("提交");
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcReportService.super.startProcess(allReportIdList,parameter );

        List<Long> orderIdList = orderService.getDao().selectListByOneColumnValues(projIdList, "PROJID").stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");

        // 根据monitorTmplId和projectId去重
        List<EmcReportBean> distinctReportList = allReportList.stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getMonitorTmplId() + ";" + o.getProjId()))), ArrayList::new));

        // 报告编制提交之后，分包数据录入的数据自动消失
        List<Long> deleteOrderTaskIdList = new ArrayList<>();
        distinctReportList.forEach(r -> {
            List<Long> orderTaskIdList = orderTaskList.stream()
                    .filter(o -> ObjectUtils.equals(r.getProjId(), o.getProjId()) && ObjectUtils.equals(r.getMonitorTmplId(), o.getMonitorTmplId()) && !ObjectUtils.isEmpty(o.getSubcontractorId()))
                    .map(o -> o.getId()).collect(Collectors.toList());
            deleteOrderTaskIdList.addAll(orderTaskIdList);
        });
        List<Long> deleteSubcontractRecordIdList = subcontractRecordService.getDao().selectListByOneColumnValues(deleteOrderTaskIdList, "ORDERTASKID").stream().map(e -> e.getId())
                .collect(Collectors.toList());
        subcontractRecordService.getDao().deleteByIdList(deleteSubcontractRecordIdList);

        // 更新报告状态
        allReportList.forEach(report -> report.setStatus(EmcBizConstant.REPORT_STATUS_REPORTAUDIT));

        getDao().update(allReportList, "STATUS");
        // 报告管理提交插入审核记录
        this.insertAuditRecordByReport(allReportList, "PROCESS_START", wrapper.getParamValue("bpmn_comment"), null);

        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_COMPLETE)
    public List<CoreBpmnInstanceStatusDTO<Long>> completeTask(RestJsonWrapperBean wrapper) {

        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcReportService.super.completeTask(wrapper);
        List<Long> reportIdList = coreBpmnInstanceStatusDTOList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcReportBean> reportList = selectListByFilter(SearchFilter.instance().match("ID", reportIdList).filter(MatchPattern.OR));

        // 更新报告状态
        String statusCode = coreBpmnInstanceStatusDTOList.get(0).getStatusCode();
        List<EmcReportBean> updateReportList = new ArrayList<EmcReportBean>();
        reportList.forEach(e -> {
            EmcReportBean updateReport = new EmcReportBean();
            updateReport.setId(e.getId());
            if (EmcBizConstant.REPORT_STATUS_REPORTCHECK.equals(statusCode)) {
                updateReport.setStatus(EmcBizConstant.REPORT_STATUS_REPORTAUDIT);
            } else if (EmcBizConstant.REPORT_STATUS_REPORTAUDIT.equals(statusCode)) {
                updateReport.setStatus(EmcBizConstant.REPORT_STATUS_REPORTISSUE);
            } else if (EmcBizConstant.REPORT_STATUS_REPORTISSUE.equals(statusCode)) {
                updateReport.setStatus(EmcBizConstant.REPORT_STATUS_REPORTPRINT);
            } else if (EmcBizConstant.REPORT_STATUS_REPORTPRINT.equals(statusCode)) {
                updateReport.setStatus(EmcBizConstant.REPORT_STATUS_REPORTSEND);
            } else if (EmcBizConstant.REPORT_STATUS_REPORTSEND.equals(statusCode)) {
                updateReport.setStatus(EmcBizConstant.REPORT_STATUS_REPORTARCHIVING);
            } else if (EmcBizConstant.REPORT_STATUS_REPORTARCHIVING.equals(statusCode)) {
                updateReport.setStatus(EmcBizConstant.REPORT_STATUS_DONE);
            }
            updateReportList.add(updateReport);
        });
        getDao().update(updateReportList, "STATUS");

        // 报告管理审核插入审核记录
        this.insertAuditRecordByReport(reportList, "PROCESS_COMPLETE", wrapper.getParamValue("bpmn_comment"), coreBpmnInstanceStatusDTOList);
        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_REJECT)
    public List<CoreBpmnInstanceStatusDTO<Long>> rejectTask(RestJsonWrapperBean wrapper) {

        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcReportService.super.rejectTask(wrapper);
        List<Long> reportIdList = coreBpmnInstanceStatusDTOList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcReportBean> reportList = selectListByFilter(SearchFilter.instance().match("ID", reportIdList).filter(MatchPattern.OR));

        // 更新报告状态
        String bpmn_targetStatusCode = wrapper.getParamMap().get("bpmn_targetStatusCode");
        List<EmcReportBean> updateReportList = new ArrayList<EmcReportBean>();
        reportList.forEach(e -> {
            EmcReportBean updateReport = new EmcReportBean();
            updateReport.setId(e.getId());
            if (EmcBizConstant.REPORT_STATUS_DRAFT.equalsIgnoreCase(bpmn_targetStatusCode)) {
                updateReport.setStatus(EmcBizConstant.REPORT_STATUS_REPORTEDIT);
            } else {
                updateReport.setStatus(bpmn_targetStatusCode);
            }
            updateReportList.add(updateReport);
        });
        getDao().update(updateReportList, "STATUS");

        // 报告管理审核插入审核记录
        this.insertAuditRecordByReport(reportList, "PROCESS_UNDO", wrapper.getParamValue("bpmn_comment"), coreBpmnInstanceStatusDTOList);

        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    public Page<EmcFolderBean> selectFolderListByReportId(Long id, RestJsonWrapperBean wrapper) {
        EmcReportBean report = selectDetail(id);
        String orderCategory = report.getOrderCategory();
        Long projId = report.getProjId();
        return folderService.selectPaginationByFilter(SearchFilter.instance().match("PROJID", projId).filter(MatchPattern.EQ).match("ORDERCATEGORY", orderCategory).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void regroupReport(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);

        // 复制项目，点位，样品，样品测试，结果，采样单，批,报告
        List<Long> folderIdList = orderTaskList.stream().map(e -> e.getFolderId()).collect(Collectors.toList());
        List<EmcFolderBean> folderList = folderService.getDao().selectListByIds(folderIdList);
        Long projId = folderList.get(0).getProjId();
        EmcProjectBean project = projectService.selectDetail(projId);
        List<Long> orderTaskIdList = orderTaskList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "orderTaskId");
        List<Long> orderIdList = orderTaskList.stream().map(e -> e.getOrderId()).collect(Collectors.toList());
        List<EmcOrderBean> orderList = orderService.getDao().selectListByIds(orderIdList);

        // 项目
        EmcProjectBean newProject = new EmcProjectBean();
        BeanUtils.copyProperties(project, newProject);
        String newProjNo = projectService.getSequenceNo(project.getBizCategoryCode());
        LocalDateTime taskDate = newProject.getTaskDate() == null ? LocalDateTime.now() : newProject.getTaskDate();
        String taskDateStr = DateTimeUtils.formatLocalDateTime(taskDate, "yy");
        String newProjectNo = project.getBizCategoryCode() + taskDateStr + newProjNo;
        newProject.setProjNo(newProjectNo);
        Long newProjectId = ApplicationContextHelper.getNextIdentity();
        newProject.setId(newProjectId);

        List<EmcFolderBean> newFolderList = new ArrayList<>();
        List<EmcOrderBean> newOrderList = new ArrayList<>();
        List<EmcOrderTaskBean> newOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> newOrderTaskResultList = new ArrayList<>();
        List<EmcSampleBean> newSampleList = new ArrayList<>();
        List<EmcRunBean> newRunList = new ArrayList<>();
        List<EmcReportBean> newReportList = new ArrayList<>();

        // 点位
        // 批量生成新的点位编号
        int folderCount = folderList.size();
        List<String> newFolderNoList = folderService.generateFolderNosByProjId(newProjectId, newProjectNo, folderList.get(0).getMonitorTmplId(), folderCount);

        for (int i = 0; i < folderCount; i++) {
            EmcFolderBean folder = folderList.get(i);
            EmcFolderBean newFolder = new EmcFolderBean();
            BeanUtils.copyProperties(folder, newFolder);
            String newFolderNo = newFolderNoList.get(i);
            newFolder.setFolderNo(newFolderNo);
            Long newFolderId = ApplicationContextHelper.getNextIdentity();
            newFolder.setId(newFolderId);
            newFolder.setProjId(newProjectId);
            newFolderList.add(newFolder);

            // 样品
            List<EmcOrderBean> orderOfFolderList = orderList.stream().filter(e -> ObjectUtils.equals(folder.getId(), e.getFolderId())).collect(Collectors.toList());

            if (!orderOfFolderList.isEmpty()) {
                for (int j = 0; j < orderOfFolderList.size(); j++) {
                    EmcOrderBean order = orderOfFolderList.get(j);
                    EmcOrderBean newOrder = new EmcOrderBean();
                    BeanUtils.copyProperties(order, newOrder);
                    newOrder.setOrderNo(newFolderNo + StringUtils.leftPad(j + 1 + "", 3));
                    Long newOrderId = ApplicationContextHelper.getNextIdentity();
                    newOrder.setId(newOrderId);
                    newOrder.setFolderId(newFolderId);
                    newOrder.setProjId(newProjectId);
                    newOrderList.add(newOrder);

                    // 样品测试
                    List<EmcOrderTaskBean> orderTaskOfOrderList = orderTaskList.stream().filter(e -> ObjectUtils.equals(order.getId(), e.getOrderId())).collect(Collectors.toList());

                    if (!orderTaskOfOrderList.isEmpty()) {
                        for (int k = 0; k < orderTaskOfOrderList.size(); k++) {
                            EmcOrderTaskBean orderTask = orderTaskOfOrderList.get(k);
                            EmcOrderTaskBean newOrderTask = new EmcOrderTaskBean();
                            BeanUtils.copyProperties(orderTask, newOrderTask);
                            Long newOrderTaskId = ApplicationContextHelper.getNextIdentity();
                            newOrderTask.setId(newOrderTaskId);
                            newOrderTask.setOrderId(newOrderId);
                            newOrderTask.setFolderId(newFolderId);
                            newOrderTask.setProjId(newProjectId);
                            newOrderTaskList.add(newOrderTask);

                            // 样品测试结果
                            List<EmcOrderTaskResultBean> orderTaskResultOfOrderTaskList = orderTaskResultList.stream().filter(e -> ObjectUtils.equals(orderTask.getId(), e.getOrderTaskId()))
                                    .collect(Collectors.toList());

                            if (!orderTaskResultOfOrderTaskList.isEmpty()) {
                                for (int n = 0; n < orderTaskResultOfOrderTaskList.size(); n++) {
                                    EmcOrderTaskResultBean orderTaskResult = orderTaskResultOfOrderTaskList.get(n);
                                    EmcOrderTaskResultBean newOrderTaskResult = new EmcOrderTaskResultBean();
                                    BeanUtils.copyProperties(orderTaskResult, newOrderTaskResult);
                                    Long newOrderTaskResultId = ApplicationContextHelper.getNextIdentity();
                                    newOrderTaskResult.setId(newOrderTaskResultId);
                                    newOrderTaskResult.setOrderTaskId(newOrderTaskId);
                                    newOrderTaskResult.setOrderId(newOrderId);
                                    newOrderTaskResult.setFolderId(newFolderId);
                                    newOrderTaskResult.setProjId(newProjectId);
                                    newOrderTaskResultList.add(newOrderTaskResult);
                                }
                                ;
                            }
                        }
                        ;
                    }
                }
                ;
            }
        }

        // 采样单
        List<Long> sampleIdList = orderList.stream().map(e -> e.getSampleId()).collect(Collectors.toList());
        if (!sampleIdList.isEmpty()) {
            List<EmcSampleBean> sampleList = sampleService.getDao().selectListByIds(sampleIdList);
            int sampleCount = sampleList.size();
            List<String> newSampleNoList = ApplicationContextHelper.getNextSequenceList(sampleService.getDao().getTable(), sampleCount);
            for (int i = 0; i < sampleCount; i++) {
                EmcSampleBean sample = sampleList.get(i);
                EmcSampleBean newSample = new EmcSampleBean();
                BeanUtils.copyProperties(sample, newSample);
                Long newSampleId = ApplicationContextHelper.getNextIdentity();
                newSample.setId(newSampleId);
                newSample.setSampleNo(newSampleNoList.get(i));
                newSample.setProjId(newProjectId);
                newSample.setProjNo(newProjNo);
                newSampleList.add(newSample);

                List<EmcOrderBean> orderOfSampleList = newOrderList.stream().filter(e -> ObjectUtils.equals(sample.getId(), e.getSampleId())).collect(Collectors.toList());
                if (!orderOfSampleList.isEmpty()) {
                    orderOfSampleList.forEach(e -> {
                        e.setSampleId(newSampleId);
                    });
                }
            }
        }

        // 批
        List<String> runIdList = orderTaskList.stream().filter(e -> e.getRunId() != null).map(e -> e.getRunId()).collect(Collectors.toList());
        if (!runIdList.isEmpty()) {
            List<EmcRunBean> runList = runService.getDao().selectListByIds(runIdList);
            int runCount = runList.size();
            List<String> newRunIdList = ApplicationContextHelper.getNextSequenceList(runService.getDao().getTable(), runCount);
            for (int i = 0; i < runCount; i++) {
                EmcRunBean run = runList.get(i);
                EmcRunBean newRun = new EmcRunBean();
                BeanUtils.copyProperties(run, newRun);
                String newRunId = newRunIdList.get(i);
                newRun.setId(newRunId);
                newRun.setProjId(newProjectId);
                newRunList.add(newRun);

                List<EmcOrderTaskBean> orderTaskOfRunList = newOrderTaskList.stream().filter(e -> ObjectUtils.equals(run.getId(), e.getRunId())).collect(Collectors.toList());
                if (!orderTaskOfRunList.isEmpty()) {
                    orderTaskOfRunList.forEach(e -> {
                        e.setRunId(newRunId);
                    });
                }
            }
        }

        // 报告
        List<EmcFolderBean> folderOfMonitorTmplList = newFolderList.stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmcFolderBean::getMonitorTmplId))), ArrayList::new));

        folderOfMonitorTmplList.forEach(e -> {
            EmcReportBean newReport = new EmcReportBean();
            newReport.setId(ApplicationContextHelper.getNextIdentity());
            newReport.setProjId(newProjectId);
            newReport.setMonitorTmplId(e.getMonitorTmplId());
            newReport.setOrderCategory(e.getOrderCategory());
            newReport.setStatus(EmcBizConstant.REPORT_STATUS_REPORTEDIT);
            newReportList.add(newReport);
        });

        projectService.getDao().insert(newProject);
        folderService.getDao().insert(newFolderList);
        orderService.getDao().insert(newOrderList);
        orderTaskService.getDao().insert(newOrderTaskList);
        orderTaskResultService.getDao().insert(newOrderTaskResultList);
        sampleService.getDao().insert(newSampleList);
        runService.getDao().insert(newRunList);
        getDao().insert(newReportList);
    }

    @Override
    @Transactional
    @ValidateDataWith(EmcViewDocumentValidator.class)
    public Long viewDocument(RestJsonWrapperBean wrapper) {
        EmcReportBean report = wrapper.parseUnique(EmcReportBean.class);

        Long projectId = report.getProjId();
        List<EmcReportBean> reportList = getDao().selectListByOneColumnValue(projectId, "PROJID");
        List<String> targetIdList = reportList.stream().map(r -> getDao().getTable() + "$" + r.getId()).collect(Collectors.toList());

        List<CoreFileBean> wordReportFileList = coreFileService.selectListByFilter(SearchFilter.instance().match("targetId",
                targetIdList).filter(MatchPattern.OR).match("bizCategory", EmcConstant.WORD_REPORT_ATTACHMENT_TYPE).filter(MatchPattern.EQ));
        EmcProjectBean project = projectService.getDao().selectById(projectId);

        // 如果Word报告之前已经生成了，不再重复生成数据
        if (!wordReportFileList.isEmpty()) {
            List<CoreFileBean> updateCoreFileList = new ArrayList<>();
            wordReportFileList.forEach(e -> {
                CoreFileBean updateCoreFile = new CoreFileBean();
                updateCoreFile.setId(e.getId());
                updateCoreFile.setTargetId("X" + e.getTargetId());
                updateCoreFileList.add(updateCoreFile);
            });
            coreFileService.getDao().update(updateCoreFileList, "TARGETID");

            CoreFileBean wordTemplateConfigFile = wordReportFileList.get(0);
            File templateFile = FilePathManager.getLocalPath(FilePathDTO.of(wordTemplateConfigFile.getScope(), DateTimeUtils.getDateSerialNumber(wordTemplateConfigFile.getCreatedTime()),
                    wordTemplateConfigFile.getMd5Name(), wordTemplateConfigFile.getName())).toFile();
            generateWordReport(templateFile, templateFile, report, reportList, project, true);
            List<CoreFileBean> newCoreFileList = saveWordReportCoreFile(wordTemplateConfigFile.getName(), templateFile, reportList);

            return newCoreFileList.get(0).getId();
        }

        Long reportConfigLineId = report.getReportConfigLineId();
        List<CoreFileBean> wordTemplateConfigFileList = coreFileService.getDao().selectListByOneColumnValue("T_CORE_REPORT_CONFIG_LINE$" + reportConfigLineId, "targetId");
        if (!wordTemplateConfigFileList.isEmpty()) {
            CoreFileBean wordTemplateConfigFile = wordTemplateConfigFileList.get(0);

            String wordReportFileName = FileUtils.getFileBaseName(wordTemplateConfigFile.getName()) + "-" + System.currentTimeMillis() + "." + FileUtils.getFileExtension(wordTemplateConfigFile.getName());
            File templateFile = FilePathManager.getLocalPath(FilePathDTO.of(wordTemplateConfigFile.getScope(), "",
                    wordTemplateConfigFile.getExt(), wordTemplateConfigFile.getName())).toFile();
            File wordReportFile = FilePathManager.getLocalPath(FilePathDTO.of(wordTemplateConfigFile.getScope(), "",
                    wordTemplateConfigFile.getExt(), wordReportFileName)).toFile();

            generateWordReport(templateFile, wordReportFile, report, reportList, project, false);
            List<CoreFileBean> newCoreFileList = saveWordReportCoreFile(wordReportFileName, wordReportFile, reportList);
            return newCoreFileList.get(0).getId();
        }

        return 0L;
    }

    @Override
    @Transactional
    public void generateReportResultByFolder(RestJsonWrapperBean wrapper) {
        String folderIdStr = wrapper.getParamValue("folderId");
        if (!NumberUtils.isNumber(folderIdStr)) {
            return;
        }
        Long folderId = NumberUtils.parseLong(folderIdStr);

        // 解析报告参数(根据点位生成报告结果数据时，前端只会传递一个报告参数)
        EmcReportBean reportBean = wrapper.parseUnique(EmcReportBean.class);
        Long reportId = reportBean.getId();

        // 根据项目ID、监测模板ID、点位ID来查询样品信息

       /*
        EmcOrderBean filterOrder = new EmcOrderBean();
        filterOrder.setProjId(reportBean.getProjId());
        filterOrder.setMonitorTmplId(reportBean.getMonitorTmplId());
        filterOrder.setFolderId(folderId);
        List<EmcOrderBean> orderBeanList = orderService.getDao().selectList(filterOrder, Arrays.asList("PROJID", "MONITORTMPLID", "FOLDERID"), Collections.emptyList());
        */
        List<EmcOrderBean> orderBeanList=orderService.selectListByFilter(SearchFilter.instance().match("PROJID",reportBean.getProjId()).filter(MatchPattern.SEQ)
                                                                .match("MONITORTMPLID",reportBean.getMonitorTmplId()).filter(MatchPattern.SEQ)
                                                                .match("FOLDERID",folderId).filter(MatchPattern.SEQ));
        // 查询样品测试
        List<EmcOrderTaskBean> orderTaskBeanList = orderTaskService.getDao().selectListByOneColumnValue(folderId, "FOLDERID");

        // 根据样品ID来查询样品结果
        List<Long> orderIdList = orderBeanList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        if(orderIdList.size()>0) {
        	List<EmcOrderTaskResultBean> orderTaskResultBeanList = orderTaskResultService.selectListByFilter(SearchFilter.instance().match("ORDERID", orderIdList).filter(MatchPattern.OR)
                    .match("REPORTDISPLAY", Constant.YES).filter(MatchPattern.SEQ)
                    .match("RESULTSTATUS", EmcBizConstant.ORDERTASKRESULT_STATUS_DONE).filter(MatchPattern.SEQ));

            // 更新特殊的指标高低限（昼间评价）
            updateSpecialIndexHighLimit(orderBeanList, orderTaskResultBeanList);

            // 对样品结果进行处理，过滤掉被终止的样品结果（其报出结果为/）
            List<EmcOrderTaskResultBean> validOrderTaskResultList = orderTaskResultBeanList.stream().filter(r -> !"/".equals(r.getReportResult())).collect(Collectors.toList());

            // 平行均值的计算
            calParallelAverageValue(validOrderTaskResultList);

            // 生成报告数据（样品值）
            List<EmcReportResultBean> newReportResultList = new ArrayList<>();
            validOrderTaskResultList.stream().forEach(r -> {
                EmcReportResultBean reportResultBean = new EmcReportResultBean();
                reportResultBean.setId(ApplicationContextHelper.getNextIdentity());
                reportResultBean.setReportId(reportId);
                reportResultBean.setDataType(EmcConstant.PN_SAMPLE);

                EmcOrderBean orderBean = orderBeanList.stream().filter(o -> ObjectUtils.equals(r.getOrderId(), o.getId())).findFirst().get();
                copyProperties(reportResultBean, orderBean, r);
                newReportResultList.add(reportResultBean);
            });

            // 生成报告数据（日均值）, 根据周期和分析项对样品结果进行分组
            Map<String, List<EmcOrderTaskResultBean>> dayTaskResultMap = validOrderTaskResultList.stream().collect(Collectors.groupingBy(e -> e.getCycle() + "$" + e.getItemName()));
            // 计算每个分组的日均值
            Map<String, Double> dayAvgResultMap = calcAvgResult(dayTaskResultMap);
            dayTaskResultMap.entrySet().stream().forEach(t -> {
                EmcReportResultBean reportResultBean = createAvgReportResult(orderBeanList, orderTaskBeanList, dayAvgResultMap, t);
                reportResultBean.setReportId(reportId);
                reportResultBean.setDataType(EmcConstant.PN_DAYAVERAGE);
                newReportResultList.add(reportResultBean);
            });

            // 生成报告数据（次均值），根据周期、频次和分析项对样品结果进行分组
            Map<String, List<EmcOrderTaskResultBean>> perTaskResultMap = validOrderTaskResultList.stream()
                    .collect(Collectors.groupingBy(e -> e.getCycle() + "$" + e.getFrequency() + "$" + e.getItemName()));
            // 计算每个分组的次均值
            Map<String, Double> perAvgResultMap = calcAvgResult(perTaskResultMap);
            perTaskResultMap.entrySet().stream().forEach(t -> {
                EmcReportResultBean reportResultBean = createAvgReportResult(orderBeanList, orderTaskBeanList, perAvgResultMap, t);
                reportResultBean.setReportId(reportId);
                reportResultBean.setDataType(EmcConstant.PN_PERAVERAGE);
                newReportResultList.add(reportResultBean);
            });

            // 通过报表ID、点位ID删除报表结果
            EmcReportResultBean reportResultBean = new EmcReportResultBean();
            reportResultBean.setReportId(reportId);
            reportResultBean.setFolderId(folderId);
            reportResultService.getDao().deleteBy(reportResultBean, "REPORTID", "FOLDERID");

            // 重新插入新的报表结果数据
            reportResultService.getDao().insert(newReportResultList);
        }

    }

    protected void insertAuditRecordByReport(List<EmcReportBean> reportList, String bpmnStatus, String comment, List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList) {
        List<EmcAuditRecordBean> auditRecordList = new ArrayList<>();
        for (EmcReportBean report : reportList) {
            EmcAuditRecordBean auditRecord = new EmcAuditRecordBean();
            auditRecord.setId(ApplicationContextHelper.getNextIdentity());
            auditRecord.setProjId(report.getProjId());
            auditRecord.setProjNo(report.getExt$Item("projno"));
            auditRecord.setComments(comment);
            auditRecord.setLogDesc("项目:" + report.getExt$Item("projno"));
            auditRecord.setBizCategory("report");
            auditRecord.setStepCode("6");
            auditRecord.setAuditCategory("报告管理");

            if ("PROCESS_START".contentEquals(bpmnStatus)) {
                auditRecord.setTaskName("报告编制");
                auditRecord.setOperateCategory("提交");
                auditRecord.setNextTaskName("报告审核");
            } else if ("PROCESS_COMPLETE".equals(bpmnStatus)) {
                auditRecord.setOperateCategory("审核");
                @SuppressWarnings("rawtypes")
                CoreBpmnInstanceStatusDTO bpmnInstance = coreBpmnInstanceStatusDTOList.stream().filter(f -> ObjectUtils.equals(f.getId(), report.getId())).findFirst().orElse(null);
                if ("reportAudit".equals(bpmnInstance.getStatusCode())) {
                    auditRecord.setTaskName("报告审核");
                    auditRecord.setNextTaskName("报告签发");
                } else if ("reportIssue".equals(bpmnInstance.getStatusCode())) {
                    auditRecord.setTaskName("报告签发");
                    auditRecord.setNextTaskName("报告打印");
                } else if ("reportPrint".equals(bpmnInstance.getStatusCode())) {
                    auditRecord.setTaskName("报告打印");
                    auditRecord.setNextTaskName("盖章发送");
                } else if ("reportSend".equals(bpmnInstance.getStatusCode())) {
                    auditRecord.setTaskName("盖章发送");
                    auditRecord.setNextTaskName("报告归档");
                } else if ("reportArchiving".equals(bpmnInstance.getStatusCode())) {
                    auditRecord.setTaskName("报告归档");
                    auditRecord.setNextTaskName("结束");
                }
            } else if ("PROCESS_UNDO".equals(bpmnStatus)) {
                auditRecord.setOperateCategory("退回");
                @SuppressWarnings("rawtypes")
                CoreBpmnInstanceStatusDTO bpmnInstance = coreBpmnInstanceStatusDTOList.stream().filter(f -> ObjectUtils.equals(f.getId(), report.getId())).findFirst().orElse(null);
                if ("reportAudit".equals(bpmnInstance.getStatusCode())) {
                    auditRecord.setTaskName("报告审核");
                    auditRecord.setNextTaskName("报告编制");
                } else if ("reportIssue".equals(bpmnInstance.getStatusCode())) {
                    auditRecord.setTaskName("报告签发");
                    auditRecord.setNextTaskName("报告审核");
                } else if ("reportPrint".equals(bpmnInstance.getStatusCode())) {
                    auditRecord.setTaskName("报告打印");
                    auditRecord.setNextTaskName("报告签发");
                } else if ("reportSend".equals(bpmnInstance.getStatusCode())) {
                    auditRecord.setTaskName("盖章发送");
                    auditRecord.setNextTaskName("报告打印");
                } else if ("reportArchiving".equals(bpmnInstance.getStatusCode())) {
                    auditRecord.setTaskName("报告归档");
                    auditRecord.setNextTaskName("盖章发送");
                }
            }

            auditRecordList.add(auditRecord);
        }
        auditRecordService.getDao().insert(auditRecordList);
    }

    /**
     * 创建均值报告结果
     *
     * @param orderBeanList
     *            样品列表
     * @param avgResultMap
     *            分组均值结果
     * @param entry
     * @return
     */
    private EmcReportResultBean createAvgReportResult(List<EmcOrderBean> orderBeanList, List<EmcOrderTaskBean> orderTaskBeanList, Map<String, Double> avgResultMap,
            Map.Entry<String, List<EmcOrderTaskResultBean>> entry) {
        EmcReportResultBean reportResultBean = new EmcReportResultBean();
        reportResultBean.setId(ApplicationContextHelper.getNextIdentity());
        // 取第一个样品结果
        EmcOrderTaskResultBean taskResultBean = entry.getValue().get(0);

        EmcOrderTaskBean orderTaskBean = orderTaskBeanList.stream().filter(t -> ObjectUtils.equals(t.getId(), taskResultBean.getOrderTaskId())).findFirst().get();
        reportResultBean.setTestName(orderTaskBean.getTestName());

        EmcOrderBean orderBean = orderBeanList.stream().filter(o -> ObjectUtils.equals(taskResultBean.getOrderId(), o.getId())).findFirst().get();
        copyProperties(reportResultBean, orderBean, taskResultBean);
        // 对均值结果进行修约
        String revisionRule = reportResultBean.getRevisionRule();
        Double avgResult = avgResultMap.get(entry.getKey());
        if (avgResult != null && !StringUtils.isEmpty(revisionRule)) {
            String roundValue = NumberUtils.rounding(avgResult + "", revisionRule);
            reportResultBean.setReportResult(roundValue);
        } else {
            reportResultBean.setReportResult(avgResult == null ? "" : avgResult + "");
        }
        return reportResultBean;
    }

    /**
     * 计算均值（日均值和次均值）
     *
     * @param resultMap
     *            分组结果
     * @return 分组均值
     */
    private static Map<String, Double> calcAvgResult(Map<String, List<EmcOrderTaskResultBean>> resultMap) {
        // 若报出结果为 ND，计算均值时使用[检出低限 / 2]替代；若报出结果为 HD，计算均值时使用[检出高限]替代
        // 只有将 ND 或 HD 替换后才能计算均值
        Map<String, List<EmcOrderTaskResultBean>> validResultMap = new HashMap<>();
        resultMap.entrySet().stream().forEach(e -> {
            e.getValue().stream().filter(t -> "ND".equals(t.getReportResult()) || "HD".equals(t.getReportResult())).forEach(m -> {
                if ("HD".equals(m.getReportResult())) {
                    m.setReportResult(m.getHighLimit() + "");
                } else {
                    m.setReportResult((NumberUtils.parseDouble(m.getLowLimit()) / 2) + "");
                }
            });
            validResultMap.put(e.getKey(), e.getValue());
        });

        // 对有效的样品结果求均值
        Map<String, Double> averageResultMap = new HashMap<>();
        validResultMap.entrySet().stream().forEach(e -> {
            e.getValue().stream().filter(r -> NumberUtils.isNumber(r.getReportResult())).mapToDouble(m -> NumberUtils.parseDouble(m.getReportResult())).average()
                    .ifPresent(d -> averageResultMap.put(e.getKey(), d));
        });

        return averageResultMap;
    }

    /**
     * 更新特殊的指标高限（昼夜评价） - 对于与昼夜评价相关联的分析项，其指标高低限维护在属性数据表中
     *
     * @param orderList
     *            样品列表
     * @param taskResultList
     *            样品测试列表
     */
    protected void updateSpecialIndexHighLimit(List<EmcOrderBean> orderList, List<EmcOrderTaskResultBean> taskResultList) {
        // 获取噪声与振动指标分类
        List<EmcIndexCategoryBean> indexCategoryList = indexCategoryService.getDao().selectListByOneColumnValue(EmcConstant.DAY_NIGHT_EVAL_PROPERTY_TMPL_ID, "PROPERTYTMPLID");
        if (indexCategoryList.isEmpty()) {
            return;
        }
        List<Long> indexCategoryIdList = indexCategoryList.stream().map(EmcIndexCategoryBean::getId).collect(Collectors.toList());

        // 获取指标设置
        List<EmcIndexSetBean> indexSetList = indexSetService.getDao().selectListByOneColumnValues(indexCategoryIdList, "INDEXCATEGORYID");
        List<String> orderIndexIdList = indexSetList.stream().map(EmcIndexSetBean::getIndexId).collect(Collectors.toList());
        if (orderIndexIdList.isEmpty()) {
            return;
        }

        // 获取指标设置分析项
        List<Long> indexItemIdList = indexSetList.stream().map(EmcIndexSetBean::getIndexItemId).collect(Collectors.toList());
        if (indexItemIdList.isEmpty()) {
            return;
        }
        List<EmcIndexTestItemBean> indexTestItemList = indexTestItemService.getDao().selectListByOneColumnValues(indexItemIdList, "INDEXITEMID");
        List<Long> itemIdList = indexTestItemList.stream().map(EmcIndexTestItemBean::getItemId).collect(Collectors.toList());

        // 获取样品测试
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");
        List<Long> orderTaskIdList = orderTaskList.stream().filter(t -> orderIndexIdList.contains(t.getIndexId())).map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        if (orderTaskIdList.isEmpty()) {
            return;
        }

        // 获取属性数据
        List<Long> indexSetIdList = indexSetList.stream().map(EmcIndexSetBean::getId).collect(Collectors.toList());
        List<EmcPropertyDataBean> propertyDataList = propertyDataService.getDao().selectListByOneColumnValues(indexSetIdList, "BIZID");

        LocalTime dayStartTime = LocalTime.of(5, 59); // 昼间开始时间 6:00
        LocalTime dayEndTime = LocalTime.of(22, 0); // 昼间结束时间 21:59
        List<EmcOrderTaskResultBean> updateOrderTaskResultList = new ArrayList<>();
        taskResultList.stream().filter(r -> orderTaskIdList.contains(r.getOrderTaskId()) && itemIdList.contains(r.getItemId())).forEach(r -> {
            EmcOrderTaskBean orderTask = orderTaskList.stream().filter(t -> ObjectUtils.equals(t.getId(), r.getOrderTaskId())).findFirst().get();
            indexSetList.stream().filter(s -> ObjectUtils.equals(s.getIndexId(), orderTask.getIndexId())).findFirst().ifPresent(s -> {
                EmcOrderBean orderBean = orderList.stream().filter(o -> ObjectUtils.equals(o.getId(), r.getOrderId())).findFirst().get();
                propertyDataList.stream().filter(d -> ObjectUtils.equals(d.getBizId(), s.getId())).findFirst().ifPresent(d -> {
                    String sampleBeginTime = orderBean.getSampleBeginTime();
                    if (!StringUtils.isEmpty(sampleBeginTime)) {
                        String[] pairs = sampleBeginTime.split(":");
                        LocalTime sampleTime = LocalTime.of(NumberUtils.parseInt(pairs[0]), NumberUtils.parseInt(pairs[1]));
                        if (sampleTime.isAfter(dayStartTime) && sampleTime.isBefore(dayEndTime)) {
                            r.setIndexHighLimit(d.getExt1()); // 昼间指标高限
                        } else {
                            r.setIndexHighLimit(d.getExt2()); // 夜间指标低限
                        }
                        updateOrderTaskResultList.add(r);
                    }
                });
            });
        });

        // 更新相应样品测试结果的指标高低限
        orderTaskResultService.getDao().update(updateOrderTaskResultList, "INDEXLOWLIMIT", "INDEXHIGHLIMIT");
    }

    // 给word生成数据
    protected void setDataToWord(XWPFDocument document, EmcReportBean report, String fileName) {

        // word基础数据容器
        Map<String, Object> dataMap = new HashMap<String, Object>();

        // 主业务流程数据
        EmcProjectBean project = projectService.selectDetail(report.getProjId());
        Long proJId = project.getId();
        List<EmcFolderBean> allFolderList = folderService.selectListByFilter(SearchFilter.instance().match("projId", proJId).filter(MatchPattern.EQ).match("orderCategory", report.getOrderCategory())
                .filter(MatchPattern.EQ).match("termination", "0").filter(MatchPattern.EQ));
        List<EmcFolderBean> folderList = allFolderList.stream().filter(e -> e.getMonitorPointName().indexOf("空白") == -1 && e.getMonitorPointName().indexOf("平行") == -1).collect(Collectors.toList());
        List<Long> folderIdList = folderList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcOrderBean> orderList = orderService.selectListByFilter(SearchFilter.instance().match("folderId", folderIdList).filter(MatchPattern.OR));
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("folderId", folderIdList).filter(MatchPattern.OR));
        List<EmcOrderTaskResultBean> allOrderTaskResultList = orderTaskResultService.selectListByFilter(SearchFilter.instance().match("folderId", folderIdList).filter(MatchPattern.OR));
        List<EmcOrderTaskResultBean> orderTaskResultList = allOrderTaskResultList.stream().filter(e -> "1".equals(e.getReportDisplay())).collect(Collectors.toList());
        List<EmcRunBean> runList = runService
                .selectListByFilter(SearchFilter.instance().match("projId", proJId).filter(MatchPattern.EQ).match("orderCategory", report.getOrderCategory()).filter(MatchPattern.EQ));
        List<EmcSampleBean> sampleList = sampleService
                .selectListByFilter(SearchFilter.instance().match("projId", proJId).filter(MatchPattern.EQ).match("orderCategory", report.getOrderCategory()).filter(MatchPattern.EQ));
        List<EmcReportResultBean> reportResultList = reportResultService
                .selectListByFilter(SearchFilter.instance().match("reportId", report.getId()).filter(MatchPattern.EQ).match("reportDisplay", "1").filter(MatchPattern.EQ));

        // 页眉和标题信息
        dataMap.put("year", LocalDate.now().getYear());
        dataMap.put("reportNo", report.getReportNo());
        String bizCategoryName = project.getExt$Item("bizcategoryname");
        if ("委托检测".equals(bizCategoryName) || "委托监测".equals(bizCategoryName) || "委托（应急）监测".equals(bizCategoryName)) {
            dataMap.put("bizCategory", "委");
        } else if ("监督性监测".equals(bizCategoryName)) {
            dataMap.put("bizCategory", "污");
        } else if ("环境质量监测".equals(bizCategoryName)) {
            dataMap.put("bizCategory", "环");// 待定，未确认
        } else {
            dataMap.put("bizCategory", "");
        }

        // 添加项目信息
        dataMap.put("projName", project.getProjName());
        dataMap.put("projAddress", project.getProjAddress());
        dataMap.put("workCondition", report.getWorkCondition());
        dataMap.put("entrustOrgName", project.getEntrustOrgName());
        dataMap.put("testOrgName", project.getTestOrgName());
        String bizName = bizCategoryName;
        dataMap.put("bizName", bizName);
        dataMap.put("reportDate", LocalDate.now());
        dataMap.put("orderCategory", report.getOrderCategory());

        // 添加其他数据
        if (EmcConstant.WORD_REPORT_TEMPLATE_ENTRUST_REPORT_NO_EVALUATION.equals(fileName)) {

            setDataByEntrustReportNoEvaluation(document, dataMap, report, folderList, orderList, orderTaskList, runList, sampleList, reportResultList);

        } else if (EmcConstant.WORD_REPORT_TEMPLATE_ENTRUST_REPORT_WITH_EVALUATION.equals(fileName)) {

            setDataByEntrustReportWithEvaluation(document, dataMap, report, folderList, orderList, orderTaskList, runList, sampleList, reportResultList);

        } else if (EmcConstant.WORD_REPORT_TEMPLATE_NORMAL_REPORT_WITHOUT_NOISE.equals(fileName)) {

            setDataByNormalReportWithoutNoise(document, dataMap, folderList, orderList, orderTaskList, runList, sampleList, reportResultList);

        } else if (EmcConstant.WORD_REPORT_TEMPLATE_NORMAL_REPORT_NOISE.equals(fileName)) {

            setDataByNormalReportNoise(document, dataMap, folderList, orderList, orderTaskList, orderTaskResultList, runList, sampleList, reportResultList);

        } else if (EmcConstant.WORD_REPORT_TEMPLATE_SAMPLING_REPORT.equals(fileName)) {

            setDataBySamplingReport(document, dataMap, report, folderList, orderList, runList, reportResultList);

        } else if (EmcConstant.WORD_REPORT_TEMPLATE_SUPERVISAL_MONITORING.equals(fileName)) {

            setDataBySupervisalMonitoring(document, dataMap, report, folderList, orderList, orderTaskList, runList, sampleList, reportResultList);
        }

        // 将表格中单位信息格式化
        Word2007Utils.formatUnit(document);
    }

    // 给监督性报告生成数据
    private void setDataBySupervisalMonitoring(XWPFDocument document, Map<String, Object> dataMap, EmcReportBean report, List<EmcFolderBean> folderList, List<EmcOrderBean> orderList,
            List<EmcOrderTaskBean> orderTaskList, List<EmcRunBean> runList, List<EmcSampleBean> sampleList, List<EmcReportResultBean> reportResultList) {
        // 替换基础数据
        setBasicDataForEntrustReport(document, dataMap, folderList, orderTaskList, sampleList);
        List<String> monitorPointNoList = folderList.stream().map(e -> e.getMonitorPointNo()).collect(Collectors.toList());
        String monitorPointNos = StringUtils.join(monitorPointNoList, "、");
        dataMap.put("monitorPointNos", monitorPointNos);

        List<String> monitorPointNameList = folderList.stream().map(e -> e.getMonitorPointName()).collect(Collectors.toList());
        String monitorPointNames = StringUtils.join(monitorPointNameList, "、");
        mapNullToString(dataMap, "");
        dataMap.put("monitorPointNames", monitorPointNames);

        Word2007Utils.replaceAll(document, dataMap);

        // 为样品表插入数据
        List<List<Object>> orderTableDataList = setTableDataForOrder(dataMap, orderList, orderTaskList, runList, reportResultList,
                dataMap.get("sampleWay") == null ? "" : dataMap.get("sampleWay").toString());
        listListNullToString(orderTableDataList, "-");
        Word2007Utils.setDataToTable(document.getTables().get(1), orderTableDataList);

        // 为测试表插入数据
        List<List<Object>> testTableDataList = setTableDataForTest(reportResultList);
        listListNullToString(testTableDataList, "-");
        Word2007Utils.setDataToTable(document.getTables().get(2), testTableDataList);

        // 生成结果表
        List<String> evaluationList = new ArrayList<>();// 总评价信息

        String monitorTmplGroup = report.getExt$Item("monitortmplgroup");

        if ("水（含大气降水）和废水".equals(monitorTmplGroup) || "土壤和沉积物".equals(monitorTmplGroup)) {

            // 废水监测结果表
            evaluationList = createResultTableWithEvaluation(document, dataMap, report, folderList, orderList, reportResultList);

        } else if ("空气和废气".equals(monitorTmplGroup)) {

            // 拿到所有测试名称，分类分表展示数据（都是一个点位一张表）
            List<String> testNameList = reportResultList.stream().map(e -> e.getTestName()).distinct().collect(Collectors.toList());

            // 按点位区分超标与未超标测试
            List<Long> allFolderIdList = reportResultList.stream().map(e -> e.getFolderId()).distinct().collect(Collectors.toList());
            Map<Long, ArrayList<String>> upToStandTestMap = new HashMap<Long, ArrayList<String>>();// 达标
            Map<Long, ArrayList<String>> notUpToStandTestMap = new HashMap<Long, ArrayList<String>>();// 未达标
            allFolderIdList.forEach(e -> {
                upToStandTestMap.put(e, new ArrayList<String>());
                notUpToStandTestMap.put(e, new ArrayList<String>());
            });

            // 燃料全硫监测结果表
            if (testNameList.contains("全硫")) {

                setDataForSOfGasTable(document, reportResultList);
            }
            // 烟气黑度监测结果表
            if (testNameList.contains("烟气黑度")) {

                setDataForBlankOfGasTable(document, folderList, reportResultList, upToStandTestMap, notUpToStandTestMap);
            }

            // 除去特殊展示五项测试的其他烟气污染物监测结果表（一个测试一张表，一张表展示所有点位）
            setDataForOtherGasOfGasTable(document, folderList, sampleList, reportResultList, upToStandTestMap, notUpToStandTestMap);

            // 废气污染物监测结果表（二氧化硫、氮氧化物、颗粒物）
            if (testNameList.contains("二氧化硫") || testNameList.contains("氮氧化物") || testNameList.contains("颗粒物")) {

                setDataForExhaustGasOfGasTable(document, folderList, sampleList, reportResultList, upToStandTestMap, notUpToStandTestMap);
            }

            // 统计超标与未超标测试，生成评价语
            for (int i = 0; i < allFolderIdList.size(); i++) {

                Long folderId = allFolderIdList.get(i);
                EmcFolderBean folder = folderList.stream().filter(e -> ObjectUtils.equals(folderId, e.getId())).findFirst().orElse(null);

                String upToStandTests = StringUtils.join(upToStandTestMap.get(folderId), "、");
                String notUpToStandTests = StringUtils.join(notUpToStandTestMap.get(folderId), "、");

                StringBuilder evaluation = new StringBuilder();
                evaluation.append("（").append(i + 1).append("）从监测结果看，该企业").append(folder.getMonitorPointName()).append("(编号：").append(folder.getMonitorPointNo()).append("）排放的污染物：")
                        .append(upToStandTests).append("均达标，").append(notUpToStandTests).append("均未达标。");
                evaluationList.add(evaluation.toString());
            }

        } else if ("噪声和振动".equals(monitorTmplGroup)) {

            // 社会生活环境噪声监测结果表
            // 根据分析项名称为昼间/夜间的值，来分组，从而分表
            List<Long> orderIdForDayList = reportResultList.stream().filter(e -> ObjectUtils.equals("昼间/夜间", e.getItemName()) && ObjectUtils.equals("昼间", e.getReportResult())).map(e -> e.getOrderId())
                    .distinct().collect(Collectors.toList());
            List<Long> orderIdForNightList = reportResultList.stream().filter(e -> ObjectUtils.equals("昼间/夜间", e.getItemName()) && ObjectUtils.equals("夜间", e.getReportResult()))
                    .map(e -> e.getOrderId()).distinct().collect(Collectors.toList());

            setDataForNoiseTable(document, dataMap, reportResultList, evaluationList, orderIdForNightList, "夜");
            setDataForNoiseTable(document, dataMap, reportResultList, evaluationList, orderIdForDayList, "昼");
        }

        Word2007Utils.removeKey(document, "resultTableInfo");// 移除关键字
        Word2007Utils.addText(document, "appraises", evaluationList); // 添加评价语

        // 生成点位示意图
        List<CoreFileBean> pointDiagramFileList = coreFileService.selectListByFilter(
                SearchFilter.instance().match("targetId", getDao().getTable() + "$" + report.getId()).filter(MatchPattern.EQ).match("bizCategory", "pointDiagram").filter(MatchPattern.EQ));
        if (!pointDiagramFileList.isEmpty()) {
            CoreFileBean pointDiagramFile = pointDiagramFileList.get(0);// 一个报告只有一个点位示意图

            String pointDiagramUrl = FilePathManager.getLocalPath(FilePathDTO.of(pointDiagramFile.getScope(), DateTimeUtils.getDateSerialNumber(pointDiagramFile.getCreatedTime()), pointDiagramFile.getMd5Name(), pointDiagramFile.getName()))
                    .toString();

            Word2007Utils.insertImageAfterParagraphOfKey("监测布点及示意图：", document, pointDiagramUrl);
        } else {
            Word2007Utils.deleteParagraph(document, "监测布点及示意图：");
        }
    }

    // 为气类型的表添加其他测试名的烟气污染物的表
    private void setDataForOtherGasOfGasTable(XWPFDocument document, List<EmcFolderBean> folderList, List<EmcSampleBean> sampleList, List<EmcReportResultBean> reportResultList,
            Map<Long, ArrayList<String>> upToStandTestMap, Map<Long, ArrayList<String>> notUpToStandTestMap) {

        String[] testNameArray = { "二氧化硫", "氮氧化物", "颗粒物", "烟气黑度", "全硫" };

        List<EmcReportResultBean> resultForOtherGasList = reportResultList.stream().filter(e -> !ArrayUtils.asList(testNameArray).contains(e.getTestName()) && e.getTestName().indexOf("参数") == -1)
                .collect(Collectors.toList());
        List<String> testNameForOtherList = resultForOtherGasList.stream().map(e -> e.getTestName()).distinct().collect(Collectors.toList());

        if (testNameForOtherList != null && testNameForOtherList.size() > 0) {

            for (int i = 0; i < testNameForOtherList.size(); i++) {
                String testName = testNameForOtherList.get(i);
                List<List<Object>> resultTableDataList = new ArrayList<List<Object>>();

                // 表格中表头数据
                List<Object> resultHeadRowOneDataList = new ArrayList<Object>();

                resultHeadRowOneDataList.add("炉型");
                resultHeadRowOneDataList.add("监测位置");
                resultHeadRowOneDataList.add("燃料类型");
                resultHeadRowOneDataList.add("烟囱高度（m）");
                resultHeadRowOneDataList.add(testName + "（mg/m3）");
                resultHeadRowOneDataList.add("");
                resultHeadRowOneDataList.add("烟气流量(m3/h)");

                resultTableDataList.add(resultHeadRowOneDataList);

                List<Object> resultHeadRowTwoDataList = new ArrayList<Object>();

                for (int j = 0; j < 4; j++) {
                    resultHeadRowTwoDataList.add("");
                }
                resultHeadRowTwoDataList.add("排放浓度");
                resultHeadRowTwoDataList.add("折算浓度");
                resultHeadRowTwoDataList.add("");

                resultTableDataList.add(resultHeadRowTwoDataList);

                List<EmcReportResultBean> resultForTestList = reportResultList.stream().filter(e -> testName.equals(e.getTestName())).collect(Collectors.toList());
                List<Long> folderIdList = resultForTestList.stream().map(e -> e.getFolderId()).distinct().collect(Collectors.toList());
                List<EmcFolderBean> folderForTestList = folderList.stream().filter(e -> folderIdList.contains(e.getId())).collect(Collectors.toList());

                for (int j = 0; j < folderForTestList.size(); j++) {

                    // 结果均值那一行数据
                    List<Object> resultRowDataForAverageList = new ArrayList<Object>();

                    // 采样单属性,监测点位置
                    EmcFolderBean folder = folderForTestList.get(j);
                    Long folderId = folder.getId();
                    List<EmcReportResultBean> resultForFolderList = resultForTestList.stream().filter(e -> ObjectUtils.equals(folder.getId(), e.getFolderId())).collect(Collectors.toList());

                    setSamplePropertiesForGasTable(sampleList, resultRowDataForAverageList, folder, resultForFolderList);

                    // 通用的2行数据
                    // 标准限值
                    List<Object> resultRowForStandLimitDataList = new ArrayList<Object>();
                    resultRowForStandLimitDataList.add("标准限值");
                    // 达标判定
                    List<Object> resultRowForStandJudgeDataList = new ArrayList<Object>();
                    resultRowForStandJudgeDataList.add("达标判定");
                    for (int k = 0; k < 3; k++) {
                        resultRowForStandLimitDataList.add("");
                        resultRowForStandJudgeDataList.add("");
                    }

                    // 报出结果均值（取所有样品均值），标准限值，达标判定
                    setDataForTestOfGasTable(upToStandTestMap, notUpToStandTestMap, resultTableDataList, resultRowDataForAverageList, resultRowForStandLimitDataList, resultRowForStandJudgeDataList,
                            folderId, resultForFolderList, testName, "排放浓度");
                    setDataForTestOfGasTable(upToStandTestMap, notUpToStandTestMap, resultTableDataList, resultRowDataForAverageList, resultRowForStandLimitDataList, resultRowForStandJudgeDataList,
                            folderId, resultForFolderList, testName, "折算浓度");
                    setDataForTestOfGasTable(upToStandTestMap, notUpToStandTestMap, resultTableDataList, resultRowDataForAverageList, resultRowForStandLimitDataList, resultRowForStandJudgeDataList,
                            folderId, resultForFolderList, testName, "烟气流量");
                    resultTableDataList.add(resultRowDataForAverageList);
                    resultTableDataList.add(resultRowForStandLimitDataList);
                    resultTableDataList.add(resultRowForStandJudgeDataList);
                }

                // 执行标准
                List<Object> resultRowForStandDataList = new ArrayList<Object>();
                resultRowForStandDataList.add("执行标准");
                // 备注信息
                List<Object> resultRowDataForRemarkList = new ArrayList<Object>();
                resultRowDataForRemarkList.add("备注");
                for (int k = 0; k < 3; k++) {
                    resultRowForStandDataList.add("");
                    resultRowDataForRemarkList.add("");
                }
                // 执行标准，备注信息
                List<String> standList = resultForTestList.stream().map(e -> e.getExt$Item("indexname")).distinct().collect(Collectors.toList());
                String stands = StringUtils.join(standList, "、");
                resultRowForStandDataList.add(stands);
                resultRowDataForRemarkList.add("“L”表示低于检出限");

                // 补齐表格
                for (int k = 0; k < 2; k++) {
                    resultRowForStandDataList.add("");
                    resultRowDataForRemarkList.add("");
                }
                resultTableDataList.add(resultRowForStandDataList);
                resultTableDataList.add(resultRowDataForRemarkList);

                // 创建表
                listListNullToString(resultTableDataList, "-");

                Word2007Utils.createTable(document, "resultTableInfo", "烟气污染物" + testName + "监测结果", null, resultTableDataList, null, null, null, null);

                // 调整表格格式（合并列）
                XWPFTable table = document.getTables().get(3);
                for (int j = 0; j < 4; j++) {
                    Word2007Utils.mergeTableRows(table, j, 0, 1);
                }
                Word2007Utils.mergeTableRows(table, 6, 0, 1);

                Word2007Utils.mergeTableColumns(table, 0, 4, 5);
                int folderCount = folderForTestList.size();
                for (int j = 1; j <= folderCount; j++) {
                    Word2007Utils.mergeTableColumns(table, j * 3, 0, 3);
                    Word2007Utils.mergeTableColumns(table, j * 3 + 1, 0, 3);
                }
                Word2007Utils.mergeTableColumns(table, folderCount * 3 + 2, 0, 3);
                Word2007Utils.mergeTableColumns(table, folderCount * 3 + 3, 0, 3);
                Word2007Utils.mergeTableColumns(table, folderCount * 3 + 2, 4, 6);
                Word2007Utils.mergeTableColumns(table, folderCount * 3 + 3, 4, 6);
            }
        }
    }

    // 为气类型的表添加测试名为二氧化硫、氮氧化物、颗粒物的表
    private void setDataForExhaustGasOfGasTable(XWPFDocument document, List<EmcFolderBean> folderList, List<EmcSampleBean> sampleList, List<EmcReportResultBean> reportResultList,
            Map<Long, ArrayList<String>> upToStandTestMap, Map<Long, ArrayList<String>> notUpToStandTestMap) {
        List<EmcReportResultBean> resultForExhaustGasList = reportResultList.stream().filter(e -> "二氧化硫".equals(e.getTestName()) || "氮氧化物".equals(e.getTestName()) || "颗粒物".equals(e.getTestName()))
                .collect(Collectors.toList());
        List<Long> folderIdList = resultForExhaustGasList.stream().map(e -> e.getFolderId()).distinct().collect(Collectors.toList());
        List<EmcFolderBean> folderForExhaustGasList = folderList.stream().filter(e -> folderIdList.contains(e.getId())).collect(Collectors.toList());

        // 一个点位一张表
        for (int i = 0; i < folderForExhaustGasList.size(); i++) {
            List<List<Object>> resultTableDataList = new ArrayList<List<Object>>();

            // 表格中表头数据
            List<Object> resultHeadRowOneDataList = new ArrayList<Object>();

            resultHeadRowOneDataList.add("炉型");
            resultHeadRowOneDataList.add("监测位置");
            resultHeadRowOneDataList.add("燃料类型");
            resultHeadRowOneDataList.add("烟囱高度（m）");
            resultHeadRowOneDataList.add("样品编号");
            resultHeadRowOneDataList.add("");
            resultHeadRowOneDataList.add("二氧化硫（mg/m3）");
            resultHeadRowOneDataList.add("");
            resultHeadRowOneDataList.add("氮氧化物（mg/m3）");
            resultHeadRowOneDataList.add("");
            resultHeadRowOneDataList.add("颗粒物（mg/m3）");
            resultHeadRowOneDataList.add("");
            resultHeadRowOneDataList.add("标干流量(m3/h)");

            resultTableDataList.add(resultHeadRowOneDataList);

            List<Object> resultHeadRowTwoDataList = new ArrayList<Object>();

            for (int j = 0; j < 6; j++) {
                resultHeadRowTwoDataList.add("");
            }
            for (int j = 0; j < 3; j++) {
                resultHeadRowTwoDataList.add("实测浓度");
                resultHeadRowTwoDataList.add("折算浓度");
            }
            resultHeadRowTwoDataList.add("");

            resultTableDataList.add(resultHeadRowTwoDataList);

            // 结果均值那一行数据
            List<Object> resultRowDataForAverageList = new ArrayList<Object>();

            // 采样单属性,监测点位置
            EmcFolderBean folder = folderForExhaustGasList.get(i);
            Long folderId = folder.getId();
            List<EmcReportResultBean> resultForFolderList = resultForExhaustGasList.stream().filter(e -> ObjectUtils.equals(folder.getId(), e.getFolderId())).collect(Collectors.toList());

            setSamplePropertiesForGasTable(sampleList, resultRowDataForAverageList, folder, resultForFolderList);

            // 样品编号
            List<String> orderNoList = resultForFolderList.stream().map(e -> e.getOrderNo()).distinct().collect(Collectors.toList());
            resultRowDataForAverageList.add(StringUtils.join(orderNoList, "、"));
            resultRowDataForAverageList.add("");

            // 通用的4行数据
            // 标准限值
            List<Object> resultRowForStandLimitDataList = new ArrayList<Object>();
            resultRowForStandLimitDataList.add("标准限值");
            // 达标判定
            List<Object> resultRowForStandJudgeDataList = new ArrayList<Object>();
            resultRowForStandJudgeDataList.add("达标判定");
            // 执行标准
            List<Object> resultRowForStandDataList = new ArrayList<Object>();
            resultRowForStandDataList.add("执行标准");
            // 备注信息
            List<Object> resultRowDataForRemarkList = new ArrayList<Object>();
            resultRowDataForRemarkList.add("备注");
            for (int j = 0; j < 5; j++) {
                resultRowForStandLimitDataList.add("");
                resultRowForStandJudgeDataList.add("");
                resultRowForStandDataList.add("");
                resultRowDataForRemarkList.add("");
            }

            // 报出结果均值（取所有样品均值），标准限值，达标判定
            setDataForTestOfGasTable(upToStandTestMap, notUpToStandTestMap, resultTableDataList, resultRowDataForAverageList, resultRowForStandLimitDataList, resultRowForStandJudgeDataList, folderId,
                    resultForFolderList, "二氧化硫", "实测浓度");
            setDataForTestOfGasTable(upToStandTestMap, notUpToStandTestMap, resultTableDataList, resultRowDataForAverageList, resultRowForStandLimitDataList, resultRowForStandJudgeDataList, folderId,
                    resultForFolderList, "二氧化硫", "折算浓度");
            setDataForTestOfGasTable(upToStandTestMap, notUpToStandTestMap, resultTableDataList, resultRowDataForAverageList, resultRowForStandLimitDataList, resultRowForStandJudgeDataList, folderId,
                    resultForFolderList, "氮氧化物", "实测浓度");
            setDataForTestOfGasTable(upToStandTestMap, notUpToStandTestMap, resultTableDataList, resultRowDataForAverageList, resultRowForStandLimitDataList, resultRowForStandJudgeDataList, folderId,
                    resultForFolderList, "氮氧化物", "折算浓度");
            setDataForTestOfGasTable(upToStandTestMap, notUpToStandTestMap, resultTableDataList, resultRowDataForAverageList, resultRowForStandLimitDataList, resultRowForStandJudgeDataList, folderId,
                    resultForFolderList, "颗粒物", "实测浓度");
            setDataForTestOfGasTable(upToStandTestMap, notUpToStandTestMap, resultTableDataList, resultRowDataForAverageList, resultRowForStandLimitDataList, resultRowForStandJudgeDataList, folderId,
                    resultForFolderList, "颗粒物", "折算浓度");
            setDataForTestOfGasTable(upToStandTestMap, notUpToStandTestMap, resultTableDataList, resultRowDataForAverageList, resultRowForStandLimitDataList, resultRowForStandJudgeDataList, folderId,
                    resultForFolderList, "颗粒物", "标干流量");

            resultTableDataList.add(resultRowDataForAverageList);
            resultTableDataList.add(resultRowForStandLimitDataList);
            resultTableDataList.add(resultRowForStandJudgeDataList);

            // 执行标准，备注信息
            List<String> standList = resultForFolderList.stream().map(e -> e.getExt$Item("indexname")).distinct().collect(Collectors.toList());
            String stands = StringUtils.join(standList, "、");
            resultRowForStandDataList.add(stands);
            resultRowDataForRemarkList.add("“L”表示低于检出限");

            // 补齐表格
            for (int k = 0; k < 6; k++) {
                resultRowForStandDataList.add("");
                resultRowDataForRemarkList.add("");
            }
            resultTableDataList.add(resultRowForStandDataList);
            resultTableDataList.add(resultRowDataForRemarkList);

            // 创建表
            listListNullToString(resultTableDataList, "-");

            Word2007Utils.createTable(document, "resultTableInfo", "废气污染物监测结果", null, resultTableDataList, null, null, null, null);

            // 调整表格格式（合并列）
            XWPFTable table = document.getTables().get(3);
            for (int j = 0; j < 6; j++) {
                Word2007Utils.mergeTableRows(table, j, 0, 1);
            }
            Word2007Utils.mergeTableRows(table, 12, 0, 1);

            for (int j = 0; j < 3; j++) {
                Word2007Utils.mergeTableColumns(table, j, 4, 5);
            }
            for (int j = 3; j < 7; j++) {
                Word2007Utils.mergeTableColumns(table, j, 0, 5);
            }
            for (int j = 5; j < 7; j++) {
                Word2007Utils.mergeTableColumns(table, j, 6, 12);
            }
            Word2007Utils.mergeTableColumns(table, 0, 6, 7);
            Word2007Utils.mergeTableColumns(table, 0, 8, 9);
            Word2007Utils.mergeTableColumns(table, 0, 10, 11);
        }
    }

    // 为气类型的表添加测试名为烟气黑度的表
    private void setDataForBlankOfGasTable(XWPFDocument document, List<EmcFolderBean> folderList, List<EmcReportResultBean> reportResultList, Map<Long, ArrayList<String>> upToStandTestMap,
            Map<Long, ArrayList<String>> notUpToStandTestMap) {
        List<EmcReportResultBean> resultForBlankList = reportResultList.stream().filter(e -> "烟气黑度".equals(e.getTestName())).collect(Collectors.toList());
        List<Long> folderIdList = resultForBlankList.stream().map(e -> e.getFolderId()).distinct().collect(Collectors.toList());
        List<EmcFolderBean> folderForBlankList = folderList.stream().filter(e -> folderIdList.contains(e.getId())).collect(Collectors.toList());

        // 一个点位一张表
        for (int i = 0; i < folderForBlankList.size(); i++) {
            List<List<Object>> resultTableDataList = new ArrayList<List<Object>>();

            // 表格中表头数据
            List<Object> resultHeadRowDataList = new ArrayList<Object>();

            resultHeadRowDataList.add("炉型");
            resultHeadRowDataList.add("燃料类型");
            resultHeadRowDataList.add("烟囱高度（m）");
            resultHeadRowDataList.add("烟气黑度（林格曼黑度，级）");
            resultHeadRowDataList.add("评价标准（级）");
            resultHeadRowDataList.add("结果判断");

            resultTableDataList.add(resultHeadRowDataList);

            // 报出结果
            Long folderId = folderForBlankList.get(i).getId();
            List<EmcReportResultBean> resultForFolderList = resultForBlankList.stream().filter(e -> ObjectUtils.equals(folderId, e.getFolderId())).collect(Collectors.toList());
            List<String> orderNoList = resultForFolderList.stream().map(e -> e.getOrderNo()).distinct().collect(Collectors.toList());

            // 一个样品展示一行数据
            for (int j = 0; j < orderNoList.size(); j++) {

                String orderNo = orderNoList.get(j);
                List<EmcReportResultBean> resultForOrderList = resultForFolderList.stream().filter(e -> ObjectUtils.equals(orderNo, e.getOrderNo())).collect(Collectors.toList());

                EmcReportResultBean resultForStoveType = resultForOrderList.stream().filter(e -> "炉型".equals(e.getItemName())).findFirst().orElse(null);
                EmcReportResultBean resultForFuelType = resultForOrderList.stream().filter(e -> "燃料类型".equals(e.getItemName())).findFirst().orElse(null);
                EmcReportResultBean resultForHeight = resultForOrderList.stream().filter(e -> "烟囱高度".equals(e.getItemName())).findFirst().orElse(null);
                EmcReportResultBean resultForBlank = resultForOrderList.stream().filter(e -> "烟气黑度".equals(e.getItemName())).findFirst().orElse(null);

                List<Object> resultRowDataList = new ArrayList<Object>();

                resultRowDataList.add(resultForStoveType != null ? resultForStoveType.getReportResult() : "-");
                resultRowDataList.add(resultForFuelType != null ? resultForFuelType.getReportResult() : "-");
                resultRowDataList.add(resultForHeight != null ? resultForHeight.getReportResult() : "-");
                resultRowDataList.add(resultForBlank != null ? resultForBlank.getReportResult() : "-");
                resultRowDataList.add("1");// 评价标准
                if (resultForBlank != null && "1".equals(resultForBlank.getExceedStandard())) {
                    resultRowDataList.add("不达标");
                    notUpToStandTestMap.get(folderId).add("全硫");
                } else {
                    resultRowDataList.add("达标");
                    upToStandTestMap.get(folderId).add("全硫");
                }

                resultTableDataList.add(resultRowDataList);
            }

            // 执行标准
            List<Object> resultRowDataForStanderList = new ArrayList<Object>();
            List<String> standerList = resultForFolderList.stream().map(e -> e.getExt$Item("indexname")).distinct().collect(Collectors.toList());
            String standers = StringUtils.join(standerList, "、");
            resultRowDataForStanderList.add("执行标准");
            resultRowDataForStanderList.add(standers);

            // 填补数据
            for (int j = 0; j < 4; j++) {
                resultRowDataForStanderList.add("");
            }
            resultTableDataList.add(resultRowDataForStanderList);

            // 创建表
            listListNullToString(resultTableDataList, "-");

            Word2007Utils.createTable(document, "resultTableInfo", "烟气黑度监测结果", null, resultTableDataList, null, null, null, null);

            // 调整表格格式（合并列）
            XWPFTable table = document.getTables().get(3);
            Word2007Utils.mergeTableColumns(table, orderNoList.size() + 1, 1, 5);
        }
    }

    // 为气类型的表添加测试名为全硫的表
    private void setDataForSOfGasTable(XWPFDocument document, List<EmcReportResultBean> reportResultList) {
        List<EmcReportResultBean> resultForSList = reportResultList.stream().filter(e -> "全硫".equals(e.getTestName())).collect(Collectors.toList());
        List<Long> folderIdList = resultForSList.stream().map(e -> e.getFolderId()).distinct().collect(Collectors.toList());

        // 一个点位一张表
        folderIdList.forEach(folderId -> {

            List<List<Object>> resultTableDataList = new ArrayList<List<Object>>();

            // 表格中表头数据
            List<Object> resultHeadRowDataList = new ArrayList<Object>();

            resultHeadRowDataList.add("监测项目");
            resultHeadRowDataList.add("样品编码");
            resultHeadRowDataList.add("燃料种类");
            resultHeadRowDataList.add("监测结果(%)");

            resultTableDataList.add(resultHeadRowDataList);

            // 报出结果
            List<EmcReportResultBean> resultForFolderList = resultForSList.stream().filter(e -> ObjectUtils.equals(folderId, e.getFolderId())).collect(Collectors.toList());
            List<String> orderNoList = resultForFolderList.stream().map(e -> e.getOrderNo()).distinct().collect(Collectors.toList());

            // 一个样品展示一行数据
            orderNoList.forEach(orderNo -> {
                List<EmcReportResultBean> resultForOrderList = resultForFolderList.stream().filter(e -> ObjectUtils.equals(orderNo, e.getOrderNo())).collect(Collectors.toList());
                EmcReportResultBean resultForFuelType = resultForOrderList.stream().filter(e -> "燃料类型".equals(e.getItemName())).findFirst().orElse(null);
                EmcReportResultBean resultForPercent = resultForOrderList.stream().filter(e -> "含硫量".equals(e.getItemName())).findFirst().orElse(null);

                List<Object> resultRowDataList = new ArrayList<Object>();
                resultRowDataList.add("全硫");
                resultRowDataList.add(orderNo);
                resultRowDataList.add(resultForFuelType != null ? resultForFuelType.getReportResult() : "-");
                resultRowDataList.add(resultForPercent != null ? resultForPercent.getReportResult() : "-");
                resultTableDataList.add(resultRowDataList);
            });

            // 创建表
            listListNullToString(resultTableDataList, "-");

            Word2007Utils.createTable(document, "resultTableInfo", "燃料全硫监测结果", null, resultTableDataList, null, null, null, null);
        });
    }

    // 为气类型的表添加采样单属性,监测点位置
    private void setSamplePropertiesForGasTable(List<EmcSampleBean> sampleList, List<Object> resultRowDataForAverageList, EmcFolderBean folder, List<EmcReportResultBean> resultForFolderList) {
        List<Long> sampleIdList = resultForFolderList.stream().map(e -> e.getSampleId()).distinct().collect(Collectors.toList());
        List<EmcSampleBean> sampleOfFolderList = sampleList.stream().filter(e -> sampleIdList.contains(e.getId())).collect(Collectors.toList());

        String stoveType = null;
        String fuelType = null;
        String tunHeight = null;
        // 取第一个采样单的采样单属性
        if (!sampleOfFolderList.isEmpty()) {

            EmcSampleBean sample = sampleOfFolderList.get(0);
            Long propertyTmplId = NumberUtils.parseLong(sample.getExt$Item("tmplid"));
            List<EmcPropertyFieldBean> propertyFieldList = propertyFieldService.selectListByFilter(SearchFilter.instance().match("tmplId", propertyTmplId).filter(MatchPattern.EQ));

            List<EmcPropertyDataBean> propertyDataList = propertyDataService
                    .selectListByFilter(SearchFilter.instance().match("tmplId", propertyTmplId).filter(MatchPattern.EQ).match("bizId", sample.getId()).filter(MatchPattern.EQ));
            if (!propertyDataList.isEmpty()) {
                Map<String, Object> propertyDataMap = BeanUtils.toMap(propertyDataList.get(0));

                EmcPropertyFieldBean propertyFieldForStoveType = propertyFieldList.stream().filter(e -> ObjectUtils.equals("炉型", e.getFieldName())).findFirst().orElse(null);
                String propertyFieldNameForStoveType = propertyFieldForStoveType == null ? "" : propertyFieldForStoveType.getInternalField();
                stoveType = propertyDataMap.get(propertyFieldNameForStoveType) == null ? "-" : (String) propertyDataMap.get(propertyFieldNameForStoveType);

                EmcPropertyFieldBean propertyFieldForFuelType = propertyFieldList.stream().filter(e -> ObjectUtils.equals("燃料类型", e.getFieldName())).findFirst().orElse(null);
                String propertyFieldNameForFuelType = propertyFieldForFuelType == null ? "" : propertyFieldForFuelType.getInternalField();
                fuelType = propertyDataMap.get(propertyFieldNameForFuelType) == null ? "-" : (String) propertyDataMap.get(propertyFieldNameForFuelType);

                EmcPropertyFieldBean propertyFieldForTunHeight = propertyFieldList.stream().filter(e -> ObjectUtils.equals("烟囱高度", e.getFieldName())).findFirst().orElse(null);
                String propertyFieldNameForTunHeight = propertyFieldForTunHeight == null ? "" : propertyFieldForTunHeight.getInternalField();
                tunHeight = propertyDataMap.get(propertyFieldNameForTunHeight) == null ? "-" : (String) propertyDataMap.get(propertyFieldNameForTunHeight);
            }
        }

        resultRowDataForAverageList.add(stoveType);
        resultRowDataForAverageList.add(folder.getMonitorPointName());
        resultRowDataForAverageList.add(fuelType);
        resultRowDataForAverageList.add(tunHeight);
    }

    // 为气类型的表添加指定测试和分析项的报出结果，指标限，达标判定
    private void setDataForTestOfGasTable(Map<Long, ArrayList<String>> upToStandTestMap, Map<Long, ArrayList<String>> notUpToStandTestMap, List<List<Object>> resultTableDataList,
            List<Object> resultRowDataForAverageList, List<Object> resultRowForStandLimitDataList, List<Object> resultRowForStandJudgeDataList, Long folderId,
            List<EmcReportResultBean> resultForFolderList, String testName, String itemName) {
        List<EmcReportResultBean> resultByTestAndItemNameList = resultForFolderList.stream()
                .filter(r -> ObjectUtils.equals(testName, r.getTestName()) && r.getItemName() != null && r.getItemName().contains(itemName)).collect(Collectors.toList());
        String averageResult = average(resultByTestAndItemNameList);
        resultRowDataForAverageList.add(averageResult);

        if (resultByTestAndItemNameList != null && resultByTestAndItemNameList.size() > 0) {
            EmcReportResultBean resultByTestAndItemName = resultByTestAndItemNameList.get(0);
            resultRowForStandLimitDataList.add(resultByTestAndItemName.getLowLimit());
            if ("1".equals(resultByTestAndItemName.getExceedStandard())) {
                resultRowForStandJudgeDataList.add("不达标");
                notUpToStandTestMap.get(folderId).add(testName);
            } else {
                resultRowForStandJudgeDataList.add("达标");
                upToStandTestMap.get(folderId).add(testName);
            }
        } else {
            resultRowForStandLimitDataList.add("-");
            resultRowForStandJudgeDataList.add("-");
            upToStandTestMap.get(folderId).add(testName);
        }
    }

    // 为送样报告生成数据
    private void setDataBySamplingReport(XWPFDocument document, Map<String, Object> dataMap, EmcReportBean report, List<EmcFolderBean> folderList, List<EmcOrderBean> orderList,
            List<EmcRunBean> runList, List<EmcReportResultBean> reportResultList) {

        // 替换基础数据
        LocalDate sendBeginDate = orderList.stream().filter(e -> e.getSendDate() != null).map(e -> e.getSendDate()).min(Comparator.naturalOrder()).orElse(null);
        LocalDate sendCompleteDate = orderList.stream().filter(e -> e.getSendDate() != null).map(e -> e.getSendDate()).max(Comparator.naturalOrder()).orElse(null);
        String sendDate = getDateZones(sendBeginDate, sendCompleteDate);
        dataMap.put("sendDate", sendDate);

        List<String> orderNoList = orderList.stream().filter(e -> e.getOrderNo() != null).map(e -> e.getOrderNo()).distinct().collect(Collectors.toList());
        String orderNos = StringUtils.join(orderNoList, "、");
        dataMap.put("orderNo", orderNos);

        LocalDate analysisBeginDate = runList.stream().map(e -> e.getAnalysisBeginDate()).min(Comparator.naturalOrder()).orElse(null);
        LocalDate analysisEndDate = runList.stream().map(e -> e.getAnalysisEndDate()).min(Comparator.naturalOrder()).orElse(null);
        String analysisDate = getDateZones(analysisBeginDate, analysisEndDate);
        dataMap.put("analysisDate", analysisDate);

        mapNullToString(dataMap, "");
        Word2007Utils.replaceAll(document, dataMap);

        // 为测试表写入数据
        List<List<Object>> testTableDataList = setTableDataForTest(reportResultList);
        listListNullToString(testTableDataList, "-");
        Word2007Utils.setDataToTable(document.getTables().get(1), testTableDataList);

        // 最后一张行转列的结果表
        createResultTableNoEvaluation(document, dataMap, 2, report, folderList, orderList, reportResultList);
    }

    // 为常规报告板式除去噪声word报告生成数据
    private void setDataByNormalReportWithoutNoise(XWPFDocument document, Map<String, Object> dataMap, List<EmcFolderBean> folderList, List<EmcOrderBean> orderList,
            List<EmcOrderTaskBean> orderTaskList, List<EmcRunBean> runList, List<EmcSampleBean> sampleList, List<EmcReportResultBean> reportResultList) {

        // 替换基础数据
        setBasicDataForEntrustReport(document, dataMap, folderList, orderTaskList, sampleList);

        LocalDate sampleBeginDate = orderList.stream().filter(e -> e.getSampleBeginDate() != null).map(e -> e.getSampleBeginDate()).min(Comparator.naturalOrder()).orElse(null);
        LocalDate sampleCompleteDate = orderList.stream().filter(e -> e.getSampleCompleteDate() != null).map(e -> e.getSampleCompleteDate()).max(Comparator.naturalOrder()).orElse(null);
        String sampleDate = getDateZones(sampleBeginDate, sampleCompleteDate);
        dataMap.put("sampleDate", sampleDate);

        List<Long> orderIdList = orderList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<String> testNameList = reportResultList.stream().filter(e -> orderIdList.contains(e.getOrderId())).map(e -> e.getTestName()).distinct().collect(Collectors.toList());
        String testNames = StringUtils.join(testNameList, "、");
        dataMap.put("testNames", testNames);
        dataMap.put("testNameCounts", testNameList.size());

        Long frequency = orderList.stream().filter(e -> e.getFrequency() != null).map(e -> e.getFrequency()).max(Comparator.naturalOrder()).orElse(null);
        dataMap.put("frequency", NumberFormatUtils.cvt(frequency));

        LocalDate analysisBeginDate = runList.stream().map(e -> e.getAnalysisBeginDate()).min(Comparator.naturalOrder()).orElse(null);
        LocalDate analysisEndDate = runList.stream().map(e -> e.getAnalysisEndDate()).min(Comparator.naturalOrder()).orElse(null);
        String analysisDate = getDateZones(analysisBeginDate, analysisEndDate);
        dataMap.put("analysisDate", analysisDate);

        mapNullToString(dataMap, "");
        Word2007Utils.replaceAll(document, dataMap);

        // 为测试表写入数据
        List<List<Object>> testTableDataList = setTableDataForTest(reportResultList);
        listListNullToString(testTableDataList, "-");
        Word2007Utils.setDataToTable(document.getTables().get(1), testTableDataList);

        // 查询采样单的属性模板，并找到所有采样单属性值
        EmcSampleBean sample = sampleList.get(0);
        List<Long> sampleIdList = sampleList.stream().map(e -> e.getId()).collect(Collectors.toList());
        Long propertyTmplId = NumberUtils.parseLong(sample.getExt$Item("tmplid"));
        List<EmcPropertyDataBean> propertyDataList = propertyDataService
                .selectListByFilter(SearchFilter.instance().match("tmplId", propertyTmplId).filter(MatchPattern.EQ).match("bizId", sampleIdList).filter(MatchPattern.OR));
        List<Long> sampleIdListForZhang = propertyDataList.stream().filter(e -> ObjectUtils.equals("涨", BeanUtils.toMap(e).get("ext20"))).map(e -> e.getBizId()).collect(Collectors.toList());
        List<Long> sampleIdListForTui = propertyDataList.stream().filter(e -> ObjectUtils.equals("退", BeanUtils.toMap(e).get("ext20"))).map(e -> e.getBizId()).collect(Collectors.toList());

        // 生成监测结果表,以及表标题
        List<String> tableTitleList = new ArrayList<>();

        // 统计数据表
        for (int i = 0; i < folderList.size(); i++) {

            EmcFolderBean folder = folderList.get(i);
            List<EmcReportResultBean> folderResultList = reportResultList.stream().filter(e -> ObjectUtils.equals(folder.getId(), e.getFolderId())).collect(Collectors.toList());
            List<String> folderItemNameList = folderResultList.stream().map(e -> e.getItemName()).distinct().collect(Collectors.toList());

            // 分成两行来展示数据
            int itemNameCounts = folderItemNameList.size();
            int tableColumnSizeForItemNames = 0;
            if (itemNameCounts % 2 == 0) {
                tableColumnSizeForItemNames = itemNameCounts / 2;
            } else {
                tableColumnSizeForItemNames = itemNameCounts / 2 + 1;
            }

            // 表名
            String tableTitle = "表4-2-" + (folderList.size() - i) + "：禅城区" + LocalDate.now().getMonthValue() + "月份佛山水道汾江河段水质监测统计数据表（见附表）";
            tableTitleList.add(0, tableTitle);
            List<List<Object>> resultTableDataList = new ArrayList<List<Object>>();

            // 给表格传入数据
            for (int j = 0; j < 2; j++) {
                // 每行前面的公用信息
                for (int k = 0; k < 8; k++) {
                    List<Object> resultRowDataList = new ArrayList<Object>();
                    if (k == 0) {
                        resultRowDataList.add("测点名称");
                        resultRowDataList.add("统计指标");
                    } else if (k == 1) {
                        for (int n = 0; n < 2; n++) {
                            resultRowDataList.add("");
                        }
                    } else if (k == 2) {
                        resultRowDataList.add(folder.getMonitorPointName());
                        resultRowDataList.add("样品数");
                    } else if (k == 3) {
                        resultRowDataList.add("");
                        resultRowDataList.add("最大值");
                    } else if (k == 4) {
                        resultRowDataList.add("");
                        resultRowDataList.add("最小值");
                    } else if (k == 5) {
                        resultRowDataList.add("");
                        resultRowDataList.add("平均值");
                    } else if (k == 6) {
                        resultRowDataList.add("");
                        resultRowDataList.add("超标率");
                    } else if (k == 7) {
                        resultRowDataList.add("");
                        resultRowDataList.add("评价标准");
                    }
                    resultTableDataList.add(resultRowDataList);
                }

                // 添加结果值
                for (int k = j * tableColumnSizeForItemNames; k < (j == 0 ? tableColumnSizeForItemNames : itemNameCounts); k++) {

                    String itemName = folderItemNameList.get(k);
                    List<Object> resultRowDataListForNull = resultTableDataList.get(8 * j + 1);
                    resultRowDataListForNull.add("");

                    List<EmcReportResultBean> resultList = folderResultList.stream().filter(e -> ObjectUtils.equals(itemName, e.getItemName())).collect(Collectors.toList());

                    // 样品数
                    List<Object> resultForOrderNumber = resultTableDataList.get(8 * j + 2);
                    resultForOrderNumber.add("4");

                    // 最大值
                    Double maxResult = resultList.stream().filter(e -> NumberUtils.isNumber(e.getReportResult())).map(e -> NumberUtils.parseDouble(e.getReportResult())).max(Comparator.naturalOrder())
                            .orElse(null);
                    List<Object> resultRowDataListForMaxResult = resultTableDataList.get(8 * j + 3);
                    resultRowDataListForMaxResult.add(ConvertUtils.convert(maxResult, String.class, "-"));

                    // 最小值
                    Double minResult = resultList.stream().filter(e -> NumberUtils.isNumber(e.getReportResult())).map(e -> NumberUtils.parseDouble(e.getReportResult())).min(Comparator.naturalOrder())
                            .orElse(null);
                    List<Object> resultRowDataListForMinResult = resultTableDataList.get(8 * j + 4);
                    resultRowDataListForMinResult.add(ConvertUtils.convert(minResult, String.class, "-"));

                    // 平均值

                    List<Object> resultRowDataListForAvgResult = resultTableDataList.get(8 * j + 5);
                    resultRowDataListForAvgResult.add(average(resultList));

                    // 达标率，报告名称，指标低限
                    long standardReachingCount = resultList.stream().filter(e -> ObjectUtils.equals("1", e.getExceedStandard())).count();
                    NumberFormat nf = NumberFormat.getPercentInstance();
                    nf.setMaximumFractionDigits(1);
                    String overStandardRate = nf.format((double) standardReachingCount / 4);
                    String reportItemName = itemName;
                    String indexLowLimit = resultList.get(0).getIndexLowLimit() == null ? "" : resultList.get(0).getIndexLowLimit();

                    if ("水温".equals(itemName) || "电导率".equals(itemName)) {
                        overStandardRate = "-";
                        indexLowLimit = "";
                    } else if ("pH值".equals(itemName)) {
                        reportItemName = itemName + "（无量纲）";
                        indexLowLimit = indexLowLimit + "--" + resultList.get(0).getIndexHighLimit() == null ? "" : resultList.get(0).getIndexHighLimit();
                    } else if ("粪大肠菌群".equals(itemName)) {
                        reportItemName = itemName + "(个/L)";
                    }
                    List<Object> resultRowDataListForItemName = resultTableDataList.get(8 * j);
                    List<Object> resultRowDataListForOverStandardRate = resultTableDataList.get(8 * j + 6);
                    List<Object> resultRowDataListForIndexLimit = resultTableDataList.get(8 * j + 7);
                    resultRowDataListForItemName.add(reportItemName);
                    resultRowDataListForOverStandardRate.add(overStandardRate);
                    resultRowDataListForIndexLimit.add(indexLowLimit);
                }
            }

            // 创建表
            listListNullToString(resultTableDataList, "-");

            Word2007Utils.createTable(document, "resultTables", tableTitle.replace("（见附表）", ""), null, resultTableDataList, 15200, 470, 9, "新宋体");

            // 合并行和列
            XWPFTable table = document.getTables().get(2);
            for (int k = 0; k < tableColumnSizeForItemNames + 2; k++) {
                Word2007Utils.mergeTableRows(table, k, 0, 1);
                Word2007Utils.mergeTableRows(table, k, 8, 9);
            }
            Word2007Utils.mergeTableRows(table, 0, 2, 7);
            Word2007Utils.mergeTableRows(table, 0, 10, 15);
        }

        // 原始数据表
        for (int i = 0; i < folderList.size(); i++) {

            EmcFolderBean folder = folderList.get(i);
            List<EmcReportResultBean> folderResultList = reportResultList.stream().filter(e -> ObjectUtils.equals(folder.getId(), e.getFolderId())).collect(Collectors.toList());
            List<String> folderItemNameList = folderResultList.stream().map(e -> e.getItemName()).distinct().collect(Collectors.toList());

            // 分成两行来展示数据
            int itemNameCounts = folderItemNameList.size();
            int tableColumnSizeForItemNames = 0;
            if (itemNameCounts % 2 == 0) {
                tableColumnSizeForItemNames = itemNameCounts / 2;
            } else {
                tableColumnSizeForItemNames = itemNameCounts / 2 + 1;
            }

            // 表名
            String tableTitle = "表4-1-" + (folderList.size() - i) + "：禅城区" + LocalDate.now().getMonthValue() + "月份佛山水道汾江河段水质监测原始数据表（见附表）";
            tableTitleList.add(0, tableTitle);
            List<List<Object>> resultTableDataList = new ArrayList<List<Object>>();

            // 表格第一行公用信息
            List<Object> resultRowForTableCommenDataList = new ArrayList<Object>();
            resultRowForTableCommenDataList.add("测点站名称：禅城区环境监测站");
            for (int j = 0; j < 4; j++) {
                resultRowForTableCommenDataList.add("");
            }
            resultRowForTableCommenDataList.add("单位：毫克/升(水温、pH值、电导率、粪大肠菌群除外)");
            for (int j = 0; j < tableColumnSizeForItemNames - 1; j++) {
                resultRowForTableCommenDataList.add("");
            }
            resultTableDataList.add(resultRowForTableCommenDataList);

            // 给表格传入数据
            for (int j = 0; j < 2; j++) {
                // 每行前面的公用信息
                for (int k = 0; k < 8; k++) {
                    List<Object> resultRowDataList = new ArrayList<Object>();
                    if (k == 0) {
                        resultRowDataList.add("测点名称");
                        resultRowDataList.add("月");
                        resultRowDataList.add("日");
                        resultRowDataList.add("涨退潮");
                        resultRowDataList.add("位置");
                    } else if (k == 1) {
                        for (int n = 0; n < 5; n++) {
                            resultRowDataList.add("");
                        }
                    } else if (k == 2) {
                        resultRowDataList.add(folder.getMonitorPointName());
                        resultRowDataList.add(sampleBeginDate == null ? "-" : sampleBeginDate.getMonthValue());
                        resultRowDataList.add(sampleBeginDate == null ? "-" : sampleBeginDate.getDayOfMonth());
                        resultRowDataList.add("退");
                        resultRowDataList.add("左");
                    } else if (k == 3) {
                        for (int n = 0; n < 4; n++) {
                            resultRowDataList.add("");
                        }
                        resultRowDataList.add("右");
                    } else if (k == 4) {
                        for (int n = 0; n < 3; n++) {
                            resultRowDataList.add("");
                        }
                        resultRowDataList.add("涨");
                        resultRowDataList.add("左");
                    } else if (k == 5) {
                        for (int n = 0; n < 4; n++) {
                            resultRowDataList.add("");
                        }
                        resultRowDataList.add("右");
                    } else if (k == 6) {
                        resultRowDataList.add("最低检出限");
                        for (int n = 0; n < 4; n++) {
                            resultRowDataList.add("");
                        }
                    } else if (k == 7) {
                        resultRowDataList.add("评价标准");
                        for (int n = 0; n < 3; n++) {
                            resultRowDataList.add("");
                        }
                        resultRowDataList.add("Ⅳ");
                    }
                    resultTableDataList.add(resultRowDataList);
                }

                // 添加结果值
                for (int k = j * tableColumnSizeForItemNames; k < (j == 0 ? tableColumnSizeForItemNames : itemNameCounts); k++) {

                    String itemName = folderItemNameList.get(k);
                    List<Object> resultRowDataListForNull = resultTableDataList.get(8 * j + 2);
                    resultRowDataListForNull.add("");

                    List<EmcReportResultBean> resultList = folderResultList.stream().filter(e -> ObjectUtils.equals(itemName, e.getItemName())).collect(Collectors.toList());

                    // 涨退潮（涨），位置（左）
                    EmcReportResultBean resultForZhangAndLeft = resultList.stream()
                            .filter(e -> ObjectUtils.equals("left", e.getExt$Item("horizontalcode")) && sampleIdListForZhang.contains(e.getSampleId())).findFirst().orElse(null);
                    List<Object> resultRowDataListForZhangAndLeft = resultTableDataList.get(8 * j + 3);
                    resultRowDataListForZhangAndLeft.add(resultForZhangAndLeft == null ? "-" : resultForZhangAndLeft.getReportResult());

                    // 涨，右
                    EmcReportResultBean resultForZhangAndRight = resultList.stream()
                            .filter(e -> ObjectUtils.equals("right", e.getExt$Item("horizontalcode")) && sampleIdListForZhang.contains(e.getSampleId())).findFirst().orElse(null);
                    List<Object> resultRowDataListForZhangAndRight = resultTableDataList.get(8 * j + 4);
                    resultRowDataListForZhangAndRight.add(resultForZhangAndRight == null ? "-" : resultForZhangAndRight.getReportResult());

                    // 退，左
                    EmcReportResultBean resultForTuiAndLeft = resultList.stream()
                            .filter(e -> ObjectUtils.equals("left", e.getExt$Item("horizontalcode")) && sampleIdListForTui.contains(e.getSampleId())).findFirst().orElse(null);
                    List<Object> resultRowDataListForTuiAndLeft = resultTableDataList.get(8 * j + 5);
                    resultRowDataListForTuiAndLeft.add(resultForTuiAndLeft == null ? "-" : resultForTuiAndLeft.getReportResult());

                    // 退，右
                    EmcReportResultBean resultForTuiAndRight = resultList.stream()
                            .filter(e -> ObjectUtils.equals("right", e.getExt$Item("horizontalcode")) && sampleIdListForTui.contains(e.getSampleId())).findFirst().orElse(null);
                    List<Object> resultRowDataListForTuiAndRight = resultTableDataList.get(8 * j + 6);
                    resultRowDataListForTuiAndRight.add(resultForTuiAndRight == null ? "-" : resultForTuiAndRight.getReportResult());

                    String reportItemName = itemName;

                    String lowLimit = resultList.get(0).getLowLimit() == null ? "-" : resultList.get(0).getLowLimit();
                    String indexLowLimit = resultList.get(0).getIndexLowLimit() == null ? "-" : resultList.get(0).getIndexLowLimit();

                    if ("水温".equals(itemName)) {
                        lowLimit = lowLimit + "℃";
                        indexLowLimit = "-";
                    } else if ("pH值".equals(itemName)) {
                        reportItemName = itemName + "（无量纲）";
                        lowLimit = lowLimit + "(pH)";
                        indexLowLimit = indexLowLimit + "--" + resultList.get(0).getIndexHighLimit() == null ? "-" : resultList.get(0).getIndexHighLimit();
                    } else if ("粪大肠菌群".equals(itemName)) {
                        reportItemName = itemName + "(个/L)";
                        lowLimit = lowLimit + "-";
                    } else if ("电导率".equals(itemName)) {
                        lowLimit = lowLimit + "（μs/cm）";
                        indexLowLimit = "-";
                    }
                    List<Object> resultRowDataListForItemName = resultTableDataList.get(8 * j + 1);
                    List<Object> resultRowDataListForLowLimit = resultTableDataList.get(8 * j + 7);
                    List<Object> resultRowDataListForIndexLimit = resultTableDataList.get(8 * j + 8);
                    resultRowDataListForItemName.add(reportItemName);
                    resultRowDataListForLowLimit.add(lowLimit);
                    resultRowDataListForIndexLimit.add(indexLowLimit);
                }
            }

            // 创建表
            listListNullToString(resultTableDataList, "-");

            Word2007Utils.createTable(document, "resultTables", tableTitle.replace("（见附表）", ""), null, resultTableDataList, 15200, 460, 9, "新宋体");

            // 合并行和列
            XWPFTable table = document.getTables().get(2);
            Word2007Utils.mergeTableColumns(table, 0, 0, 4);
            Word2007Utils.mergeTableColumns(table, 0, 5, tableColumnSizeForItemNames + 4);
            Word2007Utils.mergeTableColumns(table, 7, 0, 4);
            Word2007Utils.mergeTableColumns(table, 8, 0, 3);
            Word2007Utils.mergeTableColumns(table, 15, 0, 4);
            Word2007Utils.mergeTableColumns(table, 16, 0, 3);
            for (int k = 0; k < tableColumnSizeForItemNames + 5; k++) {
                Word2007Utils.mergeTableRows(table, k, 1, 2);
                Word2007Utils.mergeTableRows(table, k, 9, 10);
            }
            for (int k = 0; k < 3; k++) {
                Word2007Utils.mergeTableRows(table, k, 3, 6);
                Word2007Utils.mergeTableRows(table, k, 11, 14);
            }
            Word2007Utils.mergeTableRows(table, 3, 3, 4);
            Word2007Utils.mergeTableRows(table, 3, 5, 6);
            Word2007Utils.mergeTableRows(table, 3, 11, 12);
            Word2007Utils.mergeTableRows(table, 3, 13, 14);
        }
        // 添加表名信息
        Word2007Utils.addText(document, "resultTableNames", tableTitleList);
        // 删除标记
        Word2007Utils.removeKey(document, "resultTables");
    }

    // 为常规报告板式噪声 Word 报告生成数据
    private void setDataByNormalReportNoise(XWPFDocument document, Map<String, Object> dataMap, List<EmcFolderBean> folderList, List<EmcOrderBean> orderList, List<EmcOrderTaskBean> orderTaskList,
            List<EmcOrderTaskResultBean> orderTaskResultList, List<EmcRunBean> runList, List<EmcSampleBean> sampleList, List<EmcReportResultBean> reportResultList) {

        LocalDate nowDate = LocalDate.now();
        int year = nowDate.getYear();
        int month = nowDate.getMonthValue();
        int quarter = (month / 3) + ((month % 3 == 0) ? 0 : 1);
        String upperQuarter = NumberFormatUtils.cvt(quarter);
        dataMap.put("year", year);
        dataMap.put("quarter", upperQuarter);
        mapNullToString(dataMap, "");
        Word2007Utils.replaceAll(document, dataMap);

        // 向监测项目表格插入数据
        List<List<Object>> monitorItemTableDataList = new ArrayList<>();
        List<Long> methodVersionIds = orderTaskList.stream().map(EmcOrderTaskBean::getMethodVersionId).collect(Collectors.toList());
        List<EmcMethodVersionBean> methodVersionBeans = methodVersionService.getDao().selectListByIds(methodVersionIds);
        List<Long> equiptIds = orderTaskList.stream().map(EmcOrderTaskBean::getEquiptId).collect(Collectors.toList());
        List<EmcEquiptBean> equiptBeans = equiptService.getDao().selectListByIds(equiptIds);

        List<EmcOrderTaskBean> distinctOrderTaskList = orderTaskList.stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmcOrderTaskBean::getTestName))), ArrayList::new));
        distinctOrderTaskList.forEach(o -> {
            List<Object> rowDataList = new ArrayList<>();
            rowDataList.add(o.getOrderCategory());
            rowDataList.add(o.getTestName());
            EmcMethodVersionBean versionBean = methodVersionBeans.stream().filter(v -> ObjectUtils.equals(v.getId(), o.getMethodVersionId())).findFirst().get();
            rowDataList.add(versionBean.getTestMethodBasis());
            EmcEquiptBean equiptBean = equiptBeans.stream().filter(e -> ObjectUtils.equals(e.getId(), o.getEquiptId())).findFirst().get();
            rowDataList.add(equiptBean.getEquiptName() == null ? "" : equiptBean.getEquiptName() + ((equiptBean.getEquiptId() == null ? "" : "（编号：" + equiptBean.getEquiptId() + "）")));
            Optional<EmcOrderTaskResultBean> taskResultOptional = orderTaskResultList.stream().filter(r -> r.getIndexLowLimit() != null).findFirst();
            if (taskResultOptional.isPresent()) {
                EmcOrderTaskResultBean taskResultBean = taskResultOptional.get();
                String indexLowLimit = taskResultBean.getIndexLowLimit();
                rowDataList.add(indexLowLimit == null ? "" : indexLowLimit + (taskResultBean.getUnitName() == null ? "" : taskResultBean.getUnitName()));
            } else {
                rowDataList.add("");
            }
            monitorItemTableDataList.add(rowDataList);
        });
        Word2007Utils.setDataToTable(document.getTables().get(1), monitorItemTableDataList);

        // 向监测结果表格插入数据
        List<List<Object>> monitorResultTableDataList = new ArrayList<>();
        Map<Long, List<Object>> extraSamplePropertyMap = new HashMap<>();
        sampleList.stream().forEach(s -> {
            Long propertyTmplId = NumberUtils.parseLong(s.getExt$Item("tmplid"));
            List<EmcPropertyDataBean> propertyDataList = propertyDataService
                    .selectListByFilter(SearchFilter.instance().match("tmplId", propertyTmplId).filter(MatchPattern.EQ).match("bizId", s.getId()).filter(MatchPattern.EQ));
            Map<String, Object> propertyDataMap = BeanUtils.toMap(propertyDataList.get(0));

            List<Object> rowDataList = new ArrayList<>();
            EmcOrderBean minSampleDateOrder = orderList.stream().filter(r -> ObjectUtils.equals(s.getId(), r.getSampleId())).min(Comparator.comparing(EmcOrderBean::getSampleBeginDate)).get();
            rowDataList.add(minSampleDateOrder.getSampleBeginDate());
            rowDataList.add(propertyDataMap.get("ext1") == null ? "" : propertyDataMap.get("ext1"));
            rowDataList.add(propertyDataMap.get("ext5") == null ? "" : propertyDataMap.get("ext5"));
            rowDataList.add(propertyDataMap.get("ext2") == null ? "" : propertyDataMap.get("ext2"));
            EmcFolderBean folderBean = folderList.stream().filter(r -> ObjectUtils.equals(r.getId(), minSampleDateOrder.getFolderId())).findFirst().get();
            rowDataList.add(folderBean.getSampleUserName());
            monitorResultTableDataList.add(rowDataList);

            List<Object> propertyValues = new ArrayList<>();
            propertyValues.add(propertyDataMap.get("ext10"));
            propertyValues.add(propertyDataMap.get("ext11"));
            propertyValues.add(propertyDataMap.get("ext12"));
            propertyValues.add(propertyDataMap.get("ext13"));
            propertyValues.add(propertyDataMap.get("ext14"));
            extraSamplePropertyMap.put(s.getId(), propertyValues);
        });
        Word2007Utils.setDataToTable(document.getTables().get(2), monitorResultTableDataList);

        // 动态生成监测数据表格
        // 根据点位获取监测点的扩展属性值
        String defaultEmptyContent = "-";
        Map<Long, List<Object>> extraMonitorPropertyMap = new HashMap<>();
        List<String> monitorPointCategoryIds = folderList.stream().filter(f -> !StringUtils.isEmpty(f.getMonitorPointCategoryId())).map(EmcFolderBean::getMonitorPointCategoryId)
                .collect(Collectors.toList());
        List<EmcMonitorPointCategoryBean> pointCategoryBeans = monitorPointCategoryService.getDao().selectListByIds(monitorPointCategoryIds);
        List<String> monitorPointNos = folderList.stream().map(EmcFolderBean::getMonitorPointNo).collect(Collectors.toList());
        List<EmcMonitorPointBean> monitorPointBeans = monitorPointService.getDao().selectListByOneColumnValues(monitorPointNos, "MONITORPOINTNO");
        folderList.stream().forEach(f -> {
            EmcMonitorPointCategoryBean pointCategoryBean = pointCategoryBeans.stream().filter(c -> ObjectUtils.equals(c.getId(), f.getMonitorPointCategoryId())).findFirst().get();
            Long propertyTmplId = NumberUtils.parseLong(pointCategoryBean.getMonitorPointType());
            EmcMonitorPointBean pointBean = monitorPointBeans.stream().filter(p -> ObjectUtils.equals(p.getMonitorPointNo(), f.getMonitorPointNo())).findFirst().get();
            List<EmcPropertyDataBean> propertyDataList = propertyDataService
                    .selectListByFilter(SearchFilter.instance().match("tmplId", propertyTmplId).filter(MatchPattern.EQ).match("bizId", pointBean.getId()).filter(MatchPattern.EQ));

            List<Object> propertyValues = new ArrayList<>();
            propertyValues.add(pointBean.getControllerLever()); // 控制级别
            if (!propertyDataList.isEmpty()) {
                Map<String, Object> propertyDataMap = BeanUtils.toMap(propertyDataList.get(0));
                propertyValues.add(propertyDataMap.get("ext1"));
                propertyValues.add(propertyDataMap.get("ext2"));
                propertyValues.add(propertyDataMap.get("ext3"));
                propertyValues.add(propertyDataMap.get("ext4"));
                propertyValues.add(propertyDataMap.get("ext5"));
            }
            extraMonitorPropertyMap.put(f.getId(), propertyValues);
        });

        // 整个监测数据集合
        String[] itemNames1 = { "Leq", "L10", "L50", "L90", "最大值", "最小值", "标准差(SD)" };
        String[] itemNames2 = { "Ld", "Ln", "Ldn", "车流量" };
        Map<Integer, List<List<Object>>> monitorDataMap = new HashMap<>();
        for (int i = 0; i < folderList.size(); i++) {
            EmcFolderBean folderBean = folderList.get(i);
            List<List<Object>> tableDataList = new ArrayList<>();
            List<EmcOrderBean> currentOrderList = orderList.stream().filter(r -> ObjectUtils.equals(r.getFolderId(), folderBean.getId())).collect(Collectors.toList());
            // 遍历样品
            List<Object> extraMonitorPropertyValues = extraMonitorPropertyMap.get(folderBean.getId());
            currentOrderList.stream().forEach(o -> {
                // 每个样品一行数据
                List<Object> rowDataList = new ArrayList<>();
                rowDataList.add(extraMonitorPropertyValues.isEmpty() ? defaultEmptyContent : extraMonitorPropertyValues.get(1));
                rowDataList.add(year);
                rowDataList.add(folderBean.getFolderNo());
                rowDataList.add(folderBean.getMonitorPointName());
                rowDataList.add(folderBean.getLatitude() == null ? defaultEmptyContent : folderBean.getLatitude());
                rowDataList.add(folderBean.getLongitude() == null ? defaultEmptyContent : folderBean.getLongitude());
                rowDataList.add(extraMonitorPropertyValues.isEmpty() ? defaultEmptyContent : extraMonitorPropertyValues.get(3));
                rowDataList.add(extraMonitorPropertyValues.isEmpty() ? defaultEmptyContent : extraMonitorPropertyValues.get(4));
                rowDataList.add(extraMonitorPropertyValues.isEmpty() ? defaultEmptyContent : extraMonitorPropertyValues.get(2));
                LocalDate sampleBeginDate = o.getSampleBeginDate();
                String sampleBeginTime = o.getSampleBeginTime();
                rowDataList.add(sampleBeginDate == null ? defaultEmptyContent : sampleBeginDate.getMonthValue());
                rowDataList.add(sampleBeginDate == null ? defaultEmptyContent : sampleBeginDate.getDayOfMonth());
                if (!StringUtils.isEmpty(sampleBeginTime)) {
                    String[] hourMinutes = sampleBeginTime.split(":");
                    if (hourMinutes.length > 1) {
                        rowDataList.add(hourMinutes[0]);
                        rowDataList.add(hourMinutes[1]);
                    } else {
                        rowDataList.add(defaultEmptyContent);
                        rowDataList.add(defaultEmptyContent);
                    }
                } else {
                    rowDataList.add(defaultEmptyContent);
                    rowDataList.add(defaultEmptyContent);
                }

                // 获取样品下的所有样品结果
                List<EmcOrderTaskResultBean> currentOrderResultList = orderTaskResultList.stream().filter(m -> ObjectUtils.equals(m.getOrderId(), o.getId())).collect(Collectors.toList());
                Arrays.asList(itemNames1).stream().forEach(s -> {
                    Optional<EmcOrderTaskResultBean> orderTaskResultOptional = currentOrderResultList.stream().filter(r -> s.equals(r.getItemName())).findFirst();
                    if (orderTaskResultOptional.isPresent()) {
                        EmcOrderTaskResultBean orderTaskResult = orderTaskResultOptional.get();
                        String reportResult = orderTaskResult.getReportResult();
                        rowDataList.add(StringUtils.isEmpty(reportResult) ? defaultEmptyContent : reportResult);
                    } else {
                        rowDataList.add(defaultEmptyContent);
                    }
                });

                rowDataList.add("禅城区环境监测站");
                EmcOrderTaskBean orderTaskBean = orderTaskList.stream().filter(t -> ObjectUtils.equals(t.getOrderId(), o.getId())).findFirst().get();
                EmcEquiptBean equiptBean = equiptBeans.stream().filter(e -> ObjectUtils.equals(e.getId(), orderTaskBean.getEquiptId())).findFirst().get();
                rowDataList.add(equiptBean.getSpecification());
                rowDataList.add(equiptBean.getEquiptId());

                List<Object> extraSamplePropertyValues = extraSamplePropertyMap.get(o.getSampleId());
                rowDataList.addAll(extraSamplePropertyValues);
                Arrays.asList(itemNames2).stream().forEach(s -> {
                    Optional<EmcOrderTaskResultBean> orderTaskResultOptional = currentOrderResultList.stream().filter(r -> s.equals(r.getItemName())).findFirst();
                    if (orderTaskResultOptional.isPresent()) {
                        EmcOrderTaskResultBean orderTaskResult = orderTaskResultOptional.get();
                        String reportResult = orderTaskResult.getReportResult();
                        rowDataList.add(StringUtils.isEmpty(reportResult) ? defaultEmptyContent : reportResult);
                    } else {
                        rowDataList.add(defaultEmptyContent);
                    }
                });
                Object controllerLevel = extraMonitorPropertyValues.get(0);
                if ("province".equals(controllerLevel)) {
                    controllerLevel = "省控";
                } else if ("city".equals(controllerLevel)) {
                    controllerLevel = "市控";
                }
                rowDataList.add(controllerLevel);
                tableDataList.add(rowDataList);
            });
            monitorDataMap.put(i, tableDataList);
        }

        // 创建表格
        String defaultTableTitle = "表3-2 %d年第%s季度 禅城区城市功能区环境声环境监测数据%s";
        listListNullToString(monitorDataMap.get(0), "-");
        Word2007Utils.setDataToTable(document.getTables().get(3), monitorDataMap.get(0));
        for (int i = monitorDataMap.size() - 1; i >= 1; i--) {
            String tableTitle = String.format(defaultTableTitle, year, upperQuarter, "（续" + i + "）");
            listListNullToString(monitorDataMap.get(i), defaultEmptyContent);
            Word2007Utils.copyOneAterSourceTable(document, 3, tableTitle);
            Word2007Utils.setDataToTable(document.getTables().get(4), monitorDataMap.get(i));
        }
        // 删除标记
        Word2007Utils.removeKey(document, "resultTables");
    }

    // 委托报告无评价word报告数据生成： 1.点位表 数据（一个点位一行） 2.测试表数据 （一个分析项目一行）
    // 3.结果表数据，按行转列格式展示（理想状况下一个监测点编号一张表，但是一旦列数超过10，每10列新建一个表）
    private void setDataByEntrustReportNoEvaluation(XWPFDocument document, Map<String, Object> dataMap, EmcReportBean report, List<EmcFolderBean> folderList, List<EmcOrderBean> orderList,
            List<EmcOrderTaskBean> orderTaskList, List<EmcRunBean> runList, List<EmcSampleBean> sampleList, List<EmcReportResultBean> reportResultList) {

        // 替换基础数据
        setBasicDataForEntrustReport(document, dataMap, folderList, orderTaskList, sampleList);
        mapNullToString(dataMap, "");
        Word2007Utils.replaceAll(document, dataMap);

        // 为点位表写入数据
        List<List<Object>> folderTableDataList = setTableDataForFolder(folderList, orderList, orderTaskList, runList, dataMap.get("sampleWay") == null ? "" : dataMap.get("sampleWay").toString(),
                reportResultList);
        listListNullToString(folderTableDataList, "-");
        Word2007Utils.setDataToTable(document.getTables().get(1), folderTableDataList);

        // 为测试表写入数据
        List<List<Object>> testTableDataList = setTableDataForTest(reportResultList);
        listListNullToString(testTableDataList, "-");
        Word2007Utils.setDataToTable(document.getTables().get(2), testTableDataList);

        // 最后一张行转列的结果表
        createResultTableNoEvaluation(document, dataMap, 3, report, folderList, orderList, reportResultList);
    }

    // 委托报告带评价word报告数据生成: 1.点位表 数据（一个点位一行） 2.测试表数据 （一个分析项目一行）
    // 3.结果表数据，按行转列格式展示（理想状况下一个监测点编号一张表，但是一旦列数超过10，每10列新建一个表）
    private void setDataByEntrustReportWithEvaluation(XWPFDocument document, Map<String, Object> dataMap, EmcReportBean report, List<EmcFolderBean> folderList, List<EmcOrderBean> orderList,
            List<EmcOrderTaskBean> orderTaskList, List<EmcRunBean> runList, List<EmcSampleBean> sampleList, List<EmcReportResultBean> reportResultList) {

        // 替换基础数据
        setBasicDataForEntrustReport(document, dataMap, folderList, orderTaskList, sampleList);
        mapNullToString(dataMap, "");
        Word2007Utils.replaceAll(document, dataMap);

        // 为点位表写入数据
        List<List<Object>> folderTableDataList = setTableDataForFolder(folderList, orderList, orderTaskList, runList, dataMap.get("sampleWay") == null ? "" : dataMap.get("sampleWay").toString(),
                reportResultList);
        listListNullToString(folderTableDataList, "-");
        Word2007Utils.setDataToTable(document.getTables().get(1), folderTableDataList);

        // 为测试表写入数据
        List<List<Object>> testTableDataList = setTableDataForTest(reportResultList);
        listListNullToString(testTableDataList, "-");
        Word2007Utils.setDataToTable(document.getTables().get(2), testTableDataList);

        // 最后一张行转列的结果表
        List<String> evaluationList = createResultTableWithEvaluation(document, dataMap, report, folderList, orderList, reportResultList);
        Word2007Utils.removeKey(document, "resultTableInfo");// 移除关键字
        Word2007Utils.addText(document, "appraises", evaluationList); // 添加评价语
    }

    // 创建无评价的结果表
    private void createResultTableNoEvaluation(XWPFDocument document, Map<String, Object> dataMap, int tablePosition, EmcReportBean report, List<EmcFolderBean> folderList,
            List<EmcOrderBean> orderList, List<EmcReportResultBean> reportResultList) {

        String monitorTmplGroup = report.getExt$Item("monitortmplgroup");

        // 噪声的特殊处理
        if ("噪声和振动".equals(monitorTmplGroup)) {

            // 社会生活环境噪声监测结果表
            // 根据分析项名称为昼间/夜间的值，来分组，从而分表
            List<Long> orderIdForDayList = reportResultList.stream().filter(e -> ObjectUtils.equals("昼间/夜间", e.getItemName()) && ObjectUtils.equals("昼间", e.getReportResult())).map(e -> e.getOrderId())
                    .distinct().collect(Collectors.toList());
            List<Long> orderIdForNightList = reportResultList.stream().filter(e -> ObjectUtils.equals("昼间/夜间", e.getItemName()) && ObjectUtils.equals("夜间", e.getReportResult()))
                    .map(e -> e.getOrderId()).distinct().collect(Collectors.toList());

            List<String> evaluationList = new ArrayList<>();// 总评价信息
            setDataForNoiseTable(document, dataMap, reportResultList, evaluationList, orderIdForNightList, "夜");
            setDataForNoiseTable(document, dataMap, reportResultList, evaluationList, orderIdForDayList, "昼");

        } else {

            // 定义变量记录创建表的个数+1
            int tableCount = 1;

            for (int i = 0; i < folderList.size(); i++) {
                Long folderId = folderList.get(i).getId();
                List<EmcReportResultBean> resultByFolderList = reportResultList.stream().filter(e -> ObjectUtils.equals(folderId, e.getFolderId())).collect(Collectors.toList());

                List<String> itemNameList = resultByFolderList.stream().map(e -> e.getItemName()).distinct().collect(Collectors.toList());

                // 该点位下的分析项，需要在几张表下进行展示(一张表只展示8个分析项)
                int itemNameCount = itemNameList.size();
                int tableCountForFolder = itemNameCount % 8 == 0 ? itemNameCount / 8 : itemNameCount / 8 + 1;
                tableCount += tableCountForFolder;
            }

            // 先按点位表分
            for (int i = 0; i < folderList.size(); i++) {
                Long folderId = folderList.get(i).getId();
                String monitorPointName = folderList.get(i).getMonitorPointName();
                List<EmcReportResultBean> resultByFolderList = reportResultList.stream().filter(e -> ObjectUtils.equals(folderId, e.getFolderId())).collect(Collectors.toList());

                List<String> itemNameList = resultByFolderList.stream().map(e -> e.getItemName()).distinct().collect(Collectors.toList());
                Collections.sort(itemNameList);

                // 该点位下的分析项，需要在几张表下进行展示(一张表只展示8个分析项)
                int itemNameCount = itemNameList.size();
                int tableCountForFolder = itemNameCount % 8 == 0 ? itemNameCount / 8 : itemNameCount / 8 + 1;

                for (int j = 0; j < tableCountForFolder; j++) {// 再按分析项名称分
                    // 表格数据容器
                    List<List<Object>> resultTableDataList = new ArrayList<List<Object>>();
                    tableCount--;

                    List<String> itemNameForTableList = new ArrayList<>();
                    for (int k = j * 8; k < ((j == tableCountForFolder - 1) ? itemNameCount : j * 8 + 8); k++) {
                        itemNameForTableList.add(itemNameList.get(k));
                    }

                    // 表格中表头数据
                    List<Object> resultHeadRowDataList = new ArrayList<Object>();

                    resultHeadRowDataList.add("样品编号");
                    resultHeadRowDataList.add("采样位置");
                    itemNameForTableList.forEach(e -> {
                        resultHeadRowDataList.add(e);
                    });

                    resultTableDataList.add(resultHeadRowDataList);

                    // 给结果表中写入报出结果数据，并生成相应备注信息
                    StringBuilder remark = new StringBuilder();
                    int dataCount = setReportDataForResultTable(orderList, folderId, monitorPointName, resultByFolderList, resultTableDataList, remark, itemNameForTableList);

                    // 结果均值（取所有样品均值）
                    List<Object> resultRowDataForAverageList = new ArrayList<Object>();

                    resultRowDataForAverageList.add("均值");
                    resultRowDataForAverageList.add("");

                    itemNameForTableList.forEach(n -> {

                        List<EmcReportResultBean> resultByItemNameList = resultByFolderList.stream().filter(r -> ObjectUtils.equals(n, r.getItemName())).collect(Collectors.toList());

                        resultRowDataForAverageList.add(average(resultByItemNameList));
                    });

                    resultTableDataList.add(resultRowDataForAverageList);

                    // 添加备注信息到表格
                    List<Object> resultRowDataForRemarkList = new ArrayList<Object>();
                    resultRowDataForRemarkList.add("备注");
                    resultRowDataForRemarkList.add("");
                    resultRowDataForRemarkList.add(remark);
                    for (int k = 0; k < itemNameForTableList.size() - 1; k++) {
                        resultRowDataForRemarkList.add("");
                    }
                    resultTableDataList.add(resultRowDataForRemarkList);

                    listListNullToString(resultTableDataList, "-");

                    Word2007Utils.createTable(document, "resultTableInfo", "表4-" + tableCount + "  监测结果表", "单位：mg/L", resultTableDataList, null, null, null, null);

                    // 将表格中的备注信息格式调整（合并列）
                    XWPFTable table = document.getTables().get(tablePosition);
                    Word2007Utils.mergeTableColumns(table, dataCount + 1, 0, 1);
                    Word2007Utils.mergeTableColumns(table, dataCount + 2, 0, 1);
                    Word2007Utils.mergeTableColumns(table, dataCount + 2, 2, itemNameForTableList.size() + 1);
                }
            }
        }

        Word2007Utils.removeKey(document, "resultTableInfo");
    }

    // 创建带评价的结果表
    private List<String> createResultTableWithEvaluation(XWPFDocument document, Map<String, Object> dataMap, EmcReportBean report, List<EmcFolderBean> folderList, List<EmcOrderBean> orderList,
            List<EmcReportResultBean> reportResultList) {

        String monitorTmplGroup = report.getExt$Item("monitortmplgroup");
        List<String> evaluationList = new ArrayList<>();// 总评价信息

        // 噪声的特殊处理
        if ("噪声和振动".equals(monitorTmplGroup)) {

            // 社会生活环境噪声监测结果表
            // 根据分析项名称为昼间/夜间的值，来分组，从而分表
            List<Long> orderIdForDayList = reportResultList.stream().filter(e -> ObjectUtils.equals("昼间/夜间", e.getItemName()) && ObjectUtils.equals("昼间", e.getReportResult())).map(e -> e.getOrderId())
                    .distinct().collect(Collectors.toList());
            List<Long> orderIdForNightList = reportResultList.stream().filter(e -> ObjectUtils.equals("昼间/夜间", e.getItemName()) && ObjectUtils.equals("夜间", e.getReportResult()))
                    .map(e -> e.getOrderId()).distinct().collect(Collectors.toList());

            setDataForNoiseTable(document, dataMap, reportResultList, evaluationList, orderIdForNightList, "夜");
            setDataForNoiseTable(document, dataMap, reportResultList, evaluationList, orderIdForDayList, "昼");

        } else {

            // 定义变量记录创建表的个数+1
            int tableCount = 1;

            for (int i = 0; i < folderList.size(); i++) {
                Long folderId = folderList.get(i).getId();
                List<EmcReportResultBean> resultByFolderList = reportResultList.stream().filter(e -> ObjectUtils.equals(folderId, e.getFolderId())).collect(Collectors.toList());

                List<String> itemNameList = resultByFolderList.stream().map(e -> e.getItemName()).distinct().collect(Collectors.toList());

                // 该点位下的分析项，需要在几张表下进行展示(一张表只展示8个分析项)
                int itemNameCount = itemNameList.size();
                int tableCountForFolder = itemNameCount % 8 == 0 ? itemNameCount / 8 : itemNameCount / 8 + 1;
                tableCount += tableCountForFolder;
            }

            // 先按点位表分
            for (int i = 0; i < folderList.size(); i++) {
                Long folderId = folderList.get(i).getId();
                String monitorPointName = folderList.get(i).getMonitorPointName();
                List<EmcReportResultBean> resultByFolderList = reportResultList.stream().filter(e -> ObjectUtils.equals(folderId, e.getFolderId())).collect(Collectors.toList());
                List<String> itemNameList = resultByFolderList.stream().map(e -> e.getItemName()).distinct().collect(Collectors.toList());
                Collections.sort(itemNameList);

                // 该点位下的分析项，需要在几张表下进行展示(一张表只展示8个分析项)
                int itemNameCount = itemNameList.size();
                int tableCountForFolder = itemNameCount % 8 == 0 ? itemNameCount / 8 : itemNameCount / 8 + 1;

                // 达标与未达标统计
                List<String> upToStandItemList = new ArrayList<String>();
                List<String> notUpToStandItemList = new ArrayList<String>();

                for (int j = 0; j < tableCountForFolder; j++) {// 再按分析项名称分

                    List<String> itemNameForTableList = new ArrayList<>();
                    for (int k = j * 8; k < ((j == tableCountForFolder - 1) ? itemNameCount : j * 8 + 8); k++) {
                        itemNameForTableList.add(itemNameList.get(k));
                    }

                    // 表格数据容器
                    List<List<Object>> resultTableDataList = new ArrayList<List<Object>>();
                    tableCount--;

                    // 表格中表头数据
                    List<Object> resultHeadRowDataList = new ArrayList<Object>();

                    resultHeadRowDataList.add("样品编号");
                    resultHeadRowDataList.add("采样位置");
                    itemNameForTableList.forEach(e -> {
                        resultHeadRowDataList.add(e);
                    });

                    resultTableDataList.add(resultHeadRowDataList);

                    // 给结果表中写入报出结果数据，并生成相应备注信息
                    StringBuilder remark = new StringBuilder();
                    int dataCount = setReportDataForResultTable(orderList, folderId, monitorPointName, resultByFolderList, resultTableDataList, remark, itemNameForTableList);

                    // 结果均值（取所有样品均值）
                    List<Object> resultRowDataForAverageList = new ArrayList<Object>();
                    // 标准限值
                    List<Object> resultRowForStandLimitDataList = new ArrayList<Object>();
                    // 达标判定
                    List<Object> resultRowForStandJudgeDataList = new ArrayList<Object>();

                    resultRowDataForAverageList.add("均值");
                    resultRowDataForAverageList.add("");
                    resultRowForStandLimitDataList.add("标准限值");
                    resultRowForStandLimitDataList.add("");
                    resultRowForStandJudgeDataList.add("达标判定");
                    resultRowForStandJudgeDataList.add("");

                    itemNameForTableList.forEach(itemName -> {

                        List<EmcReportResultBean> resultByItemNameList = resultByFolderList.stream().filter(r -> ObjectUtils.equals(itemName, r.getItemName())).collect(Collectors.toList());
                        String averageResult = average(resultByItemNameList);
                        resultRowDataForAverageList.add(averageResult);

                        EmcReportResultBean resultByItemName = resultByItemNameList.get(0);

                        // 检出限
                        resultRowForStandLimitDataList.add(resultByItemName.getLowLimit());

                        // 达标判定
                        if ("1".equals(resultByItemName.getExceedStandard())) {
                            resultRowForStandJudgeDataList.add("未达标");
                            notUpToStandItemList.add(itemName);
                        } else {
                            resultRowForStandJudgeDataList.add("达标");
                            upToStandItemList.add(itemName);
                        }
                    });
                    resultTableDataList.add(resultRowDataForAverageList);
                    resultTableDataList.add(resultRowForStandLimitDataList);
                    resultTableDataList.add(resultRowForStandJudgeDataList);

                    // 执行标准
                    List<Object> resultRowForStandDataList = new ArrayList<Object>();
                    resultRowForStandDataList.add("执行标准");
                    resultRowForStandDataList.add("");
                    List<String> standList = resultByFolderList.stream().map(e -> e.getExt$Item("indexname")).distinct().collect(Collectors.toList());
                    String stands = StringUtils.join(standList, "、");
                    resultRowForStandDataList.add(stands);

                    // 备注信息
                    List<Object> resultRowDataForRemarkList = new ArrayList<Object>();
                    resultRowDataForRemarkList.add("备注");
                    resultRowDataForRemarkList.add("");
                    resultRowDataForRemarkList.add(remark);

                    // 补齐表格
                    for (int k = 0; k < itemNameForTableList.size() - 1; k++) {
                        resultRowForStandDataList.add("");
                        resultRowDataForRemarkList.add("");
                    }
                    resultTableDataList.add(resultRowForStandDataList);
                    resultTableDataList.add(resultRowDataForRemarkList);

                    listListNullToString(resultTableDataList, "-");

                    Word2007Utils.createTable(document, "resultTableInfo", "表4-" + tableCount + " 监测结果表", "单位：mg/L", resultTableDataList, null, null, null, null);

                    // 将表格中的部分信息格式调整（合并列）
                    XWPFTable table = document.getTables().get(3);
                    Word2007Utils.mergeTableColumns(table, dataCount + 1, 0, 1);
                    Word2007Utils.mergeTableColumns(table, dataCount + 2, 0, 1);
                    Word2007Utils.mergeTableColumns(table, dataCount + 3, 0, 1);
                    Word2007Utils.mergeTableColumns(table, dataCount + 4, 0, 1);
                    Word2007Utils.mergeTableColumns(table, dataCount + 4, 2, itemNameForTableList.size() + 1);
                    Word2007Utils.mergeTableColumns(table, dataCount + 5, 0, 1);
                    Word2007Utils.mergeTableColumns(table, dataCount + 5, 2, itemNameForTableList.size() + 1);
                }

                // 评价语
                List<String> orderNoList = resultByFolderList.stream().map(e -> e.getOrderNo()).distinct().collect(Collectors.toList());
                String orderNos = StringUtils.join(orderNoList, "、");
                StringBuilder evaluation = new StringBuilder();
                evaluation.append("（").append(i + 1).append("）从监测结果看，该企业").append(monitorPointName == null ? "" : monitorPointName).append("(编号：").append(orderNos).append("）排放的污染物：")
                        .append(StringUtils.join(upToStandItemList, "、")).append("均达标，").append(StringUtils.join(notUpToStandItemList, "、")).append("均未达标。");
                evaluationList.add(evaluation.toString());

            }
        }
        return evaluationList;
    }

    // 为委托报告传入基础替换数据
    private void setBasicDataForEntrustReport(XWPFDocument document, Map<String, Object> dataMap, List<EmcFolderBean> folderList, List<EmcOrderTaskBean> orderTaskList,
            List<EmcSampleBean> sampleList) {
        List<String> sampleUserNameList = folderList.stream().map(e -> e.getSampleUserName()).distinct().collect(Collectors.toList());
        String sampleUserNames = StringUtils.join(sampleUserNameList, "、");
        dataMap.put("sampleUserNames", sampleUserNames);

        List<String> testMethodBasisList = orderTaskList.stream().map(e -> e.getTestMethodBasis()).distinct().collect(Collectors.toList());
        String testMethodBases = StringUtils.join(testMethodBasisList, "、");
        dataMap.put("testMethodBases", testMethodBases);

        // 采样方式
        String sampleWay = "";
        // 取第一个采样单的天气状况和采样方式
        if (!sampleList.isEmpty()) {

            EmcSampleBean sample = sampleList.get(0);
            Long propertyTmplId = NumberUtils.parseLong(sample.getExt$Item("tmplid"));
            List<EmcPropertyFieldBean> propertyFieldList = propertyFieldService.selectListByFilter(SearchFilter.instance().match("tmplId", propertyTmplId).filter(MatchPattern.EQ));

            List<EmcPropertyDataBean> propertyDataList = propertyDataService
                    .selectListByFilter(SearchFilter.instance().match("tmplId", propertyTmplId).filter(MatchPattern.EQ).match("bizId", sample.getId()).filter(MatchPattern.EQ));
            if (!propertyDataList.isEmpty()) {
                Map<String, Object> propertyDataMap = BeanUtils.toMap(propertyDataList.get(0));
                dataMap.put("ext1", propertyDataMap.get("ext1") == null ? "" : propertyDataMap.get("ext1") + ";");// 天气状况
                dataMap.put("ext2", propertyDataMap.get("ext2") == null ? "" : propertyDataMap.get("ext2") + "摄氏度;");// 气温
                dataMap.put("ext3", propertyDataMap.get("ext3") == null ? "" : propertyDataMap.get("ext3") + "kPa;");// 气压
                dataMap.put("ext4", propertyDataMap.get("ext4") == null ? "" : propertyDataMap.get("ext4") + ";");// 风向
                dataMap.put("ext5", propertyDataMap.get("ext5") == null ? "" : propertyDataMap.get("ext5") + "m/s;");// 风速

                // 样品类型为废水的采样单
                EmcSampleBean wasteWaterSample = sampleList.stream().filter(e -> ObjectUtils.equals("废水", e.getOrderCategory())).findFirst().orElse(null);

                // 只有存在样品类型为废水的采样单，采样方式信息才会展示
                if (wasteWaterSample != null) {
                    EmcPropertyFieldBean propertyFieldForSampleWay = propertyFieldList.stream().filter(e -> ObjectUtils.equals("采样方式", e.getFieldName())).findFirst().orElse(null);
                    String propertyFieldNameForSampleWay = propertyFieldForSampleWay == null ? "" : propertyFieldForSampleWay.getInternalField();
                    sampleWay = propertyDataMap.get(propertyFieldNameForSampleWay) == null ? "" : (String) propertyDataMap.get(propertyFieldNameForSampleWay);
                    dataMap.put("sampleWay", sampleWay);
                } else {
                    Word2007Utils.deleteParagraph(document, "sampleWay");
                }
            } else {
                dataMap.put("ext1", "");
                dataMap.put("ext2", "");
                dataMap.put("ext3", "");
                dataMap.put("ext4", "");
                dataMap.put("ext5", "");
                dataMap.put("sampleWay", "");
            }
        }
    }

    // 给结果表中写入报出结果数据，并生成相应备注信息
    private int setReportDataForResultTable(List<EmcOrderBean> orderList, Long folderId, String monitorPointName, List<EmcReportResultBean> resultByFolderList, List<List<Object>> resultTableDataList,
            StringBuilder remark, List<String> itemNameForTableList) {

        List<EmcOrderBean> orderByFolderList = orderList.stream().filter(e -> ObjectUtils.equals(folderId, e.getFolderId())).collect(Collectors.toList());
        int dataCount = 0;
        // 普通样品
        List<EmcOrderBean> normalOrderList = orderByFolderList.stream().filter(e -> e.getParentOrderNo() == null).collect(Collectors.toList());
        if (normalOrderList != null && normalOrderList.size() > 0) {

            dataCount += normalOrderList.size();

            for (int k = 0; k < normalOrderList.size(); k++) {
                EmcOrderBean order = normalOrderList.get(k);

                List<EmcReportResultBean> resultByOrderList = resultByFolderList.stream().filter(e -> ObjectUtils.equals(order.getId(), e.getOrderId())).collect(Collectors.toList());

                // 报出结果值
                List<Object> resultRowDataList = new ArrayList<Object>();

                resultRowDataList.add(order.getOrderNo());
                resultRowDataList.add(monitorPointName);

                itemNameForTableList.forEach(n -> {

                    EmcReportResultBean resultByItemName = resultByOrderList.stream().filter(r -> ObjectUtils.equals(n, r.getItemName())).findFirst().orElse(null);
                    if (resultByItemName != null) {
                        resultRowDataList.add(resultByItemName.getReportResult());
                    } else {
                        resultRowDataList.add("-");
                    }
                });

                resultTableDataList.add(resultRowDataList);
            }
        }

        // 平行样和其父样
        // 同时添加备注信息
        // 同一点位编码下的平行样，与其父样（正常样）,备注平行样；如果没有平行样，备注其他信息
        // 样品有父样编号的就是平行样
        List<EmcOrderBean> equalRankOrderList = orderByFolderList.stream().filter(e -> e.getParentOrderNo() != null).collect(Collectors.toList());
        List<String> parentOrderNoList = equalRankOrderList.stream().map(e -> e.getParentOrderNo()).distinct().collect(Collectors.toList());
        List<EmcOrderBean> parentOrderList = orderByFolderList.stream().filter(e -> parentOrderNoList.contains(e.getOrderNo())).collect(Collectors.toList());

        if (parentOrderList != null && parentOrderList.size() > 0) {

            dataCount += parentOrderList.size();

            for (int k = 0; k < parentOrderList.size(); k++) {

                EmcOrderBean parentOrder = parentOrderList.get(k);
                String parentOrderNo = parentOrder.getOrderNo();

                List<EmcOrderBean> equalRankOrderForParentList = orderByFolderList.stream().filter(e -> ObjectUtils.equals(parentOrder.getOrderNo(), e.getParentOrderNo()))
                        .collect(Collectors.toList());
                List<Long> equalRankOrderIdList = equalRankOrderForParentList.stream().map(e -> e.getId()).collect(Collectors.toList());
                List<String> equalRankOrderNoList = equalRankOrderForParentList.stream().map(e -> e.getOrderNo()).collect(Collectors.toList());
                String equalRankOrderNos = StringUtils.join(equalRankOrderNoList, "、");

                // 报出结果值
                List<Object> resultRowDataList = new ArrayList<Object>();

                resultRowDataList.add((parentOrderNo == null ? "" : parentOrderNo) + (equalRankOrderNos == null ? "" : "（" + equalRankOrderNos + "）"));
                resultRowDataList.add(monitorPointName);

                List<EmcReportResultBean> resultByOrderList = resultByFolderList.stream()
                        .filter(e -> ObjectUtils.equals(parentOrder.getId(), e.getOrderId()) || equalRankOrderIdList.contains(e.getOrderId())).collect(Collectors.toList());

                itemNameForTableList.forEach(n -> {

                    List<EmcReportResultBean> resultByItemNameList = resultByOrderList.stream().filter(r -> ObjectUtils.equals(n, r.getItemName())).collect(Collectors.toList());

                    resultRowDataList.add(average(resultByItemNameList));
                });

                resultTableDataList.add(resultRowDataList);

                remark.append((parentOrderNo == null ? "" : parentOrderNo) + (equalRankOrderNos == null ? "" : "和" + equalRankOrderNos + "为平行样。"));

            }
        } else {
            remark.append("“L”表示低于检出限");
        }

        return dataCount;
    }

    // 为噪声表插入数据，并生成表
    private void setDataForNoiseTable(XWPFDocument document, Map<String, Object> dataMap, List<EmcReportResultBean> reportResultList, List<String> evaluationList, List<Long> orderIdList,
            String time) {

        if (orderIdList != null && orderIdList.size() > 0) {

            // 表格数据容器
            List<List<Object>> resultTableDataList = new ArrayList<List<Object>>();

            // 表格中表头数据
            List<Object> resultHeadRowDataList = new ArrayList<Object>();

            resultHeadRowDataList.add("测点编号");
            resultHeadRowDataList.add("测量值");
            resultHeadRowDataList.add("背景值");
            resultHeadRowDataList.add("修正值");
            resultHeadRowDataList.add("达标判定");

            resultTableDataList.add(resultHeadRowDataList);

            String exceedStandard = "未超过";// 达标判定
            for (int i = 0; i < orderIdList.size(); i++) {
                Long orderId = orderIdList.get(i);
                List<EmcReportResultBean> resultForOrderList = reportResultList.stream().filter(e -> ObjectUtils.equals(orderId, e.getOrderId())).collect(Collectors.toList());
                List<Object> resultRowDataList = new ArrayList<Object>();

                resultRowDataList.add(i + 1);

                EmcReportResultBean resultForCeLiang = resultForOrderList.stream().filter(e -> ObjectUtils.equals("测量值", e.getItemName())).findFirst().orElse(null);
                resultRowDataList.add(resultForCeLiang != null ? resultForCeLiang.getReportResult() : "-");

                EmcReportResultBean resultForBeiJing = resultForOrderList.stream().filter(e -> ObjectUtils.equals("背景值", e.getItemName())).findFirst().orElse(null);
                resultRowDataList.add(resultForBeiJing != null ? resultForBeiJing.getReportResult() : "-");

                EmcReportResultBean resultForXiuZheng = resultForOrderList.stream().filter(e -> ObjectUtils.equals("修正值", e.getItemName())).findFirst().orElse(null);
                resultRowDataList.add(resultForXiuZheng != null ? resultForXiuZheng.getReportResult() : "-");
                if (resultForXiuZheng != null && "1".equals(resultForXiuZheng.getExceedStandard())) {
                    resultRowDataList.add("不达标");
                    exceedStandard = "超过";
                } else {
                    resultRowDataList.add("达标");
                }

                resultTableDataList.add(resultRowDataList);
            }

            List<EmcReportResultBean> resultList = reportResultList.stream().filter(e -> orderIdList.contains(e.getOrderId())).collect(Collectors.toList());

            // 主要声源
            List<Object> resultRowDataForMainSourceList = new ArrayList<Object>();
            EmcReportResultBean resultForMainSource = resultList.stream().filter(e -> ObjectUtils.equals("主要声源", e.getItemName())).findFirst().orElse(null);
            resultRowDataForMainSourceList.add("主要声源");
            resultRowDataForMainSourceList.add(resultForMainSource != null ? resultForMainSource.getReportResult() : "-");

            // 执行标准
            List<Object> resultRowDataForStanderList = new ArrayList<Object>();
            List<String> standerList = resultList.stream().map(e -> e.getExt$Item("indexname")).distinct().collect(Collectors.toList());
            String standers = StringUtils.join(standerList, "、");
            standers += "。";
            resultRowDataForStanderList.add("执行标准");
            resultRowDataForStanderList.add(standers);

            // 排放限值
            List<Object> resultRowDataForLimitList = new ArrayList<Object>();
            EmcReportResultBean resultForLimit = resultList.stream().filter(e -> ObjectUtils.equals("排放限值", e.getItemName())).findFirst().orElse(null);
            resultRowDataForLimitList.add("排放限值");
            resultRowDataForLimitList.add(resultForLimit != null ? resultForLimit.getReportResult() : "-");

            // 填补数据
            for (int i = 0; i < 3; i++) {
                resultRowDataForMainSourceList.add("");
                resultRowDataForStanderList.add("");
                resultRowDataForLimitList.add("");
            }
            resultTableDataList.add(resultRowDataForMainSourceList);
            resultTableDataList.add(resultRowDataForStanderList);
            resultTableDataList.add(resultRowDataForLimitList);

            // 创建表
            listListNullToString(resultTableDataList, "-");

            Word2007Utils.createTable(document, "resultTableInfo", time + "间社会生活环境噪声监测结果", "单位：dB（A）", resultTableDataList, null, null, null, null);

            // 调整表格格式（合并列）
            XWPFTable table = document.getTables().get(3);
            Word2007Utils.mergeTableColumns(table, orderIdList.size() + 1, 1, 4);
            Word2007Utils.mergeTableColumns(table, orderIdList.size() + 2, 1, 4);
            Word2007Utils.mergeTableColumns(table, orderIdList.size() + 3, 1, 4);

            // 评价语
            if (evaluationList != null && evaluationList.size() > 0) {

                StringBuilder evaluationStringBuilder = new StringBuilder(evaluationList.get(0));
                evaluationStringBuilder.append(time + "间产生的社会生活环境噪声测量值" + exceedStandard + standers);
                evaluationList.set(0, evaluationStringBuilder.toString());
            } else {
                StringBuilder evaluation = new StringBuilder();
                evaluation.append("佛山市禅城区" + dataMap.get("testOrgName") + time + "间产生的社会生活环境噪声测量值" + exceedStandard + standers);
                evaluationList.add(evaluation.toString());
            }
        }
    }

    // 为点位表生成数据
    private List<List<Object>> setTableDataForFolder(List<EmcFolderBean> folderList, List<EmcOrderBean> orderList, List<EmcOrderTaskBean> orderTaskList, List<EmcRunBean> runList, String sampleWay,
            List<EmcReportResultBean> reportResultList) {
        // 点位表数据
        List<List<Object>> folderTableDataList = new ArrayList<List<Object>>();
        for (EmcFolderBean f : folderList) {
            List<EmcOrderBean> folderOrderList = orderList.stream().filter(o -> ObjectUtils.equals(o.getFolderId(), f.getId())).collect(Collectors.toList());

            LocalDate sampleCompleteDate = folderOrderList.stream().filter(e -> e.getSampleCompleteDate() != null).map(e -> e.getSampleCompleteDate()).max(Comparator.naturalOrder()).orElse(null);
            List<Long> folderOrderIdList = folderOrderList.stream().map(e -> e.getId()).collect(Collectors.toList());
            List<String> testNameList = reportResultList.stream().filter(e -> ObjectUtils.equals(e.getFolderId(), f.getId())).map(e -> e.getTestName()).distinct().collect(Collectors.toList());
            String testNames = StringUtils.join(testNameList, "、");
            List<String> folderRunIdList = orderTaskList.stream().filter(e -> folderOrderIdList.contains(e.getOrderId())).map(e -> e.getRunId()).distinct().collect(Collectors.toList());
            List<EmcRunBean> folderRunList = runList.stream().filter(e -> folderRunIdList.contains(e.getId())).collect(Collectors.toList());
            LocalDate analysisBeginDate = folderRunList.stream().map(e -> e.getAnalysisBeginDate()).min(Comparator.naturalOrder()).orElse(null);
            LocalDate analysisEndDate = folderRunList.stream().map(e -> e.getAnalysisEndDate()).min(Comparator.naturalOrder()).orElse(null);

            List<Object> folderRowDataList = new ArrayList<Object>();
            folderRowDataList.add(f.getOrderCategory());
            folderRowDataList.add(f.getMonitorPointName());
            folderRowDataList.add((sampleCompleteDate == null ? "" : sampleCompleteDate.toString()) + "，" + sampleWay + "："
                    + (folderOrderList.get(0).getFrequency() == null ? "" : folderOrderList.get(0).getFrequency()) + "次");
            folderRowDataList.add(testNames);
            // 如果分析开始时间和结束时间是同一天，只展示一天
            String analysisDate = getDateZones(analysisBeginDate, analysisEndDate);
            folderRowDataList.add(analysisDate);
            folderTableDataList.add(folderRowDataList);
        }
        return folderTableDataList;
    }

    // 为测试表生成数据
    private List<List<Object>> setTableDataForTest(List<EmcReportResultBean> reportResultBean) {

        List<List<Object>> testTableDataList = new ArrayList<List<Object>>();

        // 名为 XXXX参数XX 的测试不需要出现在技术依据里面
        List<EmcReportResultBean> distinctReportResultList = reportResultBean.stream().filter(e -> e.getTestName().indexOf("参数") < 0)
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getTestName()))), ArrayList::new));
        distinctReportResultList.forEach(o -> {

            List<Object> testRowDataList = new ArrayList<Object>();
            String testName = o.getTestName();
            testRowDataList.add(testName);

            if (o.getRunId() != null) {// 分析的

                testRowDataList.add(o.getExt$Item("analysisusername"));
                testRowDataList.add((o.getExt$Item("analysisequiptname") == null ? "" : o.getExt$Item("analysisequiptname"))
                        + (o.getExt$Item("analysisequiptno") == null ? "" : "（编号：" + o.getExt$Item("analysisequiptno") + "）"));
            } else {// 采样的

                testRowDataList.add(o.getExt$Item("sampleusername"));
                testRowDataList.add((o.getExt$Item("sampleequiptname") == null ? "" : o.getExt$Item("sampleequiptname"))
                        + (o.getExt$Item("sampleequiptno") == null ? "" : "（编号：" + o.getExt$Item("sampleequiptno") + "）"));
            }
            testRowDataList.add(o.getExt$Item("testmethodbasis"));

            String indexLowLimit = o.getIndexLowLimit();
            if (indexLowLimit == null) {

                testRowDataList.add("/");

            } else {
                if ("pH值".equals(testName)) {
                    indexLowLimit = indexLowLimit + "（pH值）";
                } else {
                    indexLowLimit = indexLowLimit + (o.getUnitName() == null ? "" : o.getUnitName());
                }
                testRowDataList.add(indexLowLimit);
            }

            testTableDataList.add(testRowDataList);
        });
        return testTableDataList;
    }

    // 为样品表生成数据
    private List<List<Object>> setTableDataForOrder(Map<String, Object> dataMap, List<EmcOrderBean> orderList, List<EmcOrderTaskBean> orderTaskList, List<EmcRunBean> runList,
            List<EmcReportResultBean> reportResultList, String sampleWay) {

        List<List<Object>> orderTableDataList = new ArrayList<List<Object>>();
        for (EmcOrderBean f : orderList) {

            List<String> testNameList = reportResultList.stream().filter(e -> ObjectUtils.equals(e.getOrderId(), f.getId())).map(e -> e.getTestName()).distinct().collect(Collectors.toList());
            String testNames = StringUtils.join(testNameList, "、");
            List<String> orderRunIdList = orderTaskList.stream().filter(e -> ObjectUtils.equals(e.getOrderId(), f.getId())).map(e -> e.getRunId()).distinct().collect(Collectors.toList());
            List<EmcRunBean> orderRunList = runList.stream().filter(e -> orderRunIdList.contains(e.getId())).collect(Collectors.toList());
            LocalDate analysisBeginDate = orderRunList.stream().map(e -> e.getAnalysisBeginDate()).min(Comparator.naturalOrder()).orElse(null);
            LocalDate analysisEndDate = orderRunList.stream().map(e -> e.getAnalysisEndDate()).min(Comparator.naturalOrder()).orElse(null);

            List<Object> orderRowDataList = new ArrayList<Object>();
            orderRowDataList.add(f.getOrderCategory());
            orderRowDataList.add(f.getOrderNo());
            orderRowDataList.add(f.getExt$Item("monitorpointname"));
            orderRowDataList.add((f.getSampleCompleteDate() == null ? "" : f.getSampleCompleteDate().toString()) + "，" + sampleWay + "：" + (f.getFrequency() == null ? "" : f.getFrequency()) + "次");
            orderRowDataList.add(testNames);
            // 如果分析开始时间和结束时间是同一天，只展示一天
            String analysisDate = getDateZones(analysisBeginDate, analysisEndDate);
            orderRowDataList.add(analysisDate);
            orderTableDataList.add(orderRowDataList);
        }
        return orderTableDataList;
    }

    protected void setSubmitData(XWPFDocument document, EmcReportBean report) {
        // 查询提交状态
        List<CoreBpmnCommentBean> commentList = bpmnCommentService.selectListByFilter(
                SearchFilter.instance().match("targetId", getDao().getTable() + "$" + report.getId()).filter(MatchPattern.EQ).match("status", "REJECT").filter(MatchPattern.DIFFER));
        CoreBpmnCommentBean submitComment = commentList.stream().filter(e -> ObjectUtils.equals("Draft", e.getStatusCode()))
                .sorted(Comparator.comparing(CoreBpmnCommentBean::getCreatedTime).reversed()).limit(1).findFirst().orElse(null);
        CoreBpmnCommentBean checkComment = commentList.stream().filter(e -> ObjectUtils.equals("reportCheck", e.getStatusCode()))
                .sorted(Comparator.comparing(CoreBpmnCommentBean::getCreatedTime).reversed()).limit(1).findFirst().orElse(null);
        CoreBpmnCommentBean auditComment = commentList.stream().filter(e -> ObjectUtils.equals("reportAudit", e.getStatusCode()))
                .sorted(Comparator.comparing(CoreBpmnCommentBean::getCreatedTime).reversed()).limit(1).findFirst().orElse(null);
        CoreBpmnCommentBean issueComment = commentList.stream().filter(e -> ObjectUtils.equals("reportIssue", e.getStatusCode()))
                .sorted(Comparator.comparing(CoreBpmnCommentBean::getCreatedTime).reversed()).limit(1).findFirst().orElse(null);

        // 添加个性签名
        if (submitComment != null) {
            insertSignature(document, submitComment, "编  写：");
        }
        if (checkComment != null) {
            insertSignature(document, checkComment, "复  核：");
        }
        if (auditComment != null) {
            insertSignature(document, auditComment, "审  核：");
        }
        if (issueComment != null) {
            insertSignature(document, issueComment, "签  发：");
            String empId = issueComment.getCreatedById();
            EmcEmpBean emp = empService.selectDetail(empId);
            if (emp != null) {
                Word2007Utils.insertContentAndImage("签发人职务：", document, emp.getPosition(), null);
                Word2007Utils.insertContentAndImage("签发日期：", document, LocalDate.now().toString(), null);
            }
        }
    }

    // 添加个性签名图片
    private void insertSignature(XWPFDocument document, CoreBpmnCommentBean submitComment, String insertPlace) {
        String userId = submitComment.getCreatedById();
        List<CoreFileBean> userFileList = coreFileService.getDao().selectListByOneColumnValue("T_CORE_USER$" + userId, "targetId");
        if (!userFileList.isEmpty()) {
            CoreFileBean userFile = userFileList.get(0);// 一个用户只有一个附件（电子签名）

            String userSignatureUrl = FilePathManager.getLocalPath(FilePathDTO.of(userFile.getScope(), DateTimeUtils.getDateSerialNumber(userFile.getCreatedTime()), userFile.getMd5Name(), userFile.getName())).toString();

            Word2007Utils.insertContentAndImage(insertPlace, document, null, userSignatureUrl);
        }
    }

    // 获取时间区间
    protected String getDateZones(LocalDate analysisBeginDate, LocalDate analysisEndDate) {

        if (analysisBeginDate == null && analysisEndDate == null) {
            return "-";
        } else if (analysisBeginDate == null) {
            return analysisEndDate.toString();
        } else if (analysisEndDate == null) {
            return analysisBeginDate.toString();
        } else if (ObjectUtils.equals(analysisBeginDate, analysisEndDate)) {
            return analysisEndDate.toString();
        } else {
            return analysisBeginDate.toString() + "～" + analysisEndDate.toString();
        }
    }

    // 处理map，value为null的值处理成指定字符串
    private void mapNullToString(Map<String, Object> map, String replaceText) {
        Set<String> set = map.keySet();
        if (set != null && !set.isEmpty()) {
            for (String key : set) {
                if (map.get(key) == null) {
                    map.put(key, replaceText);
                }
            }
        }
    }

    // 处理List<List>，value为null的值处理成指定字符串
    private void listListNullToString(List<List<Object>> resultTableByRowsDataList, String replaceText) {
        resultTableByRowsDataList.forEach(list -> {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == null) {
                    list.set(i, replaceText);
                }
            }
        });
    }

    /**
     * 平行均值的计算
     */
    protected void calParallelAverageValue(List<EmcOrderTaskResultBean> orderResultList) {
        // 查询分析项及修约规则
        List<Long> itemIdList = orderResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
        List<EmcItemRevisionBean> itemRevisionList = itemRevisionService.getDao().selectListByOneColumnValues(itemIdList, "ITEMID");
        List<EmcItemBean> itemList = itemService.getDao().selectListByIds(itemIdList);

        // 过滤出所有质控类型为现场平行和实验室平行的结果
        List<EmcOrderTaskResultBean> dupResultList = orderResultList.stream().filter(f -> EmcConstant.QC_L_DUP.equals(f.getQcCategoryCode()) || EmcConstant.QC_P_DUP.equals(f.getQcCategoryCode()))
                .collect(Collectors.toList());

        // 平行样的样品测试ID列表（现场平行和实验室平行）
        List<Long> dupOrderTaskIdList = dupResultList.stream().map(EmcOrderTaskResultBean::getOrderTaskId).distinct().collect(Collectors.toList());
        // 查询平行样的样品测试（主要查询父样品测试ID和QC类型）
        List<EmcOrderTaskBean> dupOrderTaskList = orderTaskService.getDao().selectListByIds(dupOrderTaskIdList, Arrays.asList("ID", "PARENTID", "QCCATEGORYCODE"));
        // 获取平行样的父样品测试ID
        List<Long> dupParentOrderTaskIdList = dupOrderTaskList.stream().map(EmcOrderTaskBean::getParentId).distinct().collect(Collectors.toList());

        orderResultList.stream().filter(r -> dupParentOrderTaskIdList.contains(r.getOrderTaskId())).forEach(r -> {
            List<Long> childDupOrderTaskIdList = dupOrderTaskList.stream().filter(t -> ObjectUtils.equals(t.getParentId(), r.getOrderTaskId())).map(EmcOrderTaskBean::getId)
                    .collect(Collectors.toList());

            // 获取所有的实验室平行样品结果，过滤条件为样品测试ID + 分析项ID + QC类型
            List<EmcOrderTaskResultBean> ldupChildList = orderResultList.stream()
                    .filter(m -> childDupOrderTaskIdList.contains(m.getOrderTaskId()) && ObjectUtils.equals(r.getItemId(), m.getItemId()) && EmcConstant.QC_L_DUP.equals(m.getQcCategoryCode()))
                    .collect(Collectors.toList());

            // 获取所有的现场平行样品结果，排除无效结果
            List<EmcOrderTaskResultBean> pdupChildList = orderResultList.stream()
                    .filter(m -> childDupOrderTaskIdList.contains(m.getOrderTaskId()) && ObjectUtils.equals(r.getItemId(), m.getItemId())
                            && EmcConstant.QC_P_DUP.equals(m.getQcCategoryCode()) && !"/".equals(m.getReportResult()))
                    .collect(Collectors.toList());

            // 如果有实验室平行的子样结果的话取平均结果,否则取原始结果
            String result = ldupChildList.isEmpty() ? r.getReportResult() : r.getAvgResult();

            // 现场平行计算原始结果
            String originalResult = this.calResult(result, pdupChildList);
            r.setOriginalResult(originalResult);

            // 获取分析项的分段修约规则
            List<EmcItemRevisionBean> filterItemRevisionList = itemRevisionList.stream().filter(f -> ObjectUtils.equals(r.getItemId(), f.getItemId())).collect(Collectors.toList());
            EmcItemBean item = itemList.stream().filter(f -> ObjectUtils.equals(r.getItemId(), f.getId())).findFirst().orElse(null);
            orderTaskResultService.setReportResultValue(r, item, filterItemRevisionList);

            if (NumberUtils.isNumber(r.getLowLimit()) && NumberUtils.isNumber(r.getReportResult()) && NumberUtils.parseDouble(r.getReportResult()) < NumberUtils.parseDouble(r.getLowLimit())) {
                r.setReportResult("ND");
            }
        });
    }

    /**
     * 现场平行计算原始结果
     */
    private String calResult(String result, List<EmcOrderTaskResultBean> pdupChildList) {
        if (pdupChildList.isEmpty()) {
            return result;
        }

        double totalResult = 0;
        for (EmcOrderTaskResultBean orderTaskResult : pdupChildList) {
            totalResult += NumberUtils.parseDouble(orderTaskResult.getReportResult(), 0);
        }
        double total = ArithUtils.add(NumberUtils.parseDouble(result, 0), totalResult);
        double count = pdupChildList.size() + 1;
        return String.valueOf(ArithUtils.div(total, count));
    }

    // 计算报出结果的均值(如果报出为ND的话，就显示检出限+L)
    private String average(List<EmcReportResultBean> resultList) {

        if (resultList.isEmpty()) {
            return "0.0";
        }

        List<EmcReportResultBean> averageResultList = new ArrayList<>();

        for (int i = resultList.size() - 1; i > -1; i--) {
            EmcReportResultBean result = resultList.get(i);

            String reportResult = result.getReportResult();
            if ("ND".equals(reportResult)) {
                return result.getLowLimit() + "L";
            } else if (!StringUtils.isEmpty(reportResult) && NumberUtils.isNumber(reportResult)) {
                averageResultList.add(result);
            }
        }

        if (averageResultList.isEmpty()) {
            return "0.0";
        }

        double sum = averageResultList.stream().mapToDouble(r -> NumberUtils.parseDouble(r.getReportResult())).sum();

        String revisionRule = averageResultList.get(0).getRevisionRule();// 取第一条数据的修约规则

        if (!StringUtils.isEmpty(revisionRule) && !EmcConstant.REVISION_RULE_NONE_DESC.equals(revisionRule)) {

            return revisionRuleService.getRuledValue(ObjectUtils.toString(sum / averageResultList.size()), averageResultList.get(0).getRevisionRule());
        }

        return ObjectUtils.toString(sum / averageResultList.size());
    }

    // 提取工作流方法
    public List<CoreBpmnInstanceStatusDTO<Long>> startProcessBasic(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcReportService.super.startProcess(wrapper);
        return coreBpmnInstanceStatusDTOList;
    }

    // 提取工作流方法
    public List<CoreBpmnInstanceStatusDTO<Long>> startProcessBasic(List<Long> itemIdList, String comment) {
    	CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
        parameter.setComment(comment);
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcReportService.super.startProcess(itemIdList, parameter);
        return coreBpmnInstanceStatusDTOList;
    }

    // 提取工作流方法
    public List<CoreBpmnInstanceStatusDTO<Long>> startProcessBasic(List<Long> itemIdList, CoreBpmnParameterDTO parameter) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcReportService.super.startProcess(itemIdList, parameter);
        return coreBpmnInstanceStatusDTOList;
    }

    // 提取工作流方法
    public List<CoreBpmnInstanceStatusDTO<Long>> completeTaskBasic(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcReportService.super.completeTask(wrapper);
        return coreBpmnInstanceStatusDTOList;
    }

    // 提取工作流方法
    @Override
    public List<CoreBpmnInstanceStatusDTO<Long>> rejectTaskBasic(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcReportService.super.rejectTask(wrapper);
        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    public Page<EmcArchiveCategoryBean> selectArchiveCategoryPagination(RestJsonWrapperBean wrapper) {
        return archiveCategoryService.selectPagination(wrapper);
    }

    @Override
    public void archiveFile(RestJsonWrapperBean wrapper) {
        archiveFileService.archive(wrapper);
    }

    @Override
    public Long viewRsReport(RestJsonWrapperBean wrapper) {
        EmcReportBean report = wrapper.parseUnique(EmcReportBean.class);
        Long rsReportConfigLineId = report.getRsReportConfigLineId();

        CoreReportConfigLineBean configLine = coreReportConfigLineService.selectById(rsReportConfigLineId);
        if (configLine == null) {
            return 0L;
        }

        List<EmcReportBean> reportList = getDao().selectListByOneColumnValue(report.getProjId(), "PROJID", Collections.singletonList("ID"));

        String reportingServiceServerUrl = ApplicationContextHelper.getConstantValue("REPORTING_SERVICE_SERVER_URL");

        String url = ReportingServiceUtils.getDownloadUrl(reportingServiceServerUrl, configLine, report, "PDF");
        String fileName = String.format(EmcConstant.DEFAULT_RS_REPORT_FILE_NAME, System.currentTimeMillis());
        File downloadFile = ReportingServiceUtils.downloadFile(url, fileName);

        List<CoreFileBean> deleteFileList = new ArrayList<>();
        reportList.forEach(r -> {
            CoreFileBean fileFilter = new CoreFileBean();
            fileFilter.setTargetId(getDao().getTable() + "$" + r.getId());
            fileFilter.setBizCategory(EmcConstant.RS_REPORT_FILE_BIZ_CATEGORY);
            deleteFileList.add(fileFilter);
        });
        coreFileService.getDao().deleteBy(deleteFileList, "TARGETID", "BIZCATEGORY");

        List<CoreFileBean> coreFileList = saveWordReportCoreFile(fileName, downloadFile, EmcConstant.RS_REPORT_FILE_BIZ_CATEGORY, reportList);

        return coreFileList.get(0).getId();
    }

    @Override
    public Long viewDataSheet(RestJsonWrapperBean wrapper) {
        EmcReportBean report = wrapper.parseUnique(EmcReportBean.class);
        Long dataSheetId = report.getDataSheetId();

        CoreReportConfigLineBean configLine = coreReportConfigLineService.selectById(dataSheetId);
        if (configLine == null) {
            return 0L;
        }

        List<EmcReportBean> reportList = getDao().selectListByOneColumnValue(report.getProjId(), "PROJID", Collections.singletonList("ID"));
        String reportingServiceServerUrl = ApplicationContextHelper.getConstantValue("REPORTING_SERVICE_SERVER_URL");

        String url = ReportingServiceUtils.getDownloadUrl(reportingServiceServerUrl, configLine, report, "EXCEL");
        String fileName = String.format(EmcConstant.DEFAULT_DATA_SHEET_FILE_NAME, System.currentTimeMillis());
        File downloadFile = ReportingServiceUtils.downloadFile(url, fileName);

        List<CoreFileBean> deleteFileList = new ArrayList<>();
        reportList.forEach(r -> {
            CoreFileBean fileFilter = new CoreFileBean();
            fileFilter.setTargetId(getDao().getTable() + "$" + r.getId());
            fileFilter.setBizCategory(EmcConstant.DATE_SHEET_FILE_BIZ_CATEGORY);
            deleteFileList.add(fileFilter);
        });
        coreFileService.getDao().deleteBy(deleteFileList, "TARGETID", "BIZCATEGORY");

        List<CoreFileBean> coreFileList = saveWordReportCoreFile(fileName, downloadFile, EmcConstant.DATE_SHEET_FILE_BIZ_CATEGORY, reportList);

        return coreFileList.get(0).getId();
    }

    @Override
    public Page<EmcRunBean> selectRunByProjId(Long projId, RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskBeanList = orderTaskService.selectListByFilter(SearchFilter.instance().match("PROJID", projId).filter(MatchPattern.EQ).match("ISUNDO",Arrays.asList("","0")).filter(MatchPattern.OR));
        List<String> runIds = orderTaskBeanList.stream().map(EmcOrderTaskBean::getRunId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if(runIds.size() == 0){
            return null;
        }
        return runService.selectPaginationByFilter(SearchFilter.instance().match("ID", runIds).filter(MatchPattern.OR), wrapper);
    }

    @Override
    public void undoRun(Long projId, RestJsonWrapperBean wrapper) {
        List<EmcRunBean> runBeanList = wrapper.parse(EmcRunBean.class);
        List<String> runIds = runBeanList.stream().map(EmcRunBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskBeans = orderTaskService.selectListByFilter(SearchFilter.instance().match("PROJID",projId).filter(MatchPattern.EQ).match("RUNID", runIds).filter(MatchPattern.OR));
        orderTaskBeans.forEach(e -> {
            e.setExt$Item("ISUNDO", "1");
        });
        orderTaskService.getDao().update(orderTaskBeans, "ISUNDO");
    }

    @Override
    public void submitUndid(RestJsonWrapperBean wrapper) {
        List<EmcRunBean> runBeanList = wrapper.parse(EmcRunBean.class);
        List<String> runIds = runBeanList.stream().map(EmcRunBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskBeans = orderTaskService.selectListByFilter(SearchFilter.instance().match("RUNID", runIds).filter(MatchPattern.OR));
        //List<Long> orderTaskIds = orderTaskBeans.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        orderTaskBeans.forEach(e -> {
            e.setExt$Item("ISUNDO", "0");
        });
        orderTaskService.getDao().update(orderTaskBeans, "ISUNDO");
    }

    public List<CoreFileBean> saveWordReportCoreFile(String fileName, File file, String bizCategory, List<EmcReportBean> reportList) {
        List<CoreFileBean> insertCoreFileList = new ArrayList<>();
        CoreFileBean copyFile = new CoreFileBean();
        copyFile.setName(fileName);
        copyFile.setFileExt(FileUtils.getFileExtension(fileName));
        copyFile.setScope(FileScope.export.name());
        copyFile.setTimes(0L);
        copyFile.setDownloadable(Constant.YES);
        copyFile.setCreatedTime(LocalDateTime.now());
        copyFile.setBizCategory(bizCategory);
        copyFile.setSize(file.length());

        List<Long> fileIdList = ApplicationContextHelper.getNextIdentityList(reportList.size());
        IntStream.range(0, reportList.size()).forEach(i -> {
            EmcReportBean oldReport = reportList.get(i);
            CoreFileBean insertCoreFile = new CoreFileBean();
            BeanUtils.copyProperties(copyFile, insertCoreFile);
            Long fileId = fileIdList.get(i);
            insertCoreFile.setId(fileId);
            insertCoreFile.setTargetId(getDao().getTable() + "$" + oldReport.getId());
            insertCoreFile.setMd5Name(EncryptUtils.MD5Encrypt(fileId + "$" + fileName));
            // 将文件复制一份到export目录下，并与报告ID绑定
            fileManager.upload(CoreFileUtils.toFilePath(insertCoreFile), file);
            insertCoreFileList.add(insertCoreFile);
        });

        coreFileService.getDao().insert(insertCoreFileList);

        return insertCoreFileList;
    }

    public void generateWordReport(File templateFile, File wordReportFile, EmcReportBean report, List<EmcReportBean> reportList, EmcProjectBean project, boolean exist) {
        try (FileInputStream inputStream = new FileInputStream(templateFile);
             XWPFDocument document = new XWPFDocument(inputStream);
             FileOutputStream outputStream = new FileOutputStream(wordReportFile)) {

            if (!exist) {
                writeDataToWord(templateFile, document, report, project);
            }

            setSubmitInformation(document, report);

            document.write(outputStream);
        } catch (IOException e) {
            throw new FileException(e);
        }
    }

    /**
     * 向 Word 报告模板中写入数据
     * @param document
     * @param report
     * @param project
     */
    public void writeDataToWord(File templateFile, XWPFDocument document, EmcReportBean report, EmcProjectBean project) {

    }

    private CoreCodeBean getContractCode(EmcProjectBean project) {
        // 根据项目中的合同ID来查询检测类别，一个项目可能绑定了多个合同
        CoreCodeBean coreCode = new CoreCodeBean();
        Optional.ofNullable(project.getExt$Item("contractid")).ifPresent(c -> {
            List<Long> contractIdList = Arrays.stream(c.split(",")).filter(NumberUtils::isNumber).map(s -> NumberUtils.parseLong(s.trim())).collect(Collectors.toList());
            List<EmcContractBean> contractList = contractService.getDao().selectListByIds(contractIdList);
            if (contractList.isEmpty()) {
                return;
            }
            EmcContractBean contract = contractList.get(0);
            Optional.ofNullable(contract.getExt$Item("DETECTCATEGORY")).ifPresent(d -> {
                CoreCodeBean filter = new CoreCodeBean();
                filter.setCodeCategoryId("detectCategory");
                filter.setCode(d);
                CoreCodeBean code = coreCodeService.getDao().selectFirstIfPresent(filter, Arrays.asList("CODECATEGORYID", "CODE"), Collections.singletonList("CODENAME"));
                if (code != null) {
                    coreCode.setCodeName(code.getCodeName());
                }
            });
        });

        return coreCode;
    }

    private List<Long> parseMultiEquipt(List<String> equiptIdList) {
        List<String> parsedEquiptIdList = new ArrayList<>();
        equiptIdList.forEach(multiEquiptId -> {
            if (StringUtils.contains(multiEquiptId, ",")) {
                parsedEquiptIdList.addAll(Arrays.asList(StringUtils.split(multiEquiptId, ",")));
            } else {
                parsedEquiptIdList.add(multiEquiptId);
            }
        });

        return parsedEquiptIdList.stream().filter(NumberUtils::isNumber).distinct().map(NumberUtils::parseLong).collect(Collectors.toList());
    }

    /**
     * 保存Word报告文件及相关信息
     * @param wordReportFileName
     * @param wordReportFile
     * @param reportList
     * @return
     */
    public List<CoreFileBean> saveWordReportCoreFile(String wordReportFileName, File wordReportFile, List<EmcReportBean> reportList) {
        List<CoreFileBean> insertCoreFileList = new ArrayList<>();
        CoreFileBean copyFile = new CoreFileBean();
        copyFile.setName(wordReportFileName);
        copyFile.setFileExt(FileUtils.getFileExtension(wordReportFileName));
        copyFile.setScope(FileScope.export.name());
        copyFile.setTimes(0L);
        copyFile.setDownloadable(Constant.YES);
        copyFile.setCreatedTime(LocalDateTime.now());
        copyFile.setBizCategory(EmcConstant.WORD_REPORT_ATTACHMENT_TYPE);
        copyFile.setSize(wordReportFile.length());

        List<Long> fileIdList = ApplicationContextHelper.getNextIdentityList(reportList.size());
        IntStream.range(0, reportList.size()).forEach(i -> {
            EmcReportBean oldReport = reportList.get(i);
            CoreFileBean insertCoreFile = new CoreFileBean();
            BeanUtils.copyProperties(copyFile, insertCoreFile);
            Long fileId = fileIdList.get(i);
            insertCoreFile.setId(fileId);
            insertCoreFile.setTargetId(getDao().getTable() + "$" + oldReport.getId());
            insertCoreFile.setMd5Name(EncryptUtils.MD5Encrypt(fileId + "$" + wordReportFileName));
            // 将文件复制一份到export目录下，并与报告ID绑定
            fileManager.upload(CoreFileUtils.toFilePath(insertCoreFile), wordReportFile);
            insertCoreFileList.add(insertCoreFile);
        });

        coreFileService.getDao().insert(insertCoreFileList);

        return insertCoreFileList;
    }

    /**
     * 设置提交人信息
     * @param document
     * @param report
     */
    private void setSubmitInformation(XWPFDocument document, EmcReportBean report) {
        List<CoreBpmnCommentBean> commentList = bpmnCommentService.selectListByFilter(SearchFilter.instance().match("targetId", getDao().getTable()
                + "$" + report.getId()).filter(MatchPattern.EQ).match("status", "REJECT").filter(MatchPattern.DIFFER));
        CoreBpmnCommentBean submitComment = commentList.stream().filter(e -> ObjectUtils.equals("Draft", e.getStatusCode()))
                .sorted(Comparator.comparing(CoreBpmnCommentBean::getCreatedTime).reversed()).limit(1).findFirst().orElse(null);
        CoreBpmnCommentBean auditComment = commentList.stream().filter(e -> ObjectUtils.equals("reportAudit", e.getStatusCode()))
                .sorted(Comparator.comparing(CoreBpmnCommentBean::getCreatedTime).reversed()).limit(1).findFirst().orElse(null);
        CoreBpmnCommentBean issueComment = commentList.stream().filter(e -> ObjectUtils.equals("reportIssue", e.getStatusCode()))
                .sorted(Comparator.comparing(CoreBpmnCommentBean::getCreatedTime).reversed()).limit(1).findFirst().orElse(null);

        List<XWPFTable> tableList = document.getTables();
        // 提交人位于模板中的第三个表格
        XWPFTable lastTable = tableList.get(2);

        if (submitComment != null) {
            addSignaturePicture(lastTable, 2, submitComment, null);
            Word2007Utils.insertContentAndImageToTable(lastTable, 8, null, DateTimeUtils.formatLocalDateTime(submitComment.getCreatedTime(), "yyyy-MM-dd"));
        }

        if (auditComment != null) {
            addSignaturePicture(lastTable, 4, auditComment, null);
            Word2007Utils.insertContentAndImageToTable(lastTable, 10, null, DateTimeUtils.formatLocalDateTime(auditComment.getCreatedTime(), "yyyy-MM-dd"));
        }

        if (issueComment != null) {
            addSignaturePicture(lastTable, 6, issueComment, null);
            Word2007Utils.insertContentAndImageToTable(lastTable, 12, null, DateTimeUtils.formatLocalDateTime(issueComment.getCreatedTime(), "yyyy-MM-dd"));
        }
    }

    /**
     * 添加电子签名
     * @param table
     * @param cellNum
     * @param comment
     * @param content
     */
    private void addSignaturePicture(XWPFTable table, int cellNum, CoreBpmnCommentBean comment, String content) {
        String creatorId = comment.getCreatedById();
        List<CoreFileBean> userSignatureFileList = coreFileService.getDao().selectListByOneColumnValue("T_CORE_USER$" + creatorId, "targetId");
        if (!userSignatureFileList.isEmpty()) {
            // 一个用户只有一个点子签名文件
            CoreFileBean userSignatureFile = userSignatureFileList.get(0);

            String userSignatureFileUrl = FilePathManager.getLocalPath(FilePathDTO.of(userSignatureFile.getScope(),
                    DateTimeUtils.getDateSerialNumber(userSignatureFile.getCreatedTime()), userSignatureFile.getMd5Name(),
                    userSignatureFile.getName())).toString();

            // 如果签名文件不存在，则不插入图片
            if (!new File(userSignatureFileUrl).exists()) {
                return;
            }

            Word2007Utils.insertContentAndImageToTable(table, cellNum, userSignatureFileUrl, content);
        }
    }

    /**
     * 计算样品值
     * @param orderResultList
     * @param itemRevisionList
     * @param itemList
     * @return
     */
    protected List<EmcOrderTaskResultBean> calcParallelAverageValue(List<EmcOrderTaskResultBean> orderResultList, List<EmcItemRevisionBean> itemRevisionList, List<EmcItemBean> itemList) {
        // 过滤出所有质控类型为现场平行结果
        List<EmcOrderTaskResultBean> pdupResultList = orderResultList.stream().filter(f -> EmcConstant.QC_P_DUP.equals(f.getQcCategoryCode())).collect(Collectors.toList());
        List<Long> pdupOrderTaskIdList = pdupResultList.stream().map(EmcOrderTaskResultBean::getOrderTaskId).distinct().collect(Collectors.toList());
        // 查询平行样的样品测试（主要查询父样品测试ID和QC类型）
        List<EmcOrderTaskBean> pdupOrderTaskList = orderTaskService.getDao().selectListByIds(pdupOrderTaskIdList, Arrays.asList("ID", "PARENTID", "QCCATEGORYCODE"));
        // 获取平行样的父样品测试ID
        List<Long> pdupParentOrderTaskIdList = pdupOrderTaskList.stream().map(EmcOrderTaskBean::getParentId).distinct().collect(Collectors.toList());

        // 正常样品测试结果
        List<EmcOrderTaskResultBean> naResultList = orderResultList.stream().filter(f -> EmcConstant.N_A.equals(f.getQcCategoryCode())).collect(Collectors.toList());

        List<EmcOrderTaskResultBean> hasChildNaResultList = naResultList.stream().filter(result -> pdupParentOrderTaskIdList.contains(result.getOrderTaskId())).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> noChildNaResultList = naResultList.stream().filter(result -> !pdupParentOrderTaskIdList.contains(result.getOrderTaskId())).collect(Collectors.toList());

        List<EmcOrderTaskResultBean> finalResultList = new ArrayList<>(naResultList.size());
        noChildNaResultList.forEach(result -> {
            String finalValue = StringUtils.isEmpty(result.getAvgResult()) ? result.getReportResult() : result.getAvgResult();
            finalValue = NumberUtils.isScientificNumber(finalValue) ? NumberUtils.formatNumber(NumberUtils.parseDouble(finalValue)) : finalValue;

            result.setOriginalResult(finalValue);
            result.setReportResult(finalValue);

            // 对均值进行判断(使用原始值与检出限比较)
            if (NumberUtils.isNumber(result.getLowLimit()) && NumberUtils.isNumber(result.getOriginalResult())
                    && NumberUtils.parseDouble(result.getOriginalResult()) < NumberUtils.parseDouble(result.getLowLimit())) {
                result.setReportResult(EmcConstant.ND);
            }

            finalResultList.add(result);
        });

        // 过滤掉无效的有现场平行的正常样品结果
        List<EmcOrderTaskResultBean> validHasChildNaResultList = hasChildNaResultList.stream().filter(this::isEffectiveResult).collect(Collectors.toList());
        validHasChildNaResultList.forEach(result -> {
            // 替换正常样品中的ND或HD，此处替换是为了下面计算均值
            replaceAvgOrReportResultNdHdValue(result);

            List<Long> childPdupOrderTaskIdList = pdupOrderTaskList.stream().filter(task -> ObjectUtils.equals(result.getOrderTaskId(), task.getParentId())).map(EmcOrderTaskBean::getId).collect(Collectors.toList());
            List<EmcOrderTaskResultBean> childPdupResultList = pdupResultList.stream().filter(childResult -> childPdupOrderTaskIdList.contains(childResult.getOrderTaskId())
                    && ObjectUtils.equals(result.getItemId(), childResult.getItemId())
                    && isEffectiveResult(childResult)).collect(Collectors.toList());

            // 替换ND或HD的值
            childPdupResultList.forEach(this::replaceAvgOrReportResultNdHdValue);

            // 获取现场平行的结果，排除无效的现场平行结果
            List<String> validChildPdupResultList = childPdupResultList.stream().filter(childResult -> NumberUtils.isNumber(childResult.getReportResult())).map(childResult -> NumberUtils.isNumber(childResult.getAvgResult()) ? childResult.getAvgResult() : childResult.getReportResult()).collect(Collectors.toList());

            validChildPdupResultList.add(result.getReportResult());
            validChildPdupResultList.stream().mapToDouble(NumberUtils::parseDouble).average().ifPresent(avgResult -> {
                // 使用NumberUtils.formatNumber(avgResult)先格式化一下，防止数据太小自动变成了科学计数法
                result.setOriginalResult(NumberUtils.formatNumber(avgResult) + "");

                // 对均值原始结果进行修约
                reviseOriginalValue(result, itemRevisionList, itemList);

                finalResultList.add(result);
            });
        });

        return finalResultList;
    }

    /**
     * 计算油烟的总均值，规则如下：
     * 油烟浓度五次分析结果之间，任一结果与最大值比较，若该数据小于最大值的四分之一，则该数据结果无效，不能参与平均值计算；数据取舍后，至少有三个数据参与平均值计算；若数据不符合上述条件，则需重新采样。
     * @param resultList
     * @return
     */
    private String calcLampblackTotalAvgResult(List<EmcOrderTaskResultBean> resultList) {
        List<String> resultValueList = resultList.stream().map(EmcOrderTaskResultBean::getReportResult).collect(Collectors.toList());
        OptionalDouble maxReportResult = resultValueList.stream().filter(NumberUtils::isNumber).mapToDouble(NumberUtils::parseDouble).max();
        if (!maxReportResult.isPresent()) {
            return null;
        }

        // 最大值的四分之一，报出结果大于最大值的四分之一就是有效值
        double quarterValue = ArithUtils.div(maxReportResult.getAsDouble(), 4);
        List<EmcOrderTaskResultBean> validResultList = resultList.stream().filter(result -> NumberUtils.isNumber(result.getReportResult()))
                .filter(result -> NumberUtils.parseDouble(result.getReportResult()) >= quarterValue).collect(Collectors.toList());

        // 对于无效的油烟数据，需要添加标识
        List<Long> validResultIdList = validResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
        resultList.stream().filter(result -> !validResultIdList.contains(result.getId())).forEach(result -> result.setExt$Item("invalidresult", "1"));

        OptionalDouble totalAvgValue = validResultList.stream().map(EmcOrderTaskResultBean::getReportResult).filter(NumberUtils::isNumber).mapToDouble(NumberUtils::parseDouble).average();
        if (totalAvgValue.isPresent()) {
            return totalAvgValue.getAsDouble() + "";
        }

        return null;
    }

    private boolean isEffectiveResult(EmcOrderTaskResultBean result) {
        String finalValue = StringUtils.isEmpty(result.getAvgResult()) ? result.getReportResult() : result.getAvgResult();
        return NumberUtils.isNumber(finalValue) || EmcConstant.ND.equals(finalValue) || EmcConstant.HD.equals(finalValue);
    }

    private void replaceAvgOrReportResultNdHdValue(EmcOrderTaskResultBean result) {
        String finalValue = StringUtils.isEmpty(result.getAvgResult()) ? result.getReportResult() : result.getAvgResult();

        replaceNdHdValue(result, finalValue);
    }

    private void replaceNdHdValue(EmcOrderTaskResultBean result, String finalValue) {
        if (NumberUtils.isNumber(finalValue)) {
            return;
        }

        if (EmcConstant.ND.equals(finalValue) && NumberUtils.isNumber(result.getLowLimit())) {
            result.setReportResult(ArithUtils.div(NumberUtils.parseDouble(result.getLowLimit()), 2) + "");
            return;
        }

        if (EmcConstant.HD.equals(finalValue) && NumberUtils.isNumber(result.getHighLimit())) {
            result.setReportResult(result.getHighLimit());
        }
    }

    /**
     * 对原始值进行修约
     * @param result
     * @param itemRevisionList
     * @param itemList
     */
    private void reviseOriginalValue(EmcOrderTaskResultBean result, List<EmcItemRevisionBean> itemRevisionList, List<EmcItemBean> itemList) {
        // 获取分析项的分段修约规则
        List<EmcItemRevisionBean> filterItemRevisionList = itemRevisionList.stream().filter(f -> ObjectUtils.equals(result.getItemId(), f.getItemId())).collect(Collectors.toList());
        EmcItemBean item = itemList.stream().filter(f -> ObjectUtils.equals(result.getItemId(), f.getId())).findFirst().orElseGet(EmcItemBean::new);
        orderTaskResultService.setReportResultValue(result, item, filterItemRevisionList);

        // 对均值进行判断(使用原始值与检出限比较)
        if (NumberUtils.isNumber(result.getLowLimit()) && NumberUtils.isNumber(result.getOriginalResult())
                && NumberUtils.parseDouble(result.getOriginalResult()) < NumberUtils.parseDouble(result.getLowLimit())) {
            result.setReportResult(EmcConstant.ND);
        }
    }

    protected void copyProperties(EmcReportResultBean reportResult, EmcOrderBean orderBean, EmcOrderTaskResultBean orderTaskResult) {
        // 从样品中取值
        reportResult.setOrderNo(orderBean.getOrderNo());
        reportResult.setSampleBeginDate(orderBean.getSampleBeginDate());
        reportResult.setSampleBeginTime(orderBean.getSampleBeginTime());
        reportResult.setSampleCompleteDate(orderBean.getSampleCompleteDate());
        reportResult.setSampleCompleteTime(orderBean.getSampleCompleteTime());
        reportResult.setMonitorTmplId(orderBean.getMonitorTmplId());
        reportResult.setClientOrderName(orderBean.getClientOrderName());
        reportResult.setSampleId(orderBean.getSampleId());
        reportResult.setOrderDesc(orderBean.getOrderDesc());
        reportResult.setOrderCategory(orderBean.getOrderCategory());
        reportResult.setMonitorPointName(orderBean.getExt$Item("monitorpointname"));
        reportResult.setMonitorPointNo(orderBean.getExt$Item("monitorpointno"));

        // 从样品结果中取值
        reportResult.setFolderId(orderTaskResult.getFolderId());
        reportResult.setOrderId(orderTaskResult.getOrderId());
        reportResult.setProjId(orderTaskResult.getProjId());
        reportResult.setOrderTaskId(orderTaskResult.getOrderTaskId());
        reportResult.setTestMethodId(orderTaskResult.getTestMethodId());
        reportResult.setTestName(orderTaskResult.getExt$Item("testname"));
        reportResult.setMethodId(orderTaskResult.getMethodId());
        reportResult.setMethodName(orderTaskResult.getExt$Item("methodname"));
        reportResult.setMethodVersionId(orderTaskResult.getMethodVersionId());
        reportResult.setItemId(orderTaskResult.getItemId());
        reportResult.setItemOrderNo(orderTaskResult.getItemOrderNo());
        reportResult.setItemName(orderTaskResult.getItemName());
        reportResult.setItemAliasName(orderTaskResult.getItemAliasName());
        reportResult.setRevisionRule(orderTaskResult.getRevisionRule());
        reportResult.setUnitName(orderTaskResult.getUnitName());
        reportResult.setRepeatNo(orderTaskResult.getRepeatNo());
        reportResult.setRepeatQty(orderTaskResult.getRepeatQty());
        reportResult.setCycle(orderTaskResult.getCycle());
        reportResult.setFrequency(orderTaskResult.getFrequency());

        reportResult.setOriginalResult(orderTaskResult.getOriginalResult());
        reportResult.setReportResult(orderTaskResult.getReportResult());

        reportResult.setAnalysisType(orderTaskResult.getAnalysisType());
        reportResult.setLowLimit(orderTaskResult.getLowLimit());
        reportResult.setHighLimit(orderTaskResult.getHighLimit());
        reportResult.setTestId(orderTaskResult.getTestId());
        // 这个地方需要单独处理
        reportResult.setIndexLowLimit(orderTaskResult.getIndexLowLimit());
        reportResult.setIndexHighLimit(orderTaskResult.getIndexHighLimit());
        reportResult.setIndexLimitUnit(orderTaskResult.getIndexLimitUnit());
        reportResult.setReportDisplay(orderTaskResult.getReportDisplay());
        reportResult.setResultStatus(orderTaskResult.getResultStatus());
        reportResult.setRunId(orderTaskResult.getRunId());
        String orderReportResult = orderTaskResult.getReportResult();
        if (orderReportResult != null) {
            if (NumberUtils.isNumber(orderReportResult)) {
                double reportResultNumber = NumberUtils.parseDouble(orderReportResult);
                String indexLowLimit = orderTaskResult.getIndexLowLimit();
                String indexHighLimit = orderTaskResult.getIndexHighLimit();
                boolean overLowLimit = NumberUtils.isNumber(indexLowLimit) && reportResultNumber < NumberUtils.parseDouble(indexLowLimit);
                boolean overHighLimit = NumberUtils.isNumber(indexHighLimit) && reportResultNumber > NumberUtils.parseDouble(indexHighLimit);
                if (overLowLimit || overHighLimit) {
                    // 结果超标
                    reportResult.setExceedStandard("1");
                    if (overLowLimit) {
                        double indexLowLimitNumber = NumberUtils.parseDouble(indexLowLimit);
                        reportResult.setEsAmount(indexLowLimitNumber - reportResultNumber + "");
                        reportResult.setEsMultiple((indexLowLimitNumber - reportResultNumber) / indexLowLimitNumber + "");
                    } else {
                        double indexHighLimitNumber = NumberUtils.parseDouble(indexHighLimit);
                        reportResult.setEsAmount(reportResultNumber - indexHighLimitNumber + "");
                        reportResult.setEsMultiple((reportResultNumber - indexHighLimitNumber) / indexHighLimitNumber + "");
                    }
                } else {
                    reportResult.setExceedStandard("0");
                }
            } else if ("ND".equals(orderReportResult)) {
                reportResult.setExceedStandard("0");
            }
        }

        String indexId = orderTaskResult.getExt$Item("indexid");
        reportResult.setIndexId(NumberUtils.isNumber(indexId) ? NumberUtils.parseLong(indexId) : null);
    }

    @Override
    public void insertByProjectDone(List<Long> projectIdList, boolean viewFlag) {
        insertByProject(projectIdList, viewFlag);
    }

    @Override
    public String pack(RestJsonWrapperBean wrapper) {
        List<Long> idList = wrapper.parseId(Long.class);
        if (idList.isEmpty()) return null;
        List<CoreFileBean> fileList = coreFileService.getDao().selectListByIds(idList);
        return fileList.isEmpty() ? null : pack(fileList, wrapper.getParamValue("fileName"));
    }

    private String pack(List<CoreFileBean> fileList, String fileName) {
        if (StringUtils.isBlank(fileName)) {
            fileName = I18nHelper.getMessage("GIKAM.FILE.PACK.DEFAULT_FILE_NAME");
        }
        FilePathDTO filePath = FilePathDTO.of(FileScope.temp.name(), LocalDateTime.now(), StringUtils.randomUUID(16), fileName + ".zip");
        List<Path> zipPathList = fileList.stream().map(CoreFileUtils::getLocalPath).collect(Collectors.toList());
        ArchiveUtils.zip(FilePathManager.getLocalPath(filePath), zipPathList.toArray(new Path[0]));
        fileManager.upload(filePath, FilePathManager.getLocalPath(filePath));
        return fileManager.getDownloadUrl(filePath);
    }

    @Override
    public String packReportFile(RestJsonWrapperBean wrapper) {
        return pack(wrapper);
    }

}
