/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.array;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import java.sql.Blob;
import java.sql.SQLException;
import javax.sql.rowset.serial.SerialBlob;

public enum PrimitiveByteArrayToBlobConverter implements Converter<byte[], Blob>
{
    INSTANCE;


    @Override
    public Blob convert(byte[] source) {
        if (source == null) {
            return null;
        }
        try {
            return new SerialBlob(source);
        }
        catch (SQLException e) {
            throw new ConvertFailedException(byte[].class, Blob.class, e);
        }
    }
}
