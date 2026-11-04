/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.array;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import java.sql.Clob;
import java.sql.SQLException;
import javax.sql.rowset.serial.SerialClob;

public enum PrimitiveCharacterArrayToClobConverter implements Converter<char[], Clob>
{
    INSTANCE;


    @Override
    public Clob convert(char[] source) {
        if (source == null) {
            return null;
        }
        try {
            return new SerialClob(source);
        }
        catch (SQLException e) {
            throw new ConvertFailedException(char[].class, Clob.class, e);
        }
    }
}
