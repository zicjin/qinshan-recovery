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

/**
 * Source-owned GenericResource task-query family for the item modules.
 * Every alias remains in this Spring application and shares the identity,
 * organization and Oracle lifecycle; this is not a compatibility process.
 */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedItemTaskQueryController {
    private static final String PREFIX = "/secure/emc/module/item/";
    private static final Map<String, String> TABLES = tables();
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedItemTaskQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("item task dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @PostMapping(path = {
            PREFIX + "complaints/queries/tasks", PREFIX + "corrections/queries/tasks",
            PREFIX + "emc-cars/queries/tasks", PREFIX + "improvements/queries/tasks",
            PREFIX + "inconformitys/queries/tasks", PREFIX + "internal-audits/queries/tasks",
            PREFIX + "leave-applies/queries/tasks", PREFIX + "leave-replenishs/queries/tasks",
            PREFIX + "lm/overtimes/queries/tasks", PREFIX + "manage-reviews/queries/tasks",
            PREFIX + "precautions/queries/tasks", PREFIX + "quality-controls/queries/tasks",
            PREFIX + "quality-supervisions/queries/tasks", PREFIX + "quotation/quotationregisters/queries/tasks",
            PREFIX + "report-queries/queries/tasks", PREFIX + "scm/conven-mtls/queries/tasks",
            PREFIX + "scm/equipt-purchs/queries/tasks", PREFIX + "scm/office-mtls/queries/tasks",
            PREFIX + "scm/standard-mtls/queries/tasks"
            }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            return ok(selectPage(table(resource(request.getRequestURI())), principal.getOrgId(), Page.parse(body)));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {
            PREFIX + "complaints/info/status", PREFIX + "corrections/info/status",
            PREFIX + "emc-cars/info/status", PREFIX + "improvements/info/status",
            PREFIX + "inconformitys/info/status", PREFIX + "internal-audits/info/status",
            PREFIX + "leave-applies/info/status", PREFIX + "leave-replenishs/info/status",
            PREFIX + "lm/overtimes/info/status", PREFIX + "manage-reviews/info/status",
            PREFIX + "precautions/info/status", PREFIX + "quality-controls/info/status",
            PREFIX + "quality-supervisions/info/status", PREFIX + "quotation/quotationregisters/info/status",
            PREFIX + "report-queries/info/status", PREFIX + "scm/conven-mtls/info/status",
            PREFIX + "scm/equipt-purchs/info/status", PREFIX + "scm/office-mtls/info/status",
            PREFIX + "scm/standard-mtls/info/status"
            }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { require(request); return ok("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.PERSONNEL_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private static String resource(String path) {
        if (path == null || !path.startsWith(PREFIX)) throw new IllegalArgumentException("item task route is invalid");
        String value = path.substring(PREFIX.length(), path.indexOf("/queries", PREFIX.length()) > 0 ? path.indexOf("/queries", PREFIX.length()) : path.indexOf("/info/status", PREFIX.length()));
        if (!TABLES.containsKey(value)) throw new IllegalArgumentException("unsupported item task resource");
        return value;
    }
    private static String table(String resource) { String value = TABLES.get(resource); if (value == null) throw new IllegalArgumentException("unsupported item task resource"); return value; }

    private String selectPage(String table, String organization, Page page) throws SQLException {
        String base = "SELECT T.* FROM " + table + " T WHERE T.CREATEDBYORGID=?";
        String countSql = "SELECT COUNT(1) FROM (" + base + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM (" + base + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total; List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>(); Map<String, String> types = new LinkedHashMap<String, String>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement count = connection.prepareStatement(countSql)) { count.setString(1, organization); try (ResultSet result = count.executeQuery()) { if (!result.next()) throw new SQLException("item task count unavailable"); total = result.getInt(1); } }
            try (PreparedStatement query = connection.prepareStatement(pageSql)) {
                query.setString(1, organization); query.setInt(2, page.start()); query.setInt(3, page.end());
                try (ResultSet result = query.executeQuery()) { ResultSetMetaData metadata = result.getMetaData(); for (int i = 1; i <= metadata.getColumnCount(); i++) types.put(metadata.getColumnLabel(i).toLowerCase(Locale.ROOT), type(metadata.getColumnType(i))); while (result.next()) rows.add(row(result, metadata)); }
            }
        }
        Map<String, Object> envelope = new LinkedHashMap<String, Object>(); envelope.put("columnTypeMap", types); envelope.put("first", page.number == 1); envelope.put("last", total == 0 || page.number >= pages(total, page.size)); envelope.put("numberOfElements", rows.size()); envelope.put("pageNumber", page.number); envelope.put("pageSize", page.size); envelope.put("rows", rows); envelope.put("total", total); Map<String, Object> totalMap = new LinkedHashMap<String, Object>(); totalMap.put("qty_", total); envelope.put("totalMap", totalMap); envelope.put("totalPages", pages(total, page.size)); return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException { Map<String, Object> value = new LinkedHashMap<String, Object>(); for (int i = 1; i <= metadata.getColumnCount(); i++) { String label = metadata.getColumnLabel(i); if ("RN_".equalsIgnoreCase(label)) continue; Object item = JdbcValue.normalize(result.getObject(i)); if (item != null) value.put(camel(label), item); } return value; }
    private static String camel(String label) { StringBuilder value = new StringBuilder(); boolean upper = false; for (char c : label.toLowerCase(Locale.ROOT).toCharArray()) { if (c == '_') { upper = true; continue; } value.append(upper ? Character.toUpperCase(c) : c); upper = false; } return value.toString(); }
    private static String type(int type) { if (type == Types.DATE || type == Types.TIMESTAMP || type == Types.TIMESTAMP_WITH_TIMEZONE) return "dateTime"; if (type == Types.NUMERIC || type == Types.DECIMAL || type == Types.INTEGER || type == Types.BIGINT) return "number"; return "string"; }
    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}"); }
    private static Map<String, String> tables() { Map<String, String> v = new LinkedHashMap<String, String>(); v.put("complaints", "T_EMC_COMPLAINT"); v.put("corrections", "T_EMC_CORRECTION"); v.put("emc-cars", "T_EMC_CAR"); v.put("improvements", "T_EMC_IMPROVEMENT"); v.put("inconformitys", "T_EMC_INCONFORMITY"); v.put("internal-audits", "T_EMC_INTERNAL_AUDIT"); v.put("leave-applies", "T_EMC_LEAVE_APPLY"); v.put("leave-replenishs", "T_EMC_LEAVE_REPLENISH"); v.put("lm/overtimes", "T_EMC_OVERTIME"); v.put("manage-reviews", "T_EMC_MANAGE_REVIEW"); v.put("precautions", "T_EMC_PRECAUTION"); v.put("quality-controls", "T_EMC_QUALITY_CONTROL"); v.put("quality-supervisions", "T_EMC_QUALITY_SUPERVISION"); v.put("quotation/quotationregisters", "T_EMC_QUOTATION_REGISTER"); v.put("report-queries", "T_EMC_REPORT_QUERY"); v.put("scm/conven-mtls", "T_EMC_CONVEN_MTL"); v.put("scm/equipt-purchs", "T_EMC_EQUIPT_PURCH"); v.put("scm/office-mtls", "T_EMC_OFFICE_MTL"); v.put("scm/standard-mtls", "T_EMC_STANDARD_MTL"); return Collections.unmodifiableMap(v); }
    private static final class Page { final int number, size; Page(int number, int size) { this.number = number; this.size = size; } static Page parse(String body) { JSONObject root = body == null || body.trim().isEmpty() ? new JSONObject() : JSON.parseObject(body); JSONObject p = root == null ? null : root.getJSONObject("p"); int n = p == null ? 1 : Math.max(1, p.getIntValue("n")); int s = p == null ? 50 : Math.min(500, Math.max(1, p.getIntValue("s"))); return new Page(n, s); } int start() { return (number - 1) * size; } int end() { return number * size; } }
}
