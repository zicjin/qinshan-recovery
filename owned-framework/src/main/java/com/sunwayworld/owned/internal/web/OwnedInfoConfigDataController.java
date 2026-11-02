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

/** Owned read seams for the info-config and info-data GenericResources. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedInfoConfigDataController {
    private static final String CONFIG_PREFIX = "/secure/emc/module/bp/info/info-configs";
    private static final String DATA_PREFIX = "/secure/emc/module/bp/info/info-datas";
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedInfoConfigDataController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("info config/data dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @PostMapping(path = {CONFIG_PREFIX + "/queries", CONFIG_PREFIX + "/queries/raw", CONFIG_PREFIX + "/queries/searchable"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> configQuery(HttpServletRequest request, @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            Query query = Query.parse(body);
            String extra = "";
            List<String> values = new ArrayList<String>();
            if (query.parentId != null) {
                if ("root".equalsIgnoreCase(query.parentId)) extra = " AND T.PARENTID IS NULL";
                else { extra = " AND T.PARENTID=?"; values.add(query.parentId); }
            }
            return ok(selectPage("SELECT T.* FROM T_EMC_INFO_CONFIG T WHERE T.CREATEDBYORGID=?" + extra,
                    "T.ORDERNO", principal.getOrgId(), values, query.page));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = {DATA_PREFIX + "/queries", DATA_PREFIX + "/queries/raw", DATA_PREFIX + "/queries/searchable"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> dataQuery(HttpServletRequest request, @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            Query query = Query.parse(body);
            StringBuilder extra = new StringBuilder();
            List<String> values = new ArrayList<String>();
            if (query.year != null && query.month != null) {
                extra.append(" AND TO_CHAR(T.MONITORDATE,'YYYYMM')=?");
                values.add(query.year + query.month);
            } else if (query.year != null) {
                extra.append(" AND TO_CHAR(T.MONITORDATE,'YYYY')=?");
                values.add(query.year);
            } else if (query.month != null) {
                extra.append(" AND TO_CHAR(T.MONITORDATE,'MM')=?");
                values.add(query.month);
            }
            String base = "SELECT T.* FROM (SELECT DISTINCT A.*, B.MONITORPOINTNAME FROM T_EMC_INFO_DATA A "
                    + "LEFT JOIN T_EMC_INFO_DATA_ITEM B ON B.INFODATAID=A.ID) T WHERE T.CREATEDBYORGID=?" + extra;
            return ok(selectPage(base, "T.ID DESC", principal.getOrgId(), values, query.page));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {CONFIG_PREFIX + "/info/status", DATA_PREFIX + "/info/status"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { require(request); return ok("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private String selectPage(String base, String order, String organization, List<String> extraValues, Page page) throws SQLException {
        String countSql = "SELECT COUNT(1) FROM (" + base + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY " + order + ") RN_ FROM (" + base + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total;
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, String> types = new LinkedHashMap<String, String>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement count = connection.prepareStatement(countSql)) {
                int index = bind(count, organization, extraValues, 1);
                if (index < 1) throw new SQLException("info count binding failed");
                try (ResultSet result = count.executeQuery()) {
                    if (!result.next()) throw new SQLException("info count unavailable");
                    total = result.getInt(1);
                }
            }
            try (PreparedStatement query = connection.prepareStatement(pageSql)) {
                int index = bind(query, organization, extraValues, 1);
                query.setInt(index++, page.start());
                query.setInt(index, page.end());
                try (ResultSet result = query.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    for (int i = 1; i <= metadata.getColumnCount(); i++) {
                        types.put(metadata.getColumnLabel(i).toLowerCase(Locale.ROOT), type(metadata.getColumnType(i)));
                    }
                    while (result.next()) rows.add(row(result, metadata));
                }
            }
        }
        Map<String, Object> envelope = new LinkedHashMap<String, Object>();
        envelope.put("columnTypeMap", types);
        envelope.put("first", page.number == 1);
        envelope.put("last", total == 0 || page.number >= pages(total, page.size));
        envelope.put("numberOfElements", rows.size());
        envelope.put("pageNumber", page.number);
        envelope.put("pageSize", page.size);
        envelope.put("rows", rows);
        envelope.put("total", total);
        Map<String, Object> totalMap = new LinkedHashMap<String, Object>();
        totalMap.put("qty_", total);
        envelope.put("totalMap", totalMap);
        envelope.put("totalPages", pages(total, page.size));
        return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    private static int bind(PreparedStatement statement, String organization, List<String> values, int index) throws SQLException {
        statement.setString(index++, organization);
        for (String value : values) statement.setString(index++, value);
        return index;
    }

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i);
            if ("RN_".equalsIgnoreCase(label)) continue;
            Object item = JdbcValue.normalize(result.getObject(i));
            if (item != null) value.put(camel(label), item);
        }
        return value;
    }

    private static String camel(String label) {
        StringBuilder value = new StringBuilder();
        boolean upper = false;
        for (char c : label.toLowerCase(Locale.ROOT).toCharArray()) {
            if (c == '_') { upper = true; continue; }
            value.append(upper ? Character.toUpperCase(c) : c);
            upper = false;
        }
        return value.toString();
    }

    private static String type(int type) {
        if (type == Types.DATE || type == Types.TIMESTAMP || type == Types.TIMESTAMP_WITH_TIMEZONE) return "dateTime";
        if (type == Types.NUMERIC || type == Types.DECIMAL || type == Types.INTEGER || type == Types.BIGINT) return "number";
        return "string";
    }

    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}"); }

    private static final class Query {
        final Page page;
        final String parentId;
        final String year;
        final String month;
        Query(Page page, String parentId, String year, String month) { this.page = page; this.parentId = parentId; this.year = year; this.month = month; }
        static Query parse(String body) {
            JSONObject root = body == null || body.trim().isEmpty() ? new JSONObject() : JSON.parseObject(body);
            JSONObject p = root == null ? null : root.getJSONObject("p");
            int number = p == null ? 1 : Math.max(1, p.getIntValue("n"));
            int size = p == null ? 50 : Math.min(500, Math.max(1, p.getIntValue("s")));
            return new Query(new Page(number, size), text(root, "parentId"), text(root, "year"), text(root, "month"));
        }
        private static String text(JSONObject root, String key) {
            if (root == null || root.get(key) == null) return null;
            String value = String.valueOf(root.get(key)).trim();
            return value.isEmpty() ? null : value;
        }
    }

    private static final class Page {
        final int number, size;
        Page(int number, int size) { this.number = number; this.size = size; }
        int start() { return (number - 1) * size; }
        int end() { return number * size; }
    }
}
