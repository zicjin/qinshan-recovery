package com.sunwayworld.emc.module.bp.bp.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.Serializable;

import com.sunwayworld.emc.module.bp.project.project.service.PfProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.projectnode.ProjectNodeUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.PfFolderService;
import com.sunwayworld.emc.module.bp.bp.service.PfOrderService;
import com.sunwayworld.emc.module.bp.bp.service.PfOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.PfOrderTaskService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderContainerService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.PfOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.projscheme.service.PfProjSchemeService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorTmplService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnParameterDTO;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;


/**
 * @author zhengxy@sunwayworld.com 2021年07月19日
 */
@Repository
@GikamBean
public class PfFolderServiceImpl extends EmcFolderServiceImpl implements PfFolderService {

    @Autowired
    // @Lazy
    private PfProjectService projectService;

    @Autowired
    private EmcMonitorTmplService monitorTmplService;

    @Autowired
    @Lazy
    private PfOrderService pfOrderService;

    @Autowired
    @Lazy
    private PfOrderTaskService pfOrderTaskService;

    @Autowired
    @Lazy
    private PfOrderTaskResultService pfOrderTaskResultService;

    @Autowired
    private PfOrderItemGroupService pfOrderItemGroupService;

    @Autowired
    @Lazy
    private EmcOrderContainerService orderContainerService;

    @Override
    public EmcFolderBean insertByOrder(EmcOrderBean order) {
        EmcFolderBean folder = new EmcFolderBean();
        folder.setId(ApplicationContextHelper.getNextIdentity());
        Long projId = order.getProjId();

        EmcProjectBean project = projectService.selectById(projId);
        EmcMonitorTmplBean monitorTmpl = monitorTmplService.selectById(order.getMonitorTmplId());

        String newFolderNo = generateFolderNo(project, order.getMonitorTmplId());

        folder.setFolderNo(newFolderNo);
        folder.setProjId(order.getProjId());
        folder.setOrderCategory(order.getOrderCategory());
        folder.setMonitorTmplId(monitorTmpl.getId());
        folder.setSource(EmcBizConstant.BP_SOURCE_ORDERREGISTRATION);
        folder.setTermination(Constant.NO);

        getDao().insert(folder);
        return folder;
    }

