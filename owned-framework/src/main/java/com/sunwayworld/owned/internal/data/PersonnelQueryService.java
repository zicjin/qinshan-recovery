package com.sunwayworld.owned.internal.data;

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
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Set;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.sql.DataSource;

/**
 * Owned read-only personnel query seam. It deliberately accepts only the
 * trusted principal and parsed request; caller input never supplies SQL.
 */
public final class PersonnelQueryService {
    private final DataSource dataSource;
    private final JdbcPersonnelIdentity identity;

    public PersonnelQueryService(DataSource dataSource, JdbcPersonnelIdentity identity) {
        if (dataSource == null || identity == null) throw new IllegalArgumentException("query dependencies required");
        this.dataSource = dataSource;
        this.identity = identity;
    }

    public Pagination<Map<String, Object>> list(QueryRequest request, JdbcPersonnelIdentity.Principal principal) throws SQLException {
        if (request == null || principal == null) throw new IllegalArgumentException("request and principal required");
        identity.authorizePersonnel(principal);
        Set<String> organizations = identity.permittedOrganizationIds(principal);
        if (organizations.isEmpty()) return new Pagination<Map<String, Object>>(0, request.getPageSize(), request.getPageNumber(), Collections.<Map<String, Object>>emptyList());
        Sql sql = build(request, organizations, false, null);
        try (Connection connection = dataSource.getConnection()) {
            int total;
            try (PreparedStatement count = connection.prepareStatement(sql.countSql)) {
                bind(count, sql.countValues);
                try (ResultSet rows = count.executeQuery()) { rows.next(); total = rows.getInt(1); }
            }
            List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
            Map<String, String> columnTypes = new LinkedHashMap<String, String>();
            try (PreparedStatement query = connection.prepareStatement(sql.pageSql)) {
                bind(query, sql.pageValues);
                try (ResultSet rows = query.executeQuery()) {
                    ResultSetMetaData metadata = rows.getMetaData();
                    for (int i = 1; i <= metadata.getColumnCount(); i++) {
                        String label = metadata.getColumnLabel(i);
                        if (label != null) {
                            String normalizedLabel = "RN".equalsIgnoreCase(label) ? "rn_"
                                    : ("PARENTORGNAME".equalsIgnoreCase(label)
                                    ? "orgname" : label.toLowerCase(Locale.ROOT));
                            columnTypes.put(normalizedLabel, typeName(metadata.getColumnType(i)));
                        }
                    }
                    while (rows.next()) values.add(row(rows));
                }
            }
            Pagination<Map<String, Object>> page = new Pagination<Map<String, Object>>(
                    total, request.getPageSize(), request.getPageNumber(), values);
            page.setColumnTypeMap(columnTypes);
            return page;
        }
    }

