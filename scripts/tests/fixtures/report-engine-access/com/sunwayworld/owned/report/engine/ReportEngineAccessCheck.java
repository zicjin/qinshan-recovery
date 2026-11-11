package com.sunwayworld.owned.report.engine;

import com.fr.stable.web.SessionProvider;
import com.fr.web.core.SessionPoolManager;
import com.fr.web.core.TemplateSessionIDInfo;
import com.sunwayworld.framework.context.LocalContextHelper;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Locale;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/** Executes the current filter implementation; no HTTP server or engine is started. */
public final class ReportEngineAccessCheck {
    private static final String TEMPLATE = ReportEngineAccessFilter.TEMPLATE;
    private static final String CONTRACT_TEMPLATE = ReportEngineAccessFilter.CONTRACT_TEMPLATE;
    private static final String SID = "session-fixture-00000001";
    private static final String OTHER_SID = "session-fixture-00000002";
    private static final String NONCE = "__owned_portal_report_nonce";
    private static int assertions;

    private static void require(boolean condition, String message) {
        assertions++;
        if (!condition) throw new AssertionError(message);
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return false;
        if (type == char.class) return '\0';
        if (type == long.class) return 0L;
        if (type == float.class) return 0F;
        if (type == double.class) return 0D;
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        return 0;
    }

