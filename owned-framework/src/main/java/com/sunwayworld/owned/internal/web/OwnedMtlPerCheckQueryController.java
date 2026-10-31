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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Source-owned read boundary for standard-material periodic-check plans. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedMtlPerCheckQueryController {
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedMtlPerCheckQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("material per-check dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @PostMapping(path = {
            "/secure/emc/module/item/mtl-per-checks/queries",
            "/secure/emc/module/item/mtl-per-checks/queries/raw",
            "/secure/emc/module/item/mtl-per-checks/queries/searchable"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request,
            @RequestBody(required = false) String body) {
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

    @GetMapping(path = "/secure/emc/module/item/mtl-per-checks/info/status",
            produces = MediaType.APPLICATION_JSON_VALUE)
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

    private PageResult read(HttpServletRequest request, String body) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.CONSUMABLE_READ);
        Set<String> organizations = boundary.permittedOrganizationIds(request.getSession(false));
        QueryRequest query = QueryRequest.parse(body, null);
        validateQuery(query);
        if (organizations == null || organizations.isEmpty()) return PageResult.empty(query);
        SqlSpec spec = build(query, organizations);
        try (Connection connection = dataSource.getConnection()) {
            int total;
            try (PreparedStatement count = connection.prepareStatement(spec.countSql)) {
                bind(count, spec.values);
                try (ResultSet rows = count.executeQuery()) {
                    if (!rows.next()) throw new SQLException("material per-check count failed");
                    total = rows.getInt(1);
                }
            }
            List<Object> pageValues = new ArrayList<Object>(spec.values);
            long start = ((long) query.getPageNumber() - 1L) * query.getPageSize();
            pageValues.add(start);
            pageValues.add(start + query.getPageSize());
            List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
            try (PreparedStatement select = connection.prepareStatement(spec.pageSql)) {
                bind(select, pageValues);
                try (ResultSet rows = select.executeQuery()) {
                    ResultSetMetaData metadata = rows.getMetaData();
                    while (rows.next()) values.add(row(rows, metadata));
                }
            }
            return new PageResult(total, query.getPageSize(), query.getPageNumber(), values);
        }
    }

    static void validateQuery(QueryRequest query) {
        if (query == null) throw new IllegalArgumentException("query is required");
        if (!query.getAdvancedPatterns().isEmpty() || query.getMultiPropertyPattern() != null) {
            throw new IllegalArgumentException("advanced material per-check query shape is unsupported");
        }
        for (String key : query.getMapperParameters().keySet()) {
            if (!"methodid_seq".equalsIgnoreCase(key)) {
                throw new IllegalArgumentException("unsupported material per-check query parameter: " + key);
            }
        }
        for (FilterParamPattern filter : query.getFilterPatterns()) {
            if (MatchPattern.getPatternIfPossible(filter.getMatchPattern()) == null) {
                throw new IllegalArgumentException("unsupported material per-check filter pattern");
            }
            for (String name : filter.getMap().keySet()) column(name);
        }
        for (Pair<String, String> order : query.getOrderParameters()) orderColumn(order.getFirst());
    }

    private static SqlSpec build(QueryRequest query, Set<String> organizations) {
        List<Object> values = new ArrayList<Object>();
        StringBuilder where = new StringBuilder("T.CREATEDBYORGID IN (");
        int index = 0;
        for (String organization : organizations) {
            if (index++ > 0) where.append(',');
            where.append('?');
            values.add(organization);
        }
        where.append(')');
        for (Map.Entry<String, Object> parameter : query.getMapperParameters().entrySet()) {
            if ("methodid_seq".equalsIgnoreCase(parameter.getKey())) appendCondition(where, values, "T.METHODID", MatchPattern.SEQ, parameter.getValue());
        }
        for (FilterParamPattern filter : query.getFilterPatterns()) {
            MatchPattern pattern = MatchPattern.getPatternIfPossible(filter.getMatchPattern());
            for (Map.Entry<String, Object> item : filter.getMap().entrySet()) {
                appendCondition(where, values, "T." + column(item.getKey()), pattern, item.getValue());
            }
        }
        String base = "SELECT T.* FROM T_EMC_MTL_PER_CHECK T WHERE " + where;
        String count = "SELECT COUNT(1) FROM (" + base + ") Q";
        String order = query.getOrderParameters().isEmpty()
                ? "Q.ID DESC"
                : "Q." + orderColumn(query.getOrderParameters().get(0).getFirst())
                    + ("desc".equalsIgnoreCase(query.getOrderParameters().get(0).getSecond()) ? " DESC" : " ASC");
        String page = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY " + order + ") RN FROM ("
                + base + ") Q) P WHERE RN>? AND RN<=? ORDER BY RN";
        return new SqlSpec(count, page, values);
    }

    private static void appendCondition(StringBuilder where, List<Object> values,
            String column, MatchPattern pattern, Object value) {
        String name = pattern.name();
        if ("ISNULL".equals(name)) { where.append(" AND ").append(column).append(" IS NULL"); return; }
        if ("ISNOTNULL".equals(name)) { where.append(" AND ").append(column).append(" IS NOT NULL"); return; }
        if ("EQ".equals(name) || "SEQ".equals(name) || "CISEQ".equals(name)) {
            where.append(" AND ").append("CISEQ".equals(name) ? "UPPER(" + column + ")" : column).append("=?");
            values.add("CISEQ".equals(name) ? String.valueOf(value).toUpperCase(Locale.ROOT) : value);
            return;
        }
        if ("DIFFER".equals(name)) {
            where.append(value == null ? " AND " + column + " IS NOT NULL" : " AND " + column + "<>?");
            if (value != null) values.add(value);
            return;
        }
        if ("CISC".equals(name) || "SC".equals(name) || "CISB".equals(name)
                || "SB".equals(name) || "CISE".equals(name) || "SE".equals(name)) {
            boolean insensitive = name.startsWith("CIS");
            String text = String.valueOf(value);
            String patternValue = name.endsWith("C") ? "%" + text + "%"
                    : name.endsWith("B") ? text + "%" : "%" + text;
            where.append(" AND ").append(insensitive ? "UPPER(" + column + ")" : column).append(" LIKE ?");
            values.add(insensitive ? patternValue.toUpperCase(Locale.ROOT) : patternValue);
            return;
        }
        throw new IllegalArgumentException("unsupported material per-check filter pattern: " + name);
    }

    private static String column(String name) {
        String key = name == null ? "" : name.toUpperCase(Locale.ROOT);
        for (String allowed : new String[] {"ID", "CODENAME", "VERIFYORG", "CYCLE", "MAINTDATE",
                "MAINTENDDATE", "PLANCATEGORY", "CREATEDBYID", "CREATEDBYORGID", "CHECKREASON",
                "CHECKWAY", "CHECKUSERNAME"}) if (allowed.equals(key)) return allowed;
        throw new IllegalArgumentException("unsupported material per-check column: " + name);
    }

    private static String orderColumn(String name) {
        return column(name);
    }

    private static void bind(PreparedStatement statement, List<Object> values) throws SQLException {
        for (int i = 0; i < values.size(); i++) statement.setObject(i + 1, values.get(i));
    }

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i);
            if (!"RN".equalsIgnoreCase(label)) value.put(camel(label), JdbcValue.normalize(result.getObject(i)));
        }
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
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.MTL_PER_CHECK.INVALID_REQUEST\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.MTL_PER_CHECK.UNAVAILABLE\"}"); }

    private static final class SqlSpec {
        private final String countSql;
        private final String pageSql;
        private final List<Object> values;
        private SqlSpec(String countSql, String pageSql, List<Object> values) { this.countSql = countSql; this.pageSql = pageSql; this.values = values; }
    }

    private static final class PageResult {
        private final int total;
        private final int pageSize;
        private final int pageNumber;
        private final List<Map<String, Object>> rows;
        private PageResult(int total, int pageSize, int pageNumber, List<Map<String, Object>> rows) { this.total = total; this.pageSize = pageSize; this.pageNumber = pageNumber; this.rows = rows; }
        private static PageResult empty(QueryRequest query) { return new PageResult(0, query.getPageSize(), query.getPageNumber(), Collections.<Map<String, Object>>emptyList()); }
        private int totalPages() { return total == 0 ? 0 : (total + pageSize - 1) / pageSize; }
    }
}
