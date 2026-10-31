package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
 * Source-owned read-only implementation of the recovered equipment
 * GenericResource query families.  These routes execute in the existing
 * Spring application and use the clone-derived Oracle through the same
 * identity seam; they are not a compatibility process or HTTP bridge.
 *
 * <p>The supplier resources expose a deliberately loose map projection.  The
 * descriptor keeps the recovered bean property names for known columns and
 * places columns that are not part of the bean projection in {@code ext$},
 * matching the observed Gikam envelope.</p>
 */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedEquipmentGenericResourceController {
    private static final Map<String, Descriptor> RESOURCES = descriptors();

    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedEquipmentGenericResourceController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("equipment resource dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @PostMapping(path = {
            "/secure/emc/module/bp/equipt-crves-detail/queries",
            "/secure/emc/module/bp/equipt-crves/queries",
            "/secure/emc/module/bp/equipt-dioxin-sets/queries",
            "/secure/emc/module/bp/equipt-flow-sets/queries",
            "/secure/emc/module/bp/equipt-sets/queries",
            "/secure/emc/module/bp/equipt-standard-sets/queries",
            "/secure/emc/module/bp/equipt-sw-sets/queries",
            "/secure/emc/module/bp/equipt-temp-sets/queries",
            "/secure/emc/module/bp/equipt-crves-detail/queries/raw",
            "/secure/emc/module/bp/equipt-crves/queries/raw",
            "/secure/emc/module/bp/equipt-dioxin-sets/queries/raw",
            "/secure/emc/module/bp/equipt-flow-sets/queries/raw",
            "/secure/emc/module/bp/equipt-sets/queries/raw",
            "/secure/emc/module/bp/equipt-standard-sets/queries/raw",
            "/secure/emc/module/bp/equipt-sw-sets/queries/raw",
            "/secure/emc/module/bp/equipt-temp-sets/queries/raw",
            "/secure/emc/module/bp/equipt-crves-detail/queries/searchable",
            "/secure/emc/module/bp/equipt-crves/queries/searchable",
            "/secure/emc/module/bp/equipt-dioxin-sets/queries/searchable",
            "/secure/emc/module/bp/equipt-flow-sets/queries/searchable",
            "/secure/emc/module/bp/equipt-sets/queries/searchable",
            "/secure/emc/module/bp/equipt-standard-sets/queries/searchable",
            "/secure/emc/module/bp/equipt-sw-sets/queries/searchable",
            "/secure/emc/module/bp/equipt-temp-sets/queries/searchable"
            }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        String resource = resource(request);
        try {
            Descriptor descriptor = descriptor(resource);
            requireRead(request);
            QueryPage page = QueryPage.parse(body);
            return ok(selectPage(descriptor, page));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {
            "/secure/emc/module/bp/equipt-crves-detail/{id}",
            "/secure/emc/module/bp/equipt-crves/{id}",
            "/secure/emc/module/bp/equipt-dioxin-sets/{id}",
            "/secure/emc/module/bp/equipt-flow-sets/{id}",
            "/secure/emc/module/bp/equipt-sets/{id}",
            "/secure/emc/module/bp/equipt-standard-sets/{id}",
            "/secure/emc/module/bp/equipt-sw-sets/{id}",
            "/secure/emc/module/bp/equipt-temp-sets/{id}"
            }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request,
            @PathVariable("id") String id) {
        String resource = resource(request);
        try {
            Descriptor descriptor = descriptor(resource);
            requireRead(request);
            long numericId = numericId(id);
            return ok(JSON.toJSONString(selectDetail(descriptor, numericId)));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {
            "/secure/emc/module/bp/equipt-crves-detail/{id}/action/print",
            "/secure/emc/module/bp/equipt-crves/{id}/action/print",
            "/secure/emc/module/bp/equipt-dioxin-sets/{id}/action/print",
            "/secure/emc/module/bp/equipt-flow-sets/{id}/action/print",
            "/secure/emc/module/bp/equipt-sets/{id}/action/print",
            "/secure/emc/module/bp/equipt-standard-sets/{id}/action/print",
            "/secure/emc/module/bp/equipt-sw-sets/{id}/action/print",
            "/secure/emc/module/bp/equipt-temp-sets/{id}/action/print"
            }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> print(HttpServletRequest request,
            @PathVariable("id") String id) {
        return detail(request, id);
    }

    @GetMapping(path = {
            "/secure/emc/module/bp/equipt-crves-detail/{id}/columns/{column}",
            "/secure/emc/module/bp/equipt-crves/{id}/columns/{column}",
            "/secure/emc/module/bp/equipt-dioxin-sets/{id}/columns/{column}",
            "/secure/emc/module/bp/equipt-flow-sets/{id}/columns/{column}",
            "/secure/emc/module/bp/equipt-sets/{id}/columns/{column}",
            "/secure/emc/module/bp/equipt-standard-sets/{id}/columns/{column}",
            "/secure/emc/module/bp/equipt-sw-sets/{id}/columns/{column}",
            "/secure/emc/module/bp/equipt-temp-sets/{id}/columns/{column}"
            }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> column(HttpServletRequest request,
            @PathVariable("id") String id, @PathVariable("column") String column) {
        String resource = resource(request);
        try {
            Descriptor descriptor = descriptor(resource);
            requireRead(request);
            String sqlColumn = descriptor.column(column);
            try (Connection connection = dataSource.getConnection();
                    PreparedStatement select = connection.prepareStatement(
                            "SELECT " + sqlColumn + " FROM " + descriptor.table + " WHERE ID=?")) {
                select.setLong(1, numericId(id));
                try (ResultSet rows = select.executeQuery()) {
                    return ok(JSON.toJSONString(rows.next() ? dateValue(rows.getObject(1)) : null));
                }
            }
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {
            "/secure/emc/module/bp/equipt-crves-detail/{id}/info/status",
            "/secure/emc/module/bp/equipt-crves/{id}/info/status",
            "/secure/emc/module/bp/equipt-dioxin-sets/{id}/info/status",
            "/secure/emc/module/bp/equipt-flow-sets/{id}/info/status",
            "/secure/emc/module/bp/equipt-sets/{id}/info/status",
            "/secure/emc/module/bp/equipt-standard-sets/{id}/info/status",
            "/secure/emc/module/bp/equipt-sw-sets/{id}/info/status",
            "/secure/emc/module/bp/equipt-temp-sets/{id}/info/status"
            }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detailStatus(HttpServletRequest request,
            @PathVariable("id") String id) {
        String resource = resource(request);
        try {
            descriptor(resource);
            requireRead(request);
            numericId(id);
            return ok("{\"ext$\":{}}");
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private static String resource(HttpServletRequest request) {
        String path = request.getRequestURI();
        String prefix = "/secure/emc/module/bp/";
        if (path == null || !path.startsWith(prefix)) throw new IllegalArgumentException("equipment resource path is invalid");
        int start = prefix.length();
        int slash = path.indexOf('/', start);
        if (slash <= start) throw new IllegalArgumentException("equipment resource path is invalid");
        return path.substring(start, slash);
    }

    private void requireRead(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.EQUIPMENT_READ);
    }

    private String selectPage(Descriptor descriptor, QueryPage page) throws SQLException {
        String base = "SELECT T.* FROM " + descriptor.table + " T";
        String countSql = "SELECT COUNT(1) FROM (" + base + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*, ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM ("
                + base + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        try (Connection connection = dataSource.getConnection()) {
            int total;
            try (PreparedStatement count = connection.prepareStatement(countSql);
                    ResultSet rows = count.executeQuery()) {
                if (!rows.next()) throw new SQLException("equipment resource count returned no row");
                total = rows.getInt(1);
            }
            List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
            Map<String, String> types = new LinkedHashMap<String, String>();
            try (PreparedStatement select = connection.prepareStatement(pageSql)) {
                select.setInt(1, page.start());
                select.setInt(2, page.end());
                try (ResultSet rows = select.executeQuery()) {
                    ResultSetMetaData metadata = rows.getMetaData();
                    for (int i = 1; i <= metadata.getColumnCount(); i++) {
                        types.put(metadata.getColumnLabel(i).toLowerCase(Locale.ROOT), typeName(metadata.getColumnType(i)));
                    }
                    while (rows.next()) values.add(row(rows, metadata, descriptor));
                }
            }
            Map<String, Object> envelope = new LinkedHashMap<String, Object>();
            envelope.put("columnTypeMap", types);
            envelope.put("first", page.number == 1);
            envelope.put("last", total == 0 || page.number >= pages(total, page.size));
            envelope.put("numberOfElements", values.size());
            envelope.put("pageNumber", page.number);
            envelope.put("pageSize", page.size);
            envelope.put("rows", values);
            envelope.put("total", total);
            Map<String, Object> totalMap = new LinkedHashMap<String, Object>();
            totalMap.put("qty_", total);
            envelope.put("totalMap", totalMap);
            envelope.put("totalPages", pages(total, page.size));
            return JSON.toJSONString(envelope);
        }
    }

    private Map<String, Object> selectDetail(Descriptor descriptor, long id) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement select = connection.prepareStatement(
                        "SELECT T.* FROM " + descriptor.table + " T WHERE T.ID=?")) {
            select.setLong(1, id);
            try (ResultSet rows = select.executeQuery()) {
                if (!rows.next()) return Collections.emptyMap();
                return row(rows, rows.getMetaData(), descriptor);
            }
        }
    }

    private static Map<String, Object> row(ResultSet rows, ResultSetMetaData metadata,
            Descriptor descriptor) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        Map<String, Object> extension = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i);
            Object item = wireValue(label, dateValue(JdbcValue.normalize(rows.getObject(i))));
            if (item == null) continue;
            if ("RN_".equalsIgnoreCase(label)) {
                extension.put("rn_", item == null ? null : String.valueOf(item));
            } else {
                String wire = descriptor.wire(label);
                if (wire == null) extension.put(label.toLowerCase(Locale.ROOT), item);
                else value.put(wire, item);
            }
        }
        if (!extension.isEmpty()) value.put("ext$", extension);
        return value;
    }

    private static Object dateValue(Object value) {
        if (value instanceof Date) return new SimpleDateFormat("yyyy-MM-dd").format((Date) value);
        return value;
    }

    private static Object wireValue(String label, Object value) {
        if (value == null) return null;
        String key = label.toUpperCase(Locale.ROOT);
        if ("TESTMETHODID".equals(key) || "ITEMGROUPID".equals(key) || "METHODID".equals(key)) {
            try { return Long.valueOf(String.valueOf(value)); }
            catch (NumberFormatException ignored) { return value; }
        }
        if ("SORTNO".equals(key)) return String.valueOf(value);
        return value;
    }

    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }
    private static long numericId(String id) {
        if (id == null || !id.matches("[0-9]{1,30}")) throw new IllegalArgumentException("equipment resource id is invalid");
        return Long.parseLong(id);
    }
    private static String typeName(int type) {
        if (type == Types.DATE || type == Types.TIMESTAMP || type == Types.TIMESTAMP_WITH_TIMEZONE) return "dateTime";
        if (type == Types.NUMERIC || type == Types.DECIMAL || type == Types.INTEGER || type == Types.BIGINT) return "number";
        if (type == Types.BOOLEAN || type == Types.BIT) return "boolean";
        return "string";
    }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}"); }

    private static Descriptor descriptor(String resource) {
        Descriptor value = RESOURCES.get(resource);
        if (value == null) throw new IllegalArgumentException("unsupported equipment resource");
        return value;
    }

    private static Map<String, Descriptor> descriptors() {
        Map<String, Descriptor> values = new LinkedHashMap<String, Descriptor>();
        values.put("equipt-crves-detail", new Descriptor("T_EMC_ANALYTICAL_EQUIPT_DETAIL",
                map("ID", "id", "RUNID", "runId", "SOLID", "solId", "SDSUBSTANCES", "sdSubstances",
                        "ANALYSISNUMBER", "analysisNumber", "SDSOLUTIONVOLUME", "sdSolutionVolume",
                        "SDCONCENTRATION", "sdConcentration", "REMARK", "remark", "QCTYPE", "qcType",
                        "STDSUBSTANCECONTENT", "stdSubstanceContent", "STDSUBSTANCECONTENTVOL", "stdSubstanceContentVol",
                        "CREATEDBYID", "createdById", "CREATEDBYNAME", "createdByName", "CREATEDTIME", "createdTime",
                        "CREATEDBYORGID", "createdByOrgId", "CREATEDBYORGNAME", "createdByOrgName")));
        values.put("equipt-crves", new Descriptor("T_EMC_ANALYTICAL_EQUIPT_CRVES",
                map("ID", "id", "RUNID", "runId", "SDRESERVECONCENTRATION", "sdReserveConcentration", "SOLID", "solId",
                        "SDUSEFLUIDCONCENTRATION", "sdUseFluidConcentration", "CURVEFACTORFIXEDTO", "curveFactorFixedTo",
                        "ADDDATE", "addDate", "EXPIRATIONTIME", "expirationTime", "SETDATE", "setDate", "ADDER", "adder",
                        "CREATEDBYID", "createdById", "CREATEDBYNAME", "createdByName", "CREATEDTIME", "createdTime",
                        "CREATEDBYORGID", "createdByOrgId", "CREATEDBYORGNAME", "createdByOrgName", "ITEMGROUP", "itemGroup",
                        "ITEMGROUPID", "itemGroupId", "TESTMETHODID", "testMethodId", "TESTNAME", "testName", "METHODID", "methodId",
                        "METHODNAME", "methodName", "ISABANDON", "isAbandon")));
        values.put("equipt-dioxin-sets", new Descriptor("T_EMC_EQUIPT_DIOXIN_SET",
                map("ID", "id", "SAMPLEID", "sampleId", "EQUIPTID", "equiptId", "EQUIPTNO", "equiptNo", "EQUIPTNAME", "equiptName",
                        "SPECIFICATION", "specification", "BEFORETEN", "beforeTen", "BEFORETWENTY", "beforeTwenty", "BEFOREFIFTY", "beforeFifty",
                        "BEFOREDATETIME", "beforeDateTime", "AFTERTEN", "afterTen", "AFTERTWENTY", "afterTwenty", "AFTERFIFTY", "afterFifty",
                        "AFTERDATETIME", "afterDateTime", "CONCLUSION", "conclusion", "CREATEDBYID", "createdById", "CREATEDBYNAME", "createdByName",
                        "CREATEDTIME", "createdTime", "CREATEDBYORGID", "createdByOrgId", "CREATEDBYORGNAME", "createdByOrgName", "BIZTYPE", "bizType")));
        values.put("equipt-flow-sets", new Descriptor("T_EMC_EQUIPT_FLOW_SET",
                map("ID", "id", "SAMPLEID", "sampleId", "EQUIPTID", "equiptId", "EQUIPTNO", "equiptNo", "EQUIPTNAME", "equiptName",
                        "SPECIFICATION", "specification", "EQUIPTINTERFACE", "equiptInterface", "AIRTIGHT", "airtight", "FLOWUNIT", "flowUnit",
                        "BEFORESAMPLEFLOW", "beforeSampleFlow", "BEFORESETFLOW", "beforeSetFlow", "BEFOREERROR", "beforeError", "BEFOREJUDGE", "beforeJudge",
                        "AFTERSAMPLEFLOW", "afterSampleFlow", "AFTERSETFLOW", "afterSetFlow", "AFTERERROR", "afterError", "AFTERJUDGE", "afterJudge",
                        "ERRORVALUE", "errorValue", "JUDGE", "judge", "BIZTYPE", "bizType", "CREATEDBYID", "createdById", "CREATEDBYNAME", "createdByName",
                        "CREATEDTIME", "createdTime", "CREATEDBYORGID", "createdByOrgId", "CREATEDBYORGNAME", "createdByOrgName")));
        values.put("equipt-sets", new Descriptor("T_EMC_EQUIPT_SET",
                map("ID", "id", "NAME", "name", "SAMPLEID", "sampleId", "ALLOCATIONDATE", "allocationDate", "CUSHIONNO", "cushionNo",
                        "CUSHIONNAME", "cushionName", "TEMPERATURE", "temperature", "RESULT", "result", "CREATEDBYID", "createdById",
                        "CREATEDBYNAME", "createdByName", "CREATEDTIME", "createdTime", "CREATEDBYORGID", "createdByOrgId", "CREATEDBYORGNAME", "createdByOrgName")));
        values.put("equipt-standard-sets", new Descriptor("T_EMC_EQUIPT_STANDARD_SET",
                map("ID", "id", "SAMPLEID", "sampleId", "GASNAME", "gasName", "GASNO", "gasNo", "VALIDDATE", "validDate",
                        "GASTYPE", "gasType", "DENSITY", "density", "BEFORETESTA", "beforeTestA", "BEFORETESTB", "beforeTestB",
                        "BEFOREERROR", "beforeError", "BEFORESYSERROR", "beforeSysError", "AFTERTESTA", "afterTestA", "AFTERTESTB", "afterTestB",
                        "AFTERERROR", "afterError", "AFTERSYSERROR", "afterSysError", "CONCLUSION", "conclusion", "BIZTYPE", "bizType",
                        "CREATEDBYID", "createdById", "CREATEDBYNAME", "createdByName", "CREATEDTIME", "createdTime",
                        "CREATEDBYORGID", "createdByOrgId", "CREATEDBYORGNAME", "createdByOrgName")));
        values.put("equipt-sw-sets", new Descriptor("T_EMC_EQUIPT_SW_SET",
                map("ID", "id", "SAMPLEID", "sampleId", "TEMPERATUREONE", "temperatureOne", "TEMPERATURETWO", "temperatureTwo",
                        "TEMPDIFF", "tempDiff", "MEASURESAFE", "measureSafe", "CHECKDIFF", "checkDiff", "CHECKSAFE", "checkSafe",
                        "CREATEDBYID", "createdById", "CREATEDBYNAME", "createdByName", "CREATEDTIME", "createdTime",
                        "CREATEDBYORGID", "createdByOrgId", "CREATEDBYORGNAME", "createdByOrgName", "ORDERID", "orderId", "ORDERNO", "orderNo")));
        values.put("equipt-temp-sets", new Descriptor("T_EMC_EQUIPT_TEMP_SET",
                map("ID", "id", "SAMPLEID", "sampleId", "RESISTANCE", "resistance", "TEMPERATURE", "temperature", "CHECKDIFF", "checkDiff",
                        "CHECKSAFE", "checkSafe", "EQUIPTID", "equiptId", "EQUIPTNO", "equiptNo", "EQUIPTNAME", "equiptName",
                        "SPECIFICATION", "specification", "CREATEDBYID", "createdById", "CREATEDBYNAME", "createdByName", "CREATEDTIME", "createdTime",
                        "CREATEDBYORGID", "createdByOrgId", "CREATEDBYORGNAME", "createdByOrgName")));
        return Collections.unmodifiableMap(values);
    }

    private static Map<String, String> map(String... values) {
        Map<String, String> result = new LinkedHashMap<String, String>();
        for (int i = 0; i < values.length; i += 2) result.put(values[i], values[i + 1]);
        return result;
    }

    private static final class Descriptor {
        final String table;
        final Map<String, String> columns;
        Descriptor(String table, Map<String, String> columns) { this.table = table; this.columns = columns; }
        String wire(String label) { return columns.get(label.toUpperCase(Locale.ROOT)); }
        String column(String name) {
            String key = name == null ? "" : name.toUpperCase(Locale.ROOT);
            String wire = columns.get(key);
            if (wire == null) throw new IllegalArgumentException("unsupported equipment resource column");
            return key;
        }
    }

    private static final class QueryPage {
        final int number; final int size;
        QueryPage(int number, int size) { this.number = number; this.size = size; }
        static QueryPage parse(String body) {
            JSONObject root = body == null || body.trim().isEmpty() ? new JSONObject() : JSON.parseObject(body);
            JSONObject p = root == null ? null : root.getJSONObject("p");
            int n = p == null ? 1 : positive(p.getIntValue("n"), 1);
            int s = p == null ? 50 : positive(p.getIntValue("s"), 50);
            if (s > 500) s = 500;
            return new QueryPage(n, s);
        }
        int start() { return (number - 1) * size; }
        int end() { return number * size; }
        private static int positive(int value, int fallback) { return value > 0 ? value : fallback; }
    }
}
