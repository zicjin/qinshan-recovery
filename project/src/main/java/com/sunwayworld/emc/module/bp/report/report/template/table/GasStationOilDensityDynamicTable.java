package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 油气排放浓度检测结果
 * @author zhengxy@sunwayworld.com 2020年09月03日
 */
public class GasStationOilDensityDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private List<EmcFolderBean> folderList;
    private List<EmcReportResultBean> reportResultList;
    private boolean isLast;

    public GasStationOilDensityDynamicTable(XWPFDocument document, List<EmcFolderBean> folderList, List<EmcReportResultBean> reportResultList, boolean isLast) {
        this.document = document;
        this.folderList = folderList;
        this.reportResultList = reportResultList;
        this.isLast = isLast;
    }

    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }

    @Override
    public String getTableTitle() {
        return EmcConstant.OIL_GAS_DENSITY_RESULT_TABLE_TITLE;
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
        List<List<Object>> headerDataList = buildTableDynamicHeader(EmcConstant.OIL_GAS_DENSITY_RESULT_TABLE_HEADER_TITLES, EmcConstant.OIL_GAS_DENSITY_RESULT_TABLE_HEADER_COLUMN_QTY, null);
        setTableTitle(EmcConstant.OIL_GAS_DENSITY_RESULT_TABLE_HEADER_TITLES, EmcConstant.OIL_GAS_DENSITY_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        // 合并列
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 3, 4));
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        // 合并行
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, 0, 1));
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(1, 0, 1));
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(2, 0, 1));
        dynamicTableDTO.setMergedRowList(mergedRowList);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        Map<Long, String> folderIdNameMap = folderList.stream().collect(Collectors.toMap(EmcFolderBean::getId, EmcFolderBean::getMonitorPointName));

        // 合并行
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);

        // 过滤出样品值结果
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)).collect(Collectors.toList());

        // 过滤出总均值
        List<EmcReportResultBean> totalReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_TOTALAVERAGE)).collect(Collectors.toList());

        // 按测试项目进行分组
        Map<String, List<EmcReportResultBean>> testResultMap = sampleReportResultList.stream().collect(Collectors.groupingBy(EmcReportResultBean::getTestName));
        AtomicInteger testMergedRowIndex = new AtomicInteger(1);
        AtomicInteger folderMergedRowIndex = new AtomicInteger(1);
        testResultMap.forEach((testName, testResultList) -> {
            // 对测试按点位进行分组
            Map<Long, List<EmcReportResultBean>> folderResultMap = testResultList.stream().collect(Collectors.groupingBy(EmcReportResultBean::getFolderId));

            folderResultMap.forEach((folderId, folderResultList) -> {
                // 获取点位下测试总均值
                EmcReportResultBean totalAvgResult = totalReportResultList.stream().filter(f -> ObjectUtils.equals(f.getFolderId(), folderId) && StringUtils.equals(f.getTestName(), testName)).findFirst().orElseGet(EmcReportResultBean::new);

                // 对点位下的样品按样品编号排序
                folderResultList.sort(Comparator.comparing(EmcReportResultBean::getOrderNo));
                // String avgResult = calcAvgResult(folderResultList);
                folderResultList.forEach(result -> {
                    List<Object> rowDataList = new ArrayList<>();
                    rowDataList.add(testName);
                    rowDataList.add(folderIdNameMap.get(folderId));
                    rowDataList.add(result.getOrderNo());
                    rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(result.getReportResult()));
                    rowDataList.add(totalAvgResult.getReportResult());
                    tableBodyDataList.add(rowDataList);
                });
                int startRowIndex = folderMergedRowIndex.get() + 1;
                int endRowIndex = folderMergedRowIndex.addAndGet(folderResultList.size());
                mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(1, startRowIndex, endRowIndex));
                mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(4, startRowIndex, endRowIndex));
            });

            mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, testMergedRowIndex.get() + 1, testMergedRowIndex.addAndGet(testResultList.size())));
        });
        dynamicTableDTO.setMergedRowList(mergedRowList);

        tableBodyDataList.add(buildDynamicRowDataList(EmcConstant.OIL_GAS_DENSITY_STD_RANGE_TITLE, EmcConstant.OIL_GAS_DENSITY_STD_RANGE_VALUE, EmcConstant.OIL_GAS_DENSITY_RESULT_TABLE_HEADER_COLUMN_QTY));
        tableBodyDataList.add(buildDynamicRowDataList(EmcConstant.CONCLUSION_TITLE, EmcConstant.DEFAULT_CONCLUSION, EmcConstant.OIL_GAS_DENSITY_RESULT_TABLE_HEADER_COLUMN_QTY));

        // 合并备注列
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElse(new ArrayList<>());
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size(), 1, 4));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 1, 1, 4));
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        return tableBodyDataList;
    }

    @SuppressWarnings("unused")
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
