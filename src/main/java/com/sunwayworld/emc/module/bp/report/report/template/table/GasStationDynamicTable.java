package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;

/**
 * 加油站相关情况表（油气回收报告）
 * @author zhengxy@sunwayworld.com 2020年09月03日
 */
public class GasStationDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private EmcProjectBean project;
    private EmcPropertyDataBean propertyData;

    public GasStationDynamicTable(XWPFDocument document, EmcProjectBean project, EmcPropertyDataBean propertyData) {
        this.document = document;
        this.project = project;
        this.propertyData = propertyData;
    }

    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }

    @Override
    public String getTableTitle() {
        return null;
    }

    @Override
    public int getTableIndex() {
        return 0;
    }

    @Override
    public boolean isLast() {
        return true;
    }

    @Override
    public int getDynamicTableIndex() {
        return 4;
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<String> dynamicTitleList = new ArrayList<>();
        // 加油站名称, 取项目受检单位名称
        dynamicTitleList.add(project.getTestOrgName());
        // 汽油罐数个数，EXT4
        dynamicTitleList.add(propertyData.getExt4());
        // 油气管线是否连通, EXT1
        dynamicTitleList.add(propertyData.getExt1());
        // 油罐体积, EXT5
        dynamicTitleList.add(propertyData.getExt5());
        // 油气空间, EXT15
        dynamicTitleList.add(propertyData.getExt15());
        // 加油机数, EXT6
        dynamicTitleList.add(propertyData.getExt6());
        // 加油枪数, EXT7
        dynamicTitleList.add(propertyData.getExt7());

        // 备注
        dynamicTitleList.add("/");

        // 合并列配置
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 1, 3));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(4, 1, 3));
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        List<List<Object>> headerDataList = buildTableDynamicHeader(EmcConstant.GAS_STATION_RESULT_TABLE_HEADER_TITLES, EmcConstant.GAS_STATION_RESULT_TABLE_HEADER_COLUMN_QTY, dynamicTitleList);
        setTableTitle(EmcConstant.GAS_STATION_RESULT_TABLE_HEADER_TITLES, EmcConstant.GAS_STATION_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        return null;
    }

    @Override
    public WordTemplateDynamicTableDTO buildTable() {
        WordTemplateDynamicTableDTO dynamicTableDTO = new WordTemplateDynamicTableDTO();

        List<List<Object>> tableDataList = buildTableHeader(dynamicTableDTO);
        WordTemplateUtils.listListNullToString(tableDataList, EmcConstant.NULL_REPLACED_VALUE);

        dynamicTableDTO.setTableDataList(tableDataList);
        dynamicTableDTO.setReplaceKeyword(EmcConstant.GAS_STATION_TABLE_PLACEHOLDER);

        return dynamicTableDTO;
    }
}
