package com.sunwayworld.owned.internal.web;

import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UriUtils;
import org.springframework.core.env.Environment;

/** Current-session guard for three bounded query domains and explicit loopback write seams. */
public final class OwnedQueryAccessFilter extends GenericFilterBean {
    private static final String[] PREFIXES = {
        "/secure/emc/module/mdm/emp/emps", "/secure/emc/module/mdm/equipt/equipts",
        "/secure/emc/module/item/consume/consumes"
    };
    private final OwnedIdentityHttpBoundary identity;
    private final boolean isolatedPersonnelWrites;
    private final boolean isolatedEquipmentWrites;
    private final boolean isolatedConsumableWrites;

    public OwnedQueryAccessFilter(OwnedIdentityHttpBoundary identity) {
        this(identity, null);
    }

    public OwnedQueryAccessFilter(OwnedIdentityHttpBoundary identity, Environment environment) {
        this.identity = identity;
        this.isolatedPersonnelWrites = environment != null
                && "isolated".equals(environment.getProperty("owned.personnel.write.mode"));
        this.isolatedEquipmentWrites = environment != null
                && "isolated".equals(environment.getProperty("owned.equipment.write.mode"));
        this.isolatedConsumableWrites = environment != null
                && "isolated".equals(environment.getProperty("owned.consumables.write.mode"));
    }

