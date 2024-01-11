package com.sunwayworld.emc.module.bp.report.report.template.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateBasicDTO;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateSourceDTO;
import com.sunwayworld.emc.module.bp.report.report.template.BaseWordTemplate;
import com.sunwayworld.emc.module.bp.report.report.template.table.DioxinEnvGasDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.DioxinGasDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.DioxinQcBlankTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.DioxinQcDupPassRateTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.DioxinQcDupResultTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.DioxinQcMonitorItemTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.DioxinQcOriginalRecordTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.DioxinQcSpkcTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.DioxinQcStdResultTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.DioxinSoilDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.DioxinWaterDynamicTable;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.result.temphum.bean.PfRunTempHumidityBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityMainBean;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 二噁英质控报告(水、气、土、固废)
 * @author zhengxy@sunwayworld.com 2022年01月18日
 */
public class DioxinQcWordTemplate extends BaseWordTemplate {

    private XWPFDocument document;
    private WordTemplateSourceDTO templateSourceDTO;
    private boolean sampling;

    public DioxinQcWordTemplate(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO) {
        super(document, templateSourceDTO);
        this.document = document;
        this.templateSourceDTO = templateSourceDTO;
        this.sampling = Constant.YES.equals(templateSourceDTO.getProject().getSample());
    }

    @Override
    public WordTemplateBasicDTO buildBasicInformation() {
        WordTemplateBasicDTO wordTemplateBasicDTO = super.buildBasicInformation();
        boolean sampling = Constant.YES.equals(templateSourceDTO.getProject().getSample());
        wordTemplateBasicDTO.setSamplingDesc(sampling ? PfConstant.QC_SAMPLING_DESC : "");
        wordTemplateBasicDTO.setStoreCondition(lookupStoreCondition());

        return wordTemplateBasicDTO;
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

        AtomicInteger totalTableIndex = new AtomicInteger(0);

        // 水对应的监测模板
        List<EmcMonitorTmplBean> waterMonitorTmplList = Optional.ofNullable(monitorTmplMap.get(PfConstant.WATER_MONITOR_TMPL_GROUP_ID)).orElseGet(ArrayList::new);

        // 气对应的监测模板
        List<EmcMonitorTmplBean> gasMonitorTmplList = Optional.ofNullable(monitorTmplMap.get(PfConstant.GAS_MONITOR_TMPL_GROUP_ID)).orElseGet(ArrayList::new);

        // 土对应的监测模板
        List<EmcMonitorTmplBean> soilMonitorTmplList = Optional.ofNullable(monitorTmplMap.get(PfConstant.SOIL_MONITOR_TMPL_GROUP_ID)).orElseGet(ArrayList::new);
        List<EmcMonitorTmplBean> solidWasteMonitorTmplList = Optional.ofNullable(monitorTmplMap.get(PfConstant.SOLID_WASTE_MONITOR_TMPL_GROUP_ID)).orElseGet(ArrayList::new);
        soilMonitorTmplList.addAll(solidWasteMonitorTmplList);

        Map<String, LocalDate> minDateMap = sortResult(waterMonitorTmplList, gasMonitorTmplList, soilMonitorTmplList, templateSourceDTO.getOrderList());

        // 创建原始记录结果表
        createOriginalRecordTable(waterMonitorTmplList, gasMonitorTmplList, soilMonitorTmplList, minDateMap);

        // 创建加标回收率结果表
        //createQcSpkcResultTable(waterMonitorTmplList, gasMonitorTmplList, soilMonitorTmplList, minDateMap, totalTableIndex);

        // 创建标准物质结果表
        createStdResultTable(waterMonitorTmplList, gasMonitorTmplList, soilMonitorTmplList, minDateMap, totalTableIndex);

        // 创建平行双样合格率记录表
        createQcDupPassRateTable(waterMonitorTmplList, gasMonitorTmplList, soilMonitorTmplList, minDateMap, totalTableIndex);

        // 创建平行双样分析结果记录表
        createQcDupResultTable(waterMonitorTmplList, gasMonitorTmplList, soilMonitorTmplList, minDateMap, totalTableIndex);

        // 创建实验室空白分析结果
        createQcBlankResultTable(waterMonitorTmplList, gasMonitorTmplList, soilMonitorTmplList, minDateMap, totalTableIndex);

        // 创建检测项目表
        DioxinQcMonitorItemTable monitorItemDynamicTable = new DioxinQcMonitorItemTable(getDocument(), templateSourceDTO.getProject(), templateSourceDTO.getReport(), templateSourceDTO.getOrderTaskList(), templateSourceDTO.getReportResultList(), templateSourceDTO.getEquiptList(), templateSourceDTO.getRunList(), templateSourceDTO.getCoreCodeList(), totalTableIndex.incrementAndGet());
        monitorItemDynamicTable.create();
    }

