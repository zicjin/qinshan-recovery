package com.sunwayworld.emc.module.bp.report.report.template.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateSourceDTO;
import com.sunwayworld.emc.module.bp.report.report.template.BaseWordTemplate;
import com.sunwayworld.emc.module.bp.report.report.template.table.FactorySoilDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.FactorySoilOrderStatusDynamicTable;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;

/**
 * 场调土壤报告
 * @author zhengxy@sunwayworld.com 2020年09月03日
 */
public class FactorySoilWordTemplate extends BaseWordTemplate {

    private XWPFDocument document;
    private WordTemplateSourceDTO templateSourceDTO;

    public FactorySoilWordTemplate(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO) {
        super(document, templateSourceDTO);
        this.document = document;
        this.templateSourceDTO = templateSourceDTO;
    }

    @Override
    public void createResultTable() {
        // 根据监测模板ID查询监测模板，通过监测模板组ID来区分水、气、土、声
        List<EmcMonitorTmplBean> monitorTmplList = templateSourceDTO.getMonitorTmplList();
        // 根据监测模板组ID对监测模板进行分组
        Map<Long, List<EmcMonitorTmplBean>> monitorTmplMap = monitorTmplList.stream().collect(Collectors.groupingBy(EmcMonitorTmplBean::getMonitorTmplGroupId));

        Optional.ofNullable(monitorTmplMap.get(EmcConstant.SOIL_MONITOR_TMPL_GROUP_ID)).ifPresent(l -> {
            List<String> soilTmplIdList = l.stream().map(EmcMonitorTmplBean::getId).collect(Collectors.toList());
            List<EmcFolderBean> soilFolderList = templateSourceDTO.getFolderList().stream().filter(f -> soilTmplIdList.contains(f.getMonitorTmplId())).collect(Collectors.toList());

            // 对于土壤委托的
            // 通过采样单来查询采样单属性
            List<Long> sampleIdList = templateSourceDTO.getReportResultList().stream().map(EmcReportResultBean::getSampleId).distinct().collect(Collectors.toList());
            List<EmcPropertyDataBean> propertyDataList = templateSourceDTO.getPropertyDataList();

            sampleIdList.sort(Comparator.comparing(Function.identity()));
            List<List<Long>> subSampleIdList = new ArrayList<>(ListUtils.partition(sampleIdList, 3));
            // 反转列表
            Collections.reverse(subSampleIdList);

            // 创建样品状态附表
            FactorySoilOrderStatusDynamicTable orderStatusDynamicTable = new FactorySoilOrderStatusDynamicTable(document, templateSourceDTO.getFolderList(), templateSourceDTO.getReportResultList(), propertyDataList, true);
            orderStatusDynamicTable.create();

            subSampleIdList.forEach(partSampleIdList -> {
                List<EmcReportResultBean> sampleReportResultList = templateSourceDTO.getReportResultList().stream().filter(r -> partSampleIdList.contains(r.getSampleId())).collect(Collectors.toList());
                List<Long> sampleFolderIdList = sampleReportResultList.stream().map(EmcReportResultBean::getFolderId).distinct().collect(Collectors.toList());
                List<EmcFolderBean> sampleFolderList = soilFolderList.stream().filter(f -> sampleFolderIdList.contains(f.getId())).collect(Collectors.toList());

                Map<Long, Long> sampleIdFolderIdMap = sampleReportResultList.stream().collect(Collectors.toMap(EmcReportResultBean::getSampleId, EmcReportResultBean::getFolderId, (key1, key2) -> key2));
                FactorySoilDynamicTable soilDynamicTable = new FactorySoilDynamicTable(document, templateSourceDTO.getReport(), sampleFolderList, sampleIdFolderIdMap, sampleReportResultList, propertyDataList, false);
                soilDynamicTable.create();
            });
        });
    }
}
