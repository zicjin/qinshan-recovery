/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.io.excel;

import com.sunwayworld.framework.io.excel.support.SheetContext;
import org.apache.poi.ss.usermodel.Workbook;

public interface SheetWriter {
    public SheetContext getSheetContext();

    public Class<? extends Object> getItemClass();

    public void write(Workbook var1);
}
