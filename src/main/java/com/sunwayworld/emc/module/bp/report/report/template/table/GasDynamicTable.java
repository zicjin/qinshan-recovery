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

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.report.report.constant.EmcReportConstant;
import com.sunwayworld.framework.utils.NumberUtils;
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
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 有组织废气
 * @author zhengxy@sunwayworld.com 2021年09月10日
 */
public class GasDynamicTable implements DynamicTable {

    private static final int MAX_DYNAMIC_COLUMN_QTY = 2;
    private static final int[] LEFT_FIXED_COLUMN_WIDTHS = {WordTemplateUtils.cm2Int(3.0D), WordTemplateUtils.cm2Int(3.0D)};
    private static final int[] RIGHT_FIXED_COLUMN_WIDTHS = {WordTemplateUtils.cm2Int(1.8D)};

    private XWPFDocument document;
    private EmcReportBean report;
    private List<EmcFolderBean> folderList;
    private List<EmcOrderBean> orderList;
    private List<EmcReportResultBean> reportResultList;
    private List<EmcOrderTaskResultBean> orderTaskResultList;

    private int tableIndex;
    private boolean isFirst;
    private boolean isLast;
    private boolean displayStd;
    private boolean sampling;
    private boolean mergeColumn;
    private boolean lowLimit;
    private int testCount;

