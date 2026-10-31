package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.constant.EmcReportConstant;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 质控报告（实验室平行）
 * @author zhengxy@sunwayworld.com 2021年06月25日
 */
public class QcLdupDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private List<EmcOrderBean> orderList;
    private List<EmcOrderTaskBean> orderTaskList;
    private List<EmcOrderTaskResultBean> parentTaskResultList;
    private List<EmcOrderTaskResultBean> ldupTaskResultList;
    private boolean isPh;
    private int tableIndex;
    private int dynamicTableIndex;
    private String decisionCategory;

    private static final String WATER_KEYWORD = "水";

    public QcLdupDynamicTable(XWPFDocument document, List<EmcOrderBean> orderList, List<EmcOrderTaskBean> orderTaskList, List<EmcOrderTaskResultBean> taskResultList, boolean isPh, int tableIndex, int dynamicTableIndex, String decisionCategory) {
        this.document = document;
        this.orderList = orderList;
        this.orderTaskList = orderTaskList;
        this.parentTaskResultList = taskResultList.stream().filter(result -> !StringUtils.equals(result.getQcCategoryCode(), PfConstant.QC_L_DUP)).collect(Collectors.toList());
        this.ldupTaskResultList = taskResultList.stream().filter(result -> StringUtils.equals(result.getQcCategoryCode(), PfConstant.QC_L_DUP)).collect(Collectors.toList());

        taskResultList.forEach(res ->{
            List<EmcOrderTaskBean> orderTaskMatchList = orderTaskList.stream().filter(ok ->ObjectUtils.equals(res.getOrderTaskId(), ok.getId())).collect(Collectors.toList());
            if(!orderTaskMatchList.isEmpty()){
                res.setExt$Item("orderCategory",orderTaskMatchList.get(0).getOrderCategory());
            }
        });
        // 对于水的报告，如果结果为ND，则在报告中显示检出限+L
        taskResultList.stream() .filter(r -> StringUtils.contains(r.getExt$Item("ordercategory"), WATER_KEYWORD))
                .filter(r -> EmcConstant.ND.equals(r.getReportResult())).forEach(r -> r.setReportResult(r.getLowLimit() + "L"));

        this.isPh = isPh;
        this.tableIndex = tableIndex;
        this.dynamicTableIndex = dynamicTableIndex;
        this.decisionCategory = decisionCategory;
    }

    @Override
    public XWPFDocument getDocument() {
        return document;
    }

    @Override
    public String getTableTitle() {
        return String.format(PfConstant.QC_LDUP_RESULT_TABLE_TITLE, tableIndex);
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
    public String getReplaceKeyword() {
        return PfConstant.QC_RESULT_TABLE_REPLACE_KEYWORD;
    }

    @Override
    public int getTableWidth() {
        return 10500;
    }

    @Override
    public ParagraphAlignment getTitleAlignment() {
        return ParagraphAlignment.CENTER;
    }

    @Override
    public List<Integer> getColumnWidthList() {
        return WordTemplateUtils.cm2Ints(Arrays.asList(0D, 0D, 1.7D, 2D, 2.4D, 2.2D, 2.2D, 1.6D));
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        String[] titles = lookupTitles();
        List<List<Object>> headerDataList = buildTableDynamicHeader(titles, PfConstant.QC_LDUP_RESULT_TABLE_HEADER_COLUMN_QTY, null);
        setTableTitle(titles, PfConstant.QC_LDUP_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        List<EmcOrderBean> parentOrderList = orderList.stream().filter(order -> !StringUtils.equals(order.getQcCategoryCode(), PfConstant.QC_L_DUP)).sorted(Comparator.comparing(EmcOrderBean::getOrderNo)).collect(Collectors.toList());
        parentOrderList.forEach(order -> {
            List<EmcOrderTaskBean> curTaskList = orderTaskList.stream().filter(task -> ObjectUtils.equals(task.getOrderId(), order.getId())).collect(Collectors.toList());

            WordTemplateUtils.sortedByAnalysisOrg(curTaskList);
            curTaskList.forEach(task -> {
                List<EmcOrderTaskResultBean> parentResultList = parentTaskResultList.stream().filter(result -> ObjectUtils.equals(result.getOrderTaskId(), task.getId()) && StringUtils.equals(result.getReportDisplay(), Constant.YES)).collect(Collectors.toList());
                parentResultList.stream().filter(r -> StringUtils.isEmpty(r.getExt$Item("itemGroup"))).forEach(r -> r.setExt$Item("itemGroup", r.getExt$Item("testName")));
                parentResultList.sort(Comparator.comparing(r -> r.getExt$Item("itemGroup")));

                Map<String, List<EmcOrderTaskResultBean>> parentResultMap = parentResultList.stream().collect(Collectors.groupingBy(r -> r.getExt$Item("itemGroup")));

                EmcOrderTaskBean ldupTask = orderTaskList.stream().filter(curTask -> ObjectUtils.equals(curTask.getParentId(), task.getId())).findFirst().orElseGet(EmcOrderTaskBean::new);
                parentResultMap.forEach((itemGroup, curResultList) -> {
                    List<EmcOrderTaskResultBean> curParentResultList = curResultList.stream().filter(r -> StringUtils.equals(Constant.YES, r.getReportDisplay())).collect(Collectors.toList());
                    if (!curParentResultList.isEmpty()) {
                        EmcOrderTaskResultBean parentResult = curParentResultList.get(0);

                        EmcOrderTaskResultBean ldupResult = ldupTaskResultList.stream().filter(curResult -> ObjectUtils.equals(curResult.getOrderTaskId(), ldupTask.getId())
                                && StringUtils.equals(curResult.getReportDisplay(), Constant.YES)
                                && StringUtils.equals(curResult.getExt$Item("itemGroup"), itemGroup)).findFirst().orElse(null);

                        if (ldupResult == null) {
                            return;
                        }

                        List<Object> rowDataList = new ArrayList<>();
                        rowDataList.add(order.getOrderNo());
                        String replacedName = StringUtils.replace(itemGroup, PfConstant.SPECIAL_TEST_NAME, PfConstant.SPECIAL_REPLACED_TEST_NAME);
                        rowDataList.add(replacedName);

                        rowDataList.add(parentResult.getUnitName());
                        rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(parentResult.getReportResult()));

                        rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(ldupResult.getReportResult()));
                        boolean isNd = PfConstant.ND.equals(parentResult.getReportResult());

                        boolean isWaterNd = parentResult.getReportResult().contains("L");
                        rowDataList.add((isNd || isWaterNd) ? "0" : (isPh ? ldupResult.getRn8() : BeanUtils.getPropertyValue(ldupResult, decisionCategory)));

                        boolean hasIndexLimit = !StringUtils.isEmpty(ldupResult.getIndexLowLimit()) && !StringUtils.isEmpty(ldupResult.getIndexHighLimit());
                        rowDataList.add(hasIndexLimit ? (ldupResult.getIndexLowLimit() + "-" + ldupResult.getIndexHighLimit()) : null);

                        boolean oos = StringUtils.equals(ldupResult.getResultStatus(), "OOS");
                        rowDataList.add(oos ? PfConstant.QC_RESULT_INEFFECTIVE : PfConstant.QC_RESULT_EFFECTIVE);

                        tableBodyDataList.add(rowDataList);
                    }

                });
            });

        });

        return tableBodyDataList;
    }

    private String[] lookupTitles() {
        if (isPh) {
            return PfConstant.QC_LDUP_RESULT_TABLE_HEADER_TITLES_1;
        }

        switch (decisionCategory) {
            case EmcReportConstant
                    .RN2_DECISION_CATEGORY:
                return PfConstant.QC_LDUP_RESULT_TABLE_HEADER_TITLES_4;
            case EmcReportConstant
                    .RN14_DECISION_CATEGORY:
                return PfConstant.QC_LDUP_RESULT_TABLE_HEADER_TITLES_5;
            case EmcReportConstant
                    .RN7_DECISION_CATEGORY:
            default:
                return PfConstant.QC_LDUP_RESULT_TABLE_HEADER_TITLES_3;
        }
    }

}
