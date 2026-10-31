package com.sunwayworld.emc.module.bp.edit.projectchange.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfBizConstant;
import com.sunwayworld.emc.framework.projectnode.ProjectNodeUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.PfOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.PfOrderTaskService;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcFolderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderItemGroupChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjOrderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjSchemeChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjectChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.dao.EmcProjectChangeDao;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcFolderChangeService;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcOrderChangeService;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcOrderItemGroupChangeService;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcProjOrderChangeService;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcProjSchemeChangeService;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcProjectChangeService;
import com.sunwayworld.emc.module.bp.edit.projectchange.validator.EmcProjectChangeCompleteTaskValidator;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectDirectorBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectDirectorService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.project.project.validator.EmcProjectCompleteTaskValidator;
import com.sunwayworld.emc.module.bp.project.project.validator.EmcProjectDeleteValidator;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjOrderService;
import com.sunwayworld.emc.module.bp.project.projscheme.service.PfProjSchemeService;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportService;
import com.sunwayworld.emc.module.erp.constant.ErpConstant;
import com.sunwayworld.emc.module.erp.service.ErpOrderService;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean.EmcBizCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.service.EmcBizCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.audit.constant.AuditConstant;
import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnParameterDTO;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.engine.CoreBpmnRuntimeSource;
import com.sunwayworld.module.sys.sequence.bean.CoreSequenceBean;
import com.sunwayworld.module.sys.sequence.bean.CoreSequenceParamDTO;
import com.sunwayworld.module.sys.sequence.service.CoreSequenceService;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-27
 */

@Repository
@GikamBean
public class EmcProjectChangeServiceImpl implements EmcProjectChangeService {

    @Autowired
    private EmcProjectChangeDao projectChangeDao;

    @Autowired
    @Lazy
    private EmcFolderChangeService folderChangeService;

    @Autowired
    private EmcOrderChangeService orderChangeService;

    @Autowired
    private EmcOrderItemGroupChangeService orderItemGroupChangeService;

    @Autowired
    @Lazy
    private EmcFolderService folderService;

    @Autowired
    private EmcOrderService orderService;

    @Autowired
    private EmcOrderItemGroupService orderItemGroupService;

    @Autowired
    private PfOrderTaskService orderTaskService;

    @Autowired
    private EmcBizCategoryService bizCategoryService;

    @Autowired
    private EmcProjSchemeChangeService projSchemeChangeService;

    @Autowired
    private EmcProjOrderChangeService projOrderChangeService;

    @Autowired
    private PfProjSchemeService projSchemeService;

    @Autowired
    private EmcProjOrderService projOrderService;

    @Autowired
    private EmcProjectDirectorService emcProjectDirectorService;

    @Autowired
    private CoreSequenceService sequenceService;

    @Autowired
    private ErpOrderService erpOrderService;

    @Autowired
    private EmcProjectService projectService;

    @Autowired
    private EmcReportService reportService;

    @Autowired
    private PfOrderTaskResultService orderTaskResultService;

    @Autowired
    private EmcTestMethodService testMethodService;

    @Autowired
    private EmcItemService itemService;

    @Autowired
    private EmcOrderItemService orderItemService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcProjectChangeDao getDao() {
        return projectChangeDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcProjectChangeBean projectChange = jsonWrapper.parseUnique(EmcProjectChangeBean.class);

        Long originalProjectId = projectChange.getId();
        projectChange.setId(ApplicationContextHelper.getNextIdentity());
        projectChange.setChangeNo(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        projectChange.setProcessStatus(null);
        projectChange.setStatus(EmcBizConstant.PROJECT_STATUS_CREATE);

        getDao().insert(projectChange);

        if (Constant.YES.equals(projectChange.getSample())) {
            List<EmcProjSchemeBean> projSchemeList = projSchemeService.getDao().selectListByOneColumnValue(originalProjectId, "projId");
            List<Long> projSchemeIdList = projSchemeList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList());
            List<EmcProjOrderBean> projOrderList = projOrderService.getDao().selectListByOneColumnValues(projSchemeIdList, "projSchemeId");

            List<EmcProjSchemeChangeBean> projSchemeChangeList = new ArrayList<>(projSchemeList.size());
            List<EmcProjOrderChangeBean> projOrderChangeList = new ArrayList<>(projOrderList.size());
            projSchemeList.forEach(projScheme -> {
                EmcProjSchemeChangeBean projSchemeChange = new EmcProjSchemeChangeBean();
                BeanUtils.copyProperties(projScheme, projSchemeChange, EmcConstant.CREATED_COLUMNS);
                projSchemeChange.setId(ApplicationContextHelper.getNextIdentity());
                projSchemeChange.setProjId(projectChange.getId());
                projSchemeChangeList.add(projSchemeChange);

                projOrderList.stream().filter(po -> ObjectUtils.equals(projScheme.getId(), po.getProjSchemeId())).forEach(po -> {
                    EmcProjOrderChangeBean projOrderChange = new EmcProjOrderChangeBean();
                    BeanUtils.copyProperties(po, projOrderChange, EmcConstant.CREATED_COLUMNS);
                    projOrderChange.setId(ApplicationContextHelper.getNextIdentity());
                    projOrderChange.setProjSchemeId(projSchemeChange.getId());
                    projOrderChangeList.add(projOrderChange);
                });
            });

            projSchemeChangeService.getDao().insert(projSchemeChangeList);
            projOrderChangeService.getDao().insert(projOrderChangeList);
        } else {
            List<EmcFolderBean> folderList = folderService.getDao().selectListByOneColumnValue(originalProjectId, "projId");
            List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValue(originalProjectId, "projId");
            List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());

            List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "orderId");

            List<EmcFolderChangeBean> folderChangeList = new ArrayList<>(folderList.size());
            List<EmcOrderChangeBean> orderChangeList = new ArrayList<>(orderList.size());
            List<EmcOrderItemGroupChangeBean> orderItemGroupChangeList = new ArrayList<>(orderItemGroupList.size());

            folderList.forEach(folder -> {
                EmcFolderChangeBean folderChange = new EmcFolderChangeBean();
                BeanUtils.copyProperties(folder, folderChange, EmcConstant.CREATED_COLUMNS);
                folderChange.setId(ApplicationContextHelper.getNextIdentity());
                folderChange.setProjId(projectChange.getId());
                folderChangeList.add(folderChange);

                orderList.stream().filter(order -> ObjectUtils.equals(folder.getId(), order.getFolderId())).forEach(order -> {
                    EmcOrderChangeBean orderChange = new EmcOrderChangeBean();
                    BeanUtils.copyProperties(order, orderChange, EmcConstant.CREATED_COLUMNS);
                    orderChange.setId(ApplicationContextHelper.getNextIdentity());
                    orderChange.setFolderId(folderChange.getId());
                    orderChange.setProjId(folderChange.getProjId());
                    orderChangeList.add(orderChange);

                    orderItemGroupList.stream().filter(oig -> ObjectUtils.equals(oig.getOrderId(), order.getId())).forEach(oig -> {
                        EmcOrderItemGroupChangeBean orderItemGroupChange = new EmcOrderItemGroupChangeBean();
                        BeanUtils.copyProperties(oig, orderItemGroupChange, EmcConstant.CREATED_COLUMNS);
                        orderItemGroupChange.setId(ApplicationContextHelper.getNextIdentity());
                        orderItemGroupChange.setOrderId(orderChange.getId());
                        orderItemGroupChangeList.add(orderItemGroupChange);
                    });
                });
            });

