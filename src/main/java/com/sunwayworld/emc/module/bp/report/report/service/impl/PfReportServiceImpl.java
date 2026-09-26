package com.sunwayworld.emc.module.bp.report.report.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.ListUtils;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.utils.FileSealUtils;
import com.sunwayworld.emc.framework.word.JSTIeSealHelper;
import com.sunwayworld.emc.framework.word.Word2007Utils;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.auditrecord.bean.EmcAuditRecordBean;
import com.sunwayworld.emc.module.bp.auditrecord.service.EmcAuditRecordService;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.computing.EmcOrderTaskResultComputingEngine;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportLineBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportRecordDTO;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportSendInfoBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateSourceDTO;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportLineService;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportResultService;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportSendInfoService;
import com.sunwayworld.emc.module.bp.report.report.service.PfReportService;
import com.sunwayworld.emc.module.bp.report.report.template.impl.ComplexWordTemplate;
import com.sunwayworld.emc.module.bp.report.report.template.impl.DioxinQcWordTemplate;
import com.sunwayworld.emc.module.bp.report.report.template.impl.DioxinWordTemplate;
import com.sunwayworld.emc.module.bp.report.report.template.impl.DioxinWordTemplate2;
import com.sunwayworld.emc.module.bp.report.report.template.impl.QualityControlWordTemplate;
import com.sunwayworld.emc.module.bp.report.report.validator.EmcViewDocumentValidator;
import com.sunwayworld.emc.module.bp.report.report.validator.PfGenerateReportResultValidator;
import com.sunwayworld.emc.module.bp.report.report.validator.PfViewReportValidator;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcQcRelatedOrderBean;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.result.run.service.EmcQcRelatedOrderService;
import com.sunwayworld.emc.module.bp.result.run.service.EmcRunService;
import com.sunwayworld.emc.module.bp.result.temphum.bean.PfRunTempHumidityBean;
import com.sunwayworld.emc.module.bp.result.temphum.service.PfRunTempHumidityService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.emc.module.erp.service.ErpReportService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyDataService;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.service.EmcSubcontractRecordService;
import com.sunwayworld.emc.module.mdm.basemdm.unit.bean.EmcUnitBean;
import com.sunwayworld.emc.module.mdm.basemdm.unit.bean.EmcUnitConvertBean;
import com.sunwayworld.emc.module.mdm.basemdm.unit.service.EmcUnitConvertService;
import com.sunwayworld.emc.module.mdm.basemdm.unit.service.EmcUnitService;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcIndexCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorSchemeService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorTmplService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemQcIndexBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemRevisionBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemQcIndexService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemRevisionService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestService;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcContractBean;
import com.sunwayworld.emc.module.mdm.contract.service.EmcContractService;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityContractBean;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityMainBean;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityContractService;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityMainService;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityOpenService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckPlanBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptPerCheckPlanService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.executor.ExecutorHelper;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnParameterDTO;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnCommentBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnCommentService;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.service.CoreCodeService;

/**
 * @author zhengxy@sunwayworld.com 2021年06月24日
 */
@Repository
@GikamBean
public class PfReportServiceImpl extends EmcReportServiceImpl implements PfReportService {

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
    private CoreFileService coreFileService;

    @Autowired
    private CoreFileManager fileManager;

    @Autowired
    private EmcPropertyDataService propertyDataService;

    @Autowired
    private EmcEquiptService equiptService;

    @Autowired
    private EmcMonitorTmplService monitorTmplService;

    @Autowired
    private EmcMonitorSchemeService monitorSchemeService;

    @Autowired
    private CoreBpmnCommentService bpmnCommentService;

    @Autowired
    private CoreUserService coreUserService;

    @Autowired
    private CoreOrgService coreOrgService;

    @Autowired
    private CoreCodeService coreCodeService;

    @Autowired
    private EmcSubcontractRecordService subcontractRecordService;

    @Autowired
    @Lazy
    private CrmOpportunityOpenService opportunityOpenService;

    @Autowired
    private EmcItemRevisionService itemRevisionService;

    @Autowired
    private EmcItemService itemService;

    @Autowired
    @Lazy
    private CrmOpportunityMainService opportunityMainService;

    @Autowired
    @Lazy
    private CrmOpportunityContractService opportunityContractService;

    @Autowired
    private PfRunTempHumidityService tempHumidityService;

    @Autowired
    private EmcReportLineService reportLineService;

    @Autowired
    private EmcAuditRecordService auditRecordService;

    @Autowired
    private EmcQcRelatedOrderService qcRelatedOrderService;

    @Autowired
    private EmcIndexCategoryService indexCategoryService;

    @Autowired
    private EmcEquiptPerCheckPlanService perCheckPlanService;

    @Autowired
    private EmcStockService stockService;

    @Autowired
    @Lazy
    private EmcTestService testService;

    @Autowired
    private EmcTestMethodService testMethodService;

    @Autowired
    private ErpReportService erpReportService;

    @Autowired
    private EmcReportSendInfoService reportSendInfoService;

    @Autowired
    private EmcUnitService unitService;

    @Autowired
    private EmcUnitConvertService unitConvertService;

    @Autowired
    private EmcItemQcIndexService itemQcIndexService;

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

