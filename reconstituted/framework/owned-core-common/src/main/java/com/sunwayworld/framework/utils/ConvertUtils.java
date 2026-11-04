package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.ConverterFactory;
import com.sunwayworld.framework.converter.IdentityConverter;
import com.sunwayworld.framework.converter.array.PrimitiveByteArrayToBlobConverter;
import com.sunwayworld.framework.converter.array.PrimitiveByteArrayToStringConverter;
import com.sunwayworld.framework.converter.array.PrimitiveCharacterArrayToClobConverter;
import com.sunwayworld.framework.converter.array.PrimitiveToWrapperArrayConverter.PrimitiveToWrapperBooleanArrayConverter;
import com.sunwayworld.framework.converter.array.PrimitiveToWrapperArrayConverter.PrimitiveToWrapperByteArrayConverter;
import com.sunwayworld.framework.converter.array.PrimitiveToWrapperArrayConverter.PrimitiveToWrapperCharacterArrayConverter;
import com.sunwayworld.framework.converter.array.PrimitiveToWrapperArrayConverter.PrimitiveToWrapperDoubleArrayConverter;
import com.sunwayworld.framework.converter.array.PrimitiveToWrapperArrayConverter.PrimitiveToWrapperFloatArrayConverter;
import com.sunwayworld.framework.converter.array.PrimitiveToWrapperArrayConverter.PrimitiveToWrapperIntegerArrayConverter;
import com.sunwayworld.framework.converter.array.PrimitiveToWrapperArrayConverter.PrimitiveToWrapperLongArrayConverter;
import com.sunwayworld.framework.converter.array.PrimitiveToWrapperArrayConverter.PrimitiveToWrapperShortArrayConverter;
import com.sunwayworld.framework.converter.array.WrapperToPrimitiveArrayConverter.WrapperToPrimitiveBooleanArrayConverter;
import com.sunwayworld.framework.converter.array.WrapperToPrimitiveArrayConverter.WrapperToPrimitiveByteArrayConverter;
import com.sunwayworld.framework.converter.array.WrapperToPrimitiveArrayConverter.WrapperToPrimitiveCharacterArrayConverter;
import com.sunwayworld.framework.converter.array.WrapperToPrimitiveArrayConverter.WrapperToPrimitiveDoubleArrayConverter;
import com.sunwayworld.framework.converter.array.WrapperToPrimitiveArrayConverter.WrapperToPrimitiveFloatArrayConverter;
import com.sunwayworld.framework.converter.array.WrapperToPrimitiveArrayConverter.WrapperToPrimitiveIntegerArrayConverter;
import com.sunwayworld.framework.converter.array.WrapperToPrimitiveArrayConverter.WrapperToPrimitiveLongArrayConverter;
import com.sunwayworld.framework.converter.array.WrapperToPrimitiveArrayConverter.WrapperToPrimitiveShortArrayConverter;
import com.sunwayworld.framework.converter.bool.BooleanToNumberConverterFactory;
import com.sunwayworld.framework.converter.bool.BooleanToStringConverter;
import com.sunwayworld.framework.converter.collection.CollectionToStringConverter;
import com.sunwayworld.framework.converter.collection.MapToJSONObjectConverter;
import com.sunwayworld.framework.converter.collection.MapToStringConverter;
import com.sunwayworld.framework.converter.date.DateToJSR310ConverterFactory;
import com.sunwayworld.framework.converter.date.JSR310ToJSR310ConverterFactory;
import com.sunwayworld.framework.converter.date.LongToJSR310ConverterFactory;
import com.sunwayworld.framework.converter.date.OracleDatumToJSR310ConverterFactory;
import com.sunwayworld.framework.converter.date.DateToStringConverterFactory.DateToStringConverter;
import com.sunwayworld.framework.converter.date.DateToStringConverterFactory.TimeToStringConverter;
import com.sunwayworld.framework.converter.date.JSR310ToDateGenericConverter.LocalDateTimeToDateConverter;
import com.sunwayworld.framework.converter.date.JSR310ToDateGenericConverter.LocalDateTimeToSqlDateConverter;
import com.sunwayworld.framework.converter.date.JSR310ToDateGenericConverter.LocalDateTimeToSqlTimeConverter;
import com.sunwayworld.framework.converter.date.JSR310ToDateGenericConverter.LocalDateTimeToSqlTimestampConverter;
import com.sunwayworld.framework.converter.date.JSR310ToDateGenericConverter.LocalDateToDateConverter;
import com.sunwayworld.framework.converter.date.JSR310ToDateGenericConverter.LocalDateToSqlDateConverter;
import com.sunwayworld.framework.converter.date.JSR310ToDateGenericConverter.LocalDateToSqlTimeConverter;
import com.sunwayworld.framework.converter.date.JSR310ToDateGenericConverter.LocalDateToSqlTimestampConverter;
import com.sunwayworld.framework.converter.date.JSR310ToDateGenericConverter.LocalTimeToDateConverter;
import com.sunwayworld.framework.converter.date.JSR310ToDateGenericConverter.LocalTimeToSqlDateConverter;
import com.sunwayworld.framework.converter.date.JSR310ToDateGenericConverter.LocalTimeToSqlTimeConverter;
import com.sunwayworld.framework.converter.date.JSR310ToDateGenericConverter.LocalTimeToSqlTimestampConverter;
import com.sunwayworld.framework.converter.date.JSR310ToLongGenericConverter.LocalDateTimeToLongConverter;
import com.sunwayworld.framework.converter.date.JSR310ToLongGenericConverter.LocalDateToLongConverter;
import com.sunwayworld.framework.converter.date.JSR310ToLongGenericConverter.LocalTimeToLongConverter;
import com.sunwayworld.framework.converter.date.JSR310ToStringGenericConverter.LocalDateTimeToStringConverter;
import com.sunwayworld.framework.converter.date.JSR310ToStringGenericConverter.LocalDateToStringConverter;
import com.sunwayworld.framework.converter.date.JSR310ToStringGenericConverter.LocalTimeToStringConverter;
import com.sunwayworld.framework.converter.date.OracleDatumToStringConverter.OracleDateToStringConverter;
import com.sunwayworld.framework.converter.date.OracleDatumToStringConverter.OracleTimestampToStringConverter;
import com.sunwayworld.framework.converter.date.OracleDatumToStringConverter.OracleTimestampltzToStringConverter;
import com.sunwayworld.framework.converter.date.OracleDatumToStringConverter.OracleTimestamptzToStringConverter;
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

