/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.string;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public enum StringToPropertiesConverter implements Converter<String, Properties>
{
    INSTANCE;


    @Override
    public Properties convert(String source) {
        try {
            Properties props = new Properties();
            props.load(new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)));
            return props;
        }
        catch (IOException ioe) {
            throw new ConvertFailedException(this.getSourceType(), this.getTargetType(), ioe);
        }
    }
}
