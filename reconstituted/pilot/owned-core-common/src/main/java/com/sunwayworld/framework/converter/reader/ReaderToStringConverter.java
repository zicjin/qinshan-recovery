/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.reader;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public enum ReaderToStringConverter implements Converter<Reader, String>
{
    INSTANCE;


    @Override
    public String convert(Reader source) {
        BufferedReader reader = new BufferedReader(source);
        StringBuilder sb = new StringBuilder();
        String line = "";
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        catch (IOException e) {
            throw new ConvertFailedException(this.getSourceType(), this.getTargetType(), e);
        }
        finally {
            try {
                reader.close();
            }
            catch (IOException iOException) {}
            reader = null;
        }
        return sb.toString();
    }
}