    @Override public void doFilter(ServletRequest input, ServletResponse output, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) input;
        HttpServletResponse response = (HttpServletResponse) output;
        String uri = (String) request.getAttribute(RequestDispatcher.INCLUDE_REQUEST_URI);
        String context = (String) request.getAttribute(RequestDispatcher.INCLUDE_CONTEXT_PATH);
        if (uri == null) { uri = request.getRequestURI(); context = request.getContextPath(); }
        String raw = context != null && uri.startsWith(context) ? uri.substring(context.length()) : uri;
        PathInspection path = inspect(raw);
        if (path.domain < 0 && !path.malformed) { chain.doFilter(input, output); return; }
        if (path.malformed || !raw.equals(path.canonical)) {
            deny(request, response, "non-canonical-path"); return;
        }
        if (identity == null) { deny(response); return; }
        String relative = path.canonical.substring(PREFIXES[path.domain].length());
        boolean personnelWrite = isolatedPersonnelWrites && path.domain == 0
                && personnelWrite(request, relative);
        boolean equipmentWrite = isolatedEquipmentWrites && path.domain == 1
                && equipmentWrite(request, relative);
        boolean consumableWrite = isolatedConsumableWrites && path.domain == 2
                && consumableWrite(request, relative);
        if (!safeMethod(request.getMethod()) && !("POST".equals(request.getMethod()) && query(path.domain, relative))
                && !personnelWrite && !equipmentWrite && !consumableWrite) {
            deny(request, response, "unsupported-write"); return;
        }
        boolean read = !personnelWrite && !equipmentWrite && !consumableWrite && readOnly(request, path.domain, relative);
        String permission = path.domain == 0 ? OwnedIdentityProvider.PERSONNEL_READ
                : path.domain == 1 ? (read ? OwnedIdentityProvider.EQUIPMENT_READ : OwnedIdentityProvider.EQUIPMENT_MANAGE)
                : (read ? OwnedIdentityProvider.CONSUMABLE_READ : OwnedIdentityProvider.CONSUMABLE_MANAGE);
        try {
            identity.requirePermission(request.getSession(false), permission);
        } catch (SQLException | RuntimeException failure) {
            // The boundary audits provider/session failures before they reach this guard.
            deny(response); return;
        }
        chain.doFilter(input, output);
    }

    private static boolean readOnly(HttpServletRequest request, int domain, String relative) {
        if ("POST".equals(request.getMethod())) return query(domain, relative);
        if ("/page/edit-list".equals(relative)) {
            String[] values = request.getParameterValues("readonly");
            return values != null && values.length == 1 && "1".equals(values[0]);
        }
        if ("GET".equals(request.getMethod()) && relative.matches("/[^/]+/(records|per-check-records)/maints/queries/selectable")) return true;
        return relative.length() > 1 && relative.indexOf('/', 1) < 0 && !"/page".equals(relative);
    }

    private static boolean personnelQualificationWrite(HttpServletRequest request, String relative) {
        if (!"POST".equals(request.getMethod()) && !"DELETE".equals(request.getMethod())) return false;
        if (!relative.matches("/[^/]+/(quals|tests)")) return false;
        return OwnedWriteSupport.isLoopback(request);
    }

    private static boolean personnelVisibleTestWrite(HttpServletRequest request, String relative) {
        if (!"POST".equals(request.getMethod()) && !"DELETE".equals(request.getMethod())) return false;
        if (!relative.matches("/[^/]+/tests")) return false;
        return OwnedWriteSupport.isLoopback(request);
    }

    private static boolean personnelTrainingWrite(HttpServletRequest request, String relative) {
        if (!"POST".equals(request.getMethod()) && !"DELETE".equals(request.getMethod())) return false;
        if (!relative.matches("/[^/]+/trains")) return false;
        return OwnedWriteSupport.isLoopback(request);
    }

    private static boolean personnelCertificateMethodWrite(HttpServletRequest request, String relative) {
        if ("PUT".equals(request.getMethod()) && relative.matches("/[^/]+/cert-test-methods")) return OwnedWriteSupport.isLoopback(request);
        if ("DELETE".equals(request.getMethod()) && "/cert-test-methods".equals(relative)) return OwnedWriteSupport.isLoopback(request);
        return false;
    }

    private static boolean personnelWrite(HttpServletRequest request, String relative) {
        if (personnelQualificationWrite(request, relative) || personnelVisibleTestWrite(request, relative)
                || personnelTrainingWrite(request, relative)
                || personnelCertificateMethodWrite(request, relative)) return true;
        if ("POST".equals(request.getMethod()) && relative.length() == 0) return OwnedWriteSupport.isLoopback(request);
        if ("PUT".equals(request.getMethod()) && relative.matches("/[^/]+")) return OwnedWriteSupport.isLoopback(request);
        if ("DELETE".equals(request.getMethod()) && relative.length() == 0) return OwnedWriteSupport.isLoopback(request);
        return false;
    }

    private static boolean equipmentWrite(HttpServletRequest request, String relative) {
        if ("PUT".equals(request.getMethod()) && relative.matches("/[0-9]+")) {
            return OwnedWriteSupport.isLoopback(request);
        }
        if ("DELETE".equals(request.getMethod()) && relative.length() == 0) {
            return OwnedWriteSupport.isLoopback(request);
        }
        if ("POST".equals(request.getMethod()) && "/test-methods/action/copyto".equals(relative)) {
            return OwnedWriteSupport.isLoopback(request);
        }
        if ("POST".equals(request.getMethod()) && (relative.length() == 0 || "/plans".equals(relative) || "/records".equals(relative))) {
            return OwnedWriteSupport.isLoopback(request);
        }
        if ("DELETE".equals(request.getMethod()) && "/plans".equals(relative)) {
            return OwnedWriteSupport.isLoopback(request);
        }
        if (("POST".equals(request.getMethod()) || "DELETE".equals(request.getMethod())) && "/equipt-maintain-repairs".equals(relative)) {
            return OwnedWriteSupport.isLoopback(request);
        }
        if ("PUT".equals(request.getMethod()) && "/records/action/complete".equals(relative)) {
            return OwnedWriteSupport.isLoopback(request);
        }
        if ("PUT".equals(request.getMethod()) && "/per-check-records/action/complete".equals(relative)) {
            return OwnedWriteSupport.isLoopback(request);
        }
        if ("DELETE".equals(request.getMethod()) && "/records".equals(relative)) {
            return OwnedWriteSupport.isLoopback(request);
        }
        if ("POST".equals(request.getMethod()) && ("/per-check-plan".equals(relative) || "/per-check-record".equals(relative))) {
            return OwnedWriteSupport.isLoopback(request);
        }
        if ("DELETE".equals(request.getMethod()) && ("/per-check-plan".equals(relative) || "/per-check-record".equals(relative))) {
            return OwnedWriteSupport.isLoopback(request);
        }
        if ("POST".equals(request.getMethod()) && "/use-records".equals(relative)) return loopback(request);
        if ("PUT".equals(request.getMethod()) && "/use-records".equals(relative)) return loopback(request);
        if ("DELETE".equals(request.getMethod()) && "/delete-records".equals(relative)) return loopback(request);
        if (!"POST".equals(request.getMethod()) && !"DELETE".equals(request.getMethod())) return false;
        if (!relative.matches("/[0-9]+/(test-methods|test-items)")) return false;
        return OwnedWriteSupport.isLoopback(request);
    }

    private static boolean loopback(HttpServletRequest request) {
        return OwnedWriteSupport.isLoopback(request);
    }

    private static boolean consumableWrite(HttpServletRequest request, String relative) {
        if ("POST".equals(request.getMethod()) && relative.length() == 0) {
            return OwnedWriteSupport.isLoopback(request);
        }
        if ("PUT".equals(request.getMethod()) && relative.matches("/[^/]+")) {
            return OwnedWriteSupport.isLoopback(request);
        }
        if ("DELETE".equals(request.getMethod()) && relative.length() == 0) {
            return OwnedWriteSupport.isLoopback(request);
        }
        if ("POST".equals(request.getMethod()) && relative.matches("/[^/]+/stocks")) {
            return OwnedWriteSupport.isLoopback(request);
        }
        if ("DELETE".equals(request.getMethod()) && relative.matches("/[^/]+/stocks")) {
            return OwnedWriteSupport.isLoopback(request);
        }
        if (!"PUT".equals(request.getMethod()) || !relative.matches("(/stocks/[0-9]+/action/(stockin|recieve)|/[^/]+/stocks/action/(inventory|abandon))")) return false;
        return OwnedWriteSupport.isLoopback(request);
    }

    private static boolean safeMethod(String method) {
        return "GET".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method);
    }

    /** Only observed read shapes qualify; a query-like substring never permits a write. */
    private static boolean query(int domain, String relative) {
        if ("/queries".equals(relative) || "/queries/raw".equals(relative) || "/queries/searchable".equals(relative)) return true;
        if (domain == 0) return relative.matches("/[^/]+/(quals|tests|credentials|methods|trains)/queries")
                || "/queries/choosable".equals(relative)
                || relative.matches("/(report-users|sample)/queries/choosable");
        if (domain == 1) return relative.matches("/[^/]+/(test-methods|test-items|equipcerts|plans|records|use-records|maint-warningdays|per-check-plan|per-check-record|maintain)/queries")
                || relative.matches("/[^/]+/maints/queries/selectable")
                || relative.matches("/[^/]+/(records|per-check-records)/maints/queries/selectable")
                || relative.matches("/[^/]+/DCU-confs/queries")
                || relative.matches("/(cali|verify|per-check)-queries/choosable")
                || "/queries/choosable".equals(relative)
                || relative.matches("/[^/]+/action/validate-dcu-config")
                || relative.matches("/action/select-equipt-(plan|per-check-search|maintain-repair-search|user)")
                || "/use-records/queries".equals(relative);
        return relative.matches("/[^/]+/stocks/queries") || relative.matches("/stocks/[^/]+/logs/queries");
    }

    private void deny(HttpServletRequest request, HttpServletResponse response, String reason) throws IOException {
        if (identity != null) {
            try { identity.recordRequestDenied(request.getSession(false), "bounded-query-path", reason); }
            catch (RuntimeException unavailableAudit) { /* A failed audit can never allow access. */ }
        }
        deny(response);
    }

    private static void deny(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");
    }

    private static PathInspection inspect(String raw) {
        String decoded = raw;
        int protectedDomain = -1;
        String canonical = raw;
        // Decoding is for classification only. Every changed protected path is rejected.
        for (int round = 0; round < 16; round++) {
            String slashes = decoded.replace('\\', '/');
            String normalized = normalize(slashes);
            int current = domain(slashes), normalizedDomain = domain(normalized);
            if (current >= 0) protectedDomain = current;
            if (normalizedDomain >= 0) protectedDomain = normalizedDomain;
            canonical = normalized;
            if (decoded.indexOf('%') < 0) return new PathInspection(protectedDomain, canonical, false);
            try {
                String next = UriUtils.decode(decoded, StandardCharsets.UTF_8);
                if (next.equals(decoded)) return new PathInspection(protectedDomain, canonical, true);
                decoded = next;
            } catch (IllegalArgumentException malformed) { return new PathInspection(protectedDomain, canonical, true); }
        }
        return new PathInspection(protectedDomain, canonical, true);
    }

    private static int domain(String path) {
        for (int index = 0; index < PREFIXES.length; index++) {
            String prefix = PREFIXES[index];
            if (path.equals(prefix) || path.startsWith(prefix + "/") || path.startsWith(prefix + ";")) return index;
        }
        return -1;
    }

    private static String normalize(String path) {
        List<String> components = new ArrayList<String>();
        for (String segment : path.split("/", -1)) {
            int matrix = segment.indexOf(';');
            if (matrix >= 0) segment = segment.substring(0, matrix);
            if (segment.length() == 0 || ".".equals(segment)) continue;
            if ("..".equals(segment)) { if (!components.isEmpty()) components.remove(components.size() - 1); }
            else components.add(segment);
        }
        StringBuilder result = new StringBuilder(path.length());
        for (String segment : components) result.append('/').append(segment);
        return result.length() == 0 ? "/" : result.toString();
    }

    private static final class PathInspection {
        final int domain;
        final String canonical;
        final boolean malformed;
        PathInspection(int domain, String canonical, boolean malformed) {
            this.domain = domain; this.canonical = canonical; this.malformed = malformed;
        }
    }
}
