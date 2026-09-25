package com.sunwayworld.emc.module.bp.result.run.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.cloud.module.fr.report.service.CoreReportConfigLineService;
import com.sunwayworld.emc.module.bp.result.run.validator.EmcRunSubmitValidator;
import com.sunwayworld.framework.executor.ExecutorHelper;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.utils.*;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.projectnode.ProjectNodeUtils;
import com.sunwayworld.emc.module.bp.auditrecord.bean.EmcAuditRecordBean;
import com.sunwayworld.emc.module.bp.auditrecord.service.EmcAuditRecordService;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.computing.EmcOrderTaskResultComputingEngine;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskLogService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportService;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.result.run.dao.EmcRunDao;
import com.sunwayworld.emc.module.bp.result.run.service.EmcRunService;
import com.sunwayworld.emc.module.bp.result.run.validator.EmcAddQcValidator;
import com.sunwayworld.emc.module.bp.result.run.validator.EmcAddRunValidator;
import com.sunwayworld.emc.module.bp.result.run.validator.EmcDelQcValidator;
import com.sunwayworld.emc.module.bp.result.run.validator.EmcRunAuditSubmitValidator;
import com.sunwayworld.emc.module.bp.result.run.validator.EmcRunDeleteValidator;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.bean.EmcDataCorrelationBean;
import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.bean.EmcDataCorrelationDTO;
import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.bean.EmcDataCorrelationItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.service.EmcDataCorrelationItemService;
import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.service.EmcDataCorrelationService;
import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.validating.EmcDataCorrelationValidatingEngine;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlMaintainItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockConcentBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlMaintainItemService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockConcentService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyDataService;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean.EmcQcCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.service.EmcQcCategoryService;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.bean.EmcSubcontractRecordBean;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.service.EmcSubcontractRecordService;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.EmcMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcQcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcQcItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptTestMethodService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.exception.UnsupportedException;
import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserPermissionBean;
import com.sunwayworld.module.mdm.user.service.CoreUserPermissionService;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.constant.bean.CoreConstantBean;
import com.sunwayworld.module.sys.constant.service.CoreConstantService;

@Repository
@GikamBean
public class EmcRunServiceImpl implements EmcRunService {

    private static final Logger logger = LogManager.getLogger(EmcRunServiceImpl.class);

    @Autowired
    private EmcRunDao runDao;

    @Autowired
    @Lazy
    private EmcFolderService folderService;

    @Autowired
    @Lazy
    private EmcOrderService orderService;

    @Autowired
    @Lazy
    private EmcOrderTaskService orderTaskService;

    @Autowired
    @Lazy
    private EmcOrderTaskResultService orderTaskResultService;

    @Autowired
    @Lazy
    private EmcProjectService projectService;

    @Autowired
    @Lazy
    private EmcItemService itemService;

    @Autowired
    @Lazy
    private EmcEquiptTestMethodService equiptTestMethodService;

    @Autowired
    @Lazy
    private EmcEquiptService equiptService;

    @Autowired
    @Lazy
    private EmcOrderItemGroupService orderItemGroupService;

    @Autowired
    @Lazy
    private EmcGroupItemService groupItemService;

    @Autowired
    @Lazy
    private EmcQcCategoryService qcCategoryService;

    @Autowired
    private EmcAuditRecordService auditRecordService;

    @Autowired
    private CoreConstantService coreConstantService;

    @Autowired
    private CoreReportConfigLineService coreReportConfigLineService;

    @Autowired
    private CoreFileService coreFileService;

    @Autowired
    private EmcMethodService methodService;

    @Autowired
    private EmcOrderTaskLogService orderTaskLogService;

    @Autowired
    private EmcPropertyDataService propertyDataService;

    @Autowired
    @Lazy
    private EmcSampleService sampleService;

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
    private EmcDataCorrelationService dataCorrelationService;

    @Autowired
    @Lazy
    private EmcDataCorrelationItemService dataCorrelationItemService;

    @Autowired
    @Lazy
    private CoreUserPermissionService userPermissionService;

    @Autowired
    @Lazy
    private EmcReportService reportService;

    @Autowired
    @Lazy
    private EmcSubcontractRecordService subcontractRecordService;

    @Autowired
    @Lazy
    private EmcTestMethodService testMethodService;

    @Autowired
    @Lazy
    private EmcQcItemService qcitemService;

    @SuppressWarnings("unchecked")
    @Override
    public EmcRunDao getDao() {
        return runDao;
    }

    /**
     * 添加批功能，主要步骤如下 1、取RestJsonWrapperBean数据。 2、装配run表数据 3、区分正常样品和qc样，赋值和设置杯号
     * 4、结果表装配数据 5、属性数据表装配数据 6、插入数据库 7、分析项有默认值的自动触发公式
     */
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    @ValidateDataWith(EmcAddRunValidator.class)
    public String insert(RestJsonWrapperBean wrapper) {
        // 1、取RestJsonWrapperBean数据。
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        EmcEquiptBean equipt = wrapper.parseUnique(EmcEquiptBean.class);

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
        List<Long> testMethodIdList = orderTaskList.stream().map(EmcOrderTaskBean::getTestMethodId).distinct().collect(Collectors.toList());

        String testId = StringUtils.join(testIdList.toArray(), ",");
        String testName = StringUtils.join(testNameList.toArray(), ",");
        String testMethodId = StringUtils.join(testMethodIdList.toArray(), ",");

        String runId = ApplicationContextHelper.getNextSequence(getDao().getTable());
        Long reportConfigLineId = methodService.selectReportConfigLineIdById(orderTask.getMethodId());

        // 查询测试方法中的前处理
        EmcTestMethodBean testMethod = testMethodService.getDao().selectById(testMethodIdList.get(0));

        run.setId(runId);
        run.setProjId(orderTask.getProjId());
        run.setOrderCategory(orderTask.getOrderCategory());
        run.setTestMethodId(testMethodId);
        run.setTestId(testId);
        run.setTestName(testName);
        run.setAnalysisUserId(userId);
        run.setAnalysisUserName(userName);
        run.setEquiptId(equipt.getId());
        run.setMethodId(orderTask.getMethodId());
        run.setReportConfigLineId(reportConfigLineId);
        run.setBeforeHandle(testMethod.getBeforeHandle());
        run.setBeforeHandleId(testMethod.getBeforeHandleId());
        run.setOrgId(parentOrgId);
        run.setOrgName(parentOrgName);

        // 3、区分正常样品和qc样，赋值和设置杯号

        // 正常样品测试
        // List<EmcOrderTaskBean> ordinaryOrderTaskList =
        // orderTaskList.stream().filter(a -> a.getQcCategoryId() ==
        // null).collect(Collectors.toList());

        // 根据样品编号排序分组
        Map<String, List<EmcOrderTaskBean>> orderTaskTempMap = orderTaskList.stream().collect(Collectors.groupingBy(a -> a.getExt$Item("orderno")));
        Map<String, List<EmcOrderTaskBean>> orderTaskMap = sortByKey(orderTaskTempMap);
        // 对正常样品测试赋值和设置杯号
        Long cupNoIndex = 0L;
        for (Map.Entry<String, List<EmcOrderTaskBean>> entry : orderTaskMap.entrySet()) {
            Long cupNo = cupNoIndex + 1L;
            List<EmcOrderTaskBean> tempList = entry.getValue();
            tempList.stream().forEach(a -> {
                a.setRunId(runId);
                a.setAnalysisUserId(userId);
                a.setAnalysisUserName(userName);
                a.setEquiptId(equipt.getId());
                a.setCupNo(cupNo);
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

        // 5、属性数据表装配数据
        // 添加批的时候在 property_data 表中插入数据(根据 testMethodId 查询 属性数据表)
        EmcPropertyDataBean propertyData = new EmcPropertyDataBean();
        propertyData.setBizId(orderTask.getTestMethodId());
        propertyData.setTableName("T_EMC_TEST_METHOD");
        List<EmcPropertyDataBean> propertyDataList = propertyDataService.getDao().selectList(propertyData);
        EmcPropertyDataBean runPropertyDataBean = new EmcPropertyDataBean();
        if (!propertyDataList.isEmpty()) {
            BeanUtils.copyProperties(propertyDataList.get(0), runPropertyDataBean);
        }

        runPropertyDataBean.setId(ApplicationContextHelper.getNextIdentity());
        runPropertyDataBean.setBizId(Long.parseLong(runId));
        runPropertyDataBean.setTableName("T_EMC_RUN");

        // 6、插入数据库
        orderTaskService.getDao().update(orderTaskList, "RUNID", "ANALYSISUSERID", "ANALYSISUSERNAME", "EQUIPTID", "CUPNO");
        getDao().insert(run);
        orderTaskResultService.getDao().update(orderTaskResultList, "RUNID", "ORIGINALRESULT");
        propertyDataService.getDao().insert(runPropertyDataBean);

        // 7、针对有默认值的分析项结果自动触发计算公式
        if (!defaultValueResultIdList.isEmpty()) {
            EmcOrderTaskResultComputingEngine.getEngine(defaultValueResultIdList).compute();
        }

        return runId;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    @ValidateDataWith(EmcRunDeleteValidator.class)
    public void delete(RestJsonWrapperBean wrapper) {
        List<EmcRunBean> runList = wrapper.parse(EmcRunBean.class);
        // 获取样品测试
        List<String> runIdList = runList.stream().map(EmcRunBean::getId).collect(Collectors.toList());
        delete(runIdList);
    }

    /**
     * 分析超期代办
     */
    @Override
    public Page<EmcRunBean> selectRunOverduePagination(RestJsonWrapperBean wrapper) {
        return selectPagination(wrapper);
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

        // 拿出来orderid
        List<Long> orderIdList = selectOrderTaskList.stream().map(a -> a.getOrderId()).distinct().collect(Collectors.toList());

        // 拿出来最大杯号
        Long maxCupNo = selectOrderTaskList.stream().mapToLong(a -> a.getCupNo()).max().orElse(0L);

        Map<Long, Long> orderMap = selectOrderTaskList.stream().collect(Collectors.toMap(EmcOrderTaskBean::getOrderId, EmcOrderTaskBean::getCupNo, (v1, v2) -> v1));

        // 拿出来传回来的数据
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);

        // 根据ordertaskid拿出来ordertaskresult
        List<Long> orderTaskIdList = orderTaskList.stream().map(a -> a.getId()).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID");

        // 根据orderid分组来设置杯号
        Map<Long, List<EmcOrderTaskBean>> orderTaskGroupByMap = orderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getOrderId));

        long index = 0L;
        // 空白list用来插入更新的数据
        List<EmcOrderTaskBean> updateList = new ArrayList<>();
        for (Entry<Long, List<EmcOrderTaskBean>> entry : orderTaskGroupByMap.entrySet()) {
            for (EmcOrderTaskBean orderTask : entry.getValue()) {
                orderTask.setRunId(id);
                orderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED);

                if (orderIdList.contains(entry.getKey())) {
                    orderTask.setCupNo(orderMap.get(entry.getKey()));
                } else {
                    orderTask.setCupNo(++index + maxCupNo);
                }

                updateList.add(orderTask);
            }
        }
        // 结果设置默认值和批号
        List<Long> itemIdList = orderTaskResultList.stream().map(a -> a.getItemId()).distinct().collect(Collectors.toList());
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
        List<Long> orderTaskIdList = orderTaskTempList.stream().map(a -> a.getId()).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByIds(orderTaskIdList);

        // 拿出来该样品下的样品测试
        List<Long> orderIdList = orderTaskList.stream().map(a -> a.getOrderId()).distinct().collect(Collectors.toList());
        String runId = orderTaskList.get(0).getRunId();

        EmcOrderTaskBean filter = new EmcOrderTaskBean();
        filter.setRunId(runId);
        filter.setStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED);

        List<EmcOrderTaskBean> orderTaskAllList = orderTaskService.selectList(filter, Order.asc("CUPNO"));
        List<EmcOrderTaskBean> selectOrderTaskList = orderTaskAllList.stream().filter(a -> orderIdList.contains(a.getOrderId())).collect(Collectors.toList());
        List<Long> selectOrderTaskIdList = selectOrderTaskList.stream().map(a -> a.getId()).collect(Collectors.toList());
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

        for (int i = cupNoList.size() - 1; i >= 0; i--) {
            Long cupNo = cupNoList.get(i);
            for (int j = 0; j < orderTaskUpdateList.size(); j++) {
                EmcOrderTaskBean orederTask = orderTaskUpdateList.get(j);
                if (orederTask.getCupNo() > cupNo) {
                    orederTask.setCupNo(orederTask.getCupNo() - 1);
                }
            }
        }

