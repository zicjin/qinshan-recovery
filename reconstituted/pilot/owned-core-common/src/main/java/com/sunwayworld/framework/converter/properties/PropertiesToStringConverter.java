/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.properties;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public enum PropertiesToStringConverter implements Converter<Properties, String>
{
    INSTANCE;


    @Override
    public String convert(Properties source) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream(256);
            source.store(os, null);
            return os.toString(StandardCharsets.UTF_8.name());
        }
        catch (IOException ioe) {
            throw new ConvertFailedException(this.getSourceType(), this.getTargetType(), ioe);
        }
    }
}