public class ConvertUtils {
   private static final List<Converter<?, ?>> CONVERTERS = new ArrayList<>();
   private static final List<ConverterFactory<?, ?>> CONVERTER_FACTORY = new ArrayList<>();
   private static Map<String, Converter<?, ?>> CONVERTER_CACHE = new ConcurrentHashMap<>();

   public static final <T, S> T convert(final S source, final Class<T> targetType) {
      return convert(source, targetType, (T)ClassUtils.getPrimitiveDefaultValue(targetType));
   }

   public static final <T, S> boolean canConvert(final S source, final Class<T> targetType) {
      if (source == null) {
         return true;
      } else {
         Class<S> sourceType = (Class<S>)source.getClass();
         Converter<S, T> converter = getConverterIfPresent(sourceType, targetType);
         if (converter == null) {
            return false;
         } else {
            try {
               converter.convert(source);
               return true;
            } catch (Exception var5) {
               return false;
            }
         }
      }
   }

   public static final <T, S> T convert(final S source, final Class<T> targetType, final T defaultValue) {
      if (source == null) {
         return defaultValue;
      } else {
         Class<S> sourceType = (Class<S>)source.getClass();
         Converter<S, T> converter = getConverterIfPresent(sourceType, targetType);
         if (converter != null) {
            T value = converter.convert(source);
            return value == null ? defaultValue : value;
         } else {
            throw new ConverterNotFoundException(sourceType, targetType);
         }
      }
   }

