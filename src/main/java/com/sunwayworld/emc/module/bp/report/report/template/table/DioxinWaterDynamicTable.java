package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
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
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 水（二噁英）检测结果表
 * @author zhengxy@sunwayworld.com 2021年07月08日
 */
public class DioxinWaterDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private EmcReportBean report;
    private List<EmcOrderBean> orderList;
    private List<EmcReportResultBean> reportResultList;
    private int tableIndex;
    private boolean sampling;
    private boolean last;

    public DioxinWaterDynamicTable(XWPFDocument document, EmcReportBean report, List<EmcOrderBean> orderList, List<EmcReportResultBean> reportResultList, int tableIndex, boolean sampling, boolean last) {
        this.document = document;
        this.report = report;
        this.orderList = orderList;
        this.reportResultList = reportResultList;
        this.tableIndex = tableIndex;
        this.sampling = sampling;
        this.last = last;
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
        return last;
    }

    @Override
    public int getDynamicTableIndex() {
        return 2;
    }

    @Override
    public List<Integer> getColumnWidthList() {
        return Arrays.asList(1253, 0, 1945, 0, 1140, 1587);
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        String[] tableTitles = sampling ? PfConstant.DIOXIN_WATER_RESULT_TABLE_HEADER_TITLES : PfConstant.DIOXIN_WATER_RESULT_TABLE_HEADER_TITLES_2;
        List<List<Object>> headerDataList = buildTableDynamicHeader(tableTitles, PfConstant.DIOXIN_WATER_RESULT_TABLE_HEADER_COLUMN_QTY, null);
        setTableTitle(tableTitles, PfConstant.DIOXIN_WATER_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        // 过滤出样品值结果
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)).collect(Collectors.toList());

        Map<String, String> orderNoDescMap = handleOrderDesc(orderList);

        Map<String, EmcOrderBean> orderMap = orderList.stream().collect(Collectors.toMap(EmcOrderBean::getOrderNo, Function.identity()));

        Map<String, List<EmcReportResultBean>> orderResultMap = sampleReportResultList.stream().sorted(Comparator.comparing(EmcReportResultBean::getOrderId)).collect(Collectors.groupingBy(EmcReportResultBean::getOrderNo, LinkedHashMap::new, Collectors.toList()));
        orderResultMap.forEach((orderNo, resultList) -> {
            List<Object> rowDataList = new ArrayList<>();
            EmcReportResultBean result = resultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), PfConstant.DIOXIN_KEYWORD)).findFirst().orElseGet(EmcReportResultBean::new);
            EmcOrderBean curOrder = Optional.ofNullable(orderMap.get(orderNo)).orElseGet(EmcOrderBean::new);
            rowDataList.add(sampling ? DateTimeUtils.formatLocalDate(result.getSampleBeginDate()) : DateTimeUtils.formatLocalDate(curOrder.getSendDate()));
            rowDataList.add(sampling ? result.getMonitorPointName() : Optional.ofNullable(curOrder.getClientOrderName()).orElse(curOrder.getLabelNo()));

            rowDataList.add(result.getOrderNo());
            rowDataList.add(orderNoDescMap.get(orderNo));
            rowDataList.add(result.getTestName());
            rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(result.getReportResult()));

            tableBodyDataList.add(rowDataList);
        });

        // 是否显示执行标准
        boolean displayStd = StringUtils.equals(Constant.YES, report.getExt$Item("displayStd"));
        if (displayStd) {
            // 添加备注行
            tableBodyDataList.add(buildDynamicRowDataList(PfConstant.EXECUTIVE_STANDARD, report.getRemark(), PfConstant.DIOXIN_WATER_RESULT_TABLE_HEADER_COLUMN_QTY));
            // 合并备注列
            List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 1, 1, PfConstant.DIOXIN_WATER_RESULT_TABLE_HEADER_COLUMN_QTY - 1));
        }

        return tableBodyDataList;
    }

    private Map<String, String> handleOrderDesc(List<EmcOrderBean> orderList) {
        Map<String, String> orderNoDescMap = orderList.stream().filter(order -> !StringUtils.isEmpty(order.getOrderDesc())).collect(Collectors.toMap(EmcOrderBean::getOrderNo, EmcOrderBean::getOrderDesc, (v1, v2) -> v1));

        orderNoDescMap.forEach((orderNo, orderDesc) -> {
            if (!StringUtils.contains(orderDesc, PfConstant.COMMA_DELIMITER) && !StringUtils.contains(orderDesc, PfConstant.COMMA1_DELIMITER)) {
                String validOrderDesc = StringUtils.contains(orderDesc, PfConstant.WATER_EXCLUDE_ORDER_DESC_KEYWORD) ? null : orderDesc;
                orderNoDescMap.put(orderNo, validOrderDesc);
                return;
            }

            // 处理逗号分隔的情况，去除包含浊度的描述
            if (StringUtils.contains(orderDesc, PfConstant.COMMA_DELIMITER)) {
                String validOrderDesc = Arrays.stream(StringUtils.split(orderDesc, PfConstant.COMMA_DELIMITER)).filter(desc -> !StringUtils.contains(desc, PfConstant.WATER_EXCLUDE_ORDER_DESC_KEYWORD)).collect(Collectors.joining(PfConstant.COMMA_DELIMITER));
                orderNoDescMap.put(orderNo, validOrderDesc);
                return;
            }

            // 处理顿号分隔的情况，去除包含浊度的描述，然后统一转换为使用逗号连接
            if (StringUtils.contains(orderDesc, PfConstant.COMMA1_DELIMITER)) {
                String validOrderDesc = Arrays.stream(StringUtils.split(orderDesc, PfConstant.COMMA1_DELIMITER)).filter(desc -> !StringUtils.contains(desc, PfConstant.WATER_EXCLUDE_ORDER_DESC_KEYWORD)).collect(Collectors.joining(PfConstant.COMMA_DELIMITER));
                orderNoDescMap.put(orderNo, validOrderDesc);
            }
        });

        return orderNoDescMap;
    }

}
