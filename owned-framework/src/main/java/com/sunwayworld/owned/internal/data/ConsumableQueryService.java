package com.sunwayworld.owned.internal.data;

import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentity;
import com.sunwayworld.owned.internal.web.QueryRequest;
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
import java.util.Set;
import java.util.Locale;
import javax.sql.DataSource;

/** Owned consumable read seam. All SQL identifiers are server-owned. */
public final class ConsumableQueryService {
    private final DataSource dataSource;
    private final JdbcPersonnelIdentity identity;

    public ConsumableQueryService(DataSource dataSource, JdbcPersonnelIdentity identity) {
        if (dataSource == null || identity == null) throw new IllegalArgumentException("query dependencies required");
        this.dataSource = dataSource;
        this.identity = identity;
    }

    public Pagination<Map<String,Object>> list(QueryRequest request, JdbcPersonnelIdentity.Principal principal) throws SQLException {
        if (request == null || principal == null) throw new IllegalArgumentException("request and principal required");
        identity.authorizeConsumable(principal);
        Set<String> organizations = identity.permittedOrganizationIds(principal);
        List<Object> values = new ArrayList<Object>();
        String base = "SELECT T.* FROM T_EMC_CONSUME T WHERE " + scope("T.CREATEDBYORGID", organizations, values);
        validateOrder(request, "consume");
        Sql filtered = appendFilters(request, "T", values, base, "consume");
        return page(filtered.sql, filtered.values, request);
    }

    public Map<String,Object> detail(String id, JdbcPersonnelIdentity.Principal principal) throws SQLException {
        if (id == null || id.trim().isEmpty() || principal == null) throw new IllegalArgumentException("id and principal required");
        identity.authorizeConsumable(principal);
        Set<String> organizations = identity.permittedOrganizationIds(principal);
        List<Object> values = new ArrayList<Object>();
        String sql = "SELECT T.* FROM T_EMC_CONSUME T WHERE T.ID = ? AND " + scope("T.CREATEDBYORGID", organizations, values);
        values.add(0, id);
        return one(sql, values);
    }

    public Pagination<Map<String,Object>> stock(String mtlId, QueryRequest request, JdbcPersonnelIdentity.Principal principal) throws SQLException {
        if (mtlId == null || mtlId.trim().isEmpty() || request == null || principal == null) throw new IllegalArgumentException("mtlId, request and principal required");
        identity.authorizeConsumable(principal);
        Set<String> organizations = identity.permittedOrganizationIds(principal);
        List<Object> values = new ArrayList<Object>();
        boolean allMaterials = "0".equals(mtlId);
        String base = "SELECT T.* FROM T_EMC_CSM_STOCK T JOIN T_EMC_CONSUME C ON C.ID = T.MTLID WHERE "
                + (allMaterials ? "" : "T.MTLID = ? AND ") + scope("C.CREATEDBYORGID", organizations, values);
        if (!allMaterials) values.add(0, mtlId);
        validateOrder(request, "stock");
        Sql filtered = appendFilters(request, "T", values, base, "stock");
        return page(filtered.sql, filtered.values, request);
    }

    public Map<String,Object> stockDetail(Long stockId, JdbcPersonnelIdentity.Principal principal) throws SQLException {
        if (stockId == null || principal == null) throw new IllegalArgumentException("stockId and principal required");
        identity.authorizeConsumable(principal);
        Set<String> organizations = identity.permittedOrganizationIds(principal);
        List<Object> values = new ArrayList<Object>();
        String sql = "SELECT T.* FROM T_EMC_CSM_STOCK T JOIN T_EMC_CONSUME C ON C.ID = T.MTLID WHERE T.ID = ? AND "
                + scope("C.CREATEDBYORGID", organizations, values);
        values.add(0, stockId);
        return one(sql, values);
    }

    public Pagination<Map<String,Object>> stockLog(Long stockId, QueryRequest request, JdbcPersonnelIdentity.Principal principal) throws SQLException {
        if (stockId == null || request == null || principal == null) throw new IllegalArgumentException("stockId, request and principal required");
        identity.authorizeConsumable(principal);
        Set<String> organizations = identity.permittedOrganizationIds(principal);
        List<Object> values = new ArrayList<Object>();
        String base = "SELECT T.*, A.USERNAME AS USERNAME, B.ORGNAME AS ORGNAME FROM T_EMC_CSM_STOCK_LOG T "
                + "LEFT JOIN T_CORE_USER A ON T.RECEIVEDBYID = A.ID "
                + "LEFT JOIN T_CORE_ORG B ON T.RECEIVEDBYORGID = B.ID "
                + "JOIN T_EMC_CSM_STOCK S ON S.ID = T.STOCKID JOIN T_EMC_CONSUME C ON C.ID = S.MTLID "
                + "WHERE T.STOCKID = ? AND " + scope("C.CREATEDBYORGID", organizations, values);
        values.add(0, stockId);
        validateOrder(request, "log");
        Sql filtered = appendFilters(request, "T", values, base, "log");
        return page(filtered.sql, filtered.values, request);
    }

