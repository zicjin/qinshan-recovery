package com.sunwayworld.cloud.module.lcdp.base.service;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.lcdp.base.LcdpAuditableDTO;
import com.sunwayworld.cloud.module.lcdp.base.annotation.Mapping;
import com.sunwayworld.cloud.module.lcdp.base.annotation.MappingType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.beans.BeanPropertyHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.context.TableContext;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.progress.core.ProgressTask;
import com.sunwayworld.framework.progress.service.ProgressService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnParameterDTO;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnTaskDTO;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnTaskStatusDTO;
import com.sunwayworld.framework.support.base.bean.CoreServiceStatusDTO;
import com.sunwayworld.framework.utils.BeanUtils;
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
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnTaskCandidatorsDTO;
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
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.dom4j.Element;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

public interface LcdpAuditService extends LcdpBaseService {
   @Mapping(
      value = "核心编制页面查询数据（工作流）",
      type = MappingType.SELECT
   )
   @Override
   default Page<Map<String, Object>> selectRawPagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      parameter.setProcessStatus("PROCESSSTATUS", ProcessStatus.DRAFT);
      parameter.setRawQueries();
      if (!"0".equals(wrapper.getParamValue("orgAuthority"))) {
         parameter.setOrgAuthority();
      }

      if (!wrapper.getAuthorityList().isEmpty()) {
         wrapper.getAuthorityList().forEach(a -> parameter.setAuthorityParameter(a));
      }

