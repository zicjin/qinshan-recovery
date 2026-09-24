package com.sunwayworld.emc.module.bp.report.report.template;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.word.PfWord2007Utils;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateBasicDTO;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateSourceDTO;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.result.temphum.bean.PfRunTempHumidityBean;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityMainBean;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.exception.NotImplementedException;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;

/**
 * @author zhengxy@sunwayworld.com 2020年09月07日
 */
public class BaseWordTemplate implements WordTemplate {

    /** 最多展示2个采样或分析人员名字 */
    private static final int MAX_USER_QTY = 2;
    private static final String DEFAULT_DELIMITER = "、";
    private static final String DEFAULT_SAMPLE_POSITION = "%s等%d个点位";
    private static final String HIDDEN_VALUE = "否";

    private XWPFDocument document;
    private EmcProjectBean project;
    private List<EmcOrderBean> orderList;
    private List<EmcOrderTaskBean> orderTaskList;
    private List<EmcRunBean> runList;
    private List<EmcFolderBean> folderList;
    private List<EmcSampleBean> sampleList;
    private CoreOrgBean coreOrg;
    private CrmOpportunityMainBean opportunityMain;
    private boolean sampling;
    private List<PfRunTempHumidityBean> tempHumidityList;
    private List<EmcMonitorTmplBean> monitorTmplList;
    private EmcReportBean report;
    private List<CoreFileBean> coreFileList;

    public BaseWordTemplate(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO) {
        this.document = document;
        this.project = templateSourceDTO.getProject();
        this.orderList = templateSourceDTO.getOrderList();
        this.orderTaskList = templateSourceDTO.getOrderTaskList();
        this.runList = templateSourceDTO.getRunList();
        this.folderList = templateSourceDTO.getFolderList();
        this.sampleList = templateSourceDTO.getSampleList();
        this.coreOrg = templateSourceDTO.getCoreOrg();
        this.opportunityMain = Optional.ofNullable(templateSourceDTO.getOpportunityMain()).orElseGet(CrmOpportunityMainBean::new);
        this.sampling = Constant.YES.equals(templateSourceDTO.getProject().getSample());
        this.tempHumidityList = templateSourceDTO.getTempHumidityList();
        this.monitorTmplList = templateSourceDTO.getMonitorTmplList();
        this.report = templateSourceDTO.getReport();
        this.coreFileList = templateSourceDTO.getCoreFileList();
    }

    @Override
    public String getTemplateName() {
        throw new NotImplementedException();
    }

