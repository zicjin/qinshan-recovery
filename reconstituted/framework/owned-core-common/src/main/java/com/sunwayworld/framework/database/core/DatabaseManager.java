package com.sunwayworld.framework.database.core;

import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.beans.BeanPropertyHelper;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.database.context.ColumnContext;
import com.sunwayworld.framework.database.context.EntityColumnContext;
import com.sunwayworld.framework.database.context.EntityContext;
import com.sunwayworld.framework.database.context.TableContext;
import com.sunwayworld.framework.database.context.instance.EntityHelper;
import com.sunwayworld.framework.database.dialect.DialectRepository;
import com.sunwayworld.framework.exception.database.JdbcException;
import com.sunwayworld.framework.mybatis.MybatisHelper;
import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.JdbcUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class DatabaseManager {
   public static final String TABLE_CONTEXT_CACHE_NAME = "TC";
   public static final String ENTITY_CONTEXT_CACHE_NAME = "EC";

   public static TableContext getTableContext(String tableName) {
      tableName = tableName.toUpperCase();
      TableContext tableContext = RedisHelper.get("TC", tableName);
      if (tableContext == null) {
         tableContext = new TableContext();
         tableContext.setTableName(tableName);
         tableContext.setColumnContextList(selectColumnContextList(DialectRepository.getDialect().getSelectFromTableSql(tableName)));
         tableContext.setIdColumnContext(tableContext.getColumnContextList().stream().filter(c -> "ID".equalsIgnoreCase(c.getColumnName())).findAny().get());
         tableContext.setOrderColumnFlag(tableContext.getColumnContextList().stream().anyMatch(c -> "ORDERNO".equalsIgnoreCase(c.getColumnName())));
         RedisHelper.put("TC", tableName, tableContext);
      }

      return tableContext;
   }

   public static List<ColumnContext> getTableColumnContextList(String tableName) {
      tableName = tableName.toUpperCase();
      return getTableContext(tableName).getColumnContextList();
   }

   public static EntityContext getEntityContext(Class<? extends Persistable<?>> entityClass) {
      String cacheKey = ClassUtils.getRawType(entityClass).toString();
      String tableName = PersistableMetadataHelper.getTableName(entityClass);
      EntityContext entityContext = RedisHelper.get("EC", cacheKey);
      if (entityContext == null) {
         entityContext = new EntityContext();
         entityContext.setTableName(tableName);
         TableContext tableContext = getTableContext(tableName);
         List<BeanPropertyDescriptor> propertyDescriptorList = BeanPropertyHelper.getBeanPropertyDescriptorList(entityClass);
         List<EntityColumnContext> columnContextList = new ArrayList<>();

         for (ColumnContext columnContext : tableContext.getColumnContextList()) {
            BeanPropertyDescriptor propertyDescriptor = propertyDescriptorList.stream()
               .filter(p -> columnContext.getColumnName().equalsIgnoreCase(p.getName()))
               .findFirst()
               .orElse(null);
            EntityColumnContext entityColumnContext = new EntityColumnContext();
            entityColumnContext.setEntityClass(entityClass);
            entityColumnContext.setColumnName(columnContext.getColumnName());
            entityColumnContext.setBlob(DialectRepository.getDialect().isBlob(columnContext));
            entityColumnContext.setClob(DialectRepository.getDialect().isClob(columnContext));
            entityColumnContext.setTimestamp(DialectRepository.getDialect().isTimestamp(columnContext));
            if (propertyDescriptor == null) {
               entityColumnContext.setType(columnContext.getType());
            } else {
               entityColumnContext.setType(propertyDescriptor.getPropertyType());
            }

            if ("id".equalsIgnoreCase(columnContext.getColumnName())) {
               entityContext.setIdContext(entityColumnContext);
            } else if ("version".equalsIgnoreCase(columnContext.getColumnName())) {
               entityContext.setVersionContext(entityColumnContext);
            }

            columnContextList.add(entityColumnContext);
         }

         EntityHelper.sortColumnContext(columnContextList);
         entityContext.setColumnContextList(columnContextList);
         RedisHelper.put("EC", cacheKey, entityContext);
      }

      return entityContext;
   }

   public static void evictCache(Class<? extends Persistable<?>> entityClass) {
      String cacheKey = ClassUtils.getRawType(entityClass).toString();
      String tableName = PersistableMetadataHelper.getTableName(entityClass);
      RedisHelper.evict("TC", tableName);
      RedisHelper.evict("EC", cacheKey);
   }

   public static void evictCache(String tableName) {
      ClassUtils.getPredicatedClasses(
            "com.sunwayworld",
            c -> c.isAnnotationPresent(Table.class)
               && Persistable.class.isAssignableFrom(c)
               && tableName.equalsIgnoreCase(PersistableMetadataHelper.getTableName(c))
         )
         .forEach(c -> evictCache((Class<? extends Persistable<?>>)c));
   }

   public static List<ColumnContext> selectColumnContextList(String sql) {
      String selectEmptySql = DialectRepository.getDialect().getLimitSql(sql, 0, 0);
      Connection conn = null;
      PreparedStatement ps = null;
      ResultSet rs = null;

      Object var15;
      try {
         conn = MybatisHelper.getNewConnection();
         ps = conn.prepareStatement(selectEmptySql);
         rs = ps.executeQuery();
         ResultSetMetaData rsmd = rs.getMetaData();
         int qty = rsmd.getColumnCount();
         List<ColumnContext> columnContextList = new ArrayList<>();

         for (int i = 1; i <= qty; i++) {
            ColumnContext columnContext = new ColumnContext();
            columnContext.setRawColumnName(rsmd.getColumnName(i));
            columnContext.setColumnName(StringUtils.upperCase(columnContext.getRawColumnName()));
            columnContext.setDataType(rsmd.getColumnTypeName(i));
            columnContext.setClob(DialectRepository.getDialect().isClob(columnContext));
            columnContext.setBlob(DialectRepository.getDialect().isBlob(columnContext));
            columnContext.setPrecision(rsmd.getPrecision(i));
            columnContext.setScale(rsmd.getScale(i));
            columnContext.setType(
               DialectRepository.getDialect().getJavaType(columnContext.getColumnName(), columnContext.getDataType(), columnContext.getScale())
            );
            columnContext.setTimestamp(DialectRepository.getDialect().isTimestamp(columnContext));
            columnContextList.add(columnContext);
         }

         var15 = columnContextList;
      } catch (SQLException var13) {
         throw new JdbcException(var13);
      } finally {
         JdbcUtils.closeResultSet(rs);
         JdbcUtils.closeStatement(ps);
         JdbcUtils.closeConnection(conn);
      }

      return (List<ColumnContext>)var15;
   }
}
