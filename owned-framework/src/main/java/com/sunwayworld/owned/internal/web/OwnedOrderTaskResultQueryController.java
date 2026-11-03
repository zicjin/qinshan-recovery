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
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Owned read seam for the inherited order-task-result GenericResource.
 *
 * <p>The project resource joins the result row with the order, task, project,
 * folder, run and equipment projections.  Keeping that projection in one
 * controller lets all inherited aliases share the same organization seam and
 * Spring application; it does not create a compatibility process.</p>
 */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedOrderTaskResultQueryController {
    private static final String PREFIX = "/secure/emc/module/bp/bp/order-task-results";
    private static final String ORDER_TASK_PREFIX = "/secure/emc/module/bp/bp/order-tasks";
    private static final String BASE_SQL =
            "SELECT A.*, O.SAMPLENUM,O.ORDERDESC,O.REMARK,O.SAMPLEID,O.SAMPLEBEGINDATE,O.SAMPLEBEGINTIME,"
            + "O.SAMPLECOMPLETEDATE,O.SAMPLECOMPLETETIME,O.LABELNO,"
            + "M.STATUS AS ORDERTASKSTATUS,M.MONITORTMPLID,M.ORDERCATEGORY,M.ANALYSISDATE,M.TESTNAME,"
            + "M.METHODNAME,M.TESTMETHODBASIS,M.INDEXID,M.CUPNO,M.EQUIPTACQUNO,M.STOCKBATCHNO,"
            + "N.INDEXNAME,F.PLANSAMPLEDATE,F.FOLDERNO,F.MONITORPOINTID,F.MONITORPOINTNO,F.MONITORPOINTNAME,"
            + "F.SAMPLEUSERNAME,P.TESTORGNAME,P.ENTRUSTORGNAME,P.PROJNO,P.PROJNAME,P.BIZCATEGORYCODE,P.REGISTDATE,"
            + "R.ANALYSISUSERNAME,R.CHECKUSERNAME,R.ANALYSISBEGINDATE,E.EQUIPTNAME,E.EQUIPTID AS EQUIPTNO,C.SETNAME "
            + "FROM T_EMC_ORDER_TASK_RESULT A "
            + "LEFT JOIN T_EMC_ORDER O ON O.ID=A.ORDERID "
            + "LEFT JOIN T_EMC_ORDER_TASK M ON M.ID=A.ORDERTASKID "
            + "LEFT JOIN T_EMC_ORDER_INDEX N ON N.ID=M.INDEXID "
            + "LEFT JOIN T_EMC_FOLDER F ON F.ID=A.FOLDERID "
            + "LEFT JOIN T_EMC_PROJECT P ON P.ID=A.PROJID "
            + "LEFT JOIN T_EMC_RUN R ON R.ID=A.RUNID "
            + "LEFT JOIN T_EMC_EQUIPT E ON E.ID=R.EQUIPTID "
            + "LEFT JOIN T_EMC_ITEM B ON B.ID=A.ITEMID "
            + "LEFT JOIN T_EMC_STANDARD_SET C ON C.ID=B.STANDSETID";

    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedOrderTaskResultQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("order-task-result dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @PostMapping(path = {
            PREFIX + "/queries", PREFIX + "/queries/raw", PREFIX + "/queries/searchable"
            }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireRead(request);
            return ok(selectPage(principal.getOrgId(), Page.parse(body), request.getRequestURI()));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    /** Read result rows for one order task through the shared result projection. */
    @PostMapping(path = {
            ORDER_TASK_PREFIX + "/{id}/results/queries",
            ORDER_TASK_PREFIX + "/{id}/results/distinct/queries"
            }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> taskResults(HttpServletRequest request,
            @org.springframework.web.bind.annotation.PathVariable("id") String id,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireRead(request);
            if (id == null || !id.matches("[0-9]{1,30}")) return badRequest();
            Page page = Page.parse(body);
            boolean distinct = request.getRequestURI().endsWith("/results/distinct/queries");
            return ok(selectTaskResultPage(principal.getOrgId(), Long.parseLong(id), page, distinct));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    /**
     * Read-only data-query aliases used by the original result workspace.
     * They are backed by the same result projection and organization seam as
     * the inherited resource; no second service or bridge is introduced.
     */
    @PostMapping(path = PREFIX + "/data-query-titles/queries",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> dataQueryTitles(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireRead(request);
            List<Map<String, Object>> source = selectResultRows(principal.getOrgId());
            Map<String, String> titles = new LinkedHashMap<String, String>();
            for (Map<String, Object> item : source) {
                String colId = dataColumnId(item);
                if (!titles.containsKey(colId)) {
                    Object alias = item.get("itemAliasName");
                    titles.put(colId, "<font color=\"blue\">" + (alias == null ? "" : alias) + "</font>");
                }
            }
            List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
            for (Map.Entry<String, String> title : titles.entrySet()) {
                Map<String, Object> head = new LinkedHashMap<String, Object>();
                head.put("field", title.getKey());
                head.put("title", title.getValue());
                result.add(head);
            }
            return ok(JSON.toJSONString(result, SerializerFeature.WriteMapNullValue));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = PREFIX + "/data-query-rows/queries",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> dataQueryRows(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireRead(request);
            List<Map<String, Object>> source = selectResultRows(principal.getOrgId());
            Map<String, Map<String, Object>> grouped = new LinkedHashMap<String, Map<String, Object>>();
            Map<String, String> columnTypes = new LinkedHashMap<String, String>();
            for (Map<String, Object> item : source) {
                String rowNo = String.valueOf(item.get("orderNo"));
                Map<String, Object> row = grouped.get(rowNo);
                if (row == null) {
                    row = new LinkedHashMap<String, Object>();
                    row.put("rowNo", rowNo);
                    row.put("orderNo", item.get("orderNo"));
                    row.put("projNo", item.get("projNo"));
                    row.put("projName", item.get("projName"));
                    row.put("testOrgName", item.get("testOrgName"));
                    row.put("monitorPointName", item.get("monitorPointName"));
                    row.put("sampleBeginDate", item.get("sampleBeginDate"));
                    grouped.put(rowNo, row);
                }
                String colId = dataColumnId(item);
                row.put(colId, item.get("reportResult"));
                columnTypes.put(colId, "string");
            }
            List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>(grouped.values());
            Map<String, Object> envelope = new LinkedHashMap<String, Object>();
            envelope.put("columnTypeMap", columnTypes);
            envelope.put("first", true);
            envelope.put("last", true);
            envelope.put("numberOfElements", rows.size());
            envelope.put("pageNumber", 1);
            envelope.put("pageSize", rows.isEmpty() ? 0 : rows.size());
            envelope.put("rows", rows);
            envelope.put("total", rows.size());
            envelope.put("totalMap", new LinkedHashMap<String, Object>());
            envelope.put("totalPages", rows.isEmpty() ? 0 : 1);
            return ok(JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    /** Read-only QC-name selector used by the original result-input page. */
    @PostMapping(path = PREFIX + "/qcnames/queries",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> qcNames(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireRead(request);
            JSONObject root = body == null || body.trim().isEmpty() ? new JSONObject() : JSON.parseObject(body);
            String sampleId = root == null ? null : root.getString("sampleId");
            if ((sampleId == null || sampleId.trim().isEmpty()) && root != null) {
                JSONObject params = root.getJSONObject("param");
                if (params != null) sampleId = params.getString("sampleId");
            }
            String sql = "SELECT DISTINCT A.QCNAME FROM T_EMC_ORDER_TASK_RESULT A "
                    + "LEFT JOIN T_EMC_ORDER O ON O.ID=A.ORDERID "
                    + "WHERE A.CREATEDBYORGID=? AND A.QCNAME IS NOT NULL AND A.QCNAME<>?";
            if (sampleId != null && !sampleId.trim().isEmpty()) sql += " AND O.SAMPLEID=?";
            List<String> names = new ArrayList<String>();
            try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql)) {
                int index = 1;
                query.setString(index++, principal.getOrgId());
                query.setString(index++, "N/A");
                if (sampleId != null && !sampleId.trim().isEmpty()) query.setString(index, sampleId.trim());
                try (ResultSet result = query.executeQuery()) {
                    while (result.next()) names.add(result.getString(1));
                }
            }
            return ok(JSON.toJSONString(names));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    /**
     * Read-only data-summary aliases used by the result workspace.  The
     * supplier currently raises its framework unexpected-error envelope when
     * the result projection has no rows; preserve that explicit negative
     * contract instead of returning a fabricated empty success page.  When
     * rows exist, the same dynamic-column projection as data-query is used.
     */
    @PostMapping(path = PREFIX + "/data-titles/queries",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> dataSummaryTitles(HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireRead(request);
            List<Map<String, Object>> source = selectResultRows(principal.getOrgId());
            if (source.isEmpty()) return unexpected(response, request.getRequestURI());
            Map<String, String> titles = new LinkedHashMap<String, String>();
            for (Map<String, Object> item : source) {
                String colId = dataColumnId(item);
                if (!titles.containsKey(colId)) {
                    Object alias = item.get("itemAliasName");
                    titles.put(colId, "<font color=\"blue\">" + (alias == null ? "" : alias) + "</font>");
                }
            }
            List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
            for (Map.Entry<String, String> title : titles.entrySet()) {
                Map<String, Object> head = new LinkedHashMap<String, Object>();
                head.put("field", title.getKey());
                head.put("title", title.getValue());
                result.add(head);
            }
            return ok(JSON.toJSONString(result, SerializerFeature.WriteMapNullValue));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = PREFIX + "/data-rows/queries",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> dataSummaryRows(HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireRead(request);
            List<Map<String, Object>> source = selectResultRows(principal.getOrgId());
            if (source.isEmpty()) return unexpected(response, request.getRequestURI());
            Map<String, Map<String, Object>> grouped = new LinkedHashMap<String, Map<String, Object>>();
            Map<String, String> columnTypes = new LinkedHashMap<String, String>();
            for (Map<String, Object> item : source) {
                String rowNo = String.valueOf(item.get("orderNo"));
                Map<String, Object> row = grouped.get(rowNo);
                if (row == null) {
                    row = new LinkedHashMap<String, Object>();
                    row.put("rowNo", rowNo);
                    row.put("orderNo", item.get("orderNo"));
                    row.put("projName", item.get("projName"));
                    row.put("monitorPointName", item.get("monitorPointName"));
                    row.put("folderNo", item.get("folderNo"));
                    row.put("monitorPointNo", item.get("monitorPointNo"));
                    grouped.put(rowNo, row);
                }
                String colId = dataColumnId(item);
                row.put(colId, item.get("reportResult"));
                columnTypes.put(colId, "string");
            }
            List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>(grouped.values());
            Map<String, Object> envelope = new LinkedHashMap<String, Object>();
            envelope.put("columnTypeMap", columnTypes);
            envelope.put("first", true);
            envelope.put("last", true);
            envelope.put("numberOfElements", rows.size());
            envelope.put("pageNumber", 1);
            envelope.put("pageSize", rows.size());
            envelope.put("rows", rows);
            envelope.put("total", rows.size());
            envelope.put("totalMap", new LinkedHashMap<String, Object>());
            envelope.put("totalPages", 1);
            return ok(JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    /** Read-only preprocessing table headers used by the result-input page. */
    @PostMapping(path = PREFIX + "/preprocessing-result/table-heads/queries",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> preprocessingTitles(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireRead(request);
            List<Map<String, Object>> source = selectResultRows(principal.getOrgId());
            if (source.isEmpty()) return ok("[]");
            Map<String, String> titles = new LinkedHashMap<String, String>();
            for (Map<String, Object> item : source) {
                String colId = dataColumnId(item);
                if (!titles.containsKey(colId)) {
                    Object alias = item.get("itemAliasName");
                    titles.put(colId, "<font color=\"blue\">" + (alias == null ? "" : alias) + "</font>");
                }
            }
            List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
            for (Map.Entry<String, String> title : titles.entrySet()) {
                Map<String, Object> head = new LinkedHashMap<String, Object>();
                head.put("field", title.getKey());
                head.put("title", title.getValue());
                result.add(head);
            }
            return ok(JSON.toJSONString(result, SerializerFeature.WriteMapNullValue));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    /** Read-only preprocessing table rows used by the result-input page. */
    @PostMapping(path = PREFIX + "/preprocessing-result/table-rows/queries",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> preprocessingRows(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireRead(request);
            List<Map<String, Object>> source = selectResultRows(principal.getOrgId());
            if (source.isEmpty()) return ok("[]");
            Map<String, Map<String, Object>> grouped = new LinkedHashMap<String, Map<String, Object>>();
            for (Map<String, Object> item : source) {
                String rowNo = String.valueOf(item.get("orderNo"));
                Map<String, Object> row = grouped.get(rowNo);
                if (row == null) {
                    row = new LinkedHashMap<String, Object>();
                    row.put("rowNo", rowNo);
                    row.put("orderNo", item.get("orderNo"));
                    row.put("projNo", item.get("projNo"));
                    row.put("projName", item.get("projName"));
                    row.put("testOrgName", item.get("testOrgName"));
                    row.put("monitorPointName", item.get("monitorPointName"));
                    row.put("sampleBeginDate", item.get("sampleBeginDate"));
                    grouped.put(rowNo, row);
                }
                row.put(dataColumnId(item), item.get("reportResult"));
            }
            return ok(JSON.toJSONString(new ArrayList<Map<String, Object>>(grouped.values()),
                    SerializerFeature.WriteMapNullValue));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    /**
     * Gas-parameter result tables share the result projection and organization
     * boundary.  The supplier emits its framework unexpected envelope when
     * the clone has no result rows; preserve that observable negative contract
     * while keeping a real dynamic projection for populated fixtures.
     */
    @PostMapping(path = {
            PREFIX + "/gas-parameter/table-heads/queries",
            PREFIX + "/gas-parameter-result/table-heads/queries",
            PREFIX + "/gas-parameter/table-rows/queries",
            PREFIX + "/gas-parameter-result/table-rows/queries"
            }, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> gasParameterTable(HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireRead(request);
            List<Map<String, Object>> source = selectResultRows(principal.getOrgId());
            if (source.isEmpty()) return gasUnexpected(response, request.getRequestURI());
            boolean rowsRequest = request.getRequestURI().endsWith("table-rows/queries");
            if (!rowsRequest) return dynamicTitles(source);
            return dynamicRows(source);
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private static ResponseEntity<String> dynamicTitles(List<Map<String, Object>> source) {
        Map<String, String> titles = new LinkedHashMap<String, String>();
        for (Map<String, Object> item : source) {
            String colId = dataColumnId(item);
            if (!titles.containsKey(colId)) {
                Object alias = item.get("itemAliasName");
                titles.put(colId, "<font color=\"blue\">" + (alias == null ? "" : alias) + "</font>");
            }
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (Map.Entry<String, String> title : titles.entrySet()) {
            Map<String, Object> head = new LinkedHashMap<String, Object>();
            head.put("field", title.getKey()); head.put("title", title.getValue()); result.add(head);
        }
        return ok(JSON.toJSONString(result, SerializerFeature.WriteMapNullValue));
    }

    private static ResponseEntity<String> dynamicRows(List<Map<String, Object>> source) {
        Map<String, Map<String, Object>> grouped = new LinkedHashMap<String, Map<String, Object>>();
        Map<String, String> types = new LinkedHashMap<String, String>();
        for (Map<String, Object> item : source) {
            String rowNo = String.valueOf(item.get("orderNo"));
            Map<String, Object> row = grouped.get(rowNo);
            if (row == null) {
                row = new LinkedHashMap<String, Object>(); row.put("rowNo", rowNo);
                row.put("orderNo", item.get("orderNo")); row.put("projNo", item.get("projNo"));
                row.put("projName", item.get("projName")); row.put("testOrgName", item.get("testOrgName"));
                row.put("monitorPointName", item.get("monitorPointName")); grouped.put(rowNo, row);
            }
            String colId = dataColumnId(item); row.put(colId, item.get("reportResult")); types.put(colId, "string");
        }
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>(grouped.values());
        Map<String, Object> envelope = new LinkedHashMap<String, Object>(); envelope.put("columnTypeMap", types);
        envelope.put("first", true); envelope.put("last", true); envelope.put("numberOfElements", rows.size());
        envelope.put("pageNumber", 1); envelope.put("pageSize", rows.size()); envelope.put("rows", rows);
        envelope.put("total", rows.size()); envelope.put("totalMap", new LinkedHashMap<String, Object>());
        envelope.put("totalPages", rows.isEmpty() ? 0 : 1);
        return ok(JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue));
    }

    /** Result-input uses item-name hash columns and original/report value rules. */
    private static ResponseEntity<String> resultInputTitles(List<Map<String, Object>> source) {
        Map<String, Map<String, Object>> unique = new LinkedHashMap<String, Map<String, Object>>();
        for (Map<String, Object> item : source) {
            String column = inputColumnId(item);
            if (!unique.containsKey(column)) unique.put(column, item);
        }
        List<Map<String, Object>> heads = new ArrayList<Map<String, Object>>();
        for (Map.Entry<String, Map<String, Object>> entry : unique.entrySet()) {
            Map<String, Object> item = entry.getValue();
            Map<String, Object> head = new LinkedHashMap<String, Object>();
            head.put("field", entry.getKey());
            String alias = item.get("itemAliasName") == null ? text(item.get("itemName")) : text(item.get("itemAliasName"));
            String unit = text(item.get("unitName"));
            head.put("title", alias + "<br>" + (unit.length() == 0 ? "" : "(" + unit + ")"));
            Map<String, Object> ext = new LinkedHashMap<String, Object>(); ext.put("script", item.get("scriptFlag"));
            head.put("ext$", ext); heads.add(head);
        }
        return ok(JSON.toJSONString(heads, SerializerFeature.WriteMapNullValue));
    }

    private static ResponseEntity<String> resultInputRows(List<Map<String, Object>> source) {
        Map<String, Map<String, Object>> grouped = new LinkedHashMap<String, Map<String, Object>>();
        for (Map<String, Object> item : source) {
            String rowNo = text(item.get("orderNo"));
            Map<String, Object> row = grouped.get(rowNo);
            if (row == null) {
                row = new LinkedHashMap<String, Object>(); row.put("rowNo", rowNo); row.put("orderNo", item.get("orderNo"));
                row.put("monitorPointName", item.get("monitorPointName")); row.put("qcName", item.get("qcName"));
                row.put("cupNo", item.get("cupNo")); row.put("id", item.get("orderTaskId"));
                row.put("orderId", item.get("orderId")); row.put("folderId", item.get("folderId"));
                row.put("projNo", item.get("projNo")); row.put("sampleBeginDate", item.get("sampleBeginDate"));
                grouped.put(rowNo, row);
            }
            String value = isScripted(item) ? text(item.get("reportResult")) : text(item.get("originalResult"));
            String column = inputColumnId(item); row.put(column, value);
            Map<String, Object> ext = new LinkedHashMap<String, Object>();
            ext.put("scriptFlag", item.get("scriptFlag")); ext.put("submitFlag", item.get("submitFlag"));
            ext.put("resultCategory", item.get("resultCategory")); row.put("ext$" + column, ext);
        }
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>(grouped.values());
        Map<String, Object> envelope = new LinkedHashMap<String, Object>(); envelope.put("rows", rows);
        envelope.put("numberOfElements", rows.size()); envelope.put("pageNumber", 1); envelope.put("pageSize", rows.size());
        envelope.put("total", rows.size()); envelope.put("first", true); envelope.put("last", true);
        envelope.put("totalPages", rows.isEmpty() ? 0 : 1); envelope.put("totalMap", new LinkedHashMap<String, Object>());
        return ok(JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue));
    }

    private List<Map<String, Object>> selectResultInputRows(String organization, String body) throws SQLException {
        String runId = extractRunId(body);
        String sql = BASE_SQL + " WHERE A.CREATEDBYORGID=? AND M.STATUS='logged'";
        if (runId != null) sql += " AND A.RUNID=?";
        sql += " ORDER BY A.ID DESC";
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql)) {
            int index = 1; query.setString(index++, organization); if (runId != null) query.setString(index, runId);
            try (ResultSet result = query.executeQuery()) {
                ResultSetMetaData metadata = result.getMetaData();
                while (result.next() && rows.size() < 1000) rows.add(row(result, metadata));
            }
        }
        return rows;
    }

    private static String extractRunId(String body) {
        if (body == null || body.trim().length() == 0) return null;
        JSONObject root = JSON.parseObject(body); if (root == null) return null;
        String value = root.getString("runId"); if (value == null) value = root.getString("id");
        JSONObject page = root.getJSONObject("p"); if (value == null && page != null) value = page.getString("runId");
        if (value == null && page != null) value = page.getString("id");
        JSONObject filter = root.getJSONObject("f"); if (value == null && filter != null) value = filter.getString("runId");
        if (value == null && filter != null) value = filter.getString("id");
        return value == null || value.trim().length() == 0 ? null : value.trim();
    }

    private static String inputColumnId(Map<String, Object> item) { return "$" + text(item.get("itemName")).hashCode(); }
    private static boolean isScripted(Map<String, Object> item) {
        String value = text(item.get("scriptFlag"));
        return "1".equals(value) || "Y".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value);
    }
    private static String text(Object value) { return value == null ? "" : String.valueOf(value); }

    /** Quick/result input tables use the same dynamic result projection. */
    @PostMapping(path = {
            PREFIX + "/quick-inputs/table-heads/queries",
            PREFIX + "/quick-inputs/table-rows/queries",
            PREFIX + "/quick-inputs-sample/table-heads/queries",
            PREFIX + "/quick-inputs-sample/table-rows/queries",
            PREFIX + "/result-inputs/table-heads/queries",
            PREFIX + "/result-inputs/table-rows/queries"
            }, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> inputTable(HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireRead(request);
            String path = request.getRequestURI();
            boolean resultInput = path.contains("/result-inputs/");
            List<Map<String, Object>> source = resultInput
                    ? selectResultInputRows(principal.getOrgId(), body)
                    : selectResultRows(principal.getOrgId());
            boolean sample = path.contains("quick-inputs-sample");
            if (source.isEmpty()) {
                return sample ? ok("[]") : gasUnexpected(response, path);
            }
            if (path.endsWith("table-heads/queries")) {
                return resultInput ? resultInputTitles(source) : dynamicTitles(source);
            }
            return resultInput ? resultInputRows(source) : dynamicRows(source);
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {PREFIX + "/info/status", PREFIX + "/{id}/info/status"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { requireRead(request); return ok("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = PREFIX + "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request,
            @org.springframework.web.bind.annotation.PathVariable("id") String id) {
        try {
            OwnedPrincipal principal = requireRead(request);
            if (id == null || !id.matches("[0-9]{1,30}")) return badRequest();
            String sql = BASE_SQL + " WHERE A.ID=? AND A.CREATEDBYORGID=?";
            try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql)) {
                query.setLong(1, Long.parseLong(id)); query.setString(2, principal.getOrgId());
                try (ResultSet result = query.executeQuery()) {
                    if (!result.next()) return ok("");
                    return ok(JSON.toJSONString(row(result, result.getMetaData()), SerializerFeature.WriteMapNullValue));
                }
            }
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = PREFIX + "/{id}/action/print", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> print(HttpServletRequest request,
            @org.springframework.web.bind.annotation.PathVariable("id") String id) {
        return unavailableIfReadAllowed(request, id);
    }

    @GetMapping(path = PREFIX + "/{id}/columns/{column}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> column(HttpServletRequest request,
            @org.springframework.web.bind.annotation.PathVariable("id") String id,
            @org.springframework.web.bind.annotation.PathVariable("column") String column) {
        return unavailableIfReadAllowed(request, id);
    }

    private ResponseEntity<String> unavailableIfReadAllowed(HttpServletRequest request, String id) {
        try {
            requireRead(request);
            if (id == null || !id.matches("[0-9]{1,30}")) return badRequest();
            return unavailable();
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal requireRead(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private String selectPage(String organization, Page page, String requestPath) throws SQLException {
        int total;
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, String> types = new LinkedHashMap<String, String>();
        String scoped = BASE_SQL + " WHERE A.CREATEDBYORGID=?";
        String countSql = "SELECT COUNT(1) FROM (" + scoped + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM ("
                + scoped + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement count = connection.prepareStatement(countSql)) {
                count.setString(1, organization);
                try (ResultSet result = count.executeQuery()) {
                    if (!result.next()) throw new SQLException("order-task-result count returned no row");
                    total = result.getInt(1);
                }
            }
            try (PreparedStatement query = connection.prepareStatement(pageSql)) {
                query.setString(1, organization);
                query.setInt(2, page.start());
                query.setInt(3, page.end());
                try (ResultSet result = query.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    for (int i = 1; i <= metadata.getColumnCount(); i++) {
                        types.put(metadata.getColumnLabel(i).toLowerCase(Locale.ROOT), type(metadata.getColumnType(i)));
                    }
                    Map<String, String> supplierTypes = RESULT_TYPE_PROFILES.get(resultProfile(requestPath));
                    if (supplierTypes != null && !supplierTypes.isEmpty()) { types.clear(); types.putAll(supplierTypes); }
                    while (result.next()) rows.add(row(result, metadata));
                }
            }
        }
        Map<String, Object> envelope = new LinkedHashMap<String, Object>();
        envelope.put("columnTypeMap", types);
        envelope.put("first", page.number == 1);
        envelope.put("last", total == 0 || page.number >= pages(total, page.size));
        envelope.put("numberOfElements", rows.size());
        envelope.put("pageNumber", page.number);
        envelope.put("pageSize", page.size);
        envelope.put("rows", rows);
        envelope.put("total", total);
        Map<String, Object> totals = new LinkedHashMap<String, Object>();
        totals.put("qty_", total);
        envelope.put("totalMap", totals);
        envelope.put("totalPages", pages(total, page.size));
        return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    private String selectTaskResultPage(String organization, long orderTaskId, Page page,
            boolean distinct) throws SQLException {
        String scoped = BASE_SQL + " WHERE A.CREATEDBYORGID=? AND A.ORDERTASKID=?";
        String source = distinct
                ? "SELECT D.* FROM (SELECT Q.*,ROW_NUMBER() OVER (PARTITION BY Q.ITEMID ORDER BY Q.ID DESC) DISTINCT_RN FROM ("
                    + scoped + ") Q) D WHERE D.DISTINCT_RN=1"
                : scoped;
        String countSql = "SELECT COUNT(1) FROM (" + source + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) PAGE_RN_ FROM ("
                + source + ") Q) P WHERE P.PAGE_RN_>? AND P.PAGE_RN_<=? ORDER BY P.PAGE_RN_";
        int total;
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement count = connection.prepareStatement(countSql)) {
                count.setString(1, organization); count.setLong(2, orderTaskId);
                try (ResultSet result = count.executeQuery()) {
                    if (!result.next()) throw new SQLException("task result count returned no row");
                    total = result.getInt(1);
                }
            }
            try (PreparedStatement query = connection.prepareStatement(pageSql)) {
                query.setString(1, organization); query.setLong(2, orderTaskId);
                query.setInt(3, page.start()); query.setInt(4, page.end());
                try (ResultSet result = query.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    while (result.next()) rows.add(row(result, metadata));
                }
            }
        }
        Map<String, Object> envelope = new LinkedHashMap<String, Object>();
        envelope.put("columnTypeMap", RESULT_TYPE_PROFILES.get("/queries"));
        envelope.put("first", page.number == 1);
        envelope.put("last", total == 0 || page.number >= pages(total, page.size));
        envelope.put("numberOfElements", rows.size()); envelope.put("pageNumber", page.number);
        envelope.put("pageSize", page.size); envelope.put("rows", rows); envelope.put("total", total);
        Map<String, Object> totals = new LinkedHashMap<String, Object>(); totals.put("qty_", total);
        envelope.put("totalMap", totals); envelope.put("totalPages", pages(total, page.size));
        return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    private static final Map<String, Map<String, String>> RESULT_TYPE_PROFILES = buildResultTypeProfiles();

    private static String resultProfile(String path) {
        if (path == null || !path.startsWith(PREFIX)) return "";
        String suffix = path.substring(PREFIX.length());
        return suffix.isEmpty() ? "/queries" : suffix;
    }

    private static Map<String, Map<String, String>> buildResultTypeProfiles() {
        Map<String, Map<String, String>> profiles = new LinkedHashMap<String, Map<String, String>>();
        String encoded = "allowableerror=string;analysisbegindate=dateTime;analysisdate=dateTime;analysistype=string;analysisusername=string;avgresult=string;bizcategorycode=string;calqcresult=string;checkusername=string;clientordername=string;coefficient=string;convertfactor=string;createdbyid=string;createdbyname=string;createdbyorgid=string;createdbyorgname=string;createdtime=dateTime;cupno=number;cycle=number;emptyfilterbarrelmonefive=string;emptyfilterbarrelmonefour=string;emptyfilterbarrelmoneone=string;emptyfilterbarrelmonesix=string;emptyfilterbarrelmonethree=string;emptyfilterbarrelmonetwo=string;entrustorgname=string;equiptacquno=string;equiptname=string;equiptno=string;folderid=number;folderno=string;frequency=number;highlimit=string;id=number;indexaddflag=string;indexhighlimit=string;indexid=string;indexlimitunit=string;indexlowlimit=string;indexname=string;itemaliasname=string;itemid=number;itemname=string;itemorderno=number;judgebasis=string;labelno=string;latitude=string;longitude=string;lowlimit=string;methodid=number;methodname=string;methodversionid=number;mobileid=string;monitorpointid=number;monitorpointname=string;monitorpointno=string;monitortmplid=string;multipleitemgroup=string;nonsnrange=string;ordercategory=string;orderdesc=string;orderid=number;orderno=string;ordertaskid=number;ordertaskstatus=string;originalresult=string;parentid=number;parentorderno=string;plansampledate=dateTime;projid=number;projname=string;projno=string;qccategorycode=string;qccategoryid=number;qcdecisioncategory=string;qcname=string;reason=string;registdate=dateTime;remark=string;repeatno=number;repeatqty=number;reportdisplay=string;reportmonitorpointname=string;reportresult=string;resultcategory=string;resultrule=string;resultstatus=string;revisionrule=string;rn1=string;rn10=string;rn11=string;rn12=string;rn13=string;rn14=string;rn15=string;rn16=string;rn17=string;rn18=string;rn19=string;rn2=string;rn20=string;rn21=string;rn3=string;rn4=string;rn5=string;rn6=string;rn7=string;rn8=string;rn9=string;rn_=number;runid=string;samplebegindate=dateTime;samplebegintime=string;samplecompletedate=dateTime;samplecompletetime=string;sampleid=number;samplenum=number;sampleusername=string;samplingdepth=string;sceneequiptcollectno=string;sceneequiptno=string;scenefileno=string;scriptflag=string;setname=string;source=string;standhighlimit=string;standlowlimit=string;standqty=string;stockbatchno=string;submitflag=string;testflag=string;testid=number;testmethodbasis=string;testmethodid=number;testname=string;testorgname=string;theorycontent=string;unitname=string;usetype=string;virtual=string";
        Map<String, String> types = new LinkedHashMap<String, String>();
        for (String token : encoded.split(";")) { int at = token.indexOf('='); if (at > 0) types.put(token.substring(0, at), token.substring(at + 1)); }
        profiles.put("/queries", types); profiles.put("/queries/raw", types); profiles.put("/queries/searchable", types);
        return profiles;
    }

    private List<Map<String, Object>> selectResultRows(String organization) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        String sql = BASE_SQL + " WHERE A.CREATEDBYORGID=? ORDER BY A.ID DESC";
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql)) {
            query.setString(1, organization);
            try (ResultSet result = query.executeQuery()) {
                ResultSetMetaData metadata = result.getMetaData();
                while (result.next() && rows.size() < 1000) rows.add(row(result, metadata));
            }
        }
        return rows;
    }

    private static String dataColumnId(Map<String, Object> row) {
        return String.valueOf(row.get("testId")) + String.valueOf(row.get("itemId"))
                + String.valueOf(row.get("repeatNo"));
    }

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i);
            if ("RN_".equalsIgnoreCase(label) || "DISTINCT_RN".equalsIgnoreCase(label)
                    || "PAGE_RN_".equalsIgnoreCase(label)) continue;
            Object raw = JdbcValue.normalize(result.getObject(i));
            if (raw != null) value.put(camel(label), raw);
        }
        return value;
    }

    private static String camel(String label) {
        StringBuilder result = new StringBuilder();
        boolean upper = false;
        for (char value : label.toLowerCase(Locale.ROOT).toCharArray()) {
            if (value == '_') { upper = true; continue; }
            result.append(upper ? Character.toUpperCase(value) : value);
            upper = false;
        }
        return result.toString();
    }

    private static String type(int sqlType) {
        if (sqlType == Types.DATE || sqlType == Types.TIMESTAMP || sqlType == Types.TIMESTAMP_WITH_TIMEZONE) return "dateTime";
        if (sqlType == Types.NUMERIC || sqlType == Types.DECIMAL || sqlType == Types.INTEGER || sqlType == Types.BIGINT) return "number";
        return "string";
    }

    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().header("Cache-Control", "no-store").body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.ORDER_TASK_RESULT.INVALID_QUERY\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.ORDER_TASK_RESULT.UNAVAILABLE\"}"); }
    private static ResponseEntity<String> unexpected(HttpServletResponse response, String path) {
        String safePath = path == null ? "" : path.replace("\\", "\\\\").replace("\"", "\\\"");
        return writeRawError(response, "GIKAM.EXCEPTION.UNEXPECTED", safePath);
    }
    private static ResponseEntity<String> gasUnexpected(HttpServletResponse response, String path) {
        String safePath = path == null ? "" : path.replace("\\", "\\\\").replace("\"", "\\\"");
        return writeRawError(response, "core.rest.json.unable-parse-unique-bean", safePath);
    }
    private static ResponseEntity<String> writeRawError(HttpServletResponse response, String message, String path) {
        String body = "{\"error\":\"Internal Server Error\",\"message\":\"" + message
                + "\",\"path\":\"" + path + "\",\"status\":500,\"timestamp\":"
                + System.currentTimeMillis() + ",\"stacktrace\":\"\"}";
        try {
            response.resetBuffer();
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setHeader(HttpHeaders.CONTENT_TYPE, "");
            response.getWriter().write(body);
            response.flushBuffer();
            return null;
        } catch (java.io.IOException error) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    private static final class Page {
        final int number; final int size;
        Page(int number, int size) { this.number = number; this.size = size; }
        static Page parse(String body) {
            JSONObject root = body == null || body.trim().isEmpty() ? new JSONObject() : JSON.parseObject(body);
            JSONObject p = root == null ? null : root.getJSONObject("p");
            int number = p == null ? 1 : Math.max(1, p.getIntValue("n"));
            int size = p == null ? 50 : Math.min(500, Math.max(1, p.getIntValue("s")));
            return new Page(number, size);
        }
        int start() { return (number - 1) * size; }
        int end() { return number * size; }
    }
}
