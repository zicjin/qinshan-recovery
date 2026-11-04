/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.sql;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.SQLException;

public enum BlobToStringConverter implements Converter<Blob, String>
{
    INSTANCE;


    @Override
    public String convert(Blob source) {
        if (source == null) {
            return null;
        }
        try {
            return new String(source.getBytes(1L, (int)source.length()), StandardCharsets.UTF_8);
        }
        catch (SQLException ex) {
            throw new ConvertFailedException(this.getSourceType(), this.getTargetType(), ex);
        }
    }
}
