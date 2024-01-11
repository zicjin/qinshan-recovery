package com.sunwayworld.emc.framework.word;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.PositionInParagraph;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import com.sunwayworld.framework.utils.CollectionUtils;

/**
 * word模板仅支持对 {key} 标签的替换
 * 
 * @author yuanhh@sunwayworld.com
 * @date 2019-11-14
 */
public class WordDocumentHelper {
    private static final Logger LOGGER = LogManager.getLogger(WordDocumentHelper.class);
    private static XWPFDocument document;

    /**
     * 初始化
     */
    public static void initDocument(InputStream inputStream) throws IOException {
        document = new XWPFDocument(inputStream);
    }

    /**
     * 将处理后的内容写入到输出流中
     */
    public static void write(OutputStream outputStream) throws IOException {
        document.write(outputStream);
    }

    /**
     * 根据dataMap对word文件中的标签进行替换; <br>
     * <br>
     * ！！！！***需要注意dataMap的数据格式***！！！！ <br>
     * <br>
     * 对于需要替换的普通标签数据标签（不需要循环）-----必须在dataMap中存储一个key为parametersMap的map，
     * 来存储这些不需要循环生成的数据，比如：表头信息，日期，制表人等。 <br>
     * <br>
     * 对于需要循环生成的表格数据------key自定义，value为 --ArrayList<Map<String, String>>
     */
    public static void replaceDocument(Map<String, Object> dataMap) {
        if (!dataMap.containsKey("parametersMap")) {
            LOGGER.error("====================>数据源错误--数据源(parametersMap)缺失");
            return;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> parametersMap = (Map<String, Object>) dataMap.get("parametersMap");
        List<IBodyElement> bodyElements = document.getBodyElements();
        int templateBodySize = bodyElements.size();

        int curT = 0;// 当前操作表格对象的索引
        int curP = 0;// 当前操作段落对象的索引

        for (int a = 0; a < templateBodySize; a++) {
            IBodyElement body = bodyElements.get(a);

            if (BodyElementType.TABLE.equals(body.getElementType())) {// 处理表格
                List<XWPFTable> tables = body.getBody().getTables();
                XWPFTable table = tables.get(curT);
                if (table != null) {
                    List<XWPFTableCell> tableCells = table.getRows().get(0).getTableCells();// 获取到模板表格第一行，用来判断表格类型
                    String tableText = table.getText();// 表格中的所有文本
                    if (tableText.indexOf("##{foreach") > -1) {
                        // 查找到##{foreach标签，该表格需要处理循环
                        if (tableCells.size() != 2 || tableCells.get(0).getText().indexOf("##{foreach") < 0 || tableCells.get(0).getText().trim().length() == 0) {
                            return;
                        }

                        String tableType = tableCells.get(0).getText();
                        String dataSource = tableCells.get(1).getText();
                        if (!dataMap.containsKey(dataSource)) {
                            return;
                        }
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> tableDataList = (List<Map<String, Object>>) dataMap.get(dataSource);
                        if ("##{foreachTable}##".equals(tableType)) {
                            addTableInDocFooter(table, tableDataList, parametersMap, 1);
                        } else if ("##{foreachTableRow}##".equals(tableType)) {
                            addTableInDocFooter(table, tableDataList, parametersMap, 2);
                        }
                    } else if (tableText.indexOf("{") > -1) {
                        // 没有查找到##{foreach标签，查找到了普通替换数据的{}标签，该表格只需要简单替换
                        addTableInDocFooter(table, null, parametersMap, 3);
                    } else {
                        // 没有查找到任何标签，该表格是一个静态表格，仅需要复制一个即可。
                        addTableInDocFooter(table, null, null, 0);
                    }
                    curT++;
                }
            } else if (BodyElementType.PARAGRAPH.equals(body.getElementType())) {// 处理段落
                XWPFParagraph ph = body.getBody().getParagraphArray(curP);
                if (ph != null) {
                    addParagraphInDocFooter(ph, null, parametersMap);
                    curP++;
                }
            }
        }
        // 处理完毕模板，删除文本中的模板内容
        for (int a = 0; a < templateBodySize; a++) {
            document.removeBodyElement(0);
        }
    }

    /**
     * 根据map替换段落元素内的{**}标签
     */
    public static void replaceParagraph(XWPFParagraph xWPFParagraph, Map<String, Object> parametersMap) {
        List<XWPFRun> runs = xWPFParagraph.getRuns();
        String xWPFParagraphText = xWPFParagraph.getText();
        String regEx = "\\{.+?\\}";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(xWPFParagraphText);// 正则匹配字符串{****}

        if (matcher.find()) {
            // 查找到有标签才执行替换
            int beginRunIndex = xWPFParagraph.searchText("{", new PositionInParagraph()).getBeginRun();// 标签开始run位置
            int endRunIndex = xWPFParagraph.searchText("}", new PositionInParagraph()).getEndRun();// 结束标签
            StringBuffer key = new StringBuffer();

            if (beginRunIndex == endRunIndex) {
                // {**}在一个run标签内
                XWPFRun beginRun = runs.get(beginRunIndex);
                String beginRunText = beginRun.text();

                int beginIndex = beginRunText.indexOf("{");
                int endIndex = beginRunText.indexOf("}");
                int length = beginRunText.length();

                if (beginIndex == 0 && endIndex == length - 1) {
                    // 该run标签只有{**}
                    XWPFRun insertNewRun = xWPFParagraph.insertNewRun(beginRunIndex);
                    insertNewRun.getCTR().setRPr(beginRun.getCTR().getRPr());
                    // 设置文本
                    key.append(beginRunText.substring(1, endIndex));
                    insertNewRun.setText(String.valueOf(CollectionUtils.getValueIgnorecase(parametersMap, key.toString())));
                    xWPFParagraph.removeRun(beginRunIndex + 1);
                } else {
                    // 该run标签为**{**}** 或者 **{**} 或者{**}**，替换key后，还需要加上原始key前后的文本
                    XWPFRun insertNewRun = xWPFParagraph.insertNewRun(beginRunIndex);
                    insertNewRun.getCTR().setRPr(beginRun.getCTR().getRPr());
                    // 设置文本
                    key.append(beginRunText.substring(beginRunText.indexOf("{") + 1, beginRunText.indexOf("}")));
                    String textString = beginRunText.substring(0, beginIndex) + CollectionUtils.getValueIgnorecase(parametersMap, key.toString()) + beginRunText.substring(endIndex + 1);
                    insertNewRun.setText(textString);
                    xWPFParagraph.removeRun(beginRunIndex + 1);
                }

            } else {
                // 先处理起始run标签,取得第一个{key}值
                XWPFRun beginRun = runs.get(beginRunIndex);
                String beginRunText = beginRun.text();
                int beginIndex = beginRunText.indexOf("{");
                if (beginRunText.length() > 1) {
                    key.append(beginRunText.substring(beginIndex + 1));
                }
                ArrayList<Integer> removeRunList = new ArrayList<>();// 需要移除的run
                // 处理中间的run
                for (int i = beginRunIndex + 1; i < endRunIndex; i++) {
                    XWPFRun run = runs.get(i);
                    String runText = run.text();
                    key.append(runText);
                    removeRunList.add(i);
                }

                // 获取endRun中的key值
                XWPFRun endRun = runs.get(endRunIndex);
                String endRunText = endRun.text();
                int endIndex = endRunText.indexOf("}");
                // run中**}或者**}**
                if (endRunText.length() > 1 && endIndex != 0) {
                    key.append(endRunText.substring(0, endIndex));
                }

                // 先处理开始标签
                if (beginRunText.length() == 2) {
                    // run标签内文本{
                    XWPFRun insertNewRun = xWPFParagraph.insertNewRun(beginRunIndex);
                    insertNewRun.getCTR().setRPr(beginRun.getCTR().getRPr());
                    // 设置文本
                    insertNewRun.setText(String.valueOf(CollectionUtils.getValueIgnorecase(parametersMap, key.toString())));
                    xWPFParagraph.removeRun(beginRunIndex + 1);// 移除原始的run
                } else {
                    // 该run标签为**{**或者 {** ，替换key后，还需要加上原始key前的文本
                    XWPFRun insertNewRun = xWPFParagraph.insertNewRun(beginRunIndex);
                    insertNewRun.getCTR().setRPr(beginRun.getCTR().getRPr());
                    // 设置文本
                    String textString = beginRunText.substring(0, beginRunText.indexOf("{")) + CollectionUtils.getValueIgnorecase(parametersMap, key.toString());
                    insertNewRun.setText(textString);
                    xWPFParagraph.removeRun(beginRunIndex + 1);// 移除原始的run
                }

                // 处理结束标签
                if (endRunText.length() == 1) {
                    // run标签内文本只有}
                    XWPFRun insertNewRun = xWPFParagraph.insertNewRun(endRunIndex);
                    insertNewRun.getCTR().setRPr(endRun.getCTR().getRPr());
                    // 设置文本
                    insertNewRun.setText("");
                    xWPFParagraph.removeRun(endRunIndex + 1);// 移除原始的run

                } else {
                    // 该run标签为**}**或者 }** 或者**}，替换key后，还需要加上原始key后的文本
                    XWPFRun insertNewRun = xWPFParagraph.insertNewRun(endRunIndex);
                    insertNewRun.getCTR().setRPr(endRun.getCTR().getRPr());
                    // 设置文本
                    String textString = endRunText.substring(endRunText.indexOf("}") + 1);
                    insertNewRun.setText(textString);
                    xWPFParagraph.removeRun(endRunIndex + 1);// 移除原始的run
                }

                // 处理中间的run标签
                for (int i = 0; i < removeRunList.size(); i++) {
                    XWPFRun xWPFRun = runs.get(removeRunList.get(i));// 原始run
                    XWPFRun insertNewRun = xWPFParagraph.insertNewRun(removeRunList.get(i));
                    insertNewRun.getCTR().setRPr(xWPFRun.getCTR().getRPr());
                    insertNewRun.setText("");
                    xWPFParagraph.removeRun(removeRunList.get(i) + 1);// 移除原始的run
                }
            } 
            replaceParagraph(xWPFParagraph, parametersMap);
        } 
    }

    /**
     * 根据 模板表格 和 数据list 在word文档末尾生成表格
     * 
     * @param templateTable
     *            模板表格
     * @param list
     *            循环数据集
     * @param parametersMap
     *            不循环数据集
     * @param flag
     *            (0为静态表格，1为表格整体循环，2为表格内部行循环，3为表格不循环仅简单替换标签即可)
     *
     */
    private static void addTableInDocFooter(XWPFTable templateTable, List<Map<String, Object>> list, Map<String, Object> parametersMap, int flag) {
        List<XWPFTableRow> templateTableRows = templateTable.getRows();// 获取模板表格所有行
        int size = templateTableRows.size();
        XWPFTable newCreateTable = document.createTable();// 创建新表格,默认一行一列
        if (flag == 1) {// 表格整体循环
            for (Map<String, Object> map : list) {
                for (int i = 1; i < size; i++) {
                    XWPFTableRow newCreateRow = newCreateTable.createRow();
                    CopyTableRow(newCreateRow, templateTableRows.get(i));// 复制模板行文本和样式到新行
                }
                newCreateTable.removeRow(0);// 移除多出来的第一行
                document.createParagraph();// 添加回车换行
                replaceTable(newCreateTable, map);// 替换标签
            }
        } else if (flag == 2) {// 表格表格内部行循环
            int tagRowsIndex = 0;
            for (int i = 0; i < size; i++) {
                String rowText = templateTableRows.get(i).getCell(0).getText();// 获取到表格行的第一个单元格
                if (rowText.indexOf("##{foreachRows}##") > -1) {
                    tagRowsIndex = i;
                    break;
                }
            }

            /* 复制模板行和标签行之前的行 */
            for (int i = 1; i < tagRowsIndex; i++) {
                XWPFTableRow newCreateRow = newCreateTable.createRow();
                CopyTableRow(newCreateRow, templateTableRows.get(i));// 复制行
                replaceTableRow(newCreateRow, parametersMap);// 处理不循环标签的替换
            }

            /* 循环生成模板行 */
            XWPFTableRow tempRow = templateTableRows.get(tagRowsIndex + 1);// 获取到模板行
            for (int i = 0; i < list.size(); i++) {
                XWPFTableRow newCreateRow = newCreateTable.createRow();
                CopyTableRow(newCreateRow, tempRow);// 复制模板行
                replaceTableRow(newCreateRow, list.get(i));// 处理标签替换
            }

            /* 复制模板行和标签行之后的行 */
            for (int i = tagRowsIndex + 2; i < size; i++) {
                XWPFTableRow newCreateRow = newCreateTable.createRow();
                CopyTableRow(newCreateRow, templateTableRows.get(i));// 复制行
                replaceTableRow(newCreateRow, parametersMap);// 处理不循环标签的替换
            }
            newCreateTable.removeRow(0);// 移除多出来的第一行
            document.createParagraph();// 添加回车换行
        } else {
            for (int i = 0; i < size; i++) {
                XWPFTableRow newCreateRow = newCreateTable.createRow();
                CopyTableRow(newCreateRow, templateTableRows.get(i));// 复制模板行文本和样式到新行
            }
            newCreateTable.removeRow(0);// 移除多出来的第一行
            document.createParagraph();// 添加回车换行
            if (flag == 3) {
                replaceTable(newCreateTable, parametersMap);
            }
        }
    }

    /**
     * 复制表格行XWPFTableRow格式
     */
    private static void CopyTableRow(XWPFTableRow target, XWPFTableRow source) {
        int tempRowCellsize = source.getTableCells().size();// 模板行的列数
        for (int i = 0; i < tempRowCellsize - 1; i++) {
            target.addNewTableCell();// 为新添加的行添加与模板表格对应行行相同个数的单元格
        }
        // 复制样式
        target.getCtRow().setTrPr(source.getCtRow().getTrPr());
        // 复制单元格
        for (int i = 0; i < target.getTableCells().size(); i++) {
            copyTableCell(target.getCell(i), source.getCell(i));
        }
    }

    /**
     * 复制单元格XWPFTableCell格式
     */
    private static void copyTableCell(XWPFTableCell newTableCell, XWPFTableCell templateTableCell) {
        // 列属性
        newTableCell.getCTTc().setTcPr(templateTableCell.getCTTc().getTcPr());
        // 删除目标 targetCell 所有文本段落
        for (int pos = 0; pos < newTableCell.getParagraphs().size(); pos++) {
            newTableCell.removeParagraph(pos);
        }
        // 添加新文本段落
        for (XWPFParagraph sp : templateTableCell.getParagraphs()) {
            XWPFParagraph targetP = newTableCell.addParagraph();
            copyParagraph(targetP, sp);
        }
    }

    /**
     * 根据map替换表格中的{key}标签
     */
    private static void replaceTable(XWPFTable xwpfTable, Map<String, Object> parametersMap) {
        List<XWPFTableRow> rows = xwpfTable.getRows();
        for (XWPFTableRow xWPFTableRow : rows) {
            List<XWPFTableCell> tableCells = xWPFTableRow.getTableCells();
            for (XWPFTableCell xWPFTableCell : tableCells) {
                List<XWPFParagraph> paragraphs2 = xWPFTableCell.getParagraphs();
                for (XWPFParagraph xWPFParagraph : paragraphs2) {
                    replaceParagraph(xWPFParagraph, parametersMap);
                }
            }
        }
    }

    /**
     * 根据参数parametersMap对表格的一行进行标签的替换
     *
     */
    private static void replaceTableRow(XWPFTableRow tableRow, Map<String, Object> parametersMap) {
        List<XWPFTableCell> tableCells = tableRow.getTableCells();
        for (XWPFTableCell xWPFTableCell : tableCells) {
            List<XWPFParagraph> paragraphs = xWPFTableCell.getParagraphs();
            for (XWPFParagraph xwpfParagraph : paragraphs) {
                replaceParagraph(xwpfParagraph, parametersMap);
            }
        }
    }

    /**
     * 复制文本段落XWPFParagraph格式
     */
    private static void copyParagraph(XWPFParagraph newParagraph, XWPFParagraph templateParagraph) {
        newParagraph.getCTP().setPPr(templateParagraph.getCTP().getPPr());
        for (int pos = 0; pos < newParagraph.getRuns().size(); pos++) {
            newParagraph.removeRun(pos);
        }
        for (XWPFRun s : templateParagraph.getRuns()) {
            XWPFRun targetrun = newParagraph.createRun();
            targetrun.getCTR().setRPr(s.getCTR().getRPr());
            targetrun.setText(s.text());
        }
    }

    /**
     * 根据 模板段落 和 数据 在文档末尾生成段落
     */
    private static void addParagraphInDocFooter(XWPFParagraph templateParagraph, List<Map<String, String>> list, Map<String, Object> parametersMap) {
        XWPFParagraph createParagraph = document.createParagraph();
        // 设置段落样式
        createParagraph.getCTP().setPPr(templateParagraph.getCTP().getPPr());
        // 移除原始内容
        for (int pos = 0; pos < createParagraph.getRuns().size(); pos++) {
            createParagraph.removeRun(pos);
        }
        // 添加Run标签
        for (XWPFRun s : templateParagraph.getRuns()) {
            XWPFRun targetrun = createParagraph.createRun();
            targetrun.getCTR().setRPr(s.getCTR().getRPr());
            targetrun.setText(s.text());
        }
        replaceParagraph(createParagraph, parametersMap);
    }
}
