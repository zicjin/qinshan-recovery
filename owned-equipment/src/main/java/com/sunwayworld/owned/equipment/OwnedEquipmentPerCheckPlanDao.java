package com.sunwayworld.owned.equipment;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckPlanBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptPerCheckPlanDao;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.AuditEvent;
import com.sunwayworld.owned.internal.web.AuditEventSink;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.OwnedWriteSupport;
import com.sunwayworld.owned.internal.web.QueryRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Isolated Oracle adapter for equipment period-check plans. */
public final class OwnedEquipmentPerCheckPlanDao extends MybatisDaoSupport<EmcEquiptPerCheckPlanBean, Long>
        implements EmcEquiptPerCheckPlanDao {
    private final DataSource dataSource;
    private final JdbcPersonnelIdentityProvider provider;
    private final OwnedIdentityHttpBoundary boundary;
    private final AuditEventSink audit;

    public OwnedEquipmentPerCheckPlanDao(DataSource dataSource, JdbcPersonnelIdentityProvider provider,
            OwnedIdentityHttpBoundary boundary, AuditEventSink audit) {
        this.dataSource = dataSource;
        this.provider = provider;
        this.boundary = boundary;
        this.audit = audit;
    }

    public Long insertPlan(RestJsonWrapperBean wrapper) {
        if (wrapper == null) throw new IllegalArgumentException("period-check plan body is required");
        EmcEquiptPerCheckPlanBean value = wrapper.parseUnique(EmcEquiptPerCheckPlanBean.class);
        validate(value);
        try {
            OwnedWriteSupport.requireLoopback(currentRequest());
            OwnedPrincipal principal = trustedManagePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            Long id = OwnedWriteSupport.transaction(dataSource, connection -> {
                requireEquipmentScope(connection, value.getEquiptId(), organizations);
                requireMaintenanceScope(connection, value.getEquiptId(), value.getMaintId(), organizations);
                long generatedId = OwnedWriteSupport.nextIdentity(connection);
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO T_EMC_EQUIPT_PER_CHECK_PLAN (ID,EQUIPTID,MAINTID,MAINTDATE,CYCLE,WARNINGDAYS,CHECKREASON,CHECKEQUIPT,MAINTENDDATE,REMARK,CHECKWAY,CHECKUSERID,CHECKUSERNAME,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSTIMESTAMP,?,?)")) {
                    int i = 1;
                    insert.setLong(i++, generatedId);
                    insert.setLong(i++, value.getEquiptId());
                    insert.setLong(i++, value.getMaintId());
                    setDate(insert, i++, value.getMaintDate());
                    insert.setString(i++, value.getCycle());
                    setLong(insert, i++, value.getWarningDays());
                    insert.setString(i++, value.getCheckReason());
                    insert.setString(i++, value.getCheckEquipt());
                    setDate(insert, i++, value.getMaintEndDate());
                    insert.setString(i++, value.getRemark());
                    insert.setString(i++, value.getCheckWay());
                    insert.setString(i++, value.getCheckUserId());
                    insert.setString(i++, value.getCheckUserName());
                    insert.setString(i++, principal.getId());
                    insert.setString(i++, principal.getId());
                    insert.setString(i++, principal.getOrgId());
                    insert.setString(i, principal.getOrgName());
                    insert.executeUpdate();
                }
                return Long.valueOf(generatedId);
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "equipment-per-check-plan-insert", principal.getId(),
                    String.valueOf(id), AuditEvent.Outcome.ALLOWED, "isolated-write", null));
            return id;
        } catch (SQLException failure) {
            throw new IllegalStateException("period-check plan write failed", failure);
        }
    }

    public Page<EmcEquiptPerCheckPlanBean> readPlans(Long equipmentId, RestJsonWrapperBean wrapper) {
        if (equipmentId == null || equipmentId < 0L) throw new IllegalArgumentException("equipment id is invalid");
        try {
            OwnedPrincipal principal = trustedReadPrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            QueryRequest request = QueryRequest.parse(wrapper == null ? null : wrapper.getRequestJson(),
                    wrapper == null ? null : wrapper.getParamMap());
            if (organizations == null || organizations.isEmpty()) {
                return new Pagination<EmcEquiptPerCheckPlanBean>(0, request.getPageSize(), request.getPageNumber(),
                        Collections.<EmcEquiptPerCheckPlanBean>emptyList());
            }
            List<Object> values = new ArrayList<Object>();
            String scope = scopeSql("P", equipmentId, organizations, values);
            List<String> clauses = new ArrayList<String>();
            appendFilters(request, clauses, values);
            String where = scope + (clauses.isEmpty() ? "" : " AND " + String.join(" AND ", clauses));
            String base = "SELECT P.*,M.MAINTEVENT,C.CODENAME AS CODENAME "
                    + "FROM T_EMC_EQUIPT_PER_CHECK_PLAN P "
                    + "LEFT JOIN T_EMC_EQUIPT_MAINT M ON M.ID=P.MAINTID "
                    + "LEFT JOIN T_CORE_CODE C ON M.MAINTEVENT=C.CODE AND C.CODECATEGORYID='maintainEvent' WHERE " + where;
            String countSql = "SELECT COUNT(1) FROM (" + base + ") Q";
            String pageSql = "SELECT * FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY " + order(request)
                    + ") RN FROM (" + base + ") Q) WHERE RN BETWEEN ? AND ? ORDER BY RN";
            int from = (request.getPageNumber() - 1) * request.getPageSize() + 1;
            int to = request.getPageNumber() * request.getPageSize();
            try (Connection connection = dataSource.getConnection()) {
                int total;
                try (PreparedStatement count = connection.prepareStatement(countSql)) {
                    bind(count, values);
                    try (ResultSet rows = count.executeQuery()) {
                        rows.next();
                        total = rows.getInt(1);
                    }
                }
                List<EmcEquiptPerCheckPlanBean> rows = new ArrayList<EmcEquiptPerCheckPlanBean>();
                List<Object> pageValues = new ArrayList<Object>(values);
                pageValues.add(from);
                pageValues.add(to);
                try (PreparedStatement query = connection.prepareStatement(pageSql)) {
                    bind(query, pageValues);
                    try (ResultSet result = query.executeQuery()) {
                        while (result.next()) rows.add(convert(result));
                    }
                }
                return new Pagination<EmcEquiptPerCheckPlanBean>(total, request.getPageSize(), request.getPageNumber(), rows);
            }
        } catch (SQLException failure) {
            throw new IllegalStateException("period-check plan query failed", failure);
        }
    }

    public void deletePlans(RestJsonWrapperBean wrapper) {
        if (wrapper == null) throw new IllegalArgumentException("period-check plans are required");
        List<EmcEquiptPerCheckPlanBean> values = wrapper.parse(EmcEquiptPerCheckPlanBean.class);
        if (values.isEmpty() || values.size() > 100) throw new IllegalArgumentException("period-check plans are required");
        try {
            OwnedWriteSupport.requireLoopback(currentRequest());
            OwnedPrincipal principal = trustedManagePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                try (PreparedStatement find = connection.prepareStatement("SELECT EQUIPTID FROM T_EMC_EQUIPT_PER_CHECK_PLAN WHERE ID=?");
                        PreparedStatement delete = connection.prepareStatement("DELETE FROM T_EMC_EQUIPT_PER_CHECK_PLAN WHERE ID=?")) {
                    for (EmcEquiptPerCheckPlanBean value : values) {
                        if (value == null || value.getId() == null) throw new IllegalArgumentException("period-check plan id is required");
                        find.setLong(1, value.getId());
                        try (ResultSet rows = find.executeQuery()) {
                            if (!rows.next()) throw denied();
                            requireEquipmentScope(connection, rows.getLong(1), organizations);
                        }
                        delete.setLong(1, value.getId());
                        if (delete.executeUpdate() != 1) throw denied();
                    }
                }
                return null;
            });
            for (EmcEquiptPerCheckPlanBean value : values) {
                audit.record(new AuditEvent(java.time.Instant.now(), "equipment-per-check-plan-delete", principal.getId(),
                        String.valueOf(value.getId()), AuditEvent.Outcome.ALLOWED, "isolated-write", null));
            }
        } catch (SQLException failure) {
            throw new IllegalStateException("period-check plan delete failed", failure);
        }
    }

    public void updatePlan(EmcEquiptPerCheckPlanBean value) {
        if (value == null || value.getId() == null || value.getId().longValue() < 0L)
            throw new IllegalArgumentException("period-check plan id is required");
        try {
            OwnedWriteSupport.requireLoopback(currentRequest());
            OwnedPrincipal principal = trustedManagePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                long equipmentId; long maintenanceId;
                try (PreparedStatement find = connection.prepareStatement("SELECT EQUIPTID,MAINTID FROM T_EMC_EQUIPT_PER_CHECK_PLAN WHERE ID=?")) {
                    find.setLong(1, value.getId().longValue());
                    try (ResultSet rows = find.executeQuery()) {
                        if (!rows.next()) throw denied();
                        equipmentId = rows.getLong(1); maintenanceId = rows.getLong(2);
                    }
                }
                if (value.getEquiptId() != null && value.getEquiptId().longValue() != equipmentId)
                    throw denied();
                if (value.getMaintId() != null && value.getMaintId().longValue() != maintenanceId)
                    throw denied();
                requireEquipmentScope(connection, equipmentId, organizations);
                requireMaintenanceScope(connection, equipmentId, maintenanceId, organizations);
                updatePlan(connection, value);
                return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "equipment-per-check-plan-update", principal.getId(), String.valueOf(value.getId()), AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) {
            throw new IllegalStateException("period-check plan update failed", failure);
        }
    }

    private void updatePlan(Connection connection, EmcEquiptPerCheckPlanBean value) throws SQLException {
        try (PreparedStatement update = connection.prepareStatement(
                "UPDATE T_EMC_EQUIPT_PER_CHECK_PLAN SET MAINTDATE=?,CYCLE=?,CHECKREASON=?,CHECKEQUIPT=?,MAINTENDDATE=?,REMARK=? WHERE ID=?")) {
            setDate(update, 1, value.getMaintDate());
            update.setString(2, value.getCycle());
            update.setString(3, value.getCheckReason());
            update.setString(4, value.getCheckEquipt());
            setDate(update, 5, value.getMaintEndDate());
            update.setString(6, value.getRemark());
            update.setLong(7, value.getId());
            if (update.executeUpdate() != 1) throw denied();
        }
    }

    private static void validate(EmcEquiptPerCheckPlanBean value) {
        if (value == null || value.getEquiptId() == null || value.getEquiptId() < 0L)
            throw new IllegalArgumentException("equipment id is required");
        if (value.getMaintId() == null || value.getMaintId() < 0L)
            throw new IllegalArgumentException("maintenance event is required");
        if (value.getWarningDays() != null && (value.getWarningDays() < 0L || value.getWarningDays() > 36500L))
            throw new IllegalArgumentException("warning days is invalid");
    }

    private static void appendFilters(QueryRequest request, List<String> clauses, List<Object> values) {
        for (FilterParamPattern group : request.getFilterPatterns()) {
            String pattern = group.getMatchPattern();
            for (Map.Entry<String, Object> entry : group.getMap().entrySet()) {
                String field = planColumn(entry.getKey());
                Object value = entry.getValue();
                String expression = planExpression(field);
                if ("CISC".equals(pattern) || "SC".equals(pattern)) {
                    clauses.add("UPPER(" + expression + ") LIKE ?");
                    values.add("%" + String.valueOf(value).toUpperCase(Locale.ROOT) + "%");
                } else if ("EQ".equals(pattern) || "SEQ".equals(pattern)) {
                    clauses.add(expression + (value == null ? " IS NULL" : "=?"));
                    if (value != null) values.add(value);
                } else if ("DG".equals(pattern) || "DGOE".equals(pattern) || "DL".equals(pattern) || "DLOE".equals(pattern)) {
                    clauses.add(expression + ("DG".equals(pattern) ? ">" : "DGOE".equals(pattern) ? ">=" : "DL".equals(pattern) ? "<" : "<=")
                            + " TO_DATE(?, 'YYYY-MM-DD')");
                    values.add(String.valueOf(value));
                } else {
                    throw new IllegalArgumentException("unsupported period-check plan filter pattern: " + pattern);
                }
            }
        }
        for (String key : request.getMapperParameters().keySet()) {
            if (!"target_type".equals(key) && !"orgid_seq".equals(key))
                throw new IllegalArgumentException("unsupported period-check plan query parameter: " + key);
        }
        if (!request.getAdvancedPatterns().isEmpty() || request.getMultiPropertyPattern() != null)
            throw new IllegalArgumentException("unsupported period-check plan query shape");
    }

    private static String planColumn(String key) {
        String normalized = key == null ? "" : key.toUpperCase(Locale.ROOT);
        for (String suffix : new String[]{"_CISC", "_EQ", "_SEQ", "_DG", "_DGOE", "_DL", "_DLOE"}) {
            if (normalized.endsWith(suffix)) {
                normalized = normalized.substring(0, normalized.length() - suffix.length());
                break;
            }
        }
        if ("ID".equals(normalized) || "EQUIPTID".equals(normalized) || "MAINTID".equals(normalized)
                || "MAINTDATE".equals(normalized) || "CYCLE".equals(normalized) || "WARNINGDAYS".equals(normalized)
                || "CHECKREASON".equals(normalized) || "CHECKEQUIPT".equals(normalized) || "MAINTENDDATE".equals(normalized)
                || "REMARK".equals(normalized) || "CODENAME".equals(normalized)) return normalized;
        throw new IllegalArgumentException("unsupported period-check plan filter column: " + key);
    }

    private static String planExpression(String field) {
        return "CODENAME".equals(field) ? "C.CODENAME" : "P." + field;
    }

    private static String order(QueryRequest request) {
        if (request.getOrderParameters().isEmpty()) return "Q.ID DESC";
        List<String> parts = new ArrayList<String>();
        for (Pair<String, String> pair : request.getOrderParameters()) {
            String field = planColumn(pair.getFirst());
            parts.add("Q." + field + ("desc".equalsIgnoreCase(pair.getSecond()) ? " DESC" : " ASC") + " NULLS LAST");
        }
        return String.join(",", parts);
    }

    private String scopeSql(String alias, long id, Set<String> organizations, List<Object> values) {
        StringBuilder sql = new StringBuilder(alias + ".EQUIPTID=? AND EXISTS (SELECT 1 FROM T_EMC_EQUIPT E WHERE E.ID="
                + alias + ".EQUIPTID AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION OP WHERE OP.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND OP.TARGETID=E.EQUIPTCATEGORYID AND OP.ORGID IN (");
        values.add(id);
        int i = 0;
        for (String org : organizations) {
            if (i++ > 0) sql.append(',');
            sql.append('?');
            values.add(org);
        }
        return sql.append(")))").toString();
    }

    static void requireEquipmentScope(Connection connection, long id, Set<String> organizations) throws SQLException {
        if (organizations == null || organizations.isEmpty()) throw denied();
        StringBuilder sql = new StringBuilder("SELECT 1 FROM T_EMC_EQUIPT E WHERE E.ID=? AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION P WHERE P.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND P.TARGETID=E.EQUIPTCATEGORYID AND P.ORGID IN (");
        int i = 0;
        for (String ignored : organizations) { if (i++ > 0) sql.append(','); sql.append('?'); }
        sql.append("))");
        try (PreparedStatement query = connection.prepareStatement(sql.toString())) {
            i = 1;
            query.setLong(i++, id);
            for (String org : organizations) query.setString(i++, org);
            try (ResultSet rows = query.executeQuery()) { if (!rows.next()) throw denied(); }
        }
    }

    static void requireMaintenanceScope(Connection connection, long equipmentId, long maintId, Set<String> organizations) throws SQLException {
        if (organizations == null || organizations.isEmpty()) throw denied();
        StringBuilder sql = new StringBuilder("SELECT 1 FROM T_EMC_EQUIPT_MAINT M JOIN T_EMC_EQUIPT E ON E.EQUIPTCATEGORYID=M.EQUIPTCATEGORYID WHERE E.ID=? AND M.ID=? AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION P WHERE P.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND P.TARGETID=E.EQUIPTCATEGORYID AND P.ORGID IN (");
        int i = 0;
        for (String ignored : organizations) { if (i++ > 0) sql.append(','); sql.append('?'); }
        sql.append("))");
        try (PreparedStatement query = connection.prepareStatement(sql.toString())) {
            i = 1;
            query.setLong(i++, equipmentId);
            query.setLong(i++, maintId);
            for (String org : organizations) query.setString(i++, org);
            try (ResultSet rows = query.executeQuery()) { if (!rows.next()) throw denied(); }
        }
    }

    static AccessDeniedException denied() { return new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED"); }

    private OwnedPrincipal trustedReadPrincipal() throws SQLException {
        HttpSession session = currentRequest().getSession(false);
        boundary.requirePermission(session, OwnedIdentityProvider.EQUIPMENT_READ);
        return boundary.currentPrincipal(session);
    }

    private OwnedPrincipal trustedManagePrincipal() throws SQLException {
        HttpSession session = currentRequest().getSession(false);
        boundary.requirePermission(session, OwnedIdentityProvider.EQUIPMENT_MANAGE);
        return boundary.currentPrincipal(session);
    }

    private static HttpServletRequest currentRequest() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (!(attrs instanceof ServletRequestAttributes)) throw denied();
        HttpServletRequest request = ((ServletRequestAttributes) attrs).getRequest();
        if (request == null) throw denied();
        return request;
    }

    private static void bind(PreparedStatement statement, List<Object> values) throws SQLException {
        int i = 1;
        for (Object value : values) {
            if (value == null) statement.setNull(i++, java.sql.Types.VARCHAR);
            else if (value instanceof Number) statement.setLong(i++, ((Number) value).longValue());
            else statement.setString(i++, String.valueOf(value));
        }
    }

    private static void setLong(PreparedStatement statement, int index, Long value) throws SQLException {
        if (value == null) statement.setNull(index, java.sql.Types.NUMERIC); else statement.setLong(index, value);
    }

    private static void setDate(PreparedStatement statement, int index, LocalDateTime value) throws SQLException {
        if (value == null) statement.setNull(index, java.sql.Types.TIMESTAMP); else statement.setTimestamp(index, Timestamp.valueOf(value));
    }

    private static EmcEquiptPerCheckPlanBean convert(ResultSet rows) throws SQLException {
        EmcEquiptPerCheckPlanBean bean = new EmcEquiptPerCheckPlanBean();
        bean.setId(rows.getLong("ID"));
        bean.setEquiptId(getLong(rows, "EQUIPTID"));
        bean.setMaintId(getLong(rows, "MAINTID"));
        bean.setMaintEvent(rows.getString("MAINTEVENT"));
        bean.setExt$Item("codename", rows.getString("CODENAME"));
        bean.setMaintDate(date(rows, "MAINTDATE"));
        bean.setCycle(rows.getString("CYCLE"));
        bean.setWarningDays(getLong(rows, "WARNINGDAYS"));
        bean.setCheckReason(rows.getString("CHECKREASON"));
        bean.setCheckEquipt(rows.getString("CHECKEQUIPT"));
        bean.setMaintEndDate(date(rows, "MAINTENDDATE"));
        bean.setRemark(rows.getString("REMARK"));
        bean.setCheckWay(rows.getString("CHECKWAY"));
        bean.setCheckUserId(rows.getString("CHECKUSERID"));
        bean.setCheckUserName(rows.getString("CHECKUSERNAME"));
        bean.setCreatedById(rows.getString("CREATEDBYID"));
        bean.setCreatedByName(rows.getString("CREATEDBYNAME"));
        bean.setCreatedTime(date(rows, "CREATEDTIME"));
        bean.setCreatedByOrgId(rows.getString("CREATEDBYORGID"));
        bean.setCreatedByOrgName(rows.getString("CREATEDBYORGNAME"));
        return bean;
    }

    private static Long getLong(ResultSet rows, String column) throws SQLException {
        long value = rows.getLong(column);
        return rows.wasNull() ? null : Long.valueOf(value);
    }

    private static LocalDateTime date(ResultSet rows, String column) throws SQLException {
        Timestamp value = rows.getTimestamp(column);
        return value == null ? null : value.toLocalDateTime();
    }

}
