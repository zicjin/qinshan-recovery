package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.owned.internal.data.JdbcValue;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Read-only replacement for the core selectable-code endpoint. */
@RestController
public final class OwnedCodeQueryController {
    private final OwnedIdentityHttpBoundary identity;
    private final DataSource dataSource;

    public OwnedCodeQueryController(OwnedIdentityHttpBoundary identity, DataSource dataSource) {
        this.identity = identity;
        this.dataSource = dataSource;
    }

    @GetMapping(path = "/secure/core/module/sys/codes/queries/selectable",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> selectable(HttpServletRequest request,
            @RequestParam("codeCategoryId") String category) {
        try {
            OwnedPrincipal principal = identity.currentPrincipal(request.getSession(false));
            if (category == null || category.trim().isEmpty() || category.length() > 128)
                throw new IllegalArgumentException("code category is invalid");
            // Authentication is deliberately the only gate here.  The
            // original selectable endpoint exposes business code dictionaries
            // to every authenticated workspace role.
            List<Map<String, Object>> rows = read(category.trim(), principal);
            return ResponseEntity.ok(JSON.toJSONString(rows));
        } catch (AccessDeniedException denied) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");
        } catch (IllegalArgumentException invalid) {
            return ResponseEntity.badRequest()
                    .body("{\"message\":\"GIKAM.CODE.INVALID_REQUEST\"}");
        } catch (SQLException failure) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\":\"GIKAM.CODE.UNAVAILABLE\"}");
        }
    }

    private List<Map<String, Object>> read(String category, OwnedPrincipal principal) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        String sql = "SELECT * FROM T_CORE_CODE WHERE CODECATEGORYID=? ORDER BY ID";
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql)) {
            query.setString(1, category);
            try (ResultSet result = query.executeQuery()) {
                ResultSetMetaData metadata = result.getMetaData();
                while (result.next()) {
                    Map<String, Object> row = new LinkedHashMap<String, Object>();
                    for (int i = 1; i <= metadata.getColumnCount(); i++) {
                        String name = metadata.getColumnLabel(i);
                        row.put(camel(name), JdbcValue.normalize(result.getObject(i)));
                    }
                    rows.add(row);
                }
            }
        }
        return rows;
    }

    private static String camel(String value) {
        StringBuilder result = new StringBuilder();
        boolean upper = false;
        for (char character : value.toLowerCase(Locale.ROOT).toCharArray()) {
            if (character == '_') { upper = true; continue; }
            result.append(upper ? Character.toUpperCase(character) : character);
            upper = false;
        }
        return result.toString();
    }
}
