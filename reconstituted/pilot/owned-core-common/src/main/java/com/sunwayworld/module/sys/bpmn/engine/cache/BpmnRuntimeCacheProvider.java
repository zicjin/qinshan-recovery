package com.sunwayworld.module.sys.bpmn.engine.cache;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.GikamBeanContext;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.SpringUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.utils.XmlUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.bpmn.CoreBpmnHelper;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceCandidatorDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceNextTaskElementDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskHisBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnProcBean;
import com.sunwayworld.module.sys.bpmn.diagram.BpmnDiagramHelper;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessElementType;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import com.sunwayworld.module.sys.bpmn.diagram.attribute.CandidatorFilterStrategy;
import com.sunwayworld.module.sys.bpmn.diagram.attribute.CounterSignStrategy;
import com.sunwayworld.module.sys.bpmn.engine.CoreBpmnRuntimeService;
import com.sunwayworld.module.sys.bpmn.engine.CoreBpmnRuntimeSource;
import com.sunwayworld.module.sys.bpmn.engine.cache.BpmnRuntimeData.BpmnProcessData;
import com.sunwayworld.module.sys.bpmn.exception.BpmnException;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnCommentService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnDiagramService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceTaskService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnProcService;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.dom4j.Document;
import org.dom4j.Element;

public class BpmnRuntimeCacheProvider {
   private static final ThreadLocal<List<BpmnRuntimeData>> BPMN_CACHE = new ThreadLocal<>();

   public static <T extends Auditable<ID>, ID extends Serializable> BpmnRuntimeData<T, ID> getRuntimeData() {
      List<BpmnRuntimeData> dataList = BPMN_CACHE.get();
      if (dataList == null) {
         dataList = new ArrayList<>();
         BPMN_CACHE.set(dataList);
         BpmnRuntimeData<T, ID> runtimeData = new BpmnRuntimeData<>();
         dataList.add(runtimeData);
      }

      return dataList.get(dataList.size() - 1);
   }

   public static BpmnProcessData getProcessData() {
      return getRuntimeData().getProcessData();
   }

   public static <T extends Auditable<ID>, ID extends Serializable> void init(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
      List<BpmnRuntimeData> dataList = BPMN_CACHE.get();
      if (dataList == null) {
         dataList = new ArrayList<>();
         BPMN_CACHE.set(dataList);
      }

      BpmnRuntimeData<T, ID> runtimeData = new BpmnRuntimeData<>();
      dataList.add(runtimeData);
      runtimeData.setSourceList(sourceList);
   }

   public static void remove() {
      List<BpmnRuntimeData> dataList = BPMN_CACHE.get();
      if (dataList != null && dataList.size() > 1) {
         dataList.remove(dataList.size() - 1);
      } else {
         BPMN_CACHE.remove();
      }
   }

   public static <T extends Auditable<ID>, ID extends Serializable> List<CoreBpmnRuntimeSource<T, ID>> getBpmnRuntimeSourceList() {
      BpmnRuntimeData<T, ID> runtimeData = getRuntimeData();
      return runtimeData.getSourceList();
   }

   public static <T extends Auditable<ID>, ID extends Serializable> List<CoreBpmnInstanceBean> getBpmnRuntimeInstanceList() {
      BpmnRuntimeData<T, ID> runtimeData = getRuntimeData();
      List<CoreBpmnInstanceBean> instanceList = runtimeData.getInstanceList();
      if (instanceList == null) {
         CoreBpmnInstanceService instanceService = ApplicationContextHelper.getBean(CoreBpmnInstanceService.class);
         List<String> targetIdList = getBpmnRuntimeSourceList().stream().map(CoreBpmnRuntimeSource::getTargetId).collect(Collectors.toList());
         instanceList = instanceService.getDao().selectListByOneColumnValues(targetIdList, "TARGETID");
         runtimeData.setInstanceList(instanceList);
      }

      return instanceList;
   }

