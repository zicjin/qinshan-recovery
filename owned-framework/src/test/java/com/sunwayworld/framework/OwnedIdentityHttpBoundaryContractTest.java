package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.AuditEvent;
import com.sunwayworld.owned.internal.web.InMemoryAuditEventSink;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpController;
import com.sunwayworld.owned.internal.web.OwnedLoginCredentialDecoder;
import com.sunwayworld.owned.internal.web.OwnedLoginRequest;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/** Public HTTP/session identity seam; no supplier classes, database, or production endpoint. */
public final class OwnedIdentityHttpBoundaryContractTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        FakeIdentityProvider provider = new FakeIdentityProvider();
        InMemoryAuditEventSink audit = new InMemoryAuditEventSink();
        OwnedIdentityHttpBoundary boundary = new OwnedIdentityHttpBoundary(provider, audit);
        SessionFixture session = new SessionFixture();

        fails(AccessDeniedException.class, new Attempt() {
            public void run() throws Exception { boundary.currentPrincipal(session.httpSession()); }
        });
        check(last(audit.events()).getOutcome() == AuditEvent.Outcome.DENIED, "anonymous access is audited");

        OwnedLoginRequest request = new OwnedLoginRequest("u-a", "secret".toCharArray());
        boundary.login(session.httpSession(), request);
        check(provider.seenPassword.equals("secret"), "provider receives the submitted credential");
        check(boundary.currentPrincipal(session.httpSession()).getId().equals("u-a"), "principal is stored in session");
        check(last(audit.events()).getAction().equals("login"), "successful login is audited");
        check("org-a".equals(last(audit.events()).getOrganizationId()), "successful login audit carries organization");
        check("owned-http".equals(last(audit.events()).getSource()), "successful login audit carries source");
        check(!request.hasPassword(), "login request clears its password after use");
        OwnedLoginRequest unsupported = new OwnedLoginRequest("u-a", "secret".toCharArray(), "org-a", null, null, "system");
        fails(IllegalStateException.class, new Attempt() {
            public void run() throws Exception { boundary.login(session.httpSession(), unsupported); }
        });
        check(!unsupported.hasPassword(), "unsupported login fields still clear the password");
        check(!session.contains(OwnedIdentityHttpBoundary.SESSION_ATTRIBUTE), "unsupported reauthentication clears the prior identity");
        boundary.login(session.httpSession(), new OwnedLoginRequest("u-a", "secret".toCharArray()));

        boundary.requirePermission(session.httpSession(), OwnedIdentityProvider.PERSONNEL_READ);
        check(last(audit.events()).getOutcome() == AuditEvent.Outcome.ALLOWED, "granted permission is audited");
        Set<String> scopes = boundary.permittedOrganizationIds(session.httpSession());
        check(scopes.equals(Collections.singleton("org-a")), "organization scope comes from provider");
        fails(UnsupportedOperationException.class, new Attempt() {
            public void run() throws Exception { scopes.add("org-b"); }
        });

        provider.deny = true;
        fails(AccessDeniedException.class, new Attempt() {
            public void run() throws Exception {
                boundary.requirePermission(session.httpSession(), OwnedIdentityProvider.PERSONNEL_READ);
            }
        });
        check(last(audit.events()).getOutcome() == AuditEvent.Outcome.DENIED, "denied permission is audited");
        check("org-a".equals(last(audit.events()).getOrganizationId()), "denied permission audit carries organization");

        boundary.logout(session.httpSession());
        check(last(audit.events()).getAction().equals("logout"), "logout is audited");
        fails(AccessDeniedException.class, new Attempt() {
            public void run() throws Exception { boundary.currentPrincipal(session.httpSession()); }
        });

        provider.failAuthentication = true;
        OwnedLoginRequest bad = new OwnedLoginRequest("u-a", "wrong".toCharArray());
        fails(BadCredentialsException.class, new Attempt() {
            public void run() throws Exception { boundary.login(session.httpSession(), bad); }
        });
        check(!session.contains(OwnedIdentityHttpBoundary.SESSION_ATTRIBUTE), "failed login does not create a session principal");
        check(last(audit.events()).getAction().equals("login")
                && last(audit.events()).getOutcome() == AuditEvent.Outcome.DENIED, "failed login is audited");
        check(audit.toString().indexOf("secret") < 0 && audit.toString().indexOf("wrong") < 0,
                "audit output contains no credentials");

        provider.failAuthentication = false;
        provider.deny = false;
        fails(AccessDeniedException.class, new Attempt() {
            public void run() throws Exception { boundary.currentPrincipal(session.httpSession()); }
        });
        check(provider.issueAuthenticatedCalls == 0, "anonymous context cannot adopt a project identity");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("project-user", null, Collections.emptyList()));
        check("project-user".equals(boundary.currentPrincipal(session.httpSession()).getId()),
                "authenticated project session is adapted to an owned principal");
        boundary.requirePermission(session.httpSession(), OwnedIdentityProvider.PERSONNEL_READ);
        check("project-user".equals(provider.seenAuthenticatedUser),
                "project identity adoption revalidates through the provider");
        SecurityContextHolder.clearContext();
        final RequestFixture http = new RequestFixture(session.httpSession(), "u-a", "ciphertext", "rsa-fixture");
        OwnedIdentityHttpController controller = new OwnedIdentityHttpController(boundary, new OwnedLoginCredentialDecoder() {
            public char[] decode(String userId, String encodedPassword, String transportKey) {
                check("u-a".equals(userId) && "ciphertext".equals(encodedPassword)
                        && "rsa-fixture".equals(transportKey), "supplier login wire fields reach decoder");
                return "secret".toCharArray();
            }
        });
        ResponseEntity<?> loginResponse = controller.login(http.request());
        check(loginResponse.getStatusCode() == HttpStatus.OK, "observed login route returns success");
        check(String.valueOf(loginResponse.getBody()).contains("\"id\":\"u-a\"")
                && String.valueOf(loginResponse.getBody()).contains("\"orgName\":\"Fixture Org\""),
                "login response exposes the authenticated organization context");
        com.alibaba.fastjson.JSONObject loginUser = com.alibaba.fastjson.JSON.parseObject(String.valueOf(loginResponse.getBody()));
        String firstToken = loginUser.getJSONObject("ext$").getString("logintoken");
        check(firstToken != null && firstToken.matches("[a-f0-9]{32}"), "owned session issues an opaque login token");
        com.alibaba.fastjson.JSONObject accountUser = com.alibaba.fastjson.JSON.parseObject(controller.account(http.request()).getBody());
        check(firstToken.equals(accountUser.getJSONObject("ext$").getString("logintoken")), "account reuses this session's token");
        check(!accountUser.containsKey("roles") && !accountUser.containsKey("isAdmin"), "account has no fabricated top-level role/admin fields");
        ResponseEntity<?> logoutResponse = controller.logout(http.request());
        check(logoutResponse.getStatusCode() == HttpStatus.OK
                && !session.contains(OwnedIdentityHttpBoundary.SESSION_ATTRIBUTE), "observed logout route clears session");
        check(controller.account(http.request()).getStatusCode() == HttpStatus.UNAUTHORIZED, "logout invalidates account projection");
        com.alibaba.fastjson.JSONObject secondLogin = com.alibaba.fastjson.JSON.parseObject(controller.login(http.request()).getBody());
        check(!firstToken.equals(secondLogin.getJSONObject("ext$").getString("logintoken")), "new authentication rotates token");
        provider.failAuthentication = true;
        check(controller.login(http.request()).getStatusCode() == HttpStatus.UNAUTHORIZED
                && controller.account(http.request()).getStatusCode() == HttpStatus.UNAUTHORIZED,
                "failed reauthentication cannot retain the prior session identity/token");
        SessionFixture expired = new SessionFixture();
        expired.invalidated = true;
        fails(AccessDeniedException.class, new Attempt() {
            public void run() throws Exception { boundary.currentPrincipal(expired.httpSession()); }
        });
        check(last(audit.events()).getOutcome() == AuditEvent.Outcome.DENIED,
                "request holding an invalidated session is denied and audited");
        System.out.println("owned identity HTTP boundary: PASS (" + checks + " checks; public seam only)");
    }

    private static AuditEvent last(List<AuditEvent> events) { return events.get(events.size() - 1); }
    private interface Attempt { void run() throws Exception; }
    private static void check(boolean value, String label) { checks++; if (!value) throw new AssertionError(label); }
    private static void fails(Class<? extends Throwable> type, Attempt attempt) throws Exception {
        try { attempt.run(); throw new AssertionError("expected rejection " + type.getSimpleName()); }
        catch (Throwable failure) {
            if (!type.isInstance(failure)) throw new AssertionError("wrong rejection class", failure);
            checks++;
        }
    }

    private static final class FakeIdentityProvider implements OwnedIdentityProvider {
        private final OwnedPrincipal principal = new OwnedPrincipal("u-a", "org-a", "Fixture Org");
        private String seenPassword;
        private String seenAuthenticatedUser;
        private int issueAuthenticatedCalls;
        private boolean deny;
        private boolean failAuthentication;

        public OwnedPrincipal authenticate(String userId, char[] password) {
            seenPassword = new String(password);
            if (failAuthentication) throw new BadCredentialsException("GIKAM.SECURITY.LOGIN.INCORRECT_USER_OR_PASSWORD");
            return principal;
        }
        public OwnedPrincipal issueAuthenticated(String userId) throws SQLException {
            issueAuthenticatedCalls++;
            seenAuthenticatedUser = userId;
            return new OwnedPrincipal(userId, "org-a", "Fixture Org");
        }
        public void authorize(OwnedPrincipal value, String permission) throws SQLException {
            if (deny || !PERSONNEL_READ.equals(permission)) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
        }
        public OwnedPrincipal refresh(OwnedPrincipal value) { return value; }
        public java.util.Map<String, Object> accountProfile(OwnedPrincipal value) {
            java.util.Map<String, Object> profile = new java.util.LinkedHashMap<String, Object>();
            profile.put("id", value.getId()); profile.put("orgId", value.getOrgId());
            profile.put("orgName", value.getOrgName());
            profile.put("ext$", new java.util.LinkedHashMap<String, Object>());
            return profile;
        }
        public Set<String> permittedOrganizationIds(OwnedPrincipal value) {
            return Collections.unmodifiableSet(new LinkedHashSet<String>(Arrays.asList("org-a")));
        }
    }

    private static final class SessionFixture implements InvocationHandler {
        private final java.util.Map<String, Object> values = new java.util.HashMap<String, Object>();
        private boolean invalidated;
        private final HttpSession session = (HttpSession) Proxy.newProxyInstance(
                HttpSession.class.getClassLoader(), new Class<?>[]{HttpSession.class}, this);
        HttpSession httpSession() { return session; }
        boolean contains(String key) { return values.containsKey(key); }
        public Object invoke(Object proxy, Method method, Object[] args) {
            String name = method.getName();
            if (invalidated && ("getAttribute".equals(name) || "setAttribute".equals(name) || "removeAttribute".equals(name)))
                throw new IllegalStateException("Session already invalidated");
            if ("getAttribute".equals(name)) return values.get(args[0]);
            if ("setAttribute".equals(name)) { values.put((String) args[0], args[1]); return null; }
            if ("removeAttribute".equals(name)) { values.remove(args[0]); return null; }
            if ("getId".equals(name)) return "fixture-session";
            if ("isNew".equals(name)) return false;
            if ("toString".equals(name)) return "fixture-session";
            if ("hashCode".equals(name)) return System.identityHashCode(proxy);
            if ("equals".equals(name)) return proxy == args[0];
            Class<?> type = method.getReturnType();
            if (!type.isPrimitive()) return null;
            if (type == boolean.class) return false;
            if (type == byte.class || type == short.class || type == int.class || type == long.class
                    || type == float.class || type == double.class) return 0;
            if (type == char.class) return '\0';
            return null;
        }
    }

    private static final class RequestFixture implements InvocationHandler {
        private final HttpSession session;
        private final String userId;
        private final String password;
        private final String key;
        private final HttpServletRequest request;
        RequestFixture(HttpSession session, String userId, String password, String key) {
            this.session = session; this.userId = userId; this.password = password; this.key = key;
            this.request = (HttpServletRequest) Proxy.newProxyInstance(
                    HttpServletRequest.class.getClassLoader(), new Class<?>[]{HttpServletRequest.class}, this);
        }
        HttpServletRequest request() { return request; }
        public Object invoke(Object proxy, Method method, Object[] args) {
            String name = method.getName();
            if ("getParameter".equals(name)) {
                if ("id".equals(args[0])) return userId;
                if ("password".equals(args[0])) return password;
                return null;
            }
            if ("getHeader".equals(name) && "SECURITY-RSA-KEY".equals(args[0])) return key;
            if ("getSession".equals(name)) return session;
            if ("toString".equals(name)) return "fixture-request";
            Class<?> type = method.getReturnType();
            if (!type.isPrimitive()) return null;
            if (type == boolean.class) return false;
            if (type == byte.class || type == short.class || type == int.class || type == long.class
                    || type == float.class || type == double.class) return 0;
            if (type == char.class) return '\0';
            return null;
        }
    }
}
