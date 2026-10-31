package com.sunwayworld.framework.converter.string;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.ConverterFactory;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import com.sunwayworld.framework.converter.exception.ConverterNotFoundException;
import com.sunwayworld.owned.internal.converter.DateTimeState;
import java.lang.reflect.Constructor;
import java.util.Date;

public enum StringToDateConverterFactory implements ConverterFactory<String, Date> {
    INSTANCE;

    public <T extends Date> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToDateConverter<T>(targetType);
    }

    class StringToDateConverter<T extends Date> implements Converter<String, T> {
        private final Class<T> dateType;
        public StringToDateConverter(Class<T> dateType) { this.dateType = dateType; }
        public Class<String> getSourceType() { return String.class; }
        public Class<T> getTargetType() { return dateType; }
        public T convert(String source) {
            Date parsed = DateTimeState.parseDate(source);
            if (parsed == null) return null;
            try {
                Constructor<T> constructor = dateType.getConstructor(Long.class);
                if (constructor == null) throw new ConverterNotFoundException(getSourceType(), getTargetType());
                return constructor.newInstance(Long.valueOf(parsed.getTime()));
            } catch (NoSuchMethodException e) { throw new ConverterNotFoundException(getSourceType(), getTargetType());
            } catch (InstantiationException e) { throw new ConvertFailedException(getSourceType(), getTargetType(), e);
            } catch (IllegalAccessException e) { throw new ConvertFailedException(getSourceType(), getTargetType(), e);
            } catch (IllegalArgumentException e) { throw new ConvertFailedException(getSourceType(), getTargetType(), e);
            } catch (java.lang.reflect.InvocationTargetException e) { throw new ConvertFailedException(getSourceType(), getTargetType(), e); }
        }
    }

}
