package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.data.JdbcValue;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Bounded owned organization-management seam for the historical organization
 * tree.  It deliberately exposes only the observable tree and CRUD behavior;
 * every write remains loopback-only and requires the isolated role/menu admin
 * gate.
 */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedOrganizationManagementController {
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;
    private final Environment environment;

    public OwnedOrganizationManagementController(OwnedIdentityHttpBoundary boundary,
            DataSource dataSource, Environment environment) {
        if (boundary == null || dataSource == null || environment == null)
            throw new IllegalArgumentException("organization dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
        this.environment = environment;
    }

    @PostMapping(path = "/secure/core/module/mdm/orgs/queries/raw",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            requireAdmin(request);
            String parentId = text(readObject(body).get("parentId"));
            return ok(page(readOrganizations(parentId.length() == 0 ? null : parentId)));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(invalid.getMessage()); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = "/secure/core/module/mdm/orgs/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request, @PathVariable("id") String id) {
        try {
            requireAdmin(request);
            if (!validId(id)) throw new IllegalArgumentException("organization id is invalid");
            Map<String, Object> row = readDetail(id);
            return ok(JSON.toJSONString(row == null ? new LinkedHashMap<String, Object>() : row,
                    SerializerFeature.WriteMapNullValue));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(invalid.getMessage()); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = "/secure/core/module/mdm/orgs",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> insert(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        OwnedPrincipal principal = null;
        try {
            principal = requireWrite(request);
            OrganizationChange change = parseChange(body);
            final OwnedPrincipal actor = principal;
            OwnedWriteSupport.transaction(dataSource, connection -> {
                insertOrganization(connection, actor, change);
                return null;
            });
            boundary.recordAllowed("organization-insert", principal, change.id, "isolated-write");
            return ok("{\"id\":" + JSON.toJSONString(change.id) + "}");
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(invalid.getMessage()); }
        catch (SQLException failure) { denied(request); return unavailable(); }
    }

    @PutMapping(path = "/secure/core/module/mdm/orgs/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> update(HttpServletRequest request, @PathVariable("id") String id,
            @RequestBody(required = false) String body) {
        OwnedPrincipal principal = null;
        try {
            principal = requireWrite(request);
            OrganizationChange change = parseChange(body);
            if (!id.equals(change.id)) throw new IllegalArgumentException("organization id does not match route");
            final OwnedPrincipal actor = principal;
            OwnedWriteSupport.transaction(dataSource, connection -> {
                updateOrganization(connection, actor, change);
                return null;
            });
            boundary.recordAllowed("organization-update", principal, id, "isolated-write");
            return ok("{\"updated\":1}");
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(invalid.getMessage()); }
        catch (SQLException failure) { denied(request); return unavailable(); }
    }

    @DeleteMapping(path = "/secure/core/module/mdm/orgs",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> delete(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        OwnedPrincipal principal = null;
        try {
            principal = requireWrite(request);
            List<String> ids = parseIds(body);
            final OwnedPrincipal actor = principal;
            OwnedWriteSupport.transaction(dataSource, connection -> {
                for (String id : ids) deleteOrganization(connection, actor, id);
                return null;
            });
            boundary.recordAllowed("organization-delete", principal, String.join(",", ids), "isolated-write");
            return ok("{\"deleted\":" + ids.size() + "}");
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(invalid.getMessage()); }
        catch (SQLException failure) { denied(request); return unavailable(); }
    }

    @PostMapping(path = "/secure/core/module/sys/roles/queries/raw",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> roleQuery(HttpServletRequest request) {
        try { requireAdmin(request); return ok(page(readRoles())); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = "/secure/core/module/sys/roles/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> roleDetail(HttpServletRequest request, @PathVariable("id") Long id) {
        try {
            requireAdmin(request);
            if (id == null || id.longValue() < 0L) throw new IllegalArgumentException("role id is invalid");
            return ok(JSON.toJSONString(readRole(id), SerializerFeature.WriteMapNullValue));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(invalid.getMessage()); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = "/secure/core/module/sys/roles",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> roleInsert(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        OwnedPrincipal principal = null;
        try {
            principal = requireRoleWrite(request);
            RoleChange change = parseRoleChange(body, null);
            final OwnedPrincipal actor = principal;
            Long id = OwnedWriteSupport.transaction(dataSource, connection -> insertRole(connection, actor, change));
            boundary.recordAllowed("role-insert", principal, String.valueOf(id), "isolated-write");
            return ok("{\"id\":" + id + "}");
        } catch (AccessDeniedException denied) { deniedRole(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { deniedRole(request); return badRequest(invalid.getMessage()); }
        catch (SQLException failure) { deniedRole(request); return unavailable(); }
    }

    @PutMapping(path = "/secure/core/module/sys/roles/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> roleUpdate(HttpServletRequest request, @PathVariable("id") Long id,
            @RequestBody(required = false) String body) {
        OwnedPrincipal principal = null;
        try {
            principal = requireRoleWrite(request);
            RoleChange change = parseRoleChange(body, id);
            final OwnedPrincipal actor = principal;
            OwnedWriteSupport.transaction(dataSource, connection -> { updateRole(connection, actor, change); return null; });
            boundary.recordAllowed("role-update", principal, String.valueOf(id), "isolated-write");
            return ok("{\"updated\":1}");
        } catch (AccessDeniedException denied) { deniedRole(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { deniedRole(request); return badRequest(invalid.getMessage()); }
        catch (SQLException failure) { deniedRole(request); return unavailable(); }
    }

    @DeleteMapping(path = "/secure/core/module/sys/roles",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> roleDelete(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        OwnedPrincipal principal = null;
        try {
            principal = requireRoleWrite(request);
            List<Long> ids = parseRoleIds(body);
            final OwnedPrincipal actor = principal;
            OwnedWriteSupport.transaction(dataSource, connection -> { for (Long id : ids) deleteRole(connection, actor, id); return null; });
            boundary.recordAllowed("role-delete", principal, ids.toString(), "isolated-write");
            return ok("{\"deleted\":" + ids.size() + "}");
        } catch (AccessDeniedException denied) { deniedRole(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { deniedRole(request); return badRequest(invalid.getMessage()); }
        catch (SQLException failure) { deniedRole(request); return unavailable(); }
    }

    private OwnedPrincipal requireAdmin(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.ROLE_MENU_ADMIN);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private OwnedPrincipal requireWrite(HttpServletRequest request) throws SQLException {
        OwnedWriteSupport.requireLoopback(request);
        if (!"isolated".equals(environment.getProperty("owned.organization.write.mode")))
            throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
        return requireAdmin(request);
    }

    private OwnedPrincipal requireRoleWrite(HttpServletRequest request) throws SQLException {
        OwnedWriteSupport.requireLoopback(request);
        if (!"isolated".equals(environment.getProperty("owned.role.write.mode")))
            throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
        return requireAdmin(request);
    }

    private void denied(HttpServletRequest request) {
        boundary.recordRequestDenied(request == null ? null : request.getSession(false),
                "organization.write", "permission-denied");
    }

    private void deniedRole(HttpServletRequest request) {
        boundary.recordRequestDenied(request == null ? null : request.getSession(false), "role.write", "permission-denied");
    }

    private List<Map<String, Object>> readRoles() throws SQLException {
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement("SELECT * FROM T_CORE_ROLE ORDER BY ID")) {
            return rows(query);
        }
    }

    private Map<String, Object> readRole(Long id) throws SQLException {
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement("SELECT * FROM T_CORE_ROLE WHERE ID=?")) {
            query.setLong(1, id.longValue()); List<Map<String, Object>> values = rows(query);
            return values.isEmpty() ? new LinkedHashMap<String, Object>() : values.get(0);
        }
    }

    private static Long insertRole(Connection connection, OwnedPrincipal actor, RoleChange change) throws SQLException {
        List<String> columns = tableColumns(connection, "T_CORE_ROLE");
        List<String> names = new ArrayList<String>(); List<Object> values = new ArrayList<Object>();
        names.add("ID"); values.add(nextRoleId(connection));
        add(names, values, columns, "ROLENAME", change.name);
        add(names, values, columns, "LASTSUSPENDEDFLAG", change.suspended ? "1" : "0");
        add(names, values, columns, "USEDBYORGID", change.organization);
        if (!columns.contains("ROLENAME")) throw new IllegalStateException("role schema is incomplete");
        executeDynamic(connection, "INSERT INTO T_CORE_ROLE (" + String.join(",", names) + ") VALUES (" + placeholders(names.size()) + ")", values);
        return (Long) values.get(0);
    }

    private static void updateRole(Connection connection, OwnedPrincipal actor, RoleChange change) throws SQLException {
        if (!roleExists(connection, change.id)) throw new IllegalArgumentException("role does not exist");
        if (change.suspended && roleOwnsUser(connection, change.id, actor.getId())) throw new AccessDeniedException("cannot suspend current role");
        List<String> columns = tableColumns(connection, "T_CORE_ROLE"); List<String> sets = new ArrayList<String>(); List<Object> values = new ArrayList<Object>();
        addSet(sets, values, columns, "ROLENAME", change.name); addSet(sets, values, columns, "LASTSUSPENDEDFLAG", change.suspended ? "1" : "0"); addSet(sets, values, columns, "USEDBYORGID", change.organization);
        if (sets.isEmpty()) throw new IllegalArgumentException("role update is empty"); values.add(change.id);
        executeDynamic(connection, "UPDATE T_CORE_ROLE SET " + String.join(",", sets) + " WHERE ID=?", values);
    }

    private static void deleteRole(Connection connection, OwnedPrincipal actor, Long id) throws SQLException {
        if (!roleExists(connection, id)) throw new IllegalArgumentException("role does not exist");
        if (countLong(connection, "SELECT COUNT(*) FROM T_CORE_ROLE_USER WHERE ROLEID=?", id) != 0L || countLong(connection, "SELECT COUNT(*) FROM T_CORE_ROLE_PERMISSION WHERE ROLEID=?", id) != 0L)
            throw new IllegalArgumentException("role is still referenced");
        try (PreparedStatement delete = connection.prepareStatement("DELETE FROM T_CORE_ROLE WHERE ID=?")) { delete.setLong(1, id); if (delete.executeUpdate() != 1) throw new IllegalArgumentException("role does not exist"); }
    }

    private static boolean roleExists(Connection c, Long id) throws SQLException { return countLong(c, "SELECT COUNT(*) FROM T_CORE_ROLE WHERE ID=?", id) == 1L; }
    private static boolean roleOwnsUser(Connection c, Long role, String user) throws SQLException { try (PreparedStatement q=c.prepareStatement("SELECT 1 FROM T_CORE_ROLE_USER WHERE ROLEID=? AND USERID=?")){q.setLong(1,role);q.setString(2,user);try(ResultSet r=q.executeQuery()){return r.next();}} }
    private static long countLong(Connection c,String sql,Long id)throws SQLException{try(PreparedStatement q=c.prepareStatement(sql)){q.setLong(1,id);try(ResultSet r=q.executeQuery()){r.next();return r.getLong(1);}}}
    private static long nextRoleId(Connection c)throws SQLException{try(PreparedStatement q=c.prepareStatement("SELECT SEQ_CORE.NEXTVAL FROM DUAL");ResultSet r=q.executeQuery()){r.next();return r.getLong(1);}}
    private static List<String> tableColumns(Connection c,String table)throws SQLException{List<String> v=new ArrayList<String>();try(PreparedStatement q=c.prepareStatement("SELECT * FROM "+table+" WHERE 1=0")){try(ResultSet r=q.executeQuery()){ResultSetMetaData m=r.getMetaData();for(int i=1;i<=m.getColumnCount();i++)v.add(m.getColumnName(i).toUpperCase());}}return v;}

    private static RoleChange parseRoleChange(String body, Long pathId) {
        JSONObject object = readObject(body); if (object.containsKey("p")) object = object.getJSONObject("p");
        Long id = pathId == null ? number(object.get("id")) : pathId; String name = text(object.get("roleName")); if (name.length()==0) name=text(object.get("rolename"));
        if (name.length()==0 || name.length()>100) throw new IllegalArgumentException("role name is invalid");
        Object suspendedValue=object.get("lastSuspendedFlag"); if(suspendedValue==null)suspendedValue=object.get("suspended"); boolean suspended="1".equals(text(suspendedValue)) || "true".equalsIgnoreCase(text(suspendedValue));
        String org=text(object.get("usedByOrgId")); if(org.length()==0) org=text(object.get("organization")); return new RoleChange(id,name,suspended,org.length()==0?null:org);
    }
    private static List<Long> parseRoleIds(String body){JSONObject o=readObject(body);Object raw=o.get("ids");if(!(raw instanceof JSONArray))throw new IllegalArgumentException("role ids are required");List<Long> out=new ArrayList<Long>();for(Object v:(JSONArray)raw){Long id=number(v);if(id==null||id<0)throw new IllegalArgumentException("role id is invalid");out.add(id);}return out;}
    private static Long number(Object v){if(v==null||text(v).length()==0)return null;try{return Long.valueOf(text(v));}catch(NumberFormatException e){throw new IllegalArgumentException("role id is invalid");}}

    private List<Map<String, Object>> readOrganizations(String parentId) throws SQLException {
        String sql = "SELECT * FROM T_CORE_ORG" + (parentId == null ? "" : " WHERE PARENTID=?")
                + " ORDER BY ID";
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql)) {
            if (parentId != null) query.setString(1, parentId);
            return rows(query);
        }
    }

    private Map<String, Object> readDetail(String id) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement query = connection.prepareStatement("SELECT * FROM T_CORE_ORG WHERE ID=?")) {
            query.setString(1, id);
            List<Map<String, Object>> rows = rows(query);
            return rows.isEmpty() ? null : rows.get(0);
        }
    }

    private static List<Map<String, Object>> rows(PreparedStatement query) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        try (ResultSet rows = query.executeQuery()) {
            ResultSetMetaData metadata = rows.getMetaData();
            while (rows.next()) {
                Map<String, Object> value = new LinkedHashMap<String, Object>();
                for (int i = 1; i <= metadata.getColumnCount(); i++) {
                    String name = metadata.getColumnLabel(i);
                    Object item = JdbcValue.normalize(rows.getObject(i));
                    String key = friendly(name);
                    value.put(key, item);
                    if ("id".equalsIgnoreCase(key) || "orgname".equalsIgnoreCase(key)
                            || "parentid".equalsIgnoreCase(key) || "orgcategory".equalsIgnoreCase(key)
                            || "orderno".equalsIgnoreCase(key) || "lastsuspendedflag".equalsIgnoreCase(key)) {
                        value.put(key, item);
                    }
                }
                result.add(value);
            }
        }
        return result;
    }

    private static String page(List<Map<String, Object>> rows) {
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("total", rows.size()); body.put("pageNumber", 1); body.put("pageSize", rows.size()); body.put("rows", rows);
        return JSON.toJSONString(body, SerializerFeature.WriteMapNullValue);
    }

    private static void insertOrganization(Connection connection, OwnedPrincipal actor,
            OrganizationChange change) throws SQLException {
        if (exists(connection, change.id)) throw new IllegalArgumentException("organization already exists");
        if (change.parentId != null && !exists(connection, change.parentId)) throw new IllegalArgumentException("parent organization does not exist");
        List<String> columns = columns(connection);
        List<String> insert = new ArrayList<String>();
        List<Object> values = new ArrayList<Object>();
        add(insert, values, columns, "ID", change.id);
        add(insert, values, columns, "ORGNAME", change.name);
        add(insert, values, columns, "PARENTID", change.parentId);
        add(insert, values, columns, "ORGCATEGORY", change.category);
        add(insert, values, columns, "ORDERNO", change.orderNo);
        add(insert, values, columns, "LASTSUSPENDEDFLAG", "0");
        if (insert.size() < 2) throw new IllegalStateException("organization schema is incomplete");
        executeDynamic(connection, "INSERT INTO T_CORE_ORG (" + String.join(",", insert) + ") VALUES (" + placeholders(insert.size()) + ")", values);
    }

    private static void updateOrganization(Connection connection, OwnedPrincipal actor,
            OrganizationChange change) throws SQLException {
        if (!exists(connection, change.id)) throw new IllegalArgumentException("organization does not exist");
        List<String> columns = columns(connection), sets = new ArrayList<String>();
        List<Object> values = new ArrayList<Object>();
        addSet(sets, values, columns, "ORGNAME", change.name);
        addSet(sets, values, columns, "PARENTID", change.parentId);
        addSet(sets, values, columns, "ORGCATEGORY", change.category);
        addSet(sets, values, columns, "ORDERNO", change.orderNo);
        if (sets.isEmpty()) throw new IllegalArgumentException("organization update is empty");
        values.add(change.id);
        executeDynamic(connection, "UPDATE T_CORE_ORG SET " + String.join(",", sets) + " WHERE ID=?", values);
    }

    private static void deleteOrganization(Connection connection, OwnedPrincipal actor, String id) throws SQLException {
        if (!exists(connection, id)) throw new IllegalArgumentException("organization does not exist");
        if (count(connection, "SELECT COUNT(*) FROM T_CORE_ORG WHERE PARENTID=?", id) != 0)
            throw new IllegalArgumentException("organization has child organizations");
        if (count(connection, "SELECT COUNT(*) FROM T_CORE_USER WHERE ORGID=?", id) != 0)
            throw new AccessDeniedException("organization has users");
        try (PreparedStatement delete = connection.prepareStatement("DELETE FROM T_CORE_ORG WHERE ID=?")) {
            delete.setString(1, id); if (delete.executeUpdate() != 1) throw new IllegalArgumentException("organization does not exist");
        }
    }

    private static long count(Connection connection, String sql, String id) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement(sql)) { query.setString(1, id); try (ResultSet rows = query.executeQuery()) { rows.next(); return rows.getLong(1); } }
    }

    private static boolean exists(Connection connection, String id) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("SELECT 1 FROM T_CORE_ORG WHERE ID=?")) { query.setString(1, id); try (ResultSet rows = query.executeQuery()) { return rows.next(); } }
    }

    private static List<String> columns(Connection connection) throws SQLException {
        List<String> result = new ArrayList<String>();
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM T_CORE_ORG WHERE 1=0")) {
            try (ResultSet rows = query.executeQuery()) { ResultSetMetaData metadata = rows.getMetaData(); for (int i=1;i<=metadata.getColumnCount();i++) result.add(metadata.getColumnName(i).toUpperCase()); }
        }
        return result;
    }

    private static void add(List<String> names, List<Object> values, List<String> columns, String column, Object value) {
        if (columns.contains(column) && value != null) { names.add(column); values.add(value); }
    }

    private static void addSet(List<String> sets, List<Object> values, List<String> columns, String column, Object value) {
        if (columns.contains(column) && value != null) { sets.add(column + "=?"); values.add(value); }
    }

    private static void executeDynamic(Connection connection, String sql, List<Object> values) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) { for (int i=0;i<values.size();i++) statement.setObject(i+1, values.get(i)); statement.executeUpdate(); }
    }

    private static String placeholders(int count) { StringBuilder value = new StringBuilder(); for (int i=0;i<count;i++) { if (i>0) value.append(','); value.append('?'); } return value.toString(); }

    private static OrganizationChange parseChange(String body) {
        JSONObject object = readObject(body); if (object.containsKey("p")) object = object.getJSONObject("p");
        String id = text(object.get("id")); String name = text(object.get("orgName"));
        if (!validId(id) || name.length() == 0 || name.length() > 100) throw new IllegalArgumentException("organization id/name is invalid");
        String parent = text(object.get("parentId")); String category = text(object.get("orgCategory"));
        Integer order = null; if (object.get("orderNo") != null && text(object.get("orderNo")).length() != 0) { try { order = Integer.valueOf(text(object.get("orderNo"))); } catch (NumberFormatException e) { throw new IllegalArgumentException("organization order is invalid"); } }
        return new OrganizationChange(id, name, parent.length() == 0 ? null : parent, category.length() == 0 ? null : category, order);
    }

    private static List<String> parseIds(String body) {
        JSONObject object = readObject(body); Object raw = object.get("ids"); if (raw == null) raw = object.get("p");
        List<String> result = new ArrayList<String>();
        if (raw instanceof JSONArray) for (Object item : (JSONArray) raw) result.add(validatedId(text(item)));
        else if (raw != null) result.add(validatedId(text(raw)));
        else throw new IllegalArgumentException("organization ids are required");
        return result;
    }

    private static String validatedId(String id) { if (!validId(id)) throw new IllegalArgumentException("organization id is invalid"); return id; }
    private static boolean validId(String id) { return id != null && id.matches("[A-Za-z0-9_-]{1,64}"); }
    private static String text(Object value) { return value == null ? "" : String.valueOf(value).trim(); }
    private static JSONObject readObject(String body) { if (body == null || body.trim().length() == 0) return new JSONObject(); try { Object value = JSON.parse(body); if (!(value instanceof JSONObject)) throw new IllegalArgumentException("request must be an object"); return (JSONObject) value; } catch (RuntimeException failure) { throw new IllegalArgumentException("invalid JSON", failure); } }
    private static String friendly(String name) {
        String upper = name.toUpperCase();
        if ("ID".equals(upper)) return "id";
        if ("ORGNAME".equals(upper)) return "orgName";
        if ("PARENTID".equals(upper)) return "parentId";
        if ("USEDBYORGID".equals(upper)) return "usedByOrgId";
        if ("ORGCATEGORY".equals(upper)) return "orgCategory";
        if ("ORDERNO".equals(upper)) return "orderNo";
        if ("LASTSUSPENDEDFLAG".equals(upper)) return "lastSuspendedFlag";
        StringBuilder result = new StringBuilder(); boolean upperNext=false; for (char c:name.toLowerCase().toCharArray()) { if (c=='_') { upperNext=true; continue; } result.append(upperNext ? Character.toUpperCase(c) : c); upperNext=false; } return result.toString();
    }
    private ResponseEntity<String> ok(String body) { return ResponseEntity.ok().header("Cache-Control", "no-store").body(body); }
    private ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}"); }
    private ResponseEntity<String> badRequest(String message) { return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.ORGANIZATION.INVALID_REQUEST\"}"); }
    private ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.ORGANIZATION.UNAVAILABLE\"}"); }
    private static final class OrganizationChange { final String id,name,parentId,category; final Integer orderNo; OrganizationChange(String id,String name,String parentId,String category,Integer orderNo){this.id=id;this.name=name;this.parentId=parentId;this.category=category;this.orderNo=orderNo;} }
    private static final class RoleChange { final Long id; final String name,organization; final boolean suspended; RoleChange(Long id,String name,boolean suspended,String organization){this.id=id;this.name=name;this.suspended=suspended;this.organization=organization;} }
}
