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
 * Source-owned read seam for the inherited standard-solution resource.
 *
 * <p>The supplier implementation joins the solution with its first line and
 * the preparing user.  This controller keeps those projections and all route
 * aliases in this Spring application; it is not a compatibility process.</p>
 */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedSolQueryController {
    private static final String PREFIX = "/secure/emc/module/bp/sols";
    private static final String BASE_SQL =
            "SELECT T.* FROM (SELECT S.*,U.USERNAME,M.SOLNAME AS VOLSOLNAME "
            + "FROM T_EMC_SOL S LEFT JOIN T_CORE_USER U ON S.PREPAREUSERID=U.ID "
            + "LEFT JOIN T_EMC_SOL_LINE M ON S.ID=M.SOLID "
            + "AND M.ID=(SELECT MIN(N.ID) FROM T_EMC_SOL_LINE N WHERE N.SOLID=S.ID)) T WHERE ";
    private static final String LINE_SQL = "SELECT L.* FROM T_EMC_SOL_LINE L WHERE ";
    private static final Map<String, String> TYPE_PROFILE = profile(
            "batchno=string;calibratedconcent=string;calibratedate=dateTime;calibratesolconcent=string;"
            + "calibratesolname=string;createdbyid=string;createdbyname=string;createdbyorgid=string;"
            + "createdbyorgname=string;createdtime=dateTime;id=number;methodid=number;methodname=string;"
            + "preparedconcent=string;preparedate=dateTime;prepareusername=string;reagentname=string;"
            + "revisionrule=string;rn_=number;solcategory=string;solname=string;solno=string;solsource=string;"
            + "status=string;validdate=dateTime;volsolname=string");
    private static final Map<String, String> LINE_TYPE_PROFILE = profile(
            "batchno=string;id=number;reagentmanufactor=string;remark=string;solcategory=string;"
            + "solid=number;solname=string;solsource=string;takeweightorvolume=string;weightvolume=string;rn_=number");

    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedSolQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("solution dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @PostMapping(path = {PREFIX + "/queries", PREFIX + "/queries/raw", PREFIX + "/queries/searchable",
            PREFIX + "/queries/roles/{roleId}"}, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            return ok(selectPage(principal.getOrgId(), Page.parse(body), BASE_SQL, TYPE_PROFILE, false));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {PREFIX + "/info/status", PREFIX + "/{id}/info/status"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { require(request); return ok("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {PREFIX + "/{id}", PREFIX + "/{id}/action/print"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request, @PathVariable("id") String id) {
        try {
            OwnedPrincipal principal = require(request);
            long numericId = numericId(id);
            String where = "T.CREATEDBYORGID=? AND T.ID=?";
            try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(BASE_SQL + where)) {
                statement.setString(1, principal.getOrgId());
                statement.setLong(2, numericId);
                try (ResultSet result = statement.executeQuery()) {
                    return ok(result.next() ? JSON.toJSONString(row(result, result.getMetaData()), SerializerFeature.WriteMapNullValue) : "{}");
                }
            }
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = PREFIX + "/{id}/columns/{column}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> column(HttpServletRequest request, @PathVariable("id") String id,
            @PathVariable("column") String column) {
        try {
            OwnedPrincipal principal = require(request);
            long numericId = numericId(id);
            String selected = column(column);
            try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(
                    "SELECT " + selected + " FROM T_EMC_SOL WHERE ID=? AND CREATEDBYORGID=?")) {
                statement.setLong(1, numericId); statement.setString(2, principal.getOrgId());
                try (ResultSet result = statement.executeQuery()) {
                    return ok(JSON.toJSONString(result.next() ? JdbcValue.normalize(result.getObject(1)) : null));
                }
            }
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = PREFIX + "/{id}/lines/queries", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> lines(HttpServletRequest request, @PathVariable("id") String id,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            long numericId = numericId(id);
            return ok(selectPage(principal.getOrgId(), Page.parse(body), LINE_SQL + "L.SOLID=?", LINE_TYPE_PROFILE, true, numericId));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = PREFIX + "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> lineDetail(HttpServletRequest request, @PathVariable("id") String id) {
        try {
            OwnedPrincipal principal = require(request);
            long numericId = numericId(id);
            try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(
                    LINE_SQL + "L.ID=? AND EXISTS (SELECT 1 FROM T_EMC_SOL S WHERE S.ID=L.SOLID AND S.CREATEDBYORGID=?)")) {
                statement.setLong(1, numericId); statement.setString(2, principal.getOrgId());
                try (ResultSet result = statement.executeQuery()) {
                    return ok(result.next() ? JSON.toJSONString(row(result, result.getMetaData()), SerializerFeature.WriteMapNullValue) : "{}");
                }
            }
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private String selectPage(String organization, Page page, String base, Map<String, String> profile,
            boolean line, Object... extra) throws SQLException {
        String countSql = "SELECT COUNT(1) FROM (" + base + (line ? "" : "T.CREATEDBYORGID=?") + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM (" + base
                + (line ? "" : "T.CREATEDBYORGID=?") + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total;
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(countSql)) {
                int next = 1; for (Object value : extra) statement.setObject(next++, value);
                if (!line) statement.setString(next, organization);
                try (ResultSet result = statement.executeQuery()) { if (!result.next()) throw new SQLException("solution count returned no row"); total = result.getInt(1); }
            }
            try (PreparedStatement statement = connection.prepareStatement(pageSql)) {
                int next = 1; for (Object value : extra) statement.setObject(next++, value);
                if (!line) statement.setString(next++, organization);
                statement.setInt(next++, page.start()); statement.setInt(next, page.end());
                try (ResultSet result = statement.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    while (result.next()) rows.add(row(result, metadata));
                }
            }
        }
        Map<String, Object> envelope = new LinkedHashMap<String, Object>();
        envelope.put("columnTypeMap", profile); envelope.put("first", page.number == 1);
        envelope.put("last", total == 0 || page.number >= pages(total, page.size)); envelope.put("numberOfElements", rows.size());
        envelope.put("pageNumber", page.number); envelope.put("pageSize", page.size); envelope.put("rows", rows);
        envelope.put("total", total); Map<String, Object> totals = new LinkedHashMap<String, Object>(); totals.put("qty_", total);
        envelope.put("totalMap", totals); envelope.put("totalPages", pages(total, page.size));
        return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        Map<String, Object> ext = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i); if ("RN_".equalsIgnoreCase(label)) continue;
            Object item = JdbcValue.normalize(result.getObject(i)); if (item == null) continue;
            String key = camel(label); if ("USERNAME".equalsIgnoreCase(label)) key = "prepareUserName";
            if ("VOLSOLNAME".equalsIgnoreCase(label)) key = "volSolName";
            value.put(key, item);
        }
        value.put("ext$", ext); return value;
    }

    private static String column(String value) {
        String key = value == null ? "" : value.toUpperCase(Locale.ROOT);
        String[] allowed = {"ID", "SOLNAME", "SOLNO", "SOLCATEGORY", "STATUS", "PREPAREDATE", "VALIDDATE", "CREATEDTIME", "CREATEDBYORGID"};
        for (String candidate : allowed) if (candidate.equals(key)) return candidate;
        throw new IllegalArgumentException("unsupported solution column");
    }

    private static String camel(String label) { StringBuilder result = new StringBuilder(); boolean upper = false; for (char value : label.toLowerCase(Locale.ROOT).toCharArray()) { if (value == '_') { upper = true; continue; } result.append(upper ? Character.toUpperCase(value) : value); upper = false; } return result.toString(); }
    private static long numericId(String value) { if (value == null || !value.matches("[0-9]{1,30}")) throw new IllegalArgumentException("solution id is invalid"); return Long.parseLong(value); }
    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }
    private static Map<String, String> profile(String encoded) { Map<String, String> result = new LinkedHashMap<String, String>(); for (String token : encoded.split(";")) { int index = token.indexOf('='); if (index > 0) result.put(token.substring(0, index), token.substring(index + 1)); } return Collections.unmodifiableMap(result); }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}"); }

    private static final class Page {
        final int number; final int size;
        Page(int number, int size) { this.number = number; this.size = size; }
        static Page parse(String body) { JSONObject root = body == null || body.trim().isEmpty() ? new JSONObject() : JSON.parseObject(body); JSONObject page = root == null ? null : root.getJSONObject("p"); int number = page == null ? 1 : Math.max(1, page.getIntValue("n")); int size = page == null ? 50 : Math.min(500, Math.max(1, page.getIntValue("s"))); return new Page(number, size); }
        int start() { return (number - 1) * size; }
        int end() { return number * size; }
    }
}
