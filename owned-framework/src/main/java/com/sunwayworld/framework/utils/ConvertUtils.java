package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.ConverterFactory;
import com.sunwayworld.framework.converter.IdentityConverter;
import com.sunwayworld.framework.converter.array.PrimitiveByteArrayToBlobConverter;
import com.sunwayworld.framework.converter.array.PrimitiveByteArrayToStringConverter;
import com.sunwayworld.framework.converter.array.PrimitiveCharacterArrayToClobConverter;
import com.sunwayworld.framework.converter.array.PrimitiveToWrapperArrayConverter;
import com.sunwayworld.framework.converter.array.WrapperToPrimitiveArrayConverter;
import com.sunwayworld.framework.converter.bool.BooleanToNumberConverterFactory;
import com.sunwayworld.framework.converter.bool.BooleanToStringConverter;
import com.sunwayworld.framework.converter.collection.CollectionToStringConverter;
import com.sunwayworld.framework.converter.collection.MapToJSONObjectConverter;
import com.sunwayworld.framework.converter.collection.MapToStringConverter;
import com.sunwayworld.framework.converter.date.DateToJSR310ConverterFactory;
import com.sunwayworld.framework.converter.date.DateToStringConverterFactory;
import com.sunwayworld.framework.converter.date.JSR310ToDateGenericConverter;
import com.sunwayworld.framework.converter.date.JSR310ToJSR310ConverterFactory;
import com.sunwayworld.framework.converter.date.JSR310ToLongGenericConverter;
import com.sunwayworld.framework.converter.date.JSR310ToStringGenericConverter;
import com.sunwayworld.framework.converter.date.LongToJSR310ConverterFactory;
import com.sunwayworld.framework.converter.date.OracleDatumToJSR310ConverterFactory;
import com.sunwayworld.framework.converter.date.OracleDatumToStringConverter;
import com.sunwayworld.framework.converter.exception.ConverterNotFoundException;
import com.sunwayworld.framework.converter.json.JSONObjectToMapConverter;
import com.sunwayworld.framework.converter.number.NumberToBooleanConverter;
import com.sunwayworld.framework.converter.number.NumberToNumberConverterFactory;
import com.sunwayworld.framework.converter.number.NumberToStringConverter;
import com.sunwayworld.framework.converter.properties.PropertiesToStringConverter;
import com.sunwayworld.framework.converter.reader.ReaderToStringConverter;
import com.sunwayworld.framework.converter.sql.BlobToPrimitiveByteArrayConverter;
import com.sunwayworld.framework.converter.sql.BlobToStringConverter;
import com.sunwayworld.framework.converter.sql.ClobToPrimitiveCharacterArrayConverter;
import com.sunwayworld.framework.converter.sql.ClobToStringConverter;
import com.sunwayworld.framework.converter.string.StringToBlobConverter;
import com.sunwayworld.framework.converter.string.StringToBooleanConverter;
import com.sunwayworld.framework.converter.string.StringToClobConverter;
import com.sunwayworld.framework.converter.string.StringToDateConverterFactory;
import com.sunwayworld.framework.converter.string.StringToEnumConverterFactory;
import com.sunwayworld.framework.converter.string.StringToInputStreamConverterFactory;
import com.sunwayworld.framework.converter.string.StringToJSR310ConverterFactory;
import com.sunwayworld.framework.converter.string.StringToNumberConverterFactory;
import com.sunwayworld.framework.converter.string.StringToPropertiesConverter;
import com.sunwayworld.framework.converter.string.StringToReaderConverterFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Project-owned converter registry preserving the supplier's public behavior. */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ConvertUtils {
    private static final List<Converter<?, ?>> CONVERTERS = new ArrayList<Converter<?, ?>>();
    private static final List<ConverterFactory<?, ?>> CONVERTER_FACTORY = new ArrayList<ConverterFactory<?, ?>>();
    private static final Map<String, Converter<?, ?>> CONVERTER_CACHE = new ConcurrentHashMap<String, Converter<?, ?>>();

    static {
        add(NumberToStringConverter.INSTANCE, DateToStringConverterFactory.TimeToStringConverter.INSTANCE,
                DateToStringConverterFactory.DateToStringConverter.INSTANCE,
                JSR310ToStringGenericConverter.LocalTimeToStringConverter.INSTANCE,
                JSR310ToStringGenericConverter.LocalDateToStringConverter.INSTANCE,
                JSR310ToStringGenericConverter.LocalDateTimeToStringConverter.INSTANCE,
                StringToBlobConverter.INSTANCE, BlobToStringConverter.INSTANCE,
                StringToClobConverter.INSTANCE, ClobToStringConverter.INSTANCE,
                StringToPropertiesConverter.INSTANCE, PropertiesToStringConverter.INSTANCE,
                JSR310ToDateGenericConverter.LocalDateTimeToSqlDateConverter.INSTANCE,
                JSR310ToDateGenericConverter.LocalDateTimeToSqlTimeConverter.INSTANCE,
                JSR310ToDateGenericConverter.LocalDateTimeToSqlTimestampConverter.INSTANCE,
                JSR310ToDateGenericConverter.LocalDateTimeToDateConverter.INSTANCE,
                JSR310ToDateGenericConverter.LocalDateToSqlDateConverter.INSTANCE,
                JSR310ToDateGenericConverter.LocalDateToSqlTimeConverter.INSTANCE,
                JSR310ToDateGenericConverter.LocalDateToSqlTimestampConverter.INSTANCE,
                JSR310ToDateGenericConverter.LocalDateToDateConverter.INSTANCE,
                JSR310ToDateGenericConverter.LocalTimeToSqlDateConverter.INSTANCE,
                JSR310ToDateGenericConverter.LocalTimeToSqlTimeConverter.INSTANCE,
                JSR310ToDateGenericConverter.LocalTimeToSqlTimestampConverter.INSTANCE,
                JSR310ToDateGenericConverter.LocalTimeToDateConverter.INSTANCE,
                JSR310ToLongGenericConverter.LocalDateTimeToLongConverter.INSTANCE,
                JSR310ToLongGenericConverter.LocalDateToLongConverter.INSTANCE,
                JSR310ToLongGenericConverter.LocalTimeToLongConverter.INSTANCE,
                StringToBooleanConverter.INSTANCE, BooleanToStringConverter.INSTANCE,
                NumberToBooleanConverter.INSTANCE,
                PrimitiveToWrapperArrayConverter.PrimitiveToWrapperBooleanArrayConverter.INSTANCE,
                WrapperToPrimitiveArrayConverter.WrapperToPrimitiveBooleanArrayConverter.INSTANCE,
                PrimitiveToWrapperArrayConverter.PrimitiveToWrapperByteArrayConverter.INSTANCE,
                WrapperToPrimitiveArrayConverter.WrapperToPrimitiveByteArrayConverter.INSTANCE,
                PrimitiveToWrapperArrayConverter.PrimitiveToWrapperCharacterArrayConverter.INSTANCE,
                WrapperToPrimitiveArrayConverter.WrapperToPrimitiveCharacterArrayConverter.INSTANCE,
                PrimitiveToWrapperArrayConverter.PrimitiveToWrapperDoubleArrayConverter.INSTANCE,
                WrapperToPrimitiveArrayConverter.WrapperToPrimitiveDoubleArrayConverter.INSTANCE,
                PrimitiveToWrapperArrayConverter.PrimitiveToWrapperFloatArrayConverter.INSTANCE,
                WrapperToPrimitiveArrayConverter.WrapperToPrimitiveFloatArrayConverter.INSTANCE,
                PrimitiveToWrapperArrayConverter.PrimitiveToWrapperIntegerArrayConverter.INSTANCE,
                WrapperToPrimitiveArrayConverter.WrapperToPrimitiveIntegerArrayConverter.INSTANCE,
                PrimitiveToWrapperArrayConverter.PrimitiveToWrapperLongArrayConverter.INSTANCE,
                WrapperToPrimitiveArrayConverter.WrapperToPrimitiveLongArrayConverter.INSTANCE,
                PrimitiveToWrapperArrayConverter.PrimitiveToWrapperShortArrayConverter.INSTANCE,
                WrapperToPrimitiveArrayConverter.WrapperToPrimitiveShortArrayConverter.INSTANCE,
                ReaderToStringConverter.INSTANCE, BlobToPrimitiveByteArrayConverter.INSTANCE,
                PrimitiveByteArrayToBlobConverter.INSTANCE, PrimitiveByteArrayToStringConverter.INSTANCE,
                ClobToPrimitiveCharacterArrayConverter.INSTANCE, PrimitiveCharacterArrayToClobConverter.INSTANCE,
                CollectionToStringConverter.INSTANCE, MapToStringConverter.INSTANCE,
                JSONObjectToMapConverter.INSTANCE, MapToJSONObjectConverter.INSTANCE,
                OracleDatumToStringConverter.OracleDateToStringConverter.INSTANCE,
                OracleDatumToStringConverter.OracleTimestampToStringConverter.INSTANCE,
                OracleDatumToStringConverter.OracleTimestamptzToStringConverter.INSTANCE,
                OracleDatumToStringConverter.OracleTimestampltzToStringConverter.INSTANCE);
        addFactory(StringToDateConverterFactory.INSTANCE, StringToEnumConverterFactory.INSTANCE,
                StringToJSR310ConverterFactory.INSTANCE, StringToReaderConverterFactory.INSTANCE,
                StringToNumberConverterFactory.INSTANCE, NumberToNumberConverterFactory.INSTANCE,
                BooleanToNumberConverterFactory.INSTANCE, DateToJSR310ConverterFactory.INSTANCE,
                LongToJSR310ConverterFactory.INSTANCE, StringToInputStreamConverterFactory.INSTANCE,
                JSR310ToJSR310ConverterFactory.INSTANCE, OracleDatumToJSR310ConverterFactory.INSTANCE);
    }

    public ConvertUtils() { }
    private static void add(Converter<?, ?>... values) { for (Converter<?, ?> value : values) CONVERTERS.add(value); }
    private static void addFactory(ConverterFactory<?, ?>... values) { for (ConverterFactory<?, ?> value : values) CONVERTER_FACTORY.add(value); }

    public static final <T, S> T convert(S source, Class<T> targetType) {
        return convert(source, targetType, (T) ClassUtils.getPrimitiveDefaultValue(targetType));
    }

    public static final <T, S> boolean canConvert(S source, Class<T> targetType) {
        if (source == null) return true;
        Class<S> sourceType = (Class<S>) source.getClass();
        Converter<S, T> converter = getConverterIfPresent(sourceType, targetType);
        if (converter == null) return false;
        try { converter.convert(source); return true; } catch (Exception ignored) { return false; }
    }

    public static final <T, S> T convert(S source, Class<T> targetType, T defaultValue) {
        if (source == null) return defaultValue;
        Class<S> sourceType = (Class<S>) source.getClass();
        Converter<S, T> converter = getConverterIfPresent(sourceType, targetType);
        if (converter == null) throw new ConverterNotFoundException(sourceType, targetType);
        T result = converter.convert(source);
        return result == null ? defaultValue : result;
    }

    private static <T, S> Converter<S, T> getConverterIfPresent(Class<S> sourceType, Class<T> targetType) {
        Class<?> source = ClassUtils.resolvePrimitiveIfNecessary(sourceType);
        Class<?> target = ClassUtils.resolvePrimitiveIfNecessary(targetType);
        String key = source.getName() + "-" + target.getName();
        if (!CONVERTER_CACHE.containsKey(key)) {
            synchronized (CONVERTER_CACHE) {
                if (!CONVERTER_CACHE.containsKey(key)) {
                    Converter<?, ?> converter;
                    if (target.equals(Object.class) || target.isAssignableFrom(source)) converter = new IdentityConverter<Object, Object>();
                    else {
                        converter = getConverter((Class) source, (Class) target);
                        if (converter == null) {
                            ConverterFactory factory = getConverterFactory((Class) source, (Class) target);
                            if (factory != null) converter = factory.getConverter((Class) target);
                        }
                    }
                    if (converter != null) CONVERTER_CACHE.put(key, converter);
                }
            }
        }
        return (Converter<S, T>) CONVERTER_CACHE.get(key);
    }
    private static <S, T> Converter<S, T> getConverter(Class<S> sourceType, Class<T> targetType) {
        for (Converter<?, ?> converter : CONVERTERS) if (converter.canConvert(sourceType, targetType)) return (Converter<S, T>) converter;
        return null;
    }
    private static <S, T> ConverterFactory<S, T> getConverterFactory(Class<S> sourceType, Class<T> targetType) {
        for (ConverterFactory<?, ?> factory : CONVERTER_FACTORY) if (factory.canConvert(sourceType, targetType)) return (ConverterFactory<S, T>) factory;
        return null;
    }
}
