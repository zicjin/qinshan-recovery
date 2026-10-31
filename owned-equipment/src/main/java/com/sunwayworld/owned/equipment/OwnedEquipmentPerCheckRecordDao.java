package com.sunwayworld.owned.equipment;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckRecordBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptPerCheckRecordDao;
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

/** Isolated Oracle adapter for period-check records and completion. */
public final class OwnedEquipmentPerCheckRecordDao extends MybatisDaoSupport<EmcEquiptPerCheckRecordBean, Long>
        implements EmcEquiptPerCheckRecordDao {
    private final DataSource dataSource;
    private final JdbcPersonnelIdentityProvider provider;
    private final OwnedIdentityHttpBoundary boundary;
    private final AuditEventSink audit;

    public OwnedEquipmentPerCheckRecordDao(DataSource dataSource, JdbcPersonnelIdentityProvider provider,
            OwnedIdentityHttpBoundary boundary, AuditEventSink audit) {
        this.dataSource = dataSource;
        this.provider = provider;
        this.boundary = boundary;
        this.audit = audit;
    }

    public Long insertRecord(RestJsonWrapperBean wrapper) {
        if (wrapper == null) throw new IllegalArgumentException("period-check record body is required");
        EmcEquiptPerCheckRecordBean value = wrapper.parseUnique(EmcEquiptPerCheckRecordBean.class);
        validate(value);
        try {
            OwnedWriteSupport.requireLoopback(currentRequest());
            OwnedPrincipal principal = trustedManagePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            Long id = OwnedWriteSupport.transaction(dataSource, connection -> {
                OwnedEquipmentPerCheckPlanDao.requireEquipmentScope(connection, value.getEquiptId(), organizations);
                OwnedEquipmentPerCheckPlanDao.requireMaintenanceScope(connection, value.getEquiptId(), value.getMaintId(), organizations);
                try (PreparedStatement plan = connection.prepareStatement(
                        "SELECT 1 FROM T_EMC_EQUIPT_PER_CHECK_PLAN WHERE EQUIPTID=? AND MAINTID=?")) {
                    plan.setLong(1, value.getEquiptId());
                    plan.setLong(2, value.getMaintId());
                    try (ResultSet rows = plan.executeQuery()) {
                        if (!rows.next()) throw new IllegalArgumentException("period-check plan is required");
                    }
                }
                long generatedId = OwnedWriteSupport.nextIdentity(connection);
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO T_EMC_EQUIPT_PER_CHECK_RECORD (ID,EQUIPTID,MAINTID,MAINTDATE,CYCLE,CHECKREASON,CHECKEQUIPT,COMPLETEFLAG,CHECKUSERS,CHECKUSERNAMES,REMARK,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?,?,?,?, ?,?,SYSTIMESTAMP,?,?)")) {
                    int i = 1;
                    insert.setLong(i++, generatedId);
                    insert.setLong(i++, value.getEquiptId());
                    insert.setLong(i++, value.getMaintId());
                    setDate(insert, i++, value.getMaintDate());
                    insert.setString(i++, value.getCycle());
                    insert.setString(i++, value.getCheckReason());
                    insert.setString(i++, value.getCheckEquipt());
                    insert.setString(i++, "1");
                    insert.setString(i++, value.getCheckUsers());
                    insert.setString(i++, value.getCheckUsernames());
                    insert.setString(i++, value.getRemark());
                    insert.setString(i++, principal.getId());
                    insert.setString(i++, principal.getId());
                    insert.setString(i++, principal.getOrgId());
                    insert.setString(i, principal.getOrgName());
                    insert.executeUpdate();
                }
                return Long.valueOf(generatedId);
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "equipment-per-check-record-insert", principal.getId(),
                    String.valueOf(id), AuditEvent.Outcome.ALLOWED, "isolated-write", null));
            return id;
        } catch (SQLException failure) {
            throw new IllegalStateException("period-check record write failed", failure);
        }
    }

    public Page<EmcEquiptPerCheckRecordBean> readRecords(Long equipmentId, RestJsonWrapperBean wrapper) {
        if (equipmentId == null || equipmentId < 0L) throw new IllegalArgumentException("equipment id is invalid");
        try {
            OwnedPrincipal principal = trustedReadPrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            QueryRequest request = QueryRequest.parse(wrapper == null ? null : wrapper.getRequestJson(),
                    wrapper == null ? null : wrapper.getParamMap());
            if (organizations == null || organizations.isEmpty()) {
                return new Pagination<EmcEquiptPerCheckRecordBean>(0, request.getPageSize(), request.getPageNumber(),
                        Collections.<EmcEquiptPerCheckRecordBean>emptyList());
            }
            List<Object> values = new ArrayList<Object>();
            String scope = scopeSql(equipmentId, organizations, values);
            List<String> clauses = new ArrayList<String>();
            appendFilters(request, clauses, values);
            String where = scope + (clauses.isEmpty() ? "" : " AND " + String.join(" AND ", clauses));
            String base = "SELECT R.*,M.MAINTEVENT,C.CODENAME AS CODENAME "
                    + "FROM T_EMC_EQUIPT_PER_CHECK_RECORD R "
                    + "LEFT JOIN T_EMC_EQUIPT_MAINT M ON M.ID=R.MAINTID "
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
                List<EmcEquiptPerCheckRecordBean> rows = new ArrayList<EmcEquiptPerCheckRecordBean>();
                List<Object> pageValues = new ArrayList<Object>(values);
                pageValues.add(from);
                pageValues.add(to);
                try (PreparedStatement query = connection.prepareStatement(pageSql)) {
                    bind(query, pageValues);
                    try (ResultSet result = query.executeQuery()) {
                        while (result.next()) rows.add(convert(result));
                    }
                }
                return new Pagination<EmcEquiptPerCheckRecordBean>(total, request.getPageSize(), request.getPageNumber(), rows);
            }
        } catch (SQLException failure) {
            throw new IllegalStateException("period-check record query failed", failure);
        }
    }

    public List<EmcEquiptMaintBean> selectable(Long equipmentId) {
        if (equipmentId == null || equipmentId < 0L) throw new IllegalArgumentException("equipment id is invalid");
        try {
            OwnedPrincipal principal = trustedReadPrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            if (organizations == null || organizations.isEmpty()) return Collections.emptyList();
            StringBuilder sql = new StringBuilder("SELECT DISTINCT M.ID,M.EQUIPTCATEGORYID,M.MAINTEVENT,M.WARNINGDAYS,M.SAME_DAY,C.CODENAME AS CODENAME FROM T_EMC_EQUIPT_MAINT M JOIN T_EMC_EQUIPT_PER_CHECK_PLAN P ON P.MAINTID=M.ID JOIN T_EMC_EQUIPT E ON E.ID=P.EQUIPTID LEFT JOIN T_CORE_CODE C ON M.MAINTEVENT=C.CODE AND C.CODECATEGORYID='maintainEvent' WHERE E.ID=? AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION OP WHERE OP.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND OP.TARGETID=E.EQUIPTCATEGORYID AND OP.ORGID IN (");
            int i = 0;
            for (String ignored : organizations) { if (i++ > 0) sql.append(','); sql.append('?'); }
            sql.append(")) ORDER BY M.ID");
            List<EmcEquiptMaintBean> result = new ArrayList<EmcEquiptMaintBean>();
            try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql.toString())) {
                i = 1;
                query.setLong(i++, equipmentId);
                for (String org : organizations) query.setString(i++, org);
                try (ResultSet rows = query.executeQuery()) {
                    while (rows.next()) {
                        EmcEquiptMaintBean bean = new EmcEquiptMaintBean();
                        bean.setId(rows.getLong(1));
                        bean.setEquiptCategoryId(rows.getLong(2));
                        String code = rows.getString(3);
                        String label = rows.getString(6);
                        bean.setMaintEvent(label == null ? code : label);
                        bean.setWarningDays(rows.getLong(4));
                        bean.setSameDay(rows.getString(5));
                        result.add(bean);
                    }
                }
            }
            return result;
        } catch (SQLException failure) {
            throw new IllegalStateException("period-check selectable query failed", failure);
        }
    }

    public void deleteRecords(RestJsonWrapperBean wrapper) {
        if (wrapper == null) throw new IllegalArgumentException("period-check records are required");
        List<EmcEquiptPerCheckRecordBean> values = wrapper.parse(EmcEquiptPerCheckRecordBean.class);
        if (values.isEmpty() || values.size() > 100) throw new IllegalArgumentException("period-check records are required");
        try {
            OwnedWriteSupport.requireLoopback(currentRequest());
            OwnedPrincipal principal = trustedManagePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                try (PreparedStatement find = connection.prepareStatement("SELECT EQUIPTID,COMPLETEFLAG FROM T_EMC_EQUIPT_PER_CHECK_RECORD WHERE ID=?");
                        PreparedStatement delete = connection.prepareStatement("DELETE FROM T_EMC_EQUIPT_PER_CHECK_RECORD WHERE ID=?")) {
                    for (EmcEquiptPerCheckRecordBean value : values) {
                        if (value == null || value.getId() == null) throw new IllegalArgumentException("period-check record id is required");
                        find.setLong(1, value.getId());
                        try (ResultSet rows = find.executeQuery()) {
                            if (!rows.next()) throw OwnedEquipmentPerCheckPlanDao.denied();
                            OwnedEquipmentPerCheckPlanDao.requireEquipmentScope(connection, rows.getLong(1), organizations);
                            if ("2".equals(rows.getString(2))) throw new IllegalArgumentException("completed period-check record cannot be deleted");
                        }
                        delete.setLong(1, value.getId());
                        if (delete.executeUpdate() != 1) throw OwnedEquipmentPerCheckPlanDao.denied();
                    }
                }
                return null;
            });
            for (EmcEquiptPerCheckRecordBean value : values) {
                audit.record(new AuditEvent(java.time.Instant.now(), "equipment-per-check-record-delete", principal.getId(),
                        String.valueOf(value.getId()), AuditEvent.Outcome.ALLOWED, "isolated-write", null));
            }
        } catch (SQLException failure) {
            throw new IllegalStateException("period-check record delete failed", failure);
        }
    }

    public void complete(RestJsonWrapperBean wrapper) {
        if (wrapper == null) throw new IllegalArgumentException("period-check records are required");
        List<EmcEquiptPerCheckRecordBean> values = wrapper.parse(EmcEquiptPerCheckRecordBean.class);
        if (values.isEmpty() || values.size() > 100) throw new IllegalArgumentException("period-check records are required");
        for (EmcEquiptPerCheckRecordBean value : values) {
            if (value == null || value.getId() == null || value.getEquiptId() == null || value.getMaintId() == null || value.getMaintDate() == null)
                throw new IllegalArgumentException("period-check completion fields are required");
            cycleEnd(value.getMaintDate(), value.getCycle());
        }
        try {
            OwnedWriteSupport.requireLoopback(currentRequest());
            OwnedPrincipal principal = trustedManagePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                try (PreparedStatement updateRecord = connection.prepareStatement(
                        "UPDATE T_EMC_EQUIPT_PER_CHECK_RECORD SET COMPLETEFLAG='2',MAINTDATE=?,CYCLE=?,CHECKREASON=COALESCE(?,CHECKREASON),CHECKEQUIPT=COALESCE(?,CHECKEQUIPT),CHECKUSERS=COALESCE(?,CHECKUSERS),CHECKUSERNAMES=COALESCE(?,CHECKUSERNAMES),REMARK=COALESCE(?,REMARK) WHERE ID=? AND EQUIPTID=? AND MAINTID=? AND COMPLETEFLAG='1'")) {
                    for (EmcEquiptPerCheckRecordBean value : values) {
                        OwnedEquipmentPerCheckPlanDao.requireEquipmentScope(connection, value.getEquiptId(), organizations);
                        OwnedEquipmentPerCheckPlanDao.requireMaintenanceScope(connection, value.getEquiptId(), value.getMaintId(), organizations);
                        long end = cycleEnd(value.getMaintDate(), value.getCycle());
                        setDate(updateRecord, 1, value.getMaintDate());
                        updateRecord.setString(2, value.getCycle());
                        updateRecord.setString(3, value.getCheckReason());
                        updateRecord.setString(4, value.getCheckEquipt());
                        updateRecord.setString(5, value.getCheckUsers());
                        updateRecord.setString(6, value.getCheckUsernames());
                        updateRecord.setString(7, value.getRemark());
                        updateRecord.setLong(8, value.getId());
                        updateRecord.setLong(9, value.getEquiptId());
                        updateRecord.setLong(10, value.getMaintId());
                        if (updateRecord.executeUpdate() != 1)
                            throw new IllegalArgumentException("period-check record is already complete or out of scope");
                        long planId = findPlanId(connection, value.getEquiptId(), value.getMaintId());
                        try (PreparedStatement updatePlan = connection.prepareStatement(
                                "UPDATE T_EMC_EQUIPT_PER_CHECK_PLAN SET MAINTDATE=?,CYCLE=?,CHECKREASON=COALESCE(?,CHECKREASON),CHECKEQUIPT=COALESCE(?,CHECKEQUIPT),MAINTENDDATE=?,REMARK=COALESCE(?,REMARK) WHERE ID=?")) {
                            setDate(updatePlan, 1, value.getMaintDate());
                            updatePlan.setString(2, value.getCycle());
                            updatePlan.setString(3, value.getCheckReason());
                            updatePlan.setString(4, value.getCheckEquipt());
                            updatePlan.setTimestamp(5, new Timestamp(end));
                            updatePlan.setString(6, value.getRemark());
                            updatePlan.setLong(7, planId);
                            if (updatePlan.executeUpdate() != 1) throw new IllegalArgumentException("period-check plan is missing");
                        }
                    }
                }
                return null;
            });
            for (EmcEquiptPerCheckRecordBean value : values) {
                audit.record(new AuditEvent(java.time.Instant.now(), "equipment-per-check-record-complete", principal.getId(),
                        String.valueOf(value.getId()), AuditEvent.Outcome.ALLOWED, "isolated-write; plan-update", null));
            }
        } catch (SQLException failure) {
            throw new IllegalStateException("period-check record completion failed", failure);
        }
    }

    private static long findPlanId(Connection connection, long equipmentId, long maintenanceId) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement(
                "SELECT ID FROM (SELECT ID FROM T_EMC_EQUIPT_PER_CHECK_PLAN WHERE EQUIPTID=? AND MAINTID=? ORDER BY ID) WHERE ROWNUM=1")) {
            query.setLong(1, equipmentId);
            query.setLong(2, maintenanceId);
            try (ResultSet rows = query.executeQuery()) {
                if (!rows.next()) throw new IllegalArgumentException("period-check plan is missing");
                return rows.getLong(1);
            }
        }
    }

    private static long cycleEnd(LocalDateTime date, String cycle) {
        if (date == null) throw new IllegalArgumentException("period-check maintenance date is required");
        if (cycle == null || cycle.trim().isEmpty()) throw new IllegalArgumentException("period-check cycle is required");
        int index;
        try { index = Integer.parseInt(cycle); }
        catch (RuntimeException failure) { throw new IllegalArgumentException("period-check cycle is invalid", failure); }
        String[] parts = {"0-0-1", "0-0-7", "0-1-0", "0-2-0", "0-3-0", "0-4-0", "0-6-0", "1-0-0", "2-0-0", "3-0-0", "5-0-0"};
        if (index < 1 || index > parts.length) throw new IllegalArgumentException("period-check cycle is invalid");
        String[] values = parts[index - 1].split("-");
        return Timestamp.valueOf(date.plusYears(Long.parseLong(values[0])).plusMonths(Long.parseLong(values[1]))
                .plusDays(Long.parseLong(values[2])).minusDays(1)).getTime();
    }

    private static void validate(EmcEquiptPerCheckRecordBean value) {
        if (value == null || value.getEquiptId() == null || value.getEquiptId() < 0L)
            throw new IllegalArgumentException("equipment id is required");
        if (value.getMaintId() == null || value.getMaintId() < 0L)
            throw new IllegalArgumentException("maintenance event is required");
        if (value.getCompleteFlag() != null && !"1".equals(value.getCompleteFlag()))
            throw new IllegalArgumentException("period-check record must start incomplete");
    }

    private static void appendFilters(QueryRequest request, List<String> clauses, List<Object> values) {
        for (FilterParamPattern group : request.getFilterPatterns()) {
            String pattern = group.getMatchPattern();
            for (Map.Entry<String, Object> entry : group.getMap().entrySet()) {
                String field = recordColumn(entry.getKey());
                Object value = entry.getValue();
                String expression = recordExpression(field);
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
                    throw new IllegalArgumentException("unsupported period-check record filter pattern: " + pattern);
                }
            }
        }
        for (String key : request.getMapperParameters().keySet()) {
            if (!"target_type".equals(key) && !"orgid_seq".equals(key))
                throw new IllegalArgumentException("unsupported period-check record query parameter: " + key);
        }
        if (!request.getAdvancedPatterns().isEmpty() || request.getMultiPropertyPattern() != null)
            throw new IllegalArgumentException("unsupported period-check record query shape");
    }

    private static String recordColumn(String key) {
        String normalized = key == null ? "" : key.toUpperCase(Locale.ROOT);
        for (String suffix : new String[]{"_CISC", "_EQ", "_SEQ", "_DG", "_DGOE", "_DL", "_DLOE"}) {
            if (normalized.endsWith(suffix)) {
                normalized = normalized.substring(0, normalized.length() - suffix.length());
                break;
            }
        }
        if ("ID".equals(normalized) || "EQUIPTID".equals(normalized) || "MAINTID".equals(normalized)
                || "MAINTDATE".equals(normalized) || "CYCLE".equals(normalized) || "CHECKREASON".equals(normalized)
                || "CHECKEQUIPT".equals(normalized) || "COMPLETEFLAG".equals(normalized) || "CHECKUSERS".equals(normalized)
                || "CHECKUSERNAMES".equals(normalized) || "REMARK".equals(normalized) || "CODENAME".equals(normalized)) return normalized;
        throw new IllegalArgumentException("unsupported period-check record filter column: " + key);
    }

    private static String recordExpression(String field) { return "CODENAME".equals(field) ? "C.CODENAME" : "R." + field; }

    private static String order(QueryRequest request) {
        if (request.getOrderParameters().isEmpty()) return "Q.ID DESC";
        List<String> parts = new ArrayList<String>();
        for (Pair<String, String> pair : request.getOrderParameters()) {
            String field = recordColumn(pair.getFirst());
            parts.add("Q." + field + ("desc".equalsIgnoreCase(pair.getSecond()) ? " DESC" : " ASC") + " NULLS LAST");
        }
        return String.join(",", parts);
    }

    private String scopeSql(long id, Set<String> organizations, List<Object> values) {
        StringBuilder sql = new StringBuilder("R.EQUIPTID=? AND EXISTS (SELECT 1 FROM T_EMC_EQUIPT E WHERE E.ID=R.EQUIPTID AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION OP WHERE OP.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND OP.TARGETID=E.EQUIPTCATEGORYID AND OP.ORGID IN (");
        values.add(id);
        int i = 0;
        for (String org : organizations) { if (i++ > 0) sql.append(','); sql.append('?'); values.add(org); }
        return sql.append(")))").toString();
    }

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
        if (!(attrs instanceof ServletRequestAttributes)) throw OwnedEquipmentPerCheckPlanDao.denied();
        HttpServletRequest request = ((ServletRequestAttributes) attrs).getRequest();
        if (request == null) throw OwnedEquipmentPerCheckPlanDao.denied();
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

    private static void setDate(PreparedStatement statement, int index, LocalDateTime value) throws SQLException {
        if (value == null) statement.setNull(index, java.sql.Types.TIMESTAMP); else statement.setTimestamp(index, Timestamp.valueOf(value));
    }

    private static EmcEquiptPerCheckRecordBean convert(ResultSet rows) throws SQLException {
        EmcEquiptPerCheckRecordBean bean = new EmcEquiptPerCheckRecordBean();
        bean.setId(rows.getLong("ID"));
        bean.setEquiptId(getLong(rows, "EQUIPTID"));
        bean.setMaintId(getLong(rows, "MAINTID"));
        bean.setMaintEvent(rows.getString("MAINTEVENT"));
        bean.setExt$Item("codename", rows.getString("CODENAME"));
        bean.setMaintDate(date(rows, "MAINTDATE"));
        bean.setCycle(rows.getString("CYCLE"));
        bean.setCheckReason(rows.getString("CHECKREASON"));
        bean.setCheckEquipt(rows.getString("CHECKEQUIPT"));
        bean.setCompleteFlag(rows.getString("COMPLETEFLAG"));
        bean.setCheckUsers(rows.getString("CHECKUSERS"));
        bean.setCheckUsernames(rows.getString("CHECKUSERNAMES"));
        bean.setRemark(rows.getString("REMARK"));
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
