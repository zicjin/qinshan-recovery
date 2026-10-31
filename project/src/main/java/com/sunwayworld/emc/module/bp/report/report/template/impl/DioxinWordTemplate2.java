package com.sunwayworld.emc.module.bp.report.report.template.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateBasicDTO;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateSourceDTO;
import com.sunwayworld.emc.module.bp.report.report.constant.EmcReportConstant;
import com.sunwayworld.emc.module.bp.report.report.template.BaseWordTemplate;
import com.sunwayworld.emc.module.bp.report.report.template.table.DioxinEnvGasDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.DioxinGasDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.DioxinOriginalRecordTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.DioxinRecoveryRateTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.DioxinSoilDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.DioxinWaterDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.dioxin.DioxinGasParameterTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.dioxin.DioxinGasResultTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.dioxin.DioxinMonitorContentTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.dioxin.DioxinMonitorItemTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.dioxin.DioxinOtherResultTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.dioxin.DioxinResultTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.dioxin.DioxinScenePhotoTable;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.result.temphum.bean.PfRunTempHumidityBean;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;

/**
 * 二噁英报告(水、气、土、固废)
 * @author zhengxy@sunwayworld.com 2021年07月08日
 */
public class DioxinWordTemplate2 extends BaseWordTemplate {

    private XWPFDocument document;
    private WordTemplateSourceDTO templateSourceDTO;
    private boolean sampling;

    public DioxinWordTemplate2(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO) {
        super(document, templateSourceDTO);
        this.document = document;
        this.templateSourceDTO = templateSourceDTO;
        this.sampling = Constant.YES.equals(templateSourceDTO.getProject().getSample());
    }

