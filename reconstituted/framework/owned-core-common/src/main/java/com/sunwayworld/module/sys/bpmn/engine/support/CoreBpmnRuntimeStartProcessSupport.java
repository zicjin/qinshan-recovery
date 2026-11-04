/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.engine.support;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceRuntimeContainerDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnProcBean;
import com.sunwayworld.module.sys.bpmn.diagram.BpmnDiagramHelper;
import com.sunwayworld.module.sys.bpmn.diagram.CommentStatus;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessElementType;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import com.sunwayworld.module.sys.bpmn.engine.CoreBpmnRuntimeSource;
import com.sunwayworld.module.sys.bpmn.engine.cache.BpmnRuntimeCacheProvider;
import com.sunwayworld.module.sys.bpmn.engine.support.AbstractCoreBpmnRuntimeProcessSupport;
import com.sunwayworld.module.sys.bpmn.exception.BpmnException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.dom4j.Document;
import org.dom4j.Element;

public class CoreBpmnRuntimeStartProcessSupport<T extends Auditable<ID>, ID extends Serializable>
extends AbstractCoreBpmnRuntimeProcessSupport<T, ID> {
    @Override
    protected void doValidate(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
        int j = sourceList.size();
        for (int i = 0; i < j; ++i) {
            CoreBpmnRuntimeSource<T, ID> source = sourceList.get(i);
            if (!StringUtils.startsWithIgnoreCase(source.getOldItem().getProcessStatus(), ProcessStatus.DRAFT.name())) {
                throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.PROCESS_STATUS_UPDATED", new String[0]);
            }
            Element startEventElement = BpmnRuntimeCacheProvider.getBpmnRuntimeTaskElement(source);
            if (BpmnDiagramHelper.isCommentRequiredTask(startEventElement) && StringUtils.isEmpty(source.getComment())) {
                throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_DRAFT.TIP.COMMENT_REQUIRED", new String[0]);
            }
            String passValidatorSql = BpmnDiagramHelper.getPassValidatorSql(startEventElement);
            if (!StringUtils.isBlank(passValidatorSql)) {
                this.prepareValidateSql(i + 1, passValidatorSql, source.getId());
            }
            this.prepareCallback(AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory.pass, startEventElement, source.getId());
            this.prepareScript(AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory.pass, startEventElement, source.getId());
        }
    }

    @Override
    protected List<CoreBpmnInstanceStatusDTO<ID>> doProcess(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
        ArrayList<CoreBpmnInstanceStatusDTO<ID>> instanceStatusList = new ArrayList<CoreBpmnInstanceStatusDTO<ID>>();
        for (CoreBpmnRuntimeSource<T, ID> source : sourceList) {
            CoreBpmnProcBean proc = BpmnRuntimeCacheProvider.getBpmnProc(source);
            CoreBpmnInstanceBean instance = new CoreBpmnInstanceBean();
            instance.setId(ApplicationContextHelper.getNextIdentity());
            instance.setTargetId(source.getTargetId());
            instance.setProcId(proc.getId());
            instance.setInitiator(LocalContextHelper.getLoginUserId());
            instance.setDiagramId(proc.getDiagramId());
            BpmnRuntimeCacheProvider.getProcessData().addInsertInstance(instance);
            CoreBpmnInstanceStatusDTO<ID> instanceStatus = new CoreBpmnInstanceStatusDTO<ID>();
            CoreBpmnInstanceRuntimeContainerDTO runtimeContainer = new CoreBpmnInstanceRuntimeContainerDTO();
            instanceStatus.setRuntimeContainer(runtimeContainer);
            runtimeContainer.setVars(source.getVars());
            runtimeContainer.setInitiator(instance.getInitiator());
            runtimeContainer.setProcId(instance.getProcId());
            runtimeContainer.setProcInstId(instance.getId());
            Document diagram = BpmnRuntimeCacheProvider.getBpmnRuntimeDocument(source);
            Element startEventElement = BpmnRuntimeCacheProvider.getBpmnRuntimeTaskElement(source);
            Pair<ProcessStatus, List<String>> result = this.completeTask(diagram, instance, startEventElement, source);
            instanceStatus.setId(source.getId());
            instanceStatus.setProcessStatus(result.getFirst());
            instanceStatus.setStatusCode(BpmnDiagramHelper.getUserTaskStatusCode(startEventElement));
            if (!ProcessStatus.DONE.equals((Object)result.getFirst())) {
                instanceStatus.setNextStatusCode(result.getSecond().stream().collect(Collectors.joining(",")));
            }
            instanceStatusList.add(instanceStatus);
            if (StringUtils.isEmpty(source.getComment())) {
                source.setComment(I18nHelper.getMessage("GIKAM.BPMN.COMMENT.SUBMIT", new String[0]));
            }
            String sourceName = BpmnDiagramHelper.getStartNodeName(diagram);
            String targetName = StringUtils.isEmpty(instanceStatus.getNextStatusCode()) ? BpmnDiagramHelper.getEndNodeName(diagram) : this.getElementName(diagram, ArrayUtils.asList(StringUtils.split(instanceStatus.getNextStatusCode(), ",")));
            this.insertComment(instance.getTargetId(), startEventElement, CommentStatus.SUBMIT, source, instance.getProcId(), sourceName, this.getRoute(sourceName, targetName));
            BpmnRuntimeCacheProvider.getProcessData().addInsertOrUpdateBpmnTarget(this.getBpmnTarget(source, result.getFirst()));
            if (!ProcessStatus.DONE.equals((Object)result.getFirst())) continue;
            Element endEventElement = BpmnDiagramHelper.getProcessElement(diagram, ProcessElementType.END_EVENT);
            this.prepareEndCallback(AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory.end, endEventElement, source.getId());
            this.prepareEndScript(AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory.end, endEventElement, source.getId());
        }
        return instanceStatusList;
    }

    private Pair<ProcessStatus, List<String>> completeTask(Document diagram, CoreBpmnInstanceBean instance, Element startEventElement, CoreBpmnRuntimeSource<T, ID> source) {
        List<Element> nextTaskElementList = BpmnRuntimeCacheProvider.getNextTask(source).getNextTaskElementList();
        if (!nextTaskElementList.isEmpty()) {
            if (nextTaskElementList.stream().anyMatch(t -> ProcessElementType.END_EVENT.equals((Object)BpmnDiagramHelper.getProcessElementType(t)))) {
                BpmnRuntimeCacheProvider.getProcessData().addDeleteInstance(instance);
                return Pair.of(ProcessStatus.DONE, null);
            }
        } else {
            throw new BpmnException("GIKAM.BPMN.TIP.NEXT_TASK_NOT_FOUND", new String[0]);
        }
        this.insertNextInstanceTask(diagram, instance, nextTaskElementList, source);
        List nextStatusCodeList = nextTaskElementList.stream().map(t -> BpmnDiagramHelper.getUserTaskStatusCode(t)).collect(Collectors.toList());
        return Pair.of(ProcessStatus.APPROVE, nextStatusCodeList);
    }
}