    public GasDynamicTable(XWPFDocument document, EmcProjectBean project, EmcReportBean report, List<EmcFolderBean> folderList, List<EmcOrderBean> orderList, List<EmcReportResultBean> reportResultList, List<EmcOrderTaskResultBean> orderTaskResultList, int tableIndex, boolean isFirst, boolean isLast) {
        this.document = document;
        this.report = report;
        this.folderList = folderList;
        this.orderList = orderList;
        this.reportResultList = reportResultList;
        this.orderTaskResultList = orderTaskResultList;
        this.tableIndex = tableIndex;
        this.isFirst = isFirst;
        this.isLast = isLast;
        this.displayStd = Constant.YES.equals(report.getExt$Item("displayStd"));
        this.sampling = Constant.YES.equals(project.getSample());

        this.testCount = (int) reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)
                && !StringUtils.contains(result.getTestName(), EmcConstant.PARAMETER_TEST_MATCH_KEYWORD)).map(EmcReportResultBean::getTestName).distinct().count();
        this.mergeColumn = testCount < MAX_DYNAMIC_COLUMN_QTY;
        this.lowLimit = !reportResultList.isEmpty() && StringUtils.equals(reportResultList.get(0).getExt$Item("reportLimitType"), "lowLimit");
    }

    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }

    @Override
    public String getTableTitle() {
        return String.format(isFirst ? PfConstant.WATER_RESULT_TABLE_TITLE : "续" + PfConstant.WATER_RESULT_TABLE_TITLE, tableIndex, folderList.get(0).getOrderCategory());
    }

    @Override
    public int getTableIndex() {
        return this.tableIndex;
    }

    @Override
    public boolean isLast() {
        return this.isLast;
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
    public WordTemplateDynamicTableDTO getDynamicTableDto() {
        // 设置表格字体为 Times New Roman，字号为 10
        WordTemplateDynamicTableDTO dynamicTableDTO = new WordTemplateDynamicTableDTO();
        dynamicTableDTO.setFontStyle("Times New Roman");
        dynamicTableDTO.setFontSize(10);

        return dynamicTableDTO;
    }

    @Override
    public List<Integer> getColumnWidthList() {
        return sampling ? WordTemplateUtils.cm2Ints(Arrays.asList(3.0D, 3.0D, 0D, 0D, 0D, 0D, 0D, 0D)) : WordTemplateUtils.cm2Ints(Arrays.asList(4.4D, 3.0D, 0D, 0D, 0D, 0D, 0D, 0D));
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<String> dynamicTitleList = new ArrayList<>();

        // 过滤出样品值结果
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)
                && !StringUtils.contains(result.getTestName(), EmcConstant.PARAMETER_TEST_MATCH_KEYWORD)).sorted(Comparator.comparing(EmcReportResultBean::getOrderId)).collect(Collectors.toList());
        List<String> testNameList = sampleReportResultList.stream().map(EmcReportResultBean::getTestName).distinct().collect(Collectors.toList());

        // 未检出表示方式
        boolean showLowLimit = EmcReportConstant.LOW_LIMIT_DISPLAY_MODE.equalsIgnoreCase(report.getNdDisplayMode());
        sampleReportResultList.stream().filter(r -> EmcConstant.ND.equals(r.getReportResult())).forEach(r -> r.setReportResult(showLowLimit ? r.getLowLimit() + "L" : r.getReportResult()));

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
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 2, 7));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(1, 2, 7));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(2, 2, 4));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(2, 5, 7));

        if (!sampling) {
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(3, 2, 4));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(3, 5, 7));

            if (mergeColumn) {
                mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(2, 2, 7));
                mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(3, 2, 7));
            }
        }

        if (sampling && mergeColumn) {
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(2, 2, 7));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(3, 4, 7));
        }

        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        // 合并行配置
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, 1, 3));
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(1, 1, 3));
        dynamicTableDTO.setMergedRowList(mergedRowList);

        // 样品浓度、折算浓度、排放速率列及单位
        testNameList.forEach(testName -> {
            List<EmcReportResultBean> curResultList = sampleReportResultList.stream().filter(r -> StringUtils.equals(r.getTestName(), testName)).collect(Collectors.toList());
            List<Long> orderTaskIdList = curResultList.stream().map(EmcReportResultBean ::getOrderTaskId).distinct().collect(Collectors.toList());
            List<EmcOrderTaskResultBean> curResultVolumeList = orderTaskResultList.stream()
                    .filter(r -> orderTaskIdList.contains(r.getOrderTaskId()))
                    .filter(r -> StringUtils.equals(r.getItemAliasName(), PfConstant.GAS_SAMPLING_VOLUME_KEYWORD)).collect(Collectors.toList());

            Arrays.stream(PfConstant.GAS_RESULT_KEYWORDS).forEach(keyword -> {
                // 实测浓度（样品浓度）有两种分析项别名
                String[] pairs = StringUtils.split(keyword, ",");
                EmcReportResultBean result = curResultList.stream().filter(r -> Arrays.stream(pairs).anyMatch(itemName -> StringUtils.contains(r.getItemAliasName(), itemName))).findFirst().orElseGet(EmcReportResultBean::new);
                // 采样的显示实测浓度，送样的显示样品浓度
                String needUnit ="";
                String columnName = (sampling || pairs.length == 1) ? pairs[0] : pairs[1];
                if (StringUtils.equals(PfConstant.GAS_ORDER_CONCENT_KEYWORD, columnName)) {
                    boolean noVolume = (curResultVolumeList.isEmpty() || !NumberUtils.isNumber(curResultVolumeList.get(0).getReportResult()));
                    columnName = noVolume ? "样品含量" : columnName;
                    String unitName = Optional.ofNullable(result.getUnitName()).orElse("-");
                    String detectedQuantityUnit = Optional.ofNullable(result.getExt$Item("detectedQuantityUnit")).orElse("-");
                    needUnit = noVolume?detectedQuantityUnit : unitName;
                    dynamicTitleList.add(columnName + "(" + needUnit + ")");
                }else{
                    dynamicTitleList.add(columnName + "(" + Optional.ofNullable(result.getUnitName()).orElse("-") + ")");
                }

            });
        });

        if (mergeColumn) {
            IntStream.range(0, 3).forEach(index -> dynamicTitleList.add(null));
        }

        String[] tableTitles = sampling ? PfConstant.GAS_RESULT_TABLE_HEADER_TITLES : PfConstant.GAS_RESULT_TABLE_HEADER_TITLES_2;
        List<List<Object>> headerDataList = buildTableDynamicHeader(tableTitles, PfConstant.GAS_RESULT_TABLE_HEADER_COLUMN_QTY, dynamicTitleList);
        setTableTitle(tableTitles, PfConstant.GAS_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        if (sampling) {
            buildSamplingTableBody(dynamicTableDTO, tableBodyDataList);
            return tableBodyDataList;
        }

        buildNonSamplingTableBody(dynamicTableDTO, tableBodyDataList);

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
        if (sampling && mergeColumn) {
            resizeTableColumnWidth(table);
        }
    }

    /**
     * 构建采样表格数据
     * @param dynamicTableDTO
     * @param tableBodyDataList
     */
    private void buildSamplingTableBody(WordTemplateDynamicTableDTO dynamicTableDTO, List<List<Object>> tableBodyDataList) {
        // 过滤出样品值结果
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)
                && !StringUtils.contains(result.getTestName(), EmcConstant.PARAMETER_TEST_MATCH_KEYWORD)).sorted(Comparator.comparing(EmcReportResultBean::getOrderId)).collect(Collectors.toList());

        List<String> testNameList = sampleReportResultList.stream().map(EmcReportResultBean::getTestName).distinct().collect(Collectors.toList());
        Map<Long, List<EmcReportResultBean>> folderResultMap = sampleReportResultList.stream().collect(Collectors.groupingBy(EmcReportResultBean::getFolderId, LinkedHashMap::new, Collectors.toList()));
        Map<Long, String> folderIdNameMap = folderList.stream().filter(folder -> !StringUtils.isEmpty(folder.getMonitorPointName())).collect(Collectors.toMap(EmcFolderBean::getId, EmcFolderBean::getMonitorPointName));

        int minResultColumnQty = Math.min(MAX_DYNAMIC_COLUMN_QTY, testNameList.size());
        int marginColumnQty = Math.max(0, MAX_DYNAMIC_COLUMN_QTY - testNameList.size());

        AtomicInteger folderMergedIndex = new AtomicInteger(3);
        AtomicInteger mergedColumnIndex = new AtomicInteger(3);
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElse(new ArrayList<>());

        folderResultMap.forEach((folderId, resultList) -> {
            WordTemplateUtils.sortedResultByAnalysisOrg(resultList);
            Map<String, List<EmcReportResultBean>> orderResultMap = resultList.stream().collect(Collectors.groupingBy(EmcReportResultBean::getOrderNo, LinkedHashMap::new, Collectors.toList()));
            orderResultMap.forEach((orderNo, curResultList) -> {
                List<Object> rowDataList = new ArrayList<>();

                String folderName = folderIdNameMap.get(folderId);
                rowDataList.add(folderName);
                rowDataList.add(orderNo);
                IntStream.range(0, minResultColumnQty).forEach(i -> {
                    List<EmcReportResultBean> testResultList = curResultList.stream().filter(r -> StringUtils.equals(r.getTestName(), testNameList.get(i))).collect(Collectors.toList());
                    Arrays.stream(PfConstant.GAS_RESULT_KEYWORDS).forEach(keyword -> {
                        String[] array = StringUtils.split(keyword, ",");
                        EmcReportResultBean reportResult = testResultList.stream().filter(r -> Arrays.stream(array).anyMatch(itemName -> StringUtils.contains(r.getItemAliasName(), itemName))).findFirst().orElseGet(EmcReportResultBean::new);
                        rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(reportResult.getReportResult()));
                    });
                });
                IntStream.range(0, marginColumnQty).forEach(i -> IntStream.range(0, 3).forEach(j -> rowDataList.add(null)));
                tableBodyDataList.add(rowDataList);

                if (mergeColumn) {
                    mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(mergedColumnIndex.incrementAndGet(), 4, 7));
                }
            });

            int startRowIndex = folderMergedIndex.get() + 1;
            int endRowIndex = folderMergedIndex.addAndGet(orderResultMap.size());
            mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, startRowIndex, endRowIndex));
        });
        dynamicTableDTO.setMergedRowList(mergedRowList);

        // 方法检出限
        List<Object> rowDataList = new ArrayList<>();
        rowDataList.add(lowLimit ? PfConstant.METHOD_LOW_LIMIT : PfConstant.METHOD_LOW_VALUE);
        rowDataList.add("");

        IntStream.range(0, minResultColumnQty).forEach(i -> {
            List<EmcReportResultBean> testResultList = sampleReportResultList.stream().filter(r -> StringUtils.equals(r.getTestName(), testNameList.get(i))).collect(Collectors.toList());
            Arrays.stream(PfConstant.GAS_RESULT_KEYWORDS).forEach(keyword -> {
                String[] array = StringUtils.split(keyword, ",");
                EmcReportResultBean reportResult = testResultList.stream().filter(r -> Arrays.stream(array).anyMatch(itemName -> StringUtils.contains(r.getItemAliasName(), itemName))).findFirst().orElseGet(EmcReportResultBean::new);
                // 检出限及检出限单位
                String lowLimitValue = reportResult.getLowLimit();
                String limitUnitName = Optional.ofNullable(reportResult.getExt$Item("limitUnitName")).orElse(reportResult.getUnitName());
                // 检出量及检出量单位
                String detectedQuantity = reportResult.getExt$Item("detectedQuantity");
                String detectedQuantityUnit = reportResult.getExt$Item("detectedQuantityUnit");

                String limitValue = lowLimit ? (StringUtils.isBlank(lowLimitValue) ? "-" : lowLimitValue + limitUnitName)
                        : (StringUtils.isBlank(detectedQuantity) ? "-" : detectedQuantity + detectedQuantityUnit);
                rowDataList.add(limitValue);
            });
        });

        IntStream.range(0, marginColumnQty).forEach(i -> IntStream.range(0, 3).forEach(j -> rowDataList.add(null)));
        tableBodyDataList.add(rowDataList);

        // 合并列配置
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 3, 0, 1));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 3, 2, 4));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 3, 5, 7));

        if (mergeColumn) {
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 3, 2, 7));
        }

        // 是否显示执行标准
        if (displayStd) {
            // 添加备注行
            tableBodyDataList.add(buildDynamicRowDataList(PfConstant.EXECUTIVE_STANDARD, report.getRemark(), PfConstant.GAS_RESULT_TABLE_HEADER_COLUMN_QTY));
            // 合并备注列
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 4, 2, PfConstant.GAS_RESULT_TABLE_HEADER_COLUMN_QTY - 2));
        }

        dynamicTableDTO.setMergedColumnList(mergedColumnList);
    }

    /**
     * 构建送样表格数据
     * @param dynamicTableDTO
     * @param tableBodyDataList
     */
    private void buildNonSamplingTableBody(WordTemplateDynamicTableDTO dynamicTableDTO, List<List<Object>> tableBodyDataList) {
        // 过滤出样品值结果
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)
                && !StringUtils.contains(result.getTestName(), EmcConstant.PARAMETER_TEST_MATCH_KEYWORD)).sorted(Comparator.comparing(EmcReportResultBean::getOrderId)).collect(Collectors.toList());

        List<String> testNameList = sampleReportResultList.stream().map(EmcReportResultBean::getTestName).distinct().collect(Collectors.toList());

        LinkedHashMap<Long, List<EmcReportResultBean>> orderResultMap = sampleReportResultList.stream().collect(Collectors.groupingBy(EmcReportResultBean::getOrderId, LinkedHashMap::new, Collectors.toList()));
        Map<Long, String> orderLabelNoMap = orderList.stream().filter(order -> !StringUtils.isEmpty(order.getLabelNo())).collect(Collectors.toMap(EmcOrderBean::getId, EmcOrderBean::getLabelNo));

        int minResultColumnQty = Math.min(MAX_DYNAMIC_COLUMN_QTY, testNameList.size());
        int marginColumnQty = Math.max(0, MAX_DYNAMIC_COLUMN_QTY - testNameList.size());

        AtomicInteger rowIndex = new AtomicInteger(3);
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElse(new ArrayList<>());

        orderResultMap.forEach((orderId, resultList) -> {
            List<Object> rowDataList = new ArrayList<>();
            rowDataList.add(orderLabelNoMap.get(orderId));
            rowDataList.add(resultList.get(0).getOrderNo());

            IntStream.range(0, minResultColumnQty).forEach(i -> {
                List<EmcReportResultBean> testResultList = resultList.stream().filter(r -> StringUtils.equals(r.getTestName(), testNameList.get(i))).collect(Collectors.toList());
                Arrays.stream(PfConstant.GAS_RESULT_KEYWORDS).forEach(keyword -> {
                    String[] array = StringUtils.split(keyword, ",");

                    EmcReportResultBean reportResult = testResultList.stream().filter(r -> Arrays.stream(array).anyMatch(itemName -> StringUtils.contains(r.getItemAliasName(), itemName))).findFirst().orElseGet(EmcReportResultBean::new);
                    rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(reportResult.getReportResult()));
                });
            });
            IntStream.range(0, marginColumnQty).forEach(i -> IntStream.range(0, 3).forEach(j -> rowDataList.add(null)));
            tableBodyDataList.add(rowDataList);

            int mergedRowIndex = rowIndex.incrementAndGet();
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(mergedRowIndex, 2, 4));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(mergedRowIndex, 5, 7));

            if (mergeColumn) {
                mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(mergedRowIndex, 2, 7));
            }
        });

        // 方法检出限
        List<Object> rowDataList = new ArrayList<>();
        rowDataList.add(lowLimit ? PfConstant.METHOD_LOW_LIMIT : PfConstant.METHOD_LOW_VALUE);
        rowDataList.add("");

        IntStream.range(0, minResultColumnQty).forEach(i -> {
            List<EmcReportResultBean> testResultList = sampleReportResultList.stream().filter(r -> StringUtils.equals(r.getTestName(), testNameList.get(i))).collect(Collectors.toList());
            Arrays.stream(PfConstant.GAS_RESULT_KEYWORDS).forEach(keyword -> {
                String[] array = StringUtils.split(keyword, ",");
                EmcReportResultBean reportResult = testResultList.stream().filter(r -> Arrays.stream(array).anyMatch(itemName -> StringUtils.contains(r.getItemAliasName(), itemName))).findFirst().orElseGet(EmcReportResultBean::new);
                // 检出限及检出限单位
                String lowLimitValue = reportResult.getLowLimit();
                String limitUnitName = Optional.ofNullable(reportResult.getExt$Item("limitUnitName")).orElse(reportResult.getUnitName());
                // 检出量及检出量单位
                String detectedQuantity = reportResult.getExt$Item("detectedQuantity");
                String detectedQuantityUnit = reportResult.getExt$Item("detectedQuantityUnit");

                String limitValue = lowLimit ? (StringUtils.isBlank(lowLimitValue) ? "-" : lowLimitValue + limitUnitName)
                        : (StringUtils.isBlank(detectedQuantity) ? "-" : detectedQuantity + detectedQuantityUnit);
                rowDataList.add(limitValue);
            });
        });

        IntStream.range(0, marginColumnQty).forEach(i -> IntStream.range(0, 3).forEach(j -> rowDataList.add(null)));
        tableBodyDataList.add(rowDataList);

        // 合并列配置
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 3, 0, 1));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 3, 2, 4));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 3, 5, 7));

        if (mergeColumn) {
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 3, 2, 7));
        }

        // 是否显示执行标准
        if (displayStd) {
            // 添加备注行
            tableBodyDataList.add(buildDynamicRowDataList(PfConstant.EXECUTIVE_STANDARD, report.getRemark(), PfConstant.GAS_RESULT_TABLE_HEADER_COLUMN_QTY));
            // 合并备注列
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 4, 2, PfConstant.GAS_RESULT_TABLE_HEADER_COLUMN_QTY - 2));
        }

        dynamicTableDTO.setMergedColumnList(mergedColumnList);
    }

    private void resizeTableColumnWidth(XWPFTable table) {
        int rightFixedColumnQty = displayStd ? 1 : 0;

        int leftFixedSumWidth = Arrays.stream(LEFT_FIXED_COLUMN_WIDTHS).sum();
        int rightFixedSumWidth = Arrays.stream(RIGHT_FIXED_COLUMN_WIDTHS).sum() * rightFixedColumnQty;

        int columnCount = sampling ? testCount * 3 : testCount;
        int newAvgWidth = (getTableWidth() - (leftFixedSumWidth + rightFixedSumWidth)) / columnCount;

        List<Integer> columnWidthList = new ArrayList<>();
        Arrays.stream(LEFT_FIXED_COLUMN_WIDTHS).forEach(columnWidthList::add);
        IntStream.range(0, columnCount).forEach(index -> columnWidthList.add(newAvgWidth));

        int mergedColumnQty = sampling ? (MAX_DYNAMIC_COLUMN_QTY - testCount) * 3 : MAX_DYNAMIC_COLUMN_QTY - testCount;
        IntStream.range(0, mergedColumnQty).forEach(index -> columnWidthList.add(0));

        IntStream.range(0, rightFixedColumnQty).forEach(width -> columnWidthList.add(RIGHT_FIXED_COLUMN_WIDTHS[0]));

        List<XWPFTableRow> tableRows = table.getRows();
        tableRows.forEach(row -> {
            List<XWPFTableCell> tableCells = row.getTableCells();
            IntStream.range(0, tableCells.size()).forEach(index -> tableCells.get(index).setWidth(String.valueOf(columnWidthList.get(index))));
        });
    }

}
