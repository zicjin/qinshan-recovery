package com.sunwayworld.module.sys.audit.service.impl;

import com.sunwayworld.framework.audit.AuditLogHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.at.filter.SpecialRecordLineHelper;
import com.sunwayworld.module.sys.audit.bean.CoreAuditConfigBean;
import com.sunwayworld.module.sys.audit.bean.CoreAuditConfigColumnBean;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogActivityBean;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogBean;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnBean;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnHeaderHorizontalDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnNameDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnValueHorizontalDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnVerticalDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogHorizontalRepresentation;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogInsertDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogItemActivityHorizontalDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogItemActivityVerticalDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogItemActivityVerticalRepresentation;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogItemDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogItemHorizontalDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogItemHorizontalRepresentation;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogItemVerticalDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogItemVerticalRepresentation;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogTraceOverviewDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogVerticalRepresentation;
import com.sunwayworld.module.sys.audit.dao.CoreAuditLogDao;
import com.sunwayworld.module.sys.audit.filter.SpecialAuditLogColumnFilter;
import com.sunwayworld.module.sys.audit.filter.SpecialAuditLogColumnFilterContext;
import com.sunwayworld.module.sys.audit.service.CoreAuditConfigColumnService;
import com.sunwayworld.module.sys.audit.service.CoreAuditConfigService;
import com.sunwayworld.module.sys.audit.service.CoreAuditLogActivityService;
import com.sunwayworld.module.sys.audit.service.CoreAuditLogColumnService;
import com.sunwayworld.module.sys.audit.service.CoreAuditLogService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreAuditLogServiceImpl implements CoreAuditLogService {
   @Autowired
   private CoreUserService userService;
   @Autowired
   private CoreAuditLogDao logDao;
   @Autowired
   private CoreAuditConfigService configService;
   @Autowired
   private CoreAuditConfigColumnService configColumnService;
   @Autowired
   private CoreAuditLogActivityService logActivityService;
   @Autowired
   private CoreAuditLogColumnService logColumnService;
   @Autowired
   private GlobalMapper globalMapper;
   @Lazy
   @Autowired
   private CoreAuditLogService proxyInstance;
   @Autowired(
      required = false
   )
   private List<SpecialAuditLogColumnFilter> specialAuditLogColumnFilters = Collections.emptyList();

   public CoreAuditLogDao getDao() {
      return this.logDao;
   }

   @Transactional
   @Override
   public void insertAuditLogActivity(List<CoreAuditLogInsertDTO> auditLogInsertList) {
      List<CoreAuditLogActivityBean> insertActivityList = new ArrayList<>();
      List<CoreAuditLogColumnBean> insertColumnList = new ArrayList<>();

      for (CoreAuditLogInsertDTO auditLog : auditLogInsertList) {
         CoreAuditConfigBean config = AuditLogHelper.getAuditConfig(auditLog.getTable());
         if (config != null && !"0".equals(config.getAuditable())) {
            String id = ConvertUtils.convert(Optional.ofNullable(auditLog.getOldValueMap().get("id")).orElse(auditLog.getNewValueMap().get("id")), String.class);
            GikamConcurrentLocker.block("AUDIT_LOG$" + auditLog.getTable() + "$" + id);
            Long logId = this.proxyInstance.insertAuditLogCascadeInNewTransaction(config, id);
            CoreAuditLogActivityBean logActivity = new CoreAuditLogActivityBean();
            logActivity.setId(ApplicationContextHelper.getNextIdentity());
            logActivity.setLogId(logId);
            logActivity.setActivity(auditLog.getActivity());
            logActivity.setComment(auditLog.getComment());
            logActivity.setSignature(auditLog.getSignature());
            logActivity.setUserId(auditLog.getUserId());
            logActivity.setWitnessId(auditLog.getWitnessId());
            logActivity.setWitnessName(auditLog.getWitnessName());
            logActivity.setCreatedTime(auditLog.getCreatedTime());
            List<CoreAuditConfigColumnBean> configColumnList = this.configColumnService.selectAuditableListByConfigId(config.getId());
            boolean insertLog = false;
            List<CoreAuditLogColumnBean> logColumnList = new ArrayList<>();
            if (!configColumnList.isEmpty()) {
               for (CoreAuditConfigColumnBean configColumn : configColumnList) {
                  CoreAuditLogColumnBean logColumn = new CoreAuditLogColumnBean();
                  logColumn.setId(ApplicationContextHelper.getNextIdentity());
                  logColumn.setActivityId(logActivity.getId());
                  logColumn.setColumn(configColumn.getColumn());
                  if (StringUtils.isBlank(configColumn.getSelectValueSql())) {
                     String oldValue = ConvertUtils.convert(auditLog.getOldValueMap().get(logColumn.getColumn()), String.class);
                     String newValue = ConvertUtils.convert(auditLog.getNewValueMap().get(logColumn.getColumn()), String.class);
                     if ("AUDIT.SAVE".equals(auditLog.getActivity()) && StringUtils.equals(oldValue, newValue)) {
                        continue;
                     }

                     logColumn.setOldValue(oldValue);
                     logColumn.setNewValue(newValue);
                  } else {
                     String selectValueSql = configColumn.getSelectValueSql();
                     Object oldValue = auditLog.getOldValueMap().get(logColumn.getColumn());
                     if (oldValue != null && !StringUtils.isBlank(oldValue.toString())) {
                        logColumn.setOldValue(this.globalMapper.selectOneString(StringUtils.replace(selectValueSql, "#id#", oldValue)));
                     }

                     Object newValue = auditLog.getNewValueMap().get(logColumn.getColumn());
                     if (newValue != null && !StringUtils.isBlank(newValue.toString())) {
                        logColumn.setNewValue(this.globalMapper.selectOneString(StringUtils.replace(selectValueSql, "#id#", newValue)));
                     }
                  }

                  logColumnList.add(logColumn);
                  if (!StringUtils.equals(logColumn.getOldValue(), logColumn.getNewValue())) {
                     insertLog = true;
                  }
               }

               if (insertLog) {
                  insertActivityList.add(logActivity);
                  insertColumnList.addAll(logColumnList);
               }
            }
         }
      }

      this.logActivityService.getDao().insert(insertActivityList);
      this.logColumnService.getDao().insert(insertColumnList);
   }

   @Transactional(
      propagation = Propagation.REQUIRES_NEW
   )
   @Override
   public Long insertAuditLogCascadeInNewTransaction(CoreAuditConfigBean config, String id) {
      return this.insertAuditLogCascade(config, id);
   }

   @Override
   public CoreAuditLogDTO selectAuditLogRoot(String table, String id) {
      CoreAuditConfigBean config = this.configService.selectByIdIfPresent(table);
      if (config == null) {
         return null;
      } else {
         CoreAuditLogBean filter = new CoreAuditLogBean();
         filter.setTargetId(id);
         filter.setTargetTable(table);
         CoreAuditLogBean auditLog = this.getDao().selectOneIfPresent(filter, new String[0]);
         CoreAuditLogDTO root = new CoreAuditLogDTO();
         if (auditLog != null) {
            root.setRequestUrl(
               StringUtils.replace(
                  StringUtils.replace("/secure/core/module/sys/audit/audit-logs/items?table={table}&auditLogId={auditLogId}", "{table}", table),
                  "{auditLogId}",
                  auditLog.getId()
               )
            );
         }

         root.setServiceName(config.getName());
         return root;
      }
   }

   @Override
   public List<CoreAuditLogItemDTO> selectAuditLogItemList(@Nullable String vertical, String table, @Nullable Long auditLogId, @Nullable Long parentAuditLogId) {
      List<CoreAuditLogItemDTO> logItemList = new ArrayList<>();
      CoreAuditConfigBean config = this.configService.selectByIdIfPresent(table);
      if (config == null) {
         return logItemList;
      } else {
         if (auditLogId != null) {
            CoreAuditLogBean auditLog = this.getDao().selectById(auditLogId);
            CoreAuditLogItemDTO logItem = new CoreAuditLogItemDTO();
            if (!StringUtils.isBlank(config.getSelectDescSql())) {
               logItem.setItemDesc(this.globalMapper.selectOneString(StringUtils.replace(config.getSelectDescSql(), "#id#", auditLog.getTargetId())));
            }

            if (StringUtils.isBlank(logItem.getItemDesc())) {
               logItem.setItemDesc(auditLog.getTargetId());
            }

            if ("1".equals(vertical)) {
               logItem.setRequestUrl(
                  StringUtils.replace("/secure/core/module/sys/audit/audit-logs/{auditLogId}/vertical-activities", "{auditLogId}", auditLog.getId())
               );
            } else {
               logItem.setRequestUrl(
                  StringUtils.replace("/secure/core/module/sys/audit/audit-logs/{auditLogId}/horizontal-activities", "{auditLogId}", auditLog.getId())
               );
            }

            logItemList.add(logItem);
         } else if (parentAuditLogId != null) {
            CoreAuditLogBean parentAuditLog = this.getDao().selectByIdIfPresent(parentAuditLogId);
            if (parentAuditLog == null) {
               return logItemList;
            }

            CoreAuditLogBean filter = new CoreAuditLogBean();
            filter.setParentId(parentAuditLog.getTargetId());
            filter.setParentTable(parentAuditLog.getTargetTable());
            filter.setTargetTable(table);

            for (CoreAuditLogBean auditLogx : this.getDao().selectList(filter, new Order[]{Order.asc("ID")})) {
               CoreAuditLogItemDTO logItemx = new CoreAuditLogItemDTO();
               logItemx.setItemDesc(auditLogx.getItemDesc());
               if (StringUtils.isEmpty(logItemx.getItemDesc())) {
                  if (!StringUtils.isBlank(config.getSelectDescSql())) {
                     logItemx.setItemDesc(this.globalMapper.selectOneString(StringUtils.replace(config.getSelectDescSql(), "#id#", auditLogx.getTargetId())));
                  }

                  if (StringUtils.isBlank(logItemx.getItemDesc())) {
                     logItemx.setItemDesc(auditLogx.getTargetId());
                  }
               }

               if ("1".equals(vertical)) {
                  logItemx.setRequestUrl(
                     StringUtils.replace("/secure/core/module/sys/audit/audit-logs/{auditLogId}/vertical-activities", "{auditLogId}", auditLogx.getId())
                  );
               } else {
                  logItemx.setRequestUrl(
                     StringUtils.replace("/secure/core/module/sys/audit/audit-logs/{auditLogId}/horizontal-activities", "{auditLogId}", auditLogx.getId())
                  );
               }

               logItemList.add(logItemx);
            }
         }

         return logItemList;
      }
   }

   @Override
   public CoreAuditLogItemVerticalDTO selectAuditLogItemActivityVertical(Long auditLogId) {
      CoreAuditLogBean auditLog = this.getDao().selectById(auditLogId);
      CoreAuditLogItemVerticalDTO itemVertical = new CoreAuditLogItemVerticalDTO();
      List<CoreAuditLogItemActivityVerticalDTO> itemActivityList = new ArrayList<>();
      CoreAuditLogActivityBean filter = new CoreAuditLogActivityBean();
      filter.setLogId(auditLog.getId());

      for (CoreAuditLogActivityBean logActivity : this.logActivityService.selectList(filter, new Order[]{Order.asc("ID")})) {
         CoreAuditLogItemActivityVerticalDTO activityVertical = new CoreAuditLogItemActivityVerticalDTO();
         activityVertical.setId(logActivity.getId());
         activityVertical.setActivity(I18nHelper.getMessage(logActivity.getActivity()));
         activityVertical.setComment(logActivity.getComment());
         activityVertical.setSignature(this.getSignatureValue(logActivity.getSignature()));
         activityVertical.setUserId(logActivity.getUserId());
         activityVertical.setUserName(this.getUserName(logActivity.getUserId()));
         activityVertical.setTargetTable(auditLog.getTargetTable());
         activityVertical.setCreatedTime(logActivity.getCreatedTime());
         activityVertical.setWitnessId(logActivity.getWitnessId());
         activityVertical.setWitnessName(logActivity.getWitnessName());
         activityVertical.setRequestUrl(
            StringUtils.replace("/secure/core/module/sys/audit/audit-logs/activities/{activityId}/vertical-columns", "{activityId}", logActivity.getId())
         );
         itemActivityList.add(activityVertical);
      }

      itemVertical.setActivityList(itemActivityList);
      CoreAuditLogBean logFilter = new CoreAuditLogBean();
      logFilter.setParentId(auditLog.getTargetId());
      logFilter.setParentTable(auditLog.getTargetTable());
      List<CoreAuditLogBean> auditLogList = this.logDao.selectList(logFilter, new Order[]{Order.asc("ID")});
      List<CoreAuditLogDTO> childAuditLogList = new ArrayList<>();

      for (CoreAuditLogBean childAuditLog : auditLogList) {
         CoreAuditConfigBean childConfig = this.configService.selectByIdIfPresent(childAuditLog.getTargetTable());
         if (childConfig != null && !childAuditLogList.stream().anyMatch(c -> c.getServiceName().equals(childConfig.getName()))) {
            CoreAuditLogDTO auditLogDTO = new CoreAuditLogDTO();
            auditLogDTO.setServiceName(childConfig.getName());
            auditLogDTO.setRequestUrl(
               StringUtils.replace(
                  StringUtils.replace(
                     "/secure/core/module/sys/audit/audit-logs/items?table={table}&parentAuditLogId={parentAuditLogId}",
                     "{table}",
                     childAuditLog.getTargetTable()
                  ),
                  "{parentAuditLogId}",
                  auditLog.getId()
               )
            );
            childAuditLogList.add(auditLogDTO);
         }
      }

      itemVertical.setChildAuditLogList(childAuditLogList);
      return itemVertical;
   }

   @Override
   public List<CoreAuditLogColumnVerticalDTO> selectAuditLogItemActivityColumnVerticalList(Long activityId) {
      CoreAuditLogActivityBean activity = this.logActivityService.selectByIdIfPresent(activityId);
      if (activity == null) {
         return CollectionUtils.emptyList();
      } else {
         CoreAuditLogBean auditLog = this.logDao.selectById(activity.getLogId());
         List<CoreAuditConfigColumnBean> configColumnList = this.configColumnService.selectAuditableListByConfigId(auditLog.getTargetTable());
         CoreAuditLogColumnBean filter = new CoreAuditLogColumnBean();
         filter.setActivityId(activityId);
         List<CoreAuditLogColumnBean> logColumnList = this.logColumnService.selectList(filter, new Order[]{Order.asc("COLUMN")});
         List<CoreAuditLogColumnVerticalDTO> columnVerticalList = new ArrayList<>();
         logColumnList.stream()
            .forEach(
               c -> {
                  if (!ObjectUtils.equals(c.getOldValue(), c.getNewValue())
                     || "T_CORE_LES_INSTANCE_UNIT".equals(auditLog.getTargetTable())
                     || "T_CORE_LES_TEMPLATE_UNIT".equals(auditLog.getTargetTable())) {
                     CoreAuditConfigColumnBean configColumn = configColumnList.stream()
                        .filter(cc -> cc.getColumn().equalsIgnoreCase(c.getColumn()))
                        .findAny()
                        .orElse(null);
                     if (configColumn != null && "1".equals(configColumn.getAuditable())) {
                        CoreAuditLogColumnVerticalDTO columnVertical = new CoreAuditLogColumnVerticalDTO();
                        columnVertical.setColumn(c.getColumn());
                        columnVertical.setColumnName(I18nHelper.getMessage(auditLog.getTargetTable() + "." + c.getColumn()));
                        columnVertical.setOldValue(c.getOldValue());
                        columnVertical.setNewValue(c.getNewValue());
                        columnVerticalList.add(columnVertical);
                     }
                  }
               }
            );
         return columnVerticalList;
      }
   }

   @Override
   public CoreAuditLogItemHorizontalDTO selectAuditLogItemActivityHorizontal(Long auditLogId) {
      CoreAuditLogBean auditLog = this.getDao().selectById(auditLogId);
      CoreAuditLogItemHorizontalDTO itemHorizontal = new CoreAuditLogItemHorizontalDTO();
      List<CoreAuditLogItemActivityHorizontalDTO> itemActivityList = new ArrayList<>();
      CoreAuditLogActivityBean filter = new CoreAuditLogActivityBean();
      filter.setLogId(auditLog.getId());
      List<CoreAuditLogActivityBean> logActivityList = this.logActivityService.selectList(filter, new Order[]{Order.asc("ID")});
      List<CoreAuditLogColumnBean> logColumnList = this.logColumnService
         .getDao()
         .selectListByOneColumnValues(
            logActivityList.stream().map(CoreAuditLogActivityBean::getId).collect(Collectors.toList()), "ACTIVITYID", Order.asc("COLUMN")
         );
      List<CoreAuditConfigColumnBean> configColumnList = this.configColumnService.selectAuditableListByConfigId(auditLog.getTargetTable());
      List<CoreAuditLogColumnHeaderHorizontalDTO> headerList = configColumnList.stream().map(c -> {
         CoreAuditLogColumnHeaderHorizontalDTO header = new CoreAuditLogColumnHeaderHorizontalDTO();
         header.setColumn(c.getColumn());
         header.setColumnName(I18nHelper.getMessage(auditLog.getTargetTable() + "." + c.getColumn()));
         return header;
      }).collect(Collectors.toList());
      this.appendDefaultColumnHeader(headerList);
      itemHorizontal.setHeaderList(headerList);
      List<CoreAuditLogColumnValueHorizontalDTO> oldValueList = null;

      for (CoreAuditLogActivityBean logActivity : logActivityList) {
         List<CoreAuditLogColumnValueHorizontalDTO> valueList = new ArrayList<>();

         for (CoreAuditLogColumnBean logColumn : logColumnList) {
            if (logColumn.getActivityId().equals(logActivity.getId())) {
               CoreAuditConfigColumnBean configColumn = configColumnList.stream()
                  .filter(cc -> cc.getColumn().equalsIgnoreCase(logColumn.getColumn()))
                  .findAny()
                  .orElse(null);
               if (configColumn != null && "1".equals(configColumn.getAuditable())) {
                  CoreAuditLogColumnValueHorizontalDTO value = new CoreAuditLogColumnValueHorizontalDTO();
                  value.setColumn(logColumn.getColumn());
                  value.setValue(logColumn.getNewValue());
                  if (oldValueList != null) {
                     CoreAuditLogColumnValueHorizontalDTO oldValue = oldValueList.stream()
                        .filter(v -> value.getColumn().equals(v.getColumn()))
                        .findAny()
                        .orElse(null);
                     if (oldValue != null && !ObjectUtils.equals(oldValue.getValue(), value.getValue())) {
                        value.setModified("1");
                     }
                  }

                  valueList.add(value);
               }
            }
         }

         this.appendDefaultColumnValue(valueList, logActivity);
         oldValueList = valueList;
         CoreAuditLogItemActivityHorizontalDTO itemActivity = new CoreAuditLogItemActivityHorizontalDTO();
         itemActivity.setValueList(valueList);
         itemActivityList.add(itemActivity);
      }

      itemHorizontal.setActivityList(itemActivityList);
      CoreAuditLogBean logFilter = new CoreAuditLogBean();
      logFilter.setParentId(auditLog.getTargetId());
      logFilter.setParentTable(auditLog.getTargetTable());
      List<CoreAuditLogBean> auditLogList = this.logDao.selectList(logFilter, new Order[]{Order.asc("ID")});
      List<CoreAuditLogDTO> childAuditLogList = new ArrayList<>();

      for (CoreAuditLogBean childAuditLog : auditLogList) {
         CoreAuditConfigBean childConfig = this.configService.selectByIdIfPresent(childAuditLog.getTargetTable());
         if (childConfig != null && !childAuditLogList.stream().anyMatch(c -> c.getServiceName().equals(childConfig.getName()))) {
            CoreAuditLogDTO auditLogDTO = new CoreAuditLogDTO();
            auditLogDTO.setServiceName(childConfig.getName());
            auditLogDTO.setRequestUrl(
               StringUtils.replace(
                  StringUtils.replace(
                     "/secure/core/module/sys/audit/audit-logs/items?table={table}&parentAuditLogId={parentAuditLogId}",
                     "{table}",
                     childAuditLog.getTargetTable()
                  ),
                  "{parentAuditLogId}",
                  auditLog.getId()
               )
            );
            childAuditLogList.add(auditLogDTO);
         }
      }

      itemHorizontal.setChildAuditLogList(childAuditLogList);
      return itemHorizontal;
   }

   @Override
   public CoreAuditLogVerticalRepresentation selectAuditLogVertical(String table, String id) {
      CoreAuditLogVerticalRepresentation auditLogVerticalRepresentation = new CoreAuditLogVerticalRepresentation();
      CoreAuditConfigBean config = this.configService.selectByIdIfPresent(table);
      if (config == null) {
         return null;
      } else {
         auditLogVerticalRepresentation.setServiceName(config.getName());
         CoreAuditLogBean filter = new CoreAuditLogBean();
         filter.setTargetId(id);
         filter.setTargetTable(table);
         CoreAuditLogBean auditLog = this.getDao().selectOneIfPresent(filter, new String[0]);
         List<CoreAuditLogItemVerticalRepresentation> auditLogItemRepresentationList = new ArrayList<>();
         if (auditLog != null) {
            CoreAuditLogItemVerticalRepresentation auditLogItemVerticalRepresentation = new CoreAuditLogItemVerticalRepresentation();
            if (!StringUtils.isEmpty(auditLog.getItemDesc())) {
               auditLogItemVerticalRepresentation.setItemDesc(auditLog.getItemDesc());
            } else {
               if (!StringUtils.isBlank(config.getSelectDescSql())) {
                  auditLogItemVerticalRepresentation.setItemDesc(
                     this.globalMapper.selectOneString(StringUtils.replace(config.getSelectDescSql(), "#id#", auditLog.getTargetId()))
                  );
               }

               if (StringUtils.isBlank(auditLogItemVerticalRepresentation.getItemDesc())) {
                  auditLogItemVerticalRepresentation.setItemDesc(auditLog.getTargetId());
               }
            }

            List<CoreAuditLogItemActivityVerticalRepresentation> itemActivityList = new ArrayList<>();
            CoreAuditLogActivityBean auditLogActivityfilter = new CoreAuditLogActivityBean();
            auditLogActivityfilter.setLogId(auditLog.getId());

            for (CoreAuditLogActivityBean logActivity : this.logActivityService.selectList(auditLogActivityfilter, new Order[]{Order.asc("ID")})) {
               CoreAuditLogItemActivityVerticalRepresentation activityVertical = new CoreAuditLogItemActivityVerticalRepresentation();
               activityVertical.setId(logActivity.getId());
               activityVertical.setActivity(I18nHelper.getMessage(logActivity.getActivity()));
               activityVertical.setComment(logActivity.getComment());
               activityVertical.setSignature(this.getSignatureValue(logActivity.getSignature()));
               activityVertical.setUserId(logActivity.getUserId());
               activityVertical.setUserName(this.getUserName(logActivity.getUserId()));
               activityVertical.setTargetTable(auditLog.getTargetTable());
               activityVertical.setCreatedTime(logActivity.getCreatedTime());
               activityVertical.setWitnessId(logActivity.getWitnessId());
               activityVertical.setWitnessName(logActivity.getWitnessName());
               activityVertical.setAuditLogColumnVerticalDTOList(this.selectAuditLogItemActivityColumnVerticalList(logActivity.getId()));
               itemActivityList.add(activityVertical);
            }

            auditLogItemVerticalRepresentation.setActivityList(itemActivityList);
            auditLogItemVerticalRepresentation.setChildren(this.selectChildAuditLogListVertical(auditLog));
            auditLogItemRepresentationList.add(auditLogItemVerticalRepresentation);
         }

         auditLogVerticalRepresentation.setChildren(auditLogItemRepresentationList);
         return auditLogVerticalRepresentation;
      }
   }

   @Override
   public CoreAuditLogHorizontalRepresentation selectAuditLogHorizontal(String table, String id) {
      CoreAuditLogHorizontalRepresentation auditLogHorizontalRepresentation = new CoreAuditLogHorizontalRepresentation();
      CoreAuditConfigBean config = this.configService.selectByIdIfPresent(table);
      if (config == null) {
         return null;
      } else {
         auditLogHorizontalRepresentation.setServiceName(config.getName());
         CoreAuditLogBean filter = new CoreAuditLogBean();
         filter.setTargetId(id);
         filter.setTargetTable(table);
         CoreAuditLogBean auditLog = this.getDao().selectOneIfPresent(filter, new String[0]);
         List<CoreAuditLogItemHorizontalRepresentation> auditLogItemRepresentationList = new ArrayList<>();
         if (auditLog != null) {
            CoreAuditLogItemHorizontalRepresentation auditLogItemHorizontalRepresentation = new CoreAuditLogItemHorizontalRepresentation();
            if (!StringUtils.isEmpty(auditLog.getItemDesc())) {
               auditLogItemHorizontalRepresentation.setItemDesc(auditLog.getItemDesc());
            } else {
               if (!StringUtils.isBlank(config.getSelectDescSql())) {
                  auditLogItemHorizontalRepresentation.setItemDesc(
                     this.globalMapper.selectOneString(StringUtils.replace(config.getSelectDescSql(), "#id#", auditLog.getTargetId()))
                  );
               }

               if (StringUtils.isBlank(auditLogItemHorizontalRepresentation.getItemDesc())) {
                  auditLogItemHorizontalRepresentation.setItemDesc(auditLog.getTargetId());
               }
            }

            List<CoreAuditLogItemActivityHorizontalDTO> itemActivityList = new ArrayList<>();
            CoreAuditLogActivityBean logActivityFilter = new CoreAuditLogActivityBean();
            logActivityFilter.setLogId(auditLog.getId());
            List<CoreAuditLogActivityBean> logActivityList = this.logActivityService.selectList(logActivityFilter, new Order[]{Order.asc("ID")});
            List<CoreAuditLogColumnBean> logColumnList = this.logColumnService
               .getDao()
               .selectListByOneColumnValues(
                  logActivityList.stream().map(CoreAuditLogActivityBean::getId).collect(Collectors.toList()), "ACTIVITYID", Order.asc("COLUMN")
               );
            List<CoreAuditConfigColumnBean> configColumnList = this.configColumnService.selectAuditableListByConfigId(auditLog.getTargetTable());
            List<CoreAuditLogColumnHeaderHorizontalDTO> headerList = configColumnList.stream().map(c -> {
               CoreAuditLogColumnHeaderHorizontalDTO header = new CoreAuditLogColumnHeaderHorizontalDTO();
               header.setColumn(c.getColumn());
               header.setColumnName(I18nHelper.getMessage(auditLog.getTargetTable() + "." + c.getColumn()));
               return header;
            }).collect(Collectors.toList());
            this.appendDefaultColumnHeader(headerList);
            auditLogItemHorizontalRepresentation.setHeaderList(headerList);
            List<CoreAuditLogColumnValueHorizontalDTO> oldValueList = null;

            for (CoreAuditLogActivityBean logActivity : logActivityList) {
               List<CoreAuditLogColumnValueHorizontalDTO> valueList = new ArrayList<>();

               for (CoreAuditLogColumnBean logColumn : logColumnList) {
                  if (logColumn.getActivityId().equals(logActivity.getId())) {
                     CoreAuditConfigColumnBean configColumn = configColumnList.stream()
                        .filter(cc -> cc.getColumn().equalsIgnoreCase(logColumn.getColumn()))
                        .findAny()
                        .orElse(null);
                     if (configColumn != null && "1".equals(configColumn.getAuditable())) {
                        CoreAuditLogColumnValueHorizontalDTO value = new CoreAuditLogColumnValueHorizontalDTO();
                        value.setColumn(logColumn.getColumn());
                        value.setValue(logColumn.getNewValue());
                        if (oldValueList != null) {
                           CoreAuditLogColumnValueHorizontalDTO oldValue = oldValueList.stream()
                              .filter(v -> value.getColumn().equals(v.getColumn()))
                              .findAny()
                              .orElse(null);
                           if (oldValue != null && !ObjectUtils.equals(oldValue.getValue(), value.getValue())) {
                              value.setModified("1");
                           }
                        }

                        valueList.add(value);
                     }
                  }
               }

               this.appendDefaultColumnValue(valueList, logActivity);
               oldValueList = valueList;
               CoreAuditLogItemActivityHorizontalDTO itemActivity = new CoreAuditLogItemActivityHorizontalDTO();
               itemActivity.setValueList(valueList);
               itemActivityList.add(itemActivity);
            }

            auditLogItemHorizontalRepresentation.setActivityList(itemActivityList);
            auditLogItemHorizontalRepresentation.setChildren(this.selectChildAuditLogListHorizontal(auditLog));
            auditLogItemRepresentationList.add(auditLogItemHorizontalRepresentation);
         }

         auditLogHorizontalRepresentation.setChildren(auditLogItemRepresentationList);
         return auditLogHorizontalRepresentation;
      }
   }

   @Override
   public Page<CoreAuditLogTraceOverviewDTO> selectTraceOverview(RestJsonWrapperBean wrapper) {
      String tableName = wrapper.getFilterValue("tableName");
      SpecialRecordLineHelper.checkAvailable(tableName);
      String targetId = wrapper.getFilterValue("targetId");
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      String nameField = SpecialRecordLineHelper.getNameColumnByTableName(tableName);
      return MybatisPageHelper.get(
         rowBounds,
         () -> {
            List<CoreAuditLogTraceOverviewDTO> overviewDTOList = this.getDao().selectTraceOverview(targetId, tableName, nameField);
            List<CoreAuditLogTraceOverviewDTO> deletedOverviewList = overviewDTOList.stream()
               .filter(coreAuditTraceOverviewDTOx -> StringUtils.isBlank(coreAuditTraceOverviewDTOx.getTargetName()))
               .peek(coreAuditTraceOverviewDTOx -> coreAuditTraceOverviewDTOx.setDeleted(true))
               .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(deletedOverviewList)) {
               List<String> targetIds = deletedOverviewList.stream().map(CoreAuditLogTraceOverviewDTO::getTargetId).distinct().collect(Collectors.toList());
               List<CoreAuditLogColumnNameDTO> deletedNames = this.logColumnService.selectDeletedNames(targetIds, tableName, nameField);
               Map<String, String> targetNameMap = deletedNames.stream()
                  .filter(nameDTO -> !StringUtils.isBlank(nameDTO.getName()))
                  .filter(nameDTO -> !StringUtils.isBlank(nameDTO.getTargetId()))
                  .collect(Collectors.toMap(CoreAuditLogColumnNameDTO::getTargetId, CoreAuditLogColumnNameDTO::getName, (a, b) -> a));

               for (CoreAuditLogTraceOverviewDTO coreAuditTraceOverviewDTO : deletedOverviewList) {
                  String targetName = targetNameMap.get(coreAuditTraceOverviewDTO.getTargetId());
                  coreAuditTraceOverviewDTO.setTargetName(targetName);
               }
            }

            return overviewDTOList;
         }
      );
   }

   @Override
   public List<CoreAuditLogColumnDTO> selectColumnsByTargetInfo(String tableName, String id) {
      if (!StringUtils.isBlank(tableName) && !StringUtils.isBlank(id)) {
         List<CoreAuditLogColumnDTO> coreAuditLogColumnDTOList = this.logColumnService.selectColumnsByTargetInfo(tableName, id);
         coreAuditLogColumnDTOList = coreAuditLogColumnDTOList.stream()
            .filter(coreAuditLogColumnDTO -> !Objects.equals(coreAuditLogColumnDTO.getOldValue(), coreAuditLogColumnDTO.getNewValue()))
            .collect(Collectors.toList());
         SpecialAuditLogColumnFilterContext context = new SpecialAuditLogColumnFilterContext(coreAuditLogColumnDTOList);

         for (SpecialAuditLogColumnFilter specialAuditLogColumnFilter : this.specialAuditLogColumnFilters) {
            specialAuditLogColumnFilter.filter(context);
         }

         return coreAuditLogColumnDTOList;
      } else {
         return Collections.emptyList();
      }
   }

   @Override
   public Page<CoreAuditLogActivityBean> selectActivityQueryMorePagination(RestJsonWrapperBean wrapper) {
      return this.logActivityService.selectQueryMorePagination(wrapper);
   }

   public void updateItemDesc(CoreAuditConfigBean config, Long logId, CaseInsensitiveLinkedMap<Object> valueMap) {
      if (!StringUtils.isBlank(config.getSelectDescSql())) {
         String upperSelectDescSql = StringUtils.upperCase(config.getSelectDescSql());
         List<String> itemDescFieldList = StringUtils.substringsBetween(upperSelectDescSql, "SELECT", "FROM");
         if (!itemDescFieldList.isEmpty()) {
            String itemDescField = itemDescFieldList.get(0);
            Object value = valueMap.get(itemDescField.trim());
            if (value != null && !StringUtils.isEmpty(value.toString())) {
               CoreAuditLogBean updateAuditLog = new CoreAuditLogBean();
               updateAuditLog.setId(logId);
               updateAuditLog.setItemDesc(value.toString());
               this.getDao().update(updateAuditLog, new String[]{"ITEMDESC"});
            }
         }
      }
   }

   private Long insertAuditLogCascade(CoreAuditConfigBean config, String id) {
      CoreAuditLogBean filter = new CoreAuditLogBean();
      filter.setTargetId(id);
      filter.setTargetTable(config.getId());
      CoreAuditLogBean auditLog = this.getDao().selectFirstIfPresent(filter, new Order[0]);
      if (auditLog != null) {
         return auditLog.getId();
      } else {
         CoreAuditLogBean newAuditLog = new CoreAuditLogBean();
         newAuditLog.setTargetId(id);
         newAuditLog.setTargetTable(config.getId());
         if (!StringUtils.isBlank(config.getSelectParentSql())) {
            Map<String, Object> map = this.globalMapper.selectOne(StringUtils.replace(config.getSelectParentSql(), "#id#", id));
            if (map == null || map.isEmpty()) {
               return null;
            }

            newAuditLog.setParentId(ConvertUtils.convert(CollectionUtils.getValueIgnorecase(map, "id"), String.class));
            newAuditLog.setParentTable(ConvertUtils.convert(CollectionUtils.getValueIgnorecase(map, "tablename"), String.class));
            CoreAuditConfigBean parentConfig = this.configService.selectByIdIfPresent(newAuditLog.getParentTable());
            if (parentConfig != null) {
               GikamConcurrentLocker.block("AUDIT_LOG$" + parentConfig.getId() + "$" + newAuditLog.getParentId());
               this.insertAuditLogCascadeInNewTransaction(parentConfig, newAuditLog.getParentId());
            }
         }

         newAuditLog.setId(ApplicationContextHelper.getNextIdentity());
         this.getDao().insert(newAuditLog);
         return newAuditLog.getId();
      }
   }

   private void appendDefaultColumnHeader(List<CoreAuditLogColumnHeaderHorizontalDTO> headerList) {
      CoreAuditLogColumnHeaderHorizontalDTO userIdHeader = new CoreAuditLogColumnHeaderHorizontalDTO();
      userIdHeader.setColumn("_USERID");
      userIdHeader.setColumnName(I18nHelper.getMessage("T_CORE_AUDIT_LOG_ACTIVITY.USERID"));
      CoreAuditLogColumnHeaderHorizontalDTO userNameHeader = new CoreAuditLogColumnHeaderHorizontalDTO();
      userNameHeader.setColumn("_USERNAME");
      userNameHeader.setColumnName(I18nHelper.getMessage("T_CORE_AUDIT_LOG_ACTIVITY.USERNAME"));
      CoreAuditLogColumnHeaderHorizontalDTO createdTimeHeader = new CoreAuditLogColumnHeaderHorizontalDTO();
      createdTimeHeader.setColumn("_CREATEDTIME");
      createdTimeHeader.setColumnName(I18nHelper.getMessage("T_CORE_AUDIT_LOG_ACTIVITY.CREATEDTIME"));
      CoreAuditLogColumnHeaderHorizontalDTO commentHeader = new CoreAuditLogColumnHeaderHorizontalDTO();
      commentHeader.setColumn("_COMMENT");
      commentHeader.setColumnName(I18nHelper.getMessage("T_CORE_AUDIT_LOG_ACTIVITY.COMMENT"));
      CoreAuditLogColumnHeaderHorizontalDTO signatureHeader = new CoreAuditLogColumnHeaderHorizontalDTO();
      signatureHeader.setColumn("_SIGNATURE");
      signatureHeader.setColumnName(I18nHelper.getMessage("T_CORE_AUDIT_LOG_ACTIVITY.SIGNATURE"));
      CoreAuditLogColumnHeaderHorizontalDTO activityHeader = new CoreAuditLogColumnHeaderHorizontalDTO();
      activityHeader.setColumn("_ACTIVITY");
      activityHeader.setColumnName(I18nHelper.getMessage("T_CORE_AUDIT_LOG_ACTIVITY.ACTIVITY"));
      headerList.add(userIdHeader);
      headerList.add(userNameHeader);
      headerList.add(createdTimeHeader);
      headerList.add(commentHeader);
      headerList.add(signatureHeader);
      headerList.add(activityHeader);
   }

   private void appendDefaultColumnValue(List<CoreAuditLogColumnValueHorizontalDTO> valueList, CoreAuditLogActivityBean logActivity) {
      CoreAuditLogColumnValueHorizontalDTO userIdValue = new CoreAuditLogColumnValueHorizontalDTO();
      userIdValue.setColumn("_USERID");
      userIdValue.setValue(logActivity.getUserId());
      CoreAuditLogColumnValueHorizontalDTO userNameValue = new CoreAuditLogColumnValueHorizontalDTO();
      userNameValue.setColumn("_USERNAME");
      userNameValue.setValue(this.getUserName(logActivity.getUserId()));
      CoreAuditLogColumnValueHorizontalDTO createdTimeValue = new CoreAuditLogColumnValueHorizontalDTO();
      createdTimeValue.setColumn("_CREATEDTIME");
      createdTimeValue.setValue(DateTimeUtils.formatLocalDateTime(logActivity.getCreatedTime()));
      CoreAuditLogColumnValueHorizontalDTO commentValue = new CoreAuditLogColumnValueHorizontalDTO();
      commentValue.setColumn("_COMMENT");
      commentValue.setValue(logActivity.getComment());
      CoreAuditLogColumnValueHorizontalDTO signatrueValue = new CoreAuditLogColumnValueHorizontalDTO();
      signatrueValue.setColumn("_SIGNATURE");
      signatrueValue.setValue(this.getSignatureValue(logActivity.getSignature()));
      CoreAuditLogColumnValueHorizontalDTO activityValue = new CoreAuditLogColumnValueHorizontalDTO();
      activityValue.setColumn("_ACTIVITY");
      activityValue.setValue(I18nHelper.getMessage(logActivity.getActivity()));
      valueList.add(userIdValue);
      valueList.add(userNameValue);
      valueList.add(createdTimeValue);
      valueList.add(commentValue);
      valueList.add(signatrueValue);
      valueList.add(activityValue);
   }

   private String getSignatureValue(String signature) {
      return "1".equals(signature) ? "Y" : "N";
   }

   private List<CoreAuditLogVerticalRepresentation> selectChildAuditLogListVertical(CoreAuditLogBean auditLog) {
      CoreAuditLogBean logFilter = new CoreAuditLogBean();
      logFilter.setParentId(auditLog.getTargetId());
      logFilter.setParentTable(auditLog.getTargetTable());
      List<CoreAuditLogVerticalRepresentation> childAuditLogList = new ArrayList<>();
      List<CoreAuditLogBean> childTableAuditLogList = this.logDao.selectList(logFilter, new Order[]{Order.asc("ID")});

      for (String targetTable : childTableAuditLogList.stream().map(e -> e.getTargetTable()).distinct().collect(Collectors.toList())) {
         CoreAuditConfigBean childConfig = this.configService.selectByIdIfPresent(targetTable);
         if (childConfig != null) {
            CoreAuditLogVerticalRepresentation childAuditLogVerticalRepresentation = new CoreAuditLogVerticalRepresentation();
            childAuditLogVerticalRepresentation.setServiceName(childConfig.getName());
            List<CoreAuditLogBean> targetTableAuditLogList = childTableAuditLogList.stream()
               .filter(e -> targetTable.equals(e.getTargetTable()))
               .collect(Collectors.toList());
            List<CoreAuditLogItemVerticalRepresentation> auditLogItemVerticalRepresentationList = new ArrayList<>();

            for (CoreAuditLogBean targetTableAuditLog : targetTableAuditLogList) {
               CoreAuditLogItemVerticalRepresentation logItem = new CoreAuditLogItemVerticalRepresentation();
               if (!StringUtils.isBlank(childConfig.getSelectDescSql())) {
                  logItem.setItemDesc(
                     this.globalMapper.selectOneString(StringUtils.replace(childConfig.getSelectDescSql(), "#id#", targetTableAuditLog.getTargetId()))
                  );
               }

               CoreAuditLogVerticalRepresentation auditLogVertical = this.selectAuditLogVertical(
                  targetTableAuditLog.getTargetTable(), targetTableAuditLog.getTargetId()
               );
               if (auditLogVertical.getChildren() != null && !auditLogVertical.getChildren().isEmpty()) {
                  CoreAuditLogItemVerticalRepresentation auditLogItemVerticalRepresentation = auditLogVertical.getChildren().get(0);
                  logItem.setActivityList(auditLogItemVerticalRepresentation.getActivityList());
                  logItem.setChildren(auditLogItemVerticalRepresentation.getChildren());
               }

               auditLogItemVerticalRepresentationList.add(logItem);
            }

            childAuditLogVerticalRepresentation.setChildren(auditLogItemVerticalRepresentationList);
            childAuditLogList.add(childAuditLogVerticalRepresentation);
         }
      }

      return childAuditLogList;
   }

   private List<CoreAuditLogHorizontalRepresentation> selectChildAuditLogListHorizontal(CoreAuditLogBean auditLog) {
      CoreAuditLogBean logFilter = new CoreAuditLogBean();
      logFilter.setParentId(auditLog.getTargetId());
      logFilter.setParentTable(auditLog.getTargetTable());
      List<CoreAuditLogHorizontalRepresentation> childAuditLogList = new ArrayList<>();
      List<CoreAuditLogBean> childTableAuditLogList = this.logDao.selectList(logFilter, new Order[]{Order.asc("ID")});

      for (String targetTable : childTableAuditLogList.stream().map(e -> e.getTargetTable()).distinct().collect(Collectors.toList())) {
         CoreAuditConfigBean childConfig = this.configService.selectByIdIfPresent(targetTable);
         if (childConfig != null) {
            CoreAuditLogHorizontalRepresentation childAuditLogHorizontalRepresentation = new CoreAuditLogHorizontalRepresentation();
            childAuditLogHorizontalRepresentation.setServiceName(childConfig.getName());
            List<CoreAuditLogBean> targetTableAuditLogList = childTableAuditLogList.stream()
               .filter(e -> targetTable.equals(e.getTargetTable()))
               .collect(Collectors.toList());
            List<CoreAuditLogItemHorizontalRepresentation> auditLogItemHorizontalRepresentationList = new ArrayList<>();

            for (CoreAuditLogBean targetTableAuditLog : targetTableAuditLogList) {
               CoreAuditLogItemHorizontalRepresentation logItem = new CoreAuditLogItemHorizontalRepresentation();
               if (!StringUtils.isBlank(childConfig.getSelectDescSql())) {
                  logItem.setItemDesc(
                     this.globalMapper.selectOneString(StringUtils.replace(childConfig.getSelectDescSql(), "#id#", targetTableAuditLog.getTargetId()))
                  );
               }

               CoreAuditLogHorizontalRepresentation auditLogHorizontal = this.selectAuditLogHorizontal(
                  targetTableAuditLog.getTargetTable(), targetTableAuditLog.getTargetId()
               );
               if (auditLogHorizontal.getChildren() != null && !auditLogHorizontal.getChildren().isEmpty()) {
                  CoreAuditLogItemHorizontalRepresentation auditLogItemHorizontalRepresentation = auditLogHorizontal.getChildren().get(0);
                  logItem.setActivityList(auditLogItemHorizontalRepresentation.getActivityList());
                  logItem.setHeaderList(auditLogItemHorizontalRepresentation.getHeaderList());
                  logItem.setChildren(auditLogItemHorizontalRepresentation.getChildren());
               }

               auditLogItemHorizontalRepresentationList.add(logItem);
            }

            childAuditLogHorizontalRepresentation.setChildren(auditLogItemHorizontalRepresentationList);
            childAuditLogList.add(childAuditLogHorizontalRepresentation);
         }
      }

      return childAuditLogList;
   }

   private String getUserName(String userId) {
      if (!StringUtils.isBlank(userId) && !"(system)".equals(userId)) {
         CoreUserBean user = this.userService.selectByIdIfPresent(userId);
         return user != null ? user.getUserName() : userId;
      } else {
         return "(system)";
      }
   }
}
