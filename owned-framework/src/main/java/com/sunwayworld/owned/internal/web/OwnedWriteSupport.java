package com.sunwayworld.owned.internal.web;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.security.access.AccessDeniedException;

/** Shared write boundary for isolated owned modules. */
public final class OwnedWriteSupport {
    public interface TransactionWork<T> { T run(Connection connection) throws SQLException; }

    private OwnedWriteSupport() { }

    public static boolean isLoopback(HttpServletRequest request) {
        if (request == null) return false;
        String remote = request.getRemoteAddr();
        return "127.0.0.1".equals(remote) || "0:0:0:0:0:0:0:1".equals(remote) || "::1".equals(remote);
    }

    public static void requireLoopback(HttpServletRequest request) {
        if (!isLoopback(request)) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
    }

    public static long nextIdentity(Connection connection) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("SELECT SEQ_CORE.NEXTVAL FROM DUAL");
                ResultSet rows = query.executeQuery()) {
            if (!rows.next()) throw new SQLException("identity sequence returned no value");
            return rows.getLong(1);
        }
    }

    public static <T> T transaction(DataSource dataSource, TransactionWork<T> work) throws SQLException {
        if (dataSource == null) throw new IllegalArgumentException("data source is required");
        if (work == null) throw new IllegalArgumentException("transaction work is required");
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                T result = work.run(connection);
                connection.commit();
                return result;
            } catch (RuntimeException | SQLException failure) {
                try { connection.rollback(); }
                catch (SQLException rollbackFailure) { failure.addSuppressed(rollbackFailure); }
                throw failure;
            } finally {
                try { connection.setAutoCommit(true); } catch (SQLException ignored) { }
            }
        }
    }

    public static <T> T transactionUnchecked(DataSource dataSource, TransactionWork<T> work) {
        try { return transaction(dataSource, work); }
        catch (SQLException failure) { throw new IllegalStateException("owned transaction failed", failure); }
    }
}
