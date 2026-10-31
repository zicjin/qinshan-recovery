package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhengxy@sunwayworld.com 2021年09月13日
 */
public class GasParameterDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private List<EmcFolderBean> folderList;
    private List<EmcOrderBean> orderList;
    private List<EmcReportResultBean> reportResultList;
    private List<EmcOrderTaskResultBean> orderTaskResultList;
    private LocalDate sampleBeginDate;

    private boolean isLast;

    public GasParameterDynamicTable(XWPFDocument document, List<EmcFolderBean> folderList, List<EmcOrderBean> orderList, List<EmcReportResultBean> reportResultList, List<EmcOrderTaskResultBean> orderTaskResultList, boolean isLast) {
        this.document = document;
        this.folderList = folderList;
        this.orderList = orderList;
        this.reportResultList = reportResultList;
        this.orderTaskResultList = orderTaskResultList;

        this.isLast = isLast;
    }

    @Override
    public XWPFDocument getDocument() {
        return document;
    }

    @Override
    public String getTableTitle() {
        return String.format(PfConstant.GAS_PARAMETER_RESULT_TABLE_TITLE);

    }

    @Override
    public int getTableIndex() {
        return 0;
    }

    @Override
    public boolean isLast() {
        return isLast;
    }

    @Override
    public int getDynamicTableIndex() {
        return 2;
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> headerDataList = buildTableDynamicHeader(PfConstant.GAS_PARAMETER_RESULT_TABLE_HEADER_TITLES, PfConstant.GAS_PARAMETER_RESULT_TABLE_HEADER_COLUMN_QTY, null);
        setTableTitle(PfConstant.GAS_PARAMETER_RESULT_TABLE_HEADER_TITLES, PfConstant.GAS_PARAMETER_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        // 过滤出样品值结果
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)).sorted(Comparator.comparing(EmcReportResultBean::getOrderId)).collect(Collectors.toList());

        List<Long> validOrderIdList = orderList.stream().filter(order -> !StringUtils.contains(order.getQcName(), PfConstant.QC_BLANK_KEYWORD)).map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcReportResultBean> validReportResultList = sampleReportResultList.stream().filter(r -> validOrderIdList.contains(r.getOrderId())).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> validOrderTaskResultList = orderTaskResultList.stream().filter(r -> validOrderIdList.contains(r.getOrderId())).collect(Collectors.toList());

        Map<Long, String> folderIdNameMap = folderList.stream().filter(folder -> !StringUtils.isEmpty(folder.getMonitorPointName())).collect(Collectors.toMap(EmcFolderBean::getId, EmcFolderBean::getMonitorPointName));
        Map<Long, List<EmcReportResultBean>> folderResultMap = validReportResultList.stream().collect(Collectors.groupingBy(EmcReportResultBean::getFolderId, LinkedHashMap::new, Collectors.toList()));
        Map<Long, List<EmcOrderTaskResultBean>> folderOrderTaskResultMap = validOrderTaskResultList.stream().collect(Collectors.groupingBy(EmcOrderTaskResultBean::getFolderId, LinkedHashMap::new, Collectors.toList()));

        AtomicInteger mergedRowIndex = new AtomicInteger();
        // 获取需要合并行的信息列表
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElse(new ArrayList<>());

/*        folderResultMap.forEach((folderId, folderResultList) -> {
            folderResultList.sort(Comparator.comparing(EmcReportResultBean::getOrderNo));
            LinkedHashMap<String, List<EmcReportResultBean>> orderResultMap = folderResultList.stream().collect(Collectors.groupingBy(EmcReportResultBean::getOrderNo, LinkedHashMap::new, Collectors.toList()));
            orderResultMap.forEach((orderNo, curResultList) -> {
                List<Object> rowDataList = new ArrayList<>();
                rowDataList.add(folderIdNameMap.get(folderId));
                rowDataList.add(orderNo);
                rowDataList.add(curResultList.get(0).getTestName());
                Arrays.stream(PfConstant.GAS_PARAMETER_RESULT_KEYWORDS).forEach(keyword -> {
                    EmcReportResultBean reportResult = curResultList.stream().filter(result -> StringUtils.contains(result.getItemAliasName(), keyword)).findFirst().orElseGet(EmcReportResultBean::new);
                    rowDataList.add(reportResult.getReportResult());
                });
                tableBodyDataList.add(rowDataList);
            });

            int startRowIndex = mergedRowIndex.get() + 1;
            int endRowIndex = mergedRowIndex.addAndGet(orderResultMap.size());
            mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, startRowIndex, endRowIndex));
        });*/

        folderOrderTaskResultMap.forEach((folderId, folderOrderTaskResultList) -> {
            folderOrderTaskResultList.sort(Comparator.comparing(EmcOrderTaskResultBean::getOrderNo));
            LinkedHashMap<String, List<EmcOrderTaskResultBean>> orderResultMap = folderOrderTaskResultList.stream().collect(Collectors.groupingBy(EmcOrderTaskResultBean::getOrderNo, LinkedHashMap::new, Collectors.toList()));
            orderResultMap.forEach((orderNo, curResultList) -> {
                List<Object> rowDataList = new ArrayList<>();
                rowDataList.add(folderIdNameMap.get(folderId));
                rowDataList.add(orderNo);
                Arrays.stream(PfConstant.GAS_PARAMETER_RESULT_KEYWORDS).forEach(keyword -> {
                    if(StringUtils.equals(keyword,PfConstant.GAS_PARAMETER_RESULT_KEYWORDS[2])){
                        EmcOrderTaskResultBean orderTaskResult = curResultList.stream().filter(result -> StringUtils.equals(result.getItemAliasName(), keyword)).findFirst().orElseGet(EmcOrderTaskResultBean::new);
                        rowDataList.add(orderTaskResult.getReportResult());
                    }else{
                        EmcOrderTaskResultBean orderTaskResult = curResultList.stream().filter(result -> StringUtils.contains(result.getItemAliasName(), keyword)).findFirst().orElseGet(EmcOrderTaskResultBean::new);
                        rowDataList.add(orderTaskResult.getReportResult());
                    }
                });
                tableBodyDataList.add(rowDataList);
            });

            int startRowIndex = mergedRowIndex.get() + 1;
            int endRowIndex = mergedRowIndex.addAndGet(orderResultMap.size());
            mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, startRowIndex, endRowIndex));
        });

        dynamicTableDTO.setMergedRowList(mergedRowList);

        return tableBodyDataList;
    }
}
