package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.AuditEvent;
import com.sunwayworld.owned.internal.web.AuditEventSink;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.OwnedQueryAccessFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;

/** Regression for project session requests reaching Spring Security first. */
public final class OwnedQueryAccessFilterContractTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        OwnedIdentityHttpBoundary boundary = new OwnedIdentityHttpBoundary(new Provider(), new AuditEventSink() {
            public void record(AuditEvent event) { }
        });
        OwnedQueryAccessFilter filter = new OwnedQueryAccessFilter(boundary);

        SessionFixture session = new SessionFixture();
        RequestFixture projectSessionRequest = new RequestFixture(session.session(), "/secure/emc/module/mdm/emp/emps/page/edit-list");
        ResponseFixture projectSessionResponse = new ResponseFixture();
        ChainFixture projectChain = new ChainFixture();
        SecurityContextHolder.clearContext();
        filter.doFilter(projectSessionRequest.request(), projectSessionResponse.response(), projectChain.chain());
        check(projectChain.called, "project session is deferred to Spring Security restoration");
        check(projectSessionResponse.status == 200, "deferred project session is not denied by the early guard");

        RequestFixture anonymousRequest = new RequestFixture(null, "/secure/emc/module/mdm/emp/emps/page/edit-list");
        ResponseFixture anonymousResponse = new ResponseFixture();
        ChainFixture anonymousChain = new ChainFixture();
        filter.doFilter(anonymousRequest.request(), anonymousResponse.response(), anonymousChain.chain());
        check(!anonymousChain.called && anonymousResponse.status == 403,
                "anonymous protected request remains fail-closed");
        System.out.println("owned query access filter contract: PASS (" + checks + " checks)");
    }

    private static void check(boolean value, String label) {
        checks++;
        if (!value) throw new AssertionError(label);
    }

    private static final class Provider implements OwnedIdentityProvider {
        public OwnedPrincipal authenticate(String id, char[] password) { return new OwnedPrincipal("u", "o", "org"); }
        public OwnedPrincipal refresh(OwnedPrincipal principal) { return principal; }
        public void authorize(OwnedPrincipal principal, String permission) throws SQLException { }
        public Set<String> permittedOrganizationIds(OwnedPrincipal principal) { return Collections.singleton("o"); }
        public Map<String, Object> accountProfile(OwnedPrincipal principal) { return Collections.emptyMap(); }
    }

    private static final class SessionFixture implements InvocationHandler {
        private final Map<String, Object> values = new HashMap<String, Object>();
        private final HttpSession session = (HttpSession) Proxy.newProxyInstance(
                HttpSession.class.getClassLoader(), new Class<?>[]{HttpSession.class}, this);
        HttpSession session() { return session; }
        public Object invoke(Object proxy, Method method, Object[] args) {
            if ("getAttribute".equals(method.getName())) return values.get(args[0]);
            if ("setAttribute".equals(method.getName())) { values.put((String) args[0], args[1]); return null; }
            if ("removeAttribute".equals(method.getName())) { values.remove(args[0]); return null; }
            if ("getId".equals(method.getName())) return "project-session";
            return defaultValue(method.getReturnType());
        }
    }

    private static final class RequestFixture implements InvocationHandler {
        private final HttpSession session;
        private final String uri;
        private final HttpServletRequest request = (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(), new Class<?>[]{HttpServletRequest.class}, this);
        RequestFixture(HttpSession session, String uri) { this.session = session; this.uri = uri; }
        HttpServletRequest request() { return request; }
        public Object invoke(Object proxy, Method method, Object[] args) {
            if ("getRequestURI".equals(method.getName())) return uri;
            if ("getContextPath".equals(method.getName())) return "";
            if ("getMethod".equals(method.getName())) return "GET";
            if ("getSession".equals(method.getName())) return session;
            return defaultValue(method.getReturnType());
        }
    }

    private static final class ResponseFixture implements InvocationHandler {
        private int status = 200;
        private final StringWriter body = new StringWriter();
        private final HttpServletResponse response = (HttpServletResponse) Proxy.newProxyInstance(
                HttpServletResponse.class.getClassLoader(), new Class<?>[]{HttpServletResponse.class}, this);
        HttpServletResponse response() { return response; }
        public Object invoke(Object proxy, Method method, Object[] args) throws IOException {
            if ("setStatus".equals(method.getName())) status = ((Integer) args[0]).intValue();
            if ("getWriter".equals(method.getName())) return new PrintWriter(body);
            return defaultValue(method.getReturnType());
        }
    }

    private static final class ChainFixture implements InvocationHandler {
        private boolean called;
        private final FilterChain chain = (FilterChain) Proxy.newProxyInstance(
                FilterChain.class.getClassLoader(), new Class<?>[]{FilterChain.class}, this);
        FilterChain chain() { return chain; }
        public Object invoke(Object proxy, Method method, Object[] args) { if ("doFilter".equals(method.getName())) called = true; return null; }
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
}
