package com.sunwayworld.owned.personnel;

import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertBean;
import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertTestMethodBean;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpQualBean;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpTestBean;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpTrainBean;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.owned.internal.data.JdbcValue;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentity;
import com.sunwayworld.owned.internal.web.QueryRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;

/**
 * Read-only adapter for the personnel detail tabs which already have public
 * EmcEmpResource routes.  Every query starts with the employee row scope and
 * never trusts a certificate or child-row identifier as an authorization
 * boundary.
 */
final class OwnedPersonnelAuxiliaryReadService {
    private final DataSource dataSource;
    private final JdbcPersonnelIdentity identity;

    OwnedPersonnelAuxiliaryReadService(DataSource dataSource, JdbcPersonnelIdentity identity) {
        if (dataSource == null || identity == null) throw new IllegalArgumentException("auxiliary dependencies required");
        this.dataSource = dataSource;
        this.identity = identity;
    }

    Pagination<EmcEmpQualBean> qualifications(String employeeId, QueryRequest request,
            JdbcPersonnelIdentity.Principal principal) throws SQLException {
        try {
            return page(employeeId, request, principal,
                    "SELECT A.*, D.NAME AS FILENAME, D.ID AS FILEID "
                            + "FROM T_EMC_EMP_QUAL A LEFT JOIN T_CORE_FILE D "
                            + "ON D.TARGETID = ('T_EMC_EMP_QUAL$' || TO_CHAR(A.ID))",
                    qualificationColumns(true), qualificationMapper());
        } catch (SQLException failure) {
            // Strict sss2 clones may intentionally omit attachment tables. A
            // qualification row is still a valid business record; preserve
            // that API read while leaving file metadata empty.
            if (!missingTable(failure)) throw failure;
            return page(employeeId, request, principal, "SELECT A.* FROM T_EMC_EMP_QUAL A",
                    qualificationColumns(false), qualificationMapper());
        }
    }

    private static Map<String, String> qualificationColumns(boolean withFiles) {
        return withFiles
                ? columns("ID", "A.ID", "EMPID", "A.EMPID", "QUALNAME", "A.QUALNAME", "QUALORG", "A.QUALORG",
                        "VALIDDATE", "A.VALIDDATE", "CREATEDBYID", "A.CREATEDBYID", "CREATEDBYNAME", "A.CREATEDBYNAME",
                        "CREATEDTIME", "A.CREATEDTIME", "CREATEDBYORGID", "A.CREATEDBYORGID", "CREATEDBYORGNAME", "A.CREATEDBYORGNAME",
                        "FILENAME", "D.NAME", "FILEID", "D.ID")
                : columns("ID", "A.ID", "EMPID", "A.EMPID", "QUALNAME", "A.QUALNAME", "QUALORG", "A.QUALORG",
                        "VALIDDATE", "A.VALIDDATE", "CREATEDBYID", "A.CREATEDBYID", "CREATEDBYNAME", "A.CREATEDBYNAME",
                        "CREATEDTIME", "A.CREATEDTIME", "CREATEDBYORGID", "A.CREATEDBYORGID", "CREATEDBYORGNAME", "A.CREATEDBYORGNAME");
    }

    private static Mapper<EmcEmpQualBean> qualificationMapper() {
        return new Mapper<EmcEmpQualBean>() {
            public EmcEmpQualBean map(Map<String, Object> row) { return bean(row, EmcEmpQualBean.class); }
        };
    }

    private static boolean missingTable(SQLException failure) {
        for (Throwable current = failure; current != null; current = current.getCause()) {
            String message = current.getMessage();
            if (current instanceof SQLException && ((SQLException) current).getErrorCode() == 942) return true;
            if (message != null && message.toUpperCase(Locale.ROOT).contains("ORA-00942")) return true;
        }
        return false;
    }

