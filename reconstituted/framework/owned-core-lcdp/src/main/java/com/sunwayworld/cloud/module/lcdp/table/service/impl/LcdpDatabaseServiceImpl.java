package com.sunwayworld.cloud.module.lcdp.table.service.impl;

import com.sunwayworld.cloud.module.lcdp.base.persistent.dao.LcdpBaseDao;
import com.sunwayworld.cloud.module.lcdp.support.constant.LcdpConstant;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpDynamicPagination;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableFieldBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableFieldDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableIndexBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableIndexDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableViewDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpViewBean;
import com.sunwayworld.cloud.module.lcdp.table.helper.IndexType;
import com.sunwayworld.cloud.module.lcdp.table.helper.SqlGenerateHelper;
import com.sunwayworld.cloud.module.lcdp.table.persistent.dao.LcdpDatabaseDao;
import com.sunwayworld.cloud.module.lcdp.table.service.LcdpDatabaseService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.context.ColumnContext;
import com.sunwayworld.framework.database.context.TableContext;
import com.sunwayworld.framework.database.context.instance.TableContextInstance;
import com.sunwayworld.framework.database.core.DatabaseManager;
import com.sunwayworld.framework.database.dialect.Dialect;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.mybatis.mapper.MapDaoMapperWrapper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ExceptionUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.metadata.bean.CoreTableHierarchyBean;
import com.sunwayworld.module.sys.metadata.dao.CoreTableHierarchyDao;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@GikamBean
public class LcdpDatabaseServiceImpl implements LcdpDatabaseService {
   @Autowired
   private LcdpDatabaseDao lcdpDatabaseDao;
   @Autowired
   private LcdpBaseDao lcdpBaseDao;
   @Autowired
   private Dialect dialect;
   @Autowired
   private CoreTableHierarchyDao coreTableHierarchyDao;
   @Autowired
   private SqlGenerateHelper sqlGenerateHelper;
   @Autowired
   private MapDaoMapperWrapper mapDaoMapperWrapper;
   @Autowired
   private GlobalMapper globalMapper;

   @Override
   public List<LcdpViewBean> selectPhysicalViewInfoList(MapperParameter parameter) {
      List<Map<String, Object>> mapList = this.lcdpDatabaseDao.selectViewInfoListByCondition(parameter);
      return mapList.isEmpty()
         ? CollectionUtils.emptyList()
         : mapList.stream().map(m -> PersistableHelper.mapToPersistable((Map<String, Object>)m, LcdpViewBean.class)).collect(Collectors.toList());
   }

   @Override
   public LcdpViewBean selectPhysicalViewInfo(String viewName) {
      List<LcdpViewBean> viewList = this.selectPhysicalViewInfoList(ArrayUtils.asList(viewName));
      return viewList.isEmpty() ? new LcdpViewBean() : viewList.get(0);
   }

   @Override
   public List<LcdpViewBean> selectPhysicalViewInfoList(List<String> viewNameList) {
      MapperParameter parameter = new MapperParameter();
      parameter.setFilter(SearchFilter.instance().match("VIEWNAME", viewNameList).filter(MatchPattern.OCISEQ));
      List<Map<String, Object>> viewList = this.lcdpDatabaseDao.selectViewInfoListByCondition(parameter);
      return viewList.stream().map(table -> PersistableHelper.mapToPersistable((Map<String, Object>)table, LcdpViewBean.class)).collect(Collectors.toList());
   }

   @Override
   public LcdpTableDTO selectPhysicalTableInfo(String tableName) {
      List<LcdpTableDTO> lcdpTable = this.selectPhysicalTableInfoList(ArrayUtils.asList(tableName));
      if (lcdpTable.isEmpty()) {
         return new LcdpTableDTO();
      } else {
         LcdpTableDTO tableDTO = lcdpTable.get(0);
         List<LcdpTableFieldDTO> fieldList = tableDTO.getFieldList();
         List<LcdpTableFieldDTO> createFieldList = fieldList.stream()
            .filter(field -> LcdpConstant.CREATE_COLUMN_LIST.contains(field.getFieldName().toUpperCase()))
            .collect(Collectors.toList());
         fieldList.removeAll(createFieldList);
         fieldList.addAll(createFieldList);
         tableDTO.setFieldList(fieldList);
         return tableDTO;
      }
   }

