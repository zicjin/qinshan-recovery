/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.emc.framework.word;

import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.utils.ObjectUtils;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TableWidthType;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHeight;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;

public class Word2024Utils {
    public static void insertContentAndImage(String key, XWPFDocument document, String content, String picUrl) {
        List<XWPFParagraph> paragraphList = document.getParagraphs();
        if (paragraphList != null && paragraphList.size() > 0) {
            for (XWPFParagraph paragraph : paragraphList) {
                String text = paragraph.getText();
                if (text == null || text.indexOf(key) < 0) continue;
                List<XWPFRun> runs = paragraph.getRuns();
                for (int i = runs.size() - 1; i >= 0; --i) {
                    paragraph.removeRun(i);
                }
                XWPFRun run = paragraph.createRun();
                run.setFontSize(16);
                run.setFontFamily("\u4eff\u5b8b");
                run.setBold(true);
                run.addTab();
                run.addTab();
                run.setText(key);
                run.addTab();
                if (content != null) {
                    run.setText(content);
                }
                if (picUrl == null) continue;
                try {
                    run.addPicture(new FileInputStream(picUrl), 6, new File(picUrl).getName(), Units.toEMU(60.0), Units.toEMU(30.0));
                }
                catch (Exception ioe) {
                    throw new FileException(ioe);
                }
            }
        }
    }

    public static void insertContentAndImageToTable(XWPFTable table, int cellNum, String picUrl, String content, int fontSize, String fontName) {
        int count = 0;
        List<XWPFTableRow> rows = table.getRows();
        for (XWPFTableRow row : rows) {
            List<XWPFTableCell> cells = row.getTableCells();
            for (XWPFTableCell cell : cells) {
                ++count;
                List<XWPFParagraph> cellParagraphs = cell.getParagraphs();
                if (cellParagraphs == null || cellParagraphs.size() <= 0) continue;
                XWPFParagraph paragraph = cellParagraphs.get(0);
                if (count != cellNum) continue;
                List<XWPFRun> runs = paragraph.getRuns();
                for (int i = runs.size() - 1; i >= 0; --i) {
                    paragraph.removeRun(i);
                }
                XWPFRun run = paragraph.createRun();
                if (ObjectUtils.isEmpty(fontSize)) {
                    fontSize = 12;
                }
                if (ObjectUtils.isEmpty(fontName) || "".equals(fontName)) {
                    fontName = "\u5b8b\u4f53";
                }
                run.setFontSize(fontSize);
                run.setFontFamily(fontName);
                if (content != null) {
                    run.setText(content);
                }
                if (picUrl == null) continue;
                try {
                    BufferedImage image = ImageIO.read(new FileInputStream(picUrl));
                    double srcWidth = (double)image.getWidth() * 0.6;
                    double srcHeight = (double)image.getHeight() * 0.6;
                    run.addPicture(new FileInputStream(picUrl), 6, new File(picUrl).getName(), Units.toEMU(srcWidth), Units.toEMU(srcHeight));
                }
                catch (Exception ioe) {
                    throw new FileException(ioe);
                }
            }
        }
    }

    public static void insertContentAndImageWithSizeToTable(XWPFTable table, int cellNum, String picUrl, String content) {
        int count = 0;
        List<XWPFTableRow> rows = table.getRows();
        for (XWPFTableRow row : rows) {
            List<XWPFTableCell> cells = row.getTableCells();
            for (XWPFTableCell cell : cells) {
                ++count;
                List<XWPFParagraph> cellParagraphs = cell.getParagraphs();
                if (cellParagraphs == null || cellParagraphs.size() <= 0) continue;
                XWPFParagraph paragraph = cellParagraphs.get(0);
                if (count != cellNum) continue;
                List<XWPFRun> runs = paragraph.getRuns();
                for (int i = runs.size() - 1; i >= 0; --i) {
                    paragraph.removeRun(i);
                }
                XWPFRun run = paragraph.createRun();
                run.setFontSize(12);
                run.setFontFamily("Times New Roman");
                if (content != null) {
                    run.setText(content);
                }
                if (picUrl == null) continue;
                try {
                    run.addPicture(new FileInputStream(picUrl), 6, new File(picUrl).getName(), Units.toEMU(60.0), Units.toEMU(30.0));
                }
                catch (Exception ioe) {
                    throw new FileException(ioe);
                }
            }
        }
    }

