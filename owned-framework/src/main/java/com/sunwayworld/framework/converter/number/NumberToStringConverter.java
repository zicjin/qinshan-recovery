package com.sunwayworld.framework.converter.number;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

public enum NumberToStringConverter implements Converter<Number, String> {
    INSTANCE;
    private static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat("#.##########");
    private static final DecimalFormat INTEGER_FORMAT = new DecimalFormat("#");
    private static final Pattern DOUBLE_INCORRECT_PRECISION = Pattern.compile("\\.[0-9]+9{7}[0-9]?$");

    public String convert(Number source) {
        if (source == null) return "";
        Class<?> type = source.getClass();
        Number corrected = getCorrectedNumber(source, type);
        if (type == Float.class) {
            if (((Float) corrected).isNaN()) return "";
            return DOUBLE_FORMAT.format(Double.valueOf(Float.toString(((Float) corrected).floatValue())));
        }
        if (type == Double.class) {
            if (((Double) corrected).isNaN()) return "";
            return DOUBLE_FORMAT.format(corrected);
        }
        if (type == BigDecimal.class) return DOUBLE_FORMAT.format(((BigDecimal) corrected).doubleValue());
        if (type == Byte.class || type == Short.class || type == Integer.class || type == Long.class || type == BigInteger.class) return INTEGER_FORMAT.format(corrected);
        throw new ApplicationRuntimeException("Unable format number (" + corrected + ").");
    }

    private static Number getCorrectedNumber(Number value, Class<?> type) {
        if (type != Double.class && type != Float.class) return value;
        String adjusted = accurate(accurate(String.valueOf(value)));
        if (type == Float.class) return Float.valueOf(adjusted);
        return Double.valueOf(adjusted);
    }

    private static String accurate(String value) {
        String text = value.replace(",", "").trim();
        if (!DOUBLE_INCORRECT_PRECISION.matcher(text).find()) return text;
        int zeros = text.substring(text.indexOf('.')).length() - 3;
        StringBuilder increment = new StringBuilder("0.");
        for (int i = 0; i < zeros; i++) increment.append('0');
        increment.append('1');
        double corrected = new BigDecimal(text).doubleValue() + Double.valueOf(increment.toString());
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(6);
        format.setGroupingUsed(false);
        return format.format(corrected);
    }
}
