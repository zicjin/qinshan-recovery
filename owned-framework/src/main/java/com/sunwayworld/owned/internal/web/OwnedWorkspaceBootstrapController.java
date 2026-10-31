package com.sunwayworld.owned.internal.web;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/** Read-only bootstrap projections used by the imported supplier workspace. */
@RestController
public final class OwnedWorkspaceBootstrapController {
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedWorkspaceBootstrapController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("workspace dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @GetMapping(path = "/secure/core/module/item/workspace-user-schemes/layouts",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> layouts(HttpServletRequest request) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        List<Map<String, Object>> panels = new ArrayList<Map<String, Object>>();
        try {
            String userId = boundary.currentPrincipal(request.getSession(false)).getId();
            Long schemeId = readSchemeId(userId);
            if (schemeId != null) panels.addAll(readPanels(schemeId));
        } catch (SQLException failure) {
            // The supplier keeps this optional home bootstrap non-fatal.  Keep
            // the same 200/empty contract if the isolated clone lacks a
            // workspace row; the navigation and business routes remain usable.
            panels.clear();
        } catch (org.springframework.security.access.AccessDeniedException denied) {
            panels.clear();
        }
        result.put("panelList", panels);
        return result;
    }

    private Long readSchemeId(String userId) throws SQLException {
        String sql = "SELECT SCHEMEID FROM ("
                + "SELECT SCHEMEID FROM T_CORE_WKU_SCHEME WHERE USERID=? "
                + "ORDER BY CREATEDTIME DESC NULLS LAST, ID DESC) WHERE ROWNUM=1";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement query = connection.prepareStatement(sql)) {
            query.setString(1, userId);
            try (ResultSet result = query.executeQuery()) {
                return result.next() && result.getObject(1) != null
                        ? Long.valueOf(result.getLong(1)) : null;
            }
        }
    }

    private List<Map<String, Object>> readPanels(Long schemeId) throws SQLException {
        String sql = "SELECT C.PANELID, C.CONFIG, P.TITLE, P.URL, P.REFRESHFLAG, "
                + "P.TITLEDISPLAY, P.I18NCODE FROM T_CORE_WKSP_CONF C "
                + "LEFT JOIN T_CORE_WK_PANEL P ON P.ID=C.PANELID "
                + "WHERE C.SCHEMEID=? ORDER BY C.ID";
        List<Map<String, Object>> panels = new ArrayList<Map<String, Object>>();
        try (Connection connection = dataSource.getConnection();
                PreparedStatement query = connection.prepareStatement(sql)) {
            query.setLong(1, schemeId.longValue());
            try (ResultSet result = query.executeQuery()) {
                while (result.next()) {
                    Map<String, Object> panel = new LinkedHashMap<String, Object>();
                    panel.put("panelId", result.getString("PANELID"));
                    panel.put("config", readText(result, "CONFIG"));
                    panel.put("title", result.getString("TITLE"));
                    panel.put("url", readText(result, "URL"));
                    panel.put("refreshFlag", result.getString("REFRESHFLAG"));
                    panel.put("titleDisplay", result.getString("TITLEDISPLAY"));
                    panel.put("i18nCode", result.getString("I18NCODE"));
                    panels.add(panel);
                }
            }
        }
        return panels;
    }

    private static String readText(ResultSet result, String column) throws SQLException {
        Reader reader = result.getCharacterStream(column);
        if (reader == null) return null;
        StringBuilder value = new StringBuilder();
        char[] buffer = new char[1024];
        try {
            int read;
            while ((read = reader.read(buffer)) >= 0) value.append(buffer, 0, read);
        } catch (IOException failure) {
            throw new SQLException("workspace CLOB read failed: " + column, failure);
        } finally {
            try { reader.close(); } catch (IOException ignored) { }
        }
        return value.toString();
    }

    @GetMapping(path = "/secure/core/module/lcdp/global-configs/SYS_CLIENT_JS/content",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> globalScriptMetadata() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("configCode", "SYS_CLIENT_JS");
        result.put("effectFlag", "0");
        result.put("content", "");
        return result;
    }

    @GetMapping(path = "/secure/core/module/lcdp/global-comp-configs",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> globalComponentConfig() {
        return new LinkedHashMap<String, Object>();
    }

    @PostMapping(path = "/core/module/sys/todos", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> todos() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("columnTypeMap", new LinkedHashMap<String, Object>());
        result.put("first", Boolean.TRUE);
        result.put("last", Boolean.TRUE);
        result.put("numberOfElements", 0);
        result.put("pageNumber", 1);
        result.put("pageSize", 0);
        result.put("rows", Collections.emptyList());
        result.put("total", 0);
        result.put("totalMap", new LinkedHashMap<String, Object>());
        result.put("totalPages", 0);
        return result;
    }

    @PostMapping(path = "/secure/core/module/sys/shortcut-menus/queries/accessable",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Object shortcutMenus() {
        return Collections.emptyList();
    }

    @PostMapping(path = "/secure/core/module/item/notification/notification-instances/messages/queries",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> notificationMessages() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("notificationMessageList", Collections.emptyList());
        result.put("total", 0);
        return result;
    }

    /** Empty notice index is the supplier response when the clone has no notices. */
    @PostMapping(path = "/secure/core/module/item/notices/queries/index",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Object noticeIndex() {
        return Collections.emptyList();
    }

    @GetMapping(path = "/core/module/sys/statuses/speed", produces = MediaType.TEXT_PLAIN_VALUE)
    public String statusSpeed() {
        return "1";
    }
}
