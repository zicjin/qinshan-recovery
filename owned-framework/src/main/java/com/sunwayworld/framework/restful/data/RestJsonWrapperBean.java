package com.sunwayworld.framework.restful.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.OutOfRangeException;
import com.sunwayworld.framework.mybatis.mapper.AdvancedFilterPattern;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.MultiPropertyFilterPattern;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.JsonUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Project-owned request wrapper preserving the supplier's body envelope contract. */
public class RestJsonWrapperBean {
    private final String requestJson;
    private final Map<String, String> paramMap;
    private final List<List<String>> bodyList;
    private final List<GenericService<?, ?>> bodyServiceList;
    private final List<String> authorityList = new ArrayList<String>();
    private final Map<String, Object> extFilter = new HashMap<String, Object>();
    private final List<FilterParamPattern> extStdFilter = new ArrayList<FilterParamPattern>();
    private Class<? extends com.sunwayworld.framework.mybatis.mapper.MapperParameter> mapperParameterClass;
    private String lcdpSql;
    private List<Order> lcdpOrderList;
    private String lcdpTreeNodeName;

    public RestJsonWrapperBean() { this(null, null, null, null); }

    /** Supplier-compatible form: the string is the legacy filter parameter, not a JSON body. */
    public RestJsonWrapperBean(String p0) {
        this.requestJson = null;
        this.paramMap = new LinkedHashMap<String, String>();
        if (p0 != null) this.paramMap.put("f", p0);
        this.bodyList = Collections.emptyList();
        this.bodyServiceList = Collections.emptyList();
    }

    public RestJsonWrapperBean(Map<String, String> p0, List<List<String>> p1,
            List<GenericService<?, ?>> p2) {
        this(null, p0, p1, p2);
    }

    public RestJsonWrapperBean(String p0, Map<String, String> p1, List<List<String>> p2,
            List<GenericService<?, ?>> p3) {
        this.requestJson = p0;
        this.paramMap = p1 == null ? new LinkedHashMap<String, String>()
                : new LinkedHashMap<String, String>(p1);
        if (p2 == null && p3 == null && !isBlank(p0)) {
            JSONObject envelope = parseEnvelope(p0);
            this.bodyList = parseBodyList(envelope);
            this.bodyServiceList = nullServices(this.bodyList.size());
        } else {
            this.bodyList = copyBody(p2);
            this.bodyServiceList = copyServices(this.bodyList.size(), p3);
        }
    }

    public static RestJsonWrapperBean newEmpty() {
        return new RestJsonWrapperBean("{}", null, null, null);
    }

    public Map<String, String> getParamMap() { return Collections.unmodifiableMap(paramMap); }
    public String getParamValue(String p0) { return paramMap.get(p0); }
    public void removeParam(String p0) { paramMap.remove(p0); }
    public void setParamValue(String p0, String p1) {
        if (p0 == null) throw new IllegalArgumentException("parameter name is required");
        paramMap.put(p0, p1);
    }
    public List<List<String>> getBodyList() { return bodyList; }
    public List<GenericService<?, ?>> getBodyServiceList() { return bodyServiceList; }
    public String getRequestJson() { return requestJson; }

    public String getFilterValue(String p0) {
        String filterJson = getParamValue("f");
        if (com.sunwayworld.framework.utils.StringUtils.isEmpty(filterJson)) return null;
        try {
            Map<?, ?> filter = JSON.parseObject(filterJson, Map.class);
            Object value = filter == null ? null : filter.get(p0);
            return value == null ? null : String.valueOf(value);
        } catch (RuntimeException failure) {
            throw new IllegalArgumentException("invalid filter JSON", failure);
        }
    }

    public <V> void setFilterValue(String p0, V p1) {
        String filterJson = getParamValue("f");
        Map<String, Object> filter = com.sunwayworld.framework.utils.StringUtils.isEmpty(filterJson)
                ? new LinkedHashMap<String, Object>() : new LinkedHashMap<String, Object>(parseFilterMap(filterJson));
        filter.put(p0, p1);
        paramMap.put("f", JSON.toJSONString(filter));
    }

