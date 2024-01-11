package com.sunwayworld.emc.module.bp.report.report.template.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateSourceDTO;
import com.sunwayworld.emc.module.bp.report.report.template.BaseWordTemplate;
import com.sunwayworld.emc.module.bp.report.report.template.table.LampblackDynamicTable;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 油烟报告
 * @author zhengxy@sunwayworld.com 2020年09月03日
 */
public class LampblackWordTemplate extends BaseWordTemplate {

    private XWPFDocument document;
    private WordTemplateSourceDTO templateSourceDTO;

    public LampblackWordTemplate(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO) {
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

        // 气对应的监测模板
        Optional.ofNullable(monitorTmplMap.get(EmcConstant.GAS_MONITOR_TMPL_GROUP_ID)).ifPresent(l -> {
            // 油烟
            List<EmcMonitorTmplBean> lampblackTmplList = l.stream().filter(t -> StringUtils.contains(t.getMonitorTmplName(), EmcConstant.LAMPBLACK_ORDER_CATEGORY_KEYWORD)).collect(Collectors.toList());
            if (!lampblackTmplList.isEmpty()) {
                Map<String, List<EmcFolderBean>> folderListMap = groupingFolderByOrderCategory(lampblackTmplList, templateSourceDTO.getFolderList());

                folderListMap.forEach((orderCategory, currentFolderList) -> {
                    List<Long> folderIdList = currentFolderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
                    List<EmcReportResultBean> folderReportResultList = templateSourceDTO.getReportResultList().stream().filter(r -> folderIdList.contains(r.getFolderId())).collect(Collectors.toList());
                    // 再对检测项目按天进行分组，每天生成一个检测结果表
                    Map<String, List<EmcReportResultBean>> dayOfReportResultMap = folderReportResultList.stream().collect(Collectors.groupingBy(r -> DateTimeUtils.formatLocalDate(r.getSampleBeginDate()), LinkedHashMap::new, Collectors.toList()));

                    List<EmcOrderTaskBean> folderOrderTaskList = templateSourceDTO.getOrderTaskList().stream().filter(r -> folderIdList.contains(r.getFolderId())).collect(Collectors.toList());
                    List<EmcOrderTaskResultBean> folderTaskResultList = templateSourceDTO.getOrderTaskResultList().stream().filter(r -> folderIdList.contains(r.getFolderId())).collect(Collectors.toList());

                    // 这种写法主要用于判断是否为最后一个表格，最后一个表格不需要结束标记
                    List<List<EmcReportResultBean>> dayOfReportResultMapValues = new ArrayList<>(dayOfReportResultMap.values());
                    IntStream.range(0, dayOfReportResultMapValues.size()).forEach(i -> {
                        List<EmcReportResultBean> dayOfReportResultList = dayOfReportResultMapValues.get(i);
                        LampblackDynamicTable lampblackDynamicTable = new LampblackDynamicTable(document, templateSourceDTO.getReport(), currentFolderList, dayOfReportResultList, templateSourceDTO.getRunList(), folderOrderTaskList, folderTaskResultList, (i == 0));
                        lampblackDynamicTable.create();
                    });
                });
            }
        });
    }
}
