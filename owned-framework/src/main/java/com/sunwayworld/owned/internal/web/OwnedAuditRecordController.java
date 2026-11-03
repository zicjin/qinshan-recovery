package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.owned.internal.data.JdbcValue;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Source-owned implementation of the recovered audit-record resource.
 *
 * <p>The original resource inherits its query/detail mappings from
 * {@code GenericResource}.  The project source is available, but its supplier
 * DAO implementation is not part of the owned distribution.  This controller
 * keeps the same Spring route and wire envelope while applying the owned
 * identity/organization seam and a bounded column whitelist.</p>
 */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedAuditRecordController {
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedAuditRecordController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("audit-record dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @PostMapping(path = {"/secure/emc/module/bp/audit-records/queries",
            "/secure/emc/module/bp/audit-records/queries/raw",
            "/secure/emc/module/bp/audit-records/queries/searchable"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            QueryRequest query;
            try {
                query = QueryRequest.parse(body, null);
            } catch (IllegalArgumentException invalid) {
                throw new MalformedAuditQueryException(invalid);
            }
            return ok(page(principal.getOrgId(), query));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (MalformedAuditQueryException malformed) { return unexpected(response, request.getRequestURI()); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = "/secure/emc/module/bp/audit-records/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request, @PathVariable("id") String id) {
        try {
            OwnedPrincipal principal = require(request);
            if (id == null || !id.matches("[0-9]{1,30}")) throw new IllegalArgumentException("audit-record id is invalid");
            Map<String, Object> row = readDetail(principal.getOrgId(), Long.valueOf(id));
            return ok(JSON.toJSONString(row == null ? Collections.emptyMap() : row, SerializerFeature.WriteMapNullValue));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    /** GenericResource.selectColumnById implementation for the recovered audit entity. */
    @GetMapping(path = "/secure/emc/module/bp/audit-records/{id}/columns/{column}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> column(HttpServletRequest request,
            @PathVariable("id") String id, @PathVariable("column") String column) {
        try {
            OwnedPrincipal principal = require(request);
            long auditId = numericId(id);
            String selected = selectableColumn(column);
            Object value = readColumn(principal.getOrgId(), auditId, selected);
            return ok(JSON.toJSONString(value, SerializerFeature.WriteMapNullValue));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    /** GenericResource.print is a read-only projection of the same detail row. */
    @GetMapping(path = "/secure/emc/module/bp/audit-records/{id}/action/print",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> print(HttpServletRequest request, @PathVariable("id") String id) {
        return detail(request, id);
    }

    /** GenericResource.validate checks that the record exists in the actor's organization. */
    @PostMapping(path = "/secure/emc/module/bp/audit-records/{id}/action/validate",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> validate(HttpServletRequest request, @PathVariable("id") String id,
            @RequestBody(required = false) String ignored) {
        try {
            OwnedPrincipal principal = require(request);
            boolean present = readColumn(principal.getOrgId(), numericId(id), "ID") != null;
            return ok(JSON.toJSONString(new RestValidationResultBean(Boolean.valueOf(present))));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    /** GenericResource.validateUnique supports both body and path forms. */
    @PostMapping(path = {
            "/secure/emc/module/bp/audit-records/action/validate-unique",
            "/secure/emc/module/bp/audit-records/{id}/action/validate-unique",
            "/secure/emc/module/bp/audit-records/action/validate-unique/{columnName}/{columnValue}",
            "/secure/emc/module/bp/audit-records/{id}/action/validate-unique/{columnName}/{columnValue}"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> validateUnique(HttpServletRequest request,
            @PathVariable(value = "id", required = false) String id,
            @PathVariable(value = "columnName", required = false) String columnName,
            @PathVariable(value = "columnValue", required = false) String columnValue,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            UniqueInput input = uniqueInput(id, columnName, columnValue, body);
            String selected = selectableColumn(input.column);
            int count = countColumn(principal.getOrgId(), input.id, selected, input.value);
            return ok(JSON.toJSONString(new RestValidationResultBean(Boolean.valueOf(count == 0),
                    count == 0 ? null : "core.validator.column.not-unique")));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requireAuditRead(request.getSession(false));
        return boundary.currentPrincipal(request.getSession(false));
    }

    private String page(String organization, QueryRequest request) throws SQLException {
        Sql sql = build(request, organization, null);
        try (Connection connection = dataSource.getConnection()) {
            int total;
            try (PreparedStatement count = connection.prepareStatement(sql.countSql)) {
                bind(count, sql.countValues);
                try (ResultSet rows = count.executeQuery()) {
                    if (!rows.next()) throw new SQLException("audit-record count returned no row");
                    total = rows.getInt(1);
                }
            }
            List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
            Map<String, String> types = new LinkedHashMap<String, String>();
            try (PreparedStatement select = connection.prepareStatement(sql.pageSql)) {
                bind(select, sql.pageValues);
                try (ResultSet result = select.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    types.putAll(columnTypes(metadata));
                    while (result.next()) rows.add(row(result, metadata));
                }
            }
            Map<String, Object> body = new LinkedHashMap<String, Object>();
            body.put("columnTypeMap", types);
            body.put("first", request.getPageNumber() == 1);
            body.put("last", total == 0 || request.getPageNumber() >= pages(total, request.getPageSize()));
            body.put("numberOfElements", rows.size());
            body.put("pageNumber", request.getPageNumber());
            body.put("pageSize", request.getPageSize());
            body.put("rows", rows);
            body.put("total", total);
            body.put("totalMap", new LinkedHashMap<String, Object>());
            body.put("totalPages", pages(total, request.getPageSize()));
            return JSON.toJSONString(body, SerializerFeature.WriteMapNullValue);
        }
    }

    private Map<String, Object> readDetail(String organization, Long id) throws SQLException {
        Sql sql = build(null, organization, id);
        try (Connection connection = dataSource.getConnection(); PreparedStatement select = connection.prepareStatement(sql.detailSql)) {
            bind(select, sql.detailValues);
            try (ResultSet result = select.executeQuery()) {
                if (!result.next()) return null;
                return row(result, result.getMetaData());
            }
        }
    }

    private Object readColumn(String organization, long id, String column) throws SQLException {
        String sql = "SELECT " + column + " FROM T_EMC_AUDIT_RECORD WHERE CREATEDBYORGID=? AND ID=?";
        try (Connection connection = dataSource.getConnection(); PreparedStatement select = connection.prepareStatement(sql)) {
            select.setString(1, organization); select.setLong(2, id);
            try (ResultSet result = select.executeQuery()) {
                return result.next() ? JdbcValue.normalize(result.getObject(1)) : null;
            }
        }
    }

    private int countColumn(String organization, Long id, String column, String value) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(1) FROM T_EMC_AUDIT_RECORD WHERE CREATEDBYORGID=? AND ")
                .append(column).append("=?");
        if (id != null) sql.append(" AND ID<>?");
        try (Connection connection = dataSource.getConnection(); PreparedStatement select = connection.prepareStatement(sql.toString())) {
            select.setString(1, organization); select.setString(2, value);
            if (id != null) select.setLong(3, id.longValue());
            try (ResultSet result = select.executeQuery()) { result.next(); return result.getInt(1); }
        }
    }

    private static long numericId(String id) {
        if (id == null || !id.matches("[0-9]{1,30}")) throw new IllegalArgumentException("audit-record id is invalid");
        return Long.parseLong(id);
    }

    private static String selectableColumn(String name) {
        String key = name == null ? "" : name.toUpperCase(Locale.ROOT);
        String[] allowed = {"ID", "AUDITCATEGORY", "STEPCODE", "PROJID", "PROJNO", "FOLDERID", "FOLDERNO",
                "ORDERID", "ORDERNO", "SAMPLEID", "SAMPLENO", "RUNID", "TESTID", "METHODID", "TASKNAME",
                "NEXTTASKNAME", "COMMENTS", "OPERATECATEGORY", "LOGDESC", "BIZCATEGORY", "CREATEDBYID",
                "CREATEDBYNAME", "CREATEDTIME", "CREATEDBYORGID", "CREATEDBYORGNAME"};
        for (String value : allowed) if (value.equals(key)) return value;
        throw new IllegalArgumentException("unsupported audit-record column");
    }

    private static UniqueInput uniqueInput(String id, String column, String value, String body) {
        JSONObject object = body == null || body.trim().length() == 0 ? new JSONObject() : (JSONObject) JSON.parse(body);
        if (object.containsKey("p")) object = object.getJSONObject("p");
        String actualColumn = column == null ? string(object.get("columnName")) : column;
        String actualValue = value == null ? string(object.get("columnValue")) : value;
        if (actualColumn.length() == 0 || actualValue.length() == 0) throw new IllegalArgumentException("unique column and value are required");
        Long actualId = id == null || id.length() == 0 ? null : Long.valueOf(numericId(id));
        return new UniqueInput(actualId, actualColumn, actualValue);
    }

    private static String string(Object value) { return value == null ? "" : String.valueOf(value).trim(); }

    private Sql build(QueryRequest request, String organization, Long id) {
        List<Object> values = new ArrayList<Object>();
        StringBuilder where = new StringBuilder(" WHERE T.CREATEDBYORGID=?");
        values.add(organization);
        if (id != null) { where.append(" AND T.ID=?"); values.add(id); }
        if (request != null) appendParameters(where, values, request);
        String base = "SELECT T.* FROM T_EMC_AUDIT_RECORD T" + where;
        String order = request == null ? "Q.ID DESC" : order(request);
        String count = "SELECT COUNT(1) FROM (" + base + ") Q";
        String page = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY " + order + ") RN FROM (" + base + ") Q) P WHERE RN>? AND RN<=? ORDER BY RN";
        List<Object> pageValues = new ArrayList<Object>(values);
        if (request != null) {
            long start = ((long) request.getPageNumber() - 1L) * request.getPageSize();
            pageValues.add(start); pageValues.add(start + request.getPageSize());
        }
        return new Sql(count, values, page, pageValues, base, values, metadata(request));
    }

    private void appendParameters(StringBuilder where, List<Object> values, QueryRequest request) {
        for (Map.Entry<String, Object> entry : request.getMapperParameters().entrySet()) {
            String key = entry.getKey();
            int split = key.lastIndexOf('_');
            MatchPattern pattern = split > 0 ? MatchPattern.getPatternIfPossible(key.substring(split + 1).toUpperCase(Locale.ROOT)) : null;
            if (pattern == null) throw new IllegalArgumentException("unsupported audit-record parameter: " + key);
            appendCondition(where, values, column(key.substring(0, split)), pattern, entry.getValue());
        }
        for (FilterParamPattern filter : request.getFilterPatterns()) {
            MatchPattern pattern = MatchPattern.getPatternIfPossible(filter.getMatchPattern());
            if (pattern == null) throw new IllegalArgumentException("unsupported audit-record filter");
            for (Map.Entry<String, Object> entry : filter.getMap().entrySet()) {
                appendCondition(where, values, column(entry.getKey()), pattern, entry.getValue());
            }
        }
        for (com.sunwayworld.framework.mybatis.mapper.AdvancedFilterPattern filter : request.getAdvancedPatterns()) {
            throw new IllegalArgumentException("advanced audit-record filters are not supported");
        }
        if (request.getMultiPropertyPattern() != null) throw new IllegalArgumentException("multi-property audit-record filter is not supported");
    }

    private static void appendCondition(StringBuilder where, List<Object> values, String field,
            MatchPattern pattern, Object value) {
        String p = pattern.name();
        if ("CISC".equals(p) || "SC".equals(p)) { where.append(" AND UPPER(").append(field).append(") LIKE ?"); values.add("%" + String.valueOf(value).toUpperCase(Locale.ROOT) + "%"); return; }
        if ("CISB".equals(p) || "SB".equals(p)) { where.append(" AND UPPER(").append(field).append(") LIKE ?"); values.add(String.valueOf(value).toUpperCase(Locale.ROOT) + "%"); return; }
        if ("CISE".equals(p) || "SE".equals(p)) { where.append(" AND UPPER(").append(field).append(") LIKE ?"); values.add("%" + String.valueOf(value).toUpperCase(Locale.ROOT)); return; }
        if ("CISEQ".equals(p) || "SEQ".equals(p) || "EQ".equals(p)) { where.append(" AND ").append(field).append(value == null ? " IS NULL" : "=?"); if (value != null) values.add(value); return; }
        if ("DIFFER".equals(p)) { where.append(" AND ").append(field).append(value == null ? " IS NOT NULL" : "<>?"); if (value != null) values.add(value); return; }
        if ("NG".equals(p) || "TG".equals(p) || "DG".equals(p)) { where.append(" AND ").append(field).append(">?"); values.add(value); return; }
        if ("NGOE".equals(p) || "TGOE".equals(p) || "DGOE".equals(p)) { where.append(" AND ").append(field).append(">=?"); values.add(value); return; }
        if ("NL".equals(p) || "TL".equals(p) || "DL".equals(p)) { where.append(" AND ").append(field).append("<?"); values.add(value); return; }
        if ("NLOE".equals(p) || "TLOE".equals(p) || "DLOE".equals(p)) { where.append(" AND ").append(field).append("<=?"); values.add(value); return; }
        if ("ISNULL".equals(p)) { where.append(" AND ").append(field).append(" IS NULL"); return; }
        if ("ISNOTNULL".equals(p)) { where.append(" AND ").append(field).append(" IS NOT NULL"); return; }
        throw new IllegalArgumentException("unsupported audit-record filter pattern: " + p);
    }

    private static String order(QueryRequest request) {
        if (request.getOrderParameters().isEmpty()) return "Q.STEPCODE ASC,Q.ID ASC";
        List<String> parts = new ArrayList<String>();
        for (Pair<String, String> pair : request.getOrderParameters()) {
            String direction = "desc".equalsIgnoreCase(pair.getSecond()) ? " DESC" : " ASC";
            parts.add(orderColumn(pair.getFirst()) + direction);
        }
        return String.join(",", parts);
    }

    private static String column(String name) {
        String key = name == null ? "" : name.toUpperCase(Locale.ROOT);
        String[] fields = {"ID", "AUDITCATEGORY", "STEPCODE", "PROJID", "PROJNO", "FOLDERID", "FOLDERNO",
                "ORDERID", "ORDERNO", "SAMPLEID", "SAMPLENO", "RUNID", "TESTID", "METHODID", "TASKNAME",
                "NEXTTASKNAME", "COMMENTS", "COMMENT", "OPERATECATEGORY", "LOGDESC", "BIZCATEGORY", "CREATEDBYID",
                "CREATEDBYNAME", "CREATEDTIME", "CREATEDBYORGID", "CREATEDBYORGNAME"};
        for (String field : fields) if (field.equals(key)) return "T." + ("COMMENT".equals(key) ? "COMMENTS" : key);
        throw new IllegalArgumentException("unsupported audit-record column: " + name);
    }

    private static String orderColumn(String name) {
        String key = name == null ? "" : name.toUpperCase(Locale.ROOT);
        if ("ID".equals(key) || "STEPCODE".equals(key) || "CREATEDTIME".equals(key)) return "Q." + key;
        throw new IllegalArgumentException("unsupported audit-record order column: " + name);
    }

    private static Map<String, String> columnTypes(ResultSetMetaData metadata) throws SQLException {
        Map<String, String> values = new LinkedHashMap<String, String>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String name = metadata.getColumnLabel(i);
            if ("RN".equalsIgnoreCase(name)) continue;
            int type = metadata.getColumnType(i);
            String value = type == Types.DATE || type == Types.TIMESTAMP || type == Types.TIMESTAMP_WITH_TIMEZONE ? "dateTime"
                    : type == Types.NUMERIC || type == Types.DECIMAL || type == Types.INTEGER || type == Types.BIGINT || type == Types.FLOAT || type == Types.DOUBLE ? "number" : "string";
            values.put(name.toLowerCase(Locale.ROOT), value);
        }
        return values;
    }

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> row = new LinkedHashMap<String, Object>();
        Map<String, Object> extension = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i);
            Object value = JdbcValue.normalize(result.getObject(i));
            if ("RN".equalsIgnoreCase(label)) extension.put("rn_", String.valueOf(value));
            else row.put(friendly(label), value);
        }
        row.put("ext$", extension);
        return row;
    }

    private static String friendly(String name) {
        StringBuilder value = new StringBuilder(); boolean upper = false;
        for (char c : name.toLowerCase(Locale.ROOT).toCharArray()) {
            if (c == '_') { upper = true; continue; }
            value.append(upper ? Character.toUpperCase(c) : c); upper = false;
        }
        return value.toString();
    }

    private static void bind(PreparedStatement statement, List<Object> values) throws SQLException {
        for (int i = 0; i < values.size(); i++) statement.setObject(i + 1, values.get(i));
    }
    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }
    private static Map<String, String> metadata(QueryRequest ignored) { return Collections.emptyMap(); }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().header("Cache-Control", "no-store").body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.AUDIT.INVALID_QUERY\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.AUDIT.UNAVAILABLE\"}"); }

    /** Supplier GenericResource binding failures are unexpected 500 responses. */
    private static ResponseEntity<String> unexpected(HttpServletResponse response, String path) {
        if (response != null) {
            try {
                response.resetBuffer();
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.getWriter().write(unexpectedBody(path));
                response.getWriter().flush();
                return null;
            } catch (IOException ignored) { /* fall through to a normal response */ }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(unexpectedBody(path));
    }

    private static String unexpectedBody(String path) {
        String safePath = path == null ? "" : path.replace("\\", "\\\\").replace("\"", "\\\"");
        return "{\"error\":\"Internal Server Error\",\"message\":\"GIKAM.EXCEPTION.UNEXPECTED\",\"path\":\""
                + safePath + "\",\"stacktrace\":\"owned-audit-record-malformed-query\",\"status\":500,\"timestamp\":\""
                + new java.util.Date().toString() + "\"}";
    }

    private static final class MalformedAuditQueryException extends IllegalArgumentException {
        MalformedAuditQueryException(Throwable cause) { super("malformed audit-record query", cause); }
    }

    private static final class Sql {
        final String countSql, pageSql, detailSql; final List<Object> countValues, pageValues, detailValues; final Map<String, String> metadata;
        Sql(String countSql, List<Object> countValues, String pageSql, List<Object> pageValues, String detailSql, List<Object> detailValues, Map<String, String> metadata) {
            this.countSql = countSql; this.countValues = countValues; this.pageSql = pageSql; this.pageValues = pageValues; this.detailSql = detailSql; this.detailValues = detailValues; this.metadata = metadata;
        }
    }

    private static final class UniqueInput {
        final Long id; final String column; final String value;
        UniqueInput(Long id, String column, String value) { this.id = id; this.column = column; this.value = value; }
    }
}
