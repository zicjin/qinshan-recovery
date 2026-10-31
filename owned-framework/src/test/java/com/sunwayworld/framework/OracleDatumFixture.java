package com.sunwayworld.framework;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.date.OracleDatumToJSR310ConverterFactory;
import com.sunwayworld.framework.converter.date.OracleDatumToStringConverter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import oracle.sql.*;

public final class OracleDatumFixture {
    public static final class GenericDatum extends Datum {
        private final int kind;
        public GenericDatum(int kind) { this.kind=kind; }
        public java.sql.Date dateValue() throws SQLException {
            if (kind==1) throw new SQLException("synthetic-date-failure","22007",777);
            if (kind==2) throw new IllegalArgumentException("synthetic-unchecked-failure");
            if (kind==3) return null;
            return new java.sql.Date(123456789L);
        }
        public boolean isConvertibleTo(Class type) { return false; }
        public Object toJdbc() { return null; }
        public Object makeJdbcArray(int size) { return null; }
    }
    public static final class GenericStringConverter implements OracleDatumToStringConverter<GenericDatum> {}
    private interface Observation { Object run() throws Throwable; }
    private static String describe(Throwable failure) {
        String result = failure.getClass().getName();
        // Java 8 NPE/cast text is stable but omitted for VM-generated failures.
        if (!(failure instanceof NullPointerException) && !(failure instanceof ClassCastException)) result += ":"+failure.getMessage();
        if (failure.getCause()!=null) result += ":cause="+describe(failure.getCause());
        if (failure instanceof SQLException) result += ":sql="+((SQLException)failure).getSQLState()+":code="+((SQLException)failure).getErrorCode();
        return result;
    }
    private static void add(List<String> list,String label,Observation action) {
        try { Object value=action.run(); list.add(label+"="+(value==null?"null":value.getClass().getName()+":"+value)); }
        catch(Throwable failure) { list.add(label+"=!"+describe(failure)); }
    }
    @SuppressWarnings({"rawtypes","unchecked"})
    public static List<String> observe() {
        List<String> result = new ArrayList<String>();
        final Object[] inputs={null,new DATE(Timestamp.valueOf("2024-02-29 23:45:12")),new TIMESTAMP(Timestamp.valueOf("2024-02-29 23:45:12.987654321")),new DATE(new byte[0]),new TIMESTAMP(new byte[0]),new GenericDatum(0),new GenericDatum(1),new GenericDatum(2),new GenericDatum(3),"wrong"};
        Class<?>[] targets={LocalDate.class,LocalDateTime.class,LocalTime.class,Temporal.class,Instant.class,Object.class,String.class,long.class,null};
        for(final Class target:targets) {
            final Converter converter=OracleDatumToJSR310ConverterFactory.INSTANCE.getConverter(target);
            add(result,"source",()->converter.getSourceType()); add(result,"target",()->converter.getTargetType());
            add(result,"fresh",()->converter!=OracleDatumToJSR310ConverterFactory.INSTANCE.getConverter(target));
            for(final Object input:inputs) add(result,"convert",()->converter.convert(input));
            for(final Class source:new Class[]{null,Datum.class,DATE.class,TIMESTAMP.class,Object.class}) add(result,"can",()->converter.canConvert(source,target));
        }
        for(final Converter converter:new Converter[]{OracleDatumToStringConverter.OracleDateToStringConverter.INSTANCE,OracleDatumToStringConverter.OracleTimestampToStringConverter.INSTANCE,new GenericStringConverter()}) {
            add(result,"string-source",()->converter.getSourceType()); add(result,"string-target",()->converter.getTargetType());
            for(final Object input:inputs) add(result,"string",()->converter.convert(input));
        }
        return result;
    }
}
