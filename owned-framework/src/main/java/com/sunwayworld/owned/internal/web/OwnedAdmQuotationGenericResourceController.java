package com.sunwayworld.owned.internal.web;

import com.sunwayworld.owned.internal.data.AdmQuotationQueryDao;
import com.sunwayworld.owned.internal.data.AdmQuotationQueryService;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import java.sql.SQLException;
import java.util.Set;
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

/** Administrative/quotation HTTP aliases share one owned service/DAO in this application. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedAdmQuotationGenericResourceController {
    private final OwnedIdentityHttpBoundary boundary;
    private final AdmQuotationQueryService service;

    public OwnedAdmQuotationGenericResourceController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("administrative quotation dependencies are required");
        this.boundary = boundary;
        this.service = new AdmQuotationQueryService(new AdmQuotationQueryDao(dataSource));
    }

    @PostMapping(path = {
            "/secure/emc/module/item/adm-areas/queries", "/secure/emc/module/item/adm-areas/queries/raw", "/secure/emc/module/item/adm-areas/queries/searchable",
            "/secure/emc/module/item/quotation/addprices/queries", "/secure/emc/module/item/quotation/addprices/queries/raw", "/secure/emc/module/item/quotation/addprices/queries/searchable",
            "/secure/emc/module/item/quotation/detectionfactors/queries", "/secure/emc/module/item/quotation/detectionfactors/queries/raw", "/secure/emc/module/item/quotation/detectionfactors/queries/searchable",
            "/secure/emc/module/item/quotation/discounts/queries", "/secure/emc/module/item/quotation/discounts/queries/raw", "/secure/emc/module/item/quotation/discounts/queries/searchable",
            "/secure/emc/module/item/quotation/quotationinformations/queries", "/secure/emc/module/item/quotation/quotationinformations/queries/raw", "/secure/emc/module/item/quotation/quotationinformations/queries/searchable"
            }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, HttpServletResponse response,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            Set<String> organizations = boundary.permittedOrganizationIds(request.getSession(false));
            return ok(service.query(request.getRequestURI(), principal, organizations,
                    QueryRequest.parse(body, null)));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return invalidQuery(request, response); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {
            "/secure/emc/module/item/adm-areas/info/status",
            "/secure/emc/module/item/quotation/addprices/info/status",
            "/secure/emc/module/item/quotation/detectionfactors/info/status",
            "/secure/emc/module/item/quotation/discounts/info/status",
            "/secure/emc/module/item/quotation/quotationinformations/info/status"
            }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { require(request); return ok("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.PERSONNEL_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}"); }
    private static ResponseEntity<String> invalidQuery(HttpServletRequest request, HttpServletResponse response) {
        // The inherited resource returns this error contract for malformed query/page
        // input. Do not copy supplier stack traces or disclose owned SQL internals.
        com.alibaba.fastjson.JSONObject error = new com.alibaba.fastjson.JSONObject(true);
        error.put("error", "Internal Server Error"); error.put("message", "GIKAM.EXCEPTION.UNEXPECTED");
        error.put("path", request.getRequestURI()); error.put("stacktrace", "");
        error.put("status", 500); error.put("timestamp", System.currentTimeMillis());
        String body = error.toJSONString();
        try {
            response.resetBuffer(); response.setStatus(500);
            response.setHeader("Content-Type", "");
            response.getWriter().write(body); response.flushBuffer(); return null;
        } catch (java.io.IOException failure) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}"); }
}
