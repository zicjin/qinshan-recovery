package com.sunwayworld.module.sys.bpmn.engine.support;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.executor.ExecutorHelper;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.jdk.tools.DynamicClassLoader;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.support.base.dao.MapDao;
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.SpringUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.utils.XmlUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.bpmn.CoreBpmnHelper;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnCommentBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceCandidatorDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskCandidatorDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskHisBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskHisCandidatorDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskRoleBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskUserBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnProcBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnTargetBean;
import com.sunwayworld.module.sys.bpmn.diagram.BpmnDiagramHelper;
import com.sunwayworld.module.sys.bpmn.diagram.CommentStatus;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import com.sunwayworld.module.sys.bpmn.diagram.TaskStatus;
import com.sunwayworld.module.sys.bpmn.diagram.attribute.CandidatorFilterStrategy;
import com.sunwayworld.module.sys.bpmn.diagram.attribute.CounterSignStrategy;
import com.sunwayworld.module.sys.bpmn.engine.CoreBpmnRuntimeService;
import com.sunwayworld.module.sys.bpmn.engine.CoreBpmnRuntimeSource;
import com.sunwayworld.module.sys.bpmn.engine.cache.BpmnRuntimeCacheProvider;
import com.sunwayworld.module.sys.bpmn.engine.cache.BpmnRuntimeData;
import com.sunwayworld.module.sys.bpmn.engine.cache.BpmnRuntimeData.BpmnProcessData;
import com.sunwayworld.module.sys.bpmn.event.CoreBpmnNotificationEvent;
import com.sunwayworld.module.sys.bpmn.event.CoreBpmnUpdateStatusEvent;
import com.sunwayworld.module.sys.bpmn.exception.BpmnException;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnCommentService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnDiagramService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceTaskHisService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceTaskRoleService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceTaskService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceTaskUserService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnProcService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnTargetService;
import com.sunwayworld.module.sys.menu.service.CoreMenuExtService;
import com.sunwayworld.module.sys.role.bean.CoreRoleBean;
import com.sunwayworld.module.sys.role.bean.CoreRoleUserBean;
import com.sunwayworld.module.sys.role.service.CoreRoleService;
import com.sunwayworld.module.sys.role.service.CoreRoleUserService;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public abstract class AbstractCoreBpmnRuntimeProcessSupport<T extends Auditable<ID>, ID extends Serializable> {
   private static final Logger logger = LoggerFactory.getLogger(AbstractCoreBpmnRuntimeProcessSupport.class);
   protected static final String BPMN_VALIDATOR_SQL_SN = "SN_";
   protected static final String BPMN_VALIDATOR_SQL_TYPE = "TYPE_";
   protected static final String BPMN_VALIDATOR_SQL_DESC = "DESC_";
   protected static final String BPMN_VALIDATOR_SQL_TABLE = "TABLE_";
   protected final GlobalMapper globalMapper = ApplicationContextHelper.getBean(GlobalMapper.class);
   protected final CoreBpmnProcService procService = ApplicationContextHelper.getBean(CoreBpmnProcService.class);
   protected final CoreBpmnDiagramService diagramService = ApplicationContextHelper.getBean(CoreBpmnDiagramService.class);
   protected final CoreBpmnInstanceService instanceService = ApplicationContextHelper.getBean(CoreBpmnInstanceService.class);
   protected final CoreBpmnInstanceTaskService instanceTaskService = ApplicationContextHelper.getBean(CoreBpmnInstanceTaskService.class);
   protected final CoreBpmnInstanceTaskUserService instanceTaskUserService = ApplicationContextHelper.getBean(CoreBpmnInstanceTaskUserService.class);
   protected final CoreBpmnInstanceTaskRoleService instanceTaskRoleService = ApplicationContextHelper.getBean(CoreBpmnInstanceTaskRoleService.class);
   protected final CoreBpmnCommentService commentService = ApplicationContextHelper.getBean(CoreBpmnCommentService.class);
   protected final CoreMenuExtService menuExtService = ApplicationContextHelper.getBean(CoreMenuExtService.class);
   protected final CoreBpmnTargetService bpmnTargetService = ApplicationContextHelper.getBean(CoreBpmnTargetService.class);
   protected final CoreBpmnInstanceTaskHisService taskHisService = ApplicationContextHelper.getBean(CoreBpmnInstanceTaskHisService.class);
   protected final CoreRoleUserService roleUserService = ApplicationContextHelper.getBean(CoreRoleUserService.class);
   protected final String bpmnMode = ApplicationContextHelper.getEnvironment().getProperty("sunway.task-executor.bpmn.mode", "default");

   public List<CoreBpmnInstanceStatusDTO<ID>> apply(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
      List var9;
      try {
         Collections.sort(sourceList, (s1, s2) -> ObjectUtils.toString(s1.getId()).compareTo(ObjectUtils.toString(s2.getId())));
         BpmnRuntimeCacheProvider.init(sourceList);
         this.doValidate(sourceList);
         this.validateBySql();
         List<CoreBpmnInstanceStatusDTO<ID>> statusList = this.doProcess(sourceList);
         this.processData();
         String tableName = sourceList.get(0).getTableName();
         this.updateProcessStatus(tableName, statusList);
         Map<String, List<ID>> callback = BpmnRuntimeCacheProvider.getBpmnRuntimeCallback();
         if (!CollectionUtils.isEmpty(callback)) {
            this.doCallback(callback);
         }

         Map<String, List<ID>> script = BpmnRuntimeCacheProvider.getBpmnRuntimeScript();
         if (!CollectionUtils.isEmpty(script)) {
            this.doInvokeScript(script);
         }

         Map<String, List<ID>> endCallback = BpmnRuntimeCacheProvider.getBpmnRuntimeEndCallback();
         if (!CollectionUtils.isEmpty(endCallback)) {
            this.doCallback(endCallback);
         }

         Map<String, List<ID>> endScript = BpmnRuntimeCacheProvider.getBpmnRuntimeEndScirpt();
         if (!CollectionUtils.isEmpty(endScript)) {
            this.doInvokeScript(endScript);
         }

         if (BpmnRuntimeCacheProvider.getProcessData().isAsync()) {
            ExecutorHelper.submitAfterCommitTransaction(() -> this.sendNotification(sourceList, statusList));
         } else {
            this.sendNotification(sourceList, statusList);
         }

         CoreBpmnUpdateStatusEvent event = new CoreBpmnUpdateStatusEvent(tableName, statusList);
         ApplicationContextHelper.getApplicationContext().publishEvent(event);
         var9 = statusList;
      } finally {
         BpmnRuntimeCacheProvider.remove();
      }

      return var9;
   }

   protected void doValidate(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
   }

   protected List<CoreBpmnInstanceStatusDTO<ID>> doProcess(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
      return Collections.emptyList();
   }

   protected void sendNotification(List<CoreBpmnRuntimeSource<T, ID>> sourceList, List<CoreBpmnInstanceStatusDTO<ID>> statusList) {
      if (!sourceList.isEmpty()) {
         ApplicationContext applicationContext = ApplicationContextHelper.getApplicationContext();
         List<AbstractCoreBpmnRuntimeProcessSupport.NotificationEventContainer> eventContainerList = new ArrayList<>();
         boolean defaultStrategy = applicationContext.getEnvironment().getProperty("sunway.bpmn.notification.default.strategy.enabled", Boolean.class, false);
         String name = I18nHelper.getMessage(sourceList.get(0).getTableName().toUpperCase() + ".SERVICE_NAME");
         List<CoreBpmnInstanceStatusDTO<ID>> approveOrRejectStatusList = new ArrayList<>();

         for (CoreBpmnInstanceStatusDTO<ID> instanceStatus : statusList) {
            if (ProcessStatus.DRAFT_REJECTED.equals(instanceStatus.getProcessStatus())) {
               CoreBpmnRuntimeSource<T, ID> source = sourceList.stream().filter(s -> s.getId().equals(instanceStatus.getId())).findAny().get();
               AbstractCoreBpmnRuntimeProcessSupport.UserTaskAttrContainer attrContainer = this.getUserTaskAttrContainer(null, instanceStatus);
               List<String> notifiedUserIdList = new ArrayList<>();
               if (defaultStrategy) {
                  notifiedUserIdList.add(instanceStatus.getRuntimeContainer().getInitiator());
               }

               CollectionUtils.merge(notifiedUserIdList, attrContainer.getRejectNotifiedUserIdList());
               List<Long> notifiedRoleIdList = attrContainer.getRejectNotifiedRoleIdList();
               AbstractCoreBpmnRuntimeProcessSupport.NotificationEventContainer eventContainer = new AbstractCoreBpmnRuntimeProcessSupport.NotificationEventContainer(
                  new CoreBpmnNotificationEvent(
                     source,
                     instanceStatus.getProcessStatus(),
                     name,
                     source.getDesc(),
                     attrContainer.getFromTaskName(),
                     BpmnDiagramHelper.getStartNodeName(attrContainer.getDocument()),
                     LocalContextHelper.getLoginUserId(),
                     source.getComment()
                  ),
                  notifiedUserIdList,
                  notifiedRoleIdList
               );
               eventContainerList.add(eventContainer);
            } else if (ProcessStatus.DONE.equals(instanceStatus.getProcessStatus())) {
               CoreBpmnRuntimeSource<T, ID> source = sourceList.stream().filter(s -> s.getId().equals(instanceStatus.getId())).findAny().get();
               AbstractCoreBpmnRuntimeProcessSupport.UserTaskAttrContainer attrContainer = this.getUserTaskAttrContainer(source, instanceStatus);
               List<String> notifiedUserIdList = new ArrayList<>();
               CollectionUtils.merge(notifiedUserIdList, attrContainer.getPassNotifiedUserIdList());
               List<Long> notifiedRoleIdList = attrContainer.getPassNotifiedRoleIdList();
               AbstractCoreBpmnRuntimeProcessSupport.NotificationEventContainer eventContainer = new AbstractCoreBpmnRuntimeProcessSupport.NotificationEventContainer(
                  new CoreBpmnNotificationEvent(
                     source,
                     instanceStatus.getProcessStatus(),
                     name,
                     source.getDesc(),
                     attrContainer.getFromTaskName(),
                     BpmnDiagramHelper.getEndNodeName(attrContainer.getDocument()),
                     LocalContextHelper.getLoginUserId(),
                     source.getComment()
                  ),
                  notifiedUserIdList,
                  notifiedRoleIdList
               );
               eventContainerList.add(eventContainer);
            } else if (ProcessStatus.DRAFT_WITHDRAWED.equals(instanceStatus.getProcessStatus())) {
               CoreBpmnRuntimeSource<T, ID> source = sourceList.stream().filter(s -> s.getId().equals(instanceStatus.getId())).findAny().get();
               AbstractCoreBpmnRuntimeProcessSupport.UserTaskAttrContainer attrContainer = this.getUserTaskAttrContainer(null, instanceStatus);
               List<String> notifiedUserIdList = new ArrayList<>();
               if (defaultStrategy) {
                  CollectionUtils.merge(notifiedUserIdList, instanceStatus.getRuntimeContainer().getCandidatorList());
               }

               CollectionUtils.merge(notifiedUserIdList, attrContainer.getRejectNotifiedUserIdList());
               List<Long> notifiedRoleIdList = attrContainer.getRejectNotifiedRoleIdList();
               AbstractCoreBpmnRuntimeProcessSupport.NotificationEventContainer eventContainer = new AbstractCoreBpmnRuntimeProcessSupport.NotificationEventContainer(
                  new CoreBpmnNotificationEvent(
                     source,
                     instanceStatus.getProcessStatus(),
                     name,
                     source.getDesc(),
                     attrContainer.getFromTaskName(),
                     BpmnDiagramHelper.getStartNodeName(attrContainer.getDocument()),
                     LocalContextHelper.getLoginUserId(),
                     source.getComment()
                  ),
                  notifiedUserIdList,
                  notifiedRoleIdList
               );
               eventContainerList.add(eventContainer);
            } else if ((
                  ProcessStatus.APPROVE_REJECTED.equals(instanceStatus.getProcessStatus()) || ProcessStatus.APPROVE.equals(instanceStatus.getProcessStatus())
               )
               && !ObjectUtils.equals(instanceStatus.getStatusCode(), instanceStatus.getNextStatusCode())) {
               approveOrRejectStatusList.add(instanceStatus);
            }
         }

         if (!approveOrRejectStatusList.isEmpty()) {
            List<Long> idList = approveOrRejectStatusList.stream().map(s -> s.getRuntimeContainer().getProcInstId()).collect(Collectors.toList());
            List<CoreBpmnInstanceTaskBean> taskList = this.instanceTaskService.getDao().selectListByOneColumnValues(idList, "INSTID");
            List<CoreBpmnInstanceTaskCandidatorDTO> instanceTaskCandidatorList = null;
            if (defaultStrategy) {
               instanceTaskCandidatorList = this.instanceService.selectInstanceTaskAuditorList(idList);
            }

            for (CoreBpmnInstanceStatusDTO<ID> instanceStatusx : approveOrRejectStatusList) {
               CoreBpmnRuntimeSource<T, ID> source = sourceList.stream().filter(s -> s.getId().equals(instanceStatusx.getId())).findAny().get();
               AbstractCoreBpmnRuntimeProcessSupport.UserTaskAttrContainer attrContainer = this.getUserTaskAttrContainer(source, instanceStatusx);
               List<CoreBpmnInstanceTaskBean> toTaskList = taskList.stream()
                  .filter(t -> t.getInstId().equals(instanceStatusx.getRuntimeContainer().getProcInstId()))
                  .collect(Collectors.toList());
               StringBuilder toTaskNameBuilder = new StringBuilder();
               List<String> toTaskCandidatorList = new ArrayList<>();

               for (CoreBpmnInstanceTaskBean toTask : toTaskList) {
                  if (toTaskNameBuilder.length() > 0) {
                     toTaskNameBuilder.append(",");
                  }

                  toTaskNameBuilder.append(toTask.getTaskName());
                  if (defaultStrategy) {
                     CollectionUtils.merge(
                        toTaskCandidatorList,
                        instanceTaskCandidatorList.stream()
                           .filter(c -> c.getInstTaskId().equals(toTask.getId()))
                           .map(c -> c.getCandidatorId())
                           .collect(Collectors.toList())
                     );
                  }
               }

               if (ProcessStatus.APPROVE_REJECTED.equals(instanceStatusx.getProcessStatus())) {
                  CollectionUtils.merge(toTaskCandidatorList, attrContainer.getRejectNotifiedUserIdList());
               } else {
                  CollectionUtils.merge(toTaskCandidatorList, attrContainer.getPassNotifiedUserIdList());
               }

               List<Long> notifiedRoleIdList = ProcessStatus.APPROVE_REJECTED.equals(instanceStatusx.getProcessStatus())
                  ? attrContainer.getRejectNotifiedRoleIdList()
                  : attrContainer.getPassNotifiedRoleIdList();
               AbstractCoreBpmnRuntimeProcessSupport.NotificationEventContainer eventContainer = new AbstractCoreBpmnRuntimeProcessSupport.NotificationEventContainer(
                  new CoreBpmnNotificationEvent(
                     source,
                     instanceStatusx.getProcessStatus(),
                     name,
                     source.getDesc(),
                     attrContainer.getFromTaskName(),
                     toTaskNameBuilder.toString(),
                     LocalContextHelper.getLoginUserId(),
                     source.getComment()
                  ),
                  toTaskCandidatorList,
                  notifiedRoleIdList
               );
               eventContainerList.add(eventContainer);
            }
         }

         List<Long> notifiedRoleIdList = new ArrayList<>();
         eventContainerList.stream()
            .map(AbstractCoreBpmnRuntimeProcessSupport.NotificationEventContainer::getNotifiedRoleIdList)
            .forEach(l -> CollectionUtils.merge(notifiedRoleIdList, l));
         Map<Long, Set<String>> roleUserMap = this.roleUserService
            .getDao()
            .selectListByOneColumnValues(notifiedRoleIdList, "ROLEID")
            .stream()
            .collect(Collectors.groupingBy(CoreRoleUserBean::getRoleId, Collectors.mapping(CoreRoleUserBean::getUserId, Collectors.toSet())));

         for (AbstractCoreBpmnRuntimeProcessSupport.NotificationEventContainer eventContainer : eventContainerList) {
            List<String> notifiedUserIdList = Optional.ofNullable(eventContainer.getNotifiedUserIdList()).orElseGet(() -> CollectionUtils.emptyList());
            if (eventContainer.getNotifiedRoleIdList() != null) {
               eventContainer.getNotifiedRoleIdList().forEach(r -> CollectionUtils.merge((Collection<String>)notifiedUserIdList, (Collection<String>)roleUserMap.get(r)));
            }

            CoreBpmnNotificationEvent event = eventContainer.getNotificationEvent();
            event.setNotifiedUserIdList(notifiedUserIdList);
            applicationContext.publishEvent(event);
         }
      }
   }

   protected void insertComment(
      String targetId,
      Element fromTaskElement,
      CommentStatus commentStatus,
      CoreBpmnRuntimeSource<T, ID> source,
      Long procId,
      String fromTaskName,
      String route
   ) {
      CoreBpmnCommentBean bpmnComment = new CoreBpmnCommentBean();
      bpmnComment.setId(ApplicationContextHelper.getNextIdentity());
      bpmnComment.setTargetId(targetId);
      bpmnComment.setProcId(procId);
      if (!StringUtils.isEmpty(fromTaskName)) {
         bpmnComment.setTaskName(fromTaskName);
      }

      if (fromTaskElement != null) {
         if (StringUtils.isBlank(bpmnComment.getTaskName())) {
            bpmnComment.setTaskName(XmlUtils.getAttributeValue(fromTaskElement, "name"));
         }

         bpmnComment.setTaskId(XmlUtils.getAttributeValue(fromTaskElement, "id"));
         bpmnComment.setStatusCode(XmlUtils.getAttributeValue(fromTaskElement, "statusCode"));
      }

      if (StringUtils.isEmpty(bpmnComment.getTaskName()) && (CommentStatus.SUBMIT.equals(commentStatus) || CommentStatus.UNDO.equals(commentStatus))) {
         CoreBpmnProcBean proc = this.procService.selectById(procId);
         Document document = this.diagramService.getDocument(proc.getDiagramId());
         if (CommentStatus.SUBMIT.equals(commentStatus)) {
            bpmnComment.setTaskName(BpmnDiagramHelper.getStartNodeName(document));
         } else if (CommentStatus.UNDO.equals(commentStatus)) {
            bpmnComment.setTaskName(BpmnDiagramHelper.getEndNodeName(document));
         }
      }

      bpmnComment.setAttachmentId(source.getAttachmentId());
      bpmnComment.setUserId(LocalContextHelper.getLoginUserId());
      bpmnComment.setUserName(LocalContextHelper.getLoginUserName());
      bpmnComment.setRoleId(LocalContextHelper.getLoginRoleId());
      bpmnComment.setStatus(commentStatus.name());
      bpmnComment.setComment(source.getComment());
      bpmnComment.setRoute(route);
      if (!source.getNextCandidateRoleList().isEmpty() || !source.getNextCandidatorList().isEmpty()) {
         BpmnProcessData data = BpmnRuntimeCacheProvider.getProcessData();
         CoreBpmnInstanceBean instance = BpmnRuntimeCacheProvider.getBpmnRuntimeInstance(source);
         if (!data.getInsertInstanceTaskRoleList().isEmpty()) {
            StringBuilder sb = new StringBuilder("(").append(I18nHelper.getMessage("GIKAM.BPMN.COMMENT.SELECTED_CANDIDATE_ROLE")).append(":");
            boolean start = true;
            data.getInsertInstanceTaskRoleList().removeIf(t -> data.getDeleteInstanceTaskRoleIdList().contains(t.getId()));
            List<CoreBpmnInstanceTaskRoleBean> taskRoleList = new ArrayList<>(data.getInsertInstanceTaskRoleList());
            taskRoleList.removeIf(t -> t.getInstId() != instance.getId());
            List<Long> nextCandidateRoleList = taskRoleList.stream().map(CoreBpmnInstanceTaskRoleBean::getRoleId).collect(Collectors.toList());
            CoreRoleService roleService = ApplicationContextHelper.getBean(CoreRoleService.class);

            for (CoreRoleBean role : roleService.selectListByIds(nextCandidateRoleList)) {
               if (start) {
                  start = false;
               } else {
                  sb.append(",");
               }

               sb.append(role.getRoleName());
            }

            sb.append(")");
            bpmnComment.setComment(bpmnComment.getComment() + sb.toString());
         }

         if (!data.getInsertInstanceTaskUserList().isEmpty()) {
            StringBuilder sb = new StringBuilder("(").append(I18nHelper.getMessage("GIKAM.BPMN.COMMENT.SELECTED_CANDIDATOR")).append(":");
            boolean start = true;
            data.getInsertInstanceTaskUserList().removeIf(t -> data.getDeleteInstanceTaskUserIdList().contains(t.getId()));
            List<CoreBpmnInstanceTaskUserBean> taskUserList = new ArrayList<>(data.getInsertInstanceTaskUserList());
            taskUserList.removeIf(t -> t.getInstId() != instance.getId());
            List<String> nextCandidatorList = taskUserList.stream().map(CoreBpmnInstanceTaskUserBean::getUserId).collect(Collectors.toList());
            CoreUserService userService = ApplicationContextHelper.getBean(CoreUserService.class);

            for (CoreUserBean user : userService.selectListByIds(nextCandidatorList)) {
               if (start) {
                  start = false;
               } else {
                  sb.append(",");
               }

               sb.append(user.getUserName());
            }

            sb.append(")");
            bpmnComment.setComment(bpmnComment.getComment() + sb.toString());
         }
      } else if (!StringUtils.isBlank(source.getTransferCandidator())) {
         CoreUserService userService = ApplicationContextHelper.getBean(CoreUserService.class);
         CoreUserBean transferCandidator = userService.selectById(source.getTransferCandidator());
         StringBuilder sb = new StringBuilder("(")
            .append(I18nHelper.getMessage("GIKAM.BPMN.COMMENT.TRANSFER", LocalContextHelper.getLoginUserName(), transferCandidator.getUserName()))
            .append(")");
         bpmnComment.setComment(bpmnComment.getComment() + sb.toString());
      }

      BpmnRuntimeCacheProvider.getProcessData().addInsertComment(bpmnComment);
   }

   protected void prepareValidateSql(int serialNumber, String sql, ID id) {
      this.getValidateSqlMap(serialNumber, sql, id).forEach((k, v) -> BpmnRuntimeCacheProvider.cacheBpmnRuntimeValidateSql(k, v));
   }

   protected Map<String, String> getValidateSqlMap(int serialNumber, String validatorSql, Object id) {
      if (StringUtils.isBlank(validatorSql)) {
         return CollectionUtils.emptyMap();
      } else {
         Map<String, String> sqlMap = new HashMap<>();
         String[] sqls = StringUtils.split(validatorSql, ";");

         for (String sql : sqls) {
            if (!StringUtils.isBlank(sql)) {
               String replacedSql = CoreBpmnHelper.getReplacedSql(sql, id, null);
               sqlMap.put(sql, "SELECT '" + serialNumber + "' AS " + "SN_" + ", " + StringUtils.removeStartIgnoreCase(StringUtils.trim(replacedSql), "select"));
            }
         }

         return sqlMap;
      }
   }

   protected void validateBySql() {
      Map<String, List<String>> cacheMap = BpmnRuntimeCacheProvider.getBpmnRuntimeValidateSql();
      if (cacheMap != null && !cacheMap.isEmpty()) {
         cacheMap.values().forEach(sql -> {
            List<Map<String, Object>> mapList = this.globalMapper.selectListBySqlList((List<String>)sql);
            Map<Integer, String> invalidContainer = new HashMap<>();

            for (Map<String, Object> map : mapList) {
               CollectionUtils.upperCaseKey(map);
               List<String> invalidColumnList = new ArrayList<>();
               map.forEach((k, v) -> {
                  if ("0".equals(v)) {
                     invalidColumnList.add(k);
                  }
               });
               if (!invalidColumnList.isEmpty()) {
                  int serialNumber = NumberUtils.parseInt(map.get("SN_"));
                  String type = (String)map.get("TYPE_");
                  String table = (String)map.get("TABLE_");
                  String desc = (String)map.get("DESC_");

                  for (String column : invalidColumnList) {
                     String message = invalidContainer.get(serialNumber);
                     if (StringUtils.isEmpty(message)) {
                        StringBuilder sb = new StringBuilder();
                        if (StringUtils.isEmpty(desc)) {
                           sb.append(I18nHelper.getMessage("GIKAM.VALIDATOR.INVALID_ROWNUMBER")).append(" ").append(serialNumber);
                        } else {
                           sb.append(I18nHelper.getMessage("GIKAM.VALIDATOR.CHECKED_DATA")).append(" ").append(desc);
                        }

                        sb.append(" :<br>").append(I18nHelper.getMessage(table + "." + column)).append(" ").append(I18nHelper.getMessage(type));
                        message = sb.toString();
                     } else {
                        message = message + "<br>" + I18nHelper.getMessage(table + "." + column) + " " + I18nHelper.getMessage(type);
                     }

                     invalidContainer.put(serialNumber, message);
                  }
               }
            }

            if (!invalidContainer.isEmpty()) {
               StringBuilder sb = new StringBuilder();
               invalidContainer.forEach((k, v) -> {
                  if (sb.length() > 0) {
                     sb.append(";");
                  }

                  sb.append(v);
               });
               throw new CheckedException(sb.toString());
            }
         });
      }
   }

   protected void prepareCallback(AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory callbackCateogry, Element element, ID id) {
      String callback = null;
      if (AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory.pass.equals(callbackCateogry)) {
         callback = BpmnDiagramHelper.getPassCallback(element);
      } else if (AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory.reject.equals(callbackCateogry)) {
         callback = BpmnDiagramHelper.getRejectCallback(element);
      } else if (AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory.end.equals(callbackCateogry)) {
         callback = BpmnDiagramHelper.getEndCallback(element);
      }

      if (!StringUtils.isBlank(callback)) {
         BpmnRuntimeCacheProvider.cacheBpmnRuntimeCallback(callback, id);
      }
   }

   protected void prepareEndCallback(AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory callbackCateogry, Element element, ID id) {
      String endCallback = null;
      if (AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory.pass.equals(callbackCateogry)) {
         endCallback = BpmnDiagramHelper.getPassCallback(element);
      } else if (AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory.reject.equals(callbackCateogry)) {
         endCallback = BpmnDiagramHelper.getRejectCallback(element);
      } else if (AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory.end.equals(callbackCateogry)) {
         endCallback = BpmnDiagramHelper.getEndCallback(element);
      }

      if (!StringUtils.isBlank(endCallback)) {
         BpmnRuntimeCacheProvider.cacheBpmnRuntimeEndCallback(endCallback, id);
      }
   }

   protected void prepareScript(AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory callbackCateogry, Element element, ID id) {
      String scpritName = null;
      if (AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory.pass.equals(callbackCateogry)) {
         scpritName = BpmnDiagramHelper.getPassScriptName(element);
      } else if (AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory.reject.equals(callbackCateogry)) {
         scpritName = BpmnDiagramHelper.getRejectScriptName(element);
      } else if (AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory.end.equals(callbackCateogry)) {
         scpritName = BpmnDiagramHelper.getEndScriptName(element);
      }

      if (!StringUtils.isBlank(scpritName)) {
         BpmnRuntimeCacheProvider.cacheBpmnRuntimeScirpt(scpritName.toLowerCase(), id);
      }
   }

   protected void prepareEndScript(AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory callbackCateogry, Element element, ID id) {
      String scpritName = null;
      if (AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory.pass.equals(callbackCateogry)) {
         scpritName = BpmnDiagramHelper.getPassScriptName(element);
      } else if (AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory.reject.equals(callbackCateogry)) {
         scpritName = BpmnDiagramHelper.getRejectScriptName(element);
      } else if (AbstractCoreBpmnRuntimeProcessSupport.CallbackCategory.end.equals(callbackCateogry)) {
         scpritName = BpmnDiagramHelper.getEndScriptName(element);
      }

      if (!StringUtils.isBlank(scpritName)) {
         BpmnRuntimeCacheProvider.cacheBpmnRuntimeEndScirpt(scpritName.toLowerCase(), id);
      }
   }

   protected void insertNextInstanceTask(
      Document diagram, CoreBpmnInstanceBean instance, List<Element> nextTaskElementList, CoreBpmnRuntimeSource<T, ID> source
   ) {
      CoreBpmnInstanceTaskHisBean taskHis = BpmnRuntimeCacheProvider.getBpmnRuntimeTaskHisMap() != null
         ? (CoreBpmnInstanceTaskHisBean)BpmnRuntimeCacheProvider.getBpmnRuntimeTaskHisMap().get(source.getId())
         : null;

      for (Element nextTaskElement : nextTaskElementList) {
         Long taskId = ApplicationContextHelper.getNextIdentity();
         if (taskHis == null) {
            List<String> userList = new ArrayList<>();
            List<Long> roleList = new ArrayList<>();
            String selectBySql = BpmnDiagramHelper.getUserTaskAssigneeSelectBySql(nextTaskElement);
            if (!StringUtils.isBlank(selectBySql)) {
               List<String> selectedUserList = CoreBpmnHelper.getCandidatorList(selectBySql, source.getId(), source.getVars());
               if (selectedUserList.isEmpty()) {
                  boolean nextCandidatorSelected = !source.getNextCandidatorList().isEmpty() || !source.getNextCandidateRoleList().isEmpty();
                  String bpmnTaskuserStrategy = source.getBpmnTaskuserStrategy();
                  if (nextCandidatorSelected && StringUtils.isBlank(bpmnTaskuserStrategy)) {
                     userList.addAll(source.getNextCandidatorList());
                     roleList.addAll(source.getNextCandidateRoleList());
                  }
               } else {
                  userList.addAll(selectedUserList);
               }
            } else {
               if (BpmnDiagramHelper.isUserTaskAssigneeInitiator(nextTaskElement) && !StringUtils.isEmpty(instance.getInitiator())) {
                  userList.add(instance.getInitiator());
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

               if (BpmnDiagramHelper.isFilterPreviousCandidatorsTask(nextTaskElement)) {
                  List<Element> userTaskElementList = BpmnDiagramHelper.getIncomingUserTaskElementList(diagram, nextTaskElement);
                  if (!userTaskElementList.isEmpty()) {
                     for (Element userTaskElement : userTaskElementList) {
                        List<String> auditedByList = this.commentService
                           .selectUserTaskAuditedByList(source.getTargetId(), BpmnDiagramHelper.getUserTaskStatusCode(userTaskElement));
                        if (auditedByList.size() > 0) {
                           userList.removeIf(u -> auditedByList.contains(u));
                        }
                     }
                  }

                  userList.remove(LocalContextHelper.getLoginUserId());
               }

               if (userList.isEmpty() && roleList.isEmpty()) {
                  throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.CANDIDATOR_AND_ROLE_NOT_EXISTS");
               }

               if (!roleList.isEmpty() && CandidatorFilterStrategy.sameDept.equals(BpmnDiagramHelper.getCandidatorFilterStrategy(nextTaskElement))) {
                  List<CoreRoleUserBean> roleUserList = this.roleUserService
                     .selectListByFilter(SearchFilter.instance().match("roleId", roleList).filter(MatchPattern.OR), new Order[0]);
                  if (!roleUserList.isEmpty()) {
                     List<String> userIdList = roleUserList.stream().map(CoreRoleUserBean::getUserId).distinct().collect(Collectors.toList());
                     if (!userIdList.isEmpty()) {
                        userList.addAll(userIdList);
                     }
                  }
               }

               if (userList.isEmpty() && roleList.isEmpty()) {
                  throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.CANDIDATOR_AND_ROLE_NOT_EXISTS");
               }

               String filterBySql = BpmnDiagramHelper.getUserTaskAssigneeFilterBySql(nextTaskElement);
               if (!StringUtils.isBlank(filterBySql)) {
                  List<String> filteredUserList = CoreBpmnHelper.getCandidatorList(filterBySql, source.getId(), userList, roleList, source.getVars());
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

            if (!userList.isEmpty() && CandidatorFilterStrategy.sameDept.equals(BpmnDiagramHelper.getCandidatorFilterStrategy(nextTaskElement))) {
               String createdByOrgId = (String)CollectionUtils.getValueIgnorecase(source.getVars(), "createdByOrgId");
               if (!StringUtils.isBlank(createdByOrgId)) {
                  CoreUserService userService = ApplicationContextHelper.getBean(CoreUserService.class);
                  List<CoreUserBean> userBeanList = userService.getDao()
                     .selectListByOneColumnValues(userList.stream().distinct().collect(Collectors.toList()), "ID", Arrays.asList("ID", "ORGID"));
                  userList.removeIf(
                     u -> userBeanList.stream()
                        .noneMatch(s -> s.getId().equals(u) && Objects.equals(s.getOrgId(), createdByOrgId) && !"deprecated".equals(s.getStatus()))
                  );
               } else {
                  logger.warn("No createdByOrgId column in (" + source.getTableName() + "), ignore candidator filter strategy.");
               }
            }

            if (!userList.isEmpty()) {
               List<CoreBpmnInstanceTaskUserBean> taskUserList = userList.stream().distinct().map(u -> {
                  CoreBpmnInstanceTaskUserBean taskUser = new CoreBpmnInstanceTaskUserBean();
                  taskUser.setId(ApplicationContextHelper.getNextIdentity());
                  taskUser.setInstId(instance.getId());
                  taskUser.setInstTaskId(taskId);
                  taskUser.setUserId(u);
                  return taskUser;
               }).collect(Collectors.toList());
               BpmnRuntimeCacheProvider.getProcessData().addInsertInstanceTaskUser(taskUserList);
            }

            if (!roleList.isEmpty() && !CandidatorFilterStrategy.sameDept.equals(BpmnDiagramHelper.getCandidatorFilterStrategy(nextTaskElement))) {
               List<CoreBpmnInstanceTaskRoleBean> taskRoleList = roleList.stream().distinct().map(r -> {
                  CoreBpmnInstanceTaskRoleBean taskRolex = new CoreBpmnInstanceTaskRoleBean();
                  taskRolex.setId(ApplicationContextHelper.getNextIdentity());
                  taskRolex.setInstId(instance.getId());
                  taskRolex.setInstTaskId(taskId);
                  taskRolex.setRoleId(r);
                  return taskRolex;
               }).collect(Collectors.toList());
               BpmnRuntimeCacheProvider.getProcessData().addInsertInstanceTaskRole(taskRoleList);
            }
         } else {
            CoreBpmnInstanceTaskHisCandidatorDTO taskHisCandidator = JSON.parseObject(taskHis.getCandidatorJson(), CoreBpmnInstanceTaskHisCandidatorDTO.class);
            if (!CollectionUtils.isEmpty(taskHisCandidator.getUserIdList())) {
               List<CoreBpmnInstanceTaskUserBean> taskUserList = new ArrayList<>();

               for (String userId : taskHisCandidator.getUserIdList()) {
                  CoreBpmnInstanceTaskUserBean taskUser = new CoreBpmnInstanceTaskUserBean();
                  taskUser.setId(ApplicationContextHelper.getNextIdentity());
                  taskUser.setInstId(instance.getId());
                  taskUser.setInstTaskId(taskId);
                  taskUser.setUserId(userId);
                  taskUserList.add(taskUser);
               }

               BpmnRuntimeCacheProvider.getProcessData().addInsertInstanceTaskUser(taskUserList);
            }

            if (!CollectionUtils.isEmpty(taskHisCandidator.getRoleIdList())) {
               List<CoreBpmnInstanceTaskRoleBean> taskRoleList = new ArrayList<>();

               for (Long roleId : taskHisCandidator.getRoleIdList()) {
                  CoreBpmnInstanceTaskRoleBean taskRole = new CoreBpmnInstanceTaskRoleBean();
                  taskRole.setId(ApplicationContextHelper.getNextIdentity());
                  taskRole.setInstId(instance.getId());
                  taskRole.setInstTaskId(taskId);
                  taskRole.setRoleId(roleId);
                  taskRoleList.add(taskRole);
               }

               BpmnRuntimeCacheProvider.getProcessData().addInsertInstanceTaskRole(taskRoleList);
            }
         }

         CoreBpmnInstanceTaskBean task = new CoreBpmnInstanceTaskBean();
         task.setId(taskId);
         task.setInstId(instance.getId());
         task.setTaskId(XmlUtils.getAttributeValue(nextTaskElement, "id"));
         task.setTaskName(XmlUtils.getAttributeValue(nextTaskElement, "name"));
         task.setStatusCode(XmlUtils.getAttributeValue(nextTaskElement, "statusCode"));
         if (CounterSignStrategy.none.equals(BpmnDiagramHelper.getCounterSignStrategy(nextTaskElement))) {
            task.setCounterSign("0");
         } else {
            task.setCounterSign("1");
         }

         task.setStatus(TaskStatus.CREATED.name());
         BpmnRuntimeCacheProvider.getProcessData().addInsertInstanceTask(task);
      }
   }

   protected CoreBpmnTargetBean getBpmnTarget(CoreBpmnRuntimeSource<T, ID> source, ProcessStatus status) {
      CoreBpmnTargetBean target = new CoreBpmnTargetBean();
      target.setId(source.getTargetId());
      target.setTargetId(ObjectUtils.toString(source.getId()));
      target.setTargetTable(source.getTableName());
      target.setTargetDesc(source.getDesc());
      target.setProcessStatus(status.name());
      return target;
   }

   protected String getRoute(String sourceName, String targetName) {
      return StringUtils.trim(sourceName) + " -> " + Optional.ofNullable(targetName).orElseGet(() -> I18nHelper.getMessage("GIKAM.BPMN.TASK.GATEWAY"));
   }

   protected String getElementName(Document diagram, List<String> statusCodeList) {
      if (CollectionUtils.isEmpty(statusCodeList)) {
         return null;
      } else {
         StringBuilder sb = new StringBuilder();

         for (String statusCode : statusCodeList) {
            if (sb.length() > 0) {
               sb.append(",");
            }

            if (statusCode != null && !"".equals(statusCode)) {
               sb.append(BpmnDiagramHelper.getUserTaskName(BpmnDiagramHelper.getProcessElementByStatusCode(diagram, statusCode)));
            }
         }

         return sb.toString();
      }
   }

   protected void updateProcessStatus(String table, List<CoreBpmnInstanceStatusDTO<ID>> statusList) {
      BpmnProcessData data = BpmnRuntimeCacheProvider.getProcessData();
      List<Map<String, Object>> itemList = new ArrayList<>();

      for (CoreBpmnInstanceStatusDTO<ID> status : statusList) {
         Map<String, Object> item = new HashMap<>();
         item.put("ID", status.getId());
         if (data.isAsync()) {
            item.put("PROCESSSTATUS", "waiting$" + status.getProcessStatus().name().toLowerCase());
         } else {
            item.put("PROCESSSTATUS", status.getProcessStatus().name().toLowerCase());
         }

         itemList.add(item);
      }

      if (data.isAsync()) {
         List<Map<String, Object>> asyncItemList = new ArrayList<>();

         for (CoreBpmnInstanceStatusDTO<ID> status : statusList) {
            Map<String, Object> item = new HashMap<>();
            item.put("ID", status.getId());
            item.put("PROCESSSTATUS", status.getProcessStatus().name().toLowerCase());
            asyncItemList.add(item);
         }

         data.setUpdateProcessStatusRunnable(() -> ApplicationContextHelper.getBean(MapDao.class).update(table, asyncItemList, "PROCESSSTATUS"));
      }

      ApplicationContextHelper.getBean(MapDao.class).update(table, itemList, "PROCESSSTATUS");
   }

   protected void doCallback(Map<String, List<ID>> callbackMap) {
      callbackMap.forEach((k, v) -> {
         String key = k.trim();
         int lastIndexOf = key.lastIndexOf(".");
         String className = key.substring(0, lastIndexOf);
         String methodName = key.substring(lastIndexOf + 1);
         Class<?> clazz;
         if (className.startsWith("com.sunwayworld")) {
            clazz = ClassUtils.getClass(className);
         } else {
            int lastIndexOfClass = className.lastIndexOf(".");
            String lcdpClassName = className.substring(lastIndexOfClass + 1);
            clazz = DynamicClassLoader.getClass(lcdpClassName);
            if (ObjectUtils.isEmpty(clazz)) {
               throw new UnexpectedException(new ClassNotFoundException(className));
            }
         }

         Object bean = ApplicationContextHelper.getBean(clazz);
         Method method = ReflectionUtils.findMethod(clazz, methodName, List.class);
         if (ObjectUtils.isEmpty(method)) {
            throw new UnexpectedException(new NoSuchMethodException(key));
         } else {
            ReflectionUtils.invokeMethod(method, bean, v);
         }
      });
   }

   protected void doInvokeScript(Map<String, List<ID>> stringMap) {
      stringMap.forEach((beanName, v) -> SpringUtils.invoke(beanName, "execute", v));
   }

   protected void processData() {
      BpmnRuntimeData<T, ID> runtimeData = BpmnRuntimeCacheProvider.getRuntimeData();
      List<CoreBpmnRuntimeSource<T, ID>> sourceList = runtimeData.getSourceList();
      BpmnProcessData data = BpmnRuntimeCacheProvider.getProcessData();
      boolean autoPass = false;

      for (CoreBpmnRuntimeSource<T, ID> source : sourceList) {
         Document document = BpmnRuntimeCacheProvider.getBpmnRuntimeDocument(source);
         if (BpmnDiagramHelper.isAutoPass(document)) {
            CoreBpmnInstanceBean instance = data.getInsertInstanceList()
               .stream()
               .filter(i -> i.getTargetId().equals(CoreBpmnHelper.getTargetId(source.getTableName(), source.getId())))
               .findFirst()
               .orElse(null);
            if (instance == null) {
               instance = BpmnRuntimeCacheProvider.getBpmnRuntimeInstance(source);
            }

            if (instance != null) {
               Long instanceId = instance.getId();

               for (CoreBpmnInstanceTaskBean task : data.getInsertInstanceTaskList()
                  .stream()
                  .filter(t -> t.getInstId().equals(instanceId))
                  .collect(Collectors.toList())) {
                  if (TaskStatus.CREATED.name().equals(task.getStatus())) {
                     List<CoreBpmnInstanceTaskUserBean> userList = data.getInsertInstanceTaskUserList()
                        .stream()
                        .filter(u -> u.getInstTaskId().equals(task.getId()))
                        .collect(Collectors.toList());
                     if (userList.stream().anyMatch(u -> u.getUserId().equals(LocalContextHelper.getLoginUserId()))) {
                        source.setAutoPass(true);
                        autoPass = true;
                     } else {
                        List<CoreBpmnInstanceTaskRoleBean> roleList = data.getInsertInstanceTaskRoleList()
                           .stream()
                           .filter(u -> u.getInstTaskId().equals(task.getId()))
                           .collect(Collectors.toList());
                        if (!roleList.isEmpty()) {
                           List<CoreRoleUserBean> roleUserList = this.roleUserService.selectByUserId(LocalContextHelper.getLoginUserId());
                           if (roleList.stream().anyMatch(r -> roleUserList.stream().anyMatch(u -> r.getRoleId().equals(u.getRoleId())))) {
                              source.setAutoPass(true);
                              autoPass = true;
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
      int limit = ApplicationContextHelper.getEnvironment().getProperty("sunway.bpmn.process-data-limit", Integer.class, 50);
      if (autoPass
         || limit <= 0
         || data.getInsertCommentList().size() <= limit
            && data.getInsertInstanceList().size() <= limit
            && data.getInsertInstanceTaskList().size() <= limit
            && data.getDeleteInstanceList().size() <= limit
            && data.getUpdateInstanceTaskStatusList().size() <= limit) {
         runtimeService.processData(data);
      } else {
         data.setAsync(true);
         LoginUser loginUser = ObjectUtils.clone(LocalContextHelper.getLoginPrincipal());
         ExecutorHelper.submitAfterCommitTransaction(() -> {
            try {
               if (loginUser != null) {
                  LocalContextHelper.setUserLogin(loginUser);
               }

               runtimeService.processData(data);
            } finally {
               if (loginUser != null) {
                  LocalContextHelper.removeUserLogin();
               }
            }
         });
      }
   }

   private AbstractCoreBpmnRuntimeProcessSupport.UserTaskAttrContainer getUserTaskAttrContainer(
      CoreBpmnRuntimeSource<T, ID> source, CoreBpmnInstanceStatusDTO<ID> instanceStatus
   ) {
      AbstractCoreBpmnRuntimeProcessSupport.UserTaskAttrContainer container = new AbstractCoreBpmnRuntimeProcessSupport.UserTaskAttrContainer();
      CoreBpmnProcBean proc = this.procService.selectById(instanceStatus.getRuntimeContainer().getProcId());
      StringBuilder sb = new StringBuilder();
      List<String> passNotifiedUserIdList = new ArrayList<>();
      List<Long> passNotifiedRoleIdList = new ArrayList<>();
      List<String> rejectNotifiedUserIdList = new ArrayList<>();
      List<Long> rejectNotifiedRoleIdList = new ArrayList<>();
      Document document = this.diagramService.getDocument(proc.getDiagramId());
      container.setDocument(document);

      for (String statusCode : StringUtils.split(instanceStatus.getStatusCode(), ",")) {
         if (sb.length() > 0) {
            sb.append(",");
         }

         Element userTask = BpmnDiagramHelper.getProcessElementByStatusCode(document, statusCode);
         sb.append(BpmnDiagramHelper.getUserTaskName(userTask));
         CollectionUtils.merge(passNotifiedUserIdList, BpmnDiagramHelper.getUserTaskPassNotifiedUserMap(userTask).keySet());
         CollectionUtils.merge(passNotifiedRoleIdList, BpmnDiagramHelper.getUserTaskPassNotifiedRoleMap(userTask).keySet());
         CollectionUtils.merge(rejectNotifiedUserIdList, BpmnDiagramHelper.getUserTaskRejectNotifiedUserMap(userTask).keySet());
         CollectionUtils.merge(rejectNotifiedRoleIdList, BpmnDiagramHelper.getUserTaskRejectNotifiedRoleMap(userTask).keySet());
         if (source != null
            && (
               instanceStatus.getProcessStatus().equals(ProcessStatus.APPROVE)
                  || instanceStatus.getProcessStatus().equals(ProcessStatus.APPROVE_OR_DONE)
                  || instanceStatus.getProcessStatus().equals(ProcessStatus.DONE)
            )) {
            List<String> userIdList = new ArrayList<>();
            List<Long> roleIdList = new ArrayList<>();
            CoreBpmnInstanceBean instance = BpmnRuntimeCacheProvider.getBpmnRuntimeInstance(source);
            if (BpmnDiagramHelper.isUserTaskPassNotifiedInitiator(userTask)) {
               if (!StringUtils.isEmpty(instance.getInitiator())) {
                  userIdList.add(instance.getInitiator());
               }
            } else {
               String dynamic = BpmnDiagramHelper.getUserTaskPassNotifiedDynamic(userTask);
               if (!StringUtils.isEmpty(dynamic)) {
                  String[] dynamicValues = StringUtils.split(dynamic, ",");

                  for (String value : dynamicValues) {
                     if (StringUtils.startsWith(value, "u:")) {
                        String user = (String)source.getVars().get(StringUtils.removeStart(value, "u:"));
                        if (user == null) {
                           throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.NOTIFIED.DYNAMIC_USER_NOT_EXISTS");
                        }

                        userIdList.add(user);
                     } else if (StringUtils.startsWith(value, "r:")) {
                        String role = ObjectUtils.toString(source.getVars().get(StringUtils.removeStart(value, "r:")));
                        if (StringUtils.isEmpty(role)) {
                           throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.NOTIFIED.DYNAMIC_ROLE_NOT_EXISTS");
                        }

                        roleIdList.add(NumberUtils.parseLong(role));
                     }
                  }
               } else {
                  String selectBySql = BpmnDiagramHelper.getUserTaskPassNotifiedSelectBySql(userTask);
                  if (!StringUtils.isBlank(selectBySql)) {
                     List<String> selectedUserList = CoreBpmnHelper.getCandidatorList(selectBySql, source.getId(), source.getVars());
                     if (selectedUserList.isEmpty()) {
                        throw new BpmnException(
                           "CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.NOTIFIED.NOUSER_BY_SELECT",
                           CoreBpmnHelper.getReplacedSql(selectBySql, source.getId(), source.getVars())
                        );
                     }

                     userIdList.addAll(selectedUserList);
                  } else {
                     String filterBySql = BpmnDiagramHelper.getUserTaskPassNotifiedFilterBySql(userTask);
                     if (!StringUtils.isBlank(filterBySql)) {
                        List<CoreBpmnInstanceCandidatorDTO> candidatorList = this.instanceService.selectInstanceCandidatorList(Arrays.asList(instance.getId()));
                        if (!candidatorList.isEmpty()) {
                           List<String> candidatorIdList = candidatorList.stream()
                              .map(CoreBpmnInstanceCandidatorDTO::getCandidatorId)
                              .collect(Collectors.toList());
                           List<String> filteredUserList = CoreBpmnHelper.getCandidatorList(
                              filterBySql, source.getId(), candidatorIdList, CollectionUtils.emptyList(), source.getVars()
                           );
                           if (filteredUserList.isEmpty()) {
                              throw new BpmnException(
                                 "CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.NOTIFIED.NOUSER_BY_FILTER",
                                 CoreBpmnHelper.getReplacedFilterBySql(
                                    filterBySql, source.getId(), candidatorIdList, CollectionUtils.emptyList(), source.getVars()
                                 )
                              );
                           }

                           userIdList.addAll(filteredUserList);
                        }
                     }
                  }
               }
            }

            CollectionUtils.merge(passNotifiedUserIdList, userIdList);
            CollectionUtils.merge(passNotifiedRoleIdList, roleIdList);
         }
      }

      container.setFromTaskName(sb.toString());
      container.setPassNotifiedUserIdList(passNotifiedUserIdList);
      container.setPassNotifiedRoleIdList(passNotifiedRoleIdList);
      container.setRejectNotifiedUserIdList(rejectNotifiedUserIdList);
      container.setRejectNotifiedRoleIdList(rejectNotifiedRoleIdList);
      return container;
   }

   protected static enum CallbackCategory {
      pass,
      reject,
      end;
   }

   private static class NotificationEventContainer {
      private final CoreBpmnNotificationEvent notificationEvent;
      private final List<String> notifiedUserIdList;
      private final List<Long> notifiedRoleIdList;

      public NotificationEventContainer(CoreBpmnNotificationEvent notificationEvent, List<String> notifiedUserIdList, List<Long> notifiedRoleIdList) {
         this.notificationEvent = notificationEvent;
         this.notifiedUserIdList = notifiedUserIdList;
         this.notifiedRoleIdList = notifiedRoleIdList;
      }

      public CoreBpmnNotificationEvent getNotificationEvent() {
         return this.notificationEvent;
      }

      public List<String> getNotifiedUserIdList() {
         return this.notifiedUserIdList;
      }

      public List<Long> getNotifiedRoleIdList() {
         return this.notifiedRoleIdList;
      }
   }

   private static class UserTaskAttrContainer {
      private Document document;
      private String fromTaskName;
      private List<String> passNotifiedUserIdList;
      private List<Long> passNotifiedRoleIdList;
      private List<String> rejectNotifiedUserIdList;
      private List<Long> rejectNotifiedRoleIdList;

      private UserTaskAttrContainer() {
      }

      public Document getDocument() {
         return this.document;
      }

      public void setDocument(Document document) {
         this.document = document;
      }

      public String getFromTaskName() {
         return this.fromTaskName;
      }

      public void setFromTaskName(String fromTaskName) {
         this.fromTaskName = fromTaskName;
      }

      public List<String> getPassNotifiedUserIdList() {
         return this.passNotifiedUserIdList;
      }

      public void setPassNotifiedUserIdList(List<String> passNotifiedUserIdList) {
         this.passNotifiedUserIdList = passNotifiedUserIdList;
      }

      public List<Long> getPassNotifiedRoleIdList() {
         return this.passNotifiedRoleIdList;
      }

      public void setPassNotifiedRoleIdList(List<Long> passNotifiedRoleIdList) {
         this.passNotifiedRoleIdList = passNotifiedRoleIdList;
      }

      public List<String> getRejectNotifiedUserIdList() {
         return this.rejectNotifiedUserIdList;
      }

      public void setRejectNotifiedUserIdList(List<String> rejectNotifiedUserIdList) {
         this.rejectNotifiedUserIdList = rejectNotifiedUserIdList;
      }

      public List<Long> getRejectNotifiedRoleIdList() {
         return this.rejectNotifiedRoleIdList;
      }

      public void setRejectNotifiedRoleIdList(List<Long> rejectNotifiedRoleIdList) {
         this.rejectNotifiedRoleIdList = rejectNotifiedRoleIdList;
      }
   }
}
