package com.sunwayworld.framework.mybatis.dao;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.sunwayworld.framework.at.AuditTrailHelper;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.at.aspect.AuditTrailAspect;
import com.sunwayworld.framework.at.aspect.AuditTrailAspect.AuditTrailEntryData;
import com.sunwayworld.framework.audit.AuditDatabaseOperation;
import com.sunwayworld.framework.audit.AuditLogHelper;
import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.beans.BeanPropertyEvent;
import com.sunwayworld.framework.beans.BeanPropertyHelper;
import com.sunwayworld.framework.beans.BeanPropertyListener;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.GikamBeanContext;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.context.SunwayAopContext;
import com.sunwayworld.framework.data.ChunkIterator;
import com.sunwayworld.framework.data.ListChunkIterator;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pageable;
import com.sunwayworld.framework.database.context.EntityColumnContext;
import com.sunwayworld.framework.database.context.EntityContext;
import com.sunwayworld.framework.database.context.instance.EntityColumnContextInstance;
import com.sunwayworld.framework.database.context.instance.EntityContextInstance;
import com.sunwayworld.framework.database.context.instance.EntityHelper;
import com.sunwayworld.framework.database.core.DatabaseManager;
import com.sunwayworld.framework.database.dialect.Dialect;
import com.sunwayworld.framework.database.dialect.DialectRepository;
import com.sunwayworld.framework.database.dialect.OracleDialect;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.database.sql.SqlType;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.exception.database.JdbcException;
import com.sunwayworld.framework.exception.database.NotUniqueResultJdbcException;
import com.sunwayworld.framework.mybatis.dao.support.DaoAfterDeleteContext;
import com.sunwayworld.framework.mybatis.dao.support.DaoAfterDeleteSupport;
import com.sunwayworld.framework.mybatis.dao.support.DaoAfterInsertContext;
import com.sunwayworld.framework.mybatis.dao.support.DaoAfterInsertSupport;
import com.sunwayworld.framework.mybatis.mapper.DaoMapper;
import com.sunwayworld.framework.mybatis.mapper.GlobalBoundSql;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestFieldValueTextContainer;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.domain.Orderable;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.JdbcUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineBean;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Repository;

@Repository
public abstract class MybatisDaoSupport<T extends Persistable<ID>, ID extends Serializable> implements GenericDao<T, ID> {
   @Autowired
   private Dialect dialect;
   @Autowired
   private DaoMapper daoMapper;
   @Autowired
   private GlobalMapper globalMapper;
   @Autowired
   private SqlSessionTemplate mybatisTemplate;
   @Autowired
   private DaoAfterDeleteSupport daoAfterDeleteSupport;
   @Autowired
   private DaoAfterInsertSupport daoAfterInsertSupport;
   private final Class<T> type = (Class<T>)ResolvableType.forClass(this.getClass()).as(GenericDao.class).getGeneric(0).resolve();

   @Override
   public Class<T> getType() {
      return this.type;
   }

   @Override
   public EntityContext getEntityContext() {
      return DatabaseManager.getEntityContext(this.type);
   }

   @Override
   public void insert(T item) {
      if (null != item) {
         this.preInsert(Arrays.asList((T[])(new Persistable[]{item})));
         EntityContextInstance contextInstance = EntityContextInstance.insertable(this.getEntityContext(), item);
         this.daoMapper.insert(contextInstance);
         if (!ApplicationContextHelper.stressTesting()
            && AuditTrailHelper.auditTrail()
            && AuditTrailHelper.auditTrailable(this.getTable(), AuditDatabaseOperation.INSERT)) {
            AuditTrailEntryData auditTrailEntryData = AuditTrailAspect.getAuditTrailEntryData();
            if (auditTrailEntryData != null) {
               String table = this.getTable();
               List<EntityColumnContextInstance> columnContextInstanceList = contextInstance.getColumnContextInstanceList();
               ID id = item.getId();
               List<CoreAuditTrailRecordLineBean> recordLineList = new ArrayList<>();

               for (EntityColumnContextInstance columnContextInstance : columnContextInstanceList) {
                  if (AuditTrailHelper.auditTrailableColumn(table, columnContextInstance.getColumnContext().getColumnName())) {
                     CoreAuditTrailRecordLineBean line = new CoreAuditTrailRecordLineBean();
                     line.setRecordType(SqlType.INSERT.name());
                     line.setTargetId(table + "$" + id);
                     line.setTableName(table);
                     line.setColumn(columnContextInstance.getColumnContext().getColumnName());
                     line.setNewValue(RestFieldValueTextContainer.getText(line.getColumn(), ObjectUtils.toString(columnContextInstance.getValue())));
                     recordLineList.add(line);
                  }
               }

               if (!recordLineList.isEmpty()) {
                  String targetId = AuditTrailAspect.getAncientTargetId(table, id);
                  AuditTrailAspect.addRecordLine(targetId, recordLineList);
               }
            }
         }

         if (!ApplicationContextHelper.stressTesting()) {
            boolean cacheEvictOverrided = this.isCacheEvictOverrided();
            boolean auditable = AuditLogHelper.auditable(AuditDatabaseOperation.INSERT);
            if (cacheEvictOverrided || auditable) {
               T selectedItem = this.selectById(item.getId());
               if (auditable) {
                  AuditLogHelper.insertAuditLog(this.getTable(), null, selectedItem);
               }

               SunwayAopContext.<GenericDao>currentProxy().cacheEvict(null, selectedItem);
            }
         }

         this.afterInsert(Collections.singletonList(item));
      }
   }

   @Override
   public void insert(List<T> itemList) {
      if (null != itemList && !itemList.isEmpty()) {
         this.preInsert(itemList);
         EntityContext context = this.getEntityContext();
         int columnQty = context.getColumnContextList().size();
         int loopItemQty = Math.min(this.dialect.getMaxBatchInsertQty(), this.dialect.getMaxParamQty() / columnQty);
         ListChunkIterator<T> chunkIterator = ListChunkIterator.of(itemList, loopItemQty);

         while (chunkIterator.hasNext()) {
            this.daoMapper
               .batchInsert(context, chunkIterator.nextChunk().stream().map(t -> EntityContextInstance.insertable(context, (T)t)).collect(Collectors.toList()));
         }

         if (!ApplicationContextHelper.stressTesting()
            && AuditTrailHelper.auditTrail()
            && AuditTrailHelper.auditTrailable(this.getTable(), AuditDatabaseOperation.INSERT)) {
            AuditTrailEntryData auditTrailEntryData = AuditTrailAspect.getAuditTrailEntryData();
            if (auditTrailEntryData != null) {
               String table = this.getTable();
               Map<ID, List<CoreAuditTrailRecordLineBean>> map = new HashMap<>();

               for (T item : itemList) {
                  ID lineId = item.getId();
                  List<CoreAuditTrailRecordLineBean> recordLineList = new ArrayList<>();

                  for (EntityColumnContext columnContext : context.getColumnContextList()) {
                     if (AuditTrailHelper.auditTrailableColumn(table, columnContext.getColumnName())) {
                        CoreAuditTrailRecordLineBean line = new CoreAuditTrailRecordLineBean();
                        line.setRecordType(SqlType.INSERT.name());
                        line.setTargetId(table + "$" + lineId);
                        line.setTableName(table);
                        line.setColumn(columnContext.getColumnName());
                        line.setNewValue(RestFieldValueTextContainer.getText(line.getColumn(), ObjectUtils.toString(columnContext.getValue(item))));
                        recordLineList.add(line);
                     }
                  }

                  if (!recordLineList.isEmpty()) {
                     map.put(lineId, recordLineList);
                  }
               }

               if (!map.isEmpty()) {
                  Map<ID, String> targetMap = AuditTrailAspect.getAncientTargetMap(table, new ArrayList<>(map.keySet()));
                  targetMap.forEach((k, v) -> AuditTrailAspect.addRecordLine(v, map.get(k)));
               }
            }
         }

         if (!ApplicationContextHelper.stressTesting()) {
            boolean cacheEvictOverrided = this.isCacheEvictOverrided();
            boolean auditable = AuditLogHelper.auditable(AuditDatabaseOperation.INSERT);
            if (cacheEvictOverrided || auditable) {
               List<ID> idList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
               List<T> selectedItemList = this.selectListByIds(idList, new Order[0]);
               if (AuditLogHelper.auditable(AuditDatabaseOperation.INSERT)) {
                  for (T selectedItem : selectedItemList) {
                     AuditLogHelper.insertAuditLog(this.getTable(), null, selectedItem);
                  }
               }

               for (T selectedItem : selectedItemList) {
                  SunwayAopContext.<GenericDao>currentProxy().cacheEvict(null, selectedItem);
               }
            }
         }

         this.afterInsert(itemList);
      }
   }

   @Override
   public void fastInsert(List<T> itemList) {
      if (null != itemList && !itemList.isEmpty()) {
         this.preInsert(itemList);
         EntityContext context = this.getEntityContext();
         List<EntityContextInstance> contextInstanceList = itemList.stream()
            .map(t -> EntityContextInstance.insertable(context, (T)t))
            .collect(Collectors.toList());
         GlobalBoundSql insertBoundSql = this.dialect.getBatchInsertSql(context, contextInstanceList);
         this.globalMapper.updateByBoundSql(insertBoundSql);
         if (!ApplicationContextHelper.stressTesting()
            && AuditTrailHelper.auditTrail()
            && AuditTrailHelper.auditTrailable(this.getTable(), AuditDatabaseOperation.INSERT)) {
            AuditTrailEntryData auditTrailEntryData = AuditTrailAspect.getAuditTrailEntryData();
            if (auditTrailEntryData != null) {
               String table = this.getTable();
               Map<ID, List<CoreAuditTrailRecordLineBean>> map = new HashMap<>();

               for (T item : itemList) {
                  ID lineId = item.getId();
                  List<CoreAuditTrailRecordLineBean> recordLineList = new ArrayList<>();

                  for (EntityColumnContext columnContext : context.getColumnContextList()) {
                     if (AuditTrailHelper.auditTrailableColumn(table, columnContext.getColumnName())) {
                        CoreAuditTrailRecordLineBean line = new CoreAuditTrailRecordLineBean();
                        line.setRecordType(SqlType.INSERT.name());
                        line.setTargetId(table + "$" + lineId);
                        line.setTableName(table);
                        line.setColumn(columnContext.getColumnName());
                        line.setNewValue(RestFieldValueTextContainer.getText(line.getColumn(), ObjectUtils.toString(columnContext.getValue(item))));
                        recordLineList.add(line);
                     }
                  }

                  if (!recordLineList.isEmpty()) {
                     map.put(lineId, recordLineList);
                  }
               }

               if (!map.isEmpty()) {
                  Map<ID, String> targetMap = AuditTrailAspect.getAncientTargetMap(table, new ArrayList<>(map.keySet()));
                  targetMap.forEach((k, v) -> AuditTrailAspect.addRecordLine(v, map.get(k)));
               }
            }
         }

         if (!ApplicationContextHelper.stressTesting()) {
            boolean cacheEvictOverrided = this.isCacheEvictOverrided();
            boolean auditable = AuditLogHelper.auditable(AuditDatabaseOperation.INSERT);
            if (cacheEvictOverrided || auditable) {
               List<ID> idList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
               List<T> selectedItemList = this.selectListByIds(idList, new Order[0]);
               if (AuditLogHelper.auditable(AuditDatabaseOperation.INSERT)) {
                  for (T selectedItem : selectedItemList) {
                     AuditLogHelper.insertAuditLog(this.getTable(), null, selectedItem);
                  }
               }

               for (T selectedItem : selectedItemList) {
                  SunwayAopContext.<GenericDao>currentProxy().cacheEvict(null, selectedItem);
               }
            }
         }

         this.afterInsert(itemList);
      }
   }

