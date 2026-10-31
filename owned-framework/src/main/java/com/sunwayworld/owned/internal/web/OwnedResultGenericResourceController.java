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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
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

/** Owned Java read-only GenericResource seam for result-equipment curves. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedResultGenericResourceController {
    private static final Map<String, Descriptor> RESOURCES = descriptors();
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedResultGenericResourceController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("result resource dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @PostMapping(path = {
            "/secure/emc/module/bp/result/equipt-curve-standards/queries",
            "/secure/emc/module/bp/result/equipt-curves/queries",
            "/secure/emc/module/bp/result/equipt-curve-standards/queries/raw",
            "/secure/emc/module/bp/result/equipt-curves/queries/raw",
            "/secure/emc/module/bp/result/equipt-curve-standards/queries/searchable",
            "/secure/emc/module/bp/result/equipt-curves/queries/searchable"
            }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        String resource = resource(request);
        try {
            Descriptor descriptor = descriptor(resource);
            requireRead(request);
            Page page = Page.parse(body);
            return ok(selectPage(descriptor, page));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {
            "/secure/emc/module/bp/result/equipt-curve-standards/info/status",
            "/secure/emc/module/bp/result/equipt-curves/info/status"
            }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        String resource = resource(request);
        try { descriptor(resource); requireRead(request); return ok("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {
            "/secure/emc/module/bp/result/equipt-curve-standards/{id}",
            "/secure/emc/module/bp/result/equipt-curves/{id}"
            }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request,
            @PathVariable("id") String id) {
        String resource = resource(request);
        try {
            Descriptor descriptor = descriptor(resource); requireRead(request);
            return ok(JSON.toJSONString(selectDetail(descriptor, numericId(id))));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {
            "/secure/emc/module/bp/result/equipt-curve-standards/{id}/action/print",
            "/secure/emc/module/bp/result/equipt-curves/{id}/action/print"
            }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> print(HttpServletRequest request,
            @PathVariable("id") String id) { return detail(request, id); }

    @GetMapping(path = {
            "/secure/emc/module/bp/result/equipt-curve-standards/{id}/columns/{column}",
            "/secure/emc/module/bp/result/equipt-curves/{id}/columns/{column}"
            }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> column(HttpServletRequest request,
            @PathVariable("id") String id, @PathVariable("column") String column) {
        String resource = resource(request);
        try {
            Descriptor descriptor = descriptor(resource); requireRead(request);
            String selected = descriptor.column(column);
            try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(
                    "SELECT " + selected + " FROM " + descriptor.table + " WHERE ID=?")) {
                query.setLong(1, numericId(id));
                try (ResultSet rows = query.executeQuery()) { return ok(JSON.toJSONString(rows.next() ? value(rows.getObject(1)) : null)); }
            }
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private static String resource(HttpServletRequest request) {
        String path = request.getRequestURI();
        String prefix = "/secure/emc/module/bp/result/";
        if (path == null || !path.startsWith(prefix)) throw new IllegalArgumentException("result resource path is invalid");
        int start = prefix.length();
        int slash = path.indexOf('/', start);
        if (slash <= start) throw new IllegalArgumentException("result resource path is invalid");
        return path.substring(start, slash);
    }

    private void requireRead(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ);
    }

    private String selectPage(Descriptor descriptor, Page page) throws SQLException {
        String base = descriptor.baseSql;
        String count = "SELECT COUNT(1) FROM (" + base + ") Q";
        String paged = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM ("
                + base + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        try (Connection connection = dataSource.getConnection()) {
            int total;
            try (PreparedStatement query = connection.prepareStatement(count); ResultSet rows = query.executeQuery()) {
                if (!rows.next()) throw new SQLException("result resource count returned no row"); total = rows.getInt(1);
            }
            ArrayList<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
            Map<String, String> types = new LinkedHashMap<String, String>();
            try (PreparedStatement query = connection.prepareStatement(paged)) {
                query.setInt(1, page.start()); query.setInt(2, page.end());
                try (ResultSet rows = query.executeQuery()) {
                    ResultSetMetaData meta = rows.getMetaData();
                    for (int i = 1; i <= meta.getColumnCount(); i++) types.put(meta.getColumnLabel(i).toLowerCase(Locale.ROOT), type(meta.getColumnType(i)));
                    while (rows.next()) values.add(row(rows, meta, descriptor));
                }
            }
            Map<String, Object> envelope = new LinkedHashMap<String, Object>();
            envelope.put("columnTypeMap", types); envelope.put("first", page.number == 1);
            envelope.put("last", total == 0 || page.number >= pages(total, page.size)); envelope.put("numberOfElements", values.size());
            envelope.put("pageNumber", page.number); envelope.put("pageSize", page.size); envelope.put("rows", values);
            envelope.put("total", total); Map<String, Object> totals = new LinkedHashMap<String, Object>(); totals.put("qty_", total);
            envelope.put("totalMap", totals); envelope.put("totalPages", pages(total, page.size));
            return JSON.toJSONString(envelope);
        }
    }

    private Map<String, Object> selectDetail(Descriptor descriptor, long id) throws SQLException {
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(
                descriptor.baseSql + " WHERE ID=?")) {
            query.setLong(1, id);
            try (ResultSet rows = query.executeQuery()) { return rows.next() ? row(rows, rows.getMetaData(), descriptor) : Collections.emptyMap(); }
        }
    }

    private static Map<String, Object> row(ResultSet rows, ResultSetMetaData meta, Descriptor descriptor) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>(); Map<String, Object> ext = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            String label = meta.getColumnLabel(i); Object item = wire(label, value(rows.getObject(i)));
            if (item == null) continue;
            if ("RN_".equalsIgnoreCase(label)) ext.put("rn_", String.valueOf(item));
            else { String name = descriptor.columns.get(label.toUpperCase(Locale.ROOT)); if (name == null) ext.put(label.toLowerCase(Locale.ROOT), item); else value.put(name, item); }
        }
        if (!ext.isEmpty()) value.put("ext$", ext); return value;
    }

    private static Object value(Object raw) throws SQLException {
        Object item = JdbcValue.normalize(raw); if (item instanceof Date) return new SimpleDateFormat("yyyy-MM-dd").format((Date) item); return item;
    }
    private static Object wire(String label, Object item) { if (item == null) return null; String key = label.toUpperCase(Locale.ROOT); if ("EQUIPTID".equals(key)) try { return Long.valueOf(String.valueOf(item)); } catch (NumberFormatException ignored) {} return item; }
    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }
    private static long numericId(String id) { if (id == null || !id.matches("[0-9]{1,30}")) throw new IllegalArgumentException("result resource id is invalid"); return Long.parseLong(id); }
    private static String type(int type) { if (type == Types.DATE || type == Types.TIMESTAMP || type == Types.TIMESTAMP_WITH_TIMEZONE) return "dateTime"; if (type == Types.NUMERIC || type == Types.DECIMAL || type == Types.INTEGER || type == Types.BIGINT) return "number"; return "string"; }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}"); }
    private static Descriptor descriptor(String resource) { Descriptor value = RESOURCES.get(resource); if (value == null) throw new IllegalArgumentException("unsupported result resource"); return value; }
    private static Map<String, Descriptor> descriptors() {
        Map<String, Descriptor> values = new LinkedHashMap<String, Descriptor>();
        values.put("equipt-curve-standards", new Descriptor("T_EMC_EQUIPT_CURVE_STANDARD", "SELECT T.* FROM T_EMC_EQUIPT_CURVE_STANDARD T", map(
                "ID", "id", "EQUIPTCURVEID", "equiptCurveId", "CURVECONFIGTEXT", "curveConfigText", "CREATEDBYID", "createdById", "CREATEDBYNAME", "createdByName", "CREATEDTIME", "createdTime", "CREATEDBYORGID", "createdByOrgId", "CREATEDBYORGNAME", "createdByOrgName")));
        values.put("equipt-curves", new Descriptor("T_EMC_EQUIPT_CURVE", "SELECT A.*,B.EQUIPTID AS EQUIPTNO,B.EQUIPTNAME FROM T_EMC_EQUIPT_CURVE A LEFT JOIN T_EMC_EQUIPT B ON A.EQUIPTID=B.ID", map(
                "ID", "id", "EQUIPTCURVENO", "equiptCurveNo", "EQUIPTID", "equiptId", "EQUIPTNO", "equiptNo", "EQUIPTNAME", "equiptName", "CONFDATE", "confDate", "ANALYSISDATE", "analysisDate", "EXPIREDATE", "expireDate", "MARKINGNO", "markingNo", "MARKINGNAME", "markingName", "MARKINGCONCENT", "markingConcent", "MARKINGUNITNAME", "markingUnitName", "MARKINGVALIDATE", "markingValidate", "MARKINGMANUFACTURER", "markingManufacturer", "USECONCENT", "useConcent", "USECONCENTUNITNAME", "useConcentUnitName", "COLORVOLUME", "colorVolume", "SOLCONF", "solConf", "CURVECONDITION", "curveCondition", "DILUENT", "diluent", "CONSTANTVOLUME", "constantVolume", "CONSTANTVOLUMEUNIT", "constantVolumeUnit", "CURVECONFIGTEXT", "curveConfigText", "MARKINGRECEIVENO", "markingReceiveno", "STATUS", "status", "REMARK", "remark", "CREATEDBYID", "createdById", "CREATEDBYNAME", "createdByName", "CREATEDTIME", "createdTime", "CREATEDBYORGID", "createdByOrgId", "CREATEDBYORGNAME", "createdByOrgName", "TESTNO", "testNo", "TESTNAME", "testName", "SLOPE", "slope", "INTERCEPT", "intercept", "COEFFICIENT", "coefficient")));
        return Collections.unmodifiableMap(values);
    }
    private static Map<String, String> map(String... values) { Map<String, String> result = new LinkedHashMap<String, String>(); for (int i = 0; i < values.length; i += 2) result.put(values[i], values[i + 1]); return result; }
    private static final class Descriptor { final String table; final String baseSql; final Map<String, String> columns; Descriptor(String table, String baseSql, Map<String, String> columns) { this.table = table; this.baseSql = baseSql; this.columns = columns; } String column(String name) { String key = name == null ? "" : name.toUpperCase(Locale.ROOT); if (!columns.containsKey(key)) throw new IllegalArgumentException("unsupported result resource column"); return key; } }
    private static final class Page { final int number; final int size; Page(int number, int size) { this.number = number; this.size = size; } static Page parse(String body) { JSONObject root = body == null || body.trim().isEmpty() ? new JSONObject() : JSON.parseObject(body); JSONObject p = root == null ? null : root.getJSONObject("p"); int n = p == null ? 1 : Math.max(1, p.getIntValue("n")); int s = p == null ? 50 : Math.min(500, Math.max(1, p.getIntValue("s"))); return new Page(n, s); } int start() { return (number - 1) * size; } int end() { return number * size; } }
}
