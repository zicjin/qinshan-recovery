package com.sunwayworld.cloud.module.item.customds.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomColumnDTO;
import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsBean;
import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsDTO;
import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsFieldBean;
import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsJoinFieldDTO;
import com.sunwayworld.cloud.module.item.customds.bean.CoreDsModelDTO;
import com.sunwayworld.cloud.module.item.customds.persistent.dao.CoreCustomDsDao;
import com.sunwayworld.cloud.module.item.customds.service.CoreCustomDsFieldService;
import com.sunwayworld.cloud.module.item.customds.service.CoreCustomDsService;
import com.sunwayworld.cloud.module.item.customds.service.CoreCustomDsSqlService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.context.ColumnContext;
import com.sunwayworld.framework.database.core.DatabaseManager;
import com.sunwayworld.framework.database.dialect.DialectRepository;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.database.sql.ExternalLogicalSql;
import com.sunwayworld.framework.database.sql.SqlHelper;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreCustomDsServiceImpl implements CoreCustomDsService {
   private static Logger logger = LogManager.getLogger(CoreCustomDsServiceImpl.class);
   @Autowired
   private CoreCustomDsDao coreCustomDsDao;
   @Autowired
   @Lazy
   private GlobalMapper globalMapper;
   @Autowired
   private CoreCustomDsFieldService dsFieldService;
   @Autowired
   private CoreCustomDsSqlService dsSqlService;

   public CoreCustomDsDao getDao() {
      return this.coreCustomDsDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      CoreCustomDsBean coreCustomDs = jsonWrapper.parseUnique(CoreCustomDsBean.class);
      coreCustomDs.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(coreCustomDs);
      return coreCustomDs.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   public void save(Long id, RestJsonWrapperBean wrapper) {
      CoreCustomDsBean newDsBean = wrapper.parseUnique(CoreCustomDsBean.class);
      CoreCustomDsBean olddsBean = this.selectById(id);
      if (!StringUtils.isBlank(newDsBean.getDsBody()) && !StringUtils.equals(olddsBean.getDsBody(), newDsBean.getDsBody())) {
         String dsSql = this.getDsSqlFromBody(newDsBean.getDsBody());
         if (!DialectRepository.getDialect().getDatabase().equals("postgresql")) {
            dsSql = dsSql.toUpperCase();
         }

         newDsBean.setDsSql(dsSql);
         this.updateDsFieldListBySql(id, dsSql);
      }

      this.getDao().updateIfChanged(newDsBean);
   }

   @Transactional
   @Override
   public void insertDsFields(Long dsId, RestJsonWrapperBean wrapper) {
      List<CoreCustomDsFieldBean> fieldList = wrapper.parse(CoreCustomDsFieldBean.class);
      fieldList.forEach(e -> {
         e.setId(ApplicationContextHelper.getNextIdentity());
         e.setDsId(dsId);
         e.setOrderNo(e.getId());
      });
      this.dsFieldService.getDao().insert(fieldList);
   }

   @Override
   public List<CoreCustomDsDTO> getActiveCustomDsList() {
      CoreCustomDsBean customDsBean = new CoreCustomDsBean();
      customDsBean.setActivatedFlag("1");
      List<CoreCustomDsBean> beanList = this.getDao().selectList(customDsBean, new Order[0]);
      List<CoreCustomDsDTO> dtoList = new ArrayList<>();

      for (CoreCustomDsBean coreCustomDsBean : beanList) {
         CoreCustomDsDTO dto = new CoreCustomDsDTO();
         dto.setId(coreCustomDsBean.getId());
         dto.setDsName(coreCustomDsBean.getDsName());
         dto.setDsSql(coreCustomDsBean.getDsSql());
         dto.setFieldList(
            this.dsFieldService.selectListByFilter(SearchFilter.instance().match("DSID", coreCustomDsBean.getId()).filter(MatchPattern.EQ), new Order[0])
         );
         dtoList.add(dto);
      }

      return dtoList;
   }

   @Override
   public List<Map<String, Object>> getCustomDsDataById(Long dsId, RestJsonWrapperBean wrapper) {
      if (dsId == null) {
         return CollectionUtils.emptyList();
      } else {
         CoreCustomDsBean customDsBean = this.getDao().selectById(dsId);
         List<Map<String, Object>> resultMapList = null;
         List<Map<String, Object>> resultMapListByUpperKey = new ArrayList<>();
         MapperParameter parameter = wrapper.extractMapFilter();
         String sql = this.sqlReplaceParam(customDsBean.getDsSql(), parameter);
         ArrayList<String> sqlList = Lists.newArrayList(sql);
         parameter.put("sqlList", sqlList);

         try {
            resultMapList = this.globalMapper.selectListBySqlListUnionAllAppendWhere(parameter);
            resultMapList.forEach(map -> {
               Map<String, Object> upperKeyMap = new HashMap<>();
               map.forEach((key, value) -> upperKeyMap.put(key.toUpperCase(), map.get(key)));
               resultMapListByUpperKey.add(upperKeyMap);
            });
            return resultMapListByUpperKey;
         } catch (Exception var10) {
            throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.ITEM.CUSTOM_DSES.TIP.SQL_ERROR") + customDsBean.getDsName());
         }
      }
   }

   @Override
   public List<Map<String, Object>> previewSql(Long id, RestJsonWrapperBean wrapper) {
      CoreCustomDsBean dsBean = this.getDao().selectById(id);
      String sqlContent = dsBean.getDsSql();
      sqlContent = this.sqlReplaceParam(sqlContent, wrapper.extractMapFilter());
      if (DialectRepository.getDialect().getDatabase().equals("sqlserver") && !SqlHelper.hasOrderSegment(sqlContent)) {
         sqlContent = sqlContent + " ORDER BY 1 ";
      }

      sqlContent = DialectRepository.getDialect().getLimitSql(sqlContent, 0, 30);
      new ArrayList();
      List<Map<String, Object>> returnResultMapList = new ArrayList<>();

      try {
         List<ColumnContext> columnContextList = DatabaseManager.selectColumnContextList(sqlContent);
         List<CoreCustomDsFieldBean> dsFieldList = this.dsFieldService
            .selectListByFilter(SearchFilter.instance().match("dsId", id).filter(MatchPattern.EQ), new Order[0]);

         for (Map<String, Object> map : this.globalMapper.selectList(sqlContent)) {
            if (map != null) {
               Map<String, Object> valueMap = new HashMap<>();
               map.forEach(
                  (key, value) -> {
                     columnContextList.stream()
                        .filter(c -> LocalDateTime.class.equals(c.getType()) || LocalDate.class.equals(c.getType()))
                        .forEach(dataBaseColumn -> {
                           if (dataBaseColumn.getColumnName().equals(key) && value != null) {
                              if (LocalDate.class.equals(dataBaseColumn.getType())) {
                                 map.replace(key, DateTimeUtils.formatLocalDate(ConvertUtils.convert(value, LocalDate.class)));
                              } else if (LocalDateTime.class.equals(dataBaseColumn.getType())) {
                                 map.replace(key, DateTimeUtils.formatLocalDateTime(ConvertUtils.convert(value, LocalDateTime.class)));
                              }
                           }
                        });
                     dsFieldList.forEach(f -> {
                        if (key.equalsIgnoreCase(f.getColumnName()) && !StringUtils.isBlank(f.getAliasName())) {
                           valueMap.put(f.getAliasName(), map.get(key));
                        } else {
                           valueMap.put(key.toUpperCase(), map.get(key));
                        }
                     });
                  }
               );
               returnResultMapList.add(valueMap);
            }
         }

         return returnResultMapList;
      } catch (Exception var12) {
         logger.info("自定义数据源 " + dsBean.getDsName() + " 的SQL:\n" + sqlContent);
         throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.ITEM.CUSTOM_DSES.TIP.SQL_PREVIEW_ERROR"));
      }
   }

   public CoreCustomDsBean selectDetail(Long id) {
      CoreCustomDsBean customDsBean = this.getDao().selectById(id);
      if (!StringUtils.isBlank(customDsBean.getDsBody())) {
         customDsBean.setDsBody(customDsBean.getDsBody().trim());
      }

      return customDsBean;
   }

   @Override
   public List<String> getPreviewColumnList(Long id, RestJsonWrapperBean wrapper) {
      List<CoreCustomDsFieldBean> dsFieldList = this.dsFieldService
         .selectListByFilter(SearchFilter.instance().match("dsId", id).filter(MatchPattern.EQ), new Order[0]);
      return dsFieldList.stream().map(f -> !StringUtils.isBlank(f.getAliasName()) ? f.getAliasName() : f.getColumnName()).collect(Collectors.toList());
   }

   @Override
   public List<Object> getColumnValues(Long dsId, RestJsonWrapperBean wrapper) {
      String columnName = wrapper.getParamValue("columnName");
      if (!StringUtils.isEmpty(columnName) && dsId != null) {
         CoreCustomDsFieldBean fieldBean = this.dsFieldService
            .selectFirstByFilter(
               SearchFilter.instance().match("DSID", dsId).filter(MatchPattern.EQ).match("columnName", columnName).filter(MatchPattern.CISEQ), new Order[0]
            );
         String fieldName = fieldBean == null ? columnName : fieldBean.getColumnName();
         CoreCustomDsBean customDsBean = this.getDao().selectById(dsId);
         String selectValueSql = String.format(
            "select distinct  %s from ( %s ) ds", fieldName, this.sqlReplaceParam(customDsBean.getDsSql(), wrapper.extractMapFilter())
         );
         List<Map<String, Object>> mapList = this.globalMapper.selectList(selectValueSql);
         return mapList.stream().filter(Objects::nonNull).map(map -> map.get(columnName)).collect(Collectors.toList());
      } else {
         return CollectionUtils.emptyList();
      }
   }

   private String getRevisedColumnType(String columnType) {
      switch (columnType) {
         case "Long":
         case "Double":
         case "Number":
            return "number";
         case "LocalDate":
            return "date";
         case "LocalDateTime":
            return "dateTime";
         default:
            return "string";
      }
   }

   private String getDsSqlFromBody(String dsBody) {
      StringBuilder jsonSb = new StringBuilder();
      jsonSb.append("[").append(dsBody).append("]");
      StringBuilder saveSql = new StringBuilder();
      List<CoreDsModelDTO> dsModelList = JSON.parseArray(jsonSb.toString(), CoreDsModelDTO.class);
      if (dsModelList != null && dsModelList.size() == 1) {
         Map<String, CoreDsModelDTO> allDsModelNodeMap = new HashMap<>();
         Map<String, String> leafNodeSqlMap = new HashMap<>();
         CoreDsModelDTO startModel = dsModelList.get(0);
         allDsModelNodeMap.put(startModel.getId(), startModel);
         this.getAllDsModelNodeMap(startModel.getChildren(), allDsModelNodeMap);
         this.buildSql(startModel.getId(), null, startModel.getChildren(), allDsModelNodeMap, leafNodeSqlMap);
         StringBuilder selectWhat = new StringBuilder();
         Set<String> leafNodeIdSet = leafNodeSqlMap.keySet();
         String startModelSelectWhat = this.getSelectWhatByNodeId(startModel.getId(), allDsModelNodeMap);
         selectWhat.append(startModelSelectWhat);
         leafNodeIdSet.forEach(k -> {
            if (selectWhat.length() > 0) {
               selectWhat.append(",");
            }

            String modelSelectWhat = this.getSelectWhatByNodeId(k, allDsModelNodeMap);
            selectWhat.append(modelSelectWhat);
         });
         String startSql = this.from(startModel);
         startSql = StringUtils.replace(startSql, "T" + startModel.getId() + ".*", StringUtils.removeEnd(selectWhat.toString(), " ,"));
         saveSql.append(startSql);
         leafNodeSqlMap.forEach((nodeId, sql) -> saveSql.append(sql));
      }

      return saveSql.toString();
   }

   private void getAllDsModelNodeMap(List<CoreDsModelDTO> childrenList, Map<String, CoreDsModelDTO> allDsModelNodeMap) {
      childrenList.forEach(e -> {
         allDsModelNodeMap.put(e.getId(), e);
         if (e.getChildren() != null && e.getChildren().size() > 0) {
            this.getAllDsModelNodeMap(e.getChildren(), allDsModelNodeMap);
         }
      });
   }

   private void buildSql(
      String startModelId,
      CoreDsModelDTO parentDsModel,
      List<CoreDsModelDTO> childrenList,
      Map<String, CoreDsModelDTO> allDsModelNodeMap,
      Map<String, String> leafNodeSqlMap
   ) {
      for (int i = 0; i < childrenList.size(); i++) {
         CoreDsModelDTO childDsModel = childrenList.get(i);
         if (childDsModel.getChildren() != null && childDsModel.getChildren().size() > 0) {
            this.buildSql(startModelId, childDsModel, childDsModel.getChildren(), allDsModelNodeMap, leafNodeSqlMap);
         } else if (!startModelId.equals(childDsModel.getParentId())) {
            String leafSql = this.joinParentNodeSql(startModelId, childDsModel.getId(), "", allDsModelNodeMap);
            String leafNodeId = this.getLeafNodeId(startModelId, childDsModel.getId(), allDsModelNodeMap);
            leafNodeSqlMap.put(leafNodeId, leafSql);
         } else {
            StringBuilder currentNodeJoinSql = new StringBuilder();
            if (childDsModel.getModelType().equals("table")) {
               currentNodeJoinSql.append(childDsModel.getModelId()).append(" T").append(childDsModel.getId());
            } else {
               String currentNodeSelfSql = this.from(childDsModel);
               currentNodeJoinSql.append(currentNodeSelfSql);
            }

            String leafSql = this.joinParentNodeSql(startModelId, childDsModel.getId(), currentNodeJoinSql.toString(), allDsModelNodeMap);
            leafNodeSqlMap.put(childDsModel.getId(), leafSql);
         }
      }
   }

   private String joinParentNodeSql(String startModelId, String currentModelId, String currentSubNodeSql, Map<String, CoreDsModelDTO> allDsModelNodeMap) {
      StringBuilder parentJoinCurrentSql = new StringBuilder();
      CoreDsModelDTO currentNode = allDsModelNodeMap.get(currentModelId);
      CoreDsModelDTO parentNode = allDsModelNodeMap.get(currentNode.getParentId());
      if (!startModelId.equals(currentNode.getParentId()) && !startModelId.equals(currentModelId)) {
         String parentNodeSql = this.from(parentNode);
         parentJoinCurrentSql.append(parentNodeSql);
         parentJoinCurrentSql.append(" ").append(this.joinType(parentNode.getJoinConditions().getJoinType()));
         if ("".equals(currentSubNodeSql)) {
            if (currentNode.getModelType().equals("table")) {
               parentJoinCurrentSql.append(currentNode.getModelId()).append(" T").append(currentNode.getId());
            } else {
               String currentNodeSelfSql = this.from(currentNode);
               parentJoinCurrentSql.append("(").append(currentNodeSelfSql).append(") T").append(currentNode.getId());
            }
         } else {
            parentJoinCurrentSql.append("(").append(currentSubNodeSql).append(") T").append(currentNode.getId());
         }

         parentJoinCurrentSql.append(" ON ")
            .append(this.getSqlWhereFromJoinField(currentModelId, currentNode.getParentId(), currentNode.getJoinConditions().getJoinFields()));
         currentSubNodeSql = parentJoinCurrentSql.toString();
         List<CoreDsModelDTO> sameParentIdNodeList = new ArrayList<>();
         allDsModelNodeMap.forEach((modelId, dsModelDTO) -> {
            String currentNodeParentId = currentNode.getParentId();
            if (dsModelDTO.getParentId() != null && dsModelDTO.getParentId().equals(currentNodeParentId) && !modelId.equals(currentModelId)) {
               sameParentIdNodeList.add(dsModelDTO);
            }
         });
         if (sameParentIdNodeList.size() > 0) {
            for (int i = 0; i < sameParentIdNodeList.size(); i++) {
               CoreDsModelDTO sameParentIdNode = sameParentIdNodeList.get(i);
               currentSubNodeSql = this.joinSampleParentNodeSql(startModelId, sameParentIdNode.getId(), currentSubNodeSql, allDsModelNodeMap);
            }
         }

         if (!startModelId.equals(currentNode.getParentId())) {
            currentSubNodeSql = this.joinParentNodeSql(startModelId, parentNode.getId(), currentSubNodeSql, allDsModelNodeMap);
         }

         return currentSubNodeSql;
      } else {
         CoreDsModelDTO startNode = allDsModelNodeMap.get(startModelId);
         parentJoinCurrentSql.append(" ").append(this.joinType(currentNode.getJoinConditions().getJoinType()));
         if (currentNode.getModelType().equals("table")) {
            parentJoinCurrentSql.append(currentNode.getModelId()).append(" T").append(currentNode.getId());
         } else {
            parentJoinCurrentSql.append("(").append(currentSubNodeSql).append(") T").append(currentNode.getId());
         }

         parentJoinCurrentSql.append(" ON ")
            .append(this.getSqlWhereFromJoinField(currentModelId, startNode.getId(), currentNode.getJoinConditions().getJoinFields()));
         return parentJoinCurrentSql.toString();
      }
   }

   private String joinSampleParentNodeSql(
      String startModelId, String sameParentIdNodeId, String sameParentOtherNodeSql, Map<String, CoreDsModelDTO> allDsModelNodeMap
   ) {
      StringBuilder sameParentIdNodeSql = new StringBuilder();
      CoreDsModelDTO sameParentIdNode = allDsModelNodeMap.get(sameParentIdNodeId);
      sameParentIdNodeSql.append(sameParentOtherNodeSql);
      sameParentIdNodeSql.append(" ").append(this.joinType(sameParentIdNode.getJoinConditions().getJoinType()));
      if (sameParentIdNode.getModelType().equals("table")) {
         sameParentIdNodeSql.append(sameParentIdNode.getModelId()).append(" T").append(sameParentIdNode.getId());
      } else {
         String currentNodeSelfSql = this.from(sameParentIdNode);
         sameParentIdNodeSql.append("(").append(currentNodeSelfSql).append(") T").append(sameParentIdNode.getId());
      }

      sameParentIdNodeSql.append(" ON ")
         .append(this.getSqlWhereFromJoinField(sameParentIdNodeId, sameParentIdNode.getParentId(), sameParentIdNode.getJoinConditions().getJoinFields()));
      return sameParentIdNodeSql.toString();
   }

   private String getLeafNodeId(String startModelId, String currentModelId, Map<String, CoreDsModelDTO> allDsModelNodeMap) {
      CoreDsModelDTO currentNode = allDsModelNodeMap.get(currentModelId);
      return startModelId.equals(currentNode.getParentId()) ? currentModelId : this.getLeafNodeId(startModelId, currentNode.getParentId(), allDsModelNodeMap);
   }

   private String from(CoreDsModelDTO dsModel) {
      StringBuilder fromSB = new StringBuilder();
      fromSB.append("SELECT T").append(dsModel.getId()).append(".* FROM (");
      if (dsModel.getModelType().equals("view")) {
         String sqlContent = this.dsSqlService.selectDetail(Long.valueOf(dsModel.getModelId())).getSqlContent();
         fromSB.append(sqlContent);
      } else {
         fromSB.append("SELECT * FROM ").append(dsModel.getModelId());
      }

      fromSB.append(") T").append(dsModel.getId());
      return fromSB.toString();
   }

   private String getSelectWhatByNodeId(String modelId, Map<String, CoreDsModelDTO> allDsModelNodeMap) {
      StringBuilder selectWhatSB = new StringBuilder();
      CoreDsModelDTO dsModel = allDsModelNodeMap.get(modelId);
      List<CoreCustomColumnDTO> otherNodeColumnDtoList = new ArrayList<>();
      allDsModelNodeMap.forEach((id, dsModelDTO) -> {
         if (!id.equals(modelId)) {
            otherNodeColumnDtoList.addAll(dsModelDTO.getColumns());
         }
      });
      List<String> ohterNodeColumnNameList = otherNodeColumnDtoList.stream()
         .map(c -> StringUtils.isBlank(c.getAliasName()) ? c.getColumnName() : c.getAliasName())
         .collect(Collectors.toList());
      List<CoreCustomColumnDTO> columns = dsModel.getColumns();
      String sqlLinkStr = I18nHelper.getMessage("CORE.MODULE.ITEM.CUSTOM_DSES.CONSTANT.SQL_LINK_DE");
      columns.forEach(c -> {
         if (selectWhatSB.length() > 0) {
            selectWhatSB.append(",");
         }

         selectWhatSB.append(" T").append(dsModel.getId()).append(".");
         if (DialectRepository.getDialect().getDatabase().equals("oracle")) {
            selectWhatSB.append("\"").append(c.getColumnName()).append("\"");
         } else if (DialectRepository.getDialect().getDatabase().equals("mysql")) {
            selectWhatSB.append("`").append(c.getColumnName()).append("`");
         } else {
            selectWhatSB.append(c.getColumnName());
         }

         selectWhatSB.append(" as ");
         if (!DialectRepository.getDialect().getDatabase().equals("postgresql")) {
            selectWhatSB.append("\"");
         }

         String columnName = StringUtils.isBlank(c.getAliasName()) ? c.getColumnName() : c.getAliasName();
         if (ohterNodeColumnNameList.contains(columnName)) {
            selectWhatSB.append(dsModel.getModelName()).append(sqlLinkStr);
         }

         selectWhatSB.append(columnName);
         if (!DialectRepository.getDialect().getDatabase().equals("postgresql")) {
            selectWhatSB.append("\"");
         }
      });
      return StringUtils.removeEnd(selectWhatSB.toString(), ",");
   }

   private String joinType(String joinType) {
      if ("InnerJoin".equals(joinType)) {
         return " JOIN ";
      } else {
         return "LeftJoin".equals(joinType) ? " LEFT JOIN " : " RIGHT JOIN ";
      }
   }

   private String getSqlWhereFromJoinField(String currentId, String parentId, List<CoreCustomDsJoinFieldDTO> joinFieldList) {
      StringBuilder sqlWhere = new StringBuilder();
      joinFieldList.forEach(
         e -> {
            if (sqlWhere.length() > 0) {
               sqlWhere.append(" AND ");
            }

            sqlWhere.append("T")
               .append(parentId)
               .append(".")
               .append(e.getMainModelField())
               .append(" ")
               .append(e.getOperate())
               .append(" ")
               .append("T")
               .append(currentId)
               .append(".")
               .append(e.getSubModelField());
         }
      );
      return sqlWhere.toString();
   }

   private String sqlReplaceParam(String sqlContent, MapperParameter parameter) {
      sqlContent = sqlContent.replaceAll("('?#)[^#]*(#'?)", "NULL");
      if (StringUtils.containsIgnoreCase(sqlContent, "{loginUser.id}")) {
         sqlContent = StringUtils.replaceIgnoreCase(sqlContent, "{loginUser.id}", "'" + LocalContextHelper.getLoginUserId() + "'");
      }

      if (StringUtils.containsIgnoreCase(sqlContent, "{loginUser.orgId}")) {
         sqlContent = StringUtils.replaceIgnoreCase(sqlContent, "{loginUser.orgId}", "'" + LocalContextHelper.getLoginUser().getOrgId() + "'");
      }

      if (StringUtils.containsIgnoreCase(sqlContent, "{loginOrgId}")) {
         sqlContent = StringUtils.replaceIgnoreCase(sqlContent, "{loginOrgId}", "'" + LocalContextHelper.getLoginOrgId() + "'");
      }

      if (StringUtils.containsIgnoreCase(sqlContent, "{loginRoleId}")) {
         sqlContent = StringUtils.replaceIgnoreCase(sqlContent, "{loginRoleId}", "" + LocalContextHelper.getLoginRoleId());
      }

      if (parameter != null) {
         for (String segment : StringUtils.substringsBetween(sqlContent, "/*", "*/")) {
            List<String> substringList = StringUtils.substringsBetween(segment, "{", "}");
            String sqlSegment = segment;
            boolean allReplaceFlag = true;

            for (String substring : substringList) {
               if (CollectionUtils.containsKeyIgnorecase(parameter, substring)) {
                  Object value = CollectionUtils.getValueIgnorecase(parameter, substring);
                  if (value != null && Number.class.isAssignableFrom(value.getClass())) {
                     sqlSegment = StringUtils.replace(sqlSegment, "${" + substring + "}", ObjectUtils.toString(value));
                  } else if (value == null) {
                     sqlSegment = StringUtils.replace(sqlSegment, "${" + substring + "}", "null");
                  } else if (ObjectUtils.toString(value).contains(",")) {
                     String[] valueList = ObjectUtils.toString(value).split(",");
                     sqlSegment = StringUtils.replace(
                        sqlSegment,
                        "${" + substring + "}",
                        StringUtils.join(Arrays.stream(valueList).map(item -> "'" + item + "'").collect(Collectors.toList()), ",")
                     );
                  } else {
                     sqlSegment = StringUtils.replace(sqlSegment, "${" + substring + "}", "'" + ObjectUtils.toString(value) + "'");
                  }
               } else {
                  allReplaceFlag = false;
               }
            }

            if (!substringList.isEmpty() && allReplaceFlag) {
               sqlContent = StringUtils.replace(sqlContent, "/*" + segment + "*/", ObjectUtils.toString(sqlSegment));
            }
         }
      }

      return ExternalLogicalSql.render(sqlContent);
   }

   private List<CoreCustomDsFieldBean> updateDsFieldListBySql(Long dsId, String sqlContent) {
      List<CoreCustomDsFieldBean> dsFieldList = new ArrayList<>();
      sqlContent = this.sqlReplaceParam(sqlContent, null);
      List<CoreCustomDsFieldBean> olddsFieldList = this.dsFieldService
         .selectListByFilter(SearchFilter.instance().match("dsId", dsId).filter(MatchPattern.EQ), new Order[0]);
      List<CoreCustomDsFieldBean> insertFieldList = new ArrayList<>();
      List<CoreCustomDsFieldBean> deleteFieldList = new ArrayList<>();

      try {
         List<ColumnContext> columnContextList = DatabaseManager.selectColumnContextList(sqlContent);
         List<String> columnNameList = columnContextList.stream().map(c -> c.getColumnName().toUpperCase()).collect(Collectors.toList());
         if (!olddsFieldList.isEmpty()) {
            List<String> dsSqlFieldColumnList = olddsFieldList.stream().map(d -> d.getColumnName().toUpperCase()).collect(Collectors.toList());
            olddsFieldList.forEach(e -> {
               if (!columnNameList.contains(e.getColumnName().toUpperCase())) {
                  deleteFieldList.add(e);
               }
            });
            columnContextList.forEach(
               e -> {
                  if (!dsSqlFieldColumnList.contains(e.getColumnName())) {
                     CoreCustomDsFieldBean dsFieldBean = new CoreCustomDsFieldBean();
                     dsFieldBean.setDsId(dsId);
                     dsFieldBean.setId(ApplicationContextHelper.getNextIdentity());
                     dsFieldBean.setColumnName(e.getColumnName());
                     dsFieldBean.setAliasName(e.getColumnName());
                     dsFieldBean.setFieldType(
                        this.getRevisedColumnType(
                           DialectRepository.getDialect().getJavaType(e.getColumnName().toUpperCase(), e.getDataType(), 1).getSimpleName()
                        )
                     );
                     insertFieldList.add(dsFieldBean);
                  }
               }
            );
         } else {
            columnContextList.forEach(
               e -> {
                  CoreCustomDsFieldBean dsFieldBean = new CoreCustomDsFieldBean();
                  dsFieldBean.setDsId(dsId);
                  dsFieldBean.setId(ApplicationContextHelper.getNextIdentity());
                  dsFieldBean.setColumnName(e.getColumnName());
                  dsFieldBean.setAliasName(e.getColumnName());
                  dsFieldBean.setFieldType(
                     this.getRevisedColumnType(DialectRepository.getDialect().getJavaType(e.getColumnName().toUpperCase(), e.getDataType(), 1).getSimpleName())
                  );
                  insertFieldList.add(dsFieldBean);
               }
            );
         }
      } catch (Exception var10) {
         logger.info("自定义数据源  的SQL报错:\n" + sqlContent);
         throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.ITEM.CUSTOM_DSES.TIP.SQL_ERROR"));
      }

      if (!insertFieldList.isEmpty()) {
         this.dsFieldService.getDao().insert(insertFieldList);
      }

      if (!deleteFieldList.isEmpty()) {
         this.dsFieldService.getDao().deleteByIdList(deleteFieldList.stream().map(e -> e.getId()).collect(Collectors.toList()));
      }

      return dsFieldList;
   }
}
