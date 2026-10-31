package com.sunwayworld.framework.converter.reader;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public enum ReaderToStringConverter implements Converter<Reader, String> {
    INSTANCE;

    public String convert(Reader source) {
        BufferedReader reader = new BufferedReader(source);
        StringBuilder output = new StringBuilder();
        try {
            String line;
            while ((line = reader.readLine()) != null) output.append(line).append('\n');
            return output.toString();
        } catch (IOException failure) {
            throw new ConvertFailedException(getSourceType(), getTargetType(), failure);
        } finally {
            try { reader.close(); } catch (IOException ignored) { }
        }
    }
}
