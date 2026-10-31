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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
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
 * Owned Java implementation for the two read-only GenericResource families
 * exposed by the original organization administration pages. Both resources
 * use the same Spring application/DataSource and preserve the supplier page
 * envelope; this controller is not a boundary process or bridge.
 */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedOrganizationMappingController {
    private static final String OA = "/secure/emc/module/mdm/oa-org-relations";
    private static final String PERMISSION = "/secure/emc/module/mdm/org-permissions";

    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedOrganizationMappingController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("organization mapping dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @GetMapping(path = {OA + "/info/status", PERMISSION + "/info/status"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try {
            require(request);
            return ok("{\"ext$\":{}}");
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = {OA + "/queries", OA + "/queries/raw", OA + "/queries/searchable",
            PERMISSION + "/queries", PERMISSION + "/queries/raw", PERMISSION + "/queries/searchable"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            require(request);
            PageRequest page = PageRequest.parse(body);
            String base = request.getRequestURI().startsWith(PERMISSION) ? permissionSql() : relationSql();
            return ok(selectPage(base, page));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private void require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.PERSONNEL_READ);
    }

    private String selectPage(String base, PageRequest page) throws SQLException {
        String countSql = "SELECT COUNT(1) FROM (" + base + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*, ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM ("
                + base + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        try (Connection connection = dataSource.getConnection()) {
            int total;
            try (PreparedStatement count = connection.prepareStatement(countSql);
                    ResultSet rows = count.executeQuery()) {
                if (!rows.next()) throw new SQLException("organization mapping count returned no row");
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
                    while (rows.next()) values.add(row(rows, metadata));
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
            return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
        }
    }

    private static String relationSql() {
        return "SELECT T.* FROM T_EMC_OA_ORG_RELATION T";
    }

    private static String permissionSql() {
        return "SELECT T.* FROM (SELECT A.*, (SELECT ORGNAME FROM (SELECT ORGNAME FROM T_CORE_ORG "
                + "WHERE ORGCATEGORY='company' START WITH ID=A.ORGID CONNECT BY PRIOR PARENTID=ID) "
                + "WHERE ROWNUM=1) AS COMP FROM T_EMC_ORG_PERMISSION A) T";
    }

    private static Map<String, Object> row(ResultSet rows, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        Map<String, Object> extension = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i);
            Object item = JdbcValue.normalize(rows.getObject(i));
            if ("RN_".equalsIgnoreCase(label)) {
                extension.put("rn_", item == null ? null : String.valueOf(item));
            } else if ("COMP".equalsIgnoreCase(label)) {
                extension.put("comp", item);
            } else {
                value.put(wireName(label), wireValue(label, item));
            }
        }
        if (!extension.isEmpty()) value.put("ext$", extension);
        return value;
    }

    private static String camel(String label) {
        StringBuilder result = new StringBuilder();
        boolean upper = false;
        for (char ch : label.toLowerCase(Locale.ROOT).toCharArray()) {
            if (ch == '_') { upper = true; continue; }
            result.append(upper ? Character.toUpperCase(ch) : ch); upper = false;
        }
        return result.toString();
    }

    private static String wireName(String label) {
        String key = label.toUpperCase(Locale.ROOT);
        if ("CREATEDBYID".equals(key)) return "createdById";
        if ("CREATEDBYNAME".equals(key)) return "createdByName";
        if ("CREATEDBYORGID".equals(key)) return "createdByOrgId";
        if ("CREATEDBYORGNAME".equals(key)) return "createdByOrgName";
        if ("CREATEDTIME".equals(key)) return "createdTime";
        if ("LIMSORGCODE".equals(key)) return "limsOrgCode";
        if ("OACWORGCODE".equals(key)) return "oaCwOrgCode";
        if ("OAGQORGCODE".equals(key)) return "oaGqOrgCode";
        if ("ORGID".equals(key)) return "orgId";
        if ("ORGNAME".equals(key)) return "orgName";
        if ("PERMISSIONTYPEID".equals(key)) return "permissionTypeId";
        if ("TARGETID".equals(key)) return "targetId";
        return camel(label);
    }

    private static Object wireValue(String label, Object value) {
        if (value instanceof Date && "CREATEDTIME".equalsIgnoreCase(label)) {
            return new SimpleDateFormat("yyyy-MM-dd").format((Date) value);
        }
        return value;
    }

    private static String typeName(int type) {
        if (type == Types.DATE || type == Types.TIMESTAMP || type == Types.TIMESTAMP_WITH_TIMEZONE) return "dateTime";
        if (type == Types.NUMERIC || type == Types.DECIMAL || type == Types.INTEGER || type == Types.BIGINT) return "number";
        if (type == Types.BOOLEAN || type == Types.BIT) return "boolean";
        return "string";
    }

    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }

    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}"); }

    private static final class PageRequest {
        final int number; final int size;
        private PageRequest(int number, int size) { this.number = number; this.size = size; }
        static PageRequest parse(String body) {
            int number = 1, size = 10;
            if (body != null && !body.trim().isEmpty()) {
                JSONObject object = JSON.parseObject(body);
                JSONObject page = object == null ? null : object.getJSONObject("p");
                if (page != null) {
                    number = positive(page.getIntValue("n"), 1);
                    size = positive(page.getIntValue("s"), 10);
                }
            }
            if (size > 500) size = 500;
            return new PageRequest(number, size);
        }
        int start() { return (number - 1) * size; }
        int end() { return number * size; }
        private static int positive(int value, int fallback) { return value > 0 ? value : fallback; }
    }
}
