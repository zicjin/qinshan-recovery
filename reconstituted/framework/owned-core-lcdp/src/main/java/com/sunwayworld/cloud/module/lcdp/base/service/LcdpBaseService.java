package com.sunwayworld.cloud.module.lcdp.base.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunwayworld.cloud.module.lcdp.base.LcdpResultDTO;
import com.sunwayworld.cloud.module.lcdp.base.LcdpTreeDataDTO;
import com.sunwayworld.cloud.module.lcdp.base.annotation.Mapping;
import com.sunwayworld.cloud.module.lcdp.base.annotation.MappingType;
import com.sunwayworld.cloud.module.lcdp.base.persistent.dao.LcdpBaseDao;
import com.sunwayworld.cloud.module.lcdp.log.ScriptLogWebSocket;
import com.sunwayworld.cloud.module.lcdp.log.interceptor.ScriptLogInterceptor;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBean;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceService;
import com.sunwayworld.cloud.module.lcdp.resource.support.LcdpMapperUtils;
import com.sunwayworld.cloud.module.lcdp.resource.support.LcdpWrapperParseUtils;
import com.sunwayworld.cloud.module.lcdp.resourcelock.bean.LcdpResourceLockBean;
import com.sunwayworld.cloud.module.lcdp.resourcelock.service.LcdpResourceLockService;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.PageRequest;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.context.TableContext;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.database.sql.SqlHelper;
import com.sunwayworld.framework.exception.HackingDataException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean.TargetTableFilterData;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.support.base.service.GenericDownloadableService;
import com.sunwayworld.framework.support.base.service.GenericMapImportService;
import com.sunwayworld.framework.support.base.service.MapService;
import com.sunwayworld.framework.support.tree.TreeDescriptor;
import com.sunwayworld.framework.support.tree.TreeHelper;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import com.sunwayworld.module.sys.metadata.service.CoreTableService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface LcdpBaseService extends GikamBaseService, MapService, GenericDownloadableService, GenericMapImportService {
   String MAPPERPARAMETER_SQL_KEY = "sql";
   Logger logger = LogManager.getLogger(LcdpBaseService.class);
   String ACTIVATED_FLAG = "ACTIVATEDFLAG";
   String LAST_SUSPENDED_FLAG = "LASTSUSPENDEDFLAG";
   String PROCESS_STATUS = "PROCESSSTATUS";

   default String getTable() {
      return null;
   }

   default String getQuerySql() {
      return null;
   }

   default String getQuerySqlMapperId() {
      return null;
   }

   default Page<Map<String, Object>> selectBpPagination(RestJsonWrapperBean wrapper) {
      return this.selectLcdpPagination(wrapper);
   }

   default Page<Map<String, Object>> selectLcdpPagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = this.getMapperParameter(wrapper);
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      Page<Map<String, Object>> page = this.selectLcdpPagination(parameter, rowBounds);
      List<Map<String, Object>> rows = LcdpWrapperParseUtils.lowerCaseKey(page.getRows());
      page.setRows(rows);
      return page;
   }

   default Page<Map<String, Object>> selectLcdpChoosablePagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      parameter.setOrgAuthority();
      parameter.setChoosableQueries();
      String table = this.getTable();
      if (this.isAuditable(table)) {
         parameter.setProcessStatus("PROCESSSTATUS", ProcessStatus.DONE);
      }

      if (this.isSuspendable(table)) {
         parameter.setSuspendedFlag("0");
      }

      if (this.isActivatable(table)) {
         parameter.setActivatedFlag("1");
      }

      parameter.putAll(wrapper.getExtFilter());
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      return this.selectLcdpPagination(parameter, rowBounds);
   }

   default Map<String, Object> selectLcdpDetail(RestJsonWrapperBean wrapper) {
      String id = wrapper.getParamValue("id");
      Map<String, String> paramMap = wrapper.getParamMap();
      String queryFieldListFlag = paramMap.get("queryFieldListFlag");
      MapperParameter parameter = this.getMapperParameter(wrapper);
      parameter.setFilter(SearchFilter.instance().match("id", id).filter(MatchPattern.EQ));
      ServletUtils.enhanceByRequest(parameter);
      if (StringUtils.equals(queryFieldListFlag, "1")) {
         PageRowBounds rowBounds = wrapper.extractPageRowBounds();
         Page<Map<String, Object>> page = this.selectLcdpPagination(parameter, rowBounds);
         Map<String, String> pageColumnTypeMap = page.getColumnTypeMap();
         Map<String, Object> columnTypeMap = new HashMap<>();
         pageColumnTypeMap.forEach((k, v) -> columnTypeMap.put(k, v));
         return columnTypeMap;
      } else {
         List<Map<String, Object>> mapList = this.selectLcdpList(parameter);
         mapList.forEach(map -> this.dealMapData((Map<String, Object>)map));
         return mapList.isEmpty() ? CollectionUtils.emptyMap() : LcdpWrapperParseUtils.lowerCaseKey(mapList.get(0));
      }
   }

   default List<Map<String, Object>> selectLcdpList(MapperParameter parameter) {
      return ObjectUtils.isEmpty(parameter.get("sql"))
         ? this.selectListByMapperId(this.getQuerySqlMapperId(), parameter)
         : ApplicationContextHelper.getBean(LcdpBaseDao.class).selectByCondition(parameter);
   }

   default <ID> RestValidationResultBean validateUnique(ID id, String columnName, String columnValue) {
      if (id != null) {
         Map<String, Object> map = this.getMapDao().selectByIdIfPresent(TableContext.of(this.getTable()), id);
         if (map == null) {
            return new RestValidationResultBean(true);
         }
      }

      List<ID> idList = (List<ID>)(List<?>)this.getMapDao().selectListByOneColumnValue(this.getTable(), columnValue, columnName, Arrays.asList("ID"));
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

   default Page<Map<String, Object>> selectLcdpPagination(MapperParameter parameter, PageRowBounds rowBounds) {
      Page<Map<String, Object>> mapPage = null;
      if (ObjectUtils.isEmpty(parameter.get("sql"))) {
         mapPage = this.selectPaginationByMapperId(this.getQuerySqlMapperId(), parameter, rowBounds);
      } else {
         mapPage = MybatisPageHelper.get(rowBounds, () -> ApplicationContextHelper.getBean(LcdpBaseDao.class).selectByCondition(parameter));
      }

      mapPage.getRows().forEach(map -> this.dealMapData((Map<String, Object>)map));
      return mapPage;
   }

   @Transactional
   @Audit("AUDIT.INSERT")
   default String lcdpInsert(Map<String, Object> map) {
      return this.lcdpInsert(Arrays.asList(map));
   }

   @Transactional
   @Audit("AUDIT.INSERT")
   default String lcdpInsert(List<Map<String, Object>> list) {
      if (list.isEmpty()) {
         return null;
      } else {
         list.stream().forEach(mapx -> {
            if (!CollectionUtils.containsIgnoreCase(mapx, "id")) {
               mapx.put("ID", ApplicationContextHelper.getNextIdentity());
            }

            if (!CollectionUtils.containsIgnoreCase(mapx, "ACTIVATEDFLAG")) {
               mapx.put("ACTIVATEDFLAG", "0");
            }

            if (!CollectionUtils.containsIgnoreCase(mapx, "LASTSUSPENDEDFLAG")) {
               mapx.put("LASTSUSPENDEDFLAG", "0");
            }

            if (!CollectionUtils.containsIgnoreCase(mapx, "PROCESSSTATUS")) {
               mapx.put("PROCESSSTATUS", "draft");
            }
         });
         ApplicationContextHelper.getBean(LcdpBaseDao.class).insert(this.getTable(), list);
         Map<String, Object> map = list.get(0);
         return CollectionUtils.getValueIgnorecase(map, "id").toString();
      }
   }

   @Transactional
   @Audit("AUDIT.DELETE")
   default LcdpResultDTO lcdpDelete(RestJsonWrapperBean wrapper) {
      List<String> idList = LcdpWrapperParseUtils.parseIdList(wrapper, String.class);
      if (idList.isEmpty()) {
         return new LcdpResultDTO("500", "失败");
      } else {
         ApplicationContextHelper.getBean(LcdpBaseDao.class).delete(this.getTable(), idList);
         CoreTableService tableService = ApplicationContextHelper.getBean(CoreTableService.class);
         tableService.batchCascadeDelete(this.getTable(), idList);
         return new LcdpResultDTO("200", "成功");
      }
   }

   @Transactional
   @Audit("AUDIT.SAVE")
   default LcdpResultDTO lcdpUpdate(RestJsonWrapperBean wrapper) {
      List<Map<String, Object>> mapList = LcdpWrapperParseUtils.parseList(wrapper);
      ApplicationContextHelper.getBean(LcdpBaseDao.class).updateIfChanged(this.getTable(), mapList);
      return new LcdpResultDTO("200", "成功");
   }

   default List<Map<String, Object>> selectSelectableList(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = new MapperParameter();
      Map<String, String> requestParamMap = wrapper.getParamMap();
      if (!requestParamMap.isEmpty()) {
         SearchFilter filter = SearchFilter.instance();
         requestParamMap.forEach((k, v) -> {
            if (StringUtils.startsWith(k, "filter_")) {
               parameter.put(k, v);
            } else if ("target_filter".equals(k)) {
               Map<String, String> taMap = JSONObject.parseObject(StringUtils.decodeURL(v), new TypeReference<Map<String, String>>() {}, new Feature[0]);
               TargetTableFilterData filterData = new TargetTableFilterData();
               filterData.setType(taMap.get("type"));
               filterData.setTargetTable(taMap.get("targetTable"));
               filterData.setTargetMatchColumn(Objects.toString(taMap.get("targetMatchColumn"), "ID"));
               filterData.setThisMatchColumn(Objects.toString(taMap.get("thisMatchColumn"), "ID"));
               String filterStr = taMap.get("filter");
               if (!StringUtils.isEmpty(filterStr)) {
                  JSONArray array = JSONObject.parseArray(filterStr);
                  int i = 0;

                  for (int j = array.size(); i < j; i++) {
                     JSONObject jsonObject = (JSONObject)array.get(i);
                     filterData.addTargetFilter(jsonObject.getString("targetFilterColumn"), jsonObject.getString("targetFilterValue"));
                  }
               }

               if (SqlHelper.isRiskySqlSegment(filterData.getTargetTable())) {
                  throw new HackingDataException("targetTable");
               }

               if (SqlHelper.isRiskySqlSegment(filterData.getTargetMatchColumn())) {
                  throw new HackingDataException("targetMatchColumn");
               }

               if (SqlHelper.isRiskySqlSegment(filterData.getThisMatchColumn())) {
                  throw new HackingDataException("thisMatchColumn");
               }

               filterData.getTargetFilter().keySet().forEach(e -> {
                  if (SqlHelper.isRiskySqlSegment(e)) {
                     throw new HackingDataException("targetFilterColumn");
                  }
               });
               parameter.put("targetFilter", filterData);
            } else {
               MatchPattern match = Arrays.stream(MatchPattern.values()).filter(m -> StringUtils.endsWith(k, "_" + m.name())).findAny().orElse(null);
               String key = k;
               if (match == null) {
                  match = MatchPattern.EQ;
               } else {
                  key = StringUtils.removeEnd(k, "_" + match.name());
                  if (MatchPattern.IN.equals(match)) {
                     match = MatchPattern.OR;
                  }
               }

               if (StringUtils.contains(v, ",")) {
                  filter.match(key, Arrays.asList(StringUtils.split(v, ","))).filter(match);
               } else {
                  filter.match(key, v).filter(match);
               }
            }
         });
         parameter.setFilter(filter);
      }

      parameter.setSelectableQueries();
      return this.selectLcdpList(parameter);
   }

   default LcdpResultDTO executeSql(String sql) {
      ApplicationContextHelper.getBean(GlobalMapper.class).update(sql);
      return new LcdpResultDTO("200", "成功");
   }

   default List<Map<String, Object>> selectListBySql(String sql) {
      List<Map<String, Object>> mapList = ApplicationContextHelper.getBean(GlobalMapper.class).selectList(sql);
      return LcdpWrapperParseUtils.lowerCaseKey(mapList);
   }

   default Map<String, Object> selectOneBySql(String sql) {
      return ApplicationContextHelper.getBean(GlobalMapper.class).selectOne(sql);
   }

   default void log(Object log) {
      String message = "";
      Object resultJsonObject = JSON.parse(JSONObject.toJSONStringWithDateFormat(log, "yyyy-MM-dd HH:mm:ss", new SerializerFeature[0]));
      if (resultJsonObject instanceof JSONObject) {
         message = JSONObject.toJSONString(resultJsonObject, true);
      } else if (resultJsonObject instanceof JSONArray) {
         message = JSONArray.toJSONString(resultJsonObject, true);
      } else {
         message = String.valueOf(log);
      }

      ScriptLogWebSocket console = ApplicationContextHelper.getBean(ScriptLogWebSocket.class);
      console.pushInfoLog(message);
      logger.info(log);
   }

   default List<LcdpTreeDataDTO> selectLcdpTree(RestJsonWrapperBean jsonWrapper) {
      MapperParameter parameter = this.getMapperParameter(jsonWrapper);
      if (parameter.containsKey("parentId")) {
         String parentId = (String)parameter.get("parentId");
         if (StringUtils.isBlank(parentId) || "root".equals(parentId)) {
            parentId = null;
         }

         parameter.addFilterParamPatterns(new FilterParamPattern("EQ", "PARENTID", parentId));
      } else if (parameter.containsKey("id")) {
         String id = String.valueOf(parameter.get("id"));
         parameter.addFilterParamPatterns(new FilterParamPattern("EQ", "ID", id));
      }

      List<Map<String, Object>> mapList = this.selectLcdpList(parameter);
      if (parameter.containsKey("parentId") && !mapList.isEmpty()) {
         List<String> idList = mapList.stream().map(e -> CollectionUtils.getValueIgnorecase((Map<String, ?>)e, "ID").toString()).collect(Collectors.toList());
         MapperParameter childMapperParameter = new MapperParameter();
         childMapperParameter.setFilter(SearchFilter.instance().match("PARENTID", idList).filter(MatchPattern.OR));
         childMapperParameter.put("sql", this.getQuerySql());
         List<Map<String, Object>> childMapList = this.selectLcdpList(childMapperParameter);
         mapList.forEach(
            e -> {
               long childQty = childMapList.stream()
                  .filter(
                     c -> CollectionUtils.getValueIgnorecase((Map<String, ?>)c, "PARENTID")
                        .toString()
                        .equals(CollectionUtils.getValueIgnorecase(e, "ID").toString())
                  )
                  .count();
               e.put("childQty", childQty);
            }
         );
      }

      List<LcdpTreeDataDTO> treeDataDTOList = mapList.stream()
         .map(e -> PersistableHelper.mapToPersistable((Map<String, Object>)e, LcdpTreeDataDTO.class))
         .collect(Collectors.toList());
      String orderField = StringUtils.isEmpty(jsonWrapper.getFilterValue("lcdpTreeOrderField")) ? "ID" : jsonWrapper.getFilterValue("lcdpTreeOrderField");
      if (StringUtils.isEmpty(jsonWrapper.getFilterValue("lcdpTreeOrderField"))
         && !StringUtils.isEmpty(this.getTable())
         && TableContext.of(this.getTable()).hasOrderColumn()) {
         orderField = "ORDERNO";
      }

      TreeDescriptor<LcdpTreeDataDTO> descriptor = new TreeDescriptor<>(
         "ID", "PARENTID", Optional.ofNullable(jsonWrapper.getLcdpTreeNodeName()).orElse("ID"), orderField
      );
      List<LcdpTreeDataDTO> treeDataDTOs = TreeHelper.parseTreeNode(treeDataDTOList, descriptor, LcdpTreeDataDTO.class);
      if (!parameter.containsKey("parentId") && !mapList.isEmpty()) {
         TreeHelper.updateChildQty(treeDataDTOs);
      }

      return treeDataDTOs;
   }

   default Page<Map<String, Object>> selectLcdpTreePagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = this.getMapperParameter(wrapper);
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      Page<Map<String, Object>> page = this.selectLcdpPagination(parameter, rowBounds);
      List<LcdpTreeDataDTO> treeDataDTOList = page.getRows()
         .stream()
         .map(e -> PersistableHelper.mapToPersistable((Map<String, Object>)e, LcdpTreeDataDTO.class))
         .collect(Collectors.toList());
      List<LcdpTreeDataDTO> treeList = new ArrayList<>();
      treeList.addAll(treeDataDTOList);
      Map<String, LcdpTreeDataDTO> allTreeMap = this.selectAll(TableContext.of(this.getTable()))
         .stream()
         .map(e -> PersistableHelper.mapToPersistable((Map<String, Object>)e, LcdpTreeDataDTO.class))
         .collect(Collectors.toMap(AbstractTreeNode::getId, Function.identity()));
      Set<String> treeIdSet = treeList.stream().map(AbstractTreeNode::getId).distinct().collect(Collectors.toSet());
      treeDataDTOList.forEach(u -> {
         String currParentId = u.getParentId();
         if (!StringUtils.isEmpty(currParentId)) {
            while (!treeIdSet.contains(currParentId)) {
               LcdpTreeDataDTO currData = allTreeMap.get(currParentId);
               if (null == currData) {
                  break;
               }

               treeIdSet.add(currData.getId());
               treeList.add(currData);
               currParentId = currData.getParentId();
               if (StringUtils.isEmpty(currParentId)) {
                  break;
               }
            }
         }
      });
      TreeDescriptor<LcdpTreeDataDTO> descriptor = new TreeDescriptor<>(
         "id", "parentId", Optional.ofNullable(wrapper.getLcdpTreeNodeName()).orElse("ID"), "orderNo"
      );
      List<LcdpTreeDataDTO> treeNodeList = TreeHelper.parseTreeNode(treeList, descriptor, LcdpTreeDataDTO.class);
      List<Map<String, Object>> rows = treeNodeList.stream().map(e -> this.toMap(e)).collect(Collectors.toList());
      return new Pagination<>(page, rows);
   }

   default RestValidationResultBean lcdpValidateUnique(RestJsonWrapperBean wrapper) {
      String validateUniqueFieldJson = wrapper.getParamValue("vu");
      if (null == validateUniqueFieldJson) {
         return new RestValidationResultBean(true);
      } else {
         Map<String, Object> validateUniqueFieldMap = JSONObject.parseObject(
            validateUniqueFieldJson, new TypeReference<Map<String, Object>>() {}, new Feature[0]
         );
         Map<String, Object> columnMap = new CaseInsensitiveLinkedMap<>(validateUniqueFieldMap);
         Class<?> idClass = TableContext.of(this.getTable()).getIdColumnContext().getType();
         Object id = ConvertUtils.convert(columnMap.get("id"), idClass);
         columnMap.remove("id");
         if (CollectionUtils.isEmpty(columnMap)) {
            Map<String, Object> item = this.getMapDao().selectByIdIfPresent(TableContext.of(this.getTable()), id);
            return item == null
               ? new RestValidationResultBean(true)
               : new RestValidationResultBean(false, I18nHelper.getMessage("core.validator.column.not-unique"));
         } else {
            List<?> idList = this.getMapDao().selectColumnList(TableContext.of(this.getTable()), columnMap, "ID", idClass);
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
   }

   default String getReplacedParam(String param) {
      if (StringUtils.contains(param, "${loginUserId}")) {
         param = StringUtils.replace(param, "${loginUserId}", LocalContextHelper.getLoginUserId());
      }

      if (StringUtils.contains(param, "${loginOrgId}")) {
         param = StringUtils.replace(param, "${loginOrgId}", LocalContextHelper.getLoginOrgId());
      }

      if (StringUtils.contains(param, "${loginRoleId}")) {
         param = StringUtils.replace(param, "${loginRoleId}", LocalContextHelper.getLoginRoleId());
      }

      return param;
   }

   default Page<CoreFileBean> selectLcdpFilePagination(RestJsonWrapperBean wrapper) {
      return ApplicationContextHelper.getBean(CoreFileService.class).selectSearchablePagination(wrapper);
   }

   default Long lcdpUploadFile(CoreFileBean coreFile, MultipartFile file) {
      return ApplicationContextHelper.getBean(CoreFileService.class).upload(coreFile, file);
   }

   default LcdpResultDTO lcdpDeleteFile(RestJsonWrapperBean wrapper) {
      ApplicationContextHelper.getBean(CoreFileService.class).delete(wrapper);
      return new LcdpResultDTO("200", "成功");
   }

   default MapperParameter getMapperParameter(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      if (!parameter.containsKey("sql")) {
         if (StringUtils.isBlank(wrapper.getLcdpSql())) {
            parameter.put("sql", this.getQuerySql());
         } else {
            parameter.put("sql", wrapper.getLcdpSql());
         }
      }

      if (!parameter.hasOrderParam() && wrapper.getLcdpOrderList() != null) {
         wrapper.getLcdpOrderList().forEach(o -> parameter.setOrderParam(o.getColumn(), o.getDirection()));
      }

      return parameter;
   }

   default List<Long> getIdList(RestJsonWrapperBean wrapper) {
      return wrapper.parseId(Long.class);
   }

   @Transactional
   @Audit("AUDIT.DELETE")
   default void deleteByIdList(String table, RestJsonWrapperBean wrapper) {
      List<Long> idList = wrapper.parseId(Long.class);
      this.getMapDao().deleteByIdList(TableContext.of(table), idList);
      idList.clear();
   }

   @Transactional
   @Audit("AUDIT.DELETE")
   default void deleteByIdList(RestJsonWrapperBean wrapper) {
      List<Long> idList = wrapper.parseId(Long.class);
      this.getMapDao().deleteByIdList(TableContext.of(this.getTable()), idList);
      idList.clear();
   }

   @Transactional
   @Audit("AUDIT.DELETE")
   default void deleteBy(String table, RestJsonWrapperBean wrapper, String columnName) {
      List<Long> idList = wrapper.parseId(Long.class);
      List<Map<String, Object>> mapList = new ArrayList<>();

      for (Long id : idList) {
         Map<String, Object> map = new HashMap<>();
         map.put(columnName, id);
         mapList.add(map);
      }

      this.getMapDao().deleteBy(TableContext.of(table), mapList, columnName);
      mapList.clear();
      idList.clear();
   }

   @Transactional
   @Audit("AUDIT.DELETE")
   default void deleteBy(TableContext context, Map<String, Object> item, String... searchColumns) {
      this.getMapDao().deleteBy(context, Arrays.asList(item), searchColumns);
   }

   @Transactional
   @Audit("AUDIT.DELETE")
   default void deleteBy(String table, Map<String, Object> item, String... searchColumns) {
      this.getMapDao().deleteBy(TableContext.of(table), Arrays.asList(item), searchColumns);
   }

   @Transactional
   @Audit("AUDIT.DELETE")
   default void deleteBy(Map<String, Object> item, String... searchColumns) {
      this.getMapDao().deleteBy(TableContext.of(this.getTable()), Arrays.asList(item), searchColumns);
   }

   @Transactional
   @Audit("AUDIT.DELETE")
   default void deleteBy(TableContext context, List<Map<String, Object>> itemList, String... searchColumns) {
      this.getMapDao().deleteBy(context, itemList, searchColumns);
   }

   @Transactional
   @Audit("AUDIT.DELETE")
   default void deleteBy(String table, List<Map<String, Object>> itemList, String... searchColumns) {
      this.getMapDao().deleteBy(TableContext.of(table), itemList, searchColumns);
   }

   @Transactional
   @Audit("AUDIT.DELETE")
   default void deleteBy(List<Map<String, Object>> itemList, String... searchColumns) {
      this.getMapDao().deleteBy(TableContext.of(this.getTable()), itemList, searchColumns);
   }

   @Transactional
   @Audit("AUDIT.DELETE")
   default void deleteBy(RestJsonWrapperBean wrapper, String columnName) {
      List<Long> idList = wrapper.parseId(Long.class);
      List<Map<String, Object>> mapList = new ArrayList<>();

      for (Long id : idList) {
         Map<String, Object> map = new HashMap<>();
         map.put(columnName, id);
         mapList.add(map);
      }

      this.getMapDao().deleteBy(TableContext.of(this.getTable()), mapList, columnName);
      mapList.clear();
      idList.clear();
   }

   @Transactional
   @Audit("AUDIT.INSERT")
   default void insert(String table, Map<String, Object> item) {
      this.getMapDao().insert(table, item);
   }

   @Transactional
   @Audit("AUDIT.INSERT")
   default void insert(Map<String, Object> item) {
      this.getMapDao().insert(this.getTable(), item);
   }

   @Transactional
   @Audit("AUDIT.INSERT")
   default void insert(String table, List<Map<String, Object>> itemList) {
      this.getMapDao().insert(table, itemList);
   }

   @Transactional
   @Audit("AUDIT.INSERT")
   default void insert(List<Map<String, Object>> itemList) {
      this.getMapDao().insert(this.getTable(), itemList);
   }

   @Transactional
   @Audit("AUDIT.SAVE")
   default void update(String table, Map<String, Object> item) {
      this.getMapDao().update(TableContext.of(table), item);
   }

   @Transactional
   @Audit("AUDIT.SAVE")
   default void update(Map<String, Object> item) {
      this.getMapDao().update(TableContext.of(this.getTable()), item);
   }

   @Transactional
   @Audit("AUDIT.SAVE")
   default void update(String table, List<Map<String, Object>> itemList, String... upateColumns) {
      this.getMapDao().update(TableContext.of(table), itemList, upateColumns);
   }

   @Transactional
   @Audit("AUDIT.SAVE")
   default void update(List<Map<String, Object>> itemList, String... upateColumns) {
      this.getMapDao().update(TableContext.of(this.getTable()), itemList, upateColumns);
   }

   @Transactional
   @Audit("AUDIT.SAVE")
   default void update(String table, Map<String, Object> item, String... upateColumns) {
      this.getMapDao().update(TableContext.of(table), item, upateColumns);
   }

   @Transactional
   @Audit("AUDIT.SAVE")
   default void update(Map<String, Object> item, String... upateColumns) {
      this.getMapDao().update(TableContext.of(this.getTable()), item, upateColumns);
   }

   default <ID> void postUpdate(List<String> updatedColumnNameList, List<ID> idList) {
   }

   @Transactional
   @Audit("AUDIT.ACTIVATE")
   default void activateRows(RestJsonWrapperBean wrapper) {
      List<Map<String, Object>> itemList = LcdpWrapperParseUtils.parseList(wrapper);
      this.activate(itemList, true);
   }

   @Transactional
   @Audit("AUDIT.DEACTIVATE")
   default void deactivateRows(RestJsonWrapperBean wrapper) {
      List<Map<String, Object>> itemList = LcdpWrapperParseUtils.parseList(wrapper);
      this.activate(itemList, false);
   }

   default void activate(List<Map<String, Object>> itemList, boolean activate) {
      itemList.forEach(i -> this.setActivateProperties((Map<String, Object>)i, activate));
      this.getMapDao()
         .update(TableContext.of(this.getTable()), itemList, ArrayUtils.asList("ACTIVATEDFLAG", "ACTIVATEDBYID", "ACTIVATEDBYNAME", "ACTIVATEDTIME"));
   }

   default void setActivateProperties(Map<String, Object> item, boolean activate) {
      item.put("ACTIVATEDFLAG", activate ? "1" : "0");
      if (activate) {
         item.put("ACTIVATEDBYID", LocalContextHelper.getLoginUserId());
         item.put("ACTIVATEDBYNAME", LocalContextHelper.getLoginUser().getUserName());
         item.put("ACTIVATEDTIME", LocalDateTime.now());
      } else {
         item.put("ACTIVATEDBYID", null);
         item.put("ACTIVATEDBYNAME", null);
         item.put("ACTIVATEDTIME", null);
      }
   }

   @Transactional
   @Audit("AUDIT.SUSPEND")
   default void suspend(RestJsonWrapperBean wrapper) {
      List<Map<String, Object>> itemList = LcdpWrapperParseUtils.parseList(wrapper);
      this.suspend(itemList, true);
   }

   @Transactional
   @Audit("AUDIT.UNSUSPEND")
   default void unsuspend(RestJsonWrapperBean wrapper) {
      List<Map<String, Object>> itemList = LcdpWrapperParseUtils.parseList(wrapper);
      this.suspend(itemList, false);
   }

   default void suspend(List<Map<String, Object>> itemList, boolean suspend) {
      itemList.forEach(i -> this.setSuspendProperties((Map<String, Object>)i, suspend));
      this.getMapDao()
         .update(
            TableContext.of(this.getTable()),
            itemList,
            ArrayUtils.asList("LASTSUSPENDEDFLAG", "LASTSUSPENDEDBYID", "LASTSUSPENDEDBYNAME", "LASTSUSPENDEDTIME"),
            "id"
         );
   }

   default void setSuspendProperties(Map<String, Object> item, boolean suspend) {
      item.put("LASTSUSPENDEDFLAG", suspend ? "1" : "0");
      if (suspend) {
         item.put("LASTSUSPENDEDBYID", LocalContextHelper.getLoginUserId());
         item.put("LASTSUSPENDEDBYNAME", LocalContextHelper.getLoginUser().getUserName());
         item.put("LASTSUSPENDEDTIME", LocalDateTime.now());
      } else {
         item.put("LASTSUSPENDEDBYID", null);
         item.put("LASTSUSPENDEDBYNAME", null);
         item.put("LASTSUSPENDEDTIME", null);
      }
   }

   default List<Map<String, Object>> selectListByMapperId(String mapperId, MapperParameter parameter) {
      String parsedMapperId = this.parseMapperId(mapperId);
      parameter.setTableName(this.getTable());
      List<Map<String, Object>> rows = ApplicationContextHelper.getBean(SqlSessionTemplate.class).selectList(parsedMapperId, parameter);
      rows.forEach(map -> this.dealMapData((Map<String, Object>)map));
      return LcdpWrapperParseUtils.lowerCaseKey(rows);
   }

   default Page<Map<String, Object>> selectPaginationByMapperId(String mapperId, MapperParameter parameter, PageRowBounds rowBounds) {
      String parsedMapperId = this.parseMapperId(mapperId);
      parameter.setTableName(this.getTable());
      Page<Map<String, Object>> mapPage = MybatisPageHelper.get(
         rowBounds, () -> ApplicationContextHelper.getBean(SqlSessionTemplate.class).selectList(parsedMapperId, parameter)
      );
      mapPage.getRows().forEach(map -> this.dealMapData((Map<String, Object>)map));
      List<Map<String, Object>> rows = LcdpWrapperParseUtils.lowerCaseKey(mapPage.getRows());
      mapPage.setRows(rows);
      return mapPage;
   }

   default Map<String, Object> selectFirstByMapperId(String mapperId, MapperParameter parameter) {
      PageRequest page = new PageRequest();
      page.setPageSize(1);
      PageRowBounds rowBounds = new PageRowBounds(page);
      Page<Map<String, Object>> mapList = this.selectPaginationByMapperId(mapperId, parameter, rowBounds);
      return mapList.getRows().isEmpty() ? null : mapList.getRows().get(0);
   }

   default Map<String, Object> selectDetailByMapperId(String mapperId, RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      parameter.setDetailQueries();
      Map<String, String> paramMap = wrapper.getParamMap();
      parameter.setFilter(SearchFilter.instance().match("ID", CollectionUtils.getValueIgnorecase(paramMap, "ID")).filter(MatchPattern.EQ));
      ServletUtils.enhanceByRequest(parameter);
      List<Map<String, Object>> mapList = this.selectListByMapperId(mapperId, parameter);
      return mapList.isEmpty() ? CollectionUtils.emptyMap() : mapList.get(0);
   }

   default Page<Map<String, Object>> selectPaginationByMapperId(String mapperId, RestJsonWrapperBean wrapper) {
      return this.selectPaginationByMapperId(mapperId, wrapper.extractMapFilter(), wrapper.extractPageRowBounds());
   }

   default List<Map<String, Object>> selectListByMapperId(String mapperId, RestJsonWrapperBean wrapper) {
      return this.selectListByMapperId(mapperId, wrapper.extractMapFilter());
   }

   default String parseMapperId(String mapperId) {
      if (ServletUtils.getCurrentRequest() == null) {
         return mapperId;
      } else {
         String lcdpEnv = ServletUtils.getCurrentRequest().getHeader("lcdp-env");
         if ("development".equals(lcdpEnv)) {
            String lockResource;
            try {
               ScriptLogInterceptor.disable();
               String namespace = mapperId.substring(0, mapperId.lastIndexOf("."));
               String path = StringUtils.replaceLast(namespace, "Mapper", LcdpMapperUtils.DIALECT_MAPPER_SUFFIX);
               LcdpResourceBean filter = new LcdpResourceBean();
               filter.setPath(path);
               filter.setDeleteFlag("0");
               LcdpResourceBean checkoutMapper = ApplicationContextHelper.getBean(LcdpResourceService.class)
                  .getDao()
                  .selectFirstIfPresent(filter, ArrayUtils.asList("ID"));
               if (!ObjectUtils.isEmpty(checkoutMapper)) {
                  LcdpResourceLockBean lockResourcex = null;
                  LcdpResourceLockBean var13;
                  if (StringUtils.equals("sysAdmin", LocalContextHelper.getLoginUserId())) {
                     List<LcdpResourceLockBean> lockList = ApplicationContextHelper.getBean(LcdpResourceLockService.class)
                        .selectListByFilter(
                           SearchFilter.instance()
                              .match("RESOURCEID", String.valueOf(checkoutMapper.getId()))
                              .filter(MatchPattern.EQ)
                              .match("LOCKUSERID", null)
                              .filter(MatchPattern.DIFFER),
                           new Order[0]
                        );
                     var13 = lockList.stream().findFirst().orElse(null);
                  } else {
                     LcdpResourceLockBean lockFilter = new LcdpResourceLockBean();
                     lockFilter.setResourceId(String.valueOf(checkoutMapper.getId()));
                     lockFilter.setLockUserId(LocalContextHelper.getLoginUserId());
                     var13 = ApplicationContextHelper.getBean(LcdpResourceLockService.class).getDao().selectFirstIfPresent(lockFilter);
                  }

                  if (ObjectUtils.isEmpty(var13)) {
                     return mapperId;
                  }

                  return namespace + "$DEV" + mapperId.substring(mapperId.lastIndexOf("."));
               }

               lockResource = mapperId;
            } finally {
               ScriptLogInterceptor.enable();
            }

            return lockResource;
         } else {
            return mapperId;
         }
      }
   }

   default Page<Map<String, Object>> selectRawPaginationByMapperId(String mapperId, String tableName, RestJsonWrapperBean wrapper) {
      tableName = StringUtils.isEmpty(tableName) ? this.getTable() : tableName;
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      parameter.setTableName(tableName);
      parameter.setRawQueries();
      if (this.isAuditable(tableName)) {
         parameter.setProcessStatus("PROCESSSTATUS", ProcessStatus.DRAFT);
      }

      if (this.isExistCreatedByOrgId(tableName)) {
         parameter.setOrgAuthority();
      }

      if (!wrapper.getAuthorityList().isEmpty()) {
         wrapper.getAuthorityList().forEach(a -> parameter.setAuthorityParameter(a));
      }

      return this.selectPaginationByMapperId(mapperId, parameter, rowBounds);
   }

   default Page<Map<String, Object>> selectAuditablePaginationByMapperId(String mapperId, String tableName, RestJsonWrapperBean wrapper) {
      tableName = StringUtils.isEmpty(tableName) ? this.getTable() : tableName;
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      parameter.setTableName(this.getTable());
      parameter.setAuditableQueries();
      parameter.setAuditAuthority();
      if (this.isAuditable(tableName)) {
         parameter.setProcessStatus("PROCESSSTATUS", ProcessStatus.APPROVE);
      }

      if (!"0".equals(wrapper.getParamValue("orgAuthority"))) {
         parameter.setOrgAuthority();
      }

      if (!wrapper.getAuthorityList().isEmpty()) {
         wrapper.getAuthorityList().forEach(a -> parameter.setAuthorityParameter(a));
      }

      return this.selectPaginationByMapperId(mapperId, parameter, rowBounds);
   }

   default Page<Map<String, Object>> selectWithdrawablePaginationByMapperId(String mapperId, String tableName, RestJsonWrapperBean wrapper) {
      tableName = StringUtils.isEmpty(tableName) ? this.getTable() : tableName;
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      parameter.setTableName(tableName);
      parameter.setWithdrawableQueries();
      if (this.isAuditable(tableName)) {
         parameter.setProcessStatus("PROCESSSTATUS", ProcessStatus.APPROVE);
      }

      if (!"0".equals(wrapper.getParamValue("orgAuthority"))) {
         parameter.setOrgAuthority();
      }

      if (!wrapper.getAuthorityList().isEmpty()) {
         wrapper.getAuthorityList().forEach(a -> parameter.setAuthorityParameter(a));
      }

      return this.selectPaginationByMapperId(mapperId, parameter, rowBounds);
   }

   default Page<Map<String, Object>> selectUndoablePaginationByMapperId(String mapperId, String tableName, RestJsonWrapperBean wrapper) {
      tableName = StringUtils.isEmpty(tableName) ? this.getTable() : tableName;
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      parameter.setTableName(tableName);
      parameter.setUndoableQueries();
      if (this.isAuditable(tableName)) {
         parameter.setProcessStatus("PROCESSSTATUS", ProcessStatus.DONE);
      }

      if (this.isSuspendable(tableName)) {
         parameter.setSuspendedFlag("0");
      }

      if (!"0".equals(wrapper.getParamValue("orgAuthority"))) {
         parameter.setOrgAuthority();
      }

      if (!wrapper.getAuthorityList().isEmpty()) {
         wrapper.getAuthorityList().forEach(a -> parameter.setAuthorityParameter(a));
      }

      return this.selectPaginationByMapperId(mapperId, parameter, rowBounds);
   }

   default boolean isAuditable(String tableName) {
      return TableContext.of(tableName)
         .getColumnContextList()
         .stream()
         .anyMatch(columnContext -> StringUtils.equalsIgnoreCase("PROCESSSTATUS", columnContext.getColumnName()));
   }

   default boolean isSuspendable(String tableName) {
      return TableContext.of(tableName)
         .getColumnContextList()
         .stream()
         .anyMatch(columnContext -> StringUtils.equalsIgnoreCase("LASTSUSPENDEDFLAG", columnContext.getColumnName()));
   }

   default boolean isActivatable(String tableName) {
      return TableContext.of(tableName)
         .getColumnContextList()
         .stream()
         .anyMatch(columnContext -> StringUtils.equalsIgnoreCase("ACTIVATEDFLAG", columnContext.getColumnName()));
   }

   default boolean isExistCreatedByOrgId(String tableName) {
      return TableContext.of(tableName)
         .getColumnContextList()
         .stream()
         .anyMatch(columnContext -> StringUtils.equalsIgnoreCase("CREATEDBYORGID", columnContext.getColumnName()));
   }

   default List<Map<String, Object>> selectListByFilter(SearchFilter filter, Order... orders) {
      MapperParameter parameter = new MapperParameter();
      parameter.setFilter(filter);

      for (Order order : orders) {
         parameter.setOrderParam(order.getColumn(), order.getDirection());
      }

      return this.selectListByMapperId(this.getQuerySqlMapperId(), parameter);
   }

   default List<Map<String, Object>> selectListByFilter(SearchFilter filter, RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper == null ? new MapperParameter() : wrapper.extractMapFilter();
      parameter.setFilter(filter);
      return this.selectListByMapperId(this.getQuerySqlMapperId(), parameter);
   }

   default Map<String, Object> selectFirstByFilter(SearchFilter filter, Order... orders) {
      MapperParameter parameter = new MapperParameter();
      parameter.setFilter(filter);

      for (Order order : orders) {
         parameter.setOrderParam(order.getColumn(), order.getDirection());
      }

      return this.selectFirstByMapperId(this.getQuerySqlMapperId(), parameter);
   }

   default Map<String, Object> selectFirstByFilter(SearchFilter filter, RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper == null ? new MapperParameter() : wrapper.extractMapFilter();
      parameter.setFilter(filter);
      return this.selectFirstByMapperId(this.getQuerySqlMapperId(), parameter);
   }

   default Page<Map<String, Object>> selectPaginationByFilter(SearchFilter filter, RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      parameter.setFilter(filter);
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      return this.selectPaginationByMapperId(this.getQuerySqlMapperId(), parameter, rowBounds);
   }

   default <ID> Map<String, Object> selectDetail(ID id) {
      MapperParameter parameter = new MapperParameter();
      parameter.setDetailQueries();
      parameter.setFilter(SearchFilter.instance().match("ID", id).filter(MatchPattern.EQ));
      ServletUtils.enhanceByRequest(parameter);
      List<Map<String, Object>> mapList = this.selectListByMapperId(this.getQuerySqlMapperId(), parameter);
      return mapList.isEmpty() ? CollectionUtils.emptyMap() : mapList.get(0);
   }

   default <ID> Map<String, Object> selectById(ID id) {
      return this.selectById(TableContext.of(this.getTable()), id);
   }

   default <ID> List<Map<String, Object>> selectListByIdList(List<ID> idList) {
      return this.selectListByIdList(TableContext.of(this.getTable()), idList);
   }

   @Mapping(
      value = "Lcdp列表查询数据",
      type = MappingType.SELECT
   )
   default List<Map<String, Object>> selectListData(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = this.getMapperParameter(wrapper);
      List<Map<String, Object>> mapList = this.selectLcdpList(parameter);
      return LcdpWrapperParseUtils.lowerCaseKey(mapList);
   }

   @Mapping(
      value = "Lcdp分页查询数据",
      type = MappingType.SELECT
   )
   default Page<Map<String, Object>> selectPaginationData(RestJsonWrapperBean wrapper) {
      return this.selectLcdpPagination(wrapper);
   }

   @Mapping(
      value = "Lcdp选择页查询数据",
      type = MappingType.SELECT
   )
   default Page<Map<String, Object>> selectChoosablePaginationData(RestJsonWrapperBean wrapper) {
      return this.selectLcdpChoosablePagination(wrapper);
   }

   default Page<Map<String, Object>> selectBpPaginationData(RestJsonWrapperBean wrapper) {
      return this.selectLcdpPagination(wrapper);
   }

   @Mapping(
      value = "Lcdp查询详细数据",
      type = MappingType.SELECT
   )
   default Map<String, Object> selectDetailData(RestJsonWrapperBean wrapper) {
      return this.selectLcdpDetail(wrapper);
   }

   @Mapping(
      value = "Lcdp删除数据",
      type = MappingType.DELETE
   )
   @Transactional
   @Audit("AUDIT.DELETE")
   default void deleteData(RestJsonWrapperBean wrapper) {
      this.lcdpDelete(wrapper);
   }

   @Mapping(
      value = "Lcdp更新数据",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.SAVE")
   default void updateData(RestJsonWrapperBean wrapper) {
      this.lcdpUpdate(wrapper);
   }

   @Mapping(
      value = "Lcdp检验唯一性",
      type = MappingType.SELECT
   )
   default RestValidationResultBean validateDataUnique(RestJsonWrapperBean wrapper) {
      return this.lcdpValidateUnique(wrapper);
   }

   @Mapping(
      value = "Lcdp查询附件数据",
      type = MappingType.SELECT
   )
   default Page<CoreFileBean> selectFilePaginationData(RestJsonWrapperBean wrapper) {
      return this.selectLcdpFilePagination(wrapper);
   }

   @Mapping(
      value = "Lcdp上传附件",
      type = MappingType.INSERT
   )
   @Transactional
   @Audit("AUDIT.INSERT")
   default Long uploadFileData(CoreFileBean fileBean, MultipartFile file) {
      return this.lcdpUploadFile(fileBean, file);
   }

   @Mapping(
      value = "Lcdp删除附件",
      type = MappingType.DELETE
   )
   @Transactional
   @Audit("AUDIT.DELETE")
   default LcdpResultDTO deleteFileData(RestJsonWrapperBean wrapper) {
      return this.lcdpDeleteFile(wrapper);
   }

   @Mapping(
      value = "Lcdp查询树数据",
      type = MappingType.SELECT
   )
   default List<LcdpTreeDataDTO> selectTreeData(RestJsonWrapperBean wrapper) {
      return this.selectLcdpTree(wrapper);
   }

   @Mapping(
      value = "Lcdp查询TreeGrid数据",
      type = MappingType.SELECT
   )
   default Page<Map<String, Object>> selectTreePaginationData(RestJsonWrapperBean wrapper) {
      return this.selectLcdpTreePagination(wrapper);
   }

   @Mapping(
      value = "Lcdp数据上移下移",
      type = MappingType.UPDATE
   )
   @Transactional
   @Audit("AUDIT.SWAP")
   default void swap(RestJsonWrapperBean wrapper) {
      String column = wrapper.getParamValue("column");
      if (StringUtils.isEmpty(column)) {
         column = "orderno";
      }

      String firstId = wrapper.getParamValue("firstId");
      String secondId = wrapper.getParamValue("secondId");
      Map<String, Object> first = this.getMapDao().selectById(TableContext.of(this.getTable()), firstId);
      Map<String, Object> second = this.getMapDao().selectById(TableContext.of(this.getTable()), secondId);
      Object firstValue = CollectionUtils.getValueIgnorecase(first, column);
      Object secondValue = CollectionUtils.getValueIgnorecase(second, column);
      first.put(column, secondValue);
      second.put(column, firstValue);
      this.getMapDao().update(TableContext.of(this.getTable()), Arrays.asList(first, second), column);
   }

   default List<Map<String, Object>> transformSelectData(List<Map<String, Object>> mapList, String textField, String valueField) {
      return ObjectUtils.isEmpty(mapList) ? CollectionUtils.emptyList() : mapList.stream().map(item -> {
         Map<String, Object> option = new HashMap<>();
         option.put("TEXT", String.valueOf(item.get(textField)));
         option.put("VALUE", String.valueOf(item.get(valueField)));
         return option;
      }).collect(Collectors.toList());
   }

   default <T> T selectOneByMapperId(String mapperId) {
      String statement = this.parseMapperId(mapperId);
      return ApplicationContextHelper.getBean(SqlSessionTemplate.class).selectOne(statement);
   }

   default <T> T selectOneByMapperId(String mapperId, Object parameter) {
      String statement = this.parseMapperId(mapperId);
      return ApplicationContextHelper.getBean(SqlSessionTemplate.class).selectOne(statement, parameter);
   }

   default void setDefault(Map<String, Object> dataMap, Map<String, Object> defaultMap) {
      if (defaultMap != null) {
         defaultMap.forEach((k, v) -> {
            if (dataMap.containsKey(k)) {
               Object val = dataMap.get(k);
               if (val == null) {
                  dataMap.put(k, v);
               }
            } else {
               dataMap.put(k, v);
            }
         });
      }
   }

   default void setDefault(List<Map<String, Object>> dataMapList, Map<String, Object> defaultMap) {
      dataMapList.stream().forEach(map -> this.setDefault((Map<String, Object>)map, defaultMap));
   }
}
