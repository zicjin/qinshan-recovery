package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunwayworld.owned.internal.data.JdbcValue;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

/**
 * Source-owned read implementation for project-change orders and schemes.
 * Both resources share the inherited GenericResource wire envelope but use
 * different clone-derived Oracle tables and projections.
 */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedProjectChangeQueryController {
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedProjectChangeQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("project-change dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @PostMapping(path = {
            "/secure/emc/module/bp/edit/project-change/orders/queries",
            "/secure/emc/module/bp/edit/project-change/orders/queries/raw",
            "/secure/emc/module/bp/edit/project-change/orders/queries/searchable",
            "/secure/emc/module/bp/edit/project-change/proj-schemes/queries",
            "/secure/emc/module/bp/edit/project-change/proj-schemes/queries/raw",
            "/secure/emc/module/bp/edit/project-change/proj-schemes/queries/searchable"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, HttpServletResponse response,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            boolean scheme = request.getRequestURI().contains("/proj-schemes/");
            Query query;
            try { query = Query.parse(body); }
            catch (IllegalArgumentException invalid) { throw new MalformedProjectChangeQueryException(invalid); }
            return ok(selectPage(principal.getOrgId(), query, scheme, false));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (MalformedProjectChangeQueryException malformed) { return unexpected(response, request.getRequestURI()); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = "/secure/emc/module/bp/edit/project-change/proj-schemes/proj-orders/queries",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> projOrders(HttpServletRequest request, HttpServletResponse response,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            Query query;
            try { query = Query.parse(body); }
            catch (IllegalArgumentException invalid) { throw new MalformedProjectChangeQueryException(invalid); }
            return ok(selectPage(principal.getOrgId(), query, false, true));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (MalformedProjectChangeQueryException malformed) { return unexpected(response, request.getRequestURI()); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {
            "/secure/emc/module/bp/edit/project-change/orders/info/status",
            "/secure/emc/module/bp/edit/project-change/proj-schemes/info/status"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { require(request); return ok("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    public static String sourceTable(boolean scheme) {
        return scheme ? "T_EMC_PROJ_SCHEME_CHANGE" : "T_EMC_ORDER_CHANGE";
    }

    public static String projOrdersSourceTable() {
        return "T_EMC_PROJ_ORDER_CHANGE";
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private String selectPage(String organization, Query query, boolean scheme, boolean projOrders) throws SQLException {
        SqlSpec spec = SqlSpec.forQuery(organization, scheme, projOrders);
        String countSql = "SELECT COUNT(1) FROM (" + spec.sql + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM ("
                + spec.sql + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total;
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, String> types = new LinkedHashMap<String, String>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement count = connection.prepareStatement(countSql)) {
                spec.bind(count);
                try (ResultSet result = count.executeQuery()) {
                    if (!result.next()) throw new SQLException("project-change count returned no row");
                    total = result.getInt(1);
                }
            }
            try (PreparedStatement page = connection.prepareStatement(pageSql)) {
                int next = spec.bind(page);
                page.setInt(next++, query.page.start());
                page.setInt(next, query.page.end());
                try (ResultSet result = page.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    for (int i = 1; i <= metadata.getColumnCount(); i++) {
                        String label = metadata.getColumnLabel(i);
                        if (label != null && !"RN_".equalsIgnoreCase(label)) {
                            types.put(label.toLowerCase(Locale.ROOT), type(metadata.getColumnType(i)));
                        }
                    }
                    while (result.next()) rows.add(row(result, metadata));
                }
            }
        }
        Map<String, Object> envelope = new LinkedHashMap<String, Object>();
        envelope.put("columnTypeMap", types);
        envelope.put("first", query.page.number == 1);
        envelope.put("last", total == 0 || query.page.number >= pages(total, query.page.size));
        envelope.put("numberOfElements", rows.size());
        envelope.put("pageNumber", query.page.number);
        envelope.put("pageSize", query.page.size);
        envelope.put("rows", rows);
        envelope.put("total", total);
        Map<String, Object> totals = new LinkedHashMap<String, Object>();
        totals.put("qty_", total);
        envelope.put("totalMap", totals);
        envelope.put("totalPages", pages(total, query.page.size));
        return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i);
            if (!"RN_".equalsIgnoreCase(label)) value.put(camel(label), JdbcValue.normalize(result.getObject(i)));
        }
        return value;
    }

    private static String camel(String label) {
        StringBuilder result = new StringBuilder();
        boolean upper = false;
        for (char value : label.toLowerCase(Locale.ROOT).toCharArray()) {
            if (value == '_') { upper = true; continue; }
            result.append(upper ? Character.toUpperCase(value) : value);
            upper = false;
        }
        return result.toString();
    }

    private static String type(int sqlType) {
        switch (sqlType) {
            case java.sql.Types.DATE:
            case java.sql.Types.TIMESTAMP:
            case java.sql.Types.TIMESTAMP_WITH_TIMEZONE: return "dateTime";
            case java.sql.Types.NUMERIC:
            case java.sql.Types.DECIMAL:
            case java.sql.Types.INTEGER:
            case java.sql.Types.BIGINT: return "number";
            default: return "string";
        }
    }

    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().header("Cache-Control", "no-store").body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.PROJECT_CHANGE.INVALID_QUERY\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.PROJECT_CHANGE.UNAVAILABLE\"}"); }
    private static ResponseEntity<String> unexpected(HttpServletResponse response, String path) {
        if (response != null) {
            try {
                response.resetBuffer();
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.getWriter().write(unexpectedBody(path));
                response.getWriter().flush();
                return null;
            } catch (IOException ignored) { /* fall through to a normal response */ }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(unexpectedBody(path));
    }
    private static String unexpectedBody(String path) {
        String safePath = path == null ? "" : path.replace("\\", "\\\\").replace("\"", "\\\"");
        return "{\"error\":\"Internal Server Error\",\"message\":\"GIKAM.EXCEPTION.UNEXPECTED\",\"path\":\""
                + safePath + "\",\"stacktrace\":\"owned-project-change-malformed-query\",\"status\":500,\"timestamp\":\""
                + new java.util.Date().toString() + "\"}";
    }
    private static final class MalformedProjectChangeQueryException extends IllegalArgumentException {
        MalformedProjectChangeQueryException(Throwable cause) { super("malformed project-change query", cause); }
    }

    private static final class Query {
        final Page page;
        Query(Page page) { this.page = page; }
        static Query parse(String body) {
            JSONObject root = body == null || body.trim().isEmpty() ? new JSONObject() : JSON.parseObject(body);
            JSONObject p = root == null ? null : root.getJSONObject("p");
            int number = p == null ? 1 : Math.max(1, p.getIntValue("n"));
            int size = p == null ? 50 : Math.min(500, Math.max(1, p.getIntValue("s")));
            return new Query(new Page(number, size));
        }
    }

    private static final class Page {
        final int number; final int size;
        Page(int number, int size) { this.number = number; this.size = size; }
        int start() { return (number - 1) * size; }
        int end() { return number * size; }
    }

    private static final class SqlSpec {
        final String sql; final List<String> values;
        SqlSpec(String sql, List<String> values) { this.sql = sql; this.values = values; }
        static SqlSpec forQuery(String organization, boolean scheme, boolean projOrders) {
            String source;
            if (projOrders) {
                source = "SELECT T.* FROM (SELECT A.*, B.METHODNAME, B.TESTMETHODBASIS, "
                        + "B.MAXVERSIONID AS METHODVERSIONID, D.STDNAME, E.INDEXNAME, E.INDEX1, E.INDEX2, E.INDEX3, "
                        + "F.TESTMETHODID, G.TESTNAME FROM T_EMC_PROJ_ORDER_CHANGE A "
                        + "LEFT JOIN T_EMC_METHOD B ON A.METHODID=B.ID "
                        + "LEFT JOIN T_EMC_INDEX_CATEGORY D ON A.INDEXCATEGORYID=D.ID "
                        + "LEFT JOIN T_EMC_ORDER_INDEX E ON A.INDEXID=E.ID "
                        + "LEFT JOIN T_EMC_ITEM_GROUP F ON A.ITEMGROUPID=F.ID "
                        + "LEFT JOIN T_EMC_TEST G ON A.TESTID=G.ID) T";
            } else if (scheme) {
                source = "SELECT T.* FROM (SELECT A.*, C.ORGNAME, D.MONITORTMPLNAME, D.MONITORTMPLGROUPID, "
                        + "D.MONITORPOINTCATEGORYID AS POINTCATEGORY FROM T_EMC_PROJ_SCHEME_CHANGE A "
                        + "LEFT JOIN T_CORE_ORG C ON A.SAMPLEORGID=C.ID "
                        + "LEFT JOIN T_EMC_MONITOR_TMPL D ON A.MONITORTMPLID=D.ID) T";
            } else {
                source = "SELECT T.* FROM (SELECT O.*, F.PROCESSSTATUS FOLDERSTATUS, F.MONITORPOINTNAME, "
                        + "F.MONITORPOINTNO, P.PROJNO, P.PROJNAME, F.FOLDERNO, F.SAMPLEUSERID, F.SAMPLEUSERNAME "
                        + "FROM T_EMC_ORDER_CHANGE O LEFT JOIN T_EMC_FOLDER_CHANGE F ON F.ID=O.FOLDERID "
                        + "LEFT JOIN T_EMC_PROJECT_CHANGE P ON P.ID=O.PROJID) T";
            }
            List<String> values = new ArrayList<String>();
            values.add((organization == null ? "__NO_ORG__" : organization) + "%");
            return new SqlSpec(source + " WHERE T.CREATEDBYORGID LIKE ?", values);
        }
        int bind(PreparedStatement statement) throws SQLException {
            int index = 1; for (String value : values) statement.setString(index++, value); return index;
        }
    }
}