    public static void insertImageAfterParagraphOfKey(String key, XWPFDocument document, String picUrl) {
        List<XWPFParagraph> paragraphList = document.getParagraphs();
        if (paragraphList != null && paragraphList.size() > 0) {
            for (int i = 0; i < paragraphList.size(); ++i) {
                XWPFParagraph paragraph = paragraphList.get(i);
                String text = paragraph.getText();
                if (text == null || text.indexOf(key) < 0) continue;
                int ParagraphPosForFlag = document.getPosOfParagraph(paragraph);
                XWPFParagraph paragraphAfterFlag = (XWPFParagraph)document.getBodyElements().get(ParagraphPosForFlag + 1);
                XWPFParagraph picParagraph = document.insertNewParagraph(paragraphAfterFlag.getCTP().newCursor());
                picParagraph.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun run = picParagraph.createRun();
                try {
                    run.addPicture(new FileInputStream(picUrl), 6, new File(picUrl).getName(), Units.toEMU(420.0), Units.toEMU(120.0));
                    continue;
                }
                catch (Exception ioe) {
                    throw new FileException(ioe);
                }
            }
        }
    }

    public static void replaceAll(XWPFDocument document, Map<String, Object> dataMap) {
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (XWPFParagraph xWPFParagraph : paragraphs) {
            List<XWPFRun> runs = xWPFParagraph.getRuns();
            for (XWPFRun run : runs) {
                Word2024Utils.replaceRunText(dataMap, run);
            }
        }
        List<XWPFTable> tables = document.getTables();
        for (XWPFTable table : tables) {
            List<XWPFTableRow> rows = table.getRows();
            for (XWPFTableRow row : rows) {
                List<XWPFTableCell> cells = row.getTableCells();
                for (XWPFTableCell cell : cells) {
                    List<XWPFParagraph> cellParagraphs = cell.getParagraphs();
                    if (cellParagraphs == null || cellParagraphs.size() <= 0) continue;
                    for (XWPFParagraph cellParagraph : cellParagraphs) {
                        List<XWPFRun> runs = cellParagraph.getRuns();
                        if (runs == null || runs.size() <= 0) continue;
                        for (XWPFRun run : runs) {
                            Word2024Utils.replaceRunText(dataMap, run);
                        }
                    }
                }
            }
        }
        List<XWPFHeader> list = document.getHeaderList();
        list.forEach(e -> {
            List<XWPFRun> runs;
            List<XWPFParagraph> headerParagraphs = e.getParagraphs();
            if (headerParagraphs != null && headerParagraphs.size() > 0 && (runs = headerParagraphs.get(0).getRuns()) != null && runs.size() > 0) {
                runs.forEach(run -> Word2024Utils.replaceRunText(dataMap, run));
            }
        });
    }

