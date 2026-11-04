package com.sunwayworld.cloud.module.lcdp.table.service.impl;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpAnalysisResultDTO;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceCheckoutRecordService;
import com.sunwayworld.cloud.module.lcdp.resourcelock.bean.LcdpResourceLockBean;
import com.sunwayworld.cloud.module.lcdp.resourcelock.service.LcdpResourceLockService;
import com.sunwayworld.cloud.module.lcdp.resourceversion.bean.LcdpResourceVersionBean;
import com.sunwayworld.cloud.module.lcdp.resourceversion.service.LcdpResourceVersionService;
import com.sunwayworld.cloud.module.lcdp.submitlog.bean.LcdpSubmitLogBean;
import com.sunwayworld.cloud.module.lcdp.submitlog.service.LcdpSubmitLogService;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpDynamicPagination;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableCompareDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableFieldBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableIndexBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableTreeNodeDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableViewDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpViewBean;
import com.sunwayworld.cloud.module.lcdp.table.helper.IndexType;
import com.sunwayworld.cloud.module.lcdp.table.helper.LcdpTableUtils;
import com.sunwayworld.cloud.module.lcdp.table.helper.SqlGenerateHelper;
import com.sunwayworld.cloud.module.lcdp.table.persistent.dao.LcdpDatabaseDao;
import com.sunwayworld.cloud.module.lcdp.table.persistent.dao.LcdpTableDao;
import com.sunwayworld.cloud.module.lcdp.table.service.LcdpDatabaseService;
import com.sunwayworld.cloud.module.lcdp.table.service.LcdpTableFieldService;
import com.sunwayworld.cloud.module.lcdp.table.service.LcdpTableIndexService;
import com.sunwayworld.cloud.module.lcdp.table.service.LcdpTableService;
import com.sunwayworld.cloud.module.lcdp.table.service.LcdpViewService;
import com.sunwayworld.cloud.module.lcdp.table.validator.LcdpTableDeleteDataValidator;
import com.sunwayworld.cloud.module.lcdp.table.validator.LcdpTableSaveDataValidator;
import com.sunwayworld.cloud.module.lcdp.table.validator.LcdpTableShieldUtils;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.context.SunwayAopContext;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.context.ColumnContext;
import com.sunwayworld.framework.database.core.DatabaseManager;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.exception.core.ApplicationWarningException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ExceptionUtils;
import com.sunwayworld.framework.utils.JsonUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.sys.audit.bean.CoreAuditConfigBean;
import com.sunwayworld.module.sys.audit.bean.CoreAuditConfigColumnBean;
import com.sunwayworld.module.sys.audit.service.CoreAuditConfigColumnService;
import com.sunwayworld.module.sys.audit.service.CoreAuditConfigService;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nBean;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nMessageBean;
import com.sunwayworld.module.sys.i18n.service.CoreI18nMessageService;
import com.sunwayworld.module.sys.i18n.service.CoreI18nService;
import com.sunwayworld.module.sys.menu.bean.CoreMenuExtBean;
import com.sunwayworld.module.sys.menu.service.CoreMenuExtService;
import com.sunwayworld.module.sys.metadata.bean.CoreTableBean;
import com.sunwayworld.module.sys.metadata.bean.CoreTableHierarchyBean;
import com.sunwayworld.module.sys.metadata.dao.CoreTableHierarchyDao;
import com.sunwayworld.module.sys.metadata.service.CoreTableService;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class LcdpTableServiceImpl implements LcdpTableService {
   @Value("${sunway.lcdp.table.auditable:false}")
   private Boolean auditable;
   @Autowired
   private LcdpTableDao lcdpTableDao;
   @Autowired
   private LcdpTableFieldService lcdpTableFieldService;
   @Autowired
   private LcdpTableIndexService lcdpTableIndexService;
   @Autowired
   private LcdpViewService lcdpViewService;
   @Autowired
   private LcdpDatabaseDao lcdpDatabaseDao;
   @Autowired
   private LcdpDatabaseService lcdpDatabaseService;
   @Autowired
   private SqlGenerateHelper sqlGenerateHelper;
   @Autowired
   private LcdpResourceLockService lcdpResourceLockService;
   @Autowired
   @Lazy
   private LcdpResourceVersionService lcdpResourceVersionService;
   @Autowired
   private CoreI18nService coreI18nService;
   @Autowired
   private CoreI18nMessageService coreI18nMessageService;
   @Autowired
   private CoreAuditConfigService coreAuditConfigService;
   @Autowired
   private CoreAuditConfigColumnService coreAuditConfigColumnService;
   @Autowired
   private CoreMenuExtService coreMenuExtService;
   @Autowired
   private CoreTableService coreTableService;
   @Autowired
   private CoreTableHierarchyDao coreTableHierarchyDao;
   @Autowired
   private LcdpResourceCheckoutRecordService resourceCheckoutRecordService;
   @Autowired
   @Lazy
   private LcdpSubmitLogService submitLogService;
   @Autowired
   private GlobalMapper globalMapper;
   @Autowired
   private LcdpTableShieldUtils lcdpTableShieldUtils;

   public LcdpTableDao getDao() {
      return this.lcdpTableDao;
   }

   @Override
   public List<LcdpTableTreeNodeDTO> selectTableTree(RestJsonWrapperBean wrapper) {
      List<LcdpTableTreeNodeDTO> lcdpTableTreeNodeDTOList = new ArrayList<>();
      LcdpTableTreeNodeDTO tablesTreeNodeDTO = new LcdpTableTreeNodeDTO();
      tablesTreeNodeDTO.setId("TABLES");
      tablesTreeNodeDTO.setName("TABLES");
      tablesTreeNodeDTO.setType("folder");
      LcdpTableTreeNodeDTO viewsTreeNodeDTO = new LcdpTableTreeNodeDTO();
      viewsTreeNodeDTO.setId("VIEWS");
      viewsTreeNodeDTO.setName("VIEWS");
      viewsTreeNodeDTO.setType("folder");
      MapperParameter parameter = wrapper.extractMapFilter();
      List<LcdpTableBean> tableList = this.lcdpDatabaseService
         .selectPhysicalTableNameList(parameter)
         .stream()
         .filter(table -> !this.lcdpTableShieldUtils.contains(String.valueOf(table.getTableName())))
         .collect(Collectors.toList());
      List<LcdpViewBean> viewList = this.lcdpDatabaseService.selectPhysicalViewInfoList(parameter);
      String checkoutFlag = wrapper.getParamValue("checkoutFlag");
      List<LcdpTableBean> newTableList = new ArrayList<>();
      List<LcdpViewBean> newViewList = new ArrayList<>();
      if (StringUtils.isEmpty(checkoutFlag)) {
         parameter.setFilter(
            SearchFilter.instance()
               .match("VERSION", 1)
               .filter(MatchPattern.EQ)
               .match("CREATEDBYID", LocalContextHelper.getLoginUserId())
               .filter(MatchPattern.SEQ)
               .match("SUBMITFLAG", "no")
               .filter(MatchPattern.SEQ)
         );
         newTableList = this.selectList(parameter);
         newViewList = this.lcdpViewService.selectList(parameter);
      }

      List<LcdpResourceLockBean> lockedResources = this.lcdpResourceLockService
         .selectListByFilter(
            SearchFilter.instance()
               .match("LOCKUSERID", LocalContextHelper.getLoginUserId())
               .filter(MatchPattern.EQ)
               .match("RESOURCECATEGORY", ArrayUtils.asList("table", "db-view"))
               .filter(MatchPattern.OR),
            new Order[0]
         );
      Map<String, String> tableLockStatus = lockedResources.stream()
         .collect(Collectors.toMap(LcdpResourceLockBean::getResourceId, LcdpResourceLockBean::getLockUserId));
      List<AbstractTreeNode> tableTreeNodeDTOList = new ArrayList<>();
      newTableList.forEach(
         table -> {
            LcdpTableTreeNodeDTO tableTreeNodeDTO = this.createTableTreeNodeDTO(
               table.getTableName(), "table", "TABLES", tableLockStatus.containsKey(table.getTableName()) ? "1" : "0", "new"
            );
            tableTreeNodeDTOList.add(tableTreeNodeDTO);
         }
      );
      tableList.forEach(
         table -> {
            LcdpTableTreeNodeDTO tableTreeNodeDTO = this.createTableTreeNodeDTO(
               table.getTableName(), "table", "TABLES", tableLockStatus.containsKey(table.getTableName()) ? "1" : "0", "valid"
            );
            tableTreeNodeDTOList.add(tableTreeNodeDTO);
         }
      );
      List<AbstractTreeNode> viewTreeNodeDTOList = new ArrayList<>();
      newViewList.forEach(
         view -> {
            LcdpTableTreeNodeDTO tableTreeNodeDTO = this.createTableTreeNodeDTO(
               view.getViewName(), "db-view", "VIEWS", tableLockStatus.containsKey(view.getViewName()) ? "1" : "0", "new"
            );
            viewTreeNodeDTOList.add(tableTreeNodeDTO);
         }
      );
      viewList.forEach(
         view -> {
            LcdpTableTreeNodeDTO tableTreeNodeDTO = this.createTableTreeNodeDTO(
               view.getViewName(), "db-view", "VIEWS", tableLockStatus.containsKey(view.getViewName()) ? "1" : "0", "valid"
            );
            viewTreeNodeDTOList.add(tableTreeNodeDTO);
         }
      );
      tablesTreeNodeDTO.setChildren(tableTreeNodeDTOList);
      viewsTreeNodeDTO.setChildren(viewTreeNodeDTOList);
      lcdpTableTreeNodeDTOList.add(tablesTreeNodeDTO);
      lcdpTableTreeNodeDTOList.add(viewsTreeNodeDTO);
      return lcdpTableTreeNodeDTOList;
   }

   @Override
   public LcdpTableDTO selectTableInfo(Long id) {
      List<LcdpTableDTO> lcdpTableDTOS = this.selectTableInfoList(ArrayUtils.asList(id));
      return lcdpTableDTOS.isEmpty() ? null : lcdpTableDTOS.get(0);
   }

   @Override
   public List<LcdpTableDTO> selectTableInfoList(List<Long> idList) {
      List<LcdpTableBean> lcdpTableList = this.selectListByIds(idList);
      if (lcdpTableList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         List<LcdpTableFieldBean> fieldBeanList = this.lcdpTableFieldService
            .selectListByFilter(SearchFilter.instance().match("TABLEID", idList).filter(MatchPattern.OR), new Order[]{Order.asc("ID")});
         List<LcdpTableIndexBean> indexBeanList = this.lcdpTableIndexService
            .selectListByFilter(SearchFilter.instance().match("TABLEID", idList).filter(MatchPattern.OR), new Order[]{Order.asc("ID")});
         return LcdpTableUtils.parseTableBeanToDTO(lcdpTableList, fieldBeanList, indexBeanList);
      }
   }

   @Override
   public LcdpTableDTO selectPhysicalTableInfo(String tableName) {
      return this.lcdpDatabaseService.selectPhysicalTableInfo(tableName);
   }

   @Override
   public List<LcdpTableDTO> selectPhysicalTableInfoList(List<String> tableNameList) {
      return this.lcdpDatabaseService.selectPhysicalTableInfoList(tableNameList);
   }

   @Override
   public List<LcdpTableBean> selectPhysicalTableNameList(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      List<LcdpTableBean> lcdpTableNameList = this.lcdpDatabaseService.selectPhysicalTableNameList(parameter);
      return lcdpTableNameList.stream().limit(100L).collect(Collectors.toList());
   }

   @Override
   public List<LcdpTableBean> selectVirtualTableNameList(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      List<LcdpTableBean> physicalTableNameList = this.lcdpDatabaseService.selectPhysicalTableNameList(parameter);
      parameter.setFilter(
         SearchFilter.instance()
            .match("VERSION", 1)
            .filter(MatchPattern.EQ)
            .match("CREATEDBYID", LocalContextHelper.getLoginUserId())
            .filter(MatchPattern.SEQ)
            .match("SUBMITFLAG", "no")
            .filter(MatchPattern.SEQ)
      );
      List<LcdpTableBean> lcdpTableNameList = this.selectList(parameter);
      lcdpTableNameList.addAll(physicalTableNameList);
      return lcdpTableNameList.stream().limit(100L).collect(Collectors.toList());
   }

   @Override
   public List<LcdpTableViewDTO> selectPhysicalTableViewNameList(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      List<LcdpTableViewDTO> lcdpTableViewNameList = this.lcdpDatabaseService.selectPhysicalTableOrViewNameList(parameter);
      return lcdpTableViewNameList.stream().limit(300L).collect(Collectors.toList());
   }

   @Override
   public Page<LcdpTableBean> selectChoosablePagination(RestJsonWrapperBean wrapper) {
      return this.selectPagination(() -> this.lcdpDatabaseService.selectPhysicalTableInfoList(wrapper.extractMapFilter()), wrapper.extractPageRowBounds());
   }

   @Override
   public List<LcdpTableFieldBean> selectPhysicalFieldList(RestJsonWrapperBean wrapper) {
      return this.lcdpDatabaseService.selectPhysicalFieldList(wrapper.extractMapFilter());
   }

   @Audit("AUDIT.INSERT")
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Transactional
   @ValidateDataWith(LcdpTableSaveDataValidator.class)
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      LcdpTableBean table = jsonWrapper.parseUnique(LcdpTableBean.class);
      List<LcdpTableFieldBean> fieldList = jsonWrapper.parse(LcdpTableFieldBean.class);
      List<LcdpTableIndexBean> indexList = jsonWrapper.parse(LcdpTableIndexBean.class);
      table.setId(ApplicationContextHelper.getNextIdentity());
      table.setSubmitFlag("no");
      table.setVersion(1L);
      this.lcdpResourceLockService.lock(table.getTableName(), "table");
      this.getDao().insert(table);
      fieldList.forEach(e -> {
         e.setId(ApplicationContextHelper.getNextIdentity());
         e.setTableName(table.getTableName());
         e.setTableId(table.getId());
      });
      indexList.forEach(e -> {
         e.setId(ApplicationContextHelper.getNextIdentity());
         e.setTableName(table.getTableName());
         e.setTableId(table.getId());
      });
      this.lcdpTableIndexService.getDao().insert(indexList);
      this.lcdpTableFieldService.getDao().insert(fieldList);
      this.resourceCheckoutRecordService.checkoutTableOrView(table.getTableName(), table.getTableDesc(), "table");
      return table.getId();
   }

   @Override
   public Long design(String tableName) {
      LcdpTableBean maxVersionTable = this.selectFirstByFilter(
         SearchFilter.instance().match("TABLENAME", tableName).filter(MatchPattern.EQ), new Order[]{Order.desc("VERSION")}
      );
      if (!ObjectUtils.isEmpty(maxVersionTable) && !"yes".equals(maxVersionTable.getSubmitFlag())) {
         return maxVersionTable.getId();
      } else {
         throw new CheckedException("LCDP.MODULE.TABLES.TABLE.EXCEPTION.LOCKED");
      }
   }

   @Transactional
   @Audit("AUDIT.INSERT")
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Override
   public void checkout(String tableName) {
      this.lcdpResourceLockService.lock(tableName, "table");
      LcdpTableBean maxVersionTable = this.selectFirstByFilter(
         SearchFilter.instance().match("TABLENAME", tableName).filter(MatchPattern.SEQ), new Order[]{Order.desc("VERSION")}
      );
      LcdpTableBean newVersionTable = new LcdpTableBean();
      List<LcdpTableFieldBean> fieldBeanList = new ArrayList<>();
      List<LcdpTableIndexBean> indexBeanList = new ArrayList<>();
      if (!ObjectUtils.isEmpty(maxVersionTable)) {
         newVersionTable.setTableDesc(maxVersionTable.getTableDesc());
         newVersionTable.setMasterTableName(maxVersionTable.getMasterTableName());
         newVersionTable.setReferColumn(maxVersionTable.getReferColumn());
         newVersionTable.setVersion(Optional.ofNullable(maxVersionTable.getVersion()).orElse(1L) + 1L);
      } else {
         LcdpTableDTO lcdpTableDTO = this.lcdpDatabaseService.selectPhysicalTableInfo(tableName);
         newVersionTable.setTableName(tableName);
         newVersionTable.setTableDesc(lcdpTableDTO.getTableDesc());
         newVersionTable.setVersion(2L);
      }

      fieldBeanList.addAll(this.lcdpDatabaseService.selectPhysicalFieldList(tableName));
      indexBeanList.addAll(this.lcdpDatabaseService.selectPhysicalIndexList(tableName));
      newVersionTable.setTableName(tableName);
      newVersionTable.setSubmitFlag("no");
      newVersionTable.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(newVersionTable);
      fieldBeanList.forEach(e -> {
         e.setId(ApplicationContextHelper.getNextIdentity());
         e.setTableId(newVersionTable.getId());
      });
      indexBeanList.forEach(e -> {
         e.setId(ApplicationContextHelper.getNextIdentity());
         e.setTableId(newVersionTable.getId());
      });
      this.lcdpTableIndexService.getDao().insert(indexBeanList);
      this.lcdpTableFieldService.getDao().insert(fieldBeanList);
      this.resourceCheckoutRecordService.checkoutTableOrView(tableName, newVersionTable.getTableDesc(), "table");
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void submit(List<LcdpTableBean> tableList) {
      if (!tableList.isEmpty()) {
         List<LcdpTableDTO> physicalTableList = this.lcdpDatabaseService
            .selectPhysicalTableInfoList(tableList.stream().map(LcdpTableBean::getTableName).collect(Collectors.toList()));
         List<LcdpTableDTO> submitTableList = this.selectTableInfoList(tableList.stream().map(LcdpTableBean::getId).collect(Collectors.toList()));
         tableList.forEach(
            table -> {
               LcdpTableDTO physicalTable = physicalTableList.stream()
                  .filter(currentLcdpTable -> StringUtils.equals(currentLcdpTable.getTableName(), table.getTableName()))
                  .findAny()
                  .orElse(null);
               LcdpTableDTO submitTable = submitTableList.stream()
                  .filter(submitLcdpTable -> StringUtils.equals(submitLcdpTable.getTableName(), table.getTableName()))
                  .findFirst()
                  .orElse(null);
               if (!ObjectUtils.isEmpty(submitTable)) {
                  String sql = "";
                  List<LcdpTableFieldBean> fieldOpsList = LcdpTableUtils.analyzeFieldOps(
                     submitTable.getFieldList(), ObjectUtils.isEmpty(physicalTable) ? null : physicalTable.getFieldList()
                  );
                  List<LcdpTableIndexBean> indexOpsList = LcdpTableUtils.analyzeIndexOps(
                     submitTable.getIndexList(), ObjectUtils.isEmpty(physicalTable) ? null : physicalTable.getIndexList()
                  );
                  LcdpAnalysisResultDTO validateResult = LcdpTableUtils.validateTable(physicalTable, submitTable, fieldOpsList, indexOpsList);
                  if (!validateResult.getEnable()) {
                     throw new ApplicationWarningException(
                        table.getTableName() + " : " + validateResult.getAnalysisResultList().stream().collect(Collectors.joining(","))
                     );
                  } else {
                     RedisHelper.evict("TC", table.getTableName());
                     RedisHelper.evict("EC", table.getTableName());
                     if (ObjectUtils.isEmpty(physicalTable)) {
                        sql = SunwayAopContext.<LcdpTableServiceImpl>currentProxy().createPhysicalTable(table, fieldOpsList, indexOpsList);
                     } else {
                        SunwayAopContext.<LcdpTableServiceImpl>currentProxy().alterPhysicalTable(physicalTable, table, fieldOpsList, indexOpsList);
                        sql = this.generateCreateSql(submitTable);
                     }

                     table.setSubmitFlag("yes");
                     table.setSql(sql);
                  }
               }
            }
         );
         this.resourceCheckoutRecordService.unCheckoutTableOrView(tableList.stream().map(LcdpTableBean::getTableName).collect(Collectors.toList()));
         this.getDao().update(tableList, new String[]{"SUBMITFLAG", "SQL"});
      }
   }

   @Override
   public List<String> revert(List<String> tableNameList) {
      List<LcdpTableBean> notSubmittedTableList = this.selectListByFilter(
         SearchFilter.instance()
            .match("TABLENAME", tableNameList)
            .filter(MatchPattern.OR)
            .match("SUBMITFLAG", "no")
            .filter(MatchPattern.SEQ)
            .match("CREATEDBYID", LocalContextHelper.getLoginUserId())
            .filter(MatchPattern.SEQ)
            .match("VERSION", 1)
            .filter(MatchPattern.DIFFER),
         new Order[0]
      );
      notSubmittedTableList.stream().map(LcdpTableBean::getId).forEach(id -> this.deleteCascadeById(id));
      this.resourceCheckoutRecordService.unCheckoutTableOrView(notSubmittedTableList.stream().map(LcdpTableBean::getTableName).collect(Collectors.toList()));
      return notSubmittedTableList.stream().map(LcdpTableBean::getTableName).collect(Collectors.toList());
   }

   @Override
   public LcdpTableCompareDTO<LcdpTableBean> compare(RestJsonWrapperBean wrapper) {
      String tableName = wrapper.getParamValue("TABLENAME");
      Long version = Optional.ofNullable(wrapper.getParamValue("VERSION")).map(Long::valueOf).orElse(1L);
      LcdpTableCompareDTO<LcdpTableBean> differ = new LcdpTableCompareDTO<>();
      LcdpTableBean currentTable = new LcdpTableBean();
      LcdpTableBean previousTable = new LcdpTableBean();
      List<LcdpTableBean> historyTableList = this.selectListByFilter(
         SearchFilter.instance().match("TABLENAME", tableName).filter(MatchPattern.SEQ).match("SUBMITFLAG", "yes").filter(MatchPattern.SEQ), new Order[0]
      );
      LcdpTableBean currentTableBean = historyTableList.stream().filter(table -> version == table.getVersion()).findAny().orElse(null);
      LcdpTableBean previousTableBean = historyTableList.stream().filter(table -> version == table.getVersion() + 1L).findAny().orElse(null);
      BeanUtils.copyProperties(currentTableBean, currentTable);
      BeanUtils.copyProperties(previousTableBean, previousTable);
      differ.setHistoryTableList(historyTableList);
      differ.setCurrentTable(currentTable);
      differ.setPreviousTable(previousTable);
      return differ;
   }

   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Transactional
   @ValidateDataWith(LcdpTableSaveDataValidator.class)
   public void save(Long id, RestJsonWrapperBean wrapper) {
      LcdpTableBean table = wrapper.parseUnique(LcdpTableBean.class);
      List<LcdpTableFieldBean> fieldList = wrapper.parse(LcdpTableFieldBean.class);
      List<LcdpTableIndexBean> indexList = wrapper.parse(LcdpTableIndexBean.class);
      LcdpTableBean lcdpTableBean = this.selectById(id);
      lcdpTableBean.setTableDesc(table.getTableDesc());
      lcdpTableBean.setMasterTableName(table.getMasterTableName());
      lcdpTableBean.setReferColumn(table.getReferColumn());
      this.getDao().update(lcdpTableBean, new String[]{"TABLEDESC", "MASTERTABLENAME", "REFERCOLUMN"});
      fieldList.forEach(e -> {
         e.setId(ApplicationContextHelper.getNextIdentity());
         e.setTableName(table.getTableName());
         e.setTableId(id);
      });
      indexList.forEach(e -> {
         e.setId(ApplicationContextHelper.getNextIdentity());
         e.setTableName(table.getTableName());
         e.setTableId(id);
      });
      this.lcdpTableFieldService.getDao().deleteBy(fieldList, "TABLEID");
      this.lcdpTableIndexService.getDao().deleteBy(indexList, "TABLEID");
      this.lcdpTableFieldService.getDao().insert(fieldList);
      this.lcdpTableIndexService.getDao().insert(indexList);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @ValidateDataWith(LcdpTableDeleteDataValidator.class)
   @Override
   public void delete(RestJsonWrapperBean wrapper) {
      LcdpTableBean toDeleteTable = wrapper.parseUnique(this.getDao().getType());
      LcdpTableBean queryTable = this.selectFirstByFilter(
         SearchFilter.instance().match("TABLENAME", toDeleteTable.getTableName()).filter(MatchPattern.EQ).match("SUBMITFLAG", "yes").filter(MatchPattern.EQ),
         new Order[0]
      );
      List<LcdpResourceLockBean> LockList = this.lcdpResourceLockService
         .selectListByFilter(
            SearchFilter.instance()
               .match("RESOURCEID", toDeleteTable.getTableName())
               .filter(MatchPattern.SEQ)
               .match("LOCKUSERID", LocalContextHelper.getLoginUserId())
               .filter(MatchPattern.SEQ),
            new Order[0]
         );
      if (LockList.isEmpty()) {
         throw new CheckedException("LCDP.MODULE.RESOURCES.TIP.UNCHECKOUT_CANNOT_DELETE");
      } else {
         List<Long> tableIdList = this.getDao().selectIdList(toDeleteTable, ArrayUtils.asList("TABLENAME"), new Order[0]);
         tableIdList.stream().forEach(id -> this.deleteCascadeById(id));
         List<LcdpResourceVersionBean> versionList = this.lcdpResourceVersionService
            .selectListByFilter(SearchFilter.instance().match("RESOURCEID", toDeleteTable.getTableName()).filter(MatchPattern.OR), new Order[0]);
         versionList.forEach(version -> version.setResourceDeleteFlag("1"));
         this.lcdpResourceVersionService.getDao().update(versionList, "RESOURCEDELETEFLAG");
         this.lcdpResourceLockService.unLock(ArrayUtils.asList(toDeleteTable.getTableName()));
         RedisHelper.evict("TC", toDeleteTable.getTableName());
         RedisHelper.evict("EC", toDeleteTable.getTableName());
         if (this.lcdpDatabaseService.isExistPhysicalTable(toDeleteTable.getTableName())) {
            SunwayAopContext.<LcdpTableServiceImpl>currentProxy().dropPhysicalTable(toDeleteTable);
         }

         if (queryTable != null) {
            LcdpSubmitLogBean submitLog = new LcdpSubmitLogBean();
            submitLog.setId(ApplicationContextHelper.getNextIdentity());
            submitLog.setCommit(I18nHelper.getMessage("LCDP.RESOURCE.DELETED"));
            this.submitLogService.getDao().insert(submitLog);
            LcdpResourceVersionBean resourceVersion = new LcdpResourceVersionBean();
            resourceVersion.setId(ApplicationContextHelper.getNextIdentity());
            resourceVersion.setLogId(submitLog.getId());
            resourceVersion.setResourcePath(queryTable.getTableName());
            resourceVersion.setResourceDeleteFlag("1");
            resourceVersion.setResourceAction("delete");
            resourceVersion.setResourceCategory("table");
            this.lcdpResourceVersionService.getDao().insert(resourceVersion);
         }

         this.resourceCheckoutRecordService.unCheckoutTableOrView(ArrayUtils.asList(toDeleteTable.getTableName()));
      }
   }

   @Override
   public RestValidationResultBean validateTableName(String name) {
      SearchFilter filter = SearchFilter.instance().match("TABLENAME", name).filter(MatchPattern.SEQ);
      List<LcdpTableBean> tableList = this.selectListByFilter(filter, new Order[0]);
      List<LcdpViewBean> viewList = this.lcdpViewService.selectListByFilter(filter, new Order[0]);
      return tableList.isEmpty()
            && viewList.isEmpty()
            && !this.lcdpDatabaseService.isExistPhysicalTable(name)
            && !this.lcdpDatabaseService.isExistPhysicalView(name)
         ? new RestValidationResultBean(true)
         : new RestValidationResultBean(false, I18nHelper.getMessage("LCDP.MODULE.TABLES.NAME.TIP.EXIST"));
   }

   @Override
   public RestValidationResultBean validateIndexName(String name) {
      int count = this.lcdpDatabaseDao.countByIndexName(name);
      if (count > 0) {
         return new RestValidationResultBean(false, I18nHelper.getMessage("LCDP.MODULE.TABLES.NAME.TIP.EXIST"));
      } else {
         List<LcdpTableIndexBean> indexList = this.lcdpTableIndexService
            .selectListByFilter(SearchFilter.instance().match("INDEXNAME", name).filter(MatchPattern.SEQ), new Order[0]);
         if (!indexList.isEmpty()) {
            List<Long> tableIdList = indexList.stream().map(LcdpTableIndexBean::getTableId).collect(Collectors.toList());
            List<LcdpTableBean> tableList = this.selectListByFilter(
               SearchFilter.instance().match("ID", tableIdList).filter(MatchPattern.OR).match("SUBMITFLAG", "no").filter(MatchPattern.SEQ), new Order[0]
            );
            if (!tableList.isEmpty()) {
               return new RestValidationResultBean(false, I18nHelper.getMessage("LCDP.MODULE.TABLES.NAME.TIP.EXIST"));
            }
         }

         return new RestValidationResultBean(true);
      }
   }

   @Override
   public Map<String, LcdpAnalysisResultDTO> analysisTableInfo(List<String> tableNameList, Map<String, String> dataMap) {
      Map<String, LcdpAnalysisResultDTO> result = new HashMap<>();
      List<LcdpTableDTO> physicalTableList = this.lcdpDatabaseService.selectPhysicalTableInfoList(tableNameList);
      tableNameList.forEach(
         tableName -> {
            String tableJson = dataMap.get(tableName);
            if (!StringUtils.isEmpty(tableJson)) {
               LcdpTableDTO importTable = JsonUtils.parse(dataMap.get(tableName), LcdpTableDTO.class);
               LcdpTableDTO physicalTable = physicalTableList.stream()
                  .filter(currentLcdpTableDTO -> StringUtils.equals(tableName, currentLcdpTableDTO.getTableName()))
                  .findAny()
                  .orElse(null);
               List<LcdpTableFieldBean> analyzedFieldList = LcdpTableUtils.analyzeFieldOps(
                  importTable.getFieldList(), !ObjectUtils.isEmpty(physicalTable) ? physicalTable.getFieldList() : null
               );
               List<LcdpTableIndexBean> analyzedIndexList = LcdpTableUtils.analyzeIndexOps(
                  importTable.getIndexList(), !ObjectUtils.isEmpty(physicalTable) ? physicalTable.getIndexList() : null
               );
               LcdpAnalysisResultDTO analysisResult = LcdpTableUtils.validateTable(physicalTable, importTable, analyzedFieldList, analyzedIndexList);
               if (!LcdpTableUtils.validateDatabaseConsistency(tableName)) {
                  analysisResult.setEnable(false);
                  analysisResult.getAnalysisResultList().add(I18nHelper.getMessage("LCDP.MODULE.TABLES.TABLE.EXCEPTION.DATABASE_NON_CONSISTENCY", tableName));
               }

               result.put(tableName, analysisResult);
            }
         }
      );
      return result;
   }

   @Transactional
   @Override
   public String alterPhysicalTable(
      LcdpTableDTO physicalTable, LcdpTableBean tableOps, List<LcdpTableFieldBean> fieldOpsList, List<LcdpTableIndexBean> indexOpsList
   ) {
      String sql = this.lcdpDatabaseService.alterPhysicalTable(physicalTable, tableOps, fieldOpsList, indexOpsList);
      this.initTableI18n(tableOps.getTableName(), tableOps.getTableDesc(), fieldOpsList);
      this.initAuditConfig(tableOps.getTableName(), tableOps.getTableDesc());
      this.initTableService(tableOps.getTableName(), tableOps.getTableDesc());
      this.initTableHierarchy(tableOps.getTableName(), tableOps.getMasterTableName(), tableOps.getReferColumn());
      return sql;
   }

   @Transactional
   @Override
   public String dropPhysicalTable(LcdpTableBean oldTable) {
      String sql = this.lcdpDatabaseService.dropPhysicalTable(oldTable);
      this.deleteAuditConfig(oldTable.getTableName());
      this.deleteTableService(oldTable.getTableName());
      this.deleteTableHierarchy(oldTable.getTableName());
      return sql;
   }

   @Transactional
   @Override
   public String createPhysicalTable(LcdpTableBean table, List<LcdpTableFieldBean> fieldOpsList, List<LcdpTableIndexBean> indexOpsList) {
      String sql = this.lcdpDatabaseService.createPhysicalTable(table, fieldOpsList, indexOpsList);
      this.initTableI18n(table.getTableName(), table.getTableDesc(), fieldOpsList);
      this.initAuditConfig(table.getTableName(), table.getTableDesc());
      this.initTableService(table.getTableName(), table.getTableDesc());
      this.initTableHierarchy(table.getTableName(), table.getMasterTableName(), table.getReferColumn());
      return sql;
   }

   @Override
   public List<LcdpTableFieldBean> selectPhysicalFieldSelectableList() {
      MapperParameter parameter = new MapperParameter();
      Map<String, String> requestParamMap = ServletUtils.getCurrentRequestParamMap();
      if (!requestParamMap.isEmpty()) {
         SearchFilter filter = SearchFilter.instance();
         requestParamMap.forEach((k, v) -> {
            MatchPattern match = Arrays.stream(MatchPattern.values()).filter(m -> StringUtils.endsWith(k, "_" + m.name())).findAny().orElse(null);
            String key = k;
            if (match == null) {
               match = MatchPattern.EQ;
            } else {
               key = StringUtils.removeEnd(k, "_" + match.name());
               if (MatchPattern.IN.equals(match)) {
                  match = MatchPattern.OR;
               }
            }

            if (StringUtils.contains(v, ",")) {
               filter.match(key, Arrays.asList(StringUtils.split(v, ","))).filter(match);
            } else {
               filter.match(key, v).filter(match);
            }
         });
         parameter.setFilter(filter);
      }

      return this.lcdpDatabaseService.selectPhysicalFieldList(parameter);
   }

   @Transactional
   @Override
   public LcdpTableDTO executeSql(RestJsonWrapperBean wrapper) {
      String sqlText = wrapper.getParamValue("sql");
      String filterKey = " IF\\s+NOT\\s+EXISTS";
      if (!StringUtils.isEmpty(sqlText)) {
         sqlText = sqlText.toUpperCase().replaceFirst(filterKey, "");
         sqlText = sqlText.toUpperCase().replaceFirst("\\s+DBO.", " ");
      }

      String[] sqls = sqlText.split(";");

      try {
         for (String sql : sqls) {
            if (!StringUtils.isBlank(sql)) {
               this.globalMapper.update(sql);
            }
         }

         String tableName = "";
         Pattern pattern = Pattern.compile("CREATE\\s+TABLE\\s+'?\"?`?(\\w+)'?\"?`?");
         Matcher matcher = pattern.matcher(sqlText.toUpperCase());
         if (matcher.find()) {
            tableName = matcher.group().replaceAll(" ", "").replace("CREATETABLE", "");
            tableName = tableName.replaceAll("\"", "").replaceAll("`", "").replaceAll("'", "");
         }

         if (!StringUtils.isBlank(tableName)) {
            LcdpTableDTO tableDTO = this.selectPhysicalTableInfo(tableName);
            this.globalMapper.update("DROP TABLE " + tableName);
            return tableDTO;
         } else {
            return null;
         }
      } catch (Exception var9) {
         SQLException sqlException = ExceptionUtils.getCause(var9, SQLException.class);
         if (ObjectUtils.isEmpty(sqlException)) {
            throw new CheckedException(var9.getMessage(), var9);
         } else {
            String exceptionMessage = this.sqlGenerateHelper.parseSQLException(sqlException.getMessage());
            throw new CheckedException(exceptionMessage, sqlException);
         }
      }
   }

   @Override
   public LcdpDynamicPagination<Map<String, Object>> dynamicQuery(RestJsonWrapperBean wrapper) {
      return this.lcdpDatabaseService.dynamicQuery(wrapper);
   }

   @Override
   public String generateCreateSql(LcdpTableDTO tableDTO) {
      StringBuilder recorder = new StringBuilder();
      LcdpTableIndexBean primaryKey = tableDTO.getIndexList()
         .stream()
         .filter(index -> IndexType.primarykey.name().equalsIgnoreCase(index.getIndexType()))
         .map(index -> {
            LcdpTableIndexBean indexBean = new LcdpTableIndexBean();
            BeanUtils.copyProperties(index, indexBean);
            indexBean.setIndexOperationType("add");
            return indexBean;
         })
         .findAny()
         .orElse(null);
      List<LcdpTableIndexBean> indexOpsList = tableDTO.getIndexList()
         .stream()
         .filter(index -> !IndexType.primarykey.name().equalsIgnoreCase(index.getIndexType()))
         .map(index -> {
            LcdpTableIndexBean indexBean = new LcdpTableIndexBean();
            BeanUtils.copyProperties(index, indexBean);
            indexBean.setIndexOperationType("add");
            return indexBean;
         })
         .collect(Collectors.toList());
      List<LcdpTableFieldBean> fieldOpsList = tableDTO.getFieldList().stream().map(field -> {
         LcdpTableFieldBean fieldBean = new LcdpTableFieldBean();
         BeanUtils.copyProperties(field, fieldBean);
         fieldBean.setFieldOperationType("add");
         return fieldBean;
      }).collect(Collectors.toList());
      LcdpTableBean table = new LcdpTableBean();
      BeanUtils.copyProperties(tableDTO, table);
      List<String> createTableSqlList = this.sqlGenerateHelper.generateCreateTableSql(table, fieldOpsList, primaryKey);
      createTableSqlList.forEach(sql -> recorder.append(sql + ";\r\n"));
      indexOpsList.forEach(indexOps -> recorder.append(this.sqlGenerateHelper.generateIndexSql(table, indexOps) + ";\r\n"));
      return recorder.toString();
   }

   private LcdpTableTreeNodeDTO createTableTreeNodeDTO(String name, String type, String root, String checkoutFlag, String newFlag) {
      LcdpTableTreeNodeDTO tableTreeNodeDTO = new LcdpTableTreeNodeDTO();
      tableTreeNodeDTO.setId(name);
      tableTreeNodeDTO.setName(name);
      tableTreeNodeDTO.setType(type);
      tableTreeNodeDTO.setParentId(root);
      tableTreeNodeDTO.setCheckOutFlag(checkoutFlag);
      tableTreeNodeDTO.setResourceStatus(newFlag);
      return tableTreeNodeDTO;
   }

   private void initTableI18n(String tableName, String tableDesc, List<LcdpTableFieldBean> fieldList) {
      List<CoreI18nBean> tableI18nList = this.coreI18nService
         .selectListByFilter(SearchFilter.instance().match("CODE", tableName + ".").filter(MatchPattern.CISB), new Order[0]);
      List<Long> i18nIdList = tableI18nList.stream().map(CoreI18nBean::getId).collect(Collectors.toList());
      List<CoreI18nMessageBean> tableI18nMessageList = this.coreI18nMessageService
         .selectListByFilter(SearchFilter.instance().match("I18NID", i18nIdList).filter(MatchPattern.OR), new Order[0]);
      List<CoreI18nBean> insertI18nList = new ArrayList<>();
      List<CoreI18nMessageBean> insertI18nMessageList = new ArrayList<>();
      LcdpTableFieldBean tableServiceI18n = new LcdpTableFieldBean();
      tableServiceI18n.setFieldName("SERVICE_NAME");
      tableServiceI18n.setFieldComment(tableDesc);
      fieldList.add(tableServiceI18n);
      fieldList.forEach(
         field -> {
            String i18nCode = LcdpTableUtils.i18nCode(tableName, field.getFieldName());
            String message = field.getFieldComment();
            CoreI18nBean tableI18n = tableI18nList.stream().filter(i18nx -> StringUtils.equals(i18nx.getCode(), i18nCode)).findFirst().orElse(null);
            if (ObjectUtils.isEmpty(tableI18n)) {
               CoreI18nBean i18n = new CoreI18nBean();
               i18n.setId(ApplicationContextHelper.getNextIdentity());
               i18n.setCode(i18nCode);
               i18n.setDefaultMessage(message);
               i18n.setDescription(message);
               CoreI18nMessageBean zh = new CoreI18nMessageBean();
               zh.setId(ApplicationContextHelper.getNextIdentity());
               zh.setI18nConfigId("zh-CN");
               zh.setI18nId(i18n.getId());
               zh.setMessage(message);
               insertI18nList.add(i18n);
               insertI18nMessageList.add(zh);
            } else {
               tableI18n.setDefaultMessage(message);
               tableI18n.setDescription(message);
               tableI18nMessageList.stream()
                  .filter(i18nMessage -> i18nMessage.getI18nId() == tableI18n.getId())
                  .forEach(i18nMessage -> i18nMessage.setMessage(message));
            }
         }
      );
      this.coreI18nService.updateIfChanged(tableI18nList);
      this.coreI18nMessageService.updateIfChanged(tableI18nMessageList);
      this.coreI18nService.getDao().insert(insertI18nList);
      this.coreI18nMessageService.getDao().insert(insertI18nMessageList);
   }

   private void initTableService(String tableName, String tableDesc) {
      TransactionUtils.runAfterCommit(
         () -> {
            List<ColumnContext> tableColumnContextList = DatabaseManager.getTableColumnContextList(tableName);
            boolean processStatusFlag = tableColumnContextList.stream()
               .anyMatch(columnContext -> StringUtils.equalsIgnoreCase(columnContext.getColumnName(), "PROCESSSTATUS"));
            CoreTableBean coreTableBean = this.coreTableService.selectTable(tableName);
            if (ObjectUtils.isEmpty(coreTableBean)) {
               CoreTableBean coreTable = new CoreTableBean();
               coreTable.setId(tableName);
               coreTable.setAuditable(processStatusFlag ? "1" : "0");
               coreTable.setAttachment("0");
               this.coreTableService.getDao().insert(coreTable);
            } else {
               coreTableBean.setAuditable(processStatusFlag ? "1" : "0");
               this.coreTableService.update(coreTableBean);
            }

            List<CoreMenuExtBean> menuExtList = this.coreMenuExtService
               .selectListByFilter(SearchFilter.instance().match("TABLENAME", tableName).filter(MatchPattern.SEQ), new Order[0]);
            if (processStatusFlag) {
               if (!menuExtList.isEmpty()) {
                  menuExtList.forEach(service -> service.setServiceName(tableDesc));
                  this.coreMenuExtService.updateIfChanged(menuExtList);
                  return;
               }

               CoreMenuExtBean menuExtBean = new CoreMenuExtBean();
               menuExtBean.setId(ApplicationContextHelper.getNextIdentity().toString());
               menuExtBean.setServiceName(tableDesc);
               menuExtBean.setTableName(tableName);
               menuExtBean.setEnable("1");
               menuExtBean.setType("audit_def");
               this.coreMenuExtService.getDao().insert(menuExtBean);
            } else if (!menuExtList.isEmpty()) {
               this.coreMenuExtService.getDao().deleteBy(menuExtList);
            }
         }
      );
   }

   private void initTableHierarchy(String tableName, String masterTableName, String referColumn) {
      CoreTableHierarchyBean newTableHirarchy = new CoreTableHierarchyBean();
      newTableHirarchy.setTableName(tableName);
      CoreTableHierarchyBean tableHierarchy = this.coreTableHierarchyDao.selectFirstIfPresent(newTableHirarchy, new Order[0]);
      if (!StringUtils.isEmpty(masterTableName) && !StringUtils.isEmpty(referColumn)) {
         RedisHelper.evict("T_CORE_TABLE_HIERARCHY.MASTERTABLE", masterTableName);
         RedisHelper.evict("T_CORE_TABLE_HIERARCHY.TABLENAME", tableName);
         if (ObjectUtils.isEmpty(tableHierarchy)) {
            newTableHirarchy.setId(ApplicationContextHelper.getNextIdentity());
            newTableHirarchy.setReferColumn(referColumn);
            newTableHirarchy.setMasterTableName(masterTableName);
            this.coreTableHierarchyDao.insert(newTableHirarchy);
         } else {
            tableHierarchy.setReferColumn(referColumn);
            tableHierarchy.setMasterTableName(masterTableName);
            this.coreTableHierarchyDao.updateIfChanged(tableHierarchy);
         }
      } else if (!ObjectUtils.isEmpty(tableHierarchy)) {
         this.deleteTableHierarchy(tableName);
      }
   }

   private void deleteTableHierarchy(String tableName) {
      CoreTableHierarchyBean filter = new CoreTableHierarchyBean();
      filter.setTableName(tableName);
      RedisHelper.evict("T_CORE_TABLE_HIERARCHY.TABLENAME", tableName);
      this.coreTableHierarchyDao.deleteBy(filter, new String[]{"TABLENAME"});
   }

   private void deleteTableService(String tableName) {
      CoreMenuExtBean condition = new CoreMenuExtBean();
      condition.setTableName(tableName);
      this.coreMenuExtService.getDao().deleteBy(condition, "TABLENAME");
   }

   private void initAuditConfig(String tableName, String tableDesc) {
      TransactionUtils.runAfterCommit(
         () -> {
            CoreAuditConfigBean coreAuditConfigBean = this.coreAuditConfigService.selectDetail(tableName);
            if (ObjectUtils.isEmpty(coreAuditConfigBean)) {
               CoreAuditConfigBean config = new CoreAuditConfigBean();
               config.setId(tableName);
               if (StringUtils.startsWith(tableName, "T_CORE_")) {
                  config.setCategory("system");
               } else {
                  config.setCategory("service");
               }

               config.setName(tableDesc);
               config.setAuditable(this.auditable ? "1" : "0");
               this.coreAuditConfigService.getDao().insert(config);
            }

            List<CoreAuditConfigColumnBean> configColumnList = this.coreAuditConfigColumnService.selectListByConfigId(tableName);
            List<ColumnContext> columnList = DatabaseManager.getTableColumnContextList(tableName);
            List<Long> deleteConfigColumnIdList = configColumnList.stream()
               .filter(cc -> columnList.stream().noneMatch(c -> c.getColumnName().equalsIgnoreCase(cc.getColumn())))
               .map(CoreAuditConfigColumnBean::getId)
               .collect(Collectors.toList());
            List<CoreAuditConfigColumnBean> insertConfigColumnList = columnList.stream()
               .filter(c -> configColumnList.stream().noneMatch(cc -> cc.getColumn().equalsIgnoreCase(c.getColumnName())))
               .map(c -> {
                  CoreAuditConfigColumnBean column = new CoreAuditConfigColumnBean();
                  column.setId(ApplicationContextHelper.getNextIdentity());
                  column.setConfigId(tableName);
                  column.setColumn(c.getColumnName().toUpperCase());
                  column.setAuditable(this.auditable ? "1" : "0");
                  return column;
               })
               .collect(Collectors.toList());
            this.coreAuditConfigColumnService.getDao().deleteByIdList(deleteConfigColumnIdList);
            this.coreAuditConfigColumnService.getDao().insert(insertConfigColumnList);
         }
      );
   }

   private void deleteAuditConfig(String tableName) {
      this.coreAuditConfigService.getDao().delete(tableName);
      List<CoreAuditConfigColumnBean> coreAuditConfigColumnBeans = this.coreAuditConfigColumnService.selectListByConfigId(tableName);
      this.coreAuditConfigColumnService.getDao().deleteBy(coreAuditConfigColumnBeans);
   }
}
