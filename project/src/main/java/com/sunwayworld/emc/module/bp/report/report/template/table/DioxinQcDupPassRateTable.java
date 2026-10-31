package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 平行双样分析合格率记录表（二噁英质控报告）
 * @author zhengxy@sunwayworld.com 2022年02月23日
 */
public class DioxinQcDupPassRateTable implements DynamicTable {

    private XWPFDocument document;
    private List<EmcRunBean> runList;
    private List<EmcOrderBean> qcOrderList;
    private List<EmcOrderTaskResultBean> qcTaskResultList;
    private int tableIndex;

    public DioxinQcDupPassRateTable(XWPFDocument document, List<EmcRunBean> runList, List<EmcOrderBean> qcOrderList, List<EmcOrderTaskResultBean> qcTaskResultList, int tableIndex) {
        this.document = document;
        this.runList = runList;
        this.qcOrderList = qcOrderList;
        this.qcTaskResultList = qcTaskResultList;
        this.tableIndex = tableIndex;
    }

    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }

    @Override
    public String getTableTitle() {
        return String.format(PfConstant.DIOXIN_QC_DUP_PASS_RATE_TITLE, WordTemplateUtils.handleOrderCategory(qcOrderList.get(0).getOrderCategory()));
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
    public ParagraphAlignment getTitleAlignment() {
        return ParagraphAlignment.LEFT;
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> headerDataList = buildTableDynamicHeader(PfConstant.DIOXIN_QC_DUP_PASS_RATE_TABLE_HEADER_TITLES, PfConstant.DIOXIN_QC_DUP_PASS_RATE_TABLE_HEADER_COLUMN_QTY, null);
        setTableTitle(PfConstant.DIOXIN_QC_DUP_PASS_RATE_TABLE_HEADER_TITLES, PfConstant.DIOXIN_QC_DUP_PASS_RATE_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        Map<String, List<EmcOrderBean>> qcOrderMap = qcOrderList.stream().collect(Collectors.groupingBy(EmcOrderBean::getOrderCategory));
        qcOrderMap.forEach((orderCategory, curQcOrderList) -> {
            List<Object> rowDataList = new ArrayList<>();

            LocalDateTime auditDate = runList.stream().filter(r -> StringUtils.contains(r.getOrderCategory(), orderCategory) && !ObjectUtils.isEmpty(r.getAuditedDate())).map(EmcRunBean::getAuditedDate).findFirst().orElseGet(LocalDateTime::now);

            rowDataList.add(DateTimeUtils.formatLocalDateTime(auditDate, "yyyy-MM-dd"));
            rowDataList.add(WordTemplateUtils.handleOrderCategory(orderCategory));

            List<Long> curQcOrderIdList = curQcOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
            String testName = qcTaskResultList.stream().filter(r -> curQcOrderIdList.contains(r.getOrderId())).map(r -> r.getExt$Item("testName")).distinct().collect(Collectors.joining("、"));
            rowDataList.add(testName);

            int curQcOrderQty = curQcOrderList.size();
            int qualifiedQty = curQcOrderList.size();
            rowDataList.add(curQcOrderQty);
            rowDataList.add(qualifiedQty);
            rowDataList.add(qualifiedQty / curQcOrderQty * 100);
            tableBodyDataList.add(rowDataList);
        });

        return tableBodyDataList;
    }
}
