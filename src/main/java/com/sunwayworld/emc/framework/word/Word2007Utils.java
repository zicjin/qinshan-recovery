package com.sunwayworld.emc.framework.word;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TableWidthType;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableCell.XWPFVertAlign;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHeight;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
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

import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 生成word报告的工具类
 *
 * @author magh@sunwayworld.com 2019年12月6日
 *
 */
@Deprecated
public class Word2007Utils {

    public static final String DEFAULT_FONT_FAMILY = "宋体";

    /**
     * 刪除指定关键字同行数据，并在后面添加文本和图片
     *
     * @param key
     *            关键字
     * @param document
     * @param content
     *            文本内容
     * @param picUrl
     *            图片路径
     */
    public static void insertContentAndImage(String key, XWPFDocument document, String content, String picUrl) {
        List<XWPFParagraph> paragraphList = document.getParagraphs();

        if (paragraphList != null && paragraphList.size() > 0) {
            for (XWPFParagraph paragraph : paragraphList) {
                String text = paragraph.getText();

                if (text != null) {
                    if (text.indexOf(key) >= 0) {

                        List<XWPFRun> runs = paragraph.getRuns();
                        for (int i = runs.size() - 1; i >= 0; i--) {
                            paragraph.removeRun(i);
                        }

                        XWPFRun run = paragraph.createRun();
                        run.setFontSize(16);
                        run.setFontFamily("仿宋");
                        run.setBold(true);
                        run.addTab();
                        run.addTab();
                        run.setText(key);
                        run.addTab();
                        if (content != null) {
                            run.setText(content);
                        }
                        if (picUrl != null) {
                            try {
                                run.addPicture(new FileInputStream(picUrl), Document.PICTURE_TYPE_PNG, new File(picUrl).getName(), Units.toEMU(60), Units.toEMU(30)); // 100x50pixels
                            } catch (Exception ioe) {
                                throw new FileException(ioe);
                            }
                        }
                    }

                }
            }
        }
    }

