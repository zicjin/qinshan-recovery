package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 高分辨气相色谱-质谱仪分析原始记录
 * @author zhengxy@sunwayworld.com 2021年07月30日
 */
public class DioxinOriginalRecordTable implements DynamicTable {

    protected static final Map<String, String> ITEM_CONVERTED_MAP = new HashMap<>();

    private XWPFDocument document;
    private EmcReportBean report;
    private EmcOrderBean order;
    private List<EmcReportResultBean> reportResultList;

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
        ITEM_CONVERTED_MAP.put("1,2,3,7,8,9-H6CDF", "1,2,3,7,8,9-H<sub>6</sub>CDF");
        ITEM_CONVERTED_MAP.put("2,3,4,6,7,8-H6CDF", "2,3,4,6,7,8-H<sub>6</sub>CDF");
        ITEM_CONVERTED_MAP.put("1,2,3,4,6,7,8-H7CDF", "1,2,3,4,6,7,8-H<sub>7</sub>CDF");
        ITEM_CONVERTED_MAP.put("1,2,3,4,7,8,9-H7CDF", "1,2,3,4,7,8,9-H<sub>7</sub>CDF");
        ITEM_CONVERTED_MAP.put("O8CDF", "O<sub>8</sub>CDF");
    }

    public DioxinOriginalRecordTable(XWPFDocument document, EmcReportBean report, EmcOrderBean order, List<EmcReportResultBean> reportResultList) {
        this.document = document;
        this.report = report;
        this.order = order;
        this.reportResultList = reportResultList;
    }

    @Override
    public XWPFDocument getDocument() {
        return document;
    }

    @Override
    public String getTableTitle() {
        return PfConstant.DIOXIN_ORIGINAL_RECORD_TITLE;
    }

    @Override
    public int getTableIndex() {
        return 0;
    }

    @Override
    public int getDynamicTableIndex() {
        return 2;
    }

    @Override
    public boolean isLast() {
        return false;
    }

    @Override
    public List<Integer> getColumnWidthList() {
        //return Arrays.asList(612, 2506, 1945, 2279, 0, 0);
        return Arrays.asList(720, 2506, 1945, 2279, 0, 0);
    }

    public EmcReportBean getReport() {
        return report;
    }

    public EmcOrderBean getOrder() {
        return order;
    }

    public List<EmcReportResultBean> getReportResultList() {
        return reportResultList;
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<String> dynamicTitleList = new ArrayList<>();
        dynamicTitleList.add(WordTemplateUtils.handleOrderCategory(order.getOrderCategory()));
        dynamicTitleList.add(order.getOrderNo());

        // 过滤出样品值结果
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)).collect(Collectors.toList());
        EmcReportResultBean samplingResult = sampleReportResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), PfConstant.DIOXIN_SAMPLING_QUANTITY_ITEM_KEYWORD)).findFirst().orElseGet(EmcReportResultBean::new);
        dynamicTitleList.add(WordTemplateUtils.convertScientificToWordFormat(samplingResult.getReportResult()));

        // 检出限单位
        EmcReportResultBean lowLimitResult = sampleReportResultList.stream().filter(r -> StringUtils.endsWith(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[2])).findFirst().orElseGet(EmcReportResultBean::new);
        dynamicTitleList.add(String.format(PfConstant.DIOXIN_UNIT_TITLE, lowLimitResult.getUnitName()));

        // 组份浓度单位
        EmcReportResultBean compResult = sampleReportResultList.stream().filter(r -> StringUtils.endsWith(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[0])).findFirst().orElseGet(EmcReportResultBean::new);
        dynamicTitleList.add(String.format(PfConstant.DIOXIN_UNIT_TITLE, compResult.getUnitName()));

        //毒性当量 I-TEF
        dynamicTitleList.add(String.format( "I-TEF"));

        // 换算浓度单位
        EmcReportResultBean convertResult = sampleReportResultList.stream().filter(r -> StringUtils.endsWith(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[1])).findFirst().orElseGet(EmcReportResultBean::new);
        dynamicTitleList.add(String.format(PfConstant.DIOXIN_UNIT_NEW_TITLE, convertResult.getUnitName()));

        // 合并列配置
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 0, 1));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 2, 5));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(1, 0, 1));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(2, 0, 1));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(3, 0, 1));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(1, 4, 5));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(2, 4, 5));
        //mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(3, 4, 5));
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        // 合并行配置
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, 2, 3));
        dynamicTableDTO.setMergedRowList(mergedRowList);

        String[] tableTitle = lookupTableTitle();
        List<List<Object>> headerDataList = buildTableDynamicHeader(tableTitle, PfConstant.DIOXIN_ORIGINAL_RECORD_TABLE_HEADER_COLUMN_QTY, dynamicTitleList);
        setTableTitle(tableTitle, PfConstant.DIOXIN_ORIGINAL_RECORD_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        // 过滤出样品值结果
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)).collect(Collectors.toList());

        List<EmcReportResultBean> validResultList = sampleReportResultList.stream().filter(r -> StringUtils.endsWith(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[0]) || StringUtils.endsWith(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[1])).collect(Collectors.toList());
        //检出限的list
        List<EmcReportResultBean> limitResultList = sampleReportResultList.stream().filter(r -> StringUtils.endsWith(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[2])).collect(Collectors.toList());
        //去除检出限
        limitResultList.forEach(lr ->{
            lr.setItemAliasName(StringUtils.replace(lr.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[2], ""));
        });
        validResultList.forEach(vr ->{
            List<EmcReportResultBean> limitMatchResultList = limitResultList.stream().filter(lr ->vr.getItemAliasName().contains(lr.getItemAliasName())).collect(Collectors.toList());
            vr.setLowLimit(limitMatchResultList.get(0).getReportResult());
        });
        validResultList.forEach(result -> IntStream.range(0, PfConstant.DIOXIN_ITEM_GROUP_KEYWORDS.length).forEach(index -> {
            if (StringUtils.contains(result.getItemAliasName(), PfConstant.DIOXIN_ITEM_GROUP_KEYWORDS[index])) {
                result.setExt$Item("resultItemGroup", PfConstant.DIOXIN_ITEM_GROUPS[index]);
            }
        }));

        validResultList.sort(Comparator.comparing(r -> (r.getExt$Item("resultItemGroup") + "").length()));
        Collections.reverse(validResultList);

        // 对于结果为ND的，需要显示 N.D.(<检出限)，如果是土壤类型，显示N.D.(<检出限*1000)
        validResultList.stream().filter(r -> StringUtils.equals(PfConstant.ND, r.getReportResult())).forEach(r -> r.setReportResult(formatNdValue(r.getLowLimit())));
        Map<String, List<EmcReportResultBean>> testReportResultMap = validResultList.stream().collect(Collectors.groupingBy(r -> r.getExt$Item("resultItemGroup"), LinkedHashMap::new, Collectors.toList()));

        // 合并行配置
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);

        AtomicInteger mergedRowIndex = new AtomicInteger(3);
        testReportResultMap.forEach((itemGroup, resultList) -> {
            resultList.sort(Comparator.comparing(EmcReportResultBean::getItemOrderNo));
            List<String> uniqueItemList = resultList.stream().map(r -> StringUtils.replace(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[0], "")
                    .replace(PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[1], "")).distinct().collect(Collectors.toList());
            uniqueItemList.forEach(itemName -> {
                List<Object> rowDataList = new ArrayList<>();
                rowDataList.add(itemGroup);
                rowDataList.add(Optional.ofNullable(ITEM_CONVERTED_MAP.get(itemName)).orElse(itemName));
                EmcReportResultBean compResult = resultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), itemName) && StringUtils.contains(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[0])).findFirst().orElseGet(EmcReportResultBean::new);
                rowDataList.add(Optional.ofNullable(compResult.getLowLimit()).orElse("-"));
                rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(compResult.getReportResult()));
                EmcReportResultBean convertResult = resultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), itemName) && StringUtils.contains(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[1])).findFirst().orElseGet(EmcReportResultBean::new);
                rowDataList.add(convertResult.getExt$Item("coefficient"));
                rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(convertResult.getReportResult()));
                tableBodyDataList.add(rowDataList);
            });

            int startRowIndex = mergedRowIndex.get() + 1;
            int endRowIndex = mergedRowIndex.addAndGet(uniqueItemList.size());
            mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, startRowIndex, endRowIndex));
        });

        // 添加测定浓度行
        EmcReportResultBean finalResult = sampleReportResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), PfConstant.DIOXIN_REPORT_RESULT_ITEM_KEYWORD)).findFirst().orElseGet(EmcReportResultBean::new);
        tableBodyDataList.add(buildDynamicRowDataList(String.format(PfConstant.DIOXIN_ORIGINAL_RECORD_REPORT_RESULT, finalResult.getUnitName()), WordTemplateUtils.convertScientificToWordFormat(finalResult.getReportResult()), PfConstant.DIOXIN_ORIGINAL_RECORD_TABLE_HEADER_COLUMN_QTY));

        // 平均含氧量和换算后浓度
        boolean displayConvertedResult = Constant.YES.equals(report.getExt$Item("displayConvertedResult")) && isOrganizedGas();
        if (displayConvertedResult) {
            EmcReportResultBean avgOxygenResult = sampleReportResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[3])).findFirst().orElseGet(EmcReportResultBean::new);
            EmcReportResultBean basicOxygenResult = sampleReportResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[4])).findFirst().orElseGet(EmcReportResultBean::new);
            EmcReportResultBean convertedResult = sampleReportResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[5])).findFirst().orElseGet(EmcReportResultBean::new);
            tableBodyDataList.add(buildDynamicRowDataList(PfConstant.DIOXIN_ORIGINAL_RECORD_AVG_OXYGEN_RESULT, WordTemplateUtils.convertScientificToWordFormat(avgOxygenResult.getReportResult()), PfConstant.DIOXIN_ORIGINAL_RECORD_TABLE_HEADER_COLUMN_QTY));
            tableBodyDataList.add(buildDynamicRowDataList(String.format(PfConstant.DIOXIN_ORIGINAL_RECORD_CONVERTED_RESULT, Optional.ofNullable(WordTemplateUtils.convertScientificToWordFormat(basicOxygenResult.getReportResult())).orElse("") + "%"), WordTemplateUtils.convertScientificToWordFormat(convertedResult.getReportResult()), PfConstant.DIOXIN_ORIGINAL_RECORD_TABLE_HEADER_COLUMN_QTY));
        }

        // 添加备注行
        List<Object> remarkRowDataList = new ArrayList<>();
        remarkRowDataList.add(PfConstant.DIOXIN_ORIGINAL_RECORD_REMARK);
        IntStream.range(1, PfConstant.DIOXIN_ORIGINAL_RECORD_TABLE_HEADER_COLUMN_QTY).forEach(i -> remarkRowDataList.add(""));
        tableBodyDataList.add(remarkRowDataList);

        // 合并列
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
        if (displayConvertedResult) {
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(mergedRowList.get(mergedRowList.size() - 1).getEndRowIndex() + 1, 0, 2));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(mergedRowList.get(mergedRowList.size() - 1).getEndRowIndex() + 1, 3, 5));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(mergedRowList.get(mergedRowList.size() - 1).getEndRowIndex() + 2, 0, 2));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(mergedRowList.get(mergedRowList.size() - 1).getEndRowIndex() + 2, 3, 5));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(mergedRowList.get(mergedRowList.size() - 1).getEndRowIndex() + 3, 0, 2));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(mergedRowList.get(mergedRowList.size() - 1).getEndRowIndex() + 3, 3, 5));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(mergedRowList.get(mergedRowList.size() - 1).getEndRowIndex() + 4, 0, 5));
        } else {
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(mergedRowList.get(mergedRowList.size() - 1).getEndRowIndex() + 1, 0, 2));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(mergedRowList.get(mergedRowList.size() - 1).getEndRowIndex() + 1, 3, 5));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(mergedRowList.get(mergedRowList.size() - 1).getEndRowIndex() + 2, 0, 5));
        }

        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        return tableBodyDataList;
    }

    @Override
    public List<Object> buildDynamicRowDataList(String title, String content, int totalColumnQty) {
        List<Object> dynamicRowDataList = new ArrayList<>();
        dynamicRowDataList.add(title);
        dynamicRowDataList.add(null);
        dynamicRowDataList.add(null);
        dynamicRowDataList.add(content);
        IntStream.range(4, totalColumnQty).forEach(i -> dynamicRowDataList.add(""));

        return dynamicRowDataList;
    }

    protected String[] lookupTableTitle() {
        if (isGas()) {
            return PfConstant.GAS_DIOXIN_ORIGINAL_RECORD_TABLE_HEADER_TITLES;
        }

        if (isSoil() || isSolidWaste()) {
            return PfConstant.SOIL_DIOXIN_ORIGINAL_RECORD_TABLE_HEADER_TITLES;
        }

        if (isWater()) {
            return PfConstant.WATER_DIOXIN_ORIGINAL_RECORD_TABLE_HEADER_TITLES;
        }

        return PfConstant.WATER_DIOXIN_ORIGINAL_RECORD_TABLE_HEADER_TITLES;
    }

    protected String formatNdValue(String lowLimit) {
        return String.format(PfConstant.DIOXIN_ND_REPLACED_VALUE, lowLimit);
    }

    protected boolean isGas() {
        return Arrays.stream(PfConstant.DIOXIN_GAS_KEYWORDS).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));
    }

    protected boolean isOrganizedGas() {
        return Arrays.stream(PfConstant.DIOXIN_ORGANIZED_GAS_KEYWORDS).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));
    }

    protected boolean isSoil() {
        return Arrays.stream(PfConstant.DIOXIN_SOIL_KEYWORDS).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));
    }

    protected boolean isSolidWaste() {
        return Arrays.stream(PfConstant.DIOXIN_SOLID_WASTE_ORDER_CATEGORIES).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));
    }

    protected boolean isWater() {
        return Arrays.stream(PfConstant.DIOXIN_WATER_KEYWORDS).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));
    }

    //废气
    protected boolean isWasteGas() {
        return Arrays.stream(PfConstant.DIOXIN_GAS_WASTE_KEYWORDS).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));
    }

}
