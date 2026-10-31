package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhengxy@sunwayworld.com 2021年08月05日
 * 检测方法及仪器设备
 */
public class QcMethodDeviceDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private List<EmcOrderTaskBean> orderTaskList;
    private List<EmcReportResultBean> reportResultList;
    private List<EmcEquiptBean> equiptList;
    private int tableIndex;

    public QcMethodDeviceDynamicTable(XWPFDocument document, List<EmcOrderTaskBean> orderTaskList, List<EmcReportResultBean> reportResultList, List<EmcEquiptBean> equiptList, int tableIndex) {
        this.document = document;
        this.orderTaskList = orderTaskList;
        this.reportResultList = reportResultList;
        this.equiptList = equiptList;
        this.tableIndex = tableIndex;
    }

    @Override
    public XWPFDocument getDocument() {
        return document;
    }

    @Override
    public String getTableTitle() {
        return String.format(PfConstant.QC_METHOD_DEVICE_TABLE_TITLE, tableIndex, orderTaskList.get(0).getOrderCategory());
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
    public int getTableWidth() {
        return 10500;
    }

    @Override
    public int getDynamicTableIndex() {
        return 3;
    }

    @Override
    public String getReplaceKeyword() {
        return PfConstant.QC_METHOD_DEVICE_TABLE_REPLACE_KEYWORD;
    }

    @Override
    public ParagraphAlignment getTitleAlignment() {
        return ParagraphAlignment.CENTER;
    }

    @Override
    public List<Integer> getColumnWidthList() {
        return WordTemplateUtils.cm2Ints(Arrays.asList(2.6D, 0D, 1.6D, 1.6D, 0D, 3.0D));
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> headerDataList = buildTableDynamicHeader(PfConstant.QC_METHOD_DEVICE_TABLE_HEADER_TITLES, PfConstant.QC_METHOD_DEVICE_TABLE_HEADER_COLUMN_QTY, null);
        setTableTitle(PfConstant.QC_METHOD_DEVICE_TABLE_HEADER_TITLES, PfConstant.QC_METHOD_DEVICE_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();
        Map<Long, String> sampleEquiptMap = equiptList.stream().collect(Collectors.toMap(EmcEquiptBean::getId, equipt -> equipt.getEquiptName() + "-" + equipt.getSpecification(), (k1, k2) -> k1));
        Map<Long, EmcEquiptBean> equiptMap = equiptList.stream().collect(Collectors.toMap(EmcEquiptBean::getId, Function.identity(), (k1, k2) -> k1));

        List<EmcOrderTaskBean> validOrderTaskList = orderTaskList.stream().filter(r -> !StringUtils.contains(r.getTestName(), EmcConstant.PARAMETER_TEST_MATCH_KEYWORD)
                && !StringUtils.contains(r.getTestName(), EmcConstant.DRY_MATTER_TEST_MATCH_KEYWORD)
                && !StringUtils.contains(r.getTestName(), PfConstant.WATER_CONTENT_TEST_MATCH_KEYWORD)
                && !StringUtils.contains(r.getTestName(), PfConstant.WATER_CONTENT_TEST_MATCH_KEYWORD_2)
                && !StringUtils.contains(r.getExt$Item("relatedTest"), Constant.YES)
                && !StringUtils.equals(r.getExt$Item("other"), Constant.YES)
                && EmcConstant.N_A.equals(r.getQcCategoryCode())).collect(Collectors.toList());

        Map<String, List<EmcOrderTaskBean>> testOrderTaskMap = validOrderTaskList.stream().collect(Collectors.groupingBy(task -> task.getTestId() + "$" + task.getTestMethodId()));

        // 根据测试ID + 方法ID去重（一个任务中同一测试可能有多个方法），生成检测项目表数据
        ArrayList<EmcOrderTaskBean> uniqueTaskList = validOrderTaskList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(task -> task.getTestId() + "$" + task.getTestMethodId() + "$" + task.getExt$Item("itemGroup")))), ArrayList::new));

        WordTemplateUtils.sortedByAnalysisOrg(uniqueTaskList);

        uniqueTaskList.forEach(t -> {
            List<Object> rowDataList = new ArrayList<>();
            String itemGroup = Optional.ofNullable(t.getExt$Item("itemGroup")).orElse(t.getTestName());
            String testName = StringUtils.replace(itemGroup, PfConstant.SPECIAL_TEST_NAME, PfConstant.SPECIAL_REPLACED_TEST_NAME);
            rowDataList.add(testName);
            rowDataList.add(t.getTestMethodBasis());

            EmcReportResultBean reportResult = reportResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), itemGroup) && ObjectUtils.equals(t.getTestMethodId(), r.getTestMethodId())
                    && !StringUtils.isEmpty(r.getLowLimit())).findFirst().orElse(null);
            if (reportResult == null) {
                reportResult = reportResultList.stream().filter(r -> ObjectUtils.equals(t.getTestName(), r.getTestName()) && ObjectUtils.equals(t.getTestMethodId(), r.getTestMethodId())
                        && !(StringUtils.isEmpty(r.getExt$Item("limitunitname")) && StringUtils.isEmpty(r.getUnitName()))).findFirst().orElseGet(EmcReportResultBean::new);
            }
            rowDataList.add(Optional.ofNullable(reportResult.getLowLimit()).orElse("-"));
            rowDataList.add(Optional.ofNullable(reportResult.getExt$Item("limitUnitName")).orElse(reportResult.getUnitName()));

            String key = t.getTestId() + "$" + t.getTestMethodId();
            String analysisDevice = getAnalysisDevice(testOrderTaskMap.get(key), sampleEquiptMap);
            rowDataList.add(StringUtils.contains(analysisDevice, EmcConstant.N_A) ? null : analysisDevice);

            EmcEquiptBean equipt = Optional.ofNullable(equiptMap.get(t.getEquiptId())).orElseGet(EmcEquiptBean::new);
            rowDataList.add(equipt.getEquiptId());

            tableBodyDataList.add(rowDataList);
        });

        return tableBodyDataList;
    }

    //分析科室字段按照现场室、理化组、金属组、有机组排序
    private List<EmcOrderTaskBean> sortByAnalysisOrgId(ArrayList<EmcOrderTaskBean> orderTaskList) {
        List<String> orgNameList = Arrays.asList("现场室", "理化组", "金属组", "有机组");
        List<EmcOrderTaskBean> sortedTaskList = new ArrayList<>();
        for (String orgName : orgNameList) {
            sortedTaskList.addAll(orderTaskList.stream().filter(task -> orgName.equals(task.getAnalysisOrgName())).collect(Collectors.toList()));
        }
        sortedTaskList.addAll(orderTaskList.stream().filter(task -> !orgNameList.contains(task.getAnalysisOrgName())).collect(Collectors.toList()));
        return sortedTaskList;
    }

    private String getAnalysisDevice(List<EmcOrderTaskBean> orderTaskList, Map<Long, String> sampleEquiptMap) {
        EmcOrderTaskBean orderTask = orderTaskList.stream().filter(task -> StringUtils.equals(task.getAnalysisType(), EmcBizConstant.ANALYSISCATEGORY_ANALYSIS)).findFirst().orElseGet(EmcOrderTaskBean::new);
        if(ObjectUtils.isEmpty(orderTask.getId())||orderTask.getId()==null){
        	orderTask = orderTaskList.get(0);
        }
        return Optional.ofNullable(sampleEquiptMap.get(orderTask.getEquiptId())).orElse(null);
    }

}
