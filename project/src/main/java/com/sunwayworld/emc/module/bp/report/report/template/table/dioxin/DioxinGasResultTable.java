package com.sunwayworld.emc.module.bp.report.report.template.table.dioxin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.constant.EmcReportConstant;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhengxy@sunwayworld.com 2023年09月14日
 */
public class DioxinGasResultTable implements DynamicTable {

    protected static final Map<String, String> ITEM_CONVERTED_MAP = new LinkedHashMap<>();

    private XWPFDocument document;
    private EmcFolderBean folder;
    private EmcOrderBean order;
    private List<EmcReportResultBean> reportResultList;
    private boolean endOfReport;
    private String tableTitle;

    static {
        ITEM_CONVERTED_MAP.put("2,3,7,8-T4CDD", "2,3,7,8-T<sub>4</sub>CDD");
        ITEM_CONVERTED_MAP.put("1,2,3,7,8-P5CDD", "1,2,3,7,8-P<sub>5</sub>CDD");
        ITEM_CONVERTED_MAP.put("1,2,3,4,7,8-H6CDD", "1,2,3,4,7,8-H<sub>6</sub>CDD");
        ITEM_CONVERTED_MAP.put("1,2,3,6,7,8-H6CDD", "1,2,3,6,7,8-H<sub>6</sub>CDD");
        ITEM_CONVERTED_MAP.put("1,2,3,7,8,9-H6CDD", "1,2,3,7,8,9-H<sub>6</sub>CDD");
        ITEM_CONVERTED_MAP.put("1,2,3,4,6,7,8-H7CDD", "1,2,3,4,6,7,8-H<sub>7</sub>CDD");
        ITEM_CONVERTED_MAP.put("O8CDD", "O<sub>8</sub>CDD");

        ITEM_CONVERTED_MAP.put("2,3,7,8-T4CDF", "2,3,7,8-T<sub>4</sub>CDF");
        ITEM_CONVERTED_MAP.put("1,2,3,7,8-P5CDF", "1,2,3,7,8-P<sub>5</sub>CDF");
        ITEM_CONVERTED_MAP.put("2,3,4,7,8-P5CDF", "2,3,4,7,8-P<sub>5</sub>CDF");
        ITEM_CONVERTED_MAP.put("1,2,3,4,7,8-H6CDF", "1,2,3,4,7,8-H<sub>6</sub>CDF");
        ITEM_CONVERTED_MAP.put("1,2,3,6,7,8-H6CDF", "1,2,3,6,7,8-H<sub>6</sub>CDF");
        ITEM_CONVERTED_MAP.put("2,3,4,6,7,8-H6CDF", "2,3,4,6,7,8-H<sub>6</sub>CDF");
        ITEM_CONVERTED_MAP.put("1,2,3,7,8,9-H6CDF", "1,2,3,7,8,9-H<sub>6</sub>CDF");
        ITEM_CONVERTED_MAP.put("1,2,3,4,6,7,8-H7CDF", "1,2,3,4,6,7,8-H<sub>7</sub>CDF");
        ITEM_CONVERTED_MAP.put("1,2,3,4,7,8,9-H7CDF", "1,2,3,4,7,8,9-H<sub>7</sub>CDF");
        ITEM_CONVERTED_MAP.put("O8CDF", "O<sub>8</sub>CDF");
    }

    public DioxinGasResultTable(XWPFDocument document, EmcFolderBean folder, EmcOrderBean order, List<EmcReportResultBean> reportResultList, boolean endOfReport, String tableTitle) {
        this.document = document;
        this.folder = folder;
        this.order = order;
        this.reportResultList = reportResultList;
        this.endOfReport = endOfReport;
        this.tableTitle = tableTitle;
    }

    @Override
    public XWPFDocument getDocument() {
        return document;
    }

    @Override
    public String getTableTitle() {
        return Optional.ofNullable(tableTitle).orElse(EmcReportConstant.DIOXIN_GAS_RESULT_TABLE_TITLE);
    }

    @Override
    public int getTableIndex() {
        return 3;
    }

    @Override
    public int getDynamicTableIndex() {
        return 3;
    }

    @Override
    public boolean isLast() {
        return false;
    }

    @Override
    public String getTableEndText() {
        return this.endOfReport ? EmcReportConstant.END_OF_REPORT : PfConstant.TABLE_END_TIP_MESSAGE;
    }

