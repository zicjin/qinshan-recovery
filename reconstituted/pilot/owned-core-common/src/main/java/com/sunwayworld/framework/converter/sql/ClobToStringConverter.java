/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.sql;

import com.microsoft.sqlserver.jdbc.SQLServerClob;
import com.microsoft.sqlserver.jdbc.SQLServerNClob;
import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import org.apache.commons.io.IOUtils;

public enum ClobToStringConverter implements Converter<Clob, String>
{
    INSTANCE;


    @Override
    public String convert(Clob source) {
        String string;
        if (source == null) {
            return null;
        }
        Clob clob = source;
        Reader reader = null;
        try {
            reader = clob.getCharacterStream();
            if (clob instanceof SQLServerClob || clob instanceof SQLServerNClob) {
                String string2 = IOUtils.toString(reader);
                return string2;
            }
            char[] chars = new char[(int)clob.length()];
            reader.read(chars);
            string = String.valueOf(chars);
        }
        catch (IOException | SQLException e) {
            throw new ConvertFailedException(this.getSourceType(), this.getTargetType(), e);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException iOException) {}
            }
        }
        return string;
    }
}