    @Override
    @Transactional
    public void copyFolders(String folderNo, RestJsonWrapperBean wrapper) {
        EmcFolderBean folder = wrapper.parseUnique(EmcFolderBean.class);
        EmcFolderBean selectedFolder = this.getDao().selectById(folder.getId());
        EmcFolderBean newFolder = new EmcFolderBean();
        BeanUtils.copyProperties((Object)selectedFolder, (Object)newFolder, new String[0]);
        PersistableHelper.resetBasicProperties(newFolder);
        EmcMonitorTmplBean monitorTmpl = this.monitorTmplService.selectById(selectedFolder.getMonitorTmplId());
        Long newFolderId = ApplicationContextHelper.getNextIdentity();
        newFolder.setId(newFolderId);
        newFolder.setSource("COPY");
        Long projId = folder.getProjId();
        EmcProjectBean project = (EmcProjectBean)this.projectService.selectById(projId);
        List<String> newFolderNoList = this.generateFolderNosByProjId(projId, project.getProjNo(), selectedFolder.getMonitorTmplId(), 1);
        String newFolderNo = newFolderNoList.get(0);
        newFolder.setProcessStatus(selectedFolder.getProcessStatus());
        newFolder.setFolderNo(newFolderNo);
        this.getDao().insert(newFolder);
        List<EmcOrderBean> folderOrderList = this.pfOrderService.getDao().selectListByOneColumnValue(folder.getId(), "FOLDERID", new Order[0]);
        List<EmcOrderBean> copiedOrderList = folderOrderList.stream().filter(order -> ObjectUtils.isEmpty(order.getSampleId())).collect(Collectors.toList());
        List<Long> orderIdList = copiedOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = this.pfOrderTaskService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID", new Order[0]);
        List<EmcOrderTaskResultBean> orderTaskResultList = this.pfOrderTaskResultService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID", new Order[0]);
        List<EmcOrderItemGroupBean> orderItemGroupList = this.pfOrderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID", new Order[0]);
        ArrayList<EmcOrderBean> newOrderList = new ArrayList<EmcOrderBean>();
        ArrayList<EmcOrderTaskBean> newOrderTaskList = new ArrayList<EmcOrderTaskBean>();
        ArrayList<EmcOrderTaskResultBean> newOrderTaskResultList = new ArrayList<EmcOrderTaskResultBean>();
        ArrayList<EmcOrderItemGroupBean> newOrderItemGroupList = new ArrayList<>();
        for (int i = 0; i < copiedOrderList.size(); ++i) {
            EmcOrderBean oldOrder = (EmcOrderBean)copiedOrderList.get(i);
            EmcOrderBean newOrder = new EmcOrderBean();
            BeanUtils.copyProperties((Object)oldOrder, (Object)newOrder, PfConstant.CREATED_COLUMNS);
            PersistableHelper.resetBasicProperties(newOrder);
            newOrder.setId(ApplicationContextHelper.getNextIdentity());
            newOrder.setFolderId(newFolder.getId());
            newOrder.setOrderNo(newFolderNo + StringUtils.leftPad(i + 1 + "", 3));
            newOrder.setLabelNo(newOrder.getOrderNo());
            newOrderList.add(newOrder);
            for (EmcOrderTaskBean orderTask : orderTaskList) {
                if (!oldOrder.getId().equals(orderTask.getOrderId())) continue;
                EmcOrderTaskBean newOrderTask = new EmcOrderTaskBean();
                BeanUtils.copyProperties((Object)orderTask, (Object)newOrderTask, new String[0]);
                PersistableHelper.resetBasicProperties(newOrderTask);
                newOrderTask.setId(ApplicationContextHelper.getNextIdentity());
                newOrderTask.setOrderId(newOrder.getId());
                newOrderTask.setFolderId(newFolder.getId());
                newOrderTask.setEquiptAcquNo(newOrder.getOrderNo());
                newOrderTaskList.add(newOrderTask);
                for (EmcOrderTaskResultBean orderTaskResult : orderTaskResultList) {
                    if (!orderTask.getId().equals(orderTaskResult.getOrderTaskId())) continue;
                    EmcOrderTaskResultBean newOrderTaskResult = new EmcOrderTaskResultBean();
                    BeanUtils.copyProperties((Object)orderTaskResult, (Object)newOrderTaskResult, new String[0]);
                    newOrderTaskResult.setId(ApplicationContextHelper.getNextIdentity());
                    newOrderTaskResult.setFolderId(newFolderId);
                    newOrderTaskResult.setOrderId(newOrder.getId());
                    newOrderTaskResult.setOrderTaskId(newOrderTask.getId());
                    newOrderTaskResult.setOrderNo(newOrder.getOrderNo());
                    newOrderTaskResultList.add(newOrderTaskResult);
                }
            }
            List<EmcOrderItemGroupBean> oldItemGroupList = orderItemGroupList.stream().filter(group -> ObjectUtils.equals(oldOrder.getId(), group.getOrderId())).collect(Collectors.toList());
            oldItemGroupList.forEach(group -> {
                EmcOrderItemGroupBean newItemGroup = new EmcOrderItemGroupBean();
                BeanUtils.copyProperties(group, (Object)newItemGroup, EmcConstant.CREATED_COLUMNS);
                newItemGroup.setId(ApplicationContextHelper.getNextIdentity());
                newItemGroup.setOrderId(newOrder.getId());
                newOrderItemGroupList.add(newItemGroup);
            });
        }
        this.orderContainerService.createOrderContainer(newOrderList, newOrderTaskList);
        this.pfOrderService.getDao().insert(newOrderList);
        this.pfOrderTaskService.getDao().insert(newOrderTaskList);
        this.pfOrderTaskResultService.getDao().insert(newOrderTaskResultList);
        this.pfOrderItemGroupService.getDao().insert(newOrderItemGroupList);
    }


