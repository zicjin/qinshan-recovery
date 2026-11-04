package com.sunwayworld.module.sys.bpmn.diagram;

import com.sunwayworld.framework.exception.UnsupportedException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.jdk.tools.JavaCompilerManager;
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.utils.XmlUtils;
import com.sunwayworld.module.sys.bpmn.CoreBpmnHelper;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnTaskCandidatorsDTO;
import com.sunwayworld.module.sys.bpmn.diagram.attribute.AttachmentStrategy;
import com.sunwayworld.module.sys.bpmn.diagram.attribute.CandidatorFilterStrategy;
import com.sunwayworld.module.sys.bpmn.diagram.attribute.CounterSignStrategy;
import com.sunwayworld.module.sys.bpmn.diagram.attribute.NextCandidatorOptStrategy;
import com.sunwayworld.module.sys.bpmn.engine.CoreBpmnRuntimeSource;
import com.sunwayworld.module.sys.bpmn.exception.BpmnException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.dom4j.Document;
import org.dom4j.Element;

public abstract class BpmnDiagramHelper {
   public static final List<Element> getNextReachableElement(Document diagram, Element currentElement, Map<String, Object> vars) {
      List<Element> resultList = new ArrayList<>();

      for (Element outgoingSequenceFlow : getOutgoingSequenceFlowList(diagram, currentElement)) {
         if (CoreBpmnHelper.evalSequenceFlowConditionExpression(outgoingSequenceFlow, vars)) {
            Element targetElement = getTargetElement(diagram, outgoingSequenceFlow);
            ProcessElementType targetElementType = getProcessElementType(targetElement);
            if (ProcessElementType.USER_TASK.equals(targetElementType)) {
               resultList.add(targetElement);
            } else if (ProcessElementType.END_EVENT.equals(targetElementType)) {
               resultList.add(targetElement);
            } else {
               resultList.addAll(getNextReachableElement(diagram, targetElement, vars));
            }
         }
      }

      return resultList;
   }

   public static final ProcessElementType getProcessElementType(Element processElement) {
      if (processElement == null) {
         return null;
      } else {
         String name = processElement.getName();
         return ProcessElementType.nameOf(name);
      }
   }

   public static final Element getProcessElement(Document diagram, String id) {
      if (StringUtils.isBlank(id)) {
         return null;
      } else {
         Element processElement = getProcessElement(diagram);

         for (Element element : processElement.elements()) {
            if (id.equals(XmlUtils.getAttributeValue(element, "id"))) {
               return element;
            }
         }

         return null;
      }
   }

   public static final boolean isAutoPass(Document diagram) {
      Element processElement = getProcessElement(diagram);
      return "true".equalsIgnoreCase(XmlUtils.getAttributeValue(processElement, "autoPass"));
   }

   public static final Element getProcessElement(Document diagram, ProcessElementType type) {
      Element element = getProcessElement(diagram);
      return element.element(type.getName());
   }

   public static final Element getProcessElementByStatusCode(Document diagram, String statusCode) {
      Element processElement = getProcessElement(diagram);

      for (Element element : processElement.elements()) {
         if (statusCode.equals(getUserTaskStatusCode(element))) {
            return element;
         }
      }

      return null;
   }

   public static final String getStartNodeName(Document diagram) {
      List<Element> startElementList = getProcessElementList(diagram, ProcessElementType.START_EVENT);
      return startElementList.isEmpty() ? I18nHelper.getMessage("CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.START_NODE") : getTaskName(startElementList.get(0));
   }

   public static final String getEndNodeName(Document diagram) {
      List<Element> endElementList = getProcessElementList(diagram, ProcessElementType.END_EVENT);
      return endElementList.isEmpty() ? I18nHelper.getMessage("CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.END_NODE") : getTaskName(endElementList.get(0));
   }