    public void setAuthority(String p0) { setFilterValue("authority_" + p0, "1"); }
    public void setQueries(String p0) { setFilterValue("queries_" + p0, "1"); }
    public com.sunwayworld.framework.mybatis.mapper.MapperParameter extractMapFilter() {
        if (mapperParameterClass == null) return extractMapFilter(com.sunwayworld.framework.mybatis.mapper.MapperParameter.class);
        return extractMapFilter(mapperParameterClass);
    }

    public <M extends com.sunwayworld.framework.mybatis.mapper.MapperParameter> M extractMapFilter(Class<M> p0) {
        M parameter = ClassUtils.newInstance(p0);
        extractFilter(parameter, getParamValue("f"));
        extractFilter(parameter, getParamValue("qf"));
        extractAdvancedFilter(parameter, getParamValue("af"));
        extractMultiPropertyFilter(parameter, getParamValue("mf"));
        if (!extStdFilter.isEmpty()) {
            parameter.addFilterParamPatterns(extStdFilter.toArray(new FilterParamPattern[0]));
        }
        extractOrder(parameter, getParamValue("o"));
        extractOptions(parameter, getParamValue("r"));
        parameter.putAll(extFilter);
        return parameter;
    }
    public void setTotalFieldNames(String... p0) { paramMap.put("tf", String.join(",", Arrays.asList(p0))); }
    public String[] getSumProperties() { return splitPageProperties(paramMap.get("tf")); }
    public String[] getCountProperties() { return splitPageProperties(paramMap.get("cp")); }
    public String getOptionsProperty() { return paramMap.get("op"); }
    private static String[] splitPageProperties(String value) {
        return com.sunwayworld.framework.utils.StringUtils.isBlank(value) ? new String[0]
                : com.sunwayworld.framework.utils.StringUtils.split(value, ",");
    }
    public void removePageParams() { paramMap.put("n", "-1"); paramMap.put("s", "-1"); }
    public com.sunwayworld.framework.data.page.PageRequest extractPageRequest() {
        return com.sunwayworld.framework.data.page.PageRequest.of(this);
    }
    public com.sunwayworld.framework.mybatis.page.PageRowBounds extractPageRowBounds() {
        return new com.sunwayworld.framework.mybatis.page.PageRowBounds(extractPageRequest());
    }
    public void setMapperParameterClass(Class<? extends com.sunwayworld.framework.mybatis.mapper.MapperParameter> p0) { mapperParameterClass = p0; }

    public <T> T parseUnique(Class<T> p0) {
        if (p0 == null) throw new IllegalArgumentException("type is required");
        List<T> values = new ArrayList<T>();
        boolean hasNullService = false;
        for (int i = 0; i < bodyList.size(); i++) {
            GenericService<?, ?> service = bodyServiceList.get(i);
            if (service == null) hasNullService = true;
            else if (serviceTypeMatches(service, p0)) addUniqueBody(values, bodyList.get(i), p0);
        }
        if (values.isEmpty() && hasNullService) {
            for (int i = 0; i < bodyList.size(); i++) {
                if (bodyServiceList.get(i) == null) addUniqueBody(values, bodyList.get(i), p0);
            }
        }
        if (values.size() != 1) throw new OutOfRangeException("core.rest.json.unable-parse-unique-bean");
        return values.get(0);
    }

    public <T> List<T> parse(Class<T> p0) {
        if (p0 == null) throw new IllegalArgumentException("type is required");
        List<T> values = new ArrayList<T>();
        boolean hasNullService = false;
        for (int i = 0; i < bodyList.size(); i++) {
            GenericService<?, ?> service = bodyServiceList.get(i);
            if (service == null) hasNullService = true;
            else if (serviceTypeMatches(service, p0)) addBody(values, bodyList.get(i), p0);
        }
        if (values.isEmpty() && hasNullService) {
            for (int i = 0; i < bodyList.size(); i++) {
                if (bodyServiceList.get(i) == null) addBody(values, bodyList.get(i), p0);
            }
        }
        return Collections.unmodifiableList(values);
    }

    public <T> List<T> parse(Class<T> p0, int p1) {
        if (p0 == null) throw new IllegalArgumentException("type is required");
        List<T> values = new ArrayList<T>();
        addBody(values, bodyList.get(p1), p0);
        return Collections.unmodifiableList(values);
    }

