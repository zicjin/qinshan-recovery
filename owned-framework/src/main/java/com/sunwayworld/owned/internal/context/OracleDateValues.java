package com.sunwayworld.owned.internal.context;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import javax.sql.DataSource;
import oracle.jdbc.OracleConnection;
import oracle.sql.Datum;
import oracle.sql.DATE;
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;

/** Oracle driver calls are scoped to the current real Spring DataSource. */
public final class OracleDateValues {
    private OracleDateValues() {}
    public static Date read(final Datum value, final Class<?> type) throws SQLException {
        if (type == TIMESTAMP.class) return new Date(((TIMESTAMP)value).dateValue().getTime());
        if (type == DATE.class) return new Date(((DATE)value).dateValue().getTime());
        if (type == TIMESTAMPTZ.class || type == TIMESTAMPLTZ.class) {
            DataSource source = OwnedApplicationContext.getBean(DataSource.class);
            if (source == null) throw new SQLException("No owned DataSource is bound to the application context");
            return OwnedApplicationContext.withConnection(source, new OwnedApplicationContext.JdbcWork<Date>() {
                public Date apply(Connection connection) throws SQLException {
                    Connection oracle = connection.getClass().equals(OracleConnection.class)
                            ? connection : connection.unwrap(OracleConnection.class);
                    Date date = type == TIMESTAMPTZ.class
                            ? ((TIMESTAMPTZ)value).dateValue(oracle) : ((TIMESTAMPLTZ)value).dateValue(oracle);
                    return new Date(date.getTime());
                }
            });
        }
        return value.dateValue();
    }
}
