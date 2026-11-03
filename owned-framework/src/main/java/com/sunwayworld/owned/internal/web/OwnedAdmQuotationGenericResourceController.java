package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunwayworld.owned.internal.data.JdbcValue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;

/** Shared source-owned read seam for administrative and quotation GenericResources. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedAdmQuotationGenericResourceController {
    private static final Map<String, Resource> RESOURCES = resources();
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedAdmQuotationGenericResourceController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("administrative quotation dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource;
    }

    @PostMapping(path = {
            "/secure/emc/module/item/adm-areas/queries", "/secure/emc/module/item/adm-areas/queries/raw", "/secure/emc/module/item/adm-areas/queries/searchable",
            "/secure/emc/module/item/quotation/addprices/queries", "/secure/emc/module/item/quotation/addprices/queries/raw", "/secure/emc/module/item/quotation/addprices/queries/searchable",
            "/secure/emc/module/item/quotation/detectionfactors/queries", "/secure/emc/module/item/quotation/detectionfactors/queries/raw", "/secure/emc/module/item/quotation/detectionfactors/queries/searchable",
            "/secure/emc/module/item/quotation/discounts/queries", "/secure/emc/module/item/quotation/discounts/queries/raw", "/secure/emc/module/item/quotation/discounts/queries/searchable",
            "/secure/emc/module/item/quotation/quotationinformations/queries", "/secure/emc/module/item/quotation/quotationinformations/queries/raw", "/secure/emc/module/item/quotation/quotationinformations/queries/searchable"
            }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try {
            Resource resource = resource(request);
            OwnedPrincipal principal = require(request);
            Set<String> organizations = boundary.permittedOrganizationIds(request.getSession(false));
            return ok(selectPage(resource, principal.getOrgId(), organizations, Page.parse(body)));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {
            "/secure/emc/module/item/adm-areas/info/status",
            "/secure/emc/module/item/quotation/addprices/info/status",
            "/secure/emc/module/item/quotation/detectionfactors/info/status",
            "/secure/emc/module/item/quotation/discounts/info/status",
            "/secure/emc/module/item/quotation/quotationinformations/info/status"
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
        throw new IllegalArgumentException("unsupported administrative quotation resource");
    }

    private String selectPage(Resource resource, String organization, Set<String> organizations, Page page) throws SQLException {
        if (resource.detectionFactor) return selectDetectionFactorPage(organizations, page);
        String base = "SELECT T.* FROM " + resource.table + " T WHERE T.CREATEDBYORGID=?";
        String countSql = "SELECT COUNT(1) FROM (" + base + ") Q";
        String order = resource.area ? "Q.ORDERNO ASC,Q.ID ASC" : "Q.ID DESC";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY " + order + ") RN_ FROM (" + base + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total; List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>(); Map<String, String> types = new LinkedHashMap<String, String>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement count = connection.prepareStatement(countSql)) {
                count.setString(1, organization); try (ResultSet result = count.executeQuery()) { if (!result.next()) throw new SQLException("resource count unavailable"); total = result.getInt(1); }
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
        envelope.put("first", page.number == 1); envelope.put("last", total == 0 || page.number >= pages(total, page.size));
        envelope.put("numberOfElements", rows.size()); envelope.put("pageNumber", page.number); envelope.put("pageSize", page.size);
        envelope.put("rows", rows); envelope.put("total", total); Map<String, Object> totalMap = new LinkedHashMap<String, Object>(); totalMap.put("qty_", total); envelope.put("totalMap", totalMap); envelope.put("totalPages", pages(total, page.size));
        return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    private String selectDetectionFactorPage(Set<String> organizations, Page page) throws SQLException {
        if (organizations == null || organizations.isEmpty()) return emptyPage(page);
        StringBuilder scope = new StringBuilder();
        for (int i = 0; i < organizations.size(); i++) { if (i > 0) scope.append(','); scope.append('?'); }
        String base = "SELECT A.*,B.METHODNAME AS METHODNAMEE,B.METHODNO,B.TESTMETHODBASIS,C.ORGNAME AS PARENTORGNAME,E.TESTMETHODTYPE "
                + "FROM T_EMC_QUOTATION_DETECTIONFACTOR A LEFT JOIN T_EMC_METHOD B ON A.METHODID=B.ID "
                + "LEFT JOIN T_CORE_ORG C ON A.ORGID=C.ID LEFT JOIN T_EMC_ITEM_GROUP D ON A.ITEMGROUPID=D.ID "
                + "LEFT JOIN T_EMC_TEST_METHOD E ON D.TESTMETHODID=E.ID WHERE A.ORGID IN (" + scope + ")";
        String countSql = "SELECT COUNT(1) FROM (" + base + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM (" + base + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total; List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>(); Map<String, String> types = new LinkedHashMap<String, String>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement count = connection.prepareStatement(countSql)) {
                int index = 1; for (String value : organizations) count.setString(index++, value);
                try (ResultSet result = count.executeQuery()) { if (!result.next()) throw new SQLException("detection-factor count unavailable"); total = result.getInt(1); }
            }
            try (PreparedStatement query = connection.prepareStatement(pageSql)) {
                int index = 1; for (String value : organizations) query.setString(index++, value);
                query.setInt(index++, page.start()); query.setInt(index, page.end());
                try (ResultSet result = query.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    for (int i = 1; i <= metadata.getColumnCount(); i++) { String label = metadata.getColumnLabel(i); if (!"RN_".equalsIgnoreCase(label)) types.put(label.toLowerCase(Locale.ROOT), type(metadata.getColumnType(i))); }
                    while (result.next()) rows.add(detectionFactorRow(result, metadata));
                }
            }
        }
        return pageEnvelope(page, total, rows, types);
    }

    private static String emptyPage(Page page) {
        return pageEnvelope(page, 0, new ArrayList<Map<String, Object>>(), new LinkedHashMap<String, String>());
    }

    private static String pageEnvelope(Page page, int total, List<Map<String, Object>> rows, Map<String, String> types) {
        Map<String, Object> envelope = new LinkedHashMap<String, Object>(); envelope.put("columnTypeMap", types);
        envelope.put("first", page.number == 1); envelope.put("last", total == 0 || page.number >= pages(total, page.size));
        envelope.put("numberOfElements", rows.size()); envelope.put("pageNumber", page.number); envelope.put("pageSize", page.size);
        envelope.put("rows", rows); envelope.put("total", total); Map<String, Object> totalMap = new LinkedHashMap<String, Object>(); totalMap.put("qty_", total); envelope.put("totalMap", totalMap); envelope.put("totalPages", pages(total, page.size));
        return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) { String label = metadata.getColumnLabel(i); if (!"RN_".equalsIgnoreCase(label)) value.put(camel(label), JdbcValue.normalize(result.getObject(i))); }
        return value;
    }
    private static Map<String, Object> detectionFactorRow(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        Map<String, Object> extension = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i);
            String key = detectionFactorField(label);
            Object raw = result.getObject(i);
            Object normalized = raw == null ? null : detectionFactorValue(raw, metadata.getColumnType(i));
            if ("RN_".equalsIgnoreCase(label)) {
                if (normalized != null) extension.put("rn_", normalized);
            } else if (detectionFactorExtension(label)) {
                if (normalized != null) extension.put(key.toLowerCase(Locale.ROOT), normalized);
            } else if (normalized != null) {
                value.put(key, normalized);
            }
        }
        value.put("ext$", extension);
        return value;
    }
    private static boolean detectionFactorExtension(String label) {
        String key = label == null ? "" : label.toUpperCase(Locale.ROOT);
        return "METHODNAMEE".equals(key) || "METHODNO".equals(key) || "TESTMETHODBASIS".equals(key)
                || "TESTMETHODTYPE".equals(key) || "MONITORTMPLGROUPNAME".equals(key)
                || "MONITORTMPLID".equals(key) || "PARENTORGNAME".equals(key)
                || "REMARK".equals(key) || "TRIALSCOPE".equals(key);
    }
    private static Object detectionFactorValue(Object raw, int sqlType) {
        if (sqlType == Types.DATE || sqlType == Types.TIMESTAMP || sqlType == Types.TIMESTAMP_WITH_TIMEZONE) {
            java.util.Date date = raw instanceof java.util.Date ? (java.util.Date) raw : null;
            if (date != null) return new SimpleDateFormat("yyyy-MM-dd").format(date);
        }
        return raw instanceof Timestamp ? raw.toString() : raw;
    }
    private static String detectionFactorField(String label) {
        String key = label == null ? "" : label.toUpperCase(Locale.ROOT);
        String[] mappings = {"ID=id", "ANALYSISPRICE=analysisPrice", "CREATEDBYID=createdById", "CREATEDBYNAME=createdByName",
                "CREATEDBYORGID=createdByOrgId", "CREATEDBYORGNAME=createdByOrgName", "CREATEDTIME=createdTime",
                "ITEMGROUPID=itemGroupId", "ITEMGROUPNAME=itemGroupName", "METHODID=methodId", "METHODNAME=methodName",
                "ORGID=orgId", "SAMPLEPRICE=samplePrice", "TESTCATEGORYID=testCategoryId", "TESTID=testId", "TESTNAME=testName",
                "METHODNAMEE=methodNamee", "METHODNO=methodNo", "TESTMETHODBASIS=testMethodBasis", "TESTMETHODTYPE=testMethodType",
                "MONITORTMPLGROUPNAME=monitorTmplGroupName", "MONITORTMPLID=monitorTmplId", "PARENTORGNAME=parentOrgName",
                "TRIALSCOPE=trialScope", "REMARK=remark"};
        for (String item : mappings) { int split = item.indexOf('='); if (item.substring(0, split).equals(key)) return item.substring(split + 1); }
        return camel(label);
    }
    private static String camel(String label) { String key = label == null ? "" : label.toUpperCase(Locale.ROOT); Map<String, String> special = new LinkedHashMap<String, String>(); special.put("CREATEDBYID", "createdById"); special.put("CREATEDBYNAME", "createdByName"); special.put("CREATEDTIME", "createdTime"); special.put("CREATEDBYORGID", "createdByOrgId"); special.put("CREATEDBYORGNAME", "createdByOrgName"); special.put("ADMAREANAME", "admAreaName"); special.put("PARENTID", "parentId"); special.put("ORDERNO", "orderNo"); String known = special.get(key); if (known != null) return known; StringBuilder value = new StringBuilder(); boolean upper = false; for (char c : label.toLowerCase(Locale.ROOT).toCharArray()) { if (c == '_') { upper = true; continue; } value.append(upper ? Character.toUpperCase(c) : c); upper = false; } return value.toString(); }
    private static String type(int value) { return value == Types.DATE || value == Types.TIMESTAMP || value == Types.TIMESTAMP_WITH_TIMEZONE ? "dateTime" : (value == Types.NUMERIC || value == Types.DECIMAL || value == Types.INTEGER || value == Types.BIGINT ? "number" : "string"); }
    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.QUERY.INVALID\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}"); }
    private static Map<String, Resource> resources() { Map<String, Resource> result = new LinkedHashMap<String, Resource>(); result.put("/secure/emc/module/item/adm-areas", new Resource("T_EMC_ADM_AREA", true, false)); result.put("/secure/emc/module/item/quotation/addprices", new Resource("T_EMC_QUOTATION_ADDPRICE", false, false)); result.put("/secure/emc/module/item/quotation/detectionfactors", new Resource("T_EMC_QUOTATION_DETECTIONFACTOR", false, true)); result.put("/secure/emc/module/item/quotation/discounts", new Resource("T_EMC_QUOTATION_DISCOUNT", false, false)); result.put("/secure/emc/module/item/quotation/quotationinformations", new Resource("T_EMC_QUOTATION_INFORMATION", false, false)); return Collections.unmodifiableMap(result); }
    private static final class Resource { final String table; final boolean area; final boolean detectionFactor; Resource(String table, boolean area, boolean detectionFactor) { this.table = table; this.area = area; this.detectionFactor = detectionFactor; } }
    private static final class Page { final int number, size; Page(int number, int size) { this.number = number; this.size = size; } static Page parse(String body) { com.alibaba.fastjson.JSONObject root = body == null || body.trim().isEmpty() ? new com.alibaba.fastjson.JSONObject() : JSON.parseObject(body); com.alibaba.fastjson.JSONObject p = root == null ? null : root.getJSONObject("p"); int number = p == null ? 1 : Math.max(1, p.getIntValue("n")); int size = p == null ? 50 : Math.min(500, Math.max(1, p.getIntValue("s"))); return new Page(number, size); } int start() { return (number - 1) * size; } int end() { return number * size; } }
}
