package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.owned.internal.data.JdbcValue;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Source-owned list and approval-task reads for the original material-apply pages. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedMaterialApplyQueryController {
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedMaterialApplyQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("material apply query dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource;
    }

    @PostMapping(path = {
            "/secure/core/module/item/mtl-applies/queries",
            "/secure/core/module/item/mtl-applies/queries/raw",
            "/secure/core/module/item/mtl-applies/queries/tasks",
            "/secure/core/module/item/mtl-applies/queries/withdrawable",
            "/secure/core/module/item/mtl-applies/queries/undoable",
            "/secure/emc/module/item/mtl-applies/queries",
            "/secure/emc/module/item/mtl-applies/queries/raw",
            "/secure/emc/module/item/mtl-applies/queries/searchable",
            "/secure/emc/module/item/mtl-applies/queries/tasks",
            "/secure/emc/module/item/mtl-applies/queries/withdrawable",
            "/secure/emc/module/item/mtl-applies/queries/undoable"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            String processStatus = workflowProcessStatus(request == null ? null : request.getRequestURI());
            return ok(page(read(request, body, processStatus)));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {"/secure/core/module/item/mtl-applies/info/status", "/secure/emc/module/item/mtl-applies/info/status"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.CONSUMABLE_READ); return ok("{\"auditStatus\":{\"processStatus\":\"draft\",\"statusCode\":\"draft\"},\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private PageResult read(HttpServletRequest request, String body, String processStatus) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.CONSUMABLE_READ);
        Set<String> organizations = boundary.permittedOrganizationIds(request.getSession(false));
        QueryRequest query = QueryRequest.parse(body, null);
        validateQuery(query);
        if (organizations == null || organizations.isEmpty()) return PageResult.empty(query);
        SqlSpec spec = build(query, organizations, processStatus);
        try (Connection connection = dataSource.getConnection()) {
            int total;
            try (PreparedStatement count = connection.prepareStatement(spec.countSql)) {
                bind(count, spec.values);
                try (ResultSet rows = count.executeQuery()) { if (!rows.next()) throw new SQLException("material apply count failed"); total = rows.getInt(1); }
            }
            List<Object> pageValues = new ArrayList<Object>(spec.values);
            long start = ((long) query.getPageNumber() - 1L) * query.getPageSize();
            pageValues.add(start); pageValues.add(start + query.getPageSize());
            List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
            try (PreparedStatement select = connection.prepareStatement(spec.pageSql)) {
                bind(select, pageValues);
                try (ResultSet rows = select.executeQuery()) {
                    ResultSetMetaData metadata = rows.getMetaData();
                    while (rows.next()) values.add(row(rows, metadata));
                }
            }
            return new PageResult(total, query.getPageSize(), query.getPageNumber(), values);
        }
    }

    public static void validateQuery(QueryRequest query) {
        if (query == null) throw new IllegalArgumentException("query is required");
        if (!query.getAdvancedPatterns().isEmpty() || query.getMultiPropertyPattern() != null) {
            throw new IllegalArgumentException("advanced material apply query shape is unsupported");
        }
        for (String key : query.getMapperParameters().keySet()) {
            String lower = key == null ? "" : key.toLowerCase(Locale.ROOT);
            if (!("createdbyid_seq".equals(lower) || "processstatus_seq".equals(lower))) {
                throw new IllegalArgumentException("unsupported material apply query parameter: " + key);
            }
        }
        for (FilterParamPattern filter : query.getFilterPatterns()) {
            MatchPattern pattern = MatchPattern.getPatternIfPossible(filter.getMatchPattern());
            if (pattern == null) throw new IllegalArgumentException("unsupported material apply filter pattern");
            for (String name : filter.getMap().keySet()) column(name);
        }
        for (com.sunwayworld.framework.data.Pair<String, String> order : query.getOrderParameters()) orderColumn(order.getFirst());
    }

    public static String workflowProcessStatus(String requestUri) {
        if (requestUri == null) return null;
        if (requestUri.endsWith("/queries/tasks") || requestUri.endsWith("/queries/withdrawable")) return "approve";
        if (requestUri.endsWith("/queries/undoable")) return "done";
        return null;
    }

    private static SqlSpec build(QueryRequest query, Set<String> organizations, String processStatus) {
        List<Object> values = new ArrayList<Object>();
        StringBuilder where = new StringBuilder("A.CREATEDBYORGID IN (");
        int index = 0;
        for (String ignored : organizations) { if (index++ > 0) where.append(','); where.append('?'); values.add(ignored); }
        where.append(')');
        if (processStatus != null) appendEquals(where, values, "A.PROCESSSTATUS", processStatus);
        for (Map.Entry<String, Object> parameter : query.getMapperParameters().entrySet()) {
            String key = parameter.getKey().toLowerCase(Locale.ROOT);
            if ("createdbyid_seq".equals(key)) appendEquals(where, values, "A.CREATEDBYID", parameter.getValue());
            if ("processstatus_seq".equals(key)) appendEquals(where, values, "A.PROCESSSTATUS", parameter.getValue());
        }
        for (FilterParamPattern filter : query.getFilterPatterns()) {
            MatchPattern pattern = MatchPattern.getPatternIfPossible(filter.getMatchPattern());
            for (Map.Entry<String, Object> item : filter.getMap().entrySet()) appendCondition(where, values, "A." + column(item.getKey()), pattern, item.getValue());
        }
        String base = "SELECT A.ID,A.MTLID,A.APPLYQTY,A.REMARK,A.CREATEDBYID,A.CREATEDBYNAME,A.CREATEDTIME,A.CREATEDBYORGID,A.CREATEDBYORGNAME,A.PROCESSSTATUS,A.STOCKID,"
                + "B.MTLNAME,B.SPECIFICATION,B.MTLCATEGORYID,B.HARMFULNESS,C.BATCHNO,C.STOCKQTY,D.STDVALUE "
                + "FROM T_EMC_MTL_APPLY A LEFT JOIN T_EMC_MTL B ON A.MTLID=B.ID LEFT JOIN T_EMC_STOCK C ON A.STOCKID=C.ID "
                + "LEFT JOIN (SELECT STOCKID,STDVALUE FROM T_EMC_STOCK_CONCENT M WHERE NOT EXISTS "
                + "(SELECT 1 FROM T_EMC_STOCK_CONCENT N WHERE M.STOCKID=N.STOCKID AND N.ID<M.ID)) D ON A.STOCKID=D.STOCKID WHERE " + where;
        String count = "SELECT COUNT(1) FROM (" + base + ") Q";
        String order = query.getOrderParameters().isEmpty() ? "Q.ID DESC" : "Q." + orderColumn(query.getOrderParameters().get(0).getFirst()) + ("desc".equalsIgnoreCase(query.getOrderParameters().get(0).getSecond()) ? " DESC" : " ASC");
        String page = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY " + order + ") RN FROM (" + base + ") Q) P WHERE RN>? AND RN<=? ORDER BY RN";
        return new SqlSpec(count, page, values);
    }

    private static void appendEquals(StringBuilder where, List<Object> values, String column, Object value) {
        if (value == null || String.valueOf(value).trim().isEmpty()) return;
        where.append(" AND ").append(column).append("=?"); values.add(value);
    }

    private static void appendCondition(StringBuilder where, List<Object> values, String column, MatchPattern pattern, Object value) {
        String name = pattern.name();
        if ("ISNULL".equals(name)) { where.append(" AND ").append(column).append(" IS NULL"); return; }
        if ("ISNOTNULL".equals(name)) { where.append(" AND ").append(column).append(" IS NOT NULL"); return; }
        if ("EQ".equals(name) || "SEQ".equals(name)) { where.append(" AND ").append(column).append("=?"); values.add(value); return; }
        if ("CISEQ".equals(name)) { where.append(" AND UPPER(").append(column).append(")=?"); values.add(String.valueOf(value).toUpperCase(Locale.ROOT)); return; }
        if ("CISC".equals(name) || "SC".equals(name)) { where.append(" AND ").append("CISC".equals(name) ? "UPPER(" + column + ")" : column).append(" LIKE ?"); String text = String.valueOf(value); values.add("CISC".equals(name) ? ("%" + text + "%").toUpperCase(Locale.ROOT) : "%" + text + "%"); return; }
        if ("DIFFER".equals(name)) { where.append(value == null ? " AND " + column + " IS NOT NULL" : " AND " + column + "<>?"); if (value != null) values.add(value); return; }
        throw new IllegalArgumentException("unsupported material apply filter pattern: " + name);
    }

    private static String column(String value) {
        String key = value == null ? "" : value.toUpperCase(Locale.ROOT);
        for (String allowed : new String[] {"ID", "MTLID", "APPLYQTY", "PROCESSSTATUS", "CREATEDBYID", "STOCKID"}) if (allowed.equals(key)) return allowed;
        throw new IllegalArgumentException("unsupported material apply filter column: " + value);
    }

    private static String orderColumn(String value) {
        String key = value == null ? "" : value.toUpperCase(Locale.ROOT);
        for (String allowed : new String[] {"ID", "APPLYQTY", "CREATEDTIME", "PROCESSSTATUS", "MTLID"}) if (allowed.equals(key)) return allowed;
        throw new IllegalArgumentException("unsupported material apply order column: " + value);
    }

    private static void bind(PreparedStatement statement, List<Object> values) throws SQLException { for (int i = 0; i < values.size(); i++) statement.setObject(i + 1, values.get(i)); }

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) { String label = metadata.getColumnLabel(i); if (!"RN".equalsIgnoreCase(label)) value.put(camel(label), JdbcValue.normalize(result.getObject(i))); }
        Map<String, Object> extension = new LinkedHashMap<String, Object>();
        extension.put("mtlname", value.remove("mtlName"));
        extension.put("specification", value.remove("specification"));
        extension.put("mtlcategoryid", value.remove("mtlCategoryId"));
        extension.put("harmfulness", value.remove("harmfulness"));
        extension.put("batchno", value.remove("batchNo"));
        extension.put("stockqty", value.remove("stockQty"));
        extension.put("stdvalue", value.remove("stdValue"));
        value.put("ext$", extension); return value;
    }

    private static String camel(String name) {
        String key = name.toUpperCase(Locale.ROOT);
        if ("ID".equals(key)) return "id";
        if ("MTLID".equals(key)) return "mtlId";
        if ("APPLYQTY".equals(key)) return "applyQty";
        if ("CREATEDBYID".equals(key)) return "createdById";
        if ("CREATEDBYNAME".equals(key)) return "createdByName";
        if ("CREATEDTIME".equals(key)) return "createdTime";
        if ("CREATEDBYORGID".equals(key)) return "createdByOrgId";
        if ("CREATEDBYORGNAME".equals(key)) return "createdByOrgName";
        if ("PROCESSSTATUS".equals(key)) return "processStatus";
        if ("STOCKID".equals(key)) return "stockId";
        if ("MTLNAME".equals(key)) return "mtlName";
        if ("MTLCATEGORYID".equals(key)) return "mtlCategoryId";
        if ("BATCHNO".equals(key)) return "batchNo";
        if ("STOCKQTY".equals(key)) return "stockQty";
        if ("STDVALUE".equals(key)) return "stdValue";
        StringBuilder value = new StringBuilder(); boolean upper = false;
        for (char c : name.toLowerCase(Locale.ROOT).toCharArray()) { if (c == '_') { upper = true; continue; } value.append(upper ? Character.toUpperCase(c) : c); upper = false; }
        return value.toString();
    }
    private static String page(PageResult page) { Map<String, Object> body = new LinkedHashMap<String, Object>(); body.put("columnTypeMap", columnTypes()); body.put("first", page.pageNumber == 1); body.put("last", page.total == 0 || page.pageNumber >= page.totalPages()); body.put("numberOfElements", page.rows.size()); body.put("pageNumber", page.pageNumber); body.put("pageSize", page.pageSize); body.put("rows", page.rows); body.put("total", page.total); Map<String, Number> totals = new LinkedHashMap<String, Number>(); totals.put("qty_", Integer.valueOf(page.total)); body.put("totalMap", totals); body.put("totalPages", page.totalPages()); return JSON.toJSONString(body, SerializerFeature.WriteMapNullValue); }
    private static Map<String, String> columnTypes() { Map<String, String> types = new LinkedHashMap<String, String>(); types.put("applyqty", "number"); types.put("batchno", "string"); types.put("createdbyid", "string"); types.put("createdbyname", "string"); types.put("createdbyorgid", "string"); types.put("createdbyorgname", "string"); types.put("createdtime", "dateTime"); types.put("harmfulness", "string"); types.put("id", "number"); types.put("mtlcategoryid", "number"); types.put("mtlid", "string"); types.put("mtlname", "string"); types.put("processstatus", "string"); types.put("remark", "string"); types.put("rn_", "number"); types.put("specification", "string"); types.put("stdvalue", "string"); types.put("stockid", "number"); types.put("stockqty", "number"); return types; }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().header("Cache-Control", "no-store").body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.MTL_APPLY.INVALID_QUERY\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.MTL_APPLY.UNAVAILABLE\"}"); }
    private static final class SqlSpec { private final String countSql; private final String pageSql; private final List<Object> values; private SqlSpec(String c, String p, List<Object> v) { countSql = c; pageSql = p; values = v; } }
    static final class PageResult { private final int total; private final int pageSize; private final int pageNumber; private final List<Map<String, Object>> rows; private PageResult(int t, int s, int n, List<Map<String, Object>> r) { total = t; pageSize = s; pageNumber = n; rows = r; } private static PageResult empty(QueryRequest q) { return new PageResult(0, q.getPageSize(), q.getPageNumber(), Collections.<Map<String, Object>>emptyList()); } private int totalPages() { return total == 0 ? 0 : (total + pageSize - 1) / pageSize; } }
}