    public Map<String,Object> stockLogDetail(Long logId, JdbcPersonnelIdentity.Principal principal) throws SQLException {
        if (logId == null || principal == null) throw new IllegalArgumentException("logId and principal required");
        identity.authorizeConsumable(principal);
        Set<String> organizations = identity.permittedOrganizationIds(principal);
        List<Object> values = new ArrayList<Object>();
        String sql = "SELECT T.*, A.USERNAME AS USERNAME, B.ORGNAME AS ORGNAME FROM T_EMC_CSM_STOCK_LOG T "
                + "LEFT JOIN T_CORE_USER A ON T.RECEIVEDBYID = A.ID "
                + "LEFT JOIN T_CORE_ORG B ON T.RECEIVEDBYORGID = B.ID "
                + "JOIN T_EMC_CSM_STOCK S ON S.ID = T.STOCKID JOIN T_EMC_CONSUME C ON C.ID = S.MTLID "
                + "WHERE T.ID = ? AND " + scope("C.CREATEDBYORGID", organizations, values);
        values.add(0, logId);
        return one(sql, values);
    }

    private Sql appendFilters(QueryRequest request, String alias, List<Object> initialValues, String base, String entity) {
        List<Object> values = new ArrayList<Object>(initialValues);
        StringBuilder where = new StringBuilder(base);
        if (!request.getAdvancedPatterns().isEmpty() || request.getMultiPropertyPattern() != null)
            throw new IllegalArgumentException("unsupported consumable query shape");
        for (Map.Entry<String,Object> parameter : request.getMapperParameters().entrySet()) {
            String key = parameter.getKey();
            if ("status_SEQ".equals(key) && "stock".equals(entity)) {
                if (parameter.getValue() != null) appendCondition(where, values, alias, "STATUS", MatchPattern.SEQ, parameter.getValue(), entity);
            } else if ("stockQty_NG".equals(key) && "stock".equals(entity)) {
                if (parameter.getValue() != null) appendCondition(where, values, alias, "STOCKQTY", MatchPattern.NG, parameter.getValue(), entity);
            }
            else throw new IllegalArgumentException("unsupported consumable query parameter: " + key);
        }
        for (FilterParamPattern filter : request.getFilterPatterns()) {
            MatchPattern pattern = MatchPattern.getPatternIfPossible(filter.getMatchPattern());
            if (pattern == null) throw new IllegalArgumentException("unsupported consumable filter pattern");
            for (Map.Entry<String,Object> entry : filter.getMap().entrySet()) appendCondition(where, values, alias, entry.getKey(), pattern, entry.getValue(), entity);
        }
        return new Sql(where.toString(), values);
    }

