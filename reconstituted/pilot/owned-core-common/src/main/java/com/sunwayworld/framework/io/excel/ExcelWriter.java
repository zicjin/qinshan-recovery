/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.io.excel;

import com.sunwayworld.framework.io.excel.SheetWriter;
import java.io.OutputStream;

public interface ExcelWriter {
    public SheetWriter[] getSheetWriters();

    public void write(OutputStream var1);
}
