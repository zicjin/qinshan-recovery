package com.sunwayworld.module.sys.bpmn.engine.support;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.utils.XmlUtils;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceCandidatorDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskUserBean;
import com.sunwayworld.module.sys.bpmn.diagram.BpmnDiagramHelper;
import com.sunwayworld.module.sys.bpmn.diagram.CommentStatus;
import com.sunwayworld.module.sys.bpmn.engine.CoreBpmnRuntimeSource;
import com.sunwayworld.module.sys.bpmn.engine.cache.BpmnRuntimeCacheProvider;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.dom4j.Element;

public class CoreBpmnRuntimeTransferProcessSupport<T extends Auditable<ID>, ID extends Serializable> extends AbstractCoreBpmnRuntimeProcessSupport<T, ID> {
   @Override
   protected List<CoreBpmnInstanceStatusDTO<ID>> doProcess(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
      List<CoreBpmnInstanceStatusDTO<ID>> instanceStatusList = new ArrayList<>();
      List<CoreBpmnInstanceCandidatorDTO> instanceCandidatorList = BpmnRuntimeCacheProvider.getBpmnRuntimeInstanceCandidatorList();

      for (CoreBpmnRuntimeSource<T, ID> source : sourceList) {
         CoreBpmnInstanceBean instance = BpmnRuntimeCacheProvider.getBpmnRuntimeInstance(source);
         CoreBpmnInstanceTaskBean instanceTask = BpmnRuntimeCacheProvider.getBpmnRuntimeInstanceTask(source);
         Element taskElement = BpmnRuntimeCacheProvider.getBpmnRuntimeTaskElement(source);
         if (BpmnDiagramHelper.isTransferTask(taskElement)) {
            String sourceName = XmlUtils.getAttributeValue(taskElement, "name");
            List<CoreBpmnInstanceCandidatorDTO> candidatorList = instanceCandidatorList.stream()
               .filter(c -> instanceTask.getId().equals(c.getInstTaskId()) && LocalContextHelper.getLoginUserId().equals(c.getCandidatorId()))
               .collect(Collectors.toList());
            List<Long> taskUserIdList = candidatorList.stream().filter(c -> c.getTaskUserId() != null).map(c -> c.getTaskUserId()).collect(Collectors.toList());
            List<Long> taskRoleIdList = candidatorList.stream().filter(c -> c.getTaskRoleId() != null).map(c -> c.getTaskRoleId()).collect(Collectors.toList());
            BpmnRuntimeCacheProvider.getProcessData().addDeleteInstanceTaskRoleId(taskRoleIdList);
            BpmnRuntimeCacheProvider.getProcessData().addDeleteInstanceTaskUserId(taskUserIdList);
            CoreBpmnInstanceTaskUserBean taskUser = new CoreBpmnInstanceTaskUserBean();
            taskUser.setId(ApplicationContextHelper.getNextIdentity());
            taskUser.setInstId(instance.getId());
            taskUser.setInstTaskId(instanceTask.getId());
            taskUser.setUserId(source.getTransferCandidator());
            BpmnRuntimeCacheProvider.getProcessData().addInsertInstanceTaskUser(Arrays.asList(taskUser));
            this.insertComment(
               instance.getTargetId(), taskElement, CommentStatus.TRANSFER, source, instance.getProcId(), sourceName, this.getRoute(sourceName, sourceName)
            );
         }
      }

      return instanceStatusList;
   }

   @Override
   protected void validateBySql() {
   }

   @Override
   protected void updateProcessStatus(String table, List<CoreBpmnInstanceStatusDTO<ID>> statusList) {
   }

   @Override
   protected void doCallback(Map<String, List<ID>> callbackMap) {
   }
}
