package com.sunwayworld.owned.internal.data;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSON;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.AdvancedFilterPattern;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.MultiPropertyFilterPattern;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentity;
import com.sunwayworld.owned.internal.web.QueryRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Set;
import javax.sql.DataSource;

/**
 * Owned, read-only equipment query seam.
 *
 * The category permission join follows the recovered equipment mapper.  The
 * caller can provide a parsed request, but identity, menu permission and
 * organization scope are always read from the trusted identity boundary.
 */
public final class EquipmentQueryService {
    private final DataSource dataSource;
    private final JdbcPersonnelIdentity identity;

    public EquipmentQueryService(DataSource dataSource, JdbcPersonnelIdentity identity) {
        if (dataSource == null || identity == null) throw new IllegalArgumentException("query dependencies required");
        this.dataSource = dataSource;
        this.identity = identity;
    }

    public Pagination<Map<String, Object>> list(QueryRequest request, JdbcPersonnelIdentity.Principal principal) throws SQLException {
        if (request == null || principal == null) throw new IllegalArgumentException("request and principal required");
        identity.authorizeEquipment(principal);
        Set<String> organizations = identity.permittedOrganizationIds(principal);
        if (organizations.isEmpty()) return empty(request);
        Sql sql = build(request, organizations, false, null);
        try (Connection connection = dataSource.getConnection()) {
            int total;
            try (PreparedStatement count = connection.prepareStatement(sql.countSql)) {
                bind(count, sql.countValues);
                try (ResultSet rows = count.executeQuery()) {
                    if (!rows.next()) throw new SQLException("equipment count returned no row");
                    total = rows.getInt(1);
                }
            }
            List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
            try (PreparedStatement query = connection.prepareStatement(sql.pageSql)) {
                bind(query, sql.pageValues);
                try (ResultSet rows = query.executeQuery()) {
                    while (rows.next()) values.add(row(rows));
                }
            }
            return new Pagination<Map<String, Object>>(total, request.getPageSize(), request.getPageNumber(), values);
        }
    }

