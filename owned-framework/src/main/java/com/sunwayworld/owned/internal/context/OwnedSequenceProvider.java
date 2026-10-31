package com.sunwayworld.owned.internal.context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

/** Oracle SEQ_CORE bridge used by compatibility callers that allocate IDs. */
public final class OwnedSequenceProvider {
    private final DataSource dataSource;

    public OwnedSequenceProvider(DataSource dataSource) {
        if (dataSource == null) throw new IllegalArgumentException("dataSource must not be null");
        this.dataSource = dataSource;
    }

    public long nextIdentity() throws SQLException {
        Connection connection = OwnedApplicationContext.connection(dataSource);
        try (PreparedStatement query = connection.prepareStatement("SELECT SEQ_CORE.NEXTVAL FROM DUAL");
             ResultSet rows = query.executeQuery()) {
            if (!rows.next()) throw new SQLException("SEQ_CORE returned no value");
            return rows.getLong(1);
        } finally {
            org.springframework.jdbc.datasource.DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }
}
