/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.script.test.service.impl;

import com.sunwayworld.framework.utils.ArithUtils;
import java.util.Arrays;
import java.util.List;

public class CoreScriptTestRepository {
    public static Double min(Double ... args) {
        List<Double> argList = Arrays.asList(args);
        return argList.stream().mapToDouble(e -> e).min().orElse(0.0);
    }

    public static Double max(Double ... args) {
        List<Double> argList = Arrays.asList(args);
        return argList.stream().mapToDouble(e -> e).max().orElse(0.0);
    }

    public static Double avg(Double ... args) {
        List<Double> argList = Arrays.asList(args);
        return argList.stream().mapToDouble(e -> e).average().getAsDouble();
    }

    public static Double stdev(Double ... args) {
        List<Double> argList = Arrays.asList(args);
        double avg = argList.stream().mapToDouble(e -> e).average().getAsDouble();
        double sum = argList.stream().mapToDouble(e -> {
            double value = e - avg;
            return ArithUtils.mul(value, value);
        }).sum();
        double value = ArithUtils.div(sum, args.length - 1);
        return ArithUtils.sqrt(value, 2.0);
    }
}
