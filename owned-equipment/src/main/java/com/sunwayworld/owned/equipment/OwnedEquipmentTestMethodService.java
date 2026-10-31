package com.sunwayworld.owned.equipment;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptTestMethodDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptTestMethodService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.AuditEvent;
import com.sunwayworld.owned.internal.web.AuditEventSink;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.OwnedWriteSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Isolated source-owned implementation for the inherited test-method copy route. */
public final class OwnedEquipmentTestMethodService implements EmcEquiptTestMethodService {
    private final OwnedEquipmentTestMethodDao dao;
    private final DataSource dataSource;
    private final JdbcPersonnelIdentityProvider provider;
    private final OwnedIdentityHttpBoundary boundary;
    private final AuditEventSink audit;
    private final Environment environment;

    public OwnedEquipmentTestMethodService(OwnedEquipmentTestMethodDao dao, DataSource dataSource,
            JdbcPersonnelIdentityProvider provider, OwnedIdentityHttpBoundary boundary,
            AuditEventSink audit, Environment environment) {
        this.dao = dao; this.dataSource = dataSource; this.provider = provider;
        this.boundary = boundary; this.audit = audit; this.environment = environment;
    }

    @Override public EmcEquiptTestMethodDao getDao() { return dao; }

    @Override
    public Long insert(Long equipmentId, RestJsonWrapperBean wrapper) {
        requireIsolated();
        if (equipmentId == null || equipmentId.longValue() < 0L || wrapper == null)
            throw new IllegalArgumentException("test-method equipment and body are required");
        List<EmcEquiptTestMethodBean> templates = wrapper.parse(EmcEquiptTestMethodBean.class);
        if (templates.isEmpty() || templates.size() > 100) throw new IllegalArgumentException("test-method body is required");
        try {
            OwnedPrincipal principal = trustedManagePrincipal();
            Long first = insertTemplates(principal, provider.permittedOrganizationIds(principal),
                    java.util.Collections.singletonList(equipmentId), templates);
            audit.record(new AuditEvent(java.time.Instant.now(), "equipment-test-method-insert", principal.getId(), String.valueOf(first), AuditEvent.Outcome.ALLOWED, "isolated-write", null));
            return first;
        } catch (SQLException failure) { throw new IllegalStateException("equipment test-method insert failed", failure); }
    }

    @Override
    public void testMethodCopyTo(RestJsonWrapperBean wrapper) {
        requireIsolated();
        if (wrapper == null) throw new IllegalArgumentException("test-method copy body is required");
        List<EmcEquiptBean> targets = wrapper.parse(EmcEquiptBean.class);
        List<EmcEquiptTestMethodBean> templates = wrapper.parse(EmcEquiptTestMethodBean.class);
        if (targets.isEmpty() || targets.size() > 100 || templates.isEmpty() || templates.size() > 100)
            throw new IllegalArgumentException("test-method copy body is required");
        try {
            OwnedPrincipal principal = trustedManagePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            List<Long> targetIds = new ArrayList<Long>();
            for (EmcEquiptBean target : targets) {
                if (target == null || target.getId() == null || target.getId().longValue() < 0L)
                    throw new IllegalArgumentException("test-method target equipment is invalid");
                targetIds.add(target.getId());
            }
            int inserted = insertTemplates(principal, organizations, targetIds, templates).intValue();
            audit.record(new AuditEvent(java.time.Instant.now(), "equipment-test-method-copy", principal.getId(), String.valueOf(inserted), AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("equipment test-method copy failed", failure); }
    }

    private Long insertTemplates(OwnedPrincipal principal, Set<String> organizations, List<Long> targetIds,
            List<EmcEquiptTestMethodBean> templates) throws SQLException {
        return OwnedWriteSupport.transaction(dataSource, connection -> {
            for (Long targetId : targetIds) requireEquipmentScope(connection, targetId.longValue(), organizations);
            long first = -1L;
            try (PreparedStatement insert = connection.prepareStatement(
                    "INSERT INTO T_EMC_EQUIPT_TEST_METHOD (ID,EQUIPTID,TESTCATEGORYID,TESTMETHODID,TESTID,METHODID,CURVENO,CURVEID,ORGID,ANALYSISCATEGORY,DEFAULTFLAG,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?,?,?,?, ?,?,SYSTIMESTAMP,?,?)")) {
                for (Long targetId : targetIds) for (EmcEquiptTestMethodBean template : templates) {
                    validateTemplate(template); long id = OwnedWriteSupport.nextIdentity(connection); if (first < 0L) first = id;
                    int index = 1; insert.setLong(index++, id); insert.setLong(index++, targetId.longValue());
                    setLong(insert, index++, template.getTestCategoryId()); setLong(insert, index++, template.getTestMethodId());
                    setLong(insert, index++, template.getTestId()); setLong(insert, index++, template.getMethodId());
                    insert.setString(index++, template.getCurveNo()); insert.setString(index++, template.getCurveId());
                    insert.setString(index++, template.getOrgId() == null ? principal.getOrgId() : template.getOrgId());
                    insert.setString(index++, template.getAnalysisCategory()); insert.setString(index++, template.getDefaultFlag() == null ? "0" : template.getDefaultFlag());
                    insert.setString(index++, principal.getId()); insert.setString(index++, principal.getId());
                    insert.setString(index++, principal.getOrgId()); insert.setString(index, principal.getOrgName()); insert.addBatch();
                }
                insert.executeBatch();
            }
            return Long.valueOf(first);
        });
    }

    private OwnedPrincipal trustedManagePrincipal() throws SQLException {
        HttpSession session = current().getSession(false);
        boundary.requirePermission(session, OwnedIdentityProvider.EQUIPMENT_MANAGE);
        return boundary.currentPrincipal(session);
    }

    private void requireIsolated() {
        if (environment == null || !"isolated".equals(environment.getProperty("owned.equipment.write.mode"))) throw denied();
        OwnedWriteSupport.requireLoopback(current());
    }

    private static javax.servlet.http.HttpServletRequest current() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes)) throw denied();
        return ((ServletRequestAttributes) attributes).getRequest();
    }

    private static void requireEquipmentScope(Connection connection, long equipmentId, Set<String> organizations) throws SQLException {
        if (organizations == null || organizations.isEmpty()) throw denied();
        StringBuilder sql = new StringBuilder("SELECT 1 FROM T_EMC_EQUIPT E WHERE E.ID=? AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION P WHERE P.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND P.TARGETID=E.EQUIPTCATEGORYID AND P.ORGID IN (");
        for (int i = 0; i < organizations.size(); i++) { if (i > 0) sql.append(','); sql.append('?'); }
        sql.append("))");
        try (PreparedStatement query = connection.prepareStatement(sql.toString())) {
            int index = 1; query.setLong(index++, equipmentId); for (String organization : organizations) query.setString(index++, organization);
            try (ResultSet rows = query.executeQuery()) { if (!rows.next()) throw denied(); }
        }
    }

    private static void validateTemplate(EmcEquiptTestMethodBean value) {
        if (value == null || value.getTestMethodId() == null || value.getTestMethodId().longValue() < 0L)
            throw new IllegalArgumentException("test method is required");
        if (value.getDefaultFlag() != null && !"0".equals(value.getDefaultFlag()) && !"1".equals(value.getDefaultFlag()))
            throw new IllegalArgumentException("test method default flag is invalid");
    }

    private static void setLong(PreparedStatement statement, int index, Long value) throws SQLException {
        if (value == null) statement.setNull(index, java.sql.Types.NUMERIC); else statement.setLong(index, value.longValue());
    }

    private static AccessDeniedException denied() { return new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED"); }
}