   @Override
   public void delete(ID id) {
      if (id == null) {
         throw new JdbcException("GIKAM.EXCEPTION.DELETE_REQUIRE_ID");
      } else {
         T selectedItem = this.selectByIdIfPresent(id);
         if (selectedItem != null) {
            EntityContextInstance contextInstance = EntityContextInstance.instance(this.getEntityContext(), selectedItem);
            if (!ApplicationContextHelper.stressTesting()
               && AuditTrailHelper.auditTrail()
               && AuditTrailHelper.auditTrailable(this.getTable(), AuditDatabaseOperation.DELETE)) {
               AuditTrailEntryData auditTrailEntryData = AuditTrailAspect.getAuditTrailEntryData();
               if (auditTrailEntryData != null) {
                  String table = this.getTable();
                  CoreAuditTrailRecordLineBean line = new CoreAuditTrailRecordLineBean();
                  line.setRecordType(SqlType.DELETE.name());
                  line.setTargetId(table + "$" + id);
                  line.setTableName(table);
                  line.setColumn("ID");
                  line.setOldValue(RestFieldValueTextContainer.getText(line.getColumn(), ObjectUtils.toString(id)));
                  String targetId = AuditTrailAspect.getAncientTargetId(table, id);
                  AuditTrailAspect.addRecordLine(targetId, line);
               }
            }

            this.daoMapper.deleteById(contextInstance);
            if (!ApplicationContextHelper.stressTesting()) {
               if (AuditLogHelper.auditable(AuditDatabaseOperation.DELETE)) {
                  AuditLogHelper.insertAuditLog(this.getTable(), selectedItem, null);
               }

               SunwayAopContext.<GenericDao>currentProxy().cacheEvict(selectedItem, null);
            }

            this.afterDelete(Collections.singletonList(selectedItem));
         }
      }
   }

   @Override
   public void deleteByIdList(List<ID> idList) {
      if (idList != null && !idList.isEmpty()) {
         if (idList.stream().anyMatch(i -> i == null)) {
            throw new JdbcException("GIKAM.EXCEPTION.DELETE_REQUIRE_ID");
         } else {
            if (!ApplicationContextHelper.stressTesting()
               && AuditTrailHelper.auditTrail()
               && AuditTrailHelper.auditTrailable(this.getTable(), AuditDatabaseOperation.DELETE)) {
               AuditTrailEntryData auditTrailEntryData = AuditTrailAspect.getAuditTrailEntryData();
               if (auditTrailEntryData != null) {
                  String table = this.getTable();
                  Map<ID, CoreAuditTrailRecordLineBean> map = new HashMap<>();

                  for (ID id : idList) {
                     CoreAuditTrailRecordLineBean line = new CoreAuditTrailRecordLineBean();
                     line.setRecordType(SqlType.DELETE.name());
                     line.setTargetId(table + "$" + id);
                     line.setTableName(table);
                     line.setColumn("ID");
                     line.setOldValue(RestFieldValueTextContainer.getText(line.getColumn(), ObjectUtils.toString(id)));
                     map.put(id, line);
                  }

                  if (!map.isEmpty()) {
                     Map<ID, String> targetMap = AuditTrailAspect.getAncientTargetMap(table, new ArrayList<>(map.keySet()));
                     targetMap.forEach((k, v) -> AuditTrailAspect.addRecordLine(v, map.get(k)));
                  }
               }
            }

            List<T> selectedItemList = ApplicationContextHelper.stressTesting() ? null : this.selectListByIds(idList, new Order[0]);
            EntityContext context = this.getEntityContext();
            boolean loop = idList.size() > this.dialect.getInClauseIdMaxQty();
            if (loop) {
               ListChunkIterator<ID> chunkIterator = ListChunkIterator.of(idList, this.dialect.getInClauseIdMaxQty());

               while (chunkIterator.hasNext()) {
                  this.daoMapper.batchDeleteById(context, chunkIterator.nextChunk().stream().map(i -> (Object)i).collect(Collectors.toList()));
               }
            } else {
               this.daoMapper.batchDeleteById(context, idList.stream().map(i -> (Object)i).collect(Collectors.toList()));
            }

            if (!ApplicationContextHelper.stressTesting()) {
               if (AuditLogHelper.auditable(AuditDatabaseOperation.DELETE)) {
                  for (T selectedItem : selectedItemList) {
                     AuditLogHelper.insertAuditLog(this.getTable(), selectedItem, null);
                  }
               }

               selectedItemList.forEach(i -> SunwayAopContext.<GenericDao>currentProxy().cacheEvict((T)i, null));
            }

            this.afterDelete(selectedItemList);
         }
      }
   }

   @Override
   public void deleteBy(List<T> itemList, String... searchColNames) {
      if (itemList != null && !itemList.isEmpty()) {
         if (ArrayUtils.isEmpty(searchColNames) && itemList.stream().anyMatch(i -> BeanUtils.isEmpty((T)i))) {
            throw new JdbcException("GIKAM.EXCEPTION.DELETE_REQUIRE_NONE_EMPTY_BEAN");
         } else {
            List<ID> idList = ArrayUtils.isEmpty(searchColNames)
               ? itemList.stream().filter(i -> i.getId() != null).map(i -> i.getId()).collect(Collectors.toList())
               : this.selectIdList(itemList, ArrayUtils.asList(searchColNames), new Order[0]);
            if (!idList.isEmpty()) {
               this.deleteByIdList(idList);
            }
         }
      }
   }

   @Override
   public void update(T item, List<String> updateColNameList, String... searchColNames) {
      List<String> clone = updateColNameList == null ? new ArrayList<>() : new ArrayList<>(updateColNameList);
      EntityContextInstance contextInstance = EntityContextInstance.updatable(this.getEntityContext(), item);
      if (!clone.isEmpty()) {
         contextInstance.getColumnContextInstanceList()
            .forEach(c -> c.setActive(clone.stream().anyMatch(n -> n.equalsIgnoreCase(c.getColumnContext().getColumnName()))));
      }

      if (EntityHelper.isColumnUpdated(contextInstance)) {
         List<T> oldItemList = new ArrayList<>();
         List<String> updatedColumnNameList = new ArrayList<>();
         if (ArrayUtils.isEmpty(searchColNames)) {
            T oldItem = this.selectById(item.getId());
            if (!ApplicationContextHelper.stressTesting()
               && AuditTrailHelper.auditTrail()
               && AuditTrailHelper.auditTrailable(this.getTable(), AuditDatabaseOperation.UPDATE)) {
               AuditTrailEntryData auditTrailEntryData = AuditTrailAspect.getAuditTrailEntryData();
               if (auditTrailEntryData != null) {
                  String table = this.getTable();
                  List<CoreAuditTrailRecordLineBean> recordLineList = new ArrayList<>();
                  contextInstance.getColumnContextInstanceList()
                     .stream()
                     .filter(c -> c.isActive() && AuditTrailHelper.auditTrailableColumn(table, c.getColumnContext().getColumnName()))
                     .forEach(c -> {
                        CoreAuditTrailRecordLineBean line = new CoreAuditTrailRecordLineBean();
                        line.setRecordType(SqlType.UPDATE.name());
                        line.setTargetId(table + "$" + item.getId());
                        line.setTableName(table);
                        line.setColumn(c.getColumnContext().getColumnName().toUpperCase());
                        line.setOldValue(RestFieldValueTextContainer.getText(line.getColumn(), ObjectUtils.toString(c.getColumnContext().getValue(oldItem))));
                        line.setNewValue(RestFieldValueTextContainer.getText(line.getColumn(), ObjectUtils.toString(c.getValue())));
                        recordLineList.add(line);
                     });
                  if (!recordLineList.isEmpty()) {
                     String targetId = AuditTrailAspect.getAncientTargetId(table, item.getId());
                     AuditTrailAspect.addRecordLine(targetId, recordLineList);
                  }
               }
            }

            oldItemList.add(oldItem);
            this.preUpdate(contextInstance);
            this.daoMapper.updateById(contextInstance);
            contextInstance.getColumnContextInstanceList()
               .stream()
               .filter(c -> c.isActive())
               .forEach(c -> updatedColumnNameList.add(c.getColumnContext().getColumnName()));
         } else {
            oldItemList.addAll(this.selectList(item, ArrayUtils.asList(searchColNames), CollectionUtils.emptyList()));
            if (!oldItemList.isEmpty()) {
               int chunkSize = this.dialect.getInClauseIdMaxQty();
               ChunkIterator<T> chunkIterator = ListChunkIterator.of(oldItemList, chunkSize);

               while (chunkIterator.hasNext()) {
                  List<T> chunkList = chunkIterator.nextChunk();
                  if (!ApplicationContextHelper.stressTesting()
                     && AuditTrailHelper.auditTrail()
                     && AuditTrailHelper.auditTrailable(this.getTable(), AuditDatabaseOperation.UPDATE)) {
                     AuditTrailEntryData auditTrailEntryData = AuditTrailAspect.getAuditTrailEntryData();
                     if (auditTrailEntryData != null) {
                        String table = this.getTable();
                        Map<ID, List<CoreAuditTrailRecordLineBean>> map = new HashMap<>();

                        for (T oldItem : chunkList) {
                           List<CoreAuditTrailRecordLineBean> recordLineList = new ArrayList<>();
                           contextInstance.getColumnContextInstanceList()
                              .stream()
                              .filter(c -> c.isActive() && AuditTrailHelper.auditTrailableColumn(table, c.getColumnContext().getColumnName()))
                              .forEach(c -> {
                                 Object oldValue = c.getColumnContext().getValue(oldItem);
                                 if (!ObjectUtils.equals(oldValue, c.getValue())) {
                                    CoreAuditTrailRecordLineBean line = new CoreAuditTrailRecordLineBean();
                                    line.setRecordType(SqlType.UPDATE.name());
                                    line.setTargetId(table + "$" + oldItem.getId());
                                    line.setTableName(table);
                                    line.setColumn(c.getColumnContext().getColumnName().toUpperCase());
                                    line.setOldValue(RestFieldValueTextContainer.getText(line.getColumn(), ObjectUtils.toString(oldValue)));
                                    line.setNewValue(RestFieldValueTextContainer.getText(line.getColumn(), ObjectUtils.toString(c.getValue())));
                                    recordLineList.add(line);
                                 }
                              });
                           if (!recordLineList.isEmpty()) {
                              map.put(oldItem.getId(), recordLineList);
                           }
                        }

                        if (!map.isEmpty()) {
                           Map<ID, String> targetMap = AuditTrailAspect.getAncientTargetMap(table, new ArrayList<>(map.keySet()));
                           targetMap.forEach((k, v) -> AuditTrailAspect.addRecordLine(v, map.get(k)));
                        }
                     }
                  }

                  List<Object> idList = chunkList.stream().map(i -> i.getId()).collect(Collectors.toList());
                  this.preUpdateByIds(contextInstance, idList);
                  this.daoMapper.updateByIds(contextInstance, idList);
               }

               contextInstance.getColumnContextInstanceList()
                  .stream()
                  .filter(c -> c.isActive())
                  .forEach(c -> updatedColumnNameList.add(c.getColumnContext().getColumnName()));
            }
         }

         List<ID> idList = oldItemList.stream().map(i -> i.getId()).collect(Collectors.toList());
         if (!ApplicationContextHelper.stressTesting()) {
            boolean cacheEvictOverrided = this.isCacheEvictOverrided();
            boolean auditable = AuditLogHelper.auditable(AuditDatabaseOperation.UPDATE);
            if (cacheEvictOverrided || auditable) {
               List<T> newItemList = this.selectListByIds(idList, new Order[0]);
               this.auditUpdate(oldItemList, newItemList);

               for (T newItem : newItemList) {
                  T oldItemx = oldItemList.stream().filter(i -> i.getId().equals(newItem.getId())).findAny().orElse(null);
                  SunwayAopContext.<GenericDao>currentProxy().cacheEvict(oldItemx, newItem);
               }
            }
         }

         GenericService<T, ID> service = GikamBeanContext.getServiceByTable(this.getTable());
         if (service != null) {
            service.postUpdate(updatedColumnNameList, idList);
         }
      }
   }

