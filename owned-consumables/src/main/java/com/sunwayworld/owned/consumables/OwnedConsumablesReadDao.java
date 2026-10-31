package com.sunwayworld.owned.consumables;

import com.sunwayworld.emc.module.item.consume.consume.bean.EmcConsumeBean;
import com.sunwayworld.emc.module.item.consume.consume.dao.EmcConsumeDao;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.owned.internal.data.OwnedReadDao;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.AuditEvent;
import com.sunwayworld.owned.internal.web.AuditEventSink;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.OwnedWriteSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Original consumable DAO with read parity and an isolated material-create seam. */
public final class OwnedConsumablesReadDao extends MybatisDaoSupport<EmcConsumeBean,String>
        implements EmcConsumeDao, OwnedReadDao<EmcConsumeBean,String> {
    private final OwnedConsumablesReads reads;
    private final DataSource dataSource;
    private final JdbcPersonnelIdentityProvider provider;
    private final OwnedIdentityHttpBoundary boundary;
    private final AuditEventSink audit;
    public OwnedConsumablesReadDao(OwnedConsumablesReads reads, DataSource dataSource,
            JdbcPersonnelIdentityProvider provider, OwnedIdentityHttpBoundary boundary, AuditEventSink audit) {
        this.reads = reads; this.dataSource = dataSource; this.provider = provider; this.boundary = boundary; this.audit = audit;
    }
    @Override public Page<EmcConsumeBean> readPage(RestJsonWrapperBean wrapper) { return reads.list(wrapper, EmcConsumeBean.class); }
    @Override public EmcConsumeBean readDetail(String id) { return reads.detail(id, EmcConsumeBean.class); }
    @Override public void updateStockQty(String id) {
        if (!validId(id)) throw new IllegalArgumentException("material id is invalid");
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            if (organizations == null || organizations.isEmpty())
                throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
            Connection connection = org.springframework.jdbc.datasource.DataSourceUtils.getConnection(dataSource);
            try {
                StringBuilder sql = new StringBuilder(
                        "UPDATE T_EMC_CONSUME SET STOCKQTY=(SELECT NVL(SUM(STOCKQTY),0) "
                        + "FROM T_EMC_CSM_STOCK WHERE MTLID=? AND STATUS='0') "
                        + "WHERE ID=? AND CREATEDBYORGID IN (");
                for (int i = 0; i < organizations.size(); i++) {
                    if (i > 0) sql.append(',');
                    sql.append('?');
                }
                sql.append(')');
                try (PreparedStatement update = connection.prepareStatement(sql.toString())) {
                    int index = 1;
                    update.setString(index++, id);
                    update.setString(index++, id);
                    for (String organization : organizations) update.setString(index++, organization);
                    if (update.executeUpdate() != 1)
                        throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
                }
            } finally {
                org.springframework.jdbc.datasource.DataSourceUtils.releaseConnection(connection, dataSource);
            }
        } catch (SQLException failure) {
            throw new IllegalStateException("consumable stock aggregate update failed", failure);
        }
    }

    /** Isolated Oracle write seam for the original material creation route. */
    public String insertMaterial(RestJsonWrapperBean wrapper) {
        if (wrapper == null) throw new IllegalArgumentException("material body is required");
        EmcConsumeBean material = wrapper.parseUnique(EmcConsumeBean.class);
        String requestedId = material.getId();
        if (requestedId != null && !requestedId.trim().isEmpty() && !requestedId.matches("[A-Za-z0-9_-]{1,64}"))
            throw new IllegalArgumentException("material id is invalid");
        if (text(material.getMtlName()).length() == 0 || text(material.getMtlName()).length() > 100)
            throw new IllegalArgumentException("material name is invalid");
        if (text(material.getSpecification()).length() > 100 || text(material.getProductNo()).length() > 64)
            throw new IllegalArgumentException("material field is invalid");
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            if (organizations == null || !organizations.contains(principal.getOrgId()))
                throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
            String id = OwnedWriteSupport.transaction(dataSource, connection -> {
                String allocatedId = requestedId == null || requestedId.trim().isEmpty()
                        ? String.valueOf(OwnedWriteSupport.nextIdentity(connection)) : requestedId;
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO T_EMC_CONSUME (ID,MTLNAME,SPECIFICATION,STOCKQTY,MTLCATEGORYID,HARMFULNESS,PRODUCTNO,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?,?,SYSTIMESTAMP,?,?)")) {
                    insert.setString(1, allocatedId); insert.setString(2, material.getMtlName()); insert.setString(3, material.getSpecification());
                    insert.setDouble(4, 0d); insert.setString(5, material.getMtlCategoryId()); insert.setString(6, material.getHarmfulness());
                    insert.setString(7, material.getProductNo()); insert.setString(8, principal.getId()); insert.setString(9, principal.getId());
                    insert.setString(10, principal.getOrgId()); insert.setString(11, principal.getOrgName()); insert.executeUpdate();
                }
                return allocatedId;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "consumable-material-insert", principal.getId(), id,
                    AuditEvent.Outcome.ALLOWED, "isolated-write", null));
            return id;
        } catch (SQLException failure) { throw new IllegalStateException("consumable material write failed", failure); }
    }

    /** Isolated Oracle write seam for the original material update route. */
    public void updateMaterial(String materialId, RestJsonWrapperBean wrapper) {
        if (!validId(materialId) || wrapper == null) throw new IllegalArgumentException("material update request is invalid");
        EmcConsumeBean material = wrapper.parseUnique(EmcConsumeBean.class);
        if (material.getId() != null && !materialId.equals(material.getId())) throw new IllegalArgumentException("material id does not match route");
        validateMaterialFields(material);
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                requireMaterialScope(connection, materialId, organizations);
                try (PreparedStatement update = connection.prepareStatement(
                        "UPDATE T_EMC_CONSUME SET MTLNAME=?,SPECIFICATION=?,MTLCATEGORYID=?,HARMFULNESS=?,PRODUCTNO=? WHERE ID=?")) {
                    update.setString(1, material.getMtlName()); update.setString(2, material.getSpecification());
                    update.setString(3, material.getMtlCategoryId()); update.setString(4, material.getHarmfulness());
                    update.setString(5, material.getProductNo()); update.setString(6, materialId);
                    if (update.executeUpdate() != 1) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
                }
                return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "consumable-material-update", principal.getId(), materialId,
                    AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("consumable material update failed", failure); }
    }

    /** Isolated Oracle write seam for the original material delete route. */
    public void deleteMaterials(RestJsonWrapperBean wrapper) {
        if (wrapper == null) throw new IllegalArgumentException("material delete request is required");
        List<String> ids = wrapper.parseId(String.class);
        if (ids.isEmpty() || ids.size() > 100) throw new IllegalArgumentException("material ids are invalid");
        for (String id : ids) if (!validId(id)) throw new IllegalArgumentException("material id is invalid");
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                for (String id : ids) requireMaterialScope(connection, id, organizations);
                for (String id : ids) {
                    try (PreparedStatement logs = connection.prepareStatement("DELETE FROM T_EMC_CSM_STOCK_LOG WHERE STOCKID IN (SELECT ID FROM T_EMC_CSM_STOCK WHERE MTLID=?)");
                         PreparedStatement stocks = connection.prepareStatement("DELETE FROM T_EMC_CSM_STOCK WHERE MTLID=?");
                         PreparedStatement material = connection.prepareStatement("DELETE FROM T_EMC_CONSUME WHERE ID=?")) {
                        logs.setString(1, id); logs.executeUpdate(); stocks.setString(1, id); stocks.executeUpdate();
                        material.setString(1, id); if (material.executeUpdate() != 1) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
                    }
                }
                return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "consumable-material-delete", principal.getId(), String.join(",", ids),
                    AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("consumable material delete failed", failure); }
    }

    private static boolean validId(String value) { return value != null && value.matches("[A-Za-z0-9_-]{1,64}"); }
    private static void validateMaterialFields(EmcConsumeBean material) {
        if (material == null || text(material.getMtlName()).length() == 0 || text(material.getMtlName()).length() > 100)
            throw new IllegalArgumentException("material name is invalid");
        if (text(material.getSpecification()).length() > 100 || text(material.getProductNo()).length() > 64
                || text(material.getMtlCategoryId()).length() > 64 || text(material.getHarmfulness()).length() > 36)
            throw new IllegalArgumentException("material field is invalid");
    }
    private static void requireMaterialScope(Connection connection, String materialId, Set<String> organizations) throws SQLException {
        if (organizations == null || organizations.isEmpty()) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
        StringBuilder sql = new StringBuilder("SELECT 1 FROM T_EMC_CONSUME WHERE ID=? AND CREATEDBYORGID IN (");
        for (int i=0; i<organizations.size(); i++) { if (i>0) sql.append(','); sql.append('?'); } sql.append(')');
        try (PreparedStatement query=connection.prepareStatement(sql.toString())) {
            int index=1; query.setString(index++,materialId); for (String org:organizations) query.setString(index++,org);
            try (ResultSet rows=query.executeQuery()) { if (!rows.next()) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED"); }
        }
    }

    private OwnedPrincipal trustedWritePrincipal() throws SQLException {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes)) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
        HttpSession session = request.getSession(false);
        boundary.requirePermission(session, OwnedIdentityProvider.CONSUMABLE_MANAGE);
        return boundary.currentPrincipal(session);
    }

    private static String text(String value) { return value == null ? "" : value.trim(); }
}
