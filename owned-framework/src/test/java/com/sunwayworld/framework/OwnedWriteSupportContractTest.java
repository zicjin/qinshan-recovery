package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedWriteSupport;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sql.DataSource;

/** Public transaction seam: commit, rollback, and connection-state cleanup. */
public final class OwnedWriteSupportContractTest {
    public static void main(String[] args) throws Exception {
        successfulCommitRestoresAutoCommit();
        runtimeFailureRollsBackAndPreservesRollbackFailure();
        sqlFailureRollsBackAndRestoresAutoCommit();
        System.out.println("owned write support contract checks passed (3 checks)");
    }

    private static void successfulCommitRestoresAutoCommit() throws Exception {
        Probe probe = new Probe(false);
        String value = OwnedWriteSupport.transaction(probe.dataSource(), connection -> {
            if (connection.getAutoCommit()) throw new AssertionError("transaction did not disable auto-commit");
            return "committed";
        });
        check("committed".equals(value), "transaction result returned");
        check(probe.committed.get(), "commit called");
        check(!probe.rolledBack.get(), "successful transaction not rolled back");
        check(probe.autoCommit.get(), "auto-commit restored after commit");
    }

    private static void runtimeFailureRollsBackAndPreservesRollbackFailure() throws Exception {
        Probe probe = new Probe(true);
        RuntimeException failure = new IllegalStateException("business failure");
        try {
            OwnedWriteSupport.transaction(probe.dataSource(), connection -> { throw failure; });
            throw new AssertionError("runtime failure was swallowed");
        } catch (IllegalStateException observed) {
            check(observed == failure, "original runtime failure preserved");
            check(observed.getSuppressed().length == 1, "rollback failure preserved as suppressed diagnostic");
            check("rollback failure".equals(observed.getSuppressed()[0].getMessage()), "rollback diagnostic message preserved");
        }
        check(probe.rolledBack.get(), "runtime failure rolled back");
        check(probe.autoCommit.get(), "auto-commit restored after runtime failure");
    }

    private static void sqlFailureRollsBackAndRestoresAutoCommit() throws Exception {
        Probe probe = new Probe(false);
        SQLException failure = new SQLException("sql failure");
        try {
            OwnedWriteSupport.transaction(probe.dataSource(), connection -> { throw failure; });
            throw new AssertionError("sql failure was swallowed");
        } catch (SQLException observed) {
            check(observed == failure, "original SQL failure preserved");
        }
        check(probe.rolledBack.get(), "SQL failure rolled back");
        check(probe.autoCommit.get(), "auto-commit restored after SQL failure");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static final class Probe {
        private final AtomicBoolean autoCommit = new AtomicBoolean(true);
        private final AtomicBoolean committed = new AtomicBoolean(false);
        private final AtomicBoolean rolledBack = new AtomicBoolean(false);
        private final boolean failRollback;

        Probe(boolean failRollback) { this.failRollback = failRollback; }

        DataSource dataSource() {
            return (DataSource) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {DataSource.class},
                    (proxy, method, args) -> {
                        if ("getConnection".equals(method.getName())) return connection();
                        if ("isWrapperFor".equals(method.getName())) return false;
                        if ("unwrap".equals(method.getName())) throw new SQLException("not wrapped");
                        return defaultValue(method.getReturnType());
                    });
        }

        private Connection connection() {
            return (Connection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {Connection.class},
                    (proxy, method, args) -> {
                        switch (method.getName()) {
                            case "getAutoCommit": return autoCommit.get();
                            case "setAutoCommit": autoCommit.set((Boolean) args[0]); return null;
                            case "commit": committed.set(true); return null;
                            case "rollback":
                                rolledBack.set(true);
                                if (failRollback) throw new SQLException("rollback failure");
                                return null;
                            case "close": return null;
                            case "isClosed": return false;
                            case "isWrapperFor": return false;
                            case "unwrap": throw new SQLException("not wrapped");
                            default: return defaultValue(method.getReturnType());
                        }
                    });
        }

        private static Object defaultValue(Class<?> type) {
            if (!type.isPrimitive()) return null;
            if (type == Boolean.TYPE) return false;
            if (type == Byte.TYPE) return (byte) 0;
            if (type == Short.TYPE) return (short) 0;
            if (type == Integer.TYPE) return 0;
            if (type == Long.TYPE) return 0L;
            if (type == Float.TYPE) return 0F;
            if (type == Double.TYPE) return 0D;
            if (type == Character.TYPE) return '\0';
            return null;
        }
    }
}
