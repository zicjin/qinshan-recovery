package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.emc.module.bp.result.temphum.bean.PfRunTempHumidityBean;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 平行双样分析结果记录表（二噁英质控报告）
 * @author zhengxy@sunwayworld.com 2022年02月23日
 */
public class DioxinQcDupResultTable implements DynamicTable {

    private XWPFDocument document;
    private List<EmcOrderBean> qcOrderList;
    private List<PfRunTempHumidityBean> tempHumidityList;
    private List<EmcOrderTaskResultBean> qcTaskResultList;
    private List<EmcOrderTaskResultBean> naTaskResultList;
    private int tableIndex;

    public DioxinQcDupResultTable(XWPFDocument document, List<EmcOrderBean> qcOrderList, List<PfRunTempHumidityBean> tempHumidityList, List<EmcOrderTaskResultBean> qcTaskResultList, List<EmcOrderTaskResultBean> naTaskResultList, int tableIndex) {
        this.document = document;
        this.qcOrderList = qcOrderList;
        this.tempHumidityList = tempHumidityList;
        this.qcTaskResultList = qcTaskResultList;
        this.naTaskResultList = naTaskResultList;
        this.tableIndex = tableIndex;
    }

    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }

    @Override
    public String getTableTitle() {
        return String.format(PfConstant.DIOXIN_QC_DUP_RESULT_TITLE, WordTemplateUtils.handleOrderCategory(qcOrderList.get(0).getOrderCategory()));
    }

    @Override
    public int getTableIndex() {
        return 0;
    }

    @Override
    public boolean isLast() {
        return true;
    }

    @Override
    public int getDynamicTableIndex() {
        return 2;
    }

    @Override
    public List<Integer> getColumnWidthList() {
        return WordTemplateUtils.cm2Ints(Arrays.asList(2.3D, 0D, 3.9D, 2.0D, 1.9D, 1.9D, 0D, 1.65D));
    }

    @Override
    public ParagraphAlignment getTitleAlignment() {
        return ParagraphAlignment.LEFT;
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> headerDataList = buildTableDynamicHeader(PfConstant.DIOXIN_QC_DUP_RESULT_TABLE_HEADER_TITLES, PfConstant.DIOXIN_QC_DUP_RESULT_TABLE_HEADER_COLUMN_QTY, null);
        setTableTitle(PfConstant.DIOXIN_QC_DUP_RESULT_TABLE_HEADER_TITLES, PfConstant.DIOXIN_QC_DUP_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        qcOrderList.sort(Comparator.comparing(EmcOrderBean::getOrderCategory).thenComparing(EmcOrderBean::getOrderNo));
        qcOrderList.forEach(qcOrder -> {
            List<Object> rowDataList = new ArrayList<>();
            EmcOrderTaskResultBean taskResult = qcTaskResultList.stream().filter(result -> ObjectUtils.equals(result.getOrderId(), qcOrder.getId())
                    && Constant.YES.equals(result.getReportDisplay())
                    && StringUtils.contains(result.getItemAliasName(), PfConstant.DIOXIN_KEYWORD)).findFirst().orElseGet(EmcOrderTaskResultBean::new);

            EmcOrderTaskResultBean parentResult = naTaskResultList.stream().filter(result -> StringUtils.equals(qcOrder.getParentOrderNo(), result.getOrderNo())
                    && Constant.YES.equals(result.getReportDisplay())
                    && StringUtils.contains(result.getItemAliasName(), PfConstant.DIOXIN_KEYWORD)).findFirst().orElseGet(EmcOrderTaskResultBean::new);

            LocalDate endDate = tempHumidityList.stream().filter(t -> ObjectUtils.equals(parentResult.getOrderTaskId(), t.getOrderTaskId()) && !ObjectUtils.isEmpty(t.getEndDate())).map(PfRunTempHumidityBean::getEndDate).findFirst().orElseGet(LocalDate::now);

            rowDataList.add(DateTimeUtils.formatLocalDate(endDate));
            rowDataList.add(WordTemplateUtils.handleOrderCategory(qcOrder.getOrderCategory()));
            rowDataList.add(parentResult.getOrderNo());

            rowDataList.add(taskResult.getExt$Item("testName"));
            rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(parentResult.getReportResult()));

            rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(taskResult.getReportResult()));
            rowDataList.add(StringUtils.isEmpty(taskResult.getRn7()) ? calcRelativeDeviation(taskResult.getReportResult(), parentResult.getReportResult()) : taskResult.getRn7());
            rowDataList.add(PfConstant.DIOXIN_QC_DUP_RESULT_QUALIFIED);
            tableBodyDataList.add(rowDataList);
        });

        // 添加备注行
        tableBodyDataList.add(buildDynamicRowDataList(PfConstant.REMARK, PfConstant.DIOXIN_QC_DUP_DEFAULT_REMARK, PfConstant.DIOXIN_QC_DUP_RESULT_TABLE_HEADER_COLUMN_QTY));
        // 合并备注列
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size(), 1, PfConstant.DIOXIN_QC_DUP_RESULT_TABLE_HEADER_COLUMN_QTY - 1));
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        return tableBodyDataList;
    }

    private String calcRelativeDeviation(String taskResult, String parentResult) {
        if (!NumberUtils.isNumber(taskResult) || !NumberUtils.isNumber(parentResult)) {
            return null;
        }

        double avgResult = ArithUtils.div(ArithUtils.add(taskResult, parentResult), 2D);
        double relativeDeviation = ArithUtils.mul(ArithUtils.div(ArithUtils.sub(parentResult, String.valueOf(avgResult)), avgResult), 100d);

        return NumberUtils.rounding(String.valueOf(relativeDeviation), "EMC,2");
    }
}
