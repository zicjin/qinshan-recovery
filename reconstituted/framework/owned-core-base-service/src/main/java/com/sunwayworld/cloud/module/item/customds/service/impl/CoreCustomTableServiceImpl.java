package com.sunwayworld.cloud.module.item.customds.service.impl;

import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomColumnDTO;
import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomTableDTO;
import com.sunwayworld.cloud.module.item.customds.persistent.dao.CoreCustomTableDao;
import com.sunwayworld.cloud.module.item.customds.service.CoreCustomTableService;
import com.sunwayworld.framework.database.context.ColumnContext;
import com.sunwayworld.framework.database.core.DatabaseManager;
import com.sunwayworld.framework.database.dialect.DialectRepository;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCustomTableServiceImpl implements CoreCustomTableService {
   private static Logger logger = LogManager.getLogger(CoreCustomTableServiceImpl.class);
   @Autowired
   private CoreCustomTableDao customTableDao;
   @Autowired
   @Lazy
   private CoreCustomTableService proxyInstance;
   @Autowired
   @Lazy
   private GlobalMapper globalMapper;

   @Override
   public List<CoreCustomTableDTO> selectAllList(RestJsonWrapperBean wrapper) {
      List<Map<String, Object>> allTableMapList = this.customTableDao.selectAllTable(wrapper.extractMapFilter());
      List<CoreCustomTableDTO> tableDtoList = allTableMapList.parallelStream()
         .map(m -> PersistableHelper.mapToPersistable((Map<String, Object>)m, CoreCustomTableDTO.class))
         .collect(Collectors.toList());
      tableDtoList.forEach(
         table -> {
            List<Map<String, Object>> columnMapList = this.customTableDao.selectColumnByTableName(table.getId());
            List<CoreCustomColumnDTO> columnDtoList = null;
            if (CollectionUtils.isEmpty(columnMapList)) {
               String selectSql = DialectRepository.getDialect().getSelectFromTableSql(table.getId());
               List<ColumnContext> columnContextList = DatabaseManager.selectColumnContextList(selectSql);
               columnDtoList = columnContextList.stream().map(c -> {
                  CoreCustomColumnDTO columnDto = new CoreCustomColumnDTO();
                  columnDto.setColumnName(c.getColumnName());
                  columnDto.setAliasName(c.getColumnName());
                  return columnDto;
               }).collect(Collectors.toList());
            } else {
               columnDtoList = columnMapList.parallelStream()
                  .map(m -> PersistableHelper.mapToPersistable((Map<String, Object>)m, CoreCustomColumnDTO.class))
                  .collect(Collectors.toList());
            }

            table.setColumnList(columnDtoList);
         }
      );
      return tableDtoList;
   }

   @Override
   public List<Map<String, Object>> previewSql(String tableName, RestJsonWrapperBean wrapper) {
      String tableSql = DialectRepository.getDialect().getSelectFromTableSql(tableName);
      if (DialectRepository.getDialect().getDatabase().equals("sqlserver")) {
         tableSql = tableSql + " ORDER BY 1 ";
      }

      tableSql = DialectRepository.getDialect().getLimitSql(tableSql, 0, 30);
      new ArrayList();

      try {
         List<ColumnContext> columnContextList = DatabaseManager.selectColumnContextList(tableSql);
         List<Map<String, Object>> resultMapList = this.globalMapper.selectList(tableSql);
         resultMapList.forEach(
            map -> map.forEach(
               (key, value) -> columnContextList.stream()
                  .filter(c -> LocalDateTime.class.equals(c.getType()) || LocalDate.class.equals(c.getType()))
                  .forEach(dataBaseColumn -> {
                     if (dataBaseColumn.getColumnName().equals(key) && value != null) {
                        if (LocalDate.class.equals(dataBaseColumn.getType())) {
                           map.replace(key, DateTimeUtils.formatLocalDate(ConvertUtils.convert(value, LocalDate.class)));
                        } else if (LocalDateTime.class.equals(dataBaseColumn.getType())) {
                           map.replace(key, DateTimeUtils.formatLocalDateTime(ConvertUtils.convert(value, LocalDateTime.class)));
                        }
                     }
                  })
            )
         );
         return resultMapList;
      } catch (Exception var6) {
         logger.info("数据库表" + tableName + "预览数据查询SQL:\n" + tableSql);
         throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.ITEM.CUSTOM_TABLES.TIP.TABLE_PREVIEW_ERROR") + tableName);
      }
   }

   @Override
   public void initDsTableData() {
      this.proxyInstance.selectAllList(new RestJsonWrapperBean());
   }
}
