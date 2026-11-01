package com.sunwayworld.owned.internal.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;

/** Supplier-compatible negative/read seam for the CRM opportunity order-task resource. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedCrmOpportunityOrdtaskController {
    private final OwnedIdentityHttpBoundary boundary;
    public OwnedCrmOpportunityOrdtaskController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("CRM opportunity order-task dependencies are required");
        this.boundary = boundary;
    }

    @GetMapping(path = "/secure/emc/module/mdm/crm/ordtasks/info/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.PERSONNEL_READ); return ResponseEntity.ok("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (java.sql.SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = {"/secure/emc/module/mdm/crm/ordtasks/queries", "/secure/emc/module/mdm/crm/ordtasks/queries/raw", "/secure/emc/module/mdm/crm/ordtasks/queries/searchable"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String body) {
        try { boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.PERSONNEL_READ); return unexpected(response, request.getRequestURI()); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (java.sql.SQLException failure) { return unavailable(); }
    }

    private static ResponseEntity<String> unexpected(HttpServletResponse response, String path) {
        String safe = path == null ? "" : path.replace("\\", "\\\\").replace("\"", "\\\"");
        String body = "{\"error\":\"Internal Server Error\",\"message\":\"GIKAM.EXCEPTION.UNEXPECTED\",\"path\":\"" + safe + "\",\"status\":500,\"timestamp\":" + System.currentTimeMillis() + ",\"stacktrace\":\"\"}";
        try { response.resetBuffer(); response.setStatus(500); response.setHeader("Content-Type", ""); response.getWriter().write(body); response.flushBuffer(); return null; }
        catch (java.io.IOException error) { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body); }
    }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}"); }
}
