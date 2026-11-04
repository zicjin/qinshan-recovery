package com.sunwayworld.cloud.module.item.customquery.service.impl;

import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsBean;
import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsFieldBean;
import com.sunwayworld.cloud.module.item.customds.service.CoreCustomDsFieldService;
import com.sunwayworld.cloud.module.item.customds.service.CoreCustomDsService;
import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryBean;
import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryButtonBean;
import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryConditionBean;
import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryFieldBean;
import com.sunwayworld.cloud.module.item.customquery.presistent.dao.CoreCustomQueryDao;
import com.sunwayworld.cloud.module.item.customquery.presistent.mapper.CoreCustomQueryMapper;
import com.sunwayworld.cloud.module.item.customquery.service.CoreCustomQueryButtonService;
import com.sunwayworld.cloud.module.item.customquery.service.CoreCustomQueryConditionService;
import com.sunwayworld.cloud.module.item.customquery.service.CoreCustomQueryFieldService;
import com.sunwayworld.cloud.module.item.customquery.service.CoreCustomQueryService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.context.ColumnContext;
import com.sunwayworld.framework.database.core.DatabaseManager;
import com.sunwayworld.framework.database.dialect.DialectRepository;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.database.sql.SqlHelper;
import com.sunwayworld.framework.exception.database.JdbcException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.AdvancedFilterPattern;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.menu.bean.CoreMenuBean;
import com.sunwayworld.module.sys.menu.service.CoreMenuService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreCustomQueryServiceImpl implements CoreCustomQueryService {
   @Autowired
   private CoreCustomQueryDao coreCustomQueryDao;
   @Autowired
   private CoreCustomDsFieldService coreCustomDsFieldService;
   @Autowired
   private CoreCustomQueryFieldService coreCustomQueryFieldService;
   @Autowired
   private CoreCustomQueryConditionService coreCustomQueryConditionService;
   @Autowired
   private CoreCustomQueryButtonService coreCustomQueryButtonService;
   @Autowired
   private CoreMenuService coreMenuService;
   @Autowired
   private CoreCustomDsService coreCustomDsService;
   @Autowired
   private CoreCustomQueryMapper queryMapper;
   @Autowired
   private GlobalMapper globalMapper;

   public CoreCustomQueryDao getDao() {
      return this.coreCustomQueryDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      CoreCustomQueryBean coreCustomQuery = jsonWrapper.parseUnique(CoreCustomQueryBean.class);
      this.getDao().insert(coreCustomQuery);
      return coreCustomQuery.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void insertQueryFields(Long id, Long customDsId) {
      List<CoreCustomQueryFieldBean> fieldBeanList = this.coreCustomQueryFieldService
         .selectListByFilter(SearchFilter.instance().match("CUSTOMQUERYID", id).filter(MatchPattern.EQ), new Order[0]);
      if (!CollectionUtils.isEmpty(fieldBeanList)) {
         List<Long> idList = fieldBeanList.stream().map(e -> e.getId()).collect(Collectors.toList());
         this.coreCustomQueryFieldService.getDao().deleteByIdList(idList);
      }

      List<CoreCustomDsFieldBean> dsFieldList = this.coreCustomDsFieldService
         .selectListByFilter(SearchFilter.instance().match("DSID", customDsId).filter(MatchPattern.EQ), new Order[0]);
      List<CoreCustomQueryFieldBean> queryFieldList = new ArrayList<>();
      dsFieldList.forEach(e -> {
         CoreCustomQueryFieldBean bean = new CoreCustomQueryFieldBean();
         bean.setCustomQueryId(id);
         bean.setColumnName(e.getAliasName());
         bean.setFieldName(e.getColumnName());
         bean.setFieldType("string");
         bean.setShow("1");
         bean.setWidth(120);
         bean.setId(ApplicationContextHelper.getNextIdentity());
         bean.setOrderNo(bean.getId());
         queryFieldList.add(bean);
      });
      this.coreCustomQueryFieldService.getDao().insert(queryFieldList);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void insertQueryCondition(Long id) {
      CoreCustomQueryFieldBean fieldBean = this.coreCustomQueryFieldService.selectById(id);
      CoreCustomQueryConditionBean conditionBean = new CoreCustomQueryConditionBean();
      conditionBean.setId(ApplicationContextHelper.getNextIdentity());
      conditionBean.setCustomQueryId(fieldBean.getCustomQueryId());
      conditionBean.setFieldName(fieldBean.getFieldName());
      conditionBean.setTitle(fieldBean.getColumnName());
      conditionBean.setConditionCategory("string");
      conditionBean.setOrderNo(conditionBean.getId());
      this.coreCustomQueryConditionService.getDao().insert(conditionBean);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteQueryCondition(Long id) {
      CoreCustomQueryFieldBean fieldBean = this.coreCustomQueryFieldService.selectById(id);
      CoreCustomQueryConditionBean conditionBean = new CoreCustomQueryConditionBean();
      conditionBean.setFieldName(fieldBean.getFieldName());
      conditionBean.setCustomQueryId(fieldBean.getCustomQueryId());
      this.coreCustomQueryConditionService.getDao().deleteBy(conditionBean, "CUSTOMQUERYID", "FIELDNAME");
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void publish(Long id, RestJsonWrapperBean wrapper) {
      List<CoreMenuBean> coreMenuBeanList = wrapper.parse(CoreMenuBean.class);
      CoreCustomQueryBean coreCustomQueryBean = this.selectById(id);
      coreMenuBeanList.forEach(
         e -> {
            if (ObjectUtils.isEmpty(coreCustomQueryBean.getReportConfigLineId())) {
               e.setHref("/secure/core/module/item/custom-queries/page/grid-menu-list?customQueryId=" + e.getId());
            } else {
               e.setHref(
                  "/secure/core/module/item/custom-queries/page/report-menu-list?customQueryId="
                     + e.getId()
                     + "&reportConfigLineId="
                     + coreCustomQueryBean.getReportConfigLineId()
               );
            }
         }
      );
      this.coreMenuService.getDao().insert(coreMenuBeanList);
      coreCustomQueryBean.setPublishStatus("1");
      this.getDao().update(coreCustomQueryBean, new String[]{"PUBLISHSTATUS"});
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void revoke(Long customQueryId) {
      CoreMenuBean coreMenuBean = new CoreMenuBean();
      coreMenuBean.setId(customQueryId.toString());
      this.coreMenuService.getDao().deleteBy(coreMenuBean, "ID");
      CoreCustomQueryBean coreCustomQueryBean = new CoreCustomQueryBean();
      coreCustomQueryBean.setPublishStatus("0");
      coreCustomQueryBean.setId(customQueryId);
      this.getDao().update(coreCustomQueryBean, new String[]{"PUBLISHSTATUS"});
   }

   @Override
   public List<Map<String, Object>> buildHighQuery(Long id) {
      List<CoreCustomQueryConditionBean> conditionList = this.coreCustomQueryConditionService
         .selectListByFilter(SearchFilter.instance().match("CUSTOMQUERYID", id).filter(MatchPattern.EQ), new Order[0]);
      CoreCustomQueryBean coreCustomQueryBean = this.selectById(id);
      List<Map<String, Object>> list = CollectionUtils.emptyList();
      conditionList.forEach(e -> {
         Map<String, Object> map = new HashMap<>();
         map.put("id", ApplicationContextHelper.getNextIdentity());
         if ("select".equals(e.getConditionCategory())) {
            if ("system".equals(e.getDsCategory())) {
               map.put("category", e.getDataSource());
            } else if ("customSql".equals(e.getDsCategory())) {
               String sql = e.getCustomSql();
               if (!StringUtils.isBlank(sql)) {
                  if (StringUtils.containsIgnoreCase(sql, "{loginUser.id}")) {
                     sql = StringUtils.replaceIgnoreCase(sql, "{loginUser.id}", "'" + LocalContextHelper.getLoginUserId() + "'");
                  }

                  if (StringUtils.containsIgnoreCase(sql, "{loginUser.orgId}")) {
                     sql = StringUtils.replaceIgnoreCase(sql, "{loginUser.orgId}", "'" + LocalContextHelper.getLoginUser().getOrgId() + "'");
                  }

                  if (StringUtils.containsIgnoreCase(sql, "{loginOrgId}")) {
                     sql = StringUtils.replaceIgnoreCase(sql, "{loginOrgId}", "'" + LocalContextHelper.getLoginOrgId() + "'");
                  }

                  if (StringUtils.containsIgnoreCase(sql, "{loginRoleId}")) {
                     sql = StringUtils.replaceIgnoreCase(sql, "{loginRoleId}", "" + LocalContextHelper.getLoginRoleId());
                  }

                  List<Map<String, Object>> sqlResultList = this.globalMapper.selectList(sql);
                  List<Map<String, Object>> formatSqlList = new ArrayList<>();

                  for (Map<String, Object> sqlObjectMap : sqlResultList) {
                     Map<String, Object> sqlMap = new HashMap<>();
                     sqlMap.put("code", ObjectUtils.isEmpty(sqlObjectMap.get("CODE")) ? sqlObjectMap.get("code") : sqlObjectMap.get("CODE"));
                     sqlMap.put("text", ObjectUtils.isEmpty(sqlObjectMap.get("TEXT")) ? sqlObjectMap.get("text") : sqlObjectMap.get("TEXT"));
                     formatSqlList.add(sqlMap);
                  }

                  map.put("selectOption", formatSqlList);
               }
            }
         }

         if ("choose".equals(e.getConditionCategory())) {
            Map<String, Object> chooseMap = new HashMap<>();
            chooseMap.put("category", e.getDataSource());
            chooseMap.put("field", e.getChooseField());
            map.put("chooseOption", chooseMap);
         }

         if ("choose".equals(e.getConditionCategory())) {
            map.put("type", "select");
         } else {
            map.put("type", e.getConditionCategory());
         }

         if (ObjectUtils.isEmpty(coreCustomQueryBean.getReportConfigLineId())) {
            map.put("field", e.getFieldName().toLowerCase());
         } else {
            map.put("field", e.getFrReportDsName() + "_" + e.getFieldName().toLowerCase());
         }

         map.put("fieldName", StringUtils.isBlank(e.getTitle()) ? e.getFieldName() : e.getTitle());
         map.put("operator", e.getMatchPattern());
         list.add(map);
      });
      return list;
   }

   @Override
   public Page<Map<String, Object>> selectCustomMenuPagination(Long id, RestJsonWrapperBean wrapper) {
      CoreCustomQueryBean query = this.getDao().selectByIdIfPresent(id);
      String sql = "";
      if ("customSql".equals(query.getDsType())) {
         sql = query.getCustomSql();
      } else {
         CoreCustomDsBean dsBean = this.coreCustomDsService
            .selectFirstByFilter(SearchFilter.instance().match("id", query.getCustomDsId()).filter(MatchPattern.EQ), new Order[0]);
         if (dsBean == null) {
            return new Pagination<>();
         }

         sql = dsBean.getDsSql();
      }

      CoreCustomQueryFieldBean queryFieldBean = new CoreCustomQueryFieldBean();
      queryFieldBean.setCustomQueryId(id);
      List<CoreCustomQueryFieldBean> columnList = this.coreCustomQueryFieldService.getDao().selectList(queryFieldBean, Order.asc("ORDERNO"));
      MapperParameter parameter = wrapper.extractMapFilter();
      if (StringUtils.containsIgnoreCase(sql, "{loginUser.id}")) {
         sql = StringUtils.replaceIgnoreCase(sql, "{loginUser.id}", "'" + LocalContextHelper.getLoginUserId() + "'");
      }

      if (StringUtils.containsIgnoreCase(sql, "{loginUser.orgId}")) {
         sql = StringUtils.replaceIgnoreCase(sql, "{loginUser.orgId}", "'" + LocalContextHelper.getLoginUser().getOrgId() + "'");
      }

      if (StringUtils.containsIgnoreCase(sql, "{loginOrgId}")) {
         sql = StringUtils.replaceIgnoreCase(sql, "{loginOrgId}", "'" + LocalContextHelper.getLoginOrgId() + "'");
      }

      if (StringUtils.containsIgnoreCase(sql, "{loginRoleId}")) {
         sql = StringUtils.replaceIgnoreCase(sql, "{loginRoleId}", "" + LocalContextHelper.getLoginRoleId());
      }

      List<ColumnContext> columnContextList = DatabaseManager.selectColumnContextList(sql);
      boolean postgresql = DialectRepository.getDialect().getDatabase().equals("postgresql");
      if (SqlHelper.hasOrderSegment(sql)) {
         parameter.put("sql", SqlHelper.removeEndingOrderIfPresent(sql));
         String[] orders = StringUtils.split(SqlHelper.getOrderSegment(sql), ",");

         for (String order : orders) {
            String trimedOrder = StringUtils.trim(order);
            if (StringUtils.endsWithIgnoreCase(trimedOrder, "ASC")) {
               parameter.setOrderParam(StringUtils.trim(StringUtils.removeEndIgnoreCase(trimedOrder, "ASC")), "ASC");
            } else if (StringUtils.endsWithIgnoreCase(order.trim(), "DESC")) {
               parameter.setOrderParam(StringUtils.trim(StringUtils.removeEndIgnoreCase(trimedOrder, "DESC")), "DESC");
            } else {
               parameter.setOrderParam(trimedOrder, "ASC");
            }
         }
      } else {
         parameter.put("sql", sql);
         List<Pair<String, String>> orderParamList = (List<Pair<String, String>>)parameter.get("orderParams");
         if (CollectionUtils.isEmpty(orderParamList)) {
            parameter.setOrderParam(columnList.get(0).getFieldName(), "ASC");
         }
      }

      List<Pair<String, String>> orderParamListx = (List<Pair<String, String>>)parameter.get("orderParams");
      AdvancedFilterPattern[] advancedFilterPatterns = (AdvancedFilterPattern[])parameter.get("advancedFilterPatterns");
      if (postgresql && !CollectionUtils.isEmpty(orderParamListx)) {
         final String orderSql = sql;
         orderParamListx.stream().forEach(e -> e.setFirst(this.getSortField(orderSql, e.getFirst())));
         parameter.put("orderParams", orderParamListx);
      }

      if (postgresql && advancedFilterPatterns != null && advancedFilterPatterns.length > 0) {
         String sqlTemp = sql;
         Arrays.stream(advancedFilterPatterns).forEach(e -> e.setName(this.getSortField(sqlTemp, e.getName())));
         parameter.put("advancedFilterPatterns", advancedFilterPatterns);
      }

      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      Page<Map<String, Object>> page = MybatisPageHelper.get(rowBounds, () -> this.queryMapper.selectByPage(parameter));

      for (Map<String, Object> map : page.getRows()) {
         CollectionUtils.lowerCaseKey(map);
         columnContextList.stream()
            .filter(
               c -> "LocalDate".equals(DialectRepository.getDialect().getJavaType(c.getColumnName(), c.getDataType(), c.getScale()).getSimpleName())
                  || "LocalDateTime".equals(DialectRepository.getDialect().getJavaType(c.getColumnName(), c.getDataType(), c.getScale()).getSimpleName())
            )
            .forEach(
               c -> {
                  String key = c.getColumnName().toLowerCase();
                  Object value = map.get(key);
                  if (value != null) {
                     if ("LocalDate".equals(DialectRepository.getDialect().getJavaType(c.getColumnName(), c.getDataType(), c.getScale()).getSimpleName())) {
                        map.replace(key, DateTimeUtils.formatLocalDate(ConvertUtils.convert(value, LocalDate.class)));
                     } else if ("LocalDateTime"
                        .equals(DialectRepository.getDialect().getJavaType(c.getColumnName(), c.getDataType(), c.getScale()).getSimpleName())) {
                        map.replace(key, DateTimeUtils.formatLocalDateTime(ConvertUtils.convert(value, LocalDateTime.class)));
                     }
                  }
               }
            );
      }

      return page;
   }

   private String getSortField(String sql, String field) {
      return sql.contains("\"" + field.toUpperCase() + "\"") ? "\"" + field + "\"" : field;
   }

   @Override
   public RestValidationResultBean checkCustomSql(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      String sql = parameter.get("sql").toString();
      sql = this.sqlFormat(sql);

      try {
         List<ColumnContext> columnContextList = DatabaseManager.selectColumnContextList(sql);
         List<String> columnNameList = columnContextList.stream().map(c -> c.getColumnName().toLowerCase()).collect(Collectors.toList());
         if (columnNameList.size() != 2) {
            return new RestValidationResultBean(false, I18nHelper.getMessage("CORE.MODULE.ITEM.CUSTOM_QUERIES.VALIDA.LENGTH_ONLY_TWO"));
         }

         if (!columnNameList.contains("code") || !columnNameList.contains("text")) {
            return new RestValidationResultBean(false, I18nHelper.getMessage("CORE.MODULE.ITEM.CUSTOM_QUERIES.VALIDA.ONLY_CODE_TEXT"));
         }
      } catch (JdbcException var6) {
         return new RestValidationResultBean(false, var6.getCause().getMessage());
      }

      return new RestValidationResultBean(true);
   }

   @Override
   public List<Map<String, Object>> getSelectItems(Long customQueryId, String field) {
      List<CoreCustomQueryFieldBean> fieldList = this.coreCustomQueryFieldService
         .selectListByFilter(SearchFilter.instance().match("CUSTOMQUERYID", customQueryId).filter(MatchPattern.EQ), new Order[0]);
      List<Map<String, Object>> sqlResultList = CollectionUtils.emptyList();

      for (CoreCustomQueryFieldBean fieldBean : fieldList) {
         if (field.equals(fieldBean.getFieldName())) {
            String sql = fieldBean.getCustomSql();
            if (StringUtils.containsIgnoreCase(sql, "{loginUser.id}")) {
               sql = StringUtils.replaceIgnoreCase(sql, "{loginUser.id}", "'" + LocalContextHelper.getLoginUserId() + "'");
            }

            if (StringUtils.containsIgnoreCase(sql, "{loginUser.orgId}")) {
               sql = StringUtils.replaceIgnoreCase(sql, "{loginUser.orgId}", "'" + LocalContextHelper.getLoginUser().getOrgId() + "'");
            }

            if (StringUtils.containsIgnoreCase(sql, "{loginOrgId}")) {
               sql = StringUtils.replaceIgnoreCase(sql, "{loginOrgId}", "'" + LocalContextHelper.getLoginOrgId() + "'");
            }

            if (StringUtils.containsIgnoreCase(sql, "{loginRoleId}")) {
               sql = StringUtils.replaceIgnoreCase(sql, "{loginRoleId}", "" + LocalContextHelper.getLoginRoleId());
            }

            sqlResultList = this.globalMapper.selectList(sql);
         }
      }

      return sqlResultList;
   }

   @Override
   public void swapField(Long firstId, Long secondId) {
      this.coreCustomQueryFieldService.swap("ORDERNO", firstId, secondId);
   }

   @Override
   public void swapButton(Long firstId, Long secondId) {
      this.coreCustomQueryButtonService.swap("ORDERNO", firstId, secondId);
   }

   @Override
   public void swapCondition(Long firstId, Long secondId) {
      this.coreCustomQueryConditionService.swap("ORDERNO", firstId, secondId);
   }

   @Override
   public Page<CoreCustomQueryFieldBean> selectFieldsPagation(RestJsonWrapperBean wrapper) {
      return this.coreCustomQueryFieldService.selectPagination(wrapper);
   }

   @Override
   public Page<CoreCustomQueryButtonBean> selectButtonsPagation(RestJsonWrapperBean wrapper) {
      return this.coreCustomQueryButtonService.selectPagination(wrapper);
   }

   @Override
   public Page<CoreCustomQueryConditionBean> selectConditionsPagation(RestJsonWrapperBean wrapper) {
      return this.coreCustomQueryConditionService.selectPagination(wrapper);
   }

   @Override
   public Pagination<CoreCustomQueryFieldBean> selectChoosablePagination(RestJsonWrapperBean wrapper) {
      return (Pagination<CoreCustomQueryFieldBean>)this.coreCustomQueryFieldService.selectChoosablePagination(wrapper);
   }

   @Override
   public void insertQueryButtons(RestJsonWrapperBean wrapper) {
      this.coreCustomQueryButtonService.addQueryButtons(wrapper);
   }

   @Override
   public void insertQueryConditions(RestJsonWrapperBean wrapper) {
      this.coreCustomQueryConditionService.addQueryConditions(wrapper);
   }

   @Override
   public void deleteQueryButtons(RestJsonWrapperBean wrapper) {
      this.coreCustomQueryButtonService.delete(wrapper);
   }

   @Override
   public void deleteQueryConditions(RestJsonWrapperBean wrapper) {
      this.coreCustomQueryConditionService.delQueryConditions(wrapper);
   }

   @Override
   public List<CoreCustomQueryFieldBean> selectQueryFieldList(Long id) {
      return this.coreCustomQueryFieldService.selectListByFilter(SearchFilter.instance().match("CUSTOMQUERYID", id).filter(MatchPattern.EQ), new Order[0]);
   }

   @Override
   public List<CoreCustomQueryButtonBean> selectQueryButtonList(Long id) {
      return this.coreCustomQueryButtonService.selectListByFilter(SearchFilter.instance().match("CUSTOMQUERYID", id).filter(MatchPattern.EQ), new Order[0]);
   }

   private String sqlFormat(String sql) {
      if (StringUtils.containsIgnoreCase(sql, "{loginUser.id}")) {
         sql = StringUtils.replaceIgnoreCase(sql, "{loginUser.id}", "''");
      }

      if (StringUtils.containsIgnoreCase(sql, "{loginUser.orgId}")) {
         sql = StringUtils.replaceIgnoreCase(sql, "{loginUser.orgId}", "''");
      }

      if (StringUtils.containsIgnoreCase(sql, "{loginOrgId}")) {
         sql = StringUtils.replaceIgnoreCase(sql, "{loginOrgId}", "''");
      }

      if (StringUtils.containsIgnoreCase(sql, "{loginRoleId}")) {
         sql = StringUtils.replaceIgnoreCase(sql, "{loginRoleId}", "''");
      }

      return sql;
   }
}