   @Override
   public List<LcdpTableDTO> selectPhysicalTableInfoList(List<String> tableNameList) {
      MapperParameter parameter = new MapperParameter();
      parameter.setFilter(SearchFilter.instance().match("TABLENAME", tableNameList).filter(MatchPattern.OCISEQ));
      List<Map<String, Object>> tableList = this.lcdpDatabaseDao.selectTableInfoByCondition(parameter);
      List<LcdpTableDTO> tableDTOList = tableList.stream()
         .map(table -> PersistableHelper.mapToPersistable((Map<String, Object>)table, LcdpTableDTO.class))
         .collect(Collectors.toList());
      if (tableList.isEmpty()) {
         return tableDTOList;
      } else {
         List<CoreTableHierarchyBean> filterList = tableNameList.stream().map(tableName -> {
            CoreTableHierarchyBean filter = new CoreTableHierarchyBean();
            filter.setTableName(tableName);
            return filter;
         }).collect(Collectors.toList());
         List<CoreTableHierarchyBean> tableHierarchyBeanList = this.coreTableHierarchyDao
            .selectList(filterList, ArrayUtils.asList("TABLENAME"), ArrayUtils.asList("TABLENAME", "MASTERTABLENAME", "REFERCOLUMN"), new Order[0]);
         Map<String, List<CoreTableHierarchyBean>> tableHierarchyMapByTableName = tableHierarchyBeanList.stream()
            .collect(Collectors.groupingBy(CoreTableHierarchyBean::getTableName));
         List<LcdpTableIndexDTO> indexList = this.lcdpDatabaseDao
            .selectIndexListByCondition(parameter)
            .stream()
            .map(index -> PersistableHelper.mapToPersistable((Map<String, Object>)index, LcdpTableIndexDTO.class))
            .collect(Collectors.toList());
         Map<String, List<LcdpTableIndexDTO>> indexMapByTableName = indexList.stream().collect(Collectors.groupingBy(LcdpTableIndexDTO::getTableName));
         List<LcdpTableFieldDTO> fieldList = this.lcdpDatabaseDao
            .selectFieldListByCondition(parameter)
            .stream()
            .map(field -> PersistableHelper.mapToPersistable((Map<String, Object>)field, LcdpTableFieldDTO.class))
            .map(field -> {
               field.setFieldType(this.sqlGenerateHelper.databaseTypeToFieldType(field.getFieldType()));
               return (LcdpTableFieldDTO)field;
            })
            .collect(Collectors.toList());
         Map<String, List<LcdpTableFieldDTO>> fieldMapByTableName = fieldList.stream().collect(Collectors.groupingBy(LcdpTableFieldDTO::getTableName));

         for (LcdpTableDTO tableDTO : tableDTOList) {
            List<CoreTableHierarchyBean> tableHierarchy = tableHierarchyMapByTableName.get(tableDTO.getTableName());
            if (!ObjectUtils.isEmpty(tableHierarchy) && !tableHierarchy.isEmpty()) {
               tableDTO.setMasterTableName(tableHierarchy.get(0).getMasterTableName());
               tableDTO.setReferColumn(tableHierarchy.get(0).getReferColumn());
            }

            tableDTO.setFieldList(fieldMapByTableName.get(tableDTO.getTableName()));
            tableDTO.setIndexList(indexMapByTableName.get(tableDTO.getTableName()));
         }

         return tableDTOList;
      }
   }

   @Override
   public List<LcdpTableBean> selectPhysicalTableNameList(MapperParameter parameter) {
      List<Map<String, Object>> mapList = this.lcdpDatabaseDao.selectTableNameListByCondition(parameter);
      return mapList.isEmpty()
         ? CollectionUtils.emptyList()
         : mapList.stream().map(m -> PersistableHelper.mapToPersistable((Map<String, Object>)m, LcdpTableBean.class)).collect(Collectors.toList());
   }

   @Override
   public List<LcdpTableBean> selectPhysicalTableInfoList(MapperParameter parameter) {
      List<Map<String, Object>> mapList = this.lcdpDatabaseDao.selectTableInfoByCondition(parameter);
      return mapList.isEmpty()
         ? CollectionUtils.emptyList()
         : mapList.stream().map(m -> PersistableHelper.mapToPersistable((Map<String, Object>)m, LcdpTableBean.class)).collect(Collectors.toList());
   }

   @Override
   public List<LcdpTableViewDTO> selectPhysicalTableOrViewNameList(MapperParameter parameter) {
      List<Map<String, Object>> mapList = this.lcdpDatabaseDao.selectTableOrViewNameListByCondition(parameter);
      return mapList.isEmpty()
         ? CollectionUtils.emptyList()
         : mapList.stream().map(m -> PersistableHelper.mapToPersistable((Map<String, Object>)m, LcdpTableViewDTO.class)).collect(Collectors.toList());
   }