    public <ID> List<ID> parseId(Class<ID> p0) {
        if (p0 == null) throw new IllegalArgumentException("id type is required");
        if (bodyList.size() > 1) throw new OutOfRangeException("core.rest.json.unable-parse-id");
        if (bodyList.isEmpty()) return Collections.emptyList();
        List<ID> values = new ArrayList<ID>();
        for (String value : bodyList.get(0)) {
            Map<?, ?> object = parseMap(value);
            values.add(ConvertUtils.convert(object.get("id"), p0));
        }
        List<ID> distinct = new ArrayList<ID>();
        for (ID value : values) if (!distinct.contains(value)) distinct.add(value);
        return Collections.unmodifiableList(distinct);
    }

    private static <T> void addBody(List<T> target, List<String> source, Class<T> type) {
        for (String value : source) target.add(parseObject(value, type));
    }

    private static <T> void addUniqueBody(List<T> target, List<String> source, Class<T> type) {
        if (source.size() > 1) throw new OutOfRangeException("core.rest.json.unable-parse-unique-bean");
        addBody(target, source, type);
    }

    private static boolean serviceTypeMatches(GenericService<?, ?> service, Class<?> type) {
        try { return type.equals(service.getDao().getType()); }
        catch (RuntimeException ignored) { return false; }
    }

    private static <T> T parseObject(String value, Class<T> type) {
        try { return JsonUtils.parse(value, type); }
        catch (RuntimeException failure) { throw new IllegalArgumentException("invalid wrapper record", failure); }
    }

    private static Map<?, ?> parseMap(String value) {
        try {
            Map<?, ?> map = JSON.parseObject(value, Map.class);
            if (map == null) throw new IllegalArgumentException("record must be a JSON object");
            return map;
        } catch (RuntimeException failure) { throw new IllegalArgumentException("invalid wrapper record", failure); }
    }

    public java.util.List<java.lang.String> getAuthorityList() { return authorityList; }
    public void addAuthority(java.lang.String p0) { authorityList.add("authority_" + p0); }
    public <V> void addExtFilter(java.lang.String p0, V p1) { extFilter.put(p0, p1); }
    public java.util.Map<java.lang.String, java.lang.Object> getExtFilter() { return Collections.unmodifiableMap(extFilter); }
    public void addExtStdFilter(java.lang.String p0, java.lang.Object p1, MatchPattern p2) {
        if (p1 == null) {
            extStdFilter.add(new FilterParamPattern(p2.name(), p0, null));
        } else if (p1 instanceof List<?>) {
            IdentityHashMap<String, Object> values = new IdentityHashMap<String, Object>();
            for (Object value : (List<?>) p1) values.put(ObjectUtils.clone(p0), value);
            extStdFilter.add(new FilterParamPattern(p2.name(), values));
        } else if (p1.getClass().isArray()) {
            IdentityHashMap<String, Object> values = new IdentityHashMap<String, Object>();
            for (Object value : (Object[]) p1) values.put(ObjectUtils.clone(p0), value);
            extStdFilter.add(new FilterParamPattern(p2.name(), values));
        } else {
            extStdFilter.add(new FilterParamPattern(p2.name(), p0, p1));
        }
    }
    public java.lang.String getLcdpSql() { return lcdpSql; }
    public void setLcdpSql(java.lang.String p0) { lcdpSql = p0; }
    public java.util.List<Order> getLcdpOrderList() { return lcdpOrderList; }
    public void setLcdpOrderList(java.util.List<Order> p0) { lcdpOrderList = p0; }
    public java.lang.String getLcdpTreeNodeName() { return lcdpTreeNodeName; }
    public void setLcdpTreeNodeName(java.lang.String p0) { lcdpTreeNodeName = p0; }
    public final RestJsonWrapperBean clearParam() {
        if (!paramMap.isEmpty()) {
            paramMap.clear();
            paramMap.put("f", "{}");
        }
        return this;
    }

