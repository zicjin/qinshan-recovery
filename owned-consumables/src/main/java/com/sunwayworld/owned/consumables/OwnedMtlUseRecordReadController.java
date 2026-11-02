package com.sunwayworld.owned.consumables;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlUseRecordBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.QueryRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Source-owned read implementation for the material-use-record resource family. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedMtlUseRecordReadController {
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedMtlUseRecordReadController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("material-use dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @PostMapping({
            "/secure/emc/module/mdm/basemdm/mtl-use-records/queries",
            "/secure/emc/module/mdm/basemdm/mtl-use-records/queries/raw",
            "/secure/emc/module/mdm/basemdm/mtl-use-records/queries/searchable"})
    public Page<EmcMtlUseRecordBean> query(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        return read(request, body, null);
    }

    @PostMapping("/secure/emc/module/mdm/basemdm/mtl-use-records/{id}/use-record/queries")
    public Page<EmcMtlUseRecordBean> byApply(@PathVariable String id, HttpServletRequest request,
            @RequestBody(required = false) String body) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("apply id is invalid");
        return read(request, body, id);
    }

    @GetMapping({
            "/secure/emc/module/mdm/basemdm/mtl-use-records/info/status",
            "/secure/emc/module/mdm/basemdm/mtl-use-records/{id}/info/status"})
    public Map<String, Object> status(HttpServletRequest request) {
        requireRead(request);
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("ext$", new LinkedHashMap<String, Object>());
        return result;
    }

    @GetMapping("/secure/emc/module/mdm/basemdm/mtl-use-records/{id}")
    public EmcMtlUseRecordBean detail(@PathVariable Long id, HttpServletRequest request) {
        requireRead(request);
        if (id == null || id.longValue() < 0L) throw new IllegalArgumentException("use-record id is invalid");
        try {
            Set<String> organizations = boundary.permittedOrganizationIds(request.getSession(false));
            List<Object> values = new ArrayList<Object>();
            String scope = scope("T.CREATEDBYORGID", organizations, values);
            values.add(0, id);
            try (Connection connection = dataSource.getConnection();
                    PreparedStatement query = connection.prepareStatement(
                            "SELECT T.* FROM T_EMC_MTL_USE_RECORD T WHERE T.ID=? AND " + scope)) {
                bind(query, values);
                try (ResultSet rows = query.executeQuery()) {
                    return rows.next() ? row(rows) : null;
                }
            }
        } catch (SQLException failure) {
            throw new IllegalStateException("material-use detail query failed", failure);
        }
    }

    private Page<EmcMtlUseRecordBean> read(HttpServletRequest request, String body, String applyId) {
        requireRead(request);
        try {
            QueryRequest query = QueryRequest.parse(body, null);
            if (!query.getAdvancedPatterns().isEmpty() || query.getMultiPropertyPattern() != null)
                throw new IllegalArgumentException("advanced material-use query shape is unsupported");
            Set<String> organizations = boundary.permittedOrganizationIds(request.getSession(false));
            List<Object> values = new ArrayList<Object>();
            StringBuilder where = new StringBuilder(scope("T.CREATEDBYORGID", organizations, values));
            if (applyId != null) { where.append(" AND T.APPLYID=?"); values.add(applyId); }
            appendMapper(query, where, values);
            appendFilters(query, where, values);
            String order = order(query);
            String base = "SELECT T.* FROM T_EMC_MTL_USE_RECORD T WHERE " + where;
            int start = (query.getPageNumber() - 1) * query.getPageSize();
            int end = query.getPageNumber() * query.getPageSize();
            try (Connection connection = dataSource.getConnection()) {
                int total;
                try (PreparedStatement count = connection.prepareStatement("SELECT COUNT(1) FROM (" + base + ") X")) {
                    bind(count, values);
                    try (ResultSet rows = count.executeQuery()) { rows.next(); total = rows.getInt(1); }
                }
                List<Object> pageValues = new ArrayList<Object>(values);
                pageValues.add(start); pageValues.add(end);
                List<EmcMtlUseRecordBean> rows = new ArrayList<EmcMtlUseRecordBean>();
                String sql = "SELECT * FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY " + order
                        + ") RN FROM (" + base + ") Q) WHERE RN>? AND RN<=? ORDER BY RN";
                try (PreparedStatement select = connection.prepareStatement(sql)) {
                    bind(select, pageValues);
                    try (ResultSet result = select.executeQuery()) {
                        while (result.next()) rows.add(row(result));
                    }
                }
                Pagination<EmcMtlUseRecordBean> page = new Pagination<EmcMtlUseRecordBean>(
                        total, query.getPageSize(), query.getPageNumber(), rows);
                Map<String, String> types = new LinkedHashMap<String, String>();
                types.put("amount", "string"); types.put("applyid", "string");
                types.put("createdbyid", "string"); types.put("createdbyname", "string");
                types.put("createdbyorgid", "string"); types.put("createdbyorgname", "string");
                types.put("createdtime", "dateTime"); types.put("id", "number");
                types.put("mtlid", "string"); types.put("remark", "string");
                types.put("useddate", "dateTime"); types.put("usedept", "string");
                types.put("usedeptcode", "string"); types.put("usercode", "string");
                types.put("username", "string");
                page.setColumnTypeMap(types);
                Map<String, Number> totals = new LinkedHashMap<String, Number>();
                totals.put("qty_", Integer.valueOf(total)); page.setTotalMap(totals);
                return page;
            }
        } catch (SQLException failure) {
            throw new IllegalStateException("material-use query failed", failure);
        }
    }

    private void requireRead(HttpServletRequest request) {
        try {
            boundary.requirePermission(request == null ? null : request.getSession(false), OwnedIdentityProvider.CONSUMABLE_READ);
        } catch (SQLException failure) {
            throw new IllegalStateException("material-use identity check failed", failure);
        }
    }

    private static void appendMapper(QueryRequest query, StringBuilder where, List<Object> values) {
        for (Map.Entry<String, Object> item : query.getMapperParameters().entrySet()) {
            String column = column(item.getKey());
            where.append(" AND T.").append(column).append("=?"); values.add(item.getValue());
        }
    }

    private static void appendFilters(QueryRequest query, StringBuilder where, List<Object> values) {
        for (FilterParamPattern filter : query.getFilterPatterns()) {
            MatchPattern pattern = MatchPattern.getPatternIfPossible(filter.getMatchPattern());
            if (pattern == null) throw new IllegalArgumentException("unsupported material-use filter pattern");
            for (Map.Entry<String, Object> item : filter.getMap().entrySet()) {
                String expression = "T." + column(item.getKey());
                Object value = item.getValue();
                if (pattern == MatchPattern.ISNULL) { where.append(" AND ").append(expression).append(" IS NULL"); continue; }
                if (pattern == MatchPattern.ISNOTNULL) { where.append(" AND ").append(expression).append(" IS NOT NULL"); continue; }
                if (pattern == MatchPattern.EQ || pattern == MatchPattern.SEQ) {
                    where.append(" AND ").append(expression).append("=?"); values.add(value); continue;
                }
                if (pattern == MatchPattern.CISC || pattern == MatchPattern.SC) {
                    where.append(" AND ").append(pattern == MatchPattern.CISC ? "UPPER(" + expression + ")" : expression).append(" LIKE ?");
                    String text = String.valueOf(value);
                    values.add(pattern == MatchPattern.CISC ? "%" + text.toUpperCase(Locale.ROOT) + "%" : "%" + text + "%");
                    continue;
                }
                if (pattern == MatchPattern.NG || pattern == MatchPattern.NGOE || pattern == MatchPattern.NL || pattern == MatchPattern.NLOE) {
                    where.append(" AND ").append(expression).append(pattern == MatchPattern.NG ? ">" : pattern == MatchPattern.NGOE ? ">=" : pattern == MatchPattern.NL ? "<" : "<=").append("?");
                    values.add(value); continue;
                }
                throw new IllegalArgumentException("unsupported material-use filter pattern: " + pattern.name());
            }
        }
    }

    private static String order(QueryRequest query) {
        if (query.getOrderParameters().isEmpty()) return "Q.ID DESC";
        if (query.getOrderParameters().size() > 1) throw new IllegalArgumentException("multiple material-use order fields are unsupported");
        String column = column(query.getOrderParameters().get(0).getFirst());
        String direction = query.getOrderParameters().get(0).getSecond();
        return "Q." + column + ("desc".equalsIgnoreCase(direction) ? " DESC" : " ASC");
    }

    private static String column(String raw) {
        String value = raw == null ? "" : raw.toUpperCase(Locale.ROOT).replace("EXT$.", "");
        if ("ID".equals(value) || "APPLYID".equals(value) || "MTLID".equals(value)
                || "CREATEDBYID".equals(value) || "CREATEDBYORGID".equals(value)
                || "AMOUNT".equals(value) || "USERNAME".equals(value) || "USERCODE".equals(value)
                || "USEDEPT".equals(value) || "USEDEPTCODE".equals(value) || "USED DATE".equals(value)
                || "USEDDATE".equals(value) || "REMARK".equals(value)) return value.replace(" ", "");
        throw new IllegalArgumentException("unsupported material-use field: " + raw);
    }

    private static String scope(String column, Set<String> organizations, List<Object> values) {
        if (organizations == null || organizations.isEmpty()) return "1=0";
        StringBuilder result = new StringBuilder(column).append(" IN (");
        int index = 0;
        for (String organization : organizations) { if (index++ > 0) result.append(','); result.append('?'); values.add(organization); }
        return result.append(')').toString();
    }

    private static void bind(PreparedStatement statement, List<Object> values) throws SQLException {
        for (int index = 0; index < values.size(); index++) statement.setObject(index + 1, values.get(index));
    }

    private static EmcMtlUseRecordBean row(ResultSet rows) throws SQLException {
        EmcMtlUseRecordBean value = new EmcMtlUseRecordBean();
        value.setId(rows.getLong("ID")); value.setApplyId(rows.getString("APPLYID")); value.setMtlId(rows.getString("MTLID"));
        value.setCreatedById(rows.getString("CREATEDBYID")); value.setCreatedByName(rows.getString("CREATEDBYNAME"));
        value.setCreatedByOrgId(rows.getString("CREATEDBYORGID")); value.setCreatedByOrgName(rows.getString("CREATEDBYORGNAME"));
        value.setAmount(rows.getString("AMOUNT")); value.setUserName(rows.getString("USERNAME")); value.setUserCode(rows.getString("USERCODE"));
        value.setUseDept(rows.getString("USEDEPT")); value.setUseDeptCode(rows.getString("USEDEPTCODE")); value.setRemark(rows.getString("REMARK"));
        Timestamp created = rows.getTimestamp("CREATEDTIME"); if (created != null) value.setCreatedTime(created.toLocalDateTime());
        Timestamp used = rows.getTimestamp("USEDDATE"); if (used != null) value.setUsedDate(used.toLocalDateTime());
        return value;
    }
}
