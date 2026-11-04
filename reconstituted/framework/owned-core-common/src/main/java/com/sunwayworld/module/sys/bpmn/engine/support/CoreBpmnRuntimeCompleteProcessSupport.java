package com.sunwayworld.module.sys.bpmn.engine.support;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.utils.XmlUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.bpmn.CoreBpmnHelper;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceCandidatorDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceRuntimeContainerDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskBean;
import com.sunwayworld.module.sys.bpmn.diagram.BpmnDiagramHelper;
import com.sunwayworld.module.sys.bpmn.diagram.CommentStatus;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessElementType;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import com.sunwayworld.module.sys.bpmn.diagram.TaskStatus;
import com.sunwayworld.module.sys.bpmn.diagram.attribute.CandidatorFilterStrategy;
import com.sunwayworld.module.sys.bpmn.diagram.attribute.CounterSignStrategy;
import com.sunwayworld.module.sys.bpmn.engine.CoreBpmnRuntimeSource;
import com.sunwayworld.module.sys.bpmn.engine.cache.BpmnRuntimeCacheProvider;
import com.sunwayworld.module.sys.bpmn.engine.support.AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory;
import com.sunwayworld.module.sys.bpmn.exception.BpmnException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreBpmnRuntimeCompleteProcessSupport<T extends Auditable<ID>, ID extends Serializable> extends AbstractCoreBpmnRuntimeProcessSupport<T, ID> {
   private static final Logger logger = LoggerFactory.getLogger(CoreBpmnRuntimeCompleteProcessSupport.class);

   @Override
   protected void doValidate(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
      int i = 0;

      for (int j = sourceList.size(); i < j; i++) {
         CoreBpmnRuntimeSource<T, ID> source = sourceList.get(i);
         Element taskElement = BpmnRuntimeCacheProvider.getBpmnRuntimeTaskElement(source);
         if (BpmnDiagramHelper.isCommentRequiredTask(taskElement) && StringUtils.isEmpty(source.getComment())) {
            throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_DRAFT.TIP.COMMENT_REQUIRED");
         }

         String passValidatorSql = BpmnDiagramHelper.getPassValidatorSql(taskElement);
         if (!StringUtils.isBlank(passValidatorSql)) {
            this.prepareValidateSql(i + 1, passValidatorSql, source.getId());
         }

         this.prepareCallback(CallbackCategory.pass, taskElement, source.getId());
         this.prepareScript(CallbackCategory.pass, taskElement, source.getId());
      }
   }

   @Override
   protected List<CoreBpmnInstanceStatusDTO<ID>> doProcess(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
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
         CoreBpmnInstanceTaskBean instanceTask = BpmnRuntimeCacheProvider.getBpmnRuntimeInstanceTask(source);
         instanceStatus.setStatusCode(instanceTask.getStatusCode());
         Element taskElement = BpmnRuntimeCacheProvider.getBpmnRuntimeTaskElement(source);
         List<String> previousTaskAuditedByList = BpmnRuntimeCacheProvider.getBpmnPreviousTaskAuditedByList(source);
         CounterSignStrategy counterSignStrategy = BpmnDiagramHelper.getCounterSignStrategy(taskElement);
         if (!CounterSignStrategy.none.equals(counterSignStrategy)) {
            List<CoreBpmnInstanceCandidatorDTO> instanceCandidatorList = BpmnRuntimeCacheProvider.getBpmnRuntimeInstanceCandidatorList();
            if (CounterSignStrategy.all.equals(counterSignStrategy)) {
               List<CoreBpmnInstanceCandidatorDTO> instanceTaskCandidatorList = instanceCandidatorList.stream()
                  .filter(a -> instanceTask.getId().equals(a.getInstTaskId()))
                  .collect(Collectors.toList());
               if (CandidatorFilterStrategy.sameDept.equals(BpmnDiagramHelper.getCandidatorFilterStrategy(taskElement))) {
                  String createdByOrgId = (String)CollectionUtils.getValueIgnorecase(source.getVars(), "createdByOrgId");
                  if (!StringUtils.isBlank(createdByOrgId)) {
                     CoreUserService userService = ApplicationContextHelper.getBean(CoreUserService.class);
                     List<CoreUserBean> selectedUserList = userService.selectListByIds(
                        instanceTaskCandidatorList.stream().map(c -> c.getCandidatorId()).collect(Collectors.toList())
                     );
                     instanceTaskCandidatorList.removeIf(
                        c -> selectedUserList.stream().filter(s -> s.getId().equals(c.getCandidatorId())).noneMatch(s -> createdByOrgId.equals(s.getOrgId()))
                     );
                     if (instanceTaskCandidatorList.stream().noneMatch(c -> c.getCandidatorId().equals(LocalContextHelper.getLoginUserId()))) {
                        throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.NOT_AUTHORIZED");
                     }
                  } else {
                     logger.warn("No createdByOrgId column in (" + source.getTableName() + "), ignore candidator filter strategy.");
                  }
               }

               long otherCandidatorQty = instanceTaskCandidatorList.stream()
                  .filter(
                     a -> !LocalContextHelper.getLoginUserId().equals(a.getCandidatorId())
                        && (previousTaskAuditedByList.isEmpty() || !previousTaskAuditedByList.contains(a.getCandidatorId()))
                  )
                  .count();
               if (otherCandidatorQty > 0L) {
                  for (CoreBpmnInstanceCandidatorDTO deleteInstanceTaskCandidator : instanceTaskCandidatorList.stream()
                     .filter(a -> LocalContextHelper.getLoginUserId().equals(a.getCandidatorId()))
                     .collect(Collectors.toList())) {
                     if (deleteInstanceTaskCandidator.getTaskUserId() != null) {
                        BpmnRuntimeCacheProvider.getProcessData().addDeleteInstanceTaskUserId(Arrays.asList(deleteInstanceTaskCandidator.getTaskUserId()));
                     } else if (deleteInstanceTaskCandidator.getTaskRoleId() != null
                        && instanceTaskCandidatorList.stream()
                              .filter(a -> !LocalContextHelper.getLoginUserId().equals(a.getCandidatorId()) && a.getTaskRoleId() != null)
                              .count()
                           == 0L) {
                        BpmnRuntimeCacheProvider.getProcessData().addDeleteInstanceTaskRoleId(Arrays.asList(deleteInstanceTaskCandidator.getTaskRoleId()));
                     }
                  }

                  if (StringUtils.isEmpty(source.getComment())) {
                     source.setComment(I18nHelper.getMessage("GIKAM.BPMN.COMMENT.PASS_COUNTERSIGN"));
                  }

                  instanceStatus.setId(source.getId());
                  instanceStatus.setNextStatusCode(instanceStatus.getStatusCode());
                  instanceStatus.setProcessStatus(ProcessStatus.APPROVE);
                  instanceStatusList.add(instanceStatus);
                  String sourceName = XmlUtils.getAttributeValue(taskElement, "name");
                  this.insertComment(
                     instance.getTargetId(),
                     taskElement,
                     CommentStatus.CS_APPROVE,
                     source,
                     instance.getProcId(),
                     sourceName,
                     this.getRoute(sourceName, sourceName)
                  );
                  BpmnRuntimeCacheProvider.removeBpmnRuntimeCallback(source.getId());
                  BpmnRuntimeCacheProvider.removeBpmnRuntimeScirpt(source.getId());
                  continue;
               }
            } else if (CounterSignStrategy.role.equals(counterSignStrategy)) {
               List<CoreBpmnInstanceCandidatorDTO> instanceTaskCandidatorListx = instanceCandidatorList.stream()
                  .filter(a -> instanceTask.getId().equals(a.getInstTaskId()))
                  .collect(Collectors.toList());
               if (instanceTaskCandidatorListx.stream().anyMatch(c -> c.getTaskUserId() != null)) {
                  throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_DRAFT.TIP.ROLE_COUNTER_SIGN.REQUIRES_ROLE_ONLY");
               }

               long count = instanceTaskCandidatorListx.stream().map(c -> c.getTaskRoleId()).distinct().count();
               if (count > 1L) {
                  List<Long> deleteTaskRoleIdList = instanceTaskCandidatorListx.stream()
                     .filter(c -> LocalContextHelper.getLoginUserId().equals(c.getCandidatorId()))
                     .map(c -> c.getTaskRoleId())
                     .distinct()
                     .collect(Collectors.toList());
                  BpmnRuntimeCacheProvider.getProcessData().addDeleteInstanceTaskRoleId(deleteTaskRoleIdList);
                  if (StringUtils.isEmpty(source.getComment())) {
                     source.setComment(I18nHelper.getMessage("GIKAM.BPMN.COMMENT.PASS_COUNTERSIGN_ROLE"));
                  }

                  instanceStatus.setId(source.getId());
                  instanceStatus.setNextStatusCode(instanceStatus.getStatusCode());
                  instanceStatus.setProcessStatus(ProcessStatus.APPROVE);
                  instanceStatusList.add(instanceStatus);
                  String sourceName = XmlUtils.getAttributeValue(taskElement, "name");
                  this.insertComment(
                     instance.getTargetId(),
                     taskElement,
                     CommentStatus.CS_APPROVE,
                     source,
                     instance.getProcId(),
                     sourceName,
                     this.getRoute(sourceName, sourceName)
                  );
                  BpmnRuntimeCacheProvider.removeBpmnRuntimeCallback(source.getId());
                  BpmnRuntimeCacheProvider.removeBpmnRuntimeScirpt(source.getId());
                  if (count != deleteTaskRoleIdList.size()) {
                     continue;
                  }
               }
            }
         }

         Document diagram = BpmnRuntimeCacheProvider.getBpmnRuntimeDocument(source);
         Pair<ProcessStatus, List<String>> result = this.completeTask(diagram, instance, instanceTask, source);
         instanceStatus.setId(source.getId());
         instanceStatus.setProcessStatus(result.getFirst());
         if (!ProcessStatus.DONE.equals(result.getFirst())) {
            instanceStatus.setNextStatusCode(result.getSecond().stream().collect(Collectors.joining(",")));
         }

         instanceStatusList.add(instanceStatus);
         if (ProcessStatus.DONE.equals(result.getFirst())) {
            if (StringUtils.isEmpty(source.getComment())) {
               source.setComment(I18nHelper.getMessage("GIKAM.BPMN.COMMENT.END"));
            }

            String sourceName = BpmnDiagramHelper.getTaskName(taskElement);
            String targetName = BpmnDiagramHelper.getEndNodeName(diagram);
            this.insertComment(
               instance.getTargetId(), taskElement, CommentStatus.APPROVED, source, instance.getProcId(), sourceName, this.getRoute(sourceName, targetName)
            );
            Element endEventElement = BpmnDiagramHelper.getProcessElement(diagram, ProcessElementType.END_EVENT);
            this.prepareCallback(CallbackCategory.end, endEventElement, source.getId());
            this.prepareScript(CallbackCategory.end, endEventElement, source.getId());
         } else {
            String sourceName = BpmnDiagramHelper.getUserTaskName(taskElement);
            String targetName = this.getElementName(diagram, result.getSecond());
            if (!CounterSignStrategy.none.equals(counterSignStrategy)) {
               if (StringUtils.isEmpty(source.getComment())) {
                  source.setComment(I18nHelper.getMessage("GIKAM.BPMN.COMMENT.END_COUNTERSIGN"));
               }

               this.insertComment(
                  instance.getTargetId(), taskElement, CommentStatus.CS_END, source, instance.getProcId(), sourceName, this.getRoute(sourceName, targetName)
               );
            } else {
               if (StringUtils.isEmpty(source.getComment())) {
                  source.setComment(I18nHelper.getMessage("GIKAM.BPMN.COMMENT.PASS"));
               }

               this.insertComment(
                  instance.getTargetId(), taskElement, CommentStatus.APPROVE, source, instance.getProcId(), sourceName, this.getRoute(sourceName, targetName)
               );
            }
         }

         BpmnRuntimeCacheProvider.getProcessData().addDeleteInstanceTaskId(instanceTask.getId());
         if (ProcessStatus.DONE.equals(result.getFirst())) {
            BpmnRuntimeCacheProvider.getProcessData().addDeleteBpmnTargetIdList(source.getTargetId());
         } else {
            BpmnRuntimeCacheProvider.getProcessData().addInsertOrUpdateBpmnTarget(this.getBpmnTarget(source, result.getFirst()));
         }
      }

      return instanceStatusList;
   }

   private Pair<ProcessStatus, List<String>> completeTask(
      Document diagram, CoreBpmnInstanceBean instance, CoreBpmnInstanceTaskBean task, CoreBpmnRuntimeSource<T, ID> source
   ) {
      CoreBpmnInstanceTaskBean instanceTask = new CoreBpmnInstanceTaskBean();
      instanceTask.setId(task.getId());
      instanceTask.setStatus(TaskStatus.COMPLETED.name());
      BpmnRuntimeCacheProvider.getProcessData().addUpdateInstanceTaskStatus(instanceTask);
      if (task.getId() != null) {
         BpmnRuntimeCacheProvider.getProcessData().addDeleteCandidatorInstanceTaskId(task.getId());
      }

      List<Element> nextTaskElementList = BpmnRuntimeCacheProvider.getNextTask(source).getNextTaskElementList();
      if (!nextTaskElementList.isEmpty()) {
         if (nextTaskElementList.stream().anyMatch(t -> ProcessElementType.END_EVENT.equals(BpmnDiagramHelper.getProcessElementType(t)))) {
            BpmnRuntimeCacheProvider.getProcessData().addDeleteInstance(instance);
            return Pair.of(ProcessStatus.DONE, null);
         }

         this.insertNextInstanceTask(diagram, instance, nextTaskElementList, source);
      } else {
         List<Element> sequenceFlowList = BpmnDiagramHelper.getOutgoingSequenceFlowList(diagram, task.getTaskId());
         if (sequenceFlowList.size() == 0) {
            throw new BpmnException("GIKAM.BPMN.TIP.NEXT_TASK_NOT_FOUND");
         }

         AtomicInteger count = new AtomicInteger(0);
         sequenceFlowList.forEach(v -> {
            Element targetElement = BpmnDiagramHelper.getTargetElement(diagram, v);
            ProcessElementType targetElementType = BpmnDiagramHelper.getProcessElementType(targetElement);
            if (ProcessElementType.EXCLUSIVE_GATEWAY.equals(targetElementType)) {
               targetElement = this.getParallelGateway(source, targetElement);
               targetElementType = BpmnDiagramHelper.getProcessElementType(targetElement);
            }

            if (ProcessElementType.PARALLEL_GATEWAY.equals(targetElementType)) {
               count.set(1);
               if (BpmnDiagramHelper.getIncomingSequenceFlowIdList(diagram, targetElement).size() <= 1) {
                  throw new BpmnException("GIKAM.BPMN.TIP.NEXT_TASK_NOT_FOUND");
               }
            }
         });
         if (count.get() == 0) {
            throw new BpmnException("GIKAM.BPMN.TIP.NEXT_TASK_NOT_FOUND");
         }
      }

      List<String> nextStatusCodeList = nextTaskElementList.stream().map(t -> BpmnDiagramHelper.getUserTaskStatusCode(t)).collect(Collectors.toList());
      return Pair.of(ProcessStatus.APPROVE, nextStatusCodeList);
   }

   private Element getParallelGateway(CoreBpmnRuntimeSource<T, ID> source, Element currentTask) {
      Document diagram = BpmnRuntimeCacheProvider.getBpmnRuntimeDocument(source);

      for (Element outgoingSequenceFlow : BpmnDiagramHelper.getOutgoingSequenceFlowList(diagram, currentTask)) {
         if (CoreBpmnHelper.evalSequenceFlowConditionExpression(outgoingSequenceFlow, source.getVars())) {
            Element targetElement = BpmnDiagramHelper.getTargetElement(diagram, outgoingSequenceFlow);
            ProcessElementType targetElementType = BpmnDiagramHelper.getProcessElementType(targetElement);
            if (ProcessElementType.PARALLEL_GATEWAY.equals(targetElementType)) {
               return targetElement;
            }

            if (ProcessElementType.EXCLUSIVE_GATEWAY.equals(targetElementType)) {
               return this.getParallelGateway(source, targetElement);
            }
         }
      }

      return currentTask;
   }
}
