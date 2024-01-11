package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;

/**
 * 场调地下水检测结果表（场调地下水报告）
 * @author zhengxy@sunwayworld.com 2020年09月03日
 */
public class FactoryGroundwaterDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private EmcReportBean report;
    private List<EmcFolderBean> folderList;
    private List<EmcOrderBean> orderList;
    private List<EmcReportResultBean> reportResultList;
    private boolean isLast;

    public FactoryGroundwaterDynamicTable(XWPFDocument document, EmcReportBean report, List<EmcFolderBean> folderList, List<EmcOrderBean> orderList, List<EmcReportResultBean> reportResultList, boolean isLast) {
        this.document = document;
        this.report = report;
        this.folderList = folderList;
        this.orderList = orderList;
        this.reportResultList = reportResultList;
        this.isLast = isLast;
    }

    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }

    @Override
    public String getTableTitle() {
        return EmcConstant.FACTORY_GROUNDWATER_RESULT_TABLE_TITLE;
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
        dynamicTitleList.add(folderList.get(0).getOrderCategory());
        dynamicTitleList.add(WordTemplateUtils.getDate(reportResultList.stream().map(EmcReportResultBean::getSampleBeginDate).collect(Collectors.toList())));

        List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
        folderIdList.sort(Comparator.naturalOrder());
        Map<Long, String> folderIdNameMap = folderList.stream().collect(Collectors.toMap(EmcFolderBean::getId, EmcFolderBean::getMonitorPointName));
        Map<Long, String> orderIdDescMap = orderList.stream().collect(Collectors.toMap(EmcOrderBean::getId, EmcOrderBean::getOrderDesc));

        // 一个表格最多展示3个点位
        IntStream.range(0, Math.min(EmcConstant.FACTORY_GROUNDWATER_RESULT_COLUMN_QTY, folderIdList.size())).forEach(i -> {
            dynamicTitleList.add(folderIdNameMap.get(folderIdList.get(i)));
            EmcReportResultBean result = reportResultList.stream().filter(r -> ObjectUtils.equals(r.getFolderId(), folderIdList.get(i))).findFirst().orElse(null);
            dynamicTitleList.add((result == null) ? null : orderIdDescMap.get(result.getOrderId()));
        });

        // 当点位少于3个时，列上数据补齐
        IntStream.range(0, Math.max(0, EmcConstant.FACTORY_GROUNDWATER_RESULT_COLUMN_QTY - folderIdList.size())).forEach(i -> dynamicTitleList.addAll(Arrays.asList(null, null)));

        List<List<Object>> headerDataList = buildTableDynamicHeader(EmcConstant.FACTORY_GROUNDWATER_RESULT_TABLE_HEADER_TITLES, EmcConstant.FACTORY_GROUNDWATER_RESULT_TABLE_HEADER_COLUMN_QTY, dynamicTitleList);
        setTableTitle(EmcConstant.FACTORY_GROUNDWATER_RESULT_TABLE_HEADER_TITLES, EmcConstant.FACTORY_GROUNDWATER_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        // 对于水的报告，如果结果为ND，则在报告中显示检出限+L，实际参与均值计算时，取1/2检出限计算
        reportResultList.stream().filter(r -> EmcConstant.ND.equals(r.getReportResult())).forEach(r -> r.setReportResult(r.getLowLimit() + "L"));

        List<Long> folderIdList = reportResultList.stream().map(EmcReportResultBean::getFolderId).distinct().collect(Collectors.toList());
        folderIdList.sort(Comparator.naturalOrder());
        int minResultColumnQty = Math.min(EmcConstant.FACTORY_GROUNDWATER_RESULT_COLUMN_QTY, folderIdList.size());
        int marginColumnQty = Math.max(0, EmcConstant.FACTORY_GROUNDWATER_RESULT_COLUMN_QTY - folderIdList.size());

        // 按测试对报出结果进行分组，每个测试一行数据
        Map<String, List<EmcReportResultBean>> testResultMap = reportResultList.stream().collect(Collectors.groupingBy(EmcReportResultBean::getTestName));
        testResultMap.forEach((testName, testResultList) -> {
            List<Object> rowDataList = new ArrayList<>();
            rowDataList.add(testName + "(" + Optional.ofNullable(testResultList.get(0).getUnitName()).orElse(EmcConstant.NO_UNIT) + ")");
            IntStream.range(0, minResultColumnQty).forEach(i -> {
                // 对点位下的所有样品的报出结果求均值
                List<EmcReportResultBean> folderResultList = testResultList.stream().filter(r -> ObjectUtils.equals(r.getFolderId(), folderIdList.get(i))).collect(Collectors.toList());
                rowDataList.add(calcAvgResult(folderResultList));
            });
            IntStream.range(0, marginColumnQty).forEach(i -> rowDataList.add(null));
            tableBodyDataList.add(rowDataList);
        });

        // 添加备注行
        tableBodyDataList.add(buildRemarkRowDataList(report.getRemark(), EmcConstant.FACTORY_GROUNDWATER_RESULT_TABLE_HEADER_COLUMN_QTY));

        // 合并备注列
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElse(new ArrayList<>());
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 2, 1, 3));

        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        return tableBodyDataList;
    }

    private String calcAvgResult(List<EmcReportResultBean> reportResultList) {
        if (reportResultList.isEmpty()) {
            return null;
        }

        if (reportResultList.size() == 1) {
            return WordTemplateUtils.convertScientificToWordFormat(reportResultList.get(0).getReportResult());
        }

        List<String> reportResultValueList = reportResultList.stream().map(EmcReportResultBean::getReportResult).collect(Collectors.toList());
        IntStream.range(0, reportResultList.size()).forEach(i -> {
            String ndReplacedValue = reportResultValueList.get(i);
            if (ndReplacedValue.contains("L")) {
                reportResultValueList.set(i, (NumberUtils.parseDouble(ndReplacedValue.replace("L", "")) / 2) + "");
            }
        });

        OptionalDouble average = reportResultValueList.stream().filter(NumberUtils::isNumber).mapToDouble(r -> NumberUtils.parseDouble(WordTemplateUtils.convert(r))).average();
        return average.isPresent() ? average.getAsDouble() + "" : null;
    }
}
