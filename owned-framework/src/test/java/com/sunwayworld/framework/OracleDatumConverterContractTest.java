package com.sunwayworld.framework;
import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.date.OracleDatumToJSR310ConverterFactory;
import com.sunwayworld.framework.converter.date.OracleDatumToStringConverter;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import oracle.sql.DATE;
import oracle.sql.Datum;
import oracle.sql.TIMESTAMP;
public final class OracleDatumConverterContractTest {
 public static void main(String[] args) throws Exception {
  Timestamp stamp=Timestamp.valueOf("2024-02-29 23:45:12");
  Datum[] values={new DATE(stamp),new TIMESTAMP(stamp)};
  for(Datum value:values) {
   Converter<Datum,LocalDateTime> c=OracleDatumToJSR310ConverterFactory.INSTANCE.getConverter(LocalDateTime.class);
   if(c.convert(value)==null) throw new AssertionError("null conversion");
  }
  if(!"2024-02-29 23:45:12".equals(OracleDatumToStringConverter.OracleDateToStringConverter.INSTANCE.convert(new DATE(stamp)))) throw new AssertionError("date format");
  if(OracleDatumToJSR310ConverterFactory.INSTANCE.getConverter(LocalDate.class).getSourceType()!=Datum.class) throw new AssertionError("source type");
  System.out.println("Oracle datum owned contract: PASS (DATE/TIMESTAMP synthetic values; TZ requires real DataSource bridge)");
 }
}
