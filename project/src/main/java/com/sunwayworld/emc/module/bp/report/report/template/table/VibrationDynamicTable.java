package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;

/**
 * 振动检测结果
 * @author zhengxy@sunwayworld.com 2020年09月03日
 */
public class VibrationDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private EmcReportBean report;
    private List<EmcFolderBean> folderList;
    private List<CoreFileBean> coreFileList;
    private List<EmcReportResultBean> reportResultList;
    private boolean isLast;

    public VibrationDynamicTable(XWPFDocument document, EmcReportBean report, List<EmcFolderBean> folderList, List<CoreFileBean> coreFileList, List<EmcReportResultBean> reportResultList, boolean isLast) {
        this.document = document;
        this.report = report;
        this.folderList = folderList;
        this.coreFileList = coreFileList;
        this.reportResultList = reportResultList;
        this.isLast = isLast;
    }

    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }

    @Override
    public String getTableTitle() {
        return EmcConstant.VIBRATION_RESULT_TABLE_TITLE;
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
        dynamicTitleList.add(reportResultList.get(0).getTestName());
        dynamicTitleList.add(WordTemplateUtils.getDate(reportResultList.stream().map(EmcReportResultBean::getSampleBeginDate).collect(Collectors.toList())));

        // 合并列配置
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElse(new ArrayList<>());
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(1, 1, 3));
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        // 合并行
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, 1, 2));
        dynamicTableDTO.setMergedRowList(mergedRowList);

        List<List<Object>> headerDataList = buildTableDynamicHeader(EmcConstant.VIBRATION_RESULT_TABLE_HEADER_TITLES, EmcConstant.VIBRATION_RESULT_TABLE_HEADER_COLUMN_QTY, dynamicTitleList);
        setTableTitle(EmcConstant.VIBRATION_RESULT_TABLE_HEADER_TITLES, EmcConstant.VIBRATION_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        Map<Long, String> folderIdNameMap = folderList.stream().collect(Collectors.toMap(EmcFolderBean::getId, EmcFolderBean::getMonitorPointName));

        // 过滤出总均值
        List<EmcReportResultBean> totalReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_TOTALAVERAGE)).collect(Collectors.toList());

        // 按点位对报出结果进行分组，每个点位一行数据，如果点位下有多个样品，则取均值
        Map<Long, List<EmcReportResultBean>> folderResultMap = totalReportResultList.stream().collect(Collectors.groupingBy(EmcReportResultBean::getFolderId));
        folderResultMap.forEach((folderId, folderResultList) -> {
            List<Object> rowDataList = new ArrayList<>();
            rowDataList.add(folderIdNameMap.get(folderId));
            rowDataList.add(folderResultList.get(0).getSampleBeginTime());
            rowDataList.add(folderResultList.get(0).getUnitName());
            rowDataList.add(folderResultList.get(0).getReportResult());
            tableBodyDataList.add(rowDataList);
        });

        // 添加振动检测点位布置示意图
        tableBodyDataList.add(buildDynamicRowDataList(EmcConstant.VIBRATION_POINT_DIAGRAM_TITLE, "", EmcConstant.VIBRATION_RESULT_TABLE_HEADER_COLUMN_QTY));

        // 添加备注行
        tableBodyDataList.add(buildRemarkRowDataList(report.getRemark(), EmcConstant.VIBRATION_RESULT_TABLE_HEADER_COLUMN_QTY));

        // 合并备注列和点位示意图列
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElse(new ArrayList<>());
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 1, 1, 3));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 2, 1, 3));

        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        // 点位示意图
        List<WordTemplateDynamicTableDTO.TableImage> tableImageList = Optional.ofNullable(dynamicTableDTO.getTableImageList()).orElseGet(ArrayList::new);
        coreFileList.stream().filter(f -> ObjectUtils.equals(f.getTargetId(), "T_EMC_SAMPLE$" + reportResultList.get(0).getSampleId())).findFirst().ifPresent(f -> {
            String pointImageUrl = FilePathManager.getLocalPath(FilePathDTO.of(f.getScope(), DateTimeUtils.getDateSerialNumber(f.getCreatedTime()), f.getMd5Name(), f.getName())).toString();
            tableImageList.add(new WordTemplateDynamicTableDTO.TableImage(tableBodyDataList.size() + 1, 1, pointImageUrl));
        });
        dynamicTableDTO.setTableImageList(tableImageList);

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
