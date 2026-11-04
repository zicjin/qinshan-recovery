package com.sunwayworld.framework.support.base.dao;

import com.sunwayworld.framework.context.SunwayAopContext;
import com.sunwayworld.framework.database.context.TableContext;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public interface MapDao {
   default void insert(String table, Map<String, Object> item) {
      this.insert(TableContext.of(table), item);
   }

   void insert(TableContext context, Map<String, Object> item);

   default void insert(String table, List<Map<String, Object>> itemList) {
      this.insert(TableContext.of(table), itemList);
   }

   void insert(TableContext context, List<Map<String, Object>> itemList);

   void fastInsert(TableContext context, List<Map<String, Object>> itemList);

   default <ID> void delete(String table, ID id) {
      this.delete(TableContext.of(table), id);
   }

   <ID> void delete(TableContext context, ID id);

   <ID> void deleteByIdList(TableContext context, List<ID> idList);

   default <ID> void deleteByIdList(String table, List<ID> idList) {
      this.deleteByIdList(TableContext.of(table), idList);
   }

   <ID> void deleteBy(TableContext context, List<Map<String, Object>> itemList, String... searchColumns);

   default void deleteBy(String table, Object searchValue, String searchColumn) {
      Map<String, Object> item = new HashMap<>();
      item.put(searchColumn, searchValue);
      this.deleteBy(TableContext.of(table), item, searchColumn);
   }

   default void deleteBy(TableContext context, Map<String, Object> item, String... searchColumns) {
      this.deleteBy(context, Arrays.asList(item), searchColumns);
   }

   default void deleteBy(String table, Map<String, Object> item, String... searchColumns) {
      this.deleteBy(TableContext.of(table), Arrays.asList(item), searchColumns);
   }

   default void update(String tableName, Map<String, Object> item, String... updateColumnColumns) {
      this.update(TableContext.of(tableName), item, Arrays.asList(updateColumnColumns));
   }

   default void update(String tableName, Map<String, Object> item, List<String> updateColumnList, String... searchColumns) {
      this.update(TableContext.of(tableName), item, updateColumnList, searchColumns);
   }

   default <ID> void updateByIdList(String tableName, Map<String, Object> item, List<ID> idList, String... updateColumns) {
      this.updateByIdList(TableContext.of(tableName), item, idList, updateColumns);
   }

   default void update(String tableName, List<Map<String, Object>> itemList, String... updateColumns) {
      this.update(TableContext.of(tableName), itemList, updateColumns);
   }

   <ID> void update(TableContext context, Map<String, Object> item, List<String> updateColumnList, String... searchColumns);

   default void update(TableContext context, Map<String, Object> item, String... updateColumns) {
      this.update(context, item, ArrayUtils.asList(updateColumns));
   }

   <ID> void updateByIdList(TableContext context, Map<String, Object> item, List<ID> idList, String... updateColumns);

   void update(TableContext context, List<Map<String, Object>> itemList, String... updateColumns);

   void fastUpdate(TableContext context, List<Map<String, Object>> itemList, String... updateColumns);

   default void update(TableContext context, List<Map<String, Object>> itemList, List<String> updateColumnList, String... searchColumns) {
      if (ArrayUtils.isEmpty(searchColumns)) {
         this.update(context, itemList, updateColumnList.toArray(new String[0]));
      } else {
         for (Map<String, Object> item : itemList) {
            this.update(context, item, updateColumnList, searchColumns);
         }
      }
   }

   default <ID> void update(TableContext context, Map<String, Object> updateItem, Map<String, Object> searchItem) {
      List<ID> idList = this.selectIdList(context, searchItem);
      if (!idList.isEmpty()) {
         this.updateByIdList(context, updateItem, idList);
      }
   }

   default void updateIfChanged(String table, Map<String, Object> item) {
      this.updateIfChanged(table, Arrays.asList(item));
   }

   default void updateIfChanged(String table, List<Map<String, Object>> itemList) {
      this.updateIfChanged(TableContext.of(table), itemList);
   }

   void updateIfChanged(TableContext context, List<Map<String, Object>> itemList);

   default void updateIfChanged(TableContext context, Map<String, Object> item) {
      this.updateIfChanged(context, Arrays.asList(item));
   }

   default List<Map<String, Object>> selectAll(TableContext context, List<Order> orderList, String... selectColNames) {
      return this.selectList(context, CollectionUtils.emptyMap(), ArrayUtils.asList(selectColNames), orderList.toArray(new Order[0]));
   }

   default List<Map<String, Object>> selectAll(TableContext context, Order... orders) {
      return this.selectAll(context, Arrays.asList(orders));
   }

   default List<Map<String, Object>> selectAll(TableContext context, String... selectColNames) {
      return this.selectAll(context, Collections.emptyList(), selectColNames);
   }

   default List<Map<String, Object>> selectAll(TableContext context) {
      return this.selectAll(context, Collections.emptyList());
   }

   <ID> Map<String, Object> selectByIdIfPresent(TableContext context, ID id);

   default <ID> Map<String, Object> selectById(TableContext context, ID id) {
      MapDao dao = SunwayAopContext.currentProxy();
      return Objects.requireNonNull(dao.selectByIdIfPresent(context, id), "MYBATIS.EXCEPTION.NO_VALUE_EXISTS");
   }

   Map<String, Object> selectFirstIfPresent(TableContext context, Map<String, Object> item, Order... orders);

   default Map<String, Object> selectFirstIfPresent(String tableName, Map<String, Object> item, Order... orders) {
      return this.selectFirstIfPresent(TableContext.of(tableName), item, orders);
   }

   Map<String, Object> selectOneIfPresent(TableContext context, Map<String, Object> item, String... selectColNames);

   default Map<String, Object> selectOneIfPresent(String tableName, Map<String, Object> item, String... selectColNames) {
      return this.selectOneIfPresent(TableContext.of(tableName), item, selectColNames);
   }

   default Map<String, Object> selectFirst(TableContext context, Map<String, Object> item, Order... orders) {
      MapDao dao = SunwayAopContext.currentProxy();
      return Objects.requireNonNull(dao.selectFirstIfPresent(context, item, orders), "MYBATIS.EXCEPTION.NO_VALUE_EXISTS");
   }

   default Map<String, Object> selectFirst(String tableName, Map<String, Object> item) {
      return this.selectFirst(TableContext.of(tableName), item);
   }

   default List<Map<String, Object>> selectList(
      String tableName, List<Map<String, Object>> itemList, List<String> searchColumnList, List<String> selectColumnList, Order... orders
   ) {
      return this.selectList(TableContext.of(tableName), itemList, searchColumnList, selectColumnList, orders);
   }

   List<Map<String, Object>> selectList(
      TableContext context, List<Map<String, Object>> itemList, List<String> searchColumnList, List<String> selectColumnList, Order... orders
   );

   default <V> List<V> selectList(
      TableContext context, List<Map<String, Object>> itemList, List<String> searchColumnList, String colName, Class<V> colType, Order... orders
   ) {
      List<String> selectColNameList = new ArrayList<>();
      selectColNameList.add(colName);
      List<Map<String, Object>> mapList = this.selectList(context, itemList, searchColumnList, selectColNameList, orders);
      return mapList.parallelStream()
         .map(m -> ConvertUtils.convert(CollectionUtils.getValueIgnorecase((Map<String, ?>)m, colName), colType))
         .collect(Collectors.toList());
   }

   default List<Map<String, Object>> selectList(String tableName, List<Map<String, Object>> itemList, Order... orders) {
      return this.selectList(TableContext.of(tableName), itemList, CollectionUtils.emptyList(), CollectionUtils.emptyList(), orders);
   }

   default List<Map<String, Object>> selectList(TableContext context, List<Map<String, Object>> itemList, Order... orders) {
      return this.selectList(context, itemList, CollectionUtils.emptyList(), CollectionUtils.emptyList(), orders);
   }

   default List<Map<String, Object>> selectList(TableContext context, List<Map<String, Object>> itemList, List<String> selectColumnList, Order... orders) {
      return this.selectList(context, itemList, CollectionUtils.emptyList(), selectColumnList, orders);
   }

   List<Map<String, Object>> selectList(TableContext context, Map<String, Object> item, List<String> selectColumnList, Order... orders);

   default List<Map<String, Object>> selectList(String tableName, Map<String, Object> item, Order... orders) {
      return this.selectList(TableContext.of(tableName), item, CollectionUtils.emptyList(), orders);
   }

   default List<Map<String, Object>> selectList(TableContext context, Map<String, Object> item, Order... orders) {
      return this.selectList(context, item, CollectionUtils.emptyList(), orders);
   }

   default <V> List<Map<String, Object>> selectListByColumnValueList(String tableName, List<V> valueList, String colName, Order... orders) {
      return this.selectListByColumnValueList(TableContext.of(tableName), valueList, colName, orders);
   }

   default <V> List<Map<String, Object>> selectListByColumnValueList(TableContext context, List<V> valueList, String colName, Order... orders) {
      List<Map<String, Object>> itemList = valueList.stream().map(v -> {
         Map<String, Object> map = new HashMap<>();
         map.put(colName, v);
         return map;
      }).collect(Collectors.toList());
      return this.selectList(context, itemList, Arrays.asList(colName), CollectionUtils.emptyList(), orders);
   }

   default <V> List<Map<String, Object>> selectListByOneColumnValue(String tableName, V value, String colName, Order... orders) {
      return this.selectListByOneColumnValue(TableContext.of(tableName), value, colName, orders);
   }

   default <V> List<Map<String, Object>> selectListByOneColumnValue(TableContext context, V value, String colName, Order... orders) {
      Map<String, Object> map = new HashMap<>();
      map.put(colName, value);
      return this.selectList(context, map, Collections.emptyList(), orders);
   }

   default <V> List<Map<String, Object>> selectListByColumnValueList(
      String tableName, List<V> valueList, String colName, List<String> searchColumnList, Order... orders
   ) {
      return this.selectListByColumnValueList(TableContext.of(tableName), valueList, colName, searchColumnList, orders);
   }

   default <V> List<Map<String, Object>> selectListByColumnValueList(
      TableContext context, List<V> valueList, String colName, List<String> searchColumnList, Order... orders
   ) {
      List<Map<String, Object>> itemList = valueList.stream().map(v -> {
         Map<String, Object> map = new HashMap<>();
         map.put(colName, v);
         return map;
      }).collect(Collectors.toList());
      return this.selectList(context, itemList, Arrays.asList(colName), searchColumnList, orders);
   }

   default <V> List<Map<String, Object>> selectListByOneColumnValue(String tableName, V value, String colName, List<String> searchColumnList, Order... orders) {
      return this.selectListByOneColumnValue(TableContext.of(tableName), value, colName, searchColumnList, orders);
   }

   default <V> List<Map<String, Object>> selectListByOneColumnValue(
      TableContext context, V value, String colName, List<String> searchColumnList, Order... orders
   ) {
      Map<String, Object> map = new HashMap<>();
      map.put(colName, value);
      return this.selectList(context, map, searchColumnList, orders);
   }

   <V> List<V> selectColumnList(
      TableContext context, Map<String, Object> item, List<String> searchColumnList, String colName, Class<V> colType, Order... orders
   );

   default <V> List<V> selectColumnList(TableContext context, Map<String, Object> item, String colName, Class<V> colType, Order... orders) {
      return this.selectColumnList(context, item, Collections.emptyList(), colName, colType, orders);
   }

   default <ID> List<ID> selectIdList(TableContext context, Map<String, Object> item, Order... orders) {
      return this.selectColumnList(context, item, "ID", (Class<ID>)context.getIdColumnContext().getType(), orders);
   }

   default <ID> List<ID> selectIdList(TableContext context, Map<String, Object> item, List<String> searchColNames, Order... orders) {
      return this.selectColumnList(context, item, searchColNames, "ID", (Class<ID>)context.getIdColumnContext().getType(), orders);
   }

   default <ID> List<ID> selectIdList(TableContext context, List<Map<String, Object>> itemList, List<String> searchColumnList, Order... orders) {
      return this.selectList(context, itemList, searchColumnList, "ID", (Class<ID>)context.getIdColumnContext().getType(), orders);
   }

   <ID> List<Map<String, Object>> selectListByIdList(TableContext context, List<ID> idList, List<String> selectColNames, Order... orders);

   default <ID> List<Map<String, Object>> selectListByIdList(TableContext context, List<ID> idList, Order... orders) {
      return this.selectListByIdList(context, idList, Collections.emptyList(), orders);
   }

   <ID, V> V selectColumnById(TableContext context, ID id, String column, Class<V> columnType);

   int countBy(TableContext context, Map<String, Object> item, String... searchColNames);

   int countBy(TableContext context, List<Map<String, Object>> itemList, String... searchColNames);

   default void updateCreatedBy(TableContext context, Map<String, Object> item) {
      this.updateCreatedBy(context, Arrays.asList(item));
   }

   void updateCreatedBy(TableContext context, List<Map<String, Object>> itemList);
}