   @Override
   public void updateToEmptyData(T item, List<String> updateColNameList) {
      List<String> clone = updateColNameList == null ? new ArrayList<>() : new ArrayList<>(updateColNameList);
      EntityContextInstance contextInstance = EntityContextInstance.updatable(this.getEntityContext(), item);
      if (!clone.isEmpty()) {
         contextInstance.getColumnContextInstanceList()
            .forEach(c -> c.setActive(clone.stream().anyMatch(n -> n.equalsIgnoreCase(c.getColumnContext().getColumnName()))));
      }

      if (EntityHelper.isColumnUpdated(contextInstance)) {
         List<T> oldItemList = new ArrayList<>();
         List<String> updatedColumnNameList = new ArrayList<>();
         T oldItem = this.selectById(item.getId());
         EntityContextInstance oldItemInstance = EntityContextInstance.instance(this.getEntityContext(), oldItem);
         List<EntityColumnContextInstance> oldItemColumnContextInstance = oldItemInstance.getColumnContextInstanceList();
         contextInstance.getColumnContextInstanceList()
            .stream()
            .filter(c -> c.isActive())
            .forEach(
               c -> {
                  EntityColumnContextInstance oldEntityColumnContextInstance = oldItemColumnContextInstance.stream()
                     .filter(old -> old.getColumnContext().getColumnName().equalsIgnoreCase(c.getColumnContext().getColumnName()))
                     .findFirst()
                     .get();
                  c.setActive(ObjectUtils.isEmpty(oldEntityColumnContextInstance.getValue()));
               }
            );
         if (!contextInstance.getColumnContextInstanceList().stream().filter(c -> c.isActive()).collect(Collectors.toList()).isEmpty()) {
            if (!ApplicationContextHelper.stressTesting()
               && AuditTrailHelper.auditTrail()
               && AuditTrailHelper.auditTrailable(this.getTable(), AuditDatabaseOperation.UPDATE)) {
               AuditTrailEntryData auditTrailEntryData = AuditTrailAspect.getAuditTrailEntryData();
               if (auditTrailEntryData != null) {
                  String table = this.getTable();
                  List<CoreAuditTrailRecordLineBean> recordLineList = new ArrayList<>();
                  contextInstance.getColumnContextInstanceList()
                     .stream()
                     .filter(c -> c.isActive() && AuditTrailHelper.auditTrailableColumn(table, c.getColumnContext().getColumnName()))
                     .forEach(c -> {
                        CoreAuditTrailRecordLineBean line = new CoreAuditTrailRecordLineBean();
                        line.setRecordType(SqlType.UPDATE.name());
                        line.setTargetId(table + "$" + item.getId());
                        line.setTableName(table);
                        line.setColumn(c.getColumnContext().getColumnName().toUpperCase());
                        line.setOldValue(RestFieldValueTextContainer.getText(line.getColumn(), ObjectUtils.toString(c.getColumnContext().getValue(oldItem))));
                        line.setNewValue(RestFieldValueTextContainer.getText(line.getColumn(), ObjectUtils.toString(c.getValue())));
                        recordLineList.add(line);
                     });
                  if (!recordLineList.isEmpty()) {
                     String targetId = AuditTrailAspect.getAncientTargetId(table, item.getId());
                     AuditTrailAspect.addRecordLine(targetId, recordLineList);
                  }
               }
            }

            oldItemList.add(oldItem);
            this.preUpdate(contextInstance);
            this.daoMapper.updateById(contextInstance);
            contextInstance.getColumnContextInstanceList()
               .stream()
               .filter(c -> c.isActive())
               .forEach(c -> updatedColumnNameList.add(c.getColumnContext().getColumnName()));
            List<ID> idList = oldItemList.stream().map(i -> i.getId()).collect(Collectors.toList());
            if (!ApplicationContextHelper.stressTesting()) {
               boolean cacheEvictOverrided = this.isCacheEvictOverrided();
               boolean auditable = AuditLogHelper.auditable(AuditDatabaseOperation.UPDATE);
               if (cacheEvictOverrided || auditable) {
                  List<T> newItemList = this.selectListByIds(idList, new Order[0]);
                  this.auditUpdate(oldItemList, newItemList);

                  for (T newItem : newItemList) {
                     SunwayAopContext.<GenericDao>currentProxy().cacheEvict(oldItem, newItem);
                  }
               }
            }

            GenericService<T, ID> service = GikamBeanContext.getServiceByTable(this.getTable());
            if (service != null) {
               service.postUpdate(updatedColumnNameList, idList);
            }
         }
      }
   }

   @Override
   public void updateByIds(T item, List<ID> idList, String... updateColNames) {
      EntityContextInstance contextInstance = EntityContextInstance.updatable(this.getEntityContext(), item);
      if (updateColNames != null && updateColNames.length > 0) {
         List<String> updateColNameList = ArrayUtils.asList(updateColNames);
         contextInstance.getColumnContextInstanceList()
            .forEach(p -> p.setActive(CollectionUtils.containsIgnoreCase(updateColNameList, p.getColumnContext().getColumnName())));
      }

      if (EntityHelper.isColumnUpdated(contextInstance)) {
         List<T> oldItemList = this.selectListByIds(idList, new Order[0]);
         int chunkSize = this.dialect.getInClauseIdMaxQty();
         ChunkIterator<T> chunkIterator = ListChunkIterator.of(oldItemList, chunkSize);

         while (chunkIterator.hasNext()) {
            List<T> chunkList = chunkIterator.nextChunk();
            if (!ApplicationContextHelper.stressTesting()
               && AuditTrailHelper.auditTrail()
               && AuditTrailHelper.auditTrailable(this.getTable(), AuditDatabaseOperation.UPDATE)) {
               AuditTrailEntryData auditTrailEntryData = AuditTrailAspect.getAuditTrailEntryData();
               if (auditTrailEntryData != null) {
                  String table = this.getTable();
                  Map<ID, List<CoreAuditTrailRecordLineBean>> map = new HashMap<>();

                  for (T oldItem : chunkList) {
                     List<CoreAuditTrailRecordLineBean> recordLineList = new ArrayList<>();
                     contextInstance.getColumnContextInstanceList()
                        .stream()
                        .filter(c -> c.isActive() && AuditTrailHelper.auditTrailableColumn(table, c.getColumnContext().getColumnName()))
                        .forEach(c -> {
                           Object oldValue = c.getColumnContext().getValue(oldItem);
                           if (!ObjectUtils.equals(oldValue, c.getValue())) {
                              CoreAuditTrailRecordLineBean line = new CoreAuditTrailRecordLineBean();
                              line.setRecordType(SqlType.UPDATE.name());
                              line.setTargetId(table + "$" + oldItem.getId());
                              line.setTableName(table);
                              line.setColumn(c.getColumnContext().getColumnName().toUpperCase());
                              line.setOldValue(RestFieldValueTextContainer.getText(line.getColumn(), ObjectUtils.toString(oldValue)));
                              line.setNewValue(RestFieldValueTextContainer.getText(line.getColumn(), ObjectUtils.toString(c.getValue())));
                              recordLineList.add(line);
                           }
                        });
                     if (!recordLineList.isEmpty()) {
                        map.put(oldItem.getId(), recordLineList);
                     }
                  }

                  if (!map.isEmpty()) {
                     Map<ID, String> targetMap = AuditTrailAspect.getAncientTargetMap(table, new ArrayList<>(map.keySet()));
                     targetMap.forEach((k, v) -> AuditTrailAspect.addRecordLine(v, map.get(k)));
                  }
               }
            }

            List<Object> chunkIdList = chunkList.stream().map(i -> i.getId()).collect(Collectors.toList());
            this.preUpdateByIds(contextInstance, chunkIdList);
            this.daoMapper.updateByIds(contextInstance, chunkIdList);
         }

         if (!ApplicationContextHelper.stressTesting()) {
            boolean cacheEvictOverrided = this.isCacheEvictOverrided();
            boolean auditable = AuditLogHelper.auditable(AuditDatabaseOperation.UPDATE);
            if (cacheEvictOverrided || auditable) {
               List<T> newItemList = this.selectListByIds(idList, new Order[0]);
               this.auditUpdate(oldItemList, newItemList);

               for (T newItem : newItemList) {
                  T oldItemx = oldItemList.stream().filter(i -> i.getId().equals(newItem.getId())).findAny().orElse(null);
                  SunwayAopContext.<GenericDao>currentProxy().cacheEvict(oldItemx, newItem);
               }
            }
         }

         List<String> updatedColumnNameList = contextInstance.getColumnContextInstanceList()
            .stream()
            .filter(c -> c.isActive())
            .map(c -> c.getColumnContext().getColumnName())
            .collect(Collectors.toList());
         GenericService<T, ID> service = GikamBeanContext.getServiceByTable(this.getTable());
         if (service != null) {
            service.postUpdate(updatedColumnNameList, idList);
         }
      }
   }

