package com.sunwayworld.owned.report;

import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.AuditEvent;
import com.sunwayworld.owned.internal.web.AuditEventSink;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.security.access.AccessDeniedException;

/** Authenticated report-line permission, scope, and parameter boundary. */
public final class OwnedReportReads {
    private final OwnedReportReadDao dao;
    private final OwnedIdentityHttpBoundary boundary;
    private final JdbcPersonnelIdentityProvider provider;
    private final AuditEventSink audit;

    public OwnedReportReads(OwnedReportReadDao dao, OwnedIdentityHttpBoundary boundary,
            JdbcPersonnelIdentityProvider provider, AuditEventSink audit) {
        this.dao = dao; this.boundary = boundary; this.provider = provider; this.audit = audit;
    }

    public OwnedReportRecord read(String id, HttpSession session) throws SQLException {
        OwnedPrincipal principal = boundary.currentPrincipal(session);
        OwnedReportRecord report = dao.find(id, principal.getId());
        if (report == null) {
            record(principal, id, false, "line-permission-denied");
            throw new AccessDeniedException("REPORT_UNAUTHORIZED");
        }
        if (!"reportingService".equals(report.getCategory())) throw new UnsupportedOperationException("REPORT_CATEGORY_DISABLED");
        Map<String, String> names = report.parameterNames();
        if (names.size() != 2 || !names.containsKey("ORGID") || !names.containsKey("REPORTDATE"))
            throw new UnsupportedOperationException("REPORT_PARAMETERS_DISABLED");
        if (!report.getPath().startsWith("/") || report.getPath().contains("?") || report.getPath().contains("#")
                || report.getPath().indexOf('\r') >= 0 || report.getPath().indexOf('\n') >= 0)
            throw new IllegalArgumentException("Invalid report path");
        record(principal, id, true, "line-permission-granted");
        return report;
    }

    public String request(OwnedReportRecord report, HttpSession session, Map<String, String[]> supplied) throws SQLException {
        OwnedPrincipal principal = boundary.currentPrincipal(session);
        Map<String, String> lower = new LinkedHashMap<String, String>();
        if (supplied.size() != 2) throw new IllegalArgumentException("Unexpected report parameter");
        for (Map.Entry<String, String[]> entry : supplied.entrySet()) {
            String[] values = entry.getValue();
            String key = entry.getKey().toLowerCase(Locale.ROOT);
            if (values == null || values.length != 1 || values[0] == null || values[0].trim().length() == 0
                    || values[0].length() > 128 || lower.put(key, values[0]) != null)
                throw new IllegalArgumentException("Ambiguous report parameter");
        }
        Map<String, String> ordered = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> name : report.parameterNames().entrySet()) {
            String value = lower.get(name.getValue().toLowerCase(Locale.ROOT));
            if (value == null) throw new IllegalArgumentException("Missing report parameter");
            ordered.put(name.getKey(), value);
        }
        if (!provider.permittedOrganizationIds(principal).contains(ordered.get("ORGID"))) {
            record(principal, report.getId(), false, "organization-scope-denied");
            throw new AccessDeniedException("REPORT_UNAUTHORIZED");
        }
        String date = ordered.get("REPORTDATE");
        try {
            if (!LocalDate.parse(date).toString().equals(date)) throw new IllegalArgumentException("Invalid report date");
        } catch (java.time.DateTimeException invalid) { throw new IllegalArgumentException("Invalid report date"); }
        StringBuilder request = new StringBuilder("GET ").append(report.getPath());
        for (Map.Entry<String, String> entry : ordered.entrySet()) request.append('&').append(entry.getKey()).append('=').append(encode(entry.getValue()));
        record(principal, report.getId(), true, "scoped-report-request");
        return request.toString();
    }

    private void record(OwnedPrincipal actor, String id, boolean allowed, String reason) {
        audit.record(new AuditEvent(Instant.now(), "report-read", actor.getId(), id,
                allowed ? AuditEvent.Outcome.ALLOWED : AuditEvent.Outcome.DENIED, reason, null));
    }

    public void exported(OwnedReportRecord report, HttpSession session) throws SQLException {
        OwnedPrincipal principal = boundary.currentPrincipal(session);
        audit.record(new AuditEvent(Instant.now(), "report-export", principal.getId(), report.getId(),
                AuditEvent.Outcome.ALLOWED, "synthetic-isolated-replay-html", null));
    }

    private static String encode(String value) {
        try { return URLEncoder.encode(value, "UTF-8"); }
        catch (Exception impossible) { throw new IllegalStateException(impossible); }
    }
}
