package com.sunwayworld.owned.equipment;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPlanBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptPlanDao;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Isolated Oracle equipment maintenance-plan adapter. */
public final class OwnedEquipmentPlanDao extends MybatisDaoSupport<EmcEquiptPlanBean, Long> implements EmcEquiptPlanDao {
    private final DataSource dataSource;
    private final JdbcPersonnelIdentityProvider provider;
    private final OwnedIdentityHttpBoundary boundary;
    private final AuditEventSink audit;

    public OwnedEquipmentPlanDao(DataSource dataSource, JdbcPersonnelIdentityProvider provider,
            OwnedIdentityHttpBoundary boundary, AuditEventSink audit) {
        this.dataSource = dataSource; this.provider = provider; this.boundary = boundary; this.audit = audit;
    }

    public Long insertPlan(RestJsonWrapperBean wrapper) {
        if (wrapper == null) throw new IllegalArgumentException("maintenance plan body is required");
        EmcEquiptPlanBean value = wrapper.parseUnique(EmcEquiptPlanBean.class);
        if (value == null || value.getEquiptId() == null || value.getEquiptId().longValue() < 0L)
            throw new IllegalArgumentException("equipment id is required");
        if (value.getMaintId() == null || value.getMaintId().longValue() < 0L)
            throw new IllegalArgumentException("maintenance event is required");
        if (value.getWarningDays() != null && (value.getWarningDays().longValue() < 0L || value.getWarningDays().longValue() > 36500L))
            throw new IllegalArgumentException("warning days is invalid");
        try {
            OwnedPrincipal principal = trustedManagePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            long id = OwnedWriteSupport.transaction(dataSource, connection -> {
                    requireEquipmentScope(connection, value.getEquiptId().longValue(), organizations);
                    requireMaintenanceScope(connection, value.getEquiptId().longValue(), value.getMaintId().longValue(), organizations);
                    long generatedId = OwnedWriteSupport.nextIdentity(connection);
                    try (PreparedStatement insert = connection.prepareStatement(
                            "INSERT INTO T_EMC_EQUIPT_PLAN (ID,EQUIPTID,MAINTID,MAINTDATE,CYCLE,WARNINGDAYS,ORGID,MAINTENDDATE,REMARK,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?,?,?,?,SYSTIMESTAMP,?,?)")) {
                        insert.setLong(1, generatedId); insert.setLong(2, value.getEquiptId().longValue()); insert.setLong(3, value.getMaintId().longValue());
                        setDate(insert, 4, value.getMaintDate()); insert.setString(5, value.getCycle()); setLong(insert, 6, value.getWarningDays());
                        insert.setString(7, principal.getOrgId()); setDate(insert, 8, value.getMaintEndDate()); insert.setString(9, value.getRemark());
                        insert.setString(10, principal.getId()); insert.setString(11, principal.getId()); insert.setString(12, principal.getOrgId()); insert.setString(13, principal.getOrgName());
                        insert.executeUpdate();
                    }
                    return generatedId;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "equipment-plan-insert", principal.getId(), String.valueOf(id),
                    AuditEvent.Outcome.ALLOWED, "isolated-write; principal-org-id", null));
            return Long.valueOf(id);
        } catch (SQLException failure) { throw new IllegalStateException("equipment plan write failed", failure); }
    }

