/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.es.config;

import com.sunwayworld.framework.converter.date.DateToJSR310ConverterFactory;
import com.sunwayworld.framework.utils.DateTimeUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.elasticsearch.config.ElasticsearchConfigurationSupport;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;

@Configuration
@ConditionalOnProperty(prefix="spring.elasticsearch.rest", name={"enabled"}, havingValue="true")
public class ElasticSearchAutoConfiguration
extends ElasticsearchConfigurationSupport {
    @Override
    @Bean
    public ElasticsearchCustomConversions elasticsearchCustomConversions() {
        ArrayList<Enum> converterList = new ArrayList<Enum>();
        converterList.add(DateToLocalDateTimeConverter.INSTANCE);
        converterList.add(StringToLocalDateTimeConverter.INSTANCE);
        converterList.add(DateToLocalDateConverter.INSTANCE);
        converterList.add(StringToLocalDateConverter.INSTANCE);
        return new ElasticsearchCustomConversions(converterList);
    }

    @WritingConverter
    private static enum DateToLocalDateConverter implements Converter<Date, LocalDate>
    {
        INSTANCE;


        @Override
        public LocalDate convert(Date date) {
            return DateToJSR310ConverterFactory.INSTANCE.getConverter(LocalDate.class).convert(date);
        }
    }

    @ReadingConverter
    private static enum StringToLocalDateConverter implements Converter<String, LocalDate>
    {
        INSTANCE;


        @Override
        public LocalDate convert(String source) {
            return DateTimeUtils.parseLocalDate(source);
        }
    }

    @WritingConverter
    private static enum DateToLocalDateTimeConverter implements Converter<Date, LocalDateTime>
    {
        INSTANCE;


        @Override
        public LocalDateTime convert(Date date) {
            return DateToJSR310ConverterFactory.INSTANCE.getConverter(LocalDateTime.class).convert(date);
        }
    }

    @ReadingConverter
    private static enum StringToLocalDateTimeConverter implements Converter<String, LocalDateTime>
    {
        INSTANCE;


        @Override
        public LocalDateTime convert(String source) {
            return DateTimeUtils.parseLocalDateTime(source);
        }
    }
}
