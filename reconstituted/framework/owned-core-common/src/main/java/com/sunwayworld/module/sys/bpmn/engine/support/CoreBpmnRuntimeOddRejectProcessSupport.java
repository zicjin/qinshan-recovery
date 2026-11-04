package com.sunwayworld.module.sys.bpmn.engine.support;

import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceRuntimeContainerDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnProcBean;
import com.sunwayworld.module.sys.bpmn.diagram.BpmnDiagramHelper;
import com.sunwayworld.module.sys.bpmn.diagram.CommentStatus;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import com.sunwayworld.module.sys.bpmn.engine.CoreBpmnRuntimeSource;
import com.sunwayworld.module.sys.bpmn.engine.cache.BpmnRuntimeCacheProvider;
import com.sunwayworld.module.sys.bpmn.engine.support.AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.dom4j.Document;

public class CoreBpmnRuntimeOddRejectProcessSupport<T extends Auditable<ID>, ID extends Serializable> extends AbstractCoreBpmnRuntimeProcessSupport<T, ID> {
   @Override
   protected void doValidate(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
   }

   @Override
   protected List<CoreBpmnInstanceStatusDTO<ID>> doProcess(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
      sourceList.forEach(s -> s.setCheckPermission(false));
      List<CoreBpmnInstanceStatusDTO<ID>> instanceStatusList = new ArrayList<>();

      for (CoreBpmnRuntimeSource<T, ID> source : sourceList) {
         CoreBpmnInstanceBean instance = BpmnRuntimeCacheProvider.getBpmnRuntimeInstance(source);
         CoreBpmnInstanceStatusDTO<ID> instanceStatus = new CoreBpmnInstanceStatusDTO<>();
         CoreBpmnInstanceRuntimeContainerDTO runtimeContainer = new CoreBpmnInstanceRuntimeContainerDTO();
         instanceStatus.setRuntimeContainer(runtimeContainer);
         runtimeContainer.setVars(source.getVars());
         runtimeContainer.setInitiator(instance.getInitiator());
         runtimeContainer.setProcId(instance.getProcId());
         runtimeContainer.setProcInstId(instance.getId());
         List<CoreBpmnInstanceTaskBean> instanceTaskList = BpmnRuntimeCacheProvider.getBpmnRuntimeInstanceTaskList(source);
         instanceStatus.setStatusCode(instanceTaskList.stream().map(CoreBpmnInstanceTaskBean::getStatusCode).collect(Collectors.joining(",")));
         BpmnRuntimeCacheProvider.getProcessData().addDeleteInstance(instance);
         instanceStatus.setId(source.getId());
         instanceStatus.setProcessStatus(ProcessStatus.DRAFT_REJECTED);
         instanceStatusList.add(instanceStatus);
         BpmnRuntimeCacheProvider.getProcessData().addDeleteBpmnTargetIdList(source.getTargetId());
         if (StringUtils.isEmpty(source.getComment())) {
            source.setComment(I18nHelper.getMessage("GIKAM.BPMN.COMMENT.REJECT"));
         }

         StringBuilder sourceNameBuilder = new StringBuilder();

         for (CoreBpmnInstanceTaskBean instanceTask : instanceTaskList) {
            if (sourceNameBuilder.length() > 0) {
               sourceNameBuilder.append(",");
            }

            sourceNameBuilder.append(instanceTask.getTaskName());
         }

         Document document = this.diagramService.getDocument(instance.getDiagramId());
         String sourceName = sourceNameBuilder.toString();
         String targetName = BpmnDiagramHelper.getStartNodeName(document);
         this.insertComment(instance.getTargetId(), null, CommentStatus.REJECT, source, instance.getProcId(), sourceName, this.getRoute(sourceName, targetName));
         CoreBpmnProcBean proc = this.procService.selectById(instance.getProcId());
         Document diagram = this.diagramService.getDocument(proc.getDiagramId());

         for (CoreBpmnInstanceTaskBean instanceTask : instanceTaskList) {
            this.prepareCallback(
               CallbackCategory.reject, BpmnDiagramHelper.getProcessElementByStatusCode(diagram, instanceTask.getStatusCode()), source.getId()
            );
            this.prepareScript(CallbackCategory.reject, BpmnDiagramHelper.getProcessElementByStatusCode(diagram, instanceTask.getStatusCode()), source.getId());
            BpmnRuntimeCacheProvider.getProcessData().addDeleteInstanceTaskId(instanceTask.getId());
         }
      }

      return instanceStatusList;
   }
}