    public Page<EmcEquiptPlanBean> readPlans(Long equipmentId, RestJsonWrapperBean wrapper) {
        if (equipmentId == null || equipmentId.longValue() < 0L) throw new IllegalArgumentException("equipment id is invalid");
        try {
            OwnedPrincipal principal = trustedReadPrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            QueryRequest request = QueryRequest.parse(wrapper.getRequestJson(), wrapper.getParamMap());
            if (organizations.isEmpty()) return new Pagination<EmcEquiptPlanBean>(0, request.getPageSize(), request.getPageNumber(), Collections.<EmcEquiptPlanBean>emptyList());
            List<String> params = new ArrayList<String>();
            List<Object> values = new ArrayList<Object>();
            String scope = scopeSql(equipmentId.longValue(), organizations, values);
            appendFilters(request, params, values);
            String where = scope + (params.isEmpty() ? "" : " AND " + String.join(" AND ", params));
            String countSql = "SELECT COUNT(1) FROM T_EMC_EQUIPT_PLAN P WHERE " + where;
            String pageSql = "SELECT * FROM (SELECT P.*, M.MAINTEVENT AS MAINT_EVENT_NAME, ROW_NUMBER() OVER (ORDER BY P.ID DESC) RN FROM T_EMC_EQUIPT_PLAN P LEFT JOIN T_EMC_EQUIPT_MAINT M ON M.ID=P.MAINTID WHERE " + where + ") WHERE RN BETWEEN ? AND ? ORDER BY ID DESC";
            int from = (request.getPageNumber() - 1) * request.getPageSize() + 1;
            int to = request.getPageNumber() * request.getPageSize();
            try (Connection connection = dataSource.getConnection()) {
                int total;
                try (PreparedStatement count = connection.prepareStatement(countSql)) { bind(count, values); try (ResultSet rows = count.executeQuery()) { rows.next(); total = rows.getInt(1); } }
                List<EmcEquiptPlanBean> rows = new ArrayList<EmcEquiptPlanBean>();
                List<Object> pageValues = new ArrayList<Object>(values); pageValues.add(Integer.valueOf(from)); pageValues.add(Integer.valueOf(to));
                try (PreparedStatement query = connection.prepareStatement(pageSql)) { bind(query, pageValues); try (ResultSet result = query.executeQuery()) { while (result.next()) rows.add(convert(result)); } }
                return new Pagination<EmcEquiptPlanBean>(total, request.getPageSize(), request.getPageNumber(), rows);
            }
        } catch (IllegalArgumentException failure) { throw failure;
        } catch (SQLException failure) { throw new IllegalStateException("equipment plan query failed", failure); }
    }

