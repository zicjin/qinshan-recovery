package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.mybatis.mapper.AdvancedFilterPattern;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.MultiPropertyFilterPattern;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Owned HTTP query boundary for read-only mapper calls.
 *
 * This deliberately lives outside the supplier-compatible package. It parses
 * the observed wire envelope while keeping trusted identity, authorization and
 * SQL-scope values under the caller's control.
 */
public final class QueryRequest {
    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z][A-Za-z0-9_$.]*");
    private static final int MAX_PAGE_SIZE = 500;
    private final Map<String, Object> mapperParameters;
    private final Map<String, Object> scopeHints;
    private final List<FilterParamPattern> filterPatterns;
    private final List<AdvancedFilterPattern> advancedPatterns;
    private final MultiPropertyFilterPattern multiPropertyPattern;
    private final List<Pair<String, String>> orderParameters;
    private final int pageNumber;
    private final int pageSize;

    private QueryRequest(Map<String, Object> mapperParameters,
                         List<FilterParamPattern> filterPatterns,
                         List<AdvancedFilterPattern> advancedPatterns,
                         MultiPropertyFilterPattern multiPropertyPattern,
                         List<Pair<String, String>> orderParameters,
                         Map<String, Object> scopeHints,
                         int pageNumber, int pageSize) {
        this.mapperParameters = Collections.unmodifiableMap(new LinkedHashMap<String, Object>(mapperParameters));
        this.scopeHints = Collections.unmodifiableMap(new LinkedHashMap<String, Object>(scopeHints));
        this.filterPatterns = Collections.unmodifiableList(new ArrayList<FilterParamPattern>(filterPatterns));
        this.advancedPatterns = Collections.unmodifiableList(new ArrayList<AdvancedFilterPattern>(advancedPatterns));
        this.multiPropertyPattern = multiPropertyPattern;
        this.orderParameters = Collections.unmodifiableList(new ArrayList<Pair<String, String>>(orderParameters));
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public static QueryRequest parse(String body, Map<String, ?> queryParameters) {
        JSONObject root = object(body);
        JSONObject envelope = root;
        String nested = root.getString("requestJson");
        if (nested != null && !nested.trim().isEmpty()) envelope = object(nested);
        Map<String, Object> mapper = new LinkedHashMap<String, Object>();
        Map<String, Object> scopeHints = new LinkedHashMap<String, Object>();
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        copyParameters(queryParameters, params);
        Object p = envelope.get("p");
        if (p instanceof String && !((String) p).trim().isEmpty()) {
            JSONObject pObject = object((String) p);
            pObject.forEach((key, value) -> params.put(key, value));
        } else if (p instanceof JSONObject) {
            ((JSONObject) p).forEach((key, value) -> params.put(key, value));
        } else if (p instanceof Map<?, ?>) {
            ((Map<?, ?>) p).forEach((key, value) -> params.put(String.valueOf(key), value));
        }
        params.forEach(QueryRequest::validateParameterName);

        int pageNumber = positiveInt(params.get("n"), 1, "n");
        int pageSize = positiveInt(params.get("s"), 50, "s");
        if (pageSize > MAX_PAGE_SIZE) throw new IllegalArgumentException("page size exceeds " + MAX_PAGE_SIZE);
        List<FilterParamPattern> filters = new ArrayList<FilterParamPattern>();
        List<AdvancedFilterPattern> advanced = parseAdvanced(params.get("af"));
        MultiPropertyFilterPattern multi = parseMulti(params.get("mf"));
        parseFilterObject(params.get("f"), mapper, scopeHints, filters);
        parseFilterObject(params.get("qf"), mapper, scopeHints, filters);
        List<Pair<String, String>> order = parseOrder(params.get("o"));
        params.forEach((key, value) -> {
            if ("f".equals(key) || "qf".equals(key) || "af".equals(key) || "mf".equals(key) || "o".equals(key)
                    || "n".equals(key) || "s".equals(key)) return;
            mapper.put(key, value);
        });
        return new QueryRequest(mapper, filters, advanced, multi, order, scopeHints, pageNumber, pageSize);
    }

    public Map<String, Object> getMapperParameters() { return mapperParameters; }
    /** UI scope hints are observable input only; they never become trusted SQL context. */
    public Map<String, Object> getScopeHints() { return scopeHints; }
    /**
     * Copies parsed, caller-visible mapper values and then overlays trusted
     * server context. The caller owns identity and authorization values.
     */
    public Map<String, Object> toMapperParameters(Map<String, ?> trustedParameters) {
        Map<String, Object> result = new LinkedHashMap<String, Object>(mapperParameters);
        if (trustedParameters != null) trustedParameters.forEach(result::put);
        if (!filterPatterns.isEmpty()) result.put("filterParamPatterns", filterPatterns.toArray(new FilterParamPattern[filterPatterns.size()]));
        if (!advancedPatterns.isEmpty()) result.put("advancedFilterPatterns", advancedPatterns.toArray(new AdvancedFilterPattern[advancedPatterns.size()]));
        if (multiPropertyPattern != null) result.put("multiPropertyFilterPattern", multiPropertyPattern);
        if (!orderParameters.isEmpty()) result.put("orderParams", orderParameters);
        return result;
    }
    public List<FilterParamPattern> getFilterPatterns() { return filterPatterns; }
    public List<AdvancedFilterPattern> getAdvancedPatterns() { return advancedPatterns; }
    public MultiPropertyFilterPattern getMultiPropertyPattern() { return multiPropertyPattern; }
    public List<Pair<String, String>> getOrderParameters() { return orderParameters; }
    public int getPageNumber() { return pageNumber; }
    public int getPageSize() { return pageSize; }

    private static void copyParameters(Map<String, ?> input, Map<String, Object> output) {
        if (input == null) return;
        input.forEach((key, value) -> output.put(key, value));
    }

    private static JSONObject object(String json) {
        if (json == null || json.trim().isEmpty()) return new JSONObject();
        try { return JSON.parseObject(json); }
        catch (RuntimeException error) { throw new IllegalArgumentException("invalid query JSON", error); }
    }

    private static int positiveInt(Object value, int fallback, String name) {
        if (value == null || String.valueOf(value).trim().isEmpty()) return fallback;
        try {
            int parsed = Integer.parseInt(String.valueOf(value));
            if (parsed < 1) return fallback;
            return parsed;
        } catch (NumberFormatException error) { throw new IllegalArgumentException("invalid " + name, error); }
    }

    private static void validateParameterName(String key, Object ignored) {
        if (key == null || key.trim().isEmpty()) throw new IllegalArgumentException("empty query parameter");
        String lower = key.toLowerCase(Locale.ROOT);
        if (key.contains(";") || key.contains("--") || key.contains("/*") || key.contains("*/") || key.contains("'") || key.contains("\"") || key.contains("(") || key.contains(")")) {
            throw new IllegalArgumentException("unsafe query parameter: " + key);
        }
        if (lower.equals("loginuser") || lower.equals("loginuserid") || lower.equals("loginorgid") || lower.equals("loginroleid")
                || lower.equals("org_filter") || lower.equals("userorg_filter")
                || lower.equals("servicetable") || lower.startsWith("authority_") || lower.startsWith("queries_")
                || lower.startsWith("role_") || lower.startsWith("permissiontype") || lower.startsWith("user_assigned")
                || lower.startsWith("user_unassigned") || lower.equals("lcdpsql")
                || lower.equals("sql") || lower.equals("testids") || lower.equals("methodids")) {
            throw new IllegalArgumentException("server-owned query parameter: " + key);
        }
    }

    private static void parseFilterObject(Object source, Map<String, Object> mapper, Map<String, Object> scopeHints, List<FilterParamPattern> filters) {
        if (source == null || String.valueOf(source).trim().isEmpty()) return;
        JSONObject filter;
        if (source instanceof JSONObject) filter = (JSONObject) source;
        else filter = object(String.valueOf(source));
        Map<String, Map<String, Object>> grouped = new LinkedHashMap<String, Map<String, Object>>();
        filter.forEach((originalKey, rawValue) -> {
            String filterKey = originalKey.toLowerCase(Locale.ROOT);
            if (isScopeHint(filterKey)) {
                scopeHints.put(originalKey, rawValue);
                return;
            }
            validateParameterName(originalKey, rawValue);
            String upper = originalKey.toUpperCase(Locale.ROOT);
            MatchPattern pattern = null;
            for (MatchPattern candidate : MatchPattern.values()) {
                String suffix = "_" + candidate.name();
                if (upper.endsWith(suffix)) { pattern = candidate; break; }
            }
            if (pattern == null) {
                mapper.put(originalKey.replace("ext$.", ""), rawValue);
                return;
            }
            String field = upper.substring(0, upper.length() - pattern.name().length() - 1);
            requireIdentifier(field);
            Object value = normalize(pattern, rawValue);
            Map<String, Object> values = grouped.get(pattern.name());
            if (values == null) { values = new LinkedHashMap<String, Object>(); grouped.put(pattern.name(), values); }
            values.put(field, value);
        });
        grouped.forEach((pattern, values) -> filters.add(new FilterParamPattern(pattern, values)));
    }

    private static Object normalize(MatchPattern pattern, Object value) {
        if ("VOID".equals(value)) value = null;
        if (value == null) return null;
        try {
            if (pattern == MatchPattern.DG || pattern == MatchPattern.DEQ || pattern == MatchPattern.DR) return pattern.parse(value);
            if (pattern == MatchPattern.TG || pattern == MatchPattern.TEQ || pattern == MatchPattern.TR) return pattern.parse(value);
            return pattern.parse(value);
        } catch (RuntimeException error) { throw new IllegalArgumentException("invalid filter value for " + pattern.name(), error); }
    }

    private static List<AdvancedFilterPattern> parseAdvanced(Object source) {
        if (source == null || String.valueOf(source).trim().isEmpty()) return Collections.emptyList();
        JSONArray groups = source instanceof JSONArray ? (JSONArray) source : JSON.parseArray(String.valueOf(source));
        List<AdvancedFilterPattern> result = new ArrayList<AdvancedFilterPattern>();
        for (int i = 0; i < groups.size(); i++) {
            JSONObject group = groups.getJSONObject(i);
            JSONArray children = group.getJSONArray("children");
            if (children == null || children.isEmpty()) throw new IllegalArgumentException("advanced filter group is empty");
            for (int n = 0; n < children.size(); n++) {
                JSONObject child = children.getJSONObject(n);
                String name = child.getString("name");
                requireIdentifier(name);
                String match = child.getString("match");
                MatchPattern pattern = MatchPattern.getPatternIfPossible(match);
                if (pattern == null) throw new IllegalArgumentException("unknown filter pattern: " + match);
                AdvancedFilterPattern item = new AdvancedFilterPattern(name, normalize(pattern, child.get("value")), match);
                if (i > 0 && n == 0) item.setGroupLink(link(group.getString("link")));
                if (n > 0) item.setIngroupLink(link(child.getString("link")));
                if (n == 0) item.setGroupStart(true);
                if (n == children.size() - 1) item.setGroupEnd(true);
                result.add(item);
            }
        }
        return result;
    }

    private static MultiPropertyFilterPattern parseMulti(Object source) {
        if (source == null || String.valueOf(source).trim().isEmpty()) return null;
        JSONObject value = source instanceof JSONObject ? (JSONObject) source : object(String.valueOf(source));
        String[] names = value.getString("name") == null ? new String[0] : value.getString("name").split(",");
        if (names.length == 0) throw new IllegalArgumentException("multi-property filter has no columns");
        for (String name : names) requireIdentifier(name.trim());
        return new MultiPropertyFilterPattern(names, value.getString("value"), value.getBoolean("caseSensitive"));
    }

    private static List<Pair<String, String>> parseOrder(Object source) {
        if (source == null || String.valueOf(source).trim().isEmpty()) return Collections.emptyList();
        JSONArray values = source instanceof JSONArray ? (JSONArray) source : JSON.parseArray(String.valueOf(source));
        List<Pair<String, String>> result = new ArrayList<Pair<String, String>>();
        for (Object value : values) {
            if (!(value instanceof Map<?, ?>)) throw new IllegalArgumentException("invalid order entry");
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                requireIdentifier(String.valueOf(entry.getKey()));
                String direction = String.valueOf(entry.getValue()).toLowerCase(Locale.ROOT);
                if (!"asc".equals(direction) && !"desc".equals(direction)) throw new IllegalArgumentException("invalid order direction");
                result.add(Pair.of(String.valueOf(entry.getKey()), direction));
            }
        }
        return result;
    }

    private static String link(String value) {
        if (value == null || value.trim().isEmpty()) return "AND";
        String upper = value.toUpperCase(Locale.ROOT);
        if (!"AND".equals(upper) && !"OR".equals(upper)) throw new IllegalArgumentException("invalid filter link");
        return upper;
    }

    private static void requireIdentifier(String value) {
        if (value == null || !IDENTIFIER.matcher(value).matches()) throw new IllegalArgumentException("unsafe identifier: " + value);
    }

    private static boolean isScopeHint(String key) {
        String lower = key.toLowerCase(Locale.ROOT);
        return "org_filter".equals(lower) || "userorg_filter".equals(lower);
    }
}
