package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 有组织废气（二噁英） 检测结果表
 * @author zhengxy@sunwayworld.com 2021年07月08日
 */
public class DioxinGasDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private EmcReportBean report;
    private List<EmcOrderBean> orderList;
    private List<EmcReportResultBean> reportResultList;
    private List<EmcPropertyDataBean> propertyDataList;
    private int tableIndex;
    private boolean sampling;
    private boolean last;
    private boolean displayAvgResult;
    private boolean displayParameter;
    private boolean displayConvertedResult;

    public DioxinGasDynamicTable(XWPFDocument document, EmcReportBean report, List<EmcOrderBean> orderList, List<EmcReportResultBean> reportResultList, List<EmcPropertyDataBean> propertyDataList, int tableIndex, boolean sampling, boolean last) {
        this.document = document;
        this.report = report;
        this.orderList = orderList;
        this.reportResultList = reportResultList;
        this.propertyDataList = propertyDataList;
        this.tableIndex = tableIndex;
        this.sampling = sampling;
        this.last = last;
        this.displayAvgResult = Constant.YES.equals(report.getExt$Item("displayAvgResult"));
        this.displayParameter = Constant.YES.equals(report.getExt$Item("displayParameter"));
        this.displayConvertedResult = Constant.YES.equals(report.getExt$Item("displayConvertedResult"));
    }

    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }

    @Override
    public String getTableTitle() {
        return String.format(PfConstant.DIOXIN_RESULT_TABLE_TITLE, tableIndex, orderList.isEmpty() ? "" : WordTemplateUtils.handleOrderCategory(orderList.get(0).getOrderCategory()));
    }

    @Override
    public int getTableIndex() {
        return tableIndex;
    }

    @Override
    public boolean isLast() {
        return displayParameter || last;
    }

    @Override
    public int getDynamicTableIndex() {
        return 2;
    }

    @Override
    public List<Integer> getColumnWidthList() {
        return displayAvgResult ? Arrays.asList(1253, 0, 1945, 0, 1140, 1587, 1587) : Arrays.asList(1253, 0, 1945, 0, 1140, 1587);
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        String[] samplingTitles = displayAvgResult ? PfConstant.DIOXIN_GAS_RESULT_TABLE_HEADER_TITLES : PfConstant.DIOXIN_GAS_RESULT_TABLE_HEADER_TITLES_3;
        String[] noSamplingTitles = displayAvgResult ? PfConstant.DIOXIN_GAS_RESULT_TABLE_HEADER_TITLES_2 : PfConstant.DIOXIN_GAS_RESULT_TABLE_HEADER_TITLES_4;

        String[] tableTitles = sampling ? samplingTitles : noSamplingTitles;
        int tableColumnQty = displayAvgResult ? PfConstant.DIOXIN_GAS_RESULT_TABLE_HEADER_COLUMN_QTY : PfConstant.DIOXIN_GAS_RESULT_TABLE_HEADER_COLUMN_QTY_2;

        List<List<Object>> headerDataList = buildTableDynamicHeader(tableTitles, tableColumnQty, null);
        setTableTitle(tableTitles, tableColumnQty, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        // 过滤出样品值结果
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)).collect(Collectors.toList());
        // 过滤出日均值
        List<EmcReportResultBean> dayReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_DAYAVERAGE)).collect(Collectors.toList());

        AtomicInteger mergedRowIndex = new AtomicInteger(0);
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);

        Map<Long, List<EmcReportResultBean>> folderResultMap = sampleReportResultList.stream().sorted(Comparator.comparing(EmcReportResultBean::getFolderId)).collect(Collectors.groupingBy(EmcReportResultBean::getFolderId, LinkedHashMap::new, Collectors.toList()));
        folderResultMap.forEach((folderId, resultList) -> {

            // 按日期（天）进行分组
            List<EmcReportResultBean> validResultList = resultList.stream().filter(result -> !ObjectUtils.isEmpty(result.getSampleBeginDate())).sorted(Comparator.comparing(result -> DateTimeUtils.formatLocalDate(result.getSampleBeginDate()))).collect(Collectors.toList());

            if (!validResultList.isEmpty()) {
                Map<String, List<EmcReportResultBean>> curDayReportResultMap = validResultList.stream().collect(Collectors.groupingBy(result -> DateTimeUtils.formatLocalDate(result.getSampleBeginDate()), LinkedHashMap::new, Collectors.toList()));
                curDayReportResultMap.forEach((day, curDayReportResultList) -> {
                    curDayReportResultList.sort(Comparator.comparing(EmcReportResultBean::getOrderNo));
                    Map<String, List<EmcReportResultBean>> orderResultMap = curDayReportResultList.stream().collect(Collectors.groupingBy(EmcReportResultBean::getOrderNo, LinkedHashMap::new, Collectors.toList()));

                    // 此处的均值并非日均值
                    List<EmcReportResultBean> calcAvgResultList = new ArrayList<>();
                    orderResultMap.values().forEach(list -> list.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), displayConvertedResult ? PfConstant.DIOXIN_CONVERTED_RESULT_ITEM_KEYWORD : PfConstant.DIOXIN_RESULT_ITEM_KEYWORD)).forEach(calcAvgResultList::add));
                   // OptionalDouble avgResult = calcAvgResultList.stream().filter(r -> NumberUtils.isNumber(r.getReportResult())).map(EmcReportResultBean::getReportResult).mapToDouble(NumberUtils::parseDouble).average();
                    List<String>  reportResultStrList = calcAvgResultList.stream().filter(r -> NumberUtils.isNumber(r.getReportResult())).map(EmcReportResultBean::getReportResult).collect(Collectors.toList());
                    Double reportResultSum =  reportResultStrList.stream().mapToDouble(NumberUtils::parseDouble).sum();
                    Double avgResult= 0D;
                    if(!calcAvgResultList.isEmpty()){
                         avgResult = ArithUtils.div(reportResultSum, calcAvgResultList.size());
                    }
                    
                    String revisionRule = calcAvgResultList.isEmpty() ? "EMC,2" : Optional.ofNullable(calcAvgResultList.get(0).getRevisionRule()).orElse("EMC,2");
                    //String roundingValue = avgResult.isPresent() ? NumberUtils.rounding(avgResult.getAsDouble() + "", revisionRule) : null;
                    //这一步之前就要去除科学计数法
                    //此处必须用valueOf，用new BigDecimal会导致精度丢失
                    
                    String avgResultS = String.valueOf(avgResult);
                    BigDecimal bigDecimal = new BigDecimal(avgResultS);
                    String avgResultA = bigDecimal.toPlainString();
                    
                    String roundingValue = NumberUtils.rounding(avgResultA + "", revisionRule);
                    
                    orderResultMap.forEach((orderNo, orderResultList) -> {
                        List<Object> rowDataList = new ArrayList<>();
                        EmcReportResultBean result = orderResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), displayConvertedResult ? PfConstant.DIOXIN_CONVERTED_RESULT_ITEM_KEYWORD : PfConstant.DIOXIN_RESULT_ITEM_KEYWORD)).findFirst().orElseGet(EmcReportResultBean::new);

                        rowDataList.add(DateTimeUtils.formatLocalDate(result.getSampleBeginDate()));
                        rowDataList.add(result.getMonitorPointName());
                        rowDataList.add(result.getOrderNo());
                        rowDataList.add(sampling ? PfConstant.DIOXIN_GAS_ORDER_DESC : PfConstant.DIOXIN_GAS_ORDER_DESC_2);
                        rowDataList.add(result.getTestName());
                        rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(result.getReportResult()));

                        // 是否显示平均值
                        if (displayAvgResult) {
                            rowDataList.add(NumberUtils.isScientificNumber(roundingValue) ? NumberUtils.formatNumber(NumberUtils.parseDouble(roundingValue)) : roundingValue);
                        }
                        tableBodyDataList.add(rowDataList);
                    });
                    if (displayAvgResult) {
                        int startMergedRowIndex = mergedRowIndex.get() + 1;
                        int endMergedRowIndex = mergedRowIndex.addAndGet(orderResultMap.size());
                        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(6, startMergedRowIndex, endMergedRowIndex));
                    }

                });
            } else {
            	resultList.sort(Comparator.comparing(EmcReportResultBean::getOrderNo));
                Map<String, List<EmcReportResultBean>> orderResultMap = resultList.stream().collect(Collectors.groupingBy(EmcReportResultBean::getOrderNo, LinkedHashMap::new, Collectors.toList()));
                orderResultMap.forEach((orderNo, orderResultList) -> {
                    List<Object> rowDataList = new ArrayList<>();
                    EmcReportResultBean result = orderResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), displayConvertedResult ? PfConstant.DIOXIN_CONVERTED_RESULT_ITEM_KEYWORD : PfConstant.DIOXIN_RESULT_ITEM_KEYWORD)).findFirst().orElseGet(EmcReportResultBean::new);
                    EmcReportResultBean totalAvgResult = dayReportResultList.stream().filter(r -> ObjectUtils.equals(folderId, r.getFolderId())
                            && StringUtils.contains(r.getTestName(), result.getTestName())
                            && ObjectUtils.equals(r.getCycle(), result.getCycle())
                            && ObjectUtils.equals(result.getItemId(), r.getItemId())).findFirst().orElseGet(EmcReportResultBean::new);

                    rowDataList.add(DateTimeUtils.formatLocalDate(result.getSampleBeginDate()));
                    rowDataList.add(result.getMonitorPointName());
                    rowDataList.add(result.getOrderNo());
                    rowDataList.add(sampling ? PfConstant.DIOXIN_GAS_ORDER_DESC : PfConstant.DIOXIN_GAS_ORDER_DESC_2);
                    rowDataList.add(result.getTestName());
                    rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(result.getReportResult()));

                    // 是否显示平均值
                    if (displayAvgResult) {
                       // rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(totalAvgResult.getReportResult()));
                        String avgResultS = String.valueOf(totalAvgResult.getReportResult() );
                        BigDecimal bigDecimal = new BigDecimal(avgResultS);
                        String avgResultA = bigDecimal.toPlainString();
                        
                        String roundingValueA = NumberUtils.rounding(avgResultA + "", "EMC,2");

                        rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(NumberUtils.formatNumber(NumberUtils.parseDouble(roundingValueA))));

                        

                    }
                    tableBodyDataList.add(rowDataList);
                });
                if (displayAvgResult) {
                    int startMergedRowIndex = mergedRowIndex.get() + 1;
                    int endMergedRowIndex = mergedRowIndex.addAndGet(orderResultMap.size());
                    mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(6, startMergedRowIndex, endMergedRowIndex));
                }
            }
        });

        dynamicTableDTO.setMergedRowList(mergedRowList);

        // 是否显示执行标准
        boolean displayStd = StringUtils.equals(Constant.YES, report.getExt$Item("displayStd"));
        if (displayStd) {
            int tableColumnQty = displayAvgResult ? PfConstant.DIOXIN_GAS_RESULT_TABLE_HEADER_COLUMN_QTY : PfConstant.DIOXIN_GAS_RESULT_TABLE_HEADER_COLUMN_QTY_2;

            // 添加备注行
            tableBodyDataList.add(buildDynamicRowDataList(PfConstant.EXECUTIVE_STANDARD, report.getRemark(), tableColumnQty));
            // 合并备注列
            List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 1, 1, tableColumnQty - 1));
        }

        return tableBodyDataList;
    }

    @Override
    public WordTemplateDynamicTableDTO buildTable() {
        if (displayParameter) {
            DioxinGasParameterDynamicTable dynamicTable = new DioxinGasParameterDynamicTable(document, reportResultList, propertyDataList, sampling, last);
            dynamicTable.create();
        }

        return DynamicTable.super.buildTable();
    }

}
