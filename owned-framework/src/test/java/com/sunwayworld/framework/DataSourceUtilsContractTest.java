package com.sunwayworld.framework;

import com.sunwayworld.framework.utils.DataSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/** Contract test for the owned JDBC factory; no connection is opened. */
public final class DataSourceUtilsContractTest {
    private DataSourceUtilsContractTest() {}

    public static void main(String[] args) {
        DriverManagerDataSource source = (DriverManagerDataSource) DataSourceUtils.dataSource(
                "oracle.jdbc.OracleDriver", "jdbc:oracle:thin:@localhost:1521/FREEPDB1",
                "fixture-user", null);
        if (!"jdbc:oracle:thin:@localhost:1521/FREEPDB1".equals(source.getUrl())) throw new AssertionError("url mismatch");
        if (!"fixture-user".equals(source.getUsername())) throw new AssertionError("username mismatch");
        if (source.getPassword() != null) throw new AssertionError("password must remain unset in the contract fixture");
        boolean rejected = false;
        try {
            DataSourceUtils.dataSource(null, " ", null, null);
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        if (!rejected) throw new AssertionError("empty JDBC url must be rejected");
        System.out.println("data source utils contract checks passed");
    }
}
