package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.owned.internal.data.JdbcValue;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Source-owned read implementation for the inherited material-category
 * GenericResource.  All aliases execute in this Spring application and use
 * the authenticated organization scope against the real Oracle table.
 */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedMtlCategoryQueryController {
    private static final String BASE = "/secure/emc/module/mdm/basemdm/mtl-categories";
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedMtlCategoryQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("material category dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @PostMapping(path = {BASE + "/queries", BASE + "/queries/raw", BASE + "/queries/searchable"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try {
            return ok(page(read(request, body)));
        } catch (AccessDeniedException denied) {
            return forbidden();
        } catch (IllegalArgumentException invalid) {
            return badRequest();
        } catch (SQLException failure) {
            return unavailable();
        }
    }

    @GetMapping(path = BASE + "/info/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try {
            boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.CONSUMABLE_READ);
            return ok("{\"ext$\":{}}");
        } catch (AccessDeniedException denied) {
            return forbidden();
        } catch (SQLException failure) {
            return unavailable();
        }
    }

    @GetMapping(path = BASE + "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request, @PathVariable("id") String id) {
        try {
            long numericId = numericId(id);
            boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.CONSUMABLE_READ);
            Set<String> organizations = boundary.permittedOrganizationIds(request.getSession(false));
            if (organizations == null || organizations.isEmpty()) return ok("{}");
            List<Object> values = new ArrayList<Object>();
            String sql = "SELECT T.* FROM T_EMC_MTL_CATEGORY T WHERE T.ID=? AND "
                    + scope("T.CREATEDBYORGID", organizations, values);
            values.add(0, Long.valueOf(numericId));
            try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
                bind(statement, values);
                try (ResultSet rows = statement.executeQuery()) {
                    if (!rows.next()) return ok("{}");
                    return ok(JSON.toJSONString(row(rows, rows.getMetaData()), SerializerFeature.WriteMapNullValue));
                }
            }
        } catch (AccessDeniedException denied) {
            return forbidden();
        } catch (IllegalArgumentException invalid) {
            return badRequest();
        } catch (SQLException failure) {
            return unavailable();
        }
    }

    @GetMapping(path = BASE + "/{id}/info/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detailStatus(HttpServletRequest request, @PathVariable("id") String id) {
        try {
            numericId(id);
            boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.CONSUMABLE_READ);
            return ok("{\"ext$\":{}}");
        } catch (AccessDeniedException denied) {
            return forbidden();
        } catch (IllegalArgumentException invalid) {
            return badRequest();
        } catch (SQLException failure) {
            return unavailable();
        }
    }

    private PageResult read(HttpServletRequest request, String body) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.CONSUMABLE_READ);
        Set<String> organizations = boundary.permittedOrganizationIds(request.getSession(false));
        QueryRequest query = QueryRequest.parse(body, null);
        validateQuery(query);
        if (organizations == null || organizations.isEmpty()) return PageResult.empty(query);
        List<Object> values = new ArrayList<Object>();
        StringBuilder where = new StringBuilder(scope("T.CREATEDBYORGID", organizations, values));
        for (FilterParamPattern filter : query.getFilterPatterns()) {
            MatchPattern pattern = MatchPattern.getPatternIfPossible(filter.getMatchPattern());
            for (Map.Entry<String, Object> entry : filter.getMap().entrySet()) {
                appendCondition(where, values, "T." + column(entry.getKey()), pattern, entry.getValue());
            }
        }
        String base = "SELECT T.* FROM T_EMC_MTL_CATEGORY T WHERE " + where;
        String countSql = "SELECT COUNT(1) FROM (" + base + ") Q";
        String order = order(query);
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY " + order + ") RN_ FROM ("
                + base + ") Q) P WHERE RN_>? AND RN_<=? ORDER BY RN_";
        try (Connection connection = dataSource.getConnection()) {
            int total;
            try (PreparedStatement count = connection.prepareStatement(countSql)) {
                bind(count, values);
                try (ResultSet rows = count.executeQuery()) {
                    if (!rows.next()) throw new SQLException("material category count returned no row");
                    total = rows.getInt(1);
                }
            }
            List<Object> pageValues = new ArrayList<Object>(values);
            long start = ((long) query.getPageNumber() - 1L) * query.getPageSize();
            pageValues.add(start);
            pageValues.add(start + query.getPageSize());
            List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
            Map<String, String> types = new LinkedHashMap<String, String>();
            try (PreparedStatement select = connection.prepareStatement(pageSql)) {
                bind(select, pageValues);
                try (ResultSet result = select.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    for (int i = 1; i <= metadata.getColumnCount(); i++) {
                        String label = metadata.getColumnLabel(i);
                        if (label != null) types.put(label.toLowerCase(Locale.ROOT), type(metadata.getColumnType(i)));
                    }
                    while (result.next()) rows.add(row(result, metadata));
                }
            }
            return new PageResult(total, query.getPageSize(), query.getPageNumber(), rows, types);
        }
    }

    public static void validateQuery(QueryRequest query) {
        if (query == null) throw new IllegalArgumentException("query is required");
        if (!query.getAdvancedPatterns().isEmpty() || query.getMultiPropertyPattern() != null) {
            throw new IllegalArgumentException("advanced material category query shape is unsupported");
        }
        for (String key : query.getMapperParameters().keySet()) {
            // The inherited selectable resource accepts the optional q search
            // hint without applying it to this mapper query.
            if (!"q".equalsIgnoreCase(key)) throw new IllegalArgumentException("unsupported material category parameter: " + key);
        }
        for (FilterParamPattern filter : query.getFilterPatterns()) {
            if (MatchPattern.getPatternIfPossible(filter.getMatchPattern()) == null)
                throw new IllegalArgumentException("unsupported material category filter pattern");
            for (String name : filter.getMap().keySet()) column(name);
        }
        for (Pair<String, String> order : query.getOrderParameters()) orderColumn(order.getFirst());
    }

    private static void appendCondition(StringBuilder where, List<Object> values, String column,
            MatchPattern pattern, Object value) {
        String operation = pattern.name();
        if ("ISNULL".equals(operation)) { where.append(" AND ").append(column).append(" IS NULL"); return; }
        if ("ISNOTNULL".equals(operation)) { where.append(" AND ").append(column).append(" IS NOT NULL"); return; }
        if ("EQ".equals(operation) || "SEQ".equals(operation) || "CISEQ".equals(operation)) {
            if (value == null) { where.append(" AND ").append(column).append(" IS NULL"); return; }
            if ("CISEQ".equals(operation)) where.append(" AND UPPER(").append(column).append(")=?");
            else where.append(" AND ").append(column).append("=?");
            values.add("CISEQ".equals(operation) ? String.valueOf(value).toUpperCase(Locale.ROOT) : value);
            return;
        }
        if ("DIFFER".equals(operation)) {
            where.append(value == null ? " AND " + column + " IS NOT NULL" : " AND " + column + "<>?");
            if (value != null) values.add(value);
            return;
        }
        if ("CISC".equals(operation) || "SC".equals(operation) || "CISB".equals(operation)
                || "SB".equals(operation) || "CISE".equals(operation) || "SE".equals(operation)) {
            boolean insensitive = operation.startsWith("CIS");
            String text = String.valueOf(value);
            String patternValue = operation.endsWith("C") ? "%" + text + "%"
                    : operation.endsWith("B") ? text + "%" : "%" + text;
            where.append(" AND ").append(insensitive ? "UPPER(" + column + ")" : column).append(" LIKE ?");
            values.add(insensitive ? patternValue.toUpperCase(Locale.ROOT) : patternValue);
            return;
        }
        if ("NG".equals(operation) || "TG".equals(operation) || "DG".equals(operation)) {
            where.append(" AND ").append(column).append(">?"); values.add(value); return;
        }
        if ("NGOE".equals(operation) || "TGOE".equals(operation) || "DGOE".equals(operation)) {
            where.append(" AND ").append(column).append(">=?"); values.add(value); return;
        }
        if ("NL".equals(operation) || "TL".equals(operation) || "DL".equals(operation)) {
            where.append(" AND ").append(column).append("<?"); values.add(value); return;
        }
        if ("NLOE".equals(operation) || "TLOE".equals(operation) || "DLOE".equals(operation)) {
            where.append(" AND ").append(column).append("<=?"); values.add(value); return;
        }
        if ("DEQ".equals(operation)) {
            if (value == null) { where.append(" AND ").append(column).append(" IS NULL"); return; }
            where.append(" AND TO_CHAR(").append(column).append(", 'yyyy-MM-dd')=?"); values.add(String.valueOf(value)); return;
        }
        if ("TEQ".equals(operation)) {
            if (value == null) { where.append(" AND ").append(column).append(" IS NULL"); return; }
            where.append(" AND TO_CHAR(").append(column).append(", 'yyyy-MM-dd HH24:mi')=?"); values.add(String.valueOf(value)); return;
        }
        throw new IllegalArgumentException("unsupported material category filter pattern: " + operation);
    }

    private static String scope(String column, Set<String> organizations, List<Object> values) {
        StringBuilder result = new StringBuilder(column).append(" IN (");
        int index = 0;
        for (String organization : organizations) {
            if (index++ > 0) result.append(',');
            result.append('?');
            values.add(organization);
        }
        return result.append(')').toString();
    }

    private static String column(String name) {
        String key = name == null ? "" : name.toUpperCase(Locale.ROOT);
        for (String allowed : new String[] {"ID", "MTLCATEGORYID", "MTLCATEGORYNAME", "MTLCATEGORYDESC",
                "MANAGEMENTTYPE", "CREATEDBYID", "CREATEDBYNAME", "CREATEDTIME", "CREATEDBYORGID", "CREATEDBYORGNAME"}) {
            if (allowed.equals(key)) return allowed;
        }
        throw new IllegalArgumentException("unsupported material category column: " + name);
    }

    private static String order(QueryRequest query) {
        if (query.getOrderParameters().isEmpty()) return "Q.ID DESC";
        List<String> values = new ArrayList<String>();
        for (Pair<String, String> order : query.getOrderParameters()) {
            values.add("Q." + orderColumn(order.getFirst()) + ("desc".equalsIgnoreCase(order.getSecond()) ? " DESC" : " ASC"));
        }
        return String.join(",", values);
    }

    private static String orderColumn(String name) { return column(name); }

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        Map<String, Object> extension = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i);
            if (label == null) continue;
            Object item = JdbcValue.normalize(result.getObject(i));
            String key = label.toUpperCase(Locale.ROOT);
            if ("RN_".equals(key) || "MTLCATEGORYDESC".equals(key)) extension.put(camel(label), item);
            else value.put(camel(label), item);
        }
        if (!extension.isEmpty()) value.put("ext$", extension);
        return value;
    }

    private static String camel(String name) {
        StringBuilder value = new StringBuilder();
        boolean upper = false;
        for (char c : name.toLowerCase(Locale.ROOT).toCharArray()) {
            if (c == '_') { upper = true; continue; }
            value.append(upper ? Character.toUpperCase(c) : c);
            upper = false;
        }
        return value.toString();
    }

    private static String type(int sqlType) {
        if (sqlType == Types.DATE || sqlType == Types.TIMESTAMP || sqlType == Types.TIMESTAMP_WITH_TIMEZONE) return "dateTime";
        if (sqlType == Types.NUMERIC || sqlType == Types.DECIMAL || sqlType == Types.INTEGER || sqlType == Types.BIGINT) return "number";
        return "string";
    }

    private static long numericId(String id) {
        if (id == null || !id.matches("[0-9]{1,30}")) throw new IllegalArgumentException("material category id is invalid");
        return Long.parseLong(id);
    }

    private static void bind(PreparedStatement statement, List<Object> values) throws SQLException {
        for (int i = 0; i < values.size(); i++) statement.setObject(i + 1, values.get(i));
    }

    private static String page(PageResult page) {
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("columnTypeMap", page.types);
        body.put("first", page.pageNumber == 1);
        body.put("last", page.total == 0 || page.pageNumber >= page.totalPages());
        body.put("numberOfElements", page.rows.size());
        body.put("pageNumber", page.pageNumber);
        body.put("pageSize", page.pageSize);
        body.put("rows", page.rows);
        body.put("total", page.total);
        Map<String, Object> totals = new LinkedHashMap<String, Object>();
        totals.put("qty_", page.total);
        body.put("totalMap", totals);
        body.put("totalPages", page.totalPages());
        return JSON.toJSONString(body, SerializerFeature.WriteMapNullValue);
    }

    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().header("Cache-Control", "no-store").body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.MTL_CATEGORY.INVALID_REQUEST\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.MTL_CATEGORY.UNAVAILABLE\"}"); }

    private static final class PageResult {
        private final int total;
        private final int pageSize;
        private final int pageNumber;
        private final List<Map<String, Object>> rows;
        private final Map<String, String> types;
        private PageResult(int total, int pageSize, int pageNumber, List<Map<String, Object>> rows, Map<String, String> types) {
            this.total = total; this.pageSize = pageSize; this.pageNumber = pageNumber; this.rows = rows; this.types = types;
        }
        private static PageResult empty(QueryRequest query) {
            return new PageResult(0, query.getPageSize(), query.getPageNumber(), Collections.<Map<String, Object>>emptyList(), Collections.<String, String>emptyMap());
        }
        private int totalPages() { return total == 0 ? 0 : (total + pageSize - 1) / pageSize; }
    }
}
