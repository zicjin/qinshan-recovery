package com.sunwayworld.owned.internal.data;

import java.io.IOException;
import java.io.Reader;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.util.ArrayList;
import java.util.List;

/** Converts JDBC driver wrappers into values the JSON boundary can expose. */
public final class JdbcValue {
    private JdbcValue() { }

    public static Object normalize(Object value) throws SQLException {
        if (value == null) return null;
        if (value instanceof NClob) return read(((NClob) value).getCharacterStream(), ((NClob) value).length());
        if (value instanceof Clob) return read(((Clob) value).getCharacterStream(), ((Clob) value).length());
        if (value instanceof SQLXML) return ((SQLXML) value).getString();
        if (value instanceof Blob) {
            Blob blob = (Blob) value;
            long length = blob.length();
            if (length > Integer.MAX_VALUE) throw new SQLException("BLOB is too large for JSON boundary");
            return blob.getBytes(1, (int) length);
        }
        if (value instanceof Array) {
            Object raw = ((Array) value).getArray();
            if (raw instanceof Object[]) {
                List<Object> result = new ArrayList<Object>();
                for (Object item : (Object[]) raw) result.add(normalize(item));
                return result;
            }
            return raw;
        }
        return value;
    }

    private static String read(Reader reader, long length) throws SQLException {
        if (length > Integer.MAX_VALUE) throw new SQLException("CLOB is too large for JSON boundary");
        StringBuilder result = new StringBuilder((int) Math.max(0, length));
        char[] buffer = new char[4096];
        try (Reader source = reader) {
            int count;
            while ((count = source.read(buffer)) >= 0) result.append(buffer, 0, count);
            return result.toString();
        } catch (IOException failure) {
            throw new SQLException("Unable to read JDBC character value", failure);
        }
    }
}
