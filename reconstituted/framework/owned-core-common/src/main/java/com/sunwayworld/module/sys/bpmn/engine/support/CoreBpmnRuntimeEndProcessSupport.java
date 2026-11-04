/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.engine.support;

import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceRuntimeContainerDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.diagram.BpmnDiagramHelper;
import com.sunwayworld.module.sys.bpmn.diagram.CommentStatus;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessElementType;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import com.sunwayworld.module.sys.bpmn.engine.CoreBpmnRuntimeSource;
import com.sunwayworld.module.sys.bpmn.engine.cache.BpmnRuntimeCacheProvider;
import com.sunwayworld.module.sys.bpmn.engine.support.AbstractCoreBpmnRuntimeProcessSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.Element;

public class CoreBpmnRuntimeEndProcessSupport<T extends Auditable<ID>, ID extends Serializable>
extends AbstractCoreBpmnRuntimeProcessSupport<T, ID> {
    @Override
    protected void doValidate(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
        int j = sourceList.size();
        for (int i = 0; i < j; ++i) {
            CoreBpmnRuntimeSource<T, ID> source = sourceList.get(i);
            Document diagram = BpmnRuntimeCacheProvider.getBpmnRuntimeDocument(source);
            Element taskElement = BpmnRuntimeCacheProvider.getBpmnRuntimeTaskElement(source);
            String passValidatorSql = BpmnDiagramHelper.getPassValidatorSql(taskElement);
            if (!StringUtils.isBlank(passValidatorSql)) {
                this.prepareValidateSql(i + 1, passValidatorSql, source.getId());
            }
            this.prepareCallback(AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory.pass, taskElement, source.getId());
            this.prepareScript(AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory.pass, taskElement, source.getId());
            Element endEventElement = BpmnDiagramHelper.getProcessElement(diagram, ProcessElementType.END_EVENT);
            this.prepareEndCallback(AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory.end, endEventElement, source.getId());
            this.prepareEndScript(AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory.end, endEventElement, source.getId());
        }
    }

    @Override
    protected List<CoreBpmnInstanceStatusDTO<ID>> doProcess(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
        ArrayList<CoreBpmnInstanceStatusDTO<ID>> instanceStatusList = new ArrayList<CoreBpmnInstanceStatusDTO<ID>>();
        for (CoreBpmnRuntimeSource<T, ID> source : sourceList) {
            CoreBpmnInstanceBean instance = BpmnRuntimeCacheProvider.getBpmnRuntimeInstance(source);
            CoreBpmnInstanceStatusDTO<ID> instanceStatus = new CoreBpmnInstanceStatusDTO<ID>();
            CoreBpmnInstanceRuntimeContainerDTO runtimeContainer = new CoreBpmnInstanceRuntimeContainerDTO();
            instanceStatus.setRuntimeContainer(runtimeContainer);
            runtimeContainer.setVars(source.getVars());
            runtimeContainer.setInitiator(instance.getInitiator());
            runtimeContainer.setProcId(instance.getProcId());
            runtimeContainer.setProcInstId(instance.getId());
            Element taskElement = BpmnRuntimeCacheProvider.getBpmnRuntimeTaskElement(source);
            instanceStatus.setStatusCode(BpmnDiagramHelper.getUserTaskStatusCode(taskElement));
            BpmnRuntimeCacheProvider.getProcessData().addDeleteInstance(instance);
            instanceStatus.setId(source.getId());
            instanceStatus.setProcessStatus(ProcessStatus.DONE);
            instanceStatusList.add(instanceStatus);
            if (StringUtils.isEmpty(source.getComment())) {
                source.setComment(I18nHelper.getMessage("GIKAM.BPMN.COMMENT.END", new String[0]));
            }
            String sourceName = BpmnDiagramHelper.getTaskName(taskElement);
            String targetName = BpmnDiagramHelper.getEndNodeName(BpmnRuntimeCacheProvider.getBpmnRuntimeDocument(source));
            this.insertComment(instance.getTargetId(), taskElement, CommentStatus.END, source, instance.getProcId(), sourceName, this.getRoute(sourceName, targetName));
            BpmnRuntimeCacheProvider.getProcessData().addDeleteBpmnTargetIdList(source.getTargetId());
        }
        return instanceStatusList;
    }
}