    /**
     * 刪除指定单元格数据，并在单元格添加文本和图片
     *
     * @param table
     *            表格
     * @param cellNum
     *            第几个单元格
     * @param picUrl
     *            图片路径
     * @param content
     *            文本
     */
    public static void insertContentAndImageToTable(XWPFTable table, int cellNum, String picUrl, String content) {
        int count = 0;
        List<XWPFTableRow> rows = table.getRows();
        for (XWPFTableRow row : rows) {

            List<XWPFTableCell> cells = row.getTableCells();
            for (XWPFTableCell cell : cells) {
                count++;
                List<XWPFParagraph> cellParagraphs = cell.getParagraphs();
                if (cellParagraphs != null && cellParagraphs.size() > 0) {

                    XWPFParagraph paragraph = cellParagraphs.get(0);

                    if (count == cellNum) {
                        List<XWPFRun> runs = paragraph.getRuns();
                        for (int i = runs.size() - 1; i >= 0; i--) {
                            paragraph.removeRun(i);
                        }
                        XWPFRun run = paragraph.createRun();

                        run.setFontSize(12);
                        run.setFontFamily("宋体");
                        if (content != null) {
                            run.setText(content);
                        }
                        if (picUrl != null) {
                            try {
                                BufferedImage image = ImageIO.read(new FileInputStream(picUrl));
                                double srcWidth = image.getWidth() * 0.6;
                                double srcHeight = image.getHeight() * 0.6;
                                run.addPicture(new FileInputStream(picUrl), Document.PICTURE_TYPE_PNG, new File(picUrl).getName(), Units.toEMU(srcWidth), Units.toEMU(srcHeight)); // 100x50pixels
                            } catch (Exception ioe) {
                                throw new FileException(ioe);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 在关键字后一行，插入图片
     *
     * @param key
     *            关键字
     * @param document
     * @param picUrl
     *            图片路径
     */
    public static void insertImageAfterParagraphOfKey(String key, XWPFDocument document, String picUrl) {
        List<XWPFParagraph> paragraphList = document.getParagraphs();

        if (paragraphList != null && paragraphList.size() > 0) {
            for (int i = 0; i < paragraphList.size(); i++) {
                XWPFParagraph paragraph = paragraphList.get(i);
                String text = paragraph.getText();

                if (text != null) {
                    if (text.indexOf(key) >= 0) {
                        int ParagraphPosForFlag = document.getPosOfParagraph(paragraph);
                        XWPFParagraph paragraphAfterFlag = (XWPFParagraph) document.getBodyElements().get(ParagraphPosForFlag + 1);

                        XWPFParagraph picParagraph = document.insertNewParagraph(paragraphAfterFlag.getCTP().newCursor());

                        picParagraph.setAlignment(ParagraphAlignment.CENTER);// 居中
                        XWPFRun run = picParagraph.createRun();

                        try {
                            run.addPicture(new FileInputStream(picUrl), Document.PICTURE_TYPE_PNG, new File(picUrl).getName(), Units.toEMU(60), Units.toEMU(30)); // 100x50pixels
                        } catch (Exception ioe) {
                            throw new FileException(ioe);
                        }
                    }
                }
            }
        }
    }

    public static void insertImageOfKey(String key, XWPFDocument document, String picUrl) {
        List<XWPFParagraph> paragraphList = document.getParagraphs();

        paragraphList.stream().filter(p -> !StringUtils.isBlank(p.getText()) && p.getText().contains(key)).forEach(paragraph -> {
            List<XWPFRun> runsList = paragraph.getRuns();
            List<Integer> removeIdList = new ArrayList<>();
            IntStream.range(0, runsList.size()).forEach(pos -> {
                XWPFRun xwpfRun = runsList.get(pos);
                if (StringUtils.contains(xwpfRun.getText(0), key)) {
                    removeIdList.add(pos);
                }
            });
            removeIdList.forEach(paragraph::removeRun);

            XWPFRun run = paragraph.createRun();
            try {
                run.addPicture(new FileInputStream(picUrl), Document.PICTURE_TYPE_PNG, new File(picUrl).getName(), Units.toEMU(60), Units.toEMU(30)); // 100x50pixels
            } catch (Exception ioe) {
                throw new FileException(ioe);
            }
        });

        List<XWPFTable> tables = document.getTables();
        tables.forEach(table -> {
            table.getRows().forEach(row -> row.getTableCells().forEach(cell -> {
                List<XWPFParagraph> paragraphs = cell.getParagraphs();
                paragraphs.stream().filter(p -> !StringUtils.isBlank(p.getText()) && p.getText().contains(key)).forEach(paragraph -> {
                    List<XWPFRun> runsList = paragraph.getRuns();
                    List<Integer> removeIdList = new ArrayList<>();
                    IntStream.range(0, runsList.size()).forEach(pos -> {
                        XWPFRun xwpfRun = runsList.get(pos);
                        if (StringUtils.contains(xwpfRun.getText(0), key)) {
                            removeIdList.add(pos);
                        }
                    });
                    removeIdList.forEach(paragraph::removeRun);

                    XWPFRun run = paragraph.createRun();
                    try {
                        run.addPicture(new FileInputStream(picUrl), Document.PICTURE_TYPE_PNG, new File(picUrl).getName(), Units.toEMU(60), Units.toEMU(30)); // 100x50pixels
                    } catch (Exception ioe) {
                        throw new FileException(ioe);
                    }
                });
            }));
        });
    }

    /**
     * 替换指定字符串
     *
     * @param document
     * @param dataMap
     *            替换的键值对
     */
    public static void replaceAll(XWPFDocument document, Map<String, Object> dataMap) {

        // 处理段落
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (XWPFParagraph paragraph : paragraphs) {
            List<XWPFRun> runs = paragraph.getRuns();
            for (XWPFRun run : runs) {
                replaceRunText(dataMap, run);
            }
        }

        // 处理表格
        List<XWPFTable> tables = document.getTables();
        for (XWPFTable table : tables) {
            List<XWPFTableRow> rows = table.getRows();
            for (XWPFTableRow row : rows) {
                List<XWPFTableCell> cells = row.getTableCells();
                for (XWPFTableCell cell : cells) {

                    List<XWPFParagraph> cellParagraphs = cell.getParagraphs();
                    if (cellParagraphs != null && cellParagraphs.size() > 0) {

                        List<XWPFRun> runs = cellParagraphs.get(0).getRuns();
                        if (runs != null && runs.size() > 0) {
                            for (XWPFRun run : runs) {
                                replaceRunText(dataMap, run);
                            }
                        }
                    }
                }
            }
        }

        // 处理页眉
        List<XWPFHeader> headerList = document.getHeaderList();
        headerList.forEach(header -> {
            List<XWPFParagraph> headerParagraphs = header.getParagraphs();
            headerParagraphs.forEach(paragraph -> paragraph.getRuns().forEach(run -> replaceRunText(dataMap, run)));
        });

        // 处理页脚
        List<XWPFFooter> footerList = document.getFooterList();
        footerList.forEach(footer -> {
            List<XWPFParagraph> footerParagraphs = footer.getParagraphs();
            footerParagraphs.forEach(paragraph -> paragraph.getRuns().forEach(run -> replaceRunText(dataMap, run)));

            List<XWPFTable> footerTables = footer.getTables();
            footerTables.forEach(table -> table.getRows().forEach(row -> row.getTableCells().forEach(cell -> cell.getParagraphs().forEach(paragraph -> paragraph.getRuns().forEach(run -> replaceRunText(dataMap, run))))));
        });
    }

    /**
     * 给表格添加数据(保证原表格列数与每行的数据个数相同)
     *
     * @param table
     *            表格对象
     * @param list
     *            数据容器
     */
    public static void setDataToTable(XWPFTable table, List<List<Object>> list) {

        // 获取第一行，复制表格样式
        XWPFTableRow sourceRow = table.getRow(0);

        // 原表格有几行数据
        int rowNum = table.getRows().size();

        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {

                List<Object> rowDataList = list.get(i);

                // 新增一行
                XWPFTableRow targetRow = table.insertNewTableRow(i + rowNum);

                // 复制行属性
                targetRow.getCtRow().setTrPr(sourceRow.getCtRow().getTrPr());

                List<XWPFTableCell> cellList = sourceRow.getTableCells();
                if (null == cellList) {
                    return;
                }

                // 复制列及其属性
                XWPFTableCell targetCell = null;
                for (int j = 0; j < cellList.size(); j++) {

                    targetCell = targetRow.addNewTableCell();
                    XWPFTableCell sourceCell = cellList.get(j);
                    // 列属性
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
                            targetRun.setBold(false);
                            if (!ObjectUtils.isEmpty(rowDataList.get(j))) {
                                targetRun.setText(rowDataList.get(j).toString());
                            }

                        } else {
                            targetCell.setText(sourceCell.getText());
                        }
                    } else {
                        targetCell.setText(sourceCell.getText());
                    }
                }
            }
        }
    }

    /**
     * 删除表格，会有空行
     *
     * @param table
     */
    public static void deleteTable(XWPFTable table) {
        List<XWPFTableRow> rows = table.getRows();
        int rowLength = rows.size();
        for (int i = 0; i < rowLength; i++) {
            table.removeRow(0);
        }
    }

    /**
     * 删除标记行（通过判断run是否包含）
     *
     * @param document
     * @param deleteFlag
     *            删除标记
     *
     */
    public static void deleteParagraph(XWPFDocument document, String deleteFlag) {
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (XWPFParagraph paragraph : paragraphs) {
            List<XWPFRun> runs = paragraph.getRuns();
            for (XWPFRun run : runs) {
                String text = run.getText(0);
                if (text != null && text.indexOf(deleteFlag) != -1) {
                    document.removeBodyElement(document.getPosOfParagraph(paragraph));
                    return;
                }
            }
        }
    }

    /**
     * 删除标记行和之后的表格
     *
     * @param document
     * @param deleteFlag
     *            删除标记
     *
     */
    public static void deleteParagraphAndTable(XWPFDocument document, String deleteFlag) {
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (XWPFParagraph paragraph : paragraphs) {
            List<XWPFRun> runs = paragraph.getRuns();
            for (XWPFRun run : runs) {
                String text = run.getText(0);
                if (text != null && text.indexOf(deleteFlag) != -1) {
                    int pos = document.getPosOfParagraph(paragraph);
                    document.removeBodyElement(pos);
                    document.removeBodyElement(pos);
                    return;
                }
            }
        }
    }

    /**
     * 合并列(只保留第一列数据)
     *
     * @param table
     * @param rowIndex
     *            哪一行索引需要合并列
     * @param fromCell
     *            从哪一列开始
     * @param toCell
     *            到哪一列结束
     */
    public static void mergeTableColumns(XWPFTable table, int rowIndex, int fromCell, int toCell) {
        for (int cellIndex = fromCell; cellIndex <= toCell; cellIndex++) {
            XWPFTableCell cell = table.getRow(rowIndex).getCell(cellIndex);
            if (cellIndex == fromCell) {
                cell.getCTTc().getTcPr().addNewHMerge().setVal(STMerge.RESTART);
            } else {
                cell.getCTTc().getTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
            }
        }
    }

    /**
     * 合并行(只保留第一行数据)
     *
     * @param table
     * @param col
     *            哪一列需要合并行
     * @param fromRow
     *            从哪一行开始
     * @param toRow
     *            到哪一行结束
     */
    public static void mergeTableRows(XWPFTable table, int col, int fromRow, int toRow) {
        for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
            XWPFTableRow row = table.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            XWPFTableCell cell = row.getCell(col);
            if (cell == null) {
                continue;
            }
            if (rowIndex == fromRow) {
                cell.getCTTc().getTcPr().addNewVMerge().setVal(STMerge.RESTART);
            } else {
                cell.getCTTc().getTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
            }
        }
    }

    /**
     * 找到指定的key的位置，替换key的内容为textList
     *
     * @param document
     * @param key
     *            替换标识
     * @param textList
     *            替换后的内容
     */
    public static void addText(XWPFDocument document, String key, List<String> textList) {

        List<XWPFParagraph> paragraphList = document.getParagraphs();

        if (paragraphList != null && paragraphList.size() > 0) {
            for (XWPFParagraph paragraph : paragraphList) {
                List<XWPFRun> runs = paragraph.getRuns();

                for (int i = 0; i < runs.size(); i++) {
                    XWPFRun run = runs.get(i);

                    String text = run.getText(0);
                    if (text != null) {
                        if (text.indexOf(key) >= 0) {

                            for (int j = 0; j < textList.size(); j++) {
                                if (j == 0) {
                                    run.setText(textList.get(j), 0);
                                    run.addBreak();
                                    run.addTab();
                                } else {
                                    run.setText(textList.get(j));
                                    run.addBreak();
                                    run.addTab();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * 在关键字后面创建表格（关键字后面必须留有空行）
     *
     * @param document
     * @param key
     *            关键字
     * @param tableTitle
     *            表格标题
     * @param tableUnit
     *            表格单位
     * @param tableDataList
     *            表格数据
     * @param tableWidth
     *            表格宽度
     * @param cellHeight
     *            单元格高度
     * @param fontSize
     *            表格数据字体大小
     * @param fontStyle
     *            表格数据字体样式
     */
    public static void createTable(XWPFDocument document, String key, String tableTitle, String tableUnit, List<List<Object>> tableDataList, Integer tableWidth, Integer cellHeight, Integer fontSize,
            String fontStyle) {

        List<XWPFParagraph> paragraphList = document.getParagraphs();

        if (paragraphList != null && paragraphList.size() > 0) {
            for (int i = 0; i < paragraphList.size(); i++) {

                XWPFParagraph paragraph = paragraphList.get(i);

                if (paragraph.getText().indexOf(key) >= 0) {
                    int ParagraphPosForFlag = document.getPosOfParagraph(paragraph);
                    XWPFParagraph paragraphAfterFlag = (XWPFParagraph) document.getBodyElements().get(ParagraphPosForFlag + 1);

                    document.insertNewParagraph(paragraphAfterFlag.getCTP().newCursor());// 空行，为后面创建表格提供条件

                    if (tableTitle != null) {
                        XWPFParagraph paragraphForTableTitle = document.insertNewParagraph(paragraphAfterFlag.getCTP().newCursor());
                        XWPFRun run = paragraphForTableTitle.createRun();
                        paragraphForTableTitle.setAlignment(ParagraphAlignment.CENTER); // 水平居中
                        run.addTab();
                        run.setFontSize(14);
                        run.setFontFamily("仿宋_GB2312");
                        run.setBold(true);
                        run.setText(tableTitle);
                    }

                    if (tableUnit != null) {

                        XWPFParagraph paragraphForUnitHead = document.insertNewParagraph(paragraphAfterFlag.getCTP().newCursor());
                        XWPFRun run = paragraphForUnitHead.createRun();
                        paragraphForUnitHead.setAlignment(ParagraphAlignment.RIGHT);
                        run.setFontSize(11);
                        run.setFontFamily("仿宋_GB2312");
                        run.setText(tableUnit);
                    }

                    XWPFTable table = document.insertNewTbl(paragraphAfterFlag.getCTP().newCursor());

                    // 创建初始表格
                    for (int j = 0; j < tableDataList.size(); j++) {// 几行
                        List<Object> rowDataList = tableDataList.get(j);// 几列
                        if (j == 0) {
                            XWPFTableRow row = table.getRow(0);
                            for (int k = 0; k < rowDataList.size() - 1; k++) {
                                row.addNewTableCell();
                            }
                        } else {
                            table.createRow();
                        }
                    }

                    // 表格有几列
                    int columnCount = tableDataList.get(0).size();

                    // 设置表格样式
                    if (tableWidth != null) {
                        setTableWidthAndHAlign(table, tableWidth, STJc.CENTER);
                    } else {
                        setTableWidthAndHAlign(table, 10600, STJc.CENTER);// 默认模板指定宽度
                    }
                    if (cellHeight != null) {
                        setTableHeight(table, cellHeight, STVerticalJc.CENTER);
                    }

                    // 为表格填充数据
                    for (int k = 0; k < tableDataList.size(); k++) {

                        XWPFTableRow row = table.getRow(k);
                        List<Object> rowDataList = tableDataList.get(k);

                        for (int n = 0; n < rowDataList.size(); n++) {

                            XWPFTableCell cell = row.getCell(n);

                            // 设置字体样式
                            XWPFParagraph paragraphForCell = cell.getParagraphs().get(0);
                            XWPFRun run = paragraphForCell.createRun();
                            run.setText(rowDataList.get(n).toString());
                            if (fontSize != null) {
                                run.setFontSize(fontSize);
                            } else {
                                run.setFontSize(11);
                            }
                            if (fontStyle != null) {
                                run.setFontFamily(fontStyle);
                            } else {
                                run.setFontFamily("仿宋_GB2312");
                            }

                            cell.setVerticalAlignment(XWPFVertAlign.CENTER); // 垂直居中
                            cell.getCTTc().getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);// 水平居中

                            cell.setWidthType(TableWidthType.DXA);// 宽度固定
                            cell.setWidth((getTableCTTblPr(table).getTblW().getW().intValue()) / columnCount + "");// 宽度
                        }
                    }
                }
            }

        }
    }

    public static void insertImageToTableCell(XWPFTable table, int rowIndex, int columnIndex, String picUrl) {
        if (StringUtils.isBlank(picUrl)) {
            return;
        }

        List<XWPFTableRow> rows = table.getRows();
        XWPFTableRow targetRow = rows.get(rowIndex);
        List<XWPFTableCell> cells = targetRow.getTableCells();
        XWPFTableCell targetCell = cells.get(columnIndex);
        List<XWPFParagraph> cellParagraphs = targetCell.getParagraphs();

        if (cellParagraphs.isEmpty()) {
            return;
        }

        XWPFParagraph paragraph = cellParagraphs.get(0);
        List<XWPFRun> runs = paragraph.getRuns();
        IntStream.range(0, runs.size()).forEach(i -> paragraph.removeRun(i));
        XWPFRun run = paragraph.createRun();
        try {
            File imageFile = new File(picUrl);
            if (imageFile.exists()) {
                run.addPicture(new FileInputStream(imageFile), Document.PICTURE_TYPE_PNG, new File(picUrl).getName(), Units.toEMU(360), Units.toEMU(200));
            }
        } catch (Exception ioe) {
            throw new FileException(ioe);
        }
    }

    public static void wirteDataToTable(XWPFTable table, List<List<Object>> list) {
        if (list.isEmpty()) {
            return;
        }

        // 获取第一行，复制表格样式，之后重新生成的新行与第一行样式一致
        XWPFTableRow sourceRow = table.getRow(0);

        List<XWPFTableCell> sourceRowCellList = sourceRow.getTableCells();
        if (sourceRowCellList.isEmpty()) {
            return;
        }

        // 原表格有几行数据，通常是表头
        int rowNum = table.getRows().size();

        IntStream.range(0, list.size()).forEach(i -> {
            List<Object> rowDataList = list.get(i);
            // 新增一行
            XWPFTableRow targetRow = table.insertNewTableRow(i + rowNum);
            // 复制行属性
            targetRow.getCtRow().setTrPr(sourceRow.getCtRow().getTrPr());
            // 创建单元格
            IntStream.range(0, sourceRowCellList.size()).forEach(j -> {
                XWPFTableCell targetCell = targetRow.addNewTableCell();
                XWPFTableCell sourceRowCell = sourceRowCellList.get(j);
                // 复制列属性
                targetCell.getCTTc().setTcPr(sourceRowCell.getCTTc().getTcPr());
                List<XWPFParagraph> sourceParagraphs = sourceRowCell.getParagraphs();

                if (sourceParagraphs.isEmpty()) {
                    targetCell.setText(sourceRowCell.getText());
                    return;
                }

                List<XWPFParagraph> targetParagraphs = targetCell.getParagraphs();
                // 复制段落属性
                targetParagraphs.get(0).getCTP().setPPr(sourceParagraphs.get(0).getCTP().getPPr());
                List<XWPFRun> sourceRuns = sourceParagraphs.get(0).getRuns();

                if (sourceRuns.isEmpty()) {
                    targetCell.setText(sourceRowCell.getText());
                    return;
                }

                XWPFRun sourceRun = sourceRuns.get(0);
                XWPFRun targetRun = targetParagraphs.get(0).createRun();
                targetRun.getCTR().setRPr(sourceRun.getCTR().getRPr());
                targetRun.setBold(false);

                if (!ObjectUtils.isEmpty(rowDataList.get(j))) {
                    targetRun.setText(rowDataList.get(j).toString());
                }
            });
        });
    }

    public static void createDynamicTable(XWPFDocument document, WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<XWPFParagraph> paragraphList = document.getParagraphs();

        if (paragraphList != null && paragraphList.size() > 0) {
            for (int i = 0; i < paragraphList.size(); i++) {

                XWPFParagraph paragraph = paragraphList.get(i);

                if (paragraph.getText().contains(dynamicTableDTO.getReplaceKeyword())) {
                    int paragraphPosForFlag = document.getPosOfParagraph(paragraph);
                    XWPFParagraph paragraphAfterFlag = (XWPFParagraph) document.getBodyElements().get(paragraphPosForFlag + 1);

                    // 空行，为后面创建表格提供条件
                    document.insertNewParagraph(paragraphAfterFlag.getCTP().newCursor());

                    if (dynamicTableDTO.getTableTitle() != null) {
                        XWPFParagraph paragraphForTableTitle = document.insertNewParagraph(paragraphAfterFlag.getCTP().newCursor());
                        XWPFRun run = paragraphForTableTitle.createRun();
                        // 设置段落文字水平居中
                        paragraphForTableTitle.setAlignment(ParagraphAlignment.CENTER);
                        run.addTab();
                        run.setFontSize(14);
                        run.setFontFamily(Optional.ofNullable(dynamicTableDTO.getFontStyle()).orElse(DEFAULT_FONT_FAMILY));
                        run.setBold(true);
                        run.setText(dynamicTableDTO.getTableTitle());
                    }

                    if (dynamicTableDTO.getTableUnit() != null) {

                        XWPFParagraph paragraphForUnitHead = document.insertNewParagraph(paragraphAfterFlag.getCTP().newCursor());
                        XWPFRun run = paragraphForUnitHead.createRun();
                        paragraphForUnitHead.setAlignment(ParagraphAlignment.RIGHT);
                        run.setFontSize(11);
                        run.setFontFamily(Optional.ofNullable(dynamicTableDTO.getFontStyle()).orElse(DEFAULT_FONT_FAMILY));
                        run.setText(dynamicTableDTO.getTableUnit());
                    }

                    XWPFTable table = document.insertNewTbl(paragraphAfterFlag.getCTP().newCursor());

                    List<List<Object>> tableDataList = dynamicTableDTO.getTableDataList();

                    // 创建初始表格
                    // 需要创建几行
                    for (int j = 0; j < tableDataList.size(); j++) {
                        // 几列
                        List<Object> rowDataList = tableDataList.get(j);
                        if (j == 0) {
                            XWPFTableRow row = table.getRow(0);
                            for (int k = 0; k < rowDataList.size() - 1; k++) {
                                row.addNewTableCell();
                            }
                        } else {
                            table.createRow();
                        }
                    }

                    // 表格有几列
                    int columnCount = tableDataList.get(0).size();

                    setTableWidthAndHAlign(table, Optional.ofNullable(dynamicTableDTO.getTableWidth()).orElse(10600), STJc.CENTER);

                    if (dynamicTableDTO.getCellHeight() != null) {
                        setTableHeight(table, dynamicTableDTO.getCellHeight(), STVerticalJc.CENTER);
                    }

                    // 表头
                    List<List<WordTemplateDynamicTableDTO.TableTitle>> tableTitleList = dynamicTableDTO.getTableTitleList();
                    IntStream.range(0, tableTitleList.size()).forEach(m -> {
                        XWPFTableRow row = table.getRow(m);
                        row.setRepeatHeader(true);
                        List<WordTemplateDynamicTableDTO.TableTitle> titleRowDataList = tableTitleList.get(m);
                        IntStream.range(0, titleRowDataList.size()).forEach(n -> {
                            XWPFTableCell cell = row.getCell(n);
                            // 设置字体样式
                            XWPFParagraph paragraphForCell = cell.getParagraphs().get(0);
                            XWPFRun run = paragraphForCell.createRun();
                            run.setText(titleRowDataList.get(n).getTitle());

                            if (titleRowDataList.get(n).isBold()) {
                                run.setBold(true);
                            }

                            run.setFontSize(Optional.ofNullable(dynamicTableDTO.getFontSize()).orElse(11));
                            run.setFontFamily(Optional.ofNullable(dynamicTableDTO.getFontStyle()).orElse(DEFAULT_FONT_FAMILY));

                            // 垂直居中
                            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                            // 水平居中
                            cell.getCTTc().getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);
                            // 宽度固定
                            cell.setWidthType(TableWidthType.DXA);
                            // 宽度均分
                            cell.setWidth((getTableCTTblPr(table).getTblW().getW().intValue()) / columnCount + "");
                        });
                    });

                    // 为表格填充数据
                    for (int k = tableTitleList.size(); k < tableDataList.size(); k++) {

                        XWPFTableRow row = table.getRow(k);
                        List<Object> rowDataList = tableDataList.get(k);

                        for (int n = 0; n < rowDataList.size(); n++) {

                            XWPFTableCell cell = row.getCell(n);

                            // 设置字体样式
                            XWPFParagraph paragraphForCell = cell.getParagraphs().get(0);
                            XWPFRun run = paragraphForCell.createRun();
                            run.setText(rowDataList.get(n).toString());
                            run.setFontSize(Optional.ofNullable(dynamicTableDTO.getFontSize()).orElse(11));
                            run.setFontFamily(Optional.ofNullable(dynamicTableDTO.getFontStyle()).orElse(DEFAULT_FONT_FAMILY));

                            // 垂直居中
                            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                            // 水平居中
                            cell.getCTTc().getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);
                            // 宽度固定
                            cell.setWidthType(TableWidthType.DXA);
                            // 宽度均分
                            cell.setWidth((getTableCTTblPr(table).getTblW().getW().intValue()) / columnCount + "");
                        }
                    }
                }
            }

        }
    }

    /**
     * 删除标记行（通过判断paragraph是否包含）
     *
     * @param document
     * @param key
     *            关键字
     */
    public static void removeKey(XWPFDocument document, String key) {
        List<XWPFParagraph> paragraphList = document.getParagraphs();

        if (paragraphList != null && paragraphList.size() > 0) {
            for (int i = 0; i < paragraphList.size(); i++) {

                XWPFParagraph paragraph = paragraphList.get(i);

                String text = paragraph.getText();
                if (text.indexOf(key) >= 0) {
                    document.removeBodyElement(document.getPosOfParagraph(paragraph));
                }
            }
        }
    }

    /**
     * 在指定表格后，空一行，然后复制一个表格（只复制第一行） 注意：原始表格后面需要留有空行！！！！！！
     *
     * @param document
     * @param tableIndex
     *            表格索引
     */
    public static void copyOneAterSourceTable(XWPFDocument document, int tableIndex, String tableTitle) {

        XWPFTable sourceTable = document.getTables().get(tableIndex);

        int posOfTable = document.getPosOfTable(sourceTable);
        XWPFParagraph paragraphAfterTable = (XWPFParagraph) document.getBodyElements().get(posOfTable + 1);

        XmlCursor cursorForParagraph = paragraphAfterTable.getCTP().newCursor();
        document.insertNewParagraph(cursorForParagraph);

        if (tableTitle != null) {
            XWPFParagraph paragraphForTableTitle = document.insertNewParagraph(paragraphAfterTable.getCTP().newCursor());
            XWPFRun run = paragraphForTableTitle.createRun();
            paragraphForTableTitle.setAlignment(ParagraphAlignment.CENTER); // 水平居中
            run.addTab();
            run.setFontSize(14);
            run.setFontFamily("黑体");
            run.setText(tableTitle);
        }

        XWPFTable targetTable = document.insertNewTbl(paragraphAfterTable.getCTP().newCursor());
        // 复制表格属性
        targetTable.getCTTbl().setTblGrid(sourceTable.getCTTbl().getTblGrid());
        targetTable.getCTTbl().setTblPr(sourceTable.getCTTbl().getTblPr());

        // 一个表格创建出来，已经有了一个单元格
        XWPFTableRow sourceRow = sourceTable.getRow(0);
        XWPFTableRow targetRow = targetTable.getRow(0);

        // 复制行属性
        targetRow.getCtRow().setTrPr(sourceRow.getCtRow().getTrPr());
        List<XWPFTableCell> cellList = sourceRow.getTableCells();
        if (null == cellList) {
            return;
        }

        for (int j = 0; j < cellList.size(); j++) {

            XWPFTableCell sourceCell = cellList.get(j);
            XWPFTableCell targetCell = null;
            if (j == 0) {
                targetCell = targetRow.getCell(0);
                targetCell.getCTTc().setTcPr(sourceCell.getCTTc().getTcPr());
                // 最左侧是虚线
                targetCell.getCTTc().getTcPr().addNewTcBorders().addNewLeft().setVal(STBorder.NIL);

            } else {
                targetCell = targetRow.addNewTableCell();
                targetCell.getCTTc().setTcPr(sourceCell.getCTTc().getTcPr());

            }

            // 最右侧是虚线
            if (j == cellList.size() - 1) {
                targetCell.getCTTc().getTcPr().addNewTcBorders().addNewRight().setVal(STBorder.NIL);
            }

            List<XWPFParagraph> sourceParagraphs = sourceCell.getParagraphs();

            for (int k = 0; k < sourceParagraphs.size(); k++) {
                XWPFParagraph sourceParagraph = sourceParagraphs.get(k);
                XWPFParagraph targetParagraph = null;
                if (k == 0) {
                    targetParagraph = targetCell.getParagraphs().get(0);

                } else {
                    targetParagraph = targetCell.addParagraph();
                }

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

    /**
     * 格式化文本中的表格数据中的所有单位（将html的上下标转化为word格式）
     *
     * @param document
     */
    public static void formatUnit(XWPFDocument document) {
        Pattern pattern = Pattern.compile("<(sup|sub).*?>([\\s\\S]*?)<(/sup|/sub)>");

        // 处理表格
        List<XWPFTable> tables = document.getTables();
        for (XWPFTable table : tables) {
            List<XWPFTableRow> rows = table.getRows();
            for (XWPFTableRow row : rows) {
                List<XWPFTableCell> cells = row.getTableCells();
                for (XWPFTableCell cell : cells) {

                    List<XWPFParagraph> cellParagraphs = cell.getParagraphs();
                    if (cellParagraphs != null && cellParagraphs.size() > 0) {
                        XWPFParagraph paragraph = cellParagraphs.get(0);

                        List<XWPFRun> runs = paragraph.getRuns();
                        if (runs != null && runs.size() > 0) {

                            for (int i = 0; i < runs.size(); i++) {

                                XWPFRun run = runs.get(i);

                                if (ObjectUtils.isEmpty(run.getText(0))) {
                                    continue;
                                }

                                String text = run.getText(0);
                                text = text.replaceAll("<(p|/p).*?>", ""); // 去掉p标签
                                run.setText(text, 0);
                                Matcher match = pattern.matcher(text);

                                while (match.find()) {

                                    String htmlKey = match.group();// 取的每个标签
                                    String keyWithoutHtml = htmlKey.replaceAll("<.*?>", "");
                                    XWPFRun originalRun = runs.get(runs.size() - 1);
                                    boolean bold = originalRun.isBold();
                                    paragraph.removeRun(paragraph.getRuns().size() - 1);// 移除未格式化的初始单位信息
                                    String[] textArray = text.split(htmlKey);
                                    List<String> textList = new ArrayList<>(Arrays.asList(textArray));
                                    textList.add(1, keyWithoutHtml);
                                    for (int k = 0; k < textList.size(); k++) {
                                        XWPFRun newRun = paragraph.createRun();
                                        newRun.setText(textList.get(k));
                                        newRun.setFontSize(11);
                                        newRun.setFontFamily("宋体");
                                        newRun.setBold(bold);
                                        if (k == 1) {
                                            if (htmlKey.indexOf("sup") >= 0) {
                                                newRun.setSubscript(VerticalAlign.SUPERSCRIPT);
                                            } else if (htmlKey.indexOf("sub") >= 0) {
                                                newRun.setSubscript(VerticalAlign.SUBSCRIPT);
                                            }
                                        }
                                    }
                                    //text = textArray[1];// 取下一个带标签的文本
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 设置表格总宽度与水平对齐方式
     *
     * @param table
     * @param width
     *            宽度
     * @param enumValue
     */
    public static void setTableWidthAndHAlign(XWPFTable table, int width, STJc.Enum enumValue) {
        CTTblPr tblPr = getTableCTTblPr(table);
        // 设置：自动重调尺寸以适应内容为：false
        tblPr.addNewTblLayout().setType(STTblLayoutType.FIXED);
        // 表格宽度
        CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
        CTTblBorders tblBorders = tblPr.isSetTblBorders() ? tblPr.getTblBorders() : tblPr.addNewTblBorders();
        if (enumValue != null) {
            CTJc cTJc = tblPr.addNewJc();
            cTJc.setVal(enumValue);
        }
        // 设置宽度
        tblWidth.setW(BigInteger.valueOf(width));
        tblWidth.setType(STTblWidth.DXA);
        //tblWidth.setType(STTblWidth.AUTO);

        BigInteger borderSize = new BigInteger("12");
        tblBorders.getTop().setSz(borderSize);
        tblBorders.getBottom().setSz(borderSize);
        tblBorders.getLeft().setSz(borderSize);
        tblBorders.getRight().setSz(borderSize);

    }

    /**
     * 设置表格行高
     *
     * @param infoTable
     * @param heigth
     *            高度
     * @param vertical
     *            表格内容的显示方式：居中、靠右...
     */
    public static void setTableHeight(XWPFTable table, int heigth, STVerticalJc.Enum vertical) {
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

    /**
     * 得到Table的CTTblPr,不存在则新建
     *
     * @param table
     * @return
     */
    public static CTTblPr getTableCTTblPr(XWPFTable table) {
        return Optional.ofNullable(table.getCTTbl().getTblPr()).orElseGet(() -> table.getCTTbl().addNewTblPr());
    }

    // 替换文本数据
    private static void replaceRunText(Map<String, Object> dataMap, XWPFRun run) {
        String text = run.getText(0);
        if (text != null) {
            boolean isSetText = false;
            for (Entry<String, Object> entry : dataMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().toString();
                if (text.indexOf(key) != -1) {
                    isSetText = true;
                    text = text.replaceAll(key, value);
                }
                if (isSetText) {
                    run.setText(text, 0);
                }
            }
        }
    }
}
