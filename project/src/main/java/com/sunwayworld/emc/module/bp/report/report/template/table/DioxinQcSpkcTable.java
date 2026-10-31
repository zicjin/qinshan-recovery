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
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 二噁英加标回收（二噁英质控报告）
 * @author zhengxy@sunwayworld.com 2022年02月21日
 */
public class DioxinQcSpkcTable implements DynamicTable {

    private static final Map<String, String> ITEM_CONVERTED_MAP = new HashMap<>();
    private static final String EXCLUDE_ITEM_KEYWORD = "37Cl4-2378-TCDD";

    private XWPFDocument document;
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

        ITEM_CONVERTED_MAP.put("13C-2378-TCDF", "<sup>13</sup>C-2378-TCDF");
        ITEM_CONVERTED_MAP.put("13C-23478-PeCDF", "<sup>13</sup>C-23478-PeCDF");
        ITEM_CONVERTED_MAP.put("13C-123478-HxCDF", "<sup>13</sup>C-123478-HxCDF");
        ITEM_CONVERTED_MAP.put("13C-123678-HxCDF", "<sup>13</sup>C-123678-HxCDF");
        ITEM_CONVERTED_MAP.put("13C-234678-HxCDF", "<sup>13</sup>C-234678-HxCDF");
        ITEM_CONVERTED_MAP.put("13C-123789-HxCDF", "<sup>13</sup>C-123789-HxCDF");
        ITEM_CONVERTED_MAP.put("13C-1234678-HpCDF", "<sup>13</sup>C-1234678-HpCDF");
        ITEM_CONVERTED_MAP.put("13C-1234789-HpCDF", "<sup>13</sup>C-1234789-HpCDF");
        ITEM_CONVERTED_MAP.put("13C-2378-TCDD", "<sup>13</sup>C-2378-TCDD");
        ITEM_CONVERTED_MAP.put("13C-12378-PeCDD", "<sup>13</sup>C-12378-PeCDD");
        ITEM_CONVERTED_MAP.put("13C-12378-PeCDF", "<sup>13</sup>C-12378-PeCDF");
        ITEM_CONVERTED_MAP.put("13C-123478-HxCDD", "<sup>13</sup>C-123478-HxCDD");
        ITEM_CONVERTED_MAP.put("13C-123678-HxCDD", "<sup>13</sup>C-123678-HxCDD");
        ITEM_CONVERTED_MAP.put("13C-1234678-HpCDD", "<sup>13</sup>C-1234678-HpCDD");
        ITEM_CONVERTED_MAP.put("13C-OCDD", "<sup>13</sup>C-OCDD");

