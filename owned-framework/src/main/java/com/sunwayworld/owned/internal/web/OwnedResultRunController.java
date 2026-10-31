package com.sunwayworld.owned.internal.web;

import com.sunwayworld.owned.internal.data.JdbcValue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
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
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/** Bounded Oracle read seam for the historical sample-test-run list. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedResultRunController {
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedResultRunController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("result dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource;
    }

    @PostMapping(path = "/secure/core/module/bp/result/runs/queries/raw",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request,
            @org.springframework.web.bind.annotation.RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            JSONObject filter = object(body);
            return ok(page(readRuns(principal.getOrgId(), text(filter.get("runNo")), text(filter.get("status")))));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = "/secure/core/module/bp/result/runs/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request, @PathVariable("id") String id) {
        try {
            OwnedPrincipal principal = require(request);
            if (id == null || !id.matches("[A-Za-z0-9_-]+")) throw new IllegalArgumentException("result run id is invalid");
            List<Map<String, Object>> rows = readRunDetail(principal.getOrgId(), id);
            return ok(JSON.toJSONString(rows.isEmpty() ? new LinkedHashMap<String, Object>() : rows.get(0), SerializerFeature.WriteMapNullValue));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requireResultRead(request.getSession(false));
        return boundary.currentPrincipal(request.getSession(false));
    }

    private List<Map<String, Object>> readRuns(String organization, String runNo, String status) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        StringBuilder sql = new StringBuilder("SELECT * FROM T_EMC_RUN WHERE ORGID=?");
        List<String> values = new ArrayList<String>(); values.add(organization);
        // The cloned production table has no RUNNO column.  The original
        // selector labels this input as a run number, but the stable owned
        // seam can safely resolve it against the real ID column.
        if (runNo != null && runNo.length() != 0) { sql.append(" AND ID=?"); values.add(runNo); }
        if (status != null && status.length() != 0) { sql.append(" AND STATUS=?"); values.add(status); }
        sql.append(" ORDER BY ID DESC");
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < values.size(); i++) query.setString(i + 1, values.get(i));
            try (ResultSet result = query.executeQuery()) {
                ResultSetMetaData meta = result.getMetaData();
                while (result.next()) {
                    Map<String, Object> row = new LinkedHashMap<String, Object>();
                    for (int i = 1; i <= meta.getColumnCount(); i++) row.put(friendly(meta.getColumnLabel(i)), JdbcValue.normalize(result.getObject(i)));
                    rows.add(row);
                }
            }
        }
        return rows;
    }

    private List<Map<String, Object>> readRunDetail(String organization, String id) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        // The production table contains many large CLOB columns.  Detail
        // consumers only need the stable identity/status/org projection;
        // selecting the whole row makes a single oversized/driver-specific
        // CLOB turn an otherwise valid detail request into a 500 response.
        String sql = "SELECT ID,STATUS,ORGID FROM T_EMC_RUN WHERE ORGID=? AND ID=?";
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql)) {
            query.setString(1, organization);
            query.setString(2, id);
            try (ResultSet result = query.executeQuery()) {
                if (result.next()) {
                    Map<String, Object> row = new LinkedHashMap<String, Object>();
                    row.put("id", result.getString(1));
                    row.put("status", result.getString(2));
                    row.put("orgId", result.getString(3));
                    rows.add(row);
                }
            }
        }
        return rows;
    }

    private static String friendly(String name) {
        String upper = name.toUpperCase();
        if ("ID".equals(upper)) return "id";
        if ("RUNNO".equals(upper)) return "runNo";
        if ("ORGID".equals(upper)) return "orgId";
        if ("STATUS".equals(upper)) return "status";
        if ("EQUIPTID".equals(upper)) return "equiptId";
        if ("TESTID".equals(upper)) return "testId";
        StringBuilder result = new StringBuilder(); boolean upperNext = false;
        for (char c : name.toLowerCase().toCharArray()) {
            if (c == '_') { upperNext = true; continue; }
            result.append(upperNext ? Character.toUpperCase(c) : c); upperNext = false;
        }
        return result.toString();
    }

    private static JSONObject object(String body) {
        if (body == null || body.trim().length() == 0) return new JSONObject();
        Object value = JSON.parse(body); if (!(value instanceof JSONObject)) throw new IllegalArgumentException("request must be an object");
        JSONObject object = (JSONObject) value; return object.containsKey("p") ? object.getJSONObject("p") : object;
    }
    private static String text(Object value) { return value == null ? "" : String.valueOf(value).trim(); }
    private static String page(List<Map<String, Object>> rows) { Map<String, Object> body = new LinkedHashMap<String, Object>(); body.put("total", rows.size()); body.put("pageNumber", 1); body.put("pageSize", rows.size()); body.put("rows", rows); return JSON.toJSONString(body, SerializerFeature.WriteMapNullValue); }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().header("Cache-Control", "no-store").body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.RESULT.INVALID_REQUEST\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.RESULT.UNAVAILABLE\"}"); }
}