   public static <T extends Auditable<ID>, ID extends Serializable> List<CoreBpmnInstanceTaskBean> getBpmnRuntimeInstanceTaskList() {
      BpmnRuntimeData<T, ID> runtimeData = getRuntimeData();
      List<CoreBpmnInstanceTaskBean> instanceTaskList = runtimeData.getInstanceTaskList();
      if (instanceTaskList == null) {
         instanceTaskList = new ArrayList<>();
         List<CoreBpmnRuntimeSource<T, ID>> sourceList = getBpmnRuntimeSourceList();
         List<CoreBpmnInstanceBean> instanceList = getBpmnRuntimeInstanceList();
         CoreBpmnInstanceTaskService instanceTaskService = ApplicationContextHelper.getBean(CoreBpmnInstanceTaskService.class);
         List<Long> permissionRequiredBpmnInstanceIdList = instanceList.stream()
            .filter(i -> sourceList.stream().anyMatch(s -> s.getTargetId().equals(i.getTargetId()) && s.isCheckPermission()))
            .map(i -> i.getId())
            .collect(Collectors.toList());
         List<Long> permissionIgnoredBpmnInstanceIdList = instanceList.stream()
            .filter(i -> sourceList.stream().anyMatch(s -> s.getTargetId().equals(i.getTargetId()) && !s.isCheckPermission()))
            .map(i -> i.getId())
            .collect(Collectors.toList());
         if (LocalContextHelper.isUserLogin()) {
            if (!permissionRequiredBpmnInstanceIdList.isEmpty()) {
               instanceTaskList.addAll(
                  instanceTaskService.selectAuditableInstanceTaskList(LocalContextHelper.getLoginUserId(), permissionRequiredBpmnInstanceIdList)
               );
            }

            if (!permissionIgnoredBpmnInstanceIdList.isEmpty()) {
               instanceTaskList.addAll(instanceTaskService.selectAuditableInstanceTaskList(null, permissionIgnoredBpmnInstanceIdList));
            }
         } else if (!permissionIgnoredBpmnInstanceIdList.isEmpty()) {
            instanceTaskList.addAll(instanceTaskService.selectAuditableInstanceTaskList(null, permissionIgnoredBpmnInstanceIdList));
         }

         runtimeData.setInstanceTaskList(instanceTaskList);
      }

      return instanceTaskList;
   }

   public static <T extends Auditable<ID>, ID extends Serializable> List<CoreBpmnInstanceTaskBean> getBpmnRuntimeUnlimitedInstanceTaskList() {
      BpmnRuntimeData<T, ID> runtimeData = getRuntimeData();
      List<CoreBpmnInstanceTaskBean> unlimitedInstanceTaskList = runtimeData.getUnlimitedInstanceTaskList();
      if (unlimitedInstanceTaskList == null) {
         List<CoreBpmnInstanceBean> instanceList = getBpmnRuntimeInstanceList();
         CoreBpmnInstanceTaskService instanceTaskService = ApplicationContextHelper.getBean(CoreBpmnInstanceTaskService.class);
         List<Long> unlimitedBpmnInstanceIdList = instanceList.stream().map(i -> i.getId()).collect(Collectors.toList());
         unlimitedInstanceTaskList = instanceTaskService.selectAuditableInstanceTaskList(null, unlimitedBpmnInstanceIdList);
         runtimeData.setUnlimitedInstanceTaskList(unlimitedInstanceTaskList);
      }

      return unlimitedInstanceTaskList;
   }

   public static <T extends Auditable<ID>, ID extends Serializable> List<String> getBpmnPreviousTaskAuditedByList(CoreBpmnRuntimeSource<T, ID> source) {
      BpmnRuntimeData<T, ID> runtimeData = getRuntimeData();
      List<String> previousTaskAuditedByList = runtimeData.getPreviousTaskAuditedByList();
      if (previousTaskAuditedByList == null) {
         Element taskElement = getBpmnRuntimeTaskElement(source);
         previousTaskAuditedByList = new ArrayList<>();
         if (BpmnDiagramHelper.isFilterPreviousCandidatorsTask(taskElement)) {
            List<Element> userTaskElementList = BpmnDiagramHelper.getIncomingUserTaskElementList(getBpmnRuntimeDocument(source), taskElement);
            if (!userTaskElementList.isEmpty()) {
               for (Element userTaskElement : userTaskElementList) {
                  List<String> auditedByList = ApplicationContextHelper.getBean(CoreBpmnCommentService.class)
                     .selectUserTaskAuditedByList(source.getTargetId(), BpmnDiagramHelper.getUserTaskStatusCode(userTaskElement));
                  if (auditedByList != null && auditedByList.contains(LocalContextHelper.getLoginUserId())) {
                     throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.FILTER_PREVIOUS_CANDIDATORS");
                  }

                  previousTaskAuditedByList.addAll(auditedByList);
               }
            }
         }

         runtimeData.setPreviousTaskAuditedByList(previousTaskAuditedByList);
      }

      return previousTaskAuditedByList;
   }