    private static final class Request implements InvocationHandler {
        final String method, path;
        final Map<String, String[]> parameters = new LinkedHashMap<>();
        final Map<String, String[]> headers = new LinkedHashMap<>();
        Request(String method, String path) { this.method = method; this.path = path; }
        Request parameter(String key, String... values) { parameters.put(key, values); return this; }
        Request header(String key, String... values) { headers.put(key.toLowerCase(Locale.ROOT), values); return this; }
        HttpServletRequest servlet() {
            return (HttpServletRequest) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[] {HttpServletRequest.class}, this);
        }
        @Override public Object invoke(Object proxy, Method method, Object[] args) {
            switch (method.getName()) {
            case "getMethod": return this.method;
            case "getContextPath": return "/report-engine";
            case "getRequestURI": return "/report-engine" + path;
            case "getParameterValues": return parameters.get(args[0]);
            case "getParameter":
                String[] values = parameters.get(args[0]);
                return values == null || values.length == 0 ? null : values[0];
            case "getParameterMap": return parameters;
            case "getHeader":
                String[] headerValues = headers.get(((String) args[0]).toLowerCase(Locale.ROOT));
                return headerValues == null || headerValues.length == 0 ? null : headerValues[0];
            case "getHeaders":
                String[] allHeaders = headers.get(((String) args[0]).toLowerCase(Locale.ROOT));
                return Collections.enumeration(allHeaders == null ? Collections.emptyList() : Arrays.asList(allHeaders));
            case "toString": return "RequestFixture";
            case "hashCode": return System.identityHashCode(proxy);
            case "equals": return proxy == args[0];
            default: return defaultValue(method.getReturnType());
            }
        }
    }

    private static final class Response implements InvocationHandler {
        int status = 200;
        boolean committed;
        HttpServletResponse servlet() {
            return (HttpServletResponse) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[] {HttpServletResponse.class}, this);
        }
        @Override public Object invoke(Object proxy, Method method, Object[] args) {
            switch (method.getName()) {
            case "sendError": status = (Integer) args[0]; committed = true; return null;
            case "setStatus": status = (Integer) args[0]; return null;
            case "getStatus": return status;
            case "isCommitted": return committed;
            case "toString": return "ResponseFixture";
            case "hashCode": return System.identityHashCode(proxy);
            case "equals": return proxy == args[0];
            default: return defaultValue(method.getReturnType());
            }
        }
    }

    public static final class ReportSession extends TemplateSessionIDInfo {
        final String path;
        final Map<String, Object> attributes = new java.util.concurrent.ConcurrentHashMap<>();
        final Map<String, Object> originalParameters = new HashMap<>();
        ReportSession(String path) { this.path = path; }
        @Override public Object getAttribute(String name) { return attributes.get(name); }
        @Override public void setAttribute(String name, Object value) { attributes.put(name, value); }
        @Override public String getRelativePath() { return path; }
        @Override public Map<String, Object> getOriginalParameterMap() { return originalParameters; }
    }

    private static final class PortalIdentity implements ReportEngineAccessFilter.IdentitySource {
        ReportEngineAccessFilter.Identity identity = new ReportEngineAccessFilter.Identity("fixture-user", "fixture-token-role-org-A");
        RuntimeException failure;
        int calls;
        @Override public ReportEngineAccessFilter.Identity authenticate(HttpServletRequest request, HttpServletResponse response) {
            calls++;
            require(Thread.currentThread().getContextClassLoader() == ReportEngineAccessFilter.class.getClassLoader(),
                "portal authentication must run with the application loader");
            if (failure != null) throw failure;
            return identity;
        }
    }

    private static final class Result {
        final Response response = new Response();
        int entered;
    }

    private static Result invoke(ReportEngineAccessFilter filter, Request request, FilterChain business)
            throws IOException, ServletException {
        Result result = new Result();
        ClassLoader before = Thread.currentThread().getContextClassLoader();
        try {
            filter.doFilter(request.servlet(), result.response.servlet(), (raw, output) -> {
                result.entered++;
                require(Thread.currentThread().getContextClassLoader() == before,
                    "engine chain must retain the engine thread loader");
                if (business != null) business.doFilter(raw, output);
            });
        } finally {
            require(Thread.currentThread().getContextClassLoader() == before,
                "filter must restore the incoming thread loader");
        }
        return result;
    }

    private static Request initial() {
        return new Request("GET", "/decision/view/report").parameter("viewlet", TEMPLATE);
    }

    private static Request continuation(String sid) {
        return new Request("POST", "/decision/view/report").parameter("op", "page_content").parameter("sessionID", sid);
    }

    private static String copyServerNonce(ServletRequest raw, ReportSession session) {
        String nonce = raw.getParameter(NONCE);
        require(nonce != null && nonce.matches("[A-Za-z0-9_-]{43}"), "initial request must carry a 32-byte server nonce");
        require(nonce.equals(raw.getParameterValues(NONCE)[0]), "nonce values must match scalar accessor");
        require(nonce.equals(raw.getParameterMap().get(NONCE)[0]), "nonce must appear in engine parameter map");
        require(Collections.list(raw.getParameterNames()).contains(NONCE), "nonce must appear in engine parameter enumeration");
        session.rawRequestParameters.put(NONCE, nonce);
        // The real engine's merged execution map omits undeclared request keys.
        require(!session.getOriginalParameterMap().containsKey(NONCE), "fixture must distinguish merged execution map from raw request map");
        return nonce;
    }

    private static void asyncCreate(ReportEngineAccessFilter filter, String sid) throws ServletException {
        java.util.concurrent.atomic.AtomicReference<Throwable> failure = new java.util.concurrent.atomic.AtomicReference<>();
        Thread worker = new Thread(() -> {
            try { filter.sessionCreated().accept(sid); } catch (Throwable error) { failure.set(error); }
        }, "fixture-fine-async-create");
        worker.start();
        try { worker.join(); } catch (InterruptedException interrupted) { throw new ServletException(interrupted); }
        if (failure.get() != null) throw new ServletException(failure.get());
    }

    private static void createFromWrappedRequest(ReportEngineAccessFilter filter, ServletRequest raw, String sid)
            throws ServletException {
        ReportSession session = SessionPoolManager.getSessionIDInfor(sid, ReportSession.class);
        copyServerNonce(raw, session);
        asyncCreate(filter, sid);
    }

    private static void denied(ReportEngineAccessFilter filter, Request request, int status)
            throws IOException, ServletException {
        Result result = invoke(filter, request, null);
        require(result.response.status == status, "expected denial " + status + " for " + request.path + ", got " + result.response.status);
        require(result.entered == 0, "denied request must not execute report business handling");
    }

    private static void authenticationAndRouting(PortalIdentity portal, ReportEngineAccessFilter filter)
            throws IOException, ServletException {
        ReportEngineAccessFilter unready = new ReportEngineAccessFilter(portal);
        int oldCalls = portal.calls;
        denied(unready, initial(), 503);
        require(oldCalls == portal.calls, "unready engine must not evaluate identity");
        ReportEngineAccessFilter.Identity saved = portal.identity;
        portal.identity = null;
        denied(filter, initial(), 401);
        portal.identity = saved;
        portal.failure = new org.springframework.security.authentication.BadCredentialsException("fixture-invalid-session");
        denied(filter, initial(), 401);
        portal.failure = new SecurityException("menu-denied");
        denied(filter, initial(), 403);
        portal.failure = new IllegalStateException("identity-not-ready");
        denied(filter, initial(), 503);
        portal.failure = null;

        Result allowed = invoke(filter, initial(), (raw, output) -> {
            HttpServletRequest request = (HttpServletRequest) raw;
            require("fixture-user".equals(request.getRemoteUser()), "engine must receive authenticated remote user");
            require("fixture-user".equals(request.getUserPrincipal().getName()), "engine principal name must match portal identity");
            require(!(request.getUserPrincipal() instanceof org.springframework.security.core.Authentication),
                "engine must not receive the Spring authentication object");
        });
        require(allowed.entered == 1 && allowed.response.status == 200, "exact target template must reach report handling");

        denied(filter, new Request("GET", "/decision/view/report").parameter("viewlet", "other/secret.cpt"), 403);
        denied(filter, new Request("GET", "/decision/view/report").parameter("viewlet", TEMPLATE, TEMPLATE), 403);
        denied(filter, new Request("GET", "/decision/view/report").parameter("viewlet", "reportlets/../" + TEMPLATE), 403);
        denied(filter, new Request("GET", "/decision/view/report").parameter("viewlet", TEMPLATE + "\0"), 403);
        denied(filter, new Request("POST", "/decision/view/report").parameter("viewlet", TEMPLATE), 403);
        denied(filter, initial().parameter("loginType", "reportsso"), 403);
        denied(filter, initial().parameter("sessionid", SID), 403);
        denied(filter, initial().parameter(NONCE, "forged"), 403);
        denied(filter, initial().parameter(NONCE.toUpperCase(Locale.ROOT), "forged"), 403);
        denied(filter, initial().parameter("__parameters__", "{\"" + NONCE + "\":\"forged\"}"), 403);
        denied(filter, initial().parameter("__PARAMETERS__", "forged"), 403);
        for (String path : new String[] {"/decision", "/decision/login", "/decision/config", "/WEB-INF/reportlets/" + TEMPLATE}) {
            denied(filter, new Request("GET", path), 403);
        }
    }

    private static void ownership(PortalIdentity portal, ReportEngineAccessFilter filter)
            throws IOException, ServletException {
        ReportSession target = new ReportSession(TEMPLATE);
        SessionPoolManager.put(SID, target);
        Result first = invoke(filter, initial(), (raw, output) -> createFromWrappedRequest(filter, raw, SID));
        require(first.entered == 1, "authenticated initial rendering must reach engine");
        require(!target.attributes.isEmpty(), "new async report session must receive ownership binding");
        require(invoke(filter, continuation(SID), null).entered == 1, "same owner must be able to query/page its report session");
        Request chart = new Request("GET", "/decision/view/report").parameter("op", "chart").parameter("cmd", "writer_out_html").parameter("sessionID", SID).parameter("chartID", "Cells__A2__chart");
        require(invoke(filter, chart, null).entered == 1, "owned chart rendering must reach engine");
        denied(filter, new Request("GET", "/decision/view/report").parameter("op", "chart").parameter("cmd", "delete").parameter("sessionID", SID), 403);
        denied(filter, new Request("GET", "/decision/view/report").parameter("op", "chart").parameter("cmd", "writer_out_html"), 403);
        Request headerOnly = new Request("POST", "/decision/view/report").parameter("op", "widget").header("sessionID", SID);
        require(invoke(filter, headerOnly, null).entered == 1, "Fine widget request must accept actual sessionID header transport");
        require(invoke(filter, new Request("POST", "/decision/view/report").parameter("op", "fr_paramstpl").header("SESSIONID", SID), null).entered == 1, "Fine parameter request must accept case-insensitive header names");
        require(invoke(filter, continuation(SID).header("sessionID", SID), null).entered == 1, "matching parameter and header must preserve owned session");
        ReportEngineAccessFilter.Identity owner = portal.identity;
        for (String changed : new String[] {"different-token", "different-role", "different-organization", "different-user"}) {
            portal.identity = new ReportEngineAccessFilter.Identity("fixture-user", changed);
            denied(filter, continuation(SID), 403);
            denied(filter, headerOnly, 403);
        }
        portal.identity = owner;
        denied(filter, continuation(SID).header("sessionID", OTHER_SID), 403);
        denied(filter, headerOnly.header("sessionID", SID, SID), 403);
        denied(filter, new Request("POST", "/decision/view/report").parameter("op", "widget").header("sessionID", ""), 403);
        denied(filter, new Request("POST", "/decision/view/report").parameter("op", "widget").header("sessionID", "invalid"), 403);
        denied(filter, continuation(SID).parameter("sessionID", SID, SID), 403);
        denied(filter, continuation("invalid"), 403);
        denied(filter, continuation("session-fixture-not-found"), 403);
        ReportSession unknown = new ReportSession(TEMPLATE);
        SessionPoolManager.put(OTHER_SID, unknown);
        asyncCreate(filter, OTHER_SID);
        denied(filter, continuation(OTHER_SID), 403);
        require(unknown.attributes.isEmpty(), "continuation cannot claim pre-existing unowned session");
        ReportSession wrongTemplate = new ReportSession("other/secret.cpt");
        wrongTemplate.attributes.put("owned.portal.report.owner.v1", owner.owner);
        SessionPoolManager.put(OTHER_SID, wrongTemplate);
        denied(filter, continuation(OTHER_SID), 403);
        invoke(filter, initial(), (raw, output) -> createFromWrappedRequest(filter, raw, OTHER_SID));
        denied(filter, continuation(OTHER_SID), 403);
        ReportSession prefixed = new ReportSession("reportlets/" + TEMPLATE);
        SessionPoolManager.put(OTHER_SID, prefixed);
        invoke(filter, initial(), (raw, output) -> createFromWrappedRequest(filter, raw, OTHER_SID));
        require(invoke(filter, continuation(OTHER_SID), null).entered == 1, "canonical reportlets prefix must retain exact-template ownership");
    }

    private static void resourcesAndCleanup(PortalIdentity portal, ReportEngineAccessFilter filter)
            throws IOException, ServletException {
        Request staticResource = new Request("GET", "/decision/view/report").parameter("op", "emb").parameter("resource", "finereport.main.js");
        require(invoke(filter, staticResource, null).entered == 1, "required embedded bundle must load for authenticated user");
        Request font = new Request("GET", "/decision/resources").parameter("path", "/com/fr/web/ui/font/iconfont.woff");
        require(invoke(filter, font, null).entered == 1, "required report font must load");
        denied(filter, new Request("GET", "/decision/resources").parameter("path", "/com/fr/web/../../config/secret.xml"), 403);
        denied(filter, new Request("GET", "/decision/resources").parameter("path", "/com/fr/web/%2e%2e/secret.xml"), 403);
        denied(filter, new Request("GET", "/decision/view/report").parameter("op", "emb").parameter("resource", "secret.xml"), 403);
        denied(filter, new Request("POST", "/decision/view/report").parameter("op", "emb").parameter("resource", "finereport.main.js"), 403);
        denied(filter, new Request("GET", "/decision/view/report").parameter("op", "export").parameter("sessionID", SID), 403);

        IOException expected = new IOException("fixture-business-failure");
        try {
            invoke(filter, initial(), (raw, output) -> { throw expected; });
            throw new AssertionError("business exception must not be swallowed");
        } catch (IOException actual) {
            require(actual == expected, "business exception must propagate without fake success");
        }
        ReportSession afterFailure = new ReportSession(TEMPLATE);
        SessionPoolManager.put(OTHER_SID, afterFailure);
        denied(filter, continuation(OTHER_SID), 403);
        require(afterFailure.attributes.isEmpty(), "failed request must not leave identity/request state on its thread");

    }

    private static Request dialog(String id, String parameters) {
        return new Request("POST", "/decision/view/report").parameter("op", "fr_dialog")
            .parameter("cmd", "parameters_d").header("sessionID", id).parameter("__parameters__", parameters);
    }

    private static void queryParameterContainer(PortalIdentity portal, ReportEngineAccessFilter filter)
            throws IOException, ServletException {
        ReportSession report = new ReportSession(TEMPLATE);
        SessionPoolManager.put(SID, report);
        invoke(filter, initial(), (raw, output) -> createFromWrappedRequest(filter, raw, SID));
        String values = "{\"contractno\":\"QA-NOMATCH\",\"TESTORGNAME\":\"\",\"begindate\":\"2026-11-01\",\"enddate\":\"2026-11-11\",\"kpzt\":\"\"}";
        Result accepted = invoke(filter, dialog(SID, values), (raw, output) ->
            require(values.equals(raw.getParameter("__parameters__")), "declared report filter JSON must reach the real engine unchanged"));
        require(accepted.entered == 1, "owned Fine fr_dialog query container must be accepted");
        require(invoke(filter, dialog(SID, "{}"), null).entered == 1, "empty query object must preserve empty filter behavior");
        require(invoke(filter, dialog(SID, "{\"编制人\":\"\",\"接收节点\":\"\"}"), null).entered == 1, "original Chinese parameter names must reach engine");
        require(invoke(filter, dialog(SID, "{\"kpzt\":[\"已开票\",\"未开票\"]}"), null).entered == 1,
            "declared checkbox array must retain normal Fine filter behavior");
        ReportEngineAccessFilter.Identity owner = portal.identity;
        try {
            portal.identity = null;
            denied(filter, dialog(SID, values), 401);
            portal.identity = new ReportEngineAccessFilter.Identity("fixture-other", "other-token-owner");
            denied(filter, dialog(SID, values), 403);
        } finally { portal.identity = owner; }
        denied(filter, new Request("POST", "/decision/view/report").parameter("op", "fr_dialog").parameter("__parameters__", values), 403);
        denied(filter, dialog("session-fixture-unknown", values), 403);
        denied(filter, dialog(SID, values).parameter("__parameters__", values, values), 403);
        denied(filter, dialog(SID, "{\"userid\":\"another-user\"}"), 403);
        for (String key : new String[] {"sessionID", "SESSIONID", "viewlet", "ViewLet", "reportlet", "formlet", "loginType", "op", NONCE, NONCE.toUpperCase(Locale.ROOT), "__parameters__", "@type"}) {
            denied(filter, dialog(SID, "{\"" + key + "\":\"forged\"}"), 403);
        }
        for (String invalid : new String[] {"not-json", "null", "[]", "[{}]", "{\"contractno\":{\"sessionID\":\"forged\"}}", "{\"kpzt\":[{\"sessionID\":\"forged\"}]}", "{\"kpzt\":[[\"forged\"]]}", "{\"contractno\":\"ok\",\"sessionID\":\"forged\"}"}) {
            denied(filter, dialog(SID, invalid), 403);
        }
        Request collect = new Request("POST", "/decision/preview/info/collect").header("sessionID", SID);
        require(invoke(filter, collect, null).entered == 1, "observed preview telemetry must accept its verified owned session");
        denied(filter, new Request("POST", "/decision/preview/info/collect"), 403);
        denied(filter, new Request("POST", "/decision/preview/info/collect").header("sessionID", "session-fixture-unknown"), 403);
        try {
            portal.identity = null;
            denied(filter, collect, 401);
            portal.identity = new ReportEngineAccessFilter.Identity("fixture-other", "other-token-owner");
            denied(filter, collect, 403);
        } finally { portal.identity = owner; }
    }

    private static void nestedDispatch(PortalIdentity portal, ReportEngineAccessFilter filter)
            throws IOException, ServletException {
        ReportSession outer = new ReportSession(TEMPLATE);
        ReportSession inner = new ReportSession(TEMPLATE);
        SessionPoolManager.put(SID, outer);
        SessionPoolManager.put(OTHER_SID, inner);
        final String[] nonces = new String[2];
        invoke(filter, initial(), (raw, output) -> {
            nonces[0] = copyServerNonce(raw, outer);
            asyncCreate(filter, SID);
            Result forwarded = invoke(filter, initial(), (nested, nestedOutput) -> {
                nonces[1] = copyServerNonce(nested, inner);
                asyncCreate(filter, OTHER_SID);
            });
            require(forwarded.entered == 1, "nested allowed dispatch must execute");
            denied(filter, new Request("GET", "/decision/config"), 403);
            Request resource = new Request("GET", "/decision/view/report").parameter("op", "emb").parameter("resource", "finereport.main.js");
            require(invoke(filter, resource, null).entered == 1, "nested resource dispatch must execute");
            require(nonces[0].equals(raw.getParameter(NONCE)), "nested dispatch must not change outer nonce");
        });
        require(!nonces[0].equals(nonces[1]), "nested initial requests must have distinct server nonces");
        require(invoke(filter, continuation(SID), null).entered == 1, "outer session must remain owned after nested dispatch");
        require(invoke(filter, continuation(OTHER_SID), null).entered == 1, "inner session must remain owned after nested dispatch");
    }

    private static void nonceReplayAndRace(ReportEngineAccessFilter filter)
            throws IOException, ServletException {
        ReportSession original = new ReportSession(TEMPLATE);
        SessionPoolManager.put(SID, original);
        invoke(filter, initial(), (raw, output) -> createFromWrappedRequest(filter, raw, SID));
        String consumed = (String) original.rawRequestParameters.get(NONCE);
        ReportSession replay = new ReportSession(TEMPLATE);
        replay.rawRequestParameters.put(NONCE, consumed);
        SessionPoolManager.put(OTHER_SID, replay);
        asyncCreate(filter, OTHER_SID);
        denied(filter, continuation(OTHER_SID), 403);
        require(replay.attributes.isEmpty(), "consumed nonce cannot bind a second session");
        ReportSession forged = new ReportSession(TEMPLATE);
        forged.rawRequestParameters.put(NONCE, "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        SessionPoolManager.put(OTHER_SID, forged);
        asyncCreate(filter, OTHER_SID);
        denied(filter, continuation(OTHER_SID), 403);
        require(forged.attributes.isEmpty(), "never-issued nonce cannot claim session");
        ReportSession mergedOnly = new ReportSession(TEMPLATE);
        SessionPoolManager.put(OTHER_SID, mergedOnly);
        invoke(filter, initial(), (raw, output) -> mergedOnly.originalParameters.put(NONCE, raw.getParameter(NONCE)));
        asyncCreate(filter, OTHER_SID);
        denied(filter, continuation(OTHER_SID), 403);
        require(mergedOnly.attributes.isEmpty(), "merged execution map must not substitute for raw request provenance");
        ReportSession delayed = new ReportSession(TEMPLATE);
        SessionPoolManager.put(OTHER_SID, delayed);
        invoke(filter, initial(), (raw, output) -> copyServerNonce(raw, delayed));
        denied(filter, continuation(OTHER_SID), 403);
        asyncCreate(filter, OTHER_SID);
        require(invoke(filter, continuation(OTHER_SID), null).entered == 1, "CREATE after original request returns must bind raw request nonce");
        ReportSession first = new ReportSession(TEMPLATE);
        ReportSession second = new ReportSession(TEMPLATE);
        SessionPoolManager.put(SID, first);
        SessionPoolManager.put(OTHER_SID, second);
        invoke(filter, initial(), (raw, output) -> second.rawRequestParameters.put(NONCE, copyServerNonce(raw, first)));
        java.util.concurrent.CountDownLatch ready = new java.util.concurrent.CountDownLatch(2);
        java.util.concurrent.CountDownLatch start = new java.util.concurrent.CountDownLatch(1);
        java.util.concurrent.atomic.AtomicReference<Throwable> failure = new java.util.concurrent.atomic.AtomicReference<>();
        Thread[] workers = new Thread[2];
        String[] ids = {SID, OTHER_SID};
        for (int i = 0; i < workers.length; i++) {
            final String id = ids[i];
            workers[i] = new Thread(() -> {
                ready.countDown();
                try { start.await(); filter.sessionCreated().accept(id); }
                catch (Throwable error) { failure.compareAndSet(null, error); }
            });
            workers[i].start();
        }
        try { ready.await(); start.countDown(); for (Thread worker : workers) worker.join(); }
        catch (InterruptedException interrupted) { throw new ServletException(interrupted); }
        require(failure.get() == null, "competing async CREATE callbacks must not throw");
        require(first.attributes.isEmpty() != second.attributes.isEmpty(), "one nonce binds exactly one session during a race");
        String winner = first.attributes.isEmpty() ? OTHER_SID : SID;
        String loser = first.attributes.isEmpty() ? SID : OTHER_SID;
        require(invoke(filter, continuation(winner), null).entered == 1, "winning async session remains usable");
        denied(filter, continuation(loser), 403);
        ReportSession afterDestroy = new ReportSession(TEMPLATE);
        SessionPoolManager.put(OTHER_SID, afterDestroy);
        invoke(filter, initial(), (raw, output) -> copyServerNonce(raw, afterDestroy));
        filter.destroy();
        asyncCreate(filter, OTHER_SID);
        denied(filter, continuation(OTHER_SID), 403);
        require(afterDestroy.attributes.isEmpty(), "destroy must discard pending nonces");
    }

    private static void templateGrants(PortalIdentity portal, ReportEngineAccessFilter filter)
            throws IOException, ServletException {
        ReportEngineAccessFilter.Identity original = portal.identity;
        try {
            portal.identity = new ReportEngineAccessFilter.Identity(original.name, original.owner, Collections.singleton(TEMPLATE));
            denied(filter, new Request("GET", "/decision/view/report").parameter("viewlet", CONTRACT_TEMPLATE), 403);
            ReportSession commission = new ReportSession(TEMPLATE);
            ReportSession contract = new ReportSession(CONTRACT_TEMPLATE);
            SessionPoolManager.put(SID, commission);
            SessionPoolManager.put(OTHER_SID, contract);
            invoke(filter, initial(), (raw, output) -> {
                String nonce = copyServerNonce(raw, commission);
                contract.rawRequestParameters.put(NONCE, nonce);
            });
            asyncCreate(filter, OTHER_SID);
            require(contract.attributes.isEmpty(), "commission nonce must never bind contract report session");
            denied(filter, continuation(OTHER_SID), 403);
            asyncCreate(filter, SID);
            require(invoke(filter, continuation(SID), null).entered == 1, "matching commission nonce remains valid after wrong-template callback");

            portal.identity = new ReportEngineAccessFilter.Identity(original.name, original.owner, Collections.singleton(CONTRACT_TEMPLATE));
            denied(filter, initial(), 403);
            denied(filter, continuation(SID), 403);
            invoke(filter, new Request("GET", "/decision/view/report").parameter("viewlet", CONTRACT_TEMPLATE),
                (raw, output) -> createFromWrappedRequest(filter, raw, OTHER_SID));
            require(invoke(filter, continuation(OTHER_SID), null).entered == 1, "contract grant permits its own report session");
            portal.identity = new ReportEngineAccessFilter.Identity(original.name, original.owner, Collections.singleton(TEMPLATE));
            denied(filter, continuation(OTHER_SID), 403);
            portal.identity = new ReportEngineAccessFilter.Identity(original.name, original.owner, Collections.emptySet());
            denied(filter, initial(), 403);
            denied(filter, new Request("GET", "/decision/view/report").parameter("viewlet", CONTRACT_TEMPLATE), 403);
            denied(filter, continuation(SID), 403);
            denied(filter, continuation(OTHER_SID), 403);
        } finally { portal.identity = original; }
    }

    private static void originalAnonymousContext() throws IOException, ServletException, ReflectiveOperationException {
        HttpServletRequest previousRequest = new Request("GET", "/previous").servlet();
        HttpServletResponse previousResponse = new Response().servlet();
        SecurityContext previousContext = SecurityContextHolder.createEmptyContext();
        LocalContextHelper.setServletRequest(previousRequest);
        LocalContextHelper.setServletResponse(previousResponse);
        SecurityContextHolder.setContext(previousContext);
        try {
            ReportEngineAccessFilter originalIdentity = new ReportEngineAccessFilter();
            originalIdentity.configureEngine(Thread.currentThread().getContextClassLoader());
            denied(originalIdentity, initial(), 401);
            require(LocalContextHelper.getHttpServletRequest() == previousRequest, "actual anonymous identity path must restore previous request");
            require(LocalContextHelper.getHttpServletResponse() == previousResponse, "actual anonymous identity path must restore previous response");
            require(SecurityContextHolder.getContext() == previousContext, "actual anonymous identity path must restore previous security context");
        } finally {
            LocalContextHelper.removeServletRequest();
            LocalContextHelper.removeServletResponse();
            SecurityContextHolder.clearContext();
        }
    }

    public static void main(String[] args) throws Exception {
        ClassLoader before = Thread.currentThread().getContextClassLoader();
        ClassLoader engineLoader = new ClassLoader(before) { };
        Thread.currentThread().setContextClassLoader(engineLoader);
        SessionPoolManager.clear();
        try {
            PortalIdentity portal = new PortalIdentity();
            ReportEngineAccessFilter filter = new ReportEngineAccessFilter(portal);
            filter.configureEngine(engineLoader);
            require(ReportEngineAccessFilter.installedTemplates().size() == 27, "all home templates must be registered");
            for (String template : ReportEngineAccessFilter.installedTemplates()) {
                Result restored = invoke(filter, new Request("GET", "/decision/view/report").parameter("viewlet", template), null);
                require(restored.entered == 1 && restored.response.status == 200, "restored template must reach engine: " + template);
                portal.identity = new ReportEngineAccessFilter.Identity("fixture-user", "fixture-token-role-org-A", Collections.emptySet());
                denied(filter, new Request("GET", "/decision/view/report").parameter("viewlet", template), 403);
                portal.identity = new ReportEngineAccessFilter.Identity("fixture-user", "fixture-token-role-org-A");
            }
            denied(filter, new Request("GET", "/decision/view/report").parameter("viewlet", "fxryjxtj/undefined"), 403);
            denied(filter, initial().parameter("userid", "another-user"), 403);

            authenticationAndRouting(portal, filter);
            ownership(portal, filter);
            resourcesAndCleanup(portal, filter);
            queryParameterContainer(portal, filter);
            nestedDispatch(portal, filter);
            nonceReplayAndRace(filter);
            templateGrants(portal, filter);
            originalAnonymousContext();
            System.out.println("Report engine access regression passed: " + assertions + " assertions; no HTTP, database or report engine started.");
        } finally {
            Thread.currentThread().setContextClassLoader(before);
            SessionPoolManager.clear();
        }
    }
}