   public static final String getTaskName(Element element) {
      String taskName = getUserTaskName(element);
      if (!StringUtils.isBlank(taskName)) {
         return taskName;
      } else if (ProcessElementType.START_EVENT.getName().equals(element.getName())) {
         return I18nHelper.getMessage("CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.START_NODE");
      } else if (ProcessElementType.EXCLUSIVE_GATEWAY.getName().equals(element.getName())) {
         return I18nHelper.getMessage("CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.EXCLUSIVE_GATEWAY");
      } else if (ProcessElementType.PARALLEL_GATEWAY.getName().equals(element.getName())) {
         return I18nHelper.getMessage("CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.PARALLEL_GATEWAY");
      } else if (ProcessElementType.END_EVENT.getName().equals(element.getName())) {
         return I18nHelper.getMessage("CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.END_NODE");
      } else if (ProcessElementType.USER_TASK.getName().equals(element.getName())) {
         return I18nHelper.getMessage("CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.USER_TASK");
      } else {
         throw new UnsupportedException(element.asXML());
      }
   }

   public static final List<Element> getProcessElementList(Document diagram, ProcessElementType processElementType) {
      Element element = getProcessElement(diagram);
      return element.elements(processElementType.getName());
   }

   public static final List<Element> getTaskList(Document diagram) {
      List<Element> startElementList = getProcessElementList(diagram, ProcessElementType.START_EVENT);
      if (startElementList.size() != 1) {
         return CollectionUtils.emptyList();
      } else {
         Element startElement = startElementList.get(0);
         return getOutgoingTaskList(diagram, startElement);
      }
   }

   public static final List<Element> getOutgoingSequenceFlowList(Document diagram, Element taskElement) {
      List<Element> taskSubOutgoingElementList = taskElement.elements(TaskElementType.outgoing.name());
      return taskSubOutgoingElementList.stream().map(e -> getProcessElement(diagram, e.getStringValue())).collect(Collectors.toList());
   }

   public static final List<Element> getOutgoingSequenceFlowList(Document diagram, String taskId) {
      Element taskElement = getProcessElement(diagram, taskId);
      return getOutgoingSequenceFlowList(diagram, taskElement);
   }

   public static final List<String> getOutgoingSequenceFlowIdList(Document diagram, Element taskElement) {
      List<Element> taskSubOutgoingElementList = taskElement.elements(TaskElementType.outgoing.name());
      return taskSubOutgoingElementList.stream().map(e -> e.getStringValue()).collect(Collectors.toList());
   }

   public static final List<String> getOutgoingSequenceFlowIdList(Document diagram, String taskId) {
      Element taskElement = getProcessElement(diagram, taskId);
      return getOutgoingSequenceFlowIdList(diagram, taskElement);
   }

   public static final boolean isLastUserTask(Document diagram, Element userTaskElement) {
      List<Element> outgoingSequenceFlowList = getOutgoingSequenceFlowList(diagram, userTaskElement);
      if (outgoingSequenceFlowList.isEmpty()) {
         return true;
      } else if (outgoingSequenceFlowList.size() > 1) {
         return false;
      } else {
         Element sequenceFlow = outgoingSequenceFlowList.get(0);
         Element targetElement = getTargetElement(diagram, sequenceFlow);
         ProcessElementType targetType = getProcessElementType(targetElement);
         return ProcessElementType.END_EVENT.equals(targetType);
      }
   }

   public static final List<Element> getIncomingSequenceFlowList(Document diagram, String taskId) {
      Element taskElement = getProcessElement(diagram, taskId);
      return getIncomingSequenceFlowList(diagram, taskElement);
   }

   public static final List<Element> getIncomingSequenceFlowList(Document diagram, Element taskElement) {
      List<Element> taskSubIncomingElementList = taskElement.elements(TaskElementType.incoming.name());
      return taskSubIncomingElementList.stream().map(e -> getProcessElement(diagram, e.getStringValue())).collect(Collectors.toList());
   }

   public static final List<String> getIncomingSequenceFlowIdList(Document diagram, Element taskElement) {
      List<Element> taskSubIncomingElementList = taskElement.elements(TaskElementType.incoming.name());
      return taskSubIncomingElementList.stream().map(e -> e.getStringValue()).collect(Collectors.toList());
   }

