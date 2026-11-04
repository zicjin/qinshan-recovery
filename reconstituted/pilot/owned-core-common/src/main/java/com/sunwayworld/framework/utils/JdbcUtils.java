/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.exception.database.JdbcException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class JdbcUtils {
    private static final Logger logger = LogManager.getLogger(JdbcUtils.class);

    public static final Map<String, Object> mapRow(ResultSet rs) {
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int count = rsmd.getColumnCount();
            HashMap<String, Object> map = new HashMap<String, Object>();
            for (int i = 0; i < count; ++i) {
                String name = rsmd.getColumnName(i + 1);
                map.put(name, rs.getObject(name));
            }
            return map;
        }
        catch (SQLException sqle) {
            throw new JdbcException(sqle);
        }
    }

    public static void closeResultSet(ResultSet rs) {
        block3: {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Throwable e) {
                    if (!logger.isDebugEnabled()) break block3;
                    logger.debug("Unexpected exception on closing JDBC ResultSet", e);
                }
            }
        }
    }

    public static void closeStatement(Statement stmt) {
        block3: {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Throwable e) {
                    if (!logger.isDebugEnabled()) break block3;
                    logger.debug("Unexpected exception on closing JDBC Statement", e);
                }
            }
        }
    }

    public static void closeConnection(Connection conn) {
        block3: {
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (Throwable e) {
                    if (!logger.isDebugEnabled()) break block3;
                    logger.debug("Unexpected exception on closing JDBC Connection", e);
                }
            }
        }
    }

    public static boolean isBigData(String content) {
        int length = content.length();
        if (length <= 1333) {
            return false;
        }
        if (length >= 4000) {
            return true;
        }
        int size = 0;
        for (char c : content.toCharArray()) {
            char i = c;
            size = i < '\u001c' || i > '\u0080' ? (size += 3) : ++size;
            if (size < 4000) continue;
            return true;
        }
        return size >= 4000;
    }
}
