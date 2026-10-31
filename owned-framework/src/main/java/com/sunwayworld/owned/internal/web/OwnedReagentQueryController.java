package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.owned.internal.data.JdbcValue;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Read-only Oracle seam for the original reagent list and composition grids. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedReagentQueryController {
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedReagentQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @PostMapping(path = {"/secure/core/module/bp/reagents/queries", "/secure/core/module/bp/reagents/queries/raw",
            "/secure/emc/module/bp/reagents/queries", "/secure/emc/module/bp/reagents/queries/raw"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> reagents(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try { return ok(page(read(request, body, false, null))); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = {"/secure/core/module/bp/reagents/{id}/compositions/queries",
            "/secure/emc/module/bp/reagents/{id}/compositions/queries"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> compositions(HttpServletRequest request, @PathVariable("id") Long id,
            @RequestBody(required = false) String body) {
        try {
            if (id == null || id.longValue() < 0L) throw new IllegalArgumentException("reagent id is invalid");
            return ok(page(read(request, body, true, id)));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private PageResult read(HttpServletRequest request, String body,
            boolean compositions, Long reagentId) throws SQLException {
        OwnedPrincipal principal = require(request);
        Set<String> organizations = boundary.permittedOrganizationIds(request.getSession(false));
        QueryRequest query = QueryRequest.parse(body, null);
        validateQuery(query, compositions);
        if (organizations == null || organizations.isEmpty()) return PageResult.empty(query);
        SqlSpec spec = build(query, organizations, compositions, reagentId);
        try (Connection connection = dataSource.getConnection()) {
            int total;
            try (PreparedStatement count = connection.prepareStatement(spec.countSql)) {
                bind(count, spec.values);
                try (ResultSet result = count.executeQuery()) {
                    if (!result.next()) throw new SQLException("reagent count returned no row");
                    total = result.getInt(1);
                }
            }
            List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
            List<Object> pageValues = new ArrayList<Object>(spec.values);
            long start = ((long) query.getPageNumber() - 1L) * query.getPageSize();
            pageValues.add(start); pageValues.add(start + query.getPageSize());
            try (PreparedStatement select = connection.prepareStatement(spec.pageSql)) {
                bind(select, pageValues);
                try (ResultSet result = select.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    while (result.next()) rows.add(row(result, metadata));
                }
            }
            return new PageResult(total, query.getPageSize(), query.getPageNumber(), rows);
        }
    }

    /** Package-visible contract seam used by source-only query tests. */
    public static void validateQuery(QueryRequest query, boolean compositions) {
        if (query == null) throw new IllegalArgumentException("query is required");
        if (!query.getAdvancedPatterns().isEmpty() || query.getMultiPropertyPattern() != null) {
            throw new IllegalArgumentException("advanced reagent query shape is unsupported");
        }
        for (Map.Entry<String, Object> parameter : query.getMapperParameters().entrySet()) {
            String key = parameter.getKey() == null ? "" : parameter.getKey().toLowerCase(Locale.ROOT);
            if (!compositions && "methodid_seq".equals(key)) continue;
            throw new IllegalArgumentException("unsupported reagent query parameter: " + parameter.getKey());
        }
        for (FilterParamPattern filter : query.getFilterPatterns()) {
            MatchPattern pattern = MatchPattern.getPatternIfPossible(filter.getMatchPattern());
            if (pattern == null) throw new IllegalArgumentException("unsupported reagent filter pattern");
            for (String name : filter.getMap().keySet()) column(name, compositions);
        }
        for (Pair<String, String> order : query.getOrderParameters()) orderColumn(order.getFirst(), compositions);
    }

    private static SqlSpec build(QueryRequest query, Set<String> organizations,
            boolean compositions, Long reagentId) {
        List<Object> values = new ArrayList<Object>();
        String alias = compositions ? "C" : "R";
        StringBuilder where = new StringBuilder();
        if (compositions) {
            where.append("C.REAGENTID = ? AND EXISTS (SELECT 1 FROM T_EMC_REAGENT R_SCOPE WHERE R_SCOPE.ID = C.REAGENTID AND ");
            values.add(reagentId);
            appendScope(where, "R_SCOPE.CREATEDBYORGID", organizations, values);
            where.append(')');
        } else {
            appendScope(where, "R.CREATEDBYORGID", organizations, values);
        }
        appendMapperParameters(query, alias, where, values, compositions);
        appendFilters(query, alias, where, values, compositions);
        String base = "SELECT " + alias + ".* FROM "
                + (compositions ? "T_EMC_REAGENT_COMPOSITION C" : "T_EMC_REAGENT R")
                + " WHERE " + where;
        String countSql = "SELECT COUNT(1) FROM (" + base + ") Q";
        String order = order(query, compositions);
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY " + order + ") RN FROM ("
                + base + ") Q) P WHERE RN > ? AND RN <= ? ORDER BY RN";
        return new SqlSpec(countSql, pageSql, values);
    }

    private static void appendMapperParameters(QueryRequest query, String alias,
            StringBuilder where, List<Object> values, boolean compositions) {
        for (Map.Entry<String, Object> parameter : query.getMapperParameters().entrySet()) {
            if ("methodid_seq".equals(parameter.getKey().toLowerCase(Locale.ROOT))) {
                if (compositions) throw new IllegalArgumentException("methodId_SEQ is only valid for reagent rows");
                Object value = parameter.getValue();
                if (value != null && !String.valueOf(value).trim().isEmpty()) {
                    appendCondition(where, values, alias + ".METHODID", MatchPattern.SEQ, value);
                }
            }
        }
    }

    private static void appendFilters(QueryRequest query, String alias, StringBuilder where,
            List<Object> values, boolean compositions) {
        for (FilterParamPattern filter : query.getFilterPatterns()) {
            MatchPattern pattern = MatchPattern.getPatternIfPossible(filter.getMatchPattern());
            for (Map.Entry<String, Object> entry : filter.getMap().entrySet()) {
                appendCondition(where, values, alias + "." + column(entry.getKey(), compositions), pattern, entry.getValue());
            }
        }
    }

    private static void appendCondition(StringBuilder where, List<Object> values,
            String column, MatchPattern pattern, Object value) {
        String operation = pattern.name();
        if ("ISNULL".equals(operation)) { where.append(" AND ").append(column).append(" IS NULL"); return; }
        if ("ISNOTNULL".equals(operation)) { where.append(" AND ").append(column).append(" IS NOT NULL"); return; }
        if ("SEQ".equals(operation) || "EQ".equals(operation) || "CISEQ".equals(operation)) {
            if (value == null) { where.append(" AND ").append(column).append(" IS NULL"); return; }
            if ("CISEQ".equals(operation)) {
                where.append(" AND UPPER(").append(column).append(") = ?"); values.add(String.valueOf(value).toUpperCase(Locale.ROOT));
            } else { where.append(" AND ").append(column).append(" = ?"); values.add(value); }
            return;
        }
        if ("DIFFER".equals(operation)) {
            where.append(" AND ").append(column).append(value == null ? " IS NOT NULL" : " <> ?");
            if (value != null) values.add(value);
            return;
        }
        if ("CISC".equals(operation) || "SC".equals(operation)
                || "CISB".equals(operation) || "SB".equals(operation)
                || "CISE".equals(operation) || "SE".equals(operation)) {
            boolean insensitive = operation.startsWith("CIS");
            String text = String.valueOf(value);
            String patternValue = operation.endsWith("C") ? "%" + text + "%"
                    : operation.endsWith("B") ? text + "%" : "%" + text;
            where.append(" AND ").append(insensitive ? "UPPER(" + column + ")" : column).append(" LIKE ?");
            values.add(insensitive ? patternValue.toUpperCase(Locale.ROOT) : patternValue);
            return;
        }
        if ("DG".equals(operation) || "DGOE".equals(operation) || "DL".equals(operation)
                || "DLOE".equals(operation) || "DEQ".equals(operation)) {
            appendDateCondition(where, values, column, operation, value); return;
        }
        if ("TG".equals(operation) || "TGOE".equals(operation) || "TL".equals(operation)
                || "TLOE".equals(operation) || "TEQ".equals(operation)) {
            appendTimestampCondition(where, values, column, operation, value); return;
        }
        if ("NG".equals(operation) || "NGOE".equals(operation) || "NL".equals(operation)
                || "NLOE".equals(operation) || "NEQ".equals(operation)) {
            String op = "NG".equals(operation) ? ">" : "NGOE".equals(operation) ? ">="
                    : "NL".equals(operation) ? "<" : "NLOE".equals(operation) ? "<=" : "=";
            where.append(" AND ").append(column).append(' ').append(op).append(" ?"); values.add(value); return;
        }
        throw new IllegalArgumentException("unsupported reagent filter pattern: " + operation);
    }

    private static void appendDateCondition(StringBuilder where, List<Object> values,
            String column, String operation, Object value) {
        if ("DEQ".equals(operation)) {
            if (value == null) { where.append(" AND ").append(column).append(" IS NULL"); return; }
            where.append(" AND ").append(column).append(" IS NOT NULL AND TO_CHAR(").append(column).append(", 'yyyy-MM-dd') = ?");
            values.add(String.valueOf(value)); return;
        }
        String op = "DG".equals(operation) ? ">" : "DGOE".equals(operation) ? ">="
                : "DL".equals(operation) ? "<" : "<=";
        where.append(" AND ").append(column).append(' ').append(op);
        if ("DLOE".equals(operation)) where.append(" ? + 1"); else where.append(" ?");
        values.add(value);
    }

    private static void appendTimestampCondition(StringBuilder where, List<Object> values,
            String column, String operation, Object value) {
        if ("TEQ".equals(operation)) {
            if (value == null) { where.append(" AND ").append(column).append(" IS NULL"); return; }
            where.append(" AND ").append(column).append(" IS NOT NULL AND TO_CHAR(").append(column).append(", 'yyyy-MM-dd HH24:mi') = ?");
            values.add(((LocalDateTime) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))); return;
        }
        String op = "TG".equals(operation) ? ">" : "TGOE".equals(operation) ? ">="
                : "TL".equals(operation) ? "<" : "<=";
        where.append(" AND ").append(column).append(' ').append(op).append(" ?"); values.add(value);
    }

    private static String order(QueryRequest query, boolean compositions) {
        if (query.getOrderParameters().isEmpty()) return "Q.ID DESC";
        List<String> parts = new ArrayList<String>();
        for (Pair<String, String> pair : query.getOrderParameters()) {
            parts.add("Q." + orderColumn(pair.getFirst(), compositions)
                    + ("desc".equalsIgnoreCase(pair.getSecond()) ? " DESC" : " ASC"));
        }
        return String.join(",", parts);
    }

    private static String orderColumn(String name, boolean compositions) {
        String key = name == null ? "" : name.toUpperCase(Locale.ROOT);
        if ("ID".equals(key)) return "ID";
        if (!compositions && ("REAGENTNAME".equals(key) || "PREPAREDATE".equals(key)
                || "VALIDDATE".equals(key) || "CREATEDTIME".equals(key) || "METHODID".equals(key))) return key;
        if (compositions && ("REAGENTID".equals(key) || "REAGENTNAME".equals(key)
                || "REAGENTBATCHNO".equals(key))) return key;
        throw new IllegalArgumentException("unsupported reagent order column: " + name);
    }

    private static String column(String name, boolean compositions) {
        String key = name == null ? "" : name.toUpperCase(Locale.ROOT);
        if (compositions) {
            String[] fields = {"ID", "REAGENTID", "REAGENTNAME", "REAGENTFACTORY", "REAGENTBATCHNO", "WEIGHTVOLUME", "REMARK"};
            for (String field : fields) if (field.equals(key)) return field;
        } else {
            String[] fields = {"ID", "REAGENTNAME", "PREPAREDATE", "REAGENTSPECIFICATION", "DISHWEIGHT",
                    "DISHREAGENTWEIGHT", "REAGENTWEIGHT", "CONSTANTVOLUME", "PREPARECONCENT", "APPLICABLEITEM",
                    "VALIDDATE", "REMARK", "EQUIPTID", "EQUIPTNAME", "TEMPERATURE", "HUMIDITY", "METHODID",
                    "CREATEDBYID", "CREATEDBYNAME", "CREATEDTIME", "CREATEDBYORGID", "CREATEDBYORGNAME"};
            for (String field : fields) if (field.equals(key)) return field;
        }
        throw new IllegalArgumentException("unsupported reagent filter column: " + name);
    }

    private static void appendScope(StringBuilder sql, String column,
            Set<String> organizations, List<Object> values) {
        if (organizations.isEmpty()) { sql.append("1 = 0"); return; }
        sql.append('(');
        int index = 0;
        for (String organization : organizations) {
            if (index % 1000 == 0) {
                if (index != 0) sql.append(") OR ");
                sql.append(column).append(" IN (");
            } else sql.append(',');
            sql.append('?');
            values.add(organization);
            index++;
        }
        sql.append("))");
    }

    private static void bind(PreparedStatement statement, List<Object> values) throws SQLException {
        for (int i = 0; i < values.size(); i++) {
            Object value = values.get(i);
            if (value instanceof LocalDate) value = java.sql.Date.valueOf((LocalDate) value);
            else if (value instanceof LocalDateTime) value = java.sql.Timestamp.valueOf((LocalDateTime) value);
            statement.setObject(i + 1, value);
        }
    }

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> row = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String name = metadata.getColumnLabel(i);
            if ("RN".equalsIgnoreCase(name)) continue;
            row.put(camel(name), JdbcValue.normalize(result.getObject(i)));
        }
        Map<String, Object> extension = new LinkedHashMap<String, Object>();
        for (String key : new String[] {"preparebasis", "preparevolume", "solvent", "auditorid",
                "auditorname", "equiptid", "equiptname", "environment"}) {
            if (row.containsKey(key)) extension.put(key, row.get(key));
        }
        row.put("ext$", extension);
        return row;
    }

    private static String camel(String name) {
        StringBuilder value = new StringBuilder();
        boolean upper = false;
        for (char character : name.toLowerCase(Locale.ROOT).toCharArray()) {
            if (character == '_') { upper = true; continue; }
            value.append(upper ? Character.toUpperCase(character) : character);
            upper = false;
        }
        return value.toString();
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.CONSUMABLE_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private static String page(PageResult page) {
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("columnTypeMap", new LinkedHashMap<String, Object>());
        body.put("first", page.pageNumber == 1);
        body.put("last", page.total == 0 || page.pageNumber >= page.totalPages());
        body.put("numberOfElements", page.rows.size());
        body.put("pageNumber", page.pageNumber);
        body.put("pageSize", page.pageSize);
        body.put("rows", page.rows);
        body.put("total", page.total);
        body.put("totalMap", new LinkedHashMap<String, Object>());
        body.put("totalPages", page.totalPages());
        return JSON.toJSONString(body, SerializerFeature.WriteMapNullValue);
    }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().header("Cache-Control", "no-store").body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.REAGENT.INVALID_REQUEST\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.REAGENT.UNAVAILABLE\"}"); }

    private static final class SqlSpec {
        private final String countSql;
        private final String pageSql;
        private final List<Object> values;
        private SqlSpec(String countSql, String pageSql, List<Object> values) {
            this.countSql = countSql; this.pageSql = pageSql; this.values = values;
        }
    }

    static final class PageResult {
        private final int total;
        private final int pageSize;
        private final int pageNumber;
        private final List<Map<String, Object>> rows;
        private PageResult(int total, int pageSize, int pageNumber, List<Map<String, Object>> rows) {
            this.total = total; this.pageSize = pageSize; this.pageNumber = pageNumber; this.rows = rows;
        }
        private static PageResult empty(QueryRequest query) {
            return new PageResult(0, query.getPageSize(), query.getPageNumber(), Collections.<Map<String, Object>>emptyList());
        }
        private int totalPages() { return total == 0 ? 0 : (total + pageSize - 1) / pageSize; }
    }
}
