package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Bounded current-user menu seam. It returns only menu IDs backed by the
 * existing role/menu tables; names, hierarchy and administration remain
 * outside this read slice until their schema and behavior are evidenced.
 */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedMenuController {
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedMenuController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("menu dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @GetMapping(path = "/secure/core/module/sys/menus", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> currentMenus(HttpServletRequest request) {
        try {
            String userId = boundary.currentPrincipal(request.getSession(false)).getId();
            // Reuse the same tree read as the supplier-shaped POST endpoint.
            // The former DISTINCT menu-id query can scan the large role/menu
            // join independently and make the legacy GET hang on a real
            // sss2 schema.  Flattening the already scoped tree keeps both
            // endpoints on one bounded SQL path and preserves revocation.
            return ResponseEntity.ok().header("Cache-Control", "no-store")
                    .body(json(readPermittedMenuIds(readPermittedMenuTree(userId))));
        } catch (AccessDeniedException | org.springframework.security.core.AuthenticationException denied) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");
        } catch (SQLException failure) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.MENU.UNAVAILABLE\"}");
        }
    }

    /**
     * Supplier-compatible menu tree.  The supplier endpoint returns the raw
     * tree array (rather than the ID envelope used by currentMenus).  Menu
     * rows are selected only after the current identity has been refreshed,
     * so revocation and role suspension take effect for every request.
     */
    @PostMapping(path = "/secure/core/module/sys/menus/tree/root",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> currentMenuTree(HttpServletRequest request) {
        try {
            String userId = boundary.currentPrincipal(request.getSession(false)).getId();
            return ResponseEntity.ok().header("Cache-Control", "no-store")
                    .body(JSON.toJSONString(readPermittedMenuTree(userId), SerializerFeature.WriteMapNullValue));
        } catch (AccessDeniedException | org.springframework.security.core.AuthenticationException denied) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");
        } catch (SQLException failure) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.MENU.UNAVAILABLE\"}");
        }
    }

    private List<String> readPermittedMenuIds(List<Map<String, Object>> tree) {
        List<String> ids = new ArrayList<String>();
        flattenIds(tree, ids);
        java.util.Collections.sort(ids);
        return ids;
    }

    @SuppressWarnings("unchecked")
    private static void flattenIds(List<Map<String, Object>> nodes, List<String> ids) {
        if (nodes == null) return;
        for (Map<String, Object> node : nodes) {
            if (node == null) continue;
            Object id = node.get("id");
            if (id != null) ids.add(String.valueOf(id));
            Object children = node.get("children");
            if (children instanceof List<?>) flattenIds((List<Map<String, Object>>) children, ids);
        }
    }

    private List<Map<String, Object>> readPermittedMenuTree(String userId) throws SQLException {
        String sql = "SELECT M.* FROM T_CORE_MENU M WHERE EXISTS ("
                + "SELECT 1 FROM T_CORE_ROLE_USER RU JOIN T_CORE_ROLE R ON R.ID=RU.ROLEID "
                + "JOIN T_CORE_ROLE_PERMISSION P ON P.ROLEID=R.ID "
                + "WHERE RU.USERID=? AND R.LASTSUSPENDEDFLAG='0' "
                + "AND P.PERMISSIONTYPEID='T_CORE_MENU' AND (P.TARGETID=M.ID OR P.TARGETID='0')) "
                + "ORDER BY M.ORDERNO, M.ID";
        List<MenuRow> rows = new ArrayList<MenuRow>();
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql)) {
            query.setString(1, userId);
            try (ResultSet result = query.executeQuery()) {
                ResultSetMetaData metadata = result.getMetaData();
                requireColumns(metadata, "ID", "MENUNAME", "MENUINITNAME", "HREF", "PARENTID", "ORDERNO");
                while (result.next()) rows.add(MenuRow.read(result, metadata));
            }
        }
        return MenuRow.tree(rows);
    }

    private static void requireColumns(ResultSetMetaData metadata, String... required) throws SQLException {
        for (String name : required) {
            boolean found = false;
            for (int i = 1; i <= metadata.getColumnCount(); i++) {
                if (name.equalsIgnoreCase(metadata.getColumnLabel(i)) || name.equalsIgnoreCase(metadata.getColumnName(i))) {
                    found = true; break;
                }
            }
            if (!found) throw new SQLException("T_CORE_MENU metadata column unavailable: " + name);
        }
    }

    private static String json(List<String> ids) {
        StringBuilder body = new StringBuilder("{\"menus\":[");
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) body.append(',');
            body.append("{\"id\":").append(JSON.toJSONString(ids.get(i))).append('}');
        }
        return body.append("]}").toString();
    }

    private static final class MenuRow {
        private final String id;
        private final String menuName;
        private final String menuInitName;
        private final String parentId;
        private final Map<String, Object> values;

        private MenuRow(String id, String menuName, String menuInitName, String parentId,
                Long orderNo, Map<String, Object> values) {
            this.id = id; this.menuName = menuName; this.menuInitName = menuInitName;
            this.parentId = parentId; this.values = values;
        }

        private static MenuRow read(ResultSet result, ResultSetMetaData metadata) throws SQLException {
            String id = result.getString("ID");
            if (id == null || id.length() == 0) throw new SQLException("T_CORE_MENU row has no ID");
            String menuName = result.getString("MENUNAME");
            String menuInitName = result.getString("MENUINITNAME");
            String parentId = result.getString("PARENTID");
            Number order = (Number) result.getObject("ORDERNO");
            Long orderNo = order == null ? null : Long.valueOf(order.longValue());
            Map<String, Object> values = new LinkedHashMap<String, Object>();
            put(values, "id", id);
            put(values, "menuName", menuName);
            put(values, "menuInitName", menuInitName);
            put(values, "icon", string(result, metadata, "ICON"));
            put(values, "todoIcon", string(result, metadata, "TODOICON"));
            put(values, "href", result.getString("HREF"));
            put(values, "parentId", parentId);
            put(values, "i18nCode", string(result, metadata, "I18NCODE"));
            put(values, "orderNo", orderNo);
            put(values, "resourceId", number(result, metadata, "RESOURCEID"));
            put(values, "resourceName", string(result, metadata, "RESOURCENAME"));
            put(values, "resourcePath", string(result, metadata, "RESOURCEPATH"));
            put(values, "menuParam", string(result, metadata, "MENUPARAM"));
            put(values, "publishTime", timestamp(result, metadata, "PUBLISHTIME"));
            put(values, "publishUserName", string(result, metadata, "PUBLISHUSERNAME"));
            Map<String, Object> extension = new LinkedHashMap<String, Object>();
            String menuIcon = string(result, metadata, "MENUICON");
            // The supplier always exposes this extension key, including when
            // the underlying menu has no icon value.  Keep the null entry so
            // the JSON field shape remains stable for the original shell.
            extension.put("menuiconurl", menuIcon);
            put(values, "ext$", extension);
            return new MenuRow(id, menuName, menuInitName, parentId, orderNo, values);
        }

        private static List<Map<String, Object>> tree(List<MenuRow> rows) {
            Map<String, Map<String, Object>> nodes = new LinkedHashMap<String, Map<String, Object>>();
            for (MenuRow row : rows) {
                Map<String, Object> node = new LinkedHashMap<String, Object>(row.values);
                node.put("text", row.menuName == null ? row.menuInitName : row.menuName);
                node.put("lazyLoad", Boolean.TRUE);
                node.put("checked", Boolean.FALSE);
                node.put("expanded", Boolean.FALSE);
                node.put("lvl", Integer.valueOf(1));
                node.put("childQty", Integer.valueOf(0));
                node.put("children", new ArrayList<Map<String, Object>>());
                nodes.put(row.id, node);
            }
            List<Map<String, Object>> roots = new ArrayList<Map<String, Object>>();
            for (MenuRow row : rows) {
                Map<String, Object> node = nodes.get(row.id);
                Map<String, Object> parent = row.parentId == null ? null : nodes.get(row.parentId);
                if (parent == null || row.id.equals(row.parentId) || cycle(row, rows)) {
                    roots.add(node);
                } else {
                    @SuppressWarnings("unchecked") List<Map<String, Object>> list = (List<Map<String, Object>>) parent.get("children");
                    list.add(node);
                }
            }
            assignLevels(roots, 1);
            return roots;
        }

        private static boolean cycle(MenuRow candidate, List<MenuRow> rows) {
            if (candidate.parentId == null) return false;
            String current = candidate.parentId;
            for (int i = 0; i <= rows.size(); i++) {
                if (candidate.id.equals(current)) return true;
                MenuRow parent = null;
                for (MenuRow row : rows) if (row.id.equals(current)) { parent = row; break; }
                if (parent == null || parent.parentId == null) return false;
                current = parent.parentId;
            }
            return true;
        }

        private static void assignLevels(List<Map<String, Object>> nodes, int level) {
            for (Map<String, Object> node : nodes) {
                node.put("lvl", Integer.valueOf(level));
                @SuppressWarnings("unchecked") List<Map<String, Object>> child = (List<Map<String, Object>>) node.get("children");
                node.put("childQty", Integer.valueOf(child.size()));
                assignLevels(child, level + 1);
            }
        }

        private static void put(Map<String, Object> values, String key, Object value) { values.put(key, value); }

        private static boolean has(ResultSetMetaData metadata, String name) throws SQLException {
            for (int i = 1; i <= metadata.getColumnCount(); i++) {
                if (name.equalsIgnoreCase(metadata.getColumnLabel(i)) || name.equalsIgnoreCase(metadata.getColumnName(i))) return true;
            }
            return false;
        }
        private static String string(ResultSet result, ResultSetMetaData metadata, String column) throws SQLException {
            return has(metadata, column) ? result.getString(column) : null;
        }
        private static Long number(ResultSet result, ResultSetMetaData metadata, String column) throws SQLException {
            if (!has(metadata, column)) return null;
            Number value = (Number) result.getObject(column);
            return value == null ? null : Long.valueOf(value.longValue());
        }
        private static String timestamp(ResultSet result, ResultSetMetaData metadata, String column) throws SQLException {
            if (!has(metadata, column)) return null;
            Timestamp value = result.getTimestamp(column);
            return value == null ? null : value.toInstant().toString();
        }
    }
}
