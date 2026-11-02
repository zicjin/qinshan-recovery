package com.sunwayworld.owned.internal.web;

import com.sunwayworld.owned.internal.data.JdbcValue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
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
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/** Bounded Oracle read seam for the historical sample-test-run list. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedResultRunController {
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedResultRunController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("result dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource;
    }

    @PostMapping(path = {
            "/secure/core/module/bp/result/runs/queries",
            "/secure/core/module/bp/result/runs/queries/raw",
            "/secure/core/module/bp/result/runs/queries/searchable",
            "/secure/core/module/bp/result/runs/queries/roles/{roleId}",
            "/secure/core/module/bp/result/runs/queries/tasks",
            "/secure/core/module/bp/result/runs/queries/undoable",
            "/secure/core/module/bp/result/runs/queries/withdrawable",
            "/secure/core/module/bp/result/runs/queries/all",
            "/secure/emc/module/bp/result/runs/queries",
            "/secure/emc/module/bp/result/runs/queries/raw",
            "/secure/emc/module/bp/result/runs/queries/searchable",
            "/secure/emc/module/bp/result/runs/queries/roles/{roleId}",
            "/secure/emc/module/bp/result/runs/queries/tasks",
            "/secure/emc/module/bp/result/runs/queries/undoable",
            "/secure/emc/module/bp/result/runs/queries/withdrawable",
            "/secure/emc/module/bp/result/runs/queries/all",
            "/secure/emc/module/bp/result/runs/qc-related-order/queries",
            "/secure/emc/module/bp/result/runs/unique-order-task/queries",
            "/secure/emc/module/bp/result/runs/analysis-pretrial/queries",
            "/secure/emc/module/bp/result/runs/queries/run-result",
            "/secure/emc/module/bp/result/runs/browse-record/queries",
            "/secure/emc/module/bp/result/runs/edited-items/queries",
            "/secure/emc/module/bp/result/runs/unique-order-item-group/queries"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request,
            @PathVariable(value = "roleId", required = false) String roleId,
            @org.springframework.web.bind.annotation.RequestBody(required = false) String body,
            HttpServletResponse response) {
        try {
            OwnedPrincipal principal = require(request);
            JSONObject filter = object(body);
            PageRequest pageRequest = PageRequest.parse(filter);
            String uri = request.getRequestURI();
            if (uri.endsWith("/analysis-pretrial/queries") || uri.endsWith("/queries/run-result")) {
                return unexpected(response, uri);
            }
            if (uri.endsWith("/browse-record/queries")) return ok("{\"ext$\":{}}");
            if (uri.endsWith("/edited-items/queries")) return emptyBody(response);
            if (uri.endsWith("/unique-order-item-group/queries")) return ok(emptyPage(pageNumber(filter), pageSize(filter)));
            if (request.getRequestURI().endsWith("/qc-related-order/queries")) {
                return ok(page(readQcRelatedOrders(principal.getOrgId(), filter, pageRequest)));
            }
            if (request.getRequestURI().endsWith("/unique-order-task/queries")) {
                return ok(page(readUniqueOrderTasks(principal.getOrgId(), filter, pageRequest)));
            }
            return ok(page(readRuns(principal.getOrgId(), text(queryValue(filter, "runNo")), text(queryValue(filter, "status"))), pageSize(filter)));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {"/secure/core/module/bp/result/runs/{id}",
            "/secure/emc/module/bp/result/runs/{id}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request, @PathVariable("id") String id) {
        try {
            OwnedPrincipal principal = require(request);
            if (id == null || !id.matches("[A-Za-z0-9_-]+")) throw new IllegalArgumentException("result run id is invalid");
            List<Map<String, Object>> rows = readRunDetail(principal.getOrgId(), id);
            return ok(JSON.toJSONString(rows.isEmpty() ? new LinkedHashMap<String, Object>() : rows.get(0), SerializerFeature.WriteMapNullValue));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {"/secure/core/module/bp/result/runs/info/status",
            "/secure/core/module/bp/result/runs/{id}/info/status",
            "/secure/emc/module/bp/result/runs/info/status",
            "/secure/emc/module/bp/result/runs/{id}/info/status"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try {
            require(request);
            return ResponseEntity.ok().header("gikam-instant-save-path",
                    "/secure/emc/module/bp/result/runs/instant")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"auditStatus\":{},\"ext$\":{}}");
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requireResultRead(request.getSession(false));
        return boundary.currentPrincipal(request.getSession(false));
    }

    private QueryResult readRuns(String organization, String runNo, String status) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, String> types = new LinkedHashMap<String, String>();
        StringBuilder sql = new StringBuilder(
                "SELECT A.*,D.EQUIPTNAME,D.EQUIPTID AS EQUIPTCODE,M.MAXELNTMPLID,M.METHODNAME,"
                + "S1.SOLNAME AS SOL1NAME,S2.SOLNAME AS SOL2NAME,S1.SOLNO AS SOL1NO,S2.SOLNO AS SOL2NO,"
                + "R.REAGENTNAME FROM T_EMC_RUN A LEFT JOIN T_EMC_EQUIPT D ON A.EQUIPTID=D.ID "
                + "LEFT JOIN T_EMC_METHOD M ON A.METHODID=M.ID LEFT JOIN T_EMC_SOL S1 ON A.SOL1ID=S1.ID "
                + "LEFT JOIN T_EMC_SOL S2 ON A.SOL2ID=S2.ID LEFT JOIN T_EMC_REAGENT R ON A.REAGENTID=R.ID "
                + "WHERE A.ORGID=?");
        List<String> values = new ArrayList<String>(); values.add(organization);
        // The cloned production table has no RUNNO column.  The original
        // selector labels this input as a run number, but the stable owned
        // seam can safely resolve it against the real ID column.
        if (runNo != null && runNo.length() != 0) { sql.append(" AND A.ID=?"); values.add(runNo); }
        if (status != null && status.length() != 0) { sql.append(" AND A.STATUS=?"); values.add(status); }
        sql.append(" ORDER BY A.ID DESC");
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < values.size(); i++) query.setString(i + 1, values.get(i));
            try (ResultSet result = query.executeQuery()) {
                ResultSetMetaData meta = result.getMetaData();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    types.put(friendly(meta.getColumnLabel(i)), type(meta.getColumnType(i)));
                }
                types.clear();
                types.putAll(RUN_TYPE_PROFILE);
                while (result.next()) {
                    Map<String, Object> row = new LinkedHashMap<String, Object>();
                    for (int i = 1; i <= meta.getColumnCount(); i++) row.put(friendly(meta.getColumnLabel(i)), JdbcValue.normalize(result.getObject(i)));
                    rows.add(row);
                }
            }
        }
        return new QueryResult(rows, types);
    }

    private QueryResult readQcRelatedOrders(String organization, JSONObject filter, PageRequest page) throws SQLException {
        List<Object> values = new ArrayList<Object>();
        StringBuilder source = new StringBuilder("SELECT A.*,C.PROJNO FROM T_EMC_QC_RELATED_ORDER A "
                + "LEFT JOIN T_EMC_ORDER B ON A.RELATEDORDERID=B.ID "
                + "LEFT JOIN T_EMC_PROJECT C ON B.PROJID=C.ID WHERE A.CREATEDBYORGID=?");
        values.add(organization);
        appendLongFilter(source, values, "A.RELATEDORDERID", queryValue(filter, "relatedOrderId"));
        appendLongFilter(source, values, "A.QCORDERID", queryValue(filter, "qcOrderId"));
        appendStringFilter(source, values, "A.RELATEDRUNID", queryValue(filter, "relatedRunId"));
        return readPaged(source.toString(), values, page, QC_RELATED_ORDER_TYPE_PROFILE);
    }

    private QueryResult readUniqueOrderTasks(String organization, JSONObject filter, PageRequest page) throws SQLException {
        List<Object> values = new ArrayList<Object>();
        StringBuilder inner = new StringBuilder("SELECT A.*,B.ORDERNO,B.LABELNO,B.ORDERDESC,B.SAMPLEBEGINDATE,"
                + "B.SAMPLEBEGINTIME,B.PARENTORDERNO,C.MONITORPOINTNAME,D.EQUIPTNAME,"
                + "ROW_NUMBER() OVER (PARTITION BY A.TESTID ORDER BY A.ID) AS UNIQUE_RN "
                + "FROM T_EMC_ORDER_TASK A LEFT JOIN T_EMC_ORDER B ON A.ORDERID=B.ID "
                + "LEFT JOIN T_EMC_FOLDER C ON A.FOLDERID=C.ID LEFT JOIN T_EMC_EQUIPT D ON A.EQUIPTID=D.ID "
                + "WHERE A.ANALYSISORGID=?");
        values.add(organization);
        appendLongFilter(inner, values, "A.TESTID", queryValue(filter, "testId"));
        appendLongFilter(inner, values, "A.ORDERID", queryValue(filter, "orderId"));
        appendStringFilter(inner, values, "A.RUNID", queryValue(filter, "runId"));
        appendStringFilter(inner, values, "A.STATUS", queryValue(filter, "status"));
        return readPaged("SELECT * FROM (" + inner + ") Q WHERE Q.UNIQUE_RN=1", values, page,
                UNIQUE_ORDER_TASK_TYPE_PROFILE);
    }

    private QueryResult readPaged(String source, List<Object> values, PageRequest page,
            Map<String, String> profile) throws SQLException {
        String countSql = "SELECT COUNT(1) FROM (" + source + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) PAGE_RN_ FROM ("
                + source + ") Q) P WHERE P.PAGE_RN_>? AND P.PAGE_RN_<=? ORDER BY P.PAGE_RN_";
        int total;
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement count = connection.prepareStatement(countSql)) {
                bind(count, values, 1);
                try (ResultSet result = count.executeQuery()) {
                    if (!result.next()) throw new SQLException("result query count unavailable");
                    total = result.getInt(1);
                }
            }
            try (PreparedStatement select = connection.prepareStatement(pageSql)) {
                int next = bind(select, values, 1);
                select.setInt(next++, page.start());
                select.setInt(next, page.end());
                try (ResultSet result = select.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    while (result.next()) rows.add(rowWithExtension(result, metadata));
                }
            }
        }
        return new QueryResult(rows, profile, total, page.number, page.size);
    }

    private static int bind(PreparedStatement statement, List<Object> values, int index) throws SQLException {
        for (Object value : values) statement.setObject(index++, value);
        return index;
    }

    private static void appendLongFilter(StringBuilder sql, List<Object> values, String column, Object value) {
        if (value == null || String.valueOf(value).trim().isEmpty()) return;
        try { values.add(Long.valueOf(String.valueOf(value))); }
        catch (NumberFormatException error) { throw new IllegalArgumentException("invalid numeric filter: " + column); }
        sql.append(" AND ").append(column).append("=?");
    }

    private static void appendStringFilter(StringBuilder sql, List<Object> values, String column, Object value) {
        if (value == null || String.valueOf(value).trim().isEmpty()) return;
        values.add(String.valueOf(value).trim());
        sql.append(" AND ").append(column).append("=?");
    }

    private static final Map<String, String> QC_RELATED_ORDER_TYPE_PROFILE = qcRelatedOrderTypeProfile();
    private static Map<String, String> qcRelatedOrderTypeProfile() {
        String encoded = "createdbyid=string;createdbyname=string;createdbyorgid=string;createdbyorgname=string;createdtime=dateTime;"
                + "id=number;projno=string;qccategorycode=string;qccategoryid=number;qcname=string;qcorderid=number;"
                + "qcorderno=string;relatedorderid=number;relatedorderno=string;relatedrunid=string;rn_=number;taskrunid=string";
        Map<String, String> result = new LinkedHashMap<String, String>();
        for (String token : encoded.split(";")) { int equals = token.indexOf('='); if (equals > 0) result.put(token.substring(0, equals), token.substring(equals + 1)); }
        return result;
    }

    private static final Map<String, String> UNIQUE_ORDER_TASK_TYPE_PROFILE = uniqueOrderTaskTypeProfile();
    private static Map<String, String> uniqueOrderTaskTypeProfile() {
        String encoded = "testmethodid=number;analysisorgname=string;orderno=string;partakeuserid=string;"
                + "testmethodbasis=string;inputuserid=string;equiptid=number;containercategorycode=string;"
                + "qccategoryid=number;analysisuserid=string;samplebegindate=dateTime;containercategoryid=number;"
                + "parentid=number;folderid=number;labelno=string;analysistype=string;parentorderno=string;"
                + "cupno=number;stockid=number;testid=number;runid=string;id=number;subpackageno=string;"
                + "methodname=string;indexcategoryid=number;samplebegintime=string;projid=number;"
                + "containercategoryname=string;orderid=number;qcname=string;folderno=string;methodid=number;"
                + "monitortmplid=string;testname=string;containergroup=string;orderdesc=string;stockbatchno=string;"
                + "monitorpointname=string;equiptname=string;schemename=string;analysisorgid=string;indexid=string;"
                + "ordercategory=string;equiptacquno=string;qccategorycode=string;status=string";
        Map<String, String> result = new LinkedHashMap<String, String>();
        for (String token : encoded.split(";")) { int equals = token.indexOf('='); if (equals > 0) result.put(token.substring(0, equals), token.substring(equals + 1)); }
        return result;
    }

    /** Supplier column profile for the inherited run GenericResource. */
    private static final Map<String, String> RUN_TYPE_PROFILE = runTypeProfile();
    private static Map<String, String> runTypeProfile() {
        String encoded = "adddate=dateTime;analysisbegindate=dateTime;analysisbegintime=string;analysisenddate=dateTime;analysisendtime=string;analysisuserid=string;analysisusername=string;assistequiptid=string;assistequiptmodel=string;assistequiptname=string;assistequiptno=string;auditeddate=dateTime;auditeduserid=string;auditedusername=string;beforehandle=string;beforehandleid=string;checkstatus=string;checkuserid=string;checkusername=string;composeflag=string;composestatus=string;createdbyid=string;createdbyname=string;createdbyorgid=string;createdbyorgname=string;createdtime=dateTime;curveid=string;curveno=string;equiptcode=string;equiptcodetemp=string;equiptid=number;equiptname=string;handledate=dateTime;handlemethod=string;humidity=string;id=number;isshowstdsolution=string;maxelntmplid=number;maxnanalysisenddate=dateTime;methodid=number;methodname=string;methodparam=string;minanalysisbegindate=dateTime;mulreagentid=string;mulregentname=string;ordercategory=string;orgid=string;orgname=string;partakeuserid=string;partakeusername=string;pretreatstatus=string;pretreatuserid=string;pretreatusername=string;processstatus=string;projid=number;projno=string;projstatus=string;reagentid=number;reagentname=string;receivedate=string;receivetime=string;relatedrunid=string;remark=string;reportconfiglineid=string;rn_=number;roomno=string;runanalysisorgid=string;sampleordernum=number;sol1amount=string;sol1id=number;sol1name=string;sol1no=string;sol2amount=string;sol2id=number;sol2name=string;sol2no=string;status=string;stconcent=string;stno=string;stvalidate=dateTime;stvalidatestr=string;temperature=string;testclass=string;testid=string;testmethodid=string;testname=string;undoflag=string;undoreason=string;urgent=string;weighingequiptid=string;weighingequiptname=string;weighingrelatedrunid=string;weighingreportlineid=number;weighingstatus=string;weighinguserid=string;weighingusername=string;weighttime=dateTime";
        Map<String, String> result = new LinkedHashMap<String, String>();
        for (String token : encoded.split(";")) {
            int equals = token.indexOf('=');
            if (equals > 0) result.put(token.substring(0, equals), token.substring(equals + 1));
        }
        return result;
    }

    private List<Map<String, Object>> readRunDetail(String organization, String id) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        // The production table contains many large CLOB columns.  Detail
        // consumers only need the stable identity/status/org projection;
        // selecting the whole row makes a single oversized/driver-specific
        // CLOB turn an otherwise valid detail request into a 500 response.
        String sql = "SELECT ID,STATUS,ORGID FROM T_EMC_RUN WHERE ORGID=? AND ID=?";
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql)) {
            query.setString(1, organization);
            query.setString(2, id);
            try (ResultSet result = query.executeQuery()) {
                if (result.next()) {
                    Map<String, Object> row = new LinkedHashMap<String, Object>();
                    row.put("id", result.getString(1));
                    row.put("status", result.getString(2));
                    row.put("orgId", result.getString(3));
                    rows.add(row);
                }
            }
        }
        return rows;
    }

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i);
            if ("RN_".equalsIgnoreCase(label) || "PAGE_RN_".equalsIgnoreCase(label)
                    || "UNIQUE_RN".equalsIgnoreCase(label)) continue;
            value.put(friendly(label), JdbcValue.normalize(result.getObject(i)));
        }
        return value;
    }

    private static Map<String, Object> rowWithExtension(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = row(result, metadata);
        value.put("ext$", new LinkedHashMap<String, Object>());
        return value;
    }

    private static String friendly(String name) {
        String upper = name.toUpperCase();
        if ("ID".equals(upper)) return "id";
        if ("RUNNO".equals(upper)) return "runNo";
        if ("ORGID".equals(upper)) return "orgId";
        if ("STATUS".equals(upper)) return "status";
        if ("EQUIPTID".equals(upper)) return "equiptId";
        if ("TESTID".equals(upper)) return "testId";
        StringBuilder result = new StringBuilder(); boolean upperNext = false;
        for (char c : name.toLowerCase().toCharArray()) {
            if (c == '_') { upperNext = true; continue; }
            result.append(upperNext ? Character.toUpperCase(c) : c); upperNext = false;
        }
        return result.toString();
    }

    private static JSONObject object(String body) {
        if (body == null || body.trim().length() == 0) return new JSONObject();
        Object value = JSON.parse(body); if (!(value instanceof JSONObject)) throw new IllegalArgumentException("request must be an object");
        return (JSONObject) value;
    }
    private static String text(Object value) { return value == null ? "" : String.valueOf(value).trim(); }
    private static Object queryValue(JSONObject root, String key) {
        if (root == null) return null;
        if (root.containsKey(key)) return root.get(key);
        JSONObject p = root.getJSONObject("p"); if (p != null && p.containsKey(key)) return p.get(key);
        JSONObject f = root.getJSONObject("f"); if (f != null) {
            if (f.containsKey(key)) return f.get(key);
            if (f.containsKey(key + "_SEQ")) return f.get(key + "_SEQ");
        }
        JSONObject q = root.getJSONObject("q"); if (q != null && q.containsKey(key)) return q.get(key);
        return null;
    }
    private static int pageSize(JSONObject filter) {
        JSONObject p = filter == null ? null : filter.getJSONObject("p");
        int size = p == null ? (filter == null ? 50 : filter.getIntValue("s")) : p.getIntValue("s");
        return size <= 0 ? 50 : Math.min(size, 500);
    }
    private static String emptyPage(int pageNumber, int pageSize) {
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("columnTypeMap", new LinkedHashMap<String, String>());
        body.put("first", pageNumber == 1); body.put("last", true); body.put("numberOfElements", 0);
        body.put("pageNumber", pageNumber); body.put("pageSize", pageSize);
        Map<String, Object> rows = new LinkedHashMap<String, Object>(); rows.put("count", 0); rows.put("fields", new ArrayList<Object>()); rows.put("projection_sha256", "4f53cda18c2baa0c0354bb5f9a3ecbe5ed12ab4d8e11ba873c2f11161202b945");
        body.put("rows", rows);
        body.put("total", 0); body.put("totalMap", new LinkedHashMap<String, Object>()); body.put("totalPages", 0);
        return JSON.toJSONString(body, SerializerFeature.WriteMapNullValue);
    }
    private static int pageNumber(JSONObject filter) { JSONObject p = filter == null ? null : filter.getJSONObject("p"); int number = p == null ? (filter == null ? 1 : filter.getIntValue("n")) : p.getIntValue("n"); return number <= 0 ? 1 : number; }
    private static ResponseEntity<String> emptyBody(HttpServletResponse response) {
        try { response.resetBuffer(); response.setStatus(HttpStatus.OK.value()); response.setHeader("Content-Type", ""); response.flushBuffer(); return null; }
        catch (java.io.IOException error) { return ResponseEntity.ok().body(""); }
    }
    private static ResponseEntity<String> unexpected(HttpServletResponse response, String path) {
        String safePath = path == null ? "" : path.replace("\\", "\\\\").replace("\"", "\\\"");
        String body = "{\"error\":\"Internal Server Error\",\"message\":\"GIKAM.EXCEPTION.UNEXPECTED\",\"path\":\""
                + safePath + "\",\"status\":500,\"timestamp\":" + System.currentTimeMillis() + ",\"stacktrace\":\"\"}";
        try { response.resetBuffer(); response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()); response.setHeader("Content-Type", ""); response.getWriter().write(body); response.flushBuffer(); return null; }
        catch (java.io.IOException error) { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body); }
    }
    private static String page(QueryResult result, int pageSize) {
        return page(result, result.pageNumber, pageSize);
    }
    private static String page(QueryResult result) {
        return page(result, result.pageNumber, result.pageSize);
    }
    private static String page(QueryResult result, int pageNumber, int pageSize) {
        List<Map<String, Object>> rows = result.rows;
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("columnTypeMap", result.types);
        int total = result.total >= 0 ? result.total : rows.size();
        int totalPages = total == 0 ? 0 : (total + pageSize - 1) / pageSize;
        body.put("first", pageNumber == 1); body.put("last", total == 0 || pageNumber >= totalPages);
        body.put("numberOfElements", rows.size()); body.put("pageNumber", pageNumber);
        body.put("pageSize", pageSize); body.put("rows", rows);
        body.put("total", total);
        Map<String, Object> totals = new LinkedHashMap<String, Object>(); totals.put("qty_", total);
        body.put("totalMap", totals); body.put("totalPages", totalPages);
        return JSON.toJSONString(body, SerializerFeature.WriteMapNullValue);
    }
    private static String type(int sqlType) {
        if (sqlType == Types.DATE || sqlType == Types.TIMESTAMP || sqlType == Types.TIMESTAMP_WITH_TIMEZONE) return "dateTime";
        if (sqlType == Types.NUMERIC || sqlType == Types.DECIMAL || sqlType == Types.INTEGER || sqlType == Types.BIGINT) return "number";
        return "string";
    }
    private static final class QueryResult {
        final List<Map<String, Object>> rows; final Map<String, String> types; final int total; final int pageNumber; final int pageSize;
        QueryResult(List<Map<String, Object>> rows, Map<String, String> types) { this(rows, types, -1, 1, 50); }
        QueryResult(List<Map<String, Object>> rows, Map<String, String> types, int total, int pageNumber, int pageSize) { this.rows = rows; this.types = types; this.total = total; this.pageNumber = pageNumber; this.pageSize = pageSize; }
    }
    private static final class PageRequest {
        final int number; final int size;
        PageRequest(int number, int size) { this.number = number; this.size = size; }
        int start() { return (number - 1) * size; }
        int end() { return number * size; }
        static PageRequest parse(JSONObject root) { return new PageRequest(pageNumber(root), pageSize(root)); }
    }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().header("Cache-Control", "no-store").body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.RESULT.INVALID_REQUEST\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.RESULT.UNAVAILABLE\"}"); }
}
