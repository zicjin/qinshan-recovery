package com.sunwayworld.emc.module.bp.project.project.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.cloud.module.fr.report.service.CoreReportConfigLineService;
import com.sunwayworld.cloud.module.schedule.schedule.service.CoreScheduleManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
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
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectDirectorBean;
import com.sunwayworld.emc.module.bp.project.project.dao.EmcProjectDao;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectDirectorService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.project.project.validator.EmcProjectCompleteTaskValidator;
import com.sunwayworld.emc.module.bp.project.project.validator.EmcProjectDeleteValidator;
import com.sunwayworld.emc.module.bp.project.project.validator.EmcUndoProjectValidator;
import com.sunwayworld.emc.module.bp.project.projectqc.service.EmcProjectQcService;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjOrderService;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjSchemeService;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportService;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean.EmcBizCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.service.EmcBizCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcClientBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcClientService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.audit.constant.AuditConstant;
import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.exception.CronException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnParameterDTO;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CronUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnCommentBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.engine.CoreBpmnRuntimeSource;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnCommentService;
import com.sunwayworld.module.sys.metadata.service.CoreTableService;
import com.sunwayworld.module.sys.sequence.bean.CoreSequenceBean;
import com.sunwayworld.module.sys.sequence.bean.CoreSequenceParamDTO;
import com.sunwayworld.module.sys.sequence.service.CoreSequenceService;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-27
 */

@Repository
@GikamBean
public class EmcProjectServiceImpl implements EmcProjectService {
    @Autowired
    private EmcProjectDao projectDao;

    @Autowired
    @Lazy
    private EmcFolderService folderService;

    @Autowired
    private EmcOrderService orderService;
    
    @Autowired
    private CoreReportConfigLineService reportConfigLineService;
    
    @Autowired
    private EmcProjectQcService projectQcService;

    @Autowired
    private EmcOrderItemGroupService orderItemGroupService;

    @Autowired
    private EmcOrderItemService orderItemService;

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Autowired
    private EmcBizCategoryService bizCategoryService;

    @Autowired
    private EmcProjSchemeService projSchemeService;

    @Autowired
    private EmcProjOrderService projOrderService;

    @Autowired
    private EmcSampleService sampleService;

    @Autowired
    private EmcReportService reportService;

    @Autowired
    private EmcAuditRecordService auditRecordService;

    @Autowired
    private EmcClientService clientService;

    @Autowired
    private CoreBpmnCommentService coreBpmnCommentService;

    @Autowired
    private CoreScheduleManager scheduleManager;

    @Autowired
    private EmcProjectDirectorService emcProjectDirectorService;

    @Autowired
    private CoreSequenceService sequenceService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcProjectDao getDao() {
        return projectDao;
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
        List<EmcProjectBean> deleteList = wrapper.parse(EmcProjectBean.class);
        if (deleteList.isEmpty()) {
            return;
        }

        CoreTableService tableService = ApplicationContextHelper.getBean(CoreTableService.class);

        deleteList.forEach(t -> tableService.deleteCascade(PersistableMetadataHelper.getTableName(EmcProjectBean.class), t.getId()));

        // 删除送样任务生成的点位数据，T_EMC_FOLDER
        List<EmcFolderBean> filterFolderList = deleteList.stream().map(p -> {
            EmcFolderBean folder = new EmcFolderBean();
            folder.setProjId(p.getId());
            return folder;
        }).collect(Collectors.toList());
        folderService.getDao().deleteBy(filterFolderList, "PROJID");

        // 删除送样样品数据及关联表数据，涉及T_EMC_ORDER、T_EMC_ORDER_ITEM、T_EMC_ORDER_ITEM_GROUP
        List<Long> projectIdList = deleteList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());
        List<EmcOrderBean> deleteOrderList = orderService.getDao().selectListByOneColumnValues(projectIdList, "PROJID", Collections.singletonList("ID"));
        deleteOrderList.forEach(t -> tableService.deleteCascade(PersistableMetadataHelper.getTableName(EmcOrderBean.class), t.getId()));
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcProjectBean emcProject = jsonWrapper.parseUnique(EmcProjectBean.class);
        emcProject.setId(ApplicationContextHelper.getNextIdentity());
        emcProject.setRegistDate(LocalDateTime.now());
        emcProject.setExt$Item("qualitycontrol", Constant.NO);
        emcProject.setExt$Item("detect", Constant.YES);
        emcProject.setExt$Item("subpackage", Constant.NO);
        emcProject.setUrgent(Constant.NO);
        //赋值所属单位信息
        CoreUserBean user = new CoreUserBean();
        user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        String parentOrgName = user.getExt$Item("parentorgname");
        emcProject.setOrgId(parentOrgId);
        emcProject.setOrgName(parentOrgName);

        EmcBizCategoryBean bizCategory = bizCategoryService.selectById(emcProject.getBizCategoryId());
        // 设置受检单位默认字段
        Long testOrgId = bizCategory.getTestOrgId();
        if (testOrgId != null) {
            EmcClientBean testClient = clientService.getDao().selectById(testOrgId);
            emcProject.setTestOrgId(testClient.getClientNo());
            emcProject.setTestOrgName(testClient.getClientName());
            emcProject.setTestOrgAddress(testClient.getAddress());
            emcProject.setTestOrgPhone(testClient.getPhone());
        }
        // 设置委托单位默认字段
        Long entrustOrgId = bizCategory.getEntrustOrgId();
        if (entrustOrgId != null) {
            EmcClientBean entrustClient = clientService.getDao().selectById(entrustOrgId);
            emcProject.setEntrustOrgId(entrustClient.getClientNo());
            emcProject.setEntrustOrgName(entrustClient.getClientName());
            emcProject.setEntrustOrgAddress(entrustClient.getAddress());
            emcProject.setEntrustOrgPhone(entrustClient.getPhone());
        }

        // 设置默认监测方案报表模板ID
        List<CoreReportConfigLineBean> reportConfigLineList = reportConfigLineService.getDao().selectListByOneColumnValue("rasproject", "REPORTCONFIGID");
        reportConfigLineList.stream().filter(line -> Constant.YES.equals(line.getActivatedFlag()) && StringUtils.contains(line.getReportName(), EmcConstant.SCHEME_REPORT_KEYWORD))
                .findFirst().ifPresent(line -> emcProject.setSchemeReportLineId(line.getId() + ""));

        LocalDateTime taskDate = emcProject.getTaskDate() == null ? LocalDateTime.now() : emcProject.getTaskDate();
        String taskDateStr = DateTimeUtils.formatLocalDateTime(taskDate, "yy");
        String sequenceNo = getSequenceNo(taskDate.getYear(), bizCategory.getBizCategoryId());

        emcProject.setProjNo(taskDateStr + sequenceNo);
        emcProject.setReportOrgId(bizCategory.getReportEditOrgId());
        emcProject.setBizCategoryCode(bizCategory.getBizCategoryId());
        getDao().insert(emcProject);

