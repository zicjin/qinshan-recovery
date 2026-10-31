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
 * 加油站气液比检测结果表（油气回收）
 * @author zhengxy@sunwayworld.com 2020年09月03日
 */
public class GasStationLiquidRatioDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private List<EmcReportResultBean> reportResultList;
    private boolean isLast;

    public GasStationLiquidRatioDynamicTable(XWPFDocument document, List<EmcReportResultBean> reportResultList, boolean isLast) {
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
        return EmcConstant.GAS_LIQUID_RATIO_RESULT_TABLE_TITLE;
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
        List<List<Object>> headerDataList = buildTableDynamicHeader(EmcConstant.GAS_LIQUID_RATIO_RESULT_TABLE_HEADER_TITLES, EmcConstant.GAS_LIQUID_RATIO_RESULT_TABLE_HEADER_COLUMN_QTY, null);
        setTableTitle(EmcConstant.GAS_LIQUID_RATIO_RESULT_TABLE_HEADER_TITLES, EmcConstant.GAS_LIQUID_RATIO_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        Map<Long, List<EmcReportResultBean>> orderResultMap = reportResultList.stream().collect(Collectors.groupingBy(EmcReportResultBean::getOrderId));

        orderResultMap.forEach((orderId, orderResultList) -> {
            List<Object> rowDataList = new ArrayList<>();
            Arrays.stream(EmcConstant.GAS_LIQUID_RATIO_MATCH_ITEM_KEYWORDS).forEach(keyword -> {
                rowDataList.add(getSpecifiedItemReportResult(orderResultList, keyword));
            });
            tableBodyDataList.add(rowDataList);
        });

        tableBodyDataList.add(buildDynamicRowDataList(EmcConstant.GAS_LIQUID_RATIO_STD_RANGE_TITLE, EmcConstant.GAS_LIQUID_RATIO_STD_RANGE_VALUE, EmcConstant.GAS_LIQUID_RATIO_RESULT_TABLE_HEADER_COLUMN_QTY));
        tableBodyDataList.add(buildDynamicRowDataList(EmcConstant.CONCLUSION_TITLE, EmcConstant.DEFAULT_CONCLUSION, EmcConstant.GAS_LIQUID_RATIO_RESULT_TABLE_HEADER_COLUMN_QTY));

        // 合并备注列
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElse(new ArrayList<>());
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() - 1, 1, 4));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size(), 1, 4));
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        return tableBodyDataList;
    }

    /**
     * 获取指定分析项的报告结果（使用包含关系来过滤，而非等于）
     * @param reportResultList
     * @param itemName
     * @return
     */
    private String getSpecifiedItemReportResult(List<EmcReportResultBean> reportResultList, String itemName) {
        EmcReportResultBean reportResult = reportResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), itemName)).findFirst().orElseGet(EmcReportResultBean::new);

        return WordTemplateUtils.convertScientificToWordFormat(reportResult.getReportResult());
    }
}
