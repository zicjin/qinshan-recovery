package com.sunwayworld.owned.internal.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.mybatis.mapper.AdvancedFilterPattern;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.MultiPropertyFilterPattern;
import com.sunwayworld.owned.internal.web.QueryRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;

/** Parameterized Oracle reads derived from the five original selectByCondition mappers. */
public final class AdmQuotationQueryDao {
    private final DataSource dataSource;

    public AdmQuotationQueryDao(DataSource dataSource) {
        if (dataSource == null) throw new IllegalArgumentException("quotation data source required");
        this.dataSource = dataSource;
    }

    public enum Resource {
        AREA("adm-areas", "T_EMC_ADM_AREA", "admAreaName parentId orderNo"),
        ADD_PRICE("quotation/addprices", "T_EMC_QUOTATION_ADDPRICE", "priceName price remark"),
        DETECTION_FACTOR("quotation/detectionfactors", "T_EMC_QUOTATION_DETECTIONFACTOR",
                "analysisPrice samplePrice itemGroupId itemGroupName methodId methodName orgId testCategoryId testId testName "
                + "monitorTmplGroupId monitorTmplGroupName monitorTmplId trialScope tmplGroupDesc remark "
                + "methodNamee methodNo testMethodBasis parentOrgName testMethodType"),
        DISCOUNT("quotation/discounts", "T_EMC_QUOTATION_DISCOUNT", "discount remark"),
        INFORMATION("quotation/quotationinformations", "T_EMC_QUOTATION_INFORMATION",
                "quotationRegisterId type monitorPointName testName testMethodName testId methodId monitorTmplName "
                + "testSamplePrice testAnalysisPrice testSamplePriceAfterCount testSampleDiscount testAnalysisPriceAfterCount "
                + "testAnalysisDiscount testDiscount testMonitorNum testDayNum testFrequency addPriceName addUnitPrice "
                + "addNumber addPrice totalAnalysisPrice totalSamplePrice totalTestPrice totalTestPriceAfterCount "
                + "totalPriceAfterTotalCount totalAddPrice totalPrice totalPriceAfterTax totalDiscount totalTaxAmount "
                + "totalCost totalProfit remark");

        final String path;
        final String table;
        final Map<String, String> fields;

        Resource(String suffix, String table, String columns) {
            this.path = "/secure/emc/module/item/" + suffix;
            this.table = table;
            Map<String, String> names = new LinkedHashMap<String, String>();
            for (String name : ("id createdById createdByName createdTime createdByOrgId createdByOrgName " + columns).split(" ")) {
                names.put(name.toUpperCase(Locale.ROOT), name);
            }
            this.fields = Collections.unmodifiableMap(names);
        }

        public static Resource forPath(String path) {
            for (Resource resource : values()) {
                if ((resource.path + "/queries").equals(path)
                        || (resource.path + "/queries/raw").equals(path)
                        || (resource.path + "/queries/searchable").equals(path)) return resource;
            }
            throw new IllegalArgumentException("unsupported administrative quotation resource");
        }

        String column(String field, String alias) {
            String key = field == null ? "" : field.toUpperCase(Locale.ROOT);
            if (key.startsWith("EXT$.")) key = key.substring(5);
            if (key.startsWith("T.")) key = key.substring(2);
            if (!fields.containsKey(key)) throw new IllegalArgumentException("unsupported quotation field: " + field);
            return alias + "." + key;
        }

        String select() {
            if (this != DETECTION_FACTOR) return "SELECT A.* FROM " + table + " A";
            return "SELECT A.*,B.METHODNAME AS METHODNAMEE,B.METHODNO,B.TESTMETHODBASIS,C.ORGNAME AS PARENTORGNAME,E.TESTMETHODTYPE "
                    + "FROM T_EMC_QUOTATION_DETECTIONFACTOR A LEFT JOIN T_EMC_METHOD B ON A.METHODID=B.ID "
                    + "LEFT JOIN T_CORE_ORG C ON A.ORGID=C.ID LEFT JOIN T_EMC_ITEM_GROUP D ON A.ITEMGROUPID=D.ID "
                    + "LEFT JOIN T_EMC_TEST_METHOD E ON D.TESTMETHODID=E.ID";
        }
    }

