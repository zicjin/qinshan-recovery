/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.io.excel;

import com.sunwayworld.framework.io.excel.ExcelWriter;
import com.sunwayworld.framework.io.excel.SheetWriter;

public abstract class BaseExcelWriter
implements ExcelWriter {
    private SheetWriter[] sheetWriters;

    public BaseExcelWriter(SheetWriter ... sheetWriters) {
        this.sheetWriters = sheetWriters;
    }

    @Override
    public SheetWriter[] getSheetWriters() {
        return this.sheetWriters;
    }
}
