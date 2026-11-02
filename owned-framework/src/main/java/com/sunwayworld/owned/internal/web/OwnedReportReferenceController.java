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

/** Owned read-only GenericResource seam for two base-MDM reference tables. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedReportReferenceController {
    private static final String FILE = "/secure/emc/module/mdm/basemdm/file-categories";
    private static final String REFERENCE = "/secure/emc/module/mdm/basemdm/report-info-references";
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedReportReferenceController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("reference dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource;
    }

    @PostMapping(path = {
            FILE + "/queries", FILE + "/queries/raw", FILE + "/queries/searchable",
            REFERENCE + "/queries", REFERENCE + "/queries/raw", REFERENCE + "/queries/searchable"
            }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try { require(request); return ok(selectPage(table(request.getRequestURI()), Page.parse(body))); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {FILE + "/info/status", REFERENCE + "/info/status"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { require(request); return ok("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private void require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ);
    }

    private static String table(String path) {
        if (path != null && path.startsWith(FILE + "/")) return "T_EMC_FILE_CATEGORY";
        if (path != null && path.startsWith(REFERENCE + "/")) return "T_EMC_REPORT_INFO_REFERENCE";
        throw new IllegalArgumentException("unsupported reference route");
    }

    private String selectPage(String table, Page page) throws SQLException {
        String source = source(table);
        String countSql = "SELECT COUNT(1) FROM (" + source + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM (" + source + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total; List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>(); Map<String, String> types = new LinkedHashMap<String, String>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement count = connection.prepareStatement(countSql); ResultSet result = count.executeQuery()) {
                if (!result.next()) throw new SQLException("reference count unavailable"); total = result.getInt(1);
            }
            try (PreparedStatement query = connection.prepareStatement(pageSql)) {
                query.setInt(1, page.start()); query.setInt(2, page.end());
                try (ResultSet result = query.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    for (int i = 1; i <= metadata.getColumnCount(); i++) {
                        String label = metadata.getColumnLabel(i); if (!"RN_".equalsIgnoreCase(label)) types.put(label.toLowerCase(Locale.ROOT), type(metadata.getColumnType(i)));
                    }
                    types.put("rn_", "number");
                    while (result.next()) rows.add(row(result, metadata, table));
                }
            }
        }
        Map<String, Object> envelope = new LinkedHashMap<String, Object>();
        envelope.put("columnTypeMap", types); envelope.put("first", page.number == 1);
        envelope.put("last", total == 0 || page.number >= pages(total, page.size)); envelope.put("numberOfElements", rows.size());
        envelope.put("pageNumber", page.number); envelope.put("pageSize", page.size); envelope.put("rows", rows); envelope.put("total", total);
        Map<String, Object> totalMap = new LinkedHashMap<String, Object>(); totalMap.put("qty_", total); envelope.put("totalMap", totalMap); envelope.put("totalPages", pages(total, page.size));
        return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    private static String source(String table) {
        if ("T_EMC_FILE_CATEGORY".equals(table)) return "SELECT T.ID,T.FILECATEGORYNO,T.FILECATEGORYNAME,T.CREATEDBYID,T.CREATEDBYNAME,T.CREATEDBYORGID,T.CREATEDBYORGNAME,T.CREATEDTIME FROM " + table + " T";
        return "SELECT T.ID,T.SORTNUM,T.BIZCATEGORY,T.ORDERCATEGORY,T.REPORTNAME,T.TESTCODE,T.ITEMNAME,T.REPORTSHOWNAME,T.METHOD,T.ANALYSISGROUP,T.GROUPCONDITIONONE,T.GROUPCONDITIONTWO,T.GROUPCONDITIONTHREE,T.GROUPCONDITIONFOUR,T.CREATEDBYID,T.CREATEDBYNAME,T.CREATEDBYORGID,T.CREATEDBYORGNAME,T.CREATEDTIME FROM " + table + " T";
    }

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata, String table) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) { String label = metadata.getColumnLabel(i); if ("RN_".equalsIgnoreCase(label)) continue; Object item = JdbcValue.normalize(result.getObject(i)); if (item != null) value.put(field(label, table), item); }
        value.put("ext$", new LinkedHashMap<String, Object>()); return value;
    }
    private static String field(String label, String table) {
        String key = label.toUpperCase(Locale.ROOT);
        if ("T_EMC_FILE_CATEGORY".equals(table)) {
            if ("FILECATEGORYNO".equals(key)) return "fileCategoryNo";
            if ("FILECATEGORYNAME".equals(key)) return "fileCategoryName";
        } else {
            String[] names = {"SORTNUM","BIZCATEGORY","ORDERCATEGORY","REPORTNAME","TESTCODE","ITEMNAME","REPORTSHOWNAME","METHOD","ANALYSISGROUP","GROUPCONDITIONONE","GROUPCONDITIONTWO","GROUPCONDITIONTHREE","GROUPCONDITIONFOUR"};
            String[] values = {"sortNum","bizCategory","orderCategory","reportName","testCode","itemName","reportShowName","method","analysisGroup","groupConditionOne","groupConditionTwo","groupConditionThree","groupConditionFour"};
            for (int i = 0; i < names.length; i++) if (names[i].equals(key)) return values[i];
        }
        if ("CREATEDBYID".equals(key)) return "createdById";
        if ("CREATEDBYNAME".equals(key)) return "createdByName";
        if ("CREATEDBYORGID".equals(key)) return "createdByOrgId";
        if ("CREATEDBYORGNAME".equals(key)) return "createdByOrgName";
        if ("CREATEDTIME".equals(key)) return "createdTime";
        return camel(label);
    }
    private static String camel(String label) { StringBuilder value = new StringBuilder(); boolean upper = false; for (char c : label.toLowerCase(Locale.ROOT).toCharArray()) { if (c == '_') { upper = true; continue; } value.append(upper ? Character.toUpperCase(c) : c); upper = false; } return value.toString(); }
    private static String type(int type) { if (type == Types.DATE || type == Types.TIMESTAMP || type == Types.TIMESTAMP_WITH_TIMEZONE) return "dateTime"; if (type == Types.NUMERIC || type == Types.DECIMAL || type == Types.INTEGER || type == Types.BIGINT) return "number"; return "string"; }
    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}"); }
    private static final class Page { final int number, size; Page(int n, int s) { number = n; size = s; } static Page parse(String body) { JSONObject root = body == null || body.trim().isEmpty() ? new JSONObject() : JSON.parseObject(body); JSONObject p = root == null ? null : root.getJSONObject("p"); int n = p == null ? 1 : Math.max(1, p.getIntValue("n")); int s = p == null ? 50 : Math.min(500, Math.max(1, p.getIntValue("s"))); return new Page(n, s); } int start() { return (number - 1) * size; } int end() { return number * size; } }
}