   @Override
   public void updateById(T item, ID id, String... updateColNames) {
      EntityContextInstance contextInstance = EntityContextInstance.updatable(this.getEntityContext(), item);
      if (updateColNames != null && updateColNames.length > 0) {
         List<String> updateColNameList = ArrayUtils.asList(updateColNames);
         contextInstance.getColumnContextInstanceList()
            .forEach(p -> p.setActive(CollectionUtils.containsIgnoreCase(updateColNameList, p.getColumnContext().getColumnName())));
      } else {
         contextInstance.getIdInstance().setActive(!ObjectUtils.isEmpty(item.getId()) && !ObjectUtils.equals(item.getId(), id));
      }

      T oldItem = this.selectById(id);
      if (!ApplicationContextHelper.stressTesting()
         && AuditTrailHelper.auditTrail()
         && AuditTrailHelper.auditTrailable(this.getTable(), AuditDatabaseOperation.UPDATE)) {
         AuditTrailEntryData auditTrailEntryData = AuditTrailAspect.getAuditTrailEntryData();
         if (auditTrailEntryData != null) {
            String table = this.getTable();
            Map<ID, List<CoreAuditTrailRecordLineBean>> map = new HashMap<>();
            List<CoreAuditTrailRecordLineBean> recordLineList = new ArrayList<>();
            contextInstance.getColumnContextInstanceList()
               .stream()
               .filter(c -> c.isActive() && AuditTrailHelper.auditTrailableColumn(table, c.getColumnContext().getColumnName()))
               .forEach(c -> {
                  Object oldValue = c.getColumnContext().getValue(oldItem);
                  if (!ObjectUtils.equals(oldValue, c.getValue())) {
                     CoreAuditTrailRecordLineBean line = new CoreAuditTrailRecordLineBean();
                     line.setRecordType(SqlType.UPDATE.name());
                     line.setTargetId(table + "$" + oldItem.getId());
                     line.setTableName(table);
                     line.setColumn(c.getColumnContext().getColumnName().toUpperCase());
                     line.setOldValue(RestFieldValueTextContainer.getText(line.getColumn(), ObjectUtils.toString(oldValue)));
                     line.setNewValue(RestFieldValueTextContainer.getText(line.getColumn(), ObjectUtils.toString(c.getValue())));
                     recordLineList.add(line);
                  }
               });
            if (!recordLineList.isEmpty()) {
               map.put(oldItem.getId(), recordLineList);
            }

            if (!map.isEmpty()) {
               Map<ID, String> targetMap = AuditTrailAspect.getAncientTargetMap(table, new ArrayList<>(map.keySet()));
               targetMap.forEach((k, v) -> AuditTrailAspect.addRecordLine(v, map.get(k)));
            }
         }
      }

      this.preUpdateByIds(contextInstance, ArrayUtils.asList(id));
      this.daoMapper.updateByIdExt(contextInstance, id);
      if (!ApplicationContextHelper.stressTesting()) {
         boolean cacheEvictOverrided = this.isCacheEvictOverrided();
         boolean auditable = AuditLogHelper.auditable(AuditDatabaseOperation.UPDATE);
         if (cacheEvictOverrided || auditable) {
            ID currentId = contextInstance.getIdInstance().isActive() ? item.getId() : id;
            T newItem = this.selectById(currentId);
            this.auditUpdate(ArrayUtils.asList((T[])(new Persistable[]{oldItem})), ArrayUtils.asList((T[])(new Persistable[]{newItem})));
            SunwayAopContext.<GenericDao>currentProxy().cacheEvict(oldItem, newItem);
         }
      }

      List<String> updatedColumnNameList = contextInstance.getColumnContextInstanceList()
         .stream()
         .filter(c -> c.isActive())
         .map(c -> c.getColumnContext().getColumnName())
         .collect(Collectors.toList());
      GenericService<T, ID> service = GikamBeanContext.getServiceByTable(this.getTable());
      if (service != null) {
         service.postUpdate(updatedColumnNameList, (List<ID>)ArrayUtils.asList(id));
      }
   }

   @Override
   public void update(List<T> itemList, String... updateColNames) {
      if (itemList != null && !itemList.isEmpty()) {
         List<T> cloneItemList = new ArrayList<>(itemList);
         List<String> updateColNameList = new ArrayList<>();
         EntityContext context = this.getEntityContext();
         if (ArrayUtils.isEmpty(updateColNames)) {
            T item = cloneItemList.get(0);
            EntityContextInstance contextInstance = EntityContextInstance.updatable(context, item);
            contextInstance.getColumnContextInstanceList().forEach(i -> {
               if (i.isActive() && !EntityHelper.isIdInstance(i)) {
                  updateColNameList.add(i.getColumnContext().getColumnName());
               }
            });
         } else {
            updateColNameList.addAll(Arrays.asList(updateColNames));
         }

         if (!updateColNameList.isEmpty()) {
            if (DialectRepository.getDialect() instanceof OracleDialect) {
               if (!(cloneItemList instanceof ArrayList)) {
                  cloneItemList = new ArrayList<>(cloneItemList);
               }

               Iterator<T> iterator = cloneItemList.iterator();

               while (iterator.hasNext()) {
                  T item = iterator.next();

                  for (String updateColName : updateColNameList) {
                     Object value = PersistableHelper.getPropertyValue(item, updateColName);
                     if (value instanceof String && JdbcUtils.isBigData((String)value)) {
                        iterator.remove();
                        this.update(item, updateColNameList);
                        break;
                     }
                  }
               }

               if (cloneItemList.isEmpty()) {
                  return;
               }
            }

            List<ID> idList = cloneItemList.stream().map(i -> i.getId()).collect(Collectors.toList());
            boolean cacheEvictOverrided = this.isCacheEvictOverrided();
            boolean auditable = AuditLogHelper.auditable(AuditDatabaseOperation.UPDATE);
            boolean selectList = !ApplicationContextHelper.stressTesting()
               && (
                  AuditTrailHelper.auditTrail() && AuditTrailHelper.auditTrailable(this.getTable(), AuditDatabaseOperation.UPDATE)
                     || cacheEvictOverrided
                     || auditable
               );
            List<T> oldItemList = selectList ? this.selectListByIds(idList, new Order[0]) : null;
            int updateColQty = updateColNameList.size();
            int chunkSize = this.dialect.getInClauseIdMaxQty();
            int maxParamQty = this.dialect.getMaxParamQty();
            if (maxParamQty < chunkSize * (updateColQty * 2 + 1)) {
               chunkSize = maxParamQty / (updateColQty * 2 + 1);
            }

            ChunkIterator<T> chunkIterator = ListChunkIterator.of(cloneItemList, chunkSize);

            while (chunkIterator.hasNext()) {
               List<T> chunkItemList = chunkIterator.nextChunk();
               if (!ApplicationContextHelper.stressTesting()
                  && AuditTrailHelper.auditTrail()
                  && AuditTrailHelper.auditTrailable(this.getTable(), AuditDatabaseOperation.UPDATE)) {
                  AuditTrailEntryData auditTrailEntryData = AuditTrailAspect.getAuditTrailEntryData();
                  if (auditTrailEntryData != null) {
                     String table = this.getTable();
                     Map<ID, List<CoreAuditTrailRecordLineBean>> map = new HashMap<>();

                     for (T newItem : chunkItemList) {
                        boolean deleteFile = false;
                        T oldItem = oldItemList.stream().filter(i -> i.getId().equals(newItem.getId())).findFirst().get();
                        List<CoreAuditTrailRecordLineBean> recordLineList = new ArrayList<>();

                        for (String updateColNamex : updateColNameList) {
                           if (AuditTrailHelper.auditTrailableColumn(table, updateColNamex)) {
                              Object oldValue = null;
                              Object newValue = null;
                              if (ReflectionUtils.findField(this.getType(), updateColNamex) == null) {
                                 oldValue = oldItem.getExt$().get(updateColNamex);
                                 newValue = newItem.getExt$().get(updateColNamex);
                              } else {
                                 oldValue = ReflectionUtils.invokeReadMethod(oldItem, updateColNamex);
                                 newValue = ReflectionUtils.invokeReadMethod(newItem, updateColNamex);
                              }

                              if (!ObjectUtils.equals(oldValue, newValue)) {
                                 CoreAuditTrailRecordLineBean line = new CoreAuditTrailRecordLineBean();
                                 line.setTargetId(table + "$" + oldItem.getId());
                                 line.setTableName(table);
                                 if (AuditTrailType.DELETE.equals(auditTrailEntryData.getEntry().value())
                                    && "TARGETID".equalsIgnoreCase(updateColNamex)
                                    && "T_CORE_FILE".equalsIgnoreCase(table)) {
                                    deleteFile = true;
                                    line.setRecordType(SqlType.DELETE.name());
                                    CoreFileBean file = (CoreFileBean)oldItem;
                                    line.setColumn("NAME");
                                    line.setOldValue(file.getName());
                                 } else {
                                    line.setRecordType(SqlType.UPDATE.name());
                                    line.setColumn(updateColNamex.toUpperCase());
                                    line.setOldValue(RestFieldValueTextContainer.getText(line.getColumn(), ObjectUtils.toString(oldValue)));
                                    line.setNewValue(RestFieldValueTextContainer.getText(line.getColumn(), ObjectUtils.toString(newValue)));
                                 }

                                 recordLineList.add(line);
                              }
                           }
                        }

                        if (!recordLineList.isEmpty()) {
                           if (deleteFile) {
                              String targetId = null;
                              CoreFileBean file = (CoreFileBean)oldItem;
                              int index = file.getTargetId().indexOf("$");
                              if (index > 0 && index < file.getTargetId().length()) {
                                 targetId = AuditTrailAspect.getAncientTargetId(file.getTargetId().substring(0, index), file.getTargetId().substring(index + 1));
                              } else {
                                 targetId = file.getTargetId();
                              }

                              AuditTrailAspect.addRecordLine(targetId, recordLineList);
                           } else {
                              map.put(newItem.getId(), recordLineList);
                           }
                        }
                     }

                     if (!map.isEmpty()) {
                        Map<ID, String> targetMap = AuditTrailAspect.getAncientTargetMap(table, new ArrayList<>(map.keySet()));
                        targetMap.forEach((k, v) -> AuditTrailAspect.addRecordLine(v, map.get(k)));
                     }
                  }
               }

               List<EntityContextInstance> contextInstanceList = chunkItemList.stream()
                  .map(i -> EntityContextInstance.updatable(context, (T)i))
                  .collect(Collectors.toList());
               this.preBatchUpdateByIds(context, contextInstanceList, updateColNameList);
               this.daoMapper.batchUpdateByIds(context, contextInstanceList, updateColNameList);
            }

            if (!ApplicationContextHelper.stressTesting() && (cacheEvictOverrided || auditable)) {
               List<T> newItemList = this.selectListByIds(idList, new Order[0]);
               this.auditUpdate(oldItemList, newItemList);

               for (T newItem : newItemList) {
                  T oldItem = oldItemList.stream().filter(i -> i.getId().equals(newItem.getId())).findAny().orElse(null);
                  SunwayAopContext.<GenericDao>currentProxy().cacheEvict(oldItem, newItem);
               }
            }

            GenericService<T, ID> service = GikamBeanContext.getServiceByTable(this.getTable());
            if (service != null) {
               service.postUpdate(updateColNameList, idList);
            }
         }
      }
   }