   public static <T extends Auditable<ID>, ID extends Serializable> List<CoreBpmnInstanceCandidatorDTO> getBpmnRuntimeInstanceCandidatorList() {
      BpmnRuntimeData<T, ID> runtimeData = getRuntimeData();
      List<CoreBpmnInstanceCandidatorDTO> instanceCandidatorList = runtimeData.getInstanceCandidatorList();
      if (instanceCandidatorList == null) {
         List<CoreBpmnInstanceBean> instanceList = getBpmnRuntimeInstanceList();
         List<Long> idList = instanceList.stream().map(i -> i.getId()).collect(Collectors.toList());
         CoreBpmnInstanceService instanceService = ApplicationContextHelper.getBean(CoreBpmnInstanceService.class);
         instanceCandidatorList = instanceService.selectInstanceCandidatorList(idList);
         runtimeData.setInstanceCandidatorList(instanceCandidatorList);
      }

      return instanceCandidatorList;
   }

   public static <T extends Auditable<ID>, ID extends Serializable> CoreBpmnInstanceBean getBpmnRuntimeInstance(CoreBpmnRuntimeSource<T, ID> source) {
      List<CoreBpmnInstanceBean> instanceList = getBpmnRuntimeInstanceList();
      return instanceList.stream().filter(i -> i.getTargetId().equals(source.getTargetId())).findAny().orElse(null);
   }

   public static <T extends Auditable<ID>, ID extends Serializable> CoreBpmnInstanceTaskBean getBpmnRuntimeInstanceTask(CoreBpmnRuntimeSource<T, ID> source) {
      CoreBpmnInstanceBean instance = getBpmnRuntimeInstance(source);
      List<CoreBpmnInstanceTaskBean> runtimeTaskList = getBpmnRuntimeInstanceTaskList();
      List<CoreBpmnInstanceTaskBean> instanceTaskList = runtimeTaskList.stream().filter(t -> {
         boolean flag = t.getInstId().equals(instance.getId());
         if (flag && !StringUtils.isEmpty(source.getCurrentStatusCode())) {
            flag = source.getCurrentStatusCode().equals(t.getStatusCode());
         }

         return flag;
      }).collect(Collectors.toList());
      if (instanceTaskList.isEmpty()) {
         throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.NOT_AUTHORIZED");
      } else if (instanceTaskList.size() > 1) {
         throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.MULTIPLE_TASK_ASSIGNED");
      } else {
         return instanceTaskList.get(0);
      }
   }

   public static <T extends Auditable<ID>, ID extends Serializable> List<CoreBpmnInstanceTaskBean> getBpmnRuntimeInstanceTaskList(
      CoreBpmnRuntimeSource<T, ID> source
   ) {
      CoreBpmnInstanceBean instance = getBpmnRuntimeInstance(source);
      List<CoreBpmnInstanceTaskBean> runtimeTaskList = getBpmnRuntimeInstanceTaskList();
      return runtimeTaskList.stream().filter(t -> {
         boolean flag = t.getInstId().equals(instance.getId());
         if (flag && !StringUtils.isEmpty(source.getCurrentStatusCode())) {
            flag = source.getCurrentStatusCode().equals(t.getStatusCode());
         }

         return flag;
      }).collect(Collectors.toList());
   }

   public static <T extends Auditable<ID>, ID extends Serializable> CoreBpmnProcBean getBpmnProc(CoreBpmnRuntimeSource<T, ID> source) {
      BpmnRuntimeData<T, ID> runtimeData = getRuntimeData();
      CoreBpmnProcBean proc = runtimeData.getProcMap().get(source.getId());
      if (proc == null) {
         CoreBpmnProcService procService = ApplicationContextHelper.getBean(CoreBpmnProcService.class);
         if (source.getProcId() != null) {
            proc = procService.selectById(source.getProcId());
         } else {
            CoreBpmnInstanceBean instance = getBpmnRuntimeInstance(source);
            if (instance == null) {
               throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.PROCESS_STATUS_UPDATED");
            }

            proc = procService.selectByIdIfPresent(instance.getProcId());
         }

         runtimeData.getProcMap().put(source.getId(), proc);
      }

      return proc;
   }