    public Map<String, Object> detail(String id, JdbcPersonnelIdentity.Principal principal) throws SQLException {
        if (id == null || id.trim().isEmpty() || principal == null) throw new IllegalArgumentException("id and principal required");
        identity.authorizePersonnel(principal);
        Set<String> organizations = identity.permittedOrganizationIds(principal);
        if (organizations.isEmpty()) return null;
        Sql sql = build(null, organizations, true, id);
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql.pageSql)) {
            bind(query, sql.pageValues);
            try (ResultSet rows = query.executeQuery()) { return rows.next() ? row(rows) : null; }
        }
    }

    private Sql build(QueryRequest request, Set<String> organizations, boolean detail, String id) {
        List<Object> values = new ArrayList<Object>();
        StringBuilder where = new StringBuilder(" WHERE A.ORGID IN (");
        appendPlaceholders(where, organizations.size()); where.append(')');
        values.addAll(organizations);
        if (detail) { where.append(" AND A.ID = ?"); values.add(id); }
        if (request != null) appendFilters(request, where, values);
        String select = detail
                ? "SELECT A.*,B.ORGNAME AS DEPARTMENTNAME,C.ORGNAME AS PARENTORGNAME FROM T_EMC_EMP A "
                    + "LEFT JOIN T_CORE_USER B ON A.ID=B.ID LEFT JOIN T_CORE_ORG C ON B.PARENTORGID=C.ID"
                : "SELECT A.*,B.ORGID AS DEPARTMENTID,B.ORGNAME AS DEPARTMENTNAME,B.STATUS,B.PARENTORGID,"
                    + "C.ID AS USERORGID,C.ORGNAME AS PARENTORGNAME,D.SAMPLEDEPARTMENT FROM T_EMC_EMP A "
                    + "LEFT JOIN T_CORE_USER B ON A.ID=B.ID LEFT JOIN T_CORE_ORG C ON B.PARENTORGID=C.ID "
                    + "LEFT JOIN T_CORE_ORG D ON B.ORGID=D.ID";
        String base = select + where;
        if (detail) return new Sql(base, Collections.<Object>emptyList(), base, values);
        String order = order(request);
        String countSql = "SELECT COUNT(1) FROM (" + base + ") Q";
        String pageSql = "SELECT P.* FROM ("
                + "SELECT Q.*,ROW_NUMBER() OVER (ORDER BY " + order + ") RN FROM (" + base + ") Q) P WHERE RN > ? AND RN <= ?";
        List<Object> pageValues = new ArrayList<Object>(values);
        pageValues.add((request.getPageNumber() - 1) * request.getPageSize());
        pageValues.add(request.getPageNumber() * request.getPageSize());
        return new Sql(countSql, values, pageSql, pageValues);
    }

    private void appendFilters(QueryRequest request, StringBuilder where, List<Object> values) {
        for (Map.Entry<String, Object> entry : request.getMapperParameters().entrySet()) {
            String key = entry.getKey();
            if ("departmentId_filter".equals(key)) {
                if (entry.getValue() != null) {
                    where.append(" AND (B.ORGID = ? OR B.ORGID IN (SELECT ID FROM T_CORE_ORG WHERE PARENTID = ?))");
                    values.add(entry.getValue()); values.add(entry.getValue());
                }
            } else if ("overdueFilter".equals(key)) {
                appendOverdueFilter(where, entry.getValue(), "T_EMC_EMP_QUAL");
            } else if ("remind".equals(key)) {
                appendOverdueFilter(where, entry.getValue(), "T_EMC_CERT");
            } else if ("planSampleDate".equalsIgnoreCase(key)) {
                // The sample-person chooser consumes this route-specific filter
                // while enriching the already scoped employee page from folders.
                // It must not widen or alter the personnel SQL query itself.
            } else if ("targetType".equals(key)) {
                String target = entry.getValue() == null ? "" : String.valueOf(entry.getValue());
                if (!("samplePlan".equals(target) || "analysisCheck".equals(target)
                        || "analysisUser".equals(target) || "insertDirector".equals(target)
                        || "changeSampleUser".equals(target))) {
                    throw new IllegalArgumentException("unsupported personnel chooser targetType: " + target);
                }
            } else if ("sampleOrgId".equals(key) || "analysisUserId".equals(key)) {
                // Applied after targetType validation below as bound IDs.
            } else if ("targetFilter".equals(key)) {
                appendTargetFilter(where, values, entry.getValue());
            } else if ("emc_role_assigned".equals(key)) {
                where.append(" AND EXISTS (SELECT 1 FROM T_CORE_ROLE_USER R WHERE R.ROLEID = ? AND R.USERID = A.ID)");
                values.add(entry.getValue());
            } else if ("status_DIFFER".equals(key)) {
                where.append(" AND (B.STATUS IS NULL OR B.STATUS <> ?)");
                values.add(entry.getValue());
            } else if (key.endsWith("_SEQ")) {
                appendCondition(where, values, key.substring(0, key.length() - 4), MatchPattern.SEQ, entry.getValue());
            } else {
                throw new IllegalArgumentException("unsupported personnel query parameter: " + key);
            }
        }
        for (FilterParamPattern filter : request.getFilterPatterns()) {
            MatchPattern pattern = MatchPattern.getPatternIfPossible(filter.getMatchPattern());
            if (pattern == null) throw new IllegalArgumentException("unknown filter pattern");
            where.append(" AND (");
            boolean first = true;
            String separator = isDisjunction(pattern) ? " OR " : " AND ";
            for (Map.Entry<String, Object> entry : filter.getMap().entrySet()) {
                if (!first) where.append(separator);
                StringBuilder condition = new StringBuilder();
                appendCondition(condition, values, entry.getKey(), pattern, entry.getValue());
                where.append(condition.substring(5));
                first = false;
            }
            if (first) where.append("1 = 2");
            where.append(')');
        }
        if (!request.getAdvancedPatterns().isEmpty()) {
            where.append(" AND (");
            boolean first = true;
            for (AdvancedFilterPattern filter : request.getAdvancedPatterns()) {
                if (!first) where.append(" ").append(filter.isGroupStart() ? link(filter.getGroupLink()) : link(filter.getIngroupLink())).append(" ");
                if (filter.isGroupStart()) where.append("(");
                StringBuilder condition = new StringBuilder();
                appendCondition(condition, values, filter.getName(), MatchPattern.getPatternIfPossible(filter.getMatchPattern()), filter.getValue());
                where.append(condition.substring(5));
                if (filter.isGroupEnd()) where.append(")");
                first = false;
            }
            where.append(")");
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
        appendChooserFilter(request, where, values);
    }

    private static void appendChooserFilter(QueryRequest request, StringBuilder where, List<Object> values) {
        Object targetRaw = request.getMapperParameters().get("targetType");
        if (targetRaw == null) return;
        String target = String.valueOf(targetRaw);
        if ("samplePlan".equals(target)) {
            Object sampleOrg = request.getMapperParameters().get("sampleOrgId");
            if (sampleOrg == null || String.valueOf(sampleOrg).trim().isEmpty()) {
                throw new IllegalArgumentException("sampleOrgId is required for samplePlan chooser");
            }
            where.append(" AND EXISTS (SELECT 1 FROM T_CORE_USER_PERMISSION F WHERE F.USERID = A.ID AND F.TARGETID = ?)");
            values.add(sampleOrg);
        } else if ("analysisCheck".equals(target)) {
            Object analysisUser = request.getMapperParameters().get("analysisUserId");
            if (analysisUser == null || String.valueOf(analysisUser).trim().isEmpty()) {
                throw new IllegalArgumentException("analysisUserId is required for analysisCheck chooser");
            }
            where.append(" AND A.ID <> ? AND EXISTS (SELECT 1 FROM T_CORE_ORG F WHERE B.ORGID = F.ID AND F.ORGNAME LIKE ?)");
            values.add(analysisUser); values.add("分析%");
        } else if ("analysisUser".equals(target)) {
            where.append(" AND EXISTS (SELECT 1 FROM T_CORE_ORG F WHERE B.ORGID = F.ID AND (F.ORGNAME = ? OR EXISTS (SELECT 1 FROM T_CORE_ORG P WHERE F.PARENTID = P.ID AND P.ORGNAME = ?)))");
            values.add("检测部"); values.add("检测部");
        }
    }

    private static void appendTargetFilter(StringBuilder where, List<Object> values, Object raw) {
        if (!(raw instanceof Map<?, ?>)) throw new IllegalArgumentException("targetFilter must be an object");
        Map<?, ?> filter = (Map<?, ?>) raw;
        if (!"insertDirector".equals(String.valueOf(filter.get("type")))
                || !"T_EMC_PROJECT_DIRECTOR".equals(String.valueOf(filter.get("targetTable")))
                || !"EMPID".equals(String.valueOf(filter.get("targetMatchColumn")))
                || !"ID".equals(String.valueOf(filter.get("thisMatchColumn")))) {
            throw new IllegalArgumentException("unsupported personnel targetFilter");
        }
        Object nested = filter.get("targetFilter");
        if (!(nested instanceof Map<?, ?>) || ((Map<?, ?>) nested).get("PROJECTID") == null) {
            throw new IllegalArgumentException("project targetFilter is required");
        }
        where.append(" AND NOT EXISTS (SELECT 1 FROM T_EMC_PROJECT_DIRECTOR D WHERE D.EMPID = A.ID AND D.PROJECTID = ?)");
        values.add(((Map<?, ?>) nested).get("PROJECTID"));
    }

    private static void appendOverdueFilter(StringBuilder where, Object rawValue, String table) {
        String value = rawValue == null ? "" : String.valueOf(rawValue).trim();
        if (value.isEmpty() || "0".equals(value)) return;
        if (!"1".equals(value) && !"overdue".equalsIgnoreCase(value)) {
            throw new IllegalArgumentException("unsupported overdue filter value: " + value);
        }
        where.append(" AND EXISTS (SELECT 1 FROM ").append(table).append(" R")
                .append(" WHERE R.EXPIREDATE <= SYSDATE + R.WARNINGDAYS AND R.EMPID = A.ID)");
    }

    private void appendCondition(StringBuilder where, List<Object> values, String name, MatchPattern pattern, Object value) {
        if (pattern == null) throw new IllegalArgumentException("unknown filter pattern");
        String patternName = pattern.name();
        String column = column(name, pattern == MatchPattern.CISC || pattern == MatchPattern.CISEQ
                || pattern == MatchPattern.CISB || pattern == MatchPattern.CISE || patternName.startsWith("OCI"));
        String p = patternName;
        if ("CISC".equals(p) || "SC".equals(p) || "OSC".equals(p) || "OCISC".equals(p)) { where.append(" AND ").append(column).append(" LIKE ?"); values.add((p.startsWith("OCI") || "CISC".equals(p) ? "%" + String.valueOf(value).toUpperCase(Locale.ROOT) + "%" : "%" + value + "%")); return; }
        if ("CISB".equals(p) || "SB".equals(p) || "OSB".equals(p) || "OCISB".equals(p)) { where.append(" AND ").append(column).append(" LIKE ?"); values.add((p.startsWith("OCI") || "CISB".equals(p) ? String.valueOf(value).toUpperCase(Locale.ROOT) + "%" : String.valueOf(value) + "%")); return; }
        if ("CISE".equals(p) || "SE".equals(p) || "OSE".equals(p) || "OCISE".equals(p)) { where.append(" AND ").append(column).append(" LIKE ?"); values.add((p.startsWith("OCI") || "CISE".equals(p) ? "%" + String.valueOf(value).toUpperCase(Locale.ROOT) : "%" + value)); return; }
        if ("NSC".equals(p)) { where.append(" AND ( ").append(column).append(" NOT LIKE ? OR ").append(column).append(" IS NULL )"); values.add("%" + value + "%"); return; }
        if ("DIFFER".equals(p)) { where.append(" AND ").append(column).append(value == null ? " IS NOT NULL" : " <> ?"); if (value != null) values.add(value); return; }
        if ("ISNULL".equals(p)) { where.append(" AND ").append(column).append(" IS NULL"); return; }
        if ("ISNOTNULL".equals(p)) { where.append(" AND ").append(column).append(" IS NOT NULL"); return; }
        if ("DG".equals(p)) { where.append(" AND ").append(column).append(" > TO_DATE(?, 'yyyy-MM-dd')"); values.add(String.valueOf(value)); return; }
        if ("DGOE".equals(p)) { where.append(" AND ").append(column).append(" >= TO_DATE(?, 'yyyy-MM-dd')"); values.add(String.valueOf(value)); return; }
        if ("NG".equals(p) || "TG".equals(p)) { where.append(" AND ").append(column).append(" > ?"); values.add(value); return; }
        if ("NGOE".equals(p) || "TGOE".equals(p)) { where.append(" AND ").append(column).append(" >= ?"); values.add(value); return; }
        if ("DL".equals(p)) { where.append(" AND ").append(column).append(" < TO_DATE(?, 'yyyy-MM-dd')"); values.add(String.valueOf(value)); return; }
        if ("DLOE".equals(p)) { where.append(" AND ").append(column).append(" < TO_DATE(?, 'yyyy-MM-dd') + 1"); values.add(String.valueOf(value)); return; }
        if ("NL".equals(p) || "TL".equals(p)) { where.append(" AND ").append(column).append(" < ?"); values.add(value); return; }
        if ("NLOE".equals(p) || "TLOE".equals(p)) { where.append(" AND ").append(column).append(" <= ?"); values.add(value); return; }
        if ("DEQ".equals(p)) { where.append(" AND ").append(value == null ? column + " IS NULL" : "TO_CHAR(" + column + ", 'yyyy-MM-dd') = ?"); if (value != null) values.add(String.valueOf(value)); return; }
        if ("TEQ".equals(p)) { where.append(" AND ").append(value == null ? column + " IS NULL" : "TO_CHAR(" + column + ", 'yyyy-MM-dd HH24:mi') = ?"); if (value != null) values.add(((LocalDateTime) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))); return; }
        if ("NEQ".equals(p)) { where.append(" AND ").append(column).append(" = ?"); values.add(value); return; }
        if ("CBM".equals(p)) { where.append(" AND ").append(column).append(" LIKE ?"); values.add("%" + value + "%"); return; }
        if ("COBM".equals(p)) { where.append(" AND (").append(column).append(" = ? OR ").append(column).append(" LIKE ? OR ").append(column).append(" LIKE ? OR ").append(column).append(" LIKE ?)"); values.add(value); values.add(value + ",%"); values.add("%," + value + ",%"); values.add("%," + value); return; }
        if ("CISEQ".equals(p) || "OCISEQ".equals(p)) { where.append(" AND UPPER(").append(column).append(") = ?"); values.add(value == null ? null : String.valueOf(value).toUpperCase(Locale.ROOT)); return; }
        if ("EQ".equals(p) || "SEQ".equals(p)) { where.append(" AND ").append(column).append(value == null ? " IS NULL" : " = ?"); if (value != null) values.add(value); return; }
        if ("OR".equals(p) || "NOR".equals(p) || "DOR".equals(p) || "TOR".equals(p)) { where.append(" AND ").append(column).append(value == null ? " IS NULL" : " = ?"); if (value != null) values.add(value); return; }
        throw new IllegalArgumentException("unsupported personnel filter pattern: " + p);
    }

    private static boolean isDisjunction(MatchPattern pattern) {
        String name = pattern.name();
        return "OR".equals(name) || "NOR".equals(name) || "DOR".equals(name) || "TOR".equals(name)
                || name.startsWith("O");
    }

    private static String link(String link) {
        if (link == null) return "AND";
        if (!"AND".equals(link) && !"OR".equals(link)) throw new IllegalArgumentException("unsupported filter link");
        return link;
    }

    private String column(String name, boolean upper) {
        String key = name == null ? "" : name.toUpperCase(Locale.ROOT);
        Map<String, String> columns = new HashMap<String, String>();
        columns.put("ID", "A.ID"); columns.put("NAME", "A.USERNAME"); columns.put("USERNAME", "A.USERNAME");
        columns.put("DEPARTMENTID", "B.ORGID"); columns.put("DEPARTMENTNAME", "B.ORGNAME");
        columns.put("STATUS", "B.STATUS"); columns.put("CREATEDTIME", "A.CREATEDTIME"); columns.put("ORGID", "A.ORGID");
        columns.put("FILTERTEST", "A.FILTERTEST"); columns.put("REPORTAUDIT", "A.REPORTAUDIT"); columns.put("REPORTISSUE", "A.REPORTISSUE");
        columns.put("AGE", "A.AGE");
        String result = columns.get(key);
        if (result == null) throw new IllegalArgumentException("unsupported personnel filter column: " + name);
        return upper ? "UPPER(" + result + ")" : result;
    }

    private String order(QueryRequest request) {
        if (request == null || request.getOrderParameters().isEmpty()) return "Q.CREATEDTIME DESC,Q.ID DESC";
        List<String> parts = new ArrayList<String>();
        request.getOrderParameters().forEach(pair -> parts.add(orderColumn(pair.getFirst()) + ("desc".equalsIgnoreCase(pair.getSecond()) ? " DESC" : " ASC") + " NULLS LAST"));
        return String.join(",", parts);
    }

    /** The row-number query orders the projected inner row, not its hidden table aliases. */
    private String orderColumn(String name) {
        String key = name == null ? "" : name.toUpperCase();
        Map<String, String> columns = new HashMap<String, String>();
        columns.put("ID", "Q.ID"); columns.put("NAME", "Q.USERNAME"); columns.put("USERNAME", "Q.USERNAME");
        columns.put("DEPARTMENTID", "Q.DEPARTMENTID"); columns.put("DEPARTMENTNAME", "Q.DEPARTMENTNAME");
        columns.put("STATUS", "Q.STATUS"); columns.put("CREATEDTIME", "Q.CREATEDTIME");
        String result = columns.get(key);
        if (result == null) throw new IllegalArgumentException("unsupported personnel order column: " + name);
        return result;
    }

    private static void appendPlaceholders(StringBuilder sql, int count) { for (int i = 0; i < count; i++) { if (i > 0) sql.append(','); sql.append('?'); } }
    private static void bind(PreparedStatement statement, List<Object> values) throws SQLException { for (int i = 0; i < values.size(); i++) statement.setObject(i + 1, values.get(i)); }
    private static Map<String, Object> row(ResultSet rows) throws SQLException {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        ResultSetMetaData meta = rows.getMetaData();
        Object parentOrganizationName = null;
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            String label = meta.getColumnLabel(i);
            if ("RN".equalsIgnoreCase(label)) {
                result.put("RN_", JdbcValue.normalize(rows.getObject(i)));
                continue;
            }
            Object value = JdbcValue.normalize(rows.getObject(i));
            if ("PARENTORGNAME".equalsIgnoreCase(label)) {
                parentOrganizationName = value;
            } else {
                result.put(label, value);
            }
        }
        // Older schemas omit T_EMC_EMP.ORGNAME; retain the historical ORGNAME
        // projection from the parent organization without creating duplicate
        // column names when newer schemas include the employee-owned column.
        if (!result.containsKey("ORGNAME") && parentOrganizationName != null) {
            result.put("ORGNAME", parentOrganizationName);
        }
        return result;
    }

    private static String typeName(int type) {
        if (type == Types.DATE || type == Types.TIMESTAMP || type == Types.TIMESTAMP_WITH_TIMEZONE) return "dateTime";
        if (type == Types.NUMERIC || type == Types.DECIMAL || type == Types.INTEGER
                || type == Types.BIGINT || type == Types.SMALLINT || type == Types.TINYINT) return "number";
        return "string";
    }
    private static final class Sql { final String countSql; final List<Object> countValues; final String pageSql; final List<Object> pageValues; Sql(String c,List<Object>cv,String p,List<Object>pv){countSql=c;countValues=cv;pageSql=p;pageValues=pv;} }
}
