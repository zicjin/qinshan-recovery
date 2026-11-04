/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.string;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import java.sql.Clob;
import java.sql.SQLException;
import javax.sql.rowset.serial.SerialClob;

public enum StringToClobConverter implements Converter<String, Clob>
{
    INSTANCE;


    @Override
    public Clob convert(String source) {
        if (source == null) {
            return null;
        }
        try {
            return new SerialClob(source.toCharArray());
        }
        catch (SQLException e) {
            throw new ConvertFailedException(this.getSourceType(), this.getTargetType(), e);
        }
    }
}