    public static void setDataToTable(XWPFTable table, List<List<Object>> list) {
        XWPFTableRow sourceRow = table.getRow(0);
        int rowNum = table.getRows().size();
        if (!list.isEmpty() && list.size() > 0) {
            for (int i = 0; i < list.size(); ++i) {
                List<Object> rowDataList = list.get(i);
                XWPFTableRow targetRow = table.insertNewTableRow(i + rowNum);
                targetRow.getCtRow().setTrPr(sourceRow.getCtRow().getTrPr());
                List<XWPFTableCell> cellList = sourceRow.getTableCells();
                if (null == cellList) {
                    return;
                }
                XWPFTableCell targetCell = null;
                for (int j = 0; j < cellList.size(); ++j) {
                    targetCell = targetRow.addNewTableCell();
                    XWPFTableCell sourceCell = cellList.get(j);
                    targetCell.getCTTc().setTcPr(sourceCell.getCTTc().getTcPr());
                    List<XWPFParagraph> sourceParagraphs = sourceCell.getParagraphs();
                    if (sourceParagraphs != null && sourceParagraphs.size() > 0) {
                        List<XWPFParagraph> targetParagraphs = targetCell.getParagraphs();
                        targetParagraphs.get(0).getCTP().setPPr(sourceParagraphs.get(0).getCTP().getPPr());
                        List<XWPFRun> sourceRuns = sourceParagraphs.get(0).getRuns();
                        if (sourceRuns != null && sourceRuns.size() > 0) {
                            XWPFRun sourceRun = sourceRuns.get(0);
                            XWPFRun targetRun = targetParagraphs.get(0).createRun();
                            targetRun.getCTR().setRPr(sourceRun.getCTR().getRPr());
                            if (ObjectUtils.isEmpty(rowDataList.get(j))) continue;
                            targetRun.setText(rowDataList.get(j).toString());
                            continue;
                        }
                        targetCell.setText(sourceCell.getText());
                        continue;
                    }
                    targetCell.setText(sourceCell.getText());
                }
            }
        }
    }

    public static void deleteTable(XWPFTable table) {
        List<XWPFTableRow> rows = table.getRows();
        int rowLength = rows.size();
        for (int i = 0; i < rowLength; ++i) {
            table.removeRow(0);
        }
    }

    public static void deleteParagraph(XWPFDocument document, String deleteFlag) {
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (XWPFParagraph paragraph : paragraphs) {
            List<XWPFRun> runs = paragraph.getRuns();
            for (XWPFRun run : runs) {
                String text = run.getText(0);
                if (text == null || text.indexOf(deleteFlag) == -1) continue;
                document.removeBodyElement(document.getPosOfParagraph(paragraph));
                return;
            }
        }
    }

    public static void deleteParagraphAndTable(XWPFDocument document, String deleteFlag) {
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (XWPFParagraph paragraph : paragraphs) {
            List<XWPFRun> runs = paragraph.getRuns();
            for (XWPFRun run : runs) {
                String text = run.getText(0);
                if (text == null || text.indexOf(deleteFlag) == -1) continue;
                int pos = document.getPosOfParagraph(paragraph);
                document.removeBodyElement(pos);
                document.removeBodyElement(pos);
                return;
            }
        }
    }

    public static void mergeTableColumns(XWPFTable table, int rowIndex, int fromCell, int toCell) {
        for (int cellIndex = fromCell; cellIndex <= toCell; ++cellIndex) {
            XWPFTableCell cell = table.getRow(rowIndex).getCell(cellIndex);
            if (cellIndex == fromCell) {
                cell.getCTTc().getTcPr().addNewHMerge().setVal(STMerge.RESTART);
                continue;
            }
            cell.getCTTc().getTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
        }
    }

    public static void mergeTableRows(XWPFTable table, int col, int fromRow, int toRow) {
        for (int rowIndex = fromRow; rowIndex <= toRow; ++rowIndex) {
            XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
            if (rowIndex == fromRow) {
                cell.getCTTc().getTcPr().addNewVMerge().setVal(STMerge.RESTART);
                continue;
            }
            cell.getCTTc().getTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
        }
    }

    public static void addText(XWPFDocument document, String key, List<String> textList) {
        List<XWPFParagraph> paragraphList = document.getParagraphs();
        if (paragraphList != null && paragraphList.size() > 0) {
            for (XWPFParagraph paragraph : paragraphList) {
                List<XWPFRun> runs = paragraph.getRuns();
                for (int i = 0; i < runs.size(); ++i) {
                    XWPFRun run = runs.get(i);
                    String text = run.getText(0);
                    if (text == null || text.indexOf(key) < 0) continue;
                    for (int j = 0; j < textList.size(); ++j) {
                        if (j == 0) {
                            run.setText(textList.get(j), 0);
                            run.addBreak();
                            run.addTab();
                            continue;
                        }
                        run.setText(textList.get(j));
                        run.addBreak();
                        run.addTab();
                    }
                }
            }
        }
    }

