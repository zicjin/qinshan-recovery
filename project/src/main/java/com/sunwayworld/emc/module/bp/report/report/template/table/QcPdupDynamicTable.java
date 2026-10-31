package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.sunwayworld.emc.module.bp.bp.computing.EmcOrderTaskResultComputingEngine;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemRevisionBean;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 密码（现场）平行样控制结果
 * @author zhengxy@sunwayworld.com 2021年08月06日
 */
public class QcPdupDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private List<EmcOrderBean> orderList;
    private List<EmcOrderTaskBean> orderTaskList;
    private List<EmcOrderTaskResultBean> orderTaskResultList;
    private boolean isSiteSurvey;
    private List<EmcIndexSetBean> indexSetFinalBeans;
    private boolean isPh;
    private int tableIndex;
    private int dynamicTableIndex;
    private static final String WATER_KEYWORD = "水";

    public QcPdupDynamicTable(XWPFDocument document, List<EmcOrderBean> orderList, List<EmcOrderTaskBean> orderTaskList, List<EmcOrderTaskResultBean> orderTaskResultList,boolean isSiteSurvey, List<EmcIndexSetBean> indexSetFinalBeans, boolean isPh, int tableIndex, int dynamicTableIndex) {
        this.document = document;
        this.orderList = orderList;
        this.orderTaskList = orderTaskList;
        this.orderTaskResultList = orderTaskResultList;
        this.isSiteSurvey = isSiteSurvey;
        this.indexSetFinalBeans = indexSetFinalBeans;
        this.isPh = isPh;
        this.tableIndex = tableIndex;
        this.dynamicTableIndex = dynamicTableIndex;
    }

    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }

    @Override
    public String getTableTitle() {
        return String.format(PfConstant.QC_PDUP_RESULT_TABLE_TITLE, tableIndex);
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
        return WordTemplateUtils.cm2Ints(Arrays.asList(3.2D, 1.6D, 0D, 0D, 2.2D, 2.2D, 2.2D));
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<String> dynamicTitleList = new ArrayList<>();
        orderList.forEach(order -> dynamicTitleList.add(order.getOrderNo()));

        // 合并列配置
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 0, 1));
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        // 合并行配置
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(4, 0, 1));
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(5, 0, 1));
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(6, 0, 1));
        dynamicTableDTO.setMergedRowList(mergedRowList);

        List<List<Object>> headerDataList = buildTableDynamicHeader(isPh ? PfConstant.QC_PDUP_RESULT_TABLE_HEADER_TITLES_1 : PfConstant.QC_PDUP_RESULT_TABLE_HEADER_TITLES, PfConstant.QC_PDUP_RESULT_TABLE_HEADER_COLUMN_QTY, dynamicTitleList);
        setTableTitle(isPh ? PfConstant.QC_PDUP_RESULT_TABLE_HEADER_TITLES_1 : PfConstant.QC_PDUP_RESULT_TABLE_HEADER_TITLES, PfConstant.QC_PDUP_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        if (orderList.size() < 2) {
            return tableBodyDataList;
        }

        List<EmcOrderTaskBean> validOrderTaskList = orderTaskList.stream().filter(r -> !StringUtils.contains(r.getTestName(), EmcConstant.PARAMETER_TEST_MATCH_KEYWORD)
                && !StringUtils.contains(r.getTestName(), EmcConstant.DRY_MATTER_TEST_MATCH_KEYWORD)
                && !StringUtils.contains(r.getTestName(), PfConstant.WATER_CONTENT_TEST_MATCH_KEYWORD)
                && !StringUtils.contains(r.getTestName(), PfConstant.WATER_CONTENT_TEST_MATCH_KEYWORD_2)
                && !StringUtils.contains(r.getExt$Item("relatedTest"), Constant.YES)
                && !EmcConstant.N_A.equals(r.getQcCategoryCode())).collect(Collectors.toList());

        List<EmcOrderTaskResultBean> validResultList = orderTaskResultList.stream().filter(result -> Constant.YES.equals(result.getReportDisplay())).collect(Collectors.toList());
        validResultList.forEach(res ->{
            List<EmcOrderTaskBean> validOrderTaskNewList  = orderTaskList.stream().filter(r ->ObjectUtils.equals(res.getOrderTaskId(), r.getId())).collect(Collectors.toList());
            res.setExt$Item("orderCategory",validOrderTaskNewList.get(0).getOrderCategory());
        });

        // 对于水的报告，如果结果为ND或者/，分析项为样品浓度,则在报告中显示检出限+L，实际参与均值计算时，取1/2检出限计算
        List<EmcOrderTaskResultBean> validResultNdList = validResultList.stream() .filter(r -> StringUtils.contains(r.getExt$Item("ordercategory"), WATER_KEYWORD))
                .filter(r ->r.getItemName().contains(PfConstant.QC_BLANK_RESULT_REPORT_DISPLAY))
                .filter(r -> EmcConstant.ND.equals(r.getReportResult())).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> validResultNullList = validResultList.stream() .filter(r -> StringUtils.contains(r.getExt$Item("ordercategory"), WATER_KEYWORD))
                .filter(r ->r.getItemName().contains(PfConstant.QC_BLANK_RESULT_REPORT_DISPLAY))
                .filter(r -> "/".equals(r.getReportResult())).collect(Collectors.toList());
        validResultList.stream() .filter(r -> StringUtils.contains(r.getExt$Item("ordercategory"), WATER_KEYWORD))
                .filter(r ->r.getItemName().contains(PfConstant.QC_BLANK_RESULT_REPORT_DISPLAY))
                .filter(r -> EmcConstant.ND.equals(r.getReportResult())).forEach(r -> r.setReportResult(r.getLowLimit() + "L"));
        validResultList.stream() .filter(r -> StringUtils.contains(r.getExt$Item("ordercategory"), WATER_KEYWORD))
                .filter(r ->r.getItemName().contains(PfConstant.QC_BLANK_RESULT_REPORT_DISPLAY))
                .filter(r -> "/".equals(r.getReportResult())).forEach(r -> r.setReportResult(r.getLowLimit() + "L"));

        validResultList.stream().filter(r -> StringUtils.isEmpty(r.getExt$Item("itemGroup"))).forEach(r -> r.setExt$Item("itemGroup", r.getExt$Item("testName")));

        WordTemplateUtils.sortedByAnalysisOrg(validOrderTaskList);

        validOrderTaskList.forEach(task -> {
            List<EmcOrderTaskResultBean> taskResultList = validResultList.stream().filter(r -> ObjectUtils.equals(task.getTestMethodId(), r.getTestMethodId())).sorted(Comparator.comparing(r -> r.getExt$Item("itemGroup"))).collect(Collectors.toList());
            Map<String, List<EmcOrderTaskResultBean>> taskResultMap = taskResultList.stream().collect(Collectors.groupingBy(r -> r.getExt$Item("itemGroup"), LinkedHashMap::new, Collectors.toList()));

            taskResultMap.forEach((itemGroup, curResultList) -> {
                List<Object> rowDataList = new ArrayList<>();
                String showItemGroup = StringUtils.replace(itemGroup, PfConstant.SPECIAL_TEST_NAME, PfConstant.SPECIAL_REPLACED_TEST_NAME);
                rowDataList.add(showItemGroup);

                EmcOrderBean parentOrder = orderList.get(0);
                EmcOrderBean pdupOrder = orderList.get(1);
                String orderCategory = pdupOrder.getOrderCategory();

                EmcOrderTaskResultBean parentResult = curResultList.stream().filter(result -> StringUtils.equals(result.getOrderNo(), parentOrder.getOrderNo())).findFirst().orElseGet(EmcOrderTaskResultBean::new);
                EmcOrderTaskResultBean pdupResult = curResultList.stream().filter(result -> StringUtils.equals(result.getOrderNo(), pdupOrder.getOrderNo())).findFirst().orElseGet(EmcOrderTaskResultBean::new);

                rowDataList.add(pdupResult.getUnitName());

                rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(parentResult.getReportResult()));

                rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(pdupResult.getReportResult()));

                boolean isNd = PfConstant.ND.equals(parentResult.getReportResult());
                boolean isPh = StringUtils.containsIgnoreCase(task.getTestName(), PfConstant.QC_PDUP_PH_KEYWORD);
                boolean isWaterNd = parentResult.getReportResult().contains("L");
                //对应的指标分析项数据
                List<EmcIndexSetBean> indexSetFinalMatchBeans = new ArrayList<>();
                Boolean isShowDev =false;
                if(isSiteSurvey){
                    List<EmcIndexSetBean> indexSetMatchBeans = indexSetFinalBeans.stream()
                            .filter(is ->StringUtils.equals(pdupResult.getTestId().toString(),is.getExt$Item("testId")))
                            .filter(is ->StringUtils.equals(pdupResult.getItemId().toString(),is.getExt$Item("itemId"))).collect(Collectors.toList());
                    if(!indexSetMatchBeans.isEmpty()){
                        //管制值，筛选值,进行二次判断
                        if(orderCategory.contains(PfConstant.DIOXIN_SOIL_KEYWORDS[0])){
                            indexSetFinalMatchBeans = indexSetMatchBeans.stream().filter(is ->StringUtils.equals(PfConstant.SOIL_CONTROL_VALUE_INDEX1,is.getExt$Item("index1"))).collect(Collectors.toList());
                            if(!indexSetFinalMatchBeans.isEmpty()) {
                                List<EmcIndexSetBean> indexSetFilterBeans = indexSetFinalMatchBeans.stream().filter(is ->StringUtils.equals(is.getExt$Item("index2"),PfConstant.SOIL_FILTER_VALUE_INDEX2)).collect(Collectors.toList());
                                List<EmcIndexSetBean> indexSetControlBeans = indexSetFinalMatchBeans.stream().filter(is ->StringUtils.equals(is.getExt$Item("index2"),PfConstant.SOIL_CONTROL_VALUE_INDEX2)).collect(Collectors.toList());
                                if(!indexSetFilterBeans.isEmpty()&&!indexSetControlBeans.isEmpty()){
                                    String filterValue = indexSetFilterBeans.get(0).getIndexHighLimit();
                                    String controlValue = indexSetFinalMatchBeans.stream().filter(is ->StringUtils.equals(is.getExt$Item("index2"),PfConstant.SOIL_CONTROL_VALUE_INDEX2)).collect(Collectors.toList()).get(0).getIndexHighLimit();
                                    //取到管制值和筛选值，进行逻辑判断，判断是否需要计算相对偏差
                                    isShowDev = isShowRelateDev(filterValue,controlValue,parentResult,pdupResult);
                                }

                            }
                        }
                        //水的判断方式不一样
                        if(orderCategory.contains(PfConstant.DIOXIN_WATER_KEYWORDS[0])){
                            indexSetFinalMatchBeans = indexSetMatchBeans.stream().filter(is ->StringUtils.equals(PfConstant.WATER_LIMIT_VALUE_INDEX1,is.getExt$Item("index1"))).collect(Collectors.toList());
                            if(!indexSetFinalMatchBeans.isEmpty()){
                                List<EmcIndexSetBean> indexSetStdBeans = indexSetFinalMatchBeans.stream().filter(is ->StringUtils.equals(is.getExt$Item("index2"),PfConstant.SOIL_FILTER_VALUE_INDEX2)).collect(Collectors.toList());
                                if(!indexSetStdBeans.isEmpty()){
                                    String stdValue = indexSetStdBeans.get(0).getIndexHighLimit();
                                    isShowDev = isWaterShowRelateDev(stdValue,parentResult,pdupResult);
                                }

                            }
                        }

                    }
                }

                if(isWaterNd ||isShowDev){
                    rowDataList.add("*");
                }else{
                    rowDataList.add(isNd ? "*" : (isPh ? pdupResult.getRn8() : pdupResult.getRn7()));
                }

                boolean hasIndexLimit = !StringUtils.isEmpty(pdupResult.getIndexLowLimit()) && !StringUtils.isEmpty(pdupResult.getIndexHighLimit());
                rowDataList.add(hasIndexLimit ? (pdupResult.getIndexLowLimit() + "-" + pdupResult.getIndexHighLimit()) : null);

                rowDataList.add(PfConstant.QC_RESULT_EFFECTIVE);
                tableBodyDataList.add(rowDataList);
            });

        });

        return tableBodyDataList;
    }

    //土壤判断是否显示相对偏差
    private boolean isShowRelateDev(String filterValue,String controlValue,EmcOrderTaskResultBean parentResult,EmcOrderTaskResultBean ldupResult) {
        String parentReportResult = parentResult.getReportResult();
        String ldupReportResult = parentResult.getReportResult();

        if(NumberUtils.isNumber(parentReportResult)&&NumberUtils.isNumber(ldupReportResult)&&NumberUtils.isNumber(filterValue)&&NumberUtils.isNumber(controlValue)){
            Double filterValueD = Double.parseDouble(filterValue);
            Double controlValueD = Double.parseDouble(filterValue);
            Double parentReportResultD = Double.parseDouble(parentReportResult);
            Double ldupReportResultD = Double.parseDouble(ldupReportResult);
            //进行情况判断6种情况,3种情况不计算
            if(parentReportResultD <= filterValueD && ldupReportResultD<= filterValueD){
                return false;
            }
            if(parentReportResultD >= filterValueD &&parentReportResultD <=controlValueD &&ldupReportResultD >= filterValueD &&ldupReportResultD <=controlValueD){
                return false;
            }
            if(parentReportResultD > controlValueD && ldupReportResultD > controlValueD){
                return false;
            }
        }
        return true;

    }

    //土壤判断是否显示相对偏差
    private boolean isWaterShowRelateDev(String stdValue,EmcOrderTaskResultBean parentResult,EmcOrderTaskResultBean ldupResult) {
        String parentReportResult = parentResult.getReportResult();
        String ldupReportResult = parentResult.getReportResult();

        if(NumberUtils.isNumber(parentReportResult)&&NumberUtils.isNumber(ldupReportResult)&&NumberUtils.isNumber(stdValue)){
            Double stdValueD = Double.parseDouble(stdValue);
            Double parentReportResultD = Double.parseDouble(parentReportResult);
            Double ldupReportResultD = Double.parseDouble(ldupReportResult);
            if(parentReportResultD<=stdValueD &&ldupReportResultD <=stdValueD){
                return false;
            }
            if(parentReportResultD>stdValueD && ldupReportResultD>stdValueD){
                return false;
            }
        }
        return true;

    }

}
