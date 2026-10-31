package com.sunwayworld.owned.equipment;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptMaintainRepairBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.PfEquiptMaintainRepairDTO;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptMaintainRepairDao;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.AuditEvent;
import com.sunwayworld.owned.internal.web.AuditEventSink;
import com.sunwayworld.owned.internal.web.OwnedWriteSupport;
import com.sunwayworld.owned.internal.web.QueryRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.core.env.Environment;

/** Organization-scoped Oracle read adapter for equipment maintenance/repair history. */
public final class OwnedEquipmentMaintainRepairDao extends MybatisDaoSupport<EmcEquiptMaintainRepairBean, Long>
        implements EmcEquiptMaintainRepairDao {
    private final DataSource dataSource;
    private final JdbcPersonnelIdentityProvider provider;
    private final OwnedIdentityHttpBoundary boundary;
    private final AuditEventSink audit;
    private final Environment environment;

    public OwnedEquipmentMaintainRepairDao(DataSource dataSource, JdbcPersonnelIdentityProvider provider,
            OwnedIdentityHttpBoundary boundary, AuditEventSink audit, Environment environment) {
        this.dataSource = dataSource; this.provider = provider; this.boundary = boundary; this.audit = audit; this.environment = environment;
    }

    public Long insert(RestJsonWrapperBean wrapper) {
        if (!"isolated".equals(environment.getProperty("owned.equipment.write.mode"))) throw denied();
        if (wrapper == null) throw new IllegalArgumentException("maintenance repair body is required");
        EmcEquiptMaintainRepairBean value = wrapper.parseUnique(EmcEquiptMaintainRepairBean.class);
        if (value == null || value.getEquiptId() == null || value.getEquiptId() < 0L) throw new IllegalArgumentException("equipment id is required");
        try {
            OwnedWriteSupport.requireLoopback(currentRequest()); OwnedPrincipal principal = trustedManagePrincipal(); Set<String> organizations = provider.permittedOrganizationIds(principal);
            Long id = OwnedWriteSupport.transaction(dataSource, connection -> {
                requireScope(connection, value.getEquiptId(), organizations);
                long generated = OwnedWriteSupport.nextIdentity(connection);
                try (PreparedStatement insert = connection.prepareStatement("INSERT INTO T_EMC_EQUIPT_MAINTAIN_REPAIR (ID,EQUIPTID,MAINTAINREPAIRDATE,MAINTAINREPAIRCONCENT,MAINTAINREPAIRSIGN,EQUIPTFAILURE,MAINTAINREPAIRUNIT,PLANCATEGORY,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?,?,?,SYSTIMESTAMP,?,?)")) {
                    int i=1; insert.setLong(i++,generated); insert.setLong(i++,value.getEquiptId()); if(value.getMaintainRepairDate()==null) insert.setNull(i++,java.sql.Types.DATE); else insert.setDate(i++,java.sql.Date.valueOf(value.getMaintainRepairDate())); insert.setString(i++,value.getMaintainRepairConcent()); insert.setString(i++,value.getMaintainRepairSign()); insert.setString(i++,value.getEquiptFailure()); insert.setString(i++,value.getMaintainrepairunit()); insert.setString(i++,value.getPlanCategory()); insert.setString(i++,principal.getId()); insert.setString(i++,principal.getId()); insert.setString(i++,principal.getOrgId()); insert.setString(i,principal.getOrgName()); insert.executeUpdate();
                } return generated;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "equipment-maintain-repair-insert", principal.getId(), String.valueOf(id), AuditEvent.Outcome.ALLOWED, "isolated-write", null)); return id;
        } catch (SQLException failure) { throw new IllegalStateException("equipment maintenance repair write failed", failure); }
    }

    public void delete(RestJsonWrapperBean wrapper) {
        if (!"isolated".equals(environment.getProperty("owned.equipment.write.mode"))) throw denied();
        if (wrapper == null) throw new IllegalArgumentException("maintenance repair ids are required");
        List<Long> ids = wrapper.parseId(Long.class); if (ids.isEmpty() || ids.size()>100) throw new IllegalArgumentException("maintenance repair ids are required");
        try {
            OwnedWriteSupport.requireLoopback(currentRequest()); OwnedPrincipal principal=trustedManagePrincipal(); Set<String> organizations=provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> { try(PreparedStatement find=connection.prepareStatement("SELECT EQUIPTID FROM T_EMC_EQUIPT_MAINTAIN_REPAIR WHERE ID=?"); PreparedStatement remove=connection.prepareStatement("DELETE FROM T_EMC_EQUIPT_MAINTAIN_REPAIR WHERE ID=?")){ for(Long id:ids){ if(id==null)throw denied(); find.setLong(1,id); try(ResultSet rows=find.executeQuery()){if(!rows.next())throw denied(); requireScope(connection,rows.getLong(1),organizations);} remove.setLong(1,id); if(remove.executeUpdate()!=1)throw denied(); } } return null; });
            audit.record(new AuditEvent(java.time.Instant.now(), "equipment-maintain-repair-delete", principal.getId(), ids.toString(), AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch(SQLException failure){throw new IllegalStateException("equipment maintenance repair delete failed",failure);}
    }

    public Page<EmcEquiptMaintainRepairBean> read(Long equipmentId, RestJsonWrapperBean wrapper) {
        if (equipmentId == null || equipmentId < 0L) throw new IllegalArgumentException("equipment id is invalid");
        try {
            OwnedPrincipal principal = trustedReadPrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            QueryRequest request = QueryRequest.parse(wrapper == null ? null : wrapper.getRequestJson(),
                    wrapper == null ? null : wrapper.getParamMap());
            if (organizations == null || organizations.isEmpty()) return empty(request);
            List<Object> values = new ArrayList<Object>();
            String scope = scopeSql(equipmentId.longValue(), organizations, values);
            String countSql = "SELECT COUNT(1) FROM T_EMC_EQUIPT_MAINTAIN_REPAIR R WHERE " + scope;
            String pageSql = "SELECT * FROM (SELECT R.*,ROW_NUMBER() OVER (ORDER BY R.ID DESC) RN FROM T_EMC_EQUIPT_MAINTAIN_REPAIR R WHERE " + scope + ") WHERE RN BETWEEN ? AND ? ORDER BY ID DESC";
            int from = (request.getPageNumber() - 1) * request.getPageSize() + 1;
            int to = request.getPageNumber() * request.getPageSize();
            try (Connection connection = dataSource.getConnection()) {
                int total;
                try (PreparedStatement count = connection.prepareStatement(countSql)) {
                    bind(count, values);
                    try (ResultSet rows = count.executeQuery()) { rows.next(); total = rows.getInt(1); }
                }
                List<EmcEquiptMaintainRepairBean> rows = new ArrayList<EmcEquiptMaintainRepairBean>();
                List<Object> pageValues = new ArrayList<Object>(values); pageValues.add(from); pageValues.add(to);
                try (PreparedStatement query = connection.prepareStatement(pageSql)) {
                    bind(query, pageValues);
                    try (ResultSet result = query.executeQuery()) { while (result.next()) rows.add(convert(result)); }
                }
                return new Pagination<EmcEquiptMaintainRepairBean>(total, request.getPageSize(), request.getPageNumber(), rows);
            }
        } catch (IllegalArgumentException failure) { throw failure;
        } catch (SQLException failure) { throw new IllegalStateException("equipment maintenance repair query failed", failure); }
    }

    /** Organization-scoped summary used by the original maintenance-search page. */
    public Page<PfEquiptMaintainRepairDTO> readReport(RestJsonWrapperBean wrapper) {
        try {
            OwnedPrincipal principal = trustedReadPrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            QueryRequest request = QueryRequest.parse(wrapper == null ? null : wrapper.getRequestJson(),
                    wrapper == null ? null : wrapper.getParamMap());
            if (organizations == null || organizations.isEmpty())
                return new Pagination<PfEquiptMaintainRepairDTO>(0, request.getPageSize(), request.getPageNumber(), Collections.<PfEquiptMaintainRepairDTO>emptyList());
            if (!request.getFilterPatterns().isEmpty() || !request.getMapperParameters().isEmpty())
                throw new IllegalArgumentException("unsupported maintenance summary filter");
            List<Object> values = new ArrayList<Object>();
            String scope = organizationScopeSql(organizations, values);
            String fromSql = "FROM T_EMC_EQUIPT_MAINTAIN_REPAIR R JOIN T_EMC_EQUIPT E ON E.ID=R.EQUIPTID WHERE " + scope;
            String countSql = "SELECT COUNT(1) " + fromSql;
            String pageSql = "SELECT * FROM (SELECT R.*,E.EQUIPTID AS EQUIPT_NO,ROW_NUMBER() OVER (ORDER BY R.ID DESC) RN "
                    + fromSql + ") WHERE RN BETWEEN ? AND ? ORDER BY ID DESC";
            int from = (request.getPageNumber() - 1) * request.getPageSize() + 1;
            int to = request.getPageNumber() * request.getPageSize();
            try (Connection connection = dataSource.getConnection()) {
                int total;
                try (PreparedStatement count = connection.prepareStatement(countSql)) {
                    bind(count, values);
                    try (ResultSet rows = count.executeQuery()) { rows.next(); total = rows.getInt(1); }
                }
                List<PfEquiptMaintainRepairDTO> rows = new ArrayList<PfEquiptMaintainRepairDTO>();
                List<Object> pageValues = new ArrayList<Object>(values); pageValues.add(Integer.valueOf(from)); pageValues.add(Integer.valueOf(to));
                try (PreparedStatement query = connection.prepareStatement(pageSql)) {
                    bind(query, pageValues);
                    try (ResultSet result = query.executeQuery()) {
                        while (result.next()) {
                            PfEquiptMaintainRepairDTO value = new PfEquiptMaintainRepairDTO();
                            value.setEquiptNo(result.getString("EQUIPT_NO"));
                            java.sql.Date repairDate = result.getDate("MAINTAINREPAIRDATE");
                            if (repairDate != null) value.setMaintainRepairDate(repairDate.toLocalDate());
                            value.setMaintainRepairConcent(result.getString("MAINTAINREPAIRCONCENT"));
                            value.setMaintainRepairSign(result.getString("MAINTAINREPAIRSIGN"));
                            value.setMaintainrepairunit(result.getString("MAINTAINREPAIRUNIT"));
                            value.setEquiptFailure(result.getString("EQUIPTFAILURE"));
                            rows.add(value);
                        }
                    }
                }
                return new Pagination<PfEquiptMaintainRepairDTO>(total, request.getPageSize(), request.getPageNumber(), rows);
            }
        } catch (IllegalArgumentException failure) { throw failure;
        } catch (SQLException failure) { throw new IllegalStateException("equipment maintenance summary query failed", failure); }
    }

    private static String scopeSql(long equipmentId, Set<String> organizations, List<Object> values) {
        StringBuilder sql = new StringBuilder("R.EQUIPTID=? AND EXISTS (SELECT 1 FROM T_EMC_EQUIPT E WHERE E.ID=R.EQUIPTID AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION OP WHERE OP.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND OP.TARGETID=E.EQUIPTCATEGORYID AND OP.ORGID IN (");
        values.add(equipmentId); int i = 0;
        for (String org : organizations) { if (i++ > 0) sql.append(','); sql.append('?'); values.add(org); }
        return sql.append(")))").toString();
    }
    private static String organizationScopeSql(Set<String> organizations, List<Object> values) {
        StringBuilder sql = new StringBuilder("EXISTS (SELECT 1 FROM T_EMC_EQUIPT E JOIN T_EMC_ORG_PERMISSION OP ON OP.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND OP.TARGETID=E.EQUIPTCATEGORYID WHERE E.ID=R.EQUIPTID AND OP.ORGID IN (");
        int index = 0;
        for (String organization : organizations) {
            if (index++ > 0) sql.append(',');
            sql.append('?');
            values.add(organization);
        }
        return sql.append("))").toString();
    }
    private static void bind(PreparedStatement statement, List<Object> values) throws SQLException {
        int i = 1; for (Object value : values) { if (value instanceof Number) statement.setLong(i++, ((Number)value).longValue()); else statement.setString(i++, String.valueOf(value)); }
    }
    private static Pagination<EmcEquiptMaintainRepairBean> empty(QueryRequest request) { return new Pagination<EmcEquiptMaintainRepairBean>(0, request.getPageSize(), request.getPageNumber(), Collections.<EmcEquiptMaintainRepairBean>emptyList()); }
    private OwnedPrincipal trustedReadPrincipal() throws SQLException { HttpSession session = currentRequest().getSession(false); boundary.requirePermission(session, OwnedIdentityProvider.EQUIPMENT_READ); return boundary.currentPrincipal(session); }
    private static javax.servlet.http.HttpServletRequest currentRequest() { RequestAttributes attrs = RequestContextHolder.getRequestAttributes(); if (!(attrs instanceof ServletRequestAttributes)) throw denied(); return ((ServletRequestAttributes)attrs).getRequest(); }
    private static AccessDeniedException denied() { return new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED"); }
    private static void requireScope(Connection connection,long equipmentId,Set<String> organizations)throws SQLException{if(organizations==null||organizations.isEmpty())throw denied();StringBuilder sql=new StringBuilder("SELECT 1 FROM T_EMC_EQUIPT E WHERE E.ID=? AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION P WHERE P.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND P.TARGETID=E.EQUIPTCATEGORYID AND P.ORGID IN (");int i=0;for(String ignored:organizations){if(i++>0)sql.append(',');sql.append('?');}sql.append("))");try(PreparedStatement q=connection.prepareStatement(sql.toString())){i=1;q.setLong(i++,equipmentId);for(String org:organizations)q.setString(i++,org);try(ResultSet r=q.executeQuery()){if(!r.next())throw denied();}}}
    private OwnedPrincipal trustedManagePrincipal() throws SQLException { HttpSession session=currentRequest().getSession(false); boundary.requirePermission(session, OwnedIdentityProvider.EQUIPMENT_MANAGE); return boundary.currentPrincipal(session); }
    private static EmcEquiptMaintainRepairBean convert(ResultSet rows) throws SQLException {
        EmcEquiptMaintainRepairBean bean = new EmcEquiptMaintainRepairBean(); bean.setId(rows.getLong("ID")); bean.setEquiptId(rows.getLong("EQUIPTID"));
        java.sql.Date date = rows.getDate("MAINTAINREPAIRDATE"); if (date != null) bean.setMaintainRepairDate(date.toLocalDate());
        bean.setMaintainRepairConcent(rows.getString("MAINTAINREPAIRCONCENT")); bean.setMaintainRepairSign(rows.getString("MAINTAINREPAIRSIGN")); bean.setEquiptFailure(rows.getString("EQUIPTFAILURE")); bean.setMaintainrepairunit(rows.getString("MAINTAINREPAIRUNIT")); bean.setPlanCategory(rows.getString("PLANCATEGORY")); bean.setCreatedById(rows.getString("CREATEDBYID")); bean.setCreatedByName(rows.getString("CREATEDBYNAME")); Timestamp created = rows.getTimestamp("CREATEDTIME"); if (created != null) bean.setCreatedTime(created.toLocalDateTime()); bean.setCreatedByOrgId(rows.getString("CREATEDBYORGID")); bean.setCreatedByOrgName(rows.getString("CREATEDBYORGNAME")); return bean;
    }
    @Override public List<Map<String,Object>> selectByCondition(MapperParameter parameter) { throw new UnsupportedOperationException("equipment maintenance repair mapper query is not implemented"); }
}