    public static XWPFTable createTable(XWPFDocument document, String key, String tableTitle, String tableUnit, List<List<Object>> tableDataList, Integer tableWidth, Integer cellHeight, Integer fontSize, String fontStyle) {
        List<XWPFParagraph> paragraphList = document.getParagraphs();
        if (paragraphList != null && paragraphList.size() > 0) {
            for (int i = 0; i < paragraphList.size(); ++i) {
                XWPFTableRow row;
                XWPFRun run;
                XWPFParagraph paragraph = paragraphList.get(i);
                if (paragraph.getText().indexOf(key) < 0) continue;
                int ParagraphPosForFlag = document.getPosOfParagraph(paragraph);
                XWPFParagraph paragraphAfterFlag = (XWPFParagraph)document.getBodyElements().get(ParagraphPosForFlag - 1);
                document.insertNewParagraph(paragraphAfterFlag.getCTP().newCursor());
                if (tableTitle != null) {
                    XWPFParagraph paragraphForTableTitle = document.insertNewParagraph(paragraphAfterFlag.getCTP().newCursor());
                    run = paragraphForTableTitle.createRun();
                    paragraphForTableTitle.setAlignment(ParagraphAlignment.CENTER);
                    run.addTab();
                    run.setFontSize(14);
                    run.setFontFamily("\u5b8b\u4f53");
                    run.setBold(true);
                    run.setText(tableTitle);
                }
                if (tableUnit != null) {
                    XWPFParagraph paragraphForUnitHead = document.insertNewParagraph(paragraphAfterFlag.getCTP().newCursor());
                    run = paragraphForUnitHead.createRun();
                    paragraphForUnitHead.setAlignment(ParagraphAlignment.RIGHT);
                    run.setFontSize(11);
                    run.setFontFamily("\u5b8b\u4f53");
                    run.setText(tableUnit);
                }
                XWPFTable table = document.insertNewTbl(paragraphAfterFlag.getCTP().newCursor());
                for (int j = 0; j < tableDataList.size(); ++j) {
                    List<Object> rowDataList = tableDataList.get(j);
                    if (j == 0) {
                        row = table.getRow(0);
                        for (int k = 0; k < rowDataList.size() - 1; ++k) {
                            row.addNewTableCell();
                        }
                        continue;
                    }
                    table.createRow();
                }
                int columnCount = tableDataList.get(0).size();
                if (tableWidth != null) {
                    Word2024Utils.setTableWidthAndHAlign(table, tableWidth, STJc.CENTER);
                } else {
                    Word2024Utils.setTableWidthAndHAlign(table, 10204, STJc.CENTER);
                }
                if (cellHeight != null) {
                    Word2024Utils.setTableHeight(table, cellHeight, STVerticalJc.CENTER);
                }
                for (int k = 0; k < tableDataList.size(); ++k) {
                    row = table.getRow(k);
                    List<Object> rowDataList = tableDataList.get(k);
                    for (int n = 0; n < rowDataList.size(); ++n) {
                        XWPFTableCell cell = row.getCell(n);
                        XWPFParagraph paragraphForCell = cell.getParagraphs().get(0);
                        XWPFRun run2 = paragraphForCell.createRun();
                        run2.setText(rowDataList.get(n).toString());
                        if (fontSize != null) {
                            run2.setFontSize(fontSize);
                        } else {
                            run2.setFontSize(11);
                        }
                        if (fontStyle != null) {
                            run2.setFontFamily(fontStyle);
                        } else {
                            run2.setFontFamily("\u5b8b\u4f53");
                        }
                        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                        cell.getCTTc().getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);
                        cell.setWidthType(TableWidthType.DXA);
                        cell.setWidth(Word2024Utils.getTableCTTblPr(table).getTblW().getW().intValue() / columnCount + "");
                    }
                }
                return table;
            }
        }
        return null;
    }

    public static XWPFTable createTable(XWPFDocument document, WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO) {
        List<XWPFParagraph> paragraphList = document.getParagraphs();
        if (paragraphList != null && paragraphList.size() > 0) {
            for (int i = 0; i < paragraphList.size(); ++i) {
                XWPFRun run;
                XWPFParagraph paragraph = paragraphList.get(i);
                if (paragraph.getText().indexOf(wordTemplateDynamicTableDTO.getReplaceKeyword()) < 0) continue;
                int ParagraphPosForFlag = document.getPosOfParagraph(paragraph);
                XWPFParagraph paragraphAfterFlag = (XWPFParagraph)document.getBodyElements().get(ParagraphPosForFlag - 1);
                document.insertNewParagraph(paragraphAfterFlag.getCTP().newCursor());
                if (wordTemplateDynamicTableDTO.getTableTitle() != null) {
                    XWPFParagraph paragraphForTableTitle = document.insertNewParagraph(paragraphAfterFlag.getCTP().newCursor());
                    run = paragraphForTableTitle.createRun();
                    paragraphForTableTitle.setAlignment(ParagraphAlignment.CENTER);
                    run.addTab();
                    run.setFontSize(12);
                    run.setFontFamily("Times New Roman");
                    run.setBold(true);
                    run.setText(wordTemplateDynamicTableDTO.getTableTitle());
                }
                if (wordTemplateDynamicTableDTO.getTableUnit() != null) {
                    XWPFParagraph paragraphForUnitHead = document.insertNewParagraph(paragraphAfterFlag.getCTP().newCursor());
                    run = paragraphForUnitHead.createRun();
                    paragraphForUnitHead.setAlignment(ParagraphAlignment.RIGHT);
                    run.setFontSize(12);
                    run.setFontFamily("Times New Roman");
                    run.setText(wordTemplateDynamicTableDTO.getTableUnit());
                }
                XWPFTable table = document.insertNewTbl(paragraphAfterFlag.getCTP().newCursor());
                List<List<Object>> tableDataList = wordTemplateDynamicTableDTO.getTableDataList();
                for (int j = 0; j < tableDataList.size(); ++j) {
                    List<Object> rowDataList = tableDataList.get(j);
                    if (j == 0) {
                        XWPFTableRow row = table.getRow(0);
                        for (int k = 0; k < rowDataList.size() - 1; ++k) {
                            row.addNewTableCell();
                        }
                        continue;
                    }
                    table.createRow();
                }
                int columnCount = tableDataList.get(0).size();
                int tableWidth = 10204;
                if (wordTemplateDynamicTableDTO.getTableWidth() != null) {
                    tableWidth = wordTemplateDynamicTableDTO.getTableWidth();
                }
                Word2024Utils.setTableWidthAndHAlign(table, tableWidth, STJc.CENTER);
                if (wordTemplateDynamicTableDTO.getCellHeight() != null) {
                    Word2024Utils.setTableHeight(table, wordTemplateDynamicTableDTO.getCellHeight(), STVerticalJc.CENTER);
                }
                List<Integer> columnWidthList = wordTemplateDynamicTableDTO.getColumnWidthList();
                List<Integer> actualColumnWidthList = Word2024Utils.calcColumnWidth(columnWidthList, columnCount, tableWidth);
                for (int k = 0; k < tableDataList.size(); ++k) {
                    XWPFTableRow row = table.getRow(k);
                    List<Object> rowDataList = tableDataList.get(k);
                    for (int n = 0; n < rowDataList.size(); ++n) {
                        XWPFTableCell cell = row.getCell(n);
                        XWPFParagraph paragraphForCell = cell.getParagraphs().get(0);
                        XWPFRun run2 = paragraphForCell.createRun();
                        String text = rowDataList.get(n).toString();
                        String[] lines = text.split("\n");
                        if (lines.length > 0) {
                            run2.setText(lines[0], 0);
                            for (int b = 1; b < lines.length; ++b) {
                                run2.addBreak();
                                run2.setText(lines[b]);
                            }
                        }
                        if (wordTemplateDynamicTableDTO.getFontSize() != null) {
                            run2.setFontSize(wordTemplateDynamicTableDTO.getFontSize());
                        } else {
                            CTRPr ctrPr = run2.getCTR().addNewRPr();
                            ctrPr.addNewSzCs().setVal(new BigInteger("21"));
                            ctrPr.addNewSz().setVal(new BigInteger("21"));
                        }
                        if (wordTemplateDynamicTableDTO.getFontStyle() != null) {
                            run2.setFontFamily(wordTemplateDynamicTableDTO.getFontStyle());
                        } else {
                            run2.setFontFamily("Times New Roman");
                        }
                        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                        cell.getCTTc().getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);
                        cell.setWidthType(TableWidthType.DXA);
                        cell.setWidth(String.valueOf(actualColumnWidthList.get(n)));
                    }
                }
                Optional.ofNullable(wordTemplateDynamicTableDTO.getMergedColumnList()).ifPresent(list -> list.forEach(c -> Word2024Utils.mergeTableColumns(table, c.getRowIndex(), c.getStartColumnIndex(), c.getEndColumnIndex())));
                Optional.ofNullable(wordTemplateDynamicTableDTO.getMergedRowList()).ifPresent(list -> list.forEach(r -> Word2024Utils.mergeTableRows(table, r.getColumnIndex(), r.getStartRowIndex(), r.getEndRowIndex())));
                return table;
            }
        }
        return null;
    }

    public static void removeKey(XWPFDocument document, String key) {
        List<XWPFParagraph> paragraphList = document.getParagraphs();
        if (paragraphList != null && paragraphList.size() > 0) {
            for (int i = 0; i < paragraphList.size(); ++i) {
                XWPFParagraph paragraph = paragraphList.get(i);
                String text = paragraph.getText();
                if (text.indexOf(key) < 0) continue;
                document.removeBodyElement(document.getPosOfParagraph(paragraph));
            }
        }
    }

    public static void copyOneAterSourceTable(XWPFDocument document, int tableIndex, String tableTitle) {
        XWPFTable sourceTable = document.getTables().get(tableIndex);
        int posOfTable = document.getPosOfTable(sourceTable);
        XWPFParagraph paragraphAfterTable = (XWPFParagraph)document.getBodyElements().get(posOfTable + 1);
        XmlCursor cursorForParagraph = paragraphAfterTable.getCTP().newCursor();
        document.insertNewParagraph(cursorForParagraph);
        if (tableTitle != null) {
            XWPFParagraph paragraphForTableTitle = document.insertNewParagraph(paragraphAfterTable.getCTP().newCursor());
            XWPFRun run = paragraphForTableTitle.createRun();
            paragraphForTableTitle.setAlignment(ParagraphAlignment.CENTER);
            run.addTab();
            run.setFontSize(14);
            run.setFontFamily("\u9ed1\u4f53");
            run.setText(tableTitle);
        }
        XWPFTable targetTable = document.insertNewTbl(paragraphAfterTable.getCTP().newCursor());
        targetTable.getCTTbl().setTblGrid(sourceTable.getCTTbl().getTblGrid());
        targetTable.getCTTbl().setTblPr(sourceTable.getCTTbl().getTblPr());
        XWPFTableRow sourceRow = sourceTable.getRow(0);
        XWPFTableRow targetRow = targetTable.getRow(0);
        targetRow.getCtRow().setTrPr(sourceRow.getCtRow().getTrPr());
        List<XWPFTableCell> cellList = sourceRow.getTableCells();
        if (null == cellList) {
            return;
        }
        for (int j = 0; j < cellList.size(); ++j) {
            XWPFTableCell sourceCell = cellList.get(j);
            XWPFTableCell targetCell = null;
            if (j == 0) {
                targetCell = targetRow.getCell(0);
                targetCell.getCTTc().setTcPr(sourceCell.getCTTc().getTcPr());
                targetCell.getCTTc().getTcPr().addNewTcBorders().addNewLeft().setVal(STBorder.NIL);
            } else {
                targetCell = targetRow.addNewTableCell();
                targetCell.getCTTc().setTcPr(sourceCell.getCTTc().getTcPr());
            }
            if (j == cellList.size() - 1) {
                targetCell.getCTTc().getTcPr().addNewTcBorders().addNewRight().setVal(STBorder.NIL);
            }
            List<XWPFParagraph> sourceParagraphs = sourceCell.getParagraphs();
            for (int k = 0; k < sourceParagraphs.size(); ++k) {
                XWPFParagraph sourceParagraph = sourceParagraphs.get(k);
                XWPFParagraph targetParagraph = null;
                targetParagraph = k == 0 ? targetCell.getParagraphs().get(0) : targetCell.addParagraph();
                targetParagraph.getCTP().setPPr(sourceParagraph.getCTP().getPPr());
                List<XWPFRun> sourceRuns = sourceParagraph.getRuns();
                for (XWPFRun sourceRun : sourceRuns) {
                    XWPFRun targetRun = targetParagraph.createRun();
                    targetRun.getCTR().setRPr(sourceRun.getCTR().getRPr());
                    targetRun.setText(sourceRun.getText(0));
                }
            }
        }
    }

    public static void formatUnit(XWPFDocument document) {
        Pattern pattern = Pattern.compile("<(sup|sub).*?>([\\s\\S]*?)<(/sup|/sub)>");
        List<XWPFTable> tables = document.getTables();
        for (XWPFTable table : tables) {
            List<XWPFTableRow> rows = table.getRows();
            for (XWPFTableRow row : rows) {
                List<XWPFTableCell> cells = row.getTableCells();
                for (XWPFTableCell cell : cells) {
                    XWPFParagraph paragraph;
                    List<XWPFRun> runs;
                    List<XWPFParagraph> cellParagraphs = cell.getParagraphs();
                    if (cellParagraphs == null || cellParagraphs.size() <= 0 || (runs = (paragraph = cellParagraphs.get(0)).getRuns()) == null || runs.size() <= 0) continue;
                    for (int i = 0; i < runs.size(); ++i) {
                        XWPFRun run = runs.get(i);
                        if (ObjectUtils.isEmpty(run.getText(0))) continue;
                        String text = run.getText(0);
                        text = text.replaceAll("<(p|/p).*?>", "");
                        run.setText(text, 0);
                        Matcher match = pattern.matcher(text);
                        while (match.find()) {
                            String htmlKey = match.group();
                            String keyWithoutHtml = htmlKey.replaceAll("<.*?>", "");
                            paragraph.removeRun(paragraph.getRuns().size() - 1);
                            String[] textArray = text.split(htmlKey);
                            ArrayList<String> textList = new ArrayList<String>(Arrays.asList(textArray));
                            textList.add(1, keyWithoutHtml);
                            for (int k = 0; k < textList.size(); ++k) {
                                XWPFRun newRun = paragraph.createRun();
                                newRun.setText((String)textList.get(k));
                                newRun.setFontSize(11);
                                newRun.setFontFamily("\u5b8b\u4f53");
                                if (k != 1) continue;
                                if (htmlKey.indexOf("sup") >= 0) {
                                    newRun.setSubscript(VerticalAlign.SUPERSCRIPT);
                                    continue;
                                }
                                if (htmlKey.indexOf("sub") < 0) continue;
                                newRun.setSubscript(VerticalAlign.SUBSCRIPT);
                            }
                            text = textArray[1];
                        }
                    }
                }
            }
        }
    }

    private static void setTableWidthAndHAlign(XWPFTable table, int width, STJc.Enum enumValue) {
        CTTblBorders tblBorders;
        CTTblPr tblPr = Word2024Utils.getTableCTTblPr(table);
        tblPr.addNewTblLayout().setType(STTblLayoutType.FIXED);
        CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
        CTTblBorders cTTblBorders = tblBorders = tblPr.isSetTblBorders() ? tblPr.getTblBorders() : tblPr.addNewTblBorders();
        if (enumValue != null) {
            CTJc cTJc = tblPr.addNewJc();
            cTJc.setVal(enumValue);
        }
        tblWidth.setW(BigInteger.valueOf(width));
        tblWidth.setType(STTblWidth.DXA);
        tblBorders.getLeft().setVal(STBorder.NIL);
        tblBorders.getRight().setVal(STBorder.NIL);
        BigInteger borderSize = new BigInteger("12");
        tblBorders.getTop().setSz(borderSize);
        tblBorders.getBottom().setSz(borderSize);
    }

    private static void setTableHeight(XWPFTable table, int heigth, STVerticalJc.Enum vertical) {
        List<XWPFTableRow> rows = table.getRows();
        for (XWPFTableRow row : rows) {
            CTTrPr trPr = row.getCtRow().addNewTrPr();
            CTHeight ht = trPr.addNewTrHeight();
            ht.setVal(BigInteger.valueOf(heigth));
            List<XWPFTableCell> cells = row.getTableCells();
            for (XWPFTableCell tableCell : cells) {
                CTTcPr cttcpr = tableCell.getCTTc().addNewTcPr();
                cttcpr.addNewVAlign().setVal(vertical);
            }
        }
    }

    private static CTTblPr getTableCTTblPr(XWPFTable table) {
        CTTbl ttbl = table.getCTTbl();
        CTTblPr tblPr = ttbl.getTblPr() == null ? ttbl.addNewTblPr() : ttbl.getTblPr();
        return tblPr;
    }

    private static void replaceRunText(Map<String, Object> dataMap, XWPFRun run) {
        String text = run.getText(0);
        if (text != null) {
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                String[] lines;
                boolean isSetText = false;
                String key = entry.getKey();
                String value = entry.getValue().toString();
                if (text.indexOf(key) != -1) {
                    isSetText = true;
                    text = text.replaceAll(key, value);
                }
                if (!isSetText || (lines = text.split("\n")).length <= 0) continue;
                run.setText(lines[0], 0);
                for (int b = 1; b < lines.length; ++b) {
                    run.addBreak();
                    run.setText(lines[b]);
                }
            }
        }
    }

    private static List<Integer> calcColumnWidth(List<Integer> columnWidthList, int columnCount, int tableWidth) {
        ArrayList<Integer> actualColumnWidthList = new ArrayList<Integer>(columnCount);
        if (columnWidthList == null || columnWidthList.isEmpty() || columnWidthList.size() < columnCount) {
            int defaultAvgWidth = tableWidth / columnCount;
            IntStream.range(0, columnCount).forEach(index -> actualColumnWidthList.add(defaultAvgWidth));
            return actualColumnWidthList;
        }
        boolean allMatched = columnWidthList.stream().allMatch(width -> width > 0);
        if (allMatched) {
            actualColumnWidthList.addAll(columnWidthList);
            return actualColumnWidthList;
        }
        long fixedWidthCount = columnWidthList.stream().filter(width -> width > 0).count();
        Integer totalFixedWidth = columnWidthList.stream().filter(width -> width > 0).reduce(Integer::sum).orElse(0);
        long defaultWidthCount = columnWidthList.stream().filter(width -> width == 0).count();
        long defaultWidth = (long)(tableWidth - totalFixedWidth) / Math.min(defaultWidthCount, (long)columnCount - fixedWidthCount);
        columnWidthList.forEach(width -> actualColumnWidthList.add(width == 0 ? Long.valueOf(defaultWidth).intValue() : width.intValue()));
        return actualColumnWidthList;
    }
}
