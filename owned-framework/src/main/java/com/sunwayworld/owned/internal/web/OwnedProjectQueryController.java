package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunwayworld.owned.internal.data.JdbcValue;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Owned Java read seam for the project GenericResource query family. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedProjectQueryController {
    private static final String PREFIX = "/secure/emc/module/bp/project/projects";
    private static final String SOURCE =
            "SELECT T.*, B.FORMSTYLE, B.BIZCATEGORYNAME, B.EDITMONITORINFO, B.SAMPLEORGID, "
            + "C.ORGNAME AS REPORTORGNAME, CASE WHEN T.STATUS='cancel' THEN '终止' ELSE '正常' END AS CANCELSTATUS "
            + "FROM T_EMC_PROJECT T LEFT JOIN T_EMC_BIZ_CATEGORY B ON B.ID=T.BIZCATEGORYID "
            + "LEFT JOIN T_CORE_ORG C ON C.ID=T.REPORTORGID WHERE T.CREATEDBYORGID=?";
    private static final String REPORT_EDIT_SOURCE =
            "SELECT T.*, B.FORMSTYLE, B.BIZCATEGORYNAME, B.EDITMONITORINFO, B.SAMPLEORGID, "
            + "(SELECT COUNT(1) FROM T_EMC_ORDER O WHERE O.PROJID=T.ID AND O.TERMINATION!='1') AS SAMPLEORDERCOUNT, "
            + "C.ORGNAME AS REPORTORGNAME, "
            + "CASE WHEN T.STATUS='cancel' THEN '终止' ELSE '正常' END AS CANCELSTATUS, "
            + "CASE WHEN (SELECT COUNT(1) FROM T_EMC_AUDIT_RECORD AR WHERE AR.PROJID=T.ID AND AR.TASKNAME='报告归档')>1 THEN '报告已外发' "
            + "WHEN (SELECT COUNT(1) FROM T_EMC_AUDIT_RECORD AR WHERE AR.PROJID=T.ID AND AR.TASKNAME='盖章发送')>1 THEN '报告已打印' ELSE '无' END AS REPORTCHANGEINFO, "
            + "D.ACTIVATEDFLAG AS SCHEDULEACTIVEDFLAG "
            + "FROM T_EMC_PROJECT T LEFT JOIN T_EMC_BIZ_CATEGORY B ON B.ID=T.BIZCATEGORYID "
            + "LEFT JOIN T_CORE_ORG C ON C.ID=T.REPORTORGID "
            + "LEFT JOIN T_CORE_SCHEDULE_TASK_CONFIG D ON D.\"UID\"=CONCAT('T_EMC_PROJECT$',T.ID) WHERE T.CREATEDBYORGID=?";

    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedProjectQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("project dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @GetMapping(path = PREFIX + "/info/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { require(request); return ok("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = {PREFIX + "/queries", PREFIX + "/queries/raw", PREFIX + "/queries/searchable"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try { OwnedPrincipal principal = require(request); return ok(selectPage(principal.getOrgId(), Page.parse(body))); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = PREFIX + "/select-by-reportedit/queries",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> selectByReportEdit(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            return ok(selectPage(REPORT_EDIT_SOURCE, principal.getOrgId(), Page.parse(body)));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = PREFIX + "/reports/queries",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> reportFiles(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            JSONObject root = body == null || body.trim().isEmpty() ? new JSONObject() : JSON.parseObject(body);
            String projectId = text(root, "projectId");
            String source = "SELECT F.* FROM T_CORE_FILE F WHERE EXISTS (SELECT 1 FROM T_EMC_REPORT R "
                    + "WHERE R.CREATEDBYORGID=? AND F.TARGETID=CONCAT('T_EMC_REPORT$',R.ID,'F')";
            List<String> values = new ArrayList<String>();
            if (projectId != null && projectId.matches("[0-9]+")) {
                source += " AND R.PROJID=?";
                values.add(projectId);
            }
            source += ")";
            return ok(selectPage(source, principal.getOrgId(), values, Page.parse(body)));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private String selectPage(String organization, Page page) throws SQLException {
        return selectPage(SOURCE, organization, page);
    }

    private String selectPage(String source, String organization, Page page) throws SQLException {
        return selectPage(source, organization, new ArrayList<String>(), page);
    }

    private String selectPage(String source, String organization, List<String> values, Page page) throws SQLException {
        String countSql = "SELECT COUNT(1) FROM (" + source + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM ("
                + source + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total;
        List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
        Map<String,String> types = new LinkedHashMap<String,String>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(countSql)) {
                bind(statement, organization, values, 1);
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) throw new SQLException("project count unavailable");
                    total = result.getInt(1);
                }
            }
            try (PreparedStatement statement = connection.prepareStatement(pageSql)) {
                int index = bind(statement, organization, values, 1); statement.setInt(index++, page.start()); statement.setInt(index, page.end());
                try (ResultSet result = statement.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    for (int i = 1; i <= metadata.getColumnCount(); i++) {
                        String label = metadata.getColumnLabel(i);
                        if (!"RN_".equalsIgnoreCase(label)) types.put(label.toLowerCase(Locale.ROOT), type(metadata.getColumnType(i)));
                    }
                    while (result.next()) rows.add(row(result, metadata));
                }
            }
        }
        Map<String,Object> envelope = new LinkedHashMap<String,Object>();
        envelope.put("columnTypeMap", types); envelope.put("first", page.number == 1);
        envelope.put("last", total == 0 || page.number >= pages(total, page.size));
        envelope.put("numberOfElements", rows.size()); envelope.put("pageNumber", page.number);
        envelope.put("pageSize", page.size); envelope.put("rows", rows); envelope.put("total", total);
        Map<String,Object> totals = new LinkedHashMap<String,Object>(); totals.put("qty_", total);
        envelope.put("totalMap", totals); envelope.put("totalPages", pages(total, page.size));
        return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    private static Map<String,Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String,Object> value = new LinkedHashMap<String,Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i); if ("RN_".equalsIgnoreCase(label)) continue;
            Object item = JdbcValue.normalize(result.getObject(i)); if (item != null) value.put(camel(label), item);
        }
        value.put("ext$", new LinkedHashMap<String,Object>()); return value;
    }
    private static int bind(PreparedStatement statement, String organization, List<String> values, int index) throws SQLException {
        statement.setString(index++, organization);
        for (String value : values) statement.setString(index++, value);
        return index;
    }
    private static String camel(String label) { StringBuilder value = new StringBuilder(); boolean upper = false; for (char c : label.toLowerCase(Locale.ROOT).toCharArray()) { if (c == '_') { upper = true; continue; } value.append(upper ? Character.toUpperCase(c) : c); upper = false; } return value.toString(); }
    private static String text(JSONObject root, String key) { Object value = root == null ? null : root.get(key); return value == null ? null : String.valueOf(value).trim(); }
    private static String type(int type) { return type == Types.DATE || type == Types.TIMESTAMP || type == Types.TIMESTAMP_WITH_TIMEZONE ? "dateTime" : (type == Types.NUMERIC || type == Types.DECIMAL || type == Types.INTEGER || type == Types.BIGINT ? "number" : "string"); }
    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PROJECT.UNAVAILABLE\"}"); }

    private static final class Page {
        final int number, size; Page(int number, int size) { this.number = number; this.size = size; }
        static Page parse(String body) { JSONObject root = body == null || body.trim().isEmpty() ? new JSONObject() : JSON.parseObject(body); JSONObject p = root == null ? null : root.getJSONObject("p"); int n = p == null ? 1 : Math.max(1, p.getIntValue("n")); int s = p == null ? 50 : Math.min(500, Math.max(1, p.getIntValue("s"))); return new Page(n, s); }
        int start() { return (number - 1) * size; } int end() { return number * size; }
    }
}