   public static final List<Element> getIncomingUserTaskElementList(Document diagram, Element taskElement) {
      List<Element> userTaskElementList = new ArrayList<>();

      for (Element sequenceFlow : getIncomingSequenceFlowList(diagram, taskElement)) {
         Element sourceElement = getSourceElement(diagram, sequenceFlow);
         if (sourceElement != null) {
            ProcessElementType sourceType = getProcessElementType(sourceElement);
            if (ProcessElementType.USER_TASK.equals(sourceType) || ProcessElementType.START_EVENT.equals(sourceType)) {
               userTaskElementList.add(sourceElement);
            } else if (ProcessElementType.PARALLEL_GATEWAY.equals(sourceType)
               || ProcessElementType.EXCLUSIVE_GATEWAY.equals(sourceType)
               || ProcessElementType.SEQUENCE_FLOW.equals(sourceType)) {
               userTaskElementList.addAll(getIncomingUserTaskElementList(diagram, sourceElement));
            }
         }
      }

      return userTaskElementList;
   }

   public static final List<String> getIncomingSequenceFlowIdList(Document diagram, String taskId) {
      Element taskElement = getProcessElement(diagram, taskId);
      return getIncomingSequenceFlowIdList(diagram, taskElement);
   }

   public static final Element getTargetElement(Document diagram, String sequenceFlowId) {
      Element element = getProcessElement(diagram, sequenceFlowId);
      return getTargetElement(diagram, element);
   }

   public static final Element getTargetElement(Document diagram, Element sequenceFlowElement) {
      String targetId = XmlUtils.getAttributeValue(sequenceFlowElement, "targetRef");
      return getProcessElement(diagram, targetId);
   }

   public static final Element getSourceElement(Document diagram, String sequenceFlowId) {
      Element element = getProcessElement(diagram, sequenceFlowId);
      return getSourceElement(diagram, element);
   }

   public static final Element getSourceElement(Document diagram, Element sequenceFlowElement) {
      String targetId = XmlUtils.getAttributeValue(sequenceFlowElement, "sourceRef");
      return getProcessElement(diagram, targetId);
   }

   public static final String getConditionExpression(Document diagram, String sequenceFlowId) {
      Element sequenceFlowElement = getProcessElement(diagram, sequenceFlowId);
      return getConditionExpression(sequenceFlowElement);
   }

   public static final String getConditionExpression(Element sequenceFlowElement) {
      Element conditionExpressionElement = sequenceFlowElement.element(SequenceElementType.conditionExpression.name());
      return conditionExpressionElement == null ? null : conditionExpressionElement.getStringValue();
   }

   public static final boolean isUserTaskAssigneeInitiator(Element userTask) {
      return XmlUtils.getAttributeValue(userTask, "initiator") != null;
   }

   public static final String getUserTaskAssigneeDynamic(Element userTask) {
      return XmlUtils.getAttributeValue(userTask, "dynamic");
   }

   public static final String getUserTaskAssigneeSelectBySql(Element userTask) {
      return XmlUtils.getAttributeValue(userTask, "selectBySql");
   }

   public static final String getUserTaskAssigneeFilterBySql(Element userTask) {
      return XmlUtils.getAttributeValue(userTask, "filterBySql");
   }

   public static final boolean isUserTaskPassNotifiedInitiator(Element userTask) {
      return XmlUtils.getAttributeValue(userTask, "notifiedInitiator") != null;
   }

   public static final String getUserTaskPassNotifiedDynamic(Element userTask) {
      return XmlUtils.getAttributeValue(userTask, "notifiedDynamic");
   }

   public static final String getUserTaskPassNotifiedSelectBySql(Element userTask) {
      return XmlUtils.getAttributeValue(userTask, "notifiedSelectBySql");
   }

   public static final String getUserTaskPassNotifiedFilterBySql(Element userTask) {
      return XmlUtils.getAttributeValue(userTask, "notifiedFilterBySql");
   }

   public static final String getUserTaskName(Element userTask) {
      return XmlUtils.getAttributeValue(userTask, "name");
   }

