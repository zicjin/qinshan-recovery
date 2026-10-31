package com.sunwayworld.owned.internal.web;

import com.sunwayworld.owned.internal.data.JdbcValue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import java.math.BigDecimal;
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
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Bounded owned seam for result-value entry and the observed result workflow states. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedResultItemController {
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;
    private final Environment environment;

    public OwnedResultItemController(OwnedIdentityHttpBoundary boundary, DataSource dataSource,
            Environment environment) {
        if (boundary == null || dataSource == null || environment == null)
            throw new IllegalArgumentException("result item dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
        this.environment = environment;
    }

    @PostMapping(path = "/secure/core/module/bp/result/runs/{runId}/results/queries",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request,
            @PathVariable("runId") String runId) {
        try {
            OwnedPrincipal principal = requireRead(request);
            String validRun = validId(runId);
            return ok(page(readResults(validRun, principal.getOrgId())));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PutMapping(path = "/secure/core/module/bp/result/runs/{runId}/results/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> update(HttpServletRequest request,
            @PathVariable("runId") String runId, @PathVariable("id") String id,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireWrite(request);
            String validRun = validId(runId);
            String validResult = validId(id);
            String value = parseValue(body);
            int updated = OwnedWriteSupport.transaction(dataSource, connection ->
                    updateValue(connection, validRun, validResult, principal.getOrgId(), value));
            boundary.recordAllowed("result-value-update", principal, validResult, "isolated-write");
            return ok("{\"updated\":" + updated + "}");
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(); }
        catch (SQLException failure) { denied(request); return unavailable(); }
    }

    @PostMapping(path = "/secure/core/module/bp/result/runs/{runId}/results/{id}/calculate",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> calculate(HttpServletRequest request,
            @PathVariable("runId") String runId, @PathVariable("id") String id) {
        try {
            OwnedPrincipal principal = requireWrite(request);
            String validRun = validId(runId);
            String validResult = validId(id);
            String calculated = OwnedWriteSupport.transaction(dataSource, connection ->
                    calculateValue(connection, validRun, validResult, principal.getOrgId()));
            boundary.recordAllowed("result-value-calculate", principal, validResult, "isolated-write");
            return ok(JSON.toJSONString(mapOf("reportResult", calculated, "status", "calculated")));
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(); }
        catch (SQLException failure) { denied(request); return unavailable(); }
    }

    @PutMapping(path = "/secure/core/module/bp/result/runs/{runId}/results/{id}/review",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> review(HttpServletRequest request,
            @PathVariable("runId") String runId, @PathVariable("id") String id) {
        return transition(request, runId, id, "reviewed", "result-review", "calculated", "entered");
    }

    @PutMapping(path = "/secure/core/module/bp/result/runs/{runId}/results/{id}/approve",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> approve(HttpServletRequest request,
            @PathVariable("runId") String runId, @PathVariable("id") String id) {
        return transition(request, runId, id, "approved", "result-approve", "reviewed");
    }

    private ResponseEntity<String> transition(HttpServletRequest request, String runId, String id,
            String next, String action, String... allowed) {
        try {
            OwnedPrincipal principal = requireWrite(request);
            String validRun = validId(runId);
            String validResult = validId(id);
            int updated = OwnedWriteSupport.transaction(dataSource, connection ->
                    transition(connection, validRun, validResult, principal.getOrgId(), next, allowed));
            boundary.recordAllowed(action, principal, validResult, "isolated-write");
            return ok("{\"updated\":" + updated + ",\"status\":\"" + next + "\"}");
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(); }
        catch (SQLException failure) { denied(request); return unavailable(); }
    }

    private OwnedPrincipal requireRead(HttpServletRequest request) throws SQLException {
        boundary.requireResultRead(request.getSession(false));
        return boundary.currentPrincipal(request.getSession(false));
    }

    private OwnedPrincipal requireWrite(HttpServletRequest request) throws SQLException {
        OwnedWriteSupport.requireLoopback(request);
        if (!"isolated".equals(environment.getProperty("owned.result.write.mode")))
            throw denied();
        boundary.requireResultWrite(request.getSession(false));
        return boundary.currentPrincipal(request.getSession(false));
    }

    private List<Map<String, Object>> readResults(String runId, String organization) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        String sql = "SELECT ID,RUNID,ORGID,ITEMNAME,ORIGINALRESULT,REPORTRESULT,RESULTSTATUS,UNITNAME,LOWLIMIT,HIGHLIMIT "
                + "FROM T_EMC_ORDER_TASK_RESULT WHERE RUNID=? AND ORGID=? ORDER BY ID";
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql)) {
            query.setString(1, runId); query.setString(2, organization);
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

    private static int updateValue(Connection connection, String runId, String resultId,
            String organization, String value) throws SQLException {
        try (PreparedStatement update = connection.prepareStatement(
                "UPDATE T_EMC_ORDER_TASK_RESULT SET ORIGINALRESULT=?,REPORTRESULT=?,RESULTSTATUS='entered' "
                + "WHERE ID=? AND RUNID=? AND ORGID=? AND (RESULTSTATUS IS NULL OR RESULTSTATUS <> 'approved')")) {
            update.setString(1, value); update.setString(2, value); update.setLong(3, Long.parseLong(resultId));
            update.setString(4, runId); update.setString(5, organization);
            int count = update.executeUpdate(); if (count != 1) throw denied(); return count;
        }
    }

    private static String calculateValue(Connection connection, String runId, String resultId,
            String organization) throws SQLException {
        String original;
        try (PreparedStatement query = connection.prepareStatement(
                "SELECT ORIGINALRESULT FROM T_EMC_ORDER_TASK_RESULT WHERE ID=? AND RUNID=? AND ORGID=?")) {
            query.setLong(1, Long.parseLong(resultId)); query.setString(2, runId); query.setString(3, organization);
            try (ResultSet result = query.executeQuery()) {
                if (!result.next()) throw denied(); original = result.getString(1);
            }
        }
        if (original == null || !original.matches("[-+]?(?:\\d+(?:\\.\\d*)?|\\.\\d+)"))
            throw new IllegalArgumentException("numeric result is required for calculation");
        String value = new BigDecimal(original).stripTrailingZeros().toPlainString();
        try (PreparedStatement update = connection.prepareStatement(
                "UPDATE T_EMC_ORDER_TASK_RESULT SET REPORTRESULT=?,RESULTSTATUS='calculated' WHERE ID=? AND RUNID=? AND ORGID=? AND (RESULTSTATUS IS NULL OR RESULTSTATUS IN ('entered','calculated'))")) {
            update.setString(1, value); update.setLong(2, Long.parseLong(resultId)); update.setString(3, runId); update.setString(4, organization);
            if (update.executeUpdate() != 1) throw denied();
        }
        return value;
    }

    private static int transition(Connection connection, String runId, String resultId,
            String organization, String next, String... allowed) throws SQLException {
        StringBuilder states = new StringBuilder(); for (int i = 0; i < allowed.length; i++) { if (i > 0) states.append(','); states.append('?'); }
        String sql = "UPDATE T_EMC_ORDER_TASK_RESULT SET RESULTSTATUS=? WHERE ID=? AND RUNID=? AND ORGID=? AND RESULTSTATUS IN (" + states + ") AND REPORTRESULT IS NOT NULL";
        try (PreparedStatement update = connection.prepareStatement(sql)) {
            int index = 1; update.setString(index++, next); update.setLong(index++, Long.parseLong(resultId)); update.setString(index++, runId); update.setString(index++, organization);
            for (String state : allowed) update.setString(index++, state);
            int count = update.executeUpdate(); if (count != 1) throw denied(); return count;
        }
    }

    private static String parseValue(String body) {
        if (body == null || body.trim().length() == 0) throw new IllegalArgumentException("result value is required");
        Object value = JSON.parse(body); if (!(value instanceof JSONObject)) throw new IllegalArgumentException("request must be an object");
        JSONObject object = (JSONObject) value; if (object.containsKey("p")) object = object.getJSONObject("p");
        String text = object.get("originalResult") == null ? "" : String.valueOf(object.get("originalResult")).trim();
        if (text.length() == 0 || text.length() > 200 || text.matches(".*[\\p{Cntrl}].*")) throw new IllegalArgumentException("result value is invalid");
        return text;
    }

    private static String validId(String value) { if (value == null || !value.matches("[0-9]+")) throw new IllegalArgumentException("result id is invalid"); return value; }
    private static AccessDeniedException denied() { return new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED"); }
    private void denied(HttpServletRequest request) { boundary.recordRequestDenied(request == null ? null : request.getSession(false), "result.write", "permission-denied"); }
    private static Map<String, Object> mapOf(String key, Object value, String key2, Object value2) { Map<String, Object> map = new LinkedHashMap<String, Object>(); map.put(key, value); map.put(key2, value2); return map; }
    private static String friendly(String name) { String upper = name.toUpperCase(); if ("ID".equals(upper)) return "id"; if ("RUNID".equals(upper)) return "runId"; if ("ORGID".equals(upper)) return "orgId"; if ("ITEMNAME".equals(upper)) return "itemName"; if ("ORIGINALRESULT".equals(upper)) return "originalResult"; if ("REPORTRESULT".equals(upper)) return "reportResult"; if ("RESULTSTATUS".equals(upper)) return "resultStatus"; if ("UNITNAME".equals(upper)) return "unitName"; if ("LOWLIMIT".equals(upper)) return "lowLimit"; if ("HIGHLIMIT".equals(upper)) return "highLimit"; return name.toLowerCase(); }
    private static String page(List<Map<String, Object>> rows) { Map<String, Object> body = new LinkedHashMap<String, Object>(); body.put("total", rows.size()); body.put("rows", rows); return JSON.toJSONString(body, SerializerFeature.WriteMapNullValue); }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().header("Cache-Control", "no-store").body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.RESULT.INVALID_REQUEST\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.RESULT.UNAVAILABLE\"}"); }
}