   @Override
   public void fastUpdate(List<T> itemList, String... updateColNames) {
      if (itemList != null && !itemList.isEmpty()) {
         List<String> updateColNameList = new ArrayList<>();
         EntityContext context = this.getEntityContext();
         if (ArrayUtils.isEmpty(updateColNames)) {
            T item = itemList.get(0);
            EntityContextInstance contextInstance = EntityContextInstance.updatable(context, item);
            contextInstance.getColumnContextInstanceList().forEach(i -> {
               if (i.isActive() && !EntityHelper.isIdInstance(i)) {
                  updateColNameList.add(i.getColumnContext().getColumnName());
               }
            });
         } else {
            updateColNameList.addAll(Arrays.asList(updateColNames));
         }

         if (!updateColNameList.isEmpty()) {
            if (DialectRepository.getDialect() instanceof OracleDialect) {
               if (!(itemList instanceof ArrayList)) {
                  itemList = new ArrayList<>(itemList);
               }

               Iterator<T> iterator = itemList.iterator();

               while (iterator.hasNext()) {
                  T item = iterator.next();

                  for (String updateColName : updateColNameList) {
                     Object value = PersistableHelper.getPropertyValue(item, updateColName);
                     if (value instanceof String && JdbcUtils.isBigData((String)value)) {
                        iterator.remove();
                        this.update(item, updateColNameList);
                        break;
                     }
                  }
               }

               if (itemList.isEmpty()) {
                  return;
               }
            }

            List<ID> idList = itemList.stream().map(i -> i.getId()).collect(Collectors.toList());
            boolean cacheEvictOverrided = this.isCacheEvictOverrided();
            boolean auditable = AuditLogHelper.auditable(AuditDatabaseOperation.UPDATE);
            boolean selectList = !ApplicationContextHelper.stressTesting()
               && (
                  AuditTrailHelper.auditTrail() && AuditTrailHelper.auditTrailable(this.getTable(), AuditDatabaseOperation.UPDATE)
                     || cacheEvictOverrided
                     || auditable
               );
            List<T> oldItemList = selectList ? this.selectListByIds(idList, new Order[0]) : null;
            int updateColQty = updateColNameList.size();
            int chunkSize = this.dialect.getInClauseIdMaxQty();
            int maxParamQty = this.dialect.getMaxParamQty();
            if (maxParamQty < chunkSize * (updateColQty * 2 + 1)) {
               chunkSize = maxParamQty / (updateColQty * 2 + 1);
            }

            ChunkIterator<T> chunkIterator = ListChunkIterator.of(itemList, chunkSize);

            while (chunkIterator.hasNext()) {
               List<T> chunkItemList = chunkIterator.nextChunk();
               if (!ApplicationContextHelper.stressTesting()
                  && AuditTrailHelper.auditTrail()
                  && AuditTrailHelper.auditTrailable(this.getTable(), AuditDatabaseOperation.UPDATE)) {
                  AuditTrailEntryData auditTrailEntryData = AuditTrailAspect.getAuditTrailEntryData();
                  if (auditTrailEntryData != null) {
                     String table = this.getTable();
                     Map<ID, List<CoreAuditTrailRecordLineBean>> map = new HashMap<>();

                     for (T newItem : chunkItemList) {
                        boolean deleteFile = false;
                        T oldItem = oldItemList.stream().filter(i -> i.getId().equals(newItem.getId())).findFirst().get();
                        List<CoreAuditTrailRecordLineBean> recordLineList = new ArrayList<>();

                        for (String updateColNamex : updateColNameList) {
                           if (AuditTrailHelper.auditTrailableColumn(table, updateColNamex)) {
                              Object oldValue = null;
                              Object newValue = null;
                              if (ReflectionUtils.findField(this.getType(), updateColNamex) == null) {
                                 oldValue = oldItem.getExt$().get(updateColNamex);
                                 newValue = newItem.getExt$().get(updateColNamex);
                              } else {
                                 oldValue = ReflectionUtils.invokeReadMethod(oldItem, updateColNamex);
                                 newValue = ReflectionUtils.invokeReadMethod(newItem, updateColNamex);
                              }

                              if (!ObjectUtils.equals(oldValue, newValue)) {
                                 CoreAuditTrailRecordLineBean line = new CoreAuditTrailRecordLineBean();
                                 line.setTargetId(table + "$" + oldItem.getId());
                                 line.setTableName(table);
                                 if (AuditTrailType.DELETE.equals(auditTrailEntryData.getEntry().value())
                                    && "TARGETID".equalsIgnoreCase(updateColNamex)
                                    && "T_CORE_FILE".equalsIgnoreCase(table)) {
                                    deleteFile = true;
                                    line.setRecordType(SqlType.DELETE.name());
                                    CoreFileBean file = (CoreFileBean)oldItem;
                                    line.setColumn("NAME");
                                    line.setOldValue(file.getName());
                                 } else {
                                    line.setRecordType(SqlType.UPDATE.name());
                                    line.setColumn(updateColNamex.toUpperCase());
                                    line.setOldValue(RestFieldValueTextContainer.getText(line.getColumn(), ObjectUtils.toString(oldValue)));
                                    line.setNewValue(RestFieldValueTextContainer.getText(line.getColumn(), ObjectUtils.toString(newValue)));
                                 }

                                 recordLineList.add(line);
                              }
                           }
                        }

                        if (!recordLineList.isEmpty()) {
                           if (deleteFile) {
                              String targetId = null;
                              CoreFileBean file = (CoreFileBean)oldItem;
                              int index = file.getTargetId().indexOf("$");
                              if (index > 0 && index < file.getTargetId().length()) {
                                 targetId = AuditTrailAspect.getAncientTargetId(file.getTargetId().substring(0, index), file.getTargetId().substring(index + 1));
                              } else {
                                 targetId = file.getTargetId();
                              }

                              AuditTrailAspect.addRecordLine(targetId, recordLineList);
                           } else {
                              map.put(newItem.getId(), recordLineList);
                           }
                        }
                     }

                     if (!map.isEmpty()) {
                        Map<ID, String> targetMap = AuditTrailAspect.getAncientTargetMap(table, new ArrayList<>(map.keySet()));
                        targetMap.forEach((k, v) -> AuditTrailAspect.addRecordLine(v, map.get(k)));
                     }
                  }
               }

               List<EntityContextInstance> contextInstanceList = chunkItemList.stream()
                  .map(i -> EntityContextInstance.updatable(context, (T)i))
                  .collect(Collectors.toList());
               this.preBatchUpdateByIds(context, contextInstanceList, updateColNameList);
               GlobalBoundSql updateBoundSql = this.dialect.getBatchUpdateByIdsSql(context, contextInstanceList, updateColNameList);
               this.globalMapper.updateByBoundSql(updateBoundSql);
            }

            if (!ApplicationContextHelper.stressTesting() && (cacheEvictOverrided || auditable)) {
               List<T> newItemList = this.selectListByIds(idList, new Order[0]);
               this.auditUpdate(oldItemList, newItemList);

               for (T newItem : newItemList) {
                  T oldItem = oldItemList.stream().filter(i -> i.getId().equals(newItem.getId())).findAny().orElse(null);
                  SunwayAopContext.<GenericDao>currentProxy().cacheEvict(oldItem, newItem);
               }
            }

            GenericService<T, ID> service = GikamBeanContext.getServiceByTable(this.getTable());
            if (service != null) {
               service.postUpdate(updateColNameList, idList);
            }
         }
      }
   }

