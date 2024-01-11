package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.framework.utils.DateTimeUtils;

/**
 * 加油站检测设备表（油气回收报告）
 * @author zhengxy@sunwayworld.com 2020年09月03日
 */
public class GasStationEquipmentDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private List<EmcOrderBean> orderList;
    private List<EmcOrderTaskBean> orderTaskList;
    private List<EmcEquiptBean> equiptList;
    private EmcPropertyDataBean propertyData;
    private boolean isLast;

    public GasStationEquipmentDynamicTable(XWPFDocument document, List<EmcOrderBean> orderList, List<EmcOrderTaskBean> orderTaskList, List<EmcEquiptBean> equiptList, EmcPropertyDataBean propertyData, boolean isLast) {
        this.document = document;
        this.orderList = orderList;
        this.orderTaskList = orderTaskList;
        this.equiptList = equiptList;
        this.propertyData = propertyData;
        this.isLast = isLast;
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
        return this.isLast;
    }

    @Override
    public int getDynamicTableIndex() {
        return 5;
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<Long> equipmentIdList = orderTaskList.stream().map(EmcOrderTaskBean::getEquiptId).distinct().collect(Collectors.toList());
        EmcEquiptBean equipment = equiptList.stream().filter(e -> equipmentIdList.contains(e.getId())).findFirst().orElseGet(EmcEquiptBean::new);
        LocalDate sampleBeginDate = orderList.get(0).getSampleBeginDate();

        List<String> dynamicTitleList = new ArrayList<>();
        // 检测设备型号及名称
        dynamicTitleList.add(equipment.getSpecification() + "\n" + equipment.getEquiptName());
        // 仪器编号
        dynamicTitleList.add(equipment.getEquiptId());
        // 检测时间
        dynamicTitleList.add(sampleBeginDate == null ? null : DateTimeUtils.formatLocalDate(sampleBeginDate, EmcConstant.DEFAULT_DATE_FORMAT));
        // 大气压, EXT14
        dynamicTitleList.add(propertyData.getExt14());
        // 环境温度, EXT12
        dynamicTitleList.add(propertyData.getExt12());
        // 环境湿度, EXT13
        dynamicTitleList.add(propertyData.getExt13());
        // 起始压力, EXT8
        dynamicTitleList.add(propertyData.getExt8());
        // 结束压力, EXT10
        dynamicTitleList.add(propertyData.getExt10());
        // 压力差, EXT11
        dynamicTitleList.add(propertyData.getExt11());

        // 结论
        dynamicTitleList.add(EmcConstant.DEFAULT_CONCLUSION);

        // 合并列配置
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(1, 0, 3));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(6, 1, 3));
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        List<List<Object>> headerDataList = buildTableDynamicHeader(EmcConstant.EQUIPMENT_RESULT_TABLE_HEADER_TITLES, EmcConstant.EQUIPMENT_RESULT_TABLE_HEADER_COLUMN_QTY, dynamicTitleList);
        setTableTitle(EmcConstant.EQUIPMENT_RESULT_TABLE_HEADER_TITLES, EmcConstant.EQUIPMENT_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

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
        dynamicTableDTO.setReplaceKeyword(EmcConstant.EQUIPMENT_TABLE_PLACEHOLDER);

        return dynamicTableDTO;
    }
}