   @Override
   public List<LcdpTableFieldBean> selectPhysicalFieldList(MapperParameter parameter) {
      return this.lcdpDatabaseDao
         .selectFieldListByCondition(parameter)
         .stream()
         .map(field -> PersistableHelper.mapToPersistable((Map<String, Object>)field, LcdpTableFieldBean.class))
         .map(field -> {
            field.setFieldType(this.sqlGenerateHelper.databaseTypeToFieldType(field.getFieldType()));
            return (LcdpTableFieldBean)field;
         })
         .collect(Collectors.toList());
   }

   @Override
   public List<LcdpTableFieldBean> selectPhysicalFieldList(String tableName) {
      MapperParameter parameter = new MapperParameter();
      parameter.setFilter(SearchFilter.instance().match("TABLENAME", tableName).filter(MatchPattern.SEQ));
      return this.selectPhysicalFieldList(parameter);
   }

   @Override
   public List<LcdpTableIndexBean> selectPhysicalIndexList(String tableName) {
      MapperParameter parameter = new MapperParameter();
      parameter.setFilter(SearchFilter.instance().match("TABLENAME", tableName).filter(MatchPattern.SEQ));
      return this.lcdpDatabaseDao
         .selectIndexListByCondition(parameter)
         .stream()
         .map(index -> PersistableHelper.mapToPersistable((Map<String, Object>)index, LcdpTableIndexBean.class))
         .collect(Collectors.toList());
   }

   @Override
   public boolean isExistData(String tableName) {
      int countRow = this.mapDaoMapperWrapper.countByInstance(TableContextInstance.of(TableContext.of(tableName), new HashMap()));
      return countRow > 0;
   }

   @Override
   public boolean isExistPhysicalTable(String tableName) {
      int count = this.lcdpDatabaseDao.countByTableName(tableName);
      return count > 0;
   }

   @Override
   public boolean isExistPhysicalView(String viewName) {
      int count = this.lcdpDatabaseDao.countByViewName(viewName);
      return count > 0;
   }

   @Override
   public boolean isExistNullDataInColumn(String tableName, String columnName) {
      Map<String, Object> item = new HashMap<>();
      item.put(columnName, null);
      int countRow = this.mapDaoMapperWrapper.countByInstance(TableContextInstance.of(TableContext.of(tableName), item));
      return countRow > 0;
   }

   @Override
   public boolean isExistNotNullDataInColumn(String tableName, String columnName) {
      Map<String, Object> item = new HashMap<>();
      int totalCountRow = this.mapDaoMapperWrapper.countByInstance(TableContextInstance.of(TableContext.of(tableName), item));
      item.put(columnName, null);
      int countRow = this.mapDaoMapperWrapper.countByInstance(TableContextInstance.of(TableContext.of(tableName), item));
      return totalCountRow != countRow;
   }

