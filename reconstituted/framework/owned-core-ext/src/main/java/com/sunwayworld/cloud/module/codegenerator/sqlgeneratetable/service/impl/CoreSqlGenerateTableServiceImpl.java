package com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.service.impl;

import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.bean.CoreSqlGenerateFieldBean;
import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.bean.CoreSqlGenerateTableBean;
import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.bean.SqlDataDTO;
import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.persistent.dao.CoreSqlGenerateTableDao;
import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.service.CoreSqlGenerateFieldService;
import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.service.CoreSqlGenerateTableService;
import com.sunwayworld.cloud.module.codegenerator.support.helper.AuditSqlHelper;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreSqlGenerateTableServiceImpl implements CoreSqlGenerateTableService {
   public static final String DB_PGSQL = "pgsql";
   public static final String DB_ORACLE = "oracle";
   public static final String TYPE_ADD = "add";
   public static final String TYPE_REMOVE = "remove";
   @Autowired
   private CoreSqlGenerateTableDao sqlTableDao;
   @Autowired
   private CoreSqlGenerateFieldService sqlFieldService;

   public CoreSqlGenerateTableDao getDao() {
      return this.sqlTableDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      CoreSqlGenerateTableBean table = jsonWrapper.parseUnique(CoreSqlGenerateTableBean.class);
      table.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(table);
      return table.getId();
   }

   @Override
   public SqlDataDTO generateSql(RestJsonWrapperBean wrapper) {
      SqlDataDTO sqlDataDTO = new SqlDataDTO();
      List<CoreSqlGenerateTableBean> tableList = wrapper.parse(CoreSqlGenerateTableBean.class);
      List<Long> tableIds = tableList.stream().map(CoreSqlGenerateTableBean::getId).collect(Collectors.toList());
      List<CoreSqlGenerateFieldBean> fieldList = this.sqlFieldService
         .selectListByFilter(SearchFilter.instance().match("TABLEID", tableIds).filter(MatchPattern.OR), new Order[0]);
      StringBuilder oracleBuilder = new StringBuilder();
      StringBuilder mysqlBuilder = new StringBuilder();
      StringBuilder sqlserverBuilder = new StringBuilder();
      StringBuilder pgsqlBuilder = new StringBuilder();
      StringBuilder mysqlAndOracleAndPgsqlI18NBuilder = new StringBuilder("\r\n");
      StringBuilder sqlserverI18NBuilder = new StringBuilder("\r\n");
      StringBuilder mysqlAuditSqlBuilder = new StringBuilder("\r\n");
      StringBuilder oracleAuditSqlBuilder = new StringBuilder("\r\n");
      StringBuilder sqlserverAuditSqlBuilder = new StringBuilder("\r\n");
      StringBuilder pgsqlAuditSqlBuilder = new StringBuilder("\r\n");

      for (CoreSqlGenerateTableBean table : tableList) {
         String tableName = table.getTableName().toUpperCase();
         List<CoreSqlGenerateFieldBean> tableFieldList = fieldList.stream().filter(e -> e.getTableId() == table.getId()).collect(Collectors.toList());
         this.generateSqlByTable(tableName, tableFieldList, oracleBuilder, mysqlBuilder, sqlserverBuilder, pgsqlBuilder);
         this.generateI18NSqlByTable(tableName, tableFieldList, mysqlAndOracleAndPgsqlI18NBuilder, sqlserverI18NBuilder);
         this.generateAuditSqlByTable(tableName, tableFieldList, oracleAuditSqlBuilder, mysqlAuditSqlBuilder, sqlserverAuditSqlBuilder, pgsqlAuditSqlBuilder);
      }

      sqlDataDTO.setOracle(oracleBuilder.append((CharSequence)mysqlAndOracleAndPgsqlI18NBuilder).append((CharSequence)oracleAuditSqlBuilder).toString());
      sqlDataDTO.setMysql(mysqlBuilder.append((CharSequence)mysqlAndOracleAndPgsqlI18NBuilder).append((CharSequence)mysqlAuditSqlBuilder).toString());
      sqlDataDTO.setSqlserver(sqlserverBuilder.append((CharSequence)sqlserverI18NBuilder).append((CharSequence)sqlserverAuditSqlBuilder).toString());
      sqlDataDTO.setPgsql(pgsqlBuilder.append((CharSequence)mysqlAndOracleAndPgsqlI18NBuilder).append((CharSequence)pgsqlAuditSqlBuilder).toString());
      return sqlDataDTO;
   }

   @Override
   public Page<CoreSqlGenerateFieldBean> selectFieldPaginationBySqlGenerateTableId(Long id, RestJsonWrapperBean wrapper) {
      return this.sqlFieldService.selectPaginationByFilter(SearchFilter.instance().match("TABLEID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Override
   public Long insertField(Long id, RestJsonWrapperBean wrapper) {
      List<CoreSqlGenerateFieldBean> fieldList = wrapper.parse(CoreSqlGenerateFieldBean.class);
      fieldList.forEach(e -> {
         e.setId(ApplicationContextHelper.getNextIdentity());
         e.setTableId(id);
      });
      this.sqlFieldService.getDao().insert(fieldList);
      return fieldList.get(0).getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Override
   public void deleteField(Long id, RestJsonWrapperBean wrapper) {
      this.sqlFieldService.delete(wrapper);
   }

   private void generateSqlByTable(
      String tableName,
      List<CoreSqlGenerateFieldBean> tableFieldList,
      StringBuilder oracleBuilder,
      StringBuilder mysqlBuilder,
      StringBuilder sqlserverBuilder,
      StringBuilder pgsqlBuilder
   ) {
      List<CoreSqlGenerateFieldBean> addFieldList = tableFieldList.stream().filter(e -> "add".equals(e.getType())).collect(Collectors.toList());
      List<CoreSqlGenerateFieldBean> removeFieldList = tableFieldList.stream().filter(e -> "remove".equals(e.getType())).collect(Collectors.toList());
      this.generateOracleSql(tableName, addFieldList, removeFieldList, oracleBuilder);
      this.generateMysqlSql(tableName, addFieldList, removeFieldList, mysqlBuilder);
      this.generateSqlServerSql(tableName, addFieldList, removeFieldList, sqlserverBuilder);
      this.generatePgsqlSql(tableName, addFieldList, removeFieldList, pgsqlBuilder);
   }

   private void generateI18NSqlByTable(
      String tableName, List<CoreSqlGenerateFieldBean> tableFieldList, StringBuilder mysqlAndOracleI18NBuilder, StringBuilder sqlserverI18NBuilder
   ) {
      List<CoreSqlGenerateFieldBean> addFieldList = tableFieldList.stream().filter(e -> "add".equals(e.getType())).collect(Collectors.toList());
      List<CoreSqlGenerateFieldBean> removeFieldList = tableFieldList.stream().filter(e -> "remove".equals(e.getType())).collect(Collectors.toList());
      this.generateMysqlOraclePgsqlI18nSql(tableName, addFieldList, removeFieldList, mysqlAndOracleI18NBuilder);
      this.generateSqlServerI18nSql(tableName, addFieldList, removeFieldList, sqlserverI18NBuilder);
   }

   private void generateOracleSql(
      String tableName, List<CoreSqlGenerateFieldBean> addFieldList, List<CoreSqlGenerateFieldBean> removeFieldList, StringBuilder strBuilder
   ) {
      for (CoreSqlGenerateFieldBean field : removeFieldList) {
         strBuilder.append("alter table ").append(tableName).append(" drop column ").append(field.getField().toUpperCase()).append(";\r\n");
      }

      for (CoreSqlGenerateFieldBean field : addFieldList) {
         strBuilder.append("alter table ")
            .append(tableName)
            .append(" add ")
            .append(field.getField().toUpperCase())
            .append(" ")
            .append(this.getFieldDBType(field, "oracle"))
            .append(StringUtils.isEmpty(field.getFieldLength()) ? "" : "(" + field.getFieldLength() + ")")
            .append(";\r\n");
         strBuilder.append("comment on column ")
            .append(tableName)
            .append(".")
            .append(field.getField().toUpperCase())
            .append(" is '")
            .append(field.getFieldName())
            .append("';\r\n");
      }
   }

   private void generateMysqlSql(
      String tableName, List<CoreSqlGenerateFieldBean> addFieldList, List<CoreSqlGenerateFieldBean> removeFieldList, StringBuilder strBuilder
   ) {
      for (CoreSqlGenerateFieldBean field : removeFieldList) {
         strBuilder.append("alter table ").append(tableName).append(" drop column ").append(field.getField().toUpperCase()).append(";\r\n");
      }

      for (CoreSqlGenerateFieldBean field : addFieldList) {
         strBuilder.append("alter table ")
            .append(tableName)
            .append(" add column ")
            .append(field.getField().toUpperCase())
            .append(" ")
            .append(this.getFieldDBType(field, "mysql"))
            .append(StringUtils.isEmpty(field.getFieldLength()) ? "" : "(" + field.getFieldLength() + ")")
            .append(" ")
            .append("null ");
         strBuilder.append("comment '").append(field.getFieldName()).append("';\r\n");
      }
   }

   private void generateSqlServerSql(
      String tableName, List<CoreSqlGenerateFieldBean> addFieldList, List<CoreSqlGenerateFieldBean> removeFieldList, StringBuilder strBuilder
   ) {
      for (CoreSqlGenerateFieldBean field : removeFieldList) {
         strBuilder.append("alter table dbo.").append(tableName).append(" drop column ").append(field.getField().toUpperCase()).append("\r\n").append("GO\r\n");
      }

      for (CoreSqlGenerateFieldBean field : addFieldList) {
         strBuilder.append("alter table dbo.")
            .append(tableName)
            .append(" ADD ")
            .append(field.getField().toUpperCase())
            .append(" ")
            .append(this.getFieldDBType(field, "sqlserver"))
            .append(StringUtils.isEmpty(field.getFieldLength()) ? "" : "(" + field.getFieldLength() + ")")
            .append(" ")
            .append("NULL\r\n")
            .append("GO\r\n\r\n")
            .append("EXEC sp_addextendedproperty\r\n")
            .append("'MS_Description', N'")
            .append(field.getFieldName())
            .append("',\r\n")
            .append("'SCHEMA', N'dbo',\r\n")
            .append("'TABLE', N'")
            .append(tableName)
            .append("',\r\n")
            .append("'COLUMN', N'")
            .append(field.getField().toUpperCase())
            .append("'\r\nGO\r\n");
      }
   }

   private void generatePgsqlSql(
      String tableName, List<CoreSqlGenerateFieldBean> addFieldList, List<CoreSqlGenerateFieldBean> removeFieldList, StringBuilder strBuilder
   ) {
      for (CoreSqlGenerateFieldBean field : removeFieldList) {
         strBuilder.append("alter table ").append(tableName).append(" drop column ").append(field.getField().toUpperCase()).append(";\r\n");
      }

      for (CoreSqlGenerateFieldBean field : addFieldList) {
         strBuilder.append("alter table ")
            .append(tableName)
            .append(" add column ")
            .append(field.getField().toUpperCase())
            .append(" ")
            .append(this.getFieldDBType(field, "pgsql"))
            .append(StringUtils.isEmpty(field.getFieldLength()) ? "" : "(" + field.getFieldLength() + ")")
            .append(" ")
            .append("null ;\r\n");
         strBuilder.append("comment on column ")
            .append(tableName)
            .append(".")
            .append(field.getField().toUpperCase())
            .append(" is '")
            .append(field.getFieldName())
            .append("';\r\n");
      }
   }

   private void generateSqlServerI18nSql(
      String tableName, List<CoreSqlGenerateFieldBean> addFieldList, List<CoreSqlGenerateFieldBean> removeFieldList, StringBuilder i18nBuilder
   ) {
      for (CoreSqlGenerateFieldBean field : removeFieldList) {
         i18nBuilder.append("EXEC DBO.DELETEI18N N'")
            .append(tableName)
            .append(".")
            .append(field.getField().toUpperCase())
            .append("'")
            .append("\r\n")
            .append("GO\r\n");
      }

      for (CoreSqlGenerateFieldBean field : addFieldList) {
         i18nBuilder.append("EXEC  DBO.INSERTI18N N'")
            .append(tableName)
            .append(".")
            .append(field.getField().toUpperCase())
            .append("',")
            .append("N'")
            .append(field.getFieldName())
            .append("',")
            .append("N'")
            .append(field.getFieldName())
            .append("',")
            .append("N'")
            .append("zh-CN")
            .append("',")
            .append("N'")
            .append(field.getFieldName())
            .append("'")
            .append("\r\n")
            .append("GO\r\n");
      }
   }

   private void generateMysqlOraclePgsqlI18nSql(
      String tableName, List<CoreSqlGenerateFieldBean> addFieldList, List<CoreSqlGenerateFieldBean> removeFieldList, StringBuilder i18nBuilder
   ) {
      for (CoreSqlGenerateFieldBean field : removeFieldList) {
         i18nBuilder.append("CALL DELETEI18N('").append(tableName).append(".").append(field.getField().toUpperCase()).append("');").append("\r\n");
      }

      for (CoreSqlGenerateFieldBean field : addFieldList) {
         i18nBuilder.append("CALL INSERTI18N('")
            .append(tableName)
            .append(".")
            .append(field.getField().toUpperCase())
            .append("',")
            .append("'")
            .append(field.getFieldName())
            .append("',")
            .append("'")
            .append(field.getFieldName())
            .append("',")
            .append("'")
            .append("zh-CN")
            .append("',")
            .append("'")
            .append(field.getFieldName())
            .append("'")
            .append(");")
            .append("\r\n");
      }
   }

   private String getFieldDBType(CoreSqlGenerateFieldBean field, String database) {
      String fieldType = field.getFieldType();
      if ("varchar".equals(fieldType)) {
         if ("oracle".equals(database)) {
            return "VARCHAR2";
         }
      } else {
         if ("date".equals(fieldType)) {
            if ("oracle".equals(database)) {
               return "DATE";
            }

            if ("pgsql".equals(database)) {
               return "TIMESTAMP";
            }

            return "DATETIME";
         }

         if ("datetime".equals(fieldType)) {
            if ("oracle".equals(database)) {
               return "DATE";
            }

            if ("pgsql".equals(database)) {
               return "TIMESTAMP";
            }
         } else if ("clob".equals(fieldType)) {
            if (!"oracle".equals(database)) {
               return "TEXT";
            }
         } else if ("number".equals(fieldType) && !"oracle".equals(database)) {
            if (!StringUtils.isEmpty(field.getFieldLength()) && field.getFieldLength().indexOf(",") > 0) {
               return "DECIMAL";
            }

            return "BIGINT";
         }
      }

      return fieldType.toUpperCase();
   }

   private void generateAuditSqlByTable(
      String tableName,
      List<CoreSqlGenerateFieldBean> tableFieldList,
      StringBuilder oracleAuditSql,
      StringBuilder mysqlAuditSqlBuilder,
      StringBuilder sqlserverAuditSqlBuilder,
      StringBuilder pgsqlAuditSqlBuilder
   ) {
      List<String> addFieldList = tableFieldList.stream()
         .filter(e -> "add".equals(e.getType()))
         .map(CoreSqlGenerateFieldBean::getField)
         .map(String::toUpperCase)
         .collect(Collectors.toList());
      List<String> removeFieldList = tableFieldList.stream()
         .filter(e -> "remove".equals(e.getType()))
         .map(CoreSqlGenerateFieldBean::getField)
         .map(String::toUpperCase)
         .collect(Collectors.toList());
      CoreUserBean loginUser = LocalContextHelper.getLoginUser();
      String userId = loginUser.getId();
      String username = loginUser.getUserName();
      AuditSqlHelper.generateAuditTrailSql(
         tableName, userId, username, addFieldList, removeFieldList, oracleAuditSql, mysqlAuditSqlBuilder, sqlserverAuditSqlBuilder, pgsqlAuditSqlBuilder
      );
      AuditSqlHelper.generateAuditV2Sql(
         tableName, addFieldList, removeFieldList, oracleAuditSql, mysqlAuditSqlBuilder, sqlserverAuditSqlBuilder, pgsqlAuditSqlBuilder
      );
   }
}
