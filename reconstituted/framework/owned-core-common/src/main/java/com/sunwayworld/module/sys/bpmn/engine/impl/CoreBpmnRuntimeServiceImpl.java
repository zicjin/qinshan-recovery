package com.sunwayworld.module.sys.bpmn.engine.impl;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.executor.ExecutorHelper;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.bpmn.CoreBpmnHelper;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceNextTaskElementDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskHisBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskHisCandidatorDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskRoleBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskUserBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnProcBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnTargetBean;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import com.sunwayworld.module.sys.bpmn.engine.CoreBpmnRuntimeService;
import com.sunwayworld.module.sys.bpmn.engine.CoreBpmnRuntimeSource;
import com.sunwayworld.module.sys.bpmn.engine.cache.BpmnRuntimeCacheProvider;
import com.sunwayworld.module.sys.bpmn.engine.cache.BpmnRuntimeData.BpmnProcessData;
import com.sunwayworld.module.sys.bpmn.engine.support.CoreBpmnRuntimeCompleteProcessSupport;
import com.sunwayworld.module.sys.bpmn.engine.support.CoreBpmnRuntimeEndProcessSupport;
import com.sunwayworld.module.sys.bpmn.engine.support.CoreBpmnRuntimeOddRejectProcessSupport;
import com.sunwayworld.module.sys.bpmn.engine.support.CoreBpmnRuntimeRejectProcessSupport;
import com.sunwayworld.module.sys.bpmn.engine.support.CoreBpmnRuntimeStartProcessSupport;
import com.sunwayworld.module.sys.bpmn.engine.support.CoreBpmnRuntimeTransferProcessSupport;
import com.sunwayworld.module.sys.bpmn.engine.support.CoreBpmnRuntimeUndoProcessSupport;
import com.sunwayworld.module.sys.bpmn.engine.support.CoreBpmnRuntimeWithdrawProcessSupport;
import com.sunwayworld.module.sys.bpmn.exception.BpmnException;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnCommentService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceTaskHisService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceTaskRoleService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceTaskService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceTaskUserService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnProcService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnTargetService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreBpmnRuntimeServiceImpl implements CoreBpmnRuntimeService {
   @Autowired
   private CoreBpmnProcService procService;
   @Autowired
   private CoreBpmnInstanceService instanceService;
   @Autowired
   private CoreBpmnInstanceTaskService instanceTaskService;
   @Autowired
   private CoreBpmnInstanceTaskUserService instanceTaskUserService;
   @Autowired
   private CoreBpmnInstanceTaskRoleService instanceTaskRoleService;
   @Autowired
   private CoreBpmnCommentService commentService;
   @Autowired
   private CoreBpmnTargetService bpmnTargetService;
   @Autowired
   private CoreBpmnInstanceTaskHisService taskHisService;

   @Transactional(
      timeout = 300
   )
   @Override
   public <T extends Auditable<ID>, ID extends Serializable> List<CoreBpmnInstanceStatusDTO<ID>> startProcess(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
      CoreBpmnRuntimeStartProcessSupport<T, ID> processSupport = new CoreBpmnRuntimeStartProcessSupport<>();
      return processSupport.apply(sourceList);
   }

   @Transactional(
      timeout = 300
   )
   @Override
   public <T extends Auditable<ID>, ID extends Serializable> List<CoreBpmnInstanceStatusDTO<ID>> withdrawProcess(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
      CoreBpmnRuntimeWithdrawProcessSupport<T, ID> processSupport = new CoreBpmnRuntimeWithdrawProcessSupport<>();
      return processSupport.apply(sourceList);
   }

   @Transactional(
      timeout = 300
   )
   @Override
   public <T extends Auditable<ID>, ID extends Serializable> List<CoreBpmnInstanceStatusDTO<ID>> completeTask(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
      CoreBpmnRuntimeCompleteProcessSupport<T, ID> processSupport = new CoreBpmnRuntimeCompleteProcessSupport<>();
      return processSupport.apply(sourceList);
   }

   @Transactional(
      timeout = 300
   )
   @Override
   public <T extends Auditable<ID>, ID extends Serializable> void transferTask(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
      CoreBpmnRuntimeTransferProcessSupport<T, ID> processSupport = new CoreBpmnRuntimeTransferProcessSupport<>();
      processSupport.apply(sourceList);
   }

   @Transactional(
      timeout = 300
   )
   @Override
   public <T extends Auditable<ID>, ID extends Serializable> List<CoreBpmnInstanceStatusDTO<ID>> endTask(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
      CoreBpmnRuntimeEndProcessSupport<T, ID> processSupport = new CoreBpmnRuntimeEndProcessSupport<>();
      return processSupport.apply(sourceList);
   }

   @Transactional(
      timeout = 300
   )
   @Override
   public <T extends Auditable<ID>, ID extends Serializable> List<CoreBpmnInstanceStatusDTO<ID>> undo(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
      CoreBpmnRuntimeUndoProcessSupport<T, ID> processSupport = new CoreBpmnRuntimeUndoProcessSupport<>();
      return processSupport.apply(sourceList);
   }

   @Transactional(
      timeout = 300
   )
   @Override
   public <T extends Auditable<ID>, ID extends Serializable> List<CoreBpmnInstanceStatusDTO<ID>> rejectTask(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
      CoreBpmnRuntimeRejectProcessSupport<T, ID> processSupport = new CoreBpmnRuntimeRejectProcessSupport<>();
      return processSupport.apply(sourceList);
   }

   @Transactional(
      timeout = 300
   )
   @Override
   public <T extends Auditable<ID>, ID extends Serializable> List<CoreBpmnInstanceStatusDTO<ID>> oddRejectTask(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
      CoreBpmnRuntimeOddRejectProcessSupport<T, ID> processSupport = new CoreBpmnRuntimeOddRejectProcessSupport<>();
      return processSupport.apply(sourceList);
   }

   @Override
   public <T extends Auditable<ID>, ID extends Serializable> List<CoreBpmnInstanceNextTaskElementDTO<ID>> getNextTaskList(
      List<CoreBpmnRuntimeSource<T, ID>> sourceList
   ) {
      List var2;
      try {
         BpmnRuntimeCacheProvider.init(sourceList);
         var2 = BpmnRuntimeCacheProvider.getNextTaskList();
      } finally {
         BpmnRuntimeCacheProvider.remove();
      }

      return var2;
   }

   @Override
   public <T extends Auditable<ID>, ID extends Serializable> Map<ID, CoreBpmnProcBean> selectRuntimeBpmnProcList(List<T> itemList) {
      if (itemList.isEmpty()) {
         return CollectionUtils.emptyMap();
      } else {
         List<T> rawItemList = new ArrayList<>();
         List<T> approveItemList = new ArrayList<>();

         for (T item : itemList) {
            if (StringUtils.startsWithIgnoreCase(item.getProcessStatus(), ProcessStatus.DRAFT.name())) {
               rawItemList.add(item);
            } else if (StringUtils.startsWithIgnoreCase(item.getProcessStatus(), ProcessStatus.APPROVE.name())) {
               approveItemList.add(item);
            }
         }

         Map<ID, CoreBpmnProcBean> runtimeMap = new HashMap<>();
         if (!rawItemList.isEmpty()) {
            String tableName = PersistableMetadataHelper.getTableName(itemList.get(0).getClass());
            List<CoreBpmnProcBean> procList = this.procService.selectRuntimeBpmnProcList(tableName, LocalContextHelper.getLoginOrgId());
            if (procList.isEmpty()) {
               throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.PROCESS_NOT_EXISTS", I18nHelper.getMessage(tableName + ".SERVICE_NAME"));
            }

            for (T rawItem : rawItemList) {
               List<CoreBpmnProcBean> matchProcList = new ArrayList<>();

               for (CoreBpmnProcBean proc : procList) {
                  if (StringUtils.isBlank(proc.getExpression())) {
                     matchProcList.add(proc);
                  } else {
                     Map<String, Object> vars = BeanUtils.deeplyToMap(rawItem);
                     if (CoreBpmnHelper.evalExpression(proc.getExpression(), vars)) {
                        if (!matchProcList.isEmpty() && !StringUtils.isBlank(matchProcList.get(0).getExpression())) {
                           throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.DIAGRAM.MATCH_MULTIPLE_PROC");
                        }

                        matchProcList.add(0, proc);
                     }
                  }
               }

               if (matchProcList.isEmpty()) {
                  throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.PROCESS_NOT_EXISTS", I18nHelper.getMessage(tableName + ".SERVICE_NAME"));
               }

               if (matchProcList.size() == 1) {
                  runtimeMap.put(rawItem.getId(), matchProcList.get(0));
               } else if (!StringUtils.isBlank(matchProcList.get(0).getExpression())) {
                  runtimeMap.put(rawItem.getId(), matchProcList.get(0));
               } else {
                  for (CoreBpmnProcBean procx : matchProcList) {
                     if (!StringUtils.startsWith(procx.getExt$Item("orgqty"), "0")) {
                        if (runtimeMap.get(rawItem.getId()) != null) {
                           throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.DIAGRAM.MATCH_MULTIPLE_PROC");
                        }

                        runtimeMap.put(rawItem.getId(), procx);
                     }
                  }

                  if (runtimeMap.get(rawItem.getId()) == null) {
                     throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.DIAGRAM.MATCH_MULTIPLE_PROC");
                  }
               }
            }
         }

         if (!approveItemList.isEmpty()) {
            String tableName = PersistableMetadataHelper.getTableName(itemList.get(0).getClass());
            List<String> targetIdList = approveItemList.stream().map(i -> tableName + "$" + i.getId()).collect(Collectors.toList());
            List<CoreBpmnInstanceBean> instanceList = this.instanceService
               .selectListByFilter(SearchFilter.instance().match("TARGETID", targetIdList).filter(MatchPattern.OR), new Order[0]);
            approveItemList.forEach(
               a -> {
                  CoreBpmnProcBean var10000 = runtimeMap.put(
                     a.getId(),
                     this.procService
                        .selectById(instanceList.stream().filter(i -> i.getTargetId().equals(tableName + "$" + a.getId())).findAny().get().getProcId())
                  );
               }
            );
         }

         return runtimeMap;
      }
   }

   @Override
   public CoreBpmnProcBean getLatestBpmnProc(String procCode) {
      return this.procService.selectLatestProc(procCode);
   }

   @Override
   public <ID extends Serializable> List<CoreBpmnInstanceTaskBean> selectAuditableInstanceTaskList(String table, ID id, @Nullable String userId) {
      CoreBpmnInstanceBean filter = new CoreBpmnInstanceBean();
      filter.setTargetId(CoreBpmnHelper.getTargetId(table, id));
      CoreBpmnInstanceBean instance = this.instanceService.getDao().selectOneIfPresent(filter);
      return instance == null ? CollectionUtils.emptyList() : this.instanceTaskService.selectAuditableInstanceTaskList(userId, Arrays.asList(instance.getId()));
   }

   @Transactional
   @Override
   public void processData(BpmnProcessData data) {
      if (!data.getInsertOrUpdateBpmnTargetList().isEmpty()) {
         List<String> targetIdList = data.getInsertOrUpdateBpmnTargetList().stream().map(CoreBpmnTargetBean::getId).collect(Collectors.toList());
         List<CoreBpmnTargetBean> selectedBpmnTargetList = this.bpmnTargetService
            .getDao()
            .selectListByOneColumnValues(targetIdList, "ID", Arrays.asList("ID", "TARGETDESC", "PROCESSSTATUS"));
         List<CoreBpmnTargetBean> insertBpmnTargetList = data.getInsertOrUpdateBpmnTargetList()
            .stream()
            .filter(i -> selectedBpmnTargetList.stream().noneMatch(s -> s.getId().equals(i.getId())))
            .collect(Collectors.toList());
         if (!insertBpmnTargetList.isEmpty()) {
            this.bpmnTargetService.getDao().fastInsert(insertBpmnTargetList);
         }

         List<CoreBpmnTargetBean> updateBpmnTargetList = new ArrayList<>();

         for (CoreBpmnTargetBean selectedBpmnTarget : selectedBpmnTargetList) {
            CoreBpmnTargetBean bpmnTarget = data.getInsertOrUpdateBpmnTargetList()
               .stream()
               .filter(i -> i.getId().equals(selectedBpmnTarget.getId()))
               .findAny()
               .get();
            if (!ObjectUtils.equals(bpmnTarget.getTargetDesc(), selectedBpmnTarget.getTargetDesc())
               || !ObjectUtils.equals(bpmnTarget.getProcessStatus(), selectedBpmnTarget.getProcessStatus())) {
               updateBpmnTargetList.add(bpmnTarget);
            }
         }

         if (!updateBpmnTargetList.isEmpty()) {
            this.bpmnTargetService.getDao().fastUpdate(updateBpmnTargetList, "TARGETDESC", "PROCESSSTATUS");
         }
      }

      if (!data.getUpdateProcessStatusBpmnTargetList().isEmpty()) {
         List<String> targetIdListx = data.getUpdateProcessStatusBpmnTargetList().stream().map(CoreBpmnTargetBean::getId).collect(Collectors.toList());
         List<CoreBpmnTargetBean> selectedBpmnTargetListx = this.bpmnTargetService
            .getDao()
            .selectListByOneColumnValues(targetIdListx, "ID", Arrays.asList("ID", "PROCESSSTATUS"));
         List<CoreBpmnTargetBean> updateBpmnTargetList = new ArrayList<>();

         for (CoreBpmnTargetBean selectedBpmnTargetx : selectedBpmnTargetListx) {
            CoreBpmnTargetBean bpmnTarget = data.getUpdateProcessStatusBpmnTargetList()
               .stream()
               .filter(i -> i.getId().equals(selectedBpmnTargetx.getId()))
               .findAny()
               .get();
            if (!ObjectUtils.equals(bpmnTarget.getProcessStatus(), selectedBpmnTargetx.getProcessStatus())) {
               updateBpmnTargetList.add(bpmnTarget);
            }
         }

         if (!updateBpmnTargetList.isEmpty()) {
            this.bpmnTargetService.getDao().fastUpdate(data.getUpdateProcessStatusBpmnTargetList(), "PROCESSSTATUS");
         }
      }

      if (!data.getDeleteBpmnTargetIdList().isEmpty()) {
         this.bpmnTargetService.getDao().deleteByIdList(data.getDeleteBpmnTargetIdList());
      }

      if (!data.getInsertInstanceList().isEmpty()) {
         if (!data.getDeleteInstanceList().isEmpty()) {
            data.getInsertInstanceList().removeIf(i -> data.getDeleteInstanceList().stream().anyMatch(d -> d.getId().equals(i.getId())));
         }

         if (!data.getInsertInstanceList().isEmpty()) {
            this.instanceService.getDao().fastInsert(data.getInsertInstanceList());
         }
      }

      if (!data.getDeleteInstanceList().isEmpty()) {
         List<CoreBpmnInstanceTaskBean> taskFilterList = new ArrayList<>();
         data.getDeleteInstanceList().forEach(i -> {
            CoreBpmnInstanceTaskBean taskFilter = new CoreBpmnInstanceTaskBean();
            taskFilter.setInstId(i.getId());
            taskFilterList.add(taskFilter);
         });
         data.addDeleteInstanceTaskId(this.instanceTaskService.getDao().selectIdList(taskFilterList));
         this.instanceService.getDao().deleteBy(data.getDeleteInstanceList());
      }

      if (!data.getDeleteInstanceTaskIdList().isEmpty() || !data.getDeleteCandidatorInstanceTaskIdList().isEmpty()) {
         List<CoreBpmnInstanceTaskUserBean> taskUserFilterList = new ArrayList<>();
         List<CoreBpmnInstanceTaskRoleBean> taskRoleFilterList = new ArrayList<>();
         data.getDeleteInstanceTaskIdList().forEach(i -> {
            CoreBpmnInstanceTaskUserBean taskUserFilter = new CoreBpmnInstanceTaskUserBean();
            taskUserFilter.setInstTaskId(i);
            taskUserFilterList.add(taskUserFilter);
            CoreBpmnInstanceTaskRoleBean taskRoleFilter = new CoreBpmnInstanceTaskRoleBean();
            taskRoleFilter.setInstTaskId(i);
            taskRoleFilterList.add(taskRoleFilter);
         });
         data.getDeleteCandidatorInstanceTaskIdList().forEach(i -> {
            CoreBpmnInstanceTaskUserBean taskUserFilter = new CoreBpmnInstanceTaskUserBean();
            taskUserFilter.setInstTaskId(i);
            taskUserFilterList.add(taskUserFilter);
            CoreBpmnInstanceTaskRoleBean taskRoleFilter = new CoreBpmnInstanceTaskRoleBean();
            taskRoleFilter.setInstTaskId(i);
            taskRoleFilterList.add(taskRoleFilter);
         });
         data.addDeleteInstanceTaskUserId(this.instanceTaskUserService.getDao().selectIdList(taskUserFilterList));
         data.addDeleteInstanceTaskRoleId(this.instanceTaskRoleService.getDao().selectIdList(taskRoleFilterList));
         if (!data.getDeleteInstanceTaskIdList().isEmpty()) {
            this.instanceTaskService.getDao().deleteByIdList(data.getDeleteInstanceTaskIdList());
         }
      }

      if (!data.getUpdateInstanceTaskStatusList().isEmpty()) {
         data.getUpdateInstanceTaskStatusList().removeIf(t -> data.getDeleteInstanceTaskIdList().contains(t.getId()));
         if (!data.getUpdateInstanceTaskStatusList().isEmpty()) {
            this.instanceTaskService.getDao().fastUpdate(data.getUpdateInstanceTaskStatusList(), "STATUS");
         }
      }

      if (!data.getInsertInstanceTaskList().isEmpty()) {
         data.getInsertInstanceTaskList().removeIf(t -> data.getDeleteInstanceTaskIdList().contains(t.getId()));
         if (!data.getInsertInstanceTaskList().isEmpty()) {
            this.instanceTaskService.getDao().fastInsert(data.getInsertInstanceTaskList());
         }
      }

      if (!data.getInsertInstanceTaskUserList().isEmpty()) {
         data.getInsertInstanceTaskUserList().removeIf(t -> data.getDeleteInstanceTaskUserIdList().contains(t.getId()));
         if (!data.getInsertInstanceTaskUserList().isEmpty()) {
            this.instanceTaskUserService.getDao().fastInsert(data.getInsertInstanceTaskUserList());
         }
      }

      if (!data.getInsertInstanceTaskRoleList().isEmpty()) {
         data.getInsertInstanceTaskRoleList().removeIf(t -> data.getDeleteInstanceTaskRoleIdList().contains(t.getId()));
         if (!data.getInsertInstanceTaskRoleList().isEmpty()) {
            this.instanceTaskRoleService.getDao().fastInsert(data.getInsertInstanceTaskRoleList());
         }
      }

      if (!data.getInsertInstanceTaskList().isEmpty()) {
         List<CoreBpmnInstanceTaskHisBean> taskHisList = new ArrayList<>();

         for (CoreBpmnInstanceTaskBean task : data.getInsertInstanceTaskList()) {
            CoreBpmnInstanceTaskHisCandidatorDTO hisCandidator = new CoreBpmnInstanceTaskHisCandidatorDTO();

            for (CoreBpmnInstanceTaskUserBean taskUser : data.getInsertInstanceTaskUserList()) {
               if (task.getId().equals(taskUser.getInstTaskId())) {
                  hisCandidator.addUserId(taskUser.getUserId());
               }
            }

            for (CoreBpmnInstanceTaskRoleBean taskRole : data.getInsertInstanceTaskRoleList()) {
               if (task.getId().equals(taskRole.getInstTaskId())) {
                  hisCandidator.addRoleId(taskRole.getRoleId());
               }
            }

            CoreBpmnInstanceTaskHisBean taskHis = new CoreBpmnInstanceTaskHisBean();
            taskHis.setId(ApplicationContextHelper.getNextIdentity());
            taskHis.setInstId(task.getInstId());
            taskHis.setStatusCode(task.getStatusCode());
            taskHis.setCandidatorJson(JSON.toJSONString(hisCandidator));
            taskHisList.add(taskHis);
         }

         this.taskHisService.getDao().fastInsert(taskHisList);
      }

      if (!data.getDeleteBpmnTargetIdList().isEmpty()) {
         List<CoreBpmnInstanceTaskHisBean> deleteTaskHisList = data.getDeleteInstanceList()
            .stream()
            .filter(d -> data.getDeleteBpmnTargetIdList().contains(d.getTargetId()))
            .map(d -> {
               CoreBpmnInstanceTaskHisBean taskHis = new CoreBpmnInstanceTaskHisBean();
               taskHis.setInstId(d.getId());
               return taskHis;
            })
            .collect(Collectors.toList());
         if (!deleteTaskHisList.isEmpty()) {
            ExecutorHelper.submit(() -> this.taskHisService.getDao().deleteBy(deleteTaskHisList, "INSTID"));
         }
      }

      if (!data.getInsertCommentList().isEmpty()) {
         this.commentService.getDao().fastInsert(data.getInsertCommentList());
      }

      if (!data.getDeleteInstanceTaskUserIdList().isEmpty()) {
         this.instanceTaskUserService.getDao().deleteByIdList(data.getDeleteInstanceTaskUserIdList());
      }

      if (!data.getDeleteInstanceTaskRoleIdList().isEmpty()) {
         this.instanceTaskRoleService.getDao().deleteByIdList(data.getDeleteInstanceTaskRoleIdList());
      }

      if (data.isAsync() && data.getUpdateProcessStatusRunnable() != null) {
         data.getUpdateProcessStatusRunnable().run();
      }
   }
}