    @Override
    public List<String> generateFolderNosByProjId(Long projId, String projectNo, String monitorTmplId, int num) {
        String orderCategoryCode = "";
        if (!StringUtils.isEmpty(monitorTmplId)) {
            EmcMonitorTmplBean monitorTmpl = monitorTmplService.selectById(monitorTmplId);
            if (monitorTmpl != null) {
                orderCategoryCode = monitorTmpl.getExt$Item("ordercategorycode");
            }
        }
        if (StringUtils.isEmpty(orderCategoryCode)) {
            orderCategoryCode = "";
        }
        List<EmcFolderBean> projectFolders = getDao().selectListByOneColumnValue(projId, "PROJID");
        List<EmcFolderBean> sameTemplateFolders = projectFolders.stream()
                .filter(folder -> ObjectUtils.equals(monitorTmplId, folder.getMonitorTmplId()))
                .collect(Collectors.toList());
        List<String> folderNoList = new ArrayList<>();
        String firstFolderNo;
        if (sameTemplateFolders.isEmpty()) {
            firstFolderNo = projectNo + orderCategoryCode + "001";
        } else {
            String maxFolderNo = sameTemplateFolders.stream().max(Comparator.comparing(EmcFolderBean::getFolderNo)).get().getFolderNo();
            firstFolderNo = maxFolderNo.substring(0, maxFolderNo.length() - 3)
                    + String.format("%03d", Long.parseLong(maxFolderNo.substring(maxFolderNo.length() - 3)) + 1L);
        }
        folderNoList.add(firstFolderNo);
        for (int i = 1; i < num; i++) {
            String previous = folderNoList.get(i - 1);
            folderNoList.add(previous.substring(0, previous.length() - 3)
                    + String.format("%03d", Long.parseLong(previous.substring(previous.length() - 3)) + 1L));
        }
        return folderNoList;
    }

    @Override
    public String generateFolderNo(EmcProjectBean project, String monitorTmplId) {
        List<String> seqNoList = generateFolderNosByProjId(project.getId(), project.getProjNo(), monitorTmplId, 1);
        if (!seqNoList.isEmpty()) {
            return seqNoList.get(0);
        }
        return ApplicationContextHelper.getNextSequence(getDao().getTable());
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_START)
    public List<CoreBpmnInstanceStatusDTO<Long>> startProcess(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = super.startProcessBasic(wrapper);
        List<Long> idList = coreBpmnInstanceStatusDTOList.stream().map(CoreBpmnInstanceStatusDTO::getId).collect(Collectors.toList());
        if (idList.isEmpty()) {
            return coreBpmnInstanceStatusDTOList;
        }

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
    @AuditTrailEntry(AuditTrailType.PROCESS_START)
    public List<CoreBpmnInstanceStatusDTO<Long>> startProcess(List<Long> itemIdList, CoreBpmnParameterDTO parameter) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusList = super.startProcess(itemIdList, parameter);
        List<Long> idList = coreBpmnInstanceStatusList.stream().map(CoreBpmnInstanceStatusDTO::getId).collect(Collectors.toList());
        if (idList.isEmpty()) {
            return coreBpmnInstanceStatusList;
        }

        List<EmcFolderBean> folderList = selectListByFilter(SearchFilter.instance().match("ID", idList).filter(MatchPattern.OR));
        // 采样安排提交插入审核记录
        this.insertAuditRecordByFolder(folderList, "PROCESS_START", parameter.getComment(), null);

        folderList.forEach(e -> e.setStatus(EmcBizConstant.FOLDER_STATUS_DRAW));
        getDao().update(folderList, "STATUS");

        List<Long> projectIdList = folderList.stream().map(EmcFolderBean::getProjId).distinct().collect(Collectors.toList());
        ProjectNodeUtils.updateNodes(projectIdList);

        return coreBpmnInstanceStatusList;
    }

    @Override
    @Transactional
    public String  submitRemind(RestJsonWrapperBean wrapper){
    	//分析类型是分析的类型（外面）为空
        List<EmcFolderBean> folderList = wrapper.parse(EmcFolderBean.class);
 	    List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
 	   	List<EmcOrderTaskBean> orderTaskList = pfOrderTaskService.selectListByFilter(SearchFilter.instance()
 	   		.match("FOLDERID", folderIdList).filter(MatchPattern.OR)
 	   		.match("ANALYSISTYPE", "analysis").filter(MatchPattern.SEQ));

        List<EmcOrderTaskBean> containerEmptyTaskList = orderTaskList.stream().filter(task -> ObjectUtils.isEmpty(task.getContainerCategoryId())).collect(Collectors.toList());
        if(containerEmptyTaskList.isEmpty()){
        	return null;
        }

        // 按样品编号进行分组
        List<String> messageList = new ArrayList<>();
        Map<String, List<EmcOrderTaskBean>> orderTaskMap = containerEmptyTaskList.stream().collect(Collectors.groupingBy(task -> task.getExt$Item("orderNo")));
        orderTaskMap.forEach((orderNo, taskList) -> {
            String testNames = taskList.stream().map(EmcOrderTaskBean::getTestName).distinct().collect(Collectors.joining("、"));
            String message = I18nHelper.getMessage("EMC.MODULE.BP.FOLDER.FOLDERS.TIP.SUBMITREMIND", orderNo, testNames);
            messageList.add(message);
        });

        return String.join(",", messageList);
    }

