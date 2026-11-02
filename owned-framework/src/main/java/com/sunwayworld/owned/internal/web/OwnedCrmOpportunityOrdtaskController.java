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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Owned read seam for the CRM opportunity order-task GenericResource. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedCrmOpportunityOrdtaskController {
    private static final String PREFIX = "/secure/emc/module/mdm/crm/ordtasks";
    /* TESTNO and TESTMETHODS are Oracle CLOBs in the clone.  Project them to
       VARCHAR2 before CASE/JSON handling; comparing or returning the CLOB
       aliases directly raises ORA-00932 in the inherited query family. */
    private static final String PROJECTION = "SELECT T.*, CASE WHEN T.SCHEMENAME = 'default' THEN DBMS_LOB.SUBSTR(T.TESTNO,4000,1) ELSE T.SCHEMENAME END AS GROUPFIELDS, DBMS_LOB.SUBSTR(T.TESTNO,4000,1) AS TESTNAME, DBMS_LOB.SUBSTR(T.TESTMETHODS,4000,1) AS METHODNAME, T.TESTCODE AS TESTID FROM T_CRM_OPPORTUNITY_ORDTASK T WHERE T.CREATEDBYORGID=?";
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedCrmOpportunityOrdtaskController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("CRM opportunity order-task dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @GetMapping(path = PREFIX + "/info/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { require(request); return ok("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = {PREFIX + "/queries", PREFIX + "/queries/raw", PREFIX + "/queries/searchable"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try { OwnedPrincipal principal = require(request); return ok(selectPage(principal.getOrgId(), Query.parse(body))); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.PERSONNEL_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private String selectPage(String organization, Query query) throws SQLException {
        String base = PROJECTION + (query.ordersId == null ? "" : " AND T.ORDERSID=?");
        String countSql = "SELECT COUNT(1) FROM (" + base + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*, ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM (" + base + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total;
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, String> types = new LinkedHashMap<String, String>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement count = connection.prepareStatement(countSql)) {
                int index = bind(count, organization, query.ordersId, 1);
                if (index < 1) throw new SQLException("invalid CRM query bind");
                try (ResultSet result = count.executeQuery()) { if (!result.next()) throw new SQLException("CRM opportunity order-task count unavailable"); total = result.getInt(1); }
            }
            try (PreparedStatement page = connection.prepareStatement(pageSql)) {
                int index = bind(page, organization, query.ordersId, 1);
                page.setInt(index++, query.start()); page.setInt(index, query.end());
                try (ResultSet result = page.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    for (int i = 1; i <= metadata.getColumnCount(); i++) types.put(metadata.getColumnLabel(i).toLowerCase(Locale.ROOT), type(metadata.getColumnType(i)));
                    while (result.next()) rows.add(row(result, metadata));
                }
            }
        }
        Map<String, Object> envelope = new LinkedHashMap<String, Object>();
        envelope.put("columnTypeMap", types); envelope.put("first", query.number == 1); envelope.put("last", total == 0 || query.number >= pages(total, query.size)); envelope.put("numberOfElements", rows.size()); envelope.put("pageNumber", query.number); envelope.put("pageSize", query.size); envelope.put("rows", rows); envelope.put("total", total);
        Map<String, Object> totalMap = new LinkedHashMap<String, Object>(); totalMap.put("qty_", total); envelope.put("totalMap", totalMap); envelope.put("totalPages", pages(total, query.size));
        return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    private static int bind(PreparedStatement statement, String organization, String ordersId, int index) throws SQLException { statement.setString(index++, organization); if (ordersId != null) statement.setString(index++, ordersId); return index; }
    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>(); Map<String, Object> ext = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) { String label = metadata.getColumnLabel(i); if ("RN_".equalsIgnoreCase(label)) continue; Object item = JdbcValue.normalize(result.getObject(i)); if (item == null) continue; if ("GROUPFIELDS".equalsIgnoreCase(label) || "TESTNAME".equalsIgnoreCase(label) || "METHODNAME".equalsIgnoreCase(label) || "TESTID".equalsIgnoreCase(label)) ext.put(label.toLowerCase(Locale.ROOT), item); else value.put(camel(label), item); }
        if (!ext.isEmpty()) value.put("ext$", ext); return value;
    }
    private static String camel(String label) { StringBuilder value = new StringBuilder(); boolean upper = false; for (char c : label.toLowerCase(Locale.ROOT).toCharArray()) { if (c == '_') { upper = true; continue; } value.append(upper ? Character.toUpperCase(c) : c); upper = false; } return value.toString(); }
    private static String type(int sqlType) { if (sqlType == Types.DATE || sqlType == Types.TIMESTAMP || sqlType == Types.TIMESTAMP_WITH_TIMEZONE) return "dateTime"; if (sqlType == Types.NUMERIC || sqlType == Types.DECIMAL || sqlType == Types.INTEGER || sqlType == Types.BIGINT) return "number"; return "string"; }
    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}"); }

    private static final class Query {
        final int number, size; final String ordersId;
        Query(int number, int size, String ordersId) { this.number = number; this.size = size; this.ordersId = ordersId; }
        int start() { return (number - 1) * size; } int end() { return number * size; }
        static Query parse(String body) {
            JSONObject root = body == null || body.trim().isEmpty() ? new JSONObject() : JSON.parseObject(body); JSONObject params = root == null ? null : root.getJSONObject("p");
            int number = params == null ? 1 : Math.max(1, params.getIntValue("n")); int size = params == null ? 50 : Math.min(500, Math.max(1, params.getIntValue("s")));
            String ordersId = text(root, "ordersId"); if (ordersId == null) ordersId = text(params, "ordersId"); JSONObject filter = root == null ? null : root.getJSONObject("f"); if (ordersId == null) ordersId = text(filter, "ordersId"); return new Query(number, size, ordersId);
        }
        private static String text(JSONObject object, String key) { if (object == null || object.get(key) == null) return null; String value = String.valueOf(object.get(key)).trim(); return value.isEmpty() ? null : value; }
    }
}