   public static final Map<String, String> getUserTaskCandidatorValue(Element userTask) {
      Map<String, String> map = new HashMap<>();
      Element candidateUsersElement = userTask.element(TaskElementType.candidateUsersElements.name());
      if (candidateUsersElement != null) {
         Element propertiesElement = candidateUsersElement.element("properties");
         propertiesElement.elements("property").forEach(e -> {
            String var10000 = map.put(XmlUtils.getAttributeValue(e, "value"), XmlUtils.getAttributeValue(e, "name"));
         });
      }

      return map;
   }

   public static final Map<Long, String> getUserTaskRoleValue(Element userTask) {
      Map<Long, String> map = new HashMap<>();
      Element candidateUsersElement = userTask.element(TaskElementType.candidateRolesElements.name());
      if (candidateUsersElement != null) {
         Element propertiesElement = candidateUsersElement.element("properties");
         propertiesElement.elements("property").forEach(e -> {
            String var10000 = map.put(NumberUtils.parseLong(XmlUtils.getAttributeValue(e, "value")), XmlUtils.getAttributeValue(e, "name"));
         });
      }

      return map;
   }

   public static final Map<String, String> getUserTaskPassNotifiedUserMap(Element userTask) {
      Map<String, String> map = new HashMap<>();
      Element candidateUsersElement = userTask.element(TaskElementType.passNotifiedUsersElements.name());
      if (candidateUsersElement != null) {
         Element propertiesElement = candidateUsersElement.element("properties");
         propertiesElement.elements("property").forEach(e -> {
            String var10000 = map.put(XmlUtils.getAttributeValue(e, "value"), XmlUtils.getAttributeValue(e, "name"));
         });
      }

      return map;
   }

   public static final Map<Long, String> getUserTaskPassNotifiedRoleMap(Element userTask) {
      Map<Long, String> map = new HashMap<>();
      Element candidateUsersElement = userTask.element(TaskElementType.passNotifiedRolesElements.name());
      if (candidateUsersElement != null) {
         Element propertiesElement = candidateUsersElement.element("properties");
         propertiesElement.elements("property").forEach(e -> {
            String var10000 = map.put(NumberUtils.parseLong(XmlUtils.getAttributeValue(e, "value")), XmlUtils.getAttributeValue(e, "name"));
         });
      }

      return map;
   }

   public static final Map<String, String> getUserTaskRejectNotifiedUserMap(Element userTask) {
      Map<String, String> map = new HashMap<>();
      Element candidateUsersElement = userTask.element(TaskElementType.rejectNotifiedUsersElements.name());
      if (candidateUsersElement != null) {
         Element propertiesElement = candidateUsersElement.element("properties");
         propertiesElement.elements("property").forEach(e -> {
            String var10000 = map.put(XmlUtils.getAttributeValue(e, "value"), XmlUtils.getAttributeValue(e, "name"));
         });
      }

      return map;
   }

   public static final Map<Long, String> getUserTaskRejectNotifiedRoleMap(Element userTask) {
      Map<Long, String> map = new HashMap<>();
      Element candidateUsersElement = userTask.element(TaskElementType.rejectNotifiedRolesElements.name());
      if (candidateUsersElement != null) {
         Element propertiesElement = candidateUsersElement.element("properties");
         propertiesElement.elements("property").forEach(e -> {
            String var10000 = map.put(NumberUtils.parseLong(XmlUtils.getAttributeValue(e, "value")), XmlUtils.getAttributeValue(e, "name"));
         });
      }

      return map;
   }

   public static final String getUserTaskStatusCode(Element userTask) {
      return XmlUtils.getAttributeValue(userTask, "statusCode");
   }

   public static final boolean isAuthRequiredUserTask(Element userTask) {
      return "true".equalsIgnoreCase(XmlUtils.getAttributeValue(userTask, "authRequired"));
   }

   public static final boolean isCommentRequiredTask(Element startEventOrUserTask) {
      return "true".equalsIgnoreCase(XmlUtils.getAttributeValue(startEventOrUserTask, "commentRequired"));
   }

