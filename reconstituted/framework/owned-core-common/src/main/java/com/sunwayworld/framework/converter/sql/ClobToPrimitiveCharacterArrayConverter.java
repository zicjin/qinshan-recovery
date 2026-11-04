/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.sql;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import java.io.IOException;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;

public enum ClobToPrimitiveCharacterArrayConverter implements Converter<Clob, char[]>
{
    INSTANCE;


    @Override
    public char[] convert(Clob source) {
        char[] cArray;
        if (source == null) {
            return null;
        }
        Reader reader = null;
        try {
            if (source.length() == 0L) {
                char[] cArray2 = new char[]{};
                return cArray2;
            }
            reader = source.getCharacterStream();
            char[] chars = new char[(int)source.length()];
            reader.read(chars);
            cArray = chars;
        }
        catch (IOException | SQLException e) {
            throw new ConvertFailedException(Blob.class, byte[].class, e);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException iOException) {}
            }
        }
        return cArray;
    }
}
