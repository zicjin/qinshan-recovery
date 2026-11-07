package com.sunwayworld.framework.restful.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.PageRequest;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.database.sql.SqlHelper;
import com.sunwayworld.framework.exception.HackingDataException;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.exception.OutOfRangeException;
import com.sunwayworld.framework.json.deserializer.DefaultJsonDeserializer;
import com.sunwayworld.framework.mybatis.mapper.AdvancedFilterPattern;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.MultiPropertyFilterPattern;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.JsonUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class RestJsonWrapperBean {
   private static final String VOID_VALUE = "VOID";
   private final Map<String, String> paramMap;
   private final List<List<String>> bodyList;
   @JSONField(
      serialize = false
   )
   private final List<GenericService<?, ?>> bodyServiceList;
   private final List<String> authorityList = new ArrayList<>();
   private Class<? extends MapperParameter> mapperParameterClass;
   private final Map<String, Object> extFilter = new HashMap<>();
   private final List<FilterParamPattern> extStdFilter = new ArrayList<>();
   private final String requestJson;
   private final RestJsonWrapperBean.LcdpParameter lcdpParameter = new RestJsonWrapperBean.LcdpParameter();
   private static ParserConfig parserConfig = new ParserConfig();

   public RestJsonWrapperBean() {
      this.paramMap = new HashMap<>();
      this.bodyList = CollectionUtils.emptyList();
      this.bodyServiceList = CollectionUtils.emptyList();
      this.requestJson = "";
   }

   public RestJsonWrapperBean(String filterJson) {
      this.paramMap = new HashMap<>();
      this.paramMap.put("f", filterJson);
      this.bodyList = CollectionUtils.emptyList();
      this.bodyServiceList = CollectionUtils.emptyList();
      this.requestJson = "";
   }

   public RestJsonWrapperBean(Map<String, String> paramMap, List<List<String>> bodyList, List<GenericService<?, ?>> bodyServiceList) {
      this.paramMap = Optional.ofNullable(paramMap).orElseGet(() -> CollectionUtils.emptyMap());
      this.bodyList = Optional.ofNullable(bodyList).orElseGet(() -> CollectionUtils.emptyList());
      this.bodyServiceList = Optional.ofNullable(bodyServiceList).orElseGet(() -> CollectionUtils.emptyList());
      this.requestJson = "";
   }

   public RestJsonWrapperBean(String requestjson, Map<String, String> paramMap, List<List<String>> bodyList, List<GenericService<?, ?>> bodyServiceList) {
      this.paramMap = Optional.ofNullable(paramMap).orElseGet(() -> CollectionUtils.emptyMap());
      this.bodyList = Optional.ofNullable(bodyList).orElseGet(() -> CollectionUtils.emptyList());
      this.bodyServiceList = Optional.ofNullable(bodyServiceList).orElseGet(() -> CollectionUtils.emptyList());
      this.requestJson = requestjson;
   }

   public static RestJsonWrapperBean newEmpty() {
      return new RestJsonWrapperBean(JSON.toJSONString(new HashMap()));
   }

   public Map<String, String> getParamMap() {
      return Collections.unmodifiableMap(this.paramMap);
   }

   public String getParamValue(String key) {
      return this.paramMap.get(key);
   }

   public void removeParam(String key) {
      this.paramMap.remove(key);
   }

   public void setParamValue(String key, String value) {
      this.paramMap.put(key, value);
   }

   public List<List<String>> getBodyList() {
      return this.bodyList;
   }

   public List<GenericService<?, ?>> getBodyServiceList() {
      return this.bodyServiceList;
   }

   public String getRequestJson() {
      return this.requestJson;
   }

   public String getFilterValue(String key) {
      String filterJson = this.getParamValue("f");
      if (!StringUtils.isEmpty(filterJson)) {
         Map<String, String> optionsMap = JSONObject.parseObject(filterJson, new TypeReference<Map<String, String>>() {}, new Feature[0]);
         return optionsMap.get(key);
      } else {
         return null;
      }
   }

   public <V> void setFilterValue(String key, V value) {
      String filterJson = this.getParamValue("f");
      if (StringUtils.isEmpty(filterJson)) {
         Map<String, V> map = new HashMap<>();
         map.put(key, value);
         this.paramMap.put("f", JSONObject.toJSONString(map));
      } else {
         Map<String, V> map = JSONObject.parseObject(filterJson, new TypeReference<Map<String, V>>() {}, new Feature[0]);
         map.put(key, value);
         this.paramMap.put("f", JSONObject.toJSONString(map));
      }
   }

   public void setAuthority(String authority) {
      this.setFilterValue("authority_" + authority, "1");
   }

   public void setQueries(String category) {
      this.setFilterValue("queries_" + category, "1");
   }

   public MapperParameter extractMapFilter() {
      return this.mapperParameterClass == null ? this.extractMapFilter(MapperParameter.class) : this.extractMapFilter(this.mapperParameterClass);
   }

   public <M extends MapperParameter> M extractMapFilter(Class<M> mapperParameterClass) {
      M parameter = (M)ClassUtils.newInstance(mapperParameterClass);
      this.extractFilter(parameter, this.getParamValue("f"));
      this.extractFilter(parameter, this.getParamValue("qf"));
      this.extractAdvancedFilter(parameter, this.getParamValue("af"));
      this.extractMultiPropertyFilter(parameter, this.getParamValue("mf"));
      if (!this.extStdFilter.isEmpty()) {
         parameter.addFilterParamPatterns(this.extStdFilter.toArray(new FilterParamPattern[0]));
      }

      String orderJson = this.getParamValue("o");
      if (!StringUtils.isEmpty(orderJson)) {
         JSONArray array = JSONObject.parseArray(orderJson);
         array.stream().forEach(a -> ((JSONObject)a).entrySet().forEach(e -> {
            if (SqlHelper.isRiskySqlSegment(e.getKey())) {
               throw new InvalidDataException("Order param contains risky words, the value is [" + e.getKey() + "]");
            } else {
               if ("asc".equals(e.getValue()) || "desc".equals(e.getValue())) {
                  parameter.setOrderParam(StringUtils.removeStartIgnoreCase(e.getKey(), "ext$."), (String)e.getValue());
               }
            }
         }));
      }

      String optionsJson = this.getParamValue("r");
      if (!StringUtils.isEmpty(optionsJson)) {
         Map<String, String> optionsMap = JSONObject.parseObject(optionsJson, new TypeReference<Map<String, String>>() {}, new Feature[0]);
         Map<String, Map<String, String>> resolvedOptionsMap = new HashMap<>();
         optionsMap.forEach((k, v) -> {
            Map<String, String> options = JSONObject.parseObject(v, new TypeReference<Map<String, String>>() {}, new Feature[0]);
            resolvedOptionsMap.put(k, options);
         });
         parameter.put("optionsMap", resolvedOptionsMap);
      }

      parameter.putAll(this.getExtFilter());
      return parameter;
   }

   public void setTotalFieldNames(String... totalFieldNames) {
      this.paramMap.put("tf", String.join(",", Arrays.asList(totalFieldNames)));
   }

   @JSONField(
      serialize = false
   )
   public String[] getSumProperties() {
      String tf = this.paramMap.get("tf");
      return StringUtils.isBlank(tf) ? new String[0] : StringUtils.split(tf, ",");
   }

   @JSONField(
      serialize = false
   )
   public String[] getCountProperties() {
      String cp = this.paramMap.get("cp");
      return StringUtils.isBlank(cp) ? new String[0] : StringUtils.split(cp, ",");
   }

   @JSONField(
      serialize = false
   )
   public String getOptionsProperty() {
      return this.paramMap.get("op");
   }

   public void removePageParams() {
      this.paramMap.put("n", "-1");
      this.paramMap.put("s", "-1");
   }

   public PageRequest extractPageRequest() {
      PageRequest pageRequest = new PageRequest();
      pageRequest.setPageNumber(NumberUtils.parseInt(this.getParamValue("n")));
      pageRequest.setPageSize(NumberUtils.parseInt(this.getParamValue("s")));
      pageRequest.setFilterJson(this.getParamValue("f"));
      pageRequest.setSumProperties(this.getSumProperties());
      pageRequest.setCountProperties(this.getCountProperties());
      pageRequest.setOptionsProperty(this.getOptionsProperty());
      return pageRequest;
   }

   public PageRowBounds extractPageRowBounds() {
      return new PageRowBounds(this.extractPageRequest());
   }

   public void setMapperParameterClass(Class<? extends MapperParameter> mapperParameterClass) {
      this.mapperParameterClass = mapperParameterClass;
   }

   public <T> T parseUnique(Class<T> beanClass) {
      int size = this.getBodyList().size();
      List<T> itemList = new ArrayList<>();

      for (int i = 0; i < size; i++) {
         GenericService<?, ?> service = this.getBodyServiceList().get(i);
         if (service != null && service.getDao().getType().equals(beanClass)) {
            List<String> contentList = this.getBodyList().get(i);
            if (contentList.size() > 1) {
               throw new OutOfRangeException("core.rest.json.unable-parse-unique-bean");
            }

            itemList.add(parseObject(contentList.get(0), beanClass));
         }
      }

      if (itemList.isEmpty()) {
         for (int ix = 0; ix < size; ix++) {
            if (this.getBodyServiceList().get(ix) == null) {
               List<String> contentList = this.getBodyList().get(ix);
               if (contentList.size() > 1) {
                  throw new OutOfRangeException("core.rest.json.unable-parse-unique-bean");
               }

               itemList.add(parseObject(contentList.get(0), beanClass));
            }
         }
      }

      if (itemList.size() != 1) {
         throw new OutOfRangeException("core.rest.json.unable-parse-unique-bean");
      } else {
         return itemList.get(0);
      }
   }

   public <T> List<T> parse(Class<T> beanClass) {
      int size = this.getBodyServiceList().size();
      List<T> itemList = new ArrayList<>();

      for (int i = 0; i < size; i++) {
         GenericService<?, ?> service = this.getBodyServiceList().get(i);
         if (service != null && service.getDao().getType().equals(beanClass)) {
            this.getBodyList().get(i).forEach(b -> itemList.add(parseObject(b, beanClass)));
         }
      }

      if (itemList.isEmpty()) {
         for (int ix = 0; ix < size; ix++) {
            if (this.getBodyServiceList().get(ix) == null) {
               this.getBodyList().get(ix).forEach(b -> itemList.add(parseObject(b, beanClass)));
            }
         }
      }

      return itemList;
   }

   public <T> List<T> parse(Class<T> beanClass, int index) {
      List<String> contentList = this.getBodyList().get(index);
      return contentList.stream().map(c -> parseObject(c, beanClass)).collect(Collectors.toList());
   }

   public <ID> List<ID> parseId(Class<ID> idType) {
      if (this.bodyList.size() > 1) {
         throw new OutOfRangeException("core.rest.json.unable-parse-id");
      } else {
         return this.bodyList.isEmpty() ? CollectionUtils.emptyList() : this.bodyList.get(0).stream().map(b -> {
            Map<String, Object> original = JSONObject.parseObject(b, Map.class);
            return ConvertUtils.convert(original.get("id"), idType);
         }).distinct().collect(Collectors.toList());
      }
   }

   public List<String> getAuthorityList() {
      return this.authorityList;
   }

   public void addAuthority(String authority) {
      this.authorityList.add("authority_" + authority);
   }

   public <V> void addExtFilter(String key, V value) {
      this.extFilter.put(key, value);
   }

   public Map<String, Object> getExtFilter() {
      return Collections.unmodifiableMap(this.extFilter);
   }

   public void addExtStdFilter(String key, Object value, MatchPattern matchPattern) {
      if (value == null) {
         this.extStdFilter.add(new FilterParamPattern(matchPattern.name(), key, value));
      } else if (List.class.isAssignableFrom(value.getClass())) {
         Map<String, Object> paramMap = new IdentityHashMap<>();

         for (Object v : (List)value) {
            paramMap.put(ObjectUtils.clone(key), v);
         }

         this.extStdFilter.add(new FilterParamPattern(matchPattern.name(), paramMap));
      } else if (value.getClass().isArray()) {
         Map<String, Object> paramMap = new IdentityHashMap<>();

         for (Object v : (Object[])value) {
            paramMap.put(ObjectUtils.clone(key), v);
         }

         this.extStdFilter.add(new FilterParamPattern(matchPattern.name(), paramMap));
      } else {
         this.extStdFilter.add(new FilterParamPattern(matchPattern.name(), key, value));
      }
   }

   public String getLcdpSql() {
      return this.lcdpParameter.getSql();
   }

   public void setLcdpSql(String lcdpSql) {
      this.lcdpParameter.setSql(lcdpSql);
   }

   public List<Order> getLcdpOrderList() {
      return this.lcdpParameter.getOrderList();
   }

   public void setLcdpOrderList(List<Order> lcdpOrderList) {
      this.lcdpParameter.setOrderList(lcdpOrderList);
   }

   public String getLcdpTreeNodeName() {
      return this.lcdpParameter.getTreeNodeName();
   }

   public void setLcdpTreeNodeName(String lcdpTreeNodeName) {
      this.lcdpParameter.setTreeNodeName(lcdpTreeNodeName);
   }

   private static final <T> T parseObject(String json, Class<T> clazz) {
      T proxy = BeanUtils.getPropertyListenerProxy(clazz);
      JsonUtils.parse(json, proxy);
      return proxy;
   }

   private final <M extends MapperParameter> void extractFilter(M parameter, String filterJson) {
      if (!StringUtils.isEmpty(filterJson)) {
         Map<String, Object> map = JSONObject.parseObject(filterJson, new TypeReference<Map<String, Object>>() {}, new Feature[0]);
         map.putAll(this.getExtFilter());
         List<RestJsonWrapperBean.FilterData> filterDataList = new ArrayList<>();
         map.entrySet()
            .forEach(
               e -> {
                  String keyx = StringUtils.replace(e.getKey(), "ext$.", "");
                  if (SqlHelper.isRiskySqlSegment(keyx)) {
                     throw new HackingDataException(keyx);
                  } else {
                     if ("target_filter".equals(keyx)) {
                        Map<String, String> taMap = JSONObject.parseObject((String)e.getValue(), new TypeReference<Map<String, String>>() {}, new Feature[0]);
                        RestJsonWrapperBean.TargetTableFilterData filterDatax = new RestJsonWrapperBean.TargetTableFilterData();
                        filterDatax.setType(taMap.get("type"));
                        filterDatax.setTargetTable(taMap.get("targetTable"));
                        filterDatax.setTargetMatchColumn(Objects.toString(taMap.get("targetMatchColumn"), "ID"));
                        filterDatax.setThisMatchColumn(Objects.toString(taMap.get("thisMatchColumn"), "ID"));
                        String filter = taMap.get("filter");
                        if (!StringUtils.isEmpty(filter)) {
                           JSONArray array = JSONObject.parseArray(filter);
                           int i = 0;

                           for (int j = array.size(); i < j; i++) {
                              JSONObject jsonObject = (JSONObject)array.get(i);
                              filterDatax.addTargetFilter(jsonObject.getString("targetFilterColumn"), jsonObject.getString("targetFilterValue"));
                           }
                        }

                        if (SqlHelper.isRiskySqlSegment(filterDatax.getTargetTable())) {
                           throw new HackingDataException("targetTable");
                        }

                        if (SqlHelper.isRiskySqlSegment(filterDatax.getTargetMatchColumn())) {
                           throw new HackingDataException("targetMatchColumn");
                        }

                        if (SqlHelper.isRiskySqlSegment(filterDatax.getThisMatchColumn())) {
                           throw new HackingDataException("thisMatchColumn");
                        }

                        filterDatax.getTargetFilter().keySet().forEach(k -> {
                           if (SqlHelper.isRiskySqlSegment(k)) {
                              throw new HackingDataException("targetFilterColumn");
                           }
                        });
                        parameter.setTargetFilter(filterDatax);
                     } else {
                        MatchPattern matchPattern = Arrays.stream(MatchPattern.values())
                           .filter(p -> StringUtils.endsWithIgnoreCase(keyx, "_" + p.name()))
                           .findFirst()
                           .orElse(null);
                        if (matchPattern == null) {
                           parameter.put(keyx, e.getValue());
                        } else {
                           filterDataList.addAll(parseFilterData(keyx, e.getValue(), matchPattern));
                        }
                     }
                  }
               }
            );
         Map<String, List<RestJsonWrapperBean.FilterData>> filterMap = new HashMap<>();

         for (RestJsonWrapperBean.FilterData filterData : filterDataList) {
            String key = filterData.getKey()
               + "_"
               + filterData.getMatchPattern()
               + (StringUtils.isEmpty(filterData.getGroup()) ? "" : ":" + filterData.getGroup());
            List<RestJsonWrapperBean.FilterData> list = filterMap.get(key);
            if (list == null) {
               list = new ArrayList<>();
               filterMap.put(key, list);
            }

            list.add(filterData);
         }

         if (!filterMap.isEmpty()) {
            List<FilterParamPattern> filterParamList = new ArrayList<>();
            filterMap.forEach((e, k) -> {
               Map<String, Object> paramMap = new IdentityHashMap<>();
               k.forEach(v -> paramMap.put(ObjectUtils.clone(v.getKey()), v.getValue()));
               filterParamList.add(new FilterParamPattern(k.get(0).getMatchPattern(), paramMap));
            });
            parameter.addFilterParamPatterns(filterParamList.toArray(new FilterParamPattern[0]));
         }
      }
   }

   private final <M extends MapperParameter> void extractMultiPropertyFilter(M parameter, String multiPropertyFilterJson) {
      if (!StringUtils.isBlank(multiPropertyFilterJson)) {
         JSONObject jsonObject = JSON.parseObject(multiPropertyFilterJson);
         MultiPropertyFilterPattern filterPattern = new MultiPropertyFilterPattern(
            StringUtils.split(jsonObject.getString("name"), ","), jsonObject.getString("value"), jsonObject.getBoolean("caseSensitive")
         );
         parameter.setMultiPropertyFilterPattern(filterPattern);
      }
   }

   private final <M extends MapperParameter> void extractAdvancedFilter(M parameter, String advancedFilterJson) {
      List<AdvancedFilterPattern> advancedFilterList = getAdvancedFilterList(advancedFilterJson);
      if (!advancedFilterList.isEmpty()) {
         parameter.setAdvancedFilterPatterns(advancedFilterList.toArray(new AdvancedFilterPattern[advancedFilterList.size()]));
      }
   }

   private static final List<AdvancedFilterPattern> getAdvancedFilterList(String advancedFilterJson) {
      if (StringUtils.isBlank(advancedFilterJson)) {
         return CollectionUtils.emptyList();
      } else {
         List<AdvancedFilterPattern> advancedFilterList = new ArrayList<>();
         JSONArray jsonArray = JSON.parseArray(advancedFilterJson);
         int i = 0;

         for (int j = jsonArray.size(); i < j; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONArray childrenArray = jsonObject.getJSONArray("children");
            int n = 0;

            for (int m = childrenArray.size(); n < m; n++) {
               JSONObject childObject = childrenArray.getJSONObject(n);
               String name = childObject.getString("name");
               String match = childObject.getString("match");
               Object value = MatchPattern.valueOf(match).parse(childObject.get("value"));
               AdvancedFilterPattern advancedFilter = new AdvancedFilterPattern(name, value, match);
               if (i > 0 && n == 0) {
                  advancedFilter.setGroupLink(Optional.ofNullable(jsonObject.getString("link")).orElse("AND").toUpperCase());
               }

               if (n > 0) {
                  advancedFilter.setIngroupLink(Optional.ofNullable(childObject.getString("link")).orElse("AND").toUpperCase());
               } else {
                  advancedFilter.setGroupStart(true);
               }

               if (n == m - 1) {
                  advancedFilter.setGroupEnd(true);
               }

               advancedFilterList.add(advancedFilter);
            }
         }

         return advancedFilterList;
      }
   }

   private static final List<RestJsonWrapperBean.FilterData> parseFilterData(String originalName, Object originalValue, MatchPattern matchPattern) {
      if ("VOID".equals(originalValue)) {
         originalValue = null;
      }

      List<RestJsonWrapperBean.FilterData> filterDataList = new ArrayList<>();
      String name = StringUtils.removeEndIgnoreCase(originalName.toUpperCase(), "_" + matchPattern.name());
      String filterSeparator = ApplicationContextHelper.getEnvironment().getProperty("sunway.search.multi-value-matching-seperator", ",");
      // External-only Spring configuration may omit the original application.yml default.
      // Empty separators otherwise make contains() true for every nonempty filter value.
      if (StringUtils.isBlank(filterSeparator)) {
         filterSeparator = ",";
      }
      if (MatchPattern.DEQ.equals(matchPattern)) {
         LocalDate date = matchPattern.parse(originalValue);
         if (date == null) {
            filterDataList.add(new RestJsonWrapperBean.FilterData(name, null, matchPattern.name()));
         } else {
            filterDataList.add(new RestJsonWrapperBean.FilterData(name, DateTimeUtils.formatLocalDate(date), matchPattern.name()));
         }
      } else if (MatchPattern.TEQ.equals(matchPattern)) {
         LocalDateTime date = matchPattern.parse(originalValue);
         if (date == null) {
            filterDataList.add(new RestJsonWrapperBean.FilterData(name, null, matchPattern.name()));
         } else {
            filterDataList.add(new RestJsonWrapperBean.FilterData(name, DateTimeUtils.formatLocalDateTime(date, "yyyy-MM-dd HH:mm"), matchPattern.name()));
         }
      } else if (MatchPattern.DR.equals(matchPattern)) {
         String resolvedValue = ((String)originalValue).substring(1, ((String)originalValue).length() - 1);
         String[] splitValues = StringUtils.split(resolvedValue, ",");
         if (splitValues.length != 2) {
            throw new OutOfRangeException("Range filter pattern must contains two values（can be empty）.");
         }

         for (int i = 0; i < 2; i++) {
            String value = splitValues[i];
            if (!StringUtils.isEmpty(value)) {
               LocalDate parsedValue = matchPattern.parse(value);
               if (i == 0) {
                  if (StringUtils.startsWith((String)originalValue, "(")) {
                     filterDataList.add(new RestJsonWrapperBean.FilterData(name, parsedValue, MatchPattern.DG.name()));
                  } else {
                     filterDataList.add(new RestJsonWrapperBean.FilterData(name, parsedValue, MatchPattern.DGOE.name()));
                  }
               } else if (StringUtils.endsWith((String)originalValue, ")")) {
                  filterDataList.add(new RestJsonWrapperBean.FilterData(name, parsedValue, MatchPattern.DL.name()));
               } else {
                  filterDataList.add(new RestJsonWrapperBean.FilterData(name, parsedValue.plusDays(1L), MatchPattern.DLOE.name()));
               }
            }
         }
      } else if (MatchPattern.TR.equals(matchPattern)) {
         String resolvedValue = ((String)originalValue).substring(1, ((String)originalValue).length() - 1);
         String[] splitValues = StringUtils.split(resolvedValue, ",");
         if (splitValues.length != 2) {
            throw new OutOfRangeException("Range filter pattern must contains two values（can be empty）.");
         }

         for (int ix = 0; ix < 2; ix++) {
            String value = splitValues[ix];
            if (!StringUtils.isEmpty(value)) {
               LocalDate parsedValue = matchPattern.parse(value);
               if (ix == 0) {
                  if (StringUtils.startsWith((String)originalValue, "(")) {
                     filterDataList.add(new RestJsonWrapperBean.FilterData(name, parsedValue, MatchPattern.TG.name()));
                  } else {
                     filterDataList.add(new RestJsonWrapperBean.FilterData(name, parsedValue, MatchPattern.TGOE.name()));
                  }
               } else if (StringUtils.endsWith((String)originalValue, ")")) {
                  filterDataList.add(new RestJsonWrapperBean.FilterData(name, parsedValue, MatchPattern.TL.name()));
               } else {
                  filterDataList.add(new RestJsonWrapperBean.FilterData(name, parsedValue.plusDays(1L), MatchPattern.TLOE.name()));
               }
            }
         }
      } else if (MatchPattern.NR.equals(matchPattern)) {
         String resolvedValue = ((String)originalValue).substring(1, ((String)originalValue).length() - 1);
         String[] splitValues = StringUtils.split(resolvedValue, ",");
         if (splitValues.length != 2) {
            throw new OutOfRangeException("Range filter pattern must contains two values（can be empty）.");
         }

         for (int ixx = 0; ixx < 2; ixx++) {
            String value = splitValues[ixx];
            if (!StringUtils.isEmpty(value)) {
               Object parsedValue = matchPattern.parse(value);
               if (ixx == 0) {
                  if (StringUtils.startsWith((String)originalValue, "(")) {
                     filterDataList.add(new RestJsonWrapperBean.FilterData(name, parsedValue, MatchPattern.NG.name()));
                  } else {
                     filterDataList.add(new RestJsonWrapperBean.FilterData(name, parsedValue, MatchPattern.NGOE.name()));
                  }
               } else if (StringUtils.endsWith((String)originalValue, ")")) {
                  filterDataList.add(new RestJsonWrapperBean.FilterData(name, parsedValue, MatchPattern.NL.name()));
               } else {
                  filterDataList.add(new RestJsonWrapperBean.FilterData(name, parsedValue, MatchPattern.NLOE.name()));
               }
            }
         }
      } else if (MatchPattern.MULTIPLE.equals(matchPattern)) {
         String group = System.nanoTime() + "";
         String[] keys = StringUtils.split(name, ",");

         for (String key : keys) {
            filterDataList.add(new RestJsonWrapperBean.FilterData(key, originalValue, MatchPattern.OR.name(), group));
         }
      } else if (!MatchPattern.NOR.equals(matchPattern)
         && !MatchPattern.DOR.equals(matchPattern)
         && !MatchPattern.TOR.equals(matchPattern)
         && !MatchPattern.OR.equals(matchPattern)) {
         if (MatchPattern.SB.equals(matchPattern)
            || MatchPattern.SC.equals(matchPattern)
            || MatchPattern.NSC.equals(matchPattern)
            || MatchPattern.SE.equals(matchPattern)
            || MatchPattern.SEQ.equals(matchPattern)
            || MatchPattern.CISB.equals(matchPattern)
            || MatchPattern.CISC.equals(matchPattern)
            || MatchPattern.CISE.equals(matchPattern)
            || MatchPattern.CISEQ.equals(matchPattern)) {
            String strValue = matchPattern.parse(originalValue);
            if (strValue != null && StringUtils.startsWith(matchPattern.name(), "CI")) {
               strValue = strValue.toUpperCase();
            }

            if (StringUtils.contains(strValue, filterSeparator)) {
               String group = StringUtils.randomUUID(16);
               String[] values = StringUtils.split(strValue, filterSeparator);

               for (String value : values) {
                  filterDataList.add(new RestJsonWrapperBean.FilterData(name, value, "O" + matchPattern.name(), group));
               }
            } else {
               filterDataList.add(new RestJsonWrapperBean.FilterData(name, strValue, matchPattern.name()));
            }
         } else if (MatchPattern.CBM.equals(matchPattern)) {
            String strValuex = matchPattern.parse(originalValue);
            if (!StringUtils.isBlank(strValuex)) {
               String group = System.nanoTime() + "";
               String[] values = StringUtils.split(strValuex, ",");

               for (String value : values) {
                  filterDataList.add(new RestJsonWrapperBean.FilterData(name, value, MatchPattern.CBM.name(), group));
               }
            }
         } else if (MatchPattern.COBM.equals(matchPattern)) {
            String strValuex = matchPattern.parse(originalValue);
            if (!StringUtils.isBlank(strValuex)) {
               String group = System.nanoTime() + "";
               String[] values = StringUtils.split(strValuex, ",");

               for (String value : values) {
                  filterDataList.add(new RestJsonWrapperBean.FilterData(name, value, MatchPattern.COBM.name(), group));
               }
            }
         } else if (MatchPattern.IN.equals(matchPattern)) {
            String group = StringUtils.randomUUID(16);
            String[] strArray = StringUtils.split(ConvertUtils.convert(originalValue, String.class), ",");

            for (String str : strArray) {
               filterDataList.add(new RestJsonWrapperBean.FilterData(name, matchPattern.parse(str), MatchPattern.OR.name(), group));
            }
         } else if (MatchPattern.ISNULL.equals(matchPattern)) {
            filterDataList.add(new RestJsonWrapperBean.FilterData(name, null, MatchPattern.ISNULL.name()));
         } else if (MatchPattern.ISNOTNULL.equals(matchPattern)) {
            filterDataList.add(new RestJsonWrapperBean.FilterData(name, null, MatchPattern.ISNOTNULL.name()));
         } else if (originalValue != null && String.class.equals(originalValue.getClass()) && StringUtils.contains((String)originalValue, filterSeparator)) {
            String group = StringUtils.randomUUID(16);
            String[] strArray = StringUtils.split((String)originalValue, filterSeparator);

            for (String str : strArray) {
               filterDataList.add(new RestJsonWrapperBean.FilterData(name, matchPattern.parse(str), matchPattern.name(), group));
            }
         } else {
            filterDataList.add(new RestJsonWrapperBean.FilterData(name, matchPattern.parse(originalValue), matchPattern.name()));
         }
      } else if (StringUtils.contains(name, "_")) {
         int index = name.lastIndexOf("_");
         filterDataList.add(
            new RestJsonWrapperBean.FilterData(name.substring(0, index), matchPattern.parse(originalValue), MatchPattern.OR.name(), name.substring(index + 1))
         );
      } else {
         filterDataList.add(new RestJsonWrapperBean.FilterData(name, matchPattern.parse(originalValue), MatchPattern.EQ.name()));
      }

      return filterDataList;
   }

   public final RestJsonWrapperBean clearParam() {
      if (!this.paramMap.isEmpty()) {
         this.paramMap.clear();
         this.paramMap.put("f", "{}");
      }

      return this;
   }

   static {
      parserConfig.putDeserializer(LocalDateTime.class, DefaultJsonDeserializer.INSTANCE);
      parserConfig.putDeserializer(LocalDate.class, DefaultJsonDeserializer.INSTANCE);
      parserConfig.putDeserializer(Time.class, DefaultJsonDeserializer.INSTANCE);
      parserConfig.putDeserializer(Date.class, DefaultJsonDeserializer.INSTANCE);
   }

   private static class FilterData {
      private String key;
      private Object value;
      private String matchPattern;
      private String group;

      public FilterData(String key, Object value, String matchPattern, String group) {
         this.key = key;
         this.value = value;
         this.matchPattern = matchPattern;
         this.group = group;
      }

      public FilterData(String key, Object value, String matchPattern) {
         this.key = key;
         this.value = value;
         this.matchPattern = matchPattern;
      }

      public String getKey() {
         return this.key;
      }

      public Object getValue() {
         return this.value;
      }

      public String getMatchPattern() {
         return this.matchPattern;
      }

      public String getGroup() {
         return this.group;
      }
   }

   private static class LcdpParameter {
      private String sql;
      private List<Order> orderList;
      private String treeNodeName;

      private LcdpParameter() {
      }

      public String getSql() {
         return this.sql;
      }

      public void setSql(String sql) {
         this.sql = sql;
      }

      public List<Order> getOrderList() {
         return this.orderList;
      }

      public void setOrderList(List<Order> orderList) {
         this.orderList = orderList;
      }

      public String getTreeNodeName() {
         return this.treeNodeName;
      }

      public void setTreeNodeName(String treeNodeName) {
         this.treeNodeName = treeNodeName;
      }
   }

   public static class TargetTableFilterData {
      private String type;
      private String targetTable;
      private String targetMatchColumn;
      private String thisMatchColumn;
      private Map<String, Object> targetFilter = new HashMap<>();

      public String getType() {
         return this.type;
      }

      public void setType(String type) {
         this.type = type;
      }

      public String getTargetTable() {
         return this.targetTable;
      }

      public void setTargetTable(String targetTable) {
         this.targetTable = targetTable;
      }

      public String getTargetMatchColumn() {
         return this.targetMatchColumn;
      }

      public void setTargetMatchColumn(String targetMatchColumn) {
         this.targetMatchColumn = targetMatchColumn;
      }

      public String getThisMatchColumn() {
         return this.thisMatchColumn;
      }

      public void setThisMatchColumn(String thisMatchColumn) {
         this.thisMatchColumn = thisMatchColumn;
      }

      public Map<String, Object> getTargetFilter() {
         return this.targetFilter;
      }

      public void addTargetFilter(String targetFilterColumn, String targetFilterValue) {
         this.targetFilter.put(targetFilterColumn, targetFilterValue);
      }
   }
}
