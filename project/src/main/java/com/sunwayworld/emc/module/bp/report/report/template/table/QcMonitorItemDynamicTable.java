package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;

/**
 * 质控报告-检测项目
 * @author zhengxy@sunwayworld.com 2021年08月05日
 */
public class QcMonitorItemDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private List<EmcOrderTaskBean> orderTaskList;
    private List<CoreCodeBean> coreCodeList;

    public QcMonitorItemDynamicTable(XWPFDocument document, List<EmcOrderTaskBean> orderTaskList, List<CoreCodeBean> coreCodeList) {
        this.document = document;
        this.orderTaskList = orderTaskList;
        this.coreCodeList = coreCodeList;
    }

    @Override
    public XWPFDocument getDocument() {
        return document;
    }

    @Override
    public String getTableTitle() {
        return null;
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
    public int getTableWidth() {
        return 10500;
    }

    @Override
    public int getDynamicTableIndex() {
        return 1;
    }

    @Override
    public String getReplaceKeyword() {
        return PfConstant.QC_MONITOR_ITEM_TABLE_REPLACE_KEYWORD;
    }

    @Override
    public ParagraphAlignment getTitleAlignment() {
        return ParagraphAlignment.CENTER;
    }

    @Override
    public List<Integer> getColumnWidthList() {
        return WordTemplateUtils.cm2Ints(Arrays.asList(1.8D, 3.4D, 0D));
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> headerDataList = buildTableDynamicHeader(PfConstant.QC_MONITOR_ITEM_TABLE_HEADER_TITLES, PfConstant.QC_MONITOR_ITEM__TABLE_HEADER_COLUMN_QTY, null);
        setTableTitle(PfConstant.QC_MONITOR_ITEM_TABLE_HEADER_TITLES, PfConstant.QC_MONITOR_ITEM__TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        List<EmcOrderTaskBean> validOrderTaskList = orderTaskList.stream().filter(r -> !StringUtils.contains(r.getTestName(), EmcConstant.PARAMETER_TEST_MATCH_KEYWORD)
                && !StringUtils.contains(r.getTestName(), EmcConstant.DRY_MATTER_TEST_MATCH_KEYWORD)
                && !StringUtils.contains(r.getTestName(), PfConstant.WATER_CONTENT_TEST_MATCH_KEYWORD)
                && !StringUtils.contains(r.getTestName(), PfConstant.WATER_CONTENT_TEST_MATCH_KEYWORD_2)
                && !StringUtils.contains(r.getExt$Item("relatedTest"), Constant.YES)
                && !StringUtils.equals(r.getExt$Item("other"), Constant.YES)
                && EmcConstant.N_A.equals(r.getQcCategoryCode())).collect(Collectors.toList());

        Map<String, String> testItemSortCodeMap = coreCodeList.stream().filter(code -> StringUtils.equals(code.getCodeCategoryId(), "testitemsort")).collect(Collectors.toMap(CoreCodeBean::getCode, CoreCodeBean::getCodeName, (v1, v2) -> v1));

        // 按样品类型进行分组
        AtomicInteger rowIndex = new AtomicInteger();
        validOrderTaskList.sort(Comparator.comparing(EmcOrderTaskBean::getOrderCategory));
        Map<String, List<EmcOrderTaskBean>> orderTaskMap = validOrderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getOrderCategory, LinkedHashMap::new, Collectors.toList()));
        orderTaskMap.forEach((orderCategory, curOrderTaskList) -> {
            List<String> testNameList = new ArrayList<>();

            // 按分析科室进行排序展示
            curOrderTaskList.stream().filter(task -> StringUtils.isEmpty(task.getAnalysisOrgId())).forEach(task -> task.setAnalysisOrgId(""));
            curOrderTaskList.sort(Comparator.comparing(EmcOrderTaskBean::getAnalysisOrgId));

            WordTemplateUtils.sortedByAnalysisOrg(curOrderTaskList);

            Map<String, List<EmcOrderTaskBean>> orgOrderTaskMap = curOrderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getAnalysisOrgId, LinkedHashMap::new, Collectors.toList()));
            orgOrderTaskMap.forEach((analysisOrgId, orgTaskList) -> {
                // 根据测试ID + 方法ID去重（一个任务中同一测试可能有多个方法），生成检测项目表数据
                orgTaskList.stream().filter(task -> StringUtils.isEmpty(task.getExt$Item("itemGroup"))).forEach(task -> task.setExt$Item("itemGroup", task.getTestName()));
                ArrayList<EmcOrderTaskBean> uniqueTaskList = orgTaskList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(task -> task.getExt$Item("itemGroup")))), ArrayList::new));
                List<EmcOrderTaskBean> testItemSortTaskList = uniqueTaskList.stream().filter(task -> !StringUtils.isEmpty(task.getExt$Item("testItemSort"))).collect(Collectors.toList());
                List<Long> testItemSortTaskIdList = testItemSortTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
                Map<String, List<EmcOrderTaskBean>> mergedTaskMap = testItemSortTaskList.stream().collect(Collectors.groupingBy(task -> task.getExt$Item("testItemSort")));

                // 未设置检测项目分类的测试
                List<EmcOrderTaskBean> otherTaskList = uniqueTaskList.stream().filter(task -> !testItemSortTaskIdList.contains(task.getId())).collect(Collectors.toList());

                otherTaskList.forEach(task -> {
                    String itemGroup = Optional.ofNullable(task.getExt$Item("itemGroup")).orElse(task.getTestName());
                    itemGroup = StringUtils.replace(itemGroup, PfConstant.SPECIAL_TEST_NAME, PfConstant.SPECIAL_REPLACED_TEST_NAME);
                    testNameList.add(itemGroup);
                });

                mergedTaskMap.forEach((sortCode, taskList) -> Optional.ofNullable(testItemSortCodeMap.get(sortCode)).ifPresent(testNameList::add));
            });

            //当检测项目汇总表中既有半挥发性有机物又有苯胺的时候，不显示苯胺
            if (testNameList.contains("半挥发性有机物") && testNameList.contains("苯胺")) {
                for (int i = 0; i < testNameList.size(); i++) {
                    if ("苯胺".equals(testNameList.get(i))) {
                        testNameList.remove(i);
                        break;
                    }
                }
            }

            List<Object> rowDataList = new ArrayList<>();
            rowDataList.add(rowIndex.incrementAndGet());
            rowDataList.add(orderCategory);
            rowDataList.add(String.join("、", testNameList));
            tableBodyDataList.add(rowDataList);
        });

        return tableBodyDataList;
    }
}
