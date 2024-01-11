package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
 * 质控报告（实验室标准样品）
 * @author zhengxy@sunwayworld.com 2021年06月25日
 */
public class QcStdDynamicTable  implements DynamicTable {

    private XWPFDocument document;
    private List<EmcOrderTaskBean> orderTaskList;
    private List<EmcOrderTaskResultBean> taskResultList;
    private int tableIndex;
    private int dynamicTableIndex;

    public QcStdDynamicTable(XWPFDocument document, List<EmcOrderTaskBean> orderTaskList, List<EmcOrderTaskResultBean> taskResultList, int tableIndex, int dynamicTableIndex) {
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
        return String.format(PfConstant.QC_STD_RESULT_TABLE_TITLE, tableIndex);
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
        return dynamicTableIndex;
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
        return WordTemplateUtils.cm2Ints(Arrays.asList(4.1D, 0D, 3.2D, 1.6D, 2.0D, 2.6D, 1.5D));
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> headerDataList = buildTableDynamicHeader(PfConstant.QC_STD_RESULT_TABLE_HEADER_TITLES, PfConstant.QC_STD_RESULT_TABLE_HEADER_COLUMN_QTY, null);
        setTableTitle(PfConstant.QC_STD_RESULT_TABLE_HEADER_TITLES, PfConstant.QC_STD_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        orderTaskList.sort(Comparator.comparing(EmcOrderTaskBean::getOrderId).thenComparing(EmcOrderTaskBean::getTestId));
        Map<Long, List<EmcOrderTaskBean>> orderTaskMap = orderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getOrderId, LinkedHashMap::new, Collectors.toList()));
        orderTaskMap.forEach((orderId, taskList) -> {
            WordTemplateUtils.sortedByAnalysisOrg(taskList);

            taskList.forEach(task -> {
                List<EmcOrderTaskResultBean> curResultList = taskResultList.stream().filter(r -> ObjectUtils.equals(r.getOrderTaskId(), task.getId())).collect(Collectors.toList());
                EmcOrderTaskResultBean finalResult = curResultList.stream().filter(r -> Constant.YES.equals(r.getReportDisplay())).findFirst().orElse(null);
                if (Objects.isNull(finalResult)) {
                    finalResult = curResultList.stream().filter(r -> StringUtils.contains(r.getItemName(), EmcReportConstant.RESULT_ITEM_KEYWORD)).findFirst().orElse(null);
                    if (Objects.isNull(finalResult)) {
                        curResultList.sort(Comparator.comparing(EmcOrderTaskResultBean::getItemOrderNo).reversed());
                        finalResult = curResultList.get(0);
                    }
                }

                List<Object> rowDataList = new ArrayList<>();
                //String batchNo = StringUtils.isEmpty(task.getStockBatchNo()) ? "" : "(" + task.getStockBatchNo() + ")";
                //rowDataList.add(finalResult.getOrderNo() + batchNo);
                rowDataList.add(finalResult.getOrderNo());
                rowDataList.add(task.getStockBatchNo());

                String itemGroup = Optional.ofNullable(task.getExt$Item("itemGroup")).orElse(task.getTestName());
                itemGroup = StringUtils.replace(itemGroup, PfConstant.SPECIAL_TEST_NAME, PfConstant.SPECIAL_REPLACED_TEST_NAME);
                rowDataList.add(itemGroup);
                // 单位取标准值单位，标准设置配置的rn12字段
                rowDataList.add(Optional.ofNullable(finalResult.getRn12()).orElse(finalResult.getUnitName()));

                rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(finalResult.getReportResult()));
                boolean hasIndexLimit = !StringUtils.isEmpty(finalResult.getIndexLowLimit()) && !StringUtils.isEmpty(finalResult.getIndexHighLimit());
                rowDataList.add(hasIndexLimit ? (finalResult.getIndexLowLimit() + "-" + finalResult.getIndexHighLimit()) : null);

                boolean oos = StringUtils.equals(finalResult.getResultStatus(), "OOS");
                rowDataList.add(oos ? PfConstant.QC_RESULT_INEFFECTIVE : PfConstant.QC_RESULT_EFFECTIVE);

                tableBodyDataList.add(rowDataList);
            });
        });

        return tableBodyDataList;
    }
}