    Pagination<EmcEmpTestBean> tests(String employeeId, QueryRequest request,
            JdbcPersonnelIdentity.Principal principal) throws SQLException {
        return page(employeeId, request, principal,
                "SELECT A.*, D.TESTNAME, C.TESTCATEGORYDESC AS TESTCATEGORY "
                        + "FROM T_EMC_EMP_TEST A "
                        + "LEFT JOIN T_EMC_TEST_METHOD B ON A.TESTID = B.ID "
                        + "LEFT JOIN T_EMC_TEST_CATEGORY C ON B.TESTCATEGORYID = C.ID "
                        + "LEFT JOIN T_EMC_TEST D ON B.TESTID = D.ID",
                columns("ID", "A.ID", "EMPID", "A.EMPID", "TESTID", "A.TESTID", "CREATEDBYID", "A.CREATEDBYID",
                        "CREATEDBYNAME", "A.CREATEDBYNAME", "CREATEDTIME", "A.CREATEDTIME", "CREATEDBYORGID", "A.CREATEDBYORGID",
                        "CREATEDBYORGNAME", "A.CREATEDBYORGNAME", "TESTNAME", "D.TESTNAME", "TESTCATEGORY", "C.TESTCATEGORYDESC"),
                new Mapper<EmcEmpTestBean>() {
                    public EmcEmpTestBean map(Map<String, Object> row) { return bean(row, EmcEmpTestBean.class); }
                });
    }

    Pagination<EmcEmpTrainBean> training(String employeeId, QueryRequest request,
            JdbcPersonnelIdentity.Principal principal) throws SQLException {
        return page(employeeId, request, principal, "SELECT A.* FROM T_EMC_EMP_TRAIN A",
                columns("ID", "A.ID", "EMPID", "A.EMPID", "TRAINTIME", "A.TRAINTIME",
                        "TRAINCONTENT", "A.TRAINCONTENT", "TRAINRESULT", "A.TRAINRESULT",
                        "COURSECODE", "A.COURSECODE", "COURSENAME", "A.COURSENAME",
                        "TRAINDEPT", "A.TRAINDEPT", "TRAINDEPTCODE", "A.TRAINDEPTCODE",
                        "CREATEDBYID", "A.CREATEDBYID", "CREATEDBYNAME", "A.CREATEDBYNAME",
                        "CREATEDTIME", "A.CREATEDTIME", "CREATEDBYORGID", "A.CREATEDBYORGID",
                        "CREATEDBYORGNAME", "A.CREATEDBYORGNAME", "TRAINBEGINTIME", "A.TRAINBEGINTIME",
                        "TRAINENDTIME", "A.TRAINENDTIME"),
                new Mapper<EmcEmpTrainBean>() {
                    public EmcEmpTrainBean map(Map<String, Object> row) { return bean(row, EmcEmpTrainBean.class); }
                });
    }

    Pagination<EmcCertBean> credentials(String employeeId, QueryRequest request,
            JdbcPersonnelIdentity.Principal principal) throws SQLException {
        return page(employeeId, request, principal, "SELECT A.* FROM T_EMC_CERT A",
                columns("ID", "A.ID", "EMPID", "A.EMPID", "CERTNO", "A.CERTNO", "CERTNAME", "A.CERTNAME",
                        "AUTHDATE", "A.AUTHDATE", "EXPIREDATE", "A.EXPIREDATE", "WARNINGDAYS", "A.WARNINGDAYS",
                        "STATUS", "A.STATUS", "ORDERNO", "A.ORDERNO", "CREATEDBYID", "A.CREATEDBYID", "CREATEDBYNAME", "A.CREATEDBYNAME",
                        "CREATEDTIME", "A.CREATEDTIME", "CREATEDBYORGID", "A.CREATEDBYORGID", "CREATEDBYORGNAME", "A.CREATEDBYORGNAME"),
                new Mapper<EmcCertBean>() {
                    public EmcCertBean map(Map<String, Object> row) { return bean(row, EmcCertBean.class); }
                });
    }

