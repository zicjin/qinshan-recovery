/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.io.text.impl;

import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.io.excel.support.ExcelEmcFunction;
import com.sunwayworld.framework.io.excel.support.ExcelEpaFunction;
import com.sunwayworld.framework.io.excel.support.ExcelFdaFunction;
import com.sunwayworld.framework.io.excel.support.ExcelIsoFunction;
import com.sunwayworld.framework.io.text.FileToTextExtractor;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.udf.AggregatingUDFFinder;
import org.apache.poi.ss.formula.udf.DefaultUDFFinder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelFileToTextFullExtractor
implements FileToTextExtractor {
    public static final ExcelFileToTextFullExtractor instance = new ExcelFileToTextFullExtractor();

    @Override
    public boolean match(String fileExtension) {
        return "xls".equalsIgnoreCase(fileExtension) || "xlsx".equalsIgnoreCase(fileExtension);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public String extract(InputStream is) {
        try (Workbook wb = WorkbookFactory.create(is);){
            String[] functionNames = new String[]{"ISO", "EPA", "FDA", "EMC"};
            FreeRefFunction[] functionImpls = new FreeRefFunction[]{new ExcelIsoFunction(), new ExcelEpaFunction(), new ExcelFdaFunction(), new ExcelEmcFunction()};
            DefaultUDFFinder udfs = new DefaultUDFFinder(functionNames, functionImpls);
            AggregatingUDFFinder udfToolpack = new AggregatingUDFFinder(udfs);
            wb.addToolPack(udfToolpack);
            int sheetNum = wb.getNumberOfSheets();
            StringBuilder sb = new StringBuilder();
            int sheetNo = 0;
            while (true) {
                block19: {
                    int endRowNum;
                    Sheet sheet;
                    block20: {
                        block18: {
                            if (sheetNo >= sheetNum) break block18;
                            sheet = wb.getSheetAt(sheetNo);
                            if (wb.isSheetHidden(sheetNo)) break block19;
                            sb.append("sheetName:").append(sheet.getSheetName()).append("\r\n");
                            endRowNum = sheet.getLastRowNum() + 1;
                            break block20;
                        }
                        String string = sb.toString();
                        return string;
                    }
                    for (int rowNum = sheet.getFirstRowNum(); rowNum <= endRowNum; ++rowNum) {
                        int cellIndex;
                        Row row = sheet.getRow(rowNum);
                        if (row == null) continue;
                        int n = cellIndex = row.getFirstCellNum() == -1 ? 0 : row.getFirstCellNum();
                        while (cellIndex >= 0 && cellIndex <= row.getLastCellNum()) {
                            Cell cell = row.getCell(cellIndex);
                            String value = ExcelHelper.getCellValue(cell, String.class);
                            if ("".equals(value)) {
                                value = "\t";
                            }
                            sb.append(" ").append(value);
                            ++cellIndex;
                        }
                        sb.append("\r\n");
                    }
                }
                ++sheetNo;
            }
        }
        catch (IOException ioe) {
            throw new FileException(ioe);
        }
    }
}
