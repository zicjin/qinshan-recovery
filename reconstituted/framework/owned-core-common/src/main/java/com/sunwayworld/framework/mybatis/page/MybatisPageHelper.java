/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.page;

import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.interceptor.MybatisColumnTypeHolder;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MybatisPageHelper {
    private static final ThreadLocal<Boolean> COUNT_ONLY_FLAG = new ThreadLocal();
    private static final ThreadLocal<Boolean> DATA_ONLY_FLAG = new ThreadLocal();
    private static final ThreadLocal<Consumer<ResultSet>> HIGH_PERFORMANCE_DOWNLOAD_CONSUMER = new ThreadLocal();
    private static final ThreadLocal<PageRowBounds> PAGE_CONTAINER = new ThreadLocal();
    private static final ThreadLocal<List<String>> OPTIONS_CONTAINER = new ThreadLocal();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> Page<T> get(PageRowBounds rowBounds, Supplier<List<T>> supplier) {
        try {
            if (rowBounds != null) {
                MybatisPageHelper.bind(rowBounds);
            }
            List<T> list = supplier.get();
            if (rowBounds == null) {
                Pagination<T> page = new Pagination<T>(list);
                Map<String, Class<?>> columnTypeMap = MybatisColumnTypeHolder.popColumnTypeMap();
                if (columnTypeMap != null) {
                    page.setColumnTypeMap(columnTypeMap.entrySet().stream().collect(Collectors.toMap(e -> (String)e.getKey(), e -> MybatisPageHelper.getRevisedColumnType(((Class)e.getValue()).getSimpleName()))));
                }
                page.setOptionsList(OPTIONS_CONTAINER.get());
                Pagination<T> pagination = page;
                return pagination;
            }
            Pagination<T> page = new Pagination<T>(rowBounds, list);
            Map<String, Class<?>> columnTypeMap = MybatisColumnTypeHolder.popColumnTypeMap();
            if (columnTypeMap != null) {
                page.setColumnTypeMap(columnTypeMap.entrySet().stream().collect(Collectors.toMap(e -> (String)e.getKey(), e -> MybatisPageHelper.getRevisedColumnType(((Class)e.getValue()).getSimpleName()))));
            }
            page.setOptionsList(OPTIONS_CONTAINER.get());
            Pagination<T> pagination = page;
            return pagination;
        }
        finally {
            MybatisPageHelper.clear();
        }
    }

    public static final boolean isResponseMappedStatementId() {
        return MybatisPageHelper.isBindPageRowBounds() && !MybatisPageHelper.isResponseOptions() && LocalContextHelper.isAdminLogin() && !MybatisPageHelper.isCountOnly() && !MybatisPageHelper.isDataOnly();
    }

    public static final boolean isResponseColumnType() {
        return MybatisPageHelper.isBindPageRowBounds() && !MybatisPageHelper.isResponseOptions() && !MybatisPageHelper.isCountOnly() && !MybatisPageHelper.isDataOnly();
    }

    public static final boolean isResponseCount() {
        return MybatisPageHelper.isBindPageRowBounds() && !MybatisPageHelper.isResponseOptions() && (MybatisPageHelper.getBindPageRowBounds().isPageEnabled() || !ArrayUtils.isEmpty(MybatisPageHelper.getBindPageRowBounds().getCountProperties()) || !ArrayUtils.isEmpty(MybatisPageHelper.getBindPageRowBounds().getSumProperties()));
    }

    public static final boolean isResponseNoListData() {
        return MybatisPageHelper.isBindPageRowBounds() && (MybatisPageHelper.isResponseOptions() || MybatisPageHelper.isCountOnly());
    }

    public static final boolean isResponseOptions() {
        return MybatisPageHelper.isBindPageRowBounds() && !StringUtils.isBlank(MybatisPageHelper.getBindPageRowBounds().getOptionsProperty());
    }

    public static final void setCountOnly() {
        COUNT_ONLY_FLAG.set(true);
    }

    public static final boolean isCountOnly() {
        return Boolean.TRUE.equals(COUNT_ONLY_FLAG.get());
    }

    public static final void removeCountOnly() {
        COUNT_ONLY_FLAG.remove();
    }

    public static final void setDataOnly() {
        DATA_ONLY_FLAG.set(true);
    }

    public static final boolean isDataOnly() {
        return Boolean.TRUE.equals(DATA_ONLY_FLAG.get());
    }

    public static final void setHighPerformanceDownloadConsumer(Consumer<ResultSet> consumer) {
        HIGH_PERFORMANCE_DOWNLOAD_CONSUMER.set(consumer);
    }

    public static final Consumer<ResultSet> getHighPerformanceDownloadConsumer() {
        return HIGH_PERFORMANCE_DOWNLOAD_CONSUMER.get();
    }

    public static final boolean isHighPerformanceDownload() {
        return HIGH_PERFORMANCE_DOWNLOAD_CONSUMER.get() != null;
    }

    public static final void removeDataOnly() {
        DATA_ONLY_FLAG.remove();
    }

    public static final void bind(PageRowBounds rowBounds) {
        PAGE_CONTAINER.set(rowBounds);
    }

    public static final PageRowBounds getBindPageRowBounds() {
        return PAGE_CONTAINER.get();
    }

    public static final boolean isBindPageRowBounds() {
        return PAGE_CONTAINER.get() != null;
    }

    public static final void setOptionsList(List<String> optionsList) {
        OPTIONS_CONTAINER.set(optionsList);
    }

    public static final void clearPage() {
        PAGE_CONTAINER.remove();
    }

    public static final void clear() {
        MybatisPageHelper.removeCountOnly();
        MybatisPageHelper.removeDataOnly();
        PAGE_CONTAINER.remove();
        OPTIONS_CONTAINER.remove();
        HIGH_PERFORMANCE_DOWNLOAD_CONSUMER.remove();
    }

    private static String getRevisedColumnType(String columnType) {
        switch (columnType) {
            case "Long":
            case "Double":
            case "Number": {
                return "number";
            }
            case "LocalDate": {
                return "date";
            }
            case "LocalDateTime": {
                return "dateTime";
            }
        }
        return "string";
    }
}