    Pagination<EmcCertTestMethodBean> methods(Long certificateId, QueryRequest request,
            JdbcPersonnelIdentity.Principal principal) throws SQLException {
        if (certificateId == null) throw new IllegalArgumentException("certificate id is required");
        identity.authorizePersonnel(principal);
        Set<String> organizations = identity.permittedOrganizationIds(principal);
        if (organizations.isEmpty()) return empty(request);
        return pageWithCertificate(certificateId.longValue(), organizations, request,
                "SELECT T.*, M.METHODNAME, M.TESTMETHODBASIS, D.TESTNAME, B.TESTCATEGORYID, B.TESTCLASS "
                        + "FROM T_EMC_CERT_TEST_METHOD T "
                        + "LEFT JOIN T_EMC_METHOD M ON T.METHODID = M.ID "
                        + "LEFT JOIN T_EMC_TEST D ON T.TESTID = D.ID "
                        + "LEFT JOIN T_EMC_TEST_METHOD B ON T.TESTMETHODID = B.ID",
                columns("ID", "T.ID", "CERTID", "T.CERTID", "TESTID", "T.TESTID", "METHODID", "T.METHODID",
                        "REMARK", "T.REMARK", "TESTMETHODID", "T.TESTMETHODID", "ITEMGROUP", "T.ITEMGROUP", "TYPE", "T.TYPE",
                        "CREATEDBYID", "T.CREATEDBYID", "CREATEDBYNAME", "T.CREATEDBYNAME", "CREATEDTIME", "T.CREATEDTIME",
                        "CREATEDBYORGID", "T.CREATEDBYORGID", "CREATEDBYORGNAME", "T.CREATEDBYORGNAME", "METHODNAME", "M.METHODNAME",
                        "TESTMETHODBASIS", "M.TESTMETHODBASIS", "TESTNAME", "D.TESTNAME", "TESTCATEGORYID", "B.TESTCATEGORYID", "TESTCLASS", "B.TESTCLASS"),
                new Mapper<EmcCertTestMethodBean>() {
                    public EmcCertTestMethodBean map(Map<String, Object> row) { return bean(row, EmcCertTestMethodBean.class); }
                });
    }

    private <T> Pagination<T> page(String employeeId, QueryRequest request,
            JdbcPersonnelIdentity.Principal principal, String from, Map<String, String> columns,
            Mapper<T> mapper) throws SQLException {
        if (employeeId == null || employeeId.trim().isEmpty()) throw new IllegalArgumentException("employee id is required");
        identity.authorizePersonnel(principal);
        Set<String> organizations = identity.permittedOrganizationIds(principal);
        if (organizations.isEmpty()) return empty(request);
        List<Object> values = new ArrayList<Object>();
        StringBuilder where = new StringBuilder(" WHERE A.EMPID = ? AND EXISTS (SELECT 1 FROM T_EMC_EMP E WHERE E.ID = A.EMPID AND E.ORGID IN (");
        values.add(employeeId);
        placeholders(where, organizations.size()); where.append("))");
        values.addAll(organizations);
        appendFilters(request, columns, where, values);
        return run(request, from, where.toString(), values, mapper);
    }

    private <T> Pagination<T> pageWithCertificate(long certificateId, Set<String> organizations, QueryRequest request,
            String from, Map<String, String> columns, Mapper<T> mapper) throws SQLException {
        List<Object> values = new ArrayList<Object>();
        StringBuilder where = new StringBuilder(" WHERE T.CERTID = ? AND EXISTS (SELECT 1 FROM T_EMC_CERT C JOIN T_EMC_EMP E ON E.ID = C.EMPID WHERE C.ID = T.CERTID AND E.ORGID IN (");
        values.add(Long.valueOf(certificateId));
        placeholders(where, organizations.size()); where.append("))");
        values.addAll(organizations);
        appendFilters(request, columns, where, values);
        return run(request, from, where.toString(), values, mapper);
    }