   @Transactional
   @Override
   public String alterPhysicalTable(
      LcdpTableDTO physicalTable, LcdpTableBean tableOps, List<LcdpTableFieldBean> fieldOpeList, List<LcdpTableIndexBean> indexOpsList
   ) {
      StringBuilder recorder = new StringBuilder();
      recorder.append(StringUtils.isEmpty(tableOps.getSql()) ? "" : tableOps.getSql());
      if (!StringUtils.equals(physicalTable.getTableDesc(), tableOps.getTableDesc())) {
         this.execAndRecordSql(recorder, this.sqlGenerateHelper.generateTableCommentSql(tableOps, physicalTable));
      }

      Optional.of(indexOpsList)
         .orElse(Collections.emptyList())
         .stream()
         .filter(index -> "delete".equalsIgnoreCase(index.getIndexOperationType()))
         .forEach(index -> this.execAndRecordSql(recorder, this.sqlGenerateHelper.generateIndexSql(tableOps, index)));
      Optional.of(fieldOpeList)
         .orElse(Collections.emptyList())
         .stream()
         .filter(field -> "delete".equalsIgnoreCase(field.getFieldOperationType()))
         .forEach(
            field -> {
               LcdpTableFieldBean oldField = physicalTable.getFieldList()
                  .stream()
                  .filter(fieldBean -> fieldBean.getFieldName().equals(field.getFieldName()))
                  .map(fieldBean -> {
                     LcdpTableFieldBean fieldOps = new LcdpTableFieldBean();
                     BeanUtils.copyProperties(fieldBean, fieldOps);
                     return fieldOps;
                  })
                  .findFirst()
                  .orElse(new LcdpTableFieldBean());
               List<String> sqls = this.sqlGenerateHelper.generateColumnSql(tableOps, oldField, field);
               sqls.forEach(sql -> this.execAndRecordSql(recorder, sql));
            }
         );
      Optional.of(fieldOpeList)
         .orElse(Collections.emptyList())
         .stream()
         .filter(field -> "update".equalsIgnoreCase(field.getFieldOperationType()))
         .forEach(
            field -> {
               LcdpTableFieldBean physicalField = physicalTable.getFieldList()
                  .stream()
                  .filter(fieldBean -> fieldBean.getFieldName().equals(field.getFieldName()))
                  .map(fieldBean -> {
                     LcdpTableFieldBean physicalFieldCopy = new LcdpTableFieldBean();
                     BeanUtils.copyProperties(fieldBean, physicalFieldCopy);
                     return physicalFieldCopy;
                  })
                  .findFirst()
                  .orElse(new LcdpTableFieldBean());
               List<String> sqls = this.sqlGenerateHelper.generateColumnSql(tableOps, physicalField, field);
               sqls.forEach(sql -> this.execAndRecordSql(recorder, sql));
            }
         );
      Optional.of(fieldOpeList)
         .orElse(Collections.emptyList())
         .stream()
         .filter(field -> "add".equalsIgnoreCase(field.getFieldOperationType()))
         .forEach(field -> {
            List<String> sqls = this.sqlGenerateHelper.generateColumnSql(tableOps, null, field);
            sqls.forEach(sql -> this.execAndRecordSql(recorder, sql));
         });
      Optional.of(indexOpsList)
         .orElse(Collections.emptyList())
         .stream()
         .filter(index -> "add".equalsIgnoreCase(index.getIndexOperationType()))
         .forEach(index -> this.execAndRecordSql(recorder, this.sqlGenerateHelper.generateIndexSql(tableOps, index)));
      return recorder.toString();
   }

   @Transactional
   @Override
   public String dropPhysicalTable(LcdpTableBean oldTable) {
      StringBuilder recorder = new StringBuilder();
      this.execAndRecordSql(recorder, this.sqlGenerateHelper.generateDeleteSql(oldTable));
      return recorder.toString();
   }

   @Transactional
   @Override
   public String createPhysicalTable(LcdpTableBean table, List<LcdpTableFieldBean> fieldOpsList, List<LcdpTableIndexBean> indexOpsList) {
      StringBuilder recorder = new StringBuilder();
      LcdpTableIndexBean primaryKey = indexOpsList.stream()
         .filter(index -> IndexType.primarykey.name().equalsIgnoreCase(index.getIndexType()))
         .findAny()
         .orElse(null);
      List<String> createTableSqls = this.sqlGenerateHelper.generateCreateTableSql(table, fieldOpsList, primaryKey);
      createTableSqls.forEach(sql -> this.execAndRecordSql(recorder, sql));
      indexOpsList.stream().filter(index -> !IndexType.primarykey.name().equalsIgnoreCase(index.getIndexType())).forEach(index -> {
         String sql = this.sqlGenerateHelper.generateIndexSql(table, index);
         this.execAndRecordSql(recorder, sql);
      });
      return recorder.toString();
   }

   @Transactional
   @Override
   public String createPhysicalView(LcdpViewBean view) {
      StringBuilder recorder = new StringBuilder();
      this.execAndRecordSql(recorder, this.sqlGenerateHelper.generateCreateViewSql(view));
      return recorder.toString();
   }

   @Transactional
   @Override
   public String alterPhysicalView(LcdpViewBean view) {
      StringBuilder recorder = new StringBuilder();
      this.execAndRecordSql(recorder, this.sqlGenerateHelper.generateAlterViewSql(view));
      return recorder.toString();
   }

   @Transactional
   @Override
   public String dropPhysicalView(LcdpViewBean view) {
      StringBuilder recorder = new StringBuilder();
      this.execAndRecordSql(recorder, this.sqlGenerateHelper.generateDropViewSql(view));
      return recorder.toString();
   }

   @Override
   public void testSelectStatement(String selectStatement) {
      try {
         this.globalMapper.selectFirstString(selectStatement);
      } catch (Exception var5) {
         SQLException sqlException = ExceptionUtils.getCause(var5, SQLException.class);
         if (ObjectUtils.isEmpty(sqlException)) {
            throw new CheckedException(var5.getMessage(), var5);
         } else {
            String exceptionMessage = this.sqlGenerateHelper.parseSQLException(sqlException.getMessage());
            throw new CheckedException(exceptionMessage, sqlException);
         }
      }
   }