   @Override
   public List<T> updateIfChanged(List<T> rawOrProxyItemList, List<String> ignoredColumnList) {
      if (rawOrProxyItemList != null && !rawOrProxyItemList.isEmpty()) {
         List<T> selectedItemList = this.selectListByIds(rawOrProxyItemList.stream().map(i -> i.getId()).collect(Collectors.toList()), new Order[0]);
         List<T> resultList = new ArrayList<>();
         Map<ID, List<CoreAuditTrailRecordLineBean>> map = new HashMap<>();
         boolean proxy = BeanPropertyListener.class.isAssignableFrom(rawOrProxyItemList.get(0).getClass());
         EntityContext context = this.getEntityContext();

         for (T rawOrProxyItem : rawOrProxyItemList) {
            T oldItem = selectedItemList.stream()
               .filter(i -> i.getId().equals(rawOrProxyItem.getId()))
               .findFirst()
               .orElseThrow(() -> new CheckedException("GIKAM.EXCEPTION.NO_VALUE_EXISTS"));
            T proxyItem = BeanUtils.getPropertyListenerProxy(ObjectUtils.clone(oldItem));
            if (proxy) {
               List<BeanPropertyEvent> eventList = ((BeanPropertyListener)rawOrProxyItem).getChangedPropertyEventList();
               if (eventList.isEmpty()) {
                  continue;
               }

               eventList.forEach(e -> e.getPropertyDescriptor().passValue(rawOrProxyItem, proxyItem));
               ((BeanPropertyListener)proxyItem)
                  .getChangedPropertyEventList()
                  .removeIf(e -> !StringUtils.isEmpty(e.getName()) && eventList.stream().noneMatch(re -> e.getName().equalsIgnoreCase(re.getName())));
            } else {
               context.getColumnContextList().forEach(c -> {
                  if (c.getPropertyDescriptor().isExt$BeanProperty()) {
                     if (rawOrProxyItem.getExt$().containsKey(c.getColumnName())) {
                        proxyItem.setExt$Item(c.getColumnName(), rawOrProxyItem.getExt$().get(c.getColumnName()));
                     }
                  } else {
                     Object value = c.getPropertyDescriptor().getPropertyValue(rawOrProxyItem);
                     if (value != null) {
                        c.getPropertyDescriptor().passValue(rawOrProxyItem, proxyItem);
                     }
                  }
               });
            }

            if (EntityHelper.isItemUpdated(proxyItem)) {
               EntityContextInstance proxyContextInstance = EntityContextInstance.updatable(context, proxyItem);
               if (!ObjectUtils.isEmpty(ignoredColumnList)) {
                  proxyContextInstance.getColumnContextInstanceList()
                     .forEach(
                        entityColumnContextInstance -> {
                           if (entityColumnContextInstance.isActive()
                              && CollectionUtils.containsIgnoreCase(ignoredColumnList, entityColumnContextInstance.getColumnContext().getColumnName())) {
                              entityColumnContextInstance.setActive(false);
                           }
                        }
                     );
                  if (proxyContextInstance.getColumnContextInstanceList()
                     .stream()
                     .noneMatch(entityColumnContextInstance -> entityColumnContextInstance.isActive())) {
                     continue;
                  }
               }

               if (!ApplicationContextHelper.stressTesting()
                  && AuditTrailHelper.auditTrail()
                  && AuditTrailHelper.auditTrailable(this.getTable(), AuditDatabaseOperation.UPDATE)) {
                  AuditTrailEntryData auditTrailEntryData = AuditTrailAspect.getAuditTrailEntryData();
                  if (auditTrailEntryData != null) {
                     String table = this.getTable();
                     List<CoreAuditTrailRecordLineBean> recordLineList = new ArrayList<>();
                     proxyContextInstance.getColumnContextInstanceList()
                        .stream()
                        .filter(c -> c.isActive() && AuditTrailHelper.auditTrailableColumn(table, c.getColumnContext().getColumnName()))
                        .forEach(c -> {
                           CoreAuditTrailRecordLineBean line = new CoreAuditTrailRecordLineBean();
                           line.setRecordType(SqlType.UPDATE.name());
                           line.setTargetId(table + "$" + oldItem.getId());
                           line.setTableName(table);
                           line.setColumn(c.getColumnContext().getColumnName().toUpperCase());
                           line.setOldValue(RestFieldValueTextContainer.getText(line.getColumn(), ObjectUtils.toString(c.getColumnContext().getValue(oldItem))));
                           line.setNewValue(RestFieldValueTextContainer.getText(line.getColumn(), ObjectUtils.toString(c.getValue())));
                           recordLineList.add(line);
                        });
                     if (!recordLineList.isEmpty()) {
                        map.put(oldItem.getId(), recordLineList);
                     }
                  }
               }

               this.preUpdate(proxyContextInstance);
               this.daoMapper.updateById(proxyContextInstance);
               if (!ApplicationContextHelper.stressTesting()) {
                  T newItem = this.selectById(oldItem.getId());
                  this.auditUpdate(Arrays.asList((T[])(new Persistable[]{oldItem})), Arrays.asList((T[])(new Persistable[]{newItem})));
                  SunwayAopContext.<GenericDao>currentProxy().cacheEvict(oldItem, newItem);
               }

               List<String> updatedColumnNameList = proxyContextInstance.getColumnContextInstanceList()
                  .stream()
                  .filter(c -> c.isActive())
                  .map(c -> c.getColumnContext().getColumnName())
                  .collect(Collectors.toList());
               GenericService<T, ID> service = GikamBeanContext.getServiceByTable(this.getTable());
               if (service != null) {
                  service.postUpdate(updatedColumnNameList, (List<ID>)Arrays.asList(proxyItem.getId()));
               }
            }
         }

         if (!ApplicationContextHelper.stressTesting() && !map.isEmpty()) {
            Map<ID, String> targetMap = AuditTrailAspect.getAncientTargetMap(this.getTable(), new ArrayList<>(map.keySet()));
            targetMap.forEach((k, v) -> AuditTrailAspect.addRecordLine(v, map.get(k)));
         }

         return resultList;
      } else {
         return CollectionUtils.emptyList();
      }
   }

   @Override
   public void updateCreatedBy(List<T> itemList) {
      if (!itemList.isEmpty()) {
         EntityContext context = this.getEntityContext();
         List<EntityColumnContext> createdByColumnList = context.getColumnContextList()
            .stream()
            .filter(c -> EntityHelper.isCreatedByColumn(c.getColumnName()))
            .collect(Collectors.toList());
         if (!createdByColumnList.isEmpty()) {
            List<T> oldItemList = this.selectListByIds(itemList.stream().map(i -> i.getId()).collect(Collectors.toList()), new Order[0]);
            List<EntityContextInstance> contextInstanceList = itemList.stream()
               .map(i -> EntityContextInstance.updatable(context, (T)i))
               .collect(Collectors.toList());
            this.daoMapper
               .batchUpdateByIds(context, contextInstanceList, createdByColumnList.stream().map(c -> c.getColumnName()).collect(Collectors.toList()));
            if (!ApplicationContextHelper.stressTesting()
               && AuditTrailHelper.auditTrail()
               && AuditTrailHelper.auditTrailable(this.getTable(), AuditDatabaseOperation.UPDATE)) {
               Map<ID, List<CoreAuditTrailRecordLineBean>> map = new HashMap<>();
               AuditTrailEntryData auditTrailEntryData = AuditTrailAspect.getAuditTrailEntryData();
               if (auditTrailEntryData != null) {
                  String table = this.getTable();

                  for (T item : itemList) {
                     List<CoreAuditTrailRecordLineBean> recordLineList = new ArrayList<>();
                     T oldItem = oldItemList.stream().filter(s -> s.getId().equals(item.getId())).findAny().get();

                     for (EntityColumnContext columnContext : createdByColumnList) {
                        CoreAuditTrailRecordLineBean line = new CoreAuditTrailRecordLineBean();
                        line.setRecordType(SqlType.UPDATE.name());
                        line.setTargetId(table + "$" + oldItem.getId());
                        line.setTableName(table);
                        line.setColumn(columnContext.getColumnName().toUpperCase());
                        line.setOldValue(RestFieldValueTextContainer.getText(line.getColumn(), ObjectUtils.toString(columnContext.getValue(oldItem))));
                        line.setNewValue(RestFieldValueTextContainer.getText(line.getColumn(), ObjectUtils.toString(columnContext.getValue(item))));
                        recordLineList.add(line);
                     }

                     if (!recordLineList.isEmpty()) {
                        map.put(oldItem.getId(), recordLineList);
                     }
                  }
               }

               if (!map.isEmpty()) {
                  Map<ID, String> targetMap = AuditTrailAspect.getAncientTargetMap(this.getTable(), new ArrayList<>(map.keySet()));
                  targetMap.forEach((k, v) -> AuditTrailAspect.addRecordLine(v, map.get(k)));
               }
            }

            if (!ApplicationContextHelper.stressTesting()) {
               this.auditUpdate(oldItemList, itemList);
            }

            GenericService<T, ID> service = GikamBeanContext.getServiceByTable(this.getTable());
            if (service != null) {
               service.postUpdate(
                  createdByColumnList.stream().map(c -> c.getColumnName()).collect(Collectors.toList()),
                  itemList.stream().map(i -> i.getId()).collect(Collectors.toList())
               );
            }
         }
      }
   }

   @Override
   public T selectByIdIfPresent(ID id) {
      List<Map<String, Object>> mapList = this.daoMapper
         .selectByIds(this.getEntityContext(), Arrays.asList(id), CollectionUtils.emptyList(), CollectionUtils.emptyList());
      return mapList.isEmpty() ? null : PersistableHelper.mapToPersistable(mapList.get(0), this.getType());
   }

   @Override
   public T selectOneIfPresent(T item, String... selectColNames) {
      List<Map<String, Object>> mapList = this.daoMapper
         .select(EntityContextInstance.instance(this.getEntityContext(), item), ArrayUtils.asList(selectColNames), CollectionUtils.emptyList());
      if (mapList.isEmpty()) {
         return null;
      } else if (mapList.size() > 1) {
         throw new NotUniqueResultJdbcException();
      } else {
         return PersistableHelper.mapToPersistable(mapList.get(0), this.getType());
      }
   }

   @Override
   public <V> V selectColumnById(ID id, String colName, Class<V> colType) {
      List<Map<String, Object>> mapList = this.daoMapper
         .selectByIds(this.getEntityContext(), Arrays.asList(id), ArrayUtils.asList(colName), CollectionUtils.emptyList());
      if (mapList.isEmpty()) {
         return null;
      } else if (mapList.size() > 1) {
         throw new NotUniqueResultJdbcException();
      } else {
         return ConvertUtils.convert(CollectionUtils.getFirstValue(mapList.get(0)), colType);
      }
   }

