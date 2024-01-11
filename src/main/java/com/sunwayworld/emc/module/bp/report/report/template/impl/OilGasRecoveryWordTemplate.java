package com.sunwayworld.emc.module.bp.report.report.template.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.word.Word2007Utils;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateSourceDTO;
import com.sunwayworld.emc.module.bp.report.report.template.BaseWordTemplate;
import com.sunwayworld.emc.module.bp.report.report.template.table.GasStationDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.GasStationEquipmentDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.GasStationHermeticDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.GasStationLiquidRatioDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.GasStationLiquidResistanceDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.GasStationMonitorItemDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.GasStationOilDensityDynamicTable;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 油气回收报告
 * @author zhengxy@sunwayworld.com 2020年09月03日
 */
public class OilGasRecoveryWordTemplate extends BaseWordTemplate {

    private XWPFDocument document;
    private WordTemplateSourceDTO templateSourceDTO;

    public OilGasRecoveryWordTemplate(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO) {
        super(document, templateSourceDTO);
        this.document = document;
        this.templateSourceDTO = templateSourceDTO;
    }

    @Override
    public void createResultTable() {
        // 加油站相关情况表
        EmcPropertyDataBean propertyData = new EmcPropertyDataBean();
        if (!templateSourceDTO.getPropertyDataList().isEmpty()) {
            propertyData = templateSourceDTO.getPropertyDataList().get(0);
        }
        GasStationDynamicTable gasStationDynamicTable = new GasStationDynamicTable(document, templateSourceDTO.getProject(), propertyData);
        gasStationDynamicTable.create();

        // 检测设备相关情况
        GasStationEquipmentDynamicTable equipmentDynamicTable = new GasStationEquipmentDynamicTable(document, templateSourceDTO.getOrderList(),
                templateSourceDTO.getOrderTaskList(), templateSourceDTO.getEquiptList(), propertyData, true);
        equipmentDynamicTable.create();

        List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
        // 过滤出样品值结果
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)).collect(Collectors.toList());

        // 油气排放浓度检测结果(需要样品值和总均值)
        List<EmcReportResultBean> oilGasDensityResultList = reportResultList.stream().filter(result -> StringUtils.contains(result.getTestName(), EmcConstant.OIL_GAS_DENSITY_ITEM_KEYWORD)).collect(Collectors.toList());
        GasStationOilDensityDynamicTable densityDynamicTable = new GasStationOilDensityDynamicTable(document, templateSourceDTO.getFolderList(), oilGasDensityResultList, true);
        densityDynamicTable.create();

        // 气液比检测结果
        List<EmcReportResultBean> gasLiquidRatioResultList = sampleReportResultList.stream().filter(result -> StringUtils.contains(result.getTestName(), EmcConstant.GAS_LIQUID_RATIO_ITEM_KEYWORD)).collect(Collectors.toList());
        GasStationLiquidRatioDynamicTable ratioDynamicTable = new GasStationLiquidRatioDynamicTable(document, gasLiquidRatioResultList, true);
        ratioDynamicTable.create();

        // 液阻检测结果
        List<EmcReportResultBean> liquidResistanceResultList = sampleReportResultList.stream().filter(result -> StringUtils.contains(result.getTestName(), EmcConstant.LIQUID_RESISTANCE_ITEM_KEYWORD)).collect(Collectors.toList());
        GasStationLiquidResistanceDynamicTable resistanceDynamicTable = new GasStationLiquidResistanceDynamicTable(document, liquidResistanceResultList, true);
        resistanceDynamicTable.create();

        // 密闭性检测结果
        List<EmcReportResultBean> hermeticReportResultList = sampleReportResultList.stream().filter(result -> StringUtils.contains(result.getTestName(), EmcConstant.HERMETIC_ITEM_KEYWORD)).collect(Collectors.toList());
        GasStationHermeticDynamicTable hermeticDynamicTable = new GasStationHermeticDynamicTable(document, hermeticReportResultList, true);
        hermeticDynamicTable.create();
    }

    @Override
    public void createMonitorItemTable() {
        GasStationMonitorItemDynamicTable monitorItemDynamicTable = new GasStationMonitorItemDynamicTable(document, templateSourceDTO.getOrderTaskList(), templateSourceDTO.getEquiptList());
        monitorItemDynamicTable.create();
    }

    @Override
    public void clean() {
        super.clean();
        Word2007Utils.removeKey(this.getDocument(), EmcConstant.GAS_STATION_TABLE_PLACEHOLDER);
        Word2007Utils.removeKey(this.getDocument(), EmcConstant.EQUIPMENT_TABLE_PLACEHOLDER);
    }
}