            folderChangeService.getDao().insert(folderChangeList);
            orderChangeService.getDao().insert(orderChangeList);
            orderItemGroupChangeService.getDao().insert(orderItemGroupChangeList);
        }

        return projectChange.getId();
    }

    /**
     * 复写核心删除方法，解决项目编号不连续问题
     */
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    @Audit(AuditConstant.DELETE)
    @ValidateDataWith(EmcProjectDeleteValidator.class)
    public void delete(RestJsonWrapperBean wrapper) {
        List<EmcProjectChangeBean> deleteProjectList = wrapper.parse(EmcProjectChangeBean.class);
        if (deleteProjectList.isEmpty()) {
            return;
        }

        List<Long> projectIdList = deleteProjectList.stream().map(EmcProjectChangeBean::getId).collect(Collectors.toList());
        getDao().deleteByIdList(projectIdList);

        List<EmcProjSchemeChangeBean> projSchemeList = projSchemeChangeService.getDao().selectListByOneColumnValues(projectIdList, "projId", Collections.singletonList("id"));
        List<Long> projSchemeIdList = projSchemeList.stream().map(EmcProjSchemeChangeBean::getId).collect(Collectors.toList());
        projSchemeChangeService.getDao().deleteByIdList(projSchemeIdList);

        List<EmcProjOrderChangeBean> projOrderList = projOrderChangeService.getDao().selectListByOneColumnValues(projSchemeIdList, "projSchemeId", Collections.singletonList("id"));
        List<Long> projOrderIdList = projOrderList.stream().map(EmcProjOrderChangeBean::getId).collect(Collectors.toList());
        projOrderChangeService.getDao().deleteByIdList(projOrderIdList);

        List<EmcFolderChangeBean> folderList = folderChangeService.getDao().selectListByOneColumnValues(projectIdList, "projId", Collections.singletonList("id"));
        List<Long> folderIdList = folderList.stream().map(EmcFolderChangeBean::getId).collect(Collectors.toList());
        folderChangeService.getDao().deleteByIdList(folderIdList);

        List<EmcOrderChangeBean> orderList = orderChangeService.getDao().selectListByOneColumnValues(projectIdList, "projId", Collections.singletonList("id"));
        List<Long> orderIdList = orderList.stream().map(EmcOrderChangeBean::getId).collect(Collectors.toList());
        orderChangeService.getDao().deleteByIdList(orderIdList);

        List<EmcOrderItemGroupChangeBean> orderItemGroupList = orderItemGroupChangeService.getDao().selectListByOneColumnValues(orderIdList, "orderId", Collections.singletonList("id"));
        List<Long> orderItemGroupIdList = orderItemGroupList.stream().map(EmcOrderItemGroupChangeBean::getId).collect(Collectors.toList());
        orderItemGroupChangeService.getDao().deleteByIdList(orderItemGroupIdList);
    }

    @Override
    public Page<EmcOrderChangeBean> selectOrderPaginationByProjectId(Long id, RestJsonWrapperBean wrapper) {
        return orderChangeService.selectPaginationByFilter(SearchFilter.instance().match("PROJID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    public Long insertOrder(Long id, RestJsonWrapperBean wrapper) {
        return orderChangeService.insert(id, wrapper);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id, RestJsonWrapperBean wrapper) {
        // 删除点位
        List<Long> orderIdList = wrapper.parse(EmcOrderChangeBean.class).stream().map(e -> e.getId()).collect(Collectors.toList());
        List<Long> folderIdList = orderChangeService.selectListByIds(orderIdList).stream().map(e -> e.getFolderId()).collect(Collectors.toList());
        // 删除样品及其子表
        orderChangeService.delete(wrapper);
        // 判断folder表下是否还有其它样品
        List<Long> delFolderIdList = new ArrayList<>();
        List<Long> afterDelFolderIdList = orderChangeService.getDao().selectListByOneColumnValues(folderIdList, "FOLDERID").stream().map(e -> e.getFolderId()).distinct().collect(Collectors.toList());
        folderIdList.stream().forEach(a -> {
            if (!afterDelFolderIdList.contains(a)) {
                delFolderIdList.add(a);
            }
        });

        folderChangeService.getDao().deleteByIdList(delFolderIdList);
    }

    @Override
    public Page<EmcOrderTaskBean> selectTaskPaginationByOrderId(Long orderId, RestJsonWrapperBean wrapper) {
        return orderTaskService.selectPaginationByFilter(SearchFilter.instance().match("ORDERID", orderId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    public void copeOrder(RestJsonWrapperBean jsonWrapper) {
        orderChangeService.copy(jsonWrapper);
    }

    @Override
    @Transactional
    public void copeQcOrder(RestJsonWrapperBean jsonWrapper) {
        orderChangeService.copyQc(jsonWrapper);
    }

    @Override
    public Page<EmcProjectChangeBean> selectSearchablePagination(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        parameter.setRawQueries();
        BeanPropertyDescriptor createdByOrgIdPropertyDescriptor = PersistableMetadataHelper.getCreatedByOrgIdPropertyDescriptor(EmcProjectChangeBean.class);
        if (createdByOrgIdPropertyDescriptor != null) {
            parameter.setOrgAuthority();
        }

        if (!wrapper.getAuthorityList().isEmpty()) {
            wrapper.getAuthorityList().forEach((a) -> {
                parameter.setAuthorityParameter(a);
            });
        }

        return this.selectPagination(parameter, rowBounds);
    }

    @Override
    public Long selectIdByKeyword(String keyword) {
        EmcProjectChangeBean noFilter = new EmcProjectChangeBean();
        noFilter.setProjNo(keyword);
        EmcProjectChangeBean project = getDao().selectOneIfPresent(noFilter);
        if (project == null) {
            EmcProjectChangeBean nameFilter = new EmcProjectChangeBean();
            nameFilter.setProjName(keyword);
            project = getDao().selectOneIfPresent(nameFilter);
        }

        if (project != null) {
            return project.getId();
        }
        return null;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_COMPLETE)
    @ValidateDataWith(EmcProjectChangeCompleteTaskValidator.class)
    public List<CoreBpmnInstanceStatusDTO<Long>> completeTask(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = completeTaskBasic(wrapper);
        List<Long> projectIdList = coreBpmnInstanceStatusDTOList.stream().map(CoreBpmnInstanceStatusDTO::getId).collect(Collectors.toList());
        List<EmcProjectChangeBean> projectChangeList = getDao().selectListByIds(projectIdList);

        // 审核通过后，对业务数据进行变更
        changeProject(projectChangeList);

        // 向ERP推送订单变更审核结果
        erpOrderService.confirmOrderChange(projectChangeList, false, wrapper.getParamValue("bpmn_comment"));

        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_START)
    @ValidateDataWith(EmcProjectCompleteTaskValidator.class)
    public List<CoreBpmnInstanceStatusDTO<Long>> startProcess(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = startProcessBasic(wrapper);
        List<Long> idList = coreBpmnInstanceStatusDTOList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcProjectChangeBean> projectList = selectListByFilter(SearchFilter.instance().match("ID", idList).filter(MatchPattern.OR));

        return coreBpmnInstanceStatusDTOList;
    }

    // 提取工作流方法
    @Override
    public List<CoreBpmnInstanceStatusDTO<Long>> startProcessBasic(RestJsonWrapperBean wrapper) {
        return EmcProjectChangeService.super.startProcess(wrapper);
    }

    // 提取工作流方法
    @Override
    public List<CoreBpmnInstanceStatusDTO<Long>> startProcessBasic(List<Long> projIdList, CoreBpmnParameterDTO parameter) {
        return EmcProjectChangeService.super.startProcess(projIdList, parameter);
    }

    @Override
    @Transactional
    public void changeProject(List<EmcProjectChangeBean> projectChangeList) {
        // 采样任务：
        // 1、同步方案，找出哪些是新增的，哪些是需要删除的；
        // 2、对比方案下的测试，找出哪些是新增的，哪些是需要删除的，哪些修改了方法；
        // 3、变更点位、样品、测试和结果数据
        List<EmcProjectChangeBean> samplingProjectList = projectChangeList.stream().filter(project -> Constant.YES.equals(project.getSample())).collect(Collectors.toList());
        changeSamplingProject(samplingProjectList);

        // 送样任务：
        // 1、同步样品，找出哪些样品是新增的；
        // 2、同步样品分析项组，找出哪些是新增的，哪些是需要删除的；
        // 3、 变更点位、样品、测试和结果数据
        List<EmcProjectChangeBean> nonSamplingProjectList = projectChangeList.stream().filter(project -> !Constant.YES.equals(project.getSample())).collect(Collectors.toList());
        changeNonSamplingProject(nonSamplingProjectList);
    }

    @Override
    public void syncNewOrder(List<EmcOrderBean> orderList) {
        List<EmcOrderBean> newOrderList = orderList.stream().filter(order -> StringUtils.equals(order.getExt$Item("syncStatus"), Constant.YES)).collect(Collectors.toList());
        if (newOrderList.isEmpty()) {
            return;
        }

        // 如果新增了样品类型，则向报告表增加一条数据
        List<EmcReportBean> reportList = reportService.getDao().selectListByOneColumnValue(newOrderList.get(0).getProjId(), "projId", Arrays.asList("id", "monitorTmplId"));
        List<String> reportMonitorTmplIdList = reportList.stream().filter(r -> !StringUtils.isEmpty(r.getMonitorTmplId())).map(EmcReportBean::getMonitorTmplId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> newOrderCategoryOrderList = newOrderList.stream().filter(order -> !reportMonitorTmplIdList.contains(order.getMonitorTmplId())).collect(Collectors.toList());

        if (!newOrderCategoryOrderList.isEmpty()) {
            List<EmcReportBean> newReportList = new ArrayList<>(newOrderCategoryOrderList.size());
            newOrderCategoryOrderList.forEach(order -> {
                EmcReportBean newReport = new EmcReportBean();
                newReport.setId(ApplicationContextHelper.getNextIdentity());
                newReport.setProjId(newOrderList.get(0).getProjId());
                newReport.setMonitorTmplId(order.getMonitorTmplId());
                newReport.setOrderCategory(order.getOrderCategory());
                newReport.setStatus("draft");
                newReport.setProcessStatus("draft");
                newReportList.add(newReport);
            });
            reportService.getDao().insert(newReportList);
        }

        List<Long> newOrderIdList = newOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(newOrderIdList, "orderId", Collections.singletonList("id"));

        orderTaskService.insertByOrder(newOrderList);

        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(newOrderIdList, "orderId", Collections.singletonList("id"));
        orderTaskList.forEach(task -> task.setStatus(EmcBizConstant.ORDERTASK_STATUS_PRELOGGED));
        orderTaskService.getDao().update(orderTaskList, "status");

        newOrderList.forEach(order -> order.setExt$Item("syncStatus", null));
        orderItemGroupList.forEach(group -> group.setExt$Item("syncStatus", null));

        orderService.getDao().update(newOrderList, "syncStatus");
        orderItemGroupService.getDao().update(orderItemGroupList, "syncStatus");
    }

    @Override
    public void syncNewTest(List<EmcOrderBean> orderList) {
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());

        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "orderId");
        List<EmcOrderItemGroupBean> newOrderItemGroupList = orderItemGroupList.stream().filter(group -> StringUtils.equals(group.getExt$Item("syncStatus"), Constant.YES)).collect(Collectors.toList());
        if (newOrderItemGroupList.isEmpty()) {
            return;
        }

        List<EmcOrderTaskBean> insertOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> insertOrderTaskResultList = new ArrayList<>();

        List<EmcOrderItemBean> orderItemList = orderItemService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");
        List<Long> uniqueItemIdList = orderItemList.stream().map(EmcOrderItemBean::getItemId).distinct().collect(Collectors.toList());
        List<EmcItemBean> itemList = itemService.getDao().selectListByOneColumnValues(uniqueItemIdList, "ID");

        List<Long> newOrderIdList = newOrderItemGroupList.stream().map(EmcOrderItemGroupBean::getOrderId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(newOrderIdList, "orderId", Arrays.asList("id", "orderId", "status"));

        List<String> defaultTaskStatusList = Arrays.asList("draft", "preLogged", "logged");

        Map<Long, List<EmcOrderItemGroupBean>> newOrderItemGroupMap = newOrderItemGroupList.stream().collect(Collectors.groupingBy(EmcOrderItemGroupBean::getOrderId));
        newOrderItemGroupMap.forEach((orderId, curGroupList) -> {
            EmcOrderBean curOrder = orderList.stream().filter(order -> ObjectUtils.equals(order.getId(), orderId)).findFirst().orElse(null);
            if (null == curOrder) {
                return;
            }

            // 当前样品下测试的状态
            List<String> taskStatusList = orderTaskList.stream().filter(task -> ObjectUtils.equals(task.getOrderId(), orderId)).map(EmcOrderTaskBean::getStatus).distinct().collect(Collectors.toList());
            List<Integer> statusIndexList = taskStatusList.stream().mapToInt(defaultTaskStatusList::indexOf).sorted().boxed().collect(Collectors.toList());
            String newTaskStatus = (statusIndexList.isEmpty() || statusIndexList.get(0) == -1) ? "logged" : defaultTaskStatusList.get(statusIndexList.get(0));

            ArrayList<EmcOrderItemGroupBean> uniqueGroupList = curGroupList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getTestId() + "#" + o.getMethodId()))), ArrayList::new));
            uniqueGroupList.forEach(orderItemGroup -> {

                List<EmcOrderItemGroupBean> testMethodOrderItemGroupList = curGroupList.stream().filter(e -> ObjectUtils.equals(orderItemGroup.getTestId(), e.getTestId())
                        && ObjectUtils.equals(orderItemGroup.getMethodId(), e.getMethodId())).collect(Collectors.toList());

                List<Long> testMethodOrderItemGroupIdList = testMethodOrderItemGroupList.stream().map(EmcOrderItemGroupBean::getId).collect(Collectors.toList());
                List<EmcOrderItemBean> filterOrderItemList = orderItemList.stream().filter(e -> testMethodOrderItemGroupIdList.contains(e.getOrderItemGroupId())).collect(Collectors.toList());
                List<Long> itemIdList = filterOrderItemList.stream().map(EmcOrderItemBean::getItemId).distinct().collect(Collectors.toList());
                List<EmcItemBean> filterItemList = itemList.stream().filter(e -> itemIdList.contains(e.getId()) && Constant.YES.equals(e.getActivatedFlag())).collect(Collectors.toList());

                // 由于是送样的样品类型，此处只插入分析类型
                List<String> analysisCategoryList = filterItemList.stream().map(EmcItemBean::getAnalysisCategory).distinct()
                        .filter(category -> StringUtils.equals(PfBizConstant.ANALYSISCATEGORY_ANALYSIS, category)).collect(Collectors.toList());
                analysisCategoryList.forEach(analysisCategory -> {
                    // 样品测试
                    EmcOrderTaskBean orderTask = new EmcOrderTaskBean();

                    BeanUtils.copyProperties(orderItemGroup, orderTask);

                    orderTask.setId(ApplicationContextHelper.getNextIdentity());
                    orderTask.setAnalysisType(analysisCategory);
                    orderTask.setFolderId(curOrder.getFolderId());
                    orderTask.setProjId(curOrder.getProjId());
                    orderTask.setMonitorTmplId(curOrder.getMonitorTmplId());
                    orderTask.setOrderCategory(curOrder.getOrderCategory());

                    orderTask.setEquiptAcquNo(curOrder.getOrderNo());
                    orderTask.setExt$Item("relatedTest", orderItemGroup.getExt$Item("relatedTest"));
                    orderTask.setStatus(newTaskStatus);

                    insertOrderTaskList.add(orderTask);

                    // 样品测试结果
                    List<EmcItemBean> analysisCategoryItemList = filterItemList.stream().filter(e -> ObjectUtils.equals(e.getAnalysisCategory(), analysisCategory)).collect(Collectors.toList());

                    insertOrderTaskResultList.addAll(orderTaskService.getInsertOrderTaskResultList(orderTask, analysisCategoryItemList, curOrder));
                });
            });
        });

        newOrderItemGroupList.forEach(group -> group.setExt$Item("syncStatus", null));

        orderItemGroupService.getDao().update(newOrderItemGroupList, "syncStatus");
        orderTaskService.getDao().insert(insertOrderTaskList);
        orderTaskResultService.getDao().insert(insertOrderTaskResultList);
    }

    @Override
    public void deleteSyncOrder(List<EmcOrderBean> orderList) {
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());

        // 删除点位数据
        List<Long> folderIdList = orderList.stream().map(EmcOrderBean::getFolderId).collect(Collectors.toList());
        folderService.getDao().deleteByIdList(folderIdList);

        // 删除样品数据
        getDao().deleteByIdList(orderIdList);

        // 删除样品分析项组、样品分析项数据
        List<EmcOrderItemGroupBean> deleteOrderItemGroupList = new ArrayList<>(orderIdList.size());
        List<EmcOrderItemBean> deleteOrderItemList = new ArrayList<>(orderIdList.size());
        orderIdList.forEach(orderId -> {
            EmcOrderItemGroupBean deleteOrderItemGroup = new EmcOrderItemGroupBean();
            deleteOrderItemGroup.setOrderId(orderId);
            deleteOrderItemGroupList.add(deleteOrderItemGroup);

            EmcOrderItemBean deleteOrderItem = new EmcOrderItemBean();
            deleteOrderItem.setOrderId(orderId);
            deleteOrderItemList.add(deleteOrderItem);
        });
        orderItemGroupService.getDao().deleteBy(deleteOrderItemGroupList, "orderId");
        orderItemService.getDao().deleteBy(deleteOrderItemList, "orderId");

        // 删除样品测试、样品测试结果数据，未同步的样品，表示是方案修改新增的，其他为之前已存在的样品
        List<EmcOrderBean> originalOrderList = orderList.stream().filter(order -> !StringUtils.equals(order.getExt$Item("syncStatus"), Constant.YES)).collect(Collectors.toList());
        List<Long> originalOrderIdList = originalOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());

        List<EmcOrderTaskBean> deleteOrderTaskList = new ArrayList<>(originalOrderIdList.size());
        List<EmcOrderTaskResultBean> deleteResultList = new ArrayList<>(originalOrderIdList.size());
        originalOrderIdList.forEach(orderId -> {
            EmcOrderTaskBean deleteOrderTask = new EmcOrderTaskBean();
            deleteOrderTask.setOrderId(orderId);
            deleteOrderTaskList.add(deleteOrderTask);

            EmcOrderTaskResultBean deleteResult = new EmcOrderTaskResultBean();
            deleteResult.setOrderId(orderId);
            deleteResultList.add(deleteResult);
        });

        orderTaskService.getDao().deleteBy(deleteOrderTaskList, "orderId");
        orderTaskResultService.getDao().deleteBy(deleteResultList, "orderId");
    }

    @Override
    public void deleteSyncTest(List<EmcOrderItemGroupBean> orderItemGroupList) {
        // 删除样品分析项组数据
        List<Long> orderItemGroupIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean::getId).collect(Collectors.toList());
        orderItemGroupService.getDao().deleteByIdList(orderItemGroupIdList);

        List<EmcOrderItemGroupBean> originalOrderItemGroupList = orderItemGroupList.stream().filter(group -> !StringUtils.equals(group.getExt$Item("syncStatus"), Constant.YES)).collect(Collectors.toList());

        List<EmcOrderItemBean> deleteOrderItemList = new ArrayList<>(originalOrderItemGroupList.size());
        List<EmcOrderTaskBean> deleteOrderTaskList = new ArrayList<>(originalOrderItemGroupList.size());
        List<EmcOrderTaskResultBean> deleteResultList = new ArrayList<>(originalOrderItemGroupList.size());

        originalOrderItemGroupList.forEach(group -> {
            EmcOrderItemBean deleteItem = new EmcOrderItemBean();
            deleteItem.setOrderId(group.getOrderId());
            deleteItem.setOrderItemGroupId(group.getId());
            deleteOrderItemList.add(deleteItem);

            EmcOrderTaskBean deleteOrderTask = new EmcOrderTaskBean();
            deleteOrderTask.setOrderId(group.getOrderId());
            deleteOrderTask.setTestMethodId(group.getTestMethodId());
            deleteOrderTaskList.add(deleteOrderTask);

            EmcOrderTaskResultBean deleteResult = new EmcOrderTaskResultBean();
            deleteResult.setOrderId(group.getOrderId());
            deleteResult.setTestMethodId(group.getTestMethodId());
            deleteResultList.add(deleteResult);
        });

        orderItemService.getDao().deleteBy(deleteOrderItemList, "orderId", "orderItemGroupId");
        orderTaskService.getDao().deleteBy(deleteOrderTaskList, "orderId", "testMethodId");
        orderTaskResultService.getDao().deleteBy(deleteResultList, "orderId", "testMethodId");
    }

    /**
     * 变更采样任务业务数据
     * @param samplingProjectList 采样任务
     */
    private void changeSamplingProject(List<EmcProjectChangeBean> samplingProjectList) {
        // 变更后的监测方案
        List<Long> samplingProjectIdList = samplingProjectList.stream().map(EmcProjectChangeBean::getId).collect(Collectors.toList());
        List<EmcProjSchemeChangeBean> projSchemeChangeList = projSchemeChangeService.getDao().selectListByOneColumnValues(samplingProjectIdList, "projId");
        List<Long> projSchemeChangeIdList = projSchemeChangeList.stream().map(EmcProjSchemeChangeBean::getId).collect(Collectors.toList());
        List<EmcProjOrderChangeBean> projOrderChangeList = projOrderChangeService.getDao().selectListByOneColumnValues(projSchemeChangeIdList, "projSchemeId");

        // 变更前的监测方案
        List<String> projNoList = samplingProjectList.stream().map(EmcProjectChangeBean::getProjNo).distinct().collect(Collectors.toList());
        List<EmcProjectBean> projectList = projectService.getDao().selectListByOneColumnValues(projNoList, "projNo", Arrays.asList("id", "projNo"));
        List<Long> projectIdList = projectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());
        Map<String, Long> projectNoIdMap = projectList.stream().collect(Collectors.toMap(EmcProjectBean::getProjNo, EmcProjectBean::getId));

        List<EmcProjSchemeBean> projSchemeList = projSchemeService.getDao().selectListByOneColumnValues(projectIdList, "projId");
        List<Long> projSchemeIdList = projSchemeList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList());
        List<EmcProjOrderBean> projOrderList = projOrderService.selectListByFilter(SearchFilter.instance().match("projSchemeId", projSchemeIdList).filter(MatchPattern.OR));

        List<EmcProjSchemeBean> insertSchemeList = new ArrayList<>();
        List<EmcProjOrderBean> insertProjOrderList = new ArrayList<>();
        List<EmcProjSchemeBean> deleteSchemeList = new ArrayList<>();
        List<EmcProjOrderBean> deleteProjOrderList = new ArrayList<>();

        samplingProjectList.forEach(pc -> {
            List<EmcProjSchemeChangeBean> curSchemeChangeList = projSchemeChangeList.stream().filter(sc -> ObjectUtils.equals(pc.getId(), sc.getProjId())).collect(Collectors.toList());
            List<Long> curSchemeChangeIdList = curSchemeChangeList.stream().map(EmcProjSchemeChangeBean::getId).collect(Collectors.toList());
            List<EmcProjOrderChangeBean> curProjOrderChangeList = projOrderChangeList.stream().filter(oc -> curSchemeChangeIdList.contains(oc.getProjSchemeId())).collect(Collectors.toList());

            Long projectId = projectNoIdMap.get(pc.getProjNo());
            List<EmcProjSchemeBean> curSchemeList = projSchemeList.stream().filter(ps -> ObjectUtils.equals(ps.getProjId(), projectId)).collect(Collectors.toList());
            List<Long> curSchemeIdList = curSchemeList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList());
            List<EmcProjOrderBean> curProjOrderList = projOrderList.stream().filter(po -> curSchemeIdList.contains(po.getProjSchemeId())).collect(Collectors.toList());

            // 找出新增、删除和无改变的监测方案
            List<EmcProjSchemeChangeBean> insertSchemeChangeList = curSchemeChangeList.stream().filter(scheme -> ErpConstant.INSERT_STATUS.equals(scheme.getChangeStatus())).collect(Collectors.toList());
            List<Long> insertSchemeChangeIdList = insertSchemeChangeList.stream().map(EmcProjSchemeChangeBean::getId).collect(Collectors.toList());
            List<EmcProjOrderChangeBean> insertProjOrderChangeList = curProjOrderChangeList.stream().filter(order -> insertSchemeChangeIdList.contains(order.getProjSchemeId())).collect(Collectors.toList());
            insertSchemeChangeList.forEach(schemeChange -> {
                List<EmcProjOrderChangeBean> curOrderChangeList = insertProjOrderChangeList.stream().filter(order -> ObjectUtils.equals(schemeChange.getId(), order.getProjSchemeId())).collect(Collectors.toList());
                EmcProjSchemeBean insertScheme = new EmcProjSchemeBean();
                BeanUtils.copyProperties(schemeChange, insertScheme);
                insertScheme.setId(ApplicationContextHelper.getNextIdentity());
                insertScheme.setProjId(projectId);
                insertScheme.setExt$Item("status", Constant.YES);
                insertSchemeList.add(insertScheme);

                curOrderChangeList.forEach(orderChange -> {
                    EmcProjOrderBean insertProjOrder = new EmcProjOrderBean();
                    BeanUtils.copyProperties(orderChange, insertProjOrder);
                    insertProjOrder.setId(ApplicationContextHelper.getNextIdentity());
                    insertProjOrder.setProjSchemeId(insertScheme.getId());
                    insertProjOrder.setExt$Item("status", Constant.YES);
                    insertProjOrderList.add(insertProjOrder);
                });
            });

            // 需要删除的监测方案
            List<EmcProjSchemeChangeBean> deleteSchemeChangeList = curSchemeChangeList.stream().filter(scheme -> ErpConstant.DELETE_STATUS.equals(scheme.getChangeStatus())).collect(Collectors.toList());
            List<String> deleteSchemeKeyList = deleteSchemeChangeList.stream().map(scheme -> String.join("$", scheme.getMonitorTmplId() + "", scheme.getFolderNo(), scheme.getErpGbId() + "")).collect(Collectors.toList());
            List<EmcProjSchemeBean> curDeleteSchemeList = curSchemeList.stream().filter(scheme -> deleteSchemeKeyList.contains(String.join("$", scheme.getMonitorTmplId() + "", scheme.getFolderNo(), scheme.getErpGbId() + ""))).collect(Collectors.toList());
            List<Long> curDeleteSchemeIdList = curDeleteSchemeList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList());
            List<EmcProjOrderBean> curDeleteProjOrderList = curProjOrderList.stream().filter(projOrder -> curDeleteSchemeIdList.contains(projOrder.getProjSchemeId())).collect(Collectors.toList());
            deleteSchemeList.addAll(curDeleteSchemeList);
            deleteProjOrderList.addAll(curDeleteProjOrderList);

            // 无更改的监测方案，主要判断方案下的测试是否有新增或删除
            List<EmcProjSchemeChangeBean> noChangeSchemeChangeList = curSchemeChangeList.stream().filter(scheme -> ErpConstant.NO_CHANGE_STATUS.equals(scheme.getChangeStatus())).collect(Collectors.toList());
            noChangeSchemeChangeList.forEach(schemeChange -> {
                String schemeKey = String.join("$", schemeChange.getMonitorTmplId() + "", schemeChange.getFolderNo(), schemeChange.getErpGbId() + "");
                EmcProjSchemeBean originalProjScheme = curSchemeList.stream().filter(scheme -> schemeKey.equals(String.join("$", scheme.getMonitorTmplId() + "", scheme.getFolderNo(), scheme.getErpGbId() + ""))).findFirst().orElseGet(EmcProjSchemeBean::new);
                List<EmcProjOrderBean> originalProjOrderList = curProjOrderList.stream().filter(order -> ObjectUtils.equals(originalProjScheme.getId(), order.getProjSchemeId())).collect(Collectors.toList());

                List<EmcProjOrderChangeBean> curOrderChangeList = curProjOrderChangeList.stream().filter(order -> ObjectUtils.equals(schemeChange.getId(), order.getProjSchemeId())).collect(Collectors.toList());
                // 处理新增的方案测试
                List<EmcProjOrderChangeBean> curInsertOrderChangeList = curOrderChangeList.stream().filter(orderChange -> ErpConstant.INSERT_STATUS.equals(orderChange.getChangeStatus())).collect(Collectors.toList());
                curInsertOrderChangeList.forEach(orderChange -> {
                    EmcProjOrderBean insertProjOrder = new EmcProjOrderBean();
                    BeanUtils.copyProperties(orderChange, insertProjOrder);
                    insertProjOrder.setId(ApplicationContextHelper.getNextIdentity());
                    insertProjOrder.setProjSchemeId(originalProjScheme.getId());
                    insertProjOrder.setExt$Item("status", Constant.YES);
                    insertProjOrderList.add(insertProjOrder);
                });

                // 处理删除的方案测试
                List<EmcProjOrderChangeBean> curDeleteOrderChangeList = curOrderChangeList.stream().filter(orderChange -> ErpConstant.DELETE_STATUS.equals(orderChange.getChangeStatus())).collect(Collectors.toList());
                List<String> curDeleteOrderChangeKeyList = curDeleteOrderChangeList.stream().map(order -> String.join("$", order.getTestId() + "", order.getItemGroupId() + "")).collect(Collectors.toList());
                List<EmcProjOrderBean> curDeleteOrderList = originalProjOrderList.stream().filter(order -> curDeleteOrderChangeKeyList.contains(String.join("$", order.getTestId() + "", order.getItemGroupId() + ""))).collect(Collectors.toList());
                deleteProjOrderList.addAll(curDeleteOrderList);
            });
        });

        // 删除方案对应的点位、样品、测试和结果
        projSchemeService.deleteSyncProjScheme(deleteSchemeList);

        // 删除方案下的测试对应的测试和结果
        List<Long> deleteSchemeIdList = deleteSchemeList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList());
        List<EmcProjOrderBean> otherDeleteProjOrderList = deleteProjOrderList.stream().filter(projOrder -> !deleteSchemeIdList.contains(projOrder.getProjSchemeId())).collect(Collectors.toList());
        List<Long> otherDeleteSchemeIdList = otherDeleteProjOrderList.stream().map(EmcProjOrderBean::getProjSchemeId).distinct().collect(Collectors.toList());
        List<EmcProjSchemeBean> otherDeleteSchemeList = projSchemeService.selectListByIds(otherDeleteSchemeIdList);
        projSchemeService.deleteSyncProjOrder(otherDeleteSchemeList, otherDeleteProjOrderList);

        // 删除方案和方案测试
        projSchemeService.getDao().deleteBy(deleteSchemeList, "id");
        projOrderService.getDao().deleteBy(deleteProjOrderList, "id");

        // 新增方案和方案测试
        projSchemeService.getDao().insert(insertSchemeList);
        projOrderService.getDao().insert(insertProjOrderList);

        // 同步新增方案和方案下的测试到点位、样品、测试和结果表
        projSchemeService.syncNewProjScheme(insertSchemeList);

        // 同步新增测试，其方案未改变
        List<Long> insertSchemeIdList = insertSchemeList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList());
        List<Long> otherInsertSchemeIdList = insertProjOrderList.stream().filter(projOrder -> !insertSchemeIdList.contains(projOrder.getProjSchemeId())).map(EmcProjOrderBean::getProjSchemeId).distinct().collect(Collectors.toList());
        List<EmcProjSchemeBean> otherSchemeList = projSchemeService.selectListByIds(otherInsertSchemeIdList);
        projSchemeService.syncNewProjOrder(otherSchemeList);

    }

    /**
     * 变更送样任务业务数据
     * @param nonSamplingProjectList 送样任务
     */
    private void changeNonSamplingProject(List<EmcProjectChangeBean> nonSamplingProjectList) {
        // 变更后的业务数据
        List<Long> projectChangeIdList = nonSamplingProjectList.stream().map(EmcProjectChangeBean::getId).collect(Collectors.toList());
        List<EmcOrderChangeBean> orderChangeList = orderChangeService.getDao().selectListByOneColumnValues(projectChangeIdList, "projId");
        List<Long> orderChangeIdList = orderChangeList.stream().map(EmcOrderChangeBean::getId).collect(Collectors.toList());
        List<EmcOrderItemGroupChangeBean> orderItemGroupChangeList = orderItemGroupChangeService.getDao().selectListByOneColumnValues(orderChangeIdList, "orderId");
        List<EmcFolderChangeBean> folderChangeList = folderChangeService.getDao().selectListByOneColumnValues(projectChangeIdList, "projId");

        // 变更前的业务数据
        List<String> projectNoList = nonSamplingProjectList.stream().map(EmcProjectChangeBean::getProjNo).collect(Collectors.toList());
        List<EmcProjectBean> projectList = projectService.getDao().selectListByOneColumnValues(projectNoList, "projNo", Arrays.asList("id", "projNo"));
        Map<String, Long> projectNoIdMap = projectList.stream().collect(Collectors.toMap(EmcProjectBean::getProjNo, EmcProjectBean::getId));
        List<Long> projectIdList = projectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(projectIdList, "projId");
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "orderId");
        List<Long> folderIdList = orderList.stream().map(EmcOrderBean::getFolderId).distinct().collect(Collectors.toList());
        List<EmcFolderBean> folderList = folderService.selectListByIds(folderIdList);

        // 定义增删数据对象
        List<EmcOrderBean> insertOrderList = new ArrayList<>();
        List<EmcFolderBean> insertFolderList = new ArrayList<>();
        List<EmcOrderItemGroupBean> insertOrderItemGroupList = new ArrayList<>();
        List<EmcFolderBean> deleteFolderList = new ArrayList<>();
        List<EmcOrderBean> deleteOrderList = new ArrayList<>();
        List<EmcOrderItemGroupBean> deleteOrderItemGroupList = new ArrayList<>();

        // 对比业务数据
        nonSamplingProjectList.forEach(project -> {
            // 样品数据对比，根据erpGbId进行对比，如果erpGbId一样，说明没有进行样品删减
            List<EmcOrderChangeBean> curOrderChangeList = orderChangeList.stream().filter(oc -> ObjectUtils.equals(project.getId(), oc.getProjId())).collect(Collectors.toList());
            List<Long> curOrderChangeIdList = curOrderChangeList.stream().map(EmcOrderChangeBean::getId).collect(Collectors.toList());
            List<EmcOrderItemGroupChangeBean> curOigcList = orderItemGroupChangeList.stream().filter(igc -> curOrderChangeIdList.contains(igc.getOrderId())).collect(Collectors.toList());

            Long originalProjectId = projectNoIdMap.get(project.getProjNo());
            List<EmcOrderBean> curOrderList = orderList.stream().filter(order -> ObjectUtils.equals(originalProjectId, order.getProjId())).collect(Collectors.toList());
            List<Long> curOrderIdList = curOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
            List<EmcOrderItemGroupBean> curOrderItemGroupList = orderItemGroupList.stream().filter(ig -> curOrderIdList.contains(ig.getOrderId())).collect(Collectors.toList());

            // 新增的样品
            List<EmcOrderChangeBean> insertOrderChangeList = curOrderChangeList.stream().filter(oc -> ErpConstant.INSERT_STATUS.equals(oc.getChangeStatus())).collect(Collectors.toList());
            insertOrderChangeList.forEach(orderChange -> {
                // 复制点位信息
                EmcFolderChangeBean folderChange = folderChangeList.stream().filter(fc -> ObjectUtils.equals(orderChange.getFolderId(), fc.getId())).findFirst().orElseGet(EmcFolderChangeBean::new);
                EmcFolderBean insertFolder = new EmcFolderBean();
                BeanUtils.copyProperties(folderChange, insertFolder);
                insertFolder.setId(ApplicationContextHelper.getNextIdentity());
                insertFolder.setProjId(originalProjectId);
                insertFolderList.add(insertFolder);

                // 复制样品信息
                EmcOrderBean insertOrder = new EmcOrderBean();
                BeanUtils.copyProperties(orderChange, insertOrder);
                insertOrder.setId(ApplicationContextHelper.getNextIdentity());
                insertOrder.setProjId(originalProjectId);
                insertOrder.setFolderId(insertFolder.getId());
                insertOrder.setExt$Item("syncStatus", Constant.YES);
                insertOrderList.add(insertOrder);

                // 复制样品分析项组信息
                List<EmcOrderItemGroupChangeBean> curInsertIgcList = curOigcList.stream().filter(igc -> ObjectUtils.equals(igc.getOrderId(), orderChange.getId())).collect(Collectors.toList());
                curInsertIgcList.forEach(igc -> {
                    EmcOrderItemGroupBean insertItemGroup = new EmcOrderItemGroupBean();
                    BeanUtils.copyProperties(igc, insertItemGroup);
                    insertItemGroup.setId(ApplicationContextHelper.getNextIdentity());
                    insertItemGroup.setOrderId(insertOrder.getId());
                    insertItemGroup.setExt$Item("syncStatus", Constant.YES);
                    insertOrderItemGroupList.add(insertItemGroup);
                });
            });

            // 需要删除的样品
            List<EmcOrderChangeBean> deleteOrderChangeList = curOrderChangeList.stream().filter(order -> ErpConstant.DELETE_STATUS.equals(order.getChangeStatus())).collect(Collectors.toList());
            List<String> deleteOrderKeyList = deleteOrderChangeList.stream().map(order -> String.join("$", order.getOrderCategory(), order.getOrderNo(), order.getErpGbId() + "")).collect(Collectors.toList());
            List<EmcOrderBean> curDeleteOrderList = curOrderList.stream().filter(order -> deleteOrderKeyList.contains(String.join("$", order.getOrderCategory(), order.getOrderNo(), order.getErpGbId() + ""))).collect(Collectors.toList());
            List<Long> curDeleteOrderIdList = curDeleteOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
            List<EmcOrderItemGroupBean> curDeleteOigList = curOrderItemGroupList.stream().filter(oig -> curDeleteOrderIdList.contains(oig.getOrderId())).collect(Collectors.toList());
            List<Long> curDeleteFolderIdList = curDeleteOrderList.stream().map(EmcOrderBean::getFolderId).collect(Collectors.toList());
            List<EmcFolderBean> curDeleteFolderList = folderList.stream().filter(folder -> curDeleteFolderIdList.contains(folder.getId())).collect(Collectors.toList());
            deleteFolderList.addAll(curDeleteFolderList);
            deleteOrderList.addAll(curDeleteOrderList);
            deleteOrderItemGroupList.addAll(curDeleteOigList);

            // 无变更的样品，需要比对样品下的分析项组是否有新增或者删除
            List<EmcOrderChangeBean> noChangeOrderChangeList = curOrderChangeList.stream().filter(order -> ErpConstant.NO_CHANGE_STATUS.equals(order.getChangeStatus())).collect(Collectors.toList());
            noChangeOrderChangeList.forEach(orderChange -> {
                String orderKey = String.join("$", orderChange.getOrderCategory(), orderChange.getOrderNo(), orderChange.getErpGbId() + "");
                EmcOrderBean originalOrder = curOrderList.stream().filter(order -> StringUtils.equals(orderKey, String.join("$", order.getOrderCategory(), order.getOrderNo(), order.getErpGbId() + ""))).findFirst().orElseGet(EmcOrderBean::new);
                List<EmcOrderItemGroupBean> originalOigList = curOrderItemGroupList.stream().filter(oig -> ObjectUtils.equals(originalOrder.getId(), oig.getOrderId())).collect(Collectors.toList());

                List<EmcOrderItemGroupChangeBean> curChangeOigList = curOigcList.stream().filter(oigc -> ObjectUtils.equals(orderChange.getId(), oigc.getOrderId())).collect(Collectors.toList());
                // 对于需要新增的样品测试
                List<EmcOrderItemGroupChangeBean> curInsertChangeOigList = curChangeOigList.stream().filter(oig -> ErpConstant.INSERT_STATUS.equals(oig.getChangeStatus())).collect(Collectors.toList());
                curInsertChangeOigList.forEach(oigc -> {
                    EmcOrderItemGroupBean insertItemGroup = new EmcOrderItemGroupBean();
                    BeanUtils.copyProperties(oigc, insertItemGroup);
                    insertItemGroup.setId(ApplicationContextHelper.getNextIdentity());
                    insertItemGroup.setOrderId(originalOrder.getId());
                    insertItemGroup.setExt$Item("syncStatus", Constant.YES);
                    insertOrderItemGroupList.add(insertItemGroup);
                });

                // 对于需要删除的样品测试
                List<EmcOrderItemGroupChangeBean> curDeleteChangeOigList = curChangeOigList.stream().filter(oig -> ErpConstant.DELETE_STATUS.equals(oig.getChangeStatus())).collect(Collectors.toList());
                List<String> curDeleteChangeOigKeyList = curDeleteChangeOigList.stream().map(oigc -> String.join("$", oigc.getTestMethodId() + "", oigc.getItemGroupId() + "")).collect(Collectors.toList());
                List<EmcOrderItemGroupBean> originalDeleteOigList = originalOigList.stream().filter(oigc -> curDeleteChangeOigKeyList.contains(String.join("$", oigc.getTestMethodId() + "", oigc.getItemGroupId() + ""))).collect(Collectors.toList());
                deleteOrderItemGroupList.addAll(originalDeleteOigList);
            });
        });

        // 删除的样品及分析项组
        deleteSyncOrder(deleteOrderList);

        // 样品未改变，只是删除了分析项组
        List<Long> deleteOrderIdList = deleteOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> otherDeleteOrderItemGroupList = deleteOrderItemGroupList.stream().filter(ig -> !deleteOrderIdList.contains(ig.getOrderId())).collect(Collectors.toList());
        deleteSyncTest(otherDeleteOrderItemGroupList);

        // 处理删除数据
        orderService.getDao().deleteBy(deleteOrderList, "id");
        folderService.getDao().deleteBy(deleteFolderList, "id");
        orderItemGroupService.getDao().deleteBy(deleteOrderItemGroupList, "id");

        // 处理新增数据
        orderService.getDao().insert(insertOrderList);
        folderService.getDao().insert(insertFolderList);
        orderItemGroupService.getDao().insert(insertOrderItemGroupList);

        // 新增的样品及分析项组
        syncNewOrder(insertOrderList);

        // 样品未改变，只是新增了分析项组
        List<Long> insertOrderIdList = insertOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> otherInsertOrderItemGroupList = insertOrderItemGroupList.stream().filter(ig -> !insertOrderIdList.contains(ig.getOrderId())).collect(Collectors.toList());
        List<Long> noChangeOrderIdList = otherInsertOrderItemGroupList.stream().map(EmcOrderItemGroupBean::getOrderId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> noChangeOrderList = orderService.selectListByIds(noChangeOrderIdList);
        syncNewTest(noChangeOrderList);
    }

    // 提取工作流方法
    @Override
    public List<CoreBpmnInstanceStatusDTO<Long>> completeTaskBasic(RestJsonWrapperBean wrapper) {
        return EmcProjectChangeService.super.completeTask(wrapper);
    }

    // 提取工作流方法
    @Override
    public List<CoreBpmnInstanceStatusDTO<Long>> undoBasic(RestJsonWrapperBean wrapper) {
        return EmcProjectChangeService.super.undo(wrapper);
    }

    @Override
    public Supplier<String> getDescSupplier(CoreBpmnRuntimeSource<EmcProjectChangeBean, Long> source) {
        return () -> source.getOldItem().getProjName();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_REJECT)
    public List<CoreBpmnInstanceStatusDTO<Long>> rejectTask(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = rejectTaskBasic(wrapper);
        List<Long> idList = coreBpmnInstanceStatusDTOList.stream().map(CoreBpmnInstanceStatusDTO::getId).collect(Collectors.toList());

        EmcProjectChangeBean updateProject = new EmcProjectChangeBean();
        updateProject.setStatus(EmcBizConstant.PROJECT_STATUS_CREATE);

        getDao().updateByIds(updateProject, idList, "STATUS");
        ProjectNodeUtils.updateNodes(idList);

        // 向ERP推送订单变更审核不通过结果
        List<EmcProjectChangeBean> projectChangeList = getDao().selectListByIds(idList);
        erpOrderService.confirmOrderChange(projectChangeList, true, wrapper.getParamValue("bpmn_comment"));

        return coreBpmnInstanceStatusDTOList;
    }

    // 提取工作流方法
    @Override
    public List<CoreBpmnInstanceStatusDTO<Long>> rejectTaskBasic(RestJsonWrapperBean wrapper) {
        return EmcProjectChangeService.super.rejectTask(wrapper);
    }

    @Override
    public Page<EmcProjectDirectorBean> selectDirectorPaginationByProjectId(Long id, RestJsonWrapperBean wrapper) {
        return emcProjectDirectorService.selectPaginationByFilter(SearchFilter.instance().match("PROJECTID", id).filter(MatchPattern.SEQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertDirector(RestJsonWrapperBean wrapper) {
        emcProjectDirectorService.insert(wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteDirector(RestJsonWrapperBean wrapper) {
        emcProjectDirectorService.delete(wrapper);
    }

    // ---------------------------------------------------------------------
    // 私有方法
    // ---------------------------------------------------------------------
    // 获取项目编号
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String getSequenceNo(String bizCategoryNo) {
        String sequenceId = getDao().getTable() + LocalDate.now().getYear();
        GikamConcurrentLocker.block(sequenceId);
        CoreSequenceBean sequence = sequenceService.getDao().selectByIdIfPresent(sequenceId);
        if (sequence == null) {
            CoreSequenceBean insertSequence = new CoreSequenceBean();
            insertSequence.setId(sequenceId);
            insertSequence.setPattern("[nnn]");

            List<EmcBizCategoryBean> allBizCategory = bizCategoryService.selectAll();
            List<Map<String, String>> paramList = new ArrayList<>();
            allBizCategory.forEach(e -> {
                Map<String, String> paramMap = new HashMap<String, String>();
                paramMap.put("key", e.getBizCategoryId());
                if (ObjectUtils.equals(bizCategoryNo, e.getBizCategoryId())) {
                    paramMap.put("n", "001");
                } else {
                    paramMap.put("n", "000");
                }
                paramList.add(paramMap);
            });

            String jsonParam = JSONObject.toJSONString(paramList);
            insertSequence.setJsonParam(jsonParam);
            sequenceService.getDao().insert(insertSequence);
            return "001";
        } else {
            return ApplicationContextHelper.getNextSequence(sequenceId, bizCategoryNo);
        }
    }

    @Override
    public EmcProjectChangeBean selectProjectDetail(Long id, RestJsonWrapperBean wrapper) {
        return selectFirstByFilter(SearchFilter.instance().match("ID", id).filter(MatchPattern.EQ), wrapper);
    }

    private List<String> getSequenceNo(int year, String key, int qty) {
        String tableName = getDao().getTable();
        String id = tableName + "_" + year;
        CoreSequenceBean sequence = sequenceService.getDao().selectByIdIfPresent(id);
        if (sequence == null) {
            List<String> sequenceList = null;
            CoreSequenceBean basicSequence = sequenceService.getDao().selectByIdIfPresent(tableName);
            if (basicSequence != null) {
                sequence = new CoreSequenceBean();
                sequence.setId(id);
                sequence.setPattern(basicSequence.getPattern());
                sequenceList = ApplicationContextHelper.getNextSequenceList(tableName, key, qty);

                List<CoreSequenceParamDTO> paramList = new ArrayList<>();
                paramList.add(getNewSequenceParam(basicSequence.getPattern(), key, qty));
                sequence.setJsonParam(JSON.toJSONString(paramList));

                basicSequence.setJsonParam(null);
                sequenceService.getDao().insert(sequence);
                sequenceService.getDao().update(basicSequence, "JSONPARAM");
            }

            return sequenceList;
        }

        return ApplicationContextHelper.getNextSequenceList(id, key, qty);
    }

    private CoreSequenceParamDTO getNewSequenceParam(String pattern, String key, int qty) {
        CoreSequenceParamDTO param = new CoreSequenceParamDTO();

        List<String> substringList = StringUtils.substringsBetween(pattern, "[", "]");

        substringList.forEach(s -> {
            if ("key".equals(s)) {
                param.setKey(key);
            } else if ("yyyy".equals(s)) {
                param.setYyyy(String.format("%04d", LocalDate.now().getYear()));
            } else if ("yy".equals(s)) {
                param.setYy(String.format("%02d", LocalDate.now().getYear() % 100));
            } else if ("mm".equals(s)) {
                param.setMm(String.format("%02d", LocalDate.now().getMonthValue()));
            } else if ("dd".equals(s)) {
                param.setDd(String.format("%02d", LocalDate.now().getDayOfMonth()));
            } else if (StringUtils.repeatOf(s, "n")) {
                param.setN(qty);
            }
        });

        return param;
    }
}
