package com.sunwayworld.emc.module.bp.report.report.template.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.module.bp.report.report.template.BaseWordTemplate;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateSourceDTO;
import com.sunwayworld.emc.module.bp.report.report.template.table.VibrationDynamicTable;

/**
 * 振动报告
 * @author zhengxy@sunwayworld.com 2020年09月03日
 */
public class VibrationWordTemplate extends BaseWordTemplate {

    private XWPFDocument document;
    private WordTemplateSourceDTO templateSourceDTO;

    public VibrationWordTemplate(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO) {
        super(document, templateSourceDTO);
        this.document = document;
        this.templateSourceDTO = templateSourceDTO;
    }

    @Override
    public void createResultTable() {
        // 按照振动所测的检测项目对点位进行分组
        Map<String, List<EmcReportResultBean>> testReportResultMap = templateSourceDTO.getReportResultList().stream().collect(Collectors.groupingBy(EmcReportResultBean::getTestName));

        // 每个检测项目生成独立的检测结果
        testReportResultMap.forEach((testName, testReportResultList) -> {
            List<Long> testFolderIdList = testReportResultList.stream().map(EmcReportResultBean::getFolderId).distinct().collect(Collectors.toList());
            List<EmcFolderBean> testFolderList = templateSourceDTO.getFolderList().stream().filter(folder -> testFolderIdList.contains(folder.getId())).collect(Collectors.toList());
            VibrationDynamicTable vibrationDynamicTable = new VibrationDynamicTable(document, templateSourceDTO.getReport(), testFolderList, templateSourceDTO.getCoreFileList(), testReportResultList, true);
            vibrationDynamicTable.create();
        });
    }
}
