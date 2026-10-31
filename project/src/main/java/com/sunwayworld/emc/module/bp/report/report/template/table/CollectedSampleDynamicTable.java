package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhengxy@sunwayworld.com 2020年09月08日
 */
public class CollectedSampleDynamicTable implements DynamicTable {

    private static final String WATER_KEYWORD = "水";
    private static final String SOIL_KEYWORD = "土";

    private XWPFDocument document;
    private EmcReportBean report;
    private List<EmcOrderBean> orderList;
    private List<EmcOrderTaskBean> orderTaskList;
    private List<EmcReportResultBean> reportResultList;
    private boolean isLast;

    public CollectedSampleDynamicTable(XWPFDocument document, EmcReportBean report, List<EmcOrderBean> orderList, List<EmcOrderTaskBean> orderTaskList, List<EmcReportResultBean> reportResultList, boolean isLast) {
        this.document = document;
        this.report = report;
        this.orderList = orderList;
        this.orderTaskList = orderTaskList;
        this.reportResultList = reportResultList;
        this.isLast = isLast;
    }

    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }

    @Override
    public String getTableTitle() {
        return String.format(EmcConstant.COLLECTED_SAMPLE_RESULT_TABLE_TITLE, report.getOrderCategory());
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
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<String> dynamicTitleList = new ArrayList<>();
        dynamicTitleList.add(orderList.get(0).getOrderCategory());
        dynamicTitleList.add(WordTemplateUtils.getDate(orderList.stream().map(EmcOrderBean::getSendDate).collect(Collectors.toList())));

        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 1, 2));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 3, 4));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 5, 6));
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        List<List<Object>> headerDataList = buildTableDynamicHeader(EmcConstant.COLLECTED_SAMPLE_RESULT_TABLE_HEADER_TITLES2, EmcConstant.COLLECTED_SAMPLE_RESULT_TABLE_HEADER_COLUMN_QTY2, dynamicTitleList);
        setTableTitle(EmcConstant.COLLECTED_SAMPLE_RESULT_TABLE_HEADER_TITLES2, EmcConstant.COLLECTED_SAMPLE_RESULT_TABLE_HEADER_COLUMN_QTY2, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElse(new ArrayList<>());

        orderList.stream().filter(order -> StringUtils.isEmpty(order.getClientOrderName())).forEach(order -> order.setClientOrderName(""));

        // 按原样品名称对样品进行分组
        Map<String, List<EmcOrderBean>> clientOrderMap = orderList.stream().sorted(Comparator.comparing(EmcOrderBean::getClientOrderName)).collect(Collectors.groupingBy(EmcOrderBean::getClientOrderName, LinkedHashMap::new, Collectors.toList()));

        Map<Long, EmcOrderTaskBean> orderTaskIdMap = orderTaskList.stream().collect(Collectors.toMap(EmcOrderTaskBean::getId, Function.identity()));

        // 过滤出样品值结果
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)).collect(Collectors.toList());

        AtomicInteger mergedRowIndex = new AtomicInteger(1);
        clientOrderMap.forEach((clientOrderName, clientOrderList) -> {
            Map<Long, EmcOrderBean> orderIdMap = clientOrderList.stream().collect(Collectors.toMap(EmcOrderBean::getId, Function.identity()));
            List<Long> orderIdList = clientOrderList.stream().map(EmcOrderBean::getId).sorted(Comparator.comparing(Function.identity())).collect(Collectors.toList());
            List<EmcReportResultBean> orderResultList = sampleReportResultList.stream().filter(result -> orderIdList.contains(result.getOrderId())).collect(Collectors.toList());

            orderResultList.sort(Comparator.comparing(EmcReportResultBean::getOrderId));
            replaceNdValue(clientOrderList.get(0), orderResultList);

            orderResultList.forEach(result -> {
                List<Object> rowDataList = new ArrayList<>();
                EmcOrderBean order = orderIdMap.get(result.getOrderId());
                EmcOrderTaskBean orderTask = orderTaskIdMap.get(result.getOrderTaskId());
                rowDataList.add(order.getClientOrderName());
                rowDataList.add(result.getTestName());
                rowDataList.add(order.getOrderDesc());
                rowDataList.add(Optional.ofNullable(orderTask.getOriginalOrderNo()).orElse(order.getLabelNo()));
                rowDataList.add(order.getOrderNo());
                rowDataList.add(result.getUnitName());
                rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(result.getReportResult()));
                tableBodyDataList.add(rowDataList);
            });
            mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, mergedRowIndex.get() + 1, mergedRowIndex.addAndGet(orderResultList.size())));
        });

        // 添加备注行
        tableBodyDataList.add(buildRemarkRowDataList(report.getRemark(), EmcConstant.COLLECTED_SAMPLE_RESULT_TABLE_HEADER_COLUMN_QTY2));

        // 合并备注列
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElse(new ArrayList<>());
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 1, 1, EmcConstant.COLLECTED_SAMPLE_RESULT_TABLE_HEADER_COLUMN_QTY2 - 1));

        dynamicTableDTO.setMergedColumnList(mergedColumnList);
        dynamicTableDTO.setMergedRowList(mergedRowList);

        return tableBodyDataList;
    }

    private void replaceNdValue(EmcOrderBean order, List<EmcReportResultBean> reportResultList) {
        if (StringUtils.contains(order.getOrderCategory(), WATER_KEYWORD)) {
            // 对于水的报告，如果结果为ND，则在报告中显示检出限+L，实际参与均值计算时，取1/2检出限计算
            reportResultList.stream().filter(r -> EmcConstant.ND.equals(r.getReportResult())).forEach(r -> r.setReportResult(r.getLowLimit() + "L"));
        } else if (StringUtils.contains(order.getOrderCategory(), SOIL_KEYWORD)) {
            // 土壤报告中，当结果为ND时，需要替换成未检出
            reportResultList.stream().filter(r -> EmcConstant.ND.equals(r.getReportResult())).forEach(r -> r.setReportResult(EmcConstant.NOT_CHECK_OUT));
        }
    }
}