    private Map<String, LocalDate> sortResult(List<EmcMonitorTmplBean> waterMonitorTmplList, List<EmcMonitorTmplBean> gasMonitorTmplList, List<EmcMonitorTmplBean> soilMonitorTmplList, List<EmcOrderBean> orderList) {
        Map<String, LocalDate> sortedDateMap = sortByDate(orderList, false);
        List<String> waterTmplNameList = waterMonitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());
        List<String> gasTmplNameList = gasMonitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());
        List<String> soilTmplNameList = soilMonitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());

        Map<String, LocalDate> minDateMap = new LinkedHashMap<>(3);
        sortedDateMap.forEach((orderCategory, minDate) -> {
            if (waterTmplNameList.contains(orderCategory)) {
                minDateMap.putIfAbsent(PfConstant.WATER_KEYWORD, minDate);
            }
            if (gasTmplNameList.contains(orderCategory)) {
                minDateMap.putIfAbsent(PfConstant.GAS_KEYWORD, minDate);
            }
            if (soilTmplNameList.contains(orderCategory)) {
                minDateMap.putIfAbsent(PfConstant.SOIL_KEYWORD, minDate);
            }
        });

        return minDateMap;
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

        // 移除首页表格隐藏的信息行，委托单位、受检单位、项目名称分别对应首页表格的第1行、第2行和第3行，行下标为0、1、2
        removeTableRow(tables.get(0), Arrays.asList(opportunityMain.getAuthorizationUnitShow(), opportunityMain.getInspectedUnitShow(), opportunityMain.getProjectNameShow()), Arrays.asList(0, 1, 2), true);

        // 移除基本信息表格隐藏的信息行
