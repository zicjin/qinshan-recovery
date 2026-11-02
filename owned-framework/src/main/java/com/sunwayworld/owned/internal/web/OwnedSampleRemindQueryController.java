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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

/** Source-owned read seam for sample reminders and their sample-specific checks. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedSampleRemindQueryController {
    private static final String PREFIX = "/secure/emc/module/bp/sample/sample-reminds";
    private static final String BASE_SQL = "SELECT T.* FROM T_EMC_SAMPLE_REMIND T WHERE T.CREATEDBYORGID=?";
    private static final Map<String, String> TYPE_PROFILE = profile("createdbyid=string;createdbyname=string;createdbyorgid=string;createdbyorgname=string;createdtime=dateTime;id=number;remind=string;rn_=number");
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedSampleRemindQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("sample-remind dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource;
    }

    @PostMapping(path = {PREFIX + "/queries", PREFIX + "/queries/raw", PREFIX + "/queries/searchable", PREFIX + "/queries/roles/{roleId}"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try { OwnedPrincipal principal = require(request); return ok(selectPage(principal.getOrgId(), Page.parse(body))); }
        catch (AccessDeniedException denied) { return forbidden(); } catch (IllegalArgumentException invalid) { return badRequest(); } catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = PREFIX + "/remind/queries", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> remind(HttpServletRequest request, @RequestBody(required = false) String body) {
        try { OwnedPrincipal principal = require(request); Long sampleId = sampleId(body); return ok(selectRemind(principal.getOrgId(), sampleId)); }
        catch (AccessDeniedException denied) { return forbidden(); } catch (IllegalArgumentException invalid) { return badRequest(); } catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {PREFIX + "/info/status", PREFIX + "/{id}/info/status"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) { try { require(request); return ok("{\"ext$\":{}}"); } catch (AccessDeniedException denied) { return forbidden(); } catch (SQLException failure) { return unavailable(); } }

    @GetMapping(path = {PREFIX + "/{id}", PREFIX + "/{id}/action/print"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request, @PathVariable("id") String id) {
        try { OwnedPrincipal principal = require(request); long numericId = numericId(id); try (Connection c = dataSource.getConnection(); PreparedStatement q = c.prepareStatement(BASE_SQL + " AND T.ID=?")) { q.setString(1, principal.getOrgId()); q.setLong(2, numericId); try (ResultSet r = q.executeQuery()) { return ok(r.next() ? JSON.toJSONString(row(r, r.getMetaData()), SerializerFeature.WriteMapNullValue) : "{}"); } } }
        catch (AccessDeniedException denied) { return forbidden(); } catch (IllegalArgumentException invalid) { return badRequest(); } catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = PREFIX + "/{id}/columns/{column}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> column(HttpServletRequest request, @PathVariable("id") String id, @PathVariable("column") String column) {
        try { OwnedPrincipal principal = require(request); long numericId = numericId(id); String selected = column(column); try (Connection c = dataSource.getConnection(); PreparedStatement q = c.prepareStatement("SELECT " + selected + " FROM T_EMC_SAMPLE_REMIND WHERE ID=? AND CREATEDBYORGID=?")) { q.setLong(1, numericId); q.setString(2, principal.getOrgId()); try (ResultSet r = q.executeQuery()) { return ok(JSON.toJSONString(r.next() ? JdbcValue.normalize(r.getObject(1)) : null)); } } }
        catch (AccessDeniedException denied) { return forbidden(); } catch (IllegalArgumentException invalid) { return badRequest(); } catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException { boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ); return boundary.currentPrincipal(request.getSession(false)); }

    private String selectPage(String organization, Page page) throws SQLException {
        String countSql = "SELECT COUNT(1) FROM (" + BASE_SQL + ") Q"; String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM (" + BASE_SQL + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total; List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        try (Connection c = dataSource.getConnection()) { try (PreparedStatement q = c.prepareStatement(countSql)) { q.setString(1, organization); try (ResultSet r = q.executeQuery()) { if (!r.next()) throw new SQLException("sample-remind count returned no row"); total = r.getInt(1); } } try (PreparedStatement q = c.prepareStatement(pageSql)) { q.setString(1, organization); q.setInt(2, page.start()); q.setInt(3, page.end()); try (ResultSet r = q.executeQuery()) { ResultSetMetaData m = r.getMetaData(); while (r.next()) rows.add(row(r, m)); } } }
        return envelope(total, page, rows, TYPE_PROFILE);
    }

    private String selectRemind(String organization, Long sampleId) throws SQLException {
        boolean templateMissing = false; boolean orderMissing = false;
        try (Connection c = dataSource.getConnection()) {
            try (PreparedStatement q = c.prepareStatement("SELECT REPORTCONFIGLINEID FROM T_EMC_SAMPLE WHERE ID=? AND CREATEDBYORGID=?")) { q.setLong(1, sampleId); q.setString(2, organization); try (ResultSet r = q.executeQuery()) { if (!r.next()) throw new AccessDeniedException("sample not found"); templateMissing = r.getObject(1) == null; } }
            try (PreparedStatement q = c.prepareStatement("SELECT 1 FROM T_EMC_ORDER WHERE SAMPLEID=? AND (SAMPLEBEGINDATE IS NULL OR SAMPLEBEGINTIME IS NULL)")) { q.setLong(1, sampleId); try (ResultSet r = q.executeQuery()) { orderMissing = r.next(); } }
            List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>(); try (PreparedStatement q = c.prepareStatement(BASE_SQL)) { q.setString(1, organization); try (ResultSet r = q.executeQuery()) { ResultSetMetaData m = r.getMetaData(); while (r.next()) { Map<String, Object> value = row(r, m); String remind = String.valueOf(value.get("remind")); Map<String, Object> ext = new LinkedHashMap<String, Object>(); ext.put("matter", "采样单模板".equals(remind) || "采样开始时间".equals(remind) ? "有要求" : "无要求"); boolean abnormal = ("采样单模板".equals(remind) && templateMissing) || ("采样开始时间".equals(remind) && orderMissing); ext.put("result", abnormal ? "异常" : "√"); ext.put("explain", abnormal ? ("采样单模板".equals(remind) ? "未选择" : "部分未选择") : ""); value.put("ext$", ext); rows.add(value); } } } rows.sort(new Comparator<Map<String, Object>>() { public int compare(Map<String, Object> a, Map<String, Object> b) { return String.valueOf(b.get("result")).compareTo(String.valueOf(a.get("result"))); } }); return envelope(rows.size(), new Page(1, rows.size() == 0 ? 1 : rows.size()), rows, TYPE_PROFILE);
        }
    }

    private static Long sampleId(String body) { if (body == null || body.trim().isEmpty()) throw new IllegalArgumentException("sampleId is required"); JSONObject root; try { root = JSON.parseObject(body); } catch (RuntimeException error) { throw new IllegalArgumentException("invalid sample-remind query JSON", error); } Object value = root.get("sampleId"); JSONObject f = root.getJSONObject("f"); if (value == null && f != null) value = f.get("sampleId_SEQ"); if (value == null) { JSONObject p = root.getJSONObject("p"); if (p != null) value = p.get("sampleId"); } if (value == null) throw new IllegalArgumentException("sampleId is required"); try { return Long.valueOf(String.valueOf(value)); } catch (NumberFormatException error) { throw new IllegalArgumentException("invalid sampleId"); } }
    private static Map<String, Object> row(ResultSet r, ResultSetMetaData m) throws SQLException { Map<String, Object> value = new LinkedHashMap<String, Object>(); Map<String, Object> ext = new LinkedHashMap<String, Object>(); for (int i = 1; i <= m.getColumnCount(); i++) { String label = m.getColumnLabel(i); if ("RN_".equalsIgnoreCase(label)) continue; Object item = JdbcValue.normalize(r.getObject(i)); if (item == null) continue; value.put(camel(label), item); } value.put("ext$", ext); return value; }
    private static String envelope(int total, Page page, List<Map<String, Object>> rows, Map<String, String> types) { Map<String, Object> out = new LinkedHashMap<String, Object>(); out.put("columnTypeMap", types); out.put("first", page.number == 1); out.put("last", total == 0 || page.number >= pages(total, page.size)); out.put("numberOfElements", rows.size()); out.put("pageNumber", page.number); out.put("pageSize", page.size); out.put("rows", rows); out.put("total", total); Map<String, Object> totals = new LinkedHashMap<String, Object>(); totals.put("qty_", total); out.put("totalMap", totals); out.put("totalPages", pages(total, page.size)); return JSON.toJSONString(out, SerializerFeature.WriteMapNullValue); }
    private static String column(String value) { String key = value == null ? "" : value.toUpperCase(Locale.ROOT); if ("ID".equals(key) || "REMIND".equals(key) || "CREATEDTIME".equals(key) || "CREATEDBYORGID".equals(key)) return key; throw new IllegalArgumentException("unsupported sample-remind column"); }
    private static String camel(String label) { StringBuilder result = new StringBuilder(); boolean upper = false; for (char value : label.toLowerCase(Locale.ROOT).toCharArray()) { if (value == '_') { upper = true; continue; } result.append(upper ? Character.toUpperCase(value) : value); upper = false; } return result.toString(); }
    private static long numericId(String value) { if (value == null || !value.matches("[0-9]{1,30}")) throw new IllegalArgumentException("sample-remind id is invalid"); return Long.parseLong(value); }
    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }
    private static Map<String, String> profile(String encoded) { Map<String, String> result = new LinkedHashMap<String, String>(); for (String token : encoded.split(";")) { int index = token.indexOf('='); if (index > 0) result.put(token.substring(0, index), token.substring(index + 1)); } return Collections.unmodifiableMap(result); }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}"); }
    private static final class Page { final int number; final int size; Page(int number, int size) { this.number = number; this.size = size; } static Page parse(String body) { JSONObject root = body == null || body.trim().isEmpty() ? new JSONObject() : JSON.parseObject(body); JSONObject page = root == null ? null : root.getJSONObject("p"); int number = page == null ? 1 : Math.max(1, page.getIntValue("n")); int size = page == null ? 50 : Math.min(500, Math.max(1, page.getIntValue("s"))); return new Page(number, size); } int start() { return (number - 1) * size; } int end() { return number * size; } }
}