   @Override
   public LcdpDynamicPagination<Map<String, Object>> dynamicQuery(RestJsonWrapperBean jsonWrapperBean) {
      String name = jsonWrapperBean.getFilterValue("name");
      String type = jsonWrapperBean.getFilterValue("type");

      try {
         Map<String, String> fieldDescMapping = new HashMap<>();
         if (!StringUtils.isEmpty(name)) {
            jsonWrapperBean.setFilterValue("sql", this.dialect.getSelectFromTableSql(name));
         }

         if (StringUtils.equals(type, "table")) {
            fieldDescMapping.putAll(
               this.selectPhysicalFieldList(name).stream().collect(Collectors.toMap(LcdpTableFieldBean::getFieldName, LcdpTableFieldBean::getFieldComment))
            );
         }

         MapperParameter mapperParameter = jsonWrapperBean.extractMapFilter();
         List<ColumnContext> columnContextList = DatabaseManager.selectColumnContextList(jsonWrapperBean.getFilterValue("sql"));
         if (ObjectUtils.isEmpty(mapperParameter.get("orderParams"))) {
            if (columnContextList.stream().noneMatch(columnContext -> StringUtils.equalsIgnoreCase(columnContext.getColumnName(), "ID"))) {
               Order order = Order.asc(columnContextList.stream().findFirst().get().getColumnName());
               mapperParameter.setOrderParam(order.getColumn(), order.getDirection());
            } else {
               Order order = Order.asc("ID");
               mapperParameter.setOrderParam(order.getColumn(), order.getDirection());
            }
         }

         Page<Map<String, Object>> mapPage = MybatisPageHelper.get(
            jsonWrapperBean.extractPageRowBounds(), () -> this.lcdpBaseDao.selectByCondition(mapperParameter)
         );
         Map<String, String> columnTypeMap = mapPage.getColumnTypeMap();
         columnTypeMap.remove("rn_");
         Map<String, String> finalColumnTypeMap = new LinkedHashMap<>();
         Map<String, String> columnDescMap = new LinkedHashMap<>();
         columnContextList.forEach(columnContext -> columnTypeMap.forEach((key, value) -> {
            String fieldComment = fieldDescMapping.get(key.toUpperCase());
            if (StringUtils.equalsIgnoreCase(columnContext.getColumnName(), key)) {
               if (!StringUtils.isEmpty(fieldComment)) {
                  columnDescMap.put(columnContext.getRawColumnName(), fieldComment);
               }

               if (this.dialect.isClob(columnContext)) {
                  finalColumnTypeMap.put(columnContext.getRawColumnName(), "textarea");
               } else {
                  finalColumnTypeMap.put(columnContext.getRawColumnName(), value);
               }
            }
         }));
         LcdpDynamicPagination<Map<String, Object>> lcdpDynamicPagination = new LcdpDynamicPagination<>(
            mapPage.getTotal(), mapPage.getPageSize(), mapPage.getPageNumber(), mapPage.getRows(), mapPage.getTotalMap()
         );
         lcdpDynamicPagination.setColumnTypeMap(finalColumnTypeMap);
         lcdpDynamicPagination.setColumnDescMap(columnDescMap);
         return lcdpDynamicPagination;
      } catch (Exception var12) {
         SQLException sqlException = ExceptionUtils.getCause(var12, SQLException.class);
         if (ObjectUtils.isEmpty(sqlException)) {
            throw new CheckedException(var12.getMessage(), var12);
         } else {
            String exceptionMessage = this.sqlGenerateHelper.parseSQLException(sqlException.getMessage());
            throw new CheckedException(exceptionMessage, sqlException);
         }
      }
   }

   private void execAndRecordSql(StringBuilder recorder, String sql) {
      try {
         this.globalMapper.update(sql);
      } catch (Exception var6) {
         SQLException sqlException = ExceptionUtils.getCause(var6, SQLException.class);
         if (ObjectUtils.isEmpty(sqlException)) {
            throw new CheckedException(var6.getMessage(), var6);
         }

         String exceptionMessage = this.sqlGenerateHelper.parseSQLException(sqlException.getMessage());
         throw new CheckedException(exceptionMessage, sqlException);
      }

      recorder.append(sql).append(";\r\n");
   }
}
