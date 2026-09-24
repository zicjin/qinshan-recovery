package com.sunwayworld.emc.module.bp.bp.service.impl;

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
import com.sunwayworld.emc.module.bp.bp.dao.EmcFolderDao;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.bp.validator.EmcFolderSubmitValidator;
import com.sunwayworld.emc.module.bp.bp.validator.EmcFolderUndoValidator;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.engine.CoreBpmnRuntimeSource;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

@Repository
@GikamBean
public class EmcFolderServiceImpl implements EmcFolderService {
    @Autowired
    private EmcFolderDao folderDao;

    @Autowired
    private EmcProjectService projectService;

    @Autowired
    @Lazy
    private EmcOrderService orderService;

    @Autowired
    private EmcSampleService sampleService;

    @Autowired
    private EmcAuditRecordService auditRecordService;

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Autowired
    private EmcOrderItemGroupService orderItemGroupService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcFolderDao getDao() {
        return folderDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcFolderBean emcFolder = jsonWrapper.parseUnique(EmcFolderBean.class);
        emcFolder.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcFolder);
        return emcFolder.getId();
    }

    @Override
    public Page<EmcSampleBean> selectSampleInfoById(Long id, RestJsonWrapperBean wrapper) {
        return sampleService.selectPaginationByFilter(SearchFilter.instance().match("ID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public void updatProjectStatus(RestJsonWrapperBean jsonWrapper) {
        List<EmcProjectBean> projectList = jsonWrapper.parse(EmcProjectBean.class);
        projectList.stream().forEach(e -> e.setStatus("cancel"));
        projectService.updateIfChanged(projectList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void terminateFolder(RestJsonWrapperBean jsonWrapper) {
        String termination = jsonWrapper.getParamValue("termination");
        String remark = "1".equals(termination) ? "终止点位" : "取消终止点位";
        String folderStatus = "1".equals(termination) ? "done" : "draw";
        String orderTaskStatus = "1".equals(termination) ? "done" : "draft";
        String type = jsonWrapper.getParamValue("type");
        List<EmcOrderBean> orderList = jsonWrapper.parse(EmcOrderBean.class);
        List<Long> folderIdList = orderList.stream().map(e -> e.getFolderId()).distinct().collect(Collectors.toList());
        if ("byFolder".equals(type)) {
            folderIdList = orderList.stream().map(e -> e.getId()).distinct().collect(Collectors.toList());
        }
        List<EmcFolderBean> updateFolderList = getDao().selectListByIds(folderIdList, new Order[0]);
        updateFolderList.stream().forEach(e -> {
            e.setTermination(termination);
            e.setRemark(remark);
            e.setStatus(folderStatus);
        });
        List<EmcOrderBean> updateOrderList = orderService.getDao().selectListByOneColumnValues(folderIdList, "FOLDERID", new Order[0]);
        updateOrderList.stream().forEach(e -> {
            e.setTermination(termination);
            e.setRemark(remark);
        });
        List<EmcOrderTaskBean> taskList = orderTaskService.getDao().selectListByOneColumnValues(folderIdList, "FOLDERID", new Order[0]);
        taskList.stream().forEach(e -> e.setStatus(orderTaskStatus));
        getDao().update(updateFolderList, "TERMINATION", "REMARK", "STATUS");
        orderService.getDao().update(updateOrderList, "TERMINATION", "REMARK");
        orderTaskService.getDao().update(taskList, "STATUS");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_START)
    @ValidateDataWith(EmcFolderSubmitValidator.class)
    public List<CoreBpmnInstanceStatusDTO<Long>> startProcess(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcFolderService.super.startProcess(wrapper);
        List<Long> idList = coreBpmnInstanceStatusDTOList.stream().map(CoreBpmnInstanceStatusDTO::getId).collect(Collectors.toList());
        List<EmcFolderBean> folderList = selectListByFilter(SearchFilter.instance().match("ID", idList).filter(MatchPattern.OR));

        // 采样安排提交插入审核记录
        this.insertAuditRecordByFolder(folderList, "PROCESS_START", wrapper.getParamValue("bpmn_comment"), null);

        folderList.forEach(e -> e.setStatus(EmcBizConstant.FOLDER_STATUS_DRAW));
        getDao().update(folderList, "STATUS");

        List<Long> projectIdList = folderList.stream().map(EmcFolderBean::getProjId).distinct().collect(Collectors.toList());
        ProjectNodeUtils.updateNodes(projectIdList);
        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_UNDO)
    @ValidateDataWith(EmcFolderUndoValidator.class)
    public List<CoreBpmnInstanceStatusDTO<Long>> undo(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcFolderService.super.undo(wrapper);

        List<Long> folderIdList = coreBpmnInstanceStatusDTOList.stream().map(CoreBpmnInstanceStatusDTO::getId).collect(Collectors.toList());

        List<EmcFolderBean> folderList = selectListByFilter(SearchFilter.instance().match("ID", folderIdList).filter(MatchPattern.OR));

        // 现场采样回退插入审核记录
        this.insertAuditRecordByFolder(folderList, "PROCESS_UNDO", wrapper.getParamValue("bpmn_comment"), null);

        folderList.forEach(e -> e.setStatus(EmcBizConstant.FOLDER_STATUS_DRAFT));
        getDao().update(folderList, "STATUS");

        List<Long> projectIdList = folderList.stream().map(EmcFolderBean::getProjId).distinct().collect(Collectors.toList());
        ProjectNodeUtils.updateNodes(projectIdList);
        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    public Supplier<String> getDescSupplier(CoreBpmnRuntimeSource<EmcFolderBean, Long> source) {
        EmcFolderBean folder = source.getOldItem();

        return () -> new StringBuilder(folder.getExt$Item("projname")).append("-").append(folder.getMonitorPointName()).append("-").append(folder.getSampleUserName()).toString();
    }

    @Override
    @Transactional
    public void undoToAccept(RestJsonWrapperBean jsonWrapper) {
        List<Long> folderIdList = jsonWrapper.parse(EmcFolderBean.class).stream().map(e -> e.getId()).collect(Collectors.toList());

        // 将点位下样品测试(测试类型为分析的)状态置为preLogged,去除批号
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(folderIdList, "folderId").stream()
                .filter(e -> ObjectUtils.equals(EmcBizConstant.ANALYSISCATEGORY_ANALYSIS, e.getAnalysisType())).collect(Collectors.toList());
        List<EmcOrderTaskBean> updateOrderTaskList = new ArrayList<>();
        orderTaskList.forEach(e -> {
            EmcOrderTaskBean updateOrderTask = new EmcOrderTaskBean();
            updateOrderTask.setId(e.getId());
            updateOrderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_PRELOGGED);
            updateOrderTask.setRunId(null);
            updateOrderTaskList.add(updateOrderTask);
        });

        // 将样品所属采样单置为preLogged
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(folderIdList, "folderId");
        List<Long> sampleIdList = orderList.stream().map(e -> e.getSampleId()).distinct().collect(Collectors.toList());
        List<EmcSampleBean> updateSampleList = new ArrayList<>();
        sampleIdList.forEach(e -> {
            EmcSampleBean updateSample = new EmcSampleBean();
            updateSample.setId(e);
            updateSample.setStatus(EmcBizConstant.SAMPLE_STATUS_PRELOGGED);
            updateSampleList.add(updateSample);
        });

        orderTaskService.getDao().update(updateOrderTaskList, "status", "runId");
        sampleService.getDao().update(updateSampleList, "status");
        List<Long> projIdList = orderList.stream().map(a -> a.getProjId()).distinct().collect(Collectors.toList());
        ProjectNodeUtils.updateNodes(projIdList);
    }

    @Override
    public EmcFolderBean insertByOrder(EmcOrderBean order) {
        EmcFolderBean folder = new EmcFolderBean();
        folder.setId(ApplicationContextHelper.getNextIdentity());
        Long projId = order.getProjId();

        String newFolderNo = generateFolderNoByProjId(projId, projectService.selectDetail(projId).getProjNo());

        folder.setFolderNo(newFolderNo);
        folder.setProjId(order.getProjId());
        folder.setOrderCategory(order.getOrderCategory());
        folder.setSource(EmcBizConstant.BP_SOURCE_ORDERREGISTRATION);
        folder.setTermination(Constant.NO);

        getDao().insert(folder);
        return folder;
    }

    @Override
    public String generateFolderNoByProjId(Long projId, String projectNo) {
        List<EmcFolderBean> folderOfProjectList = getDao().selectListByOneColumnValue(projId, "PROJID", Collections.singletonList("FOLDERNO"));
        String newFolderNo = "";
        if (folderOfProjectList.isEmpty()) {
            newFolderNo = projectNo + "001";
        } else {
            String maxFolderNo = folderOfProjectList.stream().max(Comparator.comparing(EmcFolderBean::getFolderNo)).get().getFolderNo();
            newFolderNo = maxFolderNo.substring(0, maxFolderNo.length() - 3) + String.format("%03d", Long.parseLong(maxFolderNo.substring(maxFolderNo.length() - 3)) + 1);
        }

        return newFolderNo;
    }

    @Override
    public List<String> generateFolderNosByProjId(Long projId, String projectNo, int num) {

        List<EmcFolderBean> folderOfProjectList = getDao().selectListByOneColumnValue(projId, "PROJID");
        List<String> newFolderNoList = new ArrayList<>();
        String firstFolderNo = "";
        if (folderOfProjectList.isEmpty()) {
            firstFolderNo = projectNo + "001";
            newFolderNoList.add(firstFolderNo);
        } else {
            String maxFolderNo = folderOfProjectList.stream().max(Comparator.comparing(EmcFolderBean::getFolderNo)).get().getFolderNo();
            firstFolderNo = maxFolderNo.substring(0, maxFolderNo.length() - 3) + String.format("%03d", Long.parseLong(maxFolderNo.substring(maxFolderNo.length() - 3)) + 1);
            newFolderNoList.add(firstFolderNo);
        }

        if (num > 1) {
            for (int i = 0; i < num - 1; i++) {
                String beforeFolderNo = newFolderNoList.get(i);
                String nextFolderNo = beforeFolderNo.substring(0, beforeFolderNo.length() - 3) + String.format("%03d", Long.parseLong(beforeFolderNo.substring(beforeFolderNo.length() - 3)) + 1);
                newFolderNoList.add(nextFolderNo);
            }
        }

        return newFolderNoList;
    }

    @Override
    public Page<EmcFolderBean> querySamplePlans(RestJsonWrapperBean wrapper) {
        Page<EmcFolderBean> page = selectUndoablePagination(wrapper);
        List<EmcFolderBean> folderList = page.getRows();

        Map<String, List<EmcFolderBean>> userFolderMap = new HashMap<>();
        Map<String, String> userMap = new HashMap<>();
        folderList.forEach(f -> {
            String sampleUserId = f.getSampleUserId();
            String sceneUserId = f.getSceneUserId();
            if (StringUtils.isEmpty(sampleUserId) || StringUtils.isEmpty(sceneUserId)) {
                return;
            }
            String[] userIds = sampleUserId.split(",");
            String[] userNames = f.getSampleUserName().split(",");
            for (int i = 0; i < userIds.length; i++) {
                userMap.put(userIds[i], userNames[i]);
            }
            List<String> userIdList = new ArrayList<>(Arrays.asList(userIds));
            if (!userIdList.contains(sceneUserId)) {
                userIdList.add(sceneUserId);
            }

            userIdList.forEach(id -> {
                List<EmcFolderBean> userFolderList = userFolderMap.get(id);
                if (userFolderList == null) {
                    userFolderList = new ArrayList<>();
                }
                EmcFolderBean folderBean = new EmcFolderBean();
                BeanUtils.copyProperties(f, folderBean);
                folderBean.setExt$Item("userid", id);
                folderBean.setExt$Item("username", userMap.get(id));
                userFolderList.add(folderBean);
                userFolderMap.put(id, userFolderList);
            });
        });

        List<EmcFolderBean> newFolderList = new ArrayList<>();
        userFolderMap.forEach((k, v) -> {
            v.forEach(f -> {
                f.setExt$Item("size", v.size() + "");
                newFolderList.add(f);
            });
        });
        page.setRows(newFolderList);
        return page;
    }

    @Override
    @Transactional
    public void copyFolders(String folderNo, RestJsonWrapperBean wrapper) {
        EmcFolderBean folder = wrapper.parseUnique(EmcFolderBean.class);

        // 查询点位信息
        EmcFolderBean selectedFolder = getDao().selectById(folder.getId());

        EmcFolderBean newFolder = new EmcFolderBean();
        BeanUtils.copyProperties(selectedFolder, newFolder);
        PersistableHelper.resetBasicProperties(newFolder);

        Long newFolderId = ApplicationContextHelper.getNextIdentity();
        newFolder.setId(newFolderId);
        Long projId = folder.getProjId();
        String newFolderNo = generateFolderNoByProjId(projId, projectService.selectDetail(projId).getProjNo());
        newFolder.setFolderNo(newFolderNo);
        getDao().insert(newFolder);

        // 查询对应点位下的样品信息
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(Arrays.asList(folder.getId()), "FOLDERID");
        List<Long> orderIdList = orderList.stream().map(e -> e.getId()).collect(Collectors.toList());

        // 查询样品下的对应测试信息
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");

        // 查询样品下对应的结果信息
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");

        // 查询样品对应的分析项组
        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");

        List<EmcOrderBean> newOrderList = new ArrayList<>();
        List<EmcOrderTaskBean> newOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> newOrderTaskResultList = new ArrayList<>();
        List<EmcOrderItemGroupBean> newOrderItemGroupList = new ArrayList<>();

        for (int i = 0; i < orderList.size(); i++) {
            EmcOrderBean oldOrder = orderList.get(i);
            EmcOrderBean newOrder = new EmcOrderBean();

            BeanUtils.copyProperties(oldOrder, newOrder);
            PersistableHelper.resetBasicProperties(newOrder);

            newOrder.setId(ApplicationContextHelper.getNextIdentity());
            newOrder.setFolderId(newFolder.getId());


            if(newFolder.getFolderNo().contains("-")){
                newOrder.setOrderNo(newFolder.getFolderNo() + "-" + String.format("%03d", i + 1));
            }else{
                newOrder.setOrderNo(newFolder.getFolderNo() + String.format("%03d", i + 1));
            }


            newOrder.setLabelNo(newOrder.getOrderNo());
            newOrderList.add(newOrder);

            for (EmcOrderTaskBean orderTask : orderTaskList) {
                if (oldOrder.getId().equals(orderTask.getOrderId())) {
                    EmcOrderTaskBean newOrderTask = new EmcOrderTaskBean();
                    BeanUtils.copyProperties(orderTask, newOrderTask);
                    PersistableHelper.resetBasicProperties(newOrderTask);

                    newOrderTask.setId(ApplicationContextHelper.getNextIdentity());
                    newOrderTask.setOrderId(newOrder.getId());
                    newOrderTask.setFolderId(newFolder.getId());
                    newOrderTask.setEquiptAcquNo(newOrder.getLabelNo());
                    newOrderTaskList.add(newOrderTask);

                    for (EmcOrderTaskResultBean orderTaskResult : orderTaskResultList) {
                        if (orderTask.getId().equals(orderTaskResult.getOrderTaskId())) {
                            EmcOrderTaskResultBean newOrderTaskResult = new EmcOrderTaskResultBean();
                            BeanUtils.copyProperties(orderTaskResult, newOrderTaskResult);

                            newOrderTaskResult.setId(ApplicationContextHelper.getNextIdentity());
                            newOrderTaskResult.setFolderId(newFolderId);
                            newOrderTaskResult.setOrderId(newOrder.getId());
                            newOrderTaskResult.setOrderTaskId(newOrderTask.getId());
                            newOrderTaskResult.setOrderNo(newOrder.getOrderNo());
                            newOrderTaskResultList.add(newOrderTaskResult);
                        }
                    }
                }
            }

            List<EmcOrderItemGroupBean> oldItemGroupList = orderItemGroupList.stream().filter(group -> ObjectUtils.equals(oldOrder.getId(), group.getOrderId())).collect(Collectors.toList());
            oldItemGroupList.forEach(group -> {
                EmcOrderItemGroupBean newItemGroup = new EmcOrderItemGroupBean();
                BeanUtils.copyProperties(group, newItemGroup, EmcConstant.CREATED_COLUMNS);
                newItemGroup.setId(ApplicationContextHelper.getNextIdentity());
                newItemGroup.setOrderId(newOrder.getId());
                newOrderItemGroupList.add(newItemGroup);
            });
        }

        orderService.getDao().insert(newOrderList);
        orderTaskService.getDao().insert(newOrderTaskList);
        orderTaskResultService.getDao().insert(newOrderTaskResultList);
        orderItemGroupService.getDao().insert(newOrderItemGroupList);
    }

    @Override
    @Transactional
    public void dataSharing(RestJsonWrapperBean wrapper) {
        List<EmcFolderBean> folderList = wrapper.parse(EmcFolderBean.class);
        List<Long> folderIdList = folderList.stream().map(e -> e.getId()).collect(Collectors.toList());

        String projectIds = wrapper.getParamValue("projectIds");
        List<String> projIdList = Arrays.asList(projectIds.split(","));
        
        if(folderIdList.size()>0) {
        	List<EmcOrderTaskBean> requiredSharingorderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("folderId", folderIdList).filter(MatchPattern.OR));

            if(requiredSharingorderTaskList.size()>0) {
            	List<EmcOrderTaskResultBean> requiredSharingorderTaskResultList = orderTaskResultService
                        .selectListByFilter(SearchFilter.instance().match("orderTaskId", requiredSharingorderTaskList.stream().map(e -> e.getId()).collect(Collectors.toList())).filter(MatchPattern.OR));

                List<EmcOrderTaskResultBean> sharingorderTaskResultList = orderTaskResultService.selectListByFilter(SearchFilter.instance().match("projId", projIdList).filter(MatchPattern.OR));

                List<EmcOrderTaskResultBean> updateOrderTaskResultList = new ArrayList<>();

                requiredSharingorderTaskResultList.forEach(r -> {

                    EmcOrderTaskResultBean resultOfSharing = sharingorderTaskResultList.stream()
                            .filter(o -> ObjectUtils.equals(r.getTestId(), o.getTestId()) && ObjectUtils.equals(r.getMethodId(), o.getMethodId()) && ObjectUtils.equals(r.getItemId(), o.getItemId())
                                    && ObjectUtils.equals(r.getRepeatNo(), o.getRepeatNo()) && ObjectUtils.equals(r.getExt$Item("monitorpointid"), o.getExt$Item("monitorpointid")))
                            .findFirst().orElse(null);

                    if (resultOfSharing != null) {

                        EmcOrderTaskResultBean updateOrderTaskResult = new EmcOrderTaskResultBean();
                        updateOrderTaskResult.setId(r.getId());
                        updateOrderTaskResult.setOriginalResult(resultOfSharing.getOriginalResult());
                        updateOrderTaskResult.setReportResult(resultOfSharing.getReportResult());
                        updateOrderTaskResultList.add(updateOrderTaskResult);
                    }
                });

                orderTaskResultService.getDao().update(updateOrderTaskResultList, "originalResult", "reportResult");
            }
        }
        
    }

    protected void insertAuditRecordByFolder(List<EmcFolderBean> folderList, String bpmnStatus, String comment, List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList) {
        List<EmcAuditRecordBean> auditRecordList = new ArrayList<>();
        for (EmcFolderBean folder : folderList) {
            EmcAuditRecordBean auditRecord = new EmcAuditRecordBean();
            auditRecord.setId(ApplicationContextHelper.getNextIdentity());
            auditRecord.setProjId(folder.getProjId());
            auditRecord.setProjNo(folder.getExt$Item("projno"));
            auditRecord.setFolderId(folder.getId());
            auditRecord.setFolderNo(folder.getFolderNo());
            auditRecord.setComments(comment);

            auditRecord.setLogDesc("点位:" + folder.getFolderNo());
            auditRecord.setBizCategory("folder");

            if ("PROCESS_START".contentEquals(bpmnStatus)) {
                auditRecord.setStepCode("3");
                auditRecord.setAuditCategory("采样管理");
                auditRecord.setTaskName("采样安排");
                auditRecord.setOperateCategory("提交");
                auditRecord.setNextTaskName("现场采样");
            } else if ("PROCESS_UNDO".equals(bpmnStatus)) {
                auditRecord.setStepCode("3");
                auditRecord.setAuditCategory("采样管理");
                auditRecord.setTaskName("现场采样");
                auditRecord.setOperateCategory("退回");
                auditRecord.setNextTaskName("采样安排");
            }

            auditRecordList.add(auditRecord);
        }
        auditRecordService.getDao().insert(auditRecordList);
    }

    // 提取工作流方法
    @Override
    public List<CoreBpmnInstanceStatusDTO<Long>> startProcessBasic(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcFolderService.super.startProcess(wrapper);
        return coreBpmnInstanceStatusDTOList;
    }
    
    @Override
    public void updateStatus(List<EmcOrderTaskBean> orderTaskList) {
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

        List<Long> folderIdList = orderTaskList.stream().map(EmcOrderTaskBean::getFolderId).distinct().collect(Collectors.toList());
        List<EmcFolderBean> folderList = getDao().selectListByOneColumnValues(folderIdList, "ID");
        List<EmcFolderBean> updateFolderList = new ArrayList<>();

        for (EmcOrderTaskBean orderTask : filterOrderTaskList) {

            Long projId = orderTask.getProjId();
            String monitorTmplId = orderTask.getMonitorTmplId();

            boolean orderTaskAllMatch = allOrderTaskList.stream().filter(e -> ObjectUtils.equals(e.getProjId(), projId) && ObjectUtils.equals(e.getMonitorTmplId(), monitorTmplId))
                    .allMatch(e -> EmcBizConstant.ORDERTASK_STATUS_DONE.equals(e.getStatus()));

            boolean sampleAllMatch = allSampleList.stream().filter(e -> ObjectUtils.equals(e.getProjId(), projId) && ObjectUtils.equals(e.getMonitorTmplId(), monitorTmplId))
                    .allMatch(e -> EmcBizConstant.SAMPLE_STATUS_DONE.equals(e.getStatus()));

            if (orderTaskAllMatch && sampleAllMatch) {

                EmcFolderBean folder = folderList.stream().filter(e -> ObjectUtils.equals(projId, e.getProjId()) && ObjectUtils.equals(monitorTmplId, e.getMonitorTmplId())).findFirst()
                        .orElse(null);
                if (folder != null) {
                    EmcFolderBean updateFolder = new EmcFolderBean();
                    updateFolder.setId(folder.getId());
                    updateFolder.setStatus(EmcBizConstant.FOLDER_STATUS_DONE);
                    updateFolderList.add(updateFolder);
                }
            }
        }

        getDao().update(updateFolderList, "STATUS");
    }
}
