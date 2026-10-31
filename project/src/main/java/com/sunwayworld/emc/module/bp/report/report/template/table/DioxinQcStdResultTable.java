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
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 有证标准物质检测结果记录表（二噁英质控报告）
 * @author zhengxy@sunwayworld.com 2022年02月23日
 */
public class DioxinQcStdResultTable implements DynamicTable {

    private static final Map<String, String> ITEM_CONVERTED_MAP = new HashMap<>();

    private XWPFDocument document;
    private EmcReportBean report;
    private List<EmcOrderBean> qcOrderList;
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

    public DioxinQcStdResultTable(XWPFDocument document, EmcReportBean report, List<EmcOrderBean> qcOrderList, List<EmcOrderTaskResultBean> taskResultList, int tableIndex) {
        this.document = document;
        this.report = report;
        this.qcOrderList = qcOrderList;
        this.taskResultList = taskResultList;
        this.tableIndex = tableIndex;
    }

    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }

    @Override
    public String getTableTitle() {
        return PfConstant.DIOXIN_QC_STD_RESULT_TITLE;
    }

    @Override
    public int getTableIndex() {
        return 0;
    }

    @Override
    public boolean isLast() {
        return true;
    }

    @Override
    public int getDynamicTableIndex() {
        return 2;
    }

    @Override
    public List<Integer> getColumnWidthList() {
        return WordTemplateUtils.cm2Ints(Arrays.asList(2.6D, 3.5D, 3.0D, 0D, 2.4D, 2.0D, 1.6D));
    }

    @Override
    public ParagraphAlignment getTitleAlignment() {
        return ParagraphAlignment.LEFT;
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> headerDataList = buildTableDynamicHeader(PfConstant.DIOXIN_QC_STD_RESULT_TABLE_HEADER_TITLES, PfConstant.DIOXIN_QC_STD_RESULT_TABLE_HEADER_COLUMN_QTY, null);
        setTableTitle(PfConstant.DIOXIN_QC_STD_RESULT_TABLE_HEADER_TITLES, PfConstant.DIOXIN_QC_STD_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        List<EmcOrderTaskResultBean> validResultList = taskResultList.stream().filter(r -> StringUtils.endsWith(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[8])).collect(Collectors.toList());
        validResultList.forEach(result -> IntStream.range(0, PfConstant.DIOXIN_ITEM_GROUP_KEYWORDS.length).forEach(index -> {
            if (StringUtils.contains(result.getItemAliasName(), PfConstant.DIOXIN_ITEM_GROUP_KEYWORDS[index])) {
                result.setExt$Item("resultItemGroup", PfConstant.DIOXIN_ITEM_GROUPS[index]);
            }
        }));

        validResultList.sort(Comparator.comparing(r -> (r.getExt$Item("resultItemGroup") + "").length()));
        Collections.reverse(validResultList);

        qcOrderList.sort(Comparator.comparing(EmcOrderBean::getOrderCategory).thenComparing(EmcOrderBean::getOrderNo));
        qcOrderList.forEach(qcOrder -> {
            List<EmcOrderTaskResultBean> curResultList = validResultList.stream().filter(result -> ObjectUtils.equals(qcOrder.getId(), result.getOrderId())).collect(Collectors.toList());
            Map<String, List<EmcOrderTaskResultBean>> testTaskResultMap = curResultList.stream().collect(Collectors.groupingBy(r -> r.getExt$Item("resultItemGroup"), LinkedHashMap::new, Collectors.toList()));

            testTaskResultMap.forEach((itemGroup, resultList) -> {
                resultList.sort(Comparator.comparing(EmcOrderTaskResultBean::getItemOrderNo));
                List<String> uniqueItemList = resultList.stream().map(r -> StringUtils.replace(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[8], "")).distinct().collect(Collectors.toList());
                uniqueItemList.forEach(itemName -> {
                    List<Object> rowDataList = new ArrayList<>();
                    EmcOrderTaskResultBean compResult = resultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), itemName) && StringUtils.contains(r.getItemAliasName(), PfConstant.DIOXIN_ORIGINAL_RECORD_KEYWORDS[8])).findFirst().orElseGet(EmcOrderTaskResultBean::new);

                    rowDataList.add(PfConstant.DIOXIN_QC_STD_ORDER_CATEGORY);
                    rowDataList.add(Optional.ofNullable(ITEM_CONVERTED_MAP.get(itemName)).orElse(itemName));
                    rowDataList.add(compResult.getOrderNo());

                    boolean emptyStandQty = StringUtils.isEmpty(compResult.getStandQty()) || StringUtils.isEmpty(compResult.getRn16());
                    boolean emptyLimit = StringUtils.isEmpty(compResult.getIndexLowLimit()) || StringUtils.isEmpty(compResult.getIndexHighLimit());
                    rowDataList.add(emptyStandQty ? null : String.join("±", compResult.getStandQty(), compResult.getRn16()));
                    rowDataList.add(emptyLimit ? null : String.join("~", compResult.getIndexLowLimit(), compResult.getIndexHighLimit()));

                    // 组份浓度直读分析项结果需要除以10
                    String reportResult = compResult.getOriginalResult();
                    reportResult = NumberUtils.isScientificNumber(reportResult) ? NumberUtils.formatNumber(NumberUtils.parseDouble(reportResult)) : reportResult;
                    if (NumberUtils.isNumber(reportResult)) {
                        reportResult = String.valueOf(ArithUtils.div(reportResult, "10"));
                        reportResult = NumberUtils.rounding(reportResult, "FDA,1");
                    }

                    rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(reportResult));
                    rowDataList.add(PfConstant.DIOXIN_QC_DUP_RESULT_QUALIFIED);
                    tableBodyDataList.add(rowDataList);
                });
            });
        });

        return tableBodyDataList;
    }
}
