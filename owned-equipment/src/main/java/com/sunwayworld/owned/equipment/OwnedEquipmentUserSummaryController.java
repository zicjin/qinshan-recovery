package com.sunwayworld.owned.equipment;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.PfEquiptUserDTO;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.QueryRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/** Source-owned facade for the original equipment-user summary page. */
@RestController
@RequestMapping("/secure/emc/module/mdm/equipt/equipts")
public final class OwnedEquipmentUserSummaryController {
    private final DataSource dataSource;
    private final JdbcPersonnelIdentityProvider provider;
    private final OwnedIdentityHttpBoundary boundary;

    public OwnedEquipmentUserSummaryController(DataSource dataSource, JdbcPersonnelIdentityProvider provider,
            OwnedIdentityHttpBoundary boundary) {
        this.dataSource = dataSource; this.provider = provider; this.boundary = boundary;
    }

    @RequestMapping(value = "/action/select-equipt-user", method = RequestMethod.POST)
    public Page<PfEquiptUserDTO> read(HttpServletRequest request, RestJsonWrapperBean wrapper) {
        try {
            boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.EQUIPMENT_READ);
            OwnedPrincipal principal = boundary.currentPrincipal(request.getSession(false));
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            QueryRequest query = QueryRequest.parse(wrapper == null ? null : wrapper.getRequestJson(),
                    wrapper == null ? null : wrapper.getParamMap());
            if (!query.getFilterPatterns().isEmpty() || !query.getMapperParameters().isEmpty())
                throw new IllegalArgumentException("unsupported equipment-user summary filter");
            if (organizations == null || organizations.isEmpty())
                return new Pagination<PfEquiptUserDTO>(0, query.getPageSize(), query.getPageNumber(), Collections.<PfEquiptUserDTO>emptyList());
            List<Object> scopeValues = new ArrayList<Object>();
            String scope = scopeSql(organizations, scopeValues);
            String from = "FROM T_EMC_EQUIPT E WHERE " + scope;
            int start = (query.getPageNumber() - 1) * query.getPageSize() + 1;
            int end = query.getPageNumber() * query.getPageSize();
            try (Connection connection = dataSource.getConnection()) {
                int total;
                try (PreparedStatement count = connection.prepareStatement("SELECT COUNT(1) " + from)) {
                    bind(count, scopeValues);
                    try (ResultSet rows = count.executeQuery()) { rows.next(); total = rows.getInt(1); }
                }
                List<PfEquiptUserDTO> values = new ArrayList<PfEquiptUserDTO>();
                List<Object> pageValues = new ArrayList<Object>(scopeValues); pageValues.add(start); pageValues.add(end);
                String sql = "SELECT * FROM (SELECT E.EQUIPTID,E.EQUIPTNAME,E.SPECIFICATION,E.USEDUSERNAME,ROW_NUMBER() OVER (ORDER BY E.ID DESC) RN "
                        + from + ") WHERE RN BETWEEN ? AND ? ORDER BY RN";
                try (PreparedStatement select = connection.prepareStatement(sql)) {
                    bind(select, pageValues);
                    try (ResultSet rows = select.executeQuery()) {
                        while (rows.next()) {
                            PfEquiptUserDTO value = new PfEquiptUserDTO();
                            value.setEquiptNo(rows.getString("EQUIPTID"));
                            value.setEquiptName(rows.getString("EQUIPTNAME"));
                            value.setSpecification(rows.getString("SPECIFICATION"));
                            value.setUsers(rows.getString("USEDUSERNAME"));
                            values.add(value);
                        }
                    }
                }
                return new Pagination<PfEquiptUserDTO>(total, query.getPageSize(), query.getPageNumber(), values);
            }
        } catch (AccessDeniedException denied) { throw denied;
        } catch (SQLException failure) { throw new IllegalStateException("equipment user summary query failed", failure); }
    }

    private static String scopeSql(Set<String> organizations, List<Object> values) {
        StringBuilder sql = new StringBuilder("EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION OP WHERE OP.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND OP.TARGETID=E.EQUIPTCATEGORYID AND OP.ORGID IN (");
        int index = 0;
        for (String organization : organizations) { if (index++ > 0) sql.append(','); sql.append('?'); values.add(organization); }
        return sql.append("))").toString();
    }

    private static void bind(PreparedStatement statement, List<Object> values) throws SQLException {
        int index = 1; for (Object value : values) statement.setObject(index++, value);
    }
}
