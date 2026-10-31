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

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 空白实验结果表（二噁英质控报告）
 * @author zhengxy@sunwayworld.com 2022年02月21日
 */
public class DioxinQcBlankTable implements DynamicTable {

    private static final Map<String, String> ITEM_CONVERTED_MAP = new HashMap<>();

    private XWPFDocument document;
    private EmcReportBean report;
    private EmcOrderBean order;
    private List<EmcOrderTaskResultBean> taskResultList;
    private int tableIndex;

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

    public DioxinQcBlankTable(XWPFDocument document, EmcReportBean report, EmcOrderBean order, List<EmcOrderTaskResultBean> taskResultList, int tableIndex) {
        this.document = document;
        this.report = report;
        this.order = order;
        this.taskResultList = taskResultList;
        this.tableIndex = tableIndex;
    }

    @Override
    public XWPFDocument getDocument() {
        return document;
    }

    @Override
    public String getTableTitle() {
        return PfConstant.DIOXIN_QC_BLANK_RESULT_TITLE;
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
        return true;
    }

    @Override
    public List<Integer> getColumnWidthList() {
        return Arrays.asList(650, 2506, 1945, 2279, 0, 0);
    }

    @Override
    public ParagraphAlignment getTitleAlignment() {
        return ParagraphAlignment.LEFT;
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<String> dynamicTitleList = new ArrayList<>();
        dynamicTitleList.add(order.getOrderNo());

        dynamicTitleList.addAll(lookupUnit());

        // 合并列配置
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 0, 1));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 2, 5));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(1, 0, 1));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(2, 0, 1));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(1, 4, 5));
        //mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(2, 4, 5));
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        // 合并行配置
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, 1, 2));
        dynamicTableDTO.setMergedRowList(mergedRowList);

        String[] tableTitle = lookupTableTitle();
        List<List<Object>> headerDataList = buildTableDynamicHeader(tableTitle, PfConstant.DIOXIN_ORIGINAL_RECORD_TABLE_HEADER_COLUMN_QTY, dynamicTitleList);
        setTableTitle(tableTitle, PfConstant.DIOXIN_ORIGINAL_RECORD_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        List<EmcOrderTaskResultBean> validResultList = taskResultList.stream().filter(r -> StringUtils.endsWith(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[0]) || StringUtils.endsWith(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[1])).collect(Collectors.toList());
        validResultList.forEach(result -> IntStream.range(0, PfConstant.DIOXIN_ITEM_GROUP_KEYWORDS.length).forEach(index -> {
            if (StringUtils.contains(result.getItemAliasName(), PfConstant.DIOXIN_ITEM_GROUP_KEYWORDS[index])) {
                result.setExt$Item("resultItemGroup", PfConstant.DIOXIN_ITEM_GROUPS[index]);
            }
        }));

        validResultList.sort(Comparator.comparing(r -> (r.getExt$Item("resultItemGroup") + "").length()));
        Collections.reverse(validResultList);

        // 对于结果为ND的，需要显示 N.D.(<检出限)，如果是土壤类型，显示N.D.(<检出限*1000)
        validResultList.stream().filter(r -> StringUtils.equals(PfConstant.ND, r.getReportResult())).forEach(r -> r.setReportResult(PfConstant.DIOXIN_QC_ND_REPLACED_VALUE));
        Map<String, List<EmcOrderTaskResultBean>> testReportResultMap = validResultList.stream().collect(Collectors.groupingBy(r -> r.getExt$Item("resultItemGroup"), LinkedHashMap::new, Collectors.toList()));

        // 合并行配置
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);

        AtomicInteger mergedRowIndex = new AtomicInteger(2);
        testReportResultMap.forEach((itemGroup, resultList) -> {
            resultList.sort(Comparator.comparing(EmcOrderTaskResultBean::getItemOrderNo));
            List<String> uniqueItemList = resultList.stream().map(r -> StringUtils.replace(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[0], "")
                    .replace(PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[1], "")).distinct().collect(Collectors.toList());
            uniqueItemList.forEach(itemName -> {
                List<Object> rowDataList = new ArrayList<>();
                rowDataList.add(itemGroup);
                rowDataList.add(Optional.ofNullable(ITEM_CONVERTED_MAP.get(itemName)).orElse(itemName));
                EmcOrderTaskResultBean compResult = resultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), itemName) && StringUtils.contains(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[0])).findFirst().orElseGet(EmcOrderTaskResultBean::new);
                rowDataList.add(compResult.getLowLimit());
                rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(compResult.getReportResult()));

                boolean isNd = PfConstant.DIOXIN_QC_ND_REPLACED_VALUE.equals(compResult.getReportResult());

                EmcOrderTaskResultBean convertResult = resultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), itemName) && StringUtils.contains(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[1])).findFirst().orElseGet(EmcOrderTaskResultBean::new);
                rowDataList.add(convertResult.getExt$Item("coefficient"));

                String convertValue = isNd ? PfConstant.DIOXIN_QC_ND_REPLACED_VALUE : WordTemplateUtils.convertScientificToWordFormat(convertResult.getReportResult());
                rowDataList.add(convertValue);
                tableBodyDataList.add(rowDataList);
            });

            int startRowIndex = mergedRowIndex.get() + 1;
            int endRowIndex = mergedRowIndex.addAndGet(uniqueItemList.size());
            mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, startRowIndex, endRowIndex));
        });

        // 平均含氧量和换算后浓度
        boolean displayConvertedResult = Constant.YES.equals(report.getExt$Item("displayConvertedResult"));
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
        if (displayConvertedResult) {
            EmcOrderTaskResultBean avgOxygenResult = taskResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[3])).findFirst().orElseGet(EmcOrderTaskResultBean::new);
            EmcOrderTaskResultBean basicOxygenResult = taskResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[4])).findFirst().orElseGet(EmcOrderTaskResultBean::new);
            EmcOrderTaskResultBean convertedResult = taskResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[5])).findFirst().orElseGet(EmcOrderTaskResultBean::new);
            tableBodyDataList.add(buildDynamicRowDataList(PfConstant.DIOXIN_ORIGINAL_RECORD_AVG_OXYGEN_RESULT, avgOxygenResult.getReportResult(), PfConstant.DIOXIN_ORIGINAL_RECORD_TABLE_HEADER_COLUMN_QTY));
            tableBodyDataList.add(buildDynamicRowDataList(String.format(PfConstant.DIOXIN_ORIGINAL_RECORD_CONVERTED_RESULT, basicOxygenResult.getReportResult() + "%"), convertedResult.getReportResult(), PfConstant.DIOXIN_ORIGINAL_RECORD_TABLE_HEADER_COLUMN_QTY));

            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(mergedRowList.get(mergedRowList.size() - 1).getEndRowIndex() + 1, 0, 2));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(mergedRowList.get(mergedRowList.size() - 1).getEndRowIndex() + 1, 3, 5));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(mergedRowList.get(mergedRowList.size() - 1).getEndRowIndex() + 2, 0, 2));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(mergedRowList.get(mergedRowList.size() - 1).getEndRowIndex() + 3, 3, 5));
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

    private String[] lookupTableTitle() {
        boolean isGas = Arrays.stream(PfConstant.DIOXIN_GAS_KEYWORDS).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));
        if (isGas) {
            return PfConstant.DIOXIN_GAS_QC_BLANK_RESULT_TABLE_HEADER_TITLES;
        }

        boolean isSoil = Arrays.stream(PfConstant.DIOXIN_SOIL_KEYWORDS).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));
        boolean isSolidWaste = Arrays.stream(PfConstant.DIOXIN_SOLID_WASTE_ORDER_CATEGORIES).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));
        if (isSoil || isSolidWaste) {
            return PfConstant.DIOXIN_SOIL_QC_BLANK_RESULT_TABLE_HEADER_TITLES;
        }

        boolean isWater = Arrays.stream(PfConstant.DIOXIN_WATER_KEYWORDS).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));
        if (isWater) {
            return PfConstant.DIOXIN_WATER_QC_BLANK_RESULT_TABLE_HEADER_TITLES;
        }

        return PfConstant.DIOXIN_WATER_QC_BLANK_RESULT_TABLE_HEADER_TITLES;
    }

    private List<String> lookupUnit() {
        List<String> unitList = new ArrayList<>();

        if (isSoil()) {
            String soilUnit = String.format(PfConstant.DIOXIN_SOIL_UNIT);
            IntStream.range(0, 4).forEach(index -> unitList.add(soilUnit));
            unitList.set(2,  "I-TEF");
            return unitList;
        }

/*        // 检出限单位
        EmcOrderTaskResultBean lowLimitResult = taskResultList.stream().filter(r -> StringUtils.endsWith(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[2])).findFirst().orElseGet(EmcOrderTaskResultBean::new);
        unitList.add(String.format(PfConstant.DIOXIN_UNIT_TITLE, Optional.ofNullable(lowLimitResult.getUnitName().replaceAll ("ng", "pg")).orElse("-")));

        // 组份浓度单位
        EmcOrderTaskResultBean compResult = taskResultList.stream().filter(r -> StringUtils.endsWith(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[0])).findFirst().orElseGet(EmcOrderTaskResultBean::new);
        unitList.add(String.format(PfConstant.DIOXIN_UNIT_TITLE, Optional.ofNullable(compResult.getUnitName().replaceAll ("ng", "pg")).orElse("-")));

        //毒性当量 I-TEF
        unitList.add(String.format( "I-TEF"));

        // 换算浓度单位
        EmcOrderTaskResultBean convertResult = taskResultList.stream().filter(r -> StringUtils.endsWith(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[1])).findFirst().orElseGet(EmcOrderTaskResultBean::new);
        unitList.add(String.format(PfConstant.DIOXIN_UNIT_NEW_TITLE, Optional.ofNullable(convertResult.getUnitName().replaceAll ("ng", "pg")).orElse("-")));*/
        unitList.add(String.format("pg"));
        unitList.add(String.format("pg"));
        unitList.add(String.format( "I-TEF"));
        unitList.add(String.format("pg"));
        return unitList;
    }

    private boolean isGas() {
        return Arrays.stream(PfConstant.DIOXIN_GAS_KEYWORDS).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));
    }

    private boolean isSoil() {
        boolean isSoil = Arrays.stream(PfConstant.DIOXIN_SOIL_KEYWORDS).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));
        boolean isSolidWaste = Arrays.stream(PfConstant.DIOXIN_SOLID_WASTE_ORDER_CATEGORIES).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));

        return isSoil || isSolidWaste;
    }

    private boolean isWater() {
        return Arrays.stream(PfConstant.DIOXIN_WATER_KEYWORDS).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));
    }

    private String formatNdValue(String lowLimit) {
        return String.format(PfConstant.DIOXIN_QC_ND_REPLACED_VALUE, lowLimit);
    }
}
