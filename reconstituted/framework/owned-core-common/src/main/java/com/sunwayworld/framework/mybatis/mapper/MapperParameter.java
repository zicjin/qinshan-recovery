package com.sunwayworld.framework.mybatis.mapper;

import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.beans.BeanPropertyEvent;
import com.sunwayworld.framework.beans.BeanPropertyHelper;
import com.sunwayworld.framework.beans.BeanPropertyListener;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.database.context.EntityColumnContext;
import com.sunwayworld.framework.database.context.EntityContext;
import com.sunwayworld.framework.database.dialect.DialectRepository;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean.TargetTableFilterData;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class MapperParameter extends HashMap<String, Object> {
   private static final long serialVersionUID = -5846726410121110379L;
   public static final String FILTER_PARAM_PATTERNS = "filterParamPatterns";
   public static final String FILTER_BY_ITEM_LIST = "filter_by_item_list";
   public static final String TARGET_FILTER = "targetFilter";
   public static final String ADVANCED_FILTER_PATTERNS = "advancedFilterPatterns";
   public static final String MULTI_PROPERTY_FILTER_PATTERN = "multiPropertyFilterPattern";

   public MapperParameter() {
      this.enhanceLoginUser();
   }

   public MapperParameter(int initialCapacity) {
      super(initialCapacity);
      this.enhanceLoginUser();
   }

   public MapperParameter(Map<? extends String, ? extends Object> m) {
      super(m);
      this.enhanceLoginUser();
   }

   public MapperParameter(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
      this.enhanceLoginUser();
   }

   public void setTableName(String tableName) {
      this.put("service_table", tableName);
   }

   public void setDetailQueries() {
      this.put("queries_detail", "1");
   }

   public void setRawQueries() {
      this.put("queries_raw", "1");
   }

   public boolean isRawQueries() {
      return "1".equals(this.get("queries_raw"));
   }

   public void setSearchableQueries() {
      this.put("queries_searchable", "1");
   }

   public void setAuditableQueries() {
      this.put("queries_auditable", "1");
   }

   public void setChoosableQueries() {
      this.put("queries_choosable", "1");
   }

   public void setSelectableQueries() {
      this.put("queries_selectable", "1");
   }

   public void setSuspendableQueries() {
      this.put("queries_suspendable", "1");
   }

   public void setUndoableQueries() {
      this.put("queries_undoable", "1");
   }

   public void setWithdrawableQueries() {
      this.put("queries_withdrawable", "1");
   }

   public void setFilter(SearchFilter filter) {
      if (!filter.getFilterMap().isEmpty()) {
         this.putAll(filter.getFilterMap());
      }

      if (!filter.getStdFilterList().isEmpty()) {
         this.addFilterParamPatterns(filter.getStdFilterList().toArray(new FilterParamPattern[0]));
      }
   }

   public void addFilterParamPatterns(FilterParamPattern... paramPatterns) {
      FilterParamPattern[] patterns = (FilterParamPattern[])this.get("filterParamPatterns");
      if (patterns == null) {
         patterns = new FilterParamPattern[0];
      }

      this.put("filterParamPatterns", ArrayUtils.concat(patterns, paramPatterns));
   }

   public FilterParamPattern[] getFilterParamPatterns() {
      return (FilterParamPattern[])this.get("filterParamPatterns");
   }

   public <T extends Persistable<?>> void setFilter(List<T> itemList, String... filterColumns) {
      if (!itemList.isEmpty() && filterColumns.length > 0) {
         List<Map<String, Object>> mapList = new ArrayList<>();

         for (T item : itemList) {
            Map<String, Object> map = new HashMap<>();
            if (!ArrayUtils.isEmpty(filterColumns)) {
               for (String filterColumn : filterColumns) {
                  map.put(filterColumn, BeanUtils.getPropertyValue(item, filterColumn));
               }
            } else if (BeanPropertyListener.class.isAssignableFrom(item.getClass())) {
               List<BeanPropertyEvent> eventList = ((BeanPropertyListener)item).getChangedPropertyEventList();
               eventList.stream().forEach(e -> map.put(e.getName(), e.getNewValue()));
            } else {
               for (BeanPropertyDescriptor pd : BeanPropertyHelper.getBeanPropertyDescriptorList(ClassUtils.getRawType(item.getClass()))) {
                  Object value = pd.getPropertyValue(item);
                  if (value != null) {
                     map.put(pd.getName(), value);
                  }
               }

               map.putAll(item.getExt$());
            }

            if (!map.isEmpty()) {
               mapList.add(map);
            }
         }

         this.put("filter_by_item_list", mapList);
      }
   }

   public List<Map<String, Object>> getFilterByItemList() {
      return (List<Map<String, Object>>)this.get("filter_by_item_list");
   }

   public void setTargetFilter(TargetTableFilterData filterData) {
      this.put("targetFilter", filterData);
   }

   public TargetTableFilterData getTargetFilter() {
      return (TargetTableFilterData)this.get("targetFilter");
   }

   public void setAdvancedFilterPatterns(AdvancedFilterPattern[] filterPatterns) {
      this.put("advancedFilterPatterns", filterPatterns);
   }

   public AdvancedFilterPattern[] getAdvancedFilterPatterns() {
      return (AdvancedFilterPattern[])this.get("advancedFilterPatterns");
   }

   public void setMultiPropertyFilterPattern(MultiPropertyFilterPattern filterPattern) {
      this.put("multiPropertyFilterPattern", filterPattern);
   }

   public MultiPropertyFilterPattern getMultiPropertyFilterPattern() {
      return (MultiPropertyFilterPattern)this.get("multiPropertyFilterPattern");
   }

   public void setSuspendedFlag(String lastSuspendedFlag) {
      SearchFilter filter = SearchFilter.instance().match("lastSuspendedFlag", lastSuspendedFlag).filter(MatchPattern.EQ);
      this.setFilter(filter);
   }

   public void setProcessStatus(String processStatusColumn, ProcessStatus status) {
      SearchFilter filter = SearchFilter.instance();
      switch (status) {
         case DRAFT:
         case DRAFT_REJECTED:
            filter.match(processStatusColumn, ProcessStatus.DRAFT.name().toLowerCase()).filter(MatchPattern.SB);
            break;
         case APPROVE:
         case APPROVE_REJECTED:
            filter.match(processStatusColumn, ProcessStatus.APPROVE.name().toLowerCase()).filter(MatchPattern.SB);
            break;
         case DONE:
            filter.match(processStatusColumn, ProcessStatus.DONE.name().toLowerCase()).filter(MatchPattern.SEQ);
            break;
         default:
            filter.match(processStatusColumn, status.name().toLowerCase()).filter(MatchPattern.SPECIAL);
      }

      this.setFilter(filter);
   }

   public void setActivatedFlag(String activatedFlag) {
      SearchFilter filter = SearchFilter.instance().match("activatedFlag", activatedFlag).filter(MatchPattern.EQ);
      this.setFilter(filter);
   }

   public void setAuthorityParameter(String authority) {
      this.put("authority_" + authority, "1");
   }

   public void setAuditAuthority() {
      this.setAuthorityParameter("audit");
   }

   public void setOrgAuthority() {
      this.setAuthorityParameter("org");
   }

   public void setCurrentAndChildOrgAuthority(String orgColumn) {
      CoreOrgService orgService = ApplicationContextHelper.getBean(CoreOrgService.class);
      List<CoreOrgBean> childOrgList = orgService.selectChildOrgList(LocalContextHelper.getLoginOrgId());
      List<String> permittedOrgIdList = childOrgList.stream().map(CoreOrgBean::getId).collect(Collectors.toList());
      SearchFilter filter = SearchFilter.instance();
      if (permittedOrgIdList.isEmpty()) {
         filter.match(orgColumn, LocalContextHelper.getLoginOrgId()).filter(MatchPattern.EQ);
      } else {
         permittedOrgIdList.add(LocalContextHelper.getLoginOrgId());
         filter.match(orgColumn, permittedOrgIdList).filter(MatchPattern.OR);
      }

      this.setFilter(filter);
   }

   public void setOrderParam(String name, String direction) {
      List<Pair<String, String>> orderParamList = (List<Pair<String, String>>)this.get("orderParams");
      if (orderParamList == null) {
         orderParamList = new ArrayList<>();
      }

      orderParamList.add(Pair.of(name, direction));
      this.put("orderParams", orderParamList);
   }

   public boolean hasOrderParam() {
      List<?> orderParamList = (List<?>)this.get("orderParams");
      return orderParamList != null && !orderParamList.isEmpty();
   }

   public void setSelectByRoleIdParam(Long roleId) {
      this.put("select_by_roleId", roleId);
   }

   public void setSelectByRoleIdExclusiveParam(Long roleId) {
      this.put("select_by_roleId_exclusive", roleId);
   }

   public void setSimpleColumns(String simpleColumns) {
      this.put("select_columns", simpleColumns);
   }

   public static boolean hasOrderParams(List<Pair<String, String>> orderParamList, String ignoreProperties) {
      if (StringUtils.isBlank(ignoreProperties)) {
         return !CollectionUtils.isEmpty(orderParamList);
      } else if (CollectionUtils.isEmpty(orderParamList)) {
         return false;
      } else {
         String[] ignorePropertyArray = ignoreProperties.split(",");

         for (String ignoreProperty : ignorePropertyArray) {
            orderParamList.removeIf(p -> ignoreProperty.trim().equalsIgnoreCase(p.getFirst()));
         }

         return !orderParamList.isEmpty();
      }
   }

   public void reviseParams(EntityContext entityContext) {
      if (entityContext != null) {
         if ("postgresql".equals(DialectRepository.getDialect().getDatabase())) {
            List<EntityColumnContext> columnContextList = entityContext.getColumnContextList();
            if (!CollectionUtils.isEmpty(columnContextList)) {
               CaseInsensitiveLinkedMap<Class<?>> columnClassMap = new CaseInsensitiveLinkedMap<>(columnContextList.size());

               for (EntityColumnContext columnContext : columnContextList) {
                  columnClassMap.put(columnContext.getColumnName(), columnContext.getType());
               }

               AdvancedFilterPattern[] advancedFilterPatterns = this.getAdvancedFilterPatterns();
               if (advancedFilterPatterns != null) {
                  for (AdvancedFilterPattern advancedFilterPattern : advancedFilterPatterns) {
                     Object oldValue = advancedFilterPattern.getValue();
                     String columnName = advancedFilterPattern.getName();
                     String matchPattern = advancedFilterPattern.getMatchPattern();
                     MatchPattern pattern = MatchPattern.getPatternIfPossible(matchPattern);
                     if (oldValue != null) {
                        Class<?> targetClass;
                        if (pattern != null && pattern.getType() == String.class) {
                           targetClass = String.class;
                        } else {
                           targetClass = columnClassMap.get(columnName);
                        }

                        if (targetClass != null && ConvertUtils.canConvert(oldValue, targetClass)) {
                           Object value = ConvertUtils.convert(oldValue, targetClass);
                           if (!oldValue.equals(value)) {
                              advancedFilterPattern.setValue(value);
                           }
                        }
                     }
                  }
               }

               List<Map<String, Object>> filterByItemList = this.getFilterByItemList();
               if (filterByItemList != null) {
                  for (Map<String, Object> filterByItem : filterByItemList) {
                     for (Entry<String, Object> entry : filterByItem.entrySet()) {
                        Object oldValue = entry.getValue();
                        String columnName = entry.getKey();
                        if (oldValue != null) {
                           Class<?> targetClassx = columnClassMap.get(columnName);
                           if (targetClassx != null && ConvertUtils.canConvert(oldValue, targetClassx)) {
                              Object value = ConvertUtils.convert(oldValue, targetClassx);
                              if (!oldValue.equals(value)) {
                                 entry.setValue(value);
                              }
                           }
                        }
                     }
                  }
               }

               FilterParamPattern[] filterParamPatterns = this.getFilterParamPatterns();
               if (filterParamPatterns != null) {
                  for (FilterParamPattern filterParamPattern : filterParamPatterns) {
                     String matchPattern = filterParamPattern.getMatchPattern();
                     MatchPattern pattern = MatchPattern.getPatternIfPossible(matchPattern);
                     Map<String, Object> filterParamPatternMap = filterParamPattern.getMap();

                     for (Entry<String, Object> entryx : filterParamPatternMap.entrySet()) {
                        Object oldValue = entryx.getValue();
                        String columnName = entryx.getKey();
                        if (oldValue != null) {
                           Class<?> targetClassx;
                           if (pattern != null && pattern.getType() == String.class) {
                              targetClassx = String.class;
                           } else {
                              targetClassx = columnClassMap.get(columnName);
                           }

                           if (targetClassx != null && ConvertUtils.canConvert(oldValue, targetClassx)) {
                              Object value = ConvertUtils.convert(oldValue, targetClassx);
                              if (!oldValue.equals(value)) {
                                 entryx.setValue(value);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void enhanceLoginUser() {
      if (LocalContextHelper.isUserLogin()) {
         if (!this.containsKey("loginUser")) this.put("loginUser", LocalContextHelper.getLoginUser());
         if (!this.containsKey("loginOrgId")) this.put("loginOrgId", LocalContextHelper.getLoginOrgId());
         if (!this.containsKey("loginOrgName")) this.put("loginOrgName", LocalContextHelper.getLoginOrgName());
         if (!this.containsKey("loginRoleId")) this.put("loginRoleId", LocalContextHelper.getLoginRoleId());
         if (!this.containsKey("loginRoleName")) this.put("loginRoleName", LocalContextHelper.getLoginRoleName());
      }
   }
}