    private <T> Pagination<T> run(QueryRequest request, String from, String where,
            List<Object> values, Mapper<T> mapper) throws SQLException {
        if (request == null) throw new IllegalArgumentException("query request is required");
        String base = from + where;
        String countSql = "SELECT COUNT(1) FROM (" + base + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*, ROW_NUMBER() OVER (ORDER BY "
                + order(request) + ") RN FROM (" + base + ") Q) P WHERE RN > ? AND RN <= ?";
        List<T> rows = new ArrayList<T>();
        int total;
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement count = connection.prepareStatement(countSql)) {
                bind(count, values);
                try (ResultSet result = count.executeQuery()) { result.next(); total = result.getInt(1); }
            }
            List<Object> pageValues = new ArrayList<Object>(values);
            pageValues.add(Integer.valueOf((request.getPageNumber() - 1) * request.getPageSize()));
            pageValues.add(Integer.valueOf(request.getPageNumber() * request.getPageSize()));
            try (PreparedStatement page = connection.prepareStatement(pageSql)) {
                bind(page, pageValues);
                try (ResultSet result = page.executeQuery()) {
                    while (result.next()) rows.add(mapper.map(row(result)));
                }
            }
        }
        return new Pagination<T>(total, request.getPageSize(), request.getPageNumber(), rows);
    }

    private static <T> Pagination<T> empty(QueryRequest request) {
        QueryRequest safe = request;
        if (safe == null) throw new IllegalArgumentException("query request is required");
        return new Pagination<T>(0, safe.getPageSize(), safe.getPageNumber(), Collections.<T>emptyList());
    }

    private static void appendFilters(QueryRequest request, Map<String, String> columns,
            StringBuilder where, List<Object> values) {
        if (request == null) throw new IllegalArgumentException("query request is required");
        if (!request.getMapperParameters().isEmpty()) {
            throw new IllegalArgumentException("unsupported auxiliary query parameter");
        }
        for (FilterParamPattern pattern : request.getFilterPatterns()) {
            MatchPattern match = MatchPattern.getPatternIfPossible(pattern.getMatchPattern());
            if (match == null) throw new IllegalArgumentException("unknown auxiliary filter pattern");
            for (Map.Entry<String, Object> entry : pattern.getMap().entrySet()) {
                String column = columns.get(entry.getKey().toUpperCase(Locale.ROOT));
                if (column == null) throw new IllegalArgumentException("unsupported auxiliary filter column: " + entry.getKey());
                appendCondition(where, values, column, match, entry.getValue());
            }
        }
        if (!request.getAdvancedPatterns().isEmpty() || request.getMultiPropertyPattern() != null) {
            throw new IllegalArgumentException("advanced auxiliary filters are not implemented");
        }
    }

    private static void appendCondition(StringBuilder where, List<Object> values, String column,
            MatchPattern pattern, Object value) {
        String name = pattern.name();
        if ("CISC".equals(name) || "CISB".equals(name) || "CISE".equals(name) || "CISEQ".equals(name)) {
            String text = String.valueOf(value).toUpperCase(Locale.ROOT);
            if ("CISB".equals(name)) text += "%";
            else if ("CISE".equals(name)) text = "%" + text;
            else if ("CISC".equals(name)) text = "%" + text + "%";
            where.append(" AND UPPER(").append(column).append(") LIKE ?"); values.add(text); return;
        }
        if ("SB".equals(name) || "SC".equals(name) || "SE".equals(name) || "SEQ".equals(name)) {
            String text = String.valueOf(value);
            if ("SC".equals(name)) text = "%" + text + "%";
            else if ("SE".equals(name)) text = "%" + text;
            else if ("SB".equals(name)) text += "%";
            where.append(" AND ").append(column).append(" LIKE ?"); values.add(text); return;
        }
        if ("DIFFER".equals(name)) { where.append(" AND ").append(column).append(" <> ?"); values.add(value); return; }
        if ("ISNULL".equals(name)) { where.append(" AND ").append(column).append(" IS NULL"); return; }
        if ("ISNOTNULL".equals(name)) { where.append(" AND ").append(column).append(" IS NOT NULL"); return; }
        if ("EQ".equals(name)) { where.append(" AND ").append(column).append(value == null ? " IS NULL" : " = ?"); if (value != null) values.add(value); return; }
        throw new IllegalArgumentException("unsupported auxiliary filter pattern: " + name);
    }

    private static String order(QueryRequest request) {
        if (request.getOrderParameters().isEmpty()) return "Q.ID DESC";
        List<String> values = new ArrayList<String>();
        for (Pair<String, String> pair : request.getOrderParameters()) {
            String field = pair.getFirst() == null ? "" : pair.getFirst().toUpperCase(Locale.ROOT);
            if (!("ID".equals(field) || "CREATEDTIME".equals(field))) throw new IllegalArgumentException("unsupported auxiliary order column");
            values.add("Q." + field + ("desc".equalsIgnoreCase(pair.getSecond()) ? " DESC" : " ASC"));
        }
        return join(values);
    }

    private static String join(List<String> values) { StringBuilder result = new StringBuilder(); for (String value : values) { if (result.length() > 0) result.append(','); result.append(value); } return result.toString(); }
    private static void placeholders(StringBuilder sql, int count) { for (int i = 0; i < count; i++) { if (i > 0) sql.append(','); sql.append('?'); } }
    private static void bind(PreparedStatement statement, List<Object> values) throws SQLException { for (int i = 0; i < values.size(); i++) statement.setObject(i + 1, values.get(i)); }

    private static Map<String, Object> row(ResultSet result) throws SQLException {
        Map<String, Object> row = new LinkedHashMap<String, Object>();
        ResultSetMetaData metadata = result.getMetaData();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i);
            if (!"RN".equalsIgnoreCase(label)) row.put(label.toUpperCase(Locale.ROOT), JdbcValue.normalize(result.getObject(i)));
        }
        return row;
    }

    private static Map<String, String> columns(String... values) {
        Map<String, String> result = new HashMap<String, String>();
        for (int i = 0; i < values.length; i += 2) result.put(values[i], values[i + 1]);
        return result;
    }

    private static <T> T bean(Map<String, Object> row, Class<T> type) {
        try {
            T target = type.newInstance();
            Map<String, Method> setters = new HashMap<String, Method>();
            for (Method method : type.getMethods()) {
                if (!method.isBridge() && method.getName().startsWith("set") && method.getParameterTypes().length == 1
                        && !"setExt$".equals(method.getName()) && !"setExt$Item".equals(method.getName())) {
                    setters.put(normalize(method.getName().substring(3)), method);
                }
            }
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                Method setter = setters.get(normalize(entry.getKey()));
                Object value = entry.getValue();
                if (setter == null) { if (value != null) target.getClass().getMethod("setExt$Item", String.class, String.class).invoke(target, entry.getKey().toLowerCase(Locale.ROOT), String.valueOf(value)); continue; }
                if (value == null) continue;
                setter.invoke(target, convert(value, setter.getParameterTypes()[0]));
            }
            return target;
        } catch (InstantiationException e) { throw new IllegalStateException("auxiliary DTO cannot be created", e); }
        catch (IllegalAccessException e) { throw new IllegalStateException("auxiliary DTO inaccessible", e); }
        catch (InvocationTargetException e) { throw new IllegalStateException("auxiliary DTO setter failed", e.getCause()); }
        catch (NoSuchMethodException e) { throw new IllegalStateException("auxiliary DTO extension contract missing", e); }
    }

    private static Object convert(Object value, Class<?> target) {
        if (target.isInstance(value)) return value;
        if (target == String.class) return String.valueOf(value);
        if (target == Long.class || target == Long.TYPE) return ((Number) number(value)).longValue();
        if (target == Integer.class || target == Integer.TYPE) return ((Number) number(value)).intValue();
        if (target == LocalDate.class) {
            if (value instanceof Date) return ((Date) value).toLocalDate();
            if (value instanceof Timestamp) return ((Timestamp) value).toLocalDateTime().toLocalDate();
            return LocalDate.parse(String.valueOf(value).substring(0, 10));
        }
        if (target == LocalDateTime.class) {
            if (value instanceof Timestamp) return ((Timestamp) value).toLocalDateTime();
            if (value instanceof java.util.Date) return new Timestamp(((java.util.Date) value).getTime()).toLocalDateTime();
            return LocalDateTime.parse(String.valueOf(value).replace(' ', 'T'));
        }
        throw new IllegalArgumentException("unsupported auxiliary DTO conversion to " + target.getName());
    }

    private static Number number(Object value) { return value instanceof Number ? (Number) value : new java.math.BigDecimal(String.valueOf(value)); }
    private static String normalize(String value) { return value.replace("_", "").replace(".", "").toUpperCase(Locale.ROOT); }
    private interface Mapper<T> { T map(Map<String, Object> row); }
}
