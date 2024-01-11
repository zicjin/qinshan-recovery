package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 场调土壤样品状态检测结果表（场调土壤报告）
 * @author zhengxy@sunwayworld.com 2020年09月03日
 */
public class FactorySoilOrderStatusDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private List<EmcFolderBean> folderList;
    private List<EmcReportResultBean> reportResultList;
    private List<EmcPropertyDataBean> propertyDataList;
    private boolean isLast;

    public FactorySoilOrderStatusDynamicTable(XWPFDocument document, List<EmcFolderBean> folderList, List<EmcReportResultBean> reportResultList, List<EmcPropertyDataBean> propertyDataList, boolean isLast) {
        this.document = document;
        this.folderList = folderList;
        this.reportResultList = reportResultList;
        this.propertyDataList = propertyDataList;
        this.isLast = isLast;
    }

    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }

    @Override
    public String getTableTitle() {
        return EmcConstant.ENTRUSTED_SOIL_ORDER_STATUS_RESULT_TABLE_TITLE;
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
        List<List<Object>> headerDataList = buildTableDynamicHeader(EmcConstant.ENTRUSTED_SOIL_ORDER_STATUS_RESULT_TABLE_HEADER_TITLES, EmcConstant.ENTRUSTED_SOIL_ORDER_STATUS_RESULT_TABLE_HEADER_COLUMN_QTY, null);
        setTableTitle(EmcConstant.ENTRUSTED_SOIL_ORDER_STATUS_RESULT_TABLE_HEADER_TITLES, EmcConstant.ENTRUSTED_SOIL_ORDER_STATUS_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        // 过滤出样品值结果
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)).sorted(Comparator.comparing(EmcReportResultBean::getFolderId)).collect(Collectors.toList());

        // 对报出结果按点位进行分组
        Map<Long, List<EmcReportResultBean>> folderReportResultMap = sampleReportResultList.stream().collect(Collectors.groupingBy(EmcReportResultBean::getFolderId, LinkedHashMap::new, Collectors.toList()));
        Map<Long, String> folderIdNameMap = folderList.stream().collect(Collectors.toMap(EmcFolderBean::getId, EmcFolderBean::getMonitorPointName));

        // 合并行配置
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);
        AtomicInteger folderMergedRowIndex = new AtomicInteger(0);

        folderReportResultMap.forEach((folderId, folderResultList) -> {
            List<Long> sampleIdList = folderResultList.stream().map(EmcReportResultBean::getSampleId).distinct().sorted(Comparator.comparing(Function.identity())).collect(Collectors.toList());
            sampleIdList.forEach(sampleId -> {
                EmcPropertyDataBean propertyData = propertyDataList.stream().filter(d -> ObjectUtils.equals(d.getBizId(), sampleId)).findFirst().orElseGet(EmcPropertyDataBean::new);
                List<Object> rowDataList = new ArrayList<>();
                rowDataList.add(folderIdNameMap.get(folderId));
                // 依次对应采样深度-ext1, 颜色-ext5, 质地-ext6, 湿度-ext7, 植物根系-ext8, 砂砾含量-ext9, 其他异物-ext10
                rowDataList.add(propertyData.getExt1());
                rowDataList.add(propertyData.getExt5());
                rowDataList.add(propertyData.getExt6());
                rowDataList.add(propertyData.getExt7());
                rowDataList.add(propertyData.getExt8());
                rowDataList.add(propertyData.getExt9());
                rowDataList.add(propertyData.getExt10());
                tableBodyDataList.add(rowDataList);
            });

            mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, folderMergedRowIndex.get() + 1, folderMergedRowIndex.addAndGet(sampleIdList.size())));
        });
        dynamicTableDTO.setMergedRowList(mergedRowList);

        return tableBodyDataList;
    }
}
