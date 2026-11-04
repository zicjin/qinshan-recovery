/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.engine.support;

import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.utils.XmlUtils;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceRuntimeContainerDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskHisBean;
import com.sunwayworld.module.sys.bpmn.diagram.BpmnDiagramHelper;
import com.sunwayworld.module.sys.bpmn.diagram.CommentStatus;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import com.sunwayworld.module.sys.bpmn.diagram.TaskStatus;
import com.sunwayworld.module.sys.bpmn.engine.CoreBpmnRuntimeSource;
import com.sunwayworld.module.sys.bpmn.engine.cache.BpmnRuntimeCacheProvider;
import com.sunwayworld.module.sys.bpmn.engine.support.AbstractCoreBpmnRuntimeProcessSupport;
import com.sunwayworld.module.sys.bpmn.exception.BpmnException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.Element;

public class CoreBpmnRuntimeRejectProcessSupport<T extends Auditable<ID>, ID extends Serializable>
extends AbstractCoreBpmnRuntimeProcessSupport<T, ID> {
    @Override
    protected void doValidate(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
        int j = sourceList.size();
        for (int i = 0; i < j; ++i) {
            CoreBpmnRuntimeSource<T, ID> source = sourceList.get(i);
            Element taskElement = BpmnRuntimeCacheProvider.getBpmnRuntimeTaskElement(source);
            if (BpmnDiagramHelper.isCommentRequiredTask(taskElement) && StringUtils.isEmpty(source.getComment())) {
                throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_DRAFT.TIP.COMMENT_REQUIRED", new String[0]);
            }
            String rejectValidatorSql = BpmnDiagramHelper.getRejectValidatorSql(taskElement);
            if (!StringUtils.isBlank(rejectValidatorSql)) {
                this.prepareValidateSql(i + 1, rejectValidatorSql, source.getId());
            }
            this.prepareCallback(AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory.reject, taskElement, source.getId());
            this.prepareScript(AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory.reject, taskElement, source.getId());
        }
    }

    @Override
    protected List<CoreBpmnInstanceStatusDTO<ID>> doProcess(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
        List<CoreBpmnInstanceTaskHisBean> taskHisList;
        ArrayList<CoreBpmnInstanceStatusDTO<ID>> instanceStatusList = new ArrayList<CoreBpmnInstanceStatusDTO<ID>>();
        ArrayList<CoreBpmnInstanceTaskHisBean> filterList = new ArrayList<CoreBpmnInstanceTaskHisBean>();
        for (CoreBpmnRuntimeSource<T, ID> source : sourceList) {
            if (StringUtils.isEmpty(source.getTargetStatusCode()) || ProcessStatus.DRAFT.name().equalsIgnoreCase(source.getTargetStatusCode())) continue;
            CoreBpmnInstanceTaskHisBean filter = new CoreBpmnInstanceTaskHisBean();
            CoreBpmnInstanceTaskBean instanceTask = BpmnRuntimeCacheProvider.getBpmnRuntimeInstanceTask(source);
            filter.setInstId(instanceTask.getInstId());
            filter.setStatusCode(source.getTargetStatusCode());
            filterList.add(filter);
        }
        if (!filterList.isEmpty() && !(taskHisList = this.taskHisService.selectLatestTaskHisList(filterList)).isEmpty()) {
            HashMap taskHisMap = new HashMap();
            for (CoreBpmnInstanceTaskHisBean taskHis : taskHisList) {
                CoreBpmnRuntimeSource source = sourceList.stream().filter(s -> {
                    if (!s.getTargetStatusCode().equals(taskHis.getStatusCode())) {
                        return false;
                    }
                    CoreBpmnInstanceTaskBean instanceTask = BpmnRuntimeCacheProvider.getBpmnRuntimeInstanceTask(s);
                    return taskHis.getInstId().equals(instanceTask.getInstId());
                }).findAny().orElse(null);
                if (source == null) continue;
                taskHisMap.put(source.getId(), taskHis);
            }
            BpmnRuntimeCacheProvider.cacheBpmnRuntimeTaskHisMap(taskHisMap);
        }
        for (CoreBpmnRuntimeSource<T, ID> source : sourceList) {
            CoreBpmnInstanceBean instance = BpmnRuntimeCacheProvider.getBpmnRuntimeInstance(source);
            CoreBpmnInstanceStatusDTO<ID> instanceStatus = new CoreBpmnInstanceStatusDTO<ID>();
            CoreBpmnInstanceRuntimeContainerDTO runtimeContainer = new CoreBpmnInstanceRuntimeContainerDTO();
            instanceStatus.setRuntimeContainer(runtimeContainer);
            runtimeContainer.setVars(source.getVars());
            runtimeContainer.setInitiator(instance.getInitiator());
            runtimeContainer.setProcId(instance.getProcId());
            runtimeContainer.setProcInstId(instance.getId());
            CoreBpmnInstanceTaskBean instanceTask = BpmnRuntimeCacheProvider.getBpmnRuntimeInstanceTask(source);
            Document diagram = BpmnRuntimeCacheProvider.getBpmnRuntimeDocument(source);
            Element taskElement = BpmnRuntimeCacheProvider.getBpmnRuntimeTaskElement(source);
            instanceStatus.setStatusCode(BpmnDiagramHelper.getUserTaskStatusCode(taskElement));
            if (StringUtils.isEmpty(source.getTargetStatusCode()) || ProcessStatus.DRAFT.name().equalsIgnoreCase(source.getTargetStatusCode())) {
                BpmnRuntimeCacheProvider.getProcessData().addDeleteInstance(instance);
                instanceStatus.setId(source.getId());
                instanceStatus.setProcessStatus(ProcessStatus.DRAFT_REJECTED);
                instanceStatusList.add(instanceStatus);
                BpmnRuntimeCacheProvider.getProcessData().addDeleteBpmnTargetIdList(source.getTargetId());
            } else {
                this.rejectTask(diagram, instance, instanceTask, BpmnRuntimeCacheProvider.getBpmnRuntimeUnlimitedInstanceTaskList(), source);
                instanceStatus.setId(source.getId());
                instanceStatus.setProcessStatus(ProcessStatus.APPROVE_REJECTED);
                instanceStatus.setNextStatusCode(source.getTargetStatusCode());
                instanceStatusList.add(instanceStatus);
                BpmnRuntimeCacheProvider.getProcessData().addUpdateProcessStatusBpmnTarget(this.getBpmnTarget(source, ProcessStatus.APPROVE_REJECTED));
            }
            if (StringUtils.isEmpty(source.getComment())) {
                source.setComment(I18nHelper.getMessage("GIKAM.BPMN.COMMENT.REJECT", new String[0]));
            }
            String sourceName = XmlUtils.getAttributeValue(taskElement, "name");
            String targetName = StringUtils.isEmpty(instanceStatus.getNextStatusCode()) ? BpmnDiagramHelper.getStartNodeName(diagram) : BpmnDiagramHelper.getUserTaskName(BpmnDiagramHelper.getProcessElementByStatusCode(diagram, instanceStatus.getNextStatusCode()));
            this.insertComment(instance.getTargetId(), taskElement, CommentStatus.REJECT, source, instance.getProcId(), sourceName, this.getRoute(sourceName, targetName));
            BpmnRuntimeCacheProvider.getProcessData().addDeleteInstanceTaskId(instanceTask.getId());
        }
        return instanceStatusList;
    }

    private void rejectTask(Document diagram, CoreBpmnInstanceBean instance, CoreBpmnInstanceTaskBean task, List<CoreBpmnInstanceTaskBean> instanceTaskUnlimitedList, CoreBpmnRuntimeSource<T, ID> source) {
        CoreBpmnInstanceTaskBean instanceTask = new CoreBpmnInstanceTaskBean();
        instanceTask.setId(task.getId());
        instanceTask.setStatus(TaskStatus.REJECTED.name());
        BpmnRuntimeCacheProvider.getProcessData().addUpdateInstanceTaskStatus(instanceTask);
        BpmnRuntimeCacheProvider.getProcessData().addDeleteCandidatorInstanceTaskId(task.getId());
        for (CoreBpmnInstanceTaskBean instanceTaskUnlimited : instanceTaskUnlimitedList) {
            if (task.getId().equals(instanceTaskUnlimited.getId()) || BpmnRuntimeCacheProvider.getProcessData().getDeleteInstanceTaskIdList().contains(instanceTaskUnlimited.getId())) continue;
            BpmnRuntimeCacheProvider.getProcessData().addDeleteInstanceTaskId(instanceTaskUnlimited.getId());
        }
        Element targetElement = BpmnDiagramHelper.getProcessElementByStatusCode(diagram, source.getTargetStatusCode());
        this.insertNextInstanceTask(diagram, instance, Arrays.asList(targetElement), source);
    }
}
