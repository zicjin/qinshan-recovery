/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.util;

import com.sunwayworld.cloud.lims.reporttemplate.support.util.Grid;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.UserCell;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;

public class ExcelUtil {
    public static Map<String, List> getGrids(String excelFilePath, String sheetName) throws Exception {
        Workbook wb = WorkbookFactory.create(new File(excelFilePath));
        Sheet sheet = wb.getSheet(sheetName);
        HashMap<Integer, Float> widthMap = new HashMap<Integer, Float>();
        HashMap<Integer, Integer> heightMap = new HashMap<Integer, Integer>();
        if (sheet == null) {
            return null;
        }
        List<CellRangeAddress> rangeAddress = sheet.getMergedRegions();
        int re = -1;
        int ce = -1;
        for (CellRangeAddress e : rangeAddress) {
            if (e.getLastColumn() > ce) {
                ce = e.getLastColumn();
            }
            if (e.getLastRow() <= re) continue;
            re = e.getLastRow();
        }
        int rowSum = sheet.getPhysicalNumberOfRows();
        int colSum = sheet.getRow(0).getPhysicalNumberOfCells();
        if (re == -1) {
            re = 0;
            ce = 0;
        }
        int rrr = re;
        int ccc = ce;
        for (int i = rowSum - 1; i >= re; --i) {
            Row row;
            if (i == re) {
                rowSum = re;
            }
            if ((row = sheet.getRow(i)) == null) {
                rrr = i - 1;
                continue;
            }
            int k = row.getPhysicalNumberOfCells();
            for (int j = k - 1; j >= ce; --j) {
                String cellValue;
                Cell cell = row.getCell(j);
                String string = cellValue = cell == null ? "" : ExcelUtil.getCellValue(cell);
                if (StringUtils.isEmpty(cellValue)) continue;
                if (rrr == re) {
                    rrr = i;
                }
                if (ccc >= j) continue;
                ccc = j;
            }
        }
        rowSum = rrr + 1;
        colSum = ccc + 1;
        int[] fromIndex = new int[]{0, 0};
        int[] toIndex = new int[]{rowSum - 1, colSum - 1};
        int rowSize = toIndex[0] + 1;
        int colSize = toIndex[1] + 1;
        int imageWidth = 0;
        int imageHeight = 0;
        UserCell[][] cells = new UserCell[rowSize][colSize];
        int[] rowPixPos = new int[rowSize + 1];
        rowPixPos[0] = 0;
        int[] colPixPos = new int[colSize + 1];
        colPixPos[0] = 0;
        for (int i = 0; i < rowSize; ++i) {
            for (int j = 0; j < colSize; ++j) {
                float widthPix;
                cells[i][j] = new UserCell();
                cells[i][j].setCell(sheet.getRow(i).getCell(j));
                cells[i][j].setRow(i);
                cells[i][j].setCol(j);
                boolean ifShow = i >= fromIndex[0] && j >= fromIndex[1];
                ifShow = ifShow && !sheet.isColumnHidden(j) && !sheet.getRow(i).getZeroHeight();
                cells[i][j].setShow(ifShow);
                float f = widthPix = !ifShow ? 0.0f : sheet.getColumnWidthInPixels(j);
                if (i == fromIndex[0]) {
                    imageWidth = (int)((float)imageWidth + widthPix);
                }
                colPixPos[j + 1] = (int)((double)widthPix * 1.15 + (double)colPixPos[j]);
            }
            boolean ifShow = i >= fromIndex[0];
            ifShow = ifShow && !sheet.getRow(i).getZeroHeight();
            float heightPoint = !ifShow ? 0.0f : sheet.getRow(i).getHeightInPoints();
            imageHeight = (int)((float)imageHeight + heightPoint);
            rowPixPos[i + 1] = (int)(heightPoint * 96.0f / 72.0f) + rowPixPos[i];
        }
        wb.close();
        ArrayList<Grid> grids = new ArrayList<Grid>();
        block5: for (int i = 0; i < rowSize; ++i) {
            for (int j = 0; j < colSize; ++j) {
                String strCell;
                Cell cell;
                Grid grid = new Grid();
                grid.setX(colPixPos[j]);
                grid.setY(rowPixPos[i]);
                grid.setWidth(colPixPos[j + 1] - colPixPos[j]);
                grid.setHeight(rowPixPos[i + 1] - rowPixPos[i]);
                grid.setRow(cells[i][j].getRow());
                grid.setCol(cells[i][j].getCol());
                grid.setShow(cells[i][j].isShow());
                if (cells[i][j].getCell() == null) continue block5;
                CellStyle cellStyle = cells[i][j].getCell().getCellStyle();
                HorizontalAlignment alignmentEnum = cellStyle.getAlignmentEnum();
                grid.setHorizontalAlignment(alignmentEnum);
                VerticalAlignment verticalAlignmentEnum = cellStyle.getVerticalAlignmentEnum();
                grid.setVerticalAlignment(verticalAlignmentEnum);
                widthMap.put(grid.getCol(), Float.valueOf(sheet.getColumnWidthInPixels(grid.getCol())));
                heightMap.put(grid.getRow(), Integer.valueOf(sheet.getRow(grid.getRow()).getHeight()));
                int[] isInMergedStatus = ExcelUtil.isInMerged(grid.getRow(), grid.getCol(), rangeAddress);
                if ((isInMergedStatus[0] != 0 || isInMergedStatus[1] != 0) && isInMergedStatus[0] != -1 && isInMergedStatus[1] != -1) {
                    int lastRowPos = isInMergedStatus[0] > rowSize - 1 ? rowSize - 1 : isInMergedStatus[0];
                    int lastColPos = isInMergedStatus[1] > colSize - 1 ? colSize - 1 : isInMergedStatus[1];
                    grid.setWidth(colPixPos[lastColPos + 1] - colPixPos[j]);
                    grid.setHeight(rowPixPos[lastRowPos + 1] - rowPixPos[i]);
                }
                if ((cell = cells[i][j].getCell()) == null) {
                    grids.add(grid);
                    continue;
                }
                CellStyle cs = cells[i][j].getCell().getCellStyle();
                if (cs.getFillPatternEnum() == FillPatternType.SOLID_FOREGROUND) {
                    grid.setBgColor(cells[i][j].getCell().getCellStyle().getFillForegroundColorColor());
                }
                Font font = wb.getFontAt(cs.getFontIndex());
                grid.setFont(font);
                if (font instanceof XSSFFont) {
                    XSSFFont xf = (XSSFFont)font;
                    grid.setFtColor(xf.getXSSFColor());
                    grid.setBold(xf.getBold());
                }
                if ("true".equals(strCell = ExcelUtil.getCellValue(cells[i][j].getCell()))) {
                    strCell = "\u2611";
                } else if ("false".equals(strCell)) {
                    strCell = "\u25a1";
                }
                grid.setText(strCell.matches("\\w*\\.0") ? strCell.substring(0, strCell.length() - 2) : strCell);
                grids.add(grid);
            }
        }
        HashMap<String, List> res = new HashMap<String, List>();
        res.put("grids", grids);
        res.put("mergeRanges", rangeAddress);
        ArrayList<HashMap<Integer, Float>> wList = new ArrayList<HashMap<Integer, Float>>();
        wList.add(widthMap);
        res.put("width", wList);
        ArrayList<HashMap<Integer, Integer>> hList = new ArrayList<HashMap<Integer, Integer>>();
        hList.add(heightMap);
        res.put("height", hList);
        return res;
    }