    @Override
    public List<Integer> getColumnWidthList() {
        return WordTemplateUtils.cm2Ints(Arrays.asList(0.89D, 1.26D, 2.6D, 0D, 0D, 0D, 0D, 0D));
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<String> dynamicTitleList = new ArrayList<>();
        String sampleTime = DateTimeUtils.formatLocalDate(order.getSampleBeginDate()) + (StringUtils.isEmpty(order.getSampleBeginTime()) ? "" : " " + order.getSampleBeginTime());
        dynamicTitleList.add(sampleTime);
        dynamicTitleList.add(folder.getMonitorPointName());

        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)).collect(Collectors.toList());

        Stream.of(EmcReportConstant.DIOXIN_GAS_RESULT_TITLE_KEYWORDS).forEach(keyword -> {
            EmcReportResultBean matchedResult = sampleReportResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), keyword)).findFirst().orElseGet(EmcReportResultBean::new);
            dynamicTitleList.add(matchedResult.getUnitName());
        });

        // 合并列配置
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 0, 1));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 2, 3));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 5, 7));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(1, 0, 2));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(1, 6, 7));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(2, 0, 2));
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, 1, 2));
        dynamicTableDTO.setMergedRowList(mergedRowList);

        List<List<Object>> headerDataList = buildTableDynamicHeader(EmcReportConstant.DIOXIN_GAS_RESULT_TABLE_HEADER_TITLES, EmcReportConstant.DIOXIN_GAS_RESULT_TABLE_HEADER_COLUMN_QTY, dynamicTitleList);
        setTableTitle(EmcReportConstant.DIOXIN_GAS_RESULT_TABLE_HEADER_TITLES, EmcReportConstant.DIOXIN_GAS_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        if (endOfReport) {
            dynamicTableDTO.setTableRemarkList(Arrays.asList(EmcReportConstant.DIOXIN_OTHER_RESULT_REMARKS));
        }

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        // 过滤出样品值结果
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)).collect(Collectors.toList());
        List<EmcReportResultBean> validResultList = sampleReportResultList.stream().filter(r -> StringUtils.endsWith(r.getItemAliasName(), EmcReportConstant.DIOXIN_ITEM_KEYWORDS[0]) || StringUtils.endsWith(r.getItemAliasName(), EmcReportConstant.DIOXIN_ITEM_KEYWORDS[1])).collect(Collectors.toList());

        validResultList.stream().filter(result -> EmcConstant.ND.equals(result.getReportResult())).forEach(result -> result.setReportResult(EmcReportConstant.DIOXIN_ND_RESULT));

        validResultList.forEach(result -> IntStream.range(0, EmcReportConstant.DIOXIN_ITEM_GROUP_KEYWORDS.length).forEach(index -> {
            if (StringUtils.contains(result.getItemAliasName(), EmcReportConstant.DIOXIN_ITEM_GROUP_KEYWORDS[index])) {
                result.setExt$Item("resultItemGroup", EmcReportConstant.DIOXIN_ITEM_GROUPS[index]);
            }
        }));

        Map<String, List<EmcReportResultBean>> testReportResultMap = validResultList.stream().collect(Collectors.groupingBy(r -> r.getExt$Item("resultItemGroup"), LinkedHashMap::new, Collectors.toList()));

        // 合并行配置
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);

        List<String> resultItemNameList = new ArrayList<>(ITEM_CONVERTED_MAP.keySet());

        AtomicInteger mergedRowIndex = new AtomicInteger(2);
        AtomicInteger mergedColumnIndex = new AtomicInteger(2);
        testReportResultMap.forEach((itemGroup, resultList) -> {
            resultList.sort(Comparator.comparing(EmcReportResultBean::getItemOrderNo));
            List<String> uniqueItemList = resultList.stream().map(r -> StringUtils.replace(r.getItemAliasName(), EmcReportConstant.DIOXIN_ITEM_KEYWORDS[0], "")
                    .replace(EmcReportConstant.DIOXIN_ITEM_KEYWORDS[1], "")).distinct().collect(Collectors.toList());

            int groupIndex = Arrays.asList(EmcReportConstant.DIOXIN_ITEM_GROUPS).indexOf(itemGroup);
            String groupKeyword = EmcReportConstant.DIOXIN_ITEM_GROUP_KEYWORDS[groupIndex];

            resultItemNameList.stream().filter(itemName -> StringUtils.contains(itemName, groupKeyword)).forEach(itemName -> {
                List<Object> rowDataList = new ArrayList<>();
                rowDataList.add(itemGroup);
                rowDataList.add(Optional.ofNullable(ITEM_CONVERTED_MAP.get(itemName)).orElse(itemName));
                rowDataList.add("");

                EmcReportResultBean compResult = resultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), itemName) && StringUtils.contains(r.getItemAliasName(), EmcReportConstant.DIOXIN_GAS_RESULT_ITEM_KEYWORDS[0])).findFirst().orElseGet(EmcReportResultBean::new);
                rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(compResult.getReportResult()));
                rowDataList.add(Optional.ofNullable(compResult.getLowLimit()).orElse("-"));

                EmcReportResultBean convertResult = resultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), itemName) && StringUtils.contains(r.getItemAliasName(), EmcReportConstant.DIOXIN_GAS_RESULT_ITEM_KEYWORDS[2])).findFirst().orElseGet(EmcReportResultBean::new);
                rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(convertResult.getReportResult()));

                EmcReportResultBean factorResult = resultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), itemName) && StringUtils.contains(r.getItemAliasName(), EmcReportConstant.DIOXIN_GAS_RESULT_ITEM_KEYWORDS[3])).findFirst().orElseGet(EmcReportResultBean::new);
                rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(factorResult.getReportResult()));

                String multiplyResult = "-";
                if (NumberUtils.isNumber(convertResult.getReportResult()) && NumberUtils.isNumber(factorResult.getReportResult())) {
                    multiplyResult = NumberUtils.rounding(ArithUtils.mul(convertResult.getReportResult(), factorResult.getReportResult()) + "", "EMC,2");
                }
                rowDataList.add(multiplyResult);

                tableBodyDataList.add(rowDataList);

                mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(mergedColumnIndex.incrementAndGet(), 1, 2));
            });

            /*uniqueItemList.forEach(itemName -> {
                List<Object> rowDataList = new ArrayList<>();
                rowDataList.add(itemGroup);
                rowDataList.add(Optional.ofNullable(ITEM_CONVERTED_MAP.get(itemName)).orElse(itemName));
                rowDataList.add("");

                EmcReportResultBean compResult = resultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), itemName) && StringUtils.contains(r.getItemAliasName(), EmcReportConstant.DIOXIN_GAS_RESULT_ITEM_KEYWORDS[0])).findFirst().orElseGet(EmcReportResultBean::new);
                rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(compResult.getReportResult()));
                rowDataList.add(Optional.ofNullable(compResult.getLowLimit()).orElse("-"));

                EmcReportResultBean convertResult = resultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), itemName) && StringUtils.contains(r.getItemAliasName(), EmcReportConstant.DIOXIN_GAS_RESULT_ITEM_KEYWORDS[2])).findFirst().orElseGet(EmcReportResultBean::new);
                rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(convertResult.getReportResult()));

                EmcReportResultBean factorResult = resultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), itemName) && StringUtils.contains(r.getItemAliasName(), EmcReportConstant.DIOXIN_GAS_RESULT_ITEM_KEYWORDS[3])).findFirst().orElseGet(EmcReportResultBean::new);
                rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(factorResult.getReportResult()));

                String multiplyResult = "-";
                if (NumberUtils.isNumber(convertResult.getReportResult()) && NumberUtils.isNumber(factorResult.getReportResult())) {
                    multiplyResult = NumberUtils.rounding(ArithUtils.mul(convertResult.getReportResult(), factorResult.getReportResult()) + "", "EMC,2");
                }
                rowDataList.add(multiplyResult);

                tableBodyDataList.add(rowDataList);

                mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(mergedColumnIndex.incrementAndGet(), 1, 2));
            });*/

            int startRowIndex = mergedRowIndex.get() + 1;
            int endRowIndex = mergedRowIndex.addAndGet(uniqueItemList.size());
            mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, startRowIndex, endRowIndex));
        });

        // 添加测定浓度行
        EmcReportResultBean finalResult = sampleReportResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), EmcReportConstant.DIOXIN_REPORT_RESULT_ITEM_KEYWORD)).findFirst().orElseGet(EmcReportResultBean::new);
        List<Object> rowDataList = new ArrayList<>();
        rowDataList.add(EmcReportConstant.DIOXIN_ORIGINAL_RECORD_REPORT_RESULT);
        rowDataList.add(null);
        rowDataList.add(null);
        IntStream.range(0, 4).forEach(index -> rowDataList.add("————"));
        rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(finalResult.getReportResult()));
        tableBodyDataList.add(rowDataList);

        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(mergedRowList.get(mergedRowList.size() - 1).getEndRowIndex() + 1, 0, 2));
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        return tableBodyDataList;
    }
}