   public static final boolean isFilterPreviousCandidatorsTask(Element userTask) {
      return "true".equalsIgnoreCase(XmlUtils.getAttributeValue(userTask, "filterPreviousCandidators"));
   }

   public static final boolean isTransferTask(Element userTask) {
      return "true".equalsIgnoreCase(XmlUtils.getAttributeValue(userTask, "transfer"));
   }

   public static final AttachmentStrategy getAttachmentStrategy(Element startEventOrUserTask) {
      String attachmentStrategy = XmlUtils.getAttributeValue(startEventOrUserTask, "attachmentStrategy");
      return StringUtils.isBlank(attachmentStrategy) ? AttachmentStrategy.none : AttachmentStrategy.valueOf(attachmentStrategy);
   }

   public static final CounterSignStrategy getCounterSignStrategy(Element userTask) {
      String counterSignStrategy = XmlUtils.getAttributeValue(userTask, "counterSignStrategy");
      return StringUtils.isBlank(counterSignStrategy) ? CounterSignStrategy.none : CounterSignStrategy.valueOf(counterSignStrategy);
   }

   public static final NextCandidatorOptStrategy getNextCandidatorOptStrategy(Element startEventOrUserTask) {
      String nextCandidatorOptStrategy = XmlUtils.getAttributeValue(startEventOrUserTask, "nextCandidatorOptStrategy");
      return StringUtils.isBlank(nextCandidatorOptStrategy) ? NextCandidatorOptStrategy.none : NextCandidatorOptStrategy.valueOf(nextCandidatorOptStrategy);
   }

   public static final CandidatorFilterStrategy getCandidatorFilterStrategy(Element userTask) {
      String candidatorFilterStrategy = XmlUtils.getAttributeValue(userTask, "candidatorFilterStrategy");
      return StringUtils.isBlank(candidatorFilterStrategy) ? CandidatorFilterStrategy.none : CandidatorFilterStrategy.valueOf(candidatorFilterStrategy);
   }

   public static final String getPassValidatorSql(Element startEventOrUserTask) {
      return XmlUtils.getAttributeValue(startEventOrUserTask, "passValidator");
   }

   public static final String getRejectValidatorSql(Element userTask) {
      return XmlUtils.getAttributeValue(userTask, "rejectValidator");
   }

   public static final String getPassCallback(Element startEventOrUserTask) {
      return XmlUtils.getAttributeValue(startEventOrUserTask, "passCallback");
   }

   public static final String getEndCallback(Element startEventOrUserTask) {
      return XmlUtils.getAttributeValue(startEventOrUserTask, "endCallback");
   }

   public static final String getRejectCallback(Element userTask) {
      return XmlUtils.getAttributeValue(userTask, "rejectCallback");
   }

   public static List<String> getPreviousStatusCodeList(Document document, Element element) {
      List<String> statusCodeList = new ArrayList<>();
      pickPreviousStatusCodeList(statusCodeList, document, element);
      return statusCodeList.stream().distinct().collect(Collectors.toList());
   }

   public static final <T extends Auditable<ID>, ID extends Serializable> CoreBpmnTaskCandidatorsDTO getUserTaskCandidators(
      CoreBpmnRuntimeSource<T, ID> source, Element userTaskElement, String initiator
   ) {
      return getUserTaskCandidators(userTaskElement, source.getVars(), initiator);
   }

