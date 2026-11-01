package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.owned.internal.data.JdbcValue;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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

/** Owned read seam for quality-control chart status and selectable lists. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedQualityControlChartController {
    private static final String PREFIX = "/secure/emc/module/sa/quality-control-charts";
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedQualityControlChartController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("quality chart dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @GetMapping(path = {PREFIX + "/info/status", PREFIX + "/{id}/info/status"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { require(request); return ok("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = PREFIX + "/items/queries/selectable", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> items(HttpServletRequest request, @RequestBody(required = false) String body) {
        return selectable(request, "SELECT DISTINCT A.ITEMID,A.ITEMNAME,A.TESTID FROM T_EMC_ORDER_TASK_RESULT A WHERE A.REPORTDISPLAY='1' ORDER BY A.ITEMID");
    }

    @PostMapping(path = PREFIX + "/tests/queries/selectable", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> tests(HttpServletRequest request, @RequestBody(required = false) String body) {
        return selectable(request, "SELECT DISTINCT A.TESTID,A.TESTNAME,B.TESTCATEGORYID FROM T_EMC_ORDER_TASK A LEFT JOIN T_EMC_TEST_METHOD B ON B.ID=A.TESTMETHODID WHERE A.ANALYSISORGID=? ORDER BY A.TESTID");
    }

    @PostMapping(path = PREFIX + "/qc-categories/queries/selectable", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> categories(HttpServletRequest request, @RequestBody(required = false) String body) {
        return selectable(request, "SELECT DISTINCT A.QCCATEGORYID,A.QCCATEGORYCODE,A.TESTID,A.QCNAME,B.ITEMID FROM T_EMC_ORDER_TASK A LEFT JOIN T_EMC_ORDER_TASK_RESULT B ON B.ORDERTASKID=A.ID WHERE A.ANALYSISORGID=? AND A.QCCATEGORYID IS NOT NULL ORDER BY A.QCCATEGORYCODE");
    }

    @PostMapping(path = PREFIX + "/users/queries/selectable", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> users(HttpServletRequest request, @RequestBody(required = false) String body) {
        return selectable(request, "SELECT DISTINCT A.INPUTUSERID,A.INPUTUSERNAME,A.TESTID,A.QCCATEGORYID,B.ITEMID FROM T_EMC_ORDER_TASK A LEFT JOIN T_EMC_ORDER_TASK_RESULT B ON B.ORDERTASKID=A.ID WHERE A.ANALYSISORGID=? ORDER BY A.INPUTUSERID");
    }

    private ResponseEntity<String> selectable(HttpServletRequest request, String sql) {
        try {
            OwnedPrincipal principal = require(request);
            List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
            try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql)) {
                if (sql.contains("ANALYSISORGID=?")) query.setString(1, principal.getOrgId());
                try (ResultSet result = query.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    while (result.next()) rows.add(row(result, metadata));
                }
            }
            return ok(JSON.toJSONString(rows));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }
    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) value.put(camel(metadata.getColumnLabel(i)), JdbcValue.normalize(result.getObject(i)));
        return value;
    }
    private static String camel(String name) {
        StringBuilder value = new StringBuilder(); boolean upper = false;
        for (char c : name.toLowerCase(Locale.ROOT).toCharArray()) { if (c == '_') { upper = true; continue; } value.append(upper ? Character.toUpperCase(c) : c); upper = false; }
        return value.toString();
    }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().header("Cache-Control", "no-store").body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.QUALITY_CONTROL_CHART.UNAVAILABLE\"}"); }
}
