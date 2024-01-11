package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 场调土壤检测结果表（场调土壤报告）
 * @author zhengxy@sunwayworld.com 2020年09月03日
 */
public class FactorySoilDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private EmcReportBean report;
    private List<EmcFolderBean> folderList;
    private Map<Long, Long> sampleIdFolderIdMap;
    private List<EmcReportResultBean> reportResultList;
    private List<EmcPropertyDataBean> propertyDataList;
    private boolean isLast;

    public FactorySoilDynamicTable(XWPFDocument document, EmcReportBean report, List<EmcFolderBean> folderList, Map<Long, Long> sampleIdFolderIdMap, List<EmcReportResultBean> reportResultList, List<EmcPropertyDataBean> propertyDataList, boolean isLast) {
        this.document = document;
        this.report = report;
        this.folderList = folderList;
        this.sampleIdFolderIdMap = sampleIdFolderIdMap;
        this.reportResultList = reportResultList;
        this.propertyDataList = propertyDataList;
        this.isLast = isLast;
    }

    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }

    @Override
    public String getTableTitle() {
        return EmcConstant.ENTRUSTED_SOIL_RESULT_TABLE_TITLE;
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

        List<Long> sampleIdList = new ArrayList<>(sampleIdFolderIdMap.keySet());
        sampleIdList.sort(Comparator.naturalOrder());
        Map<Long, String> folderIdNameMap = folderList.stream().collect(Collectors.toMap(EmcFolderBean::getId, EmcFolderBean::getMonitorPointName));

        // 最多展示3个采样深度，也即是对应3个采样单
        IntStream.range(0, Math.min(EmcConstant.ENTRUSTED_SOIL_RESULT_COLUMN_QTY, sampleIdList.size())).forEach(i -> {
            EmcPropertyDataBean propertyData = propertyDataList.stream().filter(p -> ObjectUtils.equals(p.getBizId(), sampleIdList.get(i))).findFirst().orElse(null);
            dynamicTitleList.add(folderIdNameMap.get(sampleIdFolderIdMap.get(sampleIdList.get(i))));
            // 采样深度对应采样单属性字段1
            dynamicTitleList.add((propertyData == null) ? null : propertyData.getExt1());
        });

        // 当采样单少于3个时，列数补齐
        IntStream.range(0, Math.max(0, EmcConstant.ENTRUSTED_SOIL_RESULT_COLUMN_QTY - sampleIdList.size())).forEach(i -> dynamicTitleList.addAll(Arrays.asList(null, null)));

        List<List<Object>> headerDataList = buildTableDynamicHeader(EmcConstant.ENTRUSTED_SOIL_RESULT_TABLE_HEADER_TITLES, EmcConstant.ENTRUSTED_SOIL_RESULT_TABLE_HEADER_COLUMN_QTY, dynamicTitleList);
        setTableTitle(EmcConstant.ENTRUSTED_SOIL_RESULT_TABLE_HEADER_TITLES, EmcConstant.ENTRUSTED_SOIL_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        // 将土壤报出结果为ND的替换成未检出
        reportResultList.stream().filter(r -> StringUtils.equals(EmcConstant.ND, r.getReportResult())).forEach(r -> r.setReportResult(EmcConstant.NOT_CHECK_OUT));

        // 过滤出样品值
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)).collect(Collectors.toList());

        List<Long> sampleIdList = sampleReportResultList.stream().map(EmcReportResultBean::getSampleId).distinct().sorted(Comparator.comparing(Function.identity())).collect(Collectors.toList());
        int minResultColumnQty = Math.min(EmcConstant.ENTRUSTED_SOIL_RESULT_COLUMN_QTY, sampleIdList.size());
        int marginColumnQty = Math.max(0, EmcConstant.ENTRUSTED_SOIL_RESULT_COLUMN_QTY - sampleIdList.size());

        // 按测试对报出结果进行分组，每个测试一行数据
        Map<String, List<EmcReportResultBean>> testResultMap = sampleReportResultList.stream().collect(Collectors.groupingBy(EmcReportResultBean::getTestName));
        testResultMap.forEach((testName, testResultList) -> {
            List<Object> rowDataList = new ArrayList<>();
            rowDataList.add(testName + "(" + Optional.ofNullable(testResultList.get(0).getUnitName()).orElse(EmcConstant.NO_UNIT) + ")");
            IntStream.range(0, minResultColumnQty).forEach(i -> {
                EmcReportResultBean reportResult = testResultList.stream().filter(r -> ObjectUtils.equals(r.getSampleId(), sampleIdList.get(i))).findFirst().orElse(null);
                rowDataList.add((reportResult == null) ? null : WordTemplateUtils.convertScientificToWordFormat(reportResult.getReportResult()));
            });
            IntStream.range(0, marginColumnQty).forEach(i -> rowDataList.add(null));
            tableBodyDataList.add(rowDataList);
        });

        // 添加备注行
        tableBodyDataList.add(buildRemarkRowDataList(report.getRemark(), EmcConstant.ENTRUSTED_SOIL_RESULT_TABLE_HEADER_COLUMN_QTY));

        // 合并备注列
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElse(new ArrayList<>());
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 2, 1, 3));

        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        return tableBodyDataList;
    }
}
