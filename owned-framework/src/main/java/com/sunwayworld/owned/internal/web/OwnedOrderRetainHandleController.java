package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Isolated order-retain disposal/handling update with organization scope. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedOrderRetainHandleController {
    private final OwnedIdentityHttpBoundary boundary; private final DataSource dataSource; private final Environment environment;
    public OwnedOrderRetainHandleController(OwnedIdentityHttpBoundary boundary, DataSource dataSource, Environment environment) {
        if (boundary == null || dataSource == null || environment == null) throw new IllegalArgumentException("order retain handle dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource; this.environment = environment;
    }
    @PostMapping(path = {"/secure/core/module/bp/order/order-retains/action/handle",
            "/secure/emc/module/bp/order/order-retains/action/handle"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> handle(HttpServletRequest request, @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireWrite(request); JSONArray rows = rows(body);
            if (rows.isEmpty() || rows.size() > 100) throw new IllegalArgumentException("retain rows are required");
            int updated = OwnedWriteSupport.transaction(dataSource, connection -> update(connection, rows, principal));
            boundary.recordAllowed("order-retain-handle", principal, String.valueOf(updated), "isolated-write");
            return ok("{\"updated\":" + updated + "}");
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(); }
        catch (java.sql.SQLException failure) { denied(request); return unavailable(); }
    }
    private OwnedPrincipal requireWrite(HttpServletRequest request) throws java.sql.SQLException {
        OwnedWriteSupport.requireLoopback(request); if (!"isolated".equals(environment.getProperty("owned.order-retain.write.mode"))) throw denied();
        boundary.requireOrderRetainWrite(request.getSession(false)); return boundary.currentPrincipal(request.getSession(false));
    }
    private static int update(Connection connection, JSONArray rows, OwnedPrincipal principal) throws java.sql.SQLException {
        int updated = 0;
        try (PreparedStatement statement = connection.prepareStatement("UPDATE T_EMC_ORDER_RETAIN SET HANDLEUSERID=?,HANDLEDATE=SYSDATE,HANDLERECORD=? WHERE ID=? AND HANDLEDATE IS NULL AND EXISTS (SELECT 1 FROM T_EMC_ORDER O WHERE O.ID=T_EMC_ORDER_RETAIN.ORDERID AND O.CREATEDBYORGID=?)")) {
            for (Object raw : rows) {
                if (!(raw instanceof JSONObject)) throw new IllegalArgumentException("retain row is invalid"); JSONObject row = (JSONObject) raw;
                long id = positive(row.get("id")); String record = text(row.get("handleRecord"));
                if (record.length() > 255 || record.matches(".*[\\p{Cntrl}].*")) throw new IllegalArgumentException("handle record is invalid");
                statement.setString(1, principal.getId()); statement.setString(2, record.length() == 0 ? null : record); statement.setLong(3, id); statement.setString(4, principal.getOrgId());
                if (statement.executeUpdate() != 1) throw denied(); updated++;
            }
        } return updated;
    }
    private static JSONArray rows(String body) {
        if (body == null || body.trim().length() == 0) throw new IllegalArgumentException("request is required"); Object parsed = JSON.parse(body);
        if (!(parsed instanceof JSONObject)) throw new IllegalArgumentException("request must be an object"); JSONObject object = (JSONObject) parsed;
        if (object.containsKey("p")) object = object.getJSONObject("p"); Object batch = object.get("b");
        if (batch instanceof JSONArray && ((JSONArray) batch).size() > 1 && ((JSONArray) batch).get(1) instanceof JSONArray) return (JSONArray) ((JSONArray) batch).get(1);
        Object values = object.get("rows"); if (values instanceof JSONArray) return (JSONArray) values; throw new IllegalArgumentException("retain rows are required");
    }
    private static long positive(Object value) { try { long parsed = Long.parseLong(String.valueOf(value)); if (parsed > 0) return parsed; } catch (Exception ignored) { } throw new IllegalArgumentException("retain id is invalid"); }
    private static String text(Object value) { return value == null ? "" : String.valueOf(value).trim(); }
    private static AccessDeniedException denied() { return new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED"); }
    private void denied(HttpServletRequest request) { boundary.recordRequestDenied(request == null ? null : request.getSession(false), "order-retain.write", "permission-denied"); }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().header("Cache-Control", "no-store").body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.ORDER_RETAIN.INVALID_REQUEST\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.ORDER_RETAIN.UNAVAILABLE\"}"); }
}
