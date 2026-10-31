package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhengxy@sunwayworld.com 2022年01月24日
 */
public class SimpleMonitorItemDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private List<EmcOrderBean> orderList;
    private List<EmcOrderTaskBean> orderTaskList;
    private int tableIndex;
    private boolean sampling;

    public SimpleMonitorItemDynamicTable(XWPFDocument document, EmcProjectBean project, List<EmcOrderBean> orderList, List<EmcOrderTaskBean> orderTaskList, int tableIndex) {
        this.document = document;
        this.orderList = orderList;
        this.orderTaskList = orderTaskList;
        this.tableIndex = tableIndex;
        this.sampling = Constant.YES.equals(project.getSample());
    }

    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }

    @Override
    public String getTableTitle() {
        return String.format(PfConstant.SIMPLE_MONITOR_ITEM_TABLE_TITLE, tableIndex);
    }

    @Override
    public int getTableIndex() {
        return 2;
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
        return Arrays.asList(1531, 0);
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> headerDataList = buildTableDynamicHeader(PfConstant.SIMPLE_MONITOR_ITEM_TABLE_HEADER_TITLES, PfConstant.SIMPLE_MONITOR_ITEM_TABLE_HEADER_COLUMN_QTY, null);
        setTableTitle(PfConstant.SIMPLE_MONITOR_ITEM_TABLE_HEADER_TITLES, PfConstant.SIMPLE_MONITOR_ITEM_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        // 去除包含参数的测试
        List<EmcOrderTaskBean> validOrderTaskList = orderTaskList.stream().filter(r -> !StringUtils.contains(r.getTestName(), EmcConstant.PARAMETER_TEST_MATCH_KEYWORD)
                /*&& !StringUtils.contains(r.getTestName(), EmcConstant.DRY_MATTER_TEST_MATCH_KEYWORD)
                && !StringUtils.contains(r.getTestName(), PfConstant.WATER_CONTENT_TEST_MATCH_KEYWORD)*/
                && !StringUtils.contains(r.getTestName(), PfConstant.WATER_CONTENT_TEST_MATCH_KEYWORD_2)
                && !StringUtils.contains(r.getExt$Item("relatedTest"), Constant.YES)
                && EmcConstant.N_A.equals(r.getQcCategoryCode())).collect(Collectors.toList());

        // 按样品类型进行分组
        Map<String, List<EmcOrderTaskBean>> orderTaskMap = validOrderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getOrderCategory));
        Map<String, LocalDate> sortedOrderCategoryMap = sortByDate(orderList);

        List<List<Object>> tableBodyDataList = new ArrayList<>(sortedOrderCategoryMap.size());

        sortedOrderCategoryMap.forEach((orderCategory, minDate) -> {
            List<Object> rowDataList = new ArrayList<>();
            rowDataList.add(orderCategory);

            List<EmcOrderTaskBean> curTaskList = orderTaskMap.get(orderCategory);
            if(ObjectUtils.isEmpty(curTaskList)){
                return;
            }
            if(curTaskList ==null|| curTaskList.isEmpty()){
                return;
            }
            ArrayList<EmcOrderTaskBean> uniqueTaskList = curTaskList.stream().collect(Collectors.collectingAndThen
            		(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(task -> task.getTestId() + "$" + task.getTestMethodId()+ "$" + task.getExt$Item("itemGroup")))), ArrayList::new));
            uniqueTaskList.stream().filter(task -> StringUtils.isEmpty(task.getAnalysisOrgId())).forEach(task -> task.setAnalysisOrgId(""));

            WordTemplateUtils.sortedByAnalysisOrg(uniqueTaskList);

            LinkedHashMap<String, List<EmcOrderTaskBean>> curTaskMap = uniqueTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getTestName, LinkedHashMap::new, Collectors.toList()));
            List<String> replacedTestNameList = new ArrayList<>();
            curTaskMap.forEach((testName, taskList) -> {
                List<String> itemGroupList = taskList.stream().filter(task -> !StringUtils.isEmpty(task.getExt$Item("itemGroup"))).map(task -> task.getExt$Item("itemGroup")).distinct().collect(Collectors.toList());
                boolean showTestName = itemGroupList.size() != 1;
                String showName = showTestName ? StringUtils.join(itemGroupList, "、") : itemGroupList.get(0);
                String replacedName = StringUtils.replace(showName, PfConstant.SPECIAL_TEST_NAME, PfConstant.SPECIAL_REPLACED_TEST_NAME);
                replacedTestNameList.add(replacedName);
            });

            rowDataList.add(String.join("、", replacedTestNameList));

            tableBodyDataList.add(rowDataList);
        });

        return tableBodyDataList;
    }

    @Override
    public WordTemplateDynamicTableDTO buildTable() {
        WordTemplateDynamicTableDTO dynamicTableDTO = new WordTemplateDynamicTableDTO();

        dynamicTableDTO.setFontStyle("Times New Roman");
        List<List<Object>> tableDataList = new ArrayList<>();
        tableDataList.addAll(buildTableHeader(dynamicTableDTO));
        tableDataList.addAll(buildTableBody(dynamicTableDTO));
        WordTemplateUtils.listListNullToString(tableDataList, PfConstant.NULL_REPLACED_VALUE);

        dynamicTableDTO.setTableTitle(getTableTitle());
        dynamicTableDTO.setTableDataList(tableDataList);
        dynamicTableDTO.setReplaceKeyword(PfConstant.MONITOR_TABLE_PLACEHOLDER);

        return dynamicTableDTO;
    }

    private Map<String, LocalDate> sortByDate(List<EmcOrderBean> orderList) {
        Map<String, List<EmcOrderBean>> orderCategoryMap = orderList.stream().collect(Collectors.groupingBy(EmcOrderBean::getOrderCategory));
        Map<String, LocalDate> minDateMap = new HashMap<>(orderCategoryMap.size());
        orderCategoryMap.forEach((orderCategory, curOrderList) -> {
            if (sampling) {
                EmcOrderBean minDateOrder = curOrderList.stream().filter(order -> !ObjectUtils.isEmpty(order.getSampleBeginDate())).min(Comparator.comparing(EmcOrderBean::getSampleBeginDate)).orElseGet(EmcOrderBean::new);
                minDateMap.put(orderCategory, Optional.ofNullable(minDateOrder.getSampleBeginDate()).orElseGet(LocalDate::now));
                return;
            }

            EmcOrderBean minDateOrder = curOrderList.stream().filter(order -> !ObjectUtils.isEmpty(order.getSendDate())).min(Comparator.comparing(EmcOrderBean::getSendDate)).orElseGet(EmcOrderBean::new);
            minDateMap.put(orderCategory, Optional.ofNullable(minDateOrder.getSendDate()).orElseGet(LocalDate::now));
        });

        return minDateMap.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, newVal) -> oldVal, LinkedHashMap::new));
    }
}
