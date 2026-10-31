package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Deep write seam for the bounded result-run status transition. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedResultRunWriteController {
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;
    private final Environment environment;

    public OwnedResultRunWriteController(OwnedIdentityHttpBoundary boundary,
            DataSource dataSource, Environment environment) {
        if (boundary == null || dataSource == null || environment == null)
            throw new IllegalArgumentException("result write dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
        this.environment = environment;
    }

    @PutMapping(path = "/secure/core/module/bp/result/runs/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> update(HttpServletRequest request,
            @PathVariable("id") String id,
            @RequestBody(required = false) String body) {
        OwnedPrincipal principal = null;
        try {
            OwnedWriteSupport.requireLoopback(request);
            if (!"isolated".equals(environment.getProperty("owned.result.write.mode")))
                throw denied();
            boundary.requireResultWrite(request.getSession(false));
            principal = boundary.currentPrincipal(request.getSession(false));
            String runId = validId(id);
            String status = parseStatus(body);
            final String organization = principal.getOrgId();
            final String nextStatus = status;
            int updated = OwnedWriteSupport.transaction(dataSource, connection ->
                    updateStatus(connection, runId, organization, nextStatus));
            boundary.recordAllowed("result-run-update", principal, runId, "isolated-write");
            return ok("{\"updated\":" + updated + "}");
        } catch (AccessDeniedException denied) {
            denied(request);
            return forbidden();
        } catch (IllegalArgumentException invalid) {
            denied(request);
            return badRequest(invalid.getMessage());
        } catch (SQLException failure) {
            denied(request);
            return unavailable();
        }
    }

    /**
     * Creates the minimum real run row needed by an HTTP fixture.  This is a
     * business API operation in the same Spring application; it is not a SQL
     * test shortcut.  Only the stable ID/status/organization columns are
     * populated because the remaining production fields are nullable.
     */
    @PostMapping(path = "/secure/core/module/bp/result/runs",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> create(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        OwnedPrincipal principal = null;
        try {
            OwnedWriteSupport.requireLoopback(request);
            if (!"isolated".equals(environment.getProperty("owned.result.write.mode")))
                throw denied();
            boundary.requireResultWrite(request.getSession(false));
            principal = boundary.currentPrincipal(request.getSession(false));
            JSONObject value = object(body);
            String id = validId(text(value.get("id")));
            String status = text(value.get("status"));
            if (status.length() == 0) status = "created";
            if (!status.matches("[A-Za-z][A-Za-z0-9_-]{0,35}"))
                throw new IllegalArgumentException("result status is invalid");
            final String runId = id;
            final String nextStatus = status;
            final String organization = principal.getOrgId();
            final String organizationName = principal.getOrgName();
            final String actor = principal.getId();
            int inserted = OwnedWriteSupport.transaction(dataSource, connection -> {
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO T_EMC_RUN (ID,STATUS,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME,ORGID,ORGNAME) "
                                + "VALUES (?,?,?,?,SYSTIMESTAMP,?,?,?,?)")) {
                    insert.setString(1, runId);
                    insert.setString(2, nextStatus);
                    insert.setString(3, actor);
                    insert.setString(4, actor);
                    insert.setString(5, organization);
                    insert.setString(6, organizationName);
                    insert.setString(7, organization);
                    insert.setString(8, organizationName);
                    return insert.executeUpdate();
                }
            });
            boundary.recordAllowed("result-run-create", principal, runId, "isolated-write");
            return ok("{\"id\":\"" + escape(runId) + "\",\"created\":" + inserted + "}");
        } catch (AccessDeniedException denied) {
            denied(request);
            return forbidden();
        } catch (IllegalArgumentException invalid) {
            denied(request);
            return badRequest(invalid.getMessage());
        } catch (SQLException failure) {
            denied(request);
            return unavailable();
        }
    }

    /** Deletes an isolated fixture row through the same owned business API. */
    @DeleteMapping(path = "/secure/core/module/bp/result/runs",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> delete(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        OwnedPrincipal principal = null;
        try {
            OwnedWriteSupport.requireLoopback(request);
            if (!"isolated".equals(environment.getProperty("owned.result.write.mode")))
                throw denied();
            boundary.requireResultWrite(request.getSession(false));
            principal = boundary.currentPrincipal(request.getSession(false));
            JSONObject value = object(body);
            String runId = validId(text(value.get("id")));
            final String organization = principal.getOrgId();
            int deleted = OwnedWriteSupport.transaction(dataSource, connection ->
                    deleteStatus(connection, runId, organization));
            boundary.recordAllowed("result-run-delete", principal, runId, "isolated-write");
            return ok("{\"deleted\":" + deleted + "}");
        } catch (AccessDeniedException denied) {
            denied(request);
            return forbidden();
        } catch (IllegalArgumentException invalid) {
            denied(request);
            return badRequest(invalid.getMessage());
        } catch (SQLException failure) {
            denied(request);
            return unavailable();
        }
    }

    private OwnedPrincipal principal(HttpServletRequest request) throws SQLException {
        boundary.requireResultWrite(request.getSession(false));
        return boundary.currentPrincipal(request.getSession(false));
    }

    private static int updateStatus(Connection connection, String id, String organization,
            String status) throws SQLException {
        try (PreparedStatement update = connection.prepareStatement(
                "UPDATE T_EMC_RUN SET STATUS=? WHERE ID=? AND ORGID=?")) {
            update.setString(1, status);
            update.setString(2, id);
            update.setString(3, organization);
            int count = update.executeUpdate();
            if (count != 1) throw denied();
            return count;
        }
    }

    private static int deleteStatus(Connection connection, String id, String organization)
            throws SQLException {
        try (PreparedStatement delete = connection.prepareStatement(
                "DELETE FROM T_EMC_RUN WHERE ID=? AND ORGID=?")) {
            delete.setString(1, id);
            delete.setString(2, organization);
            return delete.executeUpdate();
        }
    }

    private static String validId(String id) {
        if (id == null || !id.matches("[A-Za-z0-9_-]+")) throw new IllegalArgumentException("result run id is invalid");
        return id;
    }

    private static String parseStatus(String body) {
        if (body == null || body.trim().length() == 0) throw new IllegalArgumentException("result status is required");
        Object value = JSON.parse(body);
        if (!(value instanceof JSONObject)) throw new IllegalArgumentException("request must be an object");
        JSONObject object = (JSONObject) value;
        if (object.containsKey("p")) object = object.getJSONObject("p");
        String status = text(object.get("status"));
        if (!status.matches("[A-Za-z][A-Za-z0-9_-]{0,35}"))
            throw new IllegalArgumentException("result status is invalid");
        return status;
    }

    private static JSONObject object(String body) {
        if (body == null || body.trim().length() == 0)
            throw new IllegalArgumentException("result run body is required");
        Object value = JSON.parse(body);
        if (!(value instanceof JSONObject)) throw new IllegalArgumentException("request must be an object");
        JSONObject object = (JSONObject) value;
        if (object.containsKey("p")) object = object.getJSONObject("p");
        if (object.containsKey("b")) {
            com.alibaba.fastjson.JSONArray batch = object.getJSONArray("b");
            if (batch == null || batch.size() < 2 || !(batch.get(1) instanceof com.alibaba.fastjson.JSONArray))
                throw new IllegalArgumentException("result run batch is invalid");
            com.alibaba.fastjson.JSONArray rows = batch.getJSONArray(1);
            if (rows.isEmpty() || !(rows.get(0) instanceof JSONObject))
                throw new IllegalArgumentException("result run row is required");
            object = rows.getJSONObject(0);
        }
        return object;
    }

    private void denied(HttpServletRequest request) {
        boundary.recordRequestDenied(request == null ? null : request.getSession(false),
                "result.write", "permission-denied");
    }

    private static AccessDeniedException denied() {
        return new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
    }

    private static String text(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static ResponseEntity<String> ok(String body) {
        return ResponseEntity.ok().header("Cache-Control", "no-store").body(body);
    }

    private static ResponseEntity<String> forbidden() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");
    }

    private static ResponseEntity<String> badRequest(String message) {
        return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.RESULT.INVALID_REQUEST\"}");
    }

    private static ResponseEntity<String> unavailable() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"message\":\"GIKAM.RESULT.UNAVAILABLE\"}");
    }
}