/*        removeTableRow(tables.get(1), Arrays.asList(opportunityMain.getAuthorizationUnitShow(), opportunityMain.getEntrustededUnitAddShow(),
                opportunityMain.getInspectedUnitShow(), opportunityMain.getInspectedAddShow(), opportunityMain.getProjectNameShow(),
                opportunityMain.getDrawAreaShow()), Arrays.asList(0, 1, 2, 3, 4, 5), false);*/
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
            List<EmcFolderBean> folderList = folderListMap.get(orderCategory);
            EmcReportBean currentReport = templateSourceDTO.getReportList().stream().filter(report -> StringUtils.equals(report.getOrderCategory(), orderCategory)).findFirst().orElseGet(EmcReportBean::new);

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
            List<EmcFolderBean> folderList = folderListMap.get(orderCategory);
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
        EmcReportBean currentReport = templateSourceDTO.getReportList().stream().filter(report -> StringUtils.equals(report.getOrderCategory(), orderCategory)).findFirst().orElseGet(EmcReportBean::new);

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
        EmcReportBean currentReport = templateSourceDTO.getReportList().stream().filter(report -> StringUtils.equals(report.getOrderCategory(), orderCategory)).findFirst().orElseGet(EmcReportBean::new);

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
            List<EmcFolderBean> folderList = folderListMap.get(orderCategory);

            EmcReportBean currentReport = templateSourceDTO.getReportList().stream().filter(report -> StringUtils.equals(report.getOrderCategory(), orderCategory)).findFirst().orElseGet(EmcReportBean::new);

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
            DioxinQcOriginalRecordTable originalRecordTable = new DioxinQcOriginalRecordTable(document, currentReport, order, curResultList);
            originalRecordTable.create();
        }));
    }

    /**
     * 创建加标回收实验结果表
     */
    private void createQcSpkcResultTable(List<EmcMonitorTmplBean> waterMonitorTmplList, List<EmcMonitorTmplBean> gasMonitorTmplList, List<EmcMonitorTmplBean> soilMonitorTmplList, Map<String, LocalDate> minDateMap, AtomicInteger totalTableIndex) {
        List<EmcOrderBean> qcOrderList = templateSourceDTO.getQcOrderList();
        List<EmcOrderBean> orderBeforeList = templateSourceDTO.getOrderList();
        List<String> excludePdupSuffixList = Arrays.asList(PfConstant.QC_LDUP_ORDER_NO_SUFFIX);
        //正常样里去掉现场平行样
        List<EmcOrderBean> orderList = orderBeforeList.stream().filter(order -> excludePdupSuffixList.stream().noneMatch(suffix -> StringUtils.contains(order.getQcCategoryCode(), suffix))).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(qcOrderList) && CollectionUtils.isEmpty(orderList)) {
            return;
        }

        minDateMap.forEach((key, minDate) -> {
            if (PfConstant.WATER_KEYWORD.equals(key)) {
                createQcSpkcResultTable(waterMonitorTmplList, orderList, templateSourceDTO.getOrderTaskResultList(), totalTableIndex);
                //去掉实验室平行，标准样品，空白
                List<String> excludeQcSuffixList = Arrays.asList(PfConstant.QC_LBLANK_ORDER_NO_PREFIX, PfConstant.QC_STD_ORDER_NO_PREFIX, PfConstant.QC_LDUP_ORDER_NO_SUFFIX);
                List<EmcOrderBean> validQcOrderList = qcOrderList.stream().filter(qcOrder -> excludeQcSuffixList.stream().noneMatch(suffix -> StringUtils.contains(qcOrder.getOrderNo(), suffix))).collect(Collectors.toList());
                createQcSpkcResultTable(waterMonitorTmplList, validQcOrderList, templateSourceDTO.getQcTaskResultList(), totalTableIndex);
            } else if (PfConstant.GAS_KEYWORD.equals(key)) {
                //去掉实验室平行，标准样品，空白
                createQcSpkcResultTable(gasMonitorTmplList, orderList, templateSourceDTO.getOrderTaskResultList(), totalTableIndex);
                List<String> excludeQcSuffixList = Arrays.asList(PfConstant.QC_LBLANK_ORDER_NO_PREFIX, PfConstant.QC_STD_ORDER_NO_PREFIX, PfConstant.QC_LDUP_ORDER_NO_SUFFIX);
                List<EmcOrderBean> validQcOrderList = qcOrderList.stream().filter(qcOrder -> excludeQcSuffixList.stream().noneMatch(suffix -> StringUtils.contains(qcOrder.getOrderNo(), suffix))).collect(Collectors.toList());
                createQcSpkcResultTable(gasMonitorTmplList, validQcOrderList, templateSourceDTO.getQcTaskResultList(), totalTableIndex);
            } else if (PfConstant.SOIL_KEYWORD.equals(key)) {
                //去掉实验室平行，标准样品，空白
                createQcSpkcResultTable(soilMonitorTmplList, orderList, templateSourceDTO.getOrderTaskResultList(), totalTableIndex);
                List<String> excludeQcSuffixList = Arrays.asList(PfConstant.QC_LBLANK_ORDER_NO_PREFIX, PfConstant.QC_STD_ORDER_NO_PREFIX, PfConstant.QC_LDUP_ORDER_NO_SUFFIX);
                List<EmcOrderBean> validQcOrderList = qcOrderList.stream().filter(qcOrder -> excludeQcSuffixList.stream().noneMatch(suffix -> StringUtils.contains(qcOrder.getOrderNo(), suffix))).collect(Collectors.toList());
                createQcSpkcResultTable(soilMonitorTmplList, validQcOrderList, templateSourceDTO.getQcTaskResultList(), totalTableIndex);
            }
        });
    }

    private void createQcSpkcResultTable(List<EmcMonitorTmplBean> monitorTmplList, List<EmcOrderBean> orderList, List<EmcOrderTaskResultBean> orderTaskResultList, AtomicInteger totalTableIndex) {
        if (CollectionUtils.isEmpty(orderList)) {
            return;
        }

        List<String> monitorTmplNameList = monitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());
        List<EmcOrderBean> validOrderList = orderList.stream().filter(order -> monitorTmplNameList.contains(order.getOrderCategory())).sorted(Comparator.comparing(EmcOrderBean::getOrderNo).reversed()).collect(Collectors.toList());

        List<String> excludeQcSuffixList = Arrays.asList(PfConstant.QC_LBLANK_ORDER_NO_PREFIX, PfConstant.QC_STD_ORDER_NO_PREFIX, PfConstant.QC_LDUP_ORDER_NO_SUFFIX);
        List<EmcOrderBean> validQcOrderList = validOrderList.stream().filter(qcOrder -> excludeQcSuffixList.stream().noneMatch(suffix -> StringUtils.contains(qcOrder.getOrderNo(), suffix))).collect(Collectors.toList());

        Map<String, LocalDate> minDateMap = sortByDate(validQcOrderList, false);
        Map<String, List<EmcOrderBean>> orderCategoryMap = validQcOrderList.stream().collect(Collectors.groupingBy(EmcOrderBean::getOrderCategory, LinkedHashMap::new, Collectors.toList()));

        minDateMap.forEach((orderCategory, minDate) -> {
            List<EmcOrderBean> curOrderList = orderCategoryMap.get(orderCategory);
            if (CollectionUtils.isEmpty(curOrderList)) {
                return;
            }

            curOrderList.forEach(order -> {
                List<EmcOrderTaskResultBean> curResultList = orderTaskResultList.stream().filter(r -> ObjectUtils.equals(order.getId(), r.getOrderId())).collect(Collectors.toList());

                DioxinQcSpkcTable spkcTable = new DioxinQcSpkcTable(document,  order, curResultList, totalTableIndex.incrementAndGet());
                spkcTable.create();
            });
        });
    }

    /**
     * 创建有证标准物质检测结果记录表
     */
    private void createStdResultTable(List<EmcMonitorTmplBean> waterMonitorTmplList, List<EmcMonitorTmplBean> gasMonitorTmplList, List<EmcMonitorTmplBean> soilMonitorTmplList, Map<String, LocalDate> minDateMap, AtomicInteger totalTableIndex) {
        List<EmcOrderBean> qcOrderList = templateSourceDTO.getQcOrderList();
        if (qcOrderList == null || qcOrderList.isEmpty()) {
            return;
        }

        List<EmcOrderBean> stdOrderList = qcOrderList.stream().filter(order -> PfConstant.QCCATEGORY_QCCATEGORYID_QC_STD.equals(order.getQcCategoryCode())).collect(Collectors.toList());
        if (stdOrderList.isEmpty()) {
            return;
        }

        minDateMap.forEach((key, minDate) -> {
            if (PfConstant.WATER_KEYWORD.equals(key)) {
                createStdResultTable(waterMonitorTmplList, stdOrderList, totalTableIndex);
            } else if (PfConstant.GAS_KEYWORD.equals(key)) {
                createStdResultTable(gasMonitorTmplList, stdOrderList, totalTableIndex);
            } else if (PfConstant.SOIL_KEYWORD.equals(key)) {
                createStdResultTable(soilMonitorTmplList, stdOrderList, totalTableIndex);
            }
        });
    }

    private void createStdResultTable(List<EmcMonitorTmplBean> monitorTmplList, List<EmcOrderBean> orderList, AtomicInteger totalTableIndex) {
        List<String> monitorTmplNameList = monitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());
        List<EmcOrderBean> validOrderList = orderList.stream().filter(order -> monitorTmplNameList.contains(order.getOrderCategory())).sorted(Comparator.comparing(EmcOrderBean::getOrderNo).reversed()).collect(Collectors.toList());

        Map<String, LocalDate> minDateMap = sortByDate(validOrderList, false);
        Map<String, List<EmcOrderBean>> orderCategoryMap = validOrderList.stream().collect(Collectors.groupingBy(EmcOrderBean::getOrderCategory, LinkedHashMap::new, Collectors.toList()));

        minDateMap.forEach((orderCategory, minDate) -> {
            List<EmcOrderBean> curOrderList = orderCategoryMap.get(orderCategory);
            if (CollectionUtils.isEmpty(curOrderList)) {
                return;
            }

            List<Long> curOrderIdList = curOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
            List<EmcOrderTaskResultBean> curQcResultList = templateSourceDTO.getQcTaskResultList().stream().filter(r -> curOrderIdList.contains(r.getOrderId())).collect(Collectors.toList());

            DioxinQcStdResultTable stdResultTable = new DioxinQcStdResultTable(document, templateSourceDTO.getReport(), curOrderList, curQcResultList, totalTableIndex.incrementAndGet());
            stdResultTable.create();
        });
    }

    /**
     * 创建平行双样分析合格率记录表
     */
    private void createQcDupPassRateTable(List<EmcMonitorTmplBean> waterMonitorTmplList, List<EmcMonitorTmplBean> gasMonitorTmplList, List<EmcMonitorTmplBean> soilMonitorTmplList, Map<String, LocalDate> minDateMap, AtomicInteger totalTableIndex) {
        List<EmcOrderBean> qcOrderList = templateSourceDTO.getQcOrderList();
        if (qcOrderList == null || qcOrderList.isEmpty()) {
            return;
        }

        List<EmcOrderBean> dupOrderList = qcOrderList.stream().filter(order -> PfConstant.QC_L_DUP.equals(order.getQcCategoryCode())).collect(Collectors.toList());
        if (dupOrderList.isEmpty()) {
            return;
        }

        minDateMap.forEach((key, minDate) -> {
            if (PfConstant.WATER_KEYWORD.equals(key)) {
                createQcDupPassRateTable(waterMonitorTmplList, dupOrderList, totalTableIndex);
            } else if (PfConstant.GAS_KEYWORD.equals(key)) {
                createQcDupPassRateTable(gasMonitorTmplList, dupOrderList, totalTableIndex);
            } else if (PfConstant.SOIL_KEYWORD.equals(key)) {
                createQcDupPassRateTable(soilMonitorTmplList, dupOrderList, totalTableIndex);
            }
        });
    }

    private void createQcDupPassRateTable(List<EmcMonitorTmplBean> monitorTmplList, List<EmcOrderBean> orderList, AtomicInteger totalTableIndex) {
        List<String> monitorTmplNameList = monitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());
        List<EmcOrderBean> validOrderList = orderList.stream().filter(order -> monitorTmplNameList.contains(order.getOrderCategory())).sorted(Comparator.comparing(EmcOrderBean::getOrderNo).reversed()).collect(Collectors.toList());

        Map<String, LocalDate> minDateMap = sortByDate(validOrderList, false);
        Map<String, List<EmcOrderBean>> orderCategoryMap = validOrderList.stream().collect(Collectors.groupingBy(EmcOrderBean::getOrderCategory, LinkedHashMap::new, Collectors.toList()));

        minDateMap.forEach((orderCategory, minDate) -> {
            List<EmcOrderBean> curOrderList = orderCategoryMap.get(orderCategory);
            if (CollectionUtils.isEmpty(curOrderList)) {
                return;
            }

            List<Long> curOrderIdList = curOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
            List<EmcOrderTaskResultBean> curQcResultList = templateSourceDTO.getQcTaskResultList().stream().filter(r -> curOrderIdList.contains(r.getOrderId())).collect(Collectors.toList());

            List<String> runIdList = curQcResultList.stream().map(EmcOrderTaskResultBean::getRunId).distinct().collect(Collectors.toList());
            List<EmcRunBean> curRunList = templateSourceDTO.getRunList().stream().filter(run -> runIdList.contains(run.getId())).collect(Collectors.toList());

            DioxinQcDupPassRateTable passRateTable = new DioxinQcDupPassRateTable(document, curRunList, curOrderList, curQcResultList, totalTableIndex.incrementAndGet());
            passRateTable.create();
        });
    }

    /**
     * 创建平行双样分析结果记录表
     */
    private void createQcDupResultTable(List<EmcMonitorTmplBean> waterMonitorTmplList, List<EmcMonitorTmplBean> gasMonitorTmplList, List<EmcMonitorTmplBean> soilMonitorTmplList, Map<String, LocalDate> minDateMap, AtomicInteger totalTableIndex) {
        List<EmcOrderBean> qcOrderList = templateSourceDTO.getQcOrderList();
        if (qcOrderList == null || qcOrderList.isEmpty()) {
            return;
        }

        List<EmcOrderBean> dupOrderList = qcOrderList.stream().filter(order -> PfConstant.QC_L_DUP.equals(order.getQcCategoryCode())).collect(Collectors.toList());
        if (dupOrderList.isEmpty()) {
            return;
        }

        minDateMap.forEach((key, minDate) -> {
            if (PfConstant.WATER_KEYWORD.equals(key)) {
                createQcDupResultTable(waterMonitorTmplList, dupOrderList, totalTableIndex);
            } else if (PfConstant.GAS_KEYWORD.equals(key)) {
                createQcDupResultTable(gasMonitorTmplList, dupOrderList, totalTableIndex);
            } else if (PfConstant.SOIL_KEYWORD.equals(key)) {
                createQcDupResultTable(soilMonitorTmplList, dupOrderList, totalTableIndex);
            }
        });

    }

    private void createQcDupResultTable(List<EmcMonitorTmplBean> monitorTmplList, List<EmcOrderBean> orderList, AtomicInteger totalTableIndex) {
        List<String> monitorTmplNameList = monitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());
        List<EmcOrderBean> validOrderList = orderList.stream().filter(order -> monitorTmplNameList.contains(order.getOrderCategory())).sorted(Comparator.comparing(EmcOrderBean::getOrderNo).reversed()).collect(Collectors.toList());

        Map<String, LocalDate> minDateMap = sortByDate(validOrderList, false);
        Map<String, List<EmcOrderBean>> orderCategoryMap = validOrderList.stream().collect(Collectors.groupingBy(EmcOrderBean::getOrderCategory, LinkedHashMap::new, Collectors.toList()));

        minDateMap.forEach((orderCategory, minDate) -> {
            List<EmcOrderBean> curOrderList = orderCategoryMap.get(orderCategory);
            if (CollectionUtils.isEmpty(curOrderList)) {
                return;
            }

            List<Long> curOrderIdList = curOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
            List<EmcOrderTaskResultBean> curQcResultList = templateSourceDTO.getQcTaskResultList().stream().filter(r -> curOrderIdList.contains(r.getOrderId())).collect(Collectors.toList());

            List<String> parentOrderNoList = curOrderList.stream().filter(order -> !StringUtils.isEmpty(order.getParentOrderNo())).map(EmcOrderBean::getParentOrderNo).distinct().collect(Collectors.toList());
            List<Long> parentOrderIdList = templateSourceDTO.getOrderList().stream().filter(order -> parentOrderNoList.contains(order.getOrderNo())).map(EmcOrderBean::getId).collect(Collectors.toList());
            List<EmcOrderTaskResultBean> curParentResultList = templateSourceDTO.getOrderTaskResultList().stream().filter(r -> parentOrderIdList.contains(r.getOrderId())).collect(Collectors.toList());

            List<Long> curParentTaskIdList = curParentResultList.stream().map(EmcOrderTaskResultBean::getOrderTaskId).distinct().collect(Collectors.toList());
            List<PfRunTempHumidityBean> curTempHumidityList = templateSourceDTO.getTempHumidityList().stream().filter(t -> curParentTaskIdList.contains(t.getOrderTaskId())).collect(Collectors.toList());

            DioxinQcDupResultTable dupResultTable = new DioxinQcDupResultTable(document, curOrderList, curTempHumidityList, curQcResultList, curParentResultList, totalTableIndex.incrementAndGet());
            dupResultTable.create();
        });
    }

    /**
     * 创建实验室空白分析结果记录表
     */
    private void createQcBlankResultTable(List<EmcMonitorTmplBean> waterMonitorTmplList, List<EmcMonitorTmplBean> gasMonitorTmplList, List<EmcMonitorTmplBean> soilMonitorTmplList, Map<String, LocalDate> minDateMap, AtomicInteger totalTableIndex) {
        List<EmcOrderBean> qcOrderList = templateSourceDTO.getQcOrderList();
        if (qcOrderList == null || qcOrderList.isEmpty()) {
            return;
        }

        List<EmcOrderBean> blankOrderList = qcOrderList.stream().filter(order -> PfConstant.QCCATEGORY_QCCATEGORYID_L_BLANK.equals(order.getQcCategoryCode())).sorted(Comparator.comparing(EmcOrderBean::getOrderNo).reversed()).collect(Collectors.toList());
        if (blankOrderList.isEmpty()) {
            return;
        }

        minDateMap.forEach((key, minDate) -> {
            if (PfConstant.WATER_KEYWORD.equals(key)) {
                createQcBlankResultTable(waterMonitorTmplList, blankOrderList, totalTableIndex);
            } else if (PfConstant.GAS_KEYWORD.equals(key)) {
                createQcBlankResultTable(gasMonitorTmplList, blankOrderList, totalTableIndex);
            } else if (PfConstant.SOIL_KEYWORD.equals(key)) {
                createQcBlankResultTable(soilMonitorTmplList, blankOrderList, totalTableIndex);
            }
        });
    }

    private void createQcBlankResultTable(List<EmcMonitorTmplBean> monitorTmplList, List<EmcOrderBean> orderList, AtomicInteger totalTableIndex) {
        List<String> monitorTmplNameList = monitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());
        List<EmcOrderBean> validOrderList = orderList.stream().filter(order -> monitorTmplNameList.contains(order.getOrderCategory())).sorted(Comparator.comparing(EmcOrderBean::getOrderNo).reversed()).collect(Collectors.toList());

        Map<String, LocalDate> minDateMap = sortByDate(validOrderList, false);
        Map<String, List<EmcOrderBean>> orderCategoryMap = validOrderList.stream().collect(Collectors.groupingBy(EmcOrderBean::getOrderCategory, LinkedHashMap::new, Collectors.toList()));

        minDateMap.forEach((orderCategory, minDate) -> {
            List<EmcOrderBean> curOrderList = orderCategoryMap.get(orderCategory);
            if (CollectionUtils.isEmpty(curOrderList)) {
                return;
            }

            curOrderList.forEach(blankOrder -> {
                List<EmcOrderTaskResultBean> curResultList = templateSourceDTO.getQcTaskResultList().stream().filter(r -> ObjectUtils.equals(blankOrder.getId(), r.getOrderId())).collect(Collectors.toList());

                DioxinQcBlankTable blankTable = new DioxinQcBlankTable(document, templateSourceDTO.getReport(), blankOrder, curResultList, totalTableIndex.incrementAndGet());
                blankTable.create();
            });
        });
    }

    private String lookupStoreCondition() {
        List<String> storeConditionList = new ArrayList<>();
        List<EmcMonitorTmplBean> monitorTmplList = templateSourceDTO.getMonitorTmplList();
        List<String> monitorTmplNameList = monitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).distinct().collect(Collectors.toList());

        boolean hasGas = Arrays.stream(PfConstant.DIOXIN_GAS_KEYWORDS).anyMatch(keyword -> monitorTmplNameList.stream().anyMatch(tmplName -> StringUtils.contains(tmplName, keyword)));
        if (hasGas) {
            storeConditionList.add(PfConstant.DIOXIN_GAS_STORE_CONDITION);
        }

        boolean hasWater = Arrays.stream(PfConstant.DIOXIN_WATER_KEYWORDS).anyMatch(keyword -> monitorTmplNameList.stream().anyMatch(tmplName -> StringUtils.contains(tmplName, keyword)));
        if (hasWater) {
            storeConditionList.add(PfConstant.DIOXIN_WATER_STORE_CONDITION);
        }

        boolean hasSoil = Arrays.stream(PfConstant.DIOXIN_SOIL_KEYWORDS).anyMatch(keyword -> monitorTmplNameList.stream().anyMatch(tmplName -> StringUtils.contains(tmplName, keyword)));
        if (hasSoil) {
            storeConditionList.add(PfConstant.DIOXIN_SOIL_STORE_CONDITION);
        }

        boolean hasSolidWaste = Arrays.stream(PfConstant.DIOXIN_SOLID_WASTE_ORDER_CATEGORIES).anyMatch(keyword -> monitorTmplNameList.stream().anyMatch(tmplName -> StringUtils.contains(tmplName, keyword)));
        if (hasSolidWaste) {
            storeConditionList.add(PfConstant.DIOXIN_SOLID_WASTE_STORE_CONDITION);
        }

        return String.join("\n", storeConditionList);
    }
}