   public static <T extends Auditable<ID>, ID extends Serializable> Document getBpmnRuntimeDocument(CoreBpmnRuntimeSource<T, ID> source) {
      BpmnRuntimeData<T, ID> runtimeData = getRuntimeData();
      Document document = runtimeData.getDocumentMap().get(source.getId());
      if (document == null) {
         CoreBpmnProcBean proc = getBpmnProc(source);
         Objects.requireNonNull(proc, "CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.PROCESS_NOT_EXISTS");
         CoreBpmnDiagramService diagramService = ApplicationContextHelper.getBean(CoreBpmnDiagramService.class);
         document = diagramService.getDocument(proc.getDiagramId());
         runtimeData.getDocumentMap().put(source.getId(), document);
      }

      return document;
   }

   public static <T extends Auditable<ID>, ID extends Serializable> Element getBpmnRuntimeTaskElement(CoreBpmnRuntimeSource<T, ID> source) {
      Document diagram = getBpmnRuntimeDocument(source);
      if (isStartTask(source)) {
         return BpmnDiagramHelper.getProcessElement(diagram, ProcessElementType.START_EVENT);
      } else {
         CoreBpmnInstanceTaskBean instanceTask = getBpmnRuntimeInstanceTask(source);
         return BpmnDiagramHelper.getProcessElement(diagram, instanceTask.getTaskId());
      }
   }

   public static <T extends Auditable<ID>, ID extends Serializable> void cacheBpmnRuntimeCallback(String callback, ID id) {
      BpmnRuntimeData<T, ID> runtimeData = getRuntimeData();
      List<ID> idList = runtimeData.getCallbackMap().get(callback);
      if (idList == null) {
         idList = new ArrayList<>();
         runtimeData.getCallbackMap().put(callback, idList);
      }

      if (!idList.contains(id)) {
         idList.add(id);
      }
   }

   public static <T extends Auditable<ID>, ID extends Serializable> Map<String, List<ID>> getBpmnRuntimeCallback() {
      BpmnRuntimeData<T, ID> runtimeData = getRuntimeData();
      return runtimeData.getCallbackMap();
   }

   public static <T extends Auditable<ID>, ID extends Serializable> void removeBpmnRuntimeCallback(ID id) {
      BpmnRuntimeData<T, ID> runtimeData = getRuntimeData();
      Iterator<Entry<String, List<ID>>> iterator = runtimeData.getCallbackMap().entrySet().iterator();

      while (iterator.hasNext()) {
         Entry<String, List<ID>> entry = iterator.next();
         if (entry.getValue().contains(id)) {
            if (entry.getValue().size() == 1) {
               iterator.remove();
            } else {
               entry.getValue().remove(id);
            }
         }
      }
   }

   public static <T extends Auditable<ID>, ID extends Serializable> void cacheBpmnRuntimeScirpt(String scriptName, ID id) {
      BpmnRuntimeData<T, ID> runtimeData = getRuntimeData();
      List<ID> idList = runtimeData.getScriptMap().get(scriptName);
      if (idList == null) {
         idList = new ArrayList<>();
         runtimeData.getScriptMap().put(scriptName, idList);
      }

      if (!idList.contains(id)) {
         idList.add(id);
      }
   }

   public static <T extends Auditable<ID>, ID extends Serializable> Map<String, List<ID>> getBpmnRuntimeScript() {
      BpmnRuntimeData<T, ID> runtimeData = getRuntimeData();
      return runtimeData.getScriptMap();
   }

   public static <T extends Auditable<ID>, ID extends Serializable> void removeBpmnRuntimeScirpt(ID id) {
      BpmnRuntimeData<T, ID> runtimeData = getRuntimeData();
      Iterator<Entry<String, List<ID>>> iterator = runtimeData.getScriptMap().entrySet().iterator();

      while (iterator.hasNext()) {
         Entry<String, List<ID>> entry = iterator.next();
         if (entry.getValue().contains(id)) {
            if (entry.getValue().size() == 1) {
               iterator.remove();
            } else {
               entry.getValue().remove(id);
            }
         }
      }
   }

