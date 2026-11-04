package com.sunwayworld.framework.converter.string;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.ConverterFactory;
import com.sunwayworld.framework.utils.StringUtils;

public enum StringToEnumConverterFactory implements ConverterFactory<String, Enum> {
   INSTANCE;

   @Override
   public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
      return new StringToEnumConverterFactory.StringToEnumConverter<>(targetType);
   }

   private class StringToEnumConverter<T extends Enum> implements Converter<String, T> {
      private final Class<T> enumType;

      public StringToEnumConverter(Class<T> enumType) {
         this.enumType = enumType;
      }

      @Override
      public Class<String> getSourceType() {
         return String.class;
      }

      @Override
      public Class<T> getTargetType() {
         return this.enumType;
      }

      public T convert(String source) {
         return StringUtils.isEmpty(source) ? null : (T)(Enum)Enum.valueOf((Class)this.enumType, source.trim());
      }
   }
}
