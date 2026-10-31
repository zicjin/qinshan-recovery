package com.sunwayworld.framework.converter.date;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.ConverterFactory;
import com.sunwayworld.framework.converter.exception.ConverterNotFoundException;
import com.sunwayworld.framework.converter.number.NumberToBooleanConverter;
import com.sunwayworld.framework.converter.number.NumberToNumberConverterFactory;
import com.sunwayworld.framework.converter.number.NumberToStringConverter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;

public enum JSR310ToJSR310ConverterFactory implements ConverterFactory<Temporal, Temporal> {
    INSTANCE;

    public <T extends Temporal> Converter<Temporal, T> getConverter(Class<T> targetType) {
        return new JSR310ToJSR310Converter<T>(targetType);
    }

    class JSR310ToJSR310Converter<T extends Temporal> implements Converter<Temporal, T> {
        private final Class<T> jsr310Type;

        public JSR310ToJSR310Converter(Class<T> jsr310Type) {
            this.jsr310Type = jsr310Type;
        }

        public Class<Temporal> getSourceType() { return Temporal.class; }

        public Class<T> getTargetType() { return jsr310Type; }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public T convert(Temporal source) {
            if (source == null) return null;
            Long millis;
            if (source instanceof LocalDateTime)
                millis = JSR310ToLongGenericConverter.LocalDateTimeToLongConverter.INSTANCE.convert((LocalDateTime) source);
            else if (source instanceof LocalDate)
                millis = JSR310ToLongGenericConverter.LocalDateToLongConverter.INSTANCE.convert((LocalDate) source);
            else if (source instanceof LocalTime)
                millis = JSR310ToLongGenericConverter.LocalTimeToLongConverter.INSTANCE.convert((LocalTime) source);
            else if (source instanceof Number)
                millis = NumberToNumberConverterFactory.INSTANCE.getConverter(Long.class).convert((Number) source);
            else throw new ConverterNotFoundException(source.getClass(), Long.TYPE);

            Class<?> requestedTarget = getTargetType();
            Class<?> target = requestedTarget;
            if (target.isPrimitive()) {
                if (target == boolean.class) target = Boolean.class;
                else if (target == byte.class) target = Byte.class;
                else if (target == char.class) target = Character.class;
                else if (target == short.class) target = Short.class;
                else if (target == int.class) target = Integer.class;
                else if (target == long.class) target = Long.class;
                else if (target == float.class) target = Float.class;
                else if (target == double.class) target = Double.class;
            }
            Object converted;
            if (target.isAssignableFrom(Long.class)) converted = millis;
            else if (target == String.class) converted = NumberToStringConverter.INSTANCE.convert(millis);
            else if (target == Boolean.class) converted = NumberToBooleanConverter.INSTANCE.convert(millis);
            else if (Number.class.isAssignableFrom(target))
                converted = NumberToNumberConverterFactory.INSTANCE.getConverter((Class) target).convert(millis);
            else if (Temporal.class.isAssignableFrom(target))
                converted = LongToJSR310ConverterFactory.INSTANCE.getConverter((Class) target).convert(millis);
            else throw new ConverterNotFoundException(Long.class, requestedTarget);
            return (T) converted;
        }
    }
}