   public static <T extends Auditable<ID>, ID extends Serializable> void cacheBpmnRuntimeEndScirpt(String scriptName, ID id) {
      BpmnRuntimeData<T, ID> runtimeData = getRuntimeData();
      List<ID> idList = runtimeData.getEndScriptMap().get(scriptName);
      if (idList == null) {
         idList = new ArrayList<>();
         runtimeData.getEndScriptMap().put(scriptName, idList);
      }

      if (!idList.contains(id)) {
         idList.add(id);
      }
   }

   public static <T extends Auditable<ID>, ID extends Serializable> Map<String, List<ID>> getBpmnRuntimeEndScirpt() {
      BpmnRuntimeData<T, ID> runtimeData = getRuntimeData();
      return runtimeData.getEndScriptMap();
   }

   public static <T extends Auditable<ID>, ID extends Serializable> void cacheBpmnRuntimeEndCallback(String endCallback, ID id) {
      BpmnRuntimeData<T, ID> runtimeData = getRuntimeData();
      List<ID> idList = runtimeData.getEndCallbackMap().get(endCallback);
      if (idList == null) {
         idList = new ArrayList<>();
         runtimeData.getEndCallbackMap().put(endCallback, idList);
      }

      if (!idList.contains(id)) {
         idList.add(id);
      }
   }

   public static <T extends Auditable<ID>, ID extends Serializable> void cacheBpmnRuntimeTaskHisMap(Map<ID, CoreBpmnInstanceTaskHisBean> taskHisMap) {
      BpmnRuntimeData<T, ID> runtimeData = getRuntimeData();
      if (!taskHisMap.isEmpty()) {
         runtimeData.setTaskHisMap(taskHisMap);
      }
   }

   public static <T extends Auditable<ID>, ID extends Serializable> Map<ID, CoreBpmnInstanceTaskHisBean> getBpmnRuntimeTaskHisMap() {
      BpmnRuntimeData<T, ID> runtimeData = getRuntimeData();
      return runtimeData.getTaskHisMap();
   }

   public static <T extends Auditable<ID>, ID extends Serializable> Map<String, List<ID>> getBpmnRuntimeEndCallback() {
      BpmnRuntimeData<T, ID> runtimeData = getRuntimeData();
      return runtimeData.getEndCallbackMap();
   }

   public static <T extends Auditable<ID>, ID extends Serializable> void cacheBpmnRuntimeValidateSql(String rawSql, String validateSql) {
      BpmnRuntimeData<T, ID> runtimeData = getRuntimeData();
      List<String> validateSqlList = runtimeData.getValidateSqlMap().get(rawSql);
      if (validateSqlList == null) {
         validateSqlList = new ArrayList<>();
         runtimeData.getValidateSqlMap().put(rawSql, validateSqlList);
      }

      if (!validateSqlList.contains(validateSql)) {
         validateSqlList.add(validateSql);
      }
   }

   public static <T extends Auditable<ID>, ID extends Serializable> Map<String, List<String>> getBpmnRuntimeValidateSql() {
      BpmnRuntimeData<T, ID> runtimeData = getRuntimeData();
      return runtimeData.getValidateSqlMap();
   }

