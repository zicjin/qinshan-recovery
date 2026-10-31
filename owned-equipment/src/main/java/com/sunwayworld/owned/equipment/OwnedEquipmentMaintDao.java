package com.sunwayworld.owned.equipment;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.dao.EmcEquiptMaintDao;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.OwnedWriteSupport;
import com.sunwayworld.owned.internal.web.QueryRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Organization-scoped Oracle adapter for the original maintenance-event DAO. */
public final class OwnedEquipmentMaintDao extends MybatisDaoSupport<EmcEquiptMaintBean, Long>
        implements EmcEquiptMaintDao {
    private final DataSource dataSource;
    private final JdbcPersonnelIdentityProvider provider;
    private final OwnedIdentityHttpBoundary boundary;

    public OwnedEquipmentMaintDao(DataSource dataSource, JdbcPersonnelIdentityProvider provider,
            OwnedIdentityHttpBoundary boundary) {
        this.dataSource = dataSource;
        this.provider = provider;
        this.boundary = boundary;
    }

    @Override public Class<EmcEquiptMaintBean> getType() { return EmcEquiptMaintBean.class; }

    @Override
    public <V> List<EmcEquiptMaintBean> selectListByOneColumnValue(V value, String column, Order... orders) {
        if (!"EQUIPTCATEGORYID".equalsIgnoreCase(column)) throw new IllegalArgumentException("unsupported maintenance filter");
        if (value == null) return Collections.emptyList();
        try { return queryByCategory(Long.valueOf(String.valueOf(value)), permittedPrincipal()); }
        catch (NumberFormatException failure) { throw new IllegalArgumentException("maintenance category id is invalid"); }
        catch (SQLException failure) { throw new IllegalStateException("equipment maintenance query failed", failure); }
    }

    @Override
    public <V> List<EmcEquiptMaintBean> selectListByOneColumnValues(List<V> values, String column, Order... orders) {
        if (!"ID".equalsIgnoreCase(column) && !"EQUIPTCATEGORYID".equalsIgnoreCase(column))
            throw new IllegalArgumentException("unsupported maintenance filter");
        if (values == null || values.isEmpty() || values.size() > 100) return Collections.emptyList();
        List<Long> ids = new ArrayList<Long>();
        try { for (V value : values) ids.add(Long.valueOf(String.valueOf(value))); }
        catch (NumberFormatException failure) { throw new IllegalArgumentException("maintenance ids are invalid"); }
        try {
            return queryByColumn(ids, column, permittedPrincipal());
        } catch (SQLException failure) { throw new IllegalStateException("equipment maintenance query failed", failure); }
    }

    @Override
    public List<EmcEquiptMaintBean> selectListByIds(List<Long> ids, Order... orders) {
        if (ids == null || ids.isEmpty() || ids.size() > 100) return Collections.emptyList();
        try { return queryByColumn(ids, "ID", permittedPrincipal()); }
        catch (SQLException failure) { throw new IllegalStateException("equipment maintenance query failed", failure); }
    }

    @Override
    public EmcEquiptMaintBean selectByIdIfPresent(Long id) {
        if (id == null || id.longValue() < 0L) return null;
        try {
            List<EmcEquiptMaintBean> rows = queryByColumn(Collections.singletonList(id), "ID", permittedPrincipal());
            return rows.isEmpty() ? null : rows.get(0);
        } catch (SQLException failure) { throw new IllegalStateException("equipment maintenance detail failed", failure); }
    }

    public EmcEquiptMaintBean selectFirst(Long categoryId, String maintEvent) {
        if (categoryId == null || categoryId.longValue() < 0L || maintEvent == null || maintEvent.trim().isEmpty()) return null;
        try {
            OwnedPrincipal principal = permittedPrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            if (organizations == null || organizations.isEmpty()) return null;
            StringBuilder sql = new StringBuilder("SELECT M.ID,M.EQUIPTCATEGORYID,M.MAINTEVENT,M.EVENTDESC,M.WARNINGDAYS,M.SAME_DAY FROM T_EMC_EQUIPT_MAINT M WHERE M.EQUIPTCATEGORYID=? AND M.MAINTEVENT=? AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION P WHERE P.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND P.TARGETID=M.EQUIPTCATEGORYID AND P.ORGID IN (");
            for (int i = 0; i < organizations.size(); i++) { if (i > 0) sql.append(','); sql.append('?'); }
            sql.append(") ) ORDER BY M.ID");
            try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql.toString())) {
                int index = 1; query.setLong(index++, categoryId.longValue()); query.setString(index++, maintEvent);
                for (String organization : organizations) query.setString(index++, organization);
                try (ResultSet rows = query.executeQuery()) { return rows.next() ? read(rows) : null; }
            }
        } catch (SQLException failure) { throw new IllegalStateException("equipment maintenance first query failed", failure); }
    }

    public Page<EmcEquiptMaintBean> readPage(Long categoryId, RestJsonWrapperBean wrapper) {
        if (categoryId == null || categoryId.longValue() < 0L) throw new IllegalArgumentException("maintenance category id is invalid");
        try {
            QueryRequest request = QueryRequest.parse(wrapper == null ? null : wrapper.getRequestJson(),
                    wrapper == null ? null : wrapper.getParamMap());
            List<EmcEquiptMaintBean> all = queryByCategory(categoryId.longValue(), permittedPrincipal());
            int from = Math.min((request.getPageNumber() - 1) * request.getPageSize(), all.size());
            int to = Math.min(from + request.getPageSize(), all.size());
            return new Pagination<EmcEquiptMaintBean>(all.size(), request.getPageSize(), request.getPageNumber(),
                    new ArrayList<EmcEquiptMaintBean>(all.subList(from, to)));
        } catch (SQLException failure) { throw new IllegalStateException("equipment maintenance page query failed", failure); }
    }

    public Long insert(EmcEquiptMaintBean value, Long categoryId, OwnedPrincipal principal) throws SQLException {
        return OwnedWriteSupport.transaction(dataSource, connection -> {
            requireCategoryScope(connection, categoryId.longValue(), provider.permittedOrganizationIds(principal));
            long id = OwnedWriteSupport.nextIdentity(connection);
            try (PreparedStatement query = connection.prepareStatement(
                    "INSERT INTO T_EMC_EQUIPT_MAINT (ID,EQUIPTCATEGORYID,MAINTEVENT,EVENTDESC,WARNINGDAYS,SAME_DAY) VALUES (?,?,?,?,?,?)")) {
                query.setLong(1, id); query.setLong(2, categoryId.longValue()); query.setString(3, value.getMaintEvent());
                query.setString(4, value.getEventDesc());
                if (value.getWarningDays() == null) query.setNull(5, java.sql.Types.NUMERIC); else query.setLong(5, value.getWarningDays());
                query.setString(6, value.getSameDay()); query.executeUpdate();
            }
            return Long.valueOf(id);
        });
    }

    public void delete(List<Long> ids, OwnedPrincipal principal) throws SQLException {
        if (ids == null || ids.isEmpty() || ids.size() > 100) throw new IllegalArgumentException("maintenance event ids are required");
        OwnedWriteSupport.transaction(dataSource, connection -> {
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            if (organizations == null || organizations.isEmpty()) throw new org.springframework.security.access.AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
            for (Long id : ids) {
                if (id == null || selectByIdOnConnection(connection, id.longValue(), organizations) == null)
                    throw new org.springframework.security.access.AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
            }
            try (PreparedStatement query = connection.prepareStatement("DELETE FROM T_EMC_EQUIPT_MAINT WHERE ID=?")) {
                for (Long id : ids) { query.setLong(1, id.longValue()); query.addBatch(); }
                query.executeBatch();
            }
            return null;
        });
    }

    private static void requireCategoryScope(Connection connection, long categoryId, Set<String> organizations) throws SQLException {
        if (organizations == null || organizations.isEmpty()) throw new org.springframework.security.access.AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
        StringBuilder sql = new StringBuilder("SELECT 1 FROM T_EMC_EQUIPT_CATEGORY C WHERE C.ID=? AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION P WHERE P.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND P.TARGETID=C.ID AND P.ORGID IN (");
        for (int i = 0; i < organizations.size(); i++) { if (i > 0) sql.append(','); sql.append('?'); }
        sql.append("))");
        try (PreparedStatement query = connection.prepareStatement(sql.toString())) {
            int index = 1; query.setLong(index++, categoryId); for (String organization : organizations) query.setString(index++, organization);
            try (ResultSet rows = query.executeQuery()) { if (!rows.next()) throw new org.springframework.security.access.AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED"); }
        }
    }

    private List<EmcEquiptMaintBean> queryByCategory(long categoryId, OwnedPrincipal principal) throws SQLException {
        return queryByColumn(Collections.singletonList(Long.valueOf(categoryId)), "EQUIPTCATEGORYID", principal);
    }

    private List<EmcEquiptMaintBean> queryByColumn(List<Long> ids, String column, OwnedPrincipal principal) throws SQLException {
        Set<String> organizations = provider.permittedOrganizationIds(principal);
        if (organizations == null || organizations.isEmpty()) return Collections.emptyList();
        StringBuilder sql = new StringBuilder("SELECT M.ID,M.EQUIPTCATEGORYID,M.MAINTEVENT,M.EVENTDESC,M.WARNINGDAYS,M.SAME_DAY FROM T_EMC_EQUIPT_MAINT M WHERE M.").append(column).append(" IN (");
        for (int i = 0; i < ids.size(); i++) { if (i > 0) sql.append(','); sql.append('?'); }
        sql.append(") AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION P WHERE P.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND P.TARGETID=M.EQUIPTCATEGORYID AND P.ORGID IN (");
        for (int i = 0; i < organizations.size(); i++) { if (i > 0) sql.append(','); sql.append('?'); }
        sql.append(")) ORDER BY M.ID");
        List<EmcEquiptMaintBean> result = new ArrayList<EmcEquiptMaintBean>();
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql.toString())) {
            int index = 1; for (Long id : ids) query.setLong(index++, id.longValue());
            for (String organization : organizations) query.setString(index++, organization);
            try (ResultSet rows = query.executeQuery()) { while (rows.next()) result.add(read(rows)); }
        }
        return result;
    }

    private static EmcEquiptMaintBean selectByIdOnConnection(Connection connection, long id, Set<String> organizations) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT M.ID,M.EQUIPTCATEGORYID,M.MAINTEVENT,M.EVENTDESC,M.WARNINGDAYS,M.SAME_DAY FROM T_EMC_EQUIPT_MAINT M WHERE M.ID=? AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION P WHERE P.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND P.TARGETID=M.EQUIPTCATEGORYID AND P.ORGID IN (");
        for (int i = 0; i < organizations.size(); i++) { if (i > 0) sql.append(','); sql.append('?'); }
        sql.append("))");
        try (PreparedStatement query = connection.prepareStatement(sql.toString())) {
            int index = 1; query.setLong(index++, id); for (String organization : organizations) query.setString(index++, organization);
            try (ResultSet rows = query.executeQuery()) { return rows.next() ? read(rows) : null; }
        }
    }

    private OwnedPrincipal permittedPrincipal() throws SQLException {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes)) throw new IllegalStateException("equipment maintenance request context is required");
        HttpSession session = ((ServletRequestAttributes) attributes).getRequest().getSession(false);
        boundary.requirePermission(session, OwnedIdentityProvider.EQUIPMENT_READ);
        return boundary.currentPrincipal(session);
    }

    private static EmcEquiptMaintBean read(ResultSet rows) throws SQLException {
        EmcEquiptMaintBean value = new EmcEquiptMaintBean(); value.setId(rows.getLong("ID"));
        value.setEquiptCategoryId(rows.getLong("EQUIPTCATEGORYID")); value.setMaintEvent(rows.getString("MAINTEVENT"));
        value.setEventDesc(rows.getString("EVENTDESC")); long warning = rows.getLong("WARNINGDAYS");
        if (!rows.wasNull()) value.setWarningDays(Long.valueOf(warning)); value.setSameDay(rows.getString("SAME_DAY")); return value;
    }
}
