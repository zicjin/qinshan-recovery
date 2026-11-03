package com.sunwayworld.owned.internal.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.sql.DataSource;

/** Reads the original UserAccountDTO and role-assignment inputs, never password columns. */
public final class AccountQueryDao {
    private final DataSource source;

    public AccountQueryDao(DataSource source) {
        if (source == null) throw new IllegalArgumentException("account data source required");
        this.source = source;
    }

    public Snapshot read(String userId) throws SQLException {
        Map<String, Object> user;
        List<Map<String, Object>> assignments = new ArrayList<Map<String, Object>>();
        String appUuid = null;
        try (Connection connection = source.getConnection()) {
            try (PreparedStatement query = connection.prepareStatement(
                    "SELECT ID,USERNO,USERNAME,USERTYPE,ORGID,ORGNAME,PARENTORGID FROM T_CORE_USER WHERE ID=?")) {
                query.setString(1, userId);
                try (ResultSet rows = query.executeQuery()) {
                    if (!rows.next()) return null;
                    user = record(rows, "id userNo userName userType orgId orgName parentOrgId");
                    if (rows.next()) throw new SQLException("non-unique account identity");
                }
            }
            // CoreRoleUserService.selectByUserId uses USERID without a join or
            // active-role predicate. These are assignment records, not effective
            // permissions. The identity provider continues to filter active grants.
            try (PreparedStatement query = connection.prepareStatement(
                    "SELECT ID,ROLEID,USERID,DEFAULTFLAG,CREATEDBYID,CREATEDBYNAME,CREATEDBYORGNAME,CREATEDTIME FROM T_CORE_ROLE_USER WHERE USERID=?")) {
                query.setString(1, userId);
                try (ResultSet rows = query.executeQuery()) {
                    while (rows.next()) {
                        Map<String, Object> role = record(rows, "id roleId userId defaultFlag createdById createdByName createdByOrgName createdTime");
                        role.put("ext$", new LinkedHashMap<String, Object>());
                        assignments.add(role);
                    }
                }
            }
            try (PreparedStatement query = connection.prepareStatement(
                    "SELECT PARAMVALUE FROM T_LCDP_CONFIG_PARAM WHERE PARAMCODE=?")) {
                query.setString(1, "appuuid");
                try (ResultSet rows = query.executeQuery()) { if (rows.next()) appUuid = rows.getString(1); }
            } catch (SQLException optionalConfigurationAbsent) {
                // LCDP is an optional module in the clone. Oracle ORA-00942
                // must yield the original absent appuuid field; other errors
                // still fail closed rather than fabricating an account.
                if (optionalConfigurationAbsent.getErrorCode() != 942) throw optionalConfigurationAbsent;
            }
        }
        return new Snapshot(user, assignments, appUuid);
    }

    private static Map<String, Object> record(ResultSet rows, String allowedFields) throws SQLException {
        Map<String, String> fields = new LinkedHashMap<String, String>();
        for (String field : allowedFields.split(" ")) fields.put(field.toUpperCase(Locale.ROOT), field);
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        ResultSetMetaData metadata = rows.getMetaData();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String field = fields.get(metadata.getColumnLabel(i).toUpperCase(Locale.ROOT));
            if (field == null) continue;
            Object value = JdbcValue.normalize(rows.getObject(i));
            if (value instanceof Timestamp) value = ((Timestamp) value).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (value != null) result.put(field, value);
        }
        return result;
    }

    public static final class Snapshot {
        final Map<String, Object> user;
        final List<Map<String, Object>> assignments;
        final String appUuid;
        Snapshot(Map<String, Object> user, List<Map<String, Object>> assignments, String appUuid) {
            this.user = user; this.assignments = assignments; this.appUuid = appUuid;
        }
    }
}
