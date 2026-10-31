package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 有组织气体或固定污染源检测结果表（综合报告-验收业务）
 * @author zhengxy@sunwayworld.com 2020年09月03日
 */
public class CheckGasDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private List<EmcFolderBean> folderList;
    private List<EmcReportResultBean> reportResultList;
    private List<EmcOrderTaskResultBean> orderTaskResultList;
    private boolean isLast;

    public CheckGasDynamicTable(XWPFDocument document, List<EmcFolderBean> folderList, List<EmcReportResultBean> reportResultList, List<EmcOrderTaskResultBean> orderTaskResultList, boolean isLast) {
        this.document = document;
        this.folderList = folderList;
        this.reportResultList = reportResultList;
        this.orderTaskResultList = orderTaskResultList;
        this.isLast = isLast;
    }

    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }

    @Override
    public String getTableTitle() {
        return String.format(EmcConstant.CHECK_GAS_RESULT_TABLE_TITLE, folderList.get(0).getOrderCategory());
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
        // 合并列配置
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElse(new ArrayList<>());
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 5, 11));
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        // 合并行配置
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElse(new ArrayList<>());
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, 0, 1));
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(1, 0, 1));
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(2, 0, 1));
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(3, 0, 1));
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(4, 0, 1));
        dynamicTableDTO.setMergedRowList(mergedRowList);

        List<List<Object>> headerDataList = buildTableDynamicHeader(EmcConstant.CHECK_GAS_RESULT_TABLE_HEADER_TITLES, EmcConstant.CHECK_GAS_RESULT_TABLE_HEADER_COLUMN_QTY, null);
        setTableTitle(EmcConstant.CHECK_GAS_RESULT_TABLE_HEADER_TITLES, EmcConstant.CHECK_GAS_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();
        // 获取需要合并行的信息列表
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElse(new ArrayList<>());

        List<String> matchItemNameKeywordList = Arrays.asList(EmcConstant.CHECK_GAS_RESULT_MATCH_ITEM_KEYWORDS);

        AtomicInteger folderMergedRowIndex = new AtomicInteger(1);
        AtomicInteger testMergedRowIndex = new AtomicInteger(1);

        // 过滤出样品值结果
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)).collect(Collectors.toList());

        // 过滤出总均值
        List<EmcReportResultBean> totalReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_TOTALAVERAGE)).collect(Collectors.toList());

        sampleReportResultList.sort(Comparator.comparing(EmcReportResultBean::getFolderId));
        // 先根据点位对报告结果进行分组
        Map<Long, List<EmcReportResultBean>> folderReportResultMap = sampleReportResultList.stream().collect(Collectors.groupingBy(EmcReportResultBean::getFolderId, LinkedHashMap::new, Collectors.toList()));
        folderReportResultMap.forEach((k, v) -> {
            EmcFolderBean folder = folderList.stream().filter(f -> ObjectUtils.equals(f.getId(), k)).findFirst().orElseGet(EmcFolderBean::new);
            // 按照分析项组进行分组（按测试名称分组不太准确，一个测试下可能包含多个分析项组）
            Map<String, List<EmcReportResultBean>> testReportResultMap = v.stream().filter(r -> !StringUtils.isEmpty(r.getExt$Item("itemgroup"))).collect(Collectors.groupingBy(r -> r.getExt$Item("itemgroup")));

            int folderStartRowIndex = folderMergedRowIndex.get() + 1;

            testReportResultMap.forEach((k1, v1) -> {
                // 按照采样日期进行分组
                List<EmcReportResultBean> validResultList = v1.stream().filter(r -> !ObjectUtils.isEmpty(r.getSampleBeginDate())).collect(Collectors.toList());
                Map<String, List<EmcReportResultBean>> sampleDateResultMap = validResultList.stream().collect(Collectors.groupingBy(r -> DateTimeUtils.formatLocalDate(r.getSampleBeginDate()), TreeMap::new, Collectors.toList()));

                List<List<Object>> testRowDataList = new ArrayList<>();
                sampleDateResultMap.forEach((sampleDate, dateResultList) -> {
                    AtomicInteger frequencyIndex = new AtomicInteger(0);

                    // 再按照样品编号进行分组
                    Map<String, List<EmcReportResultBean>> dateOrderResultMap = dateResultList.stream().collect(Collectors.groupingBy(EmcReportResultBean::getOrderNo, TreeMap::new, Collectors.toList()));
                    dateOrderResultMap.forEach((orderNo, resultList) -> {
                        List<Object> rowDataList = new ArrayList<>();
                        rowDataList.add(folder.getMonitorPointName());
                        rowDataList.add(k1);
                        rowDataList.add(sampleDate);
                        rowDataList.add(String.format(EmcConstant.CHECK_GAS_RESULT_FREQUENCY, frequencyIndex.incrementAndGet()));

                        // 样品编号太长，去掉项目编号，项目编号占9位
                        rowDataList.add(orderNo.substring(9));
                        matchItemNameKeywordList.forEach(key -> {
                            EmcReportResultBean itemResult = resultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), key)).findFirst().orElse(null);
                            rowDataList.add((itemResult == null) ? null : WordTemplateUtils.convertScientificToWordFormat(itemResult.getReportResult()));
                        });
                        // 含氧量需要在另一个检测项目中取
                        EmcOrderTaskResultBean oxygenContentResult = orderTaskResultList.stream().filter(r -> ObjectUtils.equals(r.getFolderId(), k) && StringUtils.equals(orderNo, r.getOrderNo()) && StringUtils.equals(r.getItemAliasName(), EmcConstant.CHECK_GAS_MATCH_OTHER_TEST_ITEM_KEYWORD)).findFirst().orElse(null);
                        rowDataList.add((oxygenContentResult == null) ? null : WordTemplateUtils.convertScientificToWordFormat(oxygenContentResult.getReportResult()));
                        // 实测或折算浓度(排放浓度)平均值，当存在折算浓度时，就取折算浓度均值，否则取实测浓度均值
                        rowDataList.add(getEmissionConcentrationAvgResult(totalReportResultList, resultList.get(0).getFolderId(), k1));
                        // 排放速率平均值
                        rowDataList.add(getEmissionRateAvgResult(totalReportResultList, resultList.get(0).getFolderId(), k1));

                        testRowDataList.add(rowDataList);
                    });
                });

                tableBodyDataList.addAll(testRowDataList);

                // 对测试按样品编号分组，用于统计结果行数
                Map<String, List<EmcReportResultBean>> orderResultMap = v1.stream().collect(Collectors.groupingBy(EmcReportResultBean::getOrderNo, TreeMap::new, Collectors.toList()));

                int testStartRowIndex = testMergedRowIndex.get() + 1;
                int testEndRowIndex = testMergedRowIndex.addAndGet(orderResultMap.size());
                mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(1, testStartRowIndex, testEndRowIndex));
                mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(2, testStartRowIndex, testEndRowIndex));
                mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(10, testStartRowIndex, testEndRowIndex));
                mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(11, testStartRowIndex, testEndRowIndex));

                folderMergedRowIndex.addAndGet(orderResultMap.size());
            });

            int folderEndRowIndex = folderMergedRowIndex.get();
            mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, folderStartRowIndex, folderEndRowIndex));
        });

        dynamicTableDTO.setMergedRowList(mergedRowList);

        // 添加备注行
        tableBodyDataList.add(buildRemarkRowDataList("", EmcConstant.CHECK_GAS_RESULT_TABLE_HEADER_COLUMN_QTY));

        // 合并备注列
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElse(new ArrayList<>());
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(mergedRowList.get(mergedRowList.size() - 1).getEndRowIndex() + 1, 1, EmcConstant.CHECK_GAS_RESULT_TABLE_HEADER_COLUMN_QTY - 1));
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        return tableBodyDataList;
    }

    /**
     * 获取实测/折算浓度均值
     * @param totalReportResultList
     * @param folderId
     * @param testName
     * @return
     */
    private String getEmissionConcentrationAvgResult(List<EmcReportResultBean> totalReportResultList, Long folderId, String testName) {
        // 获取折算浓度（排放浓度）总均值
        List<EmcReportResultBean> emissionItemAvgResultList = totalReportResultList.stream().filter(result -> ObjectUtils.equals(result.getFolderId(), folderId)
                && StringUtils.equals(result.getTestName(), testName)
                && StringUtils.contains(result.getItemAliasName(), EmcConstant.CHECK_GAS_RESULT_MATCH_ITEM_KEYWORDS[1])).collect(Collectors.toList());

        if (!emissionItemAvgResultList.isEmpty()) {
            return emissionItemAvgResultList.get(0).getReportResult();
        }

        // 获取实测浓度总均值
        List<EmcReportResultBean> actualItemAvgResultList = totalReportResultList.stream().filter(result -> ObjectUtils.equals(result.getFolderId(), folderId)
                && StringUtils.equals(result.getTestName(), testName)
                && StringUtils.contains(result.getItemAliasName(), EmcConstant.CHECK_GAS_RESULT_MATCH_ITEM_KEYWORDS[0])).collect(Collectors.toList());

        if (!actualItemAvgResultList.isEmpty()) {
            return actualItemAvgResultList.get(0).getReportResult();
        }

        return null;
    }

    private String getEmissionRateAvgResult(List<EmcReportResultBean> totalReportResultList, Long folderId, String testName) {
        List<EmcReportResultBean> avgResultList = totalReportResultList.stream().filter(result -> ObjectUtils.equals(result.getFolderId(), folderId)
                && StringUtils.equals(result.getTestName(), testName)
                && StringUtils.contains(result.getItemAliasName(), EmcConstant.CHECK_GAS_RESULT_MATCH_ITEM_KEYWORDS[3])).collect(Collectors.toList());

        if (!avgResultList.isEmpty()) {
            return avgResultList.get(0).getReportResult();
        }

        return null;
    }

}
