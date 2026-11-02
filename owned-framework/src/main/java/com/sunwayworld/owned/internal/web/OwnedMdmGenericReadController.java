package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
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

/** Shared read-only seam for simple MDM GenericResource families. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedMdmGenericReadController {
    private static final Map<String, Resource> RESOURCES = resources();
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedMdmGenericReadController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("MDM generic read dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @PostMapping(path = {
            "/secure/emc/module/mdm/basemdm/biz-categories/queries", "/secure/emc/module/mdm/basemdm/biz-categories/queries/raw", "/secure/emc/module/mdm/basemdm/biz-categories/queries/searchable",
            "/secure/emc/module/mdm/basemdm/certs/queries", "/secure/emc/module/mdm/basemdm/certs/queries/raw", "/secure/emc/module/mdm/basemdm/certs/queries/searchable",
            "/secure/emc/module/mdm/bizmdm/index-categories/queries", "/secure/emc/module/mdm/bizmdm/index-categories/queries/raw", "/secure/emc/module/mdm/bizmdm/index-categories/queries/searchable",
            "/secure/emc/module/mdm/bizmdm/methods/queries", "/secure/emc/module/mdm/bizmdm/methods/queries/raw", "/secure/emc/module/mdm/bizmdm/methods/queries/searchable",
            "/secure/emc/module/mdm/bizmdm/monitor-tmpls/queries", "/secure/emc/module/mdm/bizmdm/monitor-tmpls/queries/raw", "/secure/emc/module/mdm/bizmdm/monitor-tmpls/queries/searchable",
            "/secure/emc/module/mdm/bizmdm/clients/queries", "/secure/emc/module/mdm/bizmdm/clients/queries/raw", "/secure/emc/module/mdm/bizmdm/clients/queries/searchable",
            "/secure/emc/module/mdm/bizmdm/comparisons/queries", "/secure/emc/module/mdm/bizmdm/comparisons/queries/raw", "/secure/emc/module/mdm/bizmdm/comparisons/queries/searchable",
            "/secure/emc/module/mdm/bizmdm/comparisons/results/queries",
            "/secure/emc/module/mdm/bizmdm/samplenumbers/queries", "/secure/emc/module/mdm/bizmdm/samplenumbers/queries/raw", "/secure/emc/module/mdm/bizmdm/samplenumbers/queries/searchable",
            "/secure/emc/module/item/sqls/queries", "/secure/emc/module/item/sqls/queries/raw", "/secure/emc/module/item/sqls/queries/searchable",
            "/secure/emc/module/item/year-train-plans/queries", "/secure/emc/module/item/year-train-plans/queries/raw", "/secure/emc/module/item/year-train-plans/queries/searchable",
            "/secure/emc/module/mdm/basemdm/areas/queries", "/secure/emc/module/mdm/basemdm/areas/queries/raw", "/secure/emc/module/mdm/basemdm/areas/queries/searchable",
            "/secure/emc/module/mdm/basemdm/biologys/queries", "/secure/emc/module/mdm/basemdm/biologys/queries/raw", "/secure/emc/module/mdm/basemdm/biologys/queries/searchable",
            "/secure/emc/module/mdm/basemdm/container-categories/queries", "/secure/emc/module/mdm/basemdm/container-categories/queries/raw", "/secure/emc/module/mdm/basemdm/container-categories/queries/searchable",
            "/secure/emc/module/mdm/basemdm/data-correlations/queries", "/secure/emc/module/mdm/basemdm/data-correlations/queries/raw", "/secure/emc/module/mdm/basemdm/data-correlations/queries/searchable",
            "/secure/emc/module/mdm/basemdm/env-records/queries", "/secure/emc/module/mdm/basemdm/env-records/queries/raw", "/secure/emc/module/mdm/basemdm/env-records/queries/searchable",
            "/secure/emc/module/mdm/basemdm/fetch-data-lines/queries", "/secure/emc/module/mdm/basemdm/fetch-data-lines/queries/raw", "/secure/emc/module/mdm/basemdm/fetch-data-lines/queries/searchable",
            "/secure/emc/module/mdm/basemdm/fetch-datas/queries", "/secure/emc/module/mdm/basemdm/fetch-datas/queries/raw", "/secure/emc/module/mdm/basemdm/fetch-datas/queries/searchable",
            "/secure/emc/module/mdm/basemdm/fixedassets/queries", "/secure/emc/module/mdm/basemdm/fixedassets/queries/raw", "/secure/emc/module/mdm/basemdm/fixedassets/queries/searchable",
            "/secure/emc/module/mdm/basemdm/index-items/queries", "/secure/emc/module/mdm/basemdm/index-items/queries/raw", "/secure/emc/module/mdm/basemdm/index-items/queries/searchable",
            "/secure/emc/module/mdm/basemdm/monitor-abilities/queries", "/secure/emc/module/mdm/basemdm/monitor-abilities/queries/raw", "/secure/emc/module/mdm/basemdm/monitor-abilities/queries/searchable",
            "/secure/emc/module/mdm/basemdm/monitor-point-categories/queries", "/secure/emc/module/mdm/basemdm/monitor-point-categories/queries/raw", "/secure/emc/module/mdm/basemdm/monitor-point-categories/queries/searchable",
            "/secure/emc/module/mdm/basemdm/monitor-tmpl-groups/queries", "/secure/emc/module/mdm/basemdm/monitor-tmpl-groups/queries/raw", "/secure/emc/module/mdm/basemdm/monitor-tmpl-groups/queries/searchable",
            "/secure/emc/module/mdm/basemdm/property-tmpls/queries", "/secure/emc/module/mdm/basemdm/property-tmpls/queries/raw", "/secure/emc/module/mdm/basemdm/property-tmpls/queries/searchable",
            "/secure/emc/module/mdm/basemdm/qc-categories/queries", "/secure/emc/module/mdm/basemdm/qc-categories/queries/raw", "/secure/emc/module/mdm/basemdm/qc-categories/queries/searchable",
            "/secure/emc/module/mdm/basemdm/rooms/queries", "/secure/emc/module/mdm/basemdm/rooms/queries/raw", "/secure/emc/module/mdm/basemdm/rooms/queries/searchable",
            "/secure/emc/module/mdm/basemdm/scripts/queries", "/secure/emc/module/mdm/basemdm/scripts/queries/raw", "/secure/emc/module/mdm/basemdm/scripts/queries/searchable",
            "/secure/emc/module/mdm/basemdm/subcontractors/queries", "/secure/emc/module/mdm/basemdm/subcontractors/queries/raw", "/secure/emc/module/mdm/basemdm/subcontractors/queries/searchable",
            "/secure/emc/module/mdm/basemdm/test-categories/queries", "/secure/emc/module/mdm/basemdm/test-categories/queries/raw", "/secure/emc/module/mdm/basemdm/test-categories/queries/searchable",
            "/secure/emc/module/mdm/basemdm/train-courses/queries", "/secure/emc/module/mdm/basemdm/train-courses/queries/raw", "/secure/emc/module/mdm/basemdm/train-courses/queries/searchable",
            "/secure/emc/module/mdm/basemdm/units/queries", "/secure/emc/module/mdm/basemdm/units/queries/raw", "/secure/emc/module/mdm/basemdm/units/queries/searchable",
            "/secure/emc/module/mdm/basemdm/vendors/queries", "/secure/emc/module/mdm/basemdm/vendors/queries/raw", "/secure/emc/module/mdm/basemdm/vendors/queries/searchable"
            }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            return ok(selectPage(resource(request), principal.getOrgId(), Page.parse(body)));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {
            "/secure/emc/module/mdm/basemdm/biz-categories/info/status", "/secure/emc/module/mdm/basemdm/biz-categories/{id}/info/status",
            "/secure/emc/module/mdm/basemdm/certs/info/status", "/secure/emc/module/mdm/basemdm/certs/{id}/info/status",
            "/secure/emc/module/mdm/bizmdm/index-categories/info/status", "/secure/emc/module/mdm/bizmdm/index-categories/{id}/info/status",
            "/secure/emc/module/mdm/bizmdm/methods/info/status", "/secure/emc/module/mdm/bizmdm/methods/{id}/info/status",
            "/secure/emc/module/mdm/bizmdm/monitor-tmpls/info/status", "/secure/emc/module/mdm/bizmdm/monitor-tmpls/{id}/info/status",
            "/secure/emc/module/mdm/bizmdm/clients/info/status", "/secure/emc/module/mdm/bizmdm/clients/{id}/info/status",
            "/secure/emc/module/mdm/bizmdm/comparisons/info/status", "/secure/emc/module/mdm/bizmdm/comparisons/{id}/info/status",
            "/secure/emc/module/mdm/bizmdm/samplenumbers/info/status", "/secure/emc/module/mdm/bizmdm/samplenumbers/{id}/info/status",
            "/secure/emc/module/item/sqls/info/status", "/secure/emc/module/item/sqls/{id}/info/status",
            "/secure/emc/module/item/year-train-plans/info/status", "/secure/emc/module/item/year-train-plans/{id}/info/status",
            "/secure/emc/module/mdm/basemdm/areas/info/status", "/secure/emc/module/mdm/basemdm/areas/{id}/info/status",
            "/secure/emc/module/mdm/basemdm/biologys/info/status", "/secure/emc/module/mdm/basemdm/biologys/{id}/info/status",
            "/secure/emc/module/mdm/basemdm/container-categories/info/status", "/secure/emc/module/mdm/basemdm/container-categories/{id}/info/status",
            "/secure/emc/module/mdm/basemdm/data-correlations/info/status", "/secure/emc/module/mdm/basemdm/data-correlations/{id}/info/status",
            "/secure/emc/module/mdm/basemdm/env-records/info/status", "/secure/emc/module/mdm/basemdm/env-records/{id}/info/status",
            "/secure/emc/module/mdm/basemdm/fetch-data-lines/info/status", "/secure/emc/module/mdm/basemdm/fetch-data-lines/{id}/info/status",
            "/secure/emc/module/mdm/basemdm/fetch-datas/info/status", "/secure/emc/module/mdm/basemdm/fetch-datas/{id}/info/status",
            "/secure/emc/module/mdm/basemdm/fixedassets/info/status", "/secure/emc/module/mdm/basemdm/fixedassets/{id}/info/status",
            "/secure/emc/module/mdm/basemdm/index-items/info/status", "/secure/emc/module/mdm/basemdm/index-items/{id}/info/status",
            "/secure/emc/module/mdm/basemdm/monitor-abilities/info/status", "/secure/emc/module/mdm/basemdm/monitor-abilities/{id}/info/status",
            "/secure/emc/module/mdm/basemdm/monitor-point-categories/info/status", "/secure/emc/module/mdm/basemdm/monitor-point-categories/{id}/info/status",
            "/secure/emc/module/mdm/basemdm/monitor-tmpl-groups/info/status", "/secure/emc/module/mdm/basemdm/monitor-tmpl-groups/{id}/info/status",
            "/secure/emc/module/mdm/basemdm/property-tmpls/info/status", "/secure/emc/module/mdm/basemdm/property-tmpls/{id}/info/status",
            "/secure/emc/module/mdm/basemdm/qc-categories/info/status", "/secure/emc/module/mdm/basemdm/qc-categories/{id}/info/status",
            "/secure/emc/module/mdm/basemdm/rooms/info/status", "/secure/emc/module/mdm/basemdm/rooms/{id}/info/status",
            "/secure/emc/module/mdm/basemdm/scripts/info/status", "/secure/emc/module/mdm/basemdm/scripts/{id}/info/status",
            "/secure/emc/module/mdm/basemdm/subcontractors/info/status", "/secure/emc/module/mdm/basemdm/subcontractors/{id}/info/status",
            "/secure/emc/module/mdm/basemdm/test-categories/info/status", "/secure/emc/module/mdm/basemdm/test-categories/{id}/info/status",
            "/secure/emc/module/mdm/basemdm/train-courses/info/status", "/secure/emc/module/mdm/basemdm/train-courses/{id}/info/status",
            "/secure/emc/module/mdm/basemdm/units/info/status", "/secure/emc/module/mdm/basemdm/units/{id}/info/status",
            "/secure/emc/module/mdm/basemdm/vendors/info/status", "/secure/emc/module/mdm/basemdm/vendors/{id}/info/status"
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

    private static Resource resource(HttpServletRequest request) {
        String path = request == null ? "" : request.getRequestURI();
        for (Map.Entry<String, Resource> entry : RESOURCES.entrySet()) if (path.startsWith(entry.getKey() + "/")) return entry.getValue();
        throw new IllegalArgumentException("unsupported MDM generic resource");
    }

    private String selectPage(Resource resource, String organization, Page page) throws SQLException {
        String base = "SELECT " + resource.select + " FROM " + resource.from + " WHERE " + resource.organizationColumn + "=?";
        String countSql = "SELECT COUNT(1) FROM (" + base + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM (" + base + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total;
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, String> types = new LinkedHashMap<String, String>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement count = connection.prepareStatement(countSql)) {
                count.setString(1, organization);
                try (ResultSet result = count.executeQuery()) { if (!result.next()) throw new SQLException("MDM generic count unavailable"); total = result.getInt(1); }
            }
            try (PreparedStatement query = connection.prepareStatement(pageSql)) {
                query.setString(1, organization); query.setInt(2, page.start()); query.setInt(3, page.end());
                try (ResultSet result = query.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    for (int i = 1; i <= metadata.getColumnCount(); i++) { String label = metadata.getColumnLabel(i); if (!"RN_".equalsIgnoreCase(label)) types.put(label.toLowerCase(Locale.ROOT), type(metadata.getColumnType(i))); }
                    while (result.next()) rows.add(row(result, metadata));
                }
            }
        }
        Map<String, Object> envelope = new LinkedHashMap<String, Object>(); envelope.put("columnTypeMap", types);
        envelope.put("first", page.number == 1); envelope.put("last", total == 0 || page.number >= pages(total, page.size)); envelope.put("numberOfElements", rows.size()); envelope.put("pageNumber", page.number); envelope.put("pageSize", page.size); envelope.put("rows", rows); envelope.put("total", total);
        Map<String, Object> totalMap = new LinkedHashMap<String, Object>(); totalMap.put("qty_", total); envelope.put("totalMap", totalMap); envelope.put("totalPages", pages(total, page.size));
        return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException { Map<String, Object> value = new LinkedHashMap<String, Object>(); for (int i = 1; i <= metadata.getColumnCount(); i++) { String label = metadata.getColumnLabel(i); if (!"RN_".equalsIgnoreCase(label)) value.put(camel(label), JdbcValue.normalize(result.getObject(i))); } return value; }
    private static String camel(String label) { String key = label == null ? "" : label.toUpperCase(Locale.ROOT); if ("CREATEDBYID".equals(key)) return "createdById"; if ("CREATEDBYNAME".equals(key)) return "createdByName"; if ("CREATEDTIME".equals(key)) return "createdTime"; if ("CREATEDBYORGID".equals(key)) return "createdByOrgId"; if ("CREATEDBYORGNAME".equals(key)) return "createdByOrgName"; StringBuilder value = new StringBuilder(); boolean upper = false; for (char c : label.toLowerCase(Locale.ROOT).toCharArray()) { if (c == '_') { upper = true; continue; } value.append(upper ? Character.toUpperCase(c) : c); upper = false; } return value.toString(); }
    private static String type(int value) { return value == Types.DATE || value == Types.TIMESTAMP || value == Types.TIMESTAMP_WITH_TIMEZONE ? "dateTime" : (value == Types.NUMERIC || value == Types.DECIMAL || value == Types.INTEGER || value == Types.BIGINT ? "number" : "string"); }
    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.QUERY.INVALID\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}"); }
    private static Map<String, Resource> resources() { Map<String, Resource> result = new LinkedHashMap<String, Resource>();
        result.put("/secure/emc/module/mdm/basemdm/biz-categories", new Resource("T_EMC_BIZ_CATEGORY")); result.put("/secure/emc/module/mdm/basemdm/certs", new Resource("T_EMC_CERT")); result.put("/secure/emc/module/mdm/bizmdm/index-categories", new Resource("T_EMC_INDEX_CATEGORY I LEFT JOIN T_EMC_MONITOR_TMPL_GROUP P ON P.ID=I.MONITORTMPLGROUPID", "P.CREATEDBYORGID", "I.*")); result.put("/secure/emc/module/mdm/bizmdm/methods", new Resource("T_EMC_METHOD")); result.put("/secure/emc/module/mdm/bizmdm/monitor-tmpls", new Resource("T_EMC_MONITOR_TMPL")); result.put("/secure/emc/module/mdm/bizmdm/clients", new Resource("T_EMC_CLIENT")); result.put("/secure/emc/module/mdm/bizmdm/comparisons/results", new Resource("T_EMC_COMPARISON_RESULT")); result.put("/secure/emc/module/mdm/bizmdm/comparisons", new Resource("T_EMC_COMPARISON")); result.put("/secure/emc/module/mdm/bizmdm/samplenumbers", new Resource("T_EMC_SAMPLENUMBER"));
        result.put("/secure/emc/module/item/sqls", new Resource("T_EMC_SQL")); result.put("/secure/emc/module/item/year-train-plans", new Resource("T_EMC_YEAR_TRAIN_PLAN"));
        result.put("/secure/emc/module/mdm/basemdm/areas", new Resource("T_EMC_AREA")); result.put("/secure/emc/module/mdm/basemdm/biologys", new Resource("T_EMC_BIOLOGY")); result.put("/secure/emc/module/mdm/basemdm/container-categories", new Resource("T_EMC_CONTAINER_CATEGORY")); result.put("/secure/emc/module/mdm/basemdm/data-correlations", new Resource("T_EMC_DATA_CORRELATION")); result.put("/secure/emc/module/mdm/basemdm/env-records", new Resource("T_EMC_ENV_RECORD")); result.put("/secure/emc/module/mdm/basemdm/fetch-data-lines", new Resource("T_EMC_FETCH_DATA_LINE")); result.put("/secure/emc/module/mdm/basemdm/fetch-datas", new Resource("T_EMC_FETCH_DATA")); result.put("/secure/emc/module/mdm/basemdm/fixedassets", new Resource("T_EMC_FIXEDASSET")); result.put("/secure/emc/module/mdm/basemdm/index-items", new Resource("T_EMC_INDEX_ITEM")); result.put("/secure/emc/module/mdm/basemdm/monitor-abilities", new Resource("T_EMC_MONITOR_ABILITY")); result.put("/secure/emc/module/mdm/basemdm/monitor-point-categories", new Resource("T_EMC_MONITOR_POINT_CATEGORY")); result.put("/secure/emc/module/mdm/basemdm/monitor-tmpl-groups", new Resource("T_EMC_MONITOR_TMPL_GROUP")); result.put("/secure/emc/module/mdm/basemdm/property-tmpls", new Resource("T_EMC_PROPERTY_TMPL")); result.put("/secure/emc/module/mdm/basemdm/qc-categories", new Resource("T_EMC_QC_CATEGORY")); result.put("/secure/emc/module/mdm/basemdm/rooms", new Resource("T_EMC_ROOM")); result.put("/secure/emc/module/mdm/basemdm/scripts", new Resource("T_EMC_SCRIPT")); result.put("/secure/emc/module/mdm/basemdm/subcontractors", new Resource("T_EMC_SUBCONTRACTOR")); result.put("/secure/emc/module/mdm/basemdm/test-categories", new Resource("T_EMC_TEST_CATEGORY")); result.put("/secure/emc/module/mdm/basemdm/train-courses", new Resource("T_EMC_TRAIN_COURSE")); result.put("/secure/emc/module/mdm/basemdm/units", new Resource("T_EMC_UNIT")); result.put("/secure/emc/module/mdm/basemdm/vendors", new Resource("T_EMC_VENDOR")); return Collections.unmodifiableMap(result); }
    private static final class Resource { final String from, organizationColumn, select; Resource(String table) { this(table + " T", "T.CREATEDBYORGID", "T.*"); } Resource(String from, String organizationColumn, String select) { this.from = from; this.organizationColumn = organizationColumn; this.select = select; } }
    private static final class Page { final int number, size; Page(int number, int size) { this.number = number; this.size = size; } static Page parse(String body) { com.alibaba.fastjson.JSONObject root = body == null || body.trim().isEmpty() ? new com.alibaba.fastjson.JSONObject() : JSON.parseObject(body); com.alibaba.fastjson.JSONObject p = root == null ? null : root.getJSONObject("p"); int number = p == null ? 1 : Math.max(1, p.getIntValue("n")); int size = p == null ? 50 : Math.min(500, Math.max(1, p.getIntValue("s"))); return new Page(number, size); } int start() { return (number - 1) * size; } int end() { return number * size; } }
}
