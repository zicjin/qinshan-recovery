/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.engine.support;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnCommentBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceCandidatorDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceRuntimeContainerDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskBean;
import com.sunwayworld.module.sys.bpmn.diagram.BpmnDiagramHelper;
import com.sunwayworld.module.sys.bpmn.diagram.CommentStatus;
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

public class CoreBpmnRuntimeWithdrawProcessSupport<T extends Auditable<ID>, ID extends Serializable>
extends AbstractCoreBpmnRuntimeProcessSupport<T, ID> {
    @Override
    protected void doValidate(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
        boolean withdrawValidateFlag = ApplicationContextHelper.getEnvironment().getProperty("sunway.bpmn.withdraw-validate", Boolean.class, true);
        if (withdrawValidateFlag) {
            List<CoreBpmnCommentBean> commentList = this.getBpmnCommentList(sourceList);
            int j = sourceList.size();
            for (int i = 0; i < j; ++i) {
                CoreBpmnRuntimeSource source = sourceList.get(i);
                CoreBpmnCommentBean comment = commentList.stream().filter(c -> c.getTargetId().equals(source.getTargetId())).max((c1, c2) -> c1.getId().compareTo(c2.getId())).orElse(null);
                if (comment != null && comment.getStatus().equals(CommentStatus.SUBMIT.name())) continue;
                throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.PROCESS_AUDITED", new String[0]);
            }
        }
    }

    @Override
    protected List<CoreBpmnInstanceStatusDTO<ID>> doProcess(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
        List<CoreBpmnInstanceBean> instanceList = BpmnRuntimeCacheProvider.getBpmnRuntimeInstanceList();
        List<Long> idList = instanceList.stream().map(i -> i.getId()).collect(Collectors.toList());
        List<CoreBpmnInstanceCandidatorDTO> instanceCandidatorList = this.instanceService.selectInstanceCandidatorList(idList);
        sourceList.forEach(s -> s.setCheckPermission(false));
        ArrayList<CoreBpmnInstanceStatusDTO<ID>> instanceStatusList = new ArrayList<CoreBpmnInstanceStatusDTO<ID>>();
        int j = sourceList.size();
        for (int i2 = 0; i2 < j; ++i2) {
            CoreBpmnRuntimeSource<T, ID> source = sourceList.get(i2);
            CoreBpmnInstanceBean instance = BpmnRuntimeCacheProvider.getBpmnRuntimeInstance(source);
            CoreBpmnInstanceStatusDTO<ID> instanceStatus = new CoreBpmnInstanceStatusDTO<ID>();
            CoreBpmnInstanceRuntimeContainerDTO runtimeContainer = new CoreBpmnInstanceRuntimeContainerDTO();
            instanceStatus.setRuntimeContainer(runtimeContainer);
            runtimeContainer.setVars(source.getVars());
            runtimeContainer.setInitiator(instance.getInitiator());
            runtimeContainer.setProcId(instance.getProcId());
            runtimeContainer.setProcInstId(instance.getId());
            List<CoreBpmnInstanceTaskBean> instanceTaskList = BpmnRuntimeCacheProvider.getBpmnRuntimeInstanceTaskList(source);
            StringBuilder sourceNameBuilder = new StringBuilder();
            StringBuilder sourceCodeBuilder = new StringBuilder();
            for (CoreBpmnInstanceTaskBean instanceTask : instanceTaskList) {
                if (sourceNameBuilder.length() > 0) {
                    sourceNameBuilder.append(",");
                }
                sourceNameBuilder.append(instanceTask.getTaskName());
                if (sourceCodeBuilder.length() > 0) {
                    sourceCodeBuilder.append(",");
                }
                sourceCodeBuilder.append(instanceTask.getStatusCode());
            }
            instanceStatus.setStatusCode(sourceCodeBuilder.toString());
            runtimeContainer.setCandidatorList(instanceCandidatorList.stream().filter(c -> c.getInstId().equals(instance.getId())).map(c -> c.getCandidatorId()).distinct().collect(Collectors.toList()));
            if (StringUtils.isEmpty(source.getComment())) {
                source.setComment(I18nHelper.getMessage("GIKAM.BPMN.COMMENT.WITHDRAW", new String[0]));
            }
            Document document = this.diagramService.getDocument(instance.getDiagramId());
            String sourceName = sourceNameBuilder.toString();
            String targetName = BpmnDiagramHelper.getStartNodeName(document);
            this.insertComment(source.getTargetId(), null, CommentStatus.WITHDRAW, source, instance.getProcId(), sourceName, this.getRoute(sourceName, targetName));
            instanceStatus.setId(source.getId());
            instanceStatus.setProcessStatus(ProcessStatus.DRAFT_WITHDRAWED);
            instanceStatusList.add(instanceStatus);
            BpmnRuntimeCacheProvider.getProcessData().addDeleteBpmnTargetIdList(source.getTargetId());
            BpmnRuntimeCacheProvider.getProcessData().addDeleteInstance(instance);
        }
        return instanceStatusList;
    }

    private List<CoreBpmnCommentBean> getBpmnCommentList(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
        List targetIdList = sourceList.stream().map(CoreBpmnRuntimeSource::getTargetId).collect(Collectors.toList());
        return this.commentService.getDao().selectListByOneColumnValues(targetIdList, "TARGETID", new Order[0]);
    }
}
