package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Isolated order-retain insert/delete seam with order organization scope. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedOrderRetainWriteController {
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;
    private final Environment environment;

    public OwnedOrderRetainWriteController(OwnedIdentityHttpBoundary boundary,
            DataSource dataSource, Environment environment) {
        if (boundary == null || dataSource == null || environment == null)
            throw new IllegalArgumentException("order retain write dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource; this.environment = environment;
    }

    @PostMapping(path = "/secure/core/module/bp/order/order-retains",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> insert(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireWrite(request);
            JSONObject values = object(body);
            long orderId = positive(values.get("orderId"), "orderId");
            long projectId = positive(values.get("projId"), "projId");
            long folderId = positive(values.get("folderId"), "folderId");
            long quantity = positive(values.get("retainQty"), "retainQty");
            long days = nonNegative(values.get("retainDays"), "retainDays");
            String location = text(values.get("retainLoc"));
            if (location.length() == 0 || location.length() > 100 || location.matches(".*[\\p{Cntrl}].*"))
                throw new IllegalArgumentException("retainLoc is invalid");
            long id = OwnedWriteSupport.transaction(dataSource, connection -> {
                requireOrder(connection, orderId, principal.getOrgId());
                long next = OwnedWriteSupport.nextIdentity(connection);
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO T_EMC_ORDER_RETAIN (ID,ORDERID,PROJID,FOLDERID,RETAINQTY,RETAINTIMELIMIT,RETAINLOC,RETAINUSERID,CREATEDBYID,CREATEDBYORGID) "
                        + "VALUES (?,?,?,?,?,SYSTIMESTAMP+?,?,?, ?,?)")) {
                    insert.setLong(1, next); insert.setLong(2, orderId); insert.setLong(3, projectId);
                    insert.setLong(4, folderId); insert.setLong(5, quantity); insert.setLong(6, days);
                    insert.setString(7, location); insert.setString(8, principal.getId());
                    insert.setString(9, principal.getId()); insert.setString(10, principal.getOrgId());
                    if (insert.executeUpdate() != 1) throw new IllegalArgumentException("order retain insert failed");
                }
                return next;
            });
            boundary.recordAllowed("order-retain-insert", principal, String.valueOf(id), "isolated-write");
            return ok("{\"id\":" + id + "}");
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(); }
        catch (java.sql.SQLException failure) { denied(request); return unavailable(); }
    }

    @DeleteMapping(path = "/secure/core/module/bp/order/order-retains/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> delete(HttpServletRequest request, @PathVariable("id") String id) {
        try {
            OwnedPrincipal principal = requireWrite(request);
            if (id == null || !id.matches("[0-9]+")) throw new IllegalArgumentException("order retain id is invalid");
            int deleted = OwnedWriteSupport.transaction(dataSource, connection -> {
                try (PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM T_EMC_ORDER_RETAIN WHERE ID=? AND EXISTS (SELECT 1 FROM T_EMC_ORDER O WHERE O.ID=T_EMC_ORDER_RETAIN.ORDERID AND O.CREATEDBYORGID=?)")) {
                    statement.setLong(1, Long.parseLong(id)); statement.setString(2, principal.getOrgId());
                    int count = statement.executeUpdate(); if (count != 1) throw denied(); return count;
                }
            });
            boundary.recordAllowed("order-retain-delete", principal, id, "isolated-write");
            return ok("{\"deleted\":" + deleted + "}");
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(); }
        catch (java.sql.SQLException failure) { denied(request); return unavailable(); }
    }

    private OwnedPrincipal requireWrite(HttpServletRequest request) throws java.sql.SQLException {
        OwnedWriteSupport.requireLoopback(request);
        if (!"isolated".equals(environment.getProperty("owned.order-retain.write.mode"))) throw denied();
        boundary.requireOrderRetainWrite(request.getSession(false));
        return boundary.currentPrincipal(request.getSession(false));
    }

    private static void requireOrder(Connection connection, long orderId, String organization) throws java.sql.SQLException {
        try (PreparedStatement query = connection.prepareStatement("SELECT 1 FROM T_EMC_ORDER WHERE ID=? AND CREATEDBYORGID=?")) {
            query.setLong(1, orderId); query.setString(2, organization);
            try (ResultSet rows = query.executeQuery()) { if (!rows.next()) throw denied(); }
        }
    }

    private static JSONObject object(String body) {
        if (body == null || body.trim().length() == 0) throw new IllegalArgumentException("request is required");
        Object value = JSON.parse(body); if (!(value instanceof JSONObject)) throw new IllegalArgumentException("request must be an object");
        JSONObject object = (JSONObject)value; return object.containsKey("p") ? object.getJSONObject("p") : object;
    }
    private static long positive(Object value, String field) { long parsed = number(value, field); if (parsed <= 0) throw new IllegalArgumentException(field + " must be positive"); return parsed; }
    private static long nonNegative(Object value, String field) { long parsed = number(value, field); if (parsed < 0) throw new IllegalArgumentException(field + " must be non-negative"); return parsed; }
    private static long number(Object value, String field) { try { return Long.parseLong(String.valueOf(value)); } catch (Exception failure) { throw new IllegalArgumentException(field + " is invalid"); } }
    private static String text(Object value) { return value == null ? "" : String.valueOf(value).trim(); }
    private static AccessDeniedException denied() { return new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED"); }
    private void denied(HttpServletRequest request) { boundary.recordRequestDenied(request == null ? null : request.getSession(false), "order-retain.write", "permission-denied"); }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().header("Cache-Control", "no-store").body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.ORDER_RETAIN.INVALID_REQUEST\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.ORDER_RETAIN.UNAVAILABLE\"}"); }
}
