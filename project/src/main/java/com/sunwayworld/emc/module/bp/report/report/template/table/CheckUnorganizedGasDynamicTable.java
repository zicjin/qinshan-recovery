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
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemGroupBean;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;

/**
 * 无组织气体检测结果表（综合报告-验收业务）
 * @author zhengxy@sunwayworld.com 2020年09月03日
 */
public class CheckUnorganizedGasDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private EmcReportBean report;
    private List<EmcFolderBean> folderList;
    private List<EmcItemGroupBean> itemGroupList;
    private List<EmcGroupItemBean> groupItemList;
    private List<EmcReportResultBean> reportResultList;
    private List<EmcPropertyDataBean> propertyDataList;
    private List<CoreFileBean> coreFileList;
    private boolean isLast;

    public CheckUnorganizedGasDynamicTable(XWPFDocument document, EmcReportBean report, List<EmcFolderBean> folderList, List<EmcItemGroupBean> itemGroupList, List<EmcGroupItemBean> groupItemList, List<EmcReportResultBean> reportResultList, List<EmcPropertyDataBean> propertyDataList, List<CoreFileBean> coreFileList, boolean isLast) {
        this.document = document;
        this.report = report;
        this.folderList = folderList;
        this.setItemGroupList(itemGroupList);
        this.setGroupItemList(groupItemList);
        this.reportResultList = reportResultList;
        this.propertyDataList = propertyDataList;
        this.coreFileList = coreFileList;
        this.isLast = isLast;
    }

    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }

    @Override
    public String getTableTitle() {
        return String.format(EmcConstant.CHECK_UNORGANIZED_GAS_RESULT_TABLE_TITLE, folderList.get(0).getOrderCategory());
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
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 4, 5));
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        // 合并行配置
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElse(new ArrayList<>());
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, 0, 1));
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(1, 0, 1));
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(2, 0, 1));
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(3, 0, 1));
        dynamicTableDTO.setMergedRowList(mergedRowList);

        List<List<Object>> headerDataList = buildTableDynamicHeader(EmcConstant.CHECK_UNORGANIZED_GAS_RESULT_TABLE_HEADER_TITLES, EmcConstant.CHECK_UNORGANIZED_GAS_RESULT_TABLE_HEADER_COLUMN_QTY, null);
        setTableTitle(EmcConstant.CHECK_UNORGANIZED_GAS_RESULT_TABLE_HEADER_TITLES, EmcConstant.CHECK_UNORGANIZED_GAS_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();
        // 获取需要合并行的信息列表
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElse(new ArrayList<>());

        Map<Long, String> folderIdNameMap = folderList.stream().collect(Collectors.toMap(EmcFolderBean::getId, EmcFolderBean::getMonitorPointName));

        // 过滤出样品值结果，并且排除气象参数
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE) && !StringUtils.contains(result.getTestName(), EmcConstant.PARAMETER_TEST_MATCH_KEYWORD)).collect(Collectors.toList());

        // 先根据分析项别名对报告结果进行分组，主要用于处理VOCs多组分的情况，如果直接使用测试名称分组，那么针对VOCs的都会合并到一起
        Map<String, List<EmcReportResultBean>> testReportResultMap = sampleReportResultList.stream().collect(Collectors.groupingBy(result -> result.getExt$Item("itemgroup")));
        testReportResultMap.forEach((k, v) -> {
            int newStartRowIndex = mergedRowList.get(mergedRowList.size() - 1).getEndRowIndex() + 1;
            int newEndRowIndex = newStartRowIndex + v.size() - 1;
            mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, newStartRowIndex, newEndRowIndex));
            mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(1, newStartRowIndex, newEndRowIndex));

            AtomicInteger frequencyIndex = new AtomicInteger();
            // 按样品编号对结果进行排序
            List<EmcReportResultBean> sortedResultList = v.stream().sorted(Comparator.comparing(EmcReportResultBean::getOrderNo)).collect(Collectors.toList());
            sortedResultList.forEach(r -> {
                List<Object> rowDataList = new ArrayList<>();
                rowDataList.add(k);
                rowDataList.add(DateTimeUtils.formatLocalDate(r.getSampleBeginDate()));
                rowDataList.add(String.format(EmcConstant.CHECK_UNORGANIZED_GAS_RESULT_FREQUENCY, frequencyIndex.incrementAndGet()));
                rowDataList.add(folderIdNameMap.get(r.getFolderId()));
                rowDataList.add(r.getOrderNo());
                rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(r.getReportResult()));
                tableBodyDataList.add(rowDataList);
            });
        });

        // 添加备注行
        tableBodyDataList.add(buildRemarkRowDataList(report.getRemark(), EmcConstant.CHECK_UNORGANIZED_GAS_RESULT_TABLE_HEADER_COLUMN_QTY));

        // 合并备注列
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElse(new ArrayList<>());
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(mergedRowList.get(mergedRowList.size() - 1).getEndRowIndex() + 1, 1, EmcConstant.CHECK_UNORGANIZED_GAS_RESULT_TABLE_HEADER_COLUMN_QTY - 1));

        dynamicTableDTO.setMergedRowList(mergedRowList);
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        return tableBodyDataList;
    }

    @Override
    public WordTemplateDynamicTableDTO buildTable() {
        CheckMeteorologyParameterDynamicTable meteorologyParameterTable = new CheckMeteorologyParameterDynamicTable(document, reportResultList, propertyDataList, coreFileList, isLast);
        meteorologyParameterTable.create();

        return DynamicTable.super.buildTable();
    }

	public List<EmcItemGroupBean> getItemGroupList() {
		return itemGroupList;
	}

	public void setItemGroupList(List<EmcItemGroupBean> itemGroupList) {
		this.itemGroupList = itemGroupList;
	}

	public List<EmcGroupItemBean> getGroupItemList() {
		return groupItemList;
	}

	public void setGroupItemList(List<EmcGroupItemBean> groupItemList) {
		this.groupItemList = groupItemList;
	}
}
