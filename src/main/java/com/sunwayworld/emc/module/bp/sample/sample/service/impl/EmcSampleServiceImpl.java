package com.sunwayworld.emc.module.bp.sample.sample.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
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
import com.sunwayworld.emc.module.bp.order.orderretain.bean.EmcOrderRetainBean;
import com.sunwayworld.emc.module.bp.order.orderretain.service.EmcOrderRetainService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportService;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.result.run.service.EmcRunService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.dao.EmcSampleDao;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.emc.module.bp.sample.sample.validator.EmcSampleSubmitValidator;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyDataService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyFieldService;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.service.EmcSubcontractRecordService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorTmplService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnParameterDTO;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnCommentBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import com.sunwayworld.module.sys.bpmn.engine.CoreBpmnRuntimeSource;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnCommentService;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

@Repository
@GikamBean
public class EmcSampleServiceImpl implements EmcSampleService {
    @Autowired
    private EmcSampleDao sampleDao;

    @Autowired
    @Lazy
    private EmcProjectService projectService;

    @Autowired
    @Lazy
    private EmcOrderService orderService;

    @Autowired
    @Lazy
    private EmcOrderTaskService orderTaskService;

    @Autowired
    @Lazy
    private EmcReportService reportService;

    @Autowired
    @Lazy
    private EmcFolderService folderService;

    @Autowired
    private EmcItemService itemService;

    @Autowired
    @Lazy
    private EmcOrderRetainService orderRetainService;

    @Autowired
    @Lazy
    private EmcOrderItemGroupService orderItemGroupService;

    @Autowired
    @Lazy
    private EmcGroupItemService groupItemService;

    @Autowired
    private EmcRunService runService;

    @Autowired
    @Lazy
    private EmcOrderTaskResultService orderTaskResultService;

    @Autowired
    private EmcSubcontractRecordService subcontractRecordService;

    @Autowired
    private EmcAuditRecordService auditRecordService;

    @Autowired
    @Lazy
    private EmcPropertyFieldService propertyFieldService;

    @Autowired
    @Lazy
    private EmcPropertyDataService propertyDataService;

    @Autowired
    @Lazy
    private EmcMonitorTmplService monitorTmplService;

    @Autowired
    private EmcOrderTaskLogService orderTaskLogService;

    @Autowired
    private CoreBpmnCommentService coreBpmnCommentService;

    @Autowired
    private EmcTestMethodService testMethodService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcSampleDao getDao() {
        return sampleDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcSampleBean sample = jsonWrapper.parseUnique(EmcSampleBean.class);
        sample.setId(ApplicationContextHelper.getNextIdentity());
        sample.setSampleNo(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        CoreUserBean user = LocalContextHelper.getLoginUser();
        //赋值所属单位和流程类型
        String parentOrgId = user.getExt$Item("parentorgid");
        String parentOrgName = user.getExt$Item("parentorgname");
        sample.setOrgId(parentOrgId);
        sample.setOrgName(parentOrgName);

        getDao().insert(sample);

        // 添加采样单时，插入数据到 t_emc_property_data 表，要判断有没有插入过
        List<EmcPropertyDataBean> propertyDataList = propertyDataService.selectListByFilter(SearchFilter.instance().match("BIZID", sample.getId()).filter(MatchPattern.SEQ), jsonWrapper);
        if (propertyDataList.isEmpty()) {
            EmcPropertyDataBean propertyData = new EmcPropertyDataBean();
            propertyData.setBizId(sample.getId());
            propertyData.setId(ApplicationContextHelper.getNextIdentity());
            propertyData.setTableName(getDao().getTable());

            propertyDataService.getDao().insert(propertyData);
        }
        return sample.getId();
    }

    /**
     * 采样超期代办
     */
    @Override
    public Page<EmcSampleBean> selectSampleOverduePagination(RestJsonWrapperBean wrapper) {
        return selectPagination(wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void accept(RestJsonWrapperBean jsonWrapper) {
        List<EmcSampleBean> sampleList = jsonWrapper.parse(EmcSampleBean.class);
        List<Long> sampleIdList = sampleList.stream().map(EmcSampleBean::getId).collect(Collectors.toList());
        List<EmcSampleBean> submitSampleList = getDao().selectListByOneColumnValues(sampleIdList, "ID", Arrays.asList("ID", "SOURCE", "SEPSAMPLETIME"));

        // 样品接受人和接收时间
        String acceptedUserId = LocalContextHelper.getLoginUserId();
        String acceptedUserName = LocalContextHelper.getLoginUser().getUserName();
        LocalDateTime acceptedDate = LocalDateTime.now();

        submitSampleList.stream().filter(sample -> ObjectUtils.isEmpty(sample.getAcceptedDate())).forEach(sample -> {
            sample.setAcceptedUserId(acceptedUserId);
            sample.setAccepteUserName(acceptedUserName);
            sample.setAcceptedDate(acceptedDate);
        });

        getDao().update(submitSampleList, "ACCEPTEDUSERID", "ACCEPTEUSERNAME", "ACCEPTEDDATE");

        // 过滤出样品登记生成的采样单
        List<EmcSampleBean> orderRegistrationSampleList = submitSampleList.stream().filter(s -> StringUtils.equals(EmcBizConstant.BP_SOURCE_ORDERREGISTRATION, s.getSource())).collect(Collectors.toList());
        if (!orderRegistrationSampleList.isEmpty()) {
            orderRegistrationSampleList.forEach(s -> {
                // 更改采样单状态
                s.setStatus(EmcBizConstant.SAMPLE_PROCESSSTATUS_DONE);
                // 更改采样单工作流状态
                s.setProcessStatus(EmcBizConstant.SAMPLE_PROCESSSTATUS_DONE);
            });

            this.getDao().update(orderRegistrationSampleList, "STATUS", "PROCESSSTATUS");
        }

        // 样品接收提交之后将分析类型为分析的样品测试的状态置为logged
        SearchFilter filter = SearchFilter.instance().match("SAMPLEID", sampleIdList).filter(MatchPattern.OR);
        filter.match("TERMINATION", Constant.YES).filter(MatchPattern.DIFFER);
        List<EmcOrderBean> orderList = orderService.selectListByFilter(filter);

        if (!orderList.isEmpty()) {
            List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).distinct().collect(Collectors.toList());
            // 样品表更新接收人和接收时间
            EmcOrderBean order = new EmcOrderBean();
            order.setAcceptedUserId(acceptedUserId);
            order.setAccepteUserName(acceptedUserName);
            order.setAcceptedDate(acceptedDate);
            orderService.getDao().updateByIds(order, orderIdList, "ACCEPTEDUSERID", "ACCEPTEUSERNAME", "ACCEPTEDDATE");
            List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByFilter(
                    SearchFilter.instance().match("ORDERID", orderIdList).filter(MatchPattern.OR));
            List<EmcOrderTaskBean> analysisOrderTaskList = orderTaskList.stream().filter(a -> EmcBizConstant.ANALYSISCATEGORY_ANALYSIS.equals(a.getAnalysisType())).collect(Collectors.toList());

            // 插入留样管理
            addOrderRetain(orderList, orderTaskList);
            // 更新数据
            if (!analysisOrderTaskList.isEmpty()) {

                EmcOrderTaskBean updateOrderTask = new EmcOrderTaskBean();

                updateOrderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED);

                orderTaskService.getDao().updateByIds(updateOrderTask, analysisOrderTaskList.stream().map(e -> e.getId()).collect(Collectors.toList()), "STATUS");
            }

            // 将分包的样品测试的状态置为done
            List<EmcOrderTaskBean> subcontractorOrderTaskList = orderTaskList.stream().filter(e -> !ObjectUtils.isEmpty(e.getSubcontractorId())).collect(Collectors.toList());

            if (!subcontractorOrderTaskList.isEmpty()) {

                EmcOrderTaskBean updateOrderTask = new EmcOrderTaskBean();

                updateOrderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_DONE);

                List<Long> subOrderTaskIdList = subcontractorOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
                orderTaskService.getDao().updateByIds(updateOrderTask, subOrderTaskIdList, "STATUS");

                // 只保留分包测试的在报告中显示的分析项，并且去掉计算公式，其他分析项删掉
                List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao()
                        .selectListByOneColumnValues(subOrderTaskIdList, "ORDERTASKID");
                List<Long> deleteOrderTaskResultIdList = orderTaskResultList.stream().filter(e -> ObjectUtils.isEmpty(e.getReportDisplay()) || ObjectUtils.equals(Constant.NO, e.getReportDisplay()))
                        .map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
                orderTaskResultService.getDao().deleteByIdList(deleteOrderTaskResultIdList);

                List<Long> updateOrderTaskResultIdList = orderTaskResultList.stream().filter(e -> ObjectUtils.equals(Constant.YES, e.getReportDisplay())).map(e -> e.getId())
                        .collect(Collectors.toList());
                EmcOrderTaskResultBean updateOrderTaskResult = new EmcOrderTaskResultBean();
                updateOrderTaskResult.setScriptFlag("0");
                orderTaskResultService.getDao().updateByIds(updateOrderTaskResult, updateOrderTaskResultIdList, "SCRIPT");

                // 分包的数据在分包数据录入能录入
                subcontractRecordService.insert(subcontractorOrderTaskList);
            }

            // 数据共享的数据不走采样和分析
            List<EmcOrderTaskBean> dataSharingOrderTaskList = orderTaskList.stream()
                    .filter(e -> ObjectUtils.equals(EmcBizConstant.ANALYSISCATEGORY_ANALYSIS, e.getAnalysisType()) && ObjectUtils.equals(Constant.YES, e.getDataSharing()))
                    .collect(Collectors.toList());

            if (!dataSharingOrderTaskList.isEmpty()) {

                List<EmcOrderTaskBean> updateOrderTaskList = new ArrayList<>();

                dataSharingOrderTaskList.forEach(e -> {

                    EmcOrderTaskBean updateOrderTask = new EmcOrderTaskBean();
                    updateOrderTask.setId(e.getId());
                    updateOrderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_DONE);
                    updateOrderTaskList.add(updateOrderTask);
                });

                orderTaskService.getDao().update(updateOrderTaskList, "STATUS");

                // 只保留分包测试的在报告中显示的分析项，并且去掉计算公式，其他分析项删掉
                List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao()
                        .selectListByOneColumnValues(dataSharingOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList()), "ORDERTASKID");
                List<Long> deleteOrderTaskResultIdList = orderTaskResultList.stream().filter(e -> ObjectUtils.isEmpty(e.getReportDisplay()) || ObjectUtils.equals(Constant.NO, e.getReportDisplay()))
                        .map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
                orderTaskResultService.getDao().deleteByIdList(deleteOrderTaskResultIdList);

                List<Long> updateOrderTaskResultIdList = orderTaskResultList.stream().filter(e -> ObjectUtils.equals(Constant.YES, e.getReportDisplay())).map(EmcOrderTaskResultBean::getId)
                        .collect(Collectors.toList());
                EmcOrderTaskResultBean updateOrderTaskResult = new EmcOrderTaskResultBean();
                updateOrderTaskResult.setScriptFlag("0");
                orderTaskResultService.getDao().updateByIds(updateOrderTaskResult, updateOrderTaskResultIdList, "SCRIPT");

            }

