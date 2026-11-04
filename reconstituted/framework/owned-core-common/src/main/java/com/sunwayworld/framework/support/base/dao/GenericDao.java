package com.sunwayworld.framework.support.base.dao;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.framework.aspect.annotation.IgnoreAop;
import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.context.SunwayAopContext;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pageable;
import com.sunwayworld.framework.database.context.EntityColumnContext;
import com.sunwayworld.framework.database.context.EntityContext;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.UnsupportedException;
import com.sunwayworld.framework.exception.database.NotUniqueResultJdbcException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface GenericDao<T extends Persistable<ID>, ID extends Serializable> {
   int STATEMENT_MAX_BATCH_BIND_VALUES = 400;
   Logger log = LoggerFactory.getLogger(GenericDao.class);

   @IgnoreAop
   default String getTable() {
      return PersistableMetadataHelper.getTableName(this.getType());
   }

   Class<T> getType();

   EntityContext getEntityContext();

   default GenericMapper<ID> getMapper() {
      throw new UnsupportedException();
   }

   void insert(T item);

   void insert(List<T> itemList);

   void fastInsert(List<T> itemList);

   void delete(ID id);

   void deleteByIdList(List<ID> idList);

   void deleteBy(List<T> itemList, String... searchColNames);

   default void deleteBy(T item, String... searchColNames) {
      this.deleteBy(Arrays.asList((T[])(new Persistable[]{item})), searchColNames);
   }

   default void updateToEmptyData(List<T> itemList, String... updateColNames) {
      this.updateToEmptyData(itemList, Arrays.asList(updateColNames));
   }

   default void updateToEmptyData(List<T> itemList, List<String> updateColNames) {
      for (T item : itemList) {
         this.updateToEmptyData(item, updateColNames);
      }
   }

   void updateToEmptyData(T item, List<String> updateColNames);

   void update(T item, List<String> updateColNames, String... searchColNames);

   default void update(T item, String... updateColNames) {
      this.update(item, ArrayUtils.asList(updateColNames));
   }

   void updateByIds(T item, List<ID> idList, String... updateColNames);

   default void update(List<T> itemList, List<String> updateColNames, String... searchColNames) {
      if (ArrayUtils.isEmpty(searchColNames)) {
         this.update(itemList, updateColNames.toArray(new String[0]));
      } else {
         for (T item : itemList) {
            this.update(item, updateColNames, searchColNames);
         }
      }
   }

   void update(List<T> itemList, String... updateColNames);

   void fastUpdate(List<T> itemList, String... updateColNames);

   default void update(T updateItem, T searchItem) {
      List<ID> idList = this.selectIdList(searchItem);
      if (!idList.isEmpty()) {
         this.updateByIds(updateItem, idList);
      }
   }

   void updateById(T updateItem, ID id, String... updateColNames);

   List<T> updateIfChanged(List<T> rawOrProxyItemList, List<String> ignoredColumnList);

   default T updateIfChanged(T rawOrProxyItem, List<String> ignoredColumnList) {
      List<T> itemList = this.updateIfChanged(Arrays.asList((T[])(new Persistable[]{rawOrProxyItem})), ignoredColumnList);
      return itemList.isEmpty() ? null : itemList.get(0);
   }

   default List<T> updateIfChanged(List<T> rawOrProxyItemList) {
      return this.updateIfChanged(rawOrProxyItemList, CollectionUtils.emptyList());
   }

   default T updateIfChanged(T rawOrProxyItem) {
      List<T> itemList = this.updateIfChanged(Arrays.asList((T[])(new Persistable[]{rawOrProxyItem})));
      return itemList.isEmpty() ? null : itemList.get(0);
   }

   default void updateCreatedBy(T item) {
      this.updateCreatedBy(Arrays.asList((T[])(new Persistable[]{item})));
   }

   void updateCreatedBy(List<T> itemList);

   T selectByIdIfPresent(ID id);

   default T selectById(ID id) {
      GenericDao<T, ID> dao = SunwayAopContext.currentProxy();
      return Objects.requireNonNull(dao.selectByIdIfPresent(id), I18nHelper.getMessage("GIKAM.EXCEPTION.NO_VALUE_EXISTS", this.getTable(), String.valueOf(id)));
   }

   T selectOneIfPresent(T item, String... selectColNames);

   default T selectOne(T item, String... selectColNames) {
      return Objects.requireNonNull(
         this.selectOneIfPresent(item, selectColNames), I18nHelper.getMessage("GIKAM.EXCEPTION.NO_VALUE_EXISTS", this.getTable(), JSON.toJSONString(item))
      );
   }

   <V> V selectColumnById(ID id, String colName, Class<V> colType);

   <V> List<V> selectColumnList(T item, List<String> searchColNames, String colName, Class<V> colType, Order... orders);

   default <V> List<V> selectColumnList(T item, String colName, Class<V> colType, Order... orders) {
      return this.selectColumnList(item, CollectionUtils.emptyList(), colName, colType, orders);
   }

   default <V> V selectColumnIfPresent(T item, String colName, Class<V> colType) {
      List<V> columnList = this.selectColumnList(item, colName, colType);
      if (columnList.size() > 1) {
         throw new NotUniqueResultJdbcException();
      } else {
         return columnList.isEmpty() ? null : columnList.get(0);
      }
   }

   default <V> V selectColumn(T item, String colName, Class<V> colType) {
      return Objects.requireNonNull(
         this.selectColumnIfPresent(item, colName, colType), I18nHelper.getMessage("GIKAM.EXCEPTION.NO_VALUE_EXISTS", this.getTable(), JSON.toJSONString(item))
      );
   }

   default List<ID> selectIdList(T item, Order... orders) {
      EntityColumnContext idContext = this.getEntityContext().getIdContext();
      return this.selectColumnList(item, idContext.getColumnName(), (Class<ID>)idContext.getType(), orders);
   }

   default List<ID> selectIdList(T item, List<String> searchColNames, Order... orders) {
      EntityColumnContext idContext = this.getEntityContext().getIdContext();
      return this.selectColumnList(item, searchColNames, idContext.getColumnName(), (Class<ID>)idContext.getType(), orders);
   }

   default ID selectIdIfPresent(T item) {
      EntityColumnContext idContext = this.getEntityContext().getIdContext();
      return this.selectColumnIfPresent(item, idContext.getColumnName(), (Class<ID>)idContext.getType());
   }

   default ID selectId(T item) {
      return Objects.requireNonNull(
         this.selectIdIfPresent(item), I18nHelper.getMessage("GIKAM.EXCEPTION.NO_VALUE_EXISTS", this.getTable(), JSON.toJSONString(item))
      );
   }

   List<T> selectAll(List<Order> orderList, String... selectColNames);

   default List<T> selectAll(Order... orders) {
      return this.selectAll(Arrays.asList(orders));
   }

   default List<T> selectAll(String... selectColNames) {
      return this.selectAll(CollectionUtils.emptyList(), selectColNames);
   }

   default List<T> selectAll() {
      return this.selectAll(CollectionUtils.emptyList());
   }

   List<T> selectListByIds(List<ID> idList, List<String> selectColNames, Order... orders);

   default List<T> selectListByIds(List<ID> idList, Order... orders) {
      return this.selectListByIds(idList, CollectionUtils.emptyList(), orders);
   }

   <V> List<V> selectColumnsByIds(List<ID> idList, String colName, Class<V> colType, Order... orders);

   List<T> selectList(T item, List<String> searchColNames, List<String> selectColNames, Order... orders);

   default List<T> selectList(T item, List<String> selectColNames, Order... orders) {
      return this.selectList(item, CollectionUtils.emptyList(), selectColNames, orders);
   }

   default List<T> selectList(T item, Order... orders) {
      return this.selectList(item, CollectionUtils.emptyList(), orders);
   }

   List<T> selectList(List<T> itemList, List<String> searchColNames, List<String> selectColNames, Order... orders);

   <V> List<V> selectList(List<T> itemList, List<String> searchColNames, String colName, Class<V> colType, Order... orders);

   default <V> List<V> selectList(List<T> itemList, String colName, Class<V> colType, Order... orders) {
      return this.selectList(itemList, CollectionUtils.emptyList(), colName, colType, orders);
   }

   List<T> selectListByOneColumnValues(String colName, List<T> itemList, List<String> selectColNames, Order... orders);

   default <V> List<T> selectListByOneColumnValues(List<V> valueList, String colName, List<String> selectColNames, Order... orders) {
      List<T> itemList = valueList.stream().map(v -> {
         T item = ClassUtils.newInstance(this.getType());
         this.getEntityContext().getColumnContextList().stream().filter(c -> c.getColumnName().equalsIgnoreCase(colName)).forEach(columnContext -> {
            BeanPropertyDescriptor propertyDescriptor = columnContext.getPropertyDescriptor();
            if (propertyDescriptor.isExt$BeanProperty()) {
               item.getExt$().put(columnContext.getColumnName(), ConvertUtils.convert(v, String.class));
            } else {
               ReflectionUtils.invokeMethod(propertyDescriptor.getWriteMethod(), item, ConvertUtils.convert(v, columnContext.getType()));
            }
         });
         return item;
      }).collect(Collectors.toList());
      return this.selectListByOneColumnValues(colName, itemList, selectColNames, orders);
   }

   default <V> List<T> selectListByOneColumnValues(List<V> valueList, String colName, Order... orders) {
      return this.selectListByOneColumnValues(valueList, colName, CollectionUtils.emptyList(), orders);
   }

   default <V> List<T> selectListByOneColumnValue(V value, String colName, List<String> selectColNames, Order... orders) {
      return this.selectListByOneColumnValues(Arrays.asList(value), colName, selectColNames, orders);
   }

   default <V> List<T> selectListByOneColumnValue(V value, String colName, Order... orders) {
      return this.selectListByOneColumnValues(Arrays.asList(value), colName, CollectionUtils.emptyList(), orders);
   }

   default List<ID> selectIdList(List<T> itemList, List<String> searchColNames, Order... orders) {
      EntityColumnContext idContext = this.getEntityContext().getIdContext();
      return this.selectList(itemList, searchColNames, idContext.getColumnName(), (Class<ID>)idContext.getType(), orders);
   }

   default List<ID> selectIdList(List<T> item, Order... orders) {
      EntityColumnContext idContext = this.getEntityContext().getIdContext();
      return this.selectList(item, CollectionUtils.emptyList(), idContext.getColumnName(), (Class<ID>)idContext.getType(), orders);
   }

   T selectFirstIfPresent(T item, List<String> searchColNames, List<String> selectColNames, Order... orders);

   default T selectFirst(T item, List<String> searchColNames, List<String> selectColNames, Order... orders) {
      return Objects.requireNonNull(
         this.selectFirstIfPresent(item, searchColNames, selectColNames, orders),
         I18nHelper.getMessage("GIKAM.EXCEPTION.NO_VALUE_EXISTS", this.getTable(), JSON.toJSONString(item))
      );
   }

   default T selectFirstIfPresent(T item, List<String> selectColNames, Order... orders) {
      return this.selectFirstIfPresent(item, CollectionUtils.emptyList(), selectColNames, orders);
   }

   default T selectFirst(T item, List<String> selectColNames, Order... orders) {
      return Objects.requireNonNull(
         this.selectFirstIfPresent(item, selectColNames, orders),
         I18nHelper.getMessage("GIKAM.EXCEPTION.NO_VALUE_EXISTS", this.getTable(), JSON.toJSONString(item))
      );
   }

   default T selectFirstIfPresent(T item, Order... orders) {
      return this.selectFirstIfPresent(item, CollectionUtils.emptyList(), CollectionUtils.emptyList(), orders);
   }

   default T selectFirst(T item, Order... orders) {
      return Objects.requireNonNull(
         this.selectFirstIfPresent(item, orders), I18nHelper.getMessage("GIKAM.EXCEPTION.NO_VALUE_EXISTS", this.getTable(), JSON.toJSONString(item))
      );
   }

   List<T> selectUnionList(T item, List<String> searchColNames, List<String> selectColNames, Order... orders);

   default List<T> selectUnionList(T item, List<String> selectColNames, Order... orders) {
      return this.selectUnionList(item, CollectionUtils.emptyList(), selectColNames, orders);
   }

   default List<T> selectUnionList(T item, Order... orders) {
      return this.selectUnionList(item, CollectionUtils.emptyList(), CollectionUtils.emptyList(), orders);
   }

   default T selectOneUnionIfPresent(T item, List<String> searchColNames, List<String> selectColNames, Order... orders) {
      List<T> resultList = this.selectUnionList(item, searchColNames, selectColNames, orders);
      if (resultList.isEmpty()) {
         return null;
      } else if (resultList.size() > 1) {
         throw new NotUniqueResultJdbcException();
      } else {
         return resultList.get(0);
      }
   }

   default T selectOneUnion(T item, List<String> searchColNames, List<String> selectColNames, Order... orders) {
      return Objects.requireNonNull(
         this.selectOneUnionIfPresent(item, searchColNames, selectColNames, orders), I18nHelper.getMessage("GIKAM.EXCEPTION.NO_VALUE_EXISTS")
      );
   }

   default T selectOneUnionIfPresent(T item, List<String> selectColNames, Order... orders) {
      return this.selectOneUnionIfPresent(item, CollectionUtils.emptyList(), selectColNames, orders);
   }

   default T selectOneUnion(T item, List<String> selectColNames, Order... orders) {
      return Objects.requireNonNull(this.selectOneUnionIfPresent(item, selectColNames, orders));
   }

   default T selectOneUnionIfPresent(T item, Order... orders) {
      return this.selectOneUnionIfPresent(item, CollectionUtils.emptyList(), CollectionUtils.emptyList(), orders);
   }

   default T selectOneUnion(T item, Order... orders) {
      return Objects.requireNonNull(this.selectOneUnionIfPresent(item, orders));
   }

   int countBy(T item, String... searchColNames);

   default int count() {
      return this.countBy(ClassUtils.newInstance(this.getType()));
   }

   int countBy(List<T> itemList, String... searchColNames);

   <V> Map<V, Long> groupCountByValueList(List<V> valueList, String searchColName);

   Page<T> selectPaginationByMybatis(String mybatisStatementId, Pageable pageable);

   default Map<String, Object> selectDetail(ID id) {
      MapperParameter parameter = new MapperParameter();
      parameter.setDetailQueries();
      parameter.setFilter(SearchFilter.instance().match("ID", id).filter(MatchPattern.EQ));
      ServletUtils.enhanceByRequest(parameter);
      parameter.setTableName(PersistableMetadataHelper.getTableName(this.getType()));
      parameter.reviseParams(this.getEntityContext());
      List<Map<String, Object>> mapList = this.getMapper().selectByCondition(parameter);
      return mapList.isEmpty() ? CollectionUtils.emptyMap() : mapList.get(0);
   }

   default List<Map<String, Object>> selectByCondition(MapperParameter parameter) {
      List<EntityColumnContext> columnContextList = this.getEntityContext().getColumnContextList();
      String simpleColumns = columnContextList.stream()
         .filter(c -> !c.isBlob() && !c.isClob())
         .map(c -> "T." + c.getColumnName())
         .collect(Collectors.joining(", "));
      parameter.setSimpleColumns(simpleColumns);
      parameter.setTableName(PersistableMetadataHelper.getTableName(this.getType()));
      parameter.reviseParams(this.getEntityContext());
      return this.getMapper().selectByCondition(parameter);
   }

   default void cacheEvict(T oldItem, T newItem) {
   }
}
