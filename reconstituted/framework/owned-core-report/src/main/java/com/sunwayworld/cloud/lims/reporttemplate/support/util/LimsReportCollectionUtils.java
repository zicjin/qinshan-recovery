/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.util;

import com.sunwayworld.framework.utils.CollectionUtils;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LimsReportCollectionUtils {
    public static final <T> List<T> distinct(List<T> list, Function<? super T, Object> keyExtractor) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        return list.stream().filter(LimsReportCollectionUtils.distinctByKey(keyExtractor)).collect(Collectors.toList());
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        ConcurrentHashMap seen = new ConcurrentHashMap();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
