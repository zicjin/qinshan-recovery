/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JacksonCaseInsensitiveLinkedMapSerializer
extends JsonSerializer<CaseInsensitiveLinkedMap<String>> {
    private static Logger logger = LogManager.getLogger(JacksonCaseInsensitiveLinkedMapSerializer.class);

    @Override
    public void serialize(CaseInsensitiveLinkedMap<String> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        value.forEach((k, v) -> {
            try {
                gen.writeStringField((String)k, (String)v);
            }
            catch (IOException ioe) {
                logger.error(ioe.getMessage(), (Throwable)ioe);
            }
        });
        gen.writeEndObject();
    }
}
