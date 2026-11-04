package com.sunwayworld.module.sys.metadata.service.impl;

import com.sunwayworld.framework.at.AuditTrailHelper;
import com.sunwayworld.framework.at.aspect.AuditTrailAspect;
import com.sunwayworld.framework.at.aspect.AuditTrailAspect.AuditTrailEntryData;
import com.sunwayworld.framework.audit.AuditDatabaseOperation;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.GikamBeanContext;
import com.sunwayworld.framework.data.ChunkIterator;
import com.sunwayworld.framework.data.ListChunkIterator;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.context.ColumnContext;
import com.sunwayworld.framework.database.context.EntityColumnContext;
import com.sunwayworld.framework.database.context.EntityContext;
import com.sunwayworld.framework.database.core.DatabaseManager;
import com.sunwayworld.framework.database.dialect.Dialect;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.database.sql.SqlType;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.framework.support.base.dao.MapDao;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineBean;
import com.sunwayworld.module.sys.bpmn.CoreBpmnHelper;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnCommentBean;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnCommentService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnTargetService;
import com.sunwayworld.module.sys.metadata.bean.CoreParentTableInstanceDTO;
import com.sunwayworld.module.sys.metadata.bean.CoreTableBean;
import com.sunwayworld.module.sys.metadata.bean.CoreTableFieldDTO;
import com.sunwayworld.module.sys.metadata.bean.CoreTableHierarchyBean;
import com.sunwayworld.module.sys.metadata.bean.CoreTablePermissionBean;
import com.sunwayworld.module.sys.metadata.dao.CoreTableDao;
import com.sunwayworld.module.sys.metadata.dao.CoreTableHierarchyDao;
import com.sunwayworld.module.sys.metadata.dao.CoreTablePermissionDao;
import com.sunwayworld.module.sys.metadata.mapper.CoreMetadataMapper;
import com.sunwayworld.module.sys.metadata.service.CoreTableService;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreTableServiceImpl implements CoreTableService {
   @Autowired
   private CoreTableDao tableDao;
   @Autowired
   private CoreTableHierarchyDao tableHierarchyDao;
   @Autowired
   private CoreTablePermissionDao tablePermissionDao;
   @Autowired
   private CoreFileService fileService;
   @Autowired
   private CoreMetadataMapper metadataMapper;
   @Autowired
   @Lazy
   private CoreTableService proxyInstance;
   @Autowired
   private CoreBpmnCommentService commentService;
   @Autowired
   private CoreBpmnTargetService bpmnTargetService;
   @Autowired
   private Dialect dialect;

   public CoreTableDao getDao() {
      return this.tableDao;
   }

   @Override
   public List<CoreParentTableInstanceDTO> selectAncientTableInstanceList(String table, List<? extends Serializable> idList) {
      return this.selectAncientList(table, idList.stream().map(i -> Pair.of(i, ConvertUtils.convert(i, String.class))).collect(Collectors.toList()));
   }

   @Override
   public Map<? extends Serializable, String> selectMasterMap(CoreTableHierarchyBean tableHierarchy, List<? extends Serializable> idList) {
      if (idList.isEmpty()) {
         return CollectionUtils.emptyMap();
      } else {
         int chunkSize = this.dialect.getInClauseIdMaxQty();
         ChunkIterator<? extends Serializable> chunkIterator = ListChunkIterator.of(idList, chunkSize);
         List<Map<String, Object>> mapList = new ArrayList<>();

         while (chunkIterator.hasNext()) {
            mapList.addAll(this.metadataMapper.selectMasterMap(tableHierarchy.getTableName(), tableHierarchy.getReferColumn(), chunkIterator.nextChunk()));
         }

         return mapList.stream()
            .collect(
               Collectors.toMap(
                  m -> (Serializable)CollectionUtils.getValueIgnorecase((Map<String, ?>)m, "ID"),
                  m -> ObjectUtils.toString(CollectionUtils.getValueIgnorecase((Map<String, ?>)m, "MASTERID"))
               )
            );
      }
   }

   @Transactional
   @Override
   public <ID extends Serializable> void deleteCascade(String table, ID id) {
      table = table.toUpperCase();
      if (AuditTrailHelper.auditTrail() && AuditTrailHelper.auditTrailable(table, AuditDatabaseOperation.DELETE)) {
         AuditTrailEntryData auditTrailEntryData = AuditTrailAspect.getAuditTrailEntryData();
         if (auditTrailEntryData != null) {
            GenericService service = GikamBeanContext.getServiceByTable(table);
            EntityContext entityContext = service.getDao().getEntityContext();
            Object item = service.selectById(ConvertUtils.convert(id, (Class<ID>)entityContext.getIdContext().getType()));
            List<EntityColumnContext> columnContextList = entityContext.getColumnContextList();
            String targetId = AuditTrailAspect.getAncientTargetId(table, id);

            for (EntityColumnContext columnContext : columnContextList) {
               if (AuditTrailHelper.auditTrailableColumn(table, columnContext.getColumnName())) {
                  CoreAuditTrailRecordLineBean line = new CoreAuditTrailRecordLineBean();
                  line.setRecordType(SqlType.DELETE.name());
                  line.setTargetId(table + "$" + id);
                  line.setTableName(table);
                  line.setColumn(columnContext.getColumnName().toUpperCase());
                  line.setOldValue(ObjectUtils.toString(columnContext.getValue(item)));
                  AuditTrailAspect.addRecordLine(targetId, line);
               }
            }
         }
      }

      List<CoreTableHierarchyBean> tableHierarchyList = this.proxyInstance.selectTableHierarchyByMaster(table);
      if (!tableHierarchyList.isEmpty()) {
         for (CoreTableHierarchyBean tableHierarchy : tableHierarchyList) {
            CoreTableBean coreTable = this.proxyInstance.selectTable(tableHierarchy.getTableName());
            List<String> childIdList = this.metadataMapper.selectIdByMaster(coreTable.getId(), tableHierarchy.getReferColumn(), id);
            this.proxyInstance.batchCascadeDelete(coreTable.getId(), childIdList);
         }
      }

      CoreTableBean coreTable = this.proxyInstance.selectTable(table);
      if (coreTable != null) {
         if ("1".equals(coreTable.getAuditable())) {
            CoreBpmnCommentBean filter = new CoreBpmnCommentBean();
            filter.setTargetId(CoreBpmnHelper.getTargetId(table, id));
            this.commentService.getDao().deleteBy(filter, "TARGETID");
            this.bpmnTargetService.delete(filter.getTargetId());
         }

         if ("1".equals(coreTable.getAttachment())) {
            this.fileService.delete(coreTable.getId(), id);
         }
      }

      if (null != GikamBeanContext.getServiceByTable(table)) {
         GenericDao<?, ?> dao = GikamBeanContext.getServiceByTable(table).getDao();
         if (dao != null) {
            Object convertId = ConvertUtils.convert(id, dao.getEntityContext().getIdContext().getType());
            Class<?> daoClazz = ClassUtils.getRawType(dao.getClass());
            Method deleteMethod = ReflectionUtils.getUniqueMethodByName(daoClazz, "delete");
            ReflectionUtils.invokeMethod(deleteMethod, dao, convertId);
         }
      } else {
         ApplicationContextHelper.getBean(MapDao.class).deleteByIdList(table, Arrays.asList(id));
      }
   }

   @Transactional
   @Override
   public <ID extends Serializable> void batchCascadeDelete(String table, List<ID> idList) {
      if (!CollectionUtils.isEmpty(idList)) {
         String upperTable = table.toUpperCase();
         if (AuditTrailHelper.auditTrail() && AuditTrailHelper.auditTrailable(upperTable, AuditDatabaseOperation.DELETE)) {
            AuditTrailEntryData auditTrailEntryData = AuditTrailAspect.getAuditTrailEntryData();
            if (auditTrailEntryData != null) {
               GenericService service = GikamBeanContext.getServiceByTable(upperTable);
               EntityContext entityContext = service.getDao().getEntityContext();
               List<Object> itemList = service.selectListByIds(idList);
               List<EntityColumnContext> columnContextList = entityContext.getColumnContextList();
               EntityColumnContext idContext = entityContext.getIdContext();
               List<EntityColumnContext> auditColumnList = columnContextList.stream()
                  .filter(columnContext -> AuditTrailHelper.auditTrailableColumn(upperTable, columnContext.getColumnName()))
                  .collect(Collectors.toList());
               itemList.forEach(item -> {
                  String id = ObjectUtils.toString(idContext.getValue(item));
                  String targetId = AuditTrailAspect.getAncientTargetId(upperTable, id);
                  List<CoreAuditTrailRecordLineBean> recordLineBeanList = auditColumnList.stream().map(columnContext -> {
                     CoreAuditTrailRecordLineBean line = new CoreAuditTrailRecordLineBean();
                     line.setRecordType(SqlType.DELETE.name());
                     line.setTargetId(upperTable + "$" + id);
                     line.setTableName(upperTable);
                     line.setColumn(columnContext.getColumnName().toUpperCase());
                     line.setOldValue(ObjectUtils.toString(columnContext.getValue(item)));
                     return line;
                  }).collect(Collectors.toList());
                  AuditTrailAspect.addRecordLine(targetId, recordLineBeanList);
               });
            }
         }

         List<CoreTableHierarchyBean> tableHierarchyList = this.proxyInstance.selectTableHierarchyByMaster(upperTable);
         if (!tableHierarchyList.isEmpty()) {
            for (CoreTableHierarchyBean tableHierarchy : tableHierarchyList) {
               CoreTableBean coreTable = this.proxyInstance.selectTable(tableHierarchy.getTableName());
               List<String> childIdList = this.metadataMapper.selectIdByMasterIdList(coreTable.getId(), tableHierarchy.getReferColumn(), idList);
               this.proxyInstance.batchCascadeDelete(coreTable.getId(), childIdList);
            }
         }

         CoreTableBean coreTable = this.proxyInstance.selectTable(upperTable);
         if (coreTable != null) {
            if ("1".equals(coreTable.getAuditable())) {
               List<CoreBpmnCommentBean> deleteCommentList = idList.stream().map(id -> {
                  CoreBpmnCommentBean commentBean = new CoreBpmnCommentBean();
                  commentBean.setTargetId(CoreBpmnHelper.getTargetId(upperTable, (ID)id));
                  return commentBean;
               }).collect(Collectors.toList());
               this.commentService.getDao().deleteBy(deleteCommentList, "TARGETID");
               List<String> targetList = deleteCommentList.stream().map(CoreBpmnCommentBean::getTargetId).collect(Collectors.toList());
               this.bpmnTargetService.getDao().deleteByIdList(targetList);
            }

            if ("1".equals(coreTable.getAttachment())) {
               this.fileService.batchDelete(upperTable, idList);
            }
         }

         if (null != GikamBeanContext.getServiceByTable(upperTable)) {
            GenericDao<?, ?> dao = GikamBeanContext.getServiceByTable(upperTable).getDao();
            if (dao != null) {
               Class<?> daoClazz = ClassUtils.getRawType(dao.getClass());
               Method deleteMethod = ReflectionUtils.getUniqueMethodByName(daoClazz, "deleteByIdList");
               ReflectionUtils.invokeMethod(deleteMethod, dao, idList);
            }
         } else {
            ApplicationContextHelper.getBean(MapDao.class).deleteByIdList(upperTable, idList);
         }
      }
   }

   @Cacheable(
      value = {"T_CORE_TABLE_HIERARCHY.MASTERTABLE"},
      key = "#masterTable"
   )
   @Override
   public List<CoreTableHierarchyBean> selectTableHierarchyByMaster(String masterTable) {
      CoreTableHierarchyBean terms = new CoreTableHierarchyBean();
      terms.setMasterTableName(masterTable);
      return this.tableHierarchyDao.selectList(terms, new Order[]{Order.desc("ID")});
   }

   @Cacheable(
      value = {"T_CORE_TABLE_HIERARCHY.TABLENAME"},
      key = "#table"
   )
   @Override
   public List<CoreTableHierarchyBean> selectTableHierarchy(String table) {
      CoreTableHierarchyBean filter = new CoreTableHierarchyBean();
      filter.setTableName(table);
      return this.tableHierarchyDao.selectList(filter, new Order[0]);
   }

   @Override
   public <ID> CoreTableHierarchyBean selectTableHierarchy(String table, ID id) {
      List<CoreTableHierarchyBean> tableHierarchyList = this.proxyInstance.selectTableHierarchy(table);
      if (tableHierarchyList.isEmpty()) {
         return null;
      } else if (tableHierarchyList.size() == 1) {
         return tableHierarchyList.get(0);
      } else {
         for (CoreTableHierarchyBean tableHierarchy : tableHierarchyList) {
            if (this.metadataMapper.countMaster(tableHierarchy.getTableName(), tableHierarchy.getMasterTableName(), tableHierarchy.getReferColumn(), id) > 0L) {
               return tableHierarchy;
            }
         }

         return null;
      }
   }

   @Cacheable(
      value = {"T_CORE_TABLE_PERMISSION.BY_TABLE_CATEGORY"},
      key = "#table + '.' + #category",
      unless = "#result == null"
   )
   @Override
   public CoreTablePermissionBean selectTablePermission(String table, String category) {
      CoreTablePermissionBean tablePermission = new CoreTablePermissionBean();
      tablePermission.setTableName(table);
      tablePermission.setCategory(category);
      return this.tablePermissionDao.selectOneIfPresent(tablePermission, new String[0]);
   }

   @Override
   public CoreTableBean selectTable(String table) {
      return this.getDao().selectByIdIfPresent(table);
   }

   @Override
   public Page<ColumnContext> selectTableColumnList(String table, RestJsonWrapperBean wrapper) {
      return new Pagination<>(DatabaseManager.getTableColumnContextList(table));
   }

   @Override
   public List<CoreTableFieldDTO> selectFieldListByTableName(String tableName) {
      MapperParameter parameter = new MapperParameter();
      parameter.setFilter(SearchFilter.instance().match("TABLENAME", tableName).filter(MatchPattern.SEQ));
      return this.getDao()
         .selectFieldListByCondition(parameter)
         .stream()
         .map(field -> PersistableHelper.mapToPersistable((Map<String, Object>)field, CoreTableFieldDTO.class))
         .collect(Collectors.toList());
   }

   private List<CoreParentTableInstanceDTO> selectAncientList(String table, List<Pair<?, String>> pairList) {
      CoreTableHierarchyBean tableHierarchy = this.proxyInstance.selectTableHierarchy(table, pairList.get(0).getSecond());
      if (tableHierarchy == null) {
         List<CoreParentTableInstanceDTO> instanceList = new ArrayList<>();
         pairList.forEach(p -> {
            CoreParentTableInstanceDTO instance = new CoreParentTableInstanceDTO();
            instance.setSourceId(ConvertUtils.convert(p.getFirst(), String.class));
            instance.setTable(table);
            instance.setId(p.getSecond());
            instanceList.add(instance);
         });
         return instanceList;
      } else {
         Map<? extends Serializable, String> masterMap = this.selectMasterMap(
            tableHierarchy, pairList.stream().map(p -> p.getSecond()).distinct().collect(Collectors.toList())
         );
         masterMap.forEach((k, v) -> pairList.stream().filter(p -> p.getSecond().equals(ConvertUtils.convert(k, String.class))).findAny().get().setSecond(v));
         return this.selectAncientList(tableHierarchy.getMasterTableName(), pairList);
      }
   }
}
