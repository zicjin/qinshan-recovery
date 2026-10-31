package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.context.OwnedApplicationContext;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import javax.sql.DataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.jdbc.datasource.ConnectionHolder;

/** Checks the owned bridge's transaction-aware connection lifecycle. */
public final class OwnedJdbcConnectionContractTest {
    public static void main(String[] args) throws Exception {
        final int[] closes = {0};
        final Connection connection = (Connection) Proxy.newProxyInstance(
                OwnedJdbcConnectionContractTest.class.getClassLoader(), new Class<?>[] {Connection.class},
                (proxy, method, arguments) -> {
                    if (method.getName().equals("close")) { closes[0]++; return null; }
                    if (method.getName().equals("isClosed")) return closes[0] > 0;
                    if (method.getName().equals("unwrap")) throw new java.sql.SQLException("not oracle");
                    if (method.getName().equals("isWrapperFor")) return false;
                    if (method.getReturnType() == boolean.class) return false;
                    if (method.getReturnType() == int.class) return 0;
                    return null;
                });
        DataSource dataSource = (DataSource) Proxy.newProxyInstance(
                OwnedJdbcConnectionContractTest.class.getClassLoader(), new Class<?>[] {DataSource.class},
                (proxy, method, arguments) -> {
                    if (method.getName().equals("getConnection")) return connection;
                    if (method.getName().equals("isWrapperFor")) return false;
                    if (method.getReturnType() == boolean.class) return false;
                    if (method.getReturnType() == int.class) return 0;
                    return null;
                });
        OwnedApplicationContext.withConnection(dataSource, c -> { if (c != connection) throw new AssertionError("connection identity"); return null; });
        if (closes[0] != 1) throw new AssertionError("non-bound connection was not released");
        closes[0] = 0;
        TransactionSynchronizationManager.bindResource(dataSource, new ConnectionHolder(connection));
        try {
            OwnedApplicationContext.withConnection(dataSource, c -> { if (c != connection) throw new AssertionError("bound identity"); return null; });
            if (closes[0] != 0) throw new AssertionError("transaction-bound connection was closed");
        } finally { TransactionSynchronizationManager.unbindResource(dataSource); }
        System.out.println("owned JDBC connection lifecycle: PASS");
    }
}