   @Override
   public <V> List<V> selectColumnList(T item, List<String> searchColNames, String colName, Class<V> colType, Order... orders) {
      EntityContextInstance contextInstance = EntityContextInstance.instance(this.getEntityContext(), item);
      if (searchColNames != null && !searchColNames.isEmpty()) {
         contextInstance.getColumnContextInstanceList()
            .forEach(c -> c.setActive(CollectionUtils.containsIgnoreCase(searchColNames, c.getColumnContext().getColumnName())));
      }

      List<Order> orderList = orders == null ? CollectionUtils.emptyList() : Arrays.asList(orders);
      List<Map<String, Object>> mapList = this.daoMapper.select(contextInstance, ArrayUtils.asList(colName), orderList);
      return mapList.stream().map(m -> ConvertUtils.convert(CollectionUtils.getFirstValue(m), colType)).collect(Collectors.toList());
   }

   @Override
   public List<T> selectAll(List<Order> orderList, String... selectColNames) {
      List<Map<String, Object>> mapList = this.daoMapper
         .select(EntityContextInstance.instance(this.getEntityContext(), ClassUtils.newInstance(this.getType())), ArrayUtils.asList(selectColNames), orderList);
      return mapList.stream().map(m -> PersistableHelper.mapToPersistable((Map<String, Object>)m, this.getType())).collect(Collectors.toList());
   }

   @Override
   public List<T> selectListByIds(List<ID> idList, List<String> selectColNameList, Order... orders) {
      List<Map<String, Object>> mapList = this.selectMapListByIdList(idList, selectColNameList, orders);
      return mapList.stream().map(m -> PersistableHelper.mapToPersistable((Map<String, Object>)m, this.getType())).collect(Collectors.toList());
   }

   @Override
   public <V> List<V> selectColumnsByIds(List<ID> idList, String colName, Class<V> colType, Order... orders) {
      List<Map<String, Object>> mapList = this.selectMapListByIdList(idList, Arrays.asList(colName), orders);
      return mapList.stream()
         .map(m -> CollectionUtils.getValueIgnorecase((Map<String, ?>)m, colName))
         .map(v -> ConvertUtils.convert(v, colType))
         .collect(Collectors.toList());
   }

   @Override
   public List<T> selectList(T item, List<String> searchColNames, List<String> selectColNameList, Order... orders) {
      EntityContextInstance contextInstance = EntityContextInstance.instance(this.getEntityContext(), item);
      if (searchColNames != null && !searchColNames.isEmpty()) {
         contextInstance.getColumnContextInstanceList()
            .forEach(c -> c.setActive(CollectionUtils.containsIgnoreCase(searchColNames, c.getColumnContext().getColumnName())));
      }

      if (selectColNameList == null) {
         selectColNameList = CollectionUtils.emptyList();
      }

      List<Map<String, Object>> mapList = this.daoMapper.select(contextInstance, selectColNameList, ArrayUtils.asList(orders));
      return mapList.stream().map(m -> PersistableHelper.mapToPersistable((Map<String, Object>)m, this.getType())).collect(Collectors.toList());
   }

   @Override
   public List<T> selectList(List<T> itemList, List<String> searchColNameList, List<String> selectColNameList, Order... orders) {
      List<Map<String, Object>> mapList = this.selectMapList(itemList, searchColNameList, selectColNameList, orders);
      return mapList.stream().map(m -> PersistableHelper.mapToPersistable((Map<String, Object>)m, this.getType())).collect(Collectors.toList());
   }

   @Override
   public <V> List<V> selectList(List<T> itemList, List<String> searchColNameList, String colName, Class<V> colType, Order... orders) {
      List<String> selectColNameList = new ArrayList<>();
      selectColNameList.add(colName);
      List<Map<String, Object>> mapList = this.selectMapList(itemList, searchColNameList, selectColNameList, orders);
      return mapList.stream().map(m -> ConvertUtils.convert(CollectionUtils.getValueIgnorecase(m, colName), colType)).collect(Collectors.toList());
   }

   @Override
   public T selectFirstIfPresent(T item, List<String> searchColNameList, List<String> selectColNameList, Order... orders) {
      EntityContextInstance contextInstance = EntityContextInstance.instance(this.getEntityContext(), item);
      if (searchColNameList != null && !searchColNameList.isEmpty()) {
         contextInstance.getColumnContextInstanceList()
            .forEach(c -> c.setActive(CollectionUtils.containsIgnoreCase(searchColNameList, c.getColumnContext().getColumnName())));
      }

      Map<String, Object> map = this.daoMapper.selectFirst(contextInstance, selectColNameList, ArrayUtils.asList(orders));
      return map == null ? null : PersistableHelper.mapToPersistable(map, this.getType());
   }

   @Override
   public List<T> selectUnionList(T item, List<String> searchColNameList, List<String> selectColNameList, Order... orders) {
      EntityContextInstance contextInstance = EntityContextInstance.instance(this.getEntityContext(), item);
      if (searchColNameList != null && !searchColNameList.isEmpty()) {
         contextInstance.getColumnContextInstanceList()
            .forEach(c -> c.setActive(CollectionUtils.containsIgnoreCase(searchColNameList, c.getColumnContext().getColumnName())));
      }

      List<Map<String, Object>> mapList = this.daoMapper.selectUnion(contextInstance, selectColNameList, ArrayUtils.asList(orders));
      return mapList.stream().map(m -> PersistableHelper.mapToPersistable((Map<String, Object>)m, this.getType())).collect(Collectors.toList());
   }

   @Override
   public List<T> selectListByOneColumnValues(String colName, List<T> itemList, List<String> selectColNames, Order... orders) {
      if (itemList != null && !itemList.isEmpty()) {
         EntityContext context = this.getEntityContext();
         List<EntityContextInstance> contextInstanceList = itemList.stream()
            .map(i -> EntityContextInstance.instance(context, (T)i))
            .collect(Collectors.toList());
         contextInstanceList.forEach(
            i -> i.getColumnContextInstanceList().forEach(c -> c.setActive(StringUtils.equalsIgnoreCase(colName, c.getColumnContext().getColumnName())))
         );
         List<String> cloneSelectColNames = selectColNames == null ? new ArrayList<>() : new ArrayList<>(selectColNames);
         List<Map<String, Object>> mapList = new ArrayList<>();
         mapList.addAll(this.daoMapper.selectByOneColumnValues(context, colName, contextInstanceList, cloneSelectColNames, ArrayUtils.asList(orders)));
         return mapList.stream().map(m -> PersistableHelper.mapToPersistable((Map<String, Object>)m, this.getType())).collect(Collectors.toList());
      } else {
         return CollectionUtils.emptyList();
      }
   }

   @Override
   public int countBy(T item, String... searchColNames) {
      EntityContextInstance contextInstance = EntityContextInstance.instance(this.getEntityContext(), item);
      if (!ArrayUtils.isEmpty(searchColNames)) {
         contextInstance.getColumnContextInstanceList()
            .forEach(c -> c.setActive(ArrayUtils.containsIgnoreCase(searchColNames, c.getColumnContext().getColumnName())));
      }

      return this.daoMapper.count(contextInstance);
   }

   @Override
   public int countBy(List<T> itemList, String... searchColNames) {
      EntityContext context = this.getEntityContext();
      List<EntityContextInstance> contextInstanceList = itemList.stream().map(i -> EntityContextInstance.instance(context, (T)i)).collect(Collectors.toList());
      if (!ArrayUtils.isEmpty(searchColNames)) {
         contextInstanceList.forEach(
            i -> i.getColumnContextInstanceList()
               .forEach(c -> c.setActive(ArrayUtils.containsIgnoreCase(searchColNames, c.getColumnContext().getColumnName())))
         );
      }

      return this.daoMapper.batchCount(context, contextInstanceList);
   }

   @Override
   public <V> Map<V, Long> groupCountByValueList(List<V> valueList, String searchColName) {
      List<Map<String, Object>> mapList = this.daoMapper.groupCountByValueList(this.getEntityContext(), valueList, searchColName);
      Map<V, Long> map = new HashMap<>();
      Class<V> vClass = (Class<V>)valueList.get(0).getClass();
      mapList.forEach(m -> {
         V key = ConvertUtils.convert(CollectionUtils.getValueIgnorecase((Map<String, ?>)m, searchColName), vClass);
         Long value = ConvertUtils.convert(CollectionUtils.getValueIgnorecase((Map<String, ?>)m, "QTY"), Long.class);
         map.put(key, value);
      });
      valueList.stream().filter(v -> !map.containsKey(v)).forEach(v -> {
         Long var10000 = map.put((V)v, 0L);
      });
      return map;
   }

   @Override
   public Page<T> selectPaginationByMybatis(String mybatisStatementId, Pageable pageable) {
      Map<String, Object> parameter = JSONObject.parseObject(pageable.getFilterJson(), new TypeReference<Map<String, Object>>() {}, new Feature[0]);
      PageRowBounds rowBounds = new PageRowBounds(pageable);
      return MybatisPageHelper.get(rowBounds, () -> this.mybatisTemplate.selectList(mybatisStatementId, parameter));
   }

