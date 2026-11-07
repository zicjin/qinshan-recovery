package com.sunwayworld.owned.internal.web;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Source-owned read seam for the two audit-trail endpoints used by recovered
 * project pages.  Visibility is deliberately opt-in through the observed
 * audit-log menu grant (9404 by default) and is restricted to the current
 * principal organization until the production audit visibility policy is
 * explicitly reconstructed.
 */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedAuditTrailController {
    private final OwnedIdentityHttpBoundary boundary;
    private AuditTrailReadOperations readOperations;

    public OwnedAuditTrailController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("audit dependencies are required");
        this.boundary = boundary;
    }

    @Autowired(required = false)
    public void setReadOperations(AuditTrailReadOperations readOperations) {
        this.readOperations = readOperations;
    }

    @PostMapping(path = {"/secure/core/module/sys/at-records/queries", "/secure/emc/module/sys/at-records/queries"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> records(HttpServletRequest request, RestJsonWrapperBean wrapper) {
        return query(request, wrapper, false);
    }

    @PostMapping(path = {"/secure/core/module/sys/at-records/record-lines/queries", "/secure/emc/module/sys/at-records/record-lines/queries"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> recordLines(HttpServletRequest request, RestJsonWrapperBean wrapper) {
        return query(request, wrapper, true);
    }

    /**
     * The recovered GenericResource exposes status routes for every resource,
     * including entities that do not implement Activatable, Suspendable or
     * Auditable.  The supplier serializes the resulting empty status DTO as
     * {"ext$":{}}. GenericResource does not apply an entity permission
     * annotation to this projection, so the owned seam refreshes the session
     * identity but does not require the separate audit-log read grant.
     */
    @GetMapping(path = {"/secure/emc/module/bp/audit-records/info/status",
            "/secure/emc/module/bp/audit-records/{id}/info/status"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            Object ownedPrincipal = session == null ? null
                    : session.getAttribute(OwnedIdentityHttpBoundary.SESSION_ATTRIBUTE);
            if (ownedPrincipal instanceof OwnedPrincipal) {
                boundary.currentPrincipal(session);
            } else {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication == null || !authentication.isAuthenticated()
                        || "anonymousUser".equals(authentication.getName())) {
                    return forbidden();
                }
            }
            return ok("{\"ext$\":{}}");
        } catch (AccessDeniedException denied) {
            return forbidden();
        } catch (SQLException failure) {
            return unavailable();
        }
    }

    private ResponseEntity<?> query(HttpServletRequest request, RestJsonWrapperBean wrapper, boolean lines) {
        try {
            OwnedPrincipal principal = require(request);
            if (readOperations == null) return unavailable();
            Object page = lines ? readOperations.lines(wrapper, principal.getOrgId())
                    : readOperations.records(wrapper, principal.getOrgId());
            return ResponseEntity.ok().header("Cache-Control", "no-store").body(page);
        } catch (AccessDeniedException denied) {
            return forbidden();
        } catch (IllegalArgumentException invalid) {
            return badRequest();
        } catch (SQLException failure) {
            return unavailable();
        }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requireAuditRead(request.getSession(false));
        return boundary.currentPrincipal(request.getSession(false));
    }

    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().header("Cache-Control", "no-store").body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.AUDIT.INVALID_QUERY\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.AUDIT.UNAVAILABLE\"}"); }

}
