/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.io.excel.support;

import com.sunwayworld.framework.utils.ArithUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.Function;

public class ExcelAverageaFunction
implements Function {
    @Override
    public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        int size = 0;
        double sum = 0.0;
        for (ValueEval arg : args) {
            List<Double> valueList = this.getAreaEvalValue(arg);
            for (Double value : valueList) {
                if (value == null) continue;
                sum = ArithUtils.add(sum, value);
                ++size;
            }
        }
        if (size == 0) {
            return new StringEval("");
        }
        return new NumberEval(ArithUtils.div(sum, size));
    }

    private List<Double> getAreaEvalValue(ValueEval valueEval) {
        int height;
        int width;
        ArrayList<Double> valueList = new ArrayList<Double>();
        int firstCol = 0;
        int firstRow = 0;
        if (valueEval instanceof AreaEval) {
            AreaEval ae = (AreaEval)valueEval;
            width = ae.getWidth();
            height = ae.getHeight();
            firstCol = ae.getFirstColumn();
            firstRow = ae.getFirstRow();
        } else if (valueEval instanceof RefEval) {
            RefEval ref = (RefEval)valueEval;
            width = 1;
            height = 1;
            firstCol = ref.getColumn();
            firstRow = ref.getRow();
        } else {
            width = 1;
            height = 1;
        }
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                Double cellValue = null;
                try {
                    ValueEval vA = OperandResolver.getSingleValue(valueEval, firstRow + i, firstCol + j);
                    if (!(vA instanceof BlankEval)) {
                        cellValue = OperandResolver.coerceValueToDouble(vA);
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
                valueList.add(cellValue);
            }
        }
        return valueList;
    }
}
