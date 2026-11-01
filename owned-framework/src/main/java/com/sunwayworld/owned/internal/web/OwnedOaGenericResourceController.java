package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
 * Source-owned read seam for the OA GenericResource family.  The seven OA
 * resources share the same table projection, identity scope and pagination
 * behavior; they therefore use one Controller in the existing Spring
 * application rather than seven compatibility processes.
 */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedOaGenericResourceController {
    private static final Map<String, String> TABLES = tables();
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedOaGenericResourceController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("OA dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @PostMapping(path = {
            "/secure/emc/module/mdm/oa-areas/queries", "/secure/emc/module/mdm/oa-areas/queries/raw", "/secure/emc/module/mdm/oa-areas/queries/searchable",
            "/secure/emc/module/mdm/oa-banks/queries", "/secure/emc/module/mdm/oa-banks/queries/raw", "/secure/emc/module/mdm/oa-banks/queries/searchable",
            "/secure/emc/module/mdm/oa-clients/queries", "/secure/emc/module/mdm/oa-clients/queries/raw", "/secure/emc/module/mdm/oa-clients/queries/searchable",
            "/secure/emc/module/mdm/oa-equipts/queries", "/secure/emc/module/mdm/oa-equipts/queries/raw", "/secure/emc/module/mdm/oa-equipts/queries/searchable",
            "/secure/emc/module/mdm/oa-new-clients/queries", "/secure/emc/module/mdm/oa-new-clients/queries/raw", "/secure/emc/module/mdm/oa-new-clients/queries/searchable",
            "/secure/emc/module/mdm/oa-orgs/queries", "/secure/emc/module/mdm/oa-orgs/queries/raw", "/secure/emc/module/mdm/oa-orgs/queries/searchable",
            "/secure/emc/module/mdm/oa-users/queries", "/secure/emc/module/mdm/oa-users/queries/raw", "/secure/emc/module/mdm/oa-users/queries/searchable"
            }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try {
            String resource = resource(request);
            String organization = boundary.currentPrincipal(request.getSession(false)).getOrgId();
            boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.PERSONNEL_READ);
            return ok(selectPage(TABLES.get(resource), organization, Page.parse(body)));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {
            "/secure/emc/module/mdm/oa-areas/info/status", "/secure/emc/module/mdm/oa-banks/info/status",
            "/secure/emc/module/mdm/oa-clients/info/status", "/secure/emc/module/mdm/oa-equipts/info/status",
            "/secure/emc/module/mdm/oa-new-clients/info/status", "/secure/emc/module/mdm/oa-orgs/info/status",
            "/secure/emc/module/mdm/oa-users/info/status"
            }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try {
            boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.PERSONNEL_READ);
            return ok("{\"ext$\":{}}");
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {
            "/secure/emc/module/mdm/oa-areas/{id}", "/secure/emc/module/mdm/oa-banks/{id}",
            "/secure/emc/module/mdm/oa-clients/{id}", "/secure/emc/module/mdm/oa-equipts/{id}",
            "/secure/emc/module/mdm/oa-new-clients/{id}", "/secure/emc/module/mdm/oa-orgs/{id}",
            "/secure/emc/module/mdm/oa-users/{id}"
            }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request, @PathVariable("id") String id) {
        try {
            String resource = resource(request);
            boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.PERSONNEL_READ);
            if (id == null || !id.matches("[0-9]{1,30}")) throw new IllegalArgumentException("OA id is invalid");
            try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement("SELECT T.* FROM " + TABLES.get(resource) + " T WHERE T.ID=?")) {
                query.setLong(1, Long.parseLong(id));
                try (ResultSet rows = query.executeQuery()) {
                    return ok(JSON.toJSONString(rows.next() ? row(rows, rows.getMetaData()) : Collections.emptyMap()));
                }
            }
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private String resource(HttpServletRequest request) {
        String path = request.getRequestURI();
        String marker = "/secure/emc/module/mdm/";
        if (path == null || !path.startsWith(marker)) throw new IllegalArgumentException("OA resource path is invalid");
        int start = marker.length();
        int slash = path.indexOf('/', start);
        if (slash <= start) throw new IllegalArgumentException("OA resource path is invalid");
        String value = path.substring(start, slash);
        if (!TABLES.containsKey(value)) throw new IllegalArgumentException("unsupported OA resource");
        return value;
    }

    private String selectPage(String table, String organization, Page page) throws SQLException {
        String base = "SELECT T.* FROM " + table + " T WHERE T.CREATEDBYORGID LIKE ?";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM (" + base + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        try (Connection connection = dataSource.getConnection()) {
            int total;
            try (PreparedStatement count = connection.prepareStatement("SELECT COUNT(1) FROM (" + base + ") Q")) {
                count.setString(1, (organization == null ? "__NO_ORG__" : organization) + "%");
                try (ResultSet rows = count.executeQuery()) { rows.next(); total = rows.getInt(1); }
            }
            List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
            Map<String, String> types = new LinkedHashMap<String, String>();
            try (PreparedStatement query = connection.prepareStatement(pageSql)) {
                query.setString(1, (organization == null ? "__NO_ORG__" : organization) + "%");
                query.setInt(2, page.start()); query.setInt(3, page.end());
                try (ResultSet rows = query.executeQuery()) {
                    ResultSetMetaData meta = rows.getMetaData();
                    for (int i = 1; i <= meta.getColumnCount(); i++) if (!"RN_".equalsIgnoreCase(meta.getColumnLabel(i))) types.put(meta.getColumnLabel(i).toLowerCase(Locale.ROOT), type(meta.getColumnType(i)));
                    while (rows.next()) values.add(row(rows, meta));
                }
            }
            Map<String, Object> envelope = new LinkedHashMap<String, Object>();
            envelope.put("columnTypeMap", types); envelope.put("first", page.number == 1);
            envelope.put("last", total == 0 || page.number >= pages(total, page.size));
            envelope.put("numberOfElements", values.size()); envelope.put("pageNumber", page.number);
            envelope.put("pageSize", page.size); envelope.put("rows", values); envelope.put("total", total);
            Map<String, Object> totalMap = new LinkedHashMap<String, Object>(); totalMap.put("qty_", total);
            envelope.put("totalMap", totalMap); envelope.put("totalPages", pages(total, page.size));
            return JSON.toJSONString(envelope);
        }
    }

    private static Map<String, Object> row(ResultSet rows, ResultSetMetaData meta) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            String label = meta.getColumnLabel(i); if ("RN_".equalsIgnoreCase(label)) continue;
            Object item = JdbcValue.normalize(rows.getObject(i)); if (item != null) value.put(camel(label), item);
        }
        return value;
    }
    private static String camel(String name) { StringBuilder value = new StringBuilder(); boolean upper = false; for (char c : name.toLowerCase(Locale.ROOT).toCharArray()) { if (c == '_') { upper = true; continue; } value.append(upper ? Character.toUpperCase(c) : c); upper = false; } return value.toString(); }
    private static String type(int type) { if (type == Types.DATE || type == Types.TIMESTAMP || type == Types.TIMESTAMP_WITH_TIMEZONE) return "dateTime"; if (type == Types.NUMERIC || type == Types.DECIMAL || type == Types.INTEGER || type == Types.BIGINT) return "number"; return "string"; }
    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }
    private static Map<String, String> tables() { Map<String, String> value = new LinkedHashMap<String, String>(); value.put("oa-areas", "T_EMC_OA_AREA"); value.put("oa-banks", "T_EMC_OA_BANK"); value.put("oa-clients", "T_EMC_OA_CLIENT"); value.put("oa-equipts", "T_EMC_OA_EQUIPT"); value.put("oa-new-clients", "T_EMC_OA_NEW_CLIENT"); value.put("oa-orgs", "T_EMC_OA_ORG"); value.put("oa-users", "T_EMC_OA_USER"); return Collections.unmodifiableMap(value); }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}"); }
    private static final class Page { final int number, size; Page(int number, int size) { this.number = number; this.size = size; } static Page parse(String body) { JSONObject root = body == null || body.trim().isEmpty() ? new JSONObject() : JSON.parseObject(body); JSONObject p = root == null ? null : root.getJSONObject("p"); int n = p == null ? 1 : Math.max(1, p.getIntValue("n")); int s = p == null ? 50 : Math.min(500, Math.max(1, p.getIntValue("s"))); return new Page(n, s); } int start() { return (number - 1) * size; } int end() { return number * size; } }
}
