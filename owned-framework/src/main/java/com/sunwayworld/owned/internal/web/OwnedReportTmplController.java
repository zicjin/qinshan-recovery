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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Owned read seam for the report-template GenericResource and its lines. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedReportTmplController {
    private static final String PREFIX = "/secure/emc/module/bp/info/report-tmpls";
    private static final String TEMPLATE_SOURCE = "SELECT T.* FROM T_EMC_REPORT_TMPL T WHERE T.CREATEDBYORGID=?";
    private static final String LINE_SOURCE = "SELECT A.*,C.ITEMNAME,D.TESTNAME,E.MONITORTMPLNAME "
            + "FROM T_EMC_REPORT_TMPL_LINE A LEFT JOIN T_EMC_TEST_METHOD B ON A.TESTID=B.ID "
            + "LEFT JOIN T_EMC_ITEM C ON A.ITEMID=C.ID LEFT JOIN T_EMC_TEST D ON B.TESTID=D.ID "
            + "LEFT JOIN T_EMC_MONITOR_TMPL E ON A.MONITORTMPLID=E.ID "
            + "WHERE EXISTS (SELECT 1 FROM T_EMC_REPORT_TMPL R "
            + "WHERE R.ID=A.REPORTTMPLID AND R.CREATEDBYORGID=?) AND A.REPORTTMPLID=?";

    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedReportTmplController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("report template dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource;
    }

    @GetMapping(path = PREFIX + "/info/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        return statusResponse(request);
    }

    @GetMapping(path = PREFIX + "/{id}/info/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detailStatus(HttpServletRequest request, @PathVariable("id") String id) {
        return statusResponse(request);
    }

    @PostMapping(path = {PREFIX + "/queries", PREFIX + "/queries/raw", PREFIX + "/queries/searchable"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try { OwnedPrincipal principal = require(request); return ok(selectPage(TEMPLATE_SOURCE, "Q.ORDERNO ASC, Q.ID ASC", principal.getOrgId(), new ArrayList<String>(), Page.parse(body))); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = PREFIX + "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request, @PathVariable("id") String id) {
        try {
            OwnedPrincipal principal = require(request);
            if (id == null || !id.matches("[0-9]+")) throw new IllegalArgumentException("report template id is invalid");
            String sql = TEMPLATE_SOURCE + " AND T.ID=?";
            List<String> values = new ArrayList<String>(); values.add(id);
            List<Map<String, Object>> rows = selectRows(sql, principal.getOrgId(), values);
            return ok(JSON.toJSONString(rows.isEmpty() ? new LinkedHashMap<String, Object>() : rows.get(0), SerializerFeature.WriteMapNullValue));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = PREFIX + "/{reportTmplId}/queries", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> lines(HttpServletRequest request, @PathVariable("reportTmplId") String reportTmplId,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            if (reportTmplId == null || !reportTmplId.matches("[0-9]+")) throw new IllegalArgumentException("report template id is invalid");
            List<String> values = new ArrayList<String>(); values.add(reportTmplId);
            return ok(selectPage(LINE_SOURCE, "Q.ORDERNO ASC, Q.ID ASC", principal.getOrgId(), values, Page.parse(body)));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private ResponseEntity<String> statusResponse(HttpServletRequest request) {
        try { require(request); return ok("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private String selectPage(String source, String order, String organization, List<String> values, Page page) throws SQLException {
        String countSql = "SELECT COUNT(1) FROM (" + source + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY " + order + ") RN_ FROM (" + source + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total; List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>(); Map<String, String> types = new LinkedHashMap<String, String>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement count = connection.prepareStatement(countSql)) { bind(count, organization, values, 1); try (ResultSet result = count.executeQuery()) { if (!result.next()) throw new SQLException("report template count unavailable"); total = result.getInt(1); } }
            try (PreparedStatement query = connection.prepareStatement(pageSql)) {
                int index = bind(query, organization, values, 1); query.setInt(index++, page.start()); query.setInt(index, page.end());
                try (ResultSet result = query.executeQuery()) { ResultSetMetaData metadata = result.getMetaData(); for (int i=1; i<=metadata.getColumnCount(); i++) { String label=metadata.getColumnLabel(i); if (!"RN_".equalsIgnoreCase(label)) types.put(label.toLowerCase(Locale.ROOT), type(metadata.getColumnType(i))); } while (result.next()) rows.add(row(result, metadata)); }
            }
        }
        Map<String,Object> envelope = new LinkedHashMap<String,Object>(); envelope.put("columnTypeMap", types); envelope.put("first", page.number == 1); envelope.put("last", total == 0 || page.number >= pages(total, page.size)); envelope.put("numberOfElements", rows.size()); envelope.put("pageNumber", page.number); envelope.put("pageSize", page.size); envelope.put("rows", rows); envelope.put("total", total); Map<String,Object> totals = new LinkedHashMap<String,Object>(); totals.put("qty_", total); envelope.put("totalMap", totals); envelope.put("totalPages", pages(total, page.size)); return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    private List<Map<String, Object>> selectRows(String sql, String organization, List<String> values) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql)) { bind(query, organization, values, 1); try (ResultSet result = query.executeQuery()) { ResultSetMetaData metadata = result.getMetaData(); while (result.next()) rows.add(row(result, metadata)); } }
        return rows;
    }

    private static int bind(PreparedStatement statement, String organization, List<String> values, int index) throws SQLException { statement.setString(index++, organization); for (String value : values) statement.setString(index++, value); return index; }
    private static Map<String,Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException { Map<String,Object> value = new LinkedHashMap<String,Object>(); for (int i=1; i<=metadata.getColumnCount(); i++) { String label=metadata.getColumnLabel(i); if ("RN_".equalsIgnoreCase(label)) continue; Object item=JdbcValue.normalize(result.getObject(i)); if (item != null) value.put(camel(label), item); } value.put("ext$", new LinkedHashMap<String,Object>()); return value; }
    private static final Map<String, String> FIELD_NAMES = fieldNames();
    private static Map<String, String> fieldNames() {
        Map<String, String> names = new LinkedHashMap<String, String>();
        for (String name : ("id tmplName remark orderNo activatedFlag activatedById activatedByName activatedTime "
                + "createdById createdByName createdTime createdByOrgId createdByOrgName reportTmplId reportName "
                + "monitorTmplId schemeName testId itemId itemAliasName itemName testName monitorTmplName").split(" "))
            names.put(name.toLowerCase(Locale.ROOT), name);
        return names;
    }
    private static String camel(String label) { String key = label.toLowerCase(Locale.ROOT); String declared = FIELD_NAMES.get(key); if (declared != null) return declared; StringBuilder value=new StringBuilder(); boolean upper=false; for (char c: key.toCharArray()) { if (c=='_') { upper=true; continue; } value.append(upper ? Character.toUpperCase(c) : c); upper=false; } return value.toString(); }
    private static String type(int type) { return type == Types.DATE || type == Types.TIMESTAMP || type == Types.TIMESTAMP_WITH_TIMEZONE ? "dateTime" : (type == Types.NUMERIC || type == Types.DECIMAL || type == Types.INTEGER || type == Types.BIGINT ? "number" : "string"); }
    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REPORT_TMPL.INVALID_QUERY\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REPORT_TMPL.UNAVAILABLE\"}"); }

    private static final class Page { final int number, size; Page(int number, int size) { this.number=number; this.size=size; } static Page parse(String body) { JSONObject root=body == null || body.trim().isEmpty() ? new JSONObject() : JSON.parseObject(body); JSONObject p=root == null ? null : root.getJSONObject("p"); int n=p == null ? 1 : Math.max(1, p.getIntValue("n")); int s=p == null ? 50 : Math.min(500, Math.max(1, p.getIntValue("s"))); return new Page(n,s); } int start() { return (number-1)*size; } int end() { return number*size; } }
}