        return emcProject.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copy(RestJsonWrapperBean wrapper) {
        List<EmcProjectBean> copyEmcProjectBeanList = wrapper.parse(EmcProjectBean.class);
        if (copyEmcProjectBeanList.isEmpty()) {
            return;
        }
        EmcProjectBean emcProjectBean = copyEmcProjectBeanList.get(0);
        String copyQtyStr = emcProjectBean.getExt$Item("copyqty");
        if (StringUtils.isEmpty(copyQtyStr) || !NumberUtils.isNumber(copyQtyStr)) {
            return;
        }
        //查询当前用户的所属单位信息
        CoreUserBean user = new CoreUserBean();
        user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        String parentOrgName = user.getExt$Item("parentorgname");
        
        int copyQty = Integer.parseInt(copyQtyStr);
        int projectQty = copyEmcProjectBeanList.size();
        List<EmcProjectBean> oldEmcProjectBeanList = getDao().selectListByIds(copyEmcProjectBeanList.stream().map(EmcProjectBean::getId).collect(Collectors.toList()));
        // 拷贝列表项属性
        oldEmcProjectBeanList.stream().forEach(m -> {
            copyEmcProjectBeanList.stream().filter(c -> ObjectUtils.equals(c.getId(), m.getId())).forEach(t -> {
                m.setBizCategoryId(t.getBizCategoryId());
                m.setTaskDate(t.getTaskDate());
                m.setSample(t.getSample());
            });
        });
        // 查询业务编码
        String bizCategoryCode = bizCategoryService.selectById(copyEmcProjectBeanList.get(0).getBizCategoryId()).getBizCategoryId();
        // 批量获取项目编号，数量为项目数 * 复制份数
        List<String> newProjectNoList = new ArrayList<>();
        String sequenceNo = getSequenceNo(bizCategoryCode);
        if (projectQty * copyQty > 1) {
            newProjectNoList = ApplicationContextHelper.getNextSequenceList(getDao().getTable() + LocalDate.now().getYear(), bizCategoryCode, projectQty * copyQty - 1);
            newProjectNoList.add(0, sequenceNo);
        } else {
            newProjectNoList.add(sequenceNo);
        }
        LocalDateTime taskDate = emcProjectBean.getTaskDate() == null ? LocalDateTime.now() : emcProjectBean.getTaskDate();
        String taskDateStr = DateTimeUtils.formatLocalDateTime(taskDate, "yy");
        newProjectNoList = newProjectNoList.stream().map(r -> taskDateStr + r).collect(Collectors.toList());

        // 采样业务项目
        List<EmcProjectBean> emcSampleProjectList = oldEmcProjectBeanList.stream().filter(m -> ObjectUtils.equals(m.getSample(), "1")).collect(Collectors.toList());
        if (!emcSampleProjectList.isEmpty()) {
            List<EmcProjectBean> newEmcProjectBeanList = new ArrayList<>();
            List<EmcProjSchemeBean> newEmcProjSchemeBeanList = new ArrayList<>();
            List<EmcProjOrderBean> newEmcProjOrderBeanList = new ArrayList<>();

            List<EmcProjSchemeBean> emcProjSchemeBeanList = projSchemeService.getDao()
                    .selectListByOneColumnValues(emcSampleProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList()), "PROJID");
            List<EmcProjOrderBean> emcProjOrderBeanList = projOrderService.getDao()
                    .selectListByOneColumnValues(emcProjSchemeBeanList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList()), "PROJSCHEMEID");

