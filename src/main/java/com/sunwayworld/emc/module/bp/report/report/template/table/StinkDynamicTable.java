package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.framework.utils.NumberUtils;

/**
 * 臭气浓度检测结果-油烟（油烟报告）
 * @author zhengxy@sunwayworld.com 2020年09月03日
 */
public class StinkDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private EmcReportBean report;
    private List<EmcFolderBean> folderList;
    private List<EmcReportResultBean> reportResultList;
    private List<EmcRunBean> runList;
    private List<EmcOrderTaskResultBean> parameterResultList;
    private boolean isLast;

    public StinkDynamicTable(XWPFDocument document, EmcReportBean report, List<EmcFolderBean> folderList, List<EmcReportResultBean> reportResultList, List<EmcRunBean> runList, List<EmcOrderTaskResultBean> parameterResultList, boolean isLast) {
        this.document = document;
        this.report = report;
        this.folderList = folderList;
        this.reportResultList = reportResultList;
        this.runList = runList;
        this.setParameterResultList(parameterResultList);
        this.isLast = isLast;
    }

    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }

    @Override
    public String getTableTitle() {
        return EmcConstant.STINK_RESULT_TABLE_TITLE;
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

        // 获取采样日期，如果采样日期不一致，则取最小采样日期和最大的采样日期
        dynamicTitleList.add(WordTemplateUtils.getDate(reportResultList.stream().map(EmcReportResultBean::getSampleBeginDate).distinct().collect(Collectors.toList())));

        // 获取分析日期
        dynamicTitleList.add(WordTemplateUtils.getDate(runList.stream().map(EmcRunBean::getAnalysisBeginDate).distinct().collect(Collectors.toList())));

        // 合并列配置
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElse(new ArrayList<>());
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 1, 2));
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        List<List<Object>> headerDataList = buildTableDynamicHeader(EmcConstant.STINK_RESULT_TABLE_HEADER_TITLES, EmcConstant.STINK_RESULT_TABLE_HEADER_COLUMN_QTY, dynamicTitleList);
        setTableTitle(EmcConstant.STINK_RESULT_TABLE_HEADER_TITLES, EmcConstant.STINK_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        // 对报出结果按点位进行分组
        Map<Long, List<EmcReportResultBean>> folderReportResultMap = reportResultList.stream().collect(Collectors.groupingBy(EmcReportResultBean::getFolderId));
        Map<Long, String> folderIdNameMap = folderList.stream().collect(Collectors.toMap(EmcFolderBean::getId, EmcFolderBean::getMonitorPointName));

        // 合并行配置
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElse(new ArrayList<>());
        AtomicInteger folderMergedRowIndex = new AtomicInteger(1);
        AtomicInteger testMergedRowIndex = new AtomicInteger(1);

        folderReportResultMap.forEach((folderId, resultList) -> {
            // 对点位下的报告结果按检测项目进行分组
            Map<String, List<EmcReportResultBean>> testResultMap = resultList.stream().collect(Collectors.groupingBy(EmcReportResultBean::getTestName));
            testResultMap.forEach((testName, testResultList) -> {
                testResultList.sort(Comparator.comparing(EmcReportResultBean::getOrderNo));
                // 取最大值
                EmcReportResultBean maxReportResult = testResultList.stream().filter(r -> NumberUtils.isNumber(r.getReportResult())).max(Comparator.comparing(r -> NumberUtils.parseDouble(r.getReportResult()))).orElseGet(EmcReportResultBean::new);

                testResultList.forEach(result -> {
                    List<Object> rowDataList = new ArrayList<>();
                    rowDataList.add(folderIdNameMap.get(folderId));
                    rowDataList.add(result.getTestName());
                    rowDataList.add(result.getOrderNo());
                    rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(result.getReportResult()));
                    rowDataList.add(maxReportResult.getReportResult());

                    tableBodyDataList.add(rowDataList);
                });

                // 合并检测项目行
                int startRowIndex = testMergedRowIndex.get() + 1;
                int endRowIndex = testMergedRowIndex.addAndGet(testResultList.size());
                mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(1, startRowIndex, endRowIndex));
                // 合并最大值行
                mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(4, startRowIndex, endRowIndex));
            });

            // 合并点位行
            mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, folderMergedRowIndex.get() + 1, folderMergedRowIndex.addAndGet(resultList.size())));
        });
        dynamicTableDTO.setMergedRowList(mergedRowList);

        // 添加备注行
        tableBodyDataList.add(buildRemarkRowDataList(report.getRemark(), EmcConstant.STINK_RESULT_TABLE_HEADER_COLUMN_QTY));

        // 合并备注列
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElse(new ArrayList<>());
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 1, 1, 4));

        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        return tableBodyDataList;
    }

	public List<EmcOrderTaskResultBean> getParameterResultList() {
		return parameterResultList;
	}

	public void setParameterResultList(List<EmcOrderTaskResultBean> parameterResultList) {
		this.parameterResultList = parameterResultList;
	}
}
