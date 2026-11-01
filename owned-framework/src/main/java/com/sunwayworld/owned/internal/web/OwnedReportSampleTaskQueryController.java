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

/**
 * Source-owned implementation of the report/advisory/sample task-query
 * aliases inherited from GenericResource.  The aliases share one Spring
 * application, identity seam and JDBC projection; they do not create a
 * compatibility process or an internal HTTP bridge.
 */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedReportSampleTaskQueryController {
    private static final String REPORT = "/secure/emc/module/bp/report/reports";
    private static final String ADVISORY = "/secure/emc/module/bp/report/advisory-reports";
    private static final String SAMPLE = "/secure/emc/module/bp/sample/samples";

    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedReportSampleTaskQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("report/sample dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @PostMapping(path = {
            REPORT + "/queries/tasks",
            ADVISORY + "/queries/tasks",
            SAMPLE + "/queries/tasks"
            }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            return ok(selectPage(resource(request.getRequestURI()), principal, Query.parse(body)));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {
            REPORT + "/info/status",
            ADVISORY + "/info/status",
            SAMPLE + "/info/status"
            }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try {
            require(request);
            return ok("{\"ext$\":{}}");
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private static String resource(String path) {
        if (path != null && path.startsWith(REPORT + "/")) return "report";
        if (path != null && path.startsWith(ADVISORY + "/")) return "advisory";
        if (path != null && path.startsWith(SAMPLE + "/")) return "sample";
        throw new IllegalArgumentException("unsupported report/sample task route");
    }

    private String selectPage(String resource, OwnedPrincipal principal, Query query) throws SQLException {
        String source = source(resource, query, principal);
        String countSql = "SELECT COUNT(1) FROM (" + source + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM (" + source + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total;
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, String> types = new LinkedHashMap<String, String>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement count = connection.prepareStatement(countSql)) {
                bind(count, principal, query, 1);
                try (ResultSet result = count.executeQuery()) {
                    if (!result.next()) throw new SQLException("report/sample count returned no row");
                    total = result.getInt(1);
                }
            }
            try (PreparedStatement page = connection.prepareStatement(pageSql)) {
                int next = bind(page, principal, query, 1);
                page.setInt(next++, query.page.start());
                page.setInt(next, query.page.end());
                try (ResultSet result = page.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    for (int i = 1; i <= metadata.getColumnCount(); i++) {
                        String label = metadata.getColumnLabel(i);
                        if (!"RN_".equalsIgnoreCase(label)) types.put(label.toLowerCase(Locale.ROOT), type(metadata.getColumnType(i)));
                    }
                    // The supplier GenericResource exposes its synthetic row
                    // number in columnTypeMap even though it is omitted from
                    // each JSON row.
                    types.put("rn_", "number");
                    while (result.next()) rows.add(row(result, metadata));
                }
            }
        }
        Map<String, Object> envelope = new LinkedHashMap<String, Object>();
        envelope.put("columnTypeMap", types);
        envelope.put("first", query.page.number == 1);
        envelope.put("last", total == 0 || query.page.number >= pages(total, query.page.size));
        envelope.put("numberOfElements", rows.size());
        envelope.put("pageNumber", query.page.number);
        envelope.put("pageSize", query.page.size);
        envelope.put("rows", rows);
        envelope.put("total", total);
        Map<String, Object> totalMap = new LinkedHashMap<String, Object>();
        totalMap.put("qty_", total);
        envelope.put("totalMap", totalMap);
        envelope.put("totalPages", pages(total, query.page.size));
        return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    private static String source(String resource, Query query, OwnedPrincipal principal) {
        StringBuilder sql = new StringBuilder();
        if ("report".equals(resource)) {
            sql.append("SELECT T.* FROM (SELECT A.*,B.CREATEDBYNAME AS PROJUSERNAME,B.PROJNO,B.PROJNAME,B.ORGID,B.ORGNAME,B.TESTORGNAME,B.ENTRUSTORGNAME,B.ENTRUSTORGADDRESS,B.ENTRUSTORGPHONE,B.CREATEDBYNAME AS PROJCREATEDBYNAME,B.REPORTREVIEWERID,B.REPORTREVIEWERNAME,B.REPORTISSUEUSERID,B.REPORTISSUEUSERNAME,B.REPORTEDITUSERNAME,B.REPORTEDITUSERID,B.REPORTISSUETIME,B.CONTRACTNO,B.SAMPLE,B.URGENT,B.SUBPACKAGE,B.COMPLEDATE AS PROJCOMPLEDATE,B.FIRSTISSUEDATETIME AS FIRSTISSUEDATE,B.DELIVERYDATE AS PROJDELIVERYDATE,B.REPORTREMARK,C.ID AS BIZID,C.BIZCATEGORYID,C.BIZCATEGORYNAME,C.FLOWCONTROL,D.REPORTNAME AS TEMPLATENAME,F.MONITORTMPLGROUP,G.REPORTNAME AS QCREPORTNAME,OA.SSGFGSMC,CASE WHEN A.PROCESSSTATUS LIKE 'approve%' THEN (SELECT LISTAGG(TASKNAME, ',') WITHIN GROUP(ORDER BY TASKNAME) FROM T_CORE_BPMN_INST I,T_CORE_BPMN_INST_TASK K WHERE I.ID=K.INSTID AND I.TARGETID=('T_EMC_REPORT$'||A.ID)) ELSE '报告编制' END AS TASKNAME_,(SELECT MAX(EXPRESSNO) FROM T_EMC_REPORT_SEND_INFO RSI WHERE A.PROJID=RSI.PROJID) AS EXPRESSNO FROM T_EMC_REPORT A LEFT JOIN T_EMC_PROJECT B ON A.PROJID=B.ID LEFT JOIN T_EMC_BIZ_CATEGORY C ON B.BIZCATEGORYID=C.ID LEFT JOIN T_CORE_REPORT_CONFIG_LINE D ON A.REPORTCONFIGLINEID=D.ID LEFT JOIN T_EMC_MONITOR_TMPL E ON A.MONITORTMPLID=E.ID LEFT JOIN T_EMC_MONITOR_TMPL_GROUP F ON E.MONITORTMPLGROUPID=F.ID LEFT JOIN T_CORE_REPORT_CONFIG_LINE G ON A.QCREPORTLINEID=G.ID LEFT JOIN T_EMC_OA_ORG OA ON OA.ZZBH=A.CREATEDBYORGID) T WHERE T.CREATEDBYORGID=?");
            if ("reportEdit".equals(query.targetType)) sql.append(" AND (T.REPORTEDITUSERID=? OR T.REPORTEDITUSERID IS NULL)");
            if ("reportAudit".equals(query.targetType)) sql.append(" AND EXISTS (SELECT 1 FROM T_EMC_PROJECT P WHERE P.ID=T.PROJID AND P.REPORTREVIEWERID=? )");
            if ("reportIssue".equals(query.targetType)) sql.append(" AND EXISTS (SELECT 1 FROM T_EMC_PROJECT P WHERE P.ID=T.PROJID AND P.REPORTISSUEUSERID=? )");
            return sql.toString();
        }
        if ("advisory".equals(resource)) {
            return "SELECT T.* FROM (SELECT A.*,B.PROJNAME,B.CREATEDBYNAME AS PROJUSERNAME,B.ORGNAME,B.TESTORGNAME,B.ACCOUNTINGFLAG,B.ENTRUSTORGNAME,C.BIZCATEGORYNAME FROM T_EMC_ADVISORY_REPORT A LEFT JOIN T_EMC_PROJECT B ON A.PROJID=B.ID LEFT JOIN T_EMC_BIZ_CATEGORY C ON B.BIZCATEGORYID=C.ID) T WHERE T.CREATEDBYORGID=?";
        }
        sql.append("SELECT T.* FROM (SELECT A.*,P.PROJNO,P.PROJNAME,P.TESTORGNAME,P.ENTRUSTORGNAME,P.BIZCATEGORYID,B.PROPERTYTMPLID AS TMPLID,B.ELNTMPLID,P.BIZCATEGORYCODE,P.SAMPLE,P.URGENT,CASE WHEN OT.ANALYSISCOUNT IS NULL THEN 0 ELSE OT.ANALYSISCOUNT END AS ANALYSISCOUNT,C.REPORTNAME FROM T_EMC_SAMPLE A LEFT JOIN T_EMC_PROJECT P ON P.ID=A.PROJID LEFT JOIN T_EMC_MONITOR_TMPL B ON B.ID=A.MONITORTMPLID LEFT JOIN T_CORE_REPORT_CONFIG_LINE C ON C.ID=A.REPORTCONFIGLINEID LEFT JOIN (SELECT COUNT(OT.ID) AS ANALYSISCOUNT,O.SAMPLEID FROM T_EMC_ORDER O,T_EMC_ORDER_TASK OT WHERE O.ID=OT.ORDERID AND OT.ANALYSISTYPE='analysis' GROUP BY O.SAMPLEID) OT ON A.ID=OT.SAMPLEID) T WHERE T.CREATEDBYORGID=?");
        if ("editSample".equals(query.targetType)) sql.append(" AND EXISTS (SELECT 1 FROM T_EMC_ORDER O LEFT JOIN T_EMC_FOLDER F ON O.FOLDERID=F.ID WHERE O.SAMPLEID=T.ID AND (F.SCENEUSERID=? OR F.SCENEUSERNAME=? OR INSTR(','||F.SAMPLEUSERNAME||',',','||?||',')>0))");
        if ("auditedData".equals(query.targetType)) sql.append(" AND EXISTS (SELECT 1 FROM T_EMC_REPORT R WHERE R.MONITORTMPLID=T.MONITORTMPLID AND R.PROJID=T.PROJID AND R.STATUS='draft')");
        if ("orderReceipt".equals(query.targetType)) sql.append(" AND EXISTS (SELECT 1 FROM T_EMC_ORDER R LEFT JOIN T_EMC_ORDER_TASK O ON O.ORDERID=R.ID WHERE R.SAMPLEID=T.ID AND O.STATUS='preLogged' AND O.ANALYSISTYPE='analysis' AND R.TERMINATION!='1')");
        return sql.toString();
    }

    private static int bind(PreparedStatement statement, OwnedPrincipal principal, Query query, int index) throws SQLException {
        statement.setString(index++, principal.getOrgId());
        if ("reportEdit".equals(query.targetType) || "reportAudit".equals(query.targetType) || "reportIssue".equals(query.targetType)) statement.setString(index++, principal.getId());
        if ("editSample".equals(query.targetType)) {
            statement.setString(index++, query.sceneUserId == null ? principal.getId() : query.sceneUserId);
            statement.setString(index++, query.sceneUserName == null ? "" : query.sceneUserName);
            statement.setString(index++, query.sampleUserName == null ? principal.getId() : query.sampleUserName);
        }
        return index;
    }

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i);
            if ("RN_".equalsIgnoreCase(label)) continue;
            value.put(camel(label), JdbcValue.normalize(result.getObject(i)));
        }
        return value;
    }
    private static String camel(String label) { StringBuilder value = new StringBuilder(); boolean upper = false; for (char c : label.toLowerCase(Locale.ROOT).toCharArray()) { if (c == '_') { upper = true; continue; } value.append(upper ? Character.toUpperCase(c) : c); upper = false; } return value.toString(); }
    private static String type(int type) { if (type == Types.DATE || type == Types.TIMESTAMP || type == Types.TIMESTAMP_WITH_TIMEZONE) return "dateTime"; if (type == Types.NUMERIC || type == Types.DECIMAL || type == Types.INTEGER || type == Types.BIGINT) return "number"; return "string"; }
    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}"); }

    private static final class Query {
        final Page page; final String targetType; final String sceneUserId; final String sceneUserName; final String sampleUserName;
        Query(Page page, String targetType, String sceneUserId, String sceneUserName, String sampleUserName) { this.page = page; this.targetType = targetType; this.sceneUserId = sceneUserId; this.sceneUserName = sceneUserName; this.sampleUserName = sampleUserName; }
        static Query parse(String body) {
            JSONObject root = body == null || body.trim().isEmpty() ? new JSONObject() : JSON.parseObject(body);
            JSONObject p = root == null ? null : root.getJSONObject("p");
            int n = p == null ? 1 : Math.max(1, p.getIntValue("n"));
            int s = p == null ? 50 : Math.min(500, Math.max(1, p.getIntValue("s")));
            return new Query(new Page(n, s), text(root, "targetType"), text(root, "sceneUserId"), text(root, "sceneUserName"), text(root, "sampleUserName"));
        }
        static String text(JSONObject root, String key) { if (root == null) return null; String value = root.getString(key); if (value == null) { JSONObject q = root.getJSONObject("q"); if (q != null) value = q.getString(key); } return value == null || value.trim().isEmpty() ? null : value.trim(); }
    }
    private static final class Page { final int number, size; Page(int number, int size) { this.number = number; this.size = size; } int start() { return (number - 1) * size; } int end() { return number * size; } }
}
