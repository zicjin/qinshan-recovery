/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.sql;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

public enum BlobToPrimitiveByteArrayConverter implements Converter<Blob, byte[]>
{
    INSTANCE;


    @Override
    public byte[] convert(Blob source) {
        byte[] byArray;
        if (source == null) {
            return null;
        }
        InputStream is = null;
        try {
            if (source.length() == 0L) {
                byte[] byArray2 = new byte[]{};
                return byArray2;
            }
            is = source.getBinaryStream();
            byte[] bytes = new byte[(int)source.length()];
            is.read(bytes, 0, (int)source.length());
            byArray = bytes;
        }
        catch (IOException | SQLException e) {
            throw new ConvertFailedException(Blob.class, byte[].class, e);
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException iOException) {}
            }
        }
        return byArray;
    }
}
