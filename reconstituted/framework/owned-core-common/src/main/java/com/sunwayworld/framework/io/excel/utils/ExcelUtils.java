/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.io.excel.utils;

import com.sunwayworld.framework.utils.StringUtils;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

public abstract class ExcelUtils {
    public static final String getWellFormedSheetName(String sheetName) {
        if (!StringUtils.isEmpty(sheetName)) {
            if (StringUtils.contains(sheetName, "/")) {
                sheetName = StringUtils.replace(sheetName, "/", "-");
            }
            if (StringUtils.contains(sheetName, "\\")) {
                sheetName = StringUtils.replace(sheetName, "\\", "-");
            }
            if (StringUtils.contains(sheetName, "'")) {
                sheetName = StringUtils.replace(sheetName, "'", "\u2018");
            }
            if (StringUtils.contains(sheetName, "?")) {
                sheetName = StringUtils.replace(sheetName, "?", "\uff1f");
            }
            if (StringUtils.contains(sheetName, "*")) {
                sheetName = StringUtils.replace(sheetName, "*", "\u00d7");
            }
            return sheetName;
        }
        return "";
    }

    public static final CellStyle getTopicCellStyle(Workbook wb) {
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontHeightInPoints((short)18);
        font.setBold(true);
        cs.setBorderBottom(BorderStyle.DOUBLE);
        cs.setAlignment(HorizontalAlignment.CENTER);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setFont(font);
        return cs;
    }

    public static final CellStyle getTitleCellStyle(Workbook wb) {
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontHeightInPoints((short)12);
        font.setBold(true);
        cs.setBorderBottom(BorderStyle.MEDIUM);
        cs.setBorderLeft(BorderStyle.THIN);
        cs.setBorderRight(BorderStyle.THIN);
        cs.setAlignment(HorizontalAlignment.CENTER);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setFont(font);
        return cs;
    }

    public static final CellStyle getFooterCenterCellStyle(Workbook wb) {
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontHeightInPoints((short)12);
        font.setBold(true);
        cs.setBorderBottom(BorderStyle.MEDIUM);
        cs.setBorderLeft(BorderStyle.THIN);
        cs.setBorderRight(BorderStyle.THIN);
        cs.setBorderTop(BorderStyle.MEDIUM);
        cs.setAlignment(HorizontalAlignment.CENTER);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setFont(font);
        return cs;
    }

    public static final CellStyle getFooterRightCellStyle(Workbook wb) {
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontHeightInPoints((short)12);
        font.setBold(true);
        cs.setBorderBottom(BorderStyle.MEDIUM);
        cs.setBorderLeft(BorderStyle.THIN);
        cs.setBorderRight(BorderStyle.THIN);
        cs.setBorderTop(BorderStyle.MEDIUM);
        cs.setAlignment(HorizontalAlignment.RIGHT);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setFont(font);
        return cs;
    }

    public static final CellStyle getStringLeftCellStyle(Workbook wb) {
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        DataFormat format = wb.createDataFormat();
        font.setFontHeightInPoints((short)12);
        cs.setBorderBottom(BorderStyle.THIN);
        cs.setBorderLeft(BorderStyle.THIN);
        cs.setBorderRight(BorderStyle.THIN);
        cs.setAlignment(HorizontalAlignment.LEFT);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setFont(font);
        cs.setDataFormat(format.getFormat("@"));
        return cs;
    }

    public static final CellStyle getCenterCellStyle(Workbook wb) {
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontHeightInPoints((short)12);
        cs.setBorderBottom(BorderStyle.MEDIUM);
        cs.setBorderLeft(BorderStyle.THIN);
        cs.setBorderRight(BorderStyle.THIN);
        cs.setAlignment(HorizontalAlignment.CENTER);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setFont(font);
        return cs;
    }

    public static final CellStyle getLeftCellStyle(Workbook wb) {
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontHeightInPoints((short)12);
        cs.setBorderBottom(BorderStyle.THIN);
        cs.setBorderLeft(BorderStyle.THIN);
        cs.setBorderRight(BorderStyle.THIN);
        cs.setAlignment(HorizontalAlignment.LEFT);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setFont(font);
        return cs;
    }

    public static final CellStyle getRightCellStyle(Workbook wb) {
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontHeightInPoints((short)12);
        cs.setBorderBottom(BorderStyle.THIN);
        cs.setBorderLeft(BorderStyle.THIN);
        cs.setBorderRight(BorderStyle.THIN);
        cs.setAlignment(HorizontalAlignment.RIGHT);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setFont(font);
        return cs;
    }

