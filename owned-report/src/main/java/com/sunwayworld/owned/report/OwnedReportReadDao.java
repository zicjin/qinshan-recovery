package com.sunwayworld.owned.report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

/** Oracle adapter for the historical report-line tables. */
public final class OwnedReportReadDao {
    private final DataSource dataSource;
    public OwnedReportReadDao(DataSource dataSource) { this.dataSource = dataSource; }

    public OwnedReportRecord find(String id, String userId) throws SQLException {
        if (id == null || !id.matches("[0-9]{1,20}")) throw new IllegalArgumentException("report id is invalid");
        String sql = "SELECT TO_CHAR(L.ID), L.REPORTNAME, L.REPORTCATEGORY, L.REPORTPATH, L.PARAM "
                + "FROM T_CORE_REPORT_CONFIG_LINE L WHERE TO_CHAR(L.ID)=? "
                + "AND L.REPORTCONFIGID='homePageReport' AND L.ACTIVATEDFLAG='1' AND ("
                + "EXISTS (SELECT 1 FROM T_CORE_USER_PERMISSION U WHERE U.TARGETID=TO_CHAR(L.ID) "
                + "AND U.PERMISSIONTYPEID='T_CORE_REPORT_CONFIG_LINE' AND U.USERID=?) OR "
                + "EXISTS (SELECT 1 FROM T_CORE_ROLE_USER RU JOIN T_CORE_ROLE R ON R.ID=RU.ROLEID "
                + "JOIN T_CORE_ROLE_PERMISSION RP ON RP.ROLEID=R.ID WHERE RU.USERID=? "
                + "AND R.LASTSUSPENDEDFLAG='0' AND RP.PERMISSIONTYPEID='T_CORE_REPORT_CONFIG_LINE' "
                + "AND RP.TARGETID=TO_CHAR(L.ID)))";
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.setString(2, userId);
            statement.setString(3, userId);
            try (ResultSet rows = statement.executeQuery()) {
                if (!rows.next()) return null;
                OwnedReportRecord value = new OwnedReportRecord(rows.getString(1), rows.getString(2), rows.getString(3),
                        rows.getString(4), rows.getString(5));
                if (rows.next()) throw new SQLException("report id is not unique");
                return value;
            }
        }
    }
}
