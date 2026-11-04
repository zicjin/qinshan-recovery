package com.sunwayworld.framework.support.auditable.service;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.beans.BeanPropertyHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.context.EntityColumnContext;
import com.sunwayworld.framework.database.core.DatabaseManager;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.framework.exception.core.ApplicationWarningException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.progress.core.ProgressTask;
import com.sunwayworld.framework.progress.service.ProgressService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnParameterDTO;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnTaskDTO;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnTaskStatusDTO;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.support.domain.Suspendable;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.bpmn.CoreBpmnHelper;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnCommentBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceNextTaskElementDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnProcBean;
import com.sunwayworld.module.sys.bpmn.diagram.BpmnDiagramHelper;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import com.sunwayworld.module.sys.bpmn.diagram.attribute.CandidatorFilterStrategy;
import com.sunwayworld.module.sys.bpmn.diagram.attribute.NextCandidatorOptStrategy;
import com.sunwayworld.module.sys.bpmn.engine.CoreBpmnRuntimeService;
import com.sunwayworld.module.sys.bpmn.engine.CoreBpmnRuntimeSource;
import com.sunwayworld.module.sys.bpmn.engine.cache.BpmnRuntimeCacheProvider;
import com.sunwayworld.module.sys.bpmn.exception.BpmnException;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnCommentService;
import com.sunwayworld.module.sys.role.bean.CoreRoleUserBean;
import com.sunwayworld.module.sys.role.service.CoreRoleService;
import com.sunwayworld.module.sys.role.service.CoreRoleUserService;
import java.io.Serializable;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

public interface GenericAuditableService<T extends Auditable<ID>, ID extends Serializable> extends GenericService<T, ID> {
   default Page<T> selectAuditablePagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      if (Auditable.class.isAssignableFrom(this.getDao().getType())) {
         parameter.setProcessStatus("PROCESSSTATUS", ProcessStatus.APPROVE);
      }

      parameter.setAuditAuthority();
      if (!"0".equals(wrapper.getParamValue("orgAuthority"))) {
         parameter.setOrgAuthority();
      }

      parameter.setAuditableQueries();
      if (!wrapper.getAuthorityList().isEmpty()) {
         wrapper.getAuthorityList().forEach(a -> parameter.setAuthorityParameter(a));
      }