    private static Map<String, Object> parseFilterMap(String filterJson) {
        try {
            Map<?, ?> parsed = JSON.parseObject(filterJson, Map.class);
            if (parsed == null) throw new IllegalArgumentException("filter JSON object is required");
            Map<String, Object> result = new LinkedHashMap<String, Object>();
            for (Map.Entry<?, ?> entry : parsed.entrySet()) {
                result.put(String.valueOf(entry.getKey()), entry.getValue());
            }
            return result;
        } catch (RuntimeException failure) {
            throw new IllegalArgumentException("invalid filter JSON", failure);
        }
    }

    private void extractFilter(com.sunwayworld.framework.mybatis.mapper.MapperParameter parameter, String filterJson) {
        if (com.sunwayworld.framework.utils.StringUtils.isEmpty(filterJson)) return;
        JSONObject filter = JSON.parseObject(filterJson);
        if (filter == null) return;
        Map<String, List<Object[]>> grouped = new LinkedHashMap<String, List<Object[]>>();
        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            String key = com.sunwayworld.framework.utils.StringUtils.replace(entry.getKey(), "ext$.", "");
            if (isRiskySqlSegment(key)) throw new IllegalArgumentException("unsafe filter key: " + key);
            if ("target_filter".equals(key)) {
                parameter.setTargetFilter(parseTargetFilter(entry.getValue()));
                continue;
            }
            MatchPattern pattern = findMatchPattern(key);
            if (pattern == null) {
                parameter.put(key, entry.getValue());
                continue;
            }
            for (Object[] value : parseFilterValues(key, entry.getValue(), pattern)) {
                String groupKey = value[2] + "\u0000" + (value[3] == null ? "" : value[3]);
                List<Object[]> values = grouped.get(groupKey);
                if (values == null) { values = new ArrayList<Object[]>(); grouped.put(groupKey, values); }
                values.add(value);
            }
        }
        for (List<Object[]> values : grouped.values()) {
            if (values.isEmpty()) continue;
            IdentityHashMap<String, Object> map = new IdentityHashMap<String, Object>();
            for (Object[] value : values) map.put(new String((String) value[0]), value[1]);
            parameter.addFilterParamPatterns(new FilterParamPattern((String) values.get(0)[2], map));
        }
        parameter.putAll(extFilter);
    }

    private static MatchPattern findMatchPattern(String key) {
        String upper = key.toUpperCase(Locale.ROOT);
        for (MatchPattern pattern : MatchPattern.values()) {
            if (upper.endsWith("_" + pattern.name())) return pattern;
        }
        return null;
    }

    private static List<Object[]> parseFilterValues(String originalName, Object originalValue, MatchPattern pattern) {
        List<Object[]> result = new ArrayList<Object[]>();
        if ("VOID".equals(originalValue)) originalValue = null;
        String name = originalName.substring(0, originalName.length() - pattern.name().length() - 1).toUpperCase(Locale.ROOT);
        if (pattern == MatchPattern.MULTIPLE) {
            String group = com.sunwayworld.framework.utils.StringUtils.randomUUID(16);
            for (String key : com.sunwayworld.framework.utils.StringUtils.split(name, ",")) {
                result.add(filterValue(key, originalValue, MatchPattern.OR.name(), group));
            }
            return result;
        }
        if (pattern == MatchPattern.IN || pattern == MatchPattern.OIN) {
            String group = com.sunwayworld.framework.utils.StringUtils.randomUUID(16);
            for (String value : com.sunwayworld.framework.utils.StringUtils.split(String.valueOf(originalValue), ",")) {
                result.add(filterValue(name, pattern.parse(value), MatchPattern.OR.name(), group));
            }
            return result;
        }
        if (pattern == MatchPattern.DR || pattern == MatchPattern.TR || pattern == MatchPattern.NR) {
            String raw = String.valueOf(originalValue);
            if (raw.length() < 2) throw new IllegalArgumentException("range filter must be enclosed");
            String[] values = com.sunwayworld.framework.utils.StringUtils.split(raw.substring(1, raw.length() - 1), ",");
            if (values.length != 2) throw new OutOfRangeException("Range filter pattern must contains two values（can be empty）.");
            for (int index = 0; index < values.length; index++) {
                if (com.sunwayworld.framework.utils.StringUtils.isEmpty(values[index])) continue;
                if (pattern == MatchPattern.DR) {
                    LocalDate date = MatchPattern.DG.parse(values[index]);
                    String op = index == 0 ? (raw.startsWith("(") ? MatchPattern.DG.name() : MatchPattern.DGOE.name())
                            : (raw.endsWith(")") ? MatchPattern.DL.name() : MatchPattern.DLOE.name());
                    Object value = index == 1 && !raw.endsWith(")") ? date.plusDays(1L) : date;
                    result.add(filterValue(name, value, op, null));
                } else if (pattern == MatchPattern.TR) {
                    LocalDateTime date = MatchPattern.TG.parse(values[index]);
                    String op = index == 0 ? (raw.startsWith("(") ? MatchPattern.TG.name() : MatchPattern.TGOE.name())
                            : (raw.endsWith(")") ? MatchPattern.TL.name() : MatchPattern.TLOE.name());
                    Object value = index == 1 && !raw.endsWith(")") ? date.plusDays(1L) : date;
                    result.add(filterValue(name, value, op, null));
                } else {
                    Object value = pattern.parse(values[index]);
                    String op = index == 0 ? (raw.startsWith("(") ? MatchPattern.NG.name() : MatchPattern.NGOE.name())
                            : (raw.endsWith(")") ? MatchPattern.NL.name() : MatchPattern.NLOE.name());
                    result.add(filterValue(name, value, op, null));
                }
            }
            return result;
        }
        Object value = pattern.parse(originalValue);
        if (value instanceof String && pattern.name().startsWith("CI")) value = ((String) value).toUpperCase(Locale.ROOT);
        if (value instanceof String && ((String) value).contains(",")
                && (pattern.name().startsWith("S") || pattern.name().startsWith("CI") || pattern.name().startsWith("O"))) {
            String group = com.sunwayworld.framework.utils.StringUtils.randomUUID(16);
            for (String item : com.sunwayworld.framework.utils.StringUtils.split((String) value, ",")) {
                result.add(filterValue(name, item, "O" + pattern.name(), group));
            }
            return result;
        }
        result.add(filterValue(name, value, pattern.name(), null));
        return result;
    }

    private static Object[] filterValue(String key, Object value, String matchPattern, String group) {
        return new Object[]{key, value, matchPattern, group};
    }

    private static RestJsonWrapperBean.TargetTableFilterData parseTargetFilter(Object raw) {
        JSONObject source = raw instanceof JSONObject ? (JSONObject) raw : JSON.parseObject(String.valueOf(raw));
        if (source == null) throw new IllegalArgumentException("target filter must be an object");
        RestJsonWrapperBean.TargetTableFilterData result = new RestJsonWrapperBean.TargetTableFilterData();
        result.setType(source.getString("type"));
        result.setTargetTable(source.getString("targetTable"));
        result.setTargetMatchColumn(source.getString("targetMatchColumn") == null ? "ID" : source.getString("targetMatchColumn"));
        result.setThisMatchColumn(source.getString("thisMatchColumn") == null ? "ID" : source.getString("thisMatchColumn"));
        Object filters = source.get("filter");
        if (filters != null) {
            JSONArray values = filters instanceof JSONArray ? (JSONArray) filters : JSON.parseArray(String.valueOf(filters));
            for (Object value : values) {
                JSONObject item = (JSONObject) value;
                result.addTargetFilter(item.getString("targetFilterColumn"), item.getString("targetFilterValue"));
            }
        }
        validateIdentifier(result.getTargetTable(), "targetTable");
        validateIdentifier(result.getTargetMatchColumn(), "targetMatchColumn");
        validateIdentifier(result.getThisMatchColumn(), "thisMatchColumn");
        for (String key : result.getTargetFilter().keySet()) validateIdentifier(key, "targetFilterColumn");
        return result;
    }

    private static void extractAdvancedFilter(com.sunwayworld.framework.mybatis.mapper.MapperParameter parameter, String json) {
        if (com.sunwayworld.framework.utils.StringUtils.isBlank(json)) return;
        JSONArray groups = JSON.parseArray(json);
        List<AdvancedFilterPattern> values = new ArrayList<AdvancedFilterPattern>();
        for (int groupIndex = 0; groupIndex < groups.size(); groupIndex++) {
            JSONObject group = groups.getJSONObject(groupIndex);
            JSONArray children = group.getJSONArray("children");
            if (children == null || children.isEmpty()) throw new IllegalArgumentException("advanced filter group is empty");
            for (int childIndex = 0; childIndex < children.size(); childIndex++) {
                JSONObject child = children.getJSONObject(childIndex);
                String match = child.getString("match");
                MatchPattern pattern = MatchPattern.getPatternIfPossible(match);
                if (pattern == null) throw new IllegalArgumentException("unknown filter pattern: " + match);
                AdvancedFilterPattern value = new AdvancedFilterPattern(child.getString("name"), pattern.parse(child.get("value")), match);
                if (groupIndex > 0 && childIndex == 0) value.setGroupLink(defaultLink(group.getString("link")));
                if (childIndex > 0) value.setIngroupLink(defaultLink(child.getString("link")));
                if (childIndex == 0) value.setGroupStart(true);
                if (childIndex == children.size() - 1) value.setGroupEnd(true);
                values.add(value);
            }
        }
        parameter.setAdvancedFilterPatterns(values.toArray(new AdvancedFilterPattern[values.size()]));
    }

    private static void extractMultiPropertyFilter(com.sunwayworld.framework.mybatis.mapper.MapperParameter parameter, String json) {
        if (com.sunwayworld.framework.utils.StringUtils.isBlank(json)) return;
        JSONObject source = JSON.parseObject(json);
        parameter.setMultiPropertyFilterPattern(new MultiPropertyFilterPattern(
                com.sunwayworld.framework.utils.StringUtils.split(source.getString("name"), ","),
                source.getString("value"), source.getBoolean("caseSensitive")));
    }

    private static void extractOrder(com.sunwayworld.framework.mybatis.mapper.MapperParameter parameter, String json) {
        if (com.sunwayworld.framework.utils.StringUtils.isEmpty(json)) return;
        JSONArray values = JSON.parseArray(json);
        for (Object raw : values) {
            JSONObject item = (JSONObject) raw;
            for (Map.Entry<String, Object> entry : item.entrySet()) {
                validateIdentifier(entry.getKey(), "order");
                String direction = String.valueOf(entry.getValue());
                if ("asc".equals(direction) || "desc".equals(direction)) {
                    parameter.setOrderParam(com.sunwayworld.framework.utils.StringUtils.removeStartIgnoreCase(entry.getKey(), "ext$."), direction);
                }
            }
        }
    }

    private static void extractOptions(com.sunwayworld.framework.mybatis.mapper.MapperParameter parameter, String json) {
        if (com.sunwayworld.framework.utils.StringUtils.isEmpty(json)) return;
        JSONObject source = JSON.parseObject(json);
        Map<String, Object> options = new LinkedHashMap<String, Object>();
        for (Map.Entry<String, Object> entry : source.entrySet()) options.put(entry.getKey(), JSON.parseObject(String.valueOf(entry.getValue())));
        parameter.put("optionsMap", options);
    }

    private static String defaultLink(String value) {
        if (com.sunwayworld.framework.utils.StringUtils.isBlank(value)) return "AND";
        String result = value.toUpperCase(Locale.ROOT);
        if (!"AND".equals(result) && !"OR".equals(result)) throw new IllegalArgumentException("invalid filter link");
        return result;
    }

    private static void validateIdentifier(String value, String field) {
        if (value == null || !value.matches("[A-Za-z][A-Za-z0-9_$.]*") || isRiskySqlSegment(value)) {
            throw new IllegalArgumentException("unsafe " + field + ": " + value);
        }
    }

    private static boolean isRiskySqlSegment(String value) {
        if (value == null) return false;
        String lower = value.toLowerCase(Locale.ROOT);
        return value.indexOf(';') >= 0 || lower.contains("--") || lower.contains("/*") || lower.contains("*/")
                || value.indexOf('\'') >= 0 || value.indexOf('"') >= 0 || value.indexOf('(') >= 0 || value.indexOf(')') >= 0;
    }


    private static JSONObject parseEnvelope(String requestJson) {
        JSONObject root = parseObject(requestJson);
        String nested = root.getString("requestJson");
        if (!isBlank(nested)) root = parseObject(nested);
        return root;
    }

    private static List<List<String>> parseBodyList(JSONObject root) {
        List<List<String>> bodies = new ArrayList<List<String>>();
        for (int index = 0; ; index++) {
            String key = index == 0 ? "b" : "b" + index;
            if (!root.containsKey(key)) break;
            Object raw = root.get(key);
            if (!(raw instanceof JSONArray)) throw new IllegalArgumentException("invalid wrapper body group: " + key);
            JSONArray group = (JSONArray) raw;
            if (group.size() != 2 || !(group.get(1) instanceof JSONArray)) {
                throw new IllegalArgumentException("invalid wrapper body group: " + key);
            }
            JSONArray records = group.getJSONArray(1);
            List<String> body = new ArrayList<String>();
            for (Object record : records) {
                if (!(record instanceof JSONObject)) throw new IllegalArgumentException("wrapper record must be a JSON object");
                body.add(((JSONObject) record).toJSONString());
            }
            bodies.add(body);
        }
        return copyBody(bodies);
    }

    private static List<List<String>> copyBody(List<List<String>> body) {
        if (body == null) return Collections.emptyList();
        List<List<String>> bodyCopy = new ArrayList<List<String>>();
        for (List<String> group : body) {
            if (group == null) throw new IllegalArgumentException("body group must not be null");
            List<String> records = new ArrayList<String>();
            for (String record : group) {
                if (record == null) throw new IllegalArgumentException("wrapper record must not be null");
                parseMap(record);
                records.add(record);
            }
            bodyCopy.add(Collections.unmodifiableList(records));
        }
        return Collections.unmodifiableList(bodyCopy);
    }

    private static List<GenericService<?, ?>> copyServices(int bodySize, List<GenericService<?, ?>> services) {
        if (services != null && services.size() != bodySize) {
            throw new IllegalArgumentException("body and service groups must have equal length");
        }
        List<GenericService<?, ?>> serviceCopy = new ArrayList<GenericService<?, ?>>();
        if (services == null) {
            for (int i = 0; i < bodySize; i++) serviceCopy.add(null);
        } else {
            serviceCopy.addAll(services);
        }
        return Collections.unmodifiableList(serviceCopy);
    }

    private static List<GenericService<?, ?>> nullServices(int count) {
        List<GenericService<?, ?>> result = new ArrayList<GenericService<?, ?>>();
        for (int i = 0; i < count; i++) result.add(null);
        return Collections.unmodifiableList(result);
    }

    private static JSONObject parseObject(String value) {
        if (isBlank(value)) return new JSONObject();
        try {
            JSONObject result = JSON.parseObject(value);
            if (result == null) throw new IllegalArgumentException("wrapper JSON object is required");
            return result;
        } catch (RuntimeException failure) {
            throw new IllegalArgumentException("invalid wrapper JSON", failure);
        }
    }

    private static boolean isBlank(String value) { return value == null || value.trim().isEmpty(); }

    public static class TargetTableFilterData {
        private String type;
        private String targetTable;
        private String targetMatchColumn;
        private String thisMatchColumn;
        private final Map<String, Object> targetFilter = new HashMap<String, Object>();
        public TargetTableFilterData() {  }
        public java.lang.String getType() { return type; }
        public void setType(java.lang.String p0) { type = p0; }
        public java.lang.String getTargetTable() { return targetTable; }
        public void setTargetTable(java.lang.String p0) { targetTable = p0; }
        public java.lang.String getTargetMatchColumn() { return targetMatchColumn; }
        public void setTargetMatchColumn(java.lang.String p0) { targetMatchColumn = p0; }
        public java.lang.String getThisMatchColumn() { return thisMatchColumn; }
        public void setThisMatchColumn(java.lang.String p0) { thisMatchColumn = p0; }
        public java.util.Map<java.lang.String, java.lang.Object> getTargetFilter() { return targetFilter; }
        public void addTargetFilter(java.lang.String p0, java.lang.String p1) { targetFilter.put(p0, p1); }
    }
}
