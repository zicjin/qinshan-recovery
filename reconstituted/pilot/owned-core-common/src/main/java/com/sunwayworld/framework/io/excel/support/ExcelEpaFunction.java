/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.io.excel.support;

import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

public class ExcelEpaFunction
implements FreeRefFunction {
    private static final Logger logger = LogManager.getLogger(ExcelEpaFunction.class);

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length != 2) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            ValueEval v1 = OperandResolver.getSingleValue(args[0], ec.getRowIndex(), ec.getColumnIndex());
            ValueEval v2 = OperandResolver.getSingleValue(args[1], ec.getRowIndex(), ec.getColumnIndex());
            String data = ConvertUtils.convert(OperandResolver.coerceValueToDouble(v1), String.class);
            Integer numberCnt = ConvertUtils.convert(OperandResolver.coerceValueToDouble(v2), Integer.class);
            return new StringEval(NumberUtils.rounding(data, "EPA," + numberCnt));
        }
        catch (EvaluationException ee) {
            logger.error(ee.getMessage(), (Throwable)ee);
            return ee.getErrorEval();
        }
    }
}