   public static final <T extends Auditable<ID>, ID extends Serializable> CoreBpmnTaskCandidatorsDTO getUserTaskCandidators(
      Element userTaskElement, Map<String, Object> vars, String initiator
   ) {
      List<String> candidatorIdList = new ArrayList<>();
      List<Long> candidateRoleIdList = new ArrayList<>();
      if (isUserTaskAssigneeInitiator(userTaskElement)) {
         candidatorIdList.add(initiator);
      }

      String dynamic = getUserTaskAssigneeDynamic(userTaskElement);
      if (!StringUtils.isEmpty(dynamic)) {
         String[] dynamicValues = StringUtils.split(dynamic, ",");

         for (String value : dynamicValues) {
            if (StringUtils.startsWith(value, "u:")) {
               String user = (String)vars.get(StringUtils.removeStart(value, "u:"));
               if (user == null) {
                  throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.DYNAMIC_USER_NOT_EXISTS");
               }

               candidatorIdList.add(user);
            } else if (StringUtils.startsWith(value, "r:")) {
               String role = ObjectUtils.toString(vars.get(StringUtils.removeStart(value, "r:")));
               if (StringUtils.isEmpty(role)) {
                  throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.DYNAMIC_ROLE_NOT_EXISTS");
               }

               candidateRoleIdList.add(NumberUtils.parseLong(role));
            }
         }
      }

      Map<String, String> userTaskCandidatorMap = getUserTaskCandidatorValue(userTaskElement);
      if (!userTaskCandidatorMap.isEmpty()) {
         candidatorIdList.addAll(userTaskCandidatorMap.keySet());
      }

      Map<Long, String> userTaskRoleMap = getUserTaskRoleValue(userTaskElement);
      if (!userTaskRoleMap.isEmpty()) {
         candidateRoleIdList.addAll(userTaskRoleMap.keySet());
      }

      CoreBpmnTaskCandidatorsDTO candidators = new CoreBpmnTaskCandidatorsDTO();
      candidators.setCandidatorIdList(candidatorIdList.stream().distinct().collect(Collectors.toList()));
      candidators.setCandidateRoleIdList(candidateRoleIdList.stream().distinct().collect(Collectors.toList()));
      return candidators;
   }

   public static final List<Element> getActivatedElementList(Document diagram, List<String> approvedTaskIdList, Map<String, Object> vars) {
      List<Element> activatedElementList = new ArrayList<>();
      Element startElement = getProcessElement(diagram, ProcessElementType.START_EVENT);
      activatedElementList.add(startElement);
      Map<String, Element> activatedElementMap = getActivatedElementMap(diagram, startElement, approvedTaskIdList, vars);
      activatedElementList.addAll(activatedElementMap.values());
      return activatedElementList;
   }

   public static final String getPassScriptName(Element userTask) {
      String passScriptCode = getPassScriptCode(userTask);
      return StringUtils.isEmpty(passScriptCode) ? "" : JavaCompilerManager.getClassName(passScriptCode);
   }

   public static final String getPassScriptCode(Element userTask) {
      return XmlUtils.getAttributeValue(userTask, "passScript");
   }

   public static final String getRejectScriptName(Element userTask) {
      String passScriptCode = getRejectScriptCode(userTask);
      return StringUtils.isEmpty(passScriptCode) ? "" : JavaCompilerManager.getClassName(passScriptCode);
   }

   public static final String getRejectScriptCode(Element userTask) {
      return XmlUtils.getAttributeValue(userTask, "rejectScript");
   }

   public static final String getEndScriptName(Element userTask) {
      String passScriptCode = getEndScriptCode(userTask);
      return StringUtils.isEmpty(passScriptCode) ? "" : JavaCompilerManager.getClassName(passScriptCode);
   }

   public static final String getEndScriptCode(Element userTask) {
      return XmlUtils.getAttributeValue(userTask, "endScript");
   }