    private static String getCellValue(Cell cell) {
        String strCell = "";
        if (cell == null) {
            return strCell;
        }
        switch (cell.getCellTypeEnum()) {
            case NUMERIC: {
                strCell = String.valueOf(cell.getNumericCellValue());
                break;
            }
            case STRING: {
                strCell = cell.getStringCellValue();
                break;
            }
            case BOOLEAN: {
                strCell = String.valueOf(cell.getBooleanCellValue());
                break;
            }
            case FORMULA: {
                try {
                    strCell = String.valueOf(cell.getNumericCellValue());
                }
                catch (IllegalStateException e) {
                    strCell = String.valueOf(cell.getRichStringCellValue());
                }
                break;
            }
            default: {
                strCell = "";
            }
        }
        if (cell.getCellStyle().getDataFormatString().contains("0.00%")) {
            try {
                double dbCell = Double.valueOf(strCell);
                strCell = new DecimalFormat("#.00").format(dbCell * 100.0) + "%";
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return strCell;
    }

    private static int[] isInMerged(int row, int col, List<CellRangeAddress> rangeAddress) {
        int[] isInMergedStatus = new int[]{-1, -1};
        for (CellRangeAddress cra : rangeAddress) {
            if (row == cra.getFirstRow() && col == cra.getFirstColumn()) {
                isInMergedStatus[0] = cra.getLastRow();
                isInMergedStatus[1] = cra.getLastColumn();
                return isInMergedStatus;
            }
            if (row < cra.getFirstRow() || row > cra.getLastRow() || col < cra.getFirstColumn() || col > cra.getLastColumn()) continue;
            isInMergedStatus[0] = 0;
            isInMergedStatus[1] = 0;
            return isInMergedStatus;
        }
        return isInMergedStatus;
    }
}
