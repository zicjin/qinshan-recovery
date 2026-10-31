package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import java.util.Collections;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Bounded owned seam for the historical role-user matrix.
 *
 * <p>The matrix is deliberately limited to explicit role grants in the
 * current user's organization. Writes require loopback, an isolated profile
 * and the same role-menu administration grant used by the menu matrix. This
 * keeps role assignment fail-closed while the broader role-management UI is
 * still being recovered.</p>
 */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedRoleUserMatrixController {
    private static final String ROLE_PREFIX = "T_CORE_ROLE$ROLENAME$";
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;
    private final Environment environment;

    public OwnedRoleUserMatrixController(OwnedIdentityHttpBoundary boundary, DataSource dataSource,
            Environment environment) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("matrix dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
        this.environment = environment;
    }

    @PostMapping(path = "/secure/core/module/sys/role-users/queries/matrix-headers",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> matrixHeaders(HttpServletRequest request) {
        try {
            boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.ROLE_MENU_ADMIN);
            return ResponseEntity.ok().header("Cache-Control", "no-store")
                    .body(JSON.toJSONString(readHeaders(), SerializerFeature.WriteMapNullValue));
        } catch (AccessDeniedException | org.springframework.security.core.AuthenticationException denied) {
            return forbidden();
        } catch (SQLException failure) {
            return unavailable();
        }
    }

    @PostMapping(path = "/secure/core/module/sys/role-users/queries/matrix-rows",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> matrixRows(HttpServletRequest request) {
        try {
            boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.ROLE_MENU_ADMIN);
            return ResponseEntity.ok().header("Cache-Control", "no-store")
                    .body(JSON.toJSONString(readRows(), SerializerFeature.WriteMapNullValue));
        } catch (AccessDeniedException | org.springframework.security.core.AuthenticationException denied) {
            return forbidden();
        } catch (SQLException failure) {
            return unavailable();
        }
    }

    @PutMapping(path = "/secure/core/module/sys/role-users/instant",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> instantSave(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        OwnedPrincipal principal = null;
        try {
            OwnedWriteSupport.requireLoopback(request);
            if (environment == null || !"isolated".equals(environment.getProperty("owned.role-user.write.mode"))) {
                throw denied();
            }
            boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.ROLE_MENU_ADMIN);
            principal = boundary.currentPrincipal(request.getSession(false));
            final List<Change> changes = parseChanges(body);
            if (changes.isEmpty()) throw new IllegalArgumentException("role-user changes are required");
            final OwnedPrincipal actor = principal;
            Integer updated = OwnedWriteSupport.transaction(dataSource, connection -> {
                for (Change change : changes) apply(connection, actor, change);
                return Integer.valueOf(changes.size());
            });
            boundary.recordAllowed("role-user-write", principal, "role-user.write", "isolated-write");
            return ResponseEntity.ok().header("Cache-Control", "no-store")
                    .body("{\"updated\":" + updated + "}");
        } catch (AccessDeniedException | org.springframework.security.core.AuthenticationException denied) {
            boundary.recordRequestDenied(request == null ? null : request.getSession(false),
                    "role-user.write", "permission-denied");
            return forbidden();
        } catch (IllegalArgumentException invalid) {
            boundary.recordRequestDenied(request == null ? null : request.getSession(false),
                    "role-user.write", "invalid-request");
            return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.ROLE_USER_MATRIX.INVALID_REQUEST\"}");
        } catch (SQLException failure) {
            boundary.recordRequestDenied(request == null ? null : request.getSession(false),
                    "role-user.write", "write-failed");
            return unavailable();
        }
    }

    private ResponseEntity<String> forbidden() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");
    }

    private ResponseEntity<String> unavailable() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.ROLE_USER_MATRIX.UNAVAILABLE\"}");
    }

    private static AccessDeniedException denied() {
        return new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
    }

    private static List<Change> parseChanges(String body) {
        if (body == null || body.trim().length() == 0) throw new IllegalArgumentException("request body is required");
        Object value;
        try { value = JSON.parse(body); }
        catch (RuntimeException failure) { throw new IllegalArgumentException("invalid JSON", failure); }
        if (value instanceof JSONObject && ((JSONObject) value).containsKey("p")) value = ((JSONObject) value).get("p");
        List<Change> changes = new ArrayList<Change>();
        if (value instanceof JSONObject) addChange(changes, (JSONObject) value);
        else if (value instanceof JSONArray) {
            for (Object item : (JSONArray) value) {
                if (!(item instanceof JSONObject)) throw new IllegalArgumentException("change must be an object");
                addChange(changes, (JSONObject) item);
            }
        } else throw new IllegalArgumentException("change must be an object");
        return changes;
    }

    private static void addChange(List<Change> changes, JSONObject object) {
        Object user = object.get("id");
        if (user == null) user = object.get("ID");
        if (user == null || String.valueOf(user).trim().length() == 0) throw new IllegalArgumentException("user id is required");
        String userId = String.valueOf(user).trim();
        boolean found = false;
        for (Map.Entry<String, Object> entry : object.entrySet()) {
            String key = entry.getKey();
            if (!key.startsWith(ROLE_PREFIX)) continue;
            String role = key.substring(ROLE_PREFIX.length());
            if (!role.matches("[0-9]+")) throw new IllegalArgumentException("invalid role field");
            String state = entry.getValue() == null ? "0" : String.valueOf(entry.getValue()).trim();
            if (state.length() == 0) state = "0";
            if (!"0".equals(state) && !"1".equals(state)) throw new IllegalArgumentException("role-user state must be 0 or 1");
            changes.add(new Change(userId, Long.valueOf(role), "1".equals(state)));
            found = true;
        }
        if (!found) throw new IllegalArgumentException("role field is required");
    }

    private static void apply(Connection connection, OwnedPrincipal actor, Change change) throws SQLException {
        UserState user = user(connection, change.userId);
        RoleState role = role(connection, change.roleId);
        if (role.suspended) throw denied();
        if (role.organization != null && role.organization.length() != 0
                && !role.organization.equals(actor.getOrgId())) throw denied();
        if (user.organization != null && user.organization.length() != 0
                && !user.organization.equals(actor.getOrgId())) throw denied();
        if (change.grant) {
            if (!grantExists(connection, change.roleId, change.userId)) {
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO T_CORE_ROLE_USER (ID,ROLEID,USERID) VALUES (?,?,?)")) {
                    insert.setLong(1, OwnedWriteSupport.nextIdentity(connection));
                    insert.setLong(2, change.roleId.longValue());
                    insert.setString(3, change.userId);
                    insert.executeUpdate();
                }
            }
        } else {
            try (PreparedStatement delete = connection.prepareStatement(
                    "DELETE FROM T_CORE_ROLE_USER WHERE ROLEID=? AND USERID=?")) {
                delete.setLong(1, change.roleId.longValue());
                delete.setString(2, change.userId);
                delete.executeUpdate();
            }
        }
    }

    private static UserState user(Connection connection, String userId) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM T_CORE_USER WHERE ID=?")) {
            query.setString(1, userId);
            try (ResultSet rows = query.executeQuery()) {
                if (!rows.next()) throw new IllegalArgumentException("user does not exist");
                ResultSetMetaData metadata = rows.getMetaData();
                String status = firstString(rows, metadata, "STATUS");
                if ("disabled".equalsIgnoreCase(status) || "deactivated".equalsIgnoreCase(status)
                        || "1".equals(firstString(rows, metadata, "LASTSUSPENDEDFLAG"))) throw denied();
                return new UserState(firstString(rows, metadata, "ORGID"));
            }
        }
    }

    private static RoleState role(Connection connection, Long roleId) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM T_CORE_ROLE WHERE ID=?")) {
            query.setLong(1, roleId.longValue());
            try (ResultSet rows = query.executeQuery()) {
                if (!rows.next()) throw new IllegalArgumentException("role does not exist");
                ResultSetMetaData metadata = rows.getMetaData();
                return new RoleState("1".equals(firstString(rows, metadata, "LASTSUSPENDEDFLAG")),
                        firstString(rows, metadata, "USEDBYORGID"));
            }
        }
    }

    private static boolean grantExists(Connection connection, Long roleId, String userId) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("SELECT 1 FROM T_CORE_ROLE_USER WHERE ROLEID=? AND USERID=?")) {
            query.setLong(1, roleId.longValue()); query.setString(2, userId);
            try (ResultSet rows = query.executeQuery()) { return rows.next(); }
        }
    }

    private List<Map<String, Object>> readHeaders() throws SQLException {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        try (Connection connection = dataSource.getConnection();
                PreparedStatement query = connection.prepareStatement("SELECT * FROM T_CORE_ROLE WHERE NVL(LASTSUSPENDEDFLAG,'0') <> '1' ORDER BY ID")) {
            try (ResultSet rows = query.executeQuery()) {
                ResultSetMetaData metadata = rows.getMetaData();
                while (rows.next()) {
                    String id = firstString(rows, metadata, "ID");
                    String name = firstString(rows, metadata, "ROLENAME");
                    Map<String, Object> header = new LinkedHashMap<String, Object>();
                    header.put("field", ROLE_PREFIX + id); header.put("title", name == null ? id : name);
                    header.put("roleId", id); header.put("type", "simpleCheckbox");
                    header.put("editor", Boolean.FALSE); header.put("readonly", Boolean.TRUE);
                    result.add(header);
                }
            }
        }
        return result;
    }

    private Map<String, Object> readRows() throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        List<RoleHeader> roles = roleHeaders();
        Map<String, java.util.Set<String>> grants = readGrants();
        try (Connection connection = dataSource.getConnection();
                PreparedStatement query = connection.prepareStatement("SELECT * FROM T_CORE_USER ORDER BY ID")) {
            try (ResultSet result = query.executeQuery()) {
                ResultSetMetaData metadata = result.getMetaData();
                while (result.next()) {
                    String userId = firstString(result, metadata, "ID");
                    Map<String, Object> row = new LinkedHashMap<String, Object>();
                    row.put("id", userId); row.put("userId", userId);
                    row.put("username", firstString(result, metadata, "USERNAME"));
                    row.put("orgId", firstString(result, metadata, "ORGID"));
                    java.util.Set<String> userGrants = grants.get(userId);
                    if (userGrants == null) userGrants = java.util.Collections.emptySet();
                    for (RoleHeader role : roles) row.put(role.field, userGrants.contains(role.id) ? "1" : "0");
                    rows.add(row);
                }
            }
        }
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("total", rows.size()); body.put("pageNumber", 1); body.put("pageSize", rows.size()); body.put("rows", rows);
        return body;
    }

    private Map<String, java.util.Set<String>> readGrants() throws SQLException {
        Map<String, java.util.Set<String>> result = new java.util.LinkedHashMap<String, java.util.Set<String>>();
        try (Connection connection = dataSource.getConnection();
                PreparedStatement query = connection.prepareStatement("SELECT USERID,ROLEID FROM T_CORE_ROLE_USER")) {
            try (ResultSet rows = query.executeQuery()) {
                while (rows.next()) {
                    String user = rows.getString(1);
                    String role = rows.getString(2);
                    if (user == null || role == null) continue;
                    java.util.Set<String> values = result.get(user);
                    if (values == null) {
                        values = new java.util.LinkedHashSet<String>();
                        result.put(user, values);
                    }
                    values.add(role);
                }
            }
        }
        return result;
    }

    private List<RoleHeader> roleHeaders() throws SQLException {
        List<RoleHeader> result = new ArrayList<RoleHeader>();
        for (Map<String, Object> header : readHeaders()) result.add(new RoleHeader(String.valueOf(header.get("roleId")), String.valueOf(header.get("field"))));
        return result;
    }

    private boolean hasGrant(String userId, String roleId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement query = connection.prepareStatement("SELECT 1 FROM T_CORE_ROLE_USER WHERE USERID=? AND ROLEID=?")) {
            query.setString(1, userId); query.setLong(2, Long.parseLong(roleId));
            try (ResultSet rows = query.executeQuery()) { return rows.next(); }
        }
    }

    private static String firstString(ResultSet rows, ResultSetMetaData metadata, String name) throws SQLException {
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            if (name.equalsIgnoreCase(metadata.getColumnLabel(i)) || name.equalsIgnoreCase(metadata.getColumnName(i))) return rows.getString(i);
        }
        return null;
    }

    private static final class Change {
        final String userId; final Long roleId; final boolean grant;
        Change(String userId, Long roleId, boolean grant) { this.userId = userId; this.roleId = roleId; this.grant = grant; }
    }
    private static final class UserState { final String organization; UserState(String organization) { this.organization = organization; } }
    private static final class RoleState { final boolean suspended; final String organization; RoleState(boolean suspended, String organization) { this.suspended = suspended; this.organization = organization; } }
    private static final class RoleHeader { final String id, field; RoleHeader(String id, String field) { this.id = id; this.field = field; } }
}