    public String readPage(Resource resource, Set<String> organizations, QueryRequest request) throws SQLException {
        List<Object> values = new ArrayList<Object>();
        StringBuilder where = new StringBuilder(" WHERE ");
        if (organizations.isEmpty()) where.append("1=0");
        else {
            where.append(resource == Resource.DETECTION_FACTOR ? "T.ORGID IN (" : "T.CREATEDBYORGID IN (");
            placeholders(where, organizations.size());
            where.append(')'); values.addAll(organizations);
        }
        appendFilters(resource, request, where, values);
        String base = "SELECT T.* FROM (" + resource.select() + ") T" + where;
        String countSql = "SELECT COUNT(1) FROM (" + base + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY " + order(resource, request)
                + ") RN_ FROM (" + base + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, String> types = new LinkedHashMap<String, String>();
        long total;
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement count = connection.prepareStatement(countSql)) {
                bind(count, values);
                try (ResultSet result = count.executeQuery()) {
                    if (!result.next()) throw new SQLException("quotation count unavailable");
                    total = result.getLong(1);
                }
            }
            try (PreparedStatement query = connection.prepareStatement(pageSql)) {
                int next = bind(query, values);
                query.setLong(next++, ((long) request.getPageNumber() - 1) * request.getPageSize());
                query.setLong(next, (long) request.getPageNumber() * request.getPageSize());
                try (ResultSet result = query.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    for (int i = 1; i <= metadata.getColumnCount(); i++) {
                        String label = metadata.getColumnLabel(i);
                        if (!"RN_".equalsIgnoreCase(label)) types.put(label.toLowerCase(Locale.ROOT), type(metadata.getColumnType(i)));
                    }
                    while (result.next()) rows.add(resource == Resource.DETECTION_FACTOR
                            ? detectionFactorRow(result, metadata) : row(resource, result, metadata));
                }
            }
        }
        long pages = total == 0 ? 0 : (total + request.getPageSize() - 1) / request.getPageSize();
        Map<String, Object> envelope = new LinkedHashMap<String, Object>();
        envelope.put("columnTypeMap", types);
        envelope.put("first", request.getPageNumber() == 1);
        envelope.put("last", total == 0 || request.getPageNumber() >= pages);
        envelope.put("numberOfElements", rows.size());
        envelope.put("pageNumber", request.getPageNumber()); envelope.put("pageSize", request.getPageSize());
        envelope.put("rows", rows); envelope.put("total", total);
        envelope.put("totalMap", Collections.singletonMap("qty_", total)); envelope.put("totalPages", pages);
        return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    private static void appendFilters(Resource resource, QueryRequest request, StringBuilder where, List<Object> values) {
        // Non-pattern mapper arguments have resource-specific meanings. Do not silently
        // discard them and return an unfiltered success as the old Page parser did.
        if (!request.getMapperParameters().isEmpty()) throw new IllegalArgumentException("unsupported quotation query parameters");
        for (FilterParamPattern filter : request.getFilterPatterns()) {
            List<String> conditions = new ArrayList<String>();
            for (Map.Entry<String, Object> entry : filter.getMap().entrySet()) {
                conditions.add(condition(resource, entry.getKey(), filter.getMatchPattern(), entry.getValue(), values));
            }
            if (!conditions.isEmpty()) where.append(" AND (").append(String.join(disjunction(filter.getMatchPattern()) ? " OR " : " AND ", conditions)).append(')');
        }
        if (!request.getAdvancedPatterns().isEmpty()) {
            where.append(" AND (");
            boolean first = true;
            for (AdvancedFilterPattern filter : request.getAdvancedPatterns()) {
                if (!first) where.append(' ').append(link(filter.isGroupStart() ? filter.getGroupLink() : filter.getIngroupLink())).append(' ');
                if (filter.isGroupStart()) where.append('(');
                where.append(condition(resource, filter.getName(), filter.getMatchPattern(), filter.getValue(), values));
                if (filter.isGroupEnd()) where.append(')');
                first = false;
            }
            where.append(')');
        }
        MultiPropertyFilterPattern multi = request.getMultiPropertyPattern();
        if (multi != null) {
            List<String> conditions = new ArrayList<String>();
            for (String name : multi.getNames()) conditions.add(condition(resource, name.trim(), multi.getCaseSensitive() ? "SC" : "CISC", multi.getValue(), values));
            where.append(" AND (").append(String.join(" OR ", conditions)).append(')');
        }
    }

    private static String condition(Resource resource, String name, String pattern, Object value, List<Object> values) {
        if (MatchPattern.getPatternIfPossible(pattern) == null) throw new IllegalArgumentException("unknown quotation filter");
        String column = resource.column(name, "T");
        boolean insensitive = pattern.startsWith("CI") || pattern.startsWith("OCI");
        if (insensitive) { column = "UPPER(" + column + ")"; if (value != null) value = String.valueOf(value).toUpperCase(Locale.ROOT); }
        if (Arrays.asList("SC", "OSC", "CISC", "OCISC", "CBM").contains(pattern)) return bindCondition(column + " LIKE ?", "%" + value + "%", values);
        if (Arrays.asList("SB", "OSB", "CISB", "OCISB").contains(pattern)) return bindCondition(column + " LIKE ?", value + "%", values);
        if (Arrays.asList("SE", "OSE", "CISE", "OCISE").contains(pattern)) return bindCondition(column + " LIKE ?", "%" + value, values);
        if ("NSC".equals(pattern)) return bindCondition("(" + column + " NOT LIKE ? OR " + column + " IS NULL)", "%" + value + "%", values);
        if ("ISNULL".equals(pattern)) return column + " IS NULL";
        if ("ISNOTNULL".equals(pattern)) return column + " IS NOT NULL";
        if ("IN".equals(pattern) || "OIN".equals(pattern)) {
            List<?> items = inValues(value);
            if (items.isEmpty()) return "1=0";
            if (items.size() > 1000) throw new IllegalArgumentException("too many quotation IN values");
            StringBuilder sql = new StringBuilder(column).append(" IN ("); placeholders(sql, items.size()); sql.append(')');
            values.addAll(items); return sql.toString();
        }
        if ("COBM".equals(pattern)) {
            values.add(value); values.add(value + ",%"); values.add("%," + value + ",%"); values.add("%," + value);
            return "(" + column + " = ? OR " + column + " LIKE ? OR " + column + " LIKE ? OR " + column + " LIKE ?)";
        }
        String operator;
        if (Arrays.asList("DG", "TG", "NG").contains(pattern)) operator = ">";
        else if (Arrays.asList("DGOE", "TGOE", "NGOE").contains(pattern)) operator = ">=";
        else if (Arrays.asList("DL", "TL", "NL").contains(pattern)) operator = "<";
        else if (Arrays.asList("DLOE", "TLOE", "NLOE").contains(pattern)) operator = "<=";
        else if (Arrays.asList("DIFFER", "DR", "TR", "NR").contains(pattern)) operator = "<>";
        else if (Arrays.asList("EQ", "SEQ", "CISEQ", "OCISEQ", "NEQ", "DEQ", "TEQ", "OR", "NOR", "DOR", "TOR").contains(pattern)) operator = "=";
        else throw new IllegalArgumentException("unsupported quotation filter: " + pattern);
        if (value == null) return column + ("<>".equals(operator) ? " IS NOT NULL" : " IS NULL");
        if (value instanceof LocalDate) {
            column = "TRUNC(" + column + ")";
            value = java.sql.Date.valueOf((LocalDate) value);
        } else if (value instanceof LocalDateTime) {
            if ("TEQ".equals(pattern) || "TR".equals(pattern) || "TOR".equals(pattern)) {
                column = "TO_CHAR(" + column + ",'yyyy-MM-dd HH24:mi')";
                value = ((LocalDateTime) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } else value = Timestamp.valueOf((LocalDateTime) value);
        }
        return bindCondition(column + " " + operator + " ?", value, values);
    }

    private static List<?> inValues(Object value) {
        if (value == null || String.valueOf(value).isEmpty()) return Collections.emptyList();
        if (value instanceof List<?>) return (List<?>) value;
        String text = String.valueOf(value);
        if (text.startsWith("[")) { JSONArray parsed = JSON.parseArray(text); return parsed; }
        return Arrays.asList(text.split(","));
    }
    private static String bindCondition(String sql, Object value, List<Object> values) { values.add(value); return sql; }
    private static String link(String link) {
        if (link == null) return "AND";
        if (!"AND".equals(link) && !"OR".equals(link)) throw new IllegalArgumentException("invalid filter link");
        return link;
    }
    private static boolean disjunction(String pattern) { return pattern.startsWith("O") || "NOR".equals(pattern) || "DOR".equals(pattern) || "TOR".equals(pattern); }
    private static String order(Resource resource, QueryRequest request) {
        if (request.getOrderParameters().isEmpty()) return resource == Resource.AREA ? "Q.ORDERNO ASC,Q.ID ASC" : "Q.ID DESC";
        List<String> order = new ArrayList<String>();
        boolean hasId = false;
        for (Pair<String, String> pair : request.getOrderParameters()) {
            String column = resource.column(pair.getFirst(), "Q");
            hasId |= "Q.ID".equals(column);
            order.add(column + ("desc".equalsIgnoreCase(pair.getSecond()) ? " DESC" : " ASC"));
        }
        if (!hasId) order.add("Q.ID DESC");
        return String.join(",", order);
    }
    private static void placeholders(StringBuilder sql, int count) { for (int i = 0; i < count; i++) { if (i > 0) sql.append(','); sql.append('?'); } }
    private static int bind(PreparedStatement statement, List<Object> values) throws SQLException {
        int index = 1; for (Object value : values) statement.setObject(index++, value); return index;
    }
    private static String type(int sqlType) { return sqlType == Types.DATE || sqlType == Types.TIMESTAMP || sqlType == Types.TIMESTAMP_WITH_TIMEZONE ? "dateTime" : (sqlType == Types.NUMERIC || sqlType == Types.DECIMAL || sqlType == Types.INTEGER || sqlType == Types.BIGINT ? "number" : "string"); }

    private static Map<String, Object> row(Resource resource, ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> row = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i);
            if ("RN_".equalsIgnoreCase(label)) continue;
            String field = resource.fields.get(label.toUpperCase(Locale.ROOT));
            row.put(field == null ? label.toLowerCase(Locale.ROOT) : field, JdbcValue.normalize(result.getObject(i)));
        }
        return row;
    }
    private static Map<String, Object> detectionFactorRow(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        Map<String, Object> extension = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i);
            String key = detectionFactorField(label);
            Object raw = result.getObject(i);
            Object normalized = raw == null ? null : detectionFactorValue(JdbcValue.normalize(raw), metadata.getColumnType(i));
            if ("RN_".equalsIgnoreCase(label)) {
                if (normalized != null) extension.put("rn_", String.valueOf(normalized));
            } else if (detectionFactorExtension(label)) {
                if (normalized != null) extension.put(key.toLowerCase(Locale.ROOT), normalized);
            } else if (normalized != null) {
                value.put(key, normalized);
            }
        }
        value.put("ext$", extension);
        return value;
    }
    private static boolean detectionFactorExtension(String label) {
        String key = label == null ? "" : label.toUpperCase(Locale.ROOT);
        return "METHODNAMEE".equals(key) || "METHODNO".equals(key) || "TESTMETHODBASIS".equals(key)
                || "TESTMETHODTYPE".equals(key)
                || "MONITORTMPLID".equals(key) || "PARENTORGNAME".equals(key);
    }
    private static Object detectionFactorValue(Object raw, int sqlType) {
        if (sqlType == Types.DATE || sqlType == Types.TIMESTAMP || sqlType == Types.TIMESTAMP_WITH_TIMEZONE) {
            java.util.Date date = raw instanceof java.util.Date ? (java.util.Date) raw : null;
            if (date != null) return new SimpleDateFormat("yyyy-MM-dd").format(date);
        }
        return raw instanceof Timestamp ? raw.toString() : raw;
    }
    private static String detectionFactorField(String label) {
        String key = label == null ? "" : label.toUpperCase(Locale.ROOT);
        String field = Resource.DETECTION_FACTOR.fields.get(key);
        return field == null ? label.toLowerCase(Locale.ROOT) : field;
    }
}