        if (ObjectUtils.isEmpty(report) || ObjectUtils.isEmpty(report.getProjId())) {
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

        boolean calcStatistic = Constant.YES.equals(report.getIfStatistics());

        // 计算日均值
        if (calcStatistic) {
            Map<String, List<EmcOrderTaskResultBean>> totalTaskResultMap = finalResultList.stream().collect(Collectors.groupingBy(e -> e.getFolderId() + "$" + e.getItemId() + "$" + e.getExt$Item("testname")));
            totalTaskResultMap.forEach((key, resultList) -> {
                resultList.forEach(result -> replaceNdHdValue(result, result.getReportResult()));

                Map<String, List<EmcOrderTaskResultBean>> dayResultListMap = resultList.stream().collect(Collectors.groupingBy(e -> e.getFolderId() + "$" + e.getCycle()));
                dayResultListMap.forEach((day, dayResultList) -> {
                    List<String> dayResultValueList = dayResultList.stream().map(EmcOrderTaskResultBean::getReportResult).collect(Collectors.toList());

                    dayResultValueList.stream().filter(NumberUtils::isNumber).mapToDouble(NumberUtils::parseDouble).average().ifPresent(avgResult -> {
                        EmcOrderTaskResultBean totalResult = new EmcOrderTaskResultBean();
                        BeanUtils.copyProperties(dayResultList.get(0), totalResult);
                        totalResult.setOriginalResult(NumberUtils.formatNumber(avgResult));

                        // 对均值原始结果进行修约
                        reviseOriginalValue(totalResult, itemRevisionList, itemList);

                        EmcReportResultBean reportResult = new EmcReportResultBean();
                        reportResult.setId(ApplicationContextHelper.getNextIdentity());
                        reportResult.setReportId(report.getId());
                        reportResult.setDataType(EmcConstant.PN_DAYAVERAGE);

                        EmcOrderBean currentOrder = orderIdMap.get(totalResult.getOrderId());
                        copyProperties(reportResult, currentOrder, totalResult);
                        reportResultList.add(reportResult);
                    });
                });
            });
        }

        // 计算总均值
        List<Long> invalidLampblackResultIdList = new ArrayList<>();
        if (calcStatistic) {
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
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void delete(RestJsonWrapperBean wrapper) {
        List<EmcReportBean> reportList = wrapper.parse(EmcReportBean.class);
        List<Long> reportIdList = reportList.stream().map(EmcReportBean::getId).collect(Collectors.toList());
        getDao().deleteByIdList(reportIdList);

        List<EmcReportResultBean> deletedReportResultList = new ArrayList<>(reportIdList.size());
        List<EmcReportLineBean> deletedReportLineList = new ArrayList<>(reportIdList.size());

        reportIdList.forEach(reportId -> {
            EmcReportResultBean reportResult = new EmcReportResultBean();
            reportResult.setReportId(reportId);
            deletedReportResultList.add(reportResult);

            EmcReportLineBean reportLine = new EmcReportLineBean();
            reportLine.setReportId(reportId);
            deletedReportLineList.add(reportLine);
        });

        reportResultService.getDao().deleteBy(deletedReportResultList, "reportId");
        reportLineService.getDao().deleteBy(deletedReportLineList, "reportId");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_START)
    public List<CoreBpmnInstanceStatusDTO<Long>> startProcess(RestJsonWrapperBean wrapper) {
        List<EmcReportBean> selectReportList = wrapper.parse(EmcReportBean.class);
        List<Long> selectReportIdList = selectReportList.stream().map(EmcReportBean::getId).collect(Collectors.toList());
        selectReportList = getDao().selectListByIds(selectReportIdList);
        List<Long> projectIdList = selectReportList.stream().map(EmcReportBean::getProjId).collect(Collectors.toList());

        List<EmcReportBean> projectReportList = getDao().selectListByOneColumnValues(projectIdList, "projId");
        List<EmcReportBean> validReportList = projectReportList.stream().filter(report -> StringUtils.isEmpty(report.getOrderCategory())).collect(Collectors.toList());

        List<Long> reportIdList = validReportList.stream().map(EmcReportBean::getId).collect(Collectors.toList());
        CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
        parameter.setComment("提交");
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = startProcessBasic(reportIdList, parameter);

        List<EmcReportLineBean> reportLineList = reportLineService.getDao().selectListByOneColumnValues(reportIdList, "reportId");
        List<Long> reportOrderTaskIdList = reportLineList.stream().map(EmcReportLineBean::getOrderTaskId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByIds(reportOrderTaskIdList);

        // 根据monitorTmplId和projectId去重
        List<EmcReportBean> distinctReportList = validReportList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getMonitorTmplId() + ";" + o.getProjId()))), ArrayList::new));

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
        validReportList.forEach(report -> report.setStatus(EmcBizConstant.REPORT_STATUS_REPORTAUDIT));
        getDao().update(validReportList, "STATUS");

        // 更新项目状态
        EmcProjectBean updateProject = new EmcProjectBean();
        updateProject.setStatus("reportAudit");
        projectService.getDao().updateByIds(updateProject, projectIdList, "status");

        // 报告管理提交插入审核记录
        this.insertAuditRecordByReport(validReportList, "PROCESS_START", wrapper.getParamValue("bpmn_comment"), null);

        // 向ERP发送报告审核申请
        erpReportService.updateMerchandiser(validReportList);
        erpReportService.uploadOrderReportFile(validReportList);
        erpReportService.uploadOrderDraftReport(validReportList);
        erpReportService.submitOrderReportAuditApply(validReportList);

        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_COMPLETE)
    public List<CoreBpmnInstanceStatusDTO<Long>> completeTask(RestJsonWrapperBean wrapper) {
        List<EmcReportBean> selectReportList = wrapper.parse(EmcReportBean.class);
        List<Long> selectReportIdList = selectReportList.stream().map(EmcReportBean::getId).collect(Collectors.toList());
        selectReportList = getDao().selectListByIds(selectReportIdList);
        List<Long> selectProjectIdList = selectReportList.stream().map(EmcReportBean::getProjId).distinct().collect(Collectors.toList());

        List<EmcReportBean> projectReportList = getDao().selectListByOneColumnValues(selectProjectIdList, "projId");
        List<EmcReportBean> validReportList = projectReportList.stream().filter(report -> StringUtils.isEmpty(report.getOrderCategory())).collect(Collectors.toList());

        List<Long> reportIdList = validReportList.stream().map(EmcReportBean::getId).collect(Collectors.toList());
        CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
        parameter.setComment(wrapper.getParamValue("comment"));
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = completeTask(reportIdList, parameter);

        String statusCode = coreBpmnInstanceStatusDTOList.get(0).getStatusCode();
        List<EmcReportBean> updateReportList = new ArrayList<>();
        validReportList.forEach(e -> {
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

        this.insertAuditRecordByReport(validReportList, "PROCESS_COMPLETE", wrapper.getParamValue("bpmn_comment"), coreBpmnInstanceStatusDTOList);

        // 向ERP发送报告审核结果
        if (EmcBizConstant.REPORT_STATUS_REPORTAUDIT.equals(statusCode)) {
            erpReportService.auditOrderReport(validReportList, false, null);
        } else if (EmcBizConstant.REPORT_STATUS_REPORTISSUE.equals(statusCode)) {
            erpReportService.uploadOrderReportFile(validReportList);
            erpReportService.uploadOrderFinalReport(validReportList);
        } else if (EmcBizConstant.REPORT_STATUS_REPORTSEND.equals(statusCode)) {
            erpReportService.sendOrderReport(validReportList);
        }

        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    public List<CoreBpmnInstanceStatusDTO<Long>> rejectTask(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = rejectTaskBasic(wrapper);

        List<EmcReportBean> selectReportList = wrapper.parse(EmcReportBean.class);
        List<Long> selectReportIdList = selectReportList.stream().map(EmcReportBean::getId).collect(Collectors.toList());
        selectReportList = getDao().selectListByIds(selectReportIdList);
        List<Long> selectProjectIdList = selectReportList.stream().map(EmcReportBean::getProjId).distinct().collect(Collectors.toList());

        List<EmcReportBean> projectReportList = getDao().selectListByOneColumnValues(selectProjectIdList, "projId");
        List<EmcReportBean> validReportList = projectReportList.stream().filter(report -> StringUtils.isEmpty(report.getOrderCategory())).collect(Collectors.toList());

        List<Long> reportIdList = validReportList.stream().map(EmcReportBean::getId).collect(Collectors.toList());
        CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
        parameter.setComment(wrapper.getParamValue("bpmn_comment"));
//        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = rejectTask(reportIdList, parameter);

        List<Long> updateProjectIdList = new ArrayList<>();

        String targetStatusCode = wrapper.getParamMap().get("bpmn_targetStatusCode");
        List<EmcReportBean> updateReportList = new ArrayList<>();
        validReportList.forEach(e -> {
            EmcReportBean updateReport = new EmcReportBean();
            updateReport.setId(e.getId());
            if (StringUtils.startsWithIgnoreCase(targetStatusCode, EmcBizConstant.REPORT_STATUS_DRAFT)) {
                updateReport.setStatus(EmcBizConstant.REPORT_STATUS_REPORTEDIT);
                updateProjectIdList.add(e.getProjId());
            } else {
                updateReport.setStatus(targetStatusCode);
            }
            updateReportList.add(updateReport);
        });
        getDao().update(updateReportList, "STATUS");

        // 报告管理审核插入审核记录
        this.insertAuditRecordByReport(validReportList, "PROCESS_UNDO", wrapper.getParamValue("bpmn_comment"), coreBpmnInstanceStatusDTOList);

        // 更新项目状态
        if (!updateProjectIdList.isEmpty()) {
            EmcProjectBean updateProject = new EmcProjectBean();
            updateProject.setStatus("reportEdit");
            projectService.getDao().updateByIds(updateProject, updateProjectIdList, "status");
        }

        // 如果时报告审核退回，向ERP发送报告审核结果
        String statusCode = coreBpmnInstanceStatusDTOList.get(0).getStatusCode();
        if (EmcBizConstant.REPORT_STATUS_REPORTAUDIT.equals(statusCode)) {
            erpReportService.auditOrderReport(validReportList, true, parameter.getComment());
        }

        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    public Page<EmcOrderTaskBean> selectUndoableAnalysisOrgDataTypePagination(RestJsonWrapperBean wrapper) {
        String reportId = wrapper.getFilterValue("reportId");
        List<EmcReportLineBean> reportLineList = reportLineService.getDao().selectListByOneColumnValue(reportId, "reportId", Arrays.asList("id", "orderTaskId"));
        List<Long> orderTaskIdList = reportLineList.stream().map(EmcReportLineBean::getOrderTaskId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderTaskIdList, "id", Arrays.asList("id", "analysisOrgId", "analysisType", "analysisOrgName"));

        ArrayList<EmcOrderTaskBean> uniqueTaskList = orderTaskList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(task -> String.join("$", task.getAnalysisType(), task.getAnalysisOrgId())))), ArrayList::new));

        return new Pagination<>(uniqueTaskList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void bizUndo(RestJsonWrapperBean wrapper) {
        String reportId = wrapper.getParamValue("reportId");
        if (!NumberUtils.isNumber(reportId)) {
            return;
        }

        String projectId = wrapper.getParamValue("projId");
        String projectNo = wrapper.getParamValue("projNo");
        String refuseReason = wrapper.getParamValue("refuseReason");
        EmcProjectBean projectJ = projectService.selectById(NumberUtils.parseLong(projectId));
        List<EmcOrderTaskBean> undoOrderTaskList;
        String analysisType = wrapper.getParamValue("analysisType");

        // 如果是采样的，直接按科室进行退回，如果是分析的，则按选中的批次进行退回
        if (EmcBizConstant.ANALYSISCATEGORY_SAMPLING.equals(analysisType)) {
            List<EmcOrderTaskBean> orgTaskList = wrapper.parse(EmcOrderTaskBean.class);
            List<String> analysisTypeList = orgTaskList.stream().map(EmcOrderTaskBean::getAnalysisType).collect(Collectors.toList());
            List<String> analysisOrgIdList = orgTaskList.stream().map(EmcOrderTaskBean::getAnalysisOrgId).collect(Collectors.toList());

            List<EmcReportLineBean> reportLineList = reportLineService.getDao().selectListByOneColumnValue(reportId, "reportId", Arrays.asList("id", "orderTaskId"));
            List<Long> orderTaskIdList = reportLineList.stream().map(EmcReportLineBean::getOrderTaskId).distinct().collect(Collectors.toList());
            List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderTaskIdList, "id", Arrays.asList("id", "analysisOrgId", "analysisType", "runId", "orderId", "folderId"));
            undoOrderTaskList = orderTaskList.stream().filter(task -> analysisTypeList.contains(task.getAnalysisType()) && analysisOrgIdList.contains(task.getAnalysisOrgId())).collect(Collectors.toList());
        } else {
            List<EmcRunBean> runList = wrapper.parse(EmcRunBean.class);
            List<String> runIdList = runList.stream().filter(r ->!StringUtils.isEmpty(r.getId())).map(EmcRunBean::getId).collect(Collectors.toList());
            undoOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(runIdList, "runId", Arrays.asList("id", "analysisOrgId", "analysisType", "runId", "orderId", "folderId"));
        }

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

        List<Long> samplingOrderIdList = samplingTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> samplingOrderList = validUndoOrderList.stream().filter(order -> samplingOrderIdList.contains(order.getId())).collect(Collectors.toList());
        List<Long> sampleIdList = samplingOrderList.stream().map(EmcOrderBean::getSampleId).distinct().collect(Collectors.toList());

        // 把采样单工作流进行回退，状态更改为logged
        CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
        parameter.setComment(refuseReason);
        sampleService.undo(sampleIdList, parameter);

        EmcSampleBean updateSample = new EmcSampleBean();
        updateSample.setStatus(EmcBizConstant.SAMPLE_STATUS_LOGGED);
        sampleService.getDao().updateByIds(updateSample, sampleIdList, "STATUS");

        // 更新测试批的状态
        List<String> runIdList = analysisTaskList.stream().map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());
        EmcRunBean updateRun = new EmcRunBean();
        updateRun.setStatus(EmcBizConstant.RUN_STATUS_AUDIT);
        updateRun.setAuditedUserId("");
        updateRun.setAuditedUserName("");
        updateRun.setAuditedDate(null);
        updateRun.setUndoReason(refuseReason);
        runService.getDao().updateByIds(updateRun, runIdList, "STATUS","auditedUserId","auditedUserName","auditedDate", "undoReason");
        //查询项目下所有ordertask
        List<EmcOrderTaskBean> projOrderTaskList = orderTaskService.getDao().selectListByOneColumnValue(projectJ.getId(),"PROJID");
        List<String> allRunIdList = projOrderTaskList.stream().map(EmcOrderTaskBean :: getRunId).filter(r ->!StringUtils.isEmpty(r)).collect(Collectors.toList());
        //项目下所有的批
        List<EmcRunBean> allRunList = runService.selectListByIds(allRunIdList);
        List<String> allRunStatusList = allRunList.stream().map(EmcRunBean ::getStatus).distinct().collect(Collectors.toList());
        // 更新点位状态
        //点位状态除非改点位下所有样品的测试状态都是draft才更新点位状态
        List<Long> folderIdList = undoOrderTaskList.stream().map(EmcOrderTaskBean::getFolderId).distinct().collect(Collectors.toList());
        List<Long> folderIdForUpdateList = new ArrayList<>();
        folderIdList.forEach(fi ->{
            //单个点位下的测试
            List<EmcOrderTaskBean> folderTaskList = projOrderTaskList.stream().filter(t ->ObjectUtils.equals(fi,t.getFolderId())).collect(Collectors.toList());
            //单点位下测试的状态
            List<String> statusList = folderTaskList.stream().map(EmcOrderTaskBean :: getStatus).distinct().filter(t ->!StringUtils.isEmpty(t)).collect(Collectors.toList());
            //分析退回为audit,采样退回为logged
            //只有单点位所有测试状态不为done,这个点位状态才可以更新
            if(!statusList.contains("done")){
                folderIdForUpdateList.add(fi);
            }
        });

        EmcFolderBean updateFolder = new EmcFolderBean();
        updateFolder.setStatus(EmcBizConstant.FOLDER_STATUS_DRAW);
        folderService.getDao().updateByIds(updateFolder, folderIdForUpdateList, "STATUS");

        // 删除报告及报告数据
        EmcReportResultBean deleteReportResult = new EmcReportResultBean();
        deleteReportResult.setReportId(NumberUtils.parseLong(reportId));

        EmcReportLineBean deleteReportLine = new EmcReportLineBean();
        deleteReportLine.setReportId(NumberUtils.parseLong(reportId));
        getDao().delete(NumberUtils.parseLong(reportId));
        reportResultService.getDao().deleteBy(deleteReportResult, "reportId");
        reportLineService.getDao().deleteBy(deleteReportLine, "reportId");

        // 更新项目状态，如果该项目下没有其他报告
        List<EmcReportBean> reportList = getDao().selectListByOneColumnValue(projectId, "projId", Arrays.asList("id", "status"));
        List<EmcReportBean> otherValidReportList = reportList.stream().filter(r -> !StringUtils.equals(r.getStatus(), "draft") && !ObjectUtils.equals(r.getId(), NumberUtils.parseLong(reportId))).collect(Collectors.toList());
        if (otherValidReportList.isEmpty()) {
            //如果项目下的所有批都没提交或退回，改变项目状态
            if(!allRunStatusList.contains("done")){
                EmcProjectBean updateProject = new EmcProjectBean();
                updateProject.setId(NumberUtils.parseLong(projectId));
                updateProject.setStatus(EmcBizConstant.PROJECT_STATUS_AUDITFINISHED);
                projectService.getDao().update(updateProject, "status");
            }
        }

        // 报告管理退回插入审核记录
        EmcAuditRecordBean auditRecord = new EmcAuditRecordBean();
        auditRecord.setProjId(NumberUtils.parseLong(projectId));
        auditRecord.setProjNo(projectNo);
        auditRecord.setComments(refuseReason);
        auditRecord.setLogDesc("项目:" + projectNo);
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
        EmcProjectBean project = projectService.selectFirstByFilter(SearchFilter.instance().match("id", projectId).filter(MatchPattern.SEQ));

        /*String reportType = wrapper.getParamValue("reportType");*/

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
            List<CoreFileBean> newCoreFileList = saveWordReportCoreFile(wordTemplateConfigFile.getName(), templateFile, reportList, EmcConstant.WORD_REPORT_ATTACHMENT_TYPE);

            return newCoreFileList.get(0).getId();
        }

        Long reportConfigLineId = report.getReportConfigLineId();
        /*if (StringUtils.equals(reportType, "qcReport") && NumberUtils.isNumber(report.getExt$Item("qcReportLineId"))) {
            reportConfigLineId = NumberUtils.parseLong(report.getExt$Item("qcReportLineId"));
        }*/

        List<CoreFileBean> wordTemplateConfigFileList = coreFileService.getDao().selectListByOneColumnValue("T_CORE_REPORT_CONFIG_LINE$" + reportConfigLineId, "targetId");
        if (!wordTemplateConfigFileList.isEmpty()) {
            CoreFileBean wordTemplateConfigFile = wordTemplateConfigFileList.get(0);

            String wordReportFileName = FileUtils.getFileBaseName(wordTemplateConfigFile.getName()) + "-" + System.currentTimeMillis() + "." + FileUtils.getFileExtension(wordTemplateConfigFile.getName());
            File templateFile = FilePathManager.getLocalPath(FilePathDTO.of(wordTemplateConfigFile.getScope(), "",
                    wordTemplateConfigFile.getExt(), wordTemplateConfigFile.getName())).toFile();
            File wordReportFile = FilePathManager.getLocalPath(FilePathDTO.of(wordTemplateConfigFile.getScope(), "",
                    wordTemplateConfigFile.getExt(), wordReportFileName)).toFile();

            generateWordReport(templateFile, wordReportFile, report, reportList, project, false);
            List<CoreFileBean> newCoreFileList = saveWordReportCoreFile(wordReportFileName, wordReportFile, reportList, EmcConstant.WORD_REPORT_ATTACHMENT_TYPE);
            return newCoreFileList.get(0).getId();
        }

        return 0L;
    }

    @Override
    public void generateWordReport(File templateFile, File wordReportFile, EmcReportBean report, List<EmcReportBean> reportList, EmcProjectBean project, boolean exist) {
        try (FileInputStream inputStream = new FileInputStream(templateFile);
             XWPFDocument document = new XWPFDocument(inputStream);
             FileOutputStream outputStream = new FileOutputStream(wordReportFile)) {

            if (!exist) {
                writeDataToWord(templateFile, document, report, project);
            }

            setSubmitInformation(templateFile, document, report);

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
    @Override
    public void writeDataToWord(File templateFile, XWPFDocument document, EmcReportBean report, EmcProjectBean project) {
        // 根据项目ID查询所有未终止的点位列表，包含正常点位、现场空白和现场平行
        List<EmcFolderBean> allFolderList = folderService.selectListByFilter(SearchFilter.instance().match("projId",
                project.getId()).filter(MatchPattern.EQ).match("termination", "0").filter(MatchPattern.EQ));
        List<Long> allFolderIdList = allFolderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());

        // 根据项目ID查询采样单，主要用于获取采样设备
        List<EmcSampleBean> sampleList = sampleService.getDao().selectListByOneColumnValue(project.getId(), "PROJID", Arrays.asList("ID", "SAMPLINGEQUIPTID", "SAMPLINGEQUIPTNAME", "ACCEPTEDDATE", "ACCEPTEUSERNAME","ORDERCATEGORY"));

        // 根据点位查询所有的正常样品列表(未终止的)，即QC为NA的样品，排除现场空白和现场平行的样品
        List<EmcOrderBean> orderList = orderService.selectListByFilter(SearchFilter.instance()
                .match("folderId", allFolderIdList).filter(MatchPattern.OR)
                .match("termination", "0").filter(MatchPattern.EQ)
                .match("QCCATEGORYCODE", EmcConstant.N_A).filter(MatchPattern.EQ));

        // 根据点位查询所有的样品测试列表
        List<Long> folderIdList = orderList.stream().map(EmcOrderBean::getFolderId).distinct().collect(Collectors.toList());
        List<EmcFolderBean> folderList = allFolderList.stream().filter(f -> folderIdList.contains(f.getId())).collect(Collectors.toList());
        //去除采样前分包
        List<EmcOrderTaskBean> orderTaskBeforeList = orderTaskService.selectListByFilter(SearchFilter.instance().match("folderId", folderIdList).filter(MatchPattern.OR));
        List<EmcOrderTaskBean> orderTaskList = orderTaskBeforeList.stream().filter(ok ->StringUtils.isEmpty(ok.getExt$Item("samplingsubpackage"))).collect(Collectors.toList());
        List<Long> filterOrderTaskIdList = orderTaskList.stream().map(EmcOrderTaskBean :: getId).distinct().collect(Collectors.toList());
        // 根据点位查询样品测试结果列表，用于获取作为参数的测试结果
        //去除采样前分包
        List<EmcOrderTaskResultBean> orderTaskResultBeforeList = orderTaskResultService.getDao().selectListByOneColumnValues(folderIdList, "FOLDERID", Arrays.asList("ID", "FOLDERID", "ORDERID", "ORDERNO", "ITEMID", "ITEMALIASNAME", "REPORTRESULT", "REPORTDISPLAY", "LOWLIMIT", "UNITNAME"));
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultBeforeList.stream().filter(res ->filterOrderTaskIdList.contains(res.getOrderTaskId())).collect(Collectors.toList());

        // 质控样品测试结果(质控样不会有分包)
        List<String> runIdList = orderTaskList.stream().filter(a -> !StringUtils.isEmpty(a.getRunId())).map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> qcOrderTaskBeforeList = runIdList.isEmpty() ? Collections.emptyList() : orderTaskService.selectListByFilter(SearchFilter.instance().match("runId", runIdList).filter(MatchPattern.OR).match("qcCategoryCode", EmcConstant.N_A).filter(MatchPattern.DIFFER));

        List<Long> qcOrderTaskIdList = qcOrderTaskBeforeList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> qcTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(qcOrderTaskIdList, "orderTaskId");
        List<Long> existQcOrderTaskId = qcTaskResultList.stream().map(EmcOrderTaskResultBean ::getOrderTaskId).distinct().collect(Collectors.toList());
        //排除结果表不存在数据的测试
        List<EmcOrderTaskBean> qcOrderTaskList = qcOrderTaskBeforeList.stream().filter(ok ->existQcOrderTaskId.contains(ok.getId())).collect(Collectors.toList());
        List<Long> qcOrderIdList = qcOrderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> qcOrderList = orderService.getDao().selectListByIds(qcOrderIdList);

        // 查询样品测试分析项组、分析项组对应的分析项
        List<Long> testMethodIdList = orderTaskList.stream().filter(task -> !ObjectUtils.isEmpty(task.getTestMethodId())).map(EmcOrderTaskBean::getTestMethodId).distinct().collect(Collectors.toList());
        List<EmcItemGroupBean> itemGroupList = itemGroupService.getDao().selectListByOneColumnValues(testMethodIdList, "testMethodId", Arrays.asList("ID", "TESTMETHODID", "ITEMGROUP"));
        List<Long> itemGroupIdList = itemGroupList.stream().filter(group -> Constant.YES.equals(group.getActivatedFlag())).map(EmcItemGroupBean::getId).collect(Collectors.toList());
        List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID", Arrays.asList("ITEMGROUPID", "ITEMID"));
        Map<Long, String> itemGroupMap = itemGroupList.stream().filter(g -> !StringUtils.isEmpty(g.getItemGroup())).collect(Collectors.toMap(EmcItemGroupBean::getId, EmcItemGroupBean::getItemGroup, (v1, v2) -> v1));

        // 获取所有样品测试中的批号，并查询批列表
        List<EmcRunBean> runList = new ArrayList<>();
        if (!runIdList.isEmpty()) {
            runList = runService.selectListByFilter(SearchFilter.instance().match("Id", runIdList).filter(MatchPattern.OR));
        }

        // 查询需要在报告中显示的报告结果列表，此处只查询样品值、日均值、总均值结果
        List<String> dataTypeList = Arrays.asList(EmcConstant.PN_SAMPLE, EmcConstant.PN_DAYAVERAGE, EmcConstant.PN_TOTALAVERAGE);
        List<EmcReportResultBean> reportResultBeforeList = reportResultService.selectListByFilter(SearchFilter.instance().match("folderId", folderIdList).filter(MatchPattern.OR)
                .match("reportDisplay", "1").filter(MatchPattern.EQ)
                .match("DATATYPE", dataTypeList).filter(MatchPattern.OR));
        //去除采样前分包
        List<EmcReportResultBean> reportResultList = reportResultBeforeList.stream().filter(res ->filterOrderTaskIdList.contains(res.getOrderTaskId())).collect(Collectors.toList());
        // 设置正常样结果分析项组
        reportResultList.stream().filter(r -> Constant.YES.equals(r.getReportDisplay())).forEach(r -> {
            groupItemList.stream().filter(g -> ObjectUtils.equals(r.getItemId(), g.getItemId())).findFirst().ifPresent(g -> r.setExt$Item("itemGroup", itemGroupMap.get(g.getItemGroupId())));
        });

        // 根据监测模板ID查询监测模板，通过监测模板组ID来区分水、气、土、声
        List<String> monitorTmplIdList = orderList.stream().filter(f -> !StringUtils.isEmpty(f.getMonitorTmplId())).map(EmcOrderBean::getMonitorTmplId).distinct().collect(Collectors.toList());
        List<EmcMonitorTmplBean> monitorTmplList = monitorTmplService.getDao().selectListByIds(monitorTmplIdList);

        // 通过采样单来查询采样单属性
        List<Long> sampleIdList = reportResultList.stream().map(EmcReportResultBean::getSampleId).distinct().collect(Collectors.toList());
        List<EmcPropertyDataBean> propertyDataList = propertyDataService.getDao().selectListByOneColumnValues(sampleIdList, "bizId");
        // 查询采样单对应的点位示意图
        List<String> targetIdList = sampleIdList.stream().map(i -> "T_EMC_SAMPLE$" + i).collect(Collectors.toList());
        List<CoreFileBean> coreFileList = Collections.emptyList();
        if (!targetIdList.isEmpty()) {
            coreFileList = coreFileService.selectListByFilter(SearchFilter.instance().match("TARGETID", targetIdList).filter(MatchPattern.OR).match("BIZCATEGORY", "pointDiagram").filter(MatchPattern.EQ));
        }

        // 查询当前项目下的所有报告数据
        List<EmcReportBean> reportList = getDao().selectListByOneColumnValue(project.getId(), "PROJID");

        // 查询采样设备
        List<String> sampleEquipmentIdList = orderTaskList.stream().filter(task -> !StringUtils.isEmpty(task.getSamplingEquiptId())).map(EmcOrderTaskBean::getSamplingEquiptId).collect(Collectors.toList());
        List<Long> equiptIdList = orderTaskList.stream().filter(task -> !ObjectUtils.isEmpty(task.getEquiptId())).map(EmcOrderTaskBean::getEquiptId).collect(Collectors.toList());
        List<Long> parsedEquiptIdList = parseMultiEquipt(sampleEquipmentIdList);
        parsedEquiptIdList.addAll(equiptIdList);
        // 称重设备
        List<String> weighingEquiptIdList = runList.stream().filter(run -> !StringUtils.isEmpty(run.getExt$Item("weighingEquiptId"))).map(run -> run.getExt$Item("weighingEquiptId")).distinct().collect(Collectors.toList());
        parsedEquiptIdList.addAll(parseMultiEquipt(weighingEquiptIdList));
        List<EmcEquiptBean> equiptList = equiptService.getDao().selectListByIds(parsedEquiptIdList, Arrays.asList("ID", "EQUIPTID", "EQUIPTNAME", "SPECIFICATION"));

        // 公司信息
        CoreUserBean loginUser = LocalContextHelper.getLoginUser();
        String parentOrgId = loginUser.getExt$Item("parentOrgId");
        CoreOrgBean coreOrg = coreOrgService.selectById(parentOrgId);

        // 编码信息
        List<String> codeCategoryList = Collections.singletonList("testitemsort");
        List<CoreCodeBean> coreCodeList = coreCodeService.getDao().selectListByOneColumnValues(codeCategoryList, "codeCategoryId", Arrays.asList("id", "codeName", "codeCategoryId", "code"));

        WordTemplateSourceDTO templateSourceDTO = new WordTemplateSourceDTO();
        templateSourceDTO.setProject(project);
        templateSourceDTO.setFolderList(folderList);
        templateSourceDTO.setSampleList(sampleList);
        templateSourceDTO.setOrderList(orderList);
        templateSourceDTO.setQcOrderList(qcOrderList);
        templateSourceDTO.setOrderTaskList(orderTaskList);
        templateSourceDTO.setQcOrderTaskList(qcOrderTaskList);
        templateSourceDTO.setOrderTaskResultList(orderTaskResultList);
        templateSourceDTO.setQcTaskResultList(qcTaskResultList);
        templateSourceDTO.setItemGroupList(itemGroupList);
        templateSourceDTO.setGroupItemList(groupItemList);
        templateSourceDTO.setRunList(runList);
        templateSourceDTO.setReport(report);
        templateSourceDTO.setReportList(reportList);
        templateSourceDTO.setReportResultList(reportResultList);
        templateSourceDTO.setMonitorTmplList(monitorTmplList);
        templateSourceDTO.setPropertyDataList(propertyDataList);
        templateSourceDTO.setEquiptList(equiptList);
        templateSourceDTO.setCoreFileList(coreFileList);
        templateSourceDTO.setCoreOrg(coreOrg);
        templateSourceDTO.setCoreCodeList(coreCodeList);

        switch (templateFile.getName()) {
            case PfConstant.COMPLEX_WORD_REPORT_TEMPLATE:
                if (!Constant.YES.equals(project.getSample())) {
                    templateSourceDTO.getFolderList().forEach(folder -> templateSourceDTO.getOrderList().stream().filter(order -> ObjectUtils.equals(order.getFolderId(), folder.getId())).findFirst().ifPresent(order -> folder.setMonitorTmplId(order.getMonitorTmplId())));
                }
                ComplexWordTemplate complexWordTemplate = new ComplexWordTemplate(document, templateSourceDTO);
                complexWordTemplate.generate();
                break;
            case PfConstant.QC_WORD_REPORT_TEMPLATE:
                QualityControlWordTemplate qualityControlWordTemplate = new QualityControlWordTemplate(document, templateSourceDTO);
                qualityControlWordTemplate.generate();
                break;
            case PfConstant.DIOXIN_WORD_REPORT_TEMPLATE:
                DioxinWordTemplate dioxinWordTemplate = new DioxinWordTemplate(document, templateSourceDTO);
                dioxinWordTemplate.generate();
                break;
            default:
                break;
        }
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
    public List<CoreFileBean> saveWordReportCoreFile(String wordReportFileName, File wordReportFile, List<EmcReportBean> reportList, String bizCategory) {
        List<CoreFileBean> insertCoreFileList = new ArrayList<>();
        CoreFileBean copyFile = new CoreFileBean();
        copyFile.setName(wordReportFileName);
        copyFile.setFileExt(FileUtils.getFileExtension(wordReportFileName));
        copyFile.setScope(FileScope.export.name());
        copyFile.setTimes(0L);
        copyFile.setDownloadable(Constant.YES);
        copyFile.setCreatedTime(LocalDateTime.now());
        copyFile.setBizCategory(bizCategory);
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
    private void setSubmitInformation(File templateFile, XWPFDocument document, EmcReportBean report) {
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
        XWPFTable lastTable = tableList.get(1);

        if (submitComment != null) {
            addSignaturePicture(lastTable, 27, submitComment, null);
        }

        if (auditComment != null) {
            addSignaturePicture(lastTable, 29, auditComment, null);
        }

        if (issueComment != null) {
            addSignaturePicture(lastTable, 31, issueComment, null);
            LocalDateTime issueDate = issueComment.getCreatedTime();
            Word2007Utils.insertContentAndImageToTable(lastTable, 34, null, String.format(PfConstant.REPORT_ISSUE_DATE, issueDate.getYear(), issueDate.getMonth().getValue(), issueDate.getDayOfMonth()));
        }

    }

    private void setSubmitInformation(File templateFile, XWPFDocument document, EmcReportBean report, String reportType) {
        List<CoreBpmnCommentBean> commentList = bpmnCommentService.selectListByFilter(SearchFilter.instance().match("targetId", getDao().getTable()
                + "$" + report.getId()).filter(MatchPattern.EQ).match("status", "REJECT").filter(MatchPattern.DIFFER));
        CoreBpmnCommentBean submitComment = commentList.stream().filter(e -> ObjectUtils.equals("Draft", e.getStatusCode()))
                .sorted(Comparator.comparing(CoreBpmnCommentBean::getCreatedTime).reversed()).limit(1).findFirst().orElse(null);
        CoreBpmnCommentBean auditComment = commentList.stream().filter(e -> ObjectUtils.equals("reportAudit", e.getStatusCode()))
                .sorted(Comparator.comparing(CoreBpmnCommentBean::getCreatedTime).reversed()).limit(1).findFirst().orElse(null);
        CoreBpmnCommentBean issueComment = commentList.stream().filter(e -> ObjectUtils.equals("reportIssue", e.getStatusCode()))
                .sorted(Comparator.comparing(CoreBpmnCommentBean::getCreatedTime).reversed()).limit(1).findFirst().orElse(null);

        if (PfConstant.WORD_REPORT_ATTACHMENT_TYPE.equals(reportType) && StringUtils.contains(templateFile.getName(), PfConstant.DIOXIN_KEYWORD)) {
            signDioXinCommonReport(document, submitComment, auditComment, issueComment);
            return;
        }

        if (PfConstant.WORD_REPORT_ATTACHMENT_TYPE.equals(reportType)) {
            signCommonReport(document, submitComment, auditComment, issueComment);
            return;
        }

        if (PfConstant.QC_WORD_REPORT_ATTACHMENT_TYPE.equals(reportType) && StringUtils.contains(templateFile.getName(), PfConstant.DIOXIN_KEYWORD)) {
            signDioxinQcReport(document, submitComment, auditComment, issueComment);
            return;
        }

        if (PfConstant.QC_WORD_REPORT_ATTACHMENT_TYPE.equals(reportType)) {
            signCommonQcReport(document, submitComment, auditComment, issueComment);
        }
    }

    private void signDioXinCommonReport(XWPFDocument document, CoreBpmnCommentBean submitComment, CoreBpmnCommentBean auditComment, CoreBpmnCommentBean issueComment) {
        List<XWPFTable> tableList = document.getTables();
        XWPFTable lastTable = tableList.get(2);

        if (submitComment != null) {
            addSignaturePicture(lastTable, 2, submitComment, null);
        }

        if (auditComment != null) {
            addSignaturePicture(lastTable, 4, auditComment, null);
        }

        if (issueComment != null) {
            addSignaturePicture(lastTable, 6, issueComment, null);
            LocalDateTime issueDate = issueComment.getCreatedTime();
            Word2007Utils.insertContentAndImageToTable(lastTable, 8, null, DateTimeUtils.formatLocalDateTime(issueDate, "yyy-MM-dd"));
        }
    }

    /**
     * 常规通用基本信息模板签名
     * @param document
     * @param submitComment
     * @param auditComment
     * @param issueComment
     */
    private void signCommonReport(XWPFDocument document, CoreBpmnCommentBean submitComment, CoreBpmnCommentBean auditComment, CoreBpmnCommentBean issueComment) {
        List<XWPFTable> tableList = document.getTables();
        XWPFTable lastTable = tableList.get(1);

        if (submitComment != null) {
            addSignaturePicture(lastTable, 29, submitComment, null);
        }

        if (auditComment != null) {
            addSignaturePicture(lastTable, 31, auditComment, null);
        }

        if (issueComment != null) {
            addSignaturePicture(lastTable, 33, issueComment, null);
            LocalDateTime issueDate = issueComment.getCreatedTime();
            Word2007Utils.insertContentAndImageToTable(lastTable, 35, null, String.format(PfConstant.REPORT_ISSUE_DATE, issueDate.getYear(), issueDate.getMonth().getValue(), issueDate.getDayOfMonth()));
        }
    }

    private void signCommonQcReport(XWPFDocument document, CoreBpmnCommentBean submitComment, CoreBpmnCommentBean auditComment, CoreBpmnCommentBean issueComment) {
        List<XWPFTable> tableList = document.getTables();
        XWPFTable signTable = tableList.get(1);

        if (submitComment != null) {
            addSignaturePicture(signTable, 2, submitComment, null);
        }

        if (auditComment != null) {
            addSignaturePicture(signTable, 4, auditComment, null);
        }

        if (issueComment != null) {
            addSignaturePicture(signTable, 6, issueComment, null);
            LocalDateTime createdTime = issueComment.getCreatedTime();
            Word2007Utils.insertContentAndImageToTable(signTable, 8, null, DateTimeUtils.formatLocalDateTime(createdTime, "yyy-MM-dd"));
        }
    }

    private void signDioxinQcReport(XWPFDocument document, CoreBpmnCommentBean submitComment, CoreBpmnCommentBean auditComment, CoreBpmnCommentBean issueComment) {
        List<XWPFTable> tableList = document.getTables();
        XWPFTable signTable = tableList.get(1);

        if (submitComment != null) {
            addSignaturePicture(signTable, 2, submitComment, null);
        }

        if (auditComment != null) {
            addSignaturePicture(signTable, 4, auditComment, null);
        }

        if (issueComment != null) {
            addSignaturePicture(signTable, 6, issueComment, null);
            LocalDateTime createdTime = issueComment.getCreatedTime();
            Word2007Utils.insertContentAndImageToTable(signTable, 8, null, DateTimeUtils.formatLocalDateTime(createdTime, "yyy-MM-dd"));
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
     * 添加水印
     * @param reportList 报告列表
     */
    private void addWatermarkToReport(List<EmcReportBean> reportList) {
        Map<Long, List<EmcReportBean>> projectReportMap = reportList.stream().collect(Collectors.groupingBy(EmcReportBean::getProjId));
        List<EmcReportBean> projectReportList = projectReportMap.values().stream().map(list -> list.get(0)).collect(Collectors.toList());
        List<String> targetIdList = projectReportList.stream().map(report -> "T_EMC_REPORT$" + report.getId()).collect(Collectors.toList());

        List<CoreFileBean> reportFileList = coreFileService.selectListByFilter(SearchFilter.instance().match("targetId",
                targetIdList).filter(MatchPattern.OR).match("bizCategory", Arrays.asList(EmcConstant.WORD_REPORT_ATTACHMENT_TYPE, PfConstant.WATERMARK_WORD_REPORT_ATTACHMENT_TYPE)).filter(MatchPattern.OR));

        // 如果之前存在水印报告，则删除
        List<CoreFileBean> watermarkReportList = reportFileList.stream().filter(file -> StringUtils.equals(file.getBizCategory(), PfConstant.WATERMARK_WORD_REPORT_ATTACHMENT_TYPE)).collect(Collectors.toList());
        if (!watermarkReportList.isEmpty()) {
            watermarkReportList.forEach(file -> file.setTargetId("X" + file.getTargetId()));
            coreFileService.getDao().update(watermarkReportList, "TARGETID");
        }

        List<EmcProjectBean> projectList = projectService.selectListByIds(new ArrayList<>(projectReportMap.keySet()));
        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
        CoreOrgBean parentOrg = coreOrgService.selectById(parentOrgId);
        Map<Long, String> projectIdNoMap = projectList.stream().collect(Collectors.toMap(EmcProjectBean::getId, EmcProjectBean::getProjNo));

        List<CoreFileBean> wordFileList = reportFileList.stream().filter(file -> StringUtils.equals(file.getBizCategory(), EmcConstant.WORD_REPORT_ATTACHMENT_TYPE) && StringUtils.equals(file.getFileExt(), "docx")).collect(Collectors.toList());

        projectReportMap.forEach((projectId, curReportList) -> {
            wordFileList.stream().filter(file -> StringUtils.equals(file.getTargetId(), "T_EMC_REPORT$" + curReportList.get(0).getId())).findFirst().ifPresent(file -> {
                File wordFile = new File(FilePathManager.getLocalPath(CoreFileUtils.toFilePath(file)).toString());
                String fileName = file.getName().substring(0, file.getName().indexOf("-") + 1) + System.currentTimeMillis() + ".pdf";
                File watermarkFile = JSTIeSealHelper.addWatermark(wordFile, projectId, projectIdNoMap.get(projectId), fileName, parentOrg.getOrgName());
                saveWordReportCoreFile(watermarkFile.getName(), watermarkFile, curReportList, PfConstant.WATERMARK_WORD_REPORT_ATTACHMENT_TYPE);
            });
        });

    }

    /**
     * 添加电子签章
     * @param reportList 报告列表
     */
    private void addStampToReport(List<EmcReportBean> reportList) {
        Map<Long, List<EmcReportBean>> projectReportMap = reportList.stream().collect(Collectors.groupingBy(EmcReportBean::getProjId));
        List<EmcReportBean> projectReportList = projectReportMap.values().stream().map(list -> list.get(0)).collect(Collectors.toList());
        List<String> targetIdList = projectReportList.stream().map(report -> "T_EMC_REPORT$" + report.getId()).collect(Collectors.toList());

        List<CoreFileBean> reportFileList = coreFileService.selectListByFilter(SearchFilter.instance().match("targetId",
                targetIdList).filter(MatchPattern.OR).match("bizCategory", Arrays.asList(EmcConstant.WORD_REPORT_ATTACHMENT_TYPE, PfConstant.STAMPED_WORD_REPORT_ATTACHMENT_TYPE)).filter(MatchPattern.OR));

        // 如果之前存在盖章报告，则删除
        List<CoreFileBean> stampedReportList = reportFileList.stream().filter(file -> StringUtils.equals(file.getBizCategory(), PfConstant.STAMPED_WORD_REPORT_ATTACHMENT_TYPE)).collect(Collectors.toList());
        if (!stampedReportList.isEmpty()) {
            stampedReportList.forEach(file -> file.setTargetId("X" + file.getTargetId()));
            coreFileService.getDao().update(stampedReportList, "TARGETID");
        }

        List<EmcProjectBean> projectList = projectService.selectListByIds(new ArrayList<>(projectReportMap.keySet()));
        Map<Long, String> projectIdNoMap = projectList.stream().collect(Collectors.toMap(EmcProjectBean::getId, EmcProjectBean::getProjNo));

        List<CoreFileBean> wordFileList = reportFileList.stream().filter(file -> StringUtils.equals(file.getBizCategory(), EmcConstant.WORD_REPORT_ATTACHMENT_TYPE) && StringUtils.equals(file.getFileExt(), "docx")).collect(Collectors.toList());

        projectReportMap.forEach((projectId, curReportList) -> {
            wordFileList.stream().filter(file -> StringUtils.equals(file.getTargetId(), "T_EMC_REPORT$" + curReportList.get(0).getId())).findFirst().ifPresent(file -> {
                File wordFile = new File(FilePathManager.getLocalPath(CoreFileUtils.toFilePath(file)).toString());
                String fileName = file.getName().substring(0, file.getName().indexOf("-") + 1) + System.currentTimeMillis() + ".pdf";
                File stampedFile = JSTIeSealHelper.sealFile(wordFile, projectId + "", projectIdNoMap.get(projectId), fileName);
                saveWordReportCoreFile(stampedFile.getName(), stampedFile, curReportList, PfConstant.STAMPED_WORD_REPORT_ATTACHMENT_TYPE);
            });
        });
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public String completeReport(RestJsonWrapperBean wrapper) {
        Long projId = Long.parseLong(wrapper.getFilterValue("projId"));
        String deliveryDate = wrapper.getFilterValue("deliverydate");
        String completeDate = wrapper.getFilterValue("completedate");
        List<EmcReportBean> updateReportList = new ArrayList<>();
        List<EmcReportBean> updateReportDoneList = new ArrayList<>();
        List<EmcProjectBean> projList = projectService.selectListByFilter(SearchFilter.instance().match("id", projId).filter(MatchPattern.OR));
        String projNo = projList.get(0).getProjNo();
        //判断是否已经点过报告完成
        List<EmcReportBean> isExistList = selectListByFilter(SearchFilter.instance().match("projId",projId).filter(MatchPattern.OR));
        List<EmcReportBean> isDoneList = selectListByFilter(SearchFilter.instance().match("projId",projId).filter(MatchPattern.OR).match("COMPLETESTATUS","done").filter(MatchPattern.OR));
        String isDone = null;
        if(!isExistList.isEmpty()){
            //已完成的报表后续只修改状态不调用核销接口
            if((isDoneList).size()>=1){
                isDone = "done";
                isExistList.forEach(e -> {
                    EmcReportBean updateReport = new EmcReportBean();
                    updateReport.setId(e.getId());
                    updateReport.setExt$Item("DELIVERYDATE",deliveryDate);
                    updateReport.setExt$Item("COMPLETEDATE", completeDate);
                    updateReport.setReportNo(projNo);
                    updateReport.setExt$Item("COMPLETESTATUS","done");
                    updateReportDoneList.add(updateReport);
                });
                getDao().update(updateReportDoneList,"DELIVERYDATE","COMPLETEDATE","REPORTNO","COMPLETESTATUS");
                List<Long> projectIdList = isDoneList.stream().map(EmcReportBean::getProjId).distinct().collect(Collectors.toList());
                List<EmcProjectBean> projectList = projectService.selectListByIds(projectIdList);
        		List<EmcProjectBean> updateProjList = new ArrayList<>();
        		//更新项目表状态
                projectList.forEach(p -> {
					p.setExt$Item("COMPLETESTATUS", "1");
					p.setExt$Item("COMPLETEDATE", completeDate);
					p.setExt$Item("DELIVERYDATE", deliveryDate);
					updateProjList.add(p);
				});
                projectService.getDao().update(updateProjList, "COMPLETESTATUS","COMPLETEDATE","DELIVERYDATE");

                return isDone;
            }
            //项目登记提交后的报告表生成数据的，点击完成修改数据发送CRM接口
            isExistList.forEach(e -> {
                EmcReportBean updateReport = new EmcReportBean();
                updateReport.setId(e.getId());
                updateReport.setExt$Item("COMPLETEDATE", completeDate);
                updateReport.setExt$Item("DELIVERYDATE",deliveryDate);
                updateReport.setExt$Item("COMPLETESTATUS","done");
                updateReportList.add(updateReport);
            });
            getDao().update(updateReportList,"DELIVERYDATE","COMPLETEDATE","REPORTNO","COMPLETESTATUS");
            // 向CRM同步项目信息
            List<Long> projectIdList = isExistList.stream().map(EmcReportBean::getProjId).distinct().collect(Collectors.toList());
            List<EmcProjectBean> projectList = projectService.selectListByIds(projectIdList);
            opportunityOpenService.syncProjectInfo(projectList);
    		List<EmcProjectBean> updateProjList = new ArrayList<>();
            projectList.forEach(p -> {
				p.setExt$Item("COMPLETEDATE", completeDate);
				p.setExt$Item("DELIVERYDATE", deliveryDate);
				p.setExt$Item("COMPLETESTATUS", "1");
				updateProjList.add(p);
			});
            projectService.getDao().update(updateProjList, "COMPLETESTATUS","COMPLETEDATE","DELIVERYDATE");
        }else{
            List<EmcReportBean> reportList = new ArrayList<>();
            EmcReportBean report = new EmcReportBean();
            report.setId(ApplicationContextHelper.getNextIdentity());
            report.setProjId(projId);
            report.setExt$Item("DELIVERYDATE",deliveryDate);
            report.setExt$Item("COMPLETEDATE", completeDate);
            report.setReportNo(projNo);
            report.setExt$Item("COMPLETESTATUS","done");
            reportList.add(report);
            this.getDao().insert(reportList);
            // 向CRM同步项目信息
            List<Long> projectIdList = reportList.stream().map(EmcReportBean::getProjId).distinct().collect(Collectors.toList());
            List<EmcProjectBean> projectList = projectService.selectListByIds(projectIdList);
            opportunityOpenService.syncProjectInfo(projectList);
    		List<EmcProjectBean> updateProjList = new ArrayList<>();
            projectList.forEach(p -> {
				p.setExt$Item("COMPLETESTATUS", "1");
				p.setExt$Item("COMPLETEDATE", completeDate);
				p.setExt$Item("DELIVERYDATE", deliveryDate);
				updateProjList.add(p);
			});
            projectService.getDao().update(updateProjList, "COMPLETESTATUS","COMPLETEDATE","DELIVERYDATE");

        }
        return isDone;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public String selectCompleteDate(RestJsonWrapperBean wrapper) {
        Long projId = Long.parseLong(wrapper.getFilterValue("projId"));
        //查找数据库中的完成日期和交付日期
        List<EmcReportBean> reportBeforeList = getDao().selectListByOneColumnValue(projId, "projId");
        List<EmcReportBean> reportList = reportBeforeList.stream().filter(r ->StringUtils.equals("done", r.getExt$Item("COMPLETESTATUS"))).collect(Collectors.toList());
        String spliDate=null;
        //增加非空判断
        if(!reportList.isEmpty()){
            String deliveryDate = reportList.get(0).getExt$Item("DELIVERYDATE");
            String completeDate = reportList.get(0).getExt$Item("COMPLETEDATE");
            spliDate = deliveryDate+'+'+completeDate;
            return spliDate;
        }
        return spliDate;

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

    /**
     * 计算样品值
     * @param orderResultList
     * @param itemRevisionList
     * @param itemList
     * @return
     */
    protected List<EmcOrderTaskResultBean> calcParallelAverageValue(boolean sampling, List<Long> orderIdList, List<EmcOrderTaskResultBean> orderResultList, List<EmcItemRevisionBean> itemRevisionList, List<EmcItemBean> itemList) {
        // 过滤出所有质控类型为现场平行结果
        List<EmcOrderTaskResultBean> pdupResultList = orderResultList.stream().filter(f -> EmcConstant.QC_P_DUP.equals(f.getQcCategoryCode())).collect(Collectors.toList());
        List<Long> pdupOrderTaskIdList = pdupResultList.stream().map(EmcOrderTaskResultBean::getOrderTaskId).distinct().collect(Collectors.toList());
        // 查询平行样的样品测试（主要查询父样品测试ID和QC类型）
        List<EmcOrderTaskBean> pdupOrderTaskList = orderTaskService.getDao().selectListByIds(pdupOrderTaskIdList, Arrays.asList("ID", "PARENTID", "QCCATEGORYCODE"));
        // 获取平行样的父样品测试ID
        //List<Long> pdupParentOrderTaskIdList = pdupOrderTaskList.stream().map(EmcOrderTaskBean::getParentId).distinct().collect(Collectors.toList());
        //包含Ph分析项的测试结果
        List<EmcOrderTaskResultBean> orderResultPhList = orderResultList.stream().filter(r ->r.getItemName().contains("pH")).collect(Collectors.toList());

        if(!orderResultPhList.isEmpty()){
        	 //筛出PH,固废pH不筛
            List<Long> orderTaskIdList = orderResultList.stream().map(EmcOrderTaskResultBean :: getOrderTaskId).distinct().collect(Collectors.toList());
            List<EmcOrderTaskBean> orderSearchTaskList = orderTaskService.getDao().selectListByIds(orderTaskIdList, Arrays.asList("ID", "PARENTID", "QCCATEGORYCODE","MONITORTMPLID"));
            //固废的模板id
            List<EmcMonitorTmplBean> monitorTmplList  = monitorTmplService.selectListByFilter(SearchFilter.instance().match("MONITORTMPLNAME",PfConstant.SOLIDWASTE).filter(MatchPattern.OR));
            List<String> monitorTmplIdList  = monitorTmplList.stream().map(EmcMonitorTmplBean :: getId).distinct().collect(Collectors.toList());
            List<EmcOrderTaskBean> orderTaskSoildWasteList = orderSearchTaskList.stream().filter(ot ->monitorTmplIdList.contains(ot.getMonitorTmplId())).collect(Collectors.toList());
            if(!orderTaskSoildWasteList.isEmpty()){
            	List<Long> orderTaskSolidIdList = orderTaskSoildWasteList.stream().map(EmcOrderTaskBean ::getId).distinct().collect(Collectors.toList());
            	//固废的测试id
                orderResultPhList = orderResultPhList.stream().filter(r ->!orderTaskSolidIdList.contains(r.getOrderTaskId())).collect(Collectors.toList());
            }
            List<Long> exceptSoildWastePhOrderTaskResultIdList = orderResultPhList.stream().map(EmcOrderTaskResultBean :: getId).distinct().collect(Collectors.toList());
            orderResultList = orderResultList.stream().filter(r ->!exceptSoildWastePhOrderTaskResultIdList.contains(r.getId())).collect(Collectors.toList());
        }

        //正常样测试里面需要去掉不是固废的PH的结果
        // 正常样品测试结果
        List<EmcOrderTaskResultBean> naResultList = sampling ? orderResultList.stream().filter(f -> EmcConstant.N_A.equals(f.getQcCategoryCode())).collect(Collectors.toList())
                : orderResultList.stream().filter(r -> orderIdList.contains(r.getOrderId())).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> naPhResultList = sampling ? orderResultPhList.stream().filter(f -> EmcConstant.N_A.equals(f.getQcCategoryCode())).collect(Collectors.toList())
                : orderResultPhList.stream().filter(r -> orderIdList.contains(r.getOrderId())).collect(Collectors.toList());

        //List<EmcOrderTaskResultBean> hasChildNaResultList = naResultList.stream().filter(result -> pdupParentOrderTaskIdList.contains(result.getOrderTaskId())).collect(Collectors.toList());
        //List<EmcOrderTaskResultBean> noChildNaResultList = naResultList.stream().filter(result -> !pdupParentOrderTaskIdList.contains(result.getOrderTaskId())).collect(Collectors.toList());

        Map<String, List<Pair<String, Double>>> unitConvertMap = queryUnitConvert();

        List<EmcOrderTaskResultBean> finalResultList = new ArrayList<>(naResultList.size());
        naResultList.forEach(result -> {
            String finalValue = StringUtils.isEmpty(result.getAvgResult()) ? result.getReportResult() : result.getAvgResult();
            //finalValue = NumberUtils.isScientificNumber(finalValue) ? NumberUtils.formatNumber(NumberUtils.parseDouble(finalValue)) : finalValue;
            if(NumberUtils.isScientificNumber(finalValue)){
                	BigDecimal finalValueB = new BigDecimal(finalValue);
                	finalValue = finalValueB.toPlainString();
            }
            // 如果分析录入填了无效结果的，则把无效结果转换为?
            if (!StringUtils.isEmpty(result.getReason()) && "/".equals(finalValue)) {
                finalValue = "?";
            }

            result.setOriginalResult(finalValue);
            reviseOriginalValue(result, itemRevisionList, itemList);

            // 判断分析项结果单位和标准限值单位是否一致，如果不一致，则将结果按标准限值单位进行转换
            String unitName = result.getUnitName();
            String indexLimitUnit = result.getIndexLimitUnit();
            if (!StringUtils.isEmpty(indexLimitUnit) && !StringUtils.equals(unitName, indexLimitUnit)) {
                finalValue = WordTemplateUtils.convertUnit(unitConvertMap, unitName, finalValue, indexLimitUnit);
                String convertedLowLimit = WordTemplateUtils.convertUnit(unitConvertMap, unitName, result.getLowLimit(), indexLimitUnit);
                result.setLowLimit(convertedLowLimit);
                result.setUnitName(indexLimitUnit);
                result.setOriginalResult(finalValue);
                String convertReportResult = WordTemplateUtils.convertUnit(unitConvertMap, unitName, result.getReportResult(), indexLimitUnit);
                result.setReportResult(convertReportResult);
            }

            // 对均值进行判断(使用原始值与检出限比较)
            if (StringUtils.equals(result.getResultCategory(), "number") && NumberUtils.isNumber(result.getLowLimit()) && NumberUtils.isNumber(result.getOriginalResult())
                    && NumberUtils.parseDouble(result.getOriginalResult()) < NumberUtils.parseDouble(result.getLowLimit())) {
                result.setReportResult(EmcConstant.ND);
            }

            finalResultList.add(result);
        });
        naPhResultList.forEach(result -> {
            String finalValue = result.getReportResult();
            finalValue = NumberUtils.isScientificNumber(finalValue) ? NumberUtils.formatNumber(NumberUtils.parseDouble(finalValue)) : finalValue;

            // 如果分析录入填了无效结果的，则把无效结果转换为?
            if (!StringUtils.isEmpty(result.getReason()) && "/".equals(finalValue)) {
                finalValue = "?";
            }

            result.setOriginalResult(finalValue);
            reviseOriginalValue(result, itemRevisionList, itemList);

            // 对均值进行判断(使用原始值与检出限比较)
            if (StringUtils.equals(result.getResultCategory(), "number") && NumberUtils.isNumber(result.getLowLimit()) && NumberUtils.isNumber(result.getOriginalResult())
                    && NumberUtils.parseDouble(result.getOriginalResult()) < NumberUtils.parseDouble(result.getLowLimit())) {
                result.setReportResult(EmcConstant.ND);
            }

            finalResultList.add(result);
        });

  /*      // 过滤掉无效的有现场平行的正常样品结果
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
            double bdnumber=0;
            if(result.getReportResult()!=null&&result.getReportResult()!=""){
            	if(NumberUtils.isNumber(result.getReportResult())){
                    BigDecimal bd = new BigDecimal(result.getReportResult());
                    bdnumber = Double.parseDouble(bd.toPlainString());
                    validChildPdupResultList.add(String.valueOf(bdnumber));
            	}

            }else{
                validChildPdupResultList.add(result.getReportResult());
            }


            validChildPdupResultList.stream().mapToDouble(NumberUtils::parseDouble).average().ifPresent(avgResult -> {
                // 使用NumberUtils.formatNumber(avgResult)先格式化一下，防止数据太小自动变成了科学计数法
                result.setOriginalResult(NumberUtils.formatNumber(avgResult) + "");

                // 对均值原始结果进行修约
                reviseOriginalValue(result, itemRevisionList, itemList);

                finalResultList.add(result);
            });
        });*/

        return finalResultList;
    }

    /**
     * 计算二噁英样品值
     * @param orderResultList
     * @param itemRevisionList
     * @param itemList
     * @return
     */
    protected List<EmcOrderTaskResultBean> calcDioxinParallelAverageValue(boolean sampling, List<Long> orderIdList, List<EmcOrderTaskResultBean> orderResultList, List<EmcItemRevisionBean> itemRevisionList, List<EmcItemBean> itemList) {
        // 采样项目：正常样品和现场平行测试结果
        // 送样项目：所有送样样品结果
        List<EmcOrderTaskResultBean> naResultList = sampling ? orderResultList.stream().filter(f -> EmcConstant.N_A.equals(f.getQcCategoryCode()) || EmcConstant.QC_P_DUP.equals(f.getQcCategoryCode())).collect(Collectors.toList())
                : orderResultList.stream().filter(r -> orderIdList.contains(r.getOrderId())).collect(Collectors.toList());

        List<EmcOrderTaskResultBean> finalResultList = new ArrayList<>(naResultList.size());
        naResultList.forEach(result -> {
            String finalValue = StringUtils.isEmpty(result.getAvgResult()) ? result.getReportResult() : result.getAvgResult();
            finalValue = NumberUtils.isScientificNumber(finalValue) ? NumberUtils.formatNumber(NumberUtils.parseDouble(finalValue)) : finalValue;

            // 如果分析录入填了无效结果的，则把无效结果转换为?
            if (!StringUtils.isEmpty(result.getReason()) && "/".equals(finalValue)) {
                finalValue = "?";
            }

            result.setOriginalResult(finalValue);
            reviseOriginalValue(result, itemRevisionList, itemList);

            // 对均值进行判断(使用原始值与检出限比较)
            if (StringUtils.equals(result.getResultCategory(), "number") && NumberUtils.isNumber(result.getLowLimit()) && NumberUtils.isNumber(result.getOriginalResult())
                    && NumberUtils.parseDouble(result.getOriginalResult()) < NumberUtils.parseDouble(result.getLowLimit())) {
                result.setReportResult(EmcConstant.ND);
            }

            finalResultList.add(result);
        });

        return finalResultList;
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

/*        if (EmcConstant.ND.equals(finalValue) && NumberUtils.isNumber(result.getLowLimit())) {
            result.setReportResult(ArithUtils.div(NumberUtils.parseDouble(result.getLowLimit()), 2) + "");
            return;
        }*/

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
        if (StringUtils.equals(result.getResultCategory(), "number") && NumberUtils.isNumber(result.getLowLimit()) && NumberUtils.isNumber(result.getOriginalResult())
                && NumberUtils.parseDouble(result.getOriginalResult()) < NumberUtils.parseDouble(result.getLowLimit())) {
            result.setReportResult(EmcConstant.ND);
        }
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public String selectCompleteStatus(RestJsonWrapperBean wrapper) {
        Long projId = Long.parseLong(wrapper.getFilterValue("projId"));
        //查找数据库中的完成状态
        List<EmcReportBean> reportList = selectListByFilter(SearchFilter.instance().match("projId",projId).filter(MatchPattern.OR));
        String completeStatus=null;
        //增加非空判断
        if(!reportList.isEmpty()){
            completeStatus = reportList.get(0).getExt$Item("COMPLETESTATUS");
            return completeStatus;
        }
        return completeStatus;

    }

    @Override
    @Transactional
    public void createReport(RestJsonWrapperBean wrapper) {
        List<EmcFolderBean> selectedFolderList = wrapper.parse(EmcFolderBean.class);
        List<EmcOrderTaskBean> selectedOrderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<Long> selectedTestIdList = selectedOrderTaskList.stream().map(EmcOrderTaskBean::getTestId).distinct().collect(Collectors.toList());

        if(!selectedFolderList.isEmpty()){
            List<Long> folderIdList = selectedFolderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
            EmcProjectBean project = projectService.selectById(selectedFolderList.get(0).getProjId());

            String needSplitReport = Optional.ofNullable(project.getNeedSplitReport()).orElse(Constant.NO);
            String erpOrderNumber = Optional.ofNullable(project.getErpOrderNumber()).orElse(project.getProjNo());

            List<EmcReportBean> reportList = getDao().selectListByOneColumnValue(project.getId(), "projId", Arrays.asList("id", "reportNo", "orderCategory"));
            List<String> reportNoList = reportList.stream().filter(r -> StringUtils.isEmpty(r.getOrderCategory())).map(EmcReportBean::getReportNo).filter(no -> !StringUtils.isEmpty(no)).distinct().collect(Collectors.toList());

            /*报告编号规则：
            判断project表的是后拆分报告
            否：报告编号为ERP订单编号
            是：报告编号为ERP订单编号+C1(C1为流水号，该订单下的第一份报告)*/
            String reportNo;
            String nextSeqNo;
            int maxSeqNo;
            if (Constant.NO.equals(needSplitReport)) {
                maxSeqNo = reportNoList.stream().map(no -> StringUtils.replace(no, project.getProjNo(), "").replace("-", "")).filter(NumberUtils::isNumber).mapToInt(NumberUtils::parseInt).max().orElse(0);
                nextSeqNo = (reportNoList.isEmpty()) ? "" : "-" + (maxSeqNo + 1);
                reportNo = erpOrderNumber + nextSeqNo;
            }else{
                maxSeqNo = reportNoList.stream().map(no -> StringUtils.replace(no, erpOrderNumber+"C", "")).filter(NumberUtils::isNumber).mapToInt(NumberUtils::parseInt).max().orElse(0);
                nextSeqNo = reportNoList.isEmpty() ? "1" : ObjectUtils.toString(maxSeqNo + 1);
                reportNo = erpOrderNumber + "C" + nextSeqNo;
            }

            EmcReportBean newReport = new EmcReportBean();
            newReport.setId(ApplicationContextHelper.getNextIdentity());
            newReport.setProjId(selectedFolderList.get(0).getProjId());
            newReport.setStatus(EmcBizConstant.REPORT_STATUS_REPORTEDIT);
            newReport.setReportNo(reportNo);
            newReport.setReportName(project.getProjName());

            // 如果未选择测试，则默认出所选点位下的所有测试
            List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(folderIdList, "folderId", Arrays.asList("id", "projId", "folderId", "orderId", "testId"));
            List<EmcOrderTaskBean> validOrderTaskList = selectedTestIdList.isEmpty() ? orderTaskList : orderTaskList.stream().filter(task -> selectedTestIdList.contains(task.getTestId())).collect(Collectors.toList());
            List<EmcReportLineBean> reportLineList = new ArrayList<>(validOrderTaskList.size());

            validOrderTaskList.forEach(task -> {
                EmcReportLineBean reportLine = new EmcReportLineBean();
                reportLine.setId(ApplicationContextHelper.getNextIdentity());
                reportLine.setReportId(newReport.getId());
                reportLine.setProjId(task.getProjId());
                reportLine.setFolderId(task.getFolderId());
                reportLine.setOrderId(task.getOrderId());
                reportLine.setOrderTaskId(task.getId());
                reportLineList.add(reportLine);
            });

            getDao().insert(newReport);
            reportLineService.getDao().insert(reportLineList);
        }else{
            EmcProjectBean project = projectService.selectById(selectedOrderTaskList.get(0).getProjId());

            String needSplitReport = Optional.ofNullable(project.getNeedSplitReport()).orElse(Constant.NO);
            String erpOrderNumber = Optional.ofNullable(project.getErpOrderNumber()).orElse(project.getProjNo());

            List<EmcReportBean> reportList = getDao().selectListByOneColumnValue(project.getId(), "projId", Arrays.asList("id", "reportNo", "orderCategory"));
            List<String> reportNoList = reportList.stream().filter(r -> StringUtils.isEmpty(r.getOrderCategory())).map(EmcReportBean::getReportNo).filter(no -> !StringUtils.isEmpty(no)).distinct().collect(Collectors.toList());

            /*报告编号规则：
            判断project表的是后拆分报告
            否：报告编号为ERP订单编号
            是：报告编号为ERP订单编号+C1(C1为流水号，该订单下的第一份报告)*/
            String reportNo;
            String nextSeqNo;
            int maxSeqNo;
            if(Constant.NO.equals(needSplitReport)){
                maxSeqNo = reportNoList.stream().map(no -> StringUtils.replace(no, project.getProjNo(), "").replace("-", "")).filter(NumberUtils::isNumber).mapToInt(NumberUtils::parseInt).max().orElse(0);
                nextSeqNo = (reportNoList.isEmpty()) ? "" : "-" + (maxSeqNo + 1);
                reportNo = erpOrderNumber + nextSeqNo;
            }else{
                maxSeqNo = reportNoList.stream().map(no -> StringUtils.replace(no, erpOrderNumber+"C", "")).filter(NumberUtils::isNumber).mapToInt(NumberUtils::parseInt).max().orElse(0);
                nextSeqNo = reportNoList.isEmpty() ? "1" : ObjectUtils.toString(maxSeqNo + 1);
                reportNo = erpOrderNumber + "C" + nextSeqNo;
            }

            EmcReportBean newReport = new EmcReportBean();
            newReport.setId(ApplicationContextHelper.getNextIdentity());
            newReport.setProjId(project.getId());
            newReport.setStatus(EmcBizConstant.REPORT_STATUS_REPORTEDIT);
            newReport.setReportNo(reportNo);

            List<EmcReportLineBean> reportLineList = new ArrayList<>(selectedOrderTaskList.size());

            selectedOrderTaskList.forEach(task -> {
                EmcReportLineBean reportLine = new EmcReportLineBean();
                reportLine.setId(ApplicationContextHelper.getNextIdentity());
                reportLine.setReportId(newReport.getId());
                reportLine.setProjId(task.getProjId());
                reportLine.setFolderId(task.getFolderId());
                reportLine.setOrderId(task.getOrderId());
                reportLine.setOrderTaskId(task.getId());
                reportLineList.add(reportLine);
            });

            getDao().insert(newReport);
            reportLineService.getDao().insert(reportLineList);
        }

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    @ValidateDataWith(PfGenerateReportResultValidator.class)
    public void generateReportResult(RestJsonWrapperBean wrapper) {
        EmcReportBean report = wrapper.parseUnique(EmcReportBean.class);
        List<EmcReportLineBean> reportLineList = reportLineService.getDao().selectListByOneColumnValue(report.getId(), "reportId");

        // 查询需要在报告中展示的样品结果数据
        List<Long> orderTaskIdList = reportLineList.stream().map(EmcReportLineBean::getOrderTaskId).collect(Collectors.toList());
        if (orderTaskIdList.isEmpty()) {
            return;
        }

        // 删除原有的报告结果数据
        EmcReportResultBean deletedReportResult = new EmcReportResultBean();
        deletedReportResult.setReportId(report.getId());
        reportResultService.getDao().deleteBy(Collections.singletonList(deletedReportResult), "reportId");

        // 过滤掉分析录入合并的测试
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderTaskIdList, "id", Arrays.asList("id", "virtual"));
        List<Long> validOrderTaskIdList = orderTaskList.stream().filter(task -> !StringUtils.equals(task.getExt$Item("virtual"), Constant.YES)).map(EmcOrderTaskBean::getId).collect(Collectors.toList());

        if(validOrderTaskIdList.size()>0) {
        	List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.selectListByFilter(SearchFilter.instance()
                    .match("orderTaskId", validOrderTaskIdList).filter(MatchPattern.OR)
                    .match("REPORTDISPLAY", Constant.YES).filter(MatchPattern.SEQ)
                    .match("RESULTSTATUS", EmcBizConstant.ORDERTASKRESULT_STATUS_DONE).filter(MatchPattern.SEQ));

            if (orderTaskResultList.isEmpty()) {
                return;
            }

            List<Long> orderIdList = reportLineList.stream().map(EmcReportLineBean::getOrderId).distinct().collect(Collectors.toList());
            List<EmcOrderBean> orderList = orderService.selectListByFilter(SearchFilter.instance().match("id", orderIdList).filter(MatchPattern.OR));
            Map<Long, EmcOrderBean> orderIdMap = orderList.stream().collect(Collectors.toMap(EmcOrderBean::getId, Function.identity()));

            // 查询分析项及修约规则
            List<Long> itemIdList = orderTaskResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
            List<EmcItemRevisionBean> itemRevisionList = itemRevisionService.getDao().selectListByOneColumnValues(itemIdList, "ITEMID");
            List<EmcItemBean> itemList = itemService.getDao().selectListByIds(itemIdList);

            // 报告数据
            List<EmcReportResultBean> reportResultList = new ArrayList<>();

            // 判断报告数据是否包含油烟类型
            boolean existLampblack = orderList.stream().anyMatch(order -> StringUtils.contains(order.getOrderCategory(), EmcConstant.LAMPBLACK_ORDER_CATEGORY_KEYWORD));
            // 判定是否为二噁英报告，二噁英报告结果数据生成方式不一样
            String bizCategoryId = report.getExt$Item("bizCategoryId");
            boolean isDioxin = StringUtils.equalsIgnoreCase(bizCategoryId, "C");

            // 是否为采样项目
            boolean sampling = StringUtils.equals(Constant.YES, report.getExt$Item("sample"));

            // 计算样品值
            List<EmcOrderTaskResultBean> finalResultList = isDioxin ? calcDioxinParallelAverageValue(sampling, orderIdList, orderTaskResultList, itemRevisionList, itemList) : calcParallelAverageValue(sampling, orderIdList, orderTaskResultList, itemRevisionList, itemList);
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

            boolean calcStatistic = Constant.YES.equals(report.getIfStatistics());

            // 计算日均值
            if (calcStatistic) {
                Map<String, List<EmcOrderTaskResultBean>> totalTaskResultMap = finalResultList.stream().collect(Collectors.groupingBy(e -> e.getFolderId() + "$" + e.getItemId() + "$" + e.getExt$Item("testname")));
                totalTaskResultMap.forEach((key, resultList) -> {
                    resultList.forEach(result -> replaceNdHdValue(result, result.getReportResult()));


                    Map<String, List<EmcOrderTaskResultBean>> dayResultListMap = resultList.stream().collect(Collectors.groupingBy(e -> e.getFolderId() + "$" + e.getCycle()));
                    dayResultListMap.forEach((day, dayResultList) -> {
                        List<String> dayResultValueList = dayResultList.stream().map(EmcOrderTaskResultBean::getReportResult).collect(Collectors.toList());

                        dayResultValueList.stream().filter(NumberUtils::isNumber).mapToDouble(NumberUtils::parseDouble).average().ifPresent(avgResult -> {
                            EmcOrderTaskResultBean totalResult = new EmcOrderTaskResultBean();
                            BeanUtils.copyProperties(dayResultList.get(0), totalResult);
                            totalResult.setOriginalResult(NumberUtils.formatNumber(avgResult));

                            // 对均值原始结果进行修约
                            reviseOriginalValue(totalResult, itemRevisionList, itemList);

                            EmcReportResultBean reportResult = new EmcReportResultBean();
                            reportResult.setId(ApplicationContextHelper.getNextIdentity());
                            reportResult.setReportId(report.getId());
                            reportResult.setDataType(EmcConstant.PN_DAYAVERAGE);
                            EmcOrderBean currentOrder = orderIdMap.get(dayResultList.get(0).getOrderId());

                            copyProperties(reportResult, currentOrder, totalResult);
                            reportResultList.add(reportResult);
                        });
                    });
                });
            }

            // 计算总均值
            List<Long> invalidLampblackResultIdList = new ArrayList<>();
            if (calcStatistic) {
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
                reportResultList.clear();
                orderTaskResultList.clear();
            }
        }

    }

    @Override
    @ValidateDataWith(PfViewReportValidator.class)
    public Long viewReport(RestJsonWrapperBean wrapper) {
        EmcReportBean report = wrapper.parseUnique(EmcReportBean.class);
        Long projectId = report.getProjId();

        String targetId = getDao().getTable() + "$" + report.getId();
        List<CoreFileBean> wordReportFileList = coreFileService.selectListByFilter(SearchFilter.instance().match("targetId",
                targetId).filter(MatchPattern.SEQ).match("bizCategory", EmcConstant.WORD_REPORT_ATTACHMENT_TYPE).filter(MatchPattern.EQ));
        EmcProjectBean project = projectService.selectFirstByFilter(SearchFilter.instance().match("id", projectId).filter(MatchPattern.SEQ));

        List<EmcReportBean> reportList = Collections.singletonList(report);

        //合成报告分析记录、采样记录
        LoginUser loginUser = LocalContextHelper.getLoginPrincipal();
        ExecutorHelper.submit(() -> {
            LocalContextHelper.setUserLogin(loginUser);
            this.composeReportRecordPdf(reportList);
            LocalContextHelper.removeUserLogin();
        });

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
            generateWordReport(templateFile, templateFile, report, project, true, EmcConstant.WORD_REPORT_ATTACHMENT_TYPE);
            List<CoreFileBean> newCoreFileList = saveWordReportCoreFile(wordTemplateConfigFile.getName(), templateFile, reportList, EmcConstant.WORD_REPORT_ATTACHMENT_TYPE);

            return newCoreFileList.get(0).getId();
        }

        Long reportConfigLineId = report.getReportConfigLineId();

        List<CoreFileBean> wordTemplateConfigFileList = coreFileService.getDao().selectListByOneColumnValue("T_CORE_REPORT_CONFIG_LINE$" + reportConfigLineId, "targetId");
        if (!wordTemplateConfigFileList.isEmpty()) {
            CoreFileBean wordTemplateConfigFile = wordTemplateConfigFileList.get(0);

            String reportNo = StringUtils.isEmpty(report.getReportNo()) ? project.getProjNo() : report.getReportNo();
            report.setReportNo(reportNo);
            String wordReportFileName = FileUtils.getFileBaseName(wordTemplateConfigFile.getName()) + "-" + reportNo + "." +FileUtils.getFileExtension(wordTemplateConfigFile.getName());

            File templateFile = FilePathManager.getLocalPath(FilePathDTO.of(wordTemplateConfigFile.getScope(), "",
                    wordTemplateConfigFile.getExt(), wordTemplateConfigFile.getName())).toFile();
            File wordReportFile = FilePathManager.getLocalPath(FilePathDTO.of(wordTemplateConfigFile.getScope(), "",
                    wordTemplateConfigFile.getExt(), wordReportFileName)).toFile();

            generateWordReport(templateFile, wordReportFile, report, project, false, EmcConstant.WORD_REPORT_ATTACHMENT_TYPE);
            List<CoreFileBean> newCoreFileList = saveWordReportCoreFile(wordReportFileName, wordReportFile, reportList, EmcConstant.WORD_REPORT_ATTACHMENT_TYPE);
            return newCoreFileList.get(0).getId();
        }

        return 0L;
    }

    /*合成报告分析记录、采样记录*/
    private void composeReportRecordPdf(List<EmcReportBean> reportList) {
        if(CollectionUtils.isEmpty(reportList)){
            return;
        }
        /*合成分析记录*/
         this.composeReportRunRecordPdf(reportList);
        /*合成采样记录*/
        this.composeReportSampleRecordPdf(reportList);
    }

    /*合成采样记录*/
    private void composeReportSampleRecordPdf(List<EmcReportBean> reportList) {
        List<Long> projIdList = reportList.stream().filter(e -> !ObjectUtils.isEmpty(e.getProjId()))
                .map(EmcReportBean :: getProjId).distinct().collect(Collectors.toList());

        //如果已经存在直接返回
        List<CoreFileBean> existFilesFilter = reportList.stream().map(report -> {
            CoreFileBean coreFileBean = new CoreFileBean();
            coreFileBean.setTargetId("T_EMC_REPORT$" + report.getId());
            coreFileBean.setExt("sampleRecordPdf");
            return coreFileBean;
        }).distinct().collect(Collectors.toList());

        List<CoreFileBean> existFiles = coreFileService.getDao().selectList(existFilesFilter, Arrays.asList("TARGETID", "EXT"), Collections.emptyList());

        if(!CollectionUtils.isEmpty(existFiles)){
            return;
        }

        List<EmcSampleBean> sampleList = sampleService.selectListByFilter(SearchFilter.instance().match("projid", projIdList).filter(MatchPattern.OR));

        List<CoreFileBean> fileFilters = new ArrayList<>();
        sampleList.forEach(r -> {
            CoreFileBean coreFileBean = new CoreFileBean();
            coreFileBean.setTargetId("T_EMC_SAMPLE" + "$" + r.getId());
            coreFileBean.setExt("sampleRecordPdf");
            fileFilters.add(coreFileBean);
        });

        List<CoreFileBean> allSampleFileList = coreFileService.getDao().selectList(fileFilters, Arrays.asList("TARGETID", "EXT"), Collections.emptyList());
        if (ListUtils.isEmpty(allSampleFileList)) {
            return;
        }

        reportList.forEach(report -> {
            Long projId = report.getProjId();
            List<CoreFileBean> curReportFileList = allSampleFileList.stream().filter(file -> StringUtils.contains(file.getTargetId(), projId + "")).collect(Collectors.toList());
            if (curReportFileList.isEmpty()) {
                return;
            }

            List<FilePathDTO> fileRepresentations = curReportFileList.stream().map(CoreFileUtils::toFilePath).collect(Collectors.toList());
            List<FilePathDTO> validFileRepresentations = fileRepresentations.stream().filter(fr -> FilePathManager.getLocalDirPath(fr).toFile().exists()).collect(Collectors.toList());
            if (validFileRepresentations.isEmpty()) {
                return;
            }

            String projectReportPdfName = "采样记录-" + report.getReportNo() + ".pdf";
            FilePathDTO composedPdfFile = FilePathDTO.of(FileScope.temp.name(), "" + System.currentTimeMillis(), StringUtils.randomUUID(8), projectReportPdfName);
            coreFileService.mergePdfList(validFileRepresentations, composedPdfFile);
            String tempFilePath = FilePathManager.getLocalPath(composedPdfFile).toString();
            // 保存文件信息到 t_core_file 表
            if (!StringUtils.isEmpty(tempFilePath)) {
                CoreFileBean coreFileBean = new CoreFileBean();
                coreFileBean.setTargetId("T_EMC_REPORT" + "$" + report.getId());
                coreFileBean.setScope(FileScope.secure.name());
                coreFileBean.setExt("sampleRecordPdf");
                coreFileService.upload(coreFileBean, new File(tempFilePath));
            }
        });
    }

    /*合成分析记录*/
    private List<CoreFileBean> composeReportRunRecordPdf(List<EmcReportBean> reportList) {
        List<Long> projIdList = reportList.stream().filter(e -> !ObjectUtils.isEmpty(e.getProjId()))
                .map(EmcReportBean :: getProjId).distinct().collect(Collectors.toList());

        //如果已经存在，直接返回
        List<CoreFileBean> existFilesFilter = reportList.stream().map(report -> {
            CoreFileBean coreFileBean = new CoreFileBean();
            coreFileBean.setTargetId("T_EMC_REPORT$" + report.getId());
            coreFileBean.setExt("analysisRecordPdf");
            return coreFileBean;
        }).distinct().collect(Collectors.toList());

        List<CoreFileBean> existFiles = coreFileService.getDao().selectList(existFilesFilter, Arrays.asList("TARGETID", "EXT"), Collections.emptyList());

        if(!CollectionUtils.isEmpty(existFiles)){
            return existFiles;
        }

        List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("projid", projIdList).filter(MatchPattern.OR)
                .match("analysisType", EmcBizConstant.ANALYSISCATEGORY_ANALYSIS).filter(MatchPattern.EQ));

        //按照项目和批号分组
        List<EmcOrderTaskBean> distinctOrderTaskList = orderTaskList.stream().filter(e -> !ObjectUtils.isEmpty(e.getProjId()) && !ObjectUtils.isEmpty(e.getRunId())).collect(Collectors
                .collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getProjId() + "#" + o.getRunId()))), ArrayList<EmcOrderTaskBean>::new));

        List<CoreFileBean> fileFilters = new ArrayList<>();
        distinctOrderTaskList.forEach(r -> {
            CoreFileBean coreFileBean = new CoreFileBean();
            coreFileBean.setTargetId("T_EMC_RUN" + "$" + r.getRunId()+"-"+r.getProjId());
            coreFileBean.setExt("composePdf");
            fileFilters.add(coreFileBean);
        });

        List<CoreFileBean> allRunFileList = coreFileService.getDao().selectList(fileFilters, Arrays.asList("TARGETID", "EXT"), Collections.emptyList());
        if (ListUtils.isEmpty(allRunFileList)) {
            return Collections.emptyList();
        }


        List<Long> fileIdList = new ArrayList<>();
        reportList.forEach(report -> {
            Long projId = report.getProjId();
            List<CoreFileBean> curReportFileList = allRunFileList.stream().filter(file -> StringUtils.contains(file.getTargetId(), projId + "")).collect(Collectors.toList());
            if (curReportFileList.isEmpty()) {
                return;
            }

            List<FilePathDTO> fileRepresentations = curReportFileList.stream().map(CoreFileUtils::toFilePath).collect(Collectors.toList());
            List<FilePathDTO> validFileRepresentations = fileRepresentations.stream().filter(fr -> FilePathManager.getLocalDirPath(fr).toFile().exists()).collect(Collectors.toList());
            if (validFileRepresentations.isEmpty()) {
                return;
            }

            String projectReportPdfName = "分析记录-"+report.getReportNo()+ ".pdf";
            FilePathDTO composedPdfFile = FilePathDTO.of(FileScope.temp.name(), "" + System.currentTimeMillis(), StringUtils.randomUUID(8), projectReportPdfName);
            coreFileService.mergePdfList(validFileRepresentations, composedPdfFile);
            String tempFilePath = FilePathManager.getLocalPath(composedPdfFile).toString();
            // 保存文件信息到 t_core_file 表
            if (!StringUtils.isEmpty(tempFilePath)) {
                CoreFileBean coreFileBean = new CoreFileBean();
                coreFileBean.setTargetId("T_EMC_REPORT" + "$" + report.getId());
                coreFileBean.setScope(FileScope.secure.name());
                coreFileBean.setExt("analysisRecordPdf");
                fileIdList.add(coreFileService.upload(coreFileBean, new File(tempFilePath)));
            }
        });

        return coreFileService.getDao().selectListByIds(fileIdList);
    }

    @Override
    @ValidateDataWith(PfViewReportValidator.class)
    public Long viewQcReport(RestJsonWrapperBean wrapper) {
        EmcReportBean report = wrapper.parseUnique(EmcReportBean.class);
        Long projectId = report.getProjId();

        String targetId = getDao().getTable() + "$" + report.getId();
        List<CoreFileBean> wordReportFileList = coreFileService.selectListByFilter(SearchFilter.instance().match("targetId",
                targetId).filter(MatchPattern.SEQ).match("bizCategory", PfConstant.QC_WORD_REPORT_ATTACHMENT_TYPE).filter(MatchPattern.EQ));
        EmcProjectBean project = projectService.selectFirstByFilter(SearchFilter.instance().match("id", projectId).filter(MatchPattern.SEQ));

        List<EmcReportBean> reportList = Collections.singletonList(report);

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
            generateWordReport(templateFile, templateFile, report, project, true, PfConstant.QC_WORD_REPORT_ATTACHMENT_TYPE);
            List<CoreFileBean> newCoreFileList = saveWordReportCoreFile(wordTemplateConfigFile.getName(), templateFile, reportList, PfConstant.QC_WORD_REPORT_ATTACHMENT_TYPE);

            return newCoreFileList.get(0).getId();
        }

        long reportConfigLineId = NumberUtils.parseLong(report.getExt$Item("qcReportLineId"));

        List<CoreFileBean> wordTemplateConfigFileList = coreFileService.getDao().selectListByOneColumnValue("T_CORE_REPORT_CONFIG_LINE$" + reportConfigLineId, "targetId");
        if (!wordTemplateConfigFileList.isEmpty()) {
            CoreFileBean wordTemplateConfigFile = wordTemplateConfigFileList.get(0);

            String wordReportFileName = FileUtils.getFileBaseName(wordTemplateConfigFile.getName()) + "-" +report.getReportNo()+ "." +FileUtils.getFileExtension(wordTemplateConfigFile.getName());

            File templateFile = FilePathManager.getLocalPath(FilePathDTO.of(wordTemplateConfigFile.getScope(), "",
                    wordTemplateConfigFile.getExt(), wordTemplateConfigFile.getName())).toFile();
            File wordReportFile = FilePathManager.getLocalPath(FilePathDTO.of(wordTemplateConfigFile.getScope(), "",
                    wordTemplateConfigFile.getExt(), wordReportFileName)).toFile();

            generateWordReport(templateFile, wordReportFile, report, project, false, PfConstant.QC_WORD_REPORT_ATTACHMENT_TYPE);
            List<CoreFileBean> newCoreFileList = saveWordReportCoreFile(wordReportFileName, wordReportFile, reportList, PfConstant.QC_WORD_REPORT_ATTACHMENT_TYPE);

            return newCoreFileList.get(0).getId();
        }

        return 0L;
    }

    @Override
    public RestValidationResultBean validateResultStatus(RestJsonWrapperBean wrapper) {
        List<EmcReportBean> reportList = wrapper.parse(EmcReportBean.class);
        List<Long> reportIdList = reportList.stream().map(EmcReportBean::getId).collect(Collectors.toList());
        List<EmcReportLineBean> reportLineList = reportLineService.getDao().selectListByOneColumnValues(reportIdList, "reportId", Arrays.asList("id", "orderTaskId"));
        List<Long> reportTaskIdList = reportLineList.stream().map(EmcReportLineBean::getOrderTaskId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(reportTaskIdList, "id", Arrays.asList("id", "runId"));
        List<String> runIdList = orderTaskList.stream().filter(ok ->!StringUtils.isEmpty(ok.getRunId())).map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());

        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(runIdList, "runId", Arrays.asList("ID", "orderNo", "reportResult", "resultStatus", "qcCategoryCode", "lowLimit"));
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
            messages.add(I18nHelper.getMessage("EMC.MODULE.BP.RESULT.RUNS.TIP.RESULT_OOS", orderNos));
        }

        // 空白样品结果不为 ND
        if (!nonNdResultList.isEmpty()) {
            String orderNos = nonNdResultList.stream().map(EmcOrderTaskResultBean::getOrderNo).distinct().collect(Collectors.joining("、"));
            messages.add(I18nHelper.getMessage("EMC.MODULE.BP.RESULT.RUNS.TIP.RESULT_NON_ND", orderNos));
        }

        messages.add(I18nHelper.getMessage("EMC.MODULE.BP.RESULT.RUNS.TIP.CONTINUE_GENERATE_QC_REPORT"));

        return new RestValidationResultBean(false, String.join("，", messages));
    }

    public void generateWordReport(File templateFile, File wordReportFile, EmcReportBean report, EmcProjectBean project, boolean exist, String reportType) {
        try (FileInputStream inputStream = new FileInputStream(templateFile);
             XWPFDocument document = new XWPFDocument(inputStream);
             FileOutputStream outputStream = new FileOutputStream(wordReportFile)) {

            if (!exist) {
                writeDataToReport(templateFile, document, report, project);
            }

            setSubmitInformation(templateFile, document, report, reportType);

            document.write(outputStream);
        } catch (IOException e) {
            throw new FileException(e);
        }
    }

    public void generateWordReport(File templateFile, File wordReportFile, EmcReportBean report,
            EmcProjectBean project, boolean exist) {
        generateWordReport(templateFile, wordReportFile, report, project, exist, EmcConstant.WORD_REPORT_ATTACHMENT_TYPE);
    }

    public void writeDataToReport2024(File templateFile, XWPFDocument document,
            EmcReportBean report, EmcProjectBean project) {
        writeDataToReport(templateFile, document, report, project);
    }

    public void setSignatureInfo(EmcReportBean report) {
        CoreFileBean reportFile = coreFileService.selectFirstByFilter(SearchFilter.instance()
                .match("targetId", getDao().getTable() + "$" + report.getId()).filter(MatchPattern.SEQ)
                .match("bizCategory", "wordReport").filter(MatchPattern.SEQ));
        if (reportFile == null) return;
        fileManager.download(reportFile);
        File inputFile = CoreFileUtils.getLocalPath(reportFile).toFile();
        File outputFile = FilePathManager.getLocalPath(CoreFileUtils.toFilePath(reportFile)).toFile();
        try (FileInputStream input = new FileInputStream(inputFile);
             XWPFDocument document = new XWPFDocument(input);
             FileOutputStream output = new FileOutputStream(outputFile)) {
            setSubmitInformation(inputFile, document, report);
            document.write(output);
        } catch (IOException e) {
            throw new FileException(e);
        }
        coreFileService.upload(reportFile, outputFile);
    }

    public void writeDataToReport(File templateFile, XWPFDocument document, EmcReportBean report, EmcProjectBean project) {
        List<EmcReportLineBean> reportLineList = reportLineService.getDao().selectListByOneColumnValue(report.getId(), "reportId");
        List<Long> reportFolderIdList = reportLineList.stream().map(EmcReportLineBean::getFolderId).distinct().collect(Collectors.toList());
        List<Long> reportOrderTaskIdList = reportLineList.stream().map(EmcReportLineBean::getOrderTaskId).collect(Collectors.toList());

        if (reportFolderIdList.isEmpty() || reportOrderTaskIdList.isEmpty()) {
            return;
        }

        // 根据项目ID查询所有未终止的点位列表，包含正常点位、现场空白和现场平行
        List<EmcFolderBean> allFolderList = folderService.selectListByFilter(SearchFilter.instance().match("id",
                reportFolderIdList).filter(MatchPattern.OR).match("termination", "0").filter(MatchPattern.EQ));
        List<Long> allFolderIdList = allFolderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());

        // 根据项目ID查询采样单，主要用于获取采样设备
        List<EmcSampleBean> sampleList = sampleService.getDao().selectListByOneColumnValue(project.getId(), "PROJID", Arrays.asList("ID", "SAMPLINGEQUIPTID", "SAMPLINGEQUIPTNAME", "ACCEPTEDDATE", "ACCEPTEUSERNAME","ORDERCATEGORY"));

        List<String> qcCategoryCodeList = new ArrayList<>();
        qcCategoryCodeList.add(EmcConstant.N_A);

        // 对于二噁英的需要在报告中展示现场平行的数据
        String bizCategoryCode = project.getBizCategoryCode();
        boolean isDioxin = StringUtils.equalsIgnoreCase(bizCategoryCode, "C");
        if (isDioxin) {
            qcCategoryCodeList.add(EmcConstant.QC_P_DUP);
        }

        boolean sampling = StringUtils.equals(Constant.YES, project.getSample());

        // 根据点位查询所有的正常样品列表(未终止的)，即QC为NA的样品，排除现场空白和现场平行的样品
        List<EmcOrderBean> orderList = sampling ? orderService.selectListByFilter(SearchFilter.instance()
                .match("folderId", allFolderIdList).filter(MatchPattern.OR)
                .match("termination", "0").filter(MatchPattern.EQ)
                .match("QCCATEGORYCODE", qcCategoryCodeList).filter(MatchPattern.OR))
                : orderService.getDao().selectListByOneColumnValues(allFolderIdList, "folderId");

        // 根据点位查询所有的样品测试列表
        List<Long> folderIdList = orderList.stream().map(EmcOrderBean::getFolderId).distinct().collect(Collectors.toList());
        List<EmcFolderBean> folderList = allFolderList.stream().filter(f -> folderIdList.contains(f.getId())).collect(Collectors.toList());

        List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("id", reportOrderTaskIdList).filter(MatchPattern.OR));
        // 根据点位查询样品测试结果列表，用于获取作为参数的测试结果
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.selectListByFilter(SearchFilter.instance().match("orderTaskId", reportOrderTaskIdList).filter(MatchPattern.OR));

        List<String> runIdList = orderTaskList.stream().filter(a -> !StringUtils.isEmpty(a.getRunId())).map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());

        // 查询样品测试分析项组、分析项组对应的分析项
        List<Long> testMethodIdList = orderTaskList.stream().filter(task -> !ObjectUtils.isEmpty(task.getTestMethodId())).map(EmcOrderTaskBean::getTestMethodId).distinct().collect(Collectors.toList());
        List<EmcItemGroupBean> itemGroupList = itemGroupService.getDao().selectListByOneColumnValues(testMethodIdList, "testMethodId", Arrays.asList("ID", "TESTMETHODID", "ITEMGROUP", "ACTIVATEDFLAG"));
        List<Long> itemGroupIdList = itemGroupList.stream().filter(group -> Constant.YES.equals(group.getActivatedFlag())).map(EmcItemGroupBean::getId).collect(Collectors.toList());
        List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID", Arrays.asList("ITEMGROUPID", "ITEMID"));
        Map<Long, String> itemGroupMap = itemGroupList.stream().filter(g -> !StringUtils.isEmpty(g.getItemGroup())).collect(Collectors.toMap(EmcItemGroupBean::getId, EmcItemGroupBean::getItemGroup, (v1, v2) -> v1));

        // 获取所有样品测试中的批号，并查询批列表
        List<EmcRunBean> runList = new ArrayList<>();
        if (!runIdList.isEmpty()) {
            runList = runService.selectListByFilter(SearchFilter.instance().match("Id", runIdList).filter(MatchPattern.OR));
        }

        // 查询需要在报告中显示的报告结果列表，此处只查询样品值、日均值、总均值结果
        List<String> dataTypeList = Arrays.asList(EmcConstant.PN_SAMPLE, EmcConstant.PN_DAYAVERAGE, EmcConstant.PN_TOTALAVERAGE);
        List<EmcReportResultBean> reportBeforeResultList = reportResultService.selectListByFilter(SearchFilter.instance().match("reportId", report.getId()).filter(MatchPattern.SEQ)
                .match("reportDisplay", "1").filter(MatchPattern.EQ)
                .match("DATATYPE", dataTypeList).filter(MatchPattern.OR));
        ArrayList<EmcReportResultBean> reportResultList = reportBeforeResultList.stream().collect(Collectors.collectingAndThen
                (Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(r -> r.getId()))), ArrayList::new));
        // 设置报告检出类型
        List<EmcTestMethodBean> testMethodList = testMethodService.getDao().selectListByOneColumnValues(testMethodIdList, "id", Arrays.asList("id", "reportLimitType"));
        testMethodList.forEach(testMethod -> reportResultList.stream().filter(r -> ObjectUtils.equals(r.getTestMethodId(), testMethod.getId())).forEach(r -> r.setExt$Item("reportLimitType", testMethod.getReportLimitType())));
        // 设置正常样结果分析项组
        reportResultList.stream().filter(r -> Constant.YES.equals(r.getReportDisplay())).forEach(r -> {
            groupItemList.stream().filter(g -> ObjectUtils.equals(r.getItemId(), g.getItemId())).findFirst().ifPresent(g -> r.setExt$Item("itemGroup", itemGroupMap.get(g.getItemGroupId())));
        });

        // 根据监测模板ID查询监测模板，通过监测模板组ID来区分水、气、土、声
        List<String> monitorTmplIdList = orderList.stream().filter(f -> !StringUtils.isEmpty(f.getMonitorTmplId())).map(EmcOrderBean::getMonitorTmplId).distinct().collect(Collectors.toList());
        List<EmcMonitorTmplBean> monitorTmplList = monitorTmplService.getDao().selectListByIds(monitorTmplIdList);

        // 通过采样单来查询采样单属性
        List<Long> sampleIdList = reportResultList.stream().map(EmcReportResultBean::getSampleId).distinct().collect(Collectors.toList());
        List<EmcPropertyDataBean> propertyDataList = propertyDataService.getDao().selectListByOneColumnValues(sampleIdList, "bizId");
        // 查询采样单对应的点位示意图
        List<String> targetIdList = sampleIdList.stream().map(i -> "T_EMC_SAMPLE$" + i).collect(Collectors.toList());
        List<CoreFileBean> coreFileList = new ArrayList<>();
        if (!targetIdList.isEmpty()) {
            coreFileList = coreFileService.selectListByFilter(SearchFilter.instance().match("TARGETID", targetIdList).filter(MatchPattern.OR).match("BIZCATEGORY", "photo").filter(MatchPattern.EQ));
        }

        // 查询当前项目下的所有报告数据
        List<EmcReportBean> reportList = getDao().selectListByOneColumnValue(project.getId(), "PROJID");

        // 查询采样设备
        List<String> sampleEquipmentIdList = orderTaskList.stream().filter(task -> !StringUtils.isEmpty(task.getSamplingEquiptId())).map(EmcOrderTaskBean::getSamplingEquiptId).collect(Collectors.toList());
        List<Long> equiptIdList = orderTaskList.stream().filter(task -> !ObjectUtils.isEmpty(task.getEquiptId())).map(EmcOrderTaskBean::getEquiptId).collect(Collectors.toList());
        List<Long> parsedEquiptIdList = parseMultiEquipt(sampleEquipmentIdList);
        parsedEquiptIdList.addAll(equiptIdList);
        // 称重设备
        List<String> weighingEquiptIdList = runList.stream().filter(run -> !StringUtils.isEmpty(run.getExt$Item("weighingEquiptId"))).map(run -> run.getExt$Item("weighingEquiptId")).distinct().collect(Collectors.toList());
        parsedEquiptIdList.addAll(parseMultiEquipt(weighingEquiptIdList));
        List<EmcEquiptBean> equiptList = equiptService.getDao().selectListByIds(parsedEquiptIdList, Arrays.asList("ID", "EQUIPTID", "EQUIPTNAME", "SPECIFICATION"));

        // 公司信息
        CoreUserBean loginUser = LocalContextHelper.getLoginUser();
        String parentOrgId = loginUser.getExt$Item("parentOrgId");
        CoreOrgBean coreOrg = coreOrgService.selectById(parentOrgId);

        // 查询公司的logo
        List<CoreFileBean> logoFileList = coreFileService.getDao().selectListByOneColumnValue("T_CORE_ORG$" + parentOrgId, "targetId");
        if (!logoFileList.isEmpty()) {
            logoFileList.forEach(file -> file.setExt$Item("orgLogo", Constant.YES));
            coreFileList.addAll(logoFileList);
        }

        // 查询二维码图片
        List<CoreFileBean> qrCodeFileList = coreFileService.selectListByFilter(SearchFilter.instance().match("targetId", "T_EMC_PROJECT$" + project.getId()).filter(MatchPattern.SEQ).match("bizCategory", Arrays.asList("qrCode", "photo")).filter(MatchPattern.OR));
        if (!qrCodeFileList.isEmpty()) {
            coreFileList.addAll(qrCodeFileList);
        }

        // 编码信息
        List<String> codeCategoryList = Collections.singletonList("testitemsort");
        List<CoreCodeBean> coreCodeList = coreCodeService.getDao().selectListByOneColumnValues(codeCategoryList, "codeCategoryId", Arrays.asList("id", "codeName", "codeCategoryId", "code"));

        // 前处理方式备注
        List<CoreCodeBean> pretreatmentCodeList = coreCodeService.getDao().selectListByOneColumnValue("pretreatmentRemark", "codeCategoryId");
        pretreatmentCodeList.stream().filter(c -> StringUtils.equals(c.getCode(), project.getPretreatment())).findFirst().ifPresent(c -> project.setPretreatment(c.getCodeName()));

        // 处理需要根据检测项目分类排序的报告结果
        Map<String, Long> testItemSortNumMap = coreCodeList.stream().filter(code -> StringUtils.equals(code.getCodeCategoryId(), "testitemsort")).collect(Collectors.toMap(CoreCodeBean::getCode, code -> Optional.ofNullable(code.getOrderNo()).orElse(code.getId()), (v1, v2) -> v1));
        Map<Long, EmcOrderTaskBean> hasItemSortTaskMap = orderTaskList.stream().filter(task -> !StringUtils.isEmpty(task.getExt$Item("testitemsort"))).collect(Collectors.toMap(EmcOrderTaskBean::getId, Function.identity(),(v1, v2) -> v1));
        Set<Long> hasItemSortTaskIdSet = hasItemSortTaskMap.keySet();
        orderTaskList.stream().filter(task -> hasItemSortTaskIdSet.contains(task.getId())).forEach(task -> task.setExt$Item("testItemSortNum", String.valueOf(testItemSortNumMap.get(task.getExt$Item("testItemSort")))));
        reportResultList.stream().filter(result -> hasItemSortTaskIdSet.contains(result.getOrderTaskId())).forEach(result -> {
            EmcOrderTaskBean orderTask = hasItemSortTaskMap.get(result.getOrderTaskId());
            result.setExt$Item("testItemSortNum", String.valueOf(testItemSortNumMap.get(orderTask.getExt$Item("testItemSort"))));
        });

        // 查询监测方案设置的排序
        List<EmcMonitorSchemeBean> monitorSchemeList = monitorSchemeService.selectListByFilter(SearchFilter.instance().match("monitorTmplId", monitorTmplIdList).filter(MatchPattern.OR)
                .match("schemeName", "default").filter(MatchPattern.SEQ).match("testMethodId", testMethodIdList).filter(MatchPattern.OR));
        List<EmcMonitorSchemeBean> hasSortNumSchemeList = monitorSchemeList.stream().filter(scheme -> !ObjectUtils.isEmpty(scheme.getSortNum())).collect(Collectors.toList());
        List<String> hasSortNumItemGroupList = hasSortNumSchemeList.stream().map(EmcMonitorSchemeBean::getItemGroup).distinct().collect(Collectors.toList());
        Map<String, Integer> itemGroupSortNumMap = hasSortNumSchemeList.stream().collect(Collectors.toMap(EmcMonitorSchemeBean::getItemGroup, EmcMonitorSchemeBean::getSortNum, (v1, v2) -> v1));
        orderTaskList.stream().filter(task -> hasSortNumItemGroupList.contains(task.getExt$Item("itemGroup"))).forEach(task -> task.setExt$Item("schemeSortNum", String.valueOf(itemGroupSortNumMap.get(task.getExt$Item("itemGroup")))));
        reportResultList.stream().filter(result -> hasSortNumItemGroupList.contains(result.getExt$Item("itemGroup"))).forEach(result -> result.setExt$Item("schemeSortNum", String.valueOf(itemGroupSortNumMap.get(result.getExt$Item("itemGroup")))));

        WordTemplateSourceDTO templateSourceDTO = new WordTemplateSourceDTO();
        templateSourceDTO.setProject(project);
        templateSourceDTO.setFolderList(folderList);
        templateSourceDTO.setSampleList(sampleList);
        templateSourceDTO.setOrderList(orderList);
        templateSourceDTO.setOrderTaskList(orderTaskList);
        templateSourceDTO.setOrderTaskResultList(orderTaskResultList);
        templateSourceDTO.setItemGroupList(itemGroupList);
        templateSourceDTO.setGroupItemList(groupItemList);
        templateSourceDTO.setRunList(runList);
        templateSourceDTO.setReport(report);
        templateSourceDTO.setReportList(reportList);
        templateSourceDTO.setReportResultList(reportResultList);
        templateSourceDTO.setMonitorTmplList(monitorTmplList);
        templateSourceDTO.setPropertyDataList(propertyDataList);
        templateSourceDTO.setEquiptList(equiptList);
        templateSourceDTO.setCoreFileList(coreFileList);
        templateSourceDTO.setCoreOrg(coreOrg);
        templateSourceDTO.setCoreCodeList(coreCodeList);

        List<CrmOpportunityContractBean> opportunityContractList = opportunityContractService.getDao().selectListByOneColumnValue(project.getExt$Item("contractNo"), "contractNo", Arrays.asList("id", "opportunityId"));
        if (!opportunityContractList.isEmpty()) {
            CrmOpportunityMainBean crmOpportunityMain = opportunityMainService.getDao().selectById(opportunityContractList.get(0).getOpportunityId());
            templateSourceDTO.setOpportunityMain(crmOpportunityMain);
        }

        List<PfRunTempHumidityBean> filterTempList = new ArrayList<>(runIdList.size());
        runIdList.forEach(runId -> {
            PfRunTempHumidityBean filterTemp = new PfRunTempHumidityBean();
            filterTemp.setRunId(runId);
            filterTemp.setProjId(project.getId());
            filterTempList.add(filterTemp);
        });

        List<PfRunTempHumidityBean> tempHumidityList = tempHumidityService.getDao().selectList(filterTempList, Arrays.asList("runId", "projId"), Collections.emptyList());
        templateSourceDTO.setTempHumidityList(tempHumidityList);

        switch (templateFile.getName()) {
            case PfConstant.COMPLEX_WORD_REPORT_TEMPLATE:
                queryIndexCategory(templateSourceDTO);
                if (!Constant.YES.equals(project.getSample())) {
                    templateSourceDTO.getFolderList().forEach(folder -> templateSourceDTO.getOrderList().stream().filter(order -> ObjectUtils.equals(order.getFolderId(), folder.getId())).findFirst().ifPresent(order -> folder.setMonitorTmplId(order.getMonitorTmplId())));
                }
                ComplexWordTemplate complexWordTemplate = new ComplexWordTemplate(document, templateSourceDTO);
                complexWordTemplate.generate();
                break;
            case PfConstant.QC_WORD_REPORT_TEMPLATE:
                initQcReportDataSource(templateSourceDTO);
                QualityControlWordTemplate qualityControlWordTemplate = new QualityControlWordTemplate(document, templateSourceDTO);
                qualityControlWordTemplate.generate();
                break;
            case PfConstant.DIOXIN_WORD_REPORT_TEMPLATE:
                // 查询QC指标
                queryIndexCategory(templateSourceDTO);

                DioxinWordTemplate2 dioxinWordTemplate = new DioxinWordTemplate2(document, templateSourceDTO);
                dioxinWordTemplate.generate();
                break;
            case PfConstant.DIOXIN_QC_WORD_REPORT_TEMPLATE:
                initDioxinQcReportDataSource(templateSourceDTO);
                DioxinQcWordTemplate dioxinQcWordTemplate = new DioxinQcWordTemplate(document, templateSourceDTO);
                dioxinQcWordTemplate.generate();
                break;
            default:
                break;
        }
        //templateSourceDTO有值后后清掉上面的list
        folderList.clear();
        sampleList.clear();
        orderList.clear();
        orderTaskList.clear();
        itemGroupList.clear();
        orderTaskResultList.clear();
        tempHumidityList.clear();
        propertyDataList.clear();
        reportResultList.clear();
        groupItemList.clear();
        coreFileList.clear();
        coreCodeList.clear();
    }

    @Override
    @Transactional
    public void createMergeReport(RestJsonWrapperBean wrapper) {
        List<EmcFolderBean> selectedFolderList = wrapper.parse(EmcFolderBean.class);
        List<EmcOrderTaskBean> selectedOrderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<Long> selectedTestIdList = selectedOrderTaskList.stream().map(EmcOrderTaskBean::getTestId).distinct().collect(Collectors.toList());
        //作为模板的项目
        Long originProjId = Long.parseLong(wrapper.getFilterValue("projId"));
        EmcProjectBean projOriginBean = projectService.getDao().selectById(originProjId);

        if(!selectedFolderList.isEmpty()){
            List<Long> folderIdList = selectedFolderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
            EmcProjectBean project = projectService.selectById(selectedFolderList.get(0).getProjId());

            EmcReportBean newReport = new EmcReportBean();
            newReport.setId(ApplicationContextHelper.getNextIdentity());
            newReport.setProjId(originProjId);
            newReport.setStatus(EmcBizConstant.REPORT_STATUS_REPORTEDIT);
            newReport.setExt$Item("mergeflag","1");
            newReport.setReportNo(projOriginBean.getProjNo());

            // 如果未选择测试，则默认出所选点位下的所有测试
            List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(folderIdList, "folderId", Arrays.asList("id", "projId", "folderId", "orderId", "testId"));
            List<EmcOrderTaskBean> validOrderTaskList = selectedTestIdList.isEmpty() ? orderTaskList : orderTaskList.stream().filter(task -> selectedTestIdList.contains(task.getTestId())).collect(Collectors.toList());
            List<EmcReportLineBean> reportLineList = new ArrayList<>(validOrderTaskList.size());

            validOrderTaskList.forEach(task -> {
                EmcReportLineBean reportLine = new EmcReportLineBean();
                reportLine.setId(ApplicationContextHelper.getNextIdentity());
                reportLine.setReportId(newReport.getId());
                reportLine.setProjId(task.getProjId());
                reportLine.setFolderId(task.getFolderId());
                reportLine.setOrderId(task.getOrderId());
                reportLine.setOrderTaskId(task.getId());
                reportLineList.add(reportLine);
            });

            getDao().insert(newReport);
            reportLineService.getDao().insert(reportLineList);
        }else{
            return;
        }

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void generateMergeReportResult(RestJsonWrapperBean wrapper) {
        EmcReportBean report = wrapper.parseUnique(EmcReportBean.class);
        //根据报告的id来的
        List<EmcReportLineBean> reportLineList = reportLineService.getDao().selectListByOneColumnValue(report.getId(), "reportId");

        // 查询需要在报告中展示的样品结果数据
        List<Long> orderTaskIdList = reportLineList.stream().map(EmcReportLineBean::getOrderTaskId).collect(Collectors.toList());
        if (orderTaskIdList.isEmpty()) {
            return;
        }

        // 删除原有的报告结果数据
        EmcReportResultBean deletedReportResult = new EmcReportResultBean();
        deletedReportResult.setReportId(report.getId());
        reportResultService.getDao().deleteBy(Collections.singletonList(deletedReportResult), "reportId");

        // 过滤掉分析录入合并的测试
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderTaskIdList, "id", Arrays.asList("id", "virtual"));
        List<Long> validOrderTaskIdList = orderTaskList.stream().filter(task -> !StringUtils.equals(task.getExt$Item("virtual"), Constant.YES)).map(EmcOrderTaskBean::getId).collect(Collectors.toList());

        if(validOrderTaskIdList.size()>0) {
            List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.selectListByFilter(SearchFilter.instance()
                    .match("orderTaskId", validOrderTaskIdList).filter(MatchPattern.OR)
                    .match("REPORTDISPLAY", Constant.YES).filter(MatchPattern.SEQ)
                    .match("RESULTSTATUS", EmcBizConstant.ORDERTASKRESULT_STATUS_DONE).filter(MatchPattern.SEQ));

            if (orderTaskResultList.isEmpty()) {
                return;
            }

            List<Long> orderIdList = reportLineList.stream().map(EmcReportLineBean::getOrderId).distinct().collect(Collectors.toList());
            List<EmcOrderBean> orderList = orderService.selectListByFilter(SearchFilter.instance().match("id", orderIdList).filter(MatchPattern.OR));
            Map<Long, EmcOrderBean> orderIdMap = orderList.stream().collect(Collectors.toMap(EmcOrderBean::getId, Function.identity()));

            // 查询分析项及修约规则
            List<Long> itemIdList = orderTaskResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
            List<EmcItemRevisionBean> itemRevisionList = itemRevisionService.getDao().selectListByOneColumnValues(itemIdList, "ITEMID");
            List<EmcItemBean> itemList = itemService.getDao().selectListByIds(itemIdList);

            // 报告数据
            List<EmcReportResultBean> reportResultList = new ArrayList<>();

            // 判断报告数据是否包含油烟类型
            boolean existLampblack = orderList.stream().anyMatch(order -> StringUtils.contains(order.getOrderCategory(), EmcConstant.LAMPBLACK_ORDER_CATEGORY_KEYWORD));
            // 判定是否为二噁英报告，二噁英报告结果数据生成方式不一样
            String bizCategoryId = report.getExt$Item("bizCategoryId");
            boolean isDioxin = StringUtils.equalsIgnoreCase(bizCategoryId, "C");

            // 是否为采样项目
            boolean sampling = StringUtils.equals(Constant.YES, report.getExt$Item("sample"));

            // 计算样品值
            List<EmcOrderTaskResultBean> finalResultList = isDioxin ? calcDioxinParallelAverageValue(sampling, orderIdList, orderTaskResultList, itemRevisionList, itemList) : calcParallelAverageValue(sampling, orderIdList, orderTaskResultList, itemRevisionList, itemList);
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

            boolean calcStatistic = Constant.YES.equals(report.getIfStatistics());

            // 计算日均值
            if (calcStatistic) {
                Map<String, List<EmcOrderTaskResultBean>> totalTaskResultMap = finalResultList.stream().collect(Collectors.groupingBy(e -> e.getFolderId() + "$" + e.getItemId() + "$" + e.getExt$Item("testname")));
                totalTaskResultMap.forEach((key, resultList) -> {
                    resultList.forEach(result -> replaceNdHdValue(result, result.getReportResult()));


                    Map<String, List<EmcOrderTaskResultBean>> dayResultListMap = resultList.stream().collect(Collectors.groupingBy(e -> e.getFolderId() + "$" + e.getCycle()));
                    dayResultListMap.forEach((day, dayResultList) -> {
                        List<String> dayResultValueList = dayResultList.stream().map(EmcOrderTaskResultBean::getReportResult).collect(Collectors.toList());

                        dayResultValueList.stream().filter(NumberUtils::isNumber).mapToDouble(NumberUtils::parseDouble).average().ifPresent(avgResult -> {
                            EmcOrderTaskResultBean totalResult = new EmcOrderTaskResultBean();
                            BeanUtils.copyProperties(dayResultList.get(0), totalResult);
                            totalResult.setOriginalResult(NumberUtils.formatNumber(avgResult));

                            // 对均值原始结果进行修约
                            reviseOriginalValue(totalResult, itemRevisionList, itemList);

                            EmcReportResultBean reportResult = new EmcReportResultBean();
                            reportResult.setId(ApplicationContextHelper.getNextIdentity());
                            reportResult.setReportId(report.getId());
                            reportResult.setDataType(EmcConstant.PN_DAYAVERAGE);
                            EmcOrderBean currentOrder = orderIdMap.get(dayResultList.get(0).getOrderId());

                            copyProperties(reportResult, currentOrder, totalResult);
                            reportResultList.add(reportResult);
                        });
                    });
                });
            }

            // 计算总均值
            List<Long> invalidLampblackResultIdList = new ArrayList<>();
            if (calcStatistic) {
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
        }

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public String cancelCompleteReport(RestJsonWrapperBean wrapper){
        return null;
    }

    @Override
    public EmcReportSendInfoBean selectSendInfo(Long projId) {
        List<EmcReportSendInfoBean> sendInfoList = reportSendInfoService.selectListByFilter(SearchFilter.instance().match("projId", projId).filter(MatchPattern.SEQ));
        if (sendInfoList.isEmpty()) {
            EmcReportSendInfoBean reportSendInfo = new EmcReportSendInfoBean();
            reportSendInfo.setProjId(projId);
            reportSendInfo.setId(ApplicationContextHelper.getNextIdentity());
            reportSendInfoService.getDao().insert(reportSendInfo);

            return reportSendInfoService.selectFirstByFilter(SearchFilter.instance().match("projId", projId).filter(MatchPattern.SEQ));
        }

        return sendInfoList.get(0);
    }

    private void initQcReportDataSource(WordTemplateSourceDTO templateSourceDTO) {
        List<EmcOrderBean> orderList = templateSourceDTO.getOrderList();
        List<EmcOrderTaskBean> orderTaskList = templateSourceDTO.getOrderTaskList();

        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<String> orderNoList = orderList.stream().map(EmcOrderBean::getOrderNo).collect(Collectors.toList());

        // 查询现场质控
        EmcOrderBean filterOrder = new EmcOrderBean();
        filterOrder.setProjId(orderList.get(0).getProjId());
        List<EmcOrderBean> projectOrderList = orderService.getDao().selectList(filterOrder, Collections.singletonList("projId"), Collections.emptyList());
        List<EmcOrderBean> samplingQcOrderList = projectOrderList.stream().filter(order -> !ObjectUtils.isEmpty(order.getQcCategoryId())).collect(Collectors.toList());
        List<Long> samplingQcOrderIdList = samplingQcOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<String> samplingQcOrderNoList = samplingQcOrderList.stream().map(EmcOrderBean::getOrderNo).collect(Collectors.toList());

        orderIdList.addAll(samplingQcOrderIdList);
        orderNoList.addAll(samplingQcOrderNoList);

        List<EmcOrderTaskBean> samplingTaskBeforeList = new ArrayList<>();
        if (!samplingQcOrderIdList.isEmpty()) {
            samplingTaskBeforeList = orderTaskService.selectListByFilter(SearchFilter.instance().match("orderId", samplingQcOrderIdList).filter(MatchPattern.OR));
        }
        List<EmcOrderTaskResultBean> samplingResultList = orderTaskResultService.getDao().selectListByOneColumnValues(samplingQcOrderIdList, "orderId");
        List<Long> existQcOrderTaskIdList = samplingResultList.stream().map(EmcOrderTaskResultBean :: getOrderTaskId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> samplingTaskList = samplingTaskBeforeList.stream().filter(ok ->existQcOrderTaskIdList.contains(ok.getId())).collect(Collectors.toList());
        // 查询样品关联的实验室质控样品
        List<EmcQcRelatedOrderBean> qcRelatedOrderList = qcRelatedOrderService.getDao().selectListByOneColumnValues(orderIdList, "relatedOrderId");
        List<Long> qcRelatedOrderIdList = qcRelatedOrderList.stream().map(EmcQcRelatedOrderBean::getQcOrderId).distinct().collect(Collectors.toList());

        // 查询该项目下的质控样品
        List<Long> orderTaskIdList = orderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<Long> testMethodIdList = orderTaskList.stream().map(EmcOrderTaskBean::getTestMethodId).collect(Collectors.toList());

        List<String> runIdList = orderTaskList.stream().filter(ok ->!StringUtils.isEmpty(ok.getRunId())).map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> runTaskList = new ArrayList<>();
        if (!runIdList.isEmpty()) {
            runTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("runId", runIdList).filter(MatchPattern.OR));
        }
        List<EmcOrderTaskBean> runQcTaskBeforeList = runTaskList.stream().filter(task -> !ObjectUtils.isEmpty(task.getQcCategoryId())
                && !orderTaskIdList.contains(task.getId())
                && testMethodIdList.contains(task.getTestMethodId())).collect(Collectors.toList());
                //&& qcRelatedOrderIdList.contains(task.getOrderId())).collect(Collectors.toList());

        List<Long> runQcOrderIdList = runQcTaskBeforeList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> runQcOrderList = orderService.getDao().selectListByIds(runQcOrderIdList);

        List<Long> runQcTaskIdList = runQcTaskBeforeList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> runQcResultList = orderTaskResultService.getDao().selectListByOneColumnValues(runQcTaskIdList, "orderTaskId");

        List<Long> existRunQcOrderTaskIdList = runQcResultList.stream().map(EmcOrderTaskResultBean :: getOrderTaskId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> runQcTaskList = runQcTaskBeforeList.stream().filter(ok ->existRunQcOrderTaskIdList.contains(ok.getId())).collect(Collectors.toList());

        // 查询有父样的质控，并找出不是本项目的样品、测试和结果
        List<String> parentOrderNoList = runQcOrderList.stream().filter(qcOrder -> !StringUtils.isEmpty(qcOrder.getParentOrderNo())).map(EmcOrderBean::getParentOrderNo).distinct().collect(Collectors.toList());
        List<String> otherProjectOrderNoList = parentOrderNoList.stream().filter(orderNo -> !orderNoList.contains(orderNo)).collect(Collectors.toList());

        List<EmcOrderBean> otherProjectOrderList = orderService.getDao().selectListByOneColumnValues(otherProjectOrderNoList, "orderNo");
        List<Long> otherProjectOrderIdList = otherProjectOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> otherOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(otherProjectOrderIdList, "orderId");
        List<EmcOrderTaskBean> validOtherOrderTaskList = otherOrderTaskList.stream().filter(task -> testMethodIdList.contains(task.getTestMethodId())).collect(Collectors.toList());
        List<Long> validOtherOrderTaskIdList = validOtherOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> validOtherResultList = orderTaskResultService.getDao().selectListByOneColumnValues(validOtherOrderTaskIdList, "orderTaskId");

        otherProjectOrderList.forEach(order -> order.setExt$Item("other", Constant.YES));
        validOtherOrderTaskList.forEach(task -> task.setExt$Item("other", Constant.YES));
        validOtherResultList.forEach(result -> result.setExt$Item("other", Constant.YES));

        // 设置正常样品数据，可能包含其他项目的数据
        List<EmcOrderBean> reportOrderList = new ArrayList<>(orderList);
        reportOrderList.addAll(otherProjectOrderList);
        List<EmcOrderTaskBean> reportTaskList = new ArrayList<>(orderTaskList);
        reportTaskList.addAll(validOtherOrderTaskList);
        List<EmcOrderTaskResultBean> reportResultList = new ArrayList<>(templateSourceDTO.getOrderTaskResultList());
        reportResultList.addAll(validOtherResultList);

        templateSourceDTO.setOrderList(reportOrderList);
        templateSourceDTO.setOrderTaskList(reportTaskList);

        // 设置质控数据
        List<EmcOrderBean> qcOrderList = new ArrayList<>(samplingQcOrderList);
        qcOrderList.addAll(runQcOrderList);
        List<EmcOrderTaskBean> qcOrderTaskList = new ArrayList<>(samplingTaskList);
        qcOrderTaskList.addAll(runQcTaskList);
        List<EmcOrderTaskResultBean> qcTaskResultList = new ArrayList<>(samplingResultList);
        qcTaskResultList.addAll(runQcResultList);

        // 查询实验室标准样品批号
        List<String> stockBatchNoList = qcOrderTaskList.stream().filter(task -> !StringUtils.isEmpty(task.getStockBatchNo())).map(EmcOrderTaskBean::getStockBatchNo).distinct().collect(Collectors.toList());
        if (!stockBatchNoList.isEmpty()) {
            List<EmcStockBean> stockList = stockService.getDao().selectListByOneColumnValues(stockBatchNoList, "batchNo", Arrays.asList("id", "batchNo", "standardLevel"));
            List<EmcStockBean> validStockList = stockList.stream().filter(stock -> !StringUtils.isEmpty(stock.getExt$Item("standardLevel"))).collect(Collectors.toList());
            Map<String, String> stockBatchNoMap = validStockList.stream().collect(Collectors.toMap(EmcStockBean::getBatchNo, stock -> stock.getExt$Item("standardLevel"), (v1, v2) -> v1));
            qcOrderTaskList.stream().filter(task -> !StringUtils.isEmpty(task.getStockBatchNo())).forEach(task -> task.setExt$Item("uniqueNo", stockBatchNoMap.get(task.getStockBatchNo())));
        }

        // 处理实验室标准样品的单位转换问题，标准值单位存于rn12字段，如果和结果单位不一致，则需要进行转换
        List<EmcOrderTaskResultBean> differentUnitResultList = qcTaskResultList.stream().filter(r -> PfConstant.QCCATEGORY_QCCATEGORYID_QC_STD.equals(r.getQcCategoryCode()) && !StringUtils.isEmpty(r.getRn12()) && !StringUtils.equals(r.getUnitName(), r.getRn12()) && NumberUtils.isNumber(r.getReportResult())).collect(Collectors.toList());
        if (!differentUnitResultList.isEmpty()) {
            Map<String, List<Pair<String, Double>>> unitConvertMap = queryUnitConvert();
            qcTaskResultList.stream().filter(r -> PfConstant.QCCATEGORY_QCCATEGORYID_QC_STD.equals(r.getQcCategoryCode())
                    && !StringUtils.isEmpty(r.getRn12())
                    && !StringUtils.equals(r.getUnitName(), r.getRn12())
                    && NumberUtils.isNumber(r.getReportResult())).forEach(r -> {

                // 单位换算
                String finalValue = WordTemplateUtils.convertUnit(unitConvertMap, r.getUnitName(), r.getReportResult(), r.getRn12());
                r.setReportResult(finalValue);
            });
        }

        ArrayList<EmcOrderBean> uniqueQcOrderList = qcOrderList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmcOrderBean::getId))), ArrayList::new));
        ArrayList<EmcOrderTaskBean> uniqueQcOrderTaskList = qcOrderTaskList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmcOrderTaskBean::getId))), ArrayList::new));
        ArrayList<EmcOrderTaskResultBean> uniqueQcOrderTaskResultList = qcTaskResultList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmcOrderTaskResultBean::getId))), ArrayList::new));

        templateSourceDTO.setQcOrderList(uniqueQcOrderList);
        templateSourceDTO.setQcOrderTaskList(uniqueQcOrderTaskList);

        // 设置质控样结果分析项组
        List<EmcItemGroupBean> itemGroupList = templateSourceDTO.getItemGroupList();
        Map<Long, String> itemGroupMap = itemGroupList.stream().filter(g -> !StringUtils.isEmpty(g.getItemGroup())).collect(Collectors.toMap(EmcItemGroupBean::getId, EmcItemGroupBean::getItemGroup, (v1, v2) -> v1));
        List<EmcGroupItemBean> groupItemList = templateSourceDTO.getGroupItemList();
        uniqueQcOrderTaskResultList.forEach(r -> {
            groupItemList.stream().filter(g -> ObjectUtils.equals(r.getItemId(), g.getItemId())).findFirst().ifPresent(g -> r.setExt$Item("itemGroup", itemGroupMap.get(g.getItemGroupId())));
        });
        //templateSourceDTO.setQcTaskResultList(uniqueQcOrderTaskResultList);

        // 设置正常样结果分析项组
        reportResultList.stream().filter(r -> Constant.YES.equals(r.getReportDisplay())).forEach(r -> {
            groupItemList.stream().filter(g -> ObjectUtils.equals(r.getItemId(), g.getItemId())).findFirst().ifPresent(g -> r.setExt$Item("itemGroup", itemGroupMap.get(g.getItemGroupId())));
        });
        // 设置质控样结果测试名称
        List<Long> testIdQcSearchList = uniqueQcOrderTaskResultList.stream().map(EmcOrderTaskResultBean :: getTestId).distinct().collect(Collectors.toList());
        List<Long> testIdSearchList = reportResultList.stream().map(EmcOrderTaskResultBean :: getTestId).distinct().collect(Collectors.toList());
        List<EmcTestBean> testMethodQcSearchList = testService.selectListByIds(testIdQcSearchList);
        List<EmcTestBean> testMethodSearchList = testService.selectListByIds(testIdSearchList);

        uniqueQcOrderTaskResultList.stream().filter(r -> Constant.YES.equals(r.getReportDisplay())).forEach(r -> {
        	String testName = testMethodQcSearchList.stream().filter(tm ->ObjectUtils.equals(r.getTestId(),tm.getId())).collect(Collectors.toList()).get(0).getTestName();
        	r.setExt$Item("testName", testName);
        });
        templateSourceDTO.setQcTaskResultList(uniqueQcOrderTaskResultList);
        // 设置正常样样品测试
        reportResultList.stream().filter(r -> Constant.YES.equals(r.getReportDisplay())).forEach(r -> {
        	String testName = testMethodSearchList.stream().filter(tm ->ObjectUtils.equals(r.getTestId(),tm.getId())).collect(Collectors.toList()).get(0).getTestName();
        	r.setExt$Item("testName", testName);
        });
        templateSourceDTO.setOrderTaskResultList(reportResultList);

        templateSourceDTO.setQcRelatedOrderList(qcRelatedOrderList);

        // 查询分析项QC指标
        queryItemQcIndex(templateSourceDTO);

        // 查询评价指标
        queryIndexCategory(templateSourceDTO);

        // 查询仪器检定日期
        queryEquiptPerCheck(templateSourceDTO);
    }

    private void initDioxinQcReportDataSource(WordTemplateSourceDTO templateSourceDTO) {
        // 质控样品测试结果
        List<EmcOrderTaskBean> orderTaskList = templateSourceDTO.getOrderTaskList();
        List<EmcOrderTaskBean> validOrderTaskList = orderTaskList.stream().filter(r -> !StringUtils.contains(r.getTestName(), EmcConstant.PARAMETER_TEST_MATCH_KEYWORD)
                && !StringUtils.contains(r.getTestName(), PfConstant.WATER_CONTENT_TEST_MATCH_KEYWORD)
                && !StringUtils.contains(r.getExt$Item("relatedTest"), Constant.YES)).collect(Collectors.toList());

        List<String> runIdList = validOrderTaskList.stream().filter(a -> !StringUtils.isEmpty(a.getRunId())).map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> qcOrderTaskBeforeList = runIdList.isEmpty() ? Collections.emptyList() : orderTaskService.selectListByFilter(SearchFilter.instance().match("runId", runIdList).filter(MatchPattern.OR).match("qcCategoryCode", EmcConstant.N_A).filter(MatchPattern.DIFFER));
        List<Long> qcOrderTaskIdList = qcOrderTaskBeforeList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());

        List<EmcOrderTaskResultBean> qcTaskResultList = qcOrderTaskIdList.isEmpty() ? Collections.emptyList() : orderTaskResultService.selectListByFilter(SearchFilter.instance().match("orderTaskId", qcOrderTaskIdList).filter(MatchPattern.OR));
        List<Long> existQcOrderTaskIdList = qcTaskResultList.stream().map(EmcOrderTaskResultBean :: getOrderTaskId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> qcOrderTaskList = qcOrderTaskBeforeList.stream().filter(ok ->existQcOrderTaskIdList.contains(ok.getId())).collect(Collectors.toList());
        List<Long> qcOrderIdList = qcOrderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> qcOrderList = orderService.getDao().selectListByIds(qcOrderIdList);

        templateSourceDTO.setQcOrderList(qcOrderList);
        templateSourceDTO.setQcOrderTaskList(qcOrderTaskList);
        templateSourceDTO.setQcTaskResultList(qcTaskResultList);
    }

    private void queryItemQcIndex(WordTemplateSourceDTO templateSourceDTO) {
        List<EmcOrderTaskResultBean> qcTaskResultList = templateSourceDTO.getQcTaskResultList();
        List<Long> itemIdList = qcTaskResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
        List<EmcItemQcIndexBean> itemQcIndexList = itemQcIndexService.getDao().selectListByOneColumnValues(itemIdList, "itemId", Arrays.asList("id", "itemId", "qcCategoryId", "DECISIONCATEGORY"));
        templateSourceDTO.setItemQcIndexList(itemQcIndexList);
    }

    private void queryIndexCategory(WordTemplateSourceDTO templateSourceDTO) {
        // 查询评价标准信息
        List<EmcOrderTaskBean> orderTaskList = templateSourceDTO.getOrderTaskList();
        List<Long> indexCategoryIdList = orderTaskList.stream().filter(task -> !ObjectUtils.isEmpty(task.getIndexCategoryId())).map(EmcOrderTaskBean::getIndexCategoryId).distinct().collect(Collectors.toList());
        List<EmcIndexCategoryBean> indexCategoryList = indexCategoryService.getDao().selectListByOneColumnValues(indexCategoryIdList, "id", Arrays.asList("id", "stdName"));
        templateSourceDTO.setIndexCategoryList(indexCategoryList);
    }

    private void queryEquiptPerCheck(WordTemplateSourceDTO templateSourceDTO) {
        List<EmcOrderTaskBean> orderTaskList = templateSourceDTO.getOrderTaskList();
        List<Long> equiptIdList = orderTaskList.stream().filter(task -> !ObjectUtils.isEmpty(task.getEquiptId())).map(EmcOrderTaskBean::getEquiptId).distinct().collect(Collectors.toList());
        List<EmcEquiptPerCheckPlanBean> checkPlanList = perCheckPlanService.getDao().selectListByOneColumnValues(equiptIdList, "equiptId");

        List<EmcEquiptPerCheckPlanBean> validCheckPlanList = checkPlanList.stream().filter(plan -> !ObjectUtils.isEmpty(plan.getMaintEndDate())).collect(Collectors.toList());
        Map<Long, String> checkPlanMap = validCheckPlanList.stream().collect(Collectors.toMap(EmcEquiptPerCheckPlanBean::getEquiptId, plan -> DateTimeUtils.formatLocalDateTime(plan.getMaintEndDate()), (v1, v2) -> v1));

        orderTaskList.forEach(task -> Optional.ofNullable(checkPlanMap.get(task.getEquiptId())).ifPresent(date -> task.setExt$Item("checkDate", date)));

        templateSourceDTO.setOrderTaskList(orderTaskList);
    }

    private Map<String, List<Pair<String, Double>>> queryUnitConvert() {
        Map<String, List<Pair<String, Double>>> convertMap = new HashMap<>();

        List<EmcUnitConvertBean> unitConvertList = unitConvertService.getDao().selectAll();
        List<EmcUnitConvertBean> validUnitConvertList = unitConvertList.stream().filter(uc -> !StringUtils.isEmpty(uc.getUnitId())
                && !StringUtils.isEmpty(uc.getConvertUnitId()) && !ObjectUtils.isEmpty(uc.getConvertFactor())).collect(Collectors.toList());
        List<String> sourceUnitIdList = validUnitConvertList.stream().map(EmcUnitConvertBean::getUnitId).distinct().collect(Collectors.toList());
        List<String> targetUnitIdList = validUnitConvertList.stream().map(EmcUnitConvertBean::getConvertUnitId).distinct().collect(Collectors.toList());

        List<String> unitIdList = new ArrayList<>(sourceUnitIdList);
        unitIdList.addAll(targetUnitIdList);
        List<EmcUnitBean> unitList = unitService.selectListByIds(unitIdList);
        Map<String, EmcUnitBean> unitMap = unitList.stream().collect(Collectors.toMap(EmcUnitBean::getId, Function.identity()));

        Map<String, List<EmcUnitConvertBean>> unitConvertMap = validUnitConvertList.stream().collect(Collectors.groupingBy(EmcUnitConvertBean::getUnitId));
        unitConvertMap.forEach((sourceUnitId, curConvertList) -> Optional.ofNullable(unitMap.get(sourceUnitId)).ifPresent(unit -> {
            List<Pair<String, Double>> convertList = new ArrayList<>();
            curConvertList.forEach(uc -> Optional.ofNullable(unitMap.get(uc.getConvertUnitId())).ifPresent(convertUnit -> {
                convertList.add(Pair.of(convertUnit.getUnitName(), uc.getConvertFactor()));
            }));
            convertMap.put(unit.getUnitName(), convertList);
        }));

        return convertMap;
    }

    @Override
    public EmcReportRecordDTO selectReportRecordDTO(RestJsonWrapperBean wrapper) {
        EmcReportRecordDTO reportRecordDTO = new EmcReportRecordDTO();
        String reportId = wrapper.getParamValue("reportId");

        String targetId = getDao().getTable() + "$" + reportId;
        CoreFileBean wordReportFile = coreFileService.selectFirstByFilter(SearchFilter.instance().match("targetId",
                targetId).filter(MatchPattern.SEQ).match("bizCategory", EmcConstant.WORD_REPORT_ATTACHMENT_TYPE).filter(MatchPattern.EQ));
        reportRecordDTO.setReportFile(wordReportFile);

        List<CoreFileBean> otherRecordList = new ArrayList<>();

        List<CoreFileBean> analysisRecordList = coreFileService.selectListByFilter(SearchFilter.instance().match("targetId",
                targetId).filter(MatchPattern.SEQ).match("ext", "analysisRecordPdf").filter(MatchPattern.EQ));
        otherRecordList.addAll(analysisRecordList);

        List<CoreFileBean> sampleRecordList = coreFileService.selectListByFilter(SearchFilter.instance().match("targetId",
                targetId).filter(MatchPattern.SEQ).match("ext", "sampleRecordPdf").filter(MatchPattern.EQ));
        otherRecordList.addAll(sampleRecordList);
        reportRecordDTO.setOtherRecordList(otherRecordList);

        return reportRecordDTO;
    }

    @Override
    public Page<EmcReportSendInfoBean> selectSendInfoPagination(RestJsonWrapperBean wrapper) {
        return reportSendInfoService.selectPagination(wrapper);
    }

    @Override
    public CoreFileBean composeProjectRunRecord(RestJsonWrapperBean wrapper) {
        EmcProjectBean project = wrapper.parseUnique(EmcProjectBean.class);

        //如果已经存在，直接返回
        CoreFileBean fileFilter = new CoreFileBean();
        fileFilter.setTargetId("T_EMC_PROJECT$" + project.getId());
        fileFilter.setExt("analysisRecordPdf");

        List<CoreFileBean> projectRunRecordFiles = coreFileService.getDao().selectList(fileFilter, Arrays.asList("targetId", "ext"), Collections.emptyList());
        if (!projectRunRecordFiles.isEmpty()) {
            return projectRunRecordFiles.get(0);
        }

        EmcOrderTaskBean orderTaskFilter = new EmcOrderTaskBean();
        orderTaskFilter.setProjId(project.getId());
        orderTaskFilter.setAnalysisType(EmcBizConstant.ANALYSISCATEGORY_ANALYSIS);
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectList(orderTaskFilter, Arrays.asList("projId", "analysisType"), Collections.emptyList());

        //按照项目和批号分组
        List<EmcOrderTaskBean> distinctOrderTaskList = orderTaskList.stream().filter(e -> !ObjectUtils.isEmpty(e.getProjId()) && !ObjectUtils.isEmpty(e.getRunId())).collect(Collectors
                .collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getProjId() + "#" + o.getRunId()))), ArrayList::new));

        List<CoreFileBean> fileFilters = new ArrayList<>();
        distinctOrderTaskList.forEach(r -> {
            CoreFileBean coreFileBean = new CoreFileBean();
            coreFileBean.setTargetId("T_EMC_RUN" + "$" + r.getRunId()+"-"+r.getProjId());
            coreFileBean.setExt("composePdf");
            fileFilters.add(coreFileBean);
        });

        List<CoreFileBean> allRunFileList = coreFileService.getDao().selectList(fileFilters, Arrays.asList("TARGETID", "EXT"), Collections.emptyList());
        if (ListUtils.isEmpty(allRunFileList)) {
            return null;
        }

        List<FilePathDTO> fileRepresentations = allRunFileList.stream().map(CoreFileUtils::toFilePath).collect(Collectors.toList());
        List<FilePathDTO> validFileRepresentations = fileRepresentations.stream().filter(fr -> FilePathManager.getLocalDirPath(fr).toFile().exists()).collect(Collectors.toList());
        if (validFileRepresentations.isEmpty()) {
            return null;
        }

        String projectReportPdfName = "分析记录-" + project.getProjNo() + ".pdf";
        FilePathDTO composedPdfFile = FilePathDTO.of(FileScope.temp.name(), "" + System.currentTimeMillis(), StringUtils.randomUUID(8), projectReportPdfName);
        coreFileService.mergePdfList(validFileRepresentations, composedPdfFile);
        String tempFilePath = FilePathManager.getLocalPath(composedPdfFile).toString();
        // 保存文件信息到 t_core_file 表
        if (!StringUtils.isEmpty(tempFilePath)) {
            CoreFileBean coreFileBean = new CoreFileBean();
            coreFileBean.setTargetId("T_EMC_PROJECT" + "$" + project.getId());
            coreFileBean.setScope(FileScope.secure.name());
            coreFileBean.setExt("analysisRecordPdf");
            Long fileId = coreFileService.upload(coreFileBean, new File(tempFilePath));

            return coreFileService.getDao().selectById(fileId);
        }

        return null;
    }
    @Override
    public Long viewNewReport(RestJsonWrapperBean wrapper) {
        return viewReport(wrapper);
    }

    @Override
    public Long projectUndo(RestJsonWrapperBean wrapper) {
        return 0L;
    }

    @Override
    @Transactional
    public void seal(RestJsonWrapperBean wrapper) {
        List<CoreFileBean> coreFileList = wrapper.parse(CoreFileBean.class);
        seal(coreFileList);
    }

    private void seal(List<CoreFileBean> coreFileList) {
        List<CoreFileBean> fileFilters = new ArrayList<>();
        coreFileList.forEach(file -> {
            CoreFileBean filter = new CoreFileBean();
            filter.setTargetId(file.getTargetId());
            filter.setExt("sealPdf");
            fileFilters.add(filter);
        });
        coreFileService.getDao().deleteBy(fileFilters, "TARGETID", "EXT");
        for (CoreFileBean file : coreFileList) {
            File originalFile = CoreFileUtils.getLocalPath(file).toFile();
            FilePathDTO archivePdf = CoreFileUtils.toFilePath(FileScope.temp,
                    FileUtils.getFileBaseName(file.getName()) + "-盖章.pdf");
            Path sealPath = FilePathManager.getLocalPath(archivePdf);
            FileUtils.makeDirs(sealPath);
            FileSealUtils.seal(originalFile.toString(), sealPath.toString(), true, originalFile.getName());
            CoreFileBean sealedFile = new CoreFileBean();
            sealedFile.setTargetId(file.getTargetId());
            sealedFile.setScope(FileScope.secure.name());
            sealedFile.setExt("sealPdf");
            coreFileService.upload(sealedFile, sealPath.toFile());
        }
    }

    @Override
    public void signIssuePdfIfRequired(CoreFileBean pdfFile) {
        Long reportId = parseReportId(pdfFile);
        if (reportId == null) {
            return;
        }
        EmcReportBean report = getDao().selectByIdIfPresent(reportId);
        if (report == null || !isQysIssueSignMode(report)) {
            return;
        }
        CoreBpmnCommentBean issueComment = selectLatestReportIssueComment(reportId);
        if (issueComment == null) {
            throw new InvalidDataException("未找到报告签发记录，无法进行契约锁报告签发章签署");
        }
        CoreUserBean issuer = coreUserService.selectByIdIfPresent(issueComment.getCreatedById());
        Long sealId = getIssueSealId(issuer);
        fileManager.download(pdfFile);
        File originalFile = CoreFileUtils.getLocalPath(pdfFile).toFile();
        FilePathDTO signedPdf = CoreFileUtils.toFilePath(FileScope.temp,
                FileUtils.getFileBaseName(pdfFile.getName()) + "-issue-sign.pdf");
        Path signedPath = FilePathManager.getLocalPath(signedPdf);
        FileUtils.makeDirs(signedPath);
        FileSealUtils.signReportIssue(originalFile.toString(), signedPath.toString(), sealId);
        fileManager.upload(CoreFileUtils.toFilePath(pdfFile), signedPath.toFile());
        pdfFile.setSize(signedPath.toFile().length());
        coreFileService.getDao().update(pdfFile, "SIZE");
        fileManager.download(pdfFile);
    }

    private Long parseReportId(CoreFileBean pdfFile) {
        if (pdfFile == null || !StringUtils.equals("wordReportPdf", pdfFile.getExt())) {
            return null;
        }
        String targetId = pdfFile.getTargetId();
        String prefix = getDao().getTable() + "$";
        if (StringUtils.isBlank(targetId) || !targetId.startsWith(prefix)) {
            return null;
        }
        String reportIdText = targetId.substring(prefix.length());
        return NumberUtils.isNumber(reportIdText) ? NumberUtils.parseLong(reportIdText) : null;
    }

    private CoreBpmnCommentBean selectLatestReportIssueComment(Long reportId) {
        return bpmnCommentService.selectListByFilter(SearchFilter.instance()
                .match("targetId", getDao().getTable() + "$" + reportId).filter(MatchPattern.EQ)
                .match("status", "REJECT").filter(MatchPattern.DIFFER)).stream()
                .filter(comment -> ObjectUtils.equals("reportIssue", comment.getStatusCode()))
                .sorted(Comparator.comparing(CoreBpmnCommentBean::getCreatedTime).reversed())
                .limit(1).findFirst().orElse(null);
    }

    private boolean isQysIssueSignMode(EmcReportBean report) {
        return !isPictureIssueSignMode(report);
    }

    private boolean isPictureIssueSignMode(EmcReportBean report) {
        return report != null && ObjectUtils.equals("picture", report.getIssueSignMode());
    }

    private Long getIssueSealId(CoreUserBean issuer) {
        if (issuer == null) {
            throw new InvalidDataException("未找到报告签发人，无法进行契约锁报告签发章签署");
        }
        String sealId = issuer.getExt$Item("sealid");
        if (StringUtils.isBlank(sealId)) sealId = issuer.getExt$Item("sealId");
        if (StringUtils.isBlank(sealId)) sealId = issuer.getExt$Item("SEALID");
        if (StringUtils.isBlank(sealId)) {
            throw new InvalidDataException("签发人[" + issuer.getUserName() + "]未维护契约锁签章ID");
        }
        if (!NumberUtils.isNumber(sealId)) {
            throw new InvalidDataException("签发人[" + issuer.getUserName() + "]维护的契约锁签章ID不是有效数字");
        }
        return NumberUtils.parseLong(sealId);
    }

    @Override
    @Transactional
    public void editItem(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskResultBean> requestedResults = wrapper.parse(EmcOrderTaskResultBean.class);
        List<EmcItemBean> items = wrapper.parse(EmcItemBean.class);
        List<Long> orderIds = requestedResults.stream().map(EmcOrderTaskResultBean::getOrderId).distinct().collect(Collectors.toList());
        Long testMethodId = requestedResults.get(0).getTestMethodId();
        List<EmcOrderBean> orders = orderService.getDao().selectListByIds(orderIds);
        Map<Long, List<EmcOrderTaskResultBean>> resultMap = orderTaskResultService.selectListByFilter(
                SearchFilter.instance().match("ORDERID", orderIds).filter(MatchPattern.OR)
                        .match("testMethodId", testMethodId).filter(MatchPattern.SEQ)
                        .match("AnalysisType", "calculate").filter(MatchPattern.SEQ))
                .stream().collect(Collectors.groupingBy(EmcOrderTaskResultBean::getOrderId));
        List<EmcOrderTaskResultBean> additions = new ArrayList<>();
        List<EmcOrderTaskResultBean> deletions = new ArrayList<>();
        resultMap.forEach((orderId, results) -> {
            List<EmcOrderTaskResultBean> retained = new ArrayList<>();
            Long sampleNum = orders.stream().filter(order -> ObjectUtils.equals(orderId, order.getId()))
                    .findAny().orElse(null).getSampleNum();
            results.forEach(result -> {
                if (items.stream().anyMatch(item -> ObjectUtils.equals(result.getItemId(), item.getId()))) {
                    retained.add(result);
                }
            });
            results.forEach(result -> {
                if (retained.stream().noneMatch(keep -> ObjectUtils.equals(result.getItemId(), keep.getItemId()))) {
                    deletions.add(result);
                }
            });
            items.forEach(item -> {
                if (retained.stream().noneMatch(keep -> ObjectUtils.equals(item.getId(), keep.getItemId()))) {
                    Long repeatItem = item.getRepeatItem() == null ? EmcConstant.ONE : item.getRepeatItem();
                    EmcOrderTaskResultBean template = results.get(0);
                    String pnFunc = item.getPnFunc();
                    if ((!"peraverage".equalsIgnoreCase(pnFunc) && !"dayaverage".equalsIgnoreCase(pnFunc))
                            || ObjectUtils.equals(1L, sampleNum)) {
                        int i = 1;
                        while ((long) i <= repeatItem) {
                            EmcOrderTaskResultBean result = new EmcOrderTaskResultBean();
                            BeanUtils.copyProperties(template, result, EmcConstant.CREATED_COLUMNS);
                            result.setTestMethodId(item.getTestMethodId());
                            result.setItemId(item.getId());
                            result.setItemOrderNo(item.getOrderNo());
                            result.setItemName(item.getItemName());
                            result.setItemAliasName(item.getItemAliasName());
                            result.setScriptFlag(item.getScriptFlag());
                            result.setResultCategory(item.getResultCategory());
                            result.setRevisionRule(item.getRevisionRule());
                            result.setUnitName(item.getUnitName());
                            result.setReportDisplay(item.getReportDisplay());
                            result.setLowLimit(item.getLowLimit());
                            result.setHighLimit(item.getHighLimit());
                            result.setOriginalResult(item.getDefaultResult());
                            result.setRepeatNo(NumberUtils.parseLong(i));
                            result.setId(ApplicationContextHelper.getNextIdentity());
                            additions.add(result);
                            ++i;
                        }
                    }
                }
            });
        });
        orderTaskResultService.getDao().insert(additions);
        List<Long> deleteIds = deletions.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
        orderTaskResultService.getDao().deleteByIdList(deleteIds);
        if (!additions.isEmpty()) {
            EmcOrderTaskResultComputingEngine.getEngine(additions.stream().map(EmcOrderTaskResultBean::getId)
                    .distinct().collect(Collectors.toList())).compute();
        }
    }

}