   private static final <T, S> Converter<S, T> getConverterIfPresent(Class<S> sourceType, Class<T> targetType) {
      Class<?> resolvedSourceType = ClassUtils.resolvePrimitiveIfNecessary(sourceType);
      Class<?> resolvedTargetType = ClassUtils.resolvePrimitiveIfNecessary(targetType);
      String cacheKey = resolvedSourceType.getName() + "-" + resolvedTargetType.getName();
      if (!CONVERTER_CACHE.containsKey(cacheKey)) {
         synchronized (CONVERTER_CACHE) {
            if (!CONVERTER_CACHE.containsKey(cacheKey)) {
               if (!Object.class.equals(resolvedTargetType) && !resolvedTargetType.isAssignableFrom(resolvedSourceType)) {
                  Converter<S, T> converter = getConverter((Class<S>)resolvedSourceType, (Class<T>)resolvedTargetType);
                  if (converter == null) {
                     ConverterFactory<S, T> converterFactory = getConverterFactory((Class<S>)resolvedSourceType, (Class<T>)resolvedTargetType);
                     if (converterFactory != null) {
                        converter = converterFactory.getConverter((Class<T>)resolvedTargetType);
                     }
                  }

                  if (converter != null) {
                     CONVERTER_CACHE.put(cacheKey, converter);
                  }
               } else {
                  CONVERTER_CACHE.put(cacheKey, new IdentityConverter());
               }
            }
         }
      }

      return (Converter<S, T>)CONVERTER_CACHE.get(cacheKey);
   }

   private static final <T, S> Converter<S, T> getConverter(Class<S> sourceType, Class<T> targetType) {
      return (Converter<S, T>)CONVERTERS.stream().filter(c -> c.canConvert(sourceType, targetType)).findFirst().orElse(null);
   }

   private static final <T, S> ConverterFactory<S, T> getConverterFactory(Class<S> sourceType, Class<T> targetType) {
      return (ConverterFactory<S, T>)CONVERTER_FACTORY.stream().filter(f -> f.canConvert(sourceType, targetType)).findFirst().orElse(null);
   }

