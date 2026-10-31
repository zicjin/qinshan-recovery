package com.sunwayworld.owned.attachments;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Owned attachment management page and organization-scoped list seam. */
@RestController
@ConditionalOnProperty(name = "owned.attachments.enabled", havingValue = "true")
public final class OwnedAttachmentManagementController {
    private final DataSource dataSource;
    private final OwnedIdentityHttpBoundary identity;

    public OwnedAttachmentManagementController(DataSource dataSource, OwnedIdentityHttpBoundary identity) {
        this.dataSource = dataSource; this.identity = identity;
    }

    @GetMapping(value = "/owned/attachments/management-available", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> available(HttpServletRequest request) {
        try {
            identity.requirePermission(request.getSession(false), OwnedIdentityProvider.ATTACHMENT_MANAGE);
            return ResponseEntity.ok("{\"available\":true}");
        } catch (AccessDeniedException denied) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");
        } catch (SQLException failure) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.ATTACHMENT.UNAVAILABLE\"}");
        }
    }

    @GetMapping(value = "/owned/attachments/management", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> page(HttpServletRequest request) {
        try {
            identity.requirePermission(request.getSession(false), OwnedIdentityProvider.ATTACHMENT_MANAGE);
            return ResponseEntity.ok(readResource("templates/owned/attachments/management.html"));
        } catch (AccessDeniedException denied) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");
        } catch (SQLException failure) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.ATTACHMENT.UNAVAILABLE\"}");
        }
    }

    @PostMapping(value = "/owned/attachments/management/query", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String ignored) {
        try {
            OwnedPrincipal principal = authorized(request);
            List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
            String sql = "SELECT A.ID,A.ENDDATE,A.ACTIVATEDFLAG,A.ACTIVATEDBYID,A.ACTIVATEDBYNAME,A.ACTIVATEDTIME,B.NAME FILENAME,B.FILEEXT FROM T_EMC_HOME_ATTACHMENT A LEFT JOIN T_CORE_FILE B ON B.TARGETID=CONCAT('T_EMC_HOME_ATTACHMENT$',A.ID) WHERE A.CREATEDBYORGID=? ORDER BY A.ID DESC";
            try (Connection connection = dataSource.getConnection(); PreparedStatement select = connection.prepareStatement(sql)) {
                select.setString(1, principal.getOrgId());
                try (ResultSet result = select.executeQuery()) {
                    while (result.next()) rows.add(row(result));
                }
            }
            Map<String, Object> body = new LinkedHashMap<String, Object>(); body.put("rows", rows); body.put("total", Integer.valueOf(rows.size()));
            return ResponseEntity.ok(JSON.toJSONString(body));
        } catch (AccessDeniedException denied) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");
        } catch (SQLException failure) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.ATTACHMENT.QUERY_FAILED\"}");
        }
    }

    private OwnedPrincipal authorized(HttpServletRequest request) throws SQLException {
        identity.requirePermission(request.getSession(false), OwnedIdentityProvider.ATTACHMENT_MANAGE);
        return identity.currentPrincipal(request.getSession(false));
    }

    private static Map<String, Object> row(ResultSet result) throws SQLException {
        Map<String, Object> row = new LinkedHashMap<String, Object>();
        row.put("id", Long.valueOf(result.getLong("ID")));
        row.put("endDate", timestamp(result.getTimestamp("ENDDATE")));
        row.put("activatedFlag", result.getString("ACTIVATEDFLAG"));
        row.put("activatedByName", result.getString("ACTIVATEDBYNAME"));
        row.put("activatedTime", timestamp(result.getTimestamp("ACTIVATEDTIME")));
        Map<String, Object> ext = new LinkedHashMap<String, Object>(); ext.put("filename", result.getString("FILENAME")); ext.put("fileext", result.getString("FILEEXT")); row.put("ext$", ext);
        return row;
    }

    private static String timestamp(Timestamp value) { return value == null ? null : value.toLocalDateTime().toString(); }

    private static String readResource(String name) throws SQLException {
        try (InputStream input = new ClassPathResource(name).getInputStream(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096]; int count; while ((count = input.read(buffer)) != -1) output.write(buffer, 0, count);
            return new String(output.toByteArray(), java.nio.charset.StandardCharsets.UTF_8);
        } catch (IOException failure) { throw new SQLException("Attachment management page unavailable", failure); }
    }
}
