package com.sunwayworld.framework.converter.properties;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public enum PropertiesToStringConverter implements Converter<Properties, String> {
    INSTANCE;

    public String convert(Properties source) {
        ByteArrayOutputStream output = new ByteArrayOutputStream(256);
        try {
            source.store(output, null);
            return output.toString(StandardCharsets.UTF_8.name());
        } catch (IOException failure) {
            throw new ConvertFailedException(getSourceType(), getTargetType(), failure);
        }
    }
}