            // 判断更新的样品测试对应的项目样品类型下的样品测试是不是都已经完成，完成的话将报告状态置为preLogged
            updateReportStatus(sampleIdList, orderList, orderTaskList);

            // 插入样品测试操作记录表
            List<EmcOrderTaskBean> allOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList()), "ORDERID");
            orderTaskLogService.insert(allOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_SAMPLE_ACCEPT, "");

            // 更新项目节点
            List<Long> projIdList = orderList.stream().map(EmcOrderBean::getProjId).distinct().collect(Collectors.toList());
            ProjectNodeUtils.updateNodes(projIdList);
        }
        // 样品接收提交插入审核记录
        insertAuditRecordBySample(sampleIdList, "PROCESS_ACCEPT", "接收", null);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertByProject(List<Long> projectIdList, String status) {
        List<EmcProjectBean> projectList = projectService.getDao().selectListByIds(projectIdList);
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(projectIdList, "PROJID");

        // 根据monitorTmplId和projectId去重
        List<EmcOrderBean> distinctOrderList = orderList.stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getMonitorTmplId() + ";" + o.getProjId()))), ArrayList::new));
        int qty = distinctOrderList.size();
        List<String> sampleNoList = ApplicationContextHelper.getNextSequenceList(getDao().getTable(), qty);

        List<EmcSampleBean> insertSampleList = new ArrayList<>();
        for (int i = 0; i < qty; i++) {
            EmcSampleBean sample = new EmcSampleBean();

            EmcOrderBean order = distinctOrderList.get(i);
            Long projectId = order.getProjId();
            String monitorTmplId = order.getMonitorTmplId();
            String orderCategory = order.getOrderCategory();

            Long id = ApplicationContextHelper.getNextIdentity();
            sample.setId(id);
            sample.setSampleNo(sampleNoList.get(i));
            sample.setProjId(projectId);

            EmcProjectBean project = projectList.stream().filter(f -> ObjectUtils.equals(f.getId(), projectId)).findFirst().orElse(null);
            if (project != null) {
                sample.setProjNo(project.getProjNo());
            }
            if (!StringUtils.isEmpty(status)) {
                sample.setStatus(status);
            }
            sample.setMonitorTmplId(monitorTmplId);
            sample.setOrderCategory(orderCategory);
            sample.setSource(EmcBizConstant.BP_SOURCE_ORDERREGISTRATION);
            sample.setProcessStatus(ProcessStatus.DRAFT.name().toLowerCase());
            sample.setOrgId(LocalContextHelper.getLoginUser().getExt$Item("parentorgid"));
            sample.setOrgName(LocalContextHelper.getLoginUser().getExt$Item("parentorgname"));

            insertSampleList.add(sample);

            orderList.stream().filter(e -> ObjectUtils.equals(e.getProjId(), projectId) && ObjectUtils.equals(e.getMonitorTmplId(), monitorTmplId)).forEach(e -> {
                e.setSampleId(id);
            });
        }
        if (!orderList.isEmpty()) {
            orderService.getDao().update(orderList, "SAMPLEID");
        }

        getDao().insert(insertSampleList);
    }

    @Override
    public void insertByProject(List<Long> projectIdList) {
        this.insertByProject(projectIdList, null);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void addOrder(Long id, RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);

        EmcOrderBean addOrder = orderList.get(0);

        EmcSampleBean sampleBean = new EmcSampleBean();
        sampleBean.setId(id);
        sampleBean.setOrderCategory(addOrder.getOrderCategory());
        sampleBean.setMonitorTmplId(addOrder.getMonitorTmplId());
        getDao().update(sampleBean);

        // 根据(monitorTmplid) id 查询 tmplid
        EmcMonitorTmplBean monitorTmplBean = monitorTmplService.selectByIdIfPresent(addOrder.getMonitorTmplId());
        if(monitorTmplBean == null) {
            throw new UnexpectedException(I18nHelper.getMessage("EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.NOT_EXISTS_TMPL", addOrder.getMonitorTmplId()));
        }
        EmcPropertyDataBean propertyDataBean = new EmcPropertyDataBean();
        propertyDataBean.setBizId(id);
        List<EmcPropertyDataBean> dataList = propertyDataService.selectListByFilter(SearchFilter.instance().match("BIZID", id).filter(MatchPattern.SEQ), wrapper);
        if (dataList.size() > 0) {
            propertyDataBean.setTmplId(monitorTmplBean.getPropertyTmplId());
            propertyDataBean.setId(dataList.get(0).getId());
            propertyDataService.update(propertyDataBean);
        }

        // 根据样品类型查询监测模板表的默认采样单模板id，更新采样单表的默认采样单模板id
        EmcSampleBean reportConfigSampleBean = getDao().selectById(id);
        Long reportConfigLineId = monitorTmplBean.getReportConfigLineId();
        if (StringUtils.isEmpty(reportConfigSampleBean.getReportConfigLineId()) && !ObjectUtils.isEmpty(reportConfigLineId)) {
            reportConfigSampleBean.setReportConfigLineId(reportConfigLineId.toString());
            getDao().update(reportConfigSampleBean, "reportConfigLineId");
        }

        // 更新采样单表的默认交接单模板ID
        String receiveReportLineId = monitorTmplBean.getReceiveReportLineId();
        if (StringUtils.isEmpty(reportConfigSampleBean.getReceiveReportLineId()) && !ObjectUtils.isEmpty(receiveReportLineId)) {
            reportConfigSampleBean.setReceiveReportLineId(receiveReportLineId);
            getDao().update(reportConfigSampleBean, "receiveReportLineId");
        }

        EmcOrderBean updateOrder = new EmcOrderBean();
        updateOrder.setSampleId(id);

        orderService.getDao().updateByIds(updateOrder, orderList.stream().map(e -> e.getId()).collect(Collectors.toList()), "SAMPLEID");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateOrderSampleDateTime(Long id, RestJsonWrapperBean wrapper) {
        orderService.updateOrder(wrapper, "SAMPLEBEGINDATE", "SAMPLEBEGINTIME", "SAMPLECOMPLETEDATE", "SAMPLECOMPLETETIME");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void deleteOrder(RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);

        EmcOrderBean updateOrder = new EmcOrderBean();
        updateOrder.setSampleId(null);
        orderService.getDao().updateByIds(updateOrder, orderList.stream().map(e -> e.getId()).collect(Collectors.toList()), "SAMPLEID");

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void delete(RestJsonWrapperBean wrapper) {

        EmcSampleService.super.delete(wrapper);

        List<EmcSampleBean> sampleList = wrapper.parse(EmcSampleBean.class);

        List<Long> sampleIdList = sampleList.stream().map(e -> e.getId()).collect(Collectors.toList());

        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(sampleIdList, "SAMPLEID");

        EmcOrderBean orderBean = new EmcOrderBean();
        orderBean.setSampleId(null);

        orderService.getDao().updateByIds(orderBean, orderList.stream().map(e -> e.getId()).collect(Collectors.toList()), "SAMPLEID");

        // 删除采样单后,删除t_emc_property_data
        List <EmcPropertyDataBean> pdList = new ArrayList<>();
        sampleIdList.stream().forEach(a -> {
            EmcPropertyDataBean pd = new EmcPropertyDataBean();
            pd.setBizId(a);
            pdList.add(pd);
        });
        propertyDataService.getDao().deleteBy(pdList, "BIZID");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_START)
    @ValidateDataWith(EmcSampleSubmitValidator.class)
    public List<CoreBpmnInstanceStatusDTO<Long>> startProcess(RestJsonWrapperBean wrapper) {
        // 现场采样或者采样补录提交
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = startProcessBasic(wrapper);
        List<Long> sampleIdList = coreBpmnInstanceStatusDTOList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcSampleBean> sampleList = getDao().selectListByIds(sampleIdList);
        List<Long> projIdList = sampleList.stream().map(a -> a.getProjId()).distinct().collect(Collectors.toList());

        // 采样单的状态改成approve
        List<EmcSampleBean> updateSampleList = new ArrayList<EmcSampleBean>();
        sampleList.forEach(e -> {
            EmcSampleBean updateSample = new EmcSampleBean();
            updateSample.setId(e.getId());
            updateSample.setStatus(EmcBizConstant.SAMPLE_STATUS_APPROVE);
            updateSampleList.add(updateSample);
        });
        getDao().update(updateSampleList, "STATUS");

        // 把类型为采样的ordtask状态改成approve（提交到采样互审）
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(sampleIdList, "SAMPLEID");
        if (!orderList.isEmpty()) {

            List<Long> orderIdList = orderList.stream().map(e -> e.getId()).collect(Collectors.toList());
            List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("ORDERID", orderIdList).filter(MatchPattern.OR));
            if (!orderTaskList.isEmpty()) {
                List<EmcOrderTaskBean> sampleOrderTaskList = orderTaskList.stream().filter(e -> ObjectUtils.equals(EmcBizConstant.ANALYSISCATEGORY_SAMPLING, e.getAnalysisType()))
                        .collect(Collectors.toList());
                List<EmcOrderTaskBean> updateOrderTaskList = new ArrayList<EmcOrderTaskBean>();
                if (!sampleOrderTaskList.isEmpty()) {
                    sampleOrderTaskList.forEach(e -> {
                        EmcOrderTaskBean updateOrderTask = new EmcOrderTaskBean();
                        updateOrderTask.setId(e.getId());
                        updateOrderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_APPROVE);
                        EmcOrderBean order = orderList.stream().filter(o -> ObjectUtils.equals(o.getId(), e.getOrderId())).findFirst().orElse(null);
                        updateOrderTask.setAnalysisDate(order.getSampleBeginDate());
                        updateOrderTask.setConfirmUserName(LocalContextHelper.getLoginUser().getUserName());
                        updateOrderTaskList.add(updateOrderTask);
                    });
                    orderTaskService.getDao().update(updateOrderTaskList, "ANALYSISDATE", "STATUS", "CONFIRMUSERNAME");

                    // 将样品测试结果表的记录日志标志置为"1"
                    List<Long> orderTaskIdList = sampleOrderTaskList.stream().map(e -> e.getId()).collect(Collectors.toList());
                    List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "orderTaskId");
                    orderTaskResultList.forEach(a -> {
                        a.setSubmitFlag(Constant.YES);
                    });
                    orderTaskResultService.getDao().update(orderTaskResultList, "SUBMITFLAG");

                    // 插入样品测试操作记录表
                    orderTaskLogService.insert(sampleOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_RESULT_INPUT, EmcBizConstant.ORDER_TASK_LOG_SAMPLING);

                }

                // 已终止样品结束流程
                this.executeTermination(sampleIdList, orderList);
            }
        }

        // 现场采样提交插入审核记录
        this.insertAuditRecordBySample(sampleIdList, "PROCESS_START", wrapper.getParamValue("bpmn_comment"), null);
        ProjectNodeUtils.updateNodes(projIdList);
        return coreBpmnInstanceStatusDTOList;
    }

    // 提取工作流方法
    public List<CoreBpmnInstanceStatusDTO<Long>> startProcessBasic(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcSampleService.super.startProcess(wrapper);
        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_COMPLETE)
    public List<CoreBpmnInstanceStatusDTO<Long>> completeTask(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = completeTaskBasic(wrapper);
        List<Long> sampleIdList = coreBpmnInstanceStatusDTOList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcSampleBean> sampleList = getDao().selectListByIds(sampleIdList);
        List<Long> projIdList = sampleList.stream().map(a -> a.getProjId()).distinct().collect(Collectors.toList());
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(sampleIdList, "SAMPLEID");

        List<EmcSampleBean> updateSampleList = new ArrayList<EmcSampleBean>();
        List<EmcOrderTaskBean> updateOrderTaskList = new ArrayList<EmcOrderTaskBean>();

        // 现场数据校核：把采样单状态改成audit，同时把采样单下类型为采样的ordtask状态改成audit
        // 现场数据审核： 把采样单状态改成done，同时把采样单下类型为采样的ordtask状态改成done
        String taskStatus = coreBpmnInstanceStatusDTOList.get(0).getStatusCode();
        if (ObjectUtils.equals(EmcBizConstant.SAMPLE_STATUS_MUTUAL, taskStatus)) {
            // 采样单的状态改成audit
            sampleList.forEach(e -> {
                EmcSampleBean updateSample = new EmcSampleBean();
                updateSample.setId(e.getId());
                updateSample.setStatus(EmcBizConstant.SAMPLE_STATUS_AUDIT);
                updateSampleList.add(updateSample);
            });
            getDao().update(updateSampleList, "STATUS");

            // 把类型为采样的ordtask状态改成audit（互审到审核），对应采样单的状态改成audit
            if (!orderList.isEmpty()) {
                List<Long> orderIdList = orderList.stream().map(e -> e.getId()).collect(Collectors.toList());
                List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("ORDERID", orderIdList).filter(MatchPattern.OR));
                if (!orderTaskList.isEmpty()) {
                    List<EmcOrderTaskBean> sampleOrderTaskList = orderTaskList.stream().filter(e -> ObjectUtils.equals(EmcBizConstant.ANALYSISCATEGORY_SAMPLING, e.getAnalysisType()))
                            .collect(Collectors.toList());
                    if (!sampleOrderTaskList.isEmpty()) {
                        sampleOrderTaskList.forEach(e -> {
                            EmcOrderTaskBean updateOrderTask = new EmcOrderTaskBean();
                            updateOrderTask.setId(e.getId());
                            updateOrderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_AUDIT);
                            updateOrderTaskList.add(updateOrderTask);
                        });
                        orderTaskService.getDao().update(updateOrderTaskList, "STATUS");

                        // 插入样品测试操作记录表
                        orderTaskLogService.insert(sampleOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_GROUP_AUDIT, EmcBizConstant.ORDER_TASK_LOG_SAMPLIE_MUTUAL_AUDIT);
                    }
                }
            }
        } else if (ObjectUtils.equals(EmcBizConstant.SAMPLE_STATUS_AUDIT, taskStatus)) {
            // 采样单的状态改成done
            sampleList.forEach(e -> {
                EmcSampleBean updateSample = new EmcSampleBean();
                updateSample.setId(e.getId());
                updateSample.setStatus(EmcBizConstant.SAMPLE_STATUS_DONE);
                updateSampleList.add(updateSample);
            });
            getDao().update(updateSampleList, "STATUS");

            // 把类型为采样的ordtask状态改成done（审核结束），对应采样单的状态改成done
            if (!orderList.isEmpty()) {
                List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
                List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("ORDERID", orderIdList).filter(MatchPattern.OR));
                if (!orderTaskList.isEmpty()) {
                    List<EmcOrderTaskBean> sampleOrderTaskList = orderTaskList.stream().filter(e -> ObjectUtils.equals(EmcBizConstant.ANALYSISCATEGORY_SAMPLING, e.getAnalysisType()))
                            .collect(Collectors.toList());
                    if (!sampleOrderTaskList.isEmpty()) {
                        sampleOrderTaskList.forEach(e -> {
                            EmcOrderTaskBean updateOrderTask = new EmcOrderTaskBean();
                            updateOrderTask.setId(e.getId());
                            updateOrderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_DONE);
                            updateOrderTaskList.add(updateOrderTask);
                        });
                        orderTaskService.getDao().update(updateOrderTaskList, "STATUS");

                        // 插入样品测试操作记录表
                        orderTaskLogService.insert(sampleOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_LEADER_AUDIT, EmcBizConstant.ORDER_TASK_LOG_SAMPLIE_AUDIT);
                    }
                    // 更新点位状态
                    folderService.updateStatus(orderTaskList);
                    // 判断对应的项目样品类型下的样品测试是不是都已经完成，完成的话将报告状态置为待录入
                    reportService.updateReportStatus(orderTaskList);
                    // 判断是否修改项目表状态
                    updateProject(projIdList);
                }
            }
        }

        // 采样管理审核插入审核记录
        insertAuditRecordBySample(sampleIdList, "PROCESS_COMPLETE", wrapper.getParamValue("bpmn_comment"), coreBpmnInstanceStatusDTOList);
        ProjectNodeUtils.updateNodes(projIdList);
        return coreBpmnInstanceStatusDTOList;
    }

    // 提取工作流方法
    public List<CoreBpmnInstanceStatusDTO<Long>> completeTaskBasic(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcSampleService.super.completeTask(wrapper);
        return coreBpmnInstanceStatusDTOList;
    }

    // 提取工作流方法
    public List<CoreBpmnInstanceStatusDTO<Long>> undoBasic(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcSampleService.super.undo(wrapper);
        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_REJECT)
    public List<CoreBpmnInstanceStatusDTO<Long>> rejectTask(RestJsonWrapperBean wrapper) {
        // 采样互审、采样审核拒绝
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = rejectTaskBasic(wrapper);
        List<Long> sampleIdList = coreBpmnInstanceStatusDTOList.stream().map(e -> e.getId()).collect(Collectors.toList());

        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(sampleIdList, "SAMPLEID");
        List<EmcOrderTaskBean> allOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderList.stream().map(e -> e.getId()).collect(Collectors.toList()), "ORDERID");

        ProcessStatus processStatus = coreBpmnInstanceStatusDTOList.get(0).getProcessStatus();
        List<EmcSampleBean> sampleList = getDao().selectListByIds(sampleIdList);
        List<Long> projIdList = sampleList.stream().map(a -> a.getProjId()).distinct().collect(Collectors.toList());
        List<EmcSampleBean> updateSampleList = sampleList.stream().filter(a -> !a.getStatus().equals(EmcBizConstant.SAMPLE_STATUS_REPORTUNDO)).collect(Collectors.toList());
        // 拒绝到结果录入
        if (ObjectUtils.equals(processStatus, ProcessStatus.DRAFT_REJECTED)) {
            updateSampleList.stream().forEach(a -> a.setStatus(EmcBizConstant.SAMPLE_STATUS_LOGGED));
            getDao().update(updateSampleList, "STATUS");

            List<EmcOrderTaskBean> sampleOrderTaskList = allOrderTaskList.stream().filter(e -> EmcBizConstant.ANALYSISCATEGORY_SAMPLING.equals(e.getAnalysisType())).collect(Collectors.toList());
            sampleOrderTaskList.stream().forEach(e -> e.setStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED));
            orderTaskService.getDao().update(sampleOrderTaskList, "STATUS");
        }

        // 拒绝到采样互审
        if (ObjectUtils.equals(processStatus, ProcessStatus.APPROVE_REJECTED)) {

            updateSampleList.stream().forEach(a -> a.setStatus(EmcBizConstant.SAMPLE_STATUS_APPROVE));
            getDao().update(updateSampleList, "STATUS");

            List<EmcOrderTaskBean> sampleOrderTaskList = allOrderTaskList.stream().filter(e -> EmcBizConstant.ANALYSISCATEGORY_SAMPLING.equals(e.getAnalysisType())).collect(Collectors.toList());
            sampleOrderTaskList.stream().forEach(e -> e.setStatus(EmcBizConstant.ORDERTASK_STATUS_APPROVE));
            orderTaskService.getDao().update(sampleOrderTaskList, "STATUS");
        }

        if (ObjectUtils.equals(EmcBizConstant.SAMPLE_STATUS_AUDIT, coreBpmnInstanceStatusDTOList.get(0).getStatusCode())) {
            // 插入样品测试操作记录表
            orderTaskLogService.insert(allOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_LEADER_AUDIT_UNDO, EmcBizConstant.ORDER_TASK_LOG_SAMPLIE_AUDIT);
        }
        if (ObjectUtils.equals(EmcBizConstant.SAMPLE_STATUS_MUTUAL, coreBpmnInstanceStatusDTOList.get(0).getStatusCode())) {
            // 插入样品测试操作记录表
            orderTaskLogService.insert(allOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_GROUP_AUDIT_UNDO, EmcBizConstant.ORDER_TASK_LOG_SAMPLIE_MUTUAL_AUDIT);
        }
        ProjectNodeUtils.updateNodes(projIdList);
        return coreBpmnInstanceStatusDTOList;
    }

    // 提取工作流方法
    public List<CoreBpmnInstanceStatusDTO<Long>> rejectTaskBasic(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcSampleService.super.rejectTask(wrapper);
        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    public void changeResult(RestJsonWrapperBean wrapper) {
        EmcOrderTaskResultBean orderTaskResult = wrapper.parseUnique(EmcOrderTaskResultBean.class);

        String originalResult = orderTaskResult.getOriginalResult();
        String revisionRule = orderTaskResult.getRevisionRule();

        orderTaskResult.setReportResult(originalResult);

        if (!StringUtils.isEmpty(revisionRule) && !StringUtils.isEmpty(originalResult)) {
            orderTaskResult.setReportResult(NumberUtils.rounding(originalResult, revisionRule));
            orderTaskResult.setResultStatus(StringUtils.isEmpty(orderTaskResult.getOriginalResult()) ? null : "Done");
        }
        orderTaskResultService.getDao().update(orderTaskResult, "ORIGINALRESULT", "REPORTRESULT", "RESULTSTATUS");
    }

    @Override
    @Transactional
    public void mergeSample(RestJsonWrapperBean wrapper) {
        List<EmcSampleBean> sampleList = wrapper.parse(EmcSampleBean.class);
        if (sampleList.size() <= 1) { // 仅有一条数据无需更新
            return;
        }
        // 以第一条数据为依据，更新其他的数据
        Long sampleId = sampleList.get(0).getId();
        List<Long> sampleIdList = sampleList.stream().map(e -> e.getId()).filter(e -> !e.equals(sampleId)).collect(Collectors.toList());
        // 根据 sampleId 查询全部 样品数据
        List<Long> orderIdList = orderService.getDao().selectListByOneColumnValues(sampleIdList, "SAMPLEID").stream().map(e -> e.getId()).collect(Collectors.toList());

        EmcOrderBean orderBean = new EmcOrderBean();
        orderBean.setSampleId(sampleId);
        orderService.getDao().updateByIds(orderBean, orderIdList, "SAMPLEID");

        getDao().deleteByIdList(sampleIdList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_UNDO)
    public List<CoreBpmnInstanceStatusDTO<Long>> undo(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> instanceStatusList = EmcSampleService.super.undo(wrapper);
        List<Long> idList = instanceStatusList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcSampleBean> sampleList = getDao().selectListByIds(idList);

        // 采样退回插入审核记录(先生成日志再删采样单)
        insertAuditRecordBySample(idList, "PROCESS_UNDO", wrapper.getParamValue("bpmn_comment"), null);

        // 样品登记
        List<EmcSampleBean> filterSampleList = sampleList.stream().filter(f -> EmcBizConstant.BP_SOURCE_ORDERREGISTRATION.equals(f.getSource())).collect(Collectors.toList());
        if (!filterSampleList.isEmpty()) {
            List<Long> projectIdList = filterSampleList.stream().map(r -> r.getProjId()).distinct().collect(Collectors.toList());

            // 回退项目
            CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
            parameter.setComment(wrapper.getParamValue("bpmn_comment"));
            projectService.undo(projectIdList, parameter);

            // 将项目节点状态置为样品登记
            EmcProjectBean updateProject = new EmcProjectBean();
            updateProject.setStatus(EmcBizConstant.PROJECT_STATUS_CREATE);
            projectService.getDao().updateByIds(updateProject, projectIdList, "STATUS");

            // 删除采样表
            List<EmcSampleBean> deleteSampleList = getDao().selectListByOneColumnValues(projectIdList, "PROJID");
            getDao().deleteBy(deleteSampleList);

            // 删除业务表：样品测试、样品结果
            List<EmcOrderTaskBean> deleteTaskList = orderTaskService.getDao().selectListByOneColumnValues(projectIdList, "PROJID");
            List<Long> folderIdList = deleteTaskList.stream().map(e -> e.getFolderId()).distinct().collect(Collectors.toList());
            List<EmcFolderBean> folderList = new ArrayList<>();
            folderIdList.forEach(e -> {
                EmcFolderBean folder = new EmcFolderBean();
                folder.setId(e);
                folder.setStatus(EmcBizConstant.FOLDER_STATUS_DRAFT);
                folder.setProcessStatus(EmcBizConstant.FOLDER_PROCESSSTATUS_DRAFT);
                folderList.add(folder);
            });
            folderService.getDao().update(folderList, "STATUS", "PROCESSSTATUS");
            List<EmcOrderTaskResultBean> deleteResultList = orderTaskResultService.getDao().selectListByOneColumnValues(projectIdList, "PROJID");
            orderTaskService.getDao().deleteBy(deleteTaskList);
            orderTaskResultService.getDao().deleteBy(deleteResultList);

            // 删除report表
            List<EmcReportBean> deleteReportList = reportService.getDao().selectListByOneColumnValues(projectIdList, "PROJID");
            reportService.getDao().deleteBy(deleteReportList);

            // 手动在审核记录添加样品接收审核节点
            List<CoreBpmnCommentBean> updateBpmnCommentList = new ArrayList<>();
            projectIdList.forEach(id -> {
                CoreBpmnCommentBean updateBpmnComment = new CoreBpmnCommentBean();
                updateBpmnComment.setTargetId(projectService.getDao().getTable() + "$" + id);
                updateBpmnComment.setStatus("UNDO");
                updateBpmnComment.setTaskName(EmcBizConstant.ORDER_TASK_LOG_SAMPLE_ACCEPT);
                updateBpmnCommentList.add(updateBpmnComment);
            });
            coreBpmnCommentService.getDao().update(updateBpmnCommentList, ArrayUtils.asList("taskName"), "targetId", "status");
            ProjectNodeUtils.updateNodes(projectIdList);
        } else {
            // 非样品登记的修改样品测试表状态
            List<Long> projectIdList = sampleList.stream().map(a -> a.getProjId()).distinct().collect(Collectors.toList());
            List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(idList, "SAMPLEID");
            List<Long> orderIdList = orderList.stream().map(a -> a.getId()).collect(Collectors.toList());
            if (!orderIdList.isEmpty()) {
                List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");
                if (!orderTaskList.isEmpty()) {
                    orderTaskList.stream().forEach(a -> a.setStatus(EmcBizConstant.ORDERTASK_STATUS_DRAFT));
                    orderTaskService.getDao().update(orderTaskList, "STATUS");
                }
            }

            ProjectNodeUtils.updateNodes(projectIdList);
            // 将sample表status置为draft
            sampleList.stream().forEach(a -> a.setStatus(EmcBizConstant.SAMPLE_STATUS_DRAFT));
            getDao().update(sampleList, "STATUS");

        }

        return instanceStatusList;
    }

    public List<CoreBpmnRuntimeSource<EmcSampleBean, Long>> parseUndoableRuntimeSource(List<Long> itemIdList, RestJsonWrapperBean wrapper) {
        List<EmcSampleBean> selectedItemList = selectListByFilter(SearchFilter.instance().match("ID", itemIdList).filter(MatchPattern.OR));

        Map<Long, Map<String, Object>> map = getBpmnVars(wrapper, selectedItemList);

        List<CoreBpmnRuntimeSource<EmcSampleBean, Long>> sourceList = new ArrayList<>();
        for (EmcSampleBean item : selectedItemList) {
            CoreBpmnRuntimeSource<EmcSampleBean, Long> source = CoreBpmnRuntimeSource.of(getDao().getTable(), item, wrapper.getParamValue("bpmn_comment"), map.get(item.getId()));

            source.setDescSupplier(getDescSupplier(source));
            source.setRuntimeTaskName("样品接收");

            sourceList.add(source);
        }

        return sourceList;
    }

    @Override
    public Page<EmcSampleBean> selectSamplePagination(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();

        return selectPagination(() -> getDao().selectSamplePagination(parameter), rowBounds);
    }

    @Override
    public Page<EmcSampleBean> selectMobileScanner(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        List<EmcSampleBean> list = selectPagination(() -> getDao().selectMobileScanner(parameter), rowBounds).getRows();
        if(list.size() == 1 && StringUtils.isEmpty(list.get(0).getSampleNo())) {
            return new Pagination<>();
        }
        return new Pagination<>(list);
    }

    @Override
    public Page<EmcSampleBean> selectDetailLabel(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return selectPagination(() -> getDao().selectDetailLabel(parameter), rowBounds);
    }

    @Override
    public Page<EmcSampleBean> selectLabelDetail(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return selectPagination(() -> getDao().selectLabelDetail(parameter), rowBounds);
    }

    @Override
    public Page<EmcOrderTaskBean> selectOrderTaskBySampleId(Long id, RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValue(id, "SAMPLEID", Collections.singletonList("ID"));
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());

        if (orderIdList.isEmpty()) {
            return new Pagination<>();
        }

        return orderTaskService.selectPaginationByFilter(SearchFilter.instance().match("ORDERID", orderIdList).filter(MatchPattern.OR), wrapper);
    }

    @Override
    public void batchInputOrderTaskResult(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskResultBean> copiedOrderTaskResultList = wrapper.parse(EmcOrderTaskResultBean.class, 0);
        List<EmcOrderTaskResultBean> notEmptyTaskResultList = copiedOrderTaskResultList.stream().filter(result -> !StringUtils.isEmpty(result.getOriginalResult())).collect(Collectors.toList());

        if (notEmptyTaskResultList.isEmpty()) {
            return;
        }

        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class, 1);
        List<Long> orderTaskIdList = orderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());

        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID", Arrays.asList("ID", "ORDERTASKID", "ANALYSISTYPE", "ITEMNAME"));
        List<EmcOrderTaskResultBean> updateTaskResultList = new ArrayList<>();

        orderTaskResultList.forEach(taskResult -> {
            notEmptyTaskResultList.stream().filter(copiedResult -> StringUtils.equals(copiedResult.getItemName(), taskResult.getItemName())).findFirst().ifPresent(copiedResult -> {
                taskResult.setOriginalResult(copiedResult.getOriginalResult());
                updateTaskResultList.add(taskResult);
            });
        });

        if (!updateTaskResultList.isEmpty()) {
            List<Long> updateResultIdList = updateTaskResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
            orderTaskResultService.getDao().update(updateTaskResultList, "ORIGINALRESULT");
            EmcOrderTaskResultComputingEngine.getEngine(updateResultIdList).compute();
        }
    }

    @Override
    public Page<EmcOrderTaskBean> selectOrderTaskBySample(RestJsonWrapperBean wrapper) {
        String sampleIds = wrapper.getFilterValue("ids");
        List<String> sampleIdList = Arrays.asList(StringUtils.split(sampleIds, ","));
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(sampleIdList, "SAMPLEID", Collections.singletonList("ID"));
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());

        if (orderIdList.isEmpty()) {
            return new Pagination<>();
        }

        return orderTaskService.selectPaginationByFilter(SearchFilter.instance().match("ORDERID", orderIdList).filter(MatchPattern.OR), wrapper);
    }

    // 需要查询所有状态的数据，所以重新写了
    @Override
    public Page<EmcSampleBean> selectAllPagination(RestJsonWrapperBean wrapper) {
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
    public Page<EmcItemBean> selectEditItem(Long id) {
        EmcOrderTaskBean orderTask = orderTaskService.selectById(id);
        // 取样品分析项组
        EmcOrderItemGroupBean filter = new EmcOrderItemGroupBean();
        filter.setSchemeName(orderTask.getSchemeName());
        filter.setTestId(orderTask.getTestId());
        filter.setTestMethodId(orderTask.getTestMethodId());
        filter.setMonitorTmplId(orderTask.getMonitorTmplId());
        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectList(filter);
        if (orderItemGroupList.isEmpty()) {
            return null;
        }
        // 根据分析项组获取分析项
        List<Long> itemGroupIdList = orderItemGroupList.stream().map(a -> a.getItemGroupId()).collect(Collectors.toList());
        List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID");
        List<Long> itemIdList = groupItemList.stream().map(a -> a.getItemId()).distinct().collect(Collectors.toList());
        List<EmcItemBean> itemList = itemService.selectListByFilter(SearchFilter.instance().match("ID", itemIdList).filter(MatchPattern.OR));
        // 筛选类型是采样且启用的分析项
        List<EmcItemBean> selectItemList = itemList.stream().filter(a -> "sampling".equals(a.getAnalysisCategory()) && "1".equals(a.getActivatedFlag())).collect(Collectors.toList());
        return new Pagination<EmcItemBean>(selectItemList);
    }

    @Override
    @Transactional
    public Long editItem(RestJsonWrapperBean wrapper) {
        // 准备数据
        List<EmcItemBean> itemList = wrapper.parse(EmcItemBean.class);
        EmcOrderTaskResultBean orderTaskResult = wrapper.parseUnique(EmcOrderTaskResultBean.class);
        String sampleId = wrapper.getFilterValue("sampleId");
        Long testId = orderTaskResult.getTestId();
        Long testMethodId = orderTaskResult.getTestMethodId();

        List<EmcOrderTaskResultBean> orderTaskResultTempList = orderTaskResultService
                .selectListByFilter(SearchFilter.instance().match("ORDERID", orderTaskResult.getOrderId()).filter(MatchPattern.SEQ).match("ANALYSISTYPE", "sampling").filter(MatchPattern.SEQ));

        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultTempList.stream()
                .filter(a -> StringUtils.equals(a.getExt$Item("sampleid"), sampleId) && ObjectUtils.equals(a.getTestMethodId(), testMethodId) && ObjectUtils.equals(a.getTestId(), testId))
                .collect(Collectors.toList());

        // 获取order数据
        List<Long> orderIdList = orderTaskResultList.stream().map(a -> a.getOrderId()).distinct().collect(Collectors.toList());
        List<EmcOrderBean> orderList = orderService.getDao().selectListByIds(orderIdList);

        List<EmcOrderTaskResultBean> addList = new ArrayList<>();
        List<EmcOrderTaskResultBean> delList = new ArrayList<>();
        // 根据样品分组
        Map<Long, List<EmcOrderTaskResultBean>> orderTaskResultMap = orderTaskResultList.stream().collect(Collectors.groupingBy(b -> b.getOrderId()));

        orderTaskResultMap.forEach((key, value) -> {
            // 两个list交集
            List<EmcOrderTaskResultBean> tempList = new ArrayList<>();

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
                    String pnFunc = a.getPnFunc();
                    EmcOrderBean orderBean = orderList.stream().filter(e -> ObjectUtils.equals(key, e.getId())).findAny().orElse(null);
                    Long sampleNum = orderBean.getSampleNum();
                    EmcOrderTaskResultBean orderTaskResultTemp = value.stream().filter(e -> ObjectUtils.equals(e.getOrderId(), orderBean.getId())).findAny().orElse(null);

                    if ((EmcConstant.PN_PERAVERAGE.equalsIgnoreCase(pnFunc) || EmcConstant.PN_DAYAVERAGE.equalsIgnoreCase(pnFunc)) && !ObjectUtils.equals(1L, sampleNum)) {
                        // 如果分析项PN函数类型是日均值或者次均值且采样数不等于一的,不用插入分析项
                    } else {
                        for (int i = 1; i <= repeatItem; i++) {
                            EmcOrderTaskResultBean orderTaskResultBean = new EmcOrderTaskResultBean();

                            BeanUtils.copyProperties(orderTaskResultTemp, orderTaskResultBean, EmcConstant.CREATED_COLUMNS);
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

    // =============================私有方法======================

    private void updateSampleStatus(List<Long> sampleIdList, List<EmcOrderBean> orderList, List<EmcOrderTaskBean> allReportOrderTaskList) {

        List<EmcSampleBean> updateSampleList = new ArrayList<>();

        for (Long sampleId : sampleIdList) {

            List<EmcOrderBean> sampleOrderList = orderList.stream().filter(e -> ObjectUtils.equals(e.getSampleId(), sampleId)).collect(Collectors.toList());

            if (!sampleOrderList.isEmpty()) {
                EmcOrderBean firstSampleOrder = sampleOrderList.get(0);

                if (allReportOrderTaskList.stream()
                        .filter(e -> ObjectUtils.equals(e.getProjId(), firstSampleOrder.getProjId()) && ObjectUtils.equals(e.getMonitorTmplId(), firstSampleOrder.getMonitorTmplId())
                                && sampleOrderList.stream().anyMatch(o -> o.getId().equals(e.getOrderId())))
                        .allMatch(e -> EmcBizConstant.ORDERTASK_STATUS_DONE.equals(e.getStatus()))
                        || allReportOrderTaskList.stream()
                                .filter(e -> ObjectUtils.equals(e.getProjId(), firstSampleOrder.getProjId()) && ObjectUtils.equals(e.getMonitorTmplId(), firstSampleOrder.getMonitorTmplId())
                                        && ObjectUtils.equals(EmcBizConstant.ANALYSISCATEGORY_ANALYSIS, e.getAnalysisType())
                                        && sampleOrderList.stream().anyMatch(o -> o.getId().equals(e.getOrderId())))
                                .collect(Collectors.toList()).isEmpty()) {

                    EmcSampleBean sample = new EmcSampleBean();
                    sample.setId(sampleId);
                    sample.setStatus(EmcBizConstant.SAMPLE_STATUS_DONE);

                    updateSampleList.add(sample);

                }
            }
        }
        if (!updateSampleList.isEmpty()) {

            getDao().update(updateSampleList, "STATUS");

        }
    }

    protected void updateReportStatus(List<Long> sampleIdList, List<EmcOrderBean> orderList, List<EmcOrderTaskBean> orderTaskList) {
        List<EmcReportBean> submitReportList = new ArrayList<>();

        List<EmcOrderTaskBean> reportOrderTaskList = orderTaskList.stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getProjId() + "#" + o.getMonitorTmplId()))), ArrayList::new));

        List<EmcOrderTaskBean> allReportOrderTaskList = orderTaskService.getDao().selectList(reportOrderTaskList, Arrays.asList("PROJID", "MONITORTMPLID"), Collections.emptyList());

        // 判断每个采样单下的样品是否全部置为done，如果是，该采样单状态置为done
        updateSampleStatus(sampleIdList, orderList, allReportOrderTaskList);

        for (EmcOrderTaskBean reportOrderTask : reportOrderTaskList) {

            if (allReportOrderTaskList.stream()
                    .filter(e -> ObjectUtils.equals(e.getProjId(), reportOrderTask.getProjId()) && ObjectUtils.equals(e.getMonitorTmplId(), reportOrderTask.getMonitorTmplId()))
                    .allMatch(e -> EmcBizConstant.ORDERTASK_STATUS_DONE.equals(e.getStatus()))) {

                EmcReportBean report = new EmcReportBean();

                report.setProjId(reportOrderTask.getProjId());
                report.setMonitorTmplId(reportOrderTask.getMonitorTmplId());

                submitReportList.add(report);

            }

        }

        if (!submitReportList.isEmpty()) {

            List<Long> updateReportIdList = reportService.getDao().selectList(submitReportList, Arrays.asList("PROJID", "MONITORTMPLID"), Arrays.asList("ID")).stream().map(e -> e.getId())
                    .collect(Collectors.toList());

            EmcReportBean report = new EmcReportBean();
            report.setStatus(EmcBizConstant.REPORT_STATUS_REPORTEDIT);

            reportService.getDao().updateByIds(report, updateReportIdList, "STATUS");

            // 将报告所在项目节点状态置为报告编制
            List<Long> projIdList = submitReportList.stream().map(e -> e.getProjId()).distinct().collect(Collectors.toList());
            ProjectNodeUtils.updateNodes(projIdList);
        }
    }

    public void insertAuditRecordBySample(List<Long> sampleIdList, String bpmnStatus, String comment, List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList) {
        List<EmcAuditRecordBean> auditRecordList = new ArrayList<>();
        List<EmcSampleBean> sampleList = getDao().selectListByIds(sampleIdList);
        for (EmcSampleBean sample : sampleList) {
            EmcAuditRecordBean auditRecord = new EmcAuditRecordBean();
            auditRecord.setId(ApplicationContextHelper.getNextIdentity());
            auditRecord.setProjId(sample.getProjId());
            auditRecord.setProjNo(sample.getProjNo());
            auditRecord.setSampleId(sample.getId());
            auditRecord.setSampleNo(sample.getSampleNo());
            auditRecord.setComments(comment);

            auditRecord.setLogDesc("采样单:" + sample.getSampleNo());
            auditRecord.setBizCategory("sample");

            if ("PROCESS_START".contentEquals(bpmnStatus)) {
                auditRecord.setStepCode("3");
                auditRecord.setAuditCategory("采样管理");
                auditRecord.setTaskName("结果录入-按采样单");
                auditRecord.setOperateCategory("提交");
                auditRecord.setNextTaskName("同组互审-按采样单");
            } else if ("PROCESS_COMPLETE".equals(bpmnStatus)) {
                auditRecord.setStepCode("3");
                auditRecord.setAuditCategory("采样管理");
                auditRecord.setOperateCategory("审核");
                @SuppressWarnings("rawtypes")
                CoreBpmnInstanceStatusDTO bpmnInstance = coreBpmnInstanceStatusDTOList.stream().filter(f -> ObjectUtils.equals(f.getId(), sample.getId())).findFirst().orElse(null);
                if ("audit".equals(bpmnInstance.getStatusCode())) {
                    auditRecord.setTaskName("采样审核");
                    auditRecord.setNextTaskName("样品接收");
                } else {
                    auditRecord.setTaskName("同组互审-按采样单");
                    auditRecord.setNextTaskName("主任审核-按采样单");
                }
            } else if ("PROCESS_ACCEPT".equals(bpmnStatus)) {
                auditRecord.setStepCode("4");
                auditRecord.setAuditCategory("样品管理");
                auditRecord.setOperateCategory("接收");
                auditRecord.setTaskName("样品接收");
                auditRecord.setNextTaskName("分析录入");
            } else if ("PROCESS_UNDO".equals(bpmnStatus)) {
                auditRecord.setStepCode("4");
                auditRecord.setAuditCategory("样品管理");
                auditRecord.setOperateCategory("退回");
                auditRecord.setTaskName("样品接收");
                if (EmcBizConstant.BP_SOURCE_ORDERREGISTRATION.equals(sample.getSource())) {
                    auditRecord.setNextTaskName("项目登记");
                } else {
                    auditRecord.setNextTaskName("现场采样");
                }

            }
            auditRecordList.add(auditRecord);
        }
        auditRecordService.getDao().insert(auditRecordList);
    }

    protected void executeTermination(List<Long> sampleIdList, List<EmcOrderBean> orderList) {
        List<EmcOrderBean> terminatedOrderList = orderList.stream().filter(f -> Constant.YES.equals(f.getTermination())).collect(Collectors.toList());
        List<Long> terminatedOrderIdList = terminatedOrderList.stream().map(e -> e.getId()).collect(Collectors.toList());

        List<EmcOrderTaskBean> updateOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(terminatedOrderIdList, "ORDERID");
        updateOrderTaskList.stream().forEach(e -> {
            e.setStatus(EmcBizConstant.ORDERTASK_STATUS_DONE);
        });

        List<Long> updateSampleIdList = new ArrayList<>();
        for (Long sampleId : sampleIdList) {
            List<EmcOrderBean> thisOrderList = orderList.stream().filter(f -> ObjectUtils.equals(f.getSampleId(), sampleId)).collect(Collectors.toList());
            List<EmcOrderBean> thisTerminatedOrderList = terminatedOrderList.stream().filter(f -> ObjectUtils.equals(f.getSampleId(), sampleId)).collect(Collectors.toList());

            if (!thisTerminatedOrderList.isEmpty() && thisOrderList.size() == thisTerminatedOrderList.size()) {
                updateSampleIdList.add(sampleId);// 当前采样单下的所有样品都已经被终止
            }
        }
        List<EmcSampleBean> updateSampleList = getDao().selectListByIds(updateSampleIdList);
        updateSampleList.stream().forEach(e -> {
            e.setStatus(EmcBizConstant.SAMPLE_STATUS_DONE);
            e.setRemark("终止");
        });

        CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
        parameter.setComment("终止");
        this.endTask(updateSampleIdList, parameter);

        orderTaskService.getDao().update(updateOrderTaskList, "STATUS");
        getDao().update(updateSampleList, "STATUS", "REMARK");

        reportService.updateReportStatus(updateOrderTaskList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_UNDO)
    public void bizUndo(RestJsonWrapperBean wrapper) {
        List<Long> idList = wrapper.parse(EmcSampleBean.class).stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcSampleBean> sampleList = getDao().selectListByIds(idList);
        List<Long> projIdList = sampleList.stream().map(a -> a.getProjId()).distinct().collect(Collectors.toList());
        // 采样退回插入审核记录(先生成日志再删采样单)
        insertAuditRecordBySample(idList, "PROCESS_UNDO", wrapper.getParamValue("bpmn_comment"), null);

        // 样品登记
        List<EmcSampleBean> orderRegistrationSampleList = sampleList.stream().filter(f -> EmcBizConstant.BP_SOURCE_ORDERREGISTRATION.equals(f.getSource())).collect(Collectors.toList());
        if (!orderRegistrationSampleList.isEmpty()) {
            List<Long> projectIdList = orderRegistrationSampleList.stream().map(r -> r.getProjId()).distinct().collect(Collectors.toList());

            // 回退项目后，手动提交审核
            List<List<String>> bodyList = new ArrayList<>();
            List<String> firstBodyList = new ArrayList<>();
            projectIdList.forEach(e -> {
                Map<String, Long> hashMap = new HashMap<>();
                hashMap.put("id", e);
                String jsonString = JSONObject.toJSONString(hashMap);
                firstBodyList.add(jsonString);
            });
            bodyList.add(firstBodyList);
            List<GenericService<?, ?>> bodyServiceList = new ArrayList<>();
            bodyServiceList.add(projectService);
            RestJsonWrapperBean newRestJsonWrapper = new RestJsonWrapperBean(null, bodyList, bodyServiceList);
            newRestJsonWrapper.setParamValue("bpmn_comment", wrapper.getParamValue("bpmn_comment"));
            projectService.undoBasic(newRestJsonWrapper);

            // 将项目节点状态置为样品登记
            EmcProjectBean updateProject = new EmcProjectBean();
            updateProject.setProjNode(EmcBizConstant.PROJECT_CN_NODE_FABZ);
            updateProject.setStatus(EmcBizConstant.PROJECT_STATUS_CREATE);
            projectService.getDao().updateByIds(updateProject, projectIdList, "PROJNODE", "STATUS");

            // 删除采样表
            List<EmcSampleBean> deleteSampleList = getDao().selectListByOneColumnValues(projectIdList, "PROJID");
            getDao().deleteBy(deleteSampleList);

            // 删除业务表：样品测试、样品结果
            List<EmcOrderTaskBean> deleteTaskList = orderTaskService.getDao().selectListByOneColumnValues(projectIdList, "PROJID");
            List<Long> folderIdList = deleteTaskList.stream().map(e -> e.getFolderId()).distinct().collect(Collectors.toList());
            List<EmcFolderBean> folderList = new ArrayList<>();
            folderIdList.forEach(e -> {
                EmcFolderBean folder = new EmcFolderBean();
                folder.setId(e);
                folder.setStatus(EmcBizConstant.FOLDER_STATUS_DRAFT);
                folder.setProcessStatus(EmcBizConstant.FOLDER_PROCESSSTATUS_DRAFT);
                folderList.add(folder);
            });
            folderService.getDao().update(folderList, "STATUS", "PROCESSSTATUS");
            List<EmcOrderTaskResultBean> deleteResultList = orderTaskResultService.getDao().selectListByOneColumnValues(projectIdList, "PROJID");
            orderTaskService.getDao().deleteBy(deleteTaskList);
            orderTaskResultService.getDao().deleteBy(deleteResultList);

            // 删除report表
            List<EmcReportBean> deleteReportList = reportService.getDao().selectListByOneColumnValues(projectIdList, "PROJID");
            reportService.getDao().deleteBy(deleteReportList);

            // 手动在审核记录添加样品接收审核节点
            List<CoreBpmnCommentBean> updateBpmnCommentList = new ArrayList<>();
            projectIdList.forEach(id -> {
                CoreBpmnCommentBean updateBpmnComment = new CoreBpmnCommentBean();
                updateBpmnComment.setTargetId(projectService.getDao().getTable() + "$" + id);
                updateBpmnComment.setStatus("UNDO");
                updateBpmnComment.setTaskName(EmcBizConstant.ORDER_TASK_LOG_SAMPLE_ACCEPT);
                updateBpmnCommentList.add(updateBpmnComment);
            });
            coreBpmnCommentService.getDao().update(updateBpmnCommentList, ArrayUtils.asList("taskName"), "targetId", "status");
        }

        // 非样品登记的修改样品测试表状态
        List<EmcSampleBean> notOrderRegistrationSampleList = sampleList.stream().filter(f -> !EmcBizConstant.BP_SOURCE_ORDERREGISTRATION.equals(f.getSource())).collect(Collectors.toList());
        if (!notOrderRegistrationSampleList.isEmpty()) {
            List<Long> notOrderRegistrationSampleIdList = notOrderRegistrationSampleList.stream().map(EmcSampleBean::getId).collect(Collectors.toList());
            List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(notOrderRegistrationSampleIdList, "SAMPLEID", Collections.singletonList("ID"));
            List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
            List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID", Collections.singletonList("ID"));

            if (!orderTaskList.isEmpty()) {
                orderTaskList.forEach(orderTask -> orderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_DRAFT));
                orderTaskService.getDao().update(orderTaskList, "STATUS");
            }

            // 对于已经走完采样三级审核的采样单，样品接收退回时需要执行工作流退回
            List<Long> doneSampleIdList = notOrderRegistrationSampleList.stream().filter(sample -> StringUtils.equalsIgnoreCase(ProcessStatus.DONE.name(), sample.getProcessStatus())).map(EmcSampleBean::getId).collect(Collectors.toList());
            if (!doneSampleIdList.isEmpty()) {
                CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
                parameter.setComment("样品接收退回");
                this.undo(doneSampleIdList, parameter);
            }

            List<Long> approveSampleIdList = notOrderRegistrationSampleList.stream().filter(sample -> StringUtils.startsWithIgnoreCase(sample.getProcessStatus(), ProcessStatus.APPROVE.name())).map(EmcSampleBean::getId).collect(Collectors.toList());
            if (!approveSampleIdList.isEmpty()) {
                CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
                parameter.setComment("样品接收退回");
                this.rejectTask(approveSampleIdList, parameter);
            }

            // 将sample表status置为draft
            sampleList.forEach(sample -> sample.setStatus(EmcBizConstant.SAMPLE_STATUS_DRAFT));
            getDao().update(sampleList, "STATUS");
        }
        // 修改项目节点状态
        ProjectNodeUtils.updateNodes(projIdList);
    }


    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    @ValidateDataWith(EmcSampleSubmitValidator.class)
    public Long resultInput(RestJsonWrapperBean wrapper) {
        List<EmcSampleBean> originalSampleList = wrapper.parse(EmcSampleBean.class);
        List<Long> originalSampleIdList = originalSampleList.stream().map(EmcSampleBean::getId).collect(Collectors.toList());
        if (originalSampleIdList.isEmpty()) {
            return 0L;
        }

        // 现场采样提交只能提交特定状态的采样单，防止PC端和平板端提交同一采样单导致状态异常
        List<EmcSampleBean> submittedSampleList = getDao().selectListByIds(originalSampleIdList, Arrays.asList("ID", "SAMPLENO", "PROJID", "STATUS"));
        List<EmcSampleBean> sampleList = submittedSampleList.stream().filter(sample -> StringUtils.equals("draft", sample.getStatus())).collect(Collectors.toList());
        List<Long> sampleIdList = sampleList.stream().map(EmcSampleBean::getId).collect(Collectors.toList());
        if (sampleIdList.isEmpty()) {
            return 0L;
        }

        // 采样单的状态改成logged
        List<EmcSampleBean> updateSampleList = new ArrayList<>();
        sampleList.forEach(e -> {
            EmcSampleBean updateSample = new EmcSampleBean();
            updateSample.setId(e.getId());
            updateSample.setStatus(EmcBizConstant.SAMPLE_STATUS_LOGGED);
            updateSampleList.add(updateSample);
        });
        getDao().update(updateSampleList, "STATUS");

        // 把类型为分析的ordtask状态改成preLogged（样品接收）
        // 把类型为采样的ordtask状态改成logged（结果录入）。
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(sampleIdList, "SAMPLEID");
        List<Long> projIdList = orderList.stream().map(EmcOrderBean::getProjId).distinct().collect(Collectors.toList());
        if (!orderList.isEmpty()) {

            List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
            List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");
            if (!orderTaskList.isEmpty()) {
                List<EmcOrderTaskBean> sampleOrderTaskList = orderTaskList.stream().filter(e -> ObjectUtils.equals(EmcBizConstant.ANALYSISCATEGORY_SAMPLING, e.getAnalysisType()))
                        .collect(Collectors.toList());
                List<EmcOrderTaskBean> analysisOrderTaskList = orderTaskList.stream().filter(e -> ObjectUtils.equals(EmcBizConstant.ANALYSISCATEGORY_ANALYSIS, e.getAnalysisType()))
                        .collect(Collectors.toList());
                List<EmcOrderTaskBean> updateOrderTaskList = new ArrayList<>();
                if (!sampleOrderTaskList.isEmpty()) {
                    sampleOrderTaskList.forEach(e -> {
                        EmcOrderTaskBean updateOrderTask = new EmcOrderTaskBean();
                        updateOrderTask.setId(e.getId());
                        updateOrderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED);
                        updateOrderTaskList.add(updateOrderTask);
                    });
                }
                if (!analysisOrderTaskList.isEmpty()) {
                    analysisOrderTaskList.forEach(e -> {
                        EmcOrderTaskBean updateOrderTask = new EmcOrderTaskBean();
                        updateOrderTask.setId(e.getId());
                        updateOrderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_PRELOGGED);
                        updateOrderTaskList.add(updateOrderTask);
                    });
                }
                orderTaskService.getDao().update(updateOrderTaskList, "STATUS");
            }
        }
        ProjectNodeUtils.updateNodes(projIdList);

        // 添加提交审核记录
        insertAuditRecord(sampleList);

        return sampleList.get(0).getId();
    }

    @Override
    public void acceptedUndo(RestJsonWrapperBean wrapper) {
        List<EmcSampleBean> acceptedSampleList = wrapper.parse(EmcSampleBean.class);
        List<Long> acceptedSampleIdList = acceptedSampleList.stream().map(EmcSampleBean::getId).collect(Collectors.toList());
        List<EmcSampleBean> sampleList = getDao().selectListByIds(acceptedSampleIdList);
        List<Long> projIdList = sampleList.stream().map(EmcSampleBean::getProjId).distinct().collect(Collectors.toList());
        // 采样退回插入审核记录(先生成日志再删采样单)
        insertAuditRecordBySample(acceptedSampleIdList, "PROCESS_UNDO", wrapper.getParamValue("bpmn_comment"), null);

        // 样品登记
        List<EmcSampleBean> orderRegistrationSampleList = sampleList.stream().filter(f -> EmcBizConstant.BP_SOURCE_ORDERREGISTRATION.equals(f.getSource())).collect(Collectors.toList());
        if (!orderRegistrationSampleList.isEmpty()) {
            List<Long> projectIdList = orderRegistrationSampleList.stream().map(EmcSampleBean::getProjId).distinct().collect(Collectors.toList());

            // 回退项目后，手动提交审核
            List<List<String>> bodyList = new ArrayList<>();
            List<String> firstBodyList = new ArrayList<>();
            projectIdList.forEach(e -> {
                Map<String, Long> hashMap = new HashMap<>();
                hashMap.put("id", e);
                String jsonString = JSONObject.toJSONString(hashMap);
                firstBodyList.add(jsonString);
            });
            bodyList.add(firstBodyList);
            List<GenericService<?, ?>> bodyServiceList = new ArrayList<>();
            bodyServiceList.add(projectService);
            RestJsonWrapperBean newRestJsonWrapper = new RestJsonWrapperBean(null, bodyList, bodyServiceList);
            newRestJsonWrapper.setParamValue("bpmn_comment", wrapper.getParamValue("bpmn_comment"));
            projectService.undoBasic(newRestJsonWrapper);

            // 将项目节点状态置为样品登记
            EmcProjectBean updateProject = new EmcProjectBean();
            updateProject.setProjNode(EmcBizConstant.PROJECT_CN_NODE_FABZ);
            updateProject.setStatus(EmcBizConstant.PROJECT_STATUS_CREATE);
            projectService.getDao().updateByIds(updateProject, projectIdList, "PROJNODE", "STATUS");

            // 删除采样表
            List<EmcSampleBean> deleteSampleList = getDao().selectListByOneColumnValues(projectIdList, "PROJID");
            getDao().deleteBy(deleteSampleList);

            // 删除业务表：样品测试、样品结果
            List<EmcOrderTaskBean> deleteTaskList = orderTaskService.getDao().selectListByOneColumnValues(projectIdList, "PROJID");
            List<Long> folderIdList = deleteTaskList.stream().map(EmcOrderTaskBean::getFolderId).distinct().collect(Collectors.toList());
            List<EmcFolderBean> folderList = new ArrayList<>();
            folderIdList.forEach(e -> {
                EmcFolderBean folder = new EmcFolderBean();
                folder.setId(e);
                folder.setStatus(EmcBizConstant.FOLDER_STATUS_DRAFT);
                folder.setProcessStatus(EmcBizConstant.FOLDER_PROCESSSTATUS_DRAFT);
                folderList.add(folder);
            });
            folderService.getDao().update(folderList, "STATUS", "PROCESSSTATUS");
            List<EmcOrderTaskResultBean> deleteResultList = orderTaskResultService.getDao().selectListByOneColumnValues(projectIdList, "PROJID");
            orderTaskService.getDao().deleteBy(deleteTaskList);
            orderTaskResultService.getDao().deleteBy(deleteResultList);

            // 删除测试批及相关质控样
            List<String> relatedRunIdList = deleteResultList.stream().filter(r -> !StringUtils.isEmpty(r.getRunId())).map(EmcOrderTaskResultBean::getRunId).distinct().collect(Collectors.toList());
            runService.delete(relatedRunIdList);

            // 删除report表
            List<EmcReportBean> deleteReportList = reportService.getDao().selectListByOneColumnValues(projectIdList, "PROJID");
            reportService.getDao().deleteBy(deleteReportList);

            // 手动在审核记录添加样品接收审核节点
            List<CoreBpmnCommentBean> updateBpmnCommentList = new ArrayList<>();
            projectIdList.forEach(id -> {
                CoreBpmnCommentBean updateBpmnComment = new CoreBpmnCommentBean();
                updateBpmnComment.setTargetId(projectService.getDao().getTable() + "$" + id);
                updateBpmnComment.setStatus("UNDO");
                updateBpmnComment.setTaskName(EmcBizConstant.ORDER_TASK_LOG_SAMPLE_ACCEPT);
                updateBpmnCommentList.add(updateBpmnComment);
            });
            coreBpmnCommentService.getDao().update(updateBpmnCommentList, ArrayUtils.asList("taskName"), "targetId", "status");
        }

        // 非样品登记的修改样品测试表状态
        List<EmcSampleBean> notOrderRegistrationSampleList = sampleList.stream().filter(f -> !EmcBizConstant.BP_SOURCE_ORDERREGISTRATION.equals(f.getSource())).collect(Collectors.toList());
        if (!notOrderRegistrationSampleList.isEmpty()) {
            List<Long> notOrderRegistrationSampleIdList = notOrderRegistrationSampleList.stream().map(EmcSampleBean::getId).collect(Collectors.toList());
            List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(notOrderRegistrationSampleIdList, "SAMPLEID", Collections.singletonList("ID"));
            List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
            List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID", Arrays.asList("ID", "RUNID"));

            if (!orderTaskList.isEmpty()) {
                // 删除样品测试关联的批
                List<String> relatedRunIdList = orderTaskList.stream().filter(task -> !StringUtils.isEmpty(task.getRunId())).map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());
                runService.delete(relatedRunIdList);

                orderTaskList.forEach(orderTask -> orderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_DRAFT));
                orderTaskService.getDao().update(orderTaskList, "STATUS");
            }

            // 对于已经走完采样三级审核的采样单，样品接收退回时需要执行工作流退回
            List<Long> doneSampleIdList = notOrderRegistrationSampleList.stream().filter(sample -> StringUtils.equalsIgnoreCase(ProcessStatus.DONE.name(), sample.getProcessStatus())).map(EmcSampleBean::getId).collect(Collectors.toList());
            if (!doneSampleIdList.isEmpty()) {
                CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
                parameter.setComment("样品接收退回");
                this.undo(doneSampleIdList, parameter);
            }

            List<Long> approveSampleIdList = notOrderRegistrationSampleList.stream().filter(sample -> StringUtils.startsWithIgnoreCase(sample.getProcessStatus(), ProcessStatus.APPROVE.name())).map(EmcSampleBean::getId).collect(Collectors.toList());
            if (!approveSampleIdList.isEmpty()) {
                CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
                parameter.setComment("样品接收退回");
                this.rejectTask(approveSampleIdList, parameter);
            }

            // 将sample表status置为draft
            sampleList.forEach(sample -> sample.setStatus(EmcBizConstant.SAMPLE_STATUS_DRAFT));
            getDao().update(sampleList, "STATUS");
        }
        // 修改项目节点状态
        ProjectNodeUtils.updateNodes(projIdList);
    }

    /**
     * 判断是否更新项目表状态
     *
     * @param projIdList
     */
    public void updateProject(List<Long> projIdList) {
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(projIdList, "PROJID");
        List<EmcSampleBean> sampleList = getDao().selectListByOneColumnValues(projIdList, "PROJID");
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

    /**
     * 添加提交审核记录
     * @param sampleList
     */
    private void insertAuditRecord(List<EmcSampleBean> sampleList) {
        List<EmcAuditRecordBean> auditRecordList = new ArrayList<>();
        sampleList.forEach(sample -> {
            EmcAuditRecordBean auditRecord = new EmcAuditRecordBean();
            auditRecord.setId(ApplicationContextHelper.getNextIdentity());
            auditRecord.setProjId(sample.getProjId());
            auditRecord.setProjNo(sample.getProjNo());
            auditRecord.setSampleId(sample.getId());
            auditRecord.setSampleNo(sample.getSampleNo());
            auditRecord.setComments("提交");
            auditRecord.setLogDesc("采样单:" + sample.getSampleNo());
            auditRecord.setBizCategory("sample");
            auditRecord.setStepCode("3");
            auditRecord.setAuditCategory("采样管理");
            auditRecord.setTaskName("现场采样");
            auditRecord.setOperateCategory("提交");
            auditRecord.setNextTaskName("样品接收");
            auditRecordList.add(auditRecord);
        });

        auditRecordService.getDao().insert(auditRecordList);
    }

    private void addOrderRetain(List<EmcOrderBean> orderList, List<EmcOrderTaskBean> taskList) {

        List<Long> testMethodIdList = taskList.stream().map(a -> a.getTestMethodId()).distinct().collect(Collectors.toList());
        List<EmcTestMethodBean> testMethodList = testMethodService.selectListByIds(testMethodIdList);
        List<Integer> retainTimes = testMethodList.stream().filter(a -> !StringUtils.isEmpty(a.getRetainTime())).distinct().map(b -> NumberUtils.parseInt(b.getRetainTime())).collect(Collectors.toList());
        int retainHours = retainTimes.isEmpty() ? 0 : retainTimes.stream().min(Comparator.naturalOrder()).get();

        List<EmcOrderRetainBean> insertList = new ArrayList<>();
        for(EmcOrderBean o : orderList) {
            EmcOrderRetainBean retain = new EmcOrderRetainBean();
            retain.setId(ApplicationContextHelper.getNextIdentity());
            retain.setOrderId(o.getId());
            retain.setProjId(o.getProjId());
            retain.setFolderId(o.getFolderId());
            retain.setRetainQty(1L);
            retain.setRetainUserId(LocalContextHelper.getLoginUserId());
            LocalDate date = o.getSampleBeginDate();
            if(date!=null) {
                String time = StringUtils.isEmpty(o.getSampleBeginTime()) ? "00:00:00" : o.getSampleBeginTime();
                String dateTime = date.toString() + " " + time;
                LocalDateTime sampleDateTime = DateTimeUtils.parseLocalDateTime(dateTime);
                sampleDateTime = sampleDateTime.plusHours(retainHours);
                retain.setRetainTimeLimit(sampleDateTime);
            }
            insertList.add(retain);
        }
        orderRetainService.getDao().insert(insertList);
    }

    @Override
    public Page<EmcSampleBean> selectTodoSampleList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return selectPagination(() -> getDao().selectTodoSampleList(parameter), rowBounds);
    }

    @Override
    public Page<EmcSampleBean> selectTodoSampleOrderReceiptList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return selectPagination(() -> getDao().selectTodoSampleOrderReceiptList(parameter), rowBounds);
    }
}
