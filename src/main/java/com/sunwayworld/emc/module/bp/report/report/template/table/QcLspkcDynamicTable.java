package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.constant.EmcReportConstant;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 质控报告（样品加标）
 * @author zhengxy@sunwayworld.com 2021年06月25日
 */
public class QcLspkcDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private List<EmcOrderTaskBean> orderTaskList;
    private List<EmcOrderTaskResultBean> taskResultList;
    private int tableIndex;
    private int dynamicTableIndex;

    public QcLspkcDynamicTable(XWPFDocument document, List<EmcOrderTaskBean> orderTaskList, List<EmcOrderTaskResultBean> taskResultList, int tableIndex, int dynamicTableIndex) {
        this.document = document;
        this.orderTaskList = orderTaskList;
        this.taskResultList = taskResultList;
        this.tableIndex = tableIndex;
        this.dynamicTableIndex = dynamicTableIndex;
    }

    @Override
    public XWPFDocument getDocument() {
        return document;
    }

    @Override
    public String getTableTitle() {
        return String.format(PfConstant.QC_LSPKC_RESULT_TABLE_TITLE, tableIndex);
    }

    @Override
    public int getTableIndex() {
        return tableIndex;
    }

    @Override
    public boolean isLast() {
        return true;
    }

    @Override
    public int getDynamicTableIndex() {
        return this.dynamicTableIndex;
    }

    @Override
    public int getTableWidth() {
        return 10500;
    }

    @Override
    public String getReplaceKeyword() {
        return PfConstant.QC_RESULT_TABLE_REPLACE_KEYWORD;
    }

    @Override
    public ParagraphAlignment getTitleAlignment() {
        return ParagraphAlignment.CENTER;
    }

    @Override
    public List<Integer> getColumnWidthList() {
        return WordTemplateUtils.cm2Ints(Arrays.asList(3.6D, 0D, 1.6D, 2.0D, 2.1D, 2.6D, 2.2D, 1.5D));
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> headerDataList = buildTableDynamicHeader(PfConstant.QC_LSPKC_RESULT_TABLE_HEADER_TITLES, PfConstant.QC_LSPKC_RESULT_TABLE_HEADER_COLUMN_QTY, null);
        setTableTitle(PfConstant.QC_LSPKC_RESULT_TABLE_HEADER_TITLES, PfConstant.QC_LSPKC_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        Map<Long, List<EmcOrderTaskBean>> orderTaskMap = orderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getOrderId));
        orderTaskMap.forEach((orderId, taskList) -> {
            WordTemplateUtils.sortedByAnalysisOrg(taskList);

            taskList.forEach(task -> {
                List<EmcOrderTaskResultBean> curResultList = taskResultList.stream().filter(result -> ObjectUtils.equals(result.getOrderTaskId(), task.getId())
                        && (StringUtils.equals(result.getReportDisplay(), Constant.YES) || StringUtils.contains(result.getItemName(), EmcReportConstant.RESULT_ITEM_KEYWORD))).collect(Collectors.toList());

                curResultList.stream().filter(r -> StringUtils.isEmpty(r.getExt$Item("itemGroup"))).forEach(r -> r.setExt$Item("itemGroup", r.getExt$Item("testName")));
                curResultList.sort(Comparator.comparing(r -> r.getExt$Item("itemGroup")));
                Map<String, List<EmcOrderTaskResultBean>> curResultMap = curResultList.stream().collect(Collectors.groupingBy(r -> r.getExt$Item("itemGroup")));

                curResultMap.forEach((itemGroup, resultList) -> {
                    EmcOrderTaskResultBean taskResult = resultList.get(0);
                    List<Object> rowDataList = new ArrayList<>();
                    rowDataList.add(taskResult.getOrderNo());
                    String replacedName = StringUtils.replace(itemGroup, PfConstant.SPECIAL_TEST_NAME, PfConstant.SPECIAL_REPLACED_TEST_NAME);
                    rowDataList.add(replacedName);
                    rowDataList.add(taskResult.getUnitName());
                    rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(taskResult.getReportResult()));
                    String standQtyUnit = taskResult.getRn12();
                    rowDataList.add(taskResult.getStandQty() + (StringUtils.isEmpty(standQtyUnit) ? "" : "(" + standQtyUnit + ")"));
                    rowDataList.add(taskResult.getRn6());
                    boolean hasIndexLimit = !StringUtils.isEmpty(taskResult.getIndexLowLimit()) && !StringUtils.isEmpty(taskResult.getIndexHighLimit());
                    rowDataList.add(hasIndexLimit ? (taskResult.getIndexLowLimit() + "-" + taskResult.getIndexHighLimit()) : null);

                    boolean oos = StringUtils.equals(taskResult.getResultStatus(), "OOS");
                    rowDataList.add(oos ? PfConstant.QC_RESULT_INEFFECTIVE : PfConstant.QC_RESULT_EFFECTIVE);

                    tableBodyDataList.add(rowDataList);
                });

            });
        });

        return tableBodyDataList;
    }
}
