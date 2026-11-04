/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.io.excel;

import com.sunwayworld.framework.io.excel.support.ExcelAverageaFunction;
import com.sunwayworld.framework.io.excel.support.ExcelLinestFunction;
import org.apache.poi.ss.formula.eval.FunctionEval;

public class ExcelFunctionRegistry {
    public static void register() {
        if (!FunctionEval.getSupportedFunctionNames().contains("AVERAGEA")) {
            FunctionEval.registerFunction("AVERAGEA", new ExcelAverageaFunction());
        }
        if (!FunctionEval.getSupportedFunctionNames().contains("LINEST")) {
            FunctionEval.registerFunction("LINEST", new ExcelLinestFunction());
        }
    }
}
