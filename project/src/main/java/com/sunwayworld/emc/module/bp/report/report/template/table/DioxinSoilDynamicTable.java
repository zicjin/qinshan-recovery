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
 * 土壤（二噁英） 检测结果表
 * @author zhengxy@sunwayworld.com 2021年07月08日
 */
public class DioxinSoilDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private EmcReportBean report;
    private List<EmcOrderBean> orderList;
    private List<EmcReportResultBean> reportResultList;
    private int tableIndex;
    private boolean sampling;
    private boolean last;
    private boolean solidWaste;

    public DioxinSoilDynamicTable(XWPFDocument document, EmcReportBean report, List<EmcOrderBean> orderList, List<EmcReportResultBean> reportResultList, int tableIndex, boolean sampling, boolean last) {
        this.document = document;
        this.report = report;
        this.orderList = orderList;
        this.reportResultList = reportResultList;
        this.tableIndex = tableIndex;
        this.sampling = sampling;
        this.last = last;
        this.solidWaste = !orderList.isEmpty() && isSolidWaste(orderList.get(0));
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
        // 送样的和固废没有采样深度
        return (sampling && !solidWaste) ? Arrays.asList(1253, 0, 1945, 1145, 0, 1140, 1587) : Arrays.asList(1253, 0, 1945, 0, 1140, 1587);
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        String[] tableTitles = sampling ? (solidWaste ? PfConstant.DIOXIN_SOLID_WASTE_RESULT_TABLE_HEADER_TITLES : PfConstant.DIOXIN_SOIL_RESULT_TABLE_HEADER_TITLES) : PfConstant.DIOXIN_SOIL_RESULT_TABLE_HEADER_TITLES_2;
        int tableColumnQty = (sampling && !solidWaste) ? PfConstant.DIOXIN_SOIL_RESULT_TABLE_HEADER_COLUMN_QTY : PfConstant.DIOXIN_SOIL_RESULT_TABLE_HEADER_COLUMN_QTY_2;

        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)).collect(Collectors.toList());
        EmcReportResultBean result = sampleReportResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), PfConstant.DIOXIN_KEYWORD)).findFirst().orElseGet(EmcReportResultBean::new);

        // 获取二噁英测定浓度分析项单位，默认为 ngTEQ/kg
        tableTitles[tableTitles.length - 1] = String.join("", PfConstant.DIOXIN_RESULT_KEYWORD, "(", Optional.ofNullable(result.getUnitName()).orElse("ngTEQ/kg"), ")");

        List<List<Object>> headerDataList = buildTableDynamicHeader(tableTitles, tableColumnQty, null);
        setTableTitle(tableTitles, tableColumnQty, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        // 过滤出样品值结果
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)).collect(Collectors.toList());

        // 样品描述
        orderList.stream().filter(order -> StringUtils.isEmpty(order.getOrderDesc())).forEach(order -> order.setOrderDesc(""));
        Map<String, String> orderNoDescMap = orderList.stream().collect(Collectors.toMap(EmcOrderBean::getOrderNo, this::concatOrderDesc, (v1, v2) -> v1));

        Map<String, EmcOrderBean> orderMap = orderList.stream().collect(Collectors.toMap(EmcOrderBean::getOrderNo, Function.identity()));

        // 采样深度
        Map<String, String> samplingDepthMap = orderList.stream().filter(order -> !StringUtils.isEmpty(order.getExt$Item("samplingDepth"))).collect(Collectors.toMap(EmcOrderBean::getOrderNo, order -> order.getExt$Item("samplingDepth"), (v1, v2) -> v1));

        sampleReportResultList.sort(Comparator.comparing(EmcReportResultBean::getOrderNo));
        Map<String, List<EmcReportResultBean>> orderResultMap = sampleReportResultList.stream().collect(Collectors.groupingBy(EmcReportResultBean::getOrderNo, LinkedHashMap::new, Collectors.toList()));
        orderResultMap.forEach((orderNo, resultList) -> {
            List<Object> rowDataList = new ArrayList<>();
            EmcReportResultBean result = resultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), PfConstant.DIOXIN_KEYWORD)).findFirst().orElseGet(EmcReportResultBean::new);
            EmcOrderBean curOrder = Optional.ofNullable(orderMap.get(orderNo)).orElseGet(EmcOrderBean::new);
            rowDataList.add(sampling ? DateTimeUtils.formatLocalDate(result.getSampleBeginDate()) : DateTimeUtils.formatLocalDate(curOrder.getSendDate()));
            rowDataList.add(sampling ? result.getMonitorPointName() : Optional.ofNullable(curOrder.getClientOrderName()).orElse(curOrder.getLabelNo()));
            rowDataList.add(result.getOrderNo());
            // 采样深度
            if (sampling && !solidWaste) {
                rowDataList.add(samplingDepthMap.get(result.getOrderNo()));
            }
            rowDataList.add(StringUtils.isEmpty(orderNoDescMap.get(orderNo)) ? null : orderNoDescMap.get(orderNo));
            rowDataList.add(result.getTestName());
            rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(result.getReportResult()));

            tableBodyDataList.add(rowDataList);
        });

        // 是否显示执行标准
        boolean displayStd = StringUtils.equals(Constant.YES, report.getExt$Item("displayStd"));
        int tableColumnQty = (sampling && !solidWaste) ? PfConstant.DIOXIN_SOIL_RESULT_TABLE_HEADER_COLUMN_QTY : PfConstant.DIOXIN_SOIL_RESULT_TABLE_HEADER_COLUMN_QTY_2;
        if (displayStd) {
            // 添加执行标准行
            tableBodyDataList.add(buildDynamicRowDataList(PfConstant.EXECUTIVE_STANDARD, report.getRemark(), tableColumnQty));
            // 合并执行标准
            List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + 1, 1, tableColumnQty - 1));
        }

        return tableBodyDataList;
    }

    private String concatOrderDesc(EmcOrderBean order) {
        // 对于送样的，样品描述会存于orderDesc字段
        if (!sampling) {
            return order.getOrderDesc();
        }

        // 对于固体废物类型，采样的样品描述统一为固定值：固体
        if (solidWaste) {
            return PfConstant.DIOXIN_SOLID_WASTE_ORDER_DESC;
        }

        List<String> orderDescList = new ArrayList<>();
        Arrays.asList("color", "texture", "humidity").forEach(property -> {
            if (!StringUtils.isEmpty(order.getExt$Item(property))) {
                orderDescList.add(order.getExt$Item(property));
            }
        });

        if (!orderDescList.isEmpty()) {
            return String.join("、", orderDescList);
        }

        return order.getOrderDesc();
    }

    private boolean isSolidWaste(EmcOrderBean order) {
        return Arrays.stream(PfConstant.DIOXIN_SOLID_WASTE_ORDER_CATEGORIES).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));
    }

}
