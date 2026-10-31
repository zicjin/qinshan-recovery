package com.sunwayworld.framework;

import com.sunwayworld.framework.converter.date.JSR310ToDateGenericConverter;
import java.time.temporal.Temporal;
import java.util.Date;

public final class DateConverterFixture implements JSR310ToDateGenericConverter<Temporal, Date> {
    private final Class<Temporal> source;
    private final Class<Date> target;

    @SuppressWarnings("unchecked")
    public DateConverterFixture(Class<?> source, Class<?> target) {
        this.source = (Class<Temporal>) source;
        this.target = (Class<Date>) target;
    }

    public Class<Temporal> getSourceType() { return source; }
    public Class<Date> getTargetType() { return target; }
}
