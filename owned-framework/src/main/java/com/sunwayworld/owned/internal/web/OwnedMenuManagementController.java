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

/** Bounded global menu CRUD seam for the role/menu administration surface. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedMenuManagementController {
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;
    private final Environment environment;

    public OwnedMenuManagementController(OwnedIdentityHttpBoundary boundary,
            DataSource dataSource, Environment environment) {
        if (boundary == null || dataSource == null || environment == null)
            throw new IllegalArgumentException("menu dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
        this.environment = environment;
    }

    @PostMapping(path = "/secure/core/module/sys/menus/queries/raw",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request) {
        try {
            requireAdmin(request);
            return ok(JSON.toJSONString(page(readMenus()), SerializerFeature.WriteMapNullValue));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = "/secure/core/module/sys/menus/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request, @PathVariable("id") String id) {
        try {
            requireAdmin(request);
            if (!validId(id)) throw new IllegalArgumentException("menu id is invalid");
            Map<String, Object> row = readMenu(id);
            return ok(JSON.toJSONString(row == null ? new LinkedHashMap<String, Object>() : row,
                    SerializerFeature.WriteMapNullValue));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(invalid.getMessage()); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = "/secure/core/module/sys/menus",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> insert(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        OwnedPrincipal principal = null;
        try {
            principal = requireWrite(request);
            MenuChange change = parseChange(body, null);
            final MenuChange value = change;
            OwnedWriteSupport.transaction(dataSource, connection -> {
                insertMenu(connection, value);
                return null;
            });
            boundary.recordAllowed("menu-insert", principal, change.id, "isolated-write");
            return ok("{\"id\":" + JSON.toJSONString(change.id) + "}");
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(invalid.getMessage()); }
        catch (SQLException failure) { denied(request); return unavailable(); }
    }

    @PutMapping(path = "/secure/core/module/sys/menus/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> update(HttpServletRequest request, @PathVariable("id") String id,
            @RequestBody(required = false) String body) {
        OwnedPrincipal principal = null;
        try {
            principal = requireWrite(request);
            MenuChange change = parseChange(body, id);
            final MenuChange value = change;
            OwnedWriteSupport.transaction(dataSource, connection -> {
                updateMenu(connection, value);
                return null;
            });
            boundary.recordAllowed("menu-update", principal, id, "isolated-write");
            return ok("{\"updated\":1}");
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(invalid.getMessage()); }
        catch (SQLException failure) { denied(request); return unavailable(); }
    }

    @DeleteMapping(path = "/secure/core/module/sys/menus",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> delete(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        OwnedPrincipal principal = null;
        try {
            principal = requireWrite(request);
            List<String> ids = parseIds(body);
            final List<String> values = ids;
            OwnedWriteSupport.transaction(dataSource, connection -> {
                for (String id : values) deleteMenu(connection, id);
                return null;
            });
            boundary.recordAllowed("menu-delete", principal, String.join(",", ids), "isolated-write");
            return ok("{\"deleted\":" + ids.size() + "}");
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(invalid.getMessage()); }
        catch (SQLException failure) { denied(request); return unavailable(); }
    }

    private OwnedPrincipal requireAdmin(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.ROLE_MENU_ADMIN);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private OwnedPrincipal requireWrite(HttpServletRequest request) throws SQLException {
        OwnedWriteSupport.requireLoopback(request);
        if (!"isolated".equals(environment.getProperty("owned.menu.write.mode")))
            throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
        return requireAdmin(request);
    }

    private void denied(HttpServletRequest request) {
        boundary.recordRequestDenied(request == null ? null : request.getSession(false),
                "menu.write", "permission-denied");
    }

    private static void insertMenu(Connection connection, MenuChange change) throws SQLException {
        if (exists(connection, change.id)) throw new IllegalArgumentException("menu already exists");
        if (change.parentId != null && !exists(connection, change.parentId))
            throw new IllegalArgumentException("parent menu does not exist");
        List<String> columns = columns(connection), names = new ArrayList<String>();
        List<Object> values = new ArrayList<Object>();
        add(names, values, columns, "ID", change.id);
        add(names, values, columns, "MENUNAME", change.name);
        add(names, values, columns, "MENUINITNAME", change.initialName);
        add(names, values, columns, "HREF", change.href);
        add(names, values, columns, "PARENTID", change.parentId);
        add(names, values, columns, "ORDERNO", change.orderNo);
        add(names, values, columns, "ICON", change.icon);
        add(names, values, columns, "MENUICON", change.menuIcon);
        add(names, values, columns, "TODOICON", change.todoIcon);
        add(names, values, columns, "I18NCODE", change.i18nCode);
        if (!columns.contains("ID") || !columns.contains("MENUNAME"))
            throw new IllegalStateException("menu schema is incomplete");
        execute(connection, "INSERT INTO T_CORE_MENU (" + String.join(",", names)
                + ") VALUES (" + placeholders(names.size()) + ")", values);
    }

    private static void updateMenu(Connection connection, MenuChange change) throws SQLException {
        if (!exists(connection, change.id)) throw new IllegalArgumentException("menu does not exist");
        if (change.parentId != null && !exists(connection, change.parentId))
            throw new IllegalArgumentException("parent menu does not exist");
        List<String> columns = columns(connection), sets = new ArrayList<String>();
        List<Object> values = new ArrayList<Object>();
        addSet(sets, values, columns, "MENUNAME", change.name);
        addSet(sets, values, columns, "MENUINITNAME", change.initialName);
        addSet(sets, values, columns, "HREF", change.href);
        addSet(sets, values, columns, "PARENTID", change.parentId);
        addSet(sets, values, columns, "ORDERNO", change.orderNo);
        addSet(sets, values, columns, "ICON", change.icon);
        addSet(sets, values, columns, "MENUICON", change.menuIcon);
        addSet(sets, values, columns, "TODOICON", change.todoIcon);
        addSet(sets, values, columns, "I18NCODE", change.i18nCode);
        if (sets.isEmpty()) throw new IllegalArgumentException("menu update is empty");
        values.add(change.id);
        execute(connection, "UPDATE T_CORE_MENU SET " + String.join(",", sets) + " WHERE ID=?", values);
    }

    private static void deleteMenu(Connection connection, String id) throws SQLException {
        if (!exists(connection, id)) throw new IllegalArgumentException("menu does not exist");
        if (count(connection, "SELECT COUNT(*) FROM T_CORE_MENU WHERE PARENTID=?", id) != 0)
            throw new IllegalArgumentException("menu has child menus");
        if (count(connection, "SELECT COUNT(*) FROM T_CORE_ROLE_PERMISSION WHERE PERMISSIONTYPEID='T_CORE_MENU' AND TARGETID=?", id) != 0)
            throw new IllegalArgumentException("menu is still referenced by a role");
        try (PreparedStatement delete = connection.prepareStatement("DELETE FROM T_CORE_MENU WHERE ID=?")) {
            delete.setString(1, id);
            if (delete.executeUpdate() != 1) throw new IllegalArgumentException("menu does not exist");
        }
    }

    private List<Map<String, Object>> readMenus() throws SQLException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement query = connection.prepareStatement("SELECT * FROM T_CORE_MENU ORDER BY ORDERNO, ID")) {
            return rows(query);
        }
    }

    private Map<String, Object> readMenu(String id) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement query = connection.prepareStatement("SELECT * FROM T_CORE_MENU WHERE ID=?")) {
            query.setString(1, id);
            List<Map<String, Object>> values = rows(query);
            return values.isEmpty() ? null : values.get(0);
        }
    }

    private static List<Map<String, Object>> rows(PreparedStatement query) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        try (ResultSet rows = query.executeQuery()) {
            ResultSetMetaData metadata = rows.getMetaData();
            while (rows.next()) {
                Map<String, Object> value = new LinkedHashMap<String, Object>();
                for (int i = 1; i <= metadata.getColumnCount(); i++)
                    value.put(friendly(metadata.getColumnLabel(i)), JdbcValue.normalize(rows.getObject(i)));
                result.add(value);
            }
        }
        return result;
    }

    private static Map<String, Object> page(List<Map<String, Object>> rows) {
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("total", rows.size()); body.put("pageNumber", 1); body.put("pageSize", rows.size()); body.put("rows", rows);
        return body;
    }

    private static MenuChange parseChange(String body, String pathId) {
        JSONObject object = readObject(body);
        if (object.containsKey("p")) object = object.getJSONObject("p");
        String id = pathId == null ? text(object.get("id")) : pathId;
        if (!validId(id)) throw new IllegalArgumentException("menu id is invalid");
        String name = text(object.get("menuName")); if (name.length() == 0) name = text(object.get("menuname"));
        if (name.length() == 0 || name.length() > 100) throw new IllegalArgumentException("menu name is invalid");
        String initial = text(object.get("menuInitName")); if (initial.length() == 0) initial = name;
        String parent = text(object.get("parentId"));
        Integer order = null;
        if (object.get("orderNo") != null && text(object.get("orderNo")).length() != 0) {
            try { order = Integer.valueOf(text(object.get("orderNo"))); }
            catch (NumberFormatException failure) { throw new IllegalArgumentException("menu order is invalid"); }
        }
        return new MenuChange(id, name, initial, text(object.get("href")),
                parent.length() == 0 ? null : parent, order, text(object.get("icon")),
                text(object.get("menuIcon")), text(object.get("todoIcon")), text(object.get("i18nCode")));
    }

    private static List<String> parseIds(String body) {
        JSONObject object = readObject(body); Object raw = object.get("ids");
        if (!(raw instanceof JSONArray)) throw new IllegalArgumentException("menu ids are required");
        List<String> result = new ArrayList<String>();
        for (Object item : (JSONArray) raw) { String id = text(item); if (!validId(id)) throw new IllegalArgumentException("menu id is invalid"); result.add(id); }
        if (result.isEmpty() || result.size() > 100) throw new IllegalArgumentException("menu ids are invalid");
        return result;
    }

    private static JSONObject readObject(String body) {
        if (body == null || body.trim().length() == 0) return new JSONObject();
        try { Object value = JSON.parse(body); if (!(value instanceof JSONObject)) throw new IllegalArgumentException("request must be an object"); return (JSONObject) value; }
        catch (RuntimeException failure) { throw new IllegalArgumentException("invalid JSON", failure); }
    }

    private static boolean exists(Connection connection, String id) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("SELECT 1 FROM T_CORE_MENU WHERE ID=?")) { query.setString(1, id); try (ResultSet rows = query.executeQuery()) { return rows.next(); } }
    }

    private static long count(Connection connection, String sql, String id) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement(sql)) { query.setString(1, id); try (ResultSet rows = query.executeQuery()) { rows.next(); return rows.getLong(1); } }
    }

    private static List<String> columns(Connection connection) throws SQLException {
        List<String> result = new ArrayList<String>();
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM T_CORE_MENU WHERE 1=0")) {
            try (ResultSet rows = query.executeQuery()) { ResultSetMetaData metadata = rows.getMetaData(); for (int i=1;i<=metadata.getColumnCount();i++) result.add(metadata.getColumnName(i).toUpperCase()); }
        }
        return result;
    }

    private static void add(List<String> names, List<Object> values, List<String> columns, String column, Object value) { if (columns.contains(column) && value != null) { names.add(column); values.add(value); } }
    private static void addSet(List<String> sets, List<Object> values, List<String> columns, String column, Object value) { if (columns.contains(column) && value != null) { sets.add(column + "=?"); values.add(value); } }
    private static void execute(Connection connection, String sql, List<Object> values) throws SQLException { try (PreparedStatement statement=connection.prepareStatement(sql)){for(int i=0;i<values.size();i++)statement.setObject(i+1,values.get(i)); statement.executeUpdate();} }
    private static String placeholders(int count) { StringBuilder result=new StringBuilder(); for(int i=0;i<count;i++){if(i>0)result.append(',');result.append('?');} return result.toString(); }
    private static boolean validId(String id) { return id != null && id.matches("[A-Za-z0-9_-]{1,64}"); }
    private static String text(Object value) { return value == null ? "" : String.valueOf(value).trim(); }
    private static String friendly(String name) { String upper=name.toUpperCase(); if("ID".equals(upper))return "id"; if("MENUNAME".equals(upper))return "menuName"; if("MENUINITNAME".equals(upper))return "menuInitName"; if("HREF".equals(upper))return "href"; if("PARENTID".equals(upper))return "parentId"; if("ORDERNO".equals(upper))return "orderNo"; StringBuilder result=new StringBuilder();boolean next=false;for(char c:name.toLowerCase().toCharArray()){if(c=='_'){next=true;continue;}result.append(next?Character.toUpperCase(c):c);next=false;}return result.toString(); }
    private ResponseEntity<String> ok(String body) { return ResponseEntity.ok().header("Cache-Control", "no-store").body(body); }
    private ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}"); }
    private ResponseEntity<String> badRequest(String message) { return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.MENU.INVALID_REQUEST\"}"); }
    private ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.MENU.UNAVAILABLE\"}"); }
    private static final class MenuChange { final String id,name,initialName,href,parentId,icon,menuIcon,todoIcon,i18nCode; final Integer orderNo; MenuChange(String id,String name,String initialName,String href,String parentId,Integer orderNo,String icon,String menuIcon,String todoIcon,String i18nCode){this.id=id;this.name=name;this.initialName=initialName;this.href=href;this.parentId=parentId;this.orderNo=orderNo;this.icon=icon;this.menuIcon=menuIcon;this.todoIcon=todoIcon;this.i18nCode=i18nCode;} }
}