   private static final Map<String, Element> getActivatedElementMap(Document diagram, Element source, List<String> approvedTaskIdList, Map<String, Object> vars) {
      Map<String, Element> nextActivatedElementMap = new HashMap<>();

      for (Element outgoingSequenceFlow : getOutgoingSequenceFlowList(diagram, source)) {
         if (CoreBpmnHelper.evalSequenceFlowConditionExpression(outgoingSequenceFlow, vars)) {
            Element targetElement = getTargetElement(diagram, outgoingSequenceFlow);
            String targetId = XmlUtils.getAttributeValue(targetElement, "id");
            ProcessElementType targetElementType = getProcessElementType(targetElement);
            boolean activated = false;
            if (!ProcessElementType.USER_TASK.equals(targetElementType) && !ProcessElementType.END_EVENT.equals(targetElementType)) {
               if (!nextActivatedElementMap.containsValue(targetElement)) {
                  Map<String, Element> activatedElementMap = getActivatedElementMap(diagram, targetElement, approvedTaskIdList, vars);
                  if (!activatedElementMap.isEmpty()) {
                     nextActivatedElementMap.put(targetId, targetElement);
                     activated = true;
                     nextActivatedElementMap.putAll(activatedElementMap);
                  }
               }
            } else if (approvedTaskIdList.contains(targetId)) {
               activated = true;
               nextActivatedElementMap.put(targetId, targetElement);
            }

            if (activated) {
               nextActivatedElementMap.put(XmlUtils.getAttributeValue(outgoingSequenceFlow, "id"), outgoingSequenceFlow);
            }
         }
      }

      if (!nextActivatedElementMap.isEmpty()) {
         for (Element activatedElement : new ArrayList<>(nextActivatedElementMap.values())) {
            ProcessElementType activatedElementType = getProcessElementType(activatedElement);
            if (ProcessElementType.USER_TASK.equals(activatedElementType)) {
               nextActivatedElementMap.putAll(getActivatedElementMap(diagram, activatedElement, approvedTaskIdList, vars));
            }
         }
      }

      return nextActivatedElementMap;
   }

   private static final Element getProcessElement(Document diagram) {
      return diagram.getRootElement().element("process");
   }

   private static void pickPreviousStatusCodeList(List<String> pickedStatusCodeList, Document document, Element element) {
      for (Element sequenceFlowElement : getIncomingSequenceFlowList(document, element)) {
         Element source = getSourceElement(document, sequenceFlowElement);
         ProcessElementType type = getProcessElementType(source);
         if (ProcessElementType.USER_TASK.equals(type) || ProcessElementType.START_EVENT.equals(type)) {
            String statusCode = getUserTaskStatusCode(source);
            if (pickedStatusCodeList.contains(statusCode)) {
               continue;
            }

            pickedStatusCodeList.add(getUserTaskStatusCode(source));
         }

         if (!ProcessElementType.START_EVENT.equals(type)) {
            pickPreviousStatusCodeList(pickedStatusCodeList, document, source);
         }
      }
   }

   private static List<Element> getOutgoingTaskList(Document diagram, Element sourceElement) {
      List<Element> outgoingTaskList = new ArrayList<>();
      List<Element> outgoingSequenceFlowList = getOutgoingSequenceFlowList(diagram, sourceElement);
      int pos = 0;

      for (int i = outgoingSequenceFlowList.size() - 1; i >= 0; i--) {
         Element outgoingSequenceFlow = outgoingSequenceFlowList.get(i);
         Element targetElement = getTargetElement(diagram, outgoingSequenceFlow);
         if (targetElement != null) {
            ProcessElementType targetElementType = getProcessElementType(targetElement);
            if (ProcessElementType.USER_TASK.equals(targetElementType)) {
               outgoingTaskList.add(pos, targetElement);
               List<Element> nextOutgoingTaskList = getOutgoingTaskList(diagram, targetElement);
               if (!nextOutgoingTaskList.isEmpty()) {
                  nextOutgoingTaskList.removeIf(t -> outgoingTaskList.stream().anyMatch(o -> equalsTask(o, t)));
                  if (!nextOutgoingTaskList.isEmpty()) {
                     outgoingTaskList.addAll(pos + 1, nextOutgoingTaskList);
                  }
               }

               pos++;
            } else if (!ProcessElementType.END_EVENT.equals(targetElementType)) {
               List<Element> nextOutgoingTaskList = getOutgoingTaskList(diagram, targetElement);
               if (!nextOutgoingTaskList.isEmpty()) {
                  nextOutgoingTaskList.removeIf(t -> outgoingTaskList.stream().anyMatch(o -> equalsTask(o, t)));
                  if (!nextOutgoingTaskList.isEmpty()) {
                     outgoingTaskList.addAll(pos, nextOutgoingTaskList);
                     pos++;
                  }
               }
            }
         }
      }

      return outgoingTaskList;
   }

   private static boolean equalsTask(Element task1, Element task2) {
      return task1 == task2 ? true : Objects.equals(task1.getUniquePath(), task2.getUniquePath());
   }
}
