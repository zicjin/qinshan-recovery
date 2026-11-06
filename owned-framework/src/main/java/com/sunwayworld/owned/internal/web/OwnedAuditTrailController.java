package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunwayworld.owned.internal.data.JdbcValue;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
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
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Source-owned read seam for the two audit-trail endpoints used by recovered
 * project pages.  Visibility is deliberately opt-in through the observed
 * audit-log menu grant (9404 by default) and is restricted to the current
 * principal organization until the production audit visibility policy is
 * explicitly reconstructed.
 */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedAuditTrailController {
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedAuditTrailController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("audit dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @PostMapping(path = {"/secure/core/module/sys/at-records/queries", "/secure/emc/module/sys/at-records/queries"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> records(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        return query(request, body, false);
    }

    @PostMapping(path = {"/secure/core/module/sys/at-records/record-lines/queries", "/secure/emc/module/sys/at-records/record-lines/queries"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> recordLines(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        return query(request, body, true);
    }

    /**
     * The recovered GenericResource exposes status routes for every resource,
     * including entities that do not implement Activatable, Suspendable or
     * Auditable.  The supplier serializes the resulting empty status DTO as
     * {"ext$":{}}. GenericResource does not apply an entity permission
     * annotation to this projection, so the owned seam refreshes the session
     * identity but does not require the separate audit-log read grant.
     */
    @GetMapping(path = {"/secure/emc/module/bp/audit-records/info/status",
            "/secure/emc/module/bp/audit-records/{id}/info/status"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try {
            boundary.currentPrincipal(request.getSession(false));
            return ok("{\"ext$\":{}}");
        } catch (AccessDeniedException denied) {
            return forbidden();
        } catch (SQLException failure) {
            return unavailable();
        }
    }

    private ResponseEntity<String> query(HttpServletRequest request, String body, boolean lines) {
        try {
            OwnedPrincipal principal = require(request);
            QueryRequest parsed = QueryRequest.parse(body, null);
            AuditQuery input = AuditQuery.from(parsed, lines);
            return ok(read(principal.getOrgId(), input));
        } catch (AccessDeniedException denied) {
            return forbidden();
        } catch (IllegalArgumentException invalid) {
            return badRequest();
        } catch (SQLException failure) {
            return unavailable();
        }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requireAuditRead(request.getSession(false));
        return boundary.currentPrincipal(request.getSession(false));
    }

    private String read(String organization, AuditQuery input) throws SQLException {
        List<Object> values = new ArrayList<Object>();
        StringBuilder where = new StringBuilder();
        if (input.lines) {
            where.append("R.CREATEDBYORGID=?");
            values.add(organization);
        } else {
            where.append("A.CREATEDBYORGID=?");
            values.add(organization);
        }
        if (input.targetId != null) {
            where.append(input.lines ? " AND R.TARGETID=?" : " AND A.TARGETID=?");
            values.add(input.targetId);
        }
        if (input.recordId != null) {
            where.append(input.lines ? " AND L.RECORDID=?" : " AND A.ID=?");
            values.add(input.recordId);
        }
        if (input.fromTime != null) {
            where.append(input.lines ? " AND R.CREATEDTIME>=TO_DATE(?,'YYYY-MM-DD HH24:MI:SS')" : " AND A.CREATEDTIME>=TO_DATE(?,'YYYY-MM-DD HH24:MI:SS')");
            values.add(input.fromTime);
        }
        if (input.toTime != null) {
            where.append(input.lines ? " AND R.CREATEDTIME<=TO_DATE(?,'YYYY-MM-DD HH24:MI:SS')" : " AND A.CREATEDTIME<=TO_DATE(?,'YYYY-MM-DD HH24:MI:SS')");
            values.add(input.toTime);
        }
        String base = input.lines
                ? "SELECT L.* FROM T_CORE_AT_RECORD_LINE L JOIN T_CORE_AT_RECORD R ON R.ID=L.RECORDID WHERE " + where
                : "SELECT A.* FROM T_CORE_AT_RECORD A WHERE " + where;
        String countSql = "SELECT COUNT(1) FROM (" + base + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN FROM (" + base + ") Q) P WHERE RN>? AND RN<=? ORDER BY RN";
        try (Connection connection = dataSource.getConnection()) {
            int total;
            try (PreparedStatement count = connection.prepareStatement(countSql)) {
                bind(count, values);
                try (ResultSet rows = count.executeQuery()) { if (!rows.next()) throw new SQLException("audit count failed"); total = rows.getInt(1); }
            }
            List<Object> pageValues = new ArrayList<Object>(values);
            long start = ((long) input.pageNumber - 1L) * input.pageSize;
            pageValues.add(start); pageValues.add(start + input.pageSize);
            List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
            try (PreparedStatement select = connection.prepareStatement(pageSql)) {
                bind(select, pageValues);
                try (ResultSet result = select.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    while (result.next()) rows.add(row(result, metadata));
                }
            }
            Map<String, Object> page = new LinkedHashMap<String, Object>();
            page.put("columnTypeMap", new LinkedHashMap<String, Object>());
            page.put("first", input.pageNumber == 1);
            page.put("last", total == 0 || input.pageNumber >= pages(total, input.pageSize));
            page.put("numberOfElements", rows.size()); page.put("pageNumber", input.pageNumber);
            page.put("pageSize", input.pageSize); page.put("rows", rows); page.put("total", total);
            page.put("totalMap", new LinkedHashMap<String, Object>()); page.put("totalPages", pages(total, input.pageSize));
            return JSON.toJSONString(page, SerializerFeature.WriteMapNullValue);
        }
    }

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> row = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i);
            if (!"RN".equalsIgnoreCase(label)) row.put(camel(label), JdbcValue.normalize(result.getObject(i)));
        }
        return row;
    }

    private static void bind(PreparedStatement statement, List<Object> values) throws SQLException {
        for (int i = 0; i < values.size(); i++) statement.setObject(i + 1, values.get(i));
    }

    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }

    private static String camel(String name) {
        StringBuilder value = new StringBuilder(); boolean upper = false;
        for (char c : name.toLowerCase(Locale.ROOT).toCharArray()) {
            if (c == '_') { upper = true; continue; }
            value.append(upper ? Character.toUpperCase(c) : c); upper = false;
        }
        return value.toString();
    }

    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().header("Cache-Control", "no-store").body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.AUDIT.INVALID_QUERY\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.AUDIT.UNAVAILABLE\"}"); }

    static final class AuditQuery {
        final boolean lines; final String targetId; final Long recordId; final String fromTime; final String toTime; final int pageNumber; final int pageSize;
        private AuditQuery(boolean lines, String targetId, Long recordId, String fromTime, String toTime, int pageNumber, int pageSize) {
            this.lines = lines; this.targetId = targetId; this.recordId = recordId; this.fromTime = fromTime; this.toTime = toTime; this.pageNumber = pageNumber; this.pageSize = pageSize;
        }
        static AuditQuery from(QueryRequest query, boolean lines) {
            Map<String, Object> params = query.getMapperParameters();
            String target = first(value(params, "targetId_SEQ"), filterValue(query, "TARGETID", "SEQ", "EQ"));
            String record = first(value(params, "recordId_SEQ"), filterValue(query, "RECORDID", "SEQ", "EQ"));
            if (lines && record == null) throw new IllegalArgumentException("recordId is required for audit lines");
            Long recordId = null;
            if (record != null) try { recordId = Long.valueOf(record); } catch (NumberFormatException error) { throw new IllegalArgumentException("recordId is invalid"); }
            String from = first(value(params, "createdTime_TGOE"), filterValue(query, "CREATEDTIME", "TGOE"));
            String to = first(value(params, "createdTime_TLOE"), filterValue(query, "CREATEDTIME", "TLOE"));
            return new AuditQuery(lines, target, recordId, normalizeTime(from), normalizeTime(to), query.getPageNumber(), query.getPageSize());
        }
        private static String first(String direct, String filtered) { return direct == null ? filtered : direct; }
        private static String value(Map<String, Object> params, String key) {
            for (Map.Entry<String, Object> entry : params.entrySet()) if (key.equalsIgnoreCase(entry.getKey())) return entry.getValue() == null ? null : String.valueOf(entry.getValue()).trim();
            return null;
        }
        private static String filterValue(QueryRequest query, String field, String... patterns) {
            for (FilterParamPattern pattern : query.getFilterPatterns()) {
                boolean allowed = false;
                for (String name : patterns) if (name.equalsIgnoreCase(pattern.getMatchPattern())) { allowed = true; break; }
                if (!allowed) continue;
                for (Map.Entry<String, Object> entry : pattern.getMap().entrySet()) {
                    if (field.equalsIgnoreCase(entry.getKey()) && entry.getValue() != null) return String.valueOf(entry.getValue()).trim();
                }
            }
            return null;
        }
        private static String normalizeTime(String value) { if (value == null || value.length() == 0) return null; String text = value.replace('T',' '); return text.length() > 19 ? text.substring(0, 19) : text; }
    }
}