    @Override
    public WordTemplateBasicDTO buildBasicInformation() {
        WordTemplateBasicDTO wordTemplateBasicDTO = super.buildBasicInformation();

        // 根据监测模板ID查询监测模板，通过监测模板组ID来区分水、气、土、声
        List<EmcMonitorTmplBean> monitorTmplList = templateSourceDTO.getMonitorTmplList();
        // 根据监测模板组ID对监测模板进行分组
        Map<Long, List<EmcMonitorTmplBean>> monitorTmplMap = monitorTmplList.stream().collect(Collectors.groupingBy(EmcMonitorTmplBean::getMonitorTmplGroupId));

        AtomicInteger totalTableIndex = new AtomicInteger(monitorTmplList.size());

        // 水对应的监测模板
        List<EmcMonitorTmplBean> waterMonitorTmplList = Optional.ofNullable(monitorTmplMap.get(PfConstant.WATER_MONITOR_TMPL_GROUP_ID)).orElseGet(ArrayList::new);

        // 气对应的监测模板
        List<EmcMonitorTmplBean> gasMonitorTmplList = Optional.ofNullable(monitorTmplMap.get(PfConstant.GAS_MONITOR_TMPL_GROUP_ID)).orElseGet(ArrayList::new);
        List<EmcMonitorTmplBean> organizedGasMonitorTmplList = Optional.ofNullable(monitorTmplMap.get(PfConstant.DIOXIN_ORGANIZED_GAS_MONITOR_TMPL_GROUP_ID)).orElseGet(ArrayList::new);
        List<EmcMonitorTmplBean> unorganizedGasMonitorTmplList = Optional.ofNullable(monitorTmplMap.get(PfConstant.DIOXIN_UNORGANIZED_GAS_MONITOR_TMPL_GROUP_ID)).orElseGet(ArrayList::new);
        gasMonitorTmplList.addAll(organizedGasMonitorTmplList);
        gasMonitorTmplList.addAll(unorganizedGasMonitorTmplList);

        // 土对应的监测模板
        List<EmcMonitorTmplBean> soilMonitorTmplList = Optional.ofNullable(monitorTmplMap.get(PfConstant.SOIL_MONITOR_TMPL_GROUP_ID)).orElseGet(ArrayList::new);
        List<EmcMonitorTmplBean> solidWasteMonitorTmplList = Optional.ofNullable(monitorTmplMap.get(PfConstant.DIOXIN_SOLID_WASTE_MONITOR_TMPL_GROUP_ID)).orElseGet(ArrayList::new);
        List<EmcMonitorTmplBean> fodderMonitorTmplList = Optional.ofNullable(monitorTmplMap.get(PfConstant.DIOXIN_FODDER_MONITOR_TMPL_GROUP_ID)).orElseGet(ArrayList::new);
        soilMonitorTmplList.addAll(solidWasteMonitorTmplList);
        soilMonitorTmplList.addAll(fodderMonitorTmplList);

        List<String> resultList = new ArrayList<>();
        List<String> orderCategoryList = new ArrayList<>();

        Map<String, LocalDate> minDateMap = sortResult(waterMonitorTmplList, gasMonitorTmplList, soilMonitorTmplList, templateSourceDTO.getOrderList());
        minDateMap.forEach((key, minDate) -> {
            if (PfConstant.WATER_KEYWORD.equals(key)) {
                List<String> monitorTmplNameList = waterMonitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());
                List<EmcOrderBean> curOrderList = templateSourceDTO.getOrderList().stream().filter(order -> monitorTmplNameList.contains(order.getOrderCategory())).collect(Collectors.toList());
                Map<String, LocalDate> curMinDateMap = sortByDate(curOrderList, false);
                curMinDateMap.forEach((oc, date) -> {
                    resultList.add(WordTemplateUtils.handleOrderCategory(oc) + PfConstant.DEFAULT_RESULT + String.format(PfConstant.DEFAULT_TABLE_MESSAGE, totalTableIndex.getAndDecrement()));
                    orderCategoryList.add(WordTemplateUtils.handleOrderCategory(oc));
                });
            } else if (PfConstant.GAS_KEYWORD.equals(key)) {
                List<String> monitorTmplNameList = gasMonitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());
                List<EmcOrderBean> curOrderList = templateSourceDTO.getOrderList().stream().filter(order -> monitorTmplNameList.contains(order.getOrderCategory())).collect(Collectors.toList());
                Map<String, LocalDate> curMinDateMap = sortByDate(curOrderList, false);
                curMinDateMap.forEach((oc, date) -> {
                    resultList.add(WordTemplateUtils.handleOrderCategory(oc) + PfConstant.DEFAULT_RESULT + String.format(PfConstant.DEFAULT_TABLE_MESSAGE, totalTableIndex.getAndDecrement()));
                    orderCategoryList.add(WordTemplateUtils.handleOrderCategory(oc));
                });
            } else if (PfConstant.SOIL_KEYWORD.equals(key)) {
                List<String> monitorTmplNameList = soilMonitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());
                List<EmcOrderBean> curOrderList = templateSourceDTO.getOrderList().stream().filter(order -> monitorTmplNameList.contains(order.getOrderCategory())).collect(Collectors.toList());
                Map<String, LocalDate> curMinDateMap = sortByDate(curOrderList, false);
                curMinDateMap.forEach((oc, date) -> {
                    resultList.add(WordTemplateUtils.handleOrderCategory(oc) + PfConstant.DEFAULT_RESULT + String.format(PfConstant.DEFAULT_TABLE_MESSAGE, totalTableIndex.getAndDecrement()));
                    orderCategoryList.add(WordTemplateUtils.handleOrderCategory(oc));
                });
            }
        });

        Collections.reverse(resultList);
        String resultDesc = String.join("、", resultList);
        wordTemplateBasicDTO.setTestResult(resultDesc);

        Collections.reverse(orderCategoryList);
        wordTemplateBasicDTO.setSimpleOrderCategory(String.join("、", orderCategoryList));

        return wordTemplateBasicDTO;
    }

    @Override
    protected String getAnalysisDate() {
    	List<EmcFolderBean> folderList = templateSourceDTO.getFolderList();
    	List<String> orderCategoryList = folderList.stream().filter(f ->!StringUtils.isEmpty(f.getOrderCategory())).map(EmcFolderBean ::getOrderCategory).distinct().collect(Collectors.toList());
    	List<EmcSampleBean> sampleList = templateSourceDTO.getSampleList().stream().filter(s ->orderCategoryList.contains(s.getOrderCategory())).distinct().collect(Collectors.toList());
        List<LocalDate> acceptedDateList = sampleList.stream().filter(sample -> !ObjectUtils.isEmpty(sample.getAcceptedDate())).map(EmcSampleBean::getAcceptedDate).distinct().map(LocalDateTime::toLocalDate).collect(Collectors.toList());
        List<LocalDate> sampleBeginDateList = templateSourceDTO.getOrderList().stream().filter(order -> !ObjectUtils.isEmpty(order.getSampleBeginDate())).map(EmcOrderBean::getSampleBeginDate).distinct().collect(Collectors.toList());
        List<LocalDate> startDateList = acceptedDateList.isEmpty() ? sampleBeginDateList : acceptedDateList;
        List<LocalDate> endDateList = templateSourceDTO.getRunList().stream().filter(run -> !ObjectUtils.isEmpty(run.getAnalysisEndDate())).map(EmcRunBean::getAnalysisEndDate).distinct().collect(Collectors.toList());

        List<PfRunTempHumidityBean> tempHumidityList = templateSourceDTO.getTempHumidityList();
        List<LocalDate> humidityEndDateList = tempHumidityList.stream().filter(h -> !ObjectUtils.isEmpty(h.getEndDate())).map(PfRunTempHumidityBean::getEndDate).distinct().collect(Collectors.toList());
        List<LocalDate> oneHumidityEndDateList = tempHumidityList.stream().filter(h -> !ObjectUtils.isEmpty(h.getEndDateOne())).map(PfRunTempHumidityBean::getEndDateOne).distinct().collect(Collectors.toList());
        List<LocalDate> twoHumidityEndDateList = tempHumidityList.stream().filter(h -> !ObjectUtils.isEmpty(h.getEndDateTwo())).map(PfRunTempHumidityBean::getEndDateTwo).distinct().collect(Collectors.toList());
        endDateList.addAll(humidityEndDateList);
        endDateList.addAll(oneHumidityEndDateList);
        endDateList.addAll(twoHumidityEndDateList);

        return WordTemplateUtils.getDate(startDateList, endDateList);
    }

    @Override
    public void createResultTable() {
        // 获取点位对应的监测模板
        List<String> monitorTmplIdList = templateSourceDTO.getFolderList().stream().filter(f -> !StringUtils.isEmpty(f.getMonitorTmplId())).map(EmcFolderBean::getMonitorTmplId).distinct().collect(Collectors.toList());
        if (monitorTmplIdList.isEmpty()) {
            return;
        }

        // 根据监测模板ID查询监测模板，通过监测模板组ID来区分水、气、土、声
        List<EmcMonitorTmplBean> monitorTmplList = templateSourceDTO.getMonitorTmplList();
        // 根据监测模板组ID对监测模板进行分组
        Map<Long, List<EmcMonitorTmplBean>> monitorTmplMap = monitorTmplList.stream().collect(Collectors.groupingBy(EmcMonitorTmplBean::getMonitorTmplGroupId));

        AtomicInteger totalTableIndex = new AtomicInteger(monitorTmplList.size());
        int totalTableCount = totalTableIndex.get();

        // 水对应的监测模板
        List<EmcMonitorTmplBean> waterMonitorTmplList = Optional.ofNullable(monitorTmplMap.get(PfConstant.WATER_MONITOR_TMPL_GROUP_ID)).orElseGet(ArrayList::new);

        // 气对应的监测模板
        List<EmcMonitorTmplBean> gasMonitorTmplList = Optional.ofNullable(monitorTmplMap.get(PfConstant.GAS_MONITOR_TMPL_GROUP_ID)).orElseGet(ArrayList::new);
        List<EmcMonitorTmplBean> organizedGasMonitorTmplList = Optional.ofNullable(monitorTmplMap.get(PfConstant.DIOXIN_ORGANIZED_GAS_MONITOR_TMPL_GROUP_ID)).orElseGet(ArrayList::new);
        List<EmcMonitorTmplBean> unorganizedGasMonitorTmplList = Optional.ofNullable(monitorTmplMap.get(PfConstant.DIOXIN_UNORGANIZED_GAS_MONITOR_TMPL_GROUP_ID)).orElseGet(ArrayList::new);
        gasMonitorTmplList.addAll(organizedGasMonitorTmplList);
        gasMonitorTmplList.addAll(unorganizedGasMonitorTmplList);

        // 土对应的监测模板
        List<EmcMonitorTmplBean> soilMonitorTmplList = Optional.ofNullable(monitorTmplMap.get(PfConstant.SOIL_MONITOR_TMPL_GROUP_ID)).orElseGet(ArrayList::new);
        List<EmcMonitorTmplBean> solidWasteMonitorTmplList = Optional.ofNullable(monitorTmplMap.get(PfConstant.DIOXIN_SOLID_WASTE_MONITOR_TMPL_GROUP_ID)).orElseGet(ArrayList::new);
        List<EmcMonitorTmplBean> fodderMonitorTmplList = Optional.ofNullable(monitorTmplMap.get(PfConstant.DIOXIN_FODDER_MONITOR_TMPL_GROUP_ID)).orElseGet(ArrayList::new);
        List<EmcMonitorTmplBean> sedimentMonitorTmplList = Optional.ofNullable(monitorTmplMap.get(PfConstant.DIOXIN_SEDIMENT_MONITOR_TMPL_GROUP_ID)).orElseGet(ArrayList::new);
        soilMonitorTmplList.addAll(solidWasteMonitorTmplList);
        soilMonitorTmplList.addAll(fodderMonitorTmplList);
        soilMonitorTmplList.addAll(sedimentMonitorTmplList);

        Map<String, LocalDate> minDateMap = sortResult(waterMonitorTmplList, gasMonitorTmplList, soilMonitorTmplList, templateSourceDTO.getOrderList());

        // 创建回收率统计结果表
        //createRecoveryRateTable(waterMonitorTmplList, gasMonitorTmplList, soilMonitorTmplList, minDateMap);
        // 创建原始记录结果表
        //createOriginalRecordTable(waterMonitorTmplList, gasMonitorTmplList, soilMonitorTmplList, minDateMap);
        // 创建结果表
        //createResultTable(waterMonitorTmplList, gasMonitorTmplList, soilMonitorTmplList, totalTableIndex, totalTableCount, minDateMap);

        AtomicInteger reportTableIndex = new AtomicInteger(0);

        // 烟气参数
        List<EmcFolderBean> gasFolderList = templateSourceDTO.getFolderList().stream().filter(folder -> StringUtils.equals(folder.getOrderCategory(), EmcReportConstant.DIOXIN_GAS_ORDER_CATEGORY)).collect(Collectors.toList());
        gasFolderList.forEach(folder -> {
            List<EmcReportResultBean> curResultList = templateSourceDTO.getReportResultList().stream().filter(r -> ObjectUtils.equals(folder.getId(), r.getFolderId())).collect(Collectors.toList());
            List<EmcOrderBean> curOrderList = templateSourceDTO.getOrderList().stream().filter(order -> ObjectUtils.equals(order.getFolderId(), folder.getId())).collect(Collectors.toList());

            int curTableIndex = reportTableIndex.getAndIncrement();
            DioxinGasParameterTable parameterTable = new DioxinGasParameterTable(getDocument(), folder, curOrderList, curResultList, (curTableIndex == 0));
            parameterTable.create();
        });

        // 附表-检测结果
        List<EmcOrderBean> orderList = templateSourceDTO.getOrderList();
        List<EmcFolderBean> sortedFolderList = templateSourceDTO.getFolderList().stream().sorted(Comparator.comparing(EmcFolderBean::getId).reversed()).collect(Collectors.toList());
        Map<String, List<EmcFolderBean>> orderCategoryFolderMap = sortedFolderList.stream().collect(Collectors.groupingBy(EmcFolderBean::getOrderCategory, LinkedHashMap::new, Collectors.toList()));
        AtomicInteger orderCategoryTableIndex = new AtomicInteger(0);
        orderCategoryFolderMap.forEach((orderCategory, curFolderList) -> {
            curFolderList.sort(Comparator.comparing(EmcFolderBean::getId).reversed());
            int curOrderCategoryTableIndex = orderCategoryTableIndex.incrementAndGet();
            String orderCategoryTableTitle = String.format(EmcReportConstant.DIOXIN_ATTACHED_TABLE_TITLE, curOrderCategoryTableIndex);

            List<Long> curFolderIdList = curFolderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
            List<EmcOrderBean> curOrderList = orderList.stream().filter(order -> curFolderIdList.contains(order.getFolderId())).sorted(Comparator.comparing(EmcOrderBean::getOrderNo).reversed()).collect(Collectors.toList());

            AtomicInteger curTotalTableIndex = new AtomicInteger(curOrderList.size());
            curFolderList.forEach(folder -> {
                List<EmcOrderBean> curFolderOrderList = curOrderList.stream().filter(order -> ObjectUtils.equals(folder.getId(), order.getFolderId())).collect(Collectors.toList());
                curFolderOrderList.forEach(order -> {
                    List<EmcReportResultBean> curResultList = templateSourceDTO.getReportResultList().stream().filter(r -> ObjectUtils.equals(order.getId(), r.getOrderId())).collect(Collectors.toList());
                    int curTableIndex = reportTableIndex.getAndIncrement();
                    String tableTitle = (curTotalTableIndex.getAndDecrement() == 1) ? orderCategoryTableTitle : EmcReportConstant.DIOXIN_ATTACHED_TABLE_TITLE_2;

                    if (EmcReportConstant.DIOXIN_GAS_ORDER_CATEGORY.equals(folder.getOrderCategory())) {
                        DioxinGasResultTable resultTable = new DioxinGasResultTable(getDocument(), folder, order, curResultList, (curTableIndex == 0), tableTitle);
                        resultTable.create();
                    } else {
                        List<EmcOrderTaskResultBean> curTaskResultList = templateSourceDTO.getOrderTaskResultList().stream().filter(result -> ObjectUtils.equals(order.getId(), result.getOrderId())).collect(Collectors.toList());
                        DioxinOtherResultTable resultTable = new DioxinOtherResultTable(getDocument(), templateSourceDTO.getProject(), folder, order, curTaskResultList, (curTableIndex == 0), tableTitle);
                        resultTable.create();
                    }
                });
            });
        });

        // 现场照片
        List<CoreFileBean> photoList = templateSourceDTO.getCoreFileList().stream().filter(file -> StringUtils.equals(file.getBizCategory(), "photo") && (StringUtils.startsWith(file.getTargetId(), "T_EMC_SAMPLE") || StringUtils.startsWith(file.getTargetId(), "T_EMC_PROJECT"))).collect(Collectors.toList());
        if (!photoList.isEmpty()) {
            DioxinScenePhotoTable photoTable = new DioxinScenePhotoTable(getDocument(), templateSourceDTO.getProject(), templateSourceDTO.getFolderList(), templateSourceDTO.getOrderList(), templateSourceDTO.getCoreFileList());
            photoTable.create();
        }

        AtomicInteger resultTableIndex = new AtomicInteger(0);
        orderCategoryFolderMap.forEach((orderCategory, curFolderList) -> {
            int curResultTableIndex = resultTableIndex.incrementAndGet();
            String tableTitle = (curResultTableIndex == orderCategoryFolderMap.size()) ? EmcReportConstant.DIOXIN_RESULT_TABLE_TITLE : null;
            List<Long> curFolderIdList = curFolderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
            List<EmcOrderTaskBean> curTaskList = templateSourceDTO.getOrderTaskList().stream().filter(task -> curFolderIdList.contains(task.getFolderId())).collect(Collectors.toList());

            DioxinResultTable resultTable = new DioxinResultTable(getDocument(), templateSourceDTO.getProject(), curFolderList, templateSourceDTO.getOrderList(), curTaskList, templateSourceDTO.getReportResultList(), templateSourceDTO.getIndexCategoryList(), tableTitle, curResultTableIndex);
            resultTable.create();
        });

        // 创建检测项目表
        DioxinMonitorItemTable monitorItemDynamicTable = new DioxinMonitorItemTable(getDocument(), templateSourceDTO.getProject(), templateSourceDTO.getOrderTaskList(), templateSourceDTO.getReportResultList(), templateSourceDTO.getEquiptList(), templateSourceDTO.getRunList(), templateSourceDTO.getCoreCodeList(), monitorTmplList.size() + 1);
        monitorItemDynamicTable.create();

        // 创建检测项目表
        DioxinMonitorContentTable monitorContentTable = new DioxinMonitorContentTable(getDocument(), templateSourceDTO.getProject(), templateSourceDTO.getFolderList(), templateSourceDTO.getOrderList(), templateSourceDTO.getOrderTaskList());
        monitorContentTable.create();
    }

    private Map<String, LocalDate> sortResult(List<EmcMonitorTmplBean> waterMonitorTmplList, List<EmcMonitorTmplBean> gasMonitorTmplList, List<EmcMonitorTmplBean> soilMonitorTmplList, List<EmcOrderBean> orderList) {
        Map<String, LocalDate> sortedDateMap = sortByDate(orderList, false);
        List<String> waterTmplNameList = waterMonitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());
        List<String> gasTmplNameList = gasMonitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());
        List<String> soilTmplNameList = soilMonitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());

        Map<String, LocalDate> minDateMap = new LinkedHashMap<>(3);
        sortedDateMap.forEach((orderCategory, minDate) -> {
            if (waterTmplNameList.contains(orderCategory)) {
                LocalDate waterDate = Optional.ofNullable(minDateMap.get(PfConstant.WATER_KEYWORD)).orElse(minDate);
                minDateMap.put(PfConstant.WATER_KEYWORD, waterDate.isBefore(minDate) ? waterDate : minDate);
            }
            if (gasTmplNameList.contains(orderCategory)) {
                LocalDate gasDate = Optional.ofNullable(minDateMap.get(PfConstant.GAS_KEYWORD)).orElse(minDate);
                minDateMap.put(PfConstant.GAS_KEYWORD, gasDate.isBefore(minDate) ? gasDate : minDate);
            }
            if (soilTmplNameList.contains(orderCategory)) {
                LocalDate soilDate = Optional.ofNullable(minDateMap.get(PfConstant.SOIL_KEYWORD)).orElse(minDate);
                minDateMap.put(PfConstant.SOIL_KEYWORD, soilDate.isBefore(minDate) ? soilDate : minDate);
            }
        });

        return minDateMap;
    }

    private void createResultTable(List<EmcMonitorTmplBean> waterMonitorTmplList, List<EmcMonitorTmplBean> gasMonitorTmplList, List<EmcMonitorTmplBean> soilMonitorTmplList, AtomicInteger totalTableIndex, int totalTableCount, Map<String, LocalDate> minDateMap) {
        minDateMap.forEach((key, minDate) -> {
            if (PfConstant.WATER_KEYWORD.equals(key)) {
                createWaterResultTable(waterMonitorTmplList, totalTableIndex, totalTableCount);
            } else if (PfConstant.GAS_KEYWORD.equals(key)) {
                createGasResultTable(gasMonitorTmplList, totalTableIndex, totalTableCount);
            } else if (PfConstant.SOIL_KEYWORD.equals(key)) {
                createSoilResultTable(soilMonitorTmplList, totalTableIndex, totalTableCount);
            }
        });
    }

    /**
     * 创建水与废水检测结果表
     * @param waterMonitorTmplList 水与废水类监测模板列表
     * @param totalTableIndex 当前结果表索引
     * @param totalTableCount 结果表总数
     */
    private void createWaterResultTable(List<EmcMonitorTmplBean> waterMonitorTmplList, AtomicInteger totalTableIndex, int totalTableCount) {
        if (waterMonitorTmplList.isEmpty()) {
            return;
        }

        List<String> monitorTmplNameList = waterMonitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());
        List<EmcOrderBean> curOrderList = templateSourceDTO.getOrderList().stream().filter(order -> monitorTmplNameList.contains(order.getOrderCategory())).collect(Collectors.toList());
        Map<String, LocalDate> minDateMap = sortByDate(curOrderList, false);

        Map<String, List<EmcFolderBean>> folderListMap = groupingFolderByOrderCategory(waterMonitorTmplList, templateSourceDTO.getFolderList());

        minDateMap.forEach((orderCategory, minDate) -> {
            List<EmcFolderBean> folderList = Optional.ofNullable(folderListMap.get(orderCategory)).orElseGet(ArrayList::new);
            EmcReportBean currentReport = templateSourceDTO.getReport();

            int waterTableIndex = totalTableIndex.getAndDecrement();
            boolean last = waterTableIndex != totalTableCount;
            List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
            List<EmcOrderBean> folderOrderList = templateSourceDTO.getOrderList().stream().filter(o -> folderIdList.contains(o.getFolderId())).sorted(Comparator.comparing(EmcOrderBean::getId)).collect(Collectors.toList());
            List<EmcReportResultBean> folderReportResultList = templateSourceDTO.getReportResultList().stream().filter(r -> folderIdList.contains(r.getFolderId())).collect(Collectors.toList());
            DioxinWaterDynamicTable waterDynamicTable = new DioxinWaterDynamicTable(document, currentReport, folderOrderList, folderReportResultList, waterTableIndex, sampling, last);
            waterDynamicTable.create();
        });
    }

    /**
     * 创建空气与废气检测结果表
     * @param gasMonitorTmplList 空气与废气类监测模板列表
     * @param totalTableIndex 当前结果表索引
     * @param totalTableCount 结果表总数
     */
    private void createGasResultTable(List<EmcMonitorTmplBean> gasMonitorTmplList, AtomicInteger totalTableIndex, int totalTableCount) {
        if (gasMonitorTmplList.isEmpty()) {
            return;
        }

        List<String> monitorTmplNameList = gasMonitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());
        List<EmcOrderBean> curOrderList = templateSourceDTO.getOrderList().stream().filter(order -> monitorTmplNameList.contains(order.getOrderCategory())).collect(Collectors.toList());
        Map<String, LocalDate> minDateMap = sortByDate(curOrderList, false);

        Map<String, List<EmcFolderBean>> folderListMap = groupingFolderByOrderCategory(gasMonitorTmplList, templateSourceDTO.getFolderList());
        List<String> unorganizedGasTmplNameList = gasMonitorTmplList.stream().filter(t -> StringUtils.contains(t.getMonitorTmplName(), PfConstant.DIOXIN_ENV_GAS_KEYWORD)
                || StringUtils.contains(t.getMonitorTmplName(), PfConstant.DIOXIN_UNORGANIZED_GAS_KEYWORD)).map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());

        minDateMap.forEach((orderCategory, minDate) -> {
            List<EmcFolderBean> folderList = Optional.ofNullable(folderListMap.get(orderCategory)).orElseGet(ArrayList::new);
            if (unorganizedGasTmplNameList.contains(orderCategory)) {
                // 环境空气或无组织模板
                createUnorganizedGasResultTable(orderCategory, folderList, totalTableIndex, totalTableCount);
                return;
            }

            // 有组织废气
            createOrganizedGasResultTable(orderCategory, folderList, templateSourceDTO.getPropertyDataList(), totalTableIndex, totalTableCount);
        });
    }

    /**
     * 创建环境空气或无组织废气结果表
     * @param orderCategory 样品类型
     * @param folderList 点位列表
     * @param totalTableIndex 结果表索引
     * @param totalTableCount 结果表总数
     */
    private void createUnorganizedGasResultTable(String orderCategory, List<EmcFolderBean> folderList, AtomicInteger totalTableIndex, int totalTableCount) {
        if (folderList.isEmpty()) {
            return;
        }

        EmcReportBean currentReport = templateSourceDTO.getReport();

        int gasTableIndex = totalTableIndex.getAndDecrement();
        boolean last = gasTableIndex != totalTableCount;
        List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
        List<EmcOrderBean> folderOrderList = templateSourceDTO.getOrderList().stream().filter(o -> folderIdList.contains(o.getFolderId())).sorted(Comparator.comparing(EmcOrderBean::getId)).collect(Collectors.toList());
        List<EmcReportResultBean> folderReportResultList = templateSourceDTO.getReportResultList().stream().filter(r -> folderIdList.contains(r.getFolderId())).collect(Collectors.toList());

        DioxinEnvGasDynamicTable gasDynamicTable = new DioxinEnvGasDynamicTable(document, currentReport, folderOrderList, folderReportResultList, gasTableIndex, sampling, last);
        gasDynamicTable.create();
    }

    /**
     * 创建有组织废气结果表
     * @param orderCategory 样品类型
     * @param folderList 点位列表
     * @param propertyDataList 属性数据列表
     * @param totalTableIndex 结果表索引
     * @param totalTableCount 结果表总数
     */
    private void createOrganizedGasResultTable(String orderCategory, List<EmcFolderBean> folderList, List<EmcPropertyDataBean> propertyDataList, AtomicInteger totalTableIndex, int totalTableCount) {
        if (folderList.isEmpty()) {
            return;
        }

        EmcReportBean currentReport = templateSourceDTO.getReport();

        int gasTableIndex = totalTableIndex.getAndDecrement();
        boolean last = gasTableIndex != totalTableCount;
        List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
        List<EmcOrderBean> folderOrderList = templateSourceDTO.getOrderList().stream().filter(o -> folderIdList.contains(o.getFolderId())).sorted(Comparator.comparing(EmcOrderBean::getId)).collect(Collectors.toList());
        List<EmcReportResultBean> folderReportResultList = templateSourceDTO.getReportResultList().stream().filter(r -> folderIdList.contains(r.getFolderId())).collect(Collectors.toList());

        DioxinGasDynamicTable gasDynamicTable = new DioxinGasDynamicTable(document, currentReport, folderOrderList, folderReportResultList, propertyDataList, gasTableIndex, sampling, last);
        gasDynamicTable.create();
    }

    /**
     * 创建土壤检测结果表
     * @param soilMonitorTmplList 土壤类监测模板列表
     * @param totalTableIndex 当前结果表索引
     * @param totalTableCount 结果表总数
     */
    private void createSoilResultTable(List<EmcMonitorTmplBean> soilMonitorTmplList, AtomicInteger totalTableIndex, int totalTableCount) {
        if (soilMonitorTmplList.isEmpty()) {
            return;
        }

        List<String> monitorTmplNameList = soilMonitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());
        List<EmcOrderBean> curOrderList = templateSourceDTO.getOrderList().stream().filter(order -> monitorTmplNameList.contains(order.getOrderCategory())).collect(Collectors.toList());
        Map<String, LocalDate> minDateMap = sortByDate(curOrderList, false);

        Map<String, List<EmcFolderBean>> folderListMap = groupingFolderByOrderCategory(soilMonitorTmplList, templateSourceDTO.getFolderList());
        minDateMap.forEach((orderCategory, minDate) -> {
            List<EmcFolderBean> folderList = Optional.ofNullable(folderListMap.get(orderCategory)).orElseGet(ArrayList::new);

            EmcReportBean currentReport = templateSourceDTO.getReport();

            int soilTableIndex = totalTableIndex.getAndDecrement();
            boolean last = soilTableIndex != totalTableCount;
            List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
            List<EmcOrderBean> folderOrderList = templateSourceDTO.getOrderList().stream().filter(o -> folderIdList.contains(o.getFolderId())).sorted(Comparator.comparing(EmcOrderBean::getId)).collect(Collectors.toList());
            List<EmcReportResultBean> folderReportResultList = templateSourceDTO.getReportResultList().stream().filter(r -> folderIdList.contains(r.getFolderId())).collect(Collectors.toList());
            DioxinSoilDynamicTable soilDynamicTable = new DioxinSoilDynamicTable(document, currentReport, folderOrderList, folderReportResultList, soilTableIndex, sampling, last);
            soilDynamicTable.create();
        });
    }

    /**
     * 创建原始记录结果表
     */
    private void createOriginalRecordTable(List<EmcMonitorTmplBean> waterMonitorTmplList, List<EmcMonitorTmplBean> gasMonitorTmplList, List<EmcMonitorTmplBean> soilMonitorTmplList, Map<String, LocalDate> minDateMap) {
        List<EmcOrderBean> orderList = templateSourceDTO.getOrderList();

        minDateMap.forEach((key, minDate) -> {
            if (PfConstant.WATER_KEYWORD.equals(key)) {
                createOriginalRecordTable(waterMonitorTmplList, orderList);
            } else if (PfConstant.GAS_KEYWORD.equals(key)) {
                createOriginalRecordTable(gasMonitorTmplList, orderList);
            } else if (PfConstant.SOIL_KEYWORD.equals(key)) {
                createOriginalRecordTable(soilMonitorTmplList, orderList);
            }
        });
    }

    private void createOriginalRecordTable(List<EmcMonitorTmplBean> monitorTmplList, List<EmcOrderBean> orderList) {
        List<String> monitorTmplNameList = monitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());
        List<EmcOrderBean> validOrderList = orderList.stream().filter(order -> monitorTmplNameList.contains(order.getOrderCategory())).sorted(Comparator.comparing(EmcOrderBean::getOrderNo).reversed()).collect(Collectors.toList());

        Map<String, LocalDate> minDateMap = sortByDate(validOrderList, false);
        Map<String, List<EmcOrderBean>> orderCategoryMap = validOrderList.stream().collect(Collectors.groupingBy(EmcOrderBean::getOrderCategory, LinkedHashMap::new, Collectors.toList()));

        minDateMap.forEach((orderCategory, minDate) -> orderCategoryMap.get(orderCategory).forEach(order -> {
            EmcReportBean currentReport = templateSourceDTO.getReport();

            List<EmcReportResultBean> curResultList = templateSourceDTO.getReportResultList().stream().filter(r -> ObjectUtils.equals(r.getOrderId(), order.getId())).collect(Collectors.toList());
            DioxinOriginalRecordTable originalRecordTable = new DioxinOriginalRecordTable(document, currentReport, order, curResultList);
            originalRecordTable.create();
        }));
    }

    /**
     * 创建回收率结果表
     */
    private void createRecoveryRateTable(List<EmcMonitorTmplBean> waterMonitorTmplList, List<EmcMonitorTmplBean> gasMonitorTmplList, List<EmcMonitorTmplBean> soilMonitorTmplList, Map<String, LocalDate> minDateMap) {
        if (!Constant.YES.equals(templateSourceDTO.getReport().getExt$Item("displayRecRate"))) {
            return;
        }

        List<EmcOrderBean> orderList = templateSourceDTO.getOrderList();

        minDateMap.forEach((key, minDate) -> {
            if (PfConstant.WATER_KEYWORD.equals(key)) {
                createRecoveryRateTable(waterMonitorTmplList, orderList);
            } else if (PfConstant.GAS_KEYWORD.equals(key)) {
                createRecoveryRateTable(gasMonitorTmplList, orderList);
            } else if (PfConstant.SOIL_KEYWORD.equals(key)) {
                createRecoveryRateTable(soilMonitorTmplList, orderList);
            }
        });
    }

    private void createRecoveryRateTable(List<EmcMonitorTmplBean> monitorTmplList, List<EmcOrderBean> orderList) {
        List<String> monitorTmplNameList = monitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());
        List<EmcOrderBean> validOrderList = orderList.stream().filter(order -> monitorTmplNameList.contains(order.getOrderCategory())).sorted(Comparator.comparing(EmcOrderBean::getOrderNo).reversed()).collect(Collectors.toList());

        Map<String, LocalDate> minDateMap = sortByDate(validOrderList, false);

        Map<String, List<EmcOrderBean>> orderCategoryMap = validOrderList.stream().collect(Collectors.groupingBy(EmcOrderBean::getOrderCategory, LinkedHashMap::new, Collectors.toList()));

        minDateMap.forEach((orderCategory, minDate) -> {
            List<EmcOrderBean> curOrderList = orderCategoryMap.get(orderCategory);
            curOrderList.sort(Comparator.comparing(EmcOrderBean::getOrderNo).reversed());
            curOrderList.forEach(order -> {
                List<EmcReportResultBean> curResultList = templateSourceDTO.getReportResultList().stream().filter(r -> ObjectUtils.equals(r.getOrderId(), order.getId())).collect(Collectors.toList());
                DioxinRecoveryRateTable recoveryRateTable = new DioxinRecoveryRateTable(document, order, curResultList);
                recoveryRateTable.create();
            });
        });
    }
}
