package com.sunwayworld.framework.support.base.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.beans.BeanPropertyHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.PageRequest;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.NotImplementedException;
import com.sunwayworld.framework.exception.ReflectionException;
import com.sunwayworld.framework.exception.UnsupportedException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.mybatis.MybatisHelper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.bean.CoreControllerRequestEntryDTO;
import com.sunwayworld.framework.support.base.bean.CoreServiceStatusDTO;
import com.sunwayworld.framework.support.base.bean.CoreServiceStatusDTO.ActiveStatus;
import com.sunwayworld.framework.support.base.bean.CoreServiceStatusDTO.AuditStatus;
import com.sunwayworld.framework.support.base.bean.CoreServiceStatusDTO.SuspendStatus;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import com.sunwayworld.framework.support.domain.Activatable;
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.support.domain.Suspendable;
import com.sunwayworld.framework.support.matrix.MatrixHelper;
import com.sunwayworld.framework.support.matrix.service.GenericMatrixService;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.utils.ReflectionUtils.MethodWrapper;
import com.sunwayworld.module.admin.page.bean.CoreAdminPageConfigBean;
import com.sunwayworld.module.admin.page.service.CoreAdminPageConfigService;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskBean;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import com.sunwayworld.module.sys.bpmn.engine.CoreBpmnRuntimeService;
import com.sunwayworld.module.sys.bpmn.exception.BpmnException;
import com.sunwayworld.module.sys.metadata.service.CoreTableService;
import com.sunwayworld.module.sys.page.bean.CorePageViewConfigBean;
import com.sunwayworld.module.sys.page.service.CorePageViewConfigService;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

public interface GenericService<T extends Persistable<ID>, ID extends Serializable> extends GenericDownloadableService {
   <D extends GenericDao<T, ID>> D getDao();

   @Transactional(readOnly = true)
   default List<T> selectAll() {
      return this.getDao().selectAll();
   }

   @Transactional(readOnly = true)
   default List<T> selectListByIds(List<ID> idList) {
      return this.<GenericDao<T, ID>>getDao().selectListByIds(idList);
   }

   @Transactional(readOnly = true)
   default List<T> selectList(T item, Order... orders) {
      return this.getDao().selectList(item, orders);
   }