    @Override
    public WordTemplateBasicDTO buildBasicInformation() {
        WordTemplateBasicDTO templateBasicDTO = new WordTemplateBasicDTO();
        LocalDateTime localDate = LocalDateTime.now();
        templateBasicDTO.setReportNo(report.getReportNo());
        templateBasicDTO.setYear(localDate.getYear() + "");
        templateBasicDTO.setMonth(localDate.getMonthValue() + "");
        templateBasicDTO.setDay(localDate.getDayOfMonth() + "");
        templateBasicDTO.setRegisteDate(DateTimeUtils.formatLocalDateTime(project.getRegistDate(), "yyyy-MM"));

        templateBasicDTO.setCompanyName(coreOrg.getOrgName());
        templateBasicDTO.setCompanyEnName(coreOrg.getExt$Item("orgEnglishName"));
        templateBasicDTO.setCompanyAddress(coreOrg.getExt$Item("orgAddress"));
        templateBasicDTO.setCompanyPhone(coreOrg.getExt$Item("orgReservedPhone"));
        templateBasicDTO.setCompanyComplaintPhone(coreOrg.getExt$Item("orgComplaintsHotline"));
        templateBasicDTO.setCompanyPostcode(coreOrg.getExt$Item("orgPostcode"));
        templateBasicDTO.setCompanyEmail(coreOrg.getExt$Item("orgEmail"));

        boolean sampling = Constant.YES.equals(project.getSample());
        boolean hiddenEntrustOrg = HIDDEN_VALUE.equals(opportunityMain.getAuthorizationUnitShow());

        templateBasicDTO.setDetectCategory(PfConstant.DETECT_CATEGORY);
        templateBasicDTO.setEntrustOrgName(project.getEntrustOrgName());
        templateBasicDTO.setEntrustOrgAddress(project.getEntrustOrgAddress());
        templateBasicDTO.setEntrustOrgDesc(hiddenEntrustOrg ? "" : String.format(PfConstant.DEFAULT_ENTRUST_ORG_DESC, project.getEntrustOrgName()));
        // 取报告联系人和联系电话
        templateBasicDTO.setEntrustOrgContact(opportunityMain.getReportContact());
        templateBasicDTO.setEntrustOrgPhone(opportunityMain.getReportPhone());
        templateBasicDTO.setTestOrgName(project.getTestOrgName());
        templateBasicDTO.setTestOrgAddress(project.getTestOrgAddress());
        templateBasicDTO.setTestOrgPhone(project.getTestOrgPhone());
        templateBasicDTO.setProjectName(project.getProjName());
        templateBasicDTO.setProjectRemark(project.getRemark());
        templateBasicDTO.setProjectAddress(project.getExt$Item("samplingLocation"));
        templateBasicDTO.setProjectInfo(Optional.ofNullable(project.getProjName()).orElse(project.getExt$Item("samplingLocation")));
        templateBasicDTO.setOrderSource(sampling ? EmcConstant.SAMPLING : EmcConstant.DELIVERING);
        templateBasicDTO.setOrderStatus(EmcConstant.DEFAULT_ORDER_DESCRIPTION);
        templateBasicDTO.setSampleUser(getSampleUser(project, folderList));
        templateBasicDTO.setAnalysisUser(getAnalysisUser(runList));
        templateBasicDTO.setSamplePosition(getSamplePosition(project, folderList, orderList));
        templateBasicDTO.setTestOrgDesc((StringUtils.isEmpty(project.getTestOrgName()) || EmcConstant.NULL_REPLACED_VALUE.equals(project.getTestOrgName()))
                ? "" : String.format(EmcConstant.TEST_ORG_NAME_DESC, project.getTestOrgName()));
        templateBasicDTO.setEntrustType(sampling ? PfConstant.SAMPLING_ENTRUST_TYPE : PfConstant.NO_SAMPLING_ENTRUST_TYPE);
        templateBasicDTO.setUserTitle(sampling ? PfConstant.SAMPLING_USER_TITLE : PfConstant.NO_SAMPLING_USER_TITLE);
        templateBasicDTO.setDateTitle(sampling ? PfConstant.SAMPLING_DATE : PfConstant.ACCEPTED_DATE);

        templateBasicDTO.setReportEndRemark(sampling ? "" : PfConstant.MONITOR_REPORT_RECORD_REMARK);
        // 采样日期
        templateBasicDTO.setSampleDate(getSampleDate(project, orderList));

        // 检测周期，如果需要进行实验室分析，则取样品接收时间-分析结束时间，如果只有现场的，则取采样日期
        templateBasicDTO.setAnalysisDate(getAnalysisDate());

        // 检测结果基本描述信息
        List<String> resultList = new ArrayList<>();
        AtomicInteger tableIndex = new AtomicInteger();
        Map<String, LocalDate> minDateMap = sortByDate(orderList, true);
        minDateMap.forEach((oc, minDate) -> resultList.add(WordTemplateUtils.handleOrderCategory(oc) + PfConstant.DEFAULT_RESULT + String.format(PfConstant.DEFAULT_TABLE_MESSAGE, tableIndex.incrementAndGet())));
        String resultDesc = String.join("、", resultList);

        templateBasicDTO.setTestResult(resultDesc);

        // 样品类型
        String simpleOrderCategory = String.join("、", WordTemplateUtils.handleOrderCategory(new ArrayList<>(minDateMap.keySet())));
        templateBasicDTO.setSimpleOrderCategory(simpleOrderCategory);
        templateBasicDTO.setOrderCategory(String.join("、", minDateMap.keySet()));

        templateBasicDTO.setTestBasis(String.format(PfConstant.DEFAULT_TABLE_MESSAGE, getTestBasicTableIndex() + 1));

        // 检测目的
        //templateBasicDTO.setDetectPurpose(lookupDetectPurpose(project, opportunityMain, templateBasicDTO.getOrderCategory()));
        templateBasicDTO.setDetectPurpose(project.getDetectPurpose());

        // 去除包含参数的测试
        List<EmcOrderTaskBean> validOrderTaskList = orderTaskList.stream().filter(r -> !StringUtils.contains(r.getTestName(), EmcConstant.PARAMETER_TEST_MATCH_KEYWORD)
                && !StringUtils.contains(r.getTestName(), EmcConstant.DRY_MATTER_TEST_MATCH_KEYWORD)
                && !StringUtils.contains(r.getTestName(), PfConstant.WATER_CONTENT_TEST_MATCH_KEYWORD)
                && !StringUtils.contains(r.getTestName(), PfConstant.WATER_CONTENT_TEST_MATCH_KEYWORD_2)
                && !StringUtils.contains(r.getExt$Item("relatedTest"), Constant.YES)
                && EmcConstant.N_A.equals(r.getQcCategoryCode())).collect(Collectors.toList());

        WordTemplateUtils.sortedByAnalysisOrg(validOrderTaskList);
        String testMethodBasis = validOrderTaskList.stream().map(EmcOrderTaskBean::getTestMethodBasis).distinct().collect(Collectors.joining("、"));
        templateBasicDTO.setTestNames(joinTestName(validOrderTaskList));
        templateBasicDTO.setTestMethodBasis(testMethodBasis);

        //样品类型组为“水(含大气降水)和废水”的符号备注和别的不一样
        templateBasicDTO.setSymbolRemark(monitorTmplList.stream().anyMatch(t -> "1".equals(t.getMonitorTmplGroupId())) ? PfConstant.QC_SYMBOL_REMARK : PfConstant.QC_SYMBOL_REMARK_2);//样品类型组为“水(含大气降水)和废水”的符号备注和别的不一样
        //采样和送样的"环境条件控制"不一样
        templateBasicDTO.setEnvironmentalConditions(sampling ? PfConstant.QC_ENVIRONMENTAL_CONDITIONS_FOR_SAMPLING : PfConstant.QC_ENVIRONMENTAL_CONDITIONS_FOR_NO_SAMPLING);

        //受控编号
        templateBasicDTO.setControlNumber(coreOrg.getExt$Item("controlNumber"));
        templateBasicDTO.setQcControlNumber(coreOrg.getExt$Item("qcControlNumber"));

        templateBasicDTO.setReportName(report.getReportName());

        templateBasicDTO.setReportDate(DateTimeUtils.formatLocalDateTime(LocalDateTime.now(), "yyyy年MM月dd日"));

        return templateBasicDTO;
    }

    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }

    @Override
    public void createMonitorItemTable() {

    }

    @Override
    public void createResultTable() {
        throw new NotImplementedException();
    }

    @Override
    public void generate() {
        setBasicInformation(getDocument(), buildBasicInformation());
        createMonitorItemTable();
        createResultTable();
        clean();
        format();

        // 隐藏不显示的表格行
        //hiddenTableRow(opportunityMain);
        // 插入二维码图片
        insertQrCode();
    }

    /**
     * 通过样品类型对点位进行分组
     * @param monitorTmplList
     * @param folderList
     * @return
     */
    protected Map<String, List<EmcFolderBean>> groupingFolderByOrderCategory(List<EmcMonitorTmplBean> monitorTmplList, List<EmcFolderBean> folderList) {
        List<String> monitorTmplIdList = monitorTmplList.stream().map(EmcMonitorTmplBean::getId).collect(Collectors.toList());
        List<EmcFolderBean> filterFolderList = folderList.stream().filter(f -> monitorTmplIdList.contains(f.getMonitorTmplId())).collect(Collectors.toList());
        // 根据样品类型对点位进行分组

        return filterFolderList.stream().collect(Collectors.groupingBy(EmcFolderBean::getOrderCategory));
    }

    protected int getTotalResultTableCount() {
        return orderList.stream().collect(Collectors.groupingBy(EmcOrderBean::getOrderCategory)).size();
    }

    protected int getTestBasicTableIndex() {
        return getTotalResultTableCount() + 1;
    }

    /**
     * 根据商机合同配置隐藏不显示的信息行
     * @param opportunityMain 商机信息
     */
    protected void hiddenTableRow(CrmOpportunityMainBean opportunityMain) {
        List<XWPFTable> tables = document.getTables();
        if (tables.isEmpty()) {
            return;
        }
        if(StringUtils.isEmpty(opportunityMain.getDrawArea())){
        	opportunityMain.setDrawAreaShow(HIDDEN_VALUE);
        }
        // 移除首页表格隐藏的信息行，委托单位、受检单位、项目名称分别对应首页表格的第1行、第2行和第3行，行下标为0、1、2
        removeTableRow(tables.get(0), Arrays.asList(opportunityMain.getAuthorizationUnitShow(), opportunityMain.getInspectedUnitShow(), opportunityMain.getProjectNameShow()), Arrays.asList(0, 1, 2), true);

        // 移除基本信息表格隐藏的信息行
        removeTableRow(tables.get(1), Arrays.asList(opportunityMain.getAuthorizationUnitShow(), opportunityMain.getEntrustededUnitAddShow(),
                opportunityMain.getInspectedUnitShow(), opportunityMain.getInspectedAddShow(), opportunityMain.getProjectNameShow(),
                opportunityMain.getDrawAreaShow()), Arrays.asList(0, 1, 2, 3, 4, 5), false);
    }

    protected void removeTableRow(XWPFTable targetTable, List<String> displayValues, List<Integer> rowIndexList, boolean removeBorder) {
        if (displayValues.isEmpty() || rowIndexList.isEmpty() || (displayValues.size() != rowIndexList.size())) {
            return;
        }

        List<Integer> removedRowIndexList = new ArrayList<>();
        IntStream.range(0, displayValues.size()).forEach(index -> {
            if (HIDDEN_VALUE.equals(displayValues.get(index))) {
                removedRowIndexList.add(rowIndexList.get(index));
            }
        });

        if (!removedRowIndexList.isEmpty()) {
            int rowSize = targetTable.getRows().size();
            removedRowIndexList.stream().filter(rowIndex -> rowIndex < rowSize).sorted(Comparator.reverseOrder()).forEach(rowIndex -> {
                if (removeBorder) {
                    targetTable.getRow(rowIndex).getTableCells().forEach(cell -> cell.getCTTc().getTcPr().addNewTcBorders().addNewBottom().setVal(STBorder.NIL));
                    // 对于类似首页表格只有下边框的行，删除行时下框线并不会被删除，因此需要通过同时删除下一行的上框线来实现
                    if (rowIndex + 1 < rowSize) {
                        targetTable.getRow(rowIndex + 1).getTableCells().forEach(cell -> cell.getCTTc().getTcPr().addNewTcBorders().addNewTop().setVal(STBorder.NIL));
                    }
                }

                targetTable.removeRow(rowIndex);
            });
        }
    }

    protected String getAnalysisDate() {
        /*List<LocalDate> acceptedDateList = sampleList.stream().filter(sample -> !ObjectUtils.isEmpty(sample.getAcceptedDate())).map(EmcSampleBean::getAcceptedDate).distinct().map(LocalDateTime::toLocalDate).collect(Collectors.toList());
        List<LocalDate> sampleBeginDateList = orderList.stream().filter(order -> !ObjectUtils.isEmpty(order.getSampleBeginDate())).map(EmcOrderBean::getSampleBeginDate).distinct().collect(Collectors.toList());
        List<LocalDate> startDateList = acceptedDateList.isEmpty() ? sampleBeginDateList : acceptedDateList;
        List<LocalDate> endDateList = runList.stream().filter(run -> !ObjectUtils.isEmpty(run.getAuditedDate())).map(EmcRunBean::getAuditedDate).map(LocalDateTime::toLocalDate).distinct().collect(Collectors.toList());
        List<LocalDate> humidityEndDateList = tempHumidityList.stream().filter(h -> !ObjectUtils.isEmpty(h.getEndDate())).map(PfRunTempHumidityBean::getEndDate).distinct().collect(Collectors.toList());
        List<LocalDate> oneHumidityEndDateList = tempHumidityList.stream().filter(h -> !ObjectUtils.isEmpty(h.getEndDateOne())).map(PfRunTempHumidityBean::getEndDateOne).distinct().collect(Collectors.toList());
        List<LocalDate> twoHumidityEndDateList = tempHumidityList.stream().filter(h -> !ObjectUtils.isEmpty(h.getEndDateTwo())).map(PfRunTempHumidityBean::getEndDateTwo).distinct().collect(Collectors.toList());
        endDateList.addAll(humidityEndDateList);
        endDateList.addAll(oneHumidityEndDateList);
        endDateList.addAll(twoHumidityEndDateList);*/

        List<LocalDate> startDateList = runList.stream().filter(run -> !ObjectUtils.isEmpty(run.getAnalysisBeginDate())).map(EmcRunBean::getAnalysisBeginDate).distinct().collect(Collectors.toList());
        List<LocalDate> acceptedDateList = sampleList.stream().filter(sample -> !ObjectUtils.isEmpty(sample.getAcceptedDate())).map(EmcSampleBean::getAcceptedDate).distinct().map(LocalDateTime::toLocalDate).collect(Collectors.toList());
        startDateList.addAll(acceptedDateList);

        List<LocalDate> endDateList = runList.stream().filter(run -> !ObjectUtils.isEmpty(run.getAnalysisEndDate())).map(EmcRunBean::getAnalysisEndDate).distinct().collect(Collectors.toList());

        return WordTemplateUtils.getDate(startDateList, endDateList);
    }

    private String getSampleUser(EmcProjectBean project, List<EmcFolderBean> folderList) {
        if (Constant.YES.equals(project.getSample())) {
            List<String> sceneUserList = folderList.stream().filter(folder -> !StringUtils.isEmpty(folder.getSceneUserName())).map(EmcFolderBean::getSceneUserName).distinct().collect(Collectors.toList());
            List<String> sampleUserList = folderList.stream().filter(folder -> !StringUtils.isEmpty(folder.getSampleUserName())).map(EmcFolderBean::getSampleUserName).distinct().collect(Collectors.toList());
            List<String> allSampleUserList = new ArrayList<>(sceneUserList);
            sampleUserList.forEach(sampleUser -> allSampleUserList.addAll(Arrays.asList(StringUtils.split(sampleUser, ","))));
            List<String> uniqueSampleUserList = CollectionUtils.distinct(allSampleUserList);

            return String.join(DEFAULT_DELIMITER, uniqueSampleUserList);
        }

        return getAcceptedUser(sampleList);
    }

    private String getAcceptedUser(List<EmcSampleBean> sampleList) {
        return sampleList.stream().filter(sample -> !StringUtils.isEmpty(sample.getAccepteUserName()))
                .map(EmcSampleBean::getAccepteUserName).distinct().collect(Collectors.joining(DEFAULT_DELIMITER));
    }

    private String getAnalysisUser(List<EmcRunBean> runList) {
        List<String> analysisUserList = runList.stream().filter(run -> !StringUtils.isEmpty(run.getAnalysisUserName())).map(EmcRunBean::getAnalysisUserName).distinct().collect(Collectors.toList());

        return getLimitValue(analysisUserList, MAX_USER_QTY);
    }

    private String getSamplePosition(EmcProjectBean project, List<EmcFolderBean> folderList, List<EmcOrderBean> orderList) {
        // 采样任务，直接取监测点作为采样地点
        if (Constant.YES.equals(project.getSample())) {
            List<String> monitorPointNameList = folderList.stream().map(EmcFolderBean::getMonitorPointName).distinct().collect(Collectors.toList());
            String limitValue = getLimitValue(monitorPointNameList, MAX_USER_QTY);
            if (monitorPointNameList.size() > MAX_USER_QTY) {
                return String.format(DEFAULT_SAMPLE_POSITION, limitValue, monitorPointNameList.size());
            }
            return limitValue;
        }

        List<String> sampleAddressList = orderList.stream().map(EmcOrderBean::getSampleAddress).distinct().filter(address -> !StringUtils.isEmpty(address)).collect(Collectors.toList());
        String limitValue = getLimitValue(sampleAddressList, MAX_USER_QTY);
        if (sampleAddressList.size() > MAX_USER_QTY) {
            return String.format(DEFAULT_SAMPLE_POSITION, limitValue, sampleAddressList.size());
        }

        return limitValue;
    }

    private String getLimitValue(List<String> valueList, int maxQty) {
        if (valueList.isEmpty()) {
            return null;
        }

        if (valueList.size() <= maxQty) {
            return String.join(DEFAULT_DELIMITER, valueList);
        }

        return String.join(DEFAULT_DELIMITER, valueList.subList(0, maxQty));
    }

    private String getSampleDate(EmcProjectBean project, List<EmcOrderBean> orderList) {
        // 采样任务直接取采样日期
        if (Constant.YES.equals(project.getSample())) {
            List<LocalDate> sampleBeginDateList = orderList.stream().map(EmcOrderBean::getSampleBeginDate).distinct().collect(Collectors.toList());
            // 含噪声的样品类型，取采样开始-采样完成日期
            if (orderList.stream().anyMatch(order -> StringUtils.contains(order.getOrderCategory(), "噪声"))) {
                List<LocalDate> sampleCompleteDateList = orderList.stream().map(EmcOrderBean::getSampleCompleteDate).distinct().collect(Collectors.toList());
                return WordTemplateUtils.getDate(sampleBeginDateList, sampleCompleteDateList);
            }
            return WordTemplateUtils.getDate(sampleBeginDateList, true);
        }

        // 送样任务取接样日期
        return WordTemplateUtils.getDate(orderList.stream().filter(order -> !ObjectUtils.isEmpty(order.getAcceptedDate())).map(EmcOrderBean::getAcceptedDate).map(LocalDateTime::toLocalDate).distinct().collect(Collectors.toList()), true);
    }

    /**
     * 按采样日期或送样日期对样品类型进行升序排序
     * @param orderList 样品列表
     * @return 样品类型集合
     */
    protected Map<String, LocalDate> sortByDate(List<EmcOrderBean> orderList) {
        return sortByDate(orderList, true);
    }

    /**
     * 按采样日期或送样日期对样品类型进行排序
     * @param orderList 样品列表
     * @param asc 是否升序
     * @return 样品类型集合
     */
    protected Map<String, LocalDate> sortByDate(List<EmcOrderBean> orderList, boolean asc) {
        Map<String, List<EmcOrderBean>> orderCategoryMap = orderList.stream().collect(Collectors.groupingBy(EmcOrderBean::getOrderCategory, LinkedHashMap::new, Collectors.toList()));
        Map<String, LocalDate> minDateMap = new LinkedHashMap<>(orderCategoryMap.size());

        orderCategoryMap.forEach((orderCategory, curOrderList) -> {
            //按样品类型分组，当土壤的分组有空白时，样品类型土壤变成土壤+空白
            /*List<EmcOrderBean> blankOrderList = curOrderList.stream().filter(order -> StringUtils.equals("土壤",order.getOrderCategory()))
            		.filter(order -> StringUtils.equals("1",order.getExt$Item("isblank"))).collect(Collectors.toList());
            if(!blankOrderList.isEmpty()){
            	orderCategory = orderCategory+"、纯水";
            }*/
            if (sampling) {
                EmcOrderBean minDateOrder = curOrderList.stream().filter(order -> !ObjectUtils.isEmpty(order.getSampleBeginDate())).min(Comparator.comparing(EmcOrderBean::getSampleBeginDate)).orElseGet(EmcOrderBean::new);
                minDateMap.put(orderCategory, Optional.ofNullable(minDateOrder.getSampleBeginDate()).orElseGet(LocalDate::now));
                return;
            }

            EmcOrderBean minDateOrder = curOrderList.stream().filter(order -> !ObjectUtils.isEmpty(order.getSendDate())).min(Comparator.comparing(EmcOrderBean::getSendDate)).orElseGet(EmcOrderBean::new);
            minDateMap.put(orderCategory, Optional.ofNullable(minDateOrder.getSendDate()).orElseGet(LocalDate::now));
        });

        if (asc) {
            return minDateMap.entrySet().stream().sorted(Comparator.comparing(entry -> entry.getValue().toString() + entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, newVal) -> oldVal, LinkedHashMap::new));
        }

        return minDateMap.entrySet().stream().sorted(Collections.reverseOrder(Comparator.comparing(entry -> entry.getValue().toString() + entry.getKey()))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, newVal) -> oldVal, LinkedHashMap::new));
    }

    protected String joinTestName(List<EmcOrderTaskBean> validOrderTaskList) {
        LinkedHashMap<String, List<EmcOrderTaskBean>> curTaskMap = validOrderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getTestName, LinkedHashMap::new, Collectors.toList()));
        List<String> replacedTestNameList = new ArrayList<>();
        curTaskMap.forEach((testName, taskList) -> {
            List<String> itemGroupList = taskList.stream().filter(task -> !StringUtils.isEmpty(task.getExt$Item("itemGroup"))).map(task -> task.getExt$Item("itemGroup")).distinct().collect(Collectors.toList());
            boolean showTestName = itemGroupList.size() != 1;
            String showName = showTestName ? testName : itemGroupList.get(0);
            String replacedName = StringUtils.replace(showName, PfConstant.SPECIAL_TEST_NAME, PfConstant.SPECIAL_REPLACED_TEST_NAME);
            replacedTestNameList.add(replacedName);
        });

        return String.join("、", replacedTestNameList);
    }

    /**
     * 检测目的
     * 1、不体现委托单位的：对项目名称的样品类型进行检测
     * 2、不体现项目名称的：受委托单位对受检单位的样品类型进行检测
     * 3、不体现受检单位的：受委托单位对项目名称的样品类型进行检测
     * 4、不体现委托单位、受检单位的（不允许出现）
     * 5、不体现委托单位、项目名称的：对受检单位的样品类型进行检测
     * 6、不体现委托单位、项目名称、受检单位的（不允许出现）
     *     private String authorizationUnitShow; //委托单位报告显示
     *     private String inspectedUnitShow; //受检单位报告显示
     *     private String projectNameShow; //项目名称报告显示
     * @param project
     * @param opportunityMain
     * @param orderCategory
     * @return
     */
    protected String lookupDetectPurpose(EmcProjectBean project, CrmOpportunityMainBean opportunityMain, String orderCategory) {
        if (HIDDEN_VALUE.equals(opportunityMain.getAuthorizationUnitShow())
                && HIDDEN_VALUE.equals(opportunityMain.getProjectNameShow())
                && HIDDEN_VALUE.equals(opportunityMain.getInspectedUnitShow())) {
            return String.format(PfConstant.DETECT_PURPOSE_FORMATE_3, orderCategory);
        }

        if (HIDDEN_VALUE.equals(opportunityMain.getAuthorizationUnitShow())
                && HIDDEN_VALUE.equals(opportunityMain.getInspectedUnitShow())) {
            return String.format(PfConstant.DETECT_PURPOSE_FORMATE_3, orderCategory);
        }

        if (HIDDEN_VALUE.equals(opportunityMain.getAuthorizationUnitShow())
                && HIDDEN_VALUE.equals(opportunityMain.getProjectNameShow())) {
            return String.format(PfConstant.DETECT_PURPOSE_FORMATE_1, project.getTestOrgName(), orderCategory);
        }

        if (HIDDEN_VALUE.equals(opportunityMain.getAuthorizationUnitShow())) {
            return String.format(PfConstant.DETECT_PURPOSE_FORMATE_1, project.getProjName(), orderCategory);
        }

        if (HIDDEN_VALUE.equals(opportunityMain.getProjectNameShow())) {
            return String.format(PfConstant.DETECT_PURPOSE_FORMATE_2, project.getEntrustOrgName(), project.getTestOrgName(), orderCategory);
        }

        if (HIDDEN_VALUE.equals(opportunityMain.getInspectedUnitShow())) {
            return String.format(PfConstant.DETECT_PURPOSE_FORMATE_2, project.getEntrustOrgName(), project.getProjName(), orderCategory);
        }

        return String.format(PfConstant.DETECT_PURPOSE_FORMATE_2, project.getEntrustOrgName(), project.getTestOrgName(), orderCategory);
    }


    protected void insertQrCode() {
        List<CoreFileBean> qrCodeFileList = coreFileList.stream().filter(file -> "qrCode".equals(file.getBizCategory())).collect(Collectors.toList());
        if (qrCodeFileList.isEmpty()) {
            PfWord2007Utils.removeKey(document, "qrCode");
            return;
        }

        CoreFileBean qrCodeFile = qrCodeFileList.get(0);
        String imageUrl = FilePathManager.getLocalPath(CoreFileUtils.toFilePath(qrCodeFile)).toString();
        File imageFile = new File(imageUrl);
        if (!imageFile.exists()) {
            PfWord2007Utils.removeKey(document, "qrCode");
            return;
        }

        PfWord2007Utils.insertImageOfKey("qrCode", document, imageUrl, 60, 60);
    }
}
