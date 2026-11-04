/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.io.excel.impl;

import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.io.excel.BaseExcelWriter;
import com.sunwayworld.framework.io.excel.SheetWriter;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class DefaultExcelWriter
extends BaseExcelWriter {
    public DefaultExcelWriter(SheetWriter ... sheetWriters) {
        super(sheetWriters);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(OutputStream out) {
        try {
            SXSSFWorkbook wb = new SXSSFWorkbook(null, 100, false, true);
            try {
                for (SheetWriter sheetWriter : this.getSheetWriters()) {
                    sheetWriter.write(wb);
                }
                wb.write(out);
            }
            finally {
                if (wb != null) {
                    try {
                        wb.dispose();
                    }
                    catch (Exception exception) {}
                    try {
                        wb.close();
                    }
                    catch (Exception exception) {}
                }
            }
        }
        catch (IOException ioe) {
            throw new UnexpectedException(ioe);
        }
    }
}