            // 开始复制项目
            for (int i = 0; i < emcSampleProjectList.size(); i++) {
                EmcProjectBean c = emcSampleProjectList.get(i);
                List<EmcProjSchemeBean> schemeBeanList = emcProjSchemeBeanList.stream().filter(e -> ObjectUtils.equals(e.getProjId(), c.getId())).collect(Collectors.toList());
                for (int j = 0; j < copyQty; j++) {
                    EmcProjectBean projectBean = new EmcProjectBean();
                    BeanUtils.copyProperties(c, projectBean);
                    projectBean.setProcessStatus(EmcBizConstant.REPORT_STATUS_DRAFT);
                    long newProjectId = ApplicationContextHelper.getNextIdentity();
                    LocalDateTime now = LocalDateTime.now();
                    projectBean.setId(newProjectId);
                    String newProjectNo = bizCategoryCode + newProjectNoList.get(i * copyQty + j);
                    projectBean.setProjNo(newProjectNo);
                    projectBean.setRegistDate(now);
                    projectBean.setAcceptDate(now);
                    projectBean.setCreatedById(LocalContextHelper.getLoginUserId());
                    projectBean.setCreatedByName(LocalContextHelper.getLoginUserName());
                    projectBean.setCreatedTime(LocalDateTime.now());
                    projectBean.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
                    projectBean.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
                    projectBean.setSource("");
                    projectBean.setStatus(EmcBizConstant.PROJECT_STATUS_CREATE);
                    //赋值所属单位信息                    
                    projectBean.setOrgId(parentOrgId);
                    projectBean.setOrgName(parentOrgName);
                    
                    newEmcProjectBeanList.add(projectBean);

                    int qty = schemeBeanList.size();
                    List<String> folderNoList = folderService.generateFolderNosByProjId(newProjectId, newProjectNo, qty);
                    // 复制项目对应的方案
                    for (int p = 0; p < qty; p++) {
                        EmcProjSchemeBean schemeBean = new EmcProjSchemeBean();
                        EmcProjSchemeBean oldschemeBean = schemeBeanList.get(p);
                        BeanUtils.copyProperties(oldschemeBean, schemeBean);
                        schemeBean.setProjId(newProjectId);
                        schemeBean.setFolderNo(folderNoList.get(p));
                        long newSchemeId = ApplicationContextHelper.getNextIdentity();
                        emcProjOrderBeanList.stream().filter(m -> ObjectUtils.equals(oldschemeBean.getId(), m.getProjSchemeId())).forEach(k -> {
                            EmcProjOrderBean orderBean = new EmcProjOrderBean();
                            BeanUtils.copyProperties(k, orderBean);
                            orderBean.setId(ApplicationContextHelper.getNextIdentity());
                            orderBean.setProjSchemeId(newSchemeId);
                            newEmcProjOrderBeanList.add(orderBean);
                        });
                        schemeBean.setId(newSchemeId);
                        newEmcProjSchemeBeanList.add(schemeBean);
                    }
                }
            }
            this.getDao().insert(newEmcProjectBeanList);
            projSchemeService.getDao().insert(newEmcProjSchemeBeanList);
            projOrderService.getDao().insert(newEmcProjOrderBeanList);
        }

        // 送样业务项目
        List<EmcProjectBean> emcOrderProjectList = oldEmcProjectBeanList.stream().filter(m -> ObjectUtils.equals(m.getSample(), "0")).collect(Collectors.toList());
        if (!emcOrderProjectList.isEmpty()) {
            // 查询项目对应的所有样品
            List<EmcOrderBean> emcOrderBeanList = orderService.getDao().selectListByOneColumnValues(emcOrderProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList()), "PROJID");
            // 查询点位列表
            List<EmcFolderBean> emcFolderBeanList = folderService.getDao()
                    .selectListByIds(emcOrderBeanList.stream().filter(r -> r.getFolderId() != null).map(EmcOrderBean::getFolderId).collect(Collectors.toList()));
            if (emcFolderBeanList.isEmpty()) {
                // 无点位信息，不进行复制，此数据有问题
                return;
            }

            List<EmcProjectBean> newEmcProjectBeanList = new ArrayList<>();
            List<Long> emcOrderIdList = emcOrderBeanList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
            List<EmcOrderItemGroupBean> emcOrderItemGroupBeanList = orderItemGroupService.getDao().selectListByOneColumnValues(emcOrderIdList, "ORDERID");
            List<EmcOrderItemBean> emcOrderItemBeanList = orderItemService.getDao().selectListByOneColumnValues(emcOrderIdList, "ORDERID");

            List<EmcFolderBean> newEmcFolderBeanList = new ArrayList<>();
            List<EmcOrderBean> newEmcOrderBeanList = new ArrayList<>();
            List<EmcOrderItemGroupBean> newEmcOrderItemGroupBeanList = new ArrayList<>();
            List<EmcOrderItemBean> newEmcOrderItemBeanList = new ArrayList<>();

            // 开始复制项目
            for (int i = 0; i < emcOrderProjectList.size(); i++) {
                EmcProjectBean c = emcOrderProjectList.get(i);
                // 当前项目需要复制的点位列表
                List<EmcFolderBean> folderBeans = emcFolderBeanList.stream().filter(e -> ObjectUtils.equals(e.getProjId(), c.getId())).collect(Collectors.toList());

                if (folderBeans.isEmpty()) {
                    // 无点位信息，不进行复制，此数据有问题
                    continue;
                }
                for (int j = 0; j < copyQty; j++) {
                    EmcProjectBean projectBean = new EmcProjectBean();
                    BeanUtils.copyProperties(c, projectBean);
                    projectBean.setProcessStatus(EmcBizConstant.REPORT_STATUS_DRAFT);
                    long newProjectId = ApplicationContextHelper.getNextIdentity();
                    LocalDateTime now = LocalDateTime.now();
                    projectBean.setId(newProjectId);
                    String newProjectNo = bizCategoryCode + newProjectNoList.get(emcSampleProjectList.size() * copyQty + i * copyQty + j);
                    projectBean.setProjNo(newProjectNo);
                    projectBean.setRegistDate(now);
                    projectBean.setAcceptDate(now);
                    projectBean.setCreatedById(LocalContextHelper.getLoginUserId());
                    projectBean.setCreatedByName(LocalContextHelper.getLoginUserName());
                    projectBean.setCreatedTime(LocalDateTime.now());
                    projectBean.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
                    projectBean.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
                    projectBean.setStatus(EmcBizConstant.PROJECT_STATUS_CREATE);
                    //赋值所属单位信息                    
                    projectBean.setOrgId(parentOrgId);
                    projectBean.setOrgName(parentOrgName);
                    newEmcProjectBeanList.add(projectBean);

                    // 批量生成新的点位编号
                    int folderSize = folderBeans.size();
                    List<String> newFolderNoList = folderService.generateFolderNosByProjId(newProjectId, newProjectNo, folderSize);

                    for (int k = 0; k < folderSize; k++) {

                        EmcFolderBean f = folderBeans.get(k);
                        EmcFolderBean newFolder = new EmcFolderBean();
                        BeanUtils.copyProperties(f, newFolder);
                        Long newFolderId = ApplicationContextHelper.getNextIdentity();
                        String newFolderNo = newFolderNoList.get(k);
                        newFolder.setId(newFolderId);
                        newFolder.setFolderNo(newFolderNo);
                        newFolder.setProjId(newProjectId);
                        newEmcFolderBeanList.add(newFolder);

                        // 当前项目需要复制的样品列表
                        List<EmcOrderBean> orderBeans = emcOrderBeanList.stream().filter(e -> ObjectUtils.equals(e.getFolderId(), f.getId())).collect(Collectors.toList());
                        for (int m = 0; m < orderBeans.size(); m++) {

                            EmcOrderBean t = orderBeans.get(m);
                            EmcOrderBean newOrderBean = new EmcOrderBean();
                            BeanUtils.copyProperties(t, newOrderBean);
                            newOrderBean.setOrderNo(newFolderNo + String.format("%03d", m + 1));
                            newOrderBean.setLabelNo(newOrderBean.getOrderNo());
                            Long newOrderId = ApplicationContextHelper.getNextIdentity();
                            newOrderBean.setId(newOrderId);
                            newOrderBean.setFolderId(newFolderId);
                            newOrderBean.setProjId(newProjectId);
                            newOrderBean.setSampleId(null);
                            newEmcOrderBeanList.add(newOrderBean);

                            emcOrderItemGroupBeanList.stream().filter(o -> ObjectUtils.equals(o.getOrderId(), t.getId())).forEach(q -> {
                                EmcOrderItemGroupBean itemGroupBean = new EmcOrderItemGroupBean();
                                BeanUtils.copyProperties(q, itemGroupBean);
                                itemGroupBean.setOrderId(newOrderId);
                                Long newItemGroupId = ApplicationContextHelper.getNextIdentity();
                                itemGroupBean.setId(newItemGroupId);
                                newEmcOrderItemGroupBeanList.add(itemGroupBean);

                                emcOrderItemBeanList.stream().filter(o -> ObjectUtils.equals(o.getOrderId(), t.getId())).forEach(p -> {
                                    EmcOrderItemBean itemBean = new EmcOrderItemBean();
                                    BeanUtils.copyProperties(p, itemBean);
                                    itemBean.setOrderId(newOrderId);
                                    itemBean.setOrderItemGroupId(newItemGroupId);
                                    itemBean.setId(ApplicationContextHelper.getNextIdentity());
                                    newEmcOrderItemBeanList.add(itemBean);
                                });
                            });
                        }
                    }
                }
            }
            this.getDao().insert(newEmcProjectBeanList);
            folderService.getDao().insert(newEmcFolderBeanList);
            orderService.getDao().insert(newEmcOrderBeanList);
            orderItemGroupService.getDao().insert(newEmcOrderItemGroupBeanList);
            orderItemService.getDao().insert(newEmcOrderItemBeanList);
        }
    }

    @Override
    public Page<EmcOrderBean> selectOrderPaginationByProjectId(Long id, RestJsonWrapperBean wrapper) {
        return orderService.selectPaginationByFilter(SearchFilter.instance().match("PROJID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    public Long insertOrder(Long id, RestJsonWrapperBean wrapper) {
        return orderService.insert(id, wrapper);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id, RestJsonWrapperBean wrapper) {
        // 删除点位
        List<Long> orderIdList = wrapper.parse(EmcOrderBean.class).stream().map(e -> e.getId()).collect(Collectors.toList());
        List<Long> folderIdList = orderService.selectListByIds(orderIdList).stream().map(e -> e.getFolderId()).collect(Collectors.toList());
        // 删除样品及其子表
        orderService.delete(wrapper);
        // 判断folder表下是否还有其它样品
        List<Long> delFolderIdList = new ArrayList<>();
        List<Long> afterDelFolderIdList = orderService.getDao().selectListByOneColumnValues(folderIdList, "FOLDERID").stream().map(e -> e.getFolderId()).distinct().collect(Collectors.toList());
        folderIdList.stream().forEach(a -> {
            if (!afterDelFolderIdList.contains(a)) {
                delFolderIdList.add(a);
            }
        });

        folderService.getDao().deleteByIdList(delFolderIdList);
    }

    @Override
    public Page<EmcOrderTaskBean> selectTaskPaginationByOrderId(Long orderId, RestJsonWrapperBean wrapper) {
        return orderTaskService.selectPaginationByFilter(SearchFilter.instance().match("ORDERID", orderId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    public void copeOrder(RestJsonWrapperBean jsonWrapper) {
        orderService.copy(jsonWrapper);
    }

    @Override
    @Transactional
    public void copeQcOrder(RestJsonWrapperBean jsonWrapper) {
        orderService.copyQc(jsonWrapper);
    }

    @Override
    public Page<EmcProjectBean> selectSearchablePagination(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        parameter.setRawQueries();
        BeanPropertyDescriptor createdByOrgIdPropertyDescriptor = PersistableMetadataHelper.getCreatedByOrgIdPropertyDescriptor(EmcProjectBean.class);
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
        EmcProjectBean noFilter = new EmcProjectBean();
        noFilter.setProjNo(keyword);
        EmcProjectBean project = getDao().selectOneIfPresent(noFilter);
        if (project == null) {
            EmcProjectBean nameFilter = new EmcProjectBean();
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
    public List<CoreBpmnInstanceStatusDTO<Long>> completeTask(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = completeTaskBasic(wrapper);
        List<Long> projectIdList = coreBpmnInstanceStatusDTOList.stream().map(CoreBpmnInstanceStatusDTO::getId).collect(Collectors.toList());

        if (!projectIdList.isEmpty()) {
            List<EmcProjectBean> projectList = getDao().selectListByIds(projectIdList);
            List<EmcProjectBean> noSampleProjectList = projectList.stream().filter(e -> Constant.NO.equals(e.getSample())).collect(Collectors.toList());

            List<EmcProjectBean> updateProjectList = new ArrayList<>();

            // 送样任务
            if (!noSampleProjectList.isEmpty()) {
                List<Long> noSampleProjectIdList = noSampleProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());
                orderService.insertBpDataByProject(noSampleProjectIdList);

                // 删除无效审核记录
                List<CoreBpmnCommentBean> filterBpmnCommentList = new ArrayList<>();
                noSampleProjectIdList.forEach(id -> {
                    CoreBpmnCommentBean filterBpmnComment = new CoreBpmnCommentBean();
                    filterBpmnComment.setTargetId(getDao().getTable() + "$" + id);
                    filterBpmnComment.setStatusCode("schemeedit");
                    filterBpmnCommentList.add(filterBpmnComment);
                });

                // 样品测试status置为preLogged
                List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(noSampleProjectIdList, "PROJID");
                orderTaskList.forEach(a -> a.setStatus(EmcBizConstant.ORDERTASK_STATUS_PRELOGGED));
                orderTaskService.getDao().update(orderTaskList, "STATUS");

                coreBpmnCommentService.getDao().deleteBy(filterBpmnCommentList, "statusCode");
                sampleService.insertByProject(noSampleProjectIdList, EmcBizConstant.SAMPLE_STATUS_PRELOGGED);
                // 新增报告数据,不可见，也就是项目监测的样品类型表，因为禅城是按照样品类型进行出报告，所以这里把t_emc_report作为报告数据主表了
                reportService.insertByProject(noSampleProjectIdList, false);
                // 更新folder表状态
                List<Long> folderIdList = orderTaskList.stream().map(EmcOrderTaskBean::getFolderId).distinct().collect(Collectors.toList());
                List<EmcFolderBean> folderList = new ArrayList<>();
                folderIdList.forEach(e -> {
                    EmcFolderBean folder = new EmcFolderBean();
                    folder.setId(e);
                    folder.setStatus(EmcBizConstant.FOLDER_STATUS_DRAW);
                    folder.setProcessStatus(EmcBizConstant.FOLDER_PROCESSSTATUS_DONE);
                    folderList.add(folder);
                });
                folderService.getDao().update(folderList, "STATUS", "PROCESSSTATUS");
                // 更新项目节点状态
                noSampleProjectIdList.forEach(id -> {
                    EmcProjectBean updateProject = new EmcProjectBean();
                    updateProject.setId(id);
                    updateProject.setStatus(EmcBizConstant.PROJECT_STATUS_AUDITFINISHED);
                    updateProjectList.add(updateProject);
                });
            }

            // 采样任务
            List<EmcProjectBean> sampleProjectList = projectList.stream().filter(e -> Constant.YES.equals(e.getSample())).collect(Collectors.toList());
            if (!sampleProjectList.isEmpty()) {
                List<Long> sampleProjectIdList = sampleProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());
                // 不存在过质量控制的项目，新增主业务流程数据
                projSchemeService.insertBpData(sampleProjectIdList);
                // 新增报告数据，不可见，也就是项目监测的样品类型表，因为禅城是按照样品类型进行出报告，所以这里把t_emc_report作为报告数据主表了
                reportService.insertByProject(sampleProjectIdList, false);

                // 更新项目节点状态
                sampleProjectIdList.forEach(i -> {
                    EmcProjectBean updateProject = new EmcProjectBean();
                    updateProject.setId(i);
                    updateProject.setStatus(EmcBizConstant.PROJECT_STATUS_AUDITFINISHED);
                    updateProjectList.add(updateProject);
                });
            }

            // 更新项目节点状态
            getDao().update(updateProjectList, "STATUS");

            insertAuditRecordByProject(projectList, "PROCESS_COMPLETE", wrapper.getParamValue("bpmn_comment"), coreBpmnInstanceStatusDTOList);
            ProjectNodeUtils.updateNodes(projectIdList);
        }

        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_START)
    @ValidateDataWith(EmcProjectCompleteTaskValidator.class)
    public List<CoreBpmnInstanceStatusDTO<Long>> startProcess(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = startProcessBasic(wrapper);
        List<Long> idList = coreBpmnInstanceStatusDTOList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcProjectBean> projectList = selectListByFilter(SearchFilter.instance().match("ID", idList).filter(MatchPattern.OR));

        // 样品登记的项目提交时插入业务流程表
        // 筛选不采样的数据进行处理
        List<EmcProjectBean> noSampleProjectList = projectList.stream().filter(e -> Constant.NO.equals(e.getSample())).collect(Collectors.toList());

        // 样品登记的项目提交完成项目流程结束
        if (!noSampleProjectList.isEmpty()) {
            List<EmcProjectBean> updateProjectList = new ArrayList<>();

            // 需要进行质控的送样项目
            List<EmcProjectBean> needQcNoSampleProjectList = noSampleProjectList.stream().filter(p -> Constant.YES.equals(p.getExt$Item("qualitycontrol"))).collect(Collectors.toList());
            if (!needQcNoSampleProjectList.isEmpty()) {
                needQcNoSampleProjectList.forEach(project -> {
                    EmcProjectBean updateProject = new EmcProjectBean();
                    updateProject.setId(project.getId());
                    updateProject.setStatus(EmcBizConstant.PROJECT_STATUS_QUALITY_CONTROL);
                    updateProjectList.add(updateProject);
                });

                // 生成质控记录
                projectQcService.insert(needQcNoSampleProjectList);
            }

            // 不需要进行质控的送样任务
            List<Long> noQcNoSampleProjectIdList = noSampleProjectList.stream().filter(p -> Constant.NO.equals(p.getExt$Item("qualitycontrol"))).map(EmcProjectBean::getId).collect(Collectors.toList());
            if (!noQcNoSampleProjectIdList.isEmpty()) {
                orderService.insertBpDataByProject(noQcNoSampleProjectIdList);

                // 删除无效审核记录
                List<CoreBpmnCommentBean> filterBpmnCommentList = new ArrayList<>();
                noQcNoSampleProjectIdList.forEach(id -> {
                    CoreBpmnCommentBean filterBpmnComment = new CoreBpmnCommentBean();
                    filterBpmnComment.setTargetId(getDao().getTable() + "$" + id);
                    filterBpmnComment.setStatusCode("schemeedit");
                    filterBpmnCommentList.add(filterBpmnComment);
                });

                // 样品测试status置为preLogged
                List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(noQcNoSampleProjectIdList, "PROJID");
                orderTaskList.forEach(a -> a.setStatus(EmcBizConstant.ORDERTASK_STATUS_PRELOGGED));
                orderTaskService.getDao().update(orderTaskList, "STATUS");

                coreBpmnCommentService.getDao().deleteBy(filterBpmnCommentList, "statusCode");
                sampleService.insertByProject(noQcNoSampleProjectIdList, EmcBizConstant.SAMPLE_STATUS_PRELOGGED);
                // 新增报告数据,不可见，也就是项目监测的样品类型表，因为禅城是按照样品类型进行出报告，所以这里把t_emc_report作为报告数据主表了
                reportService.insertByProject(noQcNoSampleProjectIdList, false);
                // 更新folder表状态
                List<Long> folderIdList = orderTaskList.stream().map(EmcOrderTaskBean::getFolderId).distinct().collect(Collectors.toList());
                List<EmcFolderBean> folderList = new ArrayList<>();
                folderIdList.forEach(e -> {
                    EmcFolderBean folder = new EmcFolderBean();
                    folder.setId(e);
                    folder.setStatus(EmcBizConstant.FOLDER_STATUS_DRAW);
                    folder.setProcessStatus(EmcBizConstant.FOLDER_PROCESSSTATUS_DONE);
                    folderList.add(folder);
                });
                folderService.getDao().update(folderList, "STATUS", "PROCESSSTATUS");
                // 更新项目节点状态
                noQcNoSampleProjectIdList.forEach(id -> {
                    EmcProjectBean updateProject = new EmcProjectBean();
                    updateProject.setId(id);
                    updateProject.setStatus(EmcBizConstant.PROJECT_STATUS_AUDITFINISHED);
                    updateProjectList.add(updateProject);
                });
            }

            // 更新项目节点状态
            getDao().update(updateProjectList, "STATUS");
        }

        // 采样任务
        List<EmcProjectBean> sampleProjectList = projectList.stream().filter(e -> Constant.YES.equals(e.getSample())).collect(Collectors.toList());
        if (!sampleProjectList.isEmpty()) {
            // 不进行检测的项目，直接提交到报告
            List<Long> notDetectProjectIdList = sampleProjectList.stream().filter(p -> Constant.NO.equals(p.getExt$Item("detect"))).map(EmcProjectBean::getId).collect(Collectors.toList());
            if (!notDetectProjectIdList.isEmpty()) {
                // 新增报告数据,可见
                reportService.insertByProject(notDetectProjectIdList, true);
            }

            List<EmcProjectBean> needDetectProjectList = sampleProjectList.stream().filter(p -> !Constant.NO.equals(p.getExt$Item("detect"))).collect(Collectors.toList());

            // 过滤需要过质量控制的项目
            List<EmcProjectBean> qcProjectList = needDetectProjectList.stream().filter(p -> Constant.YES.equals(p.getExt$Item("qualitycontrol"))).collect(Collectors.toList());

            List<EmcProjectBean> updateProjectList = new ArrayList<>();

            // 如果存在需要过质量控制的项目
            if (!qcProjectList.isEmpty()) {
                // 更新项目节点状态
                qcProjectList.forEach(project -> {
                    EmcProjectBean updateProject = new EmcProjectBean();
                    updateProject.setId(project.getId());
                    updateProject.setStatus(EmcBizConstant.PROJECT_STATUS_QUALITY_CONTROL);
                    updateProjectList.add(updateProject);
                });

                // 生成质控记录
                projectQcService.insert(qcProjectList);
            }

            List<Long> noQcProjectIdList = needDetectProjectList.stream().filter(p -> !Constant.YES.equals(p.getExt$Item("qualitycontrol"))).map(EmcProjectBean::getId).collect(Collectors.toList());

            if (!noQcProjectIdList.isEmpty()) {
                // 不存在过质量控制的项目，新增主业务流程数据
                projSchemeService.insertBpData(idList);
                // 新增报告数据，不可见，也就是项目监测的样品类型表，因为禅城是按照样品类型进行出报告，所以这里把t_emc_report作为报告数据主表了
                reportService.insertByProject(idList, false);

                // 更新项目节点状态
                noQcProjectIdList.forEach(i -> {
                    EmcProjectBean updateProject = new EmcProjectBean();
                    updateProject.setId(i);
                    updateProject.setStatus(EmcBizConstant.PROJECT_STATUS_AUDITFINISHED);
                    updateProjectList.add(updateProject);
                });
            }

            // 更新项目节点状态
            getDao().update(updateProjectList, "STATUS");
        }

        insertAuditRecordByProject(projectList, "PROCESS_COMPLETE", wrapper.getParamValue("bpmn_comment"), coreBpmnInstanceStatusDTOList);
        ProjectNodeUtils.updateNodes(idList);

        return coreBpmnInstanceStatusDTOList;
    }

    // 提取工作流方法
    @Override
    public List<CoreBpmnInstanceStatusDTO<Long>> startProcessBasic(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcProjectService.super.startProcess(wrapper);
        return coreBpmnInstanceStatusDTOList;
    }
    
    // 提取工作流方法
    @Override
    public List<CoreBpmnInstanceStatusDTO<Long>> startProcessBasic(List<Long> projIdList, CoreBpmnParameterDTO parameter) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcProjectService.super.startProcess(projIdList, parameter);
        return coreBpmnInstanceStatusDTOList;
    }

    // 提取工作流方法
    @Override
    public List<CoreBpmnInstanceStatusDTO<Long>> completeTaskBasic(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcProjectService.super.completeTask(wrapper);
        return coreBpmnInstanceStatusDTOList;
    }

    // 提取工作流方法
    @Override
    public List<CoreBpmnInstanceStatusDTO<Long>> undoBasic(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcProjectService.super.undo(wrapper);
        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    @Transactional
    public void submitToReport(RestJsonWrapperBean wrapper) {
        List<EmcProjectBean> projectList = wrapper.parse(EmcProjectBean.class);
        List<Long> projIdList = projectList.stream().map(e -> e.getId()).collect(Collectors.toList());
        reportService.insertByProject(projIdList, true);// 新增报告数据,可见
        CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
        parameter.setComment("提交到报告");
        this.startProcess(projIdList, parameter);
        //this.endTask(projIdList, "提交到报告");
    }

    @Override
    public Page<EmcProjectBean> selectProjectStatistics(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return selectPagination(() -> getDao().selectProjectStatistics(parameter), rowBounds);
    }

    @Override
    public Page<EmcProjectBean> selectAnnualProjectStatistics(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return selectPagination(() -> getDao().selectAnnualProjectStatistics(parameter), rowBounds);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_UNDO)
    @ValidateDataWith(EmcUndoProjectValidator.class)
    public List<CoreBpmnInstanceStatusDTO<Long>> undo(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> instanceStatusList = undoBasic(wrapper);
        List<Long> proIdList = instanceStatusList.stream().map(CoreBpmnInstanceStatusDTO::getId).collect(Collectors.toList());

        // 获取相关业务数据，只需查询ID即可
        List<EmcFolderBean> folderList = folderService.getDao().selectListByOneColumnValues(proIdList, "PROJID", Collections.singletonList("ID"));
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(proIdList, "PROJID", Collections.singletonList("ID"));
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(proIdList, "PROJID", Collections.singletonList("ID"));
        List<EmcOrderTaskResultBean> taskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(proIdList, "PROJID", Collections.singletonList("ID"));
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID", Collections.singletonList("ID"));
        List<EmcReportBean> reportList = reportService.getDao().selectListByOneColumnValues(proIdList, "PROJID", Collections.singletonList("ID"));

        //获取气的采样参数业务数据
        List<EmcFolderBean> sampleParamFolderList = folderService.getDao().selectListByOneColumnValues(proIdList, "PARAMPROJID", Collections.singletonList("ID"));
        List<Long> sampleParamFolderIdList = sampleParamFolderList.stream().map(EmcFolderBean :: getId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> sampleParamOrderList = orderService.getDao().selectListByOneColumnValues(sampleParamFolderIdList, "FOLDERID", Collections.singletonList("ID"));
        List<EmcOrderTaskBean> sampleParamOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(sampleParamFolderIdList, "FOLDERID", Collections.singletonList("ID"));
        List<EmcOrderTaskResultBean> sampleParamTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(sampleParamFolderIdList, "FOLDERID", Collections.singletonList("ID"));
        List<Long> sampleParamOrderIdList = sampleParamOrderList.stream().map(EmcOrderBean :: getId).distinct().collect(Collectors.toList());
        List<EmcOrderItemGroupBean> sampleParamOrderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(sampleParamOrderIdList, "ORDERID", Collections.singletonList("ID"));
        folderList.addAll(sampleParamFolderList);
        orderList.addAll(sampleParamOrderList);
        orderTaskList.addAll(sampleParamOrderTaskList);
        taskResultList.addAll(sampleParamTaskResultList);
        orderItemGroupList.addAll(sampleParamOrderItemGroupList);

        // 删除相关数据
        folderService.getDao().deleteBy(folderList);
        orderService.getDao().deleteBy(orderList);
        orderTaskService.getDao().deleteBy(orderTaskList);
        orderTaskResultService.getDao().deleteBy(taskResultList);
        reportService.getDao().deleteBy(reportList);
        orderItemGroupService.getDao().deleteBy(orderItemGroupList);

        // 修改project表ProjNode和Status字段状态
        List<EmcProjectBean> updateProjectList = new ArrayList<>();
        proIdList.forEach(a -> {
            EmcProjectBean project = new EmcProjectBean();
            project.setId(a);
            project.setStatus(EmcBizConstant.PROJECT_STATUS_CREATE);

            updateProjectList.add(project);
        });
        getDao().update(updateProjectList, "STATUS");

        // 项目退回插入审核记录
        List<EmcProjectBean> projectList = getDao().selectListByIds(proIdList);
        this.insertAuditRecordByProject(projectList, "PROCESS_UNDO", wrapper.getParamValue("bpmn_comment"), null);
        ProjectNodeUtils.updateNodes(proIdList);

        return instanceStatusList;
    }

    @Override
    public Supplier<String> getDescSupplier(CoreBpmnRuntimeSource<EmcProjectBean, Long> source) {
        return () -> source.getOldItem().getProjName();
    }

    public void insertAuditRecordByProject(List<EmcProjectBean> projectList, String bpmnStatus, String comment, List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList) {
        List<EmcAuditRecordBean> auditRecordList = new ArrayList<>();
        for (EmcProjectBean project : projectList) {
            EmcAuditRecordBean auditRecord = new EmcAuditRecordBean();
            auditRecord.setId(ApplicationContextHelper.getNextIdentity());
            auditRecord.setProjId(project.getId());
            auditRecord.setProjNo(project.getProjNo());
            auditRecord.setComments(comment);

            auditRecord.setLogDesc("项目:" + project.getProjNo());
            auditRecord.setBizCategory("proj");

            if ("PROCESS_START".contentEquals(bpmnStatus)) {
                auditRecord.setStepCode("1");
                auditRecord.setAuditCategory("项目登记");
                auditRecord.setTaskName("项目登记");
                auditRecord.setOperateCategory("提交");
                auditRecord.setNextTaskName("方案审核");
            } else if ("PROCESS_COMPLETE".equals(bpmnStatus)) {
                auditRecord.setStepCode("2");
                auditRecord.setAuditCategory("项目管理");
                auditRecord.setOperateCategory("审核");
                @SuppressWarnings("rawtypes")
                CoreBpmnInstanceStatusDTO bpmnInstance = coreBpmnInstanceStatusDTOList.stream().filter(f -> ObjectUtils.equals(f.getId(), project.getId())).findFirst().orElse(null);
                if ("schemeaudit".equals(bpmnInstance.getStatusCode())) {
                    if(Constant.NO.equals(project.getSample())){
                        auditRecord.setTaskName("方案审核");
                        auditRecord.setNextTaskName("样品接收");
                    }else{
                        auditRecord.setTaskName("方案审核");
                        auditRecord.setNextTaskName("采样安排");
                    }
                } else {
                    auditRecord.setTaskName("方案编制");
                    auditRecord.setNextTaskName("方案审核");
                }
            } else if ("PROCESS_UNDO".equals(bpmnStatus)) {
                auditRecord.setStepCode("3");
                auditRecord.setAuditCategory("采样管理");
                auditRecord.setTaskName("采样安排");
                auditRecord.setOperateCategory("退回");
                auditRecord.setNextTaskName("项目登记");
            }

            auditRecordList.add(auditRecord);
        }
        auditRecordService.getDao().insert(auditRecordList);
    }

    @Override
    public Page<EmcProjectBean> selectAllProjects(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();

        parameter.setRawQueries();

        BeanPropertyDescriptor createdByOrgIdPropertyDescriptor = PersistableMetadataHelper.getCreatedByOrgIdPropertyDescriptor(EmcProjectBean.class);
        if (createdByOrgIdPropertyDescriptor != null) {
            parameter.setOrgAuthority();
        }

        if (!wrapper.getAuthorityList().isEmpty()) {
            wrapper.getAuthorityList().forEach(a -> parameter.setAuthorityParameter(a));
        }

        return this.selectPagination(parameter, rowBounds);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insertPlan(RestJsonWrapperBean jsonWrapper) {
        EmcProjectBean emcProject = jsonWrapper.parseUnique(EmcProjectBean.class);
        emcProject.setId(ApplicationContextHelper.getNextIdentity());
        emcProject.setRegistDate(LocalDateTime.now());

        EmcBizCategoryBean bizCategory = bizCategoryService.selectById(emcProject.getBizCategoryId());
        // 设置受检单位默认字段
        Long testOrgId = bizCategory.getTestOrgId();
        if (testOrgId != null) {
            EmcClientBean testClient = clientService.getDao().selectById(testOrgId);
            emcProject.setTestOrgId(testClient.getClientNo());
            emcProject.setTestOrgName(testClient.getClientName());
            emcProject.setTestOrgAddress(testClient.getAddress());
            emcProject.setTestOrgPhone(testClient.getPhone());
        }
        // 设置委托单位默认字段
        Long entrustOrgId = bizCategory.getEntrustOrgId();
        if (entrustOrgId != null) {
            EmcClientBean entrustClient = clientService.getDao().selectById(entrustOrgId);
            emcProject.setEntrustOrgId(entrustClient.getClientNo());
            emcProject.setEntrustOrgName(entrustClient.getClientName());
            emcProject.setEntrustOrgAddress(entrustClient.getAddress());
            emcProject.setEntrustOrgPhone(entrustClient.getPhone());
        }

        LocalDateTime taskDate = emcProject.getTaskDate() == null ? LocalDateTime.now() : emcProject.getTaskDate();
        String taskDateStr = DateTimeUtils.formatLocalDateTime(taskDate, "yy");
        String projNo = StringUtils.isEmpty(bizCategory.getBizCategoryId()) ? "" : bizCategory.getBizCategoryId();
        String sequenceNo = getSequenceNo(bizCategory.getBizCategoryId());

        projNo += taskDateStr + sequenceNo;
        emcProject.setProjNo(projNo);
        emcProject.setReportOrgId(bizCategory.getReportEditOrgId());

        emcProject.setBizCategoryCode(bizCategory.getBizCategoryId());
        emcProject.setSource("plan");
        getDao().insert(emcProject);
        return emcProject.getId();
    }

    @Override
    @Transactional
    public void changeSchedule(RestJsonWrapperBean wrapper) {
        EmcProjectBean project = wrapper.parseUnique(EmcProjectBean.class);

        if (!CronUtils.isValidExpression(project.getCron())) {
            throw new CronException("GIKAM.CRON.TIP.INVALID");
        }

        LocalDateTime now = LocalDateTime.now();
        if (DateTimeUtils.isBefore(now, project.getEndTime()) && DateTimeUtils.isBefore(project.getStartTime(), project.getEndTime())) {
            LocalDateTime startTime = project.getStartTime();
            if (DateTimeUtils.isBefore(startTime, now)) {
                startTime = now;
            }

            project.setNextTaskDate(CronUtils.getNextExecutionTime(project.getCron(), startTime, project.getEndTime()));

            // 开启定时任务
            String configName = new StringBuilder(I18nHelper.getMessage("EMC.MODULE.BP.PROJECT.PLANS.TITLE.PROJ_PLAN")).append(":").append(project.getProjName()).append("(").append(project.getId())
                    .append(")").toString();

            scheduleManager.schedule(getDao().getTable(), (getDao().getTable() + "$" + project.getId()), configName, project.getCron(), ("" + project.getId()), project.getStartTime(),
                    project.getEndTime());
        }

        getDao().update(project, "STARTTIME", "ENDTIME", "CRON", "NEXTTASKDATE");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_START)
    public void startProcessBySchedule(String projId) {

        EmcProjectBean projectPlan = selectById(NumberUtils.parseLong(projId));
        // 更新任务计划的下次任务执行时间
        projectPlan.setNextTaskDate(CronUtils.getNextExecutionTime(projectPlan.getCron(), projectPlan.getStartTime(), projectPlan.getEndTime()));
        getDao().update(projectPlan, "NEXTTASKDATE");

        // 复制项目计划，生成任务
        projectPlan.setSource("");

        LocalDateTime taskDate = projectPlan.getTaskDate() == null ? LocalDateTime.now() : projectPlan.getTaskDate();
        String taskDateStr = DateTimeUtils.formatLocalDateTime(taskDate, "yy");
        String newProjNo = projectPlan.getBizCategoryCode() + taskDateStr + ApplicationContextHelper.getNextSequence(getDao().getTable() + LocalDate.now().getYear(), projectPlan.getBizCategoryCode());

        List<String> newProjectNoList = new ArrayList<>();
        newProjectNoList.add(newProjNo);
        List<EmcProjectBean> projectPlanList = new ArrayList<>();
        projectPlanList.add(projectPlan);

        List<EmcProjectBean> newProjectList = copySampleProject(1, projectPlanList, newProjectNoList);

        submitNewProjectList(newProjectList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copyPlan(RestJsonWrapperBean wrapper) {
        List<EmcProjectBean> copyEmcProjectBeanList = wrapper.parse(EmcProjectBean.class);// 传递过来的复制对象
        if (copyEmcProjectBeanList.isEmpty()) {
            return;
        }
        EmcProjectBean emcProjectBean = copyEmcProjectBeanList.get(0);
        String copyQtyStr = emcProjectBean.getExt$Item("copyqty");
        if (StringUtils.isEmpty(copyQtyStr) || !NumberUtils.isNumber(copyQtyStr)) {
            return;
        }
        int copyQty = Integer.parseInt(copyQtyStr);
        int projectQty = copyEmcProjectBeanList.size();
        List<EmcProjectBean> emcOrderProjectList = getDao().selectListByIds(copyEmcProjectBeanList.stream().map(EmcProjectBean::getId).collect(Collectors.toList()));// 原始对象
        // 拷贝列表项属性
        emcOrderProjectList.stream().forEach(m -> {
            copyEmcProjectBeanList.stream().filter(c -> ObjectUtils.equals(c.getId(), m.getId())).forEach(t -> {
                m.setBizCategoryId(t.getBizCategoryId());
                m.setTaskDate(t.getTaskDate());
                m.setSource(t.getSource());
                m.setSample(t.getSample());
            });
        });
        // 查询业务编码
        String bizCategoryCode = bizCategoryService.selectById(copyEmcProjectBeanList.get(0).getBizCategoryId()).getBizCategoryId();
        // 批量获取项目编号，数量为项目数 * 复制份数
        List<String> newProjectNoList = new ArrayList<>();
        String sequenceNo = getSequenceNo(bizCategoryCode);
        if (projectQty * copyQty > 1) {
            newProjectNoList = ApplicationContextHelper.getNextSequenceList(getDao().getTable() + LocalDate.now().getYear(), bizCategoryCode, projectQty * copyQty - 1);
            newProjectNoList.add(0, sequenceNo);
        } else {
            newProjectNoList.add(sequenceNo);
        }
        LocalDateTime taskDate = emcProjectBean.getTaskDate() == null ? LocalDateTime.now() : emcProjectBean.getTaskDate();
        String taskDateStr = DateTimeUtils.formatLocalDateTime(taskDate, "yy");
        newProjectNoList = newProjectNoList.stream().map(r -> bizCategoryCode + taskDateStr + r).collect(Collectors.toList());

        copySampleProject(copyQty, emcOrderProjectList, newProjectNoList);

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_START)
    public void submitPlan(RestJsonWrapperBean wrapper) {
        List<EmcProjectBean> submitProjectList = wrapper.parse(EmcProjectBean.class);
        List<Long> idList = submitProjectList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcProjectBean> projectList = selectListByFilter(SearchFilter.instance().match("ID", idList).filter(MatchPattern.OR));

        String bizCategoryCode = projectList.get(0).getBizCategoryCode();
        int projectQty = projectList.size();

        // 批量获取项目编号，数量为项目数 * 复制份数
        List<String> newProjectNoList = ApplicationContextHelper.getNextSequenceList(getDao().getTable() + LocalDate.now().getYear(), bizCategoryCode, projectQty);
        for (int i = 0; i < projectQty; i++) {
            EmcProjectBean project = projectList.get(i);
            project.setSource("");
            LocalDateTime taskDate = project.getTaskDate() == null ? LocalDateTime.now() : project.getTaskDate();
            String taskDateStr = DateTimeUtils.formatLocalDateTime(taskDate, "yy");
            newProjectNoList.set(i, bizCategoryCode + taskDateStr + newProjectNoList.get(i));
        }

        // 复制项目
        List<EmcProjectBean> newProjectList = copySampleProject(1, projectList, newProjectNoList);

        // 提交新项目
        submitNewProjectList(newProjectList);
    }

    @Override
    @Transactional
    public void stopSchedule(RestJsonWrapperBean wrapper) {
        List<EmcProjectBean> projectList = wrapper.parse(EmcProjectBean.class);
        projectList.forEach(e -> {
            scheduleManager.cancel(getDao().getTable(), (getDao().getTable() + "$" + e.getId()));
        });
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_REJECT)
    public List<CoreBpmnInstanceStatusDTO<Long>> rejectTask(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = rejectTaskBasic(wrapper);
        List<Long> idList = coreBpmnInstanceStatusDTOList.stream().map(CoreBpmnInstanceStatusDTO::getId).collect(Collectors.toList());

        EmcProjectBean updateProject = new EmcProjectBean();
        updateProject.setStatus(EmcBizConstant.PROJECT_STATUS_CREATE);

        getDao().updateByIds(updateProject, idList, "STATUS");
        ProjectNodeUtils.updateNodes(idList);

        projectQcService.delete(idList);

        return coreBpmnInstanceStatusDTOList;
    }

    // 提取工作流方法
    @Override
    public List<CoreBpmnInstanceStatusDTO<Long>> rejectTaskBasic(RestJsonWrapperBean wrapper) {
        return EmcProjectService.super.rejectTask(wrapper);
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
    // 复制采样项目
    private List<EmcProjectBean> copySampleProject(int copyQty, List<EmcProjectBean> copyProjectList, List<String> newProjectNoList) {

        List<EmcProjSchemeBean> copySchemeList = projSchemeService.getDao().selectListByOneColumnValues(copyProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList()), "PROJID");
        List<Long> projSchemeIdList = copySchemeList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcProjOrderBean> copyOrderList = projOrderService.getDao().selectListByOneColumnValues(projSchemeIdList, "PROJSCHEMEID");

        // 批量生成新的点位编号
        int createFolderCount = 0;// 创建点位次数

        // 开始复制项目
        List<EmcProjectBean> newProjectList = new ArrayList<>();
        List<EmcProjSchemeBean> insertSchemeList = new ArrayList<>();
        List<EmcProjOrderBean> insertOrderList = new ArrayList<>();

        for (int i = 0; i < copyProjectList.size(); i++) {
            EmcProjectBean c = copyProjectList.get(i);
            // 当前项目需要复制的点位列表
            List<EmcProjSchemeBean> copyProjSchemeList = copySchemeList.stream().filter(e -> ObjectUtils.equals(e.getProjId(), c.getId())).collect(Collectors.toList());

            List<Long> copyProjSchemeIdList = copyProjSchemeList.stream().map(e -> e.getId()).collect(Collectors.toList());

            List<EmcProjOrderBean> copyProjOrderList = copyOrderList.stream().filter(e -> copyProjSchemeIdList.contains(e.getProjSchemeId())).collect(Collectors.toList());

            for (int j = 0; j < copyQty; j++) {
                EmcProjectBean newProject = new EmcProjectBean();
                BeanUtils.copyProperties(c, newProject);
                newProject.setProcessStatus(EmcBizConstant.REPORT_STATUS_DRAFT);
                long newProjectId = ApplicationContextHelper.getNextIdentity();
                newProject.setId(newProjectId);
                String newProjectNo = newProjectNoList.get(i * copyQty + j);
                newProject.setProjNo(newProjectNo);
                newProject.setCreatedById(LocalContextHelper.getLoginUserId());
                newProject.setCreatedByName(LocalContextHelper.getLoginUserName());
                newProject.setCreatedTime(LocalDateTime.now());
                newProject.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
                newProject.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
                newProjectList.add(newProject);

                List<String> newFolderNoList = projSchemeService.generateFolderNosByProjId(newProjectId, newProjectNo, copyProjSchemeList.size());

                List<EmcProjSchemeBean> newProjSchemeList = ObjectUtils.clone(copyProjSchemeList);
                List<EmcProjOrderBean> newProjOrderList = ObjectUtils.clone(copyProjOrderList);
                for (EmcProjSchemeBean newProjScheme : newProjSchemeList) {
                    Long projSchemeId = ApplicationContextHelper.getNextIdentity();
                    if (!newProjOrderList.isEmpty()) {
                        newProjOrderList.stream().forEach(newProjOrder -> {
                            if (ObjectUtils.equals(newProjScheme.getId(), newProjOrder.getProjSchemeId())) {
                                newProjOrder.setId(ApplicationContextHelper.getNextIdentity());
                                newProjOrder.setProjSchemeId(projSchemeId);
                            }
                        });
                    }
                    newProjScheme.setId(projSchemeId);
                    newProjScheme.setFolderNo(newFolderNoList.get(createFolderCount));
                    newProjScheme.setProjId(newProjectId);
                    createFolderCount++;
                }

                insertSchemeList.addAll(newProjSchemeList);
                insertOrderList.addAll(newProjOrderList);
            }
        }
        this.getDao().insert(newProjectList);
        projSchemeService.getDao().insert(insertSchemeList);
        projOrderService.getDao().insert(insertOrderList);
        return newProjectList;
    }

    // 手动提交项目
    private void submitNewProjectList(List<EmcProjectBean> newProjectList) {
        // 提交项目
        List<Long> projIdList = newProjectList.stream().map(e -> e.getId()).collect(Collectors.toList());
        CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
        parameter.setComment(EmcConstant.PROCESS_START);
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcProjectService.super.startProcess(projIdList, parameter);
        List<Long> idList = coreBpmnInstanceStatusDTOList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcProjectBean> projectList = selectListByFilter(SearchFilter.instance().match("ID", idList).filter(MatchPattern.OR));

        // 更新项目节点状态
        ProjectNodeUtils.updateNodes(idList);

        // 项目登记提交插入审核记录
        this.insertAuditRecordByProject(projectList, "PROCESS_START", EmcConstant.PROCESS_START, null);
    }

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
    public Page<EmcProjectBean> selectAnalysisAuditProject(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return selectPagination(() -> getDao().selectAnalysisAuditProject(parameter), rowBounds);
    }

    @Override
    public List<EmcProjectBean> selectProjectByContractId(Long contractId) {
        return getDao().selectProjectByContractId(contractId);
    }

    @Override
    public EmcProjectBean selectProjectDetail(Long id, RestJsonWrapperBean wrapper) {
        return selectFirstByFilter(SearchFilter.instance().match("ID", id).filter(MatchPattern.EQ), wrapper);
    }
    
 // 审核记录需要重新设计下
    public void insertAuditRecordByProject2(List<EmcProjectBean> projectList, String bpmnStatus, String comment, List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList) {
        List<EmcAuditRecordBean> auditRecordList = new ArrayList<>();
        for (EmcProjectBean project : projectList) {
            EmcAuditRecordBean auditRecord = new EmcAuditRecordBean();
            auditRecord.setId(ApplicationContextHelper.getNextIdentity());
            auditRecord.setProjId(project.getId());
            auditRecord.setProjNo(project.getProjNo());
            auditRecord.setComments(comment);

            auditRecord.setLogDesc("项目:" + project.getProjNo());
            auditRecord.setBizCategory("proj");

            if ("PROCESS_START".contentEquals(bpmnStatus)) {
                auditRecord.setStepCode("1");
                auditRecord.setAuditCategory("项目登记");
                auditRecord.setTaskName("项目登记");
                auditRecord.setOperateCategory("提交");
                if (Constant.NO.equals(project.getSample())) {
                    auditRecord.setNextTaskName("样品接收");
                } else {
                    auditRecord.setNextTaskName("质量控制");
                }
            } else if ("PROCESS_COMPLETE".equals(bpmnStatus)) {
                auditRecord.setStepCode("2");
                auditRecord.setAuditCategory("项目管理");
                auditRecord.setOperateCategory("审核");
                @SuppressWarnings("rawtypes")
                CoreBpmnInstanceStatusDTO bpmnInstance = coreBpmnInstanceStatusDTOList.stream().filter(f -> ObjectUtils.equals(f.getId(), project.getId())).findFirst().orElse(null);
                if (EmcBizConstant.PROJECT_STATUS_QUALITY_CONTROL.equals(bpmnInstance.getStatusCode())) {
                    auditRecord.setTaskName("质量控制");
                    auditRecord.setNextTaskName("采样安排");
                } else {
                    auditRecord.setTaskName("项目登记");
                    auditRecord.setNextTaskName("采样安排");
                }
            } else if ("PROCESS_UNDO".equals(bpmnStatus)) {
                auditRecord.setStepCode("3");
                auditRecord.setAuditCategory("采样管理");
                auditRecord.setTaskName("采样安排");
                auditRecord.setOperateCategory("退回");
                auditRecord.setNextTaskName("项目登记");
            }

            auditRecordList.add(auditRecord);
        }
        //auditRecordService.getDao().insert(auditRecordList);
    }
    
    private String getSequenceNo(int year, String key) {
        String projectNoPrefix = (year + "").substring(2) + key;
        List<EmcBizCategoryBean> bizCategoryList = bizCategoryService.getDao().selectListByOneColumnValue(key, "BIZCATEGORYID", Collections.singletonList("ID"));
        if (!bizCategoryList.isEmpty()) {
            List<EmcProjectBean> projectList = getDao().selectListByOneColumnValue(bizCategoryList.get(0).getId(), "BIZCATEGORYID", Collections.singletonList("PROJNO"));
            List<String> seqNoList = projectList.stream().filter(p -> p.getProjNo().startsWith(projectNoPrefix)).map(p -> p.getProjNo().replace(projectNoPrefix, "")).collect(Collectors.toList());
            OptionalInt maxValue = seqNoList.stream().filter(NumberUtils::isNumber).mapToInt(NumberUtils::parseInt).max();

            return  maxValue.isPresent() ? key + StringUtils.leftPad((maxValue.getAsInt() + 1) + "", 4) : key + "0001";
        }

        return getSequenceNo(year, key, 1).get(0);
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

    @Override
    public Page<EmcProjectBean> selectTodoAnalysisAuditProject(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return selectPagination(() -> getDao().selectTodoAnalysisAuditProject(parameter), rowBounds);
    }

    @Override
    public Page<EmcProjectBean> selectTodoAnalysisPretrialProject(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return selectPagination(() -> getDao().selectTodoAnalysisPretrialProject(parameter), rowBounds);
    }

    @Override
    public Page<EmcProjectBean> selectByReportEdit(RestJsonWrapperBean wrapper) { MapperParameter p = wrapper.extractMapFilter(); return selectPagination(() -> getDao().selectByReportEdit(p), wrapper.extractPageRowBounds()); }
    @Override
    public Page<EmcProjectBean> selectCrmInvoiceProjPagination(RestJsonWrapperBean wrapper) { MapperParameter p = wrapper.extractMapFilter(); return selectPagination(() -> getDao().selectCrmInvoiceProjListByCondition(p), wrapper.extractPageRowBounds()); }
    @Override
    public Page<EmcProjectBean> selectCrmPaymentProjPagination(RestJsonWrapperBean wrapper) { MapperParameter p = wrapper.extractMapFilter(); return selectPagination(() -> getDao().selectCrmPaymentProjListByCondition(p), wrapper.extractPageRowBounds()); }
    @Override
    public Page<EmcProjectBean> selectCrmProjListByCondition(RestJsonWrapperBean wrapper) { MapperParameter p = wrapper.extractMapFilter(); return selectPagination(() -> getDao().selectCrmProjListByCondition(p), wrapper.extractPageRowBounds()); }
    @Override
    public Page<EmcProjectBean> selectProjListPagination(RestJsonWrapperBean wrapper) { MapperParameter p = wrapper.extractMapFilter(); return selectPagination(() -> getDao().selectProjListByCondition(p), wrapper.extractPageRowBounds()); }
}