    private void appendCondition(StringBuilder where, List<Object> values, String alias, String name, MatchPattern pattern, Object value, String entity) {
        String column = column(alias, name, entity);
        String operation = pattern.name();
        if ("ISNULL".equals(operation)) { where.append(" AND ").append(column).append(" IS NULL"); return; }
        if ("ISNOTNULL".equals(operation)) { where.append(" AND ").append(column).append(" IS NOT NULL"); return; }
        if ("SEQ".equals(operation) || "EQ".equals(operation)) {
            where.append(" AND ").append(column).append(value == null ? " IS NULL" : " = ?");
            if (value != null) values.add(value);
            return;
        }
        if ("DIFFER".equals(operation)) {
            where.append(" AND ").append(column).append(value == null ? " IS NOT NULL" : " <> ?");
            if (value != null) values.add(value);
            return;
        }
        if ("NEQ".equals(operation)) { where.append(" AND ").append(column).append(" = ?"); values.add(value); return; }
        if ("NG".equals(operation) || "TG".equals(operation) || "DG".equals(operation)) {
            where.append(" AND ").append(column).append(" > ?"); values.add(value); return;
        }
        if ("NGOE".equals(operation) || "TGOE".equals(operation) || "DGOE".equals(operation)) {
            where.append(" AND ").append(column).append(" >= ?"); values.add(value); return;
        }
        if ("NL".equals(operation) || "TL".equals(operation) || "DL".equals(operation)) {
            where.append(" AND ").append(column).append(" < ?"); values.add(value); return;
        }
        if ("NLOE".equals(operation) || "TLOE".equals(operation)) { where.append(" AND ").append(column).append(" <= ?"); values.add(value); return; }
        if ("DLOE".equals(operation)) { where.append(" AND ").append(column).append(" < ? + 1"); values.add(value); return; }
        if ("DEQ".equals(operation)) {
            if (value == null) { where.append(" AND ").append(column).append(" IS NULL"); return; }
            where.append(" AND ").append(column).append(" IS NOT NULL AND TO_CHAR(").append(column).append(", 'yyyy-MM-dd') = ?");
            values.add(String.valueOf(value)); return;
        }
        if ("TEQ".equals(operation)) {
            if (value == null) { where.append(" AND ").append(column).append(" IS NULL"); return; }
            where.append(" AND ").append(column).append(" IS NOT NULL AND TO_CHAR(").append(column).append(", 'yyyy-MM-dd HH24:mi') = ?");
            values.add(((java.time.LocalDateTime) value).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            return;
        }
        boolean insensitive = "CISC".equals(operation) || "CISE".equals(operation) || "CISB".equals(operation)
                || "CISEQ".equals(operation);
        if ("CISC".equals(operation) || "SC".equals(operation)) {
            where.append(" AND ").append(insensitive ? "UPPER(" + column + ")" : column).append(" LIKE ?");
            values.add("%" + (insensitive ? String.valueOf(value).toUpperCase(Locale.ROOT) : value) + "%"); return;
        }
        if ("CISB".equals(operation) || "SB".equals(operation)) {
            where.append(" AND ").append(insensitive ? "UPPER(" + column + ")" : column).append(" LIKE ?");
            values.add((insensitive ? String.valueOf(value).toUpperCase(Locale.ROOT) : value) + "%"); return;
        }
        if ("CISE".equals(operation) || "SE".equals(operation)) {
            where.append(" AND ").append(insensitive ? "UPPER(" + column + ")" : column).append(" LIKE ?");
            values.add("%" + (insensitive ? String.valueOf(value).toUpperCase(Locale.ROOT) : value)); return;
        }
        if ("CISEQ".equals(operation)) { where.append(" AND UPPER(").append(column).append(") = ?"); values.add(String.valueOf(value).toUpperCase(Locale.ROOT)); return; }
        throw new IllegalArgumentException("unsupported consumable filter pattern: " + operation);
    }

    private String column(String alias, String name, String entity) {
        String key = name == null ? "" : name.toUpperCase(Locale.ROOT);
        String[] fields = "consume".equals(entity)
                ? new String[]{"ID", "MTLNAME", "SPECIFICATION", "STOCKQTY", "MTLCATEGORYID", "HARMFULNESS", "PRODUCTNO", "CREATEDBYID", "CREATEDBYNAME", "CREATEDTIME", "CREATEDBYORGID", "CREATEDBYORGNAME"}
                : "stock".equals(entity)
                    ? new String[]{"ID", "MTLID", "BATCHNO", "STOCKQTY", "UNITNAME", "EXPIREDATE", "LOWLIMIT", "STATUS", "CREATEDBYID", "CREATEDBYNAME", "CREATEDTIME", "CREATEDBYORGID", "CREATEDBYORGNAME"}
                    : new String[]{"ID", "STOCKID", "LOGTYPE", "PREVIOUSQTY", "QTY", "VENDORID", "PRICE", "LOGCONTENT", "RECEIVEDBYID", "RECEIVEDBYORGID", "CREATEDBYID", "CREATEDBYNAME", "CREATEDTIME", "CREATEDBYORGID", "CREATEDBYORGNAME"};
        for (String field : fields) if (field.equals(key)) return alias + "." + field;
        throw new IllegalArgumentException("unsupported consumable filter column: " + name);
    }

    private Map<String,Object> one(String sql, List<Object> values) throws SQLException {
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql)) {
            bind(query, values);
            try (ResultSet rows = query.executeQuery()) { return rows.next() ? row(rows) : null; }
        }
    }

    private Pagination<Map<String,Object>> page(String base, List<Object> values, QueryRequest request) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            int total;
            try (PreparedStatement count = connection.prepareStatement("SELECT COUNT(1) FROM (" + base + ") Q")) {
                bind(count, values);
                try (ResultSet rows = count.executeQuery()) {
                    if (!rows.next()) throw new SQLException("consumable count returned no row");
                    total = rows.getInt(1);
                }
            }
            List<Object> pageValues = new ArrayList<Object>(values);
            long start = ((long) request.getPageNumber() - 1) * request.getPageSize();
            pageValues.add(start); pageValues.add(start + request.getPageSize());
            List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
            Map<String,String> columnTypes = new LinkedHashMap<String,String>();
            String sql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY " + order(request) + ") RN FROM ("
                    + base + ") Q) P WHERE RN > ? AND RN <= ? ORDER BY RN";
            try (PreparedStatement query = connection.prepareStatement(sql)) {
                bind(query, pageValues);
                try (ResultSet result = query.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    for (int i = 1; i <= metadata.getColumnCount(); i++) {
                        String label = metadata.getColumnLabel(i);
                        if (label == null) continue;
                        String normalized = "RN".equalsIgnoreCase(label) ? "rn_" : label.toLowerCase(Locale.ROOT);
                        columnTypes.put(normalized, typeName(metadata.getColumnType(i)));
                    }
                    while (result.next()) rows.add(row(result));
                }
            }
            Pagination<Map<String,Object>> page = new Pagination<Map<String,Object>>(
                    total, request.getPageSize(), request.getPageNumber(), rows);
            page.setColumnTypeMap(columnTypes);
            Map<String,Number> totals = new LinkedHashMap<String,Number>();
            totals.put("qty_", total);
            page.setTotalMap(totals);
            return page;
        }
    }

    private static String typeName(int type) {
        if (type == Types.DATE || type == Types.TIMESTAMP || type == Types.TIMESTAMP_WITH_TIMEZONE) return "dateTime";
        if (type == Types.NUMERIC || type == Types.DECIMAL || type == Types.INTEGER
                || type == Types.BIGINT || type == Types.SMALLINT || type == Types.TINYINT) return "number";
        return "string";
    }

    private String order(QueryRequest request) {
        if (request.getOrderParameters().isEmpty()) return "Q.ID DESC";
        List<String> parts = new ArrayList<String>();
        for (Pair<String,String> pair : request.getOrderParameters()) {
            String field = orderColumn(pair.getFirst());
            parts.add(field + ("desc".equalsIgnoreCase(pair.getSecond()) ? " DESC" : " ASC"));
        }
        return String.join(",", parts);
    }

    private String orderColumn(String name) {
        String key = name == null ? "" : name.toUpperCase(Locale.ROOT);
        if ("ID".equals(key)) return "Q.ID";
        if ("MTLNAME".equals(key) || "NAME".equals(key)) return "Q.MTLNAME";
        if ("STOCKQTY".equals(key)) return "Q.STOCKQTY";
        if ("MTLID".equals(key)) return "Q.MTLID";
        if ("BATCHNO".equals(key)) return "Q.BATCHNO";
        if ("STATUS".equals(key)) return "Q.STATUS";
        if ("STOCKID".equals(key)) return "Q.STOCKID";
        if ("LOGTYPE".equals(key)) return "Q.LOGTYPE";
        throw new IllegalArgumentException("unsupported consumable order column: " + name);
    }

    private void validateOrder(QueryRequest request, String entity) {
        for (Pair<String,String> pair : request.getOrderParameters()) {
            String key = pair.getFirst() == null ? "" : pair.getFirst().toUpperCase(Locale.ROOT);
            boolean supported = "ID".equals(key) || ("consume".equals(entity) && ("MTLNAME".equals(key) || "NAME".equals(key) || "STOCKQTY".equals(key)))
                    || ("stock".equals(entity) && ("MTLID".equals(key) || "BATCHNO".equals(key) || "STOCKQTY".equals(key) || "STATUS".equals(key)))
                    || ("log".equals(entity) && ("STOCKID".equals(key) || "LOGTYPE".equals(key)));
            if (!supported) throw new IllegalArgumentException("unsupported consumable order column: " + pair.getFirst());
        }
    }

    private static String scope(String column, Set<String> organizations, List<Object> values) {
        if (organizations.isEmpty()) return "1 = 0";
        StringBuilder sql = new StringBuilder("(");
        int index = 0;
        for (String organization : organizations) {
            if (index % 1000 == 0) {
                if (index != 0) sql.append(") OR ");
                sql.append(column).append(" IN (");
            } else sql.append(',');
            sql.append('?'); values.add(organization); index++;
        }
        return sql.append("))").toString();
    }

    private static void bind(PreparedStatement statement, List<Object> values) throws SQLException {
        for (int i = 0; i < values.size(); i++) {
            Object value = values.get(i);
            if (value instanceof java.time.LocalDate) value = java.sql.Date.valueOf((java.time.LocalDate) value);
            else if (value instanceof java.time.LocalDateTime) value = java.sql.Timestamp.valueOf((java.time.LocalDateTime) value);
            statement.setObject(i + 1, value);
        }
    }

    private static Map<String,Object> row(ResultSet rows) throws SQLException {
        Map<String,Object> result = new LinkedHashMap<String,Object>();
        ResultSetMetaData metadata = rows.getMetaData();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String name = metadata.getColumnLabel(i);
            if (!"RN".equals(name)) result.put(name, JdbcValue.normalize(rows.getObject(i)));
        }
        return result;
    }

    private static final class Sql {
        private final String sql;
        private final List<Object> values;
        private Sql(String sql, List<Object> values) { this.sql = sql; this.values = values; }
    }
}
