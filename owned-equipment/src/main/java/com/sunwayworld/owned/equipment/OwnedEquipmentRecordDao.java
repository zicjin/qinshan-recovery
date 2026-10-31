package com.sunwayworld.owned.equipment;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptRecordBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptRecordDao;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
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
import java.sql.Date;
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

/** Isolated Oracle equipment maintenance-record adapter. */
public final class OwnedEquipmentRecordDao extends MybatisDaoSupport<EmcEquiptRecordBean, Long>
        implements EmcEquiptRecordDao {
    private final DataSource dataSource;
    private final JdbcPersonnelIdentityProvider provider;
    private final OwnedIdentityHttpBoundary boundary;
    private final AuditEventSink audit;

    public OwnedEquipmentRecordDao(DataSource dataSource, JdbcPersonnelIdentityProvider provider,
            OwnedIdentityHttpBoundary boundary, AuditEventSink audit) {
        this.dataSource = dataSource;
        this.provider = provider;
        this.boundary = boundary;
        this.audit = audit;
    }

    public Long insertRecord(RestJsonWrapperBean wrapper) {
        if (wrapper == null) throw new IllegalArgumentException("maintenance record body is required");
        EmcEquiptRecordBean value = wrapper.parseUnique(EmcEquiptRecordBean.class);
        if (value == null || value.getEquiptId() == null || value.getEquiptId().longValue() < 0L)
            throw new IllegalArgumentException("equipment id is required");
        if (value.getMaintId() == null || value.getMaintId().longValue() < 0L)
            throw new IllegalArgumentException("maintenance event is required");
        String completeFlag = value.getCompleteFlag() == null ? "1" : value.getCompleteFlag();
        if (!"1".equals(completeFlag)) throw new IllegalArgumentException("maintenance record must start incomplete");
        try {
            OwnedPrincipal principal = trustedManagePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            long id = OwnedWriteSupport.transaction(dataSource, connection -> {
                    requirePlannedMaintenance(connection, value.getEquiptId().longValue(), value.getMaintId().longValue(), organizations);
                    long generatedId = OwnedWriteSupport.nextIdentity(connection);
                    try (PreparedStatement insert = connection.prepareStatement(
                            "INSERT INTO T_EMC_EQUIPT_RECORD (ID,EQUIPTID,MAINTID,VERIFYORG,CERTNO,CERTCATEGORY,CERTEFFECTDATE,STATUS,STARTSTATUS,AIRSATURATION,ADDICE,MAINTSITUATION,REMARK,COMPLETEFLAG,MAINDATE,MAINTENDDATE,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSTIMESTAMP,?,?)")) {
                        int index = 1;
                        insert.setLong(index++, generatedId);
                        insert.setLong(index++, value.getEquiptId().longValue());
                        insert.setLong(index++, value.getMaintId().longValue());
                        insert.setString(index++, value.getVerifyOrg());
                        insert.setString(index++, value.getCertNo());
                        insert.setString(index++, value.getCertCategory());
                        setDate(insert, index++, value.getCertEffectDate());
                        insert.setString(index++, value.getStatus());
                        insert.setString(index++, value.getStartStatus());
                        insert.setString(index++, value.getAirSaturation());
                        insert.setString(index++, value.getAddIce());
                        insert.setString(index++, value.getMaintSituation());
                        insert.setString(index++, value.getRemark());
                        insert.setString(index++, completeFlag);
                        setDate(insert, index++, value.getMainDate());
                        setDate(insert, index++, value.getMaintEndDate());
                        insert.setString(index++, principal.getId());
                        insert.setString(index++, principal.getId());
                        insert.setString(index++, principal.getOrgId());
                        insert.setString(index, principal.getOrgName());
                        insert.executeUpdate();
                    }
                    return generatedId;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "equipment-record-insert", principal.getId(),
                    String.valueOf(id), AuditEvent.Outcome.ALLOWED, "isolated-write; incomplete-record", null));
            return Long.valueOf(id);
        } catch (SQLException failure) {
            throw new IllegalStateException("equipment maintenance record write failed", failure);
        }
    }

    /** Complete records and carry the supplier's plan/equipment validity updates in one transaction. */
    public void completeRecords(RestJsonWrapperBean wrapper) {
        if (wrapper == null) throw new IllegalArgumentException("maintenance records are required");
        List<EmcEquiptRecordBean> records = wrapper.parse(EmcEquiptRecordBean.class);
        if (records.isEmpty() || records.size() > 100) throw new IllegalArgumentException("maintenance records are required");
        for (EmcEquiptRecordBean record : records) {
            if (record == null || record.getId() == null || record.getId().longValue() < 0L
                    || record.getEquiptId() == null || record.getMaintId() == null
                    || record.getMaintEndDate() == null) {
                throw new IllegalArgumentException("complete maintenance record fields are required");
            }
            if ("2".equals(record.getCompleteFlag())) {
                throw new IllegalArgumentException("maintenance record is already complete");
            }
        }
        try {
            OwnedPrincipal principal = trustedManagePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                    for (EmcEquiptRecordBean record : records) {
                        requireRecordScope(connection, record, organizations);
                    }
                    try (PreparedStatement updateRecord = connection.prepareStatement(
                            "UPDATE T_EMC_EQUIPT_RECORD SET COMPLETEFLAG='2' WHERE ID=? AND EQUIPTID=? AND MAINTID=?" );
                         PreparedStatement updatePlan = connection.prepareStatement(
                            "UPDATE T_EMC_EQUIPT_PLAN SET CYCLE=?,MAINTDATE=?,MAINTENDDATE=?,VERIFYORG=?,GIST=?,CERTCATEGORY=?,REMARK=? WHERE EQUIPTID=? AND MAINTID=?" );
                         PreparedStatement updateEquipment = connection.prepareStatement(
                            "UPDATE T_EMC_EQUIPT SET SOURCEDATE=? WHERE ID=?" )) {
                        for (EmcEquiptRecordBean record : records) {
                            updateRecord.setLong(1, record.getId().longValue());
                            updateRecord.setLong(2, record.getEquiptId().longValue());
                            updateRecord.setLong(3, record.getMaintId().longValue());
                            if (updateRecord.executeUpdate() != 1) throw new IllegalStateException("maintenance record completion did not update one row");

                            updatePlan.setString(1, record.getExt$Item("cycle"));
                            setDate(updatePlan, 2, record.getMainDate());
                            setDate(updatePlan, 3, record.getMaintEndDate());
                            updatePlan.setString(4, record.getVerifyOrg());
                            updatePlan.setString(5, record.getExt$Item("gist"));
                            updatePlan.setString(6, record.getCertCategory());
                            updatePlan.setString(7, record.getRemark());
                            updatePlan.setLong(8, record.getEquiptId().longValue());
                            updatePlan.setLong(9, record.getMaintId().longValue());
                            updatePlan.executeUpdate();

                            updateEquipment.setDate(1, Date.valueOf(record.getMaintEndDate().toLocalDate()));
                            updateEquipment.setLong(2, record.getEquiptId().longValue());
                            if (updateEquipment.executeUpdate() != 1) throw new IllegalStateException("equipment validity update did not update one row");
                        }
                    }
                    return null;
            });
            for (EmcEquiptRecordBean record : records) {
                audit.record(new AuditEvent(java.time.Instant.now(), "equipment-record-complete", principal.getId(),
                        String.valueOf(record.getId()), AuditEvent.Outcome.ALLOWED, "isolated-write; plan-and-source-date-update", null));
            }
        } catch (SQLException failure) {
            throw new IllegalStateException("equipment maintenance record completion failed", failure);
        }
    }

    /** Delete only records that are visible through the caller's category organization scope. */
    public void deleteRecords(RestJsonWrapperBean wrapper) {
        if (wrapper == null) throw new IllegalArgumentException("maintenance record ids are required");
        List<Long> ids = wrapper.parseId(Long.class);
        if (ids.isEmpty() || ids.size() > 100) throw new IllegalArgumentException("maintenance record ids are required");
        try {
            OwnedPrincipal principal = trustedManagePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                    for (Long id : ids) {
                        if (id == null || !recordBelongsToScope(connection, id.longValue(), organizations)) throw denied();
                    }
                    try (PreparedStatement delete = connection.prepareStatement("DELETE FROM T_EMC_EQUIPT_RECORD WHERE ID=?")) {
                        for (Long id : ids) { delete.setLong(1, id.longValue()); delete.addBatch(); }
                        delete.executeBatch();
                    }
                    return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "equipment-record-delete", principal.getId(),
                    ids.toString(), AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) {
            throw new IllegalStateException("equipment maintenance record delete failed", failure);
        }
    }

    public Page<EmcEquiptRecordBean> readRecords(Long equipmentId, RestJsonWrapperBean wrapper) {
        if (equipmentId == null || equipmentId.longValue() < 0L) throw new IllegalArgumentException("equipment id is invalid");
        if (wrapper == null) throw new IllegalArgumentException("maintenance record query is required");
        try {
            OwnedPrincipal principal = trustedReadPrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            QueryRequest request = QueryRequest.parse(wrapper.getRequestJson(), wrapper.getParamMap());
            if (organizations.isEmpty()) return empty(request);
            List<String> clauses = new ArrayList<String>();
            List<Object> values = new ArrayList<Object>();
            String scope = scopeSql(equipmentId.longValue(), organizations, values);
            appendFilters(request, clauses, values);
            String where = scope + (clauses.isEmpty() ? "" : " AND " + String.join(" AND ", clauses));
            String countSql = "SELECT COUNT(1) FROM T_EMC_EQUIPT_RECORD R WHERE " + where;
            String pageSql = "SELECT * FROM (SELECT R.*, M.MAINTEVENT AS MAINT_EVENT_NAME, ROW_NUMBER() OVER (ORDER BY R.ID DESC) RN FROM T_EMC_EQUIPT_RECORD R LEFT JOIN T_EMC_EQUIPT_MAINT M ON R.MAINTID=M.ID WHERE " + where + ") WHERE RN BETWEEN ? AND ? ORDER BY ID DESC";
            int from = (request.getPageNumber() - 1) * request.getPageSize() + 1;
            int to = request.getPageNumber() * request.getPageSize();
            try (Connection connection = dataSource.getConnection()) {
                int total;
                try (PreparedStatement count = connection.prepareStatement(countSql)) {
                    bind(count, values);
                    try (ResultSet rows = count.executeQuery()) { rows.next(); total = rows.getInt(1); }
                }
                List<EmcEquiptRecordBean> rows = new ArrayList<EmcEquiptRecordBean>();
                List<Object> pageValues = new ArrayList<Object>(values);
                pageValues.add(Integer.valueOf(from)); pageValues.add(Integer.valueOf(to));
                try (PreparedStatement query = connection.prepareStatement(pageSql)) {
                    bind(query, pageValues);
                    try (ResultSet result = query.executeQuery()) {
                        while (result.next()) rows.add(convert(result));
                    }
                }
                return new Pagination<EmcEquiptRecordBean>(total, request.getPageSize(), request.getPageNumber(), rows);
            }
        } catch (IllegalArgumentException failure) {
            throw failure;
        } catch (SQLException failure) {
            throw new IllegalStateException("equipment maintenance record query failed", failure);
        }
    }

    public List<EmcEquiptMaintBean> selectableMaintenances(Long equipmentId) {
        if (equipmentId == null || equipmentId.longValue() < 0L) throw new IllegalArgumentException("equipment id is invalid");
        try {
            OwnedPrincipal principal = trustedReadPrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            if (organizations.isEmpty()) return Collections.emptyList();
            StringBuilder sql = new StringBuilder(
                    "SELECT DISTINCT M.ID,M.EQUIPTCATEGORYID,M.MAINTEVENT,M.WARNINGDAYS,M.SAME_DAY FROM T_EMC_EQUIPT_PLAN P JOIN T_EMC_EQUIPT E ON E.ID=P.EQUIPTID JOIN T_EMC_EQUIPT_MAINT M ON M.ID=P.MAINTID WHERE P.EQUIPTID=? AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION OP WHERE OP.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND OP.TARGETID=E.EQUIPTCATEGORYID AND OP.ORGID IN (");
            List<String> values = new ArrayList<String>();
            values.add(String.valueOf(equipmentId));
            appendOrganizationPlaceholders(sql, organizations, values);
            sql.append(") ) ORDER BY M.ID");
            try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql.toString())) {
                bindStrings(query, values);
                try (ResultSet rows = query.executeQuery()) {
                    List<EmcEquiptMaintBean> result = new ArrayList<EmcEquiptMaintBean>();
                    while (rows.next()) {
                        EmcEquiptMaintBean bean = new EmcEquiptMaintBean();
                        bean.setId(getLong(rows, "ID")); bean.setEquiptCategoryId(getLong(rows, "EQUIPTCATEGORYID"));
                        bean.setMaintEvent(rows.getString("MAINTEVENT")); bean.setWarningDays(getLong(rows, "WARNINGDAYS"));
                        bean.setSameDay(rows.getString("SAME_DAY")); result.add(bean);
                    }
                    return result;
                }
            }
        } catch (SQLException failure) {
            throw new IllegalStateException("equipment maintenance selectable query failed", failure);
        }
    }

    private void appendFilters(QueryRequest request, List<String> clauses, List<Object> values) {
        for (com.sunwayworld.framework.mybatis.mapper.FilterParamPattern group : request.getFilterPatterns()) {
            String pattern = group.getMatchPattern();
            for (Map.Entry<String, Object> entry : group.getMap().entrySet()) {
                String field = recordColumn(entry.getKey()); Object value = entry.getValue();
                if ("CISC".equals(pattern) || "SC".equals(pattern)) {
                    clauses.add("UPPER(R." + field + ") LIKE ?"); values.add("%" + String.valueOf(value).toUpperCase(Locale.ROOT) + "%");
                } else if ("EQ".equals(pattern) || "SEQ".equals(pattern)) {
                    clauses.add("R." + field + (value == null ? " IS NULL" : "=?")); if (value != null) values.add(value);
                } else if ("DG".equals(pattern) || "DGOE".equals(pattern) || "DL".equals(pattern) || "DLOE".equals(pattern)) {
                    clauses.add("R." + field + ("DG".equals(pattern) ? ">" : "DGOE".equals(pattern) ? ">=" : "DL".equals(pattern) ? "<" : "<=") + " TO_DATE(?, 'YYYY-MM-DD')");
                    values.add(String.valueOf(value));
                } else throw new IllegalArgumentException("unsupported maintenance record filter pattern: " + pattern);
            }
        }
        for (String key : request.getMapperParameters().keySet()) {
            if (!"target_type".equals(key) && !"orgid_seq".equals(key)) throw new IllegalArgumentException("unsupported equipment record query parameter: " + key);
        }
    }

    private static String recordColumn(String key) {
        String normalized = key == null ? "" : key.toUpperCase(Locale.ROOT);
        String[] suffixes = {"_CISC", "_EQ", "_SEQ", "_DG", "_DGOE", "_DL", "_DLOE"};
        for (String suffix : suffixes) if (normalized.endsWith(suffix)) normalized = normalized.substring(0, normalized.length() - suffix.length());
        if ("ID".equals(normalized) || "EQUIPTID".equals(normalized) || "MAINTID".equals(normalized)
                || "VERIFYORG".equals(normalized) || "CERTNO".equals(normalized) || "CERTCATEGORY".equals(normalized)
                || "STATUS".equals(normalized) || "STARTSTATUS".equals(normalized) || "AIRSATURATION".equals(normalized)
                || "ADDICE".equals(normalized) || "MAINTSITUATION".equals(normalized) || "REMARK".equals(normalized)
                || "COMPLETEFLAG".equals(normalized) || "MAINDATE".equals(normalized) || "MAINTENDDATE".equals(normalized)) return normalized;
        throw new IllegalArgumentException("unsupported equipment record filter column: " + key);
    }

    private String scopeSql(long equipmentId, Set<String> organizations, List<Object> values) {
        StringBuilder sql = new StringBuilder("R.EQUIPTID=? AND EXISTS (SELECT 1 FROM T_EMC_EQUIPT E WHERE E.ID=R.EQUIPTID AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION OP WHERE OP.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND OP.TARGETID=E.EQUIPTCATEGORYID AND OP.ORGID IN (");
        values.add(Long.valueOf(equipmentId));
        int index = 0; for (String organization : organizations) { if (index++ > 0) sql.append(','); sql.append('?'); values.add(organization); }
        return sql.append(")))").toString();
    }

    private static void requirePlannedMaintenance(Connection connection, long equipmentId, long maintId, Set<String> organizations) throws SQLException {
        if (organizations == null || organizations.isEmpty()) throw denied();
        StringBuilder sql = new StringBuilder("SELECT 1 FROM T_EMC_EQUIPT_PLAN P JOIN T_EMC_EQUIPT E ON E.ID=P.EQUIPTID WHERE P.EQUIPTID=? AND P.MAINTID=? AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION OP WHERE OP.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND OP.TARGETID=E.EQUIPTCATEGORYID AND OP.ORGID IN (");
        List<String> values = new ArrayList<String>(); values.add(String.valueOf(equipmentId)); values.add(String.valueOf(maintId));
        appendOrganizationPlaceholders(sql, organizations, values); sql.append(") )");
        try (PreparedStatement query = connection.prepareStatement(sql.toString())) {
            bindStrings(query, values); try (ResultSet rows = query.executeQuery()) { if (!rows.next()) throw denied(); }
        }
    }

    private static void appendOrganizationPlaceholders(StringBuilder sql, Set<String> organizations, List<String> values) {
        int index = 0; for (String organization : organizations) { if (index++ > 0) sql.append(','); sql.append('?'); values.add(organization); }
    }

    private static void requireRecordScope(Connection connection, EmcEquiptRecordBean record,
            Set<String> organizations) throws SQLException {
        if (organizations == null || organizations.isEmpty()) throw denied();
        StringBuilder sql = new StringBuilder("SELECT R.COMPLETEFLAG FROM T_EMC_EQUIPT_RECORD R JOIN T_EMC_EQUIPT E ON E.ID=R.EQUIPTID WHERE R.ID=? AND R.EQUIPTID=? AND R.MAINTID=? AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION OP WHERE OP.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND OP.TARGETID=E.EQUIPTCATEGORYID AND OP.ORGID IN (");
        int index = 0; for (String ignored : organizations) { if (index++ > 0) sql.append(','); sql.append('?'); }
        sql.append("))");
        try (PreparedStatement query = connection.prepareStatement(sql.toString())) {
            index = 1; query.setLong(index++, record.getId().longValue()); query.setLong(index++, record.getEquiptId().longValue()); query.setLong(index++, record.getMaintId().longValue());
            for (String organization : organizations) query.setString(index++, organization);
            try (ResultSet rows = query.executeQuery()) {
                if (!rows.next()) throw denied();
                if ("2".equals(rows.getString(1))) throw new IllegalArgumentException("maintenance record is already complete");
            }
        }
    }

    private static boolean recordBelongsToScope(Connection connection, long id, Set<String> organizations) throws SQLException {
        if (organizations == null || organizations.isEmpty()) return false;
        StringBuilder sql = new StringBuilder("SELECT 1 FROM T_EMC_EQUIPT_RECORD R JOIN T_EMC_EQUIPT E ON E.ID=R.EQUIPTID WHERE R.ID=? AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION OP WHERE OP.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND OP.TARGETID=E.EQUIPTCATEGORYID AND OP.ORGID IN (");
        int index = 0; for (String ignored : organizations) { if (index++ > 0) sql.append(','); sql.append('?'); }
        sql.append("))");
        try (PreparedStatement query = connection.prepareStatement(sql.toString())) {
            index = 1; query.setLong(index++, id); for (String organization : organizations) query.setString(index++, organization);
            try (ResultSet rows = query.executeQuery()) { return rows.next(); }
        }
    }

    private static void bind(PreparedStatement statement, List<Object> values) throws SQLException {
        int index = 1; for (Object value : values) { if (value == null) statement.setNull(index++, java.sql.Types.VARCHAR); else if (value instanceof Number) statement.setLong(index++, ((Number) value).longValue()); else statement.setString(index++, String.valueOf(value)); }
    }
    private static void bindStrings(PreparedStatement statement, List<String> values) throws SQLException { int index = 1; for (String value : values) statement.setString(index++, value); }
    private static void setDate(PreparedStatement statement, int index, LocalDateTime value) throws SQLException { if (value == null) statement.setNull(index, java.sql.Types.TIMESTAMP); else statement.setTimestamp(index, Timestamp.valueOf(value)); }
    private static Long getLong(ResultSet rows, String column) throws SQLException { long value = rows.getLong(column); return rows.wasNull() ? null : Long.valueOf(value); }
    private static LocalDateTime date(ResultSet rows, String column) throws SQLException { Timestamp value = rows.getTimestamp(column); return value == null ? null : value.toLocalDateTime(); }
    private static AccessDeniedException denied() { return new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED"); }

    private OwnedPrincipal trustedReadPrincipal() throws SQLException { HttpServletRequest request = currentRequest(); HttpSession session = request.getSession(false); boundary.requirePermission(session, OwnedIdentityProvider.EQUIPMENT_READ); return boundary.currentPrincipal(session); }
    private OwnedPrincipal trustedManagePrincipal() throws SQLException { HttpServletRequest request = currentRequest(); HttpSession session = request.getSession(false); boundary.requirePermission(session, OwnedIdentityProvider.EQUIPMENT_MANAGE); return boundary.currentPrincipal(session); }
    private static HttpServletRequest currentRequest() { RequestAttributes attributes = RequestContextHolder.getRequestAttributes(); if (!(attributes instanceof ServletRequestAttributes)) throw denied(); HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest(); if (request == null) throw denied(); return request; }

    private static EmcEquiptRecordBean convert(ResultSet rows) throws SQLException {
        EmcEquiptRecordBean bean = new EmcEquiptRecordBean();
        bean.setId(getLong(rows, "ID")); bean.setEquiptId(getLong(rows, "EQUIPTID")); bean.setMaintId(getLong(rows, "MAINTID"));
        bean.setMaintEvent(rows.getString("MAINT_EVENT_NAME")); bean.setVerifyOrg(rows.getString("VERIFYORG")); bean.setCertNo(rows.getString("CERTNO"));
        bean.setCertCategory(rows.getString("CERTCATEGORY")); bean.setCertEffectDate(date(rows, "CERTEFFECTDATE")); bean.setStatus(rows.getString("STATUS"));
        bean.setStartStatus(rows.getString("STARTSTATUS")); bean.setAirSaturation(rows.getString("AIRSATURATION")); bean.setAddIce(rows.getString("ADDICE"));
        bean.setMaintSituation(rows.getString("MAINTSITUATION")); bean.setRemark(rows.getString("REMARK")); bean.setCompleteFlag(rows.getString("COMPLETEFLAG"));
        bean.setMainDate(date(rows, "MAINDATE")); bean.setMaintEndDate(date(rows, "MAINTENDDATE")); bean.setCreatedById(rows.getString("CREATEDBYID"));
        bean.setCreatedByName(rows.getString("CREATEDBYNAME")); bean.setCreatedTime(date(rows, "CREATEDTIME")); bean.setCreatedByOrgId(rows.getString("CREATEDBYORGID"));
        bean.setCreatedByOrgName(rows.getString("CREATEDBYORGNAME")); bean.setExt$Item("codename", rows.getString("MAINT_EVENT_NAME")); return bean;
    }

    private static Pagination<EmcEquiptRecordBean> empty(QueryRequest request) {
        return new Pagination<EmcEquiptRecordBean>(0, request.getPageSize(), request.getPageNumber(), Collections.<EmcEquiptRecordBean>emptyList());
    }

    @Override public List<Map<String, Object>> selectByCondition(MapperParameter parameter) { throw new UnsupportedOperationException("Equipment record mapper query is not implemented"); }
}
