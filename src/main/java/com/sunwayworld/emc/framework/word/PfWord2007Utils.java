package com.sunwayworld.emc.framework.word;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TableWidthType;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;

import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhengxy@sunwayworld.com 2021年06月23日
 */
public class PfWord2007Utils extends Word2007Utils {

    private static final String DEFAULT_FONT_FAMILY = "Times New Roman";
    private static final int DEFAULT_FONT_SIZE = 12;
    private static final int DEFAULT_TABLE_FONT_SIZE = 10;
    private static final int DEFAULT_REMARK_FONT_SIZE = 11;

    private static final Pattern SUP_SUB_PATTERN = Pattern.compile("<(sup|sub).*?>([\\s\\S]*?)<(/sup|/sub)>");

    public static void createDynamicTable(XWPFDocument document, WordTemplateDynamicTableDTO dynamicTableDTO) {
        createDynamicTable(document, dynamicTableDTO, ParagraphAlignment.CENTER);
    }

    public static void createDynamicTable(XWPFDocument document, WordTemplateDynamicTableDTO dynamicTableDTO, ParagraphAlignment titleAlign) {
        List<XWPFParagraph> paragraphList = document.getParagraphs();
        if (paragraphList.isEmpty()) {
            return;
        }

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
                    paragraphForTableTitle.setAlignment(titleAlign);
                    run.setFontSize(DEFAULT_FONT_SIZE);
                    run.setFontFamily(Optional.ofNullable(dynamicTableDTO.getFontStyle()).orElse(DEFAULT_FONT_FAMILY));
                    run.setBold(true);
                    run.setText(dynamicTableDTO.getTableTitle());
                }

                if (Objects.nonNull(dynamicTableDTO.getTableSubTitle())) {
                    XWPFParagraph paragraphForTableTitle = document.insertNewParagraph(paragraphAfterFlag.getCTP().newCursor());
                    XWPFRun run = paragraphForTableTitle.createRun();
                    // 设置段落文字水平居中
                    paragraphForTableTitle.setAlignment(titleAlign);
                    run.setFontSize(DEFAULT_FONT_SIZE);
                    run.setFontFamily(Optional.ofNullable(dynamicTableDTO.getFontStyle()).orElse(DEFAULT_FONT_FAMILY));
                    run.setBold(true);
                    run.setText(dynamicTableDTO.getTableSubTitle());
                }

