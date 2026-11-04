/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.exception.CronException;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import net.redhogs.cronparser.CronExpressionDescriptor;
import net.redhogs.cronparser.Options;
import org.quartz.CronExpression;

public abstract class CronUtils {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Options CRON_OPTIONS = new Options();

    public static final boolean isValidExpression(String cron) {
        if (StringUtils.isBlank(cron)) {
            return false;
        }
        String[] strs = StringUtils.regexSplit(cron, "\\s+");
        if (StringUtils.contains(strs[4], "/")) {
            String[] months = StringUtils.split(strs[4], "/");
            strs[4] = months[0];
            cron = StringUtils.join(strs, " ");
        }
        try {
            new CronExpression(cron);
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    public static final LocalDateTime getNextExecutionTime(String cron) {
        return CronUtils.getNextExecutionTime(cron, LocalDateTime.now());
    }

    public static final LocalDateTime getNextExecutionTime(String cron, LocalDateTime startTime) {
        return CronUtils.getNextExecutionTime(cron, startTime, null);
    }

    public static final LocalDateTime getNextExecutionTime(String cron, LocalDateTime startTime, LocalDateTime endTime) {
        if (!CronUtils.isValidExpression(cron)) {
            return null;
        }
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
        String monthInterval = null;
        String[] strs = StringUtils.regexSplit(cron, "\\s+");
        if (StringUtils.contains(strs[4], "/")) {
            String[] months = StringUtils.split(strs[4], "/");
            strs[4] = months[0];
            if (months.length == 2) {
                monthInterval = months[1];
            }
            cron = StringUtils.join(strs, " ");
        }
        LocalDateTime now = LocalDateTime.now();
        if (monthInterval != null) {
            startTime = startTime.plusMonths(NumberUtils.parseLong(monthInterval));
            while (DateTimeUtils.isBefore(startTime, now)) {
                startTime = startTime.plusMonths(NumberUtils.parseLong(monthInterval));
            }
        }
        try {
            CronExpression cronExpression = new CronExpression(cron);
            LocalDateTime nextExecutionTime = ConvertUtils.convert(cronExpression.getNextValidTimeAfter(ConvertUtils.convert(startTime, Date.class)), LocalDateTime.class);
            while (DateTimeUtils.isBefore(nextExecutionTime, now)) {
                nextExecutionTime = ConvertUtils.convert(cronExpression.getNextValidTimeAfter(ConvertUtils.convert(nextExecutionTime, Date.class)), LocalDateTime.class);
            }
            if (endTime != null && DateTimeUtils.isBefore(endTime, nextExecutionTime)) {
                return null;
            }
            return nextExecutionTime;
        }
        catch (ParseException pe) {
            throw new UnexpectedException(pe);
        }
    }

    public static final LocalDateTime getNextExecutionTimeByInterval(LocalDateTime lastExecuteTime, int interval, ChronoUnit unit, LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime nextExecutionTime;
        LocalDateTime now = LocalDateTime.now();
        if (startTime == null || startTime.isBefore(now)) {
            startTime = now;
        }
        if (lastExecuteTime.isBefore(startTime)) {
            long times = (long)Math.ceil(1.0 * (double)lastExecuteTime.until(startTime, unit) / (double)interval);
            nextExecutionTime = lastExecuteTime.plus(times * (long)interval, unit);
            if (nextExecutionTime.isBefore(startTime)) {
                nextExecutionTime = nextExecutionTime.plus(interval, unit);
            }
        } else {
            nextExecutionTime = lastExecuteTime;
        }
        if (endTime != null && nextExecutionTime.isAfter(endTime)) {
            return null;
        }
        return nextExecutionTime;
    }

    public static String getDescription(String cron) {
        if (StringUtils.isEmpty(cron)) {
            return null;
        }
        try {
            return CronExpressionDescriptor.getDescription(cron, CRON_OPTIONS, Locale.SIMPLIFIED_CHINESE);
        }
        catch (ParseException e) {
            throw new CronException("GIKAM.EXCEPTION.CRON_EXCEPTION", e);
        }
    }

    public static List<LocalDateTime> getNextExecutionTimeList(String cron, LocalDateTime startDate, LocalDateTime endDate, List<LocalDateTime> nextDateList, int maxRecursiveCount) {
        if (nextDateList.size() >= maxRecursiveCount) {
            return nextDateList;
        }
        CronExpression cronExpression = null;
        Date parseDate = null;
        try {
            cronExpression = new CronExpression(cron);
            parseDate = DATE_FORMAT.parse(DateTimeUtils.formatLocalDateTime(startDate));
        }
        catch (ParseException pe) {
            throw new UnexpectedException(pe);
        }
        LocalDateTime nextExecutionTime = DateTimeUtils.parseLocalDateTime(DATE_FORMAT.format(cronExpression.getNextValidTimeAfter(parseDate)));
        if (nextExecutionTime.isBefore(endDate)) {
            nextDateList.add(nextExecutionTime);
            CronUtils.getNextExecutionTimeList(cron, nextExecutionTime, endDate, nextDateList, maxRecursiveCount);
        }
        return nextDateList;
    }

    static {
        CRON_OPTIONS.setTwentyFourHourTime(true);
        CRON_OPTIONS.setNeedSpaceBetweenWords(false);
    }
}