        orderTaskService.getDao().update(orderTaskUpdateList, "CUPNO");
        return orderTaskList.get(0).getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_COMPLETE)
    public List<CoreBpmnInstanceStatusDTO<String>> completeTask(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<String>> coreBpmnInstanceStatusDTOList = EmcRunService.super.completeTask(wrapper);

        List<String> runIdList = coreBpmnInstanceStatusDTOList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcRunBean> runList = selectListByFilter(SearchFilter.instance().match("ID", runIdList).filter(MatchPattern.OR));

        List<EmcOrderTaskBean> filterOrderTaskList = new ArrayList<>();

        runIdList.forEach(runId -> {
            EmcOrderTaskBean filterOrderTask = new EmcOrderTaskBean();

            filterOrderTask.setRunId(runId);
            filterOrderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_APPROVE);
            filterOrderTaskList.add(filterOrderTask);
        });

        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectList(filterOrderTaskList, Arrays.asList("RUNID", "STATUS"), Arrays.asList("ID"));

        // 分析校核审核通过
        if (!orderTaskList.isEmpty()) {
            orderTaskList.forEach(t -> t.setStatus(EmcBizConstant.ORDERTASK_STATUS_AUDIT));
            orderTaskService.getDao().update(orderTaskList, "STATUS");
        }

        // 插入样品测试操作记录表
        List<EmcOrderTaskBean> allOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(runIdList, "RUNID");
        orderTaskLogService.insert(allOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_GROUP_AUDIT, EmcBizConstant.ORDER_TASK_LOG_ANALYSE_MUTUAL_AUDIT);

        // 分析校核审核插入审核记录
        this.insertAuditRecordByRun(runList, "PROCESS_COMPLETE", wrapper.getParamValue("bpmn_comment"), null);

        runList.stream().forEach(e -> {
            e.setStatus(EmcBizConstant.RUN_STATUS_AUDIT);
        });
        getDao().update(runList, "STATUS");

        ExecutorHelper.submit(
                new TimerTask() {
                    public void run() {
                        // 重新生成原始记录报表 PDF 文件
                        composeRunReportPdf(runList);
                    }
                },1000); // 延时1S执行
        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_START)
    @ValidateDataWith(EmcRunSubmitValidator.class)
    public List<CoreBpmnInstanceStatusDTO<String>> startProcess(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<String>> coreBpmnInstanceStatusDTOList = EmcRunService.super.startProcess(wrapper);
        List<String> idList = coreBpmnInstanceStatusDTOList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcRunBean> runList = selectListByFilter(SearchFilter.instance().match("ID", idList).filter(MatchPattern.OR));
        // 分析录入提交提交插入审核记录
        this.insertAuditRecordByRun(runList, "PROCESS_START", wrapper.getParamValue("bpmn_comment"), null);
        // orderTask表插入分析日期字段
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(idList, "RUNID").stream()
                .filter(a -> StringUtils.equals(a.getStatus(), EmcBizConstant.ORDERTASK_STATUS_LOGGED)).collect(Collectors.toList());
        orderTaskList.stream().forEach(a -> {
            runList.stream().filter(b -> StringUtils.equals(b.getId(), a.getRunId())).forEach(c -> {
                a.setAnalysisDate(c.getAnalysisBeginDate());
                a.setStatus(EmcBizConstant.ORDERTASK_STATUS_APPROVE);
            });
        });

        // 插入样品测试操作记录表
        orderTaskLogService.insert(orderTaskList, EmcBizConstant.ORDER_TASK_LOG_RESULT_INPUT, EmcBizConstant.ORDER_TASK_LOG_ANALYSE);

        // 将样品测试结果表的记录日志标志置为"1"
        List<Long> orderTaskIdList = orderTaskList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "orderTaskId");
        orderTaskResultList.stream().forEach(a -> {
            a.setSubmitFlag(Constant.YES);
        });
        orderTaskResultService.getDao().update(orderTaskResultList, "SUBMITFLAG");

        orderTaskService.getDao().update(orderTaskList, "ANALYSISDATE", "STATUS");
        runList.stream().forEach(e -> {
            e.setStatus(EmcBizConstant.RUN_STATUS_APPROVE);
        });
        getDao().update(runList, "STATUS");

        ExecutorHelper.submit(
                new TimerTask() {
                    public void run() {
                        // 重新生成原始记录报表 PDF 文件
                        composeRunReportPdf(runList);
                    }
                },1000); // 延时1S执行
        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_UNDO)
    public List<CoreBpmnInstanceStatusDTO<String>> undo(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<String>> instanceStatusList = EmcRunService.super.undo(wrapper);
        List<String> idList = instanceStatusList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcRunBean> runList = selectListByFilter(SearchFilter.instance().match("ID", idList).filter(MatchPattern.OR));

        List<EmcOrderTaskBean> filter = new ArrayList<>();
        instanceStatusList.stream().map(e -> e.getId()).forEach(runId -> {
            EmcOrderTaskBean orderTask = new EmcOrderTaskBean();
            orderTask.setRunId(runId);
            orderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_AUDIT);
            filter.add(orderTask);
        });

        List<EmcOrderTaskBean> updateOrderTaskList = orderTaskService.getDao().selectList(filter, Arrays.asList("RUNID", "STATUS"), Collections.emptyList());
        updateOrderTaskList.forEach(t -> t.setStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED));
        orderTaskService.getDao().update(updateOrderTaskList, "STATUS");

        // 插入样品测试操作记录表
        List<EmcOrderTaskBean> allOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(idList, "RUNID");
        orderTaskLogService.insert(allOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_LEADER_AUDIT_UNDO, EmcBizConstant.ORDER_TASK_LOG_ANALYSE_AUDIT);

        // 分析审核回退插入审核记录
        this.insertAuditRecordByRun(runList, "PROCESS_UNDO", wrapper.getParamValue("bpmn_comment"), null);

        runList.stream().forEach(e -> {
            e.setCheckStatus(Constant.NO);
            e.setStatus(EmcBizConstant.RUN_STATUS_DRAFT);
        });
        getDao().update(runList, "STATUS", "CHECKSTATUS");
        return instanceStatusList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_REJECT)
    public List<CoreBpmnInstanceStatusDTO<String>> rejectTask(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<String>> coreBpmnInstanceStatusDTOList = EmcRunService.super.rejectTask(wrapper);
        List<String> runIdList = coreBpmnInstanceStatusDTOList.stream().map(e -> e.getId()).collect(Collectors.toList());

        List<EmcOrderTaskBean> allOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(runIdList, "RUNID");

        // 修改样品测试表状态
        allOrderTaskList.forEach(t -> t.setStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED));
        orderTaskService.getDao().update(allOrderTaskList, "STATUS");
        // 插入样品测试操作记录表
        orderTaskLogService.insert(allOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_GROUP_AUDIT_UNDO, EmcBizConstant.ORDER_TASK_LOG_ANALYSE_MUTUAL_AUDIT);
        List<EmcRunBean> runList = selectListByFilter(SearchFilter.instance().match("ID", runIdList).filter(MatchPattern.OR));
        runList.stream().forEach(e -> {
            e.setStatus(EmcBizConstant.RUN_STATUS_DRAFT);
        });
        getDao().update(runList, "STATUS");
        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    @Transactional
    public Long init(RestJsonWrapperBean wrapper) {
        List<EmcRunBean> runList = wrapper.parse(EmcRunBean.class);
        List<String> runIdList = runList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("RUNID", runIdList).filter(MatchPattern.OR));

        List<Long> orderTaskIdList = orderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());

        if (!orderTaskList.isEmpty()) {
            List<EmcOrderTaskResultBean> orderTaskResultlist = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID");
            orderTaskResultlist.stream().forEach(e -> {
                // 只处理分析类型为分析的数据
                if (EmcBizConstant.ANALYSISCATEGORY_ANALYSIS.equals(e.getAnalysisType())) {
                    e.setOriginalResult(null);
                    e.setReportResult(null);
                    e.setResultStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED);
                }
            });
            orderTaskResultService.getDao().update(orderTaskResultlist, "ORIGINALRESULT", "REPORTRESULT", "RESULTSTATUS");
        }
        return orderTaskList.get(0).getId();
    }

    @Override
    @Transactional
    public Long updatedefaultresults(RestJsonWrapperBean wrapper) {
        List<EmcRunBean> runList = wrapper.parse(EmcRunBean.class);
        List<String> runIdList = runList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService
                .selectListByFilter(SearchFilter.instance().match("RUNID", runIdList).filter(MatchPattern.OR).match("ANALYSISTYPE", "analysis").filter(MatchPattern.SEQ));

        List<Long> orderTaskIdList = orderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());

        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID");

        List<Long> itemIdList = orderTaskResultList.stream().map(a -> a.getItemId()).distinct().collect(Collectors.toList());
        List<EmcItemBean> itemList = itemService.getDao().selectListByIds(itemIdList);
        Map<Long, String> itemMap = new HashMap<>();

        for (EmcItemBean e : itemList) {
            itemMap.put(e.getId(), e.getDefaultResult());
        }
        // 对于有默认值的样品测试结果，需要自动触发一次计算公式
        List<Long> defaultValueResultIdList = new ArrayList<>();
        for (EmcOrderTaskResultBean a : orderTaskResultList) {
            // 当测试管理中分析项默认值不会空而且当前结果值为空的情况下才进行默认结果
            if (!StringUtils.isEmpty(itemMap.get(a.getItemId())) && StringUtils.isEmpty(a.getReportResult())) {
                a.setOriginalResult(itemMap.get(a.getItemId()));
                defaultValueResultIdList.add(a.getId());
            }
        }
        orderTaskResultService.getDao().update(orderTaskResultList, "RUNID", "ORIGINALRESULT");
        // 针对有默认值的分析项结果自动触发计算公式
        if (!defaultValueResultIdList.isEmpty()) {
            EmcOrderTaskResultComputingEngine.getEngine(defaultValueResultIdList).compute();
        }
        return orderTaskList.get(0).getId();
    }

    @Override
    public Long sampleTestInitialize(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskResultBean> orderTaskResultlist = wrapper.parse(EmcOrderTaskResultBean.class);
        orderTaskResultlist.stream().forEach(e -> {
            e.setOriginalResult(null);
            e.setReportResult(null);
            e.setResultStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED);
        });
        orderTaskResultService.getDao().update(orderTaskResultlist, "ORIGINALRESULT", "REPORTRESULT", "RESULTSTATUS");
        return orderTaskResultlist.get(0).getId();
    }

    /**
     * 添加qc功能，主要步骤如下 1、取RestJsonWrapperBean数据。 2、准备数据,准备后面要用到的数据。 3、添加folder表数据。
     * 4、添加order、ordertask、ordertaskresult数据 5、插入数据库6、重新按照qc类型名称排列
     */
    @Override
    @Transactional
    @ValidateDataWith(EmcAddQcValidator.class)
    public Long addQc(RestJsonWrapperBean wrapper) {
        // 1、取RestJsonWrapperBean数据

        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<EmcQcCategoryBean> qcCategoryList = wrapper.parse(EmcQcCategoryBean.class);
        String stockBatchNo = wrapper.getFilterValue("stockBatchNo");
        Map<Long, EmcStockConcentBean> itemConcentMap = new HashMap<>();
        if (!StringUtils.isEmpty(stockBatchNo)) {
            List<EmcStockBean> stockList = stockService.getDao().selectListByOneColumnValue(stockBatchNo, "BATCHNO");
            List<Long> stockIdList = stockList.stream().map(EmcStockBean::getId).distinct().collect(Collectors.toList());
            List<EmcStockConcentBean> stockConcentList = stockConcentService.getDao().selectListByOneColumnValues(stockIdList, "STOCKID");
            List<Long> maintainIdList = stockConcentList.stream().map(EmcStockConcentBean::getMtlMaintainId).distinct().collect(Collectors.toList());
            List<EmcMtlMaintainItemBean> mtlMaintainList = mtlMaintainItemService.getDao().selectListByOneColumnValues(maintainIdList, "MTLMAITAINID");
            mtlMaintainList.forEach(m -> {
                EmcStockConcentBean stockConcent = stockConcentList.stream().filter(c -> ObjectUtils.equals(m.getMtlMaitainId(), c.getMtlMaintainId())).findFirst().get();
                itemConcentMap.put(m.getItemId(), stockConcent);
            });
        }

        // 2、准备数据

        String runId = orderTaskList.get(0).getRunId();
        // 批下所有的orderTask记录，步骤5更新杯号要用
        List<EmcOrderTaskBean> orderTaskAllList = orderTaskService.getDao().selectListByOneColumnValue(runId, "RUNID", Order.asc("CUPNO"));
        // order表数据
        List<Long> idList = orderTaskList.stream().map(e -> e.getOrderId()).distinct().collect(Collectors.toList());
        List<EmcOrderBean> selectOrderList = orderService.getDao().selectListByIds(idList);
        // orderTask表数据，这是要复制的数据
        List<EmcOrderTaskBean> selectOrderTaskList = orderTaskAllList.stream().filter(a -> idList.contains(a.getOrderId())).collect(Collectors.toList());
        // 根据样品测试的杯号生成对样品排序后的orderList
        Map<Long, List<EmcOrderTaskBean>> orderTaskMap = selectOrderTaskList.stream().collect(Collectors.groupingBy(a -> a.getOrderId()));
        List<EmcOrderTaskBean> sortList = new ArrayList<>();
        orderTaskMap.forEach((key, value) -> {
            sortList.add(value.get(0));
        });
        CollectionUtils.sort(sortList, (o1, o2) -> o1.getCupNo().compareTo(o2.getCupNo()));
        List<EmcOrderBean> orderList = new ArrayList<>();
        sortList.stream().forEach(a -> {
            Long id = a.getOrderId();
            EmcOrderBean order = selectOrderList.stream().filter(b -> ObjectUtils.equals(b.getId(), id)).findAny().orElse(null);
            orderList.add(order);
        });
        // orderTaskResult表数据
        List<Long> orderTaskIdList = selectOrderTaskList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID");
        // 根据测试ID取测试下启用的分析类型为分析的分析项
        List<Long> testMethodIdList = selectOrderTaskList.stream().map(a -> a.getTestMethodId()).distinct().collect(Collectors.toList());
        List<EmcItemBean> itemTempList = itemService.selectListByFilter(SearchFilter.instance().match("TESTMETHODID", testMethodIdList).filter(MatchPattern.OR)
                .match("ACTIVATEDFLAG", Constant.ACTIVATED_STATUS_YES).filter(MatchPattern.EQ).match("ANALYSISCATEGORY", EmcBizConstant.ANALYSISCATEGORY_ANALYSIS).filter(MatchPattern.EQ));
        List<EmcItemBean> itemList = new ArrayList<>();
        List<EmcOrderItemGroupBean> oldOrderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(idList, "ORDERID");
        // 限制分析项需要时对应分析项组里面的数据
        if (!oldOrderItemGroupList.isEmpty()) {
            List<Long> itemGroupIdList = oldOrderItemGroupList.stream().map(a -> a.getItemGroupId()).distinct().collect(Collectors.toList());
            // 获取分析项
            List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID");
            List<Long> itemIdList = groupItemList.stream().map(a -> a.getItemId()).distinct().collect(Collectors.toList());
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
            List<Long> itemGroupIdList = orderItemGroupList.stream().map(a -> a.getItemGroupId()).distinct().collect(Collectors.toList());
            // 获取分析项
            List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID");
            List<Long> itemIdList = groupItemList.stream().map(a -> a.getItemId()).distinct().collect(Collectors.toList());
            itemList = itemTempList.stream().filter(e -> itemIdList.contains(e.getId())).collect(Collectors.toList());
        }

        // 准备存放插入数据用的List
        List<EmcFolderBean> folderList = new ArrayList<>();
        List<EmcOrderBean> copyOrderList = new ArrayList<>();
        List<EmcOrderTaskBean> copyOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> copyOrderTaskResultList = new ArrayList<>();
        List<EmcOrderItemGroupBean> newOrderItemGroupList = new ArrayList<>();

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
            EmcOrderBean tempOrderSecond = orderService.getDao().selectFirstIfPresent(tempOrderFirst, Order.desc("ORDERNO"));
            orderNo = tempOrderSecond.getOrderNo();
        }
        // 点位编号累加值
        String orderNoTemp = orderNo;

        // 4、添加order、ordertask、ordertaskresult数据
        List<Long> defaultValueResultIdList = new ArrayList<>();

        // 查询测试下是否有质控分析项
        List<EmcQcItemBean> qcItemList = qcitemService.selectListByFilter(SearchFilter.instance().match("TESTMETHODID", testMethodIdList).filter(MatchPattern.OR));

        for (int i = 0; i < orderList.size(); i++) {
            // 实验室空白类型数据杯号添加次数累加值
            Long labBlankIndex = 0L;
            // 样品下杯号添加次数累加值
            Long addTotalIndex = 0L;
            for (int j = 0; j < qcCategoryList.size(); j++) {
                EmcQcCategoryBean qcCategory = qcCategoryList.get(j);
                Long addNum = qcCategory.getAddNum() == null ? 1L : qcCategory.getAddNum();
                boolean qcNameFlag = EmcConstant.QCCATEGORY_QCCATEGORYID_L_BLANK.equals(qcCategory.getQcCategoryId()); // 实验室空白类型数据需特殊处理
                boolean updateParNoFlag = "1".equals(qcCategory.getUpdateParNo()); // 是否更新父样编号字段
                boolean parOrderFlag = "1".equals(qcCategory.getParOrder()); // 是否有父样
                // 根据qc类型的添加数量插入多条数据
                for (Long num = 0L; num < addNum; num++) {

                    // 1)添加order表数据
                    EmcOrderBean order = orderList.get(i);
                    EmcOrderBean orderInfo = new EmcOrderBean();
                    BeanUtils.copyProperties(order, orderInfo, new String[] { "ID", "PARENTORDERNO", "CREATEDBYID", "CREATEDBYNAME", "CREATEDTIME", "CREATEDBYORGID", "CREATEDBYORGNAME", "SAMPLEID" });
                    Long id = ApplicationContextHelper.getNextIdentity();
                    orderInfo.setId(id);
                    orderInfo.setFolderId(folderId);
                    orderNoTemp = getNextOrderNo(orderNoTemp);
                    orderInfo.setOrderNo(qcFolder.getFolderNo() + orderNoTemp);
                    orderInfo.setLabelNo(orderInfo.getOrderNo());
                    orderInfo.setMonitorTmplId(order.getMonitorTmplId());
                    orderInfo.setQcCategoryId(qcCategory.getId());
                    orderInfo.setQcCategoryCode(qcCategory.getQcCategoryId());
                    orderInfo.setQcName(qcCategory.getQcName());
                    orderInfo.setParentOrderNo(null);
                    orderInfo.setOrderDesc(null);
                    orderInfo.setProjId(null);
                    orderInfo.setRelatedOrderNo(null);

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

                    for (int k = 0; k < selectOrderTaskList.size(); k++) {
                        EmcOrderTaskBean orderTask = selectOrderTaskList.get(k);
                        if (ObjectUtils.equals(orderTask.getOrderId(), order.getId())) {

                            // 2)添加ordertask数据
                            // 添加ordertask标志，判断ordertask下是否有result插入，如果有再插入ordertask
                            boolean addOrderTaskFlag = false;
                            EmcOrderTaskBean otInfo = new EmcOrderTaskBean();
                            BeanUtils.copyProperties(orderTask, otInfo,
                                    new String[] { "ID", "CUPNO", "CREATEDBYID", "CREATEDBYNAME", "CREATEDTIME", "CREATEDBYORGID", "CREATEDBYORGNAME", "STOCKID", "STOCKBATCHNO" });
                            Long orderTaskId = ApplicationContextHelper.getNextIdentity();

                            otInfo.setId(orderTaskId);
                            otInfo.setFolderId(folderId);
                            otInfo.setOrderId(id);
                            otInfo.setEquiptAcquNo(orderInfo.getLabelNo());
                            otInfo.setMonitorTmplId(orderTask.getMonitorTmplId());
                            otInfo.setQcCategoryId(qcCategory.getId());
                            otInfo.setQcCategoryCode(qcCategory.getQcCategoryId());
                            otInfo.setQcName(qcCategory.getQcName());
                            otInfo.setStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED);
                            otInfo.setRunId(runId);
                            otInfo.setParentId(orderTask.getId());
                            otInfo.setCupNo(null);
                            otInfo.setProjId(null);

                            if (updateParNoFlag) {
                                otInfo.setParentId(orderTask.getId());
                            }

                            // 对qc类型是QC-STD插入库存编号
                            if (EmcConstant.QCCATEGORY_QCCATEGORYID_QC_STD.equals(qcCategory.getQcCategoryId())) {
                                otInfo.setStockBatchNo(stockBatchNo);
                            }

                            if (!itemList.isEmpty()) {
                                for (EmcItemBean b : itemList) {
                                    for (int e = 0; e < orderTaskResultList.size(); e++) {
                                        // 3)插入ordertaskresult数据
                                        EmcOrderTaskResultBean orderTaskResult = orderTaskResultList.get(e);
                                        if (ObjectUtils.equals(orderTaskResult.getOrderTaskId(), orderTask.getId())) {
                                            if (ObjectUtils.equals(orderTaskResult.getItemId(), b.getId()) && !StringUtils.isEmpty(b.getQcCategoryId())
                                                    && b.getQcCategoryId().contains("" + qcCategory.getId())) {
                                                addOrderTaskFlag = true;
                                                addOrderFlag = true;

                                                EmcOrderTaskResultBean otrInfo = new EmcOrderTaskResultBean();
                                                BeanUtils.copyProperties(orderTaskResult, otrInfo,
                                                        new String[] { "ORIGINALRESULT", "REPORTRESULT", "CREATEDBYID", "CREATEDBYNAME", "CREATEDTIME", "CREATEDBYORGID", "CREATEDBYORGNAME" });
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

                                                if (updateParNoFlag) {
                                                    otrInfo.setParentOrderNo(order.getOrderNo());
                                                }

                                                // 复制实验室标准样品标准值、不确定度和浓度单位
                                                if (itemConcentMap.size() > 0 && itemConcentMap.get(b.getId()) != null) {
                                                    EmcStockConcentBean stockConcent = itemConcentMap.get(b.getId());
                                                    String uncertainty = Optional.ofNullable(stockConcent.getUncertainty()).orElse(stockConcent.getUncertaintyPercent() + "%");

                                                    otrInfo.setStandQty(ObjectUtils.isEmpty(stockConcent.getStdValue()) ? "" : stockConcent.getStdValue() + "");
                                                    //otrInfo.setRn16(ObjectUtils.isEmpty(stockConcent.getUncertainty()) ? "" : stockConcent.getUncertainty() + "");
                                                    otrInfo.setRn16(uncertainty);
                                                    otrInfo.setUnitName(ObjectUtils.isEmpty(stockConcent.getConcentUnitName()) ? "" : stockConcent.getConcentUnitName());
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
                                    if (orderTaskResultList.stream().noneMatch(z -> z.getItemId().equals(b.getId()))) {
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
                                            otrInfo.setTestId(orderTaskResultList.get(0).getTestId());
                                            otrInfo.setMethodId(orderTaskResultList.get(0).getMethodId());
                                            otrInfo.setTestMethodId(b.getTestMethodId());
                                            otrInfo.setMethodVersionId(orderTaskResultList.get(0).getMethodVersionId());
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

                                            if (updateParNoFlag) {
                                                otrInfo.setParentOrderNo(order.getOrderNo());
                                            }

                                            // 复制实验室标准样品标准值、不确定度和浓度单位
                                            if (itemConcentMap.size() > 0 && itemConcentMap.get(b.getId()) != null) {
                                                EmcStockConcentBean stockConcent = itemConcentMap.get(b.getId());
                                                String uncertainty = Optional.ofNullable(stockConcent.getUncertainty()).orElse(stockConcent.getUncertaintyPercent() + "%");

                                                otrInfo.setStandQty(ObjectUtils.isEmpty(stockConcent.getStdValue()) ? "" : stockConcent.getStdValue() + "");
                                                //otrInfo.setRn16(ObjectUtils.isEmpty(stockConcent.getUncertainty()) ? "" : stockConcent.getUncertainty() + "");
                                                otrInfo.setRn16(uncertainty);
                                                otrInfo.setUnitName(ObjectUtils.isEmpty(stockConcent.getConcentUnitName()) ? "" : stockConcent.getConcentUnitName());
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

                            // 判定是否需要添加质控分析项
                            List<EmcQcItemBean> addQcItemList = qcItemList.stream().filter(q ->!ObjectUtils.isEmpty(q.getQcCategoryId())
                                                                                              && ObjectUtils.equals(orderTask.getTestMethodId(), q.getTestMethodId())
                                                                                              && q.getQcCategoryId().contains(qcCategory.getId().toString())).collect(Collectors.toList());

                            if (!ObjectUtils.isEmpty(addQcItemList)) {
                                addQcItemList.forEach(aq ->{
                                    EmcOrderTaskResultBean addResult = new EmcOrderTaskResultBean();
                                    addResult.setId(ApplicationContextHelper.getNextIdentity());
                                    addResult.setFolderId(otInfo.getFolderId());
                                    addResult.setOrderId(id);
                                    addResult.setOrderNo(orderInfo.getOrderNo());
                                    addResult.setOrderTaskId(orderTaskId);
                                    addResult.setQcCategoryId(qcCategory.getId());
                                    addResult.setQcCategoryCode(qcCategory.getQcCategoryId());
                                    addResult.setQcName(qcCategory.getQcName());
                                    addResult.setTestId(orderTaskResultList.get(0).getTestId());
                                    addResult.setMethodId(orderTaskResultList.get(0).getMethodId());
                                    addResult.setTestMethodId(aq.getTestMethodId());
                                    addResult.setMethodVersionId(orderTaskResultList.get(0).getMethodVersionId());
                                    addResult.setRunId(runId);
                                    addResult.setItemId(aq.getId());
                                    addResult.setFrequency(EmcConstant.ONE);
                                    addResult.setCycle(EmcConstant.ONE);
                                    addResult.setItemAliasName(aq.getItemAliasName());
                                    addResult.setItemName(aq.getItemName());
                                    addResult.setAnalysisType(EmcBizConstant.ANALYSISCATEGORY_ANALYSIS);
                                    addResult.setScriptFlag("0");
                                    addResult.setLowLimit(aq.getLowLimit());
                                    addResult.setReportDisplay("0");
                                    addResult.setHighLimit(aq.getHighLimit());
                                    addResult.setRevisionRule(aq.getRevisionRule());
                                    addResult.setResultCategory("number");
                                    addResult.setRepeatNo(1L);
                                    copyOrderTaskResultList.add(addResult);
                                });
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
                        if (qcNameFlag) {
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
            folderService.getDao().insert(folderList);
            orderService.getDao().insert(copyOrderList);
            orderTaskService.getDao().insert(copyOrderTaskList);
            // 过滤下没有添加的result
            List<Long> realorderTaskIdList = copyOrderTaskList.stream().map(a -> a.getId()).collect(Collectors.toList());
            List<EmcOrderTaskResultBean> realOrderTaskResultlist = copyOrderTaskResultList.stream().filter(a -> realorderTaskIdList.contains(a.getOrderTaskId())).collect(Collectors.toList());
            orderTaskResultService.getDao().insert(realOrderTaskResultlist);
            orderTaskService.getDao().update(orderTaskAllList, "CUPNO");
            orderItemGroupService.getDao().insert(newOrderItemGroupList);

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
            Map<Long, List<EmcOrderTaskBean>> mapFirst = allTaskList.stream().filter(a -> a.getParentId() != null && qcIdFirstList.contains(a.getQcCategoryId()))
                    .collect(Collectors.groupingBy(EmcOrderTaskBean::getParentId));

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
            Map<Long, List<EmcOrderTaskBean>> mapSecond = allTaskList.stream().filter(a -> a.getParentId() != null && qcIdSecondList.contains(a.getQcCategoryId()))
                    .collect(Collectors.groupingBy(EmcOrderTaskBean::getParentId));

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

            // 对有默认值的分析项触发一次公式计算
            if (!defaultValueResultIdList.isEmpty()) {
                EmcOrderTaskResultComputingEngine.getEngine(defaultValueResultIdList).compute();
            }
        }
        return copyOrderList.get(0).getId();
    }

    /**
     * 删除qc功能，主要步骤如下 1、根据传过来的id获得完整的ordertask数据 2、准备数据,准备后面要用到的数据。
     * 3、获取要删除的order、orderTask、orderTaskResult并删除 4、判断是否可以删除folder表数据 5、重新排列杯号
     */
    @Override
    @Transactional
    @ValidateDataWith(EmcDelQcValidator.class)
    public Long delQc(RestJsonWrapperBean wrapper) {

        // 1、根据传过来的id获得完整的ordertaskList数据

        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<Long> orderTaskIdList = orderTaskList.stream().map(e -> e.getId()).collect(Collectors.toList());
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
        List<Long> orderIdList = selectedOrderTaskList.stream().map(e -> e.getOrderId()).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskDelList = orderTaskAllList.stream().filter(b -> orderIdList.contains(b.getOrderId())).collect(Collectors.toList());
        // 获得要删除的orderTaskResult数据
        List<Long> orderTaskDelIdList = orderTaskDelList.stream().map(a -> a.getId()).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskDelIdList, "ORDERTASKID");
        // 获得删除的杯号并进行排序
        List<Long> orderTaskDelCupNo = orderTaskDelList.stream().map(a -> a.getCupNo()).distinct().collect(Collectors.toList());
        Collections.sort(orderTaskDelCupNo);

        // 获得删除的OrderItemGroup
        List<EmcOrderItemGroupBean> orderItemGroupList = new ArrayList<>();
        orderIdList.forEach(t -> {
            EmcOrderItemGroupBean orderItemGroup = new EmcOrderItemGroupBean();
            orderItemGroup.setOrderId(t);
            orderItemGroupList.add(orderItemGroup);
        });

        orderService.getDao().deleteByIdList(orderIdList);
        orderTaskService.getDao().deleteBy(orderTaskDelList);
        orderTaskResultService.getDao().deleteBy(orderTaskResultList);
        orderItemGroupService.getDao().deleteBy(orderItemGroupList, "ORDERID");

        // 4、判断是否可以删除folder表数据

        EmcOrderTaskBean filter = new EmcOrderTaskBean();
        filter.setRunId(runId);
        filter.setStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED);
        List<EmcOrderTaskBean> orderTaskTempList = orderTaskService.getDao().selectList(filter);
        // 判断批下是否还有分析录入添加的非质控样
        boolean delFlag = orderTaskTempList == null ? true : false;
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

        // 获取删除后批下所有的orderTask
        EmcOrderTaskBean otNewFilter = new EmcOrderTaskBean();
        otNewFilter.setRunId(runId);
        otNewFilter.setStatus(status);
        List<EmcOrderTaskBean> orderTaskAfterDelAllList = orderTaskService.getDao().selectList(otNewFilter, Order.asc("CUPNO"));

        for (int i = orderTaskDelCupNo.size() - 1; i >= 0; i--) {
            Long CupNo = orderTaskDelCupNo.get(i);
            orderTaskAfterDelAllList.stream().filter(b -> b.getCupNo().longValue() > CupNo.longValue()).forEach(c -> c.setCupNo(c.getCupNo() - 1L));
        }
        orderTaskService.getDao().update(orderTaskAfterDelAllList, "CUPNO");

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

        return selectedOrderTaskList.get(0).getId();
    }

    @Override
    public Page<EmcEquiptBean> selectEquiptPagination(String testMethodId, RestJsonWrapperBean wrapper) {
        List<String> list = Arrays.stream(StringUtils.split(testMethodId, ",")).distinct().collect(Collectors.toList());
        String[] array = list.toArray(new String[0]);
        if (array.length == 0) return new Pagination<>();
        List<Long> testMethodIdList = Arrays.stream(array).map(NumberUtils::parseLong).collect(Collectors.toList());
        List<EmcEquiptTestMethodBean> testMethodEquiptList = equiptTestMethodService.getDao().selectListByOneColumnValues(testMethodIdList, "TESTMETHODID").stream().filter(a -> "analysis".equals(a.getAnalysisCategory())).collect(Collectors.toList());
        if (testMethodEquiptList.isEmpty()) {
            EmcEquiptBean filter = new EmcEquiptBean(); filter.setEquiptName("N/A");
            EmcEquiptBean equipt = equiptService.getDao().selectFirstIfPresent(filter);
            Pagination<EmcEquiptBean> pagination = new Pagination<>(Arrays.asList(equipt == null ? filter : equipt));
            return pagination;
        }
        Map<Long, List<EmcEquiptTestMethodBean>> grouped = testMethodEquiptList.stream().collect(Collectors.groupingBy(EmcEquiptTestMethodBean::getEquiptId));
        List<Long> equiptIdList = grouped.entrySet().stream().filter(e -> e.getValue().size() == array.length).map(Map.Entry::getKey).collect(Collectors.toList());
        List<EmcEquiptBean> equiptList = equiptService.selectListByFilter(SearchFilter.instance().match("ID", equiptIdList).filter(MatchPattern.OR), wrapper);
        equiptList.forEach(a -> testMethodEquiptList.stream().filter(b -> b.getEquiptId().equals(a.getId())).forEach(c -> a.setExt$Item("defaultflag", c.getDefaultFlag())));
        List<EmcEquiptBean> visible = equiptList.stream().filter(e -> e.getMaintenanceUserId() == null || ObjectUtils.equals("", e.getMaintenanceUserId()) || e.getMaintenanceUserId().contains(LocalContextHelper.getLoginUserId())).collect(Collectors.toList());
        return new Pagination<>(visible);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void editEquipt(RestJsonWrapperBean wrapper) {
        EmcEquiptBean equipt = wrapper.parseUnique(EmcEquiptBean.class);
        Long equiptId = equipt.getId();
        EmcRunBean run = wrapper.parseUnique(EmcRunBean.class);
        run.setCurveId(null);
        run.setCurveNo(null);
        run.setEquiptId(equiptId);

        EmcOrderTaskBean filter = new EmcOrderTaskBean();
        filter.setRunId(run.getId());
        filter.setStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED);
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectList(filter);
        orderTaskList.stream().forEach(a -> a.setEquiptId(equiptId));

        getDao().update(run, "CURVEID", "CURVENO", "EQUIPTID");
        orderTaskService.getDao().update(orderTaskList, "EQUIPTID");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
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
        composeRunReportPdf(runList);
    }

    @Override
    public Page<EmcEquiptBean> selectEditEquiptPagination(String testMethodId, String equiptId) {
        String[] array = StringUtils.split(testMethodId, ",");

        if (array.length > 0) {
            List<Long> testMethodIdList = Arrays.stream(array).map(NumberUtils::parseLong).collect(Collectors.toList());

            List<EmcEquiptTestMethodBean> testMethodEquiptList = equiptTestMethodService.getDao().selectListByOneColumnValues(testMethodIdList, "TESTMETHODID").stream()
                    .filter(a -> "analysis".equals(a.getAnalysisCategory())).collect(Collectors.toList());

            // 根据equiptId分组，如果list元素和testMethodId的数量一致，说明该测试下都维护了该仪器。
            Map<Long, List<EmcEquiptTestMethodBean>> testMethodEquiptMap = testMethodEquiptList.stream().collect(Collectors.groupingBy(EmcEquiptTestMethodBean::getEquiptId));

            List<Long> equiptIdList = testMethodEquiptMap.entrySet().stream().filter(e -> e.getValue().size() == array.length).map(Entry::getKey).collect(Collectors.toList());

            List<EmcEquiptBean> equiptList = equiptService.getDao().selectListByIds(equiptIdList);

            // 只能看到当前账号有权限的仪器
            List<EmcEquiptBean> equiptOfMaintenanceUserList = equiptList.stream().filter(a -> !ObjectUtils.equals(a.getId(), NumberUtils.parseLong(equiptId)))
                    .filter(e -> e.getMaintenanceUserId() == null || ObjectUtils.equals("", e.getMaintenanceUserId()) || e.getMaintenanceUserId().contains(LocalContextHelper.getLoginUserId()))
                    .collect(Collectors.toList());
            return new Pagination<>(equiptOfMaintenanceUserList);
        }

        return new Pagination<>();
    }

    @Override
    @Transactional
    public Long runInput(RestJsonWrapperBean wrapper) {
        // 模版数据
        List<EmcOrderTaskResultBean> resultList = wrapper.parse(EmcOrderTaskResultBean.class);

        List<Long> orderTaskResultIdList = resultList.stream().map(a -> a.getId()).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByIds(orderTaskResultIdList);

        // 要改变的数据
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<Long> orderTaskIdList = orderTaskList.stream().map(a -> a.getId()).collect(Collectors.toList());

        // 1.拿到选中的结果

        // 2.拿到选中的样品测试

        // 3.遍历选中的结果

        List<EmcOrderTaskResultBean> selectOrderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID");

        List<EmcOrderTaskResultBean> allUpdateOrderTaskResultList = new ArrayList<>();

        orderTaskResultList.stream().forEach(a -> {

            List<EmcOrderTaskResultBean> itemOrderTaskResultList = selectOrderTaskResultList.stream()
                    .filter(b -> ObjectUtils.equals(a.getItemId(), b.getItemId()) && ObjectUtils.equals(a.getRepeatNo(), b.getRepeatNo())).collect(Collectors.toList());

            itemOrderTaskResultList.forEach(e -> e.setOriginalResult(a.getOriginalResult()));

            allUpdateOrderTaskResultList.addAll(itemOrderTaskResultList);

        });

        // 去重
        List<EmcOrderTaskResultBean> updateOrderTaskResultList = allUpdateOrderTaskResultList.stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingLong(o -> o.getId()))), ArrayList::new));

        orderTaskResultService.getDao().update(updateOrderTaskResultList, "ORIGINALRESULT");

        // 触发计算
        EmcOrderTaskResultComputingEngine.getEngine(selectOrderTaskResultList.stream().map(r -> r.getId()).collect(Collectors.toList())).compute();

        return orderTaskResultList.get(0).getId();
    }

    @Override
    public Page<EmcItemBean> selectEditItem(Long id,RestJsonWrapperBean wrapper) {
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
        List<Long> itemGroupIdList = orderItemGroupList.stream().map(a -> a.getItemGroupId()).distinct().collect(Collectors.toList());
        // 获取分析项
        List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID");
        List<Long> itemIdList = groupItemList.stream().map(a -> a.getItemId()).distinct().collect(Collectors.toList());
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
    public Long editItem(RestJsonWrapperBean wrapper) {
        EmcOrderTaskResultBean orderTaskResultTemp = wrapper.parseUnique(EmcOrderTaskResultBean.class);
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<EmcItemBean> itemList = wrapper.parse(EmcItemBean.class);

        // 取要操作的result筛选条件
        String runId = orderTaskResultTemp.getRunId();
        Long testMethodId = orderTaskResultTemp.getTestMethodId();

        List<EmcOrderTaskResultBean> addList = new ArrayList<>();
        List<EmcOrderTaskResultBean> delList = new ArrayList<>();

        List<Long> orderIdList = orderTaskList.stream().map(e -> e.getOrderId()).distinct().collect(Collectors.toList());
        List<EmcOrderBean> orderList = orderService.getDao().selectListByIds(orderIdList);
        Map<Long, List<EmcOrderTaskResultBean>> orderTaskResultMap = orderTaskResultService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID").stream()
                .filter(a -> ObjectUtils.equals(a.getRunId(), runId) && ObjectUtils.equals(a.getTestMethodId(), testMethodId)).collect(Collectors.groupingBy(b -> b.getOrderId()));
        orderTaskResultMap.forEach((key, value) -> {
            // 两个list都包含的数据
            List<EmcOrderTaskResultBean> tempList = new ArrayList<>();
            Long sampleNum = orderList.stream().filter(a -> ObjectUtils.equals(key, a.getId())).findAny().orElse(null).getSampleNum();

            value.stream().forEach(a -> {
                if (itemList.stream().anyMatch(b -> ObjectUtils.equals(a.getItemId(), b.getId()))) {
                    tempList.add(a);
                }
            });

            // 筛选要删除的数据
            value.stream().forEach(a -> {
                if (tempList.stream().noneMatch(b -> ObjectUtils.equals(a.getItemId(), b.getItemId()))) {
                    delList.add(a);
                }
            });

            // 筛选要增加的元素
            itemList.stream().forEach(a -> {
                if (tempList.stream().noneMatch(b -> ObjectUtils.equals(a.getId(), b.getItemId()))) {
                    // 装配数据
                    Long repeatItem = a.getRepeatItem() == null ? EmcConstant.ONE : a.getRepeatItem();
                    EmcOrderTaskResultBean orderTaskResult = value.get(0);

                    String pnFunc = a.getPnFunc();

                    if ((EmcConstant.PN_PERAVERAGE.equalsIgnoreCase(pnFunc) || EmcConstant.PN_DAYAVERAGE.equalsIgnoreCase(pnFunc)) && !ObjectUtils.equals(1L, sampleNum)) {
                        // 如果分析项PN函数类型是日均值或者次均值且采样数不等于一的,不用插入分析项
                    } else {
                        for (int i = 1; i <= repeatItem; i++) {
                            EmcOrderTaskResultBean orderTaskResultBean = new EmcOrderTaskResultBean();

                            BeanUtils.copyProperties(orderTaskResult, orderTaskResultBean, EmcConstant.CREATED_COLUMNS);
                            orderTaskResultBean.setTestMethodId(a.getTestMethodId());
                            orderTaskResultBean.setItemId(a.getId());
                            orderTaskResultBean.setItemOrderNo(a.getOrderNo());
                            orderTaskResultBean.setItemName(a.getItemName());
                            orderTaskResultBean.setItemAliasName(a.getItemAliasName());
                            orderTaskResultBean.setScriptFlag(a.getScriptFlag());
                            orderTaskResultBean.setResultCategory(a.getResultCategory());
                            orderTaskResultBean.setRevisionRule(a.getRevisionRule());
                            orderTaskResultBean.setUnitName(a.getUnitName());
                            orderTaskResultBean.setReportDisplay(a.getReportDisplay());
                            orderTaskResultBean.setLowLimit(a.getLowLimit());
                            orderTaskResultBean.setHighLimit(a.getHighLimit());
                            orderTaskResultBean.setOriginalResult(a.getDefaultResult());

                            orderTaskResultBean.setRepeatNo(NumberUtils.parseLong(i));
                            orderTaskResultBean.setId(ApplicationContextHelper.getNextIdentity());
                            addList.add(orderTaskResultBean);
                        }
                    }
                }
            });
        });
        orderTaskResultService.getDao().insert(addList);
        List<Long> delIdList = delList.stream().map(a -> a.getId()).collect(Collectors.toList());
        orderTaskResultService.getDao().deleteByIdList(delIdList);
        if (!addList.isEmpty()) {
            EmcOrderTaskResultComputingEngine.getEngine(addList.stream().map(e -> e.getId()).distinct().collect(Collectors.toList())).compute();
        }
        return itemList.get(0).getId();
    }

    @Override
    public void syncTmplInfo(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        String runId = orderTaskList.get(0).getRunId();
        List<EmcOrderTaskResultBean> orderTaskResultList = new ArrayList<>();
        // 同步单个样品下所有选中的分析项的结果
        if (orderTaskList.size() == 1) {
            List<EmcOrderTaskResultBean> selectOrderTaskResultList = wrapper.parse(EmcOrderTaskResultBean.class);
            List<Long> itemIds = selectOrderTaskResultList.stream().map(e -> e.getItemId()).collect(Collectors.toList());
            orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValue(runId, "RUNID").stream().filter(a -> itemIds.contains(a.getItemId())).collect(Collectors.toList());

            // 同步多个样品下的结果
        } else {
            List<Long> orderIdList = orderTaskList.stream().map(a -> a.getOrderId()).collect(Collectors.toList());
            orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID").stream().filter(a -> StringUtils.equals(runId, a.getRunId()))
                    .collect(Collectors.toList());
        }

        // 获取分析项
        List<Long> itemIds = orderTaskResultList.stream().map(e -> e.getItemId()).collect(Collectors.toList());
        List<EmcItemBean> itemList = itemService.selectListByFilter(SearchFilter.instance().match("ID", itemIds).filter(MatchPattern.OR));
        // 从分析项同步数值
        orderTaskResultList.stream().forEach(e -> {
            itemList.stream().forEach(v -> {
                if (e.getItemId().equals(v.getId())) {
                    e.setItemName(v.getItemName());
                    e.setItemOrderNo(v.getOrderNo());
                    e.setItemAliasName(v.getItemAliasName());
                    e.setRevisionRule(v.getRevisionRule());
                    e.setUnitName(v.getUnitName());
                    e.setLowLimit(v.getLowLimit());
                    e.setReportDisplay(v.getReportDisplay());
                    e.setScriptFlag(v.getScriptFlag());
                    e.setResultCategory(v.getResultCategory());
                    // 同步允许偏差
                    e.setRn13(v.getAllowableError());
                }
            });
        });
        orderTaskResultService.getDao().update(orderTaskResultList, "ITEMID", "ITEMNAME", "ITEMORDERNO", "ITEMALIASNAME", "REVISIONRULE", "UNITNAME", "LOWLIMIT", "REPORTDISPLAY", "SCRIPTFLAG", "RESULTCATEGORY", "RN13");
        List<Long> resultIdList = orderTaskResultList.stream().map(a -> a.getId()).distinct().collect(Collectors.toList());
        EmcOrderTaskResultComputingEngine.getEngine(resultIdList).compute();
    }

    @Override
    public void syncSubTmplInfo(RestJsonWrapperBean wrapper) {
        List<EmcFolderBean> folderList = wrapper.parse(EmcFolderBean.class);
        List<Long> folderIdList = folderList.stream().map(e -> e.getId()).collect(Collectors.toList());

        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.selectListByFilter(SearchFilter.instance().match("folderId",folderIdList).filter(MatchPattern.OR));
        List<Long> orderTaskIdList = orderTaskResultList.stream().map(e -> e.getOrderTaskId()).distinct().collect(Collectors.toList());
        //List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByIds(orderTaskIdList);
        List<EmcSubcontractRecordBean> subcontractRecordList = subcontractRecordService.selectListByFilter(SearchFilter.instance().match("ORDERTASKID",orderTaskIdList).filter(MatchPattern.OR));
        List<Long> subOrderTaskIdList = subcontractRecordList.stream().map(e -> e.getOrderTaskId()).collect(Collectors.toList());
        List<EmcOrderTaskBean> subOrderTaskList = orderTaskService.selectListByIds(subOrderTaskIdList);
        List<EmcOrderTaskResultBean> subOrderTaskResultList = new ArrayList<>();
        orderTaskResultList.forEach(e -> {
            subOrderTaskIdList.forEach(id -> {
                if (e.getOrderTaskId().equals(id)){
                    subOrderTaskResultList.add(e);
                }
            });
        });
        // 获取已添加的分析项
        List<Long> subItemIdList = subOrderTaskResultList.stream().map(e -> e.getItemId()).collect(Collectors.toList());

        List<EmcItemBean> itemList = itemService.selectListByFilter(SearchFilter.instance().match("ID", subItemIdList).filter(MatchPattern.OR));
        //获取测试管理中该测试当前的所有分析项
        List<Long> testMethodIdList = subOrderTaskResultList.stream().map(e -> e.getTestMethodId()).distinct().collect(Collectors.toList());
        List<EmcItemBean> testItemList = itemService.selectListByFilter(SearchFilter.instance().match("testmethodid",testMethodIdList).filter(MatchPattern.OR));

        // 从分析项同步数值
        orderTaskResultList.stream().forEach(e -> {
            itemList.stream().forEach(v -> {
                if (e.getItemId().equals(v.getId())) {
                    e.setItemName(v.getItemName());
                    e.setItemOrderNo(v.getOrderNo());
                    e.setItemAliasName(v.getItemAliasName());
                    e.setRevisionRule(v.getRevisionRule());
                    e.setUnitName(v.getUnitName());
                    e.setLowLimit(v.getLowLimit());
                    e.setReportDisplay(v.getReportDisplay());
                    e.setScriptFlag(v.getScriptFlag());
                    e.setResultCategory(v.getResultCategory());
                    // 同步允许偏差
                    e.setRn13(v.getAllowableError());
                }
            });
        });

        // 当前测试有，但是结果表没有添加的分析项
        List<EmcItemBean> reportDisplayItemList = testItemList.stream().filter(e -> "1".equals(e.getReportDisplay()))
                                                .filter(e -> "1".equals(e.getActivatedFlag())).collect(Collectors.toList());
        List<EmcItemBean> resultItemList = reportDisplayItemList.stream().filter(e -> !itemList.stream().map(v -> v.getId()).collect(Collectors.toList()).contains(e.getId())).collect(Collectors.toList());

        List<EmcOrderTaskResultBean> newResultBeanList = new ArrayList<>();
        //List<EmcOrderTaskResultBean> selectData = wrapper.parse(EmcOrderTaskResultBean.class);
        if (!resultItemList.isEmpty()) {
            int qty = 0;
            for (int i = 0; i < resultItemList.size(); i++) {
                qty = qty + resultItemList.get(i).getRepeatItem().intValue();
            }
            List<Long> generateIdList = ApplicationContextHelper.getNextIdentityList(qty);
            int count = 0;
            for (int k = 0; k < resultItemList.size(); k++) {
                EmcItemBean item = resultItemList.get(k);
                EmcOrderTaskBean orderTask = subOrderTaskList.stream().filter(e -> e.getTestMethodId().equals(item.getTestMethodId())).collect(Collectors.toList()).get(0);
                Long repeatItem = item.getRepeatItem();
                for (int i = 0; i < repeatItem; i++) {
                    EmcOrderTaskResultBean result = new EmcOrderTaskResultBean();
                    EmcOrderTaskResultBean orderTaskResultBean = subOrderTaskResultList.stream().filter(e -> e.getOrderTaskId().equals(orderTask.getId())).collect(Collectors.toList()).get(0);
                    BeanUtils.copyProperties(orderTaskResultBean,result);
                    result.setId(generateIdList.get(count++));
                    result.setItemId(item.getId());
                    result.setItemOrderNo(item.getOrderNo());
                    result.setItemName(item.getItemName());
                    result.setItemAliasName(item.getItemAliasName());
                    result.setScriptFlag(item.getScriptFlag());
                    result.setResultCategory(item.getResultCategory());
                    result.setRevisionRule(item.getRevisionRule());
                    result.setUnitName(item.getUnitName());
                    result.setRepeatNo(i + 1L);
                    result.setRepeatQty(NumberUtils.parseLong(repeatItem));
                    result.setReportDisplay(item.getReportDisplay());
                    result.setAnalysisType(item.getAnalysisCategory());
                    result.setLowLimit(StringUtils.isEmpty(item.getLowLimit()) ? null : item.getLowLimit());
                    result.setHighLimit(StringUtils.isEmpty(item.getHighLimit()) ? null : item.getHighLimit());
                    result.setOriginalResult(null);
                    result.setReportResult(null);
                    newResultBeanList.add(result);
                }
            }
            orderTaskResultService.getDao().insert(newResultBeanList);
        }

        orderTaskResultService.getDao().update(orderTaskResultList, "ITEMID", "ITEMNAME", "ITEMORDERNO", "ITEMALIASNAME", "REVISIONRULE", "UNITNAME", "LOWLIMIT", "REPORTDISPLAY", "SCRIPTFLAG", "RESULTCATEGORY", "RN13");
        List<Long> resultIdList = orderTaskResultList.stream().map(a -> a.getId()).distinct().collect(Collectors.toList());
        EmcOrderTaskResultComputingEngine.getEngine(resultIdList).compute();
    }

    @Override
    public void removeFomula(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        // 如果左边的样品只选择一条直接转到测试结果处理
        if (orderTaskList.size() == 1) {
            orderTaskResultService.removeFomula(wrapper);
        } else {
            String runId = orderTaskList.get(0).getRunId();
            List<Long> orderIdList = orderTaskList.stream().map(a -> a.getOrderId()).collect(Collectors.toList());
            List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID").stream()
                    .filter(a -> StringUtils.equals(runId, a.getRunId())).collect(Collectors.toList());

            List<EmcOrderTaskResultBean> selectOrderTaskResultList = orderTaskResultService.getDao().selectList(orderTaskResultList, Arrays.asList("ITEMID", "ORDERTASKID"), Collections.emptyList());
            selectOrderTaskResultList.stream().forEach(e -> {
                e.setScriptFlag("0");
            });
            orderTaskResultService.getDao().update(selectOrderTaskResultList, "SCRIPTFLAG");
        }
    }

    @Override
    public void modifyUnit(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<EmcOrderTaskResultBean> orderTaskResultList = wrapper.parse(EmcOrderTaskResultBean.class);
        EmcOrderTaskResultBean orderTaskResult = orderTaskResultList.get(0);
        String unitName = wrapper.getFilterValue("unitName");
        String runId = orderTaskResult.getRunId();
        List<Long> ItemIdList = orderTaskResultList.stream().map(a -> a.getItemId()).collect(Collectors.toList());

        // 判断是对样品进行操作还是只是对当前结果行进行操作
        if (orderTaskList.size() > 1) {
            List<Long> orderIdList = orderTaskList.stream().map(a -> a.getOrderId()).collect(Collectors.toList());
            // 选中样品下的orderTaskResult
            List<EmcOrderTaskResultBean> selectOrderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID").stream()
                    .filter(a -> StringUtils.equals(runId, a.getRunId()) && ItemIdList.contains(a.getItemId())).collect(Collectors.toList());
            selectOrderTaskResultList.stream().forEach(a -> a.setUnitName(unitName));
            orderTaskResultService.getDao().update(selectOrderTaskResultList, "UNITNAME");
        } else {
            orderTaskResultList.stream().forEach(a -> a.setUnitName(unitName));
            orderTaskResultService.getDao().update(orderTaskResultList, "UNITNAME");
        }

    }

    @Override
    public void modifyRevision(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<EmcOrderTaskResultBean> orderTaskResultList = wrapper.parse(EmcOrderTaskResultBean.class);
        EmcOrderTaskResultBean orderTaskResult = orderTaskResultList.get(0);
        String revisionRule = wrapper.getFilterValue("revisionRule");
        String runId = orderTaskResult.getRunId();
        List<Long> ItemIdList = orderTaskResultList.stream().map(a -> a.getItemId()).collect(Collectors.toList());
        // 判断是对样品进行操作还是只是对当前结果行进行操作
        if (orderTaskList.size() > 1) {
            List<Long> orderIdList = orderTaskList.stream().map(a -> a.getOrderId()).collect(Collectors.toList());
            // 选中样品下的orderTaskResult
            List<EmcOrderTaskResultBean> selectOrderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID").stream()
                    .filter(a -> StringUtils.equals(runId, a.getRunId()) && ItemIdList.contains(a.getItemId())).collect(Collectors.toList());
            selectOrderTaskResultList.stream().forEach(a -> a.setRevisionRule(revisionRule));
            orderTaskResultService.getDao().update(selectOrderTaskResultList, "revisionRule");
            EmcOrderTaskResultComputingEngine.getEngine(selectOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList())).compute();
        } else {
            orderTaskResultList.stream().forEach(a -> a.setRevisionRule(revisionRule));
            orderTaskResultService.getDao().update(orderTaskResultList, "revisionRule");
            EmcOrderTaskResultComputingEngine.getEngine(orderTaskResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList())).compute();
        }
    }

    @Override
    public List<EmcRunBean> selectCurveInfoListByFilter(SearchFilter filter) {

        MapperParameter parameter = new MapperParameter();

        parameter.setFilter(filter);

        List<Map<String, Object>> mapList = getDao().selectCurveInfoByCondition(parameter);

        if (mapList.isEmpty()) {
            return Collections.emptyList();
        }

        return mapList.stream().map(m -> PersistableHelper.mapToPersistable(m, getDao().getType())).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void modifyLimit(RestJsonWrapperBean wrapper) {
        EmcOrderTaskResultBean orderTaskResult = wrapper.parseUnique(EmcOrderTaskResultBean.class);
        String runId = orderTaskResult.getRunId();
        Long itemId = orderTaskResult.getItemId();
        EmcOrderTaskBean filter = new EmcOrderTaskBean();
        filter.setRunId(runId);
        filter.setStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED);
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectList(filter);
        List<Long> orderIdList = orderTaskList.stream().map(a -> a.getOrderId()).distinct().collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultBean = orderTaskResultService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID").stream()
                .filter(a -> StringUtils.equals(runId, a.getRunId())).filter(b -> ObjectUtils.equals(itemId, b.getItemId())).collect(Collectors.toList());
        orderTaskResultBean.stream().forEach(a -> {
            a.setUnitName(orderTaskResult.getUnitName());
            a.setLowLimit(orderTaskResult.getLowLimit());
            a.setHighLimit(orderTaskResult.getHighLimit());
            a.setReportDisplay(orderTaskResult.getReportDisplay());
        });
        orderTaskResultService.getDao().update(orderTaskResultBean, "UNITNAME", "LOWLIMIT", "HIGHLIMIT", "REPORTDISPLAY");
    }

    // 需要查询所有状态的数据，所以重新写了
    @Override
    public Page<EmcRunBean> selectAllPagination(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();

        parameter.setRawQueries();

        BeanPropertyDescriptor createdByOrgIdPropertyDescriptor = PersistableMetadataHelper.getCreatedByOrgIdPropertyDescriptor(EmcRunBean.class);
        if (createdByOrgIdPropertyDescriptor != null) {
            parameter.setOrgAuthority();
        }

        if (!wrapper.getAuthorityList().isEmpty()) {
            wrapper.getAuthorityList().forEach(a -> parameter.setAuthorityParameter(a));
        }

        return this.selectPagination(parameter, rowBounds);
    }

    @Override
    public Page<EmcRunBean> selectRunResult(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();

      return selectPagination(() -> getDao().selectRunResult(parameter), rowBounds);
    }

    @Override
    @Transactional
    public void methodEdit(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        EmcMonitorSchemeBean monitorScheme = wrapper.parseUnique(EmcMonitorSchemeBean.class);
        EmcOrderTaskBean orderTask = orderTaskList.get(0);
        // 批号
        String runId = orderTask.getRunId();
        // 方法ID
        Long methodId = NumberUtils.parseLong(monitorScheme.getExt$Item("methodid"));
        // 测试方法ID
        Long testMethodId = monitorScheme.getTestMethodId();
        // 测试方法依据
        String testMethodBasis = monitorScheme.getExt$Item("testmethodbasis");
        // 监测方案名称
        String methodName = monitorScheme.getExt$Item("methodname");
        // 方法版本ID
        Long methodVersionId = NumberUtils.parseLong(monitorScheme.getExt$Item("methodversionid"));
        // 分析项组ID
        Long itemGroupId = monitorScheme.getItemGroupId();
        // 样品ID
        List<Long> orderIdList = orderTaskList.stream().map(a -> a.getOrderId()).distinct().collect(Collectors.toList());
        // 样品分析项组
        EmcOrderItemGroupBean orderItemGroupFilter = new EmcOrderItemGroupBean();
        orderItemGroupFilter.setTestId(orderTask.getTestId());
        orderItemGroupFilter.setMethodId(orderTask.getMethodId());
        orderItemGroupFilter.setTestMethodId(orderTask.getTestMethodId());
        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectList(orderItemGroupFilter).stream().filter(a -> orderIdList.contains(a.getOrderId()))
                .collect(Collectors.toList());
        // 获取分析项ID
        List<EmcGroupItemBean> GroupItemList = groupItemService.getDao().selectListByOneColumnValue(itemGroupId, "ITEMGROUPID");
        List<Long> itemIdList = GroupItemList.stream().map(a -> a.getItemId()).distinct().collect(Collectors.toList());
        List<EmcItemBean> itemList = itemService.getDao().selectListByIds(itemIdList);
        List<String> itemAliasNameList = itemList.stream().map(a -> a.getItemAliasName()).distinct().collect(Collectors.toList());

        // 获得样品测试下的结果信息
        List<Long> orderTaskId = orderTaskList.stream().map(a -> a.getId()).distinct().collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskId, "ORDERTASKID").stream()
                .filter(b -> ObjectUtils.equals(runId, b.getRunId())).collect(Collectors.toList());

        // 各ordertask下的result数据
        Map<Long, List<EmcOrderTaskResultBean>> orderTaskResultMap = orderTaskResultList.stream().collect(Collectors.groupingBy(EmcOrderTaskResultBean::getOrderTaskId));

        // 需要添加的数据
        List<EmcOrderTaskResultBean> addList = new ArrayList<>();
        // 需要删除的数据
        List<EmcOrderTaskResultBean> removeList = new ArrayList<>();
        // 需要更新的数据
        List<EmcOrderTaskResultBean> updateList = new ArrayList<>();
        // 遍历样品测试修改数据
        orderTaskList.stream().forEach(a -> {
            a.setMethodId(methodId);
            a.setTestMethodId(testMethodId);
            a.setTestMethodBasis(testMethodBasis);
            a.setMethodName(methodName);
            a.setMethodVersionId(methodVersionId);
        });
        // 遍历样品分析项组表修改数据
        orderItemGroupList.stream().forEach(a -> {
            a.setMethodId(methodId);
            a.setTestMethodId(testMethodId);
            a.setTestMethodBasis(testMethodBasis);
            a.setMethodName(methodName);
            a.setMethodVersionId(methodVersionId);
            a.setItemGroupId(monitorScheme.getItemGroupId());
            a.setItemGroup(monitorScheme.getItemGroup());
        });
        // 判断结果分析项是否相同用什么字段还要再确认
        // 遍历结果,如果新选方法不包含该分析项则删除
        orderTaskResultList.stream().forEach(a -> {
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
            List<String> resultItemAliasNameList = value.stream().filter(a -> itemAliasNameList.contains(a.getItemAliasName())).map(b -> b.getItemAliasName()).collect(Collectors.toList());

            itemAliasNameList.stream().filter(e -> !resultItemAliasNameList.contains(e)).forEach(a -> {

                EmcItemBean itemBean = itemList.stream().filter(b -> ObjectUtils.equals(a, b.getItemAliasName())).findAny().orElse(null);
                // 筛选启用的分析项
                if (itemBean != null && Constant.ACTIVATED_STATUS_YES.equals(itemBean.getActivatedFlag())) {
                    Long repeatItem = itemBean.getRepeatItem();
                    // 根据重复数插入多条分析项
                    for (Long i = 1L; i <= repeatItem; i++) {
                        EmcOrderTaskResultBean orderTaskResult = new EmcOrderTaskResultBean();
                        BeanUtils.copyProperties(resultBean, orderTaskResult, new String[] { "ORIGINALRESULT", "REPORTRESULT", "RESULTSTATUS", "QCCATEGORYID", "QCCATEGORYCODE", "QCNAME",
                                "CREATEDBYID", "CREATEDBYNAME", "CREATEDTIME", "CREATEDBYORGID", "CREATEDBYORGNAME" });

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
        List<Long> delIdList = removeList.stream().map(a -> a.getId()).collect(Collectors.toList());
        orderTaskService.getDao().update(orderTaskList, "METHODID", "TESTMETHODID", "TESTMETHODBASIS", "METHODNAME", "METHODVERSIONID");
        orderItemGroupService.getDao().update(orderItemGroupList, "METHODID", "TESTMETHODID", "TESTMETHODBASIS", "METHODNAME", "METHODVERSIONID", "ITEMGROUPID", "ITEMGROUP");
        // 插入样品测试记录表
        orderTaskLogService.insert(orderTaskList, EmcBizConstant.ORDER_TASK_LOG_EDIT_METHOD, EmcBizConstant.ORDER_TASK_LOG_ANALYSE);

        orderTaskResultService.getDao().deleteByIdList(delIdList);
        orderTaskResultService.getDao().insert(addList);
        orderTaskResultService.getDao().update(updateList, "METHODID", "METHODVERSIONID", "TESTMETHODID", "ITEMID", "ITEMNAME", "HIGHLIMIT", "LOWLIMIT", "UNITNAME", "REVISIONRULE");

    }

    @Override
    @Transactional
    public void triggerFormlua(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<EmcOrderTaskBean> relatedOrderTaskList = orderTaskService.getDao().selectList(orderTaskList, Arrays.asList("ORDERID", "RUNID"), Collections.singletonList("ID"));

        List<Long> orderTaskIdList = relatedOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> resultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID", Collections.singletonList("ID"));
        EmcOrderTaskResultComputingEngine.getEngine(resultList.stream().map(EmcOrderTaskResultBean::getId).distinct().collect(Collectors.toList())).compute();
    }

    /**
     * 数据相关性校验 允许同时检查多条分析审核数据； 根据项目编码、监测模板ID查询所有样品测试结果；
     * 判断样品检测结果是否包含了相关性检查中配置的所有分析项，只有全包含的才使用相关性设置上定义的公式进行检查；
     * 检查结果要写入到样品检测结果表中的correlationValidateFlag字段； 返回未通过的数据；
     * 返回值：全部合理返回“1”，否则返回“0”
     */
    @Override
    @Log(value = "数据相关性校验", type = LogType.UPDATE)
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public String validateCorrelation(RestJsonWrapperBean wrapper) {

        List<EmcRunBean> runList = wrapper.parse(EmcRunBean.class);
        List<EmcOrderTaskBean> orderTaskList = new ArrayList<>();

        if(!runList.isEmpty()) {

            List<String> runIdList = runList.stream().map(a -> a.getId()).collect(Collectors.toList());

            orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(runIdList, "RUNID");
        }
        else {
            List<EmcProjectBean> projectList = wrapper.parse(EmcProjectBean.class);

            // 根据项目编码、监测模板ID查询所有样品测试结果
            List<EmcOrderTaskBean> filterList = new ArrayList<>();
            projectList.forEach(e -> {
                EmcOrderTaskBean filter = new EmcOrderTaskBean();
                filter.setProjId(e.getId());
                filter.setMonitorTmplId(e.getExt$Item("monitortmplid"));
                filterList.add(filter);
            });
            orderTaskList = orderTaskService.getDao().selectList(filterList, Arrays.asList("PROJID", "MONITORTMPLID"), Collections.emptyList());
        }

        List<Long> orderTaskIdList = orderTaskList.stream().map(a -> a.getId()).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> resultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID");

        List<EmcOrderTaskResultBean> orderTaskResultList = resultList.stream().filter(e -> Constant.YES.equals(e.getReportDisplay()) && ObjectUtils.equals(e.getRepeatNo(), 1l))
                .collect(Collectors.toList());
        if (orderTaskResultList.isEmpty()) {
            return "1";
        }

        // 获取数据相关性数据,数据量比较少，查询所有
        List<EmcDataCorrelationBean> allDataCorrelationList = dataCorrelationService
                .selectListByFilter(SearchFilter.instance().match("activatedFlag", Constant.ACTIVATED_STATUS_YES).filter(MatchPattern.EQ));
        if (allDataCorrelationList.isEmpty()) {
            return "1";
        }
        List<EmcDataCorrelationItemBean> allDataCorrelationItemList = dataCorrelationItemService
                .selectListByFilter(SearchFilter.instance().match("correlationId", allDataCorrelationList.stream().map(e -> e.getId()).collect(Collectors.toList())).filter(MatchPattern.OR));
        if (allDataCorrelationItemList.isEmpty()) {
            return "1";
        }
        List<Long> allDataCorrelationItemIdList = allDataCorrelationItemList.stream().map(e -> e.getItemId()).distinct().collect(Collectors.toList());
        if (allDataCorrelationItemIdList.isEmpty()) {
            return "1";
        }

        // 筛选出所有维护了某一个相关性分析项的样品测试结果数据
        List<EmcOrderTaskResultBean> correlationOrderTaskResultList = orderTaskResultList.stream().filter(orderTaskResult -> allDataCorrelationItemIdList.contains(orderTaskResult.getItemId()))
                .collect(Collectors.toList());
        if (correlationOrderTaskResultList.isEmpty()) {
            return "1";
        }

        // 循环
        List<EmcDataCorrelationDTO> dataCorrelationDTOList = new ArrayList<>();
        for (EmcOrderTaskResultBean orderTaskResult : correlationOrderTaskResultList) {
            Long orderId = orderTaskResult.getOrderId();
            // 该样品下的所有维护了某一个相关性分析项的样品测试结果
            List<EmcOrderTaskResultBean> filterOrderTaskResultList = correlationOrderTaskResultList.stream().filter(e -> ObjectUtils.equals(e.getOrderId(), orderId)).collect(Collectors.toList());
            //
            List<Long> orderFilterItemIdList = filterOrderTaskResultList.stream().map(e -> e.getItemId()).distinct().collect(Collectors.toList());

            // 分析项编码
            Long itemId = orderTaskResult.getItemId();
            List<EmcDataCorrelationItemBean> correlationItemList = allDataCorrelationItemList.stream().filter(e -> ObjectUtils.equals(itemId, e.getItemId())).collect(Collectors.toList());
            correlationItemList.forEach(correlationItem -> {
                // 同一个数据相关性下所有分析项
                List<EmcDataCorrelationItemBean> sameCorrelationItemList = allDataCorrelationItemList.stream()
                        .filter(dataCorrelationItem -> ObjectUtils.equals(dataCorrelationItem.getCorrelationId(), correlationItem.getCorrelationId())).collect(Collectors.toList());
                List<Long> sameCorrelationItemIdList = sameCorrelationItemList.stream().map(sameCorrelationItem -> sameCorrelationItem.getItemId()).collect(Collectors.toList());

                // 只有同样品下含有相关性维护的所有分析项，才进行相关性的校验
                if (orderFilterItemIdList.containsAll(sameCorrelationItemIdList)) {
                    // 同一个数据相关性下要校验的样品结果
                    List<EmcOrderTaskResultBean> validateOrderTaskResultList = filterOrderTaskResultList.stream().filter(r -> sameCorrelationItemIdList.contains(r.getItemId()))
                            .collect(Collectors.toList());
                    // 构造需要进行校验的DTO
                    EmcDataCorrelationDTO dataCorrelationDTO = new EmcDataCorrelationDTO();
                    dataCorrelationDTO.setDataCorrelation(allDataCorrelationList.stream().filter(d -> ObjectUtils.equals(d.getId(), correlationItem.getCorrelationId())).findFirst().get());
                    dataCorrelationDTO.setOrderTaskResultList(validateOrderTaskResultList);
                    // 收集起来
                    dataCorrelationDTOList.add(dataCorrelationDTO);
                }
            });
        }

        // 校验
        EmcDataCorrelationValidatingEngine dataCorrelationValidatingEngine = EmcDataCorrelationValidatingEngine.getEngine(dataCorrelationDTOList);
        return dataCorrelationValidatingEngine.validate() ? "1" : "0";
    }

    @Override
    @Transactional
    // 将上传文件复制到指定的采集文件夹下
    public void equiptCollect(String id, RestJsonWrapperBean wrapper) {

        List<CoreFileBean> fileList = coreFileService.getDao().selectListByIds(wrapper.parse(CoreFileBean.class).stream().map(e -> e.getId()).collect(Collectors.toList()));

        String equiptDirString = ApplicationContextHelper.getEnvironment().getProperty("sunway.equipt.acq.file.local.path");
        String equiptNo = equiptService.selectById(selectById(id).getEquiptId()).getEquiptId();

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
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    @ValidateDataWith(EmcRunAuditSubmitValidator.class)
    public void submitAnalysisProject(RestJsonWrapperBean wrapper) {
        List<EmcProjectBean> projectList = wrapper.parse(EmcProjectBean.class);
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
        orderTaskList.forEach(e -> {
            e.setStatus(EmcBizConstant.ORDERTASK_STATUS_DONE);
        });
        orderTaskService.getDao().update(orderTaskList, "STATUS");

        // 插入样品测试操作记录表
        orderTaskLogService.insert(orderTaskList, EmcBizConstant.ORDER_TASK_LOG_LEADER_AUDIT, EmcBizConstant.ORDER_TASK_LOG_ANALYSE_AUDIT);

        // 插入样品测试关联批的审核人信息
        List<String> runIdList = orderTaskList.stream().filter(a -> a.getRunId() != null).map(EmcOrderTaskBean::getRunId).collect(Collectors.toList());
        EmcRunBean run = new EmcRunBean();
        run.setAuditedUserId(LocalContextHelper.getLoginUserId());
        run.setAuditedUserName(LocalContextHelper.getLoginUser().getUserName());
        run.setAuditedDate(LocalDateTime.now());
        run.setStatus(EmcBizConstant.RUN_STATUS_DONE);
        getDao().updateByIds(run, runIdList, "AUDITEDUSERID", "AUDITEDUSERNAME", "AUDITEDDATE", "STATUS");

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
    public Page<EmcRunBean> selectRunAuditTodoPagination(RestJsonWrapperBean wrapper) {
        wrapper.setFilterValue("showTotalFlag", Constant.NO);
        wrapper.setFilterValue("checkUserId", LocalContextHelper.getLoginUserId());
        wrapper.setFilterValue("analysisUserId_DIFFER", LocalContextHelper.getLoginUserId());
        return selectAuditablePagination(wrapper);
    }

    @Override
    public Page<EmcOrderTaskBean> selectOrderTaskPagination(RestJsonWrapperBean wrapper) {
        List<EmcRunBean> runList = selectListByFilter(SearchFilter.instance(), wrapper);
        List<String> runIdList = runList.stream().map(EmcRunBean::getId).collect(Collectors.toList());
        if (runIdList.isEmpty()) {
            return new Pagination<>();
        }

        List<EmcOrderTaskBean> blankOrderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("RUNID", runIdList).filter(MatchPattern.OR).match("QCCATEGORYCODE", EmcConstant.QCCATEGORY_QCCATEGORYID_L_BLANK).filter(MatchPattern.EQ));
        Map<String, EmcOrderTaskBean> runFirstOrderTaskMap = blankOrderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getRunId, Collectors.collectingAndThen(Collectors.toList(), list -> list.get(0))));

        return new Pagination<>(new ArrayList<>(runFirstOrderTaskMap.values()));
    }

    @Override
    public void copyBlankTo(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> selectedOrderTaskList = wrapper.parse(EmcOrderTaskBean.class, 0);
        String selectedRunId = selectedOrderTaskList.get(0).getRunId();
        String selectedOrderNo = selectedOrderTaskList.get(0).getExt$Item("orderno");
        List<Long> copiedOrderIdList = selectedOrderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> copiedOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(copiedOrderIdList, "ORDERID");

        List<EmcRunBean> targetRunList = wrapper.parse(EmcRunBean.class, 1);

        List<String> targetRunIdList = targetRunList.stream().map(EmcRunBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(targetRunIdList, "RUNID");
        List<EmcOrderTaskBean> naOrderTaskList = orderTaskList.stream().filter(task -> StringUtils.equals(task.getQcCategoryCode(), EmcConstant.N_A)).collect(Collectors.toList());

        List<Long> copiedOrderTaskIdList = copiedOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(copiedOrderTaskIdList, "ORDERTASKID");

        List<EmcQcCategoryBean> qcCategoryList = qcCategoryService.getDao().selectListByOneColumnValue(EmcConstant.QCCATEGORY_QCCATEGORYID_L_BLANK, "QCCATEGORYID");

        Map<String, List<EmcOrderTaskBean>> runOrderTaskMap = naOrderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getRunId));
        runOrderTaskMap.forEach((runId, runOrderTaskList) -> {
            addQc(runOrderTaskList, qcCategoryList, null, orderTaskResultList, selectedOrderNo);
        });

        targetRunList.forEach(run -> run.setRelatedRunId(selectedRunId));
        getDao().update(targetRunList, "RELATEDRUNID");
    }

    @Override
    public void copyBlankSpkcTo(RestJsonWrapperBean wrapper) {
        List<EmcQcCategoryBean> qcCategoryList = qcCategoryService.getDao().selectListByOneColumnValue(EmcConstant.QCCATEGORY_QCCATEGORYID_L_SPKCK, "QCCATEGORYID");
        if (qcCategoryList.isEmpty()) {
            return;
        }

        List<EmcOrderTaskBean> selectedOrderTaskList = wrapper.parse(EmcOrderTaskBean.class, 0);
        String selectedOrderNo = selectedOrderTaskList.get(0).getExt$Item("orderno");
        List<Long> copiedOrderIdList = selectedOrderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> copiedOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(copiedOrderIdList, "ORDERID");

        List<EmcOrderTaskBean> targetOrderTaskList = wrapper.parse(EmcOrderTaskBean.class, 1);

        List<Long> copiedOrderTaskIdList = copiedOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(copiedOrderTaskIdList, "ORDERTASKID");

        Map<String, List<EmcOrderTaskBean>> runOrderTaskMap = targetOrderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getRunId));
        runOrderTaskMap.forEach((runId, runOrderTaskList) -> {
            addQc(runOrderTaskList, qcCategoryList, null, orderTaskResultList, selectedOrderNo);
        });

    }

    @Override
    public void batchInputResultRule(RestJsonWrapperBean wrapper) {
        if (wrapper.getBodyList().size() < 2) {
            List<EmcOrderTaskResultBean> orderTaskResultList = wrapper.parse(EmcOrderTaskResultBean.class, 0);
            orderTaskResultService.getDao().update(orderTaskResultList, "RESULTRULE");
            return;
        }

        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class, 0);
        List<EmcOrderTaskResultBean> orderTaskResultList = wrapper.parse(EmcOrderTaskResultBean.class, 1);

        List<Long> orderTaskIdList = orderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<Long> itemIdList = orderTaskResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskResultBean> resultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID", Arrays.asList("ID", "ITEMID"));
        List<EmcOrderTaskResultBean> updateResultList = resultList.stream().filter(result -> itemIdList.contains(result.getItemId())).collect(Collectors.toList());
        updateResultList.forEach(result -> result.setResultRule(orderTaskResultList.get(0).getResultRule()));

        orderTaskResultService.getDao().update(updateResultList, "RESULTRULE");
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
    }

    @Override
    public void recomposeProjectRunPdf(RestJsonWrapperBean wrapper) {
        EmcProjectBean project = wrapper.parseUnique(EmcProjectBean.class);
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValue(project.getId(), "PROJID", Arrays.asList("ID", "RUNID", "PROJID"));
        List<String> runIdList = orderTaskList.stream().filter(task -> !StringUtils.isEmpty(task.getRunId())).map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());
        List<EmcRunBean> runList = getDao().selectListByIds(runIdList);
        // 生成批原始记录PDF
        composeRunReportPdf(runList);
        // 生成项目原始记录PDF
        composeProjectPdfReport(orderTaskList);
    }

    // =============================私有方法======================

    private Long addQc(List<EmcOrderTaskBean> orderTaskList, List<EmcQcCategoryBean> qcCategoryList, String stockBatchNo, List<EmcOrderTaskResultBean> copiedResultList, String relatedOrderNo) {
        Map<Long, EmcStockConcentBean> itemConcentMap = new HashMap<>();
        if (!StringUtils.isEmpty(stockBatchNo)) {
            List<EmcStockBean> stockList = stockService.getDao().selectListByOneColumnValue(stockBatchNo, "BATCHNO");
            List<Long> stockIdList = stockList.stream().map(EmcStockBean::getId).distinct().collect(Collectors.toList());
            List<EmcStockConcentBean> stockConcentList = stockConcentService.getDao().selectListByOneColumnValues(stockIdList, "STOCKID");
            List<Long> maintainIdList = stockConcentList.stream().map(EmcStockConcentBean::getMtlMaintainId).distinct().collect(Collectors.toList());
            List<EmcMtlMaintainItemBean> mtlMaintainList = mtlMaintainItemService.getDao().selectListByOneColumnValues(maintainIdList, "MTLMAITAINID");
            mtlMaintainList.forEach(m -> {
                EmcStockConcentBean stockConcent = stockConcentList.stream().filter(c -> ObjectUtils.equals(m.getMtlMaitainId(), c.getMtlMaintainId())).findFirst().get();
                itemConcentMap.put(m.getItemId(), stockConcent);
            });
        }

        // 2、准备数据

        String runId = orderTaskList.get(0).getRunId();
        // 批下所有的orderTask记录，步骤5更新杯号要用
        List<EmcOrderTaskBean> orderTaskAllList = orderTaskService.getDao().selectListByOneColumnValue(runId, "RUNID", Order.asc("CUPNO"));
        // order表数据
        List<Long> idList = orderTaskList.stream().map(e -> e.getOrderId()).distinct().collect(Collectors.toList());
        List<EmcOrderBean> selectOrderList = orderService.getDao().selectListByIds(idList);
        // orderTask表数据，这是要复制的数据
        List<EmcOrderTaskBean> selectOrderTaskList = orderTaskAllList.stream().filter(a -> idList.contains(a.getOrderId())).collect(Collectors.toList());
        // 根据样品测试的杯号生成对样品排序后的orderList
        Map<Long, List<EmcOrderTaskBean>> orderTaskMap = selectOrderTaskList.stream().collect(Collectors.groupingBy(a -> a.getOrderId()));
        List<EmcOrderTaskBean> sortList = new ArrayList<>();
        orderTaskMap.forEach((key, value) -> {
            sortList.add(value.get(0));
        });
        CollectionUtils.sort(sortList, (o1, o2) -> o1.getCupNo().compareTo(o2.getCupNo()));
        List<EmcOrderBean> orderList = new ArrayList<>();
        sortList.stream().forEach(a -> {
            Long id = a.getOrderId();
            EmcOrderBean order = selectOrderList.stream().filter(b -> ObjectUtils.equals(b.getId(), id)).findAny().orElse(null);
            orderList.add(order);
        });
        // orderTaskResult表数据
        List<Long> orderTaskIdList = selectOrderTaskList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID");
        // 根据测试ID取测试下启用的分析类型为分析的分析项
        List<Long> testMethodIdList = selectOrderTaskList.stream().map(a -> a.getTestMethodId()).distinct().collect(Collectors.toList());
        List<EmcItemBean> itemTempList = itemService.selectListByFilter(SearchFilter.instance().match("TESTMETHODID", testMethodIdList).filter(MatchPattern.OR)
                .match("ACTIVATEDFLAG", Constant.ACTIVATED_STATUS_YES).filter(MatchPattern.EQ).match("ANALYSISCATEGORY", EmcBizConstant.ANALYSISCATEGORY_ANALYSIS).filter(MatchPattern.EQ));
        List<EmcItemBean> itemList = new ArrayList<>();
        List<EmcOrderItemGroupBean> oldOrderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(idList, "ORDERID");
        // 限制分析项需要时对应分析项组里面的数据
        if (!oldOrderItemGroupList.isEmpty()) {
            List<Long> itemGroupIdList = oldOrderItemGroupList.stream().map(a -> a.getItemGroupId()).distinct().collect(Collectors.toList());
            // 获取分析项
            List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID");
            List<Long> itemIdList = groupItemList.stream().map(a -> a.getItemId()).distinct().collect(Collectors.toList());
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
            List<Long> itemGroupIdList = orderItemGroupList.stream().map(a -> a.getItemGroupId()).distinct().collect(Collectors.toList());
            // 获取分析项
            List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID");
            List<Long> itemIdList = groupItemList.stream().map(a -> a.getItemId()).distinct().collect(Collectors.toList());
            itemList = itemTempList.stream().filter(e -> itemIdList.contains(e.getId())).collect(Collectors.toList());
        }

        // 准备存放插入数据用的List
        List<EmcFolderBean> folderList = new ArrayList<>();
        List<EmcOrderBean> copyOrderList = new ArrayList<>();
        List<EmcOrderTaskBean> copyOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> copyOrderTaskResultList = new ArrayList<>();
        List<EmcOrderItemGroupBean> newOrderItemGroupList = new ArrayList<>();

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
            EmcOrderBean tempOrderSecond = orderService.getDao().selectFirstIfPresent(tempOrderFirst, Order.desc("ORDERNO"));
            orderNo = tempOrderSecond.getOrderNo();
        }
        // 点位编号累加值
        String orderNoTemp = orderNo;

        // 4、添加order、ordertask、ordertaskresult数据
        List<Long> defaultValueResultIdList = new ArrayList<>();

        for (int i = 0; i < orderList.size(); i++) {
            // 实验室空白类型数据杯号添加次数累加值
            Long labBlankIndex = 0L;
            // 样品下杯号添加次数累加值
            Long addTotalIndex = 0L;
            for (int j = 0; j < qcCategoryList.size(); j++) {
                EmcQcCategoryBean qcCategory = qcCategoryList.get(j);
                Long addNum = qcCategory.getAddNum() == null ? 1L : qcCategory.getAddNum();
                boolean qcNameFlag = EmcConstant.QCCATEGORY_QCCATEGORYID_L_BLANK.equals(qcCategory.getQcCategoryId()); // 实验室空白类型数据需特殊处理
                boolean updateParNoFlag = "1".equals(qcCategory.getUpdateParNo()); // 是否更新父样编号字段
                boolean parOrderFlag = "1".equals(qcCategory.getParOrder()); // 是否有父样
                // 根据qc类型的添加数量插入多条数据
                for (Long num = 0L; num < addNum; num++) {

                    // 1)添加order表数据
                    EmcOrderBean order = orderList.get(i);
                    EmcOrderBean orderInfo = new EmcOrderBean();
                    BeanUtils.copyProperties(order, orderInfo, new String[] { "ID", "PARENTORDERNO", "CREATEDBYID", "CREATEDBYNAME", "CREATEDTIME", "CREATEDBYORGID", "CREATEDBYORGNAME", "SAMPLEID" });
                    Long id = ApplicationContextHelper.getNextIdentity();
                    orderInfo.setId(id);
                    orderInfo.setFolderId(folderId);
                    orderNoTemp = getNextOrderNo(orderNoTemp);
                    orderInfo.setOrderNo(qcFolder.getFolderNo() + orderNoTemp);
                    orderInfo.setLabelNo(orderInfo.getOrderNo());
                    orderInfo.setMonitorTmplId(order.getMonitorTmplId());
                    orderInfo.setQcCategoryId(qcCategory.getId());
                    orderInfo.setQcCategoryCode(qcCategory.getQcCategoryId());
                    orderInfo.setQcName(qcCategory.getQcName());
                    orderInfo.setParentOrderNo(null);
                    orderInfo.setOrderDesc(null);
                    orderInfo.setProjId(null);

                    if (!StringUtils.isEmpty(relatedOrderNo)) {
                        orderInfo.setRelatedOrderNo(relatedOrderNo);
                    }

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

                    for (int k = 0; k < selectOrderTaskList.size(); k++) {
                        EmcOrderTaskBean orderTask = selectOrderTaskList.get(k);
                        if (ObjectUtils.equals(orderTask.getOrderId(), order.getId())) {

                            // 2)添加ordertask数据
                            // 添加ordertask标志，判断ordertask下是否有result插入，如果有再插入ordertask
                            boolean addOrderTaskFlag = false;
                            EmcOrderTaskBean otInfo = new EmcOrderTaskBean();
                            BeanUtils.copyProperties(orderTask, otInfo,
                                    new String[] { "ID", "CUPNO", "CREATEDBYID", "CREATEDBYNAME", "CREATEDTIME", "CREATEDBYORGID", "CREATEDBYORGNAME", "STOCKID", "STOCKBATCHNO" });
                            Long orderTaskId = ApplicationContextHelper.getNextIdentity();

                            otInfo.setId(orderTaskId);
                            otInfo.setFolderId(folderId);
                            otInfo.setOrderId(id);
                            otInfo.setEquiptAcquNo(orderInfo.getLabelNo());
                            otInfo.setMonitorTmplId(orderTask.getMonitorTmplId());
                            otInfo.setQcCategoryId(qcCategory.getId());
                            otInfo.setQcCategoryCode(qcCategory.getQcCategoryId());
                            otInfo.setQcName(qcCategory.getQcName());
                            otInfo.setStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED);
                            otInfo.setRunId(runId);
                            otInfo.setParentId(orderTask.getId());
                            otInfo.setCupNo(null);
                            otInfo.setProjId(null);

                            if (updateParNoFlag) {
                                otInfo.setParentId(orderTask.getId());
                            }

                            // 对qc类型是QC-STD插入库存编号
                            if (EmcConstant.QCCATEGORY_QCCATEGORYID_QC_STD.equals(qcCategory.getQcCategoryId())) {
                                otInfo.setStockBatchNo(stockBatchNo);
                            }

                            if (!itemList.isEmpty()) {
                                for (EmcItemBean b : itemList) {
                                    for (int e = 0; e < orderTaskResultList.size(); e++) {
                                        // 3)插入ordertaskresult数据
                                        EmcOrderTaskResultBean orderTaskResult = orderTaskResultList.get(e);
                                        if (ObjectUtils.equals(orderTaskResult.getOrderTaskId(), orderTask.getId())) {
                                            if (ObjectUtils.equals(orderTaskResult.getItemId(), b.getId()) && !StringUtils.isEmpty(b.getQcCategoryId())
                                                    && b.getQcCategoryId().contains("" + qcCategory.getId())) {
                                                addOrderTaskFlag = true;
                                                addOrderFlag = true;

                                                EmcOrderTaskResultBean otrInfo = new EmcOrderTaskResultBean();
                                                BeanUtils.copyProperties(orderTaskResult, otrInfo,
                                                        new String[] { "ORIGINALRESULT", "REPORTRESULT", "CREATEDBYID", "CREATEDBYNAME", "CREATEDTIME", "CREATEDBYORGID", "CREATEDBYORGNAME" });
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

                                                // 拷贝其他批次的结果
                                                copiedResultList.stream().filter(result -> ObjectUtils.equals(result.getItemId(), b.getId()) && !StringUtils.isEmpty(result.getOriginalResult())).findFirst().ifPresent(result -> {
                                                    otrInfo.setOriginalResult(result.getOriginalResult());
                                                    otrInfo.setReportResult(result.getReportResult());
                                                });

                                                if (updateParNoFlag) {
                                                    otrInfo.setParentOrderNo(order.getOrderNo());
                                                }

                                                // 复制实验室标准样品标准值、不确定度和浓度单位
                                                if (itemConcentMap.size() > 0 && itemConcentMap.get(b.getId()) != null) {
                                                    EmcStockConcentBean stockConcent = itemConcentMap.get(b.getId());
                                                    String uncertainty = Optional.ofNullable(stockConcent.getUncertainty()).orElse(stockConcent.getUncertaintyPercent() + "%");

                                                    otrInfo.setStandQty(ObjectUtils.isEmpty(stockConcent.getStdValue()) ? "" : stockConcent.getStdValue() + "");
                                                    //otrInfo.setRn16(ObjectUtils.isEmpty(stockConcent.getUncertainty()) ? "" : stockConcent.getUncertainty() + "");
                                                    otrInfo.setRn16(uncertainty);
                                                    otrInfo.setUnitName(ObjectUtils.isEmpty(stockConcent.getConcentUnitName()) ? "" : stockConcent.getConcentUnitName());
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
                                    if (orderTaskResultList.stream().noneMatch(z -> z.getItemId().equals(b.getId()))) {
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
                                            otrInfo.setTestId(orderTaskResultList.get(0).getTestId());
                                            otrInfo.setMethodId(orderTaskResultList.get(0).getMethodId());
                                            otrInfo.setTestMethodId(b.getTestMethodId());
                                            otrInfo.setMethodVersionId(orderTaskResultList.get(0).getMethodVersionId());
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

                                            if (updateParNoFlag) {
                                                otrInfo.setParentOrderNo(order.getOrderNo());
                                            }

                                            // 复制实验室标准样品标准值、不确定度和浓度单位
                                            if (itemConcentMap.size() > 0 && itemConcentMap.get(b.getId()) != null) {
                                                EmcStockConcentBean stockConcent = itemConcentMap.get(b.getId());
                                                String uncertainty = Optional.ofNullable(stockConcent.getUncertainty()).orElse(stockConcent.getUncertaintyPercent() + "%");

                                                otrInfo.setStandQty(ObjectUtils.isEmpty(stockConcent.getStdValue()) ? "" : stockConcent.getStdValue() + "");
                                                //otrInfo.setRn16(ObjectUtils.isEmpty(stockConcent.getUncertainty()) ? "" : stockConcent.getUncertainty() + "");
                                                otrInfo.setRn16(uncertainty);
                                                otrInfo.setUnitName(ObjectUtils.isEmpty(stockConcent.getConcentUnitName()) ? "" : stockConcent.getConcentUnitName());
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
                        if (qcNameFlag) {
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
            folderService.getDao().insert(folderList);
            orderService.getDao().insert(copyOrderList);
            orderTaskService.getDao().insert(copyOrderTaskList);
            // 过滤下没有添加的result
            List<Long> realorderTaskIdList = copyOrderTaskList.stream().map(a -> a.getId()).collect(Collectors.toList());
            List<EmcOrderTaskResultBean> realOrderTaskResultlist = copyOrderTaskResultList.stream().filter(a -> realorderTaskIdList.contains(a.getOrderTaskId())).collect(Collectors.toList());
            orderTaskResultService.getDao().insert(realOrderTaskResultlist);
            orderTaskService.getDao().update(orderTaskAllList, "CUPNO");
            orderItemGroupService.getDao().insert(newOrderItemGroupList);

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
            Map<Long, List<EmcOrderTaskBean>> mapFirst = allTaskList.stream().filter(a -> a.getParentId() != null && qcIdFirstList.contains(a.getQcCategoryId()))
                    .collect(Collectors.groupingBy(EmcOrderTaskBean::getParentId));

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
            Map<Long, List<EmcOrderTaskBean>> mapSecond = allTaskList.stream().filter(a -> a.getParentId() != null && qcIdSecondList.contains(a.getQcCategoryId()))
                    .collect(Collectors.groupingBy(EmcOrderTaskBean::getParentId));

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

            // 对有默认值的分析项触发一次公式计算
            if (!defaultValueResultIdList.isEmpty()) {
                EmcOrderTaskResultComputingEngine.getEngine(defaultValueResultIdList).compute();
            }
        }
        return copyOrderList.get(0).getId();
    }

    /**
     * 合成项目PDF总报表
     *
     * @param orderTaskList
     */
    protected void composeProjectPdfReport(List<EmcOrderTaskBean> orderTaskList) {
        // 通过系统参数COMPOSE_PDF来配置是否需要合成PDF
        CoreConstantBean composePdfConstant = coreConstantService.selectByIdIfPresent("COMPOSE_PDF");
        if (composePdfConstant == null || !Constant.YES.equals(composePdfConstant.getConstantValue())) {
            return;
        }

        List<EmcOrderTaskBean> validOrderTaskList = orderTaskList.stream().filter(task -> !StringUtils.isEmpty(task.getRunId())).collect(Collectors.toList());
        if (validOrderTaskList.isEmpty()) {
            return;
        }

        List<String> runIdList = validOrderTaskList.stream().map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());
        List<String> targetIdList = runIdList.stream().map(runId -> getDao().getTable() + "$" + runId).collect(Collectors.toList());
        List<CoreFileBean> coreFileBeanList = coreFileService
                .selectListByFilter(SearchFilter.instance().match("TARGETID", targetIdList).filter(MatchPattern.OR).match("EXT", "composePdf").filter(MatchPattern.EQ));

        Map<Long, List<FilePathDTO>> projectPdfMap = new HashMap<>(orderTaskList.size());
        Map<Long, List<EmcOrderTaskBean>> projectOrderTaskMap = validOrderTaskList.stream().filter(task -> !ObjectUtils.isEmpty(task.getProjId())).collect(Collectors.groupingBy(EmcOrderTaskBean::getProjId));
        projectOrderTaskMap.forEach((projectId, projectOrderTaskList) -> {
            List<FilePathDTO> pdfFileList = new ArrayList<>();
            List<String> projectRunIdList = projectOrderTaskList.stream().map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());

            List<String> projectFileTargetIdList = projectRunIdList.stream().map(s -> getDao().getTable() + "$" + s).collect(Collectors.toList());
            coreFileBeanList.stream().filter(file -> projectFileTargetIdList.contains(file.getTargetId())).forEach(file -> pdfFileList.add(CoreFileUtils.toFilePath(file)));

            if (!pdfFileList.isEmpty()) {
                projectPdfMap.put(projectId, pdfFileList);
            }
        });

        if (!projectPdfMap.isEmpty()) {
            // 删除之前合成的项目原始记录PDF
            List<Long> projectIdList = new ArrayList<>(projectPdfMap.keySet());
            List<CoreFileBean> deleteFileList = new ArrayList<>(projectIdList.size());
            projectIdList.forEach(id -> {
                CoreFileBean deleteFile = new CoreFileBean();
                deleteFile.setTargetId("T_EMC_PROJECT" + "$" + id);
                deleteFile.setBizCategory("originalLabRecords");
                deleteFileList.add(deleteFile);
            });
            coreFileService.getDao().deleteBy(deleteFileList, "TARGETID", "BIZCATEGORY");

            String projectReportPdfName = I18nHelper.getMessage("T_EMC_RUN.DOWNLOAD.PROJECT_REPORT_PDF_NAME");
            projectPdfMap.forEach((projectId, fileList) -> {
                FilePathDTO composedPdfFile = FilePathDTO.of(FileScope.temp.name(), "" + System.currentTimeMillis(), StringUtils.randomUUID(8), projectReportPdfName);
                coreFileService.mergePdfList(fileList, composedPdfFile);
                String tempFilePath = FilePathManager.getLocalPath(composedPdfFile).toString();
                if (!StringUtils.isEmpty(tempFilePath) && tempFilePath.contains(projectReportPdfName)) {
                    CoreFileBean coreFileBean = new CoreFileBean();
                    coreFileBean.setTargetId("T_EMC_PROJECT" + "$" + projectId);
                    coreFileBean.setScope(FileScope.secure.name());
                    coreFileBean.setBizCategory("originalLabRecords");
                    coreFileService.upload(coreFileBean, new File(tempFilePath));
                }
            });
        }
    }

    protected void insertAuditRecordByRun(List<EmcRunBean> runList, String bpmnStatus, String comment, List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList) {
        List<EmcAuditRecordBean> auditRecordList = new ArrayList<>();
        for (EmcRunBean run : runList) {
            List<EmcOrderTaskBean> orderTaskBeans = orderTaskService.selectListByFilter(SearchFilter.instance().match("RUNID", run.getId()).filter(MatchPattern.SEQ));
            List<Long> projIdList = orderTaskBeans.stream().map(EmcOrderTaskBean::getProjId).filter(Objects::nonNull).distinct().collect(Collectors.toList());

            for(Long projId : projIdList){
                EmcAuditRecordBean auditRecord = new EmcAuditRecordBean();
                auditRecord.setId(ApplicationContextHelper.getNextIdentity());
                auditRecord.setProjId(projId);
                auditRecord.setRunId(run.getId());
                auditRecord.setComments(comment);

                auditRecord.setLogDesc("批号:" + run.getId());
                auditRecord.setBizCategory("run");

                if ("PROCESS_START".contentEquals(bpmnStatus)) {
                    auditRecord.setStepCode("5");
                    auditRecord.setAuditCategory("结果录入");
                    auditRecord.setTaskName("分析录入");
                    auditRecord.setOperateCategory("提交");
                    auditRecord.setNextTaskName("分析校核");
                } else if ("PROCESS_COMPLETE".equals(bpmnStatus)) {
                    auditRecord.setStepCode("5");
                    auditRecord.setAuditCategory("结果录入");
                    auditRecord.setTaskName("分析校核");
                    auditRecord.setOperateCategory("审核");
                    auditRecord.setNextTaskName("分析审核");
                } else if ("PROCESS_UNDO".equals(bpmnStatus)) {
                    auditRecord.setStepCode("5");
                    auditRecord.setAuditCategory("结果录入");
                    auditRecord.setTaskName("分析审核");
                    auditRecord.setOperateCategory("退回");
                    auditRecord.setNextTaskName("分析录入");
                }

                auditRecordList.add(auditRecord);
            }
        }
        auditRecordService.getDao().insert(auditRecordList);
    }

    protected String getNextOrderNo(String on) {
        if (StringUtils.isEmpty(on) || on.length() < 3) {
            return "";
        }

        int num = NumberUtils.parseInt(on.substring(on.length() - 3)) + 1;
        return StringUtils.leftPad("" + num, 3);
    }

    /**
     * 测试批原始记录生成 PDF 报表文件
     *
     * @param runBeanList
     */
    public void composeRunReportPdf(List<EmcRunBean> runBeanList) {
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

        // 生成的PDF默认名称
        String defaultPdfFileName = I18nHelper.getMessage("T_EMC_RUN.DOWNLOAD.REPORT_PDF_NAME");

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

            Map<String, CoreFileBean> pdfReportMap = new HashMap<>();
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
                            coreFileBean.setTargetId("T_EMC_RUN" + "$" + r.getId());
                            coreFileBean.setScope(FileScope.secure.name());
                            coreFileBean.setExt("composePdf");
                            pdfReportMap.put(reportUrl.toString(), coreFileBean);
                        } catch (UnsupportedEncodingException e) {
                            throw new UnsupportedException();
                        }
                    }
                }
            });

            coreFileService.getDao().deleteBy(new ArrayList<>(pdfReportMap.values()), "TARGETID", "EXT");
            pdfReportMap.entrySet().stream().forEach(e -> {
                logger.info(e.getKey());
                File reportPdfFile = downloadFile(e.getKey(), defaultPdfFileName);
                if (reportPdfFile != null) {
                    coreFileService.upload(e.getValue(), reportPdfFile);
                }
            });
        }

        // 帆软报表模板
        List<CoreReportConfigLineBean> frConfigLineBeanList = configLineBeanList.stream().filter(r -> "fanruan".equals(r.getReportCategory())).collect(Collectors.toList());
        if (!frConfigLineBeanList.isEmpty()) {
            List<String> configReportLineIdList = frConfigLineBeanList.stream().map(r -> coreReportConfigLineService.getDao().getTable() + "$" + r.getId()).collect(Collectors.toList());
            List<CoreFileBean> coreFileBeanList = coreFileService.getDao().selectListByOneColumnValues(configReportLineIdList, "TARGETID");

            Map<String, CoreFileBean> pdfReportMap = new HashMap<>();
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
                        coreFileBean.setTargetId("T_EMC_RUN" + "$" + r.getId());
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

    /**
     * 根据map的key排序
     *
     * @param map
     *            待排序的map
     * @return 排序好的map
     */
    protected static Map<String, List<EmcOrderTaskBean>> sortByKey(Map<String, List<EmcOrderTaskBean>> map) {
        Map<String, List<EmcOrderTaskBean>> result = new LinkedHashMap<>();
        List<String> list = new ArrayList<>();

        map.forEach((k, v) -> list.add(k));
        CollectionUtils.sort(list, (o1, o2) -> o1.compareTo(o2));
        list.stream().forEach(a -> {
            result.put(a, map.get(a));
        });
        return result;
    }

    // 提取工作流方法
    public List<CoreBpmnInstanceStatusDTO<String>> startProcessBasic(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<String>> coreBpmnInstanceStatusDTOList = EmcRunService.super.startProcess(wrapper);
        return coreBpmnInstanceStatusDTOList;
    }

    // 提取工作流方法
    public List<CoreBpmnInstanceStatusDTO<String>> completeTaskBasic(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<String>> coreBpmnInstanceStatusDTOList = EmcRunService.super.completeTask(wrapper);
        return coreBpmnInstanceStatusDTOList;
    }

    // 提取工作流方法
    public List<CoreBpmnInstanceStatusDTO<String>> unDoBasic(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<String>> coreBpmnInstanceStatusDTOList = EmcRunService.super.undo(wrapper);
        return coreBpmnInstanceStatusDTOList;
    }

    // 提取工作流方法
    public List<CoreBpmnInstanceStatusDTO<String>> rejectTaskBasic(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<String>> coreBpmnInstanceStatusDTOList = EmcRunService.super.rejectTask(wrapper);
        return coreBpmnInstanceStatusDTOList;
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
	public Page<EmcRunBean> analysisPretrial(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return selectPagination(() -> getDao().selectAnalysisAuditRun(parameter), rowBounds);
	}

	@Override
	public void passPreApproval(RestJsonWrapperBean wrapper) {
		List<EmcRunBean> emcRunBean = wrapper.parse(EmcRunBean.class);
		emcRunBean.forEach(f ->{
			f.setCheckStatus("1");
		});
		getDao().updateIfChanged(emcRunBean);

	}

    @Override
    public Page<EmcRunBean> selectRunTodoList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return selectPagination(() -> getDao().selectRunTodoList(parameter), rowBounds);
    }

    @Override
    public Page<EmcRunBean> selectCheckRunTodoList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return selectPagination(() -> getDao().selectCheckRunTodoList(parameter), rowBounds);
    }

    @Override
    public Page<EmcRunBean> selectSubmitRunTodoList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return selectPagination(() -> getDao().selectSubmitRunTodoList(parameter), rowBounds);
    }
}
