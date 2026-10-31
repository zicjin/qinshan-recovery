package com.sunwayworld.emc.module.bp.report.report.template.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateSourceDTO;
import com.sunwayworld.emc.module.bp.report.report.template.BaseWordTemplate;
import com.sunwayworld.emc.module.bp.report.report.template.table.FactoryGroundwaterDynamicTable;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;

/**
 * 场调地下水报告
 * @author zhengxy@sunwayworld.com 2020年09月03日
 */
public class FactoryGroundwaterWordTemplate extends BaseWordTemplate {

    private XWPFDocument document;
    private WordTemplateSourceDTO templateSourceDTO;

    public FactoryGroundwaterWordTemplate(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO) {
        super(document, templateSourceDTO);
        this.document = document;
        this.templateSourceDTO = templateSourceDTO;
    }

    @Override
    public void createResultTable() {
        // 根据监测模板ID查询监测模板，通过监测模板组ID来区分水
        List<EmcMonitorTmplBean> monitorTmplList = templateSourceDTO.getMonitorTmplList();
        // 根据监测模板组ID对监测模板进行分组
        Map<Long, List<EmcMonitorTmplBean>> monitorTmplMap = monitorTmplList.stream().collect(Collectors.groupingBy(EmcMonitorTmplBean::getMonitorTmplGroupId));

        Optional.ofNullable(monitorTmplMap.get(EmcConstant.WATER_MONITOR_TMPL_GROUP_ID)).ifPresent(l -> {
            List<String> waterTmplIdList = l.stream().map(EmcMonitorTmplBean::getId).collect(Collectors.toList());
            List<EmcFolderBean> waterFolderList = templateSourceDTO.getFolderList().stream().filter(f -> waterTmplIdList.contains(f.getMonitorTmplId())).sorted(Comparator.comparing(EmcFolderBean::getId)).collect(Collectors.toList());

            // 对地下水点位按3个进行划分
            List<List<EmcFolderBean>> subFolderList = new ArrayList<>(ListUtils.partition(waterFolderList, 3));
            Collections.reverse(subFolderList);

            subFolderList.forEach(partFolderList -> {
                List<Long> partFolderIdList = partFolderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
                List<EmcReportResultBean> folderReportResultList = templateSourceDTO.getReportResultList().stream().filter(r -> partFolderIdList.contains(r.getFolderId())).collect(Collectors.toList());
                FactoryGroundwaterDynamicTable groundwaterDynamicTable = new FactoryGroundwaterDynamicTable(document, templateSourceDTO.getReport(), partFolderList, templateSourceDTO.getOrderList(), folderReportResultList, false);
                groundwaterDynamicTable.create();
            });
        });
    }
}