      return this.selectPagination(parameter, rowBounds);
   }

   default Page<T> selectWithdrawablePagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      if (Auditable.class.isAssignableFrom(this.getDao().getType())) {
         parameter.setProcessStatus("PROCESSSTATUS", ProcessStatus.APPROVE);
      }

      if (!"0".equals(wrapper.getParamValue("orgAuthority"))) {
         parameter.setOrgAuthority();
      }

      parameter.setWithdrawableQueries();
      if (!wrapper.getAuthorityList().isEmpty()) {
         wrapper.getAuthorityList().forEach(a -> parameter.setAuthorityParameter(a));
      }

      return this.selectPagination(parameter, rowBounds);
   }

   default Page<T> selectUndoablePagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      Class<T> type = this.getDao().getType();
      if (Auditable.class.isAssignableFrom(this.getDao().getType())) {
         parameter.setProcessStatus("PROCESSSTATUS", ProcessStatus.DONE);
      }

      if (Suspendable.class.isAssignableFrom(type)) {
         parameter.setSuspendedFlag("0");
      }

      if (!"0".equals(wrapper.getParamValue("orgAuthority"))) {
         parameter.setOrgAuthority();
      }

      parameter.setUndoableQueries();
      if (!wrapper.getAuthorityList().isEmpty()) {
         wrapper.getAuthorityList().forEach(a -> parameter.setAuthorityParameter(a));
      }

      return this.selectPagination(parameter, rowBounds);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_START)
   @Audit("AUDIT.PROCESS_START")
   default List<CoreBpmnInstanceStatusDTO<ID>> startProcess(RestJsonWrapperBean wrapper) {
      List<T> itemList = wrapper.parse(this.getDao().getType());
      if (itemList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         itemList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), i.getId())));
         this.checkAndUpdateVersion(itemList);
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<ID> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<T> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseSubmittableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         List<CoreBpmnInstanceStatusDTO<ID>> instanceStatusList = runtimeService.startProcess(runtimeSourceList);
         this.autoPass(runtimeSourceList, instanceStatusList);
         return instanceStatusList;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_START)
   @Audit("AUDIT.PROCESS_START")
   default String startProcessWithProgress(List<ID> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return null;
      } else {
         itemIdList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), (ID)i)));
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<T> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseSubmittableRuntimeSource(
            itemIdList, parameter, this.getBpmnVars(null, selectItemList)
         );
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<T, ID> runtimeSource : runtimeSourceList) {
            ProgressTask runnable = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<T, ID>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  List<CoreBpmnInstanceStatusDTO<ID>> innerInstanceStatusList = runtimeService.startProcess(innerRuntimeSourceList);
                  GenericAuditableService.this.autoPass(innerRuntimeSourceList, innerInstanceStatusList);
               }

               @Override
               public void rollback() {
                  T item = runtimeSource.getOldItem();
                  if (StringUtils.startsWith(item.getProcessStatus(), "RUN$")) {
                     item.setProcessStatus(StringUtils.removeStart(item.getProcessStatus(), "RUN$"));
                     GenericAuditableService.this.getDao().update(item, "PROCESSSTATUS");
                  }
               }
            };
            taskList.add(runnable);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.SUBMIT_NAME", I18nHelper.getMessage(this.getDao().getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_START)
   @Audit("AUDIT.PROCESS_START")
   default String startProcessWithProgress(RestJsonWrapperBean wrapper) {
      List<T> itemList = wrapper.parse(this.getDao().getType());
      if (itemList.isEmpty()) {
         return null;
      } else {
         itemList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), i.getId())));
         this.checkAndUpdateVersion(itemList);
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<ID> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<T> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseSubmittableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<T, ID> runtimeSource : runtimeSourceList) {
            ProgressTask runnable = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<T, ID>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  List<CoreBpmnInstanceStatusDTO<ID>> innerInstanceStatusList = runtimeService.startProcess(innerRuntimeSourceList);
                  GenericAuditableService.this.autoPass(innerRuntimeSourceList, innerInstanceStatusList);
               }

               @Override
               public void rollback() {
                  T item = runtimeSource.getOldItem();
                  if (StringUtils.startsWith(item.getProcessStatus(), "RUN$")) {
                     item.setProcessStatus(StringUtils.removeStart(item.getProcessStatus(), "RUN$"));
                     GenericAuditableService.this.getDao().update(item, "PROCESSSTATUS");
                  }
               }
            };
            taskList.add(runnable);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.SUBMIT_NAME", I18nHelper.getMessage(this.getDao().getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_START)
   @Audit("AUDIT.PROCESS_START")
   default List<CoreBpmnInstanceStatusDTO<ID>> startProcess(List<ID> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         itemIdList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), (ID)i)));
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<T> itemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseSubmittableRuntimeSource(itemIdList, parameter, this.getBpmnVars(null, itemList));
         List<CoreBpmnInstanceStatusDTO<ID>> instanceStatusList = runtimeService.startProcess(runtimeSourceList);
         this.autoPass(runtimeSourceList, instanceStatusList);
         return instanceStatusList;
      }
   }

   @Deprecated
   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_START)
   @Audit("AUDIT.PROCESS_START")
   default List<CoreBpmnInstanceStatusDTO<ID>> startProcess(List<ID> itemIdList, String comment) {
      CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
      parameter.setComment(comment);
      return this.startProcess(itemIdList, parameter);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_WITHDRAW)
   @Audit("AUDIT.PROCESS_WITHDRAW")
   default List<CoreBpmnInstanceStatusDTO<ID>> withdrawProcess(RestJsonWrapperBean wrapper) {
      List<T> itemList = wrapper.parse(this.getDao().getType());
      if (itemList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         itemList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), i.getId())));
         this.checkAndUpdateVersion(itemList);
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<ID> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<T> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseWithdrawableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         return runtimeService.withdrawProcess(runtimeSourceList);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_WITHDRAW)
   @Audit("AUDIT.PROCESS_WITHDRAW")
   default String withdrawProcessWithProgress(RestJsonWrapperBean wrapper) {
      List<T> itemList = wrapper.parse(this.getDao().getType());
      if (itemList.isEmpty()) {
         return null;
      } else {
         itemList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), i.getId())));
         this.checkAndUpdateVersion(itemList);
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<ID> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<T> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseWithdrawableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<T, ID> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<T, ID>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  runtimeService.withdrawProcess(innerRuntimeSourceList);
               }

               @Override
               public void rollback() {
                  GenericAuditableService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.WITHDRAW_NAME", I18nHelper.getMessage(this.getDao().getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_WITHDRAW)
   @Audit("AUDIT.PROCESS_WITHDRAW")
   default String withdrawProcessWithProgress(List<ID> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return null;
      } else {
         itemIdList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), (ID)i)));
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<T> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseWithdrawableRuntimeSource(
            itemIdList, parameter, this.getBpmnVars(null, selectItemList)
         );
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<T, ID> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<T, ID>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  runtimeService.withdrawProcess(innerRuntimeSourceList);
               }

               @Override
               public void rollback() {
                  GenericAuditableService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.WITHDRAW_NAME", I18nHelper.getMessage(this.getDao().getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_WITHDRAW)
   @Audit("AUDIT.PROCESS_WITHDRAW")
   default List<CoreBpmnInstanceStatusDTO<ID>> withdrawProcess(List<ID> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         itemIdList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), (ID)i)));
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<T> itemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseWithdrawableRuntimeSource(itemIdList, parameter, this.getBpmnVars(null, itemList));
         return runtimeService.withdrawProcess(runtimeSourceList);
      }
   }

   @Deprecated
   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_WITHDRAW)
   @Audit("AUDIT.PROCESS_WITHDRAW")
   default List<CoreBpmnInstanceStatusDTO<ID>> withdrawProcess(List<ID> itemIdList, String comment) {
      CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
      parameter.setComment(comment);
      return this.withdrawProcess(itemIdList, parameter);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_COMPLETE)
   @Audit("AUDIT.PROCESS_COMPLETE")
   default List<CoreBpmnInstanceStatusDTO<ID>> completeTask(RestJsonWrapperBean wrapper) {
      List<T> itemList = wrapper.parse(this.getDao().getType());
      if (itemList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         itemList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), i.getId())));
         this.checkAndUpdateVersion(itemList);
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<ID> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<T> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         List<CoreBpmnInstanceStatusDTO<ID>> instanceStatusList = runtimeService.completeTask(runtimeSourceList);
         this.autoPass(runtimeSourceList, instanceStatusList);
         return instanceStatusList;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_COMPLETE)
   @Audit("AUDIT.PROCESS_COMPLETE")
   default String completeTaskWithProgress(RestJsonWrapperBean wrapper) {
      List<T> itemList = wrapper.parse(this.getDao().getType());
      if (itemList.isEmpty()) {
         return null;
      } else {
         itemList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), i.getId())));
         this.checkAndUpdateVersion(itemList);
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<ID> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<T> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<T, ID> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<T, ID>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  List<CoreBpmnInstanceStatusDTO<ID>> innerInstanceStatusList = runtimeService.completeTask(innerRuntimeSourceList);
                  GenericAuditableService.this.autoPass(innerRuntimeSourceList, innerInstanceStatusList);
               }

               @Override
               public void rollback() {
                  GenericAuditableService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.COMPLETE_NAME", I18nHelper.getMessage(this.getDao().getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_COMPLETE)
   @Audit("AUDIT.PROCESS_COMPLETE")
   default String completeTaskWithProgress(List<ID> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return null;
      } else {
         itemIdList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), (ID)i)));
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<T> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseAuditableRuntimeSource(itemIdList, parameter, this.getBpmnVars(null, selectItemList));
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<T, ID> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<T, ID>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  List<CoreBpmnInstanceStatusDTO<ID>> innerInstanceStatusList = runtimeService.completeTask(innerRuntimeSourceList);
                  GenericAuditableService.this.autoPass(innerRuntimeSourceList, innerInstanceStatusList);
               }

               @Override
               public void rollback() {
                  GenericAuditableService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.COMPLETE_NAME", I18nHelper.getMessage(this.getDao().getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_COMPLETE)
   @Audit("AUDIT.PROCESS_COMPLETE")
   default List<CoreBpmnInstanceStatusDTO<ID>> completeTask(List<ID> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         itemIdList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), (ID)i)));
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<T> itemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseAuditableRuntimeSource(itemIdList, parameter, this.getBpmnVars(null, itemList));
         List<CoreBpmnInstanceStatusDTO<ID>> instanceStatusList = runtimeService.completeTask(runtimeSourceList);
         this.autoPass(runtimeSourceList, instanceStatusList);
         return instanceStatusList;
      }
   }

   @Deprecated
   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_COMPLETE)
   @Audit("AUDIT.PROCESS_COMPLETE")
   default List<CoreBpmnInstanceStatusDTO<ID>> completeTask(List<ID> itemIdList, String comment) {
      CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
      parameter.setComment(comment);
      return this.completeTask(itemIdList, parameter);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_TRANSFER)
   @Audit("AUDIT.PROCESS_TRANSFER")
   default void transferTask(RestJsonWrapperBean wrapper) {
      List<T> itemList = wrapper.parse(this.getDao().getType());
      if (!itemList.isEmpty()) {
         itemList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), i.getId())));
         this.checkAndUpdateVersion(itemList);
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<ID> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), CollectionUtils.emptyMap()
         );
         runtimeService.transferTask(runtimeSourceList);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_TRANSFER)
   @Audit("AUDIT.PROCESS_TRANSFER")
   default String transferTaskWithProgress(RestJsonWrapperBean wrapper) {
      List<T> itemList = wrapper.parse(this.getDao().getType());
      if (itemList.isEmpty()) {
         return null;
      } else {
         itemList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), i.getId())));
         this.checkAndUpdateVersion(itemList);
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<ID> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), CollectionUtils.emptyMap()
         );
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<T, ID> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<T, ID>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  runtimeService.transferTask(innerRuntimeSourceList);
               }

               @Override
               public void rollback() {
                  GenericAuditableService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.TRANSFER_NAME", I18nHelper.getMessage(this.getDao().getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_END)
   @Audit("AUDIT.PROCESS_END")
   default List<CoreBpmnInstanceStatusDTO<ID>> endTask(RestJsonWrapperBean wrapper) {
      List<T> itemList = wrapper.parse(this.getDao().getType());
      if (itemList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         itemList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), i.getId())));
         this.checkAndUpdateVersion(itemList);
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<ID> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<T> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         return runtimeService.endTask(runtimeSourceList);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_END)
   @Audit("AUDIT.PROCESS_END")
   default String endTaskWithProgress(RestJsonWrapperBean wrapper) {
      List<T> itemList = wrapper.parse(this.getDao().getType());
      if (itemList.isEmpty()) {
         return null;
      } else {
         itemList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), i.getId())));
         this.checkAndUpdateVersion(itemList);
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<ID> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<T> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<T, ID> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<T, ID>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  runtimeService.endTask(innerRuntimeSourceList);
               }

               @Override
               public void rollback() {
                  GenericAuditableService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.END_NAME", I18nHelper.getMessage(this.getDao().getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_END)
   @Audit("AUDIT.PROCESS_END")
   default String endTaskWithProgress(List<ID> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return null;
      } else {
         itemIdList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), (ID)i)));
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<T> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseAuditableRuntimeSource(itemIdList, parameter, this.getBpmnVars(null, selectItemList));
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<T, ID> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<T, ID>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  runtimeService.endTask(innerRuntimeSourceList);
               }

               @Override
               public void rollback() {
                  GenericAuditableService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.END_NAME", I18nHelper.getMessage(this.getDao().getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_END)
   @Audit("AUDIT.PROCESS_END")
   default List<CoreBpmnInstanceStatusDTO<ID>> endTask(List<ID> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         itemIdList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), (ID)i)));
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<T> itemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseAuditableRuntimeSource(itemIdList, parameter, this.getBpmnVars(null, itemList));
         return runtimeService.endTask(runtimeSourceList);
      }
   }

   @Deprecated
   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_COMPLETE)
   @Audit("AUDIT.PROCESS_COMPLETE")
   default List<CoreBpmnInstanceStatusDTO<ID>> endTask(List<ID> itemIdList, String comment) {
      CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
      parameter.setComment(comment);
      return this.endTask(itemIdList, parameter);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_REJECT)
   @Audit("AUDIT.PROCESS_REJECT")
   default List<CoreBpmnInstanceStatusDTO<ID>> rejectTask(RestJsonWrapperBean wrapper) {
      List<T> itemList = wrapper.parse(this.getDao().getType());
      if (itemList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         itemList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), i.getId())));
         this.checkAndUpdateVersion(itemList);
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<ID> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<T> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         return runtimeService.rejectTask(runtimeSourceList);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_REJECT)
   @Audit("AUDIT.PROCESS_REJECT")
   default String rejectTaskWithProgress(RestJsonWrapperBean wrapper) {
      List<T> itemList = wrapper.parse(this.getDao().getType());
      if (itemList.isEmpty()) {
         return null;
      } else {
         itemList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), i.getId())));
         this.checkAndUpdateVersion(itemList);
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<ID> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<T> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<T, ID> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<T, ID>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  runtimeService.rejectTask(innerRuntimeSourceList);
               }

               @Override
               public void rollback() {
                  GenericAuditableService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.REJECT_NAME", I18nHelper.getMessage(this.getDao().getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_REJECT)
   @Audit("AUDIT.PROCESS_REJECT")
   default String rejectTaskWithProgress(List<ID> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return null;
      } else {
         itemIdList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), (ID)i)));
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<T> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseAuditableRuntimeSource(itemIdList, parameter, this.getBpmnVars(null, selectItemList));
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<T, ID> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<T, ID>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  runtimeService.rejectTask(innerRuntimeSourceList);
               }

               @Override
               public void rollback() {
                  GenericAuditableService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.REJECT_NAME", I18nHelper.getMessage(this.getDao().getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_REJECT)
   @Audit("AUDIT.PROCESS_REJECT")
   default List<CoreBpmnInstanceStatusDTO<ID>> rejectTask(List<ID> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         itemIdList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), (ID)i)));
         List<T> itemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseAuditableRuntimeSource(itemIdList, parameter, this.getBpmnVars(null, itemList));
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         return runtimeService.rejectTask(runtimeSourceList);
      }
   }

   @Deprecated
   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_COMPLETE)
   @Audit("AUDIT.PROCESS_COMPLETE")
   default List<CoreBpmnInstanceStatusDTO<ID>> rejectTask(List<ID> itemIdList, String comment) {
      CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
      parameter.setComment(comment);
      return this.rejectTask(itemIdList, parameter);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_REJECT)
   @Audit("AUDIT.PROCESS_ODD_REJECT")
   default List<CoreBpmnInstanceStatusDTO<ID>> oddRejectTask(RestJsonWrapperBean wrapper) {
      List<T> itemList = wrapper.parse(this.getDao().getType());
      if (itemList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         itemList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), i.getId())));
         this.checkAndUpdateVersion(itemList);
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<ID> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<T> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         return runtimeService.oddRejectTask(runtimeSourceList);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_REJECT)
   @Audit("AUDIT.PROCESS_ODD_REJECT")
   default String oddRejectTaskWithProgress(RestJsonWrapperBean wrapper) {
      List<T> itemList = wrapper.parse(this.getDao().getType());
      if (itemList.isEmpty()) {
         return null;
      } else {
         itemList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), i.getId())));
         this.checkAndUpdateVersion(itemList);
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<ID> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<T> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<T, ID> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<T, ID>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  runtimeService.oddRejectTask(innerRuntimeSourceList);
               }

               @Override
               public void rollback() {
                  GenericAuditableService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.ODD_REJECT_NAME", I18nHelper.getMessage(this.getDao().getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_REJECT)
   @Audit("AUDIT.PROCESS_ODD_REJECT")
   default String oddRejectTaskWithProgress(List<ID> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return null;
      } else {
         itemIdList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), (ID)i)));
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<T> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseAuditableRuntimeSource(itemIdList, parameter, this.getBpmnVars(null, selectItemList));
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<T, ID> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<T, ID>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  runtimeService.oddRejectTask(innerRuntimeSourceList);
               }

               @Override
               public void rollback() {
                  GenericAuditableService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.ODD_REJECT_NAME", I18nHelper.getMessage(this.getDao().getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_REJECT)
   @Audit("AUDIT.PROCESS_ODD_REJECT")
   default List<CoreBpmnInstanceStatusDTO<ID>> oddRejectTask(List<ID> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         itemIdList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), (ID)i)));
         List<T> itemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseAuditableRuntimeSource(itemIdList, parameter, this.getBpmnVars(null, itemList));
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         return runtimeService.oddRejectTask(runtimeSourceList);
      }
   }

   @Deprecated
   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_COMPLETE)
   @Audit("AUDIT.PROCESS_COMPLETE")
   default List<CoreBpmnInstanceStatusDTO<ID>> oddRejectTask(List<ID> itemIdList, String comment) {
      CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
      parameter.setComment(comment);
      return this.oddRejectTask(itemIdList, parameter);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_UNDO)
   @Audit("AUDIT.PROCESS_UNDO")
   default List<CoreBpmnInstanceStatusDTO<ID>> undo(RestJsonWrapperBean wrapper) {
      List<T> itemList = wrapper.parse(this.getDao().getType());
      if (itemList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         itemList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), i.getId())));
         this.checkAndUpdateVersion(itemList);
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<ID> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<T> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseUndoableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         return runtimeService.undo(runtimeSourceList);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_UNDO)
   @Audit("AUDIT.PROCESS_UNDO")
   default String undoWithProgress(RestJsonWrapperBean wrapper) {
      List<T> itemList = wrapper.parse(this.getDao().getType());
      if (itemList.isEmpty()) {
         return null;
      } else {
         itemList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), i.getId())));
         this.checkAndUpdateVersion(itemList);
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<ID> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<T> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseUndoableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<T, ID> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<T, ID>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  runtimeService.undo(innerRuntimeSourceList);
               }

               @Override
               public void rollback() {
                  GenericAuditableService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.UNDO_NAME", I18nHelper.getMessage(this.getDao().getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_UNDO)
   @Audit("AUDIT.PROCESS_UNDO")
   default String undoWithProgress(List<ID> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return null;
      } else {
         itemIdList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), (ID)i)));
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<T> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseUndoableRuntimeSource(itemIdList, parameter, this.getBpmnVars(null, selectItemList));
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<T, ID> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<T, ID>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  runtimeService.undo(innerRuntimeSourceList);
               }

               @Override
               public void rollback() {
                  GenericAuditableService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.UNDO_NAME", I18nHelper.getMessage(this.getDao().getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_UNDO)
   @Audit("AUDIT.PROCESS_UNDO")
   default List<CoreBpmnInstanceStatusDTO<ID>> undo(List<ID> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         itemIdList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getDao().getTable(), (ID)i)));
         List<T> itemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList = this.parseUndoableRuntimeSource(itemIdList, parameter, this.getBpmnVars(null, itemList));
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         return runtimeService.undo(runtimeSourceList);
      }
   }

   @Deprecated
   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_COMPLETE)
   @Audit("AUDIT.PROCESS_COMPLETE")
   default List<CoreBpmnInstanceStatusDTO<ID>> undo(List<ID> itemIdList, String comment) {
      CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
      parameter.setComment(comment);
      return this.undo(itemIdList, parameter);
   }

   default CoreBpmnTaskDTO getPreviousTask(ID id) {
      T item = this.selectById(id);
      if (StringUtils.startsWithIgnoreCase(item.getProcessStatus(), ProcessStatus.DRAFT.name())) {
         return null;
      } else {
         CoreBpmnCommentService commentService = ApplicationContextHelper.getBean(CoreBpmnCommentService.class);
         CoreBpmnCommentBean latestComment = commentService.selectFirstByFilter(
            SearchFilter.instance().match("TARGETID", CoreBpmnHelper.getTargetId(this.getDao().getTable(), id)).filter(MatchPattern.EQ),
            new Order[]{Order.desc("ID")}
         );
         if (latestComment == null) {
            return null;
         } else {
            CoreBpmnTaskDTO task = new CoreBpmnTaskDTO();
            task.setName(latestComment.getTaskName());
            task.setStatusCode(latestComment.getStatusCode());
            return task;
         }
      }
   }

   default CoreBpmnTaskStatusDTO selectBpmnTaskStatus(RestJsonWrapperBean wrapper) {
      List<ID> idList = wrapper.parseId((Class<ID>)this.getDao().getEntityContext().getIdContext().getType());
      if (idList.isEmpty()) {
         return new CoreBpmnTaskStatusDTO();
      } else {
         String pass = wrapper.getParamValue("bpmn_pass");
         if ("-1".equals(pass)) {
            return new CoreBpmnTaskStatusDTO();
         } else {
            List<CoreBpmnRuntimeSource<T, ID>> sourceList = this.parseRuntimeSource(idList, wrapper);
            if (sourceList.stream().anyMatch(s -> ProcessStatus.DONE.name().equalsIgnoreCase(s.getOldItem().getProcessStatus()))) {
               return new CoreBpmnTaskStatusDTO();
            } else {
               CoreBpmnTaskStatusDTO taskStatus = null;
               CoreBpmnCommentService commentService = ApplicationContextHelper.getBean(CoreBpmnCommentService.class);

               try {
                  BpmnRuntimeCacheProvider.init(sourceList);

                  for (CoreBpmnRuntimeSource<T, ID> source : sourceList) {
                     CoreBpmnTaskStatusDTO sourceStatus = new CoreBpmnTaskStatusDTO();
                     Element currentElement = BpmnRuntimeCacheProvider.getBpmnRuntimeTaskElement(source);
                     sourceStatus.setAttachmentStrategy(BpmnDiagramHelper.getAttachmentStrategy(currentElement));
                     sourceStatus.setCommentRequired(BpmnDiagramHelper.isCommentRequiredTask(currentElement));
                     sourceStatus.setAuthRequired(BpmnDiagramHelper.isAuthRequiredUserTask(currentElement));
                     sourceStatus.setTransfer(BpmnDiagramHelper.isTransferTask(currentElement));
                     if ("1".equals(pass) && BpmnRuntimeCacheProvider.isLastTaskCandidator(source)) {
                        sourceStatus.setNextCandidatorOptStrategy(BpmnDiagramHelper.getNextCandidatorOptStrategy(currentElement));
                        if (NextCandidatorOptStrategy.assigned.equals(sourceStatus.getNextCandidatorOptStrategy())
                           || NextCandidatorOptStrategy.assignedRole.equals(sourceStatus.getNextCandidatorOptStrategy())) {
                           CoreBpmnInstanceNextTaskElementDTO<ID> nextTaskElementDTO = BpmnRuntimeCacheProvider.getNextTask(source);
                           List<Element> nextTaskElementList = nextTaskElementDTO.getNextTaskElementList();
                           GlobalMapper globalMapper = ApplicationContextHelper.getBean(GlobalMapper.class);
                           CoreRoleUserService roleUserService = ApplicationContextHelper.getBean(CoreRoleUserService.class);
                           List<String> userList = new ArrayList<>();
                           List<Long> roleList = new ArrayList<>();

                           for (Element nextTaskElement : nextTaskElementList) {
                              if ("Done".equals(BpmnDiagramHelper.getUserTaskStatusCode(nextTaskElement))) {
                                 sourceStatus.setNextCandidatorOptStrategy(NextCandidatorOptStrategy.none);
                              } else {
                                 String selectBySql = BpmnDiagramHelper.getUserTaskAssigneeSelectBySql(nextTaskElement);
                                 if (!StringUtils.isBlank(selectBySql)) {
                                    List<String> selectedUserList = CoreBpmnHelper.getCandidatorList(selectBySql, source.getId(), source.getVars());
                                    if (selectedUserList.isEmpty()) {
                                       throw new BpmnException(
                                          "CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.NOCANDIDATOR_BY_SELECT",
                                          CoreBpmnHelper.getReplacedSql(selectBySql, source.getId(), source.getVars())
                                       );
                                    }

                                    userList.addAll(selectedUserList);
                                 } else {
                                    if (BpmnDiagramHelper.isUserTaskAssigneeInitiator(nextTaskElement)) {
                                       CoreBpmnInstanceBean instance = BpmnRuntimeCacheProvider.getBpmnRuntimeInstance(source);
                                       String initiator = instance == null ? LocalContextHelper.getLoginUserId() : instance.getInitiator();
                                       userList.add(initiator);
                                    }

                                    boolean nextCandidatorSelected = !source.getNextCandidatorList().isEmpty() || !source.getNextCandidateRoleList().isEmpty();
                                    if (nextCandidatorSelected) {
                                       userList.addAll(source.getNextCandidatorList());
                                       roleList.addAll(source.getNextCandidateRoleList());
                                    } else {
                                       String dynamic = BpmnDiagramHelper.getUserTaskAssigneeDynamic(nextTaskElement);
                                       if (!StringUtils.isEmpty(dynamic)) {
                                          String[] dynamicValues = StringUtils.split(dynamic, ",");

                                          for (String value : dynamicValues) {
                                             if (StringUtils.startsWith(value, "u:")) {
                                                String user = (String)source.getVars().get(StringUtils.removeStart(value, "u:"));
                                                if (user == null) {
                                                   throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.DYNAMIC_USER_NOT_EXISTS");
                                                }

                                                userList.add(user);
                                             } else if (StringUtils.startsWith(value, "r:")) {
                                                String role = ObjectUtils.toString(source.getVars().get(StringUtils.removeStart(value, "r:")));
                                                if (StringUtils.isEmpty(role)) {
                                                   throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.DYNAMIC_ROLE_NOT_EXISTS");
                                                }

                                                roleList.add(NumberUtils.parseLong(role));
                                             }
                                          }
                                       }

                                       Map<String, String> userTaskCandidatorMap = BpmnDiagramHelper.getUserTaskCandidatorValue(nextTaskElement);
                                       if (!userTaskCandidatorMap.isEmpty()) {
                                          userList.addAll(userTaskCandidatorMap.keySet());
                                       }

                                       Map<Long, String> userTaskRoleMap = BpmnDiagramHelper.getUserTaskRoleValue(nextTaskElement);
                                       if (!userTaskRoleMap.isEmpty()) {
                                          roleList.addAll(userTaskRoleMap.keySet());
                                       }
                                    }

                                    if (userList.isEmpty() && roleList.isEmpty()) {
                                       throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.CANDIDATOR_AND_ROLE_NOT_EXISTS");
                                    }

                                    String filterBySql = BpmnDiagramHelper.getUserTaskAssigneeFilterBySql(nextTaskElement);
                                    if (!StringUtils.isBlank(filterBySql)) {
                                       List<String> filteredUserList = CoreBpmnHelper.getCandidatorList(
                                          filterBySql, source.getId(), userList, roleList, source.getVars()
                                       );
                                       if (filteredUserList.isEmpty()) {
                                          throw new BpmnException(
                                             "CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.NOCANDIDATOR_BY_FILTER",
                                             CoreBpmnHelper.getReplacedFilterBySql(filterBySql, source.getId(), userList, roleList, source.getVars())
                                          );
                                       }

                                       userList.clear();
                                       roleList.clear();
                                       userList.addAll(filteredUserList);
                                    }
                                 }

                                 if (NextCandidatorOptStrategy.assigned.equals(sourceStatus.getNextCandidatorOptStrategy()) && !roleList.isEmpty()) {
                                    List<CoreRoleUserBean> roleUserList = roleUserService.getDao()
                                       .selectListByOneColumnValues(roleList.stream().distinct().collect(Collectors.toList()), "ROLEID");
                                    userList.addAll(roleUserList.stream().map(CoreRoleUserBean::getUserId).collect(Collectors.toList()));
                                 }

                                 if (BpmnDiagramHelper.isFilterPreviousCandidatorsTask(nextTaskElement)) {
                                    Document diagram = BpmnRuntimeCacheProvider.getBpmnRuntimeDocument(source);
                                    List<Element> userTaskElementList = BpmnDiagramHelper.getIncomingUserTaskElementList(diagram, nextTaskElement);
                                    if (!userTaskElementList.isEmpty()) {
                                       for (Element userTaskElement : userTaskElementList) {
                                          List<String> auditedByList = commentService.selectUserTaskAuditedByList(
                                             source.getTargetId(), BpmnDiagramHelper.getUserTaskStatusCode(userTaskElement)
                                          );
                                          if (!auditedByList.isEmpty()) {
                                             userList.removeIf(auditedByList::contains);
                                          }
                                       }
                                    }

                                    if (StringUtils.startsWithIgnoreCase(source.getOldItem().getProcessStatus(), ProcessStatus.DRAFT.name())) {
                                       userList.removeIf(e -> e.equals(LocalContextHelper.getLoginUserId()));
                                    }
                                 }

                                 if (!userList.isEmpty()
                                    && CandidatorFilterStrategy.sameDept.equals(BpmnDiagramHelper.getCandidatorFilterStrategy(nextTaskElement))) {
                                    String createdByOrgId = (String)CollectionUtils.getValueIgnorecase(source.getVars(), "createdByOrgId");
                                    if (!StringUtils.isBlank(createdByOrgId)) {
                                       CoreUserService userService = ApplicationContextHelper.getBean(CoreUserService.class);
                                       List<CoreUserBean> userBeanList = userService.getDao()
                                          .selectListByOneColumnValues(
                                             userList.stream().distinct().collect(Collectors.toList()), "ID", Arrays.asList("ID", "ORGID")
                                          );
                                       userList.removeIf(u -> userBeanList.stream().noneMatch(s -> s.getId().equals(u) && s.getOrgId().equals(createdByOrgId)));
                                    }
                                 }
                              }
                           }

                           if (NextCandidatorOptStrategy.assigned.equals(sourceStatus.getNextCandidatorOptStrategy())) {
                              if (!userList.isEmpty()) {
                                 CoreUserService userService = ApplicationContextHelper.getBean(CoreUserService.class);
                                 sourceStatus.setCandidatorList(
                                    userService.selectListByIds(userList.stream().distinct().collect(Collectors.toList()))
                                       .stream()
                                       .filter(u -> !"deprecated".equals(u.getStatus()))
                                       .collect(Collectors.toList())
                                 );
                              }
                           } else if (NextCandidatorOptStrategy.assignedRole.equals(sourceStatus.getNextCandidatorOptStrategy()) && !roleList.isEmpty()) {
                              CoreRoleService roleService = ApplicationContextHelper.getBean(CoreRoleService.class);
                              sourceStatus.setCandidateRoleList(roleService.selectListByIds(roleList.stream().distinct().collect(Collectors.toList())));
                           }
                        }
                     }

                     if (taskStatus == null) {
                        taskStatus = sourceStatus;
                     } else {
                        this.checkAndMerge(taskStatus, sourceStatus);
                     }
                  }
               } finally {
                  BpmnRuntimeCacheProvider.remove();
               }

               if (NextCandidatorOptStrategy.assigned.equals(taskStatus.getNextCandidatorOptStrategy())
                  && CollectionUtils.isEmpty(taskStatus.getCandidatorList())) {
                  throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.TIP.ASSIGNED_STRATEGY_REQUIRES_CANDIDATORS");
               } else if (NextCandidatorOptStrategy.assignedRole.equals(taskStatus.getNextCandidatorOptStrategy())
                  && CollectionUtils.isEmpty(taskStatus.getCandidateRoleList())) {
                  throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.TIP.ASSIGNED_ROLE_STRATEGY_REQUIRES_CANDIDATEROLES");
               } else {
                  return taskStatus;
               }
            }
         }
      }
   }

   default List<T> getBpmnVarsItemList(List<ID> idList) {
      return this.<GenericDao<T, ID>>getDao().selectListByIds(idList);
   }

   default Map<ID, Map<String, Object>> getBpmnVars(@Nullable RestJsonWrapperBean wrapper, List<T> itemList) {
      Map<ID, Map<String, Object>> map = new HashMap<>();
      List<BeanPropertyDescriptor> propertyDescriptorList = BeanPropertyHelper.getBeanPropertyDescriptorList(this.getDao().getType());

      for (T item : itemList) {
         Map<String, Object> vars = new HashMap<>();
         propertyDescriptorList.forEach(
            pd -> {
               if (String.class.equals(pd.getPropertyType())
                  || Date.class.isAssignableFrom(pd.getPropertyType())
                  || Temporal.class.isAssignableFrom(pd.getPropertyType())
                  || Number.class.isAssignableFrom(pd.getPropertyType())) {
                  if (Temporal.class.isAssignableFrom(pd.getPropertyType())) {
                     vars.put(pd.getName().toLowerCase(), ConvertUtils.convert(pd.getPropertyValue(item), Date.class));
                  } else {
                     vars.put(pd.getName().toLowerCase(), pd.getPropertyValue(item));
                  }
               }
            }
         );
         item.getExt$().forEach((k, v) -> vars.put(k.toLowerCase(), v));
         map.put(item.getId(), vars);
      }

      return map;
   }

   default Supplier<String> getDescSupplier(CoreBpmnRuntimeSource<T, ID> source) {
      return () -> ObjectUtils.toString(source.getId());
   }

   default List<String> getChosenAuditorList(RestJsonWrapperBean wrapper) {
      return JSON.parseArray(wrapper.getParamValue("auditor"), String.class);
   }

   default List<CoreBpmnRuntimeSource<T, ID>> parseRuntimeSource(List<ID> itemIdList, RestJsonWrapperBean wrapper) {
      List<T> selectedItemList = this.getBpmnVarsItemList(itemIdList);
      Map<ID, Map<String, Object>> map = this.getBpmnVars(wrapper, selectedItemList);
      CoreBpmnParameterDTO parameter = CoreBpmnParameterDTO.of(wrapper);
      List<CoreBpmnRuntimeSource<T, ID>> sourceList = new ArrayList<>();

      for (T item : selectedItemList) {
         CoreBpmnRuntimeSource<T, ID> source = CoreBpmnRuntimeSource.of(this.getDao().getTable(), item, parameter.getComment(), map.get(item.getId()));
         source.setDescSupplier(this.getDescSupplier(source));
         sourceList.add(source);
      }

      boolean draft = selectedItemList.stream().anyMatch(i -> StringUtils.startsWithIgnoreCase(i.getProcessStatus(), ProcessStatus.DRAFT.name()));
      if (draft) {
         List<Pair<ID, Long>> procIdList = this.getBpmnProcIdList(selectedItemList);
         sourceList.forEach(s -> s.setProcId(procIdList.stream().filter(p -> s.getId().equals(p.getFirst())).findAny().get().getSecond()));
      }

      return sourceList;
   }

   default List<CoreBpmnRuntimeSource<T, ID>> parseSubmittableRuntimeSource(
      List<ID> itemIdList, CoreBpmnParameterDTO parameter, Map<ID, Map<String, Object>> replacementMap
   ) {
      List<T> selectedItemList = this.getBpmnVarsItemList(itemIdList);
      List<Pair<ID, Long>> procIdList = this.getBpmnProcIdList(selectedItemList);
      if (procIdList.isEmpty()) {
         throw new ApplicationRuntimeException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.PROCESS_NOT_EXISTS");
      } else {
         List<CoreBpmnRuntimeSource<T, ID>> sourceList = new ArrayList<>();

         for (T item : selectedItemList) {
            CoreBpmnRuntimeSource<T, ID> source = CoreBpmnRuntimeSource.of(
               this.getDao().getTable(), item, parameter.getComment(), replacementMap.get(item.getId())
            );
            Pair<ID, Long> pair = procIdList.stream().filter(p -> item.getId().equals(p.getFirst())).findAny().orElse(null);
            if (pair == null) {
               throw new ApplicationRuntimeException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.PROCESS_NOT_EXISTS");
            }

            source.setProcId(pair.getSecond());
            source.setDescSupplier(this.getDescSupplier(source));
            String attachment = parameter.getAttachment();
            if (!StringUtils.isBlank(attachment)) {
               source.setAttachmentId(NumberUtils.parseLong(attachment));
            }

            for (String nextCandidator : parameter.getNextCandidators()) {
               source.addNextCandidator(nextCandidator);
            }

            for (String nextCandidateRole : parameter.getNextCandidateRoles()) {
               source.addNextCandidatorRole(NumberUtils.parseLong(nextCandidateRole));
            }

            source.setBpmnTaskuserStrategy(parameter.getBpmnTaskuserStrategy());
            sourceList.add(source);
         }

         return sourceList;
      }
   }

   default List<CoreBpmnRuntimeSource<T, ID>> parseAuditableRuntimeSource(
      List<ID> itemIdList, CoreBpmnParameterDTO parameter, Map<ID, Map<String, Object>> replacementMap
   ) {
      List<T> selectedItemList = this.getBpmnVarsItemList(itemIdList);
      List<CoreBpmnRuntimeSource<T, ID>> sourceList = new ArrayList<>();

      for (T item : selectedItemList) {
         CoreBpmnRuntimeSource<T, ID> source = CoreBpmnRuntimeSource.of(
            this.getDao().getTable(), item, parameter.getComment(), replacementMap.get(item.getId())
         );
         source.setCurrentStatusCode(parameter.getCurrentStatusCode());
         source.setTargetStatusCode(parameter.getTargetStatusCode());
         String attachment = parameter.getAttachment();
         if (!StringUtils.isBlank(attachment)) {
            source.setAttachmentId(NumberUtils.parseLong(attachment));
         }

         for (String nextCandidator : parameter.getNextCandidators()) {
            source.addNextCandidator(nextCandidator);
         }

         for (String nextCandidateRole : parameter.getNextCandidateRoles()) {
            source.addNextCandidatorRole(NumberUtils.parseLong(nextCandidateRole));
         }

         if (!StringUtils.isBlank(parameter.getTransferCandidator())) {
            source.setTransferCandidator(parameter.getTransferCandidator());
         }

         source.setDescSupplier(this.getDescSupplier(source));
         source.setBpmnTaskuserStrategy(parameter.getBpmnTaskuserStrategy());
         sourceList.add(source);
      }

      return sourceList;
   }

   default List<CoreBpmnRuntimeSource<T, ID>> parseUndoableRuntimeSource(
      List<ID> itemIdList, CoreBpmnParameterDTO parameter, Map<ID, Map<String, Object>> replacementMap
   ) {
      List<T> selectedItemList = this.getBpmnVarsItemList(itemIdList);
      List<CoreBpmnRuntimeSource<T, ID>> sourceList = new ArrayList<>();

      for (T item : selectedItemList) {
         CoreBpmnRuntimeSource<T, ID> source = CoreBpmnRuntimeSource.of(
            this.getDao().getTable(), item, parameter.getComment(), replacementMap.get(item.getId())
         );
         source.setDescSupplier(this.getDescSupplier(source));
         sourceList.add(source);
      }

      return sourceList;
   }

   default List<CoreBpmnRuntimeSource<T, ID>> parseWithdrawableRuntimeSource(
      List<ID> itemIdList, CoreBpmnParameterDTO parameter, Map<ID, Map<String, Object>> replacementMap
   ) {
      List<T> selectedItemList = this.getBpmnVarsItemList(itemIdList);
      List<CoreBpmnRuntimeSource<T, ID>> sourceList = new ArrayList<>();

      for (T item : selectedItemList) {
         CoreBpmnRuntimeSource<T, ID> source = CoreBpmnRuntimeSource.of(
            this.getDao().getTable(), item, parameter.getComment(), replacementMap.get(item.getId())
         );
         source.setDescSupplier(this.getDescSupplier(source));
         sourceList.add(source);
      }

      return sourceList;
   }

   default List<Pair<ID, Long>> getBpmnProcIdList(List<T> itemList) {
      CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
      Map<ID, CoreBpmnProcBean> map = runtimeService.selectRuntimeBpmnProcList(itemList);
      return map.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue().getId())).collect(Collectors.toList());
   }

   default void checkAndMerge(CoreBpmnTaskStatusDTO taskStatus, CoreBpmnTaskStatusDTO newTaskStatus) {
      if (taskStatus != null) {
         if (taskStatus.getAuthRequired() != newTaskStatus.getAuthRequired()) {
            throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.TIP.AUTH_REQUIRED_DIFFER");
         } else if (!ObjectUtils.equals(taskStatus.getAttachmentStrategy(), newTaskStatus.getAttachmentStrategy())) {
            throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.TIP.ATTACHMENT_STRATEGY_DIFFER");
         } else {
            if (taskStatus.isTransfer() != newTaskStatus.isTransfer()) {
               taskStatus.setTransfer(false);
            }

            if (NextCandidatorOptStrategy.assigned.equals(taskStatus.getNextCandidatorOptStrategy()) && taskStatus.getCandidatorList() != null) {
               if (newTaskStatus.getCandidatorList() == null) {
                  taskStatus.setCandidatorList(null);
               } else {
                  taskStatus.getCandidatorList().removeIf(c -> newTaskStatus.getCandidatorList().stream().noneMatch(n -> n.getId().equals(c.getId())));
               }
            }

            if (NextCandidatorOptStrategy.assignedRole.equals(taskStatus.getNextCandidatorOptStrategy()) && taskStatus.getCandidateRoleList() != null) {
               if (newTaskStatus.getCandidateRoleList() == null) {
                  taskStatus.setCandidateRoleList(null);
               } else {
                  taskStatus.getCandidateRoleList().removeIf(c -> newTaskStatus.getCandidateRoleList().stream().noneMatch(n -> n.getId().equals(c.getId())));
               }
            }
         }
      }
   }

   default void checkAndUpdateVersion(List<T> itemList) {
      EntityColumnContext versionContext = DatabaseManager.getEntityContext(this.getDao().getType()).getVersionContext();
      if (versionContext != null) {
         List<ID> idList = itemList.stream().filter(i -> i.getVersion() != null).map(i -> i.getId()).collect(Collectors.toList());
         if (!idList.isEmpty()) {
            for (T selectedVersionItem : this.<GenericDao<T, ID>>getDao().selectListByIds(idList, Arrays.asList("ID", "VERSION"))) {
               if (itemList.stream().noneMatch(i -> i.getId().equals(selectedVersionItem.getId()) && i.getVersion().equals(selectedVersionItem.getVersion()))) {
                  throw new ApplicationWarningException("GIKAM.EXCEPTION.CONRRENT_MODIFICATION");
               }
            }
         }

         itemList.forEach(i -> i.setVersion(Optional.ofNullable(i.getVersion()).orElse(1L) + 1L));
         this.<GenericDao<T, ID>>getDao().update(itemList, "VERSION");
      }
   }

   default void autoPass(List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList, List<CoreBpmnInstanceStatusDTO<ID>> instanceStatusList) {
      List<ID> autoPassIdList = new ArrayList<>();
      int i = 0;

      for (int j = runtimeSourceList.size(); i < j; i++) {
         CoreBpmnRuntimeSource<T, ID> runtimeSource = runtimeSourceList.get(i);
         if (runtimeSource.isAutoPass()) {
            instanceStatusList.removeIf(s -> s.getId().equals(runtimeSource.getId()));
            autoPassIdList.add(runtimeSource.getId());
         }
      }

      if (!autoPassIdList.isEmpty()) {
         CoreBpmnParameterDTO bpmnParameter = new CoreBpmnParameterDTO();
         bpmnParameter.setComment(I18nHelper.getMessage("GIKAM.BPMN.COMMENT.AUTO_PASS"));
         instanceStatusList.addAll(this.completeTask(autoPassIdList, bpmnParameter));
      }
   }

   default void prependStatusCode(List<CoreBpmnRuntimeSource<T, ID>> runtimeSourceList, String prefix) {
      List<T> updateItemList = new ArrayList<>();

      for (CoreBpmnRuntimeSource<T, ID> runtimeSource : runtimeSourceList) {
         T item = ClassUtils.newInstance(this.getDao().getType());
         item.setId(runtimeSource.getId());
         item.setProcessStatus(runtimeSource.getOldItem().getProcessStatus());
         item.setProcessStatus(prefix + item.getProcessStatus());
         updateItemList.add(item);
      }

      this.<GenericDao<T, ID>>getDao().update(updateItemList, "PROCESSSTATUS");
   }

   default void rollbackTask(CoreBpmnRuntimeSource<T, ID> runtimeSource) {
      T item = runtimeSource.getOldItem();
      if (StringUtils.startsWith(item.getProcessStatus(), "RUN$")) {
         item.setProcessStatus(StringUtils.removeStart(item.getProcessStatus(), "RUN$"));
         this.getDao().update(item, "PROCESSSTATUS");
      }
   }
}
