/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.engine.support;

import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnCommentBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceRuntimeContainerDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnProcBean;
import com.sunwayworld.module.sys.bpmn.diagram.BpmnDiagramHelper;
import com.sunwayworld.module.sys.bpmn.diagram.CommentStatus;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import com.sunwayworld.module.sys.bpmn.engine.CoreBpmnRuntimeSource;
import com.sunwayworld.module.sys.bpmn.engine.support.AbstractCoreBpmnRuntimeProcessSupport;
import com.sunwayworld.module.sys.bpmn.exception.BpmnException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.dom4j.Document;

public class CoreBpmnRuntimeUndoProcessSupport<T extends Auditable<ID>, ID extends Serializable>
extends AbstractCoreBpmnRuntimeProcessSupport<T, ID> {
    @Override
    protected void doValidate(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
    }

    @Override
    protected List<CoreBpmnInstanceStatusDTO<ID>> doProcess(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
        ArrayList<CoreBpmnInstanceStatusDTO<ID>> instanceStatusList = new ArrayList<CoreBpmnInstanceStatusDTO<ID>>();
        int j = sourceList.size();
        for (int i = 0; i < j; ++i) {
            CoreBpmnRuntimeSource<T, ID> source = sourceList.get(i);
            if (!StringUtils.startsWithIgnoreCase(source.getOldItem().getProcessStatus(), ProcessStatus.DONE.name())) {
                throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.PROCESS_STATUS_UPDATED", new String[0]);
            }
            if (StringUtils.isEmpty(source.getComment())) {
                source.setComment(I18nHelper.getMessage("GIKAM.BPMN.COMMENT.UNDO", new String[0]));
            }
            String sourceName = null;
            String targetName = null;
            CoreBpmnCommentBean lastComment = (CoreBpmnCommentBean)this.commentService.selectFirstByFilter(SearchFilter.instance().match("TARGETID", source.getTargetId()).filter(MatchPattern.EQ), Order.desc("ID"));
            if (lastComment != null) {
                source.setProcId(lastComment.getProcId());
                CoreBpmnProcBean proc = (CoreBpmnProcBean)this.procService.selectByIdIfPresent(lastComment.getProcId());
                if (proc != null) {
                    Document document = this.diagramService.getDocument(proc.getDiagramId());
                    sourceName = BpmnDiagramHelper.getEndNodeName(document);
                    targetName = BpmnDiagramHelper.getStartNodeName(document);
                }
            }
            if (!StringUtils.isBlank(sourceName)) {
                sourceName = I18nHelper.getMessage("CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.END_NODE", new String[0]);
            }
            if (!StringUtils.isBlank(targetName)) {
                targetName = I18nHelper.getMessage("CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.START_NODE", new String[0]);
            }
            this.insertComment(source.getTargetId(), null, CommentStatus.UNDO, source, source.getProcId(), sourceName, this.getRoute(sourceName, targetName));
            CoreBpmnInstanceStatusDTO<ID> instanceStatus = new CoreBpmnInstanceStatusDTO<ID>();
            CoreBpmnInstanceRuntimeContainerDTO runtimeContainer = new CoreBpmnInstanceRuntimeContainerDTO();
            instanceStatus.setRuntimeContainer(runtimeContainer);
            instanceStatus.setId(source.getId());
            instanceStatus.setProcessStatus(ProcessStatus.DRAFT_UNDO);
            runtimeContainer.setVars(source.getVars());
            instanceStatusList.add(instanceStatus);
        }
        return instanceStatusList;
    }
}
