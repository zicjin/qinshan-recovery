package com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.utils.EncodingUtils;

/**
 * 文件工具类
 *
 * @author magh@sunwayworld.com 2020年1月10日
 *
 */
public class EmcFileUtils {

    public static String extractTextFromPDF(File file) {
        PDDocument pdfDocument = null;
        try {
            pdfDocument = PDDocument.load(file);
            return new PDFTextStripper().getText(pdfDocument);
        } catch (IOException e) {
            throw new FileException(e);
        } finally {
            if (pdfDocument != null) {
                try {
                    pdfDocument.close();
                } catch (IOException e) {
                    /* ignore */
                }
            }
        }
    }

    public static String extractTextFromXlsx(File file) {
        XSSFWorkbook wb = null;
        try (InputStream is = new BufferedInputStream(new FileInputStream(file));) {
            wb = new XSSFWorkbook(is);
            int sheetNum = wb.getNumberOfSheets();
            StringBuilder sb = new StringBuilder();
            for (int sheetNo = 0; sheetNo < sheetNum; sheetNo++) {
                XSSFSheet sheet = wb.getSheetAt(sheetNo);
                if (wb.isSheetHidden(sheetNo)) {
                    continue;
                }
                sb.append("sheet : ").append(sheet.getSheetName()).append(" start").append("\r\n");
                int endRowNum = sheet.getLastRowNum();
                for (int rowNum = sheet.getFirstRowNum(); rowNum <= endRowNum; rowNum++) {
                    XSSFRow row = sheet.getRow(rowNum);
                    if (row == null) {
                        continue;
                    }
                    int columnQty = row.getLastCellNum();
                    for (int cellIndex = 0; cellIndex < columnQty; cellIndex++) {
                        XSSFCell cell = row.getCell(cellIndex);
                        String value = ExcelHelper.getCellValue(cell, String.class);
                        if ("".equals(value)) {
                            value = "\t";
                        }
                        sb.append(" ").append(value);
                    }
                    sb.append("\r\n");
                }
                sb.append("sheet : ").append(sheet.getSheetName()).append(" end").append("\r\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return extractTextFromXls(file);
        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (IOException e) {
                    /* ignore */
                }
            }
        }
    }

    private static String extractTextFromXls(File file) {
        HSSFWorkbook wb = null;
        try (InputStream is = new BufferedInputStream(new FileInputStream(file));) {
            wb = new HSSFWorkbook(is);
            int sheetNum = wb.getNumberOfSheets();
            StringBuilder sb = new StringBuilder();
            for (int sheetNo = 0; sheetNo < sheetNum; sheetNo++) {
                HSSFSheet sheet = wb.getSheetAt(sheetNo);
                if (wb.isSheetHidden(sheetNo)) {
                    continue;
                }
                sb.append("sheet : ").append(sheet.getSheetName()).append(" start").append("\r\n");
                int endRowNum = sheet.getLastRowNum();
                for (int rowNum = sheet.getFirstRowNum(); rowNum <= endRowNum; rowNum++) {
                    HSSFRow row = sheet.getRow(rowNum);
                    if (row == null) {
                        continue;
                    }
                    int columnQty = row.getLastCellNum();
                    for (int cellIndex = 0; cellIndex < columnQty; cellIndex++) {
                        HSSFCell cell = row.getCell(cellIndex);
                        String value = ExcelHelper.getCellValue(cell, String.class);
                        if ("".equals(value)) {
                            value = "\t";
                        }
                        sb.append(" ").append(value);
                    }
                    sb.append("\r\n");
                }
                sb.append("sheet : ").append(sheet.getSheetName()).append(" end").append("\r\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return getTextFromExcelBIFF5(file);
        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (IOException e) {
                    /* ignore */
                }
            }
        }
    }

    private static String getTextFromExcelBIFF5(File file) {
        Workbook wb = null;
        try (InputStream ins = new BufferedInputStream(new FileInputStream(file));) {
            WorkbookSettings workbookSettings = new WorkbookSettings();
            StringBuilder sb = new StringBuilder();
            workbookSettings.setEncoding("GBK");
            // 去除警告Warning: Object record is not associated with a drawing
            // record - ignoring
            workbookSettings.setDrawingsDisabled(true);

            wb = Workbook.getWorkbook(ins, workbookSettings);
            int sheetNum = wb.getNumberOfSheets();

            for (int sheetNo = 0; sheetNo < sheetNum; sheetNo++) {
                Sheet sheet = wb.getSheet(sheetNo);

                if (sheet != null) {
                    sb.append("sheet :").append(sheet.getName()).append(" start").append("\r\n");
                    int rows = sheet.getRows();
                    int columns = sheet.getColumns();

                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < columns; j++) {
                            Cell cell = sheet.getCell(j, i);
                            String value = cell.getContents();
                            if ("".equals(value)) {
                                value = "\t";
                            }
                            sb.append(" ").append(value);
                        }
                        sb.append("\r\n");
                    }
                    sb.append("sheet :").append(sheet.getName()).append(" end").append("\r\n");
                }
            }
            return sb.toString();
        } catch (Exception x) {
            return extractTextFromTextFile(file);
        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (Exception x) {
                    /* ignore */
                }
            }
        }
    }

    private static String extractTextFromTextFile(File file) {
        FileInputStream ins = null;
        BufferedInputStream bi = null;
        StringBuilder sb = new StringBuilder();
        try {
            ins = new FileInputStream(file);
            bi = new BufferedInputStream(ins);
            byte[] byteArray = new byte[4096];
            int length = 0;
            String charset = EncodingUtils.detect(file);
            while ((length = bi.read(byteArray)) != -1) {
                sb.append(new String(byteArray, 0, length, charset));
            }
            return sb.toString();
        } catch (IOException e) {
            throw new FileException(e);
        } finally {
            if (bi != null) {
                try {
                    bi.close();
                } catch (IOException e) {
                    /* ignore */
                }
            }
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    /* ignore */
                }
            }
        }
    }
}
