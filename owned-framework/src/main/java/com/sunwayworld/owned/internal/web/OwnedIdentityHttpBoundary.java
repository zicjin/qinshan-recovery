package com.sunwayworld.owned.internal.web;

import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.servlet.http.HttpSession;
import org.springframework.security.access.AccessDeniedException;

/**
 * Owned session boundary for the LIMS login, permission, and organization
 * scope contract.  Every protected operation refreshes the provider snapshot
 * and fails closed when the session or grant is absent.
 */
public final class OwnedIdentityHttpBoundary {
    public static final String SESSION_ATTRIBUTE = OwnedIdentityHttpBoundary.class.getName() + ".principal";
    private static final String LOGIN_TIME = OwnedIdentityHttpBoundary.class.getName() + ".login-time";
    private static final String LOGIN_TOKEN = OwnedIdentityHttpBoundary.class.getName() + ".login-token";

    private final OwnedIdentityProvider provider;
    private final AuditEventSink audit;

    public OwnedIdentityHttpBoundary(OwnedIdentityProvider provider, AuditEventSink audit) {
        if (provider == null) throw new IllegalArgumentException("provider is required");
        if (audit == null) throw new IllegalArgumentException("audit is required");
        this.provider = provider;
        this.audit = audit;
    }

    public OwnedPrincipal login(HttpSession session, OwnedLoginRequest request) throws SQLException {
        requireSession(session);
        if (request == null) throw new IllegalArgumentException("request is required");
        char[] password = request.copyPassword();
        try {
            rejectUnsupportedLoginFields(request);
            OwnedPrincipal principal = provider.authenticate(request.getUserId(), password);
            record("login", principal.getId(), principal.getOrgId(), null, AuditEvent.Outcome.ALLOWED, "authenticated");
            session.setAttribute(SESSION_ATTRIBUTE, principal);
            session.setAttribute(LOGIN_TIME, java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            session.setAttribute(LOGIN_TOKEN, java.util.UUID.randomUUID().toString().replace("-", ""));
            return principal;
        } catch (SQLException failure) {
            clearPrincipal(session);
            record("login", request.getUserId(), null, AuditEvent.Outcome.DENIED, "identity-provider-failure");
            throw failure;
        } catch (RuntimeException failure) {
            clearPrincipal(session);
            record("login", request.getUserId(), null, AuditEvent.Outcome.DENIED, reason(failure));
            throw failure;
        } finally {
            java.util.Arrays.fill(password, '\0');
            request.clear();
        }
    }

    public void logout(HttpSession session) {
        requireSession(session);
        OwnedPrincipal principal = principalAttribute(session);
        clearPrincipal(session);
        record("logout", principal == null ? null : principal.getId(), principal == null ? null : principal.getOrgId(), null,
                principal == null ? AuditEvent.Outcome.DENIED : AuditEvent.Outcome.ALLOWED,
                principal == null ? "anonymous" : "session-cleared");
    }

    /** Returns a fresh database/provider identity snapshot, replacing the session value. */
    public OwnedPrincipal currentPrincipal(HttpSession session) throws SQLException {
        OwnedPrincipal stored = session == null ? null : principalAttribute(session);
        if (stored == null) {
            clearPrincipal(session);
            record("session", null, null, null, AuditEvent.Outcome.DENIED, "unauthenticated");
            throw denied();
        }
        try {
            OwnedPrincipal refreshed = provider.refresh(stored);
            if (refreshed == null) throw denied();
            session.setAttribute(SESSION_ATTRIBUTE, refreshed);
            return refreshed;
        } catch (SQLException failure) {
            clearPrincipal(session);
            record("session", stored.getId(), stored.getOrgId(), null, AuditEvent.Outcome.DENIED, "identity-provider-failure");
            throw failure;
        } catch (RuntimeException failure) {
            clearPrincipal(session);
            record("session", stored.getId(), stored.getOrgId(), null, AuditEvent.Outcome.DENIED, reason(failure));
            throw failure;
        }
    }

    /** One projection serves login and account reads; role assignments are loaded afresh. */
    @SuppressWarnings("unchecked")
    public java.util.Map<String, Object> account(HttpSession session) throws SQLException {
        OwnedPrincipal principal = currentPrincipal(session);
        java.util.Map<String, Object> account = new java.util.LinkedHashMap<String, Object>(provider.accountProfile(principal));
        java.util.Map<String, Object> extension = new java.util.LinkedHashMap<String, Object>();
        Object values = account.get("ext$");
        if (values instanceof java.util.Map<?, ?>) extension.putAll((java.util.Map<String, Object>) values);
        extension.put("loginorgid", principal.getOrgId());
        extension.put("loginorgname", principal.getOrgName());
        Object loginTime = session.getAttribute(LOGIN_TIME);
        Object loginToken = session.getAttribute(LOGIN_TOKEN);
        if (loginTime == null || loginToken == null) {
            clearPrincipal(session);
            throw denied();
        }
        extension.put("logintime", loginTime);
        extension.put("logintoken", loginToken);
        account.put("ext$", extension);
        return account;
    }

    public void requirePermission(HttpSession session, String permission) throws SQLException {
        if (permission == null || permission.trim().length() == 0) throw new IllegalArgumentException("permission is required");
        OwnedPrincipal principal = currentPrincipal(session);
        try {
            provider.authorize(principal, permission);
            record("authorize", principal.getId(), principal.getOrgId(), permission, AuditEvent.Outcome.ALLOWED, "granted");
        } catch (SQLException failure) {
            record("authorize", principal.getId(), principal.getOrgId(), permission, AuditEvent.Outcome.DENIED, "identity-provider-failure");
            throw failure;
        } catch (RuntimeException failure) {
            record("authorize", principal.getId(), principal.getOrgId(), permission, AuditEvent.Outcome.DENIED, reason(failure));
            throw failure;
        }
    }

    public void requireResultRead(HttpSession session) throws SQLException {
        requirePermission(session, OwnedIdentityProvider.RESULT_READ);
    }

    public void requireResultWrite(HttpSession session) throws SQLException {
        requirePermission(session, OwnedIdentityProvider.RESULT_WRITE);
    }

    public void requireOrderRetainRead(HttpSession session) throws SQLException {
        requirePermission(session, OwnedIdentityProvider.ORDER_RETAIN_READ);
    }

    public void requireOrderRetainWrite(HttpSession session) throws SQLException {
        requirePermission(session, OwnedIdentityProvider.ORDER_RETAIN_WRITE);
    }

    public void requireAuditRead(HttpSession session) throws SQLException {
        requirePermission(session, OwnedIdentityProvider.AUDIT_READ);
    }

    /** Records a request rejected before permission dispatch, without retaining session state. */
    public void recordRequestDenied(HttpSession session, String resource, String reason) {
        OwnedPrincipal principal = session == null ? null : principalAttribute(session);
        record("authorize", principal == null ? null : principal.getId(), principal == null ? null : principal.getOrgId(), resource,
                AuditEvent.Outcome.DENIED, reason == null ? "request-rejected" : reason);
    }

    /** Records an already-authorized isolated write without exposing the sink. */
    public void recordAllowed(String action, OwnedPrincipal principal, String resource, String reason) {
        record(action, principal == null ? null : principal.getId(), principal == null ? null : principal.getOrgId(), resource,
                AuditEvent.Outcome.ALLOWED, reason == null ? "allowed" : reason);
    }

    /** Personnel list/detail callers use this gate before applying the returned scope to SQL. */
    public Set<String> permittedOrganizationIds(HttpSession session) throws SQLException {
        requirePermission(session, OwnedIdentityProvider.PERSONNEL_READ);
        OwnedPrincipal principal = currentPrincipal(session);
        try {
            Set<String> values = provider.permittedOrganizationIds(principal);
            Set<String> copy = values == null ? Collections.<String>emptySet()
                    : Collections.unmodifiableSet(new LinkedHashSet<String>(values));
            record("data-scope", principal.getId(), principal.getOrgId(), OwnedIdentityProvider.PERSONNEL_READ,
                    AuditEvent.Outcome.ALLOWED, "organization-scope-loaded");
            return copy;
        } catch (SQLException failure) {
            record("data-scope", principal.getId(), principal.getOrgId(), OwnedIdentityProvider.PERSONNEL_READ,
                    AuditEvent.Outcome.DENIED, "identity-provider-failure");
            throw failure;
        } catch (RuntimeException failure) {
            record("data-scope", principal.getId(), principal.getOrgId(), OwnedIdentityProvider.PERSONNEL_READ,
                    AuditEvent.Outcome.DENIED, reason(failure));
            throw failure;
        }
    }

    private OwnedPrincipal principalAttribute(HttpSession session) {
        try {
            Object value = session.getAttribute(SESSION_ATTRIBUTE);
            return value instanceof OwnedPrincipal ? (OwnedPrincipal) value : null;
        } catch (IllegalStateException invalidated) {
            return null;
        }
    }
    private static void clearPrincipal(HttpSession session) {
        if (session == null) return;
        try {
            session.removeAttribute(SESSION_ATTRIBUTE);
            session.removeAttribute(LOGIN_TIME);
            session.removeAttribute(LOGIN_TOKEN);
        }
        catch (IllegalStateException ignored) { /* session was already invalidated */ }
    }
    private static void requireSession(HttpSession session) {
        if (session == null) throw new IllegalArgumentException("session is required");
    }
    private static AccessDeniedException denied() { return new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED"); }
    private static void rejectUnsupportedLoginFields(OwnedLoginRequest request) {
        if (text(request.getOrgId()) || text(request.getRoleId()) || text(request.getTenantId())
                || (text(request.getLoginType()) && !"system".equals(request.getLoginType()))) {
            throw new IllegalStateException("Unsupported login request configuration");
        }
    }
    private static boolean text(String value) { return value != null && value.trim().length() != 0; }
    private static String reason(RuntimeException failure) {
        return failure instanceof AccessDeniedException ? "permission-denied" : "identity-rejected";
    }
    private void record(String action, String actor, String resource, AuditEvent.Outcome outcome, String reason) {
        record(action, actor, null, resource, outcome, reason);
    }

    private void record(String action, String actor, String organization, String resource,
            AuditEvent.Outcome outcome, String reason) {
        audit.record(new AuditEvent(Instant.now(), action, actor, organization, resource,
                outcome, reason, "owned-http", null));
    }
}
