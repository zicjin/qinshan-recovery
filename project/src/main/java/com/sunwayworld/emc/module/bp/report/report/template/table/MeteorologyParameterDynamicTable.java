package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;

/**
 * 气象参数检测结果-无组织废气（综合报告）
 * @author zhengxy@sunwayworld.com 2020年09月03日
 */
public class MeteorologyParameterDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private List<EmcReportResultBean> reportResultList;
    private List<EmcPropertyDataBean> propertyDataList;
    private List<CoreFileBean> coreFileList;
    private boolean isLast;

    public MeteorologyParameterDynamicTable(XWPFDocument document, List<EmcReportResultBean> reportResultList, List<EmcPropertyDataBean> propertyDataList, List<CoreFileBean> coreFileList, boolean isLast) {
        this.document = document;
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
        return EmcConstant.METEOROLOGY_PARAMETER_RESULT_TABLE_TITLE;
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
        List<List<Object>> headerDataList = buildTableDynamicHeader(EmcConstant.METEOROLOGY_PARAMETER_RESULT_TABLE_HEADER_TITLES, EmcConstant.METEOROLOGY_PARAMETER_RESULT_TABLE_HEADER_COLUMN_QTY, null);
        setTableTitle(EmcConstant.METEOROLOGY_PARAMETER_RESULT_TABLE_HEADER_TITLES, EmcConstant.METEOROLOGY_PARAMETER_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();
        // 获取需要合并行的信息列表
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElse(new ArrayList<>());

        // 过滤出样品值结果
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)).collect(Collectors.toList());

        List<Object> rowDataList = new ArrayList<>();
        rowDataList.add(DateTimeUtils.formatLocalDate(sampleReportResultList.get(0).getSampleBeginDate()));
        // 天气情况保存在ext3字段
        EmcPropertyDataBean propertyData = propertyDataList.stream().filter(d -> ObjectUtils.equals(d.getBizId(), sampleReportResultList.get(0).getSampleId())).findFirst().orElseGet(EmcPropertyDataBean::new);
        rowDataList.add(propertyData.getExt3());

        // 风向
        rowDataList.add(getSpecifiedItemReportResult(sampleReportResultList, EmcConstant.METEOROLOGY_PARAMETER_SEARCH_TITLES[0]));
        // 风速
        rowDataList.add(getSpecifiedItemReportResult(sampleReportResultList, EmcConstant.METEOROLOGY_PARAMETER_SEARCH_TITLES[1]));
        // 相对湿度
        rowDataList.add(getSpecifiedItemReportResult(sampleReportResultList, EmcConstant.METEOROLOGY_PARAMETER_SEARCH_TITLES[2]));
        // 温度
        rowDataList.add(getSpecifiedItemReportResult(sampleReportResultList, EmcConstant.METEOROLOGY_PARAMETER_SEARCH_TITLES[3]));
        // 大气压力
        rowDataList.add(getSpecifiedItemReportResult(sampleReportResultList, EmcConstant.METEOROLOGY_PARAMETER_SEARCH_TITLES[4]));

        tableBodyDataList.add(rowDataList);

        // 添加无组织废气检测点位布置示意图行
        tableBodyDataList.add(buildDynamicRowDataList(EmcConstant.UNORGANIZED_GAS_POINT_DIAGRAM_TITLE, "", EmcConstant.METEOROLOGY_PARAMETER_RESULT_TABLE_HEADER_COLUMN_QTY));

        // 合并点位示意图行
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElse(new ArrayList<>());
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(2, 1, 6));

        dynamicTableDTO.setMergedRowList(mergedRowList);
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        // 点位示意图
        List<WordTemplateDynamicTableDTO.TableImage> tableImageList = Optional.ofNullable(dynamicTableDTO.getTableImageList()).orElseGet(ArrayList::new);
        coreFileList.stream().filter(f -> ObjectUtils.equals(f.getTargetId(), "T_EMC_SAMPLE$" + sampleReportResultList.get(0).getSampleId())).findFirst().ifPresent(f -> {
            String pointImageUrl = FilePathManager.getLocalPath(FilePathDTO.of(f.getScope(), DateTimeUtils.getDateSerialNumber(f.getCreatedTime()), f.getMd5Name(), f.getName())).toString();
            tableImageList.add(new WordTemplateDynamicTableDTO.TableImage(2, 1, pointImageUrl));
        });
        dynamicTableDTO.setTableImageList(tableImageList);

        return tableBodyDataList;
    }

    private String getSpecifiedItemReportResult(List<EmcReportResultBean> reportResultList, String itemName) {
        EmcReportResultBean reportResult = reportResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), itemName)).findFirst().orElseGet(EmcReportResultBean::new);

        return reportResult.getReportResult();
    }
}
