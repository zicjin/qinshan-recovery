package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 油烟检测结果表（油烟报告）
 * @author zhengxy@sunwayworld.com 2020年09月03日
 */
public class LampblackDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private EmcReportBean report;
    private List<EmcFolderBean> folderList;
    private List<EmcReportResultBean> reportResultList;
    private List<EmcRunBean> runList;
    private List<EmcOrderTaskBean> folderOrderTaskList;
    private List<EmcOrderTaskResultBean> parameterResultList;
    private boolean isLast;

    public LampblackDynamicTable(XWPFDocument document, EmcReportBean report, List<EmcFolderBean> folderList, List<EmcReportResultBean> reportResultList, List<EmcRunBean> runList, List<EmcOrderTaskBean> folderOrderTaskList, List<EmcOrderTaskResultBean> parameterResultList, boolean isLast) {
        this.document = document;
        this.report = report;
        this.folderList = folderList;
        this.reportResultList = reportResultList;
        this.runList = runList;
        this.folderOrderTaskList = folderOrderTaskList;
        this.parameterResultList = parameterResultList;
        this.isLast = isLast;
    }

    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }

    @Override
    public String getTableTitle() {
        return EmcConstant.LAMPBLACK_RESULT_TABLE_TITLE;
    }

    @Override
    public int getTableIndex() {
        return 0;
    }

    @Override
    public boolean isLast() {
        // 如果不做臭气浓度检测，那么油烟结果表就是最后一张表
        List<Long> stinkOrderTaskIdList = folderOrderTaskList.stream().filter(t -> StringUtils.contains(t.getTestName(), EmcConstant.STINK_TEST_KEYWORD)).map(EmcOrderTaskBean::getId).collect(Collectors.toList());

        return stinkOrderTaskIdList.isEmpty();
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<String> dynamicTitleList = new ArrayList<>();

        // 获取采样日期，如果采样日期不一致，则取最小采样日期和最大的采样日期
        dynamicTitleList.add(WordTemplateUtils.getDate(reportResultList.stream().map(EmcReportResultBean::getSampleBeginDate).distinct().collect(Collectors.toList())));

        // 获取分析日期
        dynamicTitleList.add(WordTemplateUtils.getDate(runList.stream().map(EmcRunBean::getAnalysisBeginDate).distinct().collect(Collectors.toList())));

        // 排气罩投影面积分析项结果
        EmcOrderTaskResultBean projectionResult = parameterResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), EmcConstant.LAMPBLACK_VENT_HOOD_MATCH_ITEM_KEYWORD)).findFirst().orElseGet(EmcOrderTaskResultBean::new);
        dynamicTitleList.add(projectionResult.getReportResult());

        // 规模，此处的规模需要通过分析项的值进行转换
        EmcOrderTaskResultBean scaleResult = parameterResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), EmcConstant.LAMPBLACK_SCALE_MATCH_ITEM_KEYWORD)).findFirst().orElseGet(EmcOrderTaskResultBean::new);
        dynamicTitleList.add(scaleResult.getReportResult());

        // 合并列配置
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElse(new ArrayList<>());
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 0, 1));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 3, 4));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(1, 0, 1));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(1, 3, 4));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(2, 3, 5));
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        // 合并行配置
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElse(new ArrayList<>());
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, 2, 3));
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(1, 2, 3));
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(2, 2, 3));
        dynamicTableDTO.setMergedRowList(mergedRowList);

        List<List<Object>> headerDataList = buildTableDynamicHeader(EmcConstant.LAMPBLACK_RESULT_TABLE_HEADER_TITLES, EmcConstant.LAMPBLACK_RESULT_TABLE_HEADER_COLUMN_QTY, dynamicTitleList);
        setTableTitle(EmcConstant.LAMPBLACK_RESULT_TABLE_HEADER_TITLES, EmcConstant.LAMPBLACK_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        // 过滤出样品值结果
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)).collect(Collectors.toList());

        // 过滤出总均值
        //List<EmcReportResultBean> totalReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_TOTALAVERAGE)).collect(Collectors.toList());

        // 过滤出日均值
        List<EmcReportResultBean> dayReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_DAYAVERAGE)).collect(Collectors.toList());

        // 对报出结果按点位进行分组
        sampleReportResultList.sort(Comparator.comparing(EmcReportResultBean::getOrderId));
        Map<Long, List<EmcReportResultBean>> folderReportResultMap = sampleReportResultList.stream().collect(Collectors.groupingBy(EmcReportResultBean::getFolderId, TreeMap::new, Collectors.toList()));
        Map<Long, String> folderIdNameMap = folderList.stream().collect(Collectors.toMap(EmcFolderBean::getId, EmcFolderBean::getMonitorPointName));

        // 合并行配置
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElse(new ArrayList<>());
        AtomicInteger folderMergedRowIndex = new AtomicInteger(3);

        folderReportResultMap.forEach((folderId, resultList) -> {
            resultList.sort(Comparator.comparing(EmcReportResultBean::getOrderId));
            // 计算点位下排放速率均值
            //EmcReportResultBean totalAvgResult = totalReportResultList.stream().filter(result -> ObjectUtils.equals(folderId, result.getFolderId()) && StringUtils.contains(result.getItemAliasName(), EmcConstant.LAMPBLACK_SEARCH_TITLES[1])).findFirst().orElseGet(EmcReportResultBean::new);

            EmcReportResultBean totalAvgResult = dayReportResultList.stream().filter(result -> ObjectUtils.equals(folderId, result.getFolderId()) && StringUtils.contains(result.getItemAliasName(), EmcConstant.LAMPBLACK_SEARCH_TITLES[1])).findFirst().orElseGet(EmcReportResultBean::new);

            // 按频次和样品编号对结果进行分组
            Map<String, List<EmcReportResultBean>> frequencyOrderResultMap = resultList.stream().collect(Collectors.groupingBy(r -> r.getFrequency() + "$" + r.getOrderNo(), TreeMap::new, Collectors.toList()));

            // 合并点位行
            int startRowIndex = folderMergedRowIndex.get() + 1;
            int endRowIndex = folderMergedRowIndex.addAndGet(frequencyOrderResultMap.size());
            mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, startRowIndex, endRowIndex));
            // 合并均值行
            mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(5, startRowIndex, endRowIndex));

            AtomicInteger orderIndex = new AtomicInteger(0);
            frequencyOrderResultMap.forEach((freOrderNo, list) -> {
                String orderNo = StringUtils.split(freOrderNo, "$")[1];

                List<Object> rowDataList = new ArrayList<>();
                rowDataList.add(folderIdNameMap.get(folderId));
                rowDataList.add(String.format(EmcConstant.LAMPBLACK_DETECTION_FREQUENCY, orderIndex.incrementAndGet()));
                rowDataList.add(orderNo);
                Arrays.asList(EmcConstant.LAMPBLACK_SEARCH_TITLES).forEach(keyword -> rowDataList.add(getSpecifiedItemReportResult(list, keyword)));
                rowDataList.add(totalAvgResult.getReportResult());

                tableBodyDataList.add(rowDataList);
            });
        });
        dynamicTableDTO.setMergedRowList(mergedRowList);

        // 添加备注行
        tableBodyDataList.add(buildRemarkRowDataList(report.getRemark(), EmcConstant.LAMPBLACK_RESULT_TABLE_HEADER_COLUMN_QTY));

        // 合并备注列
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElse(new ArrayList<>());
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 3, 1, 5));

        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        return tableBodyDataList;
    }

    @Override
    public WordTemplateDynamicTableDTO buildTable() {
        // 创建臭气浓度检测结果
        List<Long> stinkOrderTaskIdList = folderOrderTaskList.stream().filter(t -> StringUtils.contains(t.getTestName(), EmcConstant.STINK_TEST_KEYWORD)).map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        if (!stinkOrderTaskIdList.isEmpty()) {
            // 过滤出样品值结果
            List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)).collect(Collectors.toList());
            List<EmcReportResultBean> stinkReportResultList = sampleReportResultList.stream().filter(r -> stinkOrderTaskIdList.contains(r.getOrderTaskId())).collect(Collectors.toList());
            StinkDynamicTable stinkDynamicTable = new StinkDynamicTable(document, report, folderList, stinkReportResultList, runList, parameterResultList, isLast);
            stinkDynamicTable.create();
        }

        return DynamicTable.super.buildTable();
    }

    private String getSpecifiedItemReportResult(List<EmcReportResultBean> reportResultList, String itemName) {
        EmcReportResultBean reportResult = reportResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), itemName)).findFirst().orElseGet(EmcReportResultBean::new);
        // 油烟排放浓度如果小于最大值的四分之一，则为无效结果
        if (StringUtils.equals(reportResult.getInvalidResult(), Constant.YES) && StringUtils.contains(reportResult.getItemAliasName(), EmcConstant.LAMPBLACK_SEARCH_TITLES[1])) {
            return String.format(EmcConstant.LAMPBLACK_INVALID_RESULT, reportResult.getReportResult());
        }

        return WordTemplateUtils.convertScientificToWordFormat(reportResult.getReportResult());
    }
}
