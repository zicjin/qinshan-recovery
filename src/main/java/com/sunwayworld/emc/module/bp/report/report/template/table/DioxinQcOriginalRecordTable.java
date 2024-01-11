package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 样品检出限结果表（二噁英质控报告）
 * @author zhengxy@sunwayworld.com 2022年02月25日
 */
public class DioxinQcOriginalRecordTable extends DioxinOriginalRecordTable {

    private EmcOrderBean order;
    private List<EmcReportResultBean> reportResultList;

    public DioxinQcOriginalRecordTable(XWPFDocument document, EmcReportBean report, EmcOrderBean order, List<EmcReportResultBean> reportResultList) {
        super(document, report, order, reportResultList);
        this.order = order;
        this.reportResultList = reportResultList;
    }

    @Override
    public int getDynamicTableIndex() {
        return 2;
    }

    @Override
    public String getTableTitle() {
        return String.format(PfConstant.DIOXIN_QC_LIMIT_RESULT_TITLE, WordTemplateUtils.handleOrderCategory(getOrder().getOrderCategory()));
    }

    @Override
    public List<Integer> getColumnWidthList() {
        return WordTemplateUtils.cm2Ints(Arrays.asList(1.30D, 4.42D, 2.51D, 2.58D, 2.6D, 2.6D, 0D));
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<String> dynamicTitleList = new ArrayList<>();
        dynamicTitleList.add(order.getOrderNo());

        // 合并列配置
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 0, 1));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 2, 6));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(1, 0, 1));
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        String[] tableTitle = lookupTableTitle();
        List<List<Object>> headerDataList = buildTableDynamicHeader(tableTitle, PfConstant.DIOXIN_QC_LIMIT_RESULT_TABLE_HEADER_COLUMN_QTY, dynamicTitleList);
        setTableTitle(tableTitle, PfConstant.DIOXIN_QC_LIMIT_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

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

        AtomicInteger mergedRowIndex = new AtomicInteger(1);
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
                EmcReportResultBean convertResult = resultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), itemName) && StringUtils.contains(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[1])).findFirst().orElseGet(EmcReportResultBean::new);
                String coefficient = convertResult.getExt$Item("coefficient");

                rowDataList.add(coefficient);
                rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(calcResult(compResult.getLowLimit(), coefficient)));
                rowDataList.add(lookupJudgeBasicValue());
                rowDataList.add(PfConstant.DIOXIN_QC_QUALIFIED);
                tableBodyDataList.add(rowDataList);
            });

            int startRowIndex = mergedRowIndex.get() + 1;
            int endRowIndex = mergedRowIndex.addAndGet(uniqueItemList.size());
            mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, startRowIndex, endRowIndex));
        });

        // 合并判定依据和判定结果行
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(5, 2, tableBodyDataList.size() + 1));
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(6, 2, tableBodyDataList.size() + 1));

        // 添加备注行
        List<Object> remarkRowDataList = new ArrayList<>();
        remarkRowDataList.add(PfConstant.REMARK);
        remarkRowDataList.add("");
        remarkRowDataList.add(lookupRemark());
        IntStream.range(3, PfConstant.DIOXIN_QC_LIMIT_RESULT_TABLE_HEADER_COLUMN_QTY).forEach(i -> remarkRowDataList.add(""));
        tableBodyDataList.add(remarkRowDataList);

        // 合并列
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 1, 0, 1));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 1, 2, 6));

        dynamicTableDTO.setMergedRowList(mergedRowList);
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        return tableBodyDataList;
    }

    @Override
    protected String[] lookupTableTitle() {
        //气
        if (isGas()&&!isWasteGas()) {
            return PfConstant.DIOXIN_GAS_QC_LIMIT_RESULT_TABLE_HEADER_TITLES;
        }
        if (isGas()&&isWasteGas()) {
            return PfConstant.DIOXIN_GAS_WASTE_QC_LIMIT_RESULT_TABLE_HEADER_TITLES;
        }
        if (isSolidWaste()) {
            return PfConstant.DIOXIN_SOIL_WASTE_QC_LIMIT_RESULT_TABLE_HEADER_TITLES;
        }
        if (isSoil()) {
            return PfConstant.DIOXIN_SOIL_QC_LIMIT_RESULT_TABLE_HEADER_TITLES;
        }


        if (isWater()) {
            return PfConstant.DIOXIN_WATER_QC_LIMIT_RESULT_TABLE_HEADER_TITLES;
        }

        return PfConstant.DIOXIN_WATER_QC_LIMIT_RESULT_TABLE_HEADER_TITLES;
    }

    private String lookupJudgeBasicValue() {
        if (isSoil()) {
            return PfConstant.DIOXIN_QC_SOIL_JUDGE_BASIC;
        }

        if (isSolidWaste()) {
            return PfConstant.DIOXIN_QC_SOLID_WASTE_JUDGE_BASIC;
        }

        if (isOrganizedGas()) {
            return PfConstant.DIOXIN_QC_ORGANIZED_GAS_JUDGE_BASIC;
        }

        if (isUnorganizedGas()) {
            return PfConstant.DIOXIN_QC_UNORGANIZED_GAS_JUDGE_BASIC;
        }

        if (isEnvGas()) {
            return PfConstant.DIOXIN_QC_ENV_GAS_JUDGE_BASIC;
        }

        if (isFodder()) {
            return PfConstant.DIOXIN_QC_FODDER_JUDGE_BASIC;
        }

        return PfConstant.DIOXIN_QC_WATER_JUDGE_BASIC;
    }

    private String lookupRemark() {
        if (isSoil()) {
            return PfConstant.DIOXIN_QC_SOIL_REMARK;
        }

        if (isSolidWaste()) {
            return PfConstant.DIOXIN_QC_SOLID_WASTE_REMARK;
        }

        if (isOrganizedGas()) {
            return PfConstant.DIOXIN_QC_ORGANIZED_GAS_REMARK;
        }

        if (isUnorganizedGas()) {
            return PfConstant.DIOXIN_QC_UNORGANIZED_GAS_REMARK;
        }

        if (isEnvGas()) {
            return PfConstant.DIOXIN_QC_ENV_GAS_REMARK;
        }

        if (isFodder()) {
            return PfConstant.DIOXIN_QC_FODDER_REMARK;
        }

        return PfConstant.DIOXIN_QC_WATER_REMARK;
    }

    private String calcResult(String lowLimit, String coefficient) {
        String coefficientValue = StringUtils.replace(coefficient, "×", "");
        if (!NumberUtils.isNumber(lowLimit) || !NumberUtils.isNumber(coefficientValue)) {
            return null;
        }

        double result = ArithUtils.mul(lowLimit, coefficientValue);
        //此处必须用valueOf，用new BigDecimal会导致精度丢失
        String resultS = String.valueOf(result);
        BigDecimal bigDecimal = new BigDecimal(resultS);
        String resultA = bigDecimal.toPlainString();
        String resultFinal = NumberUtils.rounding(resultA, "EMC,2");
        return resultFinal;
    }

    private boolean isUnorganizedGas() {
        return Arrays.stream(PfConstant.DIOXIN_UNORGANIZED_GAS_KEYWORDS).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));
    }

    private boolean isEnvGas() {
        return Arrays.stream(PfConstant.DIOXIN_ENV_GAS_KEYWORDS).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));
    }


    private boolean isFodder() {
        return Arrays.stream(PfConstant.DIOXIN_FODDER_KEYWORDS).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));
    }
}
