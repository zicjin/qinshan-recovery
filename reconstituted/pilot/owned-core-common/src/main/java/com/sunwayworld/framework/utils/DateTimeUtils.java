/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.Date;

public class DateTimeUtils {
    private static final String DEFAULT_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final ThreadLocal<DateFormat> TL_DATETIME_FORMATTER = DateTimeUtils.getThreadLocalDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final ThreadLocal<DateFormat> TL_DATE_FORMATTER = DateTimeUtils.getThreadLocalDateFormat("yyyy-MM-dd");
    private static final ThreadLocal<DateFormat> TL_TIME_FORMATTER = DateTimeUtils.getThreadLocalDateFormat("HH:mm:ss");
    private static final ThreadLocal<DateTimeFormatter> TL_LOCAL_DATETIME_FORMATTER = DateTimeUtils.getThreadLocalDateTimeFormatter("yyyy-MM-dd HH:mm:ss");
    private static final ThreadLocal<DateTimeFormatter> TL_LOCAL_DATE_FORMATTER = DateTimeUtils.getThreadLocalDateTimeFormatter("yyyy-MM-dd");
    private static final ThreadLocal<DateTimeFormatter> TL_LOCAL_TIME_FORMATTER = DateTimeUtils.getThreadLocalDateTimeFormatter("HH:mm:ss");
    private static final ThreadLocal<DateTimeFormatter> TL_LOCAL_HOUR_MINUTE_FORMATTER = DateTimeUtils.getThreadLocalDateTimeFormatter("HH:mm");
    private static final ThreadLocal<DateTimeFormatter> TL_DATE_SERIAL_NUMBER_FORMATTER = DateTimeUtils.getThreadLocalDateTimeFormatter("yyyyMMdd");
    private static final ThreadLocal<DateTimeFormatter> TL_DATETIME_SERIAL_NUMBER_FORMATTER = DateTimeUtils.getThreadLocalDateTimeFormatter("yyyyMMddHHmmss");

    public static final String getDateTimeSerialNumber(LocalDateTime dateTime) {
        return dateTime.format(TL_DATETIME_SERIAL_NUMBER_FORMATTER.get());
    }

    public static final String getDateSerialNumber(LocalDate date) {
        return date.format(TL_DATE_SERIAL_NUMBER_FORMATTER.get());
    }

    public static final String getDateSerialNumber(LocalDateTime dateTime) {
        return dateTime.format(TL_DATE_SERIAL_NUMBER_FORMATTER.get());
    }