    public Map<String, Object> detail(Long id, JdbcPersonnelIdentity.Principal principal) throws SQLException {
        if (id == null || principal == null) throw new IllegalArgumentException("id and principal required");
        identity.authorizeEquipment(principal);
        Set<String> organizations = identity.permittedOrganizationIds(principal);
        if (organizations.isEmpty()) return null;
        Sql sql = build(null, organizations, true, id);
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql.pageSql)) {
            bind(query, sql.pageValues);
            try (ResultSet rows = query.executeQuery()) { return rows.next() ? row(rows) : null; }
        }
    }

    private Pagination<Map<String, Object>> empty(QueryRequest request) {
        return new Pagination<Map<String, Object>>(0, request.getPageSize(), request.getPageNumber(), Collections.<Map<String, Object>>emptyList());
    }

    private Sql build(QueryRequest request, Set<String> organizations, boolean detail, Long id) {
        List<Object> values = new ArrayList<Object>();
        StringBuilder where = new StringBuilder(" WHERE EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION OP_ WHERE OP_.PERMISSIONTYPEID = ? AND OP_.TARGETID = E.EQUIPTCATEGORYID AND OP_.ORGID IN (");
        values.add("T_EMC_EQUIPT_CATEGORY");
        appendPlaceholders(where, organizations.size());
        where.append(") )");
        values.addAll(organizations);
        if (detail) { where.append(" AND E.ID = ?"); values.add(id); }
        if (request != null) appendFilters(request, where, values);
        String select = "SELECT E.* ,"
                + "C.ORGNAME AS USEDDEPARTMENTNAME,O.ORGNAME AS USEDDEPOSTNAME,U.USERNAME AS MAINTAINUSERNAME,"
                + "S.USERNAME AS KEEPUSERNAME,M.EQUIPTCATEGORY AS EQUIPTCATEGORYNAME "
                + "FROM T_EMC_EQUIPT E LEFT JOIN T_CORE_ORG C ON E.USEDDEPARTMENTID = C.ID "
                + "LEFT JOIN T_CORE_ORG O ON E.USEDDEPOSTID = O.ID LEFT JOIN T_CORE_USER U ON E.MAINTAINUSERID = U.ID "
                + "LEFT JOIN T_CORE_USER S ON E.KEEPUSERID = S.ID LEFT JOIN T_EMC_EQUIPT_CATEGORY M ON E.EQUIPTCATEGORYID = M.ID";
        String base = select + where;
        if (detail) return new Sql(base, Collections.<Object>emptyList(), base, values);
        String order = order(request);
        String countSql = "SELECT COUNT(1) FROM (" + base + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY "
                + order + ") RN FROM (" + base + ") Q) P WHERE RN > ? AND RN <= ?";
        List<Object> pageValues = new ArrayList<Object>(values);
        pageValues.add((request.getPageNumber() - 1) * request.getPageSize());
        pageValues.add(request.getPageNumber() * request.getPageSize());
        return new Sql(countSql, values, pageSql, pageValues);
    }

    private void appendFilters(QueryRequest request, StringBuilder where, List<Object> values) {
        validateMapperParameters(request);
        for (Map.Entry<String, Object> entry : request.getMapperParameters().entrySet()) {
            if ("orgid_seq".equals(entry.getKey())) {
                where.append(" AND E.ORGID = ?");
                values.add(entry.getValue());
            } else if ("testMethodId".equalsIgnoreCase(entry.getKey())) {
                List<String> ids = csvIds(entry.getValue());
                where.append(" AND EXISTS (SELECT 1 FROM T_EMC_EQUIPT_TEST_METHOD TM WHERE TM.EQUIPTID=E.ID AND TM.TESTMETHODID IN (");
                appendPlaceholders(where, ids.size());
                where.append("))");
                values.addAll(ids);
            } else if ("target_filter".equalsIgnoreCase(entry.getKey())) {
                appendTargetFilter(where, values, entry.getValue());
            } else if ("maintenanceUser_assigned".equalsIgnoreCase(entry.getKey())) {
                if (!"1".equals(String.valueOf(entry.getValue()))) throw new IllegalArgumentException("unsupported maintenance assignment value");
                where.append(" AND E.MAINTAINUSERID IS NOT NULL");
            } else if ("maintenanceUserId".equalsIgnoreCase(entry.getKey())) {
                String value = String.valueOf(entry.getValue());
                if (value.length() == 0 || value.length() > 64) throw new IllegalArgumentException("invalid maintenance user id");
                where.append(" AND E.MAINTAINUSERID=?"); values.add(value);
            }
        }
        for (FilterParamPattern filter : request.getFilterPatterns()) {
            MatchPattern pattern = MatchPattern.getPatternIfPossible(filter.getMatchPattern());
            if (pattern == null) throw new IllegalArgumentException("unknown filter pattern");
            for (Map.Entry<String, Object> entry : filter.getMap().entrySet()) appendCondition(where, values, entry.getKey(), pattern, entry.getValue());
        }
        if (!request.getAdvancedPatterns().isEmpty()) {
            where.append(" AND (");
            boolean first = true;
            for (AdvancedFilterPattern filter : request.getAdvancedPatterns()) {
                if (!first) where.append(' ').append(link(filter.isGroupStart() ? filter.getGroupLink() : filter.getIngroupLink())).append(' ');
                if (filter.isGroupStart()) where.append('(');
                StringBuilder condition = new StringBuilder();
                appendCondition(condition, values, filter.getName(), MatchPattern.getPatternIfPossible(filter.getMatchPattern()), filter.getValue());
                where.append(condition.substring(5));
                if (filter.isGroupEnd()) where.append(')');
                first = false;
            }
            where.append(')');
        }
        MultiPropertyFilterPattern multi = request.getMultiPropertyPattern();
        if (multi != null) {
            StringBuilder group = new StringBuilder(" AND (");
            String[] names = multi.getNames();
            for (int i = 0; i < names.length; i++) {
                if (i > 0) group.append(" OR ");
                group.append(column(names[i], !multi.getCaseSensitive())).append(" LIKE ?");
                values.add("%" + String.valueOf(multi.getValue()) + "%");
            }
            where.append(group).append(')');
        }
    }

    private void appendCondition(StringBuilder where, List<Object> values, String name, MatchPattern pattern, Object value) {
        if (pattern == null) throw new IllegalArgumentException("unknown filter pattern");
        String column = column(name, pattern == MatchPattern.CISC || pattern == MatchPattern.CISEQ || pattern == MatchPattern.CISB || pattern == MatchPattern.CISE);
        String p = pattern.name();
        if ("CISC".equals(p) || "SC".equals(p)) { where.append(" AND ").append(column).append(" LIKE ?"); values.add(pattern == MatchPattern.CISC ? "%" + String.valueOf(value).toUpperCase(Locale.ROOT) + "%" : "%" + value + "%"); return; }
        if ("CISB".equals(p) || "SB".equals(p)) { where.append(" AND ").append(column).append(" LIKE ?"); values.add(pattern == MatchPattern.CISB ? String.valueOf(value).toUpperCase(Locale.ROOT) + "%" : String.valueOf(value) + "%"); return; }
        if ("CISE".equals(p) || "SE".equals(p)) { where.append(" AND ").append(column).append(" LIKE ?"); values.add(pattern == MatchPattern.CISE ? "%" + String.valueOf(value).toUpperCase(Locale.ROOT) : "%" + value); return; }
        if ("CISEQ".equals(p)) { where.append(" AND ").append(column).append(" = ?"); values.add(value == null ? null : String.valueOf(value).toUpperCase(Locale.ROOT)); return; }
        if ("SEQ".equals(p) || "EQ".equals(p)) { where.append(" AND ").append(column).append(value == null ? " IS NULL" : " = ?"); if (value != null) values.add(value); return; }
        if ("DIFFER".equals(p)) { where.append(" AND ").append(column).append(value == null ? " IS NOT NULL" : " <> ?"); if (value != null) values.add(value); return; }
        if ("ISNULL".equals(p)) { where.append(" AND ").append(column).append(" IS NULL"); return; }
        if ("ISNOTNULL".equals(p)) { where.append(" AND ").append(column).append(" IS NOT NULL"); return; }
        if ("DG".equals(p)) { where.append(" AND ").append(column).append(" > TO_DATE(?, 'yyyy-MM-dd')"); values.add(String.valueOf(value)); return; }
        if ("DGOE".equals(p)) { where.append(" AND ").append(column).append(" >= TO_DATE(?, 'yyyy-MM-dd')"); values.add(String.valueOf(value)); return; }
        if ("DL".equals(p)) { where.append(" AND ").append(column).append(" < TO_DATE(?, 'yyyy-MM-dd')"); values.add(String.valueOf(value)); return; }
        if ("DLOE".equals(p)) { where.append(" AND ").append(column).append(" < TO_DATE(?, 'yyyy-MM-dd') + 1"); values.add(String.valueOf(value)); return; }
        if ("DEQ".equals(p) && value == null) { where.append(" AND ").append(column).append(" IS NULL"); return; }
        if ("DEQ".equals(p)) { where.append(" AND ").append(column).append(" IS NOT NULL AND TO_CHAR(").append(column).append(", 'yyyy-MM-dd') = ?"); values.add(String.valueOf(value)); return; }
        if ("TG".equals(p) || "NG".equals(p)) { where.append(" AND ").append(column).append(" > ?"); values.add(value); return; }
        if ("NGOE".equals(p) || "TGOE".equals(p)) { where.append(" AND ").append(column).append(" >= ?"); values.add(value); return; }
        if ("NL".equals(p) || "TL".equals(p)) { where.append(" AND ").append(column).append(" < ?"); values.add(value); return; }
        if ("NLOE".equals(p) || "TLOE".equals(p)) { where.append(" AND ").append(column).append(" <= ?"); values.add(value); return; }
        if ("NEQ".equals(p)) { where.append(" AND ").append(column).append(" = ?"); values.add(value); return; }
        if ("TEQ".equals(p) && value == null) { where.append(" AND ").append(column).append(" IS NULL"); return; }
        if ("TEQ".equals(p)) { where.append(" AND ").append(column).append(" IS NOT NULL AND TO_CHAR(").append(column).append(", 'yyyy-MM-dd HH24:mi') = ?"); values.add(((java.time.LocalDateTime) value).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))); return; }
        throw new IllegalArgumentException("unsupported equipment filter pattern: " + p);
    }

    private void validateMapperParameters(QueryRequest request) {
        for (Map.Entry<String, Object> entry : request.getMapperParameters().entrySet()) {
            if (!"target_type".equals(entry.getKey()) && !"orgid_seq".equals(entry.getKey())
                    && !"testMethodId".equalsIgnoreCase(entry.getKey())
                    && !"target_filter".equalsIgnoreCase(entry.getKey())
                    && !"maintenanceUser_assigned".equalsIgnoreCase(entry.getKey())
                    && !"maintenanceUserId".equalsIgnoreCase(entry.getKey())) {
                throw new IllegalArgumentException("unsupported equipment query parameter: " + entry.getKey());
            }
            if ("target_type".equals(entry.getKey()) && entry.getValue() != null && !"equipList".equals(String.valueOf(entry.getValue()))) {
                throw new IllegalArgumentException("unsupported equipment target_type: " + entry.getValue());
            }
        }
    }

    private static String link(String value) {
        if (value == null) return "AND";
        if (!"AND".equals(value) && !"OR".equals(value)) throw new IllegalArgumentException("unsupported filter link");
        return value;
    }

    private String column(String name, boolean upper) {
        String key = name == null ? "" : name.toUpperCase(Locale.ROOT);
        Map<String, String> columns = new HashMap<String, String>();
        columns.put("ID", "E.ID"); columns.put("EQUIPTID", "E.EQUIPTID"); columns.put("NAME", "E.EQUIPTNAME");
        columns.put("EQUIPTNAME", "E.EQUIPTNAME"); columns.put("SPECIFICATION", "E.SPECIFICATION"); columns.put("FACTORYNO", "E.FACTORYNO");
        columns.put("MANUFACTURER", "E.MANUFACTURER"); columns.put("STATUS", "E.STATUS"); columns.put("EQUIPTCATEGORYID", "E.EQUIPTCATEGORYID");
        columns.put("USEDDEPARTMENTID", "E.USEDDEPARTMENTID"); columns.put("USEDDEPARTMENTNAME", "C.ORGNAME");
        columns.put("USEDDEPOSTID", "E.USEDDEPOSTID"); columns.put("USEDDEPOSTNAME", "O.ORGNAME");
        columns.put("SHARING", "E.SHARING"); columns.put("MAINTAIN", "E.MAINTAIN"); columns.put("SOURCEMETHOD", "E.SOURCEMETHOD");
        columns.put("EQUIPTBELONG", "E.EQUIPTBELONG"); columns.put("SOURCEDATE", "E.SOURCEDATE");
        columns.put("MAINTAINUSERID", "E.MAINTAINUSERID"); columns.put("KEEPUSERID", "E.KEEPUSERID");
        String result = columns.get(key);
        if (result == null) throw new IllegalArgumentException("unsupported equipment filter column: " + name);
        if (upper && ("E.EQUIPTID".equals(result) || "E.EQUIPTNAME".equals(result) || "E.SPECIFICATION".equals(result)
                || "E.FACTORYNO".equals(result) || "E.MANUFACTURER".equals(result) || "C.ORGNAME".equals(result) || "O.ORGNAME".equals(result))) {
            return "UPPER(" + result + ")";
        }
        return result;
    }

    private String order(QueryRequest request) {
        if (request == null || request.getOrderParameters().isEmpty()) return "Q.ID DESC";
        List<String> parts = new ArrayList<String>();
        request.getOrderParameters().forEach(pair -> parts.add(orderColumn(pair.getFirst()) + ("desc".equalsIgnoreCase(pair.getSecond()) ? " DESC" : " ASC") + " NULLS LAST"));
        return String.join(",", parts);
    }

    private String orderColumn(String name) {
        String key = name == null ? "" : name.toUpperCase(Locale.ROOT);
        Map<String, String> columns = new HashMap<String, String>();
        columns.put("ID", "Q.ID"); columns.put("EQUIPTID", "Q.EQUIPTID"); columns.put("EQUIPTNAME", "Q.EQUIPTNAME");
        columns.put("SPECIFICATION", "Q.SPECIFICATION"); columns.put("STATUS", "Q.STATUS"); columns.put("EQUIPTCATEGORYID", "Q.EQUIPTCATEGORYID");
        columns.put("SOURCEDATE", "Q.SOURCEDATE");
        String result = columns.get(key);
        if (result == null) throw new IllegalArgumentException("unsupported equipment order column: " + name);
        return result;
    }

    private static void appendPlaceholders(StringBuilder sql, int count) { for (int i = 0; i < count; i++) { if (i > 0) sql.append(','); sql.append('?'); } }
    private static List<String> csvIds(Object raw) {
        String text = raw == null ? "" : String.valueOf(raw).trim();
        if (text.isEmpty()) throw new IllegalArgumentException("test method id is required");
        String[] parts = text.split(",");
        if (parts.length == 0 || parts.length > 100) throw new IllegalArgumentException("test method ids are invalid");
        List<String> ids = new ArrayList<String>();
        for (String part : parts) {
            String value = part.trim();
            try { if (Long.parseLong(value) < 1L) throw new NumberFormatException(); }
            catch (NumberFormatException failure) { throw new IllegalArgumentException("test method ids are invalid"); }
            ids.add(value);
        }
        return ids;
    }

    private static void appendTargetFilter(StringBuilder where, List<Object> values, Object raw) {
        JSONObject filter;
        try { filter = JSON.parseObject(String.valueOf(raw)); }
        catch (RuntimeException failure) { throw new IllegalArgumentException("invalid target filter"); }
        if (!"assigned".equals(filter.getString("type"))
                || !"T_EMC_EQUIPT_TEST_METHOD".equalsIgnoreCase(filter.getString("targetTable"))
                || !"EQUIPTID".equalsIgnoreCase(filter.getString("targetMatchColumn"))) {
            throw new IllegalArgumentException("unsupported target filter");
        }
        JSONArray children = filter.getJSONArray("filter");
        if (children == null || children.isEmpty() || children.size() > 10)
            throw new IllegalArgumentException("target filter is empty");
        where.append(" AND EXISTS (SELECT 1 FROM T_EMC_EQUIPT_TEST_METHOD TF WHERE TF.EQUIPTID=E.ID");
        for (Object child : children) {
            if (!(child instanceof JSONObject)) throw new IllegalArgumentException("invalid target filter entry");
            JSONObject item = (JSONObject) child;
            String column = item.getString("targetFilterColumn");
            if ("testMethodId".equalsIgnoreCase(column)) {
                List<String> ids = csvIds(item.get("targetFilterValue"));
                where.append(" AND TF.TESTMETHODID IN ("); appendPlaceholders(where, ids.size()); where.append(')'); values.addAll(ids);
            } else if ("ANALYSISCATEGORY".equalsIgnoreCase(column)) {
                String value = item.getString("targetFilterValue");
                if (value == null || value.length() > 64) throw new IllegalArgumentException("invalid analysis category filter");
                where.append(" AND TF.ANALYSISCATEGORY=?"); values.add(value);
            } else {
                throw new IllegalArgumentException("unsupported target filter column: " + column);
            }
        }
        where.append(')');
    }
    private static void bind(PreparedStatement statement, List<Object> values) throws SQLException { for (int i = 0; i < values.size(); i++) {
        Object value = values.get(i);
        if (value instanceof java.time.LocalDate) value = java.sql.Date.valueOf((java.time.LocalDate) value);
        else if (value instanceof java.time.LocalDateTime) value = java.sql.Timestamp.valueOf((java.time.LocalDateTime) value);
        statement.setObject(i + 1, value);
    } }
    private static Map<String, Object> row(ResultSet rows) throws SQLException { Map<String, Object> result = new LinkedHashMap<String, Object>(); ResultSetMetaData meta = rows.getMetaData(); for (int i = 1; i <= meta.getColumnCount(); i++) if (!"RN".equals(meta.getColumnLabel(i))) result.put(meta.getColumnLabel(i), JdbcValue.normalize(rows.getObject(i))); return result; }
    private static final class Sql { final String countSql; final List<Object> countValues; final String pageSql; final List<Object> pageValues; Sql(String c,List<Object>cv,String p,List<Object>pv){countSql=c;countValues=cv;pageSql=p;pageValues=pv;} }
}
