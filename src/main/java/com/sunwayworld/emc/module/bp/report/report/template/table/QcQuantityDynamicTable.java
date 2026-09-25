package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;

/**
 * @author zhengxy@sunwayworld.com 2021年08月05日
 */
public class QcQuantityDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private EmcProjectBean project;
    private List<EmcOrderBean> orderList;
    private List<EmcOrderTaskBean> orderTaskList;
    private List<EmcOrderTaskBean> qcOrderTaskList;
    private List<EmcOrderTaskResultBean> qcTaskResultList;
    private List<CoreCodeBean> coreCodeList;

    public QcQuantityDynamicTable(XWPFDocument document, EmcProjectBean project, List<EmcOrderBean> orderList, List<EmcOrderTaskBean> orderTaskList, List<EmcOrderTaskBean> qcOrderTaskList, List<EmcOrderTaskResultBean> qcTaskResultList, List<CoreCodeBean> coreCodeList) {
        this.document = document;
        this.project = project;
        this.orderList = orderList;
        this.coreCodeList = coreCodeList;
        this.orderTaskList = orderTaskList;
        this.qcOrderTaskList = qcOrderTaskList;
        this.qcTaskResultList = qcTaskResultList;
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
        return 10204;
    }

    @Override
    public String getReplaceKeyword() {
        return PfConstant.QC_QUANTITY_TABLE_REPLACE_KEYWORD;
    }

    @Override
    public int getDynamicTableIndex() {
        return 3;
    }

    @Override
    public ParagraphAlignment getTitleAlignment() {
        return ParagraphAlignment.CENTER;
    }

    @Override
    public List<Integer> getColumnWidthList() {
        return WordTemplateUtils.cm2Ints(Arrays.asList(2.1D, 0D, 0.97D, 1.38D, 0.97D, 1.38D, 0.97D, 1.38D, 0.97D, 1.38D, 0.97D, 1.38D, 0.97D, 1.38D));
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        // 合并列配置
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 2, 3));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 4, 5));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 6, 7));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 8, 9));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 10, 11));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 12, 13));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 14, 15));
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        // 合并行配置
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, 0, 1));
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(1, 0, 1));
        dynamicTableDTO.setMergedRowList(mergedRowList);

        List<List<Object>> headerDataList = buildTableDynamicHeader(PfConstant.QC_QUANTITY_TABLE_HEADER_TITLES, PfConstant.QC_QUANTITY__TABLE_HEADER_COLUMN_QTY, null);
        setTableTitle(PfConstant.QC_QUANTITY_TABLE_HEADER_TITLES, PfConstant.QC_QUANTITY__TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    public List<List<Object>> buildTableBody0(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        AtomicInteger mergedRowIndex = new AtomicInteger(1);
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);

        List<String> qcCodeList = Arrays.asList(PfConstant.QCCATEGORY_QCCATEGORYID_P_BLANK, PfConstant.QC_P_YSKB, PfConstant.QCCATEGORY_QCCATEGORYID_L_BLANK, PfConstant.QC_P_DUP, EmcConstant.QC_L_DUP, PfConstant.QCCATEGORY_QCCATEGORYID_QC_STD, EmcConstant.QCCATEGORY_QCCATGORYID_L_SPKC);

        List<EmcOrderTaskBean> validOrderTaskList = qcOrderTaskList.stream().filter(r -> !StringUtils.contains(r.getTestName(), EmcConstant.PARAMETER_TEST_MATCH_KEYWORD)
                && !StringUtils.contains(r.getTestName(), EmcConstant.DRY_MATTER_TEST_MATCH_KEYWORD)
                && !StringUtils.contains(r.getTestName(), PfConstant.WATER_CONTENT_TEST_MATCH_KEYWORD)
                && !StringUtils.contains(r.getTestName(), PfConstant.WATER_CONTENT_TEST_MATCH_KEYWORD_2)
                && !StringUtils.contains(r.getExt$Item("relatedTest"), Constant.YES)
                && !StringUtils.equals(r.getExt$Item("other"), Constant.YES)
                && !EmcConstant.N_A.equals(r.getQcCategoryCode())).collect(Collectors.toList());

        List<Long> validOrderTaskIdList = validOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> validTaskResultList = qcTaskResultList.stream().filter(r -> validOrderTaskIdList.contains(r.getOrderTaskId())).collect(Collectors.toList());

        Map<String, Map<String, Pair<String, String>>> totalQualityMap = calcQcWithResult(validTaskResultList, qcCodeList);

        Map<String, List<EmcOrderTaskBean>> orderTaskMap = validOrderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getOrderCategory));
        orderTaskMap.forEach((orderCategory, curOrderTaskList) -> {
            // 按分析科室进行排序展示
            Set<String> uniqueTestIdSet = new HashSet<>();
            curOrderTaskList.stream().filter(task -> StringUtils.isEmpty(task.getAnalysisOrgId())).forEach(task -> task.setAnalysisOrgId(""));
            curOrderTaskList.sort(Comparator.comparing(EmcOrderTaskBean::getAnalysisOrgId));
            int startRowIndex = mergedRowIndex.get() + 1;

            WordTemplateUtils.sortedByAnalysisOrg(curOrderTaskList);

            Map<String, List<EmcOrderTaskBean>> orgOrderTaskMap = curOrderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getAnalysisOrgId, LinkedHashMap::new, Collectors.toList()));
            orgOrderTaskMap.forEach((analysisOrgId, orgTaskList) -> {
                List<Long> orgTaskIdList = orgTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
                List<EmcOrderTaskResultBean> orgTaskResultList = validTaskResultList.stream().filter(r -> orgTaskIdList.contains(r.getOrderTaskId())).collect(Collectors.toList());
                Map<String, List<EmcOrderTaskResultBean>> itemGroupResultMap = orgTaskResultList.stream().collect(Collectors.groupingBy(r -> r.getExt$Item("itemGroup")));
                itemGroupResultMap.forEach((itemGroup, itemGroupResultList) -> {
                    String key = itemGroupResultList.get(0).getTestMethodId() + "$" + itemGroup;
                    if (uniqueTestIdSet.contains(key)) {
                        return;
                    }

                    uniqueTestIdSet.add(key);

                    Map<String, Pair<String, String>> quantityMap = Optional.ofNullable(totalQualityMap.get(key)).orElseGet(HashMap::new);

                    List<Object> rowDataList = new ArrayList<>();
                    rowDataList.add(orderCategory);

                    rowDataList.add(itemGroup);
                    qcCodeList.forEach(qcCode -> {
                        Pair<String, String> quantityPair = quantityMap.get(qcCode);
                        rowDataList.add(Objects.isNull(quantityPair) ? null : quantityPair.getFirst());
                        rowDataList.add(Objects.isNull(quantityPair) ? null : quantityPair.getSecond());
                    });
                    tableBodyDataList.add(rowDataList);
                    mergedRowIndex.incrementAndGet();
                });
            });

            int endRowIndex = mergedRowIndex.get();
            mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, startRowIndex, endRowIndex));
        });

        dynamicTableDTO.setMergedRowList(mergedRowList);

        return tableBodyDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        List<String> qcCodeList = Arrays.asList(PfConstant.QCCATEGORY_QCCATEGORYID_P_BLANK, PfConstant.QC_P_YSKB, PfConstant.QCCATEGORY_QCCATEGORYID_L_BLANK, PfConstant.QC_P_DUP, EmcConstant.QC_L_DUP, PfConstant.QCCATEGORY_QCCATEGORYID_QC_STD, EmcConstant.QCCATEGORY_QCCATGORYID_L_SPKC);

        List<EmcOrderTaskBean> validOrderTaskList = qcOrderTaskList.stream().filter(r -> !StringUtils.contains(r.getTestName(), EmcConstant.PARAMETER_TEST_MATCH_KEYWORD)
                && !StringUtils.contains(r.getTestName(), EmcConstant.DRY_MATTER_TEST_MATCH_KEYWORD)
                && !StringUtils.contains(r.getTestName(), PfConstant.WATER_CONTENT_TEST_MATCH_KEYWORD)
                && !StringUtils.contains(r.getTestName(), PfConstant.WATER_CONTENT_TEST_MATCH_KEYWORD_2)
                && !StringUtils.contains(r.getExt$Item("relatedTest"), Constant.YES)
                && !StringUtils.equals(r.getExt$Item("other"), Constant.YES)
                && !EmcConstant.N_A.equals(r.getQcCategoryCode())).collect(Collectors.toList());

        List<Long> validOrderTaskIdList = validOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());

        Map<String, String> testItemSortCodeMap = coreCodeList.stream().filter(code -> StringUtils.equals(code.getCodeCategoryId(), "testitemsort")).collect(Collectors.toMap(CoreCodeBean::getCode, CoreCodeBean::getCodeName, (v1, v2) -> v1));

        Map<Long, Map<String, Pair<String, String>>> totalQualityMap = calcQc(validOrderTaskList, qcCodeList);

        Map<String, List<EmcOrderTaskBean>> orderTaskMap = validOrderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getOrderCategory));
        orderTaskMap.forEach((orderCategory, curOrderTaskList) -> {
            // 按分析科室进行排序展示
            Set<String> uniqueTestIdSet = new HashSet<>();
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

                // 未设置检测项目分类，依然单独显示
                uniqueTaskList.forEach(curTask -> {
                    String key = curTask.getTestId() + "$" + curTask.getTestMethodId();
                    if (uniqueTestIdSet.contains(key)) {
                        return;
                    }

                    uniqueTestIdSet.add(key);

                    Map<String, Pair<String, String>> quantityMap = Optional.ofNullable(totalQualityMap.get(curTask.getTestMethodId())).orElseGet(HashMap::new);

                    List<Object> rowDataList = new ArrayList<>();
                    rowDataList.add(orderCategory);

                    //rowDataList.add(Optional.ofNullable(curTask.getExt$Item("itemGroup")).orElse(curTask.getTestName()));
                    rowDataList.add(curTask.getTestName());
                    qcCodeList.forEach(qcCode -> {
                        Pair<String, String> quantityPair = quantityMap.get(qcCode);
                        rowDataList.add(quantityPair.getFirst());
                        rowDataList.add(quantityPair.getSecond());
                    });
                    tableBodyDataList.add(rowDataList);

                });

                // 设置了检测项目分类的，合并显示
                /*mergedTaskMap.forEach((groupKey, curTaskList) -> {
                    if (uniqueTestIdSet.contains(groupKey)) {
                        return;
                    }

                    uniqueTestIdSet.add(groupKey);

                    // 用于计算质控样
                    List<EmcOrderTaskBean> calcTaskList = orgTaskList.stream().filter(task -> StringUtils.equals(task.getExt$Item("testItemSort"), groupKey)).collect(Collectors.toList());

                    Map<String, Pair<String, String>> quantityMap = Optional.ofNullable(totalQualityMap.get(calcTaskList.get(0).getTestMethodId())).orElseGet(HashMap::new);

                    List<Object> rowDataList = new ArrayList<>();
                    rowDataList.add(orderCategory);

                    rowDataList.add(testItemSortCodeMap.get(groupKey));
                    qcCodeList.forEach(qcCode -> {
                        Pair<String, String> quantityPair = quantityMap.get(qcCode);
                        rowDataList.add(quantityPair.getFirst());
                        rowDataList.add(quantityPair.getSecond());
                    });
                    tableBodyDataList.add(rowDataList);
                });*/
            });

            curOrderTaskList.stream().filter(task -> StringUtils.isEmpty(task.getExt$Item("itemGroup"))).forEach(task -> task.setExt$Item("itemGroup", task.getTestName()));
            ArrayList<EmcOrderTaskBean> uniqueTaskList = curOrderTaskList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(task -> task.getExt$Item("itemGroup")))), ArrayList::new));
        });

        return tableBodyDataList;
    }

    private Map<Long, Map<String, Pair<String, String>>> calcQc(List<EmcOrderTaskBean> qcTaskList, List<String> qcCodeList) {
        long taskCount = qcTaskList.stream().map(EmcOrderTaskBean::getTestMethodId).distinct().count();
        Map<Long, Map<String, Pair<String, String>>> quantityMap = new HashMap<>((int) taskCount);

        List<String> samplingQcCodeList = Arrays.asList(PfConstant.QCCATEGORY_QCCATEGORYID_P_BLANK, PfConstant.QC_P_YSKB, PfConstant.QC_P_DUP);

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(1);
        numberFormat.setMaximumFractionDigits(1);

        Map<Long, List<EmcOrderTaskBean>> normalTaskMap = orderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getTestMethodId));

        Map<Long, List<EmcOrderTaskBean>> orderTaskMap = qcTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getTestMethodId));
        orderTaskMap.forEach((testMethodId, curTaskList) -> {
            List<EmcOrderTaskBean> curNormalTaskList = Optional.ofNullable(normalTaskMap.get(testMethodId)).orElseGet(ArrayList::new);
            List<Long> curOrderIdList = curNormalTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
            List<EmcOrderBean> curOrderList = orderList.stream().filter(order -> curOrderIdList.contains(order.getId())).distinct().collect(Collectors.toList());
            List<EmcOrderBean> curNaOrderList = curOrderList.stream().filter(order -> StringUtils.equals(PfConstant.N_A, order.getQcCategoryCode())).distinct().collect(Collectors.toList());
            // 现场平行
            List<EmcOrderBean> curPdupOrderBeforeList = curOrderList.stream().filter(order -> StringUtils.equals(PfConstant.QC_P_DUP, order.getQcCategoryCode())).distinct().collect(Collectors.toList());
            List<EmcOrderBean>  curPdupOrderList = new ArrayList<>();
            curPdupOrderBeforeList.forEach(o ->{
                List<Long> curPdupOrderIdList = curPdupOrderList.stream().map(EmcOrderBean :: getId).distinct().collect(Collectors.toList());
                if(!curPdupOrderIdList.contains(o.getId())){
                    curPdupOrderList.add(o);
                }
            });
            int curNaOrderCount = curNaOrderList.size();
            int curPdupOrderCount = curPdupOrderList.size();
            int curTotalOrderCount = curNaOrderCount + curPdupOrderCount;

            Map<String, Pair<String, String>> curCountMap = new HashMap<>();
            qcCodeList.forEach(qcCode -> {
                List<String> matchCodeList = new ArrayList<>();
                matchCodeList.add(qcCode);

                // 如果是加标类型，则还需要统计空白加标的
                if (EmcConstant.QCCATEGORY_QCCATGORYID_L_SPKC.equals(qcCode)) {
                    matchCodeList.add(PfConstant.QC_L_SPKCK);
                }

                // 判断是否为现场QC
                boolean isSamplingQc = samplingQcCodeList.contains(qcCode);
                int curQcCount = (int) curTaskList.stream().filter(task -> matchCodeList.contains(task.getQcCategoryCode())).map(EmcOrderTaskBean::getOrderId).distinct().count();
                if (isSamplingQc) {
                    curQcCount = (int) curTaskList.stream().filter(task -> matchCodeList.contains(task.getQcCategoryCode()) && ObjectUtils.equals(task.getProjId(), project.getId())).map(EmcOrderTaskBean::getOrderId).distinct().count();
                }

                int curTotalCount = isSamplingQc ? curNaOrderCount : curTotalOrderCount;
                String percentage = (curQcCount == 0 || curTotalCount == 0) ? "/" : numberFormat.format((double) curQcCount / (double) curTotalCount  * 100);
                Pair<String, String> quantityPair = Pair.of((curQcCount == 0) ? "/" : String.valueOf(curQcCount), percentage);
                curCountMap.put(qcCode, quantityPair);
            });

            quantityMap.put(testMethodId, curCountMap);
        });

        return quantityMap;
    }

    private Map<String, Map<String, Pair<String, String>>> calcQcWithResult(List<EmcOrderTaskResultBean> qcTaskResultList, List<String> qcCodeList) {
        long taskCount = qcTaskResultList.stream().map(EmcOrderTaskResultBean::getTestMethodId).distinct().count();
        Map<String, Map<String, Pair<String, String>>> quantityMap = new HashMap<>((int) taskCount);

        List<String> samplingQcCodeList = Arrays.asList(PfConstant.QCCATEGORY_QCCATEGORYID_P_BLANK, PfConstant.QC_P_YSKB, PfConstant.QC_P_DUP);

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(1);
        numberFormat.setMaximumFractionDigits(1);

        Map<Long, List<EmcOrderTaskBean>> normalTaskMap = orderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getTestMethodId));

        Map<String, List<EmcOrderTaskResultBean>> orderTaskResultMap = qcTaskResultList.stream().collect(Collectors.groupingBy(r -> r.getExt$Item("itemGroup")));
        orderTaskResultMap.forEach((itemGroup, curTaskResultList) -> {
            Long testMethodId = curTaskResultList.get(0).getTestMethodId();
            List<EmcOrderTaskBean> curNormalTaskList = Optional.ofNullable(normalTaskMap.get(testMethodId)).orElseGet(ArrayList::new);
            List<Long> curOrderIdList = curNormalTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
            List<EmcOrderBean> curOrderList = orderList.stream().filter(order -> curOrderIdList.contains(order.getId())).distinct().collect(Collectors.toList());
            List<EmcOrderBean> curNaOrderList = curOrderList.stream().filter(order -> StringUtils.equals(PfConstant.N_A, order.getQcCategoryCode())).distinct().collect(Collectors.toList());
            // 现场平行
            List<EmcOrderBean> curPdupOrderBeforeList = curOrderList.stream().filter(order -> StringUtils.equals(PfConstant.QC_P_DUP, order.getQcCategoryCode())).distinct().collect(Collectors.toList());
            List<EmcOrderBean>  curPdupOrderList = new ArrayList<>();
            curPdupOrderBeforeList.forEach(o ->{
                List<Long> curPdupOrderIdList = curPdupOrderList.stream().map(EmcOrderBean :: getId).distinct().collect(Collectors.toList());
                if(!curPdupOrderIdList.contains(o.getId())){
                    curPdupOrderList.add(o);
                }
            });
            int curNaOrderCount = curNaOrderList.size();
            int curPdupOrderCount = curPdupOrderList.size();
            int curTotalOrderCount = curNaOrderCount + curPdupOrderCount;

            Map<String, Pair<String, String>> curCountMap = new HashMap<>();
            qcCodeList.forEach(qcCode -> {
                // 判断是否为现场QC
                boolean isSamplingQc = samplingQcCodeList.contains(qcCode);
                int curQcCount = (int) curTaskResultList.stream().filter(task -> StringUtils.equals(task.getQcCategoryCode(), qcCode)).map(EmcOrderTaskResultBean::getOrderId).distinct().count();
                if (isSamplingQc) {
                    curQcCount = (int) curTaskResultList.stream().filter(task -> StringUtils.equals(task.getQcCategoryCode(), qcCode) && ObjectUtils.equals(task.getProjId(), project.getId())).map(EmcOrderTaskResultBean::getOrderId).distinct().count();
                }

                int curTotalCount = isSamplingQc ? curNaOrderCount : curTotalOrderCount;
                String percentage = (curQcCount == 0 || curTotalCount == 0) ? "/" : numberFormat.format((double) curQcCount / (double) curTotalCount  * 100);
                Pair<String, String> quantityPair = Pair.of((curQcCount == 0) ? "/" : String.valueOf(curQcCount), percentage);
                curCountMap.put(qcCode, quantityPair);
            });

            quantityMap.put(testMethodId + "$" + itemGroup, curCountMap);
        });

        return quantityMap;
    }

}
