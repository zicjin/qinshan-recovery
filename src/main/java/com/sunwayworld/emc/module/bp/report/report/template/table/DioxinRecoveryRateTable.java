package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 回收率统计
 * @author zhengxy@sunwayworld.com 2021年07月30日
 */
public class DioxinRecoveryRateTable implements DynamicTable {

    private static final Map<String, String> ITEM_CONVERTED_MAP = new HashMap<>();

    private XWPFDocument document;
    private EmcOrderBean order;
    private List<EmcReportResultBean> reportResultList;

    static {
        ITEM_CONVERTED_MAP.put("13C-2378-TCDF", "<sup>13</sup>C-2378-TCDF");
        ITEM_CONVERTED_MAP.put("13C-23478-PeCDF", "<sup>13</sup>C-23478-PeCDF");
        ITEM_CONVERTED_MAP.put("13C-123478-HxCDF", "<sup>13</sup>C-123478-HxCDF");
        ITEM_CONVERTED_MAP.put("13C-123678-HxCDF", "<sup>13</sup>C-123678-HxCDF");
        ITEM_CONVERTED_MAP.put("13C-234678-HxCDF", "<sup>13</sup>C-234678-HxCDF");
        ITEM_CONVERTED_MAP.put("13C-123789-HxCDF", "<sup>13</sup>C-123789-HxCDF");
        ITEM_CONVERTED_MAP.put("13C-1234678-HpCDF", "<sup>13</sup>C-1234678-HpCDF");
        ITEM_CONVERTED_MAP.put("13C-1234789-HpCDF", "<sup>13</sup>C-1234789-HpCDF");
        ITEM_CONVERTED_MAP.put("13C-2378-TCDD", "<sup>13</sup>C-2378-TCDD");
        ITEM_CONVERTED_MAP.put("13C-12378-PeCDD", "<sup>13</sup>C-12378-PeCDD");
        ITEM_CONVERTED_MAP.put("13C-12378-PeCDF", "<sup>13</sup>C-12378-PeCDF");
        ITEM_CONVERTED_MAP.put("13C-123478-HxCDD", "<sup>13</sup>C-123478-HxCDD");
        ITEM_CONVERTED_MAP.put("13C-123678-HxCDD", "<sup>13</sup>C-123678-HxCDD");
        ITEM_CONVERTED_MAP.put("13C-1234678-HpCDD", "<sup>13</sup>C-1234678-HpCDD");
        ITEM_CONVERTED_MAP.put("13C-OCDD", "<sup>13</sup>C-OCDD");

        ITEM_CONVERTED_MAP.put("37Cl4-2378-TCDD", "<sup>37</sup>Cl<sub>4</sub>-2378-TCDD");
    }

    public DioxinRecoveryRateTable(XWPFDocument document, EmcOrderBean order, List<EmcReportResultBean> reportResultList) {
        this.document = document;
        this.order = order;
        this.reportResultList = reportResultList;
    }

    @Override
    public XWPFDocument getDocument() {
        return document;
    }

    @Override
    public String getTableTitle() {
        return String.format(PfConstant.DIOXIN_RECOVERY_RATE_TITLE, WordTemplateUtils.handleOrderCategory(order.getOrderCategory()));
    }

    @Override
    public int getTableIndex() {
        return 0;
    }

    @Override
    public int getDynamicTableIndex() {
        return 2;
    }

    @Override
    public boolean isLast() {
        return false;
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<String> dynamicTitleList = new ArrayList<>();
        dynamicTitleList.add(order.getOrderNo());

        // 合并列配置
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 1, 2));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(1, 0, 1));
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        List<List<Object>> headerDataList = buildTableDynamicHeader(PfConstant.DIOXIN_RECOVERY_RATE_TABLE_HEADER_TITLES, PfConstant.DIOXIN_RECOVERY_RATE_TABLE_HEADER_COLUMN_QTY, dynamicTitleList);
        setTableTitle(PfConstant.DIOXIN_RECOVERY_RATE_TABLE_HEADER_TITLES, PfConstant.DIOXIN_RECOVERY_RATE_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        // 过滤出样品值结果
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)).collect(Collectors.toList());
        List<EmcReportResultBean> validResultList = sampleReportResultList.stream().filter(result -> StringUtils.contains(result.getItemAliasName(), PfConstant.DIOXIN_RECOVERY_RATE_KEYWORD)).collect(Collectors.toList());

        // 用数字序号来区分排序，采样内标需要在净化内标之前
        validResultList.forEach(r -> r.setExt$Item("resultGroup", "1" + PfConstant.DIOXIN_PURIFY_STANDARD));
        validResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), PfConstant.DIOXIN_SAMPLING_STANDARD_ITEM)).forEach(r -> r.setExt$Item("resultGroup", "0" + PfConstant.DIOXIN_SAMPLING_STANDARD));
        validResultList.sort(Comparator.comparing(result -> result.getExt$Item("resultGroup")));
        Map<String, List<EmcReportResultBean>> resultGroupMap = validResultList.stream().collect(Collectors.groupingBy(r -> r.getExt$Item("resultGroup"), LinkedHashMap::new, Collectors.toList()));

        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);
        AtomicInteger mergedRowIndex = new AtomicInteger(1);
        resultGroupMap.forEach((resultGroup, resultList) -> {
            resultList.sort(Comparator.comparing(EmcReportResultBean::getItemOrderNo));
            resultList.forEach(result -> {
                List<Object> rowDataList = new ArrayList<>();
                rowDataList.add(resultGroup.substring(1));
                String showItemName = StringUtils.replace(result.getItemAliasName(), PfConstant.DIOXIN_RECOVERY_RATE_KEYWORD, "");
                rowDataList.add(Optional.ofNullable(ITEM_CONVERTED_MAP.get(showItemName)).orElse(showItemName));
                rowDataList.add(result.getReportResult());
                tableBodyDataList.add(rowDataList);
            });

            int startRowIndex = mergedRowIndex.get() + 1;
            int endRowIndex = mergedRowIndex.addAndGet(resultList.size());
            mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, startRowIndex, endRowIndex));
        });

        dynamicTableDTO.setMergedRowList(mergedRowList);

        return tableBodyDataList;
    }
}
