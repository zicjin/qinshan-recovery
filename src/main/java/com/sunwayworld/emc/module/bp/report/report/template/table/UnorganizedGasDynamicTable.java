package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.word.PfWord2007Utils;
import com.sunwayworld.emc.framework.word.Word2007Utils;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.constant.EmcReportConstant;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhengxy@sunwayworld.com 2021年09月10日
 */
public class UnorganizedGasDynamicTable implements DynamicTable {

    private static final int MAX_DYNAMIC_COLUMN_QTY = 4;
    private static final int MIN_DYNAMIC_COLUMN_QTY = 2;
    private static final int[] LEFT_FIXED_COLUMN_WIDTHS = {WordTemplateUtils.cm2Int(4.45D), WordTemplateUtils.cm2Int(2.95D)};
    private static final int[] RIGHT_FIXED_COLUMN_WIDTHS = {WordTemplateUtils.cm2Int(1.8D)};

    private XWPFDocument document;
    private EmcReportBean report;
    private List<EmcFolderBean> folderList;
    private List<EmcOrderBean> orderList;
    private List<EmcReportResultBean> reportResultList;
    private int tableIndex;
    private boolean isFirst;
    private boolean isLast;
    private boolean displayStd;
    private boolean sampling;
    private boolean mergeColumn;

