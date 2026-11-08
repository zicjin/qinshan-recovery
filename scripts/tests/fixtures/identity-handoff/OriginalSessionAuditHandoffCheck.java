package identityhandoff;

import com.sunwayworld.emc.framework.security.OriginalAuditTrailReadScope;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.OwnedLoginRequest;
import com.sunwayworld.owned.internal.web.OwnedQueryAccessFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Actual production filter/boundary/audit scope; no HTTP listener or database. */
public final class OriginalSessionAuditHandoffCheck {
    private static int checks;
    private static final String DELETE = "/secure/emc/module/mdm/equipt/equipts/per-check-plan";

    public static void main(String[] args) throws Exception {
        Provider provider = new Provider();
        OwnedIdentityHttpBoundary boundary = new OwnedIdentityHttpBoundary(provider, event -> { });
        OwnedQueryAccessFilter filter = new OwnedQueryAccessFilter(boundary);
        Session original = new Session();
        SecurityContextHolder.clearContext();
        check(dispatch(filter, original, DELETE, false, true).delegated,
                "original session reaches original security before audit");

        Request auditRequest = new Request(original, "/secure/core/module/sys/at-records/queries/atrecord/T_DEMO/1", "POST");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(auditRequest.value));
        authenticateProject();
        try {
            check("org-a".equals(new OriginalAuditTrailReadScope(boundary).requireOrganization()),
                    "actual audit scope returns provider-validated organization");
        } finally {
            RequestContextHolder.resetRequestAttributes();
            SecurityContextHolder.clearContext();
        }
        Result afterAudit = dispatch(filter, original, DELETE, true, true);
        System.out.println("same-session DELETE after actual audit scope: status=" + afterAudit.status
                + " delegated=" + afterAudit.delegated);
        check(afterAudit.delegated && afterAudit.status == 200,
                "same original session DELETE still reaches original security and permission dispatch after audit");
        check(provider.adoptions == 2 && provider.auditGrants == 1,
                "audit grant and result both use fresh original identity snapshots");
        check(original.values.isEmpty(),
                "original authentication adaptation must not create an explicit owned-login session");
        Result deniedOriginal = dispatch(filter, original, DELETE, true, false);
        check(deniedOriginal.delegated && deniedOriginal.status == 403,
                "delegation does not bypass original downstream permission denial");
        try {
            boundary.currentPrincipal(original.value);
            throw new AssertionError("cleared original security context reused an adapted identity");
        } catch (AccessDeniedException expected) {
            check(original.values.isEmpty(), "adapted identity cannot outlive original security context");
        }
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(auditRequest.value));
        authenticateProject();
        provider.denyAudit = true;
        try {
            new OriginalAuditTrailReadScope(boundary).requireOrganization();
            throw new AssertionError("missing audit grant was accepted");
        } catch (AccessDeniedException expected) {
            check(original.values.isEmpty(), "audit grant rejection cannot create owned-login state");
        } finally {
            provider.denyAudit = false;
            RequestContextHolder.resetRequestAttributes();
            SecurityContextHolder.clearContext();
        }
        authenticateProject();
        provider.rejectOriginal = true;
        try {
            boundary.currentPrincipal(original.value);
            throw new AssertionError("disabled original identity was accepted");
        } catch (AccessDeniedException expected) {
            check(original.values.isEmpty(), "rejected original identity cannot create owned-login state");
        } finally {
            provider.rejectOriginal = false;
            SecurityContextHolder.clearContext();
        }

        Session owned = new Session();
        boundary.login(owned.value, new OwnedLoginRequest("project-user", "fixture".toCharArray()));
        check(owned.values.containsKey(OwnedIdentityHttpBoundary.SESSION_ATTRIBUTE),
                "explicit owned login retains its principal");
        Result unsupported = dispatch(filter, owned, DELETE, false, true);
        check(!unsupported.delegated && unsupported.status == 403,
                "explicit owned login default write guard remains closed");
        StandardEnvironment isolated = new StandardEnvironment();
        isolated.getPropertySources().addFirst(new org.springframework.core.env.MapPropertySource(
                "fixture", Collections.<String,Object>singletonMap("owned.equipment.write.mode", "isolated")));
        OwnedQueryAccessFilter isolatedFilter = new OwnedQueryAccessFilter(boundary, isolated);
        Result allowed = dispatch(isolatedFilter, owned, DELETE, false, true);
        check(allowed.delegated && allowed.status == 200 && provider.equipmentGrants == 1,
                "explicit isolated owned write still requires equipment management grant");
        provider.denyEquipment = true;
        Result missingGrant = dispatch(isolatedFilter, owned, DELETE, false, true);
        check(!missingGrant.delegated && missingGrant.status == 403,
                "explicit isolated owned write rejects missing equipment management grant");
        provider.denyEquipment = false;
        Result anonymous = dispatch(filter, null, DELETE, false, true);
        check(!anonymous.delegated && anonymous.status == 403, "anonymous write stays denied");
        Result malformed = dispatch(filter, original, "/secure/emc/module/mdm/equipt/equipts/%70er-check-plan", true, true);
        check(!malformed.delegated && malformed.status == 403, "noncanonical original-session path stays denied");
        SecurityContextHolder.clearContext();
        System.out.println("PASS actual audit scope -> fresh original snapshot -> same-session original security delegation (" + checks + " checks)");
    }

    private static void authenticateProject() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "project-user", null, Collections.emptyList()));
    }

    private static Result dispatch(OwnedQueryAccessFilter filter, Session session, String path,
                                   boolean restoreOriginal, boolean grantOriginal) throws Exception {
        SecurityContextHolder.clearContext();
        Result result = new Result();
        Request request = new Request(session, path, "DELETE");
        FilterChain downstream = (req, res) -> {
            result.delegated = true;
            // Models the later original Spring Security/Resource authorization
            // boundary, not the filter under test or the audit identity seam.
            if (restoreOriginal) authenticateProject();
            if (!grantOriginal) ((HttpServletResponse) res).setStatus(403);
        };
        try { filter.doFilter(request.value, result.response, downstream); }
        finally { SecurityContextHolder.clearContext(); }
        return result;
    }

    private static void check(boolean value, String label) {
        checks++;
        if (!value) throw new AssertionError(label);
    }

    private static final class Provider implements OwnedIdentityProvider {
        int adoptions, auditGrants, equipmentGrants;
        boolean denyEquipment, denyAudit, rejectOriginal;
        public OwnedPrincipal authenticate(String id, char[] password) { return new OwnedPrincipal(id, "org-a", "Fixture Org"); }
        public OwnedPrincipal issueAuthenticated(String id) {
            adoptions++;
            if (rejectOriginal) throw new AccessDeniedException("fixture identity disabled");
            return new OwnedPrincipal(id, "org-a", "Fixture Org");
        }
        public OwnedPrincipal refresh(OwnedPrincipal principal) { return principal; }
        public void authorize(OwnedPrincipal principal, String permission) {
            if (AUDIT_READ.equals(permission)) {
                auditGrants++;
                if (denyAudit) throw new AccessDeniedException("fixture audit denied");
            }
            if (EQUIPMENT_MANAGE.equals(permission)) {
                equipmentGrants++;
                if (denyEquipment) throw new AccessDeniedException("fixture denied");
            }
        }
        public Set<String> permittedOrganizationIds(OwnedPrincipal principal) { return Collections.singleton("org-a"); }
        public Map<String,Object> accountProfile(OwnedPrincipal principal) { return Collections.emptyMap(); }
    }

    private static final class Session implements InvocationHandler {
        final Map<String,Object> values = new HashMap<>();
        final HttpSession value = (HttpSession) Proxy.newProxyInstance(HttpSession.class.getClassLoader(), new Class<?>[]{HttpSession.class}, this);
        public Object invoke(Object proxy, Method method, Object[] args) {
            if ("getAttribute".equals(method.getName())) return values.get(args[0]);
            if ("setAttribute".equals(method.getName())) { values.put((String) args[0], args[1]); return null; }
            if ("removeAttribute".equals(method.getName())) { values.remove(args[0]); return null; }
            if ("getId".equals(method.getName())) return "fixture-session";
            return empty(method.getReturnType());
        }
    }

    private static final class Request implements InvocationHandler {
        final Session session;
        final String path, verb;
        final Map<String,Object> attributes = new HashMap<>();
        final HttpServletRequest value = (HttpServletRequest) Proxy.newProxyInstance(HttpServletRequest.class.getClassLoader(), new Class<?>[]{HttpServletRequest.class}, this);
        Request(Session session, String path, String verb) { this.session=session; this.path=path; this.verb=verb; }
        public Object invoke(Object proxy, Method method, Object[] args) {
            if ("getRequestURI".equals(method.getName())) return path;
            if ("getContextPath".equals(method.getName())) return "";
            if ("getRemoteAddr".equals(method.getName())) return "127.0.0.1";
            if ("getMethod".equals(method.getName())) return verb;
            if ("getSession".equals(method.getName())) return session == null ? null : session.value;
            if ("getAttribute".equals(method.getName())) return attributes.get(args[0]);
            return empty(method.getReturnType());
        }
    }

    private static final class Result implements InvocationHandler {
        int status = 200;
        boolean delegated;
        final StringWriter body = new StringWriter();
        final HttpServletResponse response = (HttpServletResponse) Proxy.newProxyInstance(HttpServletResponse.class.getClassLoader(), new Class<?>[]{HttpServletResponse.class}, this);
        public Object invoke(Object proxy, Method method, Object[] args) {
            if ("setStatus".equals(method.getName())) { status=(Integer) args[0]; return null; }
            if ("getWriter".equals(method.getName())) return new PrintWriter(body);
            return empty(method.getReturnType());
        }
    }

    private static Object empty(Class<?> type) {
        if (!type.isPrimitive() || type==void.class) return null;
        if (type==boolean.class) return false;
        if (type==char.class) return '\0';
        if (type==long.class) return 0L;
        if (type==double.class) return 0D;
        if (type==float.class) return 0F;
        return 0;
    }
}
