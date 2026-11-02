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

    /**
     * Select the enabled analysis items for one order task.  The project
     * resource exposes this as a path alias of the batch edited-items query;
     * keep both aliases on the same Oracle relation chain and Spring process.
     */
    @PostMapping(path = "/secure/emc/module/bp/result/runs/select-item/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> selectItem(HttpServletRequest request,
            @PathVariable("id") String id,
            @org.springframework.web.bind.annotation.RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            if (id == null || !id.matches("[0-9]+")) throw new IllegalArgumentException("order task id is invalid");
            JSONObject filter = object(body);
            PageRequest pageRequest = PageRequest.parse(filter);
            return ok(page(readSingleEditItems(principal.getOrgId(), id, pageRequest)));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    /** Sampling counterpart of the result-run item selector. */
    @PostMapping(path = "/secure/emc/module/bp/sample/samples/select-item/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> selectSampleItem(HttpServletRequest request,
            @PathVariable("id") String id,
            @org.springframework.web.bind.annotation.RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            if (id == null || !id.matches("[0-9]+")) throw new IllegalArgumentException("order task id is invalid");
            PageRequest pageRequest = PageRequest.parse(object(body));
            return ok(page(readSampleItems(principal.getOrgId(), id, pageRequest)));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
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
            if (uri.endsWith("/analysis-pretrial/queries")) {
                return ok(page(readAnalysisPretrial(principal.getOrgId(), filter, pageRequest)));
            }
            if (uri.endsWith("/queries/run-result")) {
                return ok(page(readRunResult(principal.getOrgId(), filter, pageRequest)));
            }
            if (uri.endsWith("/browse-record/queries")) return ok(JSON.toJSONString(readBrowseRecord(principal.getOrgId(), filter), SerializerFeature.WriteMapNullValue));
            if (uri.endsWith("/edited-items/queries")) {
                return ok(page(readEditedItems(principal.getOrgId(), filter, pageRequest)));
            }
            if (uri.endsWith("/unique-order-item-group/queries")) return ok(page(readUniqueOrderItemGroups(principal.getOrgId(), filter, pageRequest)));
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

    private QueryResult readUniqueOrderItemGroups(String organization, JSONObject filter, PageRequest page) throws SQLException {
        List<Object> values = new ArrayList<Object>();
        StringBuilder source = new StringBuilder("SELECT G.* FROM T_EMC_ORDER_ITEM_GROUP G WHERE G.CREATEDBYORGID=? AND EXISTS (SELECT 1 FROM T_EMC_ORDER_TASK T WHERE T.ORDERID=G.ORDERID AND T.TESTMETHODID=G.TESTMETHODID AND T.ANALYSISORGID=?");
        values.add(organization); values.add(organization);
        appendLongFilter(source, values, "T.ORDERID", queryValue(filter, "orderId"));
        appendLongFilter(source, values, "T.TESTMETHODID", queryValue(filter, "testMethodId"));
        source.append(")");
        return readPaged("SELECT DISTINCT Q.* FROM (" + source + ") Q", values, page, UNIQUE_ORDER_ITEM_GROUP_TYPE_PROFILE);
    }

    /**
     * Returns the enabled analysis items available for the selected order tasks.
     * The supplier resource derives this list through order-task, order-item-group,
     * capacity-item-group and group-item relations, and marks items that already
     * have a task result with ext$.selected=yes.  Keep the same relation chain
     * here so this endpoint does not become a synthetic empty page.
     */
    private QueryResult readEditedItems(String organization, JSONObject filter, PageRequest page) throws SQLException {
        List<String> ids = numericIds(queryValue(filter, "id_IN"));
        if (ids.isEmpty()) return new QueryResult(new ArrayList<Map<String, Object>>(), ITEM_TYPE_PROFILE, 0, page.number, page.size);
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) placeholders.append(',');
            placeholders.append('?');
        }
        String source = "SELECT I.*, TM.METHODID AS ITEM_METHODID, TM.TESTID AS ITEM_TESTID, "
                + "TM.ISLOCKED AS ITEM_ISLOCKED, T.TESTNAME AS ITEM_TESTNAME, "
                + "M.METHODNAME AS ITEM_METHODNAME, SS.SETNAME AS ITEM_SETNAME, "
                + "CASE WHEN EXISTS (SELECT 1 FROM T_EMC_ORDER_TASK_RESULT R "
                + "JOIN T_EMC_ORDER_TASK RT ON RT.ID=R.ORDERTASKID WHERE RT.ID IN (" + placeholders + ") "
                + "AND R.ITEMID=I.ID) THEN '1' ELSE NULL END AS SELECTED_FLAG "
                + "FROM T_EMC_ITEM I LEFT JOIN T_EMC_TEST_METHOD TM ON TM.ID=I.TESTMETHODID "
                + "LEFT JOIN T_EMC_TEST T ON T.ID=TM.TESTID "
                + "LEFT JOIN T_EMC_METHOD M ON M.ID=TM.METHODID "
                + "LEFT JOIN T_EMC_STANDARD_SET SS ON SS.ID=I.STANDSETID "
                + "WHERE I.ANALYSISCATEGORY='analysis' AND I.ACTIVATEDFLAG='1' "
                + "AND EXISTS (SELECT 1 FROM T_EMC_ORDER_TASK OT "
                + "JOIN T_EMC_ORDER_ITEM_GROUP OIG ON OIG.ORDERID=OT.ORDERID AND OIG.TESTID=OT.TESTID "
                + "AND OIG.TESTMETHODID=OT.TESTMETHODID AND (OT.MONITORTMPLID IS NULL OR OIG.MONITORTMPLID=OT.MONITORTMPLID) "
                + "JOIN T_EMC_CAPACITY_ITEM_GROUP CIG ON CIG.ID=OIG.ITEMGROUPID "
                + "JOIN T_EMC_GROUP_ITEM GI ON GI.ITEMGROUPID=CIG.ITEMGROUPID "
                + "WHERE OT.ANALYSISORGID=? AND OT.ID IN (" + placeholders + ") AND GI.ITEMID=I.ID)";
        List<Object> values = new ArrayList<Object>();
        values.addAll(ids); values.add(organization); values.addAll(ids);
        return readPagedEditedItems(source, values, page);
    }

    private QueryResult readSampleItems(String organization, String orderTaskId, PageRequest page) throws SQLException {
        String source = "SELECT I.*, TM.METHODID AS ITEM_METHODID, TM.TESTID AS ITEM_TESTID, "
                + "TM.ISLOCKED AS ITEM_ISLOCKED, T.TESTNAME AS ITEM_TESTNAME, "
                + "M.METHODNAME AS ITEM_METHODNAME, SS.SETNAME AS ITEM_SETNAME "
                + "FROM T_EMC_ITEM I LEFT JOIN T_EMC_TEST_METHOD TM ON TM.ID=I.TESTMETHODID "
                + "LEFT JOIN T_EMC_TEST T ON T.ID=TM.TESTID "
                + "LEFT JOIN T_EMC_METHOD M ON M.ID=TM.METHODID "
                + "LEFT JOIN T_EMC_STANDARD_SET SS ON SS.ID=I.STANDSETID "
                + "WHERE I.ANALYSISCATEGORY='sampling' AND I.ACTIVATEDFLAG='1' "
                + "AND EXISTS (SELECT 1 FROM T_EMC_ORDER_TASK OT "
                + "JOIN T_EMC_ORDER_ITEM_GROUP OIG ON OIG.SCHEMENAME=OT.SCHEMENAME "
                + "AND OIG.TESTID=OT.TESTID AND OIG.TESTMETHODID=OT.TESTMETHODID "
                + "AND (OT.MONITORTMPLID IS NULL OR OIG.MONITORTMPLID=OT.MONITORTMPLID) "
                + "JOIN T_EMC_CAPACITY_ITEM_GROUP CIG ON CIG.ID=OIG.ITEMGROUPID "
                + "JOIN T_EMC_GROUP_ITEM GI ON GI.ITEMGROUPID=CIG.ITEMGROUPID "
                + "WHERE OT.ID=? AND OT.ANALYSISORGID=? AND GI.ITEMID=I.ID)";
        List<Object> values = new ArrayList<Object>(); values.add(orderTaskId); values.add(organization);
        return readPagedEditedItems(source, values, page);
    }

    private QueryResult readPagedEditedItems(String source, List<Object> values, PageRequest page) throws SQLException {
        String countSql = "SELECT COUNT(1) FROM (" + source + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID ASC) PAGE_RN_ FROM ("
                + source + ") Q) P WHERE P.PAGE_RN_>? AND P.PAGE_RN_<=? ORDER BY P.PAGE_RN_";
        int total; List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement count = connection.prepareStatement(countSql)) {
                bind(count, values, 1);
                try (ResultSet result = count.executeQuery()) {
                    if (!result.next()) throw new SQLException("edited item query count unavailable");
                    total = result.getInt(1);
                }
            }
            try (PreparedStatement select = connection.prepareStatement(pageSql)) {
                int next = bind(select, values, 1);
                select.setInt(next++, page.start()); select.setInt(next, page.end());
                try (ResultSet result = select.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    while (result.next()) rows.add(editedItemRow(result, metadata));
                }
            }
        }
        return new QueryResult(rows, ITEM_TYPE_PROFILE, total, page.number, page.size);
    }

    /** EmcRunService.selectEditItem uses the scheme-name item-group relation. */
    private QueryResult readSingleEditItems(String organization, String taskId, PageRequest page) throws SQLException {
        String source = "SELECT I.*, TM.METHODID AS ITEM_METHODID, TM.TESTID AS ITEM_TESTID, "
                + "TM.ISLOCKED AS ITEM_ISLOCKED, T.TESTNAME AS ITEM_TESTNAME, "
                + "M.METHODNAME AS ITEM_METHODNAME, SS.SETNAME AS ITEM_SETNAME "
                + "FROM T_EMC_ORDER_TASK OT JOIN T_EMC_ORDER_ITEM_GROUP OIG ON OIG.SCHEMENAME=OT.SCHEMENAME "
                + "AND OIG.TESTID=OT.TESTID AND OIG.TESTMETHODID=OT.TESTMETHODID "
                + "AND (OT.MONITORTMPLID IS NULL OR OIG.MONITORTMPLID=OT.MONITORTMPLID) "
                + "JOIN T_EMC_GROUP_ITEM GI ON GI.ITEMGROUPID=OIG.ITEMGROUPID "
                + "JOIN T_EMC_ITEM I ON I.ID=GI.ITEMID "
                + "LEFT JOIN T_EMC_TEST_METHOD TM ON TM.ID=I.TESTMETHODID "
                + "LEFT JOIN T_EMC_TEST T ON T.ID=TM.TESTID "
                + "LEFT JOIN T_EMC_METHOD M ON M.ID=TM.METHODID "
                + "LEFT JOIN T_EMC_STANDARD_SET SS ON SS.ID=I.STANDSETID "
                + "WHERE OT.ID=? AND OT.ANALYSISORGID=? AND I.ANALYSISCATEGORY='analysis' AND I.ACTIVATEDFLAG='1'";
        List<Object> values = new ArrayList<Object>(); values.add(taskId); values.add(organization);
        return readPagedEditedItems(source, values, page);
    }

    private static Map<String, Object> editedItemRow(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        Map<String, Object> extension = new LinkedHashMap<String, Object>();
        String selected = null;
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i);
            if ("RN_".equalsIgnoreCase(label) || "PAGE_RN_".equalsIgnoreCase(label)) continue;
            if ("SELECTED_FLAG".equalsIgnoreCase(label)) { selected = result.getString(i); continue; }
            if ("ITEM_TESTNAME".equalsIgnoreCase(label) || "ITEM_METHODNAME".equalsIgnoreCase(label)
                    || "ITEM_SETNAME".equalsIgnoreCase(label) || "ITEM_ISLOCKED".equalsIgnoreCase(label)
                    || "ITEM_METHODID".equalsIgnoreCase(label) || "ITEM_TESTID".equalsIgnoreCase(label)) {
                String key = label.substring("ITEM_".length()).toLowerCase();
                extension.put(key, JdbcValue.normalize(result.getObject(i)));
                continue;
            }
            value.put(friendly(label), JdbcValue.normalize(result.getObject(i)));
        }
        if (selected != null && selected.length() > 0) extension.put("selected", selected);
        value.put("ext$", extension);
        return value;
    }

    private static List<String> numericIds(Object raw) {
        List<String> result = new ArrayList<String>();
        if (raw == null) return result;
        String value = String.valueOf(raw);
        for (String token : value.split(",")) {
            String id = token.trim();
            if (id.length() == 0) continue;
            if (!id.matches("[0-9]+")) throw new IllegalArgumentException("invalid order task id");
            result.add(id);
        }
        return result;
    }

    /**
     * Analysis pretrial is the audit queue used by the original run resource.
     * It is intentionally kept as a read-only query seam: only runs that have
     * an audit/qc-audit order task in the caller's organization are returned.
     */
    private QueryResult readAnalysisPretrial(String organization, JSONObject filter, PageRequest page) throws SQLException {
        List<Object> values = new ArrayList<Object>();
        StringBuilder source = new StringBuilder(runSelectSql());
        source.append(" WHERE A.ORGID=? AND EXISTS (SELECT 1 FROM T_EMC_ORDER_TASK OT WHERE OT.RUNID=A.ID AND OT.STATUS IN ('audit','qcAudit')");
        values.add(organization);
        appendLongFilter(source, values, "OT.PROJID", queryValue(filter, "projId"));
        appendLongFilter(source, values, "OT.MONITORTMPLID", queryValue(filter, "monitorTmplId"));
        source.append(")");
        appendStringFilter(source, values, "A.STATUS", queryValue(filter, "status"));
        return readPaged(source.toString(), values, page, RUN_TYPE_PROFILE);
    }

    /** Run-result is the same owned projection with the result queue filters. */
    private QueryResult readRunResult(String organization, JSONObject filter, PageRequest page) throws SQLException {
        List<Object> values = new ArrayList<Object>();
        StringBuilder source = new StringBuilder(runSelectSql());
        source.append(" WHERE A.ORGID=?");
        values.add(organization);
        appendStringFilter(source, values, "A.STATUS", queryValue(filter, "status"));
        appendStringFilter(source, values, "A.ID", queryValue(filter, "runId"));
        appendLongFilter(source, values, "A.PROJID", queryValue(filter, "projId"));
        return readPaged(source.toString(), values, page, RUN_TYPE_PROFILE);
    }

    private Map<String, Object> readBrowseRecord(String organization, JSONObject filter) throws SQLException {
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("analysisRecord", null);
        body.put("otherRecordList", new ArrayList<Map<String, Object>>());
        body.put("ext$", new LinkedHashMap<String, Object>());
        String runId = text(queryValue(filter, "runId"));
        if (runId.length() == 0) return body;
        if (!runId.matches("[A-Za-z0-9_-]+")) throw new IllegalArgumentException("run id is invalid");
        String sql = "SELECT F.* FROM T_CORE_FILE F WHERE F.TARGETID IN (?,?) ORDER BY F.ID";
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement guard = connection.prepareStatement("SELECT COUNT(1) FROM T_EMC_RUN WHERE ID=? AND ORGID=?")) {
                guard.setString(1, runId); guard.setString(2, organization);
                try (ResultSet result = guard.executeQuery()) {
                    if (!result.next() || result.getInt(1) == 0) return body;
                }
            }
            List<Map<String, Object>> files = new ArrayList<Map<String, Object>>();
            try (PreparedStatement query = connection.prepareStatement(sql)) {
                query.setString(1, "T_EMC_RUN$" + runId); query.setString(2, "T_EMC_EQUIPT$" + runId);
                try (ResultSet result = query.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    while (result.next()) files.add(row(result, metadata));
                }
            }
            Map<String, Object> analysis = null; List<Map<String, Object>> others = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> file : files) {
                if (analysis == null && "composePdf".equals(String.valueOf(file.get("ext")))) analysis = file;
                else others.add(file);
            }
            body.put("analysisRecord", analysis); body.put("otherRecordList", others);
        }
        return body;
    }

    private static String runSelectSql() {
        return "SELECT A.*,D.EQUIPTNAME,D.EQUIPTID AS EQUIPTCODE,M.MAXELNTMPLID,M.METHODNAME,"
                + "S1.SOLNAME AS SOL1NAME,S2.SOLNAME AS SOL2NAME,S1.SOLNO AS SOL1NO,S2.SOLNO AS SOL2NO,"
                + "R.REAGENTNAME FROM T_EMC_RUN A LEFT JOIN T_EMC_EQUIPT D ON A.EQUIPTID=D.ID "
                + "LEFT JOIN T_EMC_METHOD M ON A.METHODID=M.ID LEFT JOIN T_EMC_SOL S1 ON A.SOL1ID=S1.ID "
                + "LEFT JOIN T_EMC_SOL S2 ON A.SOL2ID=S2.ID LEFT JOIN T_EMC_REAGENT R ON A.REAGENTID=R.ID";
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

    private static final Map<String, String> UNIQUE_ORDER_ITEM_GROUP_TYPE_PROFILE = uniqueOrderItemGroupTypeProfile();
    private static Map<String, String> uniqueOrderItemGroupTypeProfile() {
        String encoded = "id=number;orderid=number;schemename=string;testid=number;testname=string;testmethodid=number;methodid=number;methodname=string;itemgroupid=number;itemgroup=string;analysisorgid=string;analysisorgname=string;qccategoryid=number;qccategorycode=string;qcname=string;subpackageno=string;containercategoryid=number;containercategorycode=string;containercategoryname=string;ordercategory=string;createdbyid=string;createdbyname=string;createdtime=dateTime;createdbyorgid=string;createdbyorgname=string;sortnum=number";
        Map<String, String> result = new LinkedHashMap<String, String>();
        for (String token : encoded.split(";")) { int equals = token.indexOf('='); if (equals > 0) result.put(token.substring(0, equals), token.substring(equals + 1)); }
        return result;
    }

    private static final Map<String, String> ITEM_TYPE_PROFILE = itemTypeProfile();
    private static Map<String, String> itemTypeProfile() {
        String encoded = "id=number;testmethodid=number;itemname=string;itemaliasname=string;analysiscategory=string;"
                + "activatedflag=string;unitname=string;resultcategory=string;reportdisplay=string;originalrecorddisplay=string;"
                + "orderno=number;selected=string";
        Map<String, String> result = new LinkedHashMap<String, String>();
        for (String token : encoded.split(";")) {
            int equals = token.indexOf('=');
            if (equals > 0) result.put(token.substring(0, equals), token.substring(equals + 1));
        }
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
