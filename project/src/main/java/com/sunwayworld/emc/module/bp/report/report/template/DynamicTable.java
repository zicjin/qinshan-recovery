package com.sunwayworld.emc.module.bp.report.report.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TableRowAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.word.PfWord2007Utils;
import com.sunwayworld.emc.framework.word.Word2007Utils;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.constant.EmcReportConstant;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhengxy@sunwayworld.com 2020年09月02日
 */
public interface DynamicTable {

    /**
     * 获取Word文档对象
     * @return
     */
    XWPFDocument getDocument();

    /**
     * 获取表格标题
     * @return
     */
    String getTableTitle();

    /**
     * 获取表格序号
     * @return
     */
    int getTableIndex();

    /**
     * 是否为最后一个表格
     * @return
     */
    boolean isLast();

    /**
     * 构建表格头数据
     * @param dynamicTableDTO
     * @return
     */
    List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO);

    /**
     * 构建表格体数据
     * @param dynamicTableDTO
     * @return
     */
    List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO);

    default String getTableEndText() {
        return PfConstant.TABLE_END_TIP_MESSAGE;
    }

    default String getTableSubTitle() {
        return null;
    }

    /**
     * 获取动态表格下标，默认为4（不同的模板不同）
     * @return
     */
    default int getDynamicTableIndex() {
        return 5;
    }

    /**
     * 获取动态表格替换关键字，默认为 resultTables
     * @return
     */
    default String getReplaceKeyword() { return EmcConstant.DYNAMIC_TABLE_PLACEHOLDER; }

    /**
     * 获取表格宽度，默认为 17.5cm = 9921, 15.03=8520， 16.93=9598
     * @return
     */
    default int getTableWidth() {
        return 9598;
    }

    /**
     * 获取单元格高度
     * @return
     */
    default int getCellHeight() {
        return 400;
    }

    /**
     * 获取表格列宽列表，默认不设置自动均分列表
     * @return
     */
    default List<Integer> getColumnWidthList() {
        return Collections.emptyList();
    }

    /**
     * 获取表格配置参数
     * @return
     */
    default WordTemplateDynamicTableDTO getDynamicTableDto() {
        return new WordTemplateDynamicTableDTO();
    }

    /**
     * 获取表格标题的对齐方式，默认左对齐
     * @return
     */
    default ParagraphAlignment getTitleAlignment() {
        return ParagraphAlignment.LEFT;
    }

    /**
     * 设置表格标题
     * @param headerTitles
     * @param headerColumnQty
     * @param headerDataList
     * @param dynamicTableDTO
     */
    default void setTableTitle(String[] headerTitles, int headerColumnQty, List<List<Object>> headerDataList, WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<String> headerTitleList = Arrays.asList(headerTitles);
        int tableHeaderRowQty = headerTitles.length / headerColumnQty;
        List<List<WordTemplateDynamicTableDTO.TableTitle>> tableTitleList = new ArrayList<>(tableHeaderRowQty);

        IntStream.range(0, tableHeaderRowQty).forEach(i -> {
            List<WordTemplateDynamicTableDTO.TableTitle> tableTitleRowList = new ArrayList<>();
            List<String> definedRowTitleList = headerTitleList.subList(i * headerColumnQty, (i + 1) * headerColumnQty);
            List<Object> rowTitleList = headerDataList.get(i);
            if (definedRowTitleList.size() > rowTitleList.size()) {
                return;
            }

            IntStream.range(0, definedRowTitleList.size()).forEach(m -> {
                if (!StringUtils.isBlank(definedRowTitleList.get(m)) && !definedRowTitleList.get(m).startsWith(EmcConstant.TABLE_HEADER_DYNAMIC_TITLE_PLACEHOLDER_PREFIX)) {
                    tableTitleRowList.add(new WordTemplateDynamicTableDTO.TableTitle(definedRowTitleList.get(m), true));
                } else {
                    tableTitleRowList.add(new WordTemplateDynamicTableDTO.TableTitle(rowTitleList.get(m).toString(), false));
                }
            });

            tableTitleList.add(tableTitleRowList);
        });

        dynamicTableDTO.setTableTitleList(tableTitleList);
    }

    /**
     * 构建动态表格头数据
     * @param headerTitles
     * @param headerColumnQty
     * @param dynamicTitleList
     * @return
     */
    default List<List<Object>> buildTableDynamicHeader(String[] headerTitles, int headerColumnQty, List<String> dynamicTitleList) {
        int tableHeaderRowQty = headerTitles.length / headerColumnQty;
        List<List<Object>> tableHeaderDataList = new ArrayList<>(tableHeaderRowQty);
        // 行数
        IntStream.range(0, tableHeaderRowQty).forEach(i -> {
            List<Object> rowDataList = new ArrayList<>(headerColumnQty);
            // 列数
            IntStream.range(i * headerColumnQty, (i + 1) * headerColumnQty).forEach(m -> {
                String title = headerTitles[m];
                if (!CollectionUtils.isEmpty(dynamicTitleList) && title.startsWith(EmcConstant.TABLE_HEADER_DYNAMIC_TITLE_PLACEHOLDER_PREFIX)) {
                    // 去掉占位符前缀
                    int dynamicTitleIndex = NumberUtils.parseInt(title.replaceAll(EmcConstant.TABLE_HEADER_DYNAMIC_TITLE_PLACEHOLDER_PREFIX, ""));
                    if (dynamicTitleIndex >= dynamicTitleList.size()) {
                        return;
                    }

                    title = dynamicTitleList.get(dynamicTitleIndex);
                }
                rowDataList.add(title);
            });
            tableHeaderDataList.add(rowDataList);
        });
        WordTemplateUtils.listListNullToString(tableHeaderDataList, PfConstant.NULL_REPLACED_VALUE);

        return tableHeaderDataList;
    }

    /**
     * 构建备注行数据
     * @param remark
     * @param totalColumnQty
     * @return
     */
    default List<Object> buildRemarkRowDataList(String remark, int totalColumnQty) {
        return buildDynamicRowDataList(EmcConstant.REMARK, remark, totalColumnQty);
    }

    /**
     * 构建动态行数据
     * @param title
     * @param content
     * @param totalColumnQty
     * @return
     */
    default List<Object> buildDynamicRowDataList(String title, String content, int totalColumnQty) {
        List<Object> dynamicRowDataList = new ArrayList<>();
        dynamicRowDataList.add(title);
        dynamicRowDataList.add(content);
        IntStream.range(2, totalColumnQty).forEach(i -> dynamicRowDataList.add(""));

        return dynamicRowDataList;
    }

    /**
     * 构建表格数据
     * @return
     */
    default WordTemplateDynamicTableDTO buildTable() {
        WordTemplateDynamicTableDTO dynamicTableDTO = getDynamicTableDto();

        dynamicTableDTO.setFontStyle(EmcReportConstant.DEFAULT_FONT_FAMILY);
        List<List<Object>> tableDataList = new ArrayList<>();
        tableDataList.addAll(buildTableHeader(dynamicTableDTO));
        tableDataList.addAll(buildTableBody(dynamicTableDTO));
        WordTemplateUtils.listListNullToString(tableDataList, PfConstant.NULL_REPLACED_VALUE);

        dynamicTableDTO.setTableTitle(getTableTitle());
        dynamicTableDTO.setTableDataList(tableDataList);
        dynamicTableDTO.setReplaceKeyword(getReplaceKeyword());
        dynamicTableDTO.setTableSubTitle(getTableSubTitle());

        return dynamicTableDTO;
    }

    /**
     * 创建表格
     */
    default void create() {
        WordTemplateDynamicTableDTO dynamicTableDTO = buildTable();
        dynamicTableDTO.setTableWidth(getTableWidth());
        dynamicTableDTO.setCellHeight(getCellHeight());
        dynamicTableDTO.setColumnWidthList(getColumnWidthList());
        PfWord2007Utils.createDynamicTable(getDocument(), dynamicTableDTO, getTitleAlignment());

        int tableSize = getDocument().getTables().size();
        if (tableSize <= getDynamicTableIndex()) {
            return;
        }

        XWPFTable table = getDocument().getTables().get(getDynamicTableIndex());
        Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).ifPresent(list -> list.forEach(c -> Word2007Utils.mergeTableColumns(table, c.getRowIndex(), c.getStartColumnIndex(), c.getEndColumnIndex())));
        Optional.ofNullable(dynamicTableDTO.getMergedRowList()).ifPresent(list -> list.forEach(r -> Word2007Utils.mergeTableRows(table, r.getColumnIndex(), r.getStartRowIndex(), r.getEndRowIndex())));

        // 插入图片
        Optional.ofNullable(dynamicTableDTO.getTableImageList()).ifPresent(tableImageList -> tableImageList.forEach(i -> PfWord2007Utils.insertImageToTableCell(table, i.getRowIndex(), i.getColumnIndex(), i.getImageUrl())));

        // 添加表格备注
        List<String> tableRemarkList = dynamicTableDTO.getTableRemarkList();
        boolean showTableRemark = tableRemarkList != null && !tableRemarkList.isEmpty();
        if (showTableRemark) {
            createTableRemark(getDocument(), table, tableRemarkList, true);
        }

        if (!isLast()) {
            createTableEndText(getDocument(), table, showTableRemark ? tableRemarkList.size() : 0);
        }
    }

    /**
     * 创建表格结尾说明文字
     * @param document
     * @param targetTable
     */
    default void createTableEndText(XWPFDocument document, XWPFTable targetTable, int cursorOffset) {
        int posOfTable = document.getPosOfTable(targetTable);
        XWPFParagraph paragraphAfterFlag = (XWPFParagraph) document.getBodyElements().get(posOfTable + 1 + cursorOffset);
        document.insertNewParagraph(paragraphAfterFlag.getCTP().newCursor());
        // 每创建一个结果表，重新换一页
        //createEndTableText(document, paragraphAfterFlag);
        createEndText(document, paragraphAfterFlag);

        document.insertNewParagraph(paragraphAfterFlag.getCTP().newCursor()).createRun().addBreak(BreakType.PAGE);
        // 此处添加换页标记，便于最后移除空行
        document.insertNewParagraph(paragraphAfterFlag.getCTP().newCursor()).createRun().setText(EmcConstant.NEW_PAGE_START_PLACEHOLDER);
    }

    /**
     * 利用文本段落创建结束文字
     * @param document
     * @param paragraphAfterFlag
     */
    default void createEndText(XWPFDocument document, XWPFParagraph paragraphAfterFlag) {
        // 创建表格结束段落
        XWPFParagraph endParagraph = document.insertNewParagraph(paragraphAfterFlag.getCTP().newCursor());
        endParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun endRun = endParagraph.createRun();
        endRun.setFontSize(11);
        endRun.setFontFamily(EmcReportConstant.DEFAULT_FONT_FAMILY);
        endRun.setText(getTableEndText());
    }

    /**
     * 利用表格创建结束文字
     * @param document
     * @param paragraphAfterFlag
     */
    default void createEndTableText(XWPFDocument document, XWPFParagraph paragraphAfterFlag) {
        XWPFTable endTable = document.insertNewTbl(paragraphAfterFlag.getCTP().newCursor());
        endTable.setWidth(XWPFTable.DEFAULT_PERCENTAGE_WIDTH);
        endTable.setTableAlignment(TableRowAlign.CENTER);
        endTable.setLeftBorder(XWPFTable.XWPFBorderType.NIL, 0, 0, null);
        endTable.setRightBorder(XWPFTable.XWPFBorderType.NIL, 0, 0, null);
        endTable.setBottomBorder(XWPFTable.XWPFBorderType.NIL, 0, 0, null);
        XWPFTableCell endCell = endTable.getRow(0).getCell(0);
        endCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        endCell.getCTTc().getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);
        endCell.setText(EmcConstant.TABLE_END_TIP_MESSAGE);
    }

    /**
     * 创建表格备注
     * @param document 文档备注
     * @param targetTable 目标表格
     * @param tableRemarks 表格备注
     */
    default void createTableRemark(XWPFDocument document, XWPFTable targetTable, List<String> tableRemarks) {
        int posOfTable = document.getPosOfTable(targetTable);
        XWPFParagraph paragraphAfterFlag = (XWPFParagraph) document.getBodyElements().get(posOfTable + 1);

        XWPFParagraph endParagraph = document.insertNewParagraph(paragraphAfterFlag.getCTP().newCursor());
        endParagraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun endRun = endParagraph.createRun();
        endRun.setFontSize(11);
        endRun.setFontFamily(EmcReportConstant.DEFAULT_FONT_FAMILY);

        List<String> indexTableRemarks = new ArrayList<>(tableRemarks.size());
        IntStream.range(0, tableRemarks.size()).forEach(index -> indexTableRemarks.add((index + 1) + "、" + tableRemarks.get(index)));
        endRun.setText(PfConstant.TABLE_REMARK_TITLE + String.join("；", indexTableRemarks) + "。");
    }

    /**
     * 创建表格备注
     * @param document 文档对象
     * @param targetTable 模板表格
     * @param tableRemarks 表格备注
     * @param breakLine 是否换行
     */
    default void createTableRemark(XWPFDocument document, XWPFTable targetTable, List<String> tableRemarks, boolean breakLine) {
        int posOfTable = document.getPosOfTable(targetTable);
        XWPFParagraph paragraphAfterFlag = (XWPFParagraph) document.getBodyElements().get(posOfTable + 1);

        List<String> indexTableRemarks = new ArrayList<>(tableRemarks.size());
        IntStream.range(0, tableRemarks.size()).forEach(index -> indexTableRemarks.add((index + 1) + "、" + tableRemarks.get(index)));
        if (breakLine) {
            IntStream.range(0, tableRemarks.size()).forEach(index -> {
                XWPFParagraph endParagraph = document.insertNewParagraph(paragraphAfterFlag.getCTP().newCursor());
                endParagraph.setAlignment(ParagraphAlignment.LEFT);
                XWPFRun endRun = endParagraph.createRun();
                endRun.setFontSize(11);
                endRun.setFontFamily(EmcReportConstant.DEFAULT_FONT_FAMILY);
                boolean lastRun = (index == tableRemarks.size() - 1);
                endRun.setText(((index == 0) ? PfConstant.TABLE_REMARK_TITLE : "    ") + indexTableRemarks.get(index) + (lastRun ? "。" : "；"));
            });
            return;
        }

        createTableRemark(document, targetTable, tableRemarks);
    }
}