    public UnorganizedGasDynamicTable(XWPFDocument document, EmcProjectBean project, EmcReportBean report, List<EmcFolderBean> folderList, List<EmcOrderBean> orderList, List<EmcReportResultBean> reportResultList, int tableIndex, boolean isFirst, boolean isLast) {
        this.document = document;
        this.report = report;
        this.folderList = folderList;
        this.orderList = orderList;
        this.reportResultList = reportResultList;
        this.tableIndex = tableIndex;
        this.isFirst = isFirst;
        this.isLast = isLast;
        this.displayStd = Constant.YES.equals(report.getExt$Item("displayStd"));
        this.sampling = Constant.YES.equals(project.getSample());

        long testCount = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)
                && !StringUtils.contains(result.getTestName(), EmcConstant.PARAMETER_TEST_MATCH_KEYWORD)).map(EmcReportResultBean::getTestName).distinct().count();
        this.mergeColumn = testCount < MIN_DYNAMIC_COLUMN_QTY;
    }

    @Override
    public XWPFDocument getDocument() {
        return document;
    }

    @Override
    public String getTableTitle() {
        return String.format(isFirst ? PfConstant.WATER_RESULT_TABLE_TITLE : "续" + PfConstant.WATER_RESULT_TABLE_TITLE, tableIndex, folderList.get(0).getOrderCategory());
    }

    @Override
    public int getTableIndex() {
        return tableIndex;
    }

    @Override
    public boolean isLast() {
        return isLast;
    }

    @Override
    public int getDynamicTableIndex() {
        return 2;
    }

    @Override
    public int getTableWidth() {
        // 默认表格宽度为 18cm
        return 10204;
    }

    @Override
    public List<Integer> getColumnWidthList() {
        return WordTemplateUtils.cm2Ints(Arrays.asList(0D, 3.0D, 2.7D, 2.7D, 2.7D, 2.7D));
    }

    @Override
    public WordTemplateDynamicTableDTO getDynamicTableDto() {
        // 设置表格字体为 Times New Roman，字号为 10
        WordTemplateDynamicTableDTO dynamicTableDTO = new WordTemplateDynamicTableDTO();
        dynamicTableDTO.setFontStyle("Times New Roman");
        dynamicTableDTO.setFontSize(10);

        return dynamicTableDTO;
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<String> dynamicTitleList = new ArrayList<>();

        // 过滤出样品值结果
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)
                && !StringUtils.contains(result.getTestName(), EmcConstant.PARAMETER_TEST_MATCH_KEYWORD)).sorted(Comparator.comparing(EmcReportResultBean::getOrderId)).collect(Collectors.toList());

        // 未检出表示方式
        boolean showLowLimit = EmcReportConstant.LOW_LIMIT_DISPLAY_MODE.equalsIgnoreCase(report.getNdDisplayMode());
        sampleReportResultList.stream().filter(r -> EmcConstant.ND.equals(r.getReportResult())).forEach(r -> r.setReportResult(showLowLimit ? r.getLowLimit() + "L" : r.getReportResult()));

        Map<String, List<EmcReportResultBean>> testResultMap = sampleReportResultList.stream().collect(Collectors.groupingBy(EmcReportResultBean::getTestName, LinkedHashMap::new, Collectors.toList()));

        List<String> testNameList = new ArrayList<>(testResultMap.size());
        testResultMap.forEach((testName, resultList) -> testNameList.add(String.join("", resultList.get(0).getExt$Item("itemGroup"), "(", resultList.get(0).getUnitName(), ")")));

        int minResultColumnQty = Math.min(MAX_DYNAMIC_COLUMN_QTY, testNameList.size());
        int marginColumnQty = Math.max(0, MAX_DYNAMIC_COLUMN_QTY - testNameList.size());

        String samplingDate = DateTimeUtils.formatLocalDate(reportResultList.get(0).getSampleBeginDate(), EmcConstant.TABLE_DATE_PATTERN);
        LocalDate sendDate = orderList.stream().filter(order -> !ObjectUtils.isEmpty(order.getSendDate())).map(EmcOrderBean::getSendDate).min(Comparator.comparing(Function.identity())).orElse(null);

        dynamicTitleList.add(sampling ? samplingDate : (sendDate == null) ? null : DateTimeUtils.formatLocalDate(sendDate, EmcConstant.TABLE_DATE_PATTERN));

        IntStream.range(0, minResultColumnQty).forEach(index -> dynamicTitleList.add(testNameList.get(index)));
        IntStream.range(0, marginColumnQty).forEach(index -> dynamicTitleList.add(null));

        // 合并列配置
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElse(new ArrayList<>());
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 0, 1));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 2, MAX_DYNAMIC_COLUMN_QTY + 1));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(1, 2, MAX_DYNAMIC_COLUMN_QTY + 1));

        if (sampling && mergeColumn) {
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(2, 2, MAX_DYNAMIC_COLUMN_QTY + 1));
        }

        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        // 合并行配置
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, 1, 2));
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(1, 1, 2));
        dynamicTableDTO.setMergedRowList(mergedRowList);

        String[] tableTitles = sampling ? PfConstant.UNORGANIZED_GAS_RESULT_TABLE_HEADER_TITLES : PfConstant.UNORGANIZED_GAS_RESULT_TABLE_HEADER_TITLES_2;
        List<List<Object>> headerDataList = buildTableDynamicHeader(tableTitles, PfConstant.UNORGANIZED_GAS_RESULT_TABLE_HEADER_COLUMN_QTY, dynamicTitleList);
        setTableTitle(tableTitles, PfConstant.UNORGANIZED_GAS_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        // 过滤出样品值结果
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)
                && !StringUtils.contains(result.getTestName(), EmcConstant.PARAMETER_TEST_MATCH_KEYWORD)).sorted(Comparator.comparing(EmcReportResultBean::getOrderId)).collect(Collectors.toList());

        List<String> testNameList = sampleReportResultList.stream().map(EmcReportResultBean::getTestName).distinct().collect(Collectors.toList());
        Map<Long, List<EmcReportResultBean>> folderResultMap = sampleReportResultList.stream().sorted(Comparator.comparing(EmcReportResultBean::getFolderId)).collect(Collectors.groupingBy(EmcReportResultBean::getFolderId, LinkedHashMap::new, Collectors.toList()));
        Map<Long, String> folderIdNameMap = folderList.stream().filter(folder -> !StringUtils.isEmpty(folder.getMonitorPointName())).collect(Collectors.toMap(EmcFolderBean::getId, EmcFolderBean::getMonitorPointName));

        int minResultColumnQty = Math.min(4, testNameList.size());
        int marginColumnQty = Math.max(0, 4 - testNameList.size());

        AtomicInteger rowIndex = new AtomicInteger(2);
        AtomicInteger mergedIndex = new AtomicInteger(2);
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);

        folderResultMap.forEach((folderId, resultList) -> {
            WordTemplateUtils.sortedResultByAnalysisOrg(resultList);
            Map<String, List<EmcReportResultBean>> orderResultMap = resultList.stream().collect(Collectors.groupingBy(EmcReportResultBean::getOrderNo, LinkedHashMap::new, Collectors.toList()));
            orderResultMap.forEach((orderNo, curResultList) -> {
                List<Object> rowDataList = new ArrayList<>();
                String folderName = folderIdNameMap.get(folderId);
                if (!sampling) {
                    EmcOrderBean curOrder = orderList.stream().filter(order -> StringUtils.equals(order.getOrderNo(), orderNo)).findFirst().orElseGet(EmcOrderBean::new);
                    folderName = curOrder.getLabelNo();
                }

                rowDataList.add(folderName);
                rowDataList.add(orderNo);
                IntStream.range(0, minResultColumnQty).forEach(i -> {
                    EmcReportResultBean reportResult = curResultList.stream().filter(r -> StringUtils.equals(r.getTestName(), testNameList.get(i))).findFirst().orElseGet(EmcReportResultBean::new);
                    rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(reportResult.getReportResult()));
                });
                IntStream.range(0, marginColumnQty).forEach(i -> rowDataList.add(null));
                tableBodyDataList.add(rowDataList);

                if (mergeColumn) {
                    mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(mergedIndex.incrementAndGet(), MAX_DYNAMIC_COLUMN_QTY - marginColumnQty + 1, MAX_DYNAMIC_COLUMN_QTY + 1));
                }
            });

            int startRowIndex = rowIndex.get() + 1;
            int endRowIndex = rowIndex.addAndGet(orderResultMap.size());
            mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, startRowIndex, endRowIndex));
        });
        dynamicTableDTO.setMergedRowList(mergedRowList);

        // 方法检出限
        List<Object> rowDataList = new ArrayList<>();
        // 判断分析项是否含有检出量
        boolean detectedQuantity = sampleReportResultList.stream().anyMatch(r -> !StringUtils.isEmpty(r.getExt$Item("detectedQuantity")));
        rowDataList.add(detectedQuantity ? PfConstant.METHOD_LOW_VALUE : PfConstant.METHOD_LOW_LIMIT);
        rowDataList.add("");
        IntStream.range(0, minResultColumnQty).forEach(i -> {
            EmcReportResultBean reportResult = sampleReportResultList.stream().filter(r -> StringUtils.equals(r.getTestName(), testNameList.get(i)) && !StringUtils.isEmpty(r.getLowLimit())).findFirst().orElseGet(EmcReportResultBean::new);
            if (!StringUtils.isEmpty(reportResult.getLowLimit())) {
            	rowDataList.add(reportResult.getLowLimit() + Optional.ofNullable(reportResult.getExt$Item("limitUnitName")).orElse(reportResult.getUnitName()));
            } else {
            	rowDataList.add("-");
            }
        });
        IntStream.range(0, marginColumnQty).forEach(i -> rowDataList.add(null));
        tableBodyDataList.add(rowDataList);

        // 合并列配置
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 2, 0, 1));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(mergedIndex.incrementAndGet(), MAX_DYNAMIC_COLUMN_QTY - marginColumnQty + 1, MAX_DYNAMIC_COLUMN_QTY + 1));

        // 是否显示执行标准
        if (displayStd) {
            // 添加备注行
            tableBodyDataList.add(buildDynamicRowDataList(PfConstant.EXECUTIVE_STANDARD, report.getRemark(), PfConstant.UNORGANIZED_GAS_RESULT_TABLE_HEADER_COLUMN_QTY));
            // 合并备注列
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 2, 2, PfConstant.UNORGANIZED_GAS_RESULT_TABLE_HEADER_COLUMN_QTY - 2));
        }

        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        return tableBodyDataList;
    }

    @Override
    public void create() {
        WordTemplateDynamicTableDTO dynamicTableDTO = buildTable();
        dynamicTableDTO.setTableWidth(getTableWidth());
        dynamicTableDTO.setCellHeight(getCellHeight());
        dynamicTableDTO.setColumnWidthList(getColumnWidthList());
        PfWord2007Utils.createDynamicTable(getDocument(), dynamicTableDTO);

        XWPFTable table = getDocument().getTables().get(getDynamicTableIndex());
        Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).ifPresent(list -> list.forEach(c -> Word2007Utils.mergeTableColumns(table, c.getRowIndex(), c.getStartColumnIndex(), c.getEndColumnIndex())));
        Optional.ofNullable(dynamicTableDTO.getMergedRowList()).ifPresent(list -> list.forEach(r -> Word2007Utils.mergeTableRows(table, r.getColumnIndex(), r.getStartRowIndex(), r.getEndRowIndex())));

        // 插入图片
        Optional.ofNullable(dynamicTableDTO.getTableImageList()).ifPresent(tableImageList -> tableImageList.forEach(i -> Word2007Utils.insertImageToTableCell(table, i.getRowIndex(), i.getColumnIndex(), i.getImageUrl())));

        if (!isLast()) {
            createTableEndText(getDocument(), table, 0);
        }

        // 重新调整表格列宽
        resizeTableColumnWidth(table);
    }

    private void resizeTableColumnWidth(XWPFTable table) {
        int rightFixedColumnQty = displayStd ? 1 : 0;

        int testCount = (int) reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)
                && !StringUtils.contains(result.getTestName(), EmcConstant.PARAMETER_TEST_MATCH_KEYWORD)).map(EmcReportResultBean::getTestName).distinct().count();

        int leftFixedSumWidth = Arrays.stream(LEFT_FIXED_COLUMN_WIDTHS).sum();
        int rightFixedSumWidth = Arrays.stream(RIGHT_FIXED_COLUMN_WIDTHS).sum() * rightFixedColumnQty;
        int newAvgWidth = (getTableWidth() - (leftFixedSumWidth + rightFixedSumWidth)) / testCount;

        List<Integer> columnWidthList = new ArrayList<>();
        Arrays.stream(LEFT_FIXED_COLUMN_WIDTHS).forEach(columnWidthList::add);
        IntStream.range(0, testCount).forEach(index -> columnWidthList.add(newAvgWidth));

        int mergedColumnQty = MAX_DYNAMIC_COLUMN_QTY - testCount;
        IntStream.range(0, mergedColumnQty).forEach(index -> columnWidthList.add(0));

        IntStream.range(0, rightFixedColumnQty).forEach(width -> columnWidthList.add(RIGHT_FIXED_COLUMN_WIDTHS[0]));

        List<XWPFTableRow> tableRows = table.getRows();
        tableRows.forEach(row -> {
            List<XWPFTableCell> tableCells = row.getTableCells();
            IntStream.range(0, tableCells.size()).forEach(index -> tableCells.get(index).setWidth(String.valueOf(columnWidthList.get(index))));
        });
    }

}
