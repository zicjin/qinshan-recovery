package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Read-only seam for the recovered menu-role matrix.
 *
 * <p>The historical page loads dynamic role columns from
 * {@code /queries/matrix-headers} and menu rows from
 * {@code /queries/matrix-rows}.  It also exposes the historical instant-save
 * route, but only for an explicitly isolated Oracle fixture.
 * Access is enabled only by an explicitly configured administration menu grant
 * ({@link OwnedIdentityProvider#ROLE_MENU_ADMIN}); no wildcard or default
 * administrator is introduced.</p>
 */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedRoleMenuMatrixController {
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;
    private final Environment environment;

    public OwnedRoleMenuMatrixController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        this(boundary, dataSource, null);
    }

    public OwnedRoleMenuMatrixController(OwnedIdentityHttpBoundary boundary, DataSource dataSource,
            Environment environment) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("matrix dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
        this.environment = environment;
    }

    @PostMapping(path = "/secure/core/module/sys/menu-roles/queries/matrix-headers",
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

    @PostMapping(path = "/secure/core/module/sys/menu-roles/queries/matrix-rows",
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

    /**
     * Historical instant-save contract for one or more role/menu cells.  The
     * browser sends either the row object directly or inside the standard
     * {@code {"p": ...}} envelope.  Values {@code 1} grant a menu and
     * {@code 0}/{@code null} revoke it; tri-state {@code 2} is a read-only
     * aggregate and is rejected rather than guessed.
     */
    @PutMapping(path = "/secure/core/module/sys/menu-roles/instant",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> instantSave(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        OwnedPrincipal principal = null;
        try {
            OwnedWriteSupport.requireLoopback(request);
            if (environment == null || !"isolated".equals(environment.getProperty("owned.role-menu.write.mode"))) {
                throw denied();
            }
            boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.ROLE_MENU_ADMIN);
            principal = boundary.currentPrincipal(request.getSession(false));
            final List<Change> changes = parseChanges(body);
            if (changes.isEmpty()) throw new IllegalArgumentException("role-menu changes are required");
            final OwnedPrincipal actor = principal;
            Integer updated = OwnedWriteSupport.transaction(dataSource, connection -> {
                for (Change change : changes) apply(connection, actor, change);
                return Integer.valueOf(changes.size());
            });
            boundary.recordAllowed("role-menu-write", principal, "role-menu.write", "isolated-write");
            return ResponseEntity.ok().header("Cache-Control", "no-store")
                    .body("{\"updated\":" + updated + "}");
        } catch (AccessDeniedException | org.springframework.security.core.AuthenticationException denied) {
            boundary.recordRequestDenied(request == null ? null : request.getSession(false),
                    "role-menu.write", "permission-denied");
            return forbidden();
        } catch (IllegalArgumentException invalid) {
            boundary.recordRequestDenied(request == null ? null : request.getSession(false),
                    "role-menu.write", "invalid-request");
            return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.MENU_ROLE_MATRIX.INVALID_REQUEST\"}");
        } catch (SQLException failure) {
            boundary.recordRequestDenied(request == null ? null : request.getSession(false),
                    "role-menu.write", "write-failed");
            return unavailable();
        }
    }

    private ResponseEntity<String> forbidden() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");
    }

    private ResponseEntity<String> unavailable() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.MENU_ROLE_MATRIX.UNAVAILABLE\"}");
    }

    private static AccessDeniedException denied() {
        return new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
    }

    private static List<Change> parseChanges(String body) {
        if (body == null || body.trim().length() == 0) throw new IllegalArgumentException("request body is required");
        Object value;
        try { value = JSON.parse(body); }
        catch (RuntimeException failure) { throw new IllegalArgumentException("invalid JSON", failure); }
        if (value instanceof JSONObject) {
            JSONObject object = (JSONObject) value;
            if (object.containsKey("p")) value = object.get("p");
        }
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
        Object menu = object.get("id");
        if (menu == null) menu = object.get("ID");
        if (menu == null || String.valueOf(menu).trim().length() == 0) throw new IllegalArgumentException("menu id is required");
        String menuId = String.valueOf(menu).trim();
        boolean found = false;
        for (Map.Entry<String, Object> entry : object.entrySet()) {
            String key = entry.getKey();
            String prefix = "T_CORE_ROLE$ROLENAME$";
            if (!key.startsWith(prefix)) continue;
            String roleId = key.substring(prefix.length());
            if (!roleId.matches("[0-9]+")) throw new IllegalArgumentException("invalid role field");
            String state = entry.getValue() == null ? "0" : String.valueOf(entry.getValue()).trim();
            if (state.length() == 0) state = "0";
            if (!"0".equals(state) && !"1".equals(state)) throw new IllegalArgumentException("role-menu state must be 0 or 1");
            changes.add(new Change(menuId, Long.valueOf(roleId), "1".equals(state)));
            found = true;
        }
        if (!found) throw new IllegalArgumentException("role field is required");
    }

    private static void apply(Connection connection, OwnedPrincipal actor, Change change) throws SQLException {
        RoleState role = role(connection, change.roleId);
        if (role.suspended) throw denied();
        if (role.organization != null && role.organization.length() != 0
                && !role.organization.equals(actor.getOrgId())) throw denied();
        if (!menuExists(connection, change.menuId)) throw new IllegalArgumentException("menu does not exist");
        if (change.grant) {
            if (!grantExists(connection, change.roleId, change.menuId)) {
                if (hasColumn(connection, "T_CORE_ROLE_PERMISSION", "ID")) {
                    try (PreparedStatement insert = connection.prepareStatement(
                            "INSERT INTO T_CORE_ROLE_PERMISSION (ID,ROLEID,PERMISSIONTYPEID,TARGETID,CREATEDBYID,CREATEDBYNAME,CREATEDTIME) "
                            + "VALUES (SEQ_CORE.NEXTVAL,?,?,?, ?, ?, SYSTIMESTAMP)")) {
                        insert.setLong(1, change.roleId.longValue());
                        insert.setString(2, "T_CORE_MENU");
                        insert.setString(3, change.menuId);
                        insert.setString(4, actor.getId());
                        insert.setString(5, actor.getId());
                        insert.executeUpdate();
                    }
                } else {
                    // Small contract fixtures created before the production
                    // primary-key columns remain readable; writes in the
                    // shipped schema always take the SEQ_CORE branch above.
                    try (PreparedStatement insert = connection.prepareStatement(
                            "INSERT INTO T_CORE_ROLE_PERMISSION (ROLEID,PERMISSIONTYPEID,TARGETID) VALUES (?,?,?)")) {
                        insert.setLong(1, change.roleId.longValue());
                        insert.setString(2, "T_CORE_MENU");
                        insert.setString(3, change.menuId);
                        insert.executeUpdate();
                    }
                }
            }
        } else {
            try (PreparedStatement delete = connection.prepareStatement(
                    "DELETE FROM T_CORE_ROLE_PERMISSION WHERE ROLEID=? AND PERMISSIONTYPEID='T_CORE_MENU' AND TARGETID=?")) {
                delete.setLong(1, change.roleId.longValue());
                delete.setString(2, change.menuId);
                delete.executeUpdate();
            }
        }
    }

    private static RoleState role(Connection connection, Long roleId) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM T_CORE_ROLE WHERE ID=?")) {
            query.setLong(1, roleId.longValue());
            try (ResultSet rows = query.executeQuery()) {
                if (!rows.next()) throw new IllegalArgumentException("role does not exist");
                ResultSetMetaData metadata = rows.getMetaData();
                String suspended = firstString(rows, metadata, "LASTSUSPENDEDFLAG");
                String organization = firstString(rows, metadata, "USEDBYORGID");
                return new RoleState("1".equals(suspended), organization);
            }
        }
    }

    private static boolean hasColumn(Connection connection, String table, String column) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM " + table + " WHERE 1=0")) {
            try (ResultSet rows = query.executeQuery()) {
                ResultSetMetaData metadata = rows.getMetaData();
                for (int i = 1; i <= metadata.getColumnCount(); i++) {
                    if (column.equalsIgnoreCase(metadata.getColumnLabel(i))
                            || column.equalsIgnoreCase(metadata.getColumnName(i))) return true;
                }
                return false;
            }
        }
    }

    private static String firstString(ResultSet rows, ResultSetMetaData metadata, String name) throws SQLException {
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            if (name.equalsIgnoreCase(metadata.getColumnLabel(i)) || name.equalsIgnoreCase(metadata.getColumnName(i))) {
                return rows.getString(i);
            }
        }
        return null;
    }

    private static boolean menuExists(Connection connection, String menuId) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("SELECT 1 FROM T_CORE_MENU WHERE ID=?")) {
            query.setString(1, menuId);
            try (ResultSet rows = query.executeQuery()) { return rows.next(); }
        }
    }

    private static boolean grantExists(Connection connection, Long roleId, String menuId) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement(
                "SELECT 1 FROM T_CORE_ROLE_PERMISSION WHERE ROLEID=? AND PERMISSIONTYPEID='T_CORE_MENU' AND TARGETID=?")) {
            query.setLong(1, roleId.longValue()); query.setString(2, menuId);
            try (ResultSet rows = query.executeQuery()) { return rows.next(); }
        }
    }

    private static final class Change {
        private final String menuId; private final Long roleId; private final boolean grant;
        private Change(String menuId, Long roleId, boolean grant) { this.menuId = menuId; this.roleId = roleId; this.grant = grant; }
    }
    private static final class RoleState {
        private final boolean suspended; private final String organization;
        private RoleState(boolean suspended, String organization) { this.suspended = suspended; this.organization = organization; }
    }

    private List<Map<String, Object>> readHeaders() throws SQLException {
        List<RoleRow> roles = readRoles();
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (RoleRow role : roles) {
            Map<String, Object> header = new LinkedHashMap<String, Object>();
            header.put("field", role.field());
            header.put("title", role.name);
            header.put("roleId", role.id);
            header.put("type", "simpleCheckbox");
            // Matrix writes are deliberately outside this bounded slice.
            header.put("editor", Boolean.FALSE);
            header.put("readonly", Boolean.TRUE);
            result.add(header);
        }
        return result;
    }

    private Map<String, Object> readRows() throws SQLException {
        List<RoleRow> roles = readRoles();
        List<MenuRow> menus = readMenus();
        Map<String, Set<String>> grants = readGrants();
        Map<String, List<MenuRow>> childrenByParent = indexChildren(menus);
        Map<String, Map<String, Object>> nodes = new LinkedHashMap<String, Map<String, Object>>();
        for (MenuRow menu : menus) {
            Map<String, Object> node = new LinkedHashMap<String, Object>();
            node.put("id", menu.id);
            node.put("menuname", menu.name);
            node.put("menuInitName", menu.initialName);
            node.put("href", menu.href);
            node.put("parentId", menu.parentId);
            node.put("orderNo", menu.orderNo);
            node.put("children", new ArrayList<Map<String, Object>>());
            for (RoleRow role : roles) {
                Set<String> roleGrants = grants.get(role.id);
                if (roleGrants == null) roleGrants = Collections.emptySet();
                String state = roleState(menu, roleGrants, childrenByParent);
                if (state != null) node.put(role.field(), state);
            }
            nodes.put(menu.id, node);
        }
        List<Map<String, Object>> roots = new ArrayList<Map<String, Object>>();
        for (MenuRow menu : menus) {
            Map<String, Object> node = nodes.get(menu.id);
            Map<String, Object> parent = menu.parentId == null ? null : nodes.get(menu.parentId);
            if (parent == null || menu.id.equals(menu.parentId)) roots.add(node);
            else {
                @SuppressWarnings("unchecked") List<Map<String, Object>> children = (List<Map<String, Object>>) parent.get("children");
                children.add(node);
            }
        }
        setChildCounts(roots);
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("total", menus.size());
        body.put("pageNumber", 1);
        body.put("pageSize", menus.size());
        body.put("rows", roots);
        return body;
    }

    /**
     * Reproduces the supplier matrix's tri-state parent cells: a directly
     * granted leaf is {@code 1}; a directly granted parent whose descendants
     * are not all granted is {@code 2}; absent direct grants remain absent.
     */
    private static String roleState(MenuRow menu, Set<String> grants,
            Map<String, List<MenuRow>> childrenByParent) {
        if (!grants.contains("0") && !grants.contains(menu.id)) return null;
        return hasMissingDescendant(menu.id, grants, childrenByParent, new LinkedHashSet<String>()) ? "2" : "1";
    }

    private static boolean hasMissingDescendant(String parentId, Set<String> grants,
            Map<String, List<MenuRow>> childrenByParent, Set<String> visited) {
        if (!visited.add(parentId)) return true;
        List<MenuRow> children = childrenByParent.get(parentId);
        if (children == null) return false;
        for (MenuRow child : children) {
            if (!grants.contains("0") && !grants.contains(child.id)) return true;
            if (hasMissingDescendant(child.id, grants, childrenByParent, visited)) return true;
        }
        return false;
    }

    private static Map<String, List<MenuRow>> indexChildren(List<MenuRow> menus) {
        Map<String, List<MenuRow>> result = new LinkedHashMap<String, List<MenuRow>>();
        for (MenuRow menu : menus) {
            if (menu.parentId == null || menu.parentId.length() == 0) continue;
            List<MenuRow> children = result.get(menu.parentId);
            if (children == null) {
                children = new ArrayList<MenuRow>();
                result.put(menu.parentId, children);
            }
            children.add(menu);
        }
        return result;
    }

    private List<RoleRow> readRoles() throws SQLException {
        List<RoleRow> result = new ArrayList<RoleRow>();
        try (Connection connection = dataSource.getConnection();
                PreparedStatement query = connection.prepareStatement(
                        "SELECT * FROM T_CORE_ROLE WHERE NVL(LASTSUSPENDEDFLAG,'0') <> '1' ORDER BY ID")) {
            try (ResultSet rows = query.executeQuery()) {
                ResultSetMetaData metadata = rows.getMetaData();
                require(metadata, "ID");
                while (rows.next()) {
                    String id = rows.getString("ID");
                    if (id == null || id.length() == 0) throw new SQLException("role row has no ID");
                    String name = firstString(rows, metadata, "ROLENAME", "NAME", "ROLE_NAME");
                    result.add(new RoleRow(id, name == null ? "Role " + id : name));
                }
            }
        }
        return result;
    }

    private List<MenuRow> readMenus() throws SQLException {
        List<MenuRow> result = new ArrayList<MenuRow>();
        try (Connection connection = dataSource.getConnection();
                PreparedStatement query = connection.prepareStatement("SELECT * FROM T_CORE_MENU ORDER BY ORDERNO, ID")) {
            try (ResultSet rows = query.executeQuery()) {
                ResultSetMetaData metadata = rows.getMetaData();
                require(metadata, "ID");
                while (rows.next()) {
                    String id = rows.getString("ID");
                    if (id == null || id.length() == 0) throw new SQLException("menu row has no ID");
                    String name = firstString(rows, metadata, "MENUNAME", "MENUINITNAME", "NAME");
                    String initial = firstString(rows, metadata, "MENUINITNAME", "MENUNAME", "NAME");
                    String href = firstString(rows, metadata, "HREF", "URL", "PATH");
                    String parent = firstString(rows, metadata, "PARENTID", "PARENT_ID");
                    Long order = firstNumber(rows, metadata, "ORDERNO", "ORDER_NO", "SORTNO");
                    result.add(new MenuRow(id, name == null ? id : name, initial == null ? id : initial, href, parent, order));
                }
            }
        }
        return result;
    }

    private Map<String, Set<String>> readGrants() throws SQLException {
        Map<String, Set<String>> result = new LinkedHashMap<String, Set<String>>();
        try (Connection connection = dataSource.getConnection();
                PreparedStatement query = connection.prepareStatement(
                        "SELECT ROLEID,TARGETID FROM T_CORE_ROLE_PERMISSION WHERE PERMISSIONTYPEID='T_CORE_MENU'")) {
            try (ResultSet rows = query.executeQuery()) {
                while (rows.next()) {
                    String role = rows.getString(1), target = rows.getString(2);
                    if (role == null || target == null) continue;
                    Set<String> targets = result.get(role);
                    if (targets == null) { targets = new LinkedHashSet<String>(); result.put(role, targets); }
                    targets.add(target);
                }
            }
        }
        return result;
    }

    private static void setChildCounts(List<Map<String, Object>> nodes) {
        for (Map<String, Object> node : nodes) {
            @SuppressWarnings("unchecked") List<Map<String, Object>> children = (List<Map<String, Object>>) node.get("children");
            node.put("childQty", Integer.valueOf(children.size()));
            setChildCounts(children);
        }
    }

    private static void require(ResultSetMetaData metadata, String... names) throws SQLException {
        for (String name : names) {
            boolean found = false;
            for (int i = 1; i <= metadata.getColumnCount(); i++) {
                if (name.equalsIgnoreCase(metadata.getColumnLabel(i)) || name.equalsIgnoreCase(metadata.getColumnName(i))) { found = true; break; }
            }
            if (!found) throw new SQLException("matrix metadata column unavailable: " + name);
        }
    }

    private static String firstString(ResultSet rows, ResultSetMetaData metadata, String... names) throws SQLException {
        for (String name : names) if (has(metadata, name)) return rows.getString(name);
        return null;
    }

    private static Long firstNumber(ResultSet rows, ResultSetMetaData metadata, String... names) throws SQLException {
        for (String name : names) if (has(metadata, name)) {
            Number value = (Number) rows.getObject(name); return value == null ? null : Long.valueOf(value.longValue());
        }
        return null;
    }

    private static boolean has(ResultSetMetaData metadata, String name) throws SQLException {
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            if (name.equalsIgnoreCase(metadata.getColumnLabel(i)) || name.equalsIgnoreCase(metadata.getColumnName(i))) return true;
        }
        return false;
    }

    private static final class RoleRow {
        final String id, name;
        RoleRow(String id, String name) { this.id = id; this.name = name; }
        String field() { return "T_CORE_ROLE$ROLENAME$" + id; }
    }

    private static final class MenuRow {
        final String id, name, initialName, href, parentId; final Long orderNo;
        MenuRow(String id, String name, String initialName, String href, String parentId, Long orderNo) {
            this.id = id; this.name = name; this.initialName = initialName; this.href = href; this.parentId = parentId; this.orderNo = orderNo;
        }
    }
}