    /** Return maintenance events valid for an authorized equipment category. */
    public List<EmcEquiptMaintBean> selectableMaintenance(Long equipmentId, RestJsonWrapperBean wrapper) {
        if (equipmentId == null || equipmentId.longValue() < 0L) throw new IllegalArgumentException("equipment id is invalid");
        try {
            OwnedPrincipal principal = trustedManagePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            List<EmcEquiptMaintBean> result = new ArrayList<EmcEquiptMaintBean>();
            StringBuilder sql = new StringBuilder("SELECT DISTINCT M.ID,M.EQUIPTCATEGORYID,M.MAINTEVENT,M.WARNINGDAYS,M.SAME_DAY FROM T_EMC_EQUIPT_MAINT M JOIN T_EMC_EQUIPT E ON E.EQUIPTCATEGORYID=M.EQUIPTCATEGORYID WHERE E.ID=? AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION OP WHERE OP.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND OP.TARGETID=E.EQUIPTCATEGORYID AND OP.ORGID IN (");
            int index = 0; for (String ignored : organizations) { if (index++ > 0) sql.append(','); sql.append('?'); } sql.append(")) ORDER BY M.ID");
            try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql.toString())) {
                index = 1; query.setLong(index++, equipmentId.longValue()); for (String org : organizations) query.setString(index++, org);
                try (ResultSet rows = query.executeQuery()) {
                    while (rows.next()) {
                        EmcEquiptMaintBean value = new EmcEquiptMaintBean();
                        value.setId(rows.getLong("ID")); value.setEquiptCategoryId(rows.getLong("EQUIPTCATEGORYID"));
                        value.setMaintEvent(rows.getString("MAINTEVENT"));
                        value.setWarningDays(getLong(rows, "WARNINGDAYS")); value.setSameDay(rows.getString("SAME_DAY")); result.add(value);
                    }
                }
            }
            return result;
        } catch (SQLException failure) { throw new IllegalStateException("equipment maintenance selectable query failed", failure); }
    }

    /** Organization-scoped replacement for the original overdue-plan mapper query. */
    public Page<EmcEquiptPlanBean> readOverdue(RestJsonWrapperBean wrapper) {
        try {
            OwnedPrincipal principal = trustedReadPrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            QueryRequest request = QueryRequest.parse(wrapper == null ? null : wrapper.getRequestJson(),
                    wrapper == null ? null : wrapper.getParamMap());
            if (organizations == null || organizations.isEmpty())
                return new Pagination<EmcEquiptPlanBean>(0, request.getPageSize(), request.getPageNumber(), Collections.<EmcEquiptPlanBean>emptyList());
            List<String> filters = new ArrayList<String>();
            List<Object> values = new ArrayList<Object>();
            String scope = organizationScopeSql(organizations, values);
            filters.add("NOT EXISTS (SELECT 1 FROM T_EMC_EQUIPT_PLAN A WHERE A.EQUIPTID=P.EQUIPTID AND A.CREATEDTIME>P.CREATEDTIME)");
            filters.add("P.MAINTENDDATE<=SYSDATE+P.WARNINGDAYS");
            filters.add(scope);
            appendFilters(request, filters, values);
            String where = String.join(" AND ", filters);
            String countSql = "SELECT COUNT(1) FROM T_EMC_EQUIPT_PLAN P WHERE " + where;
            String pageSql = "SELECT * FROM (SELECT P.*,M.MAINTEVENT AS MAINT_EVENT_NAME,ROW_NUMBER() OVER (ORDER BY P.ID DESC) RN "
                    + "FROM T_EMC_EQUIPT_PLAN P LEFT JOIN T_EMC_EQUIPT_MAINT M ON M.ID=P.MAINTID WHERE " + where
                    + ") WHERE RN BETWEEN ? AND ? ORDER BY ID DESC";
            int from = (request.getPageNumber() - 1) * request.getPageSize() + 1;
            int to = request.getPageNumber() * request.getPageSize();
            try (Connection connection = dataSource.getConnection()) {
                int total;
                try (PreparedStatement count = connection.prepareStatement(countSql)) {
                    bind(count, values);
                    try (ResultSet rows = count.executeQuery()) { rows.next(); total = rows.getInt(1); }
                }
                List<EmcEquiptPlanBean> rows = new ArrayList<EmcEquiptPlanBean>();
                List<Object> pageValues = new ArrayList<Object>(values); pageValues.add(Integer.valueOf(from)); pageValues.add(Integer.valueOf(to));
                try (PreparedStatement query = connection.prepareStatement(pageSql)) {
                    bind(query, pageValues);
                    try (ResultSet result = query.executeQuery()) { while (result.next()) rows.add(convert(result)); }
                }
                return new Pagination<EmcEquiptPlanBean>(total, request.getPageSize(), request.getPageNumber(), rows);
            }
        } catch (IllegalArgumentException failure) { throw failure;
        } catch (SQLException failure) { throw new IllegalStateException("equipment overdue plan query failed", failure); }
    }

    public void deletePlans(RestJsonWrapperBean wrapper) {
        if (wrapper == null) throw new IllegalArgumentException("maintenance plans are required");
        List<EmcEquiptPlanBean> values = wrapper.parse(EmcEquiptPlanBean.class);
        if (values.isEmpty() || values.size() > 100) throw new IllegalArgumentException("maintenance plans are required");
        try {
            OwnedPrincipal principal = trustedManagePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                try (PreparedStatement find = connection.prepareStatement("SELECT EQUIPTID FROM T_EMC_EQUIPT_PLAN WHERE ID=?");
                        PreparedStatement delete = connection.prepareStatement("DELETE FROM T_EMC_EQUIPT_PLAN WHERE ID=?")) {
                    for (EmcEquiptPlanBean value : values) {
                        if (value == null || value.getId() == null) throw new IllegalArgumentException("maintenance plan id is required");
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
            for (EmcEquiptPlanBean value : values) audit.record(new AuditEvent(java.time.Instant.now(), "equipment-plan-delete", principal.getId(),
                    String.valueOf(value.getId()), AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("equipment plan delete failed", failure); }
    }

    /** Source-owned equivalent of the original selective maintenance-plan update mapper. */
    public void updatePlan(EmcEquiptPlanBean value) {
        if (value == null || value.getId() == null || value.getId().longValue() < 0L)
            throw new IllegalArgumentException("maintenance plan id is required");
        if (value.getMaintDate() == null && value.getMaintEndDate() == null && value.getCycle() == null
                && value.getVerifyOrg() == null && value.getGist() == null && value.getCertCategory() == null
                && value.getRemark() == null) throw new IllegalArgumentException("maintenance plan update is empty");
        try {
            OwnedPrincipal principal = trustedManagePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                long equipmentId; long maintenanceId;
                try (PreparedStatement find = connection.prepareStatement("SELECT EQUIPTID,MAINTID FROM T_EMC_EQUIPT_PLAN WHERE ID=?")) {
                    find.setLong(1, value.getId().longValue());
                    try (ResultSet rows = find.executeQuery()) {
                        if (!rows.next()) throw denied();
                        equipmentId = rows.getLong(1); maintenanceId = rows.getLong(2);
                    }
                }
                requireEquipmentScope(connection, equipmentId, organizations);
                requireMaintenanceScope(connection, equipmentId, maintenanceId, organizations);
                List<Object> values = new ArrayList<Object>();
                StringBuilder sql = new StringBuilder("UPDATE T_EMC_EQUIPT_PLAN SET ");
                if (value.getMaintDate() != null) { sql.append("MAINTDATE=?,"); values.add(Timestamp.valueOf(value.getMaintDate())); }
                if (value.getMaintEndDate() != null) { sql.append("MAINTENDDATE=?,"); values.add(Timestamp.valueOf(value.getMaintEndDate())); }
                if (value.getCycle() != null) { sql.append("CYCLE=?,"); values.add(value.getCycle()); }
                if (value.getVerifyOrg() != null) { sql.append("VERIFYORG=?,"); values.add(value.getVerifyOrg()); }
                if (value.getGist() != null) { sql.append("GIST=?,"); values.add(value.getGist()); }
                if (value.getCertCategory() != null) { sql.append("CERTCATEGORY=?,"); values.add(value.getCertCategory()); }
                if (value.getRemark() != null) { sql.append("REMARK=?,"); values.add(value.getRemark()); }
                sql.setLength(sql.length() - 1); sql.append(" WHERE ID=?"); values.add(value.getId());
                try (PreparedStatement update = connection.prepareStatement(sql.toString())) {
                    bind(update, values); if (update.executeUpdate() != 1) throw denied();
                }
                return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "equipment-plan-update", principal.getId(), String.valueOf(value.getId()), AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("equipment plan update failed", failure); }
    }

    private String scopeSql(long equipmentId, Set<String> organizations, List<Object> values) {
        StringBuilder sql = new StringBuilder("P.EQUIPTID=? AND EXISTS (SELECT 1 FROM T_EMC_EQUIPT E WHERE E.ID=P.EQUIPTID AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION OP WHERE OP.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND OP.TARGETID=E.EQUIPTCATEGORYID AND OP.ORGID IN (");
        values.add(Long.valueOf(equipmentId));
        int index = 0; for (String org : organizations) { if (index++ > 0) sql.append(','); sql.append('?'); values.add(org); }
        return sql.append(")))").toString();
    }

    private static String organizationScopeSql(Set<String> organizations, List<Object> values) {
        StringBuilder sql = new StringBuilder("EXISTS (SELECT 1 FROM T_EMC_EQUIPT E JOIN T_EMC_ORG_PERMISSION OP ON OP.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND OP.TARGETID=E.EQUIPTCATEGORYID WHERE E.ID=P.EQUIPTID AND OP.ORGID IN (");
        int index = 0;
        for (String organization : organizations) {
            if (index++ > 0) sql.append(',');
            sql.append('?');
            values.add(organization);
        }
        return sql.append("))").toString();
    }

    private void appendFilters(QueryRequest request, List<String> clauses, List<Object> values) {
        for (com.sunwayworld.framework.mybatis.mapper.FilterParamPattern group : request.getFilterPatterns()) {
            String pattern = group.getMatchPattern();
            for (Map.Entry<String,Object> entry : group.getMap().entrySet()) {
                String field = planColumn(entry.getKey()); Object value = entry.getValue();
                if ("CISC".equals(pattern) || "SC".equals(pattern)) { clauses.add("UPPER(P." + field + ") LIKE ?"); values.add("%" + String.valueOf(value).toUpperCase(Locale.ROOT) + "%"); }
                else if ("EQ".equals(pattern) || "SEQ".equals(pattern)) { clauses.add("P." + field + (value == null ? " IS NULL" : "=?")); if (value != null) values.add(value); }
                else if ("DG".equals(pattern) || "DGOE".equals(pattern) || "DL".equals(pattern) || "DLOE".equals(pattern)) { clauses.add("P." + field + ("DG".equals(pattern) ? ">" : "DGOE".equals(pattern) ? ">=" : "DL".equals(pattern) ? "<" : "<=") + " TO_DATE(?, 'YYYY-MM-DD')"); values.add(String.valueOf(value)); }
                else throw new IllegalArgumentException("unsupported maintenance plan filter pattern: " + pattern);
            }
        }
        for (String key : request.getMapperParameters().keySet()) {
            if (!"target_type".equals(key) && !"orgid_seq".equals(key)) throw new IllegalArgumentException("unsupported equipment plan query parameter: " + key);
        }
    }

    private static String planColumn(String key) {
        String normalized = key == null ? "" : key.toUpperCase(Locale.ROOT);
        if (normalized.endsWith("_CISC") || normalized.endsWith("_EQ") || normalized.endsWith("_SEQ") || normalized.endsWith("_DG") || normalized.endsWith("_DGOE") || normalized.endsWith("_DL") || normalized.endsWith("_DLOE")) {
            normalized = normalized.substring(0, normalized.lastIndexOf('_'));
        }
        if ("ID".equals(normalized)) return "ID"; if ("EQUIPTID".equals(normalized)) return "EQUIPTID"; if ("MAINTID".equals(normalized)) return "MAINTID";
        if ("MAINTDATE".equals(normalized)) return "MAINTDATE"; if ("CYCLE".equals(normalized)) return "CYCLE"; if ("WARNINGDAYS".equals(normalized)) return "WARNINGDAYS";
        if ("ORGID".equals(normalized)) return "ORGID"; if ("MAINTENDDATE".equals(normalized)) return "MAINTENDDATE"; if ("REMARK".equals(normalized)) return "REMARK";
        throw new IllegalArgumentException("unsupported equipment plan filter column: " + key);
    }

    private static void bind(PreparedStatement statement, List<Object> values) throws SQLException { int i=1; for (Object value: values) { if (value == null) statement.setNull(i++, java.sql.Types.VARCHAR); else if (value instanceof Timestamp) statement.setTimestamp(i++, (Timestamp) value); else if (value instanceof Number) statement.setLong(i++, ((Number)value).longValue()); else statement.setString(i++, String.valueOf(value)); } }
    private static void setLong(PreparedStatement statement, int index, Long value) throws SQLException { if (value == null) statement.setNull(index, java.sql.Types.NUMERIC); else statement.setLong(index, value.longValue()); }
    private static void setDate(PreparedStatement statement, int index, LocalDateTime value) throws SQLException { if (value == null) statement.setNull(index, java.sql.Types.TIMESTAMP); else statement.setTimestamp(index, Timestamp.valueOf(value)); }

    private OwnedPrincipal trustedReadPrincipal() throws SQLException { HttpServletRequest request=currentRequest(); HttpSession session=request.getSession(false); boundary.requirePermission(session, OwnedIdentityProvider.EQUIPMENT_READ); return boundary.currentPrincipal(session); }
    private OwnedPrincipal trustedManagePrincipal() throws SQLException { HttpServletRequest request=currentRequest(); HttpSession session=request.getSession(false); boundary.requirePermission(session, OwnedIdentityProvider.EQUIPMENT_MANAGE); return boundary.currentPrincipal(session); }
    private static HttpServletRequest currentRequest() { RequestAttributes attrs=RequestContextHolder.getRequestAttributes(); if (!(attrs instanceof ServletRequestAttributes)) throw denied(); HttpServletRequest request=((ServletRequestAttributes)attrs).getRequest(); if(request==null) throw denied(); return request; }
    private static AccessDeniedException denied() { return new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED"); }
    private static void requireEquipmentScope(Connection c,long id,Set<String> orgs)throws SQLException { if(orgs==null||orgs.isEmpty())throw denied(); StringBuilder s=new StringBuilder("SELECT 1 FROM T_EMC_EQUIPT E WHERE E.ID=? AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION P WHERE P.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND P.TARGETID=E.EQUIPTCATEGORYID AND P.ORGID IN ("); int i=0;for(String org:orgs){if(i++>0)s.append(',');s.append('?');}s.append("))");try(PreparedStatement q=c.prepareStatement(s.toString())){i=1;q.setLong(i++,id);for(String org:orgs)q.setString(i++,org);try(ResultSet r=q.executeQuery()){if(!r.next())throw denied();}} }
    private static void requireMaintenanceScope(Connection c,long equipmentId,long maintId,Set<String> orgs)throws SQLException { StringBuilder s=new StringBuilder("SELECT 1 FROM T_EMC_EQUIPT_MAINT M JOIN T_EMC_EQUIPT E ON E.EQUIPTCATEGORYID=M.EQUIPTCATEGORYID WHERE E.ID=? AND M.ID=? AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION P WHERE P.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND P.TARGETID=E.EQUIPTCATEGORYID AND P.ORGID IN ("); int i=0;for(String org:orgs){if(i++>0)s.append(',');s.append('?');}s.append("))"); try(PreparedStatement q=c.prepareStatement(s.toString())){i=1;q.setLong(i++,equipmentId);q.setLong(i++,maintId);for(String org:orgs)q.setString(i++,org);try(ResultSet r=q.executeQuery()){if(!r.next())throw denied();}} catch(SQLException absent){ if(absent.getErrorCode()==942 || (absent.getMessage()!=null && absent.getMessage().contains("invalid identifier"))) throw new IllegalStateException("maintenance scope schema unavailable", absent); throw absent; } }

    private static EmcEquiptPlanBean convert(ResultSet rows) throws SQLException { EmcEquiptPlanBean b=new EmcEquiptPlanBean(); b.setId(rows.getLong("ID")); b.setEquiptId(getLong(rows,"EQUIPTID")); b.setMaintId(getLong(rows,"MAINTID")); b.setMaintDate(date(rows,"MAINTDATE")); b.setCycle(rows.getString("CYCLE")); b.setWarningDays(getLong(rows,"WARNINGDAYS")); b.setOrgId(rows.getString("ORGID")); b.setMaintEndDate(date(rows,"MAINTENDDATE")); b.setRemark(rows.getString("REMARK")); b.setCreatedById(rows.getString("CREATEDBYID")); b.setCreatedByName(rows.getString("CREATEDBYNAME")); b.setCreatedTime(date(rows,"CREATEDTIME")); b.setCreatedByOrgId(rows.getString("CREATEDBYORGID")); b.setCreatedByOrgName(rows.getString("CREATEDBYORGNAME")); b.setExt$Item("codename", rows.getString("MAINT_EVENT_NAME")); return b; }
    private static Long getLong(ResultSet r,String c)throws SQLException{long v=r.getLong(c);return r.wasNull()?null:Long.valueOf(v);} private static LocalDateTime date(ResultSet r,String c)throws SQLException{Timestamp t=r.getTimestamp(c);return t==null?null:t.toLocalDateTime();}

    @Override public List<Map<String,Object>> selectEquiptMaintPlanOverdue(MapperParameter parameter) { throw new UnsupportedOperationException("Equipment overdue plan mapper contract requires the owned page adapter"); }
}
