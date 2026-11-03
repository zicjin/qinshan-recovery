package com.sunwayworld.owned.internal.web;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Thin controller for the observed supplier route names.  Credential decoding
 * is injected because the archived page sends RSA transport material; this
 * class never accepts plaintext as a replacement or creates a fake key API.
 */
@RequestMapping
@Controller
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, OwnedLoginCredentialDecoder.class})
public final class OwnedIdentityHttpController {
    private final OwnedIdentityHttpBoundary boundary;
    private final OwnedLoginCredentialDecoder decoder;

    public OwnedIdentityHttpController(OwnedIdentityHttpBoundary boundary, OwnedLoginCredentialDecoder decoder) {
        this.boundary = Objects.requireNonNull(boundary, "boundary");
        this.decoder = Objects.requireNonNull(decoder, "decoder");
    }

    @PostMapping(path = "/core/module/sys/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> login(HttpServletRequest request) {
        String userId = request.getParameter("id");
        String encoded = request.getParameter("password");
        String transportKey = request.getHeader("SECURITY-RSA-KEY");
        String orgId = request.getParameter("orgId");
        String roleId = request.getParameter("roleId");
        String loginType = request.getParameter("loginType");
        String tenantId = request.getHeader("sunway.tenant");
        char[] decoded = null;
        try {
            decoded = decoder.decode(userId, encoded, transportKey);
            OwnedLoginRequest login = new OwnedLoginRequest(userId, decoded, orgId, roleId, tenantId, loginType);
            OwnedPrincipalSession.login(boundary, request, login);
            return json(HttpStatus.OK, com.alibaba.fastjson.JSON.toJSONString(
                    boundary.account(request.getSession(false))));
        } catch (IllegalArgumentException failure) {
            return response(HttpStatus.BAD_REQUEST, "GIKAM.SECURITY.LOGIN.INVALID_REQUEST");
        } catch (org.springframework.security.authentication.BadCredentialsException failure) {
            return response(HttpStatus.UNAUTHORIZED, failure.getMessage());
        } catch (org.springframework.security.core.AuthenticationException failure) {
            return response(HttpStatus.FORBIDDEN, failure.getMessage());
        } catch (SQLException failure) {
            return response(HttpStatus.INTERNAL_SERVER_ERROR, "GIKAM.SECURITY.LOGIN.UNAVAILABLE");
        } catch (IllegalStateException failure) {
            return response(HttpStatus.SERVICE_UNAVAILABLE, "GIKAM.SECURITY.LOGIN.UNSUPPORTED_CONFIGURATION");
        } finally {
            if (decoded != null) Arrays.fill(decoded, '\0');
        }
    }

    @PostMapping(path = "/core/module/sys/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) { boundary.logout(session); session.invalidate(); }
        return json(HttpStatus.OK, "{\"authenticated\":false}");
    }

    /**
     * Original workspace bootstrap route.  The supplier page requests this
     * projection after the menu tree; it is a read of the current HTTP session,
     * not a second authentication service or a native/license probe.
     */
    @GetMapping(path = "/core/module/sys/logins/account")
    public ResponseEntity<String> account(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        try {
            return json(HttpStatus.OK, com.alibaba.fastjson.JSON.toJSONString(boundary.account(session)));
        } catch (org.springframework.security.access.AccessDeniedException failure) {
            return response(HttpStatus.UNAUTHORIZED, failure.getMessage());
        } catch (SQLException failure) {
            return response(HttpStatus.INTERNAL_SERVER_ERROR, "GIKAM.SECURITY.SESSION.UNAVAILABLE");
        }
    }

    private static ResponseEntity<String> response(HttpStatus status, String message) {
        String value = message == null ? "GIKAM.PERMISSION.UNAUTHORIZED" : message;
        return json(status, "{\"message\":\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"}");
    }
    private static ResponseEntity<String> json(HttpStatus status, String body) {
        return ResponseEntity.status(status).header("Content-Type", MediaType.APPLICATION_JSON_VALUE).body(body);
    }

    private static final class OwnedPrincipalSession {
        private static com.sunwayworld.owned.internal.security.OwnedPrincipal login(
                OwnedIdentityHttpBoundary boundary, HttpServletRequest request, OwnedLoginRequest login)
                throws SQLException {
            com.sunwayworld.owned.internal.security.OwnedPrincipal principal =
                    boundary.login(request.getSession(true), login);
            request.changeSessionId();
            return principal;
        }
    }
}