   protected void preInsert(List<T> itemList) {
      boolean isOrderable = Orderable.class.isAssignableFrom(this.getType());
      List<BeanPropertyDescriptor> propertyDescriptorList = BeanPropertyHelper.getBeanPropertyDescriptorList(this.getType());
      List<BeanPropertyDescriptor> notNullPropertyDescriptorList = propertyDescriptorList.stream()
         .filter(p -> p.isAnnotationPresent(NotNull.class))
         .collect(Collectors.toList());
      BeanPropertyDescriptor createdByIdDescriptor = propertyDescriptorList.stream()
         .filter(p -> PersistableMetadataHelper.isCreatedByIdPropertyDescriptor(p))
         .findAny()
         .orElse(null);
      BeanPropertyDescriptor createdByNameDescriptor = propertyDescriptorList.stream()
         .filter(p -> PersistableMetadataHelper.isCreatedByNamePropertyDescriptor(p))
         .findAny()
         .orElse(null);
      BeanPropertyDescriptor createdByOrgIdDescriptor = propertyDescriptorList.stream()
         .filter(p -> PersistableMetadataHelper.isCreatedByOrgIdPropertyDescriptor(p))
         .findAny()
         .orElse(null);
      BeanPropertyDescriptor createdByOrgNameDescriptor = propertyDescriptorList.stream()
         .filter(p -> PersistableMetadataHelper.isCreatedByOrgNamePropertyDescriptor(p))
         .findAny()
         .orElse(null);
      BeanPropertyDescriptor createdTimeDescriptor = propertyDescriptorList.stream()
         .filter(p -> PersistableMetadataHelper.isCreatedTimePropertyDescriptor(p))
         .findAny()
         .orElse(null);
      String createdById = LocalContextHelper.getLoginUserId();
      String createdByName = LocalContextHelper.getLoginUserName();
      String createdByOrgId = LocalContextHelper.getLoginOrgId();
      String createdByOrgName = LocalContextHelper.getLoginOrgName();
      LocalDate nowDate = LocalDate.now();
      LocalDateTime nowTime = LocalDateTime.now();
      int i = 0;

      for (int j = itemList.size(); i < j; i++) {
         T item = itemList.get(i);
         if (createdByIdDescriptor != null && createdByIdDescriptor.getPropertyValue(item) == null) {
            createdByIdDescriptor.setPropertyValue(item, createdById);
         }

         if (createdByNameDescriptor != null && createdByNameDescriptor.getPropertyValue(item) == null) {
            createdByNameDescriptor.setPropertyValue(item, createdByName);
         }

         if (createdByOrgIdDescriptor != null && createdByOrgIdDescriptor.getPropertyValue(item) == null) {
            createdByOrgIdDescriptor.setPropertyValue(item, createdByOrgId);
         }

         if (createdByOrgNameDescriptor != null && createdByOrgNameDescriptor.getPropertyValue(item) == null) {
            createdByOrgNameDescriptor.setPropertyValue(item, createdByOrgName);
         }

         if (createdTimeDescriptor != null && createdTimeDescriptor.getPropertyValue(item) == null) {
            createdTimeDescriptor.setPropertyValue(item, nowTime);
         }

         if (!notNullPropertyDescriptorList.isEmpty()) {
            for (BeanPropertyDescriptor notNullPropertyDescriptor : notNullPropertyDescriptorList) {
               if (notNullPropertyDescriptor.getPropertyValue(item) == null) {
                  NotNull notNull = notNullPropertyDescriptor.getProperty().getAnnotation(NotNull.class);
                  if (notNull.defaultValue() != null) {
                     notNullPropertyDescriptor.setPropertyValue(item, notNull.defaultValue());
                  } else if (LocalDate.class.isAssignableFrom(notNullPropertyDescriptor.getPropertyType())) {
                     notNullPropertyDescriptor.setPropertyValue(item, nowDate);
                  } else if (LocalDateTime.class.isAssignableFrom(notNullPropertyDescriptor.getPropertyType())) {
                     notNullPropertyDescriptor.setPropertyValue(item, nowTime);
                  } else {
                     if (!Number.class.isAssignableFrom(notNullPropertyDescriptor.getPropertyType())) {
                        throw new NullPointerException();
                     }

                     notNullPropertyDescriptor.setPropertyValue(item, "0");
                  }
               }
            }
         }

         if (isOrderable) {
            Orderable<T> orderable = (Orderable<T>)item;
            if (orderable.getOrderNo() == null) {
               orderable.setOrderNo(ApplicationContextHelper.getNextOrderNo());
            }
         }
      }
   }

   protected void preUpdate(EntityContextInstance contextInstance) {
   }

   protected void preUpdateByIds(EntityContextInstance contextInstance, List<Object> idList) {
   }

   protected void preBatchUpdateByIds(EntityContext context, List<EntityContextInstance> contextInstanceList, List<String> updateColumnList) {
   }

   protected void afterDelete(List<T> items) {
      try {
         DaoAfterDeleteContext context = new DaoAfterDeleteContext(items, this.getTable(), this.getType());
         this.daoAfterDeleteSupport.afterDelete(context);
      } catch (Throwable var3) {
         log.warn("afterDelete failed", var3);
      }
   }

   protected void afterInsert(List<T> items) {
      try {
         DaoAfterInsertContext context = new DaoAfterInsertContext(items, this.getTable(), this.getType());
         this.daoAfterInsertSupport.afterInsert(context);
      } catch (Throwable var3) {
         log.warn("afterInsert failed", var3);
      }
   }

   private List<Map<String, Object>> selectMapListByIdList(List<ID> idList, List<String> selectColNames, Order... orders) {
      if (idList != null && !idList.isEmpty()) {
         List<String> cloneSelectColNames = selectColNames == null ? new ArrayList<>() : new ArrayList<>(selectColNames);
         boolean loop = idList.size() > this.dialect.getInClauseIdMaxQty();
         EntityContext context = this.getEntityContext();
         int columnQty = context.getColumnContextList().size();
         int loopItemQty = Math.min(this.dialect.getInClauseIdMaxQty(), this.dialect.getMaxParamQty() / columnQty);
         ListChunkIterator<ID> chunkIterator = ListChunkIterator.of(idList, loopItemQty);
         List<Map<String, Object>> mapList = new ArrayList<>();

         while (chunkIterator.hasNext()) {
            if (loop) {
               mapList.addAll(
                  this.daoMapper
                     .selectByIds(
                        context,
                        chunkIterator.nextChunk().stream().map(i -> (Object)i).collect(Collectors.toList()),
                        cloneSelectColNames,
                        CollectionUtils.emptyList()
                     )
               );
            } else {
               mapList.addAll(
                  this.daoMapper
                     .selectByIds(
                        context,
                        chunkIterator.nextChunk().stream().map(i -> (Object)i).collect(Collectors.toList()),
                        cloneSelectColNames,
                        ArrayUtils.asList(orders)
                     )
               );
            }
         }

         if (!mapList.isEmpty() && loop && ArrayUtils.hasElement(orders)) {
            this.sort(mapList, orders);
         }

         return mapList;
      } else {
         return CollectionUtils.emptyList();
      }
   }

   private List<Map<String, Object>> selectMapList(List<T> itemList, List<String> searchColNames, List<String> selectColNames, Order... orders) {
      if (itemList != null && !itemList.isEmpty()) {
         EntityContext context = this.getEntityContext();
         List<EntityContextInstance> contextInstanceList = itemList.stream()
            .map(i -> EntityContextInstance.instance(context, (T)i))
            .collect(Collectors.toList());
         if (searchColNames != null && !searchColNames.isEmpty()) {
            contextInstanceList.forEach(
               i -> i.getColumnContextInstanceList()
                  .forEach(c -> c.setActive(CollectionUtils.containsIgnoreCase(searchColNames, c.getColumnContext().getColumnName())))
            );
         }

         long activeQty = contextInstanceList.stream()
            .mapToLong(i -> i.getColumnContextInstanceList().stream().filter(c -> c.isActive()).count())
            .max()
            .getAsLong();
         boolean loop = activeQty * contextInstanceList.size() > this.dialect.getMaxParamQty();
         List<String> cloneSelectColNames = selectColNames == null ? new ArrayList<>() : new ArrayList<>(selectColNames);
         if (loop && ArrayUtils.hasElement(orders) && !cloneSelectColNames.isEmpty()) {
            for (Order order : orders) {
               if (!CollectionUtils.containsIgnoreCase(cloneSelectColNames, order.getColumn())) {
                  cloneSelectColNames.add(order.getColumn().toUpperCase());
               }
            }
         }

         int columnQty = context.getColumnContextList().size();
         int loopItemQty = Math.min(this.dialect.getMaxBatchInsertQty(), this.dialect.getMaxParamQty() / columnQty);
         ListChunkIterator<EntityContextInstance> chunkIterator = ListChunkIterator.of(contextInstanceList, loopItemQty);
         List<Map<String, Object>> mapList = new ArrayList<>();

         while (chunkIterator.hasNext()) {
            if (loop) {
               mapList.addAll(this.daoMapper.batchSelect(context, chunkIterator.nextChunk(), cloneSelectColNames, CollectionUtils.emptyList()));
            } else {
               mapList.addAll(this.daoMapper.batchSelect(context, chunkIterator.nextChunk(), cloneSelectColNames, ArrayUtils.asList(orders)));
            }
         }

         if (!mapList.isEmpty() && loop && ArrayUtils.hasElement(orders)) {
            this.sort(mapList, orders);
         }

         return mapList;
      } else {
         return CollectionUtils.emptyList();
      }
   }

   private void auditUpdate(List<T> selectedOldItemList, List<T> selectedNewItemList) {
      if (AuditLogHelper.auditable(AuditDatabaseOperation.UPDATE)) {
         for (T selectedOldItem : selectedOldItemList) {
            T selectedNewItem = selectedNewItemList.stream().filter(i -> selectedOldItem.getId().equals(i.getId())).findAny().orElse(null);
            AuditLogHelper.insertAuditLog(this.getTable(), selectedOldItem, selectedNewItem);
         }
      }
   }

   private void sort(List<Map<String, Object>> mapList, Order... orders) {
      Collections.sort(mapList, (m1, m2) -> {
         for (Order order : orders) {
            Comparable v1 = (Comparable)CollectionUtils.getValueIgnorecase((Map<String, ?>)m1, order.getColumn());
            Comparable v2 = (Comparable)CollectionUtils.getValueIgnorecase((Map<String, ?>)m2, order.getColumn());
            if (v1 == null) {
               if (v2 != null) {
                  if (StringUtils.endsWith(order.getDirection(), "ASC")) {
                     return -1;
                  }

                  return 1;
               }
            } else {
               if (v2 == null) {
                  if (StringUtils.endsWith(order.getDirection(), "ASC")) {
                     return 1;
                  }

                  return -1;
               }

               int cv = v1.compareTo(v2);
               if (cv != 0) {
                  if (StringUtils.endsWith(order.getDirection(), "ASC")) {
                     return cv;
                  }

                  return -1 * cv;
               }
            }
         }

         return 0;
      });
   }

   private boolean isCacheEvictOverrided() {
      GenericDao<T, ID> proxy = SunwayAopContext.currentProxy();
      Class<?> clazz = ClassUtils.getRawType(proxy.getClass());
      Method method = ReflectionUtils.getUniqueMethodByName(clazz, "cacheEvict");
      return !GenericDao.class.equals(method.getDeclaringClass());
   }
}