   static {
      CONVERTERS.add(NumberToStringConverter.INSTANCE);
      CONVERTERS.add(TimeToStringConverter.INSTANCE);
      CONVERTERS.add(DateToStringConverter.INSTANCE);
      CONVERTERS.add(LocalTimeToStringConverter.INSTANCE);
      CONVERTERS.add(LocalDateToStringConverter.INSTANCE);
      CONVERTERS.add(LocalDateTimeToStringConverter.INSTANCE);
      CONVERTERS.add(StringToBlobConverter.INSTANCE);
      CONVERTERS.add(BlobToStringConverter.INSTANCE);
      CONVERTERS.add(StringToClobConverter.INSTANCE);
      CONVERTERS.add(ClobToStringConverter.INSTANCE);
      CONVERTERS.add(StringToPropertiesConverter.INSTANCE);
      CONVERTERS.add(PropertiesToStringConverter.INSTANCE);
      CONVERTERS.add(LocalDateTimeToSqlDateConverter.INSTANCE);
      CONVERTERS.add(LocalDateTimeToSqlTimeConverter.INSTANCE);
      CONVERTERS.add(LocalDateTimeToSqlTimestampConverter.INSTANCE);
      CONVERTERS.add(LocalDateTimeToDateConverter.INSTANCE);
      CONVERTERS.add(LocalDateToSqlDateConverter.INSTANCE);
      CONVERTERS.add(LocalDateToSqlTimeConverter.INSTANCE);
      CONVERTERS.add(LocalDateToSqlTimestampConverter.INSTANCE);
      CONVERTERS.add(LocalDateToDateConverter.INSTANCE);
      CONVERTERS.add(LocalTimeToSqlDateConverter.INSTANCE);
      CONVERTERS.add(LocalTimeToSqlTimeConverter.INSTANCE);
      CONVERTERS.add(LocalTimeToSqlTimestampConverter.INSTANCE);
      CONVERTERS.add(LocalTimeToDateConverter.INSTANCE);
      CONVERTERS.add(LocalDateTimeToLongConverter.INSTANCE);
      CONVERTERS.add(LocalDateToLongConverter.INSTANCE);
      CONVERTERS.add(LocalTimeToLongConverter.INSTANCE);
      CONVERTERS.add(StringToBooleanConverter.INSTANCE);
      CONVERTERS.add(BooleanToStringConverter.INSTANCE);
      CONVERTERS.add(NumberToBooleanConverter.INSTANCE);
      CONVERTERS.add(PrimitiveToWrapperBooleanArrayConverter.INSTANCE);
      CONVERTERS.add(WrapperToPrimitiveBooleanArrayConverter.INSTANCE);
      CONVERTERS.add(PrimitiveToWrapperByteArrayConverter.INSTANCE);
      CONVERTERS.add(WrapperToPrimitiveByteArrayConverter.INSTANCE);
      CONVERTERS.add(PrimitiveToWrapperCharacterArrayConverter.INSTANCE);
      CONVERTERS.add(WrapperToPrimitiveCharacterArrayConverter.INSTANCE);
      CONVERTERS.add(PrimitiveToWrapperDoubleArrayConverter.INSTANCE);
      CONVERTERS.add(WrapperToPrimitiveDoubleArrayConverter.INSTANCE);
      CONVERTERS.add(PrimitiveToWrapperFloatArrayConverter.INSTANCE);
      CONVERTERS.add(WrapperToPrimitiveFloatArrayConverter.INSTANCE);
      CONVERTERS.add(PrimitiveToWrapperIntegerArrayConverter.INSTANCE);
      CONVERTERS.add(WrapperToPrimitiveIntegerArrayConverter.INSTANCE);
      CONVERTERS.add(PrimitiveToWrapperLongArrayConverter.INSTANCE);
      CONVERTERS.add(WrapperToPrimitiveLongArrayConverter.INSTANCE);
      CONVERTERS.add(PrimitiveToWrapperShortArrayConverter.INSTANCE);
      CONVERTERS.add(WrapperToPrimitiveShortArrayConverter.INSTANCE);
      CONVERTERS.add(ReaderToStringConverter.INSTANCE);
      CONVERTERS.add(BlobToPrimitiveByteArrayConverter.INSTANCE);
      CONVERTERS.add(PrimitiveByteArrayToBlobConverter.INSTANCE);
      CONVERTERS.add(PrimitiveByteArrayToStringConverter.INSTANCE);
      CONVERTERS.add(ClobToPrimitiveCharacterArrayConverter.INSTANCE);
      CONVERTERS.add(PrimitiveCharacterArrayToClobConverter.INSTANCE);
      CONVERTERS.add(CollectionToStringConverter.INSTANCE);
      CONVERTERS.add(MapToStringConverter.INSTANCE);
      CONVERTERS.add(JSONObjectToMapConverter.INSTANCE);
      CONVERTERS.add(MapToJSONObjectConverter.INSTANCE);
      CONVERTERS.add(OracleDateToStringConverter.INSTANCE);
      CONVERTERS.add(OracleTimestampToStringConverter.INSTANCE);
      CONVERTERS.add(OracleTimestamptzToStringConverter.INSTANCE);
      CONVERTERS.add(OracleTimestampltzToStringConverter.INSTANCE);
      CONVERTER_FACTORY.add(StringToDateConverterFactory.INSTANCE);
      CONVERTER_FACTORY.add(StringToEnumConverterFactory.INSTANCE);
      CONVERTER_FACTORY.add(StringToJSR310ConverterFactory.INSTANCE);
      CONVERTER_FACTORY.add(StringToReaderConverterFactory.INSTANCE);
      CONVERTER_FACTORY.add(StringToNumberConverterFactory.INSTANCE);
      CONVERTER_FACTORY.add(NumberToNumberConverterFactory.INSTANCE);
      CONVERTER_FACTORY.add(BooleanToNumberConverterFactory.INSTANCE);
      CONVERTER_FACTORY.add(DateToJSR310ConverterFactory.INSTANCE);
      CONVERTER_FACTORY.add(LongToJSR310ConverterFactory.INSTANCE);
      CONVERTER_FACTORY.add(StringToInputStreamConverterFactory.INSTANCE);
      CONVERTER_FACTORY.add(JSR310ToJSR310ConverterFactory.INSTANCE);
      CONVERTER_FACTORY.add(OracleDatumToJSR310ConverterFactory.INSTANCE);
   }
}