   @Transactional(readOnly = true)
   default T selectDetail(ID id) {
      Map<String, Object> map = this.getDao().selectDetail(id);
      return map != null && !map.isEmpty() ? PersistableHelper.mapToPersistable(map, this.getDao().getType()) : null;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   default void delete(RestJsonWrapperBean wrapper) {
      List<T> deleteList = wrapper.parse(this.getDao().getType());
      if (!deleteList.isEmpty()) {
         Class<T> itemType = this.getDao().getType();
         CoreTableService tableService = ApplicationContextHelper.getBean(CoreTableService.class);
         List<ID> deleteIdList = deleteList.stream().map(Persistable::getId).collect(Collectors.toList());
         tableService.batchCascadeDelete(PersistableMetadataHelper.getTableName(itemType), deleteIdList);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   default void delete(ID id) {
      this.getDao().delete(id);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   default void deleteCascadeById(ID id) {
      CoreTableService tableService = ApplicationContextHelper.getBean(CoreTableService.class);
      tableService.deleteCascade(PersistableMetadataHelper.getTableName(this.getDao().getType()), id);
   }

   @Transactional(readOnly = true)
   default T selectById(ID id) {
      return (T)this.getDao().selectById(id);
   }

   @Transactional(readOnly = true)
   default T selectByIdIfPresent(ID id) {
      return (T)this.getDao().selectByIdIfPresent(id);
   }

   @Transactional(readOnly = true)
   default String selectColumnById(ID id, String column) {
      return this.getDao().selectColumnById(id, column, String.class);
   }

   @Transactional(readOnly = true)
   default <V> V selectColumnById(ID id, String column, Class<V> columnType) {
      return this.getDao().selectColumnById(id, column, columnType);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   default ID insert(RestJsonWrapperBean wrapper) {
      throw new NotImplementedException();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.INSTANT_SAVE")
   default void instantSave(RestJsonWrapperBean wrapper) {
      List<GenericService<?, ?>> serviceList = wrapper.getBodyServiceList();
      int size = serviceList.size();

      for (int i = 0; i < size; i++) {
         GenericService service = serviceList.get(i);
         if (service != null) {
            if (GenericMatrixService.class.isAssignableFrom(ClassUtils.getRawType(service.getClass()))) {
               List<String> contentList = wrapper.getBodyList().get(i);
               if (MatrixHelper.isMatrixContent(contentList)) {
                  ((GenericMatrixService)service).save(contentList.stream().map(c -> JSON.parseObject(c)).collect(Collectors.toList()));
                  continue;
               }
            }

            service.updateIfChanged(wrapper.parse(service.getDao().getType(), i));
         } else {
            this.updateIfChanged(wrapper.parse(this.getDao().getType(), i));
         }
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   default void save(ID id, RestJsonWrapperBean wrapper) {
      List<GenericService<?, ?>> serviceList = wrapper.getBodyServiceList();
      int size = serviceList.size();

      for (int i = 0; i < size; i++) {
         GenericService service = serviceList.get(i);
         if (service == null) {
            List<T> itemList = wrapper.parse(this.getDao().getType(), i);
            if (size == 1 && itemList.size() == 1) {
               T item = itemList.get(0);
               if (item.getId() == null) {
                  item.setId(id);
               }
            }

            this.updateIfChanged(itemList);
         } else {
            List<Persistable<?>> itemList = wrapper.parse(service.<GenericDao<T, ?>>getDao().getType(), i);
            if (size == 1 && itemList.size() == 1) {
               Persistable item = itemList.get(0);
               if (item.getId() == null) {
                  item.setId(ConvertUtils.convert(id, service.getDao().getEntityContext().getIdContext().getType()));
               }
            }

            service.updateIfChanged(itemList);
         }
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   default void updateIfChanged(T item) {
      this.getDao().updateIfChanged(item);
   }

   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   default void update(T proxy) {
      this.getDao().update(proxy);
   }

   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   default void updateIfChanged(List<T> itemList) {
      this.<GenericDao<T, ID>>getDao().updateIfChanged(itemList);
   }

   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   default void updateCreatedBy(T item) {
      this.getDao().updateCreatedBy(item);
   }

   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   default void updateCreatedBy(List<T> itemList) {
      this.<GenericDao<T, ID>>getDao().updateCreatedBy(itemList);
   }

   @Transactional(readOnly = true)
   default Page<T> selectPagination(MapperParameter parameter, PageRowBounds rowBounds) {
      return this.selectPagination(() -> this.getDao().selectByCondition(parameter), rowBounds);
   }

   @Transactional(readOnly = true)
   default <V> Page<T> selectPagination(Supplier<List<V>> mapOrItemSupplier, PageRowBounds rowBounds) {
      Page<V> page = MybatisPageHelper.get(rowBounds, mapOrItemSupplier);
      if (page.getRows().isEmpty()) {
         return new Pagination<>(page, CollectionUtils.emptyList());
      } else {
         Class<V> clazz = (Class<V>)ClassUtils.getRawType(page.getRows().get(0).getClass());
         if (Map.class.isAssignableFrom(clazz)) {
            List<T> itemList = page.getRows()
               .stream()
               .map(m -> PersistableHelper.mapToPersistable((Map<String, Object>)m, this.getDao().getType()))
               .collect(Collectors.toList());
            return new Pagination<>(page, itemList);
         } else if (this.getDao().getType().isAssignableFrom(clazz)) {
            return (Page<T>)(Page<?>)page;
         } else {
            throw new UnsupportedException();
         }
      }
   }

   @Transactional(readOnly = true)
   default Page<T> selectPagination(MapperParameter parameter) {
      return this.selectPagination(parameter, null);
   }

   @Transactional(readOnly = true)
   default Page<T> selectPagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      return this.selectPagination(parameter, rowBounds);
   }

   @Transactional(readOnly = true)
   default Page<T> selectPaginationByFilter(SearchFilter filter, RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      parameter.setFilter(filter);
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      return this.selectPagination(parameter, rowBounds);
   }

   @Transactional(readOnly = true)
   default Page<T> selectRawPagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      Class<T> type = this.getDao().getType();
      if (Auditable.class.isAssignableFrom(type)) {
         parameter.setProcessStatus("PROCESSSTATUS", ProcessStatus.DRAFT);
      }

      parameter.setRawQueries();
      BeanPropertyDescriptor createdByOrgIdPropertyDescriptor = PersistableMetadataHelper.getCreatedByOrgIdPropertyDescriptor(type);
      if (createdByOrgIdPropertyDescriptor != null) {
         parameter.setOrgAuthority();
      }

      if (!wrapper.getAuthorityList().isEmpty()) {
         wrapper.getAuthorityList().forEach(a -> parameter.setAuthorityParameter(a));
      }

      return this.selectPagination(parameter, rowBounds);
   }

   @Transactional(readOnly = true)
   default Page<T> selectByRole(Long roleId, RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      parameter.setSelectByRoleIdParam(roleId);
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      return this.selectPagination(parameter, rowBounds);
   }

   @Transactional(readOnly = true)
   default Page<T> selectSearchablePagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      Class<T> type = this.getDao().getType();
      if (Auditable.class.isAssignableFrom(type)) {
         parameter.setProcessStatus("PROCESSSTATUS", ProcessStatus.APPROVE_OR_DONE);
      }

      parameter.setSearchableQueries();
      BeanPropertyDescriptor createdByOrgIdPropertyDescriptor = PersistableMetadataHelper.getCreatedByOrgIdPropertyDescriptor(type);
      if (createdByOrgIdPropertyDescriptor != null) {
         parameter.setOrgAuthority();
      }

      if (!wrapper.getAuthorityList().isEmpty()) {
         wrapper.getAuthorityList().forEach(a -> parameter.setAuthorityParameter(a));
      }

      return this.selectPagination(parameter, rowBounds);
   }

   default RestValidationResultBean validate(ID id, RestJsonWrapperBean jsonWrapper) {
      return new RestValidationResultBean(true);
   }

   default RestValidationResultBean validateUnique(RestJsonWrapperBean wrapper) {
      String validateUniqueFieldJson = wrapper.getParamValue("vu");
      if (null == validateUniqueFieldJson) {
         return new RestValidationResultBean(true);
      } else {
         Map<String, Object> validateUniqueFieldMap = JSONObject.parseObject(
            validateUniqueFieldJson, new TypeReference<Map<String, Object>>() {}, new Feature[0]
         );
         Map<String, Object> columnMap = new CaseInsensitiveLinkedMap<>(validateUniqueFieldMap);
         ID id = ConvertUtils.convert(columnMap.get("id"), (Class<ID>)this.getDao().getEntityContext().getIdContext().getType());
         columnMap.remove("id");
         return this.validateUnique(id, columnMap);
      }
   }

   default RestValidationResultBean validateUnique(ID id, String columnName, String columnValue) {
      Map<String, Object> columnMap = new CaseInsensitiveLinkedMap<>();
      columnMap.put(columnName, columnValue);
      return this.validateUnique(id, columnMap);
   }

   default RestValidationResultBean validateUnique(ID id, Map<String, Object> columnMap) {
      Class<T> type = this.getDao().getType();
      if (columnMap == null
         || columnMap.isEmpty()
         || columnMap.size() == 1 && columnMap.containsKey("ID")
         || columnMap.size() == 1 && columnMap.containsKey(null)) {
         T item = (T)this.getDao().selectByIdIfPresent(id);
         return item == null
            ? new RestValidationResultBean(true)
            : new RestValidationResultBean(false, I18nHelper.getMessage("core.validator.column.not-unique"));
      } else {
         T searchTerms = ClassUtils.newInstance(type);
         columnMap.forEach((k, v) -> {
            BeanPropertyDescriptor propertyDescriptor = BeanPropertyHelper.getBeanPropertyDescriptor(type, k);
            if (propertyDescriptor == null) {
               searchTerms.setExt$Item(k, v == null ? null : v.toString());
            } else {
               ReflectionUtils.invokeMethod(propertyDescriptor.getWriteMethod(), searchTerms, ConvertUtils.convert(v, propertyDescriptor.getPropertyType()));
            }
         });
         List<ID> idList = this.getDao().selectColumnList(searchTerms, "ID", (Class<ID>)this.getDao().getEntityContext().getIdContext().getType());
         if (idList.isEmpty()) {
            return new RestValidationResultBean(true);
         } else if (idList.size() > 1) {
            return new RestValidationResultBean(false, I18nHelper.getMessage("core.validator.column.not-unique"));
         } else {
            return !idList.get(0).equals(id)
               ? new RestValidationResultBean(false, I18nHelper.getMessage("core.validator.column.not-unique"))
               : new RestValidationResultBean(true);
         }
      }
   }

   default boolean isAvailableByCurrentUser(ID id) {
      Map<String, Object> map = new HashMap<>();
      map.put("id_SEQ", id);
      RestJsonWrapperBean wrapper = new RestJsonWrapperBean(JSONObject.toJSONString(map));
      wrapper.removePageParams();
      Page<T> rawPage = this.selectRawPagination(wrapper);
      if (rawPage.getNumberOfElements() > 0) {
         return true;
      } else {
         Page<T> searchPage = this.selectSearchablePagination(wrapper);
         return searchPage.getNumberOfElements() > 0;
      }
   }

   default boolean isUpdatedByCurrentUser(ID id) {
      Map<String, Object> map = new HashMap<>();
      map.put("id_SEQ", id);
      RestJsonWrapperBean wrapper = new RestJsonWrapperBean(JSONObject.toJSONString(map));
      wrapper.removePageParams();
      Page<T> page = this.selectRawPagination(wrapper);
      return page.getNumberOfElements() > 0;
   }

   @Transactional(readOnly = true)
   default CoreServiceStatusDTO selectStatus(ID id, String currentStatusCode) {
      CoreServiceStatusDTO status = new CoreServiceStatusDTO();
      T item = (T)(id == null ? null : this.getDao().selectByIdIfPresent(id));
      if (Activatable.class.isAssignableFrom(this.getDao().getType())) {
         ActiveStatus activeStatus = new ActiveStatus();
         if (item != null) {
            activeStatus.setActivatedFlag(((Activatable)item).getActivatedFlag());
         } else {
            activeStatus.setActivatedFlag("0");
         }

         status.setActiveStatus(activeStatus);
      }

      if (Suspendable.class.isAssignableFrom(this.getDao().getType())) {
         SuspendStatus suspendStatus = new SuspendStatus();
         if (item != null) {
            suspendStatus.setLastSuspendedFlag(((Suspendable)item).getLastSuspendedFlag());
         } else {
            suspendStatus.setLastSuspendedFlag("0");
         }

         status.setSuspendStatus(suspendStatus);
      }

      if (Auditable.class.isAssignableFrom(this.getDao().getType())) {
         AuditStatus auditStatus = new AuditStatus();
         if (item != null) {
            auditStatus.setProcessStatus(((Auditable)item).getProcessStatus());
            if (!StringUtils.isEmpty(currentStatusCode)) {
               auditStatus.setStatusCode(currentStatusCode);
            } else {
               CoreBpmnRuntimeService runtimeService = ApplicationContextHelper.getBean(CoreBpmnRuntimeService.class);
               List<CoreBpmnInstanceTaskBean> instanceTaskList = runtimeService.selectAuditableInstanceTaskList(
                  this.getDao().getTable(), id, LocalContextHelper.getLoginUserId()
               );
               if (instanceTaskList.size() > 1) {
                  throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.MULTIPLE_TASK_ASSIGNED");
               }

               if (instanceTaskList.size() > 0) {
                  auditStatus.setStatusCode(instanceTaskList.get(0).getStatusCode());
               } else {
                  auditStatus.setStatusCode(ProcessStatus.DRAFT.name().toLowerCase());
               }
            }
         } else {
            auditStatus.setProcessStatus(ProcessStatus.DRAFT.name().toLowerCase());
            auditStatus.setStatusCode(ProcessStatus.DRAFT.name().toLowerCase());
         }

         status.setAuditStatus(auditStatus);
      }

      if (status.getAuditStatus() != null) {
         CorePageViewConfigService viewConfigService = ApplicationContextHelper.getBean(CorePageViewConfigService.class);
         String statusCode = status.getAuditStatus().getStatusCode();
         CorePageViewConfigBean viewConfig = viewConfigService.selectById(this.getDao().getTable() + "$BPMN$" + statusCode);
         if (viewConfig == null) {
            viewConfig = viewConfigService.selectById(this.getDao().getTable() + "$BPMN_DEFAULT$" + statusCode);
         }

         if (viewConfig != null) {
            status.setViewConfig(viewConfig.getConfig());
         }
      } else if (status.getActiveStatus() != null) {
         CorePageViewConfigService viewConfigServicex = ApplicationContextHelper.getBean(CorePageViewConfigService.class);
         CorePageViewConfigBean viewConfigx = viewConfigServicex.selectById(this.getDao().getTable() + "$ACTIVE$" + status.getActiveStatus().getActivatedFlag());
         if (viewConfigx != null) {
            status.setViewConfig(viewConfigx.getConfig());
         }
      } else if (status.getSuspendStatus() != null) {
         CorePageViewConfigService viewConfigServicex = ApplicationContextHelper.getBean(CorePageViewConfigService.class);
         CorePageViewConfigBean viewConfigx = viewConfigServicex.selectById(
            this.getDao().getTable() + "$SUSPEND$" + status.getSuspendStatus().getLastSuspendedFlag()
         );
         if (viewConfigx != null) {
            status.setViewConfig(viewConfigx.getConfig());
         }
      }

      return status;
   }

   default void postUpdate(List<String> updatedColumnNameList, List<ID> idList) {
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.SWAP)
   @Audit("AUDIT.SWAP")
   default void swap(String column, ID firstId, ID secondId) {
      T first = (T)this.getDao().selectById(firstId);
      T second = (T)this.getDao().selectById(secondId);
      Field field = ReflectionUtils.findField(this.getDao().getType(), column);
      if (field == null) {
         String key = column.toLowerCase();
         String firstValue = first.getExt$().get(key);
         first.setExt$Item(key, second.getExt$().get(key));
         second.setExt$Item(key, firstValue);
      } else {
         Object firstValue = ReflectionUtils.invokeReadMethod(first, field.getName());
         Object secondValue = ReflectionUtils.invokeReadMethod(second, field.getName());
         ReflectionUtils.invokeWriteMethod(first, field.getName(), secondValue);
         ReflectionUtils.invokeWriteMethod(second, field.getName(), firstValue);
      }

      this.getDao().update(Arrays.asList((T[])(new Persistable[]{first, second})), column);
   }

   @Transactional(readOnly = true)
   default List<T> selectListByFilter(SearchFilter filter, Order... orders) {
      MapperParameter parameter = new MapperParameter();
      parameter.setFilter(filter);

      for (Order order : orders) {
         parameter.setOrderParam(order.getColumn(), order.getDirection());
      }

      return this.selectList(parameter);
   }

   @Transactional(readOnly = true)
   default List<T> selectListByFilter(SearchFilter filter, RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper == null ? new MapperParameter() : wrapper.extractMapFilter();
      parameter.setFilter(filter);
      return this.selectList(parameter);
   }

   @Transactional(readOnly = true)
   default List<T> selectList(MapperParameter parameter) {
      List<Map<String, Object>> mapList = this.getDao().selectByCondition(parameter);
      return mapList.isEmpty()
         ? CollectionUtils.emptyList()
         : mapList.stream().map(m -> PersistableHelper.mapToPersistable((Map<String, Object>)m, this.getDao().getType())).collect(Collectors.toList());
   }

   @Transactional(readOnly = true)
   default T selectFirst(MapperParameter parameter) {
      PageRequest page = new PageRequest();
      page.setPageSize(1);
      PageRowBounds rowBounds = new PageRowBounds(page);
      MybatisPageHelper.setDataOnly();
      Page<Map<String, Object>> mapList = MybatisPageHelper.get(rowBounds, () -> this.getDao().selectByCondition(parameter));
      return mapList.getRows().isEmpty() ? null : PersistableHelper.mapToPersistable(mapList.getRows().get(0), this.getDao().getType());
   }

   @Transactional(readOnly = true)
   default T selectFirstByFilter(SearchFilter filter, RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper == null ? new MapperParameter() : wrapper.extractMapFilter();
      parameter.setFilter(filter);
      return this.selectFirst(parameter);
   }

   @Transactional(readOnly = true)
   default T selectFirstByFilter(SearchFilter filter, Order... orders) {
      MapperParameter parameter = new MapperParameter();
      parameter.setFilter(filter);

      for (Order order : orders) {
         parameter.setOrderParam(order.getColumn(), order.getDirection());
      }

      return this.selectFirst(parameter);
   }

   default String getGikamAttrsMybatisMapper() {
      GenericMapper<ID> mapper = this.getDao().getMapper();
      Class<? extends GenericMapper<ID>> mapperClazz = (Class<? extends GenericMapper<ID>>)ClassUtils.getRawType(mapper.getClass());
      return MybatisHelper.getMapperContent(mapperClazz);
   }

   @Transactional(readOnly = true)
   default Page<CoreControllerRequestEntryDTO> getGikamAttrsReqEntryPagination() {
      for (Class<?> clazz = ClassUtils.getRawType(this.getClass()); !Object.class.equals(clazz); clazz = clazz.getSuperclass()) {
         String className = clazz.getSimpleName();
         String packageName = clazz.getPackage().getName();
         String controllerClassName = StringUtils.removeEnd(StringUtils.removeEnd(packageName, ".impl"), ".service")
            + ".controller."
            + StringUtils.removeEnd(StringUtils.removeEnd(className, "Impl"), "Service")
            + "Controller";

         try {
            Class<?> controllerClazz = ClassUtils.getClass(controllerClassName);
            Object proxy = ApplicationContextHelper.getBean(controllerClazz);
            Class<?> actualClazz = ClassUtils.getRawType(proxy.getClass());
            Object instance = ClassUtils.newInstance(actualClazz);
            RequestMapping requestMapping = ReflectionUtils.getAnnotation(actualClazz, RequestMapping.class);
            List<MethodWrapper> methodWrapperList = ReflectionUtils.getAnnotatedMethodList(actualClazz, RequestMapping.class);
            List<CoreControllerRequestEntryDTO> requestEntryList = new ArrayList<>();

            for (MethodWrapper methodWrapper : methodWrapperList) {
               CoreControllerRequestEntryDTO entry = new CoreControllerRequestEntryDTO();

               try {
                  entry.setPageUrl((String)ReflectionUtils.invokeMethod(methodWrapper.getMethod(), instance));
               } catch (ReflectionException var17) {
                  continue;
               }

               Log log = (Log)methodWrapper.getAnnotationList().stream().filter(a -> a.annotationType().equals(Log.class)).findAny().orElse(null);
               if (log != null) {
                  entry.setRequestName(log.value());
               } else {
                  entry.setRequestName(methodWrapper.getMethod().getName());
               }

               RequestMapping rm = (RequestMapping)methodWrapper.getAnnotationList()
                  .stream()
                  .filter(a -> a.annotationType().equals(RequestMapping.class))
                  .findAny()
                  .orElse(null);
               entry.setRequestUrl(requestMapping.value()[0] + rm.value()[0]);
               requestEntryList.add(entry);
            }

            if (StringUtils.isEmpty(ServletUtils.getCurrentRequest().getParameter("filter_page_config"))) {
               List<String> requestUrlList = requestEntryList.stream().map(e -> e.getRequestUrl()).collect(Collectors.toList());
               CoreAdminPageConfigService pageConfigService = ApplicationContextHelper.getBean(CoreAdminPageConfigService.class);
               List<CoreAdminPageConfigBean> pageConfigList = pageConfigService.getDao().selectListByOneColumnValues(requestUrlList, "REQUESTURI");
               requestEntryList.removeIf(e -> pageConfigList.stream().anyMatch(c -> e.getRequestUrl().equals(c.getRequestUri())));
            }

            return new Pagination<>(requestEntryList);
         } catch (Exception var18) {
            var18.printStackTrace();
         }
      }

      return new Pagination<>();
   }

   default JSONObject print(ID id) {
      throw new UnsupportedException("You should implements this method if required.");
   }

   @Transactional(readOnly = true)
   default Long selectProcId(ID id) {
      if (GenericAuditableService.class.isAssignableFrom(this.getClass())) {
         GenericAuditableService auditableService = (GenericAuditableService)this;
         T item = (T)this.getDao().selectById(id);
         List<Pair<ID, Long>> pairList = auditableService.getBpmnProcIdList(Arrays.asList((T[])(new Persistable[]{item})));
         return pairList.isEmpty() ? null : pairList.get(0).getSecond();
      } else {
         return null;
      }
   }

   default <V> void updateColumn(ID id, String columnName, V value) {
      T item = ClassUtils.newInstance(this.getDao().getType());
      item.setId(id);
      BeanPropertyDescriptor propertyDescriptor = BeanPropertyHelper.getBeanPropertyDescriptor(this.getDao().getType(), columnName);
      if (propertyDescriptor == null) {
         item.setExt$Item(columnName, ConvertUtils.convert(value, String.class));
      } else {
         propertyDescriptor.setPropertyValue(item, value);
      }

      this.getDao().update(item, columnName);
   }
}
