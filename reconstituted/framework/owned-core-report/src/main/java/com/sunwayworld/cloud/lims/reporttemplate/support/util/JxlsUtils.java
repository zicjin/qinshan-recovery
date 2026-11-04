/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.util;

import com.spire.xls.ExcelVersion;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;
import com.spire.xls.WorksheetCopyType;
import com.sunwayworld.framework.utils.ObjectUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JxlsUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JxlsUtils.class);

    public static void exportExcel(InputStream is, OutputStream os, Context context) {
        try {
            JxlsHelper.getInstance().setEvaluateFormulas(true).processTemplate(is, os, context);
        }
        catch (Throwable s) {
            LOGGER.error("exportExcel error={}", s);
            throw new RuntimeException(s);
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void exportExcel(File templateXls, File outXls, Context context) throws FileNotFoundException {
        JxlsUtils.exportExcel(new FileInputStream(templateXls), new FileOutputStream(outXls), context);
    }

    public static void mergeExcelFile(List<File> inExcelFileList, File outExcelFile) {
        if (ObjectUtils.isEmpty(inExcelFileList)) {
            return;
        }
        Workbook newBook = new Workbook();
        newBook.getWorksheets().clear();
        Workbook tempBook = new Workbook();
        for (File file : inExcelFileList) {
            tempBook.loadFromFile(file.getAbsolutePath());
            for (Object sheet : tempBook.getWorksheets()) {
                newBook.getWorksheets().addCopy((Worksheet)sheet, WorksheetCopyType.CopyAll);
            }
        }
        newBook.saveToFile(outExcelFile.getAbsolutePath(), ExcelVersion.Version2013);
    }
}