    public static final int getCellWidth(String content) {
        return Optional.ofNullable(content).orElse("").getBytes(Charset.forName("UTF-8")).length * 256;
    }

    public static final void copyRow(Sheet sheet, int sourceRowNum, int destRowNum) {
        ExcelUtils.copyRow(sheet, sourceRowNum, destRowNum, false);
    }

    public static final void copyRow(Sheet sheet, int sourceRowNum, int destRowNum, boolean copyValue) {
        int lastRowNum = sheet.getLastRowNum();
        if (destRowNum <= lastRowNum) {
            sheet.shiftRows(destRowNum, sheet.getLastRowNum(), 1);
        }
        Row sourceRow = sheet.getRow(sourceRowNum);
        Row destRow = sheet.createRow(destRowNum);
        destRow.setHeight(sourceRow.getHeight());
        int lastCellNum = sourceRow.getLastCellNum();
        block0: for (int i = 0; i < lastCellNum; ++i) {
            int j;
            Cell sourceCell = sourceRow.getCell(i);
            Cell destCell = destRow.getCell(i);
            if (sourceCell == null) continue;
            if (destCell == null) {
                destCell = destRow.createCell(i);
            }
            destCell.setCellStyle(sourceCell.getCellStyle());
            if (sourceCell.getCellType() == CellType.FORMULA && !StringUtils.isEmpty(sourceCell.getCellFormula())) {
                destCell.setCellFormula(ExcelUtils.getResolvedFormula(sourceCell.getCellFormula(), sourceRowNum, destRowNum));
            }
            if (copyValue) {
                ExcelUtils.copyCellValue(sourceCell, destCell);
            }
            boolean merge = false;
            int k = sheet.getNumMergedRegions();
            for (j = 0; j < k; ++j) {
                if (!sheet.getMergedRegion(j).isInRange(destRowNum, i)) continue;
                merge = true;
            }
            if (merge) continue;
            k = sheet.getNumMergedRegions();
            for (j = 0; j < k; ++j) {
                CellRangeAddress rangeAddress = sheet.getMergedRegion(j);
                if (!rangeAddress.isInRange(sourceRowNum, i)) continue;
                sheet.addMergedRegion(new CellRangeAddress(destRowNum, destRowNum, rangeAddress.getFirstColumn(), rangeAddress.getLastColumn()));
                continue block0;
            }
        }
    }

    public static boolean equals(Sheet sheet, CellStyle cs1, CellStyle cs2) {
        if (cs1.equals(cs2)) {
            return true;
        }
        if (!Objects.equals((Object)cs1.getAlignment(), (Object)cs2.getAlignment())) {
            return false;
        }
        if (!Objects.equals(cs1.getFillForegroundColor(), cs2.getFillForegroundColor())) {
            return false;
        }
        if (!Objects.equals((Object)cs1.getFillPattern(), (Object)cs2.getFillPattern())) {
            return false;
        }
        Font font1 = sheet.getWorkbook().getFontAt(cs1.getFontIndexAsInt());
        Font font2 = sheet.getWorkbook().getFontAt(cs2.getFontIndexAsInt());
        return Objects.equals(font1.getColor(), font2.getColor());
    }

    private static void copyCellValue(Cell sourceCell, Cell destCell) {
        switch (sourceCell.getCellType()) {
            case STRING: {
                destCell.setCellValue(sourceCell.getStringCellValue());
                break;
            }
            case NUMERIC: {
                destCell.setCellValue(sourceCell.getNumericCellValue());
                break;
            }
            case BOOLEAN: {
                destCell.setCellValue(sourceCell.getBooleanCellValue());
                break;
            }
            case ERROR: {
                destCell.setCellValue(sourceCell.getErrorCellValue());
                break;
            }
            case BLANK: {
                destCell.setBlank();
            }
        }
    }

    private static final String getResolvedFormula(String formula, int sourceRowNum, int destRowNum) {
        Pattern p = Pattern.compile("[a-zA-Z]" + (sourceRowNum + 1) + "(\\D|$)");
        Matcher m = p.matcher(formula);
        String resolvedFormula = formula;
        while (m.find()) {
            resolvedFormula = StringUtils.replace(resolvedFormula, "" + (sourceRowNum + 1), "" + (destRowNum + 1));
        }
        return resolvedFormula;
    }
}
