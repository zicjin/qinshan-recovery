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
import java.sql.Types;
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
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping(path = {
            "/secure/core/module/bp/result/runs/queries",
            "/secure/core/module/bp/result/runs/queries/raw",
            "/secure/core/module/bp/result/runs/queries/searchable",
            "/secure/core/module/bp/result/runs/queries/roles/{roleId}",
            "/secure/core/module/bp/result/runs/queries/tasks",
            "/secure/core/module/bp/result/runs/queries/undoable",
            "/secure/core/module/bp/result/runs/queries/withdrawable",
            "/secure/core/module/bp/result/runs/queries/all",
            "/secure/emc/module/bp/result/runs/queries",
            "/secure/emc/module/bp/result/runs/queries/raw",
            "/secure/emc/module/bp/result/runs/queries/searchable",
            "/secure/emc/module/bp/result/runs/queries/roles/{roleId}",
            "/secure/emc/module/bp/result/runs/queries/tasks",
            "/secure/emc/module/bp/result/runs/queries/undoable",
            "/secure/emc/module/bp/result/runs/queries/withdrawable",
            "/secure/emc/module/bp/result/runs/queries/all"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request,
            @PathVariable(value = "roleId", required = false) String roleId,
            @org.springframework.web.bind.annotation.RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            JSONObject filter = object(body);
            return ok(page(readRuns(principal.getOrgId(), text(filter.get("runNo")), text(filter.get("status"))), pageSize(filter)));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {"/secure/core/module/bp/result/runs/{id}",
            "/secure/emc/module/bp/result/runs/{id}"}, produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping(path = {"/secure/core/module/bp/result/runs/info/status",
            "/secure/core/module/bp/result/runs/{id}/info/status",
            "/secure/emc/module/bp/result/runs/info/status",
            "/secure/emc/module/bp/result/runs/{id}/info/status"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try {
            require(request);
            return ResponseEntity.ok().header("gikam-instant-save-path",
                    "/secure/emc/module/bp/result/runs/instant")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"auditStatus\":{},\"ext$\":{}}");
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requireResultRead(request.getSession(false));
        return boundary.currentPrincipal(request.getSession(false));
    }

    private QueryResult readRuns(String organization, String runNo, String status) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, String> types = new LinkedHashMap<String, String>();
        StringBuilder sql = new StringBuilder(
                "SELECT A.*,D.EQUIPTNAME,D.EQUIPTID AS EQUIPTCODE,M.MAXELNTMPLID,M.METHODNAME,"
                + "S1.SOLNAME AS SOL1NAME,S2.SOLNAME AS SOL2NAME,S1.SOLNO AS SOL1NO,S2.SOLNO AS SOL2NO,"
                + "R.REAGENTNAME FROM T_EMC_RUN A LEFT JOIN T_EMC_EQUIPT D ON A.EQUIPTID=D.ID "
                + "LEFT JOIN T_EMC_METHOD M ON A.METHODID=M.ID LEFT JOIN T_EMC_SOL S1 ON A.SOL1ID=S1.ID "
                + "LEFT JOIN T_EMC_SOL S2 ON A.SOL2ID=S2.ID LEFT JOIN T_EMC_REAGENT R ON A.REAGENTID=R.ID "
                + "WHERE A.ORGID=?");
        List<String> values = new ArrayList<String>(); values.add(organization);
        // The cloned production table has no RUNNO column.  The original
        // selector labels this input as a run number, but the stable owned
        // seam can safely resolve it against the real ID column.
        if (runNo != null && runNo.length() != 0) { sql.append(" AND A.ID=?"); values.add(runNo); }
        if (status != null && status.length() != 0) { sql.append(" AND A.STATUS=?"); values.add(status); }
        sql.append(" ORDER BY A.ID DESC");
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < values.size(); i++) query.setString(i + 1, values.get(i));
            try (ResultSet result = query.executeQuery()) {
                ResultSetMetaData meta = result.getMetaData();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    types.put(friendly(meta.getColumnLabel(i)), type(meta.getColumnType(i)));
                }
                while (result.next()) {
                    Map<String, Object> row = new LinkedHashMap<String, Object>();
                    for (int i = 1; i <= meta.getColumnCount(); i++) row.put(friendly(meta.getColumnLabel(i)), JdbcValue.normalize(result.getObject(i)));
                    rows.add(row);
                }
            }
        }
        return new QueryResult(rows, types);
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
    private static int pageSize(JSONObject filter) {
        int size = filter == null ? 50 : filter.getIntValue("s");
        return size <= 0 ? 50 : Math.min(size, 500);
    }
    private static String page(QueryResult result, int pageSize) {
        List<Map<String, Object>> rows = result.rows;
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("columnTypeMap", result.types);
        body.put("first", true); body.put("last", true);
        body.put("numberOfElements", rows.size()); body.put("pageNumber", 1);
        body.put("pageSize", pageSize); body.put("rows", rows);
        body.put("total", rows.size());
        Map<String, Object> totals = new LinkedHashMap<String, Object>(); totals.put("qty_", rows.size());
        body.put("totalMap", totals); body.put("totalPages", rows.isEmpty() ? 0 : 1);
        return JSON.toJSONString(body, SerializerFeature.WriteMapNullValue);
    }
    private static String type(int sqlType) {
        if (sqlType == Types.DATE || sqlType == Types.TIMESTAMP || sqlType == Types.TIMESTAMP_WITH_TIMEZONE) return "dateTime";
        if (sqlType == Types.NUMERIC || sqlType == Types.DECIMAL || sqlType == Types.INTEGER || sqlType == Types.BIGINT) return "number";
        return "string";
    }
    private static final class QueryResult {
        final List<Map<String, Object>> rows; final Map<String, String> types;
        QueryResult(List<Map<String, Object>> rows, Map<String, String> types) { this.rows = rows; this.types = types; }
    }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().header("Cache-Control", "no-store").body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.RESULT.INVALID_REQUEST\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.RESULT.UNAVAILABLE\"}"); }
}
