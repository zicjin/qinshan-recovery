/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.string;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.SQLException;
import javax.sql.rowset.serial.SerialBlob;

public enum StringToBlobConverter implements Converter<String, Blob>
{
    INSTANCE;


    @Override
    public Blob convert(String source) {
        if (source == null) {
            return null;
        }
        try {
            return new SerialBlob(source.getBytes(StandardCharsets.UTF_8));
        }
        catch (SQLException e) {
            throw new ConvertFailedException(this.getSourceType(), this.getTargetType(), e);
        }
    }
}
