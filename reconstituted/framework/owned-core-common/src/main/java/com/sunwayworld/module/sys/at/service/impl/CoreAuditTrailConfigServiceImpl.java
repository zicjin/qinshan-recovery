package com.sunwayworld.module.sys.at.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.context.ColumnContext;
import com.sunwayworld.framework.database.core.DatabaseManager;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.support.tree.TreeDescriptor;
import com.sunwayworld.framework.support.tree.TreeHelper;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailConfigBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailConfigLineBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailConfigTreeDTO;
import com.sunwayworld.module.sys.at.dao.CoreAuditTrailConfigDao;
import com.sunwayworld.module.sys.at.service.CoreAuditTrailConfigLineService;
import com.sunwayworld.module.sys.at.service.CoreAuditTrailConfigService;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreAuditTrailConfigServiceImpl implements CoreAuditTrailConfigService {
   @Autowired
   private CoreAuditTrailConfigDao configDao;
   @Autowired
   private CoreAuditTrailConfigLineService configLineService;
   @Autowired
   @Lazy
   private CoreAuditTrailConfigService proxyInstance;

   public CoreAuditTrailConfigDao getDao() {
      return this.configDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void insert(String table) {
      CoreAuditTrailConfigBean config = new CoreAuditTrailConfigBean();
      config.setId(table);
      config.setAuditTrail("0");
      this.getDao().insert(config);
      List<ColumnContext> columnList = DatabaseManager.getTableColumnContextList(table);
      this.configLineService.getDao().insert(columnList.stream().map(c -> {
         CoreAuditTrailConfigLineBean configLine = new CoreAuditTrailConfigLineBean();
         configLine.setId(ApplicationContextHelper.getNextIdentity());
         configLine.setConfigId(table);
         configLine.setColumnId(c.getColumnName());
         configLine.setAuditTrail("1");
         return configLine;
      }).collect(Collectors.toList()));
   }

   public CoreAuditTrailConfigBean selectByIdIfPresent(String table) {
      CoreAuditTrailConfigBean config = this.getDao().selectByIdIfPresent(table);
      if (config == null) {
         config = new CoreAuditTrailConfigBean();
         config.setId(table);
         config.setAuditTrail("0");
      }

      return config;
   }

   public CoreAuditTrailConfigBean selectDetail(String table) {
      Map<String, Object> map = this.getDao().selectDetail(table);
      if (map == null || map.isEmpty()) {
         this.proxyInstance.insert(table);
         map = this.getDao().selectDetail(table);
      }

      return PersistableHelper.mapToPersistable(map, this.getDao().getType());
   }

   @Override
   public Page<CoreAuditTrailConfigTreeDTO> selectAuditTrailableTreePagination(RestJsonWrapperBean wrapper) {
      List<CoreAuditTrailConfigBean> configList = this.getDao().selectAuditTrailableByCondition(wrapper.extractMapFilter());
      TreeDescriptor<CoreAuditTrailConfigBean> descriptor = new TreeDescriptor<>("id");
      descriptor.setParseTreeNodeParentIdFunc(t -> t.getExt$().get("parentid"));
      descriptor.setParseTreeNodeTextFunction(t -> t.getId() + ".SERVICE_NAME");
      descriptor.setOrderComparator((u1, u2) -> u1.getId().compareTo(u2.getId()));
      descriptor.addConsumer((t, n) -> n.setExpanded(true));
      List<CoreAuditTrailConfigTreeDTO> treeNodeList = TreeHelper.parseTreeNode(configList, descriptor, CoreAuditTrailConfigTreeDTO.class);
      treeNodeList.forEach(n -> n.setHref("/core/module/sys/at-configs/audit-trailable/tree-grid"));
      return new Pagination<>(treeNodeList);
   }

   @Override
   public Page<CoreAuditTrailConfigLineBean> selectAuditTrailConfigLineListByConfig(String id, RestJsonWrapperBean wrapper) {
      this.revisingAuditTrailConfigLine(id);
      return new Pagination<>(this.configLineService.selectListByFilter(SearchFilter.instance().match("CONFIGID", id).filter(MatchPattern.EQ), wrapper));
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.ACTIVATE)
   @Audit("AUDIT.ACTIVATE")
   @Override
   public void auditConfigLines(String id, RestJsonWrapperBean wrapper) {
      this.processConfigLines(wrapper, true);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DEACTIVATE)
   @Audit("AUDIT.DEACTIVATE")
   @Override
   public void cancelConfigLines(String id, RestJsonWrapperBean wrapper) {
      this.processConfigLines(wrapper, false);
   }

   @Cacheable(
      value = {"T_CORE_AT_CONFIG_LINE.BY_MASTER"},
      key = "#table",
      unless = "#result == null"
   )
   @Override
   public List<CoreAuditTrailConfigLineBean> selectAuditTrailableLineList(String table) {
      return this.configLineService
         .selectListByFilter(
            SearchFilter.instance().match("CONFIGID", table).filter(MatchPattern.EQ).match("AUDITTRAIL", "1").filter(MatchPattern.EQ), new Order[0]
         );
   }

   @Transactional
   @Override
   public void revisingAuditTrailConfigLine(String id) {
      CoreAuditTrailConfigLineBean filter = new CoreAuditTrailConfigLineBean();
      filter.setConfigId(id);
      List<String> selectedColumnList = this.configLineService.getDao().selectColumnList(filter, "COLUMNID", String.class);
      List<String> columnList = DatabaseManager.getTableColumnContextList(id).stream().map(c -> c.getColumnName()).collect(Collectors.toList());
      List<CoreAuditTrailConfigLineBean> deleteList = selectedColumnList.stream().filter(c -> !CollectionUtils.containsIgnoreCase(columnList, c)).map(c -> {
         CoreAuditTrailConfigLineBean configLine = new CoreAuditTrailConfigLineBean();
         configLine.setColumnId(c);
         return configLine;
      }).collect(Collectors.toList());
      this.configLineService.getDao().deleteBy(deleteList, "COLUMNID");
      List<CoreAuditTrailConfigLineBean> insertList = columnList.stream().filter(c -> !CollectionUtils.containsIgnoreCase(selectedColumnList, c)).map(c -> {
         CoreAuditTrailConfigLineBean configLine = new CoreAuditTrailConfigLineBean();
         configLine.setId(ApplicationContextHelper.getNextIdentity());
         configLine.setConfigId(id);
         configLine.setColumnId(c);
         configLine.setAuditTrail("1");
         return configLine;
      }).collect(Collectors.toList());
      this.configLineService.getDao().insert(insertList);
   }

   @Override
   public boolean auditTrailable(String table) {
      CoreAuditTrailConfigBean config = this.selectByIdIfPresent(table);
      return config != null && "1".equals(config.getAuditTrail());
   }

   @Override
   public Page<CoreAuditTrailConfigBean> selectPagination(RestJsonWrapperBean wrapper) {
      Page<CoreAuditTrailConfigBean> coreAuditTrailConfigBeanPage = CoreAuditTrailConfigService.super.selectPagination(wrapper);
      List<CoreAuditTrailConfigBean> rows = coreAuditTrailConfigBeanPage.getRows();
      List<String> tableNames = rows.stream().map(CoreAuditTrailConfigBean::getId).collect(Collectors.toList());
      Map<String, String> tableDescMap = tableNames.stream()
         .collect(Collectors.toMap(Function.identity(), tableName -> tableName + ".SERVICE_NAME", (a, b) -> a));

      for (CoreAuditTrailConfigBean row : rows) {
         String id = row.getId();
         row.setExt$Item("tabledesc", tableDescMap.get(id));
      }

      return coreAuditTrailConfigBeanPage;
   }

   private void processConfigLines(RestJsonWrapperBean wrapper, boolean audit) {
      List<CoreAuditTrailConfigLineBean> itemList = wrapper.parse(CoreAuditTrailConfigLineBean.class);
      if (!itemList.isEmpty()) {
         List<CoreAuditTrailConfigLineBean> proxyItemList = itemList.stream().map(i -> {
            CoreAuditTrailConfigLineBean proxy = BeanUtils.getPropertyListenerProxy(CoreAuditTrailConfigLineBean.class);
            proxy.setId(i.getId());
            proxy.setAuditTrail(audit ? "1" : "0");
            return proxy;
         }).collect(Collectors.toList());
         this.configLineService.getDao().update(proxyItemList);
      }
   }
}