   public static <T extends Auditable<ID>, ID extends Serializable> List<CoreBpmnInstanceNextTaskElementDTO<ID>> getNextTaskList() {
      BpmnRuntimeData<T, ID> runtimeData = getRuntimeData();
      List<CoreBpmnInstanceNextTaskElementDTO<ID>> nextTaskElementList = runtimeData.getNextTaskElementList();
      if (nextTaskElementList == null) {
         nextTaskElementList = new ArrayList<>();
         List<CoreBpmnRuntimeSource<T, ID>> sourceList = getBpmnRuntimeSourceList();
         List<CoreBpmnRuntimeSource<T, ID>> draftSourceList = sourceList.stream()
            .filter(s -> StringUtils.startsWithIgnoreCase(s.getOldItem().getProcessStatus(), ProcessStatus.DRAFT.name()))
            .collect(Collectors.toList());
         List<CoreBpmnRuntimeSource<T, ID>> approveSourceList = sourceList.stream()
            .filter(s -> StringUtils.startsWithIgnoreCase(s.getOldItem().getProcessStatus(), ProcessStatus.APPROVE.name()))
            .collect(Collectors.toList());
         if (!draftSourceList.isEmpty()) {
            CoreBpmnProcService procService = ApplicationContextHelper.getBean(CoreBpmnProcService.class);
            CoreBpmnDiagramService diagramService = ApplicationContextHelper.getBean(CoreBpmnDiagramService.class);
            Object serviceByTable = GikamBeanContext.getServiceByTable(draftSourceList.get(0).getTableName());
            GenericAuditableService<T, ID> service = null;
            if (serviceByTable != null) {
               service = GenericAuditableService.class.isAssignableFrom(serviceByTable.getClass()) ? (GenericAuditableService)serviceByTable : null;
            }

            List<Pair<ID, Long>> pairList = null;
            if (service == null) {
               String beanName = RedisHelper.get("TABLE_SCRIPT_MAPPING_CACHE", draftSourceList.get(0).getTableName());
               if (!StringUtils.isEmpty(beanName) && SpringUtils.getBean(beanName) != null) {
                  Object bean = SpringUtils.getBean(beanName);
                  Method getBpmnProcIdListMethod = ReflectionUtils.findMethod(bean.getClass(), "getBpmnProcIdList", List.class);
                  pairList = (List<Pair<ID, Long>>)ReflectionUtils.invokeMethod(
                     getBpmnProcIdListMethod, bean, draftSourceList.stream().map(s -> s.getOldItem()).collect(Collectors.toList())
                  );
               } else {
                  CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
                  List<T> itemList = draftSourceList.stream().map(s -> s.getOldItem()).collect(Collectors.toList());
                  Map<ID, CoreBpmnProcBean> map = runtimeService.selectRuntimeBpmnProcList(itemList);
                  pairList = map.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue().getId())).collect(Collectors.toList());
               }
            } else {
               pairList = service.getBpmnProcIdList(draftSourceList.stream().map(s -> s.getOldItem()).collect(Collectors.toList()));
            }

            for (CoreBpmnRuntimeSource<T, ID> draftSource : draftSourceList) {
               Long procId = pairList.stream().filter(p -> p.getFirst().equals(draftSource.getOldItem().getId())).findFirst().get().getSecond();
               CoreBpmnProcBean proc = procService.selectById(procId);
               if (proc == null) {
                  throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.PROCESS_NOT_EXISTS");
               }

               Document diagram = diagramService.getDocument(proc.getDiagramId());
               Element startElement = BpmnDiagramHelper.getProcessElement(diagram, ProcessElementType.START_EVENT);
               CoreBpmnInstanceNextTaskElementDTO<ID> nextTaskElement = new CoreBpmnInstanceNextTaskElementDTO<>();
               nextTaskElement.setId(draftSource.getId());
               nextTaskElement.setSourceElement(startElement);
               nextTaskElement.setNextTaskElementList(getNextTaskElementList(draftSource, startElement));
               nextTaskElementList.add(nextTaskElement);
            }
         }

         if (!approveSourceList.isEmpty()) {
            for (CoreBpmnRuntimeSource<T, ID> approveSource : approveSourceList) {
               Element taskElement = getBpmnRuntimeTaskElement(approveSource);
               CoreBpmnInstanceNextTaskElementDTO<ID> nextTaskElement = new CoreBpmnInstanceNextTaskElementDTO<>();
               nextTaskElement.setId(approveSource.getId());
               nextTaskElement.setSourceElement(taskElement);
               nextTaskElement.setNextTaskElementList(getNextTaskElementList(approveSource, taskElement));
               nextTaskElementList.add(nextTaskElement);
            }
         }