                if (dynamicTableDTO.getTableUnit() != null) {

                    XWPFParagraph paragraphForUnitHead = document.insertNewParagraph(paragraphAfterFlag.getCTP().newCursor());
                    XWPFRun run = paragraphForUnitHead.createRun();
                    paragraphForUnitHead.setAlignment(ParagraphAlignment.RIGHT);
                    run.setFontSize(DEFAULT_TABLE_FONT_SIZE);
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

                //setTableWidthAndHAlign(table, Optional.ofNullable(dynamicTableDTO.getTableWidth()).orElse(10600), STJc.CENTER);
                setTableWidthAndBorder(table, dynamicTableDTO, STJc.CENTER);

                if (dynamicTableDTO.getCellHeight() != null) {
                    setTableHeight(table, dynamicTableDTO.getCellHeight(), STVerticalJc.CENTER);
                }

                // 表头
                List<List<WordTemplateDynamicTableDTO.TableTitle>> tableTitleList = dynamicTableDTO.getTableTitleList();
                // 表格列宽度
                List<Integer> columnWidthList = dynamicTableDTO.getColumnWidthList();
                int tableWidth = getTableCTTblPr(table).getTblW().getW().intValue();
                List<Integer> actualColumnWidthList = calcColumnWidth(columnWidthList, columnCount, tableWidth);

                IntStream.range(0, tableTitleList.size()).forEach(m -> {
                    XWPFTableRow row = table.getRow(m);
                    row.setRepeatHeader(dynamicTableDTO.getRepeatHeader());
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

                        run.setFontSize(Optional.ofNullable(dynamicTableDTO.getFontSize()).orElse(DEFAULT_TABLE_FONT_SIZE));
                        run.setFontFamily(Optional.ofNullable(dynamicTableDTO.getFontStyle()).orElse(DEFAULT_FONT_FAMILY));

                        // 垂直居中
                        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                        // 水平居中
                        cell.getCTTc().getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);
                        // 宽度固定
                        cell.setWidthType(TableWidthType.DXA);
                        // 设置列宽度
                        cell.setWidth(String.valueOf(actualColumnWidthList.get(n)));

                        setCellBorderStyle(cell, dynamicTableDTO.getShowCellBorder());
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

                        String cellText = rowDataList.get(n).toString();
                        String[] runTexts = cellText.split("\n");
                        if (runTexts.length > 1) {
                            // 需要控制换行
                            IntStream.range(0, runTexts.length).forEach(index -> {
                                XWPFParagraph newParagraph = (index == 0) ? paragraphForCell :  cell.addParagraph();
                                XWPFRun run = newParagraph.createRun();
                                run.setText(runTexts[index]);

                                run.setFontSize(Optional.ofNullable(dynamicTableDTO.getFontSize()).orElse(DEFAULT_TABLE_FONT_SIZE));
                                run.setFontFamily(Optional.ofNullable(dynamicTableDTO.getFontStyle()).orElse(DEFAULT_FONT_FAMILY));
                            });
                        } else {
                            XWPFRun run = paragraphForCell.createRun();
                            run.setText(cellText);

                            run.setFontSize(Optional.ofNullable(dynamicTableDTO.getFontSize()).orElse(DEFAULT_TABLE_FONT_SIZE));
                            run.setFontFamily(Optional.ofNullable(dynamicTableDTO.getFontStyle()).orElse(DEFAULT_FONT_FAMILY));
                        }

                        // 垂直居中
                        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                        // 水平居中
                        cell.getCTTc().getPList().forEach(pl -> pl.addNewPPr().addNewJc().setVal(STJc.CENTER));
                        // 宽度固定
                        cell.setWidthType(TableWidthType.DXA);
                        // 设置列宽度
                        cell.setWidth(String.valueOf(actualColumnWidthList.get(n)));

                        setCellBorderStyle(cell, dynamicTableDTO.getShowCellBorder());
                    }
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
                                Matcher matcher = SUP_SUB_PATTERN.matcher(text);

                                // 替换上下标
                                List<String> splitStrList = new ArrayList<>();
                                splitStrList.add(text);
                                String matchedWord = "";
                                int matchedCount = 0;
                                while (matcher.find()) {
                                    String group = matcher.group();
                                    List<String> arrList = new ArrayList<>();
                                    splitStrList.forEach(str -> {
                                        if (StringUtils.isEmpty(str) || !StringUtils.contains(str, group)) {
                                            arrList.add(str);
                                            return;
                                        }
                                        List<String> tempList = new ArrayList<>(Arrays.asList(str.split(group)));
                                        tempList.add(tempList.isEmpty() ? 0 : 1, group);
                                        arrList.addAll(tempList);
                                    });
                                    splitStrList.clear();
                                    splitStrList.addAll(arrList);
                                    matchedWord = group;
                                    matchedCount++;
                                }

                                // 如果文本中出现多个上下标，并且有上下标位于文本结尾，分割后的数组需要单独添加
                                if (!StringUtils.isBlank(matchedWord) && matchedCount > 1 && text.endsWith(matchedWord)) {
                                    splitStrList.add(matchedWord);
                                }

                                if (splitStrList.size() > 1 && !StringUtils.equals(splitStrList.get(0), text)) {
                                    XWPFRun originalRun = runs.get(runs.size() - 1);
                                    boolean bold = originalRun.isBold();
                                    paragraph.removeRun(paragraph.getRuns().size() - 1);
                                    splitStrList.stream().filter(str -> !StringUtils.isEmpty(str)).forEach(str -> {
                                        XWPFRun newRun = paragraph.createRun();
                                        newRun.setText(str.replaceAll("<.*?>", ""));
                                        newRun.setFontSize(DEFAULT_TABLE_FONT_SIZE);
                                        newRun.setFontFamily(DEFAULT_FONT_FAMILY);
                                        newRun.setBold(bold);
                                        if (str.contains("sup")) {
                                            newRun.setSubscript(VerticalAlign.SUPERSCRIPT);
                                        } else if (str.contains("sub")) {
                                            newRun.setSubscript(VerticalAlign.SUBSCRIPT);
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }
        }

        // 处理段落
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (XWPFParagraph paragraph : paragraphs) {
            if (!SUP_SUB_PATTERN.matcher(paragraph.getText()).find()) {
                continue;
            }

            List<XWPFRun> runs = paragraph.getRuns();
            if (Objects.isNull(runs) || runs.isEmpty()) {
                return;
            }

            int runSize = runs.size();
            IntStream.range(0, runSize).forEach(index -> {
                XWPFRun run = runs.get(index);
                formatSubSup(paragraph, run);
            });
        }
    }

    public static void formatSubSup(XWPFParagraph paragraph, XWPFRun run) {
        String text = run.getText(0);
        if (Objects.isNull(text)) {
            return;
        }

        Matcher matcher = SUP_SUB_PATTERN.matcher(text);
        // 替换上下标
        List<String> splitStrList = new ArrayList<>();
        splitStrList.add(text);
        String matchedWord = "";
        int matchedCount = 0;
        while (matcher.find()) {
            String group = matcher.group();
            List<String> arrList = new ArrayList<>();
            splitStrList.forEach(str -> {
                if (StringUtils.isEmpty(str) || !StringUtils.contains(str, group)) {
                    arrList.add(str);
                    return;
                }
                List<String> tempList = new ArrayList<>(Arrays.asList(str.split(group)));
                tempList.add(tempList.isEmpty() ? 0 : 1, group);
                arrList.addAll(tempList);
            });
            splitStrList.clear();
            splitStrList.addAll(arrList);
            matchedWord = group;
            matchedCount++;
        }

        // 如果文本中出现多个上下标，并且有上下标位于文本结尾，分割后的数组需要单独添加
        if (!StringUtils.isBlank(matchedWord) && matchedCount > 1 && text.endsWith(matchedWord)) {
            splitStrList.add(matchedWord);
        }

        if (splitStrList.size() > 1 && !StringUtils.equals(splitStrList.get(0), text)) {
            boolean bold = run.isBold();
            paragraph.removeRun(paragraph.getRuns().size() - 1);
            splitStrList.stream().filter(str -> !StringUtils.isEmpty(str)).forEach(str -> {
                XWPFRun newRun = paragraph.createRun();
                newRun.setText(str.replaceAll("<.*?>", ""));
                newRun.setFontSize(DEFAULT_REMARK_FONT_SIZE);
                newRun.setFontFamily(DEFAULT_FONT_FAMILY);
                newRun.setBold(bold);
                if (str.contains("sup")) {
                    newRun.setSubscript(VerticalAlign.SUPERSCRIPT);
                } else if (str.contains("sub")) {
                    newRun.setSubscript(VerticalAlign.SUBSCRIPT);
                }
            });
        }
    }

    public static void insertImageOfKey(String key, XWPFDocument document, String picUrl, int width, int height) {
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
                run.addPicture(new FileInputStream(picUrl), Document.PICTURE_TYPE_PNG, new File(picUrl).getName(), Units.toEMU(width), Units.toEMU(height)); // 100x50pixels
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
                        run.addPicture(new FileInputStream(picUrl), Document.PICTURE_TYPE_PNG, new File(picUrl).getName(), Units.toEMU(width), Units.toEMU(height)); // 100x50pixels
                    } catch (Exception ioe) {
                        throw new FileException(ioe);
                    }
                });
            }));
        });
    }

    public static void setTableWidthAndBorder(XWPFTable table, WordTemplateDynamicTableDTO dynamicTableDTO, STJc.Enum enumValue) {
        CTTblPr tblPr = getTableCTTblPr(table);
        // 设置：自动重调尺寸以适应内容为：false
        tblPr.addNewTblLayout().setType(STTblLayoutType.FIXED);
        // 表格宽度
        CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
        if (enumValue != null) {
            CTJc cTJc = tblPr.addNewJc();
            cTJc.setVal(enumValue);
        }
        // 设置宽度
        Integer width = Optional.ofNullable(dynamicTableDTO.getTableWidth()).orElse(10204);
        tblWidth.setW(BigInteger.valueOf(width));
        tblWidth.setType(STTblWidth.DXA);

        CTTblBorders tblBorders = tblPr.isSetTblBorders() ? tblPr.getTblBorders() : tblPr.addNewTblBorders();
        if (dynamicTableDTO.getShowBorder()) {
            Integer tableBorderWidth = dynamicTableDTO.getTableBorderWidth();
            BigInteger borderSize = new BigInteger(Objects.isNull(tableBorderWidth) ? "12" : tableBorderWidth.toString());
            tblBorders.getTop().setSz(borderSize);
            tblBorders.getBottom().setSz(borderSize);
            tblBorders.getLeft().setVal(STBorder.NIL);
            tblBorders.getRight().setVal(STBorder.NIL);
        } else {
            tblBorders.getTop().setVal(STBorder.NIL);
            tblBorders.getBottom().setVal(STBorder.NIL);
            tblBorders.getLeft().setVal(STBorder.NIL);
            tblBorders.getRight().setVal(STBorder.NIL);
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
        IntStream.range(0, runs.size()).forEach(paragraph::removeRun);
        XWPFRun run = paragraph.createRun();
        try {
            File imageFile = new File(picUrl);
            if (imageFile.exists()) {
                BufferedImage srcImage = ImageIO.read(imageFile);
                int width = srcImage.getWidth();
                int height = srcImage.getHeight();
                int maxWidth = Math.min(250, width);
                int maxHeight = Double.valueOf((double) maxWidth / width * height).intValue();
                run.addPicture(new FileInputStream(imageFile), Document.PICTURE_TYPE_JPEG, new File(picUrl).getName(), Units.pixelToEMU(maxWidth), Units.pixelToEMU(maxHeight));
            }
        } catch (Exception ioe) {
            throw new FileException(ioe);
        }
    }

    @SuppressWarnings("unused")
	private static void setTableTitle(XWPFDocument document, WordTemplateDynamicTableDTO dynamicTableDTO, XWPFParagraph paragraphAfterFlag) {
        if (dynamicTableDTO.getTableTitle() == null) {
            return;
        }

        XWPFParagraph paragraphForTableTitle = document.insertNewParagraph(paragraphAfterFlag.getCTP().newCursor());
        XWPFRun run = paragraphForTableTitle.createRun();
        // 设置段落文字水平居中
        paragraphForTableTitle.setAlignment(ParagraphAlignment.LEFT);
        run.setFontSize(12);
        run.setFontFamily(Optional.ofNullable(dynamicTableDTO.getFontStyle()).orElse(DEFAULT_FONT_FAMILY));
        run.setBold(true);
        run.setText(dynamicTableDTO.getTableTitle());
    }

    @SuppressWarnings("unused")
	private static void setTableUnit(XWPFDocument document, WordTemplateDynamicTableDTO dynamicTableDTO, XWPFParagraph paragraphAfterFlag) {
        if (dynamicTableDTO.getTableUnit() == null) {
            return;
        }

        XWPFParagraph paragraphForUnitHead = document.insertNewParagraph(paragraphAfterFlag.getCTP().newCursor());
        XWPFRun run = paragraphForUnitHead.createRun();
        paragraphForUnitHead.setAlignment(ParagraphAlignment.RIGHT);
        run.setFontSize(11);
        run.setFontFamily(Optional.ofNullable(dynamicTableDTO.getFontStyle()).orElse(DEFAULT_FONT_FAMILY));
        run.setText(dynamicTableDTO.getTableUnit());
    }

    private static List<Integer> calcColumnWidth(List<Integer> columnWidthList, int columnCount, int tableWidth) {
        List<Integer> actualColumnWidthList = new ArrayList<>(columnCount);

        // 如果未设置表格列宽度，或者设置的列宽度数小于实际列数量，则均分列表
        if (columnWidthList.isEmpty() || columnWidthList.size() < columnCount) {
            int defaultAvgWidth = tableWidth / columnCount;
            IntStream.range(0, columnCount).forEach(index -> actualColumnWidthList.add(defaultAvgWidth));

            return actualColumnWidthList;
        }

        // 如果都设置了宽度且宽度大于0，则直接使用设置的宽度
        boolean allMatched = columnWidthList.stream().allMatch(width -> width > 0);
        if (allMatched) {
            actualColumnWidthList.addAll(columnWidthList);

            return actualColumnWidthList;
        }

        // 计算设置了有效宽度的列数
        long fixedWidthCount = columnWidthList.stream().filter(width -> width > 0).count();
        Integer totalFixedWidth = columnWidthList.stream().filter(width -> width > 0).reduce(Integer::sum).orElse(0);
        long defaultWidthCount = columnWidthList.stream().filter(width -> width == 0).count();

        long defaultWidth = (tableWidth - totalFixedWidth) / Math.min(defaultWidthCount, columnCount - fixedWidthCount);
        columnWidthList.forEach(width -> actualColumnWidthList.add((width == 0) ? Long.valueOf(defaultWidth).intValue() : width));

        return actualColumnWidthList;
    }

    private static void setCellBorderStyle(XWPFTableCell cell, boolean showCellBorder) {
        if (showCellBorder) {
            return;
        }

        CTTcPr tcPr = cell.getCTTc().getTcPr();
        CTTcBorders tcBorders = Optional.ofNullable(tcPr.getTcBorders()).orElseGet(tcPr::addNewTcBorders);
        tcBorders.addNewTop().setVal(STBorder.NIL);
        tcBorders.addNewBottom().setVal(STBorder.NIL);
        tcBorders.addNewRight().setVal(STBorder.NIL);
        tcBorders.addNewLeft().setVal(STBorder.NIL);
    }
}
