package com.sunwayworld.cloud.lims.reporttemplate.support.util;

import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFTableCell.XWPFVertAlign;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;

public class WordUtil2007 {
   public static void processParagraphs(List<XWPFParagraph> paragraphList, Map<String, Object> param, XWPFDocument doc) {
      if (paragraphList != null && paragraphList.size() > 0) {
         for (XWPFParagraph paragraph : paragraphList) {
            for (XWPFRun run : paragraph.getRuns()) {
               String text = run.getText(0);
               if (text != null) {
                  boolean isSetText = false;

                  for (Entry<String, Object> entry : param.entrySet()) {
                     String key = entry.getKey();
                     if (text.indexOf(key) != -1) {
                        isSetText = true;
                        Object value = entry.getValue();
                        if (value instanceof String) {
                           text = text.replace(key, value.toString());
                        }
                     }
                  }

                  if (isSetText) {
                     run.setText(text, 0);
                  }
               }
            }
         }
      }
   }

   public static void insertTab(String key, XWPFDocument doc, String excelFilePath) throws Exception {
      Map<String, List> res = ExcelUtil.getGrids(excelFilePath, "");
      List<Grid> grids = res.get("grids");
      List<CellRangeAddress> mergeRanges = res.get("mergeRanges");
      List<XWPFTable> tableList = doc.getTables();
      if (tableList != null && tableList.size() > 0) {
         for (XWPFTable table : tableList) {
            for (XWPFTableRow row : table.getRows()) {
               for (XWPFTableCell cell : row.getTableCells()) {
                  List<XWPFParagraph> paragraphs = cell.getParagraphs();
                  if (paragraphs != null && paragraphs.size() > 0) {
                     for (XWPFParagraph paragraph : paragraphs) {
                        for (XWPFRun run : paragraph.getRuns()) {
                           String text = run.getText(0);
                           if (text != null && text.indexOf(key) >= 0) {
                              run.setText("", 0);
                              XWPFTable cellTable = cell.insertNewTbl(paragraph.getCTP().newCursor());
                              cellTable.removeRow(0);
                              CTTblPr ctTblPr = cellTable.getCTTbl().addNewTblPr();
                              CTTblBorders ctTblBorders = ctTblPr.addNewTblBorders();
                              ctTblBorders.addNewInsideH().setVal(STBorder.SINGLE);
                              ctTblBorders.addNewInsideV().setVal(STBorder.SINGLE);
                              createTable(cellTable, grids, mergeRanges);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      List<XWPFParagraph> paragraphList = doc.getParagraphs();
      if (paragraphList != null && paragraphList.size() > 0) {
         for (XWPFParagraph paragraph : paragraphList) {
            for (XWPFRun runx : paragraph.getRuns()) {
               String text = runx.getText(0);
               if (text != null && text.indexOf(key) >= 0) {
                  XmlCursor cursor = paragraph.getCTP().newCursor();
                  XWPFTable table = doc.insertNewTbl(cursor);
                  createTable(table, grids, mergeRanges);
               }
            }
         }
      }
   }

   public static void insertTab(String key, XWPFTable table, String excelFilePath, String sheetName) throws Exception {
      Map<String, List> res = ExcelUtil.getGrids(excelFilePath, sheetName);

      for (XWPFTableRow row : table.getRows()) {
         for (XWPFTableCell cell : row.getTableCells()) {
            List<XWPFParagraph> paragraphs = cell.getParagraphs();
            if (paragraphs != null && paragraphs.size() > 0) {
               for (XWPFParagraph paragraph : paragraphs) {
                  for (XWPFRun run : paragraph.getRuns()) {
                     String text = run.getText(0);
                     if (text != null && text.indexOf(key) >= 0) {
                        run.setText("", 0);
                        if (res != null) {
                           List<Grid> grids = res.get("grids");
                           List<CellRangeAddress> mergeRanges = res.get("mergeRanges");
                           List<Map<Integer, Float>> wList = res.get("width");
                           Map<Integer, Float> wMap = wList.get(0);
                           List<Map<Integer, Integer>> hList = res.get("height");
                           Map<Integer, Integer> hMap = hList.get(0);
                           XWPFTable cellTable = cell.insertNewTbl(paragraph.getCTP().newCursor());
                           Set<Integer> keySet = wMap.keySet();
                           Float all = 0.0F;

                           for (Integer k : keySet) {
                              all = all + wMap.get(k);
                           }

                           for (Integer k : keySet) {
                              Float float1 = wMap.get(k);
                              float mul = (float)(float1.floatValue() * (5000.0 / all.floatValue()));
                              wMap.put(k, mul);
                           }

                           cellTable.removeRow(0);
                           CTTblPr ctTblPr = cellTable.getCTTbl().addNewTblPr();
                           CTTblBorders ctTblBorders = ctTblPr.addNewTblBorders();
                           ctTblBorders.addNewInsideH().setVal(STBorder.SINGLE);
                           ctTblBorders.addNewInsideV().setVal(STBorder.SINGLE);
                           createTable(cellTable, grids, mergeRanges, wMap, hMap, run);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static void insertTab(XWPFRun run, String excelFilePath, String sheetName) throws Exception {
      XWPFTableCell cell = (XWPFTableCell)((XWPFParagraph)run.getParent()).getBody();
      XWPFParagraph paragraph = cell.getParagraphs().get(0);
      Map<String, List> res = ExcelUtil.getGrids(excelFilePath, sheetName);
      run.setText("", 0);
      if (res != null) {
         List<Grid> grids = res.get("grids");
         List<CellRangeAddress> mergeRanges = res.get("mergeRanges");
         List<Map<Integer, Float>> wList = res.get("width");
         Map<Integer, Float> wMap = wList.get(0);
         List<Map<Integer, Integer>> hList = res.get("height");
         Map<Integer, Integer> hMap = hList.get(0);
         XWPFTable cellTable = cell.insertNewTbl(paragraph.getCTP().newCursor());
         Set<Integer> keySet = wMap.keySet();
         Float all = 0.0F;

         for (Integer k : keySet) {
            all = all + wMap.get(k);
         }

         for (Integer k : keySet) {
            Float float1 = wMap.get(k);
            float mul = (float)(float1.floatValue() * (5000.0 / all.floatValue()));
            wMap.put(k, mul);
         }

         cellTable.removeRow(0);
         CTTblPr ctTblPr = cellTable.getCTTbl().addNewTblPr();
         CTTblBorders ctTblBorders = ctTblPr.addNewTblBorders();
         ctTblBorders.addNewInsideH().setVal(STBorder.SINGLE);
         ctTblBorders.addNewInsideV().setVal(STBorder.SINGLE);
         createTable(cellTable, grids, mergeRanges, wMap, hMap, run);
      }
   }

   private static void createTable(
      XWPFTable table, List<Grid> grids, List<CellRangeAddress> mergeRanges, Map<Integer, Float> wMap, Map<Integer, Integer> hMap, XWPFRun oldRun
   ) {
      Map<String, Enum> aMap = new HashMap<>();

      for (int i = 0; i < grids.size(); i++) {
         if (i != 0) {
            Grid lastGrid = grids.get(i - 1);
            Grid grid = grids.get(i);
            int lastRow = lastGrid.getRow();
            int nowRow = grid.getRow();
            int lastCol = lastGrid.getCol();
            int nowCol = grid.getCol();
            if (lastRow == nowRow && lastCol + 1 != nowCol) {
               int insertCount = nowCol - lastCol - 1;

               for (int j = 0; j < insertCount; j++) {
                  XWPFTableRow row = table.getRow(grid.getRow());
                  if (row == null) {
                     row = table.createRow();
                  }

                  XWPFTableCell cell = row.getCell(lastCol + j + 1);
                  if (cell == null) {
                     cell = row.createCell();
                  }

                  cell.setText("");
               }
            }
         }

         Grid grid = grids.get(i);
         XWPFTableRow rowx = table.getRow(grid.getRow());
         if (rowx == null) {
            rowx = table.createRow();
         }

         XWPFTableCell cell = rowx.getCell(grid.getCol());
         aMap.put(grid.getRow() + "," + grid.getCol() + "h", grid.getHorizontalAlignment());
         aMap.put(grid.getRow() + "," + grid.getCol() + "v", grid.getVerticalAlignment());
         if (cell == null) {
            cell = rowx.createCell();
         }

         cell.getParagraphs();
         List<XWPFParagraph> paragraphs = cell.getParagraphs();
         if (paragraphs.size() == 1) {
            XWPFRun r1 = paragraphs.get(0).createRun();
            r1.setFontSize(grid.getFont().getSize());
            r1.setFontFamily(grid.getFont().getName());
            r1.setBold(grid.isBold());
            setCellText(paragraphs.get(0), r1, grid.getText());
         }
      }

      for (CellRangeAddress cellRangeAddress : mergeRanges) {
         int firstRow = cellRangeAddress.getFirstRow();
         int lastRow = cellRangeAddress.getLastRow();
         int firstColumn = cellRangeAddress.getFirstColumn();
         int lastColumn = cellRangeAddress.getLastColumn();
         int numberOfRows = table.getNumberOfRows();
         if (numberOfRows < lastRow + 1) {
            int insertCount = lastRow - numberOfRows + 1;

            for (int j = 0; j < insertCount; j++) {
               table.createRow();
            }
         }

         for (int j = firstRow; j < lastRow + 1; j++) {
            XWPFTableRow rowxx = table.getRow(j);

            for (int k = firstColumn; k < lastColumn + 1; k++) {
               XWPFTableCell cellx = rowxx.getCell(k);
               if (cellx == null) {
                  cellx = rowxx.createCell();
                  cellx.setText("");
               }
            }
         }

         if (firstColumn == lastColumn) {
            mergeCellsVertically(table, firstColumn, firstRow, lastRow);
         } else if (firstRow == lastRow) {
            mergeCellsHorizontal(table, firstRow, firstColumn, lastColumn);
         } else {
            for (int j = firstColumn; j < lastColumn + 1; j++) {
               mergeCellsVertically(table, j, firstRow, lastRow);
            }

            for (int j = firstRow; j < lastRow + 1; j++) {
               mergeCellsHorizontal(table, j, firstColumn, lastColumn);
            }
         }
      }

      int numberOfRows2 = table.getNumberOfRows();

      for (int i = 0; i < numberOfRows2; i++) {
         XWPFTableRow rowxx = table.getRow(i);
         CTTbl tableX = table.getCTTbl();
         CTTblPr pr = tableX.getTblPr();
         CTTblWidth tblW = pr.getTblW();
         if (tblW == null) {
            tblW = pr.addNewTblW();
         }

         tblW.setW(BigInteger.valueOf(5000L));
         tblW.setType(STTblWidth.PCT);
         pr.setTblW(tblW);
         tableX.setTblPr(pr);

         try {
            rowxx.setHeight(hMap.get(i));
         } catch (Exception var29) {
            System.err.println(var29);
         }

         List<XWPFTableCell> tableCells = rowxx.getTableCells();

         for (int j = 0; j < tableCells.size(); j++) {
            XWPFTableCell cellx = rowxx.getCell(j);
            CTTcPr tcpr = cellx.getCTTc().addNewTcPr();
            int finalI = i;
            int finalJ = j;
            List<Grid> collect = grids.stream().filter(m -> m.getRow() == finalI && m.getCol() == finalJ).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(collect)) {
               Color bgColor = collect.get(0).getBgColor();
               if (!ObjectUtils.isEmpty(bgColor)) {
                  int rgb = bgColor.getRGB();
                  String hexColor = String.format("%06x", rgb & 16777215);
                  cellx.getCTTc().addNewTcPr().addNewShd().setFill(hexColor);
               }
            }

            CTTblWidth cellw = tcpr.addNewTcW();
            int c = j;
            int r = i;
            CellRangeAddress merge = mergeRanges.stream().filter(m -> m.getFirstColumn() == c && m.getFirstRow() == r).findAny().orElse(null);
            Float width = 0.0F;
            if (merge == null) {
               CellRangeAddress mergeM = mergeRanges.stream()
                  .filter(m -> m.getFirstColumn() <= c && m.getLastColumn() >= c && m.getFirstRow() <= r && m.getLastRow() >= r)
                  .findAny()
                  .orElse(null);
               if (mergeM == null) {
                  width = wMap.get(j);
                  if (width != null) {
                     cellw.setW(BigInteger.valueOf(width.intValue()));
                     cellw.setType(STTblWidth.PCT);
                  }
               }
            } else {
               for (int x = merge.getFirstColumn(); x <= merge.getLastColumn(); x++) {
                  if (wMap.get(x) != null) {
                     width = width + wMap.get(x);
                  } else {
                     System.out.println(wMap);
                  }
               }

               cellw.setW(BigInteger.valueOf(width.intValue()));
               cellw.setType(STTblWidth.PCT);
            }

            CTTc cttc = cellx.getCTTc();
            CTTcPr ctPr = cttc.addNewTcPr();
            Enum enum1 = aMap.get(i + "," + j + "h");
            if (enum1 != null) {
               if (enum1.equals(HorizontalAlignment.CENTER)) {
                  cttc.getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);
               } else if (enum1.equals(HorizontalAlignment.RIGHT)) {
                  cttc.getPList().get(0).addNewPPr().addNewJc().setVal(STJc.RIGHT);
               } else {
                  cttc.getPList().get(0).addNewPPr().addNewJc().setVal(STJc.LEFT);
               }
            }

            Enum enum2 = aMap.get(i + "," + j + "v");
            if (enum2 != null) {
               if (enum2.equals(VerticalAlignment.CENTER)) {
                  cellx.setVerticalAlignment(XWPFVertAlign.CENTER);
               } else if (enum2.equals(VerticalAlignment.BOTTOM)) {
                  cellx.setVerticalAlignment(XWPFVertAlign.BOTTOM);
               } else {
                  cellx.setVerticalAlignment(XWPFVertAlign.TOP);
               }
            }
         }
      }
   }

   public static void setCellText(XWPFParagraph paragraph, XWPFRun currentRun, String value) {
      if ((value == null || (!value.contains("<sub>") || !value.contains("</sub>")) && (!value.contains("<sup>") || !value.contains("</sup>")))
         && !value.contains("℃")) {
         breakSetValue(value, currentRun);
      } else {
         currentRun.setText("", 0);
         List<XWPFRun> runs = paragraph.getRuns();
         List<XWPFRun> nruns = new ArrayList<>();

         for (int i = runs.size() - 1; i >= 0; i--) {
            runs.get(i).setText("", 0);
            nruns.add(runs.get(i));
            if (currentRun.equals(runs.get(i))) {
               break;
            }
         }

         if (value.contains("℃")) {
            setNewRomanContent(null, value, paragraph, nruns);
         } else {
            setSubSUpValue(null, value, paragraph, nruns);
         }
      }
   }

   public static void setSubSUpValue(XWPFTableCell cell, String value, XWPFParagraph paragraph, List<XWPFRun> runs) {
      if (value != null && (value.contains("<sub>") && value.contains("</sub>") || value.contains("<sup>") && value.contains("</sup>"))) {
         String showObj = new String(value);
         int times = 0;
         int existRunsQty = 0;

         while (showObj.indexOf("<sup>") != -1 || showObj.indexOf("<sub>") != -1) {
            int supindex = showObj.indexOf("<sup>");
            int subindex = showObj.indexOf("<sub>");
            if (subindex == -1 || supindex != -1 && supindex < subindex) {
               String sb = showObj.substring(0, supindex);
               int end = showObj.indexOf("</sup>");
               String sup = showObj.substring(supindex + 5, end);
               String se = showObj.substring(end + 6, showObj.length());
               showObj = se;
               if (times < 0) {
                  XWPFRun cr = runs.get(times);
                  breakSetValue(sb, cr);
               } else {
                  XWPFRun cr = paragraph.createRun();
                  cr.getCTR().setRPr(runs.get(0).getCTR().getRPr());
                  breakSetValue(sb, cr);
               }

               if (++times < 0) {
                  runs.get(times).setText(sup);
                  runs.get(times).setSubscript(VerticalAlign.SUPERSCRIPT);
               } else {
                  XWPFRun createRun = paragraph.createRun();
                  createRun.getCTR().setRPr(runs.get(0).getCTR().getRPr());
                  createRun.setText(sup);
                  createRun.setSubscript(VerticalAlign.SUPERSCRIPT);
               }

               times++;
            } else {
               String sbx = showObj.substring(0, subindex);
               int endx = showObj.indexOf("</sub>");
               String sub = showObj.substring(subindex + 5, endx);
               String sex = showObj.substring(endx + 6, showObj.length());
               showObj = sex;
               if (times < 0) {
                  XWPFRun cr = runs.get(times);
                  breakSetValue(sbx, cr);
               } else {
                  XWPFRun cr = paragraph.createRun();
                  cr.getCTR().setRPr(runs.get(0).getCTR().getRPr());
                  breakSetValue(sbx, cr);
               }

               if (++times < 0) {
                  runs.get(times).setText(sub);
                  runs.get(times).setSubscript(VerticalAlign.SUBSCRIPT);
               } else {
                  XWPFRun createRun = paragraph.createRun();
                  createRun.getCTR().setRPr(runs.get(0).getCTR().getRPr());
                  createRun.setText(sub);
                  createRun.setSubscript(VerticalAlign.SUBSCRIPT);
               }

               times++;
            }

            if (showObj.indexOf("<sub>") != 0 && showObj.indexOf("<sup>") != 0) {
            }

            if (showObj.indexOf("<sup>") == -1 && showObj.indexOf("<sub>") == -1) {
               if (times < 0) {
                  XWPFRun cr = runs.get(times);
                  breakSetValue(showObj, cr);
               } else {
                  XWPFRun cr = paragraph.createRun();
                  cr.getCTR().setRPr(runs.get(0).getCTR().getRPr());
                  breakSetValue(showObj, cr);
               }

               times++;
            }
         }
      }
   }

   private static void setNewRomanContent(XWPFTableCell cell, String value, XWPFParagraph paragraph, List<XWPFRun> runs) {
      int index = value.indexOf("℃");
      CTRPr rPr = runs.get(0).getCTR().getRPr();
      if (index > 0) {
         String preStr = value.substring(0, index);
         XWPFRun preRun = paragraph.createRun();
         preRun.getCTR().setRPr(rPr);
         breakSetValue(preStr, preRun);
      }

      XWPFRun rejexRun = paragraph.createRun();
      rejexRun.getCTR().setRPr(rPr);
      String font = "Times New Roman";
      rejexRun.getCTR().getRPr().getRFonts().setEastAsia(font);
      breakSetValue("℃", rejexRun);
      if (index < value.length() - 1) {
         String aftStr = value.substring(index + 1, value.length());
         XWPFRun aftRun = paragraph.createRun();
         aftRun.getCTR().setRPr(rPr);
         breakSetValue(aftStr, aftRun);
      }
   }

   private static void breakSetValue(String sb, XWPFRun cr) {
      if (!sb.contains("\n") && !sb.contains("\r") && !sb.contains("\r\n")) {
         cr.setText(sb, 0);
      } else {
         String[] split = sb.split("\n");
         if (split.length == 1) {
            split = sb.split("\r");
         }

         if (split.length == 1) {
            split = sb.split("\r\n");
         }

         for (int i = 0; i < split.length; i++) {
            if (i == 0) {
               cr.setText(split[i], 0);
            } else {
               cr.setText(split[i]);
            }

            if (i != split.length - 1) {
               cr.addBreak();
            }
         }
      }
   }

   private static void createTable(XWPFTable table, List<Grid> grids, List<CellRangeAddress> mergeRanges) {
      for (int i = 0; i < grids.size(); i++) {
         if (i != 0) {
            Grid lastGrid = grids.get(i - 1);
            Grid grid = grids.get(i);
            int lastRow = lastGrid.getRow();
            int nowRow = grid.getRow();
            int lastCol = lastGrid.getCol();
            int nowCol = grid.getCol();
            if (lastRow == nowRow && lastCol + 1 != nowCol) {
               int insertCount = nowCol - lastCol - 1;

               for (int j = 0; j < insertCount; j++) {
                  XWPFTableRow row = table.getRow(grid.getRow());
                  if (row == null) {
                     row = table.createRow();
                  }

                  XWPFTableCell cell = row.getCell(lastCol + j + 1);
                  if (cell == null) {
                     cell = row.createCell();
                     CTTc cttc = cell.getCTTc();
                     CTTcPr ctPr = cttc.addNewTcPr();
                     ctPr.addNewVAlign().setVal(STVerticalJc.CENTER);
                     cttc.getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);
                     cell.setVerticalAlignment(XWPFVertAlign.CENTER);
                  }

                  cell.setText("");
               }
            }
         }

         Grid grid = grids.get(i);
         XWPFTableRow rowx = table.getRow(grid.getRow());
         if (rowx == null) {
            rowx = table.createRow();
         }

         XWPFTableCell cell = rowx.getCell(grid.getCol());
         if (cell == null) {
            cell = rowx.createCell();
            CTTc cttc = cell.getCTTc();
            CTTcPr ctPr = cttc.addNewTcPr();
            ctPr.addNewVAlign().setVal(STVerticalJc.CENTER);
            cttc.getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);
            cell.setVerticalAlignment(XWPFVertAlign.CENTER);
         }

         cell.setText(grid.getText());
      }

      for (CellRangeAddress cellRangeAddress : mergeRanges) {
         int firstRow = cellRangeAddress.getFirstRow();
         int lastRow = cellRangeAddress.getLastRow();
         int firstColumn = cellRangeAddress.getFirstColumn();
         int lastColumn = cellRangeAddress.getLastColumn();
         int numberOfRows = table.getNumberOfRows();
         if (numberOfRows < lastRow + 1) {
            int insertCount = lastRow - numberOfRows + 1;

            for (int j = 0; j < insertCount; j++) {
               table.createRow();
            }
         }

         for (int j = firstRow; j < lastRow + 1; j++) {
            XWPFTableRow rowxx = table.getRow(j);

            for (int k = firstColumn; k < lastColumn + 1; k++) {
               XWPFTableCell cell = rowxx.getCell(k);
               if (cell == null) {
                  cell = rowxx.createCell();
                  cell.setText("");
               }
            }
         }

         if (firstColumn == lastColumn) {
            mergeCellsVertically(table, firstColumn, firstRow, lastRow);
         } else if (firstRow == lastRow) {
            mergeCellsHorizontal(table, firstRow, firstColumn, lastColumn);
         } else {
            for (int j = firstColumn; j < lastColumn + 1; j++) {
               mergeCellsVertically(table, j, firstRow, lastRow);
            }

            for (int j = firstRow; j < lastRow + 1; j++) {
               mergeCellsHorizontal(table, j, firstColumn, lastColumn);
            }
         }
      }
   }

   public static void insertDataToTableRow(XWPFTable table, int rowIndex, List<List<Object>> tableDataList) {
      XWPFTableRow sourceRow = table.getRow(rowIndex);
      List<XWPFTableCell> sourceRowCellList = sourceRow.getTableCells();

      for (int i = 0; i < tableDataList.size(); i++) {
         if (i != 0) {
            XWPFTableRow row = table.insertNewTableRow(i + rowIndex);
            row.getCtRow().setTrPr(sourceRow.getCtRow().getTrPr());

            for (XWPFTableCell sourceRowCell : sourceRowCellList) {
               XWPFTableCell cell = row.addNewTableCell();
               cell.getCTTc().setTcPr(sourceRowCell.getCTTc().getTcPr());
               cell.getParagraphs().get(0).getCTP().setPPr(sourceRowCell.getParagraphs().get(0).getCTP().getPPr());
            }
         }

         List<Object> rowDataList = tableDataList.get(i);

         for (int j = 0; j < rowDataList.size(); j++) {
            XWPFParagraph paragraph = table.getRow(i + rowIndex).getCell(j).getParagraphArray(0);
            List<XWPFRun> runs = paragraph.getRuns();

            for (int k = runs.size() - 1; k >= 0; k--) {
               paragraph.removeRun(k);
            }

            XWPFRun run = paragraph.createRun();
            run.getCTR().addNewRPr().set(paragraph.getCTP().getPPr().getRPr().copy());
            if (rowDataList.get(j) != null) {
               String[] texts = StringUtils.split(rowDataList.get(j).toString(), "\n");

               for (int k = 0; k < texts.length; k++) {
                  run.setText(texts[k]);
                  if (k != texts.length - 1) {
                     run.addBreak();
                  }
               }
            }
         }
      }
   }

   public static void insertImage(String key, XWPFDocument doc, String imgFilePath) {
      List<XWPFParagraph> paragraphList = doc.getParagraphs();

      try {
         if (paragraphList != null && paragraphList.size() > 0) {
            for (XWPFParagraph paragraph : paragraphList) {
               for (XWPFRun run : paragraph.getRuns()) {
                  String text = run.getText(0);
                  if (text != null && text.indexOf(key) >= 0) {
                     run.addBreak();
                     run.addPicture(new FileInputStream(imgFilePath), 5, imgFilePath, Units.toEMU(200.0), Units.toEMU(200.0));
                     run.addBreak(BreakType.PAGE);
                  }
               }
            }
         }
      } catch (InvalidFormatException var10) {
         var10.printStackTrace();
      } catch (FileNotFoundException var11) {
         var11.printStackTrace();
      } catch (IOException var12) {
         var12.printStackTrace();
      }
   }

   public static void mergeCellsHorizontal(XWPFTable table, int row, int fromCell, int toCell) {
      for (int cellIndex = fromCell; cellIndex <= toCell; cellIndex++) {
         XWPFTableCell cell = table.getRow(row).getCell(cellIndex);
         if (cellIndex == fromCell) {
            if (cell != null) {
               cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
            }
         } else if (cell != null) {
            cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
         }
      }
   }

   public static void mergeCellsVertically(XWPFTable table, int col, int fromRow, int toRow) {
      for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
         XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
         if (rowIndex == fromRow) {
            cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
         } else {
            cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
         }
      }
   }

   public static void main(String[] args) throws Exception {
      XWPFDocument doc = new XWPFDocument(
         new FileInputStream(
            new File(
               "C:\\Users\\xc\\Desktop\\httpsvn.gikam.com.cnsvnlims6\\guojian\\02项目代码\\gikam-lims\\files\\secure\\20200515\\592731fb612ee6e6af89f2acd92c3241\\WT2020E01B00016-中期-cn.docx"
            )
         )
      );
      insertTab(
         "$<table>",
         doc,
         "C:\\Users\\xc\\Desktop\\httpsvn.gikam.com.cnsvnlims6\\guojian\\02项目代码\\gikam-lims\\files\\secure\\20200515\\a6bbf78930fb4369f96910bbbf353ae8\\315 2020年05月15日.xlsx"
      );
      FileOutputStream fopts = new FileOutputStream("D:/docx/fd.docx");
      doc.write(fopts);
   }

   public static void maind(String[] args) throws Exception {
      XWPFDocument doc = new XWPFDocument(new FileInputStream(new File("D:\\docx\\f.docx")));
      insertTab("${table}", doc, "d:/docx/d.xlsx");
      Map<String, Object> param = new HashMap<>();
      param.put("${name}", "张三");
      param.put("${sex}", "男");
      param.put("${table}", "");
      param.put("${image}", "");
      processParagraphs(doc.getParagraphs(), param, doc);
      FileOutputStream fopts = new FileOutputStream("D:/docx/fd.docx");
      doc.write(fopts);
   }
}