        ITEM_CONVERTED_MAP.put("37Cl4-2378-TCDD", "<sup>37</sup>Cl<sub>4</sub>-2378-TCDD");
    }

    public DioxinQcSpkcTable(XWPFDocument document, EmcOrderBean order, List<EmcOrderTaskResultBean> taskResultList, int tableIndex) {
        this.document = document;
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
        return String.format(PfConstant.DIOXIN_QC_SPKC_RESULT_TITLE, WordTemplateUtils.handleOrderCategory(order.getOrderCategory()));
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
        return WordTemplateUtils.cm2Ints(Arrays.asList(4.8D, 0D, 0D, 0D));
    }

    @Override
    public ParagraphAlignment getTitleAlignment() {
        return ParagraphAlignment.LEFT;
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<String> dynamicTitleList = new ArrayList<>();
        dynamicTitleList.add(order.getOrderNo());

        // 合并列配置
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 1, 3));
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        String[] tableTitle = PfConstant.DIOXIN_QC_SPKC_RESULT_TABLE_HEADER_TITLES;
        List<List<Object>> headerDataList = buildTableDynamicHeader(tableTitle, PfConstant.DIOXIN_QC_SPKC_RESULT_TABLE_HEADER_COLUMN_QTY, dynamicTitleList);
        setTableTitle(tableTitle, PfConstant.DIOXIN_QC_SPKC_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        List<EmcOrderTaskResultBean> validResultList = taskResultList.stream().filter(r -> StringUtils.endsWith(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[6]) || StringUtils.endsWith(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[7])).collect(Collectors.toList());
        validResultList.forEach(result -> IntStream.range(0, PfConstant.DIOXIN_ITEM_GROUP_KEYWORDS.length).forEach(index -> {
            if (StringUtils.contains(result.getItemAliasName(), PfConstant.DIOXIN_ITEM_GROUP_KEYWORDS[index])) {
                result.setExt$Item("resultItemGroup", PfConstant.DIOXIN_ITEM_GROUPS[index]);
            }
        }));

        validResultList.sort(Comparator.comparing(r -> (r.getExt$Item("resultItemGroup") + "").length()));
        Collections.reverse(validResultList);

        // 对于结果为ND的，需要显示 N.D.(<检出限)，如果是土壤类型，显示N.D.(<检出限*1000)
        validResultList.stream().filter(r -> StringUtils.equals(PfConstant.ND, r.getReportResult())).forEach(r -> r.setReportResult(formatNdValue(r.getLowLimit())));
        Map<String, List<EmcOrderTaskResultBean>> testReportResultMap = validResultList.stream().collect(Collectors.groupingBy(r -> r.getExt$Item("resultItemGroup"), LinkedHashMap::new, Collectors.toList()));

        boolean isGas = isGas();
        testReportResultMap.forEach((itemGroup, resultList) -> {
            resultList.sort(Comparator.comparing(EmcOrderTaskResultBean::getItemOrderNo));
            List<String> uniqueItemList = resultList.stream().map(r -> StringUtils.replace(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[6], "")
                    .replace(PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[7], "")).distinct().collect(Collectors.toList());
            uniqueItemList.forEach(itemName -> {
                if (!isGas && StringUtils.contains(itemName, EXCLUDE_ITEM_KEYWORD)) {
                    return;
                }

                List<Object> rowDataList = new ArrayList<>();
                rowDataList.add(Optional.ofNullable(ITEM_CONVERTED_MAP.get(itemName)).orElse(itemName));

                /*EmcOrderTaskResultBean actualResult = resultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), itemName) && StringUtils.contains(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[7])).findFirst().orElseGet(EmcOrderTaskResultBean::new);
                rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(actualResult.getReportResult()));

                rowDataList.add(actualResult.getExt$Item("theoryContent"));*/

                EmcOrderTaskResultBean recoveryResult = resultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), itemName) && StringUtils.contains(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[6])).findFirst().orElseGet(EmcOrderTaskResultBean::new);
                rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(recoveryResult.getReportResult()));

                //rowDataList.add(actualResult.getExt$Item("judgeBasis"));
                rowDataList.add(String.join("-", recoveryResult.getLowLimit(), recoveryResult.getHighLimit()));

                rowDataList.add(PfConstant.DIOXIN_QC_DUP_RESULT_QUALIFIED);

                tableBodyDataList.add(rowDataList);
            });
        });

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
            return PfConstant.GAS_DIOXIN_ORIGINAL_RECORD_TABLE_HEADER_TITLES;
        }

        boolean isSoil = Arrays.stream(PfConstant.DIOXIN_SOIL_KEYWORDS).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));
        boolean isSolidWaste = Arrays.stream(PfConstant.DIOXIN_SOLID_WASTE_ORDER_CATEGORIES).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));
        if (isSoil || isSolidWaste) {
            return PfConstant.SOIL_DIOXIN_ORIGINAL_RECORD_TABLE_HEADER_TITLES;
        }

        boolean isWater = Arrays.stream(PfConstant.DIOXIN_WATER_KEYWORDS).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));
        if (isWater) {
            return PfConstant.WATER_DIOXIN_ORIGINAL_RECORD_TABLE_HEADER_TITLES;
        }

        return PfConstant.WATER_DIOXIN_ORIGINAL_RECORD_TABLE_HEADER_TITLES;
    }

    private boolean isGas() {
        return Arrays.stream(PfConstant.DIOXIN_GAS_KEYWORDS).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));
    }

    private String formatNdValue(String lowLimit) {
        return String.format(PfConstant.DIOXIN_QC_ND_REPLACED_VALUE, lowLimit);
    }
}
