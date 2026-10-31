package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 有组织废气参数（二噁英）
 * @author zhengxy@sunwayworld.com 2021年07月08日
 */
public class DioxinGasParameterDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private List<EmcReportResultBean> reportResultList;
    private boolean sampling;
    private boolean last;

    public DioxinGasParameterDynamicTable(XWPFDocument document, List<EmcReportResultBean> reportResultList, List<EmcPropertyDataBean> propertyDataList, boolean sampling, boolean last) {
        this.document = document;
        this.reportResultList = reportResultList;
        this.sampling = sampling;
        this.last = last;
    }

    @Override
    public XWPFDocument getDocument() {
        return document;
    }

    @Override
    public String getTableTitle() {
        return PfConstant.DIOXIN_GAS_PARAMETER_RESULT_TABLE_TITLE;
    }

    @Override
    public int getTableIndex() {
        return 0;
    }

    @Override
    public boolean isLast() {
        return this.last;
    }

    @Override
    public int getDynamicTableIndex() {
        return 2;
    }

    @Override
    public List<Integer> getColumnWidthList() {
        return Arrays.asList(0, 1945, 1049, 947, 947, 783, 947, 1020, 1066);
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        String[] tableTitles = sampling ? PfConstant.DIOXIN_GAS_PARAMETER_RESULT_TABLE_HEADER_TITLES : PfConstant.DIOXIN_GAS_PARAMETER_RESULT_TABLE_HEADER_TITLES_2;
        List<List<Object>> headerDataList = buildTableDynamicHeader(tableTitles, PfConstant.DIOXIN_GAS_PARAMETER_RESULT_TABLE_HEADER_COLUMN_QTY, null);
        setTableTitle(tableTitles, PfConstant.DIOXIN_GAS_PARAMETER_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();
        // 过滤出样品值结果
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)).collect(Collectors.toList());

        Map<Long, List<EmcReportResultBean>> folderResultMap = sampleReportResultList.stream().sorted(Comparator.comparing(EmcReportResultBean::getFolderId)).collect(Collectors.groupingBy(EmcReportResultBean::getFolderId, LinkedHashMap::new, Collectors.toList()));
        AtomicInteger mergedRowIndex = new AtomicInteger(0);
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);

        folderResultMap.forEach((folderId, resultList) -> {
            resultList.sort(Comparator.comparing(EmcReportResultBean::getOrderNo));
            Map<String, List<EmcReportResultBean>> orderResultMap = resultList.stream().collect(Collectors.groupingBy(EmcReportResultBean::getOrderNo, LinkedHashMap::new, Collectors.toList()));
            orderResultMap.forEach((orderNo, orderResultList) -> {
                List<Object> rowDataList = new ArrayList<>();
                rowDataList.add(orderResultList.get(0).getMonitorPointName());
                rowDataList.add(orderNo);

                // 排气筒高度、截面积、含氧量、烟温、含湿量、平均流速、标干风量
                List<EmcReportResultBean> samplingResultList = orderResultList.stream().filter(result -> EmcBizConstant.ANALYSISCATEGORY_SAMPLING.equals(result.getAnalysisType())).collect(Collectors.toList());
                Arrays.stream(PfConstant.DIOXIN_GAS_PARAMETER_RESULT_KEYWORDS).forEach(keyword -> rowDataList.add(getSpecifiedItemReportResult(samplingResultList, keyword)));

                tableBodyDataList.add(rowDataList);
            });

            int startMergedRowIndex = mergedRowIndex.get() + 1;
            int endMergedRowIndex = mergedRowIndex.addAndGet(orderResultMap.size());
            mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, startMergedRowIndex, endMergedRowIndex));
        });

        dynamicTableDTO.setMergedRowList(mergedRowList);

        return tableBodyDataList;
    }

    private String getSpecifiedItemReportResult(List<EmcReportResultBean> reportResultList, String itemName) {
        EmcReportResultBean reportResult = reportResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), itemName)).findFirst().orElseGet(EmcReportResultBean::new);

        return reportResult.getReportResult();
    }

}
