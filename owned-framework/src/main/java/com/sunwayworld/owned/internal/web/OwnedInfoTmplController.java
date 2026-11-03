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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Owned read seams for the info-template GenericResource and child queries. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedInfoTmplController {
    private static final String PREFIX = "/secure/emc/module/bp/info/info-tmpls";
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedInfoTmplController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("info template dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @PostMapping(path = {PREFIX + "/queries", PREFIX + "/queries/raw", PREFIX + "/queries/searchable"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> templateQuery(HttpServletRequest request, HttpServletResponse response,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            Page page;
            try { page = Page.parse(body); }
            catch (IllegalArgumentException invalid) { throw new MalformedInfoTmplQueryException(invalid); }
            return ok(selectPage("SELECT T.* FROM (SELECT TMPL.*, ORG.ORGNAME USEDORGNAME, CAST(NULL AS NUMBER) EXCELID, CAST(NULL AS VARCHAR2(4000)) EXCELNAME, '' POINT FROM T_EMC_INFO_TMPL TMPL LEFT JOIN T_CORE_ORG ORG ON ORG.ID=TMPL.USEDORGID) T WHERE T.CREATEDBYORGID=?", "Q.ID DESC", principal.getOrgId(), new ArrayList<String>(), page));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (MalformedInfoTmplQueryException malformed) { return unexpected(response, request.getRequestURI()); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = PREFIX + "/{tmplId}/fields/queries", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> fieldsByTemplate(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("tmplId") String tmplId, @RequestBody(required = false) String body) {
        return fields(request, response, tmplId, body);
    }

    @GetMapping(path = PREFIX + "/fields/queries/raw", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> fieldsRaw(HttpServletRequest request, HttpServletResponse response) {
        return fields(request, response, null, null);
    }

    @GetMapping(path = PREFIX + "/fields/queries/selectable", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> fieldsSelectable(HttpServletRequest request) {
        try {
            require(request);
            // The supplier selectable service deliberately uses the generic read
            // projection without injecting the caller's organization predicate.
            // This lookup is shared reference data: the returned bean projection
            // contains fields from all organizations visible in the database.
            // Keep the owned route aligned with that contract instead of turning
            // it into an empty, organization-scoped list for Test users.
            String sql = "SELECT T.* FROM T_EMC_INFO_TMPL_FIELD T ORDER BY T.ID";
            return ok(JSON.toJSONString(selectSelectableRows(sql), SerializerFeature.WriteMapNullValue));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = PREFIX + "/items/queries", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> items(HttpServletRequest request, HttpServletResponse response,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            JSONObject root = object(body);
            List<String> values = new ArrayList<String>();
            String extra = "";
            String tmplId = text(root, "tmplId");
            if (tmplId != null) { extra = " AND T.TMPLID=?"; values.add(tmplId); }
            String fieldId = text(root, "infoTmplFieldId");
            if (fieldId != null) { extra += " AND T.INFOTMPLFIELDID=?"; values.add(fieldId); }
            Page page;
            try { page = Page.parse(body); }
            catch (IllegalArgumentException invalid) { throw new MalformedInfoTmplQueryException(invalid); }
            return ok(selectPage("SELECT T.* FROM T_EMC_TMPL_FIELD_ITEM T WHERE T.CREATEDBYORGID=?" + extra, "Q.ID DESC", principal.getOrgId(), values, page));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (MalformedInfoTmplQueryException malformed) { return unexpected(response, request.getRequestURI()); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = PREFIX + "/{tmplId}/points/queries", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> pointsByTemplate(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("tmplId") String tmplId, @RequestBody(required = false) String body) {
        return points(request, response, tmplId, body);
    }

    @PostMapping(path = PREFIX + "/points/queries", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> points(HttpServletRequest request, HttpServletResponse response,
            @RequestBody(required = false) String body) {
        try { return points(request, response, text(object(body), "tmplId"), body); }
        catch (IllegalArgumentException invalid) { return unexpected(response, request.getRequestURI()); }
    }

    @GetMapping(path = PREFIX + "/info/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { require(request); return ok("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private ResponseEntity<String> fields(HttpServletRequest request, HttpServletResponse response, String tmplId, String body) {
        try {
            OwnedPrincipal principal = require(request);
            List<String> values = new ArrayList<String>();
            String extra = "";
            if (tmplId != null) { extra = " AND T.INFOTMPLID=?"; values.add(tmplId); }
            Page page;
            try { page = Page.parse(body); }
            catch (IllegalArgumentException invalid) { throw new MalformedInfoTmplQueryException(invalid); }
            return ok(selectPage("SELECT T.*, B.ITEMID FROM T_EMC_INFO_TMPL_FIELD T LEFT JOIN T_EMC_TMPL_FIELD_ITEM B ON T.ID=B.INFOTMPLFIELDID WHERE T.CREATEDBYORGID=?" + extra, "Q.ID", principal.getOrgId(), values, page));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (MalformedInfoTmplQueryException malformed) { return unexpected(response, request.getRequestURI()); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private ResponseEntity<String> points(HttpServletRequest request, HttpServletResponse response, String tmplId, String body) {
        try {
            OwnedPrincipal principal = require(request);
            List<String> values = new ArrayList<String>();
            String extra = "";
            if (tmplId != null) { extra = " AND T.INFOTMPLID=?"; values.add(tmplId); }
            Page page;
            try { page = Page.parse(body); }
            catch (IllegalArgumentException invalid) { throw new MalformedInfoTmplQueryException(invalid); }
            return ok(selectPage("SELECT T.*, MONITOR_POINT.MONITORPOINTNO, MONITOR_POINT.MONITORPOINTNAME FROM T_EMC_INFO_TMPL_POINT T LEFT JOIN T_EMC_MONITOR_POINT MONITOR_POINT ON MONITOR_POINT.ID=T.MONITORPOINTID LEFT JOIN T_EMC_INFO_TMPL TMPL ON TMPL.ID=T.INFOTMPLID WHERE TMPL.CREATEDBYORGID=?" + extra, "Q.ID", principal.getOrgId(), values, page));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (MalformedInfoTmplQueryException malformed) { return unexpected(response, request.getRequestURI()); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private String selectPage(String base, String order, String organization, List<String> values, Page page) throws SQLException {
        String countSql = "SELECT COUNT(1) FROM (" + base + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY " + order + ") RN_ FROM (" + base + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total;
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, String> types = new LinkedHashMap<String, String>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement count = connection.prepareStatement(countSql)) {
                bind(count, organization, values, 1);
                try (ResultSet result = count.executeQuery()) { if (!result.next()) throw new SQLException("info template count unavailable"); total = result.getInt(1); }
            }
            try (PreparedStatement query = connection.prepareStatement(pageSql)) {
                int index = bind(query, organization, values, 1); query.setInt(index++, page.start()); query.setInt(index, page.end());
                try (ResultSet result = query.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    for (int i = 1; i <= metadata.getColumnCount(); i++) types.put(metadata.getColumnLabel(i).toLowerCase(Locale.ROOT), type(metadata.getColumnType(i)));
                    while (result.next()) rows.add(row(result, metadata));
                }
            }
        }
        Map<String, Object> envelope = new LinkedHashMap<String, Object>(); envelope.put("columnTypeMap", types);
        envelope.put("first", page.number == 1); envelope.put("last", total == 0 || page.number >= pages(total, page.size)); envelope.put("numberOfElements", rows.size()); envelope.put("pageNumber", page.number); envelope.put("pageSize", page.size); envelope.put("rows", rows); envelope.put("total", total);
        Map<String, Object> totals = new LinkedHashMap<String, Object>(); totals.put("qty_", total); envelope.put("totalMap", totals); envelope.put("totalPages", pages(total, page.size));
        return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    private List<Map<String, Object>> selectRows(String sql, String organization, List<String> values) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql)) {
            bind(query, organization, values, 1);
            try (ResultSet result = query.executeQuery()) { ResultSetMetaData metadata = result.getMetaData(); while (result.next()) rows.add(row(result, metadata)); }
        }
        return rows;
    }

    /** Project JDBC columns through the supplier's bean serializer for this lookup. */
    private List<Map<String, Object>> selectSelectableRows(String sql) throws SQLException {
        List<Map<String, Object>> rows = selectRows(sql, null, new ArrayList<String>());
        List<Map<String, Object>> projected = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> source : rows) {
            Map<String, Object> value = new LinkedHashMap<String, Object>();
            for (Map.Entry<String, Object> entry : source.entrySet()) {
                String key = selectableProperty(entry.getKey());
                Object item = entry.getValue();
                if ("createdTime".equals(key) && item instanceof Date) {
                    item = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format((Date) item);
                } else if ("createdTime".equals(key) && item instanceof Number) {
                    item = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(new Date(((Number) item).longValue()));
                }
                if (key != null && item != null) value.put(key, item);
            }
            value.put("ext$", new LinkedHashMap<String, Object>());
            projected.add(value);
        }
        return projected;
    }

    private static String selectableProperty(String key) {
        if (key == null) return null;
        if ("createdbyid".equals(key)) return "createdById";
        if ("createdbyname".equals(key)) return "createdByName";
        if ("createdbyorgid".equals(key)) return "createdByOrgId";
        if ("createdbyorgname".equals(key)) return "createdByOrgName";
        if ("createdtime".equals(key)) return "createdTime";
        if ("fieldtitle".equals(key)) return "fieldTitle";
        if ("infotmplid".equals(key)) return "infoTmplId";
        if ("lowlimit".equals(key)) return "lowLimit";
        if ("unitname".equals(key)) return "unitName";
        if ("revisionrule".equals(key)) return "revisionRule";
        if ("primaryindex".equals(key)) return "primaryIndex";
        if ("primaryname".equals(key)) return "primaryName";
        if ("bizfield".equals(key)) return "bizField";
        if ("defaultvalue".equals(key)) return "defaultValue";
        return key;
    }

    private static int bind(PreparedStatement statement, String organization, List<String> values, int index) throws SQLException { if (organization != null) statement.setString(index++, organization); for (String value : values) statement.setString(index++, value); return index; }
    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException { Map<String, Object> value = new LinkedHashMap<String, Object>(); for (int i = 1; i <= metadata.getColumnCount(); i++) { String label = metadata.getColumnLabel(i); if ("RN_".equalsIgnoreCase(label)) continue; Object item = JdbcValue.normalize(result.getObject(i)); if (item != null) value.put(camel(label), item); } return value; }
    private static String camel(String label) { StringBuilder value = new StringBuilder(); boolean upper = false; for (char c : label.toLowerCase(Locale.ROOT).toCharArray()) { if (c == '_') { upper = true; continue; } value.append(upper ? Character.toUpperCase(c) : c); upper = false; } return value.toString(); }
    private static String type(int type) { if (type == Types.DATE || type == Types.TIMESTAMP || type == Types.TIMESTAMP_WITH_TIMEZONE) return "dateTime"; if (type == Types.NUMERIC || type == Types.DECIMAL || type == Types.INTEGER || type == Types.BIGINT) return "number"; return "string"; }
    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }
    private static JSONObject object(String body) { return body == null || body.trim().isEmpty() ? new JSONObject() : JSON.parseObject(body); }
    private static String text(JSONObject root, String key) { if (root == null || root.get(key) == null) return null; String value = String.valueOf(root.get(key)).trim(); return value.isEmpty() ? null : value; }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}"); }
    private static ResponseEntity<String> unexpected(HttpServletResponse response, String path) {
        if (response != null) {
            try {
                response.resetBuffer(); response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.getWriter().write(unexpectedBody(path)); response.getWriter().flush(); return null;
            } catch (IOException ignored) { }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(unexpectedBody(path));
    }
    private static String unexpectedBody(String path) {
        String safePath = path == null ? "" : path.replace("\\", "\\\\").replace("\"", "\\\"");
        return "{\"error\":\"Internal Server Error\",\"message\":\"GIKAM.EXCEPTION.UNEXPECTED\",\"path\":\""
                + safePath + "\",\"stacktrace\":\"owned-info-tmpl-malformed-query\",\"status\":500,\"timestamp\":\""
                + new java.util.Date().toString() + "\"}";
    }
    private static final class MalformedInfoTmplQueryException extends IllegalArgumentException {
        MalformedInfoTmplQueryException(Throwable cause) { super("malformed info-template query", cause); }
    }

    private static final class Page { final int number, size; Page(int number, int size) { this.number = number; this.size = size; } static Page parse(String body) { JSONObject root = body == null || body.trim().isEmpty() ? new JSONObject() : JSON.parseObject(body); JSONObject p = root == null ? null : root.getJSONObject("p"); int number = p == null ? 1 : Math.max(1, p.getIntValue("n")); int size = p == null ? 50 : Math.min(500, Math.max(1, p.getIntValue("s"))); return new Page(number, size); } int start() { return (number - 1) * size; } int end() { return number * size; } }
}