    public static final String formatLocalDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        if (StringUtils.isEmpty(pattern)) {
            return DateTimeUtils.formatLocalDateTime(dateTime);
        }
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static final String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(TL_LOCAL_DATETIME_FORMATTER.get());
    }

    public static final String formatDateTime(Date dateTime) {
        if (dateTime == null) {
            return null;
        }
        return TL_DATETIME_FORMATTER.get().format(dateTime);
    }

    public static final String formatTime(Date Time2) {
        if (Time2 == null) {
            return null;
        }
        return TL_TIME_FORMATTER.get().format(Time2);
    }

    public static final String formatLocalDate(LocalDate date, String pattern) {
        if (date == null) {
            return null;
        }
        if (StringUtils.isEmpty(pattern)) {
            return DateTimeUtils.formatLocalDate(date);
        }
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static final String formatLocalDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(TL_LOCAL_DATE_FORMATTER.get());
    }

    @Deprecated
    public static final String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return TL_DATE_FORMATTER.get().format(date);
    }

    public static final String formatLocalTime(LocalTime time) {
        if (time == null) {
            return null;
        }
        return TL_LOCAL_TIME_FORMATTER.get().format(time);
    }

    @Deprecated
    public static final Date parseDate(String source) {
        if (StringUtils.isEmpty(source)) {
            return null;
        }
        String tempSource = DateTimeUtils.getStandardDateText(source);
        try {
            return TL_DATETIME_FORMATTER.get().parse(tempSource);
        }
        catch (ParseException parseException) {
            if (StringUtils.contains(tempSource, ".")) {
                try {
                    int qty = tempSource.length() - (tempSource.lastIndexOf(".") + 1);
                    if (qty > 0) {
                        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss." + StringUtils.repeat("S", qty)).parse(tempSource);
                    }
                }
                catch (ParseException parseException2) {
                    // empty catch block
                }
            }
            try {
                return TL_DATE_FORMATTER.get().parse(tempSource);
            }
            catch (ParseException parseException3) {
                throw new ConvertFailedException(String.class, Date.class, source);
            }
        }
    }

    public static final LocalDate parseLocalDate(String source) {
        if (StringUtils.isEmpty(source)) {
            return null;
        }
        String tempSource = DateTimeUtils.getStandardDateText(source);
        try {
            return LocalDate.parse(tempSource, TL_LOCAL_DATE_FORMATTER.get());
        }
        catch (DateTimeParseException dateTimeParseException) {
            try {
                return LocalDateTime.parse(tempSource, TL_LOCAL_DATETIME_FORMATTER.get()).toLocalDate();
            }
            catch (DateTimeParseException dateTimeParseException2) {
                if (StringUtils.contains(tempSource, ".")) {
                    try {
                        int qty = tempSource.length() - (tempSource.lastIndexOf(".") + 1);
                        if (qty > 0) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss." + StringUtils.repeat("S", qty));
                            return LocalDateTime.parse(tempSource, formatter).toLocalDate();
                        }
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                try {
                    return LocalDate.parse(tempSource, TL_DATE_SERIAL_NUMBER_FORMATTER.get());
                }
                catch (DateTimeParseException dateTimeParseException3) {
                    throw new ConvertFailedException(String.class, LocalDate.class, source);
                }
            }
        }
    }

    public static final LocalDateTime parseLocalDateTime(String source) {
        if (StringUtils.isEmpty(source)) {
            return null;
        }
        String tempSource = DateTimeUtils.getStandardDateText(source);
        try {
            return LocalDateTime.parse(tempSource, TL_LOCAL_DATETIME_FORMATTER.get());
        }
        catch (DateTimeParseException dateTimeParseException) {
            try {
                return LocalDate.parse(tempSource, TL_LOCAL_DATE_FORMATTER.get()).atStartOfDay();
            }
            catch (DateTimeParseException dateTimeParseException2) {
                if (StringUtils.contains(tempSource, ".")) {
                    try {
                        int qty = tempSource.length() - (tempSource.lastIndexOf(".") + 1);
                        if (qty > 0) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss." + StringUtils.repeat("S", qty));
                            return LocalDateTime.parse(tempSource, formatter);
                        }
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                throw new ConvertFailedException(String.class, LocalDateTime.class, source);
            }
        }
    }

    public static final LocalTime parseLocalTime(String source) {
        if (StringUtils.isEmpty(source)) {
            return null;
        }
        try {
            return LocalTime.parse(source, TL_LOCAL_TIME_FORMATTER.get());
        }
        catch (DateTimeParseException dateTimeParseException) {
            try {
                return LocalTime.parse(source, TL_LOCAL_HOUR_MINUTE_FORMATTER.get());
            }
            catch (DateTimeParseException dateTimeParseException2) {
                try {
                    return LocalDateTime.parse(source, TL_LOCAL_DATETIME_FORMATTER.get()).toLocalTime();
                }
                catch (DateTimeParseException dateTimeParseException3) {
                    if (StringUtils.contains(source, ".")) {
                        try {
                            int qty = source.length() - (source.lastIndexOf(".") + 1);
                            if (qty > 0) {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss." + StringUtils.repeat("S", qty));
                                return LocalDateTime.parse(source, formatter).toLocalTime();
                            }
                        }
                        catch (DateTimeParseException dateTimeParseException4) {
                            // empty catch block
                        }
                    }
                    throw new ConvertFailedException(String.class, LocalTime.class, source);
                }
            }
        }
    }

    public static final long getDurationInMilliseconds(Temporal start, Temporal end) {
        return ConvertUtils.convert(end, Long.class) - ConvertUtils.convert(start, Long.class);
    }

    public static final <T> T addDay(T time, int day) {
        return (T)ConvertUtils.convert(new Date(ConvertUtils.convert(time, Date.class).getTime() + 86400000L * (long)day), time.getClass());
    }

    public static final LocalDateTime plusYears(LocalDateTime time, Long years) {
        if (null == time || null == years) {
            return null;
        }
        return time.plusYears(years);
    }

    public static final LocalDateTime plusMonths(LocalDateTime time, Long months) {
        if (null == time || null == months) {
            return null;
        }
        return time.plusMonths(months);
    }

    public static final LocalDateTime plusWeeks(LocalDateTime time, Long weeks) {
        if (null == time || null == weeks) {
            return null;
        }
        return time.plusWeeks(weeks);
    }

    public static final LocalDateTime plusDays(LocalDateTime time, Long days) {
        if (null == time || null == days) {
            return null;
        }
        return time.plusDays(days);
    }

    public static final LocalDateTime plusHours(LocalDateTime time, Long hours) {
        if (null == time || null == hours) {
            return null;
        }
        return time.plusHours(hours);
    }

    public static final LocalDateTime plusMinutes(LocalDateTime time, Long minutes) {
        if (null == time || null == minutes) {
            return null;
        }
        return time.plusMinutes(minutes);
    }

    public static final LocalDate plusYears(LocalDate date, Long years) {
        if (null == date || null == years) {
            return null;
        }
        return date.plusYears(years);
    }

    public static final LocalDate plusMonths(LocalDate date, Long months) {
        if (null == date || null == months) {
            return null;
        }
        return date.plusMonths(months);
    }

    public static final LocalDate plusWeeks(LocalDate date, Long weeks) {
        if (null == date || null == weeks) {
            return null;
        }
        return date.plusWeeks(weeks);
    }

    public static final LocalDate plusDays(LocalDate date, Long days) {
        if (null == date || null == days) {
            return null;
        }
        return date.plusDays(days);
    }

    public static final boolean isBefore(LocalDate d1, LocalDate d2) {
        if (d1 == null || d2 == null) {
            return false;
        }
        return d1.isBefore(d2);
    }

    public static final boolean isAfter(LocalDate d1, LocalDate d2) {
        if (d1 == null || d2 == null) {
            return false;
        }
        return d1.isAfter(d2);
    }

    public static final boolean isEqual(LocalDate d1, LocalDate d2) {
        if (d1 == null || d2 == null) {
            return d1 == null && d2 == null;
        }
        return d1.isEqual(d2);
    }

    public static final boolean isBefore(LocalDateTime t1, LocalDateTime t2) {
        if (t1 == null || t2 == null) {
            return false;
        }
        return t1.isBefore(t2);
    }

    public static final boolean isAfter(LocalDateTime t1, LocalDateTime t2) {
        if (t1 == null || t2 == null) {
            return false;
        }
        return t1.isAfter(t2);
    }

    public static final boolean isEqual(LocalDateTime t1, LocalDateTime t2) {
        if (t1 == null || t2 == null) {
            return t1 == null && t2 == null;
        }
        return t1.isEqual(t2);
    }

    public static final LocalDateTime max(LocalDateTime t1, LocalDateTime t2) {
        if (t1 == null) {
            return t2;
        }
        if (t2 == null) {
            return t1;
        }
        if (t1.isAfter(t2)) {
            return t1;
        }
        return t2;
    }

    public static final LocalDateTime min(LocalDateTime t1, LocalDateTime t2) {
        if (t1 == null) {
            return t2;
        }
        if (t2 == null) {
            return t1;
        }
        if (t1.isBefore(t2)) {
            return t1;
        }
        return t2;
    }

    public static final boolean isLocalDate(String text) {
        try {
            DateTimeUtils.parseLocalDate(text);
            return true;
        }
        catch (ConvertFailedException cfe) {
            return false;
        }
    }

    public static final boolean isLocalDateTime(String text) {
        try {
            DateTimeUtils.parseLocalDateTime(text);
            return true;
        }
        catch (ConvertFailedException cfe) {
            return false;
        }
    }

    private static final String getStandardDateText(String dateText) {
        StringBuilder sb = new StringBuilder();
        int zoneIndex = Math.max(dateText.lastIndexOf("-"), dateText.lastIndexOf("+"));
        if (zoneIndex >= "yyyy-M-d H:m:s".length()) {
            dateText = dateText.substring(0, zoneIndex).trim();
        }
        String[] values = dateText.split("(-|\\/|\u5e74|\u6708|\u65e5|:|T|t|\\.|\\s)");
        int j = values.length;
        for (int i = 0; i < j; ++i) {
            String value = values[i];
            if (i == 0) {
                sb.append(value);
                continue;
            }
            if (i <= 2) {
                sb.append("-").append(StringUtils.leftPad(value, 2));
                continue;
            }
            if (i == 3) {
                sb.append(" ").append(StringUtils.leftPad(value, 2));
                continue;
            }
            if (i <= 5) {
                sb.append(":").append(StringUtils.leftPad(value, 2));
                continue;
            }
            if (i != 6) continue;
            sb.append(".").append(value);
        }
        if (sb.length() == "yyyy-MM-dd HH:mm".length()) {
            sb.append(":00");
        }
        return sb.toString();
    }

    private static ThreadLocal<DateFormat> getThreadLocalDateFormat(final String pattern) {
        return new ThreadLocal<DateFormat>(){

            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(pattern);
            }
        };
    }

    private static ThreadLocal<DateTimeFormatter> getThreadLocalDateTimeFormatter(final String pattern) {
        return new ThreadLocal<DateTimeFormatter>(){

            @Override
            protected DateTimeFormatter initialValue() {
                return DateTimeFormatter.ofPattern(pattern);
            }
        };
    }
}