    @Override
    @Transactional
    public void submitProject(RestJsonWrapperBean wrapper) {
        List<EmcProjectBean> projectList = wrapper.parse(EmcProjectBean.class);
        List<Long> projectIdList = projectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());
        List<EmcFolderBean> folderList = this.selectListByFilter(
                SearchFilter.instance().match("projId", projectIdList).filter(MatchPattern.OR)
                        .match("status", "draft").filter(MatchPattern.SEQ), new Order[0]);
        if (folderList.isEmpty()) {
            return;
        }
        List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
        CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
        parameter.setComment(wrapper.getParamValue("comment"));
        this.startProcess(folderIdList, parameter);
    }

    @Override
    @Transactional
    public void updateByProject(RestJsonWrapperBean wrapper) {
        List<EmcProjectBean> projectList = wrapper.parse(EmcProjectBean.class);
        List<Long> projectIdList = projectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());
        List<EmcFolderBean> folderList = this.selectListByFilter(
                SearchFilter.instance().match("projId", projectIdList).filter(MatchPattern.OR)
                        .match("status", "draft").filter(MatchPattern.SEQ), new Order[0]);
        if (folderList.isEmpty()) {
            return;
        }
        EmcFolderBean wrapperFolder = wrapper.parseUnique(EmcFolderBean.class);
        folderList.forEach(folder -> {
            folder.setSampleUserId(wrapperFolder.getSampleUserId());
            folder.setSampleUserName(wrapperFolder.getSampleUserName());
            folder.setSceneUserId(wrapperFolder.getSceneUserId());
            folder.setSceneUserName(wrapperFolder.getSceneUserName());
            folder.setPlanSampleDate(wrapperFolder.getPlanSampleDate());
        });
        this.getDao().updateIfChanged(folderList);
    }

    @Override
    public RestValidationResultBean customizeValidate(Long id, String columnName, String columnValue) {
        if (!StringUtils.equalsIgnoreCase("monitorPointNo", columnName) || columnValue == null) {
            return new RestValidationResultBean(true);
        }
        List<EmcOrderBean> orderList = this.pfOrderService.getDao().selectListByOneColumnValue(id, "folderid", new Order[0]);
        if (orderList.stream().anyMatch(order -> order.getQcCategoryId() != null)) {
            return new RestValidationResultBean(true);
        }
        if (!validateString(columnValue)) {
            return new RestValidationResultBean(false, "必须包含大写字母和数字");
        }
        return new RestValidationResultBean(true);
    }

    public boolean validateString(String input) {
        return input != null && !input.isEmpty() && input.matches("^(?=.*[A-Z])(?=.*\\d).+$");
    }

    @Override
    @Transactional
    public void sortFolderNo(RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        if (orderList.isEmpty()) {
            return;
        }
        List<Long> folderIdList = orderList.stream().map(EmcOrderBean::getFolderId).distinct().collect(Collectors.toList());
        List<EmcFolderBean> folderList = this.selectListByIds(folderIdList);
        String monitorTmplId = orderList.get(0).getMonitorTmplId();
        EmcMonitorTmplBean monitorTmpl = this.monitorTmplService.selectById(monitorTmplId);
        String orderCategoryCode = monitorTmpl == null ? "" : monitorTmpl.getExt$Item("ordercategorycode");
        if (StringUtils.isEmpty(orderCategoryCode)) {
            orderCategoryCode = "";
        }
        EmcProjectBean project = this.projectService.selectById(orderList.get(0).getProjId());
        String prefix = project.getProjNo() + orderCategoryCode;
        folderList.sort(Comparator.comparing(EmcFolderBean::getId, Comparator.nullsLast(Long::compareTo)));
        List<EmcOrderBean> updateOrderList = new ArrayList<>();
        for (int i = 0; i < folderList.size(); i++) {
            EmcFolderBean folder = folderList.get(i);
            folder.setFolderNo(prefix + String.format("%03d", i + 1));
            EmcOrderBean order = orderList.stream().filter(o -> ObjectUtils.equals(folder.getId(), o.getFolderId())).findFirst().orElse(null);
            if (order != null) {
                order.setOrderNo(folder.getFolderNo() + "001");
                order.setLabelNo(order.getOrderNo());
                updateOrderList.add(order);
            }
        }
        this.getDao().update(folderList, "FOLDERNO");
        this.pfOrderService.getDao().update(updateOrderList, "ORDERNO", "LABELNO");
    }

}