         runtimeData.setNextTaskElementList(nextTaskElementList);
      }

      return nextTaskElementList;
   }

   public static <T extends Auditable<ID>, ID extends Serializable> CoreBpmnInstanceNextTaskElementDTO<ID> getNextTask(CoreBpmnRuntimeSource<T, ID> source) {
      List<CoreBpmnInstanceNextTaskElementDTO<ID>> nextTaskElementList = getNextTaskList();
      return nextTaskElementList.parallelStream().filter(t -> t.getId().equals(source.getId())).findAny().orElse(null);
   }

   public static <T extends Auditable<ID>, ID extends Serializable> boolean isLastTaskCandidator(CoreBpmnRuntimeSource<T, ID> source) {
      Element currentElement = getBpmnRuntimeTaskElement(source);
      CounterSignStrategy counterSignStrategy = BpmnDiagramHelper.getCounterSignStrategy(currentElement);
      if (CounterSignStrategy.none.equals(counterSignStrategy)) {
         return true;
      } else {
         List<CoreBpmnInstanceCandidatorDTO> instanceCandidatorList = getBpmnRuntimeInstanceCandidatorList();
         CoreBpmnInstanceTaskBean instanceTask = getBpmnRuntimeInstanceTask(source);
         List<CoreBpmnInstanceCandidatorDTO> candidatorList = instanceCandidatorList.stream()
            .filter(c -> c.getInstTaskId().equals(instanceTask.getId()))
            .collect(Collectors.toList());
         if (candidatorList.isEmpty()) {
            throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.CANDIDATOR_AND_ROLE_NOT_EXISTS");
         } else {
            return CounterSignStrategy.all.equals(counterSignStrategy)
               ? candidatorList.stream().allMatch(c -> LocalContextHelper.getLoginUserId().equals(c.getCandidatorId()))
               : candidatorList.stream().filter(c -> c.getRoleId() != null).count()
                  == candidatorList.stream().filter(c -> c.getRoleId() != null && LocalContextHelper.getLoginUserId().equals(c.getCandidatorId())).count();
         }
      }
   }

   private static <T extends Auditable<ID>, ID extends Serializable> boolean isStartTask(CoreBpmnRuntimeSource<T, ID> source) {
      return StringUtils.startsWithIgnoreCase(source.getOldItem().getProcessStatus(), ProcessStatus.DRAFT.name());
   }

   private static <T extends Auditable<ID>, ID extends Serializable> List<Element> getNextTaskElementList(
      CoreBpmnRuntimeSource<T, ID> source, Element currentTask
   ) {
      Document diagram = getBpmnRuntimeDocument(source);
      if (!StringUtils.isBlank(source.getNextStatusCode())) {
         return Arrays.asList(BpmnDiagramHelper.getProcessElementByStatusCode(diagram, source.getNextStatusCode()));
      } else {
         CounterSignStrategy counterSignStrategy = BpmnDiagramHelper.getCounterSignStrategy(currentTask);
         if (!CounterSignStrategy.none.equals(counterSignStrategy)) {
            List<CoreBpmnInstanceCandidatorDTO> instanceCandidatorList = getBpmnRuntimeInstanceCandidatorList();
            CoreBpmnInstanceTaskBean instanceTask = getBpmnRuntimeInstanceTask(source);
            if (!instanceTask.getTaskId().equals(XmlUtils.getAttributeValue(currentTask, "id"))) {
               return CollectionUtils.emptyList();
            }

            List<String> previousTaskAuditedByList = getBpmnPreviousTaskAuditedByList(source);
            List<CoreBpmnInstanceCandidatorDTO> currentInstanceCandidatorList = instanceCandidatorList.stream()
               .filter(c -> c.getInstTaskId().equals(instanceTask.getId()) && !previousTaskAuditedByList.contains(c.getCandidatorId()))
               .collect(Collectors.toList());
            if (CandidatorFilterStrategy.sameDept.equals(BpmnDiagramHelper.getCandidatorFilterStrategy(currentTask))) {
               String createdByOrgId = (String)CollectionUtils.getValueIgnorecase(source.getVars(), "createdByOrgId");
               if (!StringUtils.isBlank(createdByOrgId)) {
                  CoreUserService userService = ApplicationContextHelper.getBean(CoreUserService.class);
                  List<CoreUserBean> selectedUserList = userService.selectListByIds(
                     currentInstanceCandidatorList.stream().map(c -> c.getCandidatorId()).collect(Collectors.toList())
                  );
                  currentInstanceCandidatorList.removeIf(
                     c -> selectedUserList.stream().filter(s -> s.getId().equals(c.getCandidatorId())).noneMatch(s -> createdByOrgId.equals(s.getOrgId()))
                  );
               }
            }

            if (CounterSignStrategy.all.equals(counterSignStrategy)) {
               if (currentInstanceCandidatorList.stream().anyMatch(c -> !c.getCandidatorId().equals(LocalContextHelper.getLoginUserId()))) {
                  return CollectionUtils.emptyList();
               }
            } else if (CounterSignStrategy.role.equals(counterSignStrategy)) {
               List<Long> roleIdList = currentInstanceCandidatorList.stream()
                  .filter(c -> c.getTaskRoleId() != null && c.getCandidatorId().equals(LocalContextHelper.getLoginUserId()))
                  .map(c -> c.getRoleId())
                  .distinct()
                  .collect(Collectors.toList());
               if (currentInstanceCandidatorList.stream().anyMatch(c -> c.getTaskRoleId() != null && !roleIdList.contains(c.getRoleId()))) {
                  return CollectionUtils.emptyList();
               }
            }
         }

         List<Element> resultList = new ArrayList<>();

         for (Element outgoingSequenceFlow : BpmnDiagramHelper.getOutgoingSequenceFlowList(diagram, currentTask)) {
            if (CoreBpmnHelper.evalSequenceFlowConditionExpression(outgoingSequenceFlow, source.getVars())) {
               Element targetElement = BpmnDiagramHelper.getTargetElement(diagram, outgoingSequenceFlow);
               ProcessElementType targetElementType = BpmnDiagramHelper.getProcessElementType(targetElement);
               if (ProcessElementType.USER_TASK.equals(targetElementType)) {
                  resultList.add(targetElement);
               } else if (ProcessElementType.END_EVENT.equals(targetElementType)) {
                  resultList.add(targetElement);
               } else if (ProcessElementType.PARALLEL_GATEWAY.equals(targetElementType)) {
                  List<Element> incomingSequenceFlowList = BpmnDiagramHelper.getIncomingSequenceFlowList(diagram, targetElement);
                  if (incomingSequenceFlowList.size() == 1) {
                     resultList.addAll(getNextTaskElementList(source, targetElement));
                  } else {
                     CoreBpmnInstanceBean instance = getBpmnRuntimeInstance(source);
                     List<CoreBpmnInstanceTaskBean> instanceTaskCacheList = getBpmnRuntimeUnlimitedInstanceTaskList();
                     List<CoreBpmnInstanceTaskBean> instanceTaskList = instanceTaskCacheList.stream()
                        .filter(i -> i.getInstId().equals(instance.getId()))
                        .collect(Collectors.toList());
                     if (instanceTaskList.size() == 1L
                        && (
                           instanceTaskList.get(0).getTaskId().equals(XmlUtils.getAttributeValue(currentTask, "id"))
                              || ProcessElementType.EXCLUSIVE_GATEWAY.equals(BpmnDiagramHelper.getProcessElementType(currentTask))
                        )) {
                        resultList.addAll(getNextTaskElementList(source, targetElement));
                     }
                  }
               } else if (ProcessElementType.EXCLUSIVE_GATEWAY.equals(targetElementType)) {
                  List<Element> reachableTaskList = getNextTaskElementList(source, targetElement);
                  boolean parallelGateway = isParallelGateway(source, targetElement);
                  if (!parallelGateway && reachableTaskList.size() > 1) {
                     throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.EXCLUSIVE_GATEWAY_OUTGOING_MULTIPLE_TASK");
                  }

                  resultList.addAll(reachableTaskList);
               }
            }
         }

         return resultList;
      }
   }

   private static <T extends Auditable<ID>, ID extends Serializable> boolean isParallelGateway(CoreBpmnRuntimeSource<T, ID> source, Element currentTask) {
      Document diagram = getBpmnRuntimeDocument(source);

      for (Element outgoingSequenceFlow : BpmnDiagramHelper.getOutgoingSequenceFlowList(diagram, currentTask)) {
         if (CoreBpmnHelper.evalSequenceFlowConditionExpression(outgoingSequenceFlow, source.getVars())) {
            Element targetElement = BpmnDiagramHelper.getTargetElement(diagram, outgoingSequenceFlow);
            ProcessElementType targetElementType = BpmnDiagramHelper.getProcessElementType(targetElement);
            if (ProcessElementType.PARALLEL_GATEWAY.equals(targetElementType)) {
               return true;
            }

            if (ProcessElementType.EXCLUSIVE_GATEWAY.equals(targetElementType)) {
               return isParallelGateway(source, targetElement);
            }
         }
      }

      return false;
   }
}
