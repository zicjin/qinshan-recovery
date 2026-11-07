package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.AuditEvent;
import com.sunwayworld.owned.internal.web.AuditEventSink;
import com.sunwayworld.owned.internal.web.OwnedAuditTrailController;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.OwnedLoginRequest;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Contract guard for the source-owned audit-trail read aliases and permission split. */
public final class OwnedAuditTrailControllerContractTest {
    public static void main(String[] args) throws Exception {
        int mappings = 0; boolean records = false; boolean lines = false;
        boolean status = false; boolean statusById = false;
        for (Method method : OwnedAuditTrailController.class.getDeclaredMethods()) {
            PostMapping mapping = method.getAnnotation(PostMapping.class);
            if (mapping != null) {
                mappings++;
                for (String path : mapping.path()) {
                    if (path.endsWith("/at-records/queries")) records = true;
                    if (path.endsWith("/at-records/record-lines/queries")) lines = true;
                }
            }
            GetMapping statusMapping = method.getAnnotation(GetMapping.class);
            if (statusMapping != null) {
                for (String path : statusMapping.path()) {
                    if (path.endsWith("/audit-records/info/status")) status = true;
                    if (path.endsWith("/audit-records/{id}/info/status")) statusById = true;
                }
            }
        }
        if (mappings != 2 || !records || !lines || !status || !statusById) {
            throw new AssertionError("audit trail route aliases are incomplete: "
                    + mappings + "/" + records + "/" + lines + "/" + status + "/" + statusById);
        }

        FixtureIdentityProvider provider = new FixtureIdentityProvider();
        final OwnedIdentityHttpBoundary boundary = new OwnedIdentityHttpBoundary(provider, new AuditEventSink() {
            public void record(AuditEvent event) { }
        });
        SessionFixture session = new SessionFixture();
        boundary.login(session.httpSession(), new OwnedLoginRequest("u-a", "secret".toCharArray()));
        provider.deny = true;

        OwnedAuditTrailController controller = new OwnedAuditTrailController(boundary, dummyDataSource());
        ResponseEntity<String> statusResponse = controller.status(request(session.httpSession()));
        if (statusResponse.getStatusCodeValue() != 200
                || !"{\"ext$\":{}}".equals(statusResponse.getBody())) {
            throw new AssertionError("GenericResource status projection must not require audit.read");
        }
        if (provider.authorizations != 0) {
            throw new AssertionError("status projection invoked an entity permission validator");
        }

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("global-user", null, Collections.emptyList()));
        ResponseEntity<String> globalStatus = controller.status(request(null));
        SecurityContextHolder.clearContext();
        if (globalStatus.getStatusCodeValue() != 200
                || !"{\"ext$\":{}}".equals(globalStatus.getBody())) {
            throw new AssertionError("status projection must accept the original project session");
        }

        ResponseEntity<?> queryResponse = controller.records(request(session.httpSession()),
                new com.sunwayworld.framework.restful.data.RestJsonWrapperBean());
        if (queryResponse.getStatusCodeValue() != 403 || provider.authorizations != 1) {
            throw new AssertionError("audit record queries must continue to require audit.read");
        }
        provider.deny = false;
        ResponseEntity<?> missingAdapter = controller.records(request(session.httpSession()),
                new com.sunwayworld.framework.restful.data.RestJsonWrapperBean());
        if (missingAdapter.getStatusCodeValue() != 500) {
            throw new AssertionError("missing original audit adapter must fail closed without SQL fallback");
        }
        System.out.println("owned audit trail controller contract checks passed (record, line and status permission split)");
    }

    private static HttpServletRequest request(final HttpSession session) {
        return (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class<?>[]{HttpServletRequest.class},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        if ("getSession".equals(method.getName())) return session;
                        return defaultValue(method.getReturnType());
                    }
                });
    }

    private static DataSource dummyDataSource() {
        return (DataSource) Proxy.newProxyInstance(
                DataSource.class.getClassLoader(),
                new Class<?>[]{DataSource.class},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        return defaultValue(method.getReturnType());
                    }
                });
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return Boolean.FALSE;
        if (type == char.class) return Character.valueOf('\0');
        if (type == byte.class) return Byte.valueOf((byte) 0);
        if (type == short.class) return Short.valueOf((short) 0);
        if (type == int.class) return Integer.valueOf(0);
        if (type == long.class) return Long.valueOf(0L);
        if (type == float.class) return Float.valueOf(0F);
        return Double.valueOf(0D);
    }

    private static final class FixtureIdentityProvider implements OwnedIdentityProvider {
        private final OwnedPrincipal principal = new OwnedPrincipal("u-a", "org-a", "Fixture Org");
        private boolean deny;
        private int authorizations;

        public OwnedPrincipal authenticate(String userId, char[] password) { return principal; }
        public OwnedPrincipal refresh(OwnedPrincipal value) { return value; }
        public void authorize(OwnedPrincipal value, String permission) {
            authorizations++;
            if (deny || !AUDIT_READ.equals(permission)) {
                throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
            }
        }
        public Set<String> permittedOrganizationIds(OwnedPrincipal value) { return Collections.emptySet(); }
        public Map<String, Object> accountProfile(OwnedPrincipal value) { return new HashMap<String, Object>(); }
    }

    private static final class SessionFixture implements InvocationHandler {
        private final Map<String, Object> values = new HashMap<String, Object>();
        private final HttpSession session = (HttpSession) Proxy.newProxyInstance(
                HttpSession.class.getClassLoader(), new Class<?>[]{HttpSession.class}, this);

        HttpSession httpSession() { return session; }
        public Object invoke(Object proxy, Method method, Object[] args) {
            String name = method.getName();
            if ("getAttribute".equals(name)) return values.get(args[0]);
            if ("setAttribute".equals(name)) { values.put((String) args[0], args[1]); return null; }
            if ("removeAttribute".equals(name)) { values.remove(args[0]); return null; }
            return defaultValue(method.getReturnType());
        }
    }
}
