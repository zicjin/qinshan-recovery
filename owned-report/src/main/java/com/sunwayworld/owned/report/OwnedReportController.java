package com.sunwayworld.owned.report;

import java.sql.SQLException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/** One read-only report page and its isolated third-party replay seam. */
@RestController
@RequestMapping("/secure/core/module/sys/report-configs")
public final class OwnedReportController {
    private final OwnedReportReads reads;
    private final OwnedReportReplayClient replay;

    public OwnedReportController(OwnedReportReads reads, OwnedReportReplayClient replay) {
        this.reads = reads; this.replay = replay;
    }

    @GetMapping(value = "/page/common-report", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String page(HttpServletRequest request, @RequestParam(value = "lineId") String lineId)
            throws SQLException {
        HttpSession session = request.getSession(false);
        OwnedReportRecord report = reads.read(lineId, session);
        String fields = fields(report);
        return "<!doctype html><html lang=\"zh-CN\"><head><meta charset=\"utf-8\"><title>报表回放</title>"
                + "<style>body{font-family:sans-serif;margin:2rem}label{display:block;margin:.5rem 0}"
                + "output{display:block;margin-top:1rem;padding:1rem;border:1px solid #ccc}</style></head><body>"
                + "<h1 id=\"report-title\">" + escape(report.getName()) + "</h1>"
                + "<p>仅允许隔离第三方报表回放；生产端点不会被调用。</p>"
                + "<form id=\"report-form\">" + fields
                + "<button id=\"report-submit\">生成报表</button></form><output id=\"report-result\" aria-live=\"polite\"></output>"
                + "<a id=\"report-export\" hidden download>下载隔离回放</a>"
                + "<iframe id=\"report-preview\" title=\"报表结果\" sandbox=\"allow-same-origin\" hidden style=\"width:100%;height:22rem\"></iframe>"
                + "<script>(function(){var f=document.getElementById('report-form'),o=document.getElementById('report-result');"
                + "f.addEventListener('input',function(){var d=document.getElementById('report-export');d.hidden=true;d.removeAttribute('href');});"
                + "f.addEventListener('submit',function(e){e.preventDefault();var p=document.getElementById('report-preview'),d=document.getElementById('report-export');p.hidden=true;p.srcdoc='';d.hidden=true;d.removeAttribute('href');o.textContent='正在请求隔离回放…';"
                + "var q=new URLSearchParams(new FormData(f));fetch('/secure/core/module/sys/report-configs/lines/"
                + escapeJs(report.getId()) + "/result?'+q.toString(),{credentials:'same-origin'})"
                + ".then(function(r){return r.json().then(function(v){return {ok:r.ok,v:v}})})"
                + ".then(function(x){var d=document.getElementById('report-export');o.textContent=x.ok?'报表已生成（隔离回放）':(x.v.code||'报表失败');o.dataset.status=x.ok?'success':'failure';d.hidden=!x.ok;d.removeAttribute('href');if(x.ok){p.srcdoc='<meta http-equiv=\"Content-Security-Policy\" content=\"default-src '+String.fromCharCode(39)+'none'+String.fromCharCode(39)+'\">'+x.v.html;p.hidden=false;q.set('format','html');d.href='/secure/core/module/sys/report-configs/lines/"
                + escapeJs(report.getId()) + "/export?'+q.toString();}})"
                + ".catch(function(x){o.textContent='报表请求失败';o.dataset.status='failure';});});})();</script></body></html>";
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> metadata(HttpServletRequest request, @PathVariable("id") String id) throws SQLException {
        HttpSession session = request.getSession(false);
        OwnedReportRecord report = reads.read(id, session);
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("id", report.getId()); result.put("reportName", report.getName());
        result.put("reportCategory", report.getCategory()); result.put("param", report.getParameterSpec());
        result.put("source", "historical-report-config-line");
        return result;
    }

    @GetMapping(value = "/lines/{id}/result", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> result(HttpServletRequest request, @PathVariable("id") String id) throws SQLException {
        HttpSession session = request.getSession(false);
        OwnedReportRecord report = reads.read(id, session);
        String canonicalRequest = reads.request(report, session, request.getParameterMap());
        try {
            OwnedReportReplayClient.ReplayResult rendered = replay.render(canonicalRequest);
            Map<String, Object> response = new LinkedHashMap<String, Object>();
            response.put("status", "success"); response.put("source", rendered.getSource());
            response.put("requestHash", rendered.getRequestHash()); response.put("html", rendered.getBody());
            return ResponseEntity.ok(response);
        } catch (OwnedReportReplayClient.Disabled disabled) {
            return failure(HttpStatus.SERVICE_UNAVAILABLE, "REPORT_REPLAY_DISABLED", disabled.getMessage());
        } catch (OwnedReportReplayClient.Failure failure) {
            return failure(HttpStatus.BAD_GATEWAY, failure.getMessage(), failure.getMessage());
        }
    }

    /**
     * Downloads the already rendered local replay as an HTML attachment. This
     * is deliberately narrower than third-party PDF/Excel rendering: no
     * renderer, network client, or vendor authorization material is involved.
     */
    @GetMapping(value = "/lines/{id}/export")
    public ResponseEntity<byte[]> export(HttpServletRequest request, @PathVariable("id") String id,
            @RequestParam(value = "format", required = false) String format) throws SQLException {
        HttpSession session = request.getSession(false);
        OwnedReportRecord report = reads.read(id, session);
        String canonicalRequest = reads.request(report, session, reportParameters(request));
        if (!"html".equals(format)) return exportFailure(HttpStatus.NOT_IMPLEMENTED, "REPORT_EXPORT_DISABLED");
        try {
            OwnedReportReplayClient.ReplayResult rendered = replay.render(canonicalRequest);
            byte[] body = ("<meta http-equiv=\"Content-Security-Policy\" content=\"default-src 'none'; base-uri 'none'; form-action 'none'\">"
                    + rendered.getBody()).getBytes(StandardCharsets.UTF_8);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("text", "html", StandardCharsets.UTF_8));
            headers.setContentLength(body.length);
            headers.set(HttpHeaders.CACHE_CONTROL, "no-store");
            headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"report-" + report.getId() + ".html\"");
            headers.set("X-Owned-Report-Source", rendered.getSource());
            headers.set("X-Owned-Report-Request-Hash", rendered.getRequestHash());
            headers.set("X-Content-Type-Options", "nosniff");
            headers.set("Content-Security-Policy", "default-src 'none'; sandbox");
            reads.exported(report, session);
            return new ResponseEntity<byte[]>(body, headers, HttpStatus.OK);
        } catch (OwnedReportReplayClient.Disabled disabled) {
            return exportFailure(HttpStatus.SERVICE_UNAVAILABLE, "REPORT_REPLAY_DISABLED");
        } catch (OwnedReportReplayClient.Failure failure) {
            return exportFailure(HttpStatus.BAD_GATEWAY, failure.getMessage());
        }
    }

    private static Map<String, String[]> reportParameters(HttpServletRequest request) {
        Map<String, String[]> values = new LinkedHashMap<String, String[]>(request.getParameterMap());
        String[] formats = values.remove("format");
        if (formats == null || formats.length != 1 || formats[0] == null) {
            throw new IllegalArgumentException("Invalid report export format");
        }
        return values;
    }

    private static ResponseEntity<byte[]> exportFailure(HttpStatus status, String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        byte[] body = ("{\"status\":\"failure\",\"code\":\"" + code + "\"}")
                .getBytes(StandardCharsets.UTF_8);
        headers.setContentLength(body.length);
        return new ResponseEntity<byte[]>(body, headers, status);
    }
    private static ResponseEntity<Map<String, Object>> failure(HttpStatus status, String code, String message) {
        Map<String, Object> body = new LinkedHashMap<String, Object>(); body.put("status", "failure"); body.put("code", code); body.put("message", message); body.put("html", "");
        return ResponseEntity.status(status).body(body);
    }
    private static String escape(String value) { return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;"); }
    private static String escapeJs(String value) { return value.replace("\\", "\\\\").replace("'", "\\'"); }
    private static String fields(OwnedReportRecord report) {
        StringBuilder result = new StringBuilder();
        for (String name : report.parameterNames().values()) {
            result.append("<label>").append(escape(name)).append(" <input name=\"").append(escape(name))
                    .append("\" required></label>");
        }
        return result.toString();
    }
}
