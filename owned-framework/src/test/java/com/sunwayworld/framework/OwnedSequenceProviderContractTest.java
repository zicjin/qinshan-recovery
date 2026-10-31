package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.context.OwnedSequenceProvider;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicReference;
import javax.sql.DataSource;

/** Verifies the owned sequence seam without opening a database connection. */
public final class OwnedSequenceProviderContractTest {
    private OwnedSequenceProviderContractTest() {}

    public static void main(String[] args) {
        final AtomicReference<String> sql = new AtomicReference<String>();
        Connection connection = (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(), new Class<?>[]{Connection.class},
                (proxy, method, arguments) -> {
                    if ("prepareStatement".equals(method.getName())) {
                        sql.set((String) arguments[0]);
                        return Proxy.newProxyInstance(
                                PreparedStatement.class.getClassLoader(),
                                new Class<?>[]{PreparedStatement.class},
                                (statement, statementMethod, statementArguments) -> {
                                    if ("executeQuery".equals(statementMethod.getName())) {
                                        return Proxy.newProxyInstance(
                                                ResultSet.class.getClassLoader(),
                                                new Class<?>[]{ResultSet.class},
                                                (rows, rowsMethod, rowsArguments) -> {
                                                    if ("next".equals(rowsMethod.getName())) return true;
                                                    if ("getLong".equals(rowsMethod.getName())) return 123L;
                                                    if ("close".equals(rowsMethod.getName())) return null;
                                                    return defaultValue(rowsMethod.getReturnType());
                                                });
                                    }
                                    if ("close".equals(statementMethod.getName())) return null;
                                    return defaultValue(statementMethod.getReturnType());
                                });
                    }
                    if ("close".equals(method.getName())) return null;
                    return defaultValue(method.getReturnType());
                });
        DataSource source = (DataSource) Proxy.newProxyInstance(
                DataSource.class.getClassLoader(), new Class<?>[]{DataSource.class},
                (proxy, method, arguments) -> {
                    if ("getConnection".equals(method.getName())) return connection;
                    return defaultValue(method.getReturnType());
                });
        long value;
        try {
            value = new OwnedSequenceProvider(source).nextIdentity();
        } catch (Exception failure) {
            throw new AssertionError("sequence provider failed", failure);
        }
        if (value != 123L) throw new AssertionError("sequence value mismatch");
        if (!"SELECT SEQ_CORE.NEXTVAL FROM DUAL".equals(sql.get())) {
            throw new AssertionError("unexpected sequence SQL: " + sql.get());
        }
        System.out.println("owned sequence provider contract checks passed");
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return false;
        if (type == char.class) return '\0';
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0F;
        if (type == double.class) return 0D;
        return null;
    }
}
