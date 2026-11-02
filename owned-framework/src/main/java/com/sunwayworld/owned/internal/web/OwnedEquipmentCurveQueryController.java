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

/** Owned read seam for the equipment-curve and curve-standard resources. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedEquipmentCurveQueryController {
    private static final String CURVE = "/secure/emc/module/bp/result/equipt-curves";
    private static final String STANDARD = "/secure/emc/module/bp/result/equipt-curve-standards";
    private static final String CURVE_SOURCE =
            "SELECT T.* FROM (SELECT A.*,B.EQUIPTID AS EQUIPTNO,B.EQUIPTNAME "
            + "FROM T_EMC_EQUIPT_CURVE A LEFT JOIN T_EMC_EQUIPT B ON A.EQUIPTID=B.ID) T "
            + "WHERE T.CREATEDBYORGID=?";
    private static final String STANDARD_SOURCE = "SELECT T.* FROM T_EMC_EQUIPT_CURVE_STANDARD T WHERE T.CREATEDBYORGID=?";
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedEquipmentCurveQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("equipment-curve dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @GetMapping(path = {CURVE + "/info/status", STANDARD + "/info/status"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { require(request); return ok("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = {CURVE + "/queries", CURVE + "/queries/raw", CURVE + "/queries/searchable",
            STANDARD + "/queries", STANDARD + "/queries/raw", STANDARD + "/queries/searchable"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            boolean standard = request.getRequestURI().startsWith(STANDARD + "/");
            return ok(selectPage(principal.getOrgId(), Page.parse(body), standard));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private String selectPage(String organization, Page page, boolean standard) throws SQLException {
        String source = standard ? STANDARD_SOURCE : CURVE_SOURCE;
        String countSql = "SELECT COUNT(1) FROM (" + source + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM ("
                + source + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total;
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, String> types = new LinkedHashMap<String, String>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(countSql)) {
                statement.setString(1, organization);
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) throw new SQLException("equipment curve count returned no row");
                    total = result.getInt(1);
                }
            }
            try (PreparedStatement statement = connection.prepareStatement(pageSql)) {
                statement.setString(1, organization);
                statement.setInt(2, page.start());
                statement.setInt(3, page.end());
                try (ResultSet result = statement.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    for (int i = 1; i <= metadata.getColumnCount(); i++) {
                        String label = metadata.getColumnLabel(i);
                        if (!"RN_".equalsIgnoreCase(label)) types.put(camel(label), type(metadata.getColumnType(i)));
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
        Map<String, Object> totals = new LinkedHashMap<String, Object>();
        totals.put("qty_", total);
        envelope.put("totalMap", totals);
        envelope.put("totalPages", pages(total, page.size));
        return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i);
            if (!"RN_".equalsIgnoreCase(label)) value.put(camel(label), JdbcValue.normalize(result.getObject(i)));
        }
        value.put("ext$", new LinkedHashMap<String, Object>());
        return value;
    }

    private static String camel(String label) {
        StringBuilder result = new StringBuilder();
        boolean upper = false;
        for (char value : label.toLowerCase(Locale.ROOT).toCharArray()) {
            if (value == '_') { upper = true; continue; }
            result.append(upper ? Character.toUpperCase(value) : value);
            upper = false;
        }
        return result.toString();
    }

    private static String type(int sqlType) {
        return sqlType == Types.DATE || sqlType == Types.TIMESTAMP || sqlType == Types.TIMESTAMP_WITH_TIMEZONE
                ? "dateTime" : (sqlType == Types.NUMERIC || sqlType == Types.DECIMAL || sqlType == Types.INTEGER
                || sqlType == Types.BIGINT ? "number" : "string");
    }

    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.EQUIPMENT_CURVE.INVALID_QUERY\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.EQUIPMENT_CURVE.UNAVAILABLE\"}"); }

    private static final class Page {
        final int number, size;
        Page(int number, int size) { this.number = number; this.size = size; }
        static Page parse(String body) {
            JSONObject root = body == null || body.trim().isEmpty() ? new JSONObject() : JSON.parseObject(body);
            JSONObject page = root == null ? null : root.getJSONObject("p");
            int number = page == null ? 1 : Math.max(1, page.getIntValue("n"));
            int size = page == null ? 50 : Math.min(500, Math.max(1, page.getIntValue("s")));
            return new Page(number, size);
        }
        int start() { return (number - 1) * size; }
        int end() { return number * size; }
    }
}