      parameter.setTableName(this.getTable());
      return this.selectLcdpPagination(parameter, rowBounds);
   }

   @Mapping(
      value = "核心审核页面查询数据（工作流）",
      type = MappingType.SELECT
   )
   @Override
   default Page<Map<String, Object>> selectAuditablePagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      parameter.setProcessStatus("PROCESSSTATUS", ProcessStatus.APPROVE);
      parameter.setAuditAuthority();
      if (!"0".equals(wrapper.getParamValue("orgAuthority"))) {
         parameter.setOrgAuthority();
      }

      parameter.setAuditableQueries();
      if (!wrapper.getAuthorityList().isEmpty()) {
         wrapper.getAuthorityList().forEach(a -> parameter.setAuthorityParameter(a));
      }

      parameter.setTableName(this.getTable());
      return this.selectLcdpPagination(parameter, rowBounds);
   }

   @Mapping(
      value = "核心查询页面查询数据",
      type = MappingType.SELECT
   )
   @Override
   default Page<Map<String, Object>> selectSearchablePagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      parameter.setProcessStatus("PROCESSSTATUS", ProcessStatus.APPROVE_OR_DONE);
      if (!"0".equals(wrapper.getParamValue("orgAuthority"))) {
         parameter.setOrgAuthority();
      }

      if (!wrapper.getAuthorityList().isEmpty()) {
         wrapper.getAuthorityList().forEach(a -> parameter.setAuthorityParameter(a));
      }

      parameter.setTableName(this.getTable());
      return this.selectLcdpPagination(parameter, rowBounds);
   }

   @Mapping(
      value = "核心撤回页面查询数据",
      type = MappingType.SELECT
   )
   @Override
   default Page<Map<String, Object>> selectWithdrawablePagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      parameter.setProcessStatus("PROCESSSTATUS", ProcessStatus.APPROVE);
      if (!"0".equals(wrapper.getParamValue("orgAuthority"))) {
         parameter.setOrgAuthority();
      }

      parameter.setWithdrawableQueries();
      if (!wrapper.getAuthorityList().isEmpty()) {
         wrapper.getAuthorityList().forEach(a -> parameter.setAuthorityParameter(a));
      }

      parameter.setTableName(this.getTable());
      return this.selectLcdpPagination(parameter, rowBounds);
   }

   @Mapping(
      value = "回退页查询",
      type = MappingType.SELECT
   )
   @Override
   default Page<Map<String, Object>> selectUndoablePagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      parameter.setProcessStatus("PROCESSSTATUS", ProcessStatus.DONE);
      if (!"0".equals(wrapper.getParamValue("orgAuthority"))) {
         parameter.setOrgAuthority();
      }

      parameter.setUndoableQueries();
      if (!wrapper.getAuthorityList().isEmpty()) {
         wrapper.getAuthorityList().forEach(a -> parameter.setAuthorityParameter(a));
      }

      parameter.setTableName(this.getTable());
      return this.selectLcdpPagination(parameter, rowBounds);
   }

   @Mapping(
      value = "核心工作流启动",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_START")
   @Override
   default List<CoreBpmnInstanceStatusDTO<String>> startProcess(RestJsonWrapperBean wrapper) {
      List<LcdpAuditableDTO> itemList = wrapper.parse(LcdpAuditableDTO.class);
      if (itemList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         itemList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getTable(), i.getId())));
         this.checkAndUpdateVersion(itemList);
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<String> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<LcdpAuditableDTO> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseSubmittableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         List<CoreBpmnInstanceStatusDTO<String>> instanceStatusList = runtimeService.startProcess(runtimeSourceList);
         this.autoPass(runtimeSourceList, instanceStatusList);
         return instanceStatusList;
      }
   }

   @Mapping(
      value = "核心工作流启动（进度监控)",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_START")
   default String startProcessWithProgress(List<String> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return null;
      } else {
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<LcdpAuditableDTO> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseSubmittableRuntimeSource(
            itemIdList, parameter, this.getBpmnVars(null, selectItemList)
         );
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<LcdpAuditableDTO, String> runtimeSource : runtimeSourceList) {
            ProgressTask runnable = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  List<CoreBpmnInstanceStatusDTO<String>> innerInstanceStatusList = runtimeService.startProcess(innerRuntimeSourceList);
                  LcdpAuditService.this.autoPass(innerRuntimeSourceList, innerInstanceStatusList);
               }

               @Override
               public void rollback() {
                  LcdpAuditableDTO item = runtimeSource.getOldItem();
                  if (StringUtils.startsWith(item.getProcessStatus(), "RUN$")) {
                     item.setProcessStatus(StringUtils.removeStart(item.getProcessStatus(), "RUN$"));
                     LcdpAuditService.this.getMapDao().update(LcdpAuditService.this.getTable(), LcdpAuditService.this.toMap(item), "PROCESSSTATUS");
                  }
               }
            };
            taskList.add(runnable);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.SUBMIT_NAME", I18nHelper.getMessage(this.getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Mapping(
      value = "核心工作流启动（进度监控)",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_START")
   @Override
   default String startProcessWithProgress(RestJsonWrapperBean wrapper) {
      List<LcdpAuditableDTO> itemList = wrapper.parse(LcdpAuditableDTO.class);
      if (itemList.isEmpty()) {
         return null;
      } else {
         this.checkAndUpdateVersion(itemList);
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<String> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<LcdpAuditableDTO> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseSubmittableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<LcdpAuditableDTO, String> runtimeSource : runtimeSourceList) {
            ProgressTask runnable = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  List<CoreBpmnInstanceStatusDTO<String>> innerInstanceStatusList = runtimeService.startProcess(innerRuntimeSourceList);
                  LcdpAuditService.this.autoPass(innerRuntimeSourceList, innerInstanceStatusList);
               }

               @Override
               public void rollback() {
                  LcdpAuditableDTO item = runtimeSource.getOldItem();
                  if (StringUtils.startsWith(item.getProcessStatus(), "RUN$")) {
                     item.setProcessStatus(StringUtils.removeStart(item.getProcessStatus(), "RUN$"));
                     LcdpAuditService.this.getMapDao().update(LcdpAuditService.this.getTable(), LcdpAuditService.this.toMap(item), "PROCESSSTATUS");
                  }
               }
            };
            taskList.add(runnable);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.SUBMIT_NAME", I18nHelper.getMessage(this.getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Mapping(
      value = "核心工作流启动",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_START")
   default List<CoreBpmnInstanceStatusDTO<String>> startProcess(List<String> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<LcdpAuditableDTO> itemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseSubmittableRuntimeSource(
            itemIdList, parameter, this.getBpmnVars(null, itemList)
         );
         List<CoreBpmnInstanceStatusDTO<String>> instanceStatusList = runtimeService.startProcess(runtimeSourceList);
         this.autoPass(runtimeSourceList, instanceStatusList);
         return instanceStatusList;
      }
   }

   @Mapping(
      value = "核心工作流启动",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_START")
   default List<CoreBpmnInstanceStatusDTO<String>> startProcess(List<String> itemIdList, String comment) {
      CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
      parameter.setComment(comment);
      return this.startProcess(itemIdList, parameter);
   }

   @Mapping(
      value = "核心工作流撤回",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_WITHDRAW")
   @Override
   default List<CoreBpmnInstanceStatusDTO<String>> withdrawProcess(RestJsonWrapperBean wrapper) {
      List<LcdpAuditableDTO> itemList = wrapper.parse(LcdpAuditableDTO.class);
      if (itemList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         this.checkAndUpdateVersion(itemList);
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<String> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<LcdpAuditableDTO> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseWithdrawableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         return runtimeService.withdrawProcess(runtimeSourceList);
      }
   }

   @Mapping(
      value = "核心工作流撤回（进度监控）",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_WITHDRAW")
   @Override
   default String withdrawProcessWithProgress(RestJsonWrapperBean wrapper) {
      List<LcdpAuditableDTO> itemList = wrapper.parse(LcdpAuditableDTO.class);
      if (itemList.isEmpty()) {
         return null;
      } else {
         this.checkAndUpdateVersion(itemList);
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<String> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<LcdpAuditableDTO> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseWithdrawableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<LcdpAuditableDTO, String> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  runtimeService.withdrawProcess(innerRuntimeSourceList);
               }

               @Override
               public void rollback() {
                  LcdpAuditService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.WITHDRAW_NAME", I18nHelper.getMessage(this.getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Mapping(
      value = "核心工作流撤回（进度监控）",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_WITHDRAW")
   default String withdrawProcessWithProgress(List<String> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return null;
      } else {
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<LcdpAuditableDTO> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseWithdrawableRuntimeSource(
            itemIdList, parameter, this.getBpmnVars(null, selectItemList)
         );
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<LcdpAuditableDTO, String> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  runtimeService.withdrawProcess(innerRuntimeSourceList);
               }

               @Override
               public void rollback() {
                  LcdpAuditService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.WITHDRAW_NAME", I18nHelper.getMessage(this.getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Mapping(
      value = "核心工作流撤回",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_WITHDRAW")
   default List<CoreBpmnInstanceStatusDTO<String>> withdrawProcess(List<String> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<LcdpAuditableDTO> itemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseWithdrawableRuntimeSource(
            itemIdList, parameter, this.getBpmnVars(null, itemList)
         );
         return runtimeService.withdrawProcess(runtimeSourceList);
      }
   }

   @Mapping(
      value = "核心工作流撤回",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_WITHDRAW")
   default List<CoreBpmnInstanceStatusDTO<String>> withdrawProcess(List<String> itemIdList, String comment) {
      CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
      parameter.setComment(comment);
      return this.withdrawProcess(itemIdList, parameter);
   }

   @Mapping(
      value = "核心工作流审核通过",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_COMPLETE")
   @Override
   default List<CoreBpmnInstanceStatusDTO<String>> completeTask(RestJsonWrapperBean wrapper) {
      List<LcdpAuditableDTO> itemList = wrapper.parse(LcdpAuditableDTO.class);
      if (itemList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         itemList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getTable(), i.getId())));
         this.checkAndUpdateVersion(itemList);
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<String> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<LcdpAuditableDTO> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         boolean flag = runtimeSourceList.stream()
            .anyMatch(e -> !StringUtils.startsWithIgnoreCase(e.getOldItem().getProcessStatus(), ProcessStatus.APPROVE.name()));
         if (flag) {
            throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.PROCESS_STATUS_UPDATED");
         } else {
            List<CoreBpmnInstanceStatusDTO<String>> instanceStatusList = runtimeService.completeTask(runtimeSourceList);
            this.autoPass(runtimeSourceList, instanceStatusList);
            return instanceStatusList;
         }
      }
   }

   @Mapping(
      value = "核心工作流审核通过（进度监控）",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_COMPLETE")
   @Override
   default String completeTaskWithProgress(RestJsonWrapperBean wrapper) {
      List<LcdpAuditableDTO> itemList = wrapper.parse(LcdpAuditableDTO.class);
      if (itemList.isEmpty()) {
         return null;
      } else {
         this.checkAndUpdateVersion(itemList);
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<String> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<LcdpAuditableDTO> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<LcdpAuditableDTO, String> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  List<CoreBpmnInstanceStatusDTO<String>> innerInstanceStatusList = runtimeService.completeTask(innerRuntimeSourceList);
                  LcdpAuditService.this.autoPass(innerRuntimeSourceList, innerInstanceStatusList);
               }

               @Override
               public void rollback() {
                  LcdpAuditService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.COMPLETE_NAME", I18nHelper.getMessage(this.getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Mapping(
      value = "核心工作流审核通过（进度监控）",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_COMPLETE")
   default String completeTaskWithProgress(List<String> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return null;
      } else {
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<LcdpAuditableDTO> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, parameter, this.getBpmnVars(null, selectItemList)
         );
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<LcdpAuditableDTO, String> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  List<CoreBpmnInstanceStatusDTO<String>> innerInstanceStatusList = runtimeService.completeTask(innerRuntimeSourceList);
                  LcdpAuditService.this.autoPass(innerRuntimeSourceList, innerInstanceStatusList);
               }

               @Override
               public void rollback() {
                  LcdpAuditService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.COMPLETE_NAME", I18nHelper.getMessage(this.getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Mapping(
      value = "核心工作流审核通过",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_COMPLETE")
   default List<CoreBpmnInstanceStatusDTO<String>> completeTask(List<String> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<LcdpAuditableDTO> itemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, parameter, this.getBpmnVars(null, itemList)
         );
         List<CoreBpmnInstanceStatusDTO<String>> instanceStatusList = runtimeService.completeTask(runtimeSourceList);
         this.autoPass(runtimeSourceList, instanceStatusList);
         return instanceStatusList;
      }
   }

   @Mapping(
      value = "核心工作流审核通过",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_COMPLETE")
   default List<CoreBpmnInstanceStatusDTO<String>> completeTask(List<String> itemIdList, String comment) {
      CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
      parameter.setComment(comment);
      return this.completeTask(itemIdList, parameter);
   }

   @Mapping(
      value = "核心工作流转办",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_TRANSFER")
   @Override
   default void transferTask(RestJsonWrapperBean wrapper) {
      List<LcdpAuditableDTO> itemList = wrapper.parse(LcdpAuditableDTO.class);
      if (!itemList.isEmpty()) {
         this.checkAndUpdateVersion(itemList);
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<String> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), CollectionUtils.emptyMap()
         );
         runtimeService.transferTask(runtimeSourceList);
      }
   }

   @Mapping(
      value = "核心工作流转办（进度监控）",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_TRANSFER")
   @Override
   default String transferTaskWithProgress(RestJsonWrapperBean wrapper) {
      List<LcdpAuditableDTO> itemList = wrapper.parse(LcdpAuditableDTO.class);
      if (itemList.isEmpty()) {
         return null;
      } else {
         this.checkAndUpdateVersion(itemList);
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<String> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), CollectionUtils.emptyMap()
         );
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<LcdpAuditableDTO, String> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  runtimeService.transferTask(innerRuntimeSourceList);
               }

               @Override
               public void rollback() {
                  LcdpAuditService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.TRANSFER_NAME", I18nHelper.getMessage(this.getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Mapping(
      value = "核心工作流结束",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_END")
   @Override
   default List<CoreBpmnInstanceStatusDTO<String>> endTask(RestJsonWrapperBean wrapper) {
      List<LcdpAuditableDTO> itemList = wrapper.parse(LcdpAuditableDTO.class);
      if (itemList.isEmpty()) {
         return null;
      } else {
         this.checkAndUpdateVersion(itemList);
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<String> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<LcdpAuditableDTO> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         return runtimeService.endTask(runtimeSourceList);
      }
   }

   @Mapping(
      value = "核心工作流结束（进度监控）",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_END")
   @Override
   default String endTaskWithProgress(RestJsonWrapperBean wrapper) {
      List<LcdpAuditableDTO> itemList = wrapper.parse(LcdpAuditableDTO.class);
      if (itemList.isEmpty()) {
         return null;
      } else {
         this.checkAndUpdateVersion(itemList);
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<String> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<LcdpAuditableDTO> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<LcdpAuditableDTO, String> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  runtimeService.endTask(innerRuntimeSourceList);
               }

               @Override
               public void rollback() {
                  LcdpAuditService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.END_NAME", I18nHelper.getMessage(this.getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Mapping(
      value = "核心工作流结束（进度监控）",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_END")
   default String endTaskWithProgress(List<String> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return null;
      } else {
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<LcdpAuditableDTO> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, parameter, this.getBpmnVars(null, selectItemList)
         );
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<LcdpAuditableDTO, String> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  runtimeService.endTask(innerRuntimeSourceList);
               }

               @Override
               public void rollback() {
                  LcdpAuditService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.END_NAME", I18nHelper.getMessage(this.getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Mapping(
      value = "核心工作流结束",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_END")
   default List<CoreBpmnInstanceStatusDTO<String>> endTask(List<String> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<LcdpAuditableDTO> itemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, parameter, this.getBpmnVars(null, itemList)
         );
         return runtimeService.endTask(runtimeSourceList);
      }
   }

   @Mapping(
      value = "核心工作流结束",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_END")
   default List<CoreBpmnInstanceStatusDTO<String>> endTask(List<String> itemIdList, String comment) {
      CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
      parameter.setComment(comment);
      return this.endTask(itemIdList, parameter);
   }

   @Mapping(
      value = "核心工作流审核拒绝",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_REJECT")
   @Override
   default List<CoreBpmnInstanceStatusDTO<String>> rejectTask(RestJsonWrapperBean wrapper) {
      List<LcdpAuditableDTO> itemList = wrapper.parse(LcdpAuditableDTO.class);
      if (itemList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         itemList.forEach(i -> GikamConcurrentLocker.isolated(CoreBpmnHelper.getTargetId(this.getTable(), i.getId())));
         this.checkAndUpdateVersion(itemList);
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<String> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<LcdpAuditableDTO> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         return runtimeService.rejectTask(runtimeSourceList);
      }
   }

   @Mapping(
      value = "核心工作流审核拒绝（进度监控）",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_REJECT")
   @Override
   default String rejectTaskWithProgress(RestJsonWrapperBean wrapper) {
      List<LcdpAuditableDTO> itemList = wrapper.parse(LcdpAuditableDTO.class);
      if (itemList.isEmpty()) {
         return null;
      } else {
         this.checkAndUpdateVersion(itemList);
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<String> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<LcdpAuditableDTO> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<LcdpAuditableDTO, String> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  runtimeService.rejectTask(innerRuntimeSourceList);
               }

               @Override
               public void rollback() {
                  LcdpAuditService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.REJECT_NAME", I18nHelper.getMessage(this.getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Mapping(
      value = "核心工作流审核拒绝（进度监控）",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_REJECT")
   default String rejectTaskWithProgress(List<String> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return null;
      } else {
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<LcdpAuditableDTO> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, parameter, this.getBpmnVars(null, selectItemList)
         );
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<LcdpAuditableDTO, String> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  runtimeService.rejectTask(innerRuntimeSourceList);
               }

               @Override
               public void rollback() {
                  LcdpAuditService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.REJECT_NAME", I18nHelper.getMessage(this.getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Mapping(
      value = "核心工作流审核拒绝",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_REJECT")
   default List<CoreBpmnInstanceStatusDTO<String>> rejectTask(List<String> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         List<LcdpAuditableDTO> itemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, parameter, this.getBpmnVars(null, itemList)
         );
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         return runtimeService.rejectTask(runtimeSourceList);
      }
   }

   @Mapping(
      value = "核心工作流审核拒绝",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_REJECT")
   default List<CoreBpmnInstanceStatusDTO<String>> rejectTask(List<String> itemIdList, String comment) {
      CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
      parameter.setComment(comment);
      return this.rejectTask(itemIdList, parameter);
   }

   @Mapping(
      value = "核心工作流异常拒绝",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_ODD_REJECT")
   @Override
   default List<CoreBpmnInstanceStatusDTO<String>> oddRejectTask(RestJsonWrapperBean wrapper) {
      List<LcdpAuditableDTO> itemList = wrapper.parse(LcdpAuditableDTO.class);
      if (itemList.isEmpty()) {
         return null;
      } else {
         this.checkAndUpdateVersion(itemList);
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<String> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<LcdpAuditableDTO> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         return runtimeService.oddRejectTask(runtimeSourceList);
      }
   }

   @Mapping(
      value = "核心工作流异常拒绝（进度监控）",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_ODD_REJECT")
   @Override
   default String oddRejectTaskWithProgress(RestJsonWrapperBean wrapper) {
      List<LcdpAuditableDTO> itemList = wrapper.parse(LcdpAuditableDTO.class);
      if (itemList.isEmpty()) {
         return null;
      } else {
         this.checkAndUpdateVersion(itemList);
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<String> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<LcdpAuditableDTO> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<LcdpAuditableDTO, String> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  runtimeService.oddRejectTask(innerRuntimeSourceList);
               }

               @Override
               public void rollback() {
                  LcdpAuditService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.ODD_REJECT_NAME", I18nHelper.getMessage(this.getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Mapping(
      value = "核心工作流异常拒oddRejectTaskWithProgress绝（进度监控）",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_ODD_REJECT")
   default String oddRejectTaskWithProgress(List<String> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return null;
      } else {
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<LcdpAuditableDTO> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, parameter, this.getBpmnVars(null, selectItemList)
         );
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<LcdpAuditableDTO, String> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  runtimeService.oddRejectTask(innerRuntimeSourceList);
               }

               @Override
               public void rollback() {
                  LcdpAuditService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.ODD_REJECT_NAME", I18nHelper.getMessage(this.getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Mapping(
      value = "核心工作流异常拒绝",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_ODD_REJECT")
   default List<CoreBpmnInstanceStatusDTO<String>> oddRejectTask(List<String> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         List<LcdpAuditableDTO> itemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseAuditableRuntimeSource(
            itemIdList, parameter, this.getBpmnVars(null, itemList)
         );
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         return runtimeService.oddRejectTask(runtimeSourceList);
      }
   }

   @Mapping(
      value = "核心工作流异常拒绝",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_ODD_REJECT")
   default List<CoreBpmnInstanceStatusDTO<String>> oddRejectTask(List<String> itemIdList, String comment) {
      CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
      parameter.setComment(comment);
      return this.oddRejectTask(itemIdList, parameter);
   }

   @Mapping(
      value = "核心工作流回退",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_UNDO")
   @Override
   default List<CoreBpmnInstanceStatusDTO<String>> undo(RestJsonWrapperBean wrapper) {
      List<LcdpAuditableDTO> itemList = wrapper.parse(LcdpAuditableDTO.class);
      if (itemList.isEmpty()) {
         return null;
      } else {
         this.checkAndUpdateVersion(itemList);
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<String> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<LcdpAuditableDTO> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseUndoableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         return runtimeService.undo(runtimeSourceList);
      }
   }

   @Mapping(
      value = "核心工作流回退（进度监控）",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_UNDO")
   @Override
   default String undoWithProgress(RestJsonWrapperBean wrapper) {
      List<LcdpAuditableDTO> itemList = wrapper.parse(LcdpAuditableDTO.class);
      if (itemList.isEmpty()) {
         return null;
      } else {
         this.checkAndUpdateVersion(itemList);
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<String> itemIdList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         List<LcdpAuditableDTO> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseUndoableRuntimeSource(
            itemIdList, CoreBpmnParameterDTO.of(wrapper), this.getBpmnVars(wrapper, selectItemList)
         );
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<LcdpAuditableDTO, String> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  runtimeService.undo(innerRuntimeSourceList);
               }

               @Override
               public void rollback() {
                  LcdpAuditService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.UNDO_NAME", I18nHelper.getMessage(this.getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Mapping(
      value = "核心工作流回退（进度监控）",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_UNDO")
   default String undoWithProgress(List<String> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return null;
      } else {
         final CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         List<LcdpAuditableDTO> selectItemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseUndoableRuntimeSource(
            itemIdList, parameter, this.getBpmnVars(null, selectItemList)
         );
         this.prependStatusCode(runtimeSourceList, "RUN$");
         List<ProgressTask> taskList = new ArrayList<>();

         for (final CoreBpmnRuntimeSource<LcdpAuditableDTO, String> runtimeSource : runtimeSourceList) {
            ProgressTask task = new ProgressTask(this.getDescSupplier(runtimeSource).get()) {
               @Override
               public void run() {
                  List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> innerRuntimeSourceList = Arrays.asList(runtimeSource);
                  runtimeService.undo(innerRuntimeSourceList);
               }

               @Override
               public void rollback() {
                  LcdpAuditService.this.rollbackTask(runtimeSource);
               }
            };
            taskList.add(task);
         }

         String progressId = StringUtils.randomUUID();
         ProgressService progressService = ApplicationContextHelper.getBean(ProgressService.class);
         progressService.start(
            progressId, I18nHelper.getMessage("GIKAM.BPMN.TASK.UNDO_NAME", I18nHelper.getMessage(this.getTable() + ".SERVICE_NAME")), taskList
         );
         return progressId;
      }
   }

   @Mapping(
      value = "核心工作流回退",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_UNDO")
   default List<CoreBpmnInstanceStatusDTO<String>> undo(List<String> itemIdList, CoreBpmnParameterDTO parameter) {
      if (itemIdList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         List<LcdpAuditableDTO> itemList = this.getBpmnVarsItemList(itemIdList);
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList = this.parseUndoableRuntimeSource(
            itemIdList, parameter, this.getBpmnVars(null, itemList)
         );
         CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
         return runtimeService.undo(runtimeSourceList);
      }
   }

   @Mapping(
      value = "核心工作流回退",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.PROCESS_UNDO")
   default List<CoreBpmnInstanceStatusDTO<String>> undo(List<String> itemIdList, String comment) {
      CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
      parameter.setComment(comment);
      return this.undo(itemIdList, parameter);
   }

   default CoreBpmnTaskDTO getPreviousTask(String id) {
      Map<String, Object> map = this.selectById(TableContext.of(this.getTable()), id);
      if (StringUtils.startsWithIgnoreCase((String)map.get("PROCESSSTATUS"), ProcessStatus.DRAFT.name())) {
         return null;
      } else {
         CoreBpmnCommentService commentService = ApplicationContextHelper.getBean(CoreBpmnCommentService.class);
         CoreBpmnCommentBean latestComment = commentService.selectFirstByFilter(
            SearchFilter.instance().match("TARGETID", CoreBpmnHelper.getTargetId(this.getTable(), id)).filter(MatchPattern.EQ), new Order[]{Order.desc("ID")}
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

   @Mapping(
      value = "获取状态信息",
      type = MappingType.SELECT
   )
   @Override
   default CoreServiceStatusDTO selectStatus(RestJsonWrapperBean wrapper) {
      return new CoreServiceStatusDTO();
   }

   @Mapping(
      value = "核心获取工作流任务节点的状态",
      type = MappingType.SELECT
   )
   @Override
   default Map<String, Object> selectBpmnTaskStatus(RestJsonWrapperBean wrapper) {
      List<String> idList = wrapper.parseId(String.class);
      if (idList.isEmpty()) {
         return new HashMap<>();
      } else {
         String pass = wrapper.getParamValue("bpmn_pass");
         if ("-1".equals(pass)) {
            return new HashMap<>();
         } else {
            List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> sourceList = this.parseRuntimeSource(idList, wrapper);
            if (sourceList.stream().anyMatch(s -> ProcessStatus.DONE.name().equalsIgnoreCase(s.getOldItem().getProcessStatus()))) {
               return new HashMap<>();
            } else {
               CoreBpmnTaskStatusDTO taskStatus = null;

               try {
                  BpmnRuntimeCacheProvider.init(sourceList);

                  for (CoreBpmnRuntimeSource<LcdpAuditableDTO, String> source : sourceList) {
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
                           CoreBpmnInstanceNextTaskElementDTO<String> nextTaskElement = BpmnRuntimeCacheProvider.getNextTask(source);
                           if (NextCandidatorOptStrategy.assigned.equals(sourceStatus.getNextCandidatorOptStrategy())) {
                              List<Element> nextTaskElementList = nextTaskElement.getNextTaskElementList();
                              List<String> candidatorIdList = new ArrayList<>();
                              List<Long> candidateRoleIdList = new ArrayList<>();
                              CoreBpmnInstanceBean instance = BpmnRuntimeCacheProvider.getBpmnRuntimeInstance(source);
                              String initiator = instance == null ? LocalContextHelper.getLoginUserId() : instance.getInitiator();

                              for (Element taskElement : nextTaskElementList) {
                                 CoreBpmnTaskCandidatorsDTO candidators = BpmnDiagramHelper.getUserTaskCandidators(source, taskElement, initiator);
                                 if (candidators.getCandidatorIdList() != null) {
                                    candidatorIdList.addAll(candidators.getCandidatorIdList());
                                 }

                                 if (candidators.getCandidateRoleIdList() != null) {
                                    candidateRoleIdList.addAll(candidators.getCandidateRoleIdList());
                                 }
                              }

                              if (!candidateRoleIdList.isEmpty()) {
                                 CoreRoleUserService roleUserService = ApplicationContextHelper.getBean(CoreRoleUserService.class);
                                 List<CoreRoleUserBean> roleUserList = roleUserService.getDao()
                                    .selectListByOneColumnValues(candidateRoleIdList.stream().distinct().collect(Collectors.toList()), "ROLEID");
                                 roleUserList.forEach(r -> candidatorIdList.add(r.getUserId()));
                              }

                              if (!candidatorIdList.isEmpty()) {
                                 CoreUserService userService = ApplicationContextHelper.getBean(CoreUserService.class);
                                 List<CoreUserBean> candidatorList = userService.selectListByIds(
                                       candidatorIdList.stream().distinct().collect(Collectors.toList())
                                    )
                                    .stream()
                                    .filter(u -> !"deprecated".equals(u.getStatus()))
                                    .collect(Collectors.toList());
                                 if (CandidatorFilterStrategy.sameDept.equals(BpmnDiagramHelper.getCandidatorFilterStrategy(currentElement))) {
                                    String createdByOrgId = (String)PersistableHelper.getPropertyValue(source.getOldItem(), "createdByOrgId");
                                    if (!StringUtils.isBlank(createdByOrgId)) {
                                       candidatorList.removeIf(c -> !c.getOrgId().equals(createdByOrgId));
                                    }
                                 }

                                 sourceStatus.setCandidatorList(candidatorList);
                              }
                           } else if (NextCandidatorOptStrategy.assignedRole.equals(sourceStatus.getNextCandidatorOptStrategy())) {
                              List<Element> nextTaskElementList = nextTaskElement.getNextTaskElementList();
                              List<Long> candidateRoleIdList = new ArrayList<>();
                              CoreBpmnInstanceBean instance = BpmnRuntimeCacheProvider.getBpmnRuntimeInstance(source);
                              String initiator = instance == null ? LocalContextHelper.getLoginUserId() : instance.getInitiator();

                              for (Element taskElement : nextTaskElementList) {
                                 CoreBpmnTaskCandidatorsDTO candidatorsx = BpmnDiagramHelper.getUserTaskCandidators(source, taskElement, initiator);
                                 if (candidatorsx.getCandidateRoleIdList() != null) {
                                    candidateRoleIdList.addAll(candidatorsx.getCandidateRoleIdList());
                                 }
                              }

                              if (!candidateRoleIdList.isEmpty()) {
                                 CoreRoleService roleService = ApplicationContextHelper.getBean(CoreRoleService.class);
                                 sourceStatus.setCandidateRoleList(
                                    roleService.selectListByIds(candidateRoleIdList.stream().distinct().collect(Collectors.toList()))
                                 );
                              }
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
                  return this.toMap(taskStatus);
               }
            }
         }
      }
   }

   default List<LcdpAuditableDTO> getBpmnVarsItemList(List<String> idList) {
      List<Map<String, Object>> mapList = this.selectListByIdList(TableContext.of(this.getTable()), idList);
      List<LcdpAuditableDTO> list = new ArrayList<>();
      mapList.stream().forEach(e -> {
         LcdpAuditableDTO auditableDTO = BeanUtils.parse((Map<String, Object>)e, LcdpAuditableDTO.class);
         auditableDTO.setTableName(this.getTable());
         list.add(auditableDTO);
      });
      return list;
   }

   default Map<String, Map<String, Object>> getBpmnVars(@Nullable RestJsonWrapperBean wrapper, List<LcdpAuditableDTO> itemList) {
      Map<String, Map<String, Object>> map = new HashMap<>();
      List<BeanPropertyDescriptor> propertyDescriptorList = BeanPropertyHelper.getBeanPropertyDescriptorList(LcdpAuditableDTO.class);

      for (LcdpAuditableDTO item : itemList) {
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

   default Supplier<String> getDescSupplier(CoreBpmnRuntimeSource<LcdpAuditableDTO, String> source) {
      return () -> ObjectUtils.toString(source.getId());
   }

   default List<String> getChosenAuditorList(RestJsonWrapperBean wrapper) {
      return JSON.parseArray(wrapper.getParamValue("auditor"), String.class);
   }

   default List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> parseRuntimeSource(List<String> itemIdList, RestJsonWrapperBean wrapper) {
      List<LcdpAuditableDTO> selectedItemList = this.getBpmnVarsItemList(itemIdList);
      Map<String, Map<String, Object>> map = this.getBpmnVars(wrapper, selectedItemList);
      CoreBpmnParameterDTO parameter = CoreBpmnParameterDTO.of(wrapper);
      List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> sourceList = new ArrayList<>();

      for (LcdpAuditableDTO item : selectedItemList) {
         CoreBpmnRuntimeSource<LcdpAuditableDTO, String> source = CoreBpmnRuntimeSource.of(this.getTable(), item, parameter.getComment(), map.get(item.getId()));
         source.setDescSupplier(this.getDescSupplier(source));
         sourceList.add(source);
      }

      boolean draft = selectedItemList.stream().anyMatch(i -> StringUtils.startsWithIgnoreCase(i.getProcessStatus(), ProcessStatus.DRAFT.name()));
      if (draft) {
         List<Pair<String, Long>> procIdList = this.getBpmnProcIdList(selectedItemList);
         sourceList.forEach(s -> s.setProcId(procIdList.stream().filter(p -> ((String)s.getId()).equals(p.getFirst())).findAny().get().getSecond()));
      }

      return sourceList;
   }

   default List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> parseSubmittableRuntimeSource(
      List<String> itemIdList, CoreBpmnParameterDTO parameter, Map<String, Map<String, Object>> replacementMap
   ) {
      List<LcdpAuditableDTO> selectedItemList = this.getBpmnVarsItemList(itemIdList);
      List<Pair<String, Long>> procIdList = this.getBpmnProcIdList(selectedItemList);
      if (procIdList.isEmpty()) {
         throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.PROCESS_NOT_EXISTS");
      } else {
         List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> sourceList = new ArrayList<>();

         for (LcdpAuditableDTO item : selectedItemList) {
            CoreBpmnRuntimeSource<LcdpAuditableDTO, String> source = CoreBpmnRuntimeSource.of(
               this.getTable(), item, parameter.getComment(), replacementMap.get(item.getId())
            );
            Pair<String, Long> pair = procIdList.stream().filter(p -> item.getId().equals(p.getFirst())).findAny().orElse(null);
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

            sourceList.add(source);
         }

         return sourceList;
      }
   }

   default List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> parseAuditableRuntimeSource(
      List<String> itemIdList, CoreBpmnParameterDTO parameter, Map<String, Map<String, Object>> replacementMap
   ) {
      List<LcdpAuditableDTO> selectedItemList = this.getBpmnVarsItemList(itemIdList);
      List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> sourceList = new ArrayList<>();

      for (LcdpAuditableDTO item : selectedItemList) {
         CoreBpmnRuntimeSource<LcdpAuditableDTO, String> source = CoreBpmnRuntimeSource.of(
            this.getTable(), item, parameter.getComment(), replacementMap.get(item.getId())
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
         sourceList.add(source);
      }

      return sourceList;
   }

   default List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> parseUndoableRuntimeSource(
      List<String> itemIdList, CoreBpmnParameterDTO parameter, Map<String, Map<String, Object>> replacementMap
   ) {
      List<LcdpAuditableDTO> selectedItemList = this.getBpmnVarsItemList(itemIdList);
      List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> sourceList = new ArrayList<>();

      for (LcdpAuditableDTO item : selectedItemList) {
         CoreBpmnRuntimeSource<LcdpAuditableDTO, String> source = CoreBpmnRuntimeSource.of(
            this.getTable(), item, parameter.getComment(), replacementMap.get(item.getId())
         );
         source.setDescSupplier(this.getDescSupplier(source));
         sourceList.add(source);
      }

      return sourceList;
   }

   default List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> parseWithdrawableRuntimeSource(
      List<String> itemIdList, CoreBpmnParameterDTO parameter, Map<String, Map<String, Object>> replacementMap
   ) {
      List<LcdpAuditableDTO> selectedItemList = this.getBpmnVarsItemList(itemIdList);
      List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> sourceList = new ArrayList<>();

      for (LcdpAuditableDTO item : selectedItemList) {
         CoreBpmnRuntimeSource<LcdpAuditableDTO, String> source = CoreBpmnRuntimeSource.of(
            this.getTable(), item, parameter.getComment(), replacementMap.get(item.getId())
         );
         source.setDescSupplier(this.getDescSupplier(source));
         sourceList.add(source);
      }

      return sourceList;
   }

   default List<Pair<String, Long>> getBpmnProcIdList(List<LcdpAuditableDTO> itemList) {
      CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
      Map<String, CoreBpmnProcBean> map = runtimeService.selectRuntimeBpmnProcList(itemList);
      return map.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue().getId())).collect(Collectors.toList());
   }

   default void checkAndMerge(CoreBpmnTaskStatusDTO taskStatus, CoreBpmnTaskStatusDTO newTaskStatus) {
      if (taskStatus != null) {
         if (taskStatus.getAuthRequired() != newTaskStatus.getAuthRequired()) {
            throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.TIP.AUTH_REQUIRED_DIFFER");
         } else if (!ObjectUtils.equals(taskStatus.getAttachmentStrategy(), newTaskStatus.getAttachmentStrategy())) {
            throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.TIP.ATTACHMENT_STRATEGY_DIFFER");
         } else if (!ObjectUtils.equals(taskStatus.getNextCandidatorOptStrategy(), newTaskStatus.getNextCandidatorOptStrategy())) {
            throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.TIP.NEXT_CANDIDATOR_OPT_STRATEGY_DIFFER");
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

   default void checkAndUpdateVersion(List<LcdpAuditableDTO> itemList) {
   }

   default void autoPass(List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList, List<CoreBpmnInstanceStatusDTO<String>> instanceStatusList) {
      List<String> autoPassIdList = new ArrayList<>();
      int i = 0;

      for (int j = runtimeSourceList.size(); i < j; i++) {
         CoreBpmnRuntimeSource<LcdpAuditableDTO, String> runtimeSource = runtimeSourceList.get(i);
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

   default void prependStatusCode(List<CoreBpmnRuntimeSource<LcdpAuditableDTO, String>> runtimeSourceList, String prefix) {
      List<LcdpAuditableDTO> updateItemList = new ArrayList<>();

      for (CoreBpmnRuntimeSource<LcdpAuditableDTO, String> runtimeSource : runtimeSourceList) {
         LcdpAuditableDTO item = ClassUtils.newInstance(LcdpAuditableDTO.class);
         item.setId(runtimeSource.getId());
         item.setProcessStatus(runtimeSource.getOldItem().getProcessStatus());
         item.setProcessStatus(prefix + item.getProcessStatus());
         updateItemList.add(item);
      }

      this.update(this.getTable(), this.toMapList(updateItemList), new String[]{"PROCESSSTATUS"});
   }

   default void rollbackTask(CoreBpmnRuntimeSource<LcdpAuditableDTO, String> runtimeSource) {
      LcdpAuditableDTO item = runtimeSource.getOldItem();
      if (StringUtils.startsWith(item.getProcessStatus(), "RUN$")) {
         item.setProcessStatus(StringUtils.removeStart(item.getProcessStatus(), "RUN$"));
         this.getMapDao().update(this.getTable(), this.toMap(item), "PROCESSSTATUS");
      }
   }
}
