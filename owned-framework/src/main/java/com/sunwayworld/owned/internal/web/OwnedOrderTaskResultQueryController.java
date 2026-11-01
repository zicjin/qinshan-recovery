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
            return ok(selectPage(principal.getOrgId(), Page.parse(body)));
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

    private OwnedPrincipal requireRead(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private String selectPage(String organization, Page page) throws SQLException {
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
                query.setString(2, organization);
                query.setInt(3, page.start());
                query.setInt(4, page.end());
                try (ResultSet result = query.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    for (int i = 1; i <= metadata.getColumnCount(); i++) {
                        types.put(metadata.getColumnLabel(i).toLowerCase(Locale.ROOT), type(metadata.getColumnType(i)));
                    }
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

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i);
            if ("RN_".equalsIgnoreCase(label)) continue;
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
