package com.sunwayworld.module.sys.audit.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.context.ColumnContext;
import com.sunwayworld.framework.database.core.DatabaseManager;
import com.sunwayworld.framework.exception.database.JdbcException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.audit.bean.CoreAuditConfigBean;
import com.sunwayworld.module.sys.audit.bean.CoreAuditConfigColumnBean;
import com.sunwayworld.module.sys.audit.dao.CoreAuditConfigDao;
import com.sunwayworld.module.sys.audit.service.CoreAuditConfigColumnService;
import com.sunwayworld.module.sys.audit.service.CoreAuditConfigService;
import com.sunwayworld.module.sys.metadata.bean.CoreTableBean;
import com.sunwayworld.module.sys.metadata.bean.CoreTableHierarchyBean;
import com.sunwayworld.module.sys.metadata.service.CoreTableService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreAuditConfigServiceImpl implements CoreAuditConfigService {
   @Autowired
   private CoreAuditConfigDao configDao;
   @Autowired
   private CoreAuditConfigColumnService configColumnService;
   @Autowired
   private CoreTableService tableService;
   private static final Logger logger = LogManager.getLogger(CoreAuditConfigService.class);

   public CoreAuditConfigDao getDao() {
      return this.configDao;
   }

   @Transactional
   @Override
   public void init() {
      List<CoreAuditConfigBean> configList = this.getDao().selectAll();
      List<CoreTableBean> tableList = this.tableService.selectAll();
      List<CoreAuditConfigBean> insertConfigList = new ArrayList<>();
      List<CoreAuditConfigColumnBean> insertConfigColumnList = new ArrayList<>();

      for (CoreTableBean table : tableList) {
         if (configList.stream().noneMatch(c -> c.getId().equalsIgnoreCase(table.getId()))) {
            CoreAuditConfigBean config = new CoreAuditConfigBean();
            config.setId(table.getId());
            if (StringUtils.startsWith(table.getId(), "T_CORE_")) {
               config.setCategory("system");
            } else {
               config.setCategory("service");
            }

            config.setName(I18nHelper.getMessage(table.getId() + ".SERVICE_NAME"));
            config.setAuditable("0");
            List<CoreTableHierarchyBean> tableHierarchyList = this.tableService.selectTableHierarchy(config.getId());
            if (tableHierarchyList.size() == 1) {
               CoreTableHierarchyBean tableHierarchy = tableHierarchyList.get(0);
               if (tableHierarchy != null) {
                  StringBuilder selectParentSqlSb = new StringBuilder("SELECT '")
                     .append(tableHierarchy.getMasterTableName())
                     .append("' TABLENAME, ")
                     .append(tableHierarchy.getReferColumn())
                     .append(" ID FROM ")
                     .append(table.getId())
                     .append(" WHERE ID = '#id#'");
                  config.setSelectParentSql(selectParentSqlSb.toString());
               }
            }

            insertConfigList.add(config);

            try {
               List<ColumnContext> columnList = DatabaseManager.getTableColumnContextList(config.getId());
               List<CoreAuditConfigColumnBean> configColumnList = columnList.stream().map(c -> {
                  CoreAuditConfigColumnBean column = new CoreAuditConfigColumnBean();
                  column.setId(ApplicationContextHelper.getNextIdentity());
                  column.setConfigId(config.getId());
                  column.setColumn(c.getColumnName().toUpperCase());
                  column.setAuditable("0");
                  return column;
               }).collect(Collectors.toList());
               insertConfigColumnList.addAll(configColumnList);
            } catch (JdbcException var11) {
               logger.warn(var11);
            }
         } else {
            this.initConfigColumn(table.getId());
         }
      }

      this.getDao().insert(insertConfigList);
      this.configColumnService.getDao().insert(insertConfigColumnList);
   }

   @Transactional
   public String insert(RestJsonWrapperBean wrapper) {
      CoreAuditConfigBean config = wrapper.parseUnique(CoreAuditConfigBean.class);
      config.setAuditable("0");
      this.getDao().insert(config);
      List<ColumnContext> columnList = DatabaseManager.getTableColumnContextList(config.getId());
      List<CoreAuditConfigColumnBean> configColumnList = columnList.stream().map(c -> {
         CoreAuditConfigColumnBean column = new CoreAuditConfigColumnBean();
         column.setId(ApplicationContextHelper.getNextIdentity());
         column.setConfigId(config.getId());
         column.setColumn(c.getColumnName().toUpperCase());
         column.setAuditable("0");
         return column;
      }).collect(Collectors.toList());
      this.configColumnService.getDao().insert(configColumnList);
      return config.getId();
   }

   @Transactional
   @Override
   public void initConfigColumn(String id) {
      List<CoreAuditConfigColumnBean> configColumnList = this.configColumnService.selectListByConfigId(id);
      List<ColumnContext> columnList = DatabaseManager.getTableColumnContextList(id);
      List<Long> deleteConfigColumnIdList = configColumnList.stream()
         .filter(cc -> columnList.stream().noneMatch(c -> c.getColumnName().equalsIgnoreCase(cc.getColumn())))
         .map(CoreAuditConfigColumnBean::getId)
         .collect(Collectors.toList());
      List<CoreAuditConfigColumnBean> insertConfigColumnList = columnList.stream()
         .filter(c -> configColumnList.stream().noneMatch(cc -> cc.getColumn().equalsIgnoreCase(c.getColumnName())))
         .map(c -> {
            CoreAuditConfigColumnBean column = new CoreAuditConfigColumnBean();
            column.setId(ApplicationContextHelper.getNextIdentity());
            column.setConfigId(id);
            column.setColumn(c.getColumnName().toUpperCase());
            column.setAuditable("0");
            return column;
         })
         .collect(Collectors.toList());
      this.configColumnService.getDao().deleteByIdList(deleteConfigColumnIdList);
      this.configColumnService.getDao().insert(insertConfigColumnList);
   }

   @Override
   public Page<CoreAuditConfigColumnBean> selectConfigColumnPaginationById(String id) {
      return new Pagination<>(this.configColumnService.selectListByConfigId(id));
   }

   @Transactional
   @Override
   public void enableConfigColumn(String id, RestJsonWrapperBean wrapper) {
      this.enableOrDisableConfigColumn(id, wrapper, true);
   }

   @Transactional
   @Override
   public void disableConfigColumn(String id, RestJsonWrapperBean wrapper) {
      this.enableOrDisableConfigColumn(id, wrapper, false);
   }

   private void enableOrDisableConfigColumn(String id, RestJsonWrapperBean wrapper, boolean enable) {
      List<CoreAuditConfigColumnBean> configColumnList = wrapper.parse(CoreAuditConfigColumnBean.class);
      configColumnList.forEach(c -> c.setAuditable(enable ? "1" : "0"));
      this.configColumnService.getDao().update(configColumnList, "AUDITABLE");
   }
}
