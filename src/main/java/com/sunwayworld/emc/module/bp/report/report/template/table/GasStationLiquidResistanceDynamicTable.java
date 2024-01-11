package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 加油站液阻检测结果表（油气回收）
 * 
 * @author zhengxy@sunwayworld.com 2020年09月03日
 */
public class GasStationLiquidResistanceDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private List<EmcReportResultBean> reportResultList;
    private boolean isLast;

    public GasStationLiquidResistanceDynamicTable(XWPFDocument document, List<EmcReportResultBean> reportResultList, boolean isLast) {
        this.document = document;
        this.reportResultList = reportResultList;
        this.isLast = isLast;
    }

    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }

    @Override
    public String getTableTitle() {
        return EmcConstant.LIQUID_RESISTANCE_RESULT_TABLE_TITLE;
    }

    @Override
    public int getTableIndex() {
        return 0;
    }

    @Override
    public boolean isLast() {
        return this.isLast;
    }

    @Override
    public int getDynamicTableIndex() {
        return 6;
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> headerDataList = buildTableDynamicHeader(EmcConstant.LIQUID_RESISTANCE_RESULT_TABLE_HEADER_TITLES, EmcConstant.LIQUID_RESISTANCE_RESULT_TABLE_HEADER_COLUMN_QTY, null);
        setTableTitle(EmcConstant.LIQUID_RESISTANCE_RESULT_TABLE_HEADER_TITLES, EmcConstant.LIQUID_RESISTANCE_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        // 合并列
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 1, 3));
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        // 合并行
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, 0, 1));
        dynamicTableDTO.setMergedRowList(mergedRowList);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        Map<Long, List<EmcReportResultBean>> orderResultMap = reportResultList.stream().collect(Collectors.groupingBy(EmcReportResultBean::getOrderId));

        orderResultMap.forEach((orderId, orderResultList) -> {
            List<Object> rowDataList = new ArrayList<>();
            Arrays.stream(EmcConstant.LIQUID_RESISTANCE_MATCH_ITEM_KEYWORDS).forEach(keyword -> {
                rowDataList.add(getSpecifiedItemReportResult(orderResultList, keyword));
            });
            tableBodyDataList.add(rowDataList);
        });

        // 液阻最大压力限值(Pa)
        List<Object> rowDataList = new ArrayList<>();
        rowDataList.add(EmcConstant.LIQUID_RESISTANCE_MAX_KPA_TITLE);
        Arrays.stream(EmcConstant.LIQUID_RESISTANCE_MAX_KPA_MATCH_ITEM_KEYWORDS).forEach(keyword -> {
            rowDataList.add(getSpecifiedItemReportResult(reportResultList, keyword));
        });
        tableBodyDataList.add(rowDataList);

        tableBodyDataList.add(buildDynamicRowDataList(EmcConstant.CONCLUSION_TITLE, EmcConstant.DEFAULT_CONCLUSION, EmcConstant.LIQUID_RESISTANCE_RESULT_TABLE_HEADER_COLUMN_QTY));

        // 合并备注列
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElse(new ArrayList<>());
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 1, 1, 3));

        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        return tableBodyDataList;
    }

    /**
     * 获取指定分析项的报告结果（使用包含关系来过滤，而非等于）
     * 
     * @param reportResultList
     * @param itemName
     * @return
     */
    private String getSpecifiedItemReportResult(List<EmcReportResultBean> reportResultList, String itemName) {
        EmcReportResultBean reportResult = reportResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), itemName)).findFirst().orElseGet(EmcReportResultBean::new);

        return WordTemplateUtils.convertScientificToWordFormat(reportResult.getReportResult());
    }
}
